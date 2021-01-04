/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.impl.InsImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

@ManagedBean(name = "ins_bean")
@ViewScoped
public class InsBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(InsBean.class);
    private int compCode;
    private int insType;
    private Date ins_from;
    private Date ins_upto;
    private Date max_dt = DateUtil.parseDate(DateUtil.getCurrentDate());
    private Date min_dt = DateUtil.parseDate(DateUtil.getCurrentDate());
    private String policyNo;
    private List list_comp_cd;
    private List list_ins_type;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private InsDobj ins_dobj_prv;
    private String appl_no;
    private String regn_no;
    private int pur_cd = 0;
    private boolean disable;
    private boolean render_ins_update;
    private boolean eflag = false;
    private boolean blockInsPanel = false;
    private boolean govtVehFlag = false;
    private boolean disableInsType = false;
    private long idv;
    private boolean insUptoDisable = true;
    private boolean addminusInsbtn = false;
    private int selectedYear = 0;
    private boolean iibData = false;
    private String insuranceFromServiceMessage = TableConstants.INSURANCE_FROM_SERVICE_MESSAGE;
    private String insuranceFromVahanMessage = TableConstants.INSURANCE_FROM_VAHAN_MESSAGE;

    public InsBean() {
        try {
            list_comp_cd = new ArrayList();
            list_ins_type = new ArrayList();
            String[][] data = MasterTableFiller.masterTables.VM_ICCODE.getData();
            for (int i = 0; i < data.length; i++) {
                list_comp_cd.add(new SelectItem(data[i][0], data[i][1]));
            }

            data = MasterTableFiller.masterTables.VM_INSTYP.getData();
            for (int i = 0; i < data.length; i++) {
                list_ins_type.add(new SelectItem(data[i][0], data[i][1]));
            }
            min_dt = ServerUtil.dateRange(min_dt, -1, 0, 1);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", ""));
        }
    }

    public void insTypeFiller(int ownCd) {
        list_ins_type.clear();
        String[][] data = MasterTableFiller.masterTables.VM_INSTYP.getData();
        if (ownCd == TableConstants.VEH_TYPE_GOVT || ownCd == TableConstants.VEH_TYPE_STATE_GOVT) {
            for (int i = 0; i < data.length; i++) {
                list_ins_type.add(new SelectItem(data[i][0], data[i][1]));
            }
        } else {
            for (int i = 0; i < data.length; i++) {
                if (!data[i][0].equalsIgnoreCase(TableConstants.INS_TYPE_NA)) {
                    list_ins_type.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
        }
    }

    public void insTypeListener() {
        if (insType != 0 && insType == Integer.parseInt(TableConstants.INS_TYPE_NA)) {
            setCompCode(9999);
            policyNo = "NA";
            ins_from = null;
            ins_upto = null;
            setBlockInsPanel(true);
            govtVehFlag = true;
            insUptoDisable = true;
            setIdv(0);
        } else {
            setBlockInsPanel(false);
            govtVehFlag = false;
            insUptoDisable = false;
            //policyNo = "";
            //setCompCode(-1);
        }
    }

    public boolean validateInsurance(InsDobj ins_dobj_ret, InsDobj ins_dobj_retVA, boolean eApp) throws CloneNotSupportedException {
        if (ins_dobj_retVA != null) {
            if (ins_dobj_retVA.getIns_type() == Integer.parseInt(TableConstants.INS_TYPE_NA)) {
                set_Ins_dobj_to_bean(ins_dobj_retVA);
                setBlockInsPanel(true);
                govtVehFlag = true;
                disableInsType = false;
                return false;
            }
        }

        if (ins_dobj_ret != null) {
            if (ins_dobj_ret.getIns_type() == Integer.parseInt(TableConstants.INS_TYPE_NA)) {
                set_Ins_dobj_to_bean(ins_dobj_ret);
                setBlockInsPanel(true);
                govtVehFlag = true;
                disableInsType = false;
                return false;
            }
        }

        if (ins_dobj_ret != null && ins_dobj_retVA != null) {
            Date ins_upto1 = ins_dobj_ret.getIns_upto();
            Date ins_upto2 = ins_dobj_retVA.getIns_upto();
            Date dt = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.applyPattern("yyyy-MM-dd");
            String today = sdf.format(dt);
            String ins_d;
            String ins_d2;
            int compare = 0;
            int compare1 = 0;
            if (ins_upto1 != null && ins_upto2 != null) {
                ins_d = sdf.format(ins_upto1);
                ins_d2 = sdf.format(ins_upto2);

                compare = ins_d.compareTo(today);
                compare1 = ins_d2.compareTo(today);
            }
            if (compare >= 0 || compare1 >= 0) {
                blockInsPanel = true;
                disableInsType = true;
                eflag = true;
                set_Ins_dobj_to_bean(ins_dobj_retVA);
                ins_dobj_prv = (InsDobj) ins_dobj_retVA.clone();
            } else {
                eflag = false;
                set_Ins_dobj_to_bean(ins_dobj_ret);
                ins_dobj_prv = (InsDobj) ins_dobj_ret.clone();
                JSFUtils.setFacesMessage("Insurance Expired !!!", null, JSFUtils.INFO);
            }

            if (DateUtils.compareDates(ins_dobj_ret.getOp_dt(), ins_dobj_retVA.getOp_dt()) == 2) {
                set_Ins_dobj_to_bean(ins_dobj_ret);
            } else {
                set_Ins_dobj_to_bean(ins_dobj_retVA);
            }

        } else if (ins_dobj_ret != null) {
            Date ins_upto1 = ins_dobj_ret.getIns_upto();
            Date dt = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.applyPattern("yyyy-MM-dd");
            String today = sdf.format(dt);
            int compare = 0;

            if (ins_upto1 != null) {
                String ins_d = sdf.format(ins_upto1);
                compare = ins_d.compareTo(today);
            }
            if (compare >= 0) {
                blockInsPanel = true;
                disableInsType = true;
                eflag = true;
            } else {
                eflag = false;
                JSFUtils.setFacesMessage("Insurance Expired !!!", null, JSFUtils.INFO);
            }
            set_Ins_dobj_to_bean(ins_dobj_ret);
            ins_dobj_prv = (InsDobj) ins_dobj_ret.clone();

        } else if (ins_dobj_retVA != null) {
            Date ins_upto1 = ins_dobj_retVA.getIns_upto();
            Date dt = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.applyPattern("yyyy-MM-dd");
            String today = sdf.format(dt);
            int compare = 0;
            if (ins_upto1 != null) {
                String ins_d = sdf.format(ins_upto1);
                compare = ins_d.compareTo(today);
            }
            if (compare >= 0) {
                blockInsPanel = true;
                disableInsType = true;
                eflag = true;
            } else {
                eflag = false;
                JSFUtils.setFacesMessage("Insurance Expired !!!", null, JSFUtils.INFO);
            }
            set_Ins_dobj_to_bean(ins_dobj_retVA);
            ins_dobj_prv = (InsDobj) ins_dobj_retVA.clone();
        } else {
            eflag = false;
            if (!eApp) {
                JSFUtils.setFacesMessage("Insurance Details are not available !!!", null, JSFUtils.INFO);
            }
        }
        return eflag;
    }

    public boolean validateInsurance(InsDobj dobj) {
        InsImpl impl = new InsImpl();
        boolean insFlag = impl.validateInsurance(dobj);

        if (!insFlag) {
            JSFUtils.setFacesMessage("Insurance Expired !!!", null, JSFUtils.INFO);
        }
        return insFlag;
    }

    public boolean ins_update(OwnerDetailsDobj detailsDobj) throws VahanException, Exception {
        InsDobj ins_dobj_new = new InsDobj();
        ins_dobj_new = set_InsBean_to_dobj();
        boolean flag = false;
        int ownCode = detailsDobj.getOwner_cd();

        if (InsImpl.validateOwnerCodeWithInsType(ownCode, ins_dobj_new.getIns_type())) {
            compBeanList = compareChagnes();
            String stateCd = Util.getUserStateCode();
            int offCd = Util.getSelectedSeat().getOff_cd();
            if (Util.getUserStateCode().equalsIgnoreCase("KL") && Util.getUserStateCode().equalsIgnoreCase(detailsDobj.getState_cd())
                    && Util.getUserSeatOffCode() != detailsDobj.getOff_cd()) {
                offCd = detailsDobj.getOff_cd();
            }
            if (govtVehFlag || !blockInsPanel || !compBeanList.isEmpty()) {
                if (govtVehFlag) {
                    flag = InsImpl.insertUpdateInsurance(appl_no, regn_no, stateCd, offCd, ins_dobj_new);
                } else {
                    if (!compBeanList.isEmpty() && detailsDobj.getBlackListedVehicleDobj() != null
                            && (detailsDobj.getBlackListedVehicleDobj().getComplain_type() == TableConstants.BLTheftCode
                            || detailsDobj.getBlackListedVehicleDobj().getComplain_type() == TableConstants.BLDestroyedAccidentCode)) {
                        flag = InsImpl.insertUpdateInsurance(appl_no, regn_no, stateCd, offCd, ins_dobj_new);
                    } else if (validateInsurance(ins_dobj_new, null, false)) {
                        flag = InsImpl.insertUpdateInsurance(appl_no, regn_no, stateCd, offCd, ins_dobj_new);
                    }
                }
            }
        } else {
            throw new VahanException("Invalid Combination of Ownership Type & Insurance Type.");
        }
        return flag;
    }

    //For theft and totally damaged case
    public boolean ins_update() throws VahanException, Exception {
        InsDobj ins_dobj_new = new InsDobj();
        ins_dobj_new = set_InsBean_to_dobj();
        boolean flag = false;
        compBeanList = compareChagnes();
        if (govtVehFlag || !blockInsPanel || !compBeanList.isEmpty()) {
            if (govtVehFlag) {
                flag = InsImpl.insertUpdateInsurance(appl_no, regn_no, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), ins_dobj_new);
            } else {
                if (validateInsurance(ins_dobj_new, null, false)) {
                    flag = InsImpl.insertUpdateInsurance(appl_no, regn_no, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), ins_dobj_new);
                }
            }
        }
        if (flag) {
            JSFUtils.setFacesMessage("Insurance Updated !!!", null, JSFUtils.INFO);
        }
        return flag;
    }

    //End
    public void set_Ins_dobj_to_bean(InsDobj ins_dobj) {

        if (ins_dobj == null) {
            JSFUtils.setFacesMessage("Insurance Details are not available !!!", null, JSFUtils.INFO);
            return;
        }
        this.compCode = ins_dobj.getComp_cd();
        this.insType = ins_dobj.getIns_type();
        this.ins_from = ins_dobj.getIns_from();
        this.ins_upto = ins_dobj.getIns_upto();
        this.policyNo = ins_dobj.getPolicy_no();
        this.iibData = ins_dobj.isIibData();
        this.setIdv(ins_dobj.getIdv());
        if (ins_dobj.getIns_type() == Integer.parseInt(TableConstants.INS_TYPE_NA)) {
            blockInsPanel = true;
            disableInsType = true;
            insUptoDisable = true;
        }
        if (ins_dobj.getIns_type() == Integer.parseInt(TableConstants.INS_TYPE_NA)) {
            this.govtVehFlag = true;
        }
        try {
            if (ins_from != null && ins_upto != null) {
                selectedYear = DateUtils.getDate1MinusDate2_Months(ins_from, ins_upto) / 12;
            }
            if (ins_from != null && ins_from.before(min_dt)) {
                this.min_dt = ServerUtil.dateRange(ins_from, 0, 0, 0);
            }
        } catch (Exception e) {
        }

    }

    public InsDobj set_InsBean_to_dobj() {

        InsDobj dobj = new InsDobj();
        //setting the value from form_insurance to dobj
        dobj.setIns_from(this.ins_from);
        dobj.setIns_upto(this.ins_upto);
        dobj.setComp_cd(this.compCode);
        dobj.setIns_type(this.insType);
        dobj.setPolicy_no(this.policyNo);
        dobj.setIdv(this.getIdv());
        dobj.setInsPeriodYears(this.getSelectedYear());
        dobj.setIibData(this.iibData);
        return dobj;
    }

