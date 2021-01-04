/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import nic.vahan.form.impl.NocPrintImpl;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.bean.PrintCertificatesBean;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.reports.NOCReportDobj;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Afzal
 */
@ManagedBean(name = "nocprintBean")
@ViewScoped
public class NocPrintBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(PrintCertificatesBean.class);
    private String appl_regn_no;
    private String regnRadiobtn = "";
    private String searchLabel = "";
    private int maxlenght;
    private String applNoExist = null;
    private NOCReportDobj dobj = null;
    private TmConfigurationDobj configurationDobj = null;

    public NocPrintBean() {
        regnRadiobtn = "R";
        searchLabel = "Registration No";
        setMaxlenght(10);
    }

    public void confirmprintCertificate() {
        String radioBtnvalue = regnRadiobtn;
        if (this.getAppl_regn_no() == null || this.getAppl_regn_no().trim().equalsIgnoreCase("")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration / Application No should not be Blank!"));
            return;
        }
        NocPrintImpl noc_Impl;
        try {
            noc_Impl = new NocPrintImpl();
            setConfigurationDobj(Util.getTmConfiguration());
            if (getConfigurationDobj() != null && getConfigurationDobj().getTmPrintConfgDobj() != null && getConfigurationDobj().getTmPrintConfgDobj().isPrint_no_dues_cert()) {
                setDobj(noc_Impl.isApplExistForNOC(this.getAppl_regn_no().trim().toUpperCase(), regnRadiobtn, getConfigurationDobj().getTmPrintConfgDobj().isPrint_no_dues_cert()));
            } else {
                setDobj(noc_Impl.isApplExistForNOC(this.getAppl_regn_no().trim().toUpperCase(), regnRadiobtn, false));
            }
            if (getDobj() == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Application / Registration does not exist or you are not authorized to print NOC Slip for this application.!"));
                return;
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert!", ve.getMessage()));
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
        //RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().ajax().update("frm_print_noc");
        PrimeFaces.current().executeScript("PF('printNOCCertificate').show()");

    }

    public String printCertificateNOC() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("appl_no", getDobj().getAppl_no());
        if (getDobj().isPrint_no_dues_cert()) {
            return "PrintNODuesCertReport";
        } else {
            return "NOCReport";
        }
    }

    public void radioBtnListener() {
        String radioBtnvalue = regnRadiobtn;
        if (radioBtnvalue.equalsIgnoreCase("R")) {
            setSearchLabel("Registration No");
            setMaxlenght(10);
        } else {
            setSearchLabel("Application No");
            setMaxlenght(16);
        }
    }

    /**
     * @return the regnRadiobtn
     */
    public String getRegnRadiobtn() {
        return regnRadiobtn;
    }

    /**
     * @param regnRadiobtn the regnRadiobtn to set
     */
    public void setRegnRadiobtn(String regnRadiobtn) {
        this.regnRadiobtn = regnRadiobtn;
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
     * @return the applNoExist
     */
    public String getApplNoExist() {
        return applNoExist;
    }

    /**
     * @param applNoExist the applNoExist to set
     */
    public void setApplNoExist(String applNoExist) {
        this.applNoExist = applNoExist;
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
}
