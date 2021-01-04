/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.vahan.form.dobj.EpayDobj;
import nic.vahan.form.impl.EpayImpl;

/**
 *
 * @author tranC095
 */
@ManagedBean(name = "ePay")
@ViewScoped
public class EpayBean implements Serializable {

    private int purCd;
    private String purCdDescr;
    private long e_TaxFee;
    private long act_TaxFee;
    private long e_FinePenalty;
    private long act_FinePenalty;
    private int e_service_charge;
    private int act_service_charge;
    private int e_pur_total;
    private int act_pur_total;
    private int e_grand_total;
    private int act_grand_total;
    private int e_cess;
    private int act_cess;
    private Date e_tax_fromDt;
    private Date act_tax_fromDt;
    private Date e_tax_toDate;
    private Date act_tax_date;
    private long e_total;
    private long act_total;
    private String appl_no;
    private String rcpt_no;
    private List<EpayBean> list;
    private boolean applicantInformed = false;
    private boolean showPopUp = false;
    private String popUpMessage;

    public int getPurCd() {
        return purCd;
    }

    public void setPurCd(int purCd) {
        this.purCd = purCd;
    }

    public String getPurCdDescr() {
        return purCdDescr;
    }

    public void setPurCdDescr(String purCdDescr) {
        this.purCdDescr = purCdDescr;
    }

    public long getE_TaxFee() {
        return e_TaxFee;
    }

    public void setE_TaxFee(long e_TaxFee) {
        this.e_TaxFee = e_TaxFee;
    }

    public long getAct_TaxFee() {
        return act_TaxFee;
    }

    public void setAct_TaxFee(long act_TaxFee) {
        this.act_TaxFee = act_TaxFee;
    }

    public long getE_FinePenalty() {
        return e_FinePenalty;
    }

    public void setE_FinePenalty(long e_FinePenalty) {
        this.e_FinePenalty = e_FinePenalty;
    }

    public long getAct_FinePenalty() {
        return act_FinePenalty;
    }

    public void setAct_FinePenalty(long act_FinePenalty) {
        this.act_FinePenalty = act_FinePenalty;
    }

    public int getE_service_charge() {
        return e_service_charge;
    }

    public void setE_service_charge(int e_service_charge) {
        this.e_service_charge = e_service_charge;
    }

    public int getAct_service_charge() {
        return act_service_charge;
    }

    public void setAct_service_charge(int act_service_charge) {
        this.act_service_charge = act_service_charge;
    }

    public int getE_pur_total() {
        return e_pur_total;
    }

    public void setE_pur_total(int e_pur_total) {
        this.e_pur_total = e_pur_total;
    }

    public int getAct_pur_total() {
        return act_pur_total;
    }

    public void setAct_pur_total(int act_pur_total) {
        this.act_pur_total = act_pur_total;
    }

    public int getE_grand_total() {
        return e_grand_total;
    }

    public void setE_grand_total(int e_grand_total) {
        this.e_grand_total = e_grand_total;
    }

    public int getAct_grand_total() {
        return act_grand_total;
    }

    public void setAct_grand_total(int act_grand_total) {
        this.act_grand_total = act_grand_total;
    }

    public int getE_cess() {
        return e_cess;
    }

    public void setE_cess(int e_cess) {
        this.e_cess = e_cess;
    }

    public int getAct_cess() {
        return act_cess;
    }

    public void setAct_cess(int act_cess) {
        this.act_cess = act_cess;
    }

    public Date getE_tax_fromDt() {
        return e_tax_fromDt;
    }

    public void setE_tax_fromDt(Date e_tax_fromDt) {
        this.e_tax_fromDt = e_tax_fromDt;
    }

    public Date getAct_tax_fromDt() {
        return act_tax_fromDt;
    }

    public void setAct_tax_fromDt(Date act_tax_fromDt) {
        this.act_tax_fromDt = act_tax_fromDt;
    }

    public Date getE_tax_toDate() {
        return e_tax_toDate;
    }

    public void setE_tax_toDate(Date e_tax_toDate) {
        this.e_tax_toDate = e_tax_toDate;
    }

    public Date getAct_tax_date() {
        return act_tax_date;
    }

