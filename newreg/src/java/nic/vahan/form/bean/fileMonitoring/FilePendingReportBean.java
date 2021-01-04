/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.fileMonitoring;

import java.util.Iterator;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.fileMonitoring.FilePendingReportDO;
import nic.vahan.form.impl.fileMonitoring.FilePendingReportImpl;
import org.apache.log4j.Logger;

/**
 *
 * @author Deepak
 */
@ManagedBean(name = "filespendingreportbean")
@ViewScoped
public class FilePendingReportBean extends FilePendingReportDO {

    private static final Logger LOGGER = Logger.getLogger(FilePendingReportBean.class);
    static String whereami = "FilePendingReportBean";

    public void getPendingFiles() {
        try {

            if (getFromdate() == null || getToDate() == null) {
                FilePendingReportImpl.sendMessage("From Date or To Date is blank, Please select the date.");
                setFromdate(null);
                setToDate(null);
            } else {
                if (DateUtils.getDate1MinusDate2_Days(getFromdate(), getToDate()) > 60) {
                    FilePendingReportImpl.sendMessage("Difference between from date and upto date can not be more than 60 days.");
                    return;
                }
                setFilelist(FilePendingReportImpl.getPendingFiles(this));
                if (!getFilelist().isEmpty()) {
                    setRenderTbl(true);
                } else {
                    FilePendingReportImpl.sendMessage("No pending files.");
                    setRenderTbl(false);
                }
            }

        } catch (VahanException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(TableConstants.SomthingWentWrong));
        }
    }

    public String save() {
        boolean flag = false;
        String error = "";
        FilePendingReportDO usr1 = null;
        String status = null;
        boolean linkflg = false;
        try {
            if (error.equalsIgnoreCase("")) {
                Iterator itr = null;
                itr = getFilelist().iterator();
                while (itr.hasNext()) {
                    FilePendingReportDO lmst = (FilePendingReportDO) itr.next();
                    if (lmst.isEnter()) {
                        linkflg = true;
                    }
                }
                if (!linkflg) {
                    flag = true;
                    FilePendingReportImpl.sendMessage("Please Select Atleast One File");
                }
                if (flag == false) {
                    if (error.equalsIgnoreCase("")) {
                        boolean testflag = FilePendingReportImpl.saveFileDetail(getFilelist(), this);
                        if (testflag) {
                            FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "printFilesPendingPdf");
                        }
                    }
                }
            }
            if (!error.equalsIgnoreCase("")) {
                FilePendingReportImpl.sendMessage(error);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        return status;
    }
}
