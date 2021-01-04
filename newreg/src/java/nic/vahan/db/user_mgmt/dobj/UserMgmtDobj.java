/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.dobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import org.primefaces.model.TreeNode;

/**
 *
 * @author tranC102
 */
public class UserMgmtDobj implements Serializable {

    private String state_cd = "";
    private int office_cd;
    private long user_cd;
    private String user_name = "";
    private String desig_cd = "";
    private String user_id = "";
    private String user_pwd = "";
    private String off_phone = "";
    private long mobile_no;
    private String email = "";
    private String user_catg = "";
    private String selectedUserCatg = "";
    private String status = "";
    private boolean newEmp = true;
    private boolean newUniqueEmp = false;
    private List stateList = new ArrayList();
    private List officeList = new ArrayList();
    private List desigList = new ArrayList();
    private TreeMap<Long, String> dtMap = new TreeMap<>();
    private long created_by;
    private TreeNode root;
    private boolean renderPermPanel = false;
    private String assignedOffice = "";
    private Long aadharNo;
    private String newuser_change_password = "";
    private String sha_pwd = "";

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
     * @return the office_cd
     */
    public int getOffice_cd() {
        return office_cd;
    }

    /**
     * @param office_cd the office_cd to set
     */
    public void setOffice_cd(int office_cd) {
        this.office_cd = office_cd;
    }

    /**
     * @return the user_cd
     */
    public long getUser_cd() {
        return user_cd;
    }

    /**
     * @param user_cd the user_cd to set
     */
    public void setUser_cd(long user_cd) {
        this.user_cd = user_cd;
    }

    /**
     * @return the user_name
     */
    public String getUser_name() {
        return user_name;
    }

    /**
     * @param user_name the user_name to set
     */
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    /**
     * @return the desig_cd
     */
    public String getDesig_cd() {
        return desig_cd;
    }

    /**
     * @param desig_cd the desig_cd to set
     */
    public void setDesig_cd(String desig_cd) {
        this.desig_cd = desig_cd;
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
     * @return the user_pwd
     */
    public String getUser_pwd() {
        return user_pwd;
    }

    /**
     * @param user_pwd the user_pwd to set
     */
    public void setUser_pwd(String user_pwd) {
        this.user_pwd = user_pwd;
    }

    /**
     * @return the off_phone
     */
    public String getOff_phone() {
        return off_phone;
    }

    /**
     * @param off_phone the off_phone to set
     */
    public void setOff_phone(String off_phone) {
        this.off_phone = off_phone;
    }

    /**
     * @return the mobile_no
     */
    public long getMobile_no() {
        return mobile_no;
    }

    /**
     * @param mobile_no the mobile_no to set
     */
    public void setMobile_no(long mobile_no) {
        this.mobile_no = mobile_no;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
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
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the created_by
     */
    public long getCreated_by() {
        return created_by;
    }

    /**
     * @param created_by the created_by to set
     */
    public void setCreated_by(long created_by) {
        this.created_by = created_by;
    }

    /**
     * @return the stateList
     */
    public List getStateList() {
        return stateList;
    }

    /**
     * @param stateList the stateList to set
     */
    public void setStateList(List stateList) {
        this.stateList = stateList;
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
     * @return the desigList
     */
    public List getDesigList() {
        return desigList;
    }

    /**
     * @param desigList the desigList to set
     */
    public void setDesigList(List desigList) {
        this.desigList = desigList;
    }

    /**
     * @return the newEmp
     */
    public boolean isNewEmp() {
        return newEmp;
    }

    /**
     * @param newEmp the newEmp to set
     */
    public void setNewEmp(boolean newEmp) {
        this.newEmp = newEmp;
    }

    /**
     * @return the dtMap
     */
    public TreeMap<Long, String> getDtMap() {
        return dtMap;
    }

    /**
     * @param dtMap the dtMap to set
     */
    public void setDtMap(TreeMap<Long, String> dtMap) {
        this.dtMap = dtMap;
    }

    /**
     * @return the newUniqueEmp
     */
    public boolean isNewUniqueEmp() {
        return newUniqueEmp;
    }

    /**
     * @param newUniqueEmp the newUniqueEmp to set
     */
    public void setNewUniqueEmp(boolean newUniqueEmp) {
        this.newUniqueEmp = newUniqueEmp;
    }

    /**
     * @return the selectedUserCatg
     */
    public String getSelectedUserCatg() {
        return selectedUserCatg;
    }

    /**
     * @param selectedUserCatg the selectedUserCatg to set
     */
    public void setSelectedUserCatg(String selectedUserCatg) {
        this.selectedUserCatg = selectedUserCatg;
    }

    public boolean isRenderPermPanel() {
        return renderPermPanel;
    }

    public void setRenderPermPanel(boolean renderPermPanel) {
        this.renderPermPanel = renderPermPanel;
    }

    public Long getAadharNo() {
        return aadharNo;
    }

    public void setAadharNo(Long aadharNo) {
        this.aadharNo = aadharNo;
    }

    public String getAssignedOffice() {
        return assignedOffice;
    }

    public void setAssignedOffice(String assignedOffice) {
        this.assignedOffice = assignedOffice;
    }

    /**
     * @return the newuser_change_password
     */
    public String getNewuser_change_password() {
        return newuser_change_password;
    }

    /**
     * @param newuser_change_password the newuser_change_password to set
     */
    public void setNewuser_change_password(String newuser_change_password) {
        this.newuser_change_password = newuser_change_password;
    }

    /**
     * @return the sha_pwd
     */
    public String getSha_pwd() {
        return sha_pwd;
    }

    /**
     * @param sha_pwd the sha_pwd to set
     */
    public void setSha_pwd(String sha_pwd) {
        this.sha_pwd = sha_pwd;
    }
}
