/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author afzal
 */
public class DownloadDispatchDobj implements Serializable {

    private String regnNo = "";
    private String regnDt = "";
    private String rcPreprintedDt = "";
    private String ownerName = "";
    private String currentAddress = "";
    private String rcIssueDt = "";
    private List<DownloadDispatchDobj> listFileExport;
    private List<DownloadDispatchDobj> islistFileExport;
    private String downloadFileName = "";
    private String reportGenDate;
    private String cur_date;
    private String add1 = "";
    private String add2 = "";
    private String add3 = "";
    private String city = "";
    private String pincode;
    private String fname = "";
    private String appl_no;
    private String mobile_no;
    private String dispatch_ref_no;
    private List<DownloadDispatchDobj> listFileExportWithDocRefNo;
    private String dispatchdate;
    private String offName;
    private boolean isDispatchRefNo = false;
    private String stateName;
    private String dispatch_ref_no_for_display;
    private String email_id;
    private String dispatch_rc_details;
    private String pending_dispatch_rc_details;
    private String dispatch_rc_return_on;
    private String printed_on;
    private String from_date;
    private String to_date;
    private String rcptHeading;
    private String rcptSubHeading;
    private String returnReason;
    private String caddress;
    private String dispatch_by;
    private String currentStartNo;
    private String endNo;
    private String prefix;
    private boolean updateSeries;
    private String offPinCode;
    private String dispatch_rc_return_by;
    private String dispatch_rc_handed_on;
    private String dispatch_rc_handed_by;
    private String remark;
    private String dispatch_date;
    private Date dt_hand_over = null;
    private int srno = 0;
    private boolean isShowBarcode;
    private int off_cd;
    private String state_cd;
    private String image_background;
    private String image_logo;
    private boolean show_image_background;
    private boolean show_image_logo;
    private String labelMLOORDTO;
    private String offAddress;
    private String off_mobileno;
    private String off_landline;
    private String email_id_offAdmin;

    /**
     * @return the regnNo
     */
    public String getRegnNo() {
        return regnNo;
    }

    /**
     * @param regnNo the regnNo to set
     */
    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    /**
     * @return the regnDt
     */
    public String getRegnDt() {
        return regnDt;
    }

    /**
     * @param regnDt the regnDt to set
     */
    public void setRegnDt(String regnDt) {
        this.regnDt = regnDt;
    }

    /**
     * @return the rcPreprintedDt
     */
    public String getRcPreprintedDt() {
        return rcPreprintedDt;
    }

    /**
     * @param rcPreprintedDt the rcPreprintedDt to set
     */
    public void setRcPreprintedDt(String rcPreprintedDt) {
        this.rcPreprintedDt = rcPreprintedDt;
    }

    /**
     * @return the ownerName
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * @param ownerName the ownerName to set
     */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    /**
     * @return the currentAddress
     */
    public String getCurrentAddress() {
        return currentAddress;
    }

    /**
     * @param currentAddress the currentAddress to set
     */
    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    /**
     * @return the rcIssueDt
     */
    public String getRcIssueDt() {
        return rcIssueDt;
    }

    /**
     * @param rcIssueDt the rcIssueDt to set
     */
    public void setRcIssueDt(String rcIssueDt) {
        this.rcIssueDt = rcIssueDt;
    }

    /**
     * @return the listFileExport
     */
    public List<DownloadDispatchDobj> getListFileExport() {
        return listFileExport;
    }

    /**
     * @param listFileExport the listFileExport to set
     */
    public void setListFileExport(List<DownloadDispatchDobj> listFileExport) {
        this.listFileExport = listFileExport;
    }

    /**
     * @return the downloadFileName
     */
    public String getDownloadFileName() {
        return downloadFileName;
    }

    /**
     * @param downloadFileName the downloadFileName to set
     */
    public void setDownloadFileName(String downloadFileName) {
        this.downloadFileName = downloadFileName;
    }

    /**
     * @return the islistFileExport
     */
    public List<DownloadDispatchDobj> getIslistFileExport() {
        return islistFileExport;
    }

    /**
     * @param islistFileExport the islistFileExport to set
     */
    public void setIslistFileExport(List<DownloadDispatchDobj> islistFileExport) {
        this.islistFileExport = islistFileExport;
    }

    /**
     * @return the reportGenDate
     */
    public String getReportGenDate() {
        return reportGenDate;
    }

