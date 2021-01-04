/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author tranC103
 */
public class ConvDobj implements Cloneable, Serializable {

    private String appl_no;
    private String old_regn_no;
    private String new_regn_no;
    private int old_vch_class;
    private String old_vch_catg;
    private Date old_fit_dt;
    private int new_vch_class;
    private String new_vch_catg;
    private Date new_fit_dt;
    private String perm_ref_no;
    private Date perm_dt;
    private String perm_by;
    private long excessAmt;
    private Date newTaxPaidUpto;
    private Date purchaseDate;
    private String newTaxMode;
    private Date newTaxDueFrom;
    private String newTaxDueFromFLag;
    private int otherCriteria;
    private boolean assignRetainNo;
    private String assignRetainRegnNo;
    private boolean assignFancyNumber;
    private String assignFancyRegnNumber;
    private int pmt_type = -1;
    private int pmt_catg = -1;
    private int serviceType = -1;
    private List permitTypeList = new ArrayList();
    private List PermitCategoryList = new ArrayList();
    private boolean nextSeriesGen;
    private int push_bk_seat = 0;
    private int ordinary_seat = 0;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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

    /**
     * @return the new_regn_no
     */
    public String getNew_regn_no() {
        return new_regn_no;
    }

    /**
     * @param new_regn_no the new_regn_no to set
     */
    public void setNew_regn_no(String new_regn_no) {
        this.new_regn_no = new_regn_no;
    }

    /**
     * @return the old_vch_class
     */
    public int getOld_vch_class() {
        return old_vch_class;
    }

    /**
     * @param old_vch_class the old_vch_class to set
     */
    public void setOld_vch_class(int old_vch_class) {
        this.old_vch_class = old_vch_class;
    }

    /**
     * @return the old_vch_catg
     */
    public String getOld_vch_catg() {
        return old_vch_catg;
    }

    /**
     * @param old_vch_catg the old_vch_catg to set
     */
    public void setOld_vch_catg(String old_vch_catg) {
        this.old_vch_catg = old_vch_catg;
    }

    /**
     * @return the old_fit_dt
     */
    public Date getOld_fit_dt() {
        return old_fit_dt;
    }

    /**
     * @param old_fit_dt the old_fit_dt to set
     */
    public void setOld_fit_dt(Date old_fit_dt) {
        this.old_fit_dt = old_fit_dt;
    }

    /**
     * @return the new_vch_class
     */
    public int getNew_vch_class() {
        return new_vch_class;
    }

    /**
     * @param new_vch_class the new_vch_class to set
     */
    public void setNew_vch_class(int new_vch_class) {
        this.new_vch_class = new_vch_class;
    }

    /**
     * @return the new_vch_catg
     */
    public String getNew_vch_catg() {
        return new_vch_catg;
    }

    /**
     * @param new_vch_catg the new_vch_catg to set
     */
    public void setNew_vch_catg(String new_vch_catg) {
        this.new_vch_catg = new_vch_catg;
    }

    /**
     * @return the new_fit_dt
     */
    public Date getNew_fit_dt() {
        return new_fit_dt;
    }

    /**
     * @param new_fit_dt the new_fit_dt to set
     */
    public void setNew_fit_dt(Date new_fit_dt) {
        this.new_fit_dt = new_fit_dt;
    }

    /**
     * @return the perm_ref_no
     */
    public String getPerm_ref_no() {
        return perm_ref_no;
    }

    /**
     * @param perm_ref_no the perm_ref_no to set
     */
    public void setPerm_ref_no(String perm_ref_no) {
        this.perm_ref_no = perm_ref_no;
    }

    /**
     * @return the perm_dt
     */
    public Date getPerm_dt() {
        return perm_dt;
    }

    /**
     * @param perm_dt the perm_dt to set
     */
    public void setPerm_dt(Date perm_dt) {
        this.perm_dt = perm_dt;
    }

    /**
     * @return the perm_by
     */
    public String getPerm_by() {
        return perm_by;
    }

    /**
     * @param perm_by the perm_by to set
     */
    public void setPerm_by(String perm_by) {
        this.perm_by = perm_by;
    }

    /**
     * @return the excessAmt
     */
    public long getExcessAmt() {
        return excessAmt;
    }

    /**
     * @param excessAmt the excessAmt to set
     */
    public void setExcessAmt(long excessAmt) {
        this.excessAmt = excessAmt;
    }

