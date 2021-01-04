/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

public class WalletDobj implements Serializable {

    private int walletPurCode;
    private long walletAmount;
    private long walletPenalty;
    private long walletExempted;
    private long walletSurcharge;
    private long walletRebate;
    private long walletInterest;
    private String dealerCode;
    private long walletUsedAmount;
    private long walletUsedPenalty;
    private long walletUsedExempted;
    private long walletUsedSurcharge;
    private long walletUsedRebate;
    private long walletUsedInterest;
    private String purposeDescr;
    private String stateCd;
    private int offCd;
    private long walletTax1;
    private long walletTax2;
    private long walletUsedTax1;
    private long walletUsedTax2;

    /**
     * @return the walletPurCode
     */
    public int getWalletPurCode() {
        return walletPurCode;
    }

    /**
     * @param walletPurCode the walletPurCode to set
     */
    public void setWalletPurCode(int walletPurCode) {
        this.walletPurCode = walletPurCode;
    }

    /**
     * @return the walletAmount
     */
    public long getWalletAmount() {
        return walletAmount;
    }

    /**
     * @param walletAmount the walletAmount to set
     */
    public void setWalletAmount(long walletAmount) {
        this.walletAmount = walletAmount;
    }

    /**
     * @return the walletPenalty
     */
    public long getWalletPenalty() {
        return walletPenalty;
    }

    /**
     * @param walletPenalty the walletPenalty to set
     */
    public void setWalletPenalty(long walletPenalty) {
        this.walletPenalty = walletPenalty;
    }

    /**
     * @return the walletExempted
     */
    public long getWalletExempted() {
        return walletExempted;
    }

    /**
     * @param walletExempted the walletExempted to set
     */
    public void setWalletExempted(long walletExempted) {
        this.walletExempted = walletExempted;
    }

    /**
     * @return the walletSurcharge
     */
    public long getWalletSurcharge() {
        return walletSurcharge;
    }

    /**
     * @param walletSurcharge the walletSurcharge to set
     */
    public void setWalletSurcharge(long walletSurcharge) {
        this.walletSurcharge = walletSurcharge;
    }

    /**
     * @return the walletRebate
     */
    public long getWalletRebate() {
        return walletRebate;
    }

    /**
     * @param walletRebate the walletRebate to set
     */
    public void setWalletRebate(long walletRebate) {
        this.walletRebate = walletRebate;
    }

    /**
     * @return the walletInterest
     */
    public long getWalletInterest() {
        return walletInterest;
    }

    /**
     * @param walletInterest the walletInterest to set
     */
    public void setWalletInterest(long walletInterest) {
        this.walletInterest = walletInterest;
    }

