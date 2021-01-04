/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Administrator
 */
public class PermitDetailDobj implements Serializable {

    private String regn_no;
    private String state_cd;
    private int off_cd;
    private String pmt_no;
    private Date issue_dt;
    private Date valid_from;
    private Date valid_upto;
    private String rcpt_no;
    private int pur_cd;
    private int pmt_type;
    private String pmt_type_desc;
    private int pmt_catg;
    private String pmt_catg_desc;
    private int service_type;
    private String pmt_Services_desc;
    private String applicationNo;
    private String purCdDesc;
    private String goodsToCarry;
    private String journey;
    private String parking;
    private Date replaceDt;
    private String authNo;
    private String authTo;
    private String authFrom;
    private String transactionPurCdDescr;
    private int trans_pur_cd;
    private String regionCovered;

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
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

    public String getPmt_no() {
        return pmt_no;
    }

    public void setPmt_no(String pmt_no) {
        this.pmt_no = pmt_no;
    }

    public Date getIssue_dt() {
        return issue_dt;
    }

    public void setIssue_dt(Date issue_dt) {
        this.issue_dt = issue_dt;
    }

    public Date getValid_from() {
        return valid_from;
    }

    public void setValid_from(Date valid_from) {
        this.valid_from = valid_from;
    }

    public Date getValid_upto() {
        return valid_upto;
    }

    public void setValid_upto(Date valid_upto) {
        this.valid_upto = valid_upto;
    }

    public String getRcpt_no() {
        return rcpt_no;
    }

    public void setRcpt_no(String rcpt_no) {
        this.rcpt_no = rcpt_no;
    }

    public int getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    public int getPmt_type() {
        return pmt_type;
    }

    public void setPmt_type(int pmt_type) {
        this.pmt_type = pmt_type;
    }

    public String getPmt_type_desc() {
        return pmt_type_desc;
    }

    public void setPmt_type_desc(String pmt_type_desc) {
        this.pmt_type_desc = pmt_type_desc;
    }

    public int getPmt_catg() {
        return pmt_catg;
    }

    public void setPmt_catg(int pmt_catg) {
        this.pmt_catg = pmt_catg;
    }

    public String getPmt_catg_desc() {
        return pmt_catg_desc;
    }

    public void setPmt_catg_desc(String pmt_catg_desc) {
        this.pmt_catg_desc = pmt_catg_desc;
    }

    public int getService_type() {
        return service_type;
    }

    public void setService_type(int service_type) {
        this.service_type = service_type;
    }

    public String getPmt_Services_desc() {
        return pmt_Services_desc;
    }

    public void setPmt_Services_desc(String pmt_Services_desc) {
        this.pmt_Services_desc = pmt_Services_desc;
    }

    /**
     * @return the applicationNo
     */
    public String getApplicationNo() {
        return applicationNo;
    }

    /**
     * @param applicationNo the applicationNo to set
     */
    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }

    /**
     * @return the purCdDesc
     */
    public String getPurCdDesc() {
        return purCdDesc;
    }

    /**
     * @param purCdDesc the purCdDesc to set
     */
    public void setPurCdDesc(String purCdDesc) {
        this.purCdDesc = purCdDesc;
    }

    /**
     * @return the goodsToCarry
     */
    public String getGoodsToCarry() {
        return goodsToCarry;
    }

    /**
     * @param goodsToCarry the goodsToCarry to set
     */
    public void setGoodsToCarry(String goodsToCarry) {
        this.goodsToCarry = goodsToCarry;
    }

    /**
     * @return the journey
     */
    public String getJourney() {
        return journey;
    }

    /**
     * @param journey the journey to set
     */
    public void setJourney(String journey) {
        this.journey = journey;
    }

    /**
     * @return the parking
     */
    public String getParking() {
        return parking;
    }

    /**
     * @param parking the parking to set
     */
    public void setParking(String parking) {
        this.parking = parking;
    }

    /**
     * @return the replaceDt
     */
    public Date getReplaceDt() {
        return replaceDt;
    }

    /**
     * @param replaceDt the replaceDt to set
     */
    public void setReplaceDt(Date replaceDt) {
        this.replaceDt = replaceDt;
    }

    /**
     * @return the authNo
     */
    public String getAuthNo() {
        return authNo;
    }

    /**
     * @param authNo the authNo to set
     */
    public void setAuthNo(String authNo) {
        this.authNo = authNo;
    }

    /**
     * @return the authTo
     */
    public String getAuthTo() {
        return authTo;
    }

    /**
     * @param authTo the authTo to set
     */
    public void setAuthTo(String authTo) {
        this.authTo = authTo;
    }

    /**
     * @return the authFrom
     */
    public String getAuthFrom() {
        return authFrom;
    }

    /**
     * @param authFrom the authFrom to set
     */
    public void setAuthFrom(String authFrom) {
        this.authFrom = authFrom;
    }

    /**
     * @return the transactionPurCdDescr
     */
    public String getTransactionPurCdDescr() {
        return transactionPurCdDescr;
    }

    /**
     * @param transactionPurCdDescr the transactionPurCdDescr to set
     */
    public void setTransactionPurCdDescr(String transactionPurCdDescr) {
        this.transactionPurCdDescr = transactionPurCdDescr;
    }

    /**
     * @return the trans_pur_cd
     */
    public int getTrans_pur_cd() {
        return trans_pur_cd;
    }

    /**
     * @param trans_pur_cd the trans_pur_cd to set
     */
    public void setTrans_pur_cd(int trans_pur_cd) {
        this.trans_pur_cd = trans_pur_cd;
    }

    public String getRegionCovered() {
        return regionCovered;
    }

    public void setRegionCovered(String regionCovered) {
        this.regionCovered = regionCovered;
    }
}
