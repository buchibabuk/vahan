/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.reports;

import java.io.Serializable;

/**
 *
 * @author afzal
 */
public class ScrappedVehicleReportDobj implements Serializable {

    private String currentAddress;
    private String ownName;
    private String fname;
    private String oldRegnno;
    private String newRegnno;
    private String maker;
    private String model;
    private String chasino;
    private String engno;
    private String scrapCertno;
    private String scrapCertdt;
    private String nodueCertno;
    private String nodueCertdt;
    private String loino;
    private String statecd;
    private int offcd;
    private String printedon;
    private String heading;
    private String subHeading;
    private String offname;
    private String opdt;
    private String stateName;
    private int pincode;
    private String userName;
    private String scrap_reason = "";
    private String image_background;
    private String image_logo;
    private boolean show_image_background;
    private boolean show_image_logo;
    private boolean showRoadSafetySlogan;
    private VmRoadSafetySloganPrintDobj roadSafetySloganDobj = null;

    /**
     * @return the currentAddress
     */
    public String getCurrentAddress() {
        return currentAddress;
    }

    /**
     * @param currentAddress the currentAddress to set
     */
    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    /**
     * @return the oldRegnno
     */
    public String getOldRegnno() {
        return oldRegnno;
    }

    /**
     * @param oldRegnno the oldRegnno to set
     */
    public void setOldRegnno(String oldRegnno) {
        this.oldRegnno = oldRegnno;
    }

    /**
     * @return the maker
     */
    public String getMaker() {
        return maker;
    }

    /**
     * @param maker the maker to set
     */
    public void setMaker(String maker) {
        this.maker = maker;
    }

