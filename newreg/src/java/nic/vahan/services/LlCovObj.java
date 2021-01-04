/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services;

import java.io.Serializable;
import java.util.Date;

public class LlCovObj implements Serializable {

    private String lcLicno;
    private String olacd;
    private String olaName;
    private String lcDlno;
    int lcCovcd;
    private String lcCovAbbr;
    private String vecatg;
    private String lcCovDesc;

    public String getVecatg() {
        return vecatg;
    }

    public void setVecatg(String vecatg) {
        this.vecatg = vecatg;
    }

    public String getLcCovDesc() {
        return lcCovDesc;
    }

    public void setLcCovDesc(String lcCovDesc) {
        this.lcCovDesc = lcCovDesc;
    }

    public String getLcLicno() {
        return lcLicno;
    }

    public void setLcLicno(String lcLicno) {
        this.lcLicno = lcLicno;
    }

    public int getLcCovcd() {
        return lcCovcd;
    }

    public void setLcCovcd(int lcCovcd) {
        this.lcCovcd = lcCovcd;
    }

    public String getLcCovAbbr() {
        return lcCovAbbr;
    }

    public void setLcCovAbbr(String lcCovAbbr) {
        this.lcCovAbbr = lcCovAbbr;
    }

    public String getOlacd() {
        return olacd;
    }

    public void setOlacd(String olacd) {
        this.olacd = olacd;
    }

    public String getOlaName() {
        return olaName;
    }

    public void setOlaName(String olaName) {
        this.olaName = olaName;
    }

    public String getLcDlno() {
        return lcDlno;
    }

    public void setLcDlno(String lcDlno) {
        this.lcDlno = lcDlno;
    }
}