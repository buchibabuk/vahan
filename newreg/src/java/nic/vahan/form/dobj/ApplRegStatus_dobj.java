/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nic5912
 */
public class ApplRegStatus_dobj implements Serializable {

    private String appl_no;
    private String appl_dt;
    private String regno;
    private int purCd;
    private String purCdDescr;
    private String role_cd;
    private String cur_seat;
    private String roleDesc;
    private String statusDesc;
    private String op_dt;
    private String action_descr;
    private String emp_name;
    private String office_remark;
    private String Status;
    private String PublicRemark;
    private String Office;
    private String Entered_by;
    private String Entered_on;
    private String approvalStatus;
    private String entry_ip;
    private String rcPrinted;
    private String fcPrinted;
    private String smartCardDone;
    private String hsrpDone;
    private String dealerCd;
    private String dispatchRCDone;
    private String offName;
    private List<ApplRegStatus_dobj> list = new ArrayList();

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
     * @return the regno
     */
    public String getRegno() {
        return regno;
    }

    /**
     * @param regno the regno to set
     */
    public void setRegno(String regno) {
        this.regno = regno;
    }

    /**
     * @return the purCd
     */
    public int getPurCd() {
        return purCd;
    }

    /**
     * @param purCd the purCd to set
     */
    public void setPurCd(int purCd) {
        this.purCd = purCd;
    }

    /**
     * @return the purCdDescr
     */
    public String getPurCdDescr() {
        return purCdDescr;
    }

    /**
     * @param purCdDescr the purCdDescr to set
     */
    public void setPurCdDescr(String purCdDescr) {
        this.purCdDescr = purCdDescr;
    }

    /**
     * @return the role_cd
     */
    public String getRole_cd() {
        return role_cd;
    }

    /**
     * @param role_cd the role_cd to set
     */
    public void setRole_cd(String role_cd) {
        this.role_cd = role_cd;
    }

    /**
     * @return the cur_seat
     */
    public String getCur_seat() {
        return cur_seat;
    }

    /**
     * @param cur_seat the cur_seat to set
     */
    public void setCur_seat(String cur_seat) {
        this.cur_seat = cur_seat;
    }

    /**
     * @return the list
     */
    public List<ApplRegStatus_dobj> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(List<ApplRegStatus_dobj> list) {
        this.list = list;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    /**
     * @return the op_dt
     */
    public String getOp_dt() {
        return op_dt;
    }

    /**
     * @param op_dt the op_dt to set
     */
    public void setOp_dt(String op_dt) {
        this.op_dt = op_dt;
    }

    /**
     * @return the action_descr
     */
    public String getAction_descr() {
        return action_descr;
    }

    /**
     * @param action_descr the action_descr to set
     */
    public void setAction_descr(String action_descr) {
        this.action_descr = action_descr;
    }

    /**
     * @return the emp_name
     */
    public String getEmp_name() {
        return emp_name;
    }

    /**
     * @param emp_name the emp_name to set
     */
    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    /**
     * @return the office_remark
     */
    public String getOffice_remark() {
        return office_remark;
    }

    /**
     * @param office_remark the office_remark to set
     */
    public void setOffice_remark(String office_remark) {
        this.office_remark = office_remark;
    }

    /**
     * @return the Status
     */
    public String getStatus() {
        return Status;
    }

    /**
     * @param Status the Status to set
     */
    public void setStatus(String Status) {
        this.Status = Status;
    }

    /**
     * @return the PublicRemark
     */
    public String getPublicRemark() {
        return PublicRemark;
    }

    /**
     * @param PublicRemark the PublicRemark to set
     */
    public void setPublicRemark(String PublicRemark) {
        this.PublicRemark = PublicRemark;
    }

    /**
     *
     * @return the Office
     */
    public String getOffice() {
        return Office;
    }

    /**
     * @param Office the Office to set
     */
    public void setOffice(String Office) {
        this.Office = Office;
    }

    /**
     * @return the Entered_by
     */
    public String getEntered_by() {
        return Entered_by;
    }

    /**
     * @param Entered_by the Entered_by to set
     */
    public void setEntered_by(String Entered_by) {
        this.Entered_by = Entered_by;
    }

    /**
     * @return the Entered_on
     */
    public String getEntered_on() {
        return Entered_on;
    }

    /**
     * @param Entered_on the Entered_on to set
     */
    public void setEntered_on(String Entered_on) {
        this.Entered_on = Entered_on;
    }

    /**
     * @return the approvalStatus
     */
    public String getApprovalStatus() {
        return approvalStatus;
    }

    /**
     * @param approvalStatus the approvalStatus to set
     */
    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    /**
     * @return the entry_ip
     */
    public String getEntry_ip() {
        return entry_ip;
    }

    /**
     * @param entry_ip the entry_ip to set
     */
    public void setEntry_ip(String entry_ip) {
        this.entry_ip = entry_ip;
    }

    /**
     * @return the appl_dt
     */
    public String getAppl_dt() {
        return appl_dt;
    }

    /**
     * @param appl_dt the appl_dt to set
     */
    public void setAppl_dt(String appl_dt) {
        this.appl_dt = appl_dt;
    }

    /**
     * @return the rcPrinted
     */
    public String getRcPrinted() {
        return rcPrinted;
    }

    /**
     * @param rcPrinted the rcPrinted to set
     */
    public void setRcPrinted(String rcPrinted) {
        this.rcPrinted = rcPrinted;
    }

    /**
     * @return the smartCardDone
     */
    public String getSmartCardDone() {
        return smartCardDone;
    }

    /**
     * @param smartCardDone the smartCardDone to set
     */
    public void setSmartCardDone(String smartCardDone) {
        this.smartCardDone = smartCardDone;
    }

    /**
     * @return the hsrpDone
     */
    public String getHsrpDone() {
        return hsrpDone;
    }

    /**
     * @param hsrpDone the hsrpDone to set
     */
    public void setHsrpDone(String hsrpDone) {
        this.hsrpDone = hsrpDone;
    }

    /**
     * @return the dealerCd
     */
    public String getDealerCd() {
        return dealerCd;
    }

    /**
     * @param dealerCd the dealerCd to set
     */
    public void setDealerCd(String dealerCd) {
        this.dealerCd = dealerCd;
    }

    /**
     * @return the dispatchRCDone
     */
    public String getDispatchRCDone() {
        return dispatchRCDone;
    }

    /**
     * @param dispatchRCDone the dispatchRCDone to set
     */
    public void setDispatchRCDone(String dispatchRCDone) {
        this.dispatchRCDone = dispatchRCDone;
    }

    public String getOffName() {
        return offName;
    }

    public void setOffName(String offName) {
        this.offName = offName;
    }

    /**
     * @return the fcPrinted
     */
    public String getFcPrinted() {
        return fcPrinted;
    }

    /**
     * @param fcPrinted the fcPrinted to set
     */
    public void setFcPrinted(String fcPrinted) {
        this.fcPrinted = fcPrinted;
    }
}
