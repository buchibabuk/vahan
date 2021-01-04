/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author nic
 */
public class SCDetailsDobj implements Serializable {

    private String rcptno;
    private String purcd;
    private String rccardchipno;
    private String rcissuedt;
    private String drto1name;
    private String drto2name;
    private boolean smartcard;
    private String labelValue;
    private String pageHeader;
    private String applno;
    private String printedon;
    private String printedby;
    private List<SCDetailsDobj> scList;
    private List<SCDetailsDobj> scpendingList;
    private List<SCDetailsDobj> vhscList;
    private List<SCDetailsDobj> vascList;
    private String scpendingdtls;
    private String rcpendingdtls;
    private boolean smartcarddatalist;
    private boolean smartcarddatatable;
    private boolean rcddatalist;
    private boolean rcdatatable;
    private String tabTitle;

    /**
     * @return the rcptno
     */
    public String getRcptno() {
        return rcptno;
    }

    /**
     * @param rcptno the rcptno to set
     */
    public void setRcptno(String rcptno) {
        this.rcptno = rcptno;
    }

    /**
     * @return the purcd
     */
    public String getPurcd() {
        return purcd;
    }

    /**
     * @param purcd the purcd to set
     */
    public void setPurcd(String purcd) {
        this.purcd = purcd;
    }

    /**
     * @return the rccardchipno
     */
    public String getRccardchipno() {
        return rccardchipno;
    }

    /**
     * @param rccardchipno the rccardchipno to set
     */
    public void setRccardchipno(String rccardchipno) {
        this.rccardchipno = rccardchipno;
    }

    /**
     * @return the rcissuedt
     */
    public String getRcissuedt() {
        return rcissuedt;
    }

    /**
     * @param rcissuedt the rcissuedt to set
     */
    public void setRcissuedt(String rcissuedt) {
        this.rcissuedt = rcissuedt;
    }

    /**
     * @return the drto1name
     */
    public String getDrto1name() {
        return drto1name;
    }

    /**
     * @param drto1name the drto1name to set
     */
    public void setDrto1name(String drto1name) {
        this.drto1name = drto1name;
    }

    /**
     * @return the drto2name
     */
    public String getDrto2name() {
        return drto2name;
    }

    /**
     * @param drto2name the drto2name to set
     */
    public void setDrto2name(String drto2name) {
        this.drto2name = drto2name;
    }

    /**
     * @return the scList
     */
    public List<SCDetailsDobj> getScList() {
        return scList;
    }

    /**
     * @param scList the scList to set
     */
    public void setScList(List<SCDetailsDobj> scList) {
        this.scList = scList;
    }

    /**
     * @return the smartcard
     */
    public boolean isSmartcard() {
        return smartcard;
    }

    /**
     * @param smartcard the smartcard to set
     */
    public void setSmartcard(boolean smartcard) {
        this.smartcard = smartcard;
    }

    /**
     * @return the labelValue
     */
    public String getLabelValue() {
        return labelValue;
    }

    /**
     * @param labelValue the labelValue to set
     */
    public void setLabelValue(String labelValue) {
        this.labelValue = labelValue;
    }

    /**
     * @return the scpendingList
     */
    public List<SCDetailsDobj> getScpendingList() {
        return scpendingList;
    }

    /**
     * @param scpendingList the scpendingList to set
     */
    public void setScpendingList(List<SCDetailsDobj> scpendingList) {
        this.scpendingList = scpendingList;
    }

    /**
     * @return the pageHeader
     */
    public String getPageHeader() {
        return pageHeader;
    }

    /**
     * @param pageHeader the pageHeader to set
     */
    public void setPageHeader(String pageHeader) {
        this.pageHeader = pageHeader;
    }

    /**
     * @return the applno
     */
    public String getApplno() {
        return applno;
    }

    /**
     * @param applno the applno to set
     */
    public void setApplno(String applno) {
        this.applno = applno;
    }

    /**
     * @return the printedon
     */
    public String getPrintedon() {
        return printedon;
    }

    /**
     * @param printedon the printedon to set
     */
    public void setPrintedon(String printedon) {
        this.printedon = printedon;
    }

    /**
     * @return the printedby
     */
    public String getPrintedby() {
        return printedby;
    }

    /**
     * @param printedby the printedby to set
     */
    public void setPrintedby(String printedby) {
        this.printedby = printedby;
    }

    /**
     * @return the vhscList
     */
    public List<SCDetailsDobj> getVhscList() {
        return vhscList;
    }

    /**
     * @param vhscList the vhscList to set
     */
    public void setVhscList(List<SCDetailsDobj> vhscList) {
        this.vhscList = vhscList;
    }

    /**
     * @return the vascList
     */
    public List<SCDetailsDobj> getVascList() {
        return vascList;
    }

    /**
     * @param vascList the vascList to set
     */
    public void setVascList(List<SCDetailsDobj> vascList) {
        this.vascList = vascList;
    }

    /**
     * @return the scpendingdtls
     */
    public String getScpendingdtls() {
        return scpendingdtls;
    }

    /**
     * @param scpendingdtls the scpendingdtls to set
     */
    public void setScpendingdtls(String scpendingdtls) {
        this.scpendingdtls = scpendingdtls;
    }

    /**
     * @return the rcpendingdtls
     */
    public String getRcpendingdtls() {
        return rcpendingdtls;
    }

    /**
     * @param rcpendingdtls the rcpendingdtls to set
     */
    public void setRcpendingdtls(String rcpendingdtls) {
        this.rcpendingdtls = rcpendingdtls;
    }

    /**
     * @return the smartcarddatalist
     */
    public boolean isSmartcarddatalist() {
        return smartcarddatalist;
    }

    /**
     * @param smartcarddatalist the smartcarddatalist to set
     */
    public void setSmartcarddatalist(boolean smartcarddatalist) {
        this.smartcarddatalist = smartcarddatalist;
    }

    /**
     * @return the smartcarddatatable
     */
    public boolean isSmartcarddatatable() {
        return smartcarddatatable;
    }

    /**
     * @param smartcarddatatable the smartcarddatatable to set
     */
    public void setSmartcarddatatable(boolean smartcarddatatable) {
        this.smartcarddatatable = smartcarddatatable;
    }

    /**
     * @return the rcddatalist
     */
    public boolean isRcddatalist() {
        return rcddatalist;
    }

    /**
     * @param rcddatalist the rcddatalist to set
     */
    public void setRcddatalist(boolean rcddatalist) {
        this.rcddatalist = rcddatalist;
    }

    /**
     * @return the rcdatatable
     */
    public boolean isRcdatatable() {
        return rcdatatable;
    }

    /**
     * @param rcdatatable the rcdatatable to set
     */
    public void setRcdatatable(boolean rcdatatable) {
        this.rcdatatable = rcdatatable;
    }

    /**
     * @return the tabTitle
     */
    public String getTabTitle() {
        return tabTitle;
    }

    /**
     * @param tabTitle the tabTitle to set
     */
    public void setTabTitle(String tabTitle) {
        this.tabTitle = tabTitle;
    }
}
