/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import org.apache.log4j.Logger;
import nic.vahan.form.dobj.TaxClearDobj;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.TaxClearImpl;
import nic.vahan.form.impl.ComparisonBeanImpl;
import static nic.vahan.server.ServerUtil.Compare;
import static nic.vahan.server.ServerUtil.compareRefundAndExcess;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.RefundAndExcessDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.primefaces.PrimeFaces;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.impl.FeeImpl;
import nic.vahan.form.impl.HpaImpl;
import nic.vahan.form.impl.NonUseImpl;
import nic.vahan.form.impl.TaxServer_Impl;
import nic.vahan.form.impl.admin.DelDupRegnNoImpl;

/**
 *
 * @author tranC106
 */
@ManagedBean(name = "taxClearBean")
@ViewScoped
public class TaxClearBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(TaxClearBean.class);
    private TaxClearDobj taxClearDobj = new TaxClearDobj();
    private TaxClearDobj taxclear_dobj_prv = null;
    private List<TaxClearDobj> taxDetaillist = new ArrayList();
    private List<TaxClearDobj> taxDetaillistHistory = new ArrayList();
    private List<TaxClearDobj> taxDifferenceDetaillist = new ArrayList();
    private Map<String, Integer> purCodeList = null;
    private String taxClearUptoDescr;
    private List<ComparisonBean> prevChangedDataList;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private Date maxDate = new Date();
    private Date maxDateUpto = new Date();
    private Date regnDate;
    List pur_cd = new ArrayList();
    List selectedPur_cd = new ArrayList();
    private boolean disableTaxType;
    boolean orflag = false;
    boolean showSavePanel = false;
    boolean renderTaxPanel = false;
    boolean renderClearPanel = false;
    boolean disableChaeckBox = false;
    List<RefundAndExcessDobj> ref_list = new ArrayList<>();
    List<RefundAndExcessDobj> old_ref_list = new ArrayList<>();
    FeeImpl feeImpl = new FeeImpl();
    private Date minDate = DateUtil.parseDate(DateUtil.getCurrentDate());
    private Date minDateForClear = DateUtil.parseDate(DateUtil.getCurrentDate());
    private int vh_class;
    private List<OwnerDetailsDobj> dupRegnList = new ArrayList<>();
    private int currentOffCd = 0;
    private List<Status_dobj> statusList = new ArrayList<>();
    private OwnerDetailsDobj moveToHistoryOwnerDtls = null;
    private String currentOfficeName;
    private boolean renderMoveToHistoryButton = false;

    public TaxClearBean() {
        if (Util.getSelectedSeat() != null) {
            currentOffCd = Util.getSelectedSeat().getOff_cd();
        }
        if (currentOffCd == 0) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Something Went Wrong. Please try again after some time..."));
            return;
        }

        try {
            if (getAppl_details().getOwnerDobj() != null) {
                TaxClearImpl tci = new TaxClearImpl();
                getAppl_details().setTaxValidityExpired(false);
                orflag = Util.getUserStateCode().equalsIgnoreCase("OR") ? true : false;
                String taxMode = "";
                Map<String, String> taxModeMap = new HashMap<>();
                int addYear = 99;
                disableTaxType = true;
                TaxClearImpl impl = new TaxClearImpl();
                OwnerImpl ow_impl = new OwnerImpl();
                OwnerDetailsDobj ownerDetail = ow_impl.getOwnerDetails(getAppl_details().getRegn_no(), getAppl_details().getCurrent_state_cd(), getAppl_details().getCurrent_off_cd());
                if (ownerDetail != null) {
                    if (purCodeList != null) {
                        purCodeList.clear();
                    }
                    Owner_dobj dobj = ow_impl.getOwnerDobj(ownerDetail);
                    PassengerPermitDetailDobj permitDob = TaxServer_Impl.getPermitInfoForSavedTax(dobj);
                    purCodeList = impl.getAllowedPurCodeDescr(dobj, permitDob);
                    for (Map.Entry<String, Integer> entry : purCodeList.entrySet()) {
                        SelectItem item = new SelectItem(entry.getValue(), entry.getKey());
                        pur_cd.add(item);
                    }
                    vh_class = dobj.getVh_class();
                    ref_list = impl.getRefundAndExcessDetails(getAppl_details().getAppl_no(), getAppl_details().getRegn_no(), getAppl_details().getCurrent_state_cd(), getAppl_details().getCurrent_off_cd(), TableConstants.TAX_CLEAR_PUR_CD);

                    for (RefundAndExcessDobj ref_dobj : ref_list) {
                        renderTaxPanel = true;
                        RefundAndExcessDobj old = (RefundAndExcessDobj) ref_dobj.clone();
                        ref_dobj.getPur_cd_list().addAll(pur_cd);
                        old_ref_list.add(old);
                    }

                    // ref_list.add(ref_dobj);
                    regnDate = dobj.getRegn_dt();
                    //start of check for Tax Mode for L,O,S
                    taxModeMap = tci.taxModeInfo(getAppl_details().getRegn_no());
                    if (taxModeMap != null) {

                        for (Map.Entry<String, String> entry : taxModeMap.entrySet()) {
                            if (entry.getKey().equalsIgnoreCase("tax_mode")) {
                                taxMode = entry.getValue();
                            }
                        }

                        if (taxMode != null) {
                            if (taxMode.equalsIgnoreCase("O") || taxMode.equalsIgnoreCase("L") || taxMode.equalsIgnoreCase("S")) {
                                Date maxClearTo = ServerUtil.dateRange(JSFUtils.getStringToDateyyyyMMdd(ownerDetail.getPurchase_dt()), addYear, 0, -1);
                                setMaxDateUpto(maxClearTo);
                            } else {
                                setMaxDateUpto(null);
                            }
                        }
                    } //end of check for Tax Mode for L,O,S
                }
                List<TaxClearDobj> taxClearDobjList = impl.getTaxClearDetails(getAppl_details().getRegn_no(), getAppl_details().getAppl_no());
                List temp_pur_cd = new ArrayList();
                if (!taxClearDobjList.isEmpty()) {

                    for (TaxClearDobj dobj : taxClearDobjList) {
                        taxClearDobj = new TaxClearDobj();
                        taxClearDobj.setState_cd(dobj.getState_cd());
                        taxClearDobj.setOff_cd(dobj.getOff_cd());
                        taxClearDobj.setAppl_no(dobj.getAppl_no());
                        taxClearDobj.setRegnno(dobj.getRegnno());
                        taxClearDobj.setPur_cd(dobj.getPur_cd());
                        taxClearDobj.setClear_fr(dobj.getClear_fr());
                        taxClearDobj.setTaxclearuptodt(dobj.getTaxclearuptodt());
                        taxClearDobj.setOrderno(dobj.getOrderno());
                        taxClearDobj.setClearby(dobj.getClearby());
                        taxClearDobj.setOrderdt(dobj.getOrderdt());
                        taxClearDobj.setRemarks(dobj.getRemarks());
                        taxClearDobj.setRecieptNo(dobj.getRecieptNo());
                        taxClearDobj.setRecieptDate(dobj.getRecieptDate());
                        minDateForClear = dobj.getClear_fr();
                        temp_pur_cd.add(dobj.getPur_cd());
                        renderClearPanel = true;
                    }
                } else {
                    taxClearDobj = new TaxClearDobj();
                    taxClearDobj.setRegnno(getAppl_details().getRegn_no());
                    taxClearDobj.setAppl_no(getAppl_details().getAppl_no());
                }
                setSelectedPur_cd(temp_pur_cd);
                taxclear_dobj_prv = (TaxClearDobj) taxClearDobj.clone();//for holding current dobj for using in the comparison.
                taxDetaillist = TaxClearImpl.getTaxDetaillist(taxClearDobj, purCodeList);
                taxDetaillistHistory = TaxClearImpl.getHistoryTaxDetaillist(taxClearDobj, purCodeList);
                taxDifferenceDetaillist = TaxClearImpl.getDiffOfTaxDetailsPrintDobj(taxClearDobj);
                render = true;
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                disableChaeckBox = true;
            }
        } catch (VahanException vex) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Error-Could not Processed due to Technical Error in Database."));
            LOGGER.error(vex);
        } catch (Exception ex) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Error-Could not Processed due to Technical Error in Database."));
        }

    }

    public String validateRegistration() {
        OwnerImpl impl = new OwnerImpl();
        TaxClearImpl tci = new TaxClearImpl();
        String taxMode = "";
        Map<String, String> taxModeMap = new HashMap<>();
        int addYear = 99;
        String msg = "";
        showSavePanel = false;
        OwnerDetailsDobj ownDtls = null;
        moveToHistoryOwnerDtls = null;
        try {
            boolean isblacklistedvehicle;
            TmConfigurationDobj tmConfig = Util.getTmConfiguration();
            isblacklistedvehicle = ServerUtil.isPurposeCodeOnBlacklistedVehicle(taxClearDobj.getRegnno(), Util.getSelectedSeat().getAction_cd(), Util.getUserStateCode(), tmConfig);
            if (isblacklistedvehicle) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "Vehicle No. has been Blacklisted."));
                return null;
            }

            //For restricting the user to generate application no again and again.start           
