/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.dobj.reports.NOCReportDobj;
import nic.vahan.form.impl.PrintDocImpl;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author nic
 */
@ManagedBean(name = "NOCReportBean")
@RequestScoped
public class NOCReportBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(NOCReportBean.class);
    private NOCReportDobj dobj = null;
    private String text;
    private StreamedContent viewUserSign;

    public NOCReportBean() {
        Map mapReport;
        mapReport = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        String appl_no = (String) mapReport.get("appl_no");
        setDobj(new NOCReportDobj());
        viewUserSign = new DefaultStreamedContent();
        try {
            if (appl_no != null || !appl_no.isEmpty()) {
                dobj = PrintDocImpl.getNOCReportDobj(appl_no);
                if (dobj == null) {
                    return;
                }
                text = "Regn. No." + dobj.getRegn_no() + " Chassis No." + dobj.getChassi_no();
                if (dobj.getUserSign() != null && !dobj.getUserSign().equals("")) {
                    setViewUserSign(new DefaultStreamedContent(new ByteArrayInputStream(dobj.getUserSign())));
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
    public NOCReportDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(NOCReportDobj dobj) {
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

    /**
     * @return the viewUserSign
     */
    public StreamedContent getViewUserSign() {
        return viewUserSign;
    }

    /**
     * @param viewUserSign the viewUserSign to set
     */
    public void setViewUserSign(StreamedContent viewUserSign) {
        this.viewUserSign = viewUserSign;
    }
}
