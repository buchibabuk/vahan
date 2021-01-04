/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.reports.CashReceiptReportDobj;
import nic.vahan.form.dobj.reports.CashRecieptSubListDobj;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Mohd Afzal
 */
@ManagedBean(name = "printTaxTokenBean")
@RequestScoped
public class TaxTokenCertificateBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(TaxTokenCertificateBean.class);
    private CashReceiptReportDobj dobj = null;
    private SessionVariables sessionVariables;
    private List<CashRecieptSubListDobj> list = null;
    private boolean dealer = false;
    private boolean newRegis = false;
    private String taxPaidAmount;
    private String taxFrom;
    private String taxUpto;
    private String currAddress;
    private String finePaidAmount;
    private String text;
    private boolean renderqrCode;
    private String totalTaxAmountWithoutFine;

    @PostConstruct
    public void init() {
        try {
            sessionVariables = new SessionVariables();
            if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                    || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
                return;
            }
            Map map;
            Map dealerDetail = null;
            map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            setDobj((CashReceiptReportDobj) map.get("taxTokenDOBJ"));
            map.remove("taxTokenDOBJ");
            if (dobj != null) {
                if (!dobj.getPrntRecieptSubList().isEmpty()) {
                    list = new ArrayList<CashRecieptSubListDobj>();
                    list = dobj.getPrntRecieptSubList();
                    for (CashRecieptSubListDobj dobjTax : list) {
                        if (dobjTax.getPurCd() == 58) {
                            if (dobjTax.getTotal() > 0) {
                                setTaxPaidAmount(String.valueOf(dobjTax.getTotal()));
                                setTotalTaxAmountWithoutFine(String.valueOf(dobjTax.getAmount()));
                                setFinePaidAmount(String.valueOf(dobjTax.getPenalty()));
                                setTaxFrom(dobjTax.getDateFrom().replace("(", ""));
                                setTaxUpto(dobjTax.getDateUpto().replace(")", ""));
                                if ((dobj.getRegnNo() != null && !dobj.getRegnNo().equals("")) && (dobj.getApplNo() != null && !dobj.getApplNo().equals("")) && (dobj.getChasino() != null && !dobj.getChasino().equals("") && (dobj.getReceiptNo() != null && !dobj.getReceiptNo().equals("")))) {
                                    setRenderqrCode(true);
                                    //**********Generating QRCode**********                  
                                    text = "RegnNo:" + dobj.getRegnNo() + " \\nChassis: " + dobj.getChasino() + " \\nRcptNo: " + dobj.getReceiptNo() + "\\nOwner:" + dobj.getOwnerName() + "\\nEngine:" + dobj.getEngno();

                                } else if ((dobj.getApplNo() != null && !dobj.getApplNo().equals("")) && (dobj.getReceiptNo() != null && !dobj.getReceiptNo().equals(""))) {
                                    setRenderqrCode(true);
                                    //**********Generating QRCode**********                  
                                    text = "ApplNo: " + dobj.getApplNo() + " \\nRcptNo: " + dobj.getReceiptNo();
                                }
                            }

                        }
                    }
                    Long user_cd = Long.parseLong(sessionVariables.getEmpCodeLoggedIn());
                    if (sessionVariables.getUserCatgForLoggedInUser().equals(TableConstants.User_Dealer) && sessionVariables.getActionCodeSelected() != TableConstants.TM_ROLE_DEALER_CART_PAYMENT) {
                        dealerDetail = OwnerImpl.getDealerDetail(user_cd);
                        dobj.setDealerName(dealerDetail.get("dealer_name").toString());
                        setDealer(true);
                    }
                    if (dobj != null && dobj.getPrntRecieptSubList() != null && !dobj.getPrntRecieptSubList().isEmpty()) {
                        for (CashRecieptSubListDobj cashReceiptSub : dobj.getPrntRecieptSubList()) {
                            if (cashReceiptSub.getPurpose().equalsIgnoreCase("New Registration")) {
                                setNewRegis(true);
                                break;
                            }
                        }
                    }

//                    if (dobj != null && dobj.getPrntRecieptSubList() != null && !dobj.getPrntRecieptSubList().isEmpty()) {
//                        for (CashRecieptSubListDobj cashReceiptSub : dobj.getPrntRecieptSubList()) {
//                            if (cashReceiptSub.getPurpose().equalsIgnoreCase("New Registration")) {
//                                setNewRegis(true);
//                                break;
//                            }
//                        }
//                    }
                }
                //*************************************
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
     * @return the sessionVariables
     */
    public SessionVariables getSessionVariables() {
        return sessionVariables;
    }

    /**
     * @param sessionVariables the sessionVariables to set
     */
    public void setSessionVariables(SessionVariables sessionVariables) {
        this.sessionVariables = sessionVariables;
    }

    /**
     * @return the list
     */
    public List<CashRecieptSubListDobj> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(List<CashRecieptSubListDobj> list) {
        this.list = list;
    }

    /**
     * @return the taxPaidAmount
     */
    public String getTaxPaidAmount() {
        return taxPaidAmount;
    }

    /**
     * @param taxPaidAmount the taxPaidAmount to set
     */
    public void setTaxPaidAmount(String taxPaidAmount) {
        this.taxPaidAmount = taxPaidAmount;
    }

    /**
     * @return the taxFrom
     */
    public String getTaxFrom() {
        return taxFrom;
    }

    /**
     * @param taxFrom the taxFrom to set
     */
    public void setTaxFrom(String taxFrom) {
        this.taxFrom = taxFrom;
    }

    /**
     * @return the taxUpto
     */
    public String getTaxUpto() {
        return taxUpto;
    }

    /**
     * @param taxUpto the taxUpto to set
     */
    public void setTaxUpto(String taxUpto) {
        this.taxUpto = taxUpto;
    }

    /**
     * @return the currAddress
     */
    public String getCurrAddress() {
        return currAddress;
    }

    /**
     * @param currAddress the currAddress to set
     */
    public void setCurrAddress(String currAddress) {
        this.currAddress = currAddress;
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

    /**
     * @return the newRegis
     */
    public boolean isNewRegis() {
        return newRegis;
    }

    /**
     * @param newRegis the newRegis to set
     */
    public void setNewRegis(boolean newRegis) {
        this.newRegis = newRegis;
    }

    /**
     * @return the finePaidAmount
     */
    public String getFinePaidAmount() {
        return finePaidAmount;
    }

    /**
     * @param finePaidAmount the finePaidAmount to set
     */
    public void setFinePaidAmount(String finePaidAmount) {
        this.finePaidAmount = finePaidAmount;
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

    /**
     * @return the totalTaxAmountWithoutFine
     */
    public String getTotalTaxAmountWithoutFine() {
        return totalTaxAmountWithoutFine;
    }

    /**
     * @param totalTaxAmountWithoutFine the totalTaxAmountWithoutFine to set
     */
    public void setTotalTaxAmountWithoutFine(String totalTaxAmountWithoutFine) {
        this.totalTaxAmountWithoutFine = totalTaxAmountWithoutFine;
    }
}
