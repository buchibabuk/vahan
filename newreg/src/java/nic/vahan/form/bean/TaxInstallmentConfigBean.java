/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import static nic.vahan.CommonUtils.FormulaUtils.fillTaxParametersFromDobj;
import nic.vahan.CommonUtils.TaxUtils;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import org.apache.log4j.Logger;
import nic.vahan.form.dobj.TaxInstallmentConfigDobj;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.TaxInstallmentConfigImpl;
import nic.vahan.form.impl.ComparisonBeanImpl;
import static nic.vahan.server.ServerUtil.Compare;
import nic.vahan.form.impl.Util;
import org.primefaces.PrimeFaces;
import nic.vahan.form.bean.permit.PermitPanelBean;
import nic.vahan.form.dobj.DOTaxDetail;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.TaxServer_Impl;
import nic.vahan.server.ServerUtil;
import nic.vahan.form.impl.EpayImpl;
//import tax.web.service.VahanTaxParameters;
import nic.vahan.form.dobj.tax.VahanTaxParameters;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.form.dobj.BlackListedVehicleDobj;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.TaxExemptiondobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.impl.BlackListedVehicleImpl;
import nic.vahan.form.impl.ExemptionFeeFineImpl;
import static nic.vahan.form.impl.TaxServer_Impl.callTaxService;
import nic.vahan.server.CommonUtils;

/**
 *
 * @author tranC106
 */
