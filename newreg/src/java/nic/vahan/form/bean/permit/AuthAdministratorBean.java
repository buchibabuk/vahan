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
 * @author hcl
 */
@ManagedBean(name = "authAdmin")
@ViewScoped
public class AuthAdministratorBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(AuthAdministratorBean.class);
    @ManagedProperty(value = "#{pmt_owner_dtls}")
    private PermitOwnerDetailBean ownerBean;
    @ManagedProperty(value = "#{Permit_Dtls_bean}")
    private PermitDetailBean permit_Dtls_bean;
    PermitOwnerDetailDobj owner_dobj = null;
    PermitDetailDobj pmtDobj = null;
    PermitHomeAuthDobj homeAuthDobj = new PermitHomeAuthDobj();
    private Date minDate = null;
    private Date maxDate = new Date();
    private String prvAuthValidity = "";

    public void get_details() {
        try {
            prvAuthValidity = "";
            PermitOwnerDetailImpl ownerImpl = new PermitOwnerDetailImpl();
            if (CommonUtils.isNullOrBlank(homeAuthDobj.getRcptNo().toUpperCase())) {
                throw new VahanException("Please fill the receipt no.");
            }
            AuthAdministratorImpl authAdminImpl = new AuthAdministratorImpl();
            minDate = authAdminImpl.checkRegnNoWithRcptNo(homeAuthDobj.getRegnNo().toUpperCase(), homeAuthDobj.getRcptNo().toUpperCase());
            if (minDate != null) {
                owner_dobj = ownerImpl.setVtOwnerDtlsOnlyDisplay(homeAuthDobj.getRegnNo().toUpperCase(), Util.getUserStateCode());
                if (owner_dobj == null) {
                    throw new VahanException("Owner Dtls Not found");
                }
                ownerBean.setValueinDOBJ(owner_dobj);
                permit_Dtls_bean.permitComponentReadOnly(true);
                pmtDobj = PermitDetailImpl.getPermitdetailsFromRegnNo(homeAuthDobj.getRegnNo().toUpperCase());
                if (pmtDobj == null) {
                    throw new VahanException("Permit Details Not Found");
                }
                if (!(pmtDobj.getPmt_type() == Integer.valueOf(TableConstants.NATIONAL_PERMIT)
                        || pmtDobj.getPmt_type() == Integer.valueOf(TableConstants.AITP))) {
                    throw new VahanException("Permit type not a AITP/NP");
                }
                prvAuthValidity = authAdminImpl.authValidity(homeAuthDobj.getRegnNo().toUpperCase());
                permit_Dtls_bean.set_Permit_Dtls_dobj_to_bean(pmtDobj);
                if (Util.getUserStateCode().equalsIgnoreCase("UK") && pmtDobj.getPmt_type() == Integer.valueOf(TableConstants.AITP)) {
                    minDate = ServerUtil.dateRange(minDate, -3, 0, 0);
                }
            }
        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Error", e.getMessage(), FacesMessage.SEVERITY_ERROR);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public String save() {
        String redirect = "";
        try {
            if (owner_dobj == null || pmtDobj == null) {
                throw new VahanException("Owner/Permit details not found");
            }
            if (!(pmtDobj.getPmt_type() == Integer.valueOf(TableConstants.NATIONAL_PERMIT)
                    || pmtDobj.getPmt_type() == Integer.valueOf(TableConstants.AITP))) {
                throw new VahanException("Permit type not a AITP/NP");
            }
            if (CommonUtils.isNullOrBlank(homeAuthDobj.getAuthNo())) {
                throw new VahanException("Fill the Authorization No.");
            }
            if (homeAuthDobj.getAuthFrom() == null) {
                throw new VahanException("Fill the Authorization From Date");
            }
            if (homeAuthDobj.getAuthUpto() == null) {
                throw new VahanException("Fill the Authorization Upto Date");
            }
//            if (pmtDobj.getValid_upto().getTime() < new Date().getTime()) {
//                throw new VahanException("Main Permit Validity Expired");
//            }
            if (homeAuthDobj.getAuthFrom().getTime() > homeAuthDobj.getAuthUpto().getTime()) {
                throw new VahanException("Authorization from date not greater than authorization upto date");
            }
            if (ServerUtil.dateRange(homeAuthDobj.getAuthFrom(), 1, 0, -1).getTime() < homeAuthDobj.getAuthUpto().getTime()) {
                throw new VahanException("Authorization  valid only for one year");
            }
            homeAuthDobj.setPurCd(TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD);
            homeAuthDobj.setPmtNo(pmtDobj.getPmt_no().toUpperCase());
            homeAuthDobj.setRegnNo(owner_dobj.getRegn_no().toUpperCase());
            AuthAdministratorImpl impl = new AuthAdministratorImpl();
            boolean flag = impl.save(homeAuthDobj);
            if (flag) {
                redirect = "/ui/permit/permitAuthAdminstratorForm.xhtml?faces-redirect=true";
                JSFUtils.showMessagesInDialog("Information", "Your Data Save SuccessFully", FacesMessage.SEVERITY_INFO);
            }
        } catch (VahanException e) {
            redirect = "";
            JSFUtils.showMessagesInDialog("Information", e.getMessage(), FacesMessage.SEVERITY_INFO);
        } catch (Exception e) {
            redirect = "";
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return redirect;
    }

    public PermitOwnerDetailBean getOwnerBean() {
        return ownerBean;
    }

    public void setOwnerBean(PermitOwnerDetailBean ownerBean) {
        this.ownerBean = ownerBean;
    }

    public PermitOwnerDetailDobj getOwner_dobj() {
        return owner_dobj;
    }

    public void setOwner_dobj(PermitOwnerDetailDobj owner_dobj) {
        this.owner_dobj = owner_dobj;
    }

    public PermitHomeAuthDobj getHomeAuthDobj() {
        return homeAuthDobj;
    }

    public void setHomeAuthDobj(PermitHomeAuthDobj homeAuthDobj) {
        this.homeAuthDobj = homeAuthDobj;
    }

    public PermitDetailBean getPermit_Dtls_bean() {
        return permit_Dtls_bean;
    }

    public void setPermit_Dtls_bean(PermitDetailBean permit_Dtls_bean) {
        this.permit_Dtls_bean = permit_Dtls_bean;
    }

    public PermitDetailDobj getPmtDobj() {
        return pmtDobj;
    }

    public void setPmtDobj(PermitDetailDobj pmtDobj) {
        this.pmtDobj = pmtDobj;
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
