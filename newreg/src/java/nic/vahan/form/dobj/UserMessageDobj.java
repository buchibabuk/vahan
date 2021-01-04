/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

/**
 *
 * @author acer
 */
public class UserMessageDobj implements Serializable {

    private String fromUser;
    private String messageId;
    private Long fromUserCd;
    private String fromStateCd;
    private int fromOffCd;
    private String message;
    private String messageDate;
    private String copyToUser = "";
    private String docRefNo = null;
    private int uploadDocOffCode;
    private String uploadDocStateCode;
    private boolean closeRequest;

    public void setCloseRequest(boolean closeRequest) {
        this.closeRequest = closeRequest;
    }

    public boolean isCloseRequest() {
        return closeRequest;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public void setFromUserCd(Long fromUserCd) {
        this.fromUserCd = fromUserCd;
    }

    public void setFromStateCd(String fromStateCd) {
        this.fromStateCd = fromStateCd;
    }

    public void setFromOffCd(int fromOffCd) {
        this.fromOffCd = fromOffCd;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }

    public void setCopyToUser(String copyToUser) {
        this.copyToUser = copyToUser;
    }

    public void setDocRefNo(String docRefNo) {
        this.docRefNo = docRefNo;
    }

    public void setUploadDocOffCode(int uploadDocOffCode) {
        this.uploadDocOffCode = uploadDocOffCode;
    }

    public void setUploadDocStateCode(String uploadDocStateCode) {
        this.uploadDocStateCode = uploadDocStateCode;
    }

    public String getFromUser() {
        return fromUser;
    }

    public Long getFromUserCd() {
        return fromUserCd;
    }

    public String getFromStateCd() {
        return fromStateCd;
    }

    public int getFromOffCd() {
        return fromOffCd;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public String getCopyToUser() {
        return copyToUser;
    }

    public String getDocRefNo() {
        return docRefNo;
    }

    public int getUploadDocOffCode() {
        return uploadDocOffCode;
    }

    public String getUploadDocStateCode() {
        return uploadDocStateCode;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}