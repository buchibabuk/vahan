/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.dobj.reports.TaxDefaulterListDobj;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author afzal
 */
@ManagedBean(name = "TDNoticeBean")
@ViewScoped
public class TaxDefaulterNoticeBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(TaxDefaulterNoticeBean.class);
    private List<TaxDefaulterListDobj> taxDNoticeDobjList = new ArrayList<>();
    private List<TaxDefaulterListDobj> selectedTDNlist = new ArrayList<TaxDefaulterListDobj>();
    private String returnURL = null;
    private boolean renderPermanantAddress = false;

    public TaxDefaulterNoticeBean() {
        try {
            Map map;
            map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            TaxDefaulterListDobj dobj = (TaxDefaulterListDobj) map.get("TDNDobj");
            List<TaxDefaulterListDobj> printDefaulterList = (List<TaxDefaulterListDobj>) map.get("printDefaulterList");
            String state_cd = Util.getUserStateCode();
            if ("KL".contains(state_cd)) {
                setRenderPermanantAddress(true);
            } else {
                setRenderPermanantAddress(false);
            }
            int off_cd = Util.getSelectedSeat().getOff_cd();
            if (dobj != null) {
                List<TaxDefaulterListDobj> returnTDNlist = PrintDocImpl.getTaxDNoticeReportDobj(state_cd, off_cd, dobj);
                if (!returnTDNlist.isEmpty()) {
                    setSelectedTDNlist(returnTDNlist);
                    setReturnURL("/ui/reports/formTaxDefaulterList.xhtml?faces-redirect=true");
                    map.remove("TDNDobj");
                } else {
                    setReturnURL("/ui/reports/formTaxDefaulterList.xhtml?faces-redirect=true");
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert !", "Tax Amount not update yet"));
                    return;
                }
            } else if (printDefaulterList != null && !printDefaulterList.isEmpty()) {
                setSelectedTDNlist(printDefaulterList);
                setReturnURL("/ui/tax/form_road_tax_collection.xhtml?faces-redirect=true");
                map.remove("printDefaulterList");
            } else {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert !", "Tax Amount not update yet"));
                setReturnURL("/ui/tax/form_road_tax_collection.xhtml?faces-redirect=true");
                setSelectedTDNlist(null);
                return;
            }

        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

    }

    @PostConstruct
    public void init() {
    }

    /**
     * @return the taxDNoticeDobjList
     */
    public List<TaxDefaulterListDobj> getTaxDNoticeDobjList() {
        return taxDNoticeDobjList;
    }

    /**
     * @param taxDNoticeDobjList the taxDNoticeDobjList to set
     */
    public void setTaxDNoticeDobjList(List<TaxDefaulterListDobj> taxDNoticeDobjList) {
        this.taxDNoticeDobjList = taxDNoticeDobjList;
    }

    /**
     * @return the selectedTDNlist
     */
    public List<TaxDefaulterListDobj> getSelectedTDNlist() {
        return selectedTDNlist;
    }

    /**
     * @param selectedTDNlist the selectedTDNlist to set
     */
    public void setSelectedTDNlist(List<TaxDefaulterListDobj> selectedTDNlist) {
        this.selectedTDNlist = selectedTDNlist;
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

    /**
     * @return the renderPermanantAddress
     */
    public boolean isRenderPermanantAddress() {
        return renderPermanantAddress;
    }

    /**
     * @param renderPermanantAddress the renderPermanantAddress to set
     */
    public void setRenderPermanantAddress(boolean renderPermanantAddress) {
        this.renderPermanantAddress = renderPermanantAddress;
    }
}