    /**
     * @param reportGenDate the reportGenDate to set
     */
    public void setReportGenDate(String reportGenDate) {
        this.reportGenDate = reportGenDate;
    }

    /**
     * @return the cur_date
     */
    public String getCur_date() {
        return cur_date;
    }

    /**
     * @param cur_date the cur_date to set
     */
    public void setCur_date(String cur_date) {
        this.cur_date = cur_date;
    }

    /**
     * @return the add1
     */
    public String getAdd1() {
        return add1;
    }

    /**
     * @param add1 the add1 to set
     */
    public void setAdd1(String add1) {
        this.add1 = add1;
    }

    /**
     * @return the add2
     */
    public String getAdd2() {
        return add2;
    }

    /**
     * @param add2 the add2 to set
     */
    public void setAdd2(String add2) {
        this.add2 = add2;
    }

    /**
     * @return the add3
     */
    public String getAdd3() {
        return add3;
    }

    /**
     * @param add3 the add3 to set
     */
    public void setAdd3(String add3) {
        this.add3 = add3;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the pincode
     */
    public String getPincode() {
        return pincode;
    }

    /**
     * @param pincode the pincode to set
     */
    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    /**
     * @return the fname
     */
    public String getFname() {
        return fname;
    }

    /**
     * @param fname the fname to set
     */
    public void setFname(String fname) {
        this.fname = fname;
    }

    /**
     * @return the appl_no
     */
    public String getAppl_no() {
        return appl_no;
    }

    /**
     * @param appl_no the appl_no to set
     */
    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    /**
     * @return the mobile_no
     */
    public String getMobile_no() {
        return mobile_no;
    }

    /**
     * @param mobile_no the mobile_no to set
     */
    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    /**
     * @return the dispatch_ref_no
     */
    public String getDispatch_ref_no() {
        return dispatch_ref_no;
    }

    /**
     * @param dispatch_ref_no the dispatch_ref_no to set
     */
    public void setDispatch_ref_no(String dispatch_ref_no) {
        this.dispatch_ref_no = dispatch_ref_no;
    }

    /**
     * @return the listFileExportWithDocRefNo
     */
    public List<DownloadDispatchDobj> getListFileExportWithDocRefNo() {
        return listFileExportWithDocRefNo;
    }

    /**
     * @param listFileExportWithDocRefNo the listFileExportWithDocRefNo to set
     */
    public void setListFileExportWithDocRefNo(List<DownloadDispatchDobj> listFileExportWithDocRefNo) {
        this.listFileExportWithDocRefNo = listFileExportWithDocRefNo;
    }

    /**
     * @return the dispatchdate
     */
    public String getDispatchdate() {
        return dispatchdate;
    }

    /**
     * @param dispatchdate the dispatchdate to set
     */
    public void setDispatchdate(String dispatchdate) {
        this.dispatchdate = dispatchdate;
    }

    /**
     * @return the offName
     */
    public String getOffName() {
        return offName;
    }

    /**
     * @param offName the offName to set
     */
    public void setOffName(String offName) {
        this.offName = offName;
    }

    /**
     * @return the stateName
     */
    public String getStateName() {
        return stateName;
    }

    /**
     * @param stateName the stateName to set
     */
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    /**
     * @return the dispatch_ref_no_for_display
     */
    public String getDispatch_ref_no_for_display() {
        return dispatch_ref_no_for_display;
    }

    /**
     * @param dispatch_ref_no_for_display the dispatch_ref_no_for_display to set
     */
    public void setDispatch_ref_no_for_display(String dispatch_ref_no_for_display) {
        this.dispatch_ref_no_for_display = dispatch_ref_no_for_display;
    }

    /**
     * @return the email_id
     */
    public String getEmail_id() {
        return email_id;
    }

    /**
     * @param email_id the email_id to set
     */
    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    /**
     * @return the dispatch_rc_details
     */
    public String getDispatch_rc_details() {
        return dispatch_rc_details;
    }

    /**
     * @param dispatch_rc_details the dispatch_rc_details to set
     */
    public void setDispatch_rc_details(String dispatch_rc_details) {
        this.dispatch_rc_details = dispatch_rc_details;
    }

    /**
     * @return the pending_dispatch_rc_details
     */
    public String getPending_dispatch_rc_details() {
        return pending_dispatch_rc_details;
    }

    /**
     * @param pending_dispatch_rc_details the pending_dispatch_rc_details to set
     */
    public void setPending_dispatch_rc_details(String pending_dispatch_rc_details) {
        this.pending_dispatch_rc_details = pending_dispatch_rc_details;
    }

    /**
     * @return the dispatch_rc_return_on
     */
    public String getDispatch_rc_return_on() {
        return dispatch_rc_return_on;
    }

    /**
     * @param dispatch_rc_return_on the dispatch_rc_return_on to set
     */
    public void setDispatch_rc_return_on(String dispatch_rc_return_on) {
        this.dispatch_rc_return_on = dispatch_rc_return_on;
    }

    /**
     * @return the printed_on
     */
    public String getPrinted_on() {
        return printed_on;
    }

    /**
     * @param printed_on the printed_on to set
     */
    public void setPrinted_on(String printed_on) {
        this.printed_on = printed_on;
    }

    /**
     * @return the from_date
     */
    public String getFrom_date() {
        return from_date;
    }

    /**
     * @param from_date the from_date to set
     */
    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    /**
     * @return the to_date
     */
    public String getTo_date() {
        return to_date;
    }

    /**
     * @param to_date the to_date to set
     */
    public void setTo_date(String to_date) {
        this.to_date = to_date;
    }

    /**
     * @return the rcptHeading
     */
    public String getRcptHeading() {
        return rcptHeading;
    }

    /**
     * @param rcptHeading the rcptHeading to set
     */
    public void setRcptHeading(String rcptHeading) {
        this.rcptHeading = rcptHeading;
    }

    /**
     * @return the rcptSubHeading
     */
    public String getRcptSubHeading() {
        return rcptSubHeading;
    }

    /**
     * @param rcptSubHeading the rcptSubHeading to set
     */
    public void setRcptSubHeading(String rcptSubHeading) {
        this.rcptSubHeading = rcptSubHeading;
    }

    /**
     * @return the returnReason
     */
    public String getReturnReason() {
        return returnReason;
    }

    /**
     * @param returnReason the returnReason to set
     */
    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }

