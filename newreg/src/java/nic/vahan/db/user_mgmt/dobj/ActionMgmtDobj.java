/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.dobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.model.TreeNode;

/**
 *
 * @author tranC103
 */
public class ActionMgmtDobj implements Serializable{

    private Integer action_cd;
    private String action_descr = "";
    private String action_abbrv = "";
    private String dl_rc_scope = "";
    private Integer selectedAction_type;
    private String redirect_menu = " ";
    private String redirect_url = " ";
    private List actionTypeList = new ArrayList();
    private TreeNode root;

    /**
     * @return the action_cd
     */
    public Integer getAction_cd() {
        return action_cd;
    }

    /**
     * @param action_cd the action_cd to set
     */
    public void setAction_cd(Integer action_cd) {
        this.action_cd = action_cd;
    }

    /**
     * @return the action_descr
     */
    public String getAction_descr() {
        return action_descr;
    }

    /**
     * @param action_descr the action_descr to set
     */
    public void setAction_descr(String action_descr) {
        this.action_descr = action_descr;
    }

    /**
     * @return the action_abbrv
     */
    public String getAction_abbrv() {
        return action_abbrv;
    }

    /**
     * @param action_abbrv the action_abbrv to set
     */
    public void setAction_abbrv(String action_abbrv) {
        this.action_abbrv = action_abbrv;
    }

    /**
     * @return the dl_rc_scope
     */
    public String getDl_rc_scope() {
        return dl_rc_scope;
    }

    /**
     * @param dl_rc_scope the dl_rc_scope to set
     */
    public void setDl_rc_scope(String dl_rc_scope) {
        this.dl_rc_scope = dl_rc_scope;
    }

    /**
     * @return the selectedAction_type
     */
    public Integer getSelectedAction_type() {
        return selectedAction_type;
    }

    /**
     * @param selectedAction_type the selectedAction_type to set
     */
    public void setSelectedAction_type(Integer selectedAction_type) {
        this.selectedAction_type = selectedAction_type;
    }

    /**
     * @return the redirect_menu
     */
    public String getRedirect_menu() {
        return redirect_menu;
    }

    /**
     * @param redirect_menu the redirect_menu to set
     */
    public void setRedirect_menu(String redirect_menu) {
        this.redirect_menu = redirect_menu;
    }

    /**
     * @return the redirect_url
     */
    public String getRedirect_url() {
        return redirect_url;
    }

    /**
     * @param redirect_url the redirect_url to set
     */
    public void setRedirect_url(String redirect_url) {
        this.redirect_url = redirect_url;
    }

    /**
     * @return the actionTypeList
     */
    public List getActionTypeList() {
        return actionTypeList;
    }

    /**
     * @param actionTypeList the actionTypeList to set
     */
    public void setActionTypeList(List actionTypeList) {
        this.actionTypeList = actionTypeList;
    }

    /**
     * @return the root
     */
    public TreeNode getRoot() {
        return root;
    }

    /**
     * @param root the root to set
     */
    public void setRoot(TreeNode root) {
        this.root = root;
    }
}
