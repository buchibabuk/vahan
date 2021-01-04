/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.bean;

import dao.UserDAO;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.UserInfo;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;

@ViewScoped
@ManagedBean
public class HeaderAfterLoginBean implements Serializable {

    private UserInfo dobj = new UserInfo();
    private String session_id = "";

    public HeaderAfterLoginBean() {
        validateMultipleLogin();
    }

    public void validateMultipleLogin() {
        HttpSession ses = Util.getSession();
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        session_id = req.getSession().getId();
        Exception e = null;
        if (Util.getUserId() != null && session_id != null) {
            try {
                dobj = UserDAO.getSessionIdAndIPDetails(Util.getUserId());
                if (dobj != null) {
//                    if (!session_id.equals(dobj.getSession_id()) || !Util.getRequest().getRemoteAddr().equals(dobj.getIp_address())) {
                    if (!Util.getRequest().getRemoteAddr().equals(dobj.getIp_address())) {
                        Util.removeAllSessionAttribute(Util.getSession());
                        return;
                    }
                }
            } catch (VahanException vex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, vex.getMessage(), vex.getMessage()));
                return;
            } catch (Exception ex) {
                Logger.getLogger(HeaderAfterLoginBean.class.getName()).log(Level.SEVERE, null, ex);
                e = new VahanException(TableConstants.SomthingWentWrong);
            }
        }
        if (ses.getAttribute("emp_cd") != null && !ServerUtil.validateIpAddress(Long.valueOf(ses.getAttribute("emp_cd").toString()), req.getRemoteAddr())) {
            ses.setAttribute("emp_cd", null);
            FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "warning");
            return;
        }
        if (e != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getMessage()));
            return;
        }
    }
}
