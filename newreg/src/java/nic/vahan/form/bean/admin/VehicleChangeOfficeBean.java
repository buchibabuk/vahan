/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.admin;

import dao.UserDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.NocDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.PrintCertificatesDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.impl.NocImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.SmsMailOTPImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.admin.OwnerAdminImpl;
import nic.vahan.form.impl.admin.VehicleOfficeChangeImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author nicsi
 */
@ManagedBean(name = "adminChangeOffice")
@ViewScoped
public class VehicleChangeOfficeBean implements Serializable {

    private static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(VehicleChangeOfficeBean.class);
    private OwnerDetailsDobj ownerDetail;
    private String regnNo;
    private String chassiNo;
    String empCd;
    private int offCode;
    private boolean render_vehicle_type = true;
    private boolean renderTab = false;
    private boolean disableRegnNo = false;
    private String remark;
    private List office_list;
    private int off_to;
    private int current_offCd;
    private String requested_by;
    private Date requestedOn = new Date();
    private List<OwnerDetailsDobj> regnNameList = null;
    private boolean showRegList = false;
    private boolean renderBackButton = false;
    String facesMessages = "";
    private Date maxDate = new Date();
    private String otp = null;
    private String enterOtp = null;

    @PostConstruct
    public void init() {
        office_list = new ArrayList();
        List<Integer> offTypeCd = Arrays.asList(0, 1, 2);
        try {
            office_list = ServerUtil.getOfficeBasedOnType(Util.getUserStateCode(), offTypeCd);
        } catch (VahanException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            return;
        }

    }

