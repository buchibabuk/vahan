/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.bean;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.Dealer;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.user_mgmt.dobj.UserMgmtDobj;
import nic.vahan.db.user_mgmt.dobj.UserAuthorityDobj;
import nic.vahan.db.user_mgmt.impl.UserMgmtImpl;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.TreeNode;

@ManagedBean
@ViewScoped
/**
 *
 * @author tranC102
 */
public class UserMgmtBean implements Serializable {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(UserMgmtBean.class);
    private String state_cd = "";
    private int office_cd;
    private long user_cd = 0;
    private String user_name = "";
    private String desig_cd = "";
    private String user_id = "";
    private String user_pwd = "";
    private String off_phone = "";
    private long mobile_no;
    private String email = "";
    private String user_catg;
    private String selectedUserCatg;
    private String status = "";
    private TreeNode root;
    private boolean newEmp = true;
    private long created_by;
    private List stateList = new ArrayList();
    private List officeList = new ArrayList();
    private List desigList = new ArrayList();
    private List list_user_catg = new ArrayList();
    private TreeMap<Long, String> dtMap = new TreeMap<>();
    private UserMgmtDobj dobj = new UserMgmtDobj();
    private boolean state_render = false;
    private boolean off_render;
    private HtmlInputHidden txtNumber = new HtmlInputHidden();
    private String hiddenRandomNo;
    @ManagedProperty(value = "#{userAuthorityBean}")
    private UserAuthorityBean userAuthorityBean;
    private UserAuthorityDobj userDobj;
    private boolean renderPermPanel = false;
    private boolean resetPwdRender = false;
    private String newPwd = "";
    private String cnfrmPwd = "";
    private boolean resetPwdFlag = false;
    private boolean renderResetPwdCheck = false;
    private boolean userDetailRender = true;
    private boolean pwdField = true;
    private boolean renderUnblockUserCheck = false;
    private boolean unblockUserFlag = false;
    private Long aadharNo;
    private List filterList = null;
    private boolean renderAssignOffice = true;
    private List assignedOfficeList = new ArrayList();
    private boolean assignOffice_staff = false;
    private boolean assignedOff = false;
    private boolean disable = false;
    private boolean disableOfficeListItem = false;
    private boolean block_unblock = false;
    private boolean block_unlock_button = false;
    private boolean renderIpAdd = false;
    private boolean restrictUserCatg = false;
    private boolean restrictUserCatgButton = false;
    private String userPasswordMD5 = "";
    private boolean renderIpAddButton = false;
    private boolean renderModifyPanel = false;
    private boolean marqueeIPButton = false;

