/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.bean.OwnerDisclaimerBean;
import nic.vahan.form.dobj.reports.ScrapVehicleDobj;
import nic.vahan.form.impl.ScrapVehicleImpl;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author afzal
 */
@ManagedBean(name = "scrapBean")
@ViewScoped
public class ScrapVehicleBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(OwnerDisclaimerBean.class);
    private List<ScrapVehicleBean> filteredSeat = null;
    private List<ScrapVehicleDobj> printScrapDobj = new ArrayList<ScrapVehicleDobj>();
    private String old_regn_no;
    private String state_cd;
    private int off_cd;

    public void showDetails() {
        ArrayList<ScrapVehicleDobj> list = null;
        printScrapDobj.clear();
        try {
            if (this.old_regn_no == null || this.old_regn_no.equals("")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Old Registration should not be blank!"));
                return;
            }
            list = ScrapVehicleImpl.getScrapDetails(this.old_regn_no.toUpperCase());
            if (list.isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Record not found!"));
                return;
            }
            printScrapDobj.addAll(list);

        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void confirmprintScrap(ScrapVehicleDobj dobj) {
        Map map;
        map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        old_regn_no = (String) map.get("OLD_REGN_NO");
        state_cd = (String) map.get("STATE_CD");
        off_cd = Integer.parseInt((String) map.get("OFF_CD"));
        // RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().executeScript("PF('printScrapCert').show()");
    }

    public String printScrapCertificate() {
        return "ScrappedVehicleReport";

    }

    /**
     * @return the filteredSeat
     */
    public List<ScrapVehicleBean> getFilteredSeat() {
        return filteredSeat;
    }

    /**
     * @param filteredSeat the filteredSeat to set
     */
    public void setFilteredSeat(List<ScrapVehicleBean> filteredSeat) {
        this.filteredSeat = filteredSeat;
    }

    /**
     * @return the printScrapDobj
     */
    public List<ScrapVehicleDobj> getPrintScrapDobj() {
        return printScrapDobj;
    }

    /**
     * @param printScrapDobj the printScrapDobj to set
     */
    public void setPrintScrapDobj(List<ScrapVehicleDobj> printScrapDobj) {
        this.printScrapDobj = printScrapDobj;
    }

    /**
     * @return the old_regn_no
     */
    public String getOld_regn_no() {
        return old_regn_no;
    }

    /**
     * @param old_regn_no the old_regn_no to set
     */
    public void setOld_regn_no(String old_regn_no) {
        this.old_regn_no = old_regn_no;
    }

    /**
     * @return the state_cd
     */
    public String getState_cd() {
        return state_cd;
    }

    /**
     * @param state_cd the state_cd to set
     */
    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    /**
     * @return the off_cd
     */
    public int getOff_cd() {
        return off_cd;
    }

    /**
     * @param off_cd the off_cd to set
     */
    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }
}
