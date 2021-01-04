/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author nicsi
 */
public class OffenceReferDetailsDobj implements Serializable {

    private String OffenceCode;
    private String sectionCode;
    private String OffenceDescr;
    private String sectionDescr;
    private String authorityCode;
    private String authoritydescr;
    private String penalty;
    private String accusedDescr;
    private Date referDate;
    private Date referDate_show;
    private Date Decision_date;
    private Date Decision_date_show;
    private String authority_decision;

    public OffenceReferDetailsDobj() {
    }

    public OffenceReferDetailsDobj(String OffenceCode, String sectionCode, String OffenceDescr, String sectionDescr, String authorityCode, String authoritydescr, String penalty, String accusedDescr) {
        this.OffenceCode = OffenceCode;
        this.sectionCode = sectionCode;
        this.OffenceDescr = OffenceDescr;
        this.sectionDescr = sectionDescr;
        this.authorityCode = authorityCode;
        this.authoritydescr = authoritydescr;
        this.penalty = penalty;
        this.accusedDescr = accusedDescr;
        // this.referDate = referDate;
    }

    public String getOffenceCode() {
        return OffenceCode;
    }

    public void setOffenceCode(String OffenceCode) {
        this.OffenceCode = OffenceCode;
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public void setSectionCode(String sectionCode) {
        this.sectionCode = sectionCode;
    }

    public String getOffenceDescr() {
        return OffenceDescr;
    }

    public void setOffenceDescr(String OffenceDescr) {
        this.OffenceDescr = OffenceDescr;
    }

    public String getSectionDescr() {
        return sectionDescr;
    }

    public void setSectionDescr(String sectionDescr) {
        this.sectionDescr = sectionDescr;
    }

    public String getAuthorityCode() {
        return authorityCode;
    }

    public void setAuthorityCode(String authorityCode) {
        this.authorityCode = authorityCode;
    }

    public String getAuthoritydescr() {
        return authoritydescr;
    }

    public void setAuthoritydescr(String authoritydescr) {
        this.authoritydescr = authoritydescr;
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

    /**
     * @return the referDate
     */
    public Date getReferDate() {
        return referDate;
    }

    /**
     * @param referDate the referDate to set
     */
    public void setReferDate(Date referDate) {
        this.referDate = referDate;
    }

    /**
     * @return the Decision_date
     */
    public Date getDecision_date() {
        return Decision_date;
    }

    /**
     * @param Decision_date the Decision_date to set
     */
    public void setDecision_date(Date Decision_date) {
        this.Decision_date = Decision_date;
    }

    /**
     * @return the authority_decision
     */
    public String getAuthority_decision() {
        return authority_decision;
    }

    /**
     * @param authority_decision the authority_decision to set
     */
    public void setAuthority_decision(String authority_decision) {
        this.authority_decision = authority_decision;
    }

    public Date getReferDate_show() {
        return referDate_show;
    }

    public void setReferDate_show(Date referDate_show) {
        this.referDate_show = referDate_show;
    }

    public Date getDecision_date_show() {
        return Decision_date_show;
    }

    public void setDecision_date_show(Date Decision_date_show) {
        this.Decision_date_show = Decision_date_show;
    }
}
