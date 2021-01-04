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
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.reports.TaxEndorsementPrintDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.reports.TaxEndorsementPrintImpl;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Mohd Afzal
 */
@ManagedBean(name = "taxEndorsementPrintBean")
@ViewScoped
public class TaxEndorsementPrintBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(TaxEndorsementPrintBean.class);
    private String appl_regn_no;
    private String selectedRadioBtn = "";
    private String searchLabel = "";
    private int maxlenght;
    private TaxEndorsementPrintDobj dobj = null;
    private TmConfigurationDobj configurationDobj = null;
    private SessionVariables sessionVariables;
    private String vahanMessages = null;

    public TaxEndorsementPrintBean() {
        sessionVariables = new SessionVariables();
        if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
            vahanMessages = "Session time Out";
            return;
        }
        selectedRadioBtn = "R";
        searchLabel = "Registration No";
        setMaxlenght(10);

    }

    public void radioBtnListener() {
        String radioBtnvalue = selectedRadioBtn;
        if (radioBtnvalue.equalsIgnoreCase("R")) {
            setSearchLabel("Registration No");
            setMaxlenght(10);
        } else {
            setSearchLabel("Application No");
            setMaxlenght(16);
        }
    }

    public void confirmprintCertificate() {
        String radioBtnvalue = selectedRadioBtn;
        if (this.getAppl_regn_no() == null || this.getAppl_regn_no().trim().equalsIgnoreCase("")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration / Application No should not be Blank!"));
            return;
        }
        TaxEndorsementPrintImpl tax_Endorsement_Impl;
        try {
            tax_Endorsement_Impl = new TaxEndorsementPrintImpl();
            setConfigurationDobj(Util.getTmConfiguration());

            setDobj(tax_Endorsement_Impl.getTaxEndorsementDetails(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), this.getAppl_regn_no().trim().toUpperCase(), selectedRadioBtn));

            if (getDobj() == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Application / Registration does not exist or you are not authorized to print NOC Slip for this application.!"));
                return;
            }
            //RequestContext ca = RequestContext.getCurrentInstance();
            PrimeFaces.current().ajax().update("frm_print_taxend");
            PrimeFaces.current().executeScript("PF('printTaxEndCertificate').show()");
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert!", ve.getMessage()));
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
    }

    public String printTaxEndorsementCertificate() {
        Map map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        map.put("taxEndrosmentDobj", getDobj());
        return "printTaxEndDetails";


    }

    /**
     * @return the appl_regn_no
     */
    public String getAppl_regn_no() {
        return appl_regn_no;
    }

    /**
     * @param appl_regn_no the appl_regn_no to set
     */
    public void setAppl_regn_no(String appl_regn_no) {
        this.appl_regn_no = appl_regn_no;
    }

    /**
     * @return the selectedRadioBtn
     */
    public String getSelectedRadioBtn() {
        return selectedRadioBtn;
    }

    /**
     * @param selectedRadioBtn the selectedRadioBtn to set
     */
    public void setSelectedRadioBtn(String selectedRadioBtn) {
        this.selectedRadioBtn = selectedRadioBtn;
    }

    /**
     * @return the searchLabel
     */
    public String getSearchLabel() {
        return searchLabel;
    }

    /**
     * @param searchLabel the searchLabel to set
     */
    public void setSearchLabel(String searchLabel) {
        this.searchLabel = searchLabel;
    }

    /**
     * @return the maxlenght
     */
    public int getMaxlenght() {
        return maxlenght;
    }

    /**
     * @param maxlenght the maxlenght to set
     */
    public void setMaxlenght(int maxlenght) {
        this.maxlenght = maxlenght;
    }

    /**
     * @return the dobj
     */
    public TaxEndorsementPrintDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(TaxEndorsementPrintDobj dobj) {
        this.dobj = dobj;
    }

    /**
     * @return the configurationDobj
     */
    public TmConfigurationDobj getConfigurationDobj() {
        return configurationDobj;
    }

    /**
     * @param configurationDobj the configurationDobj to set
     */
    public void setConfigurationDobj(TmConfigurationDobj configurationDobj) {
        this.configurationDobj = configurationDobj;
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
}
