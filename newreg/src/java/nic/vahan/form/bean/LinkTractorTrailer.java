/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.impl.LinkTractorTrailerImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;

/**
 *
 * @author acer
 */
@ManagedBean
@ViewScoped
public class LinkTractorTrailer implements Serializable {

    private OwnerDetailsDobj tractorDobj;
    private OwnerDetailsDobj trailerDobj;
    private String tractorRegnNo;
    private String trailerRegnNo;
    private List<OwnerDetailsDobj> vehicleDtlsList = new ArrayList<>();
    private boolean disableTractor;
    private boolean disableTrailer;
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(LinkTractorTrailer.class);

    public void tractorRegnFocusLost() {
        try {
            if (tractorRegnNo != null && !tractorRegnNo.isEmpty()) {
                tractorDobj = new OwnerImpl().getOwnerDetails(tractorRegnNo);
                if (tractorDobj != null) {
                    if (!TableConstants.TRACTOR_VEH_CLASS.contains("," + tractorDobj.getVh_class() + ",")) {
                        JSFUtils.setFacesMessage("Invalid Vehicle Registration Number For Tractor.", null, JSFUtils.ERROR);
                        tractorRegnNo = "";
                    } else {
                        vehicleDtlsList.add(tractorDobj);
                        disableTractor = true;
                    }
                } else {
                    JSFUtils.setFacesMessage("Invalid Vehicle Registration Number For Tractor.", null, JSFUtils.ERROR);
                    tractorRegnNo = "";
                }
            } else {
                JSFUtils.setFacesMessage("Empty Tractor Registration Number", null, JSFUtils.ERROR);
            }
        } catch (VahanException ex) {
            JSFUtils.showMessage(ex.getMessage());
        }
    }

    public void trailerRegnFocusLost() {
        try {
            if (trailerRegnNo != null && !trailerRegnNo.isEmpty()) {
                trailerDobj = new OwnerImpl().getOwnerDetails(trailerRegnNo);
                if (trailerDobj != null) {
                    if (!TableConstants.TRAILER_VEH_CLASS.contains("," + trailerDobj.getVh_class() + ",")) {
                        JSFUtils.setFacesMessage("Invalid Vehicle Registration Number For Trailer.", null, JSFUtils.ERROR);
                        trailerRegnNo = "";
                    } else {
                        vehicleDtlsList.add(trailerDobj);
                        disableTrailer = true;
                    }
                } else {
                    JSFUtils.setFacesMessage("Invalid Vehicle Registration Number For Trailer.", null, JSFUtils.ERROR);
                    trailerRegnNo = "";
                }
            } else {
                JSFUtils.setFacesMessage("Empty Trailer Registration Number", null, JSFUtils.ERROR);
            }
        } catch (VahanException ex) {
            JSFUtils.showMessage(ex.getMessage());
        }
    }

    public void linkVehicles() {
        try {
            LinkTractorTrailerImpl implObj = new LinkTractorTrailerImpl();
            if (!tractorRegnNo.isEmpty() && !trailerRegnNo.isEmpty()) {
                implObj.saveLinkingDetails(tractorRegnNo, trailerRegnNo, Util.getUserStateCode(), Util.getUserOffCode());
                JSFUtils.setFacesMessage("Vehicles Linked successfully", null, JSFUtils.INFO);
                reset();
            } else {
                JSFUtils.setFacesMessage("Please Provide Valid Details", null, JSFUtils.INFO);
            }
        } catch (VahanException ex) {
            LOGGER.error(ex.getMessage());
            JSFUtils.setFacesMessage("Something Went Wrong. Contact to System Administrator", null, JSFUtils.ERROR);
        }
    }

    public void reset() {
        tractorDobj = null;
        trailerDobj = null;
        tractorRegnNo = "";
        trailerRegnNo = "";
        vehicleDtlsList.clear();
        disableTractor = false;
        disableTrailer = false;
    }

    /**
     * @return the tractorDobj
     */
    public OwnerDetailsDobj getTractorDobj() {
        return tractorDobj;
    }

    /**
     * @param tractorDobj the tractorDobj to set
     */
    public void setTractorDobj(OwnerDetailsDobj tractorDobj) {
        this.tractorDobj = tractorDobj;
    }

    /**
     * @return the trailerDobj
     */
    public OwnerDetailsDobj getTrailerDobj() {
        return trailerDobj;
    }

    /**
     * @param trailerDobj the trailerDobj to set
     */
    public void setTrailerDobj(OwnerDetailsDobj trailerDobj) {
        this.trailerDobj = trailerDobj;
    }

    /**
     * @return the tractorRegnNo
     */
    public String getTractorRegnNo() {
        return tractorRegnNo;
    }

    /**
     * @param tractorRegnNo the tractorRegnNo to set
     */
    public void setTractorRegnNo(String tractorRegnNo) {
        this.tractorRegnNo = tractorRegnNo;
    }

    /**
     * @return the trailerRegnNo
     */
    public String getTrailerRegnNo() {
        return trailerRegnNo;
    }

    /**
     * @param trailerRegnNo the trailerRegnNo to set
     */
    public void setTrailerRegnNo(String trailerRegnNo) {
        this.trailerRegnNo = trailerRegnNo;
    }

    /**
     * @return the vehicleDtlsList
     */
    public List<OwnerDetailsDobj> getVehicleDtlsList() {
        return vehicleDtlsList;
    }

    /**
     * @param vehicleDtlsList the vehicleDtlsList to set
     */
    public void setVehicleDtlsList(List<OwnerDetailsDobj> vehicleDtlsList) {
        this.vehicleDtlsList = vehicleDtlsList;
    }

    /**
     * @return the disableTractor
     */
    public boolean isDisableTractor() {
        return disableTractor;
    }

    /**
     * @param disableTractor the disableTractor to set
     */
    public void setDisableTractor(boolean disableTractor) {
        this.disableTractor = disableTractor;
    }

    /**
     * @return the disableTrailer
     */
    public boolean isDisableTrailer() {
        return disableTrailer;
    }

    /**
     * @param disableTrailer the disableTrailer to set
     */
    public void setDisableTrailer(boolean disableTrailer) {
        this.disableTrailer = disableTrailer;
    }
}
