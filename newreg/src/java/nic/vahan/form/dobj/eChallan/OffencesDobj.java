/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sushil
 */
public class OffencesDobj implements Serializable {

    private String offenceCode;
    private String offenceDescr;
    private String penalty;
    private String mvcrClause;
    private String accuseInOffDetails;
    private String accusedDescr;
    private String sectionName;

    public OffencesDobj() {
    }

    public OffencesDobj(String accuseInOffDetails, String accusedDescr, String offenceCode, String offenceDescr, String penalty, String mvcrClause) {
        this.offenceCode = offenceCode;
        this.offenceDescr = offenceDescr;
        this.penalty = penalty;
        this.mvcrClause = mvcrClause;
        this.accusedDescr = accusedDescr;
        this.accuseInOffDetails = accuseInOffDetails;

    }

    public String getOffenceCode() {
        return offenceCode;
    }

    public void setOffenceCode(String offenceCode) {
        this.offenceCode = offenceCode;
    }

    public String getOffenceDescr() {
        return offenceDescr;
    }

    public void setOffenceDescr(String offenceDescr) {
        this.offenceDescr = offenceDescr;
    }

    public String getMvcrClause() {
        return mvcrClause;
    }

    public void setMvcrClause(String mvcrClause) {
        this.mvcrClause = mvcrClause;
    }

    /**
     * @return the accuseInOffDetails
     */
    public String getAccuseInOffDetails() {
        return accuseInOffDetails;
    }

    /**
     * @param accuseInOffDetails the accuseInOffDetails to set
     */
    public void setAccuseInOffDetails(String accuseInOffDetails) {
        this.accuseInOffDetails = accuseInOffDetails;
    }

    /**
     * @return the sectionName
     */
    public String getSectionName() {
        return sectionName;
    }

    /**
     * @param sectionName the sectionName to set
     */
    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getPenalty() {
        return penalty;
    }

    public void setPenalty(String penalty) {
        this.penalty = penalty;
    }

    public String getAccusedDescr() {
        return accusedDescr;
    }

    public void setAccusedDescr(String accusedDescr) {
        this.accusedDescr = accusedDescr;
    }
}
