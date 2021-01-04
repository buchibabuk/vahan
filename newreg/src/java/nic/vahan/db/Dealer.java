/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db;

import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author tranC092
 */
@FacesConverter("dealerConverter")
public class Dealer implements Converter {

    private String dealer_cd;
    private String state_cd;
    private int off_cd;
    private String dealer_name;
    private String dealer_regn_no;
    private String d_add1;
    private String d_add2;
    private int d_district;
    private int d_pincode;
    private String d_state;
    private String valid_upto;
    private String entered_by;
    private String entered_on;
    private boolean blockUnBlockStatus;
    private String reason;

    /**
     * @return the dealer_cd
     */
    public String getDealer_cd() {
        return dealer_cd;
    }

    /**
     * @param dealer_cd the dealer_cd to set
     */
    public void setDealer_cd(String dealer_cd) {
        this.dealer_cd = dealer_cd;
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
     * @return the dealer_name
     */
    public String getDealer_name() {
        return dealer_name;
    }

    /**
     * @param dealer_name the dealer_name to set
     */
    public void setDealer_name(String dealer_name) {
        this.dealer_name = dealer_name;
    }

    /**
     * @return the dealer_regn_no
     */
    public String getDealer_regn_no() {
        return dealer_regn_no;
    }

    /**
     * @param dealer_regn_no the dealer_regn_no to set
     */
    public void setDealer_regn_no(String dealer_regn_no) {
        this.dealer_regn_no = dealer_regn_no;
    }

    /**
     * @return the d_add1
     */
    public String getD_add1() {
        return d_add1;
    }

    /**
     * @param d_add1 the d_add1 to set
     */
    public void setD_add1(String d_add1) {
        this.d_add1 = d_add1;
    }

    /**
     * @return the d_add2
     */
    public String getD_add2() {
        return d_add2;
    }

    /**
     * @param d_add2 the d_add2 to set
     */
    public void setD_add2(String d_add2) {
        this.d_add2 = d_add2;
    }

    /**
     * @return the d_district
     */
    public int getD_district() {
        return d_district;
    }

    /**
     * @param d_district the d_district to set
     */
    public void setD_district(int d_district) {
        this.d_district = d_district;
    }

    /**
     * @return the d_pincode
     */
    public int getD_pincode() {
        return d_pincode;
    }

    /**
     * @param d_pincode the d_pincode to set
     */
    public void setD_pincode(int d_pincode) {
        this.d_pincode = d_pincode;
    }

    /**
     * @return the d_state
     */
    public String getD_state() {
        return d_state;
    }

    /**
     * @param d_state the d_state to set
     */
    public void setD_state(String d_state) {
        this.d_state = d_state;
    }

    /**
     * @return the valid_upto
     */
    public String getValid_upto() {
        return valid_upto;
    }

    /**
     * @param valid_upto the valid_upto to set
     */
    public void setValid_upto(String valid_upto) {
        this.valid_upto = valid_upto;
    }

    /**
     * @return the entered_by
     */
    public String getEntered_by() {
        return entered_by;
    }

    /**
     * @param entered_by the entered_by to set
     */
    public void setEntered_by(String entered_by) {
        this.entered_by = entered_by;
    }

    /**
     * @return the entered_on
     */
    public String getEntered_on() {
        return entered_on;
    }

    /**
     * @param entered_on the entered_on to set
     */
    public void setEntered_on(String entered_on) {
        this.entered_on = entered_on;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object obj) {
        return obj.toString();
        // return (obj != null) ? obj.toString().toUpperCase() : "";
    }

    private Dealer getDealerFromList(String dealerCd, String stateCd, int offCd) {

        Dealer retObj = null;
//         List<Dealer> listDealers = MasterTableFiller.masterTables.VM_DEALER_LIST;
//        for (Dealer obj : listDealers) {
//            if (obj.getDealer_cd().equals(dealerCd) && obj.getState_cd().equals(stateCd) && obj.getOff_cd() == offCd) {
//                retObj = obj;
//                break;
//            }
//        }
        return retObj;
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {

        if (submittedValue == null || submittedValue.isEmpty()) {
            return null;
        }
        String[] parm = submittedValue.split(":");
        Dealer obj = getDealerFromList(parm[0], parm[1], Integer.parseInt(parm[2]));
        return obj;
    }

    @Override
    public String toString() {
        return dealer_cd + ":" + state_cd + ":" + off_cd;
    }

    @Override
    public int hashCode() {
        String str = dealer_cd + ":" + state_cd + ":" + off_cd;
        return str.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Dealer)) {
            return false;
        }
        Dealer ob = (Dealer) obj;
        if (!ob.dealer_cd.equals(dealer_cd) && !ob.state_cd.equals(state_cd) && ob.off_cd != off_cd) {
            return false;
        }
        return true;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return the blockUnBlockStatus
     */
    public boolean isBlockUnBlockStatus() {
        return blockUnBlockStatus;
    }

    /**
     * @param blockUnBlockStatus the blockUnBlockStatus to set
     */
    public void setBlockUnBlockStatus(boolean blockUnBlockStatus) {
        this.blockUnBlockStatus = blockUnBlockStatus;
    }
}
