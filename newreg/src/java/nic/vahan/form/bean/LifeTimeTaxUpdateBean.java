/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import dao.UserDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import nic.java.util.CommonUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.LifeTimeTaxUpdateDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.impl.ApplicationDisposeImpl;
import nic.vahan.form.impl.LifeTimeTaxUpdateImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.SmsMailOTPImpl;
import nic.vahan.form.impl.TaxServer_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author nicsi
 */
@ViewScoped
@ManagedBean(name = "lifeTimeTaxUpdateBean")
public class LifeTimeTaxUpdateBean implements Serializable {

    private static final Logger logger = Logger.getLogger(LifeTimeTaxUpdateBean.class);
    private String regnNo;
    private String applNo;
    private String stateCd;
    private String empCd;
    private int offCode;
    private OwnerDetailsDobj ownerDetail;
    private boolean render = false;
    private int actioncd = 0;
    private Long taxAmount;
    private String remark;
    LifeTimeTaxUpdateImpl lifeTimeTaxUpdateImpl = new LifeTimeTaxUpdateImpl();
    private boolean renderBackBtm = true;
    private boolean renderReset = false;
    private String taxPaidLabel;
    private boolean rendertaxPaid = false;
    private List<TaxFormPanelBean> listTaxFormPrivous = new ArrayList();
    private String addTaxPaidLabel;
    private boolean renderaddtaxPaid = false;
    private String taxClearLabel;
    private boolean rendertaxClear = false;
    private String addTaxClearLabel;
    private boolean renderaddtaxClear = false;
    private String taxExempLabel;
    private boolean isTaxExemp = false;
    //Add Tax
    private Long totalTaxAmount = 0L;
    private Owner_dobj ownerDobj;
    private String otp = null;
    private String enterOtp = null;

    @PostConstruct
    public void init() {
        listTaxFormPrivous.clear();
    }

