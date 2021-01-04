/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.agentlicence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nic.java.util.DateUtils;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.java.util.DateUtilsException;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.fee.FeeAgentRegnDobj;
import nic.vahan.form.impl.ApproveImpl;
import nic.vahan.form.impl.agentlicence.AgentDetailImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.primefaces.PrimeFaces;
import org.apache.log4j.Logger;

/**
 *
 * @author nicsi
 */
@ManagedBean(name = "agentRegistration")
@ViewScoped
public class AgentRegistrationBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(AgentRegistrationBean.class);
    @ManagedProperty(value = "#{approveImpl}")
    private ApproveImpl approveImpl;
    private FeeAgentRegnDobj dobj;
    AgentDetailImpl impl = new AgentDetailImpl();
    String data[][] = null;
    private List districtList;
    private int c_state;
    private String c_pincode = "";
    private boolean sameAsCurrAddress;
    private String curr_Add1 = "";
    private String curr_Add2 = "";
    private String city_N = "";
    private int action = 0;
    private String applNo = null;
    private boolean entry;
    private boolean verifyAprrove;
    private ArrayList<ComparisonBean> compBeanList;
    private String licNo = null;
    private String existingLicNo;
    private boolean flage;
    private String mobileNo = null;
    private SessionVariables sessionvariable = new SessionVariables();
    private String chooseNewRenewAgentLicOption;
    private boolean showRenewAgentLicenceApplication;
    private boolean showNewAgentLicenceApplication;
    private boolean showDupAgentLicenceApplication;
    private boolean dissableChooseNewRenewAgentLicenceOption;
    private boolean agentRegistraionPanel = true;
    private boolean agentRegistraionField;
    String transactionName = "";

    public AgentRegistrationBean() {
        districtList = new ArrayList<>();
        compBeanList = new ArrayList<ComparisonBean>();
        entry = true;
        verifyAprrove = false;

        dobj = new FeeAgentRegnDobj();
        data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
        districtList.clear();
        for (int i = 0; i < data.length; i++) {
            if (Util.getUserStateCode().equalsIgnoreCase(data[i][2])) {
                districtList.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
        String appl_no;
        String actionCode = String.valueOf(appl_details.getCurrent_action_cd());
        if (Integer.parseInt(actionCode) == TableConstants.AGENT_DETAIL_VERIFICATION
                || Integer.parseInt(actionCode) == TableConstants.AGENT_DETAIL_APPROVAL) {
            dissableChooseNewRenewAgentLicenceOption = true;

            entry = false;
            verifyAprrove = true;
            flage = true;
            sameAsCurrAddress = false;
            int purCode = 0;
            appl_no = sessionvariable.getSelectedWork().getAppl_no();
            purCode = sessionvariable.getSelectedWork().getPur_cd();
            if (purCode == TableConstants.AGENT_DETAIL_PUR_CD) {
                dobj.setNewRenewalAgentLicence("New_Agent_Licence");
                transactionName = "New_Agent_Licence";
                if (!CommonUtils.isNullOrBlank(appl_no)) {
                    get_agentdetails(appl_no);
                }
            } else if (purCode == TableConstants.AGENT_DETAIL_REN_PUR_CD) {
                dobj.setNewRenewalAgentLicence("Renew_Agent_Licence");
                transactionName = "Renew_Agent_Licence";
                agentRegistraionField = true;
                if (!CommonUtils.isNullOrBlank(appl_no)) {
                    get_agentdetails(appl_no);
                }
            } else {
                dobj.setNewRenewalAgentLicence("Duplicate_Agent_Licence");
                transactionName = "Duplicate_Agent_Licence";
                agentRegistraionField = true;
                if (!CommonUtils.isNullOrBlank(appl_no)) {
                    get_agentdetails(appl_no);
                }
            }
            PrimeFaces.current().ajax().update("console");
        }
    }

    public void dateSelectEvent() {
        Date dt = new java.util.Date();
        this.dobj.setValidFrom(dt);
        try {
            dt = DateUtils.addToDate(dt, DateUtils.YEAR, 1);
            dt = DateUtils.addToDate(dt, DateUtils.DAY, -1);
        } catch (DateUtilsException due) {
            LOGGER.error(due.toString() + " " + due.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, null));
        }
        this.dobj.setValidUpTo(dt);
    }

    public void get_agentdetails(String appl_no) {
        try {
            if (!CommonUtils.isNullOrBlank(appl_no)) {
                dobj.setAppl_no(appl_no);
                dobj = impl.getAgentData(dobj, Util.getUserStateCode(), Util.getUserSeatOffCode());
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), null));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, null));
        }
    }

    public void fillNewRenewAgentLicenceOption(AjaxBehaviorEvent event) {
        chooseNewRenewAgentLicOption = this.dobj.getNewRenewalAgentLicence();
        chooseOperation();
    }

    private void chooseOperation() {

        boolean enablingOneTimeChooseOption = false;
        if ("New_Agent_Licence".equals(chooseNewRenewAgentLicOption)) {
            reset();
            showNewAgentLicenceApplication = true;
            showRenewAgentLicenceApplication = false;
            showDupAgentLicenceApplication = false;
            this.dobj.setPurCd(TableConstants.AGENT_DETAIL_PUR_CD + "");
            dissableChooseNewRenewAgentLicenceOption = false;
            reset();
            showNewAgentLicenceApplication = false;
            enablingOneTimeChooseOption = true;
            agentRegistraionPanel = true;
            this.dobj.setNewRenewalAgentLicence("New_Agent_Licence");
            transactionName = "New_Agent_Licence";
        } else if ("Renew_Agent_Licence".equals(chooseNewRenewAgentLicOption)) {
            reset();
            showNewAgentLicenceApplication = false;
            showRenewAgentLicenceApplication = true;
            showDupAgentLicenceApplication = false;
            agentRegistraionPanel = false;
            this.dobj.setPurCd(TableConstants.AGENT_DETAIL_REN_PUR_CD + "");
            this.dobj.setNewRenewalAgentLicence("Renew_Agent_Licence");
            transactionName = "Renew_Agent_Licence";
        } else {
            reset();
            showNewAgentLicenceApplication = false;
            showRenewAgentLicenceApplication = false;
            showDupAgentLicenceApplication = true;
            agentRegistraionPanel = false;
            this.dobj.setPurCd(TableConstants.AGENT_DETAIL_DUP_PUR_CD + "");
            this.dobj.setNewRenewalAgentLicence("Duplicate_Agent_Licence");
            transactionName = "Duplicate_Agent_Licence";
        }
    }

    public void agentdetails(AjaxBehaviorEvent event) {
        try {
            if (!CommonUtils.isNullOrBlank(existingLicNo)) {
                dobj = impl.agentData(existingLicNo, Util.getUserStateCode(), Util.getUserSeatOffCode());
                existingLicNo = dobj.getLicence_No();
                Date valTo = dobj.getValidUpTo();
                Date currentDate = new Date();
                currentDate = DateUtils.parseDate(DateUtils.getCurrentDate());
                if (!CommonUtils.isNullOrBlank(transactionName)) {
                    if (transactionName.equalsIgnoreCase("Renew_Agent_Licence")) {
                        if (valTo.after(currentDate)) {
                            PrimeFaces.current().dialog().showMessageDynamic(
                                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!",
                                    "Agent Licence Validity is not expire."));
                        }
                    }
                }
                agentRegistraionPanel = true;
                if (!CommonUtils.isNullOrBlank(existingLicNo)) {
                    agentRegistraionField = true;
                } else {
                    agentRegistraionField = false;
                }
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), null));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, null));
        }
    }

    public void sameAsCurrentAddress() {
        if (this.sameAsCurrAddress) {
            dobj.setPcurrAdd1(dobj.getCurrAdd1());
            dobj.setPcurrAdd2(dobj.getCurrAdd2());
            dobj.setPcity(dobj.getCity());
            dobj.setP_district(dobj.getC_district());
            dobj.setP_pincode(dobj.getC_pincode());
        } else {
            dobj.setPcurrAdd1("");
            dobj.setPcurrAdd2("");
            dobj.setPcity("");
            dobj.setP_district(0);
            dobj.setP_pincode(0);
        }
    }

    public void reset() {
        if (dobj != null) {
            setSameAsCurrAddress(false);
            dobj.setMobileNo("");
            dobj.setOwnName("");
            dobj.setFname("");
            dobj.setCurrAdd1("");
            dobj.setCurrAdd2("");
            dobj.setCity("");
            dobj.setC_district(0);
            dobj.setC_pincode(0);
            dobj.setPcurrAdd1("");
            dobj.setPcurrAdd2("");
            dobj.setPcity("");
            dobj.setP_district(0);
            dobj.setP_pincode(0);
            dobj.setValidFrom(null);
            dobj.setValidUpTo(null);
            dobj.setCounter("");
            dobj.setPlaceOfBusiness("");
        }
    }

    public String save() {
        String returnLocation = "";
        if (appl_details.getCurrent_action_cd() == TableConstants.AGENT_DETAIL_APPLICATION) {
            try {
                if (!FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
                    return "";
                }
                if (dobj != null) {
                    applNo = impl.saveAgentDetails(dobj, Util.getUserStateCode(), Util.getUserLoginOffCode(), transactionName);
                    if (applNo != null) {
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Application '" + applNo + "' successfully submitted for Fee Collection.", "Application '" + applNo + "' successfully submitted for Fee Collection.");
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        //RequestContext rc = RequestContext.getCurrentInstance();
                        PrimeFaces.current().ajax().update("agentRegistraion:popup");
                        PrimeFaces.current().executeScript("PF('confDlgFee').show()");
                        return "";
                    } else {
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "There are some problems in saving application data", "There are some problems in saving application data");
                        FacesContext.getCurrentInstance().addMessage(null, message);
                    }
                }
            } catch (VahanException ve) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), null));
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, null));
            }
        } else {
            returnLocation = "seatwork";
        }
        return returnLocation;
    }

    private void setValidFrom() {
        Date currentDate = new java.util.Date();
        dobj.setValidFrom(currentDate);
    }

    private void setValidUpto() {

        Date currentDate = new java.util.Date();
        try {
            currentDate = DateUtils.addToDate(currentDate, DateUtils.YEAR, 1);
            currentDate = DateUtils.addToDate(currentDate, DateUtils.DAY, -1);
            this.dobj.setValidUpTo(currentDate);
        } catch (DateUtilsException due) {
            LOGGER.error(due.toString() + " " + due.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, null));
        }
    }

    @Override
    public String saveAndMoveFile() {
        String returnLocation = "", fileMoveReturn = "";
        if (appl_details.getCurrent_action_cd() == TableConstants.AGENT_DETAIL_VERIFICATION || appl_details.getCurrent_action_cd() == TableConstants.AGENT_DETAIL_APPROVAL) {
            dobj.setAppl_no(appl_details.getAppl_no());
            dobj.setPurCd(String.valueOf(appl_details.getPur_cd()));
            try {
                if (appl_details.getPur_cd() == TableConstants.AGENT_DETAIL_REN_PUR_CD) {
                    dobj.setLicence_No(appl_details.getRegn_no());
                    setValidFrom();
                    setValidUpto();
                } else if (appl_details.getPur_cd() == TableConstants.AGENT_DETAIL_DUP_PUR_CD) {
                    dobj.setLicence_No(appl_details.getRegn_no());
                }
                Status_dobj status = new Status_dobj();
                status.setAppl_dt(appl_details.getAppl_dt());
                status.setAppl_no(appl_details.getAppl_no());
                status.setPur_cd(appl_details.getPur_cd());
                status.setAction_cd(appl_details.getCurrent_action_cd());
                status.setState_cd(appl_details.getCurrent_state_cd());
                status.setOff_cd(appl_details.getCurrent_off_cd());
                status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
                status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
                status.setStatus(getApp_disapp_dobj().getNew_status());
                status.setCurrent_role(appl_details.getCurrent_role());
                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)) {
                    if (status.getStatus().equals(TableConstants.STATUS_REVERT)) {
                        status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                    }
                    fileMoveReturn = impl.saveAndMoveFeeAgent(status, dobj);
                    if (!CommonUtils.isNullOrBlank(fileMoveReturn)) {
                        dobj.setLicence_No(fileMoveReturn);
                        licNo = fileMoveReturn;
                        postSaveAndMoveOperation();
                        returnLocation = "";
                    } else {
                        returnLocation = "seatwork";
                    }
                }
            } catch (VahanException e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, null));
            }
        } else {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application data has to be saved first for verification.", "Application data has to be saved first for verification.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        return returnLocation;
    }

    private void postSaveAndMoveOperation() throws VahanException {
        //RequestContext rc = RequestContext.getCurrentInstance();
        PrimeFaces.current().ajax().update("form_detailentry:licencepopup");
        PrimeFaces.current().executeScript("PF('confDlgFeeAgentDetail').show()");
        saveApproveDetails();
    }

    public String saveApproveDetails() {
        return "home";
    }

    @Override
    public List<ComparisonBean> getCompBeanList() {
        return this.compBeanList;
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ApproveImpl getApproveImpl() {
        return approveImpl;
    }

    public void setApproveImpl(ApproveImpl approveImpl) {
        this.approveImpl = approveImpl;
    }

    public String getApplNo() {
        return applNo;
    }

    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    public boolean isEntry() {
        return entry;
    }

    public void setEntry(boolean entry) {
        this.entry = entry;
    }

    public boolean isVerifyAprrove() {
        return verifyAprrove;
    }

    public void setVerifyAprrove(boolean verifyAprrove) {
        this.verifyAprrove = verifyAprrove;
    }

    public String getLicNo() {
        return licNo;
    }

    public void setLicNo(String licNo) {
        this.licNo = licNo;
    }

    public boolean isFlage() {
        return flage;
    }

    public void setFlage(boolean flage) {
        this.flage = flage;
    }

    public FeeAgentRegnDobj getDobj() {
        return dobj;
    }

    public void setDobj(FeeAgentRegnDobj dobj) {
        this.dobj = dobj;
    }

    public int getC_state() {
        return c_state;
    }

    public void setC_state(int c_state) {
        this.c_state = c_state;
    }

    public List getDistrictList() {
        return districtList;
    }

    public void setDistrictList(List districtList) {
        this.districtList = districtList;
    }

    public String getC_pincode() {
        return c_pincode;
    }

    public void setC_pincode(String c_pincode) {
        this.c_pincode = c_pincode;
    }

    public boolean isSameAsCurrAddress() {
        return sameAsCurrAddress;
    }

    public void setSameAsCurrAddress(boolean sameAsCurrAddress) {
        this.sameAsCurrAddress = sameAsCurrAddress;
    }

    public String getCurr_Add1() {
        return curr_Add1;
    }

    public void setCurr_Add1(String curr_Add1) {
        this.curr_Add1 = curr_Add1;
    }

    public String getCurr_Add2() {
        return curr_Add2;
    }

    public void setCurr_Add2(String curr_Add2) {
        this.curr_Add2 = curr_Add2;
    }

    public String getCity_N() {
        return city_N;
    }

    public void setCity_N(String city_N) {
        this.city_N = city_N;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getChooseNewRenewAgentLicOption() {
        return chooseNewRenewAgentLicOption;
    }

    public void setChooseNewRenewAgentLicOption(String chooseNewRenewAgentLicOption) {
        this.chooseNewRenewAgentLicOption = chooseNewRenewAgentLicOption;
    }

    public boolean isShowRenewAgentLicenceApplication() {
        return showRenewAgentLicenceApplication;
    }

    public void setShowRenewAgentLicenceApplication(boolean showRenewAgentLicenceApplication) {
        this.showRenewAgentLicenceApplication = showRenewAgentLicenceApplication;
    }

    public boolean isShowNewAgentLicenceApplication() {
        return showNewAgentLicenceApplication;
    }

    public void setShowNewAgentLicenceApplication(boolean showNewAgentLicenceApplication) {
        this.showNewAgentLicenceApplication = showNewAgentLicenceApplication;
    }

    public boolean isShowDupAgentLicenceApplication() {
        return showDupAgentLicenceApplication;
    }

    public void setShowDupAgentLicenceApplication(boolean showDupAgentLicenceApplication) {
        this.showDupAgentLicenceApplication = showDupAgentLicenceApplication;
    }

    public boolean isDissableChooseNewRenewAgentLicenceOption() {
        return dissableChooseNewRenewAgentLicenceOption;
    }

    public void setDissableChooseNewRenewAgentLicenceOption(boolean dissableChooseNewRenewAgentLicenceOption) {
        this.dissableChooseNewRenewAgentLicenceOption = dissableChooseNewRenewAgentLicenceOption;
    }

    public String getExistingLicNo() {
        return existingLicNo;
    }

    public void setExistingLicNo(String existingLicNo) {
        this.existingLicNo = existingLicNo;
    }

    public boolean isAgentRegistraionPanel() {
        return agentRegistraionPanel;
    }

    public void setAgentRegistraionPanel(boolean agentRegistraionPanel) {
        this.agentRegistraionPanel = agentRegistraionPanel;
    }

    public boolean isAgentRegistraionField() {
        return agentRegistraionField;
    }

    public void setAgentRegistraionField(boolean agentRegistraionField) {
        this.agentRegistraionField = agentRegistraionField;
    }
}
