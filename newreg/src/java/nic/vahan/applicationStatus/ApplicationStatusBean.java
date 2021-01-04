/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.applicationStatus;

import nic.vahan.form.bean.common.*;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.rto.vahan.common.VahanException;

/**
 *
 * @author acer
 */
@ManagedBean(name = "statusBean")
@ViewScoped
public class ApplicationStatusBean extends AbstractApplBean implements Serializable {

    private boolean renderApplStatus = true;
    private String[] action_desc = null;
    private int statusOfApplication;

    public ApplicationStatusBean() throws VahanException {

        ApplicationStatusImpl impl = new ApplicationStatusImpl();

        if (getAppl_details() == null
                || getAppl_details().getCurrent_state_cd() == null
                || getAppl_details().getCurrent_off_cd() == 0
                || getAppl_details().getPur_cd() == 0) {
            renderApplStatus = false;
            return;
        }
        if (getAppl_details() != null) {

            action_desc = impl.applStatus(appl_details);
            statusOfApplication = impl.currentApplStatus(appl_details);

        }
    }

    /**
     * @return the action_desc
     */
    public String[] getAction_desc() {
        return action_desc;
    }

    /**
     * @param action_desc the action_desc to set
     */
    public void setAction_desc(String[] action_desc) {
        this.action_desc = action_desc;
    }

    /**
     * @return the statusOfApplication
     */
    public int getStatusOfApplication() {
        return statusOfApplication;
    }

    /**
     * @param statusOfApplication the statusOfApplication to set
     */
    public void setStatusOfApplication(int statusOfApplication) {
        this.statusOfApplication = statusOfApplication;
    }

    public boolean isRenderApplStatus() {
        return renderApplStatus;
    }

    public void setRenderApplStatus(boolean renderApplStatus) {
        this.renderApplStatus = renderApplStatus;
    }
}
