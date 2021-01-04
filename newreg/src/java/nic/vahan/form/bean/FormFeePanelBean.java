/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.form.dobj.FeeDobj;
import nic.vahan.form.impl.EpayImpl;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC105
 */
//@ManagedBean(name = "feePanelBeanView")
//@ViewScoped
public class FormFeePanelBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(FormFeePanelBean.class);
    private List<FeeDobj> feeCollectionList = null;
    private List purposeCodeList = null;
    private List<FeeDobj> payableFeeCollectionList = null;
    private long totalFeeAmount;
    private long totalFineAmount;
    private long totalAmount;
    private boolean enable = false;
    private NewVehicleFeebean newBean = null;
    private boolean readOnlyFineAmount = false;

    public FormFeePanelBean() {
        try {
            feeCollectionList = new ArrayList<FeeDobj>();
            purposeCodeList = new ArrayList();
            String[][] data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
            purposeCodeList.add(new SelectItem("-1", "Select Fee"));
            for (int i = 0; i < data.length; i++) {
                purposeCodeList.add(new SelectItem(data[i][0], data[i][1]));
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void reset() {
        resetPaymentList();
        setTotalAmount(0l);
        setTotalFeeAmount(0l);
        setTotalFineAmount(0l);
        setEnable(false);

    }

    public void resetPaymentList() {
        getFeeCollectionList().clear();
        getPayableFeeCollectionList().clear();
        getFeeCollectionList().add(new FeeDobj());
    }

    public void strictResetPaymentList() {
        getFeeCollectionList().clear();
        getPayableFeeCollectionList().clear();

    }

    /**
     * To Add a new in the Fee Collection Panel
     *
     * @param purCd
     */
    public void addNewRow(Integer purCd) {
        String mode = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("actionmode");
        if ("add".equalsIgnoreCase(mode)) {
            if (purCd == null || purCd == -1) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Select Fee Head!", "Select Fee Head!");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                return;
            }
            // add row in the fee panel
            if (getFeeCollectionList().size() == 10) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Maximum number of Fees heads collection reached", "Maximum number of Fees heads collection reached");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            } else {
                getFeeCollectionList().add(new FeeDobj());
                setEnable(false);
            }

        } else if ("minus".equalsIgnoreCase(mode)) {
            // remove current row from table.
            FeeDobj selectedFeeObject = new FeeDobj(purCd);
            int lastIndex = getFeeCollectionList().lastIndexOf(selectedFeeObject);
            if (lastIndex == 0 && getFeeCollectionList().size() == 1) {
                getFeeCollectionList().clear();
                getPayableFeeCollectionList().clear();
                getFeeCollectionList().add(new FeeDobj());
                setTotalFeeAmount(0);
                setTotalFineAmount(0);
                setTotalAmount(0);
            } else {
                getFeeCollectionList().remove(lastIndex);
                getPayableFeeCollectionList().remove(lastIndex);
                calculateTotal();
            }

        }

        for (FeeDobj fee : getPayableFeeCollectionList()) {
            //System.out.println(" Already Selected Fees Payable---  " + fee.getPurCd());

        }

        // Total Number of different Fees allowed is 7 at present
    }

    @PostConstruct
    public void postConstruct() {
    }

    public void calculateTotal() {
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
            getNewBean().updateTotalPayableAmount();
        }
    }

    public void calculateFee(Integer selectedFeeCode, int vehClass, String vehCatg) throws VahanException {

        if (Utility.isNullOrBlank(vehCatg)) {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Unknown Vehicle Category.!", "Unknown Vehicle Category!");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }

        if (selectedFeeCode == -1 || selectedFeeCode == 0) {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Unknown purpose code for fee collection.", "Unable to calculate fee!");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        if (vehClass == -1 || vehClass == 0) {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Unknown vehicle class for fee colletion.", "Unable to calculate fee!");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }

        FeeDobj selectedFeeObject = new FeeDobj(selectedFeeCode);

        if (!getPayableFeeCollectionList().contains(selectedFeeObject)) {
            // List<E_pay_dobj> feeDobj = E_pay_Impl.getFeeDetailsByAction("KL", "NEW", 15, "LMV");
            Long feeValue = EpayImpl.getPurposeCodeFee(null, vehClass, selectedFeeCode, vehCatg);
            getFeeCollectionList().remove(selectedFeeObject);
            selectedFeeObject.setFeeAmount(feeValue);
            selectedFeeObject.setFineAmount(0l);
            selectedFeeObject.setTotalAmount(feeValue + selectedFeeObject.getFineAmount());
            getPayableFeeCollectionList().add(selectedFeeObject);
            getFeeCollectionList().add(selectedFeeObject);
            calculateTotal();
            setEnable(true);

        } else {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Fee Head Already Selected!", "Fee Head Already Selected!");
            FacesContext.getCurrentInstance().addMessage(null, message);
            if (getFeeCollectionList().lastIndexOf(selectedFeeObject) > 0) {
                getFeeCollectionList().remove(getFeeCollectionList().lastIndexOf(selectedFeeObject));
            }

            calculateTotal();

            return;
        }

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
        if (payableFeeCollectionList == null) {
            payableFeeCollectionList = new ArrayList<FeeDobj>();
        }

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

    /**
     * @return the newBean
     */
    public NewVehicleFeebean getNewBean() {
        return newBean;
    }

    /**
     * @param newBean the newBean to set
     */
    public void setNewBean(NewVehicleFeebean newBean) {
        this.newBean = newBean;
    }

    public void addNewFeeDobjToList() {
        FeeDobj feeDobj = new FeeDobj();
        feeDobj.setPurCd(-1);
        //feeDobj.setRenderAddSubPanel(true);
        feeDobj.setDisableDropDown(false);
        getFeeCollectionList().add(new FeeDobj());
    }

    /**
     * @return the readOnlyFineAmount
     */
    public boolean isReadOnlyFineAmount() {
        return readOnlyFineAmount;
    }

    /**
     * @param readOnlyFineAmount the readOnlyFineAmount to set
     */
    public void setReadOnlyFineAmount(boolean readOnlyFineAmount) {
        this.readOnlyFineAmount = readOnlyFineAmount;
    }
}
