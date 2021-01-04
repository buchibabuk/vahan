/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
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
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.DupDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.CounterSignatureDobj;
import nic.vahan.form.dobj.permit.DupCounterSignatureDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.DupImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.form.impl.permit.CounterSignatureImpl;
import nic.vahan.form.impl.permit.PassengerPermitDetailImpl;
import nic.vahan.form.impl.permit.PermitDupPrintImpl;
import nic.vahan.form.impl.permit.PermitOwnerDetailImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.primefaces.PrimeFaces;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

/**
 *
 * @author hcl
 */

@ManagedBean(name = "dupConSign")
@ViewScoped
public class DupCounterSignatureBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {
    private static final Logger LOGGER = Logger.getLogger(CounterSignatureBean.class);
    private DupCounterSignatureDobj countDobj = new DupCounterSignatureDobj();
    private PermitOwnerDetailDobj ownerDobjPrv;
    private CounterSignatureDobj countDobjPrv;
    private List stateList = new ArrayList<>();
    private List offList = new ArrayList<>();
    private List areaTypeList = new ArrayList<>();
    private List pmtTypeList = new ArrayList<>();
    private List periodModeList = new ArrayList<>();
    private boolean regnNoDisable = false;
    @ManagedProperty(value = "#{pmt_owner_dtls}")
    private PermitOwnerDetailBean ownerBean;
    PermitOwnerDetailDobj owner_dobj = null;
    private String app_no_msg;
    private String header;
    private boolean panelHideShow = true;
    private boolean counterSignatureDtls = false;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private List<ComparisonBean> prevChangedDataList;
    private Date minDate = DateUtil.parseDate(DateUtil.getCurrentDate());
    private Date maxDate = null;
    private String selectOneRadio = "1";
    PassengerPermitDetailImpl impl = new PassengerPermitDetailImpl();
    private boolean renderArea;
    private Map<String, String> stateConfigMap = null;
    private String regn_state;
    private List purposeList = new ArrayList<>();
    private List<PermitRouteList> actionSource = new ArrayList<>();
    private List<PermitRouteList> actionTarget = new ArrayList<>();
    private DualListModel<PermitRouteList> routeManage;
    private String via_route = "";
    private int routeLength = 0;
    private boolean visibleRouteDtls = true;
    private boolean disableRoutDtls = false;
    private boolean noOfTripsrendered = false;
    private int no_of_trip = 0;
    private List reasonList = null;
    private boolean reasonDisable;
    private boolean dupPanel;
    private List pmtDocList = new ArrayList();
    private boolean dup_cert_visible = false;
    private Date currDate = null;
    private DupDobj prevDupDobj = null;
    private DupDobj dup_dobj = new DupDobj();

