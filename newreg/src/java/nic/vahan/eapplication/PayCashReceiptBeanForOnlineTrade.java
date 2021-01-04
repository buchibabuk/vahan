/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.eapplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import nic.vahan.form.dobj.reports.CashReceiptReportDobj;
import nic.vahan.form.dobj.reports.CashRecieptSubListDobj;
import nic.vahan.form.impl.PrintDocImpl;
import org.apache.log4j.Logger;

/**
 *
 * @author amit
 */
@ManagedBean(name = "payCashReceiptBeanOnlineTrade")
@RequestScoped
public class PayCashReceiptBeanForOnlineTrade implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(PayCashReceiptBeanForOnlineTrade.class);
    private CashReceiptReportDobj dobj = null;
    private CashRecieptSubListDobj dobjsublist = null;
    private boolean dealer = false;
    private String backButton;
    private boolean showSurcharge = false;
    private boolean showExcessamt = false;
    private boolean showCash_amt = false;
    private String renderMethod;
    private String text;
    private String label;
    private int mode;
    private int size;
    List<CashRecieptSubListDobj> list = null;
    private boolean renderqrCode = false;

    public PayCashReceiptBeanForOnlineTrade() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        Map map;
        map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        String rcpt_no = (String) map.get("rcptno");

        try {
            dobj = PrintDocImpl.getCashReceiptOnlineTradeReportDobj(rcpt_no);
            if (dobj != null) {
                dealer = true;
                if (!dobj.getPrntRecieptSubList().isEmpty()) {
                    list = new ArrayList<CashRecieptSubListDobj>();
                    list = dobj.getPrntRecieptSubList();
                    for (CashRecieptSubListDobj subDobj : list) {
                        if (subDobj.getBlnissurcharge() != null && subDobj.getBlnissurcharge().booleanValue()) {
                            if (!showSurcharge) {
                                showSurcharge = subDobj.getBlnissurcharge();
                            }
                        }
                        if (dobj.isIsecsessAmt()) {
                            if (!showExcessamt) {
                                showExcessamt = dobj.isIsecsessAmt();
                            }
                        }
                        if (dobj.isIscashAmt()) {
                            if (!showCash_amt) {
                                showCash_amt = dobj.isIscashAmt();
                            }
                        }

                    }
                }


                if ((dobj.getRegnNo() != null && !dobj.getRegnNo().equals("")) && (dobj.getApplNo() != null && !dobj.getApplNo().equals("")) && (dobj.getReceiptNo() != null && !dobj.getReceiptNo().equals(""))) {
                    renderqrCode = true;
                    //**********Generating QRCode**********
                    renderMethod = "canvas";
                    text = "RegnNo:" + dobj.getRegnNo() + " \\nRcptNo: " + dobj.getReceiptNo() + "\\nApplicant:" + dobj.getOwnerName();
                    mode = 0;
                    label = "QR Code";
                    size = 110;
                } else if ((dobj.getApplNo() != null && !dobj.getApplNo().equals("")) && (dobj.getReceiptNo() != null && !dobj.getReceiptNo().equals(""))) {
                    renderqrCode = true;
                    //**********Generating QRCode**********
                    renderMethod = "canvas";
                    text = "ApplNo: " + dobj.getApplNo() + " \\nRcptNo: " + dobj.getReceiptNo();
                    mode = 0;
                    label = "QR Code";
                    size = 110;
                }

            }
            //*************************************
        } catch (Exception e) {
            LOGGER.error("Exception for Receipt no :" + rcpt_no + " :" + e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            session.invalidate();

        }

    }

    /**
     * @return the dobj
     */
    public CashReceiptReportDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(CashReceiptReportDobj dobj) {
        this.dobj = dobj;
    }

    /**
     * @return the dealer
     */
    public boolean isDealer() {
        return dealer;
    }

    /**
     * @param dealer the dealer to set
     */
    public void setDealer(boolean dealer) {
        this.dealer = dealer;
    }

    public String backButton() {
        return backButton;
    }

    public CashRecieptSubListDobj getDobjsublist() {
        return dobjsublist;
    }

    public void setDobjsublist(CashRecieptSubListDobj dobjsublist) {
        this.dobjsublist = dobjsublist;
    }

    /**
     * @return the showSurcharge
     */
    public boolean isShowSurcharge() {
        return showSurcharge;
    }

    /**
     * @param showSurcharge the showSurcharge to set
     */
    public void setShowSurcharge(boolean showSurcharge) {
        this.showSurcharge = showSurcharge;
    }

    /**
     * @return the renderMethod
     */
    public String getRenderMethod() {
        return renderMethod;
    }

    /**
     * @param renderMethod the renderMethod to set
     */
    public void setRenderMethod(String renderMethod) {
        this.renderMethod = renderMethod;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the mode
     */
    public int getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @return the renderqrCode
     */
    public boolean isRenderqrCode() {
        return renderqrCode;
    }

    /**
     * @param renderqrCode the renderqrCode to set
     */
    public void setRenderqrCode(boolean renderqrCode) {
        this.renderqrCode = renderqrCode;
    }

    public boolean isShowExcessamt() {
        return showExcessamt;
    }

    public void setShowExcessamt(boolean showExcessamt) {
        this.showExcessamt = showExcessamt;
    }

    public boolean isShowCash_amt() {
        return showCash_amt;
    }

    public void setShowCash_amt(boolean showCash_amt) {
        this.showCash_amt = showCash_amt;
    }
}
