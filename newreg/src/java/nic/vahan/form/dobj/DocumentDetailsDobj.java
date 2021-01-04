/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.List;
import nic.vahan.services.clients.dobj.SubCategoryMasterData;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author komal
 */
public class DocumentDetailsDobj implements Serializable {

    private String regnNo;
    private String stateCd;
    private String applNo;
    private String catId;
    private String catName;
    private String mandatory;
    private Boolean docUploaded;
    private String subCatg;
    private List<SubCategoryMasterData> subCategoryMasterDataList;
    private UploadedFile file;
    private byte[] apiFile;
    // private RequestDto responseDto;
    private String showImage = "";
    private boolean renderUploadButton;
    private boolean renderModifyButton;
    private String objectId;
    private boolean disableSubCatg;
    private boolean docVerified;
    private String uploadedFileName;
    private boolean pdfExtension;
    private boolean imageExtension;
    private boolean digitallySigned;
//    private Map<String,String> subCategoryMasterList = new HashMap<String,String>();

    @Override
    public int hashCode() {
        String hash = this.stateCd + ":" + this.applNo + ":" + this.catId;
        return hash.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof DocumentDetailsDobj)) {
            return false;
        }
        DocumentDetailsDobj ob = (DocumentDetailsDobj) obj;
        if (ob.stateCd != null && ob.applNo != null && ob.catId != null && !ob.stateCd.concat(ob.applNo).concat(ob.catId).equals(this.stateCd.concat(this.applNo).concat(this.catId))) {
            return false;
        }
        return true;
    }

    public String toString() {
        return catName;
    }

    /**
     * @return the catId
     */
    public String getCatId() {
        return catId;
    }

    /**
     * @param catId the catId to set
     */
    public void setCatId(String catId) {
        this.catId = catId;
    }

    /**
     * @return the catName
     */
    public String getCatName() {
        return catName;
    }

    /**
     * @param catName the catName to set
     */
    public void setCatName(String catName) {
        this.catName = catName;
    }

    /**
     * @return the mandatory
     */
    public String getMandatory() {
        return mandatory;
    }

    /**
     * @param mandatory the mandatory to set
     */
    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    /**
     * @return the docUploaded
     */
    public Boolean getDocUploaded() {
        return docUploaded;
    }

    /**
     * @param docUploaded the docUploaded to set
     */
    public void setDocUploaded(Boolean docUploaded) {
        this.docUploaded = docUploaded;
    }

    /**
     * @return the subCatg
     */
    public String getSubCatg() {
        return subCatg;
    }

    /**
     * @param subCatg the subCatg to set
     */
    public void setSubCatg(String subCatg) {
        this.subCatg = subCatg;
    }

    /**
     * @return the subcategoryMasterDataList
     */
    public List<SubCategoryMasterData> getSubcategoryMasterDataList() {
        return getSubCategoryMasterDataList();
    }

    /**
     * @param subcategoryMasterDataList the subcategoryMasterDataList to set
     */
    public void setSubcategoryMasterDataList(List<SubCategoryMasterData> subCategoryMasterDataList) {
        this.setSubCategoryMasterDataList(subCategoryMasterDataList);
    }

    /**
     * @return the file
     */
    public UploadedFile getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(UploadedFile file) {
        this.file = file;
    }

