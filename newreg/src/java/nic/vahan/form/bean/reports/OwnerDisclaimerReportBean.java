/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import static nic.vahan.CommonUtils.FormulaUtils.fillTaxParametersFromDobj;
import nic.vahan.common.tax.VahanTaxClient;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.DOTaxDetail;
import nic.vahan.form.dobj.EpayDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.reports.OwnerDisclaimerReportDobj;
import nic.vahan.form.impl.EpayImpl;
import nic.vahan.form.impl.FeeImpl;
import nic.vahan.form.impl.NewImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
//import tax.web.service.VahanTaxParameters;
import nic.vahan.form.dobj.tax.VahanTaxParameters;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.form.dobj.TaxFieldDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.configuration.TmConfigurationReceipts;
import nic.vahan.form.impl.SeatAllotedDetails;
import nic.vahan.form.impl.TaxServer_Impl;
import static nic.vahan.form.impl.TaxServer_Impl.callTaxService;

/**
 *
 * @author nic
 */
@ManagedBean(name = "ownerDiclaimerBean")
@ViewScoped
public class OwnerDisclaimerReportBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(OwnerDisclaimerReportBean.class);
    private OwnerDisclaimerReportDobj dobj = null;
    private List<OwnerDisclaimerReportDobj> feeAndTaxList;
    private int slNo = 1;
    private String backButton;
    private String errorMessage;
    private boolean pendingTrans = true;
    private String category;
    private String printType;
    private long grandTotal = 0;
    private String text;
    private String printedDate;
    private boolean printTaxAndFee;
    private boolean showSurcharge;
    private boolean showInterest;
    private boolean showRebate;
    private String printTypeMainHeading;
    private String printTypeSubHeading;
    private boolean dlrRptBackBtn = false;
    private boolean provRcRtoBackBtn = false;
    private boolean ownerDiscBackBtn = false;
    private boolean newAppDiscBackBtn = false;
    private boolean renderqrCode = false;
    private String tax1Label;
    private String tax2Label;
    private boolean showTax1 = false;
    private boolean showTax2 = false;
    private String appl_no = null;
    private String regn_no = null;
    private int purCd = 0;
    private boolean renderRegnSeriesMsg = true;
    private boolean renderNote = false;

    public OwnerDisclaimerReportBean() {
        String reportEntryFormat = null;
        Map mapReport;
        mapReport = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        appl_no = (String) mapReport.get("applNo");
        regn_no = (String) mapReport.get("regnNo");
        category = (String) mapReport.get("category");
        reportEntryFormat = (String) mapReport.get("reportEntry");
        printType = (String) mapReport.get("printType");
        if (mapReport.get("purCd") != null) {
            if (mapReport.get("purCd") instanceof String) {
                String purCode = (String) mapReport.get("purCd");
                if (!"".equals(purCode)) {
                    purCd = Integer.parseInt(purCode);
                }
            } else if (mapReport.get("purCd") instanceof Integer) {
                purCd = (Integer) mapReport.get("purCd");
            }
        }
        if (purCd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
            ownerDiscBackBtn = false;
        }
        dobj = new OwnerDisclaimerReportDobj();
        OwnerImpl ownerImpl = new OwnerImpl();
        Owner_dobj ownerDobj = new Owner_dobj();
        feeAndTaxList = new ArrayList<OwnerDisclaimerReportDobj>();
        FeeImpl feeImpl = new FeeImpl();
        Exception ex = null;
        long netTaxAmount = 0;
        String roadTaxMod = null;
        try {
            TmConfigurationDobj tmConf = Util.getTmConfiguration();
            String user_catg = Util.getUserCategory();
            SeatAllotedDetails selectedSeat = Util.getSelectedSeat();
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date dateobj = new Date();
            printedDate = df.format(dateobj);
            if (printType != null) {
                if (printType.equals("provisionalRCAtRtoLevel")) {
                    printTypeMainHeading = "Temporary Authorization of Registration Certificate";
                    printTypeSubHeading = "Registration No :";
                } else if (printType.equals("provisionalRC")) {
                    printTypeMainHeading = "Provisional Registration Certificate";
                    printTypeSubHeading = "Provisional Registration Mark :";
                } else if (printType.equals("disclaimer")) {
                    printTypeMainHeading = "DISCLAIMER";
                    printTypeSubHeading = "Registration No :";
                }
            }
            if (reportEntryFormat != null) {
                if (reportEntryFormat.equals("reportFormat")) {
                    if (user_catg.equals(TableConstants.User_Dealer) && selectedSeat != null && selectedSeat.getAction_cd() != TableConstants.TM_ROLE_DEALER_PRINT_RC) {
                        dlrRptBackBtn = true;
                    } else {
                        if (printType != null) {
                            if (printType.equals("provisionalRCAtRtoLevel")) {
                                provRcRtoBackBtn = true;
                            } else {
                                ownerDiscBackBtn = true;
                            }
                        }
                    }
                } else {
                    newAppDiscBackBtn = true;
                }
            }
            if (category != null) {
                if (category.equalsIgnoreCase("newRegisteredVehicles")) {
                    if (appl_no == null || regn_no == null) {
                        return;
                    }
                    dobj = PrintDocImpl.getOwnerDisclaimerDobj(regn_no, appl_no);
                    if (dobj == null) {
                        throw new VahanException("Details not found for application no " + appl_no);
                    }
                    if (printType != null) {
                        if (printType.equals("provisionalRC")) {
                            if (dobj.getRegnDate() != null && !dobj.getRegnDate().equals("")) {
                                dobj.setRegnNextMonthDate(ServerUtil.getNextMonth(dobj.getRegnDate()));
                            }
                        }
                    }
                    TmConfigurationReceipts configFeeFineZero = ServerUtil.getTmConfigurationReceipts(dobj.getStateCD());
                    ownerDobj = ownerImpl.set_Owner_appl_db_to_dobj(null, appl_no.toUpperCase(), "", TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE);
                    if (dobj.getVehType() != null && !dobj.getVehType().equals("") && !dobj.getVehType().equals(TableConstants.VM_VEHTYPE_TRANSPORT_DESCR)) {
                        if (!ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                            VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj);
                            vehParameters.setNEW_VCH("Y");
                            if (Util.getUserStateCode() != null && "GA,UP".contains(Util.getUserStateCode()) && isCondition(replaceTagValues(tmConf.getFee_amt_zero(), vehParameters), "OwnerDisclaimerReportBean-tm_configuration-Fee_amt_zero(State:" + Util.getUserStateCode() + ")")) {
                                printTaxAndFee = false;
                            } else {
                                printTaxAndFee = true;
                                List<Integer> listPurCd = new ArrayList<>();
                                List<EpayDobj> feeList = EpayImpl.getFeeDetailsByAction(ownerDobj, "NEW", ownerDobj.getVh_class(), ownerDobj.getVch_catg(), null, new java.util.Date());
                                for (EpayDobj epayDobj : feeList) {
                                    if (epayDobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_ADD_HYPO) {
                                        boolean isVehicleHypothecated = feeImpl.isHypothecated(ownerDobj.getAppl_no().toUpperCase(), TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE);
                                        if (isVehicleHypothecated == true) {
                                            if (epayDobj.getE_TaxFee() != 0) {
                                                listPurCd.add(epayDobj.getPurCd());
                                                OwnerDisclaimerReportDobj dob = new OwnerDisclaimerReportDobj();
                                                dob.setSlNo(String.valueOf(slNo++));
                                                dob.setHead(epayDobj.getPurCdDescr());
                                                dob.setAmount(String.valueOf(epayDobj.getE_TaxFee()));
                                                dob.setSurcharge("0");
                                                dob.setInterest("0");
                                                dob.setRebate("0");
                                                dob.setTax1("0");
                                                dob.setTax2("0");
                                                dob.setFine(String.valueOf(epayDobj.getE_FinePenalty()));
                                                dob.setTotalAmount(String.valueOf(String.valueOf(epayDobj.getE_total())));
                                                grandTotal = grandTotal + epayDobj.getE_total();
                                                feeAndTaxList.add(dob);
                                            }
                                        }
                                    } else {
                                        if (epayDobj.getE_TaxFee() != 0) {
                                            listPurCd.add(epayDobj.getPurCd());
                                            OwnerDisclaimerReportDobj dob = new OwnerDisclaimerReportDobj();
                                            dob.setSlNo(String.valueOf(slNo++));
                                            dob.setHead(epayDobj.getPurCdDescr());
                                            if (configFeeFineZero != null) {
                                                vehParameters.setTRANSACTION_PUR_CD(epayDobj.getPurCd());
                                                if (isCondition(replaceTagValues(configFeeFineZero.getFeeAmtZero(), vehParameters), "OwnerDisclaimerReportBean-tm_configuration_receipts-Fee_amt_zero(State:" + Util.getUserStateCode() + ")")) {
                                                    dob.setAmount(String.valueOf(0));
                                                    dob.setTotalAmount(String.valueOf(Long.parseLong(dob.getAmount()) + epayDobj.getE_FinePenalty()));
                                                } else {
                                                    dob.setAmount(String.valueOf(epayDobj.getE_TaxFee()));
                                                    dob.setTotalAmount(String.valueOf(epayDobj.getE_TaxFee() + epayDobj.getE_FinePenalty()));
                                                }
                                                if (isCondition(replaceTagValues(configFeeFineZero.getFineAmtZero(), vehParameters), "OwnerDisclaimerReportBean-tm_configuration_receipts-Fine_amt_zero(State:" + Util.getUserStateCode() + ")")) {
                                                    epayDobj.setE_FinePenalty(0L);
                                                    dob.setTotalAmount(String.valueOf(Long.parseLong(dob.getAmount()) + epayDobj.getE_FinePenalty()));
                                                }
                                            } else {
                                                dob.setAmount(String.valueOf(epayDobj.getE_TaxFee()));
                                                dob.setTotalAmount(String.valueOf(epayDobj.getE_TaxFee() + epayDobj.getE_FinePenalty()));
                                            }
                                            dob.setSurcharge("0");
                                            dob.setInterest("0");
                                            dob.setRebate("0");
                                            dob.setTax1("0");
                                            dob.setTax2("0");
                                            dob.setFine(String.valueOf(epayDobj.getE_FinePenalty()));
                                            grandTotal = grandTotal + Long.parseLong(dob.getTotalAmount());
                                            feeAndTaxList.add(dob);
                                        }
                                    }
                                }

                                boolean isSmartCard = feeImpl.verifySmartCard();
                                if (isSmartCard == true) {
                                    Long smartCardFee = EpayImpl.getPurposeCodeFee(ownerDobj, ownerDobj.getVh_class(), 80, ownerDobj.getVch_catg());
                                    if (smartCardFee != 0L) {
                                        listPurCd.add(TableConstants.VM_TRANSACTION_MAST_SMART_CARD_FEE);
                                        OwnerDisclaimerReportDobj dob = new OwnerDisclaimerReportDobj();
                                        dob.setSlNo(String.valueOf(slNo++));
                                        dob.setHead("Smart Card Fee");
                                        dob.setAmount(String.valueOf(smartCardFee));
                                        dob.setSurcharge("0");
                                        dob.setInterest("0");
                                        dob.setRebate("0");
                                        dob.setTax1("0");
                                        dob.setTax2("0");
                                        dob.setFine(String.valueOf(0));
                                        dob.setTotalAmount(String.valueOf(smartCardFee));
                                        grandTotal = grandTotal + smartCardFee;
                                        feeAndTaxList.add(dob);
                                    }
                                }
                                List<EpayDobj> listTaxTypes = EpayImpl.getFeeDetailsByActionTax(ownerDobj, "NEW");
                                Map<Integer, String> rqrdTaxModes = NewImpl.getRqrdTaxModes(ownerDobj.getRqrd_tax_modes());
                                for (EpayDobj dobj : listTaxTypes) {
                                    VahanTaxParameters taxParameters = fillTaxParametersFromDobj(ownerDobj, null);
                                    if (rqrdTaxModes != null && !rqrdTaxModes.isEmpty()) {
                                        if (!isCondition(replaceTagValues(tmConf.getTax_exemption(), vehParameters), "OwnerDisclaimerReportBean-tm_configuration-Tax_exemption(State:" + Util.getUserStateCode() + ")")) {
                                            for (Map.Entry<Integer, String> entry : rqrdTaxModes.entrySet()) {
                                                if (entry.getKey() == dobj.getPurCd()) {
                                                    listPurCd.add(dobj.getPurCd());
                                                    taxParameters.setTAX_MODE(entry.getValue());
                                                    taxParameters.setPUR_CD(dobj.getPurCd());
                                                    taxParameters.setNEW_VCH("Y");
                                                    taxParameters.setTAX_MODE_NO_ADV(1);
                                                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));
                                                    if (entry.getKey() == 70 && "MH".contains(Util.getUserStateCode())) {
                                                        taxParameters.setNET_TAX_AMT((double) netTaxAmount);
                                                        taxParameters.setTAX_MODE(roadTaxMod);
                                                    }
                                                    if (ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_TEMPORARY)
                                                            && ownerDobj.getTempReg() != null && ownerDobj.getTempReg().getTmp_valid_upto() != null) {
                                                        if ("HP,HR".contains(Util.getUserStateCode())) {
                                                            taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));
                                                        } else if (DateUtils.compareDates(ownerDobj.getTempReg().getTmp_valid_upto(), new Date()) == 2) {
                                                            taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(new Date()));
                                                        } else {
                                                            taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(DateUtils.addToDate(ownerDobj.getTempReg().getTmp_valid_upto(), 1, 1)));
                                                        }
                                                    } else if (ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)) {
                                                        if ("MH".contains(Util.getUserStateCode())) {
                                                            taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getOtherStateVehDobj().getNocDate()));
                                                        } else {
                                                            taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getOtherStateVehDobj().getStateEntryDate()));
                                                        }

                                                        if (ownerDobj.getRegn_dt() == null) {
                                                            taxParameters.setREGN_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));
                                                        }
                                                    } else if (ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_CD)) {
                                                        taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(new Date()));
                                                        taxParameters.setREGN_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));
                                                    }
                                                    if ("JH".contains(Util.getUserStateCode()) && ownerDobj.getOwner_cd() == 1 && ownerDobj.getVh_class() == 7) {
                                                        if ((purCd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || purCd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE)
                                                                && (ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_TEMPORARY) || ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_NEW))) {
                                                            int panCount = OwnerImpl.checkPanNoCount(ownerDobj.getOwner_identity().getPan_no(), ownerDobj.getOwner_identity().getState_cd());
                                                            if (panCount > 0) {
                                                                taxParameters.setOTHER_CRITERIA(99);
                                                            }
                                                        }
                                                    }
                                                    taxParameters.setTRANSACTION_PUR_CD(purCd);
                                                    List<DOTaxDetail> listTaxBreakUp = null;
                                                    if (!"KL".contains(Util.getUserStateCode())) {
                                                        try {
                                                            listTaxBreakUp = callTaxService(taxParameters);
                                                        } catch (VahanException ve) {
                                                        }
                                                    }
                                                    double surcharge = 0;
                                                    double interest = 0;
                                                    double rebate = 0;
                                                    double tax1 = 0;
                                                    double tax2 = 0;
                                                    if (listTaxBreakUp != null && !listTaxBreakUp.isEmpty()) {
                                                        for (DOTaxDetail doTax : listTaxBreakUp) {
                                                            OwnerDisclaimerReportDobj dob = new OwnerDisclaimerReportDobj();
                                                            dob.setSlNo(String.valueOf(slNo++));
                                                            dob.setHead(doTax.getTAX_HEAD());
                                                            dob.setAmount(String.valueOf(doTax.getAMOUNT()).replace(".0", ""));
                                                            dob.setTax1(String.valueOf(doTax.getAMOUNT1()).replace(".0", ""));
                                                            tax1 = (tax1 + doTax.getAMOUNT1());
                                                            dob.setTax2(String.valueOf(doTax.getAMOUNT2()).replace(".0", ""));
                                                            tax2 = (tax2 + doTax.getAMOUNT2());
                                                            dob.setSurcharge(String.valueOf(doTax.getSURCHARGE()).replace(".0", ""));
                                                            surcharge = (surcharge + doTax.getSURCHARGE());
                                                            dob.setInterest(String.valueOf(doTax.getINTEREST()).replace(".0", ""));
                                                            interest = interest + doTax.getINTEREST();
                                                            dob.setRebate(String.valueOf(doTax.getREBATE()).replace(".0", ""));
                                                            rebate = rebate + doTax.getREBATE();
                                                            dob.setFine(String.valueOf(doTax.getPENALTY()).replace(".0", ""));
                                                            dob.setTotalAmount(String.valueOf(String.valueOf(doTax.getAMOUNT() + doTax.getSURCHARGE() + doTax.getINTEREST() - doTax.getREBATE() + doTax.getPENALTY() + doTax.getAMOUNT1() + doTax.getAMOUNT2())).replace(".0", ""));
                                                            grandTotal = grandTotal + (long) (doTax.getAMOUNT() + doTax.getSURCHARGE() + doTax.getINTEREST() - doTax.getREBATE() + doTax.getPENALTY() + doTax.getAMOUNT1() + doTax.getAMOUNT2());
                                                            if (entry.getKey() == 58 && "MH".contains(Util.getUserStateCode())) {
                                                                netTaxAmount = Long.parseLong(String.valueOf(doTax.getAMOUNT()).replace(".0", ""));
                                                                roadTaxMod = entry.getValue();
                                                            }
                                                            if (tax1 > 0 || tax2 > 0) {
                                                                TaxFieldDobj taxField = TaxServer_Impl.getTaxField();
                                                                setTax1Label(taxField.getTax1Label());
                                                                setTax2Label(taxField.getTax2Label());
                                                            }
                                                            feeAndTaxList.add(dob);
                                                        }
                                                    }
                                                    if (interest > 0) {
                                                        showInterest = true;
                                                    }
                                                    if (surcharge > 0) {
                                                        showSurcharge = true;
                                                    }
                                                    if (rebate > 0) {
                                                        showRebate = true;
                                                    }
                                                    if (tax1 > 0) {
                                                        showTax1 = true;
                                                    }
                                                    if (tax2 > 0) {
                                                        showTax2 = true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (EpayImpl.getServiceChargeType() != null) {
                                    VehicleParameters param = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj);
                                    param.setNEW_VCH("Y");
                                    Long userCharges = EpayImpl.getUserChargesFee(ownerDobj, listPurCd, param);
                                    OwnerDisclaimerReportDobj dob = new OwnerDisclaimerReportDobj();
                                    dob.setSlNo(String.valueOf(slNo++));
                                    dob.setHead("Service Charge Fee");
                                    dob.setAmount(String.valueOf(userCharges));
                                    dob.setSurcharge("0");
                                    dob.setInterest("0");
                                    dob.setRebate("0");
                                    dob.setTax1("0");
                                    dob.setTax2("0");
                                    dob.setFine(String.valueOf(0));
                                    dob.setTotalAmount(String.valueOf(userCharges));
                                    grandTotal = grandTotal + userCharges;
                                    feeAndTaxList.add(dob);
                                }
                            }
                        }
                    }
                } else if (category.equalsIgnoreCase("registeredVehicles")) {
                    if (appl_no == null) {
                        return;
                    }
                    if (printType != null) {
                        dobj = PrintDocImpl.getOwnerDisclaimerDobjForRegisteredVehicle(appl_no, printType);
                        if (dobj == null) {
                            throw new VahanException("Details not found for application no " + appl_no);
                        }
                        if (printType.equals("provisionalRCAtRtoLevel")) {
                            ownerDobj = ownerImpl.set_Owner_appl_db_to_dobj(dobj.getRegnNO(), "", "", TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE);
                            if (dobj.getOpDT() != null && !dobj.getOpDT().equals("")) {
                                dobj.setValidityDate(dobj.getOpDate());
                                dobj.setRegnNextMonthDate(ServerUtil.getNextMonth(dobj.getOpDT()));
                            }
                        }
                    }

                }
                if (dobj != null) {
                    setRenderqrCode(true);
                    text = "Regn. No." + dobj.getRegnNO() + " Chassis No." + dobj.getChassiNO() + " Engine No." + dobj.getEngNO();
                    if (!"REGN NO NOT ASSIGN".equals(dobj.getRegnNO())) {
                        renderRegnSeriesMsg = false;
                    }
                    if ("CG".equals(Util.getUserStateCode()) && category.equalsIgnoreCase("newRegisteredVehicles")) {
                        renderNote = true;
                    }

                }
            }
        } catch (VahanException vme) {
            pendingTrans = false;
            errorMessage = vme.getMessage();
        } catch (Exception e) {
            ex = e;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            errorMessage = TableConstants.SomthingWentWrong;
        }
    }

