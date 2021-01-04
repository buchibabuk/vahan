/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.TaxInstallmentConfigureManualDobj;
import org.apache.log4j.Logger;
import nic.vahan.form.dobj.BlackListedVehicleDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.impl.BlackListedVehicleImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.TaxInstallmentConfigureManualImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.primefaces.PrimeFaces;

/**
 *
 * @author acer
 */
@ManagedBean(name = "taxInstallmentConfigureManualBean")
@ViewScoped
public class TaxInstallmentConfigureManualBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(TaxInstallmentConfigureManualBean.class);
    private TaxInstallmentConfigureManualDobj taxInstallmentConfigureManualDobj = new TaxInstallmentConfigureManualDobj();
    private TaxInstallmentConfigureManualDobj taxInstallmentConfigureManualDobjDel = new TaxInstallmentConfigureManualDobj();
    private boolean taxInstallmentPanel = false;
    private String regnNo;
    private Owner_dobj ownerDobj;
    private OwnerDetailsDobj ownerDetail;
    private boolean disable = false;
    private Date maxDate = new Date();
    private Date minDate = new Date();
    private boolean addDisabled;
    private List<TaxInstallmentConfigureManualDobj> taxInstallmentConfigureManualDobjList = new ArrayList<TaxInstallmentConfigureManualDobj>();
    private Long totalTaxAmount;
    private long sumOfTotalTaxInstallment;
    private Long totalUserChrg = 0l;
    private boolean disableTaxPanelEliments = false;
    private Long lastinstallamt;
    private boolean reconfigure_Inst = false;

