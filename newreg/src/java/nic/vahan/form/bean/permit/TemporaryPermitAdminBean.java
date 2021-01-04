/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.server.CommonUtils;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.user_mgmt.dobj.UserAuthorityDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.dobj.permit.TemporaryPermitAdminDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.PassengerPermitDetailImpl;
import nic.vahan.form.impl.permit.PermitOwnerDetailImpl;
import nic.vahan.form.impl.permit.TemporaryPermitAdminImpl;
import org.apache.log4j.Logger;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.form.impl.permit.PermitDetailImpl;
import nic.vahan.server.ServerUtil;
import org.primefaces.PrimeFaces;

/**
 *
 * @author MukulRaiDutta
 */
@ManagedBean(name = "tempAdminBean")
@ViewScoped
public class TemporaryPermitAdminBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(TemporaryPermitAdminBean.class);
    private TemporaryPermitAdminDobj tpDobj;
    private TemporaryPermitAdminDobj prvTpDobj;
    private TemporaryPermitAdminImpl tpImpl;
    @ManagedProperty(value = "#{pmt_owner_dtls}")
    private PermitOwnerDetailBean ownerBean;
    private PermitOwnerDetailDobj ownerDobj;
    private PermitOwnerDetailImpl ownerImpl;
    private String pmtNo_regNO = "";
    private List purCdList = new ArrayList();
    private List pmt_type_list = new ArrayList();
    private List pmtCategory_list = new ArrayList();
    private int permitIssuingAuthCode = 0;
    Map<String, String> confige = null;
    Map<String, String> tempConfige = null;
    Map<String, String> splConfige = null;
    private boolean temp_pmt_type;
    private boolean route_status;
    private boolean renew_temp;
    UserAuthorityDobj authorityDobj = null;
    private List<TemporaryPermitAdminDobj> compairValue = new ArrayList<>();
    private String errorMsg = "";

    public TemporaryPermitAdminBean() {
        permitIssuingAuthCode = CommonPermitPrintImpl.getOffCdPermissionByOfficer(Util.getUserOffCode(), Util.getUserStateCode());
        confige = CommonPermitPrintImpl.getVmPermitStateConfiguration(Util.getUserStateCode());
        tempConfige = CommonPermitPrintImpl.getTmTempPmtStateConfiguration(Util.getUserStateCode());
        splConfige = CommonPermitPrintImpl.getTmSpecialPmtStateConfiguration(Util.getUserStateCode());
        temp_pmt_type = Boolean.parseBoolean(tempConfige.get("temp_pmt_type").toString());
        route_status = Boolean.parseBoolean(tempConfige.get("temp_route_area").toString());
        renew_temp = Boolean.parseBoolean(tempConfige.get("temp_renew_pmt").toString());
        String[][] data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
        for (int i = 0; i < data.length; i++) {
            if (data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TEMP_PUR_CD))
                    || data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_SPECIAL_PUR_CD))
                    || (renew_temp && data[i][0].equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RENEW_TEMP_PUR_CD)))) {
                purCdList.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
        authorityDobj = Util.getUserAuthority();
    }

    public void onTransfer() {
        PassengerPermitDetailImpl passImp = new PassengerPermitDetailImpl();
        tpDobj.setVia(passImp.getRouteVia(tpDobj.getRouteManage().getTarget(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd()));
    }

    public void onSelectPermitType() {
        pmtCategory_list.clear();
        try {
            String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
            if (authorityDobj != null) {
                if (authorityDobj.getSelectedPermitCatg().size() < 1) {
                    throw new VahanException("You are not authorized to modify.");
                } else if (authorityDobj.getSelectedPermitCatg().size() == 1) {
                    for (Object obj : authorityDobj.getSelectedPermitCatg()) {
                        if (String.valueOf(obj).equalsIgnoreCase("ANY")) {
                            for (int i = 0; i < data.length; i++) {
                                if (data[i][0].equalsIgnoreCase(Util.getUserStateCode())
                                        && data[i][3].equalsIgnoreCase(String.valueOf(tpDobj.getPmt_type()))) {
                                    pmtCategory_list.add(new SelectItem(data[i][1], data[i][2]));
                                }
                            }
                        }
                    }
                } else {
                    for (int j = 0; j < data.length; j++) {
                        for (Object obj : authorityDobj.getSelectedPermitCatg()) {
                            if (data[j][0].equalsIgnoreCase(Util.getUserStateCode())
                                    && Integer.parseInt(data[j][3]) == tpDobj.getPmt_type()
                                    && data[j][1].equalsIgnoreCase(String.valueOf(obj))) {
                                pmtCategory_list.add(new SelectItem(data[j][1], data[j][2]));
                            }
                        }
                    }
                }
                if (pmtCategory_list.size() <= 0) {
                    throw new VahanException("You are not authorized to modify.");
                }
            }
        } catch (VahanException e) {
            JSFUtils.showMessage(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("onSelectPermitType" + e.toString() + "-" + e.getStackTrace()[0]);
            JSFUtils.showMessage(TableConstants.SomthingWentWrong);
        }
    }

    public void pmtGetDetails() {
        tpImpl = new TemporaryPermitAdminImpl();
        tpDobj = new TemporaryPermitAdminDobj();
        FacesMessage message = null;
        try {
            ownerImpl = new PermitOwnerDetailImpl();
            tpDobj = tpImpl.getPmtDtlsThroughVTTEMPPERMIT(pmtNo_regNO.toUpperCase(), route_status, permitIssuingAuthCode);
            if (tpDobj != null && !CommonUtils.isNullOrBlank(tpDobj.getRegn_no())) {
                ownerDobj = ownerImpl.set_Owner_appl_db_to_dobj(tpDobj.getRegn_no().toUpperCase(), null);
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Permit Detail Not Found !");
                FacesContext.getCurrentInstance().addMessage("", message);
                return;
            }
            Map<String, String> OffAllotResult = PermitDetailImpl.getOffAllotmentResult(Util.getUserStateCode(), Util.getUserOffCode(), ownerDobj.getOff_cd());
            if (ownerDobj != null && "true".equalsIgnoreCase(OffAllotResult.get("offAllowed"))) {
                ownerBean.setValueinDOBJ(ownerDobj);
                if (temp_pmt_type) {
                    pmt_type_list.clear();
                    fillCombo();
                    if (tpDobj.getPmt_catg() != 0) {
                        onSelectPermitType();
                    }
                }
                prvTpDobj = (TemporaryPermitAdminDobj) tpDobj.clone();
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Vehicle/Permit Detail Not Found");
                FacesContext.getCurrentInstance().addMessage("", message);
            }
        } catch (Exception e) {
            LOGGER.error("Regn_no:- " + pmtNo_regNO.toUpperCase() + " / " + e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void fillCombo() throws VahanException {
        String transportCatg = "";
        pmt_type_list.clear();
        String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
        for (int i = 0; i < data.length; i++) {
            if (ownerDobj.getVh_class() == Integer.valueOf(data[i][0])) {
                transportCatg = data[i][3];
                break;
            }
        }
        if (CommonUtils.isNullOrBlank(transportCatg)) {
            throw new VahanException("This Vehicle is not Transpost Vehicle");
        } else if (authorityDobj == null) {
            throw new VahanException("User not authorized to any modify");
        } else {
            pmt_type_list.clear();
            data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
            if (authorityDobj.getPermitType().size() == 1) {
                for (Object obj : authorityDobj.getPermitType()) {
                    if (String.valueOf(obj).equalsIgnoreCase("ANY")) {
                        for (int i = 0; i < data.length; i++) {
                            if (data[i][2].equalsIgnoreCase(transportCatg)) {
                                pmt_type_list.add(new SelectItem(data[i][0], data[i][1]));
                            }
                        }
                    } else {
                        for (int i = 0; i < data.length; i++) {
                            if (data[i][2].equalsIgnoreCase(transportCatg) && data[i][0].equalsIgnoreCase(String.valueOf(obj))) {
                                pmt_type_list.add(new SelectItem(data[i][0], data[i][1]));
                                break;
                            }
                        }
                    }
                }
            } else {
                for (int i = 0; i < data.length; i++) {
                    for (Object obj : authorityDobj.getPermitType()) {
                        if (data[i][2].equalsIgnoreCase(transportCatg) && data[i][0].equalsIgnoreCase(String.valueOf(obj))) {
                            pmt_type_list.add(new SelectItem(data[i][0], data[i][1]));
                        }
                    }
                }
            }
            if (pmt_type_list.size() <= 0) {
                throw new VahanException("You are not authorized to modify.");
            }
        }
    }

    public void checkRegnNo() {
        FacesMessage message = null;
        if (CommonUtils.isNullOrBlank(prvTpDobj.getRegn_no())) {
            prvTpDobj.setRegn_no("");
        }
        if (!prvTpDobj.getRegn_no().equalsIgnoreCase(tpDobj.getRegn_no())) {
            if (tpImpl.checkRegn_Pmt_Appl_NoValidOrNot(tpDobj.getRegn_no(), "", "")) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "", "This Vehicle No. already exist in Table");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                JSFUtils.showMessagesInDialog("Info", "This Regn No. already exist in Table", FacesMessage.SEVERITY_INFO);
            }
        }
    }

    public void checkPmtNo() {
        FacesMessage message = null;
        if (CommonUtils.isNullOrBlank(prvTpDobj.getPmt_no())) {
            prvTpDobj.setPmt_no("");
        }
        if (!prvTpDobj.getPmt_no().equalsIgnoreCase(tpDobj.getPmt_no())) {
            if (tpImpl.checkRegn_Pmt_Appl_NoValidOrNot("", tpDobj.getPmt_no(), "")) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "", "This Permit No. already exist in Table");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                JSFUtils.showMessagesInDialog("Info", "This Permit No. already exist in Table", FacesMessage.SEVERITY_INFO);
            }
        }
    }

    public void checkApplNo() {
        FacesMessage message = null;
        if (CommonUtils.isNullOrBlank(prvTpDobj.getAppl_no())) {
            prvTpDobj.setAppl_no("");
        }
        if (!prvTpDobj.getAppl_no().equalsIgnoreCase(tpDobj.getAppl_no())) {
            if (tpImpl.checkRegn_Pmt_Appl_NoValidOrNot("", "", tpDobj.getAppl_no())) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "", "This Application No. already exist in Table");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                JSFUtils.showMessagesInDialog("Info", "This Application No. already exist in Table", FacesMessage.SEVERITY_INFO);
            }
        }
    }

    public void compair(String purpose, String oldValue, String newValue, List<TemporaryPermitAdminDobj> compairValue) {
        if (!oldValue.equalsIgnoreCase(newValue)) {
            TemporaryPermitAdminDobj dobj = new TemporaryPermitAdminDobj();
            dobj.setPurpose(purpose);
            dobj.setOldValue(oldValue);
            dobj.setNewValue(newValue);
            compairValue.add(dobj);
        }
    }

    public void compairList() {
        String msg = checkDetails();
        String oldValue = "";
        String newValue = "";
        if (!CommonUtils.isNullOrBlank(msg)) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", msg);
            FacesContext.getCurrentInstance().addMessage("Id", message);
            return;
        }
        compairValue.clear();
        if (CommonUtils.isNullOrBlank(prvTpDobj.getRegn_no())) {
            prvTpDobj.setRegn_no("");
        }
        compair("Vehicle No", prvTpDobj.getRegn_no(), tpDobj.getRegn_no().toUpperCase(), compairValue);

        if (CommonUtils.isNullOrBlank(prvTpDobj.getPmt_no())) {
            prvTpDobj.setPmt_no("");
        }
        compair("Permit No", prvTpDobj.getPmt_no(), tpDobj.getPmt_no().toUpperCase(), compairValue);

        if (CommonUtils.isNullOrBlank(prvTpDobj.getRcpt_no())) {
            prvTpDobj.setRcpt_no("");
        }
        compair("Receipt No", prvTpDobj.getRcpt_no(), tpDobj.getRcpt_no().toUpperCase(), compairValue);

        if (CommonUtils.isNullOrBlank(prvTpDobj.getAppl_no())) {
            prvTpDobj.setAppl_no("");
        }
        compair("Application No.", prvTpDobj.getAppl_no(), tpDobj.getAppl_no().toUpperCase(), compairValue);

        if (prvTpDobj.getValid_from() == null) {
            compair("Valid From", "", CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(tpDobj.getValid_from()).toUpperCase(), compairValue);
        } else {
            compair("Valid From", CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(prvTpDobj.getValid_from()), CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(tpDobj.getValid_from()).toUpperCase(), compairValue);
        }

        if (prvTpDobj.getValid_upto() == null) {
            compair("Valid Upto", "", CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(tpDobj.getValid_upto()).toUpperCase(), compairValue);
        } else {
            compair("Valid Upto", CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(prvTpDobj.getValid_upto()), CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(tpDobj.getValid_upto()).toUpperCase(), compairValue);
        }

        if (prvTpDobj.getIssue_dt() == null) {
            compair("Permit Issue Date", "", CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(tpDobj.getIssue_dt()).toUpperCase(), compairValue);
        } else {
            compair("Permit Issue Date", CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(prvTpDobj.getIssue_dt()), CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(tpDobj.getIssue_dt()).toUpperCase(), compairValue);
        }
        compair("Goods to Carry", prvTpDobj.getGoods_to_carry(), tpDobj.getGoods_to_carry().toUpperCase(), compairValue);
        compair("Route From", prvTpDobj.getRoute_fr(), tpDobj.getRoute_fr().toUpperCase(), compairValue);
        compair("Route To", prvTpDobj.getRoute_to(), tpDobj.getRoute_to().toUpperCase(), compairValue);
        compair("Route Description", prvTpDobj.getVt_temp_Route_via(), tpDobj.getVt_temp_Route_via().toUpperCase(), compairValue);
        if (temp_pmt_type) {
            compair("Permit Category", String.valueOf(prvTpDobj.getPmt_catg()), String.valueOf(tpDobj.getPmt_catg()), compairValue);
            compair("Permit Type", String.valueOf(prvTpDobj.getPmt_type()), String.valueOf(tpDobj.getPmt_type()), compairValue);
        }
        for (PermitRouteList s : tpDobj.getRegionActionTarget()) {
            oldValue = oldValue + s.getKey() + ",";
        }
        for (Object ss : tpDobj.getRegionManage().getTarget()) {
            newValue = newValue + (String) ss + ",";
        }
        compair("Area Details", oldValue, newValue, compairValue);
        oldValue = "";
        newValue = "";
        for (PermitRouteList s : tpDobj.getRouteActionTarget()) {
            oldValue = oldValue + s.getKey() + ",";
        }
        for (Object ss : tpDobj.getRouteManage().getTarget()) {
            newValue = newValue + (String) ss + ",";
        }
        compair("Route Details", oldValue, newValue, compairValue);
        PrimeFaces.current().executeScript("PF('compairVar').show();");
        PrimeFaces.current().ajax().update("compair");
    }

    public String saveTempPmtDetails() {
        FacesMessage message = null;
        String returnUrl = "";
        String region = "";
        try {
            String pendingApplication = ServerUtil.applicationStatusForPermit(tpDobj.getRegn_no().toUpperCase(), Util.getUserStateCode());
            if (CommonUtils.isNullOrBlank(pendingApplication)) {
                List area = tpDobj.getRegionManage().getTarget();
                for (Object s : area) {
                    region = region + s + ",";
                }
                tpDobj.setRegion_covered(region);
                boolean flag = tpImpl.saveDetailsInVtTables(tpDobj, prvTpDobj, tpDobj.getRouteManage().getTarget(), compairValue);
                if (flag) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Successfully Save your deails");
                    PrimeFaces.current().executeScript("PF('compairVar').hide();");
                    FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                    returnUrl = "/ui/permit/formTemporaryPermitAdminForm.xhtml?faces-redirect=true";
                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Data  not Saved");
                    PrimeFaces.current().executeScript("PF('compairVar').hide();");
                    FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                }
            } else {
                setErrorMsg(pendingApplication);
                PrimeFaces.current().ajax().update("pendingApplicationID");
                PrimeFaces.current().executeScript("PF('pendingAppication').show();");
            }
        } catch (VahanException e) {
            PrimeFaces.current().executeScript("PF('compairVar').hide();");
            JSFUtils.showMessagesInDialog("Info", e.getMessage(), FacesMessage.SEVERITY_INFO);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Info", TableConstants.SomthingWentWrong, FacesMessage.SEVERITY_INFO);
        }
        return returnUrl;
    }

    public String checkDetails() {
        String msg = "";
        if (CommonUtils.isNullOrBlank(tpDobj.getRegn_no())) {
            msg = "Please enter the Registration Number";
        }
        if (CommonUtils.isNullOrBlank(tpDobj.getAppl_no())) {
            msg = "Please enter the Application Number";
        }
        if (CommonUtils.isNullOrBlank(tpDobj.getPmt_no())) {
            msg = "Please enter the Permit Number";
        }
        if (CommonUtils.isNullOrBlank(tpDobj.getRcpt_no())) {
            msg = "Please enter the Receipt Number";
        }
        if (tpDobj.getPmt_type() == -1 && temp_pmt_type) {
            msg = "Please select any Permit Type";
        }
        if (tpDobj.getIssue_dt() == null) {
            msg = "Please enter the Issue Date";
        }
        if (tpDobj.getValid_from() == null) {
            msg = "Please enter the Valid From";
        }
        if (tpDobj.getValid_upto() == null) {
            msg = "Please enter the Valid Upto";
        }
        return msg;
    }

    public boolean isTemp_pmt_type() {
        return temp_pmt_type;
    }

    public void setTemp_pmt_type(boolean temp_pmt_type) {
        this.temp_pmt_type = temp_pmt_type;
    }

    public boolean isRoute_status() {
        return route_status;
    }

    public void setRoute_status(boolean route_status) {
        this.route_status = route_status;
    }

    public List getPurCdList() {
        return purCdList;
    }

    public void setPurCdList(List purCdList) {
        this.purCdList = purCdList;
    }

    public List getPmt_type_list() {
        return pmt_type_list;
    }

    public void setPmt_type_list(List pmt_type_list) {
        this.pmt_type_list = pmt_type_list;
    }

    public List getPmtCategory_list() {
        return pmtCategory_list;
    }

    public void setPmtCategory_list(List pmtCategory_list) {
        this.pmtCategory_list = pmtCategory_list;
    }

    public String getPmtNo_regNO() {
        return pmtNo_regNO;
    }

    public void setPmtNo_regNO(String pmtNo_regNO) {
        this.pmtNo_regNO = pmtNo_regNO;
    }

    public TemporaryPermitAdminDobj getTpDobj() {
        return tpDobj;
    }

    public void setTpDobj(TemporaryPermitAdminDobj tpDobj) {
        this.tpDobj = tpDobj;
    }

    public PermitOwnerDetailBean getOwnerBean() {
        return ownerBean;
    }

    public void setOwnerBean(PermitOwnerDetailBean ownerBean) {
        this.ownerBean = ownerBean;
    }

    public List<TemporaryPermitAdminDobj> getCompairValue() {
        return compairValue;
    }

    public void setCompairValue(List<TemporaryPermitAdminDobj> compairValue) {
        this.compairValue = compairValue;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
