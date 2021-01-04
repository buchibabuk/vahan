/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.permit.PermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitHomeAuthDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.AuthAdministratorImpl;
import nic.vahan.form.impl.permit.PermitDetailImpl;
import nic.vahan.form.impl.permit.PermitOwnerDetailImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author DELL
 */
@ManagedBean(name = "deleteAuthAdmin")
@ViewScoped
public class DeletePermitHomeAuthBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(DeletePermitHomeAuthBean.class);
    @ManagedProperty(value = "#{pmt_owner_dtls}")
    private PermitOwnerDetailBean ownerBean;
    @ManagedProperty(value = "#{Permit_Dtls_bean}")
    private PermitDetailBean permit_Dtls_bean;
    private PermitOwnerDetailDobj owner_dobj = null;
    private PermitDetailDobj pmtDobj = null;
    private PermitHomeAuthDobj homeAuthDobj = new PermitHomeAuthDobj();
    private Date minDate = null;
    private Date maxDate = new Date();
    private String prvAuthValidity = "";

    public void get_details() {
        try {
            prvAuthValidity = "";
            PermitOwnerDetailImpl ownerImpl = new PermitOwnerDetailImpl();

            AuthAdministratorImpl authAdminImpl = new AuthAdministratorImpl();

            owner_dobj = ownerImpl.setVtOwnerDtlsOnlyDisplay(homeAuthDobj.getRegnNo().toUpperCase(), Util.getUserStateCode());
            if (owner_dobj == null) {
                throw new VahanException("Owner Dtls Not found");
            }else if(!"A,Y".contains(owner_dobj.getOwnerStatus())){
                throw new VahanException("Vehicle is Not Active");
            }
            ownerBean.setValueinDOBJ(owner_dobj);
            permit_Dtls_bean.permitComponentReadOnly(true);
            pmtDobj = PermitDetailImpl.getPermitdetailsFromRegnNo(homeAuthDobj.getRegnNo().toUpperCase());
            
            prvAuthValidity = authAdminImpl.authValidity(homeAuthDobj.getRegnNo().toUpperCase());
            permit_Dtls_bean.set_Permit_Dtls_dobj_to_bean(pmtDobj);

        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Error", e.getMessage(), FacesMessage.SEVERITY_ERROR);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public String deleteHomeAuth() {
        String redirect = "";
        boolean flag = false;
        boolean isRegnNoExistInVhPmtOrVtPmt=false;
        try {
            String regnNo=homeAuthDobj.getRegnNo().toUpperCase();
            AuthAdministratorImpl authAdminImpl = new AuthAdministratorImpl();
            isRegnNoExistInVhPmtOrVtPmt=authAdminImpl.isRegnNoExistInVhPermitOrVtPermit(regnNo);
            if(!isRegnNoExistInVhPmtOrVtPmt){
                flag=authAdminImpl.deleteHomeAuth(regnNo);
            }else{
              JSFUtils.showMessagesInDialog("Error", "Autorization details are not deleted as permit exist or surrendered.", FacesMessage.SEVERITY_ERROR);  
            }            
            if (flag) {
                redirect = "/ui/permit/form_delete_permit_home_auth_admin.xhtml?faces-redirect=true";
                JSFUtils.showMessagesInDialog("Information", "Your Data Deleted SuccessFully", FacesMessage.SEVERITY_INFO);
            }
        } catch (Exception e) {
            JSFUtils.showMessagesInDialog("Error", e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
        return redirect;
    }

    public PermitOwnerDetailDobj getOwner_dobj() {
        return owner_dobj;
    }

    public void setOwner_dobj(PermitOwnerDetailDobj owner_dobj) {
        this.owner_dobj = owner_dobj;
    }

    public PermitDetailDobj getPmtDobj() {
        return pmtDobj;
    }

    public void setPmtDobj(PermitDetailDobj pmtDobj) {
        this.pmtDobj = pmtDobj;
    }

    public PermitHomeAuthDobj getHomeAuthDobj() {
        return homeAuthDobj;
    }

    public void setHomeAuthDobj(PermitHomeAuthDobj homeAuthDobj) {
        this.homeAuthDobj = homeAuthDobj;
    }

    public PermitOwnerDetailBean getOwnerBean() {
        return ownerBean;
    }

    public void setOwnerBean(PermitOwnerDetailBean ownerBean) {
        this.ownerBean = ownerBean;
    }

    public PermitDetailBean getPermit_Dtls_bean() {
        return permit_Dtls_bean;
    }

    public void setPermit_Dtls_bean(PermitDetailBean permit_Dtls_bean) {
        this.permit_Dtls_bean = permit_Dtls_bean;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public String getPrvAuthValidity() {
        return prvAuthValidity;
    }

    public void setPrvAuthValidity(String prvAuthValidity) {
        this.prvAuthValidity = prvAuthValidity;
    }
}
