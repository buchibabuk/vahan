/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services.clients.dobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Anu
 */
public class ChallanHistoryDobj implements Serializable {

    private String challan_no;
    private String challan_place;
    private String date_time;
    private int challan_source;
    private String state_code;
    private String rto_name;
    private String vehicle_no;
    private String accused_name;
    private String accused_father_name;
    private String mobile_number;
    private String accused_address;
    private int amount;
    private String challan_status;
    private List<OffenceDetailsDobj> offence_details = new ArrayList<>();

    /**
     * @return the challan_no
     */
    public String getChallan_no() {
        return challan_no;
    }

    /**
     * @param challan_no the challan_no to set
     */
    public void setChallan_no(String challan_no) {
        this.challan_no = challan_no;
    }

    /**
     * @return the challan_place
     */
    public String getChallan_place() {
        return challan_place;
    }

    /**
     * @param challan_place the challan_place to set
     */
    public void setChallan_place(String challan_place) {
        this.challan_place = challan_place;
    }

    /**
     * @return the date_time
     */
    public String getDate_time() {
        return date_time;
    }

    /**
     * @param date_time the date_time to set
     */
    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    /**
     * @return the state_code
     */
    public String getState_code() {
        return state_code;
    }

    /**
     * @param state_code the state_code to set
     */
    public void setState_code(String state_code) {
        this.state_code = state_code;
    }

    /**
     * @return the rto_name
     */
    public String getRto_name() {
        return rto_name;
    }

    /**
     * @param rto_name the rto_name to set
     */
    public void setRto_name(String rto_name) {
        this.rto_name = rto_name;
    }

    /**
     * @return the vehicle_no
     */
    public String getVehicle_no() {
        return vehicle_no;
    }

    /**
     * @param vehicle_no the vehicle_no to set
     */
    public void setVehicle_no(String vehicle_no) {
        this.vehicle_no = vehicle_no;
    }

    /**
     * @return the accused_name
     */
    public String getAccused_name() {
        return accused_name;
    }

    /**
     * @param accused_name the accused_name to set
     */
    public void setAccused_name(String accused_name) {
        this.accused_name = accused_name;
    }

    /**
     * @return the accused_father_name
     */
    public String getAccused_father_name() {
        return accused_father_name;
    }

    /**
     * @param accused_father_name the accused_father_name to set
     */
    public void setAccused_father_name(String accused_father_name) {
        this.accused_father_name = accused_father_name;
    }

    /**
     * @return the accused_address
     */
    public String getAccused_address() {
        return accused_address;
    }

    /**
     * @param accused_address the accused_address to set
     */
    public void setAccused_address(String accused_address) {
        this.accused_address = accused_address;
    }

    /**
     * @return the challan_status
     */
    public String getChallan_status() {
        return challan_status;
    }

    /**
     * @param challan_status the challan_status to set
     */
    public void setChallan_status(String challan_status) {
        this.challan_status = challan_status;
    }

    /**
     * @return the offence_details
     */
    public List<OffenceDetailsDobj> getOffence_details() {
        return offence_details;
    }

    /**
     * @param offence_details the offence_details to set
     */
    public void setOffence_details(List<OffenceDetailsDobj> offence_details) {
        this.offence_details = offence_details;
    }

    /**
     * @return the challan_source
     */
    public int getChallan_source() {
        return challan_source;
    }

    /**
     * @param challan_source the challan_source to set
     */
    public void setChallan_source(int challan_source) {
        this.challan_source = challan_source;
    }

    /**
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * @return the mobile_number
     */
    public String getMobile_number() {
        return mobile_number;
    }

    /**
     * @param mobile_number the mobile_number to set
     */
    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }
}