    /**
     * init() method for filling initial data according to the user login.
     */
    @PostConstruct
    public void init() {
        try {
            user_catg = Util.getUserCategory();
            reset();
            String[][] data = null;
            TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();
            if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_PORTAL_ADMIN)) {//Portal Admin
                showPortalAdminData(data);
            } else if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_STATE_ADMIN)) {//State Admin
                showStateAdminData(data);
                if (tmConfigurationDobj != null && tmConfigurationDobj.getTmConfigUserMgmtDobj() != null
                        && tmConfigurationDobj.getTmConfigUserMgmtDobj().isIp_config_by_state_admin()) {
                    renderIpAddButton = true;
                }
                restrictUserCatgButton = true;
            } else if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_OFFICE_ADMIN)) {//Office Admin
                showOfficeAdminData(data);
                if (tmConfigurationDobj != null && tmConfigurationDobj.getTmConfigUserMgmtDobj() != null
                        && tmConfigurationDobj.getTmConfigUserMgmtDobj().isIp_config_by_state_admin()) {
                    marqueeIPButton = true;
                    renderIpAddButton = false;
                    block_unlock_button = true;
                } else {
                    renderIpAddButton = true;
                    block_unlock_button = true;
                }
            } else if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER_ADMIN)) {//Dealer Admin
                showDealerData(data);
            } else if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_ADMIN)) {//Fitness Admin
                showFitnessAdminData(data);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage("Problem in getting record.");
        }
    }

    private void showPortalAdminData(String[][] data) {
        stateList = MasterTableFiller.getStateList();
        setBlock_unblock(true);
        renderIpAdd = false;
        block_unlock_button = false;
        data = MasterTableFiller.masterTables.TM_USER_CATG.getData();
        for (int i = 0; i < data.length; i++) {
            if (data[i][0].equalsIgnoreCase(TableConstants.USER_CATG_STATE_ADMIN)) {
                list_user_catg.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
        setState_render(true);
        setOff_render(false);
        setOffice_cd(0);
        setRenderAssignOffice(false);
    }

    private void showStateAdminData(String[][] data) {
        renderModifyPanel = true;
        state_cd = Util.getUserStateCode();
        officeList = MasterTableFiller.getOfficeList(state_cd);
        data = MasterTableFiller.masterTables.TM_USER_CATG.getData();
        for (int i = 0; i < data.length; i++) {
            if (data[i][0].equalsIgnoreCase(TableConstants.USER_CATG_REPORT_ADMIN)
                    || data[i][0].equalsIgnoreCase(TableConstants.USER_CATG_OFFICE_ADMIN)
                    || data[i][0].equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_ADMIN)
                    || data[i][0].equalsIgnoreCase(TableConstants.USER_CATG_STATE_HSRP)
                    || data[i][0].equalsIgnoreCase(TableConstants.USER_CATG_STATE_SMARTCARD)) {
                list_user_catg.add(new SelectItem(data[i][0], data[i][1]));
            }
        }

        data = MasterTableFiller.masterTables.TM_DESIGNATION.getData();
        for (int i = 0; i < data.length; i++) {
            desigList.add(new SelectItem(data[i][0], data[i][1]));
        }

        setState_render(false);
        setOff_render(true);
        fillDataTable();
    }

    private void showOfficeAdminData(String[][] data) {
        try {
            renderModifyPanel = true;
            setRenderPermPanel(true);
            data = MasterTableFiller.masterTables.TM_USER_CATG.getData();
            for (int i = 0; i < data.length; i++) {
                if (!user_catg.equalsIgnoreCase(data[i][0]) && !data[i][0].equalsIgnoreCase(TableConstants.USER_CATG_PORTAL_ADMIN)
                        && !data[i][0].equalsIgnoreCase(TableConstants.USER_CATG_STATE_ADMIN)
                        && !data[i][0].equalsIgnoreCase(TableConstants.USER_CATG_REPORT_ADMIN)
                        && !data[i][0].equalsIgnoreCase(TableConstants.USER_CATG_DEALER)
                        && !data[i][0].equalsIgnoreCase(TableConstants.USER_CATG_STATE_SUPER_ADMIN)
                        && !data[i][0].equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_USER)
                        && !data[i][0].equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_ADMIN)) {
                    list_user_catg.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
            officeList = UserMgmtImpl.getAssignedOfficeList(Long.parseLong(Util.getEmpCode()));
            if (!officeList.isEmpty()) {
            }
            state_cd = Util.getUserStateCode();
            office_cd = Util.getUserSeatOffCode();
            setState_render(false);
            setOff_render(false);
            fillDataTable();

            data = MasterTableFiller.masterTables.TM_DESIGNATION.getData();
            for (int i = 0; i < data.length; i++) {
                desigList.add(new SelectItem(data[i][0], data[i][1]));
            }
        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    private void showDealerData(String data[][]) {
        try {
            renderModifyPanel = true;
            setRenderPermPanel(true);
            data = MasterTableFiller.masterTables.TM_USER_CATG.getData();
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equalsIgnoreCase(TableConstants.USER_CATG_DEALER)) {
                    list_user_catg.add(new SelectItem(data[i][0], data[i][1]));
                }
            }

            //Not Required
            officeList = UserMgmtImpl.getAssignedOfficeList(Long.parseLong(Util.getEmpCode()));
            if (!officeList.isEmpty()) {
            }
            state_cd = Util.getUserStateCode();
            office_cd = Util.getSelectedSeat().getOff_cd();
            setState_render(false);
            setOff_render(false);
            fillDataTable();

            data = MasterTableFiller.masterTables.TM_DESIGNATION.getData();
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equalsIgnoreCase("DS")) {
                    desigList.add(new SelectItem(data[i][0], data[i][1]));
                }
            }

        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    private void showFitnessAdminData(String data[][]) {
        try {
            setRenderPermPanel(true);
            renderModifyPanel = true;
//            setBlock_unblock(true);
//            renderIpAdd = false;
//            block_unlock_button = false;
            data = MasterTableFiller.masterTables.TM_USER_CATG.getData();
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_USER)) {
                    list_user_catg.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
            officeList = UserMgmtImpl.getAssignedOfficeList(Long.parseLong(Util.getEmpCode()));
            state_cd = Util.getUserStateCode();
            office_cd = Util.getSelectedSeat().getOff_cd();
            setState_render(false);
            setOff_render(false);
            fillDataTable();
            data = MasterTableFiller.masterTables.TM_DESIGNATION.getData();
            for (int i = 0; i < data.length; i++) {
                desigList.add(new SelectItem(data[i][0], data[i][1]));
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void resetPwdListener() {
        if (resetPwdFlag) {
            setResetPwdRender(true);
            setUserDetailRender(false);
        } else {
            setResetPwdRender(false);
            setUserDetailRender(true);
        }
    }

    public void userCategoryListener() {
        try {
            if (selectedUserCatg.trim().equalsIgnoreCase(TableConstants.USER_CATG_STATE_ADMIN)
                    || selectedUserCatg.trim().equalsIgnoreCase(TableConstants.USER_CATG_OFFICE_ADMIN)
                    || selectedUserCatg.trim().equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_ADMIN)
                    || selectedUserCatg.trim().equalsIgnoreCase(TableConstants.USER_CATG_STATE_HSRP)
                    || selectedUserCatg.trim().equalsIgnoreCase(TableConstants.USER_CATG_STATE_SMARTCARD)) {
                boolean flag = false;
                flag = UserMgmtImpl.checkStateOrOffAdminUnique(state_cd, assignedOfficeList, selectedUserCatg);
                if (flag) {
                    JSFUtils.setFacesMessage("Admin already exist.", null, JSFUtils.WARN);
                    assignedOfficeList.clear();
                    setSelectedUserCatg("");
                } else {
                    JSFUtils.setFacesMessage("Valid user category.", null, JSFUtils.INFO);
                }
                if (selectedUserCatg.trim().equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_ADMIN)) {
                    //check selected off is of type Fitness Center
                    UserMgmtImpl implObj = new UserMgmtImpl();
                    flag = implObj.validateOfficeType(state_cd, selectedUserCatg, assignedOfficeList);
                    if (flag) {
                        JSFUtils.setFacesMessage("Invalid Combination of User Category & Office.", null, JSFUtils.WARN);
                        assignedOfficeList.clear();
                        setSelectedUserCatg("");
                    }
                }
            }
            if (selectedUserCatg.equalsIgnoreCase(TableConstants.USER_CATG_REPORT_ADMIN)) {
                setRenderAssignOffice(false);
            }
            if (selectedUserCatg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER_ADMIN)) {
                setRenderPermPanel(true);
                userAuthorityBean.setRenderAuthPanel(true);
                assignedOfficeListener();
                dealerCatgListener(selectedUserCatg);
            }

            if (selectedUserCatg.equals(TableConstants.USER_CATG_OFF_STAFF)
                    || selectedUserCatg.equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_USER)) {
                assignedOfficeListener();
                setRenderPermPanel(true);
                userAuthorityBean.setRenderAuthPanel(true);
                userAuthorityBean.setRenderCheckBoxRow(true);
                userAuthorityBean.setRenderVehicle(true);
                userAuthorityBean.setRenderDealer(false);
                TmConfigurationDobj configDobj = Util.getTmConfiguration();
                if (configDobj != null && configDobj.isUser_signature()) {
                    userAuthorityBean.setRenderSignatureUpload(true);
                }
            } else {
                userAuthorityBean.setRenderSignatureUpload(false);
            }

            if (selectedUserCatg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER)) {
                setAssignedOff(true);
            } else {
                setAssignedOff(false);
            }
            if (selectedUserCatg.equals(TableConstants.USER_CATG_OFFICE_ADMIN)) {
                setRenderPermPanel(true);
                TmConfigurationDobj configDobj = Util.getTmConfiguration();
                if (configDobj != null && configDobj.isUser_signature()) {
                    userAuthorityBean.setRenderSignatureUpload(true);
                } else {
                    userAuthorityBean.setRenderSignatureUpload(false);
                }
            }

            if (selectedUserCatg.equalsIgnoreCase(TableConstants.USER_CATG_HSRP)
                    || selectedUserCatg.equalsIgnoreCase(TableConstants.USER_CATG_SMARTCARD)) {
                setRenderPermPanel(false);
                assignedOfficeListener();
            }
        } catch (VahanException e) {
            JSFUtils.setFacesMessage(e.getMessage(), null, JSFUtils.WARN);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.setFacesMessage(TableConstants.SomthingWentWrong, null, JSFUtils.ERROR);
        }
    }

    public void assignedOfficeListener() {
        if (!assignedOfficeList.isEmpty()) {
            if (assignedOfficeList.size() > 1) {
                setAssignOffice_staff(true);
                assignedOfficeList.clear();
                if (selectedUserCatg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER_ADMIN)) {
                    userAuthorityBean.setRenderDealer(false);
                }
                JSFUtils.setFacesMessage("Multiple office's are not permitted for this User Category.", null, JSFUtils.ERROR);
                setSelectedUserCatg("");
                reset();
            }
        }
    }

    private void dealerCatgListener(String userCatg) {
        try {
            if (userCatg.equals(TableConstants.USER_CATG_DEALER_ADMIN)) {
                userAuthorityBean.setRenderCheckBoxRow(false);
                userAuthorityBean.setRegnSeqRender(false);
                userAuthorityBean.setRenderVehicle(true);
                userAuthorityBean.setRenderDealer(true);
                List<Dealer> listDealer = UserMgmtImpl.getDealerList(state_cd, assignedOfficeList);
                for (Dealer dl : listDealer) {
                    userAuthorityBean.getDealerList().add(new SelectItem(dl.getDealer_cd(), dl.getDealer_name()));
                }
                String[][] data = MasterTableFiller.masterTables.VM_MAKER.getData();
                for (int i = 0; i < data.length; i++) {
                    userAuthorityBean.getMakerList().add(new SelectItem(data[i][0], data[i][1]));
                }
            } else {
                userAuthorityBean.setRenderDealer(false);
            }
        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void userIdListener() {
        if (user_id == null || user_id.isEmpty() || "admin".equalsIgnoreCase(user_id) || "administrator".equalsIgnoreCase(user_id)
                || user_id.equals(user_name)) {
            user_id = null;
            JSFUtils.setFacesMessage("Invalid User ID.", null, JSFUtils.INFO);
        } else {
            boolean flag = UserMgmtImpl.checkUniqueUserID(user_id);
            if (flag) {
                JSFUtils.setFacesMessage("User ID already exists.", null, JSFUtils.WARN);
            } else {
                JSFUtils.setFacesMessage("Valid User ID.", null, JSFUtils.INFO);
            }
        }
    }

    //for filling datatable
    public void fillDataTable() {
        reset();
        if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_PORTAL_ADMIN)) {
            String[][] data = MasterTableFiller.masterTables.TM_DESIGNATION.getData();
            for (int i = 0; i < data.length; i++) {
                desigList.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
        dtMap.clear();
        dobj.setState_cd(state_cd);
        dobj.setOffice_cd(office_cd);
        dobj.setUser_catg(user_catg);
        if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_OFFICE_ADMIN)
                || user_catg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER_ADMIN)
                || user_catg.equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_ADMIN)) {
            dobj.setUser_cd(Long.parseLong(Util.getEmpCode()));
        }
        dobj.setDtMap(dtMap);
        UserMgmtImpl.fillDt(dobj);

        if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_STATE_ADMIN)) {
            assignedOfficeList.add(office_cd + "");
        }
        dobj.setUser_cd(0l);
    }

    //for Row Select event in dataTable
    public void updateListner(long emp_code) {
        try {
            reset();
            setPwdField(false);
            setDisable(true);
            setRenderResetPwdCheck(true);
            if (resetPwdFlag) {
                resetPwdFlag = false;
            }
            dobj.setState_cd(state_cd);
            dobj.setUser_cd(emp_code);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("userCd", emp_code);
            UserMgmtDobj robj = UserMgmtImpl.getEmpDetails(dobj);
            userDobj = UserMgmtImpl.getUserPermissionDetails(emp_code, userDobj);
            userAuthorityBean.setDobj(userDobj);
            setState_cd(state_cd);
            setUser_name(robj.getUser_name());
            setUser_cd(emp_code);
            setDesig_cd(robj.getDesig_cd());
            setUser_id(robj.getUser_id());
            setUser_pwd(robj.getUser_pwd());
            setOff_phone(robj.getOff_phone());
            setMobile_no(robj.getMobile_no());
            setEmail(robj.getEmail());
            setSelectedUserCatg(robj.getSelectedUserCatg());
            setStatus(robj.getStatus());
            setAadharNo(robj.getAadharNo());
//        if (status.equalsIgnoreCase("T")) {
//            setRenderUnblockUserCheck(true);
//            setRenderResetPwdCheck(false);
//            setUserDetailRender(false);
//        } else {
//            setRenderUnblockUserCheck(false);
//            setRenderResetPwdCheck(true);
//            setUserDetailRender(true);
//        }
            if (robj.getSelectedUserCatg().equalsIgnoreCase(TableConstants.USER_CATG_OFFICE_ADMIN)) {
                setDisableOfficeListItem(true);
                setRenderPermPanel(true);
                TmConfigurationDobj configDobj = Util.getTmConfiguration();
                if (configDobj != null && configDobj.isUser_signature()) {
                    userAuthorityBean.setRenderSignatureUpload(true);
                    if (userDobj.getSignatureFile() != null) {
                        userAuthorityBean.setViewSignFile(new DefaultStreamedContent(new ByteArrayInputStream(userDobj.getSignatureFile())));
                    }
                } else {
                    userAuthorityBean.setRenderSignatureUpload(false);
                }
                if (userDobj.getSignatureFile() != null && !userDobj.getSignatureFile().equals("")) {
                    userAuthorityBean.setRenderedRemoveSignature(true);
                } else {
                    userAuthorityBean.setRenderedRemoveSignature(false);
                }
            }
            if (userDobj != null) {
                setAssignedOfficeList(ServerUtil.makeList(userDobj.getAssignedOffice()));
                if (!robj.getSelectedUserCatg().equalsIgnoreCase(TableConstants.USER_CATG_STATE_ADMIN)
                        && !robj.getSelectedUserCatg().equalsIgnoreCase(TableConstants.USER_CATG_OFFICE_ADMIN)
                        && !robj.getSelectedUserCatg().equalsIgnoreCase(TableConstants.USER_CATG_DEALER)) {
                    setRenderPermPanel(true);
                    userAuthorityBean.setRenderAuthPanel(true);
                    if (robj.getSelectedUserCatg().equals(TableConstants.USER_CATG_SMARTCARD)
                            || robj.getSelectedUserCatg().equals(TableConstants.USER_CATG_HSRP)
                            || robj.getSelectedUserCatg().equals(TableConstants.USER_CATG_REPORT_ADMIN)
                            || robj.getSelectedUserCatg().equals(TableConstants.USER_CATG_FITNESS_CENTER_ADMIN)) {
                        setRenderPermPanel(false);
                        userAuthorityBean.setRenderAuthPanel(false);
                        if (robj.getSelectedUserCatg().equals(TableConstants.USER_CATG_REPORT_ADMIN)) {
                            setRenderAssignOffice(false);
                        }
                    }
                    if (userDobj.getLowerBound() != 0 && userDobj.getUpperBound() != 0) {
                        userAuthorityBean.setRegnSeqRender(true);
                        userAuthorityBean.setDisable(false);
                        userAuthorityBean.setDisable(false);
                        userAuthorityBean.setLowerBound(userDobj.getLowerBound());
                        userAuthorityBean.setUpperBound(userDobj.getUpperBound());
                    }
                    if (userDobj.getVehType() != 0) {
                        userAuthorityBean.vehTypeListener();
                    }
                    if (!userDobj.getSelectedVehClass().isEmpty()) {
                        userAuthorityBean.vehTypeCatgListener();
                    }
                    if (userDobj.getPermitType() != null) {
                        userAuthorityBean.permitTypeListener();
                    }
                    if (userDobj.getDealerCode() != null) {
                        dealerCatgListener(selectedUserCatg);
                        userAuthorityBean.setDisableDealer(true);
                    }
                    userAuthorityBean.setRenderCheckBoxRow(true);
                    if (userDobj.isIsEnforcementOfficer() && userAuthorityBean.getDobj().getTeam_id() != 0) {
                        userAuthorityBean.setRenderTeam_id(true);
                        userAuthorityBean.getDobj().setTeam_id(userDobj.getTeam_id());
                    } else {
                        userAuthorityBean.setRenderTeam_id(false);
                    }
                    TmConfigurationDobj configDobj = Util.getTmConfiguration();
                    if (configDobj != null && configDobj.isUser_signature() && robj.getSelectedUserCatg().equalsIgnoreCase(TableConstants.USER_CATG_OFF_STAFF)) {
                        userAuthorityBean.setRenderSignatureUpload(true);
                        if (userDobj.getSignatureFile() != null) {
                            userAuthorityBean.setRenderedRemoveSignature(true);
                            userAuthorityBean.setViewSignFile(new DefaultStreamedContent(new ByteArrayInputStream(userDobj.getSignatureFile())));
                        }
                    } else {
                        userAuthorityBean.setRenderSignatureUpload(false);
                    }

                    UserMgmtImpl implObj = new UserMgmtImpl();
                    if (Util.getUserStateCode().equalsIgnoreCase("DL") && !implObj.isSelfDealer(userDobj, Util.getUserStateCode())) {
                        userAuthorityBean.setRenderAllOfficeAuth(true);
                    } else {
                        if (configDobj != null && configDobj.isDealer_auth_for_all_office()) {
                            userAuthorityBean.setRenderAllOfficeAuth(true);
                        } else {
                            userAuthorityBean.setRenderAllOfficeAuth(false);
                        }
                    }
                }
            }
        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void saveEmpRecord() {
        String msg = "";
        try {
            if (!resetPwdFlag && !unblockUserFlag) {
                userDobj = userAuthorityBean.getDobj();
                userDobj.setPermitTypeCount(userAuthorityBean.getPermitTypeList().size());
                userDobj.setPermitTypeCatgCount(userAuthorityBean.getPermitCatgList().size());
                newEmp = true;
                if (state_cd != null && user_name != null && user_id != null) {
                    created_by = Long.parseLong(Util.getEmpCode());
                    dobj.setState_cd(state_cd.toUpperCase());
                    if (assignedOfficeList != null && !assignedOfficeList.isEmpty()) {
                        if (assignedOfficeList.size() < 2) {
                            dobj.setOffice_cd(Integer.parseInt(assignedOfficeList.get(0).toString()));
                            dobj.setAssignedOffice(assignedOfficeList.get(0).toString());
                            userDobj.setAssignedOffice(assignedOfficeList.get(0).toString());
                        } else {
                            if (selectedUserCatg.equalsIgnoreCase(TableConstants.USER_CATG_OFFICE_ADMIN)) {
                                String offC = "";
                                for (Object temp : assignedOfficeList) {
                                    offC += temp + ",";
                                }
                                dobj.setAssignedOffice(offC);
                                userDobj.setAssignedOffice(offC);
                            } else {
                                if (!selectedUserCatg.equalsIgnoreCase(TableConstants.USER_CATG_STATE_HSRP)
                                        && (!selectedUserCatg.equalsIgnoreCase(TableConstants.USER_CATG_STATE_SMARTCARD))) {
                                    assignedOfficeListener();
                                }
                            }
                            dobj.setOffice_cd(0);
                        }
                    }

                    if (assignOffice_staff) {
                        msg = "Error in saving employee data, cannot assign more than one office";
                        JSFUtils.setFacesMessage(msg, null, JSFUtils.ERROR);
                        setAssignOffice_staff(false);
                        return;
                        // throw new VahanException();
                    }

                    dobj.setUser_name(user_name.toUpperCase());
                    dobj.setDesig_cd(desig_cd);
                    dobj.setUser_id(user_id.trim());
                    dobj.setUser_pwd(user_pwd);
                    dobj.setOff_phone(off_phone);
                    dobj.setMobile_no(mobile_no);
                    dobj.setEmail(email);
                    dobj.setUser_catg(user_catg);
                    dobj.setSelectedUserCatg(selectedUserCatg);
                    dobj.setStatus("F");
                    dobj.setCreated_by(created_by);
                    dobj.setRenderPermPanel(renderPermPanel);
                    dobj.setAadharNo(0l);
                    dobj.setNewuser_change_password("T");
                    msg = UserMgmtImpl.checkSaveOrUpdateEmpRecord(dobj, userDobj);
                    JSFUtils.setFacesMessage(msg, null, JSFUtils.INFO);
                    newReset();

                } else {
                    msg = "Provide Correct data !!!";
                    JSFUtils.setFacesMessage(msg, null, JSFUtils.WARN);
                }
                fillDataTable();
            } else {
                if (resetPwdFlag) {
                    dobj.setUser_id(user_id);
                    dobj.setUser_cd(user_cd);

                    //dobj.setUser_pwd(ServerUtil.MD5(newPwd));
                    dobj.setUser_pwd(ServerUtil.sha256hex(newPwd));
                    boolean flag = UserMgmtImpl.updatePassword(dobj);
                    if (flag) {
                        msg = "Password Updated Successfully !!!";
                    } else {
                        msg = "Technical Problem !!!";
                    }
                }
//                else if (unblockUserFlag) {
//                    ServerUtil.updateLoginStatus(user_cd, "F");
//                    msg = "User unblocked successfully !!!";
//                    setUserDetailRender(true);
//                    setRenderResetPwdCheck(true);
//                    setRenderUnblockUserCheck(false);
//                }
                JSFUtils.setFacesMessage(msg, null, JSFUtils.INFO);
            }
        } catch (VahanException e) {
            JSFUtils.setFacesMessage(e.getMessage(), null, JSFUtils.WARN);
        } //        catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
        //            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        //            JSFUtils.setFacesMessage(TableConstants.SomthingWentWrong, null, JSFUtils.ERROR);
        //        } 
        catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            JSFUtils.setFacesMessage(TableConstants.SomthingWentWrong, null, JSFUtils.ERROR);
        }
    }

//    public void deleteRecord() {
//        String m = "";
//        if (state_cd != null && user_cd != null) {
//            dobj.setState_cd(state_cd);
//            dobj.setUser_cd(user_cd);
//            m = UserMgmtImpl.deleteEmp(dobj);
//
//        } else {
//            m = "Provide Employee Code!!!";
//        }
//        delReset();
//        fillDataTable();
//        FacesContext context = FacesContext.getCurrentInstance();
//        context.addMessage(null, new FacesMessage(m));
//    }
    public void newReset() {
        setPwdField(true);
        setDisable(false);
        setUserDetailRender(true);
        setRenderResetPwdCheck(false);
        setResetPwdRender(false);
        setResetPwdFlag(false);
        setRenderUnblockUserCheck(false);
        setRenderAssignOffice(true);
        setDisableOfficeListItem(false);
        if (assignedOfficeList != null) {
            assignedOfficeList.clear();
        }
        desig_cd = "";
        user_cd = 0;
        email = "";
        user_name = "";
        mobile_no = 0;
        aadharNo = null;
        off_phone = "";
        status = "";
        selectedUserCatg = "";
        user_id = "";
        user_pwd = "";
        newEmp = false;
        if (dobj != null) {
            dobj.setCreated_by(0l);
            dobj.setEmail("");
            dobj.setUser_catg("");
            dobj.setUser_cd(0);
            dobj.setDesig_cd("");
            dobj.setUser_id("");
            dobj.setUser_name("");
            dobj.setUser_pwd("");
            dobj.setMobile_no(0);
            dobj.setAadharNo(null);
        }
        if (userDobj != null) {
            userDobj.setVehType(0);
            userDobj.getSelectedVehClass().clear();
            userDobj.getSelectedPermitCatg().clear();
            userDobj.getPermitType().clear();
            userDobj.getSelectedMakerType().clear();
            userDobj.setDealerCode("");
            userDobj.setAllOfficeAuth(false);
        }
        userAuthorityBean.setDobj(new UserAuthorityDobj());
        userAuthorityBean.getPermitTypeList().clear();
        userAuthorityBean.getPermitCatgList().clear();
        userAuthorityBean.getVehClassList().clear();
        userAuthorityBean.getMakerList().clear();
        userAuthorityBean.getDealerList().clear();
        userAuthorityBean.setLowerBound(1);
        userAuthorityBean.setUpperBound(9999);
        userAuthorityBean.setRegnSeqRender(false);
        setRenderPermPanel(false);
        userAuthorityBean.setDisableDealer(false);
        userAuthorityBean.setRenderAllOfficeAuth(false);
        userAuthorityBean.setRenderSignatureUpload(false);
        userAuthorityBean.setViewSignFile(null);
    }

//    public void delReset() {
//        userDobj = userAuthorityBean.getDobj();
//        desig_cd = "";
//        email = "";
//        user_cd = 0;
//        user_name = "";
//        mobile_no = 0;
//        off_phone = "";
//        status = "";
//        selectedUserCatg = "";
//        user_id = "";
//        user_pwd = "";
//        newEmp = false;
//        userDobj.setVehType(0);
//        userDobj.getSelectedVehClass().clear();
//        userDobj.getSelectedPermitCatg().clear();
//        userDobj.getPermitType().clear();
//        userDobj.setDealerCode("");
//        userAuthorityBean.getPermitCatgList().clear();
//        userAuthorityBean.getVehClassList().clear();
//        userAuthorityBean.getLowerBound().setValue(null);
//        userAuthorityBean.getUpperBound().setValue(null);
//        userAuthorityBean.setRegnSeqRender(false);
//        setRenderPermPanel(false);
//    }
    public void reset() {
        setPwdField(true);
        setDisable(false);
        setUserDetailRender(true);
        setRenderResetPwdCheck(false);
        setResetPwdRender(false);
        setResetPwdFlag(false);
        setRenderUnblockUserCheck(false);
        setRenderAssignOffice(true);
        setDisableOfficeListItem(false);
        if (assignedOfficeList != null) {
            assignedOfficeList.clear();
        }
        if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_OFFICE_ADMIN)
                || user_catg.equalsIgnoreCase(TableConstants.USER_CATG_STATE_ADMIN)
                || user_catg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER_ADMIN)) {
            desig_cd = "";
            email = "";
            user_cd = 0;
            user_name = "";
            mobile_no = 0;
            aadharNo = null;
            off_phone = "";
            status = "";
            selectedUserCatg = "";
            user_id = "";
            user_pwd = "";
            if (dobj != null) {
                dobj.setCreated_by(0l);
                dobj.setEmail("");
                dobj.setUser_catg("");
                dobj.setUser_cd(0);
                dobj.setDesig_cd("");
                dobj.setUser_id("");
                dobj.setUser_name("");
                dobj.setUser_pwd("");
                dobj.setMobile_no(0);
                dobj.setAadharNo(null);
            }
            if (userDobj != null) {
                userDobj.setVehType(0);
                userDobj.getSelectedVehClass().clear();
                userDobj.getSelectedPermitCatg().clear();
                userDobj.getPermitType().clear();
                userDobj.getSelectedMakerType().clear();
                userDobj.setDealerCode("");
                userDobj.setAllOfficeAuth(false);
            } else {
                userAuthorityBean.setDobj(new UserAuthorityDobj());
            }
            userAuthorityBean.getPermitTypeList().clear();
            userAuthorityBean.getPermitCatgList().clear();
            userAuthorityBean.getVehClassList().clear();
            userAuthorityBean.getMakerList().clear();
            userAuthorityBean.getDealerList().clear();
            userAuthorityBean.setLowerBound(1);
            userAuthorityBean.setUpperBound(9999);
            userAuthorityBean.setRegnSeqRender(false);
            setRenderPermPanel(false);
            userAuthorityBean.setDisableDealer(false);
            userAuthorityBean.setRenderAllOfficeAuth(false);
            userAuthorityBean.setRenderSignatureUpload(false);
            userAuthorityBean.setViewSignFile(null);
        } else {
            desig_cd = "";
            email = "";
            user_cd = 0;
            user_name = "";
            mobile_no = 0;
            aadharNo = null;
            off_phone = "";
            status = "";
            selectedUserCatg = "";
            user_id = "";
            user_pwd = "";
            office_cd = 0;
            dtMap.clear();
            if (userDobj != null) {
                userDobj.setVehType(0);
                userDobj.getSelectedVehClass().clear();
                userDobj.getSelectedPermitCatg().clear();
                userDobj.getPermitType().clear();
                userDobj.getSelectedMakerType().clear();
                userDobj.setDealerCode("");
                userDobj.setAllOfficeAuth(false);
            } else {
                userAuthorityBean.setDobj(new UserAuthorityDobj());
            }
            userAuthorityBean.getPermitTypeList().clear();
            userAuthorityBean.getPermitCatgList().clear();
            userAuthorityBean.getVehClassList().clear();
            userAuthorityBean.setLowerBound(1);
            userAuthorityBean.setUpperBound(9999);
            userAuthorityBean.setRegnSeqRender(false);
            setRenderPermPanel(false);
            userAuthorityBean.setDisableDealer(false);
            userAuthorityBean.setRenderAllOfficeAuth(false);
            userAuthorityBean.setRenderSignatureUpload(false);
            userAuthorityBean.setViewSignFile(null);
        }

    }

    public void renderIpAddPanel() {
        setRenderIpAdd(true);
        block_unblock = false;
        block_unlock_button = false;
        restrictUserCatgButton = false;
        renderIpAddButton = false;

    }

    public void renderRistrictCatg() {
        restrictUserCatg = true;
        restrictUserCatgButton = false;
        renderIpAddButton = false;
    }

    /**
     * @return the state_cd
     */
    public String getState_cd() {
        return state_cd;
    }

    /**
     * @param state_cd the state_cd to set
     */
    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    /**
     * @return the office_cd
     */
    public int getOffice_cd() {
        return office_cd;
    }

    /**
     * @param office_cd the office_cd to set
     */
    public void setOffice_cd(int office_cd) {
        this.office_cd = office_cd;
    }

    /**
     * @return the user_cd
     */
    public long getUser_cd() {
        return user_cd;
    }

    /**
     * @param user_cd the user_cd to set
     */
    public void setUser_cd(long user_cd) {
        this.user_cd = user_cd;
    }

    /**
     * @return the user_name
     */
    public String getUser_name() {
        return user_name;
    }

    /**
     * @param user_name the user_name to set
     */
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    /**
     * @return the desig_cd
     */
    public String getDesig_cd() {
        return desig_cd;
    }

    /**
     * @param desig_cd the desig_cd to set
     */
    public void setDesig_cd(String desig_cd) {
        this.desig_cd = desig_cd;
    }

    /**
     * @return the user_id
     */
    public String getUser_id() {
        return user_id;
    }

    /**
     * @param user_id the user_id to set
     */
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    /**
     * @return the user_pwd
     */
    public String getUser_pwd() {
        return user_pwd;
    }

    /**
     * @param user_pwd the user_pwd to set
     */
    public void setUser_pwd(String user_pwd) {
        this.user_pwd = user_pwd;
    }

    /**
     * @return the off_phone
     */
    public String getOff_phone() {
        return off_phone;
    }

    /**
     * @param off_phone the off_phone to set
     */
    public void setOff_phone(String off_phone) {
        this.off_phone = off_phone;
    }

    /**
     * @return the mobile_no
     */
    public long getMobile_no() {
        return mobile_no;
    }

    /**
     * @param mobile_no the mobile_no to set
     */
    public void setMobile_no(long mobile_no) {
        this.mobile_no = mobile_no;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the user_catg
     */
    public String getUser_catg() {
        return user_catg;
    }

    /**
     * @param user_catg the user_catg to set
     */
    public void setUser_catg(String user_catg) {
        this.user_catg = user_catg;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the dobj
     */
    public UserMgmtDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(UserMgmtDobj dobj) {
        this.dobj = dobj;
    }

    /**
     * @return the officeList
     */
    public List getOfficeList() {
        return officeList;
    }

    /**
     * @param officeList the officeList to set
     */
    public void setOfficeList(List officeList) {
        this.officeList = officeList;
    }

    /**
     * @return the root
     */
    public TreeNode getRoot() {
        return root;
    }

    /**
     * @param root the root to set
     */
    public void setRoot(TreeNode root) {
        this.root = root;
    }

    /**
     * @return the created_by
     */
    public long getCreated_by() {
        return created_by;
    }

    /**
     * @param created_by the created_by to set
     */
    public void setCreated_by(long created_by) {
        this.created_by = created_by;
    }

    /**
     * @return the desigList
     */
    public List getDesigList() {
        return desigList;
    }

    /**
     * @param desigList the desigList to set
     */
    public void setDesigList(List desigList) {
        this.desigList = desigList;
    }

    /**
     * @return the newEmp
     */
    public boolean isNewEmp() {
        return newEmp;
    }

    /**
     * @param newEmp the newEmp to set
     */
    public void setNewEmp(boolean newEmp) {
        this.newEmp = newEmp;
    }

    /**
     * @return the dtMap
     */
    public TreeMap<Long, String> getDtMap() {
        return dtMap;
    }

    /**
     * @param dtMap the dtMap to set
     */
    public void setDtMap(TreeMap<Long, String> dtMap) {
        this.dtMap = dtMap;
    }

    /**
     * @return the list_user_catg
     */
    public List getList_user_catg() {
        return list_user_catg;
    }

    /**
     * @param list_user_catg the list_user_catg to set
     */
    public void setList_user_catg(List list_user_catg) {
        this.list_user_catg = list_user_catg;
    }

    /**
     * @return the stateList
     */
    public List getStateList() {
        return stateList;
    }

    /**
     * @param stateList the stateList to set
     */
    public void setStateList(List stateList) {
        this.stateList = stateList;
    }

    public boolean isState_render() {
        return state_render;
    }

    public void setState_render(boolean state_render) {
        this.state_render = state_render;
    }

    public boolean isOff_render() {
        return off_render;
    }

    public void setOff_render(boolean off_render) {
        this.off_render = off_render;
    }

    /**
     * @return the txtNumber
     */
    public HtmlInputHidden getTxtNumber() {
        return txtNumber;
    }

    /**
     * @param txtNumber the txtNumber to set
     */
    public void setTxtNumber(HtmlInputHidden txtNumber) {
        this.txtNumber = txtNumber;
    }

    public String getHiddenRandomNo() {
        return hiddenRandomNo;
    }

    public void setHiddenRandomNo(String hiddenRandomNo) {
        this.hiddenRandomNo = hiddenRandomNo;
    }

    /**
     * @return the selectedUserCatg
     */
    public String getSelectedUserCatg() {
        return selectedUserCatg;
    }

    /**
     * @param selectedUserCatg the selectedUserCatg to set
     */
    public void setSelectedUserCatg(String selectedUserCatg) {
        this.selectedUserCatg = selectedUserCatg;
    }

    public UserAuthorityBean getUserAuthorityBean() {
        return userAuthorityBean;
    }

    public void setUserAuthorityBean(UserAuthorityBean userAuthorityBean) {
        this.userAuthorityBean = userAuthorityBean;
    }

    public UserAuthorityDobj getUserDobj() {
        return userDobj;
    }

    public void setUserDobj(UserAuthorityDobj userDobj) {
        this.userDobj = userDobj;
    }

    public boolean isRenderPermPanel() {
        return renderPermPanel;
    }

    public void setRenderPermPanel(boolean renderPermPanel) {
        this.renderPermPanel = renderPermPanel;
    }

    public boolean isResetPwdRender() {
        return resetPwdRender;
    }

    public void setResetPwdRender(boolean resetPwdRender) {
        this.resetPwdRender = resetPwdRender;
    }

    public String getNewPwd() {
        return newPwd;
    }

    public void setNewPwd(String newPwd) {
        this.newPwd = newPwd;
    }

    public String getCnfrmPwd() {
        return cnfrmPwd;
    }

    public void setCnfrmPwd(String cnfrmPwd) {
        this.cnfrmPwd = cnfrmPwd;
    }

    public boolean isResetPwdFlag() {
        return resetPwdFlag;
    }

    public void setResetPwdFlag(boolean resetPwdFlag) {
        this.resetPwdFlag = resetPwdFlag;
    }

    public boolean isRenderResetPwdCheck() {
        return renderResetPwdCheck;
    }

    public void setRenderResetPwdCheck(boolean renderResetPwdCheck) {
        this.renderResetPwdCheck = renderResetPwdCheck;
    }

    public boolean isUserDetailRender() {
        return userDetailRender;
    }

    public void setUserDetailRender(boolean userDetailRender) {
        this.userDetailRender = userDetailRender;
    }

    public boolean isPwdField() {
        return pwdField;
    }

    public void setPwdField(boolean pwdField) {
        this.pwdField = pwdField;
    }

    public boolean isRenderUnblockUserCheck() {
        return renderUnblockUserCheck;
    }

    public void setRenderUnblockUserCheck(boolean renderUnblockUserCheck) {
        this.renderUnblockUserCheck = renderUnblockUserCheck;
    }

    public boolean isUnblockUserFlag() {
        return unblockUserFlag;
    }

    public void setUnblockUserFlag(boolean unblockUserFlag) {
        this.unblockUserFlag = unblockUserFlag;
    }

    public Long getAadharNo() {
        return aadharNo;
    }

    public void setAadharNo(Long aadharNo) {
        this.aadharNo = aadharNo;
    }

    /**
     * @return the filterList
     */
    public List getFilterList() {
        return filterList;
    }

    /**
     * @param filterList the filterList to set
     */
    public void setFilterList(List filterList) {
        this.filterList = filterList;
    }

    public List getAssignedOfficeList() {
        return assignedOfficeList;
    }

    public void setAssignedOfficeList(List assignedOfficeList) {
        this.assignedOfficeList = assignedOfficeList;
    }

    public boolean isRenderAssignOffice() {
        return renderAssignOffice;
    }

    public void setRenderAssignOffice(boolean renderAssignOffice) {
        this.renderAssignOffice = renderAssignOffice;
    }

    public boolean isAssignOffice_staff() {
        return assignOffice_staff;
    }

    public void setAssignOffice_staff(boolean assignOffice_dealer) {
        this.assignOffice_staff = assignOffice_dealer;
    }

    public boolean isAssignedOff() {
        return assignedOff;
    }

    public void setAssignedOff(boolean assignedOff) {
        this.assignedOff = assignedOff;
    }

    /**
     * @return the disable
     */
    public boolean isDisable() {
        return disable;
    }

    /**
     * @param disable the disable to set
     */
    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    /**
     * @return the disableOfficeListItem
     */
    public boolean isDisableOfficeListItem() {
        return disableOfficeListItem;
    }

    /**
     * @param disableOfficeListItem the disableOfficeListItem to set
     */
    public void setDisableOfficeListItem(boolean disableOfficeListItem) {
        this.disableOfficeListItem = disableOfficeListItem;
    }

    public boolean isBlock_unblock() {
        return block_unblock;
    }

    public void setBlock_unblock(boolean block_unblock) {
        this.block_unblock = block_unblock;
    }

    public void renderPanelAndButton(String regType) {
        renderModifyPanel = false;
        block_unblock = false;
        block_unlock_button = false;
        renderIpAddButton = false;
        restrictUserCatgButton = false;
        if ("B".equals(regType)) {
            block_unblock = true;
        } else if ("I".equals(regType)) {
            renderIpAdd = true;
        } else if ("R".equals(regType)) {
            restrictUserCatg = true;
        }

    }

    public boolean isBlock_unlock_button() {
        return block_unlock_button;
    }

    public void setBlock_unlock_button(boolean block_unlock_button) {
        this.block_unlock_button = block_unlock_button;
    }

    /**
     * @return the renderIpAdd
     */
    public boolean isRenderIpAdd() {
        return renderIpAdd;
    }

    /**
     * @param renderIpAdd the renderIpAdd to set
     */
    public void setRenderIpAdd(boolean renderIpAdd) {
        this.renderIpAdd = renderIpAdd;
    }

    /**
     * @return the userPasswordMD5
     */
    public String getUserPasswordMD5() {
        return userPasswordMD5;
    }

    /**
     * @param userPasswordMD5 the userPasswordMD5 to set
     */
    public void setUserPasswordMD5(String userPasswordMD5) {
        this.userPasswordMD5 = userPasswordMD5;
    }

    /**
     * @return the restrictUserCatg
     */
    public boolean isRestrictUserCatg() {
        return restrictUserCatg;
    }

    /**
     * @param restrictUserCatg the restrictUserCatg to set
     */
    public void setRestrictUserCatg(boolean restrictUserCatg) {
        this.restrictUserCatg = restrictUserCatg;
    }

    /**
     * @return the restrictUserCatgButton
     */
    public boolean isRestrictUserCatgButton() {
        return restrictUserCatgButton;
    }

    /**
     * @param restrictUserCatgButton the restrictUserCatgButton to set
     */
    public void setRestrictUserCatgButton(boolean restrictUserCatgButton) {
        this.restrictUserCatgButton = restrictUserCatgButton;
    }

    public boolean isRenderIpAddButton() {
        return renderIpAddButton;
    }

    public void setRenderIpAddButton(boolean renderIpAddButton) {
        this.renderIpAddButton = renderIpAddButton;
    }

    /**
     * @return the renderModifyPanel
     */
    public boolean isRenderModifyPanel() {
        return renderModifyPanel;
    }

    /**
     * @param renderModifyPanel the renderModifyPanel to set
     */
    public void setRenderModifyPanel(boolean renderModifyPanel) {
        this.renderModifyPanel = renderModifyPanel;
    }

    /**
     * @return the marqueeIPButton
     */
    public boolean isMarqueeIPButton() {
        return marqueeIPButton;
    }

    /**
     * @param marqueeIPButton the marqueeIPButton to set
     */
    public void setMarqueeIPButton(boolean marqueeIPButton) {
        this.marqueeIPButton = marqueeIPButton;
    }
}
