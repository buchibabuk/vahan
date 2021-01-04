/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.dobj.ExArmyDobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.NidChangedDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.impl.AxleImpl;
import nic.vahan.form.impl.ExArmyImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.NidChangedImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author ASHOK
 */
@ViewScoped
@ManagedBean(name = "nidChangedBean")
public class NidChangedBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(NidChangedBean.class);
    private String regn_no;
    private String message = null;
    private boolean render = false;
    private OwnerDetailsDobj ownerDetail;
    @ManagedProperty(value = "#{exArmyBean}")
    private ExArmyBean exArmyBean;
    @ManagedProperty(value = "#{axleBean}")
    private AxleBean axleBean;
    private FitnessDobj fitnessDobj = null;
    private NidChangedDobj nidChangedDobj = new NidChangedDobj();
    private Date maxDate = new Date();

    public NidChangedBean() {
        nidChangedDobj.setNew_fit_nid(maxDate);
    }

    public void showDetails() {

        if (this.regn_no.trim() == null || this.regn_no.trim().equalsIgnoreCase("")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Please Enter Valid Registration Number"));
            return;
        }

        try {

            OwnerImpl owner_Impl = new OwnerImpl();
            ownerDetail = owner_Impl.getOwnerDetails(this.regn_no.trim(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
            if (ownerDetail == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Registration Number Not Found in Current State and Office"));
                return;
            }
            boolean isblacklistedvehicle;
            TmConfigurationDobj tmConfig = Util.getTmConfiguration();
            isblacklistedvehicle = ServerUtil.isPurposeCodeOnBlacklistedVehicle(regn_no, Util.getSelectedSeat().getAction_cd(), Util.getUserStateCode(), tmConfig);
            if (isblacklistedvehicle) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "Vehicle No. has been Blacklisted."));
                return;
            }

            //for Owner Identification Fields disallow typing
            if (ownerDetail.getOwnerIdentity() != null) {
                ownerDetail.getOwnerIdentity().setFlag(true);
                ownerDetail.getOwnerIdentity().setMobileNoEditable(true);
            }

            if (ownerDetail.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_EXARMY)) {
                ExArmyDobj dobj = ExArmyImpl.setExArmyDetails_db_to_dobj(null, regn_no, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                if (dobj != null) {
                    exArmyBean.setDobj_To_Bean(dobj);
                }
            }

            if (ownerDetail.getVehTypeDescr().equalsIgnoreCase(TableConstants.VM_VEHTYPE_TRANSPORT_DESCR)) {
                AxleDetailsDobj dobj = AxleImpl.setAxleVehDetails_db_to_dobj(null, regn_no, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                if (dobj != null) {
                    axleBean.setDobj_To_Bean(dobj);
                }
            }

            FitnessImpl fitImpl = new FitnessImpl();
            fitnessDobj = fitImpl.set_Fitness_appl_db_to_dobj(regn_no, null);
            if (fitnessDobj == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Vehicle Fitness Detail is not Found."));
                return;
            }

            if (fitnessDobj.getFit_valid_to() != null && fitnessDobj.getFit_valid_to().compareTo(DateUtils.parseDate(DateUtils.getCurrentDate())) < 0) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Fitness of Vehicle is Expired."));
                return;
            }

            if (fitnessDobj.getFit_valid_to() == null && fitnessDobj.getFit_nid() == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Fitness Valid Upto and Next Inspection Date(NID) is not Found in the Database"));
                return;
            }

            if (fitnessDobj.getFit_nid() != null && fitnessDobj.getFit_nid().compareTo(DateUtils.parseDate(DateUtils.getCurrentDate())) > 0) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "You Can not Change NID (Next Inspection Date) Before Expiration of NID [" + fitnessDobj.getFit_nid_descr() + "]"));
                return;
            }

            render = true;

        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ve.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error!", "Registration Number Not Found"));
        }
    }

    public void changeFitnesNID() {

        try {

            NidChangedImpl impl = new NidChangedImpl();

            nidChangedDobj.setRegn_no(regn_no);
            nidChangedDobj.setMoved_by(Util.getEmpCode());
            nidChangedDobj.setOff_cd(Util.getSelectedSeat().getOff_cd());
            nidChangedDobj.setState_cd(Util.getUserStateCode());
            nidChangedDobj.setOld_fit_nid(fitnessDobj.getFit_nid());

            impl.UpdateFitNid(nidChangedDobj);

        } catch (VahanException vex) {
            message = "Exception Occured - Could not Save NID Due to " + vex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            message = "Exception Occured - Could not Save NID Due to " + ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
            return;
        }

        message = "NID is Updated Successfully for Registration Number : " + nidChangedDobj.getRegn_no();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
        render = false;
        regn_no = "";
        PrimeFaces.current().ajax().update("panelOwnerInfo");
        return;
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
     * @return the render
     */
    public boolean isRender() {
        return render;
    }

    /**
     * @param render the render to set
     */
    public void setRender(boolean render) {
        this.render = render;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the ownerDetail
     */
    public OwnerDetailsDobj getOwnerDetail() {
        return ownerDetail;
    }

    /**
     * @param ownerDetail the ownerDetail to set
     */
    public void setOwnerDetail(OwnerDetailsDobj ownerDetail) {
        this.ownerDetail = ownerDetail;
    }

    /**
     * @param exArmyBean the exArmyBean to set
     */
    public void setExArmyBean(ExArmyBean exArmyBean) {
        this.exArmyBean = exArmyBean;
    }

    /**
     * @param axleBean the axleBean to set
     */
    public void setAxleBean(AxleBean axleBean) {
        this.axleBean = axleBean;
    }

    /**
     * @return the fitnessDobj
     */
    public FitnessDobj getFitnessDobj() {
        return fitnessDobj;
    }

    /**
     * @param fitnessDobj the fitnessDobj to set
     */
    public void setFitnessDobj(FitnessDobj fitnessDobj) {
        this.fitnessDobj = fitnessDobj;
    }

    /**
     * @return the nidChangedDobj
     */
    public NidChangedDobj getNidChangedDobj() {
        return nidChangedDobj;
    }

    /**
     * @param nidChangedDobj the nidChangedDobj to set
     */
    public void setNidChangedDobj(NidChangedDobj nidChangedDobj) {
        this.nidChangedDobj = nidChangedDobj;
    }

    /**
     * @return the maxDate
     */
    public Date getMaxDate() {
        return maxDate;
    }

    /**
     * @param maxDate the maxDate to set
     */
    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }
}
