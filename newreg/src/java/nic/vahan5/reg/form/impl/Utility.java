/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.form.impl;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.user_mgmt.dobj.UserAuthorityDobj;
import nic.vahan.form.dobj.TmConfigurationDispatchDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.TmConfigurationNonUseDobj;
import nic.vahan.form.dobj.TmConfigurationSwappingDobj;
import nic.vahan.form.impl.SeatAllotedDetails;
import static nic.vahan.form.impl.Util.getSession;
import static nic.vahan.form.impl.Util.getUserSeatOffCode;
import static nic.vahan.form.impl.Util.getUserStateCode;
import nic.vahan.server.ServerUtil;
import nic.vahan5.reg.server.ServerUtility;

/**
 *
 * @author Kartikey Singh
 */
public class Utility {

    private static Map<String, HttpSession> loginUsers = new HashMap<>();

    public static HttpSession getSession() {
        return (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
    }

    public static HttpServletRequest getRequest() {
        return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    }

    public static String getUserName() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if (session != null && session.getAttribute("emp_name") != null) {
            return (String) session.getAttribute("emp_name");
        } else {
            return null;
        }
    }

    public static String getEmpCode() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if (session != null && session.getAttribute("emp_cd") != null) {
            return session.getAttribute("emp_cd").toString();
        } else {
            return null;
        }
    }

    public static long getEmpCodeLong() throws VahanException {

        long empCd = 0;
        try {
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            if (session != null && session.getAttribute("emp_cd") == null) {
                empCd = Long.parseLong(TableConstants.ONLINE_PAYMENT);
            } else if (session != null && session.getAttribute("emp_cd") != null
                    && !session.getAttribute("emp_cd").toString().isEmpty()) {
                empCd = Long.parseLong(session.getAttribute("emp_cd").toString());
            } else {
                throw new VahanException("Error in Getting Emp Code");
            }

        } catch (VahanException e) {
            throw e;
        }
        return empCd;
    }

    public static String getUserId() {
        HttpSession session = getSession();
        if (session != null && session.getAttribute("user_id") != null) {
            return (String) session.getAttribute("user_id");
        } else {
            return null;
        }
    }

    public static String getUserStateCode() {
        HttpSession session = getSession();
        if (session != null && session.getAttribute("state_cd") != null) {
            return (String) session.getAttribute("state_cd");
        } else {
            return null;
        }
    }

    public static Integer getUserOffCode() {
        return getUserSeatOffCode();
    }

    /**
     * this will give you off_cd of selected seat.
     *
     * @getUserSeatOffCode
     * @param none
     */
    public static Integer getUserSeatOffCode() {
        HttpSession session = getSession();
        if (session != null && session.getAttribute("selected_off_cd") != null) {
            return Integer.parseInt(session.getAttribute("selected_off_cd").toString());
        } else {
            return null;
        }
    }

    public static SeatAllotedDetails getSelectedSeat() {
        HttpSession session = getSession();
        if (session != null && session.getAttribute("SelectedSeat") != null) {
            return (SeatAllotedDetails) session.getAttribute("SelectedSeat");
        } else {
            return null;
        }
    }

    public static void dateFormat() {
//    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                  String date= sdf.format( "2014-12-12 55:55:55" );
//                  System.out.println( "date "+date );
    }

    public static UserAuthorityDobj getUserAuthority() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if (session != null && session.getAttribute("userAuthority") != null) {
            return (UserAuthorityDobj) session.getAttribute("userAuthority");
        } else {
            return null;
        }
    }

    public static String getClientIpAdress() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        //is client behind something?
        String ipAddress = req.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = req.getRemoteAddr();
        }
        return ipAddress;
    }

    public static TmConfigurationDobj getTmConfiguration() throws VahanException {
        HttpSession session = getSession();
        TmConfigurationDobj dobj = null;
        if (session != null && session.getAttribute("tmConfig") != null) {
            //testing required that is what will happen whern session.getAttribute("tmConfig") give null
            // As of 19/5/20, both the objects seem to be the same (checked both the objects line by line)
            dobj = (TmConfigurationDobj) session.getAttribute("tmConfig");
        }

        if (dobj == null) {
            dobj = ServerUtil.getTmConfigurationParameters(getUserStateCode());
        }

        return dobj;
    }

    /**
     * @author Kartikey Singh made to call public static TmConfigurationDobj
     * getTmConfiguration() throws VahanException from Rest
     */
    public static TmConfigurationDobj getTmConfiguration(TmConfigurationDobj dobj, String userStateCode) throws VahanException {
        if (dobj == null) {
            dobj = ServerUtility.getTmConfigurationParameters(userStateCode);
        }

        return dobj;
    }

    public static String getUserCategory() {
        HttpSession session = getSession();
        if (session != null && session.getAttribute("user_catg") != null) {
            return (String) session.getAttribute("user_catg");
        } else {
            return null;
        }
    }

    public static Integer getUserLoginOffCode() {
        HttpSession session = getSession();
        if (session != null && session.getAttribute("off_cd") != null) {
            return (Integer) session.getAttribute("off_cd");
        } else {
            return null;
        }
    }

    public static void removeAllSessionAttribute(HttpSession session) {

        try {
            if (session != null) {
//            Enumeration<String> allSessionAttributes = session.getAttributeNames();
//            while (allSessionAttributes.hasMoreElements()) {
//                String string = allSessionAttributes.nextElement();
//                System.out.println("---"+string);
//            }
//                session.removeAttribute("emp_cd");
//                session.removeAttribute("state_cd");
//                session.removeAttribute("off_cd");
                session.removeAttribute("emp_name");
                session.removeAttribute("desig_name");
                session.removeAttribute("state_name");
                session.removeAttribute("user_catg");
                session.removeAttribute("userAuthority");
                session.removeAttribute("ipAddress");
                session.removeAttribute("msgDobj");
                session.removeAttribute("tmConfig");
                session.removeAttribute("SelectedSeat");
                session.removeAttribute("selected_cntr_id");
                session.removeAttribute("selected_role_cd");
                session.removeAttribute("selected_off_cd");
                session.removeAttribute("seat_map");
            }
        } catch (Exception ex) {
        } finally {
            if (session != null) {
                session.invalidate();
            }
        }
    }

    /**
     * @return the loginUsers
     */
    public static Map<String, HttpSession> getLoginUsers() {
        return loginUsers;
    }

    /**
     * @param aLoginUsers the loginUsers to set
     */
    public static void setLoginUsers(Map<String, HttpSession> aLoginUsers) {
        loginUsers = aLoginUsers;
    }
