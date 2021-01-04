/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filters;

import dao.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import javax.faces.application.ResourceHandler;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.impl.LoginBean;
import nic.vahan.form.impl.SeatAllotedDetails;
import nic.vahan5.cas.authentication.CASAuthentication;
import nic.vahan5.cas.authentication.CASTicketDetails;
import nic.vahan5.login.model.CustomLoginBeanModel;
import nic.vahan5.reg.rest.config.SpringContext;
import nic.vahan5.reg.rto.common.CryptographyAESNewReg;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@WebFilter(filterName = "AuthFilter5", urlPatterns = {"/vahan/*", "*.xhtml"})
public class AuthFilter5 implements Filter {

    private static final Logger LOGGER = Logger.getLogger(AuthFilter5.class);
    private String timeoutPage = "/vahan/ui/session/sessionTimeout.xhtml";
    private String forcedTimeoutPage = "/vahan/ui/session/forcedSessionTimeout.xhtml";
    private WebClient.Builder webClientBuilder = SpringContext.getBean(WebClient.Builder.class);

    public AuthFilter5() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // check whether session variable is set
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession ses = req.getSession(false);
        String reqURI = req.getRequestURI();
//        LOGGER.debug("vahan5 req.getRequestURI()=" + reqURI);
        String casTicket = null;
        Exception exception = null;
        boolean onlinePayment = false;
        CryptographyAESNewReg aes = new CryptographyAESNewReg();
        try {
//            String baseUrl = req.getRequestURL().substring(0, req.getRequestURL().length() - reqURI.length()) + "/vahan5";                       
            InetAddress localhost = InetAddress.getLocalHost();
//            LOGGER.info("System IP Address : " + (localhost.getHostAddress()).trim());
            String baseUrl = "";
            if (req.getProtocol().equals("HTTPS/1.1")) {
                baseUrl = "https://";
            } else {
                baseUrl = "http://";
            }
//            baseUrl = baseUrl + req.getLocalName() + ":" + req.getLocalPort() + "/vahan5";
            baseUrl = baseUrl + (localhost.getHostAddress()).trim() + ":" + req.getLocalPort() + req.getContextPath();
//            baseUrl = baseUrl + "127.0.0.1:80/vahan5";
//            LOGGER.info("vahan5 baseUrl=" + baseUrl);
            String response1 = "";
            HashMap returnParams = null;
            response1 = req.getParameter("encData");
            if (response1 != null) {
                returnParams = aes.getReturnParametersNew(response1, req.getSession(true));
            }

            // Called after the final redirect to newregistration
            if ((reqURI.indexOf("/vahan/workbench.xhtml") >= 0 || reqURI.indexOf("fee/form_new_registration_fee.xhtml") >= 0
                    || reqURI.indexOf("dealer/form_add_to_payment_gateway.xhtml") >= 0 || reqURI.indexOf("ui/form_documents_upload.xhtml") >= 0)
                    && req.getParameter("encData") != null) {
                if (req.getHeader("Referer") != null && ses != null && ses.getAttribute("ses.expire") != null) {
                    res.sendRedirect(reqURI);
                    return;
                }
                // fetch rand from db to verify
                CustomLoginBeanModel customLoginBeanModel = new CustomLoginBeanModel((String) returnParams.get("user_id"), "VAHAN4");
                String restUrl = baseUrl + "/login/get";
                try {
                    customLoginBeanModel = webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .build()
                            .post()
                            .uri(restUrl)
                            .body(Mono.just(customLoginBeanModel), CustomLoginBeanModel.class)
                            .retrieve()
                            .bodyToMono(CustomLoginBeanModel.class)
                            .block();
                } catch (WebClientResponseException e) {
                    LOGGER.error(e.getMessage());
                    throw new VahanException("Error in connecting to New Reg Application");
                }

                if (returnParams.get("rand") != null) {
                    if (((String) returnParams.get("rand")).equals(customLoginBeanModel.getRandNo() + "")) {

                        // delete rand from db so it cant be used again
                        restUrl = baseUrl + "/login/delete";
                        try {
                            int recordsDeleted = webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .build()
                                    .post()
                                    .uri(restUrl)
                                    .body(Mono.just(customLoginBeanModel), CustomLoginBeanModel.class)
                                    .retrieve()
                                    .bodyToMono(Integer.class)
                                    .block();
                            if (recordsDeleted != 1) {
                                LOGGER.info("some issue in redircting. recordsDeleted = " + recordsDeleted);
                            }
                        } catch (WebClientResponseException e) {
                            LOGGER.error(e.getMessage());
                            throw new VahanException("Error in connecting to New Reg Application");
                        }
                        req.setAttribute("user_id", (String) returnParams.get("user_id"));
                        if (returnParams.get("forcedLoginFlag") != null) {
                            new LoginBean(req, Boolean.parseBoolean((String) returnParams.get("forcedLoginFlag")));
                        } else {
                            new LoginBean(req, false);
                        }

                        ses = req.getSession(false);

                        Map mapTemp = new HashMap();
                        //if condition
                        if (returnParams.get("appl_no") == null) {
                            mapTemp.put("pur_code", returnParams.get("pur_code") != null ? (String) returnParams.get("pur_code") : "");
                            mapTemp.put("appl_no", "");
                            mapTemp.put("actionCode", (String) returnParams.get("action_cd"));
                            mapTemp.put("appl_dt", "");
                            mapTemp.put("office_remark", "");
                            mapTemp.put("public_remark", "");
                            mapTemp.put("Purpose", "");
                            mapTemp.put("regn_no", "");
                            mapTemp.put("cur_status", "");
                        } else {
                            // when coming from grid - Start
                            mapTemp.put("pur_code", returnParams.get("pur_code") != null ? (String) returnParams.get("pur_code") : "");
                            mapTemp.put("appl_no", (String) returnParams.get("appl_no"));
                            mapTemp.put("actionCode", (String) returnParams.get("action_cd"));
                            mapTemp.put("appl_dt", (String) returnParams.get("appl_dt"));
                            mapTemp.put("office_remark", (String) returnParams.get("office_remark"));
                            mapTemp.put("public_remark", (String) returnParams.get("public_remark"));
                            mapTemp.put("Purpose", (String) returnParams.get("Purpose"));
                            mapTemp.put("regn_no", (String) returnParams.get("regn_no"));
                            mapTemp.put("cur_status", (String) returnParams.get("cur_status"));
                        }

                        // set variables from query parameter to mapTemp
                        mapTemp.put("request_source", "seat");

                        // when coming from grid - END
                        ses.setAttribute("seat_map", mapTemp);
                        ses.setAttribute("cas_flag", (String) returnParams.get("cas_flag"));
                        ses.setAttribute("casTicket", customLoginBeanModel.getCasTicket());
                        ses.setAttribute("ses.expire", "VAHAN4");

                        SeatAllotedDetails selectedSeat = new SeatAllotedDetails();

                        // selectedSeat.setPur_cd(1);
                        //selectedSeat.setFile_movement_slno(1);
                        //selectedSeat.setAction_descr("NEW-REGN-APPL");
                        //selectedSeat.setColour("#FFCD46");
                        //selectedSeat.setApplStatusDescr("Inwarded");
                        selectedSeat.setAction_cd(returnParams.get("action_cd") != null ? Integer.parseInt((String) returnParams.get("action_cd")) : 0);
                        selectedSeat.setOff_cd(returnParams.get("selected_off_cd") != null ? Integer.parseInt((String) returnParams.get("selected_off_cd")) : 0);
                        selectedSeat.setCntr_id((String) returnParams.get("selected_cntr_id"));

                        selectedSeat.setAppl_no((String) returnParams.get("appl_no"));
                        selectedSeat.setAppl_dt((String) returnParams.get("appl_dt"));
                        selectedSeat.setRegn_no((String) returnParams.get("regn_no"));
                        selectedSeat.setOffice_remark((String) returnParams.get("office_remark"));
                        selectedSeat.setRemark_for_public((String) returnParams.get("public_remark"));
                        selectedSeat.setPurpose_descr((String) returnParams.get("Purpose"));
                        selectedSeat.setStatus((String) returnParams.get("cur_status"));

                        if (returnParams.get("appl_no") != null) {
                            selectedSeat.setPur_cd(Integer.parseInt((String) returnParams.get("pur_code")));
                            selectedSeat.setAction_cd(Integer.parseInt((String) returnParams.get("action_cd")));
                        }

                        ses.setAttribute("SelectedSeat", selectedSeat);

                        // for vahan5 feetax working as vahan4 grid
                        ses.setAttribute("selected_cntr_id", (String) returnParams.get("selected_cntr_id"));
                        ses.setAttribute("selected_off_cd", (String) returnParams.get("selected_off_cd"));
                    } else {
                        if (ses != null) {
                            ses.invalidate();
                            ses = null;
                        }
                    }
                }
            }
            if (reqURI.indexOf("/home.xhtml") >= 0 && req.getParameter("encData") != null) {

                // fetch rand from db to verify
                CustomLoginBeanModel customLoginBeanModel = new CustomLoginBeanModel((String) returnParams.get("user_id"), "VAHAN4");
                String restUrl = baseUrl + "/login/get";
                try {
                    customLoginBeanModel = webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .build()
                            .post()
                            .uri(restUrl)
                            .body(Mono.just(customLoginBeanModel), CustomLoginBeanModel.class)
                            .retrieve()
                            .bodyToMono(CustomLoginBeanModel.class)
                            .block();
                } catch (WebClientResponseException e) {
                    LOGGER.error(e.getMessage());
                    throw new VahanException("Error in connecting to New Reg Application");
                }

//                status = (String) returnParams.get("status");
                boolean casFlag = Boolean.parseBoolean((String) returnParams.get("cas_flag"));
                if (casFlag) {
                    casTicket = customLoginBeanModel.getCasTicket();
                    LOGGER.info(":::::::::::::::::::casTicket:::::::::::::::::::::::::::::::" + casTicket);
                    CASAuthentication authentication = new CASAuthentication();
                    CASTicketDetails ticketDetails = authentication.casServiceTicket(casTicket);
                    int statusCode = authentication.casServiceTicket(casTicket).getStatusCode();

                    if (!(statusCode == 200)) {

                        // delete rand from db so it cant be used again
                        restUrl = baseUrl + "/login/delete";
                        try {
                            int recordsDeleted = webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .build()
                                    .post()
                                    .uri(restUrl)
                                    .body(Mono.just(customLoginBeanModel), CustomLoginBeanModel.class)
                                    .retrieve()
                                    .bodyToMono(Integer.class)
                                    .block();
                            if (recordsDeleted != 1) {
                                //LOGGER.debug("some issue in redircting. recordsDeleted = " + recordsDeleted);
                            }
                        } catch (WebClientResponseException e) {
                            LOGGER.error(e.getMessage());
                            throw new VahanException("Error in connecting to New Reg Application");
                        }
                        res.sendRedirect("/vahan/vahan4/redirect/login.xhtml");  // Anonymous user. Redirect to login page/home page
                        //LOGGER.debug("cas auth fails vahan5 after redirecting to vahan login page");
//                    return;
                    }
                }

                if ((String) returnParams.get("rand") != null) {
                    if (((String) returnParams.get("rand")).equals(customLoginBeanModel.getRandNo() + "")) {
                        if (returnParams.get("wb") != null && ((String) returnParams.get("wb")).equals("1")) {
                            String initialURL = req.getContextPath() + "/vahan/workbench.xhtml?encData=";
                            String initialParams = "user_id=" + (String) returnParams.get("user_id")
                                    + "|rand=" + customLoginBeanModel.getRandNo()
                                    + "|selected_cntr_id=" + (String) returnParams.get("selected_cntr_id")
                                    + "|action_cd=" + (String) returnParams.get("action_cd")
                                    + "|selected_off_cd=" + (String) returnParams.get("selected_off_cd")
                                    + "|cas_flag=" + casFlag;
                            if ((String) returnParams.get("forcedLoginFlag") != null) {
                                initialParams += "|forcedLoginFlag=" + (String) returnParams.get("forcedLoginFlag");
                            }
                            if ((String) returnParams.get("appl_no") == null) {
                                if (returnParams.get("redirect_url") != null && ((String) returnParams.get("redirect_url")).indexOf("dealer/form_add_to_payment_gateway.xhtml") >= 0) {
                                    initialURL = req.getContextPath() + "/vahan" + (String) returnParams.get("redirect_url");
                                    if (initialURL.contains("?")) {
                                        initialURL += "&encData=";
                                    } else {
                                        initialURL += "?encData=";
                                    }
                                }
                                initialParams += "|pur_code=" + (String) returnParams.get("pur_code");
                                initialParams = aes.getEncriptedString(initialParams, req.getSession(true));
                                res.sendRedirect(initialURL + initialParams);

                            } else {
                                if (returnParams.get("redirect_url").equals("/ui/fee/form_new_registration_fee.xhtml?faces-redirect=true")
                                        || ((String) returnParams.get("redirect_url")).indexOf("/ui/form_documents_upload.xhtml") >= 0) {
                                    initialURL = req.getContextPath() + "/vahan" + (String) returnParams.get("redirect_url");
                                    if (initialURL.contains("?")) {
                                        initialURL += "&encData=";
                                    } else {
                                        initialURL += "?encData=";
                                    }

                                }
                                initialParams += "|pur_code=" + (String) returnParams.get("pur_code")
                                        + "|appl_no=" + (String) returnParams.get("appl_no")
                                        + "|Purpose=" + (String) returnParams.get("Purpose")
                                        + "|office_remark=" + (String) returnParams.get("office_remark")
                                        + "|public_remark=" + (String) returnParams.get("public_remark")
                                        + "|regn_no=" + (String) returnParams.get("regn_no")
                                        + "|appl_dt=" + (String) returnParams.get("appl_dt")
                                        + "|cur_status=" + (String) returnParams.get("cur_status");
                                initialParams = aes.getEncriptedString(initialParams, req.getSession(true));
                                res.sendRedirect(initialURL + initialParams);
                            }
                            return;
                        } else {
                            // delete rand from db so it cant be used again
                            restUrl = baseUrl + "/login/delete";
                            try {
                                int recordsDeleted = webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                        .build()
                                        .post()
                                        .uri(restUrl)
                                        .body(Mono.just(customLoginBeanModel), CustomLoginBeanModel.class)
                                        .retrieve()
                                        .bodyToMono(Integer.class)
                                        .block();
                                if (recordsDeleted != 1) {
                                    LOGGER.info("some issue in redircting. recordsDeleted = " + recordsDeleted);
                                }
                            } catch (WebClientResponseException e) {
                                LOGGER.error(e.getMessage());
                                throw new VahanException("Error in connecting to New Reg Application");
                            }
                        }
                    } else {
                        if (ses != null) {
                            ses.invalidate();
                            ses = null;
                        }
                    }
                }
            }
            // Executed when user requests the home.xhtml page after entering newregistration
            if (ses != null && reqURI.indexOf("/home.xhtml") >= 0) {
                if (ses.getAttribute("user_id") != null && ses.getAttribute("casTicket") != null
                        && !UserDAO.validateCASTicketForLoggedInUser((String) ses.getAttribute("user_id"), (String) ses.getAttribute("casTicket"))) {
//                if (ses.getAttribute("casTicket") != null && !new CASAuthentication().casTicketStatus((String) ses.getAttribute("casTicket"))) {
                    ses.invalidate();
                    ses = null;
                    res.sendRedirect("/vahan" + timeoutPage);
                    return;
                }
                //inserting rand to db
                CustomLoginBeanModel customLoginBeanModel = new CustomLoginBeanModel((String) ses.getAttribute("user_id"), "VAHAN5", (String) ses.getAttribute("casTicket"));
                String restUrl = baseUrl + "/login/insert";
                try {
                    customLoginBeanModel = webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .build()
                            .post()
                            .uri(restUrl)
                            .body(Mono.just(customLoginBeanModel), CustomLoginBeanModel.class)
                            .retrieve()
                            .bodyToMono(CustomLoginBeanModel.class)
                            .block();

                    ses.setAttribute("ses.expire", null);
                    String initialParams = "user_id=" + ses.getAttribute("user_id") + "|rand=" + customLoginBeanModel.getRandNo() + "|cas_flag=" + ses.getAttribute("cas_flag");
                    initialParams = aes.getEncriptedString(initialParams, ses);
                    res.sendRedirect("/vahan/vahan/home.xhtml?encData=" + initialParams);
                } catch (WebClientResponseException e) {
                    LOGGER.error(e.getMessage());
                    throw new VahanException("Error in connecting to New Reg Application");
                }
                return;
            }

            //disable the browser cache.
            if (!reqURI.startsWith(req.getContextPath() + "/vahan" + ResourceHandler.RESOURCE_IDENTIFIER)) { // Skip JSF resources (CSS/JS/Images/etc)
                res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
                res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
                res.setDateHeader("Expires", 0); // Proxies.
                res.setHeader("X-Frame-Options", "SAMEORIGIN");
                res.setHeader("X-Powered-By", "unset");
                res.setHeader("Server", "unset");
            }

            //  allow user to proccede if url is login.xhtml or user logged in or user is accessing any page in eapplication folder
            if ((ses != null && ses.getAttribute("emp_cd") != null && ses.getAttribute("state_cd") != null && ses.getAttribute("off_cd") != null)
                    || reqURI.indexOf("/login.xhtml") >= 0
                    || reqURI.indexOf("/ui/eapplication") >= 0 || reqURI.indexOf("/ui/session") >= 0 || reqURI.contains("javax.faces.resource") || reqURI.indexOf("/ui/userNotFound.xhtml") >= 0 || reqURI.indexOf("/ui/login/forgetPassword.xhtml") >= 0 || reqURI.indexOf("/ui/contactus.xhtml") >= 0) {
                onlinePayment = false;
                if (ses != null && ses.getAttribute("onlinePayment") != null) {
                    onlinePayment = true;
                }
                /*if (ses != null && ses.getAttribute("emp_cd") != null && !onlinePayment) {
                 if (!ServerUtil.validateIpAddress(Long.valueOf(ses.getAttribute("emp_cd").toString()), req.getRemoteAddr())) {
                 LOGGER.debug("MultiLogin:" + req.getRemoteAddr() + ",User:" + ses.getAttribute("emp_cd").toString());
                 ses.setAttribute("emp_cd", null);
                 res.sendRedirect(req.getContextPath() + "/vahan/ui/session/warning.xhtml");
                 return;
                 }
                 }*/
                if (reqURI.indexOf("/vahan4/redirect") >= 0 || reqURI.indexOf("/vahan/ui/login/login.xhtml") >= 0) {
                    res.sendRedirect("/vahan/vahan4/redirect/login.xhtml");  // Anonymous user. Redirect to login page/home page
//                    res.sendRedirect("http://127.0.0.1/vahan");
                    return;
                }
                boolean dealerPayment = false;
                if (ses != null && ses.getAttribute("DealerPayment") != null) {
                    dealerPayment = true;
                }
                if (!dealerPayment) {
                    // Uncommented for testing purpose
//                    if (req.getHeader("Referer") == null && ses != null && ses.getAttribute("emp_cd") != null && reqURI.indexOf("/warning.xhtml") < 0) {
                    if (req.getHeader("Referer") == null && ses != null && ses.getAttribute("emp_cd") != null && reqURI.indexOf("/warning.xhtml") < 0) {
                        ses.invalidate();
                        res.sendRedirect("/vahan/vahan/ui/session/warning.xhtml");
                        return;
                    } else if (req.getHeader("Referer") == null && ses != null && ses.getAttribute("emp_cd") != null && reqURI.indexOf("/warning.xhtml") >= 0) {
                        ses.invalidate();
//                        res.sendRedirect(req.getContextPath() + "/vahan/ui/session/warning.xhtml");
                        res.sendRedirect("/vahan/vahan/ui/session/warning.xhtml");
                        return;
                    }
                }
                if (reqURI.indexOf("/login.xhtml") >= 0
                        || reqURI.indexOf("/ui/eapplication") >= 0 || reqURI.indexOf("/ui/session") >= 0 || reqURI.contains("javax.faces.resource") || reqURI.indexOf("/ui/userNotFound.xhtml") >= 0 || reqURI.indexOf("/ui/login/forgetPassword.xhtml") >= 0 || reqURI.indexOf("/ui/contactus.xhtml") >= 0) {
                    // do nothing - all the blocks concerning these urls have been taking care of earlier
                } else if (ses != null && ses.getAttribute("user_id") != null && ses.getAttribute("casTicket") != null
                        && !UserDAO.validateCASTicketForLoggedInUser((String) ses.getAttribute("user_id"), (String) ses.getAttribute("casTicket"))) {
//                } else if (ses != null && ses.getAttribute("casTicket") != null && !new CASAuthentication().casTicketStatus((String) ses.getAttribute("casTicket"))) {
                    ses.invalidate();
                    ses = null;
                    res.sendRedirect("/vahan" + timeoutPage);
                    return;
                }
                chain.doFilter(request, response);
            } else if (reqURI.equalsIgnoreCase(req.getContextPath() + "/")) {
                //res.sendRedirect("/vahan/vahan/ui/login/login.xhtml");  // Anonymous user. Redirect to login page/home page
                res.sendRedirect("/vahan/vahan4/redirect/login.xhtml");
//                res.sendRedirect("http://127.0.0.1/vahan");
                return;
            } // user didn't log in but asking for a page that is not allowed so take user to login page/home page
            else {
                String redirectURL = "";
                if (ses != null && ses.getAttribute("emp_cd") == null && ses.getAttribute("state_cd") == null && ses.getAttribute("off_cd") == null) {
                    if (isAJAXRequest(req)) {
//                        redirectURL = res.encodeRedirectURL(req.getContextPath() + "/vahan/ui/session/warning.xhtml");
                        redirectURL = res.encodeRedirectURL("/vahan/vahan/ui/session/warning.xhtml");
                        StringBuilder sb = new StringBuilder();
                        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><partial-response><redirect url=\"").append(redirectURL).append("\"></redirect></partial-response>");
                        res.setHeader("Cache-Control", "no-cache");
                        res.setCharacterEncoding("UTF-8");
                        res.setContentType("text/xml");
                        PrintWriter pw = res.getWriter();
                        pw.println(sb.toString());
                        pw.flush();
                    } else {
                        //res.sendRedirect("/vahan/vahan/ui/login/login.xhtml");  // Anonymous user. Redirect to login page/home page
                        res.sendRedirect("/vahan/vahan4/redirect/login.xhtml");
//                        res.sendRedirect("http://127.0.0.1/vahan");
                        return;
                    }

                } else if (ses == null) {
//                    if (Util.isForcedLogin()) {
//                        redirectURL = res.encodeRedirectURL(req.getContextPath() + forcedTimeoutPage);
//                    } else {
//                    redirectURL = res.encodeRedirectURL(req.getContextPath() + timeoutPage);
                    redirectURL = res.encodeRedirectURL("/vahan" + timeoutPage);
//                    }

                    if (isAJAXRequest(req)) {
                        //LOGGER.debug("Ajax Request Session timeout");
                        StringBuilder sb = new StringBuilder();
                        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><partial-response><redirect url=\"").append(redirectURL).append("\"></redirect></partial-response>");
                        res.setHeader("Cache-Control", "no-cache");
                        res.setCharacterEncoding("UTF-8");
                        res.setContentType("text/xml");
                        PrintWriter pw = res.getWriter();
                        pw.println(sb.toString());
                        pw.flush();
                    } else {
                        //LOGGER.debug("Non-Ajax Request Session timeout");                        
//                        res.sendRedirect(req.getContextPath() + timeoutPage);  // Anonymous user. Redirect to login page
//                        if (Util.isForcedLogin()) {
//                            res.sendRedirect(req.getContextPath() + forcedTimeoutPage);
//                        } else {
//                        res.sendRedirect(req.getContextPath() + timeoutPage);
                        res.sendRedirect("/vahan" + timeoutPage);
                        return;
//                        }
                    }
                }
            }
        } catch (IOException | ServletException e) {
            e.printStackTrace();
            exception = e;
        } catch (Exception ex) {
            ex.printStackTrace();
            exception = ex;
        }

        if (exception != null) {
            if (ses != null && ses.getAttribute("emp_cd") != null && reqURI.indexOf("/error.xhtml") < 0) {
                if (onlinePayment) {
                    res.sendRedirect("/vahan/vahan/ui/session/outside-error.xhtml");
                } else {
//                    res.sendRedirect(req.getContextPath() + "/vahan/ui/session/error.xhtml");
                    res.sendRedirect("/vahan/vahan/ui/session/error.xhtml");
                }
            }
            if (ses == null && reqURI.indexOf("/outside-error.xhtml") < 0) {
                res.sendRedirect("/vahan/vahan/ui/session/outside-error.xhtml");
            }
            if (ses != null && ses.getAttribute("emp_cd") == null && reqURI.indexOf("/outside-error.xhtml") < 0) {
                res.sendRedirect("/vahan/vahan/ui/session/outside-error.xhtml");
            }
            //LOGGER.error(exception.toString() + "" + exception.getStackTrace()[0]);
        }
    } //end of doFilter

    private boolean isAJAXRequest(HttpServletRequest request) {
        boolean check = false;
        String facesRequest = request.getHeader("Faces-Request");
        if (facesRequest != null && facesRequest.equals("partial/ajax")) {
            check = true;
        }
        return check;
    }// end of isAJAXRequest

    @Override
    public void destroy() {
    }
}//end of AuthFilter
