/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.form.dobj.DOTaxDetail;

/**
 *
 * @author tranC094
 */
public class TaxFormPanelBean implements Serializable {

    // private List<DOTaxDetail> groupedTaxDetails;
    private List<DOTaxDetail> taxDescriptionList;
    private List listTaxModes = new ArrayList();
    private long totalPaybaleTax = 0;
    private long totalPaybalePenalty = 0;
    private long totalPaybaleSurcharge = 0;
    private long totalPaybaleRebate = 0;
    private long totalPaybaleInterest = 0;
    private long totalPayableAmount = 0;
    private long totalPayablePrvAdj = 0;
    private long totalTax1 = 0;
    private long totalTax2 = 0;
    //-----
    private String finalTaxFrom = null;
    private String finalTaxUpto = null;
    private Date finalEditableTaxFrom = DateUtil.parseDate(DateUtil.getCurrentDate());
    private Date finalEditableTaxUpto = DateUtil.parseDate(DateUtil.getCurrentDate());
    private long finalTaxAmount = 0;
    private String taxMode = "0";
    private int pur_cd = 0;
    private String taxPurcdDesc = null;
    private int noOfUnits = 1;
    private boolean disableNoOfUnits = false;
    private boolean renderPrvAdj = false;
    private boolean disableFinalTaxFrom = true;
    private boolean renderFinalTaxFrom = true;
    private boolean disableFinalTaxUpto = true;
    private boolean renderFinalTaxUpto = true;
    private Date initialDueDate = null;
    private Date initialEffectiveDate = null;
    private boolean renderEditableTax = false;
    private boolean renderEditableTax1 = false;
    private boolean renderEditableTax2 = false;
    private boolean renderEditablePenalty = false;
    private boolean disableTaxMode = false;
    private boolean disableTaxAmt = false;
    private boolean taxExemption = false;
    private String taxCalcBasedOnParms = null;
    private Date minFromDate = null;
    private Date maxFromDate = null;
    private Date minUptoDate = null;
    private Date maxUptoDate = null;
    private Date taxCeaseDate = null;

    public TaxFormPanelBean(Integer purCd) {
        this.pur_cd = purCd;
    }

    public TaxFormPanelBean() {
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.pur_cd);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TaxFormPanelBean other = (TaxFormPanelBean) obj;

