/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.util.Date;


public class HolidayMasterDobj {

    private String holiday_reason = "";
    private String state_cd = "";
    private int off_cd = 0;
    private String holiday_date;
    private String op_date;

    public String getHoliday_reason() {
        return holiday_reason;
    }

    public void setHoliday_reason(String holiday_reason) {
        this.holiday_reason = holiday_reason;
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

    public String getHoliday_date() {
        return holiday_date;
    }

    public void setHoliday_date(String holiday_date) {
        this.holiday_date = holiday_date;
    }

    public String getOp_date() {
        return op_date;
    }

    public void setOp_date(String op_date) {
        this.op_date = op_date;
    }

    
}