//    public List<DOTaxDetail> callTaxService(VahanTaxParameters taxParameters) throws VahanException {
//        List<DOTaxDetail> tempTaxList = null;
//
//        VahanTaxClient taxClient = null;
//        try {
//            taxClient = new VahanTaxClient();
//            String taxServiceResponse = taxClient.getTaxDetails(taxParameters);
//            tempTaxList = taxClient.parseTaxResponse(taxServiceResponse, taxParameters.getPURCD(), taxParameters.getTAXMODE());
//
//        } catch (javax.xml.ws.WebServiceException e) {
//            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
//        }
//
//        return tempTaxList;
//    }
    public String printReports(String printType) throws VahanException {
        return ServerUtil.printReports(printType, appl_no, purCd, regn_no);
    }

    /**
     * @return the dobj
     */
    public OwnerDisclaimerReportDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(OwnerDisclaimerReportDobj dobj) {
        this.dobj = dobj;
    }

    /**
     * @return the feeAndTaxList
     */
    public List<OwnerDisclaimerReportDobj> getFeeAndTaxList() {
        return feeAndTaxList;
    }

    /**
     * @param feeAndTaxList the feeAndTaxList to set
     */
    public void setFeeAndTaxList(List<OwnerDisclaimerReportDobj> feeAndTaxList) {
        this.feeAndTaxList = feeAndTaxList;
    }

    public String backButton() {
        return backButton;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * @return the pendingTrans
     */
    public boolean isPendingTrans() {
        return pendingTrans;
    }

    /**
     * @param pendingTrans the pendingTrans to set
     */
    public void setPendingTrans(boolean pendingTrans) {
        this.pendingTrans = pendingTrans;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return the grandTotal
     */
    public long getGrandTotal() {
        return grandTotal;
    }

    /**
     * @param grandTotal the grandTotal to set
     */
    public void setGrandTotal(long grandTotal) {
        this.grandTotal = grandTotal;
    }

    /**
     * @return the printType
     */
    public String getPrintType() {
        return printType;
    }

    /**
     * @param printType the printType to set
     */
    public void setPrintType(String printType) {
        this.printType = printType;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the printedDate
     */
    public String getPrintedDate() {
        return printedDate;
    }

    /**
     * @param printedDate the printedDate to set
     */
    public void setPrintedDate(String printedDate) {
        this.printedDate = printedDate;
    }

    /**
     * @return the printTaxAndFee
     */
    public boolean isPrintTaxAndFee() {
        return printTaxAndFee;
    }

    /**
     * @param printTaxAndFee the printTaxAndFee to set
     */
    public void setPrintTaxAndFee(boolean printTaxAndFee) {
        this.printTaxAndFee = printTaxAndFee;
    }

    /**
     * @return the showSurcharge
     */
    public boolean isShowSurcharge() {
        return showSurcharge;
    }

    /**
     * @param showSurcharge the showSurcharge to set
     */
    public void setShowSurcharge(boolean showSurcharge) {
        this.showSurcharge = showSurcharge;
    }

    /**
     * @return the showInterest
     */
    public boolean isShowInterest() {
        return showInterest;
    }

    /**
     * @param showInterest the showInterest to set
     */
    public void setShowInterest(boolean showInterest) {
        this.showInterest = showInterest;
    }

    /**
     * @return the showRebate
     */
    public boolean isShowRebate() {
        return showRebate;
    }

    /**
     * @param showRebate the showRebate to set
     */
    public void setShowRebate(boolean showRebate) {
        this.showRebate = showRebate;
    }

    /**
     * @return the printTypeMainHeading
     */
    public String getPrintTypeMainHeading() {
        return printTypeMainHeading;
    }

    /**
     * @param printTypeMainHeading the printTypeMainHeading to set
     */
    public void setPrintTypeMainHeading(String printTypeMainHeading) {
        this.printTypeMainHeading = printTypeMainHeading;
    }

    /**
     * @return the printTypeSubHeading
     */
    public String getPrintTypeSubHeading() {
        return printTypeSubHeading;
    }

    /**
     * @param printTypeSubHeading the printTypeSubHeading to set
     */
    public void setPrintTypeSubHeading(String printTypeSubHeading) {
        this.printTypeSubHeading = printTypeSubHeading;
    }

    /**
     * @return the dlrRptBackBtn
     */
    public boolean isDlrRptBackBtn() {
        return dlrRptBackBtn;
    }

    /**
     * @param dlrRptBackBtn the dlrRptBackBtn to set
     */
    public void setDlrRptBackBtn(boolean dlrRptBackBtn) {
        this.dlrRptBackBtn = dlrRptBackBtn;
    }

    /**
     * @return the provRcRtoBackBtn
     */
    public boolean isProvRcRtoBackBtn() {
        return provRcRtoBackBtn;
    }

    /**
     * @param provRcRtoBackBtn the provRcRtoBackBtn to set
     */
    public void setProvRcRtoBackBtn(boolean provRcRtoBackBtn) {
        this.provRcRtoBackBtn = provRcRtoBackBtn;
    }

    /**
     * @return the ownerDiscBackBtn
     */
    public boolean isOwnerDiscBackBtn() {
        return ownerDiscBackBtn;
    }

    /**
     * @param ownerDiscBackBtn the ownerDiscBackBtn to set
     */
    public void setOwnerDiscBackBtn(boolean ownerDiscBackBtn) {
        this.ownerDiscBackBtn = ownerDiscBackBtn;
    }

    /**
     * @return the newAppDiscBackBtn
     */
    public boolean isNewAppDiscBackBtn() {
        return newAppDiscBackBtn;
    }

    /**
     * @param newAppDiscBackBtn the newAppDiscBackBtn to set
     */
    public void setNewAppDiscBackBtn(boolean newAppDiscBackBtn) {
        this.newAppDiscBackBtn = newAppDiscBackBtn;
    }

    /**
     * @return the renderqrCode
     */
    public boolean isRenderqrCode() {
        return renderqrCode;
    }

    /**
     * @param renderqrCode the renderqrCode to set
     */
    public void setRenderqrCode(boolean renderqrCode) {
        this.renderqrCode = renderqrCode;
    }

    /**
     * @return the tax1Label
     */
    public String getTax1Label() {
        return tax1Label;
    }

    /**
     * @param tax1Label the tax1Label to set
     */
    public void setTax1Label(String tax1Label) {
        this.tax1Label = tax1Label;
    }

    /**
     * @return the tax2Label
     */
    public String getTax2Label() {
        return tax2Label;
    }

    /**
     * @param tax2Label the tax2Label to set
     */
    public void setTax2Label(String tax2Label) {
        this.tax2Label = tax2Label;
    }

    /**
     * @return the showTax1
     */
    public boolean isShowTax1() {
        return showTax1;
    }

    /**
     * @param showTax1 the showTax1 to set
     */
    public void setShowTax1(boolean showTax1) {
        this.showTax1 = showTax1;
    }

    /**
     * @return the showTax2
     */
    public boolean isShowTax2() {
        return showTax2;
    }

    /**
     * @param showTax2 the showTax2 to set
     */
    public void setShowTax2(boolean showTax2) {
        this.showTax2 = showTax2;
    }

    /**
     * @return the appl_no
     */
    public String getAppl_no() {
        return appl_no;
    }

    /**
     * @param appl_no the appl_no to set
     */
    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    /**
     * @return the regn_no
     */
    public String getRegn_no() {
        return regn_no;
    }

    /**
     * @param regn_no the regn_no to set
     */
    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    /**
     * @return the purCd
     */
    public int getPurCd() {
        return purCd;
    }

    /**
     * @param purCd the purCd to set
     */
    public void setPurCd(int purCd) {
        this.purCd = purCd;
    }

    /**
     * @return the renderRegnSeriesMsg
     */
    public boolean isRenderRegnSeriesMsg() {
        return renderRegnSeriesMsg;
    }

    /**
     * @param renderRegnSeriesMsg the renderRegnSeriesMsg to set
     */
    public void setRenderRegnSeriesMsg(boolean renderRegnSeriesMsg) {
        this.renderRegnSeriesMsg = renderRegnSeriesMsg;
    }

    /**
     * @return the renderNote
     */
    public boolean isRenderNote() {
        return renderNote;
    }

    /**
     * @param renderNote the renderNote to set
     */
    public void setRenderNote(boolean renderNote) {
        this.renderNote = renderNote;
    }
}
