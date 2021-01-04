/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.impl.Util;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC094
 */
@ManagedBean
@ViewScoped
public class PermitPanelBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(PermitPanelBean.class);
    private List pmt_type_list = new ArrayList();
    private List pmt_domain_list = new ArrayList();
    private List pmt_catg_list = new ArrayList();
    private List pmt_service_type_list = new ArrayList();
    private PassengerPermitDetailDobj permitDobj = null;
    private boolean tripsRouteLengthRqrd = false;
    private boolean isDisable = false;
    private SessionVariables sessionVariables = null;
    private boolean isDisableForInstlmnt;
    private PassengerPermitDetailDobj permitDobjPrev = null;
    private List regionList = new ArrayList();
    private boolean renderMultiRegionList = false;
    private boolean renderDomain = false;

    public PermitPanelBean() {
        sessionVariables = new SessionVariables();
        if (sessionVariables == null
                || sessionVariables.getStateCodeSelected() == null
                || sessionVariables.getOffCodeSelected() == 0
                || sessionVariables.getEmpCodeLoggedIn() == null) {
            //vahanMessages = "Something went wrong, Please try again...";
            return;
        }
        permitDobj = new PassengerPermitDetailDobj();
        permitDobjPrev = new PassengerPermitDetailDobj();
        postConstruct();
        tripsRouteLengthRqrd = true;
    }

    private void postConstruct() {
        pmt_catg_list = new ArrayList();
        pmt_service_type_list = new ArrayList();
        String[][] data = MasterTableFiller.masterTables.vm_service_type.getData();
        pmt_service_type_list.add(new SelectItem("", ""));
        for (int i = 0; i < data.length; i++) {
            pmt_service_type_list.add(new SelectItem(data[i][0], data[i][1]));
        }
        pmt_type_list = new ArrayList();
        data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
//        pmt_type_list.add(new SelectItem("", ""));
        for (int i = 0; i < data.length; i++) {
            pmt_type_list.add(new SelectItem(data[i][0], data[i][1]));
        }
        pmt_domain_list = new ArrayList();
        data = MasterTableFiller.masterTables.VM_REGION.getData();
//        pmt_domain_list.add(new SelectItem("", ""));
        for (int i = 0; i < data.length; i++) {
            if (sessionVariables.getStateCodeSelected().equalsIgnoreCase(data[i][0]) && sessionVariables.getOffCodeSelected() == Integer.parseInt(data[i][1])) {
                pmt_domain_list.add(new SelectItem(data[i][2], data[i][3]));
            }
        }

        getRegionList().clear();
        String[][] data1 = MasterTableFiller.masterTables.VM_REGION.getData();
        for (int i = 0; i < data1.length; i++) {
            if (sessionVariables.getStateCodeSelected().equalsIgnoreCase(data1[i][0]) && sessionVariables.getOffCodeSelected() == Integer.parseInt(data1[i][1])) {
                getRegionList().add(new SelectItem(data1[i][2], data1[i][3]));
            }
        }
    }

    public void onSelectPermitType(AjaxBehaviorEvent event) {
        pmt_catg_list.clear();
        pmt_catg_list.add(new SelectItem("", ""));
        String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
        String permit_type = permitDobj.getPmt_type_code();
        if (permit_type == null) {
            permit_type = permitDobj.getPmt_type();
            permitDobj.setPmt_type_code(permit_type);
        }
        if (permit_type != null) {
            if (permit_type.equalsIgnoreCase("105") && !("OR,MN,KL".contains(sessionVariables.getStateCodeSelected()))) {
                setTripsRouteLengthRqrd(false);
            } else if (permit_type.equalsIgnoreCase("101") || permit_type.equalsIgnoreCase("102") || (permit_type.equalsIgnoreCase("103")) || (permit_type.equalsIgnoreCase("104")) || (permit_type.equalsIgnoreCase("106"))) {
                setTripsRouteLengthRqrd(true);
            }
            if (NumberUtils.isNumber(permit_type)) {
                for (int j = 0; j < data.length; j++) {
                    if (data[j][0].equalsIgnoreCase(sessionVariables.getStateCodeSelected())
                            && Integer.parseInt(data[j][3]) == Integer.parseInt(permit_type)) {
                        pmt_catg_list.add(new SelectItem(data[j][1], data[j][2]));
                    }
                }
            }
        }
        //if permit type listener is called permit catg ajax listener is not called 
        //thus value is not changed correctly for permit catg
        if (event != null && permitDobj != null) {
            permitDobj.setPmtCatg("");
        }

    }

    public List getPmt_type_list() {

        return pmt_type_list;
    }

    public void setPmt_type_list(List pmt_type_list) {
        this.pmt_type_list = pmt_type_list;
    }

    public List getPmt_domain_list() {
        return pmt_domain_list;
    }

    public void setPmt_domain_list(List pmt_domain_list) {
        this.pmt_domain_list = pmt_domain_list;
    }

    public List getPmt_catg_list() {
        return pmt_catg_list;
    }

    public void setPmt_catg_list(List pmt_catg_list) {
        this.pmt_catg_list = pmt_catg_list;
    }

    public List getPmt_service_type_list() {
        return pmt_service_type_list;
    }

    public void setPmt_service_type_list(List pmt_service_type_list) {
        this.pmt_service_type_list = pmt_service_type_list;
    }

    public PassengerPermitDetailDobj getPermitDobj() {
        return permitDobj;
    }

    public void setPermitDobj(PassengerPermitDetailDobj permitDobj) {
        this.permitDobj = permitDobj;
    }

    /**
     * @return the tripsRouteLengthRqrd
     */
    public boolean isTripsRouteLengthRqrd() {
        return tripsRouteLengthRqrd;
    }

    /**
     * @param tripsRouteLengthRqrd the tripsRouteLengthRqrd to set
     */
    public void setTripsRouteLengthRqrd(boolean TripsRouteLengthRqrd) {
        this.tripsRouteLengthRqrd = TripsRouteLengthRqrd;
    }

    /**
     * @return the isDisable
     */
    public boolean isIsDisable() {
        return isDisable;
    }

    /**
     * @param isDisable the isDisable to set
     */
    public void setIsDisable(boolean isDisable) {
        this.isDisable = isDisable;
    }

    public boolean isIsDisableForInstlmnt() {
        return isDisableForInstlmnt;
    }

    public void setIsDisableForInstlmnt(boolean isDisableForInstlmnt) {
        this.isDisableForInstlmnt = isDisableForInstlmnt;
    }

    public PassengerPermitDetailDobj getPermitDobjPrev() {
        return permitDobjPrev;
    }

    public void setPermitDobjPrev(PassengerPermitDetailDobj permitDobjPrev) {
        this.permitDobjPrev = permitDobjPrev;
    }

    public List getRegionList() {
        return regionList;
    }

    public void setRegionList(List regionList) {
        this.regionList = regionList;
    }

    public boolean isRenderMultiRegionList() {
        return renderMultiRegionList;
    }

    public void setRenderMultiRegionList(boolean renderMultiRegionList) {
        this.renderMultiRegionList = renderMultiRegionList;
    }

    public boolean isRenderDomain() {
        return renderDomain;
    }

    public void setRenderDomain(boolean renderDomain) {
        this.renderDomain = renderDomain;
    }
}
