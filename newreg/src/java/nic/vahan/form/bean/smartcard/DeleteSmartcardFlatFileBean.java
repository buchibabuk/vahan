/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.smartcard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.java.util.CommonUtils;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.smartcard.DeleteSmartcardFlatFileDobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.SmartCardImpl;
import nic.vahan.form.impl.SmsMailOTPImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author NIC
 */
@ManagedBean(name = "deletesmartcardflatfilebean")
@ViewScoped
public class DeleteSmartcardFlatFileBean extends AbstractApplBean implements ApproveDisapproveInterface, Serializable {

    private static final Logger LOGGER = Logger.getLogger(DeleteSmartcardFlatFileBean.class);
    private DeleteSmartcardFlatFileDobj dobj = new DeleteSmartcardFlatFileDobj();
    private DeleteSmartcardFlatFileDobj dobj_prev = null;
    private Date start_dt;
    private boolean flatFileDetails = false;
    private boolean oldApplication = false;
    private String vahanMessages;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private List<ComparisonBean> prevChangedDataList;
    private String appl_no;
    private SmartCardImpl impl = new SmartCardImpl();
    private boolean enableDisable = false;
    private String deleteSmartOtp = null;
    private String enterSmartRcptOtp = null;
    private String empCd;
    private boolean smartCardOtpType = false;
    private String returnFile;

