/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

public class VmDaPenaltyDobj implements Cloneable {

    private int srno;
    private int dacode;
    private String vhclass;
    private int openalty;
    private int dpenalty;
    private int cpenalty;
    private int susdays;
    private int openalty1;
    private int dpenalty1;
    private int cpenalty1;
    private int susdays1;
    private int openalty2;
    private int dpenalty2;
    private int cpenalty2;
    private int susdays2;
    private String susaction;
    private String state_cd;

    public VmDaPenaltyDobj(int srno, int dacode, String vhclass, int openalty, int dpenalty, int cpenalty, int susdays, int openalty1, int dpenalty1, int cpenalty1, int susdays1, int openalty2, int dpenalty2, int cpenalty2, int susdays2, String susaction) {
        this.srno = srno;
        this.dacode = dacode;
        this.vhclass = vhclass;
        this.openalty = openalty;
        this.dpenalty = dpenalty;
        this.cpenalty = cpenalty;
        this.susdays = susdays;
        this.openalty1 = openalty1;
        this.dpenalty1 = dpenalty1;
        this.cpenalty1 = cpenalty1;
        this.susdays1 = susdays1;
        this.openalty2 = openalty2;
        this.dpenalty2 = dpenalty2;
        this.cpenalty2 = cpenalty2;
        this.susdays2 = susdays2;
        this.susaction = susaction;

    }

    public VmDaPenaltyDobj() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    public int getSrno() {
        return srno;
    }

    public void setSrno(int srno) {
        this.srno = srno;
    }

    public int getDacode() {
        return dacode;
    }

    public void setDacode(int dacode) {
        this.dacode = dacode;
    }

    public String getVhclass() {
        return vhclass;
    }

    public void setVhclass(String vhclass) {
        this.vhclass = vhclass;
    }

    public int getOpenalty() {
        return openalty;
    }

    public void setOpenalty(int openalty) {
        this.openalty = openalty;
    }

    public int getDpenalty() {
        return dpenalty;
    }

    public void setDpenalty(int dpenalty) {
        this.dpenalty = dpenalty;
    }

    public int getCpenalty() {
        return cpenalty;
    }

    public void setCpenalty(int cpenalty) {
        this.cpenalty = cpenalty;
    }

    public int getSusdays() {
        return susdays;
    }

    public void setSusdays(int susdays) {
        this.susdays = susdays;
    }

    public int getOpenalty1() {
        return openalty1;
    }

    public void setOpenalty1(int openalty1) {
        this.openalty1 = openalty1;
    }

    public int getDpenalty1() {
        return dpenalty1;
    }

    public void setDpenalty1(int dpenalty1) {
        this.dpenalty1 = dpenalty1;
    }

    public int getCpenalty1() {
        return cpenalty1;
    }

    public void setCpenalty1(int cpenalty1) {
        this.cpenalty1 = cpenalty1;
    }

    public int getSusdays1() {
        return susdays1;
    }

    public void setSusdays1(int susdays1) {
        this.susdays1 = susdays1;
    }

    public int getOpenalty2() {
        return openalty2;
    }

    public void setOpenalty2(int openalty2) {
        this.openalty2 = openalty2;
    }

    public int getDpenalty2() {
        return dpenalty2;
    }

    public void setDpenalty2(int dpenalty2) {
        this.dpenalty2 = dpenalty2;
    }

    public int getCpenalty2() {
        return cpenalty2;
    }

    public void setCpenalty2(int cpenalty2) {
        this.cpenalty2 = cpenalty2;
    }

    public int getSusdays2() {
        return susdays2;
    }

    public void setSusdays2(int susdays2) {
        this.susdays2 = susdays2;
    }

    public String getSusaction() {
        return susaction;
    }

    public void setSusaction(String susaction) {
        this.susaction = susaction;
    }

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }
}
