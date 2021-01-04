/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.audit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.bean.ExArmyBean;
import nic.vahan.form.dobj.AuditRecoveryDobj;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.dobj.ExArmyDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.impl.ExArmyImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Trailer_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.audit.EntryAuditRecoveryImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
/**
 *
 * @author Kunal Maiti
 */
@ManagedBean(name = "entryAuditRecovery")
@ViewScoped
public class EntryAuditRecovery implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(EntryAuditRecovery.class);
    private String regn_no;
    private boolean showRegList = false;
    private boolean showOwnerDetails = false;
    private ArrayList<OwnerDetailsDobj> regnNameList = null;
    private List listOwnerCatg = new ArrayList();
    private OwnerDetailsDobj ownerDetail;
    private String paraNo;
    private String year;
    private int amount;
    private String objection;
    private Date periodFrom;
    private Date periodTo;
    private String auditType;
    private boolean regnNoDisabled = false;
    private boolean showDtlsBtnDisabled = false;
    private EntryAuditRecoveryImpl entryAuditRecoveryImpl = new EntryAuditRecoveryImpl();

    @PostConstruct
    public void Init() {
        try {
//            fillmastercombo();
//            disabledatatablepanels();
            String user_catg = ServerUtil.getUserCategory(Long.parseLong(Util.getEmpCode()));
            boolean checkUserPerm = false;
            if (user_catg.equals("S") || (user_catg.equals("A"))) {
                checkUserPerm = true;
            }
            if (checkUserPerm == false) {
//                combofilllist.clear();
                JSFUtils.showMessage("YOU ARE NOT ALLOWED TO ENTER DATA IN MASTER FORM");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void saveDetails() {

        if ("".equals(auditType)) {
            JSFUtils.showMessage("Please Select Audit Type");
            return;
        }
        if (this.paraNo.trim() == null || this.paraNo.trim().equalsIgnoreCase("")) {
            JSFUtils.showMessage("Please Insert Para no");
            return;
        }
        if (this.year.trim() == null || this.year.trim().equalsIgnoreCase("")) {
            JSFUtils.showMessage("Please Insert Year");
            return;
        }
        if (year.length() != 4) {
            JSFUtils.showMessage(" Year should be 4 character length");
            return;
        }
        if (this.regn_no.trim() == null || this.regn_no.trim().equalsIgnoreCase("")) {
            JSFUtils.showMessage("Please Enter Valid Registration Number");
            return;
        }
        String empCode = Util.getEmpCode();
        Date currentdate = new Date();
        AuditRecoveryDobj auditRecoveryDobj = new AuditRecoveryDobj();
        auditRecoveryDobj.setRegn_no(ownerDetail.getRegn_no());
        auditRecoveryDobj.setState_cd(ownerDetail.getState_cd());
        auditRecoveryDobj.setOff_cd(ownerDetail.getOff_cd());
        auditRecoveryDobj.setAmount(amount);
        auditRecoveryDobj.setAudit_ty(auditType);
        auditRecoveryDobj.setDeal_cd(empCode);
        auditRecoveryDobj.setFrom_dt(periodFrom);
        auditRecoveryDobj.setObjection(objection);
        auditRecoveryDobj.setOp_date(currentdate);
        auditRecoveryDobj.setPara_year(year);
        auditRecoveryDobj.setPaid_by(Util.getEmpCode());

        auditRecoveryDobj.setPara_no(paraNo);
//        auditRecoveryDobj.setPay_dt(currentdate);
//        auditRecoveryDobj.setRcpt_no("wer");
//        auditRecoveryDobj.setReconcil_flag("t");
        auditRecoveryDobj.setTo_dt(periodTo);

        String val = EntryAuditRecoveryImpl.saveAuditRecoveryDtls(auditRecoveryDobj);
        if ("true".equals(val)) {
            reset();
            JSFUtils.showMessage("Audit Recovery details saved successfully");
        } else {
            JSFUtils.showMessage("Audit Recovery details saved fail/Record Already Exist");
        }
    }

    public void reset() {
        setRegn_no("");
        showRegList = false;
        showOwnerDetails = false;
        regnNameList = null;
        showDtlsBtnDisabled = false;
        setOwnerDetail(null);
        setParaNo("");
        setYear("");
        setAmount(0);
        setObjection("");
        setPeriodFrom(null);
        setPeriodTo(null);
        setAuditType("");
        regnNoDisabled = false;
    }

    public void showAllRegnNos() {
        if (this.regn_no.trim() == null || this.regn_no.trim().equalsIgnoreCase("")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Please Enter Valid Registration Number"));
            return;
        }
        try {
            OwnerImpl owner_Impl = new OwnerImpl();
            setRegnNameList((ArrayList<OwnerDetailsDobj>) owner_Impl.getAllRegnNoFromAllStatesAndRto(this.regn_no.trim()));
            if (getRegnNameList().isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Registration No. Not Found"));
                return;
            }
            if (getRegnNameList().size() > 1) {
                setShowRegList(true);
                setRegnNoDisabled(true);
                setShowDtlsBtnDisabled(true);

            } else {
                for (int i = 0; i < getRegnNameList().size(); i++) {
                    showDetails(getRegnNameList().get(i).getState_cd(), getRegnNameList().get(i).getOff_cd());

                }

            }

        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }

    public void showDetails(String stateCd, int offCd) {
        Exception ex = null;
        Trailer_Impl trailer_Impl = new Trailer_Impl();

        if (this.regn_no.trim() == null || this.regn_no.trim().equalsIgnoreCase("")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Please Enter Valid Registration Number"));
            return;
        }
        try {
            //Master Filler for Owner Category
            String[][] data = MasterTableFiller.masterTables.VM_OWCATG.getData();
            for (int i = 0; i < data.length; i++) {
                listOwnerCatg.add(new SelectItem(data[i][0], data[i][1]));
            }

            setOwnerDetail(entryAuditRecoveryImpl.getOwnerDetails(this.regn_no.trim()));
            if (getOwnerDetail() == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Registration Number Not Found"));
                return;
            } else {
                setShowOwnerDetails(true);

            }

            Long user_cd = Long.parseLong(Util.getEmpCode());
            String userCatg = Util.getUserCategory();
            if (userCatg != null) {
                if (userCatg.equals(TableConstants.User_Dealer)) {
                    Map makerAndDealerDetail = OwnerImpl.getDealerDetail(user_cd);
                    if (!ownerDetail.getDealer_cd().equals(makerAndDealerDetail.get("dealer_cd"))) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Not a valid Dealer to see the registered vehicle Details."));
                        return;
                    }
                }
            }

            //for Owner Identification Fields disallow typing
            if (getOwnerDetail().getOwnerIdentity() != null) {
                getOwnerDetail().getOwnerIdentity().setFlag(true);
                getOwnerDetail().getOwnerIdentity().setMobileNoEditable(true);
                getOwnerDetail().getOwnerIdentity().setOwnerCatgEditable(true);
            }

        } catch (VahanException ve) {
            ex = ve;
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", ex.getMessage()));
        } catch (Exception exp) {
            ex = exp;
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", TableConstants.SomthingWentWrong));
        }
    }

    /**
     * @return the regn_no
     */
    public String getRegn_no() {
        return regn_no;
    }

    /**
     * @param regn_no the regn_no to set
     */
    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    /**
     * @return the regnNameList
     */
    public ArrayList<OwnerDetailsDobj> getRegnNameList() {
        return regnNameList;
    }

    /**
     * @param regnNameList the regnNameList to set
     */
    public void setRegnNameList(ArrayList<OwnerDetailsDobj> regnNameList) {
        this.regnNameList = regnNameList;
    }

    /**
     * @return the showRegList
     */
    public boolean isShowRegList() {
        return showRegList;
    }

    /**
     * @param showRegList the showRegList to set
     */
    public void setShowRegList(boolean showRegList) {
        this.showRegList = showRegList;
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
     * @return the showOwnerDetails
     */
    public boolean isShowOwnerDetails() {
        return showOwnerDetails;
    }

    /**
     * @param showOwnerDetails the showOwnerDetails to set
     */
    public void setShowOwnerDetails(boolean showOwnerDetails) {
        this.showOwnerDetails = showOwnerDetails;
    }

    /**
     * @return the paraNo
     */
    public String getParaNo() {
        return paraNo;
    }

    /**
     * @param paraNo the paraNo to set
     */
    public void setParaNo(String paraNo) {
        this.paraNo = paraNo;
    }

    /**
     * @return the periodFrom
     */
    public Date getPeriodFrom() {
        return periodFrom;
    }

    /**
     * @param periodFrom the periodFrom to set
     */
    public void setPeriodFrom(Date periodFrom) {
        this.periodFrom = periodFrom;
    }

    /**
     * @return the periodTo
     */
    public Date getPeriodTo() {
        return periodTo;
    }

    /**
     * @param periodTo the periodTo to set
     */
    public void setPeriodTo(Date periodTo) {
        this.periodTo = periodTo;
    }

    /**
     * @return the auditType
     */
    public String getAuditType() {
        return auditType;
    }

    /**
     * @param auditType the auditType to set
     */
    public void setAuditType(String auditType) {
        this.auditType = auditType;
    }

    /**
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * @return the objection
     */
    public String getObjection() {
        return objection;
    }

    /**
     * @param objection the objection to set
     */
    public void setObjection(String objection) {
        this.objection = objection;
    }

    /**
     * @return the year
     */
    public String getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * @return the regnNoDisabled
     */
    public boolean isRegnNoDisabled() {
        return regnNoDisabled;
    }

    /**
     * @param regnNoDisabled the regnNoDisabled to set
     */
    public void setRegnNoDisabled(boolean regnNoDisabled) {
        this.regnNoDisabled = regnNoDisabled;
    }

    /**
     * @return the showDtlsBtnDisabled
     */
    public boolean isShowDtlsBtnDisabled() {
        return showDtlsBtnDisabled;
    }

    /**
     * @param showDtlsBtnDisabled the showDtlsBtnDisabled to set
     */
    public void setShowDtlsBtnDisabled(boolean showDtlsBtnDisabled) {
        this.showDtlsBtnDisabled = showDtlsBtnDisabled;
    }
}
