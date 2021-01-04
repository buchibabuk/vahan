/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tranC103
 */
public class State {

    private String stateCode;
    private String stateDescr;
    private int egovCode;
    private List disrict = new ArrayList();
    private List office = new ArrayList();
    private List court = new ArrayList();
    private List offence = new ArrayList();
    private List section = new ArrayList();
    private List da = new ArrayList();
    private List otherCriteria = new ArrayList();
    private List magistrate = new ArrayList();
    private List speed_governer_list = new ArrayList();

    /**
     * @return the stateCode
     */
    public String getStateCode() {
        return stateCode;
    }

    /**
     * @param stateCode the stateCode to set
     */
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    /**
     * @return the stateDescr
     */
    public String getStateDescr() {
        return stateDescr;
    }

    /**
     * @param stateDescr the stateDescr to set
     */
    public void setStateDescr(String stateDescr) {
        this.stateDescr = stateDescr;
    }

    /**
     * @return the egovCode
     */
    public int getEgovCode() {
        return egovCode;
    }

    /**
     * @param egovCode the egovCode to set
     */
    public void setEgovCode(int egovCode) {
        this.egovCode = egovCode;
    }

    /**
     * @return the disrict
     */
    public List getDisrict() {
        return disrict;
    }

    /**
     * @param disrict the disrict to set
     */
    public void setDisrict(List disrict) {
        this.disrict = disrict;
    }

    /**
     * @return the office
     */
    public List getOffice() {
        return new ArrayList(office);
    }

    /**
     * @param office the office to set
     */
    public void setOffice(List office) {
        this.office = office;
    }

    /**
     * @return the court
     */
    public List getCourt() {
        return court;
    }

    /**
     * @param court the court to set
     */
    public void setCourt(List court) {
        this.court = court;
    }

    /**
     * @return the offence
     */
    public List getOffence() {
        return offence;
    }

    /**
     * @param offence the offence to set
     */
    public void setOffence(List offence) {
        this.offence = offence;
    }

    /**
     * @return the section
     */
    public List getSection() {
        return section;
    }

    /**
     * @param section the section to set
     */
    public void setSection(List section) {
        this.section = section;
    }

    /**
     * @return the da
     */
    public List getDa() {
        return da;
    }

    /**
     * @param da the da to set
     */
    public void setDa(List da) {
        this.da = da;
    }

    /**
     * @return the otherCriteria
     */
    public List getOtherCriteria() {
        return otherCriteria;
    }

    /**
     * @param otherCriteria the otherCriteria to set
     */
    public void setOtherCriteria(List otherCriteria) {
        this.otherCriteria = otherCriteria;
    }

    public List getMagistrate() {
        return magistrate;
    }

    public void setMagistrate(List magistrate) {
        this.magistrate = magistrate;
    }

    public List getSpeed_governer_list() {
        return speed_governer_list;
    }

    public void setSpeed_governer_list(List speed_governer_list) {
        this.speed_governer_list = speed_governer_list;
    }
}
