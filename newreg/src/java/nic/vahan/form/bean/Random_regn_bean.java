/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.dobj.NumberDetail_dobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.NewVehicleNo;
import org.apache.log4j.Logger;

/**
 *
 * @author nic5912
 */
@ManagedBean(name = "random_regn_bean")
@ViewScoped
public class Random_regn_bean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(Random_regn_bean.class);
    private ArrayList<NumberDetail_dobj> applArrayList = new ArrayList<>();
    private Map<String, List<NumberDetail_dobj>> seriesWiseList = new HashMap<>();
    int offCd = 0;
    String stateCd = null;
    String alertMsg = null;
    private boolean optionGenNumber = false;

    public Random_regn_bean() {
        offCd = Util.getSelectedSeat().getOff_cd();
        stateCd = Util.getUserStateCode();
        if (offCd == 0 || stateCd == null || stateCd.isEmpty()) {
            //throw vahan Exception
        }
    }

    @PostConstruct
    public void getNewVehicleNumberToBeAssigned() {
        try {
            NewVehicleNo newVehicleNo = new NewVehicleNo();
            seriesWiseList = newVehicleNo.getNewVehicleNumberToBeAssigned(offCd, stateCd);

            if (seriesWiseList != null && !seriesWiseList.isEmpty()) {
                optionGenNumber = true;
            }

        } catch (VahanException vex) {
            alertMsg = "Exception Occurred due to " + vex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, alertMsg, alertMsg));
        } catch (Exception ex) {
            alertMsg = "Something Went Wrong, Please Contact to the System Administrator.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, alertMsg, alertMsg));
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public List getSeriesList() {
        return new ArrayList(seriesWiseList.entrySet());
    }

    public List<NumberDetail_dobj> getApplicationListSeriesWise(String key) {
        return new ArrayList<>(seriesWiseList.get(key));
    }

    public String getSeriesDetails(String prefix_series) {
        String series_details;
        try {
            String retValues[] = NewVehicleNo.getSeriesDetails(prefix_series);

            // below is is positional parameter information for the developer. 
            //retValues[0] = prefix_series
            //retValues[1] = running_no
            //retValues[2] = usedper
            //retValues[3] = upper_range_no
            //retValues[4] = next_prefix_series

            String nextseriesPrefix = retValues[4] != null && !retValues[4].equals("") ? retValues[4] : "Not Available";
            if (retValues[0] != null && !retValues[0].equals("")) {
                series_details = "Series [ " + retValues[0] + " ]  Running No. [ " + retValues[1] + "  ] Percentage Used  [" + retValues[2] + " % ]  Next Series [ " + nextseriesPrefix + "]";
            } else {
                series_details = "Details Not Available";
            }

        } catch (Exception e) {
            series_details = "Details Not Available";
        }
        return series_details;
    }

    public void assignNumber() {

        try {
            NewVehicleNo newVehicleNo = new NewVehicleNo();
            newVehicleNo.assignNewVehicleNumberRandom(seriesWiseList, Util.getSelectedSeat().getOff_cd(), null, Util.getUserStateCode());
            optionGenNumber = false;
        } catch (VahanException vex) {
            alertMsg = "Exception Occurred due to " + vex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, alertMsg, alertMsg));
        } catch (Exception ex) {
            alertMsg = "Problem in Generationg Registration No, Please Contact to the System Administrator.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, alertMsg, alertMsg));
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    /**
     * @return the applArrayList
     */
    public ArrayList<NumberDetail_dobj> getApplArrayList() {
        return applArrayList;
    }

    /**
     * @param applArrayList the applArrayList to set
     */
    public void setApplArrayList(ArrayList<NumberDetail_dobj> applArrayList) {
        this.applArrayList = applArrayList;
    }

    /**
     * @return the seriesWiseList
     */
    public Map<String, List<NumberDetail_dobj>> getSeriesWiseList() {
        return seriesWiseList;
    }

    /**
     * @param seriesWiseList the seriesWiseList to set
     */
    public void setSeriesWiseList(Map<String, List<NumberDetail_dobj>> seriesWiseList) {
        this.seriesWiseList = seriesWiseList;
    }

    /**
     * @return the optionGenNumber
     */
    public boolean isOptionGenNumber() {
        return optionGenNumber;
    }

    /**
     * @param optionGenNumber the optionGenNumber to set
     */
    public void setOptionGenNumber(boolean optionGenNumber) {
        this.optionGenNumber = optionGenNumber;
    }
}
