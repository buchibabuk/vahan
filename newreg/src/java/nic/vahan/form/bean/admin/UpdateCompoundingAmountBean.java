/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.dobj.BlackListedVehicleDobj;
import nic.vahan.form.impl.BlackListedVehicleImpl;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author acer
 */
@ViewScoped
@ManagedBean(name = "updateCompoundingAmountBean")
public class UpdateCompoundingAmountBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(UpdateCompoundingAmountBean.class);
    private List<BlackListedVehicleDobj> dobjList = new ArrayList<>();
    private String regn_no;
    BlackListedVehicleImpl impl = new BlackListedVehicleImpl();
    private boolean amtPanel = false;

    public void showDetails() {

        try {
            dobjList = impl.getCompoundingAmountDetails(regn_no);
            if (dobjList == null || dobjList.isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Vehicle is not Blacklisted in this Office for Compounding Amount"));
                return;
            } else {
                amtPanel = true;
            }

        } catch (VahanException ve) {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
    }

    public void update(BlackListedVehicleDobj obj) {
        try {
            if (obj.getCompounding_amt() == 0) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Compounding Amount can not be 0.."));
                return;
            }
            impl.updateCompoundingAmount(regn_no, obj.getCompounding_amt(), obj.getComplain_dt());

        } catch (VahanException ve) {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        //RequestContext rc = RequestContext.getCurrentInstance();
        PrimeFaces.current().ajax().update("FormUpdateCompoundingAmount:db_reopened");
        PrimeFaces.current().executeScript("PF('dlgdb_reopened').show()");
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public List<BlackListedVehicleDobj> getDobjList() {
        return dobjList;
    }

    public void setDobjList(List<BlackListedVehicleDobj> dobjList) {
        this.dobjList = dobjList;
    }

    public boolean isAmtPanel() {
        return amtPanel;
    }

    public void setAmtPanel(boolean amtPanel) {
        this.amtPanel = amtPanel;
    }
}
