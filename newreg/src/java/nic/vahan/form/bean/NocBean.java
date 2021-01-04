/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.State;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.NocDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.ToDobj;
import nic.vahan.form.dobj.configuration.TmConfigurationApplInwardDobj;
import nic.vahan.form.impl.ApplicationInwardImpl;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.InsImpl;
import nic.vahan.form.impl.NocImpl;
import nic.vahan.form.impl.ToImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import nic.vahan.services.insurance.InsuranceDetailService;
import nic.vahan.services.insurance.dobj.InsuranceInfoDobj;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "noc_bean")
@ViewScoped
public class NocBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(NocBean.class);
    private List state_list;
    private List office_list;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private List<ComparisonBean> prevChangedDataList;
    private NocDobj noc_dobj_prv;
    private Date today = new Date();
    private NocDobj noc_dobj = new NocDobj();
    private Owner_dobj onwer_dobj = null;
    private boolean cancellation_panel_visibility = false;
    private boolean comp_disable = false;
    private int pur_cd;
    private boolean renderSaveButton;
    private boolean renderprintbutton;
    @ManagedProperty(value = "#{ins_bean}")
    private InsBean ins_bean;
    private boolean isNOCInwardwithHPC = false;
    private String nocHPCDetails;
    private boolean renderInsPanel = true;
    private boolean fancyRetention;
    private String selectedFancyRetnetion;
    private String vahanMessages = null;
    private String header = "NOC Vehicle Details";
    //add
    private List purposeCodeList = null;
    private int select_purt_cd;
    private boolean rendered_own_name;
    String[][] noc_reason = null;
    String permitStatus = "";
    boolean toWithNOC = false;
    private OwnerDetailsDobj ownerDetail;

    //end
    public NocBean() {

        if (appl_details == null || appl_details.getCurrent_state_cd() == null || appl_details.getCurrent_off_cd() == 0) {
            vahanMessages = "Something went wrong, Please try again...";
            return;
        }
        renderSaveButton = true;
        renderprintbutton = false;
        state_list = new ArrayList();
        office_list = new ArrayList();
        state_list = MasterTableFiller.getStateList();
    }

    //Added By Pawan
    @PostConstruct
    public void init() {
        InsDobj ins_dobj_ret = null;
        InsDobj ins_dobj_retVA = null;
        //add
        rendered_own_name = false;
        purposeCodeList = new ArrayList();
        //end
        try {

            NocImpl noc_Impl = new NocImpl();
            if (getAppl_details() != null) {
                if (appl_details.getOwnerDetailsDobj() == null) {
                    vahanMessages = "Owner Details not Found in the Database, Please Contact to the System Administrator";
                    return;
                }

                if (appl_details.getOwnerDetailsDobj() != null) {
                    ownerDetail = appl_details.getOwnerDetailsDobj();
                }

                if (ownerDetail.getOwnerIdentity() != null) {
                    ownerDetail.getOwnerIdentity().setFlag(true);
                    ownerDetail.getOwnerIdentity().setMobileNoEditable(true);
                    ownerDetail.getOwnerIdentity().setOwnerCatgEditable(true);
                }

                pur_cd = appl_details.getPur_cd();
                noc_dobj.setPur_cd(pur_cd);
                if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NOC_CANCEL) {
                    setRenderInsPanel(false);
                    if (NocImpl.validateAppOfNoc(getAppl_details().getRegn_no())) {
                        setCancellation_panel_visibility(true);
                        noc_dobj = noc_Impl.set_NOC_cancel_appl_db_to_dobj(getAppl_details().getAppl_no(), getAppl_details().getRegn_no());
                        if (noc_dobj == null) {
                            noc_dobj = new NocDobj();
                            noc_dobj.setAppl_no(getAppl_details().getAppl_no());
                            noc_dobj.setRegn_no(getAppl_details().getRegn_no());
                        } else {
                            noc_dobj.setAppl_no(getAppl_details().getAppl_no());
                            noc_dobj.setRegn_no(getAppl_details().getRegn_no());
                            noc_dobj_prv = (NocDobj) noc_dobj.clone();
                        }
                        prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                        setComp_disable(true);
                        //  setRendered_own_name(true);
                    } else {
                        setComp_disable(true);
                        noc_dobj = noc_Impl.set_NOC_cancel_appl_db_to_dobj(getAppl_details().getAppl_no(), getAppl_details().getRegn_no());
                        String stLabel = "";
                        String offCdLabel = "";
                        if (noc_dobj != null) {
                            State stateTo = MasterTableFiller.state.get(noc_dobj.getState_to());
                            stLabel = stateTo == null ? "Unknown State" : stateTo.getStateDescr();
                            offCdLabel = "Unknown Off Code";
                            if (stateTo != null) {
                                stLabel = stateTo.getStateDescr();
                                List<SelectItem> listOff = stateTo.getOffice();
                                for (SelectItem off : listOff) {
                                    if (off.getValue().equals(noc_dobj.getOff_to())) {
                                        offCdLabel = off.getLabel();
                                        break;
                                    }
                                }
                            }
                        }
                        vahanMessages = "Invalid Application for " + getAppl_details().getPur_desc() + ". Vehicle has NOC issued to state : " + stLabel + " Office: " + offCdLabel;
                    }
                } else {
                    TmConfigurationDobj tmConfig = Util.getTmConfiguration();
                    ToImpl to_Impl = new ToImpl();
                    ToDobj to_dobj = new ToDobj();
                    if (tmConfig != null) {
                        if (tmConfig.isTo_retention() && (to_Impl.isFancyNo(appl_details.getRegn_no()) || tmConfig.isTo_retention_for_all_regn())) {
                            fancyRetention = true;
                        }
                        if (fancyRetention && to_Impl.isSurrenderRetention(appl_details.getAppl_no())) {
                            selectedFancyRetnetion = "YES";
                        } else if (fancyRetention && appl_details.getCurrent_role() != TableConstants.TM_ROLE_ENTRY) {
                            selectedFancyRetnetion = "NO";
                        }
                    }

                    setRenderInsPanel(true);
                    boolean NOCInwardwithHPC = noc_Impl.isNOCInwardwithHPC(getAppl_details().getAppl_no(), getAppl_details().getRegn_no(), noc_dobj, appl_details.getCurrent_state_cd(), appl_details.getCurrent_off_cd());
                    if (NOCInwardwithHPC) {
                        setIsNOCInwardwithHPC(true);
                        setNocHPCDetails(noc_dobj.getHptDetails());
                    }

                    noc_dobj = noc_Impl.set_NOC_appl_db_to_dobj(getAppl_details().getAppl_no(), null);
                    List<Status_dobj> statusList = ServerUtil.applicationStatus(getAppl_details().getRegn_no(), getAppl_details().getAppl_no(), getAppl_details().getCurrent_state_cd());
                    if (!statusList.isEmpty() && statusList.size() > 1) {
                        for (int i = 0; i < statusList.size(); i++) {
                            if (statusList.get(i).getPur_cd() == TableConstants.VM_TRANSACTION_MAST_TO) {
                                ApplicationInwardImpl inwardImpl = new ApplicationInwardImpl();
                                TmConfigurationApplInwardDobj tmConfigurationApplInwardDobj = inwardImpl.getApplInwardAnywhereInStateConfig(getAppl_details().getCurrent_state_cd());
                                if (tmConfigurationApplInwardDobj != null) {
                                    toWithNOC = tmConfigurationApplInwardDobj.isCheck_for_to_with_noc();
                                }
                            }
                        }
                    }
                    if (noc_dobj == null) {
                        noc_dobj = new NocDobj();
                        noc_dobj.setAppl_no(getAppl_details().getAppl_no());
                        noc_dobj.setRegn_no(getAppl_details().getRegn_no());
                        if (toWithNOC) {
                            to_dobj = to_Impl.set_TO_appl_db_to_dobj(getAppl_details().getAppl_no());
                            if (to_dobj != null) {
                                noc_dobj.setState_to(to_dobj.getStateCode());
                                noc_dobj.setOff_to(to_dobj.getOffCode());
                                noc_dobj.setNew_own_name(to_dobj.getOwner_name());
                            }
                        }
                        if (appl_details.getOwnerDetailsDobj().getC_state().equalsIgnoreCase("RJ")) {
                            noc_dobj.setMin_dt(new SimpleDateFormat("yyyy-MM-dd").parse(appl_details.getOwnerDetailsDobj().getPurchase_dt()));
                        } else {
                            noc_dobj.setMin_dt(DateUtil.parseDate(DateUtil.getCurrentDate()));
                        }
                    } else {
                        if (appl_details.getOwnerDetailsDobj().getC_state().equalsIgnoreCase("RJ") && noc_dobj.getNoc_dt() == null) {
                            noc_dobj.setMin_dt(new SimpleDateFormat("yyyy-MM-dd").parse(appl_details.getOwnerDetailsDobj().getPurchase_dt()));
                            noc_dobj.setNoc_dt(new Date());
                        }
                        if (noc_dobj.getNoc_dt() != null && noc_dobj.getNoc_dt().before(noc_dobj.getMin_dt())) {
                            noc_dobj.setMin_dt(noc_dobj.getNoc_dt());
                        }
                        noc_dobj_prv = (NocDobj) noc_dobj.clone();
                        if (appl_details.getCurrent_state_cd().equalsIgnoreCase(noc_dobj.getState_to())) {
                            header = "Clearance Certificate(CC)";
                        }
                    }
                    prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                    ins_bean.setAppl_no(appl_details.getAppl_no());
                    ins_bean.setRegn_no(appl_details.getRegn_no());
                    ins_bean.setPur_cd(appl_details.getPur_cd());

                    ins_dobj_ret = InsImpl.set_ins_dtls_db_to_dobj(appl_details.getRegn_no(), null, appl_details.getCurrent_state_cd(), appl_details.getCurrent_off_cd());
                    ins_dobj_retVA = InsImpl.set_ins_dtls_db_to_dobjVA(appl_details.getRegn_no());
                    //start of getting insurance details from service
                    InsuranceDetailService detailService = new InsuranceDetailService();
                    InsDobj insDobj = detailService.getInsuranceDetailsByService(getAppl_details().getRegn_no(), getAppl_details().getCurrent_state_cd(), getAppl_details().getCurrent_off_cd());
                    if (insDobj != null) {
                        if ((DateUtils.compareDates(DateUtils.addToDate(insDobj.getIns_from(), DateUtils.MONTH, -1), ownerDetail.getPurchase_date()) != 1)) {
                            if (ins_dobj_ret != null) {
                                insDobj.setIdv(ins_dobj_ret.getIdv());
                            } else if (ins_dobj_retVA != null) {
                                insDobj.setIdv(ins_dobj_retVA.getIdv());
                            }
                            ins_bean.set_Ins_dobj_to_bean(insDobj);
                            ownerDetail.setInsDobj(insDobj);
                            ins_bean.setDisable(true);
                            //end of getting insurance details from service 
                        }

                        //for checking insurance availablity and expiration start
                        if (!insDobj.isIibData()) {
                            ins_bean.componentReadOnly(true);
                            ins_bean.setGovtVehFlag(false);
                            ins_bean.validateInsurance(ins_dobj_ret, ins_dobj_retVA, false);
                        } //for checking insurance availablity and expiration end  
                    }

                }
            }

            if (appl_details != null && noc_dobj != null) {
                if (noc_dobj.getState_to() == null) {
                    noc_dobj.setState_to(appl_details.getCurrent_state_cd());
                }
                //for setting saved list from db into the form start
                filterNOCOffcList();
                if (appl_details.getCurrent_state_cd().equalsIgnoreCase(noc_dobj.getState_to())) {
                    Iterator ite = office_list.iterator();
                    while (ite.hasNext()) {
                        SelectItem obj = (SelectItem) ite.next();
                        if (Integer.parseInt(obj.getValue().toString()) == appl_details.getCurrent_off_cd() && !toWithNOC) {
                            office_list.remove(obj);
                            break;
                        }
                    }
                }
                //for setting saved list from db into the form end
                //add
                noc_reason = noc_Impl.getNocReason();
                if (noc_reason != null) {
                    for (int i = 0; i < noc_reason.length; i++) {
                        if (Integer.parseInt(noc_reason[i][0]) == TableConstants.VM_TRANSACTION_MAST_CHG_ADD || Integer.parseInt(noc_reason[i][0]) == TableConstants.VM_TRANSACTION_MAST_TO) {
                            purposeCodeList.add(new SelectItem(noc_reason[i][0], noc_reason[i][1]));
                        }
                    }
                }
                if (noc_dobj.getPur_cd_to() != 0 && noc_dobj.getPur_cd_to() == TableConstants.VM_TRANSACTION_MAST_TO) {
                    rendered_own_name = true;
                }
            }
            //end

        } catch (VahanException ve) {
            vahanMessages = ve.getMessage();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " [ApplNo-" + appl_details.getAppl_no() + ",RegnNo-" + appl_details.getRegn_no() + ",PurCd-" + appl_details.getPur_cd() + "] " + ex.getStackTrace()[0]);
            vahanMessages = "Something Went Wrong, Please Contact to the System Administrator";
        }
    }

    @Override
    public List<ComparisonBean> compareChanges() {

        if (noc_dobj_prv == null) {
            return compBeanList;
        }
        compBeanList.clear();

        Compare("State_to", noc_dobj_prv.getState_to(), noc_dobj.getState_to(), (ArrayList) compBeanList);
        Compare("auth_to", noc_dobj_prv.getOff_to(), noc_dobj.getOff_to(), (ArrayList) compBeanList);
        Compare("rto_disp_no", noc_dobj_prv.getDispatch_no(), noc_dobj.getDispatch_no(), (ArrayList) compBeanList);
        Compare("ncrb_ref_no", noc_dobj_prv.getNcrb_ref(), noc_dobj.getNcrb_ref(), (ArrayList) compBeanList);
        Compare("reason_for_taking", noc_dobj_prv.getPur_cd_to(), noc_dobj.getPur_cd_to(), (ArrayList) compBeanList);
        Compare("transferee_name", noc_dobj_prv.getNew_own_name(), noc_dobj.getNew_own_name(), (ArrayList) compBeanList);
        Compare("noc_dt", noc_dobj_prv.getNoc_dt(), noc_dobj.getNoc_dt(), (ArrayList) compBeanList);

        if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NOC_CANCEL) {
            Compare("file_ref_no", noc_dobj_prv.getFile_ref_no(), noc_dobj.getFile_ref_no(), (ArrayList) compBeanList);
            Compare("approve_by", noc_dobj_prv.getApp_by(), noc_dobj.getApp_by(), (ArrayList) compBeanList);
            Compare("reason", noc_dobj_prv.getReason(), noc_dobj.getReason(), (ArrayList) compBeanList);
        }

        return compBeanList;

    }

    @Override
    public String save() {
        String return_location = "";
        try {
            if (noc_dobj_prv != null && noc_dobj_prv.getPur_cd_to() == TableConstants.VM_TRANSACTION_MAST_TO && noc_dobj_prv.getPur_cd_to() != noc_dobj.getPur_cd_to()) {
                noc_dobj.setNew_own_name("");
            }
            if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NOC_CANCEL) {
                List<ComparisonBean> compareChanges = compareChanges();
                if (!compareChanges.isEmpty() || noc_dobj_prv == null) { //save only when data is really changed by user and when form is empty
                    noc_dobj.setState_cd(appl_details.getCurrent_state_cd());
                    noc_dobj.setOff_cd(appl_details.getCurrent_off_cd());
                    noc_dobj.setPur_cd(pur_cd);
                    NocImpl.makeChangeNOC(noc_dobj, ComparisonBeanImpl.changedDataContents(compareChanges), null, null);
                }
                return_location = "seatwork";
            } else {
                SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
                Date applDate = format.parse(appl_details.getAppl_dt());
                InsDobj ins_dobj_new = ins_bean.set_InsBean_to_dobj();
                if (ins_dobj_new != null && ins_bean.validateInsurance(ins_dobj_new)) {
                    ins_bean.ins_update(appl_details.getOwnerDetailsDobj());
                    List<ComparisonBean> compareChanges = compareChanges();
                    if (!compareChanges.isEmpty() || noc_dobj_prv == null) { //save only when data is really changed by user and when form is empty
                        noc_dobj.setState_cd(appl_details.getCurrent_state_cd());
                        noc_dobj.setOff_cd(appl_details.getCurrent_off_cd());
                        noc_dobj.setPur_cd(pur_cd);
                        NocImpl.makeChangeNOC(noc_dobj, ComparisonBeanImpl.changedDataContents(compareChanges), selectedFancyRetnetion, applDate);
                    }
                    return_location = "seatwork";
                } else {
                    if (ins_dobj_new == null) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Insurance Details not available !!!"));
                    }
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

    @Override
    public String saveAndMoveFile() {

        String return_location = "";
        try {
            if (noc_dobj_prv != null && noc_dobj_prv.getPur_cd_to() == TableConstants.VM_TRANSACTION_MAST_TO && noc_dobj_prv.getPur_cd_to() != noc_dobj.getPur_cd_to()) {
                noc_dobj.setNew_own_name("");
            }
            if (appl_details.getCurrent_action_cd() == TableConstants.ISSUE_OF_NOC_ENTRY || appl_details.getCurrent_action_cd() == TableConstants.ISSUE_OF_NOC_VERIFY
                    || appl_details.getCurrent_action_cd() == TableConstants.ISSUE_OF_NOC_APPROVE) {
                if (!noc_dobj.getState_to().equalsIgnoreCase(appl_details.getOwnerDetailsDobj().getC_state())) {
                    ApplicationInwardImpl applicationInwardImpl = new ApplicationInwardImpl();
                    if (applicationInwardImpl.checkPermit(appl_details.getOwnerDetailsDobj().getVh_class())) {
                        String regn_no = CommonUtils.formRegnNo(appl_details.getOwnerDetailsDobj().getRegn_no().trim());
                        permitStatus = applicationInwardImpl.checkNationalPermitValidity(regn_no);
                        if (!permitStatus.isEmpty()) {
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert", "NOC Entry can not be done " + permitStatus));
                        }
                    }
                }

            }
            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE) && this.appl_details.isDocVerifyForFacelessAppl()) {
                String[] notVerifiedDocDetails = ServerUtil.verifyDocUploaded(appl_details.getAppl_no(), getAppl_details().getCurrent_action_cd());
                if (notVerifiedDocDetails != null) {
                    appl_details.setNotVerifiedDocdetails(notVerifiedDocDetails[1]);
                    throw new VahanException(notVerifiedDocDetails[0]);
                }
            }
            if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NOC_CANCEL) {
                Status_dobj status = new Status_dobj();
                status.setAppl_dt(appl_details.getAppl_dt());
                status.setAppl_no(appl_details.getAppl_no());
                status.setPur_cd(appl_details.getPur_cd());
                status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
                status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
                status.setStatus(getApp_disapp_dobj().getNew_status());
                status.setCurrent_role(appl_details.getCurrent_role());

                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                        || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)
                        || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                    status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                    NocImpl noc_Impl = new NocImpl();
                    noc_dobj.setState_cd(appl_details.getCurrent_state_cd());
                    noc_dobj.setOff_cd(appl_details.getCurrent_off_cd());
                    noc_dobj.setPur_cd(pur_cd);
                    noc_Impl.update_NOC_Status(noc_dobj, noc_dobj_prv, status, ComparisonBeanImpl.changedDataContents(compareChanges()), null, null, appl_details.getCurrent_state_cd(), appl_details.getCurrent_off_cd(), appl_details.getCurrentEmpCd());
                    if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                        ServerUtil.sendSmsInFacelessService(appl_details.getAppl_no(), Util.getUserStateCode(), Util.getUserSeatOffCode(), appl_details.getRegn_no(), appl_details.getNotVerifiedDocdetails());
                        return_location = disapprovalPrint();
                    } else {
                        return_location = "seatwork";
                    }
                }
            } else {

                InsDobj ins_dobj_new = ins_bean.set_InsBean_to_dobj();
                if (ins_dobj_new != null) {
                    if (!getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                        if (!appl_details.getOwnerDetailsDobj().getStatus().equalsIgnoreCase(TableConstants.VT_RC_SURRENDER_STATUS) && !"KA".contains(appl_details.getCurrent_state_cd())) {
                            if (ins_bean.validateInsurance(ins_dobj_new)) {
                                ins_bean.ins_update(appl_details.getOwnerDetailsDobj());
                            } else {
                                return return_location;
                            }
                        }
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
                            || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)
                            || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                        status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                        NocImpl noc_Impl = new NocImpl();
                        noc_dobj.setState_cd(appl_details.getCurrent_state_cd());
                        noc_dobj.setOff_cd(appl_details.getCurrent_off_cd());
                        noc_dobj.setPur_cd(pur_cd);
                        noc_Impl.update_NOC_Status(noc_dobj, noc_dobj_prv, status, ComparisonBeanImpl.changedDataContents(compareChanges()), selectedFancyRetnetion, appl_details.getOwnerDobj(), appl_details.getCurrent_state_cd(), appl_details.getCurrent_off_cd(), appl_details.getCurrentEmpCd());
                        if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                            ServerUtil.sendSmsInFacelessService(appl_details.getAppl_no(), Util.getUserStateCode(), Util.getUserSeatOffCode(), appl_details.getRegn_no(), appl_details.getNotVerifiedDocdetails());
                            return_location = disapprovalPrint();
                        } else {
                            return_location = "seatwork";
                        }
                    }
                } else {
                    if (ins_dobj_new == null) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Insurance Details not available !!!"));
                    }
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

    public void updateRtoFromStateListener(AjaxBehaviorEvent even) {
        try {
            filterNOCOffcList();
            if (Util.getUserStateCode().equalsIgnoreCase(noc_dobj.getState_to())) {
                Iterator ite = office_list.iterator();
                while (ite.hasNext()) {
                    SelectItem obj = (SelectItem) ite.next();
                    if (Integer.parseInt(obj.getValue().toString()) == appl_details.getCurrent_off_cd()) {
                        office_list.remove(obj);
                        break;
                    }
                }
                header = "Clearance Certificate(CC)";
            } else {
                header = "NOC Vehicle Details";
            }
            purposeCodeList.clear();
            noc_dobj.setPur_cd_to(0);
            if (noc_reason != null) {
                for (int i = 0; i < noc_reason.length; i++) {
                    if (Integer.parseInt(noc_reason[i][0]) == TableConstants.VM_TRANSACTION_MAST_CHG_ADD || Integer.parseInt(noc_reason[i][0]) == TableConstants.VM_TRANSACTION_MAST_TO) {
                        purposeCodeList.add(new SelectItem(noc_reason[i][0], noc_reason[i][1]));
                    }
                }
            }
            rendered_own_name = false;
            //end
        } catch (VahanException ve) {
            vahanMessages = ve.getMessage();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " [ApplNo-" + appl_details.getAppl_no() + ",RegnNo-" + appl_details.getRegn_no() + ",PurCd-" + appl_details.getPur_cd() + "] " + ex.getStackTrace()[0]);
            vahanMessages = "Something Went Wrong, Please Contact to the System Administrator";
        }
    }

    //add 
    public void update_purpuse(AjaxBehaviorEvent even) {
        int pur_cd_to = (int) ((UIOutput) even.getSource()).getValue();
        if (pur_cd_to == TableConstants.VM_TRANSACTION_MAST_TO) {
            rendered_own_name = true;
        } else {

            rendered_own_name = false;
        }

    }
    //end    

    private void filterNOCOffcList() throws VahanException {
        office_list.clear();
        List<Integer> offTypeCd = Arrays.asList(0, 1, 2);
        office_list = ServerUtil.getOfficeBasedOnType(noc_dobj.getState_to(), offTypeCd);
    }

    /**
     * @return the state_list
     */
    public List getState_list() {
        return state_list;
    }

    /**
     * @param state_list the state_list to set
     */
    public void setState_list(List state_list) {
        this.state_list = state_list;
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
     * @return the noc_dobj_prv
     */
    public NocDobj getNoc_dobj_prv() {
        return noc_dobj_prv;
    }

    /**
     * @param noc_dobj_prv the noc_dobj_prv to set
     */
    public void setNoc_dobj_prv(NocDobj noc_dobj_prv) {
        this.noc_dobj_prv = noc_dobj_prv;
    }

    /**
     * @return the noc_dobj
     */
    public NocDobj getNoc_dobj() {
        return noc_dobj;
    }

    /**
     * @param noc_dobj the noc_dobj to set
     */
    public void setNoc_dobj(NocDobj noc_dobj) {
        this.noc_dobj = noc_dobj;
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
     * @return the office_list
     */
    public List getOffice_list() {
        return office_list;
    }

    /**
     * @param office_list the office_list to set
     */
    public void setOffice_list(List office_list) {
        this.office_list = office_list;
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
     * @return the cancellation_panel_visibility
     */
    public boolean isCancellation_panel_visibility() {
        return cancellation_panel_visibility;
    }

    /**
     * @param cancellation_panel_visibility the cancellation_panel_visibility to
     * set
     */
    public void setCancellation_panel_visibility(boolean cancellation_panel_visibility) {
        this.cancellation_panel_visibility = cancellation_panel_visibility;
    }

    /**
     * @return the comp_disable
     */
    public boolean isComp_disable() {
        return comp_disable;
    }

    /**
     * @param comp_disable the comp_disable to set
     */
    public void setComp_disable(boolean comp_disable) {
        this.comp_disable = comp_disable;
    }

    /**
     * @return the renderSaveButton
     */
    public boolean isRenderSaveButton() {
        return renderSaveButton;
    }

    /**
     * @param renderSaveButton the renderSaveButton to set
     */
    public void setRenderSaveButton(boolean renderSaveButton) {
        this.renderSaveButton = renderSaveButton;
    }

    /**
     * @return the renderprintbutton
     */
    public boolean isRenderprintbutton() {
        return renderprintbutton;
    }

    /**
     * @param renderprintbutton the renderprintbutton to set
     */
    public void setRenderprintbutton(boolean renderprintbutton) {
        this.renderprintbutton = renderprintbutton;
    }

    /**
     * @param ins_bean the ins_bean to set
     */
    public void setIns_bean(InsBean ins_bean) {
        this.ins_bean = ins_bean;
    }

    /**
     * @return the isNOCInwardwithHPC
     */
    public boolean isIsNOCInwardwithHPC() {
        return isNOCInwardwithHPC;
    }

    /**
     * @param isNOCInwardwithHPC the isNOCInwardwithHPC to set
     */
    public void setIsNOCInwardwithHPC(boolean isNOCInwardwithHPC) {
        this.isNOCInwardwithHPC = isNOCInwardwithHPC;
    }

    /**
     * @return the nocHPCDetails
     */
    public String getNocHPCDetails() {
        return nocHPCDetails;
    }

    /**
     * @param nocHPCDetails the nocHPCDetails to set
     */
    public void setNocHPCDetails(String nocHPCDetails) {
        this.nocHPCDetails = nocHPCDetails;
    }

    /**
     * @return the renderInsPanel
     */
    public boolean isRenderInsPanel() {
        return renderInsPanel;
    }

    /**
     * @param renderInsPanel the renderInsPanel to set
     */
    public void setRenderInsPanel(boolean renderInsPanel) {
        this.renderInsPanel = renderInsPanel;
    }

    /**
     * @return the fancyRetention
     */
    public boolean isFancyRetention() {
        return fancyRetention;
    }

    /**
     * @param fancyRetention the fancyRetention to set
     */
    public void setFancyRetention(boolean fancyRetention) {
        this.fancyRetention = fancyRetention;
    }

    /**
     * @return the selectedFancyRetnetion
     */
    public String getSelectedFancyRetnetion() {
        return selectedFancyRetnetion;
    }

    /**
     * @param selectedFancyRetnetion the selectedFancyRetnetion to set
     */
    public void setSelectedFancyRetnetion(String selectedFancyRetnetion) {
        this.selectedFancyRetnetion = selectedFancyRetnetion;
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
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }

    public List getPurposeCodeList() {
        return purposeCodeList;
    }

    public void setPurposeCodeList(List purposeCodeList) {
        this.purposeCodeList = purposeCodeList;
    }

    public int getSelect_purt_cd() {
        return select_purt_cd;
    }

    public void setSelect_purt_cd(int select_purt_cd) {
        this.select_purt_cd = select_purt_cd;
    }

    public boolean isRendered_own_name() {
        return rendered_own_name;
    }

    public void setRendered_own_name(boolean rendered_own_name) {
        this.rendered_own_name = rendered_own_name;
    }

    public OwnerDetailsDobj getOwnerDetail() {
        return ownerDetail;
    }

    public void setOwnerDetail(OwnerDetailsDobj ownerDetail) {
        this.ownerDetail = ownerDetail;
    }
}
