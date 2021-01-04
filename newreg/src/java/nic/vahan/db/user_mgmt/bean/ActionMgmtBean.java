/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.user_mgmt.dobj.ActionMgmtDobj;
import nic.vahan.db.user_mgmt.impl.ActionMgmtImpl;
import org.primefaces.model.TreeNode;

/**
 *
 * @author tranC103
 */
@ManagedBean
@ViewScoped

public class ActionMgmtBean implements Serializable {

    private Integer action_cd;
    private String action_descr = "";
    private String action_abbrv = "";
    private String dl_rc_scope = "";
    private Integer selectedAction_type;
    private String redirect_menu = "";
    private String redirect_url = "";
    private boolean newAction = true;
    private List actionTypeList = new ArrayList();    
    private TreeNode root;    
    ActionMgmtDobj dobj = new ActionMgmtDobj();

    @PostConstruct
    public void init() {
        String[][] data = MasterTableFiller.masterTables.TM_ROLE.getData();
        for (int i = 0; i < data.length; i++) {
            actionTypeList.add(new SelectItem(data[i][0], data[i][1]));
        }

    }

    //for creating tree structure
    public void createTree() {
        dobj.setRoot(root);
        dobj.setSelectedAction_type(selectedAction_type);
        root = ActionMgmtImpl.createTreeStructure(dobj);
    }

    //for checking unique action code
    public void uniqueActCode() {
        if (action_cd != null) {
            dobj.setSelectedAction_type(selectedAction_type);
            dobj.setAction_cd(action_cd);
            String m = "";
            boolean flag = ActionMgmtImpl.checkActionCode(dobj);
            if (flag) {
                newAction = true;
                m = "Action Already Exist!!!";
            } else {
                newAction = false;
                m = "Unique Action Code!!!";
            }
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(m));
        }
    }

    //for saving action 
    public void saveAction() {
        String m = "";
        dobj.setSelectedAction_type(selectedAction_type);
        dobj.setAction_cd(action_cd);
        dobj.setAction_descr(action_descr);
        dobj.setAction_abbrv(action_abbrv);
        dobj.setRedirect_menu(redirect_menu);
        dobj.setRedirect_url(redirect_url);
        m = ActionMgmtImpl.saveActionRecord(dobj);

        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(m));

        createTree();
    }

    //Reset Function
    public void reset() {
        selectedAction_type = null;
        action_abbrv = "";
        action_cd = null;
        action_descr = "";
        dl_rc_scope = "";
        redirect_menu = "";
        redirect_url = "";
        root.getChildren().clear();

    }

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

    /**
     * @return the newAction
     */
    public boolean isNewAction() {
        return newAction;
    }

    /**
     * @param newAction the newAction to set
     */
    public void setNewAction(boolean newAction) {
        this.newAction = newAction;
    }

}
