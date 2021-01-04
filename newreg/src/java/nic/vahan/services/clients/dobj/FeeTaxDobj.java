/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services.clients.dobj;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import nic.vahan.form.bean.TaxFormPanelBean;
import nic.vahan.form.dobj.EpayDobj;

@XmlRootElement
public class FeeTaxDobj {

    private List<EpayDobj> listOfFees;
    private List<TaxFormPanelBean> listTaxForm;
    private boolean autoRunTaxListener = false;
    private List<DOTaxDetail> taxDescriptionList = new ArrayList<>();
    private List<DOTaxDetail> tem_breakup_list = new ArrayList<>();
    private boolean renderUserChargesAmountPanel;
    private long totalAmountPayable;
    private String errorTaxMessage;
    private boolean errorInTaxService;
    private String errorFeesMessage;
    private boolean errorInFees;

    /**
     * @return the listOfFees
     */
    public List<EpayDobj> getListOfFees() {
        return listOfFees;
    }

    /**
     * @param listOfFees the listOfFees to set
     */
    public void setListOfFees(List<EpayDobj> listOfFees) {
        this.listOfFees = listOfFees;
    }

    /**
     * @return the listTaxForm
     */
    public List<TaxFormPanelBean> getListTaxForm() {
        return listTaxForm;
    }

    /**
     * @param listTaxForm the listTaxForm to set
     */
    public void setListTaxForm(List<TaxFormPanelBean> listTaxForm) {
        this.listTaxForm = listTaxForm;
    }

    /**
     * @return the autoRunTaxListener
     */
    public boolean isAutoRunTaxListener() {
        return autoRunTaxListener;
    }

    /**
     * @param autoRunTaxListener the autoRunTaxListener to set
     */
    public void setAutoRunTaxListener(boolean autoRunTaxListener) {
        this.autoRunTaxListener = autoRunTaxListener;
    }

    /**
     * @return the taxDescriptionList
     */
    public List<DOTaxDetail> getTaxDescriptionList() {
        return taxDescriptionList;
    }

    /**
     * @param taxDescriptionList the taxDescriptionList to set
     */
    public void setTaxDescriptionList(List<DOTaxDetail> taxDescriptionList) {
        this.taxDescriptionList = taxDescriptionList;
    }

    /**
     * @return the tem_breakup_list
     */
    public List<DOTaxDetail> getTem_breakup_list() {
        return tem_breakup_list;
    }

    /**
     * @param tem_breakup_list the tem_breakup_list to set
     */
    public void setTem_breakup_list(List<DOTaxDetail> tem_breakup_list) {
        this.tem_breakup_list = tem_breakup_list;
    }

    public void taxModeListener() {
    }

    public void getBreakUpDetailsByPurpose(TaxFormPanelBean bean) {
    }

    /**
     * @return the renderUserChargesAmountPanel
     */
    public boolean isRenderUserChargesAmountPanel() {
        return renderUserChargesAmountPanel;
    }

    /**
     * @param renderUserChargesAmountPanel the renderUserChargesAmountPanel to
     * set
     */
    public void setRenderUserChargesAmountPanel(boolean renderUserChargesAmountPanel) {
        this.renderUserChargesAmountPanel = renderUserChargesAmountPanel;
    }

    /**
     * @return the totalAmountPayable
     */
    public long getTotalAmountPayable() {
        return totalAmountPayable;
    }

    /**
     * @param totalAmountPayable the totalAmountPayable to set
     */
    public void setTotalAmountPayable(long totalAmountPayable) {
        this.totalAmountPayable = totalAmountPayable;
    }

    /**
     * @return the errorTaxMessage
     */
    public String getErrorTaxMessage() {
        return errorTaxMessage;
    }

    /**
     * @param errorTaxMessage the errorTaxMessage to set
     */
    public void setErrorTaxMessage(String errorTaxMessage) {
        this.errorTaxMessage = errorTaxMessage;
    }

    /**
     * @return the errorInTaxService
     */
    public boolean isErrorInTaxService() {
        return errorInTaxService;
    }

    /**
     * @param errorInTaxService the errorInTaxService to set
     */
    public void setErrorInTaxService(boolean errorInTaxService) {
        this.errorInTaxService = errorInTaxService;
    }

    /**
     * @return the errorFeesMessage
     */
    public String getErrorFeesMessage() {
        return errorFeesMessage;
    }

    /**
     * @param errorFeesMessage the errorFeesMessage to set
     */
    public void setErrorFeesMessage(String errorFeesMessage) {
        this.errorFeesMessage = errorFeesMessage;
    }

    /**
     * @return the errorInFees
     */
    public boolean isErrorInFees() {
        return errorInFees;
    }

    /**
     * @param errorInFees the errorInFees to set
     */
    public void setErrorInFees(boolean errorInFees) {
        this.errorInFees = errorInFees;
    }
}
