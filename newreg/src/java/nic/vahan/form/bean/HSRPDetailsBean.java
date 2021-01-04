/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.vahan.form.dobj.hsrp.HSRP_dobj;
import nic.vahan.form.impl.HSRPDetailsImpl;

/**
 *
 * @author tranC075
 */
@ManagedBean(name = "hsrp_bean")
@ViewScoped
public class HSRPDetailsBean implements Serializable {

    private String frontLaser;
    private String rearLaser;
    private String hsrpFixdate;
    private boolean ishsrp = false;

    public HSRP_dobj setRegNo(String regnNo, String stateCd, int offCd) {
        HSRP_dobj hsrp_dobj = HSRPDetailsImpl.getHsrpDetails(stateCd, offCd, regnNo);
        if (hsrp_dobj != null) {
            setFrontLaser(hsrp_dobj.getHsrp_no_front());
            setRearLaser(hsrp_dobj.getHsrp_no_back());
            setHsrpFixdate(hsrp_dobj.getHsrp_fix_dt());
        }
        return hsrp_dobj;
    }

    public boolean setHSRPFlag(String stateCd, int offCd) {
        setIshsrp(HSRPDetailsImpl.isHsrp(stateCd, offCd));
        return isIshsrp();
    }

    /**
     * @return the frontLaser
     */
    public String getFrontLaser() {
        return frontLaser;
    }

    /**
     * @param frontLaser the frontLaser to set
     */
    public void setFrontLaser(String frontLaser) {
        this.frontLaser = frontLaser;
    }

    /**
     * @return the rearLaser
     */
    public String getRearLaser() {
        return rearLaser;
    }

    /**
     * @param rearLaser the rearLaser to set
     */
    public void setRearLaser(String rearLaser) {
        this.rearLaser = rearLaser;
    }

    /**
     * @return the ishsrp
     */
    public boolean isIshsrp() {
        return ishsrp;
    }

    /**
     * @param ishsrp the ishsrp to set
     */
    public void setIshsrp(boolean ishsrp) {
        this.ishsrp = ishsrp;
    }

    /**
     * @return the hsrpFixdate
     */
    public String getHsrpFixdate() {
        return hsrpFixdate;
    }

    /**
     * @param hsrpFixdate the hsrpFixdate to set
     */
    public void setHsrpFixdate(String hsrpFixdate) {
        this.hsrpFixdate = hsrpFixdate;
    }
}
