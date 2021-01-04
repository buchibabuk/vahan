/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;

/**
 *
 * @author R Gautam
 */
public class AITPStateCoveredDobj implements Serializable {

    private String StateCd;
    private String StateName;

    public String getStateCd() {
        return StateCd;
    }

    public void setStateCd(String StateCd) {
        this.StateCd = StateCd;
    }

    public String getStateName() {
        return StateName;
    }

    public void setStateName(String StateName) {
        this.StateName = StateName;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AITPStateCoveredDobj)) {
            return false;
        }
        AITPStateCoveredDobj coveredStateBeanObj = (AITPStateCoveredDobj) obj;
        return (coveredStateBeanObj.getStateCd() != null && coveredStateBeanObj.getStateCd().equals(StateCd));
    }

    @Override
    public int hashCode() {
        int hash = 1;
        if (StateCd != null) {
            hash = hash * 31 + StateCd.hashCode();
        }
        return hash;
    }

    @Override
    public String toString() {
        return StateCd;
    }
}
