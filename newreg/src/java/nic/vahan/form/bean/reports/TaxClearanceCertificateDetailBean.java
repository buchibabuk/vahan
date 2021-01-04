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
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.impl.SeatAllotedDetails;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import nic.vahan.form.dobj.TaxClearanceCertificateDetailDobj;
import nic.vahan.form.impl.TaxClearanceCertificatePrintImpl;

/**
 *
 * @author Ankur
 */
@ManagedBean(name = "taxClearCertificateBean")
@ViewScoped
public class TaxClearanceCertificateDetailBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(TaxClearanceCertificateDetailBean.class);
    private String appl_no;
    private String applno;
    private String regno;
    private List<TaxClearanceCertificateDetailBean> filteredSeat = null;
    private int action_cd;
    private String indexValue = "";
    private String btn_print_label = "";
    private String main_header_label = "";
    private boolean isrcprint = false;
    private String paper_rc;
    private List<TaxClearanceCertificateDetailDobj> printCertDobj = new ArrayList<TaxClearanceCertificateDetailDobj>();
    private List<TaxClearanceCertificateDetailDobj> selectedCertDobj = new ArrayList<TaxClearanceCertificateDetailDobj>();
    private List<TaxClearanceCertificateDetailDobj> histprintCertDobj = new ArrayList();
    private boolean tax_clr_amt_rendred = false;
    private String regn_No;
    private boolean render_TCC_No;

    public void setListBeans(List<TaxClearanceCertificateDetailDobj> listDobjs) {
        setPrintCertDobj(listDobjs);
    }
