/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.vahan.form.dobj.reports.KMSReportDobj;

/**
 *
 * @author tranC114
 */
@ManagedBean(name = "kmsReportBean")
@ViewScoped
public class KMSReportBean implements Serializable {

    private KMSReportDobj dobj = new KMSReportDobj();

    @PostConstruct
    public void init() {
        Map map;
        Date frm_dte;
        Date to_dte;
        map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        frm_dte = (Date) map.get("frm_dte");
        to_dte = (Date) map.get("to_dte");
        dobj = (KMSReportDobj) map.get("dobj");
    }

    public KMSReportBean() {
    }

    /**
     * @return the dobj
     */
    public KMSReportDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(KMSReportDobj dobj) {
        this.dobj = dobj;
    }
}
