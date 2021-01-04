/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import nic.vahan.db.user_mgmt.dobj.TmConfigurationOtpDobj;
import nic.vahan.db.user_mgmt.dobj.TmConfigurationUserMgmtDobj;
import nic.vahan.form.dobj.configuration.TmConfigurationFasTag;
import nic.vahan.form.dobj.dealer.TmConfigurationDealerDobj;
import nic.vahan.form.dobj.reports.TmConfigurationPrintDobj;
import nic.vahan.form.dobj.tradecert.TmConfigurationTradeCertificateDobj;

/**
 *
 * @author Ashok
 */
public class TmConfigurationDobj implements Serializable {

    private String state_cd;
    private boolean financial_year_tax;
    private boolean calendar_month_tax;
    private boolean service_charges_per_rcpt;
    private boolean service_charges_per_trans;
    private String regn_gen_type;
    private int regn_gen_random_batch;
    private String rcpt_heading;
    private String rcpt_subheading;
    private String tax_exemption;
    private boolean ex_showroom_price_homologation;
    private boolean consider_holiday_fine;
    private boolean advance_regn_no;
    private boolean biometrics;
    private String paper_rc;
    private boolean prov_rc;
    private int fit_failed_grace_days;
    private int advanceNoJump;
    private boolean tax_adjustment;
    private int tax_adjustment_with_surcharge;
    private boolean to_retention;
    private String fitness_rqrd_for;
    private String renewal_regn_rqrd_for;
    private boolean considerTradeCert;
    private boolean to_retention_for_all_regn;
    private boolean is_rc_dispatch;
    private int nid_days;
    private boolean fit_fine_due_nid;
    private boolean prov_rc_rto;
    private boolean temp_tax_as_mvtax;
    private boolean auto_cash_rcpt_gen = true;
    private boolean scrap_veh_no_retain;
    private String fee_amt_zero = "false";
    private boolean reassign_retained_no_with_to;
    private boolean reassign_retained_no_with_conv;
    private boolean dealer_auth_for_all_office;
    private boolean user_signature;
    private boolean auto_tax_mode_filler;
    private int insurance_validity;
    private boolean rc_after_hsrp;
    private String tax_installment;
    private String fancyFeeValidMode;
    private int fancyFeeValidPeriod;
    private boolean auto_tax_no_units;
    private boolean tcNoForEachVehCatg;
    private boolean cnginfo_from_cngmaker;
    private boolean smartcard_fee_at_vendor;
    private String permit_exemption;
    private String scrap_veh_type;
    private String scrap_ret_age;
    private boolean onlinePayment;
    private boolean fine_penalty_exemtion;
    private boolean random_odd_even_reassign_allowed;
    private boolean tempFeeInNewRegis;
    private String oldFeeValidMod;
    private int oldFeeValidPeriod;
    private boolean regnNoNotAssignOthState;
    private boolean fancyFeeEditable;
    private boolean smart_card_hpa_hpt;
    private boolean tempRegnToNewRegnAtDealer;
    private boolean num_gen_allowed_dealer;
    private String applInwardExempForVehAgeExpire;
    private boolean mv_tax_at_any_office;
    private boolean is_rc_dispatch_without_postal_fee;
    private int noOfApplsForDealerPayment;
    private boolean hold_regnNo_with_conversion;
    private boolean allow_fitness_all_RTO;
    private boolean regn_upto_as_fit_upto;
    private boolean considerFMSDealer;
    private String ren_regn_from_date;
    private boolean new_reg_loi;
    private boolean validateHomoSeatCap;
    private boolean mobile_verify;
    private String retentionFeeValidMode;
    private int retentionFeeValidPeriod;
    private String user_catg_mandate_otp;
    private TmConfigurationDealerDobj tmConfigDealerDobj;
    private int fmsGraceDays;
    private boolean isFancyFeeZero;
    private TmConfigurationDMS tmConfigDmsDobj;
    private boolean otpforOwnerAdmin;
    private boolean login_via_ip_address;
    private TmConfigurationUserMgmtDobj tmConfigUserMgmtDobj;
    private boolean is_rc_dispatch_repost_fee;
    private TmConfigurationOtpDobj tmConfigOtpDobj;
    private String blocked_purcd_for_blacklist_vehicle;
    private String owner_admin_modify_on_status;
    private String other_rto_number_change;
    private boolean defacement;
    private TmCofigurationOnlinePaymentDobj tmConfigOnlineDobj;
    private TmConfigurationPrintDobj tmPrintConfgDobj;
    private TmConfigurationUserMessagingDobj tmUserMessagingConfigDobj;
    private String updateadditionallttpurcd;
    private TmConfigurationTradeCertificateDobj tmTradeCertConfigDobj;
    private boolean noCashPayment;
    private String vltd_condition_formula;
    private boolean allowRegnDataFoundForOS_OR;
    private boolean allowRegnDataFoundForTRC;
    private boolean allowFacelessService;
    private TmConfigurationPayVerifyDobj tmConfigPayVerifyDobj;
    private String showPermitMultiRegion;
    private boolean mdfVehDtlsApproveByStateAdmin;
    private String echallan_pur_cd_restict;
    private TmConfigurationFasTag tmConfigurationFasTag;

    /**
     * @return the state_cd
     */
    public String getState_cd() {
        return state_cd;
    }

