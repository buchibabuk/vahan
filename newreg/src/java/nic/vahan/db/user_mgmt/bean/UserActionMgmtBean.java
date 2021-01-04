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
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.user_mgmt.dobj.UserActionMgmtDobj;
import nic.vahan.db.user_mgmt.impl.UserActionMgmtImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;

@ManagedBean
@ViewScoped
/**
 *
 * @author tranC102
 */
public class UserActionMgmtBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(UserActionMgmtBean.class);
    private String msg = "";
    private String stateCode = "";
    private String user_catg;
    private List selectedOfficeCode = new ArrayList();
    private String enteredBy = "";
    //for Employee
    private String selectedEmp = "";
    private List empList = new ArrayList();
    //for office
    private List offList = new ArrayList();
    //for Purpose
    private String selectedPurpose = "";
    private List pList = new ArrayList();
    private List stateList = new ArrayList();
    private boolean disabled;
    //for Action
    private DualListModel<String> action;
    List<String> actionSource = new ArrayList<>();
    List<String> actionTarget = new ArrayList<>();
    private String strTarget = "";
    //for tree
    private TreeNode root;
    //Dobj Object
    UserActionMgmtDobj dobj = new UserActionMgmtDobj();
    private boolean state_render = false;
    private boolean off_render = false;
    private String empCode = null;

    @PostConstruct
    public void init() {
        String[][] data;
        try {
            stateCode = Util.getUserStateCode();
            user_catg = Util.getUserCategory();
            empCode = Util.getEmpCode();
            if (stateCode == null || user_catg == null || empCode == null) {
                return;
            }
            if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_PORTAL_ADMIN)) {//Portal Admin
                stateList = MasterTableFiller.getStateList();
                data = MasterTableFiller.masterTables.TM_ROLE.getData();
                for (int i = 0; i < data.length; i++) {
                    if (data[i][2].equalsIgnoreCase(TableConstants.USER_CATG_STATE_ADMIN)) {
                        pList.add(new SelectItem(data[i][0], data[i][1]));
                    }
                }
                setOff_render(false);
                setState_render(true);
                selectedOfficeCode.add(0);
            } else if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_STATE_ADMIN)) {//State Admin
                offList.clear();
                setOff_render(true);
                empList = UserActionMgmtImpl.fillEmpList(stateCode, null, user_catg);
            } else if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_ADMIN)) {//Fitness Center Admin
                List offUnder = new ArrayList();
                offUnder = ServerUtil.getOffCode(empCode);
                setOff_render(true);
                empList = UserActionMgmtImpl.fillEmpList(stateCode, offUnder, user_catg);
            } else if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_OFFICE_ADMIN)
                    || user_catg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER_ADMIN)) {//Office Admin OR Dealer Admin
                List offUnder = new ArrayList();
                offUnder = ServerUtil.getOffCode(empCode);
                setOff_render(true);
                //offUnder will be replace with the assigned office fetched from tm_permission of the user
                empList = UserActionMgmtImpl.fillEmpList(stateCode, offUnder, user_catg);
            }
            action = new DualListModel<>(actionSource, actionTarget);
            setStrTarget("");
            root = new DefaultTreeNode("Root", null);
        } catch (VahanException vex) {
            LOGGER.error(vex.toString() + " " + vex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vex.getMessage(), vex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void stateListener() {
        empList.clear();
        try {
            empList = UserActionMgmtImpl.fillEmpList(stateCode, null, user_catg);
        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vex.getMessage(), vex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void onTransfer(TransferEvent event) {
        for (Object item : event.getItems()) {
            setStrTarget(getStrTarget() + item + ",");
        }
    }

    //for creating tree structure
    public void createTree() {
        try {
            selectedPurpose = "";
            actionSource = new ArrayList<>();
            actionTarget = new ArrayList<>();
            List assignOffCode = new ArrayList();
            dobj.setRoot(root);
            dobj.setSelectedEmp(selectedEmp);
            dobj.setState_cd(stateCode);
            String selectedUserCatg = ServerUtil.getUserCategory(Integer.parseInt(selectedEmp));
            if (!user_catg.equalsIgnoreCase(TableConstants.USER_CATG_PORTAL_ADMIN)) {
                String[][] data = MasterTableFiller.masterTables.TM_OFFICE.getData();
                assignOffCode = ServerUtil.getOffCode(selectedEmp);
                if (assignOffCode != null) {
                    if (assignOffCode.size() < 2) {
                        selectedOfficeCode.add(assignOffCode.get(0));
                        offList.clear();
                        if (!assignOffCode.contains("ANY")) {
                            for (int i = 0; i < data.length; i++) {
                                if (stateCode.equalsIgnoreCase(data[i][13]) && assignOffCode.get(0).toString().equalsIgnoreCase(data[i][0])) {
                                    offList.add(new SelectItem(data[i][0], data[i][1]));
                                }
                            }
                        } else {
                            if (selectedOfficeCode.isEmpty()) {
                                selectedOfficeCode.add(0);
                            }
                            //selectedOfficeCode.add(0);
                            for (int i = 0; i < data.length; i++) {
                                if (stateCode.equalsIgnoreCase(data[i][13]) && "0".equalsIgnoreCase(data[i][12])) {
                                    offList.add(new SelectItem(data[i][0], data[i][1]));
                                }
                            }
                        }
                    } else {
                        offList.clear();
                        for (Object offCode : assignOffCode) {
                            for (int i = 0; i < data.length; i++) {
                                if (stateCode.equalsIgnoreCase(data[i][13]) && offCode.toString().equalsIgnoreCase(data[i][0])) {
                                    offList.add(new SelectItem(data[i][0], data[i][1]));
                                }
                            }
                        }
                    }
                }
            }
            root = UserActionMgmtImpl.createTreeStructureImpl(dobj);
            fillPList();

            if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_STATE_ADMIN)
                    || user_catg.equalsIgnoreCase(TableConstants.USER_CATG_OFFICE_ADMIN)
                    || user_catg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER_ADMIN)
                    || user_catg.equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_ADMIN)) {
                pList.clear();
                String[][] data = MasterTableFiller.masterTables.TM_ROLE.getData();
                for (int i = 0; i < data.length; i++) {
                    if (data[i][2].contains(selectedUserCatg)) {
                        pList.add(new SelectItem(data[i][0], data[i][1]));
                    }
                }
            }

            if (!selectedUserCatg.isEmpty() && (selectedUserCatg.equalsIgnoreCase(TableConstants.USER_CATG_REPORT_ADMIN))) {
                setOff_render(false);
            } else {
                setOff_render(true);
            }
        } catch (VahanException vex) {
            LOGGER.error(vex.toString() + " " + vex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    //for Saving action assigned to particular employees
    public void saveActionEmp() {
        try {
            String stateCd;
            if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_PORTAL_ADMIN)) {
                stateCd = stateCode;
            } else {
                stateCd = Util.getUserStateCode();
            }
            dobj.setSelectedEmp(selectedEmp);
            dobj.setSelectedPurpose(selectedPurpose);
            dobj.setStateCode(stateCd);
            dobj.setSelectedOfficeCode(selectedOfficeCode);
            dobj.setEnteredBy(Util.getEmpCode());
            dobj.setStrTarget(strTarget);
            String m = "";
            m = UserActionMgmtImpl.saveActionImpl(dobj);
            createTree();
            strTarget = "";
            setMsg(m);
            JSFUtils.setFacesMessage(msg, null, JSFUtils.INFO);
        } catch (VahanException vex) {
            LOGGER.error(vex.toString() + " " + vex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    //for pick list
    public void fillPList() {
        strTarget = "";
        try {
            if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_PORTAL_ADMIN)
                    || user_catg.equalsIgnoreCase(TableConstants.USER_CATG_REPORT_ADMIN)) {

                dobj.setSelectedEmp(selectedEmp);
                dobj.setSelectedPurpose(selectedPurpose);
                dobj.setActionSource(actionSource);
                dobj.setActionTarget(actionTarget);
                dobj.setUser_categ(user_catg);
                if (!"".equals(dobj.getSelectedEmp()) && !"".equals(dobj.getSelectedPurpose())) {
                    UserActionMgmtImpl.implFillActionList(dobj);
                }
                action = new DualListModel<>(actionSource, actionTarget);
            } else {
                dobj.setSelectedEmp(selectedEmp);
                dobj.setSelectedPurpose(selectedPurpose);
                dobj.setActionSource(actionSource);
                dobj.setActionTarget(actionTarget);
                dobj.setUser_categ(ServerUtil.getUserCategory(Integer.parseInt(selectedEmp)));
                dobj.setSelectedOfficeCode(selectedOfficeCode);
                if (!"".equals(dobj.getSelectedEmp()) && !"".equals(dobj.getSelectedPurpose()) && !dobj.getSelectedOfficeCode().isEmpty()) {
                    UserActionMgmtImpl.implFillActionList(dobj);
                }
                action = new DualListModel<>(actionSource, actionTarget);
            }
        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void reset() {
        if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_PORTAL_ADMIN)) {
            setStateCode("");
            root.getChildren().clear();
            actionSource.clear();
            actionTarget.clear();
            action = new DualListModel<>(actionSource, actionTarget);
            setStrTarget("");
            setSelectedEmp("");
            setSelectedPurpose("");
        } else if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_STATE_ADMIN)) {
            root.getChildren().clear();
            actionSource.clear();
            actionTarget.clear();
            selectedOfficeCode.clear();
            offList.clear();
            action = new DualListModel<>(actionSource, actionTarget);
            setStrTarget("");
            setSelectedEmp("");
            setSelectedPurpose("");
        } else if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_OFFICE_ADMIN)
                || user_catg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER_ADMIN)
                || user_catg.equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_ADMIN)) {
            root.getChildren().clear();
            actionSource.clear();
            actionTarget.clear();
            selectedOfficeCode.clear();
            offList.clear();
            action = new DualListModel<>(actionSource, actionTarget);
            setStrTarget("");
            setSelectedEmp("");
            setSelectedPurpose("");
        }
    }

    /**
     * @return the selectedEmp
     */
    public String getSelectedEmp() {
        return selectedEmp;
    }

    /**
     * @param selectedEmp the selectedEmp to set
     */
    public void setSelectedEmp(String selectedEmp) {
        this.selectedEmp = selectedEmp;
    }

    /**
     * @return the empList
     */
    public List getEmpList() {
        return empList;
    }

    /**
     * @param empList the empList to set
     */
    public void setEmpList(List empList) {
        this.empList = empList;
    }

    /**
     * @return the selectedPurpose
     */
    public String getSelectedPurpose() {
        return selectedPurpose;
    }

    /**
     * @param selectedPurpose the selectedPurpose to set
     */
    public void setSelectedPurpose(String selectedPurpose) {
        this.selectedPurpose = selectedPurpose;
    }

    /**
     * @return the pList
     */
    public List getpList() {
        return pList;
    }

    /**
     * @param pList the pList to set
     */
    public void setpList(List pList) {
        this.pList = pList;
    }

    /**
     * @return the action
     */
    public DualListModel<String> getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(DualListModel<String> action) {
        this.action = action;
    }

    /**
     * @return the strTarget
     */
    public String getStrTarget() {
        return strTarget;
    }

    /**
     * @param strTarget the strTarget to set
     */
    public void setStrTarget(String strTarget) {
        this.strTarget = strTarget;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
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
     * @return the stateCode
     */
    public String getStateCode() {
        return stateCode;
    }

    /**
     * @param stateCode the stateCode to set
     */
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    /**
     * @return the officeCode
     */
    public List getSelectedOfficeCode() {
        return selectedOfficeCode;
    }

    /**
     * @param officeCode the officeCode to set
     */
    public void setSelectedOfficeCode(List officeCode) {
        this.selectedOfficeCode = officeCode;
    }

    /**
     * @return the enteredBy
     */
    public String getEnteredBy() {
        return enteredBy;
    }

    /**
     * @param enteredBy the enteredBy to set
     */
    public void setEnteredBy(String enteredBy) {
        this.enteredBy = enteredBy;
    }

    /**
     * @return the offList
     */
    public List getOffList() {
        return offList;
    }

    /**
     * @param offList the offList to set
     */
    public void setOffList(List offList) {
        this.offList = offList;
    }

    public String getUser_catg() {
        return user_catg;
    }

    public void setUser_catg(String user_catg) {
        this.user_catg = user_catg;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isState_render() {
        return state_render;
    }

    public void setState_render(boolean state_render) {
        this.state_render = state_render;
    }

    public boolean isOff_render() {
        return off_render;
    }

    public void setOff_render(boolean off_render) {
        this.off_render = off_render;
    }

    public List getStateList() {
        return stateList;
    }

    public void setStateList(List stateList) {
        this.stateList = stateList;
    }
}
