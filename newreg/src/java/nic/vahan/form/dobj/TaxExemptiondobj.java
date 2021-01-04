/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Administrator
 */
public class TaxExemptiondobj implements Cloneable, Serializable, Comparable<TaxExemptiondobj> {

    private Date exemFromDt;
    private Date exemTo;
    private String authBy;
    private Date permissionDt;
    private String permissionNo;
    private String perpose;
    private String regn_no;
    private String Appl_no;
    private String state_cd;
    private int off_cd;
    private int pur_cd;
    private String exemHead;
    private long exemFeeAmount;
    private long exemAmount;
    private long totalAmount;

    public Date getExemFromDt() {
        return exemFromDt;
    }

    public void setExemFromDt(Date exemFromDt) {
        this.exemFromDt = exemFromDt;
    }

    public Date getExemTo() {
        return exemTo;
    }

    public void setExemTo(Date exemTo) {
        this.exemTo = exemTo;
    }

    public String getAuthBy() {
        return authBy;
    }

    public void setAuthBy(String authBy) {
        this.authBy = authBy;
    }

    public Date getPermissionDt() {
        return permissionDt;
    }

    public void setPermissionDt(Date permissionDt) {
        this.permissionDt = permissionDt;
    }

    public String getPermissionNo() {
        return permissionNo;
    }

    public void setPermissionNo(String permissionNo) {
        this.permissionNo = permissionNo;
    }

    public String getPerpose() {
        return perpose;
    }

    public void setPerpose(String perpose) {
        this.perpose = perpose;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getAppl_no() {
        return Appl_no;
    }

    public void setAppl_no(String Appl_no) {
        this.Appl_no = Appl_no;
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    /**
     * @return the exemHead
     */
    public String getExemHead() {
        return exemHead;
    }

    /**
     * @param exemHead the exemHead to set
     */
    public void setExemHead(String exemHead) {
        this.exemHead = exemHead;
    }

    /**
     * @return the exemAmount
     */
    public long getExemAmount() {
        return exemAmount;
    }

    /**
     * @param exemAmount the exemAmount to set
     */
    public void setExemAmount(long exemAmount) {
        this.exemAmount = exemAmount;
    }

    /**
     * @return the totalAmount
     */
    public long getTotalAmount() {
        return totalAmount;
    }

    /**
     * @param totalAmount the totalAmount to set
     */
    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public int compareTo(TaxExemptiondobj o) {
        return this.pur_cd - o.pur_cd;
    }

    public long getExemFeeAmount() {
        return exemFeeAmount;
    }

    public void setExemFeeAmount(long exemFeeAmount) {
        this.exemFeeAmount = exemFeeAmount;
    }
}
