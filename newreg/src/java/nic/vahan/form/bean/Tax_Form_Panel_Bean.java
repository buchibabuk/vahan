/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import nic.vahan.form.dobj.DOTaxDetail;

/**
 *
 * @author tranC094
 */
public class Tax_Form_Panel_Bean implements Serializable {

    private List<DOTaxDetail> taxDescriptionList;
    private ArrayList listTaxModes = new ArrayList();
    private long totalPaybaleTax = 0;
    private long totalPaybalePenalty = 0;
    private long totalPaybaleSurcharge = 0;
    private long totalPaybaleRebate = 0;
    private long totalPaybaleInterest = 0;
    private long totalPayableAmount = 0;
    private String finalTaxFrom = null;
    private String finalTaxUpto = null;
    private long finalTaxAmount = 0;
    private String taxMode = null;
    private int pur_cd = 0;
    private String taxPurcdDesc = null;

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
        finalTaxAmount = 0;
        finalTaxFrom = "";
        finalTaxUpto = "";

    }

    public void updateTaxBean() {
        resetAll();
        int i = 0;
        long finalTaxAmount = 0;
        for (DOTaxDetail dobj : getTaxDescriptionList()) {
            if (i == 0) {
                setFinalTaxFrom(dobj.getTAX_FROM());
            }

            if (i == getTaxDescriptionList().size() - 1) {
                setFinalTaxUpto(dobj.getTAX_UPTO());
            }
            i++;
            setTotalPaybaleInterest((long)(getTotalPaybaleInterest() + dobj.getINTEREST()));
            setTotalPaybalePenalty((long)(getTotalPaybalePenalty() + dobj.getPENALTY()));
            setTotalPaybaleRebate((long)(getTotalPaybaleRebate() + dobj.getREBATE()));
            setTotalPaybaleSurcharge((long)(getTotalPaybaleSurcharge() + dobj.getSURCHARGE()));
            setTotalPaybaleTax((long)(getTotalPaybaleTax() + dobj.getAMOUNT()));


        }
        finalTaxAmount = finalTaxAmount + getTotalPaybaleTax() + getTotalPaybaleSurcharge() + getTotalPaybaleInterest()
                - getTotalPaybaleRebate() + getTotalPaybalePenalty();

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
    public ArrayList getListTaxModes() {
        return listTaxModes;
    }

    /**
     * @param listTaxModes the listTaxModes to set
     */
    public void setListTaxModes(ArrayList listTaxModes) {
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
}
