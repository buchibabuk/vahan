/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.user_mgmt.dobj.FitnessOfficerMobileDtlsDobj;
import nic.vahan.db.user_mgmt.impl.FitnessOfficerMobileDtlsImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author DELL
 */
@ManagedBean(name = "fitoffmobdtls")
@ViewScoped
public class FitnessOfficerMobileDtlsBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(FitnessOfficerMobileDtlsBean.class);
    private SessionVariables sessionVariables = null;
    private List fitofflist = new ArrayList();
    FitnessOfficerMobileDtlsImpl impl = new FitnessOfficerMobileDtlsImpl();
    private FitnessOfficerMobileDtlsDobj dobj = new FitnessOfficerMobileDtlsDobj();
    private List<FitnessOfficerMobileDtlsDobj> officeLocationList;
    private List<FitnessOfficerMobileDtlsDobj> handleDeviceList;
    private String user_catg;
    private String state_cd;
    private String user_id;
    private Integer off_cd;
    private Boolean rendredSaveButton = false;
    private Boolean rendredRejectButton = false;
    String msg = null;

    public FitnessOfficerMobileDtlsBean() {
        try {
            sessionVariables = new SessionVariables();
            if (sessionVariables == null
                    || sessionVariables.getStateCodeSelected() == null
                    || sessionVariables.getEmpCodeLoggedIn() == null) {
                throw new VahanException(TableConstants.SomthingWentWrong);
            } else {
                state_cd = Util.getUserStateCode();
                off_cd = Util.getUserOffCode();
                user_id = Util.getUserId();
                fitofflist = impl.getFitnessOfficerDetails(state_cd, off_cd);
                fillHandledDeviceDataTable();
                fillOfficeLocationDataTable();
            }
        } catch (VahanException vex) {
            JSFUtils.showMessage(vex.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void fitnessListener() {
        String status = null;
        try {
            user_catg = Util.getUserCategory();
            if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_OFFICE_ADMIN)) {
                FitnessOfficerMobileDtlsDobj mobiledtlsdobj = FitnessOfficerMobileDtlsImpl.getfitoffData(dobj.getUser_id(), state_cd, off_cd);
                if (mobiledtlsdobj != null) {
                    dobj.setUser_id(mobiledtlsdobj.getUser_id());
                    dobj.setUserName(mobiledtlsdobj.getUserName());
                    dobj.setDevice_id(mobiledtlsdobj.getDevice_id());
                    dobj.setMobileNo(mobiledtlsdobj.getMobileNo());
                    status = impl.checkRecordStatus(dobj);
                    if (status != null) {
                        switch (status) {
                            case "requested":
                                rendredSaveButton = true;
                                rendredRejectButton = true;
                                break;
                            case "rejected":
                                rendredSaveButton = true;
                                break;
                            case "deleted":
                                rendredSaveButton = true;
                                break;
                            case "handled":
                                JSFUtils.showMessagesInDialog("Alert", "Record Already Handled for " + mobiledtlsdobj.getUser_id() + ",Please Select Another Fitness Officer!!", FacesMessage.SEVERITY_INFO);
                                break;
                        }
                    } else {
                        JSFUtils.setFacesMessage(TableConstants.SomthingWentWrong, null, JSFUtils.ERROR);
                    }
                }
            }
        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public String saveFitnessMobileDtls() throws VahanException {
        String path = "";
        boolean flag = false;
        try {
            FitnessOfficerMobileDtlsImpl fitOffMobDtls = new FitnessOfficerMobileDtlsImpl();
            flag = fitOffMobDtls.updateFitoffDtls(dobj, user_id, state_cd, off_cd);
            if (flag) {
                JSFUtils.setFacesMessage("Device Verify Successfully.", null, JSFUtils.INFO);
                dobj.setUser_id("");
                dobj.setUserName("");
                dobj.setMobileNo("");
                dobj.setDevice_id("");
                rendredSaveButton = false;
                handleDeviceList = FitnessOfficerMobileDtlsImpl.fillHandledDeviceDataTable(state_cd, off_cd);
            } else {
                JSFUtils.setFacesMessage("Technical Error", null, JSFUtils.ERROR);
            }
        } catch (VahanException e) {
            JSFUtils.setFacesMessage(e.getMessage(), null, JSFUtils.INFO);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            JSFUtils.setFacesMessage("Technical Error", null, JSFUtils.ERROR);
        }
        return path;
    }

    public String rejectFitnessMobileDtls() throws VahanException {
        String path = "";
        boolean flag = false;
        try {
            FitnessOfficerMobileDtlsImpl fitOffMobDtls = new FitnessOfficerMobileDtlsImpl();
            flag = fitOffMobDtls.rejectFitoffDtls(dobj, user_id, state_cd, off_cd);
            if (flag) {
                JSFUtils.setFacesMessage("Device Details Rejected Successfully.", null, JSFUtils.INFO);
                dobj.setUser_id("");
                dobj.setUserName("");
                dobj.setMobileNo("");
                dobj.setDevice_id("");
                handleDeviceList = FitnessOfficerMobileDtlsImpl.fillHandledDeviceDataTable(state_cd, off_cd);
            } else {
                JSFUtils.setFacesMessage("Technical Error", null, JSFUtils.ERROR);
            }
        } catch (VahanException e) {
            JSFUtils.setFacesMessage(e.getMessage(), null, JSFUtils.INFO);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            JSFUtils.setFacesMessage("Technical Error", null, JSFUtils.ERROR);
        }
        return path;
    }

    private void fillHandledDeviceDataTable() {
        try {
            handleDeviceList = FitnessOfficerMobileDtlsImpl.fillHandledDeviceDataTable(state_cd, off_cd);
        } catch (VahanException e) {
            LOGGER.error(e.getMessage());
            JSFUtils.setFacesMessage("Technical Error.", null, JSFUtils.ERROR);
        }
    }

    // For Fitness Office Location 
    public String saveFitnessOfficeDtls() throws VahanException {
        String path = "";
        boolean flag = false;
        try {
            FitnessOfficerMobileDtlsImpl fitOffMobDtls = new FitnessOfficerMobileDtlsImpl();
            if (!CommonUtils.isNullOrBlank(dobj.getLocationName())
                    && !CommonUtils.isNullOrBlank(dobj.getLatitude()) && !CommonUtils.isNullOrBlank(dobj.getLongitude())) {
                flag = fitOffMobDtls.saveFitnessOfficeDtls(dobj, state_cd, off_cd);
                officeLocationList = FitnessOfficerMobileDtlsImpl.fillOfficeLocationDataTable(state_cd, off_cd);
            }
            if (flag) {
                JSFUtils.setFacesMessage("Office Location Entry Successfully Saved.", null, JSFUtils.INFO);
                dobj.setLatitude("");
                dobj.setLongitude("");
                dobj.setLocationName("");
            } else {
                JSFUtils.setFacesMessage("Technical Error, Please Try Again", null, JSFUtils.ERROR);
                dobj.setLatitude("");
                dobj.setLongitude("");
                dobj.setLocationName("");
            }
        } catch (VahanException e) {
            JSFUtils.setFacesMessage(e.getMessage(), null, JSFUtils.INFO);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            JSFUtils.setFacesMessage("Technical Error", null, JSFUtils.ERROR);
        }
        return path;
    }

    private void fillOfficeLocationDataTable() {
        try {
            officeLocationList = FitnessOfficerMobileDtlsImpl.fillOfficeLocationDataTable(state_cd, off_cd);
        } catch (VahanException e) {
            LOGGER.error(e.getMessage());
            JSFUtils.setFacesMessage("Technical Error.", null, JSFUtils.ERROR);
        }
    }

    public void deleteLocationEntry(FitnessOfficerMobileDtlsDobj dobj) {
        try {
            msg = FitnessOfficerMobileDtlsImpl.deleteLocationEntry(dobj, state_cd, off_cd);
            officeLocationList = FitnessOfficerMobileDtlsImpl.fillOfficeLocationDataTable(state_cd, off_cd);
            JSFUtils.showMessage(msg);
        } catch (VahanException ex) {
            JSFUtils.showMessage(ex.getMessage());
        }
    }

    public void deleteHandledRecord(FitnessOfficerMobileDtlsDobj dobj) {
        try {
            msg = FitnessOfficerMobileDtlsImpl.deleteHandleRecord(dobj, state_cd, off_cd);
            handleDeviceList = FitnessOfficerMobileDtlsImpl.fillHandledDeviceDataTable(state_cd, off_cd);
            JSFUtils.showMessage(msg);
        } catch (VahanException ex) {
            JSFUtils.showMessage(ex.getMessage());
        }
    }

    /**
     * @return the fitofflist
     */
    public List getFitofflist() {
        return fitofflist;
    }

    /**
     * @param fitofflist the fitofflist to set
     */
    public void setFitofflist(List fitofflist) {
        this.fitofflist = fitofflist;
    }

    /**
     * @return the dobj
     */
    public FitnessOfficerMobileDtlsDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(FitnessOfficerMobileDtlsDobj dobj) {
        this.dobj = dobj;
    }

    /**
     * @return the user_catg
     */
    public String getUser_catg() {
        return user_catg;
    }

    /**
     * @param user_catg the user_catg to set
     */
    public void setUser_catg(String user_catg) {
        this.user_catg = user_catg;
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
    public Integer getOff_cd() {
        return off_cd;
    }

    /**
     * @param off_cd the off_cd to set
     */
    public void setOff_cd(Integer off_cd) {
        this.off_cd = off_cd;
    }

    /**
     * @return the rendredSaveButton
     */
    public Boolean getRendredSaveButton() {
        return rendredSaveButton;
    }

    /**
     * @param rendredSaveButton the rendredSaveButton to set
     */
    public void setRendredSaveButton(Boolean rendredSaveButton) {
        this.rendredSaveButton = rendredSaveButton;
    }

    /**
     * @return the user_id
     */
    public String getUser_id() {
        return user_id;
    }

    /**
     * @param user_id the user_id to set
     */
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    /**
     * @return the officeLocationList
     */
    public List<FitnessOfficerMobileDtlsDobj> getOfficeLocationList() {
        return officeLocationList;
    }

    /**
     * @param officeLocationList the officeLocationList to set
     */
    public void setOfficeLocationList(List<FitnessOfficerMobileDtlsDobj> officeLocationList) {
        this.officeLocationList = officeLocationList;
    }

    /**
     * @return the rendredRejectButton
     */
    public Boolean getRendredRejectButton() {
        return rendredRejectButton;
    }

    /**
     * @param rendredRejectButton the rendredRejectButton to set
     */
    public void setRendredRejectButton(Boolean rendredRejectButton) {
        this.rendredRejectButton = rendredRejectButton;
    }

    /**
     * @return the handleDeviceList
     */
    public List<FitnessOfficerMobileDtlsDobj> getHandleDeviceList() {
        return handleDeviceList;
    }

    /**
     * @param handleDeviceList the handleDeviceList to set
     */
    public void setHandleDeviceList(List<FitnessOfficerMobileDtlsDobj> handleDeviceList) {
        this.handleDeviceList = handleDeviceList;
    }
}
