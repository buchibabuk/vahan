/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;

/**
 *
 * @author tranC105
 */
public class VehcleOffenceDobj implements Serializable, Cloneable {

    private String slNo;
    private String accuCatg;
    private String offenceCd;
    private String offenceSec;
    private String penalty;
    private String offenceDesc;
    private String AccusedDescR;
    private boolean select;
    private boolean test;
    private String moved_by;
    private String moved_on;

    public VehcleOffenceDobj(String slNo, String accuCatg, String AccusedDescR, String offenceCd, String offenceSec, String penalty, String offenceDesc) {
        this.slNo = slNo;
        this.accuCatg = accuCatg;
        this.offenceCd = offenceCd;
        this.offenceSec = offenceSec;
        this.penalty = penalty;
        this.offenceDesc = offenceDesc;
        this.AccusedDescR = AccusedDescR;

    }
     public VehcleOffenceDobj(String slNo, String accuCatg, String AccusedDescR, String offenceCd, String offenceSec, String penalty, String offenceDesc,String moved_by,String moved_on) {
        this.slNo = slNo;
        this.accuCatg = accuCatg;
        this.offenceCd = offenceCd;
        this.offenceSec = offenceSec;
        this.penalty = penalty;
        this.offenceDesc = offenceDesc;
        this.AccusedDescR = AccusedDescR;
        this.moved_by=moved_by;
        this.moved_on=moved_on;

    }

    public VehcleOffenceDobj() {
    }

    public String getSlNo() {
        return slNo;
    }

    public void setSlNo(String slNo) {
        this.slNo = slNo;
    }

    public String getAccuCatg() {
        return accuCatg;
    }

    public void setAccuCatg(String accuCatg) {
        this.accuCatg = accuCatg;
    }

    public String getOffenceCd() {
        return offenceCd;
    }

    public void setOffenceCd(String offenceCd) {
        this.offenceCd = offenceCd;
    }

    public String getOffenceSec() {
        return offenceSec;
    }

    public void setOffenceSec(String offenceSec) {
        this.offenceSec = offenceSec;
    }

    /**
     * @return the offenceDesc
     */
    public String getOffenceDesc() {
        return offenceDesc;
    }

    /**
     * @param offenceDesc the offenceDesc to set
     */
    public void setOffenceDesc(String offenceDesc) {
        this.offenceDesc = offenceDesc;
    }

    public String getAccusedDescR() {
        return AccusedDescR;
    }

    public void setAccusedDescR(String AccusedDescR) {
        this.AccusedDescR = AccusedDescR;
    }

    public String getPenalty() {
        return penalty;
    }

    public void setPenalty(String penalty) {
        this.penalty = penalty;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public boolean isTest() {
        return test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return the moved_by
     */
    public String getMoved_by() {
        return moved_by;
    }

    /**
     * @param moved_by the moved_by to set
     */
    public void setMoved_by(String moved_by) {
        this.moved_by = moved_by;
    }

    /**
     * @return the moved_on
     */
    public String getMoved_on() {
        return moved_on;
    }

    /**
     * @param moved_on the moved_on to set
     */
    public void setMoved_on(String moved_on) {
        this.moved_on = moved_on;
    }
}
