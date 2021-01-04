/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.hsrp;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.hsrp.HSRPFileExportDobj;
import nic.vahan.form.dobj.smartcard.CsvFormatDO;
import nic.vahan.form.dobj.smartcard.SmartCardDobj;
import nic.vahan.form.impl.hsrp.HSRPFileExportImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author tranC075
 */
@ManagedBean(name = "fileDownloadBean")
@ViewScoped
public class HSRPFileExportBean implements Serializable {

    FacesMessage message = null;
    private static final Logger LOGGER = Logger.getLogger(HSRPFileExportBean.class);
    private StreamedContent file;
    private List fileNameList = new ArrayList();
    private ArrayList<HSRPFileExportDobj> hsrpDetailList = null;
    private String currentDate = "";
    private List<HSRPFileExportDobj> oldRecords = new ArrayList<>();
    private Date oldDate = new Date();
    private HSRPFileExportDobj hsrp = new HSRPFileExportDobj();
    private HSRPFileExportDobj selectedOldHsrp;
    private boolean blnCurrentPanel = false;
    private boolean isExceedPage = false;
    private String stateCode;
    private int offCode;
    String userCatg = "";
    private boolean forAllStates = false;
    private Date curDateForCal;
    private boolean showButton = false;

    @PostConstruct
    private void init() {
        try {
            setStateCode(Util.getUserStateCode());
            setOffCode(Util.getSelectedSeat().getOff_cd());
            userCatg = Util.getUserCategory();
            if (userCatg.endsWith(TableConstants.USER_CATG_STATE_HSRP)) {
                forAllStates = true;
                if (getStateCode() == null) {
                    return;
                }
            } else {
                forAllStates = false;
                if (getStateCode() == null || getOffCode() == 0) {
                    return;
                }
            }

            oldRecords = HSRPFileExportImpl.getHSRPOldList(getStateCode(), getOffCode(), new Date());
            blnCurrentPanel = false;
            curDateForCal = new Date();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void oldSelectSCListener() {
        hsrp = getSelectedOldHsrp();
        if (hsrp != null && hsrp.getHsrpFileName() != null && !"".contains(hsrp.getHsrpFileName())) {
            hsrp = HSRPFileExportImpl.getHSRPOldListFilewise(getStateCode(), getOffCode(), hsrp.getHsrpFileName()).get(0);
        }
        if (hsrp.getListFileExport().size() > 50) {
            isExceedPage = true;

        } else {
            isExceedPage = false;

        }
        setBlnCurrentPanel(false);
        showButton = true;
    }

    public void downloadCSV() {
        try {
            String fileName = hsrp.getHsrpFileName() + ".csv";
            List<CsvFormatDO> csv = new ArrayList<>();
            String hsrpString = "";
            for (HSRPFileExportDobj dobj : hsrp.getListFileExport()) {
                CsvFormatDO dos = new CsvFormatDO();
                if (forAllStates) {
                    hsrpString = dobj.getRegnNo() + dobj.getSeperator() + dobj.getHrspFlag() + dobj.getSeperator() + dobj.getVehicleClass() + "~" + dobj.getVehicleCatg() + "~" + dobj.getFuelDescr() + dobj.getSeperator() + dobj.getMaker() + dobj.getSeperator() + dobj.getModel() + dobj.getSeperator() + dobj.getOwnerName() + dobj.getSeperator() + dobj.getDealer() + "(" + dobj.getPurCd() + ")" + dobj.getSeperator() + dobj.getReciptNo() + dobj.getSeperator() + dobj.getRegnDt() + dobj.getSeperator() + dobj.getStateCd() + dobj.getOffCd();
                } else {
                    hsrpString = dobj.getRegnNo() + dobj.getSeperator() + dobj.getHrspFlag() + dobj.getSeperator() + dobj.getVehicleClass() + "~" + dobj.getVehicleCatg() + "~" + dobj.getFuelDescr() + dobj.getSeperator() + dobj.getMaker() + dobj.getSeperator() + dobj.getModel() + dobj.getSeperator() + dobj.getOwnerName() + dobj.getSeperator() + dobj.getDealer() + "(" + dobj.getPurCd() + ")" + dobj.getSeperator() + dobj.getReciptNo() + dobj.getSeperator() + dobj.getRegnDt();
                }

                dos.setSmartCardString(hsrpString);
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

    public void oldDateListener(AjaxBehaviorEvent event) {
        oldRecords = HSRPFileExportImpl.getHSRPOldList(getStateCode(), getOffCode(), getOldDate());
    }

    public static String CurrentDateGen() {
        DateFormat dateFormat = new SimpleDateFormat("ddMMyy");
        Date date = new Date();
        String yourDate = dateFormat.format(date);
        return yourDate;
    }

    public void showPendingRecords() {
        showButton = false;
        setHsrp(HSRPFileExportImpl.getHsrpDetails(getStateCode(), getOffCode()));

        if (hsrp.getListFileExport().size() > 50) {
            isExceedPage = true;
        } else {
            isExceedPage = false;
        }
        if (hsrp.getListFileExport().size() > 0) {
            setBlnCurrentPanel(true);
        } else {
            setBlnCurrentPanel(false);
        }

    }

    public void downLoadListener() {
        try {
            if (getHsrp().getListFileExport() == null || getHsrp().getListFileExport().size() == 0) {
                return;
            }
            HSRPFileExportImpl.saveDownLoadInfo(getStateCode(), getOffCode(), getHsrp());
            setHsrp(HSRPFileExportImpl.getHsrpDetails(getStateCode(), getOffCode()));
            if (hsrp.getListFileExport().size() > 50) {
                isExceedPage = true;
            } else {
                isExceedPage = false;
            }
            if (hsrp.getListFileExport().size() > 0) {
                showButton = false;
                setBlnCurrentPanel(false);
            }
            oldRecords = HSRPFileExportImpl.getHSRPOldList(getStateCode(), getOffCode(), getOldDate());
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error - Technical Problem in Database", "Error - Technical Problem in Database"));
        }

    }

    /**
     * @return the file
     */
    public StreamedContent getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(StreamedContent file) {
        this.file = file;
    }

    /**
     * @return the fileNameList
     */
    public List getFileNameList() {
        return fileNameList;
    }

    /**
     * @param fileNameList the fileNameList to set
     */
    public void setFileNameList(List fileNameList) {
        this.fileNameList = fileNameList;
    }

    /**
     * @return the hsrpDetailList
     */
    public ArrayList<HSRPFileExportDobj> getHsrpDetailList() {
        return hsrpDetailList;
    }

    /**
     * @param hsrpDetailList the hsrpDetailList to set
     */
    public void setHsrpDetailList(ArrayList<HSRPFileExportDobj> hsrpDetailList) {
        this.hsrpDetailList = hsrpDetailList;
    }

    /**
     * @return the currentDate
     */
    public String getCurrentDate() {
        return currentDate;
    }

    /**
     * @param currentDate the currentDate to set
     */
    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    /**
     * @return the oldRecords
     */
    public List<HSRPFileExportDobj> getOldRecords() {
        return oldRecords;
    }

    /**
     * @param oldRecords the oldRecords to set
     */
    public void setOldRecords(List<HSRPFileExportDobj> oldRecords) {
        this.oldRecords = oldRecords;
    }

    /**
     * @return the oldDate
     */
    public Date getOldDate() {
        return oldDate;
    }

    /**
     * @param oldDate the oldDate to set
     */
    public void setOldDate(Date oldDate) {
        this.oldDate = oldDate;
    }

    /**
     * @return the hsrp
     */
    public HSRPFileExportDobj getHsrp() {
        return hsrp;
    }

    /**
     * @param hsrp the hsrp to set
     */
    public void setHsrp(HSRPFileExportDobj hsrp) {
        this.hsrp = hsrp;
    }

    /**
     * @return the selectedOldHsrp
     */
    public HSRPFileExportDobj getSelectedOldHsrp() {
        return selectedOldHsrp;
    }

    /**
     * @param selectedOldHsrp the selectedOldHsrp to set
     */
    public void setSelectedOldHsrp(HSRPFileExportDobj selectedOldHsrp) {
        this.selectedOldHsrp = selectedOldHsrp;
    }

    /**
     * @return the blnCurrentPanel
     */
    public boolean isBlnCurrentPanel() {
        return blnCurrentPanel;
    }

    /**
     * @param blnCurrentPanel the blnCurrentPanel to set
     */
    public void setBlnCurrentPanel(boolean blnCurrentPanel) {
        this.blnCurrentPanel = blnCurrentPanel;
    }

    /**
     * @return the isExceedPage
     */
    public boolean isIsExceedPage() {
        return isExceedPage;
    }

    /**
     * @param isExceedPage the isExceedPage to set
     */
    public void setIsExceedPage(boolean isExceedPage) {
        this.isExceedPage = isExceedPage;
    }

    /**
     * @return the stateCode
     */
    public String getStateCode() {
        return stateCode;
    }

    /**
     * @param stateCode the stateCode to set
     */
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    /**
     * @return the offCode
     */
    public int getOffCode() {
        return offCode;
    }

    /**
     * @param offCode the offCode to set
     */
    public void setOffCode(int offCode) {
        this.offCode = offCode;
    }

    /**
     * @return the forAllStates
     */
    public boolean isForAllStates() {
        return forAllStates;
    }

    /**
     * @param forAllStates the forAllStates to set
     */
    public void setForAllStates(boolean forAllStates) {
        this.forAllStates = forAllStates;
    }

    /**
     * @return the curDateForCal
     */
    public Date getCurDateForCal() {
        return curDateForCal;
    }

    /**
     * @param curDateForCal the curDateForCal to set
     */
    public void setCurDateForCal(Date curDateForCal) {
        this.curDateForCal = curDateForCal;
    }

    /**
     * @return the showButton
     */
    public boolean isShowButton() {
        return showButton;
    }

    /**
     * @param showButton the showButton to set
     */
    public void setShowButton(boolean showButton) {
        this.showButton = showButton;
    }
}