        if (this.pur_cd == other.pur_cd) {
            return true;
        } else {
            return false;
        }

    }

    public void reset() {
        getTaxDescriptionList().clear();
        setTotalPaybaleInterest(0);
        setTotalPayableAmount(0);
        setTotalPaybalePenalty(0);
        setTotalPaybaleRebate(0);
        setTotalPaybaleSurcharge(0);
        setTotalPaybaleTax(0);

    }

    public void resetAll() {

        totalPaybaleTax = 0;
        totalPaybalePenalty = 0;
        totalPaybaleSurcharge = 0;
        totalPaybaleRebate = 0;
        totalPaybaleInterest = 0;
        totalPayableAmount = 0;
        totalPayablePrvAdj = 0;
        finalTaxAmount = 0;
        totalTax1 = 0;
        totalTax2 = 0;
        finalTaxFrom = "";
        finalTaxUpto = "";

    }

    public void updateTaxBean() {
        resetAll();
        int i = 0;
        long finalTaxAmount = 0;
        BigDecimal totaPayableInterest = BigDecimal.valueOf(0.0);
        BigDecimal totalPayablePenalty = BigDecimal.valueOf(0.0);
        BigDecimal totalPayableRebate = BigDecimal.valueOf(0.0);
        BigDecimal totalPayableSurcharge = BigDecimal.valueOf(0.0);
        BigDecimal totalPayableTax = BigDecimal.valueOf(0.0);
        BigDecimal totalPayablePrvTax = BigDecimal.valueOf(0.0);
        BigDecimal totalTax1 = BigDecimal.valueOf(0.0);
        BigDecimal tota1Tax2 = BigDecimal.valueOf(0.0);

        for (DOTaxDetail dobj : getTaxDescriptionList()) {
            if (i == 0) {
                setFinalTaxFrom(dobj.getTAX_FROM());
            }

            if (i == getTaxDescriptionList().size() - 1) {
                setFinalTaxUpto(dobj.getTAX_UPTO());
            }
            i++;

            totaPayableInterest = totaPayableInterest.add(BigDecimal.valueOf((double) dobj.getINTEREST()));
            totalPayablePenalty = totalPayablePenalty.add(BigDecimal.valueOf((double) dobj.getPENALTY()));
            totalPayableRebate = totalPayableRebate.add(BigDecimal.valueOf((double) dobj.getREBATE()));
            totalPayableSurcharge = totalPayableSurcharge.add(BigDecimal.valueOf((double) dobj.getSURCHARGE()));
            totalPayableTax = totalPayableTax.add(BigDecimal.valueOf((double) dobj.getAMOUNT()));
            totalPayablePrvTax = totalPayablePrvTax.add(BigDecimal.valueOf((double) dobj.getPRV_ADJ()));
            totalTax1 = totalTax1.add(BigDecimal.valueOf((double) dobj.getAMOUNT1()));
            tota1Tax2 = tota1Tax2.add(BigDecimal.valueOf((double) dobj.getAMOUNT2()));

        }

        setTotalPaybaleInterest(totaPayableInterest.longValue());
        setTotalPaybalePenalty(totalPayablePenalty.longValue());
        setTotalPaybaleRebate(totalPayableRebate.longValue());
        setTotalPaybaleSurcharge(totalPayableSurcharge.longValue());
        setTotalPaybaleTax(totalPayableTax.longValue());
        setTotalPayablePrvAdj(totalPayablePrvTax.longValue());
        setTotalTax1(totalTax1.longValue());
        setTotalTax2(tota1Tax2.longValue());
        finalTaxAmount = finalTaxAmount + getTotalPaybaleTax() + getTotalTax1() + getTotalTax2() + getTotalPaybaleSurcharge() + getTotalPaybaleInterest()
                - getTotalPaybaleRebate() + getTotalPaybalePenalty() + getTotalPayablePrvAdj();

        setFinalTaxAmount(finalTaxAmount);

    }

    public List<DOTaxDetail> getTaxDescriptionList() {
        if (taxDescriptionList == null) {
            taxDescriptionList = new ArrayList<DOTaxDetail>();
        }
        return taxDescriptionList;
    }

    public void setTaxDescriptionList(List<DOTaxDetail> taxDescriptionList) {
        this.taxDescriptionList = taxDescriptionList;
    }

    public long getTotalPaybaleTax() {
        return totalPaybaleTax;
    }

    public void setTotalPaybaleTax(long totalPaybaleTax) {
        this.totalPaybaleTax = totalPaybaleTax;
    }

    public long getTotalPaybalePenalty() {
        return totalPaybalePenalty;
    }

    public void setTotalPaybalePenalty(long totalPaybalePenalty) {
        this.totalPaybalePenalty = totalPaybalePenalty;
    }

    public long getTotalPaybaleSurcharge() {
        return totalPaybaleSurcharge;
    }

    public void setTotalPaybaleSurcharge(long totalPaybaleSurcharge) {
        this.totalPaybaleSurcharge = totalPaybaleSurcharge;
    }

    public long getTotalPaybaleRebate() {
        return totalPaybaleRebate;
    }

    public void setTotalPaybaleRebate(long totalPaybaleRebate) {
        this.totalPaybaleRebate = totalPaybaleRebate;
    }

    public long getTotalPaybaleInterest() {
        return totalPaybaleInterest;
    }

    public void setTotalPaybaleInterest(long totalPaybaleInterest) {
        this.totalPaybaleInterest = totalPaybaleInterest;
    }

    public long getTotalPayableAmount() {
        return totalPayableAmount;
    }

    public void setTotalPayableAmount(long totalPayableAmount) {
        this.totalPayableAmount = totalPayableAmount;
    }

    /**
     * @return the finalTaxFrom
     */
    public String getFinalTaxFrom() {
        return finalTaxFrom;
    }

    /**
     * @param finalTaxFrom the finalTaxFrom to set
     */
    public void setFinalTaxFrom(String finalTaxFrom) {
        this.finalTaxFrom = finalTaxFrom;
    }

    /**
     * @return the finalTaxUpto
     */
    public String getFinalTaxUpto() {
        return finalTaxUpto;
    }

    /**
     * @param finalTaxUpto the finalTaxUpto to set
     */
    public void setFinalTaxUpto(String finalTaxUpto) {
        this.finalTaxUpto = finalTaxUpto;
    }

    /**
     * @return the finalTaxAmount
     */
    public long getFinalTaxAmount() {
        return finalTaxAmount;
    }

    /**
     * @param finalTaxAmount the finalTaxAmount to set
     */
    public void setFinalTaxAmount(long finalTaxAmount) {
        this.finalTaxAmount = finalTaxAmount;
    }

    /**
     * @return the listTaxModes
     */
    public List getListTaxModes() {
        return listTaxModes;
    }

    /**
     * @param listTaxModes the listTaxModes to set
     */
    public void setListTaxModes(List listTaxModes) {
        this.listTaxModes = listTaxModes;
    }

    /**
     * @return the taxMode
     */
    public String getTaxMode() {
        return taxMode;
    }

    /**
     * @param taxMode the taxMode to set
     */
    public void setTaxMode(String taxMode) {
        this.taxMode = taxMode;
    }

    /**
     * @return the taxPurcdDesc
     */
    public String getTaxPurcdDesc() {
        return taxPurcdDesc;
    }

    /**
     * @param taxPurcdDesc the taxPurcdDesc to set
     */
    public void setTaxPurcdDesc(String taxPurcdDesc) {
        this.taxPurcdDesc = taxPurcdDesc;
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
     * @return the noOfUnits
     */
    public int getNoOfUnits() {
        return noOfUnits;
    }

    /**
     * @param noOfUnits the noOfUnits to set
     */
    public void setNoOfUnits(int noOfUnits) {
        this.noOfUnits = noOfUnits;
    }

    /**
     * @return the disableNoOfUnits
     */
    public boolean isDisableNoOfUnits() {
        return disableNoOfUnits;
    }

    /**
     * @param disableNoOfUnits the disableNoOfUnits to set
     */
    public void setDisableNoOfUnits(boolean disableNoOfUnits) {
        this.disableNoOfUnits = disableNoOfUnits;
    }

    /**
     * @return the totalPayablePrvAdj
     */
    public long getTotalPayablePrvAdj() {
        return totalPayablePrvAdj;
    }

    /**
     * @param totalPayablePrvAdj the totalPayablePrvAdj to set
     */
    public void setTotalPayablePrvAdj(long totalPayablePrvAdj) {
        this.totalPayablePrvAdj = totalPayablePrvAdj;
    }

    /**
     * @return the renderPrvAdj
     */
    public boolean isRenderPrvAdj() {
        return renderPrvAdj;
    }

    /**
     * @param renderPrvAdj the renderPrvAdj to set
     */
    public void setRenderPrvAdj(boolean renderPrvAdj) {
        this.renderPrvAdj = renderPrvAdj;
    }

    /**
     * @return the initialEffectiveDate
     */
    public Date getInitialEffectiveDate() {
        return initialEffectiveDate;
    }

    /**
     * @param initialEffectiveDate the initialEffectiveDate to set
     */
    public void setInitialEffectiveDate(Date initialEffectiveDate) {
        this.initialEffectiveDate = initialEffectiveDate;
    }

    /**
     * @return the initialDueDate
     */
    public Date getInitialDueDate() {
        return initialDueDate;
    }

    /**
     * @param initialDueDate the initialDueDate to set
     */
    public void setInitialDueDate(Date initialDueDate) {
        this.initialDueDate = initialDueDate;
    }

    @Override
    public String toString() {
        return pur_cd + "=" + taxMode; //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the totalTax1
     */
    public long getTotalTax1() {
        return totalTax1;
    }

    /**
     * @param totalTax1 the totalTax1 to set
     */
    public void setTotalTax1(long totalTax1) {
        this.totalTax1 = totalTax1;
    }

    /**
     * @return the totalTax2
     */
    public long getTotalTax2() {
        return totalTax2;
    }

    /**
     * @param totalTax2 the totalTax2 to set
     */
    public void setTotalTax2(long totalTax2) {
        this.totalTax2 = totalTax2;
    }

    /**
     * @return the disableFinalTaxFrom
     */
    public boolean isDisableFinalTaxFrom() {
        return disableFinalTaxFrom;
    }

    /**
     * @param disableFinalTaxFrom the disableFinalTaxFrom to set
     */
    public void setDisableFinalTaxFrom(boolean disableFinalTaxFrom) {
        this.disableFinalTaxFrom = disableFinalTaxFrom;
    }

    /**
     * @return the renderFinalTaxFrom
     */
    public boolean isRenderFinalTaxFrom() {
        return renderFinalTaxFrom;
    }

    /**
     * @param renderFinalTaxFrom the renderFinalTaxFrom to set
     */
    public void setRenderFinalTaxFrom(boolean renderFinalTaxFrom) {
        this.renderFinalTaxFrom = renderFinalTaxFrom;
    }

    /**
     * @return the disableFinalTaxUpto
     */
    public boolean isDisableFinalTaxUpto() {
        return disableFinalTaxUpto;
    }

    /**
     * @param disableFinalTaxUpto the disableFinalTaxUpto to set
     */
    public void setDisableFinalTaxUpto(boolean disableFinalTaxUpto) {
        this.disableFinalTaxUpto = disableFinalTaxUpto;
    }

    /**
     * @return the renderFinalTaxUpto
     */
    public boolean isRenderFinalTaxUpto() {
        return renderFinalTaxUpto;
    }

    /**
     * @param renderFinalTaxUpto the renderFinalTaxUpto to set
     */
    public void setRenderFinalTaxUpto(boolean renderFinalTaxUpto) {
        this.renderFinalTaxUpto = renderFinalTaxUpto;
    }

    /**
     * @return the renderEditableTax2
     */
    public boolean isRenderEditableTax2() {
        return renderEditableTax2;
    }

    /**
     * @param renderEditableTax2 the renderEditableTax2 to set
     */
    public void setRenderEditableTax2(boolean renderEditableTax2) {
        this.renderEditableTax2 = renderEditableTax2;
    }

    /**
     * @return the renderEditablePenalty
     */
    public boolean isRenderEditablePenalty() {
        return renderEditablePenalty;
    }

    /**
     * @param renderEditablePenalty the renderEditablePenalty to set
     */
    public void setRenderEditablePenalty(boolean renderEditablePenalty) {
        this.renderEditablePenalty = renderEditablePenalty;
    }

    /**
     * @return the finalEditableTaxFrom
     */
    public Date getFinalEditableTaxFrom() {
        return finalEditableTaxFrom;
    }

    /**
     * @param finalEditableTaxFrom the finalEditableTaxFrom to set
     */
    public void setFinalEditableTaxFrom(Date finalEditableTaxFrom) {
        this.finalEditableTaxFrom = finalEditableTaxFrom;
    }

    /**
     * @return the finalEditableTaxUpto
     */
    public Date getFinalEditableTaxUpto() {
        return finalEditableTaxUpto;
    }

    /**
     * @param finalEditableTaxUpto the finalEditableTaxUpto to set
     */
    public void setFinalEditableTaxUpto(Date finalEditableTaxUpto) {
        this.finalEditableTaxUpto = finalEditableTaxUpto;
    }

    /**
     * @return the disableTaxMode
     */
    public boolean isDisableTaxMode() {
        return disableTaxMode;
    }

    /**
     * @param disableTaxMode the disableTaxMode to set
     */
    public void setDisableTaxMode(boolean disableTaxMode) {
        this.disableTaxMode = disableTaxMode;
    }

    /**
     * @return the renderEditableTax
     */
    public boolean isRenderEditableTax() {
        return renderEditableTax;
    }

    /**
     * @param renderEditableTax the renderEditableTax to set
     */
    public void setRenderEditableTax(boolean renderEditableTax) {
        this.renderEditableTax = renderEditableTax;
    }

    /**
     * @return the renderEditableTax1
     */
    public boolean isRenderEditableTax1() {
        return renderEditableTax1;
    }

    /**
     * @param renderEditableTax1 the renderEditableTax1 to set
     */
    public void setRenderEditableTax1(boolean renderEditableTax1) {
        this.renderEditableTax1 = renderEditableTax1;
    }

    /**
     * @return the taxExemption
     */
    public boolean isTaxExemption() {
        return taxExemption;
    }

    /**
     * @param taxExemption the taxExemption to set
     */
    public void setTaxExemption(boolean taxExemption) {
        this.taxExemption = taxExemption;
    }

    /**
     * @return the taxCalcBasedOnParms
     */
    public String getTaxCalcBasedOnParms() {
        return taxCalcBasedOnParms;
    }

    /**
     * @param taxCalcBasedOnParms the taxCalcBasedOnParms to set
     */
    public void setTaxCalcBasedOnParms(String taxCalcBasedOnParms) {
        this.taxCalcBasedOnParms = taxCalcBasedOnParms;
    }

    /**
     * @return the minFromDate
     */
    public Date getMinFromDate() {
        return minFromDate;
    }

    /**
     * @param minFromDate the minFromDate to set
     */
    public void setMinFromDate(Date minFromDate) {
        this.minFromDate = minFromDate;
    }

    /**
     * @return the maxFromDate
     */
    public Date getMaxFromDate() {
        return maxFromDate;
    }

    /**
     * @param maxFromDate the maxFromDate to set
     */
    public void setMaxFromDate(Date maxFromDate) {
        this.maxFromDate = maxFromDate;
    }

    /**
     * @return the minUptoDate
     */
    public Date getMinUptoDate() {
        return minUptoDate;
    }

    /**
     * @param minUptoDate the minUptoDate to set
     */
    public void setMinUptoDate(Date minUptoDate) {
        this.minUptoDate = minUptoDate;
    }

    /**
     * @return the maxUptoDate
     */
    public Date getMaxUptoDate() {
        return maxUptoDate;
    }

    /**
     * @param maxUptoDate the maxUptoDate to set
     */
    public void setMaxUptoDate(Date maxUptoDate) {
        this.maxUptoDate = maxUptoDate;
    }

    /**
     * @return the disableTaxAmt
     */
    public boolean isDisableTaxAmt() {
        return disableTaxAmt;
    }

    /**
     * @param disableTaxAmt the disableTaxAmt to set
     */
    public void setDisableTaxAmt(boolean disableTaxAmt) {
        this.disableTaxAmt = disableTaxAmt;
    }

    public Date getTaxCeaseDate() {
        return taxCeaseDate;
    }

    public void setTaxCeaseDate(Date taxCeaseDate) {
        this.taxCeaseDate = taxCeaseDate;
    }
}
