/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.common;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author Ashok Kumar <send2ashok@hotmail.com>
 */
public class ApproveDisapprove_dobj implements Serializable {

    private String public_remark;
    private String office_remark;
    private String new_status;
    private String pre_action_code;
    private String revertBack;
    private Map<String, Object> prevRoleLabelValue;

    /**
     * @return the public_remark
     */
    public String getPublic_remark() {
        return public_remark;
    }

    /**
     * @param public_remark the public_remark to set
     */
    public void setPublic_remark(String public_remark) {
        this.public_remark = public_remark;
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
     * @return the new_status
     */
    public String getNew_status() {
        return new_status;
    }

    /**
     * @param new_status the new_status to set
     */
    public void setNew_status(String new_status) {
        this.new_status = new_status;
    }

    /**
     * @return the pre_action_code
     */
    public String getPre_action_code() {
        return pre_action_code;
    }

    /**
     * @param pre_action_code the pre_action_code to set
     */
    public void setPre_action_code(String pre_action_code) {
        this.pre_action_code = pre_action_code;
    }

    /**
     * @return the revertBack
     */
    public String getRevertBack() {
        return revertBack;
    }

    /**
     * @param revertBack the revertBack to set
     */
    public void setRevertBack(String revertBack) {
        this.revertBack = revertBack;
    }

    /**
     * @return the prevRoleLabelValue
     */
    public Map<String, Object> getPrevRoleLabelValue() {
        return prevRoleLabelValue;
    }

    /**
     * @param prevRoleLabelValue the prevRoleLabelValue to set
     */
    public void setPrevRoleLabelValue(Map<String, Object> prevRoleLabelValue) {
        this.prevRoleLabelValue = prevRoleLabelValue;
    }
}
