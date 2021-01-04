/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.commoncarrier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.dobj.commoncarrier.DetailCommonCarrierDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.commoncarrier.DetailCommonCarrierImpl;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.common.FileMovementAbstractApplBean;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.server.CommonUtils;
import org.apache.commons.lang3.time.DateUtils;
import static nic.vahan.server.ServerUtil.Compare;

/**
 *
 * @author acer
 */
@ManagedBean(name = "detailCommonCarrierBean", eager = true)
@ViewScoped
public class DetailCommonCarrierBean extends FileMovementAbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(DetailCommonCarrierBean.class);
    private DetailCommonCarrierDobj detailCommonCarrierDobj = new DetailCommonCarrierDobj();
    private DetailCommonCarrierDobj detailCommonCarrierDobjPrev = null;
    private final List<DetailCommonCarrierDobj> newApplicationSectionsAddedList;
    private final List<DetailCommonCarrierDobj> listBranchDetailsFor;
    private List<DetailCommonCarrierDobj> listBranchDetailsForPrev = null;
    private final List<DetailCommonCarrierDobj> newOwnerDetailsList;
    private final List<DetailCommonCarrierDobj> listBranchDetailsBackUp;
    private final List<DetailCommonCarrierDobj> changeAddedList;
    private ArrayList<ComparisonBean> compBeanList;
    private boolean visibleMainBranchPanel;
    private boolean visibleBravchDetailPanel;
    private String chooseRenewDuplicateOption;
    private boolean disableOwnerName;
    private boolean disableApplNo;
    private boolean disablePersonAuthorised;
    private boolean disableContactNumber;
    private boolean disableAddressMainOffice;
    private boolean disableValidFrom;
    private boolean disableValidTo;
    private boolean disableDateComm;
    private boolean disableAddMore;
    private boolean disableClearField;
    private boolean disableRemoveRecord;
    private boolean visibleSave;
    private String app_no_msg;
    private boolean renderFileMovement;
    private boolean disableSave;
    private boolean disableSavePopup;
    private String applNo;
    private Date currentDate = new Date();
    private String callPageRedirect;
    private String myNo;
    private String postSaveMessage;
    private List list_c_state;
    private boolean notValid;
    private boolean disableSaveButton;
    private boolean renderExemptionOD;
    private boolean renderRemoveRow;
    private boolean visibleRenewDuplicate = false;
    private boolean optionCCRenualDuplicate = true;

    public DetailCommonCarrierBean() {

        newApplicationSectionsAddedList = new ArrayList<>();
        listBranchDetailsFor = new ArrayList<>();
        newOwnerDetailsList = new ArrayList<>();
        listBranchDetailsBackUp = new ArrayList<>();
        compBeanList = new ArrayList<>();
        changeAddedList = new ArrayList<>();
        list_c_state = new ArrayList();
        renderExemptionOD = false;
    }

    @PostConstruct
    public void init() {
        DetailCommonCarrierImpl detailCommonCarrierImpl = new DetailCommonCarrierImpl();
        try {
            if (appl_details == null
                    || appl_details.getCurrent_state_cd() == null
                    || appl_details.getCurrent_off_cd() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, null));
                return;
            }
            if (appl_details != null) {
                detailCommonCarrierDobj = detailCommonCarrierImpl.set_CC_appl_db_to_dobj(appl_details.getAppl_no());
                List listForBanksDetail = detailCommonCarrierImpl.set_CC_branch_db_to_list(appl_details.getAppl_no());
                if (detailCommonCarrierDobj == null) {
                    detailCommonCarrierDobj = DetailCommonCarrierImpl.set_CC_renw_dup_dt(appl_details.getRegn_no());
                    listForBanksDetail = DetailCommonCarrierImpl.set_CC_renw_dup_branch_list(appl_details.getRegn_no());
                }
                if (detailCommonCarrierDobj == null) {
                    detailCommonCarrierDobj = new DetailCommonCarrierDobj();
                    detailCommonCarrierDobj.setStateCd(appl_details.getCurrent_state_cd());
                    detailCommonCarrierDobj.setOffCd(appl_details.getCurrent_off_cd());
                    setValidFrom();
                    setValidUpto();
                    visibleBravchDetailPanel = false;
                    visibleMainBranchPanel = true;
                    list_c_state = MasterTableFiller.getStateList();
                    this.disableValidFrom = false;
                    renderRemoveRow = true;
                    chooseRenewDuplicateOption = "New_CC_Certificate";
                } else {
                    Calendar c = Calendar.getInstance();
                    c.setTime(currentDate);
                    detailCommonCarrierDobjPrev = (DetailCommonCarrierDobj) detailCommonCarrierDobj.clone();
                    this.disableApplNo = true;
                    this.disableValidFrom = true;
                    this.disableValidTo = true;
                    this.disableClearField = true;
                    this.disableRemoveRecord = true;
                    this.disableAddMore = false;
                    renderFileMovement = true;
                    this.disableDateComm = true;
                    visibleBravchDetailPanel = true;
                    optionCCRenualDuplicate = false;
                    if (!listForBanksDetail.isEmpty()) {
                        listBranchDetailsFor.addAll(listForBanksDetail);
                        listBranchDetailsForPrev = new ArrayList<>(listBranchDetailsFor);
                    }
                    if (appl_details.getCurrent_action_cd() == TableConstants.VM_COMMON_CARRIER_APPL_VERIFY) {
                        list_c_state = MasterTableFiller.getStateList();
                        visibleMainBranchPanel = true;
                        renderRemoveRow = true;
                        if (appl_details.getPur_cd() == TableConstants.VM_TRANSACTION_CARRIER_DUPLICATE) {
                            visibleMainBranchPanel = false;
                            this.disableOwnerName = true;
                            this.disableAddressMainOffice = true;
                            this.disablePersonAuthorised = true;
                            this.disableContactNumber = true;
                            visibleBravchDetailPanel = true;
                            renderRemoveRow = false;
                        }
                    } else if (appl_details.getCurrent_action_cd() == TableConstants.VM_COMMON_CARRIER_APPL_APPROVE) {
                        visibleMainBranchPanel = false;
                        this.disableOwnerName = true;
                        this.disableAddressMainOffice = true;
                        this.disablePersonAuthorised = true;
                        this.disableContactNumber = true;
                        if (appl_details.getPur_cd() != TableConstants.VM_TRANSACTION_CARRIER_DUPLICATE) {
                            setValidFrom();
                            setValidUpto();
                        }
                    }
                }
                disableSave = true;
            }
        } catch (VahanException vme) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vme.getMessage(), null));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, null));
        }
    }

    private void setValidFrom() {
        detailCommonCarrierDobj.setValidFrom(currentDate);
        this.disableValidFrom = true;
    }

    private void setValidUpto() {
        Date dt = null;
        dt = DateUtils.addYears(detailCommonCarrierDobj.getValidFrom(), 1);
        dt = DateUtils.addDays(dt, -1);
        this.detailCommonCarrierDobj.setValidUpto(dt);
        this.disableValidTo = true;
    }

    public void setValidUptoListner(AjaxBehaviorEvent event) {
        Date dt = null;
        dt = DateUtils.addYears(detailCommonCarrierDobj.getValidFrom(), 1);
        this.detailCommonCarrierDobj.setValidUpto(dt);
        this.disableValidTo = true;
    }

    public void cStateListener(AjaxBehaviorEvent event) {
        this.disableAddMore = true;
    }

    @Override
    public String save() {
        String returnLocation = "";
        mapDobj();
        try {
            List<ComparisonBean> compareChanges = compareChanges();
            if (!compareChanges.isEmpty() || detailCommonCarrierDobjPrev == null) { //save only when data is really changed by user
                DetailCommonCarrierImpl.save(detailCommonCarrierDobj, detailCommonCarrierDobjPrev, listBranchDetailsForPrev, listBranchDetailsFor, chooseRenewDuplicateOption, ComparisonBeanImpl.changedDataContents(compareChanges()));
            }
            if (appl_details.getCurrent_action_cd() == TableConstants.VM_COMMON_CARRIER_APPL_ENTRY) {
                this.postSaveMessage = "Application Number :" + detailCommonCarrierDobj.getApplNo();
                showDialogBox(postSaveMessage);
            }
        } catch (VahanException vme) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vme.getMessage(), vme.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-File Could Not Save and Move Due to Technical Error in Database", "Error-File Could Not Save and Move Due to Technical Error in Database"));
        }
        return returnLocation;
    }

    public void setReset() {
        detailCommonCarrierDobj = new DetailCommonCarrierDobj();
        this.listBranchDetailsFor.clear();
        if (visibleBravchDetailPanel == true) {
            visibleBravchDetailPanel = false;
        }
    }

    public void setDetailsBranch() {
        this.detailCommonCarrierDobj.setBranchAddress("");
        this.detailCommonCarrierDobj.setBranchCenter("");
        this.detailCommonCarrierDobj.setBranchDateCommencement(null);
        this.detailCommonCarrierDobj.setBranchLocation("");
        if (visibleBravchDetailPanel == false) {
            this.disableAddMore = false;
        }
    }

    public void showDialogBox(String app_no) {
        setApplNo(app_no);
        callPageRedirect = "ApplTime";
        if (!CommonUtils.isNullOrBlank(getApplNo())) {
            setApp_no_msg("Application Number is not genrated");
            PrimeFaces.current().executeScript("PF('appNum').show();");
        } else {
            setApp_no_msg("Application Number generated :" + getApplNo() + ". Please note down the Application No for future reference.");
            PrimeFaces.current().executeScript("PF('appNum').show();");
        }
    }

    private void mapDobj() {
        DetailCommonCarrierDobj detailCommonCarrierDobjFrom = new DetailCommonCarrierDobj();
        detailCommonCarrierDobjFrom.setApplNo(detailCommonCarrierDobj.getApplNo());
        detailCommonCarrierDobjFrom.setAddContact(detailCommonCarrierDobj.getAddContact());
        detailCommonCarrierDobjFrom.setOrganijationName(detailCommonCarrierDobj.getOrganijationName());
        detailCommonCarrierDobjFrom.setPersonAuthorised(detailCommonCarrierDobj.getPersonAuthorised());
        detailCommonCarrierDobjFrom.setAddress(detailCommonCarrierDobj.getAddress());
        detailCommonCarrierDobjFrom.setValidFrom(detailCommonCarrierDobj.getValidFrom());
        detailCommonCarrierDobjFrom.setValidUpto(detailCommonCarrierDobj.getValidUpto());
        detailCommonCarrierDobjFrom.setContactNumber(detailCommonCarrierDobj.getContactNumber());
        detailCommonCarrierDobj.setStateCd(Util.getUserStateCode());
        detailCommonCarrierDobj.setOffCd(Util.getUserOffCode());
    }

    private void fillBranchDetails() {
        DetailCommonCarrierDobj detailCommonCarrierDobjFrom = new DetailCommonCarrierDobj();
        detailCommonCarrierDobjFrom.setApplNo(detailCommonCarrierDobj.getApplNo());
        detailCommonCarrierDobjFrom.setBranchAddress(detailCommonCarrierDobj.getBranchAddress());
        detailCommonCarrierDobjFrom.setBranchCenter(detailCommonCarrierDobj.getBranchCenter());
        detailCommonCarrierDobjFrom.setBranchDateCommencement(detailCommonCarrierDobj.getBranchDateCommencement());
        String stateTo = detailCommonCarrierDobj.getBranchLocation();
        stateTo = MasterTableFiller.getStateDescrByStateCode(stateTo);
        detailCommonCarrierDobjFrom.setBranchLocation(stateTo);
        detailCommonCarrierDobjFrom.setStateCd(Util.getUserStateCode());
        detailCommonCarrierDobjFrom.setOffCd(Util.getUserOffCode());
        this.listBranchDetailsFor.add(detailCommonCarrierDobjFrom);
    }

    public FacesMessage[] addNewSectionsToApplication() {
        FacesMessage[] facesMessages = new FacesMessage[4];
        notValid = false;
        int i = 0;
        if (CommonUtils.isNullOrBlank(detailCommonCarrierDobj.getBranchAddress())) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please Provide Address Of Bank branch", "Please Provide Address Of Bank branch");
            facesMessages[i++] = message;
            notValid = true;
        }
        if (CommonUtils.isNullOrBlank(detailCommonCarrierDobj.getBranchCenter())) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please Provide Godown/Hub Center", "Please Provide Godown/Hub Center");
            facesMessages[i++] = message;
            notValid = true;
        }
        if (detailCommonCarrierDobj.getBranchDateCommencement() == null) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please Provide Date of Commencement", "Please Provide Date of Commencement");
            facesMessages[i++] = message;
            notValid = true;
        }
        if (CommonUtils.isNullOrBlank(detailCommonCarrierDobj.getBranchLocation())) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please Provide Location of Branch", "Please Provide Location of Branch");
            facesMessages[i++] = message;
            notValid = true;
        }
        if (!notValid) {
            fillBranchDetails();
            if (!listBranchDetailsFor.isEmpty()) {
                visibleBravchDetailPanel = true;
                visibleSave = true;
                this.disableDateComm = true;
            }
            detailCommonCarrierDobj.setBranchLocation("");
            detailCommonCarrierDobj.setBranchAddress("");
            detailCommonCarrierDobj.setBranchCenter("");
            detailCommonCarrierDobj.setBranchDateCommencement(null);
            return new FacesMessage[0];
        } else {
            return facesMessages;
        }
    }

    @Override
    public ArrayList<ComparisonBean> compareChanges() {
        if (detailCommonCarrierDobjPrev == null) {
            return compBeanList;
        }
        compBeanList.clear();
        Compare("org_name", detailCommonCarrierDobjPrev.getOrganijationName(), detailCommonCarrierDobj.getOrganijationName(), compBeanList);
        Compare("authorized_person", detailCommonCarrierDobjPrev.getPersonAuthorised(), detailCommonCarrierDobj.getPersonAuthorised(), compBeanList);
        Compare("address", detailCommonCarrierDobjPrev.getAddress(), detailCommonCarrierDobj.getAddress(), compBeanList);
        Compare("contact_no", detailCommonCarrierDobjPrev.getContactNumber(), detailCommonCarrierDobj.getContactNumber(), compBeanList);
        if (listBranchDetailsForPrev != null && !listBranchDetailsForPrev.isEmpty()) {
            Compare("Branch Locations", listBranchDetailsForPrev.toString(), listBranchDetailsFor.toString(), compBeanList);
        }
        return getCompBeanList();
    }

    @Override
    public String saveAndMoveFile() {

        String returnLocation = "seatwork";
        String fileMoveReturn = "";
        try {
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setPur_cd(appl_details.getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            status.setCurrent_role(appl_details.getCurrent_role());
            status.setAction_cd(appl_details.getCurrent_action_cd());
            mapDobjForChangeData();
            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                    || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)) {
                if (!appl_details.getRegn_no().isEmpty() && appl_details.getRegn_no() != null) {
                    detailCommonCarrierDobj.setRegnNo(appl_details.getRegn_no());
                }
                status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                fileMoveReturn = DetailCommonCarrierImpl.saveAndMoveFile(status, detailCommonCarrierDobj, detailCommonCarrierDobjPrev, listBranchDetailsForPrev, listBranchDetailsFor, ComparisonBeanImpl.changedDataContents(compareChanges()));
                if (!CommonUtils.isNullOrBlank(fileMoveReturn)) {
                    if (appl_details.getPur_cd() == TableConstants.VM_TRANSACTION_CARRIER_REGN) {
                        this.postSaveMessage = "Registration Succesfull " + fileMoveReturn + ".";
                    } else if (appl_details.getPur_cd() == TableConstants.VM_TRANSACTION_CARRIER_RENEWAL) {
                        this.postSaveMessage = "Renewal Succesfull " + fileMoveReturn + ".";
                    } else if (appl_details.getPur_cd() == TableConstants.VM_TRANSACTION_CARRIER_DUPLICATE) {
                        this.postSaveMessage = "Duplicate Succesfull " + fileMoveReturn + ".";
                    }
                    postSaveOperation();
                    setMyNo(myNo);
                    PrimeFaces.current().executeScript("PF('regnNum').show();");
                    returnLocation = "";
                }
            }
        } catch (VahanException vme) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vme.getMessage(), null));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, null));
        }
        return returnLocation;
    }

    private void postSaveOperation() {
        setDisableSaveButton(true);
    }

    public void removeInListListener(AjaxBehaviorEvent event) {
        if (this.listBranchDetailsFor.size() > 0) {
            for (Object dobjObj : this.listBranchDetailsFor) {
                DetailCommonCarrierDobj Dobj = (DetailCommonCarrierDobj) dobjObj;
                if (!Dobj.isIsRowRemove()) {
                    this.listBranchDetailsBackUp.add(Dobj);
                }
            }
            this.listBranchDetailsFor.clear();
            this.listBranchDetailsFor.addAll(listBranchDetailsBackUp);
            if (this.listBranchDetailsFor.size() <= 0) {
                visibleBravchDetailPanel = false;
                visibleSave = false;
            }
            this.listBranchDetailsBackUp.clear();
        }
    }

    private void mapDobjForChangeData() {

        DetailCommonCarrierDobj detailCommonCarrierDobjFrom = new DetailCommonCarrierDobj();
        detailCommonCarrierDobjFrom.setApplNo(detailCommonCarrierDobj.getApplNo());
        detailCommonCarrierDobjFrom.setOrganijationName(detailCommonCarrierDobj.getOrganijationName());
        detailCommonCarrierDobjFrom.setPersonAuthorised(detailCommonCarrierDobj.getPersonAuthorised());
        detailCommonCarrierDobjFrom.setAddress(detailCommonCarrierDobj.getAddress());
        detailCommonCarrierDobjFrom.setContactNumber(detailCommonCarrierDobj.getContactNumber());
        detailCommonCarrierDobjFrom.setStateCd(Util.getUserStateCode());
        detailCommonCarrierDobjFrom.setOffCd(Util.getUserOffCode());
        this.changeAddedList.add(detailCommonCarrierDobjFrom);
    }

    public void disableAddMoreListener(AjaxBehaviorEvent event) {
        this.disableAddMore = true;
    }

    public void fillRenewDuplicateOption(AjaxBehaviorEvent event) {
        chooseRenewDuplicateOption = detailCommonCarrierDobj.getDuplicateRenewCert();
        if (chooseRenewDuplicateOption.equals("Renew_CC_Certificate") || chooseRenewDuplicateOption.equals("Duplicate_CC_Certificate")) {
            visibleRenewDuplicate = true;
            visibleMainBranchPanel = false;
            this.disableOwnerName = true;
            this.disableAddressMainOffice = true;
            this.disablePersonAuthorised = true;
            this.disableContactNumber = true;
            this.disableDateComm = true;
        } else {
            detailCommonCarrierDobj = new DetailCommonCarrierDobj();
            setChooseRenewDuplicateOption("New_CC_Certificate");
            visibleRenewDuplicate = false;
            this.disableOwnerName = false;
            this.disableAddressMainOffice = false;
            this.disablePersonAuthorised = false;
            this.disableContactNumber = false;
            visibleBravchDetailPanel = false;
            visibleMainBranchPanel = true;
            this.listBranchDetailsFor.clear();
            setValidFrom();
            setValidUpto();
        }
    }

    public void fillRenewDuplicateDetailstoDobj(AjaxBehaviorEvent event) {
        try {
            if (detailCommonCarrierDobj.getRegnNo() == null || detailCommonCarrierDobj.getRegnNo().isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration No Can not be Blank"));
            }
            if (chooseRenewDuplicateOption.equals("Renew_CC_Certificate") || chooseRenewDuplicateOption.equals("Duplicate_CC_Certificate")) {
                detailCommonCarrierDobj = DetailCommonCarrierImpl.set_CC_renw_dup_dt(detailCommonCarrierDobj.getRegnNo());
                if (detailCommonCarrierDobj != null) {
                    List listForBanksDetail = DetailCommonCarrierImpl.set_CC_renw_dup_branch_list(detailCommonCarrierDobj.getRegnNo());
                    if (!listForBanksDetail.isEmpty()) {
                        listBranchDetailsFor.addAll(listForBanksDetail);
                        visibleMainBranchPanel = false;
                        this.disableOwnerName = true;
                        this.disableAddressMainOffice = true;
                        this.disablePersonAuthorised = true;
                        this.disableContactNumber = true;
                        visibleBravchDetailPanel = true;
                        renderRemoveRow = false;
                        this.disableValidFrom = true;
                        this.disableValidTo = true;
                        visibleSave = true;
                    }
                    if (chooseRenewDuplicateOption.equals("Renew_CC_Certificate")) {
                        if (detailCommonCarrierDobj.getValidUpto().after(currentDate)) {
                            visibleSave = false;
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Common Carrier Certificate is not expired."));
                            return;
                        }
                    }
                }
            }
        } catch (VahanException vme) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vme.getMessage(), null));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, null));
        }
    }

    public DetailCommonCarrierDobj getDetailCommonCarrierDobj() {
        return detailCommonCarrierDobj;
    }

    public void setDetailCommonCarrierDobj(DetailCommonCarrierDobj detailCommonCarrierDobj) {
        this.detailCommonCarrierDobj = detailCommonCarrierDobj;
    }

    public boolean isDisableOwnerName() {
        return disableOwnerName;
    }

    public void setDisableOwnerName(boolean disableOwnerName) {
        this.disableOwnerName = disableOwnerName;
    }

    public boolean isVisibleBravchDetailPanel() {
        return visibleBravchDetailPanel;
    }

    public List<DetailCommonCarrierDobj> getNewApplicationSectionsAddedList() {
        return newApplicationSectionsAddedList;
    }

    public List<DetailCommonCarrierDobj> getNewOwnerDetailsList() {
        return newOwnerDetailsList;
    }

    public List<DetailCommonCarrierDobj> getListBranchDetailsFor() {
        return listBranchDetailsFor;
    }

    public boolean isDisableApplNo() {
        return disableApplNo;
    }

    public void setDisableApplNo(boolean disableApplNo) {
        this.disableApplNo = disableApplNo;
    }

    public String getApp_no_msg() {
        return app_no_msg;
    }

    public void setApp_no_msg(String app_no_msg) {
        this.app_no_msg = app_no_msg;
    }

    public boolean isRenderFileMovement() {
        return renderFileMovement;
    }

    public void setRenderFileMovement(boolean renderFileMovement) {
        this.renderFileMovement = renderFileMovement;
    }

    public boolean isDisableSave() {
        return disableSave;
    }

    public void setDisableSave(boolean disableSave) {
        this.disableSave = disableSave;
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setCompBeanList(ArrayList<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    public ArrayList<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isDisableSavePopup() {
        return disableSavePopup;
    }

    public void setDisableSavePopup(boolean disableSavePopup) {
        this.disableSavePopup = disableSavePopup;
    }

    public boolean isDisablePersonAuthorised() {
        return disablePersonAuthorised;
    }

    public void setDisablePersonAuthorised(boolean disablePersonAuthorised) {
        this.disablePersonAuthorised = disablePersonAuthorised;
    }

    public boolean isDisableContactNumber() {
        return disableContactNumber;
    }

    public void setDisableContactNumber(boolean disableContactNumber) {
        this.disableContactNumber = disableContactNumber;
    }

    public boolean isDisableAddressMainOffice() {
        return disableAddressMainOffice;
    }

    public void setDisableAddressMainOffice(boolean disableAddressMainOffice) {
        this.disableAddressMainOffice = disableAddressMainOffice;
    }

    public boolean isDisableValidFrom() {
        return disableValidFrom;
    }

    public void setDisableValidFrom(boolean disableValidFrom) {
        this.disableValidFrom = disableValidFrom;
    }

    public boolean isDisableValidTo() {
        return disableValidTo;
    }

    public void setDisableValidTo(boolean disableValidTo) {
        this.disableValidTo = disableValidTo;
    }

    public String getApplNo() {
        return applNo;
    }

    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public String getCallPageRedirect() {
        return callPageRedirect;
    }

    public void setCallPageRedirect(String callPageRedirect) {
        this.callPageRedirect = callPageRedirect;
    }

    public String getMyNo() {
        return myNo;
    }

    public void setMyNo(String myNo) {
        this.myNo = myNo;
    }

    public boolean isDisableAddMore() {
        return disableAddMore;
    }

    public void setDisableAddMore(boolean disableAddMore) {
        this.disableAddMore = disableAddMore;
    }

    public boolean isDisableClearField() {
        return disableClearField;
    }

    public void setDisableClearField(boolean disableClearField) {
        this.disableClearField = disableClearField;
    }

    public boolean isDisableRemoveRecord() {
        return disableRemoveRecord;
    }

    public void setDisableRemoveRecord(boolean disableRemoveRecord) {
        this.disableRemoveRecord = disableRemoveRecord;
    }

    public boolean isVisibleMainBranchPanel() {
        return visibleMainBranchPanel;
    }

    public void setVisibleMainBranchPanel(boolean visibleMainBranchPanel) {
        this.visibleMainBranchPanel = visibleMainBranchPanel;
    }

    public boolean isVisibleSave() {
        return visibleSave;
    }

    public void setVisibleSave(boolean visibleSave) {
        this.visibleSave = visibleSave;
    }

    public boolean isDisableDateComm() {
        return disableDateComm;
    }

    public void setDisableDateComm(boolean disableDateComm) {
        this.disableDateComm = disableDateComm;
    }

    public String getPostSaveMessage() {
        return postSaveMessage;
    }

    public List getList_c_state() {
        return list_c_state;
    }

    public void setList_c_state(List list_c_state) {
        this.list_c_state = list_c_state;
    }

    public boolean isDisableSaveButton() {
        return disableSaveButton;
    }

    public void setDisableSaveButton(boolean disableSaveButton) {
        this.disableSaveButton = disableSaveButton;
    }

    public boolean isRenderExemptionOD() {
        return renderExemptionOD;
    }

    public void setRenderExemptionOD(boolean renderExemptionOD) {
        this.renderExemptionOD = renderExemptionOD;
    }

    public boolean isRenderRemoveRow() {
        return renderRemoveRow;
    }

    public void setRenderRemoveRow(boolean renderRemoveRow) {
        this.renderRemoveRow = renderRemoveRow;
    }

    public List<DetailCommonCarrierDobj> getListBranchDetailsForPrev() {
        return listBranchDetailsForPrev;
    }

    public void setListBranchDetailsForPrev(List<DetailCommonCarrierDobj> listBranchDetailsForPrev) {
        this.listBranchDetailsForPrev = listBranchDetailsForPrev;
    }

    public boolean isVisibleRenewDuplicate() {
        return visibleRenewDuplicate;
    }

    public void setVisibleRenewDuplicate(boolean visibleRenewDuplicate) {
        this.visibleRenewDuplicate = visibleRenewDuplicate;
    }

    public void setChooseRenewDuplicateOption(String chooseRenewDuplicateOption) {
        this.chooseRenewDuplicateOption = chooseRenewDuplicateOption;
    }

    public boolean isOptionCCRenualDuplicate() {
        return optionCCRenualDuplicate;
    }

    public void setOptionCCRenualDuplicate(boolean optionCCRenualDuplicate) {
        this.optionCCRenualDuplicate = optionCCRenualDuplicate;
    }
}
