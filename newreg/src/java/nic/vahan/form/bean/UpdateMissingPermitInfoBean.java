/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import nic.rto.vahan.common.VahanException;
import static nic.vahan.CommonUtils.FormulaUtils.fillVehicleParametersFromDobj;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.bean.permit.PermitPanelBean;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.impl.ApproveImpl;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.SeatAllotedDetails;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.PassengerPermitDetailImpl;
import nic.vahan.form.impl.permit.PermitImpl;
import nic.vahan.form.impl.UpdateMissingPermitInfoImpl;
import nic.vahan.form.impl.admin.OwnerAdminImpl;
import nic.vahan.server.CommonUtils;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Divya Kamboj
 */
@ManagedBean(name = "updatemissingpermitinfo")
@ViewScoped
public class UpdateMissingPermitInfoBean extends AbstractApplBean implements ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(UpdateMissingPermitInfoBean.class);
    private String regn_no;
    private SessionVariables sessionVariables = null;
    @ManagedProperty(value = "#{approveImpl}")
    private ApproveImpl approveImpl;
    @ManagedProperty(value = "#{permitPanelBean}")
    private PermitPanelBean permitPanelBean;
    private Owner_dobj ownerDobj;
    private OwnerDetailsDobj ownerDetailsDobj;
    private String registrationDetails;
    private List pmt_type_list = new ArrayList();
    private List<SelectItem> pmt_catg_list = new ArrayList();
    private List<SelectItem> pmt_service_type_list = new ArrayList();
    private boolean renderPermitOutputPanel = false;
    private boolean renderRegnno = false;
    private boolean tripsRouteLengthRqrd = false;
    private PassengerPermitDetailDobj updateMissingPermitInfoDobj = null;
    private PassengerPermitDetailDobj spareTaxDtlsDobj_prvs = null;
    private boolean renderSaveBtn = false;
    private boolean renderSaveAndFileMoveBtn = false;
    private PermitImpl permit_Impl = new PermitImpl();
    private OwnerImpl ownerImpl = new OwnerImpl();
    private int tabIndex;
    private String appl_no;
    private List<ComparisonBean> compareChanges = new ArrayList<>();
    private String app_disapp = "A";
    private boolean rendervehiclenumber = true;
    private List<ComparisonBean> listPreviousChanges;
    private PassengerPermitDetailDobj permitDobjPrev = null;
    private UpdateMissingPermitInfoImpl updatePermitInfo = new UpdateMissingPermitInfoImpl();

    @PostConstruct
    public void Init() {
        SeatAllotedDetails selectedSeat = (SeatAllotedDetails) Util.getSession().getAttribute("SelectedSeat");
        sessionVariables = new SessionVariables();
        setRenderRegnno(false);
        try {
            if (sessionVariables == null || sessionVariables.getStateCodeSelected() == null) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Something went wrong, Please try again...", "Something went wrong, Please try again...");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }
            appl_details.setAppl_no(selectedSeat.getAppl_no());
            appl_details.setPur_cd(selectedSeat.getPur_cd());
            appl_details.setCurrent_action_cd(selectedSeat.getAction_cd());
            if (appl_details.getCurrent_action_cd() == TableConstants.UPDATE_MISSING_PERMIT_VERIFY || appl_details.getCurrent_action_cd() == TableConstants.UPDATE_MISSING_PERMIT_APPROVAL) {
                regn_no = appl_details.getRegn_no();
                appl_no = appl_details.getAppl_no();
                showPermitDetails();
                renderPermitOutputPanel = true;
                rendervehiclenumber = false;
                permitPanelBean.setPermitDobj(updatePermitInfo.getSpareTaxParameterDetailsFromVa(appl_no));
                permitPanelBean.onSelectPermitType(null);
                permitDobjPrev = (PassengerPermitDetailDobj) permitPanelBean.getPermitDobj().clone();
                updateMissingPermitInfoDobj = permitPanelBean.getPermitDobj();

            }
            listPreviousChanges = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
        } catch (VahanException ve) {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }

    }

    public void showPermitDetails() throws VahanException {
        OwnerDetailsDobj ownDtls = null;
        if (Utility.isNullOrBlank(regn_no)) {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please Enter Valid Registration Number!", "Please Enter Valid Registration Number!");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        String checkPending = new OwnerAdminImpl().checkPending(regn_no, sessionVariables.getStateCodeSelected());
        if (checkPending != null && appl_details.getCurrent_action_cd() == TableConstants.UPDATE_MISSING_PERMIT_ENTRY) {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, checkPending, checkPending);
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        regn_no = regn_no.toUpperCase();
        try {
            List<OwnerDetailsDobj> listOwnerDetails = ownerImpl.getOwnerDetailsList(this.regn_no.trim(), Util.getUserStateCode());
            if (listOwnerDetails == null || listOwnerDetails.isEmpty()) {
                throw new VahanException("Vehicle Details Not Found");
            }

            if (listOwnerDetails.size() == 1) {
                ownDtls = listOwnerDetails.get(0);
            }
            if (ownDtls.getOff_cd() != Util.getSelectedSeat().getOff_cd()) {
                throw new VahanException("You are not authorized to Enter the Spare Tax Details for this vehicle no.");
            }
            if (ownDtls.getStatus().equals("N")) {
                throw new VahanException(" Cannot move further due to NOC is Issued");
            }
            if (ownDtls.getStatus().equals(TableConstants.VT_RC_CANCEL_STATUS)) {
                throw new VahanException(" Cannot move further due to RC is Cancelled");
            }
            ownerDetailsDobj = ownDtls;
            ownerDobj = ownerImpl.getOwnerDobj(ownerDetailsDobj);
            if (ownerDobj != null) {
                PassengerPermitDetailImpl permitImpl = new PassengerPermitDetailImpl();
                PassengerPermitDetailDobj permitDob = permitImpl.set_vt_permit_regnNo_to_dobj(regn_no, "");
                if (permitDob != null) {
                    PrimeFaces.current().ajax().update("spare_permit_subview:db_reopened");
                    PrimeFaces.current().ajax().update("spare_permit_subview:dialog_id1");
                    PrimeFaces.current().executeScript("PF('rec_alert').show();");
                    return;
                }
                renderPermitOutputPanel = true;
                if (appl_details.getCurrent_action_cd() == TableConstants.UPDATE_MISSING_PERMIT_ENTRY) {
                    renderSaveBtn = true;
                } else {
                    renderSaveAndFileMoveBtn = true;
                }
                renderRegnno = true;
                updateMissingPermitInfoDobj = permitPanelBean.getPermitDobj();
            }
        } catch (VahanException ex) {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    @Override
    public String save() {
        String return_location = "";
        try {
            compareChanges = compareChanges();
            if (compareChanges != null && !compareChanges.isEmpty()) {
                updatePermitInfo.saveChangeData(permitPanelBean.getPermitDobj(), ComparisonBeanImpl.changedDataContents(compareChanges), sessionVariables.getEmpCodeLoggedIn(), appl_no, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
            }
            return_location = "seatwork";
        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vex.getMessage(), vex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
        return return_location;
    }

    @Override
    public String saveAndMoveFile() {
        String return_location = "";
        Status_dobj status = null;
        try {
            status = setStatusDobj();
            FacesMessage message = null;
            if (permitPanelBean.isTripsRouteLengthRqrd() && (CommonUtils.isNullOrBlank(updateMissingPermitInfoDobj.getServices_TYPE()) || Integer.parseInt(updateMissingPermitInfoDobj.getServices_TYPE()) == -1)) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Service Type Cannot be Blank  ", "Service Type Cannot be Blank ");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return null;
            }
            compareChanges = compareChanges();
            updatePermitInfo.saveChangeDataAndFileMove(permitPanelBean.getPermitDobj(), ComparisonBeanImpl.changedDataContents(compareChanges), sessionVariables.getEmpCodeLoggedIn(), appl_no, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), status);
            return_location = "seatwork";
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-File Could Not Save and Move Due to Technical Error in Database", "Error-File Could Not Save and Move Due to Technical Error in Database"));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
        return return_location;
    }

    public void savePermitMissingInfo() {
        try {
            getUpdateMissingPermitInfoDobj().setRegnNo(regn_no);
            getUpdateMissingPermitInfoDobj().setState_cd(Util.getUserStateCode());
            getUpdateMissingPermitInfoDobj().setOff_cd(ownerDetailsDobj.getOff_cd() + "");
            VehicleParameters vehParameters = fillVehicleParametersFromDobj(ownerDobj);
            boolean result = updatePermitInfo.saveMissingPermitInfo(getUpdateMissingPermitInfoDobj(), vehParameters, appl_details.getCurrent_action_cd());
            if (result) {
                PrimeFaces.current().ajax().update("spare_permit_subview:db_reopened");
                PrimeFaces.current().executeScript("PF('rec_confrm').show()");
            } else {
                throw new VahanException("Problem in saving data.");
            }
        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vex.getMessage(), vex.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public Status_dobj setStatusDobj() {
        Status_dobj status = new Status_dobj();
        try {
            Date dt = new Date();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setPur_cd(TableConstants.MISSING_PERMIT_INFO_PUR_CD);
            status.setRegn_no(appl_details.getRegn_no());
            status.setConfirm_date(dt);
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            status.setConfirm_ip(request.getRemoteAddr());
            status.setEntry_ip(request.getRemoteAddr());
            status.setOff_cd(appl_details.getCurrent_off_cd());
            status.setState_cd(appl_details.getCurrent_state_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            status.setEmp_cd(appl_details.getCurrent_emp_cd());
            status.setAction_cd(appl_details.getCurrent_action_cd());

            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                    || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)) {

                status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                status.setVehicleParameters(appl_details.getVehicleParameters());
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
        return status;
    }

    /**
     * @return the permit_Impl
     */
    public PermitImpl getPermit_Impl() {
        return permit_Impl;
    }

    /**
     * @param permit_Impl the permit_Impl to set
     */
    public void setPermit_Impl(PermitImpl permit_Impl) {
        this.permit_Impl = permit_Impl;
    }

    /**
     * @return the permitPanelBean
     */
    public PermitPanelBean getPermitPanelBean() {
        return permitPanelBean;
    }

    /**
     * @param permitPanelBean the permitPanelBean to set
     */
    public void setPermitPanelBean(PermitPanelBean permitPanelBean) {
        this.permitPanelBean = permitPanelBean;
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
     * @return the ownerDobj
     */
    public Owner_dobj getOwnerDobj() {
        return ownerDobj;
    }

    /**
     * @return the ownerDetailsDobj
     */
    public OwnerDetailsDobj getOwnerDetailsDobj() {
        return ownerDetailsDobj;
    }

    /**
     * @param ownerDobj the ownerDobj to set
     */
    public void setOwnerDobj(Owner_dobj ownerDobj) {
        this.ownerDobj = ownerDobj;
    }

    /**
     * @param ownerDetailsDobj the ownerDetailsDobj to set
     */
    public void setOwnerDetailsDobj(OwnerDetailsDobj ownerDetailsDobj) {
        this.ownerDetailsDobj = ownerDetailsDobj;
    }

    /**
     * @return the ownerImpl
     */
    public OwnerImpl getOwnerImpl() {
        return ownerImpl;
    }

    /**
     * @param ownerImpl the ownerImpl to set
     */
    public void setOwnerImpl(OwnerImpl ownerImpl) {
        this.ownerImpl = ownerImpl;
    }

    /**
     * @return the registrationDetails
     */
    public String getRegistrationDetails() {
        return registrationDetails;
    }

    /**
     * @param registrationDetails the registrationDetails to set
     */
    public void setRegistrationDetails(String registrationDetails) {
        this.registrationDetails = registrationDetails;
    }

    /**
     * @return the pmt_type_list
     */
    public List getPmt_type_list() {
        return pmt_type_list;
    }

    /**
     * @param pmt_type_list the pmt_type_list to set
     */
    public void setPmt_type_list(List pmt_type_list) {
        this.pmt_type_list = pmt_type_list;
    }

    /**
     * @return the pmt_catg_list
     */
    public List<SelectItem> getPmt_catg_list() {
        return pmt_catg_list;
    }

    /**
     * @param pmt_catg_list the pmt_catg_list to set
     */
    public void setPmt_catg_list(List<SelectItem> pmt_catg_list) {
        this.pmt_catg_list = pmt_catg_list;
    }

    /**
     * @return the pmt_service_type_list
     */
    public List<SelectItem> getPmt_service_type_list() {
        return pmt_service_type_list;
    }

    /**
     * @param pmt_service_type_list the pmt_service_type_list to set
     */
    public void setPmt_service_type_list(List<SelectItem> pmt_service_type_list) {
        this.pmt_service_type_list = pmt_service_type_list;
    }

    /**
     * @return the renderPermitOutputPanel
     */
    public boolean isRenderPermitOutputPanel() {
        return renderPermitOutputPanel;
    }

    /**
     * @param renderPermitOutputPanel the renderPermitOutputPanel to set
     */
    public void setRenderPermitOutputPanel(boolean renderPermitOutputPanel) {
        this.renderPermitOutputPanel = renderPermitOutputPanel;
    }

    /**
     * @return the tripsRouteLengthRqrd
     */
    public boolean isTripsRouteLengthRqrd() {
        return tripsRouteLengthRqrd;
    }

    /**
     * @param tripsRouteLengthRqrd the tripsRouteLengthRqrd to set
     */
    public void setTripsRouteLengthRqrd(boolean tripsRouteLengthRqrd) {
        this.tripsRouteLengthRqrd = tripsRouteLengthRqrd;
    }

    /**
     * @return the sparePermitDtlsDobj
     */
    /**
     * @return the renderSaveBtn
     */
    public boolean isRenderSaveBtn() {
        return renderSaveBtn;
    }

    /**
     * @param renderSaveBtn the renderSaveBtn to set
     */
    public void setRenderSaveBtn(boolean renderSaveBtn) {
        this.renderSaveBtn = renderSaveBtn;
    }

    public PassengerPermitDetailDobj getUpdateMissingPermitInfoDobj() {
        return updateMissingPermitInfoDobj;
    }

    public void setUpdateMissingPermitInfoDobj(PassengerPermitDetailDobj updateMissingPermitInfoDobj) {
        this.updateMissingPermitInfoDobj = updateMissingPermitInfoDobj;
    }

    public PassengerPermitDetailDobj getSpareTaxDtlsDobj_prvs() {
        return spareTaxDtlsDobj_prvs;
    }

    public void setSpareTaxDtlsDobj_prvs(PassengerPermitDetailDobj spareTaxDtlsDobj_prvs) {
        this.spareTaxDtlsDobj_prvs = spareTaxDtlsDobj_prvs;
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        if (permitDobjPrev == null) {
            return compareChanges;
        }
        Compare("Permit Category", permitDobjPrev.getPmtCatg(), permitPanelBean.getPermitDobj().getPmtCatg(), getCompareChanges());
        Compare("Permit Type", permitDobjPrev.getPmt_type_code(), permitPanelBean.getPermitDobj().getPmt_type_code(), getCompareChanges());
        Compare("Service Type", permitDobjPrev.getServices_TYPE(), permitPanelBean.getPermitDobj().getServices_TYPE(), getCompareChanges());
        Compare("Route Length", permitDobjPrev.getRout_length(), permitPanelBean.getPermitDobj().getRout_length(), getCompareChanges());
        Compare("No of Trips", permitDobjPrev.getNumberOfTrips(), permitPanelBean.getPermitDobj().getNumberOfTrips(), getCompareChanges());
        return compareChanges;
    }

    @Override
    public List<ComparisonBean> getCompBeanList() {
        return null;
    }

    @Override
    public void saveEApplication() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the tabIndex
     */
    public int getTabIndex() {
        return tabIndex;
    }

    /**
     * @param tabIndex the tabIndex to set
     */
    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    /**
     * @return the app_disapp
     */
    public String getApp_disapp() {
        return app_disapp;
    }

    /**
     * @param app_disapp the app_disapp to set
     */
    public void setApp_disapp(String app_disapp) {
        this.app_disapp = app_disapp;
    }

    /**
     * @return the approveImpl
     */
    public ApproveImpl getApproveImpl() {
        return approveImpl;
    }

    /**
     * @param approveImpl the approveImpl to set
     */
    public void setApproveImpl(ApproveImpl approveImpl) {
        this.approveImpl = approveImpl;
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

//    @Override
//    public List<ComparisonBean> getCompBeanList() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the rendervehiclenumber
     */
    public boolean isRendervehiclenumber() {
        return rendervehiclenumber;
    }

    /**
     * @param rendervehiclenumber the rendervehiclenumber to set
     */
    public void setRendervehiclenumber(boolean rendervehiclenumber) {
        this.rendervehiclenumber = rendervehiclenumber;
    }

    /**
     * @return the listPreviousChanges
     */
    public List<ComparisonBean> getListPreviousChanges() {
        return listPreviousChanges;
    }

    /**
     * @param listPreviousChanges the listPreviousChanges to set
     */
    public void setListPreviousChanges(List<ComparisonBean> listPreviousChanges) {
        this.listPreviousChanges = listPreviousChanges;
    }

    /**
     * @return the compareChanges
     */
    public List<ComparisonBean> getCompareChanges() {
        return compareChanges;
    }

    /**
     * @param compareChanges the compareChanges to set
     */
    public void setCompareChanges(List<ComparisonBean> compareChanges) {
        this.compareChanges = compareChanges;
    }

    /**
     * @return the renderRegnno
     */
    public boolean isRenderRegnno() {
        return renderRegnno;
    }

    /**
     * @param renderRegnno the renderRegnno to set
     */
    public void setRenderRegnno(boolean renderRegnno) {
        this.renderRegnno = renderRegnno;
    }

    /**
     * @return the renderSaveAndFileMoveBtn
     */
    public boolean isRenderSaveAndFileMoveBtn() {
        return renderSaveAndFileMoveBtn;
    }

    /**
     * @param renderSaveAndFileMoveBtn the renderSaveAndFileMoveBtn to set
     */
    public void setRenderSaveAndFileMoveBtn(boolean renderSaveAndFileMoveBtn) {
        this.renderSaveAndFileMoveBtn = renderSaveAndFileMoveBtn;
    }
}
