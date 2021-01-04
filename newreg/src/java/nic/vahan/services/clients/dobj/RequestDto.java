/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services.clients.dobj;

import java.io.Serializable;
import java.util.List;

public class RequestDto implements Serializable {

    private String j_key;
    private String j_securityKey;
    private String state;
    private String message;
    private String applno;
    private String key;
    private List<DmsFileDetails> dmsFileDetails;
    private List<VtDocuments> vtDocuments;
    private String statusCode;
    private String prj_name;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String inStatusCode) {
        statusCode = inStatusCode;
    }

    public String getJ_key() {
        return j_key;
    }

    public String getJ_securityKey() {
        return j_securityKey;
    }

    public String getState() {
        return state;
    }

    public String getMessage() {
        return message;
    }

    public String getApplno() {
        return applno;
    }

    public String getKey() {
        return key;
    }

    public void setJ_key(String inJ_key) {
        j_key = inJ_key;
    }

    public void setJ_securityKey(String inJ_securityKey) {
        j_securityKey = inJ_securityKey;
    }

    public void setState(String inState) {
        state = inState;
    }

    public void setMessage(String inMessage) {
        message = inMessage;
    }

    public void setApplno(String inApplno) {
        applno = inApplno;
    }

    public void setKey(String inKey) {
        key = inKey;
    }

    public List<DmsFileDetails> getDmsFileDetails() {
        return dmsFileDetails;
    }

    public void setDmsFileDetails(List<DmsFileDetails> inDmsFileDetails) {
        dmsFileDetails = inDmsFileDetails;
    }

    public List<VtDocuments> getVtDocuments() {
        return vtDocuments;
    }

    public void setVtDocuments(List<VtDocuments> inVtDocuments) {
        vtDocuments = inVtDocuments;
    }

    public String getPrj_name() {
        return prj_name;
    }

    public void setPrj_name(String inPrj_name) {
        prj_name = inPrj_name;
    }

    @Override
    public String toString() {
        return "RequestDto [j_key=" + j_key + ", j_securityKey=" + j_securityKey + ", state=" + state + ", message="
                + message + ", applno=" + applno + ", key=" + key + ", dmsFileDetails=" + dmsFileDetails + "]";
    }
}
