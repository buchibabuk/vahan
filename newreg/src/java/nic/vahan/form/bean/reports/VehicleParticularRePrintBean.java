/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.vahan.form.dobj.reports.VehicleParticularDobj;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author nicsi
 */
@ManagedBean(name = "printVehParBean")
@ViewScoped
public class VehicleParticularRePrintBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(VehicleParticularRePrintBean.class);
    private String regn_no;
    private String state_cd;
    private int off_cd;
    private String appl_no;
    private List<VehicleParticularDobj> printVchParDobj = new ArrayList<VehicleParticularDobj>();

    public void setListBeans(List<VehicleParticularDobj> listDobjs) {
        setPrintVchParDobj(listDobjs);
    }

    @PostConstruct
    public void init() {
        try {
            state_cd = Util.getUserStateCode();
            off_cd = Util.getSelectedSeat().getOff_cd();
            ArrayList<VehicleParticularDobj> list = PrintDocImpl.getVchParTodayPrintedDetails(state_cd, off_cd);
            if (list.isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "There is no printed RC as on Today !!"));
                return;
            } else {
                this.setListBeans(list);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void confirmPrintVP() {
        if (regn_no == null || "".contains(regn_no)) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration No should not be Blank !!"));
            return;
        }
        try {
            appl_no = PrintDocImpl.isRegnExistForVehParPrint(regn_no.trim().toUpperCase(), state_cd, off_cd);
            if (appl_no == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration does not exist or you are not authorized to print Vehicle Particular for this Registration !!!!"));
                return;
            } else {
                //RequestContext ca = RequestContext.getCurrentInstance();
                PrimeFaces.current().executeScript("PF('printVP').show()");
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public String printVehicleParticular() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("veh_par_regn_no", regn_no.trim().toUpperCase());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("veh_par_appl_no", appl_no);
        return "rePrintVehParticulatReport";

    }

    /**
     * @return the regn_no
     */
    public String getRegn_no() {
        return regn_no;
    }

    /**
     * @param regn_no the regn_no to set
     */
    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
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
     * @return the printVchParDobj
     */
    public List<VehicleParticularDobj> getPrintVchParDobj() {
        return printVchParDobj;
    }

    /**
     * @param printVchParDobj the printVchParDobj to set
     */
    public void setPrintVchParDobj(List<VehicleParticularDobj> printVchParDobj) {
        this.printVchParDobj = printVchParDobj;
    }
}
