/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services.clients.dobj;

import java.util.List;

public class UploadedList {

    private String catId;
    private String catName;
    private String mandatory;
    private Boolean docUploaded;
    private String docUrl;
    private String uniqueRefNo;
    private Boolean docVerified;
    private Boolean docApproved;
    private Boolean docRecieved;
    private Boolean tempDocApproved;
    private String objectId;
    private String docUploadedDate;
    private SubCategoryMasterData subcategoryMasterData;

    public UploadedList() {
        super();
        // TODO Auto-generated constructor stub
    }

    public UploadedList(String catId, String catName, String mandatory, Boolean docUploaded, String docUrl,
            String uniqueRefNo, Boolean docVerified, Boolean docApproved, Boolean docRecieved, Boolean tempDocApproved,
            String objectId, String docUploadedDate, SubCategoryMasterData subcategoryMasterData) {
        super();
        this.catId = catId;
        this.catName = catName;
        this.mandatory = mandatory;
        this.docUploaded = docUploaded;
        this.docUrl = docUrl;
        this.uniqueRefNo = uniqueRefNo;
        this.docVerified = docVerified;
        this.docApproved = docApproved;
        this.docRecieved = docRecieved;
        this.tempDocApproved = tempDocApproved;
        this.objectId = objectId;
        this.docUploadedDate = docUploadedDate;
        this.subcategoryMasterData = subcategoryMasterData;
    }

    public String getCatId() {
        return catId;
    }

    public String getCatName() {
        return catName;
    }

    public String getMandatory() {
        return mandatory;
    }

    public Boolean getDocUploaded() {
        return docUploaded;
    }

    public String getUniqueRefNo() {
        return uniqueRefNo;
    }

    public Boolean getDocVerified() {
        return docVerified;
    }

    public Boolean getDocApproved() {
        return docApproved;
    }

    public Boolean getDocRecieved() {
        return docRecieved;
    }

    public Boolean getTempDocApproved() {
        return tempDocApproved;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getDocUploadedDate() {
        return docUploadedDate;
    }

    public SubCategoryMasterData getSubcategoryMasterData() {
        return subcategoryMasterData;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    public void setDocUploaded(Boolean docUploaded) {
        this.docUploaded = docUploaded;
    }


    public void setUniqueRefNo(String uniqueRefNo) {
        this.uniqueRefNo = uniqueRefNo;
    }

    public void setDocVerified(Boolean docVerified) {
        this.docVerified = docVerified;
    }

    public void setDocApproved(Boolean docApproved) {
        this.docApproved = docApproved;
    }

    public void setDocRecieved(Boolean docRecieved) {
        this.docRecieved = docRecieved;
    }

    public void setTempDocApproved(Boolean tempDocApproved) {
        this.tempDocApproved = tempDocApproved;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public void setDocUploadedDate(String docUploadedDate) {
        this.docUploadedDate = docUploadedDate;
    }

    public void setSubcategoryMasterData(SubCategoryMasterData subcategoryMasterData) {
        this.subcategoryMasterData = subcategoryMasterData;
    }

    /**
     * @return the docUrl
     */
    public String getDocUrl() {
        return docUrl;
    }

    /**
     * @param docUrl the docUrl to set
     */
    public void setDocUrl(String docUrl) {
        this.docUrl = docUrl;
    }
}
