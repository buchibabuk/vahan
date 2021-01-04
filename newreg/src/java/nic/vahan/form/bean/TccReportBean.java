/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.vahan.form.dobj.TccDobj;
import nic.vahan.form.impl.TccReportImpl;
import org.apache.log4j.Logger;

/**
 *
 * @author acer
 */
@ManagedBean(name = "tccReportBean")
@ViewScoped
public class TccReportBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(TccReportBean.class);

    private List<TccDobj> tccList = new ArrayList<>();

    public void setRegNo(String regNo, int vhClass, String stateCd, int offCd) {
        try {
            tccList = TccReportImpl.getTccData(regNo, stateCd, offCd);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    /**
     * @return the tccList
     */
    public List<TccDobj> getTccList() {
        return tccList;
    }

    /**
     * @param tccList the tccList to set
     */
    public void setTccList(List<TccDobj> tccList) {
        this.tccList = tccList;
    }
}
