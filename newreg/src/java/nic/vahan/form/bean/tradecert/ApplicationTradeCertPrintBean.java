/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.tradecert;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.tradecert.ApplicationTradeCertDobj;
import nic.vahan.form.dobj.tradecert.IssueTradeCertDobj;
import nic.vahan.form.dobj.tradecert.PublicApplicationTradeCertDobj;
import nic.vahan.form.impl.tradecert.IssueTradeCertImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.tradecert.ApplicationTradeCertImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.PrinterDocDetails;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author nicsi
 */
@ManagedBean(name = "applicationTradeCertPrintBean")
@RequestScoped
public class ApplicationTradeCertPrintBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ApplicationTradeCertPrintBean.class);
    private ApplicationTradeCertDobj dobj = null;
    private List<IssueTradeCertDobj> dobjsublist = null;
    private boolean renewal;
    private final String currentDate;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
    private int purCd;
    private String text;
    private boolean displayFuel;
    private final SessionVariables sessionVariables;
    private boolean doNotShowNoOfVehicles;
    private String applicantTypeDescr;
    private int noOfTradeCertificates;
    private TmConfigurationDobj tmConfigDobj;

    public ApplicationTradeCertPrintBean() {
        currentDate = sdf.format(new Date());
        String applNo = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("tcApplNo");
        String applicantType = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("applicantType");
        Map mapOfSelectedDealer = (Map) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("tcApplMapOfSelectedDealer");
        this.dobj = new ApplicationTradeCertDobj();
        this.dobjsublist = new ArrayList<>();
        sessionVariables = new SessionVariables();
        try {
            tmConfigDobj = Util.getTmConfiguration();
        } catch (VahanException ex) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            return;
        }
        if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed()) {  /// for Gujarat
            displayFuel = true;
        }
        if (tmConfigDobj.getTmTradeCertConfigDobj().isNoOfVehiclesNotToBeShown()) {  //// for Chhattisgarh
            doNotShowNoOfVehicles = true;
        }
        try {
            if (tmConfigDobj.getTmTradeCertConfigDobj().isVehClassToBeRendered()) {   ///for Odisha
                setDataForOR(mapOfSelectedDealer);
                this.purCd = Util.getSelectedSeat().getPur_cd();
            } else {
                dobj.setApplicantCategory(applicantType);
                Map dataMap = PrinterDocDetails.getTradeCertificateApplPrinting(Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), applNo, Util.getSelectedSeat().getPur_cd(), dobj.getApplicantCategory());
                this.purCd = Util.getSelectedSeat().getPur_cd();
                getDataIntoDobj(dataMap, mapOfSelectedDealer, this.dobj, this.dobjsublist);
                this.dobj.setStateCd(sessionVariables.getStateCodeSelected());
            }

            if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_MANUFACTURER)) {
                applicantTypeDescr = "manufacturer of motor vehicles";
            } else if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_FINANCIER)) {
                applicantTypeDescr = "engaged in the business of hire / purchase / lease / hypothecation of vehicles";
            } else if (!CommonUtils.isNullOrBlank(applicantType) && applicantType.equalsIgnoreCase(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_RETRO_FITTER)) {
                applicantTypeDescr = "engaged in fitting parts into vehicles";
            } else {
                applicantTypeDescr = "dealer in motor vehicles";
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Exception in fetching trade certificate application printing data."));
        }
    }

    private void setDataForOR(Map map) throws VahanException {
        noOfTradeCertificates = 0;
        Iterator keyIterator = map.keySet().iterator();
        while (keyIterator.hasNext()) {
            PublicApplicationTradeCertDobj dobj = (PublicApplicationTradeCertDobj) map.get(keyIterator.next());
            map_PublicApplicationTradeCertDobj_To_ApplicationTradeCertDobj(dobj, this.dobj);
            if (!CommonUtils.isNullOrBlank(dobj.getNoOfAllowedVehicles())) {
                noOfTradeCertificates += Integer.valueOf(dobj.getNoOfAllowedVehicles());
            }
            break;
        }
        Map dataMap = null;
        dataMap = PrinterDocDetails.getTradeCertificateApplPrinting(Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), dobj.getApplNo(), Util.getSelectedSeat().getPur_cd(), dobj.getApplicantCategory());
        dobj.setFeesCollected(dataMap.get("fees").toString());
        dobj.setApplicantCategory(dobj.getApplicantCategory().equals(TableConstants.TRADE_CERT_APPLICANT_TYPE_CODE_DEALER) ? "Dealer in motor vehicles" : "Engaged in the business of hire / purchase / lease / hypothecation of vehicles");

        text = " Application No." + dobj.getApplNo() + "Applicant Name." + dobj.getDealerName() + "Applicant Address." + dobj.getDealerAddress()
                + "Fee." + dobj.getFeesCollected() + "Vehicle Class." + dobj.getVehClass() + "Applicant Category." + dobj.getApplicantCategory()
                + "State Name." + dobj.getStateName() + "Office Name." + dobj.getOfficeName();

    }

    private void map_PublicApplicationTradeCertDobj_To_ApplicationTradeCertDobj(PublicApplicationTradeCertDobj dobjFrom, ApplicationTradeCertDobj dobjTo) throws VahanException {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        dobjTo.setStateCd(sessionVariables.getStateCodeSelected());
        dobjTo.setOfficeName(dobjFrom.getOfficeName());
        dobjTo.setStateName((String) sessionMap.get("state_name"));
        dobjTo.setApplNo(dobjFrom.getApplNo());
        dobjTo.setDealerName(dobjFrom.getDealerName());
        dobjTo.setApplicantRelation(dobjFrom.getApplicantRelation());
        dobjTo.setDealerAddress(dobjFrom.getApplicantAddress1());
        dobjTo.setApplicantCategory(dobjFrom.getApplicantCategory());
        dobjTo.setVehClass(new ApplicationTradeCertImpl().getVehClassDesc(dobjFrom.getVehCatgFor()).toUpperCase());
        dobjTo.setNoOfAllowedVehicles(dobjFrom.getNoOfAllowedVehicles());
    }

    private void getDataIntoDobj(Map dataMap, Map mapOfSelectedDealer, ApplicationTradeCertDobj dobj, List<IssueTradeCertDobj> dobjSubList) {
        noOfTradeCertificates = 0;
        String vehCatgNames = "";
        String certNo = "";
        IssueTradeCertImpl issueTradeCertImpl = new IssueTradeCertImpl();
        Map expiredTradeCertMap = new HashMap();
        ApplicationTradeCertDobj applicationTradeCertDobjFrom = null;
        String vchCatgSubListRenewWhereClauseString = "(";
        String fuelCdSubListRenewWhereClauseString = "(";
        List certNoListForRenew = new ArrayList();
        if (!dataMap.isEmpty()) {
            for (Object keyObj : mapOfSelectedDealer.keySet()) {
                applicationTradeCertDobjFrom = (ApplicationTradeCertDobj) mapOfSelectedDealer.get(keyObj);
                if (vchCatgSubListRenewWhereClauseString.indexOf(applicationTradeCertDobjFrom.getVehCatgFor()) == -1) {
                    vchCatgSubListRenewWhereClauseString += "'" + applicationTradeCertDobjFrom.getVehCatgFor() + "',";
                }
                fuelCdSubListRenewWhereClauseString += applicationTradeCertDobjFrom.getFuelTypeFor() + ",";
                if (this.purCd == TableConstants.VM_TRANSACTION_TRADE_CERT_DUP) {  // DUPLICATE
                    certNo = applicationTradeCertDobjFrom.getTradeCertNo();
                }
                if (doNotShowNoOfVehicles) {
                    if (displayFuel) {
                        vehCatgNames += applicationTradeCertDobjFrom.getVehCatgName() + "(" + applicationTradeCertDobjFrom.getFuelTypeName() + "),";
                    } else {
                        vehCatgNames += applicationTradeCertDobjFrom.getVehCatgName() + ",";
                    }
                } else {
                    if (displayFuel) {
                        vehCatgNames += applicationTradeCertDobjFrom.getVehCatgName() + "(" + applicationTradeCertDobjFrom.getNoOfAllowedVehicles() + "," + applicationTradeCertDobjFrom.getFuelTypeName() + "),";
                    } else {
                        vehCatgNames += applicationTradeCertDobjFrom.getVehCatgName() + "  ( " + applicationTradeCertDobjFrom.getNoOfAllowedVehicles() + " ),";
                    }
                    if (!CommonUtils.isNullOrBlank(applicationTradeCertDobjFrom.getNoOfAllowedVehicles())) {
                        noOfTradeCertificates += Integer.valueOf(applicationTradeCertDobjFrom.getNoOfAllowedVehicles());
                    }
                }
                if (!CommonUtils.isNullOrBlank(applicationTradeCertDobjFrom.getTradeCertNo())) {
                    certNoListForRenew.add(applicationTradeCertDobjFrom.getTradeCertNo());
                }
            }
            vchCatgSubListRenewWhereClauseString = vchCatgSubListRenewWhereClauseString.substring(0, vchCatgSubListRenewWhereClauseString.lastIndexOf(",")) + ")";
            fuelCdSubListRenewWhereClauseString = fuelCdSubListRenewWhereClauseString.substring(0, fuelCdSubListRenewWhereClauseString.lastIndexOf(",")) + ")";
            vehCatgNames = vehCatgNames.substring(0, vehCatgNames.length() - 1);

            dobj.setApplNo(dataMap.get("applNo").toString());
            if (!tmConfigDobj.getTmTradeCertConfigDobj().isFeesNotToBeShownInForm16()) {   //// for Rajasthan
                dobj.setFeesCollected(dataMap.get("fees").toString());
            }
            dobj.setDealerName(dataMap.get("dealerName").toString());
            dobj.setDealerAddress(dataMap.get("dealerAddress").toString());
            dobj.setStateName(dataMap.get("stateName").toString());
            dobj.setStateCd(sessionVariables.getStateCodeSelected());
            dobj.setOfficeName(dataMap.get("officeName").toString());
            dobj.setVehCatgName(vehCatgNames);
            dobj.setNoOfAllowedVehicles(dataMap.get("NoOfVehicles").toString());

            text = " Application No:" + dobj.getApplNo() + ", Dealer Name:" + dobj.getDealerName() + ", Dealer Address:" + dobj.getDealerAddress()
                    + ", Fee Amount:" + dobj.getFeesCollected() + ", Vehicle Category Names:" + dobj.getVehCatgName()
                    + ", State Name:" + dobj.getStateName() + ", Office Name:" + dobj.getOfficeName();

            //////////// FOR RENEW CASE 
            try {
                if (this.purCd == TableConstants.VM_TRANSACTION_TRADE_CERT_DUP && !CommonUtils.isNullOrBlank(certNo)) {  // DUPLICATE
                    issueTradeCertImpl.getAllExpiredSrNoForSelectedDealerFromVtTradeCert(dataMap.get("dealerCode").toString(), certNo, null, null, expiredTradeCertMap);
                } else {
                    issueTradeCertImpl.getAllExpiredSrNoForSelectedDealerFromVtTradeCert(dataMap.get("dealerCode").toString(), null, vchCatgSubListRenewWhereClauseString, fuelCdSubListRenewWhereClauseString, expiredTradeCertMap);
                }


                if (!expiredTradeCertMap.isEmpty()) {
                    for (Object keyObj : expiredTradeCertMap.keySet()) {
                        IssueTradeCertDobj issueTradeCertDobj = (IssueTradeCertDobj) expiredTradeCertMap.get(keyObj);
                        if (vehCatgNames.contains(issueTradeCertDobj.getVehCatgName())) {
                            if (certNoListForRenew.isEmpty()
                                    || (!certNoListForRenew.isEmpty() && !certNoListForRenew.contains(issueTradeCertDobj.getTradeCertNo()))) {
                                continue;
                            }
                            issueTradeCertDobj.setIssueDtAsString(sdf.format(issueTradeCertDobj.getIssueDt()));
                            text += ", Issue Date: " + issueTradeCertDobj.getIssueDtAsString();
                            issueTradeCertDobj.setValidDtAsString(sdf.format(issueTradeCertDobj.getValidDt()));
                            issueTradeCertDobj.setValidUptoAsString(sdf.format(issueTradeCertDobj.getValidUpto()));
                            text += ", Validity Period: (" + issueTradeCertDobj.getValidDtAsString() + " - " + issueTradeCertDobj.getValidUptoAsString() + ")";
                            dobjSubList.add(issueTradeCertDobj);

                            renewal = true;
                        }
                    }
                } else {
                    text += ", Issue Date: " + currentDate + ", Validity Period: 1 year ";
                }
            } catch (VahanException ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Exception in mapping trade certificate application printing data."));
            }

            ////////////////////////////////////////////


        }

    }

    public ApplicationTradeCertDobj getDobj() {
        return dobj;
    }

    public void setDobj(ApplicationTradeCertDobj dobj) {
        this.dobj = dobj;
    }

    public List<IssueTradeCertDobj> getDobjsublist() {
        return dobjsublist;
    }

    public void setDobjsublist(List<IssueTradeCertDobj> dobjsublist) {
        this.dobjsublist = dobjsublist;
    }

    public boolean isRenewal() {
        return renewal;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public int getPurCd() {
        return purCd;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isDisplayFuel() {
        return displayFuel;
    }

    public boolean isDoNotShowNoOfVehicles() {
        return doNotShowNoOfVehicles;
    }

    public void setDoNotShowNoOfVehicles(boolean doNotShowNoOfVehicles) {
        this.doNotShowNoOfVehicles = doNotShowNoOfVehicles;
    }

    public String getApplicantTypeDescr() {
        return applicantTypeDescr;
    }

    public void setApplicantTypeDescr(String applicantTypeDescr) {
        this.applicantTypeDescr = applicantTypeDescr;
    }

    public int getNoOfTradeCertificates() {
        return noOfTradeCertificates;
    }

    public void setNoOfTradeCertificates(int noOfTradeCertificates) {
        this.noOfTradeCertificates = noOfTradeCertificates;
    }
}
