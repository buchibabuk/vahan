/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.fancy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Administrator
 */
public class AdvanceRegnNo_dobj implements Serializable {

    private String state_cd;
    private int off_cd;
    private String off_desc;
    private String recp_no;
    private String regn_appl_no;
    private String old_regn_no;
    private String regn_no;
    private String owner_name;
    private String f_name;
    private String c_add1;
    private String c_add2;
    private String c_add3;
    private int c_district;
    private String chasisNo;
    private int c_pincode = 0;
    private String c_state;
    private Long mobile_no;
    private long total_amt;
    private long userCharge;
    private String recp_date;
    private List<AdvanceRegnNo_dobj> availReservedNumbers = new ArrayList();
    private boolean renderNumberdatatbl = false;
    private List<AdvanceRegnNo_dobj> getSerieslist = new ArrayList();
    private String seriesName;
    private int runningNumber;
    private String Consolidatedseries;
    private int regnNo;
    private List<AdvanceRegnNo_dobj> availNumbers = new ArrayList();
    private String regnType;
    private String vehClass;
    private String orderNumber;
    private int ownerCd;
    private Date rcptDate;
    private String vch_catg;
    private String serialNo = "";
    private int pmtType;

    /**
     * @return the state_cd
     */
    public String getState_cd() {
        return state_cd;
    }

    /**
     * @param state_cd the state_cd to set
     */
    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    /**
     * @return the off_cd
     */
    public int getOff_cd() {
        return off_cd;
    }

