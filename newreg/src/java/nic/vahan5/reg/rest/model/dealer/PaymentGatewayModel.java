/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.rest.model.dealer;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.form.bean.dealer.PaymentGatewayBean;
import nic.vahan.form.dobj.dealer.PaymentGatewayDobj;
import nic.vahan5.reg.rest.model.SessionVariablesModel;
import org.apache.log4j.Logger;

/**
 *
 * @author Ramesh
 */
public class PaymentGatewayModel {

    private static Logger LOGGER = Logger.getLogger(PaymentGatewayModel.class);
//    private static final long serialVersionUID = 1L;
    private List<PaymentGatewayDobj> applicationNoAndAmountDetail;
    private long totalPayableAmount;
    private int actionCode;
    private boolean makePaymentButtonVisibility = false;
    private boolean verifyButtonVisibility = false;
    private boolean paymentGatewayPanelVisibility = false;
    private String paymentId;
    private List<PaymentGatewayDobj> cartList;
    private boolean removeRollBack = false;
    private List<PaymentGatewayDobj> payGatewayDetailList;
    private String applicationNumberList = "";
    private String rollBackApplList = "";
    private String purCdList = null;
    //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SessionVariablesModel sessionVariables = null;
    private PaymentGatewayDobj paymentGatewayDobj;
    private String vahan_pgi_url;
    private String trans_no;

    public List<PaymentGatewayDobj> getApplicationNoAndAmountDetail() {
        return applicationNoAndAmountDetail;
    }

    public void setApplicationNoAndAmountDetail(List<PaymentGatewayDobj> applicationNoAndAmountDetail) {
        this.applicationNoAndAmountDetail = applicationNoAndAmountDetail;
    }

    public long getTotalPayableAmount() {
        return totalPayableAmount;
    }

    public void setTotalPayableAmount(long totalPayableAmount) {
        this.totalPayableAmount = totalPayableAmount;
    }

    public int getActionCode() {
        return actionCode;
    }

    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
    }

    public boolean isMakePaymentButtonVisibility() {
        return makePaymentButtonVisibility;
    }

    public void setMakePaymentButtonVisibility(boolean makePaymentButtonVisibility) {
        this.makePaymentButtonVisibility = makePaymentButtonVisibility;
    }

    public boolean isVerifyButtonVisibility() {
        return verifyButtonVisibility;
    }

    public void setVerifyButtonVisibility(boolean verifyButtonVisibility) {
        this.verifyButtonVisibility = verifyButtonVisibility;
    }

    public boolean isPaymentGatewayPanelVisibility() {
        return paymentGatewayPanelVisibility;
    }

    public void setPaymentGatewayPanelVisibility(boolean paymentGatewayPanelVisibility) {
        this.paymentGatewayPanelVisibility = paymentGatewayPanelVisibility;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public List<PaymentGatewayDobj> getCartList() {
        return cartList;
    }

    public void setCartList(List<PaymentGatewayDobj> cartList) {
        this.cartList = cartList;
    }

    public boolean isRemoveRollBack() {
        return removeRollBack;
    }

    public void setRemoveRollBack(boolean removeRollBack) {
        this.removeRollBack = removeRollBack;
    }

    public List<PaymentGatewayDobj> getPayGatewayDetailList() {
        return payGatewayDetailList;
    }

    public void setPayGatewayDetailList(List<PaymentGatewayDobj> payGatewayDetailList) {
        this.payGatewayDetailList = payGatewayDetailList;
    }

    public String getApplicationNumberList() {
        return applicationNumberList;
    }

    public void setApplicationNumberList(String applicationNumberList) {
        this.applicationNumberList = applicationNumberList;
    }

    public String getRollBackApplList() {
        return rollBackApplList;
    }

    public void setRollBackApplList(String rollBackApplList) {
        this.rollBackApplList = rollBackApplList;
    }

    public String getPurCdList() {
        return purCdList;
    }

    public void setPurCdList(String purCdList) {
        this.purCdList = purCdList;
    }

//    public SimpleDateFormat getDateFormat() {
//        return dateFormat;
//    }
//    public void setDateFormat(SimpleDateFormat dateFormat) {
//        this.dateFormat = dateFormat;
//    }
    public SessionVariablesModel getSessionVariables() {
        return sessionVariables;
    }

    public void setSessionVariables(SessionVariablesModel sessionVariables) {
        this.sessionVariables = sessionVariables;
    }

    public PaymentGatewayDobj getPaymentGatewayDobj() {
        return paymentGatewayDobj;
    }

    public void setPaymentGatewayDobj(PaymentGatewayDobj paymentGatewayDobj) {
        this.paymentGatewayDobj = paymentGatewayDobj;
    }

    public String getVahan_pgi_url() {
        return vahan_pgi_url;
    }

    public void setVahan_pgi_url(String vahan_pgi_url) {
        this.vahan_pgi_url = vahan_pgi_url;
    }

    public String getTrans_no() {
        return trans_no;
    }

    public void setTrans_no(String trans_no) {
        this.trans_no = trans_no;
    }

    @Override
    public String toString() {
        return "PaymentGatewayModel{" + "applicationNoAndAmountDetail=" + applicationNoAndAmountDetail + ", totalPayableAmount=" + totalPayableAmount + ", actionCode=" + actionCode + ", makePaymentButtonVisibility=" + makePaymentButtonVisibility + ", verifyButtonVisibility=" + verifyButtonVisibility + ", paymentGatewayPanelVisibility=" + paymentGatewayPanelVisibility + ", paymentId=" + paymentId + ", cartList=" + cartList + ", removeRollBack=" + removeRollBack + ", payGatewayDetailList=" + payGatewayDetailList + ", applicationNumberList=" + applicationNumberList + ", rollBackApplList=" + rollBackApplList + ", purCdList=" + purCdList + ", sessionVariables=" + sessionVariables + ", paymentGatewayDobj=" + paymentGatewayDobj + ", vahan_pgi_url=" + vahan_pgi_url + ", trans_no=" + trans_no + '}';
    }

  
}
