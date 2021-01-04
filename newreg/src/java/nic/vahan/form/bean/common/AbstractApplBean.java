/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.fillVehicleParametersFromDobj;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.ApproveDisapprove_dobj;
import nic.vahan.form.dobj.BlackListedVehicleDobj;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.TaxExemptiondobj;
import nic.vahan.form.dobj.common.Appl_Details_Dobj;
import nic.vahan.form.dobj.permit.PermitDetailDobj;
import nic.vahan.form.impl.ApplicationInwardImpl;
import nic.vahan.form.impl.BlackListedVehicleImpl;
import nic.vahan.form.impl.ExemptionFeeFineImpl;
import nic.vahan.form.impl.HpaImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.SeatAllotedDetails;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.PermitDetailImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.configuration.TmConfigurationApplInwardDobj;
import nic.vahan.form.impl.NonUseImpl;
import nic.vahan.services.DmsDocCheckUtils;
import nic.vahan.services.VTDocumentModel;

public abstract class AbstractApplBean {

    private static final Logger LOGGER = Logger.getLogger(AbstractApplBean.class);
    protected ApproveDisapprove_dobj app_disapp_dobj = null;
    protected Appl_Details_Dobj appl_details = null;
    protected Map<String, String> currentdata = new HashMap();
    protected boolean render = false;
    protected boolean disappPrint = false;
    protected boolean renderExemptionOD = false;