    /**
     * @return the model
     */
    public String getModel() {
        return model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * @return the chasino
     */
    public String getChasino() {
        return chasino;
    }

    /**
     * @param chasino the chasino to set
     */
    public void setChasino(String chasino) {
        this.chasino = chasino;
    }

    /**
     * @return the engno
     */
    public String getEngno() {
        return engno;
    }

    /**
     * @param engno the engno to set
     */
    public void setEngno(String engno) {
        this.engno = engno;
    }

    /**
     * @return the scrapCertno
     */
    public String getScrapCertno() {
        return scrapCertno;
    }

    /**
     * @param scrapCertno the scrapCertno to set
     */
    public void setScrapCertno(String scrapCertno) {
        this.scrapCertno = scrapCertno;
    }

    /**
     * @return the scrapCertdt
     */
    public String getScrapCertdt() {
        return scrapCertdt;
    }

    /**
     * @param scrapCertdt the scrapCertdt to set
     */
    public void setScrapCertdt(String scrapCertdt) {
        this.scrapCertdt = scrapCertdt;
    }

    /**
     * @return the nodueCertno
     */
    public String getNodueCertno() {
        return nodueCertno;
    }

    /**
     * @param nodueCertno the nodueCertno to set
     */
    public void setNodueCertno(String nodueCertno) {
        this.nodueCertno = nodueCertno;
    }

    /**
     * @return the nodueCertdt
     */
    public String getNodueCertdt() {
        return nodueCertdt;
    }

    /**
     * @param nodueCertdt the nodueCertdt to set
     */
    public void setNodueCertdt(String nodueCertdt) {
        this.nodueCertdt = nodueCertdt;
    }

    /**
     * @return the loino
     */
    public String getLoino() {
        return loino;
    }

    /**
     * @param loino the loino to set
     */
    public void setLoino(String loino) {
        this.loino = loino;
    }

    /**
     * @return the statecd
     */
    public String getStatecd() {
        return statecd;
    }

    /**
     * @param statecd the statecd to set
     */
    public void setStatecd(String statecd) {
        this.statecd = statecd;
    }

    /**
     * @return the offcd
     */
    public int getOffcd() {
        return offcd;
    }

    /**
     * @param offcd the offcd to set
     */
    public void setOffcd(int offcd) {
        this.offcd = offcd;
    }

    /**
     * @return the printedon
     */
    public String getPrintedon() {
        return printedon;
    }

    /**
     * @param printedon the printedon to set
     */
    public void setPrintedon(String printedon) {
        this.printedon = printedon;
    }

    /**
     * @return the heading
     */
    public String getHeading() {
        return heading;
    }

    /**
     * @param heading the heading to set
     */
    public void setHeading(String heading) {
        this.heading = heading;
    }

    /**
     * @return the subHeading
     */
    public String getSubHeading() {
        return subHeading;
    }

    /**
     * @param subHeading the subHeading to set
     */
    public void setSubHeading(String subHeading) {
        this.subHeading = subHeading;
    }

    /**
     * @return the offname
     */
    public String getOffname() {
        return offname;
    }

    /**
     * @param offname the offname to set
     */
    public void setOffname(String offname) {
        this.offname = offname;
    }

    /**
     * @return the ownName
     */
    public String getOwnName() {
        return ownName;
    }

    /**
     * @param ownName the ownName to set
     */
    public void setOwnName(String ownName) {
        this.ownName = ownName;
    }

    /**
     * @return the fname
     */
    public String getFname() {
        return fname;
    }

    /**
     * @param fname the fname to set
     */
    public void setFname(String fname) {
        this.fname = fname;
    }

    /**
     * @return the opdt
     */
    public String getOpdt() {
        return opdt;
    }

    /**
     * @param opdt the opdt to set
     */
    public void setOpdt(String opdt) {
        this.opdt = opdt;
    }

    /**
     * @return the stateName
     */
    public String getStateName() {
        return stateName;
    }

    /**
     * @param stateName the stateName to set
     */
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    /**
     * @return the pincode
     */
    public int getPincode() {
        return pincode;
    }

    /**
     * @param pincode the pincode to set
     */
    public void setPincode(int pincode) {
        this.pincode = pincode;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the scrap_reason
     */
    public String getScrap_reason() {
        return scrap_reason;
    }

    /**
     * @param scrap_reason the scrap_reason to set
     */
    public void setScrap_reason(String scrap_reason) {
        this.scrap_reason = scrap_reason;
    }

    public String getNewRegnno() {
        return newRegnno;
    }

    public void setNewRegnno(String newRegnno) {
        this.newRegnno = newRegnno;
    }

    /**
     * @return the image_background
     */
    public String getImage_background() {
        return image_background;
    }

    /**
     * @param image_background the image_background to set
     */
    public void setImage_background(String image_background) {
        this.image_background = image_background;
    }

    /**
     * @return the image_logo
     */
    public String getImage_logo() {
        return image_logo;
    }

    /**
     * @param image_logo the image_logo to set
     */
    public void setImage_logo(String image_logo) {
        this.image_logo = image_logo;
    }

    /**
     * @return the show_image_background
     */
    public boolean isShow_image_background() {
        return show_image_background;
    }

    /**
     * @param show_image_background the show_image_background to set
     */
    public void setShow_image_background(boolean show_image_background) {
        this.show_image_background = show_image_background;
    }

    /**
     * @return the show_image_logo
     */
    public boolean isShow_image_logo() {
        return show_image_logo;
    }

    /**
     * @param show_image_logo the show_image_logo to set
     */
    public void setShow_image_logo(boolean show_image_logo) {
        this.show_image_logo = show_image_logo;
    }

    /**
     * @return the showRoadSafetySlogan
     */
    public boolean isShowRoadSafetySlogan() {
        return showRoadSafetySlogan;
    }

    /**
     * @param showRoadSafetySlogan the showRoadSafetySlogan to set
     */
    public void setShowRoadSafetySlogan(boolean showRoadSafetySlogan) {
        this.showRoadSafetySlogan = showRoadSafetySlogan;
    }

    /**
     * @return the roadSafetySloganDobj
     */
    public VmRoadSafetySloganPrintDobj getRoadSafetySloganDobj() {
        return roadSafetySloganDobj;
    }

    /**
     * @param roadSafetySloganDobj the roadSafetySloganDobj to set
     */
    public void setRoadSafetySloganDobj(VmRoadSafetySloganPrintDobj roadSafetySloganDobj) {
        this.roadSafetySloganDobj = roadSafetySloganDobj;
    }
}
