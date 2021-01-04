/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author tranC103
 */
public class ImportedVehicleDobj implements Serializable, Cloneable {

    private int cm_country_imp;
    private String tf_dealer_imp = "";
    private String tf_place_imp = "";
    private String tf_foreign_imp = "";
    private Integer tf_YOM_imp;
    private boolean importedVehInsertUpdateFlag;

    public int getCm_country_imp() {
        return cm_country_imp;
    }

    public void setCm_country_imp(int cm_country_imp) {
        this.cm_country_imp = cm_country_imp;
    }

    public String getTf_dealer_imp() {
        return tf_dealer_imp;
    }

    public void setTf_dealer_imp(String tf_dealer_imp) {
        this.tf_dealer_imp = tf_dealer_imp;
    }

    public String getTf_place_imp() {
        return tf_place_imp;
    }

    public void setTf_place_imp(String tf_place_imp) {
        this.tf_place_imp = tf_place_imp;
    }

    public String getTf_foreign_imp() {
        return tf_foreign_imp;
    }

    public void setTf_foreign_imp(String tf_foreign_imp) {
        this.tf_foreign_imp = tf_foreign_imp;
    }

    public Integer getTf_YOM_imp() {
        return tf_YOM_imp;
    }

    public void setTf_YOM_imp(Integer tf_YOM_imp) {
        this.tf_YOM_imp = tf_YOM_imp;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean isImportedVehInsertUpdateFlag() {
        return importedVehInsertUpdateFlag;
    }

    public void setImportedVehInsertUpdateFlag(boolean importedVehInsertUpdateFlag) {
        this.importedVehInsertUpdateFlag = importedVehInsertUpdateFlag;
    }
}
