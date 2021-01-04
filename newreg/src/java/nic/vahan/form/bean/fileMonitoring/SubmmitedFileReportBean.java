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
import nic.vahan.form.dobj.fileMonitoring.SubmmitedFileReportDO;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.fileMonitoring.SubmmitedFileReportImpl;
import nic.vahan.form.impl.fileMonitoring.IncommingFilesImpl;
import org.apache.log4j.Logger;

/**
 *
 * @author Deepak
 */
@ManagedBean(name = "submmitedfilereport")
@ViewScoped
public class SubmmitedFileReportBean extends SubmmitedFileReportDO {

    private static final Logger LOGGER = Logger.getLogger(SubmmitedFileReportBean.class);
    static String whereami = "SubmmitedFileReportBean";

    @PostConstruct
    public void init() {
        try {
            setDearlist(IncommingFilesImpl.getDealers(Util.getUserStateCode(), Util.getUserLoginOffCode()));
            if (!getDearlist().isEmpty()) {
                setEnableCalender(true);
            }
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

    public void getSuccessfullFiles() throws Exception {
        if (getDealerName().equalsIgnoreCase("-1")) {
            SubmmitedFileReportImpl.sendMessage("Please Select Dealer Name");
            return;
        } else if (getFromdate() == null || getToDate() == null) {
            SubmmitedFileReportImpl.sendMessage("Either From date or To date is blank, Please select the date.");
            return;
        } else {
            setFilelist(SubmmitedFileReportImpl.getSuccessfullFiles(this));
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
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("successfullfilelist", getFilelist());
            FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "printSubmmitedFilesReportPdf");
        }
    }
}
