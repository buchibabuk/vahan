/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import dao.UserDAO;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import nic.rto.vahan.common.MsgProperties;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.user_mgmt.dobj.UserAuthorityDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.UserInfo;
import nic.vahan.form.dobj.common.DOAuditTrail;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import java.util.Map;
import nic.vahan.db.MasterTableFiller;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import javax.annotation.PostConstruct;
import nic.rto.vahan.common.ApplicationConfiguration;
import nic.vahan.form.dobj.IpAddressEntryDobj;
import nl.captcha.Captcha;
import org.primefaces.PrimeFaces;

@ManagedBean
@SessionScoped
/**
 *
 * @author User
 */
public class LoginBean implements Serializable, HttpSessionBindingListener {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LoginBean.class);
    private String password;
    private String user_id;
    private String hiddenRandomNo;
    private static int logfile = 0;
    private DOAuditTrail doaudittrail = null;
    private boolean dispalyStatusReport = true;
    private boolean forcedLoginFlag = false;
    private String ipAddress = "";
    private String serverIpAddress = "";
    private String session_id = "";
    private boolean pmtOwnerByName = false;
    private boolean collSummaryDisplay = false;
    private BarChartModel barChartModel7;
    private Map<String, String> totalRtoRecords = null;
    private Map<String, String> runningRtoRecords = null;
    private Map<String, String> nonRtoRecords = null;
    private BarChartModel barModel;
    private String mobile_otp;
    private String opvalue = "P";
    private String matchedOTP;
    private String mobileno;
    private String otpNote;
    private boolean isOTPLoggedUser = false;
    private boolean renderLoginButton = false;
    private boolean renderOTPFields = false;
    private boolean renderLoginPanel = false;
    private boolean renderValidateUserIdPanel = true;
    private boolean renderRegisteredVehReport = true;
    private boolean renderUserInformationPanel = false;
    private String totalV4_offices = "";
    private int counter;
    private boolean forgetpassword = false;
    private boolean renderOTPPanel = false;
    private boolean renderSubmitButton = false;
    private boolean renderBackButton = false;
    private boolean renderOTPBasedFields = false;
    private boolean dealerRegnPendencyDisplay = false;
    private boolean renderDealerHSRPReport = false;
    private String statesV4;
    private String officsV4;

    public LoginBean() {
    }

    public LoginBean(HttpServletRequest req, boolean forcedLoginFlag) {
//        resetRandomNo();
        HttpServletRequest request = req;

        //(HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        serverIpAddress = request.getLocalAddr();
        int serverPort = request.getLocalPort();
        if (serverIpAddress != null && serverIpAddress.length() > 0) {
            String strPort = (serverPort - 80) + "";
            String splitIp[] = serverIpAddress.split("\\.");
            if (splitIp.length > 3) {
                serverIpAddress = splitIp[3];
                serverIpAddress = "~" + serverIpAddress + "~" + strPort;
            } else {
                serverIpAddress = "";
            }
        } else {
            serverIpAddress = "";
        }
        String[] words = ApplicationConfiguration.totalRunningV4_offices.split("\\s");//splits the string based on whitespace  
        if (words.length != 0) {
            setStatesV4(words[0]);
            setOfficsV4(words[3]);
        }
        setTotalV4_offices(ApplicationConfiguration.totalRunningV4_offices);

        setSession(req, forcedLoginFlag);
    }

    @PostConstruct
    public void LoginBean() {
        barChartModel();
        reset();
    }

    private void barChartModel() {
        barChartModel7 = initBarChartModel();
        barChartModel7.setAnimate(true);
        barChartModel7.setLegendPosition("ne");
        barChartModel7.setLegendCols(5);
        barChartModel7.setShowPointLabels(true);
        barChartModel7.setStacked(true);

        Axis xAxisBarChartModel7 = barChartModel7.getAxis(AxisType.X);
        if (("Invalid Selection").equalsIgnoreCase(barChartModel7.getSeries().get(0).getLabel())) {
            xAxisBarChartModel7.setTickAngle(0);
        } else {
            xAxisBarChartModel7.setTickAngle(-40);
        }
    }

    private BarChartModel initBarChartModel() {
        BarChartModel barchar_model = new BarChartModel();
        try {
            String qry = "";
            runningRtoRecords = new LinkedHashMap();
            totalRtoRecords = new LinkedHashMap();
            nonRtoRecords = new LinkedHashMap();

            runningRtoRecords.putAll(MasterTableFiller.runningRtoRecords);
            totalRtoRecords.putAll(MasterTableFiller.totalRtoRecords);
            nonRtoRecords.putAll(MasterTableFiller.nonRtoRecords);

            ChartSeries running_rto = new ChartSeries();
            ChartSeries total_rto = new ChartSeries();
            ChartSeries non_rto = new ChartSeries();

            String strSeriesColor = "298B1A,FCBB3A,FF0000";
            barchar_model.setSeriesColors(strSeriesColor);

            if (runningRtoRecords.size() > 0 || totalRtoRecords.size() > 0 || nonRtoRecords.size() > 0) {

                for (Map.Entry<String, String> dataValue : runningRtoRecords.entrySet()) {
                    if (!CommonUtils.isNullOrBlank(dataValue.getValue() + "")) {
                        running_rto.setLabel("(Centralized Vahan4: " + ApplicationConfiguration.totalVahan4_offices + " Offices) ");
                        running_rto.set(dataValue.getKey(), Long.parseLong(dataValue.getValue()));
                    } else {
                        running_rto.set(" ", Long.parseLong("0"));
                    }
                }

                for (Map.Entry<String, String> dataValue : totalRtoRecords.entrySet()) {
                    if (!CommonUtils.isNullOrBlank(dataValue.getValue() + "")) {
                        total_rto.setLabel("(Older Vahan: " + ApplicationConfiguration.totalOlderVahan_offices + " Offices) ");
                        total_rto.set(dataValue.getKey(), Long.parseLong(dataValue.getValue()) - Long.parseLong(runningRtoRecords.get(dataValue.getKey())) - Long.parseLong(nonRtoRecords.get(dataValue.getKey())));
                    } else {
                        total_rto.set(" ", Long.parseLong("0"));
                    }
                }

                for (Map.Entry<String, String> dataValue : nonRtoRecords.entrySet()) {
                    if (!CommonUtils.isNullOrBlank(dataValue.getValue() + "")) {
                        non_rto.setLabel(" (Non-Vahan: " + ApplicationConfiguration.getTotalnonVahan_states() + " States (" + ApplicationConfiguration.getTotalnonVahan_offices() + " Offices)) ");
                        non_rto.set(dataValue.getKey(), Long.parseLong(dataValue.getValue()));
                    } else {
                        non_rto.set(" ", Long.parseLong("0"));
                    }
                }

                barchar_model.addSeries(running_rto);
                barchar_model.addSeries(total_rto);
                barchar_model.addSeries(non_rto);

            } else {
                // Do Not Change
                total_rto.set("No Data Found", 0);
                total_rto.setLabel("Invalid Selection");
                barchar_model.addSeries(total_rto);

                running_rto.set("No Data Found", 0);
                running_rto.setLabel("Invalid Selection");
                barchar_model.addSeries(running_rto);

                non_rto.set("No Data Found", 0);
                non_rto.setLabel("Invalid Selection");
                barchar_model.addSeries(non_rto);

                barchar_model.setTitle("No Data Found for This Selection");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        totalRtoRecords.clear();
        runningRtoRecords.clear();
        nonRtoRecords.clear();

        return barchar_model;
    }

    public LoginBean(String password, String user_id) {
        this.password = password;
        this.user_id = user_id;
    }

    private void resetRandomNo() {
        hiddenRandomNo = ServerUtil.generateRandomAlphaNumeric(40);
    }

    public void regenrateOTP() {
        try {
            if (counter < 2) {
                counter++;
                String[] arr = UserDAO.generateLoginType(user_id, true).split("`");
                if (arr != null && arr.length == 6) {
                    opvalue = arr[0];
                    mobileno = arr[1];
                    matchedOTP = arr[2];
                }
            } else {
                throw new VahanException("You can't Resend OTP more than two times. ");
            }
        } catch (VahanException vex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, vex.getMessage(), vex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public String validateUserid() {
        String path = "";
        String empCd = "";
        List<IpAddressEntryDobj> ipAddressFinalList = new ArrayList<>();
        try {
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            String captcha = (String) session.getAttribute("captcha");
            Captcha secretcaptcha = (Captcha) session.getAttribute("serverCaptcha");
            if (captcha == null) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Verification Code is missing", "Verification Code is missing");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return "";
            } else {
                if (!secretcaptcha.isCorrect(captcha)) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Verification code does not match", "Verification code does not match");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return "";
                }
            }
            if (logfile == 0) {
                logfile++;

                HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                String serverAddrWithPort = request.getLocalAddr();
                int serverPort = request.getLocalPort();
                if (serverAddrWithPort != null && serverAddrWithPort.length() > 0) {
                    String splitIp[] = serverAddrWithPort.split("\\.");
                    if (splitIp.length > 3) {
                        serverAddrWithPort = "_" + serverAddrWithPort + "_" + serverPort;
                    } else {
                        serverAddrWithPort = "-" + serverPort;
                    }
                } else {
                    serverAddrWithPort = "-" + serverPort;
                }
                System.setProperty("serverAddrWithPort", serverAddrWithPort);

                Properties properties = new Properties();
                InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("/resources/log4j.properties");
                properties.load(inputStream);
                PropertyConfigurator.configure(properties);
                LOGGER.info("=====Log4j=====Properties file loaded Successfully=====");
            }

            List<UserInfo> logList = UserDAO.login(user_id);
            if (logList != null && !logList.isEmpty()) {
//                if ("RJ".equalsIgnoreCase(logList.get(0).getState_cd())) {
//                    path = "";
//                    throw new VahanException("As per the instruction from the Rajasthan State Transport Department, The operation on Vahan4 has been stopped in Rajasthan until further order . Sorry for the inconvenience.....");
//                }
                // For check block dealer details
                if (TableConstants.USER_CATG_DEALER_ADMIN.equals(logList.get(0).getUser_catg())) {
                    String dealerCd = ServerUtil.getDealerCodeByUserCd(logList.get(0).getEmp_cd(), logList.get(0).getState_cd());
                    if (!CommonUtils.isNullOrBlank(dealerCd)) {
                        String dealerStatusMsg = Home_Impl.getDealerBlockUnBlockStatus(dealerCd);
                        if (dealerStatusMsg != null && !dealerStatusMsg.isEmpty()) {
                            throw new VahanException("Your Dealership has been blocked by the respective registering authority due to " + dealerStatusMsg + ".");
                        }
                    } else {
                        throw new VahanException("Dealer code not available for User Id : " + logList.get(0).getUser_id() + ", Please Contact to the System Administrator.");
                    }
                }

                // for check Restrict User Category for Login
                UserDAO.getRestrictUserCatg(logList.get(0).getState_cd(), logList.get(0).getUser_catg());

                //for check IP address
                boolean isLoginViaIP = UserDAO.getIpAddress(logList.get(0).getState_cd());
                if (isLoginViaIP) {
                    ipAddressFinalList = IpAddressEntryImpl.getIpAddressList(logList.get(0).getState_cd(), logList.get(0).getOff_cd());
                    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                    ipAddress = request.getRemoteAddr();
                    boolean ipValid = false;
                    if (ipAddressFinalList != null && ipAddress != null && !ipAddressFinalList.isEmpty() && !TableConstants.OFFICE_TIMING_EXEMPTION_CATG.contains("," + logList.get(0).getUser_catg() + ",")) {
                        for (IpAddressEntryDobj dobj : ipAddressFinalList) {
                            if (dobj.getIpAddress().equals(ipAddress)) {
                                ipValid = true;
                                break;
                            }
                        }
                        if (!ipValid) {
                            path = "";
                            throw new VahanException(ipAddress + " IP Address is not Authorised. Please Contact to Office Admin!");
                        }
                    }
                }
                boolean flag = UserDAO.getBlockUser(user_id);
                empCd = String.valueOf(logList.get(0).getEmp_cd());
                if (!flag) {
                    boolean isverified = UserDAO.getVerificationDetails(empCd);
                    if (isverified) {
                        boolean isOTPBased = UserDAO.getOTPBasedLoginDetails(user_id);
                        if (isOTPBased) {
                            PasswordPanel();
                            forgetPassword(logList.get(0).getUser_catg());
                        } else {
                            PasswordNonOtpPanel();
                            forgetPassword(logList.get(0).getUser_catg());
                        }
                    } else {
                        PasswordNonOtpPanel();
                        forgetPassword(logList.get(0).getUser_catg());
                    }
                } else {
                    String msg = user_id + " is Blocked, Please contact to Admin !!!";
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, msg, msg);
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    user_id = null;
                }
            } else {
                user_id = null;
                path = "/ui/userNotFound.xhtml?faces-redirect=true";
            }
        } catch (VahanException vex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, vex.getMessage(), vex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            user_id = null;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            user_id = null;
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        return path;
    }

    public void PasswordPanel() {
        renderLoginButton = false;
        renderSubmitButton = true;
        renderLoginPanel = true;
        isOTPLoggedUser = false;
        renderValidateUserIdPanel = false;
        renderBackButton = true;
        PrimeFaces.current().ajax().update("loginPanelOutput");
    }

    public void PasswordNonOtpPanel() {
        renderLoginPanel = true;
        isOTPLoggedUser = false;
        renderValidateUserIdPanel = false;
        renderBackButton = true;
        renderLoginButton = true;
        renderSubmitButton = false;
        renderOTPBasedFields = false;
        PrimeFaces.current().ajax().update("loginPanelOutput");

    }

    public void PasswordOtpPanel(String[] arr) {
        mobileno = arr[1];
        matchedOTP = arr[2];
        renderLoginButton = true; // if user id valid.
        renderLoginPanel = false;
        renderOTPBasedFields = true; // if login as OTP 
        isOTPLoggedUser = true;
        renderValidateUserIdPanel = false;
        renderOTPPanel = true;
        renderSubmitButton = false;
        otpNote = "One Time Password (OTP) has been sent to your registered Mobile No " + "*******" + mobileno.substring(mobileno.length() - 3, mobileno.length()) + " and OTP is valid upto " + returnCurrentOfficeTime();
        PrimeFaces.current().ajax().update("loginPanelOutput");
    }

    private String returnCurrentOfficeTime() {
        try {
            String[] time = TableConstants.MORNING_TIMING_OTP.split("-");
            Calendar cal = Calendar.getInstance();
            DateFormat st = new SimpleDateFormat("HH:mm:ss");
            String currentTime = st.format(cal.getTime());
            System.out.println(st.parse(time[0]));
            if (st.parse(time[0]).getTime() <= st.parse(currentTime).getTime() && st.parse(currentTime).getTime() <= st.parse(time[1]).getTime()) {
                return time[1];
            } else {
                return (TableConstants.EVENING_TIMING_OTP.substring(TableConstants.EVENING_TIMING_OTP.indexOf('-') + 1));
            }
        } catch (ParseException ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
        }
        return "12 Hours after first OTP";
    }

    // Not called in newregistration
    public String loginProject() {
        boolean validUser = false;
        FacesMessage message = null;
        String msg = "Invalid Login. Please Try Again!!!";
        message = new FacesMessage(FacesMessage.SEVERITY_WARN, msg, msg);
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        ipAddress = request.getRemoteAddr();
        session_id = request.getSession().getId();

        try {
            if (renderOTPBasedFields) {
                if (CommonUtils.isNullOrBlank(getMobile_otp())) {
                    JSFUtils.showMessage("Please Enter OTP");
                    return "";

                } else if (getMatchedOTP().equals(getMobile_otp())) {
                    validUser = UserDAO.validatePassword(user_id, password, hiddenRandomNo);
                } else {
                    JSFUtils.showMessage("OTP is not Matched Please Try Again!");
                    return "";
                }

            } else {
                validUser = UserDAO.validatePassword(user_id, password, hiddenRandomNo);
            }

            if (validUser) {
                if (logfile == 0) {
                    System.out.println("The log file -" + logfile);
                    logfile++;
                    Properties properties = new Properties();
                    InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("/resources/log4j.properties");
                    properties.load(inputStream);
                    PropertyConfigurator.configure(properties);
                    LOGGER.info("=====Log4j=====Properties file loaded Successfully=====");
                }

                HttpSession session = null;
                if ((user_id == null || user_id.isEmpty() || password == null || password.isEmpty()) && getOpvalue().equals("P")) {
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return "";
                }
                List<UserInfo> logList = UserDAO.login(user_id);
                UserAuthorityDobj authorityDobj = UserDAO.userAuthority(user_id);
                Integer size = logList.size();

                //For checking office timing 
                if (size != 0 && !TableConstants.OFFICE_TIMING_EXEMPTION_CATG.contains("," + logList.get(0).getUser_catg() + ",")
                        && !UserDAO.getOfficetime(logList.get(0).getState_cd(), logList.get(0).getOff_cd(), logList.get(0).getEmp_cd())) {
                    message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "ALERT! Office Time is Over. Please Contact to Office Administrator.", "");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return "";
                }

                if (size != 0) {
                    //for checking multiple tab of the same browser is Opened or Not start
                    session = Util.getSession(); // get Http Session
                    if (session != null) {
                        if (session.getAttribute("emp_cd") != null) {
                            user_id = "";
                            reset();
                            msg = "Multiple Login are not Allowed. User is Already Logged-in. Please Restart the browser for Forced Login.";
                            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, msg, msg);
                            FacesContext.getCurrentInstance().addMessage(null, message);
                            PrimeFaces.current().ajax().update("loginPanelOutput");
                            return "login.xhtml";

                        }
                    }
                    if (forcedLoginFlag) {
                        forcedLogin();
                    }
                    if (Util.getLoginUsers().containsKey(user_id)
                            || !ServerUtil.validateIpAddress(logList.get(0).getEmp_cd(), ipAddress)) {
                        HttpSession userSes = Util.getLoginUsers().get(user_id);
                        Object ipAddr = null;
                        String ip = "";
                        if (userSes != null) {
                            ipAddr = userSes.getAttribute("ipAddress");
                        }
                        if (ipAddr != null) {
                            ip = String.valueOf(ipAddr);
                        }
                        if (!CommonUtils.isNullOrBlank(ip)) {
                            msg = "You are already logged in from " + ip + ". You can invalidate\n"
                                    + " the session and create a forced login.";
                        } else {
                            msg = "You are already logged in. You can invalidate\n"
                                    + " the session and create a forced login.";
                        }

                        JSFUtils.setFacesMessage(msg, null, JSFUtils.ERROR);
                        forcedLoginFlag = true;
                        reset();
                        PrimeFaces.current().ajax().update("loginPanelOutput");
//                        Util.setForcedLogin(true);
                        return "login.xhtml"; //We can redirect to particular page which give proper information to the user                        
                    }

                    session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

                    session.setAttribute("emp_cd", logList.get(0).getEmp_cd());
                    session.setAttribute("emp_name", logList.get(0).getEmp_name().toUpperCase());
                    session.setAttribute("desig_name", logList.get(0).getDesig_name() != null ? logList.get(0).getDesig_name().toUpperCase() : "");
                    session.setAttribute("state_cd", logList.get(0).getState_cd());
                    session.setAttribute("off_cd", logList.get(0).getOff_cd());
                    session.setAttribute("state_name", logList.get(0).getState_name());
                    session.setAttribute("user_id", logList.get(0).getUser_id());
                    session.setAttribute("user_catg", logList.get(0).getUser_catg());
                    session.setAttribute("userAuthority", authorityDobj);
                    session.setAttribute("ipAddress", ipAddress);
                    session.setAttribute("serverIpAddress", serverIpAddress);
                    session.setAttribute("msgDobj", null);
                    Util.getLoginUsers().remove(user_id);
                    Util.getLoginUsers().put(user_id, session);
                    ServerUtil.updateLoginStatus(user_id, "A", ipAddress);

                    //for setting configuration details for particular state. 
                    TmConfigurationDobj tmConfigurationDobj = ServerUtil.getTmConfigurationParameters(logList.get(0).getState_cd());
                    session.setAttribute("tmConfig", tmConfigurationDobj);

                    doaudittrail = new DOAuditTrail(user_id, ipAddress, MsgProperties.getKeyValue("login.success.actiontype"), MsgProperties.getKeyValue("audit.trail.status.success"));
                    new ServerUtil().auditTrailDAO(doaudittrail);
//                    new UserDAO().loginUserInfo(doaudittrail, session_id);
                    if (!ServerUtil.checkUserMobileVerifiedOrNot(logList.get(0).getEmp_cd())
                            && (tmConfigurationDobj.isMobile_verify()
                            || tmConfigurationDobj.getUser_catg_mandate_otp().contains("," + logList.get(0).getUser_catg() + ","))) {
                        return "mobileVerify";
                    } else if ((TableConstants.USER_CATG_STATE_ADMIN.equalsIgnoreCase(logList.get(0).getUser_catg()) && ServerUtil.getForgetPasswordStatus(logList.get(0).getUser_id()).equals("Y")) // After use forget password functionalty first time open change pasword form
                            || (logList.get(0).getNewuser_change_password().equals("T"))) // After Creating New User first time Open Change Password Form
                    {
                        return "changedPassword";
                    } else {
                        return "home";
                    }
                }
            } else {
                doaudittrail = new DOAuditTrail(user_id, ipAddress, MsgProperties.getKeyValue("login.unsuccessful.actiontype"), MsgProperties.getKeyValue("audit.trail.status.failure"));
                new ServerUtil().auditTrailDAO(doaudittrail);
                mobile_otp = "";
                password = "";
                FacesContext.getCurrentInstance().addMessage(null, message);
                return "login.xhtml";
            }
            counter = 0;
        } catch (IOException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
        } catch (SQLException sqe) {
            LOGGER.error(sqe.toString() + "-" + sqe.getStackTrace()[0]);
        } catch (IllegalStateException e) {
            if (Util.getLoginUsers() != null && user_id != null) {
                Util.getLoginUsers().remove(user_id);
            }
            if (user_id != null) {
                ServerUtil.updateLoginStatus(user_id, "D", null);
            }
        } catch (VahanException e) {
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), null);
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
        }
        return "login.xhtml";
    }

    public void displayStatusReport(ComponentSystemEvent event) {
        String user_catg = Util.getUserCategory();
        if (user_catg != null) {
            if (user_catg.equals(TableConstants.USER_CATG_HSRP) || user_catg.equals(TableConstants.USER_CATG_SMARTCARD)
                    || user_catg.equalsIgnoreCase(TableConstants.USER_CATG_STATE_HSRP) || user_catg.equalsIgnoreCase(TableConstants.USER_CATG_STATE_SMARTCARD)) {
                dispalyStatusReport = false;
            } else {
                collSummaryDisplay = true;
            }
            if (user_catg.equals(TableConstants.USER_CATG_OFF_STAFF) || user_catg.equals(TableConstants.USER_CATG_DEALER)) {
                dealerRegnPendencyDisplay = true;
                if (user_catg.equals(TableConstants.USER_CATG_OFF_STAFF)) {
                    pmtOwnerByName = true;
                }
            }
            if (user_catg.equals(TableConstants.USER_CATG_FITNESS_CENTER_ADMIN) || user_catg.equals(TableConstants.USER_CATG_FITNESS_CENTER_USER)
                    || user_catg.equalsIgnoreCase(TableConstants.USER_CATG_STATE_HSRP) || user_catg.equalsIgnoreCase(TableConstants.USER_CATG_STATE_SMARTCARD)
                    || user_catg.equals(TableConstants.USER_CATG_HSRP) || user_catg.equals(TableConstants.USER_CATG_SMARTCARD)) {
                renderRegisteredVehReport = false;
            }
            if (user_catg.equals(TableConstants.USER_CATG_STATE_ADMIN) || user_catg.equals(TableConstants.USER_CATG_OFFICE_ADMIN)) {
                renderUserInformationPanel = true;
            }
            if (user_catg.equals(TableConstants.USER_CATG_DEALER)) {
                renderDealerHSRPReport = true;
            }
        }
    }

    public String logout() {
        HttpSession session = null;
        try {
            session = Util.getSession();
            if (session != null) {
                user_id = (String) session.getAttribute("user_id");
                if (user_id != null && user_id.trim().length() > 0) {
                    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                    ipAddress = request.getRemoteAddr();
                    ServerUtil.updateLoginStatus(user_id, "D", null);
                    setDoaudittrail(new DOAuditTrail(user_id, ipAddress, MsgProperties.getKeyValue("logout.success.actiontype"), MsgProperties.getKeyValue("audit.trail.status.success")));
                    new ServerUtil().auditTrailDAO(getDoaudittrail());
                    ServerUtil.insertIntoDscRegistrationHistory();
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
        return "login_home4";
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void reset() {
        setOpvalue("P");
        setMatchedOTP(null);
        setMobile_otp(null);
        setMobileno(null);
        user_id = null;
        setPassword(null);
        setUser_id(null);
        renderLoginButton = false;
        renderLoginPanel = false;
        renderValidateUserIdPanel = true;
        counter = 0;
        renderOTPPanel = false;
        renderBackButton = false;
        renderSubmitButton = false;
        forgetpassword = false;

    }

    public void forgetPassword(String user_catg) {
        if (user_catg != null) {
            if (user_catg.equals(TableConstants.USER_CATG_STATE_ADMIN) || (user_catg.equals(TableConstants.USER_CATG_OFFICE_ADMIN))
                    || (user_catg.equals(TableConstants.USER_CATG_DEALER_ADMIN))) {
                forgetpassword = true;
            } else {
                forgetpassword = false;
            }
        }
    }

    public String forgetPassForm() {
        String path = "";
        if (user_id != null) {
            path = "/ui/login/forgetPassword.xhtml?faces-redirect=true";
        }
        reset();
        return path;

    }

    public void validatePasswordAndSendOtp() throws VahanException {
        boolean validUser = false;
        FacesMessage message = null;
        String msg = "Invalid Login. Please Try Again!!!";
        message = new FacesMessage(FacesMessage.SEVERITY_WARN, msg, msg);
        try {
            validUser = UserDAO.validatePassword(user_id, password, hiddenRandomNo);
            if (validUser) {
                String[] arr = UserDAO.generateLoginType(user_id, false).split("`");
                PasswordOtpPanel(arr);
            } else {
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
        } catch (VahanException e) {
            FacesContext.getCurrentInstance().addMessage(TableConstants.SomthingWentWrong, null);
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
        }
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
     * @return the hiddenRandomNo
     */
    public String getHiddenRandomNo() {
        return hiddenRandomNo;
    }

    /**
     * @param hiddenRandomNo the hiddenRandomNo to set
     */
    public void setHiddenRandomNo(String hiddenRandomNo) {
        this.hiddenRandomNo = hiddenRandomNo;
    }

    /**
     * @return the doaudittrail
     */
    public DOAuditTrail getDoaudittrail() {
        return doaudittrail;
    }

    /**
     * @param doaudittrail the doaudittrail to set
     */
    public void setDoaudittrail(DOAuditTrail doaudittrail) {
        this.doaudittrail = doaudittrail;
    }

    /**
     * @return the dispalyStatusReport
     */
    public boolean isDispalyStatusReport() {
        return dispalyStatusReport;
    }

    /**
     * @param dispalyStatusReport the dispalyStatusReport to set
     */
    public void setDispalyStatusReport(boolean dispalyStatusReport) {
        this.dispalyStatusReport = dispalyStatusReport;
    }

    public void forcedLogin() {
        HttpSession session = null;
        try {
            if (user_id != null && Util.getLoginUsers() != null) {
                session = Util.getLoginUsers().get(user_id);
            }
            if (user_id != null && ipAddress != null) {
                ServerUtil.updateLoginStatus(user_id, "D", ipAddress);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
        } finally {
            if (user_id != null && session != null && session.getAttribute("state_cd") != null) {
                Util.getLoginUsers().remove(user_id);
                Util.removeAllSessionAttribute(session);
            }
        }
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        try {
            if (user_id != null && Util.getLoginUsers() != null) {
                ServerUtil.updateLoginStatus(user_id, "D", null);
                Util.getLoginUsers().remove(user_id);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
        }
    }

    /**
     * @return the forcedLoginFlag
     */
    public boolean isForcedLoginFlag() {
        return forcedLoginFlag;
    }

    /**
     * @param forcedLoginFlag the forcedLoginFlag to set
     */
    public void setForcedLoginFlag(boolean forcedLoginFlag) {
        this.forcedLoginFlag = forcedLoginFlag;
    }

    /**
     * @return the ipAddress
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * @param ipAddress the ipAddress to set
     */
    public void setIpAddress(String ipAdress) {
        this.ipAddress = ipAdress;
    }

    public boolean isPmtOwnerByName() {
        return pmtOwnerByName;
    }

    public void setPmtOwnerByName(boolean pmtOwnerByName) {
        this.pmtOwnerByName = pmtOwnerByName;
    }

    /**
     * @return the collSummaryDisplay
     */
    public boolean isCollSummaryDisplay() {
        return collSummaryDisplay;
    }

    /**
     * @param collSummaryDisplay the collSummaryDisplay to set
     */
    public void setCollSummaryDisplay(boolean collSummaryDisplay) {
        this.collSummaryDisplay = collSummaryDisplay;
    }

    /**
     * @return the serverIpAddress
     */
    public String getServerIpAddress() {
        return serverIpAddress;
    }

    /**
     * @param serverIpAddress the serverIpAddress to set
     */
    public void setServerIpAddress(String serverIpAddress) {
        this.serverIpAddress = serverIpAddress;
    }

    /**
     * @return the barChartModel7
     */
    public BarChartModel getBarChartModel7() {
        return barChartModel7;
    }

    /**
     * @param barChartModel7 the barChartModel7 to set
     */
    public void setBarChartModel7(BarChartModel barChartModel7) {
        this.barChartModel7 = barChartModel7;
    }

    /**
     * @return the totalRtoRecords
     */
    public Map<String, String> getTotalRtoRecords() {
        return totalRtoRecords;
    }

    /**
     * @param totalRtoRecords the totalRtoRecords to set
     */
    public void setTotalRtoRecords(Map<String, String> totalRtoRecords) {
        this.totalRtoRecords = totalRtoRecords;
    }

    /**
     * @return the runningRtoRecords
     */
    public Map<String, String> getRunningRtoRecords() {
        return runningRtoRecords;
    }

    /**
     * @param runningRtoRecords the runningRtoRecords to set
     */
    public void setRunningRtoRecords(Map<String, String> runningRtoRecords) {
        this.runningRtoRecords = runningRtoRecords;
    }

    /**
     * @return the nonRtoRecords
     */
    public Map<String, String> getNonRtoRecords() {
        return nonRtoRecords;
    }

    /**
     * @param nonRtoRecords the nonRtoRecords to set
     */
    public void setNonRtoRecords(Map<String, String> nonRtoRecords) {
        this.nonRtoRecords = nonRtoRecords;
    }

    /**
     * @return the barModel
     */
    public BarChartModel getBarModel() {
        return barModel;
    }

    /**
     * @param barModel the barModel to set
     */
    public void setBarModel(BarChartModel barModel) {
        this.barModel = barModel;
    }

    /**
     * @return the mobile_otp
     */
    public String getMobile_otp() {
        return mobile_otp;
    }

    /**
     * @param mobile_otp the mobile_otp to set
     */
    public void setMobile_otp(String mobile_otp) {
        this.mobile_otp = mobile_otp;
    }

    /**
     * @return the opvalue
     */
    public String getOpvalue() {
        return opvalue;
    }

    /**
     * @param opvalue the opvalue to set
     */
    public void setOpvalue(String opvalue) {
        this.opvalue = opvalue;
    }

    /**
     * @return the matchedOTP
     */
    public String getMatchedOTP() {
        return matchedOTP;
    }

    /**
     * @param matchedOTP the matchedOTP to set
     */
    public void setMatchedOTP(String matchedOTP) {
        this.matchedOTP = matchedOTP;
    }

    /**
     * @return the mobileno
     */
    public String getMobileno() {
        return mobileno;
    }

    /**
     * @param mobileno the mobileno to set
     */
    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    /**
     * @return the otpNote
     */
    public String getOtpNote() {
        return otpNote;
    }

    /**
     * @param otpNote the otpNote to set
     */
    public void setOtpNote(String otpNote) {
        this.otpNote = otpNote;
    }

    /**
     * @return the renderLoginButton
     */
    public boolean isRenderLoginButton() {
        return renderLoginButton;
    }

    /**
     * @param renderLoginButton the renderLoginButton to set
     */
    public void setRenderLoginButton(boolean renderLoginButton) {
        this.renderLoginButton = renderLoginButton;
    }

    /**
     * @return the renderOTPFields
     */
    public boolean isRenderOTPFields() {
        return renderOTPFields;
    }

    /**
     * @param renderOTPFields the renderOTPFields to set
     */
    public void setRenderOTPFields(boolean renderOTPFields) {
        this.renderOTPFields = renderOTPFields;
    }

    /**
     * @return the renderLoginPanel
     */
    public boolean isRenderLoginPanel() {
        return renderLoginPanel;
    }

    /**
     * @param renderLoginPanel the renderLoginPanel to set
     */
    public void setRenderLoginPanel(boolean renderLoginPanel) {
        this.renderLoginPanel = renderLoginPanel;
    }

    /**
     * @return the isOTPLoggedUser
     */
    public boolean isIsOTPLoggedUser() {
        return isOTPLoggedUser;
    }

    /**
     * @param isOTPLoggedUser the isOTPLoggedUser to set
     */
    public void setIsOTPLoggedUser(boolean isOTPLoggedUser) {
        this.isOTPLoggedUser = isOTPLoggedUser;
    }

    /**
     * @return the renderValidateUserIdPanel
     */
    public boolean isRenderValidateUserIdPanel() {
        return renderValidateUserIdPanel;
    }

    /**
     * @param renderValidateUserIdPanel the renderValidateUserIdPanel to set
     */
    public void setRenderValidateUserIdPanel(boolean renderValidateUserIdPanel) {
        this.renderValidateUserIdPanel = renderValidateUserIdPanel;
    }

    /**
     * @return the renderRegisteredVehReport
     */
    public boolean isRenderRegisteredVehReport() {
        return renderRegisteredVehReport;
    }

    /**
     * @param renderRegisteredVehReport the renderRegisteredVehReport to set
     */
    public void setRenderRegisteredVehReport(boolean renderRegisteredVehReport) {
        this.renderRegisteredVehReport = renderRegisteredVehReport;
    }

    /**
     * @return the totalV4_offices
     */
    public String getTotalV4_offices() {
        return totalV4_offices;
    }

    /**
     * @param totalV4_offices the totalV4_offices to set
     */
    public void setTotalV4_offices(String totalV4_offices) {
        this.totalV4_offices = totalV4_offices;
    }

    /**
     * @return the forgetpassword
     */
    public boolean isForgetpassword() {
        return forgetpassword;
    }

    /**
     * @param forgetpassword the forgetpassword to set
     */
    public void setForgetpassword(boolean forgetpassword) {
        this.forgetpassword = forgetpassword;
    }

    /**
     * @return the renderUserInformationPanel
     */
    public boolean isRenderUserInformationPanel() {
        return renderUserInformationPanel;
    }

    /**
     * @param renderUserInformationPanel the renderUserInformationPanel to set
     */
    public void setRenderUserInformationPanel(boolean renderUserInformationPanel) {
        this.renderUserInformationPanel = renderUserInformationPanel;
    }

    /**
     * @return the renderOTPPanel
     */
    public boolean isRenderOTPPanel() {
        return renderOTPPanel;
    }

    /**
     * @param renderOTPPanel the renderOTPPanel to set
     */
    public void setRenderOTPPanel(boolean renderOTPPanel) {
        this.renderOTPPanel = renderOTPPanel;
    }

    /**
     * @return the renderSubmitButton
     */
    public boolean isRenderSubmitButton() {
        return renderSubmitButton;
    }

    /**
     * @param renderSubmitButton the renderSubmitButton to set
     */
    public void setRenderSubmitButton(boolean renderSubmitButton) {
        this.renderSubmitButton = renderSubmitButton;
    }

    /**
     * @return the renderBackButton
     */
    public boolean isRenderBackButton() {
        return renderBackButton;
    }

    /**
     * @param renderBackButton the renderBackButton to set
     */
    public void setRenderBackButton(boolean renderBackButton) {
        this.renderBackButton = renderBackButton;
    }

    /**
     * @return the renderOTPBasedFields
     */
    public boolean isRenderOTPBasedFields() {
        return renderOTPBasedFields;
    }

    /**
     * @param renderOTPBasedFields the renderOTPBasedFields to set
     */
    public void setRenderOTPBasedFields(boolean renderOTPBasedFields) {
        this.renderOTPBasedFields = renderOTPBasedFields;
    }

    /**
     * @return the dealerRegnPendencyDisplay
     */
    public boolean isDealerRegnPendencyDisplay() {
        return dealerRegnPendencyDisplay;
    }

    /**
     * @param dealerRegnPendencyDisplay the dealerRegnPendencyDisplay to set
     */
    public void setDealerRegnPendencyDisplay(boolean dealerRegnPendencyDisplay) {
        this.dealerRegnPendencyDisplay = dealerRegnPendencyDisplay;
    }

    /**
     * @return the renderDealerHSRPReport
     */
    public boolean isRenderDealerHSRPReport() {
        return renderDealerHSRPReport;
    }

    /**
     * @param renderDealerHSRPReport the renderDealerHSRPReport to set
     */
    public void setRenderDealerHSRPReport(boolean renderDealerHSRPReport) {
        this.renderDealerHSRPReport = renderDealerHSRPReport;
    }

    public String getStatesV4() {
        return statesV4;
    }

    public void setStatesV4(String statesV4) {
        this.statesV4 = statesV4;
    }

    public String getOfficsV4() {
        return officsV4;
    }

    public void setOfficsV4(String officsV4) {
        this.officsV4 = officsV4;
    }

    public final void setSession(HttpServletRequest request, boolean forcedLoginFlag) {
        boolean validUser = true;
        ipAddress = request.getRemoteAddr();
        session_id = request.getSession().getId();

        try {
            if (validUser) {
                if (logfile == 0) {
                    logfile++;
                    Properties properties = new Properties();
                    InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("/resources/log4j.properties");
                    properties.load(inputStream);
                    PropertyConfigurator.configure(properties);
                    LOGGER.info("=====Log4j=====Properties file loaded Successfully=====");
                }

                HttpSession session = null;
//                this.user_id = request.getParameter("user_id");
                this.user_id = (String) request.getAttribute("user_id");
                List<UserInfo> logList = UserDAO.login(user_id);
                UserAuthorityDobj authorityDobj = UserDAO.userAuthority(user_id);
                Integer size = logList.size();

                if (size != 0) {
                    if (forcedLoginFlag) {
                        try {
                            if (user_id != null && Util.getLoginUsers() != null) {
                                session = Util.getLoginUsers().get(user_id);
                            }
                        } catch (Exception ex) {
                            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
                        } finally {
                            if (user_id != null && session != null) {
                                Util.getLoginUsers().remove(user_id);
                                try {
                                    Util.removeAllSessionAttribute(session);
                                } catch (Exception e) {// Happens if the session has already been invalidated from V5
                                    LOGGER.error(e.toString());
                                }
                            }
                        }
                    }
                    session = request.getSession(true);

                    session.setAttribute("emp_cd", logList.get(0).getEmp_cd());
                    session.setAttribute("emp_name", logList.get(0).getEmp_name().toUpperCase());
                    session.setAttribute("desig_name", logList.get(0).getDesig_name() != null ? logList.get(0).getDesig_name().toUpperCase() : "");
                    session.setAttribute("state_cd", logList.get(0).getState_cd());
                    session.setAttribute("off_cd", logList.get(0).getOff_cd());
                    session.setAttribute("state_name", logList.get(0).getState_name());
                    session.setAttribute("user_id", logList.get(0).getUser_id());
                    session.setAttribute("user_catg", logList.get(0).getUser_catg());
                    session.setAttribute("userAuthority", authorityDobj);
                    session.setAttribute("ipAddress", ipAddress);
                    session.setAttribute("serverIpAddress", serverIpAddress);
                    session.setAttribute("msgDobj", null);
                    Util.getLoginUsers().remove(user_id);
                    Util.getLoginUsers().put(user_id, session);

                    //for setting configuration details for particular state.
                    TmConfigurationDobj tmConfigurationDobj = ServerUtil.getTmConfigurationParameters(logList.get(0).getState_cd());
                    session.setAttribute("tmConfig", tmConfigurationDobj);
                }
            }
            counter = 0;
        } catch (IOException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
        } catch (VahanException e) {
//            message = new FacesMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), null);
//            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
        }
    }
}
