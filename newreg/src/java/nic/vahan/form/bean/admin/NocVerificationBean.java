/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.NocDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.admin.NocVerificationImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "nocVerifyBean")
@ViewScoped
public class NocVerificationBean implements Serializable {

    private List state_list;
    private List office_list;
    private NocDobj noc_dobj = new NocDobj();
    private String successNocVerificationMessg = "";
    private Date currentDate = new Date();
    private static Logger LOGGER = Logger.getLogger(NocVerificationBean.class);

    public NocVerificationBean() {
        state_list = new ArrayList();
        office_list = new ArrayList();

        state_list = MasterTableFiller.getStateList();
    }

    public void updateRtoFromStateListener(AjaxBehaviorEvent even) {
        office_list.clear();
        office_list = MasterTableFiller.getOfficeList(getNoc_dobj().getState_from());
        if (Util.getUserStateCode().equalsIgnoreCase(getNoc_dobj().getState_from())) {
            Iterator ite = office_list.iterator();
            while (ite.hasNext()) {
                SelectItem obj = (SelectItem) ite.next();
                if (Integer.parseInt(obj.getValue().toString()) == Util.getSelectedSeat().getOff_cd()) {
                    office_list.remove(obj);
                    break;
                }
            }
        }
    }

    public void verification() {
        NocVerificationImpl noc_ver_impl = new NocVerificationImpl();
        Exception ex = null;
        try {

            Date vahan4StartDate = ServerUtil.getVahan4StartDate(getNoc_dobj().getState_from(), getNoc_dobj().getOff_from());
            if (vahan4StartDate != null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", TableConstants.INVALID_FOR_NOC_VERIFICATION));
                return;
            }

            NocDobj nocDobj = ServerUtil.getChasiNoExist(getNoc_dobj().getChasiNo());

            if (nocDobj == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", TableConstants.NOC_DATA_NOT_FOUND_CHASSIS));
                return;
            }

            if (nocDobj.getNoc_dt() == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", TableConstants.NOC_DATA_NOT_FOUND_CHASSIS));
                return;
            }

            int diffInDays = (int) ((currentDate.getTime() - nocDobj.getNoc_dt().getTime()) / (1000 * 60 * 60 * 24));
            if (diffInDays < 1) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", TableConstants.NOC_DAY_ERROR));
                return;
            }

            noc_ver_impl.insertIntoNOCVerification(noc_dobj);
            setSuccessNocVerificationMessg("Noc Verification Done Succesfully");
            PrimeFaces.current().ajax().update("showNocVerifiedMessg");
            PrimeFaces.current().executeScript("PF('successNocVerfiedDialog').show()");
        } catch (VahanException vex) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", vex.getMessage()));
        } catch (Exception e) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", "Problem In Verification of Data"));
        }
    }

    /**
     * @return the state_list
     */
    public List getState_list() {
        return state_list;
    }

    /**
     * @param state_list the state_list to set
     */
    public void setState_list(List state_list) {
        this.state_list = state_list;
    }

    /**
     * @return the office_list
     */
    public List getOffice_list() {
        return office_list;
    }

    /**
     * @param office_list the office_list to set
     */
    public void setOffice_list(List office_list) {
        this.office_list = office_list;
    }

    /**
     * @return the noc_dobj
     */
    public NocDobj getNoc_dobj() {
        return noc_dobj;
    }

    /**
     * @param noc_dobj the noc_dobj to set
     */
    public void setNoc_dobj(NocDobj noc_dobj) {
        this.noc_dobj = noc_dobj;
    }

    /**
     * @return the successNocVerificationMessg
     */
    public String getSuccessNocVerificationMessg() {
        return successNocVerificationMessg;
    }

    /**
     * @param successNocVerificationMessg the successNocVerificationMessg to set
     */
    public void setSuccessNocVerificationMessg(String successNocVerificationMessg) {
        this.successNocVerificationMessg = successNocVerificationMessg;
    }

    /**
     * @return the currentDate
     */
    public Date getCurrentDate() {
        return currentDate;
    }

    /**
     * @param currentDate the currentDate to set
     */
    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }
}
