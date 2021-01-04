/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.reports;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author nic
 */
public class CashReceiptReportDobj implements Serializable {

    private String stateName;
    private String offname;
    private String receiptNo;
    private String ownerName;
    private String regnNo;
    private String vhClass;
    private String receiptDate;
    private String chasino;
    private String vhCatg;
    private String srNo;
    private String rcptNo;
    private int grandTotal;
    private String applNo;
    private List<CashRecieptSubListDobj> prntRecieptSubList;
    private String header;
    private String empName;
    private String grandTotalInWords;
    private String transactionId;
    private String bankRefNo;
    private Long saleAmt;
    private String financerName;
    private String dealerName;
    private List<CashReceiptReportDobj> prntInstList;
    private String instrumentDtls;
    private long instrumentcd;
    private String instrumentAmt;
    private String excessAmt;
    private String cashAmt;
    private boolean isinstrumentAmt;
    private boolean iscashAmt;
    private boolean isecsessAmt;
    private String subHeader;
    private String balFeeRemarks;
    private boolean isbalFeeRemarks;
    private boolean isfancyno;
    private String regnDate;
    private boolean isregnDate;
    private String printedDate;
    private boolean ishpState;
    private String engno;
    private boolean isserviceType;
    private boolean ispmtType;
    private String serviceType;
    private String pmtType;
    private boolean DNStatelOGO;
    private boolean isRegn_no;
    private boolean isChasi_no;
    private boolean isVhClass;
    private String image_background;
    private String image_logo;
    private boolean show_image_background;
    private boolean show_image_logo;
    private boolean print_tax_token;
    private String c_full_add;
    private boolean showRoadSafetySlogan;
    private VmRoadSafetySloganPrintDobj roadSafetySloganDobj = null;
    private boolean showCashRcptNote;
    private String cashRcptNote;
    private String exemAmount;
    private int advance_receipt_validity_days;

    /**
     * @return the stateName
     */
    public String getStateName() {
        return stateName;
    }

    /**
     * @param stateName the stateName to set
     */
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    /**
     * @return the receiptNo
     */
    public String getReceiptNo() {
        return receiptNo;
    }

    /**
     * @param receiptNo the receiptNo to set
     */
    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    /**
     * @return the ownerName
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * @param ownerName the ownerName to set
     */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
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
     * @return the vhClass
     */
    public String getVhClass() {
        return vhClass;
    }

    /**
     * @param vhClass the vhClass to set
     */
    public void setVhClass(String vhClass) {
        this.vhClass = vhClass;
    }

    /**
     * @return the receiptDate
     */
    public String getReceiptDate() {
        return receiptDate;
    }

    /**
     * @param receiptDate the receiptDate to set
     */
    public void setReceiptDate(String receiptDate) {
        this.receiptDate = receiptDate;
    }

    /**
     * @return the chasino
     */
    public String getChasino() {
        return chasino;
    }

    /**
     * @param chasino the chasino to set
     */
    public void setChasino(String chasino) {
        this.chasino = chasino;
    }

    /**
     * @return the vhCatg
     */
    public String getVhCatg() {
        return vhCatg;
    }

    /**
     * @param vhCatg the vhCatg to set
     */
    public void setVhCatg(String vhCatg) {
        this.vhCatg = vhCatg;
    }

    /**
     * @return the srNo
     */
    public String getSrNo() {
        return srNo;
    }

    /**
     * @param srNo the srNo to set
     */
    public void setSrNo(String srNo) {
        this.srNo = srNo;
    }

    /**
     * @return the rcptNo
     */
    public String getRcptNo() {
        return rcptNo;
    }

    /**
     * @param rcptNo the rcptNo to set
     */
    public void setRcptNo(String rcptNo) {
        this.rcptNo = rcptNo;
    }

    /**
     * @return the grandTotal
     */
    public int getGrandTotal() {
        return grandTotal;
    }

    /**
     * @param grandTotal the grandTotal to set
     */
    public void setGrandTotal(int grandTotal) {
        this.grandTotal = grandTotal;
    }

    /**
     * @return the applNo
     */
    public String getApplNo() {
        return applNo;
    }

    /**
     * @param applNo the applNo to set
     */
    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * @return the prntRecieptSubList
     */
    public List<CashRecieptSubListDobj> getPrntRecieptSubList() {
        return prntRecieptSubList;
    }