@ManagedBean(name = "taxInstallmentConfigBean")
@ViewScoped
public class TaxInstallmentConfigBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(TaxInstallmentConfigBean.class);
    private TaxInstallmentConfigDobj taxInstallmentConfigDobj = new TaxInstallmentConfigDobj();
    private TaxInstallmentConfigDobj taxInstallmentConfigDobjdel = new TaxInstallmentConfigDobj();
    private TaxInstallmentConfigDobj taxInstallmentFileNoDobj = new TaxInstallmentConfigDobj();
    private TaxInstallmentConfigDobj taxInstallmentFileNoDobj_prv = new TaxInstallmentConfigDobj();
    private TaxInstallmentConfigDobj taxInstallmentConfigDobj_prv = null;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private Map<String, Integer> purCodeList = null;
    private List<TaxInstallmentConfigDobj> taxInstallmentConfigDobjList = new ArrayList<>();
    private List<TaxInstallmentConfigDobj> taxInstallmentConfigDobj_prvList = new ArrayList<>();
    private List<TaxFormPanelBean> listTaxForm = new ArrayList();
    private OwnerImpl ownerImpl = null;
    private String regnNo;
    private String taxmode;
    private Owner_dobj ownerDobj;
    private boolean disable = false;
    private Long userChrg;
    private PermitPanelBean permitPanelBean = null;
    private boolean renderPermitPanel = false;
    private boolean renderUserChargesAmountPanel = false;
    private boolean taxInstallmentPanel = false;
    private ArrayList list_vh_class = new ArrayList();
    private ArrayList list_vm_catg = new ArrayList();
    private ArrayList list_maker_model = new ArrayList();
    private Long totalTaxAmount;
    private long sumOfTotalTaxInstallment;
    private Long totalUserChrg = 0l;
    private boolean renderApplNoGenMessage;
    private List<ComparisonBean> prevChangedDataList;
    private Date maxDate = new Date();
    private Date minDate = DateUtil.parseDate(DateUtil.getCurrentDate());
    private Long lastinstallamt;
    private boolean addDisabled;
    private OwnerDetailsDobj ownerDetail;
    private OwnerIdentificationDobj ownerIdentificationPrev = null;
    private boolean autoRunTaxListener = true;
    private List<DOTaxDetail> taxDescriptionList = new ArrayList<>();
    private List<TaxExemptiondobj> taxExemList = new ArrayList<>();
    //Add for permit
    private PassengerPermitDetailDobj permitDob = null;

    public TaxInstallmentConfigBean() {
        permitDob = null;
        renderPermitPanel = false;
        ownerImpl = new OwnerImpl();
        permitPanelBean = new PermitPanelBean();
        String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
        getList_vh_class().clear();
        for (int i = 0; i < data.length; i++) {
            getList_vh_class().add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.VM_VCH_CATG.getData();
        for (int i = 0; i < data.length; i++) {
            getList_vm_catg().add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.VM_MODELS.getData();
        for (int i = 0; i < data.length; i++) {

            getList_maker_model().add(new SelectItem(data[i][1], data[i][2]));
        }

        try {
            if (getAppl_details().getOwnerDobj() != null) {
                TaxInstallmentConfigImpl impl = new TaxInstallmentConfigImpl();
                taxInstallmentConfigDobjList = impl.getTaxInstCongigDetails(getAppl_details().getRegn_no(), getAppl_details().getAppl_no());
                for (TaxInstallmentConfigDobj taxInstallmentConfigDobjnew : taxInstallmentConfigDobjList) {
                    int i = 0;
                    taxInstallmentConfigDobj_prvList.add((TaxInstallmentConfigDobj) taxInstallmentConfigDobjnew.clone());
                    i++;
                }
                taxInstallmentConfigDobj = (TaxInstallmentConfigDobj) taxInstallmentConfigDobjList.get(getTaxInstallmentConfigDobjList().size() - 1).clone();
                TaxInstallmentConfigImpl impl2 = new TaxInstallmentConfigImpl();
                taxInstallmentFileNoDobj = impl2.getTaxInstFileNoDetails(getAppl_details().getRegn_no(), getAppl_details().getAppl_no());
                taxInstallmentFileNoDobj_prv = (TaxInstallmentConfigDobj) taxInstallmentFileNoDobj.clone();
                taxmode = taxInstallmentFileNoDobj.getTaxmode();
                setTaxInstallmentPanel(true);
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                regnNo = getAppl_details().getRegn_no();
                regnNoDetails();
                listTaxForm.get(0).setTaxMode(taxmode);
                TaxFormPanelBean taxSelectedFormBean = listTaxForm.get(0);
                taxModeListener(taxSelectedFormBean);
                setSumOfTotalTaxInstallment(totalTaxAmount);
                setAddDisabled(true);
                taxInstallmentConfigDobj.setSerialno((Integer.valueOf(taxInstallmentConfigDobj.getSerialno()) + 1) + "");
                taxInstallmentConfigDobj.setTaxamountinstl(0L);
                minDate = ServerUtil.dateRange(taxInstallmentConfigDobj.getPayduedt(), 0, 0, 1);
                taxInstallmentConfigDobj.setPayduedt(null);
                //RequestContext rc = RequestContext.getCurrentInstance();
                PrimeFaces.current().ajax().update("taxInstallmentId:totalPaybaleSumInstallmentPanel");
            }

        } catch (Exception ex) {
            String mgs = Util.getLocaleSomthingMsg();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, mgs, mgs));
        }

    }

    public void regnNoDetails() {
        if (Utility.isNullOrBlank(regnNo)) {
            FacesMessage message = null;
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("enter_regn_no")));
            return;
        }
        ExemptionFeeFineImpl exemImpl = new ExemptionFeeFineImpl();
        regnNo = regnNo.toUpperCase();
        getListTaxForm().clear();
        Exception e = null;
        TaxInstallmentConfigImpl tci = new TaxInstallmentConfigImpl();
        String taxMode = "";
        Map<String, String> taxModeMap = new HashMap<>();
        String msg = "";
        BlackListedVehicleDobj blacklistedStatus = null;
        String nocStatus = null;
        Boolean blnnocfound = false;
        Boolean blnEMIfound = false;

        try {
            if (taxInstallmentConfigDobj_prvList.isEmpty()) {
                //For restricting the user to generate application no again and again.start           
                List<Status_dobj> statusList = ServerUtil.applicationStatus(regnNo);
                if (!statusList.isEmpty()) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("pending_applno") + " [" + statusList.get(0).getAppl_no() + "]"));
                    return;
                }  //For restricting the user to generate application no again and again.end
            }
            if (ownerImpl != null) {

                OwnerImpl owner_Impl = new OwnerImpl();
                setOwnerDetail(owner_Impl.getOwnerDetails(regnNo, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd()));
                if (getOwnerDetail() == null) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("regn_not_found")));
                    return;
                }
                //Check for blacklisted
                BlackListedVehicleImpl obj = new BlackListedVehicleImpl();
                blacklistedStatus = obj.getBlacklistedVehicleDetails(regnNo, getOwnerDetail().getChasi_no());
                if (blacklistedStatus != null) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("VehBlckNoInstllmt")));
                    return;
                }
                //Check for Vehicle NOC
                if (ownerDetail != null && ownerDetail.getStatus().equalsIgnoreCase(TableConstants.VT_NOC_ISSUE_STATUS)) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("vehNocNoInstllmt")));
                    return;
                }

                //Check for Vehicle Already EMI
                TaxInstallmentConfigImpl objalreadyEmi = new TaxInstallmentConfigImpl();
                blnEMIfound = objalreadyEmi.checkalreadymadeEMI(regnNo);
                if (blnEMIfound) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("alreadyGenInstllmt")));
                    return;
                }
                //for Owner Identification Fields disallow typing
                if (getOwnerDetail().getOwnerIdentity() != null) {
                    //ownerDetail.getOwnerIdentity().setFlag(true);
                    //ownerDetail.getOwnerIdentity().setMobileNoEditable(true);
                    if (getOwnerDetail().getOwnerIdentity().getMobile_no() == null) {
                        getOwnerDetail().getOwnerIdentity().setMobile_no(0l);
                    }
                    setOwnerIdentificationPrev((OwnerIdentificationDobj) getOwnerDetail().getOwnerIdentity().clone());
                }

                setOwnerDobj(ownerImpl.set_Owner_appl_db_to_dobj_with_state_off_cd(getRegnNo(), null, null, 2));
                if (getOwnerDobj() != null) {
                    setDisable(true);
                    //calculateTax(ownerDobj);
                    Map<Integer, String> taxPurCdDesc = TaxServer_Impl.getTaxPurCodeDescr(getOwnerDobj());
                    for (Map.Entry<Integer, String> entry : taxPurCdDesc.entrySet()) {
                        TaxFormPanelBean bean = new TaxFormPanelBean();
                        bean.setPur_cd(entry.getKey());
                        bean.setTaxPurcdDesc(entry.getValue());
                        List<SelectItem> listTaxModes = getListTaxModes(getOwnerDobj(), bean.getPur_cd());
                        bean.setListTaxModes(listTaxModes);

                        getListTaxForm().add(bean);
                    }
                    int vehClass = getOwnerDobj().getVh_class();
                    //Fetching Tax Penalty Exemption Details
                    setTaxExemList(exemImpl.getExemptionDetails(regnNo));

                    setTaxInstallmentPanel(true);

                    //AddFor Permit
                    permitDob = TaxServer_Impl.getPermitInfoForSavedTax(ownerDobj);
                    if (permitDob == null) {
                        permitDob = objalreadyEmi.getPermitBaseOnTaxPermitInfo(ownerDobj.getState_cd(), ownerDobj.getOff_cd(), regnNo);
                    }
                    if (permitDob != null && permitDob.getOtherCriteria() != null && !permitDob.getOtherCriteria().isEmpty() && !permitDob.getOtherCriteria().equalsIgnoreCase("0")) {
                        ownerDobj.setOther_criteria(Integer.parseInt(permitDob.getOtherCriteria()));
                    }
                    if (ServerUtil.isTaxOnPermit(vehClass, Util.getUserStateCode())) {
                        if (permitDob != null) {
                            getPermitPanelBean().setPermitDobj(permitDob);
                            getPermitPanelBean().onSelectPermitType(null);
                        }
                        getPermitPanelBean().setIsDisable(true);
                        getPermitPanelBean().setIsDisableForInstlmnt(true);
                        setRenderPermitPanel(true);
                    } else {
                        getPermitPanelBean().setPermitDobj(null);
                        setRenderPermitPanel(false);
                        getPermitPanelBean().setIsDisable(false);
                        getPermitPanelBean().setIsDisableForInstlmnt(false);
                    }

                } else {
                    FacesMessage message = null;
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("regn_not_found")));
                    return;
                }
            }
        } catch (VahanException ve) {
            LOGGER.error(ve);
            e = ve;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            e = ex;
        }

        if (e != null) {
            FacesMessage message = null;
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), e.getMessage()));
            reset();
            return;
        }
    }

    public void reset() {
        setRegnNo("");
        setOwnerDobj(null);
    }

    private List<SelectItem> getListTaxModes(Owner_dobj ownerDobj, int pur_cd) throws VahanException {
        String[] taxModes = TaxServer_Impl.getAvailalableTaxModes(ownerDobj, pur_cd, null);
        List<SelectItem> listTaxModes = new ArrayList();
        String[][] dataTaxModes = MasterTableFiller.masterTables.VM_TAX_MODE.getData();
        if (taxModes != null) {
            listTaxModes.add(new SelectItem("0", "--Select--"));
            for (int i = 0; i < taxModes.length; i++) {
                for (int ii = 0; ii < dataTaxModes.length; ii++) {
                    if (dataTaxModes[ii][0].trim().equals(taxModes[i].trim())) {
                        listTaxModes.add(new SelectItem(dataTaxModes[ii][0], dataTaxModes[ii][1]));
                        break;
                    }
                }
            }
        }

        return listTaxModes;
    }

    public void addRowsInTaxInstallmentDobjList() {
        if (getTotalTaxAmount() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " Please Select Tax Mode", " Please Select Tax Mode"));
            return;
        }
        try {
            if (taxInstallmentConfigDobj != null && taxInstallmentConfigDobj.getTaxamountinstl() != 0 && taxInstallmentConfigDobj.getPayduedt() != null) {
                TaxInstallmentConfigDobj dobj = new TaxInstallmentConfigDobj();
                dobj.setRegnno(this.getRegnNo());
                if (getAppl_details().getAppl_no() != null) {
                    dobj.setAppl_no(taxInstallmentConfigDobj.getAppl_no());
                }
                dobj.setTaxmode(this.getListTaxForm().get(0).getTaxMode());
                dobj.setTaxfromdt(new java.sql.Date((DateUtils.parseDate(this.getListTaxForm().get(0).getFinalTaxFrom())).getTime()));
                dobj.setTaxuptodt(new java.sql.Date((DateUtils.parseDate(this.getListTaxForm().get(0).getFinalTaxUpto())).getTime()));
                dobj.setFilerefno(taxInstallmentFileNoDobj.getFilerefno());
                dobj.setOrderissueby(taxInstallmentFileNoDobj.getOrderissueby());
                dobj.setOrderno(taxInstallmentFileNoDobj.getOrderno());
                dobj.setOrderdate(taxInstallmentFileNoDobj.getOrderdate());
                dobj.setSerialnotable(taxInstallmentConfigDobj.getSerialno());
                dobj.setTaxamountinstltable(taxInstallmentConfigDobj.getTaxamountinstl());
                Date addedDate = taxInstallmentConfigDobj.getPayduedt();
                DateFormat frm_dte_formatter = new SimpleDateFormat("dd-MMM-yyyy");
                String returnDate = frm_dte_formatter.format(((java.util.Date) addedDate));
                dobj.setDueDateStr(returnDate);
                setSumOfTotalTaxInstallment(getSumOfTotalTaxInstallment() + dobj.getTaxamountinstltable());
                //RequestContext rc = RequestContext.getCurrentInstance();
                PrimeFaces.current().ajax().update("taxInstallmentId:totalPaybaleSumInstallmentPanel");
                if (getSumOfTotalTaxInstallment() >= getTotalTaxAmount()) {
                    setAddDisabled(true);
                }
                getTaxInstallmentConfigDobjList().add(dobj);
                //RequestContext rd = RequestContext.getCurrentInstance();
                PrimeFaces.current().ajax().update("taxInstallmentId:installmentpanel");
                taxInstallmentConfigDobj.setSerialno((Integer.valueOf(taxInstallmentConfigDobj.getSerialno()) + 1) + "");
                taxInstallmentConfigDobj.setTaxamountinstl(0L);
                minDate = ServerUtil.dateRange(taxInstallmentConfigDobj.getPayduedt(), 0, 0, 1);
                taxInstallmentConfigDobj.setPayduedt(null);
                //RequestContext re = RequestContext.getCurrentInstance();
                PrimeFaces.current().ajax().update("taxInstallmentId:instlmntBrkup");
            } else {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("notEptyAmtOrDate")));
                return;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleSomthingMsg()));
        }
    }

    public void deleteRowsInTaxInstallmentDobjList() {
        if (taxInstallmentConfigDobj != null) {
            try {
                setAddDisabled(false);
                taxInstallmentConfigDobj = taxInstallmentConfigDobjList.get(getTaxInstallmentConfigDobjList().size() - 1);
                if (taxInstallmentConfigDobjList.size() >= 2) {
                    taxInstallmentConfigDobjdel = taxInstallmentConfigDobjList.get(getTaxInstallmentConfigDobjList().size() - 2);
                }
                if (taxInstallmentConfigDobjList.size() == 1) {
                    minDate = DateUtil.parseDate(DateUtil.getCurrentDate());
                }
                lastinstallamt = taxInstallmentConfigDobj.getTaxamountinstltable();
                setSumOfTotalTaxInstallment(getSumOfTotalTaxInstallment() - lastinstallamt);
                taxInstallmentConfigDobj.setSerialno((Integer.valueOf(taxInstallmentConfigDobj.getSerialnotable())) + "");
                getTaxInstallmentConfigDobjList().remove(getTaxInstallmentConfigDobjList().size() - 1);
                //RequestContext rc = RequestContext.getCurrentInstance();
                PrimeFaces.current().ajax().update("taxInstallmentId:totalPaybaleSumInstallmentPanel");
                PrimeFaces.current().ajax().update("taxInstallmentId:installmentpanel");
                taxInstallmentConfigDobj.setTaxamountinstl(0L);
                if (taxInstallmentConfigDobjdel != null && taxInstallmentConfigDobjList.size() > 0) {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
                    Date returndate = formatter.parse(taxInstallmentConfigDobjdel.getDueDateStr());
                    new java.sql.Date(returndate.getTime());
                    minDate = ServerUtil.dateRange(new java.sql.Date(returndate.getTime()), 0, 0, 1);
                }
                taxInstallmentConfigDobj.setPayduedt(null);
                PrimeFaces.current().ajax().update("taxInstallmentId:instlmntBrkup");
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("notDelete")));
            }
        }

    }

    public void addfixeddataDobjList() {
        int i = 0;
        for (TaxInstallmentConfigDobj taxInstallmentConfigDobjn : taxInstallmentConfigDobjList) {

            taxInstallmentConfigDobjList.get(i).setTaxmode(taxInstallmentFileNoDobj.getTaxmode());
            taxInstallmentConfigDobjList.get(i).setTaxfromdt(taxInstallmentFileNoDobj.getTaxfromdt());
            taxInstallmentConfigDobjList.get(i).setTaxuptodt(taxInstallmentFileNoDobj.getTaxuptodt());
            taxInstallmentConfigDobjList.get(i).setFilerefno(taxInstallmentFileNoDobj.getFilerefno());
            taxInstallmentConfigDobjList.get(i).setOrderissueby(taxInstallmentFileNoDobj.getOrderissueby());
            taxInstallmentConfigDobjList.get(i).setOrderno(taxInstallmentFileNoDobj.getOrderno());
            taxInstallmentConfigDobjList.get(i).setOrderdate(taxInstallmentFileNoDobj.getOrderdate());
            i++;
        }
    }

    public void savetaxinstallmentconfig() {
        String appl_no = null;
        Exception e = null;
        if (getSumOfTotalTaxInstallment() != 0 && getTotalTaxAmount() != 0) {
            try {

                if (getSumOfTotalTaxInstallment() != getTotalTaxAmount() && !"OR".equalsIgnoreCase(Util.getUserStateCode())) {//add 
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("notEqualAmt")));
                    return;
                }
                Status_dobj status_dobj = new Status_dobj();
                status_dobj.setRegn_no(this.regnNo);
                status_dobj.setPur_cd(TableConstants.TAX_INSTALLMENT_PUR_CD);
                status_dobj.setFlow_slno(1);//initial flow serial no.
                status_dobj.setFile_movement_slno(1);//initial file movement serial no.
                status_dobj.setState_cd(Util.getUserStateCode());
                status_dobj.setOff_cd(getAppl_details().getCurrent_off_cd());
                status_dobj.setEmp_cd(0);
                status_dobj.setSeat_cd("");
                status_dobj.setCntr_id("");
                status_dobj.setStatus("N");
                status_dobj.setOffice_remark("");
                status_dobj.setPublic_remark("");
                status_dobj.setFile_movement_type("F");
                status_dobj.setUser_id(Util.getUserId());
                status_dobj.setAppl_date(ServerUtil.getSystemDateInPostgres());
                status_dobj.setUser_type("");
                status_dobj.setEntry_ip("");
                status_dobj.setEntry_status("");//for New File
                status_dobj.setConfirm_ip("");
                status_dobj.setConfirm_status("");
                status_dobj.setConfirm_date(new java.util.Date());

                TaxInstallmentConfigImpl impl = new TaxInstallmentConfigImpl();
                appl_no = impl.InsertIntoVATaxInstallmentConfigInward(status_dobj, getTaxInstallmentConfigDobjList());

            } catch (SQLException sqle) {
                e = sqle;
            } catch (VahanException ve) {
                e = ve;
            } catch (Exception ex) {
                e = ex;
            }
            if (e != null) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("occurExp") + " " + e.getMessage()));
                return;
            }
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("applnogen") + " : " + appl_no));
            setTaxInstallmentConfigDobj(new TaxInstallmentConfigDobj());
            purCodeList = null;
            getAppl_details().setOwnerDobj(null);
            setTaxInstallmentPanel(false);
            PrimeFaces.current().ajax().update("taxInstallmentId");
        } else {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("noZeroAmt")));
            return;
        }
    }

    @Override
    public String save() {
        String return_location = "";

        try {
            List<ComparisonBean> compareChanges = compareChanges();
            if (!compareChanges.isEmpty() || taxInstallmentConfigDobj_prv == null) { //save only when data is really changed by user
                addfixeddataDobjList();
                TaxInstallmentConfigImpl.makeChangeTaxInstallmentConfig(taxInstallmentConfigDobj, ComparisonBeanImpl.changedDataContents(compareChanges), getTaxInstallmentConfigDobjList());
            }
            return_location = "seatwork";

        } catch (VahanException vme) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), vme.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleSomthingMsg()));
        }
        return return_location;
    }

    @Override
    public List<ComparisonBean> compareChanges() {

        if (taxInstallmentConfigDobj_prvList == null) {
            return compBeanList;
        }
        compBeanList.clear();
        int i = 0;
        int prvListSize = taxInstallmentConfigDobj_prvList.size();
        int currentListSize = taxInstallmentConfigDobjList.size();
        for (TaxInstallmentConfigDobj taxInstallmentConfigDobj : taxInstallmentConfigDobjList) {
            if (i >= prvListSize || i >= currentListSize) {
                break;
            }
            Compare("filerefno", taxInstallmentFileNoDobj_prv.getFilerefno(), taxInstallmentFileNoDobj.getFilerefno(), compBeanList);
            Compare("orderissueby", taxInstallmentFileNoDobj_prv.getOrderissueby(), taxInstallmentFileNoDobj.getOrderissueby(), compBeanList);
            Compare("orderno", taxInstallmentFileNoDobj_prv.getOrderno(), taxInstallmentFileNoDobj.getOrderno(), compBeanList);
            Compare("orderdate", taxInstallmentFileNoDobj_prv.getOrderdate(), taxInstallmentFileNoDobj.getOrderdate(), compBeanList);
            Compare("serialnotable", taxInstallmentConfigDobj_prvList.get(i).getSerialnotable(), taxInstallmentConfigDobjList.get(i).getSerialnotable(), compBeanList);
            Compare("taxamountinstltable", taxInstallmentConfigDobj_prvList.get(i).getTaxamountinstltable(), taxInstallmentConfigDobjList.get(i).getTaxamountinstltable(), compBeanList);
            Compare("dueDateStr", taxInstallmentConfigDobj_prvList.get(i).getDueDateStr(), taxInstallmentConfigDobjList.get(i).getDueDateStr(), compBeanList);
            i++;
        }
        return getCompBeanList();
    }

    @Override
    public String saveAndMoveFile() {
        String return_location = "";
        Exception e = null;
        if (getSumOfTotalTaxInstallment() != 0 && getTotalTaxAmount() != 0) {
            try {
                if (getSumOfTotalTaxInstallment() != getTotalTaxAmount() && !"OR".equalsIgnoreCase(Util.getUserStateCode())) {//add
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("notEqualAmt")));
                    return return_location;
                }
                Status_dobj status = new Status_dobj();
                status.setAppl_dt(getAppl_details().getAppl_dt());
                status.setAppl_no(getAppl_details().getAppl_no());
                status.setPur_cd(getAppl_details().getPur_cd());
                status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
                status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
                status.setStatus(getApp_disapp_dobj().getNew_status());

                status.setCurrent_role(appl_details.getCurrent_action_cd());

                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                        || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)
                        || getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                    status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                    TaxInstallmentConfigImpl taxInstallmentConfig_Impl = new TaxInstallmentConfigImpl();
                    addfixeddataDobjList();
                    taxInstallmentConfig_Impl.update_TAXINSTALLMENTCONFIG_Status(getTaxInstallmentConfigDobj(), taxInstallmentConfigDobj_prv, status, ComparisonBeanImpl.changedDataContents(compareChanges()), getTaxInstallmentConfigDobjList(), taxExemList);

                    if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                        return_location = disapprovalPrint();
                    } else {
                        return_location = "seatwork";
                    }
                }
            } catch (VahanException vme) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), vme.getMessage()));
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleSomthingMsg()));
            }
            return return_location;
        } else {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("noZeroAmt")));
            return return_location;
        }
    }

    public void taxModeListener(AjaxBehaviorEvent event) {
        try {
            TaxFormPanelBean taxSelectedFormBean = (TaxFormPanelBean) event.getComponent().getAttributes().get("taxBeanAttr");
            taxModeListener(taxSelectedFormBean);

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

    }

    public void taxModeListener(TaxFormPanelBean taxSelectedFormBean) {

        try {
            if (taxSelectedFormBean.getTaxMode().equals("0")) {
                taxSelectedFormBean.resetAll();
                updateTotalPayableAmount();
                return;
            }
            if (taxSelectedFormBean.getTaxMode().equalsIgnoreCase("O") || taxSelectedFormBean.getTaxMode().equalsIgnoreCase("L")) {
                taxSelectedFormBean.setNoOfUnits(1);
            }
            VahanTaxParameters taxParameters = fillTaxParametersFromDobj(ownerDobj, getPermitPanelBean().getPermitDobj());
            taxParameters.setTAX_MODE(taxSelectedFormBean.getTaxMode());
            taxParameters.setPUR_CD(taxSelectedFormBean.getPur_cd());
            Date taxDueFrom = TaxServer_Impl.getTaxDueFromDate(ownerDobj, taxSelectedFormBean.getPur_cd());
            taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(taxDueFrom));
            taxParameters.setNEW_VCH("N");
            taxParameters.setTAX_MODE_NO_ADV(taxSelectedFormBean.getNoOfUnits());
            List<DOTaxDetail> listTaxBreakUp = callTaxService(taxParameters);

            taxSelectedFormBean.setTaxDescriptionList(TaxUtils.sortTaxDetails(listTaxBreakUp));
            taxSelectedFormBean.updateTaxBean();
            validateExemptionDtls();
            updateTotalPayableAmount();
        } catch (VahanException ve) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, ve.getMessage(), ve.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void updateTotalPayableAmount() {
        setTotalTaxAmount(0L);
        long exemptionPenalty = 0l;
        long finalTaxAmount = 0;
        List<Integer> listPurCd = new ArrayList<>();
        for (TaxFormPanelBean bean : listTaxForm) {
            finalTaxAmount = finalTaxAmount + bean.getFinalTaxAmount();
            if (bean.getFinalTaxAmount() > 0) {
                listPurCd.add(bean.getPur_cd());
            }
        }
        if (EpayImpl.getServiceChargeType() != null) {
            Long userCharges = EpayImpl.getUserChargesFee(ownerDobj, listPurCd, null);
            setTotalUserChrg(userCharges);
            setRenderUserChargesAmountPanel(true);
        } else {
            setRenderUserChargesAmountPanel(false);
        }
        if (taxExemList != null && !taxExemList.isEmpty()) {
            for (TaxExemptiondobj dobj : taxExemList) {
                exemptionPenalty += dobj.getExemAmount();
            }
            setTotalTaxAmount(getTotalTaxAmount() + finalTaxAmount + getTotalUserChrg() - exemptionPenalty);
        } else {
            setTotalTaxAmount(getTotalTaxAmount() + finalTaxAmount + getTotalUserChrg());
        }

    }

    public void validateExemptionDtls() throws VahanException {
        for (TaxExemptiondobj exemdobj : taxExemList) {
            if (exemdobj.getPur_cd() == TableConstants.FEE_FINE_EXEMTION) {
                if (exemdobj.getExemAmount() > 0) {
                    throw new VahanException(Util.getLocaleMsg("invalidExemp"));
                }
            } else if (exemdobj.getPur_cd() == TableConstants.TAX_PENALTY_EXEMTION) {
                long totalPenaltyAmnt = 0l;
                for (TaxFormPanelBean bean : listTaxForm) {
                    totalPenaltyAmnt = totalPenaltyAmnt + bean.getTotalPaybalePenalty();
                }
                if (totalPenaltyAmnt < exemdobj.getExemAmount()) {
                    throw new VahanException(Util.getLocaleMsg("invalidExemp"));
                }
            } else if (exemdobj.getPur_cd() == TableConstants.TAX_INTEREST_EXEMTION) {
                long totalInterestAmnt = 0l;
                for (TaxFormPanelBean bean : listTaxForm) {
                    totalInterestAmnt = totalInterestAmnt + bean.getTotalPaybaleInterest();
                }
                if (totalInterestAmnt < exemdobj.getExemAmount()) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, Util.getLocaleMsg("alert"), Util.getLocaleMsg("invalidExemp")));
                    return;
                }
            }
        }
    }

