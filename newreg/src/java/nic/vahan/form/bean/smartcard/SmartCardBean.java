/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.smartcard;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletResponse;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.dobj.smartcard.CsvFormatDO;
import nic.vahan.form.dobj.smartcard.SmartCardDobj;
import nic.vahan.form.impl.SmartCardImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "smartcard")
@ViewScoped
public class SmartCardBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(SmartCardBean.class);
    private SmartCardDobj s_card = new SmartCardDobj();
    private List<SmartCardDobj> oldRecords = new ArrayList<>();
    private SmartCardDobj selectedOldSmartcard;
    private Date oldDate = new Date();
    private boolean blnCurrentPanel = true;
    private boolean blnOldPanel = false;
    private boolean showPendingTable = false;
    private boolean showDownLoadButton = false;
    private String stateCode;
    private int offCode;
    private boolean showFileSearchPanel = false;
    private String regnNo = "";
    // private List<SmartCardDobj> regnRecord = new ArrayList<>();
    private SmartCardDobj regnRecord = new SmartCardDobj();
    private boolean showDownloadForVehicleSearch = false;
    private boolean showSearchVehicle = false;
    private boolean renderSmartCardDialog = true;
    private int offset = 0;
    private int limit = 2000;
    private String pendingReason = "";
    private boolean showDownload = false;
    private boolean showNext = false;
    private boolean showBack = false;
    private boolean showPendingButton = false;

    @PostConstruct
    private void init() {
        try {
            setStateCode(Util.getUserStateCode());
            if (Util.getSelectedSeat() != null) {
                setOffCode(Util.getSelectedSeat().getOff_cd());
            } else {
                throw new VahanException("Error in Getting Smart Card Details,Please Try again");
            }
            if (getStateCode() == null || getOffCode() == 0) {
                return;
            }
            showPendingButton = true;
            showSearchVehicle = true;
            blnCurrentPanel = false;
            oldRecords = SmartCardImpl.getSmartCardOldList(getStateCode(), getOffCode(), new Date());
        } catch (VahanException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void downLoadListener() {
        try {
            if (getS_card().getListSmartCard() == null || getS_card().getListSmartCard().size() == 0) {
                return;
            }
            setS_card(SmartCardImpl.saveDownLoadInfo(getStateCode(), getOffCode(), getS_card(), offset, limit));

        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, vex.getMessage(), vex.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error - Technical Problem in Database", "Error - Technical Problem in Database"));
        }

    }

    public void downLoadListenerForSingleVehicle() {
        try {
            if (regnRecord.getListSmartCard() == null || regnRecord.getListSmartCard().size() == 0) {
                return;
            }
            SmartCardImpl.saveDownLoadInfo(getStateCode(), getOffCode(), regnRecord, offset, limit);
            showDownload = true;
            blnCurrentPanel = false;
        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, vex.getMessage(), vex.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error - Technical Problem in Database", "Error - Technical Problem in Database"));
        }

    }

    public void next() {
        showBack = true;
        this.offset += limit;
        showPendingRecords();
    }

    public void back() {
        this.offset -= limit;
        showPendingRecords();
    }

    public void downloadCSV() {
        try {
            String fileName = s_card.getSmartCardFileName() + ".csv";
            List<CsvFormatDO> csv = new ArrayList<>();
            String smartcardString = "";
            for (SmartCardDobj dobj : s_card.getListSmartCard()) {
                CsvFormatDO dos = new CsvFormatDO();
                smartcardString = dobj.getVehregno() + dobj.getRegdate() + dobj.getOwnername() + dobj.getFname() + dobj.getCaddress() + dobj.getManufacturer() + dobj.getModelno() + dobj.getColour() + dobj.getFuel() + dobj.getVehclass() + dobj.getBodytype() + dobj.getSeatcap() + dobj.getStandcap() + dobj.getManufdate() + dobj.getUnladenwt() + dobj.getCubiccap() + dobj.getWheelbase() + dobj.getNoofcylin() + dobj.getOwnerserial() + dobj.getChasisno() + dobj.getEngineno() + dobj.getTaxpaidupto() + dobj.getRegnvalidity() + dobj.getApprovingauth() + dobj.getFinname() + dobj.getFinaddress() + dobj.getHypofrom() + dobj.getHypoto() + dobj.getNocno() + dobj.getStateto() + dobj.getRtoto() + dobj.getNcrbclearno() + dobj.getNocissuedt() + dobj.getInscompname() + dobj.getCoverpolicyno() + dobj.getInstype() + dobj.getInsvalidupto() + dobj.getPuccentercode() + dobj.getPucvalidupto() + dobj.getTaxamount() + dobj.getFine() + dobj.getExemptrecptno() + dobj.getPaymentdt() + dobj.getTaxvalidfrom() + dobj.getTaxvalidto() + dobj.getExemption() + dobj.getDrtocode() + dobj.getBuflag() + dobj.getFitvalidupto() + dobj.getFitinsofficer() + dobj.getFitlocation() + dobj.getGrossvehwt() + dobj.getSemitrailers() + dobj.getTyreinfo() + dobj.getAxleinfo() + dobj.getAppl_no() + dobj.getPur_cd();

                dos.setSmartCardString(smartcardString);
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

    public void downloadCSVForSingleVehicle() {
        try {
            String fileName = "fileDownload" + ".csv";
            List<CsvFormatDO> csv = new ArrayList<>();
            String smartcardString = "";
            for (SmartCardDobj dobj : regnRecord.getListSmartCard()) {
                CsvFormatDO dos = new CsvFormatDO();
                smartcardString = dobj.getVehregno() + dobj.getRegdate() + dobj.getOwnername() + dobj.getFname() + dobj.getCaddress() + dobj.getManufacturer() + dobj.getModelno() + dobj.getColour() + dobj.getFuel() + dobj.getVehclass() + dobj.getBodytype() + dobj.getSeatcap() + dobj.getStandcap() + dobj.getManufdate() + dobj.getUnladenwt() + dobj.getCubiccap() + dobj.getWheelbase() + dobj.getNoofcylin() + dobj.getOwnerserial() + dobj.getChasisno() + dobj.getEngineno() + dobj.getTaxpaidupto() + dobj.getRegnvalidity() + dobj.getApprovingauth() + dobj.getFinname() + dobj.getFinaddress() + dobj.getHypofrom() + dobj.getHypoto() + dobj.getNocno() + dobj.getStateto() + dobj.getRtoto() + dobj.getNcrbclearno() + dobj.getNocissuedt() + dobj.getInscompname() + dobj.getCoverpolicyno() + dobj.getInstype() + dobj.getInsvalidupto() + dobj.getPuccentercode() + dobj.getPucvalidupto() + dobj.getTaxamount() + dobj.getFine() + dobj.getExemptrecptno() + dobj.getPaymentdt() + dobj.getTaxvalidfrom() + dobj.getTaxvalidto() + dobj.getExemption() + dobj.getDrtocode() + dobj.getBuflag() + dobj.getFitvalidupto() + dobj.getFitinsofficer() + dobj.getFitlocation() + dobj.getGrossvehwt() + dobj.getSemitrailers() + dobj.getTyreinfo() + dobj.getAxleinfo() + dobj.getAppl_no() + dobj.getPur_cd();

                dos.setSmartCardString(smartcardString);
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

    public void showPendingRecords() {
        try {
            renderSmartCardDialog = false;
            showDownLoadButton = false;
            if (getS_card() != null && getS_card().getListHsrpPending() != null && !getS_card().getListHsrpPending().isEmpty()) {
                getS_card().getListHsrpPending().clear();
            }
            setS_card(SmartCardImpl.getSmartCardGenerateDetailsList(getStateCode(), getOffCode(), offset, limit, null));

            if (s_card.getListSmartCard().size() < 1) {
                setBlnCurrentPanel(false);
            } else {
                setBlnCurrentPanel(true);
            }
            if (s_card.isRecordsPresent()) {
                showNext = true;
            } else {
                showNext = false;
            }
            if (offset == 0) {
                showBack = false;
            }
            if (s_card.getListSmartCard().size() == 0 && s_card.getListHsrpPending().size() == 0) {
                showNext = false;
            }
            showPendingButton = false;
        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, vex.getMessage(), vex.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error - Technical Problem in Database", "Error - Technical Problem in Database"));
        }

    }

    public void oldDateListener(AjaxBehaviorEvent event) {
        showBack = false;
        showNext = false;
        blnCurrentPanel = false;
        showDownLoadButton = false;
        oldRecords = SmartCardImpl.getSmartCardOldList(getStateCode(), getOffCode(), getOldDate());
        if (getS_card() != null && getS_card().getListHsrpPending() != null && !getS_card().getListHsrpPending().isEmpty()) {
            getS_card().getListHsrpPending().clear();
        }
        if (getS_card() != null && getS_card().getListSmartCard() != null && !getS_card().getListSmartCard().isEmpty()) {
            getS_card().getListSmartCard().clear();
        }
    }

    public void oldSelectSCListener() {
        renderSmartCardDialog = false;
        showBack = false;
        showNext = false;
        blnCurrentPanel = false;
        showDownLoadButton = true;
        s_card = getSelectedOldSmartcard();
        if (s_card != null && s_card.getSmartCardFileName() != null && !"".contains(s_card.getSmartCardFileName())) {
            s_card = SmartCardImpl.getSmartCardOldListFilewise(getStateCode(), getOffCode(), s_card.getSmartCardFileName()).get(0);
        }
        setBlnCurrentPanel(false);
    }

    public void showVehicle() {
        regnNo = "";
        showFileSearchPanel = true;
        showDownloadForVehicleSearch = false;
        regnRecord.setListSmartCard(null);
        regnRecord.setListHsrpPending(null);
    }

    public void searchVehicle() {
        try {
            renderSmartCardDialog = false;
            if (this.regnNo.trim() == null || this.regnNo.trim().equalsIgnoreCase("")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Please Enter Valid Registration Number"));
                return;
            }
            setRegnRecord(SmartCardImpl.getSmartCardGenerateDetailsList(getStateCode(), getOffCode(), offset, limit, regnNo));
            blnCurrentPanel = true;
            showDownload = false;
            if (regnRecord.getListSmartCard().size() < 1 && regnRecord.getListHsrpPending().size() < 1) {
                String flatfile = SmartCardImpl.getSmartCardFlatFileName(getStateCode(), getOffCode(), regnNo);
                if (flatfile.length() > 1) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Flat File Already Generated - " + flatfile, "Flat File Already Generated - " + flatfile));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "No Record Found.", "No Record Found."));
                }
            }
        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, vex.getMessage(), vex.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error - Technical Problem in Database", "Error - Technical Problem in Database"));
        }
    }

    public void backToFlatFile() {
        showFileSearchPanel = false;
        showDownloadForVehicleSearch = false;
    }

    /**
     * @return the s_card
     */
    public SmartCardDobj getS_card() {
        return s_card;
    }

    /**
     * @param s_card the s_card to set
     */
    public void setS_card(SmartCardDobj s_card) {
        this.s_card = s_card;
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
     * @return the oldRecords
     */
    public List<SmartCardDobj> getOldRecords() {
        return oldRecords;
    }

    /**
     * @param oldRecords the oldRecords to set
     */
    public void setOldRecords(List<SmartCardDobj> oldRecords) {
        this.oldRecords = oldRecords;
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
     * @return the blnOldPanel
     */
    public boolean isBlnOldPanel() {
        return blnOldPanel;
    }

    /**
     * @param blnOldPanel the blnOldPanel to set
     */
    public void setBlnOldPanel(boolean blnOldPanel) {
        this.blnOldPanel = blnOldPanel;
    }

    /**
     * @return the selectedOldSmartcard
     */
    public SmartCardDobj getSelectedOldSmartcard() {
        return selectedOldSmartcard;
    }

    /**
     * @param selectedOldSmartcard the selectedOldSmartcard to set
     */
    public void setSelectedOldSmartcard(SmartCardDobj selectedOldSmartcard) {
        this.selectedOldSmartcard = selectedOldSmartcard;
    }

    /**
     * @return the showPendingTable
     */
    public boolean isShowPendingTable() {
        return showPendingTable;
    }

    /**
     * @param showPendingTable the showPendingTable to set
     */
    public void setShowPendingTable(boolean showPendingTable) {
        this.showPendingTable = showPendingTable;
    }

    /**
     * @return the showDownLoadButton
     */
    public boolean isShowDownLoadButton() {
        return showDownLoadButton;
    }

    /**
     * @param showDownLoadButton the showDownLoadButton to set
     */
    public void setShowDownLoadButton(boolean showDownLoadButton) {
        this.showDownLoadButton = showDownLoadButton;
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
     * @return the showFileSearchPanel
     */
    public boolean isShowFileSearchPanel() {
        return showFileSearchPanel;
    }

    /**
     * @param showFileSearchPanel the showFileSearchPanel to set
     */
    public void setShowFileSearchPanel(boolean showFileSearchPanel) {
        this.showFileSearchPanel = showFileSearchPanel;
    }

    /**
     * @return the regnNo
     */
    public String getRegnNo() {
        return regnNo;
    }

    /**
     * @param regnNo the regnNo to set
     */
    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    /**
     * @return the showDownloadForVehicleSearch
     */
    public boolean isShowDownloadForVehicleSearch() {
        return showDownloadForVehicleSearch;
    }

    /**
     * @param showDownloadForVehicleSearch the showDownloadForVehicleSearch to
     * set
     */
    public void setShowDownloadForVehicleSearch(boolean showDownloadForVehicleSearch) {
        this.showDownloadForVehicleSearch = showDownloadForVehicleSearch;
    }

    /**
     * @return the showSearchVehicle
     */
    public boolean isShowSearchVehicle() {
        return showSearchVehicle;
    }

    /**
     * @param showSearchVehicle the showSearchVehicle to set
     */
    public void setShowSearchVehicle(boolean showSearchVehicle) {
        this.showSearchVehicle = showSearchVehicle;
    }

    /**
     * @return the renderSmartCardDialog
     */
    public boolean isRenderSmartCardDialog() {
        return renderSmartCardDialog;
    }

    /**
     * @param renderSmartCardDialog the renderSmartCardDialog to set
     */
    public void setRenderSmartCardDialog(boolean renderSmartCardDialog) {
        this.renderSmartCardDialog = renderSmartCardDialog;
    }

    /**
     * @return the pendingReason
     */
    public String getPendingReason() {
        return pendingReason;
    }

    /**
     * @param pendingReason the pendingReason to set
     */
    public void setPendingReason(String pendingReason) {
        this.pendingReason = pendingReason;
    }

    /**
     * @return the regnRecord
     */
    public SmartCardDobj getRegnRecord() {
        return regnRecord;
    }

    /**
     * @param regnRecord the regnRecord to set
     */
    public void setRegnRecord(SmartCardDobj regnRecord) {
        this.regnRecord = regnRecord;
    }

    /**
     * @return the showDownload
     */
    public boolean isShowDownload() {
        return showDownload;
    }

    /**
     * @param showDownload the showDownload to set
     */
    public void setShowDownload(boolean showDownload) {
        this.showDownload = showDownload;
    }

    /**
     * @return the showNext
     */
    public boolean isShowNext() {
        return showNext;
    }

    /**
     * @param showNext the showNext to set
     */
    public void setShowNext(boolean showNext) {
        this.showNext = showNext;
    }

    /**
     * @return the showPendingButton
     */
    public boolean isShowPendingButton() {
        return showPendingButton;
    }

    /**
     * @param showPendingButton the showPendingButton to set
     */
    public void setShowPendingButton(boolean showPendingButton) {
        this.showPendingButton = showPendingButton;
    }

    /**
     * @return the showBack
     */
    public boolean isShowBack() {
        return showBack;
    }

    /**
     * @param showBack the showBack to set
     */
    public void setShowBack(boolean showBack) {
        this.showBack = showBack;
    }
}
