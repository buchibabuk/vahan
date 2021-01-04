/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.tradecert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.vahan.form.dobj.tradecert.ApplicationAuditTrailTradeCertDobj;
import nic.vahan.form.impl.tradecert.ApplicationAuditTrailTradeCertImpl;
import org.apache.log4j.Logger;

/**
 *
 * @author rizwan on 24-march-2017
 */
@ManagedBean(name = "tradecertaudittrial")
@ViewScoped
public class ApplicationAuditTrailTradeCertBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ApplicationAuditTrailTradeCertBean.class);
    private String applicationNo = ""; //
    private String applNo = "";
    private List<ApplicationAuditTrailTradeCertDobj> auditList = new ArrayList<ApplicationAuditTrailTradeCertDobj>();
    private ApplicationAuditTrailTradeCertDobj auditdobj = new ApplicationAuditTrailTradeCertDobj();
    private boolean renderAuditTable = false;
    private String currentStatus = "";

    /**
     * Creates a new instance of ApplicationAuditTrailTradeCertBean
     */
    public ApplicationAuditTrailTradeCertBean() {
    }

    /**
     * @return the applicationNo
     */
    public String getApplicationNo() {
        return applicationNo;
    }

    /**
     * @param applicationNo the applicationNo to set
     */
    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }

    public void applicationInfo() {
        try {
            auditList.clear();

            boolean isExist = ApplicationAuditTrailTradeCertImpl.checkApplicationNoExist(applNo);
            if (!isExist) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application no does not exist try next....", "Application no does not exist try next.....");
                FacesContext.getCurrentInstance().addMessage(null, message);
                setRenderAuditTable(false);
                return;

            }
            if (isExist) {
                auditList = ApplicationAuditTrailTradeCertImpl.getAuditTrailInfo(getApplNo());
                //  Collections.sort(auditList,new AuditListComparator());
                this.currentStatus = ((ApplicationAuditTrailTradeCertDobj) auditList.get(auditList.size() - 1)).getCurrentStatus();
                if (!auditList.isEmpty()) {
                    setRenderAuditTable(true);
                } else {
                    setRenderAuditTable(false);
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No records are found for entered Application no ", "No records are found for entered Application no");
                    FacesContext.getCurrentInstance().addMessage(null, message);

                }
            }





        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }

    /**
     * @return the auditList
     */
    public List<ApplicationAuditTrailTradeCertDobj> getAuditList() {
        return auditList;
    }

    /**
     * @param auditList the auditList to set
     */
    public void setAuditList(List<ApplicationAuditTrailTradeCertDobj> auditList) {
        this.auditList = auditList;
    }

    /**
     * @return the applNo
     */
    public String getApplNo() {
        return applNo;
    }

    /**
     * @param applNo the applNo to set
     */
    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    /**
     * @return the auditdobj
     */
    public ApplicationAuditTrailTradeCertDobj getAuditdobj() {
        return auditdobj;
    }

    /**
     * @param auditdobj the auditdobj to set
     */
    public void setAuditdobj(ApplicationAuditTrailTradeCertDobj auditdobj) {
        this.auditdobj = auditdobj;
    }

    /**
     * @return the renderAuditTable
     */
    public boolean isRenderAuditTable() {
        return renderAuditTable;
    }

    /**
     * @param renderAuditTable the renderAuditTable to set
     */
    public void setRenderAuditTable(boolean renderAuditTable) {
        this.renderAuditTable = renderAuditTable;
    }

    /**
     * @return the currentStatus
     */
    public String getCurrentStatus() {
        return currentStatus;
    }

    /**
     * @param currentStatus the currentStatus to set
     */
    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    private class AuditListComparator implements Comparator<ApplicationAuditTrailTradeCertDobj> {

        @Override
        public int compare(ApplicationAuditTrailTradeCertDobj o1, ApplicationAuditTrailTradeCertDobj o2) {
            return o1.getIterationCounter().compareTo(o2.getIterationCounter());
        }
    }
}
