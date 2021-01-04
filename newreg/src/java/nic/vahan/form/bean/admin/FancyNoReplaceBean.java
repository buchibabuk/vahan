/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.bean.fancy.RetenRegnNo_dobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.configuration.TmConfigurationAdvanceRegnDobj;
import nic.vahan.form.dobj.fancy.AdvanceRegnNo_dobj;
import nic.vahan.form.dobj.fancy.FancyNoReplaceDobj;
import nic.vahan.form.impl.NewImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.admin.FancyNoReplaceImpl;
import nic.vahan.form.impl.admin.OwnerAdminImpl;
import nic.vahan.server.ServerUtil;
import org.primefaces.PrimeFaces;

/**
 *
 * @author nicsi
 */
@ManagedBean(name = "fancyNoReplaceBean")
@ViewScoped
public class FancyNoReplaceBean implements Serializable {

    private static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(FancyNoReplaceBean.class);
    String empCd;
    private String regnNo;
    private OwnerDetailsDobj ownerDetail;
    TmConfigurationDobj tmConfDobj = null;
    private boolean disableRegnNo = false;
    private boolean render_vehicle_type = true;
    private boolean renderBackButton = false;
    private boolean renderTab = false;
    private String selectedFancyRetnetion;
    private boolean retAllowed = false;
    private boolean advRegnCheck = false;
    private boolean disableAdvRegnCheck = false;
    private boolean disableRetCheck = false;
    private boolean advRetCheckDialogue = false;
    private boolean retCheck = false;
    private boolean fancyRetention;
    private boolean advRegnCheckDialogue = false;
    private AdvanceRegnNo_dobj advRegnNoDobj = new AdvanceRegnNo_dobj();
    private RetenRegnNo_dobj retenRegNoDobj = new RetenRegnNo_dobj();
    private List list_c_state;
    private List list_adv_district = new ArrayList();
    private boolean advanceFancyNorender = true;
    private String remark;
    private FancyNoReplaceDobj fancyNoDobj = null;
    private String messageFancyNo = "";
    private TmConfigurationAdvanceRegnDobj tmConfigChangeAdminDobj;
    private int oldRegnSize = 7;
    private boolean blnOldRegParam = false;

    public FancyNoReplaceBean() {
    }

