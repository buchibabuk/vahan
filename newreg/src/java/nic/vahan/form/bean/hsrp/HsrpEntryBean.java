/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.hsrp;

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
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.fancy.FancyNoReplaceDobj;
import nic.vahan.form.dobj.hsrp.HSRP_dobj;
import nic.vahan.form.impl.DocumentUploadImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.admin.FancyNoReplaceImpl;
import nic.vahan.form.impl.hsrp.HsrpEntryImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Niraj
 */
@ManagedBean(name = "hsrpEntry")
@ViewScoped
public class HsrpEntryBean implements Serializable {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(HsrpEntryBean.class);
    private String regn_no = "";
    private HSRP_dobj hsrpdobj = new HSRP_dobj();
    private boolean renderHSRPDetails = false;
    HsrpEntryImpl hsrpImpl = new HsrpEntryImpl();
    private SessionVariables sessionVariables = null;
    private String vahanMessages = null;
    private Date minDate = null;
    private Date maxDate = new Date();
    private OwnerDetailsDobj ownerDobj = null;
//    private boolean renderUpdateButton = false;
    private String applicationNumber;
    private boolean renderHsrpDialog = true;
    private boolean renderFitUptoBox = false;
    private boolean isBeforeApproval = true;
    private int purCd = 0;
    private String displayMessage = "";
    private String hsrpReason = "";
    private List hsrpResonList = new ArrayList();
    private boolean frontDisable = false;
    private boolean rearDisable = false;
    private boolean optionDisable = true;