    /**
     * @return the caddress
     */
    public String getCaddress() {
        return caddress;
    }

    /**
     * @param caddress the caddress to set
     */
    public void setCaddress(String caddress) {
        this.caddress = caddress;
    }

    /**
     * @return the dispatch_by
     */
    public String getDispatch_by() {
        return dispatch_by;
    }

    /**
     * @param dispatch_by the dispatch_by to set
     */
    public void setDispatch_by(String dispatch_by) {
        this.dispatch_by = dispatch_by;
    }

    /**
     * @return the currentStartNo
     */
    public String getCurrentStartNo() {
        return currentStartNo;
    }

    /**
     * @param currentStartNo the currentStartNo to set
     */
    public void setCurrentStartNo(String currentStartNo) {
        this.currentStartNo = currentStartNo;
    }

    /**
     * @return the endNo
     */
    public String getEndNo() {
        return endNo;
    }

    /**
     * @param endNo the endNo to set
     */
    public void setEndNo(String endNo) {
        this.endNo = endNo;
    }

    /**
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @param prefix the prefix to set
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * @return the updateSeries
     */
    public boolean isUpdateSeries() {
        return updateSeries;
    }

    /**
     * @param updateSeries the updateSeries to set
     */
    public void setUpdateSeries(boolean updateSeries) {
        this.updateSeries = updateSeries;
    }

    /**
     * @return the offPinCode
     */
    public String getOffPinCode() {
        return offPinCode;
    }

    /**
     * @param offPinCode the offPinCode to set
     */
    public void setOffPinCode(String offPinCode) {
        this.offPinCode = offPinCode;
    }

    /**
     * @return the dispatch_rc_return_by
     */
    public String getDispatch_rc_return_by() {
        return dispatch_rc_return_by;
    }

    /**
     * @param dispatch_rc_return_by the dispatch_rc_return_by to set
     */
    public void setDispatch_rc_return_by(String dispatch_rc_return_by) {
        this.dispatch_rc_return_by = dispatch_rc_return_by;
    }

    /**
     * @return the dispatch_rc_handed_on
     */
    public String getDispatch_rc_handed_on() {
        return dispatch_rc_handed_on;
    }

    /**
     * @param dispatch_rc_handed_on the dispatch_rc_handed_on to set
     */
    public void setDispatch_rc_handed_on(String dispatch_rc_handed_on) {
        this.dispatch_rc_handed_on = dispatch_rc_handed_on;
    }

