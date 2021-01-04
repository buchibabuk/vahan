/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.util.ArrayList;
import java.util.List;

public class VmOffencePenaltyDobj implements Cloneable {

    private int srno;
    private String oldoffcode;
    private String newoffcode;
    private String vhclass;
    private List selected_vhclass = new ArrayList();
    private List selected_accused = new ArrayList();
    private String accused;
    private String sectioncd;
    private int openalty;
    private int dpenalty;
    private int cpenalty;
    private int openalty1;
    private int dpenalty1;
    private int cpenalty1;
    private int openalty2;
    private int dpenalty2;
    private int cpenalty2;
    private String state_cd;

    public VmOffencePenaltyDobj(int srno, String oldoffcode, String newoffcode, String vhclass, String sectioncd, int openalty, int dpenalty, int cpenalty, int openalty1, int dpenalty1, int cpenalty1, int openalty2, int dpenalty2, int cpenalty2) {
        //To change body of generated methods, choose Tools | Templates.
        this.srno = srno;
        this.newoffcode = newoffcode;
        this.oldoffcode = oldoffcode;
        this.vhclass = vhclass;
        this.sectioncd = sectioncd;
        this.openalty = openalty;
        this.dpenalty = dpenalty;
        this.cpenalty = cpenalty;
        this.openalty1 = openalty1;
        this.dpenalty1 = dpenalty1;
        this.cpenalty1 = cpenalty1;
        this.openalty2 = openalty2;
        this.dpenalty2 = dpenalty2;
        this.cpenalty2 = cpenalty2;

    }

    public VmOffencePenaltyDobj() {
    }

    public int getSrno() {
        return srno;
    }

    public void setSrno(int srno) {
        this.srno = srno;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    public String getVhclass() {
        return vhclass;
    }

    public void setVhclass(String vhclass) {
        this.vhclass = vhclass;
    }

    public String getSectioncd() {
        return sectioncd;
    }

    public void setSectioncd(String sectioncd) {
        this.sectioncd = sectioncd;
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

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public String getOldoffcode() {
        return oldoffcode;
    }

    public void setOldoffcode(String oldoffcode) {
        this.oldoffcode = oldoffcode;
    }

    public String getNewoffcode() {
        return newoffcode;
    }

    public void setNewoffcode(String newoffcode) {
        this.newoffcode = newoffcode;
    }

    /**
     * @return the selected_vhclass
     */
    public List getSelected_vhclass() {
        return selected_vhclass;
    }

    /**
     * @param selected_vhclass the selected_vhclass to set
     */
    public void setSelected_vhclass(List selected_vhclass) {
        this.selected_vhclass = selected_vhclass;
    }

    /**
     * @return the selected_accused
     */
    public List getSelected_accused() {
        return selected_accused;
    }

    /**
     * @param selected_accused the selected_accused to set
     */
    public void setSelected_accused(List selected_accused) {
        this.selected_accused = selected_accused;
    }

    /**
     * @return the accused
     */
    public String getAccused() {
        return accused;
    }

    /**
     * @param accused the accused to set
     */
    public void setAccused(String accused) {
        this.accused = accused;
    }
}
