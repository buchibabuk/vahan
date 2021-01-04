/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.rest.model;

import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.form.impl.SeatAllotedDetails;
import nic.vahan.form.impl.Util;

/**
 *
 * @author Kartikey Singh
 */
public class SessionVariablesModel {

    private String empCodeLoggedIn;
    private String stateCodeSelected;
    private int offCodeSelected;
    private String userCatgForLoggedInUser;
    private int actionCodeSelected;
    private String empNameLoggedIn;
    private SeatAllotedDetails selectedWork;
    private String userIdForLoggedInUser;
    // Added on top of the original class
    private int userLoginOffCode;
    private String selectedRoleCode;
    private String clientIpAddress;

    public SessionVariablesModel() {
    }

    public SessionVariablesModel(SessionVariables sessionVariables) {
        this.empCodeLoggedIn = sessionVariables.getEmpCodeLoggedIn();
        this.stateCodeSelected = sessionVariables.getStateCodeSelected();
        this.offCodeSelected = sessionVariables.getOffCodeSelected();
        this.userCatgForLoggedInUser = sessionVariables.getUserCatgForLoggedInUser();
        this.actionCodeSelected = sessionVariables.getActionCodeSelected();
        this.empNameLoggedIn = sessionVariables.getEmpNameLoggedIn();
        this.selectedWork = sessionVariables.getSelectedWork();
        this.userIdForLoggedInUser = sessionVariables.getUserIdForLoggedInUser();
        // Fetch details from Util class
        this.userLoginOffCode = Util.getUserLoginOffCode();
        this.selectedRoleCode = (String) Util.getSession().getAttribute("selected_role_cd");
        this.clientIpAddress = Util.getClientIpAdress();
    }

    @Override
    public String toString() {
        return "SessionVariablesModel{" + "empCodeLoggedIn=" + empCodeLoggedIn + ", stateCodeSelected=" + stateCodeSelected + ", offCodeSelected=" + offCodeSelected + ", userCatgForLoggedInUser=" + userCatgForLoggedInUser + ", actionCodeSelected=" + actionCodeSelected + ", empNameLoggedIn=" + empNameLoggedIn + ", selectedWork=" + selectedWork + ", userIdForLoggedInUser=" + userIdForLoggedInUser + ", userLoginOffCode=" + userLoginOffCode + ", selectedRoleCode=" + selectedRoleCode + ", clientIpAddress=" + clientIpAddress + '}';
    }

    public String getEmpCodeLoggedIn() {
        return empCodeLoggedIn;
    }

    public String getStateCodeSelected() {
        return stateCodeSelected;
    }

    public int getOffCodeSelected() {
        return offCodeSelected;
    }

    public String getUserCatgForLoggedInUser() {
        return userCatgForLoggedInUser;
    }

    public int getActionCodeSelected() {
        return actionCodeSelected;
    }

    public String getEmpNameLoggedIn() {
        return empNameLoggedIn;
    }

    public SeatAllotedDetails getSelectedWork() {
        return selectedWork;
    }

    public String getUserIdForLoggedInUser() {
        return userIdForLoggedInUser;
    }

    public int getUserLoginOffCode() {
        return userLoginOffCode;
    }

    public String getSelectedRoleCode() {
        return selectedRoleCode;
    }

    public String getClientIpAddress() {
        return clientIpAddress;
    }
}
