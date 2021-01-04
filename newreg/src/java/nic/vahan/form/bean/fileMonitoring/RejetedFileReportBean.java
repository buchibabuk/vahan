/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.fileMonitoring;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.dobj.fileMonitoring.RejectedFileReportDO;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.fileMonitoring.RejectedFileReportImpl;
import nic.vahan.form.impl.fileMonitoring.SubmmitedFileReportImpl;
import nic.vahan.form.impl.fileMonitoring.IncommingFilesImpl;
import org.apache.log4j.Logger;

/**
 *
 * @author Deepak
 */
@ManagedBean(name = "rejectedfilereport")
@ViewScoped
public class RejetedFileReportBean extends RejectedFileReportDO {

    private static final Logger LOGGER = Logger.getLogger(RejetedFileReportBean.class);
    static String whereami = "RejetedFileReportBean";

    @PostConstruct
    public void init() {
        try {
            setDearlist(IncommingFilesImpl.getDealers(Util.getUserStateCode(), Util.getUserLoginOffCode()));
        } catch (VahanException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage(), e.getMessage()));
        }
    }

    public void getRnderCalender() {
        if (!getDearlist().isEmpty()) {
            setEnableCalender(true);
        }
        if (getDealerName().equalsIgnoreCase("-1")) {
            setEnableCalender(false);
            setEnablePrint(false);
            setToDate(null);
            setFromdate(null);
        }
        if (!getDealerName().equalsIgnoreCase("-1")) {
            setEnableCalender(true);
        }
    }

    public void getRejectedFiles() throws Exception {
        if (getDealerName().equalsIgnoreCase("-1")) {
            RejectedFileReportImpl.sendMessage("Please Select Dealer Name");
            return;
        } else if (getFromdate() == null || getToDate() == null) {
            RejectedFileReportImpl.sendMessage("From Date or To Date is blank, Please select the date.");
            return;
        } else {
            setFilelist(RejectedFileReportImpl.getRejectedFiles(this));
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("dealername", getFilelist().get(0).getDealerName());
        }
        if (getFilelist().isEmpty()) {
            SubmmitedFileReportImpl.sendMessage("No Record Found");
            setEnablePrint(false);
            setFromdate(null);
            setToDate(null);
        } else {
            setEnablePrint(true);
        }
    }

    public void reDirectToPrint() {
        if (!getFilelist().isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("rejectedfilelist", getFilelist());
            FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "printRejetedFilesReportPdf");
        }
    }
}
