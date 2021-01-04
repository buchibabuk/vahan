/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

/**
 *
 * @author NICSI
 */
public class Advance_regn_dobj implements Serializable {

    private int series_no;
    private String prefix_series;

    public int getSeries_no() {
        return series_no;
    }

    public void setSeries_no(int series_no) {
        this.series_no = series_no;
    }

    public String getPrefix_series() {
        return prefix_series;
    }

    public void setPrefix_series(String prefix_series) {
        this.prefix_series = prefix_series;
    }
}
