/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Naman Jain
 */
public class PermitShowFeeDetailDobj implements Serializable {

    String permitHead;
    String permitFrom;
    String permitUpto;
    String vhClass;
    String permitAmt;
    String penalty;
    String purCd;
    boolean permitHeadDisable = false;
    boolean disableMinusBt = false;
    private boolean exem_fee_fine = false;

    public PermitShowFeeDetailDobj(PermitShowFeeDetailDobj dobj) {
        this.permitHead = dobj.permitHead;
        this.permitFrom = dobj.permitFrom;
        this.permitUpto = dobj.permitUpto;
        this.vhClass = dobj.vhClass;
        this.permitAmt = dobj.permitAmt;
        this.penalty = dobj.penalty;
        this.purCd = dobj.purCd;
        this.permitHeadDisable = dobj.permitHeadDisable;
        this.disableMinusBt = dobj.disableMinusBt;
        this.exem_fee_fine = dobj.exem_fee_fine;
    }

    public PermitShowFeeDetailDobj(String pur_cd) {
        this.purCd = pur_cd;
    }

    public PermitShowFeeDetailDobj() {
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.purCd);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        final PermitShowFeeDetailDobj other = (PermitShowFeeDetailDobj) obj;
        if (this.purCd.equalsIgnoreCase(other.purCd)) {
            return true;
        } else {
            return false;
        }
    }

    public String getPermitHead() {
        return permitHead;
    }

    public void setPermitHead(String permitHead) {
        this.permitHead = permitHead;
    }

    public String getPermitFrom() {
        return permitFrom;
    }

    public void setPermitFrom(String permitFrom) {
        this.permitFrom = permitFrom;
    }

    public String getPermitUpto() {
        return permitUpto;
    }

    public void setPermitUpto(String permitUpto) {
        this.permitUpto = permitUpto;
    }

    public String getVhClass() {
        return vhClass;
    }

    public void setVhClass(String vhClass) {
        this.vhClass = vhClass;
    }

    public String getPermitAmt() {
        return permitAmt;
    }

    public void setPermitAmt(String permitAmt) {
        this.permitAmt = permitAmt;
    }

    public String getPenalty() {
        return penalty;
    }

    public void setPenalty(String penalty) {
        this.penalty = penalty;
    }

    public String getPurCd() {
        return purCd;
    }

    public void setPurCd(String purCd) {
        this.purCd = purCd;
    }

    public boolean isPermitHeadDisable() {
        return permitHeadDisable;
    }

    public void setPermitHeadDisable(boolean permitHeadDisable) {
        this.permitHeadDisable = permitHeadDisable;
    }

    public boolean isDisableMinusBt() {
        return disableMinusBt;
    }

    public void setDisableMinusBt(boolean disableMinusBt) {
        this.disableMinusBt = disableMinusBt;
    }

    public boolean isExem_fee_fine() {
        return exem_fee_fine;
    }

    public void setExem_fee_fine(boolean exem_fee_fine) {
        this.exem_fee_fine = exem_fee_fine;
    }
}
