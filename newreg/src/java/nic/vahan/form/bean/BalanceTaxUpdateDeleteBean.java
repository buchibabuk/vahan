/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import dao.UserDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.RefundAndExcessDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.impl.BalanceTaxUpdateDeleteImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.SmsMailOTPImpl;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author GULSHAN
 */
@ViewScoped
@ManagedBean(name = "balanceTaxUpdateDeleteBean")
public class BalanceTaxUpdateDeleteBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(BalanceTaxUpdateDeleteBean.class);
    private String regnNo;
    private String stateCd;
    private String empCd;
    private int offCode;
    private Long taxAmount;
    private String remark;
    private boolean render = false;
    private OwnerDetailsDobj ownerDetail;
    private boolean renderBackBtm = true;
    private List<RefundAndExcessDobj> refDojList = new ArrayList<RefundAndExcessDobj>();
    private BalanceTaxUpdateDeleteImpl balanceTaxUpdateDeleteImpl = new BalanceTaxUpdateDeleteImpl();
    private List<RefundAndExcessDobj> clonedRefunList = null;
    private RefundAndExcessDobj reFundAndExcessDobj = null;
    private Date maxOrderDate = new Date();
    private String enterOtp = null;
    private String oTP = null;

    @PostConstruct
    public void init() {
        reFundAndExcessDobj = new RefundAndExcessDobj();
        reset();
    }

    public void showDetails() {
        try {
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
            if (this.regnNo == null || this.regnNo.trim().equalsIgnoreCase("")) {
                msg = Util.getLocaleMsg("regn_noblank");
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }
            ownerDetail = owner_Impl.getOwnerDetailsWithOffice(regnNo.trim(), stateCd, offCode);
            msg = Util.getLocaleMsg("regn_not_found");
            if (ownerDetail == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }
            refDojList = balanceTaxUpdateDeleteImpl.getBalanceTaxDetailsForUpdate(ownerDetail.getRegn_no(), ownerDetail.getOff_cd(), ownerDetail.getState_cd());
            if (refDojList.isEmpty()) {
                msg = "Balance Tax entry not Found!";
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }
            clonedRefunList = new ArrayList<RefundAndExcessDobj>(refDojList);
            setRender(true);
            setRenderBackBtm(false);
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ve.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ex.getMessage()));
        }

    }

    public void updateAndDeleteBalTax() {
        try {
            if (!clonedRefunList.isEmpty()) {
                clonedRefunList.get(0).setOrderDate(reFundAndExcessDobj.getOrderDate());
                clonedRefunList.get(0).setOrderIssueBy(reFundAndExcessDobj.getOrderIssueBy());
                clonedRefunList.get(0).setOrderNo(reFundAndExcessDobj.getOrderNo());
                clonedRefunList.get(0).setRemark(reFundAndExcessDobj.getRemark());
            }
            //end refun
            balanceTaxUpdateDeleteImpl.updateDelBalaceTaxAmt(refDojList, clonedRefunList, empCd);
            String facesMessages = "Successfully Updated Refund/Excess/Balance Tax Amount.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, facesMessages, facesMessages));
            PrimeFaces.current().ajax().update("msgDialog");
            PrimeFaces.current().executeScript("PF('messageDialog').show();");
            reset();
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ve.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ex.getMessage()));
        }
    }

    public void reset() {
        ownerDetail = null;
        setRenderBackBtm(true);
        setRender(false);
        refDojList.clear();
        clonedRefunList = null;
        reFundAndExcessDobj = new RefundAndExcessDobj();
        enterOtp = null;
    }

    public String sendOtpMailForUpdateRefund(String otpType) {
        try {
            String mgsOTP = "";
            TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();
            if (tmConfigurationDobj != null && tmConfigurationDobj.getTmConfigOtpDobj() != null && tmConfigurationDobj.getTmConfigOtpDobj().isDeleteModifyRefundWithOtp()) {
                boolean isverified = UserDAO.getVerificationDetails(empCd);
                if (isverified) {
                    if (otpType != null && otpType.equals("sendOtp") || otpType.equals("resendOtp")) {
                        oTP = SmsMailOTPImpl.sendOTPorMail(empCd, "OTP for Modify/Delete Refund/Balance Tax.", otpType, oTP, "OTP for Modify/Delete Refund/Balance Tax.");
                        if (oTP != null && !oTP.equals("")) {
                            PrimeFaces.current().ajax().update("otp_confirmation");
                            PrimeFaces.current().executeScript("PF('otp_confrm').show()");
                        } else {
                            throw new VahanException("Unable to generate OTP and send it.");
                        }
                    } else if (otpType.equals("confirmOtp")) {
                        if (oTP.equals(enterOtp)) {
                            updateAndDeleteBalTax();
                            PrimeFaces.current().ajax().update("formBalTaxAmt:otp_confirmation");
                            PrimeFaces.current().ajax().update("formBalTaxAmt");
                            PrimeFaces.current().executeScript("PF('otp_confrm').hide()");
                            return "";
                        } else {
                            mgsOTP = "Invalid OTP, Please enter correct OTP.";
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, mgsOTP, mgsOTP));
                        }
                    }
                } else {
                    mgsOTP = "OTP is mandatory for Modify/Delete Refund Balance Tax.and Your Mobile no is not verified, please verify first from UPDATE PROFILE module.";
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, mgsOTP, mgsOTP));
                }
            } else {
                updateAndDeleteBalTax();
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ve.getMessage(), ve.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
        return "";
    }

    public void onRowEdit(RowEditEvent event) {
    }

    public void onRowCancel(RowEditEvent event) {
        refDojList.remove(((RefundAndExcessDobj) event.getObject()));
    }

    public String getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
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

    public boolean isRender() {
        return render;
    }

    public void setRender(boolean render) {
        this.render = render;
    }

    public OwnerDetailsDobj getOwnerDetail() {
        return ownerDetail;
    }

    public void setOwnerDetail(OwnerDetailsDobj ownerDetail) {
        this.ownerDetail = ownerDetail;
    }

    public boolean isRenderBackBtm() {
        return renderBackBtm;
    }

    public void setRenderBackBtm(boolean renderBackBtm) {
        this.renderBackBtm = renderBackBtm;
    }

    public List<RefundAndExcessDobj> getRefDojList() {
        return refDojList;
    }

    public void setRefDojList(List<RefundAndExcessDobj> refDojList) {
        this.refDojList = refDojList;
    }

    public BalanceTaxUpdateDeleteImpl getBalanceTaxUpdateDeleteImpl() {
        return balanceTaxUpdateDeleteImpl;
    }

    public void setBalanceTaxUpdateDeleteImpl(BalanceTaxUpdateDeleteImpl balanceTaxUpdateDeleteImpl) {
        this.balanceTaxUpdateDeleteImpl = balanceTaxUpdateDeleteImpl;
    }

    public List<RefundAndExcessDobj> getClonedRefunList() {
        return clonedRefunList;
    }

    public void setClonedRefunList(List<RefundAndExcessDobj> clonedRefunList) {
        this.clonedRefunList = clonedRefunList;
    }

    public RefundAndExcessDobj getReFundAndExcessDobj() {
        return reFundAndExcessDobj;
    }

    public void setReFundAndExcessDobj(RefundAndExcessDobj reFundAndExcessDobj) {
        this.reFundAndExcessDobj = reFundAndExcessDobj;
    }

    public Date getMaxOrderDate() {
        return maxOrderDate;
    }

    public void setMaxOrderDate(Date maxOrderDate) {
        this.maxOrderDate = maxOrderDate;
    }

    public String getEnterOtp() {
        return enterOtp;
    }

    public void setEnterOtp(String enterOtp) {
        this.enterOtp = enterOtp;
    }

    public String getoTP() {
        return oTP;
    }

    public void setoTP(String oTP) {
        this.oTP = oTP;
    }
}