//    /**
//     * @return the forcedLogin
//     */
//    public static boolean isForcedLogin() {
//        return forcedLogin;
//    }
//
//    /**
//     * @param aForcedLogin the forcedLogin to set
//     */
//    public static void setForcedLogin(boolean aForcedLogin) {
//        forcedLogin = aForcedLogin;
//    }

//    /**
//     * @return the ipAddress
//     */
//    public static String getIpAddress() {
//        return ipAddress;
//    }
//
//    /**
//     * @param aIpAddress the ipAddress to set
//     */
//    public static void setIpAddress(String aIpAddress) {
//        ipAddress = aIpAddress;
//    }
    public static TmConfigurationDispatchDobj getTmConfigurationDispatch() throws VahanException {
        TmConfigurationDispatchDobj dobj = null;
        if (dobj == null) {
            dobj = ServerUtil.getTmConfigurationDispatchParameters(getUserStateCode());
        }

        return dobj;
    }

    public static TmConfigurationSwappingDobj getTmConfigurationSwapping() throws VahanException {
        TmConfigurationSwappingDobj dobj = null;
        if (dobj == null) {
            dobj = ServerUtil.getTmConfigurationSwappingParameters(getUserStateCode());
        }

        return dobj;
    }

    public static TmConfigurationNonUseDobj getTmConfigurationNonUse() throws VahanException {
        TmConfigurationNonUseDobj dobj = null;
        if (dobj == null) {
            dobj = ServerUtil.getTmConfigurationNonUseParameters(getUserStateCode());
        }

        return dobj;
    }

    public static String getLocaleMsg(String labelName) {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        if (locale != null) {
            ResourceBundle labels = ResourceBundle.getBundle("nic.vahan.language.resources.messages", locale);
            if (labels != null) {
                return labels.getString(labelName);
            }
        }
        return labelName;
    }

    public static String getLocaleSomthingMsg() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        if (locale != null) {
            ResourceBundle labels = ResourceBundle.getBundle("nic.vahan.language.resources.messages", locale);
            if (labels != null) {
                return labels.getString("somthingWentWrong");
            }
        }
        return "somthingWentWrong";
    }

    public static String getLocaleSessionMsg() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        if (locale != null) {
            ResourceBundle labels = ResourceBundle.getBundle("nic.vahan.language.resources.messages", locale);
            if (labels != null) {
                return labels.getString("sessionExpired");
            }
        }
        return "sessionExpired";
    }

    public static String getEncriptString(int DocType) throws VahanException {
        SecureRandom rand = new SecureRandom();
        String alphaNumeric = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghigklmnopqrstuvwxyz";
        final Calendar cal = Calendar.getInstance();
        int length = alphaNumeric.length();
        String returnVal = "" + DocType + alphaNumeric.charAt(rand.nextInt(length));
        returnVal = returnVal + (cal.get(Calendar.YEAR) % 100);
        returnVal = returnVal + alphaNumeric.charAt(rand.nextInt(length));
        returnVal = returnVal + alphaNumeric.charAt(cal.get(Calendar.MONTH) + 1);
        returnVal = returnVal + alphaNumeric.charAt(rand.nextInt(length));
        returnVal = returnVal + alphaNumeric.charAt(cal.get(Calendar.DAY_OF_MONTH));
        returnVal = returnVal + alphaNumeric.charAt(rand.nextInt(length));
        returnVal = returnVal + alphaNumeric.charAt(cal.get(Calendar.HOUR_OF_DAY));
        returnVal = returnVal + alphaNumeric.charAt(rand.nextInt(length));
        returnVal = returnVal + alphaNumeric.charAt(cal.get(Calendar.MINUTE));
        returnVal = returnVal + alphaNumeric.charAt(rand.nextInt(length));
        returnVal = returnVal + alphaNumeric.charAt(cal.get(Calendar.SECOND));
        returnVal = returnVal + alphaNumeric.charAt(rand.nextInt(length));
        return returnVal;
    }
}
