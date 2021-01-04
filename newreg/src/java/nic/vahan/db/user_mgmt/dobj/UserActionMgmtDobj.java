/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.dobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;

/**
 *
 * @author tranC102
 */
public class UserActionMgmtDobj implements Serializable {

    private String msg = "";
    private String userName = "";
    private String pwd = "";
    private String stateCode = "";
    private String state_cd = "";
    private List selectedOfficeCode;
    private String enteredBy = "";
    //for Employee
    private String selectedEmp = "";
    private List empList = new ArrayList();
    //for Purpose
    private String selectedPurpose = "";
    private List pList = new ArrayList();
    private List offList = new ArrayList();
    //for Action
    private DualListModel<String> action;
    private List<String> actionSource = new ArrayList<String>();
    private List<String> actionTarget = new ArrayList<String>();
    private String strTarget = "";
    private String user_categ;
    //for tree
    private TreeNode root;

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the pwd
     */
    public String getPwd() {
        return pwd;
    }

    /**
     * @param pwd the pwd to set
     */
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    /**
     * @return the selectedEmp
     */
    public String getSelectedEmp() {
        return selectedEmp;
    }

    /**
     * @param selectedEmp the selectedEmp to set
     */
    public void setSelectedEmp(String selectedEmp) {
        this.selectedEmp = selectedEmp;
    }

    /**
     * @return the empList
     */
    public List getEmpList() {
        return empList;
    }

    /**
     * @param empList the empList to set
     */
    public void setEmpList(List empList) {
        this.empList = empList;
    }

    /**
     * @return the selectedPurpose
     */
    public String getSelectedPurpose() {
        return selectedPurpose;
    }

    /**
     * @param selectedPurpose the selectedPurpose to set
     */
    public void setSelectedPurpose(String selectedPurpose) {
        this.selectedPurpose = selectedPurpose;
    }

    /**
     * @return the pList
     */
    public List getpList() {
        return pList;
    }

    /**
     * @param pList the pList to set
     */
    public void setpList(List pList) {
        this.pList = pList;
    }

    /**
     * @return the action
     */
    public DualListModel<String> getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(DualListModel<String> action) {
        this.action = action;
    }

    /**
     * @return the actionSource
     */
    public List<String> getActionSource() {
        return actionSource;
    }

    /**
     * @param actionSource the actionSource to set
     */
    public void setActionSource(List<String> actionSource) {
        this.actionSource = actionSource;
    }

    /**
     * @return the actionTarget
     */
    public List<String> getActionTarget() {
        return actionTarget;
    }

    /**
     * @param actionTarget the actionTarget to set
     */
    public void setActionTarget(List<String> actionTarget) {
        this.actionTarget = actionTarget;
    }

    /**
     * @return the strTarget
     */
    public String getStrTarget() {
        return strTarget;
    }

    /**
     * @param strTarget the strTarget to set
     */
    public void setStrTarget(String strTarget) {
        this.strTarget = strTarget;
    }

    /**
     * @return the root
     */
    public TreeNode getRoot() {
        return root;
    }

    /**
     * @param root the root to set
     */
    public void setRoot(TreeNode root) {
        this.root = root;
    }

    /**
     * @return the stateCode
     */
    public String getStateCode() {
        return stateCode;
    }

    /**
     * @param stateCode the stateCode to set
     */
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    /**
     * @return the officeCode
     */
    public List getSelectedOfficeCode() {
        return selectedOfficeCode;
    }

    /**
     * @param officeCode the officeCode to set
     */
    public void setSelectedOfficeCode(List officeCode) {
        this.selectedOfficeCode = officeCode;
    }

    /**
     * @return the enteredBy
     */
    public String getEnteredBy() {
        return enteredBy;
    }

    /**
     * @param enteredBy the enteredBy to set
     */
    public void setEnteredBy(String enteredBy) {
        this.enteredBy = enteredBy;
    }

    /**
     * @return the offList
     */
    public List getOffList() {
        return offList;
    }

    /**
     * @param offList the offList to set
     */
    public void setOffList(List offList) {
        this.offList = offList;
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

    public String getUser_categ() {
        return user_categ;
    }

    public void setUser_categ(String user_categ) {
        this.user_categ = user_categ;
    }
}
