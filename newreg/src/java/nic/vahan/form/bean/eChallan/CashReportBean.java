/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.eChallan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.db.TableConstants;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.eChallan.CashReportDobj;
import nic.vahan.form.dobj.eChallan.ChallanReportDobj;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
@ManagedBean
@ViewScoped
public class CashReportBean extends AbstractApplBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(CashReportBean.class);
    private String appl_no;
    private String cmpd_rcpt_no;
    private String chal_date;
    private String cmpd_rcpt_date;
    private String Owner_name;
    private String regn_no;
    private String chal_no;
    private int cmpd_amt;
    private int tax;
    private int tax_penalty;
    private int grand_total_tax;
    private long grandTotal;
    String lb_particular;
    private String rcpt_header;
    private String rcpt_sub_header;
    PrintDocImpl impl = new PrintDocImpl();
    CashReportDobj dobj = new CashReportDobj();
    private String numToWord;
    private String coming_from;
    private String going_to;
    private List<ChallanReportDobj> accusedOffenceList = new ArrayList<>();
    private String office_name;
    private String driver_name;

    public CashReportBean() {
        if (appl_details != null) {
            appl_no = appl_details.getAppl_no();
        }
        getCashReportDetails(appl_no);
    }

    public void getCashReportDetails(String applNo) {
        try {


            Utility util = new Utility();
            dobj = impl.getCashReportDetails(applNo);

            setChal_date(dobj.getChal_date());
            setChal_no(dobj.getChal_no());
            setOwner_name(dobj.getOwner_name());




            setRegn_no(dobj.getRegn_no());
            setCmpd_rcpt_date(dobj.getCmpd_rcpt_date());
            setCmpd_rcpt_no(dobj.getCmpd_rcpt_no());
            setCmpd_amt(dobj.getCmpd_amt());
            setTax(dobj.getTax());
            setTax_penalty(dobj.getTax_penalty());
            setGrand_total_tax(dobj.getGrand_total_tax());
            setGrandTotal(dobj.getGrand_total());
            setNumToWord(util.ConvertNumberToWords(dobj.getGrand_total()));
            setRcpt_header(ServerUtil.getRcptHeading());
            setRcpt_sub_header(ServerUtil.getRcptSubHeading());
            setComing_from(dobj.getComing_from().toUpperCase());
            setGoing_to(dobj.getGoing_to().toUpperCase());
            setAccusedOffenceList(PrintDocImpl.getOffenceAndAccusedDetails(applNo));
            setOffice_name(Util.getSelectedSeat().getOff_name());



            if (CommonUtils.isNullOrBlank(dobj.getOwner_name())) {
                for (ChallanReportDobj challanReportDobj : accusedOffenceList) {
              setOwner_name(challanReportDobj.getAccused());
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    /**
     * @return the cmpd_rcpt_no
     */
    public String getCmpd_rcpt_no() {
        return cmpd_rcpt_no;
    }

    /**
     * @param cmpd_rcpt_no the cmpd_rcpt_no to set
     */
    public void setCmpd_rcpt_no(String cmpd_rcpt_no) {
        this.cmpd_rcpt_no = cmpd_rcpt_no;
    }

    /**
     * @return the chal_date
     */
    public String getChal_date() {
        return chal_date;
    }

    /**
     * @param chal_date the chal_date to set
     */
    public void setChal_date(String chal_date) {
        this.chal_date = chal_date;
    }

    /**
     * @return the cmpd_rcpt_date
     */
    public String getCmpd_rcpt_date() {
        return cmpd_rcpt_date;
    }

    /**
     * @param cmpd_rcpt_date the cmpd_rcpt_date to set
     */
    public void setCmpd_rcpt_date(String cmpd_rcpt_date) {
        this.cmpd_rcpt_date = cmpd_rcpt_date;
    }

    /**
     * @return the Owner_name
     */
    public String getOwner_name() {
        return Owner_name;
    }

    /**
     * @param Owner_name the Owner_name to set
     */
    public void setOwner_name(String Owner_name) {
        this.Owner_name = Owner_name;
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
     * @return the chal_no
     */
    public String getChal_no() {
        return chal_no;
    }

    /**
     * @param chal_no the chal_no to set
     */
    public void setChal_no(String chal_no) {
        this.chal_no = chal_no;
    }

    /**
     * @return the cmpd_amt
     */
    public int getCmpd_amt() {
        return cmpd_amt;
    }

    /**
     * @param cmpd_amt the cmpd_amt to set
     */
    public void setCmpd_amt(int cmpd_amt) {
        this.cmpd_amt = cmpd_amt;
    }

    /**
     * @return the tax
     */
    public int getTax() {
        return tax;
    }

    /**
     * @param tax the tax to set
     */
    public void setTax(int tax) {
        this.tax = tax;
    }

    /**
     * @return the tax_penalty
     */
    public int getTax_penalty() {
        return tax_penalty;
    }

    /**
     * @param tax_penalty the tax_penalty to set
     */
    public void setTax_penalty(int tax_penalty) {
        this.tax_penalty = tax_penalty;
    }

    /**
     * @return the grand_total_tax
     */
    public int getGrand_total_tax() {
        return grand_total_tax;
    }

    /**
     * @param grand_total_tax the grand_total_tax to set
     */
    public void setGrand_total_tax(int grand_total_tax) {
        this.grand_total_tax = grand_total_tax;
    }

    /**
     * @return the appl_no
     */
    public String getAppl_no() {
        return appl_no;
    }

    /**
     * @param appl_no the appl_no to set
     */
    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    /**
     * @return the grandTotal
     */
    public long getGrandTotal() {
        return grandTotal;
    }

    /**
     * @param grandTotal the grandTotal to set
     */
    public void setGrandTotal(long grandTotal) {
        this.grandTotal = grandTotal;
    }

    /**
     * @return the rcpt_header
     */
    public String getRcpt_header() {
        return rcpt_header;
    }

    /**
     * @param rcpt_header the rcpt_header to set
     */
    public void setRcpt_header(String rcpt_header) {
        this.rcpt_header = rcpt_header;
    }

    /**
     * @return the rcpt_sub_header
     */
    public String getRcpt_sub_header() {
        return rcpt_sub_header;
    }

    /**
     * @param rcpt_sub_header the rcpt_sub_header to set
     */
    public void setRcpt_sub_header(String rcpt_sub_header) {
        this.rcpt_sub_header = rcpt_sub_header;
    }

    /**
     * @return the numToWord
     */
    public String getNumToWord() {
        return numToWord;
    }

    /**
     * @param numToWord the numToWord to set
     */
    public void setNumToWord(String numToWord) {
        this.numToWord = numToWord;
    }

    /**
     * @return the coming_from
     */
    public String getComing_from() {
        return coming_from;
    }

    /**
     * @param coming_from the coming_from to set
     */
    public void setComing_from(String coming_from) {
        this.coming_from = coming_from;
    }

    /**
     * @return the going_to
     */
    public String getGoing_to() {
        return going_to;
    }

    /**
     * @param going_to the going_to to set
     */
    public void setGoing_to(String going_to) {
        this.going_to = going_to;
    }

    public List<ChallanReportDobj> getAccusedOffenceList() {
        return accusedOffenceList;
    }

    public void setAccusedOffenceList(List<ChallanReportDobj> accusedOffenceList) {
        this.accusedOffenceList = accusedOffenceList;
    }

    public String getOffice_name() {
        return office_name;
    }

    public void setOffice_name(String office_name) {
        this.office_name = office_name;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }
}