    @PostConstruct
    public void init() {
        retAllowed = false;
        advanceFancyNorender = false;
        try {
            list_c_state = new ArrayList();
            tmConfDobj = Util.getTmConfiguration();
            list_c_state = MasterTableFiller.getStateList();

            tmConfigChangeAdminDobj = ServerUtil.getTmConfigurationChangeAdminParameters(Util.getUserStateCode());
            if (tmConfigChangeAdminDobj != null) {
                setOldRegnSize(tmConfigChangeAdminDobj.getSizeOldNumber());
            }
        } catch (Exception ex) {
            String mgs = Util.getLocaleSessionMsg();
            Logger.getLogger(FancyNoReplaceBean.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, mgs, mgs));
            return;
        }
    }

    public void getDetails() {
        String msg = "";
        empCd = Util.getEmpCode();
        if (this.empCd == null || this.empCd.trim().equalsIgnoreCase("")) {
            msg = Util.getLocaleSessionMsg();
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
            return;
        }
        if (this.regnNo == null || this.regnNo.trim().equalsIgnoreCase("")) {
            msg = Util.getLocaleMsg("valid_regn_no");
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
            return;
        }
        try {
            OwnerImpl ownerImpl = new OwnerImpl();
            OwnerAdminImpl adminImpl = new OwnerAdminImpl();
            String isPending = adminImpl.checkPending(regnNo, Util.getUserStateCode());
            if (isPending != null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", isPending));
                return;
            }


            if (tmConfigChangeAdminDobj != null && tmConfigChangeAdminDobj.isOldNumberReplace() && regnNo.length() <= oldRegnSize) {
                blnOldRegParam = true;
            }


            FancyNoReplaceImpl fancyImp = new FancyNoReplaceImpl();
            fancyNoDobj = fancyImp.getApplNoAndPurCode(Util.getUserStateCode(), regnNo);
            if (!blnOldRegParam) {
                if (fancyNoDobj == null) {
                    msg = Util.getLocaleMsg("noFromV4");
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                    return;
                }
            } else {
                fancyNoDobj = new FancyNoReplaceDobj();
            }
            List<OwnerDetailsDobj> list = ownerImpl.getOwnerDetailsList(regnNo, null);
            msg = Util.getLocaleMsg("regn_not_found");
            if (list.isEmpty() || tmConfDobj == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            } else if (list.size() == 1) {
                ownerDetail = (OwnerDetailsDobj) list.get(0);
                if (ownerDetail != null && (!ownerDetail.getState_cd().equals(Util.getUserStateCode()) || ownerDetail.getOff_cd() != Util.getSelectedSeat().getOff_cd() || !"Y,A".contains(ownerDetail.getStatus()))) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                    return;
                }
                if (ownerDetail != null && !"N,T,O".contains(ownerDetail.getRegn_type()) && !blnOldRegParam) {
                    msg = Util.getLocaleMsg("onlyNewTmp");
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                    return;
                }
                if (tmConfDobj != null && tmConfDobj.isTo_retention() && !blnOldRegParam) {
                    retAllowed = true;
                } else {
                    retAllowed = false;
                }
                disableRegnNo = true;
                render_vehicle_type = false;
                advanceFancyNorender = true;
                renderTab = true;
                renderBackButton = true;
            } else if (list.size() > 1) {
                msg = Util.getLocaleMsg("multiRecord");
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }
        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vex.getMessage(), vex.getMessage()));
            return;
        } catch (Exception ee) {
            String mgs = Util.getLocaleSomthingMsg();
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, mgs, mgs));
        }
    }

    public void advanceRcptListener() throws Exception {
        try {//
            if (retCheck) {
                String rcptno = getRetenRegNoDobj().getRecp_no();
                if (rcptno == null || rcptno.isEmpty()) {
                    return;
                }
                Date rcptDate = NewImpl.getRetainNoRcptDate(rcptno, ownerDetail.getState_cd(), ownerDetail.getOff_cd());
                if (rcptDate != null) {
                    if ("SK".contains(Util.getUserStateCode()) && (ownerDetail.getOwner_cd() == TableConstants.VEH_TYPE_GOVT || ownerDetail.getOwner_cd() == TableConstants.VEH_TYPE_STATE_GOVT || ownerDetail.getOwner_cd() == TableConstants.VEH_TYPE_GOVT_UNDERTAKING)) {
                        //donothing 
                    } else {
                        NewImpl.validationRetainNoRcptDate(Util.getTmConfiguration(), rcptno, rcptDate);
                    }
                    RetenRegnNo_dobj dobj = NewImpl.getRetenRegNoDetails(rcptno);
                    setRetenRegNoDobj(dobj);
                    String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
                    getList_adv_district().clear();

                    for (int i = 0; i < data.length; i++) {
                        if (data[i][2].trim().equals(getRetenRegNoDobj().getState_cd())) {
                            getList_adv_district().add(new SelectItem(data[i][0], data[i][1]));
                        }
                    }
                    if (!ownerDetail.getOwner_name().equalsIgnoreCase(dobj.getOwner_name())
                            || (ownerDetail.getOwner_cd() == TableConstants.OWNER_CODE_INDIVIDUAL && !ownerDetail.getF_name().equalsIgnoreCase(dobj.getF_name()))) {
                        JSFUtils.setFacesMessage(Util.getLocaleMsg("ownerFatherName"), null, JSFUtils.ERROR);
                        RetenRegnNo_dobj dobj1 = new RetenRegnNo_dobj();
                        setRetenRegNoDobj(dobj1);
                    }
                } else {
                    throw new VahanException(Util.getLocaleMsg("reciptNotFnd"));
                }
            } else if (advRegnCheck) {

                String rcptno = getAdvRegnNoDobj().getRecp_no();
                Date rcptDate = NewImpl.getFancyNoRcptDate(rcptno);
                if (rcptDate != null || blnOldRegParam) {
                    if (!blnOldRegParam) {
                        NewImpl.validationFancyRcptDate(Util.getTmConfiguration(), rcptno, rcptDate);
                    }
                    AdvanceRegnNo_dobj dobj = NewImpl.getAdvanceRegNoDetails(rcptno);
                    setAdvRegnNoDobj(dobj);
                    String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
                    getList_adv_district().clear();

                    for (int i = 0; i < data.length; i++) {
                        if (data[i][2].trim().equals(getAdvRegnNoDobj().getState_cd())) {
                            getList_adv_district().add(new SelectItem(data[i][0], data[i][1]));
                        }
                    }
                    if (!ownerDetail.getOwner_name().equalsIgnoreCase(dobj.getOwner_name())
                            || !ownerDetail.getF_name().equalsIgnoreCase(dobj.getF_name())) {
                        JSFUtils.setFacesMessage(Util.getLocaleMsg("ownerFatherName"), null, JSFUtils.ERROR);
                        AdvanceRegnNo_dobj dobj1 = new AdvanceRegnNo_dobj();
                        setAdvRegnNoDobj(dobj1);
                    }
                } else {
                    throw new VahanException(Util.getLocaleMsg("reciptNotFnd"));
                }
            }

        } catch (VahanException ex) {
            JSFUtils.setFacesMessage(ex.getMessage() + "Please Try Again!", null, JSFUtils.ERROR);
            RetenRegnNo_dobj dobj1 = new RetenRegnNo_dobj();
            setRetenRegNoDobj(dobj1);
        }
    }

    public void advanceExitListener() {
        messageFancyNo = "";
        if (retCheck) {
            RetenRegnNo_dobj dobj1 = new RetenRegnNo_dobj();
            setRetenRegNoDobj(dobj1);
            setRetCheck(false);
        }
        if (advRegnCheck) {
            AdvanceRegnNo_dobj advRegnNoDobj = new AdvanceRegnNo_dobj();
            setAdvRegnNoDobj(advRegnNoDobj);
            setAdvRegnCheck(false);
        }
    }

    public void advanceSaveListener() {
        messageFancyNo = "";
        if (getRetenRegNoDobj().getRegn_no() != null) {
            fancyNoDobj.setAssignRetainNo(true);
            fancyNoDobj.setAssignRetainRegnNo(getRetenRegNoDobj().getRegn_no());
            fancyNoDobj.setRcpt_no(getRetenRegNoDobj().getRecp_no());
            setDisableAdvRegnCheck(true);//add
            messageFancyNo = Util.getLocaleMsg("replacVehNo") + ownerDetail.getRegn_no() + " " + Util.getLocaleMsg("to") + " " + getRetenRegNoDobj().getRegn_no();


        } else if (getAdvRegnNoDobj().getRegn_no() != null) {
            setDisableRetCheck(true);
            fancyNoDobj.setAssignAdvanceNunber(true);
            fancyNoDobj.setAssignAdvanceNO(getAdvRegnNoDobj().getRegn_no());
            fancyNoDobj.setRcpt_no(getAdvRegnNoDobj().getRecp_no());
            messageFancyNo = Util.getLocaleMsg("replacVehNo") + ownerDetail.getRegn_no() + " " + Util.getLocaleMsg("to") + " " + getAdvRegnNoDobj().getRegn_no();
        } else {
            setAdvRegnNoDobj(null);
            setRetenRegNoDobj(null);
            setAdvRegnCheck(false);
            setRetCheck(false);
        }
    }

    public void setVehicleNotoFancyNo() {
        try {
            FancyNoReplaceImpl fancyNoImpl = new FancyNoReplaceImpl();
            String facesMessages = "";
            if ((!advRegnCheck && !retCheck) && tmConfDobj != null && tmConfDobj.isTo_retention()) {
                facesMessages = Util.getLocaleMsg("checkAdvanceRetention");
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", facesMessages));
                return;
            }
            if (!advRegnCheck && tmConfDobj != null && !tmConfDobj.isTo_retention()) {
                facesMessages = Util.getLocaleMsg("checkAdvance");
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", facesMessages));
                return;
            }
            if (remark == null || remark.trim().length() < 5) {
                throw new VahanException(Util.getLocaleMsg("rmk_field"));
            }

            OwnerImpl ownerImpl = new OwnerImpl();
            Owner_dobj ownerDobj = ownerImpl.getOwnerDobj(ownerDetail);
            fancyNoDobj.setRemarks(remark);
            boolean flag = false;
            String replaceNo = "";
            if (advRegnCheck) {
                if (fancyNoDobj.getAssignAdvanceNO() != null) {
                    replaceNo = fancyNoDobj.getAssignAdvanceNO();
                    flag = fancyNoImpl.saveRegisterdNoToFancyNo(Util.getUserStateCode(), empCd, fancyNoDobj, ownerDobj, tmConfDobj, blnOldRegParam);
                }
            } else if (retCheck) {
                if (fancyNoDobj.getAssignRetainRegnNo() != null) {
                    replaceNo = fancyNoDobj.getAssignRetainRegnNo();
                    flag = fancyNoImpl.saveRegisterdNoToFancyNo(Util.getUserStateCode(), empCd, fancyNoDobj, ownerDobj, tmConfDobj, blnOldRegParam);
                }
            } else {
                return;
            }
            if (flag) {
                facesMessages = Util.getLocaleMsg("succesUpdate");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, facesMessages, facesMessages));
                PrimeFaces.current().ajax().update("msgDialog");
                PrimeFaces.current().executeScript("PF('messageDialog').show();");
            } else {

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
                PrimeFaces.current().ajax().update("msgDialog");
                PrimeFaces.current().executeScript("PF('messageDialog').show();");
                return;
            }
            render_vehicle_type = false;
            renderTab = false;
            disableRegnNo = true;
            renderBackButton = true;
            advanceFancyNorender = false;
            retAllowed = false;

        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vex.getMessage(), vex.getMessage()));
            return;
        } catch (Exception ee) {
            String mgs = Util.getLocaleSomthingMsg();
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, mgs, mgs));
        }

    }

    public String getEmpCd() {
        return empCd;
    }

    public void setEmpCd(String empCd) {
        this.empCd = empCd;
    }

    public String getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    public OwnerDetailsDobj getOwnerDetail() {
        return ownerDetail;
    }

    public void setOwnerDetail(OwnerDetailsDobj ownerDetail) {
        this.ownerDetail = ownerDetail;
    }

    public TmConfigurationDobj getTmConfDobj() {
        return tmConfDobj;
    }

    public void setTmConfDobj(TmConfigurationDobj tmConfDobj) {
        this.tmConfDobj = tmConfDobj;
    }

    public boolean isDisableRegnNo() {
        return disableRegnNo;
    }

    public void setDisableRegnNo(boolean disableRegnNo) {
        this.disableRegnNo = disableRegnNo;
    }

    public boolean isRender_vehicle_type() {
        return render_vehicle_type;
    }

    public void setRender_vehicle_type(boolean render_vehicle_type) {
        this.render_vehicle_type = render_vehicle_type;
    }

    public boolean isRenderBackButton() {
        return renderBackButton;
    }

    public void setRenderBackButton(boolean renderBackButton) {
        this.renderBackButton = renderBackButton;
    }

    public boolean isRenderTab() {
        return renderTab;
    }

    public void setRenderTab(boolean renderTab) {
        this.renderTab = renderTab;
    }

    public void advanceCheckListener() {
        if (advRegnCheck) {
            advRegnCheckDialogue = true;
            retCheck = false;
            setAdvRetCheckDialogue(false);


        }
    }

    public void retCheckListener() {
        if (retCheck) {
            advRegnCheck = false;
            setAdvRetCheckDialogue(true);
            //setAdvRetCheckDialogue(true);
            setAdvRegnCheckDialogue(false);

        }

    }

    public String getSelectedFancyRetnetion() {
        return selectedFancyRetnetion;
    }

    public void setSelectedFancyRetnetion(String selectedFancyRetnetion) {
        this.selectedFancyRetnetion = selectedFancyRetnetion;
    }

    public boolean isRetAllowed() {
        return retAllowed;
    }

    public void setRetAllowed(boolean retAllowed) {
        this.retAllowed = retAllowed;
    }

    public boolean isAdvRegnCheck() {
        return advRegnCheck;
    }

    public void setAdvRegnCheck(boolean advRegnCheck) {
        this.advRegnCheck = advRegnCheck;
    }

    public boolean isDisableAdvRegnCheck() {
        return disableAdvRegnCheck;
    }

    public void setDisableAdvRegnCheck(boolean disableAdvRegnCheck) {
        this.disableAdvRegnCheck = disableAdvRegnCheck;
    }

    public boolean isDisableRetCheck() {
        return disableRetCheck;
    }

    public void setDisableRetCheck(boolean disableRetCheck) {
        this.disableRetCheck = disableRetCheck;
    }

    public boolean isAdvRetCheckDialogue() {
        return advRetCheckDialogue;
    }

    public void setAdvRetCheckDialogue(boolean advRetCheckDialogue) {
        this.advRetCheckDialogue = advRetCheckDialogue;
    }

    public boolean isRetCheck() {
        return retCheck;
    }

    public void setRetCheck(boolean retCheck) {
        this.retCheck = retCheck;
    }

    public boolean isAdvRegnCheckDialogue() {
        return advRegnCheckDialogue;
    }

    public void setAdvRegnCheckDialogue(boolean advRegnCheckDialogue) {
        this.advRegnCheckDialogue = advRegnCheckDialogue;
    }

    public AdvanceRegnNo_dobj getAdvRegnNoDobj() {
        return advRegnNoDobj;
    }

    public void setAdvRegnNoDobj(AdvanceRegnNo_dobj advRegnNoDobj) {
        this.advRegnNoDobj = advRegnNoDobj;
    }

    public RetenRegnNo_dobj getRetenRegNoDobj() {
        return retenRegNoDobj;
    }

    public void setRetenRegNoDobj(RetenRegnNo_dobj retenRegNoDobj) {
        this.retenRegNoDobj = retenRegNoDobj;
    }

    public List getList_c_state() {
        return list_c_state;
    }

    public void setList_c_state(List list_c_state) {
        this.list_c_state = list_c_state;
    }

    public List getList_adv_district() {
        return list_adv_district;
    }

    public void setList_adv_district(List list_adv_district) {
        this.list_adv_district = list_adv_district;
    }

    public boolean isFancyRetention() {
        return fancyRetention;
    }

    public void setFancyRetention(boolean fancyRetention) {
        this.fancyRetention = fancyRetention;
    }

    public boolean isAdvanceFancyNorender() {
        return advanceFancyNorender;
    }

    public void setAdvanceFancyNorender(boolean advanceFancyNorender) {
        this.advanceFancyNorender = advanceFancyNorender;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public FancyNoReplaceDobj getFancyNoDobj() {
        return fancyNoDobj;
    }

    public void setFancyNoDobj(FancyNoReplaceDobj fancyNoDobj) {
        this.fancyNoDobj = fancyNoDobj;
    }

    public String getMessageFancyNo() {
        return messageFancyNo;
    }

    public void setMessageFancyNo(String messageFancyNo) {
        this.messageFancyNo = messageFancyNo;
    }

    /**
     * @return the oldRegnSize
     */
    public int getOldRegnSize() {
        return oldRegnSize;
    }

    /**
     * @param oldRegnSize the oldRegnSize to set
     */
    public void setOldRegnSize(int oldRegnSize) {
        this.oldRegnSize = oldRegnSize;
    }
}
