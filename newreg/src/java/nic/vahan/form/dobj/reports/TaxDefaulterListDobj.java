
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.reports;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author afzal
 */
public class TaxDefaulterListDobj implements Serializable {

    private String regn_no;
    private String tax_cleared_upto;
    private String vch_class_desc;
    private String vch_catg;
    private String cur_date;
    private List<TaxDefaulterListDobj> listFileExport;
    private String downloadFileName = "";
    private String totalCount;
    private int vch_class_cd;
    private String vch_catg_cd;
    private String owner_name;
    private String fatherName;
    private String Address;
    private String taxFrom;
    private String taxupTo;
    private String printDate;
    private String offName;
    private String stateName;
    private String ldwt;
    private String seatcap;
    private String taxamt;
    private String taxfine;
    private String totalamt;
    private String regndate;
    private String unldwt;
    private String grosswt;
    private String fitvalidt;
    private String fncr_name;
    private String fncr_address;
    private String rcpt_heading;
    private String rcpt_subheading;
    private String curr_address;
    private String memono;
    private String taxTenure;
    private String taxAmtInWords;
    private int pur_cd;
    private List<TaxDefaulterListDobj> listTaxAmt;
    private String purcdDescr;
    private String taxNoticeHead1;
    private String taxNoticeHead2;
    private String taxNoticeHead3;
    private String taxNoticeHead4;
    private String taxNoticeHead5;
    private int defaulterNoticeGracePeriod = 0;
    private String permanant_address;

    /**
     * @return the regn_no
     */
    public String getRegn_no() {
        return regn_no;
    }

    /**
     * @return the tax_cleared_upto
     */
    public String getTax_cleared_upto() {
        return tax_cleared_upto;
    }

    /**
     * @return the vch_class_desc
     */
    public String getVch_class_desc() {
        return vch_class_desc;
    }

    /**
     * @return the vch_catg
     */
    public String getVch_catg() {
        return vch_catg;
    }

    /**
     * @param vch_catg the vch_catg to set
     */
    public void setVch_catg(String vch_catg) {
        this.vch_catg = vch_catg;
    }

    /**
     * @param regn_no the regn_no to set
     */
    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    /**
     * @param tax_cleared_upto the tax_cleared_upto to set
     */
    public void setTax_cleared_upto(String tax_cleared_upto) {
        this.tax_cleared_upto = tax_cleared_upto;
    }

    /**
     * @param vch_class_desc the vch_class_desc to set
     */
    public void setVch_class_desc(String vch_class_desc) {
        this.vch_class_desc = vch_class_desc;
    }

    /**
     * @return the cur_date
     */
    public String getCur_date() {
        return cur_date;
    }

    /**
     * @param cur_date the cur_date to set
     */
    public void setCur_date(String cur_date) {
        this.cur_date = cur_date;
    }

    /**
     * @return the listFileExport
     */
    public List<TaxDefaulterListDobj> getListFileExport() {
        return listFileExport;
    }

