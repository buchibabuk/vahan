/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.admin;

import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.admin.UserMsgImpl;
import nic.vahan.form.dobj.UserInfoForMsgDobj;
import nic.vahan.form.dobj.UserMessageDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.feature.SortFeature;
import org.primefaces.PrimeFaces;

/**
 *
 * @author acer
 */
@ManagedBean(name = "userMsg")
@ViewScoped
public class UserMsgBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(UserMsgBean.class);
    private List<UserInfoForMsgDobj> usersList;
    private List<UserInfoForMsgDobj> rtoUsersList;
    private List<UserMessageDobj> userMessagesList = null;
    private List<UserMessageDobj> unreadUserMessages = null;
    private String messageText = "";
    private Long user_code = null;
    private String state_code = null;
    private int off_cd = -1;
    private String stateCodeSelected;
    private int offCodeSelected;
    private int fileUploadOffCode;
    private String fileUploadStateCode;
    private boolean renderViewUnreadMsgs = false;
    private boolean renderStateCdRtoCdJoined = false;
    private boolean sortByOffName = false;
    private boolean sortByOffCd = false;
    private boolean fileAttached;
    private boolean renderFileUpload = false;
    private boolean showUnclosedMsgsOnly = false;
    private boolean showClosedMsgs = false;
    private boolean sendCopyStateAdmin = true;
    private boolean renderUserMsgByCatgButton = false;
    private String dmsFileServerAPIString = "";
    private String docRefNo = "";
    private TmConfigurationDobj tmConfigDobj;

    public void UserMsgBean() {
    }

    @PostConstruct
    public void init() {
        stateCodeSelected = Util.getUserStateCode();
        offCodeSelected = Util.getUserSeatOffCode();
        try {
            tmConfigDobj = Util.getTmConfiguration();
            if (tmConfigDobj.getTmUserMessagingConfigDobj() != null && tmConfigDobj.getTmUserMessagingConfigDobj().isViewMsgsFront()) {
                renderViewUnreadMsgs = true;
            }
            if (tmConfigDobj.getTmUserMessagingConfigDobj() != null && tmConfigDobj.getTmUserMessagingConfigDobj().isStateCdRtoCdJoined()) {
                renderStateCdRtoCdJoined = true;
            }
            if (tmConfigDobj.getTmUserMessagingConfigDobj() != null && tmConfigDobj.getTmUserMessagingConfigDobj().isFileUpload()) {
                renderFileUpload = true;
            }
            if (tmConfigDobj.getTmUserMessagingConfigDobj() != null && tmConfigDobj.getTmUserMessagingConfigDobj().isSortByOffCd()) {
                sortByOffCd = true;
            }
            if (tmConfigDobj.getTmUserMessagingConfigDobj() != null && tmConfigDobj.getTmUserMessagingConfigDobj().isSortByOffName()) {
                sortByOffName = true;
            }
            if (tmConfigDobj.getTmUserMessagingConfigDobj() != null && tmConfigDobj.getTmUserMessagingConfigDobj().isShowUnclosedMsgsOnly()) {
                showUnclosedMsgsOnly = true;
            }
            if (tmConfigDobj.getTmUserMessagingConfigDobj() != null && !tmConfigDobj.getTmUserMessagingConfigDobj().isSendCopyStateAdmin()) {
                sendCopyStateAdmin = false;
            }
            if (tmConfigDobj.getTmUserMessagingConfigDobj() != null && tmConfigDobj.getTmUserMessagingConfigDobj().isUserMsgByCatg()
                    && Util.getUserCategory().equalsIgnoreCase(TableConstants.USER_CATG_STATE_ADMIN)) {
                renderUserMsgByCatgButton = true;
            }
            usersList = UserMsgImpl.getUsersListForMsg(Long.parseLong(Util.getEmpCode()));

        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
        } catch (Exception exp) {
            LOGGER.error(exp.toString() + " " + exp.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }

    }

    public List<UserMessageDobj> showMessageDialog(Long userCode, String stateCode, String offCd) {
        HttpSession session = null;
        user_code = userCode;
        state_code = stateCode;
        if (renderStateCdRtoCdJoined == true && (offCd.matches("([A-Z]{2}[0-9]{3})"))) {
            off_cd = Integer.parseInt(offCd.substring(2, offCd.length() - 1));
        } else {
            off_cd = Integer.parseInt(offCd);
        }
        messageText = "";
        try {

            userMessagesList = UserMsgImpl.getUserMessagesList(userCode, state_code, Util.getEmpCode(), Util.getUserStateCode(), showUnclosedMsgsOnly);

            session = Util.getSession();
            if (session != null && session.getAttribute("forwardedMsgDobj") != null) {
                UserMessageDobj forwardedMsgDobj = (UserMessageDobj) session.getAttribute("forwardedMsgDobj");
                messageText = "Forwarded from " + forwardedMsgDobj.getFromUser() + " ("
                        + forwardedMsgDobj.getMessageDate() + ") " + forwardedMsgDobj.getMessage();
                if (!CommonUtils.isNullOrBlank(forwardedMsgDobj.getDocRefNo())) {
                    fileAttached = true;
                    fileUploadOffCode = forwardedMsgDobj.getUploadDocOffCode();
                    fileUploadStateCode = forwardedMsgDobj.getUploadDocStateCode();
                    docRefNo = forwardedMsgDobj.getDocRefNo();
                    PrimeFaces.current().ajax().update("pnl_view_docs");
                }
                session.removeAttribute("forwardedMsgDobj");
                PrimeFaces.current().ajax().update("pnl_msgbox");
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            PrimeFaces.current().dialog().closeDynamic(null);
            return null;
        } catch (Exception exp) {
            LOGGER.error(exp.toString() + " " + exp.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            PrimeFaces.current().dialog().closeDynamic(null);
            return null;
        }
        return userMessagesList;
    }

    public List<UserMessageDobj> viewMessages() throws VahanException {
        try {

            unreadUserMessages = UserMsgImpl.getUnreadUserMessages(Long.parseLong(Util.getEmpCode()));
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            PrimeFaces.current().dialog().closeDynamic(null);
            return null;
        } catch (Exception exp) {
            LOGGER.error(exp.toString() + " " + exp.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            PrimeFaces.current().dialog().closeDynamic(null);
            return null;
        }
        return unreadUserMessages;

    }

    public void sendMessage() {
        String messageDate = DateUtils.getDateInDDMMYYYY_HHMMSS(new java.util.Date());
        String messageId = "";

        if (CommonUtils.isNullOrBlank(getMessageText())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please enter message before clicking Send.", "Please enter message before clicking Send."));
            return;
        }

        try {
            messageId = UserMsgImpl.sendMsg(Util.getEmpCode(), stateCodeSelected, offCodeSelected, messageText, user_code, state_code, off_cd, docRefNo, fileUploadStateCode, fileUploadOffCode, sendCopyStateAdmin);
            UserMessageDobj userMsg = new UserMessageDobj();
            userMsg.setFromUser("You");
            userMsg.setMessage(messageText);
            userMsg.setMessageDate(messageDate);
            userMsg.setDocRefNo(docRefNo);
            userMsg.setUploadDocStateCode(fileUploadStateCode);
            userMsg.setUploadDocOffCode(fileUploadOffCode);
            userMsg.setMessageId(messageId);
            userMessagesList.add(userMsg);
            fileAttached = false;
            docRefNo = "";
            fileUploadStateCode = "";
            fileUploadOffCode = 0;
            PrimeFaces.current().ajax().update("pnl_view_docs");
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

    public void handleClose() {
        try {

            UserMsgImpl.markUserMessageAsRead(user_code, Util.getEmpCode());
            fileAttached = false;
            PrimeFaces.current().ajax().update("view_docs");

            FacesContext.getCurrentInstance().getExternalContext().redirect(ServerUtil.getIpPath() + "/vahan/ui/admin/formUserMsg.xhtml?faces-redirect=true");
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
        } catch (Exception exp) {
            LOGGER.error(exp.toString() + " " + exp.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void onRowSelectToggler(UserInfoForMsgDobj dobj) {
        String selectedRTOStateCode = dobj.getStateCd();
        try {
            rtoUsersList = UserMsgImpl.getRtoUsersListForMsg(Long.parseLong(Util.getEmpCode()), selectedRTOStateCode, renderStateCdRtoCdJoined, sortByOffCd, sortByOffName);
            FacesContext facesContext = FacesContext.getCurrentInstance();
            UIViewRoot view = FacesContext.getCurrentInstance().getViewRoot();
            DataTable table = (DataTable) view.findComponent("masterLayout:tbl_userlist:tbl_rtouserlist");
            if (table == null) {
                return;
            }
            SortFeature sortFeature = new SortFeature();
            sortFeature.singleSort(facesContext, table);
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
        } catch (Exception exp) {
            LOGGER.error(exp.toString() + " " + exp.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }

    }

    public void forwardMessage(String fromUser, String msgDate, String message, String uploadStateCd, int uploadOffCd, String docRefNo) {
        HttpSession session = null;
        try {
            UserMessageDobj forwardedMsgDobj = new UserMessageDobj();
            forwardedMsgDobj.setFromUser(fromUser);
            forwardedMsgDobj.setMessageDate(msgDate);
            forwardedMsgDobj.setMessage(message);
            forwardedMsgDobj.setUploadDocStateCode(uploadStateCd);
            forwardedMsgDobj.setUploadDocOffCode(uploadOffCd);
            forwardedMsgDobj.setDocRefNo(docRefNo);
            session = Util.getSession();
            session.removeAttribute("forwardedMsgDobj");
            UserMsgImpl.markUserMessageAsRead(user_code, Util.getEmpCode());
            usersList = UserMsgImpl.getUsersListForMsg(Long.parseLong(Util.getEmpCode()));
            session.setAttribute("forwardedMsgDobj", forwardedMsgDobj);
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            PrimeFaces.current().ajax().update("msg");
        } catch (Exception exp) {
            LOGGER.error(exp.toString() + " " + exp.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            PrimeFaces.current().ajax().update("msg");
        }

        PrimeFaces.current().executeScript("PF('dlg').hide()");
    }

    public void closeRequest(String messageId) {
        try {
            UserMsgImpl.closeRequest(messageId);
            PrimeFaces.current().ajax().update("tbl_usermessageslist");
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            PrimeFaces.current().ajax().update("msg");
        } catch (Exception exp) {
            LOGGER.error(exp.toString() + " " + exp.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            PrimeFaces.current().ajax().update("msg");
        }

    }

    public void uploadDocuments() throws VahanException {

        try {
            fileAttached = false;
            this.setDocRefNo(UserMsgImpl.getDocRefNo());
            if (UserMsgImpl.chkGeneratedDocRefNo(this.getDocRefNo())) {
                throw new VahanException("Document Reference number already exists.");
            }
            PrimeFaces.current().ajax().update("view_docs");
            dmsFileServerAPIString = ServerUtil.getVahanPgiUrl(TableConstants.DMS_USRMSG_URL);
            fileUploadOffCode = offCodeSelected;
            fileUploadStateCode = stateCodeSelected;
            if (dmsFileServerAPIString.isEmpty()) {
                throw new VahanException("Problem with uploading documents.");
            }
            dmsFileServerAPIString = dmsFileServerAPIString.replace("ApplNo", docRefNo);
            dmsFileServerAPIString = dmsFileServerAPIString.replace("state_cd", fileUploadStateCode);
            dmsFileServerAPIString = dmsFileServerAPIString.replace("regn_no", "NA");
            dmsFileServerAPIString = dmsFileServerAPIString.replace("off_name", UserMsgImpl.getOffNameByOffCode(stateCodeSelected, offCodeSelected));
            dmsFileServerAPIString = dmsFileServerAPIString.replace("pur_cd", TableConstants.VM_USER_MESSAGING + "");
            dmsFileServerAPIString = dmsFileServerAPIString + TableConstants.SECURITY_KEY;
            fileAttached = true;
            PrimeFaces.current().ajax().update("pnl_view_docs");
        } catch (VahanException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            PrimeFaces.current().dialog().closeDynamic(null);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            PrimeFaces.current().dialog().closeDynamic(null);
        }
    }

    public void viewUploadedDocuments(String stateCode, int offCode, String applNo) throws VahanException {
        try {
            dmsFileServerAPIString = ServerUtil.getVahanPgiUrl(TableConstants.DMS_USRMSG_URL);
            if (dmsFileServerAPIString.isEmpty()) {
                throw new VahanException("Problem in view documents.");
            }
            dmsFileServerAPIString = dmsFileServerAPIString.replace("ApplNo", applNo);
            dmsFileServerAPIString = dmsFileServerAPIString.replace("state_cd", stateCode);
            dmsFileServerAPIString = dmsFileServerAPIString.replace("regn_no", "NA");
            dmsFileServerAPIString = dmsFileServerAPIString.replace("off_name", UserMsgImpl.getOffNameByOffCode(stateCode, offCode));
            dmsFileServerAPIString = dmsFileServerAPIString.replace("pur_cd", TableConstants.VM_USER_MESSAGING + "");
            dmsFileServerAPIString = dmsFileServerAPIString + TableConstants.SECURITY_KEY;
        } catch (VahanException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            PrimeFaces.current().dialog().closeDynamic(null);
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().closeDynamic(null);
        }
    }

    public void showUserMsgByCatg() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(ServerUtil.getIpPath() + "/vahan/ui/admin/formUserMsgByCatg.xhtml?faces-redirect=true");
        } catch (Exception exp) {
            LOGGER.error(exp.toString() + " " + exp.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }

    }

    public void handleShowClosedMsgs() {
        if (showClosedMsgs == true) {
            showUnclosedMsgsOnly = false;
        } else {
            showUnclosedMsgsOnly = true;
        }
    }

    public List<UserInfoForMsgDobj> getUsersList() {
        return usersList;
    }

    public void setUserMessagesList(List<UserMessageDobj> userMessagesList) {
        this.userMessagesList = userMessagesList;
    }

    public void setUsersList(List<UserInfoForMsgDobj> usersList) {
        this.usersList = usersList;
    }

    public List<UserMessageDobj> getUserMessagesList() {
        return userMessagesList;
    }

    public List<UserMessageDobj> getAllUserMessagesList() {
        return userMessagesList;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setRtoUsersList(List<UserInfoForMsgDobj> rtoUsersList) {
        this.rtoUsersList = rtoUsersList;
    }

    public List<UserInfoForMsgDobj> getRtoUsersList() {
        return rtoUsersList;
    }

    public void setUnreadUserMessages(List<UserMessageDobj> unreadUserMessages) {
        this.unreadUserMessages = unreadUserMessages;
    }

    public List<UserMessageDobj> getUnreadUserMessages() {
        return unreadUserMessages;
    }

    public void setRenderViewUnreadMsgs(boolean renderViewUnreadMsgs) {
        this.renderViewUnreadMsgs = renderViewUnreadMsgs;
    }

    public void setRenderStateCdRtoCdJoined(boolean renderStateCdRtoCdJoined) {
        this.renderStateCdRtoCdJoined = renderStateCdRtoCdJoined;
    }

    public boolean isRenderViewUnreadMsgs() {
        return renderViewUnreadMsgs;
    }

    public boolean isRenderStateCdRtoCdJoined() {
        return renderStateCdRtoCdJoined;
    }

    public void setDocRefNo(String docRefNo) {
        this.docRefNo = docRefNo;
    }

    public String getDocRefNo() {
        return docRefNo;
    }

    public void setFileAttached(boolean fileAttached) {
        this.fileAttached = fileAttached;
    }

    public boolean isFileAttached() {
        return fileAttached;
    }

    public void setDmsFileServerAPIString(String dmsFileServerAPIString) {
        this.dmsFileServerAPIString = dmsFileServerAPIString;
    }

    public String getDmsFileServerAPIString() {
        return dmsFileServerAPIString;
    }

    public void setFileUploadOffCode(int fileUploadOffCode) {
        this.fileUploadOffCode = fileUploadOffCode;
    }

    public void setFileUploadStateCode(String fileUploadStateCode) {
        this.fileUploadStateCode = fileUploadStateCode;
    }

    public int getFileUploadOffCode() {
        return fileUploadOffCode;
    }

    public String getFileUploadStateCode() {
        return fileUploadStateCode;
    }

    public void setRenderFileUpload(boolean renderFileUpload) {
        this.renderFileUpload = renderFileUpload;
    }

    public boolean isRenderFileUpload() {
        return renderFileUpload;
    }

    public void setShowUnclosedMsgsOnly(boolean showUnclosedMsgsOnly) {
        this.showUnclosedMsgsOnly = showUnclosedMsgsOnly;
    }

    public boolean isShowUnclosedMsgsOnly() {
        return showUnclosedMsgsOnly;
    }

    public void setSendCopyStateAdmin(boolean sendCopyStateAdmin) {
        this.sendCopyStateAdmin = sendCopyStateAdmin;
    }

    public boolean isSendCopyStateAdmin() {
        return sendCopyStateAdmin;
    }

    public boolean isRenderUserMsgByCatgButton() {
        return renderUserMsgByCatgButton;
    }

    public void setRenderUserMsgByCatgButton(boolean renderUserMsgByCatgButton) {
        this.renderUserMsgByCatgButton = renderUserMsgByCatgButton;
    }

    public boolean isShowClosedMsgs() {
        return showClosedMsgs;
    }

    public void setShowClosedMsgs(boolean showClosedMsgs) {
        this.showClosedMsgs = showClosedMsgs;
    }
}
