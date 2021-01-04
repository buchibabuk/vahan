/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.vahan.form.dobj.HpaDobj;

/**
 *
 * @author Ashok Kumar
 */
@ManagedBean(name = "hypothecationDetails_bean")
@ViewScoped
public class HypothecationDetailsBean implements Serializable {

    private List<HpaDobj> listHypthDetails;

    public HypothecationDetailsBean() {
    }

    /**
     * @return the listHypthDetails
     */
    public List<HpaDobj> getListHypthDetails() {
        return listHypthDetails;
    }

    /**
     * @param listHypthDetails the listHypthDetails to set
     */
    public void setListHypthDetails(List<HpaDobj> listHypthDetails) {
        this.listHypthDetails = listHypthDetails;
    }
}
