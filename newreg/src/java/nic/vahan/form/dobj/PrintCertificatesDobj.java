/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author tranC095
 */
public class PrintCertificatesDobj implements Serializable {

    private String appl_no;
    private String regno;
    private int purCd;
    private String purCdDescr;
    private List<PrintCertificatesDobj> list = new ArrayList();
    private int action_cd;
    private String state_cd;
    private int off_cd;
    private String printed_on;
    private String printed_by;
    private String regn_type;
    private String printForm;
    private Date regn_date;
    private int mfgYearMonthYYYYMM;

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public int getPurCd() {
        return purCd;
    }

    public void setPurCd(int purCd) {
        this.purCd = purCd;
    }

    public String getPurCdDescr() {
        return purCdDescr;
    }

    public void setPurCdDescr(String purCdDescr) {
        this.purCdDescr = purCdDescr;
    }

    public List<PrintCertificatesDobj> getList() {
        return list;
    }

    public void setList(List<PrintCertificatesDobj> list) {
        this.list = list;
    }

    public int getAction_cd() {
        return action_cd;
    }

    public void setAction_cd(int action_cd) {
        this.action_cd = action_cd;
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

    /**
     * @return the printed_on
     */
    public String getPrinted_on() {
        return printed_on;
    }

    /**
     * @param printed_on the printed_on to set
     */
    public void setPrinted_on(String printed_on) {
        this.printed_on = printed_on;
    }

    /**
     * @return the printed_by
     */
    public String getPrinted_by() {
        return printed_by;
    }

    /**
     * @param printed_by the printed_by to set
     */
    public void setPrinted_by(String printed_by) {
        this.printed_by = printed_by;
    }

    /**
     * @return the regn_type
     */
    public String getRegn_type() {
        return regn_type;
    }

    /**
     * @param regn_type the regn_type to set
     */
    public void setRegn_type(String regn_type) {
        this.regn_type = regn_type;
    }

    /**
     * @return the printForm
     */
    public String getPrintForm() {
        return printForm;
    }

    /**
     * @param printForm the printForm to set
     */
    public void setPrintForm(String printForm) {
        this.printForm = printForm;
    }

    /**
     * @return the regn_date
     */
    public Date getRegn_date() {
        return regn_date;
    }

    /**
     * @param regn_date the regn_date to set
     */
    public void setRegn_date(Date regn_date) {
        this.regn_date = regn_date;
    }

    /**
     * @return the mfgYearMonthYYYYMM
     */
    public int getMfgYearMonthYYYYMM() {
        return mfgYearMonthYYYYMM;
    }

    /**
     * @param mfgYearMonthYYYYMM the mfgYearMonthYYYYMM to set
     */
    public void setMfgYearMonthYYYYMM(int mfgYearMonthYYYYMM) {
        this.mfgYearMonthYYYYMM = mfgYearMonthYYYYMM;
    }
}
