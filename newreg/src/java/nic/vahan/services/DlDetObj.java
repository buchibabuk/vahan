/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author acer
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DlDetObj implements Serializable, Comparator<DlDetObj> {

    private String erormsg;
    private int errorcd;
    private BiolicObj bioObj;
    private DlicenceObj dlobj;
    private DlCovObj[] dlcovs;
    private DlCovObj dlcovses;
    private String dbLoc;

    public DlCovObj getDlcovses() {
        return dlcovses;
    }

    public void setDlcovses(DlCovObj dlcovses) {
        this.dlcovses = dlcovses;
    }

    public String getDbLoc() {
        return dbLoc;
    }

    public void setDbLoc(String dbLoc) {
        this.dbLoc = dbLoc;
    }

    public DlicenceObj getDlobj() {
        return dlobj;
    }

    public void setDlobj(DlicenceObj dlobj) {
        this.dlobj = dlobj;
    }

    public BiolicObj getBioObj() {
        return bioObj;
    }

    public void setBioObj(BiolicObj bioObj) {
        this.bioObj = bioObj;
    }

    public String getErormsg() {
        return erormsg;
    }

    public void setErormsg(String erormsg) {
        this.erormsg = erormsg;
    }

    public int getErrorcd() {
        return errorcd;
    }

    public void setErrorcd(int errorcd) {
        this.errorcd = errorcd;
    }

    public DlCovObj[] getDlcovs() {
        return dlcovs;
    }

    public void setDlcovs(DlCovObj[] dlcovs) {
        this.dlcovs = dlcovs;
    }
//	//This method is used to Short Object Based on Date
//	@Override

    public int compare(DlDetObj o1, DlDetObj o2) {
        Date firstDate = o1.getDlobj().getDlIssuedt();
        Date secondDate = o2.getDlobj().getDlIssuedt();
        return firstDate.compareTo(secondDate);
    }
}
