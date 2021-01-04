/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.ApplicationConfiguration;
import nic.vahan.server.ServerUtil;

@RequestScoped
@ManagedBean(name = "masterHome")
public class MasterHome_bean implements Serializable {

    private int pur_cd;
    private boolean allowConn = true;
    private String alertMessage = "";
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(MasterHome_bean.class);

    @PostConstruct
    public void init() {
        try {
            if (ApplicationConfiguration.showMessage == null) {
                ApplicationConfiguration.showMessage = ServerUtil.getAlertMessages("N");
            }
            setAlertMessage(ApplicationConfiguration.showMessage);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public String showEApplication() {
        int pur_cd = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("pur_cd"));
        //FacesContext.getCurrentInstance().getExternalContext().getFlash().put("pur_cd", pur_cd);
        if (pur_cd == 0) {
            return "/ui/eapplication/form_show_tax?faces-redirect=true";
        } else {
            return "/ui/eapplication/form_eApplicatonHome?faces-redirect=true";
        }
    }

    public String showOnlineCashPayment() {
        return "/ui/eapplication/form_payment?faces-redirect=true";
    }

    /**
     * @return the pur_cd
     */
    public int getPur_cd() {
        return pur_cd;
    }

    /**
     * @param pur_cd the pur_cd to set
     */
    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    /**
     * @return the allowConn
     */
    public boolean isAllowConn() {
        allowConn = ApplicationConfiguration.allowConn;
        return allowConn;
    }

    /**
     * @param allowConn the allowConn to set
     */
    public void setAllowConn(boolean allowConn) {
        this.allowConn = allowConn;
    }

    /**
     * @return the alertMessage
     */
    public String getAlertMessage() {
        return alertMessage;
    }

    /**
     * @param alertMessage the alertMessage to set
     */
    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }
}
