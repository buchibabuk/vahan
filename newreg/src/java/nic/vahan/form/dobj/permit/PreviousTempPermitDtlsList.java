/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hcl
 */
public class PreviousTempPermitDtlsList {

    List<PreviousTempPermitDtlsDobj> dtlsList = null;
    private String headerName;

    public PreviousTempPermitDtlsList() {
        dtlsList = new ArrayList<PreviousTempPermitDtlsDobj>();
    }

    public PreviousTempPermitDtlsList(String headerName) {
        this.headerName = headerName;
        dtlsList = new ArrayList<PreviousTempPermitDtlsDobj>();
    }

    public List<PreviousTempPermitDtlsDobj> getDtlsList() {
        return dtlsList;
    }

    public void setDtlsList(List<PreviousTempPermitDtlsDobj> dtlsList) {
        this.dtlsList = dtlsList;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public int getTotaldays() {
        int totalDays = 0;
        for (PreviousTempPermitDtlsDobj s : dtlsList) {
            totalDays += s.getDays();
        }
        return totalDays;
    }
}
