/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.form.dobj.UserBlockUnblockdobj;
import nic.vahan.form.impl.UserBlockUnblockImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;

/**
 *
 * @author acer
 */
@ManagedBean(name = "userBlckUnblckBean")
@ViewScoped
public class UserBlockUnblockbean implements Serializable {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(UserBlockUnblockImpl.class);
    private List<UserBlockUnblockdobj> userBlkedList;
    private List<UserBlockUnblockdobj> userUnBlkedList;
    private String successBlockUnBlockMessg = "";
    private String state_cd;
    private int offCd;
    private boolean btnpanel = false;
    private String user_catg;
    private String emp_cd;
    private String dealer_cd;

    public UserBlockUnblockbean() {
        try {
            if (Util.getUserStateCode() != null && Util.getUserOffCode() != null
                    && Util.getUserCategory() != null) {
                state_cd = Util.getUserStateCode();
                offCd = Util.getUserOffCode();
                user_catg = Util.getUserCategory();
                emp_cd = Util.getEmpCode();
                dealer_cd = ServerUtil.getDealerCode(Long.parseLong(emp_cd), state_cd, offCd);

            }
            if (state_cd != null) {
                userUnBlkedList = UserBlockUnblockImpl.getUserUnBlockedList(state_cd, offCd, user_catg, dealer_cd);
                userBlkedList = UserBlockUnblockImpl.getUserBlockedList(state_cd, offCd, user_catg, dealer_cd);
            } else {
                JSFUtils.showMessage("Please reload this form");
            }

        } catch (Exception e) {

            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage("Some Error occurred while fetching the Record");
        }
    }

    public void blkUserDtls() {
        try {
            int countChecked = 0;
            int successBlocked = 0;
            for (int i = 0; i < userUnBlkedList.size(); i++) {
                if (userUnBlkedList.get(i).isBlockUnBlockStatus()) {
                    countChecked++;
                    break;
                }
            }
            if (countChecked > 0) {
                UserBlockUnblockImpl userBlkImpl = new UserBlockUnblockImpl();
                successBlocked = userBlkImpl.blockUser(userUnBlkedList);

            } else {
                throw new VahanException("Please Select atleast one User to Block");
            }

            if (successBlocked > 0) {
                userUnBlkedList = UserBlockUnblockImpl.getUserUnBlockedList(state_cd, offCd, user_catg, dealer_cd);
                userBlkedList = UserBlockUnblockImpl.getUserBlockedList(state_cd, offCd, user_catg, dealer_cd);
                successBlockUnBlockMessg = "Selected Users are Successfully Blocked.";
                JSFUtils.showMessagesInDialog("Info!", "Selected Users are Successfully Blocked!", FacesMessage.SEVERITY_INFO);

            }
        } catch (VahanException vmex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vmex.getMessage(), vmex.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage("Some Error occurred while fetching the Record");
        }
    }

    public void renderbtn() {
        setBtnpanel(true);
    }

    public void unBlkUserDetails() {
        try {
            int countChecked = 0;
            int successUnBlocked = 0;
            for (int i = 0; i < userBlkedList.size(); i++) {
                if (userBlkedList.get(i).isBlockUnBlockStatus()) {
                    countChecked++;
                    break;
                }
            }
            if (countChecked > 0) {
                UserBlockUnblockImpl userBlkImpl = new UserBlockUnblockImpl();
                successUnBlocked = userBlkImpl.unBlockUser(userBlkedList);

            } else {
                throw new VahanException("Please Select atleast one User to UnBlock");
            }
            if (successUnBlocked > 0) {
                userBlkedList = UserBlockUnblockImpl.getUserBlockedList(state_cd, offCd, user_catg, dealer_cd);
                userUnBlkedList = UserBlockUnblockImpl.getUserUnBlockedList(state_cd, offCd, user_catg, dealer_cd);
                successBlockUnBlockMessg = "Selected Users are Successfully UnBlocked.";
                JSFUtils.showMessagesInDialog("Info!", "Selected Users are Successfully UnBlocked!", FacesMessage.SEVERITY_INFO);

            }
        } catch (VahanException vmex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vmex.getMessage(), vmex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            JSFUtils.showMessage("Some Error occurred while fetching the Record");
        }
    }

    public List<UserBlockUnblockdobj> getUserBlkedList() {
        return userBlkedList;
    }

    public void setUserBlkedList(List<UserBlockUnblockdobj> userBlkedList) {
        this.userBlkedList = userBlkedList;
    }

    public List<UserBlockUnblockdobj> getUserUnBlkedList() {
        return userUnBlkedList;
    }

    public void setUserUnBlkedList(List<UserBlockUnblockdobj> userUnBlkedList) {
        this.userUnBlkedList = userUnBlkedList;
    }

    public String getSuccessBlockUnBlockMessg() {
        return successBlockUnBlockMessg;
    }

    public void setSuccessBlockUnBlockMessg(String successBlockUnBlockMessg) {
        this.successBlockUnBlockMessg = successBlockUnBlockMessg;
    }

    public boolean isBtnpanel() {
        return btnpanel;
    }

    public void setBtnpanel(boolean btnpanel) {
        this.btnpanel = btnpanel;
    }
}
