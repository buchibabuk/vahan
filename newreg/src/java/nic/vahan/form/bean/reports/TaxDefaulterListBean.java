/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.reports.TaxDefaulterListDobj;
import nic.vahan.form.dobj.reports.TaxDefaulterYearWiseSummaryDobj;
import nic.vahan.form.impl.TaxDefaulterListImpl;
import nic.vahan.form.impl.TaxDefaulterYearWiseSummaryImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author afzal
 */
@ManagedBean(name = "taxDefaulterBean")
@ViewScoped
public class TaxDefaulterListBean implements Serializable {

    FacesMessage message = null;
    private static final Logger LOGGER = Logger.getLogger(TaxDefaulterListBean.class);
    private TaxDefaulterListDobj download = new TaxDefaulterListDobj();
    private List<TaxDefaulterListDobj> downloadDetailsClassWise = new ArrayList<>();
    private List<TaxDefaulterListDobj> downloadDetailsCategoryWise = new ArrayList<>();
    private List<TaxDefaulterListDobj> oldRecords = new ArrayList<>();
    private boolean isExceedPage = false;
    private TaxDefaulterListDobj selectedVchClassDetail;
    private TaxDefaulterListDobj selectedVchCatgDetail;
    private String DwnltableHeader = "";
    private boolean isClassWise = false;
    private boolean isCatgWise = false;
    private List purCodeList = new ArrayList();
    private boolean isRenderPanel = false;
    private TaxDefaulterListDobj selectedOldTaxDefaulter;
    private List<TaxDefaulterYearWiseSummaryDobj> listtaxDefaulterYearWiseSummaryDobj = new ArrayList<TaxDefaulterYearWiseSummaryDobj>();
    private boolean taxdefaulteryearwisesummary = false;
    private List<TaxDefaulterListDobj> taxlist = new ArrayList<>();
    private boolean renderdemandnoticepanel = false;
    private List<TaxDefaulterListDobj> selectedPrintDobj = new ArrayList<TaxDefaulterListDobj>();
    private String regn_no;
    private String vahanMessages = null;
    private SessionVariables sessionVariables;
    private boolean recalculateTax = false;
    private boolean renderInsertRecordDialog = false;
    private int defaulterNoticeGracePeriod = 0;

