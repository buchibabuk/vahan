/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.util.Date;

/**
 *
 * @author nicsi
 */
public class DisposeChallanDobj {

    private String court_paid_amount;
    private String court_rcpt_no;
    private String decisionOfCourt;
    private Date court_rcpt_date;
    private String dateOfDispose;
    private String remarksOfDispose;
    private Date hearing_date;
    private String court_name;
    private String migistrate;

    public String getCourt_paid_amount() {
        return court_paid_amount;
    }

    public void setCourt_paid_amount(String court_paid_amount) {
        this.court_paid_amount = court_paid_amount;
    }

    public String getCourt_rcpt_no() {
        return court_rcpt_no;
    }

    public void setCourt_rcpt_no(String court_rcpt_no) {
        this.court_rcpt_no = court_rcpt_no;
    }

    public String getDecisionOfCourt() {
        return decisionOfCourt;
    }

    public void setDecisionOfCourt(String decisionOfCourt) {
        this.decisionOfCourt = decisionOfCourt;
    }

    public Date getCourt_rcpt_date() {
        return court_rcpt_date;
    }

    public void setCourt_rcpt_date(Date court_rcpt_date) {
        this.court_rcpt_date = court_rcpt_date;
    }

    public String getDateOfDispose() {
        return dateOfDispose;
    }

    public void setDateOfDispose(String dateOfDispose) {
        this.dateOfDispose = dateOfDispose;
    }

    public String getRemarksOfDispose() {
        return remarksOfDispose;
    }

    public void setRemarksOfDispose(String remarksOfDispose) {
        this.remarksOfDispose = remarksOfDispose;
    }

    /**
     * @return the hearing_date
     */
    public Date getHearing_date() {
        return hearing_date;
    }

    /**
     * @param hearing_date the hearing_date to set
     */
    public void setHearing_date(Date hearing_date) {
        this.hearing_date = hearing_date;
    }

    /**
     * @return the court_name
     */
    public String getCourt_name() {
        return court_name;
    }

    /**
     * @param court_name the court_name to set
     */
    public void setCourt_name(String court_name) {
        this.court_name = court_name;
    }

    /**
     * @return the migistrate
     */
    public String getMigistrate() {
        return migistrate;
    }

    /**
     * @param migistrate the migistrate to set
     */
    public void setMigistrate(String migistrate) {
        this.migistrate = migistrate;
    }
}
