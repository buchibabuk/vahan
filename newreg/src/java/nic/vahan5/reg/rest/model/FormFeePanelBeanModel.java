/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.rest.model;

import java.util.List;
import nic.vahan.form.bean.FormFeePanelBean;
import nic.vahan.form.bean.NewVehicleFeebean;
import nic.vahan.form.dobj.FeeDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author Kartikey Singh
 */
public class FormFeePanelBeanModel {

    private static final Logger LOGGER = Logger.getLogger(FormFeePanelBeanModel.class);
    private List<FeeDobj> feeCollectionList;
    private List purposeCodeList;
    private List<FeeDobj> payableFeeCollectionList;
    private long totalFeeAmount;
    private long totalFineAmount;
    private long totalAmount;
    private boolean enable;
    private NewVehicleFeeBeanModel newBean;
    private boolean readOnlyFineAmount;

    public FormFeePanelBeanModel(FormFeePanelBean formFeePanelBean) {
        this.feeCollectionList = formFeePanelBean.getFeeCollectionList();
//        this.purposeCodeList = formFeePanelBean.getPurposeCodeList();
        this.payableFeeCollectionList = formFeePanelBean.getPayableFeeCollectionList();
        this.totalFeeAmount = formFeePanelBean.getTotalFeeAmount();
        this.totalFineAmount = formFeePanelBean.getTotalFineAmount();
        this.totalAmount = formFeePanelBean.getTotalAmount();
        this.enable = formFeePanelBean.isEnable();
        // Need to change this
        if (formFeePanelBean.getNewBean() != null) {
//            this.newBean = new NewVehicleFeeBeanModel(formFeePanelBean.getNewBean());
            this.newBean = null;
        }
        this.readOnlyFineAmount = formFeePanelBean.isReadOnlyFineAmount();
    }

    public void setFormFeePanelBeanFromModel(FormFeePanelBean formFeePanelBean) {
        formFeePanelBean.setFeeCollectionList(this.getFeeCollectionList());
//        formFeePanelBean.setPurposeCodeList(this.getPurposeCodeList());
        formFeePanelBean.setPayableFeeCollectionList(this.getPayableFeeCollectionList());
        formFeePanelBean.setTotalFeeAmount(this.getTotalFeeAmount());
        formFeePanelBean.setTotalFineAmount(this.getTotalFineAmount());
        formFeePanelBean.setTotalAmount(this.getTotalAmount());
        formFeePanelBean.setEnable(this.isEnable());
        // Need to change this
        if (this.getNewBean() != null) {
            // Need to fix the cyclic dependency
        }
        formFeePanelBean.setReadOnlyFineAmount(this.isReadOnlyFineAmount());
    }

    public void strictResetPaymentList() {
        getFeeCollectionList().clear();
        getPayableFeeCollectionList().clear();
    }

    public void calculateTotal(TmConfigurationDobj tmConfigDobj, SessionVariablesModel sessionVariablesModel) {
        long sumOfFee = 0;
        long sumOfFine = 0;
        long sumOfTotal = 0;

        for (FeeDobj sumDobj : getFeeCollectionList()) {
            sumOfFee = sumOfFee + (sumDobj.getFeeAmount() == null ? 0l : sumDobj.getFeeAmount());
            sumOfFine = sumOfFine + (sumDobj.getFineAmount() == null ? 0l : sumDobj.getFineAmount());
            sumOfTotal = sumOfTotal + sumDobj.getTotalAmount();
            setTotalFeeAmount(sumOfFee);
            setTotalFineAmount(sumOfFine);
            setTotalAmount(sumOfTotal);
        }
        if (getNewBean() != null) {
            //getNewBean().setTotalAmountPayable(getNewBean().getTotalAmountPayable() + sumOfTotal);
            getNewBean().updateTotalPayableAmount(tmConfigDobj, sessionVariablesModel);
        }
    }

    public FormFeePanelBeanModel() {
    }

    public List<FeeDobj> getFeeCollectionList() {
        return feeCollectionList;
    }

    public void setFeeCollectionList(List<FeeDobj> feeCollectionList) {
        this.feeCollectionList = feeCollectionList;
    }

    public List getPurposeCodeList() {
        return purposeCodeList;
    }

    public void setPurposeCodeList(List purposeCodeList) {
        this.purposeCodeList = purposeCodeList;
    }

    public List<FeeDobj> getPayableFeeCollectionList() {
        return payableFeeCollectionList;
    }

    public void setPayableFeeCollectionList(List<FeeDobj> payableFeeCollectionList) {
        this.payableFeeCollectionList = payableFeeCollectionList;
    }

    public long getTotalFeeAmount() {
        return totalFeeAmount;
    }

    public void setTotalFeeAmount(long totalFeeAmount) {
        this.totalFeeAmount = totalFeeAmount;
    }

    public long getTotalFineAmount() {
        return totalFineAmount;
    }

    public void setTotalFineAmount(long totalFineAmount) {
        this.totalFineAmount = totalFineAmount;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public NewVehicleFeeBeanModel getNewBean() {
        return newBean;
    }

    public void setNewBean(NewVehicleFeeBeanModel newBean) {
        this.newBean = newBean;
    }

    public boolean isReadOnlyFineAmount() {
        return readOnlyFineAmount;
    }

    public void setReadOnlyFineAmount(boolean readOnlyFineAmount) {
        this.readOnlyFineAmount = readOnlyFineAmount;
    }
}