    /**
     * @return the newTaxPaidUpto
     */
    public Date getNewTaxPaidUpto() {
        return newTaxPaidUpto;
    }

    /**
     * @param newTaxPaidUpto the newTaxPaidUpto to set
     */
    public void setNewTaxPaidUpto(Date newTaxPaidUpto) {
        this.newTaxPaidUpto = newTaxPaidUpto;
    }

    /**
     * @return the purchaseDate
     */
    public Date getPurchaseDate() {
        return purchaseDate;
    }

    /**
     * @param purchaseDate the purchaseDate to set
     */
    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    /**
     * @return the newTaxMode
     */
    public String getNewTaxMode() {
        return newTaxMode;
    }

    /**
     * @param newTaxMode the newTaxMode to set
     */
    public void setNewTaxMode(String newTaxMode) {
        this.newTaxMode = newTaxMode;
    }

    /**
     * @return the newTaxDueFrom
     */
    public Date getNewTaxDueFrom() {
        return newTaxDueFrom;
    }

    /**
     * @param newTaxDueFrom the newTaxDueFrom to set
     */
    public void setNewTaxDueFrom(Date newTaxDueFrom) {
        this.newTaxDueFrom = newTaxDueFrom;
    }

    /**
     * @return the newTaxDueFromFLag
     */
    public String getNewTaxDueFromFLag() {
        return newTaxDueFromFLag;
    }

    /**
     * @param newTaxDueFromFLag the newTaxDueFromFLag to set
     */
    public void setNewTaxDueFromFLag(String newTaxDueFromFLag) {
        this.newTaxDueFromFLag = newTaxDueFromFLag;
    }

    /**
     * @return the otherCriteria
     */
    public int getOtherCriteria() {
        return otherCriteria;
    }

    /**
     * @param otherCriteria the otherCriteria to set
     */
    public void setOtherCriteria(int otherCriteria) {
        this.otherCriteria = otherCriteria;
    }

    /**
     * @return the assignRetainNo
     */
    public boolean isAssignRetainNo() {
        return assignRetainNo;
    }

    /**
     * @param assignRetainNo the assignRetainNo to set
     */
    public void setAssignRetainNo(boolean assignRetainNo) {
        this.assignRetainNo = assignRetainNo;
    }

    /**
     * @return the assignRetainRegnNo
     */
    public String getAssignRetainRegnNo() {
        return assignRetainRegnNo;
    }

    /**
     * @param assignRetainRegnNo the assignRetainRegnNo to set
     */
    public void setAssignRetainRegnNo(String assignRetainRegnNo) {
        this.assignRetainRegnNo = assignRetainRegnNo;
    }

    public boolean isAssignFancyNumber() {
        return assignFancyNumber;
    }

    public void setAssignFancyNumber(boolean assignFancyNumber) {
        this.assignFancyNumber = assignFancyNumber;
    }

    public String getAssignFancyRegnNumber() {
        return assignFancyRegnNumber;
    }

    public void setAssignFancyRegnNumber(String assignFancyRegnNumber) {
        this.assignFancyRegnNumber = assignFancyRegnNumber;
    }

    public List getPermitTypeList() {
        return permitTypeList;
    }

    public void setPermitTypeList(List permitTypeList) {
        this.permitTypeList = permitTypeList;
    }

    public List getPermitCategoryList() {
        return PermitCategoryList;
    }

    public void setPermitCategoryList(List PermitCategoryList) {
        this.PermitCategoryList = PermitCategoryList;
    }

    public int getPmt_type() {
        return pmt_type;
    }

    public void setPmt_type(int pmt_type) {
        this.pmt_type = pmt_type;
    }

    public int getPmt_catg() {
        return pmt_catg;
    }

    public void setPmt_catg(int pmt_catg) {
        this.pmt_catg = pmt_catg;
    }

    public boolean isNextSeriesGen() {
        return nextSeriesGen;
    }

    public void setNextSeriesGen(boolean nextSeriesGen) {
        this.nextSeriesGen = nextSeriesGen;
    }

    public int getPush_bk_seat() {
        return push_bk_seat;
    }

    public void setPush_bk_seat(int push_bk_seat) {
        this.push_bk_seat = push_bk_seat;
    }

    public int getOrdinary_seat() {
        return ordinary_seat;
    }

    public void setOrdinary_seat(int ordinary_seat) {
        this.ordinary_seat = ordinary_seat;
    }

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }
    
}
