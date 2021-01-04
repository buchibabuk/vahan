/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.BlackListedVehicleDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.SwappingRegnDobj;
import nic.vahan.form.dobj.TmConfigurationSwappingDobj;
import nic.vahan.form.impl.BlackListedVehicleImpl;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.SwappingRegnImpl;
import nic.vahan.form.impl.ToImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import static nic.vahan.server.ServerUtil.Compare;
import org.primefaces.PrimeFaces;

/**
 *
 * @author nic
 */
@ViewScoped
@ManagedBean(name = "swapBean")
public class SwappingRegnBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(SwappingRegnBean.class);
    private List<ComparisonBean> prevChangedDataList;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private SwappingRegnDobj swap_dobj = null;
    private SwappingRegnDobj swap_dobj_prv = null;
    private String orderby;
    private String orderno;
    private String regn_no_one;
    private String regn_no_two;
    private String appl_no_one;
    private String appl_no_two;
    private String reason;
    private Date today = new Date();
    private Date orderdt;
    private List list_c_state;
    private List list_c_district;
    private List list_c_state_two;
    private List list_c_district_two;
    private boolean render_appdisp = false;
    private boolean btn_showDtl = true;
    private boolean readOnly = false;
    private String chasi_no_one;
    private String chasi_no_two;
    private TmConfigurationSwappingDobj tmConfSwappingDobj = null;
    private String relation_code;
    private List relation_list = new ArrayList();
    private boolean renderUntracedCheck = false;

    public SwappingRegnBean() {
        try {
            setTmConfSwappingDobj(Util.getTmConfigurationSwapping());
            list_c_state = new ArrayList();
            list_c_district = new ArrayList();
            list_c_state_two = new ArrayList();
            list_c_district_two = new ArrayList();
            if (this.tmConfSwappingDobj.isSwapping_allowed_theft_untraced_case()) {
                setRenderUntracedCheck(true);
            } else {
                setRenderUntracedCheck(false);
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage("wb_errorMessages", new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), null));
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage("wb_errorMessages", new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, null));
            return;
        }
    }

    public void showOwnerDetails() throws VahanException {
        Boolean isExit = false;
        Exception e = null;
        List<Integer> purCd = new ArrayList<>();
        ToImpl toImpl = new ToImpl();
        Boolean isFancy_regn_no_one = false;
        Boolean isFancy_regn_no_two = false;
        if ("DD".contains(getTmConfSwappingDobj().getState_cd())) {
            if (Util.getUserLoginOffCode() == 1) {
                getTmConfSwappingDobj().setMultiple_swapping_allowed(false);
                getTmConfSwappingDobj().setOld_vehicle_age(3);
                getTmConfSwappingDobj().setNew_vehicle_age(1);
            }
        }
        if (this.regn_no_one.trim().equals("")) {
            FacesMessage message = new FacesMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Enter Registration no for First Vehicle", "Please Enter Registration no for First Vehicle");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        if (this.chasi_no_one.trim().equals("")) {
            FacesMessage message = new FacesMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Enter Last Five Digit for First Vehicle", "Please Enter Registration no for First Vehicle");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        if (this.chasi_no_two.trim().equals("")) {
            FacesMessage message = new FacesMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Enter Last Five Digit for Second Vehicle", "Please Enter Last Five Digit for Second Vehicle");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        if (this.regn_no_two.trim().equals("")) {
            FacesMessage message = new FacesMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Enter Registration no for Second Vehicle", "Please Enter Registration no for Second Vehicle");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        if (this.regn_no_one.trim().toUpperCase().equals(this.regn_no_two.trim().toUpperCase())) {
            FacesMessage message = new FacesMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Both Vehicle Registration no should not be same", "Both Vehicle Registration no should not be same");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        if (this.relation_code.trim().equals("-1")) {
            FacesMessage message = new FacesMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Select Blood Relation Type", "Please Select Blood Relation Type");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        try {
            //if (!"RJ,PB".contains(Util.getUserStateCode())) {
            if (!tmConfSwappingDobj.isMultiple_swapping_allowed()) {
                isExit = SwappingRegnImpl.isAlreadyRetened(this.regn_no_one.trim().toUpperCase());
                if (isExit) {
                    FacesMessage message = new FacesMessage();
                    message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Registration no " + this.regn_no_one.trim().toUpperCase() + " Already Swapped", "Registration no " + this.regn_no_one.trim().toUpperCase() + " Already Swapped");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return;
                }
                isExit = SwappingRegnImpl.isAlreadyRetened(this.regn_no_two.trim().toUpperCase());
                if (isExit) {
                    FacesMessage message = new FacesMessage();
                    message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Registration no " + this.regn_no_two.trim().toUpperCase() + " Already Swapped", "Registration no " + this.regn_no_two.trim().toUpperCase() + " Already Swapped");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return;
                }
            }
            if (tmConfSwappingDobj.isOne_regn_must_be_fancy_no() && getRegn_no_one().length() > 4 && getRegn_no_two().length() > 4) {
                isFancy_regn_no_one = SwappingRegnImpl.isFancyNo(getRegn_no_one());
                isFancy_regn_no_two = SwappingRegnImpl.isFancyNo(getRegn_no_two());
                if (!isFancy_regn_no_one && !isFancy_regn_no_two) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "One Registration no should be Fancy", "One Registration no should be Fancy");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return;
                }
            }
            List<Status_dobj> statusList = ServerUtil.applicationStatus(this.regn_no_one.trim().toUpperCase());
            if (!statusList.isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!",
                        "You have Already Generated Application No (" + statusList.get(0).getAppl_no() + ") Against Registration No (" + statusList.get(0).getRegn_no() + ")"));
                return;
            }
            statusList = ServerUtil.applicationStatus(this.regn_no_two.trim().toUpperCase());
            if (!statusList.isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!",
                        "You have Already Generated Application No (" + statusList.get(0).getAppl_no() + ") Against Registration No (" + statusList.get(0).getRegn_no() + ")"));
                return;
            }

            swap_dobj = SwappingRegnImpl.getApplOwnerDetailDBToDobj(this.regn_no_one.toUpperCase(), this.regn_no_two.toUpperCase(), getAppl_details().getCurrent_action_cd(), this.chasi_no_one.trim(), this.chasi_no_two.trim(), tmConfSwappingDobj);
            if (swap_dobj != null) {
                //Check for blacklisted
                BlackListedVehicleImpl obj = new BlackListedVehicleImpl();
                BlackListedVehicleDobj blackListedDobj = obj.getBlacklistedVehicleDetails(swap_dobj.getRegnNoOne(), swap_dobj.getChassi_one());
                //Check for untraced report of blacklisted vehicle                
                List<BlackListedVehicleDobj> untracedblackListedDobjOLDVehicle = obj.getUntracedRCDetail(swap_dobj.getRegnNoOne(), null);
                if (blackListedDobj != null && untracedblackListedDobjOLDVehicle.size() > 0) {
                    blackListedDobj.setUntracedReportOfBlacklistedForSwappingUse(untracedblackListedDobjOLDVehicle.get(0).isUntracedReportOfBlacklistedForSwappingUse());
                } else {
                    // Check OLD vehicle age if untraced report is not recorded of blacklisted vehicle
                    if ("DL".contains(Util.getUserStateCode()) && tmConfSwappingDobj.getOld_vehicle_age() > 0 && swap_dobj.getRegn_dt() != null && !swap_dobj.getRegn_dt().equals("")) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        java.util.Date CurrentDate = null;
                        java.sql.Date CurrentDateinsql = null;
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.YEAR, -tmConfSwappingDobj.getOld_vehicle_age());
                        CurrentDate = sdf.parse("" + cal.get(Calendar.YEAR) + "-"
                                + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH));
                        CurrentDateinsql = new java.sql.Date(CurrentDate.getTime());
                        if (!swap_dobj.getRegn_dt().before(CurrentDateinsql)) {
                            swap_dobj = null;
                            throw new VahanException("Could not swap the registration no as Registration date for first vehicle must be greater than three years from Current Date!!!");
                        }
                    }
                }
                swap_dobj.setBlackListedVehicleDobj(blackListedDobj);
                ApplicationInwardBean applInwdBean = new ApplicationInwardBean();
                purCd.add(TableConstants.SWAPPING_REGN_PUR_CD);
                swap_dobj.setVh_class(swap_dobj.getVh_class_one());
                swap_dobj.setState_cd(swap_dobj.getC_statecodeOne());
                swap_dobj.setRegn_type(swap_dobj.getRegnTypeforold());
                swap_dobj.setRegn_no(this.regn_no_one.toUpperCase());
                if (applInwdBean.performAllCheck(this.regn_no_one.toUpperCase(), purCd, swap_dobj, false)) {
                    swap_dobj = null;
                    return;
                }
                blackListedDobj = obj.getBlacklistedVehicleDetails(swap_dobj.getRegnNoTwo(), swap_dobj.getChassi_two());
                if (blackListedDobj != null) {
                    //Check for untraced report of blacklisted vehicle                
                    List<BlackListedVehicleDobj> untracedblackListedDobjNEWVehicle = obj.getUntracedRCDetail(swap_dobj.getRegnNoTwo(), null);
                    if (untracedblackListedDobjNEWVehicle.size() > 0) {
                        blackListedDobj.setUntracedReportOfBlacklistedForSwappingUse(untracedblackListedDobjNEWVehicle.get(0).isUntracedReportOfBlacklistedForSwappingUse());
                    }
                }
                swap_dobj.setBlackListedVehicleDobj(blackListedDobj);
                if (!tmConfSwappingDobj.isSame_vehicle_class()) {
                    swap_dobj.setVh_class(swap_dobj.getVh_class_two());
                }
                swap_dobj.setState_cd(swap_dobj.getC_statecodeTwo());
                swap_dobj.setRegn_type(swap_dobj.getRegnTypefornew());
                swap_dobj.setRegn_no(this.regn_no_two.toUpperCase());
                if (applInwdBean.performAllCheck(this.regn_no_two.toUpperCase(), purCd, swap_dobj, true)) {
                    swap_dobj = null;
                    return;
                }
                setReadOnly(true);
                String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
                list_c_district.clear();
                list_c_district_two.clear();
                for (int i = 0; i < data.length; i++) {
                    if (data[i][2].equals(swap_dobj.getC_statecodeOne())) {
                        list_c_district.add(new SelectItem(data[i][0], data[i][1]));
                    }
                    if (data[i][2].equals(swap_dobj.getC_statecodeTwo())) {
                        list_c_district_two.add(new SelectItem(data[i][0], data[i][1]));
                    }
                }
                data = MasterTableFiller.masterTables.TM_STATE.getData();
                list_c_state.clear();
                for (int i = 0; i < data.length; i++) {
                    if (data[i][0].equals(swap_dobj.getC_statecodeOne())) {
                        list_c_state.add(new SelectItem(data[i][0], data[i][1]));
                    }
                    if (data[i][0].equals(swap_dobj.getC_statecodeTwo())) {
                        list_c_state_two.add(new SelectItem(data[i][0], data[i][1]));
                    }
                }
                if (this.relation_code != null && !this.relation_code.isEmpty()) {
                    swap_dobj.setRelation_code(Integer.parseInt(this.relation_code));
                }
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!!! Please contact Administrator.", "Error!!! Please contact Administrator."));
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void onDateSelect(SwappingRegnDobj swap_dobj) throws ParseException {
        DateFormat wfromat = new SimpleDateFormat("yyyy-MM-dd");
        if (this.getOrderdt() != null) {
            swap_dobj.setOrder_dt(wfromat.format(this.getOrderdt()));
        }
        SimpleDateFormat datetimeFormatter1 = new SimpleDateFormat("yyyy-MM-dd");
        Date lFromDate1 = datetimeFormatter1.parse(swap_dobj.getOrder_dt());
        if (!swap_dobj.getRegn_dt().before(lFromDate1) || swap_dobj.getRegn_dt().equals(lFromDate1)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Order date Should Not Less Than Rgistration Date!", "Order date Should Not Less Than Rgistration Date!"));
            this.orderdt = null;
            return;
        }
    }

    @PostConstruct
    public void init() {
        try {
            if (getAppl_details().getCurrent_action_cd() == TableConstants.SWAPPING_REGN_VERIFICATION || getAppl_details().getCurrent_action_cd() == TableConstants.SWAPPING_REGN_APPROVAL) {
                swap_dobj = SwappingRegnImpl.setPreSwapRetDobj(getAppl_details().getAppl_no().toUpperCase());
                if (swap_dobj != null) {
                    swap_dobj_prv = (SwappingRegnDobj) swap_dobj.clone();
                }
                if (getAppl_details() != null) {
                    String[] currentData = SwappingRegnImpl.present_technicalDetail(getAppl_details().getRegn_no());
                    if (currentData != null) {
                        currentdata.put("Vehicle Current Technical Detail", currentData[0]);
                    }
                    prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                }
                setReadOnly(true);
                setBtn_showDtl(false);
                setRender_appdisp(true);
                swap_dobj = SwappingRegnImpl.setSwappingFromApplDBToDobj(getAppl_details().getAppl_no().toUpperCase(), tmConfSwappingDobj);
                swap_dobj.setApplno(getAppl_details().getAppl_no());
                swap_dobj.setAppldt(getAppl_details().getAppl_dt());
                this.setOrderby(swap_dobj.getOrder_by());
                this.setOrderdt(new Date(swap_dobj.getOrder_dt()));
                this.reason = swap_dobj.getReason();
                this.setOrderno(swap_dobj.getOrder_no());
                this.setRegn_no_one(swap_dobj.getRegnNoOne());
                this.setRegn_no_two(swap_dobj.getRegnNoTwo());
                this.setChasi_no_one(swap_dobj.getChasiOneLastFiveChar());
                this.setChasi_no_two(swap_dobj.getChasiTwoLastFiveChar());
                this.setRelation_code(String.valueOf(swap_dobj.getRelation_code()));
            }
            if (swap_dobj != null) {
                String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
                list_c_district.clear();
                for (int i = 0; i < data.length; i++) {
                    if (data[i][2].equals(swap_dobj.getC_statecodeOne())) {
                        list_c_district.add(new SelectItem(data[i][0], data[i][1]));
                    }
                }
                data = MasterTableFiller.masterTables.TM_STATE.getData();
                list_c_state.clear();
                for (int i = 0; i < data.length; i++) {
                    if (data[i][0].equals(swap_dobj.getC_statecodeOne())) {
                        list_c_state.add(new SelectItem(data[i][0], data[i][1]));
                    }
                    if (data[i][0].equals(swap_dobj.getC_statecodeTwo())) {
                        list_c_state_two.add(new SelectItem(data[i][0], data[i][1]));
                    }
                }
            }
            String[][] relationdata = MasterTableFiller.masterTables.VM_RELATION.getData();
            if (relation_list != null || !relation_list.isEmpty()) {
                relation_list.clear();
            }
            relation_list.add(new SelectItem("-1", "Select"));
            for (int i = 0; i < relationdata.length; i++) {
                if (getTmConfSwappingDobj().isSwapping_allowed_theft_untraced_case() && (relationdata[i][0].equals("98") || relationdata[i][0].equals("99"))) {
                    relation_list.add(new SelectItem(relationdata[i][0], relationdata[i][1]));
                } else if (getTmConfSwappingDobj().isSame_owner_name() && (relationdata[i][0].equals("99"))) {
                    relation_list.add(new SelectItem(relationdata[i][0], relationdata[i][1]));
                } else if (!getTmConfSwappingDobj().isSame_owner_name()) {
                    relation_list.add(new SelectItem(relationdata[i][0], relationdata[i][1]));
                }
            }

        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage("wb_errorMessages", new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), null));
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage("wb_errorMessages", new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, null));
            return;
        }
    }

    public void saveRetData() throws VahanException {
        Exception e = null;
        List<SwappingRegnDobj> stringList = new ArrayList<SwappingRegnDobj>();
        try {
            updateSwappingRegnDobj(swap_dobj);
            Status_dobj status_dobj = new Status_dobj();
            status_dobj.setPur_cd(TableConstants.SWAPPING_REGN_PUR_CD);
            status_dobj.setFlow_slno(1);//initial flow serial no.
            status_dobj.setFile_movement_slno(1);//initial file movement serial no.
            status_dobj.setState_cd(Util.getUserStateCode());
            status_dobj.setOff_cd(getAppl_details().getCurrent_off_cd());
            status_dobj.setEmp_cd(0);
            status_dobj.setSeat_cd("");
            status_dobj.setCntr_id("");
            status_dobj.setStatus("N");
            status_dobj.setOffice_remark("");
            status_dobj.setPublic_remark("");
            status_dobj.setFile_movement_type("F");
            status_dobj.setUser_id(Util.getUserId());
            status_dobj.setAppl_date(ServerUtil.getSystemDateInPostgres());
            status_dobj.setUser_type("");
            status_dobj.setEntry_ip("");
            status_dobj.setEntry_status("");//for New File
            status_dobj.setConfirm_ip("");
            status_dobj.setConfirm_status("");
            status_dobj.setConfirm_date(new java.util.Date());
            stringList = SwappingRegnImpl.saveDataforInward(swap_dobj, status_dobj);
            setAppl_no_one(stringList.get(0).getApplnoOne().toUpperCase());
            setAppl_no_two(stringList.get(0).getApplnoTwo().toUpperCase());
            PrimeFaces.current().ajax().update("op_showGenApplNo");
            PrimeFaces.current().executeScript("PF('successDialog').show()");

        } catch (SQLException sqle) {
            e = sqle;
        } catch (VahanException ve) {
            e = ve;
        } catch (Exception ex) {
            e = ex;
        }

        if (e != null) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Exception Occured - Could not Generate Application No Due to " + e.getMessage(), "Exception Occured - Could not Generate Application No Due to " + e.getMessage()));
            return;
        }
    }

    @Override
    public String save() {
        String return_location = "";
        Exception exception = null;
        try {
            updateSwappingRegnDobj(swap_dobj);
            List<ComparisonBean> compareChanges = compareChanges();
            if (!compareChanges.isEmpty() || swap_dobj_prv == null) { //save only when data is really changed by user
                SwappingRegnImpl.makeChangeRET(swap_dobj, ComparisonBeanImpl.changedDataContents(compareChanges));
            }
            return_location = "seatwork";
        } catch (VahanException e) {
            exception = e;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception e) {
            exception = e;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        if (exception != null) {
            LOGGER.error(exception);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-Could Not Save Due to Technical Error in Database", "Error-Could Not Save Due to Technical Error in Database"));
            return "";
        }
        return return_location;
    }

    @Override
    public String saveAndMoveFile() {
        String return_location = "";
        Exception exception = null;
        try {
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(swap_dobj.getApplnoOne());
            status.setPur_cd(appl_details.getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            status.setCurrent_role(appl_details.getCurrent_role());
            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE) || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)) {
                status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                SwappingRegnImpl impl = new SwappingRegnImpl();
                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE) && this.appl_details.isDocVerifyForFacelessAppl()) {
                    String[] notVerifiedDocDetails = ServerUtil.verifyDocUploaded(appl_details.getAppl_no(), getAppl_details().getCurrent_action_cd());
                    if (notVerifiedDocDetails != null) {
                        appl_details.setNotVerifiedDocdetails(notVerifiedDocDetails[1]);
                        throw new VahanException(notVerifiedDocDetails[0]);
                    }
                }
                updateSwappingRegnDobj(swap_dobj);
                impl.update_Swapping_Status(swap_dobj, swap_dobj_prv, status, ComparisonBeanImpl.changedDataContents(compareChanges()));
                if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                    ServerUtil.sendSmsInFacelessService(appl_details.getAppl_no(), Util.getUserStateCode(), Util.getUserSeatOffCode(), appl_details.getRegn_no(),appl_details.getNotVerifiedDocdetails());
                }
                return_location = "seatwork";
            }
        } catch (Exception e) {
            exception = e;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        if (exception != null) {
            LOGGER.info(exception.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-File Could Not Save and Move Due to Technical Error in Database", "Error-File Could Not Save and Move Due to Technical Error in Database"));
            return "";
        }
        return return_location;
    }

    public void updateSwappingRegnDobj(SwappingRegnDobj swap_dobj) throws ParseException {
        DateFormat wfromat = new SimpleDateFormat("dd-MMM-yyyy");
        DateFormat rformat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        if (this.getOrderby().trim() != null) {
            swap_dobj.setOrder_by(this.getOrderby().trim());
        }
        if (this.getOrderno().trim() != null) {
            swap_dobj.setOrder_no(this.getOrderno().trim());
        }
        if (this.getOrderdt() != null) {
            swap_dobj.setOrder_dt(wfromat.format(this.getOrderdt()));
        }
        if (this.reason.trim() != null) {
            swap_dobj.setReason(this.reason.trim());
        }
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ComparisonBean> compareChanges() {

        if (swap_dobj_prv == null) {
            return compBeanList;
        }

        compBeanList.clear();
        Compare("orderby", swap_dobj_prv.getOrder_by(), swap_dobj.getOrder_by(), compBeanList);
        Compare("orderno", swap_dobj_prv.getOrder_no(), swap_dobj.getOrder_no(), compBeanList);
        Compare("orderdt", swap_dobj_prv.getOrder_dt(), swap_dobj.getOrder_dt(), compBeanList);
        Compare("reason", swap_dobj_prv.getReason(), swap_dobj.getReason(), compBeanList);
        return getCompBeanList();
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        return prevChangedDataList;
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        this.prevChangedDataList = prevChangedDataList;
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
     * @return the swap_dobj
     */
    public SwappingRegnDobj getSwap_dobj() {
        return swap_dobj;
    }

    /**
     * @param swap_dobj the swap_dobj to set
     */
    public void setSwap_dobj(SwappingRegnDobj swap_dobj) {
        this.swap_dobj = swap_dobj;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return the today
     */
    public Date getToday() {
        return today;
    }

    /**
     * @param today the today to set
     */
    public void setToday(Date today) {
        this.today = today;
    }

    /**
     * @return the swap_dobj_prv
     */
    public SwappingRegnDobj getSwap_dobj_prv() {
        return swap_dobj_prv;
    }

    /**
     * @param swap_dobj_prv the swap_dobj_prv to set
     */
    public void setSwap_dobj_prv(SwappingRegnDobj swap_dobj_prv) {
        this.swap_dobj_prv = swap_dobj_prv;
    }

    /**
     * @return the list_c_state
     */
    public List getList_c_state() {
        return list_c_state;
    }

    /**
     * @param list_c_state the list_c_state to set
     */
    public void setList_c_state(List list_c_state) {
        this.list_c_state = list_c_state;
    }

    /**
     * @return the list_c_district
     */
    public List getList_c_district() {
        return list_c_district;
    }

    /**
     * @param list_c_district the list_c_district to set
     */
    public void setList_c_district(List list_c_district) {
        this.list_c_district = list_c_district;
    }

    /**
     * @return the regn_no_one
     */
    public String getRegn_no_one() {
        return regn_no_one;
    }

    /**
     * @param regn_no_one the regn_no_one to set
     */
    public void setRegn_no_one(String regn_no_one) {
        this.regn_no_one = regn_no_one;
    }

    /**
     * @return the regn_no_two
     */
    public String getRegn_no_two() {
        return regn_no_two;
    }

    /**
     * @param regn_no_two the regn_no_two to set
     */
    public void setRegn_no_two(String regn_no_two) {
        this.regn_no_two = regn_no_two;
    }

    /**
     * @return the orderby
     */
    public String getOrderby() {
        return orderby;
    }

    /**
     * @param orderby the orderby to set
     */
    public void setOrderby(String orderby) {
        this.orderby = orderby;
    }

    /**
     * @return the orderno
     */
    public String getOrderno() {
        return orderno;
    }

    /**
     * @param orderno the orderno to set
     */
    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    /**
     * @return the orderdt
     */
    public Date getOrderdt() {
        return orderdt;
    }

    /**
     * @param orderdt the orderdt to set
     */
    public void setOrderdt(Date orderdt) {
        this.orderdt = orderdt;
    }

    /**
     * @return the list_c_state_two
     */
    public List getList_c_state_two() {
        return list_c_state_two;
    }

    /**
     * @param list_c_state_two the list_c_state_two to set
     */
    public void setList_c_state_two(List list_c_state_two) {
        this.list_c_state_two = list_c_state_two;
    }

    /**
     * @return the list_c_district_two
     */
    public List getList_c_district_two() {
        return list_c_district_two;
    }

    /**
     * @param list_c_district_two the list_c_district_two to set
     */
    public void setList_c_district_two(List list_c_district_two) {
        this.list_c_district_two = list_c_district_two;
    }

    /**
     * @return the render_appdisp
     */
    public boolean isRender_appdisp() {
        return render_appdisp;
    }

    /**
     * @param render_appdisp the render_appdisp to set
     */
    public void setRender_appdisp(boolean render_appdisp) {
        this.render_appdisp = render_appdisp;
    }

    /**
     * @return the btn_showDtl
     */
    public boolean isBtn_showDtl() {
        return btn_showDtl;
    }

    /**
     * @param btn_showDtl the btn_showDtl to set
     */
    public void setBtn_showDtl(boolean btn_showDtl) {
        this.btn_showDtl = btn_showDtl;
    }

    /**
     * @return the readOnly
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * @param readOnly the readOnly to set
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * @return the appl_no_one
     */
    public String getAppl_no_one() {
        return appl_no_one;
    }

    /**
     * @param appl_no_one the appl_no_one to set
     */
    public void setAppl_no_one(String appl_no_one) {
        this.appl_no_one = appl_no_one;
    }

    /**
     * @return the appl_no_two
     */
    public String getAppl_no_two() {
        return appl_no_two;
    }

    /**
     * @param appl_no_two the appl_no_two to set
     */
    public void setAppl_no_two(String appl_no_two) {
        this.appl_no_two = appl_no_two;
    }

    /**
     * @return the chasi_no_one
     */
    public String getChasi_no_one() {
        return chasi_no_one;
    }

    /**
     * @param chasi_no_one the chasi_no_one to set
     */
    public void setChasi_no_one(String chasi_no_one) {
        this.chasi_no_one = chasi_no_one;
    }

    /**
     * @return the chasi_no_two
     */
    public String getChasi_no_two() {
        return chasi_no_two;
    }

    /**
     * @param chasi_no_two the chasi_no_two to set
     */
    public void setChasi_no_two(String chasi_no_two) {
        this.chasi_no_two = chasi_no_two;
    }

    /**
     * @return the tmConfSwappingDobj
     */
    public TmConfigurationSwappingDobj getTmConfSwappingDobj() {
        return tmConfSwappingDobj;
    }

    /**
     * @param tmConfSwappingDobj the tmConfSwappingDobj to set
     */
    public void setTmConfSwappingDobj(TmConfigurationSwappingDobj tmConfSwappingDobj) {
        this.tmConfSwappingDobj = tmConfSwappingDobj;
    }

    /**
     * @return the relation_code
     */
    public String getRelation_code() {
        return relation_code;
    }

    /**
     * @param relation_code the relation_code to set
     */
    public void setRelation_code(String relation_code) {
        this.relation_code = relation_code;
    }

    /**
     * @return the relation_list
     */
    public List getRelation_list() {
        return relation_list;
    }

    /**
     * @param relation_list the relation_list to set
     */
    public void setRelation_list(List relation_list) {
        this.relation_list = relation_list;
    }

    /**
     * @return the renderUntracedCheck
     */
    public boolean isRenderUntracedCheck() {
        return renderUntracedCheck;
    }

    /**
     * @param renderUntracedCheck the renderUntracedCheck to set
     */
    public void setRenderUntracedCheck(boolean renderUntracedCheck) {
        this.renderUntracedCheck = renderUntracedCheck;
    }
}
