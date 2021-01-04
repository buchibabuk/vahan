/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.user_mgmt.dobj.UserTransferDobj;
import nic.vahan.db.user_mgmt.impl.UserTransferImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC103
 */
@ManagedBean
@ViewScoped
public class UserTransferBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(UserTransferBean.class);
    private List<UserTransferDobj> userDetailList;
    private List<UserTransferDobj> userTransferList;
    private UserTransferDobj dobj = new UserTransferDobj();
    UserTransferImpl implObj = new UserTransferImpl();
    private List filterList = null;
    private List officeList = new ArrayList();

    public UserTransferBean() {
        officeList = MasterTableFiller.getOfficeList(Util.getUserStateCode());
        fillDataTable();
        fillTransferDataTable();
    }

    private void fillDataTable() {
//        reset();
        try {
            getDobj().setStateCode(Util.getUserStateCode());
            getDobj().setOffCode(Util.getUserSeatOffCode());
            getDobj().setUserCode(Long.parseLong(Util.getEmpCode()));
            userDetailList = implObj.fillDataTable(dobj);
        } catch (VahanException e) {
            LOGGER.error(e.getMessage());
            JSFUtils.setFacesMessage("Technical Error.", null, JSFUtils.ERROR);
        }
    }

    private void fillTransferDataTable() {
        try {
            userTransferList = implObj.fillTransferDataTable(dobj);
        } catch (VahanException e) {
            LOGGER.error(e.getMessage());
            JSFUtils.setFacesMessage("Technical Error.", null, JSFUtils.ERROR);
        }
    }

    public void updateListner(long userCode) {
        try {
            dobj = implObj.getUserDetails(userCode);
        } catch (VahanException e) {
            JSFUtils.setFacesMessage(e.getMessage(), null, JSFUtils.ERROR);
        }
    }

    public void transferUser() {
        try {
            if (ServerUtil.getVahan4StartDate(dobj.getStateCode(), dobj.getOffCode()) == null && dobj.getOffCode() != 0) {
                throw new VahanException("Vahan4 is not Started in this Office. You can't transfer user to this Selected Office.");
            }


            boolean flag = implObj.checkBlockUser(dobj);
            if (flag) {
                throw new VahanException("User is Blocked. You can't Transfer Blocked User.");
            }
            if (dobj == null || Util.getEmpCode() == null || Util.getEmpCode().equalsIgnoreCase(String.valueOf(dobj.getUserCode()))) {
                throw new VahanException("Please select the user to be transfered.");
            }
            boolean transferFlag = implObj.transferUser(dobj);
            if (transferFlag) {
                reset();
                fillDataTable();
                JSFUtils.setFacesMessage("User Transfered Successfully.", null, JSFUtils.INFO);
            }
        } catch (VahanException vme) {
            JSFUtils.setFacesMessage(vme.getMessage(), null, JSFUtils.WARN);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.setFacesMessage("Error In Saving User Data.", null, JSFUtils.ERROR);
        }
    }

    /**
     * @return the dobj
     */
    public UserTransferDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(UserTransferDobj dobj) {
        this.dobj = dobj;
    }

    /**
     * @return the filterList
     */
    public List getFilterList() {
        return filterList;
    }

    /**
     * @param filterList the filterList to set
     */
    public void setFilterList(List filterList) {
        this.filterList = filterList;
    }

    /**
     * @return the officeList
     */
    public List getOfficeList() {
        return officeList;
    }

    /**
     * @param officeList the officeList to set
     */
    public void setOfficeList(List officeList) {
        this.officeList = officeList;
    }

    public void reset() {
        dobj = new UserTransferDobj();
    }

    /**
     * @return the userDetailList
     */
    public List<UserTransferDobj> getUserDetailList() {
        return userDetailList;
    }

    /**
     * @param userDetailList the userDetailList to set
     */
    public void setUserDetailList(List<UserTransferDobj> userDetailList) {
        this.userDetailList = userDetailList;
    }

    /**
     * @return the userTransferList
     */
    public List<UserTransferDobj> getUserTransferList() {
        return userTransferList;
    }

    /**
     * @param userTransferList the userTransferList to set
     */
    public void setUserTransferList(List<UserTransferDobj> userTransferList) {
        this.userTransferList = userTransferList;
    }
}