//            List<Status_dobj> statusList = ServerUtil.applicationStatus(taxClearDobj.getRegnno());
//            if (!statusList.isEmpty()) {
//                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!",
//                        "Vehicle is already Pending for Approval with Generated Application No (" + statusList.get(0).getAppl_no() + ") against Registration No (" + statusList.get(0).getRegn_no() + ")"));
//                return;
//
//            }
            //For restricting the user to generate application no again and again.end
            //start of check for Tax Mode for L,O,S
            taxModeMap = tci.taxModeInfo(taxClearDobj.getRegnno());
            if (taxModeMap != null) {

                for (Map.Entry<String, String> entry : taxModeMap.entrySet()) {
                    if (entry.getKey().equalsIgnoreCase("tax_mode")) {
                        taxMode = entry.getValue();
                    }
                }
            } //end of check for Tax Mode for L,O,S

            List<OwnerDetailsDobj> listOwnerDetails = impl.getOwnerDetailsList(taxClearDobj.getRegnno(), Util.getUserStateCode());

            if (listOwnerDetails == null || listOwnerDetails.isEmpty()) {
                throw new VahanException("Either Invalid vehicle Registration No or You are not authorized to clear Tax for this vehicle no.");
            } else if (listOwnerDetails.size() > 0) {

                if (listOwnerDetails.size() == 1) {
                    ownDtls = listOwnerDetails.get(0);

                    if (ownDtls.getOff_cd() != currentOffCd) {
                        throw new VahanException("You are not authorized to clear Tax for this vehicle no. As it does not Belong to This RTO.");
                    }
                    if (!"A,Y".contains(ownDtls.getStatus())) {
                        throw new VahanException("Tax can not be cleared for this vehicle. As Vehicle is not Active.");
                    }

                    if (purCodeList != null) {
                        purCodeList.clear();
                    }

                    if ("OR".equalsIgnoreCase(ownDtls.getState_cd())) {
                        boolean isNonUse = NonUseImpl.nonUseDetailsExist(ownDtls.getRegn_no(), ownDtls.getState_cd());
                        if (isNonUse) {
                            FacesMessage message = null;
                            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Vehicle is in NonUse . Please Restore/ Remove Vehicle From NonUse!", "Vehicle is in NonUse . Please Restore/ Remove Vehicle From NonUse!!");
                            FacesContext.getCurrentInstance().addMessage(null, message);
                            return null;
                        }
                    }
                    Owner_dobj dobj = impl.getOwnerDobj(ownDtls);
                    PassengerPermitDetailDobj permitDob = TaxServer_Impl.getPermitInfoForSavedTax(dobj);
                    purCodeList = tci.getAllowedPurCodeDescr(dobj, permitDob);
                    showDetails(purCodeList);
                    disableTaxType = false;
                    regnDate = dobj.getRegn_dt();
                    if (taxMode != null) {
                        if (taxMode.equalsIgnoreCase("O") || taxMode.equalsIgnoreCase("L") || taxMode.equalsIgnoreCase("S")) {
                            Date maxClearTo = ServerUtil.dateRange(JSFUtils.getStringToDateyyyyMMdd(ownDtls.getPurchase_dt()), addYear, 0, -1);
                            setMaxDateUpto(maxClearTo);
                        } else {
                            setMaxDateUpto(null);
                        }
                    }
                    orflag = Util.getUserStateCode().equals("OR") ? true : false;
                    showSavePanel = true;

                } else if (listOwnerDetails.size() >= 2) {

                    int ownerDetailsCounts = 0;
                    int ownerDetailsCountsNoc = 0;
                    boolean isSameOffice = false;
                    for (int i = 0; i < listOwnerDetails.size(); i++) {

                        if (listOwnerDetails.get(i).getOff_cd() == Util.getUserLoginOffCode()) {
                            isSameOffice = true;
                        }
                        if ("N".equalsIgnoreCase(listOwnerDetails.get(i).getStatus())) {
                            ownerDetailsCountsNoc++;
                        }
                        if (!"N".equalsIgnoreCase(listOwnerDetails.get(i).getStatus())) {
                            ownerDetailsCounts++;
                        }
                    }
                    if (!isSameOffice) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Registration Number not found in Selected Office"));
                        return null;
                    }
                    setDupRegnList(listOwnerDetails);
                    if ("WB".contains(Util.getUserStateCode())) {
                        setRenderMoveToHistoryButton(true);
                    }
                    PrimeFaces.current().executeScript("PF('varDupRegNo').show()");
                    return null;
                }
            }
        } catch (VahanException ex) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", ex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Error-Could not Processed due to Technical Error in Database."));
        }
        return null;
    }

    public void delDuplicateVehDetails(OwnerDetailsDobj ownerDtls) {
        try {
            //For restricting the user to generate application no again and again.start           
            setStatusList(ServerUtil.applicationStatus(ownerDtls.getRegn_no()));
            if (statusList != null && !statusList.isEmpty()) {
                PrimeFaces.current().executeScript("PF('varInwardedApplNo').show()");
                return;
            }
            if ("TN".equals(ownerDtls.getState_cd()) && getMoveToHistoryOwnerDtls() == null && ownerDtls != null && ownerDtls.getHpaDobj() != null && getAppl_details() != null) {
                List<HpaDobj> hypth = new HpaImpl().getHypothecationList(ownerDtls.getRegn_no(), ownerDtls.getState_cd(), currentOffCd);
                if (hypth == null || hypth.isEmpty()) {
                    setMoveToHistoryOwnerDtls(ownerDtls);
                    setCurrentOfficeName(ServerUtil.getOfficeName(currentOffCd, ownerDtls.getState_cd()));
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().ajax().update("confirmDialog");
                    PrimeFaces.current().executeScript("PF('confirmDialogVar').show()");
                    return;
                }
            }
            boolean isDelete = new DelDupRegnNoImpl().deleteDuplicateVehicle(ownerDtls);
            if (isDelete) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Registration Number Deleted Successfully"));
                PrimeFaces.current().executeScript("PF('varDupRegNo').hide()");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void moveHistoryDuplicateVehDetails(String moveHPAType) {
        try {
            if (getMoveToHistoryOwnerDtls() != null) {
                getMoveToHistoryOwnerDtls().setMoveHPADetails(moveHPAType);
                boolean isDelete = new DelDupRegnNoImpl().deleteDuplicateVehicle(getMoveToHistoryOwnerDtls());
                if (isDelete) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Registration Number Deleted Successfully"));
                    PrimeFaces.current().executeScript("PF('varDupRegNo').hide()");
                    PrimeFaces.current().executeScript("PF('confirmDialogVar').hide()");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void showDetails(Map<String, Integer> purCodeList) {

        try {
            if (pur_cd != null) {
                pur_cd.clear();
            }

            taxDetaillist = TaxClearImpl.getTaxDetaillist(taxClearDobj, purCodeList);
            taxDifferenceDetaillist = TaxClearImpl.getDiffOfTaxDetailsPrintDobj(taxClearDobj);

            if (taxDetaillist.isEmpty()) {
                // For Vehicles from other RTO, details only present in VT_OWNER
                taxDetaillistHistory = TaxClearImpl.getHistoryTaxDetaillist(taxClearDobj, purCodeList);
                render = true;
//                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", "There is no Tax Entry in the Database!!!"));
            } else {
                taxDetaillistHistory = TaxClearImpl.getHistoryTaxDetaillist(taxClearDobj, purCodeList);
                render = true;
                taxClearUptoDescr = taxDetaillist.get(0).getDescrold();
            }

            for (Map.Entry<String, Integer> entry : purCodeList.entrySet()) {
                SelectItem item = new SelectItem(entry.getValue(), entry.getKey());
                pur_cd.add(item);
            }

            RefundAndExcessDobj ref_dobj = new RefundAndExcessDobj();
            ref_dobj.getPur_cd_list().addAll(pur_cd);
            ref_list.add(ref_dobj);

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Error-Could not Processed due to Technical Error in Database."));
        }

    }

    public void addNewRowTaxDiff(RefundAndExcessDobj dobj) {
        Integer purCd = dobj.getPur_cd();
        String appl_no = appl_details.getAppl_no();
        String mode = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("actionmode");
        if ("addTaxDiff".equalsIgnoreCase(mode)) {
            if (purCd == null || purCd == -1) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Select Excess And Refund Tax Head!", "Select Excess And Refund Tax Head!");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                return;
            }
            // add row in the panel
            if (getRef_list().size() == 7) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Maximum number of Excess And Refund Tax heads collection reached", "Maximum number of Tax heads collection reached");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            } else {
                RefundAndExcessDobj dotd = new RefundAndExcessDobj();
                dotd.getPur_cd_list().addAll(pur_cd);
                getRef_list().add(dotd);
            }

        } else if ("minusTaxDiff".equalsIgnoreCase(mode)) {
            // remove current row from table.
            boolean isExist = feeImpl.checkPurCDExist(appl_no, purCd);
            if (isExist) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Excess And Refund Tax Details can't be remove!", "Excess And Refund Tax Details can't be remove!");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                return;
            }
            RefundAndExcessDobj selectedtaxObject = new RefundAndExcessDobj(purCd);
            int lastIndex = getRef_list().lastIndexOf(selectedtaxObject);
            if (lastIndex == 0 && getRef_list().size() == 1) {
                getRef_list().clear();
                RefundAndExcessDobj dotd = new RefundAndExcessDobj();
                getRef_list().add(dotd);
            } else {
                getRef_list().remove(lastIndex);
            }
        }
        //RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().ajax().update("sub_view_Refund_Exess:tableRefAndExcess");
    }

    public void listnerUpdateUptoForBalance(RefundAndExcessDobj bean) {
        setMinDate(bean.getTaxFrom());
        bean.setTaxUpto(bean.getTaxFrom());
    }

    public void listnerUpdateUptoForTaxClear() {
        setMinDateForClear(taxClearDobj.getClear_fr());
        taxClearDobj.setTaxclearuptodt(taxClearDobj.getClear_fr());
    }

    public void selectTaxListner(int pur_code) {
        try {
            //taxClearDobj.getRegnno()
            TaxClearImpl impl = new TaxClearImpl();
            boolean isExist = impl.getDetailsOfBalanceTax(pur_code, taxClearDobj.getRegnno());
            RefundAndExcessDobj selectedTaxObject = new RefundAndExcessDobj(pur_code);
            selectedTaxObject.getPur_cd_list().addAll(pur_cd);
            int lastIndex = getRef_list().lastIndexOf(selectedTaxObject);
            getRef_list().remove(lastIndex);
            if (!isExist) {
                if (!getRef_list().contains(selectedTaxObject)) {
                    getRef_list().add(selectedTaxObject);
                } else {
                    FacesMessage message = null;
                    message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Tax Head Already Selected!", "Tax Head Already Selected!");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return;
                }
            } else {
                selectedTaxObject.setPur_cd(-1);
                getRef_list().add(selectedTaxObject);
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "More Then One Balance Tax Entry Not Allowed .Please Pay The Balance First", "More Then One Balance Tax Entry Not Allowed .Please Pay The Balance First");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        }
    }

    public void savetaxclear() {
        String appl_no = null;
        String errorMsg = null;
        if (!renderClearPanel && !renderTaxPanel) {
            JSFUtils.setFacesMessage("Please select Atleast  One Purpose Either Tax Clear Or Refund/Excess ", "Please select Atleast  One Purpose Either Tax Clear Or Refund/Excess", JSFUtils.FATAL);
            return;
        }
        if (renderTaxPanel) {
            for (RefundAndExcessDobj taxDiffList : getRef_list()) {
                if (taxDiffList.getPur_cd() == -1 && (taxDiffList.getExcessAmt() > 0 || taxDiffList.getBalanceAmt() > 0 || taxDiffList.getRefundAmt() > 0)) {
                    JSFUtils.setFacesMessage("Please select Tax type", "Please select Tax type", JSFUtils.INFO);
                    return;
                } else if (taxDiffList.getPur_cd() != -1 && (taxDiffList.getExcessAmt() <= 0 && taxDiffList.getBalanceAmt() <= 0 && taxDiffList.getRefundAmt() <= 0)) {
                    JSFUtils.setFacesMessage("Tax Head Amount Type Can't be Less Then or Equal to Zero ", "Tax Head Amount Type Can't be Less Then or Equal to Zero", JSFUtils.INFO);
                    return;
                } else if (taxDiffList.getPur_cd() == -1 && (taxDiffList.getExcessAmt() < 0 || taxDiffList.getBalanceAmt() < 0 || taxDiffList.getRefundAmt() < 0)) {
                    JSFUtils.setFacesMessage("Please select Tax type", "Please select Tax type", JSFUtils.INFO);
                    return;
                }
            }
        }
        try {

            Status_dobj status_dobj = new Status_dobj();
            status_dobj.setRegn_no(taxClearDobj.getRegnno());
            status_dobj.setPur_cd(TableConstants.TAX_CLEAR_PUR_CD);
            status_dobj.setFlow_slno(1);//initial flow serial no.
            status_dobj.setFile_movement_slno(1);//initial file movement serial no.
            status_dobj.setState_cd(Util.getUserStateCode());
            status_dobj.setOff_cd(getAppl_details().getCurrent_off_cd());
            status_dobj.setEmp_cd(0);
            status_dobj.setSeat_cd("");
            status_dobj.setCntr_id("");
            status_dobj.setStatus("N");
            status_dobj.setOffice_remark("");
            status_dobj.setPublic_remark("");
            status_dobj.setFile_movement_type("F");
            status_dobj.setUser_id(Util.getUserId());
            status_dobj.setAppl_date(ServerUtil.getSystemDateInPostgres());
            status_dobj.setUser_type("");
            status_dobj.setEntry_ip("");
            status_dobj.setEntry_status("");//for New File
            status_dobj.setConfirm_ip("");
            status_dobj.setConfirm_status("");
            status_dobj.setConfirm_date(new java.util.Date());

            TaxClearImpl impl = new TaxClearImpl();
            //taxClearDobj.setClear_fr(regnDate);
            appl_no = impl.InsertIntoVATaxClearInward(status_dobj, taxClearDobj, selectedPur_cd, ref_list, renderClearPanel, renderTaxPanel);
        } catch (VahanException ve) {
            errorMsg = ve.getMessage();
        } catch (Exception ex) {
            errorMsg = "Something went wrong, please try again.";
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }

        if (errorMsg != null) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", errorMsg));
            return;
        }

        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Application Number is Generated Successfully.Application Number is : " + appl_no));
        taxClearDobj = new TaxClearDobj();
        purCodeList = null;
        taxDetaillist.clear();
        taxDetaillistHistory.clear();
        selectedPur_cd.clear();
        pur_cd.clear();
        showSavePanel = false;
        getAppl_details().setOwnerDobj(null);
        render = false;
        PrimeFaces.current().ajax().update("formVehDetails");
    }

    @Override
    public String save() {
        String return_location = "";

        try {
            List<ComparisonBean> compareChanges = compareChanges();

            if (!compareChanges.isEmpty() || taxclear_dobj_prv == null) { //save only when data is really changed by user
                TaxClearImpl.makeChangeTaxClear(taxClearDobj, ComparisonBeanImpl.changedDataContents(compareChanges), selectedPur_cd, ref_list, renderClearPanel, renderTaxPanel);
            }

            return_location = "seatwork";

        } catch (VahanException vme) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", vme.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Error-File Could Not Save and Move Due to Technical Error in Database"));
        }
        return return_location;
    }

    @Override
    public List<ComparisonBean> compareChanges() {

        if (taxclear_dobj_prv == null) {
            return compBeanList;
        }

        compBeanList.clear();
        Compare("taxClearUpto", taxclear_dobj_prv.getTaxclearuptodt(), taxClearDobj.getTaxclearuptodt(), compBeanList);
        Compare("clearBy", taxclear_dobj_prv.getClearby(), taxClearDobj.getClearby(), compBeanList);
        Compare("orderNo", taxclear_dobj_prv.getOrderno(), taxClearDobj.getOrderno(), compBeanList);
        Compare("orderDt", taxclear_dobj_prv.getOrderdt(), taxClearDobj.getOrderdt(), compBeanList);
        Compare("remarks", taxclear_dobj_prv.getRemarks(), taxClearDobj.getRemarks(), compBeanList);
        compareRefundAndExcess("Tax Refund And Excess", getRef_list(), getOld_ref_list(), compBeanList);
        Compare("receiptNo", taxclear_dobj_prv.getRecieptNo(), taxClearDobj.getRecieptNo(), compBeanList);
        Compare("receiptDate", taxclear_dobj_prv.getRecieptDate(), taxClearDobj.getRecieptDate(), compBeanList);

        return getCompBeanList();
    }

    @Override
    public String saveAndMoveFile() {
        String return_location = "";

        try {
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(getAppl_details().getAppl_dt());
            status.setAppl_no(getAppl_details().getAppl_no());
            status.setPur_cd(getAppl_details().getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());

            status.setCurrent_role(appl_details.getCurrent_action_cd());

            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                    || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)
                    || getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                TaxClearImpl taxclear_impl = new TaxClearImpl();
                taxclear_impl.update_TAXCLEAR_Status(taxClearDobj, taxclear_dobj_prv, status, ComparisonBeanImpl.changedDataContents(compareChanges()), selectedPur_cd, ref_list, renderClearPanel, renderTaxPanel, vh_class);

                if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                    return_location = disapprovalPrint();
                } else {
                    return_location = "seatwork";
                }
            }
        } catch (VahanException vme) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", vme.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Error-File Could Not Save and Move Due to Technical Error in Database"));
        }
        return return_location;

    }

    @Override
    public List<ComparisonBean> getCompBeanList() {
        return this.compBeanList;
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        return this.prevChangedDataList;
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
    }

    @Override
    public void saveEApplication() {
    }

    /**
     * @return the taxDetaillist
     */
    public List<TaxClearDobj> getTaxDetaillist() {
        return taxDetaillist;
    }

    /**
     * @param taxDetaillist the taxDetaillist to set
     */
    public void setTaxDetaillist(List<TaxClearDobj> taxDetaillist) {
        this.taxDetaillist = taxDetaillist;
    }

    /**
     * @return the taxDetaillistHistory
     */
    public List<TaxClearDobj> getTaxDetaillistHistory() {
        return taxDetaillistHistory;
    }

    /**
     * @param taxDetaillistHistory the taxDetaillistHistory to set
     */
    public void setTaxDetaillistHistory(List<TaxClearDobj> taxDetaillistHistory) {
        this.taxDetaillistHistory = taxDetaillistHistory;
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
     * @return the taxClearDobj
     */
    public TaxClearDobj getTaxClearDobj() {
        return taxClearDobj;
    }

    /**
     * @param taxClearDobj the taxClearDobj to set
     */
    public void setTaxClearDobj(TaxClearDobj taxClearDobj) {
        this.taxClearDobj = taxClearDobj;
    }

    /**
     * @return the taxClearUptoDescr
     */
    public String getTaxClearUptoDescr() {
        return taxClearUptoDescr;
    }

    /**
     * @param taxClearUptoDescr the taxClearUptoDescr to set
     */
    public void setTaxClearUptoDescr(String taxClearUptoDescr) {
        this.taxClearUptoDescr = taxClearUptoDescr;
    }

    /**
     * @return the maxDate
     */
    public Date getMaxDate() {
        return maxDate;
    }

    /**
     * @param maxDate the maxDate to set
     */
    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    /**
     * @return the regnDate
     */
    public Date getRegnDate() {
        return regnDate;
    }

    /**
     * @param regnDate the regnDate to set
     */
    public void setRegnDate(Date regnDate) {
        this.regnDate = regnDate;
    }

    public List<TaxClearDobj> getTaxDifferenceDetaillist() {
        return taxDifferenceDetaillist;
    }

    public void setTaxDifferenceDetaillist(List<TaxClearDobj> taxDifferenceDetaillist) {
        this.taxDifferenceDetaillist = taxDifferenceDetaillist;
    }

    public List getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(List pur_cd) {
        this.pur_cd = pur_cd;
    }

    public List getSelectedPur_cd() {
        return selectedPur_cd;
    }

    public void setSelectedPur_cd(List selectedPur_cd) {
        this.selectedPur_cd = selectedPur_cd;
    }

    public boolean isDisableTaxType() {
        return disableTaxType;
    }

    public void setDisableTaxType(boolean disableTaxType) {
        this.disableTaxType = disableTaxType;
    }

    public Date getMaxDateUpto() {
        return maxDateUpto;
    }

    public void setMaxDateUpto(Date maxDateUpto) {
        this.maxDateUpto = maxDateUpto;
    }

    public List<RefundAndExcessDobj> getRef_list() {
        return ref_list;
    }

    public void setRef_list(List<RefundAndExcessDobj> ref_list) {
        this.ref_list = ref_list;
    }

    public List<RefundAndExcessDobj> getOld_ref_list() {
        return old_ref_list;
    }

    public void setOld_ref_list(List<RefundAndExcessDobj> old_ref_list) {
        this.old_ref_list = old_ref_list;
    }

    public boolean isOrflag() {
        return orflag;
    }

    public void setOrflag(boolean orflag) {
        this.orflag = orflag;
    }

    public boolean isShowSavePanel() {
        return showSavePanel;
    }

    public void setShowSavePanel(boolean showSavePanel) {
        this.showSavePanel = showSavePanel;
    }

    public boolean isRenderTaxPanel() {
        return renderTaxPanel;
    }

    public void setRenderTaxPanel(boolean renderTaxPanel) {
        this.renderTaxPanel = renderTaxPanel;
    }

    public boolean isRenderClearPanel() {
        return renderClearPanel;
    }

    public void setRenderClearPanel(boolean renderClearPanel) {
        this.renderClearPanel = renderClearPanel;
    }

    public boolean isDisableChaeckBox() {
        return disableChaeckBox;
    }

    public void setDisableChaeckBox(boolean disableChaeckBox) {
        this.disableChaeckBox = disableChaeckBox;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    public Date getMinDateForClear() {
        return minDateForClear;
    }

    public void setMinDateForClear(Date minDateForClear) {
        this.minDateForClear = minDateForClear;
    }

    /**
     * @return the dupRegnList
     */
    public List<OwnerDetailsDobj> getDupRegnList() {
        return dupRegnList;
    }

    /**
     * @param dupRegnList the dupRegnList to set
     */
    public void setDupRegnList(List<OwnerDetailsDobj> dupRegnList) {
        this.dupRegnList = dupRegnList;
    }

    /**
     * @return the statusList
     */
    public List<Status_dobj> getStatusList() {
        return statusList;
    }

    /**
     * @param statusList the statusList to set
     */
    public void setStatusList(List<Status_dobj> statusList) {
        this.statusList = statusList;
    }

    /**
     * @return the currentOffCd
     */
    public int getCurrentOffCd() {
        return currentOffCd;
    }

    /**
     * @param currentOffCd the currentOffCd to set
     */
    public void setCurrentOffCd(int currentOffCd) {
        this.currentOffCd = currentOffCd;
    }

    /**
     * @return the moveToHistoryOwnerDtls
     */
    public OwnerDetailsDobj getMoveToHistoryOwnerDtls() {
        return moveToHistoryOwnerDtls;
    }

    /**
     * @param moveToHistoryOwnerDtls the moveToHistoryOwnerDtls to set
     */
    public void setMoveToHistoryOwnerDtls(OwnerDetailsDobj moveToHistoryOwnerDtls) {
        this.moveToHistoryOwnerDtls = moveToHistoryOwnerDtls;
    }

    /**
     * @return the currentOfficeName
     */
    public String getCurrentOfficeName() {
        return currentOfficeName;
    }

    /**
     * @param currentOfficeName the currentOfficeName to set
     */
    public void setCurrentOfficeName(String currentOfficeName) {
        this.currentOfficeName = currentOfficeName;
    }

    /**
     * @return the renderMoveToHistoryButton
     */
    public boolean isRenderMoveToHistoryButton() {
        return renderMoveToHistoryButton;
    }

    /**
     * @param renderMoveToHistoryButton the renderMoveToHistoryButton to set
     */
    public void setRenderMoveToHistoryButton(boolean renderMoveToHistoryButton) {
        this.renderMoveToHistoryButton = renderMoveToHistoryButton;
    }
}
