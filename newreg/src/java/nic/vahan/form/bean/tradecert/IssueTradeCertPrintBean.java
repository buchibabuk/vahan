/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.tradecert;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.TableConstants;
import nic.vahan.form.bean.common.FileMovementAbstractApplBean;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.tradecert.IssueTradeCertDobj;
import nic.vahan.form.dobj.tradecert.TCPrintDobj;
import nic.vahan.form.impl.tradecert.IssueTradeCertImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.tradecert.ApplicationTradeCertImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.PrinterDocDetails;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author nicsi
 */
@ManagedBean(name = "issueTradeCertPrintBean")
@RequestScoped
public class IssueTradeCertPrintBean extends FileMovementAbstractApplBean {

    private static final Logger LOGGER = Logger.getLogger(IssueTradeCertPrintBean.class);
    private TCPrintDobj tcPrintDobj = null;
    private final String currentDate;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy h:mm:ss a");
    private int purCd;
    private String tcType;
    private final String reportHeading;
    private final String reportSubHeading;
    private String outcome;
    private List selectedTcDobjList;
    private boolean tcForEachVehCatg = true;
    private boolean displayFuel;
    private final SessionVariables sessionVariables;
    private boolean doNotShowNoOfVehicles;
    private TmConfigurationDobj tmConfigDobj;

