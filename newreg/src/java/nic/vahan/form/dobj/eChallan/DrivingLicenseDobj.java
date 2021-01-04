/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;

/**
 *
 * @author tranC094
 */
public class DrivingLicenseDobj implements Serializable {

    private String challanNo;
    private String bookNo;
    private String acctCatg;
    private String accussedName;
    private String accusedAddress;
    private String dlNo;
    private String stateCd;
    private String rtoCd;

    /**
     * @return the challanNo
     */
    public String getChallanNo() {
        return challanNo;
    }

    /**
     * @param challanNo the challanNo to set
     */
    public void setChallanNo(String challanNo) {
        this.challanNo = challanNo;
    }

    /**
     * @return the bookNo
     */
    public String getBookNo() {
        return bookNo;
    }

    /**
     * @param bookNo the bookNo to set
     */
    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
    }

    /**
     * @return the acctCatg
     */
    public String getAcctCatg() {
        return acctCatg;
    }

    /**
     * @param acctCatg the acctCatg to set
     */
    public void setAcctCatg(String acctCatg) {
        this.acctCatg = acctCatg;
    }

    /**
     * @return the accussedName
     */
    public String getAccussedName() {
        return accussedName;
    }

    /**
     * @param accussedName the accussedName to set
     */
    public void setAccussedName(String accussedName) {
        this.accussedName = accussedName;
    }

    /**
     * @return the accusedAddress
     */
    public String getAccusedAddress() {
        return accusedAddress;
    }

    /**
     * @param accusedAddress the accusedAddress to set
     */
    public void setAccusedAddress(String accusedAddress) {
        this.accusedAddress = accusedAddress;
    }

    /**
     * @return the dlNo
     */
    public String getDlNo() {
        return dlNo;
    }

    /**
     * @param dlNo the dlNo to set
     */
    public void setDlNo(String dlNo) {
        this.dlNo = dlNo;
    }

    /**
     * @return the stateCd
     */
    public String getStateCd() {
        return stateCd;
    }

    /**
     * @param stateCd the stateCd to set
     */
    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    /**
     * @return the rtoCd
     */
    public String getRtoCd() {
        return rtoCd;
    }

    /**
     * @param rtoCd the rtoCd to set
     */
    public void setRtoCd(String rtoCd) {
        this.rtoCd = rtoCd;
    }
}
