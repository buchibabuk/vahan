/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services.clients.dobj;

import java.io.Serializable;

/**
 * @author Anu
 */
public class InputChallanDobj implements Serializable {

    private String flag;
    private String search_field;

    /**
     * @return the flag
     */
    public String getFlag() {
        return flag;
    }

    /**
     * @param flag the flag to set
     */
    public void setFlag(String flag) {
        this.flag = flag;
    }

    /**
     * @return the search_field
     */
    public String getSearch_field() {
        return search_field;
    }

    /**
     * @param search_field the search_field to set
     */
    public void setSearch_field(String search_field) {
        this.search_field = search_field;
    }
}
