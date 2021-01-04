/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.hsrp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.hsrp.NewHsrpDo;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.hsrp.HSRPRequestImpl;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author iftekhar
 */
@ManagedBean(name = "newhsrp")
@ViewScoped
public class NewHsrpRequest extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private String reset = "";
    private String registrationNo = "";
    private NewHsrpDo dobj = null;
    private NewHsrpDo prev_dobj = null;
    private String hsrpMenu = "";
    private List hsrpMenuList = new ArrayList();
    private String headerTitle = "";
    private boolean isDisplayHsrpPanel = false;
    private static final Logger LOGGER = Logger.getLogger(NewHsrpRequest.class);
    private String firNo = "";
    private String policeStation = "";
    private Date firDate = null;
    private OwnerDetailsDobj ownerDetail;
    private OwnerImpl ownerImpl = null;
    private boolean isSearchPanelFlag = false;
    private boolean isApplicationFlag = false;
    private String applicationNo = "";
    private boolean isSaveOrUpdate = false;
    private boolean IsFirPanel = false;
    private String regnNo = "";
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private List<ComparisonBean> prevChangedDataList;
    private boolean isSaveBtnVisible = true;
    private String applNoGenMessage = "";
    private Date maxFirDate = new Date();
    private String hsrpReason = "";
    private List hsrpResonList = new ArrayList();
    private boolean isHsrpReason = false;
    private boolean isOkBtn = true;
    private boolean inputSelection = false;

    /**
     * @return the reset
     */
    @PostConstruct
    public void Init() {
        try {
            resetForm();
            ownerImpl = new OwnerImpl();
            String role_cd = String.valueOf(appl_details.getCurrent_action_cd());
            applicationNo = appl_details.getAppl_no();
            HttpSession session = Util.getSession();
            Map map = (Map) session.getAttribute("seat_map");
            if (Integer.parseInt(role_cd) == (TableConstants.DUPLICATE_HSRP_APPROVAL_CD) || Integer.parseInt(role_cd) == (TableConstants.DUPLICATE_HSRP_APPROVAL_CD_VENDOR)) {
//                applicationNo = map.get("appl_no") == null ? "" : (String) map.get("appl_no");
                setIsSearchPanelFlag(true);
                setIsDisplayHsrpPanel(true);
                setIsApplicationFlag(true);
                setIsSaveBtnVisible(false);
                registrationNo = map.get("regn_no") == null ? "" : (String) map.get("regn_no");
                fillHsrpDetails(applicationNo);
                setInputSelection(true);
            } else if (Integer.parseInt(role_cd) == (TableConstants.DUPLICATE_HSRP_ENTRY_CD) || Integer.parseInt(role_cd) == (TableConstants.DUPLICATE_HSRP_ENTRY_CD_VENDOR)) {
                setIsSearchPanelFlag(false);
                setIsSaveBtnVisible(true);
                setInputSelection(false);
            }
            setOwnerDetail(getOwnerImpl().getOwnerDetails(registrationNo, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd()));
            if (ownerDetail != null) {
                ownerDetail.setOwnerIdentity(null);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void fillHsrpDetails(String appl_no) {
        try {
            NewHsrpDo dupHsrpDobj = new HSRPRequestImpl().DuplicateHsrpInfo(appl_no);
            dobj = dupHsrpDobj;
            if (dupHsrpDobj != null) {
                String[] args = {registrationNo, Util.getUserStateCode(), "" + Util.getUserOffCode() + ""};
                String[] hsrpFlag = new HSRPRequestImpl().checkVehicleRegistration(args);
                dobj.getHsrpInfodobj().setHsrp_no_front(hsrpFlag[1]);
                dobj.getHsrpInfodobj().setHsrp_no_back(hsrpFlag[2]);
                if (dupHsrpDobj.getHsrpInfodobj().getHsrpReason().equalsIgnoreCase("Lost")) {
                    setIsHsrpReason(true);
                    setHsrpReason(dupHsrpDobj.getHsrpInfodobj().getHsrpReason());
                    reasonChangeListener();
                    setHsrpMenu(dupHsrpDobj.getHsrpInfodobj().getHsrp_flag());
                } else if (dupHsrpDobj.getHsrpInfodobj().getHsrpReason().equalsIgnoreCase("Damage")) {
                    setIsHsrpReason(false);
                    setHsrpReason(dupHsrpDobj.getHsrpInfodobj().getHsrpReason());
                    reasonChangeListener();
                    setHsrpMenu(dupHsrpDobj.getHsrpInfodobj().getHsrp_flag());
                } else if (dupHsrpDobj.getHsrpInfodobj().getHsrp_flag().equalsIgnoreCase("OB")) {
                    setIsFirPanel(false);
                    setIsHsrpReason(false);
                    hsrpMenuList.clear();
                    hsrpMenuList.add(new SelectItem("OB", "FIRST TIME OLD VEHICLE HSRP"));
                    setHsrpMenu(dupHsrpDobj.getHsrpInfodobj().getHsrp_flag());
                }
                setFirNo(dupHsrpDobj.getFirno());
                setPoliceStation(dupHsrpDobj.getPolicestation());
                setFirDate(dupHsrpDobj.getFirDate());
                prev_dobj = (NewHsrpDo) dobj.clone();
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    private String hsrpFormValidation() {
        String message = "";
        boolean flag = true;
        if (hsrpReason.toString().equalsIgnoreCase("-1")) {
            message = message + "Please Select the Reason <br/>";
        }
        if (hsrpMenu.toString().equals("-1")) {
            message = message + "Please Select the HSRP Flag <br/>";
        } else {
            if (hsrpReason.toString().equals("Lost")) {
                if (firNo.equalsIgnoreCase("")) {
                    message = message + "Enter the FIR Number <br/>";
                } else if (firDate == null) {
                    message = message + "Select the Fir date <br/>";
                } else if (policeStation.equalsIgnoreCase("")) {
                    message = message + "Enter the police Station <br/>";
                }
            }
        }
        return message;
    }

    public void resetForm() {
        setRegnNo("");
        setIsDisplayHsrpPanel(false);
        setHeaderTitle("");
        hsrpMenuList.clear();
        setIsFirPanel(false);
        hsrpResonList.clear();
        hsrpMenuList.clear();
        hsrpResonList.add(new SelectItem("Lost", "Lost"));
        hsrpResonList.add(new SelectItem("Damage", "Damage"));
        setIsOkBtn(true);
        setInputSelection(false);

    }

    public void reasonChangeListener() {
        hsrpMenuList.clear();
        if (hsrpReason.equalsIgnoreCase("Lost")) {
            hsrpMenuList.add(new SelectItem("DB", "LOST DUPLICATE BOTH HSRP"));
            hsrpMenuList.add(new SelectItem("DF", "LOST DUPLICATE FRONT HSRP"));
            hsrpMenuList.add(new SelectItem("DR", "LOST DUPLICATE REAR HSRP"));
            setIsFirPanel(true);
        } else if (hsrpReason.equalsIgnoreCase("Damage")) {
            hsrpMenuList.add(new SelectItem("DB", "DUPLICATE BOTH HSRP"));
            hsrpMenuList.add(new SelectItem("DF", "DUPLICATE FRONT HSRP"));
            hsrpMenuList.add(new SelectItem("DR", "DUPLICATE REAR HSRP"));
            setIsFirPanel(false);
        }


    }
    /*
     * Method user for check the existance of registered vechile no with chasis no
     */

    public String getApplicationDetails() {
        FacesMessage message = null;
        String state_cd = "";
        int off_cd = 0;
        String chasis_no = "";
        if (regnNo.toString().equalsIgnoreCase("")) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "please enter the registration No", "please enter the registration No");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return "";
        }

        try {
            setRegistrationNo(regnNo);
            List<Status_dobj> statusList = ServerUtil.applicationStatus(registrationNo);
            if (!statusList.isEmpty()) {
                for (Status_dobj dobj : statusList) {
                    if (dobj.getAction_cd() == TableConstants.DUPLICATE_HSRP_ENTRY_CD || dobj.getAction_cd() == TableConstants.DUPLICATE_HSRP_APPROVAL_CD
                            || dobj.getAction_cd() == TableConstants.DUPLICATE_HSRP_ENTRY_CD_VENDOR || dobj.getAction_cd() == TableConstants.DUPLICATE_HSRP_APPROVAL_CD_VENDOR) {
                        PrimeFaces.current().dialog().showMessageDynamic(
                                new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!",
                                "Vehicle is already Pending for Approval with Generated Application No (" + dobj.getAppl_no() + ") against Registration No (" + dobj.getRegn_no() + ")"));
                        return "";
                    }
                }
            }
            state_cd = (String) Util.getUserStateCode();
            off_cd = Util.getUserOffCode();
            setOwnerDetail(getOwnerImpl().getOwnerDetails(registrationNo, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd()));
            if (ownerDetail != null) {
                if (off_cd != ownerDetail.getOff_cd()) {
                    PrimeFaces.current().dialog().showMessageDynamic(
                            new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!",
                            "Vehicle is registered in " + ownerDetail.getOff_name() + ". Please made HSRP request in Registered office."));
                    return "";
                }
                ownerDetail.setOwnerIdentity(null);
                String[] args = {getRegistrationNo(), state_cd, "" + off_cd + ""};
                dobj = new HSRPRequestImpl().applicationDetails(args);
                if (dobj != null) {
                    if (dobj.getStatus().equalsIgnoreCase("PENDING")) {
                        setIsDisplayHsrpPanel(false);
                        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Already Application No.  " + dobj.getHsrpInfodobj().getAppl_no() + " ( " + dobj.getHsrpInfodobj().getHsrp_flag() + " ) is pending for HSRP Request.", "Already Application No. " + dobj.getHsrpInfodobj().getAppl_no() + "( " + dobj.getHsrpInfodobj().getHsrp_flag() + " ) is pending for HSRP Request");
                        FacesContext.getCurrentInstance().addMessage(null, message);
                    } else if (dobj.getStatus().equalsIgnoreCase("FlatFileGeneratedNotApproved")) {
                        setIsDisplayHsrpPanel(false);
                        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Flat File For Application No.  " + dobj.getHsrpInfodobj().getAppl_no() + " ( " + dobj.getHsrpInfodobj().getHsrp_flag() + " ) Generated But Not Exported By Vendor ", "Flat File For Application No.  " + dobj.getHsrpInfodobj().getAppl_no() + "( " + dobj.getHsrpInfodobj().getHsrp_flag() + " ) Generated But Not Exported By Vendor ");
                        FacesContext.getCurrentInstance().addMessage(null, message);
                    } else if (dobj.getStatus().equalsIgnoreCase("DUPLICATE")) {
                        setIsDisplayHsrpPanel(true);
                        hsrpResonList.clear();
                        setIsHsrpReason(true);
                        hsrpResonList.add(new SelectItem("Lost", "Lost"));
                        hsrpResonList.add(new SelectItem("Damage", "Damage"));
                        setHeaderTitle("HSRP ALL READY  ISSUED / APPLY FOR DUPLICATE DUE TO DAMAGE");
                    } else {
                        setIsHsrpReason(false);
                        hsrpMenuList.clear();
                        hsrpMenuList.add(new SelectItem("OB", "FIRST TIME OLD VEHICLE HSRP"));
                        setHeaderTitle("FIRST TIME OLD VEHICLE HSRP");
                        setIsDisplayHsrpPanel(true);
                    }
                    //  prev_dobj = (NewHsrpDo) dobj.clone();
                } else {
                    setIsDisplayHsrpPanel(false);
                    setIsFirPanel(false);
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "vehicle is not Registered in  RTO", "Vehicle is Not Registered in  RTO");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return "";
                }
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Vehicle Details Not Found", "Vehicle Details Not Found");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return "";
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        return "";
    }

    public String updateHsrpRequest() {
        FacesMessage message = null;
        boolean flag = false;
        try {
            String errrMsg = hsrpFormValidation();
            if (errrMsg.isEmpty() && dobj != null) {
                dobj.setRegn_no(registrationNo);
                dobj.setFirno(firNo);
                dobj.setFirDate(firDate);
                dobj.setPolicestation(policeStation);
                dobj.getHsrpInfodobj().setHsrpReason(hsrpReason.toString());
                dobj.getHsrpInfodobj().setHsrp_flag(hsrpMenu.toString());
                Status_dobj status_dobj = new Status_dobj();
                status_dobj.setRegn_no(registrationNo);
                status_dobj.setPur_cd(appl_details.getPur_cd());
                status_dobj.setAppl_no(appl_details.getAppl_no());
                status_dobj.setAction_cd(appl_details.getCurrent_action_cd());
                status_dobj.setState_cd(Util.getUserStateCode());
                status_dobj.setOff_cd(Util.getUserSeatOffCode());
                status_dobj.setEmp_cd(Integer.parseInt(Util.getEmpCode()));
                status_dobj.setOffice_remark("");
                status_dobj.setPublic_remark("");
                status_dobj.setFile_movement_type("F");
                status_dobj.setUser_id(Util.getUserId());
                status_dobj.setAppl_date(ServerUtil.getSystemDateInPostgres());
                flag = new HSRPRequestImpl().approveRequest(dobj, status_dobj);
                if (flag) {
                    setApplNoGenMessage("Application Has been Approved Successfully");
                    setIsOkBtn(true);
                } else {
                    setApplNoGenMessage("Due to technical error, Application is not Approved");
                    setIsOkBtn(false);
                }
            } else {
                if (dobj == null) {
                    errrMsg = errrMsg + " Something went wrong.";
                }
                setApplNoGenMessage(errrMsg);
                setIsOkBtn(false);
            }
            PrimeFaces.current().ajax().update("showdlgbox");
            PrimeFaces.current().executeScript("PF('successDialog').show()");
        } catch (VahanException ex) {
            setApplNoGenMessage(ex.getMessage());
            setIsOkBtn(false);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        return "";
    }

    /**
     * method user for back
     */
    public String exitOwner() {
        return "";
    }

    /**
     * method to re intialised the form
     */
    public void resetForm1() {
    }

    /**
     * @param reset the reset to set
     */
    public void setReset(String reset) {
        this.reset = reset;
    }

    /**
     * @return the registrationNo
     */
    public String getRegistrationNo() {
        return registrationNo;
    }

    /**
     * @param registrationNo the registrationNo to set
     */
    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    /**
     * @return the dobj
     */
    public NewHsrpDo getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(NewHsrpDo dobj) {
        this.dobj = dobj;
    }

    /**
     * @return the hsrpMenuList
     */
    public List getHsrpMenuList() {
        return hsrpMenuList;
    }

    /**
     * @param hsrpMenuList the hsrpMenuList to set
     */
    public void setHsrpMenuList(List hsrpMenuList) {
        this.hsrpMenuList = hsrpMenuList;
    }

    /**
     * @return the headerTitle
     */
    public String getHeaderTitle() {
        return headerTitle;
    }

    /**
     * @param headerTitle the headerTitle to set
     */
    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    /**
     * @return the isDisplayHsrpPanel
     */
    public boolean isIsDisplayHsrpPanel() {
        return isDisplayHsrpPanel;
    }

    /**
     * @param isDisplayHsrpPanel the isDisplayHsrpPanel to set
     */
    public void setIsDisplayHsrpPanel(boolean isDisplayHsrpPanel) {
        this.isDisplayHsrpPanel = isDisplayHsrpPanel;
    }

    /**
     * @return the hsrpMenu
     */
    public String getHsrpMenu() {
        return hsrpMenu;
    }

    /**
     * @param hsrpMenu the hsrpMenu to set
     */
    public void setHsrpMenu(String hsrpMenu) {
        this.hsrpMenu = hsrpMenu;
    }

    /**
     * @return the firNo
     */
    public String getFirNo() {
        return firNo;
    }

    /**
     * @param firNo the firNo to set
     */
    public void setFirNo(String firNo) {
        this.firNo = firNo;
    }

    /**
     * @return the policeStation
     */
    public String getPoliceStation() {
        return policeStation;
    }

    /**
     * @param policeStation the policeStation to set
     */
    public void setPoliceStation(String policeStation) {
        this.policeStation = policeStation;
    }

    /**
     * @return the firDate
     */
    public Date getFirDate() {
        return firDate;
    }

    /**
     * @param firDate the firDate to set
     */
    public void setFirDate(Date firDate) {
        this.firDate = firDate;
    }

    /**
     * @return the ownerDetail
     */
    public OwnerDetailsDobj getOwnerDetail() {
        return ownerDetail;
    }

    /**
     * @param ownerDetail the ownerDetail to set
     */
    public void setOwnerDetail(OwnerDetailsDobj ownerDetail) {
        this.ownerDetail = ownerDetail;
    }

    /**
     * @return the ownerImpl
     */
    public OwnerImpl getOwnerImpl() {
        return ownerImpl;
    }

    /**
     * @param ownerImpl the ownerImpl to set
     */
    public void setOwnerImpl(OwnerImpl ownerImpl) {
        this.ownerImpl = ownerImpl;
    }

    /**
     * @return the isSearchPanelFlag
     */
    public boolean isIsSearchPanelFlag() {
        return isSearchPanelFlag;
    }

    /**
     * @param isSearchPanelFlag the isSearchPanelFlag to set
     */
    public void setIsSearchPanelFlag(boolean isSearchPanelFlag) {
        this.isSearchPanelFlag = isSearchPanelFlag;
    }

    /**
     * @return the applicationNo
     */
    public String getApplicationNo() {
        return applicationNo;
    }

    /**
     * @param applicationNo the applicationNo to set
     */
    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }

    /**
     * @return the isApplicationFlag
     */
    public boolean isIsApplicationFlag() {
        return isApplicationFlag;
    }

    /**
     * @param isApplicationFlag the isApplicationFlag to set
     */
    public void setIsApplicationFlag(boolean isApplicationFlag) {
        this.isApplicationFlag = isApplicationFlag;
    }

    /**
     * @return the isSaveOrUpdate
     */
    public boolean isIsSaveOrUpdate() {
        return isSaveOrUpdate;
    }

    /**
     * @param isSaveOrUpdate the isSaveOrUpdate to set
     */
    public void setIsSaveOrUpdate(boolean isSaveOrUpdate) {
        this.isSaveOrUpdate = isSaveOrUpdate;
    }

    public void saveHsrpRequestData() {
        FacesMessage message = null;
        String applno = "";
        String returnUrl = "";
        try {
            String errrMsg = hsrpFormValidation();
            if (errrMsg.isEmpty() && dobj != null) {
                dobj.setRegn_no(registrationNo);
                dobj.getHsrpInfodobj().setHsrpReason(hsrpReason);
                dobj.getHsrpInfodobj().setHsrp_flag(hsrpMenu.toString());
                dobj.setFirno(firNo);
                dobj.setFirDate(firDate);
                dobj.setPolicestation(policeStation);
                Status_dobj status_dobj = new Status_dobj();
                status_dobj.setRegn_no(registrationNo);
                status_dobj.setPur_cd(appl_details.getPur_cd());
                status_dobj.setAppl_no(appl_details.getAppl_no());
                status_dobj.setAction_cd(appl_details.getCurrent_action_cd());
                status_dobj.setState_cd(Util.getUserStateCode());
                status_dobj.setOff_cd(Util.getUserSeatOffCode());
                status_dobj.setEmp_cd(Integer.parseInt(Util.getEmpCode()));
                status_dobj.setOffice_remark("");
                status_dobj.setPublic_remark("");
                status_dobj.setFile_movement_type("F");
                status_dobj.setUser_id(Util.getUserId());
                status_dobj.setAppl_date(ServerUtil.getSystemDateInPostgres());
                applno = new HSRPRequestImpl().saveHsrpRequest(dobj, status_dobj);
                if (applno.length() > 0) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Application Number is Generted Successfully.Application Number is : " + applno, "Application Number is Generted Successfully.Application Number is : " + applno);
                    setApplNoGenMessage("Application Number is Generted Successfully.Application Number is : " + applno);
                    setIsSaveBtnVisible(false);
                    setIsOkBtn(true);
                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Request Not Saved", "Request Not Saved");
                    setApplNoGenMessage("Technical Error");
                    setIsOkBtn(false);
                }
            } else {
                if (dobj == null) {
                    errrMsg = errrMsg + " Something went wrong.";
                }
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, errrMsg, errrMsg);
                setApplNoGenMessage(errrMsg);
                setIsOkBtn(false);
            }
        } catch (VahanException ve) {
            setApplNoGenMessage(ve.getMessage());
            setIsOkBtn(false);
        } catch (Exception ex) {
            setApplNoGenMessage("Something went wrong. Please try again.");
            setIsOkBtn(false);
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }

        PrimeFaces.current().ajax().update("showdlgbox");
        PrimeFaces.current().executeScript("PF('successDialog').show()");
    }

    public String returnPage() {
        String returnUrl = "";
        if (isOkBtn) {
            returnUrl = "home";
        } else {
            returnUrl = "";
        }
        return returnUrl;
    }

    @Override
    public String save() {
        FacesMessage message = null;
        String applno = "";
        String rtnlocation = "";
        try {
            String errrMsg = hsrpFormValidation();
            if (errrMsg.isEmpty()) {
                dobj.setRegn_no(registrationNo);
                dobj.setFirno(firNo);
                dobj.setFirDate(firDate);
                dobj.setPolicestation(policeStation);
                dobj.getHsrpInfodobj().setHsrpReason(hsrpReason.toString());
                dobj.getHsrpInfodobj().setHsrp_flag(hsrpMenu.toString());
                Status_dobj status_dobj = new Status_dobj();
                status_dobj.setRegn_no(registrationNo);
                status_dobj.setPur_cd(appl_details.getPur_cd());
                status_dobj.setAppl_no(appl_details.getAppl_no());
                status_dobj.setAction_cd(appl_details.getCurrent_action_cd());
                status_dobj.setState_cd(Util.getUserStateCode());
                status_dobj.setOff_cd(Util.getUserSeatOffCode());
                status_dobj.setEmp_cd(Integer.parseInt(Util.getEmpCode()));
                status_dobj.setOffice_remark("");
                status_dobj.setPublic_remark("");
                status_dobj.setFile_movement_type("F");
                status_dobj.setUser_id(Util.getUserId());
                status_dobj.setAppl_date(ServerUtil.getSystemDateInPostgres());
                new HSRPRequestImpl().saveHsrpData(dobj, status_dobj, ComparisonBeanImpl.changedDataContents(compareChanges()));
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, errrMsg, errrMsg);
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        return rtnlocation;
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        if (prev_dobj == null) {
            return compBeanList;
        }
        compBeanList.clear();
        Compare("fir", prev_dobj.getFirno(), firNo, compBeanList);
        Compare("Hsrp reason", prev_dobj.getHsrpInfodobj().getHsrpReason(), hsrpReason, compBeanList);
        Compare("Hsrp flag", prev_dobj.getHsrpInfodobj().getHsrp_flag(), hsrpMenu, compBeanList);
        Compare("FirDate", prev_dobj.getFirDate(), firDate, compBeanList);
        Compare("policestation", prev_dobj.getPolicestation(), policeStation, compBeanList);
        return compBeanList;
    }

    @Override
    public String saveAndMoveFile() {
        FacesMessage message = null;
        String applno = "";
        String rtnlocation = "";
        try {
            if (!hsrpMenu.toString().equalsIgnoreCase("-1")) {
                dobj.setRegn_no(registrationNo);
                dobj.setFirno(firNo);
                dobj.setFirDate(firDate);
                dobj.setPolicestation(policeStation);
                dobj.getHsrpInfodobj().setHsrp_flag(hsrpMenu.toString());
                Status_dobj status_dobj = new Status_dobj();
                status_dobj.setRegn_no(registrationNo);
                status_dobj.setAppl_no(appl_details.getAppl_no());
                status_dobj.setPur_cd(appl_details.getPur_cd());
                status_dobj.setAction_cd(appl_details.getCurrent_action_cd());
                status_dobj.setState_cd(Util.getUserStateCode());
                status_dobj.setOff_cd(Util.getUserSeatOffCode());
                status_dobj.setEmp_cd(Integer.parseInt(Util.getEmpCode()));
                status_dobj.setStatus("N");
                status_dobj.setOffice_remark("");
                status_dobj.setPublic_remark("");
                status_dobj.setFile_movement_type("F");
                status_dobj.setUser_id(Util.getUserId());
                status_dobj.setAppl_date(ServerUtil.getSystemDateInPostgres());
                rtnlocation = new HSRPRequestImpl().saveAndMoveHsrpData(dobj, status_dobj, ComparisonBeanImpl.changedDataContents(compareChanges()));
                return "seatwork";
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        return rtnlocation;
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
    }

    /**
     * @return the IsFirPanel
     */
    public boolean isIsFirPanel() {
        return IsFirPanel;
    }

    /**
     * @param IsFirPanel the IsFirPanel to set
     */
    public void setIsFirPanel(boolean IsFirPanel) {
        this.IsFirPanel = IsFirPanel;
    }

    /**
     * @return the regnNo
     */
    public String getRegnNo() {
        return regnNo;
    }

    /**
     * @param regnNo the regnNo to set
     */
    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    /**
     * @return the isSaveBtnVisible
     */
    public boolean isIsSaveBtnVisible() {
        return isSaveBtnVisible;
    }

    /**
     * @param isSaveBtnVisible the isSaveBtnVisible to set
     */
    public void setIsSaveBtnVisible(boolean isSaveBtnVisible) {
        this.isSaveBtnVisible = isSaveBtnVisible;
    }

    /**
     * @return the applNoGenMessage
     */
    public String getApplNoGenMessage() {
        return applNoGenMessage;
    }

    /**
     * @param applNoGenMessage the applNoGenMessage to set
     */
    public void setApplNoGenMessage(String applNoGenMessage) {
        this.applNoGenMessage = applNoGenMessage;
    }

    /**
     * @return the maxFirDate
     */
    public Date getMaxFirDate() {
        return maxFirDate;
    }

    /**
     * @param maxFirDate the maxFirDate to set
     */
    public void setMaxFirDate(Date maxFirDate) {
        this.maxFirDate = maxFirDate;
    }

    /**
     * @return the hsrpReason
     */
    public String getHsrpReason() {
        return hsrpReason;
    }

    /**
     * @param hsrpReason the hsrpReason to set
     */
    public void setHsrpReason(String hsrpReason) {
        this.hsrpReason = hsrpReason;
    }

    /**
     * @return the hsrpResonList
     */
    public List getHsrpResonList() {
        return hsrpResonList;
    }

    /**
     * @param hsrpResonList the hsrpResonList to set
     */
    public void setHsrpResonList(List hsrpResonList) {
        this.hsrpResonList = hsrpResonList;
    }

    /**
     * @return the isHsrpReason
     */
    public boolean isIsHsrpReason() {
        return isHsrpReason;
    }

    /**
     * @param isHsrpReason the isHsrpReason to set
     */
    public void setIsHsrpReason(boolean isHsrpReason) {
        this.isHsrpReason = isHsrpReason;
    }

    /**
     * @return the isOkBtn
     */
    public boolean isIsOkBtn() {
        return isOkBtn;
    }

    /**
     * @param isOkBtn the isOkBtn to set
     */
    public void setIsOkBtn(boolean isOkBtn) {
        this.isOkBtn = isOkBtn;
    }

    /**
     * @return the inputSelection
     */
    public boolean isInputSelection() {
        return inputSelection;
    }

    /**
     * @param inputSelection the inputSelection to set
     */
    public void setInputSelection(boolean inputSelection) {
        this.inputSelection = inputSelection;
    }
}
