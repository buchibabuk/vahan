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
import nic.vahan.form.dobj.RestrictUserForLoginDobj;
import nic.vahan.form.impl.RestrictUserForLoginImpl;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author DELL
 */
@ManagedBean(name = "restrictuserbean")
@ViewScoped
public class RestrictUserForLoginBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(LoginTimingBean.class);
    private RestrictUserForLoginDobj restrictUser = new RestrictUserForLoginDobj();
    private SessionVariables sessionVariables = null;
    private String state_name;
    private String user_catg;
    private List<SelectItem> officeList = null;
    private List restrictUserList = new ArrayList();
    private List userCatgList = new ArrayList();
    RestrictUserForLoginImpl impl = new RestrictUserForLoginImpl();

    public RestrictUserForLoginBean() {
        String[][] data = null;
        try {
            sessionVariables = new SessionVariables();
            if (sessionVariables == null
                    || sessionVariables.getStateCodeSelected() == null
                    || sessionVariables.getEmpCodeLoggedIn() == null) {
                throw new VahanException(TableConstants.SomthingWentWrong);
            } else {
                state_name = MasterTableFiller.state.get(sessionVariables.getStateCodeSelected()).getStateDescr();
                user_catg = sessionVariables.getUserCatgForLoggedInUser();
                restrictUser.setStateName(this.state_name);
                restrictUserList = impl.getRestrictUserCatgDetails(Util.getUserStateCode());
                data = MasterTableFiller.masterTables.TM_USER_CATG.getData();
                for (int i = 0; i < data.length; i++) {
                    if (!data[i][0].equalsIgnoreCase(TableConstants.USER_CATG_STATE_ADMIN)
                            && !data[i][0].equalsIgnoreCase(TableConstants.USER_CATG_PORTAL_ADMIN)
                            && !data[i][0].equalsIgnoreCase(TableConstants.USER_CATG_STATE_SUPER_ADMIN)) {
                        userCatgList.add(new SelectItem(data[i][0], data[i][1]));
                    }
                }
            }
        } catch (VahanException vex) {
            JSFUtils.showMessage(vex.getMessage());
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void save() {
        String msg = null;
        try {
            if (TableConstants.USER_CATG_STATE_ADMIN.equals(user_catg)) {
                boolean flag = RestrictUserForLoginImpl.updateRestrictUsercatg(Util.getUserStateCode(), getRestrictUserList());
                if (flag) {
                    msg = "User Category Restricted Successfully !!!";
                } else {
                    msg = "Technical Problem  !!!";
                }
            }
            JSFUtils.setFacesMessage(msg, null, JSFUtils.INFO);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage("Error in Data Saving.");
        }
    }

    /**
     * @return the restrictUser
     */
    public RestrictUserForLoginDobj getRestrictUser() {
        return restrictUser;
    }

    /**
     * @param restrictUser the restrictUser to set
     */
    public void setRestrictUser(RestrictUserForLoginDobj restrictUser) {
        this.restrictUser = restrictUser;
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
     * @return the userCatgList
     */
    public List getUserCatgList() {
        return userCatgList;
    }

    /**
     * @param userCatgList the userCatgList to set
     */
    public void setUserCatgList(List userCatgList) {
        this.userCatgList = userCatgList;
    }

    /**
     * @return the restrictUserList
     */
    public List getRestrictUserList() {
        return restrictUserList;
    }

    /**
     * @param restrictUserList the restrictUserList to set
     */
    public void setRestrictUserList(List restrictUserList) {
        this.restrictUserList = restrictUserList;
    }
}
