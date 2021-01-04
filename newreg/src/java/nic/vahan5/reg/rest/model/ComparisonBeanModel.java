/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.rest.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.dobj.TmConfigurationDobj;

/**
 *
 * @author Kartikey Singh
 */
public class ComparisonBeanModel {

    private String fields;
    private String old_value;
    private String new_value;
    private String changed_data;
    private String op_dt;
    private int user;
    private ArrayList<ComparisonBeanModel> compBeanList = new ArrayList<ComparisonBeanModel>();
    private List<ComparisonBeanModel> prevChangedDataList;
    private ArrayList<ComparisonBeanModel> compareChagnesList;
    private String APPL_NO;
    private String REGN_NO;
    private String PUR_CD;
    private int ROLE_CD;
    private int ACTION_CODE;
    private long Emp_Cd;
    private String userName;
    private String COUNTER_ID;
    private boolean printDisclaimerButton = false;
    private boolean okButton = false;
    private List<Map.Entry<String, String>> entryDetails;
    private String ownerMobileVerifyOtp = null;
    private String enteredOwnerMobVerifyOtp = null;
    TmConfigurationDobj tmConfDobj = null;
    String mobileNoCountMessage = null;
    /*
     * Adding this here as we need to return it to save_ActionListerner()
     */
    String seriesAvailMess = "";
    VehicleParameters vehicleParameters = null;

    public ComparisonBeanModel() {
    }

    public ComparisonBeanModel(String fields, String old_value, String new_value, String changed_data, String op_dt, int user, List<ComparisonBeanModel> prevChangedDataList, ArrayList<ComparisonBeanModel> compareChagnesList, String APPL_NO, String REGN_NO, String PUR_CD, int ROLE_CD, int ACTION_CODE, long Emp_Cd, String userName, String COUNTER_ID, List<Map.Entry<String, String>> entryDetails) {
        this.fields = fields;
        this.old_value = old_value;
        this.new_value = new_value;
        this.changed_data = changed_data;
        this.op_dt = op_dt;
        this.user = user;
        this.prevChangedDataList = prevChangedDataList;
        this.compareChagnesList = compareChagnesList;
        this.APPL_NO = APPL_NO;
        this.REGN_NO = REGN_NO;
        this.PUR_CD = PUR_CD;
        this.ROLE_CD = ROLE_CD;
        this.ACTION_CODE = ACTION_CODE;
        this.Emp_Cd = Emp_Cd;
        this.userName = userName;
        this.COUNTER_ID = COUNTER_ID;
        this.entryDetails = entryDetails;
    }
    