    /**
     * @param listFileExport the listFileExport to set
     */
    public void setListFileExport(List<TaxDefaulterListDobj> listFileExport) {
        this.listFileExport = listFileExport;
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
     * @return the totalCount
     */
    public String getTotalCount() {
        return totalCount;
    }

    /**
     * @param totalCount the totalCount to set
     */
    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * @return the vch_class_cd
     */
    public int getVch_class_cd() {
        return vch_class_cd;
    }

    /**
     * @param vch_class_cd the vch_class_cd to set
     */
    public void setVch_class_cd(int vch_class_cd) {
        this.vch_class_cd = vch_class_cd;
    }

    /**
     * @return the vch_catg_cd
     */
    public String getVch_catg_cd() {
        return vch_catg_cd;
    }

    /**
     * @param vch_catg_cd the vch_catg_cd to set
     */
    public void setVch_catg_cd(String vch_catg_cd) {
        this.vch_catg_cd = vch_catg_cd;
    }

    /**
     * @return the owner_name
     */
    public String getOwner_name() {
        return owner_name;
    }

    /**
     * @param owner_name the owner_name to set
     */
    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    /**
     * @return the fatherName
     */
    public String getFatherName() {
        return fatherName;
    }

    /**
     * @param fatherName the fatherName to set
     */
    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    /**
     * @return the Address
     */
    public String getAddress() {
        return Address;
    }

    /**
     * @param Address the Address to set
     */
    public void setAddress(String Address) {
        this.Address = Address;
    }

    /**
     * @return the taxFrom
     */
    public String getTaxFrom() {
        return taxFrom;
    }

    /**
     * @param taxFrom the taxFrom to set
     */
    public void setTaxFrom(String taxFrom) {
        this.taxFrom = taxFrom;
    }

    /**
     * @return the taxupTo
     */
    public String getTaxupTo() {
        return taxupTo;
    }

    /**
     * @param taxupTo the taxupTo to set
     */
    public void setTaxupTo(String taxupTo) {
        this.taxupTo = taxupTo;
    }

    /**
     * @return the printDate
     */
    public String getPrintDate() {
        return printDate;
    }

    /**
     * @param printDate the printDate to set
     */
    public void setPrintDate(String printDate) {
        this.printDate = printDate;
    }

    /**
     * @return the offName
     */
    public String getOffName() {
        return offName;
    }

    /**
     * @param offName the offName to set
     */
    public void setOffName(String offName) {
        this.offName = offName;
    }

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
     * @return the ldwt
     */
    public String getLdwt() {
        return ldwt;
    }

    /**
     * @param ldwt the ldwt to set
     */
    public void setLdwt(String ldwt) {
        this.ldwt = ldwt;
    }

    /**
     * @return the seatcap
     */
    public String getSeatcap() {
        return seatcap;
    }

    /**
     * @param seatcap the seatcap to set
     */
    public void setSeatcap(String seatcap) {
        this.seatcap = seatcap;
    }

    /**
     * @return the taxamt
     */
    public String getTaxamt() {
        return taxamt;
    }

    /**
     * @param taxamt the taxamt to set
     */
    public void setTaxamt(String taxamt) {
        this.taxamt = taxamt;
    }

    /**
     * @return the taxfine
     */
    public String getTaxfine() {
        return taxfine;
    }

    /**
     * @param taxfine the taxfine to set
     */
    public void setTaxfine(String taxfine) {
        this.taxfine = taxfine;
    }

    /**
     * @return the totalamt
     */
    public String getTotalamt() {
        return totalamt;
    }

    /**
     * @param totalamt the totalamt to set
     */
    public void setTotalamt(String totalamt) {
        this.totalamt = totalamt;
    }

    /**
     * @return the regndate
     */
    public String getRegndate() {
        return regndate;
    }

    /**
     * @param regndate the regndate to set
     */
    public void setRegndate(String regndate) {
        this.regndate = regndate;
    }

    /**
     * @return the unldwt
     */
    public String getUnldwt() {
        return unldwt;
    }

    /**
     * @param unldwt the unldwt to set
     */
    public void setUnldwt(String unldwt) {
        this.unldwt = unldwt;
    }

    /**
     * @return the grosswt
     */
    public String getGrosswt() {
        return grosswt;
    }

    /**
     * @param grosswt the grosswt to set
     */
    public void setGrosswt(String grosswt) {
        this.grosswt = grosswt;
    }

    /**
     * @return the fitvalidt
     */
    public String getFitvalidt() {
        return fitvalidt;
    }

    /**
     * @param fitvalidt the fitvalidt to set
     */
    public void setFitvalidt(String fitvalidt) {
        this.fitvalidt = fitvalidt;
    }

    /**
     * @return the fncr_name
     */
    public String getFncr_name() {
        return fncr_name;
    }

    /**
     * @param fncr_name the fncr_name to set
     */
    public void setFncr_name(String fncr_name) {
        this.fncr_name = fncr_name;
    }

    /**
     * @return the fncr_address
     */
    public String getFncr_address() {
        return fncr_address;
    }

    /**
     * @param fncr_address the fncr_address to set
     */
    public void setFncr_address(String fncr_address) {
        this.fncr_address = fncr_address;
    }

    /**
     * @return the rcpt_heading
     */
    public String getRcpt_heading() {
        return rcpt_heading;
    }

    /**
     * @param rcpt_heading the rcpt_heading to set
     */
    public void setRcpt_heading(String rcpt_heading) {
        this.rcpt_heading = rcpt_heading;
    }

    /**
     * @return the rcpt_subheading
     */
    public String getRcpt_subheading() {
        return rcpt_subheading;
    }

    /**
     * @param rcpt_subheading the rcpt_subheading to set
     */
    public void setRcpt_subheading(String rcpt_subheading) {
        this.rcpt_subheading = rcpt_subheading;
    }

    /**
     * @return the curr_address
     */
    public String getCurr_address() {
        return curr_address;
    }

    /**
     * @param curr_address the curr_address to set
     */
    public void setCurr_address(String curr_address) {
        this.curr_address = curr_address;
    }

    /**
     * @return the memono
     */
    public String getMemono() {
        return memono;
    }

    /**
     * @param memono the memono to set
     */
    public void setMemono(String memono) {
        this.memono = memono;
    }

    /**
     * @return the taxTenure
     */
    public String getTaxTenure() {
        return taxTenure;
    }

    /**
     * @param taxTenure the taxTenure to set
     */
    public void setTaxTenure(String taxTenure) {
        this.taxTenure = taxTenure;
    }

    /**
     * @return the taxAmtInWords
     */
    public String getTaxAmtInWords() {
        return taxAmtInWords;
    }

    /**
     * @param taxAmtInWords the taxAmtInWords to set
     */
    public void setTaxAmtInWords(String taxAmtInWords) {
        this.taxAmtInWords = taxAmtInWords;
    }

    /**
     * @return the pur_cd
     */
    public int getPur_cd() {
        return pur_cd;
    }

    /**
     * @param pur_cd the pur_cd to set
     */
    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    /**
     * @return the listTaxAmt
     */
    public List<TaxDefaulterListDobj> getListTaxAmt() {
        return listTaxAmt;
    }

    /**
     * @param listTaxAmt the listTaxAmt to set
     */
    public void setListTaxAmt(List<TaxDefaulterListDobj> listTaxAmt) {
        this.listTaxAmt = listTaxAmt;
    }

    /**
     * @return the purcdDescr
     */
    public String getPurcdDescr() {
        return purcdDescr;
    }

    /**
     * @param purcdDescr the purcdDescr to set
     */
    public void setPurcdDescr(String purcdDescr) {
        this.purcdDescr = purcdDescr;
    }

    /**
     * @return the taxNoticeHead1
     */
    public String getTaxNoticeHead1() {
        return taxNoticeHead1;
    }

    /**
     * @param taxNoticeHead1 the taxNoticeHead1 to set
     */
    public void setTaxNoticeHead1(String taxNoticeHead1) {
        this.taxNoticeHead1 = taxNoticeHead1;
    }

    /**
     * @return the taxNoticeHead2
     */
    public String getTaxNoticeHead2() {
        return taxNoticeHead2;
    }

    /**
     * @param taxNoticeHead2 the taxNoticeHead2 to set
     */
    public void setTaxNoticeHead2(String taxNoticeHead2) {
        this.taxNoticeHead2 = taxNoticeHead2;
    }

    /**
     * @return the taxNoticeHead3
     */
    public String getTaxNoticeHead3() {
        return taxNoticeHead3;
    }

    /**
     * @param taxNoticeHead3 the taxNoticeHead3 to set
     */
    public void setTaxNoticeHead3(String taxNoticeHead3) {
        this.taxNoticeHead3 = taxNoticeHead3;
    }

    /**
     * @return the taxNoticeHead4
     */
    public String getTaxNoticeHead4() {
        return taxNoticeHead4;
    }

    /**
     * @param taxNoticeHead4 the taxNoticeHead4 to set
     */
    public void setTaxNoticeHead4(String taxNoticeHead4) {
        this.taxNoticeHead4 = taxNoticeHead4;
    }

    /**
     * @return the taxNoticeHead5
     */
    public String getTaxNoticeHead5() {
        return taxNoticeHead5;
    }

    /**
     * @param taxNoticeHead5 the taxNoticeHead5 to set
     */
    public void setTaxNoticeHead5(String taxNoticeHead5) {
        this.taxNoticeHead5 = taxNoticeHead5;
    }

    /**
     * @return the permanant_address
     */
    public String getPermanant_address() {
        return permanant_address;
    }

    /**
     * @param permanant_address the permanant_address to set
     */
    public void setPermanant_address(String permanant_address) {
        this.permanant_address = permanant_address;
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
