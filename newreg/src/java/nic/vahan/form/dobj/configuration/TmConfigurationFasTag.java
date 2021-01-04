/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.configuration;

import java.io.Serializable;

/**
 *
 * @author Pramod
 */
public class TmConfigurationFasTag implements Serializable {

    private String stateCd;
    private String fasTagCondition;
    private boolean fasTagMandatory = false;

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
     * @return the fasTagCondition
     */
    public String getFasTagCondition() {
        return fasTagCondition;
    }

    /**
     * @param fasTagCondition the fasTagCondition to set
     */
    public void setFasTagCondition(String fasTagCondition) {
        this.fasTagCondition = fasTagCondition;
    }

    /**
     * @return the fasTagMandatory
     */
    public boolean isFasTagMandatory() {
        return fasTagMandatory;
    }

    /**
     * @param fasTagMandatory the fasTagMandatory to set
     */
    public void setFasTagMandatory(boolean fasTagMandatory) {
        this.fasTagMandatory = fasTagMandatory;
    }
}