    public HsrpEntryBean() {
        sessionVariables = new SessionVariables();
        if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
            vahanMessages = "Session time Out";
            return;
        }
    }

    public void gettingUserDetails() {
        try {
            if (CommonUtils.isNullOrBlank(getRegn_no())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Enter  Registration No ", "Please Enter  Registration No"));
                return;
            }
            if (!JSFUtils.isNumeric(regn_no.substring(regn_no.length() - 4, regn_no.length()))) {
                throw new VahanException("Invalid Registration No ( Last 4 Character of Registration No Should be Numeric)");
            }
            boolean isHsrpAfterFinalApproval = hsrpImpl.getHSRPAfterFinalApprovalStatus(sessionVariables.getStateCodeSelected());
            ownerDobj = new DocumentUploadImpl().getVaOwnerDetailsForDocumentUpload(null, sessionVariables.getStateCodeSelected(), regn_no);
            if (ownerDobj == null) {
                ownerDobj = new OwnerImpl().getOwnerDetails(regn_no);
                isBeforeApproval = false;
                if (ownerDobj == null) {
                    setRegn_no(null);
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Regn No", "Invalid Regn No");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return;
                }
            } else if (isHsrpAfterFinalApproval) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Can't upload the HSRP details, the New Registration Application is pending at RTO for final approval.", "Can't upload the HSRP details, the New Registration Application is pending at RTO for final approval.");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }
            boolean tempDiffStateVehicle = false;

            if (getOwnerDobj() != null) {
                String fitUpto = getOwnerDobj().getFitUptoDescr();
                if (fitUpto != null) {
                    renderFitUptoBox = true;
                }
                //
                String regisDate = getOwnerDobj().getRegn_dt();
                minDate = JSFUtils.getStringToDateyyyyMMdd(regisDate);
                if (TableConstants.USER_CATG_DEALER.equals(sessionVariables.getUserCatgForLoggedInUser())) {
                    String empCode = sessionVariables.getEmpCodeLoggedIn();
                    if (!CommonUtils.isNullOrBlank(empCode) && !getOwnerDobj().getDealer_cd().equalsIgnoreCase(ServerUtil.getDealerCode(Long.parseLong(empCode), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected()))) {
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "You are not authorized to enter HSRP details for this vehicle.", "You are not authorized to enter HSRP details for this vehicle.");
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        return;
                    }
                }
                if (sessionVariables.getOffCodeSelected() != ownerDobj.getOff_cd()) {
                    throw new VahanException("Vehicle details found in state " + ServerUtil.getStateNameByStateCode(sessionVariables.getStateCodeSelected()) + " and Office :" + ServerUtil.getOfficeName(ownerDobj.getOff_cd(), ownerDobj.getState_cd()));
                }
                if (ownerDobj.getRegn_type().equalsIgnoreCase("T")) {
                    tempDiffStateVehicle = hsrpImpl.checkTempDiffStateVehicle(regn_no, ownerDobj.getState_cd());

                }
                HSRP_dobj hsrpDobj = hsrpImpl.validateRegnForHSRP(regn_no, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), isBeforeApproval);
                boolean isPresentInHomologation = hsrpImpl.makerPresentInHomologation(ownerDobj.getMaker());
                frontDisable = true;
                rearDisable = true;
                if (hsrpDobj != null) { // updation case
                    if (hsrpDobj.getEmp_cd().equals(sessionVariables.getEmpCodeLoggedIn())) {
                        optionDisable = false;
                    }
                    if (DateUtils.compareDates(minDate, JSFUtils.getStringToDateyyyyMMdd("2019-11-24")) == 1 || DateUtils.compareDates(minDate, JSFUtils.getStringToDateyyyyMMdd("2019-11-24")) == 0) {
                        optionDisable = false;
                    } else {
                        if (!isPresentInHomologation || tempDiffStateVehicle
                                || ownerDobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                                || ownerDobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_EXARMY)
                                || ownerDobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_CD)) {
                            optionDisable = false;

                        }
                    }
                    if (TableConstants.USER_CATG_DEALER.equals(sessionVariables.getUserCatgForLoggedInUser())) {
                        if (!optionDisable) {
                            optionDisable = true;
                            frontDisable = false;
                            rearDisable = false;
                        }

                        hsrpResonList.clear();
                        hsrpResonList.add(new SelectItem("UB", "Update Both"));
                        hsrpReason = "UB";

                    } else {
                        hsrpResonList.clear();
                        hsrpResonList.add(new SelectItem("UB", "Update Both"));
                        hsrpResonList.add(new SelectItem("DB", "Duplicate Both"));
                        hsrpResonList.add(new SelectItem("DF", "Duplicate Front"));
                        hsrpResonList.add(new SelectItem("DR", "Duplicate Rear"));
                    }

                    renderHSRPDetails = true;
                    hsrpdobj = hsrpDobj;

                    applicationNumber = hsrpDobj.getAppl_no();
                    //disableFields = true;

                } else {// new entry

                    if (DateUtils.compareDates(minDate, JSFUtils.getStringToDateyyyyMMdd("2019-11-24")) == 1 || DateUtils.compareDates(minDate, JSFUtils.getStringToDateyyyyMMdd("2019-11-24")) == 0) {
                        optionDisable = false;
                    } else {
                        optionDisable = true;
                        if (!isPresentInHomologation || tempDiffStateVehicle
                                || ownerDobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                                || ownerDobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_EXARMY)
                                || ownerDobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_CD)) {
                            optionDisable = false;
                        } else {
                            optionDisable = true;
                        }
                    }

                    //displayMessage = hsrpImpl.getValidVender(ownerDobj.getState_cd(), ownerDobj.getOff_cd(), ownerDobj.getMaker());
