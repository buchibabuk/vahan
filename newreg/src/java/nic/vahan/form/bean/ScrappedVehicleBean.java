/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import nic.java.util.CommonUtils;
import nic.rto.vahan.common.VahanException;
import static nic.vahan.CommonUtils.FormulaUtils.fillVehicleParametersFromDobj;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.dobj.ScrappedVehicleDobj;
import nic.vahan.form.impl.ScrappedVehicleImpl;
import nic.vahan.form.impl.dealer.OfficeCorrectionImpl;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;

/**
 *
 * @author kaptan singh
 */
@ManagedBean(name = "ScrappedVehicle")
@ViewScoped
public class ScrappedVehicleBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ScrappedVehicleBean.class);
    private OwnerDetailsDobj ownerDetail;
    private List<OwnerDetailsDobj> dupRegnList = new ArrayList<>();
    private ScrappedVehicleDobj scrappedVehicleDobj = new ScrappedVehicleDobj();
    private ScrappedVehicleDobj preScrappedVehicleDobj = new ScrappedVehicleDobj();
    Date maxDate = new Date();
    private ScrappedVehicleImpl impl = new ScrappedVehicleImpl();
    private int actionCode;
    private int purCode;
    private String vehicleNo;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private List<ComparisonBean> prevChangedDataList;
    TmConfigurationDobj config_dobj = null;
    boolean showCbxRetainVehicleNo;
    private List scrapReasonList = new ArrayList();
    private boolean disableRetention = false;

    public ScrappedVehicleBean() {
        try {
            String[][] data = MasterTableFiller.masterTables.VM_SCRAP_REASONS.getData();
            scrapReasonList.add(new SelectItem("-1", "---Select---"));
            for (int i = 0; i < data.length; i++) {
                scrapReasonList.add(new SelectItem(data[i][0], data[i][1]));
            }
            purCode = appl_details.getPur_cd();
            actionCode = appl_details.getCurrent_action_cd();
            vehicleNo = appl_details.getRegn_no();
            OwnerImpl owner_Impl = new OwnerImpl();
            List<OwnerDetailsDobj> ownerDetailsDobjList = owner_Impl.getOwnerDetailsList(appl_details.getRegn_no().toUpperCase().trim(), null);
            ownerDetailsDobjList = owner_Impl.getStateWiseOwnerList(ownerDetailsDobjList, Util.getUserStateCode());
            ownerDetail = ownerDetailsDobjList.get(0);
            ownerDetail.setOwnerIdentity(null);
            ScrappedVehicleDobj dobj = impl.getdelatils(purCode, actionCode, vehicleNo, appl_details.getAppl_no());
            if (dobj != null) {
                scrappedVehicleDobj = (ScrappedVehicleDobj) dobj.clone();
                preScrappedVehicleDobj = (ScrappedVehicleDobj) dobj.clone();
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(appl_details.getAppl_no(), purCode);
            }
            Owner_dobj owner_dobj = owner_Impl.getOwnerDobj(ownerDetail);
            VehicleParameters vehParameters = fillVehicleParametersFromDobj(owner_dobj);
            config_dobj = Util.getTmConfiguration();
            if (config_dobj != null && config_dobj.isScrap_veh_no_retain()) {
                if (!isCondition(replaceTagValues(config_dobj.getScrap_ret_age(), vehParameters), "getScrapRetAge")) {
                    showCbxRetainVehicleNo = false;
                } else if ("UP".contains(Util.getUserStateCode()) && ServerUtil.isTransport(owner_dobj.getVh_class(), owner_dobj)) {
                    showCbxRetainVehicleNo = false;
                } else {
                    showCbxRetainVehicleNo = true;
                }
            } else {
                showCbxRetainVehicleNo = false;
            }
            if (appl_details != null && showCbxRetainVehicleNo) {
                String rcptNo = OfficeCorrectionImpl.getRcptNoForFeePaidForPurpose(appl_details.getAppl_no(), TableConstants.SWAPPING_REGN_PUR_CD);
                if (!CommonUtils.isNullOrBlank(rcptNo)) {
                    disableRetention = true;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    @Override
    public String save() {
        String returnLocation = "";
        boolean flag = true;
        flag = fillAndValidateValues(flag);
        Status_dobj statusDobj = new Status_dobj();
        statusDobj.setAction_cd(appl_details.getCurrent_action_cd());
        statusDobj.setAppl_dt(appl_details.getAppl_dt());
        statusDobj.setAppl_no(appl_details.getAppl_no());
        statusDobj.setRegn_no(appl_details.getRegn_no());
        statusDobj.setPur_cd(appl_details.getPur_cd());
        statusDobj.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
        statusDobj.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
        statusDobj.setStatus(getApp_disapp_dobj().getNew_status());
        statusDobj.setCurrent_role(appl_details.getCurrent_role());
        if (flag) {
            boolean save = false;
            try {
                save = impl.saveScrrapeDetails(scrappedVehicleDobj, statusDobj, ComparisonBeanImpl.changedDataContents(compareChanges()), config_dobj.isScrap_veh_no_retain());
                if (save) {
                    JSFUtils.setFacesMessage("Record Successfully Saved", "message", JSFUtils.INFO);
                    returnLocation = "seatwork";
                } else {
                    JSFUtils.setFacesMessage("There is some problem in saving data. Try after some time", "message", JSFUtils.ERROR);
                    returnLocation = "";
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return returnLocation;
    }

    private boolean fillAndValidateValues(boolean flag) {
        if ("".equalsIgnoreCase(scrappedVehicleDobj.getAgency_name())) {
            flag = false;
        }
        if ("".equalsIgnoreCase(scrappedVehicleDobj.getAgency_address())) {
            flag = false;
        }
        if ("".equalsIgnoreCase(scrappedVehicleDobj.getNo_dues_cert_no())) {
            flag = false;
        }
        if (scrappedVehicleDobj.getNo_dues_issue_dt() == null) {
            flag = false;
        }
        if ("".equalsIgnoreCase(scrappedVehicleDobj.getScrap_cert_no())) {
            flag = false;
        }
        if (scrappedVehicleDobj.getScrap_cert_issue_dt() == null) {
            flag = false;
        }
        if ("-1".equalsIgnoreCase(scrappedVehicleDobj.getScrap_reason())) {
            flag = false;
        }
        return flag;
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        compBeanList.clear();
        Compare("Scrapping Agency Name", preScrappedVehicleDobj.getAgency_name(), scrappedVehicleDobj.getAgency_name(), compBeanList);
        Compare("Scrapping Agency Adress", preScrappedVehicleDobj.getAgency_address(), scrappedVehicleDobj.getAgency_address(), compBeanList);
        Compare("No Dues Date", preScrappedVehicleDobj.getNo_dues_issue_dt(), scrappedVehicleDobj.getNo_dues_issue_dt(), compBeanList);
        Compare("Scraping Cert Date", preScrappedVehicleDobj.getScrap_cert_issue_dt(), scrappedVehicleDobj.getScrap_cert_issue_dt(), compBeanList);
        Compare("No Dues Cert No", preScrappedVehicleDobj.getNo_dues_cert_no(), scrappedVehicleDobj.getNo_dues_cert_no(), compBeanList);
        Compare("Scraping Cert No", preScrappedVehicleDobj.getScrap_cert_no(), scrappedVehicleDobj.getScrap_cert_no(), compBeanList);
        Compare("Scrapping Reason", preScrappedVehicleDobj.getScrap_reason(), scrappedVehicleDobj.getScrap_reason(), compBeanList);
        Compare("Retention ", Boolean.toString(preScrappedVehicleDobj.isRetain_regn_no()), Boolean.toString(scrappedVehicleDobj.isRetain_regn_no()), compBeanList);
        return compBeanList;
    }

    @Override
    public String saveAndMoveFile() {
        String returnLocation = "";
        boolean flag = true;
        flag = fillAndValidateValues(flag);
        Status_dobj statusDobj = new Status_dobj();
        statusDobj.setAction_cd(appl_details.getCurrent_action_cd());
        statusDobj.setAppl_dt(appl_details.getAppl_dt());
        statusDobj.setAppl_no(appl_details.getAppl_no());
        statusDobj.setRegn_no(appl_details.getRegn_no());
        statusDobj.setPur_cd(appl_details.getPur_cd());
        statusDobj.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
        statusDobj.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
        statusDobj.setStatus(getApp_disapp_dobj().getNew_status());
        statusDobj.setCurrent_role(appl_details.getCurrent_role());
        if (flag) {
            try {
                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE) && this.appl_details.isDocVerifyForFacelessAppl()) {
                    String[] notVerifiedDocDetails = ServerUtil.verifyDocUploaded(appl_details.getAppl_no(), getAppl_details().getCurrent_action_cd());
                    if (notVerifiedDocDetails != null) {
                        appl_details.setNotVerifiedDocdetails(notVerifiedDocDetails[1]);
                        throw new VahanException(notVerifiedDocDetails[0]);
                    }
                }
                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                        || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                    if ((String.valueOf(TableConstants.TM_ROLE_SCRAPPED_VEHICLE_ENTRY) == null ? String.valueOf(appl_details.getCurrent_action_cd()) == null : String.valueOf(TableConstants.TM_ROLE_SCRAPPED_VEHICLE_ENTRY).equals(String.valueOf(appl_details.getCurrent_action_cd())))) {
                        impl.isSaveAndMove(scrappedVehicleDobj, statusDobj, ComparisonBeanImpl.changedDataContents(compareChanges()), config_dobj.isScrap_veh_no_retain());
                        returnLocation = "seatwork";
                    } else if ((String.valueOf(TableConstants.TM_ROLE_SCRAPPED_VEHICLE_VERIFICATION) == null ? String.valueOf(appl_details.getCurrent_action_cd()) == null : String.valueOf(TableConstants.TM_ROLE_SCRAPPED_VEHICLE_VERIFICATION).equals(String.valueOf(appl_details.getCurrent_action_cd())))) {
                        impl.isVarification(scrappedVehicleDobj, statusDobj, ComparisonBeanImpl.changedDataContents(compareChanges()));
                        returnLocation = "seatwork";
                    } else if ((String.valueOf(TableConstants.TM_ROLE_SCRAPPED_VEHICLE_APPROVAL) == null ? String.valueOf(appl_details.getCurrent_action_cd()) == null : String.valueOf(TableConstants.TM_ROLE_SCRAPPED_VEHICLE_APPROVAL).equals(String.valueOf(appl_details.getCurrent_action_cd())))) {
                        impl.isApproval(scrappedVehicleDobj, statusDobj, ComparisonBeanImpl.changedDataContents(compareChanges()), ownerDetail, config_dobj.isScrap_veh_no_retain(), appl_details.getOwnerDobj());
                        returnLocation = "seatwork";
                    }
                }

                if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                    ServerUtil.sendSmsInFacelessService(appl_details.getAppl_no(), Util.getUserStateCode(), Util.getUserSeatOffCode(), appl_details.getRegn_no(), appl_details.getNotVerifiedDocdetails());
                }
                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)) {
                    statusDobj.setPrev_action_cd_selected(app_disapp_dobj.getPre_action_code());
                    impl.reback(scrappedVehicleDobj, statusDobj, ComparisonBeanImpl.changedDataContents(compareChanges()));
                    returnLocation = "seatwork";
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return returnLocation;
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

    /**
     * @return the scrappedVehicleDobj
     */
    public ScrappedVehicleDobj getScrappedVehicleDobj() {
        return scrappedVehicleDobj;
    }

    /**
     * @param scrappedVehicleDobj the scrappedVehicleDobj to set
     */
    public void setScrappedVehicleDobj(ScrappedVehicleDobj scrappedVehicleDobj) {
        this.scrappedVehicleDobj = scrappedVehicleDobj;
    }

    public OwnerDetailsDobj getOwnerDetail() {
        return ownerDetail;
    }

    public void setOwnerDetail(OwnerDetailsDobj ownerDetail) {
        this.ownerDetail = ownerDetail;
    }

    public List<OwnerDetailsDobj> getDupRegnList() {
        return dupRegnList;
    }

    public void setDupRegnList(List<OwnerDetailsDobj> dupRegnList) {
        this.dupRegnList = dupRegnList;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public int getActionCode() {
        return actionCode;
    }

    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
    }

    public int getPurCode() {
        return purCode;
    }

    public void setPurCode(int purCode) {
        this.purCode = purCode;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public boolean isShowCbxRetainVehicleNo() {
        return showCbxRetainVehicleNo;
    }

    public void setShowCbxRetainVehicleNo(boolean showCbxRetainVehicleNo) {
        this.showCbxRetainVehicleNo = showCbxRetainVehicleNo;
    }

    /**
     * @return the scrapReasonList
     */
    public List getScrapReasonList() {
        return scrapReasonList;
    }

    /**
     * @param scrapReasonList the scrapReasonList to set
     */
    public void setScrapReasonList(List scrapReasonList) {
        this.scrapReasonList = scrapReasonList;
    }

    /**
     * @return the disableRetention
     */
    public boolean isDisableRetention() {
        return disableRetention;
    }

    /**
     * @param disableRetention the disableRetention to set
     */
    public void setDisableRetention(boolean disableRetention) {
        this.disableRetention = disableRetention;
    }
}
