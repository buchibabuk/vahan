/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.HpaImpl;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author AMBRISH
 */
@ManagedBean(name = "hpt_bean")
@ViewScoped
public class HptBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(HptBean.class);
    private HpaDobj hpt_dobj = new HpaDobj();
    private HpaDobj hpt_dobj_prv;
    private List list_district;
    private List list_hp_type;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private List<ComparisonBean> prevChangedDataList;
    private List<HpaDobj> listHypthDetails = null;
    private List<HpaDobj> listHptDetails = null;
    private String editOrTerminate = "";
    private int index = 0;
    private Date minDate = DateUtil.parseDate(DateUtil.getCurrentDate());
    private Date maxDate = new Date();
    private String vahanMessages = null;
    private OwnerDetailsDobj ownerDetail;

    @PostConstruct
    public void init() {
        list_district = new ArrayList();
        list_hp_type = new ArrayList();
        String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
        list_district.add(new SelectItem("-1", "---Select---"));
        for (int i = 0; i < data.length; i++) {
            list_district.add(new SelectItem(data[i][0], data[i][1]));
        }

        data = MasterTableFiller.masterTables.VM_HP_TYPE.getData();
        list_hp_type.add(new SelectItem("-1", "---Select---"));
        for (int i = 0; i < data.length; i++) {
            list_hp_type.add(new SelectItem(data[i][0], data[i][1]));
        }

        try {
            HpaImpl hpa_Impl = new HpaImpl();
            if (getAppl_details() != null) {

                listHypthDetails = hpa_Impl.getHypoDetails(getAppl_details().getAppl_no(), TableConstants.VM_TRANSACTION_MAST_REM_HYPO, null, false, Util.getUserStateCode());

                if (listHypthDetails == null || listHypthDetails.isEmpty()) {
                    vahanMessages = "Hypothecation Details not Found in the Database, Please Contact to the System Administrator";
                    return;
                }

                listHptDetails = hpa_Impl.getHptDetails(getAppl_details().getAppl_no());
                if (hpt_dobj != null) {
//                        // Note: hpt_bean: user will only enter from_dt so that in the first time entry case hpt_dobj will not come null, like in the cases.
//                        // so that exta check on the upto date is applied.
//                        // remaining form data is populated from vt_hypth table if it is first time entry case.
//                        // be carefull.
                    if (hpt_dobj.getUpto_dt() != null) {
                        hpt_dobj_prv = (HpaDobj) hpt_dobj.clone();
                    }
                }

                if (appl_details.getOwnerDetailsDobj() == null) {
                    vahanMessages = "Owner Details not Found in the Database, Please Contact to the System Administrator";
                    return;
                }
                ownerDetail = appl_details.getOwnerDetailsDobj();

                if (ownerDetail.getOwnerIdentity() != null) {
                    ownerDetail.getOwnerIdentity().setFlag(true);
                    ownerDetail.getOwnerIdentity().setMobileNoEditable(true);
                    ownerDetail.getOwnerIdentity().setOwnerCatgEditable(true);
                }

                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
            }
        } catch (VahanException vex) {
            vahanMessages = vex.getMessage();
        } catch (SQLException | CloneNotSupportedException ex) {
            LOGGER.error(ex.toString() + " [ApplNo-" + appl_details.getAppl_no() + ",RegnNo-" + appl_details.getRegn_no() + ",PurCd-" + appl_details.getPur_cd() + "] " + ex.getStackTrace()[0]);
            vahanMessages = "Something Went Wrong, Please Contact to the System Administrator";
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " [ApplNo-" + appl_details.getAppl_no() + ",RegnNo-" + appl_details.getRegn_no() + ",PurCd-" + appl_details.getPur_cd() + "] " + ex.getStackTrace()[0]);
            vahanMessages = "Something Went Wrong, Please Contact to the System Administrator";
        }
    }

    public List<ComparisonBean> addToComapreChangesList(List<ComparisonBean> compBeanListPrev) throws VahanException {

        List<ComparisonBean> list = compareChanges();

        if (compBeanListPrev == null) {
            compBeanListPrev = new ArrayList<>();
        }
        if (list.size() > 0) {
            compBeanListPrev.addAll(list);
        }

        return compBeanListPrev;
    }

    /**
     * @return the list_district
     */
    public List getList_district() {
        return list_district;
    }

    /**
     * @param list_district the list_district to set
     */
    public void setList_district(List list_district) {
        this.list_district = list_district;
    }

    /**
     * @return the list_hp_type
     */
    public List getList_hp_type() {
        return list_hp_type;
    }

    /**
     * @param list_hp_type the list_hp_type to set
     */
    public void setList_hp_type(List list_hp_type) {
        this.list_hp_type = list_hp_type;
    }

    /**
     * @return the compBeanList
     */
    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    /**
     * @param compBeanList the compBeanList to set
     */
    public void setCompBeanList(List<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    /**
     * @return the hpt_dobj
     */
    public HpaDobj getHpt_dobj() {
        return hpt_dobj;
    }

    /**
     * @param hpt_dobj the hpt_dobj to set
     */
    public void setHpt_dobj(HpaDobj hpt_dobj) {
        this.hpt_dobj = hpt_dobj;
    }

    /**
     * @return the hpt_dobj_prv
     */
    public HpaDobj getHpt_dobj_prv() {
        return hpt_dobj_prv;
    }

    /**
     * @param hpt_dobj_prv the hpt_dobj_prv to set
     */
    public void setHpt_dobj_prv(HpaDobj hpt_dobj_prv) {
        this.hpt_dobj_prv = new HpaDobj();
        this.hpt_dobj_prv.setAppl_no(hpt_dobj_prv.getAppl_no());
        this.hpt_dobj_prv.setRegn_no(hpt_dobj_prv.getRegn_no());
        this.hpt_dobj_prv.setFncr_name(hpt_dobj_prv.getFncr_name());
        this.hpt_dobj_prv.setFncr_add1(hpt_dobj_prv.getFncr_add1());
        this.hpt_dobj_prv.setFncr_add2(hpt_dobj_prv.getFncr_add2());
        this.hpt_dobj_prv.setFncr_add3(hpt_dobj_prv.getFncr_add3());
        this.hpt_dobj_prv.setFncr_district(hpt_dobj_prv.getFncr_district());
        this.hpt_dobj_prv.setFncr_state(hpt_dobj_prv.getFncr_state());
        this.hpt_dobj_prv.setFncr_pincode(hpt_dobj_prv.getFncr_pincode());
        this.hpt_dobj_prv.setFrom_dt(hpt_dobj_prv.getFrom_dt());
        this.hpt_dobj_prv.setHp_type(hpt_dobj_prv.getHp_type());
        this.hpt_dobj_prv.setSr_no(hpt_dobj_prv.getSr_no());
        this.hpt_dobj_prv.setUpto_dt(hpt_dobj_prv.getUpto_dt());
    }

    @Override
    public String save() {
        String return_location = "";
        try {
            if (listHptDetails.isEmpty()) {
                return "seatwork";
            }
            HpaImpl hpa_impl = new HpaImpl();
            hpa_impl.makeChange_hpt(listHptDetails, ComparisonBeanImpl.changedDataContents(compareChanges()));
            return_location = "seatwork";
        } catch (VahanException vme) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vme.getMessage(), vme.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-File Could Not Save and Move Due to Technical Error in Database", "Error-File Could Not Save and Move Due to Technical Error in Database"));
        }
        return return_location;
    }

    @Override
    public List<ComparisonBean> compareChanges() {

        if (getHpt_dobj_prv() == null) {
            return getCompBeanList();
        }
        getCompBeanList().clear();

        Compare("finance_upto_dt", getHpt_dobj_prv().getUpto_dt(), getHpt_dobj().getUpto_dt(), getCompBeanList());

        return getCompBeanList();
    }

    @Override
    public String saveAndMoveFile() {
        String return_location = "";
        try {
            if (listHptDetails.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "File can't be moved because of no Termination!", "File can't be moved because of no Termination!"));
                return "";
            }
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setPur_cd(appl_details.getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            status.setCurrent_role(appl_details.getCurrent_role());

            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                    || getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_REVERT)
                    || getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                status.setVehicleParameters(appl_details.getVehicleParameters());
                HpaImpl hpa_impl = new HpaImpl();
                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE) && this.appl_details.isDocVerifyForFacelessAppl()) {
                    String[] notVerifiedDocDetails = ServerUtil.verifyDocUploaded(appl_details.getAppl_no(), getAppl_details().getCurrent_action_cd());
                    if (notVerifiedDocDetails != null) {
                        appl_details.setNotVerifiedDocdetails(notVerifiedDocDetails[1]);
                        throw new VahanException(notVerifiedDocDetails[0]);
                    }
                }
                hpa_impl.update_HPT_Status(listHptDetails, status, ComparisonBeanImpl.changedDataContents(compareChanges()));
                if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                    ServerUtil.sendSmsInFacelessService(appl_details.getAppl_no(), Util.getUserStateCode(), Util.getUserSeatOffCode(), appl_details.getRegn_no(), appl_details.getNotVerifiedDocdetails());
                    return_location = disapprovalPrint();
                } else {
                    return_location = "seatwork";
                }
            }
            if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_COMPLETE) && appl_details.getCurrent_role() == 4) {
                if (!ServerUtil.getDataEntryIncomplete(appl_details.getAppl_no())) {
                    PrimeFaces.current().ajax().update("app_disapp_new_form:showOwnerDiscPopup");
                    PrimeFaces.current().executeScript("PF('successDialog').show()");
                    return_location = "";
                }
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-File Could Not Save and Move Due to Technical Error in Database", "Error-File Could Not Save and Move Due to Technical Error in Database"));
        }
        return return_location;
    }

    public String printDisclaimer() {
        return PrintDocImpl.printOwnerDiscReport("registeredVehicles", "reportFormat");
    }

    public void getHypoTerminatedRow(HpaDobj dobj) {
        if (dobj != null) {

            this.setMinDate(dobj.getFrom_dt());

            if (!listHptDetails.isEmpty()) {

                for (int i = 0; i < listHptDetails.size(); i++) {
                    if (dobj.getSr_no() == listHptDetails.get(i).getSr_no()) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "You Have Already Terminated This Record."));
                        return;
                    }
                }
            }

            this.hpt_dobj = dobj;
            PrimeFaces.current().executeScript("PF('hptDlg').show();");
        }
    }

    public void fillHypTerminationList() {

        this.hpt_dobj.setAppl_no(getAppl_details().getAppl_no());
        this.hpt_dobj.getUpto_dt();
        this.hpt_dobj.setTerm_dt(new Date());
        this.hpt_dobj.setUpto_dt_descr(ServerUtil.parseDateToString(this.hpt_dobj.getUpto_dt()));
        if (editOrTerminate.equalsIgnoreCase("edit")) {
            listHptDetails.set(index, this.hpt_dobj); //when edit in termination
        } else {
            if (listHptDetails != null && !listHptDetails.isEmpty() && listHptDetails.size() >= 1) {
                for (int i = 0; i < listHptDetails.size(); i++) {
                    if (this.hpt_dobj.getSr_no() == listHptDetails.get(i).getSr_no()) {
                        return;//for skipping multiple same entry
                    }
                }
            }
            listHptDetails.add(this.hpt_dobj); // when termination
        }
    }

    public void editHPT(HpaDobj hpt, int currentIndex, String edit) {

        if (hpt != null) {
            this.setMinDate(hpt.getFrom_dt());
            this.hpt_dobj = hpt;
            editOrTerminate = edit;
            index = currentIndex;
        }
    }

    /**
     * @return the prevChangedDataList
     */
    public List<ComparisonBean> getPrevChangedDataList() {
        return prevChangedDataList;
    }

    /**
     * @param prevChangedDataList the prevChangedDataList to set
     */
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        this.prevChangedDataList = prevChangedDataList;
    }

    /**
     * @return the listHptDetails
     */
    public List<HpaDobj> getListHptDetails() {
        return listHptDetails;
    }

    /**
     * @param listHptDetails the listHptDetails to set
     */
    public void setListHptDetails(List<HpaDobj> listHptDetails) {
        this.listHptDetails = listHptDetails;
    }

    /**
     * @return the listHypthDetails
     */
    public List<HpaDobj> getListHypthDetails() {
        return listHypthDetails;
    }

    /**
     * @param listHypthDetails the listHypthDetails to set
     */
    public void setListHypthDetails(List<HpaDobj> listHypthDetails) {
        this.listHypthDetails = listHypthDetails;
    }

    /**
     * @return the editOrTerminate
     */
    public String getEditOrTerminate() {
        return editOrTerminate;
    }

    /**
     * @param editOrTerminate the editOrTerminate to set
     */
    public void setEditOrTerminate(String editOrTerminate) {
        this.editOrTerminate = editOrTerminate;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return the maxDate
     */
    public Date getMinDate() {
        return minDate;
    }

    /**
     * @param maxDate the maxDate to set
     */
    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    /**
     * @return the StateCode
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

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    public OwnerDetailsDobj getOwnerDetail() {
        return ownerDetail;
    }

    public void setOwnerDetail(OwnerDetailsDobj ownerDetail) {
        this.ownerDetail = ownerDetail;
    }
}
