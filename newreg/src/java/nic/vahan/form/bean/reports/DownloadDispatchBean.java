/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.text.DateFormat;
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
import javax.faces.event.AjaxBehaviorEvent;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.TmConfigurationDispatchDobj;
import nic.vahan.form.dobj.reports.DownloadDispatchDobj;
import nic.vahan.form.dobj.smartcard.CsvFormatDO;
import nic.vahan.form.impl.DownloadDispatchImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author afzal
 */
@ManagedBean(name = "rcDownloadBean")
@ViewScoped
public class DownloadDispatchBean implements Serializable {

    FacesMessage message = null;
    private static final Logger LOGGER = Logger.getLogger(DownloadDispatchBean.class);
    private DownloadDispatchDobj download = new DownloadDispatchDobj();
    private List<DownloadDispatchDobj> oldRecords = new ArrayList<>();
    private boolean isExceedPage = false;
    private String downLoadLabel;
    private String btnDownloadFileLabel;
    private Date oldDate;
    private DownloadDispatchDobj selectedOldDownload;
    private boolean btnGenRc;
    private boolean isExportFName;
    private boolean isExportAdd3;
    private String downloadFileName;
    private boolean btnEdit;
    private boolean btnPrint;
    private List<DownloadDispatchDobj> filteredSeat = null;
    private String regn_no;
    private DownloadDispatchDobj regnNoDetails = new DownloadDispatchDobj();
    private DownloadDispatchDobj downloadPenWorkDtls = new DownloadDispatchDobj();
    private SessionVariables sessionVariables;
    private String downloadLabelForLblFile;
    private String downloadLabelForMghFile;
    private String currentStartNo;
    private String endNo;
    private String prefix;
    private TmConfigurationDispatchDobj tmConfDispatchDobj = null;
    private boolean isSpeedPostSeries;
    private boolean isShowingAllPendingWork;
    private boolean is_search_by_regn_no;
    private String printEnvelopStickerLabel;
    private boolean isprintCoverPage;
    private boolean isSeriesUpdate;
    private String vahanMessages = null;
    private String flatFileName;
    private boolean is_rcdispatch_letter;
    private boolean is_edit_on_all_pending_records;
    private String regn_no_by_hand;
    private boolean Is_rc_dispatch_by_hand;
    private Date dt_hand_over = null;
    private String remark;
    private DownloadDispatchDobj byHandRegnNoDetails = new DownloadDispatchDobj();
    private Date today = DateUtil.parseDate(DateUtil.getCurrentDate());
    private boolean isdisapble = false;
    private Date penDate;
    private boolean isDispatchAddress = false;
    private boolean btnEitherStickerOREnvelopPrint;
    private boolean downloadFileAsPerBRstateRequirement;
    private boolean downloadFileAsPerRJstateRequirement;
    private boolean downloadFileCommon;
    private boolean downloadFileWhereBarcodeIsOptional;
    private boolean showRCDispatchByHandPanel;
    private boolean showUpdateDispatchPanel;

    @PostConstruct
    private void init() {
        try {
            sessionVariables = new SessionVariables();
            if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                    || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
                vahanMessages = "Session time Out";
                return;
            }
            tmConfDispatchDobj = Util.getTmConfigurationDispatch();
            if (tmConfDispatchDobj.isIs_barcode_mandatory()) {
                setDownloadLabelForLblFile("Download Dispatch File");
                setDownloadLabelForMghFile("Download Dispatch File");

            } else {
                setDownloadLabelForLblFile("Download Dispatch LblFile");
                setDownloadLabelForMghFile("Download Dispatch MghFile");

            }
            if (tmConfDispatchDobj.isIs_speed_post_series()) {
                setIsprintCoverPage(false);
                setIsSpeedPostSeries(true);
                setDownload(DownloadDispatchImpl.getSpeedPostSeriesDetails(sessionVariables));
                if (getDownload() != null) {
                    setCurrentStartNo(getDownload().getCurrentStartNo());
                    setEndNo(getDownload().getEndNo());
                    setPrefix(getDownload().getPrefix());
                    setIsSeriesUpdate(getDownload().isUpdateSeries());
                }
            } else {
                setIsSpeedPostSeries(false);
                setIsprintCoverPage(true);
            }
            if (tmConfDispatchDobj.isIs_sticker_print()) {
                setPrintEnvelopStickerLabel("Print Sticker");
            } else if (tmConfDispatchDobj.isIs_envelop_print()) {
                setPrintEnvelopStickerLabel("Print Envelop");
            } else {
                setPrintEnvelopStickerLabel("");
            }
            if (tmConfDispatchDobj.isIs_show_all_pending_records()) {
                setIsShowingAllPendingWork(true);
            } else {
                setIsShowingAllPendingWork(false);
            }
            if (tmConfDispatchDobj.isIs_search_by_regn_no()) {
                setIs_search_by_regn_no(true);
            } else {
                setIs_search_by_regn_no(false);
            }
            if (tmConfDispatchDobj.isIs_rcdispatch_letter()) {
                setIs_rcdispatch_letter(true);
            } else {
                setIs_rcdispatch_letter(false);
            }
            if (tmConfDispatchDobj.isIs_edit_on_all_pending_records()) {
                setIs_edit_on_all_pending_records(true);
            } else {
                setIs_edit_on_all_pending_records(false);
            }
            if (tmConfDispatchDobj.isIs_rcdispatch_byhand()) {
                setIs_rc_dispatch_by_hand(true);
            } else {
                setIs_rc_dispatch_by_hand(false);
            }
            if (tmConfDispatchDobj.isIs_dispatch_address()) {
                setIsDispatchAddress(true);
            } else {
                setIsDispatchAddress(false);
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
    }

//    public void onBlurBarcode() {
//        try {
//            String returnResponse;
//            if (getDispatch_ref_no() == null || getDispatch_ref_no().isEmpty()) {
//                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Barcode Should Not be Blank", "Barcode Should Not be Blank"));
//                return;
//            }
//            if (getDispatch_ref_no() != null && !getDispatch_ref_no().isEmpty()) {
//                getRegnNoDetails().setDispatch_ref_no_for_display(getDispatch_ref_no().toUpperCase());
//                getRegnNoDetails().setDispatch_ref_no(getDispatch_ref_no().toUpperCase());
//            }
////            String error_message = null;
////            if (DownloadDispatchImpl.chkDuplicateBarcodeNo(dobj.getDispatch_ref_no().toUpperCase())) {
////                error_message = "Duplicate Barcode Number";
////            }
////            if (error_message != null) {
////                RequestContext rc = RequestContext.getCurrentInstance();
////                rc.showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", error_message + " " + dobj.getDispatch_ref_no()));
////                return;
////            }
//            returnResponse = DownloadDispatchImpl.updateVaDispatchRCDetail(sessionVariables, getRegnNoDetails().getDispatch_ref_no().trim().toUpperCase(), getRegnNoDetails().getAppl_no().trim().toUpperCase());
//            if ("true".contains(returnResponse)) {
//                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Barcode Save Successfully", "Barcode Save Successfully"));
//                return;
//            } else if ("false".contains(returnResponse)) {
//                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error in Saving Barcode", "Error in Saving Barcode"));
//                return;
//            }
//        } catch (VahanException ve) {
//            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
//            return;
//        } catch (Exception ex) {
//            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
//            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
//            return;
//        }
//    }
    public void onRowEdit(RowEditEvent event) {
        try {
            String returnResponse;
            DownloadDispatchDobj dobj = ((DownloadDispatchDobj) event.getObject());
            if (dobj.getDispatch_ref_no() == null || dobj.getDispatch_ref_no().isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Barcode Should Not be Blank", "Barcode Should Not be Blank"));
                return;
            }
            if (dobj.getDispatch_ref_no() != null && !dobj.getDispatch_ref_no().equals("")) {
                dobj.setDispatch_ref_no_for_display(dobj.getDispatch_ref_no().toUpperCase());
            }
//            String error_message = null;
//            if (DownloadDispatchImpl.chkDuplicateBarcodeNo(dobj.getDispatch_ref_no().toUpperCase())) {
//                error_message = "Duplicate Barcode Number";
//            }
//            if (error_message != null) {
//                RequestContext rc = RequestContext.getCurrentInstance();
//                rc.showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", error_message + " " + dobj.getDispatch_ref_no()));
//                return;
//            }
            returnResponse = DownloadDispatchImpl.updateVaDispatchRCDetail(sessionVariables, dobj.getAppl_no(), dobj.getDispatch_ref_no());
            if ("true".contains(returnResponse)) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Barcode Save Successfully", "Barcode Save Successfully"));
                return;
            } else if ("false".contains(returnResponse)) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error in Saving Barcode", "Error in Saving Barcode"));
                return;
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            return;
        }
    }

