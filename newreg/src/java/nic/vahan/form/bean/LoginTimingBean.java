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
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.LoginTimingDobj;
import nic.vahan.form.impl.LoginTimingImpl;
import org.apache.log4j.Logger;

/**
 *
 * @author acer
 */
@ManagedBean(name = "loginTimingBean")
@ViewScoped
public class LoginTimingBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(LoginTimingBean.class);
    private LoginTimingDobj loginTiming = new LoginTimingDobj();
    private SessionVariables sessionVariables = null;
    private String state_name;
    private String officeName;
    private String user_catg;
    private ArrayList login_list = new ArrayList();
    private List<LoginTimingDobj> loginTiminglist = new ArrayList();
    private List<LoginTimingDobj> officeTiminglist = new ArrayList();
    private List<LoginTimingDobj> loginTiming_FilteredList = null;
    private List<SelectItem> officeList = null;

    public LoginTimingBean() {
        try {
            sessionVariables = new SessionVariables();
            if (sessionVariables == null
                    || sessionVariables.getStateCodeSelected() == null
                    || sessionVariables.getEmpCodeLoggedIn() == null) {
                throw new VahanException(TableConstants.SomthingWentWrong);
            } else {
                state_name = MasterTableFiller.state.get(sessionVariables.getStateCodeSelected()).getStateDescr();
                officeList = (ArrayList<SelectItem>) ((ArrayList<SelectItem>) MasterTableFiller.state.get(sessionVariables.getStateCodeSelected()).getOffice()).clone();
                user_catg = sessionVariables.getUserCatgForLoggedInUser();


                for (SelectItem e : officeList) {
                    if (Integer.valueOf(e.getValue().toString()) == sessionVariables.getOffCodeSelected()) {
                        officeName = e.getLabel();
                        break;
                    }
                }
                getLoginTiming().setStateName(this.state_name);
                getLoginTiming().setOfficeName(this.officeName);
                getLoginTiming().setState_cd(sessionVariables.getStateCodeSelected());
                if (!TableConstants.USER_CATG_STATE_ADMIN.equals(user_catg)) {
                    showDetail();
                } else {
                    officeTiminglist = LoginTimingImpl.getAllOfficesTiming(loginTiming.getStateName(), loginTiming.getOfficeName(), sessionVariables.getStateCodeSelected());
                }
            }
        } catch (VahanException vex) {
            JSFUtils.showMessage(vex.getMessage());
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void gotUserdetails() {
        try {
            loginTiminglist = LoginTimingImpl.getDataAndTimefromdataTable(loginTiming.getStateName(), loginTiming.getOfficeName(), loginTiming.getOff_cd(), loginTiming.getState_cd());
        } catch (VahanException vex) {
            JSFUtils.showMessage(vex.getMessage());
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    private void showDetail() {
        try {
            setLoginTiming(LoginTimingImpl.getDataFromDataTable(sessionVariables.getOffCodeSelected(), sessionVariables.getStateCodeSelected()));
            if (getLoginTiming() == null) {
                setLoginTiming(new LoginTimingDobj());
                getLoginTiming().setSaveButton(true);
                getLoginTiming().setStateName(this.state_name);
                getLoginTiming().setOfficeName(this.officeName);
                getLoginTiming().setState_cd(sessionVariables.getStateCodeSelected());
                getLoginTiming().setOff_cd(sessionVariables.getOffCodeSelected());
                JSFUtils.showMessage("Record Not Available Please Enter Record & Save");
            } else {
                getLoginTiming().setSaveButton(false);
                getLoginTiming().setOfficeName(this.officeName);
                getLoginTiming().setStateName(this.state_name);
            }
        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void save() {
        String msg = null;
        try {
            if ((getLoginTiming() != null && getLoginTiming().getOpen_timing() != null && getLoginTiming().getClose_timing() != null)) {
                msg = (loginTiming.isSaveButton()) ? LoginTimingImpl.insertDataintoDataTable(loginTiming) : LoginTimingImpl.modifydeleteintoDataTable(loginTiming, sessionVariables.getEmpCodeLoggedIn(), loginTiming.getState_cd());
                loginTiminglist = LoginTimingImpl.getDataAndTimefromdataTable(loginTiming.getStateName(), loginTiming.getOfficeName(), sessionVariables.getOffCodeSelected(), loginTiming.getState_cd());
            } else {
                JSFUtils.showMessage("Please Enter Office Open & Close Timing");
            }
            if (TableConstants.USER_CATG_STATE_ADMIN.equals(user_catg)) {
                officeTiminglist = LoginTimingImpl.getAllOfficesTiming(loginTiming.getStateName(), loginTiming.getOfficeName(), sessionVariables.getStateCodeSelected());
            }
            JSFUtils.showMessage(msg);
        } catch (VahanException e) {
            JSFUtils.showMessage(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage("Error in Data Saving.");
        }
    }

    public void saveUserTiming(LoginTimingDobj dobj) {
        String msg = null;
        try {
            msg = LoginTimingImpl.modifyUserTimingintoDataTable(dobj, sessionVariables.getStateCodeSelected(), sessionVariables.getEmpCodeLoggedIn());
            JSFUtils.showMessage(msg);
        } catch (VahanException ex) {
            JSFUtils.showMessage(ex.getMessage());
        }
    }

    public void deleteOfficeTiming(LoginTimingDobj dobj) {
        String msg = null;
        try {
            msg = LoginTimingImpl.deleteOfficeTiming(dobj, sessionVariables.getStateCodeSelected());
            officeTiminglist = LoginTimingImpl.getAllOfficesTiming(loginTiming.getStateName(), loginTiming.getOfficeName(), sessionVariables.getStateCodeSelected());
            JSFUtils.showMessage(msg);
        } catch (VahanException ex) {
            JSFUtils.showMessage(ex.getMessage());
        }
    }

    public void officeListner() {
        try {
            LoginTimingDobj loginTimingdobj = LoginTimingImpl.getDataFromDataTable(loginTiming.getOff_cd(), sessionVariables.getStateCodeSelected());
            if (loginTimingdobj == null) {
                getLoginTiming().setSaveButton(true);
                getLoginTiming().setStateName(this.state_name);
                getLoginTiming().setOfficeName(this.officeName);
                getLoginTiming().setState_cd(sessionVariables.getStateCodeSelected());
                getLoginTiming().setOff_cd(loginTiming.getOff_cd());
                getLoginTiming().setOpen_timing(null);
                getLoginTiming().setClose_timing(null);
                JSFUtils.showMessage("Record Not Available Please Enter Record & Save");
            } else {
                getLoginTiming().setSaveButton(false);
                getLoginTiming().setStateName(this.state_name);
                getLoginTiming().setOfficeName(this.officeName);
                getLoginTiming().setOff_cd(loginTimingdobj.getOff_cd());
                getLoginTiming().setOpen_timing(loginTimingdobj.getOpen_timing());
                getLoginTiming().setClose_timing(loginTimingdobj.getClose_timing());
            }
        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    /**
     * @return the loginTiming
     */
    public LoginTimingDobj getLoginTiming() {
        return loginTiming;
    }

    /**
     * @param loginTiming the loginTiming to set
     */
    public void setLoginTiming(LoginTimingDobj loginTiming) {
        this.loginTiming = loginTiming;
    }

    /**
     * @return the login_list
     */
    public ArrayList getLogin_list() {
        return login_list;
    }

    /**
     * @param login_list the login_list to set
     */
    public void setLogin_list(ArrayList login_list) {
        this.login_list = login_list;
    }

    /**
     * @return the loginTiminglist
     */
    public List<LoginTimingDobj> getLoginTiminglist() {
        return loginTiminglist;
    }

    /**
     * @param loginTiminglist the loginTiminglist to set
     */
    public void setLoginTiminglist(List<LoginTimingDobj> loginTiminglist) {
        this.loginTiminglist = loginTiminglist;
    }

    public List<LoginTimingDobj> getLoginTiming_FilteredList() {
        return loginTiming_FilteredList;
    }

    public void setLoginTiming_FilteredList(List<LoginTimingDobj> loginTiming_FilteredList) {
        this.loginTiming_FilteredList = loginTiming_FilteredList;
    }

    /**
     * @return the officeList
     */
    public List<SelectItem> getOfficeList() {
        return officeList;
    }

    /**
     * @param officeList the officeList to set
     */
    public void setOfficeList(List<SelectItem> officeList) {
        this.officeList = officeList;
    }

    /**
     * @return the officeTiminglist
     */
    public List<LoginTimingDobj> getOfficeTiminglist() {
        return officeTiminglist;
    }

    /**
     * @param officeTiminglist the officeTiminglist to set
     */
    public void setOfficeTiminglist(List<LoginTimingDobj> officeTiminglist) {
        this.officeTiminglist = officeTiminglist;
    }
}