    public IssueTradeCertPrintBean() {

        this.outcome = "/ui/tradecert/formTradeCertPrintingList.xhtml?faces-redirect=true";
        boolean dataMapFilled = false;
        selectedTcDobjList = (List) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("selectedTcDobjList");
        sessionVariables = new SessionVariables();
        currentDate = sdf.format(new Date());
        reportHeading = ServerUtil.getRcptHeading();
        reportSubHeading = ServerUtil.getRcptSubHeading();
        String applNo = null;
        try {
            tmConfigDobj = Util.getTmConfiguration();
        } catch (VahanException ex) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            return;
        }
        if (tmConfigDobj.getTmTradeCertConfigDobj().isFuelToBeDisplayed()) {  // Gujarat
            displayFuel = true;
        }
        if (tmConfigDobj.getTmTradeCertConfigDobj().isNoOfVehiclesNotToBeShown()) {   /// Chhattisgarh
            doNotShowNoOfVehicles = true;
        }
        if (selectedTcDobjList != null) {
            for (Object selectedTcDobjListObj : selectedTcDobjList) {
                tcPrintDobj = (TCPrintDobj) selectedTcDobjListObj;
                ///////////////////////// 
                try {
                    Map mapForSelectedTradeCert = new HashMap();
                    Map dataMap = null;
                    String tcNo = tcPrintDobj.getTcNo();
                    //Note:Avoid using try block inside another try block
                    try {
                        if (!CommonUtils.isNullOrBlank(tcPrintDobj.getApplicantType())) {
                            dataMap = PrinterDocDetails.getTradeCertificatePrintingFromVt(Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), tcNo, doNotShowNoOfVehicles, displayFuel, tcPrintDobj.getApplicantType());
                            dataMapFilled = true;
                            purCd = (int) dataMap.get("purCd");
                        } else {
                            purCd = Integer.valueOf(String.valueOf(sessionVariables.getActionCodeSelected()).substring(0, 2));
                            dataMap = PrinterDocDetails.getTradeCertificatePrintingFromVt(Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), purCd, tcNo, doNotShowNoOfVehicles, displayFuel);
                        }
                        applNo = (String) dataMap.get("applNo");
                    } catch (VahanException ex) {
                        LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Exception in fetching and mapping data for trade certificate printing."));
                    }

                    tcPrintDobj.setDobjSubList(new ArrayList());

                    IssueTradeCertImpl issueTradeCertImpl = new IssueTradeCertImpl();
                    try {
                        issueTradeCertImpl.getTradeCertDetailsForTradeCertNoFromVtTradeCert(dataMap.get("certNo").toString(), mapForSelectedTradeCert);
                        tcPrintDobj.getDobjSubList().addAll(mapForSelectedTradeCert.values());

                    } catch (VahanException ex) {
                        LOGGER.error(ex);
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Exception in fetching and mapping data for trade certificate details for printing."));
                    }

                    if (CommonUtils.isNullOrBlank(dataMap.get("dealerName").toString())) {
                        tcPrintDobj.setDealerName(ApplicationTradeCertImpl.fetchApplicantNameFromOnlineSchemaVmApplicantMastAppl(dataMap.get("dealerCode").toString(), Util.getUserStateCode()));
                    } else {
                        tcPrintDobj.setDealerName(dataMap.get("dealerName").toString());
                    }
                    tcPrintDobj.setDealerAddress(dataMap.get("dealerAddress").toString());
                    if (dataMap.containsKey("showroomName")) {
                        tcPrintDobj.setShowroomName(dataMap.get("showroomName").toString());
                    }
                    if (dataMap.containsKey("showroomAddress")) {
                        tcPrintDobj.setShowroomAddress(dataMap.get("showroomAddress").toString());
                    }
                    tcPrintDobj.setOfficeName(dataMap.get("officeName").toString());
                    tcPrintDobj.setFees(dataMap.get("fees").toString());
                    tcPrintDobj.setValidUptoAsString(dataMap.get("validUpto").toString());
                    if (dataMap.containsKey("vchClass")) {
                        tcPrintDobj.setVchClass(dataMap.get("vchClass").toString());
                    }
                    if (tmConfigDobj.getTmTradeCertConfigDobj().isValidUptoInsteadOfValidityPeriod()) {   /// valid_upto_instead_of_validity_period work for Chhattisgarh
                        tcPrintDobj.setValidityPeriod(dataMap.get("validUpto").toString());
                    } else {
                        tcPrintDobj.setValidityPeriod(dataMap.get("validityPeriod").toString());
                    }

                    String text = " Trade Certificate No: " + tcPrintDobj.getTcNo() + ", Dealer: " + tcPrintDobj.getDealerName() + ", Address: " + tcPrintDobj.getDealerAddress()
                            + ", Vehicle Categories: " + tcPrintDobj.getVehCatgs() + ", State:" + tcPrintDobj.getStateCd()
                            + ", Office: " + tcPrintDobj.getOfficeName() + ", Vehicle Class: " + tcPrintDobj.getVchClass() + ", Fee: " + tcPrintDobj.getFees();
                    if (tmConfigDobj.getTmTradeCertConfigDobj().isValidUptoInsteadOfValidityPeriod()) {   /// valid_upto_instead_of_validity_period work for Chhattisgarh
                        text += ", Valid Upto: " + tcPrintDobj.getValidUptoAsString();
                    } else {
                        text += ", Validity Period: " + tcPrintDobj.getValidityPeriod();
                    }

                    if (!doNotShowNoOfVehicles) {
                        String numberOfVehPerVehCategory = "{";
                        int srNo = 1;
                        for (Object issueTradeCertDobjObj : tcPrintDobj.getDobjSubList()) {
                            IssueTradeCertDobj issueTradeCertDobj = (IssueTradeCertDobj) issueTradeCertDobjObj;
                            issueTradeCertDobj.setSrNo((srNo++) + "");
                            issueTradeCertDobj.setNoOfVehiclePrint(Integer.valueOf(issueTradeCertDobj.getNoOfVehiclePrint()) + 1 + "");
                            numberOfVehPerVehCategory += issueTradeCertDobj.getVehCatgName() + "(" + issueTradeCertDobj.getNoOfAllowedVehicles() + "),";
                        }
                        numberOfVehPerVehCategory = numberOfVehPerVehCategory.substring(0, numberOfVehPerVehCategory.length() - 1);
                        numberOfVehPerVehCategory += "}";

                        text += ",Number Of Vehicles:" + numberOfVehPerVehCategory;
                    }

                    tcPrintDobj.setText(text);

                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Exception in printing trade certificate after data fetching and mapping."));
                }
            }
        }

        try {
            TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();
            if (tmConfigurationDobj.isTcNoForEachVehCatg()) {
                tcForEachVehCatg = true;
            }
        } catch (VahanException vahanException) {
            LOGGER.error(vahanException.getMessage() + " " + vahanException.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Exception in setting trade certificate number for each vehicle category"));
        }

        try {
            if (ApplicationTradeCertImpl.isTradeCertificateProcessNotCompleted(applNo, purCd)) {
                Status_dobj status = new Status_dobj();
                status.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
                status.setStatus(TableConstants.STATUS_COMPLETE);
                status.setAppl_no(applNo);
                status.setOffice_remark(TableConstants.TRADE_CERTIFICATE_PRINTED);
                status.setPublic_remark(TableConstants.TRADE_CERTIFICATE_PRINTED);
                status.setState_cd(Util.getUserStateCode());
                status.setPur_cd(purCd);
                status.setEmp_cd(Util.getEmpCodeLong());
                status.setCurrent_role(getAppl_details().getCurrent_role());
                status.setAction_cd(getAppl_details().getCurrent_action_cd());
                new ApplicationTradeCertImpl().fileMove(status);
            }
        } catch (VahanException ve) {
            LOGGER.error(ve.getMessage() + " " + ve.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Exception occurred during file movement."));
        }
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public int getPurCd() {
        return purCd;
    }

    public String getTcType() {
        return tcType;
    }

    public String getReportHeading() {
        return reportHeading;
    }

    public String getReportSubHeading() {
        return reportSubHeading;
    }

    public String getOutcome() {
        return outcome;
    }

    public List getSelectedTcDobjList() {
        return selectedTcDobjList;
    }

    public void setSelectedTcDobjList(List selectedTcDobjList) {
        this.selectedTcDobjList = selectedTcDobjList;
    }

    public boolean isTcForEachVehCatg() {
        return tcForEachVehCatg;
    }

    public void setTcForEachVehCatg(boolean tcForEachVehCatg) {
        this.tcForEachVehCatg = tcForEachVehCatg;
    }

    public boolean isDisplayFuel() {
        return displayFuel;
    }

    /**
     * @return the tcPrintDobj
     */
    public TCPrintDobj getTcPrintDobj() {
        return tcPrintDobj;
    }

    /**
     * @param tcPrintDobj the tcPrintDobj to set
     */
    public void setTcPrintDobj(TCPrintDobj tcPrintDobj) {
        this.tcPrintDobj = tcPrintDobj;
    }

    public boolean isDoNotShowNoOfVehicles() {
        return doNotShowNoOfVehicles;
    }

    public void setDoNotShowNoOfVehicles(boolean doNotShowNoOfVehicles) {
        this.doNotShowNoOfVehicles = doNotShowNoOfVehicles;
    }
}
