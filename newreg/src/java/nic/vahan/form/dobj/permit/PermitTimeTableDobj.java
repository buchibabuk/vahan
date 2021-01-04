/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;

/**
 *
 * @author WS39
 */
public class PermitTimeTableDobj implements Serializable, Cloneable {

    private String trip_no;
    private String route_fr_time;
    private String route_to_time;
    private String route_via;
    private String state_cd;
    private int off_cd;
    private String stoppage;
    private String route_cd;
    private int day;
    private boolean disableDay;
    private boolean disableFromTime;
    private boolean disableToTime;

    public String getRoute_via() {
        return route_via;
    }

    public void setRoute_via(String route_via) {
        this.route_via = route_via;
    }

    public String getTrip_no() {
        return trip_no;
    }

    public void setTrip_no(String trip_no) {
        this.trip_no = trip_no;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
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

    public String getStoppage() {
        return stoppage;
    }

    public void setStoppage(String stoppage) {
        this.stoppage = stoppage;
    }

    public String getRoute_cd() {
        return route_cd;
    }

    public void setRoute_cd(String route_cd) {
        this.route_cd = route_cd;
    }

    public String getRoute_fr_time() {
        return route_fr_time;
    }

    public void setRoute_fr_time(String route_fr_time) {
        this.route_fr_time = route_fr_time;
    }

    public String getRoute_to_time() {
        return route_to_time;
    }

    public void setRoute_to_time(String route_to_time) {
        this.route_to_time = route_to_time;
    }

    public boolean isDisableDay() {
        return disableDay;
    }

    public void setDisableDay(boolean disableDay) {
        this.disableDay = disableDay;
    }

    public boolean isDisableFromTime() {
        return disableFromTime;
    }

    public void setDisableFromTime(boolean disableFromTime) {
        this.disableFromTime = disableFromTime;
    }

    public boolean isDisableToTime() {
        return disableToTime;
    }

    public void setDisableToTime(boolean disableToTime) {
        this.disableToTime = disableToTime;
    }
}
