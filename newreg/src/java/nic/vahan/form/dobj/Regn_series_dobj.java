/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

/**
 *
 * @author AMBRISH
 */
public class Regn_series_dobj implements Cloneable {

    private int series_id;
    private String criteria_formula;
    private String criteria_formuladesc;
    private String criteria_formula_descr;
    private String prefix_series;
    private int lower_range_no = 1;
    private int upper_range_no = 9999;
    private int running_no = 1;
    private String next_prefix_series;
    private String no_gen_type;
    private String entered_by;
    private String entered_on;
    private String state_cd;
    private int off_cd;
    private String action_type;
    private boolean isApprovebutton;
    private String no_gen_type_descr;
    private String regn_series_format = "";
    private String regnColorCode = "";
    private String sufixRegnNo = "";

    /**
     * @return the series_id
     */
    public int getSeries_id() {
        return series_id;
    }

    /**
     * @param series_id the series_id to set
     */
    public void setSeries_id(int series_id) {
        this.series_id = series_id;
    }

    /**
     * @return the criteria_formula
     */
    public String getCriteria_formula() {
        return criteria_formula;
    }

    /**
     * @param criteria_formula the criteria_formula to set
     */
    public void setCriteria_formula(String criteria_formula) {
        this.criteria_formula = criteria_formula;
    }

    /**
     * @return the prefix_series
     */
    public String getPrefix_series() {
        return prefix_series;
    }

    /**
     * @param prefix_series the prefix_series to set
     */
    public void setPrefix_series(String prefix_series) {
        this.prefix_series = prefix_series;
    }

    /**
     * @return the lower_range_no
     */
    public int getLower_range_no() {
        return lower_range_no;
    }

    /**
     * @param lower_range_no the lower_range_no to set
     */
    public void setLower_range_no(int lower_range_no) {
        this.lower_range_no = lower_range_no;
    }

    /**
     * @return the upper_range_no
     */
    public int getUpper_range_no() {
        return upper_range_no;
    }

    /**
     * @param upper_range_no the upper_range_no to set
     */
    public void setUpper_range_no(int upper_range_no) {
        this.upper_range_no = upper_range_no;
    }

    /**
     * @return the running_no
     */
    public int getRunning_no() {
        return running_no;
    }

    /**
     * @param running_no the running_no to set
     */
    public void setRunning_no(int running_no) {
        this.running_no = running_no;
    }

    /**
     * @return the next_prefix_series
     */
    public String getNext_prefix_series() {
        return next_prefix_series;
    }

    /**
     * @param next_prefix_series the next_prefix_series to set
     */
    public void setNext_prefix_series(String next_prefix_series) {
        this.next_prefix_series = next_prefix_series;
    }

    /**
     * @return the no_gen_type
     */
    public String getNo_gen_type() {
        return no_gen_type;
    }

    /**
     * @param no_gen_type the no_gen_type to set
     */
    public void setNo_gen_type(String no_gen_type) {
        this.no_gen_type = no_gen_type;
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
     * @return the criteria_formuladesc
     */
    public String getCriteria_formuladesc() {
        return criteria_formuladesc;
    }

    /**
     * @param criteria_formuladesc the criteria_formuladesc to set
     */
    public void setCriteria_formuladesc(String criteria_formuladesc) {
        this.criteria_formuladesc = criteria_formuladesc;
    }

    /**
     * @return the criteria_formula_descr
     */
    public String getCriteria_formula_descr() {
        return criteria_formula_descr;
    }

    /**
     * @param criteria_formula_descr the criteria_formula_descr to set
     */
    public void setCriteria_formula_descr(String criteria_formula_descr) {
        this.criteria_formula_descr = criteria_formula_descr;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return the isApprovebutton
     */
    public boolean isIsApprovebutton() {
        return isApprovebutton;
    }

    /**
     * @param isApprovebutton the isApprovebutton to set
     */
    public void setIsApprovebutton(boolean isApprovebutton) {
        this.isApprovebutton = isApprovebutton;
    }

    /**
     * @return the action_type
     */
    public String getAction_type() {
        return action_type;
    }

    /**
     * @param action_type the action_type to set
     */
    public void setAction_type(String action_type) {
        this.action_type = action_type;
    }

    /**
     * @return the no_gen_type_descr
     */
    public String getNo_gen_type_descr() {
        return no_gen_type_descr;
    }

    /**
     * @param no_gen_type_descr the no_gen_type_descr to set
     */
    public void setNo_gen_type_descr(String no_gen_type_descr) {
        this.no_gen_type_descr = no_gen_type_descr;
    }

    /**
     * @return the regn_series_format
     */
    public String getRegn_series_format() {
        return regn_series_format;
    }

    /**
     * @param regn_series_format the regn_series_format to set
     */
    public void setRegn_series_format(String regn_series_format) {
        this.regn_series_format = regn_series_format;
    }

    /**
     * @return the regnColorCode
     */
    public String getRegnColorCode() {
        return regnColorCode;
    }

    /**
     * @param regnColorCode the regnColorCode to set
     */
    public void setRegnColorCode(String regnColorCode) {
        this.regnColorCode = regnColorCode;
    }

    /**
     * @return the sufixRegnNo
     */
    public String getSufixRegnNo() {
        return sufixRegnNo;
    }

    /**
     * @param sufixRegnNo the sufixRegnNo to set
     */
    public void setSufixRegnNo(String sufixRegnNo) {
        this.sufixRegnNo = sufixRegnNo;
    }
}
