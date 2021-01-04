/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.user_mgmt.dobj.ReceiptMasterDobj;
import nic.vahan.db.user_mgmt.impl.ReceiptMasterImpl;
import nic.vahan.form.impl.Util;

/**
 *
 * @author tranC103
 */
@ManagedBean
@ViewScoped
public class ReceiptMasterBean implements Serializable {
    
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ReceiptMasterBean.class);
    private ReceiptMasterDobj receiptDobj;
    private String selectBook;
    private boolean panel1 = false;
    private boolean panel2 = false;
    private boolean panel3 = false;
    private boolean savePanelRender = false;
    private boolean disabled = true;
    private String stateCode;
    private int offCode;
    private List<ReceiptMasterDobj> expiredList = new ArrayList<>();
    private List<ReceiptMasterDobj> issuedList = new ArrayList<>();
    ReceiptMasterImpl impl = new ReceiptMasterImpl();
    private List userList = new ArrayList();
    private List expireList = new ArrayList();
    
    public ReceiptMasterBean() {
        receiptDobj = new ReceiptMasterDobj();
    }
    
    @PostConstruct
    public void init() {
        stateCode = Util.getUserStateCode();
        offCode = Util.getSelectedSeat().getOff_cd();
        receiptDobj.setStateCode(stateCode);
        receiptDobj.setOffCode(offCode);
        receiptDobj.setRcptCurrent(1);
        setDisabled(true);
        setPanel3(true);
        selectBook = "N";
        if (panel3) {
            setSavePanelRender(true);
//            if (stateCode.equalsIgnoreCase("DL")) {
            userList.add(new SelectItem("0", "For All Cashiers"));
//            } else {
//                userList.clear();
//            }
//            try {
//                userList = impl.getUserData(stateCode, offCode, userList);
//            } catch (VahanException ex) {
//                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
//            }
        }
        expireList.add(new SelectItem("Y", "Y"));
        expireList.add(new SelectItem("N", "N"));
    }
    
    public void receiptBookListener() {
        if (selectBook.equalsIgnoreCase("R")) {
            setPanel1(true);
            setPanel2(false);
            setPanel3(false);
            setSavePanelRender(false);
            fillDataTableIssued();
        } else if (selectBook.equalsIgnoreCase("E")) {
            setPanel1(false);
            setPanel3(false);
            setPanel2(true);
            setSavePanelRender(false);
            fillDataTableExpired();
        } else if (selectBook.equalsIgnoreCase("N")) {
            setPanel1(false);
            setPanel2(false);
            setPanel3(true);
            setSavePanelRender(true);
        }
    }
    
    public void verifyUser() {
        boolean flag = false;
        try {
            flag = impl.verifyNewBook(receiptDobj);
        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        if (flag) {
            setDisabled(false);
        } else {
            setDisabled(true);
            JSFUtils.setFacesMessage("User already have assigned Book Number.", null, JSFUtils.WARN);
        }
    }
    
    public void checkSeries() {
        if (receiptDobj.getRcptEnd() > receiptDobj.getRcptStart()) {
        } else {
            reset();
            JSFUtils.setFacesMessage("Invalid Series (End number should be greater than start number. )", null, JSFUtils.WARN);
        }
    }
    
    public void startAtListener() {
        boolean flag = false;
        if (receiptDobj.getRcptStart() != 0) {
            receiptDobj.setRcptCurrent(receiptDobj.getRcptStart());
        }
        try {
            flag = impl.verifyLowerBound(receiptDobj);
            if (flag) {
                setDisabled(!flag);
            } else {
                JSFUtils.setFacesMessage("Invalid Lower Bound (Please check Running & Expired receipt numbers under this book)", null, JSFUtils.WARN);
                setDisabled(!flag);
                reset();
            }
        } catch (VahanException ex) {
            JSFUtils.showMessage(ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            JSFUtils.showMessage(TableConstants.SomthingWentWrong);
        }
    }
    
    public void validateBookName() {
        boolean flag = false;
        try {
            flag = impl.uniqueBookNumber(receiptDobj);
        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        if (flag) {
        } else {
            JSFUtils.setFacesMessage("Invalid Book Number(Already assigned)", null, JSFUtils.WARN);
            setDisabled(true);
            reset();
        }
    }
    
    public void issueNewBook() {
        try {
            if (panel3) {
                boolean flag = false;
                boolean verifyBookFlag = impl.verifyNewBook(receiptDobj);
                boolean verifySeries = impl.verifySeries(receiptDobj);
                if (verifyBookFlag && verifySeries) {
                    checkSeries();
                    flag = impl.saveNewBook(receiptDobj);
                    if (flag) {
                        JSFUtils.setFacesMessage("New Book Issued Successfully", null, JSFUtils.INFO);
                    } else {
                        JSFUtils.setFacesMessage("Technical Error", null, JSFUtils.INFO);
                    }
                } else {
                    if (verifySeries) {
                        JSFUtils.setFacesMessage("Alredy assigned series", null, JSFUtils.INFO);
                    }
                    JSFUtils.setFacesMessage("This User Already have assigned book", null, JSFUtils.INFO);
                }
                reset();
            }
        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.setFacesMessage("Technical Error in Database", null, JSFUtils.ERROR);
        }
    }
    
    private void fillDataTableExpired() {
        expiredList.clear();
        try {
            impl.getExpiredRows(receiptDobj, expiredList);
        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        
    }
    
    private void fillDataTableIssued() {
        issuedList.clear();
        try {
            impl.getIssuedRows(receiptDobj, issuedList);
        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }
    
    public void onRowEdit(ReceiptMasterDobj dobj) {
        dobj.setExpiredFlag(receiptDobj.getExpiredFlag());
        try {
            if (receiptDobj.getExpiredFlag().equalsIgnoreCase("Y")) {
                impl.updateEditableRow(dobj);
            }
        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }
    
    public void reset() {
        receiptDobj.setRcptUserCode(-1);
        receiptDobj.setRcptPrefix(null);
        receiptDobj.setRcptStart(0);
        receiptDobj.setRcptCurrent(1);
        receiptDobj.setRcptEnd(0);
    }
    
    public String getSelectBook() {
        return selectBook;
    }
    
    public void setSelectBook(String selectBook) {
        this.selectBook = selectBook;
    }
    
    public boolean isPanel1() {
        return panel1;
    }
    
    public void setPanel1(boolean panel1) {
        this.panel1 = panel1;
    }
    
    public boolean isPanel2() {
        return panel2;
    }
    
    public void setPanel2(boolean panel2) {
        this.panel2 = panel2;
    }
    
    public boolean isPanel3() {
        return panel3;
    }
    
    public void setPanel3(boolean panel3) {
        this.panel3 = panel3;
    }
    
    public String getStateCode() {
        return stateCode;
    }
    
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }
    
    public int getOffCode() {
        return offCode;
    }
    
    public void setOffCode(int offCode) {
        this.offCode = offCode;
    }
    
    public List getUserList() {
        return userList;
    }
    
    public void setUserList(List userList) {
        this.userList = userList;
    }
    
    public ReceiptMasterDobj getReceiptDobj() {
        return receiptDobj;
    }
    
    public void setReceiptDobj(ReceiptMasterDobj receiptDobj) {
        this.receiptDobj = receiptDobj;
    }
    
    public List<ReceiptMasterDobj> getExpiredList() {
        return expiredList;
    }
    
    public void setExpiredList(List<ReceiptMasterDobj> expiredList) {
        this.expiredList = expiredList;
    }
    
    public List<ReceiptMasterDobj> getIssuedList() {
        return issuedList;
    }
    
    public void setIssuedList(List<ReceiptMasterDobj> issuedList) {
        this.issuedList = issuedList;
    }
    
    public boolean isSavePanelRender() {
        return savePanelRender;
    }
    
    public void setSavePanelRender(boolean savePanelRender) {
        this.savePanelRender = savePanelRender;
    }
    
    public List getExpireList() {
        return expireList;
    }
    
    public void setExpireList(List expireList) {
        this.expireList = expireList;
    }
    
    public boolean isDisabled() {
        return disabled;
    }
    
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