//    /**
//     * @return the responseDto
//     */
//    public RequestDto getResponseDto() {
//        return responseDto;
//    }
//
//    /**
//     * @param responseDto the responseDto to set
//     */
//    public void setResponseDto(RequestDto responseDto) {
//        this.responseDto = responseDto;
//    }
    /**
     * @return the showImage
     */
    public String getShowImage() {
        return showImage;
    }

    /**
     * @param showImage the showImage to set
     */
    public void setShowImage(String showImage) {
        this.showImage = showImage;
    }

    /**
     * @return the objectId
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * @param objectId the objectId to set
     */
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    /**
     * @return the disableSubCatg
     */
    public boolean isDisableSubCatg() {
        return disableSubCatg;
    }

    /**
     * @param disableSubCatg the disableSubCatg to set
     */
    public void setDisableSubCatg(boolean disableSubCatg) {
        this.disableSubCatg = disableSubCatg;
    }

    /**
     * @return the stateCd
     */
    public String getStateCd() {
        return stateCd;
    }

    /**
     * @param stateCd the stateCd to set
     */
    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    /**
     * @return the regnNo
     */
    public String getRegnNo() {
        return regnNo;
    }

    /**
     * @param regnNo the regnNo to set
     */
    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    /**
     * @return the applNo
     */
    public String getApplNo() {
        return applNo;
    }

    /**
     * @param applNo the applNo to set
     */
    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    /**
     * @return the subCategoryMasterDataList
     */
    public List<SubCategoryMasterData> getSubCategoryMasterDataList() {
        return subCategoryMasterDataList;
    }

    /**
     * @param subCategoryMasterDataList the subCategoryMasterDataList to set
     */
    public void setSubCategoryMasterDataList(List<SubCategoryMasterData> subCategoryMasterDataList) {
        this.subCategoryMasterDataList = subCategoryMasterDataList;
    }

    /**
     * @return the apiFile
     */
    public byte[] getApiFile() {
        return apiFile;
    }

    /**
     * @param apiFile the apiFile to set
     */
    public void setApiFile(byte[] apiFile) {
        this.apiFile = apiFile;
    }

    /**
     * @return the renderUploadButton
     */
    public boolean isRenderUploadButton() {
        return renderUploadButton;
    }

    /**
     * @param renderUploadButton the renderUploadButton to set
     */
    public void setRenderUploadButton(boolean renderUploadButton) {
        this.renderUploadButton = renderUploadButton;
    }

    /**
     * @return the renderModifyButton
     */
    public boolean isRenderModifyButton() {
        return renderModifyButton;
    }

    /**
     * @param renderModifyButton the renderModifyButton to set
     */
    public void setRenderModifyButton(boolean renderModifyButton) {
        this.renderModifyButton = renderModifyButton;
    }

    /**
     * @return the docVerified
     */
    public boolean isDocVerified() {
        return docVerified;
    }

    /**
     * @param docVerified the docVerified to set
     */
    public void setDocVerified(boolean docVerified) {
        this.docVerified = docVerified;
    }

    /**
     * @return the uploadedFileName
     */
    public String getUploadedFileName() {
        return uploadedFileName;
    }

    /**
     * @param uploadedFileName the uploadedFileName to set
     */
    public void setUploadedFileName(String uploadedFileName) {
        this.uploadedFileName = uploadedFileName;
    }
//    /**
//     * @return the subCategoryMasterList
//     */
//    public Map<String,String> getSubCategoryMasterList() {
//        return subCategoryMasterList;
//    }
//
//    /**
//     * @param subCategoryMasterList the subCategoryMasterList to set
//     */
//    public void setSubCategoryMasterList(Map<String,String> subCategoryMasterList) {
//        this.subCategoryMasterList = subCategoryMasterList;
//    }
//
//  

    /**
     * @return the pdfExtension
     */
    public boolean isPdfExtension() {
        return pdfExtension;
    }

    /**
     * @param pdfExtension the pdfExtension to set
     */
    public void setPdfExtension(boolean pdfExtension) {
        this.pdfExtension = pdfExtension;
    }

    /**
     * @return the imageExtension
     */
    public boolean isImageExtension() {
        return imageExtension;
    }

    /**
     * @param imageExtension the imageExtension to set
     */
    public void setImageExtension(boolean imageExtension) {
        this.imageExtension = imageExtension;
    }

    /**
     * @return the digitallySigned
     */
    public boolean isDigitallySigned() {
        return digitallySigned;
    }

    /**
     * @param digitallySigned the digitallySigned to set
     */
    public void setDigitallySigned(boolean digitallySigned) {
        this.digitallySigned = digitallySigned;
    }
}
