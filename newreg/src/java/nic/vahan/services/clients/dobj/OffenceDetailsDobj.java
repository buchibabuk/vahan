/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services.clients.dobj;

import java.io.Serializable;

/**
 *
 * @author Anu
 */
public class OffenceDetailsDobj implements Serializable {

    private String name;
    private String act;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the act
     */
    public String getAct() {
        return act;
    }

    /**
     * @param act the act to set
     */
    public void setAct(String act) {
        this.act = act;
    }
}
