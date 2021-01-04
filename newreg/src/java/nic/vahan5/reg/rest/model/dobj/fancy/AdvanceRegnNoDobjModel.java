/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.rest.model.dobj.fancy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nic.vahan.form.dobj.fancy.AdvanceRegnNo_dobj;

/**
 * @author Kartikey Singh Created this because
 * AdvanceRegnNo_dobj.getAvailMessage() had a reference to Session object
 */
public class AdvanceRegnNoDobjModel {

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
    private int c_pincode;
    private String c_state;
    private Long mobile_no;
    private long total_amt;
    private long userCharge;
    private String recp_date;
    private List<AdvanceRegnNo_dobj> availReservedNumbers;
    private boolean renderNumberdatatbl;
    private List<AdvanceRegnNo_dobj> getSerieslist;
    private String seriesName;
    private int runningNumber;
    private String Consolidatedseries;
    private int regnNo;
    private List<AdvanceRegnNo_dobj> availNumbers;
    private String regnType;
    private String vehClass;
    private String orderNumber;
    private int ownerCd;
    private Date rcptDate;
    private String vch_catg;
    private String serialNo;
    private int pmtType;

    public AdvanceRegnNoDobjModel() {
    }

    public AdvanceRegnNoDobjModel(AdvanceRegnNo_dobj advanceRegnNoDobj) {
        if (advanceRegnNoDobj != null) {
            this.state_cd = advanceRegnNoDobj.getState_cd();
            this.off_cd = advanceRegnNoDobj.getOff_cd();
            this.off_desc = advanceRegnNoDobj.getOff_desc();
            this.recp_no = advanceRegnNoDobj.getRecp_no();
            this.regn_appl_no = advanceRegnNoDobj.getRegn_appl_no();
            this.old_regn_no = advanceRegnNoDobj.getOld_regn_no();
            this.regn_no = advanceRegnNoDobj.getRegn_no();
            this.owner_name = advanceRegnNoDobj.getOwner_name();
            this.f_name = advanceRegnNoDobj.getF_name();
            this.c_add1 = advanceRegnNoDobj.getC_add1();
            this.c_add2 = advanceRegnNoDobj.getC_add2();
            this.c_add3 = advanceRegnNoDobj.getC_add3();
            this.c_district = advanceRegnNoDobj.getC_district();
            this.chasisNo = advanceRegnNoDobj.getChasisNo();
            this.c_state = advanceRegnNoDobj.getC_state();
            this.mobile_no = advanceRegnNoDobj.getMobile_no();
            this.total_amt = advanceRegnNoDobj.getTotal_amt();
            this.userCharge = advanceRegnNoDobj.getUserCharge();
            this.recp_date = advanceRegnNoDobj.getRecp_date();
            this.availReservedNumbers = advanceRegnNoDobj.getAvailReservedNumbers();
            this.renderNumberdatatbl = advanceRegnNoDobj.isRenderNumberdatatbl();
            this.getSerieslist = advanceRegnNoDobj.getGetSerieslist();
            this.seriesName = advanceRegnNoDobj.getSeriesName();
            this.runningNumber = advanceRegnNoDobj.getRunningNumber();
            this.Consolidatedseries = advanceRegnNoDobj.getConsolidatedseries();
            this.regnNo = advanceRegnNoDobj.getRegnNo();
            this.availNumbers = advanceRegnNoDobj.getAvailNumbers();
            this.regnType = advanceRegnNoDobj.getRegnType();
            this.vehClass = advanceRegnNoDobj.getVehClass();
            this.orderNumber = advanceRegnNoDobj.getOrderNumber();
            this.ownerCd = advanceRegnNoDobj.getOwnerCd();
            this.rcptDate = advanceRegnNoDobj.getRcptDate();
            this.vch_catg = advanceRegnNoDobj.getVch_catg();
            this.serialNo = advanceRegnNoDobj.getSerialNo();
            this.pmtType = advanceRegnNoDobj.getPmtType();
        }
    }

