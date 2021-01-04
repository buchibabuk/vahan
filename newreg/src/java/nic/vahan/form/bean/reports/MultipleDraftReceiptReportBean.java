/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.form.dobj.PaymentCollectionDobj;
import nic.vahan.form.dobj.SingleDraftPaymentDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author nic
 */
@ManagedBean(name = "draftRcptReportBean")
@RequestScoped
public class MultipleDraftReceiptReportBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(MultipleDraftReceiptReportBean.class);

    /**
     * @return the LOGGER
     */
    public static Logger getLOGGER() {
        return LOGGER;
    }

    /**
     * @param aLOGGER the LOGGER to set
     */
    public static void setLOGGER(Logger aLOGGER) {
        LOGGER = aLOGGER;
    }
    private List<SingleDraftPaymentDobj> list = new ArrayList<SingleDraftPaymentDobj>();
    private List<PaymentCollectionDobj> paymentlist = new ArrayList<PaymentCollectionDobj>();
    private List instrumentList = null;
    private List bank_list = null;

    public MultipleDraftReceiptReportBean() {
        String[][] instrmentType = MasterTableFiller.masterTables.TM_INSTRUMENTS.getData();
        String[][] data = MasterTableFiller.masterTables.TM_BANK.getData();
        instrumentList = new ArrayList();
        instrumentList.add(new SelectItem("-1", "Select"));
        for (int i = 0; i < instrmentType.length; i++) {
            instrumentList.add(new SelectItem(instrmentType[i][0], instrmentType[i][1]));
        }

        bank_list = new ArrayList();
        bank_list.add(new SelectItem("-1", "Select Bank"));
        for (int i = 0; i < data.length; i++) {
            bank_list.add(new SelectItem(data[i][0], data[i][1]));
        }
        Map map;
        map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        list = (List<SingleDraftPaymentDobj>) map.get("list");
        paymentlist = (List<PaymentCollectionDobj>) map.get("paymentlist");
        // String quotationId = (String) sessionMap.get("quotationId");
//        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
//        Map<String, Object> sessionMap = externalContext.getSessionMap();
//        String quotationId = (String) sessionMap.get("quotationId");
//        


    }

    /**
     * @return the list
     */
    public List<SingleDraftPaymentDobj> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(List<SingleDraftPaymentDobj> list) {
        this.list = list;
    }

    /**
     * @return the paymentlist
     */
    public List<PaymentCollectionDobj> getPaymentlist() {
        return paymentlist;
    }

    /**
     * @param paymentlist the paymentlist to set
     */
    public void setPaymentlist(List<PaymentCollectionDobj> paymentlist) {
        this.paymentlist = paymentlist;
    }

    /**
     * @return the instrumentList
     */
    public List getInstrumentList() {
        return instrumentList;
    }

    /**
     * @param instrumentList the instrumentList to set
     */
    public void setInstrumentList(List instrumentList) {
        this.instrumentList = instrumentList;
    }

    /**
     * @return the bank_list
     */
    public List getBank_list() {
        return bank_list;
    }

    /**
     * @param bank_list the bank_list to set
     */
    public void setBank_list(List bank_list) {
        this.bank_list = bank_list;
    }
}
