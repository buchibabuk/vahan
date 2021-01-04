/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services.clients.dobj;

import java.io.Serializable;

/**
 *
 * @author komal
 */
public class SubCategoryMasterData implements Serializable {

    private String sub_cat_id;
    private String sub_cat_name;

    public SubCategoryMasterData() {
        super();
        // TODO Auto-generated constructor stub
    }

    public SubCategoryMasterData(String sub_cat_id, String sub_cat_name) {
        super();
        this.sub_cat_id = sub_cat_id;
        this.sub_cat_name = sub_cat_name;
    }

    public String getSub_cat_id() {
        return sub_cat_id;
    }

    public String getSub_cat_name() {
        return sub_cat_name;
    }

    public void setSub_cat_id(String sub_cat_id) {
        this.sub_cat_id = sub_cat_id;
    }

    public void setSub_cat_name(String sub_cat_name) {
        this.sub_cat_name = sub_cat_name;
    }
}
