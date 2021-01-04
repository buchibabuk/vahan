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
public class ReleaseDocumentDetailsDobj implements Serializable {

    private String documentImpound = "";
    private String vehicleImpound = "";
    private String realeasedate = "";
    private String realeseofficer = "";
    private String remarkany = "";
    private String vehicleNo = "";
    private DocImpoundDobj docimpounddobj = new DocImpoundDobj();
    private List<DocImpoundDobj> docimpounddobjList = new <DocImpoundDobj>ArrayList();
    private VehicleImpoundDobj vehicleimpouddobj = new VehicleImpoundDobj();
    private List<VehicleImpoundDobj> vehicleimpounddobjList = new <VehicleImpoundDobj>ArrayList();
    private MasterAccuesedDobj masteraccusedobj = new MasterAccuesedDobj();
    private List<MasterAccuesedDobj> masteraccusedobjList = new <MasterAccuesedDobj> ArrayList();

    /**
     * @return the documentImpound
     */
    public String getDocumentImpound() {
        return documentImpound;
    }

    /**
     * @param documentImpound the documentImpound to set
     */
    public void setDocumentImpound(String documentImpound) {
        this.documentImpound = documentImpound;
    }

    /**
     * @return the vehicleImpound
     */
    public String getVehicleImpound() {
        return vehicleImpound;
    }

    /**
     * @param vehicleImpound the vehicleImpound to set
     */
    public void setVehicleImpound(String vehicleImpound) {
        this.vehicleImpound = vehicleImpound;
    }

    public String getRealeasedate() {
        return realeasedate;
    }

    /**
     * @param realeasedate the realeasedate to set
     */
    public void setRealeasedate(String realeasedate) {
        this.realeasedate = realeasedate;
    }

    /**
     * @return the realeseofficer
     */
    public String getRealeseofficer() {
        return realeseofficer;
    }

    /**
     * @param realeseofficer the realeseofficer to set
     */
    public void setRealeseofficer(String realeseofficer) {
        this.realeseofficer = realeseofficer;
    }

    /**
     * @return the remarkany
     */
    public String getRemarkany() {
        return remarkany;
    }

    /**
     * @param remarkany the remarkany to set
     */
    public void setRemarkany(String remarkany) {
        this.remarkany = remarkany;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    /**
     * @param vehicleNo the vehicleNo to set
     */
    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public DocImpoundDobj getDocimpounddobj() {
        return docimpounddobj;
    }

    public void setDocimpounddobj(DocImpoundDobj docimpounddobj) {
        this.docimpounddobj = docimpounddobj;
    }

    public List<DocImpoundDobj> getDocimpounddobjList() {
        return docimpounddobjList;
    }

    public void setDocimpounddobjList(List<DocImpoundDobj> docimpounddobjList) {
        this.docimpounddobjList = docimpounddobjList;
    }

    public VehicleImpoundDobj getVehicleimpouddobj() {
        return vehicleimpouddobj;
    }

    public void setVehicleimpouddobj(VehicleImpoundDobj vehicleimpouddobj) {
        this.vehicleimpouddobj = vehicleimpouddobj;
    }

    public List<VehicleImpoundDobj> getVehicleimpounddobjList() {
        return vehicleimpounddobjList;
    }

    public void setVehicleimpounddobjList(List<VehicleImpoundDobj> vehicleimpounddobjList) {
        this.vehicleimpounddobjList = vehicleimpounddobjList;
    }

    public MasterAccuesedDobj getMasteraccusedobj() {
        return masteraccusedobj;
    }

    public void setMasteraccusedobj(MasterAccuesedDobj masteraccusedobj) {
        this.masteraccusedobj = masteraccusedobj;
    }

    public List<MasterAccuesedDobj> getMasteraccusedobjList() {
        return masteraccusedobjList;
    }

    public void setMasteraccusedobjList(List<MasterAccuesedDobj> masteraccusedobjList) {
        this.masteraccusedobjList = masteraccusedobjList;
    }
}
