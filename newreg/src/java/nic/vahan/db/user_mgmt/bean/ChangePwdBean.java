/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.bean;

import java.io.Serializable;
import java.sql.SQLException;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import nic.rto.vahan.common.MsgProperties;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.user_mgmt.dobj.ChangePwdDobj;
import nic.vahan.db.user_mgmt.impl.ChangePwdImpl;
import nic.vahan.form.dobj.common.DOAuditTrail;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.primefaces.PrimeFaces;

/**
 *
 * @author tranC103
 */
@ManagedBean
@ViewScoped
public class ChangePwdBean implements Serializable {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ChangePwdBean.class);
    private ChangePwdDobj pwdDobj;

    public ChangePwdBean() {
        pwdDobj = new ChangePwdDobj();
    }

    @PostConstruct
    public void init() {
        pwdDobj.setUser_cd(Long.parseLong(Util.getEmpCode()));
    }

    public void updatePassword() {
        try {
            String strMsg = "";
            Boolean usedOldPwdstatus = ChangePwdImpl.checkUsedOldPassword(pwdDobj);
            if (!CommonUtils.isNullOrBlank(pwdDobj.getOldPwd())
                    && !CommonUtils.isNullOrBlank(pwdDobj.getNewPwd())
                    && !CommonUtils.isNullOrBlank(pwdDobj.getCnfrmNewPwd())) {
                if (pwdDobj.getOldPwd().equals(pwdDobj.getNewPwd())) {
                    pwdDobj.setNewPwd("");
                    pwdDobj.setOldPwd("");
                    PrimeFaces.current().ajax().update("samePwdDialog");
                    PrimeFaces.current().executeScript("PF('samepswd').show()");
                } else if (usedOldPwdstatus) {
                    pwdDobj.setNewPwd("");
                    pwdDobj.setOldPwd("");
                    PrimeFaces.current().ajax().update("usedPwdDialog");
                    PrimeFaces.current().executeScript("PF('usedpswd').show()");
                } else {
                    ChangePwdImpl changePwdImpl = new ChangePwdImpl();
                    String str = changePwdImpl.checkOrUpdatePassword(pwdDobj);
                    if (!CommonUtils.isNullOrBlank(str)) {
                        if (str.equalsIgnoreCase("Y")) {
                            PrimeFaces.current().ajax().update("pswdDialog");
                            PrimeFaces.current().executeScript("PF('resetpswd').show()");
                        } else {
                            strMsg = "Old Password does not exist.";
                            JSFUtils.setFacesMessage(strMsg, null, JSFUtils.WARN);
                        }
                    } else {
                        strMsg = "Technical Error.";
                        JSFUtils.setFacesMessage(strMsg, null, JSFUtils.ERROR);
                    }
                }
            } else {
                strMsg = "Insufficient Data";
                JSFUtils.setFacesMessage(strMsg, null, JSFUtils.ERROR);
            }
        } catch (VahanException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public String logout() {
        String user_id = null;
        HttpSession session = null;
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
        return "homePage";
    }

    public ChangePwdDobj getPwdDobj() {
        return pwdDobj;
    }

    public void setPwdDobj(ChangePwdDobj pwdDobj) {
        this.pwdDobj = pwdDobj;
    }
}
