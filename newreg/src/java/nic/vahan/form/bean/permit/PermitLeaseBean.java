package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitLeaseDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.form.impl.permit.PassengerPermitDetailImpl;
import nic.vahan.form.impl.permit.PermitLeaseImpl;
import nic.vahan.form.impl.permit.PermitOwnerDetailImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.primefaces.PrimeFaces;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;

/**
 *
 * @author manoj
 */
@ManagedBean(name = "pmtLease")
@ViewScoped
public class PermitLeaseBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(PermitLeaseBean.class);
    private PermitLeaseDobj countDobj = new PermitLeaseDobj();
    private PermitOwnerDetailDobj ownerDobjPrv;
    private PermitLeaseDobj countDobjPrv;
    private List areaTypeList = new ArrayList<>();
    private List pmtTypeList = new ArrayList<>();
    private List periodModeList = new ArrayList<>();
    private boolean regnNoDisable = false;
    @ManagedProperty(value = "#{pmt_owner_dtls}")
    private PermitOwnerDetailBean ownerBean;
    PermitOwnerDetailDobj owner_dobj = null;
    private String app_no_msg;
    private String header;
    private boolean panelHideShow = true;
    private boolean disablePmtDtls = true;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private List<ComparisonBean> prevChangedDataList;
    private String selectOneRadio = "1";
    PassengerPermitDetailImpl impl = new PassengerPermitDetailImpl();
    private String owner_name;
    private String f_name;
    private String leaseRegnNo;
    private String address;
    private String city;
    private String pincode;
    private String periodMode = "-1";
    private Integer periodValue;
    private boolean periodModeDisable = false;
    private boolean periodValueDisable = false;
    @ManagedProperty(value = "#{pmtDtlsBean}")
    private PermitCheckDetailsBean pmtCheckDtsl;
    private PassengerPermitDetailDobj permit_dobj = new PassengerPermitDetailDobj();

    public PermitLeaseBean() {
        String[][] data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
        for (int i = 0; i < data.length; i++) {
            pmtTypeList.add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.VM_TAX_MODE.getData();
        periodModeList.add(new SelectItem("-1", "--- Select Period Mode ---"));
        for (int i = 0; i < data.length; i++) {
            if (("M").equalsIgnoreCase(data[i][0]) || ("Y").equalsIgnoreCase(data[i][0])) {
                periodModeList.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
        if (getAppl_details().getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_LEASE_PERMIT_VERIFICATION
                || getAppl_details().getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_LEASE_PERMIT_APPROVAL
                || (getAppl_details().getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_LEASE_PERMIT_ENTRY
                && !CommonUtils.isNullOrBlank(getAppl_details().getAppl_no()))) {
            owner_dobj = new PermitOwnerDetailDobj();
            fillDtlsVeriApproStage();
        }
    }

    @PostConstruct
    public void init() {
        try {

            if (getAppl_details().getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_LEASE_PERMIT_ENTRY
                    && !CommonUtils.isNullOrBlank(getAppl_details().getAppl_no())) {
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                header = "PERMIT LEASE RE-ENTRY";
                ownerBean.getPmt_dobj().setFlage(true);
                ownerBean.setValueinDOBJ(owner_dobj);
                pmtCheckDtsl.getAlldetails(getAppl_details().getRegn_no(), null, owner_dobj.getState_cd(), owner_dobj.getOff_cd());
                disablePmtDtls = false;

            } else if (getAppl_details().getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_LEASE_PERMIT_ENTRY
                    && CommonUtils.isNullOrBlank(getAppl_details().getAppl_no())) {
                header = "PERMIT ON LEASE APPLICATION ENTRY";
                disablePmtDtls = true;

            } else if (getAppl_details().getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_LEASE_PERMIT_VERIFICATION) {
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                header = "PERMIT ON LEASE VERIFICATION";
                ownerBean.getPmt_dobj().setFlage(true);
                ownerBean.setValueinDOBJ(owner_dobj);
                pmtCheckDtsl.getAlldetails(getAppl_details().getRegn_no(), null, owner_dobj.getState_cd(), owner_dobj.getOff_cd());
                disablePmtDtls = true;

            } else if (getAppl_details().getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_LEASE_PERMIT_APPROVAL) {
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                header = "PERMIT ON LEASE APPROVAL";
                ownerBean.setValueinDOBJ(owner_dobj);
                ownerBean.getPmt_dobj().setFlage(true);
                pmtCheckDtsl.getAlldetails(getAppl_details().getRegn_no(), null, owner_dobj.getState_cd(), owner_dobj.getOff_cd());
                disablePmtDtls = true;

            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void check_Time_Period() {
        if (("M").equalsIgnoreCase(getPeriodMode()) || ("Y").equalsIgnoreCase(getPeriodMode())) {
            setPeriodValue(null);
            setPeriodValueDisable(false);
        } else {
            setPeriodValue(null);
            setPeriodValueDisable(true);
        }
    }

    public final void fillDtlsVeriApproStage() {
        try {
            panelHideShow = false;
            PassengerPermitDetailImpl passImp = new PassengerPermitDetailImpl();
            PermitOwnerDetailImpl ownImpl = new PermitOwnerDetailImpl();
            owner_dobj = ownImpl.set_Owner_appl_db_to_dobj(appl_details.getRegn_no(), Util.getUserStateCode());
            PassengerPermitDetailDobj pmt_dobj = passImp.set_vt_permit_regnNo_to_dobj(appl_details.getRegn_no().toUpperCase(), "");
            if (owner_dobj == null) {
                JSFUtils.showMessagesInDialog("Error", "Owner details not found", FacesMessage.SEVERITY_ERROR);
            } else {
                ownerDobjPrv = new PermitOwnerDetailDobj();
                ownerDobjPrv = (PermitOwnerDetailDobj) owner_dobj.clone();
            }
            PermitLeaseImpl counImpl = new PermitLeaseImpl();
            PermitLeaseDobj leaseDobj = counImpl.getLeaseDeatilForApplNo(appl_details.getAppl_no());
            if (leaseDobj == null) {
                JSFUtils.showMessagesInDialog("Error", "Lease details not found", FacesMessage.SEVERITY_ERROR);
            } else {
                if (pmt_dobj != null) {
                    setPermit_dobj(pmt_dobj);
                    leaseDobj.setPmt_type(Integer.parseInt(pmt_dobj.getPmt_type()));
                    leaseDobj.setValid_from(pmt_dobj.getValid_from());
                    leaseDobj.setValid_upto(pmt_dobj.getValid_upto());
                } else {
                    JSFUtils.showMessagesInDialog("Error", "Permit details not found", FacesMessage.SEVERITY_ERROR);
                }
                setCountDobj(leaseDobj);
                countDobjPrv = new PermitLeaseDobj();
                countDobjPrv = (PermitLeaseDobj) leaseDobj.clone();
            }
            countDobj.setAppl_no(appl_details.getAppl_no());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void get_details() {
        try {
            String regn_No = "";
            regn_No = CommonPermitPrintImpl.getRegnNoinVtPermit(countDobj.getRegn_no().toUpperCase(), Util.getUserStateCode());
            if (CommonUtils.isNullOrBlank(regn_No)) {
                // throw new VahanException("Please Enter Valid Vehicle Number!");
                JSFUtils.showMessagesInDialog("Error", "Please Enter Valid Vehicle", FacesMessage.SEVERITY_ERROR);
            } else {
                if (!CommonUtils.isNullOrBlank(regn_No)) {
                    String str = ServerUtil.applicationStatusForPermit(regn_No.trim().toUpperCase(), Util.getUserStateCode());
                    if (!CommonUtils.isNullOrBlank(str)) {
                        showApplPendingDialogBox(str);
                        return;
                    }
                }

                PermitOwnerDetailImpl ownImpl = new PermitOwnerDetailImpl();
                PassengerPermitDetailImpl passImp = new PassengerPermitDetailImpl();
                PermitOwnerDetailDobj ownDobj = ownImpl.set_Owner_appl_db_to_dobj(regn_No, Util.getUserStateCode());
                pmtCheckDtsl.getAlldetails(regn_No, null, ownDobj.getState_cd(), ownDobj.getOff_cd());
                PassengerPermitDetailDobj permit_dobj = passImp.set_vt_permit_regnNo_to_dobj(regn_No.toUpperCase(), "");
                if (permit_dobj != null) {
                    countDobj.setPmt_type(Integer.parseInt(permit_dobj.getPmt_type()));
                    countDobj.setPmt_no(permit_dobj.getPmt_no());
                    countDobj.setValid_from(permit_dobj.getValid_from());
                    countDobj.setValid_upto(permit_dobj.getValid_upto());
                }
                if (ownDobj != null) {
                    ownerBean.setValueinDOBJ(ownDobj);
                } else {
                    ownerBean.getPmt_dobj().setFlage(false);
                }
                regnNoDisable = true;
            }
        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Error", e.getMessage(), FacesMessage.SEVERITY_ERROR);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public String checkPmtValidation() {
        String msg = null;
        msg = CommonPermitPrintImpl.checkPmtValidation(pmtCheckDtsl.getDtlsDobj());
        return msg;
    }

    public void save_details() {
        String errorMsg = "";
        try {
            checkField();
            if (getSelectOneRadio().equals("1")) {
                countDobj.setPur_cd(TableConstants.VM_PMT_LEASE_PUR_CD);
                errorMsg = checkPmtValidation();
                if (!CommonUtils.isNullOrBlank(errorMsg)) {
                    throw new VahanException(errorMsg);
                }
            } else {
                countDobj.setPur_cd(TableConstants.VM_PMT_LEASE_CANCEL_PUR_CD);
            }
            PermitLeaseImpl impl = new PermitLeaseImpl();
            String str = ServerUtil.applicationStatusForPermit(countDobj.getRegn_no().toUpperCase(), Util.getUserStateCode());
            if (CommonUtils.isNullOrBlank(str)) {
                String appl_no = impl.createApplication(countDobj);
                printDialogBox(appl_no);
            } else {
                showApplPendingDialogBox(str);
            }
        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Exception", e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void checkField() throws VahanException {
        try {
            if (CommonUtils.isNullOrBlank(countDobj.getLeaseRegnNo())) {
                throw new VahanException("Please enter Lease Vehicle Number");
            } else if (CommonUtils.isNullOrBlank(countDobj.getOwner_name())) {
                throw new VahanException("Please enter owner name");
            } else if (CommonUtils.isNullOrBlank(countDobj.getF_name())) {
                throw new VahanException("Please enter father name");
            } else if (CommonUtils.isNullOrBlank(countDobj.getPincode())) {
                throw new VahanException("Please enter pincode");
            } else if (CommonUtils.isNullOrBlank(countDobj.getAddress())) {
                throw new VahanException("Please enter owner name");
            } else if (CommonUtils.isNullOrBlank(countDobj.getCity())) {
                throw new VahanException("Please enter city name");
            } else if (countDobj.getPeriod_mode().equalsIgnoreCase("-1") || CommonUtils.isNullOrBlank(countDobj.getPeriod_mode())) {
                throw new VahanException("Please Select Period Mode");
            } else if (countDobj.getPeriod() <= 0) {
                throw new VahanException("Please enter period");
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public void printDialogBox(String app_no) {
        if (!CommonUtils.isNullOrBlank(app_no)) {
            setApp_no_msg("Application Number generated :" + app_no + ". Please note down the Application No for future reference.");
            PrimeFaces.current().executeScript("PF('appNum').show();");
        } else {
            setApp_no_msg("There are some problem to genrate the application");
            PrimeFaces.current().executeScript("PF('appNum').show();");
        }
    }

    public void showApplPendingDialogBox(String str) {
        setApp_no_msg(str);
        PrimeFaces.current().ajax().update("app_num_id");
        PrimeFaces.current().executeScript("PF('appNum').show();");
    }

    @Override
    public String save() {
        PermitLeaseImpl impl = new PermitLeaseImpl();
        try {
            impl.stayOnTheSameStage(appl_details.getAppl_no(), ownerBean.setValueinDOBJ(), countDobj, ComparisonBeanImpl.changedDataContents(compareChanges()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return "seatwork";
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        try {
            if (ownerDobjPrv == null || countDobjPrv == null) {
                return getCompBeanList();
            }
            getCompBeanList().clear();
            if (countDobjPrv.getOwner_name() != null && countDobj.getOwner_name() != null) {
                Compare("Owner Name", countDobjPrv.getOwner_name().toUpperCase(), countDobj.getOwner_name().toUpperCase(), (ArrayList) getCompBeanList());
            }
            if (countDobjPrv.getF_name() != null && countDobj.getF_name() != null) {
                Compare("Father/Husband's Name", countDobjPrv.getF_name().toUpperCase(), countDobj.getF_name().toUpperCase(), (ArrayList) getCompBeanList());
            }
            if (countDobjPrv.getAddress() != null && countDobj.getAddress() != null) {
                Compare("Current House No. & Street Name", countDobjPrv.getAddress().toUpperCase(), countDobj.getAddress().toUpperCase(), (ArrayList) getCompBeanList());
            }
            if (countDobjPrv.getCity() != null && countDobj.getCity() != null) {
                Compare("Current Village/Town/City ", countDobjPrv.getCity().toUpperCase(), countDobj.getCity().toUpperCase(), (ArrayList) getCompBeanList());
            }
            if (String.valueOf(countDobjPrv.getPincode()) != null && countDobj.getPincode() != null) {
                Compare("Current Pin Code", String.valueOf(countDobjPrv.getPincode()), countDobj.getPincode(), (ArrayList) getCompBeanList());
            }
            Compare("Period Mode", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) getPeriodModeList(), countDobjPrv.getPeriod_mode()), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) getPeriodModeList(), countDobj.getPeriod_mode()), (ArrayList) getCompBeanList());
            Compare("Period", String.valueOf(countDobjPrv.getPeriod()), String.valueOf(countDobj.getPeriod()), (ArrayList) getCompBeanList());


        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return getCompBeanList();
    }

    @Override
    public String saveAndMoveFile() {
        String return_location = "";
        PermitLeaseImpl impl = new PermitLeaseImpl();
        try {
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setPur_cd(appl_details.getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            status.setCurrent_role(appl_details.getCurrent_role());
            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)) {
                if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_LEASE_PERMIT_VERIFICATION
                        || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_LEASE_PERMIT_ENTRY) {
                    return_location = impl.verificationStage(appl_details.getAppl_no(), status, ownerBean.setValueinDOBJ(), countDobj, ComparisonBeanImpl.changedDataContents(compareChanges()));
                }
                if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_LEASE_PERMIT_APPROVAL) {
                    countDobj.setPur_cd(appl_details.getPur_cd());
                    return_location = impl.approvalStage(appl_details.getAppl_no(), appl_details.getRegn_no(), status, ownerBean.setValueinDOBJ(), countDobj, permit_dobj);
                }
            }
//            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)) {
//                status.setPrev_action_cd_selected(app_disapp_dobj.getPre_action_code());
//                reback(status, ComparisonBeanImpl.changedDataContents(compareChanges()));
//                return_location = "seatwork";
//            }
        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Exception", e.getMessage(), FacesMessage.SEVERITY_ERROR);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return return_location;
    }

    @Override
    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        return prevChangedDataList;
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        this.prevChangedDataList = prevChangedDataList;
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public PermitLeaseDobj getCountDobj() {
        return countDobj;
    }

    public void setCountDobj(PermitLeaseDobj countDobj) {
        this.countDobj = countDobj;
    }

    public List getPmtTypeList() {
        return pmtTypeList;
    }

    public void setPmtTypeList(List pmtTypeList) {
        this.pmtTypeList = pmtTypeList;
    }

    public List getPeriodModeList() {
        return periodModeList;
    }

    public void setPeriodModeList(List periodModeList) {
        this.periodModeList = periodModeList;
    }

    public PermitOwnerDetailBean getOwnerBean() {
        return ownerBean;
    }

    public void setOwnerBean(PermitOwnerDetailBean ownerBean) {
        this.ownerBean = ownerBean;
    }

    public boolean isRegnNoDisable() {
        return regnNoDisable;
    }

    public void setRegnNoDisable(boolean regnNoDisable) {
        this.regnNoDisable = regnNoDisable;
    }

    public String getApp_no_msg() {
        return app_no_msg;
    }

    public void setApp_no_msg(String app_no_msg) {
        this.app_no_msg = app_no_msg;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public boolean isPanelHideShow() {
        return panelHideShow;
    }

    public void setPanelHideShow(boolean panelHideShow) {
        this.panelHideShow = panelHideShow;
    }

    public PermitOwnerDetailDobj getOwnerDobjPrv() {
        return ownerDobjPrv;
    }

    public void setOwnerDobjPrv(PermitOwnerDetailDobj ownerDobjPrv) {
        this.ownerDobjPrv = ownerDobjPrv;
    }

    public PermitLeaseDobj getCountDobjPrv() {
        return countDobjPrv;
    }

    public void setCountDobjPrv(PermitLeaseDobj countDobjPrv) {
        this.countDobjPrv = countDobjPrv;
    }

    public PermitOwnerDetailDobj getOwner_dobj() {
        return owner_dobj;
    }

    public void setOwner_dobj(PermitOwnerDetailDobj owner_dobj) {
        this.owner_dobj = owner_dobj;
    }

    public String getSelectOneRadio() {
        return selectOneRadio;
    }

    public void setSelectOneRadio(String selectOneRadio) {
        this.selectOneRadio = selectOneRadio;
    }

    /**
     * @return the areaTypeList
     */
    public List getAreaTypeList() {
        return areaTypeList;
    }

    /**
     * @param areaTypeList the areaTypeList to set
     */
    public void setAreaTypeList(List areaTypeList) {
        this.areaTypeList = areaTypeList;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getF_name() {
        return f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getLeaseRegnNo() {
        return leaseRegnNo;
    }

    public void setLeaseRegnNo(String leaseRegnNo) {
        this.leaseRegnNo = leaseRegnNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isDisablePmtDtls() {
        return disablePmtDtls;
    }

    public void setDisablePmtDtls(boolean disablePmtDtls) {
        this.disablePmtDtls = disablePmtDtls;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPeriodMode() {
        return periodMode;
    }

    public void setPeriodMode(String periodMode) {
        this.periodMode = periodMode;
    }

    public Integer getPeriodValue() {
        return periodValue;
    }

    public void setPeriodValue(Integer periodValue) {
        this.periodValue = periodValue;
    }

    public boolean isPeriodModeDisable() {
        return periodModeDisable;
    }

    public void setPeriodModeDisable(boolean periodModeDisable) {
        this.periodModeDisable = periodModeDisable;
    }

    public boolean isPeriodValueDisable() {
        return periodValueDisable;
    }

    public void setPeriodValueDisable(boolean periodValueDisable) {
        this.periodValueDisable = periodValueDisable;
    }

    public PermitCheckDetailsBean getPmtCheckDtsl() {
        return pmtCheckDtsl;
    }

    public void setPmtCheckDtsl(PermitCheckDetailsBean pmtCheckDtsl) {
        this.pmtCheckDtsl = pmtCheckDtsl;
    }

    public PassengerPermitDetailDobj getPermit_dobj() {
        return permit_dobj;
    }

    public void setPermit_dobj(PassengerPermitDetailDobj permit_dobj) {
        this.permit_dobj = permit_dobj;
    }
}
