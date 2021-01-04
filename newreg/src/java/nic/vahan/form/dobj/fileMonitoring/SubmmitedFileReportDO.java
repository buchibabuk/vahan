/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.fileMonitoring;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Deepak
 */
public class SubmmitedFileReportDO implements Serializable {

    private int srNo;
    private String registrationDate;
    private String regnDate;
    private String submmitDate;
    private String fileNo;
    private List<SubmmitedFileReportDO> filelist = new ArrayList();
    private boolean enableDataTbl = false;
    private Date today = new Date();
    private int pendingDays;
    private String appl_no;
    private int off_cd;
    private String state_Name;
    private String status;
    private String statusDesc;
    private HashMap rtolist = new HashMap();
    private HashMap dearlist = new HashMap();
    private String dealerName;
    private boolean renderTbl = false;
    private boolean enter;
    private String serviceID;
    private boolean enableCalender = false;
    private boolean enablePrint = false;
    private Date currentDate = new Date();
    private Date toDate;
    private Date fromdate;

    public int getSrNo() {
        return srNo;
    }

    public void setSrNo(int srNo) {
        this.srNo = srNo;
    }

    public String getFileNo() {
        return fileNo;
    }

    public void setFileNo(String fileNo) {
        this.fileNo = fileNo;
    }

    public List<SubmmitedFileReportDO> getFilelist() {
        return filelist;
    }

    public void setFilelist(List<SubmmitedFileReportDO> filelist) {
        this.filelist = filelist;
    }

    public boolean isEnableDataTbl() {
        return enableDataTbl;
    }

    public void setEnableDataTbl(boolean enableDataTbl) {
        this.enableDataTbl = enableDataTbl;
    }

    public Date getToday() {
        return today;
    }

    public void setToday(Date today) {
        this.today = today;
    }

    public int getPendingDays() {
        return pendingDays;
    }

    public void setPendingDays(int pendingDays) {
        this.pendingDays = pendingDays;
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public HashMap getRtolist() {
        return rtolist;
    }

    public void setRtolist(HashMap rtolist) {
        this.rtolist = rtolist;
    }

    public HashMap getDearlist() {
        return dearlist;
    }

    public void setDearlist(HashMap dearlist) {
        this.dearlist = dearlist;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public boolean isRenderTbl() {
        return renderTbl;
    }

    public void setRenderTbl(boolean renderTbl) {
        this.renderTbl = renderTbl;
    }

    public boolean isEnter() {
        return enter;
    }

    public void setEnter(boolean enter) {
        this.enter = enter;
    }

    public String getServiceID() {
        return serviceID;
    }

    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getState_Name() {
        return state_Name;
    }

    public void setState_Name(String state_Name) {
        this.state_Name = state_Name;
    }

    public String getRegnDate() {
        return regnDate;
    }

    public void setRegnDate(String regnDate) {
        this.regnDate = regnDate;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public int getOff_cd() {
        return off_cd;
    }

    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }

    public String getSubmmitDate() {
        return submmitDate;
    }

    public void setSubmmitDate(String submmitDate) {
        this.submmitDate = submmitDate;
    }

    public boolean isEnableCalender() {
        return enableCalender;
    }

    public void setEnableCalender(boolean enableCalender) {
        this.enableCalender = enableCalender;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Date getFromdate() {
        return fromdate;
    }

    public void setFromdate(Date fromdate) {
        this.fromdate = fromdate;
    }

    public boolean isEnablePrint() {
        return enablePrint;
    }

    public void setEnablePrint(boolean enablePrint) {
        this.enablePrint = enablePrint;
    }
}
