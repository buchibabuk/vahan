/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

public class Owner_temp_dobj implements Serializable, Cloneable {

    private String appl_no;
    private String temp_regn_no;
    private String state_cd_to;
    private int off_cd_to;
    private String purpose;
    private String purposeDescr;
    private String bodyBuilding;
    private String state_cd_to_descr;
    private String off_cd_to_descr;
    private Date validUpto;
    private Date validFrom;
    private String temp_regn_type;
    private String tempState_cd_from;
    private int tempOff_cd_from;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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
     * @return the temp_regn_no
     */
    public String getTemp_regn_no() {
        return temp_regn_no;
    }

    /**
     * @param temp_regn_no the temp_regn_no to set
     */
    public void setTemp_regn_no(String temp_regn_no) {
        this.temp_regn_no = temp_regn_no;
    }

    /**
     * @return the state_cd_to
     */
    public String getState_cd_to() {
        return state_cd_to;
    }

    /**
     * @param state_cd_to the state_cd_to to set
     */
    public void setState_cd_to(String state_cd_to) {
        this.state_cd_to = state_cd_to;
    }

    /**
     * @return the off_cd_to
     */
    public int getOff_cd_to() {
        return off_cd_to;
    }

    /**
     * @param off_cd_to the off_cd_to to set
     */
    public void setOff_cd_to(int off_cd_to) {
        this.off_cd_to = off_cd_to;
    }

    /**
     * @return the bodyBuilding
     */
    public String getBodyBuilding() {
        return bodyBuilding;
    }

    /**
     * @param bodyBuilding the bodyBuilding to set
     */
    public void setBodyBuilding(String bodyBuilding) {
        this.bodyBuilding = bodyBuilding;
    }

    /**
     * @return the purpose
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * @param purpose the purpose to set
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    /**
     * @return the state_cd_to_descr
     */
    public String getState_cd_to_descr() {
        return state_cd_to_descr;
    }

    /**
     * @param state_cd_to_descr the state_cd_to_descr to set
     */
    public void setState_cd_to_descr(String state_cd_to_descr) {
        this.state_cd_to_descr = state_cd_to_descr;
    }

    /**
     * @return the off_cd_to_descr
     */
    public String getOff_cd_to_descr() {
        return off_cd_to_descr;
    }

    /**
     * @param off_cd_to_descr the off_cd_to_descr to set
     */
    public void setOff_cd_to_descr(String off_cd_to_descr) {
        this.off_cd_to_descr = off_cd_to_descr;
    }

    /**
     * @return the validUpto
     */
    public Date getValidUpto() {
        return validUpto;
    }

    /**
     * @param validUpto the validUpto to set
     */
    public void setValidUpto(Date validUpto) {
        this.validUpto = validUpto;
    }

    /**
     * @return the validFrom
     */
    public Date getValidFrom() {
        return validFrom;
    }

    /**
     * @param validFrom the validFrom to set
     */
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * @return the purposeDescr
     */
    public String getPurposeDescr() {
        return purposeDescr;
    }

    /**
     * @param purposeDescr the purposeDescr to set
     */
    public void setPurposeDescr(String purposeDescr) {
        this.purposeDescr = purposeDescr;
    }

    /**
     * @return the temp_regn_type
     */
    public String getTemp_regn_type() {
        return temp_regn_type;
    }

    /**
     * @param temp_regn_type the temp_regn_type to set
     */
    public void setTemp_regn_type(String temp_regn_type) {
        this.temp_regn_type = temp_regn_type;
    }
    /**
     * @return the tempOff_cd_from
     */
    public int getTempOff_cd_from() {
        return tempOff_cd_from;
    }

    /**
     * @param tempOff_cd_from the tempOff_cd_from to set
     */
    public void setTempOff_cd_from(int tempOff_cd_from) {
        this.tempOff_cd_from = tempOff_cd_from;
    }

    /**
     * @return the tempState_cd_from
     */
    public String getTempState_cd_from() {
        return tempState_cd_from;
    }

    /**
     * @param tempState_cd_from the tempState_cd_from to set
     */
    public void setTempState_cd_from(String tempState_cd_from) {
        this.tempState_cd_from = tempState_cd_from;
    }
}