//    public List<DOTaxDetail> callTaxService(VahanTaxParameters taxParameters) throws VahanException {
//        List<DOTaxDetail> tempTaxList = null;
//
//        VahanTaxClient taxClient = null;
//        try {
//            taxClient = new VahanTaxClient();
//            String taxServiceResponse = taxClient.getTaxDetails(taxParameters);
//            tempTaxList = taxClient.parseTaxResponse(taxServiceResponse, taxParameters.getPURCD(), taxParameters.getTAXMODE());
//
//        } catch (javax.xml.ws.WebServiceException e) {
//            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
//        }
//        return tempTaxList;
//    }
    public void getBreakUpDetailsByPurpose(TaxFormPanelBean bean) {
        if (taxDescriptionList != null) {
            taxDescriptionList.clear();
        }
        taxDescriptionList = bean.getTaxDescriptionList();
        //RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().ajax().update("taxInstallmentId:sub_view_tax_dtls:opBreakupDetails");
        PrimeFaces.current().executeScript("PF('dia_tax_breakup').show()");
    }

    public void isViewIsRendered() {
        // used for calculating tax based on modes automatically
        autoRunTaxListener = false;
    }

    @Override
    public List<ComparisonBean> getCompBeanList() {
        return this.compBeanList;
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        return this.prevChangedDataList;
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
    }

    @Override
    public void saveEApplication() {
    }

    /**
     * @return the purCodeList
     */
    public Map<String, Integer> getPurCodeList() {
        return purCodeList;
    }

    /**
     * @param purCodeList the purCodeList to set
     */
    public void setPurCodeList(Map<String, Integer> purCodeList) {
        this.purCodeList = purCodeList;
    }

    public TaxInstallmentConfigDobj getTaxInstallmentConfigDobj() {
        return taxInstallmentConfigDobj;
    }

    public void setTaxInstallmentConfigDobj(TaxInstallmentConfigDobj taxInstallmentConfigDobj) {
        this.taxInstallmentConfigDobj = taxInstallmentConfigDobj;
    }

    public List<TaxInstallmentConfigDobj> getTaxInstallmentConfigDobjList() {
        return taxInstallmentConfigDobjList;
    }

    public void setTaxInstallmentConfigDobjList(List<TaxInstallmentConfigDobj> taxInstallmentConfigDobjList) {
        this.taxInstallmentConfigDobjList = taxInstallmentConfigDobjList;
    }

    public String getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    public Owner_dobj getOwnerDobj() {
        return ownerDobj;
    }

    public void setOwnerDobj(Owner_dobj ownerDobj) {
        this.ownerDobj = ownerDobj;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public Long getUserChrg() {
        return userChrg;
    }

    public void setUserChrg(Long userChrg) {
        this.userChrg = userChrg;
    }

    public PermitPanelBean getPermitPanelBean() {
        return permitPanelBean;
    }

    public void setPermitPanelBean(PermitPanelBean permitPanelBean) {
        this.permitPanelBean = permitPanelBean;
    }

    public boolean isRenderPermitPanel() {
        return renderPermitPanel;
    }

    public void setRenderPermitPanel(boolean renderPermitPanel) {
        this.renderPermitPanel = renderPermitPanel;
    }

    public boolean isRenderUserChargesAmountPanel() {
        return renderUserChargesAmountPanel;
    }

    public void setRenderUserChargesAmountPanel(boolean renderUserChargesAmountPanel) {
        this.renderUserChargesAmountPanel = renderUserChargesAmountPanel;
    }

    public List<TaxFormPanelBean> getListTaxForm() {
        return listTaxForm;
    }

    public void setListTaxForm(List<TaxFormPanelBean> listTaxForm) {
        this.listTaxForm = listTaxForm;
    }

    public boolean isTaxInstallmentPanel() {
        return taxInstallmentPanel;
    }

    public void setTaxInstallmentPanel(boolean taxInstallmentPanel) {
        this.taxInstallmentPanel = taxInstallmentPanel;
    }

    public ArrayList getList_vh_class() {
        return list_vh_class;
    }

    public void setList_vh_class(ArrayList list_vh_class) {
        this.list_vh_class = list_vh_class;
    }

    public ArrayList getList_vm_catg() {
        return list_vm_catg;
    }

    public void setList_vm_catg(ArrayList list_vm_catg) {
        this.list_vm_catg = list_vm_catg;
    }

    public ArrayList getList_maker_model() {
        return list_maker_model;
    }

    public void setList_maker_model(ArrayList list_maker_model) {
        this.list_maker_model = list_maker_model;
    }

    public Long getTotalTaxAmount() {
        return totalTaxAmount;
    }

    public void setTotalTaxAmount(Long totalTaxAmount) {
        this.totalTaxAmount = totalTaxAmount;
    }

    public Long getTotalUserChrg() {
        return totalUserChrg;
    }

    public void setTotalUserChrg(Long totalUserChrg) {
        this.totalUserChrg = totalUserChrg;
    }

    public boolean isRenderApplNoGenMessage() {
        return renderApplNoGenMessage;
    }

    public void setRenderApplNoGenMessage(boolean renderApplNoGenMessage) {
        this.renderApplNoGenMessage = renderApplNoGenMessage;
    }

    public String getTaxmode() {
        return taxmode;
    }

    public void setTaxmode(String taxmode) {
        this.taxmode = taxmode;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    public long getSumOfTotalTaxInstallment() {
        return sumOfTotalTaxInstallment;
    }

    public void setSumOfTotalTaxInstallment(long sumOfTotalTaxInstallment) {
        this.sumOfTotalTaxInstallment = sumOfTotalTaxInstallment;
    }
//    public List<TaxInstallmentConfigDobj> getTaxInstallmentConfigDobj_prvList() {
//        return taxInstallmentConfigDobj_prvList;
//    }
//
//    public void setTaxInstallmentConfigDobj_prvList(List<TaxInstallmentConfigDobj> taxInstallmentConfigDobj_prvList) {
//        this.taxInstallmentConfigDobj_prvList = taxInstallmentConfigDobj_prvList;
//    }

    public TaxInstallmentConfigDobj getTaxInstallmentFileNoDobj() {
        return taxInstallmentFileNoDobj;
    }

    public void setTaxInstallmentFileNoDobj(TaxInstallmentConfigDobj taxInstallmentFileNoDobj) {
        this.taxInstallmentFileNoDobj = taxInstallmentFileNoDobj;
    }

    public TaxInstallmentConfigDobj getTaxInstallmentConfigDobjdel() {
        return taxInstallmentConfigDobjdel;
    }

    public void setTaxInstallmentConfigDobjdel(TaxInstallmentConfigDobj taxInstallmentConfigDobjdel) {
        this.taxInstallmentConfigDobjdel = taxInstallmentConfigDobjdel;
    }

    public boolean isAddDisabled() {
        return addDisabled;
    }

    public void setAddDisabled(boolean addDisabled) {
        this.addDisabled = addDisabled;
    }

    public TaxInstallmentConfigDobj getTaxInstallmentFileNoDobj_prv() {
        return taxInstallmentFileNoDobj_prv;
    }

    public void setTaxInstallmentFileNoDobj_prv(TaxInstallmentConfigDobj taxInstallmentFileNoDobj_prv) {
        this.taxInstallmentFileNoDobj_prv = taxInstallmentFileNoDobj_prv;
    }

    public OwnerIdentificationDobj getOwnerIdentificationPrev() {
        return ownerIdentificationPrev;
    }

    public void setOwnerIdentificationPrev(OwnerIdentificationDobj ownerIdentificationPrev) {
        this.ownerIdentificationPrev = ownerIdentificationPrev;
    }

    public OwnerDetailsDobj getOwnerDetail() {
        return ownerDetail;
    }

    public void setOwnerDetail(OwnerDetailsDobj ownerDetail) {
        this.ownerDetail = ownerDetail;
    }

    /**
     * @return the autoRunTaxListener
     */
    public boolean isAutoRunTaxListener() {
        return autoRunTaxListener;
    }

    /**
     * @param autoRunTaxListener the autoRunTaxListener to set
     */
    public void setAutoRunTaxListener(boolean autoRunTaxListener) {
        this.autoRunTaxListener = autoRunTaxListener;
    }

    public List<DOTaxDetail> getTaxDescriptionList() {
        return taxDescriptionList;
    }

    public void setTaxDescriptionList(List<DOTaxDetail> taxDescriptionList) {
        this.taxDescriptionList = taxDescriptionList;
    }

    /**
     * @return the taxExemList
     */
    public List<TaxExemptiondobj> getTaxExemList() {
        return taxExemList;
    }

    /**
     * @param taxExemList the taxExemList to set
     */
    public void setTaxExemList(List<TaxExemptiondobj> taxExemList) {
        this.taxExemList = taxExemList;
    }

    public PassengerPermitDetailDobj getPermitDob() {
        return permitDob;
    }

    public void setPermitDob(PassengerPermitDetailDobj permitDob) {
        this.permitDob = permitDob;
    }
}
