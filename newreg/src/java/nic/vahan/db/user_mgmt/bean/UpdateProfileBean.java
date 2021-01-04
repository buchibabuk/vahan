/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.bean;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import nic.rto.vahan.common.MsgProperties;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.user_mgmt.dobj.UpdateProfileDobj;
import nic.vahan.db.user_mgmt.impl.UpdateProfileImpl;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.dobj.common.DOAuditTrail;
import nic.vahan.form.impl.Util;
import static nic.vahan.server.ServerUtil.Compare;
import nic.vahan.server.TOTP;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import nic.vahan.server.mail.MailSender;
import org.primefaces.PrimeFaces;

/**
 *
 * @author tranC103
 */
@ManagedBean
@ViewScoped
public class UpdateProfileBean implements ApproveDisapproveInterface, Serializable {

    private static final Logger LOGGER = Logger.getLogger(UpdateProfileBean.class);
    private UpdateProfileDobj updateDobj;
    private UpdateProfileDobj updateDobjPrev;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private List<ComparisonBean> prevChangedDataList;
    private boolean disableUpdateBtn = true;
    private String otp = null;
    private String sendedOtp = null;
    private boolean renderResendOTP;
    private boolean verifyCheck = false;
    private boolean emailCheck = false;
    UpdateProfileImpl updateImpl = new UpdateProfileImpl();
    private String verificationType;

    public UpdateProfileBean() {
        updateDobj = new UpdateProfileDobj();
    }