    public DupCounterSignatureBean() {
        //RequestContext context = RequestContext.getCurrentInstance();
        Map<String, String> routeMap = impl.getSourcesAreaMapWithStrBuil("0", Util.getUserStateCode(), Util.getUserOffCode());
        areaTypeList.clear();
        areaTypeList.add(new SelectItem("0", "SELECT ANY AREA/REAGION"));
        for (Map.Entry<String, String> entry : routeMap.entrySet()) {
            areaTypeList.add(new SelectItem(entry.getKey(), entry.getValue()));
        }
        String[][] data = MasterTableFiller.masterTables.TM_STATE.getData();
        for (int i = 0; i < data.length; i++) {
            stateList.add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
        for (int i = 0; i < data.length; i++) {
            pmtTypeList.add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.VM_TAX_MODE.getData();
        periodModeList.add(new SelectItem("-1", "--- Select Period Mode ---"));
        for (int i = 0; i < data.length; i++) {
            if (("M").equalsIgnoreCase(data[i][0]) || ("Y").equalsIgnoreCase(data[i][0])) {
                periodModeList.add(new SelectItem(data[i][0], data[i][1]));
            }
        }

        stateConfigMap = CommonPermitPrintImpl.getVmPermitStateConfiguration(Util.getUserStateCode());

        setVisibleRouteDtls(Boolean.parseBoolean(stateConfigMap.get("show_route_details_cs")));
        if (isVisibleRouteDtls()) {
            routeManage = new DualListModel<>(actionSource, actionTarget);
        }
        if (CommonUtils.isNullOrBlank(getAppl_details().getAppl_no()) && isVisibleRouteDtls()) {
            routeCodeDetail("NULL", TableList.vt_permit_route, Util.getUserStateCode(), Util.getUserOffCode());
            //  PrimeFaces.current().ajax().update("via Length trips");
        }

        reasonList = new ArrayList();
        reasonList.add(new SelectItem("SELECT", "SELECT"));
        reasonList.add(new SelectItem("LOST", "LOST"));
        reasonList.add(new SelectItem("THEFT", "THEFT"));
        reasonList.add(new SelectItem("TORN", "TORN"));
        reasonList.add(new SelectItem("OTHER", "OTHER"));

    }

    @PostConstruct
    public void init() {
        try {
            if (getAppl_details().getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_DUPCOUNTER_SIGNATURE_ENTRY
                    && !CommonUtils.isNullOrBlank(getAppl_details().getAppl_no())) {
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                header = "DUPLICATE COUNTER SIGNATURE RE-ENTRY";
                ownerBean.getPmt_dobj().setFlage(false);
                ownerBean.setValueinDOBJ(owner_dobj);
                counterSignatureDtls = false;
                setVisibleRouteDtls(Boolean.parseBoolean(stateConfigMap.get("show_route_details_cs")));
                setRenderArea(Boolean.parseBoolean(stateConfigMap.get("show_region_on_cs_permit")));

            } else if (getAppl_details().getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_DUPCOUNTER_SIGNATURE_ENTRY
                    && CommonUtils.isNullOrBlank(getAppl_details().getAppl_no())) {
                header = "DUPLICATE COUNTER SIGNATURE APPLICATION ENTRY";
                counterSignatureDtls = true;
                setVisibleRouteDtls(Boolean.parseBoolean(stateConfigMap.get("show_route_details_cs")));
                setRenderArea(Boolean.parseBoolean(stateConfigMap.get("show_region_on_cs_permit")));
            } else if (getAppl_details().getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_DUPCOUNTER_SIGNATURE_VERIFICATION) {
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                header = "DUPLICATE COUNTER SIGNATURE VERIFICATION";
                setPanelHideShow(true);
                counterSignatureDtls = true;
                // countDobj.setRegn_no(appl_details.getRegn_no().toUpperCase());
                fillDtlsVeriApproStage();
                saveDataInForm();
                setVisibleRouteDtls(Boolean.parseBoolean(stateConfigMap.get("show_route_details_cs")));
                setRenderArea(Boolean.parseBoolean(stateConfigMap.get("show_region_on_cs_permit")));
            } else if (getAppl_details().getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_DUPCOUNTER_SIGNATURE_APPROVAL) {
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                header = "DUPLICATE COUNTER SIGNATURE APPROVAL";
                counterSignatureDtls = true;
                setPanelHideShow(true);
                fillDtlsVeriApproStage();
                saveDataInForm();
                setVisibleRouteDtls(Boolean.parseBoolean(stateConfigMap.get("show_route_details_cs")));
                setRenderArea(Boolean.parseBoolean(stateConfigMap.get("show_region_on_cs_permit")));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public final void fillDtlsVeriApproStage() {
        // RequestContext context = RequestContext.getCurrentInstance();
        try {
            panelHideShow = false;
            PermitOwnerDetailImpl ownImpl = new PermitOwnerDetailImpl();
            owner_dobj = ownImpl.ownerDeatilsCounterSigFromVt(appl_details.getRegn_no().toUpperCase());
            if (owner_dobj == null) {
                JSFUtils.showMessagesInDialog("Error", "Owner details not found", FacesMessage.SEVERITY_ERROR);
            } else {
                ownerBean.setValueinDOBJ(owner_dobj);
                ownerDobjPrv = new PermitOwnerDetailDobj();
                ownerDobjPrv = (PermitOwnerDetailDobj) owner_dobj.clone();
            }

            CounterSignatureImpl counImpl = new CounterSignatureImpl();
            DupCounterSignatureDobj counDobj = counImpl.getCounterSignatureDetailFromVt(appl_details.getRegn_no().toUpperCase(), Util.getUserStateCode());
            setNo_of_trip(counDobj.getNo_of_trips());
            stateConfigMap = CommonPermitPrintImpl.getVmPermitStateConfiguration(Util.getUserStateCode());
            setVisibleRouteDtls(Boolean.parseBoolean(stateConfigMap.get("show_route_details_cs")));
            if (isVisibleRouteDtls()) {
                routeCodeDetail(counDobj.getAppl_no(), TableList.vt_permit_route, Util.getUserStateCode(), Util.getUserOffCode());
                //      PrimeFaces.current().ajax().update("via Length trips");
            }
            if (counDobj == null) {
                JSFUtils.showMessagesInDialog("Error", "Counter Signature details not found", FacesMessage.SEVERITY_ERROR);
            } else {
                if (CommonUtils.isNullOrBlank(counDobj.getMaker_name())) {
                    counDobj.setMaker_name(null);
                }
                if (CommonUtils.isNullOrBlank(counDobj.getModel_name())) {
                    counDobj.setModel_name(null);
                }
                setCountDobj(counDobj);
                PassengerPermitDetailImpl passImp = new PassengerPermitDetailImpl();
                CounterSignatureImpl impl = new CounterSignatureImpl();
                String docListValue = impl.getCountrSignatureDocCode(Util.getUserStateCode());
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

                setDup_cert_visible(true);

                onSelectState();
                counDobj.setOff_cd_from(counDobj.getOff_cd_from());
                //   countDobjPrv = new CounterSignatureDobj();
                //   countDobjPrv = (CounterSignatureDobj) counDobj.clone();
            }
            countDobj.setAppl_no(appl_details.getAppl_no());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void get_details() {
        // RequestContext context = RequestContext.getCurrentInstance();
        CounterSignatureImpl impl = new CounterSignatureImpl();
        try {
            String regn_No = "";
            if (!CommonUtils.isNullOrBlank(countDobj.getRegn_no()) && ("1").equalsIgnoreCase(getSelectOneRadio())) {
                try {
                    countDobj = impl.getCounterSignatureDetailFromVt(countDobj.getRegn_no().trim().toUpperCase(), null);
                } catch (VahanException e) {
                    JSFUtils.showMessagesInDialog("Information", e.getMessage(), FacesMessage.SEVERITY_INFO);
                }
            } else if (!CommonUtils.isNullOrBlank(countDobj.getCountSignNo()) && ("2").equalsIgnoreCase(getSelectOneRadio())) {
                try {
                    countDobj = impl.getCounterSignatureDetailFromVt(countDobj.getCountSignNo().trim().toUpperCase(), null);

                } catch (VahanException e) {
                    JSFUtils.showMessagesInDialog("Information", e.getMessage(), FacesMessage.SEVERITY_INFO);
                }
            }
            String[][] data = MasterTableFiller.masterTables.TM_OFFICE.getData();
            offList.clear();
            for (int i = 0; i < data.length; i++) {
                if (countDobj.getState_cd_from().equalsIgnoreCase(data[i][13])) {
                    offList.add(new SelectItem(data[i][0], data[i][1]));
                }
            }

            regnNoDisable = true;
            counterSignatureDtls = true;
            setRenderArea(Boolean.parseBoolean(stateConfigMap.get("show_region_on_cs_permit")));
            setVisibleRouteDtls(Boolean.parseBoolean(stateConfigMap.get("show_route_details_cs")));
            if (isVisibleRouteDtls()) {
                routeCodeDetail(countDobj.getAppl_no(), TableList.vt_permit_route, Util.getUserStateCode(), Util.getUserOffCode());
                setNo_of_trip(countDobj.getNo_of_trips());
                //      PrimeFaces.current().ajax().update("route_details pickList via Length trips");
            }

            if (CommonUtils.isNullOrBlank(countDobj.getRegn_no()) && ("1").equalsIgnoreCase(getSelectOneRadio())) {
                JSFUtils.showMessagesInDialog("Error", "Please enter registration no.", FacesMessage.SEVERITY_ERROR);
            } else if (CommonUtils.isNullOrBlank(countDobj.getCountSignNo()) && ("2").equalsIgnoreCase(getSelectOneRadio())) {
                JSFUtils.showMessagesInDialog("Error", "Please enter permit no.", FacesMessage.SEVERITY_ERROR);
            } else {
                if (!CommonUtils.isNullOrBlank(countDobj.getRegn_no())) {
                    String str = ServerUtil.applicationStatusForPermit(countDobj.getRegn_no().trim().toUpperCase(), Util.getUserStateCode());
                    if (!CommonUtils.isNullOrBlank(str)) {
                        showApplPendingDialogBox(str);
                        return;
                    }
                }
                PermitOwnerDetailImpl ownImpl = new PermitOwnerDetailImpl();
                PassengerPermitDetailImpl passImp = new PassengerPermitDetailImpl();
                PermitOwnerDetailDobj ownDobj = ownImpl.set_Owner_appl_db_to_dobj(countDobj.getRegn_no(), countDobj.getState_cd_from());

                if (ownDobj != null) {
                    ownerBean.setValueinDOBJ(ownDobj);
                    countDobj.setMaker_name(ownDobj.getMakerName());
                    countDobj.setModel_name(ownDobj.getModelName());
                } else {
                    ownerBean.getPmt_dobj().setFlage(false);
                    countDobj.setMaker_name(null);
                    countDobj.setModel_name(null);
                }
                if (!CommonUtils.isNullOrBlank(countDobj.getCountSignNo())) {
                    setDup_cert_visible(true);
                    this.dup_cert_visible = true;
                }
                String docListValue = impl.getCountrSignatureDocCode(Util.getUserStateCode());
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

            }
        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Error", e.getMessage(), FacesMessage.SEVERITY_ERROR);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void save_details() {
        CounterSignatureImpl impl = new CounterSignatureImpl();
        try {
            if (this.countDobj.getReason() == null || this.countDobj.getReason().equals("")) {
                throw new VahanException("Please select Reason.");
            }
            if (this.countDobj.getPmtDoc() == null || this.countDobj.getPmtDoc().length == 0) {
                throw new VahanException("Please select any Document.");
            }
            if (new Date().getTime() < this.countDobj.getValid_upto().getTime()) {
                DupDobj dobj = new DupDobj();
                dobj = makeDobjFromBean();
                if (dobj != null) {
                    dobj.setPur_cd(TableConstants.VM_PMT_DUP_COUNTER_SIGNATURE_PUR_CD);
                    dobj.setRegn_no(this.countDobj.getRegn_no());
                    String appl_no = impl.insertIntoVa_Dup(dobj);
                    printDialogBox(appl_no);
                } else {
                    printDialogBox("dobj");
                }
            } else {
                printDialogBox("expired");
            }
        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Exception", e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void checkField(PermitOwnerDetailDobj ownDobj, CounterSignatureDobj countDobj) throws VahanException {
        try {
            if (CommonUtils.isNullOrBlank(countDobj.getState_cd_from()) || countDobj.getState_cd_from().equalsIgnoreCase("-1")) {
                throw new VahanException("Please select state from");
            } else if (countDobj.getOff_cd_from() == -1 || countDobj.getOff_cd_from() == 0) {
                throw new VahanException("Please select office from");
            } else if (CommonUtils.isNullOrBlank(countDobj.getPmt_no())) {
                throw new VahanException("Please enter permit no.");
            } else if (countDobj.getPmt_type() == -1 || countDobj.getPmt_type() == 0) {
                throw new VahanException("Please enter permit type");
            } else if (countDobj.getPeriod_mode().equalsIgnoreCase("-1") || CommonUtils.isNullOrBlank(countDobj.getPeriod_mode())) {
                throw new VahanException("Please Select Period Mode");
            } else if (countDobj.getPeriod() <= 0) {
                throw new VahanException("Please enter period");
            } else if (countDobj.getValid_from() == null) {
                throw new VahanException("Please enter permit valid from");
            } else if (countDobj.getValid_upto() == null) {
                throw new VahanException("Please enter permit valid upto");
            } else if (CommonUtils.isNullOrBlank(ownDobj.getOwner_name())) {
                throw new VahanException("Please enter owner name");
            } else if (ownDobj.getVh_class() == -1) {
                throw new VahanException("Please enter vehicle class");
            } else if (CommonUtils.isNullOrBlank(ownDobj.getVch_catg())) {
                throw new VahanException("Please enter vehicle category");
            } else if (CommonUtils.isNullOrBlank(ownDobj.getF_name())) {
                throw new VahanException("Please enter father name");
            } else if (ownDobj.getC_district() == -1) {
                throw new VahanException("Please enter permanent district");
            } else if (ownDobj.getP_district() == -1) {
                throw new VahanException("Please enter permanent district");
            } else if (countDobj.getPur_cd() == -1) {
                throw new VahanException("Please select purpose of counter signature");
            } else if (Util.getUserStateCode().equals("UK") && countDobj.getRegion() == 0) {
                throw new VahanException("Please Select Area ");
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

//    public void printDialogBox(String app_no) {
//        if (!CommonUtils.isNullOrBlank(app_no)) {
//            setApp_no_msg("Application Number generated :" + app_no + ". Please note down the Application No for future reference.");
//            PrimeFaces.current().executeScript("PF('appNum').show();");
//        } else {
//            setApp_no_msg("There are some problem to genrate the application");
//            PrimeFaces.current().executeScript("PF('appNum').show();");
//        }
//    }
    public void showApplPendingDialogBox(String str) {
        setApp_no_msg(str);
        PrimeFaces.current().ajax().update("app_num_id");
        PrimeFaces.current().executeScript("PF('appNum').show();");
    }

    public void onSelectState() {
        String[][] data = MasterTableFiller.masterTables.TM_OFFICE.getData();
        offList.clear();
        for (int i = 0; i < data.length; i++) {
            if (countDobj.getState_cd_from().equalsIgnoreCase(data[i][13])) {
                offList.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
    }

    public void changePeriodMode() {
        countDobj.setPeriod(0);
        countDobj.setCount_valid_upto(null);
    }

    public void blurActionPeriod() {
        countDobj.setCount_valid_upto(null);
        Calendar cal = Calendar.getInstance();
        if (("Y").equalsIgnoreCase(countDobj.getPeriod_mode())) {
            cal.add(Calendar.YEAR, countDobj.getPeriod());
            cal.add(Calendar.DAY_OF_YEAR, -1);
        } else if (("M").equalsIgnoreCase(countDobj.getPeriod_mode())) {
            cal.add(Calendar.MONTH, countDobj.getPeriod());
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        maxDate = cal.getTime();
    }

    @Override
    public String save() {
        String return_location = "seatwork";
        try {
            List<ComparisonBean> compareChanges = compareChanges();
            if (this.countDobj.getReason() == null || this.countDobj.getReason().equals("")) {
                throw new VahanException("Please select Reason.");
            }
            if (this.countDobj.getPmtDoc() == null || this.countDobj.getPmtDoc().length == 0) {
                throw new VahanException("Please select any Document.");
            }
            if (!compareChanges.isEmpty() || getPrevDupDobj() == null) {
                dup_dobj = makeDobjFromBean();
                dup_dobj.setAppl_no(appl_details.getAppl_no().toUpperCase());
                dup_dobj.setRegn_no(appl_details.getRegn_no().toUpperCase());
                dup_dobj.setState_cd(Util.getUserStateCode());
                dup_dobj.setOff_cd(Util.getSelectedSeat().getOff_cd());
                DupImpl.saveChangeDup(dup_dobj, ComparisonBeanImpl.changedDataContents(compareChanges));
                return_location = "seatwork";
            }
        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Exception", e.getMessage(), FacesMessage.SEVERITY_ERROR);
            return "";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Internal Error in Duplicated Certificate Process", "Internal Error"));
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return return_location;
    }

    @Override
    public List<ComparisonBean> compareChanges() {

        DupDobj dobj = getPrevDupDobj();
        if (dobj == null) {
            return compBeanList;
        }
        compBeanList.clear();
        if (("THEFT").equalsIgnoreCase(dobj.getReason().trim())) {
            Compare("Fir Date", dobj.getFir_dt(), this.countDobj.getFir_dt(), (ArrayList) compBeanList);
            Compare("Fir No", dobj.getFir_no(), this.countDobj.getFir_no(), (ArrayList) compBeanList);
            Compare("Police Station", dobj.getPolice_station(), this.countDobj.getPolice_station(), (ArrayList) compBeanList);
        }
        Compare("Reason", dobj.getReason(), this.countDobj.getReason(), (ArrayList) compBeanList);
        return getCompBeanList();
    }

    @Override
    public String saveAndMoveFile() {
        String return_location = "";
        CounterSignatureImpl impl = new CounterSignatureImpl();
        try {
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setPur_cd(appl_details.getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            int currentRole = appl_details.getCurrent_role() % 10;
            status.setCurrent_role(currentRole);
            if (this.countDobj.getReason() == null || this.countDobj.getReason().equals("")) {
                throw new VahanException("Please select Reason.");
            }
            if (this.countDobj.getPmtDoc() == null || this.countDobj.getPmtDoc().length == 0) {
                throw new VahanException("Please select any Document.");
            }
            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)) {
                if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_DUPCOUNTER_SIGNATURE_VERIFICATION
                        || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_DUPCOUNTER_SIGNATURE_APPROVAL) {
                    DupImpl frc_impl = new DupImpl();
                    dup_dobj = makeDobjFromBean();
                    dup_dobj.setAppl_no(appl_details.getAppl_no().toUpperCase());
                    dup_dobj.setRegn_no(appl_details.getRegn_no().toUpperCase());
                    dup_dobj.setPur_cd(appl_details.getPur_cd());
                    dup_dobj.setState_cd(Util.getUserStateCode());
                    dup_dobj.setOff_cd(Util.getSelectedSeat().getOff_cd());
                    frc_impl.update_DupCert_Status(status.getCurrent_role(), dup_dobj, prevDupDobj, status, ComparisonBeanImpl.changedDataContents(compareChanges()));
                    if (status.getStatus().equalsIgnoreCase(TableConstants.STATUS_DISPATCH_PENDING)) {
                        return_location = disapprovalPrint();
                    } else {
                        return_location = "seatwork";
                    }
                }

            }

        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Exception", e.getMessage(), FacesMessage.SEVERITY_ERROR);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return return_location;
    }

    public void routeCodeDetail(String Appl_no, String TableName, String state_cd, int off_cd) {
        try {
            CounterSignatureImpl impl = new CounterSignatureImpl();
            boolean flage = false;
            noOfTripsrendered = false;
            actionSource.clear();
            actionTarget.clear();
            Map<String, String> routeMap = impl.getSourcesRouteMap(Appl_no, TableName, state_cd, off_cd);
            for (Map.Entry<String, String> entry : routeMap.entrySet()) {
                actionSource.add(new PermitRouteList(entry.getKey(), entry.getValue()));
            }
            Map<String, String> mapRouteList = impl.getTargetRouteMap(Appl_no, TableName, state_cd, off_cd);
            for (Map.Entry<String, String> entry : mapRouteList.entrySet()) {
                flage = true;
                actionTarget.add(new PermitRouteList(entry.getKey(), entry.getValue()));
            }
            setVia_route("");
            if (flage) {
                setVia_route(impl.getRouteViaRouteList(Appl_no, state_cd, TableName, off_cd));
                setRouteLength(impl.getRouteLength(null, Appl_no, TableName, state_cd, off_cd));
                noOfTripsrendered = true;
            }
            routeManage = new DualListModel<PermitRouteList>(actionSource, actionTarget);
        } catch (VahanException e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
        }
    }

    public void onTransfer() {
        try {
            CounterSignatureImpl impl = new CounterSignatureImpl();
            String route_cd = "";

            setVia_route(impl.getRouteVia(routeManage.getTarget(), Util.getUserStateCode(), Util.getUserOffCode()));
            setRouteLength(impl.getRouteLength(routeManage.getTarget(), null, null, Util.getUserStateCode(), Util.getUserOffCode()));
            if (getVia_route().isEmpty()) {
                noOfTripsrendered = false;
            } else {
                noOfTripsrendered = true;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
        }
    }

    public void vehReasonChangeListener(AjaxBehaviorEvent event) {
        if (("THEFT").equalsIgnoreCase(countDobj.getReasonSelect()) || ("LOST").equalsIgnoreCase(countDobj.getReasonSelect())) {
            countDobj.setReason(countDobj.getReasonSelect());
            setReasonDisable(true);
            dupPanel = true;
        } else {
            countDobj.setReason(countDobj.getReasonSelect());
            setReasonDisable(true);
            dupPanel = false;
            if (("OTHER").equalsIgnoreCase(countDobj.getReasonSelect())) {
                countDobj.setReason("");
                setReasonDisable(false);
            }
            if (("SELECT").equalsIgnoreCase(countDobj.getReasonSelect())) {
                countDobj.setReason("");
                setReasonDisable(true);
            }
        }
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
                this.countDobj.setReason("LOST");
            }
            setBeanFromDobj(dup_dobj);
        } catch (SQLException | CloneNotSupportedException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public DupDobj makeDobjFromBean() throws VahanException {
        DupDobj dobj = new DupDobj();
        if (CommonUtils.isNullOrBlank(this.countDobj.getReason()) || ("").equalsIgnoreCase(this.countDobj.getReason())) {
            dobj = null;
            throw new VahanException("Please select any reason");
        }
        if (validateForm()) {
            if (this.countDobj.getFir_dt() != null
                    && (("THEFT").equalsIgnoreCase(this.countDobj.getReason()) || ("LOST").equalsIgnoreCase(this.countDobj.getReason()))) {
                dobj.setFir_dt(this.countDobj.getFir_dt());
            } else {
                dobj.setFir_dt(null);
            }
            if (!CommonUtils.isNullOrBlank(this.countDobj.getFir_no())
                    && (("THEFT").equalsIgnoreCase(this.countDobj.getReason()) || ("LOST").equalsIgnoreCase(this.countDobj.getReason()))) {
                dobj.setFir_no(this.countDobj.getFir_no());
            } else {
                dobj.setFir_no("");
            }
            if (!CommonUtils.isNullOrBlank(this.countDobj.getPolice_station())
                    && (("THEFT").equalsIgnoreCase(this.countDobj.getReason()) || ("LOST").equalsIgnoreCase(this.countDobj.getReason()))) {
                dobj.setPolice_station(this.countDobj.getPolice_station());
            } else {
                dobj.setPolice_station("");
            }
            dobj.setReason(this.countDobj.getReason());
            if (this.countDobj.getPmtDoc() != null || this.countDobj.getPmtDoc().length > 0) {
                String docId = "";
                for (int i = 0; i < this.countDobj.getPmtDoc().length; i++) {
                    docId += this.countDobj.getPmtDoc()[i] + ",";
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

        this.countDobj.setReason(dobj.getReason());
        this.countDobj.setReasonSelect(dobj.getReason());

        if (!("LOST").equalsIgnoreCase(dobj.getReason()) && !("THEFT").equalsIgnoreCase(dobj.getReason())
                && !("TORN").equalsIgnoreCase(dobj.getReason()) && !("OTHER").equalsIgnoreCase(dobj.getReason())) {
            setReasonDisable(false);
            this.countDobj.setReasonSelect("OTHER");
        } else if (("THEFT").equalsIgnoreCase(dobj.getReason()) || ("LOST").equalsIgnoreCase(dobj.getReason())) {
            this.countDobj.setFir_no(dobj.getFir_no());
            this.countDobj.setFir_dt(dobj.getFir_dt());
            this.countDobj.setPolice_station(dobj.getPolice_station());
            setReasonDisable(true);
            dupPanel = true;
        } else {
            this.countDobj.setFir_no("");
            this.countDobj.setPolice_station("");
        }
        PermitDupPrintImpl dupImpl = new PermitDupPrintImpl();
        String list = dupImpl.getMultiDocumentList(dobj.getRegn_no().toUpperCase(), dobj.getAppl_no().toUpperCase());
        if (!CommonUtils.isNullOrBlank(list)) {
            this.countDobj.setPmtDoc((list).split(","));
        }
    }

    private boolean validateForm() {
        return true;
    }

    @Override
    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
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

    public DupCounterSignatureDobj getCountDobj() {
        return countDobj;
    }

    public void setCountDobj(DupCounterSignatureDobj countDobj) {
        this.countDobj = countDobj;
    }

    public List getStateList() {
        return stateList;
    }

    public void setStateList(List stateList) {
        this.stateList = stateList;
    }

    public List getOffList() {
        return offList;
    }

    public void setOffList(List offList) {
        this.offList = offList;
    }

    public List getPmtTypeList() {
        return pmtTypeList;
    }

    public void setPmtTypeList(List pmtTypeList) {
        this.pmtTypeList = pmtTypeList;
    }

    public List getPeriodModeList() {
        return periodModeList;
    }

    public void setPeriodModeList(List periodModeList) {
        this.periodModeList = periodModeList;
    }

    public PermitOwnerDetailBean getOwnerBean() {
        return ownerBean;
    }

    public void setOwnerBean(PermitOwnerDetailBean ownerBean) {
        this.ownerBean = ownerBean;
    }

    public boolean isRegnNoDisable() {
        return regnNoDisable;
    }

    public void setRegnNoDisable(boolean regnNoDisable) {
        this.regnNoDisable = regnNoDisable;
    }

    public String getApp_no_msg() {
        return app_no_msg;
    }

    public void setApp_no_msg(String app_no_msg) {
        this.app_no_msg = app_no_msg;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public boolean isPanelHideShow() {
        return panelHideShow;
    }

    public void setPanelHideShow(boolean panelHideShow) {
        this.panelHideShow = panelHideShow;
    }

    public boolean isCounterSignatureDtls() {
        return counterSignatureDtls;
    }

    public void setCounterSignatureDtls(boolean counterSignatureDtls) {
        this.counterSignatureDtls = counterSignatureDtls;
    }

    public PermitOwnerDetailDobj getOwnerDobjPrv() {
        return ownerDobjPrv;
    }

    public void setOwnerDobjPrv(PermitOwnerDetailDobj ownerDobjPrv) {
        this.ownerDobjPrv = ownerDobjPrv;
    }

    public CounterSignatureDobj getCountDobjPrv() {
        return countDobjPrv;
    }

    public void setCountDobjPrv(CounterSignatureDobj countDobjPrv) {
        this.countDobjPrv = countDobjPrv;
    }

    public PermitOwnerDetailDobj getOwner_dobj() {
        return owner_dobj;
    }

    public void setOwner_dobj(PermitOwnerDetailDobj owner_dobj) {
        this.owner_dobj = owner_dobj;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public String getSelectOneRadio() {
        return selectOneRadio;
    }

    public void setSelectOneRadio(String selectOneRadio) {
        this.selectOneRadio = selectOneRadio;
    }

    /**
     * @return the areaTypeList
     */
    public List getAreaTypeList() {
        return areaTypeList;
    }

    /**
     * @param areaTypeList the areaTypeList to set
     */
    public void setAreaTypeList(List areaTypeList) {
        this.areaTypeList = areaTypeList;
    }

    public boolean isRenderArea() {
        return renderArea;
    }

    public void setRenderArea(boolean renderArea) {
        this.renderArea = renderArea;
    }

    public String getRegn_state() {
        return regn_state;
    }

    public void setRegn_state(String regn_state) {
        this.regn_state = regn_state;
    }

    public List getPurposeList() {
        return purposeList;
    }

    public void setPurposeList(List purposeList) {
        this.purposeList = purposeList;
    }

    public DualListModel<PermitRouteList> getRouteManage() {
        return routeManage;
    }

    public void setRouteManage(DualListModel<PermitRouteList> routeManage) {
        this.routeManage = routeManage;
    }

    public String getVia_route() {
        return via_route;
    }

    public void setVia_route(String via_route) {
        this.via_route = via_route;
    }

    public int getRouteLength() {
        return routeLength;
    }

    public void setRouteLength(int routeLength) {
        this.routeLength = routeLength;
    }

    public boolean isVisibleRouteDtls() {
        return visibleRouteDtls;
    }

    public void setVisibleRouteDtls(boolean visibleRouteDtls) {
        this.visibleRouteDtls = visibleRouteDtls;
    }

    public boolean isDisableRoutDtls() {
        return disableRoutDtls;
    }

    public void setDisableRoutDtls(boolean disableRoutDtls) {
        this.disableRoutDtls = disableRoutDtls;
    }

    public boolean isNoOfTripsrendered() {
        return noOfTripsrendered;
    }

    public void setNoOfTripsrendered(boolean noOfTripsrendered) {
        this.noOfTripsrendered = noOfTripsrendered;
    }

    public int getNo_of_trip() {
        return no_of_trip;
    }

    public void setNo_of_trip(int no_of_trip) {
        this.no_of_trip = no_of_trip;
    }

    public List getReasonList() {
        return reasonList;
    }

    public void setReasonList(List reasonList) {
        this.reasonList = reasonList;
    }

    public void setReasonDisable(boolean reasonDisable) {
        this.reasonDisable = reasonDisable;
    }

    public boolean isDupPanel() {
        return dupPanel;
    }

    public void setDupPanel(boolean dupPanel) {
        this.dupPanel = dupPanel;
    }

    public List getPmtDocList() {
        return pmtDocList;
    }

    public void setPmtDocList(List pmtDocList) {
        this.pmtDocList = pmtDocList;
    }

    public boolean isDup_cert_visible() {
        return dup_cert_visible;
    }

    public void setDup_cert_visible(boolean dup_cert_visible) {
        this.dup_cert_visible = dup_cert_visible;
    }

    public Date getCurrDate() {
        currDate = new Date();
        return currDate;
    }

    public void setCurrDate(Date currDate) {
        this.currDate = currDate;
    }

    /**
     * @return the reasonDisable
     */
    public boolean isReasonDisable() {
        return reasonDisable;
    }

    public DupDobj getPrevDupDobj() {
        return prevDupDobj;
    }

    public void setPrevDupDobj(DupDobj prevDupDobj) {
        this.prevDupDobj = prevDupDobj;
    }
}
