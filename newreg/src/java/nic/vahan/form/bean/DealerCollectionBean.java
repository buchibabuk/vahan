/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.DealerCollectionDobj;
import nic.vahan.form.impl.DealerCollectionImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
//import org.primefaces.context.RequestContext;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "rtoWiseTtlCollectionBean")
@ViewScoped
public class DealerCollectionBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(DealerCollectionBean.class);
    private Date uptoDate;
    private Date startDate;
    private List<DealerCollectionDobj> rtoWiseCollectionDobj;
    private long grandTotalRegn;
    private long grandFees;
    private long grandTax;
    private long grandTotalAmount;
    private int year;
    private int month;
    private Map<String, Integer> monthList;
    private boolean formPanelVisibility = false;
    private boolean grandTotalVisibility = false;
    private boolean transactionWisePanelVisibility = false;
    private String searchByValue;
    private String tinNo;
    private String oldTinNo;
    private String dealerCd;
    private String errorMessage;
    private boolean tinNoPanelVisibility = false;
    private String printedFromDate;
    private String printedUptoDate;
    private String printedDate;
    private boolean dealerValueVisibilty = true;
    private String reportName;
    private String empCodeLoggedIn = null;
    private String userCatgForLoggedInUser = null;

    public DealerCollectionBean() {
        empCodeLoggedIn = Util.getEmpCode();
        userCatgForLoggedInUser = Util.getUserCategory();
        if (empCodeLoggedIn == null || userCatgForLoggedInUser == null) {
            return;
        }
        DealerCollectionImpl report_impl = new DealerCollectionImpl();
        Calendar currentCal = Calendar.getInstance();
        month = currentCal.get(Calendar.MONTH);
        year = currentCal.get(Calendar.YEAR);
        monthList = ServerUtil.monthList();
        searchByValue = "transactionWise";
        try {
            Long user_cd = Long.parseLong(empCodeLoggedIn);
            if (userCatgForLoggedInUser.equals(TableConstants.USER_CATG_DEALER)) {
                tinNoPanelVisibility = true;
                Map makerAndDealerDetail = OwnerImpl.getDealerDetail(user_cd);
                dealerCd = (String) makerAndDealerDetail.get("dealer_cd");
                oldTinNo = report_impl.getTinNoStatus(dealerCd);
                dealerValueVisibilty = false;
            }
        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vex.getMessage(), vex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void getDetails() {
        try {
            String monthName = "";
            DealerCollectionImpl report_impl = new DealerCollectionImpl();
            Calendar currentCal = Calendar.getInstance();
            int currentMonth = currentCal.get(Calendar.MONTH);
            int currentYear = currentCal.get(Calendar.YEAR);

            if (year > currentYear) {
                formPanelVisibility = false;
                transactionWisePanelVisibility = false;
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Year should be equal or less than Current Year", "Year should be equal or less than Current Year");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, 1, 0, 0);
            Date fromDate = calendar.getTime();

            if (month == currentMonth && year == currentYear) {
                startDate = fromDate;
                uptoDate = new Date();
            } else {
                startDate = fromDate;
                Calendar calEnd = report_impl.getCalendarForNow(startDate);
                calEnd.set(Calendar.DAY_OF_MONTH, calEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
                report_impl.setTimeToEndofDay(calEnd);
                uptoDate = calEnd.getTime();
            }
            if (searchByValue.equals("summaryWise")) {
                rtoWiseCollectionDobj = report_impl.getRtoWiseTotalCollection(startDate, uptoDate);
                formPanelVisibility = true;
                transactionWisePanelVisibility = false;
                if (userCatgForLoggedInUser != null && !userCatgForLoggedInUser.equalsIgnoreCase(TableConstants.USER_CATG_DEALER)) {
                    grandTotalVisibility = true;
                    if (rtoWiseCollectionDobj != null && !rtoWiseCollectionDobj.isEmpty() && rtoWiseCollectionDobj.size() > 0) {
                        for (int i = rtoWiseCollectionDobj.size(); i == rtoWiseCollectionDobj.size(); i--) {

                            DealerCollectionDobj rtoCollection = rtoWiseCollectionDobj.get(i - 1);
                            if (rtoCollection.getDealerName().equals("<-GRAND TOTAL->")) {
                                grandTotalRegn = rtoCollection.getGrandTotalRegn();
                                grandFees = rtoCollection.getGrandFees();
                                grandTax = rtoCollection.getGrandTax();
                                grandTotalAmount = rtoCollection.getGrandTotalAmount();
                                rtoWiseCollectionDobj.remove(i - 1);
                            }
                            if (rtoCollection.getDealerName().equals("<-SUB TOTAL->")) {
                                rtoWiseCollectionDobj.remove(i - 1);
                            }
                        }
                    }
                }
            } else if (searchByValue.equals("transactionWise")) {
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                DateFormat date = new SimpleDateFormat("dd-MM-yyyy");
                Date dateobj = new Date();
                printedDate = df.format(dateobj);
                printedFromDate = date.format(fromDate);
                printedUptoDate = date.format(uptoDate);
                rtoWiseCollectionDobj = report_impl.getTransactionWiseData(startDate, uptoDate);
                transactionWisePanelVisibility = true;
                formPanelVisibility = false;
                for (Entry<String, Integer> entry : monthList.entrySet()) {
                    if (entry.getValue().equals(Integer.valueOf(month))) {
                        monthName = entry.getKey();
                        break;
                    }
                }
                reportName = monthName.substring(0, 3) + "-" + Integer.valueOf(year);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public String print() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("startDate", startDate);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("uptoDate", uptoDate);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return "/ui/reports/formDealerTtlCollectionReport.xhtml?faces-redirect=true";
    }

    public void updateTinNo() {
        DealerCollectionImpl report_impl = new DealerCollectionImpl();
        //RequestContext rc = RequestContext.getCurrentInstance();

        try {
            if (getTinNo() != null && !getTinNo().equals("")) {
                oldTinNo = report_impl.updateTinNo(dealerCd, getTinNo());
                tinNo = "";
                PrimeFaces.current().executeScript("PF('tinNoDialog_dlg1').hide()");
            }
        } catch (VahanException vme) {
            PrimeFaces.current().executeScript("PF('tinNoDialog_dlg1').hide()");
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, vme.getMessage(), vme.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

//    public void postProcessXLS(Object document) {
//        HSSFWorkbook wb = (HSSFWorkbook) document;
//        HSSFFont font = wb.createFont();
//        HSSFCellStyle style = wb.createCellStyle();
//        HSSFSheet sheet = wb.getSheetAt(0);
//        sheet.shiftRows(0, sheet.getLastRowNum(), 1);
//
//        HSSFRow row = sheet.createRow(0);
//        HSSFCell cell = row.createCell(0);
//        cell.setCellValue("From Date");
//        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
//        style.setFont(font);
//        cell.setCellStyle(style);
//        sheet.addMergedRegion(CellRangeAddress.valueOf("A1:B1"));
//
//
//        cell = row.createCell(2);
//        cell.setCellValue(printedFromDate);
//        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
//        style.setFont(font);
//        cell.setCellStyle(style);
//        sheet.addMergedRegion(CellRangeAddress.valueOf("C1:D1"));
//
//        cell = row.createCell(4);
//        cell.setCellValue("Upto Date");
//        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
//        style.setFont(font);
//        cell.setCellStyle(style);
//        sheet.addMergedRegion(CellRangeAddress.valueOf("E1:F1"));
//
//        cell = row.createCell(6);
//        cell.setCellValue(printedUptoDate);
//        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
//        style.setFont(font);
//        cell.setCellStyle(style);
//        sheet.addMergedRegion(CellRangeAddress.valueOf("G1:H1"));
//
//
//        cell = row.createCell(8);
//        cell.setCellValue("Printed Date");
//        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
//        style.setFont(font);
//        cell.setCellStyle(style);
//        sheet.addMergedRegion(CellRangeAddress.valueOf("I1:J1"));
//
//        cell = row.createCell(10);
//        cell.setCellValue(printedDate);
//        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
//        style.setFont(font);
//        cell.setCellStyle(style);
//        sheet.addMergedRegion(CellRangeAddress.valueOf("K1:L1"));
//
//    }

    /**
     * @return the LOGGER
     */
    public static Logger getLOGGER() {
        return LOGGER;
    }

    /**
     * @param aLOGGER the LOGGER to set
     */
    public static void setLOGGER(Logger aLOGGER) {
        LOGGER = aLOGGER;
    }

    /**
     * @return the uptoDate
     */
    public Date getUptoDate() {
        return uptoDate;
    }

    /**
     * @param uptoDate the uptoDate to set
     */
    public void setUptoDate(Date uptoDate) {
        this.uptoDate = uptoDate;
    }

    /**
     * @return the rtoWiseCollectionDobj
     */
    public List<DealerCollectionDobj> getRtoWiseCollectionDobj() {
        return rtoWiseCollectionDobj;
    }

    /**
     * @param rtoWiseCollectionDobj the rtoWiseCollectionDobj to set
     */
    public void setRtoWiseCollectionDobj(List<DealerCollectionDobj> rtoWiseCollectionDobj) {
        this.rtoWiseCollectionDobj = rtoWiseCollectionDobj;
    }

    /**
     * @return the grandTotalRegn
     */
    public long getGrandTotalRegn() {
        return grandTotalRegn;
    }

    /**
     * @param grandTotalRegn the grandTotalRegn to set
     */
    public void setGrandTotalRegn(long grandTotalRegn) {
        this.grandTotalRegn = grandTotalRegn;
    }

    /**
     * @return the grandFees
     */
    public long getGrandFees() {
        return grandFees;
    }

    /**
     * @param grandFees the grandFees to set
     */
    public void setGrandFees(long grandFees) {
        this.grandFees = grandFees;
    }

    /**
     * @return the grandTax
     */
    public long getGrandTax() {
        return grandTax;
    }

    /**
     * @param grandTax the grandTax to set
     */
    public void setGrandTax(long grandTax) {
        this.grandTax = grandTax;
    }

    /**
     * @return the grandTotalAmount
     */
    public long getGrandTotalAmount() {
        return grandTotalAmount;
    }

    /**
     * @param grandTotalAmount the grandTotalAmount to set
     */
    public void setGrandTotalAmount(long grandTotalAmount) {
        this.grandTotalAmount = grandTotalAmount;
    }

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * @return the month
     */
    public int getMonth() {
        return month;
    }

    /**
     * @param month the month to set
     */
    public void setMonth(int month) {
        this.month = month;
    }

    /**
     * @return the monthList
     */
    public Map<String, Integer> getMonthList() {
        return monthList;
    }

    /**
     * @param monthList the monthList to set
     */
    public void setMonthList(Map<String, Integer> monthList) {
        this.monthList = monthList;
    }

    /**
     * @return the formPanelVisibility
     */
    public boolean isFormPanelVisibility() {
        return formPanelVisibility;
    }

    /**
     * @param formPanelVisibility the formPanelVisibility to set
     */
    public void setFormPanelVisibility(boolean formPanelVisibility) {
        this.formPanelVisibility = formPanelVisibility;
    }

    /**
     * @return the grandTotalVisibility
     */
    public boolean isGrandTotalVisibility() {
        return grandTotalVisibility;
    }

    /**
     * @param grandTotalVisibility the grandTotalVisibility to set
     */
    public void setGrandTotalVisibility(boolean grandTotalVisibility) {
        this.grandTotalVisibility = grandTotalVisibility;
    }

    /**
     * @return the searchByValue
     */
    public String getSearchByValue() {
        return searchByValue;
    }

    /**
     * @param searchByValue the searchByValue to set
     */
    public void setSearchByValue(String searchByValue) {
        this.searchByValue = searchByValue;
    }

    /**
     * @return the transactionWisePanelVisibility
     */
    public boolean isTransactionWisePanelVisibility() {
        return transactionWisePanelVisibility;
    }

    /**
     * @param transactionWisePanelVisibility the transactionWisePanelVisibility
     * to set
     */
    public void setTransactionWisePanelVisibility(boolean transactionWisePanelVisibility) {
        this.transactionWisePanelVisibility = transactionWisePanelVisibility;
    }

    /**
     * @return the tinNo
     */
    public String getTinNo() {
        return tinNo;
    }

    /**
     * @param tinNo the tinNo to set
     */
    public void setTinNo(String tinNo) {
        this.tinNo = tinNo;
    }

    /**
     * @return the dealerCd
     */
    public String getDealerCd() {
        return dealerCd;
    }

    /**
     * @param dealerCd the dealerCd to set
     */
    public void setDealerCd(String dealerCd) {
        this.dealerCd = dealerCd;
    }

    /**
     * @return the oldTinNo
     */
    public String getOldTinNo() {
        return oldTinNo;
    }

    /**
     * @param oldTinNo the oldTinNo to set
     */
    public void setOldTinNo(String oldTinNo) {
        this.oldTinNo = oldTinNo;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * @return the tinNoPanelVisibility
     */
    public boolean isTinNoPanelVisibility() {
        return tinNoPanelVisibility;
    }

    /**
     * @param tinNoPanelVisibility the tinNoPanelVisibility to set
     */
    public void setTinNoPanelVisibility(boolean tinNoPanelVisibility) {
        this.tinNoPanelVisibility = tinNoPanelVisibility;
    }

    /**
     * @return the printedFromDate
     */
    public String getPrintedFromDate() {
        return printedFromDate;
    }

    /**
     * @param printedFromDate the printedFromDate to set
     */
    public void setPrintedFromDate(String printedFromDate) {
        this.printedFromDate = printedFromDate;
    }

    /**
     * @return the printedUptoDate
     */
    public String getPrintedUptoDate() {
        return printedUptoDate;
    }

    /**
     * @param printedUptoDate the printedUptoDate to set
     */
    public void setPrintedUptoDate(String printedUptoDate) {
        this.printedUptoDate = printedUptoDate;
    }

    /**
     * @return the printedDate
     */
    public String getPrintedDate() {
        return printedDate;
    }

    /**
     * @param printedDate the printedDate to set
     */
    public void setPrintedDate(String printedDate) {
        this.printedDate = printedDate;
    }

    /**
     * @return the dealerValueVisibilty
     */
    public boolean isDealerValueVisibilty() {
        return dealerValueVisibilty;
    }

    /**
     * @param dealerValueVisibilty the dealerValueVisibilty to set
     */
    public void setDealerValueVisibilty(boolean dealerValueVisibilty) {
        this.dealerValueVisibilty = dealerValueVisibilty;
    }

    /**
     * @return the reportName
     */
    public String getReportName() {
        return reportName;
    }

    /**
     * @param reportName the reportName to set
     */
    public void setReportName(String reportName) {
        this.reportName = reportName;
    }
}
