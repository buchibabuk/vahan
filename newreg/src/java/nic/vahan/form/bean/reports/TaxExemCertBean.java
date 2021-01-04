/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.TableConstants;


import nic.vahan.form.dobj.reports.TaxExemCertDobj;
import nic.vahan.form.impl.reports.TaxExemCertImpl;
import nic.vahan.server.CommonUtils;

import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Mohd Afzal
 */
@ManagedBean(name = "taxExemCertBean")
@ViewScoped
public class TaxExemCertBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(TaxExemCertBean.class);
    private List<TaxExemCertDobj> filteredSeat = null;
    private SessionVariables sessionVariables;
    private String vahanMessages = null;
    private String appl_no;
    private String regn_no;

    @PostConstruct
    public void init() {
        try {
            setSessionVariables(new SessionVariables());
            if (getSessionVariables() == null || CommonUtils.isNullOrBlank(getSessionVariables().getStateCodeSelected())
                    || getSessionVariables().getOffCodeSelected() == 0 || getSessionVariables().getActionCodeSelected() == 0) {
                setVahanMessages("Session time Out");
                return;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
    }

    public void getTaxExemDetails() {
        TaxExemCertDobj dobj = null;
        try {
            if ((regn_no.trim().equalsIgnoreCase("") && getAppl_no().trim().equalsIgnoreCase("")) || (regn_no.trim().equalsIgnoreCase("") && getAppl_no().trim().equalsIgnoreCase(""))) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration No should not be Blank !!!"));
                return;
            }
            dobj = TaxExemCertImpl.getTaxExemCertDetails(getSessionVariables(), regn_no);
            if (dobj == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "There is no record found !!!"));
                return;
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
        //RequestContext rc = RequestContext.getCurrentInstance();
        PrimeFaces.current().executeScript("PF('printTaxExemCertificate').show()");
    }

    public String printTaxExemCertificate() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("Tax_exem_regn_no", regn_no);
        return "PrintTaxExemCertificateReport";
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

    /**
     * @return the vahanMessages
     */
    public String getVahanMessages() {
        return vahanMessages;
    }

    /**
     * @param vahanMessages the vahanMessages to set
     */
    public void setVahanMessages(String vahanMessages) {
        this.vahanMessages = vahanMessages;
    }

    /**
     * @return the appl_no
     */
    public String getAppl_no() {
        return appl_no;
    }

    /**
     * @param appl_no the appl_no to set
     */
    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    /**
     * @return the regn_no
     */
    public String getRegn_no() {
        return regn_no;
    }

    /**
     * @param regn_no the regn_no to set
     */
    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }
}
