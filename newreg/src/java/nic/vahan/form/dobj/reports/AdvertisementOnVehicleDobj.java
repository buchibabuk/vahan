/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.reports;

import java.io.Serializable;

/**
 *
 * @author Administrator
 */
public class AdvertisementOnVehicleDobj implements Serializable {

    private String appl_no;
    private String regn_no;
    private String StateName;
    private String fron_date;
    private String upto_date;
    private String offName;
    private String header;
    private String subHeader;
    private String printedOn;

    /**
     * @return the regn_no
     */
    public String getRegn_no() {
        return regn_no;
    }

    /**
     * @param regn_no the regn_no to set
     */
    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    /**
     * @return the StateName
     */
    public String getStateName() {
        return StateName;
    }

    /**
     * @param StateName the StateName to set
     */
    public void setStateName(String StateName) {
        this.StateName = StateName;
    }

    /**
     * @return the fron_date
     */
    public String getFron_date() {
        return fron_date;
    }

    /**
     * @param fron_date the fron_date to set
     */
    public void setFron_date(String fron_date) {
        this.fron_date = fron_date;
    }

    /**
     * @return the upto_date
     */
    public String getUpto_date() {
        return upto_date;
    }

    /**
     * @param upto_date the upto_date to set
     */
    public void setUpto_date(String upto_date) {
        this.upto_date = upto_date;
    }

    /**
     * @return the offName
     */
    public String getOffName() {
        return offName;
    }

    /**
     * @param offName the offName to set
     */
    public void setOffName(String offName) {
        this.offName = offName;
    }

    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * @return the subHeader
     */
    public String getSubHeader() {
        return subHeader;
    }

    /**
     * @param subHeader the subHeader to set
     */
    public void setSubHeader(String subHeader) {
        this.subHeader = subHeader;
    }

    /**
     * @return the printedOn
     */
    public String getPrintedOn() {
        return printedOn;
    }

    /**
     * @param printedOn the printedOn to set
     */
    public void setPrintedOn(String printedOn) {
        this.printedOn = printedOn;
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
}
