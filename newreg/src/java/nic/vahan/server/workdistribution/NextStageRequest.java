/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.server.workdistribution;

/**
 *
 * @author Ashok Kumar <send2ashok@hotmail.com>
 */
public class NextStageRequest {

    private String appl_no;
    private String cntr_id;
    private String state_cd;
    private String rto_code;
    private String file_movement_type;
    private int pur_cd;
    private int action_cd;
    private int flow_slno;
    private long emp_cd;
    private int off_cd;

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
     * @return the rto_code
     */
    public String getRto_code() {
        return rto_code;
    }

    /**
     * @param rto_code the rto_code to set
     */
    public void setRto_code(String rto_code) {
        this.rto_code = rto_code;
    }

    /**
     * @return the pur_cd
     */
    public int getPur_cd() {
        return pur_cd;
    }

    /**
     * @param pur_cd the pur_cd to set
     */
    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    /**
     * @return the action_cd
     */
    public int getAction_cd() {
        return action_cd;
    }

    /**
     * @param action_cd the action_cd to set
     */
    public void setAction_cd(int action_cd) {
        this.action_cd = action_cd;
    }

    /**
     * @return the flow_slno
     */
    public int getFlow_slno() {
        return flow_slno;
    }

    /**
     * @param flow_slno the flow_slno to set
     */
    public void setFlow_slno(int flow_slno) {
        this.flow_slno = flow_slno;
    }

    /**
     * @return the emp_cd
     */
    public long getEmp_cd() {
        return emp_cd;
    }

    /**
     * @param emp_cd the emp_cd to set
     */
    public void setEmp_cd(long emp_cd) {
        this.emp_cd = emp_cd;
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
     * @return the file_movement_type
     */
    public String getFile_movement_type() {
        return file_movement_type;
    }

    /**
     * @param file_movement_type the file_movement_type to set
     */
    public void setFile_movement_type(String file_movement_type) {
        this.file_movement_type = file_movement_type;
    }
}
