/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.reports.TaxExemCertDobj;
import nic.vahan.form.impl.reports.TaxExemptionCertificateReportImpl;
import nic.vahan.server.CommonUtils;

import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Mohd Afzal
 */
@ManagedBean(name = "taxExemCertReportBean")
@ViewScoped
public class TaxExemptionCertificateReportBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(TaxExemptionCertificateReportBean.class);

    /**
     * @return the LOGGER
     */
    public static Logger getLOGGER() {
        return LOGGER;
    }

    /**
     * @param aLOGGER the LOGGER to set
     */
    public static void setLOGGER(Logger aLOGGER) {
        LOGGER = aLOGGER;
    }
    private TaxExemCertDobj dobj = null;
    private String text;
    private SessionVariables sessionVariables = null;

    public TaxExemptionCertificateReportBean() {
        sessionVariables = new SessionVariables();
        if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                || sessionVariables.getOffCodeSelected() == 0 || CommonUtils.isNullOrBlank(sessionVariables.getUserCatgForLoggedInUser())
                || CommonUtils.isNullOrBlank(sessionVariables.getEmpCodeLoggedIn())) {
            return;
        }
        Map map;
        map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        String regn_no = (String) map.get("Tax_exem_regn_no");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("Tax_exem_regn_no");
        try {
            dobj = TaxExemptionCertificateReportImpl.getTaxExemptionReportDobj(regn_no, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
            if (dobj != null) {
                setText("Regn. No." + dobj.getRegn_no() + " Chassis No." + dobj.getChasi_no() + " Engine No." + dobj.getEngine_no() + " Valid From." + dobj.getFrom_date() + " Valid Upto." + dobj.getUpto_date());
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
    }

    /**
     * @return the dobj
     */
    public TaxExemCertDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(TaxExemCertDobj dobj) {
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
     * @return the sessionVariables
     */
    public SessionVariables getSessionVariables() {
        return sessionVariables;
    }

    /**
     * @param sessionVariables the sessionVariables to set
     */
    public void setSessionVariables(SessionVariables sessionVariables) {
        this.sessionVariables = sessionVariables;
    }
}