    @PostConstruct
    public void init() {
        UpdateProfileImpl updateImpl = new UpdateProfileImpl();
        updateDobj.setUserCode(Long.parseLong(Util.getEmpCode()));
        updateDobj = updateImpl.getUserData(updateDobj);

        try {
            updateDobjPrev = (UpdateProfileDobj) updateDobj.clone();
            verifyCheck = updateDobj.isMobile_verify();
            emailCheck = updateDobj.isEmail_verify();
        } catch (CloneNotSupportedException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    @Override
    public List<ComparisonBean> compareChanges() {

        if (updateDobjPrev == null) {
            return compBeanList;
        }
        Compare("User Name", updateDobjPrev.getUserName(), updateDobj.getUserName(), (ArrayList) compBeanList);
        Compare("Office No", updateDobjPrev.getOffNo(), updateDobj.getOffNo(), (ArrayList) compBeanList);
        Compare("Mobile No", updateDobjPrev.getMobileNo(), updateDobj.getMobileNo(), (ArrayList) compBeanList);
        Compare("Email ID", updateDobjPrev.getEmailID(), updateDobj.getEmailID(), (ArrayList) compBeanList);

        return compBeanList;
    }

    public String saveUserData() throws VahanException {
        String path = "";
        try {
            FacesContext faceContext = FacesContext.getCurrentInstance();
            Map requestParamMap = faceContext.getExternalContext().getRequestParameterMap();
            String uniqueToken = (String) requestParamMap.get("uniqueToken");
            if (!isInVisitedTokenList(faceContext, uniqueToken)) {
                throw new VahanException("Technical Error");
            }

            UpdateProfileImpl updateImpl = new UpdateProfileImpl();
            boolean flag = false;
            List<ComparisonBean> compareChanges = compareChanges();
            String changedData = ComparisonBeanImpl.changedDataContents(compareChanges);
            String dealerCode = ServerUtil.getDealerCode(updateDobj.getUserCode(), updateDobj.getStateCode(), updateDobj.getOffCode());
            flag = updateImpl.updateUserData(updateDobj, changedData, verificationType, dealerCode);
            if (flag) {
                JSFUtils.setFacesMessage("Profile Updated Successfully.", null, JSFUtils.INFO);
                path = "home";
            } else {
                if (compareChanges.isEmpty()) {
                    JSFUtils.setFacesMessage("Please Update Your Data", null, JSFUtils.INFO);
                } else {
                    JSFUtils.setFacesMessage("Technical Error", null, JSFUtils.ERROR);
                }
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return path;
    }

    public void generateOTP() {
        Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String otpReason = map.get("otpGenReason");
        if (map.get("verifyType") != null) {
            verificationType = map.get("verifyType");
        }
        String otpMsg = null;
        String resetVerifyMsg = null;
        try {
            boolean userData = new UpdateProfileImpl().getMobileNoAndEmailId(verificationType, updateDobj.getMobileNo(), updateDobj.getEmailID(), updateDobj.getUser_id());

            if (otpReason.equalsIgnoreCase("new")) {
                if ("mobileno".equals(verificationType) && ((updateDobjPrev.getMobileNo() == updateDobj.getMobileNo() && verifyCheck))) {
                    throw new VahanException("Mobile No is already verified. Please use different Mobile No, if you want to change");
                } else if ("email".equals(verificationType) && ((updateDobjPrev.getEmailID().equals(updateDobj.getEmailID()) && emailCheck))) {
                    throw new VahanException("Email-Id is already verified. Please use different Email-Id, if you want to change");
                } else if (userData) {
                    if ("mobileno".equals(verificationType) && !TableConstants.OFFICE_TIMING_EXEMPTION_CATG.contains("," + updateDobj.getUser_catg() + ",") && !UpdateProfileImpl.IsFitOfficerOrNot(updateDobj.getUserCode())) {
                        throw new VahanException("Mobile No is already verified with other UserId. Please use different Mobile No");
                    } else if ("email".equals(verificationType) && !TableConstants.OFFICE_TIMING_EXEMPTION_CATG.contains("," + updateDobj.getUser_catg() + ",") && !UpdateProfileImpl.IsFitOfficerOrNot(updateDobj.getUserCode())) {
                        throw new VahanException("Email-Id is already verified with other UserId. Please use different Email-Id");
                    }
                }
                sendedOtp = TOTP.getOTPTimeStamp();
                if ("mobileno".equals(verificationType)) {
                    otpMsg = sendedOtp + ": is the OTP for mobile verification in VAHAN 4.0";
                    ServerUtil.sendOTP(String.valueOf(updateDobj.getMobileNo()), otpMsg, Util.getUserStateCode());
                    if (updateDobjPrev.getMobileNo() != updateDobj.getMobileNo()) {
                        resetVerifyMsg = "Someone has change your mobile number in Vahan 4.0";
                        ServerUtil.sendSMS(String.valueOf(updateDobjPrev.getMobileNo()), resetVerifyMsg);
                    }
                } else if ("email".equals(verificationType)) {
                    otpMsg = sendedOtp + ": is the OTP for Email-Id verification in VAHAN 4.0";
                    if (updateDobj.getEmailID() != null && !updateDobj.getEmailID().equals("")) {
                        MailSender sendMail = new MailSender(updateDobj.getEmailID(), otpMsg, "OTP for VAHAN4 Emailid Verification");
                        sendMail.start();
                    }
                    if (!updateDobjPrev.getEmailID().equals(updateDobj.getEmailID())) {
                        resetVerifyMsg = "Someone has change your Email-Id in Vahan 4.0";
                        MailSender sendMail = new MailSender(updateDobjPrev.getEmailID(), resetVerifyMsg, "Email-Id Changed in Vahan 4.0");
                        sendMail.start();
                    }
                }
                PrimeFaces.current().executeScript("PF('otp').show()");
                PrimeFaces.current().ajax().update("updateProfileForm:otpText");
            } else if (otpReason.equalsIgnoreCase("resend")) {
                sendedOtp = TOTP.getOTPTimeStamp();
                otpMsg = sendedOtp + ": is the OTP for request verification in VAHAN 4.0";
                if ("mobileno".equals(verificationType)) {
                    ServerUtil.sendOTP(String.valueOf(updateDobj.getMobileNo()), otpMsg, Util.getUserStateCode());
                } else if ("email".equals(verificationType)) {
                    MailSender sendMail = new MailSender(updateDobj.getEmailID(), otpMsg, "OTP for VAHAN4 Email-Id Verification");
                    sendMail.start();
                }
            }
        } catch (VahanException ve) {
            JSFUtils.setFacesMessage(ve.getMessage(), null, JSFUtils.INFO);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            JSFUtils.setFacesMessage("Something Went Wrong While generating OTP", null, JSFUtils.INFO);

        }
    }

    public String logout() {
        HttpSession session = null;
        String user_id = "";
        try {
            session = Util.getSession();
            if (session != null) {
                user_id = (String) session.getAttribute("user_id");
                if (user_id != null && user_id.trim().length() > 0) {
                    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                    ServerUtil.updateLoginStatus(user_id, "D", null);
                    new ServerUtil().auditTrailDAO(new DOAuditTrail(user_id, request.getRemoteAddr(), MsgProperties.getKeyValue("logout.success.actiontype"), MsgProperties.getKeyValue("audit.trail.status.success")));
                }
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
        } catch (VahanException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
        } finally {
            if (session != null && session.getAttribute("state_cd") != null) {
                if (user_id != null && Util.getLoginUsers() != null) {
                    Util.getLoginUsers().remove(user_id);
                }
                Util.removeAllSessionAttribute(session);
            }
        }
        return "login_home";
    }

    public String validateOTP() throws VahanException {
        String path = "";
        try {
            if (CommonUtils.isNullOrBlank(sendedOtp)) {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
            if (sendedOtp.equals(otp)) {
                PrimeFaces.current().executeScript("PF('otp').hide()");
                setDisableUpdateBtn(false);
                verifyCheck = true;
                path = saveUserData();
            } else {
                throw new VahanException("Invalid OTP, Please enter correct OTP");
            }
        } catch (VahanException ve) {
            JSFUtils.setFacesMessage(ve.getMessage(), null, JSFUtils.ERROR);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.setFacesMessage(TableConstants.SomthingWentWrong, null, JSFUtils.ERROR);
        }

        return path;
    }

    private boolean isInVisitedTokenList(FacesContext faceContext, String uniqueToken) {
        return getVisitedTokenMap(faceContext).containsValue(uniqueToken);
    }

    private Map getVisitedTokenMap(FacesContext faceContext) {
        return (Map) faceContext.getApplication().createValueBinding("#{visitedTokenMap}").getValue(faceContext);
    }

    public UpdateProfileDobj getUpdateDobj() {
        return updateDobj;
    }

    public void setUpdateDobj(UpdateProfileDobj updateDobj) {
        this.updateDobj = updateDobj;
    }

    public UpdateProfileDobj getUpdateDobjPrev() {
        return updateDobjPrev;
    }

    public void setUpdateDobjPrev(UpdateProfileDobj updateDobjPrev) {
        this.updateDobjPrev = updateDobjPrev;
    }

    @Override
    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    public void setCompBeanList(List<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
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
    public String saveAndMoveFile() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the disableUpdateBtn
     */
    public boolean isDisableUpdateBtn() {
        return disableUpdateBtn;
    }

    /**
     * @param disableUpdateBtn the disableUpdateBtn to set
     */
    public void setDisableUpdateBtn(boolean disableUpdateBtn) {
        this.disableUpdateBtn = disableUpdateBtn;
    }

    /**
     * @return the otp
     */
    public String getOtp() {
        return otp;
    }

    /**
     * @param otp the otp to set
     */
    public void setOtp(String otp) {
        this.otp = otp;
    }

    /**
     * @return the renderResendOTP
     */
    public boolean isRenderResendOTP() {
        return renderResendOTP;
    }

    /**
     * @param renderResendOTP the renderResendOTP to set
     */
    public void setRenderResendOTP(boolean renderResendOTP) {
        this.renderResendOTP = renderResendOTP;
    }

    /**
     * @return the verifyCheck
     */
    public boolean isVerifyCheck() {
        return verifyCheck;
    }

    /**
     * @param verifyCheck the verifyCheck to set
     */
    public void setVerifyCheck(boolean verifyCheck) {
        this.verifyCheck = verifyCheck;
    }

    /**
     * @return the emailCheck
     */
    public boolean isEmailCheck() {
        return emailCheck;
    }

    /**
     * @param emailCheck the emailCheck to set
     */
    public void setEmailCheck(boolean emailCheck) {
        this.emailCheck = emailCheck;
    }

    /**
     * @return the verificationType
     */
    public String getVerificationType() {
        return verificationType;
    }

    /**
     * @param verificationType the verificationType to set
     */
    public void setVerificationType(String verificationType) {
        this.verificationType = verificationType;
    }
}
