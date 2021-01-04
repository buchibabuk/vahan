/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

public class VmDocumentsDobj implements Cloneable {

    private int oldcode;
    private int newcode;
    private String description;
    private String state_cd;

    public VmDocumentsDobj(int oldcode, int newcode, String description) {
        this.oldcode = oldcode;
        this.newcode = newcode;
        this.description = description;
    }

    public VmDocumentsDobj() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public int getOldcode() {
        return oldcode;
    }

    public void setOldcode(int oldcode) {
        this.oldcode = oldcode;
    }

    public int getNewcode() {
        return newcode;
    }

    public void setNewcode(int newcode) {
        this.newcode = newcode;
    }
}
