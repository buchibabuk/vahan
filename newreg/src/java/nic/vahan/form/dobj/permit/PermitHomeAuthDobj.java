/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Administrator
 */
public class PermitHomeAuthDobj implements Cloneable, Serializable {

    private String authNo;
    private Date authFrom;
    private Date authUpto;
    private Date taxFrom;
    private Date taxUpto;
    private String regnNo;
    private String applNo;
    private String pmtNo;
    private Date prvAuthUpto;
    private int purCd;
    private String authFromInString;
    private String authUptoInString;
    private String opDateInString;
    private String rcptNo;
    private int regionCoveredNP;
    private String state_cd;
    private int off_cd;
    private String npVerifyStatus;

    public String to_String() {
        return "";
    }

    public String getAuthNo() {
        return authNo;
    }

    public void setAuthNo(String authNo) {
        this.authNo = authNo;
    }

    public Date getAuthFrom() {
        return authFrom;
    }

    public void setAuthFrom(Date authFrom) {
        this.authFrom = authFrom;
    }

    public Date getAuthUpto() {
        return authUpto;
    }

    public void setAuthUpto(Date authUpto) {
        this.authUpto = authUpto;
    }

    public String getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    public String getApplNo() {
        return applNo;
    }

    public void setApplNo(String applNo) {
        this.applNo = applNo;
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

    public String getPmtNo() {
        return pmtNo;
    }

    public void setPmtNo(String pmtNo) {
        this.pmtNo = pmtNo;
    }

    public Date getPrvAuthUpto() {
        return prvAuthUpto;
    }

    public void setPrvAuthUpto(Date prvAuthUpto) {
        this.prvAuthUpto = prvAuthUpto;
    }

    public int getPurCd() {
        return purCd;
    }

    public void setPurCd(int purCd) {
        this.purCd = purCd;
    }

    public String getAuthFromInString() {
        return authFromInString;
    }

    public void setAuthFromInString(String authFromInString) {
        this.authFromInString = authFromInString;
    }

    public String getAuthUptoInString() {
        return authUptoInString;
    }

    public void setAuthUptoInString(String authUptoInString) {
        this.authUptoInString = authUptoInString;
    }

    public String getOpDateInString() {
        return opDateInString;
    }

    public void setOpDateInString(String opDateInString) {
        this.opDateInString = opDateInString;
    }

    public String getRcptNo() {
        return rcptNo;
    }

    public void setRcptNo(String rcptNo) {
        this.rcptNo = rcptNo;
    }

    public int getRegionCoveredNP() {
        return regionCoveredNP;
    }

    public void setRegionCoveredNP(int regionCoveredNP) {
        this.regionCoveredNP = regionCoveredNP;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
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

    public String getNpVerifyStatus() {
        return npVerifyStatus;
    }

    public void setNpVerifyStatus(String npVerifyStatus) {
        this.npVerifyStatus = npVerifyStatus;
    }
}
