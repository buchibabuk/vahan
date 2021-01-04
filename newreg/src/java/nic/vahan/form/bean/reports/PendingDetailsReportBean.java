/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.vahan.form.dobj.reports.PendingReportDobj;
import nic.vahan.form.impl.PrintDocImpl;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC114
 */
@ManagedBean(name = "pendingdetailReportBean")
@ViewScoped
public class PendingDetailsReportBean implements Serializable {

    private Date appl_Date;
    private Date current_Date = new Date();
    private PendingReportDobj dobj = null;
    private static final Logger LOGGER = Logger.getLogger(nic.vahan.form.bean.reports.PendingDetailsReportBean.class);

    public void displayPendencyReport() {
        try {

            dobj = new PendingReportDobj();
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String dateappl = sdf1.format(appl_Date);
            java.util.Date appldate = sdf1.parse(dateappl);
            java.sql.Date appl_dte = new java.sql.Date(appldate.getTime());
            dobj = (PendingReportDobj) PrintDocImpl.getPendingDobj(appl_dte);

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);


        }
    }

    public Date getCurrent_Date() {
        return current_Date;
    }

    public void setCurrent_Date(Date current_Date) {
        this.current_Date = current_Date;
    }

    public Date getAppl_Date() {
        return appl_Date;
    }

    public void setAppl_Date(Date appl_Date) {
        this.appl_Date = appl_Date;
    }

    public PendingReportDobj getDobj() {
        return dobj;
    }

    public void setDobj(PendingReportDobj dobj) {
        this.dobj = dobj;
    }
}
