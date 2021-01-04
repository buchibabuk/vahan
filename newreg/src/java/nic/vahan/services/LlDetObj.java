/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services;

import java.io.Serializable;
import java.util.Date;

public class LlDetObj implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String errormessage;
    private int errorcd;
    private String dataDesc;
    private LlicenceObj llicenceObj;
    private LlCovObj[] llcovs;
    private BiolicObj bioObj;
    private String dbLoc;
    private Date todate;
    private Date frdate;
    private Date modtodate;
    private Date modfrdate;

    public String getDbLoc() {
        return dbLoc;
    }

    public void setDbLoc(String dbLoc) {
        this.dbLoc = dbLoc;
    }

    public LlicenceObj getLlicenceObj() {
        return llicenceObj;
    }

    public void setLlicenceObj(LlicenceObj llicenceObj) {
        this.llicenceObj = llicenceObj;
    }

    public String getDataDesc() {
        return dataDesc;
    }

    public void setDataDesc(String dataDesc) {
        this.dataDesc = dataDesc;
    }

    public BiolicObj getBioObj() {
        return bioObj;
    }

    public void setBioObj(BiolicObj bioObj) {
        this.bioObj = bioObj;
    }

    public String getErrormessage() {
        return errormessage;
    }

    public void setErrormessage(String errormessage) {
        this.errormessage = errormessage;
    }

    public int getErrorcd() {
        return errorcd;
    }

    public void setErrorcd(int errorcd) {
        this.errorcd = errorcd;
    }

    public LlCovObj[] getLlcovs() {
        return llcovs;
    }

    public void setLlcovs(LlCovObj[] llcovs) {
        this.llcovs = llcovs;
    }

    public Date getTodate() {
        return todate;
    }

    public void setTodate(Date todate) {
        this.todate = todate;
    }

    public Date getFrdate() {
        return frdate;
    }

    public void setFrdate(Date frdate) {
        this.frdate = frdate;
    }

    public Date getModtodate() {
        return modtodate;
    }

    public void setModtodate(Date modtodate) {
        this.modtodate = modtodate;
    }

    public Date getModfrdate() {
        return modfrdate;
    }

    public void setModfrdate(Date modfrdate) {
        this.modfrdate = modfrdate;
    }
}