//    public void sethistListBeans(List<TaxClearanceCertificateDetailDobj> HistlistDobjs) {
//        setHistprintCertDobj(HistlistDobjs);
//    }

    @PostConstruct
    public void init() {
        regn_No = "";
        try {
            SeatAllotedDetails seatDtl = (SeatAllotedDetails) Util.getSession().getAttribute("SelectedSeat");
            if (Util.getUserStateCode() == null) {
                throw new VahanException("Error in Getting Tax Clearence details,Please try again ");
            } else if (Util.getUserStateCode() != null && Util.getUserStateCode().equals("ML")) {
                tax_clr_amt_rendred = true;
                if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_TAXCC) {
                    setMain_header_label("Tax Clearance Report");
                    setBtn_print_label("Print TCC");
                    setIsrcprint(true);
                }
            } else {
                tax_clr_amt_rendred = false;
                if (Util.getUserStateCode() != null && Util.getUserStateCode().equals("RJ")) {
                    render_TCC_No = true;
                } else {
                    render_TCC_No = false;
                }

                if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_TAXCC) {
                    setMain_header_label("Tax Clearance Report");
                    setBtn_print_label("Print TCC");
                    setIsrcprint(true);
                    this.setListBeans(TaxClearanceCertificatePrintImpl.getPurCdPrintTCCDetails());
                    histprintCertDobj = TaxClearanceCertificatePrintImpl.getPurCdPrintHistTCCDetails();
                }
            }

        } catch (VahanException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

    }

    public String printTCC_Report_Regn() {

        if (regn_No.trim().equalsIgnoreCase("")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Registration No should not be Blank!"));
            return "";
        }
        try {
            TaxClearanceCertificateDetailDobj dobj = TaxClearanceCertificatePrintImpl.isRegnExistForTCC(regn_No.trim().toUpperCase(), Util.getUserStateCode(), Util.getUserSeatOffCode());
            if (dobj == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Registration No does not Exit!"));
                return "";
            } else {
                dobj.setAppl_no("");
                dobj.setRegno(regn_No.toUpperCase());
                selectedCertDobj.add(dobj);
                SeatAllotedDetails seatDtl = (SeatAllotedDetails) Util.getSession().getAttribute("SelectedSeat");
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("listPrintTCC", selectedCertDobj);
                return "TCCReport";
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return "";
    }

    public String confirmPrintTCC() {

        if (applno.trim().equalsIgnoreCase("")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Application No should not be Blank!"));
            return "";
        }

        try {
            TaxClearanceCertificateDetailDobj dobj = TaxClearanceCertificatePrintImpl.isApplExistForTCC(applno.trim().toUpperCase());
            if (dobj == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Wrong Application No, Can Print Only Todays Already printed Tax Clearance Certificates!"));
                return "";
            } else {
                selectedCertDobj.add(dobj);
                SeatAllotedDetails seatDtl = (SeatAllotedDetails) Util.getSession().getAttribute("SelectedSeat");
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("listPrintTCC", selectedCertDobj);
                return "TCCReport";

            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return "";
    }

    public String printTCC_Report_before_SevenDays() {

        if (regn_No.trim().equalsIgnoreCase("")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Registration No should not be Blank!"));
            return "";
        }
        try {
            TaxClearanceCertificateDetailDobj dobj = TaxClearanceCertificatePrintImpl.isRegnExistFor_TCC_before_SevenDays(regn_No.trim().toUpperCase(), Util.getUserStateCode(), Util.getUserSeatOffCode());
            if (dobj == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Either fee is not taken or already printed !"));
                return "";
            } else {

                dobj.setRegno(regn_No.toUpperCase());
                selectedCertDobj.add(dobj);
                SeatAllotedDetails seatDtl = (SeatAllotedDetails) Util.getSession().getAttribute("SelectedSeat");
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("listPrintTCC", selectedCertDobj);
                return "TCCReport";
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return "";
    }

    public void confirmprintCertificate() {
        Map map;
        map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        regno = (String) map.get("regnno");
        appl_no = (String) map.get("applno");
        //RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().executeScript("PF('printCertificate').show()");
    }

    public String printCertificate() {
        if (selectedCertDobj.size() < 1) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Please Select Atleast One Registration No For Printing"));
            return "";
        }

        if (selectedCertDobj.size() > 8) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Increase Bulk Printing Limit, Maximum 8 RC Allowed"));
            return "";
        }


        SeatAllotedDetails seatDtl = (SeatAllotedDetails) Util.getSession().getAttribute("SelectedSeat");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("listPrintTCC", selectedCertDobj);
        return "TCCReport";

    }

    public void confirmIsPrintCertificate() {
        Map map;
        map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        // RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().executeScript("PF('isPrintCertificate').show()");
    }

    public void deleteAndSaveHistory() {

        if (selectedCertDobj.size() > 0) {
            String result = TaxClearanceCertificatePrintImpl.deleteAndSaveHistoryTCC(selectedCertDobj);

            if (result.equalsIgnoreCase("success")) {
                List<TaxClearanceCertificateDetailDobj> tempList = new ArrayList<>();
                for (int i = 0; i < selectedCertDobj.size(); i++) {
                    for (int j = 0; j < printCertDobj.size(); j++) {
                        if (selectedCertDobj.get(i).getAppl_no().equalsIgnoreCase(printCertDobj.get(j).getAppl_no())) {
                            tempList.add(printCertDobj.get(j));
                        }
                    }
                    printCertDobj.removeAll(tempList);

                }

                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Successfully moved into history table"));
            } else {

                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert!", "Error in Moving Application no. " + result));
            }

        } else {

            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Please Select Atleast One Registration No. "));
        }
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public int getAction_cd() {
        return action_cd;
    }

    public void setAction_cd(int action_cd) {
        this.action_cd = action_cd;
    }

    /**
     * @return the indexValue
     */
    public String getIndexValue() {
        return indexValue;
    }

    /**
     * @param indexValue the indexValue to set
     */
    public void setIndexValue(String indexValue) {
        this.indexValue = indexValue;
    }

    /**
     * @return the printCertDobj
     */
    public List<TaxClearanceCertificateDetailDobj> getPrintCertDobj() {
        return printCertDobj;
    }

    /**
     * @param printCertDobj the printCertDobj to set
     */
    public void setPrintCertDobj(List<TaxClearanceCertificateDetailDobj> printCertDobj) {
        this.printCertDobj = printCertDobj;
    }

    /**
     * @return the filteredSeat
     */
    public List<TaxClearanceCertificateDetailBean> getFilteredSeat() {
        return filteredSeat;
    }

    /**
     * @param filteredSeat the filteredSeat to set
     */
    public void setFilteredSeat(List<TaxClearanceCertificateDetailBean> filteredSeat) {
        this.filteredSeat = filteredSeat;
    }

    /**
     * @return the btn_print_label
     */
    public String getBtn_print_label() {
        return btn_print_label;
    }

    /**
     * @param btn_print_label the btn_print_label to set
     */
    public void setBtn_print_label(String btn_print_label) {
        this.btn_print_label = btn_print_label;
    }

    /**
     * @return the main_header_label
     */
    public String getMain_header_label() {
        return main_header_label;
    }

    /**
     * @param main_header_label the main_header_label to set
     */
    public void setMain_header_label(String main_header_label) {
        this.main_header_label = main_header_label;
    }

    /**
     * @return the isrcprint
     */
    public boolean isIsrcprint() {
        return isrcprint;
    }

    /**
     * @param isrcprint the isrcprint to set
     */
    public void setIsrcprint(boolean isrcprint) {
        this.isrcprint = isrcprint;
    }

    /**
     * @return the paper_rc
     */
    public String getPaper_rc() {
        return paper_rc;
    }

    /**
     * @param paper_rc the paper_rc to set
     */
    public void setPaper_rc(String paper_rc) {
        this.paper_rc = paper_rc;
    }

    /**
     * @return the applno
     */
    public String getApplno() {
        return applno;
    }

    /**
     * @param applno the applno to set
     */
    public void setApplno(String applno) {
        this.applno = applno;
    }

    public List<TaxClearanceCertificateDetailDobj> getSelectedCertDobj() {
        return selectedCertDobj;
    }

    public void setSelectedCertDobj(List<TaxClearanceCertificateDetailDobj> selectedCertDobj) {
        this.selectedCertDobj = selectedCertDobj;
    }

    /**
     * @return the histprintCertDobj
     */
    public List<TaxClearanceCertificateDetailDobj> getHistprintCertDobj() {
        return histprintCertDobj;
    }

    /**
     * @param histprintCertDobj the histprintCertDobj to set
     */
    public void setHistprintCertDobj(List<TaxClearanceCertificateDetailDobj> histprintCertDobj) {
        this.histprintCertDobj = histprintCertDobj;
    }

    public boolean isTax_clr_amt_rendred() {
        return tax_clr_amt_rendred;
    }

    public void setTax_clr_amt_rendred(boolean tax_clr_amt_rendred) {
        this.tax_clr_amt_rendred = tax_clr_amt_rendred;
    }

    public String getRegn_No() {
        return regn_No;
    }

    public void setRegn_No(String regn_No) {
        this.regn_No = regn_No;
    }

    public boolean isRender_TCC_No() {
        return render_TCC_No;
    }

    public void setRender_TCC_No(boolean render_TCC_No) {
        this.render_TCC_No = render_TCC_No;
    }
}
