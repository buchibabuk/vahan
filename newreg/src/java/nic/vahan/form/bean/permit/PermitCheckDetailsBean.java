/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.form.bean.InsBean;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.permit.PermitCheckDetailsDobj;
import nic.vahan.form.impl.ApplicationInwardImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.eChallan.SaveChallanImpl;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.form.impl.permit.PermitCheckDetailsImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.services.insurance.InsuranceDetailService;
import nic.vahan.services.insurance.dobj.InsuranceInfoDobj;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import nic.vahan.form.dobj.eChallan.ChallanReportDobj;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "pmtDtlsBean")
@ViewScoped
public class PermitCheckDetailsBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(PrintPermitBean.class);
    PermitCheckDetailsDobj dtlsDobj = null;
    PermitCheckDetailsImpl pmtDtlsImpl = null;
    private boolean renderInsDialog = false;
    @ManagedProperty(value = "#{ins_bean}")
    private InsBean insBean;
    private String registrationNo = "";
    InsDobj insDobj = null;
    private List arrayInsCmpy = new ArrayList();
    private List arrayInsType = new ArrayList();
    private List arrayPmtType = new ArrayList();
    private List arrayPmtCatg = new ArrayList();
    private List arraySerType = new ArrayList();
    private String pmtType = "";
    private String pmtCatg = "";
    private String serType = "";
    private String routeLength = "";
    private String noOfTrips = "";
    private String ownerStateCd = "";
    private int ownerOffCd;
    private List<ChallanReportDobj> eChallanInfo = null;
    ApplicationInwardImpl inwardImpl = new ApplicationInwardImpl();
    private Map<String, String> stateConfigMap = null;
    private boolean renderGoodsPassengerTax = false;

    @PostConstruct
    public void init() {
        dtlsDobj = null;
        String[][] data = MasterTableFiller.masterTables.VM_ICCODE.getData();
        for (int i = 0; i < data.length; i++) {
            arrayInsCmpy.add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.VM_INSTYP.getData();
        for (int i = 0; i < data.length; i++) {
            arrayInsType.add(new SelectItem(data[i][0], data[i][1]));
        }

        data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
        for (int i = 0; i < data.length; i++) {
            arrayPmtType.add(new SelectItem(data[i][0], data[i][1]));
        }

        data = MasterTableFiller.masterTables.vm_service_type.getData();
        for (int i = 0; i < data.length; i++) {
            arraySerType.add(new SelectItem(data[i][0], data[i][1]));
        }

        stateConfigMap = CommonPermitPrintImpl.getVmPermitStateConfiguration(Util.getUserStateCode());
        renderGoodsPassengerTax = Boolean.parseBoolean(stateConfigMap.get("render_goods_passanger_tax"));

    }

    public PermitCheckDetailsDobj getInsuranceFromService(String regn_no, String state_cd, int off_cd, PermitCheckDetailsDobj dtlsDobj) throws VahanException {
        try {
            InsDobj ins_dobj = null;
            if (dtlsDobj != null && !CommonUtils.isNullOrBlank(dtlsDobj.getInsUpto())) {
                ins_dobj = new InsDobj();
                if (dtlsDobj.getInsFrom() != null) {
                    ins_dobj.setIns_from(DateUtils.parseDate(dtlsDobj.getInsFrom()));
                }
                if (dtlsDobj.getInsUpto() != null) {
                    ins_dobj.setIns_upto(DateUtils.parseDate(dtlsDobj.getInsUpto()));
                }
                if (!CommonUtils.isNullOrBlank(dtlsDobj.getInsIdv())) {
                    ins_dobj.setIdv(Long.parseLong(dtlsDobj.getInsIdv()));
                }
                if (!CommonUtils.isNullOrBlank(dtlsDobj.getInsCmpyNo())) {
                    ins_dobj.setComp_cd(dtlsDobj.getInsCmpyNoCode());
                }
                if (!CommonUtils.isNullOrBlank(dtlsDobj.getInsPolicyNo())) {
                    ins_dobj.setPolicy_no(dtlsDobj.getInsPolicyNo());
                }
                if (dtlsDobj.getInsTypeCode() != 0) {
                    ins_dobj.setIns_type(dtlsDobj.getInsTypeCode());
                }
            }
            InsuranceDetailService detailService = new InsuranceDetailService();
            InsDobj ins_Dobj = detailService.getInsuranceDetailsByService(regn_no, state_cd, off_cd);
            if (ins_Dobj != null) {
                if (ins_dobj != null) {
                    ins_Dobj.setIdv(ins_dobj.getIdv());
                }
                dtlsDobj.setInsFrom(DateUtils.parseDate(ins_Dobj.getIns_from()));
                dtlsDobj.setInsPolicyNo(ins_Dobj.getPolicy_no());
                dtlsDobj.setInsUpto(DateUtils.parseDate(ins_Dobj.getIns_upto()));
                dtlsDobj.setInsIdv(String.valueOf(ins_Dobj.getIdv()));
                dtlsDobj.setInsValid(true);
                dtlsDobj.setInsSaveData(false);
                dtlsDobj.setInsCmpyNo(CommonPermitPrintImpl.getLableForSelectedList((ArrayList) arrayInsCmpy, String.valueOf(ins_Dobj.getComp_cd())));
                dtlsDobj.setInsType(CommonPermitPrintImpl.getLableForSelectedList((ArrayList) arrayInsType, String.valueOf(ins_Dobj.getIns_type())));

                if (ins_dobj != null && !ins_Dobj.isIibData() && ins_Dobj.getIns_upto().getTime() < ins_dobj.getIns_upto().getTime()) {
                    dtlsDobj.setInsFrom(DateUtils.parseDate(ins_dobj.getIns_from()));
                    dtlsDobj.setInsPolicyNo(ins_dobj.getPolicy_no());
                    dtlsDobj.setInsUpto(DateUtils.parseDate(ins_dobj.getIns_upto()));
                    dtlsDobj.setInsIdv(String.valueOf(ins_dobj.getIdv()));
                    dtlsDobj.setInsValid(true);
                    dtlsDobj.setInsSaveData(false);
                    dtlsDobj.setInsCmpyNo(CommonPermitPrintImpl.getLableForSelectedList((ArrayList) arrayInsCmpy, String.valueOf(ins_dobj.getComp_cd())));
                    dtlsDobj.setInsType(CommonPermitPrintImpl.getLableForSelectedList((ArrayList) arrayInsType, String.valueOf(ins_dobj.getIns_type())));
                }
                if ((ins_Dobj == null && ins_dobj == null) || (ins_Dobj.getIns_upto() == null || ins_Dobj.getIns_upto().getTime() < new Date().getTime())) {
                    if (ins_Dobj.isIibData()) {
                        dtlsDobj.setInsValid(false);
                        dtlsDobj.setInsSaveData(true);
                    } else {
                        dtlsDobj.setInsValid(false);
                        dtlsDobj.setInsSaveData(true);
                        if (ins_dobj != null && ins_Dobj.getIns_upto().getTime() < ins_dobj.getIns_upto().getTime()) {
                            dtlsDobj.setInsValid(true);
                            dtlsDobj.setInsSaveData(false);
                        }
                    }
                }
            }//end of getting insurance details from service 
        } catch (VahanException ex) {
            throw ex;
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(PermitCheckDetailsBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dtlsDobj;

    }

    public void getAlldetails(String regn_no, InsDobj insDobj, String state_cd, int off_cd) {
        try {
            registrationNo = regn_no;
            ownerOffCd = off_cd;
            ownerStateCd = state_cd;
            dtlsDobj = new PermitCheckDetailsDobj();
            if (!CommonUtils.isNullOrBlank(state_cd)) {
                stateConfigMap = CommonPermitPrintImpl.getVmPermitStateConfiguration(state_cd);
                renderGoodsPassengerTax = Boolean.parseBoolean(stateConfigMap.get("render_goods_passanger_tax"));
            }
            pmtDtlsImpl = new PermitCheckDetailsImpl();
            // dtlsDobj = pmtDtlsImpl.getTaxDtls(dtlsDobj, regn_no);
            dtlsDobj = pmtDtlsImpl.getLatestTaxDtls(dtlsDobj, regn_no);
            dtlsDobj = pmtDtlsImpl.getFitnessDtls(dtlsDobj, regn_no);
            if (renderGoodsPassengerTax) {
                dtlsDobj = pmtDtlsImpl.getGoodsPassengerTaxDtls(dtlsDobj, regn_no);
            }
            if (insDobj == null) {
                dtlsDobj = pmtDtlsImpl.getInsuranceDtls(dtlsDobj, regn_no);
                dtlsDobj = getInsuranceFromService(regn_no, state_cd, off_cd, dtlsDobj);
            } else {
                dtlsDobj.setInsCmpyNo(CommonPermitPrintImpl.getLableForSelectedList((ArrayList) arrayInsCmpy, String.valueOf(insDobj.getComp_cd())));
                dtlsDobj.setInsFrom(CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(insDobj.getIns_from()));
                dtlsDobj.setInsPolicyNo(insDobj.getPolicy_no());
                dtlsDobj.setInsType(CommonPermitPrintImpl.getLableForSelectedList((ArrayList) arrayInsType, String.valueOf(insDobj.getIns_type())));
                dtlsDobj.setInsUpto(CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(insDobj.getIns_upto()));
                dtlsDobj.setInsIdv(String.valueOf(insDobj.getIdv()));
                dtlsDobj.setInsValid(true);
                dtlsDobj.setInsSaveData(true);
            }
            dtlsDobj = pmtDtlsImpl.getChallanDtls(dtlsDobj, regn_no);
            dtlsDobj = pmtDtlsImpl.getVehicleBlackListedDtls(dtlsDobj, regn_no);
            dtlsDobj = pmtDtlsImpl.getPuccDetails(dtlsDobj, regn_no);
            //getting eChallan Details from function  
            challanDetailFromFunction(regn_no);
        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Information", e.getMessage(), FacesMessage.SEVERITY_INFO);
        }
    }

    public void challanDetailFromFunction(String regn_no) {
        try {

            eChallanInfo = inwardImpl.getEChallanInfo(regn_no);

            if (geteChallanInfo() == null || geteChallanInfo().isEmpty()) {
                seteChallanInfo(SaveChallanImpl.getVahaneChallanInfo(regn_no));
            }
            if (geteChallanInfo() != null && !eChallanInfo.isEmpty()) {
//                if (Util.getUserStateCode().equalsIgnoreCase("UP")) {
//                    dtlsDobj.setChalPendingInService(true);
//                    if (JSFUtils.findComponentById("pmtCheckDetails", "eChallanInfo")) {
//                        PrimeFaces.current().ajax().update("pmtCheckDetails:eChallanInfo");
//                    }
//                } else {
                eChallanInfo = null;
                //}
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void showInsDialog() {
        renderInsDialog = true;
        PrimeFaces.current().executeScript("PF('insDtls').show();");
    }

    public void exitInsDialog() {
        PrimeFaces.current().executeScript("PF('insDtls').hide();");
        renderInsDialog = false;
    }

    public void insertIntoVaInsurance() {
        FacesMessage message = null;
        try {
            pmtDtlsImpl = new PermitCheckDetailsImpl();
            insDobj = insBean.set_InsBean_to_dobj();
            if (insDobj != null && ((insDobj.getIns_type() != 0) && (insDobj.getIns_type() != -1)) && ((insDobj.getComp_cd() != 0) && (insDobj.getComp_cd() != -1)) && !CommonUtils.isNullOrBlank(insDobj.getPolicy_no())) {
                PrimeFaces.current().executeScript("PF('insDtls').hide();");
                renderInsDialog = false;
                getAlldetails(registrationNo, insDobj, ownerStateCd, ownerOffCd);
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Insurance Details updated successfully with save Permit Details :");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Insurance Details not updated.");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                PrimeFaces.current().executeScript("PF('insDtls').hide();");
                renderInsDialog = false;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().executeScript("PF('insDtls').hide();");
            renderInsDialog = false;
        }
    }

    public void showTaxDetails() {
        String[] taxPmtDetls = pmtDtlsImpl.getPermitDetailsOnTaxBasedOn(registrationNo, ownerStateCd, ownerOffCd, null);
        arrayPmtCatg.clear();
        if (CommonUtils.isNullOrBlank(taxPmtDetls[0])) {
            pmtType = "-1";
        } else {
            pmtType = taxPmtDetls[0];
        }

        if (CommonUtils.isNullOrBlank(taxPmtDetls[1])) {
            pmtCatg = "-1";
        } else {
            String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equalsIgnoreCase(Util.getUserStateCode())
                        && data[i][3].equalsIgnoreCase(taxPmtDetls[0])) {
                    arrayPmtCatg.add(new SelectItem(data[i][1], data[i][2]));
                }
            }
            pmtCatg = taxPmtDetls[1];
        }

        if (CommonUtils.isNullOrBlank(taxPmtDetls[2])) {
            serType = "-1";
        } else {
            serType = taxPmtDetls[2];
        }

        if (CommonUtils.isNullOrBlank(taxPmtDetls[3])) {
            routeLength = "0";
        } else {
            routeLength = taxPmtDetls[3];
        }

        if (CommonUtils.isNullOrBlank(taxPmtDetls[4])) {
            noOfTrips = "0";
        } else {
            noOfTrips = taxPmtDetls[4];
        }
        PrimeFaces.current().executeScript("PF('pmtDtls').show();");
        if (JSFUtils.findComponentById("pmtDetailBasedOnTax", "pmt_dtls")) {
            PrimeFaces.current().ajax().update("pmtDetailBasedOnTax:pmt_dtls");
        }
    }

    public void closePmtTaxPanle() {
        PrimeFaces.current().executeScript("PF('pmtDtls').hide();");
    }

    public PermitCheckDetailsDobj getDtlsDobj() {
        return dtlsDobj;
    }

    public void setDtlsDobj(PermitCheckDetailsDobj dtlsDobj) {
        this.dtlsDobj = dtlsDobj;
    }

    public InsBean getInsBean() {
        return insBean;
    }

    public void setInsBean(InsBean insBean) {
        this.insBean = insBean;
    }

    public boolean isRenderInsDialog() {
        return renderInsDialog;
    }

    public void setRenderInsDialog(boolean renderInsDialog) {
        this.renderInsDialog = renderInsDialog;
    }

    public List getArrayInsCmpy() {
        return arrayInsCmpy;
    }

    public void setArrayInsCmpy(List arrayInsCmpy) {
        this.arrayInsCmpy = arrayInsCmpy;
    }

    public List getArrayInsType() {
        return arrayInsType;
    }

    public void setArrayInsType(List arrayInsType) {
        this.arrayInsType = arrayInsType;
    }

    public List getArrayPmtType() {
        return arrayPmtType;
    }

    public void setArrayPmtType(List arrayPmtType) {
        this.arrayPmtType = arrayPmtType;
    }

    public List getArrayPmtCatg() {
        return arrayPmtCatg;
    }

    public void setArrayPmtCatg(List arrayPmtCatg) {
        this.arrayPmtCatg = arrayPmtCatg;
    }

    public List getArraySerType() {
        return arraySerType;
    }

    public void setArraySerType(List arraySerType) {
        this.arraySerType = arraySerType;
    }

    public String getPmtType() {
        return pmtType;
    }

    public void setPmtType(String pmtType) {
        this.pmtType = pmtType;
    }

    public String getPmtCatg() {
        return pmtCatg;
    }

    public void setPmtCatg(String pmtCatg) {
        this.pmtCatg = pmtCatg;
    }

    public String getSerType() {
        return serType;
    }

    public void setSerType(String serType) {
        this.serType = serType;
    }

    public String getRouteLength() {
        return routeLength;
    }

    public void setRouteLength(String routeLength) {
        this.routeLength = routeLength;
    }

    public String getNoOfTrips() {
        return noOfTrips;
    }

    public void setNoOfTrips(String noOfTrips) {
        this.noOfTrips = noOfTrips;
    }

    public String getOwnerStateCd() {
        return ownerStateCd;
    }

    public void setOwnerStateCd(String ownerStateCd) {
        this.ownerStateCd = ownerStateCd;
    }

    public int getOwnerOffCd() {
        return ownerOffCd;
    }

    public void setOwnerOffCd(int ownerOffCd) {
        this.ownerOffCd = ownerOffCd;
    }

    /**
     * @return the eChallanInfo
     */
    public List<ChallanReportDobj> geteChallanInfo() {
        return eChallanInfo;
    }

    /**
     * @param eChallanInfo the eChallanInfo to set
     */
    public void seteChallanInfo(List<ChallanReportDobj> eChallanInfo) {
        this.eChallanInfo = eChallanInfo;
    }

    public boolean isRenderGoodsPassengerTax() {
        return renderGoodsPassengerTax;
    }

    public void setRenderGoodsPassengerTax(boolean renderGoodsPassengerTax) {
        this.renderGoodsPassengerTax = renderGoodsPassengerTax;
    }
}
