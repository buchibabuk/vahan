/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services;

/**
 *
 * @author nicsi
 */
import java.io.Serializable;
import java.util.List;

public class VTDocumentModelDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<VTDocumentModel> vtDocumentModelList;
    private String status = "";
    private boolean statusFlag = false;

    public List<VTDocumentModel> getVtDocumentModelList() {
        return vtDocumentModelList;
    }

    public void setVtDocumentModelList(List<VTDocumentModel> vtDocumentModelList) {
        this.vtDocumentModelList = vtDocumentModelList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isStatusFlag() {
        return statusFlag;
    }

    public void setStatusFlag(boolean statusFlag) {
        this.statusFlag = statusFlag;
    }
}
