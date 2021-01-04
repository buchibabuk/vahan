package nic.vahan.dashBoard;

import java.io.Serializable;

/**
 *
 * @author Dhananjay
 */
public class PendingApplTreeNodeDobj implements Serializable {

    private String nodeName;
    private int total;
    private String viewType;
    private int purCd;
    private int actionCd;
    private String regnType;
    private int norms;
    private int vhClass;
    private int pmtType;
    private int pmtCatg;
    private int step;
    private String purposeTypeCd;

    public PendingApplTreeNodeDobj(String nodeName, int total) {
        this.nodeName = nodeName;
        this.total = total;

    }

    public PendingApplTreeNodeDobj(String nodeName, int total, int purCd, int actionCode, String viewType, String purposeTypeCd, int step) {
        this.nodeName = nodeName;
        this.total = total;
        this.purCd = purCd;
        this.actionCd = actionCode;
        this.viewType = viewType;
        this.purposeTypeCd = purposeTypeCd;
        this.step = step;

    }

    public PendingApplTreeNodeDobj(String nodeName, int total, String regnType, int norms, int vhClass, String viewType, int step) {
        this.nodeName = nodeName;
        this.total = total;
        this.regnType = regnType;
        this.norms = norms;
        this.vhClass = vhClass;
        this.viewType = viewType;
        this.step = step;
    }

    public PendingApplTreeNodeDobj(String nodeName, int total, int pmtType, int pmtCatg, String viewType) {
        this.nodeName = nodeName;
        this.total = total;
        this.pmtType = pmtType;
        this.pmtCatg = pmtCatg;
        this.viewType = viewType;
    }

    /**
     * @return the nodeName
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * @param nodeName the nodeName to set
     */
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * @return the purCd
     */
    public int getPurCd() {
        return purCd;
    }

    /**
     * @param purCd the purCd to set
     */
    public void setPurCd(int purCd) {
        this.purCd = purCd;
    }

    /**
     * @return the actionCd
     */
    /**
     * @return the regnType
     */
    public String getRegnType() {
        return regnType;
    }

    /**
     * @param regnType the regnType to set
     */
    public void setRegnType(String regnType) {
        this.regnType = regnType;
    }

    /**
     * @return the norms
     */
    public int getNorms() {
        return norms;
    }

    /**
     * @param norms the norms to set
     */
    public void setNorms(int norms) {
        this.norms = norms;
    }

    /**
     * @return the vhClass
     */
    public int getVhClass() {
        return vhClass;
    }

    /**
     * @param vhClass the vhClass to set
     */
    public void setVhClass(int vhClass) {
        this.vhClass = vhClass;
    }

    /**
     * @return the pmtType
     */
    public int getPmtType() {
        return pmtType;
    }

    /**
     * @param pmtType the pmtType to set
     */
    public void setPmtType(int pmtType) {
        this.pmtType = pmtType;
    }

    /**
     * @return the pmtCatg
     */
    public int getPmtCatg() {
        return pmtCatg;
    }

    /**
     * @param pmtCatg the pmtCatg to set
     */
    public void setPmtCatg(int pmtCatg) {
        this.pmtCatg = pmtCatg;
    }

    /**
     * @return the viewType
     */
    public String getViewType() {
        return viewType;
    }

    /**
     * @param viewType the viewType to set
     */
    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    /**
     * @return the step
     */
    public int getStep() {
        return step;
    }

    /**
     * @param step the step to set
     */
    public void setStep(int step) {
        this.step = step;
    }

    /**
     * @return the total
     */
    public int getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(int total) {
        this.total = total;
    }

    public String getPurposeTypeCd() {
        return purposeTypeCd;
    }

    public void setPurposeTypeCd(String purposeTypeCd) {
        this.purposeTypeCd = purposeTypeCd;
    }

    public int getActionCd() {
        return actionCd;
    }

    public void setActionCd(int actionCd) {
        this.actionCd = actionCd;
    }
}
