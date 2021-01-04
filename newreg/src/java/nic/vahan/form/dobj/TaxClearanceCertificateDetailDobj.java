/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ankur
 */
public class TaxClearanceCertificateDetailDobj implements Serializable {

    private String appl_no;
    private String regno;
    private int purCd;
    private String purCdDescr;
    private List<PrintCertificatesDobj> list = new ArrayList();
    private int action_cd;
    private String state_cd;
    private int off_cd;
    private String appl_no_hist;
    private String regno_hist;
    private String tcc_no;

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

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

    public List<PrintCertificatesDobj> getList() {
        return list;
    }

    public void setList(List<PrintCertificatesDobj> list) {
        this.list = list;
    }

    public int getAction_cd() {
        return action_cd;
    }

    public void setAction_cd(int action_cd) {
        this.action_cd = action_cd;
    }

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public int getOff_cd() {
        return off_cd;
    }

    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }

    /**
     * @return the appl_no_hist
     */
    public String getAppl_no_hist() {
        return appl_no_hist;
    }

    /**
     * @param appl_no_hist the appl_no_hist to set
     */
    public void setAppl_no_hist(String appl_no_hist) {
        this.appl_no_hist = appl_no_hist;
    }

    /**
     * @return the regno_hist
     */
    public String getRegno_hist() {
        return regno_hist;
    }

    /**
     * @param regno_hist the regno_hist to set
     */
    public void setRegno_hist(String regno_hist) {
        this.regno_hist = regno_hist;
    }

    public String getTcc_no() {
        return tcc_no;
    }

    public void setTcc_no(String tcc_no) {
        this.tcc_no = tcc_no;
    }
}
