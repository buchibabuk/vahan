/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.bean.AxleBean;
import nic.vahan.form.bean.ExArmyBean;
import nic.vahan.form.bean.HSRPDetailsBean;
import nic.vahan.form.bean.HypothecationDetailsBean;
import nic.vahan.form.bean.InsBean;
import nic.vahan.form.bean.PermitDisplayDetailBean;
import nic.vahan.form.bean.SCDetailsBean;
import nic.vahan.form.bean.Trailer_bean;
import nic.vahan.form.bean.VehicleHistoryDetailsBean;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.dobj.DelDupRegnNoDobj;
import nic.vahan.form.dobj.ExArmyDobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.NocDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.Trailer_dobj;
import nic.vahan.form.dobj.common.RegistrationStatusParametersDobj;
import nic.vahan.form.impl.AxleImpl;
import nic.vahan.form.impl.ExArmyImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.HSRPDetailsImpl;
import nic.vahan.form.impl.HpaImpl;
import nic.vahan.form.impl.InsImpl;
import nic.vahan.form.impl.NocImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.SmartCardImpl;
import nic.vahan.form.impl.Trailer_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.admin.DelDupRegnNoImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

/**
 *
 * @author tranC075
 */
@ManagedBean(name = "delDupReg")
@ViewScoped
public class DelDupRegnNoBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(DelDupRegnNoBean.class);
    private String regnNo = "";
    private boolean disableRegnNo = false;
    @ManagedProperty(value = "#{trailer_bean}")
    private Trailer_bean trailer_bean;
    @ManagedProperty(value = "#{ins_bean}")
    private InsBean ins_bean;
    @ManagedProperty(value = "#{hypothecationDetails_bean}")
    private HypothecationDetailsBean hypothecationDetails_bean;
    @ManagedProperty(value = "#{exArmyBean}")
    private ExArmyBean exArmyBean;
    @ManagedProperty(value = "#{axleBean}")
    private AxleBean axleBean;
    private List<OwnerDetailsDobj> regnNameList = null;
    private boolean showRegList = false;
    @ManagedProperty(value = "#{hsrp_bean}")
    private HSRPDetailsBean hsrpDetailsBean;
    @ManagedProperty(value = "#{smartcard_bean}")
    private SCDetailsBean scDetailsBean;
    @ManagedProperty(value = "#{vehicleHistory_bean}")
    private VehicleHistoryDetailsBean vehicleHistoryDetailsBean;
    @ManagedProperty(value = "#{permitDtls}")
    private PermitDisplayDetailBean permitDetailBean;
    private boolean smartcardStatus = false;
    private boolean hsrpStatus = false;
    private FitnessDobj fitnessDobj = null;
    private boolean isTransport = false;
    private RegistrationStatusParametersDobj regStatus = new RegistrationStatusParametersDobj();
    private boolean render = false;
    private boolean showBack = false;
    private OwnerDetailsDobj ownerDetail;
    private boolean showDelete = false;
    private boolean showRegistrationNo = true;
    private List listOwnerCatg = new ArrayList();
    private ArrayList<Status_dobj> statusList = null;
    //Add by amit
    private List<OwnerDetailsDobj> regnDupList = null;
    private OwnerDetailsDobj ownerDtlsDeDup;
    private boolean rendeDel_show;
    private boolean renderDelete_Duplicate;
    private boolean render_back;
    //vdd
    private boolean renderVddDup;
    private List<DelDupRegnNoDobj> vddDupListRegno = new ArrayList<>();
    private List office_list;
    private int offCd;
    private String vddRegnNo;
    private DelDupRegnNoDobj delDupRegnNoDobj;

    @PostConstruct
    public void init() {
        renderVddDup = true;
        vddDupListRegno.clear();
        offCd = 0;
        List<Integer> offTypeCd = Arrays.asList(0, 1, 2);
        try {
            office_list = ServerUtil.getOfficeBasedOnType(Util.getUserStateCode(), offTypeCd);
            if (!Util.getUserCategory().equalsIgnoreCase(TableConstants.USER_CATG_STATE_ADMIN)) {
                Iterator ite = office_list.iterator();
                while (ite.hasNext()) {
                    SelectItem obj = (SelectItem) ite.next();
                    if (Integer.parseInt(obj.getValue().toString().trim()) == Util.getUserOffCode()) {
                        office_list.clear();
                        office_list.add(obj);
                        break;
                    }
                }
            }
        } catch (VahanException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
        }

    }

    public void restDataTable() {
        PrimeFaces.current().executeScript("PF('vddregnoidDel').clearFilters()");
        PrimeFaces.current().ajax().update("vddregnoid");
    }

    public void showListOfRegNo() {
        renderVddDup = false;
        FacesMessage message = null;
        String mgs = "";

        if (this.getRegnNo().trim() == null || this.getRegnNo().trim().equalsIgnoreCase("")) {
            mgs = Util.getLocaleMsg("valid_regn_no");
            renderVddDup = true;
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, mgs, mgs);
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }

        try {
            restDataTable();
            rendeDel_show = false;
            renderDelete_Duplicate = false;
            statusList = null;
            OwnerImpl owner_Impl = new OwnerImpl();
            regnNameList = owner_Impl.getOwnerDetailsList(this.getRegnNo().toUpperCase().trim(), null);
            regnDupList = owner_Impl.getDupOwnerDetails(this.getRegnNo().toUpperCase().trim(), null);
            if ((regnNameList == null || regnNameList.isEmpty()) && (regnDupList == null || regnDupList.isEmpty())) {
                renderVddDup = true;
                showRegistrationNo = true;
                setShowRegList(false);
                render = false;
                mgs = Util.getLocaleMsg("regn_not_found");
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, mgs, mgs);
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            } else if (getRegnNameList().size() == 1 && regnDupList.size() == 0) {
                renderVddDup = true;
                showRegistrationNo = true;
                setShowRegList(false);
                render = false;
                mgs = Util.getLocaleMsg("dupNotFnd");
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, mgs, mgs);
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            } else {
                renderDelete_Duplicate = true;
                setShowRegList(true);
                showRegistrationNo = false;
                render = false;
                render_back = true;
            }
            if (getRegnNameList().size() == 1) {
                renderDelete_Duplicate = true;
                setShowRegList(true);
                render_back = true;
            }
            if (regnDupList != null && !regnDupList.isEmpty()) {
                rendeDel_show = true;
            }

        } catch (Exception ex) {
            getLOGGER().error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void getDetails(OwnerDetailsDobj ownerDetail) {
        this.ownerDetail = ownerDetail;
        Exception ex = null;
        Trailer_Impl trailer_Impl = new Trailer_Impl();
        try {
            //Master Filler for Owner Category
            if (!ownerDetail.getState_cd().equalsIgnoreCase(Util.getUserStateCode()) && !ownerDetail.getStatus().equalsIgnoreCase(TableConstants.VT_NOC_ISSUE_STATUS)) {
                JSFUtils.showMessage(Util.getLocaleMsg("vehRegistered") + " " + ownerDetail.getState_name() + ", " + ownerDetail.getOff_name() + "." + Util.getLocaleMsg("notMoveHistory"));
                return;
            } else if (!ownerDetail.getState_cd().equalsIgnoreCase(Util.getUserStateCode()) && ownerDetail.getStatus().equals("N")) {
                NocImpl nocImpl = new NocImpl();
                NocDobj nocDobj = nocImpl.getNocDetails(ownerDetail.getRegn_no(), ownerDetail.getState_cd(), ownerDetail.getOff_cd());
                if (nocDobj != null) {
                    if (!nocDobj.getState_to().equalsIgnoreCase(Util.getUserStateCode())) {
                        if (nocDobj.getOff_to() != 0) {
                            JSFUtils.showMessage(Util.getLocaleMsg("noc_taken") + " " + ServerUtil.getStateNameByStateCode(nocDobj.getState_to()) + ", " + ServerUtil.getOfficeName(nocDobj.getOff_to(), nocDobj.getState_to()) + "," + Util.getLocaleMsg("notMoveHistory"));
                            return;
                        } else {
                            JSFUtils.showMessage(Util.getLocaleMsg("noc_taken") + " " + ServerUtil.getStateNameByStateCode(nocDobj.getState_to()) + "." + Util.getLocaleMsg("notMoveHistory"));
                            return;
                        }
                    }
                } else {
                    JSFUtils.showMessage(Util.getLocaleMsg("noc_not_found"));
                    return;

                }

            }
            statusList = ServerUtil.applicationStatusInSameOffice(this.getRegnNo().trim(), Util.getUserStateCode(), ownerDetail.getOff_cd());
            if (statusList != null && !statusList.isEmpty()) {

                boolean isNewRegnPendingApplication = false;
                for (int i = 0; i < statusList.size(); i++) {
                    if (statusList.get(i).getPur_cd() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                            || statusList.get(i).getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
                        isNewRegnPendingApplication = true;
                        statusList.clear();
                        break;
                    }
                }
                if (!isNewRegnPendingApplication) {//for skip in case of new registration pending case
                    showRegistrationNo = true;
                    setShowRegList(false);
                    render = false;
                    render_back = false;
                    rendeDel_show = false;
                    return;
                }
            }
            String[][] data = MasterTableFiller.masterTables.VM_OWCATG.getData();
            for (int i = 0; i < data.length; i++) {
                listOwnerCatg.add(new SelectItem(data[i][0], data[i][1]));
            }
            Long user_cd = Long.parseLong(Util.getEmpCode());
            String userCatg = Util.getUserCategory();
            if (userCatg != null) {
                if (userCatg.equals(TableConstants.User_Dealer)) {
                    Map makerAndDealerDetail = OwnerImpl.getDealerDetail(user_cd);
                    if (!ownerDetail.getDealer_cd().equals(makerAndDealerDetail.get("dealer_cd"))) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", Util.getLocaleMsg("notValidDlr")));
                        return;
                    }
                }
            }

            setSmartcardStatus(SmartCardImpl.isSmartCard(ownerDetail.getState_cd(), ownerDetail.getOff_cd()));
            setHsrpStatus(HSRPDetailsImpl.isHsrp(ownerDetail.getState_cd(), ownerDetail.getOff_cd()));
            setIsTransport(ServerUtil.isTransport(ownerDetail.getVh_class(), null));
            //for Owner Identification Fields disallow typing
            if (ownerDetail.getOwnerIdentity() != null) {
                ownerDetail.getOwnerIdentity().setFlag(true);
                ownerDetail.getOwnerIdentity().setMobileNoEditable(true);
                ownerDetail.getOwnerIdentity().setOwnerCatgEditable(true);
            }

            if (ownerDetail.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_EXARMY)) {
                ExArmyDobj dobj = ExArmyImpl.setExArmyDetails_db_to_dobj(null, ownerDetail.getRegn_no(), ownerDetail.getState_cd(), ownerDetail.getOff_cd());
                if (dobj != null) {
                    getExArmyBean().setDobj_To_Bean(dobj);
                }
            }

            if (ownerDetail.getVehTypeDescr().equalsIgnoreCase(TableConstants.VM_VEHTYPE_TRANSPORT_DESCR)) {
                AxleDetailsDobj dobj = AxleImpl.setAxleVehDetails_db_to_dobj(null, ownerDetail.getRegn_no(), ownerDetail.getState_cd(), ownerDetail.getOff_cd());
                if (dobj != null) {
                    getAxleBean().setDobj_To_Bean(dobj);
                }
                FitnessImpl fitImpl = new FitnessImpl();
                setFitnessDobj(fitImpl.set_Fitness_appl_db_to_dobj_withStateCdOffCd(ownerDetail.getRegn_no(), null, ownerDetail.getState_cd(), ownerDetail.getOff_cd()));
            }
            //done by komal
            // as discussed by ashok sir and akshey sir

            if (getHsrpDetailsBean() != null) {

                getHsrpDetailsBean().setHSRPFlag(ownerDetail.getState_cd(), ownerDetail.getOff_cd());
                getHsrpDetailsBean().setRegNo(ownerDetail.getRegn_no().trim(), ownerDetail.getState_cd(), ownerDetail.getOff_cd());
            }
            if (getScDetailsBean() != null) {
                getScDetailsBean().setRegNo(ownerDetail.getRegn_no().trim(), ownerDetail.getState_cd(), ownerDetail.getOff_cd(), smartcardStatus);
            }
            if (getVehicleHistoryDetailsBean() != null) {
                getVehicleHistoryDetailsBean().setRegNo(ownerDetail.getRegn_no().trim(), ownerDetail.getVh_class(), ownerDetail.getState_cd(), ownerDetail.getOff_cd());
            }

            if (getPermitDetailBean() != null) {
                getPermitDetailBean().setRegNo(ownerDetail.getRegn_no().trim());
            }

            Trailer_dobj trailer_dobj = trailer_Impl.set_trailer_dtls_to_dobj("", ownerDetail.getRegn_no().trim(), 0);
            if (trailer_dobj != null) {
                getTrailer_bean().set_trailer_dobj_to_bean(trailer_dobj);
            }

            InsDobj ins_dobj = InsImpl.set_ins_dtls_db_to_dobj(ownerDetail.getRegn_no().trim(), null, ownerDetail.getState_cd(), ownerDetail.getOff_cd());
            if (ins_dobj != null) {
                getIns_bean().set_Ins_dobj_to_bean(ins_dobj);
                ownerDetail.setInsDobj(ins_dobj);
            }

            HpaImpl hpa_Impl = new HpaImpl();
            List<HpaDobj> hypth = hpa_Impl.getHypothecationList(ownerDetail.getRegn_no().trim(), ownerDetail.getState_cd(), ownerDetail.getOff_cd());
            getHypothecationDetails_bean().setListHypthDetails(hypth);

            setRegStatus(ServerUtil.fillRegStatusParameters(ownerDetail));

            getIns_bean().componentReadOnly(false);
            getIns_bean().setGovtVehFlag(true);
            getTrailer_bean().componentReadOnly(false);
            setRender(true);
            showDelete = true;
            showRegistrationNo = false;
            showRegList = false;
            rendeDel_show = false;
            render_back = false;
            renderVddDup = false;

        } catch (VahanException ve) {
            ex = ve;
        } catch (Exception exp) {
            ex = exp;
        }

        if (ex != null) {
            getLOGGER().error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", Util.getLocaleMsg("regn_not_found")));
        }
    }

    public void showConfirmationBox() {
        PrimeFaces.current().executeScript("PF('ConfirmationBlock').show()");
    }

    public void deleteDuplicateRegnNo() {
        ownerDetail.getRegn_no();
        ownerDetail.getState_cd();
        ownerDetail.getOff_cd();

        if (regnNameList.size() > 1) {
            boolean isDelete = new DelDupRegnNoImpl().deleteDuplicateVehicle(ownerDetail);
            if (isDelete) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", Util.getLocaleMsg("delsucces")));
                for (int i = 0; i < regnNameList.size(); i++) {
                    if ((regnNameList.get(i).getRegn_no().equals(ownerDetail.getRegn_no()))
                            && (regnNameList.get(i).getState_cd().equals(ownerDetail.getState_cd()))
                            && (regnNameList.get(i).getOff_cd() == ownerDetail.getOff_cd())) {
                        regnNameList.remove(i);
                        break;
                    }
                }
                render = false;
                showRegList = true;
                showRegistrationNo = false;
                render_back = true;
                if (regnNameList.size() == 1) {
                    if (offCd != 0) {
                        // vddDupListRegno
                        Iterator<DelDupRegnNoDobj> itr = vddDupListRegno.iterator();
                        while (itr.hasNext()) {
                            DelDupRegnNoDobj delDupRegnNoDobj = itr.next();
                            if (ownerDetail.getRegn_no().equals(delDupRegnNoDobj.getRegnNo())) {
                                itr.remove();
                                break;
                            }
                        }
                    }
                    backToRegnNO();
                }

            } else {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", Util.getLocaleSomthingMsg()));
                return;
            }

        } else {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", Util.getLocaleSomthingMsg()));
            render = false;
            showRegList = true;
            showRegistrationNo = false;
        }
    }

    public void reStoreDuplicateRegnNo(OwnerDetailsDobj ownerDetail) {
        this.ownerDtlsDeDup = ownerDetail;
        try {
            if (!ownerDetail.getState_cd().equalsIgnoreCase(Util.getUserStateCode()) && !ownerDetail.getStatus().equalsIgnoreCase(TableConstants.VT_NOC_ISSUE_STATUS)) {
                JSFUtils.showMessage(Util.getLocaleMsg("vehRegistered") + " " + ownerDetail.getState_name() + ", " + ownerDetail.getOff_name() + "." + Util.getLocaleMsg("NoRestore"));
                return;
            } else if (!ownerDetail.getState_cd().equalsIgnoreCase(Util.getUserStateCode()) && ownerDetail.getStatus().equals("N")) {
                NocImpl nocImpl = new NocImpl();
                NocDobj nocDobj;

                nocDobj = nocImpl.getNocDetails(ownerDetail.getRegn_no(), ownerDetail.getState_cd(), ownerDetail.getOff_cd());

                if (nocDobj != null) {
                    if (!nocDobj.getState_to().equalsIgnoreCase(Util.getUserStateCode())) {
                        if (nocDobj.getOff_to() != 0) {
                            JSFUtils.showMessage(Util.getLocaleMsg("noc_taken") + " " + ServerUtil.getStateNameByStateCode(nocDobj.getState_to()) + ", " + ServerUtil.getOfficeName(nocDobj.getOff_to(), nocDobj.getState_to()) + ", " + Util.getLocaleMsg("notMoveHistory"));
                            return;
                        } else {
                            JSFUtils.showMessage(Util.getLocaleMsg("noc_taken") + " " + ServerUtil.getStateNameByStateCode(nocDobj.getState_to()) + ". " + Util.getLocaleMsg("notMoveHistory"));
                            return;
                        }
                    }
                } else {
                    JSFUtils.showMessage(Util.getLocaleMsg("noc_not_found"));
                    return;
                }
            }
        } catch (VahanException ex) {
            JSFUtils.showMessage(ex.getMessage());
        }
        if (regnDupList != null && regnDupList.size() >= 1) {
            boolean isDelete = new DelDupRegnNoImpl().reStoreDuplicateVehicle(ownerDtlsDeDup, Util.getEmpCode());
            if (isDelete) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", Util.getLocaleMsg("restoreSucces")));
                for (int i = 0; i < regnDupList.size(); i++) {
                    if ((regnDupList.get(i).getRegn_no().equals(ownerDetail.getRegn_no()))
                            && (regnDupList.get(i).getState_cd().equals(ownerDetail.getState_cd()))
                            && (regnDupList.get(i).getOff_cd() == ownerDetail.getOff_cd())) {
                        regnDupList.remove(i);
                        break;
                    }
                }
                render = false;
                showRegList = false;
                showRegistrationNo = false;
                renderDelete_Duplicate = false;
                render_back = true;
                if (regnDupList.size() == 0) {
                    showRegistrationNo = true;
                    renderDelete_Duplicate = false;
                    render = false;
                    render_back = false;
                    showRegList = false;
                    rendeDel_show = false;
                    renderVddDup = true;
                    if (offCd != 0) {
                        getVddDuplcateVeh(null);
                    }
                }

            } else {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", Util.getLocaleSomthingMsg()));
                return;
            }

        } else {
            showRegistrationNo = true;
        }
    }

    public void getVddDuplcateVeh(AjaxBehaviorEvent event) {
        try {
            restDataTable();
            vddDupListRegno = new DelDupRegnNoImpl().getDupVddRegnList(Util.getUserStateCode(), offCd);
        } catch (Exception ex) {
            getLOGGER().error(ex.toString() + " " + ex.getStackTrace()[0]);
        }

    }

    public void onRowSelect(SelectEvent event) {
        delDupRegnNoDobj = (DelDupRegnNoDobj) event.getObject();
        setRegnNo(delDupRegnNoDobj.getRegnNo());
    }

    public void onRowUnselect(UnselectEvent event) {
        delDupRegnNoDobj = (DelDupRegnNoDobj) event.getObject();
        setRegnNo("");
    }

    public void backToRegnList() {
        showRegList = true;
        render = false;
        showRegistrationNo = false;
        rendeDel_show = false;
        render_back = true;
        renderVddDup = false;

    }

    public void backToRegnNO() {
        showRegistrationNo = true;
        render = false;
        showRegList = false;
        rendeDel_show = false;
        renderDelete_Duplicate = false;
        render_back = false;
        renderVddDup = true;
        restDataTable();
    }

    /**
     * @return the regnNo
     */
    public String getRegnNo() {
        return regnNo;
    }

    /**
     * @param regnNo the regnNo to set
     */
    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    /**
     * @return the disableRegnNo
     */
    public boolean isDisableRegnNo() {
        return disableRegnNo;
    }

    /**
     * @param disableRegnNo the disableRegnNo to set
     */
    public void setDisableRegnNo(boolean disableRegnNo) {
        this.disableRegnNo = disableRegnNo;
    }

    /**
     * @return the regnNameList
     */
    public List<OwnerDetailsDobj> getRegnNameList() {
        return regnNameList;
    }

    /**
     * @param regnNameList the regnNameList to set
     */
    public void setRegnNameList(ArrayList<OwnerDetailsDobj> regnNameList) {
        this.regnNameList = regnNameList;
    }

    /**
     * @return the showRegList
     */
    public boolean isShowRegList() {
        return showRegList;
    }

    /**
     * @param showRegList the showRegList to set
     */
    public void setShowRegList(boolean showRegList) {
        this.showRegList = showRegList;
    }

    /**
     * @return the trailer_bean
     */
    public Trailer_bean getTrailer_bean() {
        return trailer_bean;
    }

    /**
     * @param trailer_bean the trailer_bean to set
     */
    public void setTrailer_bean(Trailer_bean trailer_bean) {
        this.trailer_bean = trailer_bean;
    }

    /**
     * @return the ins_bean
     */
    public InsBean getIns_bean() {
        return ins_bean;
    }

    /**
     * @param ins_bean the ins_bean to set
     */
    public void setIns_bean(InsBean ins_bean) {
        this.ins_bean = ins_bean;
    }

    /**
     * @return the hypothecationDetails_bean
     */
    public HypothecationDetailsBean getHypothecationDetails_bean() {
        return hypothecationDetails_bean;
    }

    /**
     * @param hypothecationDetails_bean the hypothecationDetails_bean to set
     */
    public void setHypothecationDetails_bean(HypothecationDetailsBean hypothecationDetails_bean) {
        this.hypothecationDetails_bean = hypothecationDetails_bean;
    }

    /**
     * @return the exArmyBean
     */
    public ExArmyBean getExArmyBean() {
        return exArmyBean;
    }

    /**
     * @param exArmyBean the exArmyBean to set
     */
    public void setExArmyBean(ExArmyBean exArmyBean) {
        this.exArmyBean = exArmyBean;
    }

    /**
     * @return the axleBean
     */
    public AxleBean getAxleBean() {
        return axleBean;
    }

    /**
     * @param axleBean the axleBean to set
     */
    public void setAxleBean(AxleBean axleBean) {
        this.axleBean = axleBean;
    }

    /**
     * @return the hsrpDetailsBean
     */
    public HSRPDetailsBean getHsrpDetailsBean() {
        return hsrpDetailsBean;
    }

    /**
     * @param hsrpDetailsBean the hsrpDetailsBean to set
     */
    public void setHsrpDetailsBean(HSRPDetailsBean hsrpDetailsBean) {
        this.hsrpDetailsBean = hsrpDetailsBean;
    }

    /**
     * @return the scDetailsBean
     */
    public SCDetailsBean getScDetailsBean() {
        return scDetailsBean;
    }

    /**
     * @param scDetailsBean the scDetailsBean to set
     */
    public void setScDetailsBean(SCDetailsBean scDetailsBean) {
        this.scDetailsBean = scDetailsBean;
    }

    /**
     * @return the vehicleHistoryDetailsBean
     */
    public VehicleHistoryDetailsBean getVehicleHistoryDetailsBean() {
        return vehicleHistoryDetailsBean;
    }

    /**
     * @param vehicleHistoryDetailsBean the vehicleHistoryDetailsBean to set
     */
    public void setVehicleHistoryDetailsBean(VehicleHistoryDetailsBean vehicleHistoryDetailsBean) {
        this.vehicleHistoryDetailsBean = vehicleHistoryDetailsBean;
    }

    /**
     * @return the permitDetailBean
     */
    public PermitDisplayDetailBean getPermitDetailBean() {
        return permitDetailBean;
    }

    /**
     * @param permitDetailBean the permitDetailBean to set
     */
    public void setPermitDetailBean(PermitDisplayDetailBean permitDetailBean) {
        this.permitDetailBean = permitDetailBean;
    }

    /**
     * @return the smartcardStatus
     */
    public boolean isSmartcardStatus() {
        return smartcardStatus;
    }

    /**
     * @param smartcardStatus the smartcardStatus to set
     */
    public void setSmartcardStatus(boolean smartcardStatus) {
        this.smartcardStatus = smartcardStatus;
    }

    /**
     * @return the hsrpStatus
     */
    public boolean isHsrpStatus() {
        return hsrpStatus;
    }

    /**
     * @param hsrpStatus the hsrpStatus to set
     */
    public void setHsrpStatus(boolean hsrpStatus) {
        this.hsrpStatus = hsrpStatus;
    }

    /**
     * @return the fitnessDobj
     */
    public FitnessDobj getFitnessDobj() {
        return fitnessDobj;
    }

    /**
     * @param fitnessDobj the fitnessDobj to set
     */
    public void setFitnessDobj(FitnessDobj fitnessDobj) {
        this.fitnessDobj = fitnessDobj;
    }

    /**
     * @return the isTransport
     */
    public boolean isIsTransport() {
        return isTransport;
    }

    /**
     * @param isTransport the isTransport to set
     */
    public void setIsTransport(boolean isTransport) {
        this.isTransport = isTransport;
    }

    /**
     * @return the render
     */
    public boolean isRender() {
        return render;
    }

    /**
     * @param render the render to set
     */
    public void setRender(boolean render) {
        this.render = render;
    }

    /**
     * @return the regStatus
     */
    public RegistrationStatusParametersDobj getRegStatus() {
        return regStatus;
    }

    /**
     * @param regStatus the regStatus to set
     */
    public void setRegStatus(RegistrationStatusParametersDobj regStatus) {
        this.regStatus = regStatus;
    }

    /**
     * @return the showBack
     */
    public boolean isShowBack() {
        return showBack;
    }

    /**
     * @param showBack the showBack to set
     */
    public void setShowBack(boolean showBack) {
        this.showBack = showBack;
    }

    /**
     * @return the ownerDetail
     */
    public OwnerDetailsDobj getOwnerDetail() {
        return ownerDetail;
    }

    /**
     * @param ownerDetail the ownerDetail to set
     */
    public void setOwnerDetail(OwnerDetailsDobj ownerDetail) {
        this.ownerDetail = ownerDetail;
    }

    /**
     * @return the showDelete
     */
    public boolean isShowDelete() {
        return showDelete;
    }

    /**
     * @param showDelete the showDelete to set
     */
    public void setShowDelete(boolean showDelete) {
        this.showDelete = showDelete;
    }

    /**
     * @return the LOGGER
     */
    public static Logger getLOGGER() {
        return LOGGER;
    }

    /**
     * @param aLOGGER the LOGGER to set
     */
    public static void setLOGGER(Logger aLOGGER) {
        LOGGER = aLOGGER;
    }

    /**
     * @return the showRegistrationNo
     */
    public boolean isShowRegistrationNo() {
        return showRegistrationNo;
    }

    /**
     * @param showRegistrationNo the showRegistrationNo to set
     */
    public void setShowRegistrationNo(boolean showRegistrationNo) {
        this.showRegistrationNo = showRegistrationNo;
    }

    /**
     * @return the listOwnerCatg
     */
    public List getListOwnerCatg() {
        return listOwnerCatg;
    }

    /**
     * @param listOwnerCatg the listOwnerCatg to set
     */
    public void setListOwnerCatg(List listOwnerCatg) {
        this.listOwnerCatg = listOwnerCatg;
    }

    /**
     * @return the statusList
     */
    public ArrayList<Status_dobj> getStatusList() {
        return statusList;
    }

    /**
     * @param statusList the statusList to set
     */
    public void setStatusList(ArrayList<Status_dobj> statusList) {
        this.statusList = statusList;
    }

    public OwnerDetailsDobj getOwnerDtlsDeDup() {
        return ownerDtlsDeDup;
    }

    public void setOwnerDtlsDeDup(OwnerDetailsDobj ownerDtlsDeDup) {
        this.ownerDtlsDeDup = ownerDtlsDeDup;
    }

    public List<OwnerDetailsDobj> getRegnDupList() {
        return regnDupList;
    }

    public void setRegnDupList(List<OwnerDetailsDobj> regnDupList) {
        this.regnDupList = regnDupList;
    }

    public boolean isRendeDel_show() {
        return rendeDel_show;
    }

    public void setRendeDel_show(boolean rendeDel_show) {
        this.rendeDel_show = rendeDel_show;
    }

    public boolean isRenderDelete_Duplicate() {
        return renderDelete_Duplicate;
    }

    public void setRenderDelete_Duplicate(boolean renderDelete_Duplicate) {
        this.renderDelete_Duplicate = renderDelete_Duplicate;
    }

    public boolean isRender_back() {
        return render_back;
    }

    public void setRender_back(boolean render_back) {
        this.render_back = render_back;
    }

    public boolean isRenderVddDup() {
        return renderVddDup;
    }

    public void setRenderVddDup(boolean renderVddDup) {
        this.renderVddDup = renderVddDup;
    }

    public List<DelDupRegnNoDobj> getVddDupListRegno() {
        return vddDupListRegno;
    }

    public void setVddDupListRegno(List<DelDupRegnNoDobj> vddDupListRegno) {
        this.vddDupListRegno = vddDupListRegno;
    }

    public void onRowToggle() {
    }

    public List getOffice_list() {
        return office_list;
    }

    public void setOffice_list(List office_list) {
        this.office_list = office_list;
    }

    public int getOffCd() {
        return offCd;
    }

    public void setOffCd(int offCd) {
        this.offCd = offCd;
    }

    public String getVddRegnNo() {
        return vddRegnNo;
    }

    public void setVddRegnNo(String vddRegnNo) {
        this.vddRegnNo = vddRegnNo;
    }

    public DelDupRegnNoDobj getDelDupRegnNoDobj() {
        return delDupRegnNoDobj;
    }

    public void setDelDupRegnNoDobj(DelDupRegnNoDobj delDupRegnNoDobj) {
        this.delDupRegnNoDobj = delDupRegnNoDobj;
    }
}
