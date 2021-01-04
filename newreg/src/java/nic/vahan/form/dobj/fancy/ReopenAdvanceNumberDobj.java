/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.fancy;

import java.io.Serializable;

/**
 *
 * @author SUMIT GUPTA
 */
public class ReopenAdvanceNumberDobj implements Serializable {

    private String regn_no;
    private String owner_name;
    private String book_dt;
    private boolean checked;

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getBook_dt() {
        return book_dt;
    }

    public void setBook_dt(String book_dt) {
        this.book_dt = book_dt;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof ReopenAdvanceNumberDobj)) {
            return false;
        }
        ReopenAdvanceNumberDobj dobj = (ReopenAdvanceNumberDobj) obj;
        if (dobj.getRegn_no() != null && !dobj.getRegn_no().isEmpty()
                && dobj.getRegn_no().equals(getRegn_no())) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {
        return (getRegn_no() != null && !getRegn_no().isEmpty()) ? getRegn_no().hashCode() : 0; //To change body of generated methods, choose Tools | Templates.
    }
}
