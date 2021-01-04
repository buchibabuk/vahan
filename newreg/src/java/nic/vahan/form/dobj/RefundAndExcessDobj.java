/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author nirbhay.singh
 */
public class RefundAndExcessDobj implements Cloneable {

    List pur_cd_list = new ArrayList();
    private int pur_cd;
    private int refundAmt;
    private int excessAmt;
    private int balanceAmt;
    private String state_cd;
    private int off_cd;
    private String appl_no;
    private String regn_no;
    private Date op_dt;
    private Date taxFrom;
    private Date taxUpto;
    //refun
    private String rcptNo;
    private Date rcptDt;
    private String roadTaxApplNo;
    private String balTaxApplNo;
    private String purCdDescr;
    private Date orderDate;
    private String orderIssueBy;
    private String orderNo;
    private String remark;
    //end refun

    public RefundAndExcessDobj(Integer purCd) {
        this.pur_cd = purCd;
    }

    public RefundAndExcessDobj() {
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.pur_cd);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RefundAndExcessDobj other = (RefundAndExcessDobj) obj;

        if (this.pur_cd == other.pur_cd) {
            return true;
        } else {
            return false;
        }

    }

    public List getPur_cd_list() {
        return pur_cd_list;
    }

    public void setPur_cd_list(List pur_cd_list) {
        this.pur_cd_list = pur_cd_list;
    }

    public int getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    public int getRefundAmt() {
        return refundAmt;
    }

    public void setRefundAmt(int refundAmt) {
        this.refundAmt = refundAmt;
    }

    public int getExcessAmt() {
        return excessAmt;
    }

    public void setExcessAmt(int excessAmt) {
        this.excessAmt = excessAmt;
    }

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public int getOff_cd() {
        return off_cd;
    }

    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public Date getOp_dt() {
        return op_dt;
    }

    public void setOp_dt(Date op_dt) {
        this.op_dt = op_dt;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int getBalanceAmt() {
        return balanceAmt;
    }

    public void setBalanceAmt(int balanceAmt) {
        this.balanceAmt = balanceAmt;
    }

    public Date getTaxFrom() {
        return taxFrom;
    }

    public void setTaxFrom(Date taxFrom) {
        this.taxFrom = taxFrom;
    }

    public Date getTaxUpto() {
        return taxUpto;
    }

    public void setTaxUpto(Date taxUpto) {
        this.taxUpto = taxUpto;
    }

    public String getRcptNo() {
        return rcptNo;
    }

    public void setRcptNo(String rcptNo) {
        this.rcptNo = rcptNo;
    }

    public Date getRcptDt() {
        return rcptDt;
    }

    public void setRcptDt(Date rcptDt) {
        this.rcptDt = rcptDt;
    }

    public String getRoadTaxApplNo() {
        return roadTaxApplNo;
    }

    public void setRoadTaxApplNo(String roadTaxApplNo) {
        this.roadTaxApplNo = roadTaxApplNo;
    }

    public String getBalTaxApplNo() {
        return balTaxApplNo;
    }

    public void setBalTaxApplNo(String balTaxApplNo) {
        this.balTaxApplNo = balTaxApplNo;
    }

    public String getPurCdDescr() {
        return purCdDescr;
    }

    public void setPurCdDescr(String purCdDescr) {
        this.purCdDescr = purCdDescr;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderIssueBy() {
        return orderIssueBy;
    }

    public void setOrderIssueBy(String orderIssueBy) {
        this.orderIssueBy = orderIssueBy;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