//                    if (displayMessage != "") {
//                        RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", displayMessage));
//                        return;
//                    }
                    if (!CommonUtils.isNullOrBlank(ownerDobj.getApplNo())) {
                        //inputSelection = true;
                        renderHSRPDetails = true;

                        applicationNumber = ownerDobj.getApplNo();
                    } else {

                        FancyNoReplaceDobj dobj = new FancyNoReplaceImpl().getApplNoAndPurCode(sessionVariables.getStateCodeSelected(), regn_no);
                        if (dobj != null) {
                            //optionDisable = false;
                            hsrpResonList.clear();
                            hsrpResonList.add(new SelectItem("NB", "New Both"));
                            hsrpReason = "NB";
                            renderHSRPDetails = true;

                            applicationNumber = dobj.getApplNo();
                            purCd = dobj.getPurCode();
                            if (!optionDisable) {
                                optionDisable = true;
                                frontDisable = false;
                                rearDisable = false;
                            }

                        } else {
                            if (!TableConstants.USER_CATG_DEALER.equals(sessionVariables.getUserCatgForLoggedInUser())) {
                                renderHSRPDetails = true;

                                //optionDisable = false;
                                hsrpResonList.clear();
                                hsrpResonList.add(new SelectItem("OB", "Old Both"));
                                hsrpReason = "OB";
                                applicationNumber = (regn_no);
                                if (!optionDisable) {
                                    optionDisable = true;
                                    frontDisable = false;
                                    rearDisable = false;
                                }

                            } else {
                                throw new VahanException("No valid application number found for registration no: " + getRegn_no());
                            }
//                            throw new VahanException("No valid application number found for registration no: " + getRegn_no());
                        }
                    }

                }

            } else {
                throw new VahanException("Data not found for Registration No: " + getRegn_no());
            }
        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null, vex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null, TableConstants.SomthingWentWrong));
        }
    }

    public void save() {
        if (!validateForm()) {
            return;
        }
        try {
            if (hsrpReason.equals("-1")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Select reason for updation", "Please Select reason for updation"));
                return;
            }
            if (hsrpdobj != null) {
                hsrpdobj.setRegn_no(regn_no);
                hsrpdobj.setAppl_no(applicationNumber);
                hsrpdobj.setState_cd(sessionVariables.getStateCodeSelected());
                hsrpdobj.setOff_cd(sessionVariables.getOffCodeSelected());
                hsrpdobj.setEmp_cd(sessionVariables.getEmpCodeLoggedIn());
                if (hsrpReason.equalsIgnoreCase("NB") || hsrpReason.equalsIgnoreCase("OB")) {
                    boolean status = hsrpImpl.saveHSRPDetails(regn_no, hsrpdobj, purCd, hsrpReason);
                    if (status) {
                        setVahanMessages("Record Save Successfully");
                        PrimeFaces.current().ajax().update("showMessg");
                        PrimeFaces.current().executeScript("PF('successDialog').show();");
                    } else {
                        throw new VahanException("There is some error to save HSRP data");
                    }
                } else {
                    boolean status = hsrpImpl.updateHSRPVehicleDetails(hsrpdobj, sessionVariables.getEmpCodeLoggedIn(), isBeforeApproval, hsrpReason);
                    if (status) {
                        setVahanMessages("Record Updated Successfully");
                        PrimeFaces.current().ajax().update("showMessg");
                        PrimeFaces.current().executeScript("PF('successDialog').show();");
                    } else {
                        throw new VahanException("There is some error to update HSRP data.");
                    }
                }

            }
        } catch (VahanException vex) {
            vahanMessages = vex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, vex.getMessage(), vex.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void reasonChangeListener() {

        if (hsrpReason.equalsIgnoreCase("DR")) {
            frontDisable = true;
            rearDisable = false;
        } else if (hsrpReason.equalsIgnoreCase("DF")) {
            rearDisable = true;
            frontDisable = false;
        } else {
            rearDisable = false;
            frontDisable = false;
        }

    }

    public boolean validateForm() {
        boolean valid = true;
        FacesMessage message = null;
        if (hsrpdobj != null && isRenderHSRPDetails()) {
            if (CommonUtils.isNullOrBlank(hsrpdobj.getHsrp_no_front())) {
                valid = false;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Blank HSRP Front Number");
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
            if (CommonUtils.isNullOrBlank(hsrpdobj.getHsrp_no_back())) {
                valid = false;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Blank HSRP Back Number");
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
            if (hsrpdobj.getHsrp_fixed_dt() == null) {
                valid = false;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Blank HSRP Fix Date");
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
            if (hsrpdobj.getHsrp_no_front().equalsIgnoreCase(hsrpdobj.getHsrp_no_back())) {
                valid = false;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "HSRP Front Number and HSRP Back Number can't be same.");
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
            if (hsrpdobj.getHsrp_no_front() != null && !hsrpdobj.getHsrp_no_front().equals("")) {
                if (!CommonUtils.isNullOrBlank(hsrpdobj.getHsrp_no_front()) && !(hsrpdobj.getHsrp_no_front().matches("([a-zA-Z]{2}[0-9]{5,20})"))) {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "First two character should be alphabet and remaining should be numeric of HSRP Front Number.!!!");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    valid = false;
                }
            }

            if (hsrpdobj.getHsrp_no_back() != null && !hsrpdobj.getHsrp_no_back().equals("")) {
                if (!CommonUtils.isNullOrBlank(hsrpdobj.getHsrp_no_back()) && !(hsrpdobj.getHsrp_no_back().matches("([a-zA-Z]{2}[0-9]{5,20})"))) {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "First two character should be alphabet and remaining should be numeric of HSRP Back Number.!!!");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    valid = false;
                }
            }
        }
        return valid;
    }

    public void changeEventListener() {
//        renderUpdateButton = true;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public HSRP_dobj getHsrpdobj() {
        return hsrpdobj;
    }

    public void setHsrpdobj(HSRP_dobj hsrpdobj) {
        this.hsrpdobj = hsrpdobj;
    }

    public boolean isRenderHSRPDetails() {
        return renderHSRPDetails;
    }

    public void setRenderHSRPDetails(boolean renderHSRPDetails) {
        this.renderHSRPDetails = renderHSRPDetails;
    }

    /**
     * @return the minDate
     */
    public Date getMinDate() {
        return minDate;
    }

    /**
     * @param minDate the minDate to set
     */
    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    /**
     * @return the ownerDobj
     */
    public OwnerDetailsDobj getOwnerDobj() {
        return ownerDobj;
    }

    /**
     * @param ownerDobj the ownerDobj to set
     */
    public void setOwnerDobj(OwnerDetailsDobj ownerDobj) {
        this.ownerDobj = ownerDobj;
    }

    /**
     * @return the maxDate
     */
    public Date getMaxDate() {
        return maxDate;
    }

    /**
     * @param maxDate the maxDate to set
     */
    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
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
     * @return the applicationNumber
     */
    public String getApplicationNumber() {
        return applicationNumber;
    }

    /**
     * @param applicationNumber the applicationNumber to set
     */
    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    /**
     * @return the renderHsrpDialog
     */
    public boolean isRenderHsrpDialog() {
        return renderHsrpDialog;
    }

    /**
     * @param renderHsrpDialog the renderHsrpDialog to set
     */
    public void setRenderHsrpDialog(boolean renderHsrpDialog) {
        this.renderHsrpDialog = renderHsrpDialog;
    }

    /**
     * @return the renderFitUptoBox
     */
    public boolean isRenderFitUptoBox() {
        return renderFitUptoBox;
    }

    /**
     * @param renderFitUptoBox the renderFitUptoBox to set
     */
    public void setRenderFitUptoBox(boolean renderFitUptoBox) {
        this.renderFitUptoBox = renderFitUptoBox;
    }

    /**
     * @return the displayMessage
     */
    public String getDisplayMessage() {
        return displayMessage;
    }

    /**
     * @param displayMessage the displayMessage to set
     */
    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    /**
     * @return the hsrpReason
     */
    public String getHsrpReason() {
        return hsrpReason;
    }

    /**
     * @param hsrpReason the hsrpReason to set
     */
    public void setHsrpReason(String hsrpReason) {
        this.hsrpReason = hsrpReason;
    }

    /**
     * @return the hsrpResonList
     */
    public List getHsrpResonList() {
        return hsrpResonList;
    }

    /**
     * @param hsrpResonList the hsrpResonList to set
     */
    public void setHsrpResonList(List hsrpResonList) {
        this.hsrpResonList = hsrpResonList;
    }

    /**
     * @return the frontDisable
     */
    public boolean isFrontDisable() {
        return frontDisable;
    }

    /**
     * @param frontDisable the frontDisable to set
     */
    public void setFrontDisable(boolean frontDisable) {
        this.frontDisable = frontDisable;
    }

    /**
     * @return the rearDisable
     */
    public boolean isRearDisable() {
        return rearDisable;
    }

    /**
     * @param rearDisable the rearDisable to set
     */
    public void setRearDisable(boolean rearDisable) {
        this.rearDisable = rearDisable;
    }

    /**
     * @return the optionDisable
     */
    public boolean isOptionDisable() {
        return optionDisable;
    }

    /**
     * @param optionDisable the optionDisable to set
     */
    public void setOptionDisable(boolean optionDisable) {
        this.optionDisable = optionDisable;
    }
}