    @Override
    public int hashCode() {
        String hash = this.getDealerCode() + ":" + this.getWalletPurCode() + ":" + this.getStateCd() + ":" + this.getOffCd();
        return hash.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof WalletDobj)) {
            return false;
        }
        WalletDobj ob = (WalletDobj) obj;

        if (ob.getDealerCode() == null || ob.getStateCd() == null || ob.getWalletPurCode() == 0 || ob.getOffCd() == 0) {
            return false;
        }

        if (ob.getDealerCode() != null && ob.getWalletPurCode() != 0 && ob.getStateCd() != null && ob.getOffCd() != 0 && !(ob.dealerCode.concat(String.valueOf(ob.walletPurCode)).concat(ob.stateCd).concat(String.valueOf(ob.offCd))).equals((this.dealerCode.concat(String.valueOf(this.walletPurCode)).concat(this.stateCd).concat(String.valueOf(this.offCd))))) {
            return false;
        }
        return true;
    }

    /**
     * @return the dealerCode
     */
    public String getDealerCode() {
        return dealerCode;
    }

    /**
     * @param dealerCode the dealerCode to set
     */
    public void setDealerCode(String dealerCode) {
        this.dealerCode = dealerCode;
    }

    /**
     * @return the walletUsedAmount
     */
    public long getWalletUsedAmount() {
        return walletUsedAmount;
    }

    /**
     * @param walletUsedAmount the walletUsedAmount to set
     */
    public void setWalletUsedAmount(long walletUsedAmount) {
        this.walletUsedAmount = walletUsedAmount;
    }

    /**
     * @return the walletUsedPenalty
     */
    public long getWalletUsedPenalty() {
        return walletUsedPenalty;
    }

    /**
     * @param walletUsedPenalty the walletUsedPenalty to set
     */
    public void setWalletUsedPenalty(long walletUsedPenalty) {
        this.walletUsedPenalty = walletUsedPenalty;
    }

    /**
     * @return the walletUsedExempted
     */
    public long getWalletUsedExempted() {
        return walletUsedExempted;
    }

    /**
     * @param walletUsedExempted the walletUsedExempted to set
     */
    public void setWalletUsedExempted(long walletUsedExempted) {
        this.walletUsedExempted = walletUsedExempted;
    }

    /**
     * @return the walletUsedSurcharge
     */
    public long getWalletUsedSurcharge() {
        return walletUsedSurcharge;
    }

    /**
     * @param walletUsedSurcharge the walletUsedSurcharge to set
     */
    public void setWalletUsedSurcharge(long walletUsedSurcharge) {
        this.walletUsedSurcharge = walletUsedSurcharge;
    }

    /**
     * @return the walletUsedRebate
     */
    public long getWalletUsedRebate() {
        return walletUsedRebate;
    }

    /**
     * @param walletUsedRebate the walletUsedRebate to set
     */
    public void setWalletUsedRebate(long walletUsedRebate) {
        this.walletUsedRebate = walletUsedRebate;
    }

    /**
     * @return the walletUsedInterest
     */
    public long getWalletUsedInterest() {
        return walletUsedInterest;
    }

    /**
     * @param walletUsedInterest the walletUsedInterest to set
     */
    public void setWalletUsedInterest(long walletUsedInterest) {
        this.walletUsedInterest = walletUsedInterest;
    }

    /**
     * @return the purposeDescr
     */
    public String getPurposeDescr() {
        return purposeDescr;
    }

    /**
     * @param purposeDescr the purposeDescr to set
     */
    public void setPurposeDescr(String purposeDescr) {
        this.purposeDescr = purposeDescr;
    }

    /**
     * @return the stateCd
     */
    public String getStateCd() {
        return stateCd;
    }

    /**
     * @param stateCd the stateCd to set
     */
    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    /**
     * @return the offCd
     */
    public int getOffCd() {
        return offCd;
    }

    /**
     * @param offCd the offCd to set
     */
    public void setOffCd(int offCd) {
        this.offCd = offCd;
    }

    /**
     * @return the walletTax1
     */
    public long getWalletTax1() {
        return walletTax1;
    }

    /**
     * @param walletTax1 the walletTax1 to set
     */
    public void setWalletTax1(long walletTax1) {
        this.walletTax1 = walletTax1;
    }

    /**
     * @return the walletTax2
     */
    public long getWalletTax2() {
        return walletTax2;
    }

    /**
     * @param walletTax2 the walletTax2 to set
     */
    public void setWalletTax2(long walletTax2) {
        this.walletTax2 = walletTax2;
    }

    /**
     * @return the walletUsedTax1
     */
    public long getWalletUsedTax1() {
        return walletUsedTax1;
    }

    /**
     * @param walletUsedTax1 the walletUsedTax1 to set
     */
    public void setWalletUsedTax1(long walletUsedTax1) {
        this.walletUsedTax1 = walletUsedTax1;
    }

    /**
     * @return the walletUsedTax2
     */
    public long getWalletUsedTax2() {
        return walletUsedTax2;
    }

    /**
     * @param walletUsedTax2 the walletUsedTax2 to set
     */
    public void setWalletUsedTax2(long walletUsedTax2) {
        this.walletUsedTax2 = walletUsedTax2;
    }
}