    public void showDetails() {
        try {
            boolean onlineFlag = false;
            otp = "";
            listTaxFormPrivous.clear();
            String msg = "";
            remark = "";
            stateCd = Util.getUserStateCode();
            offCode = Util.getSelectedSeat().getOff_cd();
            empCd = Util.getEmpCode();
            if (this.empCd == null || this.empCd.trim().equalsIgnoreCase("") || this.stateCd == null || this.stateCd.trim().equalsIgnoreCase("")) {
                msg = Util.getLocaleSessionMsg();
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }

            OwnerImpl owner_Impl = new OwnerImpl();
            if (this.applNo == null || this.applNo.trim().equalsIgnoreCase("")) {
                msg = Util.getLocaleMsg("applNoMsg");
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }
            regnNo = ApplicationDisposeImpl.getRegNo(applNo);
            List<Status_dobj> statusList = null;
            if (CommonUtils.isNullOrBlank(regnNo)) {
                statusList = ServerUtil.applicationStatusOnline(applNo, stateCd);
                if (!statusList.isEmpty()) {
                    onlineFlag = true;
                    regnNo = statusList.get(0).getRegn_no();

                }
            }
            if (this.regnNo == null || this.regnNo.trim().equalsIgnoreCase("") || regnNo.equalsIgnoreCase("NEW") || regnNo.equalsIgnoreCase("TEMPREG")) {
                msg = Util.getLocaleMsg("applNoMsg");
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }
            String regNo = lifeTimeTaxUpdateImpl.checkEntryInValttamtupdate(regnNo, stateCd, offCode);
            if (!CommonUtils.isNullOrBlank(regNo)) {
                msg = "[" + regNo + "]" + Util.getLocaleMsg("veh_already_exist");
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }
            if (statusList == null) {
                statusList = ServerUtil.applicationStatus(regnNo, applNo, stateCd);
            }
            int purCd = 0;
            boolean flag = false;
            if (statusList.isEmpty()) {
                msg = Util.getLocaleMsg("applno_approved") + "  !";
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            } else {
                TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();
                for (Status_dobj statusDobj : statusList) {
                    if (tmConfigurationDobj != null && !CommonUtils.isNullOrBlank(tmConfigurationDobj.getUpdateadditionallttpurcd()) && tmConfigurationDobj.getUpdateadditionallttpurcd().contains(statusDobj.getPur_cd() + "")) {
                        flag = true;
                        break;
                    } else {
                        purCd = statusDobj.getPur_cd();
                    }
                }
            }
            if (!flag) {
                msg = Util.getLocaleMsg("applno_approved") + ServerUtil.getTaxHead(purCd) + "  !";
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;

            }
            String rctpNo = lifeTimeTaxUpdateImpl.getPayTaxFromVahan4(regnNo, stateCd, offCode);
            if (!CommonUtils.isNullOrBlank(rctpNo)) {
                msg = Util.getLocaleMsg("ltt_update_vahan4");
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }
            actioncd = ApplicationDisposeImpl.getActionCode(applNo);
            if (actioncd != TableConstants.TM_ACTION_REGISTERED_VEH_FEE && !onlineFlag) {
                msg = Util.getLocaleMsg("llt_not_onfee");
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }

            ownerDetail = owner_Impl.getOwnerDetails(this.regnNo.trim());

            msg = Util.getLocaleMsg("regn_not_found");
            if (ownerDetail == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }
            if (ownerDetail != null && (!ownerDetail.getState_cd().equals(Util.getUserStateCode()) || ownerDetail.getOff_cd() != Util.getSelectedSeat().getOff_cd())) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }
            //Tax
            LifeTimeTaxUpdateDobj lifeTimeTaxUpdate = new LifeTimeTaxUpdateDobj();
            lifeTimeTaxUpdate.setApplNo(applNo);
            lifeTimeTaxUpdate.setRegnNo(ownerDetail.getRegn_no());
            lifeTimeTaxUpdate.setOffcd(ownerDetail.getOff_cd());
            lifeTimeTaxUpdate.setStateCd(stateCd);
            taxAmount = lifeTimeTaxUpdateImpl.getLifeTaxUpdateAmt(lifeTimeTaxUpdate);
            OwnerImpl ownerImpl = new OwnerImpl();
            Owner_dobj ownerDobj = ownerImpl.getOwnerDobj(ownerDetail);
            // for show previous tax
            PassengerPermitDetailDobj permitDob = TaxServer_Impl.getPermitInfoForSavedTax(ownerDobj);
            VehicleParameters vehicleParameters = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj, permitDob);
            listTaxFormPrivous = TaxServer_Impl.getListTaxForm(ownerDobj, vehicleParameters);
            for (TaxFormPanelBean bean : listTaxFormPrivous) {
                Map<String, String> taxPaidAndClearDetail = TaxServer_Impl.getTaxPaidAndClearDetail(regnNo, bean.getPur_cd());
                for (Map.Entry<String, String> entry : taxPaidAndClearDetail.entrySet()) {
                    if (entry.getKey().equals("taxpaid")) {
                        if (entry.getValue() != null && !entry.getValue().equals("")) {
                            setRendertaxPaid(true);
                            setTaxPaidLabel(entry.getValue());
                        } else {
                            setRendertaxPaid(false);
                        }
                    }
                    if (entry.getKey().equals("addtaxpaid")) {
                        if (entry.getValue() != null && !entry.getValue().equals("")) {
                            setRenderaddtaxPaid(true);
                            setAddTaxPaidLabel(entry.getValue());
                        } else {
                            setRenderaddtaxPaid(false);
                        }
                    }
                    if (entry.getKey().equals("taxclear")) {
                        if (entry.getValue() != null && !entry.getValue().equals("")) {
                            setRendertaxClear(true);
                            setTaxClearLabel(entry.getValue());
                        } else {
                            setRendertaxClear(false);
                        }
                    }
                    if (entry.getKey().equals("addtaxclear")) {
                        if (entry.getValue() != null && !entry.getValue().equals("")) {
                            setRenderaddtaxClear(true);
                            setAddTaxClearLabel(entry.getValue());
                        } else {
                            setRenderaddtaxClear(false);
                        }
                    }

                    if (entry.getKey().equals("taxExemp")) {
                        if (entry.getValue() != null && !entry.getValue().equals("")) {
                            setIsTaxExemp(true);
                            setTaxExempLabel(entry.getValue());
                        } else {
                            setIsTaxExemp(false);
                        }
                    }

                }
            }

