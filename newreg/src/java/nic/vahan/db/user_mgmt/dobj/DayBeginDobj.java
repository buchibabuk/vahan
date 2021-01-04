/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.dobj;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Administrator
 */
public class DayBeginDobj implements Serializable {

    private String lastWorkDay;
    private String StateCd;
    private int OffCd;
    private String currentDt;
    private long noofHoliday;
    private boolean cashCounterClose = true;
    private String cashCounterOpenDt;
    private String previousCashCounterOpenDt;
    private String previousCashCounterCloseDt;
    private String cashCounterOpenDateTime;
    private String cashCounterCloseDateTime;
    private Boolean closeAllCashCounter;
    private String user_cd;

    {
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        Date CurrDate = new Date();
        currentDt = format.format(CurrDate);
    }

    /**
     * @return the lastWorkDay
     */
    public String getLastWorkDay() {
        return lastWorkDay;
    }

    /**
     * @param lastWorkDay the lastWorkDay to set
     */
    public void setLastWorkDay(String lastWorkDay) {
        this.lastWorkDay = lastWorkDay;
    }

    /**
     * @return the StateCd
     */
    public String getStateCd() {
        return StateCd;
    }

    /**
     * @param StateCd the StateCd to set
     */
    public void setStateCd(String StateCd) {
        this.StateCd = StateCd;
    }

    /**
     * @return the OffCd
     */
    public int getOffCd() {
        return OffCd;
    }

    /**
     * @param OffCd the OffCd to set
     */
    public void setOffCd(int OffCd) {
        this.OffCd = OffCd;
    }

    public String getCurrentDt() {
        return currentDt;
    }

    public void setCurrentDt(String currentDt) {
        this.currentDt = currentDt;
    }

    public long getNoofHoliday() {
        return noofHoliday;
    }

    public void setNoofHoliday(long noofHoliday) {
        this.noofHoliday = noofHoliday;
    }

    public boolean isCashCounterClose() {
        return cashCounterClose;
    }

    public void setCashCounterClose(boolean cashCounterClose) {
        this.cashCounterClose = cashCounterClose;
    }

    public String getCashCounterOpenDt() {
        return cashCounterOpenDt;
    }

    public void setCashCounterOpenDt(String cashCounterOpenDt) {
        this.cashCounterOpenDt = cashCounterOpenDt;
    }

    public String getPreviousCashCounterOpenDt() {
        return previousCashCounterOpenDt;
    }

    public void setPreviousCashCounterOpenDt(String previousCashCounterOpenDt) {
        this.previousCashCounterOpenDt = previousCashCounterOpenDt;
    }

    public String getPreviousCashCounterCloseDt() {
        return previousCashCounterCloseDt;
    }

    public void setPreviousCashCounterCloseDt(String previousCashCounterCloseDt) {
        this.previousCashCounterCloseDt = previousCashCounterCloseDt;
    }

    public String getCashCounterOpenDateTime() {
        return cashCounterOpenDateTime;
    }

    public void setCashCounterOpenDateTime(String cashCounterOpenDateTime) {
        this.cashCounterOpenDateTime = cashCounterOpenDateTime;
    }

    public String getCashCounterCloseDateTime() {
        return cashCounterCloseDateTime;
    }

    public void setCashCounterCloseDateTime(String cashCounterCloseDateTime) {
        this.cashCounterCloseDateTime = cashCounterCloseDateTime;
    }

    public Boolean getCloseAllCashCounter() {
        return closeAllCashCounter;
    }

    public void setCloseAllCashCounter(Boolean closeAllCashCounter) {
        this.closeAllCashCounter = closeAllCashCounter;
    }

    public String getUser_cd() {
        return user_cd;
    }

    public void setUser_cd(String user_cd) {
        this.user_cd = user_cd;
    }
    
}
