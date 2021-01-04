/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.rest.model.permit;

import java.util.List;
import nic.vahan.form.bean.permit.PermitPanelBean;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author Kartikey Singh
 */
public class PermitPanelBeanModel {

    private static final Logger LOGGER = Logger.getLogger(PermitPanelBean.class);
    private List pmt_type_list;
    private List pmt_domain_list;
    private List pmt_catg_list;
    private List pmt_service_type_list;
    private PassengerPermitDetailDobj permitDobj;
    private boolean tripsRouteLengthRqrd;
    private boolean isDisable;
    private boolean isDisableForInstlmnt;
    private PassengerPermitDetailDobj permitDobjPrev;
    private List regionList;
    private boolean renderMultiRegionList;
    private boolean renderDomain;

    public PermitPanelBeanModel(PermitPanelBean permitPanelBean) {
//        this.pmt_type_list = permitPanelBean.getPmt_type_list();
//        this.pmt_domain_list = permitPanelBean.getPmt_domain_list();
//        this.pmt_catg_list = permitPanelBean.getPmt_catg_list();
//        this.pmt_service_type_list = permitPanelBean.getPmt_service_type_list();
        this.permitDobj = permitPanelBean.getPermitDobj();
        this.tripsRouteLengthRqrd = permitPanelBean.isTripsRouteLengthRqrd();
        this.isDisable = permitPanelBean.isIsDisable();
        this.isDisableForInstlmnt = permitPanelBean.isIsDisableForInstlmnt();
        this.permitDobjPrev = permitPanelBean.getPermitDobjPrev();
//        this.regionList = permitPanelBean.getRegionList();
//        this.renderMultiRegionList = permitPanelBean.isRenderMultiRegionList();
        this.renderDomain = permitPanelBean.isRenderDomain();
    }

    public void setPermitPanelBeanFromModel(PermitPanelBean permitPanelBean) {
//        permitPanelBean.setPmt_type_list(this.getPmt_type_list());
//        permitPanelBean.setPmt_domain_list(this.getPmt_domain_list());
//        permitPanelBean.setPmt_catg_list(this.getPmt_catg_list());
//        permitPanelBean.setPmt_service_type_list(this.getPmt_service_type_list());
        permitPanelBean.setPermitDobj(this.getPermitDobj());
        permitPanelBean.setTripsRouteLengthRqrd(this.isTripsRouteLengthRqrd());
        permitPanelBean.setIsDisable(this.isIsDisable());
        permitPanelBean.setIsDisableForInstlmnt(this.isIsDisableForInstlmnt());
        permitPanelBean.setPermitDobjPrev(this.getPermitDobjPrev());
//        permitPanelBean.setRegionList(this.getRegionList());
//        permitPanelBean.setRenderMultiRegionList(this.isRenderMultiRegionList());
        permitPanelBean.setRenderDomain(this.isRenderDomain());
    }

    public PermitPanelBeanModel() {
    }

    public PermitPanelBeanModel(List pmt_type_list) {
        this.pmt_type_list = pmt_type_list;
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

    public boolean isTripsRouteLengthRqrd() {
        return tripsRouteLengthRqrd;
    }

    public void setTripsRouteLengthRqrd(boolean tripsRouteLengthRqrd) {
        this.tripsRouteLengthRqrd = tripsRouteLengthRqrd;
    }

    public boolean isIsDisable() {
        return isDisable;
    }

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
