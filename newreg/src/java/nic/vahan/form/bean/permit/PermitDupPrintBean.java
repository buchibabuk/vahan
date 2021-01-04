/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.DupDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.permit.PermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.PermitHomeAuthDobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.DupImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.permit.PermitDetailImpl;
import nic.vahan.form.impl.permit.PermitOwnerDetailImpl;
import nic.vahan.form.impl.permit.PermitDupPrintImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.form.impl.permit.PermitHomeAuthImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author tranC095
 */
@ManagedBean(name = "pmtDupPrint")
@ViewScoped
public class PermitDupPrintBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(PermitDupPrintBean.class);
    private String regn_no;
    private String reason;
    private boolean reasonDisable;
    private String fir_no;
    private String header;
    private boolean dup_cert_visible = false;
    private Date fir_dt = null;
    private String police_station;
    private Date op_dt = new Date();
    private String reasoOneMenu;
    private List reasonList = null;
    private Date currDate = null;
    private DupDobj prevDupDobj = null;
    private DupDobj dup_dobj = new DupDobj();
    private List<ComparisonBean> prevChangedDataList;
    private List<ComparisonBean> compBeanList = new ArrayList<ComparisonBean>();
    private String reasonSelect;
    private String masterLayout = "/masterLayoutPage_new.xhtml";
    private int PUR_CD = 0;
    private boolean dupPanel;
    @ManagedProperty(value = "#{pmt_owner_dtls}")
    private PermitOwnerDetailBean ownerBean;
    @ManagedProperty(value = "#{Permit_Dtls_bean}")
    private PermitDetailBean permit_Dtls_bean;
    PermitOwnerDetailDobj owner_dobj = null;
    PermitOwnerDetailImpl ownerImpl = null;
    PermitDetailDobj pmtDobj = null;
    PermitDetailImpl pmtImpl = null;
    public String main_regn_no = "";
    private String app_no_msg;
    private boolean panel_visible = false;
    @ManagedProperty(value = "#{pmtDtlsBean}")
    private PermitCheckDetailsBean pmtCheckDtsl;
    private List arrayInsCmpy = new ArrayList();
    private List arrayInsType = new ArrayList();
