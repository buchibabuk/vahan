/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitLOIDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.form.impl.permit.PassengerPermitDetailImpl;
import nic.vahan.form.impl.permit.PermitLOIImpl;
import nic.vahan.form.impl.permit.PermitLoiAdditionalDtlsImpl;
import nic.vahan.form.impl.permit.PermitOwnerDetailImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DualListModel;

/**
 *
 * @author hcl
 */
@ManagedBean(name = "pmtLoi")
@ViewScoped
public class PermitLOIBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(PermitLOIBean.class);
    private String searchByValue = "applNo";
    private String ownName = "";
    private String firstPartApplNo = "";
    private String secondPartApplNo = "";
    List<PermitLOIDobj> pendingList = null;
    private List<PermitLOIDobj> allPendingList = null;
    private List<PermitLOIDobj> pendingListFilter = null;
    private PermitLOIDobj rowDetails = null;
    private PermitOwnerDetailDobj pmtOwnDtlsDobj = null;
    @ManagedProperty(value = "#{pmt_owner_dtls}")
    private PermitOwnerDetailBean pmtOwnDtlsBean;
    private PermitOwnerDetailImpl pmtOwnDtlsImpl;
    private String orderNo = "";
    private String orderBy = "";
    private Date orderDt;
    private String reason = "";
    private String appDisapp = "A";
    private String offerNoGenMsg = "";
    private List pmtObjectionList = new ArrayList();
    private String[] pmtObjection = null;
    private List appDisappList = new ArrayList();
    @ManagedProperty(value = "#{loiAddDlts}")
    private PermitLoiAdditionalDtlsBean loiAdditionalDlts;
    private List route_mast = new ArrayList();
    private List area_mast = new ArrayList();
    private DualListModel<PermitRouteList> routeManage;
    private DualListModel<PermitRouteList> areaManage;
    private List<PermitRouteList> actionSource = new ArrayList<>();
    private List<PermitRouteList> actionTarget = new ArrayList<>();
    private List<PermitRouteList> areaActionSource = new ArrayList<>();
    private List<PermitRouteList> areaActionTarget = new ArrayList<>();
    private String via_route = "";
    private PassengerPermitDetailDobj permit_dobj = new PassengerPermitDetailDobj();
    private List<SelectItem> pmt_service_type_list = new ArrayList();
    private boolean renderDaysPanal = true;
    private String dmsUrl = "";
    private boolean renderDocUploadTab = false;
    Map<String, String> stateConfigMap = null;

    public PermitLOIBean() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        String monthYear = String.valueOf(cal.get(java.util.Calendar.YEAR)).substring(2, 4) + String.format("%02d", cal.get(java.util.Calendar.MONTH) + 1);
        firstPartApplNo = Util.getUserStateCode() + monthYear;
        pmtOwnDtlsBean = new PermitOwnerDetailBean();
        String[][] data = MasterTableFiller.masterTables.VM_PERMIT_OBJECTION.getData();
        if (data != null) {
            for (int i = 0; i < data.length; i++) {
                pmtObjectionList.add(new SelectItem(data[i][1], data[i][2]));
            }
        }

        data = MasterTableFiller.masterTables.vm_service_type.getData();
        pmt_service_type_list.add(new SelectItem("-1", "Select"));
        for (int i = 0; i < data.length; i++) {
            pmt_service_type_list.add(new SelectItem(data[i][0].trim(), data[i][1]));
        }

        try {
            stateConfigMap = CommonPermitPrintImpl.getVmPermitStateConfiguration(Util.getUserStateCode());
            VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
            parameters.setPUR_CD(TableConstants.VM_PMT_FRESH_PUR_CD);
            if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("show_uploaded_document"), parameters), "init")) {
                renderDocUploadTab = true;
            } else {
                renderDocUploadTab = false;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

    }

    public void booleanRegisterVehicle(AjaxBehaviorEvent event) {
        String paddedApplNo = firstPartApplNo.trim() + secondPartApplNo.trim();
        if (allPendingList != null) {
            allPendingList.clear();
        }
        if (pendingList != null) {
            pendingList.clear();
        }
        pmtOwnDtlsDobj = null;
        if (searchByValue.equalsIgnoreCase("applNo")) {
            renderDaysPanal = true;
            // allPendingList = impl.getAllLoiPendingAppl(Util.getUserSeatOffCode(), Util.getUserStateCode(), searchByValue, paddedApplNo);
            PrimeFaces.current().ajax().update("pendingpmtLoi");
        } else if (searchByValue.equalsIgnoreCase("allApplNo")) {
            PermitLOIImpl impl = new PermitLOIImpl();
            allPendingList = impl.getAllLoiPendingAppl(Util.getUserSeatOffCode(), Util.getUserStateCode(), searchByValue, paddedApplNo);
            if (allPendingList.size() > 0) {
                renderDaysPanal = false;
                PrimeFaces.current().ajax().update("pendingpmtLoi");
            }
        }
    }

    public void btGetDetailsForRePrint() {
        String errorMsg = "";
        PermitLOIImpl impl = null;
        try {
            impl = new PermitLOIImpl();
            if (searchByValue.equalsIgnoreCase("all") || (!firstPartApplNo.equals("") && !secondPartApplNo.equals("")) || !ownName.equals("")) {
                if (searchByValue.equalsIgnoreCase("all") || secondPartApplNo.trim().length() <= 10) {
                    if (searchByValue.equalsIgnoreCase("applNo")) {
                        String paddedApplNo = secondPartApplNo.trim();
                        if (paddedApplNo != null && !paddedApplNo.isEmpty() && paddedApplNo.replaceAll("[0-9]", "").length() == 0) {
                            paddedApplNo = String.format("%010d", Long.parseLong(secondPartApplNo.trim()));
                        }
                        pendingList = impl.getPendingWork(firstPartApplNo.trim() + paddedApplNo, null, Util.getUserStateCode());
                        if (pendingList.size() < 1) {
                            errorMsg = "No pending work against " + firstPartApplNo.trim() + paddedApplNo + "";
                        }
                    } else if (searchByValue.equalsIgnoreCase("ownName")) {
                        pendingList = impl.getPendingWork(null, getOwnName(), Util.getUserStateCode());
                        if (pendingList.size() < 1) {
                            errorMsg = "No pending work against " + ownName + "";
                        }
                    } else if (searchByValue.equalsIgnoreCase("all")) {
                        pendingList = impl.getPendingWork(null, null, Util.getUserStateCode());
                        if (pendingList.size() < 1) {
                            errorMsg = "No pending work against";
                        }
                    }
                    if (pendingList.size() < 1) {
                        pendingList.clear();
                        JSFUtils.showMessagesInDialog("Information", errorMsg, FacesMessage.SEVERITY_INFO);
                    }
                } else {
                    pendingList = null;
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Invalid Application no.Minimum Character is 1 and Maximum Character is 10"));
                }
            } else {
                pendingList = null;
                //RequestContext rc = RequestContext.getCurrentInstance();
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please enter Application No."));
            }
        } catch (VahanException ex) {
            JSFUtils.showMessagesInDialog("Error", ex.getMessage(), FacesMessage.SEVERITY_INFO);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void showGetDetails(String appl_no) {
        PermitLOIImpl impl = null;
        try {
            impl = new PermitLOIImpl();
            if (appl_no != null) {
                pendingList = impl.getPendingWork(appl_no, null, Util.getUserStateCode());
            } else {
                JSFUtils.showMessagesInDialog("Info", "No Detail found", FacesMessage.SEVERITY_INFO);
            }
        } catch (VahanException ex) {
            JSFUtils.showMessagesInDialog("Error", ex.getMessage(), FacesMessage.SEVERITY_INFO);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void showDetails() {
        pmtOwnDtlsImpl = new PermitOwnerDetailImpl();
        pmtOwnDtlsDobj = new PermitOwnerDetailDobj();
        pmtOwnDtlsDobj.setDisable(true);
        PassengerPermitDetailImpl passImp = new PassengerPermitDetailImpl();
        try {
            if (getRowDetails().getRegn_no().equalsIgnoreCase("NEW")) {
                pmtOwnDtlsBean.setValueinDOBJ(pmtOwnDtlsImpl.set_VA_Owner_permit_to_dobj(getRowDetails().getAppl_no(), "NEW"));
            } else if (!getRowDetails().getRegn_no().equalsIgnoreCase("NEW")) {
                pmtOwnDtlsBean.setValueinDOBJ(pmtOwnDtlsImpl.set_Owner_appl_db_to_dobj(getRowDetails().getRegn_no(), null));
            }

            permit_dobj = passImp.set_permit_appl_db_to_dobj(getRowDetails().getAppl_no());

            String str = permit_dobj.getRegion_covered();
            String[] temp = str.split(",");
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < temp.length; i++) {
                stringBuilder.append("(").append("'" + temp[i] + "'").append("),");
            }

            areaCodeDetail(Util.getUserStateCode(), stringBuilder.substring(0, stringBuilder.length() - 1), Util.getUserOffCode());

            routeCodeDetail(getRowDetails().getAppl_no(), TableList.va_permit_route, Util.getUserStateCode(), Util.getUserOffCode(), false);

            PermitLoiAdditionalDtlsImpl loiImpl = new PermitLoiAdditionalDtlsImpl();
            loiAdditionalDlts.setLoiAdditionalDobj(loiImpl.getVpLoiDtls(getRowDetails().getAppl_no()));
            PrimeFaces.current().ajax().update("pmtDtlsPanel");
            PermitLOIImpl impl = new PermitLOIImpl();
            boolean flag = impl.objectionAssign(getRowDetails().getAppl_no().toUpperCase());
            if (flag) {
                appDisappList.clear();
                appDisappList.add(new SelectItem("A", "Approve"));
                appDisappList.add(new SelectItem("O", "Objection"));
            } else {
                appDisappList.clear();
                appDisappList.add(new SelectItem("A", "Approve"));
                appDisappList.add(new SelectItem("D", "Disapprove"));
            }
            if (Util.getUserName().length() > 29) {
                setOrderBy(Util.getUserName().substring(0, 29));
            } else {
                setOrderBy(Util.getUserName());
            }
            setOrderDt(new Date());
            setOrderNo(new PermitLOIImpl().readOnlyPermitUniqueNo(Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), 0, 0, "A"));
        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Alert!", e.getMessage(), FacesMessage.SEVERITY_ERROR);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Alert!", TableConstants.SomthingWentWrong, FacesMessage.SEVERITY_ERROR);
        }
    }

    public void onTransfer() {
        try {
            PassengerPermitDetailImpl passImp = new PassengerPermitDetailImpl();
            setVia_route(passImp.getRouteVia(routeManage.getTarget(), Util.getUserStateCode(), Util.getUserOffCode()));

        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
        }
    }

    public String reverBackForRectification() {
        Status_dobj status_dobj = new Status_dobj();
        int prevAcCode = 0;
        Exception ex = null;
        String return_path = "";
        PermitLOIImpl impl = null;
        try {
            if (getAppDisapp().equalsIgnoreCase("D") || getAppDisapp().equalsIgnoreCase("O")) {
                if (getAppDisapp().equalsIgnoreCase("O")) {
                    if (getPmtObjection() == null || getPmtObjection().length == 0) {
                        JSFUtils.showMessagesInDialog("Error", "Please select any one objection", FacesMessage.SEVERITY_ERROR);
                        return "";
                    } else {
                        if (getPmtObjection().length > 0) {
                            String objectList = "";
                            for (int i = 0; i < getPmtObjection().length; i++) {
                                objectList += pmtObjection[i] + ",";
                            }
                            setReason(objectList.substring(0, (objectList.length() - 1)));
                        }
                    }
                }
                impl = new PermitLOIImpl();
                Util.getSelectedSeat().setAction_cd(TableConstants.TM_ROLE_PMT_FEE);
                prevAcCode = ServerUtil.getPreviousActionCode(TableConstants.TM_ROLE_PMT_FEE, TableConstants.VM_PMT_FRESH_PUR_CD, getRowDetails().getAppl_no(), null);
                status_dobj.setPrev_action_cd_selected(prevAcCode);
                status_dobj.setAppl_no(getRowDetails().getAppl_no());
                status_dobj.setPur_cd(TableConstants.VM_PMT_FRESH_PUR_CD);
                status_dobj.setStatus(TableConstants.STATUS_REVERT);
                impl.revertBackForRectification(status_dobj, getRowDetails().getAppl_no(), getReason().toUpperCase(), getAppDisapp(), pmtOwnDtlsBean.getMobile_no());
                return_path = "home";
            } else {
                JSFUtils.showMessagesInDialog("Error", "Please fill the disapprove reason", FacesMessage.SEVERITY_ERROR);
                return_path = "";
            }
        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Alert!", e.getMessage(), FacesMessage.SEVERITY_ERROR);
        } catch (Exception e) {
            JSFUtils.showMessagesInDialog("Alert!", "Can't RollBack For " + getRowDetails().getAppl_no() + ",Please contact Administrator!", FacesMessage.SEVERITY_ERROR);
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return return_path;
    }

    public void approveApplication() {
        PermitLOIImpl impl = null;
        try {
            if (CommonUtils.isNullOrBlank(getOrderBy())) {
                JSFUtils.showMessagesInDialog("Error", "Order By is not Empty", FacesMessage.SEVERITY_ERROR);
            } else if (CommonUtils.isNullOrBlank(getOrderNo())) {
                JSFUtils.showMessagesInDialog("Error", "Order No is not Empty", FacesMessage.SEVERITY_ERROR);
            } else if (getOrderDt() == null) {
                JSFUtils.showMessagesInDialog("Error", "Order Date is not Empty", FacesMessage.SEVERITY_ERROR);
            } else {
                impl = new PermitLOIImpl();
                String offerNo = impl.approveApplication(getRowDetails().getAppl_no(), getOrderNo().toUpperCase(), getOrderBy().toUpperCase(), getOrderDt(), getRowDetails().getPmt_type(), pmtOwnDtlsBean.getMobile_no());
                popUpDialogBox(offerNo);
            }
        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Alert!", e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void openModifyUploadedDocumentService() {
        try {

            String appl_no = ((PermitLOIDobj) pendingList.get(0)).getAppl_no();

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

    public void routeCodeDetail(String Appl_no, String TableName, String state_cd, int off_cd, boolean otherOffRouteAllow) {
        try {
            boolean flage = false;
            PassengerPermitDetailImpl passImp = new PassengerPermitDetailImpl();
            actionSource.clear();
            actionTarget.clear();
            Map<String, String> routeMap = passImp.getSourcesRouteMap(Appl_no, TableName, state_cd, off_cd, otherOffRouteAllow);
            for (Map.Entry<String, String> entry : routeMap.entrySet()) {
                actionSource.add(new PermitRouteList(entry.getKey(), entry.getValue()));
            }
            Map<String, String> mapRouteList = passImp.getTargetRouteMap(Appl_no, TableName, state_cd, off_cd, otherOffRouteAllow);
            for (Map.Entry<String, String> entry : mapRouteList.entrySet()) {
                flage = true;
                actionTarget.add(new PermitRouteList(entry.getKey(), entry.getValue()));
            }
            setVia_route("");
            if (flage) {
                setVia_route(passImp.getRouteViaRouteList(Appl_no, state_cd, TableName, off_cd, otherOffRouteAllow));

            }
            routeManage = new DualListModel<PermitRouteList>(actionSource, actionTarget);

        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Alert!", TableConstants.SomthingWentWrong, FacesMessage.SEVERITY_ERROR);
        }
    }

    public void areaCodeDetail(String state_cd, String stringBuilder, int off_cd) {
        PassengerPermitDetailImpl passImp = new PassengerPermitDetailImpl();
        areaActionSource.clear();
        areaActionTarget.clear();
        if (CommonUtils.isNullOrBlank(stringBuilder) || stringBuilder.equals("-1") || stringBuilder.isEmpty() || ("('')").equalsIgnoreCase(stringBuilder)) {
            Map<String, String> routeMap = passImp.getTargetAreaMap(state_cd, off_cd);
            for (Map.Entry<String, String> entry : routeMap.entrySet()) {
                areaActionSource.add(new PermitRouteList(entry.getKey(), entry.getValue()));
            }
        } else {
            String tempStr = stringBuilder;
            tempStr = tempStr.replace("('", "").replace("')", "");
            String[] temp = tempStr.split(",");
            if (JSFUtils.isNumeric(temp[0])) {
                Map<String, String> routeMap = passImp.getSourcesAreaMapWithStrBuil(stringBuilder, state_cd, off_cd);
                for (Map.Entry<String, String> entry : routeMap.entrySet()) {
                    areaActionSource.add(new PermitRouteList(entry.getKey(), entry.getValue()));
                }
                Map<String, String> mapRouteList = passImp.getTargetAreaMapWithStrBuil(stringBuilder, state_cd, off_cd);
                for (Map.Entry<String, String> entry : mapRouteList.entrySet()) {
                    areaActionTarget.add(new PermitRouteList(entry.getKey(), entry.getValue()));
                }
            }
        }
        areaManage = new DualListModel<PermitRouteList>(areaActionSource, areaActionTarget);

    }

    public void popUpDialogBox(String offerNo) {
        if (CommonUtils.isNullOrBlank(orderNo)) {
            setOfferNoGenMsg("Offer/LOI Number is not genrated");
            PrimeFaces.current().executeScript("PF('offNum').show();");
        } else {
            setOfferNoGenMsg("Offer/LOI No generated :<font color=\"green\">" + offerNo + "</font>. Please note down the Offer/LOI No for future reference.");
            PrimeFaces.current().executeScript("PF('offNum').show();");
        }
    }

    public String getSearchByValue() {
        return searchByValue;
    }

    public void setSearchByValue(String searchByValue) {
        this.searchByValue = searchByValue;
    }

    public String getOwnName() {
        return ownName;
    }

    public void setOwnName(String ownName) {
        this.ownName = ownName;
    }

    public String getFirstPartApplNo() {
        return firstPartApplNo;
    }

    public void setFirstPartApplNo(String firstPartApplNo) {
        this.firstPartApplNo = firstPartApplNo;
    }

    public String getSecondPartApplNo() {
        return secondPartApplNo;
    }

    public void setSecondPartApplNo(String secondPartApplNo) {
        this.secondPartApplNo = secondPartApplNo;
    }

    public List<PermitLOIDobj> getPendingList() {
        return pendingList;
    }

    public void setPendingList(List<PermitLOIDobj> pendingList) {
        this.pendingList = pendingList;
    }

    public PermitLOIDobj getRowDetails() {
        return rowDetails;
    }

    public void setRowDetails(PermitLOIDobj rowDetails) {
        this.rowDetails = rowDetails;
    }

    public PermitOwnerDetailDobj getPmtOwnDtlsDobj() {
        return pmtOwnDtlsDobj;
    }

    public void setPmtOwnDtlsDobj(PermitOwnerDetailDobj pmtOwnDtlsDobj) {
        this.pmtOwnDtlsDobj = pmtOwnDtlsDobj;
    }

    public PermitOwnerDetailBean getPmtOwnDtlsBean() {
        return pmtOwnDtlsBean;
    }

    public void setPmtOwnDtlsBean(PermitOwnerDetailBean pmtOwnDtlsBean) {
        this.pmtOwnDtlsBean = pmtOwnDtlsBean;
    }

    public PermitOwnerDetailImpl getPmtOwnDtlsImpl() {
        return pmtOwnDtlsImpl;
    }

    public void setPmtOwnDtlsImpl(PermitOwnerDetailImpl pmtOwnDtlsImpl) {
        this.pmtOwnDtlsImpl = pmtOwnDtlsImpl;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Date getOrderDt() {
        return orderDt;
    }

    public void setOrderDt(Date orderDt) {
        this.orderDt = orderDt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAppDisapp() {
        return appDisapp;
    }

    public void setAppDisapp(String appDisapp) {
        this.appDisapp = appDisapp;
    }

    public String getOfferNoGenMsg() {
        return offerNoGenMsg;
    }

    public void setOfferNoGenMsg(String offerNoGenMsg) {
        this.offerNoGenMsg = offerNoGenMsg;
    }

    public List getPmtObjectionList() {
        return pmtObjectionList;
    }

    public void setPmtObjectionList(List pmtObjectionList) {
        this.pmtObjectionList = pmtObjectionList;
    }

    public String[] getPmtObjection() {
        return pmtObjection;
    }

    public void setPmtObjection(String[] pmtObjection) {
        this.pmtObjection = pmtObjection;
    }

    public List getAppDisappList() {
        return appDisappList;
    }

    public void setAppDisappList(List appDisappList) {
        this.appDisappList = appDisappList;
    }

    public PermitLoiAdditionalDtlsBean getLoiAdditionalDlts() {
        return loiAdditionalDlts;
    }

    public void setLoiAdditionalDlts(PermitLoiAdditionalDtlsBean loiAdditionalDlts) {
        this.loiAdditionalDlts = loiAdditionalDlts;
    }

    /**
     * @return the pendingListFilter
     */
    public List<PermitLOIDobj> getPendingListFilter() {
        return pendingListFilter;
    }

    /**
     * @param pendingListFilter the pendingListFilter to set
     */
    public void setPendingListFilter(List<PermitLOIDobj> pendingListFilter) {
        this.pendingListFilter = pendingListFilter;
    }

    public List getRoute_mast() {
        return route_mast;
    }

    public void setRoute_mast(List route_mast) {
        this.route_mast = route_mast;
    }

    public List getArea_mast() {
        return area_mast;
    }

    public void setArea_mast(List area_mast) {
        this.area_mast = area_mast;
    }

    public DualListModel<PermitRouteList> getRouteManage() {
        return routeManage;
    }

    public void setRouteManage(DualListModel<PermitRouteList> routeManage) {
        this.routeManage = routeManage;
    }

    public DualListModel<PermitRouteList> getAreaManage() {
        return areaManage;
    }

    public void setAreaManage(DualListModel<PermitRouteList> areaManage) {
        this.areaManage = areaManage;
    }

    public List<PermitRouteList> getActionSource() {
        return actionSource;
    }

    public void setActionSource(List<PermitRouteList> actionSource) {
        this.actionSource = actionSource;
    }

    public List<PermitRouteList> getActionTarget() {
        return actionTarget;
    }

    public void setActionTarget(List<PermitRouteList> actionTarget) {
        this.actionTarget = actionTarget;
    }

    public List<PermitRouteList> getAreaActionSource() {
        return areaActionSource;
    }

    public void setAreaActionSource(List<PermitRouteList> areaActionSource) {
        this.areaActionSource = areaActionSource;
    }

    public List<PermitRouteList> getAreaActionTarget() {
        return areaActionTarget;
    }

    public void setAreaActionTarget(List<PermitRouteList> areaActionTarget) {
        this.areaActionTarget = areaActionTarget;
    }

    public String getVia_route() {
        return via_route;
    }

    public void setVia_route(String via_route) {
        this.via_route = via_route;
    }

    public PassengerPermitDetailDobj getPermit_dobj() {
        return permit_dobj;
    }

    public void setPermit_dobj(PassengerPermitDetailDobj permit_dobj) {
        this.permit_dobj = permit_dobj;
    }

    public List<SelectItem> getPmt_service_type_list() {
        return pmt_service_type_list;
    }

    public void setPmt_service_type_list(List<SelectItem> pmt_service_type_list) {
        this.pmt_service_type_list = pmt_service_type_list;
    }

    public List<PermitLOIDobj> getAllPendingList() {
        return allPendingList;
    }

    public void setAllPendingList(List<PermitLOIDobj> allPendingList) {
        this.allPendingList = allPendingList;
    }

    public boolean isRenderDaysPanal() {
        return renderDaysPanal;
    }

    public void setRenderDaysPanal(boolean renderDaysPanal) {
        this.renderDaysPanal = renderDaysPanal;
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
}
