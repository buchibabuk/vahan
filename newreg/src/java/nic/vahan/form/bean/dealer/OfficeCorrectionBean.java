package nic.vahan.form.bean.dealer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.user_mgmt.dobj.DealerMasterDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.impl.ApplicationDisposeImpl;
import nic.vahan.form.impl.Home_Impl;
import nic.vahan.form.impl.NewImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.dealer.OfficeCorrectionImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.configuration.TmConfigurationApplInwardDobj;
import nic.vahan.form.impl.ApplicationInwardImpl;
import nic.vahan.server.CommonUtils;

/**
 *
 * @author pramod-nicsi
 */
@ManagedBean(name = "officeCorrection")
@ViewScoped
public class OfficeCorrectionBean implements Serializable {

    private String applNo;
    private boolean showOfficeChangeVisible = false;
    private boolean showApplPanelvisible = true;
    private int newOffCd;
    private String changeReason;
    private OwnerDetailsDobj ownerDetailsDobj;
    private String officeName;
    private int oldOffCode;
    private String stateCd;
    private Long userCd;
    private Map<String, Object> allotedOfficeList;
    private SessionVariables sessionVariables = null;
    private static Logger LOGGER = Logger.getLogger(OfficeCorrectionBean.class);
    private TmConfigurationDobj configDobj = null;
    private boolean temporaryFlow = false;