    /**
     * @param off_cd the off_cd to set
     */
    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }

    /**
     * @return the recp_no
     */
    public String getRecp_no() {
        return recp_no;
    }

    /**
     * @param recp_no the recp_no to set
     */
    public void setRecp_no(String recp_no) {
        this.recp_no = recp_no;
    }

    /**
     * @return the regn_appl_no
     */
    public String getRegn_appl_no() {
        return regn_appl_no;
    }

    /**
     * @param regn_appl_no the regn_appl_no to set
     */
    public void setRegn_appl_no(String regn_appl_no) {
        this.regn_appl_no = regn_appl_no;
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
     * @return the f_name
     */
    public String getF_name() {
        return f_name;
    }

    /**
     * @param f_name the f_name to set
     */
    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    /**
     * @return the c_add1
     */
    public String getC_add1() {
        return c_add1;
    }

    /**
     * @param c_add1 the c_add1 to set
     */
    public void setC_add1(String c_add1) {
        this.c_add1 = c_add1;
    }

    /**
     * @return the c_add2
     */
    public String getC_add2() {
        return c_add2;
    }

    /**
     * @param c_add2 the c_add2 to set
     */
    public void setC_add2(String c_add2) {
        this.c_add2 = c_add2;
    }

    /**
     * @return the c_add3
     */
    public String getC_add3() {
        return c_add3;
    }

    /**
     * @param c_add3 the c_add3 to set
     */
    public void setC_add3(String c_add3) {
        this.c_add3 = c_add3;
    }

    /**
     * @return the c_district
     */
    public int getC_district() {
        return c_district;
    }

    /**
     * @param c_district the c_district to set
     */
    public void setC_district(int c_district) {
        this.c_district = c_district;
    }

    /**
     * @return the c_state
     */
    public String getC_state() {
        return c_state;
    }

    /**
     * @param c_state the c_state to set
     */
    public void setC_state(String c_state) {
        this.c_state = c_state;
    }

    /**
     * @return the total_amt
     */
    public long getTotal_amt() {
        return total_amt;
    }

    /**
     * @param total_amt the total_amt to set
     */
    public void setTotal_amt(long total_amt) {
        this.total_amt = total_amt;
    }

    /**
     * @return the userCharge
     */
    public long getUserCharge() {
        return userCharge;
    }

    /**
     * @param userCharge the userCharge to set
     */
    public void setUserCharge(long userCharge) {
        this.userCharge = userCharge;
    }

    /**
     * @return the availReservedNumbers
     */
    /**
     * @return the recp_date
     */
    public String getRecp_date() {
        return recp_date;
    }

    /**
     * @param recp_date the recp_date to set
     */
    public void setRecp_date(String recp_date) {
        this.recp_date = recp_date;
    }

    /**
     * @return the off_desc
     */
    public String getOff_desc() {
        return off_desc;
    }

    /**
     * @param off_desc the off_desc to set
     */
    public void setOff_desc(String off_desc) {
        this.off_desc = off_desc;
    }

    /**
     * @return the regnType
     */
    public String getRegnType() {
        return regnType;
    }

    /**
     * @param regnType the regnType to set
     */
    public void setRegnType(String regnType) {
        this.regnType = regnType;
    }

    /**
     * @return the vehClass
     */
    public String getVehClass() {
        return vehClass;
    }

    /**
     * @param vehClass the vehClass to set
     */
    public void setVehClass(String vehClass) {
        this.vehClass = vehClass;
    }

    /**
     * @return the chasisNo
     */
    public String getChasisNo() {
        return chasisNo;
    }

    /**
     * @param chasisNo the chasisNo to set
     */
    public void setChasisNo(String chasisNo) {
        this.chasisNo = chasisNo;
    }

    /**
     * @return the orderNumber
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * @param orderNumber the orderNumber to set
     */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getC_pincode() {
        return c_pincode;
    }

    public void setC_pincode(int c_pincode) {
        this.c_pincode = c_pincode;
    }

    public Long getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(Long mobile_no) {
        this.mobile_no = mobile_no;
    }

    /**
     * @return the ownerCd
     */
    public int getOwnerCd() {
        return ownerCd;
    }

    /**
     * @param ownerCd the ownerCd to set
     */
    public void setOwnerCd(int ownerCd) {
        this.ownerCd = ownerCd;
    }

    /**
     * @return the rcptDate
     */
    public Date getRcptDate() {
        return rcptDate;
    }

    /**
     * @param rcptDate the rcptDate to set
     */
    public void setRcptDate(Date rcptDate) {
        this.rcptDate = rcptDate;
    }

    public String getVch_catg() {
        return vch_catg;
    }

    public void setVch_catg(String vch_catg) {
        this.vch_catg = vch_catg;
    }

    public List<AdvanceRegnNo_dobj> getAvailReservedNumbers() {
        return availReservedNumbers;
    }

    public void setAvailReservedNumbers(List<AdvanceRegnNo_dobj> availReservedNumbers) {
        this.availReservedNumbers = availReservedNumbers;
    }

    public List<AdvanceRegnNo_dobj> getAvailNumbers() {
        Map mapReport;
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        mapReport = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        List<AdvanceRegnNo_dobj> numberList = (ArrayList<AdvanceRegnNo_dobj>) mapReport.get("AdvanceAvailableNumberList");
        if (numberList != null) {
            setAvailNumbers(numberList);
        }

        return availNumbers;
    }

    public void setAvailNumbers(List<AdvanceRegnNo_dobj> availNumbers) {
        this.availNumbers = availNumbers;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public List<AdvanceRegnNo_dobj> getGetSerieslist() {
        return getSerieslist;
    }

    public void setGetSerieslist(List<AdvanceRegnNo_dobj> getSerieslist) {
        this.getSerieslist = getSerieslist;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public boolean isRenderNumberdatatbl() {
        return renderNumberdatatbl;
    }

    public void setRenderNumberdatatbl(boolean renderNumberdatatbl) {
        this.renderNumberdatatbl = renderNumberdatatbl;
    }

    public int getRunningNumber() {
        return runningNumber;
    }

    public void setRunningNumber(int runningNumber) {
        this.runningNumber = runningNumber;
    }

    public String getConsolidatedseries() {
        return Consolidatedseries;
    }

    public void setConsolidatedseries(String Consolidatedseries) {
        this.Consolidatedseries = Consolidatedseries;
    }

    public int getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(int regnNo) {
        this.regnNo = regnNo;
    }

    public int getPmtType() {
        return pmtType;
    }

    public void setPmtType(int pmtType) {
        this.pmtType = pmtType;
    }

    /**
     * @return the old_regn_no
     */
    public String getOld_regn_no() {
        return old_regn_no;
    }

    /**
     * @param old_regn_no the old_regn_no to set
     */
    public void setOld_regn_no(String old_regn_no) {
        this.old_regn_no = old_regn_no;
    }
}
