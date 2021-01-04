package nic.vahan.form.dobj;

import java.util.Comparator;

/**
 *
 * @author AMBRISH
 */
public class FancyAuctionDobj implements Comparable<FancyAuctionDobj> {

    private String recp_no;
    private String recp_dt;
    private String regn_appl_no;
    private String regn_no;
    private String app_auth;
    private String dt_of_app;
    private String file_no;
    private String owner_name;
    private String c_add1;
    private String c_add2;
    private int c_village;
    private int c_taluk;
    private int c_district;
    private String c_pincode;
    private int reserve_amt;
    private int draft_amt;
    private int auction_amt;
    private int total_amt;
    private int offer_amt;
    private int bal_amt;
    private String auction_dt;
    private String auction_amt_recp_no;
    private String aution_amt_recp_dt;
    private String attendance_at_auction;
    private String status;
    private String state_cd;
    private int rto_cd;

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
     * @return the recp_dt
     */
    public String getRecp_dt() {
        return recp_dt;
    }

    /**
     * @param recp_dt the recp_dt to set
     */
    public void setRecp_dt(String recp_dt) {
        this.recp_dt = recp_dt;
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
     * @return the app_auth
     */
    public String getApp_auth() {
        return app_auth;
    }

    /**
     * @param app_auth the app_auth to set
     */
    public void setApp_auth(String app_auth) {
        this.app_auth = app_auth;
    }

    /**
     * @return the dt_of_app
     */
    public String getDt_of_app() {
        return dt_of_app;
    }

    /**
     * @param dt_of_app the dt_of_app to set
     */
    public void setDt_of_app(String dt_of_app) {
        this.dt_of_app = dt_of_app;
    }

    /**
     * @return the file_no
     */
    public String getFile_no() {
        return file_no;
    }

    /**
     * @param file_no the file_no to set
     */
    public void setFile_no(String file_no) {
        this.file_no = file_no;
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
     * @return the c_village
     */
    public int getC_village() {
        return c_village;
    }

    /**
     * @param c_village the c_village to set
     */
    public void setC_village(int c_village) {
        this.c_village = c_village;
    }

    /**
     * @return the c_taluk
     */
    public int getC_taluk() {
        return c_taluk;
    }

    /**
     * @param c_taluk the c_taluk to set
     */
    public void setC_taluk(int c_taluk) {
        this.c_taluk = c_taluk;
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
     * @return the c_pincode
     */
    public String getC_pincode() {
        return c_pincode;
    }

    /**
     * @param c_pincode the c_pincode to set
     */
    public void setC_pincode(String c_pincode) {
        this.c_pincode = c_pincode;
    }

    /**
     * @return the reserve_amt
     */
    public int getReserve_amt() {
        return reserve_amt;
    }

    /**
     * @param reserve_amt the reserve_amt to set
     */
    public void setReserve_amt(int reserve_amt) {
        this.reserve_amt = reserve_amt;
    }

    /**
     * @return the draft_amt
     */
    public int getDraft_amt() {
        return draft_amt;
    }

    /**
     * @param draft_amt the draft_amt to set
     */
    public void setDraft_amt(int draft_amt) {
        this.draft_amt = draft_amt;
    }

    /**
     * @return the auction_amt
     */
    public int getAuction_amt() {
        return auction_amt;
    }

    /**
     * @param auction_amt the auction_amt to set
     */
    public void setAuction_amt(int auction_amt) {
        this.auction_amt = auction_amt;
    }

    /**
     * @return the total_amt
     */
    public int getTotal_amt() {
        return total_amt;
    }

    /**
     * @param total_amt the total_amt to set
     */
    public void setTotal_amt(int total_amt) {
        this.total_amt = total_amt;
    }

    /**
     * @return the auction_dt
     */
    public String getAuction_dt() {
        return auction_dt;
    }

    /**
     * @param auction_dt the auction_dt to set
     */
    public void setAuction_dt(String auction_dt) {
        this.auction_dt = auction_dt;
    }

    /**
     * @return the auction_amt_recp_no
     */
    public String getAuction_amt_recp_no() {
        return auction_amt_recp_no;
    }

    /**
     * @param auction_amt_recp_no the auction_amt_recp_no to set
     */
    public void setAuction_amt_recp_no(String auction_amt_recp_no) {
        this.auction_amt_recp_no = auction_amt_recp_no;
    }

    /**
     * @return the aution_amt_recp_dt
     */
    public String getAution_amt_recp_dt() {
        return aution_amt_recp_dt;
    }

    /**
     * @param aution_amt_recp_dt the aution_amt_recp_dt to set
     */
    public void setAution_amt_recp_dt(String aution_amt_recp_dt) {
        this.aution_amt_recp_dt = aution_amt_recp_dt;
    }

    /**
     * @return the attendance_at_auction
     */
    public String getAttendance_at_auction() {
        return attendance_at_auction;
    }

    /**
     * @param attendance_at_auction the attendance_at_auction to set
     */
    public void setAttendance_at_auction(String attendance_at_auction) {
        this.attendance_at_auction = attendance_at_auction;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

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
     * @return the rto_cd
     */
    public int getRto_cd() {
        return rto_cd;
    }

    /**
     * @param rto_cd the rto_cd to set
     */
    public void setRto_cd(int rto_cd) {
        this.rto_cd = rto_cd;
    }

    /**
     * @return the bal_amt
     */
    public int getBal_amt() {
        return bal_amt;
    }

    /**
     * @param bal_amt the bal_amt to set
     */
    public void setBal_amt(int bal_amt) {
        this.bal_amt = bal_amt;
    }

    /**
     * @return the offer_amt
     */
    public int getOffer_amt() {
        return offer_amt;
    }

    /**
     * @param offer_amt the offer_amt to set
     */
    public void setOffer_amt(int offer_amt) {
        this.offer_amt = offer_amt;
    }

    @Override
    public int compareTo(FancyAuctionDobj compareFruit) {
        int compareTotal = ((FancyAuctionDobj) compareFruit).getTotal_amt();
        return this.total_amt > compareTotal ? 1 : 0;
    }

    @Override
    public boolean equals(Object obj) {
        FancyAuctionDobj dobj = (FancyAuctionDobj) obj;
        return dobj.total_amt == getTotal_amt();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + this.total_amt;
        return hash;
    }
    /*
     * Comparator implementation to Sort Order object based on Amount
     */

    public static class OrderByTotalAmount implements Comparator<FancyAuctionDobj> {

        @Override
        public int compare(FancyAuctionDobj o1, FancyAuctionDobj o2) {
            return o1.total_amt > o2.total_amt ? -1 : (o1.total_amt < o2.total_amt ? 1 : 0);
        }
    }
    public static final Comparator<FancyAuctionDobj> FancyNumberTotalComparator = new Comparator<FancyAuctionDobj>() {
        @Override
        public int compare(FancyAuctionDobj dobj1, FancyAuctionDobj dobj2) {
            return dobj1.compareTo(dobj2);
        }
    };

    @Override
    public String toString() {
        StringBuilder ab = new StringBuilder();
        ab.append("[Regn No]");
        ab.append(this.regn_no);

        ab.append("[Regn Application No]");
        ab.append(this.regn_appl_no);

        ab.append("[Total Amount]");
        ab.append(this.total_amt);

        ab.append("[Status]");
        ab.append(this.status);

        return ab.toString();
    }
}