    public void setAct_tax_date(Date act_tax_date) {
        this.act_tax_date = act_tax_date;
    }

    public List<EpayBean> getList() {
        return list;
    }

    public void setList(List<EpayBean> list) {
        this.list = list;
    }

    public long getE_total() {
        return e_total;
    }

    public void setE_total(long e_total) {
        this.e_total = e_total;
    }

    public long getAct_total() {
        return act_total;
    }

    public void setAct_total(long act_total) {
        this.act_total = act_total;
    }

    public boolean isApplicantInformed() {
        return applicantInformed;
    }

    public void setApplicantInformed(boolean applicantInformed) {
        this.applicantInformed = applicantInformed;
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getRcpt_no() {
        return rcpt_no;
    }

    public void setRcpt_no(String rcpt_no) {
        this.rcpt_no = rcpt_no;
    }

    public boolean isShowPopUp() {
        return showPopUp;
    }

    public void setShowPopUp(boolean showPopUp) {
        this.showPopUp = showPopUp;
    }

    public String getPopUpMessage() {
        return popUpMessage;
    }

    public void setPopUpMessage(String popUpMessage) {
        this.popUpMessage = popUpMessage;
    }

    public void setBeanListFromDobj(ArrayList<EpayDobj> listDobjs) {
        list = new ArrayList();
        for (int i = 0; i < listDobjs.size(); i++) {
            EpayBean bean = new EpayBean();
            EpayDobj dobj = listDobjs.get(i);

            bean.setPurCd(dobj.getPurCd());
            bean.setPurCdDescr(dobj.getPurCdDescr());

            bean.setAct_TaxFee(dobj.getAct_TaxFee());
            bean.setAct_FinePenalty(dobj.getAct_FinePenalty());
            //bean.setAct_cess(dobj.getAct_cess());
            // bean.setAct_service_charge(dobj.getAct_service_charge());
            bean.setAct_total(dobj.getAct_TaxFee() + dobj.getAct_FinePenalty());

            bean.setE_TaxFee(dobj.getE_TaxFee());
            bean.setE_FinePenalty(dobj.getE_FinePenalty());
            //bean.setE_cess(dobj.getE_cess());
            //bean.setE_service_charge(dobj.getE_service_charge());
            bean.setE_total(dobj.getE_TaxFee() + dobj.getE_FinePenalty());
            bean.setAppl_no(dobj.getAppl_no());
            bean.setRcpt_no(dobj.getRcpt_no());

            list.add(bean);

        }

    }

    public List<EpayDobj> get_dobjList_from_beanList(List<EpayBean> listBean) {

        List<EpayDobj> listDobj = new ArrayList();
        for (int i = 0; i < listBean.size(); i++) {
            EpayDobj dobj = new EpayDobj();
            EpayBean bean = listBean.get(i);

            dobj.setPurCd(bean.getPurCd());
            dobj.setPurCdDescr(bean.getPurCdDescr());

            dobj.setAct_TaxFee(bean.getAct_TaxFee());
            dobj.setAct_FinePenalty(bean.getAct_FinePenalty());
            //bean.setAct_cess(dobj.getAct_cess());
            // bean.setAct_service_charge(dobj.getAct_service_charge());
            dobj.setAct_total(bean.getAct_TaxFee() + bean.getAct_FinePenalty());

            dobj.setE_TaxFee(bean.getE_TaxFee());
            dobj.setE_FinePenalty(bean.getE_FinePenalty());
            //bean.setE_cess(dobj.getE_cess());
            //bean.setE_service_charge(dobj.getE_service_charge());
            dobj.setE_total(bean.getE_TaxFee() + bean.getE_FinePenalty());
            dobj.setAppl_no(bean.getAppl_no());
            dobj.setRcpt_no(bean.getRcpt_no());
            listDobj.add(dobj);

        }

        return listDobj;

    }

    public void intimateUser() {
        Exception e = null;
        try {
            popUpMessage = EpayImpl.intimateUser(get_dobjList_from_beanList(list));
            showPopUp = true;

        } catch (Exception ee) {
            e = ee;
        }

        if (e != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage(), e.getMessage()));

        }

    }
}
