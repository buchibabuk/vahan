/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.dobj.reports.ScrappedVehicleReportDobj;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author afzal
 */
@ManagedBean(name = "scrappedvchReportBean")
@RequestScoped
public class ScrappedVehicleReportBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ScrappedVehicleReportBean.class);
    private ScrappedVehicleReportDobj dobj = null;
    private String text;

    public ScrappedVehicleReportBean() {
        try {
            Map map;
            map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            String oldregnno = (String) map.get("oldregnno");
            String statecd = (String) map.get("statecd");
            int offcd = (int) map.get("offcd");
            String new_regn_no = (String) map.get("newregn_no");
            String scrap_reason = (String) map.get("scrap_reason");
            dobj = PrintDocImpl.getScrapVehicleDobj(oldregnno.toUpperCase(), new_regn_no.toUpperCase(), statecd, offcd, Util.getEmpCode(), scrap_reason);
            if (dobj != null) {
                text = "Engine. No." + dobj.getEngno() + " Chassis No." + dobj.getChasino();
            }
        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    /**
     * @return the dobj
     */
    public ScrappedVehicleReportDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(ScrappedVehicleReportDobj dobj) {
        this.dobj = dobj;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }
}
