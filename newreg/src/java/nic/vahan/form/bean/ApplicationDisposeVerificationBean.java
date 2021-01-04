/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.ApplDisposeVerifyByAdminDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.ApplicationDisposeImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
//import org.primefaces.context.RequestContext;

/**
 *
 * @author ASHOK
 */
@ViewScoped
@ManagedBean(name = "applicationDisposeVerificationBean")
public class ApplicationDisposeVerificationBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ApplicationDisposeVerificationBean.class);
    private boolean render = false;
    private ApplDisposeVerifyByAdminDobj verifyByAdminDobj = new ApplDisposeVerifyByAdminDobj();
    private String vahanMessages = null;
    private List<Status_dobj> applStatus = applStatus = new ArrayList<>();

    public ApplicationDisposeVerificationBean() {
        verifyByAdminDobj.setAppl_no(null);
        verifyByAdminDobj.setState_cd(Util.getUserStateCode());
        verifyByAdminDobj.setEmp_cd(Util.getEmpCode());
        if (Util.getSelectedSeat() != null) {
            verifyByAdminDobj.setOff_cd(Util.getSelectedSeat().getOff_cd());
        }
    }

    public void showDetails() {
        vahanMessages = null;
        verifyByAdminDobj.setRemarks(null);
        applStatus = null;
        render = false;
        if (verifyByAdminDobj.getState_cd() == null
                || verifyByAdminDobj.getOff_cd() == 0
                || verifyByAdminDobj.getEmp_cd() == null) {
            vahanMessages = "Current Session for this Page is Expired, Please try again Later...";
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", vahanMessages));
            return;
        }

        if (verifyByAdminDobj.getAppl_no() == null || verifyByAdminDobj.getAppl_no().trim().length() < 1) {
            vahanMessages = "Invalid Application No, Please Provide Valid Application No";
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", vahanMessages));
            return;
        }

        try {
            applStatus = ServerUtil.applicationStatusByApplNo(verifyByAdminDobj.getAppl_no(), verifyByAdminDobj.getState_cd());
            if (applStatus == null || applStatus.isEmpty() || applStatus.size() < 1) {
                vahanMessages = "Either Application No is Invalid or Application No is Approved.Please try with different Application No";
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", vahanMessages));
                return;
            } else {
                if (applStatus.get(0).getOff_cd() != verifyByAdminDobj.getOff_cd()) {
                    vahanMessages = "This Application No is Pending at Different Office -[" + applStatus.get(0).getOffName() + "]" + " State-[" + applStatus.get(0).getStateName() + "]";
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", vahanMessages));
                    return;
                }

                ApplicationDisposeImpl disposeImpl = new ApplicationDisposeImpl();
                ApplDisposeVerifyByAdminDobj adminDobj = disposeImpl.getApplicationDisposedVerificationDetails(verifyByAdminDobj.getAppl_no());
                if (adminDobj != null) {
                    vahanMessages = "This Application No is Already Verified for Dispose, Please Try with Different Application No";
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", vahanMessages));
                    return;
                }

                render = true;//for render the more information for the Application no on the page
            }

        } catch (VahanException vex) {
            vahanMessages = vex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vahanMessages, vahanMessages));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            vahanMessages = TableConstants.SomthingWentWrong;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vahanMessages, vahanMessages));
        }
    }

    public void applicationDisposeVerification() {
        try {
            ApplicationDisposeImpl impl = new ApplicationDisposeImpl();
            impl.ApplicationDisposeVerification(verifyByAdminDobj);
        } catch (VahanException vex) {
            vahanMessages = vex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vahanMessages, vahanMessages));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            vahanMessages = TableConstants.SomthingWentWrong;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vahanMessages, vahanMessages));
            return;
        }

        vahanMessages = "Verification Process for Application Dispose has been Successfully done for the Application Number " + verifyByAdminDobj.getAppl_no();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, vahanMessages, vahanMessages));
        vahanMessages = null;
        verifyByAdminDobj.setRemarks(null);
        verifyByAdminDobj.setAppl_no(null);
        applStatus = null;
        render = false;
        PrimeFaces.current().ajax().update("applStatusInfo");
    }

    /**
     * @return the render
     */
    public boolean isRender() {
        return render;
    }

    /**
     * @param render the render to set
     */
    public void setRender(boolean render) {
        this.render = render;
    }

    /**
     * @return the verifyByAdminDobj
     */
    public ApplDisposeVerifyByAdminDobj getVerifyByAdminDobj() {
        return verifyByAdminDobj;
    }

    /**
     * @param verifyByAdminDobj the verifyByAdminDobj to set
     */
    public void setVerifyByAdminDobj(ApplDisposeVerifyByAdminDobj verifyByAdminDobj) {
        this.verifyByAdminDobj = verifyByAdminDobj;
    }

    /**
     * @return the applStatus
     */
    public List<Status_dobj> getApplStatus() {
        return applStatus;
    }

    /**
     * @param applStatus the applStatus to set
     */
    public void setApplStatus(List<Status_dobj> applStatus) {
        this.applStatus = applStatus;
    }
}
