/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.HpaImpl;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "hpa_entrybean")
@ViewScoped
/**
 * *
 * this bean will deal with the appication of hypotheaction addintion for the
 * registered vehicle. i.e pur_cd = 6
 *
 */
public class HPAEntryBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(HPAEntryBean.class);
    private HpaDobj hpa_entry_dobj = null;
    private HpaDobj hpa_entry_dobj_prv;
    private List list_district;
    private List list_state;
    private List list_hp_type;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private List<ComparisonBean> prevChangedDataList;
    private List<HpaDobj> listHypoDetails = null;
    private String editOrAdd = "";
    private int index = 0;
    private Owner_dobj onwer_dobj = null;
    private Date maxDate = new Date();
    private Date minDate = DateUtil.parseDate(DateUtil.getCurrentDate());
    private String vahanMessages = null;
    private boolean renderFooter;
    private boolean renderDelete;

    public HPAEntryBean() {
        list_district = new ArrayList();
        list_state = new ArrayList();
        list_hp_type = new ArrayList();
    }

    @PostConstruct
    public void init() {
        HpaImpl hpa_Impl = new HpaImpl();
        try {

            if (getAppl_details() == null
                    || getAppl_details().getCurrent_state_cd() == null
                    || getAppl_details().getCurrent_off_cd() == 0) {
                vahanMessages = "Something went wrong, Please try again...";
                return;
            }

            String[][] data = MasterTableFiller.masterTables.VM_HP_TYPE.getData();
            for (int i = 0; i < data.length; i++) {
                list_hp_type.add(new SelectItem(data[i][0], data[i][1]));
            }

            if (getAppl_details() != null) {

                if (getAppl_details().getCurrent_action_cd() == TableConstants.HYPOTHECATION_ADDITION_ENTRY) {
                    renderFooter = true;
                    renderDelete = true;
                }

                list_state = MasterTableFiller.getStateList();
                list_district = MasterTableFiller.getDistrictList(getAppl_details().getCurrent_state_cd());

                /////this need to be chacked after removing it......start pending XXXXXXX
                hpa_entry_dobj = hpa_Impl.set_HPA_Entry_appl_db_to_dobj(getAppl_details().getAppl_no());
                /////this need to be chacked after removing it......End pending XXXXXXXX
                listHypoDetails = hpa_Impl.getHypoDetails(getAppl_details().getAppl_no(), TableConstants.VM_TRANSACTION_MAST_ADD_HYPO, null, false, appl_details.getCurrent_state_cd());
                if (hpa_entry_dobj == null) {
                    hpa_entry_dobj = new HpaDobj();
                    hpa_entry_dobj.setAppl_no(getAppl_details().getAppl_no());
                    hpa_entry_dobj.setRegn_no(getAppl_details().getRegn_no());
                } else {
                    hpa_entry_dobj.setAppl_no(getAppl_details().getAppl_no());
                    hpa_entry_dobj.setRegn_no(getAppl_details().getRegn_no());
                    hpa_entry_dobj_prv = (HpaDobj) hpa_entry_dobj.clone();
                }
                hpa_entry_dobj.setFncr_state(appl_details.getCurrent_state_cd());//for setting current_state
                // Nothing to display in current information area, because it is a Hypothecation entry case.

                if (appl_details.getOwnerDetailsDobj() == null) {
                    vahanMessages = "Owner Details not Found in the Database, Please Contact to the System Administrator";
                    return;
                }

                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());

                if (appl_details.getOwnerDobj().getPurchase_dt() != null) {
                    minDate = appl_details.getOwnerDobj().getPurchase_dt();//for having check of from date at the time of hpa
                }
            }

        } catch (VahanException vex) {
            vahanMessages = vex.getMessage();
        } catch (SQLException sqex) {
            LOGGER.error(sqex.toString() + " [ApplNo-" + appl_details.getAppl_no() + ",RegnNo-" + appl_details.getRegn_no() + ",PurCd-" + appl_details.getPur_cd() + "] " + sqex.getStackTrace()[0]);
            vahanMessages = "Something Went Wrong, Please Contact to the System Administrator";
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " [ApplNo-" + appl_details.getAppl_no() + ",RegnNo-" + appl_details.getRegn_no() + ",PurCd-" + appl_details.getPur_cd() + "] " + ex.getStackTrace()[0]);
            vahanMessages = "Something Went Wrong, Please Contact to the System Administrator";
        }
    }

    public void stateChangeListener(AjaxBehaviorEvent event) {
        String stateCd = String.valueOf(hpa_entry_dobj.getFncr_state());
        list_district.clear();
        list_district = MasterTableFiller.getDistrictList(stateCd);
    }//end of stateChangeListener

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
     * @return the hpa_entry_dobj
     */
    public HpaDobj getHpa_entry_dobj() {
        return hpa_entry_dobj;
    }

    /**
     * @param hpa_entry_dobj the hpa_entry_dobj to set
     */
    public void setHpa_entry_dobj(HpaDobj hpa_entry_dobj) {
        this.hpa_entry_dobj = hpa_entry_dobj;
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
     * @return the hpa_entry_dobj_prv
     */
    public HpaDobj getHpa_entry_dobj_prv() {
        return hpa_entry_dobj_prv;
    }

    /**
     * @param hpa_entry_dobj_prv the hpa_entry_dobj_prv to set
     */
    public void setHpa_entry_dobj_prv(HpaDobj hpa_entry_dobj_prv) {
        // making copy of the orignal Database values.
        this.hpa_entry_dobj_prv = new HpaDobj();
        this.hpa_entry_dobj_prv.setAppl_no(hpa_entry_dobj_prv.getAppl_no());
        this.hpa_entry_dobj_prv.setRegn_no(hpa_entry_dobj_prv.getRegn_no());
        this.hpa_entry_dobj_prv.setHp_type(hpa_entry_dobj_prv.getHp_type());
        this.hpa_entry_dobj_prv.setFncr_name(hpa_entry_dobj_prv.getFncr_name());
        this.hpa_entry_dobj_prv.setSr_no(hpa_entry_dobj_prv.getSr_no());
        this.hpa_entry_dobj_prv.setFrom_dt(hpa_entry_dobj_prv.getFrom_dt());
        this.hpa_entry_dobj_prv.setFncr_add1(hpa_entry_dobj_prv.getFncr_add1());
        this.hpa_entry_dobj_prv.setFncr_add2(hpa_entry_dobj_prv.getFncr_add2());
        this.hpa_entry_dobj_prv.setFncr_add3(hpa_entry_dobj_prv.getFncr_add3());
        this.hpa_entry_dobj_prv.setFncr_state(hpa_entry_dobj_prv.getFncr_state());
        this.hpa_entry_dobj_prv.setFncr_district(hpa_entry_dobj_prv.getFncr_district());
        this.hpa_entry_dobj_prv.setFncr_pincode(hpa_entry_dobj_prv.getFncr_pincode());
    }

    /**
     * @return the compBeanList
     */
    @Override
    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    /**
     * @param compBeanList the compBeanList to set
     */
    public void setCompBeanList(List<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    @Override
    public String save() {
        String return_location = "";
        try {
            if (listHypoDetails != null && !listHypoDetails.isEmpty()) {
                HpaImpl hpa_impl = new HpaImpl();
                listHypoDetails.get(0).setAppl_no(appl_details.getAppl_no());
                listHypoDetails.get(0).setRegn_no(appl_details.getRegn_no());
                hpa_impl.makeChange_hpa_enrty(listHypoDetails, appl_details.getCurrent_state_cd(), appl_details.getCurrent_off_cd(), ComparisonBeanImpl.changedDataContents(compareChanges()));
                return_location = "seatwork";
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "File can't be saved because of no HPA Details!", "File can't be saved because of no HPA Details!"));
            }
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

        try {
            if (hpa_entry_dobj_prv == null) {
                return getCompBeanList();
            }
            getCompBeanList().clear();

            Compare("Hypothecation_Type", hpa_entry_dobj_prv.getHp_type(), hpa_entry_dobj.getHp_type(), getCompBeanList());
            Compare("fncr_Name ", hpa_entry_dobj_prv.getFncr_name(), hpa_entry_dobj.getFncr_name(), getCompBeanList());
            Compare("fncr_Add1", hpa_entry_dobj_prv.getFncr_add1(), hpa_entry_dobj.getFncr_add1(), getCompBeanList());
            Compare("fncr_Add2", hpa_entry_dobj_prv.getFncr_add2(), hpa_entry_dobj.getFncr_add2(), getCompBeanList());
            Compare("fncr_Add3", hpa_entry_dobj_prv.getFncr_add3(), hpa_entry_dobj.getFncr_add3(), getCompBeanList());
            Compare("fncr_District", hpa_entry_dobj_prv.getFncr_district(), hpa_entry_dobj.getFncr_district(), getCompBeanList());
            Compare("fncr_State", hpa_entry_dobj_prv.getFncr_state(), hpa_entry_dobj.getFncr_state(), getCompBeanList());
            Compare("fncr_Pincode", hpa_entry_dobj_prv.getFncr_pincode(), hpa_entry_dobj.getFncr_pincode(), getCompBeanList());
            Compare("finace_from_dt", hpa_entry_dobj_prv.getFrom_dt(), hpa_entry_dobj.getFrom_dt(), getCompBeanList());
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Something went wrong during comparison of old and new updated data, Please contact to System Administrator.", "Something went wrong during comparison of old and new updated data, Please contact to System Administrator."));
        }
        return getCompBeanList();
    }

    @Override
    public String saveAndMoveFile() {
        String return_location = "";
        try {
            if (listHypoDetails != null && !listHypoDetails.isEmpty()) {
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
                    if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE) && this.appl_details.isDocVerifyForFacelessAppl()) {
                        String[] notVerifiedDocDetails = ServerUtil.verifyDocUploaded(appl_details.getAppl_no(), getAppl_details().getCurrent_action_cd());
                        if (notVerifiedDocDetails != null) {
                            appl_details.setNotVerifiedDocdetails(notVerifiedDocDetails[1]);
                            throw new VahanException(notVerifiedDocDetails[0]);
                        }
                    }
                    HpaImpl hpa_impl = new HpaImpl();
                    hpa_impl.update_HPA_Status(listHypoDetails, status, ComparisonBeanImpl.changedDataContents(compareChanges()), appl_details);
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
                        renderDelete = false;
                        return_location = "";
                    }
                }

            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "File can't be saved because of no HPA Details!", "File can't be saved because of no HPA Details!"));
                PrimeFaces.current().ajax().update("hpaErrorMsg");
            }

        } catch (VahanException vme) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vme.getMessage(), vme.getMessage()));
            PrimeFaces.current().ajax().update("hpaErrorMsg");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-File Could Not Save and Move Due to Technical Error in Database", "Error-File Could Not Save and Move Due to Technical Error in Database"));
            PrimeFaces.current().ajax().update("hpaErrorMsg");
        }
        return return_location;
    }

    public String printDisclaimer() {
        return PrintDocImpl.printOwnerDiscReport("registeredVehicles", "reportFormat");
    }

    public void fillHpaDetails(HpaDobj hpa_dobj, int editIndex) {
        setIndex(editIndex);
        Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        editOrAdd = map.get("key");

        if (hpa_dobj != null) {
            this.hpa_entry_dobj.setFncr_add1(hpa_dobj.getFncr_add1());
            this.hpa_entry_dobj.setFncr_add2(hpa_dobj.getFncr_add2());
            this.hpa_entry_dobj.setFncr_add3(hpa_dobj.getFncr_add3());
            this.hpa_entry_dobj.setFncr_name(hpa_dobj.getFncr_name());
            this.hpa_entry_dobj.setHp_type(hpa_dobj.getHp_type());
            this.hpa_entry_dobj.setFncr_district(hpa_dobj.getFncr_district());
            this.hpa_entry_dobj.setFncr_state(hpa_dobj.getFncr_state());
            this.hpa_entry_dobj.setFncr_pincode(hpa_dobj.getFncr_pincode());
            this.hpa_entry_dobj.setFrom_dt(hpa_dobj.getFrom_dt());

            String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
            list_district.clear();
            for (int i = 0; i < data.length; i++) {
                if (data[i][2].equals(hpa_entry_dobj.getFncr_state())) {
                    list_district.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
        }
    }

    /*
     *This method will the temporary data.
     */

    public void deleteHpaDetails(HpaDobj hpa_dobj) throws SQLException {
        try {
            if (hpa_dobj != null) {
                HpaImpl.insertToVhaHpaDeleteFromVaHpa(hpa_dobj, appl_details.getAppl_no(), hpa_dobj.getSr_no());
                reloadData();
                JSFUtils.showMessagesInDialog("Message", "Data delete sucessfully", FacesMessage.SEVERITY_INFO);
            } else {
                JSFUtils.showMessagesInDialog("Message", "Data not deleted", FacesMessage.SEVERITY_INFO);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    /*
     *This method will reload the page with the current data.
     */

    public void reloadData() {
        try {
            HpaImpl hpa_Impl = new HpaImpl();
            listHypoDetails.clear();
            listHypoDetails = hpa_Impl.getHypoDetails(appl_details.getAppl_no(), appl_details.getPur_cd(), appl_details.getRegn_no(), false, appl_details.getCurrent_state_cd());
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void addHypoRecordListener(AjaxBehaviorEvent event) {
        Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        editOrAdd = map.get("key");
        this.resetComponent();
    }

    public void resetComponent() {
        this.hpa_entry_dobj.setFncr_add1("");
        this.hpa_entry_dobj.setFncr_add2("");
        this.hpa_entry_dobj.setFncr_add3("");
        this.hpa_entry_dobj.setFncr_name("");
        this.hpa_entry_dobj.setFncr_pincode(0);
        this.hpa_entry_dobj.setFrom_dt(null);
    }

    public void saveHypothecationRecord() {

        String district = ServerUtil.getLableFromSelectedListToShow(list_district, String.valueOf(hpa_entry_dobj.getFncr_district()));
        String state = ServerUtil.getLableFromSelectedListToShow(list_state, String.valueOf(hpa_entry_dobj.getFncr_state()));
        String hp_type_descr = ServerUtil.getLableFromSelectedListToShow(list_hp_type, String.valueOf(hpa_entry_dobj.getHp_type()));

        if (editOrAdd.equalsIgnoreCase("add")) {
            HpaDobj hpa_dobj = new HpaDobj();
            hpa_dobj.setSr_no(0);
            hpa_dobj.setRegn_no(appl_details.getRegn_no());
            hpa_dobj.setAppl_no(appl_details.getAppl_no());
            hpa_dobj.setHp_type(hpa_entry_dobj.getHp_type());
            hpa_dobj.setHp_type_descr(hp_type_descr);
            hpa_dobj.setFncr_add1(hpa_entry_dobj.getFncr_add1());
            hpa_dobj.setFncr_add2(hpa_entry_dobj.getFncr_add2());
            hpa_dobj.setFncr_add3(hpa_entry_dobj.getFncr_add3());
            hpa_dobj.setFncr_name(hpa_entry_dobj.getFncr_name());
            hpa_dobj.setFncr_district(hpa_entry_dobj.getFncr_district());
            hpa_dobj.setFncr_district_descr(district);
            hpa_dobj.setFncr_state(hpa_entry_dobj.getFncr_state());
            hpa_dobj.setFncr_state_name(state);
            hpa_dobj.setFncr_pincode(hpa_entry_dobj.getFncr_pincode());
            hpa_dobj.setFrom_dt(hpa_entry_dobj.getFrom_dt());
            hpa_dobj.setFrom_dt_descr(ServerUtil.parseDateToString(hpa_entry_dobj.getFrom_dt()));
            listHypoDetails.add(hpa_dobj);//for adding new record in the table of Hypothecation Details List.
        } else if (editOrAdd.equalsIgnoreCase("edit")) {
            listHypoDetails.get(index).setRegn_no(appl_details.getRegn_no());
            listHypoDetails.get(index).setAppl_no(appl_details.getAppl_no());
            listHypoDetails.get(index).setHp_type(hpa_entry_dobj.getHp_type());
            listHypoDetails.get(index).setHp_type_descr(hp_type_descr);
            listHypoDetails.get(index).setFncr_add1(hpa_entry_dobj.getFncr_add1());
            listHypoDetails.get(index).setFncr_add2(hpa_entry_dobj.getFncr_add2());
            listHypoDetails.get(index).setFncr_add3(hpa_entry_dobj.getFncr_add3());
            listHypoDetails.get(index).setFncr_name(hpa_entry_dobj.getFncr_name());
            listHypoDetails.get(index).setFncr_district(hpa_entry_dobj.getFncr_district());
            listHypoDetails.get(index).setFncr_district_descr(district);
            listHypoDetails.get(index).setFncr_state(hpa_entry_dobj.getFncr_state());
            listHypoDetails.get(index).setFncr_state_name(state);
            listHypoDetails.get(index).setFncr_pincode(hpa_entry_dobj.getFncr_pincode());
            listHypoDetails.get(index).setFrom_dt_descr(ServerUtil.parseDateToString(hpa_entry_dobj.getFrom_dt()));
            listHypoDetails.get(index).setFrom_dt(hpa_entry_dobj.getFrom_dt());
        }

        PrimeFaces.current().executeScript("PF('hpaDlg').hide();");

    }

    /**
     * @return the prevChangedDataList
     */
    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        return prevChangedDataList;
    }

    /**
     * @param prevChangedDataList the prevChangedDataList to set
     */
    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        this.prevChangedDataList = prevChangedDataList;
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the listHypoDetails
     */
    public List<HpaDobj> getListHypoDetails() {
        return listHypoDetails;
    }

    /**
     * @param listHypoDetails the listHypoDetails to set
     */
    public void setListHypoDetails(List<HpaDobj> listHypoDetails) {
        this.listHypoDetails = listHypoDetails;
    }

    /**
     * @return the editOrAdd
     */
    public String getEditOrAdd() {
        return editOrAdd;
    }

    /**
     * @param editOrAdd the editOrAdd to set
     */
    public void setEditOrAdd(String editOrAdd) {
        this.editOrAdd = editOrAdd;
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
     * @return the onwer_dobj
     */
    public Owner_dobj getOnwer_dobj() {
        return onwer_dobj;
    }

    /**
     * @param onwer_dobj the onwer_dobj to set
     */
    public void setOnwer_dobj(Owner_dobj onwer_dobj) {
        this.onwer_dobj = onwer_dobj;
    }

    /**
     * @return the list_state
     */
    public List getList_state() {
        return list_state;
    }

    /**
     * @param list_state the list_state to set
     */
    public void setList_state(List list_state) {
        this.list_state = list_state;
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
     * @return the minDate
     */
    public Date getMinDate() {
        return minDate;
    }

    /**
     * @param minDate the minDate to set
     */
    public void setMinDate(Date minDate) {
        this.minDate = minDate;
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
     * @return the renderFooter
     */
    public boolean isRenderFooter() {
        return renderFooter;
    }

    /**
     * @param renderFooter the renderFooter to set
     */
    public void setRenderFooter(boolean renderFooter) {
        this.renderFooter = renderFooter;
    }

    public boolean isRenderDelete() {
        return renderDelete;
    }

    public void setRenderDelete(boolean renderDelete) {
        this.renderDelete = renderDelete;
    }
}
