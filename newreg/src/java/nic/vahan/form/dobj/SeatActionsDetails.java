/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

/**
 *
 * @author nic5912
 */
public class SeatActionsDetails {

    private String appl_no, appl_dt, seat_cd, seat_descr, regn_no;
    private String descr, office_remark, remark_for_public, off_name;
    private int pur_cd, file_movement_slno, srl_no, off_cd;
    private String status;
    private String role_desc;
    private String cntr_id;
    private int seatrole;

    public String getRole_desc() {
        return role_desc;
    }

    public void setRole_desc(String role_desc) {
        this.role_desc = role_desc;
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getSeat_cd() {
        return seat_cd;
    }

    public void setSeat_cd(String seat_cd) {
        this.seat_cd = seat_cd;
    }

    public String getSeat_descr() {
        return seat_descr;
    }

    public void setSeat_descr(String seat_descr) {
        this.seat_descr = seat_descr;
    }

    public int getSeatrole() {
        return seatrole;
    }

    public void setSeatrole(int seatrole) {
        this.seatrole = seatrole;
    }

    public int getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    /**
     * @return the descr
     */
    public String getDescr() {
        return descr;
    }

    /**
     * @param descr the descr to set
     */
    public void setDescr(String descr) {
        this.descr = descr;
    }

    /**
     * @return the office_remark
     */
    public String getOffice_remark() {
        return office_remark;
    }

    /**
     * @param office_remark the office_remark to set
     */
    public void setOffice_remark(String office_remark) {
        this.office_remark = office_remark;
    }

    /**
     * @return the remark_for_public
     */
    public String getRemark_for_public() {
        return remark_for_public;
    }

    /**
     * @param remark_for_public the remark_for_public to set
     */
    public void setRemark_for_public(String remark_for_public) {
        this.remark_for_public = remark_for_public;
    }

    /**
     * @return the file_movement_slno
     */
    public int getFile_movement_slno() {
        return file_movement_slno;
    }

    /**
     * @param file_movement_slno the file_movement_slno to set
     */
    public void setFile_movement_slno(int file_movement_slno) {
        this.file_movement_slno = file_movement_slno;
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
     * @return the srl_no
     */
    public int getSrl_no() {
        return srl_no;
    }

    /**
     * @param srl_no the srl_no to set
     */
    public void setSrl_no(int srl_no) {
        this.srl_no = srl_no;
    }

    /**
     * @return the off_name
     */
    public String getOff_name() {
        return off_name;
    }

    /**
     * @param off_name the off_name to set
     */
    public void setOff_name(String off_name) {
        this.off_name = off_name;
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
     * @return the appl_dt
     */
    public String getAppl_dt() {
        return appl_dt;
    }

    /**
     * @param appl_dt the appl_dt to set
     */
    public void setAppl_dt(String appl_dt) {
        this.appl_dt = appl_dt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the cntr_id
     */
    public String getCntr_id() {
        return cntr_id;
    }

    /**
     * @param cntr_id the cntr_id to set
     */
    public void setCntr_id(String cntr_id) {
        this.cntr_id = cntr_id;
    }
}
