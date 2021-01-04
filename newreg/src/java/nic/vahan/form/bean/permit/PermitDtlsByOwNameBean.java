/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.form.dobj.permit.PermitDtlsByOwNameDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.PermitDtlsByOwNameImpl;
import nic.vahan.server.CommonUtils;
import org.primefaces.PrimeFaces;

/**
 *
 * @author hcl
 */
@ManagedBean(name = "pmtByOwn")
@ViewScoped
public class PermitDtlsByOwNameBean implements Serializable {

    private String ownerName;
    private String ftName;
    private String pmtNo;
    private String option = "1";
    List<PermitDtlsByOwNameDobj> pmtDtlsList = new ArrayList<>();

    public void get_Details() {
        switch (option) {
            case "1":
                if (CommonUtils.isNullOrBlank(ownerName)) {
                    pmtDtlsList.clear();
                    JSFUtils.showMessagesInDialog("Error", "Please fill the owner name", FacesMessage.SEVERITY_ERROR);
                } else {
                    PermitDtlsByOwNameImpl impl = new PermitDtlsByOwNameImpl();
                    pmtDtlsList = impl.getPmtDtlsByOwnName(getOwnerName().toUpperCase(), getFtName().toUpperCase(), Util.getUserStateCode());
                    if (pmtDtlsList.size() < 1) {
                        JSFUtils.showMessagesInDialog("Information", "No record found", FacesMessage.SEVERITY_INFO);
                    }
                }
                break;
            case "2":
                if (CommonUtils.isNullOrBlank(pmtNo)) {
                    pmtDtlsList.clear();
                    JSFUtils.showMessagesInDialog("Error", "Please enter the permit number", FacesMessage.SEVERITY_ERROR);
                } else {
                    PermitDtlsByOwNameImpl impl = new PermitDtlsByOwNameImpl();
                    pmtDtlsList = impl.getPmtDtlsByPmtNo(getPmtNo().toUpperCase(), Util.getUserStateCode());
                    if (pmtDtlsList.size() < 1) {
                        JSFUtils.showMessagesInDialog("Information", "No record found", FacesMessage.SEVERITY_INFO);
                    }
                }
                break;
        }
    }

    public void onChange() {
        switch (option) {
            case "2":
                setOwnerName("");
                setFtName("");
                pmtDtlsList.clear();
                break;
            case "1":
                setPmtNo("");
                pmtDtlsList.clear();
                break;
        }
        PrimeFaces.current().ajax().update("ownTDtls GetOwnerDtls GetPmtNoDtls");
    }

    public List<PermitDtlsByOwNameDobj> getPmtDtlsList() {
        return pmtDtlsList;
    }

    public void setPmtDtlsList(List<PermitDtlsByOwNameDobj> pmtDtlsList) {
        this.pmtDtlsList = pmtDtlsList;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getFtName() {
        return ftName;
    }

    public void setFtName(String ftName) {
        this.ftName = ftName;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getPmtNo() {
        return pmtNo;
    }

    public void setPmtNo(String pmtNo) {
        this.pmtNo = pmtNo;
    }
}
