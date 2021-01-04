/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.impl.PrintPrevCashReceiptImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author nic
 */
@ManagedBean(name = "prevCashRcptbean")
@ViewScoped
public class PrintPrevCashReceiptBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(PrintPrevCashReceiptBean.class);
    private String rcpt_no;
    private String applNo;
    private boolean renderReceiptPanel = false;
    Calendar cal = Calendar.getInstance();
    Date date = cal.getTime();
    private String selectedRcpt;
    String userCode = "";

    public void confirmPrintReceipt() throws VahanException {
        PrintPrevCashReceiptImpl impl = null;
        try {
            impl = new PrintPrevCashReceiptImpl();
            if (getRcpt_no() == null || getRcpt_no().trim().equalsIgnoreCase("")) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Receipt No should not be Blank!", "Receipt No should not be Blank!");
                FacesContext.getCurrentInstance().addMessage("PrintId", message);
                return;
            }
            if (selectedRcpt.equalsIgnoreCase("O")) {
                userCode = TableConstants.ONLINE_PAYMENT;
            } else if (selectedRcpt.equalsIgnoreCase("R")) {
                userCode = Util.getEmpCode();
            }
            if (!CommonUtils.isNullOrBlank(getRcpt_no())) {
                if (!impl.isReceiptExist(getRcpt_no().trim().toUpperCase(), userCode)) {
                    FacesMessage message = null;
                    message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Receipt No " + getRcpt_no().trim().toUpperCase() + " Does not exist!", "Receipt No " + getRcpt_no().trim().toUpperCase() + " Does not exist!");
                    FacesContext.getCurrentInstance().addMessage("PrintId", message);
                    return;
                }
            }
        } catch (VahanException ve) {
            //LOGGER.error(ve);
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }

        // RequestContext rc = RequestContext.getCurrentInstance();
        PrimeFaces.current().executeScript("PF('printConfDlgTax').show()");
    }

    public void receiptListener() {
        setRenderReceiptPanel(true);
    }

    public String printReceipt() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("rcptno", getRcpt_no().trim().toUpperCase());
        return "PrintCashReceiptReport";
    }

    /**
     * @return the rcpt_no
     */
    public String getRcpt_no() {
        return rcpt_no;
    }

    /**
     * @param rcpt_no the rcpt_no to set
     */
    public void setRcpt_no(String rcpt_no) {
        this.rcpt_no = rcpt_no;
    }

    /**
     * @return the selectedRcpt
     */
    public String getSelectedRcpt() {
        return selectedRcpt;
    }

    /**
     * @param selectedRcpt the selectedRcpt to set
     */
    public void setSelectedRcpt(String selectedRcpt) {
        this.selectedRcpt = selectedRcpt;
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
     * @return the renderOnlineReceipt
     */
    public boolean isRenderReceiptPanel() {
        return renderReceiptPanel;
    }

    /**
     * @param renderOnlineReceipt the renderOnlineReceipt to set
     */
    public void setRenderReceiptPanel(boolean renderReceiptPanel) {
        this.renderReceiptPanel = renderReceiptPanel;
    }
}