            //
            setRender(true);
            setRenderBackBtm(false);
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ve.getMessage()));
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ex.getMessage()));
        }

    }

    public String sendOtpMailAndModificationLTT(String otpType) {
        try {
            String mgsOTP = "";
            TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();
            if (tmConfigurationDobj != null && tmConfigurationDobj.getTmConfigOtpDobj() != null && tmConfigurationDobj.getTmConfigOtpDobj().isModifyLTTorOTTwithotp()) {
                boolean isverified = UserDAO.getVerificationDetails(empCd);
                if (isverified) {
                    if (otpType != null && otpType.equals("sendOtp") || otpType.equals("resendOtp")) {
                        otp = SmsMailOTPImpl.sendOTPorMail(empCd, "OTP for Modification LTT/OTT. ", otpType, otp, "OTP for Modification LTT/OTT.");
                        if (otp != null && !otp.equals("")) {
                            PrimeFaces.current().ajax().update("otp_confirmation");
                            PrimeFaces.current().executeScript("PF('otp_confrm').show()");
                        } else {
                            throw new VahanException(Util.getLocaleMsg("otp_unable"));
                        }
                    } else if (otpType.equals("confirmOtp")) {
                        if (otp.equals(enterOtp)) {
                            saveLifeTimeTax();
                            PrimeFaces.current().ajax().update("formGenApplNo:otp_confirmation");
                            PrimeFaces.current().executeScript("PF('otp_confrm').hide()");
                            return "";
                        } else {
                            mgsOTP = Util.getLocaleMsg("otp_invalid");
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, mgsOTP, mgsOTP));
                        }
                    }
                } else {
                    mgsOTP = Util.getLocaleMsg("ltt_invalidotp");
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, mgsOTP, mgsOTP));
                }
            } else {
                saveLifeTimeTax();
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ve.getMessage(), ve.getMessage()));
        } catch (Exception e) {
            logger.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
        return "";
    }

    public void saveLifeTimeTax() {
        try {

            String msg = "";
            if (Util.getEmpCode() == null || Util.getEmpCode().trim().equalsIgnoreCase("")) {
                msg = Util.getLocaleSessionMsg();
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }
            if (CommonUtils.isNullOrBlank(remark) || remark.trim().length() < 5) {
                throw new VahanException(Util.getLocaleMsg("rmk_field"));
            }
            if (this.taxAmount > 0) {
                long distAmt = 300;
                if (this.taxAmount < distAmt) {
                    msg = Util.getLocaleMsg("ltt_amt_grtthen");
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                    return;
                }
            } else {
                msg = Util.getLocaleMsg("ltt_plse_enter");
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }
            LifeTimeTaxUpdateDobj lifeTimeTaxUpdateDobj = new LifeTimeTaxUpdateDobj();
            lifeTimeTaxUpdateDobj.setEmpCode(empCd);
            lifeTimeTaxUpdateDobj.setApplNo(applNo);
            lifeTimeTaxUpdateDobj.setRemark(remark);
            lifeTimeTaxUpdateDobj.setTaxAmt(taxAmount);
            lifeTimeTaxUpdateDobj.setStateCd(stateCd);
            lifeTimeTaxUpdateDobj.setOffcd(ownerDetail.getOff_cd());
            lifeTimeTaxUpdateDobj.setRegnNo(ownerDetail.getRegn_no());
            lifeTimeTaxUpdateImpl.saveAndUpdatLTTaxAmt(lifeTimeTaxUpdateDobj);
            String facesMessages = Util.getLocaleMsg("ltt_succes");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, facesMessages, facesMessages));
            PrimeFaces.current().ajax().update("msgDialog");
            PrimeFaces.current().executeScript("PF('messageDialog').show();");
            setRenderBackBtm(true);
            setRender(false);
            setRenderReset(true);

        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ve.getMessage()));
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ex.getMessage()));
        }

    }

    public void reset() {
        otp = "";
        ownerDetail = null;
        applNo = "";
        regnNo = "";
        remark = "";
        setRenderBackBtm(true);
        setRender(false);
        setRenderReset(false);
        totalTaxAmount = 0L;
        listTaxFormPrivous.clear();
    }

    public void checkTaxAmt(AjaxBehaviorEvent event) {
        String msg = "";
        if (this.taxAmount > 0) {
            long distAmt = 300;
            if (this.taxAmount < distAmt) {
                msg = Util.getLocaleMsg("ltt_amt_grtthen");
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }
        } else {
            msg = Util.getLocaleMsg("ltt_plse_enter");
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
            return;
        }
    }

    public String getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    public String getApplNo() {
        return applNo;
    }

    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    public String getStateCd() {
        return stateCd;
    }

    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    public String getEmpCd() {
        return empCd;
    }

    public void setEmpCd(String empCd) {
        this.empCd = empCd;
    }

    public int getOffCode() {
        return offCode;
    }

    public void setOffCode(int offCode) {
        this.offCode = offCode;
    }

    public OwnerDetailsDobj getOwnerDetail() {
        return ownerDetail;
    }

    public void setOwnerDetail(OwnerDetailsDobj ownerDetail) {
        this.ownerDetail = ownerDetail;
    }

    public boolean isRender() {
        return render;
    }

    public void setRender(boolean render) {
        this.render = render;
    }

    public int getActioncd() {
        return actioncd;
    }

    public void setActioncd(int actioncd) {
        this.actioncd = actioncd;
    }

    public Long getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Long taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isRenderBackBtm() {
        return renderBackBtm;
    }

    public void setRenderBackBtm(boolean renderBackBtm) {
        this.renderBackBtm = renderBackBtm;
    }

    public boolean isRenderReset() {
        return renderReset;
    }

    public void setRenderReset(boolean renderReset) {
        this.renderReset = renderReset;
    }

    public String getTaxPaidLabel() {
        return taxPaidLabel;
    }

    public void setTaxPaidLabel(String taxPaidLabel) {
        this.taxPaidLabel = taxPaidLabel;
    }

    public String getAddTaxPaidLabel() {
        return addTaxPaidLabel;
    }

    public void setAddTaxPaidLabel(String addTaxPaidLabel) {
        this.addTaxPaidLabel = addTaxPaidLabel;
    }

    public String getTaxClearLabel() {
        return taxClearLabel;
    }

    public void setTaxClearLabel(String taxClearLabel) {
        this.taxClearLabel = taxClearLabel;
    }

    public String getAddTaxClearLabel() {
        return addTaxClearLabel;
    }

    public void setAddTaxClearLabel(String addTaxClearLabel) {
        this.addTaxClearLabel = addTaxClearLabel;
    }

    public String getTaxExempLabel() {
        return taxExempLabel;
    }

    public void setTaxExempLabel(String taxExempLabel) {
        this.taxExempLabel = taxExempLabel;
    }

    public boolean isIsTaxExemp() {
        return isTaxExemp;
    }

    public void setIsTaxExemp(boolean isTaxExemp) {
        this.isTaxExemp = isTaxExemp;
    }

    public boolean isRendertaxPaid() {
        return rendertaxPaid;
    }

    public void setRendertaxPaid(boolean rendertaxPaid) {
        this.rendertaxPaid = rendertaxPaid;
    }

    public boolean isRenderaddtaxPaid() {
        return renderaddtaxPaid;
    }

    public void setRenderaddtaxPaid(boolean renderaddtaxPaid) {
        this.renderaddtaxPaid = renderaddtaxPaid;
    }

    public boolean isRendertaxClear() {
        return rendertaxClear;
    }

    public void setRendertaxClear(boolean rendertaxClear) {
        this.rendertaxClear = rendertaxClear;
    }

    public boolean isRenderaddtaxClear() {
        return renderaddtaxClear;
    }

    public void setRenderaddtaxClear(boolean renderaddtaxClear) {
        this.renderaddtaxClear = renderaddtaxClear;
    }

    public Long getTotalTaxAmount() {
        return totalTaxAmount;
    }

    public void setTotalTaxAmount(Long totalTaxAmount) {
        this.totalTaxAmount = totalTaxAmount;
    }

    public Owner_dobj getOwnerDobj() {
        return ownerDobj;
    }

    public void setOwnerDobj(Owner_dobj ownerDobj) {
        this.ownerDobj = ownerDobj;
    }

    public List<TaxFormPanelBean> getListTaxFormPrivous() {
        return listTaxFormPrivous;
    }

    public void setListTaxFormPrivous(List<TaxFormPanelBean> listTaxFormPrivous) {
        this.listTaxFormPrivous = listTaxFormPrivous;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getEnterOtp() {
        return enterOtp;
    }

    public void setEnterOtp(String enterOtp) {
        this.enterOtp = enterOtp;
    }
}
