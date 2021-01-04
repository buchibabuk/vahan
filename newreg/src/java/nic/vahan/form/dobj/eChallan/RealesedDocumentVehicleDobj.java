/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tranC111
 */
public class RealesedDocumentVehicleDobj implements Serializable, Cloneable {

    private ReleaseDocumentDetailsDobj realesdocdetails = new ReleaseDocumentDetailsDobj();
    private List<ReleaseDocumentDetailsDobj> realesdocdtlist = new <ReleaseDocumentDetailsDobj> ArrayList();
    private String messgae = "";

    /**
     * @return the realesdocdetails
     */
    public ReleaseDocumentDetailsDobj getRealesdocdetails() {
        return realesdocdetails;
    }

    /**
     * @param realesdocdetails the realesdocdetails to set
     */
    public void setRealesdocdetails(ReleaseDocumentDetailsDobj realesdocdetails) {
        this.realesdocdetails = realesdocdetails;
    }

    /**
     * @return the realesdocdtlist
     */
    public List<ReleaseDocumentDetailsDobj> getRealesdocdtlist() {
        return realesdocdtlist;
    }

    /**
     * @param realesdocdtlist the realesdocdtlist to set
     */
    public void setRealesdocdtlist(List<ReleaseDocumentDetailsDobj> realesdocdtlist) {
        this.realesdocdtlist = realesdocdtlist;
    }

    /**
     * @return the messgae
     */
    public String getMessgae() {
        return messgae;
    }

    /**
     * @param messgae the messgae to set
     */
    public void setMessgae(String messgae) {
        this.messgae = messgae;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
