/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

/**
 *
 * @author acer
 */
public class TmConfigurationUserMessagingDobj implements Serializable {

    private boolean viewMsgsFront;
    private boolean stateCdRtoCdJoined;
    private boolean fileUpload;
    private boolean sortByOffName;
    private boolean sortByOffCd;
    private boolean showUnclosedMsgsOnly;
    private boolean sendCopyStateAdmin;
    private boolean userMsgByCatg;

    public void setUserMsgByCatg(boolean userMsgByCatg) {
        this.userMsgByCatg = userMsgByCatg;
    }

    public boolean isUserMsgByCatg() {
        return userMsgByCatg;
    }

    public void setSendCopyStateAdmin(boolean sendCopyStateAdmin) {
        this.sendCopyStateAdmin = sendCopyStateAdmin;
    }

    public boolean isSendCopyStateAdmin() {
        return sendCopyStateAdmin;
    }

    public void setShowUnclosedMsgsOnly(boolean showUnclosedMsgsOnly) {
        this.showUnclosedMsgsOnly = showUnclosedMsgsOnly;
    }

    public boolean isShowUnclosedMsgsOnly() {
        return showUnclosedMsgsOnly;
    }

    public void setFileUpload(boolean fileUpload) {
        this.fileUpload = fileUpload;
    }

    public boolean isFileUpload() {
        return fileUpload;
    }

    public void setViewMsgsFront(boolean viewMsgsFront) {
        this.viewMsgsFront = viewMsgsFront;
    }

    public void setStateCdRtoCdJoined(boolean stateCdRtoCdJoined) {
        this.stateCdRtoCdJoined = stateCdRtoCdJoined;
    }

    public boolean isViewMsgsFront() {
        return viewMsgsFront;
    }

    public boolean isStateCdRtoCdJoined() {
        return stateCdRtoCdJoined;
    }

    public boolean isSortByOffName() {
        return sortByOffName;
    }

    public boolean isSortByOffCd() {
        return sortByOffCd;
    }

    public void setSortByOffName(boolean sortByOffName) {
        this.sortByOffName = sortByOffName;
    }

    public void setSortByOffCd(boolean sortByOffCd) {
        this.sortByOffCd = sortByOffCd;
    }
}
