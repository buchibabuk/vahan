/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

/**
 *
 * @author siddhant
 */
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.vahan.form.dobj.reports.TaxDefaulterListDobj;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import nic.vahan.db.TableConstants;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

@ManagedBean(name = "printtaxdfltrRegnNowise")
@ViewScoped
public class TaxDefaulterReport implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(TaxDefaulterReport.class);
    private List<TaxDefaulterListDobj> taxDemandNoticeReportlist = new ArrayList<>();
    private String returnURL;

    @PostConstruct
    public void init() {
        try {
            Map map;
            map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            setTaxDemandNoticeReportlist((List<TaxDefaulterListDobj>) map.get("listPrintRc"));
            if (Util.getUserCategory().equals(TableConstants.USER_CATG_OFFICE_ADMIN)) {
                setReturnURL("/ui/reports/formTaxDNRequest.xhtml");
            } else if (Util.getUserCategory().equals(TableConstants.USER_CATG_OFF_STAFF)) {
                setReturnURL("/ui/reports/formTaxDefaulterList.xhtml");
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }

    }

    public List<TaxDefaulterListDobj> getTaxDemandNoticeReportlist() {
        return taxDemandNoticeReportlist;
    }

    public void setTaxDemandNoticeReportlist(List<TaxDefaulterListDobj> taxDemandNoticeReportlist) {
        this.taxDemandNoticeReportlist = taxDemandNoticeReportlist;
    }

    /**
     * @return the returnURL
     */
    public String getReturnURL() {
        return returnURL;
    }

    /**
     * @param returnURL the returnURL to set
     */
    public void setReturnURL(String returnURL) {
        this.returnURL = returnURL;
    }
}