    /**
     * @param prntRecieptSubList the prntRecieptSubList to set
     */
    public void setPrntRecieptSubList(List<CashRecieptSubListDobj> prntRecieptSubList) {
        this.prntRecieptSubList = prntRecieptSubList;
    }

    /**
     * @return the offname
     */
    public String getOffname() {
        return offname;
    }

    /**
     * @param offname the offname to set
     */
    public void setOffname(String offname) {
        this.offname = offname;
    }

    /**
     * @return the grandTotalInWords
     */
    public String getGrandTotalInWords() {
        return grandTotalInWords;
    }

    /**
     * @param grandTotalInWords the grandTotalInWords to set
     */
    public void setGrandTotalInWords(String grandTotalInWords) {
        this.grandTotalInWords = grandTotalInWords;
    }

    /**
     * @return the empName
     */
    public String getEmpName() {
        return empName;
    }

    /**
     * @param empName the empName to set
     */
    public void setEmpName(String empName) {
        this.empName = empName;
    }

    /**
     * @return the transactionId
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * @param transactionId the transactionId to set
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * @return the bankRefNo
     */
    public String getBankRefNo() {
        return bankRefNo;
    }

    /**
     * @param bankRefNo the bankRefNo to set
     */
    public void setBankRefNo(String bankRefNo) {
        this.bankRefNo = bankRefNo;
    }

    /**
     * @return the saleAmt
     */
    public Long getSaleAmt() {
        return saleAmt;
    }

    /**
     * @param saleAmt the saleAmt to set
     */
    public void setSaleAmt(Long saleAmt) {
        this.saleAmt = saleAmt;
    }

    /**
     * @return the financerName
     */
    public String getFinancerName() {
        return financerName;
    }

    /**
     * @param financerName the financerName to set
     */
    public void setFinancerName(String financerName) {
        this.financerName = financerName;
    }

    /**
     * @return the dealerName
     */
    public String getDealerName() {
        return dealerName;
    }

    /**
     * @param dealerName the dealerName to set
     */
    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    /**
     * @return the instrumentAmt
     */
    public String getInstrumentAmt() {
        return instrumentAmt;
    }

    /**
     * @param instrumentAmt the instrumentAmt to set
     */
    public void setInstrumentAmt(String instrumentAmt) {
        this.instrumentAmt = instrumentAmt;
    }

    /**
     * @return the excessAmt
     */
    public String getExcessAmt() {
        return excessAmt;
    }

    /**
     * @param excessAmt the excessAmt to set
     */
    public void setExcessAmt(String excessAmt) {
        this.excessAmt = excessAmt;
    }

    /**
     * @return the cashAmt
     */
    public String getCashAmt() {
        return cashAmt;
    }

    /**
     * @param cashAmt the cashAmt to set
     */
    public void setCashAmt(String cashAmt) {
        this.cashAmt = cashAmt;
    }

    /**
     * @return the isinstrumentAmt
     */
    public boolean isIsinstrumentAmt() {
        return isinstrumentAmt;
    }

    /**
     * @param isinstrumentAmt the isinstrumentAmt to set
     */
    public void setIsinstrumentAmt(boolean isinstrumentAmt) {
        this.isinstrumentAmt = isinstrumentAmt;
    }

    /**
     * @return the iscashAmt
     */
    public boolean isIscashAmt() {
        return iscashAmt;
    }

    /**
     * @param iscashAmt the iscashAmt to set
     */
    public void setIscashAmt(boolean iscashAmt) {
        this.iscashAmt = iscashAmt;
    }

    /**
     * @return the isecsessAmt
     */
    public boolean isIsecsessAmt() {
        return isecsessAmt;
    }

    /**
     * @param isecsessAmt the isecsessAmt to set
     */
    public void setIsecsessAmt(boolean isecsessAmt) {
        this.isecsessAmt = isecsessAmt;
    }

    /**
     * @return the prntInstList
     */
    public List<CashReceiptReportDobj> getPrntInstList() {
        return prntInstList;
    }

    /**
     * @param prntInstList the prntInstList to set
     */
    public void setPrntInstList(List<CashReceiptReportDobj> prntInstList) {
        this.prntInstList = prntInstList;
    }

    /**
     * @return the instrumentDtls
     */
    public String getInstrumentDtls() {
        return instrumentDtls;
    }

