/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.bean.fancy.RetenRegnNo_dobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Rereg_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.fancy.AdvanceRegnNo_dobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.InsImpl;
import nic.vahan.form.impl.NewImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Rereg_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import nic.vahan.services.insurance.InsuranceDetailService;
import nic.vahan.services.insurance.dobj.InsuranceInfoDobj;

@ManagedBean(name = "reReg")
@ViewScoped
public class Rereg_bean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Rereg_bean.class);
    private String appl_no;
    private String old_regn_no;
    private String new_regn_no;
    private String reason;
    private Date op_dt = new Date();
    private String state_cd;
    private int off_cd;
    ArrayList listReason = null;
    private Rereg_dobj prevReReg_dobj;
    private List<ComparisonBean> prevChangedDataList;
    private ArrayList<ComparisonBean> compBeanList = new ArrayList<ComparisonBean>();
    private String reasonSelect;
    @ManagedProperty(value = "#{ins_bean}")
    private InsBean ins_bean;
    private AdvanceRegnNo_dobj advRegnNoDobj = new AdvanceRegnNo_dobj();
    private boolean advRegnCheck = false;
    private boolean disableAdvRegnCheck = false;
    private boolean advRegnCheckDialogue = false;
    private List list_adv_district = new ArrayList();
    private List list_c_state = new ArrayList();
    private boolean disable = true;
    private RetenRegnNo_dobj retenRegNoDobj = new RetenRegnNo_dobj();
    private boolean retCheck = false;
    private boolean disableRetCheck = false;
    private boolean advRetCheckDialogue = false;
    private boolean retAllowed = false;
    private String vahanMessages = null;
    private boolean permitPanel;
    private int pmt_type = -1;
    private int pmt_catg = -1;
    private List permitTypeList = new ArrayList();
    private List PermitCategoryList = new ArrayList();
    private boolean isPmtTypeRqrd;
    private boolean isPmtCatgRqrd;
    private String seriesAvailMessage;
    private boolean approvalDialog = true;
    private boolean renderConfirmationCheck;
    private boolean oddEvenRender;
    private boolean oddEvenOpted;
    private boolean disableOddEvenChk;
    private OwnerDetailsDobj OwnerDetailsDobj = null;
    private Owner_dobj OwnerDobj = null;

    @PostConstruct
    void init() {

        listReason = new ArrayList();
        listReason.add(new SelectItem("-1", "Select"));
        listReason.add(new SelectItem("Govt Order/Notification", "Govt Order/Notification"));
        listReason.add(new SelectItem("Other State Vehicle", "Other State Vehicle"));
        listReason.add(new SelectItem("DTO ORDER", "DTO ORDER"));
        listReason.add(new SelectItem("Wrong Permit Details", "Wrong Permit Details"));
        listReason.add(new SelectItem("Other Case", "Other Case"));

        list_c_state = MasterTableFiller.getStateList();
        InsDobj ins_dobj_ret = new InsDobj();
        ins_dobj_ret = null;
        InsDobj ins_dobj_retVA = new InsDobj();
        ins_dobj_retVA = null;
        TmConfigurationDobj configDobj = null;
        OwnerImpl impl = new OwnerImpl();

        try {
            Rereg_Impl reRegImpl = new Rereg_Impl();
            Rereg_dobj reRegDobj = null;
            if (getAppl_details() != null) {
                reRegDobj = reRegImpl.set_dobj_from_db(getAppl_details().getAppl_no(), getAppl_details().getCurrent_role());
                if (appl_details.getOwnerDetailsDobj() == null) {
                    OwnerDetailsDobj = impl.getOwnerDetails(reRegDobj.getOld_regn_no(), appl_details.getCurrent_state_cd(), appl_details.getCurrent_off_cd());
                    OwnerDobj = impl.getOwnerDobj(OwnerDetailsDobj);
                    if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_REASIGN_APPROVAL) {
                        appl_details.setChasi_no(OwnerDobj.getChasi_no());
                        appl_details.setOwn_name(OwnerDobj.getOwner_name());
                    }
                    if (OwnerDetailsDobj == null) {
                        vahanMessages = "Owner Details not Found in the Database, Please Contact to the System Administrator";
                        return;
                    }
                } else {
                    OwnerDetailsDobj = appl_details.getOwnerDetailsDobj();
                    OwnerDobj = appl_details.getOwnerDobj();
                }
                configDobj = Util.getTmConfiguration();
                if ("SK,HR,DL".contains(appl_details.getCurrent_state_cd())) {
                    retAllowed = true;
                }
                if (configDobj != null && configDobj.isRandom_odd_even_reassign_allowed()) {
                    setOddEvenRender(true);
                }
                if (ServerUtil.isTransport(OwnerDetailsDobj.getVh_class(), null)) {
                    permitPanel = true;
                    String taxRqrdField[] = ServerUtil.getFieldsReqForTax(Util.getUserStateCode(), OwnerDetailsDobj.getVh_class());
                    if (taxRqrdField != null && taxRqrdField.length > 0) {
                        isPmtTypeRqrd = false;
                        isPmtCatgRqrd = false;
                        for (int i = 0; i < taxRqrdField.length; i++) {
                            if (taxRqrdField[i].equalsIgnoreCase(TableConstants.VM_PMT_TYPE_IN_TAX_CODE)) {
                                isPmtTypeRqrd = true;
                            }
                            if (taxRqrdField[i].equalsIgnoreCase(TableConstants.VM_PMT_CATG_IN_TAX_CODE)) {
                                isPmtTypeRqrd = true;
                                isPmtCatgRqrd = true;
                                break;
                            }
                        }
                    }

                    //############################Filteration of Permit Type Start###############
                    String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
                    String transportCatg = null;
                    for (int i = 0; i < data.length; i++) {
                        if (data[i][0].equalsIgnoreCase(String.valueOf(OwnerDetailsDobj.getVh_class()))) {
                            transportCatg = data[i][3];
                            break;
                        }
                    }

                    if (transportCatg != null) {
                        data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
                        getPermitTypeList().clear();
                        getPermitCategoryList().clear();
                        for (int i = 0; i < data.length; i++) {
                            if (data[i][2].equalsIgnoreCase(transportCatg)) {
                                getPermitTypeList().add(new SelectItem(data[i][0], data[i][1]));
                            }
                        }
                    }
                    permitTypeChangeListener();
                } else {
                    permitPanel = false;
                }

                if (reRegDobj != null) {
                    prevReReg_dobj = (Rereg_dobj) reRegDobj.clone();
                    //If Data only Saved at Entry Role(Disabled the selection of Advance Reg & Ret No Check).
                    if (reRegDobj.getNew_regn_no() != null && !reRegDobj.getNew_regn_no().isEmpty()) {
                        setDisableAdvRegnCheck(true);
                        setDisableRetCheck(true);
                    }
                    if (reRegDobj.getPmt_type() != 0) {
                        setPmt_type(reRegDobj.getPmt_type());
                        permitTypeChangeListener();
                    }
                    if (reRegDobj.isOddEvenOpted()) {
                        setDisableOddEvenChk(true);
                    }
                    OwnerDobj.setPmt_type(reRegDobj.getPmt_type());
                    OwnerDobj.setPmt_catg(reRegDobj.getPmt_catg());
                } else {
                    reRegDobj = new Rereg_dobj();
                    reRegDobj.setAppl_no(getAppl_details().getAppl_no());
                    reRegDobj.setOld_regn_no(getAppl_details().getRegn_no());
                    reRegDobj.setState_cd(Util.getUserStateCode());
                    reRegDobj.setOff_cd(Util.getSelectedSeat().getOff_cd());
                }

                setBeanFromDobj(reRegDobj);
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                ins_bean.setAppl_no(appl_details.getAppl_no());
                ins_bean.setRegn_no(OwnerDetailsDobj.getRegn_no());
                ins_bean.setPur_cd(appl_details.getPur_cd());
                ins_dobj_ret = InsImpl.set_ins_dtls_db_to_dobj(OwnerDetailsDobj.getRegn_no(), null, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                ins_dobj_retVA = InsImpl.set_ins_dtls_db_to_dobjVA(appl_details.getRegn_no());
                //start of getting insurance details from service
                InsuranceDetailService detailService = new InsuranceDetailService();
                InsDobj insDobj = detailService.getInsuranceDetailsByService(getAppl_details().getRegn_no(), getAppl_details().getCurrent_state_cd(), getAppl_details().getCurrent_off_cd());
                if (insDobj != null) {
                    if ((DateUtils.compareDates(DateUtils.addToDate(insDobj.getIns_from(), DateUtils.MONTH, -1), OwnerDetailsDobj.getPurchase_date()) != 1)) {
                        if (ins_dobj_ret != null) {
                            insDobj.setIdv(ins_dobj_ret.getIdv());
                        } else if (ins_dobj_retVA != null) {
                            insDobj.setIdv(ins_dobj_retVA.getIdv());
                        }
                        ins_bean.set_Ins_dobj_to_bean(insDobj);
                        ins_bean.setDisable(true);
                        //end of getting insurance details from service 
                    }
                    if (!insDobj.isIibData()) {
                        ins_bean.componentReadOnly(true);
                        ins_bean.setGovtVehFlag(false);
                        ins_bean.validateInsurance(ins_dobj_ret, ins_dobj_retVA, false);
                    } //for checking insurance availablity and expiration end  

                }
                //for checking insurance availablity and expiration start

                if (appl_details.getCurrent_role() == TableConstants.TM_ROLE_VERIFICATION
                        || appl_details.getCurrent_role() == TableConstants.TM_ROLE_APPROVAL) {
                    VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(OwnerDobj);
                    String seriesAvailMessage = ServerUtil.getAvailablePrefixSeries(vehParameters);
                    if (!seriesAvailMessage.equals(TableConstants.SERIES_EXHAUST_MESSAGE) && !seriesAvailMessage.equals("")) {
                        seriesAvailMessage = "Vehicle Registration No will be Generated from the Series " + seriesAvailMessage + ".";
                        setSeriesAvailMessage(seriesAvailMessage);
                    }

                    setDisableOddEvenChk(true);
                    String regnNoAllotted = NewImpl.getAdvanceRegnNo(appl_details.getAppl_no());
                    if (regnNoAllotted != null && !regnNoAllotted.isEmpty()) {
                        setNew_regn_no(regnNoAllotted);
                        setAdvRegnCheck(true);
                        seriesAvailMessage = "Vehicle Registration No will be Generated  " + regnNoAllotted + ".";
                        setSeriesAvailMessage(seriesAvailMessage);
                    } else {
                        regnNoAllotted = NewImpl.getAdvanceRetenNo(appl_details.getAppl_no());
                        if (regnNoAllotted != null && !regnNoAllotted.isEmpty()) {
                            setNew_regn_no(regnNoAllotted);
                            setRetCheck(true);
                            seriesAvailMessage = "Vehicle Registration No will be Generated  " + regnNoAllotted + ".";
                            setSeriesAvailMessage(seriesAvailMessage);
                        } else {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Advance Registration Number is not selected.Registration Number Will be generated randomly  after approval.", "Advance Registration Number is not selected.Registration Number Will be generated randomly after approval."));
                        }
                    }
                    if (appl_details.getCurrent_role() == TableConstants.TM_ROLE_VERIFICATION && CommonUtils.isNullOrBlank(regnNoAllotted)) {
                        setDisableAdvRegnCheck(false);
                        setDisableRetCheck(false);
                    }
                    if (appl_details.getCurrent_role() == TableConstants.TM_ROLE_APPROVAL) {
                        setDisableAdvRegnCheck(true);
                        setDisableRetCheck(true);
                        ArrayList<Status_dobj> statusDobjList = ServerUtil.applicationStatusByApplNo(appl_details.getAppl_no(), Util.getUserStateCode());
                        if (!statusDobjList.isEmpty() && statusDobjList.size() > 1) {
                            throw new VahanException("Please Apporve the other pending transactions first.");
                        }
                        if (configDobj != null) {
                            if (configDobj.getRegn_gen_type().equalsIgnoreCase(TableConstants.NO_GEN_TYPE_MIX_P)) {
                                regnNoAllotted = ServerUtil.getRegnNoAllotedDetail(appl_details.getAppl_no(), appl_details.getCurrent_state_cd(), appl_details.getCurrent_off_cd());
                                if (!CommonUtils.isNullOrBlank(regnNoAllotted)) {
                                    setNew_regn_no(regnNoAllotted);
                                }
                            }
                            renderConfirmationCheck = true;
                            approvalDialog = false;
                        }
                    }
                }
            }
        } catch (VahanException ve) {
            vahanMessages = ve.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            vahanMessages = "Something Went Wrong, Please Contact to the System Administrator";
        }
    }

    public Rereg_dobj make_dobj() {
        Rereg_dobj dobj = new Rereg_dobj();
        if (validateForm()) {

            dobj.setAppl_no(getAppl_no());
            dobj.setNew_regn_no(getNew_regn_no());
            dobj.setOld_regn_no(getOld_regn_no());
            dobj.setReason(getReason());
            dobj.setState_cd(getState_cd());
            dobj.setOff_cd(getOff_cd());
            dobj.setRetRegnNo(retCheck);
            dobj.setAdvRegnNo(advRegnCheck);
            dobj.setPmt_type(pmt_type);
            dobj.setPmt_catg(pmt_catg);
            dobj.setOddEvenOpted(oddEvenOpted);
        }
        return dobj;
    }

    public void advanceCheckListener() {
        if (advRegnCheck) {
            advRegnCheckDialogue = true;
            retCheck = false;
        }
    }

    public void retCheckListener() {
        if (retCheck) {
            advRetCheckDialogue = true;
            advRegnCheck = false;
        }
    }

    public void setBeanFromDobj(Rereg_dobj dobj) {
        if (dobj != null) {
            setAppl_no(dobj.getAppl_no());
            setNew_regn_no(dobj.getNew_regn_no());
            setOld_regn_no(dobj.getOld_regn_no());
            setReason(dobj.getReason());
            setState_cd(dobj.getState_cd());
            setOff_cd(dobj.getOff_cd());
            reasonSelect = dobj.getReason();
            if (reason != null) {
                if (!reasonSelect.equalsIgnoreCase("Govt Order/Notification")
                        && !reasonSelect.equalsIgnoreCase("Other State Vehicle")
                        && !reasonSelect.equalsIgnoreCase("DTO ORDER")
                        && !reasonSelect.equalsIgnoreCase("Wrong Permit Details")) {
                    reasonSelect = "Other Case";
                }
            }
            setPmt_type(dobj.getPmt_type());
            setPmt_catg(dobj.getPmt_catg());
            setOddEvenOpted(dobj.isOddEvenOpted());
        }
    }

    public void advanceSaveListener() {
        //fill details from AdvanceRegnNo_dobj
        if (getAdvRegnNoDobj().getRegn_no() != null) {
            setNew_regn_no(getAdvRegnNoDobj().getRegn_no());
            setDisableRetCheck(true);
        } else if (getRetenRegNoDobj().getRegn_no() != null) {
            setNew_regn_no(getRetenRegNoDobj().getRegn_no());
            setDisableAdvRegnCheck(true);
        } else {
            setAdvRegnNoDobj(null);
            setRetenRegNoDobj(null);
            setAdvRegnCheck(false);
            setRetCheck(false);
        }
    }

    public void advanceRcptListener() {
        try {
            if (advRegnCheck) {
                String rcptno = getAdvRegnNoDobj().getRecp_no();
                Date rcptDate = NewImpl.getFancyNoRcptDate(rcptno);
                if (rcptDate != null) {
                    NewImpl.validationFancyRcptDate(Util.getTmConfiguration(), rcptno, rcptDate);
                    AdvanceRegnNo_dobj dobj = NewImpl.getAdvanceRegNoDetails(rcptno);
                    setAdvRegnNoDobj(dobj);
                    String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
                    getList_adv_district().clear();

                    for (int i = 0; i < data.length; i++) {
                        if (data[i][2].trim().equals(getAdvRegnNoDobj().getState_cd())) {
                            getList_adv_district().add(new SelectItem(data[i][0], data[i][1]));
                        }
                    }
                    if (!getAppl_details().getOwnerDobj().getOwner_name().trim().equalsIgnoreCase(dobj.getOwner_name().trim())
                            || !getAppl_details().getOwnerDobj().getF_name().equalsIgnoreCase(dobj.getF_name())) {
                        JSFUtils.setFacesMessage("Owner Name and Father Name does not match!", null, JSFUtils.ERROR);
                        AdvanceRegnNo_dobj dobj1 = new AdvanceRegnNo_dobj();
                        setAdvRegnNoDobj(dobj1);
                    }
                } else {
                    throw new VahanException("Receipt Date Not Found");
                }
            } else if (retCheck) {
                String rcptno = getRetenRegNoDobj().getRecp_no();
                if (rcptno == null || rcptno.isEmpty()) {
                    return;
                }
                Date rcptDate = NewImpl.getRetainNoRcptDate(rcptno, appl_details.getCurrent_state_cd(), appl_details.getCurrent_off_cd());
                if (rcptDate != null) {
                    if ("SK".contains(appl_details.getCurrent_state_cd())
                            && (appl_details.getOwnerDobj().getOwner_cd() == TableConstants.VEH_TYPE_GOVT
                            || appl_details.getOwnerDobj().getOwner_cd() == TableConstants.VEH_TYPE_STATE_GOVT
                            || appl_details.getOwnerDobj().getOwner_cd() == TableConstants.VEH_TYPE_GOVT_UNDERTAKING)) {
                        // Do Nothing
                    } else {
                        NewImpl.validationRetainNoRcptDate(Util.getTmConfiguration(), rcptno, rcptDate);
                    }
                    RetenRegnNo_dobj dobj = NewImpl.getRetenRegNoDetails(rcptno);
                    setRetenRegNoDobj(dobj);
                    String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
                    list_adv_district.clear();

                    for (int i = 0; i < data.length; i++) {
                        if (data[i][2].trim().equals(getRetenRegNoDobj().getState_cd())) {
                            list_adv_district.add(new SelectItem(data[i][0], data[i][1]));
                        }
                    }
                    if (!appl_details.getCurrent_state_cd().equalsIgnoreCase("HR")) {
                        if (!getAppl_details().getOwnerDobj().getOwner_name().equalsIgnoreCase(dobj.getOwner_name())
                                || !getAppl_details().getOwnerDobj().getF_name().equalsIgnoreCase(dobj.getF_name())) {
                            JSFUtils.setFacesMessage("Owner Name and Father Name does not match!", null, JSFUtils.ERROR);
                            RetenRegnNo_dobj dobj1 = new RetenRegnNo_dobj();
                            setRetenRegNoDobj(dobj1);
                        }
                    }

                } else {
                    throw new VahanException("Receipt Date Not Found");
                }
            }

        } catch (VahanException ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), "Please Try Again!");
            FacesContext.getCurrentInstance().addMessage(null, message);
            AdvanceRegnNo_dobj dobj = new AdvanceRegnNo_dobj();
            setAdvRegnNoDobj(dobj);
        }
    }

    public void advanceExitListener() {
        if (advRegnCheck) {
            setAdvRegnNoDobj(null);
            setAdvRegnCheck(false);
        }
        if (retCheck) {
            setRetenRegNoDobj(null);
            setRetCheck(false);
        }
    }

    public ArrayList<ComparisonBean> addToComapreChangesList(ArrayList<ComparisonBean> compBeanListPrev) throws VahanException {

        ArrayList<ComparisonBean> list = compareChanges();

        if (compBeanListPrev == null) {
            compBeanListPrev = new ArrayList<ComparisonBean>();
        }
        if (list.size() > 0) {
            compBeanListPrev.addAll(list);
        }

        return compBeanListPrev;
    }

    public void validatePermitType(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (permitPanel && value != null) {
            FacesMessage msg = null;
            if (isIsPmtTypeRqrd() && Integer.parseInt(value.toString()) < 0) {
                msg = new FacesMessage("Invalid Permit Type, Please Select Valid Permit Type");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
        }
    }

    public void validatePermitCatg(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (permitPanel && value != null) {
            FacesMessage msg = null;
            if (isIsPmtTypeRqrd() && Integer.parseInt(value.toString()) < 0) {
                msg = new FacesMessage("Invalid Permit Category, Please Select Valid Permit Category");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
        }
    }

    public void permitTypeChangeListener() {
        if (getPmt_type() > 0) {
            getPermitCategoryList().clear();
            String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
            for (int j = 0; j < data.length; j++) {
                if (data[j][0].equalsIgnoreCase(Util.getUserStateCode())
                        && Integer.parseInt(data[j][3]) == getPmt_type()) {
                    getPermitCategoryList().add(new SelectItem(data[j][1], data[j][2]));
                }
            }
        }
    }

    /**
     *
     * @return
     */
    @Override
    public ArrayList<ComparisonBean> compareChanges() {

        Rereg_dobj dobj = getPrevReReg_dobj();  //getting the dobj from workbench

        if (dobj == null) {
            return compBeanList;
        }
        compBeanList.clear();
        if (!Utility.isNullOrBlank(getNew_regn_no())) {
            Compare("Registration No", dobj.getNew_regn_no(), this.getNew_regn_no(), compBeanList);
        }
        Compare("Reason", dobj.getReason(), this.getReason(), compBeanList);
        Compare("Permit Type", dobj.getPmt_type(), this.getPmt_type(), compBeanList);
        Compare("Permit Category", dobj.getPmt_catg(), this.getPmt_catg(), compBeanList);

        return getCompBeanList();

    }

    private boolean validateForm() {

        // validate is pending here...
        return true;
    }

    public ArrayList getListReason() {
        return listReason;
    }

    public void setListReason(ArrayList listReason) {
        this.listReason = listReason;
    }

    public Date getOp_dt() {
        return op_dt;
    }

    public void setOp_dt(Date op_dt) {
        this.op_dt = op_dt;
    }

    public void reasonChangeListener(ValueChangeEvent event) {

        String reasonSelected = (String) event.getNewValue();
        if (!reasonSelected.trim().equalsIgnoreCase("Other Case") && !reasonSelected.equals("-1")) {
            setReason(reasonSelected);
            setDisable(true);
        } else {
            setReason(null);
            setDisable(false);
        }

    }

    /**
     * @return the PrevReReg_dobj
     */
    public Rereg_dobj getPrevReReg_dobj() {
        return prevReReg_dobj;
    }

    /**
     * @param PrevReReg_dobj the PrevReReg_dobj to set
     */
    public void setPrevReReg_dobj(Rereg_dobj prevReReg_dobj) {
        this.prevReReg_dobj = prevReReg_dobj;
    }

    /**
     * @return the compBeanList
     */
    @Override
    public ArrayList<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    /**
     * @param compBeanList the compBeanList to set
     */
    public void setCompBeanList(ArrayList<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    /**
     * @return the reasonSelect
     */
    public String getReasonSelect() {
        return reasonSelect;
    }

    /**
     * @param reasonSelect the reasonSelect to set
     */
    public void setReasonSelect(String reasonSelect) {
        this.reasonSelect = reasonSelect;
    }

    @Override
    public String save() {
        String return_location = "";
        try {
            ArrayList<ComparisonBean> compareChanges = compareChanges();
            //for updating or inserting insurance details with Re-Registration details
            InsDobj ins_dobj_new = new InsDobj();
            ins_dobj_new = ins_bean.set_InsBean_to_dobj();
            if (ins_dobj_new != null && ins_bean.validateInsurance(ins_dobj_new)) {
                ins_bean.ins_update(OwnerDetailsDobj);
                if (!compareChanges.isEmpty() || getPrevReReg_dobj() == null) { //save only when data is really changed by user}
                    Rereg_Impl.saveChangeReReg(make_dobj(), ComparisonBeanImpl.changedDataContents(compareChanges));
                }
                return_location = "seatwork";
            } else {
                if (ins_dobj_new == null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Insurance Details not available !!!"));
                }
            }

        } catch (VahanException vme) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vme.getMessage(), vme.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-File Could Not Save and Move Due to Technical Error in Database", "Error-File Could Not Save and Move Due to Technical Error in Database"));
        }
        return return_location;
    }

    @Override
    public String saveAndMoveFile() {
        String return_location = "";
        InsDobj ins_dobj_new = new InsDobj();
        ins_dobj_new = ins_bean.set_InsBean_to_dobj();
        boolean flag = false;
        if (ins_dobj_new != null) {
            try {
                if (!getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                    if (ins_bean.validateInsurance(ins_dobj_new)) {
                        ins_bean.ins_update(OwnerDetailsDobj);
                    } else {
                        return return_location;
                    }
                }

                Status_dobj status = new Status_dobj();
                status.setAppl_dt(appl_details.getAppl_dt());
                status.setAppl_no(appl_details.getAppl_no());
                status.setPur_cd(appl_details.getPur_cd());
                status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
                status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
                status.setStatus(getApp_disapp_dobj().getNew_status());

                status.setCurrent_role(appl_details.getCurrent_role());

                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                        || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)
                        || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                    status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                    status.setVehicleParameters(appl_details.getVehicleParameters());
                    if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE) && this.appl_details.isDocVerifyForFacelessAppl()) {
                        String[] notVerifiedDocDetails = ServerUtil.verifyDocUploaded(appl_details.getAppl_no(), getAppl_details().getCurrent_action_cd());
                        if (notVerifiedDocDetails != null) {
                            appl_details.setNotVerifiedDocdetails(notVerifiedDocDetails[1]);
                            throw new VahanException(notVerifiedDocDetails[0]);
                        }
                    }
                    Rereg_Impl rereg_Impl = new Rereg_Impl();
                    rereg_Impl.update_ReReg_Status(status.getCurrent_role(), make_dobj(), status, ComparisonBeanImpl.changedDataContents(compareChanges()), appl_details);
                    if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                        ServerUtil.sendSmsInFacelessService(appl_details.getAppl_no(), Util.getUserStateCode(), Util.getUserSeatOffCode(), appl_details.getRegn_no(), appl_details.getNotVerifiedDocdetails());
                        return_location = disapprovalPrint();
                    } else {
                        return_location = "seatwork";
                    }
                }
            } catch (VahanException ve) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ve.getMessage()));
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(TableConstants.SomthingWentWrong));
            }
        } else {
            if (ins_dobj_new == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Insurance Details not available !!!"));
            }
        }
        return return_location;
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        return prevChangedDataList;
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        this.prevChangedDataList = prevChangedDataList;
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @param ins_bean the ins_bean to set
     */
    public void setIns_bean(InsBean ins_bean) {
        this.ins_bean = ins_bean;
    }

    /**
     * @return the appl_no
     */
    public String getAppl_no() {
        return appl_no;
    }

    /**
     * @param appl_no the appl_no to set
     */
    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    /**
     * @return the old_regn_no
     */
    public String getOld_regn_no() {
        return old_regn_no;
    }

    /**
     * @param old_regn_no the old_regn_no to set
     */
    public void setOld_regn_no(String old_regn_no) {
        this.old_regn_no = old_regn_no;
    }

    /**
     * @return the new_regn_no
     */
    public String getNew_regn_no() {
        return new_regn_no;
    }

    /**
     * @param new_regn_no the new_regn_no to set
     */
    public void setNew_regn_no(String new_regn_no) {
        this.new_regn_no = new_regn_no;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return the state_cd
     */
    public String getState_cd() {
        return state_cd;
    }

    /**
     * @param state_cd the state_cd to set
     */
    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    /**
     * @return the off_cd
     */
    public int getOff_cd() {
        return off_cd;
    }

    /**
     * @param off_cd the off_cd to set
     */
    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }

    /**
     * @return the advRegnNoDobj
     */
    public AdvanceRegnNo_dobj getAdvRegnNoDobj() {
        return advRegnNoDobj;
    }

    /**
     * @param advRegnNoDobj the advRegnNoDobj to set
     */
    public void setAdvRegnNoDobj(AdvanceRegnNo_dobj advRegnNoDobj) {
        this.advRegnNoDobj = advRegnNoDobj;
    }

    /**
     * @return the advRegnCheck
     */
    public boolean isAdvRegnCheck() {
        return advRegnCheck;
    }

    /**
     * @param advRegnCheck the advRegnCheck to set
     */
    public void setAdvRegnCheck(boolean advRegnCheck) {
        this.advRegnCheck = advRegnCheck;
    }

    /**
     * @return the list_adv_district
     */
    public List getList_adv_district() {
        return list_adv_district;
    }

    /**
     * @param list_adv_district the list_adv_district to set
     */
    public void setList_adv_district(List list_adv_district) {
        this.list_adv_district = list_adv_district;
    }

    /**
     * @return the list_c_state
     */
    public List getList_c_state() {
        return list_c_state;
    }

    /**
     * @param list_c_state the list_c_state to set
     */
    public void setList_c_state(List list_c_state) {
        this.list_c_state = list_c_state;
    }

    /**
     * @return the disable
     */
    public boolean isDisable() {
        return disable;
    }

    /**
     * @param disable the disable to set
     */
    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    /**
     * @return the advRegnCheckDialogue
     */
    public boolean isAdvRegnCheckDialogue() {
        return advRegnCheckDialogue;
    }

    /**
     * @param advRegnCheckDialogue the advRegnCheckDialogue to set
     */
    public void setAdvRegnCheckDialogue(boolean advRegnCheckDialogue) {
        this.advRegnCheckDialogue = advRegnCheckDialogue;
    }

    /**
     * @return the disableAdvRegnCheck
     */
    public boolean isDisableAdvRegnCheck() {
        return disableAdvRegnCheck;
    }

    /**
     * @param disableAdvRegnCheck the disableAdvRegnCheck to set
     */
    public void setDisableAdvRegnCheck(boolean disableAdvRegnCheck) {
        this.disableAdvRegnCheck = disableAdvRegnCheck;
    }

    /**
     * @return the retenRegNoDobj
     */
    public RetenRegnNo_dobj getRetenRegNoDobj() {
        return retenRegNoDobj;
    }

    /**
     * @param retenRegNoDobj the retenRegNoDobj to set
     */
    public void setRetenRegNoDobj(RetenRegnNo_dobj retenRegNoDobj) {
        this.retenRegNoDobj = retenRegNoDobj;
    }

    /**
     * @return the retCheck
     */
    public boolean isRetCheck() {
        return retCheck;
    }

    /**
     * @param retCheck the retCheck to set
     */
    public void setRetCheck(boolean retCheck) {
        this.retCheck = retCheck;
    }

    /**
     * @return the disableRetCheck
     */
    public boolean isDisableRetCheck() {
        return disableRetCheck;
    }

    /**
     * @param disableRetCheck the disableRetCheck to set
     */
    public void setDisableRetCheck(boolean disableRetCheck) {
        this.disableRetCheck = disableRetCheck;
    }

    /**
     * @return the advRetCheckDialogue
     */
    public boolean isAdvRetCheckDialogue() {
        return advRetCheckDialogue;
    }

    /**
     * @param advRetCheckDialogue the advRetCheckDialogue to set
     */
    public void setAdvRetCheckDialogue(boolean advRetCheckDialogue) {
        this.advRetCheckDialogue = advRetCheckDialogue;
    }

    /**
     * @return the retAllowed
     */
    public boolean isRetAllowed() {
        return retAllowed;
    }

    /**
     * @param retAllowed the retAllowed to set
     */
    public void setRetAllowed(boolean retAllowed) {
        this.retAllowed = retAllowed;
    }

    /**
     * @return the vahanMessages
     */
    public String getVahanMessages() {
        return vahanMessages;
    }

    /**
     * @param vahanMessages the vahanMessages to set
     */
    public void setVahanMessages(String vahanMessages) {
        this.vahanMessages = vahanMessages;
    }

    /**
     * @return the permitPanel
     */
    public boolean isPermitPanel() {
        return permitPanel;
    }

    /**
     * @param permitPanel the permitPanel to set
     */
    public void setPermitPanel(boolean permitPanel) {
        this.permitPanel = permitPanel;
    }

    /**
     * @return the pmt_type
     */
    public int getPmt_type() {
        return pmt_type;
    }

    /**
     * @param pmt_type the pmt_type to set
     */
    public void setPmt_type(int pmt_type) {
        this.pmt_type = pmt_type;
    }

    /**
     * @return the pmt_catg
     */
    public int getPmt_catg() {
        return pmt_catg;
    }

    /**
     * @param pmt_catg the pmt_catg to set
     */
    public void setPmt_catg(int pmt_catg) {
        this.pmt_catg = pmt_catg;
    }

    /**
     * @return the permitTypeList
     */
    public List getPermitTypeList() {
        return permitTypeList;
    }

    /**
     * @param permitTypeList the permitTypeList to set
     */
    public void setPermitTypeList(List permitTypeList) {
        this.permitTypeList = permitTypeList;
    }

    /**
     * @return the PermitCategoryList
     */
    public List getPermitCategoryList() {
        return PermitCategoryList;
    }

    /**
     * @param PermitCategoryList the PermitCategoryList to set
     */
    public void setPermitCategoryList(List PermitCategoryList) {
        this.PermitCategoryList = PermitCategoryList;
    }

    /**
     * @return the isPmtTypeRqrd
     */
    public boolean isIsPmtTypeRqrd() {
        return isPmtTypeRqrd;
    }

    /**
     * @param isPmtTypeRqrd the isPmtTypeRqrd to set
     */
    public void setIsPmtTypeRqrd(boolean isPmtTypeRqrd) {
        this.isPmtTypeRqrd = isPmtTypeRqrd;
    }

    /**
     * @return the isPmtCatgRqrd
     */
    public boolean isIsPmtCatgRqrd() {
        return isPmtCatgRqrd;
    }

    /**
     * @param isPmtCatgRqrd the isPmtCatgRqrd to set
     */
    public void setIsPmtCatgRqrd(boolean isPmtCatgRqrd) {
        this.isPmtCatgRqrd = isPmtCatgRqrd;
    }

    /**
     * @return the seriesAvailMessage
     */
    public String getSeriesAvailMessage() {
        return seriesAvailMessage;
    }

    /**
     * @param seriesAvailMessage the seriesAvailMessage to set
     */
    public void setSeriesAvailMessage(String seriesAvailMessage) {
        this.seriesAvailMessage = seriesAvailMessage;
    }

    /**
     * @return the approvalDialog
     */
    public boolean isApprovalDialog() {
        return approvalDialog;
    }

    /**
     * @param approvalDialog the approvalDialog to set
     */
    public void setApprovalDialog(boolean approvalDialog) {
        this.approvalDialog = approvalDialog;
    }

    /**
     * @return the renderConfirmationCheck
     */
    public boolean isRenderConfirmationCheck() {
        return renderConfirmationCheck;
    }

    /**
     * @param renderConfirmationCheck the renderConfirmationCheck to set
     */
    public void setRenderConfirmationCheck(boolean renderConfirmationCheck) {
        this.renderConfirmationCheck = renderConfirmationCheck;
    }

    /**
     * @return the oddEvenRender
     */
    public boolean isOddEvenRender() {
        return oddEvenRender;
    }

    /**
     * @param oddEvenRender the oddEvenRender to set
     */
    public void setOddEvenRender(boolean oddEvenRender) {
        this.oddEvenRender = oddEvenRender;
    }

    /**
     * @return the oddEvenOpted
     */
    public boolean isOddEvenOpted() {
        return oddEvenOpted;
    }

    /**
     * @param oddEvenOpted the oddEvenOpted to set
     */
    public void setOddEvenOpted(boolean oddEvenOpted) {
        this.oddEvenOpted = oddEvenOpted;
    }

    /**
     * @return the disableOddEvenChk
     */
    public boolean isDisableOddEvenChk() {
        return disableOddEvenChk;
    }

    /**
     * @param disableOddEvenChk the disableOddEvenChk to set
     */
    public void setDisableOddEvenChk(boolean disableOddEvenChk) {
        this.disableOddEvenChk = disableOddEvenChk;
    }
}