    /**
     * @param state_cd the state_cd to set
     */
    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    /**
     * @return the financial_year_tax
     */
    public boolean isFinancial_year_tax() {
        return financial_year_tax;
    }

    /**
     * @param financial_year_tax the financial_year_tax to set
     */
    public void setFinancial_year_tax(boolean financial_year_tax) {
        this.financial_year_tax = financial_year_tax;
    }

    /**
     * @return the calendar_month_tax
     */
    public boolean isCalendar_month_tax() {
        return calendar_month_tax;
    }

    /**
     * @param calendar_month_tax the calendar_month_tax to set
     */
    public void setCalendar_month_tax(boolean calendar_month_tax) {
        this.calendar_month_tax = calendar_month_tax;
    }

    /**
     * @return the service_charges_per_rcpt
     */
    public boolean isService_charges_per_rcpt() {
        return service_charges_per_rcpt;
    }

    /**
     * @param service_charges_per_rcpt the service_charges_per_rcpt to set
     */
    public void setService_charges_per_rcpt(boolean service_charges_per_rcpt) {
        this.service_charges_per_rcpt = service_charges_per_rcpt;
    }

    /**
     * @return the service_charges_per_trans
     */
    public boolean isService_charges_per_trans() {
        return service_charges_per_trans;
    }

    /**
     * @param service_charges_per_trans the service_charges_per_trans to set
     */
    public void setService_charges_per_trans(boolean service_charges_per_trans) {
        this.service_charges_per_trans = service_charges_per_trans;
    }

    /**
     * @return the regn_gen_type
     */
    public String getRegn_gen_type() {
        return regn_gen_type;
    }

    /**
     * @param regn_gen_type the regn_gen_type to set
     */
    public void setRegn_gen_type(String regn_gen_type) {
        this.regn_gen_type = regn_gen_type;
    }

    /**
     * @return the regn_gen_random_batch
     */
    public int getRegn_gen_random_batch() {
        return regn_gen_random_batch;
    }

    /**
     * @param regn_gen_random_batch the regn_gen_random_batch to set
     */
    public void setRegn_gen_random_batch(int regn_gen_random_batch) {
        this.regn_gen_random_batch = regn_gen_random_batch;
    }

    /**
     * @return the rcpt_heading
     */
    public String getRcpt_heading() {
        return rcpt_heading;
    }

    /**
     * @param rcpt_heading the rcpt_heading to set
     */
    public void setRcpt_heading(String rcpt_heading) {
        this.rcpt_heading = rcpt_heading;
    }

    /**
     * @return the rcpt_subheading
     */
    public String getRcpt_subheading() {
        return rcpt_subheading;
    }

    /**
     * @param rcpt_subheading the rcpt_subheading to set
     */
    public void setRcpt_subheading(String rcpt_subheading) {
        this.rcpt_subheading = rcpt_subheading;
    }

    /**
     * @return the tax_exemption
     */
    public String getTax_exemption() {
        return tax_exemption;
    }

    /**
     * @param tax_exemption the tax_exemption to set
     */
    public void setTax_exemption(String tax_exemption) {
        this.tax_exemption = tax_exemption;
    }

    /**
     * @return the ex_showroom_price_homologation
     */
    public boolean isEx_showroom_price_homologation() {
        return ex_showroom_price_homologation;
    }

    /**
     * @param ex_showroom_price_homologation the ex_showroom_price_homologation
     * to set
     */
    public void setEx_showroom_price_homologation(boolean ex_showroom_price_homologation) {
        this.ex_showroom_price_homologation = ex_showroom_price_homologation;
    }

    /**
     * @return the consider_holiday_fine
     */
    public boolean isConsider_holiday_fine() {
        return consider_holiday_fine;
    }

    /**
     * @param consider_holiday_fine the consider_holiday_fine to set
     */
    public void setConsider_holiday_fine(boolean consider_holiday_fine) {
        this.consider_holiday_fine = consider_holiday_fine;
    }

    /**
     * @return the advance_regn_no
     */
    public boolean isAdvance_regn_no() {
        return advance_regn_no;
    }

    /**
     * @param advance_regn_no the advance_regn_no to set
     */
    public void setAdvance_regn_no(boolean advance_regn_no) {
        this.advance_regn_no = advance_regn_no;
    }

    /**
     * @return the biometrics
     */
    public boolean isBiometrics() {
        return biometrics;
    }

    /**
     * @param biometrics the biometrics to set
     */
    public void setBiometrics(boolean biometrics) {
        this.biometrics = biometrics;
    }

    /**
     * @return the paper_rc
     */
    public String getPaper_rc() {
        return paper_rc;
    }

    /**
     * @param paper_rc the paper_rc to set
     */
    public void setPaper_rc(String paper_rc) {
        this.paper_rc = paper_rc;
    }

    /**
     * @return the prov_rc
     */
    public boolean isProv_rc() {
        return prov_rc;
    }

    /**
     * @param prov_rc the prov_rc to set
     */
    public void setProv_rc(boolean prov_rc) {
        this.prov_rc = prov_rc;
    }

    /**
     * @return the fit_failed_grace_days
     */
    public int getFit_failed_grace_days() {
        return fit_failed_grace_days;
    }