    public OfficeCorrectionBean() {
        try {
            sessionVariables = new SessionVariables();
            if (sessionVariables == null || sessionVariables.getStateCodeSelected() == null
                    || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getEmpCodeLoggedIn() == null
                    || sessionVariables.getUserCatgForLoggedInUser() == null) {
                throw new VahanException(TableConstants.SomthingWentWrong);
            } else {
                stateCd = sessionVariables.getStateCodeSelected();
                oldOffCode = sessionVariables.getOffCodeSelected();
                userCd = Long.parseLong(sessionVariables.getEmpCodeLoggedIn());
                configDobj = Util.getTmConfiguration();
            }
        } catch (VahanException ve) {
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void validateApplicationNo() {
        String dealerCd = null;
        String fancyNo = null;
        String advanceRetenNo = null;
        int actionCd = 0;
        boolean isPayDone = false;
        boolean isWorkDoneAtOffice = false;
        temporaryFlow = false;
        boolean isShowCorrectionPanel = false;
        int applicationSize = 0;
        List<Status_dobj> onlineApplDataList = null;
        TmConfigurationApplInwardDobj tmConfigurationApplInwardDobj = null;
        try {
            String userCatg = sessionVariables.getUserCatgForLoggedInUser();
            oldOffCode = sessionVariables.getOffCodeSelected();
            ownerDetailsDobj = new OwnerImpl().getVaOwnerDetails(applNo.toUpperCase(), stateCd, oldOffCode);
            ArrayList<Status_dobj> applStatus = ServerUtil.applicationStatusByApplNo(applNo, stateCd);

            if (TableConstants.USER_CATG_DEALER.equals(userCatg) && !new Home_Impl().getDealerAuthority(userCd)) {
                throw new VahanException("Can't change the office, As you are not authorized for all offices.");
            }
            if (userCd != null && ownerDetailsDobj != null && applStatus != null && !applStatus.isEmpty()
                    && (applStatus.get(0).getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || applStatus.get(0).getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE)
                    && (ownerDetailsDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_NEW) || ownerDetailsDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_TEMPORARY))) {
                if (ownerDetailsDobj.getOff_cd() == oldOffCode) {
                    if (configDobj != null && configDobj.isDealer_auth_for_all_office()) {
                        applicationSize = applStatus.size();
                        dealerCd = ownerDetailsDobj.getDealer_cd();
                        allotedOfficeList = Home_Impl.getAllotedOfficeCodeDescr(true);
                        actionCd = ApplicationDisposeImpl.getActionCode(applNo);
                        fancyNo = NewImpl.getAdvanceRegnNo(applNo);
                        advanceRetenNo = NewImpl.getAdvanceRetenNo(applNo);
                        isWorkDoneAtOffice = OfficeCorrectionImpl.isWorkDoneAtOffice(applNo, stateCd, oldOffCode, applStatus.get(0).getPur_cd());
                        if (advanceRetenNo == null && fancyNo == null) {
                            if (actionCd == TableConstants.TM_ROLE_DEALER_CART_PAYMENT) {
                                throw new VahanException("Application is Pending at Cart Payment.Please Rollback the Application  for office Correction !");
                            }
                            if (!isWorkDoneAtOffice) {
                                switch (userCatg) {
                                    case TableConstants.USER_CATG_DEALER:
                                        dealerCd = ServerUtil.getDealerCode(userCd, stateCd, oldOffCode);
                                        isPayDone = new ApplicationDisposeImpl().dealerCheckForDispose(applNo);
                                        if (!CommonUtils.isNullOrBlank(dealerCd) && dealerCd.equals(ownerDetailsDobj.getDealer_cd())) {
                                            if (configDobj.getTmConfigDealerDobj().isOffCorrectionAtOffice()) {
                                                if (!isPayDone) {
                                                    isShowCorrectionPanel = true;
                                                } else {
                                                    throw new VahanException("As payment has been made. Kindly contact to office.");
                                                }
                                            } else if (!configDobj.getTmConfigDealerDobj().isOffCorrectionAtOffice()) {
                                                List assignedOffList = ServerUtil.getOffCode(String.valueOf(userCd));
                                                if (assignedOffList != null && !assignedOffList.isEmpty() && configDobj.isTempFeeInNewRegis() && configDobj.getTmConfigDealerDobj() != null && !configDobj.getTmConfigDealerDobj().isTempRegnFeeWithNewForSameOffices() && applStatus.get(0).getPur_cd() != TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                                                    for (Object offCd : assignedOffList) {
                                                        if (offCd != null && String.valueOf(oldOffCode).equals(String.valueOf(offCd)) && ServerUtil.getOfficeCdForDealerTempAppl(applNo, stateCd, "offCorrection") == 0) {
                                                            throw new VahanException("Application inwarded for HOME office, you can't change office.");
                                                        }
                                                    }
                                                }
                                                isShowCorrectionPanel = true;
                                            }
                                        } else {
                                            throw new VahanException("Not authorised for office Correction against this application no.");
                                        }
                                        break;
                                    case TableConstants.USER_CATG_OFF_STAFF:
                                        if (configDobj.isTempFeeInNewRegis() && configDobj.getTmConfigDealerDobj() != null && !configDobj.getTmConfigDealerDobj().isTempRegnFeeWithNewForSameOffices()) {
                                            if (ServerUtil.getOfficeCdForDealerTempAppl(applNo, stateCd, "offCorrection") != 0) {
                                                isShowCorrectionPanel = true;
                                            } else if (!CommonUtils.isNullOrBlank(OfficeCorrectionImpl.getRcptNoForFeePaidForPurpose(applNo, TableConstants.VM_TRANSACTION_MAST_TEMP_REG))) {
                                                if (applicationSize == 1 && applStatus.get(0).getPur_cd() != TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                                                    temporaryFlow = true;
                                                }
                                                isShowCorrectionPanel = true;
                                            } else {
                                                DealerMasterDobj dobj = ServerUtil.getDealerDetailsByDealerCode(dealerCd, stateCd);
                                                if (dobj != null) {
                                                    if (dobj.getOffCode() != oldOffCode) {
                                                        isShowCorrectionPanel = true;
                                                    } else {
                                                        throw new VahanException("Temporary Fee not paid. Kindly pay through Balance Fee.");
                                                    }
                                                } else {
                                                    throw new VahanException(TableConstants.SomthingWentWrong);
                                                }
                                            }
                                        } else {
                                            isShowCorrectionPanel = true;
                                        }
                                        break;
                                }
                                if (isShowCorrectionPanel) {
                                    setShowApplPanelvisible(false);
                                    setShowOfficeChangeVisible(true);
                                }
                            } else {
                                throw new VahanException("Some work initiated at office level, so can't change the office.");
                            }
                        } else {
                            throw new VahanException("Retention no./Fancy no. vehicle not allowed for office Correction.");
                        }
                    } else {
                        throw new VahanException("State is not permitted to change office.");
                    }
                } else {
                    throw new VahanException("You are not authorized to Office Correction for this Application no.");
                }
            } else if (applStatus != null && !applStatus.isEmpty()) {
                tmConfigurationApplInwardDobj = new ApplicationInwardImpl().getApplInwardAnywhereInStateConfig(stateCd);
                if (tmConfigurationApplInwardDobj != null && (tmConfigurationApplInwardDobj.getPur_code().contains(String.valueOf(TableConstants.VM_CHANGE_OF_ADDRESS_PUR_CD))
                        || tmConfigurationApplInwardDobj.getPur_code().contains(String.valueOf(TableConstants.VM_TRANSFER_OWNER_PUR_CD)))) {
                    onlineApplDataList = new OfficeCorrectionImpl().getOnlineApplicationDetail(applNo.toUpperCase());
                    if (onlineApplDataList != null && !onlineApplDataList.isEmpty()) {
                        if (onlineApplDataList.get(0).getOff_cd() == oldOffCode) {
                            ownerDetailsDobj = new OwnerImpl().getOwnerDetails(applStatus.get(0).getRegn_no(), stateCd, oldOffCode);
                            if (ownerDetailsDobj != null) {
                                allotedOfficeList = Home_Impl.getAllotedOfficeCodeDescr(true);
                                setShowApplPanelvisible(false);
                                setShowOfficeChangeVisible(true);
                            } else {
                                throw new VahanException("Invalid Application no./Owner Details not found.");
                            }
                        } else {
                            throw new VahanException("This Application did not applied for this Office.");
                        }
                    } else {
                        throw new VahanException("Invalid Application no./Allowed only for 'Change of Address' Or 'Transfer of OwnerShip' from Online Service.");
                    }
                } else {
                    throw new VahanException("Invalid Application no./State is not permitted to change office");
                }
            } else {
                throw new VahanException("Invalid Application no./Allowed only for Dealer Registration Applications.");
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "", ve.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "", e.getMessage()));
        }
    }

    public void changeOfficeAction() {
        Exception ex = null;
        try {
            officeName = ServerUtil.getOfficeName(newOffCd, stateCd);
            new OfficeCorrectionImpl().updateAllVhaRtoCorrection(applNo, newOffCd, changeReason, stateCd, userCd, oldOffCode, officeName, temporaryFlow, ownerDetailsDobj);//insert into all  vha tables            
            setOfficeName(officeName);
        } catch (VahanException vex) {
            ex = vex;
        } catch (Exception e) {
            ex = e;
        }
        if (ex != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert!", ex.getMessage()));
            LOGGER.error(ex.getStackTrace()[0].toString() + " " + ex.getMessage());
        } else {
            PrimeFaces.current().ajax().update("office_correction_view:officeDialog");
            PrimeFaces.current().executeScript("PF('officeCorrection_dlg').show()");
        }
    }

    public void selectedOfficeValidate() {
        if (ServerUtil.getVahan4StartDate(stateCd, newOffCd) == null && newOffCd != 0) {
            JSFUtils.showMessagesInDialog("Alert!", "Vahan 4.0 is not implemented in this RTO.", FacesMessage.SEVERITY_INFO);
            setNewOffCd(0);
            return;
        } else if (getOldOffCode() == newOffCd) {
            JSFUtils.showMessagesInDialog("Alert!", "You are already registered for selected RTO.", FacesMessage.SEVERITY_INFO);
            setNewOffCd(0);
            return;
        }
    }

    /**
     * @return the applNo
     */
    public String getApplNo() {
        return applNo;
    }

    /**
     * @param applNo the applNo to set
     */
    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    /**
     * @return the showOfficeChangeVisible
     */
    public boolean isShowOfficeChangeVisible() {
        return showOfficeChangeVisible;
    }

    /**
     * @param showOfficeChangeVisible the showOfficeChangeVisible to set
     */
    public void setShowOfficeChangeVisible(boolean showOfficeChangeVisible) {
        this.showOfficeChangeVisible = showOfficeChangeVisible;
    }

    /**
     * @return the changeReason
     */
    public String getChangeReason() {
        return changeReason;
    }

    /**
     * @param changeReason the changeReason to set
     */
    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }

    /**
     * @return the showApplPanelvisible
     */
    public boolean isShowApplPanelvisible() {
        return showApplPanelvisible;
    }

    /**
     * @param showApplPanelvisible the showApplPanelvisible to set
     */
    public void setShowApplPanelvisible(boolean showApplPanelvisible) {
        this.showApplPanelvisible = showApplPanelvisible;
    }

    /**
     * @return the ownerDetailsDobj
     */
    public OwnerDetailsDobj getOwnerDetailsDobj() {
        return ownerDetailsDobj;
    }

    /**
     * @param ownerDetailsDobj the ownerDetailsDobj to set
     */
    public void setOwnerDetailsDobj(OwnerDetailsDobj ownerDetailsDobj) {
        this.ownerDetailsDobj = ownerDetailsDobj;
    }

    /**
     * @return the newOffCd
     */
    public int getNewOffCd() {
        return newOffCd;
    }

    /**
     * @param newOffCd the newOffCd to set
     */
    public void setNewOffCd(int newOffCd) {
        this.newOffCd = newOffCd;
    }

    /**
     * @return the oldOffCode
     */
    public int getOldOffCode() {
        return oldOffCode;
    }

    /**
     * @param oldOffCode the oldOffCode to set
     */
    public void setOldOffCode(int oldOffCode) {
        this.oldOffCode = oldOffCode;
    }

    /**
     * @return the officeName
     */
    public String getOfficeName() {
        return officeName;
    }

    /**
     * @param officeName the officeName to set
     */
    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    /**
     * @return the allotedOfficeList
     */
    public Map<String, Object> getAllotedOfficeList() {
        return allotedOfficeList;
    }

    /**
     * @param allotedOfficeList the allotedOfficeList to set
     */
    public void setAllotedOfficeList(Map<String, Object> allotedOfficeList) {
        this.allotedOfficeList = allotedOfficeList;
    }

    /**
     * @return the sessionVariables
     */
    public SessionVariables getSessionVariables() {
        return sessionVariables;
    }

    /**
     * @param sessionVariables the sessionVariables to set
     */
    public void setSessionVariables(SessionVariables sessionVariables) {
        this.sessionVariables = sessionVariables;
    }
}
