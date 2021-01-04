/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.hsrp;

import java.io.Serializable;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import nic.vahan.form.bean.smartcard.*;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.vahan.form.dobj.hsrp.HSRPIpDobj;
import nic.vahan.form.impl.SmartCardImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.hsrp.HSRPFileExportImpl;

/**
 *
 * @author tranC075
 */
@ManagedBean(name = "hsrpIP")
@ViewScoped
public class HSRPIPRegisterBean implements Serializable {

    private String ipAdress1 = "";
    private String ipAdress2 = "";
    private String ipAdress3 = "";
    private String ipAdress4 = "";
    FacesMessage message = null;
    private ArrayList<HSRPIpDobj> listIp = null;
    private boolean listShow = false;
    private HSRPIpDobj selectva_ip = new HSRPIpDobj();
    private boolean showform = false;
    private boolean showDataTable = false;
    private String showEditOrNew = "";
    HSRPIpDobj dobj = new HSRPIpDobj();
    private String merchantKey = "";

    @PostConstruct
    private void init() {
        showDataTable = true;
        showform = false;
        listIp = (ArrayList<HSRPIpDobj>) HSRPFileExportImpl.getIpList(Util.getEmpCode());

        if (listIp != null) {
            if (listIp.size() > 10) {
                setListShow(true);
            } else {
                setListShow(false);
            }
        } else {
            setListShow(false);
        }

    }

    public boolean checkIp(String ip) {

        if (Integer.parseInt(ip) <= 255) {
            return false;

        } else {
            return true;
        }

    }

    public void deleteIp() {
        if (getSelectva_ip() == null || getSelectva_ip().equals("")) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "  Please Select any one Case For Deletion  ", null);
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        dobj.setIp(getSelectva_ip().getIp().trim().toUpperCase());

        String status = HSRPFileExportImpl.deleteIPDetails(dobj.getIp());
        if (status.equals("SUCCESS")) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "  IP Deleted Successfully  ", null);
            FacesContext.getCurrentInstance().addMessage(null, message);
            listIp = null;
            listIp = (ArrayList<HSRPIpDobj>) HSRPFileExportImpl.getIpList(Util.getEmpCode());
            if (listIp != null) {
                if (listIp.size() > 10) {
                    setListShow(true);
                } else {
                    setListShow(false);
                }
            } else {
                setListShow(false);
            }
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "  Technical Error  ", null);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void registerIP() {
        if (checkIp(ipAdress1) || checkIp(ipAdress2) || checkIp(ipAdress3) || checkIp(ipAdress4)) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "  IP Address cannot be greater than 255  ", null);
            FacesContext.getCurrentInstance().addMessage(null, message);
            ipAdress1 = "";
            ipAdress2 = "";
            ipAdress3 = "";
            ipAdress4 = "";
            return;
        }

        String ipAddress = ipAdress1 + "." + ipAdress2 + "." + ipAdress3 + "." + ipAdress4;
        String status = HSRPFileExportImpl.saveIPDetails(ipAddress, merchantKey);
        if (status.equals("SUCCESS")) {
            setShowform(false);
            showDataTable = true;
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "  IP " + ipAddress + " registered successfully  ", null);
            FacesContext.getCurrentInstance().addMessage(null, message);
            ipAdress1 = "";
            ipAdress2 = "";
            ipAdress3 = "";
            ipAdress4 = "";
            listIp = null;
            listIp = (ArrayList<HSRPIpDobj>) HSRPFileExportImpl.getIpList(Util.getEmpCode());
            if (listIp != null) {
                if (listIp.size() > 10) {
                    setListShow(true);
                } else {
                    setListShow(false);
                }
            } else {
                setListShow(false);
            }

        } else if (status.equals("REPEAT")) {
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "  IP Address has been already registered  ", null);
            FacesContext.getCurrentInstance().addMessage(null, message);
            ipAdress1 = "";
            ipAdress2 = "";
            ipAdress3 = "";
            ipAdress4 = "";
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "  Technical error   ", null);
            FacesContext.getCurrentInstance().addMessage(null, message);
            ipAdress1 = "";
            ipAdress2 = "";
            ipAdress3 = "";
            ipAdress4 = "";
        }
    }

    public String newFormFillUp() {

        setShowform(true);
        showDataTable = false;
        ipAdress1 = "";
        ipAdress2 = "";
        ipAdress3 = "";
        ipAdress4 = "";
        merchantKey = "";

        return "";
    }

    /**
     * @return the ipAdress1
     */
    public String getIpAdress1() {
        return ipAdress1;
    }

    /**
     * @param ipAdress1 the ipAdress1 to set
     */
    public void setIpAdress1(String ipAdress1) {
        this.ipAdress1 = ipAdress1;
    }

    /**
     * @return the ipAdress2
     */
    public String getIpAdress2() {
        return ipAdress2;
    }

    /**
     * @param ipAdress2 the ipAdress2 to set
     */
    public void setIpAdress2(String ipAdress2) {
        this.ipAdress2 = ipAdress2;
    }

    /**
     * @return the ipAdress3
     */
    public String getIpAdress3() {
        return ipAdress3;
    }

    /**
     * @param ipAdress3 the ipAdress3 to set
     */
    public void setIpAdress3(String ipAdress3) {
        this.ipAdress3 = ipAdress3;
    }

    /**
     * @return the ipAdress4
     */
    public String getIpAdress4() {
        return ipAdress4;
    }

    /**
     * @param ipAdress4 the ipAdress4 to set
     */
    public void setIpAdress4(String ipAdress4) {
        this.ipAdress4 = ipAdress4;
    }

    /**
     * @return the listIp
     */
    public ArrayList getListIp() {
        return listIp;
    }

    /**
     * @param listIp the listIp to set
     */
    public void setListIp(ArrayList listIp) {
        this.listIp = listIp;
    }

    /**
     * @return the listShow
     */
    public boolean isListShow() {
        return listShow;
    }

    /**
     * @param listShow the listShow to set
     */
    public void setListShow(boolean listShow) {
        this.listShow = listShow;
    }

    /**
     * @return the selectva_ip
     */
    public HSRPIpDobj getSelectva_ip() {
        return selectva_ip;
    }

    /**
     * @param selectva_ip the selectva_ip to set
     */
    public void setSelectva_ip(HSRPIpDobj selectva_ip) {
        this.selectva_ip = selectva_ip;
    }

    /**
     * @return the showform
     */
    public boolean isShowform() {
        return showform;
    }

    /**
     * @param showform the showform to set
     */
    public void setShowform(boolean showform) {
        this.showform = showform;
    }

    /**
     * @return the showDataTable
     */
    public boolean isShowDataTable() {
        return showDataTable;
    }

    /**
     * @param showDataTable the showDataTable to set
     */
    public void setShowDataTable(boolean showDataTable) {
        this.showDataTable = showDataTable;
    }

    /**
     * @return the showEditOrNew
     */
    public String getShowEditOrNew() {
        return showEditOrNew;
    }

    /**
     * @param showEditOrNew the showEditOrNew to set
     */
    public void setShowEditOrNew(String showEditOrNew) {
        this.showEditOrNew = showEditOrNew;
    }

    /**
     * @return the merchantKey
     */
    public String getMerchantKey() {
        return merchantKey;
    }

    /**
     * @param merchantKey the merchantKey to set
     */
    public void setMerchantKey(String merchantKey) {
        this.merchantKey = merchantKey;
    }
}
