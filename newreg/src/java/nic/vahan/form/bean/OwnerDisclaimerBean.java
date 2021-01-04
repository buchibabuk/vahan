/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.dobj.OwnerDisclaimerDobj;
import nic.vahan.form.impl.OwnerDisclaimerImpl;
import nic.vahan.form.impl.PrintDocImpl;
import org.apache.log4j.Logger;

/**
 *
 * @author nic
 */
@ManagedBean(name = "ownerDiscmrBean")
@ViewScoped
public class OwnerDisclaimerBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(OwnerDisclaimerBean.class);
    private String appl_no;
    private String applNoForRegisteredVehicle;
    private List<OwnerDisclaimerBean> filteredSeat = null;
    private List<OwnerDisclaimerDobj> printCertDobj = new ArrayList<OwnerDisclaimerDobj>();

    public void setListBeans(List<OwnerDisclaimerDobj> listDobjs) {
        setPrintCertDobj(listDobjs);
    }

    @PostConstruct
    public void init() {
        try {
            this.setListBeans(OwnerDisclaimerImpl.getPurCdPrintDocsDetails());
        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public String confirmprintOwnerDisc() {
        return PrintDocImpl.printOwnerDiscReport("newRegisteredVehicles", "reportFormat");
    }

    public String showDetails() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("appl", getApplNoForRegisteredVehicle().toUpperCase());
        return PrintDocImpl.printOwnerDiscReport("registeredVehicles", "reportFormat");

    }

    /**
     * @return the appl_no
     */
    public String getAppl_no() {
        return appl_no;
    }

    /**
     * @param appl_no the appl_no to set
     */
    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    /**
     * @return the filteredSeat
     */
    public List<OwnerDisclaimerBean> getFilteredSeat() {
        return filteredSeat;
    }

    /**
     * @param filteredSeat the filteredSeat to set
     */
    public void setFilteredSeat(List<OwnerDisclaimerBean> filteredSeat) {
        this.filteredSeat = filteredSeat;
    }

    /**
     * @return the printCertDobj
     */
    public List<OwnerDisclaimerDobj> getPrintCertDobj() {
        return printCertDobj;
    }

    /**
     * @param printCertDobj the printCertDobj to set
     */
    public void setPrintCertDobj(List<OwnerDisclaimerDobj> printCertDobj) {
        this.printCertDobj = printCertDobj;
    }

    /**
     * @return the applNoForRegisteredVehicle
     */
    public String getApplNoForRegisteredVehicle() {
        return applNoForRegisteredVehicle;
    }

    /**
     * @param applNoForRegisteredVehicle the applNoForRegisteredVehicle to set
     */
    public void setApplNoForRegisteredVehicle(String applNoForRegisteredVehicle) {
        this.applNoForRegisteredVehicle = applNoForRegisteredVehicle;
    }
}