    public void getDetails() {
        OwnerAdminImpl adminImpl = new OwnerAdminImpl();
        VehicleOfficeChangeImpl vehBean = new VehicleOfficeChangeImpl();
        String msg = "";
        offCode = Util.getSelectedSeat().getOff_cd();
        empCd = Util.getEmpCode();
        if (this.empCd == null || this.empCd.trim().equalsIgnoreCase("")) {
            msg = Util.getLocaleSessionMsg();//"Session Expired. Please try again.";
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
            return;
        }
        if (this.regnNo == null || this.regnNo.trim().equalsIgnoreCase("")) {
            msg = Util.getLocaleMsg("valid_regn_no");//"Please Enter Valid Registration Number";
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
            return;
        }
        if (this.chassiNo == null || this.chassiNo.trim().equalsIgnoreCase("") || this.chassiNo.length() != 5) {
            msg = Util.getLocaleMsg("valid_chassisno");//"Please Enter Valid Chassis Number";
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
            return;
        }
        try {
            regnNameList = vehBean.getOwnerDetailsListByChassiNo(this.getRegnNo().toUpperCase().trim(), Util.getUserStateCode(), chassiNo);
            if ((regnNameList == null || regnNameList.isEmpty())) {
                String mgs = Util.getLocaleMsg("regn_not_found");
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, mgs, mgs);
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }
            showRegList = true;
            render_vehicle_type = false;
            disableRegnNo = true;
            renderBackButton = true;

        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vex.getMessage(), vex.getMessage()));
            return;
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }

    }

    public void showDetails(OwnerDetailsDobj ownerDetail) {
        this.ownerDetail = ownerDetail;
        OwnerAdminImpl adminImpl = new OwnerAdminImpl();
        VehicleOfficeChangeImpl vehBean = new VehicleOfficeChangeImpl();
        String msg = "";
        try {
            offCode = Util.getSelectedSeat().getOff_cd();
            empCd = Util.getEmpCode();

            if (this.empCd == null || this.empCd.trim().equalsIgnoreCase("")) {
                msg = Util.getLocaleSessionMsg();//"Session Expired. Please try again.";
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }
            if (this.regnNo == null || this.regnNo.trim().equalsIgnoreCase("")) {
                msg = Util.getLocaleMsg("valid_regn_no");//"Please Enter Valid Registration Number";
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }
            if (this.chassiNo == null || this.chassiNo.trim().equalsIgnoreCase("")) {
                msg = Util.getLocaleMsg("valid_chassisno");//"Please Enter Valid Chassi Number";
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }
            if (ownerDetail.getStatus().equals("N")) {
                NocImpl nocImpl = new NocImpl();
                NocDobj nocDobj = nocImpl.getNocDetails(ownerDetail.getRegn_no(), ownerDetail.getState_cd(), ownerDetail.getOff_cd());
                if (nocDobj != null) {
                    if (!nocDobj.getState_to().equalsIgnoreCase(Util.getUserStateCode())) {
                        if (nocDobj.getOff_to() != 0) {
                            msg = Util.getLocaleMsg("noc_taken") + ServerUtil.getStateNameByStateCode(nocDobj.getState_to()) + ", " + ServerUtil.getOfficeName(nocDobj.getOff_to(), nocDobj.getState_to()) + "," + Util.getLocaleMsg("chngeoffcd_notmodify");
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                            return;
                        } else {
                            msg = Util.getLocaleMsg("noc_taken") + ServerUtil.getStateNameByStateCode(nocDobj.getState_to()) + "," + Util.getLocaleMsg("chngeoffcd_notmodify");
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                            return;
                        }
                    }
                } else {
                    msg = Util.getLocaleMsg("noc_not_found");//"NOC details not found!";
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                    return;
                }
            }

            if (!ownerDetail.getStatus().equalsIgnoreCase("Y") && !ownerDetail.getStatus().equalsIgnoreCase("A")) {
                msg = Util.getLocaleMsg("vehNo") + ownerDetail.getRegn_no() + Util.getLocaleMsg("not_active");
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;

            }

            String isPending = vehBean.getPendingApplNoInwardotheroffices(regnNo, Util.getUserStateCode(), ownerDetail.getOff_cd());
            if (isPending != null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, isPending, isPending));
                PrimeFaces.current().ajax().update("msgDialog");
                PrimeFaces.current().executeScript("PF('messageDialog').show();");
                return;
            }
            if (ownerDetail.getOwnerIdentity() != null) {
                ownerDetail.getOwnerIdentity().setFlag(true);
                ownerDetail.getOwnerIdentity().setMobileNoEditable(true);
            }
            Iterator ite = office_list.iterator();
            while (ite.hasNext()) {
                SelectItem obj = (SelectItem) ite.next();
                if (Integer.parseInt(obj.getValue().toString().trim()) == ownerDetail.getOff_cd()) {
                    office_list.remove(obj);
                    break;
                }
            }
            PrintDocImpl docImpl = new PrintDocImpl();
            boolean printNewRcAtDealer = false;
            if (Util.getTmConfiguration() != null && Util.getTmConfiguration().getTmConfigDealerDobj() != null) {
                printNewRcAtDealer = Util.getTmConfiguration().getTmConfigDealerDobj().isPrintNewRCAtDealer();
            }
            ArrayList<PrintCertificatesDobj> list = docImpl.isRegnExistForRC(regnNo, Util.getUserStateCode(), ownerDetail.getOff_cd(), Util.getSelectedSeat().getAction_cd(), ownerDetail.getDealer_cd(), printNewRcAtDealer);
            String appl_no = null;
            String messages = "";
            boolean flag1 = false, flag2 = false;
            if (!list.isEmpty()) {
                flag1 = true;
                PrimeFaces.current().executeScript("PF('FCandRCcheck').show()");
                facesMessages = Util.getLocaleMsg("rc_pending_print") + regnNo + Util.getLocaleMsg("against_applno") + appl_no + "."
                        + "<br />" + Util.getLocaleMsg("rc_withoutprint");
            }
            appl_no = vehBean.getApplNoFromVaFcPrint(regnNo, Util.getUserStateCode(), ownerDetail.getOff_cd());
            if (appl_no != null) {
                flag2 = true;
                PrimeFaces.current().executeScript("PF('FCandRCcheck').show()");
                facesMessages = Util.getLocaleMsg("fc_pending_print") + regnNo + Util.getLocaleMsg("against_applno") + appl_no + "."
                        + "<br /> Do you want to change the RTO without print FC?";
            }
            if (flag1 && flag2) {
                facesMessages = Util.getLocaleMsg("rc_fc_pending_print") + regnNo + Util.getLocaleMsg("against_applno") + appl_no + "."
                        + "<br />" + Util.getLocaleMsg("without_rc_fc_print");
            }
            render_vehicle_type = false;
            renderTab = true;
            disableRegnNo = true;
            showRegList = false;
            renderBackButton = false;
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public String sendOtpMailAndChangeOffice(String otpType) {
        try {
            String otpMsg = "";
            TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();
            if (tmConfigurationDobj != null && tmConfigurationDobj.getTmConfigOtpDobj() != null && tmConfigurationDobj.getTmConfigOtpDobj().isChange_veh_office_with_otp()) {
                boolean isverified = UserDAO.getVerificationDetails(empCd);
                if (isverified) {
                    if (otpType != null && otpType.equals("sendOtp") || otpType.equals("resendOtp")) {
                        otpMsg = Util.getLocaleMsg("chngeoffcd_otpvehicle");
                        otp = SmsMailOTPImpl.sendOTPorMail(empCd, otpMsg + ".", otpType, otp, otpMsg + ".");
                        if (otp != null && !otp.equals("")) {
                            PrimeFaces.current().ajax().update("otp_confirmation");
                            PrimeFaces.current().executeScript("PF('otp_confrm').show()");
                        } else {
                            throw new VahanException(Util.getLocaleMsg("otp_unable"));
                        }
                    } else if (otpType.equals("confirmOtp")) {
                        if (otp.equals(enterOtp)) {
                            saveChanges();
                            PrimeFaces.current().ajax().update("otp_confirmation");
                            PrimeFaces.current().executeScript("PF('otp_confrm').hide()");
                            return "";
                        } else {
                            otpMsg = Util.getLocaleMsg("otp_invalid");
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, otpMsg, otpMsg));
                        }
                    }
                } else {
                    otpMsg = Util.getLocaleMsg("otp_mandatory");
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, otpMsg, otpMsg));
                }
            } else {
                saveChanges();
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ve.getMessage(), ve.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
        return "";
    }

    public String saveChanges() {
        try {
            OwnerImpl ownerImpl = new OwnerImpl();
            Owner_dobj ownerDobj = ownerImpl.getOwnerDobj(ownerDetail);
            VehicleOfficeChangeImpl vehImpl = new VehicleOfficeChangeImpl();
            VehicleOfficeChangeDobj vehicleDObj = new VehicleOfficeChangeDobj();
            vehicleDObj.setChassiNo(chassiNo);
            vehicleDObj.setReguestedDate(requestedOn);
            vehicleDObj.setNewOffCd(off_to);
            vehicleDObj.setRegnNo(regnNo.toUpperCase());
            vehicleDObj.setRemark(remark);
            vehicleDObj.setRequestedBy(requested_by);
            vehImpl.updateAndSaveChangeOffice(ownerDobj, Util.getUserStateCode(), empCd, vehicleDObj);
            String facesMessages = Util.getLocaleMsg("chngeoffcd_succes");//"RTO Successfully Updated !";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, facesMessages, facesMessages));
            PrimeFaces.current().ajax().update("msgDialog");
            PrimeFaces.current().executeScript("PF('messageDialog').show();");
            render_vehicle_type = false;
            renderTab = false;
            disableRegnNo = true;
            showRegList = false;
            renderBackButton = true;
            return "";
        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vex.getMessage(), vex.getMessage()));
            return "";
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
        return "";
    }

    public OwnerDetailsDobj getOwnerDetail() {
        return ownerDetail;
    }

    public void setOwnerDetail(OwnerDetailsDobj ownerDetail) {
        this.ownerDetail = ownerDetail;
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public static void setLOGGER(Logger LOGGER) {
        VehicleChangeOfficeBean.LOGGER = LOGGER;
    }

    public String getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    public String getChassiNo() {
        return chassiNo;
    }

    public void setChassiNo(String chassiNo) {
        this.chassiNo = chassiNo;
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

    public boolean isRender_vehicle_type() {
        return render_vehicle_type;
    }

    public void setRender_vehicle_type(boolean render_vehicle_type) {
        this.render_vehicle_type = render_vehicle_type;
    }

    public boolean isRenderTab() {
        return renderTab;
    }

    public void setRenderTab(boolean renderTab) {
        this.renderTab = renderTab;
    }

    public boolean isDisableRegnNo() {
        return disableRegnNo;
    }

    public void setDisableRegnNo(boolean disableRegnNo) {
        this.disableRegnNo = disableRegnNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List getOffice_list() {
        return office_list;
    }

    public void setOffice_list(List office_list) {
        this.office_list = office_list;
    }

    public int getOff_to() {
        return off_to;
    }

    public void setOff_to(int off_to) {
        this.off_to = off_to;
    }

    public int getCurrent_offCd() {
        return current_offCd;
    }

    public void setCurrent_offCd(int current_offCd) {
        this.current_offCd = current_offCd;
    }

    public String getRequested_by() {
        return requested_by;
    }

    public void setRequested_by(String requested_by) {
        this.requested_by = requested_by;
    }

    public Date getRequestedOn() {
        return requestedOn;
    }

    public void setRequestedOn(Date requestedOn) {
        this.requestedOn = requestedOn;
    }

    public List<OwnerDetailsDobj> getRegnNameList() {
        return regnNameList;
    }

    public void setRegnNameList(List<OwnerDetailsDobj> regnNameList) {
        this.regnNameList = regnNameList;
    }

    public boolean isShowRegList() {
        return showRegList;
    }

    public void setShowRegList(boolean showRegList) {
        this.showRegList = showRegList;
    }

    public boolean isRenderBackButton() {
        return renderBackButton;
    }

    public void setRenderBackButton(boolean renderBackButton) {
        this.renderBackButton = renderBackButton;
    }

    public String getFacesMessages() {
        return facesMessages;
    }

    public void setFacesMessages(String facesMessages) {
        this.facesMessages = facesMessages;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    /**
     * @return the enterOtp
     */
    public String getEnterOtp() {
        return enterOtp;
    }

    /**
     * @param enterOtp the enterOtp to set
     */
    public void setEnterOtp(String enterOtp) {
        this.enterOtp = enterOtp;
    }

    /**
     * @return the otp
     */
    public String getOtp() {
        return otp;
    }

    /**
     * @param otp the otp to set
     */
    public void setOtp(String otp) {
        this.otp = otp;
    }
}
