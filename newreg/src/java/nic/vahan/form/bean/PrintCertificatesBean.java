/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.PrintCertificatesDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.VmSmartCardHsrpDobj;
import nic.vahan.form.dobj.fitness.TmConfigurationFitnessDobj;
import nic.vahan.form.impl.ApplicationInwardImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.SeatAllotedDetails;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Afzal
 */
@ManagedBean(name = "printCerts")
@ViewScoped
public class PrintCertificatesBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(PrintCertificatesBean.class);
    private String appl_no;
    private List<PrintCertificatesBean> filteredSeat = null;
    private int action_cd;
    private String indexValue = "";
    private String btn_print_label = "";
    private String main_header_label = "";
    private String paper_rc;
    private List<PrintCertificatesDobj> printCertDobj = new ArrayList<PrintCertificatesDobj>();
    private List<PrintCertificatesDobj> selectedCertDobj = new ArrayList<PrintCertificatesDobj>();
    private TmConfigurationDobj tmConfDobj = null;
    private boolean provRcButtonVisibility = false;
    private boolean printRc = true;
    private String rcRadiobtn = "";
    private boolean isprintedRc = false;
    private String regn_no;
    private boolean printRcbyRegnNo = false;
    private boolean printFc = false;
    private String selectPrintColumnHeader = "";
    private String selectDataTableHeader = "";
    SeatAllotedDetails seatDtl = null;
    private boolean ispendingFC = false;
    private boolean isprintedFC = false;
    private VmSmartCardHsrpDobj vmSmartCardHsrpDobj = null;
    private boolean printTempRC = false;
    private boolean printTempRcbyRegnNo = false;
    private String state_cd;
    private int off_cd;
    private String labelRegnNotxtField;
    private String form38And38ARadiobtn = "";
    private boolean isform38 = false;
    private boolean isRCLabel = false;
    private boolean renderSearchBy = true;
    private String userCatg;
    private boolean renderHsrpDialog = false;

    public void setListBeans(List<PrintCertificatesDobj> listDobjs) {
        setPrintCertDobj(listDobjs);
    }

    @PostConstruct
    public void init() {
        try {
            state_cd = Util.getUserStateCode();
            if (Util.getSelectedSeat() != null) {
                off_cd = Util.getSelectedSeat().getOff_cd();
            }
            seatDtl = (SeatAllotedDetails) Util.getSession().getAttribute("SelectedSeat");
            userCatg = Util.getUserCategory();
            tmConfDobj = Util.getTmConfiguration();
            if (state_cd == null || seatDtl == null || userCatg == null || tmConfDobj == null || off_cd == 0) {
                throw new VahanException("Error in Getting Certificate details,Please try again ");
            }
            if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_PRINTED_RC) {
                setVmSmartCardHsrpDobj(ServerUtil.getVmSmartCardHsrpParameters(state_cd, off_cd));
                setPrintRc(false);
                setPrintFc(false);
                setIsprintedRc(true);
                rcRadiobtn = "REPRINTRC";
                selectPrintColumnHeader = "Select to print RC";
                selectDataTableHeader = "Printed Registration Certificate";
                setMain_header_label("Reprint RC");
                setBtn_print_label("Printed RC");
                labelRegnNotxtField = "Registration No";
                setPrintRcbyRegnNo(true);
                this.setPaper_rc(getVmSmartCardHsrpDobj().getPaper_rc());
                provRcButtonVisibility = getTmConfDobj().isProv_rc_rto();
                setProvRcButtonVisibility(false);
                setPrintTempRC(false);
                setIsRCLabel(true);
                setRenderHsrpDialog(true);
            } else if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_REPRINT_FC) {
                setPrintRc(false);
                setPrintFc(true);
                setIsprintedRc(true);
                setPrintFc(true);
                rcRadiobtn = "REPRINTFC";
                renderSearchBy = false;
                selectPrintColumnHeader = "Select to print FC";
                selectDataTableHeader = "Printed Fitness Certificate";
                setMain_header_label("Reprint FC");
                setBtn_print_label("Printed FC");
                labelRegnNotxtField = "Registration No";
                setPrintRcbyRegnNo(true);
                form38And38ARadiobtn = "FORM38";
                setProvRcButtonVisibility(false);
                setPrintTempRC(false);
                setIsRCLabel(false);
                setRenderHsrpDialog(false);
            }
            if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_REPRINT_TMP_RC) {
                setPrintRc(false);
                setPrintFc(false);
                setIsprintedRc(true);
                rcRadiobtn = "REPRINTTEMPRC";
                selectPrintColumnHeader = "Select to print Temp RC";
                selectDataTableHeader = "Printed Temporary Registration Certificate";
                setMain_header_label("Reprint TempRC");
                setBtn_print_label("Printed TempRC");
                labelRegnNotxtField = "Temporary Registration No";
                setPrintRcbyRegnNo(true);
                provRcButtonVisibility = getTmConfDobj().isProv_rc_rto();
                setProvRcButtonVisibility(false);
                setPrintTempRC(false);
                setIsRCLabel(false);
                setRenderHsrpDialog(false);
            } else if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_RC || seatDtl.getAction_cd() == TableConstants.TM_ROLE_DEALER_PRINT_RC) {
                setVmSmartCardHsrpDobj(ServerUtil.getVmSmartCardHsrpParameters(state_cd, off_cd));
                rcRadiobtn = "REGNNORC";
                labelRegnNotxtField = "Registration No";
                selectPrintColumnHeader = "Select to print RC";
                selectDataTableHeader = "Print Registration Certificate";
                setMain_header_label("RC Print Form");
                setBtn_print_label("Print RC");
                setPrintRcbyRegnNo(true);
                this.setPaper_rc(getVmSmartCardHsrpDobj().getPaper_rc());
                provRcButtonVisibility = getTmConfDobj().isProv_rc_rto();
                setPrintTempRC(false);
                setIsRCLabel(true);
                setRenderHsrpDialog(true);
            } else if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_FC) {
                rcRadiobtn = "REGNNOFC";
                setPrintRcbyRegnNo(true);
                setPrintRc(false);
                setPrintFc(true);
                labelRegnNotxtField = "Registration No";
                setSelectPrintColumnHeader("Select to print FC");
                setSelectDataTableHeader("Print Fitness Certificate");
                setMain_header_label("Print Fitness Certificate");
                setBtn_print_label("Print Form-38");
                setProvRcButtonVisibility(false);
                setPrintTempRC(false);
                form38And38ARadiobtn = "FORM38";
                setIsform38(true);
                setIsRCLabel(false);
                setRenderHsrpDialog(false);
            }
            if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_TMP_RC || seatDtl.getAction_cd() == TableConstants.TM_ROLE_DEALER_PRINT_TMP_RC) {
                rcRadiobtn = "TEMPRC";
                setPrintRcbyRegnNo(true);
                setPrintRc(false);
                setPrintTempRC(true);
                labelRegnNotxtField = "Temp Registration No";
                setSelectPrintColumnHeader("Select to print Temp RC");
                setSelectDataTableHeader("Print Temp RC");
                setMain_header_label("Temp RC Print Form");
                setBtn_print_label("Print Temp RC");
                if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_TMP_RC) {
                    //this.setListBeans(PrintDocImpl.getTempPrintDocsDetails(TableConstants.VM_TRANSACTION_MAST_TEMP_REG));
                } else if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_DEALER_PRINT_TMP_RC) {
                    Long user_cd = Long.parseLong(Util.getSession().getAttribute("emp_cd").toString());
                    if (user_cd != null && !user_cd.equals("")) {
                        Map makerAndDealerDetail = OwnerImpl.getDealerDetail(user_cd);
                        if (makerAndDealerDetail.get("dealer_cd") != null && !makerAndDealerDetail.get("dealer_cd").equals("")) {
                            //this.setListBeans(PrintDocImpl.getTempPrintDocsDealerDetails(TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE, makerAndDealerDetail.get("dealer_cd").toString()));
                        }
                    }
                }