    /**
     * @param fit_failed_grace_days the fit_failed_grace_days to set
     */
    public void setFit_failed_grace_days(int fit_failed_grace_days) {
        this.fit_failed_grace_days = fit_failed_grace_days;
    }

    /**
     * @return the advanceNoJump
     */
    public int getAdvanceNoJump() {
        return advanceNoJump;
    }

    /**
     * @param advanceNoJump the advanceNoJump to set
     */
    public void setAdvanceNoJump(int advanceNoJump) {
        this.advanceNoJump = advanceNoJump;
    }

    /**
     * @return the tax_adjustment_with_surcharge
     */
    public int getTax_adjustment_with_surcharge() {
        return tax_adjustment_with_surcharge;
    }

    /**
     * @param tax_adjustment_with_surcharge the tax_adjustment_with_surcharge to
     * set
     */
    public void setTax_adjustment_with_surcharge(int tax_adjustment_with_surcharge) {
        this.tax_adjustment_with_surcharge = tax_adjustment_with_surcharge;
    }

    /**
     * @return the tax_adjustment
     */
    public boolean isTax_adjustment() {
        return tax_adjustment;
    }

    /**
     * @param tax_adjustment the tax_adjustment to set
     */
    public void setTax_adjustment(boolean tax_adjustment) {
        this.tax_adjustment = tax_adjustment;
    }

    /**
     * @return the to_retention
     */
    public boolean isTo_retention() {
        return to_retention;
    }

    /**
     * @param to_retention the to_retention to set
     */
    public void setTo_retention(boolean to_retention) {
        this.to_retention = to_retention;
    }

    /**
     * @return the fitness_rqrd_for
     */
    public String getFitness_rqrd_for() {
        return fitness_rqrd_for;
    }

    /**
     * @param fitness_rqrd_for the fitness_rqrd_for to set
     */
    public void setFitness_rqrd_for(String fitness_rqrd_for) {
        this.fitness_rqrd_for = fitness_rqrd_for;
    }

    /**
     * @return the renewal_regn_rqrd_for
     */
    public String getRenewal_regn_rqrd_for() {
        return renewal_regn_rqrd_for;
    }

    /**
     * @param renewal_regn_rqrd_for the renewal_regn_rqrd_for to set
     */
    public void setRenewal_regn_rqrd_for(String renewal_regn_rqrd_for) {
        this.renewal_regn_rqrd_for = renewal_regn_rqrd_for;
    }

    /**
     * @return the considerTradeCert
     */
    public boolean isConsiderTradeCert() {
        return considerTradeCert;
    }

    /**
     * @param considerTradeCert the considerTradeCert to set
     */
    public void setConsiderTradeCert(boolean considerTradeCert) {
        this.considerTradeCert = considerTradeCert;
    }

    /**
     * @return the to_retention_for_all_regn
     */
    public boolean isTo_retention_for_all_regn() {
        return to_retention_for_all_regn;
    }

    /**
     * @param to_retention_for_all_regn the to_retention_for_all_regn to set
     */
    public void setTo_retention_for_all_regn(boolean to_retention_for_all_regn) {
        this.to_retention_for_all_regn = to_retention_for_all_regn;
    }

    /**
     * @return the is_rc_dispatch
     */
    public boolean isIs_rc_dispatch() {
        return is_rc_dispatch;
    }

    /**
     * @param is_rc_dispatch the is_rc_dispatch to set
     */
    public void setIs_rc_dispatch(boolean is_rc_dispatch) {
        this.is_rc_dispatch = is_rc_dispatch;
    }

    /**
     * @return the nid_days
     */
    public int getNid_days() {
        return nid_days;
    }

    /**
     * @param nid_days the nid_days to set
     */
    public void setNid_days(int nid_days) {
        this.nid_days = nid_days;
    }

    /**
     * @return the fit_fine_due_nid
     */
    public boolean isFit_fine_due_nid() {
        return fit_fine_due_nid;
    }

    /**
     * @param fit_fine_due_nid the fit_fine_due_nid to set
     */
    public void setFit_fine_due_nid(boolean fit_fine_due_nid) {
        this.fit_fine_due_nid = fit_fine_due_nid;
    }

    /**
     * @return the prov_rc_rto
     */
    public boolean isProv_rc_rto() {
        return prov_rc_rto;
    }

    /**
     * @param prov_rc_rto the prov_rc_rto to set
     */
    public void setProv_rc_rto(boolean prov_rc_rto) {
        this.prov_rc_rto = prov_rc_rto;
    }

    /**
     * @return the temp_tax_as_mvtax
     */
    public boolean isTemp_tax_as_mvtax() {
        return temp_tax_as_mvtax;
    }

    /**
     * @param temp_tax_as_mvtax the temp_tax_as_mvtax to set
     */
    public void setTemp_tax_as_mvtax(boolean temp_tax_as_mvtax) {
        this.temp_tax_as_mvtax = temp_tax_as_mvtax;
    }

    /**
     * @return the auto_cash_rcpt_gen
     */
    public boolean isAuto_cash_rcpt_gen() {
        return auto_cash_rcpt_gen;
    }

    /**
     * @param auto_cash_rcpt_gen the auto_cash_rcpt_gen to set
     */
    public void setAuto_cash_rcpt_gen(boolean auto_cash_rcpt_gen) {
        this.auto_cash_rcpt_gen = auto_cash_rcpt_gen;
    }

