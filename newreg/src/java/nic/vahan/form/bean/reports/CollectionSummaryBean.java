/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.reports.CollectionSummaryDobj;
import nic.vahan.form.impl.PrintDocImpl;

@ViewScoped
@ManagedBean(name = "collectionSummary")
public class CollectionSummaryBean implements Serializable {

    private ArrayList<CollectionSummaryDobj> summaryList;
    private ArrayList<CollectionSummaryDobj> cancelSummaryList;
    private Date date;
    private Date today = new Date();
    private long totalCollection;
    private long totalCancelCollection;
    private int totalReceiptCollection;
    private int totalReceiptCancel;
    private String userCatg;
    private SessionVariables sessionVariables = null;
    private String vahanMessages = null;

    @PostConstruct
    public void init() {
        sessionVariables = new SessionVariables();
        if (sessionVariables == null || sessionVariables.getStateCodeSelected() == null) {
            vahanMessages = "Session time Out";
            return;
        }
        userCatg = sessionVariables.getUserCatgForLoggedInUser();
        date = today;
        try {
            setSummaryList(PrintDocImpl.getCollectionStatement(getDate(), userCatg));
            for (CollectionSummaryDobj obj : summaryList) {
                totalCollection += obj.getAmount();
                totalReceiptCollection += obj.getNoOfRcpt();
            }
            if (!userCatg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER)) {
                setCancelSummaryList(PrintDocImpl.getCancelCollectionStatement(getDate()));
                for (CollectionSummaryDobj obj : cancelSummaryList) {
                    totalCancelCollection += obj.getAmount();
                    totalReceiptCancel += obj.getNoOfRcpt();
                }
            }
        } catch (VahanException e) {
            JSFUtils.setFacesMessage(e.getMessage(), null, JSFUtils.ERROR);
        }
    }

    public void getDetails() {
        setTotalCollection(0l);
        setTotalCancelCollection(0l);
        setTotalReceiptCollection(0);
        setTotalReceiptCancel(0);
        try {
            if (sessionVariables == null || sessionVariables.getStateCodeSelected() == null) {
                return;
            }
            setSummaryList(PrintDocImpl.getCollectionStatement(getDate(), userCatg));
            for (CollectionSummaryDobj obj : summaryList) {
                totalCollection += obj.getAmount();
                totalReceiptCollection += obj.getNoOfRcpt();
            }
            if (!userCatg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER)) {
                setCancelSummaryList(PrintDocImpl.getCancelCollectionStatement(getDate()));
                for (CollectionSummaryDobj obj : cancelSummaryList) {
                    totalCancelCollection += obj.getAmount();
                    totalReceiptCancel += obj.getNoOfRcpt();
                }
            }
        } catch (VahanException e) {
            JSFUtils.setFacesMessage(e.getMessage(), null, JSFUtils.ERROR);
        }
    }

    /**
     * @return the summaryList
     */
    public ArrayList<CollectionSummaryDobj> getSummaryList() {
        return summaryList;
    }

    /**
     * @param summaryList the summaryList to set
     */
    public void setSummaryList(ArrayList<CollectionSummaryDobj> summaryList) {
        this.summaryList = summaryList;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the cancelSummaryList
     */
    public ArrayList<CollectionSummaryDobj> getCancelSummaryList() {
        return cancelSummaryList;
    }

    /**
     * @param cancelSummaryList the cancelSummaryList to set
     */
    public void setCancelSummaryList(ArrayList<CollectionSummaryDobj> cancelSummaryList) {
        this.cancelSummaryList = cancelSummaryList;
    }

    /**
     * @return the today
     */
    public Date getToday() {
        return today;
    }

    /**
     * @param today the today to set
     */
    public void setToday(Date today) {
        this.today = today;
    }

    /**
     * @return the totalCollection
     */
    public long getTotalCollection() {
        return totalCollection;
    }

    /**
     * @param totalCollection the totalCollection to set
     */
    public void setTotalCollection(long totalCollection) {
        this.totalCollection = totalCollection;
    }

    /**
     * @return the totalCancelCollection
     */
    public long getTotalCancelCollection() {
        return totalCancelCollection;
    }

    /**
     * @param totalCancelCollection the totalCancelCollection to set
     */
    public void setTotalCancelCollection(long totalCancelCollection) {
        this.totalCancelCollection = totalCancelCollection;
    }

    /**
     * @return the totalReceiptCollection
     */
    public int getTotalReceiptCollection() {
        return totalReceiptCollection;
    }

    /**
     * @param totalReceiptCollection the totalReceiptCollection to set
     */
    public void setTotalReceiptCollection(int totalReceiptCollection) {
        this.totalReceiptCollection = totalReceiptCollection;
    }

    /**
     * @return the totalReceiptCancel
     */
    public int getTotalReceiptCancel() {
        return totalReceiptCancel;
    }

    /**
     * @param totalReceiptCancel the totalReceiptCancel to set
     */
    public void setTotalReceiptCancel(int totalReceiptCancel) {
        this.totalReceiptCancel = totalReceiptCancel;
    }

    /**
     * @return the userCatg
     */
    public String getUserCatg() {
        return userCatg;
    }

    /**
     * @param userCatg the userCatg to set
     */
    public void setUserCatg(String userCatg) {
        this.userCatg = userCatg;
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
