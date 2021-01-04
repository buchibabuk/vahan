/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services.clients.dobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.annotate.JsonSetter;

/**
 *
 * @author Anu
 */
public class ChallanResponseDobj implements Serializable {

    private String total;
    private List<ChallanHistoryDobj> challan_history = new ArrayList<>();

    /**
     * @return the total
     */
    public String getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    @JsonSetter("Total")
    public void setTotal(String total) {
        this.total = total;
    }

    /**
     * @return the challan_history
     */
    public List<ChallanHistoryDobj> getChallan_history() {
        return challan_history;
    }

    /**
     * @param challan_history the challan_history to set
     */
    public void setChallan_history(List<ChallanHistoryDobj> challan_history) {
        this.challan_history = challan_history;
    }
}
