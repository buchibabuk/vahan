/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApproveDisapprove_dobj implements Serializable {

    private String public_remark;
    private String office_remark;
    private String new_status;
    private int pre_action_code;
    private String revertBack;
    private Map<String, Object> prevRoleLabelValue;
    private boolean renderRevert = false;
    private boolean renderRevertList = false;
    private List reasonList = new ArrayList();
    private List selectedReasonList = new ArrayList();
    private List assignedReasons = new ArrayList();
    private String notRecommend;
    private boolean renderNotRecommend;
    private boolean renderHold;

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

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(new_status);
        sb.append(" ");
        sb.append(office_remark);
        sb.append("  ");
        sb.append(getPre_action_code());
        sb.append("  ");
        sb.append(public_remark);
        sb.append("  ");
        sb.append(revertBack);
        sb.append("  ");
        sb.append(notRecommend);
        return sb.toString();
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

    /**
     * @return the pre_action_code
     */
    public int getPre_action_code() {
        return pre_action_code;
    }

    /**
     * @param pre_action_code the pre_action_code to set
     */
    public void setPre_action_code(int pre_action_code) {
        this.pre_action_code = pre_action_code;
    }

    /**
     * @return the renderRevert
     */
    public boolean isRenderRevert() {
        return renderRevert;
    }

    /**
     * @param renderRevert the renderRevert to set
     */
    public void setRenderRevert(boolean renderRevert) {
        this.renderRevert = renderRevert;
    }

    /**
     * @return the renderRevertList
     */
    public boolean isRenderRevertList() {
        return renderRevertList;
    }

    /**
     * @param renderRevertList the renderRevertList to set
     */
    public void setRenderRevertList(boolean renderRevertList) {
        this.renderRevertList = renderRevertList;
    }

    /**
     * @return the reasonList
     */
    public List getReasonList() {
        return reasonList;
    }

    /**
     * @param reasonList the reasonList to set
     */
    public void setReasonList(List reasonList) {
        this.reasonList = reasonList;
    }

    public List getSelectedReasonList() {
        return selectedReasonList;
    }

    public void setSelectedReasonList(List selectedReasonList) {
        this.selectedReasonList = selectedReasonList;
    }

    public List getAssignedReasons() {
        return assignedReasons;
    }

    public void setAssignedReasons(List assignedReasons) {
        this.assignedReasons = assignedReasons;
    }

    public String getNotRecommend() {
        return notRecommend;
    }

    public void setNotRecommend(String notRecommend) {
        this.notRecommend = notRecommend;
    }

    public boolean isRenderNotRecommend() {
        return renderNotRecommend;
    }

    public void setRenderNotRecommend(boolean renderNotRecommend) {
        this.renderNotRecommend = renderNotRecommend;
    }

    public boolean isRenderHold() {
        return renderHold;
    }

    public void setRenderHold(boolean renderHold) {
        this.renderHold = renderHold;
    }
    
    
}
