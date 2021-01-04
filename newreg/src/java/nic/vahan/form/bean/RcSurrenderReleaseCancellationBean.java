/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.RcSurrenderReleaseCancellationDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.RcSurrenderReleaseCancellationImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author nicsi
 */
@ManagedBean(name = "rcSurrender")
@ViewScoped
public class RcSurrenderReleaseCancellationBean extends AbstractApplBean implements ApproveDisapproveInterface, Serializable {

    private static final Logger LOGGER = Logger.getLogger(RcSurrenderReleaseCancellationBean.class);
    private String checkValueOfRcAction;
    private boolean panelSurrenderDetailsDisable;
    private boolean panelReleaseDetailsDisable;
    private boolean panelReleaseSuspensionUpto = false;
    private boolean panelSuspensionDetailsDisable = false;
    private boolean panelCancellationDetailsDisable;
    private boolean paneldocumentSurrenderDetailsDisable;
    private String recieptOrInwordNO;
    private String vehicleNo;
    private String vehicleStatus;
    private String chassisNo;
    private String engNo;
    private String manuMonth;
    private String manuYear;
    private String address1;
    private String vhClassDescr;
    private String vhCatg;
    private String bdType;
    private String regnDt;
    private String ownerName;
    private String address;
    private String vehicleClass;
    private Date fitnessValidity;
    private List listVehicleClass = new ArrayList();
    private Date surrenderDate;
    private Date suspendedUptoDate;
    private String approvedBy;
    private String fileReferenceNo;
    private String reason;
    private String releaseApprovedBy;
    private String releaseFileReferenceNo;
    private Date releaseDate;
    private Date cancellationDate;
    private String cancellationApprovedBy;
    private String cancellationFileReferenceNo;
    private String cancellationReason;
    boolean cbxRc;
    boolean cbxPermit;
    boolean cbxFitness;
    boolean cbxTaxExamption;
    boolean cbxRcDisabled;
    boolean cbxPermitDisabled;
    boolean cbxFitnessDisabled;
    boolean cbxTaxExamptionDisabled;
    private String rcNo;
    private String permitNo;
    private String fitnessCerNo;
    private String rcNoStatus;
    private String permitNoStatus;
    private String fitnessCerNoStatus;
    private String taxExampStatus;
    private boolean rcNoDisabled;
    private boolean permitNoDisabled;
    private boolean fitnessCerNoDisabled;
    private String rcCnclSurrndr = "";
    private int rcCnclBy = 1;
    private boolean rcCnclByDisable = false;
    private boolean rcCnclDisable;
    private boolean rcSurdisable;
    private boolean rcSuspdisable;
    private boolean renderRcCnclSurrndr;
    private boolean renderRcCnclBy;
    private boolean renderPnlRcCnclSurrndr = true;
    private String surrCnclName;
    RcSurrenderReleaseCancellationDobj dobj = new RcSurrenderReleaseCancellationDobj();
    RcSurrenderReleaseCancellationDobj dobjPrevious = new RcSurrenderReleaseCancellationDobj();
    RcSurrenderReleaseCancellationImpl impl = new RcSurrenderReleaseCancellationImpl();
    int purCode;
    int actionCode;
    private List<ComparisonBean> compBeanList = new ArrayList<ComparisonBean>();
    private List<ComparisonBean> prevChangedDataList;
    private Date maxDate = new Date();
    private Date minDate = DateUtil.parseDate(DateUtil.getCurrentDate());
    private List<RcSurrenderReleaseCancellationDobj> surr_list;
    private String inwardSusSur = null;
    private String OffCdDesr = "";
    private boolean rendeRcCnclReason;
    private List listReason = new ArrayList();
    private String currentDate = "";