    public boolean isScrap_veh_no_retain() {
        return scrap_veh_no_retain;
    }

    public void setScrap_veh_no_retain(boolean scrap_veh_no_retain) {
        this.scrap_veh_no_retain = scrap_veh_no_retain;
    }

    /**
     * @return the fee_amt_zero
     */
    public String getFee_amt_zero() {
        return fee_amt_zero;
    }

    /**
     * @param fee_amt_zero the fee_amt_zero to set
     */
    public void setFee_amt_zero(String fee_amt_zero) {
        this.fee_amt_zero = fee_amt_zero;
    }

    /**
     * @return the reassign_retained_no_with_to
     */
    public boolean isReassign_retained_no_with_to() {
        return reassign_retained_no_with_to;
    }

    /**
     * @param reassign_retained_no_with_to the reassign_retained_no_with_to to
     * set
     */
    public void setReassign_retained_no_with_to(boolean reassign_retained_no_with_to) {
        this.reassign_retained_no_with_to = reassign_retained_no_with_to;
    }

    /**
     * @return the dealer_auth_for_all_office
     */
    public boolean isDealer_auth_for_all_office() {
        return dealer_auth_for_all_office;
    }

    /**
     * @param dealer_auth_for_all_office the dealer_auth_for_all_office to set
     */
    public void setDealer_auth_for_all_office(boolean dealer_auth_for_all_office) {
        this.dealer_auth_for_all_office = dealer_auth_for_all_office;
    }

    /**
     * @return the user_signature
     */
    public boolean isUser_signature() {
        return user_signature;
    }

    /**
     * @param user_signature the user_signature to set
     */
    public void setUser_signature(boolean user_signature) {
        this.user_signature = user_signature;
    }

    /**
     * @return the auto_tax_mode_filler
     */
    public boolean isAuto_tax_mode_filler() {
        return auto_tax_mode_filler;
    }

    /**
     * @param auto_tax_mode_filler the auto_tax_mode_filler to set
     */
    public void setAuto_tax_mode_filler(boolean auto_tax_mode_filler) {
        this.auto_tax_mode_filler = auto_tax_mode_filler;
    }

    /**
     * @return the insurance_validity
     */
    public int getInsurance_validity() {
        return insurance_validity;
    }

    /**
     * @param insurance_validity the insurance_validity to set
     */
    public void setInsurance_validity(int insurance_validity) {
        this.insurance_validity = insurance_validity;
    }

    public boolean isRc_after_hsrp() {
        return rc_after_hsrp;
    }

    public void setRc_after_hsrp(boolean rc_after_hsrp) {
        this.rc_after_hsrp = rc_after_hsrp;
    }

    public String getTax_installment() {
        return tax_installment;
    }

    /**
     * @param tax_installment the tax_installment to set
     */
    public void setTax_installment(String tax_installment) {
        this.tax_installment = tax_installment;
    }

    /**
     * @return the fancyFeeValidMode
     */
    public String getFancyFeeValidMode() {
        return fancyFeeValidMode;
    }

    /**
     * @param fancyFeeValidMode the fancyFeeValidMode to set
     */
    public void setFancyFeeValidMode(String fancyFeeValidMode) {
        this.fancyFeeValidMode = fancyFeeValidMode;
    }

    /**
     * @return the fancyFeeValidPeriod
     */
    public int getFancyFeeValidPeriod() {
        return fancyFeeValidPeriod;
    }

    /**
     * @param fancyFeeValidPeriod the fancyFeeValidPeriod to set
     */
    public void setFancyFeeValidPeriod(int fancyFeeValidPeriod) {
        this.fancyFeeValidPeriod = fancyFeeValidPeriod;
    }

    /**
     * @return the auto_tax_no_units
     */
    public boolean isAuto_tax_no_units() {
        return auto_tax_no_units;
    }

    /**
     * @param auto_tax_no_units the auto_tax_no_units to set
     */
    public void setAuto_tax_no_units(boolean auto_tax_no_units) {
        this.auto_tax_no_units = auto_tax_no_units;
    }

    /**
     * @return the tcNoForEachVehCatg
     */
    public boolean isTcNoForEachVehCatg() {
        return tcNoForEachVehCatg;
    }

    /**
     * @param tcNoForEachVehCatg the tcNoForEachVehCatg to set
     */
    public void setTcNoForEachVehCatg(boolean tcNoForEachVehCatg) {
        this.tcNoForEachVehCatg = tcNoForEachVehCatg;
    }

    /**
     * @return the smartcard_fee_at_vendor
     */
    public boolean isSmartcard_fee_at_vendor() {
        return smartcard_fee_at_vendor;
    }

    /**
     * @param smartcard_fee_at_vendor the smartcard_fee_at_vendor to set
     */
    public void setSmartcard_fee_at_vendor(boolean smartcard_fee_at_vendor) {
        this.smartcard_fee_at_vendor = smartcard_fee_at_vendor;
    }

    /**
     * @return the cnginfo_from_cngmaker
     */
    public boolean isCnginfo_from_cngmaker() {
        return cnginfo_from_cngmaker;
    }

    /**
     * @param cnginfo_from_cngmaker the cnginfo_from_cngmaker to set
     */
    public void setCnginfo_from_cngmaker(boolean cnginfo_from_cngmaker) {
        this.cnginfo_from_cngmaker = cnginfo_from_cngmaker;
    }

