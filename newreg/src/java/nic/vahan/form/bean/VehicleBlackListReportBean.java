/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.VehicleBlackListReportDobj;
import nic.vahan.form.impl.PrintDocImpl;
import org.apache.log4j.Logger;

/**
 *
 * @author afzal
 */
@ManagedBean(name = "vchblklistreport")
@ViewScoped
public class VehicleBlackListReportBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(VehicleBlackListReportBean.class);
    private List<VehicleBlackListReportDobj> printCertDobj = new ArrayList<VehicleBlackListReportDobj>();
    private int selected_action_code;
    private boolean showActionTaken;

    public void setListBeans(List<VehicleBlackListReportDobj> listDobjs) {
        setPrintCertDobj(listDobjs);
    }

    @PostConstruct
    public void init() {
        try {
            selected_action_code = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("selected_role_cd").toString());
            if (selected_action_code == TableConstants.TM_ROLE_BLACK_LIST_VEHICLE) {
                setShowActionTaken(false);
            } else if (selected_action_code == TableConstants.TM_ROLE_RELEASE_BLACK_LIST_VEHICLE) {
                setShowActionTaken(true);
            }
            this.setListBeans(PrintDocImpl.getVehicleBlackListDetails(selected_action_code));
        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public String doAction() {
        String returnURL = null;
        if (selected_action_code == TableConstants.TM_ROLE_BLACK_LIST_VEHICLE) {
            returnURL = "/ui/appl_status/Form_black_listed_vehicles.xhtml";
        } else if (selected_action_code == TableConstants.TM_ROLE_RELEASE_BLACK_LIST_VEHICLE) {
            returnURL = "/ui/reports/Form_release_black_listed_vehicles.xhtml";
        }
        return returnURL;
    }

    /**
     * @return the printCertDobj
     */
    public List<VehicleBlackListReportDobj> getPrintCertDobj() {
        return printCertDobj;
    }

    /**
     * @param printCertDobj the printCertDobj to set
     */
    public void setPrintCertDobj(List<VehicleBlackListReportDobj> printCertDobj) {
        this.printCertDobj = printCertDobj;
    }

    /**
     * @return the selected_action_code
     */
    public int getSelected_action_code() {
        return selected_action_code;
    }

    /**
     * @param selected_action_code the selected_action_code to set
     */
    public void setSelected_action_code(int selected_action_code) {
        this.selected_action_code = selected_action_code;
    }

    /**
     * @return the showActionTaken
     */
    public boolean isShowActionTaken() {
        return showActionTaken;
    }

    /**
     * @param showActionTaken the showActionTaken to set
     */
    public void setShowActionTaken(boolean showActionTaken) {
        this.showActionTaken = showActionTaken;
    }
}