//    public TaxInstallmentConfigureManualBean() {
//        reconfigure_Inst = false;
//    }
    public void regnNoDetails() {
        if (Utility.isNullOrBlank(regnNo)) {
            FacesMessage message = null;
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("regn_noblank")));
            return;
        }
        regnNo = regnNo.toUpperCase();
        Exception e = null;
        String taxMode = "";
        Map<String, String> taxModeMap = new HashMap<>();
        String msg = "";
        BlackListedVehicleDobj blacklistedStatus = null;
        String nocStatus = null;
        Boolean blnnocfound = false;
        Boolean blnEMIfound = false;

        try {
            OwnerImpl owner_Impl = new OwnerImpl();
            setOwnerDetail(owner_Impl.getOwnerDetails(regnNo, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd()));
            if (getOwnerDetail() == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("invalidRegn")));
                return;
            }
            //Check for blacklisted
            BlackListedVehicleImpl obj = new BlackListedVehicleImpl();
            blacklistedStatus = obj.getBlacklistedVehicleDetails(regnNo, getOwnerDetail().getChasi_no());
            if (blacklistedStatus != null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("VehBlckNoInstllmt")));
                return;
            }
            //Check for Vehicle NOC
            if (getOwnerDetail() != null && getOwnerDetail().getStatus().equalsIgnoreCase(TableConstants.VT_NOC_ISSUE_STATUS)) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("vehNocNoInstllmt")));
                return;
            }

            //Check for Vehicle Already EMI
            TaxInstallmentConfigureManualImpl objalreadyEmi = new TaxInstallmentConfigureManualImpl();
            blnEMIfound = objalreadyEmi.checkalreadymadeEMI(regnNo, Util.getUserStateCode(), false);
            if (blnEMIfound == true) {
                if (Util.getUserStateCode().equals("OR")) {
                    blnEMIfound = objalreadyEmi.checkalreadymadeEMI(regnNo, Util.getUserStateCode(), true);
                    if (blnEMIfound) {
                        if (!reconfigure_Inst) {
                            //RequestContext rc = RequestContext.getCurrentInstance();
                            PrimeFaces.current().ajax().update("taxInstallmentManualId:taxInstallmentSuccessMsgReConf");
                            PrimeFaces.current().executeScript("PF('dlgdb_taxinstallmentReConf').show()");
                            reconfigure_Inst = true;
                            return;
                        }
                    } else {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("alreadyGenInstllmt")));
                        return;
                    }
                } else {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("alreadyCreateInstllmt")));
                    return;
                }
            }
            //for Owner Identification Fields disallow typing
            if (getOwnerDetail().getOwnerIdentity() != null) {
                if (getOwnerDetail().getOwnerIdentity().getMobile_no() == null) {
                    getOwnerDetail().getOwnerIdentity().setMobile_no(0l);
                }
            }

            setOwnerDobj(owner_Impl.set_Owner_appl_db_to_dobj_with_state_off_cd(getRegnNo(), null, null, 2));
            if (getOwnerDobj() != null) {
                setDisable(true);
                setTaxInstallmentPanel(true);
                setMinDate(ownerDobj.getPurchase_dt());
            } else {
                FacesMessage message = null;
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("regn_not_found")));
                return;
            }

        } catch (VahanException ve) {
            LOGGER.error(ve);
            e = ve;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            e = ex;
        }

        if (e != null) {
            FacesMessage message = null;
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", e.getMessage()));
            reset();
            return;
        }
    }

    public void reset() {
        setRegnNo("");
        setOwnerDobj(null);
    }

    public void addRowsInTaxInstallmentConfigureManualDobjList() {
        Exception e = null;
        if (getTotalTaxAmount() == null) {
            String mgs = Util.getLocaleMsg("blank") + Util.getLocaleMsg("taxAmt");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, mgs, mgs));
            return;
        }
        if (taxInstallmentConfigureManualDobj.getTaxamountinstl() != 0 && taxInstallmentConfigureManualDobj.getPayduedt() != null) {
            if (taxInstallmentConfigureManualDobj != null) {
                TaxInstallmentConfigureManualDobj dobj = new TaxInstallmentConfigureManualDobj();
                dobj.setRegnno(this.getRegnNo());
                dobj.setTaxfromdt(taxInstallmentConfigureManualDobj.getTaxfromdt());
                dobj.setTaxuptodt(taxInstallmentConfigureManualDobj.getTaxuptodt());
                dobj.setFilerefno(taxInstallmentConfigureManualDobj.getFilerefno());
                dobj.setOrderissueby(taxInstallmentConfigureManualDobj.getOrderissueby());
                dobj.setOrderno(taxInstallmentConfigureManualDobj.getOrderno());
                dobj.setOrderdate(taxInstallmentConfigureManualDobj.getOrderdate());
                dobj.setSerialnotable(taxInstallmentConfigureManualDobj.getSerialno());
                dobj.setTaxamountinstltable(taxInstallmentConfigureManualDobj.getTaxamountinstl());
                Date addedDate = taxInstallmentConfigureManualDobj.getPayduedt();
                DateFormat frm_dte_formatter = new SimpleDateFormat("dd-MMM-yyyy");
                String returnDate = frm_dte_formatter.format(((java.util.Date) addedDate));
                dobj.setDueDateStr(returnDate);
                setSumOfTotalTaxInstallment(getSumOfTotalTaxInstallment() + dobj.getTaxamountinstltable());
                if (getSumOfTotalTaxInstallment() >= getTotalTaxAmount()) {
                    setAddDisabled(true);
                }
                getTaxInstallmentConfigureManualDobjList().add(dobj);
                taxInstallmentConfigureManualDobj.setSerialno((Integer.valueOf(taxInstallmentConfigureManualDobj.getSerialno()) + 1) + "");
                taxInstallmentConfigureManualDobj.setTaxamountinstl(0L);
                minDate = ServerUtil.dateRange(taxInstallmentConfigureManualDobj.getPayduedt(), 0, 0, 1);
                taxInstallmentConfigureManualDobj.setPayduedt(null);
            }
        } else {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("notEptyAmtOrDate")));
            return;
        }
    }

    public void deleteRowsInTaxInstallmentConfigureManualDobjList() {
        Exception e = null;
        if (taxInstallmentConfigureManualDobj != null) {
            try {
                setAddDisabled(false);
                taxInstallmentConfigureManualDobj = taxInstallmentConfigureManualDobjList.get(getTaxInstallmentConfigureManualDobjList().size() - 1);
                if (taxInstallmentConfigureManualDobjList.size() >= 2) {
                    taxInstallmentConfigureManualDobjDel = taxInstallmentConfigureManualDobjList.get(getTaxInstallmentConfigureManualDobjList().size() - 2);
                }
                if (taxInstallmentConfigureManualDobjList.size() == 1) {
                    minDate = new Date();
                }
                lastinstallamt = taxInstallmentConfigureManualDobj.getTaxamountinstltable();
                setSumOfTotalTaxInstallment(getSumOfTotalTaxInstallment() - lastinstallamt);
                taxInstallmentConfigureManualDobj.setSerialno(Integer.valueOf(taxInstallmentConfigureManualDobj.getSerialnotable()) + "");
                getTaxInstallmentConfigureManualDobjList().remove(getTaxInstallmentConfigureManualDobjList().size() - 1);
                taxInstallmentConfigureManualDobj.setTaxamountinstl(0L);
                if (taxInstallmentConfigureManualDobjDel != null && taxInstallmentConfigureManualDobjList.size() > 0) {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
                    Date returndate = formatter.parse(taxInstallmentConfigureManualDobjDel.getDueDateStr());
                    new java.sql.Date(returndate.getTime());
                    minDate = ServerUtil.dateRange(new java.sql.Date(returndate.getTime()), 0, 0, 1);
                }
                taxInstallmentConfigureManualDobj.setPayduedt(null);
            } catch (Exception ex) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("notDelete")));
            }
        }
    }

    public void savetaxinstallmentDetails() {
        String appl_no = null;
        Exception e = null;
        if (getSumOfTotalTaxInstallment() != 0 && getTotalTaxAmount() != 0) {
            try {
                if (getSumOfTotalTaxInstallment() != getTotalTaxAmount()) {//add 
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("notEqualAmt")));
                    return;
                }
                TaxInstallmentConfigureManualImpl impl = new TaxInstallmentConfigureManualImpl();
                appl_no = impl.InsertIntoVTTaxInstallment(taxInstallmentConfigureManualDobjList, Util.getUserStateCode(), Util.getUserOffCode(), Util.getEmpCode());

            } catch (VahanException ve) {
                e = ve;
            } catch (Exception ex) {
                e = ex;
            }
            if (e != null) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("occurExp") + " " + e.getMessage()));
                return;
            }
        } else {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("noZeroAmt")));
            return;
        }
        //RequestContext rc = RequestContext.getCurrentInstance();
        PrimeFaces.current().ajax().update("taxInstallmentManualId:taxInstallmentSuccessMsg");
        PrimeFaces.current().executeScript("PF('dlgdb_taxinstallment').show()");
    }

    public void reConfigureInstallment() {
        Exception e = null;
        TaxInstallmentConfigureManualImpl tax_reCon_Inst = new TaxInstallmentConfigureManualImpl();
        try {
            boolean flagValue = tax_reCon_Inst.getBackupForReconfigureInstallment(regnNo, Util.getUserStateCode(), Util.getEmpCode(), Util.getUserOffCode());
            if (flagValue) {
                reconfigure_Inst = true;
                regnNoDetails();
            } else {
                throw new VahanException(Util.getLocaleSomthingMsg());
            }
        } catch (VahanException ex) {
            e = ex;
        } catch (Exception ex) {
            e = ex;
        }
        if (e != null) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, Util.getLocaleMsg("alert"), Util.getLocaleMsg("occurExp") + " " + e.getMessage()));
            return;
        }

    }

    public void taxAmountListener() {
        setTotalTaxAmount(taxInstallmentConfigureManualDobj.getTaxAmount());
        setDisableTaxPanelEliments(true);
    }

    public TaxInstallmentConfigureManualDobj getTaxInstallmentConfigureManualDobj() {
        return taxInstallmentConfigureManualDobj;
    }

    public void setTaxInstallmentConfigureManualDobj(TaxInstallmentConfigureManualDobj taxInstallmentConfigureManualDobj) {
        this.taxInstallmentConfigureManualDobj = taxInstallmentConfigureManualDobj;
    }

    public boolean isTaxInstallmentPanel() {
        return taxInstallmentPanel;
    }

    public void setTaxInstallmentPanel(boolean taxInstallmentPanel) {
        this.taxInstallmentPanel = taxInstallmentPanel;
    }

    public String getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
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
     * @return the maxDate
     */
    public Date getMaxDate() {
        return maxDate;
    }

    /**
     * @param maxDate the maxDate to set
     */
    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    /**
     * @return the minDate
     */
    public Date getMinDate() {
        return minDate;
    }

    /**
     * @param minDate the minDate to set
     */
    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    /**
     * @return the addDisabled
     */
    public boolean isAddDisabled() {
        return addDisabled;
    }

    /**
     * @param addDisabled the addDisabled to set
     */
    public void setAddDisabled(boolean addDisabled) {
        this.addDisabled = addDisabled;
    }

    /**
     * @return the taxInstallmentConfigureManualDobjList
     */
    public List<TaxInstallmentConfigureManualDobj> getTaxInstallmentConfigureManualDobjList() {
        return taxInstallmentConfigureManualDobjList;
    }

    /**
     * @param taxInstallmentConfigureManualDobjList the
     * taxInstallmentConfigureManualDobjList to set
     */
    public void setTaxInstallmentConfigureManualDobjList(List<TaxInstallmentConfigureManualDobj> taxInstallmentConfigureManualDobjList) {
        this.taxInstallmentConfigureManualDobjList = taxInstallmentConfigureManualDobjList;
    }

    /**
     * @return the totalTaxAmount
     */
    public Long getTotalTaxAmount() {
        return totalTaxAmount;
    }

    /**
     * @param totalTaxAmount the totalTaxAmount to set
     */
    public void setTotalTaxAmount(Long totalTaxAmount) {
        this.totalTaxAmount = totalTaxAmount;
    }

    /**
     * @return the sumOfTotalTaxInstallment
     */
    public long getSumOfTotalTaxInstallment() {
        return sumOfTotalTaxInstallment;
    }

    /**
     * @param sumOfTotalTaxInstallment the sumOfTotalTaxInstallment to set
     */
    public void setSumOfTotalTaxInstallment(long sumOfTotalTaxInstallment) {
        this.sumOfTotalTaxInstallment = sumOfTotalTaxInstallment;
    }

    /**
     * @return the totalUserChrg
     */
    public Long getTotalUserChrg() {
        return totalUserChrg;
    }

    /**
     * @param totalUserChrg the totalUserChrg to set
     */
    public void setTotalUserChrg(Long totalUserChrg) {
        this.totalUserChrg = totalUserChrg;
    }

    /**
     * @return the disableTaxPanelEliments
     */
    public boolean isDisableTaxPanelEliments() {
        return disableTaxPanelEliments;
    }

    /**
     * @param disableTaxPanelEliments the disableTaxPanelEliments to set
     */
    public void setDisableTaxPanelEliments(boolean disableTaxPanelEliments) {
        this.disableTaxPanelEliments = disableTaxPanelEliments;
    }

    /**
     * @return the taxInstallmentConfigureManualDobjDel
     */
    public TaxInstallmentConfigureManualDobj getTaxInstallmentConfigureManualDobjDel() {
        return taxInstallmentConfigureManualDobjDel;
    }

    /**
     * @param taxInstallmentConfigureManualDobjDel the
     * taxInstallmentConfigureManualDobjDel to set
     */
    public void setTaxInstallmentConfigureManualDobjDel(TaxInstallmentConfigureManualDobj taxInstallmentConfigureManualDobjDel) {
        this.taxInstallmentConfigureManualDobjDel = taxInstallmentConfigureManualDobjDel;
    }

    /**
     * @return the lastinstallamt
     */
    public Long getLastinstallamt() {
        return lastinstallamt;
    }

    /**
     * @param lastinstallamt the lastinstallamt to set
     */
    public void setLastinstallamt(Long lastinstallamt) {
        this.lastinstallamt = lastinstallamt;
    }
}
