/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.vahan.form.dobj.Receipt_Master_dobj;
import nic.vahan.form.impl.Receipt_Master_Impl;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author nic5912
 */
@ManagedBean(name = "rcpt_bean")
@ViewScoped
public class ReceiptMasterBean extends Receipt_Master_dobj implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ReceiptMasterBean.class);
    //boolean show_rcpt_popup;
    String fname = "";

    public void Receipt_Master_bean() {
        this.setBook_no("");

    }

    @PostConstruct
    public void init() {
        try {

            Receipt_Master_dobj dobj = Receipt_Master_Impl.getReceipt_Master_dobj();
            if (dobj != null) {
                this.setBook_rcpt_no(dobj.getBook_rcpt_no());
                this.setBook_no(dobj.getBook_no());
                this.setCurrent_rcpt_no(dobj.getCurrent_rcpt_no());
                this.setRcpt_start(dobj.getRcpt_start());
                this.setRcpt_end(dobj.getRcpt_end());
                this.setNew_rcpt_no(dobj.getNew_rcpt_no());
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

    }

    public void popUpSkipRecptNo() {

        try {
            Map<String, Object> options = new HashMap<String, Object>();
            options.put("modal", true);
            options.put("draggable", true);
            options.put("resizable", false);
            options.put("closable", true);
            options.put("contentHeight", 240);
            options.put("contentWidth", 500);
            PrimeFaces.current().dialog().openDynamic("skip_rcpto", options, null);


        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

    }

    public void closePopUpSkipRecptNo() {

        try {
            PrimeFaces.current().dialog().closeDynamic(null);

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

    }

    public void popUpSkipRecptNoReturn() {
        //Empty Method
    }

    public void skipRecptNo() {

        try {
            int newrcptNo = 0;
            if (this.getNew_rcpt_no() != null) {
                newrcptNo = this.getNew_rcpt_no() + 1;
            } else {
                newrcptNo = this.getCurrent_rcpt_no() + 1;
            }
            this.setNew_rcpt_no(newrcptNo);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

    }

    public void saveNewRcptNo() {
        try {
            Receipt_Master_Impl.saveNewRcptNo(this.getNew_rcpt_no());
            reset();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

    }

    public void reset() {
        Receipt_Master_dobj dobj = Receipt_Master_Impl.getReceipt_Master_dobj();
        if (dobj != null) {
            this.setBook_rcpt_no(dobj.getBook_rcpt_no());
            this.setBook_no(dobj.getBook_no());
            this.setCurrent_rcpt_no(dobj.getCurrent_rcpt_no());
            this.setRcpt_start(dobj.getRcpt_start());
            this.setRcpt_end(dobj.getRcpt_end());
            this.setNew_rcpt_no(dobj.getNew_rcpt_no());
        }
    }
}
