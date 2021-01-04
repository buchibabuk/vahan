/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.dealer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import nic.rto.vahan.common.CryptographyAES;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.dealer.PaymentGatewayDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.dealer.PaymentGatewayImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import nic.java.util.DateUtils;

@ManagedBean(name = "reverify_cart_pay_bean")
@ViewScoped
public class ReVerifyCartPayBean implements Serializable {

    private List<PaymentGatewayDobj> cartList;
    private Date fromDate;
    private Date uptoDate;
    private boolean prevDateButton;
    private boolean nextDateButton;
    private static final Logger LOGGER = Logger.getLogger(ReVerifyCartPayBean.class);

    @PostConstruct
    public void init() {
        PaymentGatewayImpl reverify_impl = new PaymentGatewayImpl();
        cartList = new ArrayList<PaymentGatewayDobj>();
        String stateCd = null;
        int offCd = 0;
        String empCode = null;
        try {
            if (Util.getUserStateCode() != null && Util.getUserSeatOffCode() != null && Util.getEmpCode() != null) {
                stateCd = Util.getUserStateCode();
                offCd = Util.getSelectedSeat().getOff_cd();
                empCode = Util.getEmpCode();
                uptoDate = Calendar.getInstance().getTime();
                fromDate = ServerUtil.dateRange(uptoDate, 0, 0, -14);
                cartList = reverify_impl.getFailCartList(fromDate, uptoDate, stateCd, offCd, empCode);
                prevDateButton = true;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void getPendingWorkDateWisePrev() {
        PaymentGatewayImpl reverify_impl = new PaymentGatewayImpl();
        String stateCd = null;
        int offCd = 0;
        String empCode = null;
        if (fromDate != null && uptoDate != null && Util.getUserStateCode() != null && Util.getUserSeatOffCode() != null && Util.getEmpCode() != null) {
            setNextDateButton(true);
            stateCd = Util.getUserStateCode();
            offCd = Util.getSelectedSeat().getOff_cd();
            empCode = Util.getEmpCode();
            fromDate = ServerUtil.dateRange(fromDate, 0, 0, -15);
            uptoDate = ServerUtil.dateRange(uptoDate, 0, 0, -15);
            cartList = reverify_impl.getFailCartList(fromDate, uptoDate, stateCd, offCd, empCode);
        }
    }

    public void getPendingWorkDateWiseNext() {
        PaymentGatewayImpl reverify_impl = new PaymentGatewayImpl();
        String stateCd = null;
        int offCd = 0;
        String empCode = null;
        if (fromDate != null && uptoDate != null && Util.getUserStateCode() != null && Util.getUserSeatOffCode() != null && Util.getEmpCode() != null) {
            Date tempDate = ServerUtil.dateRange(fromDate, 0, 0, 15);
            if (tempDate.compareTo(new Date()) >= 0) {
                setNextDateButton(false);
                JSFUtils.showMessagesInDialog("Information", "Out of Range Dates", FacesMessage.SEVERITY_ERROR);
            } else {
                stateCd = Util.getUserStateCode();
                offCd = Util.getSelectedSeat().getOff_cd();
                empCode = Util.getEmpCode();
                fromDate = ServerUtil.dateRange(fromDate, 0, 0, 15);
                uptoDate = ServerUtil.dateRange(uptoDate, 0, 0, 15);
                cartList = reverify_impl.getFailCartList(fromDate, uptoDate, stateCd, offCd, empCode);
            }
        }
    }

    public String reVerifyPayment(PaymentGatewayDobj dobj) {
        String vahan_pgi_url = "";
        String ipPath = "";
        String return_path = "";
        CryptographyAES aes = new CryptographyAES();
        String return_dealer_url = null;
        PaymentGatewayImpl reverify_impl = new PaymentGatewayImpl();
        try {
            // check for application count and amount 
            reverify_impl.checkForReverifyApplicationCountAndAmount(dobj.getPaymentId(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), Util.getEmpCode(), dobj.getApplicationNumberList());
            vahan_pgi_url = ServerUtil.getVahanPgiUrl(TableConstants.VAHAN_PGI_CHECK_FAIL_CONNTYPE);
            ipPath = ServerUtil.getIpPath();
            return_dealer_url = ipPath + "/vahan/ui/dealer/form_printReceipt.xhtml";
            String data = "payment_id=" + dobj.getPaymentId() + "|" + "state_cd=" + Util.getUserStateCode() + "|" + "total_payable_amount=" + dobj.getTtlAmount() + "|" + "return_url=" + return_dealer_url + "|" + "nic_merchant=" + TableConstants.VAHAN_PGI_MERCHANT_CODE + "|" + "off_cd=" + Util.getSelectedSeat().getOff_cd();
            System.out.println(DateUtils.getDateInDDMMYYYY_HHMMSS(new java.util.Date()) + " D-Verify: paymentId:" + dobj.getPaymentId() + ", ApplNos:" + dobj.getApplicationNumberList() + ", Amt:" + dobj.getTtlAmount());
            data = aes.getEncriptedString(data);
            String ip = vahan_pgi_url + data;
            setSessionValue(dobj);
            HttpServletResponse hres = ((HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse());
            hres.sendRedirect(ip);
        } catch (VahanException vmex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vmex.getMessage(), vmex.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        return return_path;
    }

    private void setSessionValue(PaymentGatewayDobj dobj) {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.setAttribute("GrandTotal", dobj.getTtlAmount());
        session.setAttribute("TransactionId", dobj.getPaymentId());
        session.setAttribute("DealerPayment", true);
        session.setAttribute("PaymentVerificationType", TableConstants.DEALER_PAYMENT_REVERIFICATION);
    }

    /**
     * @return the cartList
     */
    public List<PaymentGatewayDobj> getCartList() {
        return cartList;
    }

    /**
     * @param cartList the cartList to set
     */
    public void setCartList(List<PaymentGatewayDobj> cartList) {
        this.cartList = cartList;
    }

    /**
     * @return the fromDate
     */
    public Date getFromDate() {
        return fromDate;
    }

    /**
     * @param fromDate the fromDate to set
     */
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * @return the uptoDate
     */
    public Date getUptoDate() {
        return uptoDate;
    }

    /**
     * @param uptoDate the uptoDate to set
     */
    public void setUptoDate(Date uptoDate) {
        this.uptoDate = uptoDate;
    }

    /**
     * @return the prevDateButton
     */
    public boolean isPrevDateButton() {
        return prevDateButton;
    }

    /**
     * @param prevDateButton the prevDateButton to set
     */
    public void setPrevDateButton(boolean prevDateButton) {
        this.prevDateButton = prevDateButton;
    }

    /**
     * @return the nextDateButton
     */
    public boolean isNextDateButton() {
        return nextDateButton;
    }

    /**
     * @param nextDateButton the nextDateButton to set
     */
    public void setNextDateButton(boolean nextDateButton) {
        this.nextDateButton = nextDateButton;
    }
}