    public DeleteSmartcardFlatFileBean() {
        empCd = Util.getEmpCode();
        try {

            if (getAppl_details() == null
                    || getAppl_details().getCurrent_state_cd() == null
                    || getAppl_details().getCurrent_off_cd() == 0) {
                vahanMessages = "Something went wrong, Please try again later...";
                return;
            }
            if (getAppl_details() != null) {
                if (appl_details.getCurrent_action_cd() == TableConstants.DELETE_SMART_CARD_FLAT_FILE_APPROVE) {
                    dobj = impl.getVehicleDataFromVaDeleteFlatFile(appl_details.getAppl_no());
                    dobj_prev = (DeleteSmartcardFlatFileDobj) dobj.clone();//for holding current dobj for using in the comparison.
                    flatFileDetails = true;
                    enableDisable = true;
                    prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                }
            }
        } catch (VahanException ve) {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void generatedFlatFileDetails() {
        try {

            start_dt = impl.getOfficeStartDate(dobj.getRegn_no(), Util.getUserStateCode(), Util.getUserLoginOffCode());
            if (start_dt == null) {
                flatFileDetails = false;
                throw new VahanException("VAHAN4 has not started in this office OR Registration number does not belong to this RTO");
            }
            dobj = impl.getGeneratedFlatFileRecord(dobj.getRegn_no());
            if (dobj == null) {
                flatFileDetails = false;
                throw new VahanException("Flat File is not Generated for this Vehicle");
            }
            boolean oldApplication = impl.isOldApplication(dobj.getRcpt_no());
            if (!oldApplication) {
                flatFileDetails = false;
                throw new VahanException("Flat File can not be deleted for this vehicle as it is generated through Vahan4.0");
            } else {
                flatFileDetails = true;
                enableDisable = true;
            }

        } catch (VahanException ve) {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void saveDeleteEntry() {

        Exception e = null;
        try {
            Status_dobj status_dobj = new Status_dobj();
            status_dobj.setRegn_no(dobj.getRegn_no());
            status_dobj.setPur_cd(TableConstants.DELETE_SMART_CARD_FLAT_FILE);
            status_dobj.setFlow_slno(1);//initial flow serial no.
            status_dobj.setFile_movement_slno(1);//initial file movement serial no.
            status_dobj.setState_cd(getAppl_details().getCurrent_state_cd());
            status_dobj.setOff_cd(getAppl_details().getCurrent_off_cd());
            status_dobj.setEmp_cd(0);
            status_dobj.setSeat_cd("");
            status_dobj.setCntr_id("");
            status_dobj.setStatus("N");
            status_dobj.setOffice_remark("");
            status_dobj.setPublic_remark("");
            status_dobj.setFile_movement_type("F");
            status_dobj.setUser_id(appl_details.getCurrentEmpCd());
            status_dobj.setAppl_date(ServerUtil.getSystemDateInPostgres());
            status_dobj.setUser_type("");
            status_dobj.setEntry_ip("");
            status_dobj.setEntry_status("");//for New File
            status_dobj.setConfirm_ip("");
            status_dobj.setConfirm_status("");
            status_dobj.setConfirm_date(new java.util.Date());

            appl_no = impl.insertIntoVaDeleteFlatFile(status_dobj, dobj);
            dobj.setAppl_no(appl_no);
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Something went wrong, Please try again."));
            return;
        }
        //RequestContext rc = RequestContext.getCurrentInstance();
        PrimeFaces.current().ajax().update("db_reopened");
        PrimeFaces.current().executeScript("PF('dlgdb_reopened').show()");
    }

    public DeleteSmartcardFlatFileDobj getDobj() {
        return dobj;
    }

    public void setDobj(DeleteSmartcardFlatFileDobj dobj) {
        this.dobj = dobj;
    }

    public Date getStart_dt() {
        return start_dt;
    }

    public void setStart_dt(Date start_dt) {
        this.start_dt = start_dt;
    }

    /**
     * @return the flatFileDetails
     */
    public boolean isFlatFileDetails() {
        return flatFileDetails;
    }

    /**
     * @param flatFileDetails the flatFileDetails to set
     */
    public void setFlatFileDetails(boolean flatFileDetails) {
        this.flatFileDetails = flatFileDetails;
    }

    /**
     * @return the oldApplication
     */
    public boolean isOldApplication() {
        return oldApplication;
    }

    /**
     * @param oldApplication the oldApplication to set
     */
    public void setOldApplication(boolean oldApplication) {
        this.oldApplication = oldApplication;
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

    @Override
    public String save() {
        String return_location = "";
        try {
            List<ComparisonBean> compareChanges = compareChanges();
            if (!compareChanges.isEmpty() || dobj_prev == null) { //save only when data is really changed by user
                SmartCardImpl.makeChangeDeleteFlatFileData(dobj, ComparisonBeanImpl.changedDataContents(compareChanges));
            }
            return_location = "seatwork";
        } catch (VahanException vme) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", vme.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Error-File Could Not Save and Move Due to Technical Error in Database"));
        }
        return return_location;
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        if (dobj_prev == null) {
            return compBeanList;
        }
        compBeanList.clear();
        Compare("reason", dobj_prev.getReason(), dobj.getReason(), compBeanList);
        return getCompBeanList();
    }

    @Override
    public String saveAndMoveFile() {
        String return_location = "";
        try {
            if (CommonUtils.isNullOrBlank(returnFile)) {
                returnFile = otpDeleteSmartcardFlatfile("sendOtp");
            }
            if (!CommonUtils.isNullOrBlank(returnFile)) {
                Status_dobj status = new Status_dobj();
                status.setAppl_dt(getAppl_details().getAppl_dt());
                status.setAppl_no(getAppl_details().getAppl_no());
                status.setPur_cd(getAppl_details().getPur_cd());
                status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
                status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
                status.setStatus(getApp_disapp_dobj().getNew_status());

                status.setCurrent_role(appl_details.getCurrent_action_cd());
                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)) {
                    status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                    impl.update_deleteFlatFile_Status(dobj, dobj_prev, status, ComparisonBeanImpl.changedDataContents(compareChanges()));
                    return_location = "seatwork";
                }
            }

        } catch (VahanException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-TEB-1Could not Processed due to Technical Error in Database.", "Error-TEB1-Could not Processed due to Technical Error in Database."));
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        return return_location;
    }

    public String otpDeleteSmartcardFlatfile(String otpType) throws VahanException {
        try {
            TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();
            if (tmConfigurationDobj != null && tmConfigurationDobj.getTmConfigOtpDobj() != null && tmConfigurationDobj.getTmConfigOtpDobj().isDelete_smartcard_flatfile_withOtp()) {
                if (otpType != null && otpType.equals("sendOtp") || otpType.equals("resendOtp")) {
                    deleteSmartOtp = SmsMailOTPImpl.sendOTPorMail(empCd, "OTP for Delete Smartcard Flatfile. ", otpType, deleteSmartOtp, "OTP for Delete Smartcard Flatfile. ");
                    if (deleteSmartOtp != null && !deleteSmartOtp.equals("")) {
                        PrimeFaces.current().ajax().update("otp_confirmation");
                        PrimeFaces.current().executeScript("PF('otp_confrm').show()");
                    } else {
                        throw new VahanException("Unable to generate OTP and send it.");
                    }
                } else if (otpType.equals("confirmOtp")) {
                    if (deleteSmartOtp != null && deleteSmartOtp.equals(enterSmartRcptOtp)) {
                        PrimeFaces.current().executeScript("PF('otp_confrm').hide()");
                        returnFile = "Confirm";
                        returnFile = saveAndMoveFile();
                    } else {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Invalid OTP, Please enter correct OTP", "Invalid OTP, Please enter correct OTP"));
                    }
                }
            } else {
                returnFile = "Confirm";
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem occur while Sending OTP");
        }
        return returnFile;
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
     * @return the deleteSmartOtp
     */
    public String getDeleteSmartOtp() {
        return deleteSmartOtp;
    }

    /**
     * @param deleteSmartOtp the deleteSmartOtp to set
     */
    public void setDeleteSmartOtp(String deleteSmartOtp) {
        this.deleteSmartOtp = deleteSmartOtp;
    }

    /**
     * @return the enterSmartRcptOtp
     */
    public String getEnterSmartRcptOtp() {
        return enterSmartRcptOtp;
    }

    /**
     * @param enterSmartRcptOtp the enterSmartRcptOtp to set
     */
    public void setEnterSmartRcptOtp(String enterSmartRcptOtp) {
        this.enterSmartRcptOtp = enterSmartRcptOtp;
    }

    /**
     * @return the smartCardOtpType
     */
    public boolean isSmartCardOtpType() {
        return smartCardOtpType;
    }

    /**
     * @param smartCardOtpType the smartCardOtpType to set
     */
    public void setSmartCardOtpType(boolean smartCardOtpType) {
        this.smartCardOtpType = smartCardOtpType;
    }

    /**
     * @return the returnFile
     */
    public String getReturnFile() {
        return returnFile;
    }

    /**
     * @param returnFile the returnFile to set
     */
    public void setReturnFile(String returnFile) {
        this.returnFile = returnFile;
    }

    public boolean isEnableDisable() {
        return enableDisable;
    }

    public void setEnableDisable(boolean enableDisable) {
        this.enableDisable = enableDisable;
    }
}