    public AbstractApplBean() {
        List<TaxExemptiondobj> exemptionList = null;
        List<HpaDobj> hypoList = new ArrayList<>();

        try {
            SeatAllotedDetails selectedSeat = (SeatAllotedDetails) Util.getSession().getAttribute("SelectedSeat");
            app_disapp_dobj = new ApproveDisapprove_dobj();
            String[][] data = MasterTableFiller.masterTables.VM_REASON.getData();
            for (int i = 0; i < data.length; i++) {
                app_disapp_dobj.getReasonList().add(new SelectItem(data[i][0], data[i][1]));
            }
            if (selectedSeat != null) {

                appl_details = new Appl_Details_Dobj();
                appl_details.setAppl_no(selectedSeat.getAppl_no());
                appl_details.setPur_cd(selectedSeat.getPur_cd());
                appl_details.setCurrent_action_cd(selectedSeat.getAction_cd());
                appl_details.setCurrent_role(selectedSeat.getAction_cd() % 1000);// for extracting role_code from action_code
                appl_details.setAppl_dt(selectedSeat.getAppl_dt() != null ? selectedSeat.getAppl_dt() : "");
                appl_details.setCurrent_office_remark(selectedSeat.getOffice_remark());
                appl_details.setCurrent_public_remark(selectedSeat.getRemark_for_public());
                appl_details.setCurrent_status(selectedSeat.getStatus());
                appl_details.setCurrent_cntr_id(selectedSeat.getCntr_id());
                appl_details.setPur_desc(selectedSeat.getPurpose_descr());
                appl_details.setRegn_no(selectedSeat.getRegn_no());
                appl_details.setCurrent_state_cd(Util.getUserStateCode());
                appl_details.setCurrent_off_cd(Util.getSelectedSeat().getOff_cd());
                appl_details.setCurrentEmpCd(Util.getEmpCode());

                //Exemption Order Details
                exemptionList = new ExemptionFeeFineImpl().getExemptedDetails(ServerUtil.getLatestRcptNo(selectedSeat.getAppl_no(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd()));
                if (exemptionList != null && !exemptionList.isEmpty()) {
                    setRenderExemptionOD(true);
                    for (TaxExemptiondobj obj : exemptionList) {
                        appl_details.getExemptionOD().append("Order No: ").append(obj.getPermissionNo()).append("/Order Date: ").append(obj.getPermissionDt()).append("/Reason: ").append(obj.getPerpose());
                        break;
                    }
                    for (TaxExemptiondobj obj : exemptionList) {
                        appl_details.getExemptionOD().append("<br/>").append(obj.getExemHead()).append(": ").append(obj.getExemAmount());
                    }
                }

                OwnerImpl impl = new OwnerImpl();
                ApplicationInwardImpl inwardImpl = new ApplicationInwardImpl();
                if (appl_details.getOwnerDetailsDobj() == null) {
                    Status_dobj applInwardOthOffDobj = inwardImpl.getApplInwardOthOffDobj(appl_details.getAppl_no());
                    if (applInwardOthOffDobj != null) {
                        appl_details.setOwnerDetailsDobj(impl.getOwnerDetails(appl_details.getRegn_no(), applInwardOthOffDobj.getState_cd(), applInwardOthOffDobj.getOff_cd()));
                    } else {
                        appl_details.setOwnerDetailsDobj(impl.getOwnerDetails(appl_details.getRegn_no(), appl_details.getCurrent_state_cd(), appl_details.getCurrent_off_cd()));
                    }
                }

                if (appl_details.getOwnerDetailsDobj() != null) {
                    PermitDetailDobj permitDetailDobj = null;
                    if (appl_details.getOwnerDetailsDobj().getVehType() == TableConstants.VM_VEHTYPE_TRANSPORT) {
                        permitDetailDobj = PermitDetailImpl.getPermitdetailsFromRegnNo(appl_details.getRegn_no());
                    }
                    appl_details.setOwn_name(appl_details.getOwnerDetailsDobj().getOwner_name());
                    appl_details.setChasi_no(appl_details.getOwnerDetailsDobj().getChasi_no());
                    appl_details.setOwnerDobj(impl.getOwnerDobj(appl_details.getOwnerDetailsDobj()));
                    BlackListedVehicleImpl blackListedVehicleImpl = new BlackListedVehicleImpl();
                    BlackListedVehicleDobj blackListedVehicleDobj = blackListedVehicleImpl.getBlacklistedVehicleDetails(appl_details.getOwnerDetailsDobj().getRegn_no(), appl_details.getOwnerDetailsDobj().getChasi_no());
                    appl_details.getOwnerDetailsDobj().setBlackListedVehicleDobj(blackListedVehicleDobj);

                    //for knowing that if vehicle registration is expired or not
                    ApplicationInwardImpl applicationInwardImpl = new ApplicationInwardImpl();
                    appl_details.getOwnerDetailsDobj().setVehAgeExpire(applicationInwardImpl.isVehAgeExpired(appl_details.getOwnerDobj(), permitDetailDobj));
                    if (appl_details.getOwnerDetailsDobj().isVehAgeExpire()) {
                        TmConfigurationApplInwardDobj tmConfigurationApplInwardDobj = inwardImpl.getApplInwardAnywhereInStateConfig(appl_details.getCurrent_state_cd());
                        VehicleParameters vehParameters = fillVehicleParametersFromDobj(appl_details.getOwnerDobj());
                        if (tmConfigurationApplInwardDobj != null && tmConfigurationApplInwardDobj.getNcr_office() != null
                                && isCondition(replaceTagValues(tmConfigurationApplInwardDobj.getNcr_office(), vehParameters), "getNcr_office()")) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Verified that vehicle age as per state policy has been expired", "Verified that vehicle age as per state policy has been expired"));
                        }
                    }
                    if (appl_details.getOwnerDobj() != null) {
                        appl_details.setVehicleParameters(FormulaUtils.fillVehicleParametersFromDobj(appl_details.getOwnerDobj()));

                        //start of -- for showing message for displaying tax validity expired or not
                        TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();
                        boolean taxPaidOrClear = applicationInwardImpl.taxPaidOrClearedStatus(appl_details.getRegn_no(), tmConfigurationDobj, appl_details.getOwnerDobj(), TableConstants.TM_ROAD_TAX);

                        if (!taxPaidOrClear && "DL,UP".contains(appl_details.getCurrent_state_cd()) && appl_details.getOwnerDobj().getVehType() == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
                            taxPaidOrClear = true;
                        }

                        //for Skipping Tax Constraints in Particular Cases Start
                        if (!taxPaidOrClear) {
                            boolean isNonUse = NonUseImpl.nonUseDetailsExist(appl_details.getOwnerDobj().getRegn_no(), appl_details.getOwnerDobj().getState_cd());
                            if (appl_details.getPur_cd() == TableConstants.VM_MAST_RC_RELEASE
                                    || appl_details.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS
                                    || appl_details.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_FOR_OFFICE_PURPOSE
                                    || appl_details.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_NOC_CANCEL
                                    || appl_details.getPur_cd() == TableConstants.VM_MAST_VEHICLE_SCRAPE
                                    || appl_details.getPur_cd() == TableConstants.VM_DUPLICATE_TO_TAX_CARD
                                    || appl_details.getPur_cd() == TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE
                                    || (isNonUse && appl_details.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_REM_HYPO)
                                    || (isNonUse && appl_details.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_ADD_HYPO)) {
                                taxPaidOrClear = true;
                            }

                            if (appl_details.getOwnerDobj().getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)
                                    && appl_details.getCurrent_state_cd().equalsIgnoreCase("DL")
                                    && appl_details.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION) {
                                //for handling case when vehicle comes for Vehicle Conversion within same state to different RTO
                                taxPaidOrClear = applicationInwardImpl.taxPaidOrClearedStatusOnVehType(appl_details.getRegn_no());
                            } else if ("JH".contains(appl_details.getCurrent_state_cd()) && appl_details.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_REN_REG) {
                                taxPaidOrClear = true;
                            } else if ("OR".contains(appl_details.getCurrent_state_cd()) && (appl_details.getPur_cd() == TableConstants.VM_MAST_RC_CANCELLATION || appl_details.getPur_cd() == TableConstants.VM_MAST_NON_USE) && isNonUse) {
                                taxPaidOrClear = true;
                            }
                        }//for Skipping Tax Constraints in Particular Cases End

                        if (!taxPaidOrClear) {
                            appl_details.setTaxValidityExpired(true);
                        }
                        //end of -- for showing message for displaying tax validity expired or not
                    }

                    //Hypothecation Details
                    hypoList = new HpaImpl().getHypoDetails(null, TableConstants.VM_TRANSACTION_MAST_REM_HYPO, appl_details.getRegn_no(), true, appl_details.getOwnerDobj().getState_cd());
                    if (!hypoList.isEmpty()) {
                        appl_details.setRenderHypo(true);
                        appl_details.getHyptoDtls().append("Hypothecation Details : ").append(hypoList.get(0).getFncr_name()).append(",").append(hypoList.get(0).getFncr_add1()).append(",").append(hypoList.get(0).getFncr_add2()).append(",").append(hypoList.get(0).getFncr_add3()).append(",").append(hypoList.get(0).getFncr_district_descr()).append(",").append(hypoList.get(0).getFncr_state_name()).append(",").append(hypoList.get(0).getFncr_pincode());
                    }

                }

                app_disapp_dobj.setNew_status(TableConstants.STATUS_COMPLETE);
                app_disapp_dobj.setPrevRoleLabelValue(ServerUtil.getPrevRole(appl_details.getCurrent_action_cd(), appl_details.getPur_cd(), appl_details.getCurrent_state_cd()));

                if (app_disapp_dobj.getPrevRoleLabelValue().isEmpty()) {
                    app_disapp_dobj.setRenderRevert(false);
                } else {
                    app_disapp_dobj.setRenderRevert(true);
                }

                if (appl_details != null && appl_details.getCurrent_status() != null) {
                    if (appl_details.getCurrent_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                        setDisappPrint(true);
                        app_disapp_dobj.setAssignedReasons(ServerUtil.getReasonsForHolding(appl_details.getAppl_no()));
                    } else {
                        setDisappPrint(false);
                    }
                }
            }
            // for view button 
            if (appl_details != null) {
                boolean isDocsUpload = new ApplicationInwardImpl().getRegisteredVehDocView(appl_details.getRegn_no(), Util.getUserStateCode(), appl_details.getPur_cd());
                if (isDocsUpload) {
                    appl_details.setDocumentUploadShow(true);
                    if (Util.getTmConfiguration() != null && Util.getTmConfiguration().isAllowFacelessService()) {
                        if (Util.getTmConfiguration().getTmConfigDmsDobj() != null && (Util.getTmConfiguration().getTmConfigDmsDobj().getVerfyActionCd().contains("," + appl_details.getCurrent_action_cd() + ","))
                                || (Util.getTmConfiguration().getTmConfigDmsDobj().getApproveActionCd().contains("," + appl_details.getCurrent_action_cd() + ","))) {
                            boolean isFacelessDocPurCd = new ApplicationInwardImpl().getRegisteredVehFacelessDocView(appl_details.getRegn_no(), Util.getUserStateCode(), appl_details.getPur_cd());
                            if (isFacelessDocPurCd) {
                                appl_details.setDocVerifyForFacelessAppl(true);
                            }
                        }
                    }
                }
            }
        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vex.getMessage(), vex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public String getKeyData(String str) {
        return currentdata.get(str);
    }

    public List getCurrentInfoLabel() {
        ArrayList<String> arr = new ArrayList<>();
        java.util.Set s = currentdata.keySet();
        Iterator itr = s.iterator();
        for (Iterator it = s.iterator(); it.hasNext();) {
            Object object = it.next();
            arr.add(String.valueOf(object));
        }

        return arr;
    }

    public void reverBackListener(AjaxBehaviorEvent e) {
        if (app_disapp_dobj.getNew_status().trim().equalsIgnoreCase(TableConstants.STATUS_DISPATCH_PENDING)) {
            PrimeFaces.current().executeScript("PF('reasonDlg').show();");
        } else {
            PrimeFaces.current().executeScript("PF('reasonDlg').hide();");
        }
        if (app_disapp_dobj.getNew_status().equalsIgnoreCase(TableConstants.STATUS_REVERT)) {
            app_disapp_dobj.setRenderRevert(true);
            app_disapp_dobj.setRenderRevertList(true);
        } else {
            app_disapp_dobj.setRenderRevertList(false);
        }

    }

    public void saveWithReason() {
        String selectedReasons = "";
        if (app_disapp_dobj.getNew_status().trim().equalsIgnoreCase(TableConstants.STATUS_DISPATCH_PENDING)) {
            for (Object resn : app_disapp_dobj.getSelectedReasonList()) {
                selectedReasons = selectedReasons + String.valueOf(resn) + ",";
            }
            if (selectedReasons.endsWith(",")) {
                selectedReasons = selectedReasons.substring(0, selectedReasons.length() - 1);
            }
            app_disapp_dobj.setPublic_remark(selectedReasons);
            PrimeFaces.current().executeScript("PF('reasonDlg').hide();");
        }
    }

    public String disapprovalPrint() {
        if (appl_details != null) {
            HttpSession ses = Util.getSession();
            ses.setAttribute("applNo", appl_details.getAppl_no());
            ses.setAttribute("regnNo", appl_details.getRegn_no());
            ses.setAttribute("purCode", appl_details.getPur_cd());
            return "disAppNotice";
        }
        return "";
    }

    public void viewUploadedDocuments() {
        try {
            String dmsURL = ServerUtil.getVahanPgiUrl(TableConstants.DMS_CON_VER);
            if (dmsURL.isEmpty()) {
                throw new VahanException("Problem in view documents.");
            }
            dmsURL = dmsURL.replace("ApplNo", appl_details.getAppl_no());
            dmsURL = dmsURL.replace("ApplStatus", TableConstants.DOCUMENT_VIEW_STATUS);
            dmsURL = dmsURL + TableConstants.SECURITY_KEY;
            appl_details.setDmsFileUploadUrl(dmsURL);
            PrimeFaces.current().ajax().update("app_disapp_new_form:viewUploadedDmsReg");
            PrimeFaces.current().executeScript("PF('dmsfileUploadedReg').show();");

        } catch (VahanException ex) {
            JSFUtils.setFacesMessage(ex.getMessage(), ex.getMessage(), JSFUtils.WARN);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void verifyAndViewUploadedDocuments() {
        int fileCount = 0;
        String documentStauts = null;
        String dmsFileUploadUrl = null;
        try {
            List<VTDocumentModel> uploadedDocList = DmsDocCheckUtils.getUploadedDocumentList(appl_details.getAppl_no());
            if (uploadedDocList != null && !uploadedDocList.isEmpty() && uploadedDocList.size() > 0) {
                if (Util.getTmConfiguration().getTmConfigDmsDobj().getVerfyActionCd().contains("," + appl_details.getCurrent_action_cd() + ",")) {
                    fileCount = uploadedDocList.size();
                    documentStauts = TableConstants.DOCUMENT_RTO_VERIFICATION;
                } else if (Util.getTmConfiguration().getTmConfigDmsDobj().getApproveActionCd().contains("," + appl_details.getCurrent_action_cd() + ",")) {
                    fileCount = uploadedDocList.size();
                    documentStauts = TableConstants.DOCUMENT_RTO_APPROVAL;
                }

                if (fileCount > 0 && !documentStauts.equals("")) {
                    dmsFileUploadUrl = ServerUtil.getVahanPgiUrl(TableConstants.DMS_CON_VER);
                    if (dmsFileUploadUrl.isEmpty()) {
                        throw new VahanException("Problem in view documents.");
                    }
                    dmsFileUploadUrl = dmsFileUploadUrl.replace("ApplNo", appl_details.getAppl_no());
                    dmsFileUploadUrl = dmsFileUploadUrl.replace("ApplStatus", documentStauts);
                    dmsFileUploadUrl = dmsFileUploadUrl + TableConstants.SECURITY_KEY;
                    appl_details.setDmsFileUploadUrl(dmsFileUploadUrl);
                    PrimeFaces.current().ajax().update("app_disapp_new_form:viewUploadedDmsReg");
                    PrimeFaces.current().executeScript("PF('dmsfileUploadedReg').show();");
                } else {
                    throw new VahanException("Problem in DMS Configuiration Utility");
                }
            } else {
                throw new VahanException("No Documents Uploaded");
            }
        } catch (VahanException ex) {
            JSFUtils.setFacesMessage(ex.getMessage(), ex.getMessage(), JSFUtils.WARN);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    /**
     * @return the app_disapp_dobj
     */
    public ApproveDisapprove_dobj getApp_disapp_dobj() {
        return app_disapp_dobj;
    }

    /**
     * @param app_disapp_dobj the app_disapp_dobj to set
     */
    public void setApp_disapp_dobj(ApproveDisapprove_dobj app_disapp_dobj) {
        this.app_disapp_dobj = app_disapp_dobj;
    }

    /**
     * @return the appl_details
     */
    public Appl_Details_Dobj getAppl_details() {
        return appl_details;
    }

    /**
     * @param appl_details the appl_details to set
     */
    public void setAppl_details(Appl_Details_Dobj appl_details) {
        this.appl_details = appl_details;
    }

    /**
     * @return the currentdata
     */
    public Map<String, String> getCurrentdata() {
        return currentdata;
    }

    /**
     * @param currentdata the currentdata to set
     */
    public void setCurrentdata(Map<String, String> currentdata) {
        this.currentdata = currentdata;
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

    public boolean isDisappPrint() {
        return disappPrint;
    }

    public void setDisappPrint(boolean disappPrint) {
        this.disappPrint = disappPrint;
    }

    /**
     * @return the renderExemptionOD
     */
    public boolean isRenderExemptionOD() {
        return renderExemptionOD;
    }

    /**
     * @param renderExemptionOD the renderExemptionOD to set
     */
    public void setRenderExemptionOD(boolean renderExemptionOD) {
        this.renderExemptionOD = renderExemptionOD;
    }
}
