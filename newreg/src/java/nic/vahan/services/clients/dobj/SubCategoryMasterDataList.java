/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services.clients.dobj;

import java.io.Serializable;

public class SubCategoryMasterDataList implements Serializable {

    private String cat_id;
    private int sub_cat_id;
    private String sub_cat_name;

    public String getCat_id() {
        return cat_id;
    }

    public int getSub_cat_id() {
        return sub_cat_id;
    }

    public String getSub_cat_name() {
        return sub_cat_name;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public void setSub_cat_id(int sub_cat_id) {
        this.sub_cat_id = sub_cat_id;
    }

    public void setSub_cat_name(String sub_cat_name) {
        this.sub_cat_name = sub_cat_name;
    }
}
