/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.reports.DealerRCPendencyDobj;
import nic.vahan.form.dobj.smartcard.CsvFormatDO;
import nic.vahan.form.impl.DealerRCPendencyImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import static nic.vahan.server.ServerUtil.getDealerCode;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Afzal
 */
@ViewScoped
@ManagedBean(name = "dealerRCPendency")
public class DealerRCPendencyBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(DealerRCPendencyBean.class);
    private List<DealerRCPendencyDobj> printDobj = new ArrayList<DealerRCPendencyDobj>();
    private SessionVariables sessionVariables;
    private String vahanMessages = null;
    private String downloadFileName;
    private List<DealerRCPendencyBean> filteredSeat = null;
    private DealerRCPendencyDobj dobj;
    private boolean showOfficeName = false;
    private boolean showDealerName = false;
    private String repGeneDate;

    public void setListBeans(List<DealerRCPendencyDobj> listDobjs) {
        setPrintDobj(listDobjs);
    }

    public void showDealerPendencyDetails() {
        int office_cd = 0;
        String state_cd = null;
        Long user_cd = 0l;
        String user_catg = null;
        String DealerCode;
        repGeneDate = DateUtil.getDateInDDMMYYYY_HHMMSS(new Date()).replaceAll(":", "-");

        try {
            sessionVariables = new SessionVariables();
            if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())) {
                vahanMessages = "Session time Out";
                return;
            }
            HttpSession session = Util.getSession();
            if (session != null && session.getAttribute("off_cd") != null) {
                office_cd = Integer.parseInt(session.getAttribute("off_cd").toString());
                state_cd = session.getAttribute("state_cd").toString();
                user_cd = Long.parseLong(session.getAttribute("emp_cd").toString());
                user_catg = session.getAttribute("user_catg").toString();
            }

            if (state_cd != null) {
                DealerCode = getDealerCode(user_cd, state_cd, office_cd);
                if (user_catg != null) {
                    if (user_catg.equals(TableConstants.USER_CATG_OFF_STAFF)) {
                        DealerCode = "";
                        setShowOfficeName(false);
                        setShowDealerName(true);
                    } else if (user_catg.equals(TableConstants.USER_CATG_DEALER)) {
                        office_cd = 0;
                        setShowOfficeName(true);
                        setShowDealerName(false);
                    }
                }
                this.setListBeans(DealerRCPendencyImpl.getDealerRCPenDetails(office_cd, state_cd, DealerCode));
                if (!getPrintDobj().isEmpty()) {
                    dobj = getPrintDobj().get(getPrintDobj().size() - 1);
                }
            } else {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert", "Error in getting record"));
                return;
            }

        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void updatelblFile() {

        setDownloadFileName("Dealer_Registration_pendency_Report as on " + dobj.getReportGenDate() + " ( " + dobj.getOffName() + " )");

    }

    public void downloadCSV() {
        try {
            String fileName = this.downloadFileName + ".csv";
            List<CsvFormatDO> csv = new ArrayList<>();
            String downloadCVSString = "";
            HttpSession session = Util.getSession();
            for (DealerRCPendencyDobj dobj : getPrintDobj()) {
                CsvFormatDO dos = new CsvFormatDO();
                if (session.getAttribute("user_catg").toString() != null) {
                    if (session.getAttribute("user_catg").toString().equals(TableConstants.USER_CATG_OFF_STAFF)) {
                        downloadCVSString = dobj.getDealerName() + " " + dobj.getApplNO() + " " + dobj.getRegnNO() + " " + dobj.getChasiNO() + " " + dobj.getOwnerName() + " " + dobj.getPurpose() + " " + dobj.getModelName() + " " + dobj.getHypth() + " " + dobj.getApplStatus() + " " + dobj.getStatus() + " " + dobj.getPendingFrom();
                    } else if (session.getAttribute("user_catg").toString().equals(TableConstants.USER_CATG_DEALER)) {
                        downloadCVSString = dobj.getOffName() + " " + dobj.getApplNO() + " " + dobj.getRegnNO() + " " + dobj.getChasiNO() + " " + dobj.getOwnerName() + " " + dobj.getPurpose() + " " + dobj.getModelName() + " " + dobj.getHypth() + " " + dobj.getApplStatus() + " " + dobj.getStatus() + " " + dobj.getPendingFrom();
                    }
                }

                dos.setSmartCardString(downloadCVSString);
                csv.add(dos);
            }

            CommonUtils.writeCSVFile(csv, fileName);

        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, vex.getMessage(), vex.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error - Technical Problem in Database", "Error - Technical Problem in Database"));

        }
    }

    /**
     * @return the printDobj
     */
    public List<DealerRCPendencyDobj> getPrintDobj() {
        return printDobj;
    }

    /**
     * @param printDobj the printDobj to set
     */
    public void setPrintDobj(List<DealerRCPendencyDobj> printDobj) {
        this.printDobj = printDobj;
    }

    /**
     * @return the downloadFileName
     */
    public String getDownloadFileName() {
        return downloadFileName;
    }

    /**
     * @param downloadFileName the downloadFileName to set
     */
    public void setDownloadFileName(String downloadFileName) {
        this.downloadFileName = downloadFileName;
    }

    /**
     * @return the filteredSeat
     */
    public List<DealerRCPendencyBean> getFilteredSeat() {
        return filteredSeat;
    }

    /**
     * @param filteredSeat the filteredSeat to set
     */
    public void setFilteredSeat(List<DealerRCPendencyBean> filteredSeat) {
        this.filteredSeat = filteredSeat;
    }

    /**
     * @return the dobj
     */
    public DealerRCPendencyDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(DealerRCPendencyDobj dobj) {
        this.dobj = dobj;
    }

    /**
     * @return the showOfficeName
     */
    public boolean isShowOfficeName() {
        return showOfficeName;
    }

    /**
     * @param showOfficeName the showOfficeName to set
     */
    public void setShowOfficeName(boolean showOfficeName) {
        this.showOfficeName = showOfficeName;
    }

    /**
     * @return the showDealerName
     */
    public boolean isShowDealerName() {
        return showDealerName;
    }

    /**
     * @param showDealerName the showDealerName to set
     */
    public void setShowDealerName(boolean showDealerName) {
        this.showDealerName = showDealerName;
    }

    /**
     * @return the repGeneDate
     */
    public String getRepGeneDate() {
        return repGeneDate;
    }

    /**
     * @param repGeneDate the repGeneDate to set
     */
    public void setRepGeneDate(String repGeneDate) {
        this.repGeneDate = repGeneDate;
    }
}