    public RcSurrenderReleaseCancellationBean() {
        try {
            purCode = appl_details.getPur_cd();
            actionCode = appl_details.getCurrent_action_cd();
            vehicleNo = appl_details.getRegn_no();
            vehicleStatus = appl_details.getOwnerDetailsDobj().getStatus();
            String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
            listVehicleClass.add(new SelectItem("-1", "---Select---"));
            OffCdDesr = ServerUtil.getOfficeName(appl_details.getCurrent_off_cd(), appl_details.getCurrent_state_cd());
            currentDate = DateUtils.parseDate(new Date());
            engNo = appl_details.getOwnerDobj().getEng_no();
            manuMonth = String.valueOf(appl_details.getOwnerDobj().getManu_mon());
            manuYear = String.valueOf(appl_details.getOwnerDobj().getManu_yr());
            vhClassDescr = ServerUtil.getVehicleClassDescr(appl_details.getOwnerDobj().getVh_class());
            vhCatg = appl_details.getOwnerDobj().getVch_catg();
            bdType = appl_details.getOwnerDobj().getBody_type();
            regnDt = DateUtils.parseDate(appl_details.getOwnerDobj().getRegn_dt());

            for (String[] data1 : data) {
                listVehicleClass.add(new SelectItem(data1[0], data1[1]));
            }
            if (purCode == TableConstants.VM_MAST_RC_SURRENDER && actionCode == TableConstants.VM_ROLE_RC_SURRENDER_ENTRY) {
                checkValueOfRcAction = "rcsurrender";
                setPanelSurrenderDetailsDisable(true);
                setPanelReleaseDetailsDisable(false);
                setPaneldocumentSurrenderDetailsDisable(true);
                setPanelCancellationDetailsDisable(false);
                setCbxFitnessDisabled(false);
                setCbxPermitDisabled(false);
                setCbxRcDisabled(false);
                setCbxTaxExamptionDisabled(false);
                getDataForRcSurrenderVerificationOrApproval(purCode, actionCode, vehicleNo);

            } else if (purCode == TableConstants.VM_MAST_RC_SURRENDER && (actionCode == TableConstants.VM_ROLE_RC_SURRENDER_VERIFICATION || actionCode == TableConstants.VM_ROLE_RC_SURRENDER_APPROVAL)) {
                checkValueOfRcAction = "rcsurrender";
                setPanelSurrenderDetailsDisable(true);
                setPanelReleaseDetailsDisable(false);
                setPaneldocumentSurrenderDetailsDisable(true);
                setPanelCancellationDetailsDisable(false);
                setCbxFitnessDisabled(false);
                setCbxPermitDisabled(false);
                setCbxRcDisabled(false);
                setCbxTaxExamptionDisabled(false);
                getDataForRcSurrenderVerificationOrApproval(purCode, actionCode, vehicleNo);
            }
            if (purCode == TableConstants.VM_MAST_RC_RELEASE && actionCode == TableConstants.VM_ROLE_RC_RELEASE_ENTRY) {
                checkValueOfRcAction = "rcrelease";
                setPanelSurrenderDetailsDisable(false);
                setPanelCancellationDetailsDisable(false);
                setCbxFitnessDisabled(true);
                setCbxPermitDisabled(true);
                setCbxRcDisabled(true);
                setCbxTaxExamptionDisabled(true);
                setPanelReleaseDetailsDisable(false);
                setPaneldocumentSurrenderDetailsDisable(false);
                getDataForRcSurrenderVerificationOrApproval(purCode, actionCode, vehicleNo);

            } else if (purCode == TableConstants.VM_MAST_RC_RELEASE && (actionCode == TableConstants.VM_ROLE_RC_RELEASE_VERIFICATION || actionCode == TableConstants.VM_ROLE_RC_RELEASE_APPROVAL)) {
                checkValueOfRcAction = "rcrelease";
                setPanelReleaseDetailsDisable(true);
                setPanelSurrenderDetailsDisable(false);
                setPaneldocumentSurrenderDetailsDisable(true);
                setPanelCancellationDetailsDisable(false);
                setCbxFitnessDisabled(true);
                setCbxPermitDisabled(true);
                setCbxRcDisabled(true);
                setCbxTaxExamptionDisabled(true);
                getDataForRcSurrenderVerificationOrApproval(purCode, actionCode, vehicleNo);
            }

            if (purCode == TableConstants.VM_MAST_RC_CANCELLATION && actionCode == TableConstants.VM_ROLE_RC_CANCELLATION_ENTRY) {
                checkValueOfRcAction = "rccancellation";
                setPanelSurrenderDetailsDisable(false);
                setPanelReleaseDetailsDisable(false);
                setPaneldocumentSurrenderDetailsDisable(false);
                setPanelCancellationDetailsDisable(true);
                if (appl_details.getCurrent_state_cd().equalsIgnoreCase("OR")) {
                    setRenderRcCnclBy(true);
                }
                getDataForRcSurrenderVerificationOrApproval(purCode, actionCode, vehicleNo);

            } else if (purCode == TableConstants.VM_MAST_RC_CANCELLATION && (actionCode == TableConstants.VM_ROLE_RC_CANCELLATION_VERIFICATION || actionCode == TableConstants.VM_ROLE_RC_CANCELLATION_APPROVAL || actionCode == TableConstants.VM_ROLE_RC_CANCELLATION_INSPECTION)) {
                checkValueOfRcAction = "rccancellation";
                setPanelSurrenderDetailsDisable(false);
                setPanelReleaseDetailsDisable(false);
                setPaneldocumentSurrenderDetailsDisable(false);
                setPanelCancellationDetailsDisable(true);
                setRcCnclByDisable(true);
                if (appl_details.getCurrent_state_cd().equalsIgnoreCase("OR")) {
                    setRenderRcCnclBy(true);
                }
                getDataForRcSurrenderVerificationOrApproval(purCode, actionCode, vehicleNo);
            }
            if (vehicleStatus.equalsIgnoreCase(TableConstants.VT_DE_REGISTRATION)) {
                throw new VahanException("Vehicle is DE-REGISTERED, Please Contact to System Administrator ");
            }
            setRcNoDisabled(true);
            setPermitNoDisabled(true);
            setFitnessCerNoDisabled(true);
            rcCnclSurrndrShow();
        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void showRcActionValue() {

        if ("rcsurrender".equals(checkValueOfRcAction)) {
            setPanelSurrenderDetailsDisable(true);
            setPanelReleaseDetailsDisable(false);
            setPaneldocumentSurrenderDetailsDisable(true);
            setPanelCancellationDetailsDisable(false);
        }
        if ("rcrelease".equals(checkValueOfRcAction)) {
            setPanelReleaseDetailsDisable(true);
            setPanelSurrenderDetailsDisable(false);
            setPaneldocumentSurrenderDetailsDisable(true);
            setPanelCancellationDetailsDisable(false);
        }
        if ("rccancellation".equals(checkValueOfRcAction)) {
            setPanelSurrenderDetailsDisable(false);
            setPanelReleaseDetailsDisable(false);
            setPaneldocumentSurrenderDetailsDisable(false);
            setPanelCancellationDetailsDisable(true);
        }
    }

    public void checkRecieptNoIsValid() {

        String applNo;
        applNo = appl_details.getAppl_no();
        String vehicleNO = appl_details.getRegn_no();
        try {
            if (!"".equals(vehicleNO)) {
                dobj = impl.getVehicleDetailsForRc(vehicleNO, purCode, actionCode, applNo);
                if (dobj != null) {
                    setVehicleClass(dobj.getVehicleClass());
                    setAddress(dobj.getAddress());
                    setChassisNo(dobj.getChassisNo());
                    setVehicleNo(dobj.getVehicleNo());
                    setFitnessValidity(dobj.getFitnessValidity());
                    setOwnerName(dobj.getOwnerName());
                    if (purCode == TableConstants.VM_MAST_RC_RELEASE && actionCode == TableConstants.VM_ROLE_RC_RELEASE_ENTRY) {
                        if ("Y".equals(dobj.getRcStatus())) {
                            setCbxRc(true);
                            setRcNo(dobj.getRcNo());
                        }
                        if ("Y".equals(dobj.getFitnessCerStatus())) {
                            setCbxFitness(true);
                            setFitnessCerNo(dobj.getFitnessCerNo());
                        }
                        if ("Y".equals(dobj.getPermitStatus())) {
                            setCbxPermit(true);
                            setPermitNo(dobj.getPermitNo());
                        }
                        if ("Y".equals(dobj.getTaxExampStatus())) {
                            setCbxTaxExamption(true);
                        }
                    }
                }
            } else {
                JSFUtils.setFacesMessage("Receipt Details Not Found. Please Enter A valid Receipt NO", "message", JSFUtils.WARN);
            }
        } catch (VahanException e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void showsurrenderDetails() {
        String applNo = appl_details.getAppl_no();
        String vehicleNO = appl_details.getRegn_no();
        surr_list = new ArrayList<>();
        surr_list.add(dobj);
        //RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().executeScript("PF('rcSurrenderDetails').show()");
    }

    public void documentSurrenderDetails() {
        if (cbxRc == true) {
            setRcNoDisabled(false);
        } else {
            setRcNoDisabled(true);
        }
        if (cbxFitness == true) {
            setFitnessCerNoDisabled(false);
        } else {
            setFitnessCerNoDisabled(true);
        }
        if (cbxPermit == true) {
            setPermitNoDisabled(false);
        } else {
            setPermitNoDisabled(true);
        }
    }

    /**
     *
     */
    @Override
    public String save() {
        String returnLocation = "";
        RcSurrenderReleaseCancellationDobj dobjSurr = new RcSurrenderReleaseCancellationDobj();
        dobjSurr = fillAndValidateValues(appl_details.getPur_cd());
        Status_dobj statusDobj = new Status_dobj();
        statusDobj.setAction_cd(appl_details.getCurrent_action_cd());
        statusDobj.setAppl_dt(appl_details.getAppl_dt());
        statusDobj.setAppl_no(appl_details.getAppl_no());
        statusDobj.setRegn_no(appl_details.getRegn_no());
        statusDobj.setPur_cd(appl_details.getPur_cd());
        statusDobj.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
        statusDobj.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
        statusDobj.setStatus(getApp_disapp_dobj().getNew_status());
        statusDobj.setCurrent_role(appl_details.getCurrent_role());
        try {
            boolean rcSurrendered = false;
            if (dobjSurr != null) {
                rcSurrendered = impl.saveNoStatusUpdate(actionCode, purCode, statusDobj, ComparisonBeanImpl.changedDataContents(compareChanges()), dobjSurr, inwardSusSur);
                if (rcSurrendered) {
                    returnLocation = "seatwork";
                }
            }
        } catch (VahanException e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        return returnLocation;
    }

    private RcSurrenderReleaseCancellationDobj fillAndValidateValues(int pur_cd) {
        RcSurrenderReleaseCancellationDobj dobjSurr = new RcSurrenderReleaseCancellationDobj();
        boolean flag = true;
        dobjSurr.setVehicleNo(vehicleNo);
        if (pur_cd == TableConstants.VM_MAST_RC_SURRENDER) {
            if (!CommonUtils.isNullOrBlank(rcCnclSurrndr)) {
                if (surrenderDate == null) {
                    JSFUtils.setFacesMessage("Please enter the Surrender Date", "message", JSFUtils.INFO);
                    flag = false;
                } else {
                    dobjSurr.setSurrenderDate(surrenderDate);
                }
                if (rcCnclSurrndr.equalsIgnoreCase(TableConstants.RC_SUSPENSION)) {
                    if (suspendedUptoDate == null) {
                        JSFUtils.setFacesMessage("Please enter the Surrender Upto Date", "message", JSFUtils.INFO);
                        flag = false;
                    } else {
                        dobjSurr.setSuspendedUptoDate(suspendedUptoDate);
                    }
                }
                if (CommonUtils.isNullOrBlank(fileReferenceNo)) {
                    JSFUtils.setFacesMessage("Please enter the File Reference NO", "message", JSFUtils.INFO);
                    flag = false;
                } else {
                    dobjSurr.setFileReferenceNo(fileReferenceNo);
                }
                if (CommonUtils.isNullOrBlank(approvedBy)) {
                    JSFUtils.setFacesMessage("Please enter the Approval Officer Name", "message", JSFUtils.INFO);
                    flag = false;
                } else {
                    dobjSurr.setApprovedBy(approvedBy);
                }
                if (CommonUtils.isNullOrBlank(reason)) {
                    JSFUtils.setFacesMessage("Please enter the Reason", "message", JSFUtils.INFO);
                    flag = false;
                } else {
                    dobjSurr.setReason(reason);
                }
            }
        }
        if (pur_cd == TableConstants.VM_MAST_RC_RELEASE) {
            if (CommonUtils.isNullOrBlank(releaseApprovedBy)) {
                JSFUtils.setFacesMessage("Please enter the Approval Officer Name", "message", JSFUtils.INFO);
                flag = false;
            } else {
                dobjSurr.setReleaseApprovedBy(releaseApprovedBy);
            }

            if (releaseDate == null) {
                JSFUtils.setFacesMessage("Please enter the Surrender Date", "message", JSFUtils.INFO);
                flag = false;
            } else {
                dobjSurr.setReleaseDate(releaseDate);
            }
            if (CommonUtils.isNullOrBlank(releaseFileReferenceNo)) {
                JSFUtils.setFacesMessage("Please enter the File Reference NO", "message", JSFUtils.INFO);
                flag = false;
            } else {
                dobjSurr.setReleaseFileReferenceNo(releaseFileReferenceNo);
            }
            if (CommonUtils.isNullOrBlank(reason) && CommonUtils.isNullOrBlank(cancellationReason)) {
                JSFUtils.setFacesMessage("Please Contact the Administrator", "message", JSFUtils.INFO);
                flag = false;
            } else {
                if (vehicleStatus.equalsIgnoreCase(TableConstants.VT_RC_CANCEL_STATUS)) {
                    dobjSurr.setReason(cancellationReason);
                } else if (vehicleStatus.equalsIgnoreCase(TableConstants.VT_RC_SURRENDER_STATUS)) {
                    dobjSurr.setReason(reason);
                }
            }

        }
        if (pur_cd == TableConstants.VM_MAST_RC_CANCELLATION) {
            if (surrenderDate == null) {
                JSFUtils.setFacesMessage("Please enter the Rc CAncellation  Date", "message", JSFUtils.INFO);
                flag = false;
            } else {
                dobjSurr.setCancellationDate(surrenderDate);
            }

            if (CommonUtils.isNullOrBlank(approvedBy)) {
                JSFUtils.setFacesMessage("Please enter the Approval Officer Name", "message", JSFUtils.INFO);
                flag = false;
            } else {
                dobjSurr.setCancellationApprovedBy(approvedBy);
            }
            if (CommonUtils.isNullOrBlank(fileReferenceNo)) {
                JSFUtils.setFacesMessage("Please enter the File Reference No", "message", JSFUtils.INFO);
                flag = false;
            } else {
                dobjSurr.setCancellationFileReferenceNo(fileReferenceNo);
            }
            if (CommonUtils.isNullOrBlank(reason)) {
                JSFUtils.setFacesMessage("Please enter the Reason", "message", JSFUtils.INFO);
                flag = false;
            } else {
                dobjSurr.setCancellationReason(reason);
            }
            if (appl_details.getCurrent_state_cd().equalsIgnoreCase("OR")) {
                if (CommonUtils.isNullOrBlank(String.valueOf(rcCnclBy))) {
                    JSFUtils.setFacesMessage("Please Select  atleast one RC Cancel By:", "message", JSFUtils.INFO);
                    flag = false;
                } else {
                    dobjSurr.setRcCancelBy(rcCnclBy);
                }
            }
        }

        if (pur_cd == TableConstants.VM_MAST_RC_SURRENDER || pur_cd == TableConstants.VM_MAST_RC_RELEASE) {
            if (cbxRc == true) {
                dobjSurr.setRcStatus("Y");
                if (CommonUtils.isNullOrBlank(rcNo)) {
                    JSFUtils.setFacesMessage("please fill the Rc NO", ",message", JSFUtils.INFO);
                    flag = false;
                } else {
                    dobjSurr.setRcNo(rcNo);

                }

            } else if (!vehicleStatus.equalsIgnoreCase(TableConstants.VT_RC_CANCEL_STATUS) && !rcCnclSurrndr.equalsIgnoreCase("RCSus")) {
                JSFUtils.setFacesMessage("please click on the Rc Check Box and fill the Rc NO ", ",message", JSFUtils.INFO);
                flag = false;
            }
        }

        if (cbxPermit == true) {
            dobjSurr.setPermitStatus("Y");
            setPermitNoStatus("Y");
            dobjSurr.setPermitNo(permitNo);
        } else {
            dobjSurr.setPermitStatus("N");
            dobjSurr.setPermitNo("");
            setPermitNoStatus("N");
        }
        if (cbxFitness == true) {
            dobjSurr.setFitnessCerStatus("Y");
            dobjSurr.setFitnessCerNo(fitnessCerNo);
            setFitnessCerNoStatus("Y");
        } else {
            dobjSurr.setFitnessCerStatus("N");
            dobjSurr.setFitnessCerNo("");
            setFitnessCerNoStatus("N");
        }
        if (cbxTaxExamption == true) {
            dobjSurr.setTaxExampStatus("Y");
            setTaxExampStatus("Y");

        } else {
            dobjSurr.setTaxExampStatus("N");
            setTaxExampStatus("N");
        }
        if (flag == false) {
            dobjSurr = null;
        }
        return dobjSurr;
    }

    /**
     *
     * @param purCode
     * @param actionCode
     */
    private void getDataForRcSurrenderVerificationOrApproval(int purCode, int actionCode, String vehicleNo) throws VahanException {
        String applNo;
        applNo = appl_details.getAppl_no();
        try {
            if (!"".equalsIgnoreCase(vehicleNo)) {
                dobj = impl.getVehicleDetailsForRc(vehicleNo, purCode, actionCode, applNo);
                dobjPrevious = (RcSurrenderReleaseCancellationDobj) dobj.clone();
                if (dobj != null) {
                    setVehicleClass(dobj.getVehicleClass());
                    setAddress(dobj.getAddress());
                    setChassisNo(dobj.getChassisNo());
                    setVehicleNo(dobj.getVehicleNo());
                    setFitnessValidity(dobj.getFitnessValidity());
                    setOwnerName(dobj.getOwnerName());
                    setApprovedBy(dobj.getApprovedBy());
                    setReason(dobj.getReason());
                    setSurrenderDate(dobj.getSurrenderDate());
                    setSuspendedUptoDate(dobj.getSuspendedUptoDate());
                    setFileReferenceNo(dobj.getFileReferenceNo());
                    if ("Y".equals(dobj.getRcStatus())) {
                        setCbxRc(true);
                        setRcNoStatus(dobj.getRcStatus());
                        setRcNo(dobj.getRcNo());
                    }
                    if ("Y".equals(dobj.getFitnessCerStatus())) {
                        setCbxFitness(true);
                        setFitnessCerNoStatus(dobj.getFitnessCerStatus());
                        setFitnessCerNo(dobj.getFitnessCerNo());
                    }
                    if ("Y".equals(dobj.getPermitStatus())) {
                        setCbxPermit(true);
                        setPermitNoStatus(dobj.getPermitStatus());
                        setPermitNo(dobj.getPermitNo());
                    }
                    if ("Y".equals(dobj.getTaxExampStatus())) {
                        setTaxExampStatus(dobj.getTaxExampStatus());
                        setCbxTaxExamption(true);
                    }
                    setReleaseDate(dobj.getReleaseDate());
                    setReleaseApprovedBy(dobj.getReleaseApprovedBy());
                    setReleaseFileReferenceNo(dobj.getReleaseFileReferenceNo());
                    prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(applNo, purCode);
                    rcCnclSurrndr = dobj.getSurAndSus();
                    rcCnclBy = dobj.getRcCancelBy();
                    address1 = dobj.getAddress1();
                    if (dobj.getSurrenderDate() != null && dobj.getSurrenderDate().before(minDate)) {
                        this.minDate = ServerUtil.dateRange(dobj.getSurrenderDate(), 0, 0, 0);
                    }
                    if (dobj.getReleaseDate() != null && dobj.getReleaseDate().before(minDate)) {
                        this.minDate = ServerUtil.dateRange(dobj.getReleaseDate(), 0, 0, 0);
                    }
                }
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }

    }

    private void reset() {
        setVehicleClass("-1");
        setVehicleNo("");
        setChassisNo("");
        setFileReferenceNo("");
    }

    private void rcCnclSurrndrShow() {
        if (vehicleStatus.equalsIgnoreCase(TableConstants.VT_RC_CANCEL_STATUS)) {
            rcSurdisable = true;
            rcCnclDisable = false;
            rcCnclSurrndr = TableConstants.RC_CANCEL;
            renderRcCnclSurrndr = true;
            surrCnclName = "Cancellation";
            // renderPnlRcCnclSurrndr = true;
            rcSuspdisable = true;
        } else if (vehicleStatus.equalsIgnoreCase(TableConstants.VT_RC_SURRENDER_STATUS)) {
            rcCnclSurrndr = TableConstants.RC_SURRENDER;
            rcCnclDisable = true;
            rcSurdisable = false;
            renderRcCnclSurrndr = true;
            rcSuspdisable = true;
            surrCnclName = "Surrender";
            //renderPnlRcCnclSurrndr = false;
        } else if (vehicleStatus.equalsIgnoreCase("A") || vehicleStatus.equalsIgnoreCase("Y")) {
            if (appl_details.getPur_cd() == TableConstants.VM_MAST_RC_SURRENDER) {
                renderRcCnclSurrndr = true;
                rcCnclDisable = true;
                setPaneldocumentSurrenderDetailsDisable(false);
                setPanelSurrenderDetailsDisable(false);
            } else {
                renderRcCnclSurrndr = false;
            }
        } else if (vehicleStatus.equalsIgnoreCase(TableConstants.VT_RC_SUSPEND_STATUS)) {
            rcCnclSurrndr = TableConstants.RC_SUSPENSION;
            surrCnclName = "Suspension";
            renderRcCnclSurrndr = true;
            rcSurdisable = true;
            rcCnclDisable = true;
            // renderPnlRcCnclSurrndr = false;

        }
        if (purCode == TableConstants.VM_MAST_RC_RELEASE) {
            if (!CommonUtils.isNullOrBlank(rcCnclSurrndr) && rcCnclSurrndr.equalsIgnoreCase(TableConstants.RC_SURRENDER)) {
                // setPanelCancellationDetailsDisable(true);
                setPanelReleaseDetailsDisable(true);
                setPaneldocumentSurrenderDetailsDisable(true);
            } else if (!CommonUtils.isNullOrBlank(rcCnclSurrndr) && rcCnclSurrndr.equalsIgnoreCase(TableConstants.RC_CANCEL)) {
                //setPanelCancellationDetailsDisable(true);
                setPanelReleaseDetailsDisable(true);
                setPaneldocumentSurrenderDetailsDisable(false);
            } else if (!CommonUtils.isNullOrBlank(rcCnclSurrndr) && rcCnclSurrndr.equalsIgnoreCase(TableConstants.RC_SUSPENSION)) {
                //setPanelCancellationDetailsDisable(true);
                setPanelReleaseDetailsDisable(true);
                setPaneldocumentSurrenderDetailsDisable(false);
                setPanelReleaseSuspensionUpto(true);
            }
        }
        if (!CommonUtils.isNullOrBlank(rcCnclSurrndr)) {
            rcSuspensionShow();
            if (rcCnclSurrndr.equalsIgnoreCase(TableConstants.RC_SURRENDER)) {
                rcSuspdisable = true;
            } else if (rcCnclSurrndr.equalsIgnoreCase(TableConstants.RC_SUSPENSION)) {
                rcSurdisable = true;
            }
        }

    }

    public void rcSuspensionShow() {

        if (appl_details.getPur_cd() == TableConstants.VM_MAST_RC_SURRENDER) {
            if (rcCnclSurrndr.equalsIgnoreCase(TableConstants.RC_SUSPENSION)) {
                inwardSusSur = TableConstants.RC_INWARD_SUSPENSION;
                setPanelSurrenderDetailsDisable(false);
                setPanelSuspensionDetailsDisable(true);
                setPaneldocumentSurrenderDetailsDisable(false);
            } else if (rcCnclSurrndr.equalsIgnoreCase(TableConstants.RC_SURRENDER)) {
                inwardSusSur = TableConstants.RC_INWARD_SURRENDER;
                setPanelSurrenderDetailsDisable(true);
                setPanelSuspensionDetailsDisable(false);
                setPaneldocumentSurrenderDetailsDisable(true);
                if ("KA".contains(appl_details.getCurrent_state_cd())) {
                    minDate = DateUtils.parseDate(appl_details.getAppl_dt());
                    maxDate = DateUtils.parseDate(appl_details.getAppl_dt());
                }
            }
        }
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        Compare("Surrender Approved By", dobjPrevious.getApprovedBy(), getApprovedBy(), compBeanList);
        Compare("Surrender Reason", dobjPrevious.getReason(), getReason(), compBeanList);
        Compare("Surrender File Reference No ", dobjPrevious.getFileReferenceNo(), getFileReferenceNo(), compBeanList);
        if (getSurrenderDate() != null && dobjPrevious.getSurrenderDate() != null) {
            Compare("Surrender  Date", dobjPrevious.getSurrenderDate(), getSurrenderDate(), compBeanList);
        }
        if (getReleaseDate() != null && dobjPrevious.getReleaseDate() != null) {
            Compare("Release Date", dobjPrevious.getReleaseDate(), getReleaseDate(), compBeanList);
        }
        Compare("Release Approved By", dobjPrevious.getReleaseApprovedBy(), getReleaseApprovedBy(), compBeanList);
        Compare("Release File Reference No", dobjPrevious.getReleaseFileReferenceNo(), getReleaseFileReferenceNo(), compBeanList);

        if (getCancellationDate() != null && dobjPrevious.getCancellationDate() != null) {
            Compare("Cancellation  Date", dobjPrevious.getCancellationDate(), getCancellationDate(), compBeanList);
        }
        Compare("Cancellation Approved By", dobjPrevious.getCancellationApprovedBy(), getCancellationApprovedBy(), compBeanList);
        Compare("Cancellation File Reference No", dobjPrevious.getCancellationFileReferenceNo(), getCancellationFileReferenceNo(), compBeanList);
        Compare("Cancellation  Reason", dobjPrevious.getCancellationReason(), getCancellationReason(), compBeanList);
        Compare("RC SerialNo", dobjPrevious.getRcNo(), getRcNo(), compBeanList);
        Compare("Permit SerialNo", dobjPrevious.getPermitNo(), getPermitNo(), compBeanList);
        Compare("Fitness Certificate", dobjPrevious.getFitnessCerNo(), getFitnessCerNo(), compBeanList);
        Compare("Rc Check BOx", dobjPrevious.getRcStatus(), getRcNoStatus(), compBeanList);
        Compare("Permit Check BOx", dobjPrevious.getPermitStatus(), getPermitNoStatus(), compBeanList);
        Compare("Fitness Check BOx", dobjPrevious.getFitnessCerStatus(), getFitnessCerNoStatus(), compBeanList);
        Compare("getTaxExamp Check BOx", dobjPrevious.getTaxExampStatus(), getTaxExampStatus(), compBeanList);
        return compBeanList;
    }

    @Override
    public String saveAndMoveFile() {
        String actionCd = String.valueOf(actionCode);
        String purCd = String.valueOf(purCode);
        String returnLocation = "";
        RcSurrenderReleaseCancellationImpl impl = null;
        RcSurrenderReleaseCancellationDobj dobj = new RcSurrenderReleaseCancellationDobj();
        try {
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setRegn_no(appl_details.getRegn_no());
            status.setPur_cd(appl_details.getPur_cd());
            status.setAction_cd(appl_details.getCurrent_action_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            status.setCurrent_role(appl_details.getCurrent_role());
            status.setVehicleParameters(appl_details.getVehicleParameters());
            dobj = fillAndValidateValues(appl_details.getPur_cd());
            impl = new RcSurrenderReleaseCancellationImpl();

            if (dobj != null) {
                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE) && this.appl_details.isDocVerifyForFacelessAppl()) {
                    String[] notVerifiedDocDetails = ServerUtil.verifyDocUploaded(appl_details.getAppl_no(), getAppl_details().getCurrent_action_cd());
                    if (notVerifiedDocDetails != null) {
                        appl_details.setNotVerifiedDocdetails(notVerifiedDocDetails[1]);
                        throw new VahanException(notVerifiedDocDetails[0]);
                    }
                }
                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                        || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                    if ((String.valueOf(TableConstants.VM_ROLE_RC_SURRENDER_ENTRY) == null ? actionCd == null : String.valueOf(TableConstants.VM_ROLE_RC_SURRENDER_ENTRY).equals(actionCd)) || (String.valueOf(TableConstants.VM_ROLE_RC_RELEASE_ENTRY) == null ? actionCd == null : String.valueOf(TableConstants.VM_ROLE_RC_RELEASE_ENTRY).equals(actionCd)) || (String.valueOf(TableConstants.VM_ROLE_RC_CANCELLATION_ENTRY) == null ? actionCd == null : String.valueOf(TableConstants.VM_ROLE_RC_CANCELLATION_ENTRY).equals(actionCd))) {
                        boolean rcSurrendered = false;
                        if (dobj != null) {
                            if (purCode == TableConstants.VM_MAST_RC_SURRENDER && inwardSusSur == "rcSus") {
                                dobj.setRcCancelBy(1);
                            }
                            rcSurrendered = impl.rcDataSave(Integer.parseInt(actionCd), Integer.parseInt(purCd), status, ComparisonBeanImpl.changedDataContents(compareChanges()), dobj, inwardSusSur);
                        }
                        if (rcSurrendered) {
                            if (appl_details.getCurrent_state_cd().equalsIgnoreCase("OR") && actionCd.equals(String.valueOf(TableConstants.VM_ROLE_RC_CANCELLATION_ENTRY)) && rcCnclBy == TableConstants.RC_CANCEL_BY_AUTHORITY) {
                                returnLocation = "rcCancelEntryIntimation";
                            } else {
                                returnLocation = "seatwork";
                                reset();
                            }
                        }
                    }

                    if ((actionCd == null ? (String.valueOf(TableConstants.VM_ROLE_RC_SURRENDER_VERIFICATION)) == null : actionCd.equals(String.valueOf(TableConstants.VM_ROLE_RC_SURRENDER_VERIFICATION))) || (actionCd == null ? (String.valueOf(TableConstants.VM_ROLE_RC_RELEASE_VERIFICATION)) == null : actionCd.equals(String.valueOf(TableConstants.VM_ROLE_RC_RELEASE_VERIFICATION))) || (actionCd == null ? (String.valueOf(TableConstants.VM_ROLE_RC_CANCELLATION_VERIFICATION)) == null : actionCd.equals(String.valueOf(TableConstants.VM_ROLE_RC_CANCELLATION_VERIFICATION)))
                            || (actionCd == null ? (String.valueOf(TableConstants.VM_ROLE_RC_CANCELLATION_INSPECTION)) == null : actionCd.equals(String.valueOf(TableConstants.VM_ROLE_RC_CANCELLATION_INSPECTION)))) {
                        impl.updateRcStatus(actionCd, purCd, status, ComparisonBeanImpl.changedDataContents(compareChanges()), dobj, null, inwardSusSur);
                        returnLocation = "seatwork";
                    }
                    if ((actionCd == null ? (String.valueOf(TableConstants.VM_ROLE_RC_SURRENDER_APPROVAL)) == null : actionCd.equals(String.valueOf(TableConstants.VM_ROLE_RC_SURRENDER_APPROVAL))) || (actionCd == null ? (String.valueOf(TableConstants.VM_ROLE_RC_RELEASE_APPROVAL)) == null : actionCd.equals(String.valueOf(TableConstants.VM_ROLE_RC_RELEASE_APPROVAL))) || (actionCd == null ? (String.valueOf(TableConstants.VM_ROLE_RC_CANCELLATION_APPROVAL)) == null : actionCd.equals(String.valueOf(TableConstants.VM_ROLE_RC_CANCELLATION_APPROVAL)))) {
                        impl.updateRcStatus(actionCd, purCd, status, ComparisonBeanImpl.changedDataContents(compareChanges()), dobj, vehicleStatus, inwardSusSur);
                        if (actionCd.equals(String.valueOf(TableConstants.VM_ROLE_RC_CANCELLATION_APPROVAL))) {
                            if (appl_details.getCurrent_state_cd().equalsIgnoreCase("OR") && rcCnclBy == TableConstants.RC_CANCEL_BY_AUTHORITY) {
                                returnLocation = "rcCancelIntimation";
                            } else {
                                returnLocation = "rcCancelPrint";
                            }
                        } else if (actionCd.equals(String.valueOf(TableConstants.VM_ROLE_RC_SURRENDER_APPROVAL)) && !CommonUtils.isNullOrBlank(inwardSusSur) && inwardSusSur.equalsIgnoreCase(TableConstants.RC_INWARD_SUSPENSION)) {
                            returnLocation = "rcSuspensionPrint";
                        } else {
                            returnLocation = "seatwork";
                        }
                        if ((actionCd == null ? (String.valueOf(TableConstants.VM_ROLE_RC_SURRENDER_APPROVAL)) == null : actionCd.equals(String.valueOf(TableConstants.VM_ROLE_RC_SURRENDER_APPROVAL))) || (actionCd == null ? (String.valueOf(TableConstants.VM_ROLE_RC_CANCELLATION_APPROVAL)) == null : actionCd.equals(String.valueOf(TableConstants.VM_ROLE_RC_CANCELLATION_APPROVAL)))) {
                            String mobileNo = String.valueOf(appl_details.getOwnerDetailsDobj().getOwnerIdentity().getMobile_no());
                            if (!CommonUtils.isNullOrBlank(mobileNo)) {
                                //String resetVerifyMsg = "RC is Surrender/Cancel/Suspended against your vehicle No: " + appl_details.getRegn_no();
                                //ServerUtil.sendSMS(mobileNo, resetVerifyMsg);
                            }
                        }
                    }
                    if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                        ServerUtil.sendSmsInFacelessService(appl_details.getAppl_no(), Util.getUserStateCode(), Util.getUserSeatOffCode(), appl_details.getRegn_no(), appl_details.getNotVerifiedDocdetails());
                        returnLocation = disapprovalPrint();
                    }
                }

                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)) {
                    status.setPrev_action_cd_selected(app_disapp_dobj.getPre_action_code());
                    impl.reback(actionCd, purCd, status, ComparisonBeanImpl.changedDataContents(compareChanges()), dobj, inwardSusSur);
                    returnLocation = "seatwork";
                }
            } else {
                JSFUtils.setFacesMessage("There Is some Problem For Saving The Data", "message", JSFUtils.INFO);
            }
        } catch (VahanException ve) {
            JSFUtils.setFacesMessage(ve.getMessage(), "message", JSFUtils.INFO);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.setFacesMessage(TableConstants.SomthingWentWrong, "message", JSFUtils.INFO);
        }
        return returnLocation;

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

    public String getCheckValueOfRcAction() {
        return checkValueOfRcAction;
    }

    public void setCheckValueOfRcAction(String checkValueOfRcAction) {
        this.checkValueOfRcAction = checkValueOfRcAction;
    }

    public boolean isPanelSurrenderDetailsDisable() {
        return panelSurrenderDetailsDisable;
    }

    public final void setPanelSurrenderDetailsDisable(boolean panelSurrenderDetailsDisable) {
        this.panelSurrenderDetailsDisable = panelSurrenderDetailsDisable;
    }

    public boolean isPanelReleaseDetailsDisable() {
        return panelReleaseDetailsDisable;
    }

    final void setPanelReleaseDetailsDisable(boolean panelReleaseDetailsDisable) {
        this.panelReleaseDetailsDisable = panelReleaseDetailsDisable;
    }

    public boolean isPaneldocumentSurrenderDetailsDisable() {
        return paneldocumentSurrenderDetailsDisable;
    }

    public final void setPaneldocumentSurrenderDetailsDisable(boolean paneldocumentSurrenderDetailsDisable) {
        this.paneldocumentSurrenderDetailsDisable = paneldocumentSurrenderDetailsDisable;
    }

    public boolean isPanelCancellationDetailsDisable() {
        return panelCancellationDetailsDisable;
    }

    public final void setPanelCancellationDetailsDisable(boolean panelCancellationDetailsDisable) {
        this.panelCancellationDetailsDisable = panelCancellationDetailsDisable;
    }

    public String getRecieptOrInwordNO() {
        return recieptOrInwordNO;
    }

    public void setRecieptOrInwordNO(String recieptOrInwordNO) {
        this.recieptOrInwordNO = recieptOrInwordNO;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getChassisNo() {
        return chassisNo;
    }

    public void setChassisNo(String chassisNo) {
        this.chassisNo = chassisNo;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVehicleClass() {
        return vehicleClass;
    }

    public void setVehicleClass(String vehicleClass) {
        this.vehicleClass = vehicleClass;
    }

    public Date getFitnessValidity() {
        return fitnessValidity;
    }

    public void setFitnessValidity(Date fitnessValidity) {
        this.fitnessValidity = fitnessValidity;
    }

    public List getListVehicleClass() {
        return listVehicleClass;
    }

    public void setListVehicleClass(List listVehicleClass) {
        this.listVehicleClass = listVehicleClass;
    }

    public Date getSurrenderDate() {
        return surrenderDate;
    }

    public void setSurrenderDate(Date surrenderDate) {
        this.surrenderDate = surrenderDate;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getFileReferenceNo() {
        return fileReferenceNo;
    }

    public void setFileReferenceNo(String fileReferenceNo) {
        this.fileReferenceNo = fileReferenceNo;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isCbxRc() {
        return cbxRc;
    }

    public void setCbxRc(boolean cbxRc) {
        this.cbxRc = cbxRc;
    }

    public boolean isCbxPermit() {
        return cbxPermit;
    }

    public void setCbxPermit(boolean cbxPermit) {
        this.cbxPermit = cbxPermit;
    }

    public boolean isCbxFitness() {
        return cbxFitness;
    }

    public void setCbxFitness(boolean cbxFitness) {
        this.cbxFitness = cbxFitness;
    }

    public boolean isCbxTaxExamption() {
        return cbxTaxExamption;
    }

    public void setCbxTaxExamption(boolean cbxTaxExamption) {
        this.cbxTaxExamption = cbxTaxExamption;
    }

    public int getPurCode() {
        return purCode;
    }

    public void setPurCode(int purCode) {
        this.purCode = purCode;
    }

    public int getActionCode() {
        return actionCode;
    }

    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
    }

    public String getRcNo() {
        return rcNo;
    }

    public void setRcNo(String rcNo) {
        this.rcNo = rcNo;
    }

    public String getPermitNo() {
        return permitNo;
    }

    public void setPermitNo(String permitNo) {
        this.permitNo = permitNo;
    }

    public String getFitnessCerNo() {
        return fitnessCerNo;
    }

    public void setFitnessCerNo(String fitnessCerNo) {
        this.fitnessCerNo = fitnessCerNo;
    }

    public boolean isRcNoDisabled() {
        return rcNoDisabled;
    }

    public final void setRcNoDisabled(boolean rcNoDisabled) {
        this.rcNoDisabled = rcNoDisabled;
    }

    public boolean isPermitNoDisabled() {
        return permitNoDisabled;
    }

    public final void setPermitNoDisabled(boolean permitNoDisabled) {
        this.permitNoDisabled = permitNoDisabled;
    }

    public boolean isFitnessCerNoDisabled() {
        return fitnessCerNoDisabled;
    }

    public final void setFitnessCerNoDisabled(boolean fitnessCerNoDisabled) {
        this.fitnessCerNoDisabled = fitnessCerNoDisabled;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Date getCancellationDate() {
        return cancellationDate;
    }

    public void setCancellationDate(Date cancellationDate) {
        this.cancellationDate = cancellationDate;
    }

    public String getReleaseApprovedBy() {
        return releaseApprovedBy;
    }

    public void setReleaseApprovedBy(String releaseApprovedBy) {
        this.releaseApprovedBy = releaseApprovedBy;
    }

    public String getReleaseFileReferenceNo() {
        return releaseFileReferenceNo;
    }

    public void setReleaseFileReferenceNo(String releaseFileReferenceNo) {
        this.releaseFileReferenceNo = releaseFileReferenceNo;
    }

    public String getCancellationApprovedBy() {
        return cancellationApprovedBy;
    }

    public void setCancellationApprovedBy(String cancellationApprovedBy) {
        this.cancellationApprovedBy = cancellationApprovedBy;
    }

    public String getCancellationFileReferenceNo() {
        return cancellationFileReferenceNo;
    }

    public void setCancellationFileReferenceNo(String cancellationFileReferenceNo) {
        this.cancellationFileReferenceNo = cancellationFileReferenceNo;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public boolean isCbxRcDisabled() {
        return cbxRcDisabled;
    }

    public final void setCbxRcDisabled(boolean cbxRcDisabled) {
        this.cbxRcDisabled = cbxRcDisabled;
    }

    public boolean isCbxPermitDisabled() {
        return cbxPermitDisabled;
    }

    public final void setCbxPermitDisabled(boolean cbxPermitDisabled) {
        this.cbxPermitDisabled = cbxPermitDisabled;
    }

    public boolean isCbxFitnessDisabled() {
        return cbxFitnessDisabled;
    }

    public final void setCbxFitnessDisabled(boolean cbxFitnessDisabled) {
        this.cbxFitnessDisabled = cbxFitnessDisabled;
    }

    public boolean isCbxTaxExamptionDisabled() {
        return cbxTaxExamptionDisabled;
    }

    public final void setCbxTaxExamptionDisabled(boolean cbxTaxExamptionDisabled) {
        this.cbxTaxExamptionDisabled = cbxTaxExamptionDisabled;
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

    /**
     * @return the rcNoStatus
     */
    public String getRcNoStatus() {
        return rcNoStatus;
    }

    /**
     * @param rcNoStatus the rcNoStatus to set
     */
    public void setRcNoStatus(String rcNoStatus) {
        this.rcNoStatus = rcNoStatus;
    }

    /**
     * @return the permitNoStatus
     */
    public String getPermitNoStatus() {
        return permitNoStatus;
    }

    /**
     * @param permitNoStatus the permitNoStatus to set
     */
    public void setPermitNoStatus(String permitNoStatus) {
        this.permitNoStatus = permitNoStatus;
    }

    /**
     * @return the fitnessCerNoStatus
     */
    public String getFitnessCerNoStatus() {
        return fitnessCerNoStatus;
    }

    /**
     * @param fitnessCerNoStatus the fitnessCerNoStatus to set
     */
    public void setFitnessCerNoStatus(String fitnessCerNoStatus) {
        this.fitnessCerNoStatus = fitnessCerNoStatus;
    }

    /**
     * @return the taxExampStatus
     */
    public String getTaxExampStatus() {
        return taxExampStatus;
    }

    /**
     * @param taxExampStatus the taxExampStatus to set
     */
    public void setTaxExampStatus(String taxExampStatus) {
        this.taxExampStatus = taxExampStatus;
    }

    public List<RcSurrenderReleaseCancellationDobj> getSurr_list() {
        return surr_list;
    }

    public void setSurr_list(List<RcSurrenderReleaseCancellationDobj> surr_list) {
        this.surr_list = surr_list;
    }

    public String getRcCnclSurrndr() {
        return rcCnclSurrndr;
    }

    public void setRcCnclSurrndr(String rcCnclSurrndr) {
        this.rcCnclSurrndr = rcCnclSurrndr;
    }

    public String getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(String vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public boolean isRcSurdisable() {
        return rcSurdisable;
    }

    public void setRcSurdisable(boolean rcSurdisable) {
        this.rcSurdisable = rcSurdisable;
    }

    public boolean isRcCnclDisable() {
        return rcCnclDisable;
    }

    public void setRcCnclDisable(boolean rcCnclDisable) {
        this.rcCnclDisable = rcCnclDisable;
    }

    public boolean isRenderRcCnclSurrndr() {
        return renderRcCnclSurrndr;
    }

    public void setRenderRcCnclSurrndr(boolean renderRcCnclSurrndr) {
        this.renderRcCnclSurrndr = renderRcCnclSurrndr;
    }

    public String getSurrCnclName() {
        return surrCnclName;
    }

    public void setSurrCnclName(String surrCnclName) {
        this.surrCnclName = surrCnclName;
    }

    public boolean isRenderPnlRcCnclSurrndr() {
        return renderPnlRcCnclSurrndr;
    }

    public void setRenderPnlRcCnclSurrndr(boolean renderPnlRcCnclSurrndr) {
        this.renderPnlRcCnclSurrndr = renderPnlRcCnclSurrndr;
    }

    public boolean isRcSuspdisable() {
        return rcSuspdisable;
    }

    public void setRcSuspdisable(boolean rcSuspdisable) {
        this.rcSuspdisable = rcSuspdisable;
    }

    public boolean isPanelSuspensionDetailsDisable() {
        return panelSuspensionDetailsDisable;
    }

    public void setPanelSuspensionDetailsDisable(boolean panelSuspensionDetailsDisable) {
        this.panelSuspensionDetailsDisable = panelSuspensionDetailsDisable;
    }

    public boolean isRendeRcCnclReason() {
        return rendeRcCnclReason;
    }

    public void setRendeRcCnclReason(boolean rendeRcCnclReason) {
        this.rendeRcCnclReason = rendeRcCnclReason;
    }

    public List getListReason() {
        return listReason;
    }

    public void setListReason(List listReason) {
        this.listReason = listReason;
    }

    public String getOffCdDesr() {
        return OffCdDesr;
    }

    public void setOffCdDesr(String OffCdDesr) {
        this.OffCdDesr = OffCdDesr;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public boolean isPanelReleaseSuspensionUpto() {
        return panelReleaseSuspensionUpto;
    }

    public void setPanelReleaseSuspensionUpto(boolean panelReleaseSuspensionUpto) {
        this.panelReleaseSuspensionUpto = panelReleaseSuspensionUpto;
    }

    public boolean isRenderRcCnclBy() {
        return renderRcCnclBy;
    }

    public void setRenderRcCnclBy(boolean renderRcCnclBy) {
        this.renderRcCnclBy = renderRcCnclBy;
    }

    public boolean isRcCnclByDisable() {
        return rcCnclByDisable;
    }

    public void setRcCnclByDisable(boolean rcCnclByDisable) {
        this.rcCnclByDisable = rcCnclByDisable;
    }

    public int getRcCnclBy() {
        return rcCnclBy;
    }

    public void setRcCnclBy(int rcCnclBy) {
        this.rcCnclBy = rcCnclBy;
    }

    public Date getSuspendedUptoDate() {
        return suspendedUptoDate;
    }

    public void setSuspendedUptoDate(Date suspendedUptoDate) {
        this.suspendedUptoDate = suspendedUptoDate;
    }

    public String getEngNo() {
        return engNo;
    }

    public void setEngNo(String engNo) {
        this.engNo = engNo;
    }

    public String getManuMonth() {
        return manuMonth;
    }

    public void setManuMonth(String manuMonth) {
        this.manuMonth = manuMonth;
    }

    public String getManuYear() {
        return manuYear;
    }

    public void setManuYear(String manuYear) {
        this.manuYear = manuYear;
    }

    public String getVhClassDescr() {
        return vhClassDescr;
    }

    public void setVhClassDescr(String vhClassDescr) {
        this.vhClassDescr = vhClassDescr;
    }

    public String getVhCatg() {
        return vhCatg;
    }

    public void setVhCatg(String vhCatg) {
        this.vhCatg = vhCatg;
    }

    public String getBdType() {
        return bdType;
    }

    public void setBdType(String bdType) {
        this.bdType = bdType;
    }

    public String getRegnDt() {
        return regnDt;
    }

    public void setRegnDt(String regnDt) {
        this.regnDt = regnDt;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }
}
