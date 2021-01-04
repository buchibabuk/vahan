/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.vahan.services.clients.ChallanClient;
import nic.vahan.services.clients.dobj.ChallanHistoryDobj;
import nic.vahan.services.clients.dobj.ChallanResponseDobj;
import nic.vahan.services.clients.dobj.InputChallanDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author Anu
 */
@ViewScoped
@ManagedBean(name = "challanDetails")
public class ChallanDetailsBean implements Serializable {

    private List<ChallanHistoryDobj> challanlist = new ArrayList<>();
    private static final Logger LOGGER = Logger.getLogger(ChallanDetailsBean.class);

    public void setRegNo(String regNo) {
        try {
            ChallanResponseDobj obj = null;
            ChallanClient cl = new ChallanClient();
            InputChallanDobj input = new InputChallanDobj();
            input.setFlag("V");
            input.setSearch_field(regNo);
            obj = cl.getChallanHistory(input);
            if (obj != null && obj.getChallan_history() != null) {
                setChallanlist(obj.getChallan_history());
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }

    }

    /**
     * @return the challanlist
     */
    public List<ChallanHistoryDobj> getChallanlist() {
        return challanlist;
    }

    /**
     * @param challanlist the challanlist to set
     */
    public void setChallanlist(List<ChallanHistoryDobj> challanlist) {
        this.challanlist = challanlist;
    }
}