    /**
     * @param instrumentDtls the instrumentDtls to set
     */
    public void setInstrumentDtls(String instrumentDtls) {
        this.instrumentDtls = instrumentDtls;
    }

    /**
     * @return the instrumentcd
     */
    public long getInstrumentcd() {
        return instrumentcd;
    }

    /**
     * @param instrumentcd the instrumentcd to set
     */
    public void setInstrumentcd(long instrumentcd) {
        this.instrumentcd = instrumentcd;
    }

    /**
     * @return the subHeader
     */
    public String getSubHeader() {
        return subHeader;
    }

    /**
     * @param subHeader the subHeader to set
     */
    public void setSubHeader(String subHeader) {
        this.subHeader = subHeader;
    }

    /**
     * @return the balFeeRemarks
     */
    public String getBalFeeRemarks() {
        return balFeeRemarks;
    }

    /**
     * @param balFeeRemarks the balFeeRemarks to set
     */
    public void setBalFeeRemarks(String balFeeRemarks) {
        this.balFeeRemarks = balFeeRemarks;
    }

    /**
     * @return the isbalFeeRemarks
     */
    public boolean isIsbalFeeRemarks() {
        return isbalFeeRemarks;
    }

    /**
     * @param isbalFeeRemarks the isbalFeeRemarks to set
     */
    public void setIsbalFeeRemarks(boolean isbalFeeRemarks) {
        this.isbalFeeRemarks = isbalFeeRemarks;
    }

    /**
     * @return the isfancyno
     */
    public boolean isIsfancyno() {
        return isfancyno;
    }

    /**
     * @param isfancyno the isfancyno to set
     */
    public void setIsfancyno(boolean isfancyno) {
        this.isfancyno = isfancyno;
    }

    /**
     * @return the regnDate
     */
    public String getRegnDate() {
        return regnDate;
    }

    /**
     * @param regnDate the regnDate to set
     */
    public void setRegnDate(String regnDate) {
        this.regnDate = regnDate;
    }

    /**
     * @return the isregnDate
     */
    public boolean isIsregnDate() {
        return isregnDate;
    }

