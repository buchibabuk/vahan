/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author nicsi
 */
public class ChallanReferToDobj implements Serializable {

    private String appl_no;
    private String courtCode;
    private Date hearingDate;

    public String getCourtCode() {
        return courtCode;
    }

    public void setCourtCode(String courtCode) {
        this.courtCode = courtCode;
    }

    public Date getHearingDate() {
        return hearingDate;
    }

    public void setHearingDate(Date hearingDate) {
        this.hearingDate = hearingDate;
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }
}
