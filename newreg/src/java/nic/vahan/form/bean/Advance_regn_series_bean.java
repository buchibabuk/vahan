/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.form.dobj.Advance_regn_dobj;
import nic.vahan.form.impl.Advance_regn_series_Impl;
import nic.vahan.form.impl.Util;
//import org.primefaces.context.RequestContext;
import org.primefaces.PrimeFaces;

/**
 *
 * @author NICSI
 */
@ManagedBean(name = "advance_regn_series")
@ViewScoped
public class Advance_regn_series_bean implements Serializable {

    private List<Advance_regn_dobj> listRegnSeries = new ArrayList<Advance_regn_dobj>();
    private List<Advance_regn_dobj> filterRegnSeries;
    private Map<String, Integer> series = new LinkedHashMap<String, Integer>();
    private String series_val;
    private String prefix_series;
    private List<Advance_regn_series_bean> filteredSeat = null;
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Advance_regn_series_bean.class);

    public Advance_regn_series_bean() {
        loadAllAdvanceSeries();
    }

    public void save_Advance_Series() {
        String msg = "";
        try {
            if (Utility.isNullOrBlank(series_val)) {
                FacesMessage message = null;
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Please Select Series !"));
                return;
            }
            if (Utility.isNullOrBlank(prefix_series)) {
                FacesMessage message = null;
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Please Enter Prefix Series Number!"));
                return;
            }
            prefix_series = prefix_series.toUpperCase();
            Advance_regn_series_Impl advan_series = new Advance_regn_series_Impl();
            msg = advan_series.saveOrCheckAdvanceSeries(prefix_series, series_val, Util.getUserStateCode(), Util.getEmpCode(), Util.getUserOffCode());
            JSFUtils.setFacesMessage(msg, null, JSFUtils.INFO);
            setPrefix_series("");
            setSeries_val("");
            loadAllAdvanceSeries();

        } catch (VahanException e) {
            JSFUtils.setFacesMessage(e.getMessage(), null, JSFUtils.WARN);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            JSFUtils.setFacesMessage("Technical Error", null, JSFUtils.WARN);
        }
    }

    private void loadAllAdvanceSeries() {
        Advance_regn_series_Impl advan_series = new Advance_regn_series_Impl();
        try {
            series = advan_series.getRegnSeries();
            listRegnSeries = advan_series.fetchAllAdvanceRegnSeries();
        } catch (VahanException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ex.getMessage()));
        }
    }

    public Map<String, Integer> getSeries() {
        return series;
    }

    public void setSeries(Map<String, Integer> series) {
        this.series = series;
    }

    public String getSeries_val() {
        return series_val;
    }

    public void setSeries_val(String series_val) {
        this.series_val = series_val;
    }

    public String getPrefix_series() {
        return prefix_series;
    }

    public void setPrefix_series(String prifix_series) {
        this.prefix_series = prifix_series;
    }

    public List<Advance_regn_dobj> getListRegnSeries() {
        return listRegnSeries;
    }

    public void setListRegnSeries(List<Advance_regn_dobj> listRegnSeries) {
        this.listRegnSeries = listRegnSeries;
    }

    public List<Advance_regn_dobj> getFilterRegnSeries() {
        return filterRegnSeries;
    }

    public void setFilterRegnSeries(List<Advance_regn_dobj> filterRegnSeries) {
        this.filterRegnSeries = filterRegnSeries;
    }

    /**
     * @return the filteredSeat
     */
    public List<Advance_regn_series_bean> getFilteredSeat() {
        return filteredSeat;
    }

    /**
     * @param filteredSeat the filteredSeat to set
     */
    public void setFilteredSeat(List<Advance_regn_series_bean> filteredSeat) {
        this.filteredSeat = filteredSeat;
    }
}
