/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.eChallan;

import java.io.Serializable;
import javax.faces.application.FacesMessage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpSession;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;

import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.eChallan.ChallanReferToImpl;
import nic.vahan.server.CommonUtils;

import org.apache.log4j.Logger;

/**
 *
 * @author ASHOK
 */
@ManagedBean(name = "ReferChallanReport")
@ViewScoped
public class ReferChallanReportBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(ReferChallanReportBean.class);
    private String applicatioNo;
    private String challanNo;
    private String selectedOption ="applNo";
    private boolean renderApplPnl = true;
    private boolean renderChallanPnl;
    private boolean renderPrintReportButton = true;
 

    public String printReport() {
        String str="";
        boolean isPrExist = false;
        ChallanReferToImpl challanReferToImpl = new ChallanReferToImpl();
        try {
            HttpSession session = Util.getSession();
            session.setAttribute("challan_no", challanNo);
            if (challanNo != null && !challanNo.isEmpty()) {
                String applNo = challanReferToImpl.isCourtDetailsExistForChallan(challanNo);
                if (!CommonUtils.isNullOrBlank(applNo)) {
                    isPrExist = true;
                    applicatioNo = applNo;
                }
            }
            session.setAttribute("appl_no", applicatioNo);
            isPrExist = challanReferToImpl.isCourtDetailsExist(applicatioNo);

            if (isPrExist) {
                if ("OR".equalsIgnoreCase(Util.getUserStateCode())) {
                    str="/ui/reports/formChallanProsecutionReports.xhtml?faces-redirect=true";                 
                } else {                   
                     str="/ui/reports/formInformationViolationReport.xhtml?faces-redirect=true";
                }
            }
        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            return null;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return null;
        }
        return str;
    }

    public void selectedOptionListener(AjaxBehaviorEvent ajax) {
        if (getSelectedOption().equalsIgnoreCase("applNo")) {
            renderApplPnl = true;
            renderChallanPnl = false;
            renderPrintReportButton = true;
        } else if (getSelectedOption().equalsIgnoreCase("challanNo")) {
            renderApplPnl = false;
            renderChallanPnl = true;
            renderPrintReportButton = true;
        }
    }

    public String getApplicatioNo() {
        return applicatioNo;
    }

    public void setApplicatioNo(String applicatioNo) {
        this.applicatioNo = applicatioNo;
    }

    /**
     * @return the selectedOption
     */
    public String getSelectedOption() {
        return selectedOption;
    }

    /**
     * @param selectedOption the selectedOption to set
     */
    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }
    /**
     * @return the renderApplPnl
     */
    public boolean isRenderApplPnl() {
        return renderApplPnl;
    }

    /**
     * @param renderApplPnl the renderApplPnl to set
     */
    public void setRenderApplPnl(boolean renderApplPnl) {
        this.renderApplPnl = renderApplPnl;
    }

    /**
     * @return the renderChallanPnl
     */
    public boolean isRenderChallanPnl() {
        return renderChallanPnl;
    }

    /**
     * @param renderChallanPnl the renderChallanPnl to set
     */
    public void setRenderChallanPnl(boolean renderChallanPnl) {
        this.renderChallanPnl = renderChallanPnl;
    }

    /**
     * @return the challanNo
     */
    public String getChallanNo() {
        return challanNo;
    }

    /**
     * @param challanNo the challanNo to set
     */
    public void setChallanNo(String challanNo) {
        this.challanNo = challanNo;
    }

    /**
     * @return the renderPrintReportButton
     */
    public boolean isRenderPrintReportButton() {
        return renderPrintReportButton;
    }

    /**
     * @param renderPrintReportButton the renderPrintReportButton to set
     */
    public void setRenderPrintReportButton(boolean renderPrintReportButton) {
        this.renderPrintReportButton = renderPrintReportButton;
    }

}
