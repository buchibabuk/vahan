/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.rest.model;

import java.util.List;
import nic.vahan.form.bean.PaymentCollectionBean;
import nic.vahan.form.dobj.PaymentCollectionDobj;
import nic.vahan.form.dobj.TmConfigurationPayVerifyDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author Kartikey Singh
 */
public class PaymentCollectionBeanModel {
     private static Logger LOGGER = Logger.getLogger(PaymentCollectionBeanModel.class);
    private List<PaymentCollectionDobj> paymentlist;
    private String bank;
    private String payment_mode;
    private String header_name;
    private boolean render_payment_table;
    private boolean render_button;
    private long balanceAmount;
    private long amountInCash;
    private long excessAmount;
    private long cash;
    private boolean isCashSelected;
    private List instrumentList;
    private PaymentCollectionDobj paydobj;
    private String popUpMessage;
    private boolean paymentCollectionPanelVisibility;
    private boolean renderColumn;
    private TmConfigurationPayVerifyDobj payVerifyDobj;

    public PaymentCollectionBeanModel(PaymentCollectionBean paymentCollectionBean) {
        this.paymentlist = paymentCollectionBean.getPaymentlist();
        this.bank = paymentCollectionBean.getBank();
        this.payment_mode = paymentCollectionBean.getPayment_mode();
        this.header_name = paymentCollectionBean.getHeader_name();
        this.render_payment_table = paymentCollectionBean.isRender_payment_table();
        this.render_button = paymentCollectionBean.isRender_button();
        this.balanceAmount = paymentCollectionBean.getBalanceAmount();
        this.amountInCash = paymentCollectionBean.getAmountInCash();
        this.excessAmount = paymentCollectionBean.getExcessAmount();
        this.cash = paymentCollectionBean.getCash();
        this.isCashSelected = paymentCollectionBean.isIsCashSelected();
//        this.instrumentList = paymentCollectionBean.getInstrumentList();
        this.paydobj = paymentCollectionBean.getPaydobj();
        this.popUpMessage = paymentCollectionBean.getPopUpMessage();
        this.paymentCollectionPanelVisibility = paymentCollectionBean.isPaymentCollectionPanelVisibility();
        this.renderColumn = paymentCollectionBean.isRenderColumn();
        this.payVerifyDobj = paymentCollectionBean.getPayVerifyDobj();
    }
    
    public void setPaymentCollectionBeanFromModel(PaymentCollectionBean paymentCollectionBean) {
        paymentCollectionBean.setPaymentlist(this.getPaymentlist());
        paymentCollectionBean.setBank(this.getBank());
        paymentCollectionBean.setPayment_mode(this.getPayment_mode());
        paymentCollectionBean.setHeader_name(this.getHeader_name());
        paymentCollectionBean.setRender_payment_table(this.isRender_payment_table());
        paymentCollectionBean.setRender_button(this.isRender_button());
        paymentCollectionBean.setBalanceAmount(this.getBalanceAmount());
        paymentCollectionBean.setAmountInCash(this.getAmountInCash());
        paymentCollectionBean.setExcessAmount(this.getExcessAmount());
        paymentCollectionBean.setCash(this.getCash());
        paymentCollectionBean.setIsCashSelected(this.isIsCashSelected());
//        paymentCollectionBean.setInstrumentList(this.getInstrumentList());
        paymentCollectionBean.setPaydobj(this.getPaydobj());
        paymentCollectionBean.setPopUpMessage(this.getPopUpMessage());
        paymentCollectionBean.setPaymentCollectionPanelVisibility(this.isPaymentCollectionPanelVisibility());
        paymentCollectionBean.setRenderColumn(this.isRenderColumn());
        paymentCollectionBean.setPayVerifyDobj(this.getPayVerifyDobj());
    }

    public PaymentCollectionBeanModel() {
    }

    public List<PaymentCollectionDobj> getPaymentlist() {
        return paymentlist;
    }

    public void setPaymentlist(List<PaymentCollectionDobj> paymentlist) {
        this.paymentlist = paymentlist;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public String getHeader_name() {
        return header_name;
    }

    public void setHeader_name(String header_name) {
        this.header_name = header_name;
    }

    public boolean isRender_payment_table() {
        return render_payment_table;
    }

    public void setRender_payment_table(boolean render_payment_table) {
        this.render_payment_table = render_payment_table;
    }

    public boolean isRender_button() {
        return render_button;
    }

    public void setRender_button(boolean render_button) {
        this.render_button = render_button;
    }

    public long getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(long balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public long getAmountInCash() {
        return amountInCash;
    }

    public void setAmountInCash(long amountInCash) {
        this.amountInCash = amountInCash;
    }

    public long getExcessAmount() {
        return excessAmount;
    }

    public void setExcessAmount(long excessAmount) {
        this.excessAmount = excessAmount;
    }

    public long getCash() {
        return cash;
    }

    public void setCash(long cash) {
        this.cash = cash;
    }

    public boolean isIsCashSelected() {
        return isCashSelected;
    }

    public void setIsCashSelected(boolean isCashSelected) {
        this.isCashSelected = isCashSelected;
    }

    public List getInstrumentList() {
        return instrumentList;
    }

    public void setInstrumentList(List instrumentList) {
        this.instrumentList = instrumentList;
    }

    public PaymentCollectionDobj getPaydobj() {
        return paydobj;
    }

    public void setPaydobj(PaymentCollectionDobj paydobj) {
        this.paydobj = paydobj;
    }

    public String getPopUpMessage() {
        return popUpMessage;
    }

    public void setPopUpMessage(String popUpMessage) {
        this.popUpMessage = popUpMessage;
    }

    public boolean isPaymentCollectionPanelVisibility() {
        return paymentCollectionPanelVisibility;
    }

    public void setPaymentCollectionPanelVisibility(boolean paymentCollectionPanelVisibility) {
        this.paymentCollectionPanelVisibility = paymentCollectionPanelVisibility;
    }

    public boolean isRenderColumn() {
        return renderColumn;
    }

    public void setRenderColumn(boolean renderColumn) {
        this.renderColumn = renderColumn;
    }

    public TmConfigurationPayVerifyDobj getPayVerifyDobj() {
        return payVerifyDobj;
    }

    public void setPayVerifyDobj(TmConfigurationPayVerifyDobj payVerifyDobj) {
        this.payVerifyDobj = payVerifyDobj;
    }
    
    
}
