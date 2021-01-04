/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.user_mgmt.dobj.OfficeModificationDobj;
import nic.vahan.db.user_mgmt.impl.OfficeModificationImpl;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import static nic.vahan.server.ServerUtil.Compare;

/**
 *
 * @author acer
 */
@ManagedBean
@ViewScoped
public class OfficeModificationBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(OfficeModificationBean.class);
    private List<OfficeModificationDobj> userOfficeList;
    private OfficeModificationDobj offModification;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private OfficeModificationDobj offModificationDobjPrev;
    private String modifyOffOtp = null;
    private String enterOffOtp = null;
    private boolean saveButton;
    private SessionVariables sessionVariables;
    private String vahanMessages = null;
    TmConfigurationDobj tmConfigurationDobj = null;
    private List<OfficeModificationDobj> allOfficeListOfState;

    public OfficeModificationBean() {
        try {
            sessionVariables = new SessionVariables();
            if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                    || sessionVariables.getActionCodeSelected() == 0) {
                vahanMessages = "Session time Out";
                return;
            }
            tmConfigurationDobj = Util.getTmConfiguration();
            offModification = new OfficeModificationDobj();
            getAllOfficeListInState();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage("Some Error occurred while fetching the Record");
        }
    }

    public void updateListner(OfficeModificationDobj office_modify) {
        try {
            offModificationDobjPrev = (OfficeModificationDobj) office_modify.clone();
            offModification = OfficeModificationImpl.getOfficeDetails((OfficeModificationDobj) office_modify.clone());
            setSaveButton(true);
        } catch (CloneNotSupportedException cnse) {
            LOGGER.error(cnse.toString() + " " + cnse.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }

    }

    public void sendOtpMailAndAddModify(String otptype) {
        String modifyMessage = "";
        try {
            List<ComparisonBean> compareChanges = compareChanges();
            String changedData = ComparisonBeanImpl.changedDataContents(compareChanges);

            if (compareChanges.isEmpty()) {
                reset();
                throw new VahanException("Without Changes, Data can't be Modify");
            }

            if (tmConfigurationDobj != null && tmConfigurationDobj.getTmConfigOtpDobj() != null && tmConfigurationDobj.getTmConfigOtpDobj().isAdd_modify_office_with_otp()) {
                String returnvalue = ServerUtil.sendOtpAndMailForTransaction(otptype, enterOffOtp, modifyOffOtp, getSessionVariables().getEmpCodeLoggedIn(), "", "Add-Modify Office");
                modifyOffOtp = returnvalue;
                if ("confirmOtp".equals(otptype) && !CommonUtils.isNullOrBlank(returnvalue)) {
                    PrimeFaces.current().ajax().update("otp_text otp_confirmation");
                    PrimeFaces.current().executeScript("PF('otp_confrm').hide()");
                    modifyMessage = OfficeModificationImpl.insertOfficeRecords(offModification);
                    MasterTableFiller.ReloadMasterTables();
                    reset();
                    getAllOfficeListInState();
                    JSFUtils.showMessage(modifyMessage);
                    PrimeFaces.current().ajax().update(":delform:officeName");
                } else {
                    PrimeFaces.current().ajax().update(":delform:otp_confirmation");
                    PrimeFaces.current().executeScript("PF('otp_confrm').show()");
                }
            } else {
                modifyMessage = OfficeModificationImpl.insertOfficeRecords(offModification);
                MasterTableFiller.ReloadMasterTables();
                reset();
                getAllOfficeListInState();
                JSFUtils.showMessage(modifyMessage);
            }
        } catch (VahanException ve) {
            JSFUtils.showMessage(ve.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void reset() {
        offModification = null;
        offModification = new OfficeModificationDobj();
        offModification.setOff_cd(0);
        setSaveButton(false);
    }

    public void checkFlatfile() throws VahanException {
        if (tmConfigurationDobj != null && tmConfigurationDobj.isRc_after_hsrp()
                && offModification.isSmartcardFlag()
                && OfficeModificationImpl.CheckPendingSmartcardFlatfile(offModification.getOff_cd(), offModification.getState_cd())) {
            reset();
            JSFUtils.showMessage("Please Generate All Pending Flat-File for SmartCard then Enable/Disable HSRP.");
        }
    }

    public List<ComparisonBean> compareChanges() {

        if (!compBeanList.isEmpty()) {
            compBeanList.clear();
        }
        if (offModificationDobjPrev == null) {
            return compBeanList;
        }
        Compare("Office Name", offModificationDobjPrev.getOff_name(), offModification.getOff_name(), (ArrayList) compBeanList);
        Compare("Office Address 1", offModificationDobjPrev.getOff_add1(), offModification.getOff_add1(), (ArrayList) compBeanList);
        Compare("Office Address 2", offModificationDobjPrev.getOff_add2(), offModification.getOff_add2(), (ArrayList) compBeanList);
        Compare("Pincode", offModificationDobjPrev.getPinCode(), offModification.getPinCode(), (ArrayList) compBeanList);
        Compare("District Code", offModificationDobjPrev.getDistrict_Cd(), offModification.getDistrict_Cd(), (ArrayList) compBeanList);
        Compare("Mobile No", offModificationDobjPrev.getMobileNo(), offModification.getMobileNo(), (ArrayList) compBeanList);
        Compare("Landline", offModificationDobjPrev.getLandLine_No(), offModification.getLandLine_No(), (ArrayList) compBeanList);
        Compare("Email ID", offModificationDobjPrev.getEmail_Id(), offModification.getEmail_Id(), (ArrayList) compBeanList);
        Compare("Office Under Code", offModificationDobjPrev.getOff_under_cd(), offModification.getOff_under_cd(), (ArrayList) compBeanList);
        Compare("Office Type Code", offModificationDobjPrev.getOff_type_cd(), offModification.getOff_type_cd(), (ArrayList) compBeanList);
        Compare("RC Type", offModificationDobjPrev.getRcOption(), offModification.getRcOption(), (ArrayList) compBeanList);
        compareBoolean("HSRP", offModificationDobjPrev.isHsrpFlag(), offModification.isHsrpFlag());
        compareBoolean("OLD HSRP", offModificationDobjPrev.isOldHsrp(), offModification.isOldHsrp());

        return compBeanList;
    }

    public void compareBoolean(String fieldName, boolean oldValue, boolean newValue) {
        ComparisonBean comparisonBean = null;
        if (oldValue && !newValue) {
            comparisonBean = new ComparisonBean();
            comparisonBean.setFields(fieldName);
            comparisonBean.setOld_value(String.valueOf(oldValue));
            comparisonBean.setNew_value(String.valueOf(newValue));
            compBeanList.add(comparisonBean);
        } else if (!oldValue && newValue) {
            comparisonBean = new ComparisonBean();
            comparisonBean.setFields(fieldName);
            comparisonBean.setOld_value(String.valueOf(oldValue));
            comparisonBean.setNew_value(String.valueOf(newValue));
            compBeanList.add(comparisonBean);
        }
    }

    public void getAllOfficeListInState() {
        try {
            allOfficeListOfState = OfficeModificationImpl.getAllOfficeListInState(getSessionVariables().getStateCodeSelected());
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
    }

    /**
     * @return the userOfficeList
     */
    public List<OfficeModificationDobj> getUserOfficeList() {
        return userOfficeList;
    }

    /**
     * @param userOfficeList the userOfficeList to set
     */
    public void setUserOfficeList(List<OfficeModificationDobj> userOfficeList) {
        this.userOfficeList = userOfficeList;
    }

    /**
     * @return the offModification
     */
    public OfficeModificationDobj getOffModification() {
        return offModification;
    }

    /**
     * @param offModification the offModification to set
     */
    public void setOffModification(OfficeModificationDobj offModification) {
        this.offModification = offModification;
    }

    /**
     * @return the modifyOffOtp
     */
    public String getModifyOffOtp() {
        return modifyOffOtp;
    }

    /**
     * @param modifyOffOtp the modifyOffOtp to set
     */
    public void setModifyOffOtp(String modifyOffOtp) {
        this.modifyOffOtp = modifyOffOtp;
    }

    /**
     * @return the enterOffOtp
     */
    public String getEnterOffOtp() {
        return enterOffOtp;
    }

    /**
     * @param enterOffOtp the enterOffOtp to set
     */
    public void setEnterOffOtp(String enterOffOtp) {
        this.enterOffOtp = enterOffOtp;
    }

    /**
     * @return the saveButton
     */
    public boolean isSaveButton() {
        return saveButton;
    }

    /**
     * @param saveButton the saveButton to set
     */
    public void setSaveButton(boolean saveButton) {
        this.saveButton = saveButton;
    }

    /**
     * @return the allOfficeListOfState
     */
    public List<OfficeModificationDobj> getAllOfficeListOfState() {
        return allOfficeListOfState;
    }

    /**
     * @param allOfficeListOfState the allOfficeListOfState to set
     */
    public void setAllOfficeListOfState(List<OfficeModificationDobj> allOfficeListOfState) {
        this.allOfficeListOfState = allOfficeListOfState;
    }

    /**
     * @return the vahanMessages
     */
    public String getVahanMessages() {
        return vahanMessages;
    }

    /**
     * @param vahanMessages the vahanMessages to set
     */
    public void setVahanMessages(String vahanMessages) {
        this.vahanMessages = vahanMessages;
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
