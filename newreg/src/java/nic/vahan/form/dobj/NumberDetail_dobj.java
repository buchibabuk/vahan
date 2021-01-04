/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author AMBRISH
 */
public class NumberDetail_dobj implements Serializable {

    // this is a pojo class for va_random_regn_no table.
    private String appl_no;
    private String regn_no_alloted;
    private String allot_dt;
    private String status;
    private String remark;
    private int off_cd;
    private String state_cd;
    private String series_to_be_allocated;
    private int series_cd_to_be_allocated;

    /**
     * @return the appl_no
     */
    public String getAppl_no() {
        return appl_no;
    }

    /**
     * @param appl_no the appl_no to set
     */
    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    /**
     * @return the regn_no_alloted
     */
    public String getRegn_no_alloted() {
        return regn_no_alloted;
    }

    /**
     * @param regn_no_alloted the regn_no_alloted to set
     */
    public void setRegn_no_alloted(String regn_no_alloted) {
        this.regn_no_alloted = regn_no_alloted;
    }

    /**
     * @return the allot_dt
     */
    public String getAllot_dt() {
        return allot_dt;
    }

    /**
     * @param allot_dt the allot_dt to set
     */
    public void setAllot_dt(String allot_dt) {
        this.allot_dt = allot_dt;
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
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark the remark to set
     */
    public void setRemark(String remark) {
        this.remark = remark;
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
     * @return the series_to_be_allocated
     */
    public String getSeries_to_be_allocated() {
        return series_to_be_allocated;
    }

    /**
     * @param series_to_be_allocated the series_to_be_allocated to set
     */
    public void setSeries_to_be_allocated(String series_to_be_allocated) {
        this.series_to_be_allocated = series_to_be_allocated;
    }

    /**
     * @return the series_cd_to_be_allocated
     */
    public int getSeries_cd_to_be_allocated() {
        return series_cd_to_be_allocated;
    }

    /**
     * @param series_cd_to_be_allocated the series_cd_to_be_allocated to set
     */
    public void setSeries_cd_to_be_allocated(int series_cd_to_be_allocated) {
        this.series_cd_to_be_allocated = series_cd_to_be_allocated;
    }

    /*
     * Comparator implementation to Sort Order object based on Amount
     */
    public static class OrderBySeriesPrefix implements Comparator<NumberDetail_dobj> {

        @Override
        public int compare(NumberDetail_dobj o1, NumberDetail_dobj o2) {

            return o1.series_to_be_allocated.compareTo(o2.series_to_be_allocated);

        }
    }
}
