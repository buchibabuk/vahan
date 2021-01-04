/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.vahan.form.dobj.SCDetailsDobj;
import nic.vahan.form.impl.SCDetailsImpl;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC075
 */
@ManagedBean(name = "smartcard_bean")
@ViewScoped
public class SCDetailsBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(SCDetailsBean.class);
    private SCDetailsDobj dobj = null;
    private String tabTitle = null;

    public SCDetailsDobj setRegNo(String regnNo, String stateCd, int offCd, boolean smartcardStatus) {
        try {
            dobj = SCDetailsImpl.getSCDetails(regnNo, smartcardStatus, stateCd, offCd);
            setTabTitle(dobj.getTabTitle());
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        return dobj;
    }

    /**
     * @return the dobj
     */
    public SCDetailsDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(SCDetailsDobj dobj) {
        this.dobj = dobj;
    }

    /**
     * @return the tabTitle
     */
    public String getTabTitle() {
        return tabTitle;
    }

    /**
     * @param tabTitle the tabTitle to set
     */
    public void setTabTitle(String tabTitle) {
        this.tabTitle = tabTitle;
    }
}