    /**
     * @return the permit_exemption
     */
    public String getPermit_exemption() {
        return permit_exemption;
    }

    /**
     * @param permit_exemption the permit_exemption to set
     */
    public void setPermit_exemption(String permit_exemption) {
        this.permit_exemption = permit_exemption;
    }

    public String getScrap_veh_type() {
        return scrap_veh_type;
    }

    public void setScrap_veh_type(String scrap_veh_type) {
        this.scrap_veh_type = scrap_veh_type;
    }

    public String getScrap_ret_age() {
        return scrap_ret_age;
    }

    public void setScrap_ret_age(String scrap_ret_age) {
        this.scrap_ret_age = scrap_ret_age;
    }

    /**
     * @return the reassign_retained_no_with_conv
     */
    public boolean isReassign_retained_no_with_conv() {
        return reassign_retained_no_with_conv;
    }

    /**
     * @param the reassign_retained_no_with_conv to set
     */
    public void setReassign_retained_no_with_conv(boolean reassign_retained_no_with_conv) {
        this.reassign_retained_no_with_conv = reassign_retained_no_with_conv;
    }

    /**
     * @return the onlinePayment
     */
    public boolean isOnlinePayment() {
        return onlinePayment;
    }

    /**
     * @param the onlinePayment to set
     */
    public void setOnlinePayment(boolean onlinePayment) {
        this.onlinePayment = onlinePayment;
    }

    /**
     * @return the fine_penalty_exemtion
     */
    public boolean isFine_penalty_exemtion() {
        return fine_penalty_exemtion;
    }

    /**
     * @param fine_penalty_exemtion the fine_penalty_exemtion to set
     */
    public void setFine_penalty_exemtion(boolean fine_penalty_exemtion) {
        this.fine_penalty_exemtion = fine_penalty_exemtion;
    }

    /**
     * @ return the random_odd_even_reassign_allowed
     */
    public boolean isRandom_odd_even_reassign_allowed() {
        return random_odd_even_reassign_allowed;
    }

    /**
     * @param random_odd_even_reassign_allowed the
     * random_odd_even_reassign_allowed to set
     */
    public void setRandom_odd_even_reassign_allowed(boolean random_odd_even_reassign_allowed) {
        this.random_odd_even_reassign_allowed = random_odd_even_reassign_allowed;
    }

    /**
     * @return the tempFeeInNewRegis
     */
    public boolean isTempFeeInNewRegis() {
        return tempFeeInNewRegis;
    }

    /**
     * @param tempFeeInNewRegis the tempFeeInNewRegis to set
     */
    public void setTempFeeInNewRegis(boolean tempFeeInNewRegis) {
        this.tempFeeInNewRegis = tempFeeInNewRegis;
    }

    public String getOldFeeValidMod() {
        return oldFeeValidMod;
    }

    public void setOldFeeValidMod(String oldFeeValidMod) {
        this.oldFeeValidMod = oldFeeValidMod;
    }

    public int getOldFeeValidPeriod() {
        return oldFeeValidPeriod;
    }

    public void setOldFeeValidPeriod(int oldFeeValidPeriod) {
        this.oldFeeValidPeriod = oldFeeValidPeriod;
    }

    /**
     * @return the regnNoNotAssignOthState
     */
    public boolean isRegnNoNotAssignOthState() {
        return regnNoNotAssignOthState;
    }

    /**
     * @param regnNoNotAssignOthState the regnNoNotAssignOthState to set
     */
    public void setRegnNoNotAssignOthState(boolean regnNoNotAssignOthState) {
        this.regnNoNotAssignOthState = regnNoNotAssignOthState;
    }

    /**
     * @return the fancyFeeEditable
     */
    public boolean isFancyFeeEditable() {
        return fancyFeeEditable;
    }

    /**
     * @param fancyFeeEditable the fancyFeeEditable to set
     */
    public void setFancyFeeEditable(boolean fancyFeeEditable) {
        this.fancyFeeEditable = fancyFeeEditable;
    }

    /**
     * @return the smart_card_hpa_hpt
     */
    public boolean isSmart_card_hpa_hpt() {
        return smart_card_hpa_hpt;
    }

    /**
     * @param smart_card_hpa_hpt the smart_card_hpa_hpt to set
     */
    public void setSmart_card_hpa_hpt(boolean smart_card_hpa_hpt) {
        this.smart_card_hpa_hpt = smart_card_hpa_hpt;
    }

    /**
     * @return the tempRegnToNewRegnAtDealer
     */
    public boolean isTempRegnToNewRegnAtDealer() {
        return tempRegnToNewRegnAtDealer;
    }

    /**
     * @param tempRegnToNewRegnAtDealer the tempRegnToNewRegnAtDealer to set
     */
    public void setTempRegnToNewRegnAtDealer(boolean tempRegnToNewRegnAtDealer) {
        this.tempRegnToNewRegnAtDealer = tempRegnToNewRegnAtDealer;
    }

    /**
     * @return the mv_tax_at_any_office
     */
    public boolean isMv_tax_at_any_office() {
        return mv_tax_at_any_office;
    }