    @PostConstruct
    public void init() {
        sessionVariables = new SessionVariables();
        if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
            vahanMessages = "Session time Out";
            return;
        }
        setTaxdefaulteryearwisesummary(false);
        getPurCodeList().clear();
        getPurCodeList().add(new SelectItem("58", "MV Tax"));
        getPurCodeList().add(new SelectItem("59", "Additional MV Tax"));
        try {
            TmConfigurationDobj tmConfDobj = Util.getTmConfiguration();
            if (tmConfDobj != null && tmConfDobj.getTmPrintConfgDobj() != null) {
                setDefaulterNoticeGracePeriod(tmConfDobj.getTmPrintConfgDobj().getDefaulterNoticeGracePeriod());
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

    public void getTaxDefaulterListYearwiseSummary() {
        getDownloadDetailsClassWise().clear();
        if (!getTaxlist().isEmpty()) {
            getTaxlist().clear();
        }
        getDownloadDetailsCategoryWise().clear();
        getListtaxDefaulterYearWiseSummaryDobj().clear();
        if (getDownload() != null) {
            if (getDownload().getListFileExport() != null) {
                getDownload().getListFileExport().clear();
            }
        }
        setRecalculateTax(false);
        this.regn_no = null;
        setRenderdemandnoticepanel(false);
        TaxDefaulterYearWiseSummaryImpl impl = null;
        try {
            impl = new TaxDefaulterYearWiseSummaryImpl();
            this.setListtaxDefaulterYearWiseSummaryDobj(impl.getTaxDefaulterYearWiseSummary(58));
            if (this.getListtaxDefaulterYearWiseSummaryDobj().size() > 0) {
                setTaxdefaulteryearwisesummary(true);
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

    public void PurCodeListener() {
        try {
            getListtaxDefaulterYearWiseSummaryDobj().clear();
            setRenderdemandnoticepanel(false);
            getDownloadDetailsClassWise().clear();
            getDownloadDetailsCategoryWise().clear();
            if (getDownload() != null) {
                if (getDownload().getListFileExport() != null) {
                    getDownload().getListFileExport().clear();
                }
            }
            setRecalculateTax(false);
            this.regn_no = null;
            setDownloadDetailsClassWise(TaxDefaulterListImpl.getTaxDefaulterClassWise(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), 58, getDefaulterNoticeGracePeriod()));
            setDownloadDetailsCategoryWise(TaxDefaulterListImpl.getTaxDefaulterCategoryWise(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), 58, getDefaulterNoticeGracePeriod()));
            setIsRenderPanel(true);
            PrimeFaces.current().ajax().update("panelTaxVehDtls_n");
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
    }

    public void vchClassWiseDataListener() {
        try {
            download = TaxDefaulterListImpl.getVchClassWiseDataListener(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), getSelectedVchClassDetail().getVch_class_cd(), getDefaulterNoticeGracePeriod());
            String taxType = "";
            if (download != null) {
                setIsClassWise(false);
                setIsCatgWise(true);
                setDwnltableHeader(taxType + " Defaulter Detail for " + getSelectedVchClassDetail().getVch_class_desc());
                if (download.getListFileExport().size() > 10) {
                    isExceedPage = true;

                } else {
                    isExceedPage = false;
                }
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

    public String vchClassWiseTDNotice() {
        try {
            download = TaxDefaulterListImpl.getVchClassWiseDataListener(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), getSelectedVchClassDetail().getVch_class_cd(), getDefaulterNoticeGracePeriod());
            TaxDefaulterListDobj dobj = download.getListFileExport().get(0);
            dobj.setDefaulterNoticeGracePeriod(defaulterNoticeGracePeriod);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("TDNDobj", dobj);
            return "taxDefaulterNotice";
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return "";
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return "";
        }
    }

    public void vchCatgWiseDataListener() {
        try {
            download = TaxDefaulterListImpl.getVchCatgWiseDataListener(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), getSelectedVchCatgDetail());
            String taxType = "";
            if (download != null) {
                setIsCatgWise(false);
                setIsClassWise(true);
                setDwnltableHeader(taxType + " Defaulter Detail for " + getSelectedVchCatgDetail().getVch_catg());
                if (download.getListFileExport().size() > 50) {
                    isExceedPage = true;

                } else {
                    isExceedPage = false;
                }
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

    public void updatetaxtable(TaxDefaulterYearWiseSummaryDobj dobj) {
        try {
            String yrfilter = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("one");
            int vh_class = dobj.getVchClasscode();
            setTaxlist(TaxDefaulterListImpl.getTaxDefaulterInformation(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), 58, yrfilter, vh_class));
            if (getTaxlist().isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "No Record Found"));
            } else {
                setRenderdemandnoticepanel(true);
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

    public void printConfirmTaxDefStatment() {
        if (selectedPrintDobj.size() < 1) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Please Select Atleast One Registration No For Printing"));
            return;
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("listPrintRc", selectedPrintDobj);
        //RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().executeScript("PF('printConfirmTax').show()");
    }

    public String printTaxDefCertificate() {
        return "printTaxDefaulterReport";
    }

    public void getGetails() {
        String regnno = null;
        getListtaxDefaulterYearWiseSummaryDobj().clear();
        getDownloadDetailsClassWise().clear();
        getDownloadDetailsCategoryWise().clear();
        if (getDownload() != null) {
            if (getDownload().getListFileExport() != null) {
                getDownload().getListFileExport().clear();
            }
        }
        getTaxlist().clear();
        setRenderdemandnoticepanel(false);
        setRecalculateTax(false);
        if (regn_no == null || regn_no.isEmpty()) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration No should not be Blank !!!"));
            return;
        }
        try {
            regnno = TaxDefaulterListImpl.isRegnExistForTaxDefaulter(regn_no.trim().toUpperCase(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
            if (regnno == null) {
                setRenderInsertRecordDialog(true);
                setRenderdemandnoticepanel(true);
                PrimeFaces.current().executeScript("PF('successDialog').show()");
                return;
            } else {
                setRenderInsertRecordDialog(false);
                setTaxlist(TaxDefaulterListImpl.getTaxDefaulterInformationRegnNoWise(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), 58, regn_no.trim().toUpperCase(), getDefaulterNoticeGracePeriod()));
                if (!getTaxlist().isEmpty()) {
                    setRenderdemandnoticepanel(true);
                    setRecalculateTax(true);
                } else {
                    throw new VahanException("Tax Defaulter details Doesn't exist for Registration No: " + regnno);
                }

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

    public void insertRecordforTaxDefaulter() {
        try {
            Map map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String regn_no = (String) map.get("regn_no");
            boolean isRecordInsertSuccessfully = TaxDefaulterListImpl.insertRecordforTaxDefaulter(regn_no.toUpperCase(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
            getTaxlist().clear();
            setRenderdemandnoticepanel(false);
            this.regn_no = null;
            setRecalculateTax(false);
            if (isRecordInsertSuccessfully) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "The request has been noted down to add registratio no " + regn_no.toUpperCase() + " as tax defaulter, please check after sometime !!!"));
                return;
            } else {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Error In adding registratio no " + regn_no.toUpperCase() + " as tax defaulter !!!"));
                return;
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

    public void reCalculateTax() {
        try {
            boolean isReCalculateSuccessfully = TaxDefaulterListImpl.updateTaxDefaulterInformationRegnNoWise(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), regn_no.trim().toUpperCase());
            getTaxlist().clear();
            this.regn_no = null;
            setRenderdemandnoticepanel(false);
            setRecalculateTax(false);
            if (isReCalculateSuccessfully) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "The request has been noted down to re-calculate, please check after sometime !!!"));
                return;
            } else {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Error In Re-Calculate the Tax Amount !!!"));
                return;
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

    /**
     * @return the download
     */
    public TaxDefaulterListDobj getDownload() {
        return download;
    }

    /**
     * @param download the download to set
     */
    public void setDownload(TaxDefaulterListDobj download) {
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
     * @return the selectedVchClassDetail
     */
    public TaxDefaulterListDobj getSelectedVchClassDetail() {
        return selectedVchClassDetail;
    }

    /**
     * @param selectedVchClassDetail the selectedVchClassDetail to set
     */
    public void setSelectedVchClassDetail(TaxDefaulterListDobj selectedVchClassDetail) {
        this.selectedVchClassDetail = selectedVchClassDetail;
    }

    /**
     * @return the downloadDetailsClassWise
     */
    public List<TaxDefaulterListDobj> getDownloadDetailsClassWise() {
        return downloadDetailsClassWise;
    }

    /**
     * @param downloadDetailsClassWise the downloadDetailsClassWise to set
     */
    public void setDownloadDetailsClassWise(List<TaxDefaulterListDobj> downloadDetailsClassWise) {
        this.downloadDetailsClassWise = downloadDetailsClassWise;
    }

    /**
     * @return the downloadDetailsCategoryWise
     */
    public List<TaxDefaulterListDobj> getDownloadDetailsCategoryWise() {
        return downloadDetailsCategoryWise;
    }

    /**
     * @param downloadDetailsCategoryWise the downloadDetailsCategoryWise to set
     */
    public void setDownloadDetailsCategoryWise(List<TaxDefaulterListDobj> downloadDetailsCategoryWise) {
        this.downloadDetailsCategoryWise = downloadDetailsCategoryWise;
    }

    /**
     * @return the selectedVchCatgDetail
     */
    public TaxDefaulterListDobj getSelectedVchCatgDetail() {
        return selectedVchCatgDetail;
    }

    /**
     * @param selectedVchCatgDetail the selectedVchCatgDetail to set
     */
    public void setSelectedVchCatgDetail(TaxDefaulterListDobj selectedVchCatgDetail) {
        this.selectedVchCatgDetail = selectedVchCatgDetail;
    }

//    /**
//     * @return the downloadCatgWise
//     */
//    public TaxDefaulterListDobj getDownloadCatgWise() {
//        return downloadCatgWise;
//    }
//
//    /**
//     * @param downloadCatgWise the downloadCatgWise to set
//     */
//    public void setDownloadCatgWise(TaxDefaulterListDobj downloadCatgWise) {
//        this.downloadCatgWise = downloadCatgWise;
//    }
    /**
     * @return the isClassWise
     */
    public boolean isIsClassWise() {
        return isClassWise;
    }

    /**
     * @param isClassWise the isClassWise to set
     */
    public void setIsClassWise(boolean isClassWise) {
        this.isClassWise = isClassWise;
    }

    /**
     * @return the isCatgWise
     */
    public boolean isIsCatgWise() {
        return isCatgWise;
    }

    /**
     * @param isCatgWise the isCatgWise to set
     */
    public void setIsCatgWise(boolean isCatgWise) {
        this.isCatgWise = isCatgWise;
    }

    /**
     * @return the DwnltableHeader
     */
    public String getDwnltableHeader() {
        return DwnltableHeader;
    }

    /**
     * @param DwnltableHeader the DwnltableHeader to set
     */
    public void setDwnltableHeader(String DwnltableHeader) {
        this.DwnltableHeader = DwnltableHeader;
    }

    /**
     * @return the purCodeList
     */
    public List getPurCodeList() {
        return purCodeList;
    }

    /**
     * @param purCodeList the purCodeList to set
     */
    public void setPurCodeList(List purCodeList) {
        this.purCodeList = purCodeList;
    }

    /**
     * @return the isRender
     */
    public boolean isIsRenderPanel() {
        return isRenderPanel;
    }

    /**
     * @param isRender the isRender to set
     */
    public void setIsRenderPanel(boolean isRender) {
        this.isRenderPanel = isRenderPanel;
    }

    /**
     * @return the oldRecords
     */
    public List<TaxDefaulterListDobj> getOldRecords() {
        return oldRecords;
    }

    /**
     * @param oldRecords the oldRecords to set
     */
    public void setOldRecords(List<TaxDefaulterListDobj> oldRecords) {
        this.oldRecords = oldRecords;
    }
//
//    /**
//     * @return the oldDate
//     */
//    public Date getOldDate() {
//        return oldDate;
//    }
//
//    /**
//     * @param oldDate the oldDate to set
//     */
//    public void setOldDate(Date oldDate) {
//        this.oldDate = oldDate;
//    }

    /**
     * @return the selectedOldTaxDefaulter
     */
    public TaxDefaulterListDobj getSelectedOldTaxDefaulter() {
        return selectedOldTaxDefaulter;
    }

    /**
     * @param selectedOldTaxDefaulter the selectedOldTaxDefaulter to set
     */
    public void setSelectedOldTaxDefaulter(TaxDefaulterListDobj selectedOldTaxDefaulter) {
        this.selectedOldTaxDefaulter = selectedOldTaxDefaulter;
    }

    /**
     * @return the listtaxDefaulterYearWiseSummaryDobj
     */
    public List<TaxDefaulterYearWiseSummaryDobj> getListtaxDefaulterYearWiseSummaryDobj() {
        return listtaxDefaulterYearWiseSummaryDobj;
    }

    /**
     * @param listtaxDefaulterYearWiseSummaryDobj the
     * listtaxDefaulterYearWiseSummaryDobj to set
     */
    public void setListtaxDefaulterYearWiseSummaryDobj(List<TaxDefaulterYearWiseSummaryDobj> listtaxDefaulterYearWiseSummaryDobj) {
        this.listtaxDefaulterYearWiseSummaryDobj = listtaxDefaulterYearWiseSummaryDobj;
    }

    /**
     * @return the taxdefaulteryearwisesummary
     */
    public boolean isTaxdefaulteryearwisesummary() {
        return taxdefaulteryearwisesummary;
    }

    /**
     * @param taxdefaulteryearwisesummary the taxdefaulteryearwisesummary to set
     */
    public void setTaxdefaulteryearwisesummary(boolean taxdefaulteryearwisesummary) {
        this.taxdefaulteryearwisesummary = taxdefaulteryearwisesummary;
    }

    /**
     * @return the taxlist
     */
    public List<TaxDefaulterListDobj> getTaxlist() {
        return taxlist;
    }

    /**
     * @param taxlist the taxlist to set
     */
    public void setTaxlist(List<TaxDefaulterListDobj> taxlist) {
        this.taxlist = taxlist;
    }

    /**
     * @return the renderdemandnoticepanel
     */
    public boolean isRenderdemandnoticepanel() {
        return renderdemandnoticepanel;
    }

    /**
     * @param renderdemandnoticepanel the renderdemandnoticepanel to set
     */
    public void setRenderdemandnoticepanel(boolean renderdemandnoticepanel) {
        this.renderdemandnoticepanel = renderdemandnoticepanel;
    }

    /**
     * @return the selectedPrintDobj
     */
    public List<TaxDefaulterListDobj> getSelectedPrintDobj() {
        return selectedPrintDobj;
    }

    /**
     * @param selectedPrintDobj the selectedPrintDobj to set
     */
    public void setSelectedPrintDobj(List<TaxDefaulterListDobj> selectedPrintDobj) {
        this.selectedPrintDobj = selectedPrintDobj;
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
     * @return the sessionVariables
     */
    public SessionVariables getSessionVariables() {
        return sessionVariables;
    }

    /**
     * @param sessionVariables the sessionVariables to set
     */
    public void setSessionVariables(SessionVariables sessionVariables) {
        this.sessionVariables = sessionVariables;
    }

    /**
     * @return the recalculateTax
     */
    public boolean isRecalculateTax() {
        return recalculateTax;
    }

    /**
     * @param recalculateTax the recalculateTax to set
     */
    public void setRecalculateTax(boolean recalculateTax) {
        this.recalculateTax = recalculateTax;
    }

    /**
     * @return the renderInsertRecordDialog
     */
    public boolean isRenderInsertRecordDialog() {
        return renderInsertRecordDialog;
    }

    /**
     * @param renderInsertRecordDialog the renderInsertRecordDialog to set
     */
    public void setRenderInsertRecordDialog(boolean renderInsertRecordDialog) {
        this.renderInsertRecordDialog = renderInsertRecordDialog;
    }

    /**
     * @return the defaulterNoticeGracePeriod
     */
    public int getDefaulterNoticeGracePeriod() {
        return defaulterNoticeGracePeriod;
    }

    /**
     * @param defaulterNoticeGracePeriod the defaulterNoticeGracePeriod to set
     */
    public void setDefaulterNoticeGracePeriod(int defaulterNoticeGracePeriod) {
        this.defaulterNoticeGracePeriod = defaulterNoticeGracePeriod;
    }
}