    public ComparisonBeanModel(ComparisonBean comparisonBean) {
        this.fields = comparisonBean.getFields();
        this.old_value = comparisonBean.getOld_value();
        this.new_value = comparisonBean.getNew_value();
        this.changed_data = comparisonBean.getChanged_data();
        this.op_dt = comparisonBean.getOp_dt();
        this.user = comparisonBean.getUser();
        this.APPL_NO = comparisonBean.getAPPL_NO();
        this.REGN_NO = comparisonBean.getREGN_NO();
        this.PUR_CD = comparisonBean.getPUR_CD();
        this.ROLE_CD = comparisonBean.getROLE_CD();
        this.ACTION_CODE = comparisonBean.getACTION_CODE();
        this.Emp_Cd = comparisonBean.getEmp_Cd();
        this.userName = comparisonBean.getUserName();
        this.COUNTER_ID = comparisonBean.getCOUNTER_ID();
        this.entryDetails = comparisonBean.getEntryDetails();
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getOld_value() {
        return old_value;
    }

    public void setOld_value(String old_value) {
        this.old_value = old_value;
    }

    public String getNew_value() {
        return new_value;
    }

    public void setNew_value(String new_value) {
        this.new_value = new_value;
    }

    public String getChanged_data() {
        return changed_data;
    }

    public void setChanged_data(String changed_data) {
        this.changed_data = changed_data;
    }

    public String getOp_dt() {
        return op_dt;
    }

    public void setOp_dt(String op_dt) {
        this.op_dt = op_dt;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public ArrayList<ComparisonBeanModel> getCompBeanList() {
        return compBeanList;
    }

    public void setCompBeanList(ArrayList<ComparisonBeanModel> compBeanList) {
        this.compBeanList = compBeanList;
    }

    public List<ComparisonBeanModel> getPrevChangedDataList() {
        return prevChangedDataList;
    }

    public void setPrevChangedDataList(List<ComparisonBeanModel> prevChangedDataList) {
        this.prevChangedDataList = prevChangedDataList;
    }

    public ArrayList<ComparisonBeanModel> getCompareChagnesList() {
        return compareChagnesList;
    }

    public void setCompareChagnesList(ArrayList<ComparisonBeanModel> compareChagnesList) {
        this.compareChagnesList = compareChagnesList;
    }

    public String getAPPL_NO() {
        return APPL_NO;
    }

    public void setAPPL_NO(String APPL_NO) {
        this.APPL_NO = APPL_NO;
    }

    public String getREGN_NO() {
        return REGN_NO;
    }

    public void setREGN_NO(String REGN_NO) {
        this.REGN_NO = REGN_NO;
    }

    public String getPUR_CD() {
        return PUR_CD;
    }

    public void setPUR_CD(String PUR_CD) {
        this.PUR_CD = PUR_CD;
    }

    public int getROLE_CD() {
        return ROLE_CD;
    }

    public void setROLE_CD(int ROLE_CD) {
        this.ROLE_CD = ROLE_CD;
    }

    public int getACTION_CODE() {
        return ACTION_CODE;
    }

    public void setACTION_CODE(int ACTION_CODE) {
        this.ACTION_CODE = ACTION_CODE;
    }

    public long getEmp_Cd() {
        return Emp_Cd;
    }

    public void setEmp_Cd(long Emp_Cd) {
        this.Emp_Cd = Emp_Cd;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCOUNTER_ID() {
        return COUNTER_ID;
    }

    public void setCOUNTER_ID(String COUNTER_ID) {
        this.COUNTER_ID = COUNTER_ID;
    }

    public boolean isPrintDisclaimerButton() {
        return printDisclaimerButton;
    }

    public void setPrintDisclaimerButton(boolean printDisclaimerButton) {
        this.printDisclaimerButton = printDisclaimerButton;
    }

    public boolean isOkButton() {
        return okButton;
    }

    public void setOkButton(boolean okButton) {
        this.okButton = okButton;
    }

    public List<Map.Entry<String, String>> getEntryDetails() {
        return entryDetails;
    }

    public void setEntryDetails(List<Map.Entry<String, String>> entryDetails) {
        this.entryDetails = entryDetails;
    }

    public String getOwnerMobileVerifyOtp() {
        return ownerMobileVerifyOtp;
    }

    public void setOwnerMobileVerifyOtp(String ownerMobileVerifyOtp) {
        this.ownerMobileVerifyOtp = ownerMobileVerifyOtp;
    }

    public String getEnteredOwnerMobVerifyOtp() {
        return enteredOwnerMobVerifyOtp;
    }

    public void setEnteredOwnerMobVerifyOtp(String enteredOwnerMobVerifyOtp) {
        this.enteredOwnerMobVerifyOtp = enteredOwnerMobVerifyOtp;
    }

    public TmConfigurationDobj getTmConfDobj() {
        return tmConfDobj;
    }

    public void setTmConfDobj(TmConfigurationDobj tmConfDobj) {
        this.tmConfDobj = tmConfDobj;
    }

    public String getMobileNoCountMessage() {
        return mobileNoCountMessage;
    }

    public void setMobileNoCountMessage(String mobileNoCountMessage) {
        this.mobileNoCountMessage = mobileNoCountMessage;
    }

    public String getSeriesAvailMess() {
        return seriesAvailMess;
    }

    public void setSeriesAvailMess(String seriesAvailMess) {
        this.seriesAvailMess = seriesAvailMess;
    }

    public VehicleParameters getVehicleParameters() {
        return vehicleParameters;
    }

    public void setVehicleParameters(VehicleParameters vehicleParameters) {
        this.vehicleParameters = vehicleParameters;
    }
}
