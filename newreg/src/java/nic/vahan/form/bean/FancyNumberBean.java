/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.swing.event.ChangeEvent;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.form.dobj.FancyNumberDobj;
import nic.vahan.form.impl.FancyNumberImpl;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author nic5912
 */
@ManagedBean(name = "fancy")
@ViewScoped
public class FancyNumberBean extends FancyNumberDobj implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(FancyAuctionEntryBean.class);
    private ArrayList listC_village = null;
    private ArrayList listC_taluk = null;
    private ArrayList listC_dist = null;
    private String SELECT_LABEL1 = "Reservation of New Registration Number";
    private String SELECT_LABEL2 = "Reservation of Lapsed Registration Number";
    private String SELECT_LABEL3 = "Payment of Balance of Auction Money";
    private String SELECT_VALUE1 = "1";
    private String SELECT_VALUE2 = "2";
    private String SELECT_VALUE3 = "3";
    private String rb_select_one = SELECT_VALUE1;//default Value
    private boolean saved_success = false;
    private boolean rcpt_view = false;
    private boolean bal_rcpt_view = false;
    @ManagedProperty(value = "#{rcpt_bean}")
    private ReceiptMasterBean rcpt_bean;

    @PostConstruct
    public void init() {

        listC_village = new ArrayList();
        listC_taluk = new ArrayList();
        listC_dist = new ArrayList();

//        String[][] data = MasterTableFiller.masterTables.TM_VILLAGE.getData();
//        for (int i = 0; i < data.length; i++) {
//            listC_village.add(new SelectItem(data[i][2], data[i][3]));
//
//        }
//
//        data = MasterTableFiller.masterTables.TM_TALUK.getData();
//        for (int i = 0; i < data.length; i++) {
//            listC_taluk.add(new SelectItem(data[i][1], data[i][2]));
//
//        }

        String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
        for (int i = 0; i < data.length; i++) {
            listC_dist.add(new SelectItem(data[i][0], data[i][1]));

        }


    }

    public void selectOneradioListener(AjaxBehaviorEvent actionEvent) {
        reset();
        if (getRb_select_one().equals(SELECT_VALUE1)) {
            setRcpt_view(false);
            setBal_rcpt_view(false);
        } else if (getRb_select_one().equals(SELECT_VALUE3)) {

            setRcpt_view(false);
            setBal_rcpt_view(true);
        }

    }

    public void regno_focusLost(AjaxBehaviorEvent actionEvent) {
        boolean flg = false;
        Exception e = null;
        try {
            if ((getRegn_no() == null) || getRegn_no().equals("") || rb_select_one.equals(SELECT_VALUE3)) {
                return;
            }



            if (getRegn_no().length() != 10) {
                throw new VahanException("Registration number length is less then 10");
            }
            String off_cd = Util.getSession().getAttribute("selected_off_cd").toString().trim();
            int offcd = Integer.parseInt(off_cd);
            FancyNumberImpl.verifyFancy_RegnNo(getRegn_no(), offcd);

            if (FancyNumberImpl.isNumberBooked(getRegn_no())) {
                PrimeFaces.current().executeScript("choice" + ".show()");
                flg = true;
            }

            if (!flg) {
                //Get Fancy Fee
                int fancy_amt = FancyNumberImpl.getAmount(getRegn_no());
                setReserve_amt(fancy_amt);

            }
        } catch (VahanException ve) {
            e = ve;
        } catch (Exception ee) {
            e = ee;
        }

        if (e != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage(), e.getMessage()));
            reset();
        }

    }

    public void rcptNo_focusLost(AjaxBehaviorEvent actionEvent) {

        Exception e = null;
        try {
            if ((getBal_rcptno() == null) || getBal_rcptno().equals("") || !rb_select_one.equals(SELECT_VALUE3)) {
                FacesContext.getCurrentInstance().renderResponse();
                return;
            }
            FancyNumberDobj dobj = FancyNumberImpl.get_fancy_bal(getBal_rcptno());
            if (dobj != null) {

                setRegn_appl_no(dobj.getRegn_appl_no());
                setRegn_no(dobj.getRegn_no());

                setOwner_name(dobj.getOwner_name());
                setC_add1(dobj.getC_add1());
                setC_add2(dobj.getC_add2());
                setChasi_no(dobj.getChasi_no());
                setC_district(dobj.getC_district());
                setC_taluk(dobj.getC_taluk());
                setC_village(dobj.getC_village());
                setC_pincode(dobj.getC_pincode());
                setReserve_amt(dobj.getBal_amt());

            }



        } catch (VahanException ve) {
            e = ve;


        } catch (Exception ee) {
            e = ee;

        }

        if (e != null) {
            reset();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage(), e.getMessage()));

        }

    }

    public void applno_focusLost(AjaxBehaviorEvent actionEvent) {

        Exception e = null;
        try {


            if ((getRegn_appl_no() == null) || getRegn_appl_no().equals("") || rb_select_one.equals(SELECT_VALUE3)) {
                FacesContext.getCurrentInstance().renderResponse();
                return;
            }
            FancyNumberDobj dobj = FancyNumberImpl.get_fancy_appl_no(getRegn_appl_no());
            if (dobj != null) {
                setOwner_name(dobj.getOwner_name());
                setC_add1(dobj.getC_add1());
                setC_add2(dobj.getC_add2());
                setChasi_no(dobj.getChasi_no());
                setC_district(dobj.getC_district());
                setC_taluk(dobj.getC_taluk());
                setC_village(dobj.getC_village());
                setC_pincode(dobj.getC_pincode());

            }



        } catch (VahanException ve) {
            e = ve;


        } catch (Exception ee) {
            e = ee;

        }

        if (e != null) {
            reset();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage(), e.getMessage()));

        }

    }

    public void saveFancyNo() {
        Exception e = null;
        String rcpt = null;
        try {

            validate();
            String state_cd = Util.getSession().getAttribute("state_cd").toString();
            int offcd = Integer.parseInt(Util.getSession().getAttribute("selected_off_cd").toString().trim());


            if (rb_select_one.equals(SELECT_VALUE1)) {
                rcpt = FancyNumberImpl.saveFancyDetail(this, state_cd, offcd);
                setRecp_no(rcpt);
                setRcpt_view(true);
            } else if (rb_select_one.equals(SELECT_VALUE3)) {//save balance amount
                rcpt = FancyNumberImpl.saveBalanceFancyAmount(this, state_cd, offcd);
                setRecp_no(rcpt);
                setRcpt_view(true);
            }

            setSaved_success(true);
            rcpt_bean.reset();


        } catch (VahanException ve) {
            e = ve;
        } catch (Exception ee) {
            e = ee;
        }

        if (e != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage(), e.getMessage()));
        }
    }

    public void validate() throws VahanException {
        StringBuffer bf = new StringBuffer();
        validateField("APPL NO", getRegn_appl_no(), bf);
        validateField("REGN NO", getRegn_no(), bf);
        if (rb_select_one.equals(SELECT_VALUE3)) {
            validateField("RECEIPT NO", getBal_rcptno(), bf);
        }
        if (bf.length() > 0) {
            throw new VahanException(bf.toString());
        }


    }

    public void validateField(String fldName, String fieldValue, StringBuffer bf) {
        if (fieldValue == null || fieldValue.trim().equals("")) {
            bf.append("Empty " + fldName);
            bf.append("\n");
        }

    }

    public void reset() {
        setReserve_amt(0);
        setRegn_no("");
        setRegn_appl_no(null);
        setChasi_no("");
        setOwner_name("");
        setC_add1("");
        setC_add2("");
        setC_pincode("");
        setRecp_no(null);
        setBal_rcptno(null);
        rcpt_bean.reset();

    }

    public void setFlagTrueListerner(boolean flg) {
        if (!flg) {
//            RequestContext.getCurrentInstance().reset("frm_fancey");
            PrimeFaces.current().resetInputs("frm_fancey");
            return;
        }
        try {
            //Get Fancy Fee
            int fancy_amt = FancyNumberImpl.getAmount(getRegn_no());
            setReserve_amt(fancy_amt);
        } catch (Exception e) {
            FacesMessage message = new FacesMessage("Info", "Error in Fetching Amount");
            FacesContext.getCurrentInstance().addMessage(null, message);
            PrimeFaces.current().resetInputs("frm_fancey");
        }


    }

    public String reloadPage() {
        return null;
    }

    public void vehDistListener(ValueChangeEvent event) {
        String distCd = event.getNewValue().toString();
//        String[][] data = MasterTableFiller.masterTables.TM_TALUK.getData();
//        listC_taluk.clear();
//        for (int i = 0; i < data.length; i++) {
//            if (data[i][0].equals(distCd)) {
//                listC_taluk.add(new SelectItem(data[i][1], data[i][2]));
//            }
//
//        }

    }

    public void reservListener() {
        setListReservedNumbers(FancyNumberImpl.getReservedNos());
    }

    public void vehTalukListener(ValueChangeEvent event) {
        String tal = event.getNewValue().toString();
//        String[][] data = MasterTableFiller.masterTables.TM_VILLAGE.getData();
//        listC_village.clear();
//        for (int i = 0; i < data.length; i++) {
//            if (data[i][1].equals(tal)) {
//                listC_village.add(new SelectItem(data[i][2], data[i][3]));
//            }
//        }

    }

    private void rb_selectListener(ChangeEvent evt) {
    }

    public void reservedNocListener() {
    }

    public boolean isBal_rcpt_view() {
        return bal_rcpt_view;
    }

    public void setBal_rcpt_view(boolean bal_rcpt_view) {
        this.bal_rcpt_view = bal_rcpt_view;
    }

    public boolean isSaved_success() {
        return saved_success;
    }

    public void setSaved_success(boolean saved_success) {
        this.saved_success = saved_success;
    }

    public void setSuucessMessageFalse() {
        this.saved_success = false;
    }

    public boolean isRcpt_view() {
        return rcpt_view;
    }

    public void setRcpt_view(boolean rcpt_view) {
        this.rcpt_view = rcpt_view;
    }

    public String getRb_select_one() {
        return rb_select_one;
    }

    public void setRb_select_one(String rb_select_one) {
        this.rb_select_one = rb_select_one;
    }

    public ArrayList getListC_village() {
        return listC_village;
    }

    public void setListC_village(ArrayList listC_village) {
        this.listC_village = listC_village;
    }

    public ArrayList getListC_taluk() {
        return listC_taluk;
    }

    public void setListC_taluk(ArrayList listC_taluk) {
        this.listC_taluk = listC_taluk;
    }

    public ArrayList getListC_dist() {
        return listC_dist;
    }

    public void setListC_dist(ArrayList listC_dist) {
        this.listC_dist = listC_dist;
    }

    /**
     * @return the SELECT_LABEL1
     */
    public String getSELECT_LABEL1() {
        return SELECT_LABEL1;
    }

    /**
     * @param SELECT_LABEL1 the SELECT_LABEL1 to set
     */
    public void setSELECT_LABEL1(String SELECT_LABEL1) {
        this.SELECT_LABEL1 = SELECT_LABEL1;
    }

    /**
     * @return the SELECT_LABEL2
     */
    public String getSELECT_LABEL2() {
        return SELECT_LABEL2;
    }

    /**
     * @param SELECT_LABEL2 the SELECT_LABEL2 to set
     */
    public void setSELECT_LABEL2(String SELECT_LABEL2) {
        this.SELECT_LABEL2 = SELECT_LABEL2;
    }

    /**
     * @return the SELECT_LABEL3
     */
    public String getSELECT_LABEL3() {
        return SELECT_LABEL3;
    }

    /**
     * @param SELECT_LABEL3 the SELECT_LABEL3 to set
     */
    public void setSELECT_LABEL3(String SELECT_LABEL3) {
        this.SELECT_LABEL3 = SELECT_LABEL3;
    }

    /**
     * @return the SELECT_VALUE1
     */
    public String getSELECT_VALUE1() {
        return SELECT_VALUE1;
    }

    /**
     * @param SELECT_VALUE1 the SELECT_VALUE1 to set
     */
    public void setSELECT_VALUE1(String SELECT_VALUE1) {
        this.SELECT_VALUE1 = SELECT_VALUE1;
    }

    /**
     * @return the SELECT_VALUE2
     */
    public String getSELECT_VALUE2() {
        return SELECT_VALUE2;
    }

    /**
     * @param SELECT_VALUE2 the SELECT_VALUE2 to set
     */
    public void setSELECT_VALUE2(String SELECT_VALUE2) {
        this.SELECT_VALUE2 = SELECT_VALUE2;
    }

    /**
     * @return the SELECT_VALUE3
     */
    public String getSELECT_VALUE3() {
        return SELECT_VALUE3;
    }

    /**
     * @param SELECT_VALUE3 the SELECT_VALUE3 to set
     */
    public void setSELECT_VALUE3(String SELECT_VALUE3) {
        this.SELECT_VALUE3 = SELECT_VALUE3;
    }

    public ReceiptMasterBean getRcpt_bean() {
        return rcpt_bean;
    }

    public void setRcpt_bean(ReceiptMasterBean rcpt_bean) {
        this.rcpt_bean = rcpt_bean;
    }
}
