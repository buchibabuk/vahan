/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import dao.UserDAO;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.ApplicationConfiguration;
import nic.rto.vahan.common.MessagesDobj;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.dashBoard.DashboardDetails;
import nic.vahan.dashBoard.PendingApplDashBoardImpl;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import static nic.vahan.form.bean.Home_Bean.checkOfficeTiming;
import nic.vahan.form.dobj.BlackListedVehicleDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.TmConfigurationDMS;
import nic.vahan.form.dobj.TmConfigurationSwappingDobj;
import nic.vahan.form.dobj.permit.PermitDetailDobj;
import nic.vahan.form.dobj.tradecert.TradeCertDetailsDobj;
import nic.vahan.form.impl.ApplicationInwardImpl;
import nic.vahan.form.impl.BlackListedVehicleImpl;
import nic.vahan.form.impl.Home_Impl;
import nic.vahan.form.impl.InsImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.SeatAllotedDetails;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.admin.UserMsgImpl;
import nic.vahan.form.impl.permit.PermitDetailImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import nic.vahan.services.DmsDocCheckUtils;
import nic.vahan.services.VTDocumentModel;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author nic5912
 */
@ManagedBean(name = "home_bean")
@ViewScoped
public class Home_Bean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(Home_Bean.class);
    private List<SeatAllotedDetails> allSeat = null;
    private SeatAllotedDetails selectedSeat;
    private int allotedRoleCode;
    private int allotedOffCode;
    private int allotedActionCode;
    private Map<String, Object> allotedRoleCodeList;
    private Map<String, Object> allotedOffCodeList;
    private String offDescr = "No Selection";
    private String roleDescr = "No Selection";
    private String actionDescr = "No Selection";
    private SeatAllotedDetails selectedSeatAction;
    private List<SeatAllotedDetails> allseatWork = null;
    private List<SeatAllotedDetails> filteredSeat = null;
    private Map<String, Object> allotedActionCodeList;
    String counter_id = "";
    private boolean cashCounterOpen = false;
    private boolean cashCounterClose = false;
    private boolean pendingWorkPanel = true;
    private String firstPartApplNo;
    private String secondPartApplNo = "";
    private String regnNo = "";
    private String oldApplNo = "";
    private String searchByValue = "applNo";
    private String availableRegnNoList;
    private boolean availableRegnNO = false;
    private String smartCard = "";
    private String hsrp = "";
    private String tradeCertificateList = "";
    private boolean tradeCertPanel = false;
    private String dealerBlockMsg = "";
    private boolean dealerBlockPanel = false;
    private Date fromDate;
    private Date uptoDate = new Date();
    private boolean prevDateButton;
    private boolean nextDateButton;
    private String pullBackReason;
    private Map<String, Integer> purCodeList = null;
    private List<String> selectedPurCdForPullBack;
    private String message;
    private String dealerCd;
    private String empCodeLoggedIn = null;
    private String stateCodeSelected = null;
    private int pendingWorkDaysPeriod = 2;
    private boolean unreadMessages = false;
    private long noOfUnreadMessages = 0;
    private String unReadMessagesNotif = null;
    private boolean userCatgMessages = false;
    private long noOfUnreadUserCatgMessages = 0;
    private long noOfUserCatgMessages = 0;
    private long noOfReadUserCatgMessages = 0;
    private String userCatgMessagesNotif = null;
    private Date currentDate = new Date();
    private Date vow4Date = null;
    private boolean calPanel;
    private boolean renderFaceApplRadioButton = false;
    private boolean renderedDashBoardBtn = false;

    public Home_Bean() {
        try {
            if (Util.getSession() != null && Util.getSession().getAttribute("onlinePayment") != null) {
                FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "homePage");
                return;
            }
            empCodeLoggedIn = Util.getEmpCode();
            stateCodeSelected = Util.getUserStateCode();
            if (empCodeLoggedIn == null || stateCodeSelected == null) {
                return;
            }
            String selectedApplNo = "";
            SeatAllotedDetails selectedWork = Util.getSelectedSeat();
            if (selectedWork != null && selectedWork.getAppl_no() != null && !selectedWork.getAppl_no().equals("")) {
                selectedApplNo = selectedWork.getAppl_no();
            }
            allotedOffCodeList = fillAllotedOfficeCodeDescr(empCodeLoggedIn);
            String user_catg = "";
            for (Map.Entry<String, Object> offcdEntry : allotedOffCodeList.entrySet()) {
                allotedActionCodeList = Home_Impl.getAllotedActionCodeDescr((int) offcdEntry.getValue(), stateCodeSelected, empCodeLoggedIn);
                user_catg = Util.getUserCategory();
                if (user_catg == null) {
                    user_catg = "";
                }
                if (user_catg.equals(TableConstants.USER_CATG_PORTAL_ADMIN)
                        || user_catg.equals(TableConstants.USER_CATG_STATE_ADMIN)
                        || user_catg.equals(TableConstants.USER_CATG_OFFICE_ADMIN)
                        || user_catg.equals(TableConstants.USER_CATG_DEALER_ADMIN)
                        || user_catg.equals(TableConstants.USER_CATG_FITNESS_CENTER_ADMIN)) {
                    allotedActionCodeList.put("CREATE/MODIFY USER", TableConstants.TM_ROLE_CREATE_USER);
                    allotedActionCodeList.put("ASSIGN ROLE/ACTION", TableConstants.TM_ROLE_ASSIGN_ROLE);
                }
                if (user_catg.equals(TableConstants.USER_CATG_OFF_STAFF)) {
                    allotedOffCode = (int) offcdEntry.getValue();
                }
                if (user_catg.equals(TableConstants.USER_CATG_STATE_HSRP)) {
                    allotedOffCodeList.put("All Office", 0);
                    allotedActionCodeList.put("HSRP-VENDOR-FLAT-FILE", TableConstants.HSRP_VENDOR_FLAT_FILE);
                    allotedActionCodeList.put("HSRP-VENDOR-UPLOAD-FILE", TableConstants.HSRP_VENDOR_UPLOAD_FILE);
                }
                if (user_catg.equals(TableConstants.USER_CATG_OFF_STAFF) || user_catg.equals(TableConstants.USER_CATG_OFFICE_ADMIN) || user_catg.equals(TableConstants.USER_CATG_STATE_ADMIN)) {
                    renderedDashBoardBtn = true;
                }
                for (Map.Entry<String, Object> actionCdEntry : allotedActionCodeList.entrySet()) {
                    offDescr = offcdEntry.getKey();
                    allotedOffCode = (int) offcdEntry.getValue();
                    actionDescr = actionCdEntry.getKey();
                    allotedActionCode = (int) actionCdEntry.getValue();
                    break;
                }
                break;
            }
            if (empCodeLoggedIn != null && !empCodeLoggedIn.isEmpty() && empCodeLoggedIn.length() > 0 && ServerUtil.validateDealerUserForAllOffice(Long.parseLong(empCodeLoggedIn))) {
                int allotedOfficeCodeDealer = 0;
                if (Util.getSelectedSeat() != null && Util.getSelectedSeat().getOff_cd() != 0) {
                    allotedOffCode = Util.getSelectedSeat().getOff_cd();
                    allotedActionCode = Util.getSelectedSeat().getAction_cd();
                    Map<String, Object> allotedOffCodeListTemp = Home_Impl.getAllotedOfficeCodeDescr(false);
                    for (Map.Entry<String, Object> offcdEntry : allotedOffCodeListTemp.entrySet()) {
                        allotedOfficeCodeDealer = (int) offcdEntry.getValue();
                    }
                    allotedActionCodeList = Home_Impl.getAllotedActionCodeDescr(allotedOfficeCodeDealer, stateCodeSelected, empCodeLoggedIn);
                }
            }

            Util.getSession().setAttribute("selected_off_cd", String.valueOf(allotedOffCode));
            Util.getSession().setAttribute("selected_role_cd", String.valueOf(allotedActionCode));
            Util.getSession().setAttribute("selected_cntr_id", counter_id);
            selectedSeat = new SeatAllotedDetails();
            selectedSeat.setAction_cd(allotedActionCode);
            selectedSeat.setOff_cd(allotedOffCode);
            selectedSeat.setCntr_id(counter_id);
            //Util.getSession().setAttribute("SelectedSeat", selectedSeat);
            if (user_catg.equals(TableConstants.USER_CATG_OFFICE_ADMIN)
                    || user_catg.equals(TableConstants.USER_CATG_OFF_STAFF)) {
                List list = Home_Impl.getCashCounterDetails();
                if (list.size() > 0) {
                    if (((boolean) list.get(1)) == true) {
                        setCashCounterClose(true);
                    } else {
                        setCashCounterOpen(true);
                    }
                } else {
                    setCashCounterClose(true);
                }
            }
            if (selectedApplNo.length() == 0) {
                java.util.Calendar cal = java.util.Calendar.getInstance();
                String monthYear = String.valueOf(cal.get(java.util.Calendar.YEAR)).substring(2, 4) + String.format("%02d", cal.get(java.util.Calendar.MONTH) + 1);
                firstPartApplNo = Util.getUserStateCode() + monthYear;
            } else {
                firstPartApplNo = selectedApplNo.substring(0, 6);
                secondPartApplNo = selectedApplNo.substring(6);
                this.getPendingWorkAndActionList();
            }
            if (user_catg.equals(TableConstants.USER_CATG_SMARTCARD)
                    || user_catg.equals(TableConstants.USER_CATG_STATE_ADMIN)
                    || user_catg.equals(TableConstants.USER_CATG_STATE_SUPER_ADMIN)
                    || user_catg.equals(TableConstants.USER_CATG_FITNESS_CENTER_ADMIN)
                    || user_catg.equals(TableConstants.USER_CATG_STATE_HSRP)
                    || user_catg.equals(TableConstants.USER_CATG_STATE_SMARTCARD)) {
                setPendingWorkPanel(false);
            }

            if (user_catg.equals(TableConstants.USER_CATG_HSRP)) {
                setPendingWorkPanel(true);
            }
            // forstateadmin
            if (user_catg.equals(TableConstants.USER_CATG_STATE_ADMIN) && Util.getTmConfiguration() != null && Util.getTmConfiguration().isMdfVehDtlsApproveByStateAdmin()) {
                setPendingWorkPanel(true);
            }
            //end

            if (user_catg.equals(TableConstants.USER_CATG_DEALER) || user_catg.equals(TableConstants.USER_CATG_OFF_STAFF)) {
                if (!"OR".equals(stateCodeSelected)) {
                    availableRegnNoList = Home_Impl.getAvailableRegnNoList(allotedOffCode);
                    availableRegnNO = true;
                }
            }
            if (user_catg.equals(TableConstants.USER_CATG_DEALER)) {
                tradeCertPanel = true;
                if (empCodeLoggedIn != null && !empCodeLoggedIn.isEmpty() && empCodeLoggedIn.length() > 0) {
                    Long user_cd = Long.parseLong(empCodeLoggedIn);
                    Map makerAndDealerDetail = OwnerImpl.getDealerDetail(user_cd);
                    if (makerAndDealerDetail.get("dealer_cd") != null && !makerAndDealerDetail.get("dealer_cd").equals("")) {
                        dealerCd = makerAndDealerDetail.get("dealer_cd").toString();
                        String dealerStatusMsg = Home_Impl.getDealerBlockUnBlockStatus(dealerCd);
                        if (dealerStatusMsg != null && !dealerStatusMsg.isEmpty()) {
                            dealerBlockPanel = true;
                            pendingWorkPanel = false;
                            dealerBlockMsg = "Your user-id has been blocked by the respective registering authority due to " + dealerStatusMsg + ". However, you can work only in limited actions.";
                        }
                        List<TradeCertDetailsDobj> tradeCertificate = Home_Impl.getTradeCertificateDetails(dealerCd);
                        if (!tradeCertificate.isEmpty()) {
                            for (TradeCertDetailsDobj tradeCert : tradeCertificate) {
                                if (tradeCert.getMessage() == null && tradeCert.getValidUpto() != null) {
                                    DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
                                    String validUpto = format.format(tradeCert.getValidUpto());
                                    tradeCertificateList += " Trade Certificate No " + tradeCert.getTradeCertNo() + " issued by " + ServerUtil.getOfficeName(allotedOffCode, Util.getUserStateCode()) + " valid upto " + validUpto + " for vehicle category " + ServerUtil.getCatgDesc(tradeCert.getVehCatg()) + "<span style='color:blue; font-size:18px;'> | </span>";
                                } else {
                                    tradeCertificateList += "<span style='color:red;'>" + tradeCert.getMessage() + "for vehicle category " + ServerUtil.getCatgDesc(tradeCert.getVehCatg()) + "</span>" + "<span style='color:blue; font-size:18px;'> | </span>";
                                }
                            }
                        } else {
                            tradeCertificateList = "<span style='color:red;'>Trade Certificate Details are not available in the system.</span>";
                        }
                        if (tradeCertificateList.endsWith("<span style='color:blue; font-size:18px;'> | </span>")) {
                            tradeCertificateList = tradeCertificateList.substring(0, tradeCertificateList.length() - 52);
                        }
                    }
                }
            }
            if (Util.getSession() != null && Util.getSession().getAttribute("msgDobj") == null) {
                MessagesDobj messagesDobj = new MessagesDobj();
                if (user_catg.equals(TableConstants.USER_CATG_OFF_STAFF)) {
                    SeatAllotedDetails status = Home_Impl.getSmartCardAndHsrpStatus(allotedOffCode);
                    messagesDobj.setSmartCardStatus(status.getSmartCardStatus());
                    messagesDobj.setHsrpStatus(status.getHsrpStatus());
                    Util.getSession().setAttribute("msgDobj", messagesDobj);
                }
            }
            if (ApplicationConfiguration.showUserMessage == null) {
                ApplicationConfiguration.showUserMessage = ServerUtil.getAlertMessages("F");
            }
            message = ApplicationConfiguration.showUserMessage;
            if (Util.getSession() != null && Util.getSession().getAttribute("msgDobj") != null) {
                MessagesDobj messagesDobj = (MessagesDobj) Util.getSession().getAttribute("msgDobj");
                if (messagesDobj != null) {
                    smartCard = messagesDobj.getSmartCardStatus();
                    hsrp = messagesDobj.getHsrpStatus();
                }
            }
            ServerUtil.getVmSmartCardHsrpParameters(Util.getUserStateCode(), getAllotedOffCode());
            noOfUnreadMessages = UserMsgImpl.getNoOfUnreadMessages(Long.parseLong(Util.getEmpCode()));
            if (noOfUnreadMessages > 0) {
                unreadMessages = true;
                unReadMessagesNotif = "You have " + noOfUnreadMessages + " unread messages";
            }
            if (Util.getTmConfiguration().getTmUserMessagingConfigDobj().isUserMsgByCatg()) {
                noOfUserCatgMessages = UserMsgImpl.getNoOfUserCatgMessages(Util.getUserCategory(), Util.getUserStateCode());
                noOfReadUserCatgMessages = UserMsgImpl.getNoOfReadUserCatgMessages(Util.getUserCategory(), Util.getUserStateCode(), Util.getEmpCodeLong());
                noOfUnreadUserCatgMessages = noOfUserCatgMessages - noOfReadUserCatgMessages;
                if (!Util.getUserCategory().equals("S")) {
                    userCatgMessages = true;
                }
                userCatgMessagesNotif = "Read State Administrator Announcements";
            }

            vow4Date = ServerUtil.getVahan4StartDate(Util.getUserStateCode(), allotedOffCode);
            if (user_catg != null && user_catg.equals(TableConstants.USER_CATG_OFF_STAFF) && Util.getTmConfiguration() != null && Util.getTmConfiguration().isAllowFacelessService()) {
                renderFaceApplRadioButton = true;
            }
        } catch (VahanException e) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", e.getMessage()));
        } catch (NumberFormatException e) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", TableConstants.SomthingWentWrong));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public String seatWork() {
        String requestedUrl = null;
        try {
            vow4Date = ServerUtil.getVahan4StartDate(Util.getUserStateCode(), allotedOffCode);
            String user_catg = Util.getUserCategory();
            if (checkOfficeTiming()) {
                return null;
            }
            if (allotedOffCode != 0) {
                if (vow4Date == null) {
                    JSFUtils.setFacesMessage("Can't perform the transaction as VAHAN4 has not been started for selected office.", null, JSFUtils.INFO);
                    return null;
                }
                if (allotedActionCode != TableConstants.TM_ROLE_CREATE_USER && allotedActionCode != TableConstants.TM_ROLE_ASSIGN_ROLE) {
                    if (vow4Date.after(ServerUtil.getSystemDateInPostgres())) {
                        JSFUtils.setFacesMessage("Can't perform the transaction as VAHAN4 will start from the " + vow4Date, null, JSFUtils.INFO);
                        return null;
                    }
                }
            }
            String CashCounterMessage = ServerUtil.getConfigurationCashCounter(stateCodeSelected);
            if (allotedActionCode == TableConstants.TM_ROLE_BALANCE_FEE_TAX_COLLECTION
                    || allotedActionCode == TableConstants.TM_ROLE_FANCY_NUMBER_FEE
                    || allotedActionCode == TableConstants.TM_ROLE_TAX_COLLECTION
                    || allotedActionCode == TableConstants.CANCEL_CASH_RCPT
                    || allotedActionCode == TableConstants.TM_ROLE_FANCY_ADVANCE_REGN_FEE) {
                List list = Home_Impl.getCashCounterDetails();
                FacesMessage facesMessage = null;
                if (list != null && list.size() > 0) {
                    if (list.get(0) != null && !list.get(0).equals(Home_Impl.getDBCurrentDate())) {
                        facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Day begin/Cash counter open process not started.!", "Day begin/Cash counter open process not started.!");
                        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                        return null;
                    }
                    if (((boolean) list.get(1)) == true) {
                        facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cash counter is not opened!", "Cash counter is not opened!");
                        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                        return null;
                    }
                } else {
                    facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Day begin/Cash counter open process not started.!", "Day begin/Cash counter open process not started.!");
                    FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                    return null;
                }

                if (!CommonUtils.isNullOrBlank(CashCounterMessage)) {
                    facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, CashCounterMessage, CashCounterMessage);
                    FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                    return null;
                }
            }

            if (getAllotedOffCode() == -1 || getAllotedActionCode() == -1) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please Select Office and Action for Desired Result"));
                return null;
            } else { //We can set whole map into the session           
                if (Util.getSession() != null) {
                    Util.getSession().setAttribute("selected_cntr_id", counter_id);
                    Util.getSession().setAttribute("selected_role_cd", String.valueOf(allotedActionCode)); //here selected_role_cd is Action_cd
                    Util.getSession().setAttribute("selected_off_cd", String.valueOf(allotedOffCode));
                    selectedSeat = new SeatAllotedDetails();
                    selectedSeat.setAction_cd(allotedActionCode);
                    selectedSeat.setOff_cd(allotedOffCode);
                    selectedSeat.setCntr_id(counter_id);
                    Util.getSession().setAttribute("SelectedSeat", selectedSeat);
                }

                if (user_catg != null && user_catg.equals(TableConstants.USER_CATG_DEALER)) {
                    if (allotedActionCode != 0
                            && (allotedActionCode == TableConstants.TM_ROLE_DEALER_TEMP_APPL
                            || allotedActionCode == TableConstants.TM_ROLE_DEALER_NEW_APPL)) {

                        String dealerTradeValidity = ServerUtil.getDealerTradeCertificateDetails(dealerCd, null, stateCodeSelected, Util.getTmConfiguration());
                        if (!CommonUtils.isNullOrBlank(dealerTradeValidity)) {
                            FacesMessage dealerValidityMessg = new FacesMessage(FacesMessage.SEVERITY_ERROR, dealerTradeValidity, dealerTradeValidity);
                            FacesContext.getCurrentInstance().addMessage(null, dealerValidityMessg);
                            return null;
                        }
                    }
                    if (Util.getUserLoginOffCode() != null && Util.getUserLoginOffCode() != allotedOffCode && allotedActionCode == TableConstants.TM_ROLE_DEALER_TEMP_APPL
                            && Util.getTmConfiguration() != null && Util.getTmConfiguration().getTmConfigDealerDobj() != null && !Util.getTmConfiguration().getTmConfigDealerDobj().isAllowInwardTempAnyOffice()) {
                        FacesMessage validiteMessg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Temporary Registration cann't be issued from the other office RTO, please select correct office name in Home Page.", "Temporary Registration cann't be issued from the other office RTO, please select correct office name in Home Page.");
                        FacesContext.getCurrentInstance().addMessage(null, validiteMessg);
                        return null;
                    }
                }

                if (dealerBlockPanel && user_catg != null && TableConstants.USER_CATG_DEALER.equals(user_catg)) {
                    if (allotedActionCode == TableConstants.TM_ROLE_DEALER_NEW_APPL || allotedActionCode == TableConstants.TM_ROLE_DEALER_TEMP_APPL) {
                        throw new VahanException(dealerBlockMsg);
                    }
                }

                if (Util.getTmConfiguration() != null && Util.getTmConfiguration().getTmConfigDmsDobj() != null && (CommonUtils.isNullOrBlank(Util.getTmConfiguration().getTmConfigDmsDobj().getPurCd()) || TableConstants.NotApplicableValue.equalsIgnoreCase(Util.getTmConfiguration().getTmConfigDmsDobj().getPurCd()))
                        && (allotedActionCode == TableConstants.TM_ROLE_DEALER_DOCUMENT_MODIFY || allotedActionCode == TableConstants.TM_ROLE_UPLOAD_MODIFY_DOCUMENT)) {
                    FacesMessage validiteMessg = new FacesMessage(FacesMessage.SEVERITY_WARN, "You can't use this Action because currently DMS is not enable in your state.", "You can't use this Action because currently DMS is not enable in your state.");
                    FacesContext.getCurrentInstance().addMessage(null, validiteMessg);
                    return null;
                }

                if (allotedActionCode == TableConstants.TM_ROLE_CREATE_USER
                        || allotedActionCode == TableConstants.TM_ROLE_ASSIGN_ROLE) {
                    if (allotedActionCode == TableConstants.TM_ROLE_CREATE_USER) {
                        requestedUrl = "/ui/user_mgmt/form_emp_mgmt.xhtml?faces-redirect=true";
                    } else {
                        requestedUrl = "/ui/user_mgmt/form_user_mgmt.xhtml?faces-redirect=true";
                    }
                } else {
                    requestedUrl = ServerUtil.getRequestedUrl(allotedActionCode, "Home_Bean");
                }

                if (allotedActionCode == TableConstants.TM_ROLE_DEALER_NEW_APPL
                        || allotedActionCode == TableConstants.TM_ROLE_NEW_APPL
                        || allotedActionCode == TableConstants.TM_ROLE_NEW_APPL_TEMP
                        || allotedActionCode == TableConstants.TM_ROLE_DEALER_TEMP_APPL) {
                    Map mapTemp = new HashMap();
                    mapTemp.put("appl_no", "");
                    if (allotedActionCode == TableConstants.TM_ROLE_DEALER_NEW_APPL) {
                        mapTemp.put("pur_code", String.valueOf(TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE));
                    } else if (allotedActionCode == TableConstants.TM_ROLE_NEW_APPL) {
                        mapTemp.put("pur_code", String.valueOf(TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE));
                    } else if (allotedActionCode == TableConstants.TM_ROLE_NEW_APPL_TEMP) {
                        mapTemp.put("pur_code", String.valueOf(TableConstants.VM_TRANSACTION_MAST_TEMP_REG));
                    } else if (allotedActionCode == TableConstants.TM_ROLE_DEALER_TEMP_APPL) {
                        mapTemp.put("pur_code", String.valueOf(TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE));
                    }
                    mapTemp.put("actionCode", String.valueOf(allotedActionCode));
                    mapTemp.put("appl_dt", "");
                    mapTemp.put("office_remark", "");
                    mapTemp.put("public_remark", "");
                    mapTemp.put("Purpose", "");
                    mapTemp.put("regn_no", "");
                    mapTemp.put("cur_status", "");
                    Util.getSession().setAttribute("seat_map", mapTemp);
                } else if (requestedUrl.equals("")) {
                    FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("SelectedSeatAction", getSelectedSeatAction());
                    return null;
                }
            }
        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, vex.getMessage(), vex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
        return requestedUrl;
    }

    public String seatWorkArea() {
        FacesMessage message = null;
        String displayMsg = null;
        boolean theftDestroyedWithTO = false;
        String requestedUrl = null;
        TmConfigurationDMS tmConfigDMS = null;
        try {
            if (checkOfficeTiming()) {
                return "";
            }
            Map map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            if (map == null) {
                return "";
            }
            int action_cd = 0;
            if (map.get("actionCode") != null) {
                action_cd = Integer.parseInt(map.get("actionCode").toString());
            } else {
                action_cd = selectedSeat.getAction_cd();
            }

            int action_role = Integer.parseInt(String.valueOf(action_cd).substring(3));// for extracting role_code from action_code

            int pur_cd = 0;
            if (map.get("pur_code") != null) {
                pur_cd = Integer.parseInt(map.get("pur_code").toString());
            } else {
                pur_cd = selectedSeat.getPur_cd();
            }

            String cur_status = null;
            if (map.get("cur_status") == null) {
                cur_status = (String) map.get("cur_status");
            } else {
                cur_status = selectedSeat.getStatus();
            }

            String appl_no = null;
            if (map.get("appl_no") != null) {
                appl_no = map.get("appl_no").toString();
            } else {
                appl_no = selectedSeat.getAppl_no();
            }
            String CashCounterMessage = ServerUtil.getConfigurationCashCounter(stateCodeSelected);
            if (Home_Impl.getRoleCodeOfSeatWork(action_cd) == 9) {
                List list = Home_Impl.getCashCounterDetails();
                if (list.size() > 0) {

                    if (list.get(0) != null && !list.get(0).equals(Home_Impl.getDBCurrentDate())) {
                        displayMsg = "Day Begin/Cash Counter Open Process not Started.";
                        message = new FacesMessage(FacesMessage.SEVERITY_ERROR, displayMsg, displayMsg);
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        return null;
                    }
                    if (((boolean) list.get(1)) == true) {
                        displayMsg = "Cash Counter is not Opened.";
                        message = new FacesMessage(FacesMessage.SEVERITY_ERROR, displayMsg, displayMsg);
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        return null;
                    }
                } else {
                    displayMsg = "Day Begin/Cash Counter Open Process not Started.";
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, displayMsg, displayMsg);
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return null;
                }

                if (!CommonUtils.isNullOrBlank(CashCounterMessage)) {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, CashCounterMessage, CashCounterMessage);
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return null;
                }
            }
            if (!CommonUtils.isNullOrBlank(CashCounterMessage) && action_cd == TableConstants.TM_ROLE_DEALER_NEW_REGN_FEE) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, CashCounterMessage, CashCounterMessage);
                FacesContext.getCurrentInstance().addMessage(null, message);
                return null;
            }

            if (action_cd == TableConstants.SWAPPING_REGN_VERIFICATION || action_cd == TableConstants.SWAPPING_REGN_APPROVAL) {
                if (!Home_Impl.isFeePaid(appl_no)) {
                    String linkApplno = ServerUtil.getLinkApplNo(appl_no);
                    displayMsg = "Application for Swapping/Retention of Registration Mark. Please Pay Fee for Link Application no " + linkApplno + " First.";
                    message = new FacesMessage(FacesMessage.SEVERITY_WARN, displayMsg, displayMsg);
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return null;
                }
            }
            //copy map
            Map map2 = new HashMap();
            map2.putAll(map);
            map2.put("request_source", "seat");
            if (Util.getSession() != null) {
                Util.getSession().setAttribute("seat_map", map2);
                getSelectedSeat().setOff_cd(getAllotedOffCode());
                Util.getSession().setAttribute("SelectedSeat", getSelectedSeat());
            }
            if (pur_cd != TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS
                    && pur_cd != TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_FOR_OFFICE_PURPOSE
                    && pur_cd != TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_COMPANY
                    && pur_cd != TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_OWNER
                    && pur_cd != TableConstants.VM_PMT_PERMIT_PARTICULAR_PUR_CD
                    && pur_cd != TableConstants.VM_PMT_PERMIT_PARTICULAR_PUR_CD_WITHOUT_FEE) {
                if (action_role == TableConstants.TM_ROLE_PRINT && cur_status.equalsIgnoreCase(TableConstants.STATUS_NOT_STARTED)) {
                    return "print_normal";
                }

            }

            //for checking blacklisted vehicle        
            BlackListedVehicleImpl blackListedVehicleImpl = new BlackListedVehicleImpl();
            BlackListedVehicleDobj blackListedVehicleDobj = blackListedVehicleImpl.getBlacklistedVehicleDetails(selectedSeat.getRegn_no(), null);
            if (blackListedVehicleDobj != null) {
                TmConfigurationSwappingDobj tmConfigurationSwappingDobj = Util.getTmConfigurationSwapping();
                if ((pur_cd == TableConstants.VM_TRANSACTION_MAST_TO
                        || pur_cd == TableConstants.VM_TRANSACTION_MAST_FRESH_RC
                        || pur_cd == TableConstants.VM_TRANSACTION_MAST_REM_HYPO
                        || pur_cd == TableConstants.VM_TRANSACTION_MAST_HPC
                        || pur_cd == TableConstants.VM_TRANSACTION_MAST_DUP_RC
                        || pur_cd == TableConstants.VM_PMT_TRANSFER_PUR_CD)
                        && (blackListedVehicleDobj.getComplain_type() == TableConstants.BLTheftCode
                        || blackListedVehicleDobj.getComplain_type() == TableConstants.BLDestroyedAccidentCode
                        || blackListedVehicleDobj.getComplain_type() == TableConstants.BLLoanDefaulterCode
                        || blackListedVehicleDobj.getComplain_type() == TableConstants.BLConfiscationCode)) {
                    //showing no messages in case of theft or destroyed vehicle.
                    theftDestroyedWithTO = true;
                } else if (pur_cd == TableConstants.SWAPPING_REGN_PUR_CD
                        && tmConfigurationSwappingDobj != null && tmConfigurationSwappingDobj.isSwapping_allowed_theft_untraced_case()
                        && blackListedVehicleDobj.getComplain_type() == TableConstants.BLTheftCode) {
                    if (blackListedVehicleImpl.getUntracedRCDetail(blackListedVehicleDobj.getRegn_no(), null).size() <= 0) {
                        displayMsg = "Vehicle is Blacklisted Without Untraced report due to Reason [" + blackListedVehicleDobj.getComplainDesc().toUpperCase() + "] Dated [ " + blackListedVehicleDobj.getComplaindt() + " ] in State [ " + blackListedVehicleDobj.getStateName() + " ] at Office [ " + blackListedVehicleDobj.getOfficeName() + " ]";
                        message = new FacesMessage(FacesMessage.SEVERITY_ERROR, displayMsg, displayMsg);
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        return null;
                    }
                } else if ("KA".equalsIgnoreCase(Util.getUserStateCode()) && (pur_cd == TableConstants.VM_TRANSACTION_MAST_FIT_CERT || pur_cd == TableConstants.TAX_CLEAR_PUR_CD || pur_cd == TableConstants.TAX_EXAMPT_PUR_CD) && blackListedVehicleDobj != null) {
                    if (blackListedVehicleDobj.getComplain_type() != TableConstants.BLDestroyedAccidentCode
                            && blackListedVehicleDobj.getComplain_type() != TableConstants.BLTheftCode
                            && blackListedVehicleDobj.getComplain_type() != TableConstants.BLScrappedCode) {
                        //do nothing
                    } else if (blackListedVehicleDobj.getComplain_type() == TableConstants.BLDestroyedAccidentCode
                            || blackListedVehicleDobj.getComplain_type() == TableConstants.BLTheftCode
                            || blackListedVehicleDobj.getComplain_type() == TableConstants.BLScrappedCode) {
                        displayMsg = "Vehicle is Blacklisted due to Reason [" + blackListedVehicleDobj.getComplainDesc().toUpperCase() + "] Dated [ " + blackListedVehicleDobj.getComplaindt() + " ] in State [ " + blackListedVehicleDobj.getStateName() + " ] at Office [ " + blackListedVehicleDobj.getOfficeName() + " ]";
                        message = new FacesMessage(FacesMessage.SEVERITY_ERROR, displayMsg, displayMsg);
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        return null;
                    }
                } else if (pur_cd != TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS
                        && pur_cd != TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_FOR_OFFICE_PURPOSE
                        && pur_cd != TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_COMPANY
                        && pur_cd != TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_OWNER
                        && pur_cd != TableConstants.ADMIN_OWNER_DATA_CHANGE
                        && pur_cd != TableConstants.VM_TRANSACTION_MAST_AUCTION
                        && !Util.getTmConfiguration().getBlocked_purcd_for_blacklist_vehicle().contains(String.valueOf(pur_cd))) {
                    if (blackListedVehicleImpl.getUntracedRCDetail(blackListedVehicleDobj.getRegn_no(), null).size() <= 0) {
                        displayMsg = "Vehicle is Blacklisted due to Reason [" + blackListedVehicleDobj.getComplainDesc().toUpperCase() + "] Dated [ " + blackListedVehicleDobj.getComplaindt() + " ] in State [ " + blackListedVehicleDobj.getStateName() + " ] at Office [ " + blackListedVehicleDobj.getOfficeName() + " ]";
                        message = new FacesMessage(FacesMessage.SEVERITY_ERROR, displayMsg, displayMsg);
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        return null;
                    }
                }
            }
            //for checking insurance/pucc validity
            boolean InsPuccValidation = false;
            for (int i = 0; i < TableConstants.INSURANCE_VALIDITY_CHECK_ACTIONS.length; i++) {
                if (action_cd == TableConstants.INSURANCE_VALIDITY_CHECK_ACTIONS[i] && !theftDestroyedWithTO) {
                    InsPuccValidation = true;
                    break;
                }
            }
            Owner_dobj ownerDobj = null;
            ownerDobj = new OwnerImpl().set_Owner_appl_db_to_dobj_with_state_off_cd(selectedSeat.getRegn_no(), null, null, pur_cd);
            if (ownerDobj != null) {
                if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NOC) {
                    if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NOC && "KA".contains(stateCodeSelected) && ownerDobj.getStatus().equalsIgnoreCase(TableConstants.VT_RC_SURRENDER_STATUS)) {
                        InsPuccValidation = false;
                    }
                }
                ApplicationInwardImpl applicationInwardImpl = new ApplicationInwardImpl();
                PermitDetailDobj permitDetailDobj = PermitDetailImpl.getPermitdetailsFromRegnNo(selectedSeat.getRegn_no());
                if (applicationInwardImpl.isVehAgeExpired(ownerDobj, permitDetailDobj)) {
                    InsPuccValidation = false;
                }
                if ("GA".contains(stateCodeSelected) && ownerDobj.getOther_criteria() == TableConstants.OTHER_CRITERIA_MINE_VEH && (pur_cd == TableConstants.VM_TRANSACTION_MAST_ADD_HYPO || pur_cd == TableConstants.VM_TRANSACTION_MAST_REM_HYPO || pur_cd == TableConstants.VM_TRANSACTION_MAST_HPC)) {
                    InsPuccValidation = false;
                }
                if (pur_cd == TableConstants.VM_TRANSACTION_MAST_REM_HYPO && !theftDestroyedWithTO) {
                    InsPuccValidation = false;
                }
            }
            if (InsPuccValidation) {
                InsDobj ins_dobj = InsImpl.set_ins_dtls_db_to_dobj(null, appl_no, null, 0);
                if (ins_dobj == null) {
                    ins_dobj = InsImpl.set_ins_dtls_db_to_dobj(selectedSeat.getRegn_no(), null, null, 0);
                    InsImpl insImpl = new InsImpl();
                    if (ins_dobj == null || !insImpl.validateInsurance(ins_dobj)) {
                        getSelectedSeat().setIsInsuranceCheck(true);
                        return "/ui/registration/formUpdateInsurancePollutionDetails.xhtml?faces-redirect=true";
                    }
                }
            }
            if (Util.getTmConfiguration() != null && Util.getTmConfiguration().getTmConfigDmsDobj() != null) {
                tmConfigDMS = Util.getTmConfiguration().getTmConfigDmsDobj();
            } else {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
            if (tmConfigDMS != null && tmConfigDMS.getPurCd().contains("," + pur_cd + ",") && (tmConfigDMS.getDocUploadAllotedOff().contains("ALL") || tmConfigDMS.getDocUploadAllotedOff().contains("," + Util.getSelectedSeat().getOff_cd() + ","))) {
                if (tmConfigDMS.getUploadActionCd().contains("," + action_cd + ",") && !tmConfigDMS.isDocsFolwRequired()) {
                    Owner_dobj ownerDbj = null;
                    if (pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                        ownerDbj = new OwnerImpl().set_Owner_appl_db_to_dobj_with_state_off_cd(null, appl_no, null, pur_cd);
                    }
                    if ((pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || (!"JH".equals(stateCodeSelected) && pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE)) || ("JH".equals(stateCodeSelected) && pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) && ownerDbj != null && ownerDbj.getDob_temp() != null && !ownerDbj.getDob_temp().getPurpose().equals(TableConstants.STOCK_TRANSFER)) {
                        List<VTDocumentModel> docsDetailsList = DmsDocCheckUtils.getUploadedDocumentList(appl_no);
                        if ((action_cd == TableConstants.TM_ROLE_DEALER_VERIFICATION || action_cd == TableConstants.TM_ROLE_DEALER_TEMP_VERIFICATION) && (docsDetailsList == null || docsDetailsList.isEmpty())) {
                            String msg = "Note:- Please upload documents before application verification.";
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("appl_no", appl_no);
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("message", msg);
                            return "/ui/form_documents_upload.xhtml?faces-redirect=true";
                        }
                    }
                } else if (tmConfigDMS.isDocsFolwRequired() && pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE && Util.getTmConfiguration().isTempFeeInNewRegis() && Util.getTmConfiguration().getTmConfigDealerDobj() != null
                        && Util.getTmConfiguration().getTmConfigDealerDobj().isTempRegnApprovalBeforeNewRegn() && (action_cd == TableConstants.TM_TMP_RC_VERIFICATION || action_cd == TableConstants.TM_TMP_RC_APPROVAL)) {
                    int loginOffcd = ServerUtil.getOfficeCdForDealerTempAppl(appl_no, stateCodeSelected, "offCorrection");
                    if (loginOffcd > 0) {
                        List<VTDocumentModel> docsDetailsList = DmsDocCheckUtils.getUploadedDocumentList(appl_no);
                        if (docsDetailsList == null || docsDetailsList.isEmpty()) {
                            throw new VahanException("Can't process the file as document has not been uploaded by the dealer.");
                        }
                    }
                }
            }
            requestedUrl = getSelectedSeat().getRedirect_url();
        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, vex.getMessage(), vex.getMessage()));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
        }
        return requestedUrl;
    }

    public void getPendingWorkAndActionList() {
        int allotedOfficeCode = getAllotedOffCode();
        allseatWork = new ArrayList<SeatAllotedDetails>();
        String errorMsg = "";
        try {
            if (checkOfficeTiming()) {
                return;
            }
            if (allotedOfficeCode == -1) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please Select Office for Desired Result"));
                allseatWork.clear();
            } else {
                allseatWork.clear();
                if ((searchByValue.equalsIgnoreCase("all") || searchByValue.equalsIgnoreCase("faceApplNo")) || (!firstPartApplNo.equals("") && !secondPartApplNo.equals("")) || !regnNo.equals("") || !oldApplNo.equals("")) {
                    if (searchByValue.equalsIgnoreCase("all") || secondPartApplNo.trim().length() <= 10) {
                        if (searchByValue.equalsIgnoreCase("applNo")) {
                            setNextDateButton(false);
                            setPrevDateButton(false);
                            setCalPanel(false);
                            String paddedApplNo = secondPartApplNo.trim();
                            if (paddedApplNo != null && !paddedApplNo.isEmpty() && paddedApplNo.replaceAll("[0-9]", "").length() == 0) {
                                paddedApplNo = String.format("%010d", Long.parseLong(paddedApplNo));
                            }
                            String applNumberForPendingWrk = firstPartApplNo.trim() + paddedApplNo;
                            allseatWork = Home_Impl.seatWorkList(String.valueOf(allotedOfficeCode), applNumberForPendingWrk, "", searchByValue, fromDate, uptoDate);
                            errorMsg = "No pending work against " + applNumberForPendingWrk + "";
                        } else if (searchByValue.equalsIgnoreCase("regnNo")) {
                            setNextDateButton(false);
                            setPrevDateButton(false);
                            setCalPanel(false);
                            allseatWork = Home_Impl.seatWorkList(String.valueOf(allotedOfficeCode), "", regnNo, searchByValue, fromDate, uptoDate);
                            errorMsg = "No pending work against " + regnNo + "";
                        } else if (searchByValue.equalsIgnoreCase("all")) {
                            setPrevDateButton(true);
                            setNextDateButton(false);
                            setCalPanel(true);
                            uptoDate = new Date();
                            fromDate = ServerUtil.dateRange(uptoDate, 0, 0, -1 * (getPendingWorkDaysPeriod() - 1));
                            allseatWork = Home_Impl.seatWorkList(String.valueOf(allotedOfficeCode), "", "", searchByValue, fromDate, uptoDate);
                            errorMsg = "No pending work against";
                        } else if (searchByValue.equalsIgnoreCase("oldApplNo")) {
                            setNextDateButton(false);
                            setPrevDateButton(false);
                            setCalPanel(false);
                            allseatWork = Home_Impl.seatWorkList(String.valueOf(allotedOfficeCode), oldApplNo, "", searchByValue, fromDate, uptoDate);
                            errorMsg = "No pending work against " + oldApplNo;
                        } else if (searchByValue.equalsIgnoreCase("faceApplNo")) {
                            setNextDateButton(false);
                            setPrevDateButton(false);
                            setCalPanel(false);
                            allseatWork = Home_Impl.seatWorkList(String.valueOf(allotedOfficeCode), "", "", searchByValue, fromDate, uptoDate);
                            errorMsg = "No pending work against";
                        }

                        if (allseatWork.size() > 0) {
                            if (JSFUtils.findComponentById("masterLayout", "actionMsg")) {
                                PrimeFaces.current().ajax().update("actionMsg");
                            }
                        } else {
                            if (JSFUtils.findComponentById("masterLayout", "actionMsg")) {
                                PrimeFaces.current().ajax().update("actionMsg");
                            }
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", errorMsg));
                        }
                    } else {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Invalid Application no.Minimum Character is 1 and Maximum Character is 10"));
                    }
                } else {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please enter Application No/Regn No"));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void getPendingWorkDateWisePrev() {
        int allotedOfficeCode = getAllotedOffCode();
        if (fromDate != null && vow4Date != null && uptoDate != null && searchByValue.equalsIgnoreCase("all")) {
            Date tempDate = fromDate;
            fromDate = ServerUtil.dateRange(fromDate, 0, 0, -1 * getPendingWorkDaysPeriod());

            if (DateUtils.compareDates(vow4Date, fromDate) == 2) {
                setPrevDateButton(false);
                fromDate = tempDate;
                JSFUtils.showMessagesInDialog("Information", "Out of Range Dates", FacesMessage.SEVERITY_ERROR);
            } else if (DateUtils.compareDates(fromDate, vow4Date) == 0) {
                setNextDateButton(true);
                setPrevDateButton(false);
                uptoDate = ServerUtil.dateRange(fromDate, 0, 0, (getPendingWorkDaysPeriod() - 1));
                allseatWork = Home_Impl.seatWorkList(String.valueOf(allotedOfficeCode), "", "", searchByValue, fromDate, uptoDate);
            } else {
                setPrevDateButton(true);
                setNextDateButton(true);
                if (DateUtils.compareDates(tempDate, uptoDate) == 0) {
                    uptoDate = ServerUtil.dateRange(uptoDate, 0, 0, -1 * (getPendingWorkDaysPeriod() - 1));
                } else {
                    uptoDate = ServerUtil.dateRange(uptoDate, 0, 0, -1 * getPendingWorkDaysPeriod());
                }
                allseatWork = Home_Impl.seatWorkList(String.valueOf(allotedOfficeCode), "", "", searchByValue, fromDate, uptoDate);
            }
        }
    }

    public void getPendingWorkDateWiseNext() {
        int allotedOfficeCode = getAllotedOffCode();
        if (fromDate != null && uptoDate != null && searchByValue.equalsIgnoreCase("all")) {
            Date tempDate = ServerUtil.dateRange(fromDate, 0, 0, getPendingWorkDaysPeriod());
            if (tempDate.compareTo(new Date()) >= 0) {
                setNextDateButton(false);
                JSFUtils.showMessagesInDialog("Information", "Out of Range Dates", FacesMessage.SEVERITY_ERROR);
            } else {
                setPrevDateButton(true);
                fromDate = ServerUtil.dateRange(fromDate, 0, 0, getPendingWorkDaysPeriod());
                uptoDate = ServerUtil.dateRange(uptoDate, 0, 0, getPendingWorkDaysPeriod());
                allseatWork = Home_Impl.seatWorkList(String.valueOf(allotedOfficeCode), "", "", searchByValue, fromDate, uptoDate);
            }
        }
    }

    public void getPendingWorkDateSelectionWise() {
        int allotedOfficeCode = getAllotedOffCode();
        if (fromDate != null && vow4Date != null && uptoDate != null && searchByValue.equalsIgnoreCase("all")) {
            if (DateUtils.compareDates(fromDate, currentDate) == 0) {
                setNextDateButton(false);
                setPrevDateButton(true);
                uptoDate = fromDate;
                allseatWork = Home_Impl.seatWorkList(String.valueOf(allotedOfficeCode), "", "", searchByValue, fromDate, uptoDate);
            } else if (DateUtils.compareDates(fromDate, vow4Date) == 0) {
                setNextDateButton(true);
                setPrevDateButton(false);
                uptoDate = ServerUtil.dateRange(fromDate, 0, 0, (getPendingWorkDaysPeriod() - 1));
                allseatWork = Home_Impl.seatWorkList(String.valueOf(allotedOfficeCode), "", "", searchByValue, fromDate, uptoDate);
            } else {
                setNextDateButton(true);
                setPrevDateButton(true);
                uptoDate = ServerUtil.dateRange(fromDate, 0, 0, (getPendingWorkDaysPeriod() - 1));
                allseatWork = Home_Impl.seatWorkList(String.valueOf(allotedOfficeCode), "", "", searchByValue, fromDate, uptoDate);
            }
        }
    }

    public void validatePullBackAppl() {
        Home_Impl implObj = new Home_Impl();
        String applNumberForPendingWrk = null;
        try {
            if ((searchByValue.equalsIgnoreCase("applNo") && secondPartApplNo != null && !secondPartApplNo.isEmpty())
                    || (searchByValue.equalsIgnoreCase("oldApplNo") && oldApplNo != null && !oldApplNo.isEmpty())) {

                if (searchByValue.equalsIgnoreCase("applNo")) {
                    String paddedApplNo = secondPartApplNo.trim();
                    if (paddedApplNo != null && !paddedApplNo.isEmpty() && paddedApplNo.replaceAll("[0-9]", "").length() == 0) {
                        paddedApplNo = String.format("%010d", Long.parseLong(secondPartApplNo.trim()));
                    }
                    applNumberForPendingWrk = firstPartApplNo.trim() + paddedApplNo;
                } else if (searchByValue.equalsIgnoreCase("oldApplNo")) {
                    applNumberForPendingWrk = oldApplNo;
                }
                ApplicationInwardImpl inwardImpl = new ApplicationInwardImpl();
                purCodeList = inwardImpl.getPurCodeDescr(applNumberForPendingWrk);
                if (purCodeList.isEmpty()) {
                    throw new VahanException("Incorrect Application Number");
                }
                boolean status = implObj.validatePullBackApplication(applNumberForPendingWrk, Util.getEmpCode(), purCodeList);
                if (status) {
                    PrimeFaces.current().executeScript("PF('pullBack').show()");
                }
            } else {
                JSFUtils.showMessagesInDialog("Alert!", "Provide the Valid Application Number.", FacesMessage.SEVERITY_ERROR);
            }
        } catch (VahanException e) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", e.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public String pullBackAppl() {
        Home_Impl obj = new Home_Impl();
        String applNumberForPendingWrk = null;
        if ((!CommonUtils.isNullOrBlank(secondPartApplNo) || !CommonUtils.isNullOrBlank(oldApplNo))
                && !CommonUtils.isNullOrBlank(pullBackReason)
                && !selectedPurCdForPullBack.isEmpty()) {
            if (!CommonUtils.isNullOrBlank(secondPartApplNo)) {
                String paddedApplNo = secondPartApplNo.trim();
                if (paddedApplNo != null && !paddedApplNo.isEmpty() && paddedApplNo.replaceAll("[0-9]", "").length() == 0) {
                    paddedApplNo = String.format("%010d", Long.parseLong(secondPartApplNo.trim()));
                }
                applNumberForPendingWrk = firstPartApplNo.trim() + paddedApplNo;
            } else if (!CommonUtils.isNullOrBlank(oldApplNo)) {
                applNumberForPendingWrk = oldApplNo;
            }
            try {
                if (obj.pullBackApplication(applNumberForPendingWrk, pullBackReason, selectedPurCdForPullBack)) {
                    PrimeFaces.current().executeScript("PF('pullBack').hide();");
                    JSFUtils.setFacesMessage("Application Pulled Back Successfully.", null, JSFUtils.INFO);
                    return "home";
                }
            } catch (VahanException e) {
                PrimeFaces.current().executeScript("PF('pullBack').hide();");
                JSFUtils.setFacesMessage(e.getMessage(), null, JSFUtils.INFO);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        } else {
            if (CommonUtils.isNullOrBlank(secondPartApplNo)) {
                JSFUtils.showMessagesInDialog("Alert!", "Provide the Valid Application Number.", FacesMessage.SEVERITY_ERROR);
            } else if (CommonUtils.isNullOrBlank(pullBackReason) && CommonUtils.isNullOrBlank(pullBackReason)) {
                JSFUtils.showMessagesInDialog("Alert!", "Please Provide the Details.", FacesMessage.SEVERITY_ERROR);
            } else if (CommonUtils.isNullOrBlank(pullBackReason)) {
                JSFUtils.showMessagesInDialog("Alert!", "Please Provide the Reason.", FacesMessage.SEVERITY_ERROR);
            } else if (selectedPurCdForPullBack.isEmpty()) {
                JSFUtils.showMessagesInDialog("Alert!", "Please Select One of the Transaction.", FacesMessage.SEVERITY_ERROR);
            }
        }
        return "";
    }

    public void updateAssignedOffWise() {
        try {
            int allotedOfficeCode = getAllotedOffCode();
            vow4Date = ServerUtil.getVahan4StartDate(Util.getUserStateCode(), allotedOfficeCode);
            if (vow4Date == null && allotedOfficeCode != 0) {
                setAllotedOffCode(0);
                JSFUtils.showMessagesInDialog("Alert!", "Vahan 4.0 is not implemented in this RTO.", FacesMessage.SEVERITY_INFO);
                return;
            } else {
                updatePendingWorkListAssignedOffWise();
                updateAvailableRegistrationNoListAssignedOffWise(allotedOfficeCode);
                updateActionListAssignedOffWise(allotedOfficeCode);
            }
            ServerUtil.getVmSmartCardHsrpParameters(Util.getUserStateCode(), getAllotedOffCode());
        } catch (VahanException e) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", e.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

    }

    public void updatePendingWorkListAssignedOffWise() {
        try {
            allseatWork = new ArrayList<SeatAllotedDetails>();
            allseatWork.clear();
            searchByValue = "applNo";
            secondPartApplNo = "";
            if (JSFUtils.findComponentById("masterLayout", "actionMsg")) {
                PrimeFaces.current().ajax().update("actionMsg");
            }
            PrimeFaces.current().ajax().update("pendingInputs");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void updateAvailableRegistrationNoListAssignedOffWise(int allotedOffCode) {
        try {
            if (allotedOffCode == -1) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please Select Office for Desired Result"));
                availableRegnNoList = "";
                PrimeFaces.current().ajax().update("availableRegnListID");
            } else {
                String user_catg = Util.getUserCategory();
                if (!"OR".equals(stateCodeSelected) && user_catg != null && (user_catg.equals(TableConstants.USER_CATG_DEALER) || user_catg.equals(TableConstants.USER_CATG_OFF_STAFF))) {
                    availableRegnNoList = Home_Impl.getAvailableRegnNoList(allotedOffCode);
                    availableRegnNO = true;
                    PrimeFaces.current().ajax().update("availableRegnListID");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void updateActionListAssignedOffWise(int allotedOfficeCode) throws VahanException {
        int allotedOfficeCodeDealer = 0;
        try {
            if (allotedOffCode == -1) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please Select Office for Desired Result"));
                allotedActionCodeList.clear();
            } else if (allotedOffCode == 0 && Util.getUserCategory().equals(TableConstants.USER_CATG_STATE_HSRP)) { //0 off code is for all RTO // for hsrp single user
                allotedActionCodeList.clear();
                allotedActionCodeList.put("HSRP-VENDOR-FLAT-FILE", TableConstants.HSRP_VENDOR_FLAT_FILE);
                allotedActionCodeList.put("HSRP-VENDOR-UPLOAD-FILE", TableConstants.HSRP_VENDOR_UPLOAD_FILE);
            } else if (empCodeLoggedIn != null && empCodeLoggedIn.length() > 0) {
                if (ServerUtil.validateDealerUserForAllOffice(Long.parseLong(empCodeLoggedIn))) {
                    Map<String, Object> allotedOffCodeListTemp = Home_Impl.getAllotedOfficeCodeDescr(false);
                    for (Map.Entry<String, Object> offcdEntry : allotedOffCodeListTemp.entrySet()) {
                        allotedOfficeCodeDealer = (int) offcdEntry.getValue();
                    }
                    allotedActionCodeList = Home_Impl.getAllotedActionCodeDescr(allotedOfficeCodeDealer, stateCodeSelected, empCodeLoggedIn);
                } else {
                    allotedActionCodeList = Home_Impl.getAllotedActionCodeDescr(allotedOfficeCode, stateCodeSelected, empCodeLoggedIn);
                }
                for (Map.Entry<String, Object> actionCdEntry : allotedActionCodeList.entrySet()) {
                    actionDescr = actionCdEntry.getKey();
                    allotedActionCode = (int) actionCdEntry.getValue();
                    break;
                }
                if (!allotedOffCodeList.isEmpty()) {
                    String user_catg = Util.getUserCategory();
                    if (user_catg.equals(TableConstants.USER_CATG_PORTAL_ADMIN)
                            || user_catg.equals(TableConstants.USER_CATG_STATE_ADMIN)
                            || user_catg.equals(TableConstants.USER_CATG_OFFICE_ADMIN)
                            || user_catg.equals(TableConstants.USER_CATG_FITNESS_CENTER_ADMIN)) {
                        allotedActionCodeList.put("CREATE USER", TableConstants.TM_ROLE_CREATE_USER);
                        allotedActionCodeList.put("ASSIGN ROLE/ACTION", TableConstants.TM_ROLE_ASSIGN_ROLE);
                    }
                }
                Util.getSession().setAttribute("selected_off_cd", String.valueOf(allotedOfficeCode));
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public static boolean checkOfficeTiming() throws VahanException {
        String state_cd = Util.getUserStateCode();
        Integer off_cd = Util.getUserOffCode();
        String user_catg = Util.getUserCategory();
        Integer user_cd = Integer.parseInt(Util.getEmpCode());
        String[][] data;
        data = MasterTableFiller.masterTables.TM_CONFIGURATION_LOGIN.getData();
        for (int i = 0; i < data.length; i++) {
            if (data[i][0].equalsIgnoreCase(state_cd) && Integer.parseInt(data[i][1]) == off_cd && !TableConstants.OFFICE_TIMING_EXEMPTION_CATG.contains("," + user_catg + ",")
                    && DateUtil.compareCurrentTimeAndOfficeTime(DateUtil.getTimeInHHMMSS(new Date()), data[i][3])
                    && !UserDAO.compareUserCloseTiming(user_cd)) {
                Util.removeAllSessionAttribute(Util.getSession());
                JSFUtils.setFacesMessage("Can't perform the transaction because Office Timing is Over.", null, JSFUtils.INFO);
                return true;
            }
        }
        return false;
    }

    private Map<String, Object> fillAllotedOfficeCodeDescr(String empCode) throws Exception {
        boolean dealerCheck = false;
        if (empCode != null && empCode.trim().length() > 0) {
            dealerCheck = ServerUtil.validateDealerUserForAllOffice(Long.parseLong(empCode));
        }
        return Home_Impl.getAllotedOfficeCodeDescr(dealerCheck);
    }

    public String showDashBoard() throws VahanException {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("OffName", offDescr);
        return "/ui/dashBoard/dashboardForPendingApplication.xhtml?faces-redirect=true";

    }

    public String dashBoarSelectedActionOnApplication(DashboardDetails dobj) {
        String errorMsg = "";
        boolean allowForUser = false;
        String requestedUrl = null;
        try {
            if (dobj != null) {
                PendingApplDashBoardImpl impl = new PendingApplDashBoardImpl();
                allowForUser = impl.dashBoardActionOnSelectedAppl(dobj, getAllotedOffCode());
                if (allowForUser) {
                    selectedSeat = dobj.getSelectedSeat();
                    requestedUrl = seatWorkArea();
                } else {
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    JSFUtils.showMessagesInDialog("Alert!", "You are not authorized to work on this application,please contact system administrator!!! ", FacesMessage.SEVERITY_INFO);

                }
            }
        } catch (VahanException vme) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vme.getMessage(), vme.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
        return requestedUrl;
    }

    /**
     * @return the allSeat
     */
    public List<SeatAllotedDetails> getAllSeat() {
        return allSeat;
    }

    /**
     * @param allSeat the allSeat to set
     */
    public void setAllSeat(List<SeatAllotedDetails> allSeat) {
        this.allSeat = allSeat;
    }

    /**
     * @return the allotedOffCode
     */
    public int getAllotedOffCode() {
        return allotedOffCode;
    }

    /**
     * @param allotedOffCode the allotedOffCode to set
     */
    public void setAllotedOffCode(int allotedOffCode) {
        this.allotedOffCode = allotedOffCode;
    }

    /**
     * @return the allotedOffCodeList
     */
    public Map<String, Object> getAllotedOffCodeList() {
        return allotedOffCodeList;
    }

    /**
     * @param allotedOffCodeList the allotedOffCodeList to set
     */
    public void setAllotedOffCodeList(Map<String, Object> allotedOffCodeList) {
        this.allotedOffCodeList = allotedOffCodeList;
    }

    /**
     * @return the allotedRoleCode
     */
    public int getAllotedRoleCode() {
        return allotedRoleCode;
    }

    /**
     * @param allotedRoleCode the allotedRoleCode to set
     */
    public void setAllotedRoleCode(int allotedRoleCode) {
        this.allotedRoleCode = allotedRoleCode;
    }

    /**
     * @return the allotedRoleCodeList
     */
    public Map<String, Object> getAllotedRoleCodeList() {
        return allotedRoleCodeList;
    }

    /**
     * @param allotedRoleCodeList the allotedRoleCodeList to set
     */
    public void setAllotedRoleCodeList(Map<String, Object> allotedRoleCodeList) {
        this.allotedRoleCodeList = allotedRoleCodeList;
    }

    /**
     * @return the offDescr
     */
    public String getOffDescr() {
        return offDescr;
    }

    /**
     * @param offDescr the offDescr to set
     */
    public void setOffDescr(String offDescr) {
        this.offDescr = offDescr;
    }

    /**
     * @return the roleDescr
     */
    public String getRoleDescr() {
        return roleDescr;
    }

    /**
     * @param roleDescr the roleDescr to set
     */
    public void setRoleDescr(String roleDescr) {
        this.roleDescr = roleDescr;
    }

    /**
     * @return the selectedSeatAction
     */
    public SeatAllotedDetails getSelectedSeatAction() {
        return selectedSeatAction;
    }

    /**
     * @param selectedSeatAction the selectedSeatAction to set
     */
    public void setSelectedSeatAction(SeatAllotedDetails selectedSeatAction) {
        this.selectedSeatAction = selectedSeatAction;
    }

    /**
     * @return the allseatWork
     */
    public List<SeatAllotedDetails> getAllseatWork() {
        return allseatWork;
    }

    /**
     * @param allseatWork the allseatWork to set
     */
    public void setAllseatWork(List<SeatAllotedDetails> allseatWork) {
        this.allseatWork = allseatWork;
    }

    /**
     * @return the filteredSeat
     */
    public List<SeatAllotedDetails> getFilteredSeat() {
        return filteredSeat;
    }

    /**
     * @param filteredSeat the filteredSeat to set
     */
    public void setFilteredSeat(List<SeatAllotedDetails> filteredSeat) {
        this.filteredSeat = filteredSeat;
    }

    /**
     * @return the allotedActionCodeList
     */
    public Map<String, Object> getAllotedActionCodeList() {
        return allotedActionCodeList;
    }

    /**
     * @param allotedActionCodeList the allotedActionCodeList to set
     */
    public void setAllotedActionCodeList(Map<String, Object> allotedActionCodeList) {
        this.allotedActionCodeList = allotedActionCodeList;
    }

    /**
     * @return the allotedActionCode
     */
    public int getAllotedActionCode() {
        return allotedActionCode;
    }

    /**
     * @param allotedActionCode the allotedActionCode to set
     */
    public void setAllotedActionCode(int allotedActionCode) {
        this.allotedActionCode = allotedActionCode;
    }

    /**
     * @return the actionDescr
     */
    public String getActionDescr() {
        return actionDescr;
    }

    /**
     * @param actionDescr the actionDescr to set
     */
    public void setActionDescr(String actionDescr) {
        this.actionDescr = actionDescr;
    }

    /**
     * @return the selectedSeat
     */
    public SeatAllotedDetails getSelectedSeat() {
        return selectedSeat;
    }

    /**
     * @param selectedSeat the selectedSeat to set
     */
    public void setSelectedSeat(SeatAllotedDetails selectedSeat) {
        this.selectedSeat = selectedSeat;
    }

    /**
     * @return the cashCounterOpen
     */
    public boolean isCashCounterOpen() {
        return cashCounterOpen;
    }

    /**
     * @param cashCounterOpen the cashCounterOpen to set
     */
    public void setCashCounterOpen(boolean cashCounterOpen) {
        this.cashCounterOpen = cashCounterOpen;
    }

    /**
     * @return the cashCounterClose
     */
    public boolean isCashCounterClose() {
        return cashCounterClose;
    }

    /**
     * @param cashCounterClose the cashCounterClose to set
     */
    public void setCashCounterClose(boolean cashCounterClose) {
        this.cashCounterClose = cashCounterClose;
    }

    /**
     * @return the firstPartApplNo
     */
    public String getFirstPartApplNo() {
        return firstPartApplNo;
    }

    /**
     * @param firstPartApplNo the firstPartApplNo to set
     */
    public void setFirstPartApplNo(String firstPartApplNo) {
        this.firstPartApplNo = firstPartApplNo;
    }

    /**
     * @return the secondPartApplNo
     */
    public String getSecondPartApplNo() {
        return secondPartApplNo;
    }

    /**
     * @param secondPartApplNo the secondPartApplNo to set
     */
    public void setSecondPartApplNo(String secondPartApplNo) {
        this.secondPartApplNo = secondPartApplNo;
    }

    /**
     * @return the regnNo
     */
    public String getRegnNo() {
        return regnNo;
    }

    /**
     * @param regnNo the regnNo to set
     */
    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    /**
     * @return the searchByValue
     */
    public String getSearchByValue() {
        return searchByValue;
    }

    /**
     * @param searchByValue the searchByValue to set
     */
    public void setSearchByValue(String searchByValue) {
        this.searchByValue = searchByValue;
    }

    /**
     * @return the pendingWorkPanel
     */
    public boolean isPendingWorkPanel() {
        return pendingWorkPanel;
    }

    /**
     * @param pendingWorkPanel the pendingWorkPanel to set
     */
    public void setPendingWorkPanel(boolean pendingWorkPanel) {
        this.pendingWorkPanel = pendingWorkPanel;
    }

    /**
     * @return the availableRegnNO
     */
    public boolean isAvailableRegnNO() {
        return availableRegnNO;
    }

    /**
     * @param availableRegnNO the availableRegnNO to set
     */
    public void setAvailableRegnNO(boolean availableRegnNO) {
        this.availableRegnNO = availableRegnNO;
    }

    /**
     * @return the availableRegnNoList
     */
    public String getAvailableRegnNoList() {
        return availableRegnNoList;
    }

    /**
     * @param availableRegnNoList the availableRegnNoList to set
     */
    public void setAvailableRegnNoList(String availableRegnNoList) {
        this.availableRegnNoList = availableRegnNoList;
    }

    /**
     * @return the smartCard
     */
    public String getSmartCard() {
        return smartCard;
    }

    /**
     * @param smartCard the smartCard to set
     */
    public void setSmartCard(String smartCard) {
        this.smartCard = smartCard;
    }

    /**
     * @return the hsrp
     */
    public String getHsrp() {
        return hsrp;
    }

    /**
     * @param hsrp the hsrp to set
     */
    public void setHsrp(String hsrp) {
        this.hsrp = hsrp;
    }

    /**
     * @return the tradeCertificateList
     */
    public String getTradeCertificateList() {
        return tradeCertificateList;
    }

    /**
     * @param tradeCertificateList the tradeCertificateList to set
     */
    public void setTradeCertificateList(String tradeCertificateList) {
        this.tradeCertificateList = tradeCertificateList;
    }

    /**
     * @return the tradeCertPanel
     */
    public boolean isTradeCertPanel() {
        return tradeCertPanel;
    }

    /**
     * @param tradeCertPanel the tradeCertPanel to set
     */
    public void setTradeCertPanel(boolean tradeCertPanel) {
        this.tradeCertPanel = tradeCertPanel;
    }

    /**
     * @return the dealerBlockMsg
     */
    public String getDealerBlockMsg() {
        return dealerBlockMsg;
    }

    /**
     * @param dealerBlockMsg the dealerBlockMsg to set
     */
    public void setDealerBlockMsg(String dealerBlockMsg) {
        this.dealerBlockMsg = dealerBlockMsg;
    }

    /**
     * @return the dealerBlockPanel
     */
    public boolean isDealerBlockPanel() {
        return dealerBlockPanel;
    }

    /**
     * @param dealerBlockPanel the dealerBlockPanel to set
     */
    public void setDealerBlockPanel(boolean dealerBlockPanel) {
        this.dealerBlockPanel = dealerBlockPanel;
    }

    /**
     * @return the fromDate
     */
    public Date getFromDate() {
        return fromDate;
    }

    /**
     * @param fromDate the fromDate to set
     */
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * @return the uptoDate
     */
    public Date getUptoDate() {
        return uptoDate;
    }

    /**
     * @param uptoDate the uptoDate to set
     */
    public void setUptoDate(Date uptoDate) {
        this.uptoDate = uptoDate;
    }

    /**
     * @return the prevDateButton
     */
    public boolean isPrevDateButton() {
        return prevDateButton;
    }

    /**
     * @param prevDateButton the prevDateButton to set
     */
    public void setPrevDateButton(boolean prevDateButton) {
        this.prevDateButton = prevDateButton;
    }

    /**
     * @return the nextDateButton
     */
    public boolean isNextDateButton() {
        return nextDateButton;
    }

    /**
     * @param nextDateButton the nextDateButton to set
     */
    public void setNextDateButton(boolean nextDateButton) {
        this.nextDateButton = nextDateButton;
    }

    /**
     * @return the pullBackReason
     */
    public String getPullBackReason() {
        return pullBackReason;
    }

    /**
     * @param pullBackReason the pullBackReason to set
     */
    public void setPullBackReason(String pullBackReason) {
        this.pullBackReason = pullBackReason;
    }

    /**
     * @return the purCodeList
     */
    public Map<String, Integer> getPurCodeList() {
        return purCodeList;
    }

    /**
     * @param purCodeList the purCodeList to set
     */
    public void setPurCodeList(Map<String, Integer> purCodeList) {
        this.purCodeList = purCodeList;
    }

    /**
     * @return the selectedPurCdForPullBack
     */
    public List<String> getSelectedPurCdForPullBack() {
        return selectedPurCdForPullBack;
    }

    /**
     * @param selectedPurCdForPullBack the selectedPurCdForPullBack to set
     */
    public void setSelectedPurCdForPullBack(List<String> selectedPurCdForPullBack) {
        this.selectedPurCdForPullBack = selectedPurCdForPullBack;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the oldApplNo
     */
    public String getOldApplNo() {
        return oldApplNo;
    }

    /**
     * @param oldApplNo the oldApplNo to set
     */
    public void setOldApplNo(String oldApplNo) {
        this.oldApplNo = oldApplNo;
    }

    /**
     * @return the pendingWorkDaysPeriod
     */
    public int getPendingWorkDaysPeriod() {
        return pendingWorkDaysPeriod;
    }

    /**
     * @param pendingWorkDaysPeriod the pendingWorkDaysPeriod to set
     */
    public void setPendingWorkDaysPeriod(int pendingWorkDaysPeriod) {
        this.pendingWorkDaysPeriod = pendingWorkDaysPeriod;
    }

    public void setUnreadMessages(boolean unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public boolean isUnreadMessages() {
        return unreadMessages;
    }

    public long getNoOfUnreadMessages() {
        return noOfUnreadMessages;
    }

    public void setNoOfUnreadMessages(long noOfUnreadMessages) {
        this.noOfUnreadMessages = noOfUnreadMessages;
    }

    public void setUnReadMessagesNotif(String unReadMessagesNotif) {
        this.unReadMessagesNotif = unReadMessagesNotif;
    }

    public String getUnReadMessagesNotif() {
        return unReadMessagesNotif;
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

    /**
     * @return the vow4Date
     */
    public Date getVow4Date() {
        return vow4Date;
    }

    /**
     * @param vow4Date the vow4Date to set
     */
    public void setVow4Date(Date vow4Date) {
        this.vow4Date = vow4Date;
    }

    /**
     * @return the calPanel
     */
    public boolean isCalPanel() {
        return calPanel;
    }

    /**
     * @param calPanel the calPanel to set
     */
    public void setCalPanel(boolean calPanel) {
        this.calPanel = calPanel;
    }

    /**
     * @return the renderFaceApplRadioButton
     */
    public boolean isRenderFaceApplRadioButton() {
        return renderFaceApplRadioButton;
    }

    /**
     * @param renderFaceApplRadioButton the renderFaceApplRadioButton to set
     */
    public void setRenderFaceApplRadioButton(boolean renderFaceApplRadioButton) {
        this.renderFaceApplRadioButton = renderFaceApplRadioButton;
    }

    public boolean isRenderedDashBoardBtn() {
        return renderedDashBoardBtn;
    }

    public void setRenderedDashBoardBtn(boolean renderedDashBoardBtn) {
        this.renderedDashBoardBtn = renderedDashBoardBtn;
    }

    public void setUserCatgMessages(boolean userCatgMessages) {
        this.userCatgMessages = userCatgMessages;
    }

    public boolean isUserCatgMessages() {
        return userCatgMessages;
    }

    public void setUserCatgMessagesNotif(String userCatgMessagesNotif) {
        this.userCatgMessagesNotif = userCatgMessagesNotif;
    }

    public String getUserCatgMessagesNotif() {
        return userCatgMessagesNotif;
    }

    public long getNoOfUnreadUserCatgMessages() {
        return noOfUnreadUserCatgMessages;
    }

    public void setNoOfUnreadUserCatgMessages(long noOfUnreadUserCatgMessages) {
        this.noOfUnreadUserCatgMessages = noOfUnreadUserCatgMessages;
    }

    public long getNoOfUserCatgMessages() {
        return noOfUserCatgMessages;
    }

    public void setNoOfUserCatgMessages(long noOfUserCatgMessages) {
        this.noOfUserCatgMessages = noOfUserCatgMessages;
    }

    /*
     * Created for the sole purpose of starting another New Registration Flow
     */
    public String newRegistrationSeatWork() {
        int purCd = Integer.parseInt(((Map) Util.getSession().getAttribute("seat_map")).get("pur_code").toString());
        if (purCd == 1) {
            allotedActionCode = TableConstants.TM_ROLE_NEW_APPL;
        } else if (purCd == 123) {
            allotedActionCode = TableConstants.TM_ROLE_DEALER_NEW_APPL;
        }

        return this.seatWork();
    }
}
