/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.PermitAdministratorImpl;
import nic.vahan.form.impl.permit.PermitOwnerDetailImpl;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author
 */
@ManagedBean(name = "adminSurrenderBean")
@ViewScoped
public class PermitSurrenderAdministratorBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(PermitAdministratorBean.class);
    private String userInfo = "";
    PermitAdministratorImpl adminImpl = new PermitAdministratorImpl();
    private PassengerPermitDetailDobj permitDetailsDobj;
    private boolean noOfTripsrendered = false;
    private List purCdList = new ArrayList();
    private String errorMsg = "";
    private List<PassengerPermitDetailDobj> passList = new ArrayList();
    private int userOffcd = 0;
    @ManagedProperty(value = "#{pmt_owner_dtls}")
    private PermitOwnerDetailBean ownerBean;
    private PermitOwnerDetailDobj ownerDobj;
    private PermitOwnerDetailImpl ownerImpl;

    public PermitSurrenderAdministratorBean() {
        permitDetailsDobj = new PassengerPermitDetailDobj();
        setUserOffcd(Util.getUserOffCode());
    }

    public void getPmtSurrenderDetails() {
        try {
            permitDetailsDobj = new PassengerPermitDetailDobj();
            passList = adminImpl.getPmtDetailsFromVtPmtTrans(userInfo.toUpperCase());
            if (passList.size() > 0) {
                PrimeFaces.current().executeScript("PF('permit_details_Dlg').show();");
            } else {
                throw new VahanException("Surrender permit details not found.");
            }
        } catch (VahanException e) {
            JSFUtils.showMessage(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Regn_no:- " + userInfo.toUpperCase() + " / " + e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage(TableConstants.SomthingWentWrong);
        }
    }

    public void pmtTransGetDetails() {
        try {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String pmt_no = params.get("pmt_no");
            String app_off_cd = params.get("app_off_cd");
            String regnNo = params.get("regnNo");
            if (Integer.parseInt(app_off_cd) != Util.getUserOffCode()) {
                throw new VahanException("Your office is not authorized to delete the surrender details of other office permit.");
            }
            this.permitDetailsDobj = adminImpl.getDetailsFromVtPmtTrans(pmt_no);
            ownerImpl = new PermitOwnerDetailImpl();
            ownerDobj = ownerImpl.set_Owner_appl_db_to_dobj(regnNo.toUpperCase(), null);
            if (ownerDobj != null) {
                ownerBean.setValueinDOBJ(ownerDobj);
            }
            PrimeFaces.current().executeScript("PF('permit_details_Dlg').hide();");
        } catch (VahanException e) {
            JSFUtils.showMessage(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Regn_no/Pmt_no:- " + userInfo.toUpperCase() + " / " + e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage(TableConstants.SomthingWentWrong);
        }
    }

    public String deletePmtTransDetails() {
        FacesMessage message = null;
        String returnUrl = "";
        try {
            boolean flag = adminImpl.moveVtToVHpmtTransaction(this.permitDetailsDobj.getPmt_no(), this.permitDetailsDobj.getRegnNo());
            if (flag) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Successfully deleted your details");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                returnUrl = "/ui/permit/permitSurrenderAdminstratorForm.xhtml?faces-redirect=true";
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Data is not Deleted");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            }
        } catch (VahanException e) {
            JSFUtils.showMessage(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage(TableConstants.SomthingWentWrong);
        }
        return returnUrl;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public boolean isNoOfTripsrendered() {
        return noOfTripsrendered;
    }

    public void setNoOfTripsrendered(boolean noOfTripsrendered) {
        this.noOfTripsrendered = noOfTripsrendered;
    }

    public List getPurCdList() {
        return purCdList;
    }

    public void setPurCdList(List purCdList) {
        this.purCdList = purCdList;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public List<PassengerPermitDetailDobj> getPassList() {
        return passList;
    }

    public void setPassList(List<PassengerPermitDetailDobj> passList) {
        this.passList = passList;
    }

    public int getUserOffcd() {
        return userOffcd;
    }

    public void setUserOffcd(int userOffcd) {
        this.userOffcd = userOffcd;
    }

    public PassengerPermitDetailDobj getPermitDetailsDobj() {
        return permitDetailsDobj;
    }

    public void setPermitDetailsDobj(PassengerPermitDetailDobj permitDetailsDobj) {
        this.permitDetailsDobj = permitDetailsDobj;
    }

    public PermitOwnerDetailBean getOwnerBean() {
        return ownerBean;
    }

    public void setOwnerBean(PermitOwnerDetailBean ownerBean) {
        this.ownerBean = ownerBean;
    }
}
