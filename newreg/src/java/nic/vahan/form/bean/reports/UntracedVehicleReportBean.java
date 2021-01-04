/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.form.dobj.BlackListedVehicleDobj;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Afzal
 */
@ManagedBean(name = "untracedReportBean")
@ViewScoped
public class UntracedVehicleReportBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(UntracedVehicleReportBean.class);
    private String userName;
    private List<BlackListedVehicleDobj> untracedVehicleDobj = new ArrayList<>();
    private BlackListedVehicleDobj dobj = null;
    private SessionVariables sessionVariables;

    @PostConstruct
    public void init() {
        try {
            setSessionVariables(new SessionVariables());
            if (getSessionVariables() == null || CommonUtils.isNullOrBlank(getSessionVariables().getStateCodeSelected())
                    || getSessionVariables().getOffCodeSelected() == 0 || getSessionVariables().getActionCodeSelected() == 0) {
                return;
            }
            setUserName(getSessionVariables().getEmpNameLoggedIn());
            Map map;
            if (getUntracedVehicleDobj() != null && !getUntracedVehicleDobj().isEmpty()) {
                getUntracedVehicleDobj().clear();
            }

            map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            setDobj((BlackListedVehicleDobj) map.get("UntracedDobj"));
            map.remove("UntracedDobj");

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the dobj
     */
    public BlackListedVehicleDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(BlackListedVehicleDobj dobj) {
        this.dobj = dobj;
    }

    /**
     * @return the sessionVariables
     */
    public SessionVariables getSessionVariables() {
        return sessionVariables;
    }

    /**
     * @param sessionVariables the sessionVariables to set
     */
    public void setSessionVariables(SessionVariables sessionVariables) {
        this.sessionVariables = sessionVariables;
    }

    /**
     * @return the untracedVehicleDobj
     */
    public List<BlackListedVehicleDobj> getUntracedVehicleDobj() {
        return untracedVehicleDobj;
    }

    /**
     * @param untracedVehicleDobj the untracedVehicleDobj to set
     */
    public void setUntracedVehicleDobj(List<BlackListedVehicleDobj> untracedVehicleDobj) {
        this.untracedVehicleDobj = untracedVehicleDobj;
    }
}
