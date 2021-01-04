/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.UUID;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import nic.rto.vahan.common.CryptographyAES;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.user_mgmt.dobj.ChangePwdDobj;
import nic.vahan.form.impl.ForgetPasswordImpl;
import nic.vahan.form.impl.RegVehCancelReceiptImpl;
import nic.vahan.form.impl.SmsMailOTPImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import nic.vahan.server.mail.MailSender;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author acer
 */
@ManagedBean
@ViewScoped
public class ForgetPasswordBean implements Serializable {

    private ChangePwdDobj userinfo;
    private boolean showResend = false;
    private boolean showEnterOtp = false;
    private String user_id;
    private String forgetPasswordOtp = null;
    private String newOtp = "";
    private String error_msg = "";
    private String sentOtp = "";
    private String userCd = "";
    private String state_cd = "";
    private int off_cd;
    private boolean renderSendMailPanel = false;
    private boolean renderResetPassPanel = false;
    private static final Logger logger = Logger.getLogger(ForgetPasswordBean.class);

    public ForgetPasswordBean() {
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            String resetTokenId = httpServletRequest.getParameter("encdata");
            if (!CommonUtils.isNullOrBlank(resetTokenId)) {
                resetTokenId = new CryptographyAES().getDecriptedString(resetTokenId);
                if (resetTokenId != null) {
                    userinfo = new ForgetPasswordImpl().getTokenDetail(resetTokenId);
                    if (userinfo != null) {
                        renderResetPassPanel = true;
                    }
                }
            } else {
                renderSendMailPanel = true;
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void sendOtpMailForgetPassword(String otpType) {
        FacesMessage message = null;
        try {
            String[] arr = ForgetPasswordImpl.getUserCd(user_id).split("`");
            if (arr != null && arr.length == 3) {
                if (!CommonUtils.isNullOrBlank(arr[0])) {
                    if (otpType != null && (otpType.equals("sendOtp") || otpType.equals("resendOtp"))) {
                        forgetPasswordOtp = SmsMailOTPImpl.sendOTPorMail(arr[0], "OTP for Reset Password ", otpType, forgetPasswordOtp, "OTP for Reset Password ");
                        if (forgetPasswordOtp != null && !forgetPasswordOtp.equals("")) {
                            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "OTP has been sent to your Mobile No", "OTP has been sent to your Mobile No");
                            FacesContext.getCurrentInstance().addMessage(null, message);
                            showResend = true;
                            showEnterOtp = true;
                            userCd = arr[0];
                            state_cd = arr[1];
                            off_cd = Integer.parseInt(arr[2]);
                        } else {
                            throw new VahanException("Unable to generate OTP and send it.");
                        }
                    }
                }
            } else {
                throw new VahanException(user_id + ": is not allowed to use Forgot Password Functionality");
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ve.getMessage(), ve.getMessage()));
        } catch (Exception e) {
            logger.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void resetPassword() {

        String strMsg = "";
        FacesMessage message = null;
        StringBuilder sb = new StringBuilder("@$");
        String newOtp = getNewOtp().trim();
        String emailId = "";
        String mobileNo = "";
        String contextIpPath = null;
        if (getNewOtp().isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Enter OTP", "Please Enter OTP");
            FacesContext.getCurrentInstance().addMessage(null, message);
            setError_msg("Please Enter OTP");
        } else if (!getNewOtp().equalsIgnoreCase(forgetPasswordOtp)) {
            strMsg = "OTP not matched. Please enter correct OTP";
            JSFUtils.setFacesMessage(strMsg, null, JSFUtils.WARN);
            setNewOtp("");
            return;
        }
        try {
            String[] userData = new RegVehCancelReceiptImpl().getUserDetails(userCd).split(",");
            String uuid = UUID.randomUUID().toString();
            uuid = uuid.substring(uuid.length() - 10, uuid.length());
            String enuuid = new CryptographyAES().getEncriptedString(uuid);
            Boolean tokenstatus = ForgetPasswordImpl.insertToken(state_cd, off_cd, userCd, uuid);
            if (tokenstatus && userData != null && !userData.toString().equals("") && userData.length == 2) {
                mobileNo = userData[0];
                emailId = userData[1];
                contextIpPath = ServerUtil.getIpPath();

                String msgEmail = "Dear Sir/Madam ,<br/><br/>"
                        + " We have received your request for reset password.<br/><br/>"
                        + " To reset, please click the link below :<br/><br/>"
                        + contextIpPath + "/vahan/ui/login/forgetPassword.xhtml?encdata=" + enuuid
                        + " The password recovery link expires in 30 minutes after password recovery request initiation.<br/><br/>"
                        + " Please do not share this information with others and change the password immediately after first login.<br/><br/>"
                        + " **** Please do not reply to this mail as it is system genarated **** <br/><br/>";

                if (emailId != null && !emailId.equalsIgnoreCase("null")) {
                    MailSender sendMail = new MailSender(emailId, msgEmail, "Password Reset Email For: " + user_id);
                    sendMail.start();
                    PrimeFaces.current().ajax().update("passwordDialog");
                    PrimeFaces.current().executeScript("PF('resetpswd').show()");
                    return;
                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Email Id not found. Please update your profile", "Email Id not found. Please update your profile");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    emailId = "";
                }
            } else {
                setError_msg("");
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Try Again After Some Time.", "Please Try Again After Some Time.");
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
        } catch (Exception e) {
            logger.error("Exception occured in mail() method of ForgotPassword :- ", e);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void updatePassword() {
        String pwd = "";
        try {
            String strMsg = "";
            if (!CommonUtils.isNullOrBlank(userinfo.getNewPwd())
                    && !CommonUtils.isNullOrBlank(userinfo.getCnfrmNewPwd())) {
                if (userinfo.getNewPwd().equals(userinfo.getCnfrmNewPwd())) {
                    pwd = ServerUtil.sha256hex(userinfo.getCnfrmNewPwd());
                    Boolean status = ForgetPasswordImpl.updateUserPassword(userinfo.getUser_cd(), pwd, userinfo.getState_cd(), userinfo.getOff_cd());
                    if (status) {
                        PrimeFaces.current().ajax().update("passwordSucessDialog");
                        PrimeFaces.current().executeScript("PF('resetpswdsucess').show()");
                        return;
                    } else {
                        JSFUtils.setFacesMessage(TableConstants.SomthingWentWrong, null, JSFUtils.ERROR);
                    }
                } else {
                    JSFUtils.setFacesMessage("Password and Confirm Password not Matched!!", null, JSFUtils.WARN);
                }
            } else {
                strMsg = "Insufficient Data";
                JSFUtils.setFacesMessage(strMsg, null, JSFUtils.ERROR);
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
        } catch (Exception e) {
            logger.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void confirmPassBlurListener() {
        try {
            if (userinfo != null && !CommonUtils.isNullOrBlank(userinfo.getNewPwd()) && !CommonUtils.isNullOrBlank(userinfo.getCnfrmNewPwd())) {
                String user_oldpwd = ForgetPasswordImpl.getOldPassword(userinfo.getUser_cd());
                if (!userinfo.getNewPwd().equals(userinfo.getCnfrmNewPwd())) {
                    userinfo.setNewPwd("");
                    userinfo.setCnfrmNewPwd("");
                    PrimeFaces.current().ajax().update("receiveLink");
                    throw new VahanException("Password and Confirm Password not Matched !!!");
                }
                if (!CommonUtils.isNullOrBlank(user_oldpwd)) {
                    String newPwd = ServerUtil.sha256hex(userinfo.getNewPwd());
                    if (newPwd.equals(user_oldpwd)) {
                        userinfo.setNewPwd("");
                        userinfo.setCnfrmNewPwd("");
                        PrimeFaces.current().ajax().update("receiveLink");
                        throw new VahanException("New Password can't be same as Old Password. Please Use another Password!!");
                    }
                }
            }
        } catch (VahanException ve) {
            JSFUtils.showMessage(ve.getMessage());
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public String returnHome() {
        return "homePage";
    }

    /**
     * @return the showResend
     */
    public boolean isShowResend() {
        return showResend;
    }

    /**
     * @param showResend the showResend to set
     */
    public void setShowResend(boolean showResend) {
        this.showResend = showResend;
    }

    /**
     * @return the showEnterOtp
     */
    public boolean isShowEnterOtp() {
        return showEnterOtp;
    }

    /**
     * @param showEnterOtp the showEnterOtp to set
     */
    public void setShowEnterOtp(boolean showEnterOtp) {
        this.showEnterOtp = showEnterOtp;
    }

    /**
     * @return the forgetPasswordOtp
     */
    public String getForgetPasswordOtp() {
        return forgetPasswordOtp;
    }

    /**
     * @param forgetPasswordOtp the forgetPasswordOtp to set
     */
    public void setForgetPasswordOtp(String forgetPasswordOtp) {
        this.forgetPasswordOtp = forgetPasswordOtp;
    }

    /**
     * @return the user_id
     */
    public String getUser_id() {
        return user_id;
    }

    /**
     * @param user_id the user_id to set
     */
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    /**
     * @return the newOtp
     */
    public String getNewOtp() {
        return newOtp;
    }

    /**
     * @param newOtp the newOtp to set
     */
    public void setNewOtp(String newOtp) {
        this.newOtp = newOtp;
    }

    /**
     * @return the error_msg
     */
    public String getError_msg() {
        return error_msg;
    }

    /**
     * @param error_msg the error_msg to set
     */
    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    /**
     * @return the userinfo
     */
    public ChangePwdDobj getUserinfo() {
        return userinfo;
    }

    /**
     * @param userinfo the userinfo to set
     */
    public void setUserinfo(ChangePwdDobj userinfo) {
        this.userinfo = userinfo;
    }

    /**
     * @return the renderSendMailPanel
     */
    public boolean isRenderSendMailPanel() {
        return renderSendMailPanel;
    }

    /**
     * @param renderSendMailPanel the renderSendMailPanel to set
     */
    public void setRenderSendMailPanel(boolean renderSendMailPanel) {
        this.renderSendMailPanel = renderSendMailPanel;
    }

    /**
     * @return the renderResetPassPanel
     */
    public boolean isRenderResetPassPanel() {
        return renderResetPassPanel;
    }

    /**
     * @param renderResetPassPanel the renderResetPassPanel to set
     */
    public void setRenderResetPassPanel(boolean renderResetPassPanel) {
        this.renderResetPassPanel = renderResetPassPanel;
    }
}
