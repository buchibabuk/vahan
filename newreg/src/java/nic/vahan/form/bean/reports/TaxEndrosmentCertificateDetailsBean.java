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
import nic.vahan.form.dobj.reports.TaxEndorsementPrintDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author Mohd Afzal
 */
@ManagedBean(name = "taxEndrosmentCertDtlsBean")
@RequestScoped
public class TaxEndrosmentCertificateDetailsBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(TaxEndrosmentCertificateDetailsBean.class);
    private TaxEndorsementPrintDobj taxEndrCertDobj = new TaxEndorsementPrintDobj();

    public TaxEndrosmentCertificateDetailsBean() {
        try {
            Map map;
            map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            TaxEndorsementPrintDobj dobj = (TaxEndorsementPrintDobj) map.get("taxEndrosmentDobj");
            if (dobj != null) {
                setTaxEndrCertDobj(dobj);
                map.remove("taxEndrosmentDobj");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    /**
     * @return the taxEndrCertDobj
     */
    public TaxEndorsementPrintDobj getTaxEndrCertDobj() {
        return taxEndrCertDobj;
    }

    /**
     * @param taxEndrCertDobj the taxEndrCertDobj to set
     */
    public void setTaxEndrCertDobj(TaxEndorsementPrintDobj taxEndrCertDobj) {
        this.taxEndrCertDobj = taxEndrCertDobj;
    }
}