    /**
     * @return the dispatch_rc_handed_by
     */
    public String getDispatch_rc_handed_by() {
        return dispatch_rc_handed_by;
    }

    /**
     * @param dispatch_rc_handed_by the dispatch_rc_handed_by to set
     */
    public void setDispatch_rc_handed_by(String dispatch_rc_handed_by) {
        this.dispatch_rc_handed_by = dispatch_rc_handed_by;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark the remark to set
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return the dispatch_date
     */
    public String getDispatch_date() {
        return dispatch_date;
    }

    /**
     * @param dispatch_date the dispatch_date to set
     */
    public void setDispatch_date(String dispatch_date) {
        this.dispatch_date = dispatch_date;
    }

    /**
     * @return the dt_hand_over
     */
    public Date getDt_hand_over() {
        return dt_hand_over;
    }

    /**
     * @param dt_hand_over the dt_hand_over to set
     */
    public void setDt_hand_over(Date dt_hand_over) {
        this.dt_hand_over = dt_hand_over;
    }

    /**
     * @return the srno
     */
    public int getSrno() {
        return srno;
    }

    /**
     * @param srno the srno to set
     */
    public void setSrno(int srno) {
        this.srno = srno;
    }

    /**
     * @return the isShowBarcode
     */
    public boolean isIsShowBarcode() {
        return isShowBarcode;
    }

    /**
     * @param isShowBarcode the isShowBarcode to set
     */
    public void setIsShowBarcode(boolean isShowBarcode) {
        this.isShowBarcode = isShowBarcode;
    }

    /**
     * @return the off_cd
     */
    public int getOff_cd() {
        return off_cd;
    }

    /**
     * @param off_cd the off_cd to set
     */
    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
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

    /**
     * @return the image_background
     */
    public String getImage_background() {
        return image_background;
    }

    /**
     * @param image_background the image_background to set
     */
    public void setImage_background(String image_background) {
        this.image_background = image_background;
    }

    /**
     * @return the image_logo
     */
    public String getImage_logo() {
        return image_logo;
    }

    /**
     * @param image_logo the image_logo to set
     */
    public void setImage_logo(String image_logo) {
        this.image_logo = image_logo;
    }

    /**
     * @return the show_image_background
     */
    public boolean isShow_image_background() {
        return show_image_background;
    }

    /**
     * @param show_image_background the show_image_background to set
     */
    public void setShow_image_background(boolean show_image_background) {
        this.show_image_background = show_image_background;
    }

    /**
     * @return the show_image_logo
     */
    public boolean isShow_image_logo() {
        return show_image_logo;
    }

    /**
     * @param show_image_logo the show_image_logo to set
     */
    public void setShow_image_logo(boolean show_image_logo) {
        this.show_image_logo = show_image_logo;
    }

    /**
     * @return the labelMLOORDTO
     */
    public String getLabelMLOORDTO() {
        return labelMLOORDTO;
    }

    /**
     * @param labelMLOORDTO the labelMLOORDTO to set
     */
    public void setLabelMLOORDTO(String labelMLOORDTO) {
        this.labelMLOORDTO = labelMLOORDTO;
    }

    /**
     * @return the offAddress
     */
    public String getOffAddress() {
        return offAddress;
    }

    /**
     * @param offAddress the offAddress to set
     */
    public void setOffAddress(String offAddress) {
        this.offAddress = offAddress;
    }

    /**
     * @return the off_mobileno
     */
    public String getOff_mobileno() {
        return off_mobileno;
    }

    /**
     * @param off_mobileno the off_mobileno to set
     */
    public void setOff_mobileno(String off_mobileno) {
        this.off_mobileno = off_mobileno;
    }

    /**
     * @return the off_landline
     */
    public String getOff_landline() {
        return off_landline;
    }

    /**
     * @param off_landline the off_landline to set
     */
    public void setOff_landline(String off_landline) {
        this.off_landline = off_landline;
    }

    /**
     * @return the email_id_offAdmin
     */
    public String getEmail_id_offAdmin() {
        return email_id_offAdmin;
    }

    /**
     * @param email_id_offAdmin the email_id_offAdmin to set
     */
    public void setEmail_id_offAdmin(String email_id_offAdmin) {
        this.email_id_offAdmin = email_id_offAdmin;
    }
}
