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
@ManagedBean(name = "payCashReceiptBean")
@RequestScoped
public class PayCashReceiptBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(PayCashReceiptBean.class);
    private CashReceiptReportDobj dobj = null;
    private CashRecieptSubListDobj dobjsublist = null;
    private boolean dealer = false;
    private boolean newRegis = false;
    private String backButton;
    private boolean showSurcharge = false;
    private boolean showExcessamt = false;
    private boolean showCash_amt = false;
    private boolean showRebate = false;
    private boolean showPrv_adjst = false;
    private boolean showinterest = false;
    private boolean showexempted = false;
    private boolean showfine = false;
    private String text;
    List<CashRecieptSubListDobj> list = null;
    private boolean renderqrCode = false;

    public PayCashReceiptBean() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        Map map;
        Map dealerDetail = null;
        map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        String rcpt_no = (String) map.get("rcptno");

        try {
            dobj = PrintDocImpl.getCashReceiptReportDobj(rcpt_no);
            if (dobj != null) {
                if (!dobj.getPrntRecieptSubList().isEmpty()) {
                    list = new ArrayList<CashRecieptSubListDobj>();
                    list = dobj.getPrntRecieptSubList();
                    for (CashRecieptSubListDobj dobj : list) {
                        if (dobj.getBlnissurcharge() != null) {
                            if (!showSurcharge) {
                                showSurcharge = dobj.getBlnissurcharge();
                            }
                        }
                        if (dobj.getBlnisexcessAmt() != null) {
                            if (!showExcessamt) {
                                showExcessamt = dobj.getBlnisexcessAmt();
                            }
                        }
                        if (dobj.getBlniscashAmt() != null) {
                            if (!showCash_amt) {
                                showCash_amt = dobj.getBlniscashAmt();
                            }
                        }
                        if (dobj.getBlnisrebate() != null) {
                            if (!showRebate) {
                                showRebate = dobj.getBlnisrebate();
                            }
                        }

                        if (dobj.getBlnisprv_adjustment() != null) {
                            if (!showPrv_adjst) {
                                showPrv_adjst = dobj.getBlnisprv_adjustment();
                            }
                        }

                        if (dobj.getBlnisinterest() != null) {
                            if (!showinterest) {
                                showinterest = dobj.getBlnisinterest();
                            }
                        }

                        if (dobj.getBlnisexempted() != null) {
                            if (!showexempted) {
                                showexempted = dobj.getBlnisexempted();
                            }
                        }
                        if (dobj.getBlnisfine() != null) {
                            if (!showfine) {
                                showfine = dobj.getBlnisfine();
                            }
                        }
                    }
                }
                //String user_catg = Util.getUserCategory();
                //  Long user_cd = Long.parseLong(Util.getEmpCode());
//                if (user_catg.equals(TableConstants.User_Dealer)) {
//                    dealerDetail = OwnerImpl.getDealerDetail(user_cd);
//                    dobj.setDealerName(dealerDetail.get("dealer_name").toString());
//                    dealer = true;
//                    backButton = "/ui/dealer/form_printDealerReports.xhtml?faces-redirect=true";
//                } else {
//                    if (Util.getSelectedSeat().getAction_cd() == TableConstants.TM_ROLE_TAX_COLLECTION) {
//                        backButton = "/ui/tax/form_road_tax_collection.xhtml?faces-redirect=true";
//                    } else {
//                        backButton = "home";
//                    }
//
//                }

                if (dobj != null && dobj.getPrntRecieptSubList() != null && !dobj.getPrntRecieptSubList().isEmpty()) {
                    for (CashRecieptSubListDobj cashReceiptSub : dobj.getPrntRecieptSubList()) {
                        if (cashReceiptSub.getPurpose().equalsIgnoreCase("New Registration")) {
                            newRegis = true;
                            break;
                        }
                    }
                }
                if ((dobj.getRegnNo() != null && !dobj.getRegnNo().equals("")) && (dobj.getApplNo() != null && !dobj.getApplNo().equals("")) && (dobj.getChasino() != null && !dobj.getChasino().equals("") && (dobj.getReceiptNo() != null && !dobj.getReceiptNo().equals("")))) {
                    renderqrCode = true;
                    //**********Generating QRCode**********
                    text = "RegnNo:" + dobj.getRegnNo() + " \\nChassis: " + dobj.getChasino() + " \\nRcptNo: " + dobj.getReceiptNo() + "\\nOwner:" + dobj.getOwnerName() + "\\nEngine:" + dobj.getEngno();
                } else if ((dobj.getApplNo() != null && !dobj.getApplNo().equals("")) && (dobj.getReceiptNo() != null && !dobj.getReceiptNo().equals(""))) {
                    renderqrCode = true;
                    //**********Generating QRCode**********
                    text = "ApplNo: " + dobj.getApplNo() + " \\nRcptNo: " + dobj.getReceiptNo();
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
     * @return the showRebate
     */
    public boolean isShowRebate() {
        return showRebate;
    }

    /**
     * @param showRebate the showRebate to set
     */
    public void setShowRebate(boolean showRebate) {
        this.showRebate = showRebate;
    }

    /**
     * @return the showPrv_adjst
     */
    public boolean isShowPrv_adjst() {
        return showPrv_adjst;
    }

    /**
     * @param showPrv_adjst the showPrv_adjst to set
     */
    public void setShowPrv_adjst(boolean showPrv_adjst) {
        this.showPrv_adjst = showPrv_adjst;
    }

    /**
     * @return the showinterest
     */
    public boolean isShowinterest() {
        return showinterest;
    }

    /**
     * @param showinterest the showinterest to set
     */
    public void setShowinterest(boolean showinterest) {
        this.showinterest = showinterest;
    }

    /**
     * @return the showexempted
     */
    public boolean isShowexempted() {
        return showexempted;
    }

    /**
     * @param showexempted the showexempted to set
     */
    public void setShowexempted(boolean showexempted) {
        this.showexempted = showexempted;
    }

    /**
     * @return the showfine
     */
    public boolean isShowfine() {
        return showfine;
    }

    /**
     * @param showfine the showfine to set
     */
    public void setShowfine(boolean showfine) {
        this.showfine = showfine;
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
