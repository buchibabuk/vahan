/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.bean.OwnerBean;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.permit.PermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.SurrenderPermitRevertDobj;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.permit.PermitOwnerDetailImpl;
import nic.vahan.form.impl.permit.SurrenderPermitRevertImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author ankur
 */
@ManagedBean(name = "surren_goods_permit_revert")
@ViewScoped
public class SurrenderPermitRevertBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(SurrenderPermitBean.class);
    @ManagedProperty(value = "#{pmt_owner_dtls}")
    private PermitOwnerDetailBean ownerBean;
    @ManagedProperty(value = "#{Permit_Dtls_bean}")
    private PermitDetailBean permit_Dtls_bean;
    private OwnerBean owner_bean;
    private Owner_dobj ownerDobj;
    PermitOwnerDetailDobj owner_dobj = null;
    PermitOwnerDetailImpl ownerImpl = null;
    PermitDetailDobj pmt_dobj = null;
    private String new_regn_no = "";
    private boolean renderNewVeh = false;
    private boolean disableBut = true;
    private String header_msg = null;
    private String regn_number = null;
    private String regn_no;
    private String pmt_type_desc;
    private String purpose;
    private boolean renderRegdetails = false;
    private String app_no_msg = "";
    private String pur_cd = "";
    private int pmt_type;
    private Date surrender_dt = new Date();
    private List purpose_list = new ArrayList();
    FacesMessage message = null;
    private PassengerPermitDetailDobj selectedValue = null;
    private boolean newRegnNoDisable = false;
    private OwnerDetailsDobj ownerDetail;
    SurrenderPermitRevertDobj dobj = new SurrenderPermitRevertDobj();

    public SurrenderPermitRevertBean() {
        String[][] data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
        purpose_list.add(new SelectItem("-1", "SELECT"));
        for (int i = 0; i < data.length; i++) {
            purpose_list.add(new SelectItem(data[i][0], data[i][1]));
        }
    }

    @PostConstruct
    public void init() {
        header_msg = "Surrender Goods/Passenger Permit Revert";
    }

    public void reset() {
        regn_no = "";
        pmt_type_desc = "";
        purpose = "-1";
        surrender_dt = null;
        pmt_type_desc = "";
    }

    // rev
    public void permitDetail() {
        try {
            OwnerImpl owner_Impl = new OwnerImpl();

            List<OwnerDetailsDobj> ownerDetailsDobjList = owner_Impl.getOwnerDetailsList(regn_number.toUpperCase().trim(), null);
            ownerDetailsDobjList = owner_Impl.getStateWiseOwnerList(ownerDetailsDobjList, Util.getUserStateCode());
            if (ownerDetailsDobjList != null && ownerDetailsDobjList.size() == 1) {
                ownerDetail = ownerDetailsDobjList.get(0);
            }
            if (ownerDetail == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Registration Number Not Found in Current Office"));
                return;
            }
            dobj = SurrenderPermitRevertImpl.getToatalNoOfPermitList(regn_number.toUpperCase());
            if (dobj == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Registration Number Not Found Of Surrender Case"));
                setRenderRegdetails(false);
                return;
            } else {
                setPurpose(dobj.getPaction_code());
                setSurrender_dt(dobj.getOp_dt());
                setRenderRegdetails(true);
            }
            showPermitDetails(dobj.getPmt_no().toUpperCase(), Integer.parseInt(dobj.getPaction_code()), ownerDetail);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    //rev
    public void showPermitDetails(String pmt_no, int pur_cd, OwnerDetailsDobj ownerDetail) {
        try {
            ownerImpl = new PermitOwnerDetailImpl();
            permit_Dtls_bean.permitComponentReadOnly(true);
            pmt_dobj = SurrenderPermitRevertImpl.getPermitdetailsForCancellation(regn_number.toUpperCase());
            PermitOwnerDetailDobj dobj = ownerBean.getOwnerDetailsDobj(ownerDetail);
            if (pmt_dobj != null && dobj.getState_cd().equalsIgnoreCase(Util.getUserStateCode())) {
                ownerBean.setValueinDOBJ(dobj);
                permit_Dtls_bean.set_Permit_Dtls_dobj_to_bean(pmt_dobj);
                regn_no = regn_number.toUpperCase();
                disableBut = false;
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Please Enter Recipt Number", "Please Enter Valid Registration  Number!");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                return;
            }
            PrimeFaces.current().ajax().update("All_detail");
            PrimeFaces.current().ajax().update("panalSave");
        } catch (Exception e) {
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Enter Valid Registration  Number!", "Please Enter Valid Registration  Number!");
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void getPermitDetailThroughDataTable() {
        showPermitDetails(selectedValue.getPmt_no(), Integer.valueOf(selectedValue.getPaction_code()), ownerDetail);
    }

    //rev
    public void saveSurrenderDetailsRevert() {
        try {
            String pendingApplication = ServerUtil.applicationStatusForPermit(regn_no.toUpperCase(), Util.getUserStateCode());
            if (CommonUtils.isNullOrBlank(pendingApplication)) {
                boolean flag = SurrenderPermitRevertImpl.saveSurenderPermitRevertDetails(regn_no.toUpperCase(), dobj, pmt_dobj);
                if (flag) {
                    printDialogBox("Surrender Details Revert Successfully");
                } else {
                    printDialogBox(TableConstants.SomthingWentWrong);
                }
            } else {
                printDialogBox(pendingApplication);
            }
        } catch (Exception e) {
            LOGGER.error(new VahanException("saveSurrenderDetailsRevert() Some prob to save data" + e.getMessage()));
        }
    }

    public PermitDetailDobj getPmt_dobj() {
        return pmt_dobj;
    }

    public void setPmt_dobj(PermitDetailDobj pmt_dobj) {
        this.pmt_dobj = pmt_dobj;
    }

    public String getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(String pur_cd) {
        this.pur_cd = pur_cd;
    }

    public boolean isDisableBut() {
        return disableBut;
    }

    public void setDisableBut(boolean disableBut) {
        this.disableBut = disableBut;
    }

    public String getApp_no_msg() {
        return app_no_msg;
    }

    public void setApp_no_msg(String app_no_msg) {
        this.app_no_msg = app_no_msg;
    }

    public void printDialogBox(String app_no) {
        if (("").equalsIgnoreCase(app_no)) {
            setApp_no_msg("Application Number is not genrated");
            PrimeFaces.current().executeScript("PF('appNum').show();");
        } else {
            setApp_no_msg(app_no);
            PrimeFaces.current().executeScript("PF('appNum').show();");
        }
    }

    public void showApplPendingDialogBox(String str) {
        setApp_no_msg(str);
        PrimeFaces.current().executeScript("PF('appNum').show();");
    }

    public String getNew_regn_no() {
        return new_regn_no;
    }

    public void setNew_regn_no(String new_regn_no) {
        this.new_regn_no = new_regn_no;
    }

    public void newRegistrationProcess() {
        PrimeFaces.current().executeScript("PF('newinfo1').hide()");
        newRegnNoDisable = true;
        disableBut = false;
    }

    public void editVehicleLink() {
        newRegnNoDisable = false;
        disableBut = true;
    }

    public boolean isRenderNewVeh() {
        return renderNewVeh;
    }

    public void setRenderNewVeh(boolean renderNewVeh) {
        this.renderNewVeh = renderNewVeh;
    }

    public PermitOwnerDetailDobj getOwner_dobj() {
        return owner_dobj;
    }

    public void setOwner_dobj(PermitOwnerDetailDobj owner_dobj) {
        this.owner_dobj = owner_dobj;
    }

    public PermitOwnerDetailImpl getOwnerImpl() {
        return ownerImpl;
    }

    public void setOwnerImpl(PermitOwnerDetailImpl ownerImpl) {
        this.ownerImpl = ownerImpl;
    }

    public PermitOwnerDetailBean getOwnerBean() {
        return ownerBean;
    }

    public void setOwnerBean(PermitOwnerDetailBean ownerBean) {
        this.ownerBean = ownerBean;
    }

    public String getHeader_msg() {
        return header_msg;
    }

    public void setHeader_msg(String header_msg) {
        this.header_msg = header_msg;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public int getPmt_type() {
        return pmt_type;
    }

    public void setPmt_type(int pmt_type) {
        this.pmt_type = pmt_type;
    }

    public String getPmt_type_desc() {
        return pmt_type_desc;
    }

    public void setPmt_type_desc(String pmt_type_desc) {
        this.pmt_type_desc = pmt_type_desc;
    }

    public Date getSurrender_dt() {
        setSurrender_dt(new Date());
        return surrender_dt;
    }

    public void setSurrender_dt(Date surrender_dt) {
        this.surrender_dt = surrender_dt;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getRegn_number() {
        return regn_number;
    }

    public void setRegn_number(String regn_number) {
        this.regn_number = regn_number;
    }

    public PermitDetailBean getPermit_Dtls_bean() {
        return permit_Dtls_bean;
    }

    public void setPermit_Dtls_bean(PermitDetailBean permit_Dtls_bean) {
        this.permit_Dtls_bean = permit_Dtls_bean;
    }

    public OwnerBean getOwner_bean() {
        return owner_bean;
    }

    public void setOwner_bean(OwnerBean owner_bean) {
        this.owner_bean = owner_bean;
    }

    public Owner_dobj getOwnerDobj() {
        return ownerDobj;
    }

    public void setOwnerDobj(Owner_dobj ownerDobj) {
        this.ownerDobj = ownerDobj;
    }

    public List getPurpose_list() {
        return purpose_list;
    }

    public void setPurpose_list(List purpose_list) {
        this.purpose_list = purpose_list;
    }

    public PassengerPermitDetailDobj getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(PassengerPermitDetailDobj selectedValue) {
        this.selectedValue = selectedValue;
    }

    public boolean isNewRegnNoDisable() {
        return newRegnNoDisable;
    }

    public void setNewRegnNoDisable(boolean newRegnNoDisable) {
        this.newRegnNoDisable = newRegnNoDisable;
    }

    public boolean isRenderRegdetails() {
        return renderRegdetails;
    }

    public void setRenderRegdetails(boolean renderRegdetails) {
        this.renderRegdetails = renderRegdetails;
    }
}
