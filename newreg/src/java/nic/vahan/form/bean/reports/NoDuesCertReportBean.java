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
import nic.vahan.form.dobj.reports.NoDuesCertReportDobj;
import nic.vahan.form.impl.reports.NoDuesCertReportImpl;
import org.apache.log4j.Logger;

/**
 *
 * @author Mohd Afzal
 */
@ManagedBean(name = "NODuesCertBean")
@RequestScoped
public class NoDuesCertReportBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(NoDuesCertReportBean.class);
    private NoDuesCertReportDobj dobj = null;

    public NoDuesCertReportBean() {
        Map mapReport;
        mapReport = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        String appl_no = (String) mapReport.get("appl_no");
        try {
            if (appl_no != null || !appl_no.isEmpty()) {
                dobj = NoDuesCertReportImpl.getNoDuesCertDetails(appl_no);
                if (dobj == null) {
                    return;
                }
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
    public NoDuesCertReportDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(NoDuesCertReportDobj dobj) {
        this.dobj = dobj;
    }
}