//                else if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_DEALER_PRINT_TMP_RC_AT_RTO) {
//                    //this.setListBeans(PrintDocImpl.getTempPrintDocsDealerDetailsAtRto());
//                }
                setRenderHsrpDialog(false);
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", ve.getMessage()));
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
    }

    public void confirmPrintRC() {
        long userCd = 0l;
        String dealerCd = "";
        printCertDobj.clear();
        if (selectedCertDobj != null) {
            selectedCertDobj.clear();
        }
        if (isPrintTempRC()) {
            if (regn_no.trim().equalsIgnoreCase("") && appl_no.trim().equalsIgnoreCase("")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration / Application No should not be Blank !!"));
                return;
            }

        } else if (isIsprintedRc()) {
            if (regn_no.trim().equalsIgnoreCase("") && appl_no.trim().equalsIgnoreCase("")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration / Application No should not be Blank !!"));
                return;
            }

        } else {
            if (regn_no.trim().equalsIgnoreCase("")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration No should not be Blank !!"));
                return;
            }
        }

        try {
            ArrayList<PrintCertificatesDobj> list = null;
            if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_RC || seatDtl.getAction_cd() == TableConstants.TM_ROLE_DEALER_PRINT_RC) {
                boolean newRCPrintAtDealer = tmConfDobj.getTmConfigDealerDobj().isPrintNewRCAtDealer();
                if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_DEALER_PRINT_RC && Util.getEmpCode() != null && tmConfDobj.getTmConfigDealerDobj() != null) {
                    if (!newRCPrintAtDealer) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "You are not authorized to use this action. Please contact to Administrator !!!"));
                        return;
                    }
                    userCd = Long.parseLong(Util.getEmpCode());
                    Map makerAndDealerDetail = OwnerImpl.getDealerDetail(userCd);
                    if (makerAndDealerDetail != null && makerAndDealerDetail.get("dealer_cd") != null) {
                        dealerCd = (String) makerAndDealerDetail.get("dealer_cd");
                    }
                }
                list = PrintDocImpl.isRegnExistForRC(regn_no.trim().toUpperCase(), state_cd, off_cd, seatDtl.getAction_cd(), dealerCd, newRCPrintAtDealer);
                if (list.isEmpty()) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration does not exist / May be already printed or you are not authorized to print RC for this Registration !!"));
                    return;
                }
            } else if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_FC) {
                if ("FORM38".contains(form38And38ARadiobtn)) {
                    list = PrintDocImpl.isRegnExistForFC(regn_no.trim().toUpperCase(), state_cd, off_cd);
                } else if ("FORM38A".contains(form38And38ARadiobtn)) {
                    list = PrintDocImpl.isRegnExistForForm38AFC(regn_no.trim().toUpperCase(), state_cd, off_cd, form38And38ARadiobtn);
                }
            } else if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_PRINTED_RC) {
                list = PrintDocImpl.isRegnExistForPrintedRC(regn_no.trim().toUpperCase(), state_cd, off_cd);
            } else if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_TMP_RC) {
                list = PrintDocImpl.isTempRegnNoExistForRC(regn_no.trim().toUpperCase(), appl_no.trim().toUpperCase(), state_cd, off_cd);
            } else if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_DEALER_PRINT_TMP_RC) {

                Long user_cd = Long.parseLong(Util.getSession().getAttribute("emp_cd").toString());
                if (user_cd != null && !user_cd.equals("")) {
                    Map makerAndDealerDetail = OwnerImpl.getDealerDetail(user_cd);
                    if (makerAndDealerDetail.get("dealer_cd") != null && !makerAndDealerDetail.get("dealer_cd").equals("")) {
                        list = PrintDocImpl.isTempRegnNoExistPrintDocsDealerDetails(regn_no.trim().toUpperCase(), appl_no.trim().toUpperCase(), TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE, makerAndDealerDetail.get("dealer_cd").toString(), state_cd, off_cd);
                    }
                }
            } else if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_REPRINT_TMP_RC) {
                list = PrintDocImpl.isTempRegnExistForPrintedRC(regn_no.trim().toUpperCase(), appl_no.trim().toUpperCase(), state_cd, off_cd);
            } else if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_REPRINT_FC) {
                if ("FORM38".contains(form38And38ARadiobtn)) {
                    list = PrintDocImpl.getDetailsOfRePrint38FC(state_cd, off_cd, regn_no.trim().toUpperCase());
                } else if ("FORM38A".contains(form38And38ARadiobtn)) {
                    list = PrintDocImpl.isRegnExistForForm38AFC(regn_no.trim().toUpperCase(), state_cd, off_cd, form38And38ARadiobtn);
                }
            }
