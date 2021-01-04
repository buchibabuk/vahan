/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import nic.vahan.form.dobj.AuctionDobj;
import nic.vahan.form.dobj.BlackListedVehicleDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.PermitDetailDobj;
import nic.vahan.form.impl.AuctionImpl;
import nic.vahan.form.impl.BlackListedVehicleImpl;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.OtherStateVehImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.PermitDetailImpl;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;
//import org.primefaces.context.RequestContext;
import org.primefaces.PrimeFaces;
import javax.faces.event.AjaxBehaviorEvent;
import nic.vahan.server.CommonUtils;

@ManagedBean(name = "auctionBean")
@ViewScoped
public class AuctionBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static Logger LOGGER = Logger.getLogger(AuctionBean.class);
    private Owner_dobj ownerDobj;
    private ArrayList list_vh_class = new ArrayList();
    private ArrayList list_vm_catg = new ArrayList();
    private ArrayList list_maker_model = new ArrayList();
    private AuctionDobj auctionDobj = new AuctionDobj();
    private OwnerDetailsDobj ownerDetail;
    private OwnerIdentificationDobj ownerIdentificationPrev = null;
    private boolean disable = false;
    private boolean renderOwnerPanel = false;
    private AuctionDobj auction_dobj_prv = null;
    private List<ComparisonBean> prevChangedDataList;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private boolean auctionDetail = false;
    private String applNoGenMessage;
    private boolean renderGeneratedApplNo;
    private boolean blackListedVehicle;
    private String searchByValue;
    private boolean regnNoPanelVisibility;
    private boolean chassisNoPanelVisibility;
    private boolean renderAuctionPanel;
    private List stateList;
    private List officeList;
    private boolean disableUnRegisteredVehPanel;
    private boolean renderUnRegisteredVehPanel = true;

    public AuctionBean() {
        try {
            AuctionImpl impl = new AuctionImpl();
            OwnerImpl ownerImpl = new OwnerImpl();

            String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
            getList_vh_class().clear();
            for (int i = 0; i < data.length; i++) {
                getList_vh_class().add(new SelectItem(data[i][0], data[i][1]));
            }
            data = MasterTableFiller.masterTables.VM_VCH_CATG.getData();
            for (int i = 0; i < data.length; i++) {
                getList_vm_catg().add(new SelectItem(data[i][0], data[i][1]));
            }
            data = MasterTableFiller.masterTables.VM_MODELS.getData();
            for (int i = 0; i < data.length; i++) {
                getList_maker_model().add(new SelectItem(data[i][1], data[i][2]));
            }

            AuctionDobj auctionDbj = impl.getAuctionDetails(getAppl_details().getRegn_no(), getAppl_details().getAppl_no());
            if (auctionDbj != null) {
                auction_dobj_prv = (AuctionDobj) auctionDbj.clone();//for holding current dobj for using in the comparison.
                auctionDetail = true;
                setAuctionDobjDetail(auctionDbj);

                if (auctionDbj.getRegnNo() != null && !auctionDbj.getRegnNo().equals("NEW") && auctionDbj.getAuctionBy().equals("R")) {
                    this.setOwnerDetails();
                    this.setOwnerIdentityDetails();
                    this.setOwnerDobj();
                    auctionDobj.setPurchaseDate(ownerDetail.getPurchase_date());
                } else {
                    setSearchByValue("chassisNo");
                    if (auctionDbj.getRegnNo() != null && auctionDbj.getRegnNo().equals("NEW") && auctionDbj.getStateCdFrom().equals("NA") && auctionDbj.getOffCdFrom() == 0) {
                        renderUnRegisteredVehPanel = false;
                    } else if (auctionDbj.getRegnNo() != null && !auctionDbj.getRegnNo().equals("NEW") && !auctionDbj.getStateCdFrom().equals("NA") && auctionDbj.getOffCdFrom() != 0) {
                        disableUnRegisteredVehPanel = true;
                        stateList = MasterTableFiller.getStateList();
                        officeList = new ArrayList();
                        officeList = MasterTableFiller.getOfficeList(getAuctionDobj().getStateCdFrom());
                    }
                    setDisappPrint(false);

                }
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(auctionDbj.getApplNo(), TableConstants.VM_TRANSACTION_MAST_AUCTION);
                if (getAppl_details().getCurrent_action_cd() == TableConstants.TM_ROLE_AUCTION_APPROVAL) {
                    auctionDobj.setDisableAuctionPanel(true);
                }
                setRenderAuctionPanel(true);
                this.disclaimerForBlackListedVehicle(auctionDbj.getRegnNo(), ownerDetail.getChasi_no());
            }
        } catch (VahanException vex) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", vex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Something went wrong, Please try again..."));
        }
    }

    public void showDetailAction() {
        AuctionImpl impl = new AuctionImpl();
        BlackListedVehicleImpl obj = new BlackListedVehicleImpl();
        try {
            if (searchByValue != null && searchByValue.equals("regnNo")) {
                if (getAuction_dobj_prv() == null) {
                    List<Status_dobj> statusList = ServerUtil.applicationStatus(getAuctionDobj().getRegnNo());
                    if (!statusList.isEmpty()) {
                        PrimeFaces.current().dialog().showMessageDynamic(
                                new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!",
                                        "Vehicle is already Pending for Approval with Generated Application No (" + statusList.get(0).getAppl_no() + ") against Registration No (" + statusList.get(0).getRegn_no() + ")"));
                        return;
                    }
                }

                int countOfChassis = impl.countOfChassisNoAgainstRegnNo(getAuctionDobj().getRegnNo());
                if (countOfChassis > 1) {
                    throw new VahanException("Chassis Number already Exist!!!.");
                }

                this.setOwnerDetails();
                if (ownerDetail.getVehType() == TableConstants.VM_VEHTYPE_TRANSPORT) {
                    PermitDetailDobj permitDetailDobj = PermitDetailImpl.getPermitdetailsFromRegnNo(getAuctionDobj().getRegnNo());
                    if (permitDetailDobj != null) {
                        throw new VahanException("Auction can't be done due to Permit is not Surrender !!!");
                    }
                }
                this.setOwnerIdentityDetails();
                this.setOwnerDobj();
                auctionDobj.setPurchaseDate(ownerDetail.getPurchase_date());

                //Check for blacklisted
                BlackListedVehicleDobj blacklistedStatus = this.disclaimerForBlackListedVehicle(getAuctionDobj().getRegnNo(), getOwnerDetail().getChasi_no());

                if (blacklistedStatus != null) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Info!", "Vehicle is Blacklisted due to " + blacklistedStatus.getComplainDesc() + ""));
                }
                //Check for Vehicle NOC
                if (getOwnerDetail().getStatus().equalsIgnoreCase(TableConstants.VT_NOC_ISSUE_STATUS)) {
                    OtherStateVehImpl vehImpl = new OtherStateVehImpl();
                    String issuedMess = vehImpl.getOtherStateDetails(getAuctionDobj().getRegnNo());
                    if (issuedMess != null) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Info!", "NOC is Taken for this Vehicle"));
                    }
                }
            } else if (searchByValue != null && searchByValue.equals("chassisNo")) {
                String chasiNoExistMessage = impl.checkChassisNoExist(getAuctionDobj().getChasiNo(), TableConstants.VM_REGN_TYPE_CONFISCATED_AUCTION);
                if (chasiNoExistMessage != null) {
                    if (chasiNoExistMessage.contains("Appl.No:")) {
                        chasiNoExistMessage = chasiNoExistMessage + " or Dispose application";
                        throw new VahanException(chasiNoExistMessage);
                    } else {
                        chasiNoExistMessage = chasiNoExistMessage + " or do Auction through Registration Number.";
                        throw new VahanException(chasiNoExistMessage);
                    }
                }

                Owner_dobj tempOwnerDobj = new OwnerImpl().set_Owner_appl_db_to_dobj(null, null, getAuctionDobj().getChasiNo(), TableConstants.VM_TRANSACTION_MAST_TEMP_REG);
                if (tempOwnerDobj != null) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Info!", "Temporary is Taken for this Vehicle"));
                }

                String blacklistMess = obj.checkChassisNoForBlackList(getAuctionDobj().getChasiNo());
                if (blacklistMess != null) {
                    if (Util.getUserStateCode() != null && Util.getUserStateCode().equals("BR")) {
                        blackListedVehicle = true;
                    }
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Info!", blacklistMess));
                }
            }

            if (searchByValue != null && (searchByValue.equals("chassisNo") || searchByValue.equals("regnNo"))) {
                boolean auctionDetailsExist = impl.isAuctionDetailsExist(getAuctionDobj().getRegnNo(), getAuctionDobj().getChasiNo());
                if (auctionDetailsExist) {
                    throw new VahanException("Auction is already done for this vehicle.");
                }
                setRenderAuctionPanel(true);
            }
        } catch (VahanException vex) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", vex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Something went wrong, Please try again..."));
        }

    }

    public BlackListedVehicleDobj disclaimerForBlackListedVehicle(String regnNo, String chasiNo) throws VahanException {
        BlackListedVehicleImpl obj = new BlackListedVehicleImpl();
        BlackListedVehicleDobj blacklistedStatus = obj.getBlacklistedVehicleDetails(regnNo, chasiNo);
        if (blacklistedStatus != null) {
            if (Util.getUserStateCode() != null && Util.getUserStateCode().equals("BR") && blacklistedStatus.getComplain_type() != TableConstants.BLCompoundingAmtCode) {
                blackListedVehicle = true;
            }
        }
        return blacklistedStatus;
    }

    public void setOwnerDetails() throws VahanException {
        List<OwnerDetailsDobj> ownerDetailsDobjList = new OwnerImpl().getOwnerDetailsList(getAuctionDobj().getRegnNo().trim(), null);
        if (ownerDetailsDobjList == null || ownerDetailsDobjList.isEmpty()) {
            throw new VahanException("Invalid Registration Number or Registration No not found in the Record");
        }
        if (ownerDetailsDobjList.size() == 1) {
            setOwnerDetail(ownerDetailsDobjList.get(0));
        } else if (ownerDetailsDobjList.size() >= 2) {
            throw new VahanException("Duplicate Record Found!!!");
        }
    }

    public void setOwnerIdentityDetails() throws CloneNotSupportedException {
        if (getOwnerDetail().getOwnerIdentity() != null) {
            getOwnerDetail().getOwnerIdentity().setFlag(true);
            getOwnerDetail().getOwnerIdentity().setMobileNoEditable(true);
            if (getOwnerDetail().getOwnerIdentity().getMobile_no() == null) {
                getOwnerDetail().getOwnerIdentity().setMobile_no(0l);
            }
            setOwnerIdentificationPrev((OwnerIdentificationDobj) getOwnerDetail().getOwnerIdentity().clone());
        }
    }

    public void setOwnerDobj() throws VahanException {
        Owner_dobj owDobj = new OwnerImpl().getOwnerDobj(getOwnerDetail());
        setOwnerDobj(owDobj);
        if (getOwnerDobj() != null) {
            setDisable(true);
            setRenderOwnerPanel(true);
        } else {
            throw new VahanException("Vehicle Details Not Found!!!");
        }
    }

    public void saveAuctionData() {
        String applNo = "";
        FacesMessage message = null;
        AuctionImpl impl = new AuctionImpl();
        try {
            Status_dobj status_dobj = new Status_dobj();
            if (searchByValue != null && searchByValue.equals("regnNo")) {
                status_dobj.setRegn_no(getAuctionDobj().getRegnNo());
                getAuctionDobj().setChasiNo(getOwnerDetail().getChasi_no());
                getAuctionDobj().setStateCdFrom(getOwnerDetail().getState_cd());
                getAuctionDobj().setOffCdFrom(getOwnerDetail().getOff_cd());
                getAuctionDobj().setAuctionBy("R");
            } else if (searchByValue != null && searchByValue.equals("chassisNo")) {
                getAuctionDobj().setAuctionBy("C");
                if (!CommonUtils.isNullOrBlank(getAuctionDobj().getRegnNo())) {
                    this.mandatoryFieldsInCaseOfUnregisteredVehWhenPutRegnNo();
                    status_dobj.setRegn_no(getAuctionDobj().getRegnNo());
                } else {
                    status_dobj.setRegn_no("NEW");
                    getAuctionDobj().setRegnNo("NEW");
                }
            } else {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
            status_dobj.setPur_cd(TableConstants.VM_TRANSACTION_MAST_AUCTION);
            status_dobj.setFlow_slno(1);//initial flow serial no.
            status_dobj.setFile_movement_slno(1);//initial file movement serial no.
            status_dobj.setState_cd(Util.getUserStateCode());
            status_dobj.setOff_cd(Util.getUserSeatOffCode());
            status_dobj.setEmp_cd(Long.parseLong(Util.getEmpCode()));
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

            applNo = impl.insertAuctionDetails(status_dobj, getAuctionDobj());
            if (!applNo.isEmpty()) {
                setRenderGeneratedApplNo(true);
                setApplNoGenMessage("Application generated successfully. Application No. :" + applNo);
                PrimeFaces.current().executeScript("PF('successDialog').show()");
            } else {
                throw new VahanException("File Could Not Save Due to Technical Error in Database!!!");
            }
        } catch (VahanException vex) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", vex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "File Could Not Save and Move Due to Technical Error in Database"));
        }
    }

    @Override
    public String saveAndMoveFile() {
        String return_location = "";
        try {
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(getAppl_details().getAppl_dt());
            status.setAppl_no(getAppl_details().getAppl_no());
            status.setPur_cd(getAppl_details().getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            status.setCurrent_role(appl_details.getCurrent_action_cd());

            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                    || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)
                    || getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                AuctionImpl auctionImpl = new AuctionImpl();
                auctionImpl.update_Auction_Status(auctionDobj, auction_dobj_prv, status, ComparisonBeanImpl.changedDataContents(compareChanges()), getOwnerDobj());
                if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING) && auctionDobj.getRegnNo() != null && !auctionDobj.getRegnNo().equals("NEW") && auctionDobj.getAuctionBy().equals("R")) {
                    return_location = disapprovalPrint();
                } else {
                    return_location = "seatwork";
                }
            }
        } catch (VahanException vex) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", vex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "File Could Not Save and Move Due to Technical Error in Database"));
        }
        return return_location;
    }

    @Override
    public String save() {
        String return_location = "";
        try {
            List<ComparisonBean> compareChanges = compareChanges();

            if (!compareChanges.isEmpty() || auction_dobj_prv == null) { //save only when data is really changed by user
                new AuctionImpl().makeChangeAuction(auctionDobj, ComparisonBeanImpl.changedDataContents(compareChanges));
            }

            return_location = "seatwork";

        } catch (VahanException vex) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", vex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "File Could Not Save and Move Due to Technical Error in Database"));
        }
        return return_location;
    }

    public List<ComparisonBean> compareChanges() {

        if (auction_dobj_prv == null) {
            return getCompBeanList();
        }
        getCompBeanList().clear();
        Compare("Auction Date", auction_dobj_prv.getAuctionDate(), auctionDobj.getAuctionDate(), getCompBeanList());
        Compare("FIR Date", auction_dobj_prv.getFirDate(), auctionDobj.getFirDate(), getCompBeanList());
        Compare("FIR No", auction_dobj_prv.getFirNo(), auctionDobj.getFirNo(), getCompBeanList());
        Compare("REASON", auction_dobj_prv.getReason(), auctionDobj.getReason(), getCompBeanList());
        Compare("ORDER NO", auction_dobj_prv.getOrderNo(), auctionDobj.getOrderNo(), getCompBeanList());
        Compare("STATE CD FROM", auction_dobj_prv.getStateCdFrom(), auctionDobj.getStateCdFrom(), getCompBeanList());
        Compare("OFF CD FROM ", auction_dobj_prv.getOffCdFrom(), auctionDobj.getOffCdFrom(), getCompBeanList());
        Compare("REGN NO", auction_dobj_prv.getRegnNo(), auctionDobj.getRegnNo(), getCompBeanList());
        Compare("REGN DATE", auction_dobj_prv.getRegnDt(), auctionDobj.getRegnDt(), getCompBeanList());
        return getCompBeanList();
    }

    public void setAuctionDobjDetail(AuctionDobj acDbj) {
        auctionDobj.setStateCd(acDbj.getStateCd());
        auctionDobj.setOffCd(acDbj.getOffCd());
        auctionDobj.setApplNo(acDbj.getApplNo());
        auctionDobj.setRegnNo(acDbj.getRegnNo());
        auctionDobj.setChasiNo(acDbj.getChasiNo());
        auctionDobj.setStateCdFrom(acDbj.getStateCdFrom());
        auctionDobj.setOffCdFrom(acDbj.getOffCdFrom());
        auctionDobj.setAuctionDate(acDbj.getAuctionDate());
        auctionDobj.setFirNo(acDbj.getFirNo());
        auctionDobj.setFirDate(acDbj.getFirDate());
        auctionDobj.setReason(acDbj.getReason());
        auctionDobj.setOrderNo(acDbj.getOrderNo());
        auctionDobj.setRegnDt(acDbj.getRegnDt());
        auctionDobj.setAuctionBy(acDbj.getAuctionBy());
    }

    public void doneAuctionBy() throws VahanException, Exception {
        if (searchByValue != null) {
            if (!searchByValue.equals("-1")) {
                if (searchByValue.equals("regnNo")) {
                    regnNoPanelVisibility = true;
                    chassisNoPanelVisibility = false;
                } else if (searchByValue.equals("chassisNo")) {
                    chassisNoPanelVisibility = true;
                    regnNoPanelVisibility = false;
                    stateList = MasterTableFiller.getStateList();
                    officeList = new ArrayList();
                }
                setRenderAuctionPanel(false);
            } else {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Select Search Type", "Please Select Search Type");
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
        }
    }

    public void updateRtoFromStateListener(AjaxBehaviorEvent even) {
        officeList.clear();
        officeList = MasterTableFiller.getOfficeList(getAuctionDobj().getStateCdFrom());
        if (Util.getUserStateCode().equalsIgnoreCase(getAuctionDobj().getStateCdFrom())) {
            Iterator ite = officeList.iterator();
            while (ite.hasNext()) {
                SelectItem obj = (SelectItem) ite.next();
                if (Integer.parseInt(obj.getValue().toString()) == Util.getSelectedSeat().getOff_cd()) {
                    officeList.remove(obj);
                    break;
                }
            }
        }
    }

    public void mandatoryFieldsInCaseOfUnregisteredVehWhenPutRegnNo() throws VahanException {
        String pattern = "[a-zA-Z0-9]+";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(getAuctionDobj().getRegnNo());

        if (getAuctionDobj().getRegnNo().length() > 10) {
            throw new VahanException("Allowed Maximum Length of Registration Number is 10");
        }

        if (getAuctionDobj().getRegnNo().length() < 4) {
            throw new VahanException("Allowed Minimum Length of Registration Number is 4");
        }
        if (!m.matches()) {
            throw new VahanException("Invalid Registration number,Please enter valid Registration Number");
        }

        if (getAuctionDobj().getRegnDt() == null) {
            throw new VahanException("Please select Registration Date!!!");
        }

        if (getAuctionDobj().getStateCdFrom().equals("NA")) {
            throw new VahanException("Please select State From!!!");
        }

        if (getAuctionDobj().getOffCdFrom() == 0) {
            throw new VahanException("Please select Office From!!!");
        }
    }

    /**
     * @return the ownerDobj
     */
    public Owner_dobj getOwnerDobj() {
        return ownerDobj;
    }

    /**
     * @param ownerDobj the ownerDobj to set
     */
    public void setOwnerDobj(Owner_dobj ownerDobj) {
        this.ownerDobj = ownerDobj;
    }

    /**
     * @return the list_vh_class
     */
    public ArrayList getList_vh_class() {
        return list_vh_class;
    }

    /**
     * @param list_vh_class the list_vh_class to set
     */
    public void setList_vh_class(ArrayList list_vh_class) {
        this.list_vh_class = list_vh_class;
    }

    /**
     * @return the list_vm_catg
     */
    public ArrayList getList_vm_catg() {
        return list_vm_catg;
    }

    /**
     * @param list_vm_catg the list_vm_catg to set
     */
    public void setList_vm_catg(ArrayList list_vm_catg) {
        this.list_vm_catg = list_vm_catg;
    }

    /**
     * @return the list_maker_model
     */
    public ArrayList getList_maker_model() {
        return list_maker_model;
    }

    /**
     * @param list_maker_model the list_maker_model to set
     */
    public void setList_maker_model(ArrayList list_maker_model) {
        this.list_maker_model = list_maker_model;
    }

    /**
     * @return the LOGGER
     */
    public static Logger getLOGGER() {
        return LOGGER;
    }

    /**
     * @param aLOGGER the LOGGER to set
     */
    public static void setLOGGER(Logger aLOGGER) {
        LOGGER = aLOGGER;
    }

    /**
     * @return the auctionDobj
     */
    public AuctionDobj getAuctionDobj() {
        return auctionDobj;
    }

    /**
     * @param auctionDobj the auctionDobj to set
     */
    public void setAuctionDobj(AuctionDobj auctionDobj) {
        this.auctionDobj = auctionDobj;
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
     * @return the ownerIdentificationPrev
     */
    public OwnerIdentificationDobj getOwnerIdentificationPrev() {
        return ownerIdentificationPrev;
    }

    /**
     * @param ownerIdentificationPrev the ownerIdentificationPrev to set
     */
    public void setOwnerIdentificationPrev(OwnerIdentificationDobj ownerIdentificationPrev) {
        this.ownerIdentificationPrev = ownerIdentificationPrev;
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
     * @return the auction_dobj_prv
     */
    public AuctionDobj getAuction_dobj_prv() {
        return auction_dobj_prv;
    }

    /**
     * @param auction_dobj_prv the auction_dobj_prv to set
     */
    public void setAuction_dobj_prv(AuctionDobj auction_dobj_prv) {
        this.auction_dobj_prv = auction_dobj_prv;
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
     * @param compBeanList the compBeanList to set
     */
    public void setCompBeanList(List<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    /**
     * @return the compBeanList
     */
    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the auctionDetail
     */
    public boolean isAuctionDetail() {
        return auctionDetail;
    }

    /**
     * @param auctionDetail the auctionDetail to set
     */
    public void setAuctionDetail(boolean auctionDetail) {
        this.auctionDetail = auctionDetail;
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
     * @return the renderGeneratedApplNo
     */
    public boolean isRenderGeneratedApplNo() {
        return renderGeneratedApplNo;
    }

    /**
     * @param renderGeneratedApplNo the renderGeneratedApplNo to set
     */
    public void setRenderGeneratedApplNo(boolean renderGeneratedApplNo) {
        this.renderGeneratedApplNo = renderGeneratedApplNo;
    }

    /**
     * @return the blackListedVehicle
     */
    public boolean isBlackListedVehicle() {
        return blackListedVehicle;
    }

    /**
     * @param blackListedVehicle the blackListedVehicle to set
     */
    public void setBlackListedVehicle(boolean blackListedVehicle) {
        this.blackListedVehicle = blackListedVehicle;
    }

    /**
     * @return the renderOwnerPanel
     */
    public boolean isRenderOwnerPanel() {
        return renderOwnerPanel;
    }

    /**
     * @param renderOwnerPanel the renderOwnerPanel to set
     */
    public void setRenderOwnerPanel(boolean renderOwnerPanel) {
        this.renderOwnerPanel = renderOwnerPanel;
    }

    /**
     * @return the searchByValue
     */
    public String getSearchByValue() {
        return searchByValue;
    }

    /**
     * @param searchByValue the searchByValue to set
     */
    public void setSearchByValue(String searchByValue) {
        this.searchByValue = searchByValue;
    }

    /**
     * @return the regnNoPanelVisibility
     */
    public boolean isRegnNoPanelVisibility() {
        return regnNoPanelVisibility;
    }

    /**
     * @param regnNoPanelVisibility the regnNoPanelVisibility to set
     */
    public void setRegnNoPanelVisibility(boolean regnNoPanelVisibility) {
        this.regnNoPanelVisibility = regnNoPanelVisibility;
    }

    /**
     * @return the chassisNoPanelVisibility
     */
    public boolean isChassisNoPanelVisibility() {
        return chassisNoPanelVisibility;
    }

    /**
     * @param chassisNoPanelVisibility the chassisNoPanelVisibility to set
     */
    public void setChassisNoPanelVisibility(boolean chassisNoPanelVisibility) {
        this.chassisNoPanelVisibility = chassisNoPanelVisibility;
    }

    /**
     * @return the renderAuctionPanel
     */
    public boolean isRenderAuctionPanel() {
        return renderAuctionPanel;
    }

    /**
     * @param renderAuctionPanel the renderAuctionPanel to set
     */
    public void setRenderAuctionPanel(boolean renderAuctionPanel) {
        this.renderAuctionPanel = renderAuctionPanel;
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
     * @return the disableUnRegisteredVehPanel
     */
    public boolean isDisableUnRegisteredVehPanel() {
        return disableUnRegisteredVehPanel;
    }

    /**
     * @param disableUnRegisteredVehPanel the disableUnRegisteredVehPanel to set
     */
    public void setDisableUnRegisteredVehPanel(boolean disableUnRegisteredVehPanel) {
        this.disableUnRegisteredVehPanel = disableUnRegisteredVehPanel;
    }

    /**
     * @return the renderUnRegisteredVehPanel
     */
    public boolean isRenderUnRegisteredVehPanel() {
        return renderUnRegisteredVehPanel;
    }

    /**
     * @param renderUnRegisteredVehPanel the renderUnRegisteredVehPanel to set
     */
    public void setRenderUnRegisteredVehPanel(boolean renderUnRegisteredVehPanel) {
        this.renderUnRegisteredVehPanel = renderUnRegisteredVehPanel;
    }
}
