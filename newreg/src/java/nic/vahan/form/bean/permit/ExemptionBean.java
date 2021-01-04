/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.PermitDetailDobj;
import nic.vahan.form.dobj.permit.ExemptionDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.form.impl.permit.PermitDetailImpl;
import nic.vahan.form.impl.permit.ExemptionImpl;
import nic.vahan.form.impl.permit.PermitOwnerDetailImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import static nic.vahan.server.ServerUtil.Compare;

/**
 *
 * @author hcl
 */
@ManagedBean(name = "pmtExem")
@ViewScoped
public class ExemptionBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(ExemptionBean.class);
    private ExemptionDobj exemDobj = new ExemptionDobj();
    private ExemptionDobj exemDobjPrv;
    @ManagedProperty(value = "#{Permit_Dtls_bean}")
    private PermitDetailBean permit_Dtls_bean;
    @ManagedProperty(value = "#{pmt_owner_dtls}")
    private PermitOwnerDetailBean pmtOwnerBean;
    PermitOwnerDetailDobj owner_dobj = null;
    PermitOwnerDetailImpl ownerImpl = null;
    private OwnerDetailsDobj ownerDetail;
    PermitDetailDobj pmtDobj = null;
    private boolean exemMoney = false;
    private boolean exemMoneyDisable = false;
    private String amountTaken = "1";
    private String app_no_msg;
    private String header;
    private boolean eApplshowHide = false;
    private List<ComparisonBean> compBeanList = new ArrayList<ComparisonBean>();
    private List<ComparisonBean> prevChangedDataList;
    private List exemPurposeArrayList = new ArrayList();
    private boolean exemPurposeDisable = false;
    private boolean pmtDtlsShow = false;
    private boolean fitDtlsShow = false;
    private boolean parkDtlsShow = false;
    private boolean registrationPanal = true;

    public ExemptionBean() {
        String[][] data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
        for (int i = 0; i < data.length; i++) {
            if (data[i][0].equals(String.valueOf(TableConstants.VM_PMT_EXEMPTION_PUR_CD))
                    || data[i][0].equals(String.valueOf(TableConstants.VM_FIT_EXEMPTION_PUR_CD))
                    || data[i][0].equals(String.valueOf(TableConstants.VM_PARK_EXEMPTION_PUR_CD))) {
                exemPurposeArrayList.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
    }

    @PostConstruct
    public void init() {
        try {
            int action_cd = appl_details.getCurrent_action_cd();
            if (action_cd == TableConstants.TM_ROLE_EXEMPTION_APPL_ENTRY) {
                header = "Exemption Application Entry";
                eApplshowHide = true;
            } else if (action_cd == TableConstants.TM_ROLE_PMT_EXEMPTION_VERIFICATION) {
                header = "Permit Fee/Fine Exemption Of Verification";
                registrationPanal = false;
                eApplshowHide = false;
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                showSaveDtls(appl_details.getAppl_no());
                exemPurposeDisable = true;
                onPurposeSelect();
            } else if (action_cd == TableConstants.TM_ROLE_PARK_EXEMPTION_VERIFICATION) {
                header = "Parking Fee/Fine Exemption Of Verification";
                registrationPanal = false;
                eApplshowHide = false;
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                showSaveDtls(appl_details.getAppl_no());
                exemPurposeDisable = true;
                onPurposeSelect();
            } else if (action_cd == TableConstants.TM_ROLE_FITNESS_EXEMPTION_VERIFICATION) {
                header = "Fitness Fee/Fine Exemption Of Verification";
                registrationPanal = false;
                eApplshowHide = false;
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                showSaveDtls(appl_details.getAppl_no());
                exemPurposeDisable = true;
                onPurposeSelect();
            } else if (action_cd == TableConstants.TM_ROLE_PMT_EXEMPTION_APPROVAL) {
                registrationPanal = false;
                header = "Permit Fee/Fine Exemption Of Approval";
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                eApplshowHide = false;
                showSaveDtls(appl_details.getAppl_no());
                exemPurposeDisable = true;
                onPurposeSelect();
            } else if (action_cd == TableConstants.TM_ROLE_PARK_EXEMPTION_APPROVAL) {
                registrationPanal = false;
                header = "Parking Fee/Fine Exemption Of Approval";
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                eApplshowHide = false;
                showSaveDtls(appl_details.getAppl_no());
                exemPurposeDisable = true;
                onPurposeSelect();
            } else if (action_cd == TableConstants.TM_ROLE_FITNESS_EXEMPTION_APPROVAL) {
                registrationPanal = false;
                header = "Fitness Fee/Fine Exemption Of Approval";
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                eApplshowHide = false;
                showSaveDtls(appl_details.getAppl_no());
                exemPurposeDisable = true;
                onPurposeSelect();
            }
        } catch (Exception e) {
        }
    }

    public void get_details() {

        owner_dobj = new PermitOwnerDetailDobj();
        ownerImpl = new PermitOwnerDetailImpl();
        try {
            OwnerImpl owner_Impl = new OwnerImpl();
            List<OwnerDetailsDobj> ownerDetailsDobjList = owner_Impl.getOwnerDetailsList(getExemDobj().getRegn_no().toUpperCase(), null);
            ownerDetailsDobjList = owner_Impl.getStateWiseOwnerList(ownerDetailsDobjList, Util.getUserStateCode());
            if (ownerDetailsDobjList != null && ownerDetailsDobjList.size() == 1) {
                ownerDetail = ownerDetailsDobjList.get(0);
            } else if (ownerDetailsDobjList != null && ownerDetailsDobjList.size() >= 2) {
                pmtOwnerBean.setDupRegnList(ownerDetailsDobjList);
                PrimeFaces.current().executeScript("PF('varDupRegNo').show()");
                return;
            }
            if (ownerDetail == null) {
                pmtOwnerBean.setValueReset();
                owner_dobj.setDisable(true);
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Registration Number Not Found."));
                return;
            }
            String str = ServerUtil.applicationStatusForPermit(getExemDobj().getRegn_no().toUpperCase(), Util.getUserStateCode());
            if (!CommonUtils.isNullOrBlank(str)) {
                setApp_no_msg(str);
                PrimeFaces.current().ajax().update("app_num_id");
                PrimeFaces.current().executeScript("PF('appNum').show();");
            } else {
                PermitOwnerDetailDobj dobj = pmtOwnerBean.getOwnerDetailsDobj(ownerDetail);
                owner_dobj.setDisable(true);
                pmtOwnerBean.setValueinDOBJ(dobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void saveExemDtls() {
        try {
            if (exemDobj != null) {
                if (exemDobj.getPur_cd() == TableConstants.VM_PMT_EXEMPTION_PUR_CD && pmtDobj.getPmt_no() != null) {
                    exemDobj.setPmt_no(pmtDobj.getPmt_no());
                } else {
                    exemDobj.setPmt_no("");
                }
                if (exemDobj.getExem_from_date() == null) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Exemption from date Empty"));
                    return;
                }
                if (exemDobj.getExem_to_date() == null) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Exemption to date Empty"));
                    return;
                }
                ExemptionImpl exemImpl = new ExemptionImpl();
                printDialogBox(exemImpl.saveDetailsInVa(exemDobj));
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ve.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", TableConstants.SomthingWentWrong));
        }
    }

    public void printDialogBox(String app_no) {
        if (CommonUtils.isNullOrBlank(app_no)) {
            setApp_no_msg("Application Number is not genrated");
            PrimeFaces.current().executeScript("PF('appNum').show();");
        } else {
            setApp_no_msg("Application Number generated :" + app_no + ". Please note down the Application No for future reference.");
            PrimeFaces.current().executeScript("PF('appNum').show();");
        }
    }

    public void showSaveDtls(String app_no) {
        try {
            ExemptionImpl exemImpl = new ExemptionImpl();
            owner_dobj = new PermitOwnerDetailDobj();
            ownerImpl = new PermitOwnerDetailImpl();
            ExemptionDobj exemDobj = exemImpl.showSaveDetails(app_no);
            if (exemDobj.getExem_amount() > 0 || exemDobj.getFine_to_be_taken() > 0) {
                exemMoneyDisable = exemMoney = true;
                if (exemDobj.getExem_amount() != 0) {
                    setAmountTaken("2");
                } else if (exemDobj.getFine_to_be_taken() != 0) {
                    setAmountTaken("1");
                }
            }
            setExemDobj(exemDobj);
            PrimeFaces.current().ajax().update("exemDtls");
            setExemDobjPrv((ExemptionDobj) exemDobj.clone());
            PermitOwnerDetailDobj dobj = ownerImpl.set_VA_Owner_permit_to_dobj(null, exemDobj.getRegn_no().toUpperCase());
            pmtOwnerBean.setValueinDOBJ(dobj);
            owner_dobj.setDisable(true);
            permit_Dtls_bean.permitComponentReadOnly(true);
            permit_Dtls_bean.set_Permit_Dtls_dobj_to_bean(PermitDetailImpl.getPermitdetails(CommonPermitPrintImpl.getPmtNoThroughVtPermit(exemDobj.getRegn_no().toUpperCase())));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", TableConstants.SomthingWentWrong));
        }
    }

    public void onPurposeSelect() {
        if (exemDobj.getPur_cd() == TableConstants.VM_PMT_EXEMPTION_PUR_CD) {
            fitDtlsShow = parkDtlsShow = false;
            pmtDtlsShow = true;
            permit_Dtls_bean.permitComponentReadOnly(true);
            pmtDobj = PermitDetailImpl.getPermitdetails(CommonPermitPrintImpl.getPmtNoThroughVtPermit(getExemDobj().getRegn_no().toUpperCase()));
            permit_Dtls_bean.set_Permit_Dtls_dobj_to_bean(pmtDobj);
            if (pmtDobj == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Permit Details not found"));
            }
        } else if (exemDobj.getPur_cd() == TableConstants.VM_FIT_EXEMPTION_PUR_CD) {
            pmtDtlsShow = parkDtlsShow = false;
            fitDtlsShow = true;
            exemDobj.setFitValidUpto(new ExemptionImpl().getFitnessDetails(getExemDobj().getRegn_no().toUpperCase()));
            if (CommonUtils.isNullOrBlank(exemDobj.getFitValidUpto())) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Previous fitness details not found"));
            }
        } else if (exemDobj.getPur_cd() == TableConstants.VM_PARK_EXEMPTION_PUR_CD) {
            pmtDtlsShow = fitDtlsShow = false;
            parkDtlsShow = true;
            exemDobj.setParkValidUpto(new ExemptionImpl().getParkingDetails(getExemDobj().getRegn_no().toUpperCase(), Util.getUserStateCode()));
            if (CommonUtils.isNullOrBlank(exemDobj.getParkValidUpto())) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Previous Parking details not found"));
            }
        } else {
        }
    }

    public void pmtExemptedMoneyDetail(AjaxBehaviorEvent event) {
        exemMoneyDisable = exemMoney;
    }

    public ExemptionDobj getExemDobj() {
        return exemDobj;
    }

    public void setExemDobj(ExemptionDobj exemDobj) {
        this.exemDobj = exemDobj;
    }

    public PermitDetailBean getPermit_Dtls_bean() {
        return permit_Dtls_bean;
    }

    public void setPermit_Dtls_bean(PermitDetailBean permit_Dtls_bean) {
        this.permit_Dtls_bean = permit_Dtls_bean;
    }

    public PermitDetailDobj getPmtDobj() {
        return pmtDobj;
    }

    public void setPmtDobj(PermitDetailDobj pmtDobj) {
        this.pmtDobj = pmtDobj;
    }

    public boolean isExemMoney() {
        return exemMoney;
    }

    public void setExemMoney(boolean exemMoney) {
        this.exemMoney = exemMoney;
    }

    public boolean isExemMoneyDisable() {
        return exemMoneyDisable;
    }

    public void setExemMoneyDisable(boolean exemMoneyDisable) {
        this.exemMoneyDisable = exemMoneyDisable;
    }

    public String getAmountTaken() {
        return amountTaken;
    }

    public void setAmountTaken(String amountTaken) {
        this.amountTaken = amountTaken;
    }

    public PermitOwnerDetailBean getPmtOwnerBean() {
        return pmtOwnerBean;
    }

    public void setPmtOwnerBean(PermitOwnerDetailBean pmtOwnerBean) {
        this.pmtOwnerBean = pmtOwnerBean;
    }

    public PermitOwnerDetailDobj getOwner_dobj() {
        return owner_dobj;
    }

    public void setOwner_dobj(PermitOwnerDetailDobj owner_dobj) {
        this.owner_dobj = owner_dobj;
    }

    public PermitOwnerDetailImpl getOwnerImpl() {
        return ownerImpl;
    }

    public void setOwnerImpl(PermitOwnerDetailImpl ownerImpl) {
        this.ownerImpl = ownerImpl;
    }

    public String getApp_no_msg() {
        return app_no_msg;
    }

    public void setApp_no_msg(String app_no_msg) {
        this.app_no_msg = app_no_msg;
    }

    public boolean iseApplshowHide() {
        return eApplshowHide;
    }

    public void seteApplshowHide(boolean eApplshowHide) {
        this.eApplshowHide = eApplshowHide;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public ExemptionDobj getExemDobjPrv() {
        return exemDobjPrv;
    }

    public void setExemDobjPrv(ExemptionDobj exemDobjPrv) {
        this.exemDobjPrv = exemDobjPrv;
    }

    public List getExemPurposeArrayList() {
        return exemPurposeArrayList;
    }

    public void setExemPurposeArrayList(List exemPurposeArrayList) {
        this.exemPurposeArrayList = exemPurposeArrayList;
    }

    public boolean isPmtDtlsShow() {
        return pmtDtlsShow;
    }

    public void setPmtDtlsShow(boolean pmtDtlsShow) {
        this.pmtDtlsShow = pmtDtlsShow;
    }

    public boolean isFitDtlsShow() {
        return fitDtlsShow;
    }

    public void setFitDtlsShow(boolean fitDtlsShow) {
        this.fitDtlsShow = fitDtlsShow;
    }

    public boolean isParkDtlsShow() {
        return parkDtlsShow;
    }

    public void setParkDtlsShow(boolean parkDtlsShow) {
        this.parkDtlsShow = parkDtlsShow;
    }

    public boolean isExemPurposeDisable() {
        return exemPurposeDisable;
    }

    public void setExemPurposeDisable(boolean exemPurposeDisable) {
        this.exemPurposeDisable = exemPurposeDisable;
    }

    public boolean isRegistrationPanal() {
        return registrationPanal;
    }

    public void setRegistrationPanal(boolean registrationPanal) {
        this.registrationPanal = registrationPanal;
    }

    @Override
    public String save() {
        try {
            ExemptionImpl impl = new ExemptionImpl();
            impl.onlySaveDetails(appl_details.getAppl_no(), exemDobj, ComparisonBeanImpl.changedDataContents(compareChanges()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return "seatwork";
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        try {
            if (exemDobjPrv == null) {
                return getCompBeanList();
            }
            getCompBeanList().clear();
            Compare("Exempted From Date", exemDobjPrv.getExem_from_date(), exemDobj.getExem_from_date(), (ArrayList) getCompBeanList());
            Compare("Exempted To Date", exemDobjPrv.getExem_to_date(), exemDobj.getExem_to_date(), (ArrayList) getCompBeanList());
            Compare("Exempted Amount", exemDobjPrv.getExem_amount(), exemDobj.getExem_amount(), (ArrayList) getCompBeanList());
            Compare("Fine To Be Taken", exemDobjPrv.getFine_to_be_taken(), exemDobj.getFine_to_be_taken(), (ArrayList) getCompBeanList());
            Compare("Order By", exemDobjPrv.getOrder_by(), exemDobj.getOrder_by(), (ArrayList) getCompBeanList());
            Compare("Order Number", exemDobjPrv.getOrder_no(), exemDobj.getOrder_no(), (ArrayList) getCompBeanList());
            Compare("Order Date", exemDobjPrv.getOrder_dt(), exemDobj.getOrder_dt(), (ArrayList) getCompBeanList());
            Compare("Exempted Reason", exemDobjPrv.getExem_reason(), exemDobj.getExem_reason(), (ArrayList) getCompBeanList());
        } catch (Exception ex) {
            LOGGER.error("Application No : " + exemDobjPrv.getAppl_no() + "--" + ex.toString() + " " + ex.getStackTrace()[0]);
        }
        return getCompBeanList();
    }

    @Override
    public String saveAndMoveFile() {
        try {
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setPur_cd(appl_details.getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            status.setCurrent_role(appl_details.getCurrent_role());
            ExemptionImpl exemImpl = null;
            if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_EXEMPTION_VERIFICATION
                    || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PARK_EXEMPTION_VERIFICATION
                    || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_FITNESS_EXEMPTION_VERIFICATION) {
                exemImpl = new ExemptionImpl();
                exemImpl.saveAndMoveforVarification(appl_details.getAppl_no(), exemDobj, ComparisonBeanImpl.changedDataContents(compareChanges()), status);
            } else if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_EXEMPTION_APPROVAL
                    || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PARK_EXEMPTION_APPROVAL
                    || appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_FITNESS_EXEMPTION_APPROVAL) {
                exemImpl = new ExemptionImpl();
                exemImpl.saveAndMoveforApproval(appl_details.getAppl_no(), appl_details.getRegn_no(), exemDobj, ComparisonBeanImpl.changedDataContents(compareChanges()), status);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return "seatwork";
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
}