//            else if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_DEALER_PRINT_TMP_RC_AT_RTO) {
//                list = PrintDocImpl.isTempRegnNoExistForPrintDocsDealerDetailsAtRto(regn_no.trim().toUpperCase());
//            }
            if (list != null && list.isEmpty() && seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_RC) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration does not exist / May be already printed or you are not authorized to print RC for this Registration !!"));
                return;
            } else if (list != null && list.isEmpty() && seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_FC) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration does not exist or you are not authorized to print Fitness Certificate for this Registration !!"));
                return;
            } else if (list != null && list.isEmpty() && seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_PRINTED_RC) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration does not exist or you are not authorized to print Printed RC for this Registration !!"));
                return;
            } else if (list != null && list.isEmpty() && (seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_TMP_RC
                    || seatDtl.getAction_cd() == TableConstants.TM_ROLE_DEALER_PRINT_TMP_RC)) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Temporary Registration / Application does not exist or you are not authorized to print Temp RC for this Temporary Registration !!"));
                return;
            } else if (list != null && list.isEmpty() && (seatDtl.getAction_cd() == TableConstants.TM_ROLE_REPRINT_FC)) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration does not exist or you are not authorized to print Fitness Certificate for this Registration !!"));
                return;
            } else {
                this.setListBeans(list);
            }

        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public void rcRadioBtnListener() {
        long userCd = 0l;
        String dealerCd = "";
        if (printCertDobj != null) {
            printCertDobj.clear();
        }
        if (selectedCertDobj != null) {
            selectedCertDobj.clear();
        }
        String radioBtnvalue = rcRadiobtn;
        try {
            if (seatDtl == null) {
                throw new VahanException("Error in Getting RC/FC details,Please try again ");
            }
            if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_RC || seatDtl.getAction_cd() == TableConstants.TM_ROLE_DEALER_PRINT_RC) {
                boolean newRCPrintAtDealer = newRCPrintAtDealer = tmConfDobj.getTmConfigDealerDobj().isPrintNewRCAtDealer();
                if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_DEALER_PRINT_RC && Util.getEmpCode() != null) {
                    if (!newRCPrintAtDealer) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "You are not authorized to use this action. Please contact to Administrator !!!"));
                        return;
                    }
                    userCd = Long.parseLong(Util.getEmpCode());
                    Map makerAndDealerDetail = OwnerImpl.getDealerDetail(userCd);
                    if (makerAndDealerDetail != null && makerAndDealerDetail.get("dealer_cd") != null) {
                        dealerCd = (String) makerAndDealerDetail.get("dealer_cd");
                    }
                }
                if (getVmSmartCardHsrpDobj() == null) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Data from VM_SMART_CARD_HSRP is Failed to Load"));
                    return;
                }
                if (radioBtnvalue.equalsIgnoreCase("PENRC")) {
                    setPrintRcbyRegnNo(false);
                    setIsprintedRc(false);
                    setProvRcButtonVisibility(true);
                    setPrintTempRcbyRegnNo(false);
                    this.regn_no = "";
                    this.appl_no = "";
                    ArrayList<PrintCertificatesDobj> list = PrintDocImpl.getPendingRCPrintDocsDetails(state_cd, off_cd, seatDtl.getAction_cd(), dealerCd, newRCPrintAtDealer);
                    if (list.isEmpty()) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "There is no record found !!"));
                        return;
                    } else {
                        this.setListBeans(list);
                    }
                } else if (radioBtnvalue.equalsIgnoreCase("PRTRC")) {
                    setPrintRcbyRegnNo(false);
                    setIsprintedRc(true);
                    setProvRcButtonVisibility(false);
                    setPrintTempRcbyRegnNo(false);
                    this.regn_no = "";
                    this.appl_no = "";
                    ArrayList<PrintCertificatesDobj> list = PrintDocImpl.getRCTodayPrintedDocsDetails(state_cd, off_cd, seatDtl.getAction_cd(), dealerCd, userCatg, newRCPrintAtDealer);
                    if (list.isEmpty()) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "There is no printed RC as on Today !!"));
                        return;
                    } else {
                        this.setListBeans(list);
                    }

                } else if (radioBtnvalue.equalsIgnoreCase("REGNNORC")) {
                    setProvRcButtonVisibility(true);
                    setPrintRcbyRegnNo(true);
                    setIsprintedRc(false);
                    setPrintTempRcbyRegnNo(false);
                }
            } else if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_FC) {
                if (radioBtnvalue.equalsIgnoreCase("PENFC")) {
                    setPrintRcbyRegnNo(false);
                    this.regn_no = "";
                    this.appl_no = "";
                    ArrayList<PrintCertificatesDobj> list = PrintDocImpl.getPurCdPrintDocsDetailsFC(state_cd, off_cd);
                    if (list.isEmpty()) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "There is no record found !!"));
                        return;
                    } else {
                        this.setListBeans(list);
                    }
                    setIsprintedRc(false);
                    setIspendingFC(true);
                    setIsprintedFC(false);
                    setProvRcButtonVisibility(false);
                    setPrintTempRcbyRegnNo(false);
                } else if (radioBtnvalue.equalsIgnoreCase("PRTFC")) {
                    setPrintRcbyRegnNo(false);
                    this.regn_no = "";
                    this.appl_no = "";
                    ArrayList<PrintCertificatesDobj> list = PrintDocImpl.getFCTodayPrintedDocsDetails(state_cd, off_cd);
                    if (list.isEmpty()) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "There is no printed Fitness Certificate as on Today !!"));
                        return;
                    } else {
                        this.setListBeans(list);
                    }
                    setIsprintedRc(false);
                    setIspendingFC(false);
                    setIsprintedFC(true);
                    setProvRcButtonVisibility(false);
                    setPrintTempRcbyRegnNo(false);
                } else if (radioBtnvalue.equalsIgnoreCase("REGNNOFC")) {
                    setProvRcButtonVisibility(false);
                    setPrintRcbyRegnNo(true);
                    setIsprintedFC(false);
                    setPrintTempRcbyRegnNo(false);

                }
            } else if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_TMP_RC) {
                if (radioBtnvalue.equalsIgnoreCase("PENDINGTEMPRC")) {
                    setIsprintedRc(false);
                    setIspendingFC(false);
                    setIsprintedFC(false);
                    setProvRcButtonVisibility(false);
                    setPrintRcbyRegnNo(false);
                    setPrintTempRcbyRegnNo(false);
                    this.regn_no = "";
                    this.appl_no = "";
                    ArrayList<PrintCertificatesDobj> list = PrintDocImpl.getTempRCPendingDocsDetails(state_cd, off_cd);
                    if (list.isEmpty()) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "There is no record found !!"));
                        return;
                    } else {
                        this.setListBeans(list);
                    }
                } else if (radioBtnvalue.equalsIgnoreCase("PRINTEDTEMPRC")) {
                    setPrintRcbyRegnNo(false);
                    setIsprintedRc(true);
                    setIspendingFC(false);
                    setIsprintedFC(false);
                    setProvRcButtonVisibility(false);
                    setPrintTempRcbyRegnNo(false);
                    this.regn_no = "";
                    this.appl_no = "";
                    ArrayList<PrintCertificatesDobj> list = PrintDocImpl.getTempRCTodayPrintedDocsDetails(state_cd, off_cd);
                    if (list.isEmpty()) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "There is no printed RC as on Today !!"));
                        return;
                    } else {
                        this.setListBeans(list);
                    }

                } else if (radioBtnvalue.equalsIgnoreCase("TEMPRC")) {
                    setProvRcButtonVisibility(false);
                    setPrintRcbyRegnNo(true);
                    setIsprintedRc(false);
                    setIspendingFC(false);
                    setIsprintedFC(false);
                    setPrintTempRcbyRegnNo(true);
                }

            } else if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_DEALER_PRINT_TMP_RC) {
                if (radioBtnvalue.equalsIgnoreCase("PENDINGTEMPRC")) {
                    setIsprintedRc(false);
                    setIspendingFC(false);
                    setIsprintedFC(false);
                    setProvRcButtonVisibility(false);
                    setPrintRcbyRegnNo(false);
                    setPrintTempRcbyRegnNo(false);
                    this.regn_no = "";
                    this.appl_no = "";
                    ArrayList<PrintCertificatesDobj> list = new ArrayList<PrintCertificatesDobj>();
                    //ArrayList<PrintCertificatesDobj> list = PrintDocImpl.getPurCdPrintDocsDetails();
                    Long user_cd = Long.parseLong(Util.getSession().getAttribute("emp_cd").toString());
                    if (user_cd != null && !user_cd.equals("")) {
                        Map makerAndDealerDetail = OwnerImpl.getDealerDetail(user_cd);
                        if (makerAndDealerDetail.get("dealer_cd") != null && !makerAndDealerDetail.get("dealer_cd").equals("")) {
                            list = PrintDocImpl.getTempPrintDocsDealerDetails(TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE, makerAndDealerDetail.get("dealer_cd").toString(), state_cd, off_cd);
                        }
                    }
                    if (list.isEmpty()) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "There is no record found !!"));
                        return;
                    } else {
                        this.setListBeans(list);
                    }
                } else if (radioBtnvalue.equalsIgnoreCase("PRINTEDTEMPRC")) {
                    setPrintRcbyRegnNo(false);
                    setIsprintedRc(true);
                    setIspendingFC(false);
                    setIsprintedFC(false);
                    setProvRcButtonVisibility(false);
                    setPrintTempRcbyRegnNo(false);
                    this.regn_no = "";
                    this.appl_no = "";
                    ArrayList<PrintCertificatesDobj> list = new ArrayList<PrintCertificatesDobj>();
                    Long user_cd = Long.parseLong(Util.getSession().getAttribute("emp_cd").toString());
                    if (user_cd != null && !user_cd.equals("")) {
                        Map makerAndDealerDetail = OwnerImpl.getDealerDetail(user_cd);
                        if (makerAndDealerDetail.get("dealer_cd") != null && !makerAndDealerDetail.get("dealer_cd").equals("")) {
                            list = PrintDocImpl.getDealerTempRCTodayPrintedDocsDetails(TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE, makerAndDealerDetail.get("dealer_cd").toString(), state_cd, off_cd);
                        }
                    }
                    if (list.isEmpty()) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "There is no printed RC as on Today !!"));
                        return;
                    } else {
                        this.setListBeans(list);
                    }

                } else if (radioBtnvalue.equalsIgnoreCase("TEMPRC")) {
                    setProvRcButtonVisibility(false);
                    setPrintRcbyRegnNo(true);
                    setIsprintedRc(false);
                    setIspendingFC(false);
                    setIsprintedFC(false);
                    setPrintTempRcbyRegnNo(true);
                }

            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", ve.getMessage()));
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Something went wrong, Please try again."));
            return;
        }
    }

    public void form38And38ARadBtnListener() {
        printCertDobj.clear();
        if (selectedCertDobj != null) {
            selectedCertDobj.clear();
        }
        this.regn_no = null;
        if (form38And38ARadiobtn.equalsIgnoreCase("FORM38")) {
            setProvRcButtonVisibility(false);
            setPrintRcbyRegnNo(true);
            setIsprintedFC(false);
            setPrintTempRcbyRegnNo(false);
            setIsform38(true);
            setPrintFc(true);
            rcRadiobtn = "REGNNOFC";
            setBtn_print_label("Print Form-38");

        } else if (form38And38ARadiobtn.equalsIgnoreCase("FORM38A")) {
            setProvRcButtonVisibility(false);
            setPrintRcbyRegnNo(true);
            setIsprintedFC(false);
            setPrintTempRcbyRegnNo(false);
            setIsform38(false);
            setPrintFc(true);
            setBtn_print_label("Print Form-38A");
        }

    }

    public void confirmprintCertificate() {
        if (seatDtl.getAction_cd() != TableConstants.TM_ROLE_PRINT_FC
                && seatDtl.getAction_cd() != TableConstants.TM_ROLE_REPRINT_TMP_RC
                && seatDtl.getAction_cd() != TableConstants.TM_ROLE_PRINT_TMP_RC
                && seatDtl.getAction_cd() != TableConstants.TM_ROLE_DEALER_PRINT_TMP_RC
                && seatDtl.getAction_cd() != TableConstants.TM_ROLE_REPRINT_FC
                && seatDtl.getAction_cd() != TableConstants.TM_ROLE_DEALER_PRINT_RC
                && getVmSmartCardHsrpDobj() == null) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Data from VM_SMART_CARD_HSRP is Failed to Load"));
            return;
        }
        //RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().executeScript("PF('printCertificate').show()");
    }

    public String printCertificate() {

        int vhType = 0;
        String returnurl = "";
        boolean isOldHSRPTrue = false;
        if (selectedCertDobj == null || selectedCertDobj.size() < 1) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Please Select Atleast One Registration No For Printing !!"));
            return "";
        }
        if (selectedCertDobj.size() > 8) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Increase Bulk Printing Limit, Maximum 8 RC Allowed !!"));
            return "";
        }
        try {
            SeatAllotedDetails seatDtl = (SeatAllotedDetails) Util.getSession().getAttribute("SelectedSeat");
            boolean HSRPUploadedSuccessfully = false;
            if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_RC || seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_FC) {
                TmConfigurationFitnessDobj tmConfigFitnessDobj = new FitnessImpl().getFitnessConfiguration(Util.getUserStateCode());
                if ((seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_RC && tmConfDobj.isRc_after_hsrp()) || (seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_FC && tmConfigFitnessDobj != null && tmConfigFitnessDobj.isFcAfterHSRP())) {
                    String PrintType = null;
                    if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_FC) {
                        PrintType = "FC";
                    } else {
                        PrintType = "RC";
                    }
                    if (PrintDocImpl.checkForHsrp(state_cd, off_cd)) {
                        isOldHSRPTrue = PrintDocImpl.checkForOldHsrp(state_cd, off_cd);
                        for (int i = 0; i < selectedCertDobj.size(); i++) {
                            OwnerImpl ownerImpl = new OwnerImpl();
                            Owner_dobj ownerDobj = ownerImpl.set_Owner_appl_db_to_dobj(selectedCertDobj.get(i).getRegno().trim(), null, "", 1);
                            if (ownerDobj != null) {
                                vhType = ownerDobj.getVehType();
                            }
                            ApplicationInwardImpl applicationInwardImpl = new ApplicationInwardImpl();
                            boolean taxPaidOrClear = applicationInwardImpl.taxPaidOrClearedStatus(selectedCertDobj.get(i).getRegno().trim(), tmConfDobj, ownerDobj, TableConstants.TM_ROAD_TAX);
                            if (!taxPaidOrClear && "DL,UP".contains(Util.getUserStateCode()) && vhType == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
                                taxPaidOrClear = true;
                            }
                            if (!taxPaidOrClear && ownerDobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)
                                    && Util.getUserStateCode().equalsIgnoreCase("DL")) {
                                taxPaidOrClear = applicationInwardImpl.taxPaidOrClearedStatusOnVehType(selectedCertDobj.get(i).getRegno().trim());
                            }
                            if (!taxPaidOrClear) {
                                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "You can not print the " + PrintType + " for Registration No. " + selectedCertDobj.get(i).getRegno().toUpperCase().trim() + " as Tax is not clear!!"));
                                return "";
                            }
                        }
                        if (isOldHSRPTrue && "WB".contains(state_cd)) {
                            List<String> regnList = ServerUtil.isHSRPUploaded(selectedCertDobj, rcRadiobtn);
                            if (!regnList.isEmpty()) {
                                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "You can not print the " + PrintType + " for Registration No. " + regnList.toString() + " as HSRP details are not uploaded!!"));
                                return "";
                            } else {
                                HSRPUploadedSuccessfully = true;
                            }
                        } else if (!isOldHSRPTrue && "WB".contains(state_cd)) {
                            List<PrintCertificatesDobj> selectedRegnList = PrintDocImpl.getNewVehicleFeeDtlsForOtherStateVehicle(selectedCertDobj);
                            List<String> regnList = new ArrayList<>();
                            if (!selectedRegnList.isEmpty()) {
                                regnList = ServerUtil.isHSRPUploaded(selectedRegnList, rcRadiobtn);
                            }
                            if (!regnList.isEmpty()) {
                                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "You can not print the " + PrintType + " for Registration No. " + regnList.toString() + " as HSRP details are not uploaded!!"));
                                return "";
                            } else {
                                HSRPUploadedSuccessfully = true;
                            }
                        } else if (!"WB".contains(state_cd)) {
                            List<String> regnList = ServerUtil.isHSRPUploaded(selectedCertDobj, rcRadiobtn);
                            if (!regnList.isEmpty()) {
                                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "You can not print the " + PrintType + " for Registration No. " + regnList.toString() + " as HSRP details are not uploaded!!"));
                                return "";
                            } else {
                                HSRPUploadedSuccessfully = true;
                            }
                        }
                    }
                }
            }

            if (!HSRPUploadedSuccessfully && (seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_RC || seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_PRINTED_RC || seatDtl.getAction_cd() == TableConstants.TM_ROLE_DEALER_PRINT_RC)) {
                for (int i = 0; i < selectedCertDobj.size(); i++) {
                    if (TableConstants.CHECK_MANU_MONTH_YEAR_FOR_HSRP <= selectedCertDobj.get(i).getMfgYearMonthYYYYMM() && (selectedCertDobj.get(i).getPurCd() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || selectedCertDobj.get(i).getPurCd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE)) {
                        List<String> regnList = ServerUtil.isHSRPUploaded(selectedCertDobj, rcRadiobtn);
                        if (!regnList.isEmpty()) {
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "RC can't be printed for Registration No. " + regnList.toString() + " as HSRP details are not uploaded!!"));
                            return "";
                        }
                    }
                }
            }
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("rcRadiobtn", rcRadiobtn);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("listPrintRc", selectedCertDobj);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("paperrc", getPaper_rc());
            if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_TMP_RC || seatDtl.getAction_cd() == TableConstants.TM_ROLE_REPRINT_TMP_RC || seatDtl.getAction_cd() == TableConstants.TM_ROLE_DEALER_PRINT_TMP_RC) {
                returnurl = "TempRCReport";
            } else if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_FC || seatDtl.getAction_cd() == TableConstants.TM_ROLE_REPRINT_FC) {
                returnurl = "FCReport";
            } else if (seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_RC || seatDtl.getAction_cd() == TableConstants.TM_ROLE_PRINT_PRINTED_RC || seatDtl.getAction_cd() == TableConstants.TM_ROLE_DEALER_PRINT_RC) {

                if (getPaper_rc().equals(TableConstants.VM_PAPER_RC_CD)) {
                    returnurl = "NewRCReport";
                } else {
                    returnurl = "SmartCardRCReport";
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return returnurl;
    }

    public String printProvisionalRC(PrintCertificatesDobj reportDobj) {
        try {
            String application_no = reportDobj.getAppl_no();
            String regn_no = reportDobj.getRegno();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("applNo", application_no);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("regnNo", regn_no);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("category", "registeredVehicles");
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("reportEntry", "reportFormat");
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("printType", "provisionalRCAtRtoLevel");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return "OwnerDisclaimerReport";
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public int getAction_cd() {
        return action_cd;
    }

    public void setAction_cd(int action_cd) {
        this.action_cd = action_cd;
    }

    /**
     * @return the indexValue
     */
    public String getIndexValue() {
        return indexValue;
    }

    /**
     * @param indexValue the indexValue to set
     */
    public void setIndexValue(String indexValue) {
        this.indexValue = indexValue;
    }

    /**
     * @return the printCertDobj
     */
    public List<PrintCertificatesDobj> getPrintCertDobj() {
        return printCertDobj;
    }

    /**
     * @param printCertDobj the printCertDobj to set
     */
    public void setPrintCertDobj(List<PrintCertificatesDobj> printCertDobj) {
        this.printCertDobj = printCertDobj;
    }

    /**
     * @return the filteredSeat
     */
    public List<PrintCertificatesBean> getFilteredSeat() {
        return filteredSeat;
    }

    /**
     * @param filteredSeat the filteredSeat to set
     */
    public void setFilteredSeat(List<PrintCertificatesBean> filteredSeat) {
        this.filteredSeat = filteredSeat;
    }

    /**
     * @return the btn_print_label
     */
    public String getBtn_print_label() {
        return btn_print_label;
    }

    /**
     * @param btn_print_label the btn_print_label to set
     */
    public void setBtn_print_label(String btn_print_label) {
        this.btn_print_label = btn_print_label;
    }

    /**
     * @return the main_header_label
     */
    public String getMain_header_label() {
        return main_header_label;
    }

    /**
     * @param main_header_label the main_header_label to set
     */
    public void setMain_header_label(String main_header_label) {
        this.main_header_label = main_header_label;
    }

    /**
     * @return the paper_rc
     */
    public String getPaper_rc() {
        return paper_rc;
    }

    /**
     * @param paper_rc the paper_rc to set
     */
    public void setPaper_rc(String paper_rc) {
        this.paper_rc = paper_rc;
    }

    /**
     * @return the selectedCertDobj
     */
    public List<PrintCertificatesDobj> getSelectedCertDobj() {
        return selectedCertDobj;
    }

    /**
     * @param selectedCertDobj the selectedCertDobj to set
     */
    public void setSelectedCertDobj(List<PrintCertificatesDobj> selectedCertDobj) {
        this.selectedCertDobj = selectedCertDobj;
    }

    /**
     * @return the tmConfDobj
     */
    public TmConfigurationDobj getTmConfDobj() {
        return tmConfDobj;
    }

    /**
     * @param tmConfDobj the tmConfDobj to set
     */
    public void setTmConfDobj(TmConfigurationDobj tmConfDobj) {
        this.tmConfDobj = tmConfDobj;
    }

    /**
     * @return the provRcButtonVisibility
     */
    public boolean isProvRcButtonVisibility() {
        return provRcButtonVisibility;
    }

    /**
     * @param provRcButtonVisibility the provRcButtonVisibility to set
     */
    public void setProvRcButtonVisibility(boolean provRcButtonVisibility) {
        this.provRcButtonVisibility = provRcButtonVisibility;
    }

    /**
     * @return the printRc
     */
    public boolean isPrintRc() {
        return printRc;
    }

    /**
     * @param printRc the printRc to set
     */
    public void setPrintRc(boolean printRc) {
        this.printRc = printRc;
    }

    /**
     * @return the rcRadiobtn
     */
    public String getRcRadiobtn() {
        return rcRadiobtn;
    }

    /**
     * @param rcRadiobtn the rcRadiobtn to set
     */
    public void setRcRadiobtn(String rcRadiobtn) {
        this.rcRadiobtn = rcRadiobtn;
    }

    /**
     * @return the isprintedRc
     */
    public boolean isIsprintedRc() {
        return isprintedRc;
    }

    /**
     * @param isprintedRc the isprintedRc to set
     */
    public void setIsprintedRc(boolean isprintedRc) {
        this.isprintedRc = isprintedRc;
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
     * @return the printRcbyRegnNo
     */
    public boolean isPrintRcbyRegnNo() {
        return printRcbyRegnNo;
    }

    /**
     * @param printRcbyRegnNo the printRcbyRegnNo to set
     */
    public void setPrintRcbyRegnNo(boolean printRcbyRegnNo) {
        this.printRcbyRegnNo = printRcbyRegnNo;
    }

    /**
     * @return the printFc
     */
    public boolean isPrintFc() {
        return printFc;
    }

    /**
     * @param printFc the printFc to set
     */
    public void setPrintFc(boolean printFc) {
        this.printFc = printFc;
    }

    /**
     * @return the selectPrintColumnHeader
     */
    public String getSelectPrintColumnHeader() {
        return selectPrintColumnHeader;
    }

    /**
     * @param selectPrintColumnHeader the selectPrintColumnHeader to set
     */
    public void setSelectPrintColumnHeader(String selectPrintColumnHeader) {
        this.selectPrintColumnHeader = selectPrintColumnHeader;
    }

    /**
     * @return the selectDataTableHeader
     */
    public String getSelectDataTableHeader() {
        return selectDataTableHeader;
    }

    /**
     * @param selectDataTableHeader the selectDataTableHeader to set
     */
    public void setSelectDataTableHeader(String selectDataTableHeader) {
        this.selectDataTableHeader = selectDataTableHeader;
    }

    /**
     * @return the ispendingFC
     */
    public boolean isIspendingFC() {
        return ispendingFC;
    }

    /**
     * @param ispendingFC the ispendingFC to set
     */
    public void setIspendingFC(boolean ispendingFC) {
        this.ispendingFC = ispendingFC;
    }

    /**
     * @return the isprintedFC
     */
    public boolean isIsprintedFC() {
        return isprintedFC;
    }

    /**
     * @param isprintedFC the isprintedFC to set
     */
    public void setIsprintedFC(boolean isprintedFC) {
        this.isprintedFC = isprintedFC;
    }

    /**
     * @return the vmSmartCardHsrpDobj
     */
    public VmSmartCardHsrpDobj getVmSmartCardHsrpDobj() {
        return vmSmartCardHsrpDobj;
    }

    /**
     * @param vmSmartCardHsrpDobj the vmSmartCardHsrpDobj to set
     */
    public void setVmSmartCardHsrpDobj(VmSmartCardHsrpDobj vmSmartCardHsrpDobj) {
        this.vmSmartCardHsrpDobj = vmSmartCardHsrpDobj;
    }

    /**
     * @return the printTempRC
     */
    public boolean isPrintTempRC() {
        return printTempRC;
    }

    /**
     * @param printTempRC the printTempRC to set
     */
    public void setPrintTempRC(boolean printTempRC) {
        this.printTempRC = printTempRC;
    }

    /**
     * @return the printTempRcbyRegnNo
     */
    public boolean isPrintTempRcbyRegnNo() {
        return printTempRcbyRegnNo;
    }

    /**
     * @param printTempRcbyRegnNo the printTempRcbyRegnNo to set
     */
    public void setPrintTempRcbyRegnNo(boolean printTempRcbyRegnNo) {
        this.printTempRcbyRegnNo = printTempRcbyRegnNo;
    }

    /**
     * @return the labelRegnNotxtField
     */
    public String getLabelRegnNotxtField() {
        return labelRegnNotxtField;
    }

    /**
     * @param labelRegnNotxtField the labelRegnNotxtField to set
     */
    public void setLabelRegnNotxtField(String labelRegnNotxtField) {
        this.labelRegnNotxtField = labelRegnNotxtField;
    }

    /**
     * @return the form38And38ARadiobtn
     */
    public String getForm38And38ARadiobtn() {
        return form38And38ARadiobtn;
    }

    /**
     * @param form38And38ARadiobtn the form38And38ARadiobtn to set
     */
    public void setForm38And38ARadiobtn(String form38And38ARadiobtn) {
        this.form38And38ARadiobtn = form38And38ARadiobtn;
    }

    /**
     * @return the isform38
     */
    public boolean isIsform38() {
        return isform38;
    }

    /**
     * @param isform38 the isform38 to set
     */
    public void setIsform38(boolean isform38) {
        this.isform38 = isform38;
    }

    /**
     * @return the isRCLabel
     */
    public boolean isIsRCLabel() {
        return isRCLabel;
    }

    /**
     * @param isRCLabel the isRCLabel to set
     */
    public void setIsRCLabel(boolean isRCLabel) {
        this.isRCLabel = isRCLabel;
    }

    /**
     * @return the renderSearchBy
     */
    public boolean isRenderSearchBy() {
        return renderSearchBy;
    }

    /**
     * @param renderSearchBy the renderSearchBy to set
     */
    public void setRenderSearchBy(boolean renderSearchBy) {
        this.renderSearchBy = renderSearchBy;
    }

    /**
     * @return the userCatg
     */
    public String getUserCatg() {
        return userCatg;
    }

    /**
     * @param userCatg the userCatg to set
     */
    public void setUserCatg(String userCatg) {
        this.userCatg = userCatg;
    }

    /**
     * @return the renderHsrpDialog
     */
    public boolean isRenderHsrpDialog() {
        return renderHsrpDialog;
    }

    /**
     * @param renderHsrpDialog the renderHsrpDialog to set
     */
    public void setRenderHsrpDialog(boolean renderHsrpDialog) {
        this.renderHsrpDialog = renderHsrpDialog;
    }
}