    public void onBlurBarcode() {
        try {
            String returnResponse;
            DownloadDispatchDobj dobj = getDownloadPenWorkDtls();
            Map<String, String> par_map = new HashMap<String, String>();
            par_map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String appl_no = par_map.get("appl_no");
            int id = Integer.parseInt(par_map.get("ID"));
            String barcode = par_map.get("regn_no_tab:tbmsg_file_download:" + id + ":tf_barcode");
            if (barcode == null || barcode.isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Barcode Should Not be Blank", "Barcode Should Not be Blank"));
                return;
            }
            returnResponse = DownloadDispatchImpl.updateVaDispatchRCDetail(sessionVariables, appl_no, barcode);
            if ("true".contains(returnResponse)) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Barcode Save Successfully", "Barcode Save Successfully"));
                return;
            } else if ("false".contains(returnResponse)) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error in Saving Barcode", "Error in Saving Barcode"));
                return;
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            return;
        }
    }

    public void onDateSelect() {
        setIsdisapble(false);
        setDownloadFileCommon(false);
        if (download != null && download.getListFileExport() != null && download.getListFileExport().size() > 0) {
            download = null;
        }
        if (byHandRegnNoDetails != null && byHandRegnNoDetails.getListFileExport() != null && !byHandRegnNoDetails.getListFileExport().isEmpty()) {
            byHandRegnNoDetails.getListFileExport().clear();
        }
        if (regnNoDetails != null && regnNoDetails.getListFileExport() != null && !regnNoDetails.getListFileExport().isEmpty()) {
            regnNoDetails.getListFileExport().clear();
        }
        if (downloadPenWorkDtls != null && downloadPenWorkDtls.getListFileExport() != null && !downloadPenWorkDtls.getListFileExport().isEmpty()) {
            downloadPenWorkDtls.getListFileExport().clear();
        }
        this.penDate = oldDate;
    }

    public void updatelblFile() {
        setIsExportFName(true);
        setIsExportAdd3(false);
        if (getDownload().getDownloadFileName() != null && !getDownload().getDownloadFileName().equals("")) {
            if (tmConfDispatchDobj.isIs_barcode_mandatory()) {
                setDownloadFileName(getDownload().getDownloadFileName());
            } else {
                setDownloadFileName(getDownload().getDownloadFileName() + "_lbl");
            }
        }
    }

    public void updatemghFile() {
        setIsExportFName(false);
        setIsExportAdd3(true);
        if (getDownload().getDownloadFileName() != null && !getDownload().getDownloadFileName().equals("")) {
            if (tmConfDispatchDobj.isIs_barcode_mandatory()) {
                setDownloadFileName(getDownload().getDownloadFileName());
            } else {
                setDownloadFileName(getDownload().getDownloadFileName() + "_mgh");

            }
        }
    }

    public void showPendingRecords() throws VahanException {
        try {
            if (tmConfDispatchDobj.isIs_speed_post_series()) {
                if (currentStartNo == null || currentStartNo.trim().equalsIgnoreCase("")) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Current Start No should not be Blank !!"));
                    return;
                }
                if (endNo == null || endNo.trim().equalsIgnoreCase("")) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "End No should not be Blank !!"));
                    return;
                }
                if (prefix == null || prefix.trim().equalsIgnoreCase("")) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "End No should not be Blank !!"));
                    return;
                }
                if (Integer.parseInt(endNo) <= Integer.parseInt(currentStartNo)) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Current Start No Should not be greater than End No !!"));
                    return;
                }
                if (getDownload() == null) {
                    DownloadDispatchDobj dobj = new DownloadDispatchDobj();
                    setDownload(dobj);
                }
                getDownload().setPrefix(this.prefix.toUpperCase());
                getDownload().setCurrentStartNo(this.currentStartNo);
                getDownload().setEndNo(this.endNo);
            }
            setDownloadFileAsPerBRstateRequirement(false);
            setDownloadFileCommon(false);
            setDownloadFileWhereBarcodeIsOptional(false);
            setDownloadFileAsPerRJstateRequirement(false);
            setShowRCDispatchByHandPanel(false);
            setShowUpdateDispatchPanel(false);
            setDownload(DownloadDispatchImpl.getDispatchRCDetails(sessionVariables, null, false, tmConfDispatchDobj, getDownload(), getOldDate(), false));
            if (download != null && download.getListFileExport().size() > 0) {
                setBtnGenRc(true);
                setBtnEdit(true);
                setBtnPrint(false);
                if (tmConfDispatchDobj.isIs_barcode_mandatory()) {
                    if ("BR".contains(tmConfDispatchDobj.getState_cd())) {
                        setDownloadFileAsPerBRstateRequirement(true);
                        setDownloadFileCommon(false);
                        setDownloadFileWhereBarcodeIsOptional(false);
                    } else if ("RJ".contains(tmConfDispatchDobj.getState_cd())) {
                        setDownloadFileAsPerRJstateRequirement(true);
                        setDownloadFileCommon(false);
                        setDownloadFileWhereBarcodeIsOptional(false);
                    } else {
                        setDownloadFileAsPerBRstateRequirement(false);
                        setDownloadFileAsPerRJstateRequirement(false);
                        setDownloadFileWhereBarcodeIsOptional(false);
                        setDownloadFileCommon(true);
                    }
                } else {
                    setDownloadFileAsPerBRstateRequirement(false);
                    setDownloadFileAsPerRJstateRequirement(false);
                    setDownloadFileCommon(false);
                    setDownloadFileWhereBarcodeIsOptional(true);
                }

            }
            if (download.getListFileExport().size() > 50) {
                isExceedPage = true;
            } else {
                isExceedPage = false;
            }
            setDownLoadLabel("Pending Records for RC dispatch as on date");
            getOldRecords().clear();
            if (downloadPenWorkDtls != null && downloadPenWorkDtls.getListFileExport() != null && !downloadPenWorkDtls.getListFileExport().isEmpty()) {
                downloadPenWorkDtls.getListFileExport().clear();
            }
            if (regnNoDetails != null && regnNoDetails.getListFileExport() != null && !regnNoDetails.getListFileExport().isEmpty()) {
                regnNoDetails.getListFileExport().clear();
            }
            if (byHandRegnNoDetails != null && byHandRegnNoDetails.getListFileExport() != null && !byHandRegnNoDetails.getListFileExport().isEmpty()) {
                byHandRegnNoDetails.getListFileExport().clear();
            }
            reset();
            if (getDownload() == null || getDownload().getListFileExport().isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Records not found for RC Dispatch List or KMS/HSRP is pending for RC Dispatch List !!"));
                return;
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            return;
        }
    }

    public void showAllPendingWork() throws VahanException {
        try {
            if (penDate == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Pending as on Date Should Not Blank !!"));
                return;
            }
            setDownloadFileAsPerBRstateRequirement(false);
            setDownloadFileCommon(false);
            setDownloadFileWhereBarcodeIsOptional(false);
            setDownloadFileAsPerRJstateRequirement(false);
            setShowRCDispatchByHandPanel(false);
            setShowUpdateDispatchPanel(false);
            DateFormat writeFormat = new SimpleDateFormat("dd-MMM-yyyy");
            setDownloadPenWorkDtls(DownloadDispatchImpl.getDispatchRCDetails(sessionVariables, null, true, tmConfDispatchDobj, getDownload(), getPenDate(), false));
            if (downloadPenWorkDtls != null && downloadPenWorkDtls.getListFileExport().size() > 0) {
                setBtnGenRc(true);
                setBtnEdit(true);
                setBtnPrint(false);
                setIsdisapble(true);
                if (tmConfDispatchDobj.isIs_barcode_mandatory()) {
                    if ("BR".contains(tmConfDispatchDobj.getState_cd())) {
                        setDownloadFileAsPerBRstateRequirement(true);
                        setDownloadFileCommon(false);
                        setDownloadFileWhereBarcodeIsOptional(false);
                    } else if ("RJ".contains(tmConfDispatchDobj.getState_cd())) {
                        setDownloadFileAsPerRJstateRequirement(true);
                        setDownloadFileCommon(false);
                        setDownloadFileWhereBarcodeIsOptional(false);
                    } else {
                        setDownloadFileAsPerBRstateRequirement(false);
                        setDownloadFileAsPerRJstateRequirement(false);
                        setDownloadFileWhereBarcodeIsOptional(false);
                        setDownloadFileCommon(true);
                    }
                } else {
                    setDownloadFileAsPerBRstateRequirement(false);
                    setDownloadFileAsPerRJstateRequirement(false);
                    setDownloadFileCommon(false);
                    setDownloadFileWhereBarcodeIsOptional(true);
                }

            } else {
                setIsdisapble(false);
            }
            if (downloadPenWorkDtls.getListFileExport().size() > 50) {
                isExceedPage = true;
            } else {
                isExceedPage = false;
            }
            setDownLoadLabel("Pending Records for RC dispatch as on date " + writeFormat.format(getPenDate()));
            getOldRecords().clear();
            if (download != null && download.getListFileExport() != null && !download.getListFileExport().isEmpty()) {
                download.getListFileExport().clear();
            }
            if (regnNoDetails != null && regnNoDetails.getListFileExport() != null && !regnNoDetails.getListFileExport().isEmpty()) {
                regnNoDetails.getListFileExport().clear();
            }
            if (byHandRegnNoDetails != null && byHandRegnNoDetails.getListFileExport() != null && !byHandRegnNoDetails.getListFileExport().isEmpty()) {
                byHandRegnNoDetails.getListFileExport().clear();
            }
            this.regn_no = null;
            if (downloadPenWorkDtls == null || downloadPenWorkDtls.getListFileExport().isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Pending Records Does Not exist or KMS/HSRP is Pending. RC Dispatched as on " + writeFormat.format(getPenDate()) + "  !!"));
                return;
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            return;
        }
    }

    public void showAllOldGeneratedRecordByDateListener() throws VahanException {
        try {
            if (getOldDate() == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pending as on Should Not be Blank", "Pending as on Should Not be Blank"));
                return;
            }
            setDownloadFileAsPerBRstateRequirement(false);
            setDownloadFileCommon(false);
            setDownloadFileWhereBarcodeIsOptional(false);
            setDownloadFileAsPerRJstateRequirement(false);
            setShowRCDispatchByHandPanel(false);
            setShowUpdateDispatchPanel(false);
            DateFormat writeFormat = new SimpleDateFormat("dd-MMM-yyyy");
            setDownLoadLabel("Downloaded Records for RC Dispatched as on " + writeFormat.format(getOldDate()));
            setDownload(DownloadDispatchImpl.getAllOldDispatchRCList(sessionVariables, getOldDate(), tmConfDispatchDobj));
            if (download != null && download.getListFileExport().size() > 0) {
                setBtnGenRc(false);
                setBtnEdit(false);
                setBtnPrint(true);
                if (tmConfDispatchDobj.isIs_barcode_mandatory()) {
                    if ("BR".contains(tmConfDispatchDobj.getState_cd())) {
                        setDownloadFileAsPerBRstateRequirement(true);
                        setDownloadFileCommon(false);
                        setDownloadFileWhereBarcodeIsOptional(false);
                    } else if ("RJ".contains(tmConfDispatchDobj.getState_cd())) {
                        setDownloadFileAsPerRJstateRequirement(true);
                        setDownloadFileCommon(false);
                        setDownloadFileWhereBarcodeIsOptional(false);
                    } else {
                        setDownloadFileAsPerBRstateRequirement(false);
                        setDownloadFileAsPerRJstateRequirement(false);
                        setDownloadFileWhereBarcodeIsOptional(false);
                        setDownloadFileCommon(true);
                    }
                } else {
                    setDownloadFileAsPerBRstateRequirement(false);
                    setDownloadFileAsPerRJstateRequirement(false);
                    setDownloadFileCommon(false);
                    setDownloadFileWhereBarcodeIsOptional(true);
                }
                if (tmConfDispatchDobj.isIs_sticker_print() || tmConfDispatchDobj.isIs_envelop_print()) {
                    setBtnEitherStickerOREnvelopPrint(true);
                } else {
                    setBtnEitherStickerOREnvelopPrint(false);
                }
            }
            if (download.getListFileExport().size() > 50) {
                isExceedPage = true;
            } else {
                isExceedPage = false;
            }
            getOldRecords().clear();
            if (download == null || download.getListFileExport().isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Records not found or you are not authorized to View the Records Generated as on date " + writeFormat.format(getOldDate()) + " !!"));
                return;
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            return;
        }
    }

    public void oldSelectDatewiseListener() {
        download = getSelectedOldDownload();
        if (download.getListFileExport().size() > 50) {
            isExceedPage = true;
        } else {
            isExceedPage = false;

        }
        setBtnGenRc(false);
        setBtnEdit(false);
        setBtnPrint(true);
    }

    public String printDispatchInfoReport() {
        if (getDownload().getListFileExportWithDocRefNo() != null) {
            getDownload().getListFileExportWithDocRefNo().clear();
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("downloadlist", getDownload().getListFileExport());
        return "printDispatchInfo";
    }

    public String printDispatchInfoReportCoverPage() {
        if (getDownload().getListFileExportWithDocRefNo() != null) {
            getDownload().getListFileExportWithDocRefNo().clear();
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("downloadlist", getDownload().getListFileExport());
        return "printDispatchInfoCoverPage";
    }

    public String printDispatchInfoReportEnvelop() {
        if (getDownload().getListFileExportWithDocRefNo() != null) {
            getDownload().getListFileExportWithDocRefNo().clear();
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("downloadlist", getDownload().getListFileExport());
        return "printDispatchInfoEnvelop";
    }

    public String printDispatchLetter() {
        String returnURL = null;
        if (getDownload().getListFileExportWithDocRefNo() != null) {
            getDownload().getListFileExportWithDocRefNo().clear();
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("downloadlist", getDownload().getListFileExport());
        if ("RJ".contains(Util.getUserStateCode())) {
            returnURL = "printRJDispatchHindiLetter";
        } else if ("BR".contains(Util.getUserStateCode())) {
            returnURL = "printBRDispatchHindiLetter";
        } else {
            returnURL = "printDispatchLetter";
        }
        return returnURL;
    }

    public String printDispatchAddressInfoReport() {
        if (getDownload().getListFileExportWithDocRefNo() != null) {
            getDownload().getListFileExportWithDocRefNo().clear();
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("downloadlist", getDownload().getListFileExport());
        return "printDispatchAddressInfo";
    }

    public void dispatchRCByHand() {
        boolean isSuccess = false;
        try {
            if (this.getRemark() == null || this.getRemark().isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Remark Should Not be Blank", "Remark Should Not be Blank"));
                return;
            }
            if (this.getDt_hand_over() == null || this.getDt_hand_over().equals("")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Handed Over Date Should Not be Blank", "Handed Over Date Should Not be Blank"));
                return;
            }
            getByHandRegnNoDetails().getListFileExport().get(0).setRemark(this.getRemark().toUpperCase());
            getByHandRegnNoDetails().getListFileExport().get(0).setDt_hand_over(this.getDt_hand_over());
            isSuccess = DownloadDispatchImpl.saveAndMoveInHistoryDispatchRCByHand(sessionVariables, getByHandRegnNoDetails(), tmConfDispatchDobj);
            if (isSuccess) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Registration No " + getByHandRegnNoDetails().getListFileExport().get(0).getRegnNo() + " is Handed Over Successfully", "Registration No " + getByHandRegnNoDetails().getListFileExport().get(0).getRegnNo() + " is Handed Over Successfully"));
                getByHandRegnNoDetails().getListFileExport().clear();
                reset();
            } else {
                PrimeFaces.current().dialog().showMessageDynamic(
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!",
                                "Nothing to Generate Dispatch File Because Either Empty List or all Barcodes are Empty"));
                return;
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            return;
        }
    }

    public void updateVHdownloadStatus() {
        try {
            if (sessionVariables == null || sessionVariables.getStateCodeSelected() == null || getDownload() == null || getDownload().getListFileExport() == null || tmConfDispatchDobj == null) {
                throw new VahanException("Error in Generating Dispatch File,Please try again ");
            }
            if (DownloadDispatchImpl.updateVhRcPrintAfterIsDownloadedAction(sessionVariables, getDownload(), tmConfDispatchDobj)) {
                getDownload().getListFileExport().clear();
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            return;
        }
    }

    public void saveAndMoveInHistory() {
        try {
            if (DownloadDispatchImpl.saveAndMoveInHistory(sessionVariables, getDownload(), tmConfDispatchDobj)) {
                getDownload().getListFileExport().clear();
            } else {
                PrimeFaces.current().dialog().showMessageDynamic(
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!",
                                "Nothing to Generate Dispatch File Because Either Empty List or all Barcodes are Empty"));
                return;
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            return;
        }
    }

    public void oldDateListener(AjaxBehaviorEvent event) throws VahanException {
        try {
            if (getOldDate() == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pending as on Should Not be Blank", "Pending as on Should Not be Blank"));
                return;
            }
            DateFormat writeFormat = new SimpleDateFormat("dd-MMM-yyyy");
            if (download != null && download.getListFileExport() != null && !download.getListFileExport().isEmpty()) {
                download.getListFileExport().clear();
            }
            setDownloadFileAsPerBRstateRequirement(false);
            setDownloadFileCommon(false);
            setDownloadFileWhereBarcodeIsOptional(false);
            setDownloadFileAsPerRJstateRequirement(false);
            setShowRCDispatchByHandPanel(false);
            setShowUpdateDispatchPanel(false);
            oldRecords = DownloadDispatchImpl.getOldDispatchRCList(sessionVariables, getOldDate(), tmConfDispatchDobj);
            if (oldRecords == null || oldRecords.isEmpty()) {
                throw new VahanException("RC Dispatch List does not exist or you are not authorized to View RC Dispatch List Generated as on date " + writeFormat.format(getOldDate()).toUpperCase() + " !!!");
            }

        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            return;
        }
    }

    public void oldSelectDwlListener() {
        try {
            download = getSelectedOldDownload();
            if (download != null && download.getDownloadFileName() != null && !"".contains(download.getDownloadFileName())) {
                download = DownloadDispatchImpl.getOldDispatchRCListFilewise(sessionVariables, download.getDownloadFileName()).get(0);
            }
            if (download != null && download.getListFileExport().size() > 0) {
                setBtnGenRc(false);
                setBtnEdit(false);
                setBtnPrint(true);
                if (tmConfDispatchDobj.isIs_barcode_mandatory()) {
                    if ("BR".contains(tmConfDispatchDobj.getState_cd())) {
                        setDownloadFileAsPerBRstateRequirement(true);
                        setDownloadFileCommon(false);
                        setDownloadFileWhereBarcodeIsOptional(false);
                    } else if ("RJ".contains(tmConfDispatchDobj.getState_cd())) {
                        setDownloadFileAsPerRJstateRequirement(true);
                        setDownloadFileCommon(false);
                        setDownloadFileWhereBarcodeIsOptional(false);
                    } else {
                        setDownloadFileAsPerBRstateRequirement(false);
                        setDownloadFileAsPerRJstateRequirement(false);
                        setDownloadFileWhereBarcodeIsOptional(false);
                        setDownloadFileCommon(true);
                    }
                } else {
                    setDownloadFileAsPerBRstateRequirement(false);
                    setDownloadFileAsPerRJstateRequirement(false);
                    setDownloadFileCommon(false);
                    setDownloadFileWhereBarcodeIsOptional(true);
                }
                if (tmConfDispatchDobj.isIs_sticker_print() || tmConfDispatchDobj.isIs_envelop_print()) {
                    setBtnEitherStickerOREnvelopPrint(true);
                } else {
                    setBtnEitherStickerOREnvelopPrint(false);
                }
            }
            if (download.getListFileExport().size() > 50) {
                isExceedPage = true;
            } else {
                isExceedPage = false;
            }
            setDownLoadLabel("Downloaded Records for RC Dispatched as on " + download.getDownloadFileName());
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            return;
        }
    }

    public void updateDispatchDetails() {
        String regn_no_entered = null;
        regn_no_by_hand = null;
        oldDate = null;
        if (getDownloadPenWorkDtls() != null && getDownloadPenWorkDtls().getListFileExport() != null && !getDownloadPenWorkDtls().getListFileExport().isEmpty()) {
            getDownloadPenWorkDtls().getListFileExport().clear();
        }
        if (getRegnNoDetails() != null && getRegnNoDetails().getListFileExport() != null && !getRegnNoDetails().getListFileExport().isEmpty()) {
            getRegnNoDetails().getListFileExport().clear();
        }
        if (getDownload() != null && getDownload().getListFileExport() != null && !getDownload().getListFileExport().isEmpty()) {
            getDownload().getListFileExport().clear();
        }
        if ((regn_no == null || regn_no.trim().equalsIgnoreCase("")) && (this.regn_no_by_hand == null || this.regn_no_by_hand.isEmpty())) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration No should not be Blank !!"));
            return;
        }
        try {
            if (this.regn_no != null && !this.regn_no.isEmpty()) {
                regn_no_entered = regn_no;
            }
            setRegnNoDetails(DownloadDispatchImpl.getDispatchRCDetails(sessionVariables, regn_no_entered.trim().toUpperCase(), false, tmConfDispatchDobj, getDownload(), getOldDate(), false));
            setDownloadFileAsPerBRstateRequirement(false);
            setDownloadFileCommon(false);
            setDownloadFileWhereBarcodeIsOptional(false);
            setDownloadFileAsPerRJstateRequirement(false);
            setShowRCDispatchByHandPanel(false);
            setShowUpdateDispatchPanel(false);
            if (getRegnNoDetails().getPending_dispatch_rc_details() != null && !"".contains(getRegnNoDetails().getPending_dispatch_rc_details())) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", getRegnNoDetails().getPending_dispatch_rc_details() + " !!"));
                return;
            }
            if (getRegnNoDetails().getDispatch_rc_details() != null && !"".contains(getRegnNoDetails().getDispatch_rc_details())) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", getRegnNoDetails().getDispatch_rc_details() + " !!"));
                return;
            }
            if (getRegnNoDetails().getListFileExport().isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration no does not exist!!"));
                return;
            }
            if (regnNoDetails != null && regnNoDetails.getListFileExport().size() > 0) {
                setBtnGenRc(true);
                setBtnEdit(true);
                setBtnPrint(false);
                setDt_hand_over(DateUtil.parseDate(DateUtil.getCurrentDate()));
                setShowRCDispatchByHandPanel(false);
                setShowUpdateDispatchPanel(true);
                if (regnNoDetails.getListFileExport().size() > 50) {
                    isExceedPage = true;
                } else {
                    isExceedPage = false;
                }
                if (tmConfDispatchDobj.isIs_barcode_mandatory()) {
                    if ("BR".contains(tmConfDispatchDobj.getState_cd())) {
                        setDownloadFileAsPerBRstateRequirement(true);
                        setDownloadFileCommon(false);
                        setDownloadFileWhereBarcodeIsOptional(false);
                    } else if ("RJ".contains(tmConfDispatchDobj.getState_cd())) {
                        setDownloadFileAsPerRJstateRequirement(true);
                        setDownloadFileCommon(false);
                        setDownloadFileWhereBarcodeIsOptional(false);
                    } else {
                        setDownloadFileAsPerBRstateRequirement(false);
                        setDownloadFileAsPerRJstateRequirement(false);
                        setDownloadFileWhereBarcodeIsOptional(false);
                        setDownloadFileCommon(false);
                    }
                } else {
                    setDownloadFileAsPerBRstateRequirement(false);
                    setDownloadFileAsPerRJstateRequirement(false);
                    setDownloadFileCommon(false);
                    setDownloadFileWhereBarcodeIsOptional(true);
                }
                setDownLoadLabel("Pending Record for RC dispatch by Regisitration No.");
                getOldRecords().clear();
                setByHandRegnNoDetails(getRegnNoDetails());
                this.regn_no = null;
            } else {
                setShowRCDispatchByHandPanel(false);
                setShowUpdateDispatchPanel(false);
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            return;
        }
    }

    public void getDispatchGetailsByHand() {
        String regn_no_entered = null;
        oldDate = null;
        regn_no = null;
        setByHandRegnNoDetails(null);
        setShowRCDispatchByHandPanel(false);
        if (getDownloadPenWorkDtls() != null && getDownloadPenWorkDtls().getListFileExport() != null && !getDownloadPenWorkDtls().getListFileExport().isEmpty()) {
            getDownloadPenWorkDtls().getListFileExport().clear();
        }
        if (getRegnNoDetails() != null && getRegnNoDetails().getListFileExport() != null && !getRegnNoDetails().getListFileExport().isEmpty()) {
            getRegnNoDetails().getListFileExport().clear();
        }
        if (getDownload() != null && getDownload().getListFileExport() != null && !getDownload().getListFileExport().isEmpty()) {
            getDownload().getListFileExport().clear();
        }
        if ((regn_no_by_hand == null || regn_no_by_hand.trim().equalsIgnoreCase("")) && (this.regn_no_by_hand == null || this.regn_no_by_hand.isEmpty())) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration No should not be Blank !!"));
            return;
        }
        try {
            if (this.regn_no_by_hand != null || !this.regn_no_by_hand.isEmpty()) {
                regn_no_entered = getRegn_no_by_hand();
            }
            setRegnNoDetails(DownloadDispatchImpl.getDispatchRCDetails(sessionVariables, regn_no_entered.trim().toUpperCase(), false, tmConfDispatchDobj, getDownload(), getOldDate(), true));
            setDownloadFileAsPerBRstateRequirement(false);
            setDownloadFileCommon(false);
            setDownloadFileWhereBarcodeIsOptional(false);
            setDownloadFileAsPerRJstateRequirement(false);
            setShowRCDispatchByHandPanel(false);
            if (getRegnNoDetails().getPending_dispatch_rc_details() != null && !"".contains(getRegnNoDetails().getPending_dispatch_rc_details())) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", getRegnNoDetails().getPending_dispatch_rc_details() + " !!"));
                return;
            }
            if (getRegnNoDetails().getDispatch_rc_details() != null && !"".contains(getRegnNoDetails().getDispatch_rc_details())) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", getRegnNoDetails().getDispatch_rc_details() + " !!"));
                return;
            }
            if (getRegnNoDetails().getListFileExport().isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration no does not exist!!"));
                return;
            }
            if (regnNoDetails != null && regnNoDetails.getListFileExport().size() > 0) {
                setBtnGenRc(true);
                setBtnEdit(true);
                setBtnPrint(false);
                setDt_hand_over(DateUtil.parseDate(DateUtil.getCurrentDate()));
                setShowRCDispatchByHandPanel(true);
                if (regnNoDetails.getListFileExport().size() > 50) {
                    isExceedPage = true;
                } else {
                    isExceedPage = false;
                }
                if (tmConfDispatchDobj.isIs_barcode_mandatory()) {
                    if ("BR".contains(tmConfDispatchDobj.getState_cd())) {
                        setDownloadFileAsPerBRstateRequirement(true);
                        setDownloadFileCommon(false);
                        setDownloadFileWhereBarcodeIsOptional(false);
                    } else if ("RJ".contains(tmConfDispatchDobj.getState_cd())) {
                        setDownloadFileAsPerRJstateRequirement(true);
                        setDownloadFileCommon(false);
                        setDownloadFileWhereBarcodeIsOptional(false);
                    } else {
                        setDownloadFileAsPerBRstateRequirement(false);
                        setDownloadFileAsPerRJstateRequirement(false);
                        setDownloadFileWhereBarcodeIsOptional(false);
                        setDownloadFileCommon(true);
                    }
                } else {
                    setDownloadFileAsPerBRstateRequirement(false);
                    setDownloadFileAsPerRJstateRequirement(false);
                    setDownloadFileCommon(false);
                    setDownloadFileWhereBarcodeIsOptional(true);
                }
                setDownLoadLabel("Pending Record for RC dispatch by Regisitration No.");
                getOldRecords().clear();
                setByHandRegnNoDetails(getRegnNoDetails());
                if (this.regn_no_by_hand != null && !this.regn_no_by_hand.isEmpty()) {
                    setRegnNoDetails(null);
                }
                this.regn_no = null;
                this.setRegn_no_by_hand(null);
            } else {
                setShowRCDispatchByHandPanel(false);
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            return;
        }
    }

    public void SaveSpeedPostSeriesGetails() {
        if (getRegnNoDetails() != null && getRegnNoDetails().getListFileExport() != null && !getRegnNoDetails().getListFileExport().isEmpty()) {
            getRegnNoDetails().getListFileExport().clear();
        }
        if (getDownload() != null && getDownload().getListFileExport() != null && !getDownload().getListFileExport().isEmpty()) {
            getDownload().getListFileExport().clear();
        }
        if (currentStartNo == null || currentStartNo.trim().equalsIgnoreCase("")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Current Start No should not be Blank !!"));
            return;
        }
        if (endNo == null || endNo.trim().equalsIgnoreCase("")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "End No should not be Blank !!"));
            return;
        }
        if (prefix == null || prefix.trim().equalsIgnoreCase("")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "End No should not be Blank !!"));
            return;
        }
        if (Integer.parseInt(endNo) <= Integer.parseInt(currentStartNo)) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Current Start No Should not be greater than End No !!"));
            return;
        }
        try {
            if (getDownload() == null) {
                DownloadDispatchDobj dobj = new DownloadDispatchDobj();
                setDownload(dobj);
            }
            getDownload().setPrefix(this.prefix.toUpperCase());
            getDownload().setCurrentStartNo(this.currentStartNo);
            getDownload().setEndNo(this.endNo);
            getDownload().setUpdateSeries(isIsSeriesUpdate());
            boolean status = DownloadDispatchImpl.saveSpeedPostSeries(sessionVariables, getDownload());
            if (status) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Record Save successfully !!"));
            } else {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Error in saving records !!"));
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            return;
        }
    }

    public void revertGetails() {
        if (getRegnNoDetails() != null && getRegnNoDetails().getListFileExport() != null && !getRegnNoDetails().getListFileExport().isEmpty()) {
            getRegnNoDetails().getListFileExport().clear();
        }
        if (getDownload() != null && getDownload().getListFileExport() != null && !getDownload().getListFileExport().isEmpty()) {
            getDownload().getListFileExport().clear();
        }
        if (getOldRecords() != null || getOldRecords().isEmpty()) {
            getOldRecords().clear();
        }
        if (flatFileName == null || flatFileName.isEmpty()) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Flat File hould not be Blank !!"));
            return;
        }
        try {
            boolean status = DownloadDispatchImpl.revertDetails(sessionVariables, flatFileName, tmConfDispatchDobj);
            if (status) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Record Revert successfully for flat file " + flatFileName.toUpperCase() + " !!"));
                this.flatFileName = null;
            } else {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Error in Reverting records for flat file " + flatFileName.toUpperCase() + " !!"));
                this.flatFileName = null;
            }
        } catch (VahanException ve) {
            this.flatFileName = null;
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception ex) {
            this.flatFileName = null;
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            return;
        }
    }

    public void reset() {
        this.regn_no = null;
        this.regn_no_by_hand = null;
        this.remark = null;
        this.dt_hand_over = null;
    }

    public void downloadCSV() {
        try {
            String fileName = this.downloadFileName + ".csv";
            List<CsvFormatDO> csv = new ArrayList<>();
            String downloadCVSString = "";
            for (DownloadDispatchDobj dobj : getDownload().getListFileExport()) {
                CsvFormatDO dos = new CsvFormatDO();
                if (downloadFileCommon) {
//                    downloadCVSString = "OWNER NAME" + " " + "FATHER NAME" + " " + "ADD1" + " " + "ADD2" + " " + "ADD3" + " " + "CITY" + " " + "PINCODE" + " " + "REGN NO" + " " + "BARCODE" + " " + "MOBILE NO" + "\n";
                    downloadCVSString = dobj.getOwnerName() + " " + dobj.getFname() + " " + dobj.getAdd1() + " " + dobj.getAdd2() + " " + dobj.getAdd3() + " " + dobj.getCity() + " " + dobj.getPincode() + " " + dobj.getRegnNo() + " " + dobj.getDispatch_ref_no_for_display() + " " + dobj.getMobile_no();
                } else if (downloadFileAsPerBRstateRequirement) {
//                     downloadCVSString = "OWNER NAME" + " " + "FATHER NAME" + " " + "ADD1" + " " + "ADD2" + " " + "ADD3" + " " + "CITY" + " " + "PINCODE" + " " + "REGN NO" + " " + "BARCODE" + " " + "MOBILE NO" + "\n";
                    downloadCVSString = dobj.getDispatch_ref_no_for_display() + " " + dobj.getRegnNo() + " " + dobj.getCity() + " " + dobj.getPincode() + " " + dobj.getOwnerName() + " " + dobj.getAdd1() + " " + dobj.getAdd2() + " " + dobj.getAdd3() + " " + dobj.getMobile_no() + " " + dobj.getFname();
                } else if (downloadFileWhereBarcodeIsOptional) {
//                     downloadCVSString = "OWNER NAME" + " " + "FATHER NAME" + " " + "ADD1" + " " + "ADD2" + " " + "ADD3" + " " + "CITY" + " " + "PINCODE" + " " + "REGN NO" + " " + "BARCODE" + " " + "MOBILE NO" + "\n";
                    downloadCVSString = dobj.getDispatch_ref_no_for_display() + " " + dobj.getRegnNo() + " " + dobj.getCity() + " " + dobj.getPincode() + " " + dobj.getOwnerName() + " " + dobj.getFname() + " " + dobj.getAdd1() + " " + dobj.getAdd2() + " " + dobj.getAdd3() + " " + dobj.getEmail_id() + " " + dobj.getMobile_no();
                } else if (downloadFileAsPerRJstateRequirement) {
//                     downloadCVSString = "OWNER NAME" + " " + "FATHER NAME" + " " + "ADD1" + " " + "ADD2" + " " + "ADD3" + " " + "CITY" + " " + "PINCODE" + " " + "REGN NO" + " " + "BARCODE" + " " + "MOBILE NO" + "\n";
                    downloadCVSString = dobj.getDispatch_ref_no_for_display() + " " + dobj.getRegnNo() + " " + dobj.getCity() + " " + dobj.getPincode() + " " + dobj.getOwnerName() + " " + dobj.getAdd1() + " " + dobj.getAdd2() + " " + dobj.getAdd3() + " " + dobj.getEmail_id() + " " + dobj.getMobile_no();
                } else {
                    downloadCVSString = dobj.getDispatch_ref_no_for_display() + " " + dobj.getRegnNo() + " " + dobj.getCity() + " " + dobj.getPincode() + " " + dobj.getOwnerName() + " " + dobj.getFname() + " " + dobj.getAdd1() + " " + dobj.getAdd2() + " " + dobj.getAdd3() + " " + dobj.getEmail_id() + " " + dobj.getMobile_no();
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
     * @return the download
     */
    public DownloadDispatchDobj getDownload() {
        return download;
    }

    /**
     * @param download the download to set
     */
    public void setDownload(DownloadDispatchDobj download) {
        this.download = download;
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
     * @return the downLoadLabel
     */
    public String getDownLoadLabel() {
        return downLoadLabel;
    }

    /**
     * @param downLoadLabel the downLoadLabel to set
     */
    public void setDownLoadLabel(String downLoadLabel) {
        this.downLoadLabel = downLoadLabel;
    }

    /**
     * @return the btnDownloadFileLabel
     */
    public String getBtnDownloadFileLabel() {
        return btnDownloadFileLabel;
    }

    /**
     * @param btnDownloadFileLabel the btnDownloadFileLabel to set
     */
    public void setBtnDownloadFileLabel(String btnDownloadFileLabel) {
        this.btnDownloadFileLabel = btnDownloadFileLabel;
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
    public List<DownloadDispatchDobj> getOldRecords() {
        return oldRecords;
    }

    /**
     * @param oldRecords the oldRecords to set
     */
    public void setOldRecords(List<DownloadDispatchDobj> oldRecords) {
        this.oldRecords = oldRecords;
    }

    /**
     * @return the selectedOldDownload
     */
    public DownloadDispatchDobj getSelectedOldDownload() {
        return selectedOldDownload;
    }

    /**
     * @param selectedOldDownload the selectedOldDownload to set
     */
    public void setSelectedOldDownload(DownloadDispatchDobj selectedOldDownload) {
        this.selectedOldDownload = selectedOldDownload;
    }

    /**
     * @return the btnGenRc
     */
    public boolean isBtnGenRc() {
        return btnGenRc;
    }

    /**
     * @param btnGenRc the btnGenRc to set
     */
    public void setBtnGenRc(boolean btnGenRc) {
        this.btnGenRc = btnGenRc;
    }

    /**
     * @return the isExportFName
     */
    public boolean isIsExportFName() {
        return isExportFName;
    }

    /**
     * @param isExportFName the isExportFName to set
     */
    public void setIsExportFName(boolean isExportFName) {
        this.isExportFName = isExportFName;
    }

    /**
     * @return the isExportAdd3
     */
    public boolean isIsExportAdd3() {
        return isExportAdd3;
    }

    /**
     * @param isExportAdd3 the isExportAdd3 to set
     */
    public void setIsExportAdd3(boolean isExportAdd3) {
        this.isExportAdd3 = isExportAdd3;
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
     * @return the btnEdit
     */
    public boolean isBtnEdit() {
        return btnEdit;
    }

    /**
     * @param btnEdit the btnEdit to set
     */
    public void setBtnEdit(boolean btnEdit) {
        this.btnEdit = btnEdit;
    }

    /**
     * @return the btnPrint
     */
    public boolean isBtnPrint() {
        return btnPrint;
    }

    /**
     * @param btnPrint the btnPrint to set
     */
    public void setBtnPrint(boolean btnPrint) {
        this.btnPrint = btnPrint;
    }

    /**
     * @return the filteredSeat
     */
    public List<DownloadDispatchDobj> getFilteredSeat() {
        return filteredSeat;
    }

    /**
     * @param filteredSeat the filteredSeat to set
     */
    public void setFilteredSeat(List<DownloadDispatchDobj> filteredSeat) {
        this.filteredSeat = filteredSeat;
    }

    /**
     * @return the regn_no
     */
    public String getRegn_no() {
        return regn_no;
    }

    /**
     * @param regn_no the regn_no to set
     */
    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    /**
     * @return the regnNoDetails
     */
    public DownloadDispatchDobj getRegnNoDetails() {
        return regnNoDetails;
    }

    /**
     * @param regnNoDetails the regnNoDetails to set
     */
    public void setRegnNoDetails(DownloadDispatchDobj regnNoDetails) {
        this.regnNoDetails = regnNoDetails;
    }

    /**
     * @return the downloadPenWorkDtls
     */
    public DownloadDispatchDobj getDownloadPenWorkDtls() {
        return downloadPenWorkDtls;
    }

    /**
     * @param downloadPenWorkDtls the downloadPenWorkDtls to set
     */
    public void setDownloadPenWorkDtls(DownloadDispatchDobj downloadPenWorkDtls) {
        this.downloadPenWorkDtls = downloadPenWorkDtls;
    }

    /**
     * @return the downloadLabelForLblFile
     */
    public String getDownloadLabelForLblFile() {
        return downloadLabelForLblFile;
    }

    /**
     * @param downloadLabelForLblFile the downloadLabelForLblFile to set
     */
    public void setDownloadLabelForLblFile(String downloadLabelForLblFile) {
        this.downloadLabelForLblFile = downloadLabelForLblFile;
    }

    /**
     * @return the downloadLabelForMghFile
     */
    public String getDownloadLabelForMghFile() {
        return downloadLabelForMghFile;
    }

    /**
     * @param downloadLabelForMghFile the downloadLabelForMghFile to set
     */
    public void setDownloadLabelForMghFile(String downloadLabelForMghFile) {
        this.downloadLabelForMghFile = downloadLabelForMghFile;
    }

    /**
     * @return the currentStartNo
     */
    public String getCurrentStartNo() {
        return currentStartNo;
    }

    /**
     * @param currentStartNo the currentStartNo to set
     */
    public void setCurrentStartNo(String currentStartNo) {
        this.currentStartNo = currentStartNo;
    }

    /**
     * @return the endNo
     */
    public String getEndNo() {
        return endNo;
    }

    /**
     * @param endNo the endNo to set
     */
    public void setEndNo(String endNo) {
        this.endNo = endNo;
    }

    /**
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @param prefix the prefix to set
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * @return the isSpeedPostSeries
     */
    public boolean isIsSpeedPostSeries() {
        return isSpeedPostSeries;
    }

    /**
     * @param isSpeedPostSeries the isSpeedPostSeries to set
     */
    public void setIsSpeedPostSeries(boolean isSpeedPostSeries) {
        this.isSpeedPostSeries = isSpeedPostSeries;
    }

    /**
     * @return the isShowingAllPendingWork
     */
    public boolean isIsShowingAllPendingWork() {
        return isShowingAllPendingWork;
    }

    /**
     * @param isShowingAllPendingWork the isShowingAllPendingWork to set
     */
    public void setIsShowingAllPendingWork(boolean isShowingAllPendingWork) {
        this.isShowingAllPendingWork = isShowingAllPendingWork;
    }

    /**
     * @return the is_search_by_regn_no
     */
    public boolean isIs_search_by_regn_no() {
        return is_search_by_regn_no;
    }

    /**
     * @param is_search_by_regn_no the is_search_by_regn_no to set
     */
    public void setIs_search_by_regn_no(boolean is_search_by_regn_no) {
        this.is_search_by_regn_no = is_search_by_regn_no;
    }

    /**
     * @return the printEnvelopStickerLabel
     */
    public String getPrintEnvelopStickerLabel() {
        return printEnvelopStickerLabel;
    }

    /**
     * @param printEnvelopStickerLabel the printEnvelopStickerLabel to set
     */
    public void setPrintEnvelopStickerLabel(String printEnvelopStickerLabel) {
        this.printEnvelopStickerLabel = printEnvelopStickerLabel;
    }

    /**
     * @return the isprintCoverPage
     */
    public boolean isIsprintCoverPage() {
        return isprintCoverPage;
    }

    /**
     * @param isprintCoverPage the isprintCoverPage to set
     */
    public void setIsprintCoverPage(boolean isprintCoverPage) {
        this.isprintCoverPage = isprintCoverPage;
    }

    /**
     * @return the isSeriesUpdate
     */
    public boolean isIsSeriesUpdate() {
        return isSeriesUpdate;
    }

    /**
     * @param isSeriesUpdate the isSeriesUpdate to set
     */
    public void setIsSeriesUpdate(boolean isSeriesUpdate) {
        this.isSeriesUpdate = isSeriesUpdate;
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

    /**
     * @return the flatFileName
     */
    public String getFlatFileName() {
        return flatFileName;
    }

    /**
     * @param flatFileName the flatFileName to set
     */
    public void setFlatFileName(String flatFileName) {
        this.flatFileName = flatFileName;
    }

    /**
     * @return the is_rcdispatch_letter
     */
    public boolean isIs_rcdispatch_letter() {
        return is_rcdispatch_letter;
    }

    /**
     * @param is_rcdispatch_letter the is_rcdispatch_letter to set
     */
    public void setIs_rcdispatch_letter(boolean is_rcdispatch_letter) {
        this.is_rcdispatch_letter = is_rcdispatch_letter;
    }

    /**
     * @return the is_edit_on_all_pending_records
     */
    public boolean isIs_edit_on_all_pending_records() {
        return is_edit_on_all_pending_records;
    }

    /**
     * @param is_edit_on_all_pending_records the is_edit_on_all_pending_records
     * to set
     */
    public void setIs_edit_on_all_pending_records(boolean is_edit_on_all_pending_records) {
        this.is_edit_on_all_pending_records = is_edit_on_all_pending_records;
    }

    /**
     * @return the regn_no_by_hand
     */
    public String getRegn_no_by_hand() {
        return regn_no_by_hand;
    }

    /**
     * @param regn_no_by_hand the regn_no_by_hand to set
     */
    public void setRegn_no_by_hand(String regn_no_by_hand) {
        this.regn_no_by_hand = regn_no_by_hand;
    }

    /**
     * @return the Is_rc_dispatch_by_hand
     */
    public boolean isIs_rc_dispatch_by_hand() {
        return Is_rc_dispatch_by_hand;
    }

    /**
     * @param Is_rc_dispatch_by_hand the Is_rc_dispatch_by_hand to set
     */
    public void setIs_rc_dispatch_by_hand(boolean Is_rc_dispatch_by_hand) {
        this.Is_rc_dispatch_by_hand = Is_rc_dispatch_by_hand;
    }

    /**
     * @return the dt_hand_over
     */
    public Date getDt_hand_over() {
        return dt_hand_over;
    }

    /**
     * @param dt_hand_over the dt_hand_over to set
     */
    public void setDt_hand_over(Date dt_hand_over) {
        this.dt_hand_over = dt_hand_over;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark the remark to set
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return the byHandRegnNoDetails
     */
    public DownloadDispatchDobj getByHandRegnNoDetails() {
        return byHandRegnNoDetails;
    }

    /**
     * @param byHandRegnNoDetails the byHandRegnNoDetails to set
     */
    public void setByHandRegnNoDetails(DownloadDispatchDobj byHandRegnNoDetails) {
        this.byHandRegnNoDetails = byHandRegnNoDetails;
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
     * @return the isdisapble
     */
    public boolean isIsdisapble() {
        return isdisapble;
    }

    /**
     * @param isdisapble the isdisapble to set
     */
    public void setIsdisapble(boolean isdisapble) {
        this.isdisapble = isdisapble;
    }

    /**
     * @return the penDate
     */
    public Date getPenDate() {
        return penDate;
    }

    /**
     * @param penDate the penDate to set
     */
    public void setPenDate(Date penDate) {
        this.penDate = penDate;
    }

    /**
     * @return the isDispatchAddress
     */
    public boolean isIsDispatchAddress() {
        return isDispatchAddress;
    }

    /**
     * @param isDispatchAddress the isDispatchAddress to set
     */
    public void setIsDispatchAddress(boolean isDispatchAddress) {
        this.isDispatchAddress = isDispatchAddress;
    }

    /**
     * @return the btnEitherStickerOREnvelopPrint
     */
    public boolean isBtnEitherStickerOREnvelopPrint() {
        return btnEitherStickerOREnvelopPrint;
    }

    /**
     * @param btnEitherStickerOREnvelopPrint the btnEitherStickerOREnvelopPrint
     * to set
     */
    public void setBtnEitherStickerOREnvelopPrint(boolean btnEitherStickerOREnvelopPrint) {
        this.btnEitherStickerOREnvelopPrint = btnEitherStickerOREnvelopPrint;
    }

    /**
     * @return the downloadFileAsPerBRstateRequirement
     */
    public boolean isDownloadFileAsPerBRstateRequirement() {
        return downloadFileAsPerBRstateRequirement;
    }

    /**
     * @param downloadFileAsPerBRstateRequirement the
     * downloadFileAsPerBRstateRequirement to set
     */
    public void setDownloadFileAsPerBRstateRequirement(boolean downloadFileAsPerBRstateRequirement) {
        this.downloadFileAsPerBRstateRequirement = downloadFileAsPerBRstateRequirement;
    }

    /**
     * @return the downloadFileAsPerRJstateRequirement
     */
    public boolean isDownloadFileAsPerRJstateRequirement() {
        return downloadFileAsPerRJstateRequirement;
    }

    /**
     * @param downloadFileAsPerRJstateRequirement the
     * downloadFileAsPerRJstateRequirement to set
     */
    public void setDownloadFileAsPerRJstateRequirement(boolean downloadFileAsPerRJstateRequirement) {
        this.downloadFileAsPerRJstateRequirement = downloadFileAsPerRJstateRequirement;
    }

    /**
     * @return the downloadFileCommon
     */
    public boolean isDownloadFileCommon() {
        return downloadFileCommon;
    }

    /**
     * @param downloadFileCommon the downloadFileCommon to set
     */
    public void setDownloadFileCommon(boolean downloadFileCommon) {
        this.downloadFileCommon = downloadFileCommon;
    }

    /**
     * @return the downloadFileWhereBarcodeIsOptional
     */
    public boolean isDownloadFileWhereBarcodeIsOptional() {
        return downloadFileWhereBarcodeIsOptional;
    }

    /**
     * @param downloadFileWhereBarcodeIsOptional the
     * downloadFileWhereBarcodeIsOptional to set
     */
    public void setDownloadFileWhereBarcodeIsOptional(boolean downloadFileWhereBarcodeIsOptional) {
        this.downloadFileWhereBarcodeIsOptional = downloadFileWhereBarcodeIsOptional;
    }

    /**
     * @return the showRCDispatchByHandPanel
     */
    public boolean isShowRCDispatchByHandPanel() {
        return showRCDispatchByHandPanel;
    }

    /**
     * @param showRCDispatchByHandPanel the showRCDispatchByHandPanel to set
     */
    public void setShowRCDispatchByHandPanel(boolean showRCDispatchByHandPanel) {
        this.showRCDispatchByHandPanel = showRCDispatchByHandPanel;
    }

    /**
     * @return the showUpdateDispatchPanel
     */
    public boolean isShowUpdateDispatchPanel() {
        return showUpdateDispatchPanel;
    }

    /**
     * @param showUpdateDispatchPanel the showUpdateDispatchPanel to set
     */
    public void setShowUpdateDispatchPanel(boolean showUpdateDispatchPanel) {
        this.showUpdateDispatchPanel = showUpdateDispatchPanel;
    }
}
