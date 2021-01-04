/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.UserMessageDobj;

import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.admin.UserMsgByCatgImpl;

import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;
//import org.primefaces.context.RequestContext;
import org.primefaces.PrimeFaces;

/**
 *
 * @author DELL
 */
@ManagedBean(name = "userMsgByCatgBean")
@ViewScoped
public class UserMsgByCatgBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(UserMsgByCatgBean.class);
    private SessionVariables sessionVariables = null;
    private String state_name = "";
    private String messageText = "";
    private List userCatgList = new ArrayList();
    private List<String> userMsgByCatgList = new ArrayList();
    private List<UserMessageDobj> userMessagesList = new ArrayList<>();
    private List<UserMessageDobj> userMessagesListByCatg = null;
    private String formHeader = "";
    String userMsgType = "";
    private boolean readMsgByUserCatg = false;
    UserMsgByCatgImpl impl = new UserMsgByCatgImpl();

    public UserMsgByCatgBean() {
        String[][] data = null;
        try {
            sessionVariables = new SessionVariables();
            if (sessionVariables == null
                    || sessionVariables.getStateCodeSelected() == null
                    || sessionVariables.getEmpCodeLoggedIn() == null) {
                throw new VahanException(TableConstants.SomthingWentWrong);
            } else {
                state_name = MasterTableFiller.state.get(sessionVariables.getStateCodeSelected()).getStateDescr();
                data = MasterTableFiller.masterTables.TM_USER_CATG.getData();
                for (int i = 0; i < data.length; i++) {
                    if (!data[i][0].equalsIgnoreCase(TableConstants.USER_CATG_STATE_ADMIN)
                            && !data[i][0].equalsIgnoreCase(TableConstants.USER_CATG_PORTAL_ADMIN)
                            && !data[i][0].equalsIgnoreCase(TableConstants.USER_CATG_STATE_SUPER_ADMIN)) {
                        userCatgList.add(new SelectItem(data[i][0], data[i][1]));
                    }
                }
            }
            UserMsgByCatgImpl.insertReadUserCatgMessage(Util.getEmpCodeLong(), Util.getUserStateCode(), Util.getUserOffCode(), Util.getUserCategory());
        } catch (VahanException vex) {
            JSFUtils.showMessage(vex.getMessage());
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void onLoad() {
        try {
            userMsgType = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("userMsgType");
            if (!(CommonUtils.isNullOrBlank(userMsgType)) && (userMsgType.equals("by_catg"))) {
                userMessagesListByCatg = UserMsgByCatgImpl.getMessagesListByCatg(Util.getUserCategory(), Util.getUserStateCode());
                readMsgByUserCatg = true;
                formHeader = "Message/Instruction/Orders from State Administrator";
            } else {
                formHeader = "Send Message/Order/Instruction to User by Category";
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            PrimeFaces.current().dialog().closeDynamic(null);
            userMessagesListByCatg = null;
        } catch (Exception exp) {
            LOGGER.error(exp.toString() + " " + exp.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            PrimeFaces.current().dialog().closeDynamic(null);
            userMessagesListByCatg = null;
        }
    }

    public void handleClose() {
        messageText = "";
        userMessagesList.clear();
        userMsgByCatgList.clear();
        PrimeFaces.current().ajax().update("pnl_Message");
        PrimeFaces.current().ajax().update("pnl_SendMsg");
    }

    public void showMessageDialog() {
    }

    public void sendMessage() {
        String messageDate = DateUtils.getDateInDDMMYYYY_HHMMSS(new java.util.Date());


        if (CommonUtils.isNullOrBlank(getMessageText())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please enter message before clicking Send.", "Please enter message before clicking Send."));
            return;
        }

        if ((userMsgByCatgList == null) || (userMsgByCatgList.isEmpty())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select User Category/Categories before clicking Send.", "Please select User Category/Categories before clicking Send."));
            return;
        }



        try {
            UserMsgByCatgImpl.sendMsgByUserCatg(Util.getUserStateCode(), Util.getUserSeatOffCode(), getUserMsgByCatgList(), messageText);;
            UserMessageDobj userMsg = new UserMessageDobj();
            userMsg.setFromUser("You");
            userMsg.setMessage(messageText);
            userMsg.setMessageDate(messageDate);
            userMessagesList.add(userMsg);
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            PrimeFaces.current().dialog().closeDynamic(null);
        } catch (Exception exp) {
            LOGGER.error(exp.toString() + " " + exp.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            PrimeFaces.current().dialog().closeDynamic(null);
        }
        messageText = "";
        PrimeFaces.current().ajax().update("pnl_Message");
    }

    public List getUserCatgList() {
        return userCatgList;
    }

    /**
     * @param userCatgList the userCatgList to set
     */
    public void setUserCatgList(List userCatgList) {
        this.userCatgList = userCatgList;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    public String getState_name() {
        return state_name;
    }

    public void setUserMsgByCatgList(List userMsgByCatgList) {
        this.userMsgByCatgList = userMsgByCatgList;
    }

    public List getUserMsgByCatgList() {
        return userMsgByCatgList;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setUserMessagesList(List<UserMessageDobj> userMessagesList) {
        this.userMessagesList = userMessagesList;
    }

    public List<UserMessageDobj> getUserMessagesList() {
        return userMessagesList;
    }

    public void setFormHeader(String formHeader) {
        this.formHeader = formHeader;
    }

    public String getFormHeader() {
        return formHeader;
    }

    public void setUserMsgType(String userMsgType) {
        this.userMsgType = userMsgType;
    }

    public String getUserMsgType() {
        return userMsgType;
    }

    public void setUserMessagesListByCatg(List<UserMessageDobj> userMessagesListByCatg) {
        this.userMessagesListByCatg = userMessagesListByCatg;
    }

    public List<UserMessageDobj> getUserMessagesListByCatg() {
        return userMessagesListByCatg;
    }

    public void setReadMsgByUserCatg(boolean readMsgByUserCatg) {
        this.readMsgByUserCatg = readMsgByUserCatg;
    }

    public boolean isReadMsgByUserCatg() {
        return readMsgByUserCatg;
    }
}