    /**
     * @param mv_tax_at_any_office the mv_tax_at_any_office to set
     */
    public void setMv_tax_at_any_office(boolean mv_tax_at_any_office) {
        this.mv_tax_at_any_office = mv_tax_at_any_office;
    }

    /**
     * @return the num_gen_allowed_dealer
     */
    public boolean isNum_gen_allowed_dealer() {
        return num_gen_allowed_dealer;
    }

    /**
     * @param num_gen_allowed_dealer the num_gen_allowed_dealer to set
     */
    public void setNum_gen_allowed_dealer(boolean num_gen_allowed_dealer) {
        this.num_gen_allowed_dealer = num_gen_allowed_dealer;
    }

    /**
     * @return the applInwardExempForVehAgeExpire
     */
    public String getApplInwardExempForVehAgeExpire() {
        return applInwardExempForVehAgeExpire;
    }

    /**
     * @param applInwardExempForVehAgeExpire the applInwardExempForVehAgeExpire
     * to set
     */
    public void setApplInwardExempForVehAgeExpire(String applInwardExempForVehAgeExpire) {
        this.applInwardExempForVehAgeExpire = applInwardExempForVehAgeExpire;
    }

    /**
     * @return the is_rc_dispatch_without_postal_fee
     */
    public boolean isIs_rc_dispatch_without_postal_fee() {
        return is_rc_dispatch_without_postal_fee;
    }

    /**
     * @param is_rc_dispatch_without_postal_fee the
     * is_rc_dispatch_without_postal_fee to set
     */
    public void setIs_rc_dispatch_without_postal_fee(boolean is_rc_dispatch_without_postal_fee) {
        this.is_rc_dispatch_without_postal_fee = is_rc_dispatch_without_postal_fee;
    }

    public boolean isHold_regnNo_with_conversion() {
        return hold_regnNo_with_conversion;
    }

    public void setHold_regnNo_with_conversion(boolean hold_regnNo_with_conversion) {
        this.hold_regnNo_with_conversion = hold_regnNo_with_conversion;
    }

    /**
     * @return the noOfApplsForDealerPayment
     */
    public int getNoOfApplsForDealerPayment() {
        return noOfApplsForDealerPayment;
    }

    /**
     * @param noOfApplsForDealerPayment the noOfApplsForDealerPayment to set
     */
    public void setNoOfApplsForDealerPayment(int noOfApplsForDealerPayment) {
        this.noOfApplsForDealerPayment = noOfApplsForDealerPayment;
    }

    public boolean isAllow_fitness_all_RTO() {
        return allow_fitness_all_RTO;
    }

    public void setAllow_fitness_all_RTO(boolean allow_fitness_all_RTO) {
        this.allow_fitness_all_RTO = allow_fitness_all_RTO;
    }

    /**
     * @return the regn_upto_as_fit_upto
     */
    public boolean isRegn_upto_as_fit_upto() {
        return regn_upto_as_fit_upto;
    }

    /**
     * @param regn_upto_as_fit_upto the regn_upto_as_fit_upto to set
     */
    public void setRegn_upto_as_fit_upto(boolean regn_upto_as_fit_upto) {
        this.regn_upto_as_fit_upto = regn_upto_as_fit_upto;
    }

    /**
     * @return the considerFMSDealer
     */
    public boolean isConsiderFMSDealer() {
        return considerFMSDealer;
    }

    /**
     * @param considerFMSDealer the considerFMSDealer to set
     */
    public void setConsiderFMSDealer(boolean considerFMSDealer) {
        this.considerFMSDealer = considerFMSDealer;
    }

    public String getRen_regn_from_date() {
        return ren_regn_from_date;
    }

    public void setRen_regn_from_date(String ren_regn_from_date) {
        this.ren_regn_from_date = ren_regn_from_date;
    }

    public boolean isNew_reg_loi() {
        return new_reg_loi;
    }

    public void setNew_reg_loi(boolean new_reg_loi) {
        this.new_reg_loi = new_reg_loi;
    }

    /**
     * @return the tmConfigDealerDobj
     */
    public TmConfigurationDealerDobj getTmConfigDealerDobj() {
        return tmConfigDealerDobj;
    }

    /**
     * @param tmConfigDealerDobj the tmConfigDealerDobj to set
     */
    public void setTmConfigDealerDobj(TmConfigurationDealerDobj tmConfigDealerDobj) {
        this.tmConfigDealerDobj = tmConfigDealerDobj;
    }

    /**
     * @return the validateHomoSeatCap
     */
    public boolean isValidateHomoSeatCap() {
        return validateHomoSeatCap;
    }

    /**
     * @param validateHomoSeatCap the validateHomoSeatCap to set
     */
    public void setValidateHomoSeatCap(boolean validateHomoSeatCap) {
        this.validateHomoSeatCap = validateHomoSeatCap;
    }

    /**
     * @return the mobile_verify
     */
    public boolean isMobile_verify() {
        return mobile_verify;
    }

    /**
     * @param mobile_verify the mobile_verify to set
     */
    public void setMobile_verify(boolean mobile_verify) {
        this.mobile_verify = mobile_verify;
    }

    public String getRetentionFeeValidMode() {
        return retentionFeeValidMode;
    }

    public void setRetentionFeeValidMode(String retentionFeeValidMode) {
        this.retentionFeeValidMode = retentionFeeValidMode;
    }

