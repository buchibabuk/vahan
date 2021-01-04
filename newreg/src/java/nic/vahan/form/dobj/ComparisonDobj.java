/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

public class ComparisonDobj implements Serializable {

    private String fields;
    private String old_value;
    private String new_value;

    /**
     * @return the fields
     */
    public String getFields() {
        return fields;
    }

    /**
     * @param fields the fields to set
     */
    public void setFields(String fields) {
        this.fields = fields;
    }

    /**
     * @return the old_value
     */
    public String getOld_value() {
        return old_value;
    }

    /**
     * @param old_value the old_value to set
     */
    public void setOld_value(String old_value) {
        this.old_value = old_value;
    }

    /**
     * @return the new_value
     */
    public String getNew_value() {
        return new_value;
    }

    /**
     * @param new_value the new_value to set
     */
    public void setNew_value(String new_value) {
        this.new_value = new_value;
    }
}
