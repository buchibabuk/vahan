/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.form.dobj.rtoLaunchDobj;
import nic.vahan.form.impl.rtoLaunchImpl;
import org.primefaces.PrimeFaces;
import org.apache.log4j.Logger;

/**
 *
 * @author nicsi
 */
@ManagedBean(name = "rto_launch")
@ViewScoped
public class rtoLaunchBean {

    private static Logger LOGGER = Logger.getLogger(rtoLaunchBean.class);
    private ArrayList<rtoLaunchDobj> rtoLaunchList = new ArrayList<>();
    private ArrayList<rtoLaunchDobj> runningRtoList = new ArrayList<>();
    rtoLaunchImpl impl = new rtoLaunchImpl();
    rtoLaunchDobj dobj = new rtoLaunchDobj();
    Date max_date = new Date();
    Date min_date = DateUtil.parseDate(DateUtil.getCurrentDate());
    boolean showBackButton;
    private String launchRto;
    private Map<String, Object> launchRtoList;

    public rtoLaunchBean() {
        init();
    }

    public void init() {
        try {
            launchRtoList = impl.getRtosForLaunch();
            Calendar cal = Calendar.getInstance();
            cal.setTime(max_date);
            cal.add(Calendar.DATE, 7);
            max_date = cal.getTime();
            getRunningRtoLIst();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        }
    }

    public void getRunningRtoLIst() {
        try {
            runningRtoList = impl.getRunningRtoList();
            PrimeFaces.current().executeScript("PF('" + "dialog_runn_rto" + "').show()");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        }
    }

    public void getRtoDetails() {
        try {
            dobj = impl.getAllRtoList(Integer.parseInt(launchRto));
        } catch (NumberFormatException | VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        }

    }

    public void saveLaunchRto() {
        try {
            if ("-1".equalsIgnoreCase(launchRto)) {
                JSFUtils.setFacesMessage("Please Select The Rto", "message", JSFUtils.WARN);
                return;
            }
            if (dobj.getVow4_launch_date() != null) {
                boolean save = impl.saveRtoToBeLaunched(dobj);
                if (save) {
                    JSFUtils.setFacesMessage("Selected Rto Launch Date Submitted Successfully", "message", JSFUtils.INFO);
                    showBackButton = true;
                }
            } else {
                JSFUtils.setFacesMessage("Please Select the Launch Date For The Selscted Rto", "message", JSFUtils.INFO);
                return;
            }

        } catch (VahanException | SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        }
    }

    public ArrayList<rtoLaunchDobj> getRtoLaunchList() {
        return rtoLaunchList;
    }

    public void setRtoLaunchList(ArrayList<rtoLaunchDobj> rtoLaunchList) {
        this.rtoLaunchList = rtoLaunchList;
    }

    public ArrayList<rtoLaunchDobj> getRunningRtoList() {
        return runningRtoList;
    }

    public void setRunningRtoList(ArrayList<rtoLaunchDobj> runningRtoList) {
        this.runningRtoList = runningRtoList;
    }

    public Date getMax_date() {
        return max_date;
    }

    public void setMax_date(Date max_date) {
        this.max_date = max_date;
    }

    public Date getMin_date() {
        return min_date;
    }

    public void setMin_date(Date min_date) {
        this.min_date = min_date;
    }

    public boolean isShowBackButton() {
        return showBackButton;
    }

    public void setShowBackButton(boolean showBackButton) {
        this.showBackButton = showBackButton;
    }

    public String getLaunchRto() {
        return launchRto;
    }

    public void setLaunchRto(String launchRto) {
        this.launchRto = launchRto;
    }

    public Map<String, Object> getLaunchRtoList() {
        return launchRtoList;
    }

    public void setLaunchRtoList(Map<String, Object> launchRtoList) {
        this.launchRtoList = launchRtoList;
    }

    public rtoLaunchDobj getDobj() {
        return dobj;
    }

    public void setDobj(rtoLaunchDobj dobj) {
        this.dobj = dobj;
    }
}