//    E-Application Insurance Function
    public void eAppInsValidate() {
        try {
            InsDobj ins_dobj = set_InsBean_to_dobj();
            setEflag(validateInsurance(ins_dobj, null, true));
        } catch (CloneNotSupportedException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void componentEditable(boolean flag) {
        boolean flag1;
        flag1 = !flag;
        setDisable(flag1);
        setGovtVehFlag(flag1);
    }

    public void componentReadOnly(boolean flag) {
        boolean flag1;
        flag1 = !flag;
        setDisable(flag1);
        setGovtVehFlag(flag1);
    }

    public void policy_no_blur_listener() {
        try {
            if (!CommonUtils.isNullOrBlank(getPolicyNo()) && (!getPolicyNo().equalsIgnoreCase("NA"))) {
                String regnNo = InsImpl.checkPolicyNoUniqueness(getPolicyNo(), compCode);
                if (!CommonUtils.isNullOrBlank(regnNo)) {
                    setPolicyNo("");
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Duplicate policy number Against Vehicle " + regnNo + ".", null));
                }
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), null));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, null));
        }
    }

    public void fromDateChangeListener(SelectEvent event) {
        try {
            Date dt = (Date) event.getObject();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            sdf.applyPattern("dd-MMM-yyyy");
            java.util.Calendar cal = new GregorianCalendar();
            cal.setTime(dt);
            cal.add(Calendar.YEAR, 1);
            cal.add(Calendar.DAY_OF_MONTH, -1);
            setIns_upto(null);
            selectedYear = 0;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.setFacesMessage("Date Format Error.", null, JSFUtils.WARN);
        }
    }

    public void uptoDateChangeListener(SelectEvent event) {
        try {
            TmConfigurationDobj tmConfig = Util.getTmConfiguration();
            if (ins_from != null) {
                Date dt = (Date) event.getObject();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                sdf.applyPattern("dd-MMM-yyyy");
                if (dt.compareTo(ins_from) <= 0) {
                    setIns_upto(null);
                    JSFUtils.setFacesMessage("Upto Date Should be greater than From Date", null, JSFUtils.WARN);
                }
                java.util.Calendar cal = new GregorianCalendar();
                cal.setTime(ins_from);
                cal.add(Calendar.YEAR, tmConfig.getInsurance_validity());
                cal.add(Calendar.DAY_OF_MONTH, -1);
                if (dt.compareTo(cal.getTime()) > 0) {
                    setIns_upto(null);
                    JSFUtils.setFacesMessage("Upto Date Cannot be more than " + tmConfig.getInsurance_validity() + " Year.", null, JSFUtils.WARN);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.setFacesMessage("Date Format Error.", null, JSFUtils.WARN);
        }
    }

    public void updateInsuranceUptoDate(AjaxBehaviorEvent event) {
        try {
            int selectYear = (int) ((javax.faces.component.html.HtmlSelectOneMenu) event.getSource()).getValue();
            if (ins_from != null) {
                java.util.Calendar cal = new GregorianCalendar();
                cal.setTime(ins_from);
                cal.add(Calendar.YEAR, selectYear);
                cal.add(Calendar.DAY_OF_MONTH, -1);
                Date insuranceUptoDate = cal.getTime();
                Date dt = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.applyPattern("yyyy-MM-dd");
                String today = sdf.format(dt);
                if (Util.getSelectedSeat() != null && Util.getSelectedSeat().getAction_cd() != 0) {
                    if (insuranceUptoDate != null && (Util.getSelectedSeat().getAction_cd() == TableConstants.TM_ROLE_NEW_APPL || Util.getSelectedSeat().getAction_cd() == TableConstants.TM_ROLE_NEW_APPL_TEMP
                            || Util.getSelectedSeat().getAction_cd() == TableConstants.TM_ROLE_DEALER_NEW_APPL || Util.getSelectedSeat().getAction_cd() == TableConstants.TM_ROLE_DEALER_TEMP_APPL)) {
                        String ins_d = sdf.format(insuranceUptoDate);
                        if (ins_d.compareTo(today) < 0) {
                            selectedYear = 0;
                            setIns_upto(null);
                            JSFUtils.setFacesMessage("Insurance upto date is not valid " + DateUtil.parseDateToString(insuranceUptoDate), null, JSFUtils.INFO);
                            return;
                        }
                    }
                    setIns_upto(insuranceUptoDate);
                }
            } else {
                JSFUtils.setFacesMessage("Please select the Insurance From date.", null, JSFUtils.INFO);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.setFacesMessage("Problem occurred during updating the Insurance Upto date.", null, JSFUtils.ERROR);
        }
    }

    public List<ComparisonBean> compareChagnes() throws VahanException {

        InsDobj dobj = getIns_dobj_prv();  //getting the dobj from workbench

        if (dobj == null) {
            return compBeanList;
        }
        compBeanList.clear();

        Compare("Ins_Type", dobj.getIns_type(), this.insType, (ArrayList) compBeanList);
        Compare("Ins_comp", dobj.getComp_cd(), this.compCode, (ArrayList) compBeanList);
        Compare("Policy_no", dobj.getPolicy_no(), this.policyNo, (ArrayList) compBeanList);
        Compare("Ins_from_dt", dobj.getIns_from(), this.ins_from, (ArrayList) compBeanList);
        Compare("Ins_upto_dt", dobj.getIns_upto(), this.ins_upto, (ArrayList) compBeanList);
        Compare("Ins_declared_value", dobj.getIdv(), this.getIdv(), (ArrayList) compBeanList);
        return compBeanList;

    }

    public List<ComparisonBean> addToComapreChangesList(List<ComparisonBean> compBeanListPrev) throws VahanException {

        List<ComparisonBean> list = compareChagnes();

        if (compBeanListPrev == null) {
            compBeanListPrev = new ArrayList<>();
        }
        if (!list.isEmpty()) {
            compBeanListPrev.addAll(list);
        }

        return compBeanListPrev;
    }

    /**
     * @return the list_comp_cd
     */
    public List getList_comp_cd() {
        return list_comp_cd;
    }

    /**
     * @param list_comp_cd the list_comp_cd to set
     */
    public void setList_comp_cd(List list_comp_cd) {
        this.list_comp_cd = list_comp_cd;
    }

    /**
     * @return the list_ins_type
     */
    public List getList_ins_type() {
        return list_ins_type;
    }

    /**
     * @param list_ins_type the list_ins_type to set
     */
    public void setList_ins_type(List list_ins_type) {
        this.list_ins_type = list_ins_type;
    }

    /**
     * @return the ins_from
     */
    public Date getIns_from() {
        return ins_from;
    }

    /**
     * @param ins_from the ins_from to set
     */
    public void setIns_from(Date ins_from) {
        this.ins_from = ins_from;
    }

    /**
     * @return the ins_upto
     */
    public Date getIns_upto() {
        return ins_upto;
    }

    /**
     * @param ins_upto the ins_upto to set
     */
    public void setIns_upto(Date ins_upto) {
        this.ins_upto = ins_upto;
    }

    /**
     * @return the max_dt
     */
    public Date getMax_dt() {
        return max_dt;
    }

    /**
     * @param max_dt the max_dt to set
     */
    public void setMax_dt(Date max_dt) {
        this.max_dt = max_dt;
    }

    /**
     * @return the compBeanList
     */
    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    /**
     * @param compBeanList the compBeanList to set
     */
    public void setCompBeanList(List<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    /**
     * @return the ins_dobj_prv
     */
    public InsDobj getIns_dobj_prv() {
        return ins_dobj_prv;
    }

    /**
     * @param ins_dobj_prv the ins_dobj_prv to set
     */
    public void setIns_dobj_prv(InsDobj ins_dobj_prv) {
        this.ins_dobj_prv = ins_dobj_prv;
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
     * @return the regn_no
     */
    public String getRegn_no() {
        return regn_no;
    }

    /**
     * @param regn_no the regn_no to set
     */
    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    /**
     * @return the render_ins_update
     */
    public boolean isRender_ins_update() {
        return render_ins_update;
    }

    /**
     * @param render_ins_update the render_ins_update to set
     */
    public void setRender_ins_update(boolean render_ins_update) {
        this.render_ins_update = render_ins_update;
    }

    /**
     * @return the eflag
     */
    public boolean isEflag() {
        return eflag;
    }

    /**
     * @param eflag the eflag to set
     */
    public void setEflag(boolean eflag) {
        this.eflag = eflag;
    }

    /**
     * @return the blockInsPanel
     */
    public boolean isBlockInsPanel() {
        return blockInsPanel;
    }

    /**
     * @param blockInsPanel the blockInsPanel to set
     */
    public void setBlockInsPanel(boolean blockInsPanel) {
        this.blockInsPanel = blockInsPanel;
    }

    /**
     * @return the pur_cd
     */
    public int getPur_cd() {
        return pur_cd;
    }

    /**
     * @param pur_cd the pur_cd to set
     */
    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    /**
     * @return the min_dt
     */
    public Date getMin_dt() {
        return min_dt;
    }

    /**
     * @param min_dt the min_dt to set
     */
    public void setMin_dt(Date min_dt) {
        this.min_dt = min_dt;
    }

    /**
     * @return the disableInsType
     */
    public boolean isDisableInsType() {
        return disableInsType;
    }

    /**
     * @param disableInsType the disableInsType to set
     */
    public void setDisableInsType(boolean disableInsType) {
        this.disableInsType = disableInsType;
    }

    public int getInsType() {
        return insType;
    }

    public void setInsType(int insType) {
        this.insType = insType;
    }

    public int getCompCode() {
        return compCode;
    }

    public void setCompCode(int compCode) {
        this.compCode = compCode;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public boolean isGovtVehFlag() {
        return govtVehFlag;
    }

    public void setGovtVehFlag(boolean govtVehFlag) {
        this.govtVehFlag = govtVehFlag;
    }

    /**
     * @return the idv
     */
    public long getIdv() {
        return idv;
    }

    /**
     * @param idv the idv to set
     */
    public void setIdv(long idv) {
        this.idv = idv;
    }

    /**
     * @return the insUptoDisable
     */
    public boolean isInsUptoDisable() {
        return insUptoDisable;
    }

    /**
     * @param insUptoDisable the insUptoDisable to set
     */
    public void setInsUptoDisable(boolean insUptoDisable) {
        this.insUptoDisable = insUptoDisable;
    }

    /**
     * @return the addminusInsbtn
     */
    public boolean isAddminusInsbtn() {
        return addminusInsbtn;
    }

    /**
     * @param addminusInsbtn the addminusInsbtn to set
     */
    public void setAddminusInsbtn(boolean addminusInsbtn) {
        this.addminusInsbtn = addminusInsbtn;
    }

    /**
     * @return the selectedYear
     */
    public int getSelectedYear() {
        return selectedYear;
    }

    /**
     * @param selectedYear the selectedYear to set
     */
    public void setSelectedYear(int selectedYear) {
        this.selectedYear = selectedYear;
    }

    public boolean isIibData() {
        return iibData;
    }

    public void setIibData(boolean iibData) {
        this.iibData = iibData;
    }

    public String getInsuranceFromServiceMessage() {
        return insuranceFromServiceMessage;
    }

    public void setInsuranceFromServiceMessage(String insuranceFromServiceMessage) {
        this.insuranceFromServiceMessage = insuranceFromServiceMessage;
    }

    public String getInsuranceFromVahanMessage() {
        return insuranceFromVahanMessage;
    }

    public void setInsuranceFromVahanMessage(String insuranceFromVahanMessage) {
        this.insuranceFromVahanMessage = insuranceFromVahanMessage;
    }
}