    public static AdvanceRegnNo_dobj convertModelToDobj(AdvanceRegnNoDobjModel advanceRegnNoDobjModel) {
        AdvanceRegnNo_dobj advanceRegnNoDobj = new AdvanceRegnNo_dobj();
        if (advanceRegnNoDobjModel != null) {            
            advanceRegnNoDobj.setState_cd(advanceRegnNoDobjModel.getState_cd());
            advanceRegnNoDobj.setOff_cd(advanceRegnNoDobjModel.getOff_cd());
            advanceRegnNoDobj.setOff_desc(advanceRegnNoDobjModel.getOff_desc());
            advanceRegnNoDobj.setRecp_no(advanceRegnNoDobjModel.getRecp_no());
            advanceRegnNoDobj.setRegn_appl_no(advanceRegnNoDobjModel.getRegn_appl_no());
            advanceRegnNoDobj.setOld_regn_no(advanceRegnNoDobjModel.getOld_regn_no());
            advanceRegnNoDobj.setRegn_no(advanceRegnNoDobjModel.getRegn_no());
            advanceRegnNoDobj.setOwner_name(advanceRegnNoDobjModel.getOwner_name());
            advanceRegnNoDobj.setF_name(advanceRegnNoDobjModel.getF_name());
            advanceRegnNoDobj.setC_add1(advanceRegnNoDobjModel.getC_add1());
            advanceRegnNoDobj.setC_add2(advanceRegnNoDobjModel.getC_add2());
            advanceRegnNoDobj.setC_add3(advanceRegnNoDobjModel.getC_add3());
            advanceRegnNoDobj.setC_district(advanceRegnNoDobjModel.getC_district());
            advanceRegnNoDobj.setChasisNo(advanceRegnNoDobjModel.getChasisNo());
            advanceRegnNoDobj.setC_state(advanceRegnNoDobjModel.getC_state());
            advanceRegnNoDobj.setMobile_no(advanceRegnNoDobjModel.getMobile_no());
            advanceRegnNoDobj.setTotal_amt(advanceRegnNoDobjModel.getTotal_amt());
            advanceRegnNoDobj.setUserCharge(advanceRegnNoDobjModel.getUserCharge());
            advanceRegnNoDobj.setRecp_date(advanceRegnNoDobjModel.getRecp_date());
            advanceRegnNoDobj.setAvailReservedNumbers(advanceRegnNoDobjModel.getAvailReservedNumbers());
            advanceRegnNoDobj.setRenderNumberdatatbl(advanceRegnNoDobjModel.isRenderNumberdatatbl());
            advanceRegnNoDobj.setGetSerieslist(advanceRegnNoDobjModel.getGetSerieslist());
            advanceRegnNoDobj.setSeriesName(advanceRegnNoDobjModel.getSeriesName());
            advanceRegnNoDobj.setRunningNumber(advanceRegnNoDobjModel.getRunningNumber());
            advanceRegnNoDobj.setConsolidatedseries(advanceRegnNoDobjModel.getConsolidatedseries());
            advanceRegnNoDobj.setRegnNo(advanceRegnNoDobjModel.getRegnNo());
            advanceRegnNoDobj.setAvailNumbers(advanceRegnNoDobjModel.getAvailNumbers());
            advanceRegnNoDobj.setRegnType(advanceRegnNoDobjModel.getRegnType());
            advanceRegnNoDobj.setVehClass(advanceRegnNoDobjModel.getVehClass());
            advanceRegnNoDobj.setOrderNumber(advanceRegnNoDobjModel.getOrderNumber());
            advanceRegnNoDobj.setOwnerCd(advanceRegnNoDobjModel.getOwnerCd());
            advanceRegnNoDobj.setRcptDate(advanceRegnNoDobjModel.getRcptDate());
            advanceRegnNoDobj.setVch_catg(advanceRegnNoDobjModel.getVch_catg());
            advanceRegnNoDobj.setSerialNo(advanceRegnNoDobjModel.getSerialNo());
            advanceRegnNoDobj.setPmtType(advanceRegnNoDobjModel.getPmtType());
        }
        return advanceRegnNoDobj;
    }

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public int getOff_cd() {
        return off_cd;
    }

    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }

    public String getOff_desc() {
        return off_desc;
    }

    public void setOff_desc(String off_desc) {
        this.off_desc = off_desc;
    }

    public String getRecp_no() {
        return recp_no;
    }

    public void setRecp_no(String recp_no) {
        this.recp_no = recp_no;
    }

    public String getRegn_appl_no() {
        return regn_appl_no;
    }

    public void setRegn_appl_no(String regn_appl_no) {
        this.regn_appl_no = regn_appl_no;
    }

    public String getOld_regn_no() {
        return old_regn_no;
    }

    public void setOld_regn_no(String old_regn_no) {
        this.old_regn_no = old_regn_no;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getF_name() {
        return f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getC_add1() {
        return c_add1;
    }

    public void setC_add1(String c_add1) {
        this.c_add1 = c_add1;
    }

    public String getC_add2() {
        return c_add2;
    }

    public void setC_add2(String c_add2) {
        this.c_add2 = c_add2;
    }

    public String getC_add3() {
        return c_add3;
    }

    public void setC_add3(String c_add3) {
        this.c_add3 = c_add3;
    }

    public int getC_district() {
        return c_district;
    }

    public void setC_district(int c_district) {
        this.c_district = c_district;
    }

    public String getChasisNo() {
        return chasisNo;
    }

    public void setChasisNo(String chasisNo) {
        this.chasisNo = chasisNo;
    }

    public int getC_pincode() {
        return c_pincode;
    }

    public void setC_pincode(int c_pincode) {
        this.c_pincode = c_pincode;
    }

    public String getC_state() {
        return c_state;
    }

    public void setC_state(String c_state) {
        this.c_state = c_state;
    }

    public Long getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(Long mobile_no) {
        this.mobile_no = mobile_no;
    }

    public long getTotal_amt() {
        return total_amt;
    }

    public void setTotal_amt(long total_amt) {
        this.total_amt = total_amt;
    }

    public long getUserCharge() {
        return userCharge;
    }

    public void setUserCharge(long userCharge) {
        this.userCharge = userCharge;
    }

    public String getRecp_date() {
        return recp_date;
    }

    public void setRecp_date(String recp_date) {
        this.recp_date = recp_date;
    }

    public List<AdvanceRegnNo_dobj> getAvailReservedNumbers() {
        return availReservedNumbers;
    }

    public void setAvailReservedNumbers(List<AdvanceRegnNo_dobj> availReservedNumbers) {
        this.availReservedNumbers = availReservedNumbers;
    }

    public boolean isRenderNumberdatatbl() {
        return renderNumberdatatbl;
    }

    public void setRenderNumberdatatbl(boolean renderNumberdatatbl) {
        this.renderNumberdatatbl = renderNumberdatatbl;
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

    public List<AdvanceRegnNo_dobj> getAvailNumbers() {
        return availNumbers;
    }

    public void setAvailNumbers(List<AdvanceRegnNo_dobj> availNumbers) {
        this.availNumbers = availNumbers;
    }

    public String getRegnType() {
        return regnType;
    }

    public void setRegnType(String regnType) {
        this.regnType = regnType;
    }

    public String getVehClass() {
        return vehClass;
    }

    public void setVehClass(String vehClass) {
        this.vehClass = vehClass;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getOwnerCd() {
        return ownerCd;
    }

    public void setOwnerCd(int ownerCd) {
        this.ownerCd = ownerCd;
    }

    public Date getRcptDate() {
        return rcptDate;
    }

    public void setRcptDate(Date rcptDate) {
        this.rcptDate = rcptDate;
    }

    public String getVch_catg() {
        return vch_catg;
    }

    public void setVch_catg(String vch_catg) {
        this.vch_catg = vch_catg;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public int getPmtType() {
        return pmtType;
    }

    public void setPmtType(int pmtType) {
        this.pmtType = pmtType;
    }
}
