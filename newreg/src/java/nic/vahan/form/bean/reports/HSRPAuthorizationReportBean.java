/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.reports.HSRPAuthorizationReportDobj;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author nic
 */
@ManagedBean(name = "hsrpAuthRptBean")
@ViewScoped
public class HSRPAuthorizationReportBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(HSRPAuthorizationReportBean.class);
    private HSRPAuthorizationReportDobj dobj = null;

    public HSRPAuthorizationReportBean() {
        Map map;
        map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        String appl_no = (String) map.get("applno");
        boolean isApproved = (Boolean) map.get("isApproved");
        dobj = new HSRPAuthorizationReportDobj();
        String dealerCd = null;
        try {
            if (Util.getUserCategory() != null && Util.getUserCategory().equals(TableConstants.User_Dealer)) {
                if (Util.getEmpCode() != null) {
                    Long user_cd = Long.parseLong(Util.getEmpCode());
                    Map makerAndDealerDetail = OwnerImpl.getDealerDetail(user_cd);
                    if (makerAndDealerDetail != null && makerAndDealerDetail.get("dealer_cd") != null) {
                        dealerCd = makerAndDealerDetail.get("dealer_cd").toString();
                    }
                }
            }

            dobj = PrintDocImpl.getHsrpAuthorizationReportDobj(appl_no, isApproved, dealerCd);
            if (dobj != null && dobj.getRegnNo() != null && !dobj.getRegnNo().equals("") && dobj.getChasiNo() != null && !dobj.getChasiNo().equals("")) {
                dobj.setQrText("Vehicle No: " + dobj.getRegnNo() + " Chassis No: " + dobj.getChasiNo());
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
    }

    /**
     * @return the dobj
     */
    public HSRPAuthorizationReportDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(HSRPAuthorizationReportDobj dobj) {
        this.dobj = dobj;
    }
}
