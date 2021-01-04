/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

/**
 *
 * @author nic5912
 */
public class DOTaxDetail implements Serializable {

    private Double AMOUNT;
    private Double AMOUNT1;
    private Double AMOUNT2;
    private Double FINE;
    private Double REBATE;
    private Double SURCHARGE;
    private String TAX_FROM;
    private String TAX_UPTO;
    private Double PENALTY;
    private Double INTEREST;
    private Integer PUR_CD;
    private Long PRV_ADJ = 0L;
    private String TAX_MODE;
    private String TAX_HEAD;

    public DOTaxDetail() {
    }

    @Override
    public String toString() {
        return "DOTaxDetail{" + "AMOUNT=" + AMOUNT + ", FINE=" + FINE + ", REBATE=" + REBATE + ", SURCHARGE=" + SURCHARGE + ", TAX_FROM=" + TAX_FROM + ", TAX_UPTO=" + TAX_UPTO + ", PENALTY=" + PENALTY + ", INTEREST=" + INTEREST + ", PUR_CD=" + PUR_CD + ", TAX_MODE=" + TAX_MODE + ", TAX_HEAD=" + TAX_HEAD + '}';
    }

    public Double getAMOUNT() {
        return AMOUNT;
    }

    public void setAMOUNT(Double AMOUNT) {
        this.AMOUNT = AMOUNT;
    }

    public Double getFINE() {
        return FINE;
    }

    public void setFINE(Double FINE) {
        this.FINE = FINE;
    }

    public Double getREBATE() {
        return REBATE;
    }

    public void setREBATE(Double REBATE) {
        this.REBATE = REBATE;
    }

    public Double getSURCHARGE() {
        return SURCHARGE;
    }

    public void setSURCHARGE(Double SURCHARGE) {
        this.SURCHARGE = SURCHARGE;
    }

    public String getTAX_FROM() {
        return TAX_FROM;
    }

    public void setTAX_FROM(String TAX_FROM) {
        this.TAX_FROM = TAX_FROM;
    }

    public String getTAX_UPTO() {
        return TAX_UPTO;
    }

    public void setTAX_UPTO(String TAX_UPTO) {
        this.TAX_UPTO = TAX_UPTO;
    }

    /**
     * @return the PENALTY
     */
    public Double getPENALTY() {
        return PENALTY;
    }

    /**
     * @param PENALTY the PENALTY to set
     */
    public void setPENALTY(Double PENALTY) {
        this.PENALTY = PENALTY;
    }

    /**
     * @return the INTEREST
     */
    public Double getINTEREST() {
        return INTEREST;
    }

    /**
     * @param INTEREST the INTEREST to set
     */
    public void setINTEREST(Double INTEREST) {
        this.INTEREST = INTEREST;
    }

    public Integer getPUR_CD() {
        return PUR_CD;
    }

    public void setPUR_CD(Integer PUR_CD) {
        this.PUR_CD = PUR_CD;
    }

    public String getTAX_MODE() {
        return TAX_MODE;
    }

    public void setTAX_MODE(String TAX_MODE) {
        this.TAX_MODE = TAX_MODE;
    }

    public String getTAX_HEAD() {
        return TAX_HEAD;
    }

    public void setTAX_HEAD(String TAX_HEAD) {
        this.TAX_HEAD = TAX_HEAD;
    }

    /**
     * @return the PRV_ADJ
     */
    public long getPRV_ADJ() {
        return PRV_ADJ;
    }

    /**
     * @param PRV_ADJ the PRV_ADJ to set
     */
    public void setPRV_ADJ(long PRV_ADJ) {
        this.PRV_ADJ = PRV_ADJ;
    }

    /**
     * @return the AMOUNT1
     */
    public Double getAMOUNT1() {
        return AMOUNT1;
    }

    /**
     * @param AMOUNT1 the AMOUNT1 to set
     */
    public void setAMOUNT1(Double AMOUNT1) {
        this.AMOUNT1 = AMOUNT1;
    }

    /**
     * @return the AMOUNT2
     */
    public Double getAMOUNT2() {
        return AMOUNT2;
    }

    /**
     * @param AMOUNT2 the AMOUNT2 to set
     */
    public void setAMOUNT2(Double AMOUNT2) {
        this.AMOUNT2 = AMOUNT2;
    }
}