    public int getRetentionFeeValidPeriod() {
        return retentionFeeValidPeriod;
    }

    public void setRetentionFeeValidPeriod(int retentionFeeValidPeriod) {
        this.retentionFeeValidPeriod = retentionFeeValidPeriod;
    }

    /**
     * @return the user_catg_mandate_otp
     */
    public String getUser_catg_mandate_otp() {
        return user_catg_mandate_otp;
    }

    /**
     * @param user_catg_mandate_otp the user_catg_mandate_otp to set
     */
    public void setUser_catg_mandate_otp(String user_catg_mandate_otp) {
        this.user_catg_mandate_otp = user_catg_mandate_otp;
    }

    /**
     * @return the fmsGraceDays
     */
    public int getFmsGraceDays() {
        return fmsGraceDays;
    }

    /**
     * @param fmsGraceDays the fmsGraceDays to set
     */
    public void setFmsGraceDays(int fmsGraceDays) {
        this.fmsGraceDays = fmsGraceDays;
    }

    public boolean isIsFancyFeeZero() {
        return isFancyFeeZero;
    }

    public void setIsFancyFeeZero(boolean isFancyFeeZero) {
        this.isFancyFeeZero = isFancyFeeZero;
    }

    /**
     * @return the tmConfigDmsDobj
     */
    public TmConfigurationDMS getTmConfigDmsDobj() {
        return tmConfigDmsDobj;
    }

    /**
     * @param tmConfigDmsDobj the tmConfigDmsDobj to set
     */
    public void setTmConfigDmsDobj(TmConfigurationDMS tmConfigDmsDobj) {
        this.tmConfigDmsDobj = tmConfigDmsDobj;
    }

    public boolean isOtpforOwnerAdmin() {
        return otpforOwnerAdmin;
    }

    public void setOtpforOwnerAdmin(boolean otpforOwnerAdmin) {
        this.otpforOwnerAdmin = otpforOwnerAdmin;

    }

    /**
     * @return the login_via_ip_address
     */
    public boolean isLogin_via_ip_address() {
        return login_via_ip_address;
    }

    /**
     * @param login_via_ip_address the login_via_ip_address to set
     */
    public void setLogin_via_ip_address(boolean login_via_ip_address) {
        this.login_via_ip_address = login_via_ip_address;
    }

    /**
     * @return the tmConfigUserMgmtDobj
     */
    public TmConfigurationUserMgmtDobj getTmConfigUserMgmtDobj() {
        return tmConfigUserMgmtDobj;
    }

    /**
     * @param tmConfigUserMgmtDobj the tmConfigUserMgmtDobj to set
     */
    public void setTmConfigUserMgmtDobj(TmConfigurationUserMgmtDobj tmConfigUserMgmtDobj) {
        this.tmConfigUserMgmtDobj = tmConfigUserMgmtDobj;
    }

    /**
     * @return the is_rc_dispatch_repost_fee
     */
    public boolean isIs_rc_dispatch_repost_fee() {
        return is_rc_dispatch_repost_fee;
    }

    /**
     * @param is_rc_dispatch_repost_fee the is_rc_dispatch_repost_fee to set
     */
    public void setIs_rc_dispatch_repost_fee(boolean is_rc_dispatch_repost_fee) {
        this.is_rc_dispatch_repost_fee = is_rc_dispatch_repost_fee;
    }

    /**
     * @return the tmConfigOtpDobj
     */
    public TmConfigurationOtpDobj getTmConfigOtpDobj() {
        return tmConfigOtpDobj;
    }

    /**
     * @param tmConfigOtpDobj the tmConfigOtpDobj to set
     */
    public void setTmConfigOtpDobj(TmConfigurationOtpDobj tmConfigOtpDobj) {
        this.tmConfigOtpDobj = tmConfigOtpDobj;
    }

    public String getBlocked_purcd_for_blacklist_vehicle() {
        return blocked_purcd_for_blacklist_vehicle;
    }

    public void setBlocked_purcd_for_blacklist_vehicle(String blocked_purcd_for_blacklist_vehicle) {
        this.blocked_purcd_for_blacklist_vehicle = blocked_purcd_for_blacklist_vehicle;
    }

    public String getOwner_admin_modify_on_status() {
        return owner_admin_modify_on_status;
    }

    public void setOwner_admin_modify_on_status(String owner_admin_modify_on_status) {
        this.owner_admin_modify_on_status = owner_admin_modify_on_status;
    }

    /**
     * @return the other_rto_number_change
     */
    public String getOther_rto_number_change() {
        return other_rto_number_change;
    }

    /**
     * @param other_rto_number_change the other_rto_number_change to set
     */
    public void setOther_rto_number_change(String other_rto_number_change) {
        this.other_rto_number_change = other_rto_number_change;
    }

    public TmCofigurationOnlinePaymentDobj getTmConfigOnlineDobj() {
        return tmConfigOnlineDobj;
    }

    public void setTmConfigOnlineDobj(TmCofigurationOnlinePaymentDobj tmConfigOnlineDobj) {
        this.tmConfigOnlineDobj = tmConfigOnlineDobj;
    }

    public boolean isDefacement() {
        return defacement;
    }

    public void setDefacement(boolean defacement) {
        this.defacement = defacement;
    }

