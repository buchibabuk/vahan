/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.RegistrationNoAssignmentImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author nicsi
 */
@ManagedBean(name = "reg_no_assign")
@ViewScoped
public class RegistrationNoAssignmentBean extends AbstractApplBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(RegistrationNoAssignmentBean.class);
    private String alloted_regn_no;
    private Map<String, Object> regnNoList;
    private OwnerDetailsDobj ownerDetail;
    RegistrationNoAssignmentImpl impl = new RegistrationNoAssignmentImpl();
    private String vahanMessages = null;
    private boolean showBackButton;
    private boolean isAdvanceRegnAssign;
    private String regn_show;
    boolean showFancyDetails;
    boolean renderReverBackButton;
    @ManagedProperty(value = "#{owner_bean}")
    private OwnerBean owner_bean;
    private String pur_cd_d = null;

    @PostConstruct
    public void init() {
        try {
            if ("PB".equalsIgnoreCase(Util.getUserStateCode())) {
                renderReverBackButton = true;
            }
            boolean isFancy = impl.isFancyNoAssigned(appl_details.getAppl_no());
            regnNoList = impl.getAvailableRegistrationNoList(appl_details.getAppl_no(), alloted_regn_no, isFancy);
            if (isFancy) {
                owner_bean.setBlnAdvancedRegNo(true);
                owner_bean.setDisableOwnerName(true);
                owner_bean.setDisableAdvanceRegnAllotted(true);
                for (Object key : regnNoList.keySet()) {
                    alloted_regn_no = key.toString();
                    break;
                }
                showFancyDetails = true;
            }
            OwnerImpl owner_Impl = new OwnerImpl();
            ownerDetail = owner_Impl.getVaOwnerDetails(appl_details.getAppl_no().toUpperCase(), Util.getUserStateCode(), Util.getUserSeatOffCode());
            if (ownerDetail != null) {
                appl_details.setChasi_no(ownerDetail.getChasi_no());
                appl_details.setOwn_name(ownerDetail.getOwner_name());
                Owner_dobj owner_dobj = owner_Impl.getOwnerDobj(ownerDetail);
                owner_bean.set_Owner_appl_dobj_to_bean(owner_dobj);
                owner_bean.getOwnerDobj().setImported_vch(owner_dobj.getImported_vch());
                owner_bean.getOwnerDobj().setOwner_identity(owner_bean.getOwner_identification());
                owner_bean.setBlnPgAdvancedRegNo(true);
            } else {
                ownerDetail = owner_Impl.getOwnerDetails(appl_details.getRegn_no(), Util.getUserStateCode(), Util.getUserSeatOffCode());
                if (ownerDetail != null) {
                    owner_bean.setBlnAdvancedRegNo(true);
                    owner_bean.setDisableOwnerName(true);
                    appl_details.setChasi_no(ownerDetail.getChasi_no());
                    appl_details.setOwn_name(ownerDetail.getOwner_name());
                    appl_details.setChasi_no(ownerDetail.getChasi_no());
                    appl_details.setOwn_name(ownerDetail.getOwner_name());
                    Owner_dobj owner_dobj = owner_Impl.getOwnerDobj(ownerDetail);
                    owner_bean.set_Owner_appl_dobj_to_bean(owner_dobj);
                    owner_bean.getOwnerDobj().setImported_vch(owner_dobj.getImported_vch());
                    owner_bean.getOwnerDobj().setOwner_identity(owner_bean.getOwner_identification());

                } else {
                    vahanMessages = "Vehicle (" + appl_details.getRegn_no() + ") Details not Found.";
                }
                List<Status_dobj> statusList = ServerUtil.applicationStatus(appl_details.getRegn_no(), appl_details.getAppl_no().toUpperCase(), appl_details.getCurrent_state_cd());
                if (!statusList.isEmpty() && statusList.size() > 1 && "AS,OR".contains(appl_details.getCurrent_state_cd())) {
                    for (int i = 0; i < statusList.size(); i++) {
                        pur_cd_d = pur_cd_d + "," + statusList.get(i).getPur_cd();
                    }
                    pur_cd_d = pur_cd_d + ",";
                    if (statusList.size() > 1 && pur_cd_d.contains("," + TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO + ",") || pur_cd_d.contains("," + TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION + ",")) {
                        throw new VahanException("Please Approve other transaction first against Appl No." + appl_details.getAppl_no().toUpperCase());
                    }
                }
            }
        } catch (VahanException ve) {
            vahanMessages = ve.getMessage();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            vahanMessages = "Something went wrong, please try again.";
        }
    }

    public void save() {
        String new_Regn_no = "";
        if ("".equalsIgnoreCase(alloted_regn_no) && CommonUtils.isNullOrBlank(alloted_regn_no)) {
            JSFUtils.setFacesMessage("Please Select One Registration NO", "message", JSFUtils.WARN);
            return;
        }
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
        VehicleParameters vehparameters = FormulaUtils.fillVehicleParametersFromDobj(owner_bean.getOwnerDobj());
        statusDobj.setVehicleParameters(vehparameters);
        try {
            vehparameters.setAPPL_DATE(ServerUtil.setTempApplDtAsNewApplDT(statusDobj));
            vehparameters.setPUR_CD(appl_details.getPur_cd());
            vehparameters.setACTION_CD(appl_details.getCurrent_action_cd());
            owner_bean.getOwnerDobj().setAppl_no(appl_details.getAppl_no());
            owner_bean.getOwnerDobj().setRegn_no(appl_details.getRegn_no());
            boolean isValidForRegistration = ServerUtil.validateVehicleNorms(owner_bean.getOwnerDobj(), appl_details.getPur_cd(), vehparameters, Util.getTmConfiguration().getTmConfigDealerDobj());
            if (!isValidForRegistration) {
                throw new VahanException("State Transport Department has not authorized you to further process this Registration Application of '" + MasterTableFiller.masterTables.VM_VH_CLASS.getDesc(owner_bean.getOwnerDobj().getVh_class() + "") + "' with emission norms " + MasterTableFiller.masterTables.VM_NORMS.getDesc(owner_bean.getOwnerDobj().getNorms() + "") + " , Purchase Date: " + DateUtil.parseDateToString(owner_bean.getOwnerDobj().getPurchase_dt()) + " , Application Date: " + DateUtil.parseDateToString(DateUtil.parseDate(DateUtil.convertStringYYYYMMDDToDDMMYYYY(vehparameters.getAPPL_DATE()))) + ", please contact respective Registering Authority regarding this.");
            }
            // OwnerImpl ownerImpl = new OwnerImpl();
            // Owner_dobj ownerDobj = ownerImpl.getOwnerDobj(ownerDetail);
            List<ComparisonBean> compareChagnes = owner_bean.getCompBeanList();
            new_Regn_no = impl.saveAndAssignRegn_no(owner_bean.getOwnerDobj(), alloted_regn_no, statusDobj, appl_details.getPur_cd(), compareChagnes, owner_bean.getAdvanceRegNoDobj());
            if (!"".equalsIgnoreCase(new_Regn_no) && !CommonUtils.isNullOrBlank(new_Regn_no)) {
                JSFUtils.setFacesMessage("Registration No:" + new_Regn_no + " Assigned For The Application No: " + owner_bean.getOwnerDobj().getAppl_no() + "", "message", JSFUtils.INFO);
                setShowBackButton(true);
            } else {
                JSFUtils.setFacesMessage("Error: Problem to Assigned Registration No For Application No" + owner_bean.getOwnerDobj().getAppl_no() + "", "message", JSFUtils.ERROR);
                return;
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
            return;
        }
    }

    public String revertBackForAdvanceRegistrationNo() {
        String returnLocation = null;
        Status_dobj statusDobj = new Status_dobj();
        statusDobj.setAction_cd(appl_details.getCurrent_action_cd());
        statusDobj.setAppl_dt(appl_details.getAppl_dt());
        statusDobj.setAppl_no(appl_details.getAppl_no());
        statusDobj.setRegn_no(appl_details.getRegn_no());
        statusDobj.setPur_cd(appl_details.getPur_cd());
        statusDobj.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
        statusDobj.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
        statusDobj.setStatus(TableConstants.STATUS_REVERT);
        statusDobj.setCurrent_role(appl_details.getCurrent_role());
        try {
            OwnerImpl ownerImpl = new OwnerImpl();
            Owner_dobj ownerDobj = ownerImpl.getOwnerDobj(ownerDetail);
            ownerDobj.setAppl_no(appl_details.getAppl_no());

            impl.revertForAdvanceRegnNo(ownerDobj, alloted_regn_no, statusDobj, appl_details.getPur_cd());
            returnLocation = "seatwork";
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return "";
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
            return "";
        }
        return returnLocation;
    }

    public void advanceSaveListener(AjaxBehaviorEvent ajax) {
        //fill details from AdvanceRegnNo_dobj
        try {
            if (owner_bean.getAdvanceRegNoDobj() != null && owner_bean.getAdvanceRegNoDobj().getRegn_no() != null) {
                if (!owner_bean.getOwnerDobj().getOwner_name().trim().equalsIgnoreCase(owner_bean.getAdvanceRegNoDobj().getOwner_name().trim())) {
                    JSFUtils.setFacesMessage("Error: Owner Name Must Be Same as in Advance Registration No Receipt", "message", JSFUtils.ERROR);
                    return;
                }
                owner_bean.setBlnAdvancedRegNo(true);
                owner_bean.setRegn_no(owner_bean.getAdvanceRegNoDobj().getRegn_no());
                owner_bean.getOwnerDobj().setOwner_name(owner_bean.getAdvanceRegNoDobj().getOwner_name());
                owner_bean.setDisableOwnerName(true);
                owner_bean.getOwnerDobj().setF_name(owner_bean.getAdvanceRegNoDobj().getF_name());
                owner_bean.setDisableFName(false);
                owner_bean.getOwnerDobj().setC_add1(owner_bean.getAdvanceRegNoDobj().getC_add1());
                owner_bean.getOwnerDobj().setC_add2(owner_bean.getAdvanceRegNoDobj().getC_add2());
                owner_bean.getOwnerDobj().setC_add3(owner_bean.getAdvanceRegNoDobj().getC_add3());
                owner_bean.getOwnerDobj().setC_district(owner_bean.getAdvanceRegNoDobj().getC_district());
                owner_bean.getOwnerDobj().setC_pincode(owner_bean.getAdvanceRegNoDobj().getC_pincode());
                owner_bean.getOwnerDobj().setC_state(owner_bean.getAdvanceRegNoDobj().getC_state());
                owner_bean.getOwner_identification().setMobile_no(owner_bean.getAdvanceRegNoDobj().getMobile_no());
                owner_bean.getOwner_identification().setMobileNoEditable(true);
                owner_bean.setDisableAdvanceRegnAllotted(true);
                regnNoList.clear();
                regnNoList.put(owner_bean.getAdvanceRegNoDobj().getRegn_no(), owner_bean.getAdvanceRegNoDobj().getRegn_no());
                alloted_regn_no = owner_bean.getAdvanceRegNoDobj().getRegn_no();
                showFancyDetails = true;
                PrimeFaces.current().executeScript("PF('wd_choiceno').hide()");

            } else if (owner_bean.getRetenRegNoDobj() != null && owner_bean.getRetenRegNoDobj().getRegn_no() != null) {
                if (!owner_bean.getOwnerDobj().getOwner_name().trim().equalsIgnoreCase(owner_bean.getAdvanceRegNoDobj().getOwner_name().trim())) {
                    JSFUtils.setFacesMessage("Error: Owner Name Must Be Same as in  Advance Registration No Receipt", "message", JSFUtils.ERROR);
                    return;
                }
                if (owner_bean.getRetenRegNoDobj().getC_district() == 0 || owner_bean.getRetenRegNoDobj().getC_pincode() == 0) {
                    owner_bean.setBlnAdvancedRegNo(false);
                } else {
                    owner_bean.setBlnAdvancedRegNo(true);
                }
                owner_bean.setRegn_no(owner_bean.getRetenRegNoDobj().getRegn_no());
                owner_bean.getOwnerDobj().setOwner_name(owner_bean.getRetenRegNoDobj().getOwner_name());
                if ("HR".contains(Util.getUserStateCode())) {
                    owner_bean.setDisableOwnerName(false);
                } else {
                    owner_bean.setDisableOwnerName(true);
                }
                owner_bean.getOwnerDobj().setF_name(owner_bean.getRetenRegNoDobj().getF_name());
                owner_bean.setDisableFName(false);
                owner_bean.getOwnerDobj().setC_add1(owner_bean.getRetenRegNoDobj().getC_add1());
                owner_bean.getOwnerDobj().setC_add2(owner_bean.getRetenRegNoDobj().getC_add2());
                owner_bean.getOwnerDobj().setC_add3(owner_bean.getRetenRegNoDobj().getC_add3());
                owner_bean.getOwnerDobj().setC_district(owner_bean.getRetenRegNoDobj().getC_district());
                owner_bean.getOwnerDobj().setC_pincode(owner_bean.getRetenRegNoDobj().getC_pincode());
                owner_bean.getOwnerDobj().setC_state(owner_bean.getRetenRegNoDobj().getC_state());
                owner_bean.getOwner_identification().setMobile_no(owner_bean.getRetenRegNoDobj().getMobile_no());
                if (owner_bean.getOwner_identification().getMobile_no() != 0l) {
                    owner_bean.getOwner_identification().setMobileNoEditable(true);
                }
                owner_bean.setDisableAdvanceRegnAllotted(true);
                regnNoList.clear();
                regnNoList.put(owner_bean.getAdvanceRegNoDobj().getRegn_no(), owner_bean.getAdvanceRegNoDobj().getRegn_no());
                alloted_regn_no = owner_bean.getAdvanceRegNoDobj().getRegn_no();
                showFancyDetails = true;
                PrimeFaces.current().executeScript("PF('wd_choiceno').hide()");
            } else {
                owner_bean.setAdvanceRegNoDobj(null);
                owner_bean.setRetenRegNoDobj(null);
                owner_bean.setBlnAdvancedRegNo(false);
                owner_bean.setAdvanceRegnAllotted("No");
                owner_bean.setDisableAdvanceRegnAllotted(false);
                regnNoList = impl.getAvailableRegistrationNoList(appl_details.getAppl_no(), alloted_regn_no, false);
                alloted_regn_no = "";
                showFancyDetails = false;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }

    }

    public String getAlloted_regn_no() {
        return alloted_regn_no;
    }

    public void setAlloted_regn_no(String alloted_regn_no) {
        this.alloted_regn_no = alloted_regn_no;
    }

    public Map<String, Object> getRegnNoList() {
        return regnNoList;
    }

    public void setRegnNoList(Map<String, Object> regnNoList) {
        this.regnNoList = regnNoList;
    }

    public boolean isShowFancyDetails() {
        return showFancyDetails;
    }

    public void setShowFancyDetails(boolean showFancyDetails) {
        this.showFancyDetails = showFancyDetails;
    }

    public OwnerDetailsDobj getOwnerDetail() {
        return ownerDetail;
    }

    public void setOwnerDetail(OwnerDetailsDobj ownerDetail) {
        this.ownerDetail = ownerDetail;
    }

    public String getVahanMessages() {
        return vahanMessages;
    }

    public void setVahanMessages(String vahanMessages) {
        this.vahanMessages = vahanMessages;
    }

    public boolean isShowBackButton() {
        return showBackButton;
    }

    public void setShowBackButton(boolean showBackButton) {
        this.showBackButton = showBackButton;
    }

    public String getRegn_show() {
        return regn_show;
    }

    public void setRegn_show(String regn_show) {
        this.regn_show = regn_show;
    }

    public boolean isIsAdvanceRegnAssign() {
        return isAdvanceRegnAssign;
    }

    public void setIsAdvanceRegnAssign(boolean isAdvanceRegnAssign) {
        this.isAdvanceRegnAssign = isAdvanceRegnAssign;
    }

    public boolean isRenderReverBackButton() {
        return renderReverBackButton;
    }

    public void setRenderReverBackButton(boolean renderReverBackButton) {
        this.renderReverBackButton = renderReverBackButton;
    }

    public OwnerBean getOwner_bean() {
        return owner_bean;
    }

    public void setOwner_bean(OwnerBean owner_bean) {
        this.owner_bean = owner_bean;
    }
}
