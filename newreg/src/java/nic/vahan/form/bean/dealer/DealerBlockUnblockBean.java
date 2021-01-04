/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.dealer;

import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.Dealer;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.dealer.DealerBlockUnblockImpl;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "dealerBlckUnblckBean")
@ViewScoped
public class DealerBlockUnblockBean implements Serializable {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(DealerBlockUnblockImpl.class);
    private List<Dealer> dealerBlkedList;
    private List<Dealer> dealerUnBlkedList;
    private List<Dealer> filteredList;
    private String successBlockUnBlockMessg = "";

    public DealerBlockUnblockBean() {
        try {
            DealerBlockUnblockImpl dealerBlkImpl = new DealerBlockUnblockImpl();
            dealerUnBlkedList = dealerBlkImpl.getDealerUnBlockedList(Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
            dealerBlkedList = dealerBlkImpl.getDealerBlockedList(Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void blkDlrDetails() {
        try {
            int countChecked = 0;
            int successBlocked = 0;
            for (int i = 0; i < getDealerUnBlkedList().size(); i++) {
                if (getDealerUnBlkedList().get(i).isBlockUnBlockStatus()) {
                    countChecked++;
                    break;
                }
            }
            if (countChecked > 0) {
                DealerBlockUnblockImpl dealerBlkImpl = new DealerBlockUnblockImpl();
                successBlocked = dealerBlkImpl.blockDealer(getDealerUnBlkedList());
                dealerUnBlkedList = dealerBlkImpl.getDealerUnBlockedList(Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                dealerBlkedList = dealerBlkImpl.getDealerBlockedList(Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
            } else {
                throw new VahanException("Please Select atleast one Dealer to Block");
            }

            if (successBlocked > 0) {
                successBlockUnBlockMessg = "Selected Dealers are Successfully Blocked";
                PrimeFaces.current().ajax().update("blockUnblockDealer:showMessg");
                PrimeFaces.current().executeScript("PF('successDialog').show()");
            }
        } catch (VahanException vmex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vmex.getMessage(), vmex.getMessage()));
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void unBlkDlrDetails() {
        try {
            int countChecked = 0;
            int successUnBlocked = 0;
            for (int i = 0; i < getDealerBlkedList().size(); i++) {
                if (getDealerBlkedList().get(i).isBlockUnBlockStatus()) {
                    countChecked++;
                    break;
                }
            }
            if (countChecked > 0) {
                DealerBlockUnblockImpl dealerBlkImpl = new DealerBlockUnblockImpl();
                successUnBlocked = dealerBlkImpl.unBlockDealer(getDealerBlkedList());
                dealerBlkedList = dealerBlkImpl.getDealerBlockedList(Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                dealerUnBlkedList = dealerBlkImpl.getDealerUnBlockedList(Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
            } else {
                throw new VahanException("Please Select atleast one Dealer to UnBlock");
            }
            if (successUnBlocked > 0) {
                successBlockUnBlockMessg = "Selected Dealers are Successfully UnBlocked";
                PrimeFaces.current().ajax().update("blockUnblockDealer:showMessg");
                PrimeFaces.current().executeScript("PF('successDialog').show()");
            }
        } catch (VahanException vmex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vmex.getMessage(), vmex.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    /**
     * @return the filteredList
     */
    public List<Dealer> getFilteredList() {
        return filteredList;
    }

    /**
     * @param filteredList the filteredList to set
     */
    public void setFilteredList(List<Dealer> filteredList) {
        this.filteredList = filteredList;
    }

    /**
     * @return the dealerBlkedList
     */
    public List<Dealer> getDealerBlkedList() {
        return dealerBlkedList;
    }

    /**
     * @param dealerBlkedList the dealerBlkedList to set
     */
    public void setDealerBlkedList(List<Dealer> dealerBlkedList) {
        this.dealerBlkedList = dealerBlkedList;
    }

    /**
     * @return the dealerUnBlkedList
     */
    public List<Dealer> getDealerUnBlkedList() {
        return dealerUnBlkedList;
    }

    /**
     * @param dealerUnBlkedList the dealerUnBlkedList to set
     */
    public void setDealerUnBlkedList(List<Dealer> dealerUnBlkedList) {
        this.dealerUnBlkedList = dealerUnBlkedList;
    }

    /**
     * @return the successBlockUnBlockMessg
     */
    public String getSuccessBlockUnBlockMessg() {
        return successBlockUnBlockMessg;
    }

    /**
     * @param successBlockUnBlockMessg the successBlockUnBlockMessg to set
     */
    public void setSuccessBlockUnBlockMessg(String successBlockUnBlockMessg) {
        this.successBlockUnBlockMessg = successBlockUnBlockMessg;
    }
}
