/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.dobj.FancyAuctionDobj;
import nic.vahan.form.impl.FancyAuctionVerifyImpl;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;
import org.primefaces.component.commandbutton.CommandButton;

/**
 *
 * @author AMBRISH
 */
@ManagedBean(name = "fancyauctioninward")
@ViewScoped
public class FancyAuctionInwardBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(FancyAuctionEntryBean.class);
    private Map<String, List<FancyAuctionDobj>> fancyPendingInwadList = new HashMap<>();
    private List<FancyAuctionDobj> selectedFancyPendingInwadList = new ArrayList<>();
    private FancyAuctionVerifyImpl fancyImpl = new FancyAuctionVerifyImpl();
    private RowSet rsOpenSeriesDetails = null;
    private CommandButton btnInwardApplication = new CommandButton();

    @PostConstruct
    public void init() {
        try {

            getBtnInwardApplication().setDisabled(true);

            rsOpenSeriesDetails = fancyImpl.getOpenSeriesDetails(Util.getUserStateCode(), Util.getUserSeatOffCode());
            rsOpenSeriesDetails.beforeFirst();
            if (rsOpenSeriesDetails.next()) {
                String strdt = rsOpenSeriesDetails.getString("date_upto");
                java.text.SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                Date dt = sdf.parse(strdt);

                Date current_dt = new Date();
                if (current_dt.compareTo(dt) >= 0) {

                    setFancyPendingInwadList(fancyImpl.getFancyNumberListPendingForInward(Util.getUserSeatOffCode()));
                    if (fancyPendingInwadList.isEmpty()) {
                        getBtnInwardApplication().setValue("No Application to Inward");
                        getBtnInwardApplication().setDisabled(true);
                    } else {
                        getBtnInwardApplication().setValue("Inward Application");
                        getBtnInwardApplication().setDisabled(false);
                    }

                } else {
                    getBtnInwardApplication().setValue("Wait till - " + rsOpenSeriesDetails.getString("date_upto"));
                    getBtnInwardApplication().setDisabled(true);

                }

            }
            rsOpenSeriesDetails.beforeFirst();
        } catch (VahanException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        } catch (SQLException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Open Series For Fancy Number Not Found", null));
        } catch (ParseException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public List getRegnList() {
        return new ArrayList(fancyPendingInwadList.entrySet());
    }

    public List<FancyAuctionDobj> getApplicationListRegnWise(String key) {
        return new ArrayList<FancyAuctionDobj>(fancyPendingInwadList.get(key));
    }

    public void inwardApplication() {
        if (!fancyPendingInwadList.isEmpty()) {
            try {
                fancyImpl.processFancyNumberListPendingForInward(fancyPendingInwadList.keySet(), Util.getUserStateCode(), Util.getUserSeatOffCode());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Applications Inwarded Successfully", null));
                getBtnInwardApplication().setValue("No Application to Inward");
                getBtnInwardApplication().setDisabled(true);
            } catch (VahanException ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
            }
        }

    }

    /**
     * @return the fancyPendingInwadList
     */
    public Map<String, List<FancyAuctionDobj>> getFancyPendingInwadList() {
        return fancyPendingInwadList;
    }

    /**
     * @param fancyPendingInwadList the fancyPendingInwadList to set
     */
    public void setFancyPendingInwadList(Map<String, List<FancyAuctionDobj>> fancyPendingInwadList) {
        this.fancyPendingInwadList = fancyPendingInwadList;
    }

    /**
     * @return the rsOpenSeriesDetails
     */
    public RowSet getRsOpenSeriesDetails() {
        return rsOpenSeriesDetails;
    }

    /**
     * @param rsOpenSeriesDetails the rsOpenSeriesDetails to set
     */
    public void setRsOpenSeriesDetails(RowSet rsOpenSeriesDetails) {
        this.rsOpenSeriesDetails = rsOpenSeriesDetails;
    }

    /**
     * @return the btnInwardApplication
     */
    public CommandButton getBtnInwardApplication() {
        return btnInwardApplication;
    }

    /**
     * @param btnInwardApplication the btnInwardApplication to set
     */
    public void setBtnInwardApplication(CommandButton btnInwardApplication) {
        this.btnInwardApplication = btnInwardApplication;
    }

    /**
     * @return the selectedFancyPendingInwadList
     */
    public List<FancyAuctionDobj> getSelectedFancyPendingInwadList() {
        return selectedFancyPendingInwadList;
    }

    /**
     * @param selectedFancyPendingInwadList the selectedFancyPendingInwadList to
     * set
     */
    public void setSelectedFancyPendingInwadList(List<FancyAuctionDobj> selectedFancyPendingInwadList) {
        this.selectedFancyPendingInwadList = selectedFancyPendingInwadList;
    }
}
