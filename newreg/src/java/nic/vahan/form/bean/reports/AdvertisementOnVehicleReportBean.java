/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.vahan.form.dobj.reports.AdvertisementOnVehicleDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "advOnVehicleReportBean")
@ViewScoped
public class AdvertisementOnVehicleReportBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(AdvertisementOnVehicleReportBean.class);
    private AdvertisementOnVehicleDobj dobj = null;

    public AdvertisementOnVehicleReportBean() {
        Map map;
        map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        dobj = (AdvertisementOnVehicleDobj) map.get("ADVDobj");
    }

    /**
     * @return the dobj
     */
    public AdvertisementOnVehicleDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(AdvertisementOnVehicleDobj dobj) {
        this.dobj = dobj;
    }
}