// Nitin KUmar 21-01-2016 Begin 
    private OwnerDetailsDobj ownerDetail;
    // Nitin KUmar 21-01-2016 End
    private PermitHomeAuthDobj authDobj = null;
    private List pmtDocList = new ArrayList();
    private String[] pmtDoc;
    private Map<String, String> stateConfigMap = null;
    private SessionVariables sessionVariables = null;
    private String vahanMessages = null;
    private String dmsUrl = "";
    private boolean renderDocUploadTab = false;
    private boolean exempt_echallan = false;

    public PermitDupPrintBean() {
        sessionVariables = new SessionVariables();
        if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
            vahanMessages = "Session time Out";
            return;
        }
        reasonList = new ArrayList();
        reasonList.add(new SelectItem("SELECT", "SELECT"));
        reasonList.add(new SelectItem("LOST", "LOST"));
        reasonList.add(new SelectItem("THEFT", "THEFT"));
        reasonList.add(new SelectItem("TORN", "TORN"));
        reasonList.add(new SelectItem("OTHER", "OTHER"));
        dupPanel = false;
        reasonDisable = true;
        stateConfigMap = CommonPermitPrintImpl.getVmPermitStateConfiguration(Util.getUserStateCode());
        exempt_echallan = Boolean.parseBoolean(stateConfigMap.get("exempt_echallan"));
        try {
            VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
            parameters.setPUR_CD(appl_details.getPur_cd());
            if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("show_uploaded_document"), parameters), "init")) {
                renderDocUploadTab = true;
            } else {
                renderDocUploadTab = false;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    @PostConstruct
    public void init() {
        try {
            if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                    || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
                vahanMessages = "Session time Out.";
                return;
            }
            String role_cd = String.valueOf(appl_details.getCurrent_action_cd());
            if (role_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_DUPL_APPLICATION))
                    && CommonUtils.isNullOrBlank(appl_details.getAppl_no())) {
                panel_visible = false;
                setHeader("Permit Duplicate Print Application");
                owner_dobj = new PermitOwnerDetailDobj();
                owner_dobj.setDisable(true);
                permit_Dtls_bean.permitComponentReadOnly(true);
            } else if (role_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_DUPL_APPLICATION))
                    && !CommonUtils.isNullOrBlank(appl_details.getAppl_no())) {
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                panel_visible = true;
                setHeader("Permit Duplicate Print Application");
                owner_dobj = new PermitOwnerDetailDobj();
                owner_dobj.setDisable(true);
                permit_Dtls_bean.permitComponentReadOnly(true);
                setRegn_no(appl_details.getRegn_no().toUpperCase());
                get_details();
                saveDataInForm();
            } else if (role_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_DUPL_VERIFICATION))) {
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                panel_visible = true;
                setHeader("Permit Duplicate Print VERIFICATION");
                owner_dobj = new PermitOwnerDetailDobj();
                owner_dobj.setDisable(true);
                permit_Dtls_bean.permitComponentReadOnly(true);
                setRegn_no(appl_details.getRegn_no().toUpperCase());
                get_details();
                saveDataInForm();
            } else if (role_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_DUPL_APPROVAL))) {
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                panel_visible = true;
                setHeader("Permit Duplicate Print APPROVAL");
                owner_dobj = new PermitOwnerDetailDobj();
                owner_dobj.setDisable(true);
                permit_Dtls_bean.permitComponentReadOnly(true);
                setRegn_no(appl_details.getRegn_no().toUpperCase());
                get_details();
                saveDataInForm();
            }
        } catch (Exception e) {
        }
    }

    public void saveDataInForm() {
        try {
            DupImpl dup_Impl = new DupImpl();
            dup_dobj = dup_Impl.set_dobj_from_db(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
            if (dup_dobj != null) {
                prevDupDobj = (DupDobj) dup_dobj.clone();
            } else {
                dup_dobj = new DupDobj();
                dup_dobj.setAppl_no(getAppl_details().getAppl_no());
                dup_dobj.setRegn_no(getAppl_details().getRegn_no());
                dup_dobj.setPur_cd(getAppl_details().getPur_cd());
                dup_dobj.setState_cd(Util.getUserStateCode());
                dup_dobj.setOff_cd(Util.getSelectedSeat().getOff_cd());
                setReason("LOST");
            }
            setBeanFromDobj(dup_dobj);
        } catch (SQLException | CloneNotSupportedException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void get_details() {
        try {

            //nitin kumar - 21-01-2015 BEGIN 
            OwnerImpl owner_Impl = new OwnerImpl();
            main_regn_no = CommonPermitPrintImpl.getRegnNoinVtPermit(getRegn_no().toUpperCase(), Util.getUserStateCode());
            List<OwnerDetailsDobj> ownerDetailsDobjList = owner_Impl.getOwnerDetailsList(main_regn_no.toUpperCase().trim(), null);
            ownerDetailsDobjList = owner_Impl.getStateWiseOwnerList(ownerDetailsDobjList, Util.getUserStateCode());
            if (ownerDetailsDobjList != null && ownerDetailsDobjList.size() == 1) {
                ownerDetail = ownerDetailsDobjList.get(0);
            } else if (ownerDetailsDobjList != null && ownerDetailsDobjList.size() >= 2) {
                ownerBean.setDupRegnList(ownerDetailsDobjList);//dupRegnList = ownerDetailsDobjList;
                //PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Can't Inward Application due to Duplicate Registration No Found for Current State"));
                PrimeFaces.current().executeScript("PF('varDupRegNo').show()");
                return;
            }
            if (ownerDetail == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Registration Number Not Found in Currnet Office"));
                return;
            }
            if (ownerDetail != null && !("YA".contains(ownerDetail.getStatus()))) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Vehicle is not in active stage. Please contact with System administrator"));
                return;
            }
            //nitin kumar  - 21-01-2015 - END
            //     main_regn_no = CommonPermitPrintImpl.getRegnNoinVtPermit(getRegn_no().toUpperCase());
            if (!"".equalsIgnoreCase(main_regn_no)) {
                InsDobj insDobj = null;
                ownerImpl = new PermitOwnerDetailImpl();
                //nitin kumar
//                PermitOwnerDetailDobj dobj = ownerImpl.set_Owner_appl_db_to_dobj(main_regn_no);
                PermitOwnerDetailDobj dobj = ownerBean.getOwnerDetailsDobj(ownerDetail);
                //nitin kumar
                pmtCheckDtsl.getAlldetails(main_regn_no, insDobj, dobj.getState_cd(), dobj.getOff_cd());
                if (exempt_echallan) {
                    pmtCheckDtsl.getDtlsDobj().setExemptedChallan(true);
                }
                pmtDobj = PermitDetailImpl.getPermitdetails(CommonPermitPrintImpl.getPmtNoThroughVtPermit(main_regn_no));
                if (pmtDobj.getPmt_type() == Integer.valueOf(TableConstants.AITP)
                        || pmtDobj.getPmt_type() == Integer.valueOf(TableConstants.NATIONAL_PERMIT)) {
                    authDobj = new PermitHomeAuthDobj();
                    PermitHomeAuthImpl authImpl = new PermitHomeAuthImpl();
                    authDobj = authImpl.getSelectInVtPermit(main_regn_no);
                } else {
                    authDobj = null;
                }
                if (dobj != null && pmtDobj != null && dobj.getState_cd().equalsIgnoreCase(Util.getUserStateCode())) {
                    dup_cert_visible = true;
                    ownerBean.setValueinDOBJ(dobj);
                    permit_Dtls_bean.set_Permit_Dtls_dobj_to_bean(pmtDobj);
                    PermitDupPrintImpl dupImpl = new PermitDupPrintImpl();
                    String docListValue = dupImpl.getPmtDocCode(Util.getUserStateCode(), pmtDobj.getPmt_type());
                    if (!CommonUtils.isNullOrBlank(docListValue)) {
                        String[] docListArr = docListValue.split(",");
                        String[][] docList = MasterTableFiller.masterTables.VM_PERMIT_DOCUMENTS.getData();
                        pmtDocList.clear();
                        for (int i = 0; i < docList.length; i++) {
                            for (int j = 0; j < docListArr.length; j++) {
                                if (docList[i][0].equalsIgnoreCase(docListArr[j])) {
                                    pmtDocList.add(new SelectItem(docList[i][0], docList[i][1]));
                                }
                            }
                        }
                    }
                } else {
                    dup_cert_visible = false;
                    JSFUtils.setFacesMessage("Invalid No.", "Please enter correct Number", JSFUtils.ERROR);
                    permit_Dtls_bean.permit_ResetValue();
                    ownerBean.setValueReset();
                    return;
                }
            } else {
                JSFUtils.setFacesMessage("No permit Found For this Vehicle / Permit NO.", "Message", JSFUtils.WARN);
            }
        } catch (VahanException e) {
            dup_cert_visible = false;
            JSFUtils.setFacesMessage("Invalid No.", e.getMessage(), JSFUtils.ERROR);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void save_deatils() {
        InsDobj ins_dobj = null;
        String errorMsg = "";
        boolean restrictionExempted = false;
        try {
            String pendingApplication = ServerUtil.applicationStatusForPermit(getRegn_no(), sessionVariables.getStateCodeSelected());
            if (CommonUtils.isNullOrBlank(pendingApplication)) {
                VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                parameters.setPUR_CD(TableConstants.VM_PMT_DUPLICATE_PUR_CD);
                parameters.setOWNER_CD(ownerBean.getOwner_cd());
                parameters.setPERMIT_EXPIRED(stateConfigMap.get("permit_expire_exempted").toString());
                restrictionExempted = isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("misllenious_restriction_on_permit").toString(), parameters), "save_deatils");
                if (Boolean.parseBoolean(stateConfigMap.get("insaurance_exempted"))) {
                    if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("insaurance_condition"), parameters), "save_details")) {
                        pmtCheckDtsl.getDtlsDobj().setExemptedIns(true);
                    }
                }
                errorMsg = restrictionExempted ? null : CommonPermitPrintImpl.checkPmtValidation(pmtCheckDtsl.getDtlsDobj());
                if (Util.getUserStateCode().equals("KL")) {
                    if (!CommonUtils.isNullOrBlank(errorMsg) && errorMsg.equalsIgnoreCase("Please pay MV Tax first.")) {
                        errorMsg = CommonPermitPrintImpl.checkPmtGracePeriod(pmtCheckDtsl.getDtlsDobj(), pmtDobj.getPmt_type());
                    }
                }
                if (CommonUtils.isNullOrBlank(errorMsg)) {
                    if ((pmtDobj.getPmt_type() == Integer.valueOf(TableConstants.AITP)
                            || pmtDobj.getPmt_type() == Integer.valueOf(TableConstants.NATIONAL_PERMIT))
                            && (authDobj == null)) {
                        throw new VahanException("Authorization details are missing. Please contact the administrator");
                    }
                    if (pmtDoc == null || pmtDoc.length == 0) {
                        throw new VahanException("Please select any Document.");
                    }
                    if (new Date().getTime() < pmtDobj.getValid_upto().getTime() || restrictionExempted) {
                        DupDobj dobj = new DupDobj();
                        dobj = makeDobjFromBean();
                        if (dobj != null) {
                            dobj.setPur_cd(TableConstants.VM_PMT_DUPLICATE_PUR_CD);
                            dobj.setRegn_no(main_regn_no);
                            String appl_no = PermitDupPrintImpl.insertIntoVa_Dup(dobj, ins_dobj, pmtCheckDtsl);
                            printDialogBox(appl_no);
                        } else {
                            printDialogBox("dobj");
                        }
                    } else {
                        printDialogBox("expired");
                    }
                } else {
                    JSFUtils.setFacesMessage(errorMsg, errorMsg, JSFUtils.INFO);
                }
            } else {
                showApplPendingDialogBox(pendingApplication);
            }
        } catch (VahanException e) {
            JSFUtils.setFacesMessage(e.getMessage(), e.getMessage(), JSFUtils.ERROR);
        } catch (Exception e) {
            JSFUtils.setFacesMessage("There is some problem", "There is some problem to save application", JSFUtils.ERROR);
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void showApplPendingDialogBox(String pendingApplication) {
        setApp_no_msg(pendingApplication);
        PrimeFaces.current().ajax().update("app_num_id");
        PrimeFaces.current().executeScript("PF('appNum').show();");
    }

    public void printDialogBox(String app_no) {
        if (("").equalsIgnoreCase(app_no)) {
            setApp_no_msg("Application Number is not genrated");
            PrimeFaces.current().executeScript("PF('appNum').show();");
        } else if (("expired").equalsIgnoreCase(app_no)) {
            setApp_no_msg("Your Permit is Expired");
            PrimeFaces.current().executeScript("PF('appNum').show();");
        } else if (("dobj").equalsIgnoreCase(app_no)) {
            setApp_no_msg("Please select any reason.");
            PrimeFaces.current().executeScript("PF('appNum').show();");
        } else {
            setApp_no_msg("Application Number generated :" + app_no + ". Please note down the Application No for future reference.");
            PrimeFaces.current().executeScript("PF('appNum').show();");
        }
    }

    public void openModifyUploadedDocumentService() {

        try {
            String appl_no = appl_details.getAppl_no();
            dmsUrl = ServerUtil.getVahanPgiUrl(TableConstants.DMS_CON_VER);
            dmsUrl = dmsUrl.replace("ApplNo", appl_no);
            dmsUrl = dmsUrl.replace("ApplStatus", TableConstants.DOCUMENT_MODIFY_STATUS);
            dmsUrl = dmsUrl + TableConstants.SECURITY_KEY;
            PrimeFaces.current().ajax().update("test_opnFrame");
            PrimeFaces.current().executeScript("PF('ifrmDlg').show()");
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }

    public DupDobj makeDobjFromBean() throws VahanException {
        DupDobj dobj = new DupDobj();
        if (CommonUtils.isNullOrBlank(this.reason) || ("").equalsIgnoreCase(this.reason)) {
            dobj = null;
            throw new VahanException("Please select any reason");
        }
        if (validateForm()) {
            if (fir_dt != null
                    && (("THEFT").equalsIgnoreCase(this.reason) || ("LOST").equalsIgnoreCase(this.reason))) {
                dobj.setFir_dt(fir_dt);
            } else {
                dobj.setFir_dt(null);
            }
            if (!CommonUtils.isNullOrBlank(fir_no)
                    && (("THEFT").equalsIgnoreCase(this.reason) || ("LOST").equalsIgnoreCase(this.reason))) {
                dobj.setFir_no(fir_no);
            } else {
                dobj.setFir_no("");
            }
            if (!CommonUtils.isNullOrBlank(police_station)
                    && (("THEFT").equalsIgnoreCase(this.reason) || ("LOST").equalsIgnoreCase(this.reason))) {
                dobj.setPolice_station(police_station);
            } else {
                dobj.setPolice_station("");
            }
            dobj.setReason(reason);
            if (getPmtDoc() != null || getPmtDoc().length > 0) {
                String docId = "";
                for (int i = 0; i < getPmtDoc().length; i++) {
                    docId += pmtDoc[i] + ",";
                }
                if (!CommonUtils.isNullOrBlank(docId)) {
                    dobj.setPmtDoc(docId.substring(0, (docId.length() - 1)));
                }
            }
        }
        return dobj;
    }

    public void setBeanFromDobj(DupDobj dobj) {

        if (CommonUtils.isNullOrBlank(dobj.getReason()) || ("").equalsIgnoreCase(dobj.getReason())) {
            return;
        }

        setReason(dobj.getReason());
        reasonSelect = dobj.getReason();

        if (!("LOST").equalsIgnoreCase(dobj.getReason()) && !("THEFT").equalsIgnoreCase(dobj.getReason())
                && !("TORN").equalsIgnoreCase(dobj.getReason()) && !("OTHER").equalsIgnoreCase(dobj.getReason())) {
            reasonDisable = false;
            reasonSelect = "OTHER";
        } else if (("THEFT").equalsIgnoreCase(dobj.getReason()) || ("LOST").equalsIgnoreCase(dobj.getReason())) {
            setFir_no(dobj.getFir_no());
            this.fir_dt = dobj.getFir_dt();
            setPolice_station(dobj.getPolice_station());
            reasonDisable = true;
            dupPanel = true;
        } else {
            setFir_no("");
            setPolice_station("");
        }
        PermitDupPrintImpl dupImpl = new PermitDupPrintImpl();
        String list = dupImpl.getMultiDocumentList(dobj.getRegn_no().toUpperCase(), dobj.getAppl_no().toUpperCase());
        if (!CommonUtils.isNullOrBlank(list)) {
            setPmtDoc((list).split(","));
        }
    }

    public List<ComparisonBean> addToComapreChangesList(List<ComparisonBean> compBeanListPrev) throws VahanException {
        List<ComparisonBean> list = compareChanges();
        if (compBeanListPrev == null) {
            compBeanListPrev = new ArrayList<ComparisonBean>();
        }
        if (list.size() > 0) {
            compBeanListPrev.addAll(list);
        }
        return compBeanListPrev;
    }

    @Override
    public List<ComparisonBean> compareChanges() {

        DupDobj dobj = getPrevDupDobj();
        if (dobj == null) {
            return compBeanList;
        }
        compBeanList.clear();
        if (("THEFT").equalsIgnoreCase(dobj.getReason().trim())) {
            Compare("Fir Date", dobj.getFir_dt(), this.fir_dt, (ArrayList) compBeanList);
            Compare("Fir No", dobj.getFir_no(), this.getFir_no(), (ArrayList) compBeanList);
            Compare("Police Station", dobj.getPolice_station(), this.getPolice_station(), (ArrayList) compBeanList);
        }
        Compare("Reason", dobj.getReason(), this.getReason(), (ArrayList) compBeanList);
        return getCompBeanList();
    }

    private boolean validateForm() {
        return true;
    }

    public Date getFir_dt() {
        return fir_dt;
    }

    public void setFir_dt(Date fir_dt) {
        this.fir_dt = fir_dt;
    }

    public Date getOp_dt() {
        return op_dt;
    }

    public void setOp_dt(Date op_dt) {
        this.op_dt = op_dt;
    }

    public List getReasonList() {
        return reasonList;
    }

    public void setReasonList(List reasonList) {
        this.reasonList = reasonList;
    }

    public void vehReasonChangeListener(AjaxBehaviorEvent event) {
        if (("THEFT").equalsIgnoreCase(reasonSelect) || ("LOST").equalsIgnoreCase(reasonSelect)) {
            setReason(reasonSelect);
            reasonDisable = true;
            dupPanel = true;
        } else {
            setReason(reasonSelect);
            reasonDisable = true;
            dupPanel = false;
            if (("OTHER").equalsIgnoreCase(reasonSelect)) {
                setReason("");
                reasonDisable = false;
            }
            if (("SELECT").equalsIgnoreCase(reasonSelect)) {
                setReason("");
                reasonDisable = true;
            }
        }
    }

    public Date getCurrDate() {
        currDate = new Date();
        return currDate;
    }

    public void setCurrDate(Date currDate) {
        this.currDate = currDate;
    }

    public DupDobj getPrevDupDobj() {
        return prevDupDobj;
    }

    public void setPrevDupDobj(DupDobj prevDupDobj) {
        this.prevDupDobj = prevDupDobj;
    }

    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    public void setCompBeanList(List<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    public String getReasonSelect() {
        return reasonSelect;
    }

    public void setReasonSelect(String reasonSelect) {
        this.reasonSelect = reasonSelect;
    }

    @Override
    public String save() {
        String return_location = "";
        try {
            List<ComparisonBean> compareChanges = compareChanges();
            if (!compareChanges.isEmpty() || getPrevDupDobj() == null) {
                dup_dobj = makeDobjFromBean();
                dup_dobj.setAppl_no(appl_details.getAppl_no().toUpperCase());
                dup_dobj.setRegn_no(appl_details.getRegn_no().toUpperCase());
                dup_dobj.setState_cd(Util.getUserStateCode());
                dup_dobj.setOff_cd(Util.getSelectedSeat().getOff_cd());
                DupImpl.saveChangeDup(dup_dobj, ComparisonBeanImpl.changedDataContents(compareChanges));
                return_location = "seatwork";
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Internal Error in Duplicated Certificate Process", "Internal Error"));
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return return_location;
    }

    @Override
    public String saveAndMoveFile() {
        String return_location = "";
        try {
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
                    || getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                DupImpl frc_impl = new DupImpl();
                dup_dobj = makeDobjFromBean();
                dup_dobj.setAppl_no(appl_details.getAppl_no().toUpperCase());
                dup_dobj.setRegn_no(appl_details.getRegn_no().toUpperCase());
                dup_dobj.setPur_cd(appl_details.getPur_cd());
                dup_dobj.setState_cd(Util.getUserStateCode());
                dup_dobj.setOff_cd(Util.getSelectedSeat().getOff_cd());
                if (Util.getUserStateCode().equals("KL")) {
                    String errorMsg = CommonPermitPrintImpl.checkPmtGracePeriod(pmtCheckDtsl.getDtlsDobj(), pmtDobj.getPmt_type());
                    if (!CommonUtils.isNullOrBlank(errorMsg)) {
                        throw new VahanException(errorMsg);
                    }
                }
                frc_impl.update_DupCert_Status(status.getCurrent_role(), dup_dobj, prevDupDobj, status, ComparisonBeanImpl.changedDataContents(compareChanges()));
                if (status.getStatus().equalsIgnoreCase(TableConstants.STATUS_DISPATCH_PENDING)) {
                    return_location = disapprovalPrint();
                } else {
                    return_location = "seatwork";
                }
            }
        } catch (VahanException vme) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vme.getMessage(), vme.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
    }

    public String getMasterLayout() {
        return masterLayout;
    }

    public void setMasterLayout(String masterLayout) {
        this.masterLayout = masterLayout;
    }

    public DupDobj getDup_dobj() {
        return dup_dobj;
    }

    public void setDup_dobj(DupDobj dup_dobj) {
        this.dup_dobj = dup_dobj;
    }

    public int getPUR_CD() {
        return PUR_CD;
    }

    public void setPUR_CD(int PUR_CD) {
        this.PUR_CD = PUR_CD;
    }

    public PermitOwnerDetailBean getOwnerBean() {
        return ownerBean;
    }

    public void setOwnerBean(PermitOwnerDetailBean ownerBean) {
        this.ownerBean = ownerBean;
    }

    public PermitDetailBean getPermit_Dtls_bean() {
        return permit_Dtls_bean;
    }

    public void setPermit_Dtls_bean(PermitDetailBean permit_Dtls_bean) {
        this.permit_Dtls_bean = permit_Dtls_bean;
    }

    public PermitOwnerDetailDobj getOwner_dobj() {
        return owner_dobj;
    }

    public void setOwner_dobj(PermitOwnerDetailDobj owner_dobj) {
        this.owner_dobj = owner_dobj;
    }

    public PermitOwnerDetailImpl getOwnerImpl() {
        return ownerImpl;
    }

    public void setOwnerImpl(PermitOwnerDetailImpl ownerImpl) {
        this.ownerImpl = ownerImpl;
    }

    public PermitDetailDobj getPmtDobj() {
        return pmtDobj;
    }

    public void setPmtDobj(PermitDetailDobj pmtDobj) {
        this.pmtDobj = pmtDobj;
    }

    public PermitDetailImpl getPmtImpl() {
        return pmtImpl;
    }

    public void setPmtImpl(PermitDetailImpl pmtImpl) {
        this.pmtImpl = pmtImpl;
    }

    public boolean isDup_cert_visible() {
        return dup_cert_visible;
    }

    public void setDup_cert_visible(boolean dup_cert_visible) {
        this.dup_cert_visible = dup_cert_visible;
    }

    public String getApp_no_msg() {
        return app_no_msg;
    }

    public void setApp_no_msg(String app_no_msg) {
        this.app_no_msg = app_no_msg;
    }

    public boolean isPanel_visible() {
        return panel_visible;
    }

    public void setPanel_visible(boolean panel_visible) {
        this.panel_visible = panel_visible;
    }

    public PermitCheckDetailsBean getPmtCheckDtsl() {
        return pmtCheckDtsl;
    }

    public void setPmtCheckDtsl(PermitCheckDetailsBean pmtCheckDtsl) {
        this.pmtCheckDtsl = pmtCheckDtsl;
    }

    public List getArrayInsCmpy() {
        return arrayInsCmpy;
    }

    public void setArrayInsCmpy(List arrayInsCmpy) {
        this.arrayInsCmpy = arrayInsCmpy;
    }

    public List getArrayInsType() {
        return arrayInsType;
    }

    public void setArrayInsType(List arrayInsType) {
        this.arrayInsType = arrayInsType;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getFir_no() {
        return fir_no;
    }

    public void setFir_no(String fir_no) {
        this.fir_no = fir_no;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getPolice_station() {
        return police_station;
    }

    public void setPolice_station(String police_station) {
        this.police_station = police_station;
    }

    public String getReasoOneMenu() {
        return reasoOneMenu;
    }

    public void setReasoOneMenu(String reasoOneMenu) {
        this.reasoOneMenu = reasoOneMenu;
    }

    public boolean isDupPanel() {
        return dupPanel;
    }

    public void setDupPanel(boolean dupPanel) {
        this.dupPanel = dupPanel;
    }

    public String getMain_regn_no() {
        return main_regn_no;
    }

    public void setMain_regn_no(String main_regn_no) {
        this.main_regn_no = main_regn_no;
    }

    public boolean isReasonDisable() {
        return reasonDisable;
    }

    public void setReasonDisable(boolean reasonDisable) {
        this.reasonDisable = reasonDisable;
    }

    public PermitHomeAuthDobj getAuthDobj() {
        return authDobj;
    }

    public void setAuthDobj(PermitHomeAuthDobj authDobj) {
        this.authDobj = authDobj;
    }

    public List getPmtDocList() {
        return pmtDocList;
    }

    public void setPmtDocList(List pmtDocList) {
        this.pmtDocList = pmtDocList;
    }

    public String[] getPmtDoc() {
        return pmtDoc;
    }

    public void setPmtDoc(String[] pmtDoc) {
        this.pmtDoc = pmtDoc;
    }

    public String getVahanMessages() {
        return vahanMessages;
    }

    public void setVahanMessages(String vahanMessages) {
        this.vahanMessages = vahanMessages;
    }

    public String getDmsUrl() {
        return dmsUrl;
    }

    public void setDmsUrl(String dmsUrl) {
        this.dmsUrl = dmsUrl;
    }

    public boolean isRenderDocUploadTab() {
        return renderDocUploadTab;
    }

    public void setRenderDocUploadTab(boolean renderDocUploadTab) {
        this.renderDocUploadTab = renderDocUploadTab;
    }

    public boolean isExempt_echallan() {
        return exempt_echallan;
    }

    public void setExempt_echallan(boolean exempt_echallan) {
        this.exempt_echallan = exempt_echallan;
    }
}
