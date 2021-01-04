/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

public class VmExScheduleDobj implements Cloneable {

    private int srno;
    private String type;
    private String vh_class;
    private int flatrate;
    private int unitrate;
    private String state_cd;

    public VmExScheduleDobj(int srno, String type, String vh_class, int flatrate, int unitrate) {
        this.srno = srno;
        this.type = type;
        this.vh_class = vh_class;
        this.flatrate = flatrate;
        this.unitrate = unitrate;
    }

    public VmExScheduleDobj() {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVh_class() {
        return vh_class;
    }

    public void setVh_class(String vh_class) {
        this.vh_class = vh_class;
    }

    public int getFlatrate() {
        return flatrate;
    }

    public void setFlatrate(int flatrate) {
        this.flatrate = flatrate;
    }

    public int getUnitrate() {
        return unitrate;
    }

    public void setUnitrate(int unitrate) {
        this.unitrate = unitrate;
    }

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }
}
