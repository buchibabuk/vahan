/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services.clients.dobj;

public class DmsFileDetails {

    private String folderName = null;
    private String fileName = null;
    private String docCatgId;
    private byte[] apiFile;
    private String objectId;
    private String regNo;
    private Integer docId;
    private boolean uploaded;

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean inUploaded) {
        uploaded = inUploaded;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getApiFile() {
        return apiFile;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setApiFile(byte[] inApiFile) {
        apiFile = inApiFile;
    }

    public void setObjectId(String inObjectId) {
        objectId = inObjectId;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String inRegNo) {
        regNo = inRegNo;
    }

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer inDocId) {
        docId = inDocId;
    }

    public String getDocCatgId() {
        return docCatgId;
    }

    public void setDocCatgId(String inDocCatgId) {
        docCatgId = inDocCatgId;
    }

    @Override
    public String toString() {
        return "DmsFileDetails [folderName=" + folderName + ", fileName=" + fileName + "]";
    }
}
