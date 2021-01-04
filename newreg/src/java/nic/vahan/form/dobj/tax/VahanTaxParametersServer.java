/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.tax;

/**
 *
 * @author DELL
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VahanTaxParametersServer implements Serializable {

    private boolean FINANCIAL_QTR;
    private boolean FINANCIAL_YEAR;
    private boolean CALENDAR_MONTH;
    private boolean COMPLETE_QTR;
    private Integer DELAY_MONTHS = 0;
    private Integer DELAY_DAYS = 0;
    private Integer VCH_AGE = 0;
    private Integer PURCHASE_DT_VCH_AGE = 0;
    private Integer VCH_AGE_MONTHS = 0;
    private Integer TAX_MONTH_DURATION = 0;
    private Integer FIN_VCH_AGE = 0;
    private Double TAX_AMT = 0.0;
    private Double NET_TAX_AMT = 0.0;
    private Double SURCHARGE = 0.0;
    private Double REBATE = 0.0;
    private Double PENALTY = 0.0;
    private Double INTEREST = 0.0;
    private Double TAX_AMT_ROUND_TO = 1.0;
    private boolean PENALTY_FROM_FIRST_TAX_DUE_DATE;
    private Map mapTagFields = new HashMap<String, String>();
    private ArrayList arrTaxDuePeriods = new ArrayList();
    private Integer TAX_PERIOD_SR_NO = 1;
    private Integer HOLIDAYS = 0;
    private String TAX_BREAKUP_FROM_DATE = "";
    private String TAX_BREAKUP_UPTO_DATE = "";

    /**
     * @return the mapTagFields
     */
    public Map getMapTagFields() {
        return mapTagFields;
    }

    /**
     * @param mapTagFields the mapTagFields to set
     */
    public void setMapTagFields(Map mapTagFields) {
//        mapTagFields.put("$0", getSEAT_CAP().toString());
//        mapTagFields.put("$1", getUNLD_WT().toString());
//        mapTagFields.put("$2", getLD_WT().toString());
//        mapTagFields.put("$3", getHP().toString());
//        mapTagFields.put("$4", getCC().toString());
//        mapTagFields.put("$5", getSALE_AMT().toString());
//        mapTagFields.put("$6", getNO_OF_TRIPS_PER_DAY().toString());
//        mapTagFields.put("$7", getROUTE_LENGTH().toString());
//        mapTagFields.put("$8", getSTAND_CAP().toString());

        this.mapTagFields = mapTagFields;
    }

    /**
     * @return the FINANCIAL_YEAR
     */
    public boolean isFINANCIAL_YEAR() {
        return FINANCIAL_YEAR;
    }

    /**
     * @param FINANCIAL_YEAR the FINANCIAL_YEAR to set
     */
    public void setFINANCIAL_YEAR(boolean FINANCIAL_YEAR) {
        this.FINANCIAL_YEAR = FINANCIAL_YEAR;
    }

    /**
     * @return the CALENDAR_MONTH
     */
    public boolean isCALENDAR_MONTH() {
        return CALENDAR_MONTH;
    }

    /**
     * @param CALENDAR_MONTH the CALENDAR_MONTH to set
     */
    public void setCALENDAR_MONTH(boolean CALENDAR_MONTH) {
        this.CALENDAR_MONTH = CALENDAR_MONTH;
    }

    /**
     * @return the arrTaxDuePeriods
     */
    public ArrayList getArrTaxDuePeriods() {
        return arrTaxDuePeriods;
    }

    /**
     * @param arrTaxDuePeriods the arrTaxDuePeriods to set
     */
    public void setArrTaxDuePeriods(ArrayList arrTaxDuePeriods) {
        this.arrTaxDuePeriods = arrTaxDuePeriods;
    }

    /**
     * @return the VCH_AGE
     */
    public Integer getVCH_AGE() {
        return VCH_AGE;
    }

    /**
     * @param VCH_AGE the VCH_AGE to set
     */
    public void setVCH_AGE(Integer VCH_AGE) {
        this.VCH_AGE = VCH_AGE;
    }

    /**
     * @return the DELAY_MONTHS
     */
    public Integer getDELAY_MONTHS() {
        return DELAY_MONTHS;
    }

    /**
     * @param DELAY_MONTHS the DELAY_MONTHS to set
     */
    public void setDELAY_MONTHS(Integer DELAY_MONTHS) {
        this.DELAY_MONTHS = DELAY_MONTHS;
    }

    /**
     * @return the DELAY_DAYS
     */
    public Integer getDELAY_DAYS() {
        return DELAY_DAYS;
    }

    /**
     * @param DELAY_DAYS the DELAY_DAYS to set
     */
    public void setDELAY_DAYS(Integer DELAY_DAYS) {
        this.DELAY_DAYS = DELAY_DAYS;
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

    /**
     * @return the TAX_AMT
     */
    public Double getTAX_AMT() {
        return TAX_AMT;
    }

    /**
     * @param TAX_AMT the TAX_AMT to set
     */
    public void setTAX_AMT(Double TAX_AMT) {
        this.TAX_AMT = TAX_AMT;
    }

    /**
     * @return the SURCHARGE
     */
    public Double getSURCHARGE() {
        return SURCHARGE;
    }

    /**
     * @param SURCHARGE the SURCHARGE to set
     */
    public void setSURCHARGE(Double SURCHARGE) {
        this.SURCHARGE = SURCHARGE;
    }

    /**
     * @return the REBATE
     */
    public Double getREBATE() {
        return REBATE;
    }

    /**
     * @param REBATE the REBATE to set
     */
    public void setREBATE(Double REBATE) {
        this.REBATE = REBATE;
    }

    /**
     * @return the FIN_VCH_AGE
     */
    public Integer getFIN_VCH_AGE() {
        return FIN_VCH_AGE;
    }

    /**
     * @param FIN_VCH_AGE the FIN_VCH_AGE to set
     */
    public void setFIN_VCH_AGE(Integer FIN_VCH_AGE) {
        this.FIN_VCH_AGE = FIN_VCH_AGE;
    }

    /**
     * @return the FINANCIAL_QTR
     */
    public boolean isFINANCIAL_QTR() {
        return FINANCIAL_QTR;
    }

    /**
     * @param FINANCIAL_QTR the FINANCIAL_QTR to set
     */
    public void setFINANCIAL_QTR(boolean FINANCIAL_QTR) {
        this.FINANCIAL_QTR = FINANCIAL_QTR;
    }

    /**
     * @return the NET_TAX_AMT
     */
    public Double getNET_TAX_AMT() {
        return NET_TAX_AMT;
    }

    /**
     * @param NET_TAX_AMT the NET_TAX_AMT to set
     */
    public void setNET_TAX_AMT(Double NET_TAX_AMT) {
        this.NET_TAX_AMT = NET_TAX_AMT;
    }

    /**
     * @return the TAX_AMT_ROUND_TO
     */
    public Double getTAX_AMT_ROUND_TO() {
        return TAX_AMT_ROUND_TO;
    }

    /**
     * @param TAX_AMT_ROUND_TO the TAX_AMT_ROUND_TO to set
     */
    public void setTAX_AMT_ROUND_TO(Double TAX_AMT_ROUND_TO) {
        this.TAX_AMT_ROUND_TO = TAX_AMT_ROUND_TO;
    }

    /**
     * @return the VCH_AGE_MONTHS
     */
    public Integer getVCH_AGE_MONTHS() {
        return VCH_AGE_MONTHS;
    }

    /**
     * @param VCH_AGE_MONTHS the VCH_AGE_MONTHS to set
     */
    public void setVCH_AGE_MONTHS(Integer VCH_AGE_MONTHS) {
        this.VCH_AGE_MONTHS = VCH_AGE_MONTHS;
    }

    /**
     * @return the TAX_MONTH_DURATION
     */
    public Integer getTAX_MONTH_DURATION() {
        return TAX_MONTH_DURATION;
    }

    /**
     * @param TAX_MONTH_DURATION the TAX_MONTH_DURATION to set
     */
    public void setTAX_MONTH_DURATION(Integer TAX_MONTH_DURATION) {
        this.TAX_MONTH_DURATION = TAX_MONTH_DURATION;
    }

    /**
     * @return the PENALTY_FROM_FIRST_TAX_DUE_DATE
     */
    public boolean isPENALTY_FROM_FIRST_TAX_DUE_DATE() {
        return PENALTY_FROM_FIRST_TAX_DUE_DATE;
    }

    /**
     * @param PENALTY_FROM_FIRST_TAX_DUE_DATE the
     * PENALTY_FROM_FIRST_TAX_DUE_DATE to set
     */
    public void setPENALTY_FROM_FIRST_TAX_DUE_DATE(boolean PENALTY_FROM_FIRST_TAX_DUE_DATE) {
        this.PENALTY_FROM_FIRST_TAX_DUE_DATE = PENALTY_FROM_FIRST_TAX_DUE_DATE;
    }

    /**
     * @return the COMPLETE_QTR
     */
    public boolean isCOMPLETE_QTR() {
        return COMPLETE_QTR;
    }

    /**
     * @param COMPLETE_QTR the COMPLETE_QTR to set
     */
    public void setCOMPLETE_QTR(boolean COMPLETE_QTR) {
        this.COMPLETE_QTR = COMPLETE_QTR;
    }

    /**
     * @return the TAX_PERIOD_SR_NO
     */
    public Integer getTAX_PERIOD_SR_NO() {
        return TAX_PERIOD_SR_NO;
    }

    /**
     * @param TAX_PERIOD_SR_NO the TAX_PERIOD_SR_NO to set
     */
    public void setTAX_PERIOD_SR_NO(Integer TAX_PERIOD_SR_NO) {
        this.TAX_PERIOD_SR_NO = TAX_PERIOD_SR_NO;
    }

    /**
     * @return the HOLIDAYS
     */
    public Integer getHOLIDAYS() {
        return HOLIDAYS;
    }

    /**
     * @param HOLIDAYS the HOLIDAYS to set
     */
    public void setHOLIDAYS(Integer HOLIDAYS) {
        this.HOLIDAYS = HOLIDAYS;
    }

    public Integer getPURCHASE_DT_VCH_AGE() {
        return PURCHASE_DT_VCH_AGE;
    }

    public void setPURCHASE_DT_VCH_AGE(Integer PURCHASE_DT_VCH_AGE) {
        this.PURCHASE_DT_VCH_AGE = PURCHASE_DT_VCH_AGE;
    }

    /**
     * @return the TAX_BREAKUP_FROM_DATE
     */
    public String getTAX_BREAKUP_FROM_DATE() {
        return TAX_BREAKUP_FROM_DATE;
    }

    /**
     * @param TAX_BREAKUP_FROM_DATE the TAX_BREAKUP_FROM_DATE to set
     */
    public void setTAX_BREAKUP_FROM_DATE(String TAX_BREAKUP_FROM_DATE) {
        this.TAX_BREAKUP_FROM_DATE = TAX_BREAKUP_FROM_DATE;
    }

    /**
     * @return the TAX_BREAKUP_UPTO_DATE
     */
    public String getTAX_BREAKUP_UPTO_DATE() {
        return TAX_BREAKUP_UPTO_DATE;
    }

    /**
     * @param TAX_BREAKUP_UPTO_DATE the TAX_BREAKUP_UPTO_DATE to set
     */
    public void setTAX_BREAKUP_UPTO_DATE(String TAX_BREAKUP_UPTO_DATE) {
        this.TAX_BREAKUP_UPTO_DATE = TAX_BREAKUP_UPTO_DATE;
    }
}