    public void setUpdateadditionallttpurcd(String updateadditionallttpurcd) {
        this.updateadditionallttpurcd = updateadditionallttpurcd;
    }

    /**
     * @param tmPrintConfgDobj the tmPrintConfgDobj to set
     */
    public void setTmPrintConfgDobj(TmConfigurationPrintDobj tmPrintConfgDobj) {
        this.tmPrintConfgDobj = tmPrintConfgDobj;
    }

    public String getUpdateadditionallttpurcd() {
        return updateadditionallttpurcd;
    }

    /**
     * @return the tmPrintConfgDobj
     */
    public TmConfigurationPrintDobj getTmPrintConfgDobj() {
        return tmPrintConfgDobj;
    }

    /**
     * @return the tmTradeCertConfigDobj
     */
    public TmConfigurationTradeCertificateDobj getTmTradeCertConfigDobj() {
        return tmTradeCertConfigDobj;
    }

    /**
     * @param tmTradeCertConfigDobj the tmTradeCertConfigDobj to set
     */
    public void setTmTradeCertConfigDobj(TmConfigurationTradeCertificateDobj tmTradeCertConfigDobj) {
        this.tmTradeCertConfigDobj = tmTradeCertConfigDobj;
    }

    /**
     * @return the noCashPayment
     */
    public boolean isNoCashPayment() {
        return noCashPayment;
    }

    /**
     * @param noCashPayment the noCashPayment to set
     */
    public void setNoCashPayment(boolean noCashPayment) {
        this.noCashPayment = noCashPayment;
    }

    public String getVltd_condition_formula() {
        return vltd_condition_formula;
    }

    public void setVltd_condition_formula(String vltd_condition_formula) {
        this.vltd_condition_formula = vltd_condition_formula;
    }

    public void setTmUserMessagingConfigDobj(TmConfigurationUserMessagingDobj tmUserMessagingConfigDobj) {
        this.tmUserMessagingConfigDobj = tmUserMessagingConfigDobj;
    }

    public TmConfigurationUserMessagingDobj getTmUserMessagingConfigDobj() {
        return tmUserMessagingConfigDobj;
    }

    /**
     * @return the allowRegnDataFoundForOS_OR
     */
    public boolean isAllowRegnDataFoundForOS_OR() {
        return allowRegnDataFoundForOS_OR;
    }

    /**
     * @param allowRegnDataFoundForOS_OR the allowRegnDataFoundForOS_OR to set
     */
    public void setAllowRegnDataFoundForOS_OR(boolean allowRegnDataFoundForOS_OR) {
        this.allowRegnDataFoundForOS_OR = allowRegnDataFoundForOS_OR;
    }

    /**
     * @return the allowRegnDataFoundForTRC
     */
    public boolean isAllowRegnDataFoundForTRC() {
        return allowRegnDataFoundForTRC;
    }

    /**
     * @param allowRegnDataFoundForTRC the allowRegnDataFoundForTRC to set
     */
    public void setAllowRegnDataFoundForTRC(boolean allowRegnDataFoundForTRC) {
        this.allowRegnDataFoundForTRC = allowRegnDataFoundForTRC;
    }

    /**
     * @return the allowFacelessService
     */
    public boolean isAllowFacelessService() {
        return allowFacelessService;
    }

    /**
     * @param allowFacelessService the allowFacelessService to set
     */
    public void setAllowFacelessService(boolean allowFacelessService) {
        this.allowFacelessService = allowFacelessService;
    }

    /**
     * @return the tmConfigPayVerifyDobj
     */
    public TmConfigurationPayVerifyDobj getTmConfigPayVerifyDobj() {
        return tmConfigPayVerifyDobj;
    }

    /**
     * @param tmConfigPayVerifyDobj the tmConfigPayVerifyDobj to set
     */
    public void setTmConfigPayVerifyDobj(TmConfigurationPayVerifyDobj tmConfigPayVerifyDobj) {
        this.tmConfigPayVerifyDobj = tmConfigPayVerifyDobj;
    }

    /**
     * @return the tmConfigurationFasTag
     */
    public TmConfigurationFasTag getTmConfigurationFasTag() {
        return tmConfigurationFasTag;
    }

    /**
     * @param tmConfigurationFasTag the tmConfigurationFasTag to set
     */
    public void setTmConfigurationFasTag(TmConfigurationFasTag tmConfigurationFasTag) {
        this.tmConfigurationFasTag = tmConfigurationFasTag;
    }

    public String getShowPermitMultiRegion() {
        return showPermitMultiRegion;
    }

    public void setShowPermitMultiRegion(String showPermitMultiRegion) {
        this.showPermitMultiRegion = showPermitMultiRegion;
    }

    public boolean isMdfVehDtlsApproveByStateAdmin() {
        return mdfVehDtlsApproveByStateAdmin;
    }

    public void setMdfVehDtlsApproveByStateAdmin(boolean mdfVehDtlsApproveByStateAdmin) {
        this.mdfVehDtlsApproveByStateAdmin = mdfVehDtlsApproveByStateAdmin;
    }

    public String getEchallan_pur_cd_restict() {
        return echallan_pur_cd_restict;
    }

    public void setEchallan_pur_cd_restict(String echallan_pur_cd_restict) {
        this.echallan_pur_cd_restict = echallan_pur_cd_restict;
    }
}
