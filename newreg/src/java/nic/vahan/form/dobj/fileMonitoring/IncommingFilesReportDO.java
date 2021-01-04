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
public class IncommingFilesReportDO implements Serializable {

    private String serialNo;
    private String serviceName;
    private int srNo;
    private String registrationDate;
    private String regnDate;
    private String receiveDate;
    private String fileNo;
    private List<IncommingFilesReportDO> filelist = new ArrayList();
    private List<IncommingFilesReportDO> filterfilelist;
    private boolean enableDataTbl = false;
    private Date today = new Date();
    private int pendingDays;
    private String appl_no;
    private int off_cd;
    private String state_Name;
    private String status;
    private HashMap rtolist = new HashMap();
    private HashMap dearlist = new HashMap();
    private String dealerName;
    private boolean renderTbl = false;
    private boolean enter;
    private int serviceID;
    private List<IncommingFilesReportDO> moduleServiceDataList = new ArrayList<IncommingFilesReportDO>();
    private List<IncommingFilesReportDO> moduleServiceDataList1 = new ArrayList<IncommingFilesReportDO>();
    private List<IncommingFilesReportDO> availableDocuments = new ArrayList();
    private String file;
    private ArrayList fileList = new ArrayList();
    private boolean renderReason = false;
    private String reason;

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

    public List<IncommingFilesReportDO> getFilelist() {
        return filelist;
    }

    public void setFilelist(List<IncommingFilesReportDO> filelist) {
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

    public List<IncommingFilesReportDO> getModuleServiceDataList() {
        return moduleServiceDataList;
    }

    public void setModuleServiceDataList(List<IncommingFilesReportDO> moduleServiceDataList) {
        this.moduleServiceDataList = moduleServiceDataList;
    }

    public List<IncommingFilesReportDO> getModuleServiceDataList1() {
        return moduleServiceDataList1;
    }

    public void setModuleServiceDataList1(List<IncommingFilesReportDO> moduleServiceDataList1) {
        this.moduleServiceDataList1 = moduleServiceDataList1;
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

    public List<IncommingFilesReportDO> getAvailableDocuments() {
        return availableDocuments;
    }

    public void setAvailableDocuments(List<IncommingFilesReportDO> availableDocuments) {
        this.availableDocuments = availableDocuments;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public ArrayList getFileList() {
        return fileList;
    }

    public void setFileList(ArrayList fileList) {
        this.fileList = fileList;
    }

    public boolean isRenderReason() {
        return renderReason;
    }

    public void setRenderReason(boolean renderReason) {
        this.renderReason = renderReason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getOff_cd() {
        return off_cd;
    }

    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }

    public String getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(String receiveDate) {
        this.receiveDate = receiveDate;
    }

    public int getServiceID() {
        return serviceID;
    }

    public void setServiceID(int serviceID) {
        this.serviceID = serviceID;
    }

    public List<IncommingFilesReportDO> getFilterfilelist() {
        return filterfilelist;
    }

    public void setFilterfilelist(List<IncommingFilesReportDO> filterfilelist) {
        this.filterfilelist = filterfilelist;
    }
}
