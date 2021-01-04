/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.PermitHomeAuthDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.form.impl.permit.PrintPermitDocInXhtmlImpl;
import nic.vahan.form.impl.permit.PrintPermitShowDetails;
import nic.vahan.form.impl.permit.PrintPermitImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "newPmt")
@ViewScoped
public class PrintPermitBean extends AbstractApplBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(PrintPermitBean.class);
    private String sel_pmt_type;
    private String selPurposeType;
    private String selDocmentType;
    private List arrayPurposeType = new ArrayList();
    private List arrayDocmentType = new ArrayList();
    private List<PrintPermitShowDetails> listPermit = null;
    private List array_pmt_typ = new ArrayList();
    private PrintPermitImpl pmtImpl = null;
    private List<PrintPermitShowDetails> allPrintWork = null;
    private List<PrintPermitShowDetails> filteredAllPrintWork = null;
    private PrintPermitShowDetails pmtDetails = null;
    private boolean boolPermitType;
    private boolean boolDocumentType;
    private boolean renderColumn = false;
    private String searchByValue = "applNo";
    private String firstPartApplNo = "";
    private String secondPartApplNo = "";
    private String regnNo = "";
    private String loiNo = "";
    private String printPermit = "2";
    private boolean rePrintPermit = false;
    private boolean surrenderSlip = false;
    private boolean hidecombopanel = false;
    private boolean showRegnNo = false;

    public PrintPermitBean() {
        try {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            String monthYear = String.valueOf(cal.get(java.util.Calendar.YEAR)).substring(2, 4) + String.format("%02d", cal.get(java.util.Calendar.MONTH) + 1);
            firstPartApplNo = Util.getUserStateCode() + monthYear;

            String[][] data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
            for (int i = 0; i < data.length; i++) {
                array_pmt_typ.add(new SelectItem(data[i][0], data[i][1]));
            }
            data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
            for (int i = 0; i < data.length; i++) {
                int pur_cd = Integer.valueOf(data[i][0]);
                if (pur_cd == TableConstants.VM_PMT_APPLICATION_PUR_CD
                        || pur_cd == TableConstants.VM_PMT_FRESH_PUR_CD
                        || pur_cd == TableConstants.VM_PMT_RENEWAL_PUR_CD
                        || pur_cd == TableConstants.VM_PMT_TEMP_PUR_CD || pur_cd == TableConstants.VM_PMT_RENEW_TEMP_PUR_CD || pur_cd == TableConstants.VM_PMT_SPECIAL_PUR_CD
                        || (pur_cd == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD && !"UP,DL,GJ".contains(Util.getUserStateCode()))
                        || pur_cd == TableConstants.VM_PMT_DUPLICATE_PUR_CD
                        || (pur_cd == TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD && !"UP,DL,GJ".contains(Util.getUserStateCode()))
                        || pur_cd == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD
                        || pur_cd == TableConstants.VM_PMT_TRANSFER_PUR_CD || pur_cd == TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD
                        || (pur_cd == TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD && !"UP,DL,GJ".contains(Util.getUserStateCode()))
                        || pur_cd == TableConstants.VM_PMT_CA_PUR_CD
                        || pur_cd == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD || pur_cd == TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD
                        || pur_cd == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD || pur_cd == TableConstants.VM_PMT_DUP_COUNTER_SIGNATURE_PUR_CD
                        || pur_cd == TableConstants.VM_PMT_RENEW_COUNTER_SIGNATURE_PUR_CD) {
                    arrayPurposeType.add(new SelectItem(data[i][0], data[i][1]));
                }
            }

            data = MasterTableFiller.masterTables.VM_PERMIT_DOCUMENTS.getData();
            arrayDocmentType.add(new SelectItem("0", "Select"));
            for (int i = 0; i < data.length; i++) {
                arrayDocmentType.add(new SelectItem(data[i][0], data[i][1]));
            }
            onClickPrint();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }
    //By manoj

    @PostConstruct
    public void init() {
        try {
            HttpSession ses = Util.getSession();
            if (ses.getAttribute("byregnNo") != null && ses.getAttribute("regnNoBack") != null) {
                String checkbyregnNo = ses.getAttribute("byregnNo").toString();
                String regnNoBack = ses.getAttribute("regnNoBack").toString();
                if (checkbyregnNo.equals("2")) {
                    setRegnNo(regnNoBack);
                    setRenderColumn(true);
                    setPrintPermit(checkbyregnNo);
                    setShowRegnNo(true);
                    setHidecombopanel(false);
                    allPrintWork = new ArrayList<>();
                    pmtImpl = new PrintPermitImpl();
                    allPrintWork = pmtImpl.getPermitPrintByRegn_no(getRegnNo().toUpperCase());
                    if (allPrintWork.isEmpty()) {
                        setRegnNo("");
                    }
                    ses.removeAttribute("byregnNo");
                    ses.removeAttribute("regnNoBack");
                }
            } else if (ses.getAttribute("byrePrint") != null && ses.getAttribute("rePrintApplNo") != null || ses.getAttribute("rePrintRegnNo") != null) {
                String checkReprint = ses.getAttribute("byrePrint").toString();
                String rePrintRegnNo = ses.getAttribute("rePrintRegnNo").toString();
                String rePrintApplNo = ses.getAttribute("rePrintApplNo").toString();
                String searchValue = ses.getAttribute("searchValue").toString();
                if (checkReprint.equals("1")) {
                    setRePrintPermit(true);
                    setRenderColumn(false);
                    setPrintPermit(checkReprint);
                    setShowRegnNo(false);
                    setHidecombopanel(false);
                    setSearchByValue(searchValue);
                    allPrintWork = new ArrayList<>();
                    pmtImpl = new PrintPermitImpl();
                    switch (searchByValue) {
                        case "regnNo":
                            setRegnNo(rePrintRegnNo);
                            setFirstPartApplNo("");
                            setSecondPartApplNo("");
                            setLoiNo("");
                            allPrintWork = pmtImpl.getRePermitPrintRow("", getRegnNo(), null);
                            break;
                        case "applNo":
                            setRegnNo("");
                            setLoiNo("");
                            setFirstPartApplNo(rePrintApplNo.substring(0, 6));
                            setSecondPartApplNo(rePrintApplNo.substring(6));
                            allPrintWork = pmtImpl.getRePermitPrintRow(rePrintApplNo, "", null);
                            break;
                        case "loiNo":
                            setRegnNo("");
                            setFirstPartApplNo("");
                            setSecondPartApplNo("");
                            allPrintWork = pmtImpl.getRePermitPrintRow(rePrintApplNo, "", getLoiNo());
                            break;
                    }
                    if (allPrintWork.isEmpty()) {
                        setFirstPartApplNo("");
                        setSecondPartApplNo("");
                        setRegnNo("");
                        setLoiNo("");
                        setPrintPermit("");
                    }
                    ses.removeAttribute("byrePrint");
                    ses.removeAttribute("rePrintApplNo");
                    ses.removeAttribute("rePrintRegnNo");
                    ses.removeAttribute("searchValue");
                }

            } else if (ses.getAttribute("purposeType") != null && ses.getAttribute("pmtType") != null && ses.getAttribute("docType") != null) {
                String purPmtType = ses.getAttribute("bypurPmtType").toString();
                String purposeType = ses.getAttribute("purposeType").toString();
                String pmtType = ses.getAttribute("pmtType").toString();
                String docType = ses.getAttribute("docType").toString();
                boolean surrSlip = Boolean.parseBoolean(ses.getAttribute("surrenderSlip").toString());
                if (purPmtType.equals("3")) {
                    if (surrSlip) {
                        setSurrenderSlip(surrSlip);
                    }
                    setSel_pmt_type(pmtType);
                    setSelDocmentType(docType);
                    setSelPurposeType(purposeType);
                    setPrintPermit(purPmtType);
                    setRenderColumn(false);
                    pmtImpl = new PrintPermitImpl();
                    allPrintWork = new ArrayList<>();
                    allPrintWork = pmtImpl.getPermitPrintRow(Integer.valueOf(purposeType), Integer.valueOf(pmtType), docType);
                    setHidecombopanel(true);
                    setShowRegnNo(false);
                    setRePrintPermit(false);
                    if (allPrintWork.isEmpty()) {
                        setSel_pmt_type("0");
                        setSelDocmentType("0");
                        setSelPurposeType("0");
                    }
                    ses.removeAttribute("purposeType");
                    ses.removeAttribute("pmtType");
                    ses.removeAttribute("docType");
                    ses.removeAttribute("surrenderSlip");
                    ses.removeAttribute("bypurPmtType");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }
    //By manoj

    public void onClickPrint() {
        switch (getPrintPermit()) {
            case "1":
                rePrintPermit = true;
                setHidecombopanel(false);
                setShowRegnNo(false);
                setRenderColumn(false);
                setFirstPartApplNo("");
                setSecondPartApplNo("");
                setRegnNo("");
                setRenderColumn(false);
                setSearchByValue("applNo");
                if (allPrintWork != null) {
                    allPrintWork.clear();
                }
                break;
            case "2":
                rePrintPermit = false;
                setHidecombopanel(false);
                setShowRegnNo(true);
                setRenderColumn(true);
                setRegnNo("");
                if (allPrintWork != null) {
                    allPrintWork.clear();
                }
                break;
            case "3":
                rePrintPermit = false;
                setHidecombopanel(true);
                setShowRegnNo(false);
                setRenderColumn(false);
                setRegnNo("");
                setSel_pmt_type("0");
                setSelDocmentType("0");
                setSelPurposeType("0");
                if (allPrintWork != null) {
                    allPrintWork.clear();
                }
                break;
        }
    }

    public void onChangeReprint() {
        switch (searchByValue) {
            case "applNo":
                setRegnNo("");
                if (allPrintWork != null) {
                    allPrintWork.clear();
                }
                break;
            case "regnNo":
                setFirstPartApplNo("");
                setSecondPartApplNo("");
                if (allPrintWork != null) {
                    allPrintWork.clear();
                }
                break;
            case "loiNo":
                setRegnNo("");
                setFirstPartApplNo("");
                setSecondPartApplNo("");
                if (allPrintWork != null) {
                    allPrintWork.clear();
                }
                break;
        }

    }

    public void bt_GetDetails() {
        String np_appl_no = "";
        Date np_auth_upto = null;
        boolean renew_appl = false;
        try {
            pmtImpl = new PrintPermitImpl();
            if (getSel_pmt_type() == null) {
                setSel_pmt_type("0");
            }
            if (surrenderSlip) {
                setSelDocmentType("9");
                setSelPurposeType(String.valueOf(TableConstants.VM_PMT_SURRENDER_PUR_CD));
                setSel_pmt_type("0");
            }
            if (getPrintPermit().equals("2")) {
                //RequestContext rc = RequestContext.getCurrentInstance();
                if (!CommonUtils.isNullOrBlank(getRegnNo())) {
                    allPrintWork = pmtImpl.getPermitPrintByRegn_no(getRegnNo().toUpperCase());
                    if (allPrintWork.isEmpty()) {
                        if (pmtImpl.checkAuthDetailsforPrint(getRegnNo().toUpperCase())) {
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "No Authorization Details found for this Vehicle No." + getRegnNo().toUpperCase() + ""));
                        } else {
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "No permit print regarding Vehicle No." + getRegnNo().toUpperCase() + ""));
                        }
                    } else {
                        if (allPrintWork.size() > 0 && "DL,UP,GJ".contains(Util.getUserStateCode())) {
                            for (PrintPermitShowDetails printDobj : allPrintWork) {
                                if (printDobj.getPur_cd().contains(String.valueOf(TableConstants.VM_PMT_RENEWAL_PUR_CD)) && printDobj.getPmt_type().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT)) {
                                    renew_appl = true;
                                }
                            }
                            if (!renew_appl) {
                                for (PrintPermitShowDetails printDobj : allPrintWork) {
                                    if (printDobj.getPmt_type().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT) && "26,37,39".contains(printDobj.getPur_cd())) {
                                        if (!ServerUtil.insertNPAuthDetailsAtPrint(Integer.parseInt(printDobj.getPur_cd()), getRegnNo().toUpperCase(), printDobj.getAppl_no(), Util.getUserStateCode())) {
                                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Permit details not found." + getRegnNo().toUpperCase() + ""));
                                        }
                                        PermitHomeAuthDobj permitHomeAuthDobj = ServerUtil.getNPAuthDetailsAtPrint(regnNo.toUpperCase(), printDobj.getAppl_no(), Util.getUserStateCode());
                                        Map<String, String> getNpMap = new PrintPermitDocInXhtmlImpl().getNPHomeAuthFromNpPortal(regnNo.toUpperCase(), null);
                                        if (getNpMap != null && !getNpMap.isEmpty()) {
                                            np_auth_upto = DateUtils.parseDate(getNpMap.get("auth_to"));
                                            np_appl_no = getNpMap.get("v4_appl_no");
                                            if (permitHomeAuthDobj != null) {
                                                long dayDiff = 0;
                                                if (np_auth_upto.after(permitHomeAuthDobj.getAuthUpto())) {
                                                    dayDiff = DateUtils.getDate1MinusDate2_Days(permitHomeAuthDobj.getAuthUpto(), np_auth_upto);
                                                } else {
                                                    dayDiff = DateUtils.getDate1MinusDate2_Days(np_auth_upto, permitHomeAuthDobj.getAuthUpto());
                                                }
                                                if ((!CommonUtils.isNullOrBlank(np_appl_no) && !np_appl_no.equalsIgnoreCase(printDobj.getAppl_no()))) {
                                                    if (dayDiff > 30) {
                                                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "First pay Morth/Composite fee." + getRegnNo().toUpperCase() + ""));
                                                        allPrintWork = null;
                                                        break;
                                                    }
                                                } else if ((!permitHomeAuthDobj.getNpVerifyStatus().equalsIgnoreCase("A")) && "DL".contains(Util.getUserStateCode()) && dayDiff > 90) {
                                                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "First pay Morth/Composite fee." + getRegnNo().toUpperCase() + ""));
                                                    allPrintWork = null;
                                                    break;
                                                } else if ((!permitHomeAuthDobj.getNpVerifyStatus().equalsIgnoreCase("A")) && "UP,GJ".contains(Util.getUserStateCode())) {
                                                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "First pay Morth/Composite fee." + getRegnNo().toUpperCase() + ""));
                                                    allPrintWork = null;
                                                    break;
                                                }
                                            }

                                        } else {
                                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "First pay Morth/Composite fee." + getRegnNo().toUpperCase() + ""));
                                            allPrintWork = null;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // RequestContext rqc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please enter Registration No"));
                }
            } else {
                setRenderColumn(false);
                allPrintWork = pmtImpl.getPermitPrintRow(CommonUtils.isNullOrBlank(getSelPurposeType()) ? 0 : Integer.valueOf(getSelPurposeType()), CommonUtils.isNullOrBlank(getSel_pmt_type()) ? 0 : Integer.valueOf(getSel_pmt_type()), CommonUtils.isNullOrBlank(getSelDocmentType()) ? "0" : getSelDocmentType());
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void btGetDetailsForRePrint() {
        String errorMsg = "";
        try {
            pmtImpl = new PrintPermitImpl();
            if ((!firstPartApplNo.equals("") && !secondPartApplNo.equals("")) || !regnNo.equals("") || !loiNo.equals("")) {
                if (secondPartApplNo.trim().length() <= 10) {
                    if (searchByValue.equalsIgnoreCase("applNo")) {
                        String paddedApplNo = secondPartApplNo.trim();
                        if (paddedApplNo.replaceAll("[0-9]", "").length() == 0) {
                            paddedApplNo = String.format("%010d", Long.parseLong(secondPartApplNo.trim()));
                        }
                        allPrintWork = pmtImpl.getRePermitPrintRow(firstPartApplNo.trim() + paddedApplNo, "", null);
                        errorMsg = "No pending work against " + firstPartApplNo.trim() + paddedApplNo + "";
                    } else if (searchByValue.equalsIgnoreCase("regnNo")) {
                        allPrintWork = pmtImpl.getRePermitPrintRow("", regnNo, null);
                        errorMsg = "No pending work against " + regnNo + "";
                    } else if (searchByValue.equalsIgnoreCase("loiNo")) {
                        allPrintWork = pmtImpl.getRePermitPrintRow("", "", loiNo);
                        errorMsg = "No pending work against " + loiNo + "";
                    }

                    if (allPrintWork.size() > 0) {
                        // RequestContext rc = RequestContext.getCurrentInstance();
                        PrimeFaces.current().ajax().update("RePrintPermitDetails");
                    } else {
                        // RequestContext rc = RequestContext.getCurrentInstance();
                        PrimeFaces.current().ajax().update("RePrintPermitDetails");
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", errorMsg));
                    }
                } else {
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Invalid Application no.Minimum Character is 1 and Maximum Character is 10"));
                }
            } else {
                // RequestContext rc = RequestContext.getCurrentInstance();
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please enter Application No/Regn No"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void selectPurposeType() {
        String[][] data = MasterTableFiller.masterTables.VM_PERMIT_DOCUMENTS.getData();
        try {
            if (Integer.valueOf(getSelPurposeType()) == TableConstants.VM_PMT_FRESH_PUR_CD
                    || Integer.valueOf(getSelPurposeType()) == TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD) {
                array_pmt_typ.clear();
                data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
                for (int i = 0; i < data.length; i++) {
                    if ("UP,DL,GJ".contains(Util.getUserStateCode()) && !data[i][0].equals(TableConstants.NATIONAL_PERMIT)) {
                        array_pmt_typ.add(new SelectItem(data[i][0], data[i][1]));
                    }
                }
                arrayDocmentType.clear();
                boolPermitType = false;
                data = MasterTableFiller.masterTables.VM_PERMIT_DOCUMENTS.getData();
                for (int i = 0; i < data.length; i++) {
                    if (("1").equalsIgnoreCase(data[i][0]) || ("2").equalsIgnoreCase(data[i][0])) {
                        arrayDocmentType.add(new SelectItem(data[i][0], data[i][1]));
                    }
                }
            } else if (Integer.valueOf(getSelPurposeType()) == TableConstants.VM_PMT_DUPLICATE_PUR_CD
                    || Integer.valueOf(getSelPurposeType()) == TableConstants.VM_PMT_RENEWAL_PUR_CD) {
                arrayDocmentType.clear();
                boolPermitType = false;
                data = MasterTableFiller.masterTables.VM_PERMIT_DOCUMENTS.getData();
                for (int i = 0; i < data.length; i++) {
                    if (("1").equalsIgnoreCase(data[i][0]) || ("2").equalsIgnoreCase(data[i][0])
                            || ("5").equalsIgnoreCase(data[i][0])) {
                        arrayDocmentType.add(new SelectItem(data[i][0], data[i][1]));
                    }
                }
            } else {
                arrayDocmentType.clear();
                boolPermitType = true;
                switch (Integer.parseInt(getSelPurposeType())) {
                    case TableConstants.VM_PMT_APPLICATION_PUR_CD: {
                        for (int i = 0; i < data.length; i++) {
                            if (("3").equalsIgnoreCase(data[i][0])) {
                                arrayDocmentType.add(new SelectItem(data[i][0], data[i][1]));
                            }
                        }
                        break;
                    }
                    case TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD: {
                        for (int i = 0; i < data.length; i++) {
                            if (("10").equalsIgnoreCase(data[i][0])) {
                                arrayDocmentType.add(new SelectItem(data[i][0], data[i][1]));
                            }
                        }
                        break;
                    }
                    case TableConstants.VM_PMT_TEMP_PUR_CD: {
                        for (int i = 0; i < data.length; i++) {
                            if (("6").equalsIgnoreCase(data[i][0])) {
                                arrayDocmentType.add(new SelectItem(data[i][0], data[i][1]));
                            }
                        }
                        break;
                    }
                    case TableConstants.VM_PMT_SPECIAL_PUR_CD: {
                        for (int i = 0; i < data.length; i++) {
                            if (("7").equalsIgnoreCase(data[i][0])) {
                                arrayDocmentType.add(new SelectItem(data[i][0], data[i][1]));
                            }
                        }
                        break;
                    }
                    case TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD:
                    case TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD: {
                        for (int i = 0; i < data.length; i++) {
                            if (("5").equalsIgnoreCase(data[i][0])) {
                                arrayDocmentType.add(new SelectItem(data[i][0], data[i][1]));
                            }
                        }
                        break;
                    }
                    case TableConstants.VM_PMT_TRANSFER_PUR_CD:
                    case TableConstants.VM_PMT_CA_PUR_CD:
                    case TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD:
                    case TableConstants.VM_PMT_REPLACE_VEH_PUR_CD:
                    case TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD:
                    case TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD: {
                        boolPermitType = false;
                        for (int i = 0; i < data.length; i++) {
                            if (("1").equalsIgnoreCase(data[i][0]) || ("2").equalsIgnoreCase(data[i][0])
                                    || ("8").equalsIgnoreCase(data[i][0]) || ("5").equalsIgnoreCase(data[i][0])) {
                                arrayDocmentType.add(new SelectItem(data[i][0], data[i][1]));
                            }
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void singlePrint() {
        //RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().executeScript("PF('printPermitReport').show()");
    }

    public String printPermitReportInXhtml() {
        PrimeFaces.current().executeScript("PF('bui').show();");
        HttpSession ses = Util.getSession();
        pmtImpl = new PrintPermitImpl();
        Status_dobj status = new Status_dobj();
        status.setAppl_dt(appl_details.getAppl_dt());
        status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
        status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
        status.setStatus(getApp_disapp_dobj().getNew_status());
        status.setCurrent_role(appl_details.getCurrent_role());
        String redirect = "";
        try {
            if (getPrintPermit().equals("1")) {
                ses.setAttribute("byrePrint", getPrintPermit());
                ses.setAttribute("rePrintApplNo", pmtDetails.getAppl_no());
                ses.setAttribute("rePrintRegnNo", pmtDetails.getRegn_no());
                ses.setAttribute("searchValue", searchByValue);
                ses.setAttribute("OfferRedirect", "PrintForm");
                if (CommonUtils.isNullOrBlank(pmtDetails.getPur_cd())) {
                    throw new VahanException("Pupose not define. Please contact to system administrator");
                }
                redirect = pmtImpl.getPermitPrintInXHTML(pmtDetails.getAppl_no(), Integer.valueOf(pmtDetails.getPur_cd()), 0, pmtDetails.getDoc_id(), status, pmtDetails.getRegn_no());
            } else if (getPrintPermit().equals("2")) {
                ses.setAttribute("byregnNo", getPrintPermit());
                ses.setAttribute("regnNoBack", pmtDetails.getRegn_no());
                ses.setAttribute("OfferRedirect", "PrintForm");
                if (CommonUtils.isNullOrBlank(pmtDetails.getPur_cd())) {
                    throw new VahanException("Pupose not define. Please contact to system administrator");
                } else if (CommonUtils.isNullOrBlank(pmtDetails.getPmt_type())) {
                    throw new VahanException("Permit type not define. Please contact to system administrator");
                }
                redirect = pmtImpl.getPermitPrintInXHTML(pmtDetails.getAppl_no(), Integer.valueOf(pmtDetails.getPur_cd()), Integer.valueOf(pmtDetails.getPmt_type()), pmtDetails.getDoc_id(), status, pmtDetails.getRegn_no());
            } else if (getPrintPermit().equals("3")) {
                ses.setAttribute("bypurPmtType", getPrintPermit());
                ses.setAttribute("surrenderSlip", surrenderSlip);
                ses.setAttribute("purposeType", getSelPurposeType());
                ses.setAttribute("pmtType", getSel_pmt_type());
                ses.setAttribute("docType", getSelDocmentType());
                ses.setAttribute("OfferRedirect", "PrintForm");
                redirect = pmtImpl.getPermitPrintInXHTML(pmtDetails.getAppl_no(), Integer.valueOf(getSelPurposeType()), Integer.valueOf(getSel_pmt_type()), getSelDocmentType(), status, pmtDetails.getRegn_no());
            }
        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Information", e.getMessage(), FacesMessage.SEVERITY_INFO);
        }
        return redirect;
    }

    public void setHeaderFooterInReport(Map param) {
        param.put("headerLine1", "FORM P.Pr.C.");
        param.put("headerLine2", "(Rule 54 of the Delhi Motor Vehicles Rules, 1992)");
        param.put("headerLine3", "GOVERNMENT OF NATIONAL CAPITAL TERRITORY OF DELHI");
        param.put("headerLine4", "PRIVATE GOODS CARRIER'S PERMIT");
        param.put("headerLine3", "GOVERNMENT OF NATIONAL CAPITAL TERRITORY OF DELHI");
        param.put("footerLine1", "Secretary");
        param.put("footerLine2", "State Transport Authority,");
        param.put("footerLine3", "DELHI");
    }

    public String permitDocumentAtApproval(String applNo, String regn_no, int purCd, int pmtType) {
        String redirect = null;
        try {
            HttpSession ses = Util.getSession();
            ses.setAttribute("byregnNo", "2");
            ses.setAttribute("regnNoBack", regn_no);
            ses.setAttribute("OfferRedirect", "PermitPassForm");
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            status.setCurrent_role(appl_details.getCurrent_role());
            pmtImpl = new PrintPermitImpl();
            redirect = pmtImpl.getPermitPrintInXHTML(applNo, purCd, pmtType, CommonPermitPrintImpl.getDocumentId(applNo, regn_no), status, regn_no);
        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return redirect;
    }

    public String getSel_pmt_type() {
        return sel_pmt_type;
    }

    public void setSel_pmt_type(String sel_pmt_type) {
        this.sel_pmt_type = sel_pmt_type;
    }

    public List getArray_pmt_typ() {
        return array_pmt_typ;
    }

    public void setArray_pmt_typ(List array_pmt_typ) {
        this.array_pmt_typ = array_pmt_typ;
    }

    public List<PrintPermitShowDetails> getListPermit() {
        return listPermit;
    }

    public void setListPermit(List<PrintPermitShowDetails> listPermit) {
        this.listPermit = listPermit;
    }

    public List<PrintPermitShowDetails> getAllPrintWork() {
        return allPrintWork;
    }

    public void setAllPrintWork(List<PrintPermitShowDetails> allPrintWork) {
        this.allPrintWork = allPrintWork;
    }

    public PrintPermitShowDetails getPmtDetails() {
        return pmtDetails;
    }

    public void setPmtDetails(PrintPermitShowDetails pmtDetails) {
        this.pmtDetails = pmtDetails;
    }

    public String getSelPurposeType() {
        return selPurposeType;
    }

    public void setSelPurposeType(String selPurposeType) {
        this.selPurposeType = selPurposeType;
    }

    public List getArrayPurposeType() {
        return arrayPurposeType;
    }

    public void setArrayPurposeType(List arrayPurposeType) {
        this.arrayPurposeType = arrayPurposeType;
    }

    public String getSelDocmentType() {
        return selDocmentType;
    }

    public void setSelDocmentType(String selDocmentType) {
        this.selDocmentType = selDocmentType;
    }

    public List getArrayDocmentType() {
        return arrayDocmentType;
    }

    public void setArrayDocmentType(List arrayDocmentType) {
        this.arrayDocmentType = arrayDocmentType;
    }

    public boolean isBoolPermitType() {
        return boolPermitType;
    }

    public void setBoolPermitType(boolean boolPermitType) {
        this.boolPermitType = boolPermitType;
    }

    public boolean isBoolDocumentType() {
        return boolDocumentType;
    }

    public void setBoolDocumentType(boolean boolDocumentType) {
        this.boolDocumentType = boolDocumentType;
    }

    public String getSearchByValue() {
        return searchByValue;
    }

    public void setSearchByValue(String searchByValue) {
        this.searchByValue = searchByValue;
    }

    public String getFirstPartApplNo() {
        return firstPartApplNo;
    }

    public void setFirstPartApplNo(String firstPartApplNo) {
        this.firstPartApplNo = firstPartApplNo;
    }

    public String getSecondPartApplNo() {
        return secondPartApplNo;
    }

    public void setSecondPartApplNo(String secondPartApplNo) {
        this.secondPartApplNo = secondPartApplNo;
    }

    public String getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    public boolean isRePrintPermit() {
        return rePrintPermit;
    }

    public void setRePrintPermit(boolean rePrintPermit) {
        this.rePrintPermit = rePrintPermit;
    }

    public boolean isSurrenderSlip() {
        return surrenderSlip;
    }

    public void setSurrenderSlip(boolean surrenderSlip) {
        this.surrenderSlip = surrenderSlip;
    }

    /**
     * @return the filteredAllPrintWork
     */
    public List<PrintPermitShowDetails> getFilteredAllPrintWork() {
        return filteredAllPrintWork;
    }

    /**
     * @param filteredAllPrintWork the filteredAllPrintWork to set
     */
    public void setFilteredAllPrintWork(List<PrintPermitShowDetails> filteredAllPrintWork) {
        this.filteredAllPrintWork = filteredAllPrintWork;
    }

    public boolean isHidecombopanel() {
        return hidecombopanel;
    }

    public void setHidecombopanel(boolean hidecombopanel) {
        this.hidecombopanel = hidecombopanel;
    }

    public boolean isShowRegnNo() {
        return showRegnNo;
    }

    public void setShowRegnNo(boolean showRegnNo) {
        this.showRegnNo = showRegnNo;
    }

    public boolean isRenderColumn() {
        return renderColumn;
    }

    public void setRenderColumn(boolean renderColumn) {
        this.renderColumn = renderColumn;
    }

    public String getPrintPermit() {
        return printPermit;
    }

    public void setPrintPermit(String printPermit) {
        this.printPermit = printPermit;
    }

    public String getLoiNo() {
        return loiNo;
    }

    public void setLoiNo(String loiNo) {
        this.loiNo = loiNo;
    }
}