    /**
     * @param isregnDate the isregnDate to set
     */
    public void setIsregnDate(boolean isregnDate) {
        this.isregnDate = isregnDate;
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
     * @return the ishpState
     */
    public boolean isIshpState() {
        return ishpState;
    }

    /**
     * @param ishpState the ishpState to set
     */
    public void setIshpState(boolean ishpState) {
        this.ishpState = ishpState;
    }

    /**
     * @return the engno
     */
    public String getEngno() {
        return engno;
    }

    /**
     * @param engno the engno to set
     */
    public void setEngno(String engno) {
        this.engno = engno;
    }

    /**
     * @return the isserviceType
     */
    public boolean isIsserviceType() {
        return isserviceType;
    }

    /**
     * @param isserviceType the isserviceType to set
     */
    public void setIsserviceType(boolean isserviceType) {
        this.isserviceType = isserviceType;
    }

    /**
     * @return the ispmtType
     */
    public boolean isIspmtType() {
        return ispmtType;
    }

    /**
     * @param ispmtType the ispmtType to set
     */
    public void setIspmtType(boolean ispmtType) {
        this.ispmtType = ispmtType;
    }

    /**
     * @return the serviceType
     */
    public String getServiceType() {
        return serviceType;
    }

    /**
     * @param serviceType the serviceType to set
     */
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * @return the pmtType
     */
    public String getPmtType() {
        return pmtType;
    }

    /**
     * @param pmtType the pmtType to set
     */
    public void setPmtType(String pmtType) {
        this.pmtType = pmtType;
    }

    /**
     * @return the DNStatelOGO
     */
    public boolean isDNStatelOGO() {
        return DNStatelOGO;
    }

    /**
     * @param DNStatelOGO the DNStatelOGO to set
     */
    public void setDNStatelOGO(boolean DNStatelOGO) {
        this.DNStatelOGO = DNStatelOGO;
    }

    /**
     * @return the isRegn_no
     */
    public boolean isIsRegn_no() {
        return isRegn_no;
    }

    /**
     * @param isRegn_no the isRegn_no to set
     */
    public void setIsRegn_no(boolean isRegn_no) {
        this.isRegn_no = isRegn_no;
    }

    /**
     * @return the isChasi_n0
     */
    public boolean isIsChasi_no() {
        return isChasi_no;
    }

    /**
     * @param isChasi_n0 the isChasi_n0 to set
     */
    public void setIsChasi_no(boolean isChasi_no) {
        this.isChasi_no = isChasi_no;
    }

    /**
     * @return the isVhClass
     */
    public boolean isIsVhClass() {
        return isVhClass;
    }

    /**
     * @param isVhClass the isVhClass to set
     */
    public void setIsVhClass(boolean isVhClass) {
        this.isVhClass = isVhClass;
    }

    /**
     * @return the image_background
     */
    public String getImage_background() {
        return image_background;
    }

    /**
     * @param image_background the image_background to set
     */
    public void setImage_background(String image_background) {
        this.image_background = image_background;
    }

    /**
     * @return the image_logo
     */
    public String getImage_logo() {
        return image_logo;
    }

    /**
     * @param image_logo the image_logo to set
     */
    public void setImage_logo(String image_logo) {
        this.image_logo = image_logo;
    }

    /**
     * @return the show_image_background
     */
    public boolean isShow_image_background() {
        return show_image_background;
    }

    /**
     * @param show_image_background the show_image_background to set
     */
    public void setShow_image_background(boolean show_image_background) {
        this.show_image_background = show_image_background;
    }

    /**
     * @return the show_image_logo
     */
    public boolean isShow_image_logo() {
        return show_image_logo;
    }

    /**
     * @param show_image_logo the show_image_logo to set
     */
    public void setShow_image_logo(boolean show_image_logo) {
        this.show_image_logo = show_image_logo;
    }

    /**
     * @return the print_tax_token
     */
    public boolean isPrint_tax_token() {
        return print_tax_token;
    }

    /**
     * @param print_tax_token the print_tax_token to set
     */
    public void setPrint_tax_token(boolean print_tax_token) {
        this.print_tax_token = print_tax_token;
    }

    /**
     * @return the c_full_add
     */
    public String getC_full_add() {
        return c_full_add;
    }

    /**
     * @param c_full_add the c_full_add to set
     */
    public void setC_full_add(String c_full_add) {
        this.c_full_add = c_full_add;
    }

    /**
     * @return the showRoadSafetySlogan
     */
    public boolean isShowRoadSafetySlogan() {
        return showRoadSafetySlogan;
    }

    /**
     * @param showRoadSafetySlogan the showRoadSafetySlogan to set
     */
    public void setShowRoadSafetySlogan(boolean showRoadSafetySlogan) {
        this.showRoadSafetySlogan = showRoadSafetySlogan;
    }

    /**
     * @return the roadSafetySloganDobj
     */
    public VmRoadSafetySloganPrintDobj getRoadSafetySloganDobj() {
        return roadSafetySloganDobj;
    }

    /**
     * @param roadSafetySloganDobj the roadSafetySloganDobj to set
     */
    public void setRoadSafetySloganDobj(VmRoadSafetySloganPrintDobj roadSafetySloganDobj) {
        this.roadSafetySloganDobj = roadSafetySloganDobj;
    }

    /**
     * @return the showCashRcptNote
     */
    public boolean isShowCashRcptNote() {
        return showCashRcptNote;
    }

    /**
     * @param showCashRcptNote the showCashRcptNote to set
     */
    public void setShowCashRcptNote(boolean showCashRcptNote) {
        this.showCashRcptNote = showCashRcptNote;
    }

    /**
     * @return the cashRcptNote
     */
    public String getCashRcptNote() {
        return cashRcptNote;
    }

    /**
     * @param cashRcptNote the cashRcptNote to set
     */
    public void setCashRcptNote(String cashRcptNote) {
        this.cashRcptNote = cashRcptNote;
    }

    /**
     * @return the exemAmount
     */
    public String getExemAmount() {
        return exemAmount;
    }

    /**
     * @param exemAmount the exemAmount to set
     */
    public void setExemAmount(String exemAmount) {
        this.exemAmount = exemAmount;
    }

    /**
     * @return the advance_receipt_validity_days
     */
    public int getAdvance_receipt_validity_days() {
        return advance_receipt_validity_days;
    }

    /**
     * @param advance_receipt_validity_days the advance_receipt_validity_days to
     * set
     */
    public void setAdvance_receipt_validity_days(int advance_receipt_validity_days) {
        this.advance_receipt_validity_days = advance_receipt_validity_days;
    }
}
