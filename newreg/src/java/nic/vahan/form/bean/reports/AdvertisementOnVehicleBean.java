/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.reports.AdvertisementOnVehicleDobj;
import nic.vahan.form.impl.reports.AdvertisementOnVehicleImpl;
import org.apache.log4j.Logger;
//import org.primefaces.context.RequestContext;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "advOnVehicleBean")
@ViewScoped
public class AdvertisementOnVehicleBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(AdvertisementOnVehicleBean.class);
    private String regn_no;
    private AdvertisementOnVehicleDobj dobj = null;

    public void confirmprintCertificate() {
        if (this.getRegn_no() == null || this.getRegn_no().trim().equalsIgnoreCase("")) {
            //RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration No should not be Blank!"));
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration No should not be Blank!"));
            return;
        }
        AdvertisementOnVehicleImpl adv_Impl;
        try {
            adv_Impl = new AdvertisementOnVehicleImpl();
            setDobj(adv_Impl.isRegnExist(this.getRegn_no().toUpperCase()));
            if (getDobj() == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert!", "Vehicle details not found for Registration No " + getRegn_no().toUpperCase()));
                return;
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert!", ve.getMessage()));
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
        //RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().ajax().update("frmAdv_print");
        PrimeFaces.current().executeScript("PF('printAdvCertificate').show()");
    }

    public String printCertificateADV() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("ADVDobj", getDobj());
        return "printAdvetisementOnVehicle";
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
     * @return the dobj
     */
    public AdvertisementOnVehicleDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(AdvertisementOnVehicleDobj dobj) {
        this.dobj = dobj;
    }
}
