/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.dobj.ExArmyDobj;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.NonUseDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.RestoreDisposeApplicationDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.common.Appl_Details_Dobj;
import nic.vahan.form.dobj.smartcard.DeleteSmartcardFlatFileDobj;
import nic.vahan.form.impl.agentlicence.AgentDetailImpl;
import nic.vahan.form.impl.commoncarrier.DetailCommonCarrierImpl;
import nic.vahan.form.impl.hsrp.HSRPRequestImpl;
import nic.vahan.form.impl.permit.CounterSignatureImpl;
import nic.vahan.form.impl.permit.PassengerPermitDetailImpl;
import nic.vahan.form.impl.permit.PermitEndorsementAppImpl;
import nic.vahan.form.impl.permit.PermitHomeAuthImpl;
import nic.vahan.form.impl.permit.SurrenderPermitImpl;
import nic.vahan.form.impl.permit.TemporaryPermitImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author nicsi
 */
public class RestoreDisposeApplicationImpl {

    private static final Logger LOGGER = Logger.getLogger(RestoreDisposeApplicationImpl.class);

    public static String getRegNoOfDisposeApplNo(String appl_no, String purCd) throws VahanException {

        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        String regnNo = "";
        try {
            tmgr = new TransactionManagerReadOnly("getRegNoOfDisposeApplNo");
            if (!CommonUtils.isNullOrBlank(purCd)) {
                sql = "SELECT  count(*) as regn_no from " + TableList.VHA_DETAILS
                        + " WHERE appl_no=? and pur_cd in (" + purCd + ")";
            } else {
                sql = "SELECT  regn_no from " + TableList.VHA_DETAILS
                        + " WHERE appl_no=?";
            }
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                if (!CommonUtils.isNullOrBlank(purCd) && rs.getInt("regn_no") > 1) {
                    throw new VahanException(Util.getLocaleMsg("restorNewWthTemp"));
                } else {
                    regnNo = rs.getString("regn_no");
                }
            }
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return regnNo;
    }

    public Map<String, Integer> listRestoreApplicationDispose(String appl_no, String state_cd, int off_cd, long userCode, String userCatg) {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        StringBuilder sql = null;
        StringBuilder whereConditionVar = null;

        StringBuilder selectVar = new StringBuilder("SELECT a.pur_cd,a.descr from tm_purpose_mast a "
                + " left join vha_details detail on a.pur_cd = detail.pur_cd "
                + " left join vha_status status on a.pur_cd = status.pur_cd  "
                + "  and status.appl_no=detail.appl_no  and status.status='D' ");

        if (userCatg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER)) {
            whereConditionVar = new StringBuilder(" WHERE (a.inward_appl='Y' OR a.pur_cd IN "
                    + " (" + TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE
                    + " ," + TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE
                    + " )) "
                    + "  and status.appl_no=? and (detail.entry_status <> 'A' or detail.entry_status is null) and status.state_cd=? and status.off_cd=? ");
        } else {
            whereConditionVar = new StringBuilder(" WHERE (a.inward_appl='Y' OR a.pur_cd IN "
                    + " (" + TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                    + " ," + TableConstants.VM_TRANSACTION_MAST_TEMP_REG
                    + " ," + TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE
                    + " ," + TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE
                    + " ," + TableConstants.VM_TRANSACTION_TRADE_CERT_NEW
                    + " ," + TableConstants.VM_TRANSACTION_TRADE_CERT_DUP
                    + " ," + TableConstants.VM_PMT_FRESH_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_RENEWAL_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_TEMP_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_SPECIAL_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_SURRENDER_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_CANCELATION_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_TRANSFER_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_SUSPENSION_PUR_CD // Added by Manoj
                    + " ," + TableConstants.VM_PMT_CA_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_REPLACE_VEH_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_RESTORE_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_APPLICATION_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_DUPLICATE_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD // Added by NAMAN
                    + " ," + TableConstants.TAX_EXAMPT_PUR_CD // Added by Niraj
                    + " ," + TableConstants.TAX_CLEAR_PUR_CD // Added by Ankur
                    + " ," + TableConstants.TAX_INSTALLMENT_PUR_CD // Added by Ankur
                    + " ," + TableConstants.VM_DUPLICATE_TO_TAX_CARD // Added by Ankur
                    + " ," + TableConstants.SWAPPING_REGN_PUR_CD // Added by Afzal
                    + " ," + TableConstants.ADMIN_OWNER_DATA_CHANGE
                    + " ," + TableConstants.STATEADMIN_OWNER_DATA_CHANGE
                    + " ," + TableConstants.AGENT_DETAIL_PUR_CD
                    + " ," + TableConstants.AGENT_DETAIL_DUP_PUR_CD
                    + " ," + TableConstants.AGENT_DETAIL_REN_PUR_CD
                    + " ," + TableConstants.VM_TRANSACTION_CARRIER_REGN
                    + " ," + TableConstants.VM_TRANSACTION_CARRIER_RENEWAL
                    + " ," + TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE_FITNESS
                    + " ," + TableConstants.VM_MAST_TEMP_RC_CANCEL
                    + " ," + TableConstants.RE_POSTAL_PUR_CD
                    + " ," + TableConstants.DELETE_SMART_CARD_FLAT_FILE
                    + " ," + TableConstants.DUPLICATE_HSRP_PUR_CD
                    + " ," + TableConstants.ENDORSMENT_TAX
                    + " )) "
                    + "  and status.appl_no=? and (detail.entry_status <> 'A' OR (detail.entry_status = 'A' AND detail.pur_cd = 25) or detail.entry_status is null) and status.state_cd=? and status.off_cd=? ");
        }
        if (appl_no != null) {
            appl_no = appl_no.toUpperCase().trim();
        }

        Map<String, Integer> purMap = new LinkedHashMap<>();

        try {
            tmgr = new TransactionManager("listRestoreApplicationDispose");
            if (userCatg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER)) {
                sql = selectVar.append(" left join vha_owner vo on vo.appl_no=detail.appl_no \n"
                        + " left join tm_user_permissions p on p.dealer_cd=vo.dealer_cd").append(whereConditionVar).append("and p.user_cd = ?");
            } else {
                sql = selectVar.append(whereConditionVar);
            }
            ps = tmgr.prepareStatement(sql.toString());
            ps.setString(1, appl_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            if (userCatg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER)) {
                ps.setLong(4, userCode);
            }
            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next())//found
            {
                purMap.put(rs.getString("descr"), rs.getInt("pur_cd")); //label,value
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return purMap;
    }

    public OwnerDetailsDobj getVhaOwnerDetails(String applNo, String stateCd, int offCd) throws VahanException {
        OwnerDetailsDobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            String where_condition = "";
            TmConfigurationDobj configurationDobj = Util.getTmConfiguration();
            if (Util.getUserCategory() == null || configurationDobj == null) {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
            if (!Util.getUserCategory().equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_USER) && !configurationDobj.isAllow_fitness_all_RTO()) {
                where_condition = " and off_cd=?";
            }

            String sql = " SELECT owner.*,type.descr as regn_type_descr,owcode.descr as owner_cd_descr "
                    + " FROM " + TableList.VIEW_VVHA_OWNER + " owner "
                    + " left join vm_regn_type type on owner.regn_type=type.regn_typecode "
                    + " left join vm_owcode owcode on owner.owner_cd = owcode.ow_code "
                    + " WHERE appl_no =? and state_cd=? " + where_condition + " order by moved_on desc limit 1";
            tmgr = new TransactionManager("getVaOwnerDetails");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, stateCd);
            if (!"".equalsIgnoreCase(where_condition)) {
                ps.setInt(3, offCd);
            }
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new OwnerDetailsDobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setState_name(rs.getString("state_name"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setOff_name(rs.getString("off_name"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setRegn_dt(rs.getString("regn_dt"));
                dobj.setRegnDateDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getRegn_dt())));
                dobj.setPurchase_dt(rs.getString("purchase_dt"));
                dobj.setPurchase_date(rs.getDate("purchase_dt"));
                dobj.setPurchaseDateDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getPurchase_dt())));
                dobj.setOwner_sr(rs.getInt("owner_sr"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setF_name(rs.getString("f_name"));
                dobj.setC_add1(rs.getString("c_add1"));
                dobj.setC_add2(rs.getString("c_add2"));
                dobj.setC_add3(rs.getString("c_add3"));
                dobj.setC_district(rs.getInt("c_district"));
                dobj.setC_district_name(rs.getString("c_district_name"));
                dobj.setC_state(rs.getString("c_state"));
                dobj.setC_state_name(rs.getString("c_state_name"));
                dobj.setC_pincode(rs.getInt("c_pincode"));
                dobj.setP_add1(rs.getString("p_add1"));
                dobj.setP_add2(rs.getString("p_add2"));
                dobj.setP_add3(rs.getString("p_add3"));
                dobj.setP_district(rs.getInt("p_district"));
                dobj.setP_district_name(rs.getString("p_district_name"));
                dobj.setP_state(rs.getString("p_state"));
                dobj.setP_state_name(rs.getString("p_state_name"));
                dobj.setP_pincode(rs.getInt("p_pincode"));
                dobj.setOwner_cd(rs.getInt("owner_cd"));
                dobj.setOwner_cd_descr(rs.getString("owner_cd_descr"));
                dobj.setRegn_type(rs.getString("regn_type"));
                dobj.setRegn_type_descr(rs.getString("regn_type_descr"));
                dobj.setVh_class(rs.getInt("vh_class"));
                dobj.setVh_class_desc(rs.getString("vh_class_desc"));
                dobj.setChasi_no(rs.getString("chasi_no"));
                dobj.setEng_no(rs.getString("eng_no"));
                dobj.setMaker(rs.getInt("maker"));
                dobj.setMaker_name(rs.getString("maker_name"));
                dobj.setModel_cd(rs.getString("model_cd"));
                dobj.setModel_name(rs.getString("model_name"));
                dobj.setBody_type(rs.getString("body_type"));
                dobj.setNo_cyl(rs.getInt("no_cyl"));
                dobj.setHp(rs.getFloat("hp"));
                dobj.setSeat_cap(rs.getInt("seat_cap"));
                dobj.setStand_cap(rs.getInt("stand_cap"));
                dobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                dobj.setUnld_wt(rs.getInt("unld_wt"));
                dobj.setLd_wt(rs.getInt("ld_wt"));
                dobj.setGcw(rs.getInt("gcw"));
                dobj.setFuel(rs.getInt("fuel"));
                dobj.setFuel_descr(rs.getString("fuel_descr"));
                dobj.setColor(rs.getString("color"));
                dobj.setManu_mon(rs.getInt("manu_mon"));
                dobj.setManu_yr(rs.getInt("manu_yr"));
                dobj.setNorms(rs.getInt("norms"));
                dobj.setNorms_descr(rs.getString("norms_descr"));
                dobj.setWheelbase(rs.getInt("wheelbase"));
                dobj.setCubic_cap(rs.getFloat("cubic_cap"));
                dobj.setFloor_area(rs.getFloat("floor_area"));
                dobj.setAc_fitted(rs.getString("ac_fitted"));
                dobj.setAudio_fitted(rs.getString("audio_fitted"));
                dobj.setVideo_fitted(rs.getString("video_fitted"));
                dobj.setVch_purchase_as_code(rs.getString("vch_purchase_as"));
                dobj.setVch_catg(rs.getString("vch_catg"));
                dobj.setDealer_cd(rs.getString("dealer_cd"));
                dobj.setDlr_name(rs.getString("dlr_name"));
                dobj.setDlr_add1(rs.getString("dlr_add1"));
                dobj.setDlr_add2(rs.getString("dlr_add2"));
                dobj.setDlr_add3(rs.getString("dlr_add3"));
                dobj.setDlr_city(rs.getString("dlr_city"));
                dobj.setDlr_district(rs.getString("dlr_district"));
                dobj.setDlr_pincode(rs.getString("dlr_pincode"));
                dobj.setSale_amt(rs.getInt("sale_amt"));
                dobj.setLaser_code(rs.getString("laser_code"));
                dobj.setGarage_add(rs.getString("garage_add"));
                dobj.setLength(rs.getInt("length"));
                dobj.setWidth(rs.getInt("width"));
                dobj.setHeight(rs.getInt("height"));
                dobj.setRegn_upto(rs.getString("regn_upto"));
                dobj.setRegnUptoDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getRegn_upto())));
                dobj.setFit_upto(rs.getString("fit_upto"));
                dobj.setFitUptoDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getFit_upto())));
                dobj.setAnnual_income(rs.getInt("annual_income"));
                dobj.setOp_dt(rs.getString("op_dt"));
                dobj.setImported_vch(rs.getString("imported_vch"));
                dobj.setOther_criteria(rs.getInt("other_criteria"));
                dobj.setPmt_type(rs.getInt("pmt_type"));
                dobj.setPmt_catg(rs.getInt("pmt_catg"));
                dobj.setRqrd_tax_modes(rs.getString("rqrd_tax_modes"));
                dobj.setVehType(ServerUtil.VehicleClassType(dobj.getVh_class()));

                if (ServerUtil.isTransport(dobj.getVh_class(), null)) {
                    dobj.setVehTypeDescr(TableConstants.VM_VEHTYPE_TRANSPORT_DESCR);
                    dobj.setVehType(TableConstants.VM_VEHTYPE_TRANSPORT);
                    dobj.setVch_type(TableConstants.VM_VEHTYPE_TRANSPORT_DESCR);
                } else {
                    dobj.setVehTypeDescr(TableConstants.VM_VEHTYPE_NON_TRANSPORT_DESCR);
                    dobj.setVehType(TableConstants.VM_VEHTYPE_NON_TRANSPORT);
                    dobj.setVch_type(TableConstants.VM_VEHTYPE_NON_TRANSPORT_DESCR);
                }

                if (dobj.getVch_purchase_as_code().equalsIgnoreCase("B")) {
                    dobj.setVch_purchase_as("Fully Built");
                } else {
                    dobj.setVch_purchase_as("Drive Away Chasis");
                }

                //Setting Owenr Identification Details                
                String sqlOwnerId = "SELECT * FROM " + TableList.VA_OWNER_IDENTIFICATION
                        + " WHERE appl_no =?";
                ps = tmgr.prepareStatement(sqlOwnerId);
                ps.setString(1, applNo);
                rs = tmgr.fetchDetachedRowSet_No_release();
                OwnerIdentificationDobj own_identity = fillOwnerIdentityDobj(rs);
                if (own_identity != null) {
                    dobj.setOwnerIdentity(own_identity);
                }
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return dobj;
    }

    public OwnerIdentificationDobj fillOwnerIdentityDobj(RowSet rsOwnerId) throws SQLException {
        OwnerIdentificationDobj dobj = new OwnerIdentificationDobj();

        if (rsOwnerId.next()) {
            dobj.setState_cd(rsOwnerId.getString("state_cd"));
            dobj.setOff_cd(rsOwnerId.getInt("off_cd"));
            dobj.setRegn_no(rsOwnerId.getString("regn_no"));
            dobj.setMobile_no(rsOwnerId.getLong("mobile_no"));
            dobj.setEmail_id(rsOwnerId.getString("email_id"));
            dobj.setPan_no(rsOwnerId.getString("pan_no"));
            dobj.setAadhar_no(rsOwnerId.getString("aadhar_no"));
            dobj.setPassport_no(rsOwnerId.getString("passport_no"));
            dobj.setRation_card_no(rsOwnerId.getString("ration_card_no"));
            dobj.setVoter_id(rsOwnerId.getString("voter_id"));
            dobj.setDl_no(rsOwnerId.getString("dl_no"));
            dobj.setVerified_on(rsOwnerId.getDate("verified_on"));
            dobj.setOwnerCatg(rsOwnerId.getInt("owner_ctg"));
            dobj.setOwnerCdDept(rsOwnerId.getInt("dept_cd"));
        }
        return dobj;
    }

    public ExArmyDobj setVHAExArmyDetails_db_to_dobj(String appl_no, String regn_no, String state_cd, int off_cd) {
        ExArmyDobj dobj = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = "SELECT voucher_no,voucher_dt,place FROM " + TableList.VHA_OWNER_EX_ARMY + " where appl_no = ? order by moved_on desc limit 1";
        try {
            tmgr = new TransactionManagerReadOnly("setVHAExArmyDetails_db_to_dobj");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no.toUpperCase());

            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new ExArmyDobj();
                dobj.setTf_Voucher_no(rs.getString("voucher_no"));
                dobj.setTf_VoucherDate((java.util.Date) rs.getDate("voucher_dt"));
                dobj.setTf_POP(rs.getString("place"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return dobj;
    }

    public AxleDetailsDobj setVHAAxleVehDetails_db_to_dobj(String appl_no) {
        AxleDetailsDobj dobj = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        String condition = null;
        try {
            tmgr = new TransactionManagerReadOnly("setVHAAxleVehDetails_db_to_dobj");
            condition = appl_no.toUpperCase();
            sql = "SELECT * FROM " + TableList.VHA_AXLE + " WHERE appl_no=? order by moved_on desc limit 1";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, condition);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new AxleDetailsDobj();
                dobj.setNoOfAxle(rs.getInt("no_of_axles"));
                dobj.setTf_Front(rs.getInt("f_axle_weight"));
                dobj.setTf_Front1(rs.getString("f_axle_descp"));

                dobj.setTf_Rear(rs.getInt("r_axle_weight"));
                dobj.setTf_Rear1(rs.getString("r_axle_descp"));

                dobj.setTf_Other(rs.getInt("o_axle_weight"));
                dobj.setTf_Other1(rs.getString("o_axle_descp"));

                dobj.setTf_Tandem(rs.getInt("t_axle_weight"));
                dobj.setTf_Tandem1(rs.getString("t_axle_descp"));

                dobj.setTf_Rear_Over(rs.getInt("r_overhang"));

                dobj.setTf_Front_tyre(rs.getInt("f_axle_tyre"));
                dobj.setTf_Rear_tyre(rs.getInt("r_axle_tyre"));
                dobj.setTf_Other_tyre(rs.getInt("o_axle_tyre"));
                dobj.setTf_Tandem_tyre(rs.getInt("t_axle_tyre"));

            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return dobj;
    }

    public InsDobj set_VHAins_dtls_db_to_dobj(String appl_no) throws VahanException {
        InsDobj ins_dobj = null;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        String pvalue = null;
        String query = "";
        try {
            if (appl_no != null && !appl_no.isEmpty()) {
                tmgr = new TransactionManagerReadOnly("set_VHAins_dtls_db_to_dobj");
                if (appl_no != null && !appl_no.isEmpty()) {
                    pvalue = appl_no.toUpperCase();
                    query = "select regn_no,comp_cd,descr, "
                            + "ins_type, "
                            + "ins_from, "
                            + "ins_upto,policy_no,idv,op_dt "
                            + " from " + TableList.VHA_INSURANCE + " a left join  " + TableList.VM_ICCODE
                            + " b on a.comp_cd=b.ic_code"
                            + " where appl_no = ? order by moved_on desc limit 1";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, pvalue);
                }
                RowSet rs = tmgr.fetchDetachedRowSet();

                if (rs.next()) {
                    ins_dobj = new InsDobj();
                    ins_dobj.setComp_cd(rs.getInt("comp_cd"));
                    ins_dobj.setIns_type(rs.getInt("ins_type"));
                    ins_dobj.setIns_from(rs.getDate("ins_from"));
                    ins_dobj.setIns_upto(rs.getDate("ins_upto"));
                    ins_dobj.setPolicy_no(rs.getString("policy_no"));
                    ins_dobj.setIdv(rs.getLong("idv"));
                    ins_dobj.setInsCompName(rs.getString("descr"));
                    ins_dobj.setRegn_no(rs.getString("regn_no"));
                    ins_dobj.setOp_dt(rs.getDate("op_dt"));
                }
            }
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + "-" + sqle.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            }
        }

        return ins_dobj;
    }

    public List<HpaDobj> getVHAHypoDetails(String appl_no, int pur_cd) throws SQLException, Exception {

        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        String sql = "";
        HpaDobj hpa_dobj = null;
        List<HpaDobj> listHypoDetails = new ArrayList<>();

        if (appl_no != null) {
            appl_no = appl_no.toUpperCase();
        }
        try {

            tmgr = new TransactionManagerReadOnly("getVHAHypoDetails");


            sql = " SELECT  appl_no,regn_no,sr_no,hp_type,h.hp_type_descr,fncr_name,fncr_add1,fncr_add2,fncr_add3,"
                    + "         fncr_district,d.descr as fncr_district_descr,fncr_state,s.descr as fncr_state_name,fncr_pincode,"
                    + "         from_dt,to_char(from_dt,'DD-MON-YYYY') as from_dt_descr"
                    + "   FROM  " + TableList.VHA_HPA
                    + "         left join tm_state s on fncr_state = s.state_code "
                    + "         left join tm_district d on fncr_district = d.dist_cd "
                    + "         left join vm_hp_type h on hp_type = h.hp_type_cd "
                    + "  where  moved_on  =(select max (moved_on) from vha_hpa where appl_no=?) order by sr_no";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                hpa_dobj = new HpaDobj();

                if (pur_cd == TableConstants.VM_TRANSACTION_MAST_ADD_HYPO) {
                    hpa_dobj.setAppl_no(rs.getString("appl_no"));
                }
                hpa_dobj.setRegn_no(rs.getString("regn_no"));
                hpa_dobj.setSr_no(rs.getInt("sr_no"));
                hpa_dobj.setFncr_add1(rs.getString("fncr_add1"));
                hpa_dobj.setFncr_add2(rs.getString("fncr_add2"));
                hpa_dobj.setFncr_add3(rs.getString("fncr_add3"));
                hpa_dobj.setFncr_name(rs.getString("fncr_name"));
                hpa_dobj.setFncr_district(rs.getInt("fncr_district"));
                hpa_dobj.setFncr_district_descr(rs.getString("fncr_district_descr"));
                hpa_dobj.setFncr_state(rs.getString("fncr_state"));
                hpa_dobj.setFncr_state_name(rs.getString("fncr_state_name"));
                hpa_dobj.setFncr_pincode(rs.getInt("fncr_pincode"));
                hpa_dobj.setFrom_dt(rs.getDate("from_dt"));
                hpa_dobj.setFrom_dt_descr(rs.getString("from_dt_descr"));
                hpa_dobj.setHp_type(rs.getString("hp_type"));
                hpa_dobj.setHp_type_descr(rs.getString("hp_type_descr"));

                listHypoDetails.add(hpa_dobj);

            }

        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return listHypoDetails;
    }

    public void reStoreDisposeApplications(Status_dobj status, int sizeOfSelectedApplforDispose, int sizsOfApplInwarded, Owner_dobj ownerDobj, RestoreDisposeApplicationDobj restoreDisposeDobj) throws Exception {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String tableName = "";
        String sql = "";
        PassengerPermitDetailImpl passImpl = null;
        TemporaryPermitImpl tempImpl = null;
        PermitHomeAuthImpl authImpl = null;
        TmConfigurationDobj configDobj = null;
        boolean pmtFlag = false;

        try {
            tmgr = new TransactionManager("reStoreDisposeApplications");
            sql = "INSERT INTO vahan4.vt_restore_dispose_appl(\n"
                    + "            state_cd, off_cd, regn_no, pur_cd, appl_no, remark, entered_by, \n"
                    + "            entered_on)\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, \n"
                    + "            current_timestamp)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, status.getState_cd());
            ps.setInt(2, status.getOff_cd());
            ps.setString(3, status.getRegn_no());
            ps.setInt(4, status.getPur_cd());
            ps.setString(5, status.getAppl_no());
            ps.setString(6, status.getOffice_remark());
            ps.setString(7, Util.getEmpCode());
            ps.executeUpdate();

            configDobj = Util.getTmConfiguration();
            switch (status.getPur_cd()) {
                case TableConstants.ADMIN_OWNER_DATA_CHANGE://
                case TableConstants.STATEADMIN_OWNER_DATA_CHANGE:
                case TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE:
                case TableConstants.VM_TRANSACTION_MAST_TEMP_REG:
                case TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE:
                case TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE:
                    for (int i = 0; i < TableList.VAList.size(); i++) {
                        insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHAList.get(i).toString(), TableList.VAList.get(i).toString());
                    }

                    if ((status.getPur_cd() == TableConstants.ADMIN_OWNER_DATA_CHANGE || status.getPur_cd() == TableConstants.STATEADMIN_OWNER_DATA_CHANGE) && restoreDisposeDobj.isCheckPurcdOwnerAdmin()) {
                        insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_OWNER, TableList.VA_OWNER_TEMP_ADMIN);
                    }
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), "vha_owner_temp", "va_owner_temp");

                    if ("DL".equals(status.getState_cd()) && status.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {

                        sql = "insert into " + TableList.VP_BANK_SUBSIDY + " select state_cd, off_cd, appl_no, regn_no, ll_dl_no, bank_code, bank_ifsc_cd, \n"
                                + "            bank_ac_no, aadhar_no, subsidy_amount, status, op_dt from " + TableList.VPH_BANK_SUBSIDY + " a WHERE a.appl_no=? order by a.moved_on desc limit 1";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, status.getAppl_no());
                        ps.executeUpdate();
                    }
                    //for reuse of receipt amount paid for fancy no

                    sql = " select regn_appl_no,regn_no \n"
                            + " from " + TableList.VH_ADVANCE_REGN_NO
                            + " where regn_appl_no = ? order by reopen_on desc limit 1 ";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, status.getAppl_no());
                    RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        sql = "UPDATE " + TableList.VT_ADVANCE_REGN_NO
                                + " SET regn_appl_no = ? WHERE regn_no=?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, rs.getString("regn_appl_no"));
                        ps.setString(2, rs.getString("regn_no"));
                        ps.executeUpdate();
                    }

                    sql = "select state_cd,off_cd,regn_appl_no, old_regn_no, new_regn_no, \n"
                            + "            rcpt_no \n"
                            + " from " + TableList.VH_SURRENDER_RETENTION
                            + " where regn_appl_no = ? order by moved_on desc limit 1";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, status.getAppl_no());
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        sql = "Update " + TableList.VT_SURRENDER_RETENTION + " Set regn_appl_no = ? Where rcpt_no = ? and state_cd=? and off_cd=?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, rs.getString("regn_appl_no"));
                        ps.setString(2, rs.getString("rcpt_no"));
                        ps.setString(3, rs.getString("state_cd"));
                        ps.setInt(4, rs.getInt("off_cd"));
                        ps.executeUpdate();
                    }
                    sql = "insert into " + TableList.VT_SCRAP_VEHICLE
                            + " select state_cd, off_cd, loi_no, old_regn_no, old_chasi_no, new_regn_no, \n"
                            + "            new_chasi_no, agency_name, agency_address, no_dues_cert_no, no_dues_issue_dt, \n"
                            + "            scrap_cert_no, scrap_cert_issue_dt, retain_regn_no, scrap_reason, \n"
                            + "            op_dt, regn_appl_no "
                            + " from " + TableList.VH_SCRAP_VEHICLE
                            + " where regn_appl_no = ? order by moved_on desc limit 1";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, status.getAppl_no());
                    ps.executeUpdate();

                    //for handling case new regn against scrapped vehicle --End---

                    if (ownerDobj != null
                            && (status.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                            || status.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) && !"NEW".equalsIgnoreCase(ownerDobj.getRegn_no())) {

                        RegVehCancelReceiptImpl regVehCancelReceiptImpl = new RegVehCancelReceiptImpl();
                        VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj);
                        int seriesId = regVehCancelReceiptImpl.getSeriesId(vehParameters);

                        String allotedNo = ServerUtil.getRegnNoAllotedDetail(status.getAppl_no(), status.getState_cd(), status.getOff_cd());
                        if (allotedNo != null && allotedNo.trim().length() > 0) {

                            throw new VahanException(Util.getLocaleMsg("home_rightpanelregno") + "[" + ownerDobj.getRegn_no() + "]" + Util.getLocaleMsg("generated") + Util.getLocaleMsg("home_rightpanelApplno") + " [" + status.getAppl_no() + "] " + Util.getLocaleMsg("cannotRestore"));
                        }
                    }
                    if (status.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE
                            || status.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE
                            || status.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                            || status.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
                        if (configDobj != null && configDobj.getTmConfigDmsDobj() != null && configDobj.getTmConfigDmsDobj().isDigitalSignAllowStateWise()) {
                            restoreIntoVtDScDocDetailsTable(tmgr, status.getAppl_no(), status.getState_cd(), TableList.VH_DSC_DOC_DETAILS, TableList.VT_DSC_DOC_DETAILS);
                        }
                    }
                    break;

                case TableConstants.VM_TRANSACTION_MAST_CHG_ADD:

                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_CA, TableList.VA_CA);
                    //for case of CA without NOC
                    FitnessImpl fitnessImpl = new FitnessImpl();
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_INSPECTION, TableList.VA_INSPECTION);
                    break;

                case TableConstants.VM_TRANSACTION_MAST_TO:
                case TableConstants.VM_TRANSACTION_MAST_FRESH_RC:
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_TO, TableList.VA_TO);
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_SURRENDER_RETENTION, TableList.VA_SURRENDER_RETENTION);
                    sql = "select state_cd,off_cd,regn_appl_no, old_regn_no, new_regn_no, \n"
                            + "            rcpt_no \n"
                            + " from " + TableList.VH_SURRENDER_RETENTION
                            + " where regn_appl_no = ? order by moved_on desc limit 1";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, status.getAppl_no());
                    rs = tmgr.fetchDetachedRowSet_No_release();//fetchDetachedRowSet
                    if (rs.next()) {
                        sql = "Update " + TableList.VT_SURRENDER_RETENTION + " Set regn_appl_no = ? Where rcpt_no = ? and state_cd=? and off_cd=?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, rs.getString("regn_appl_no"));
                        ps.setString(2, rs.getString("rcpt_no"));
                        ps.setString(3, rs.getString("state_cd"));
                        ps.setInt(4, rs.getInt("off_cd"));
                        ps.executeUpdate();
                    }

                    //Restore
                    sql = "insert into " + TableList.VA_FRC_PRINT
                            + " select state_cd, off_cd, appl_no, regn_no, op_dt "
                            + " from " + TableList.VHA_FRC_PRINT
                            + " where appl_no = ? ";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, status.getAppl_no());
                    ps.executeUpdate();



                    sql = " select regn_appl_no,regn_no \n"
                            + " from " + TableList.VH_ADVANCE_REGN_NO
                            + " where regn_appl_no = ? order by reopen_on desc limit 1 ";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, status.getAppl_no());
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        sql = "UPDATE " + TableList.VT_ADVANCE_REGN_NO
                                + " SET regn_appl_no = ? WHERE regn_no=?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, rs.getString("regn_appl_no"));
                        ps.setString(2, rs.getString("regn_no"));
                        ps.executeUpdate();
                    }

                    //end
                    break;

                case TableConstants.VM_TRANSACTION_MAST_NOC:
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_NOC, TableList.VA_NOC);
                    break;

                case TableConstants.VM_TRANSACTION_MAST_NOC_CANCEL:
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_NOC_CANCEL, TableList.VA_NOC_CANCEL);
                    break;

                case TableConstants.VM_TRANSACTION_MAST_DUP_RC:
                case TableConstants.VM_TRANSACTION_MAST_DUP_FC:
                    restoreIntoDupHistory(tmgr, status.getAppl_no(), status.getPur_cd());
                    break;

                case TableConstants.VM_TRANSACTION_MAST_FIT_CERT:
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_FITNESS, TableList.VA_FITNESS);
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_SPEED_GOVERNOR, TableList.VA_SPEED_GOVERNOR);
                    break;

                case TableConstants.VM_TRANSACTION_MAST_ADD_HYPO:
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_HPA, TableList.VA_HPA);
                    break;

                case TableConstants.VM_TRANSACTION_MAST_REM_HYPO:
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_HPT, TableList.VA_HPT);
                    break;

                case TableConstants.VM_TRANSACTION_MAST_HPC:
                    break;

                case TableConstants.VM_TRANSACTION_MAST_REN_REG:
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_RENEWAL, TableList.VA_RENEWAL);
                    break;

                case TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION:
                    Appl_Details_Dobj dobj = new Appl_Details_Dobj();
                    dobj.setCurrentEmpCd(String.valueOf(status.getEmp_cd()));
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_CONVERSION, TableList.VA_CONVERSION);
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_NUM_GEN_PERMITDETAILS, TableList.VA_NUM_GEN_PERMITDETAILS);
                    break;
                case TableConstants.VM_TRANSACTION_MAST_VEH_ALTER:
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_ALT, TableList.VA_ALT);
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_TRAILER, TableList.VA_TRAILER);
                    break;
                case TableConstants.VM_MAST_RC_SURRENDER:
                    reStoreVaRcReleaseSurrenderCancellantionDispose(status, tmgr);
                    break;
                case TableConstants.VM_MAST_RC_RELEASE:
                    reStoreVaRcReleaseSurrenderCancellantionDispose(status, tmgr);
                    break;
                case TableConstants.VM_MAST_RC_CANCELLATION:
                    reStoreVaRcReleaseSurrenderCancellantionDispose(status, tmgr);
                    break;
                case TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE_FITNESS:
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_OWNER, TableList.VA_OWNER);
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_OWNER_IDENTIFICATION, TableList.VA_OWNER_IDENTIFICATION);
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), "vha_owner_temp", "va_owner_temp");
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_TMP_REGN_DTL, TableList.VA_TMP_REGN_DTL);
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_SIDE_TRAILER, TableList.VA_SIDE_TRAILER);
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_RETROFITTING_DTLS, TableList.VA_RETROFITTING_DTLS);
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_AXLE, TableList.VA_AXLE);
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_SPEED_GOVERNOR, TableList.VA_SPEED_GOVERNOR);
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_FITNESS_CHASSIS, TableList.VA_FITNESS_CHASSIS);
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_REFLECTIVE_TAPE, TableList.VA_REFLECTIVE_TAPE);
                    break;
                case TableConstants.VM_TRANSACTION_TRADE_CERT_NEW:
                case TableConstants.VM_TRANSACTION_TRADE_CERT_DUP:
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_TRADE_CERTIFICATE, TableList.VA_TRADE_CERTIFICATE);
                    break;
                case TableConstants.VM_PMT_FRESH_PUR_CD:
                case TableConstants.VM_PMT_APPLICATION_PUR_CD:
                    passImpl = new PassengerPermitDetailImpl();
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_PERMIT, TableList.VA_PERMIT);
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_PERMIT_OWNER, TableList.VA_PERMIT_OWNER);
                    pmtFlag = passImpl.applPermitIsThere(status.getAppl_no(), TableConstants.VM_PMT_APPLICATION_PUR_CD);
                    restorePermitPrintTOvaPermitPrint(status.getAppl_no(), tmgr, "3");
                    break;
                case TableConstants.VM_PMT_RENEWAL_PUR_CD:
                    passImpl = new PassengerPermitDetailImpl();
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_PERMIT, TableList.VA_PERMIT);
                    break;
                case TableConstants.VM_PMT_TEMP_PUR_CD:
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_TEMP_PERMIT, TableList.VA_TEMP_PERMIT);
                    break;
                case TableConstants.VM_PMT_SPECIAL_PUR_CD:
                    tempImpl = new TemporaryPermitImpl();
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_TEMP_PERMIT, TableList.VA_TEMP_PERMIT);
                    break;
                case TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD:
                    authImpl = new PermitHomeAuthImpl();
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_PERMIT_HOME_AUTH, TableList.VA_PERMIT_HOME_AUTH);
                    break;
                case TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO:
                    Rereg_Impl.insertIntoReRegHistory(tmgr, status.getAppl_no());
                    //for reuse of receipt amount paid for fancy no
                    sql = " select regn_appl_no,regn_no \n"
                            + " from " + TableList.VH_ADVANCE_REGN_NO
                            + " where regn_appl_no = ? order by reopen_on desc limit 1 ";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, status.getAppl_no());
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        sql = "UPDATE " + TableList.VT_ADVANCE_REGN_NO
                                + " SET regn_appl_no = ? WHERE regn_no=?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, rs.getString("regn_appl_no"));
                        ps.setString(2, rs.getString("regn_no"));
                        ps.executeUpdate();
                    }
                    sql = "select state_cd,off_cd,regn_appl_no, old_regn_no, new_regn_no, \n"
                            + "            rcpt_no \n"
                            + " from " + TableList.VH_SURRENDER_RETENTION
                            + " where regn_appl_no = ? order by moved_on desc limit 1";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, status.getAppl_no());
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        sql = "Update " + TableList.VT_SURRENDER_RETENTION + " Set regn_appl_no = ? Where rcpt_no = ? and state_cd=? and off_cd=?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, rs.getString("regn_appl_no"));
                        ps.setString(2, rs.getString("rcpt_no"));
                        ps.setString(3, rs.getString("state_cd"));
                        ps.setInt(4, rs.getInt("off_cd"));
                        ps.executeUpdate();
                    }



                    break;
                case TableConstants.VM_PMT_DUPLICATE_PUR_CD:
                    //DupImpl.insertIntoDupHistory(tmgr, status.getAppl_no(), status.getPur_cd());
                    restoreIntoDupHistory(tmgr, status.getAppl_no(), status.getPur_cd());

                    break;
                case TableConstants.VM_PMT_SURRENDER_PUR_CD:
                case TableConstants.VM_PMT_TRANSFER_PUR_CD:
                case TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD:
                case TableConstants.VM_PMT_CA_PUR_CD:
                case TableConstants.VM_PMT_REPLACE_VEH_PUR_CD:
                case TableConstants.VM_PMT_RESTORE_PUR_CD:
                case TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD:
                case TableConstants.VM_PMT_SUSPENSION_PUR_CD:
                    SurrenderPermitImpl.vhaPermitTransactionToVaPermitTransaction(tmgr, status.getAppl_no());//
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_PERMIT_TRANSACTION, TableList.VA_PERMIT_TRANSACTION);
                    if (status.getPur_cd() == TableConstants.VM_PMT_RESTORE_PUR_CD
                            || status.getPur_cd() == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD
                            || status.getPur_cd() == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD
                            || status.getPur_cd() == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD) {
                        String pmt_status = SurrenderPermitImpl.checkPermitIn_VT_Permit(status.getRegn_no());
                        if (!CommonUtils.isNullOrBlank(pmt_status)) {
                            restoreVhPermitTranactionToVtPermitTranaction(tmgr, status.getRegn_no(), status.getAppl_no());
                        }
                    }
                    break;
                case TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS:
                case TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_FOR_OFFICE_PURPOSE:
                    break;
                case TableConstants.VM_MAST_VEHICLE_SCRAPE:
                    ScrappedVehicleImpl scaImpl = new ScrappedVehicleImpl();
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_SCRAP_VEHICLE, TableList.VA_SCRAP_VEHICLE);
                    break;
                case TableConstants.TM_PURPOSE_FITNESS_CANCELLATION:
                    FitnessCancellationImpl fitnessCancellationImpl = new FitnessCancellationImpl();
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_FIT_CANCEL, TableList.VA_FIT_CANCEL);
                    break;
                case TableConstants.VM_PMT_CANCELATION_PUR_CD:
                case TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD:
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_PERMIT_TRANSACTION, TableList.VA_PERMIT_TRANSACTION);
                    break;
                case TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD:
                    CounterSignatureImpl counterImp = new CounterSignatureImpl();
                    counterImp.moveVaToVha(tmgr, status.getAppl_no());
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), "permit.vha_permit_countersignature", "permit.va_permit_countersignature");
                    break;
                case TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD:
                    PermitEndorsementAppImpl endImp = new PermitEndorsementAppImpl();
                    restoreEndorsmentApplication(tmgr, status.getRegn_no());
                    break;
                case TableConstants.TAX_EXAMPT_PUR_CD://
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_TAX_EXEM, TableList.VA_TAX_EXEM);
                    break;
                case TableConstants.TAX_CLEAR_PUR_CD:
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_TAX_CLEAR, TableList.VA_TAX_CLEAR);
                    break;
                case TableConstants.TAX_INSTALLMENT_PUR_CD://
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_TAX_INSTALLMENT, TableList.VA_TAX_INSTALLMENT);
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_TAX_INSTALLMENT_BRKUP, TableList.VA_TAX_INSTALLMENT_BRKUP);
                    break;
                case TableConstants.VM_DUPLICATE_TO_TAX_CARD://

                    //inserting data into VA_TCC_PRINT from VHA_TCC_PRINT
                    sql = "INSERT INTO " + TableList.VA_TCC_PRINT
                            + " SELECT state_cd, off_cd, appl_no, regn_no, op_dt, tcc_no "
                            + "  FROM  " + TableList.VHA_TCC_PRINT
                            + " WHERE  appl_no=? order by printed_on desc limit 1";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, status.getAppl_no());
                    ps.executeUpdate();
                    // insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_TCC_PRINT, TableList.VA_TCC_PRINT);
                    break;

                case TableConstants.VM_MAST_NON_USE:
                    NonUseImpl impl = new NonUseImpl();
                    restoreInNonUseDetails(tmgr, null, status);
                    break;
                case TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE:
                    NonUseImpl nonUseImpl = new NonUseImpl();
                    restoreInNonUseDetails(tmgr, null, status);
                    break;
                case TableConstants.RE_POSTAL_PUR_CD:
                    break;
                case TableConstants.SWAPPING_REGN_PUR_CD:
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_RETENTION, TableList.VA_RETENTION);
                    break;
                case TableConstants.VM_TRANSACTION_MAST_FIT_REVOKE://
                    FitnessCancellationImpl fitCancellationImpl = new FitnessCancellationImpl();
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_FIT_REVOKE, TableList.VA_FIT_REVOKE);
                    break;
                case TableConstants.AGENT_DETAIL_REN_PUR_CD:
                case TableConstants.AGENT_DETAIL_DUP_PUR_CD:
                case TableConstants.AGENT_DETAIL_PUR_CD:
                    AgentDetailImpl agentDetailImpl = new AgentDetailImpl();
                    agentDetailImpl.insertIntoVhaAgentDetails(status.getAppl_no(), tmgr);//
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_AGENT_DETAILS, TableList.VA_AGENT_DETAILS);
                    break;
                case TableConstants.VM_TRANSACTION_CARRIER_REGN:
                case TableConstants.VM_TRANSACTION_CARRIER_RENEWAL:
                    DetailCommonCarrierImpl detailCommonCarrierImpl = new DetailCommonCarrierImpl();
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_COMMON_CARRIERS, TableList.VA_COMMON_CARRIERS);
                    break;
                case TableConstants.VM_TRANSACTION_MAST_RC_TO_SMART_CARD_CONVERSION:
                    break;
                case TableConstants.VM_MAST_TEMP_RC_CANCEL:
                    TempRcCancelImpl TempCanImpl = new TempRcCancelImpl();
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_TEMP_RC_CANCEL, TableList.VA_TEMP_RC_CANCEL);
                    break;
                case TableConstants.DELETE_SMART_CARD_FLAT_FILE:
                    DeleteSmartcardFlatFileDobj deleteDobj = new DeleteSmartcardFlatFileDobj();
                    deleteDobj.setAppl_no(status.getAppl_no());
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_REMOVE_RC_BE_TO_BO, TableList.VA_REMOVE_RC_BE_TO_BO);
                    break;
                case TableConstants.TAX_EXAMPT_CANCEL_PUR_CD:
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_TAX_EXEM_CANCEL, TableList.VA_TAX_EXEM_CANCEL);
                    break;
                case TableConstants.RETENTION_OF_REGISTRATION_NO_PUR_CD:
                    ToImpl tImpl = new ToImpl();
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_SURRENDER_RETENTION, TableList.VA_SURRENDER_RETENTION);
                    break;
                case TableConstants.DUPLICATE_HSRP_PUR_CD://
                    HSRPRequestImpl.insertIntoHsrpHistory(tmgr, status.getRegn_no(), status.getAppl_no());
                    insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), "hsrp.vha_hsrp_dup", "hsrp.va_hsrp_dup");
                    break;
                default:
                    throw new VahanException(Util.getLocaleMsg("entry_purpose") + status.getPur_cd() + ")" + Util.getLocaleMsg("restoreNotAvailbe"));
            }


            //**************************************history mantain when will be dispose

//            sql = "insert into vt_special_order select state_cd, off_cd, regn_no, appl_no, reason, order_by, order_dt, \n"
//                    + "            requested_by, op_dt from vh_special_order WHERE regn_no=? order by moved_on desc limit 1";
//            ps = tmgr.prepareStatement(sql);
//            ps.setString(1, status.getRegn_no());
//            ps.executeUpdate();


            sql = "select state_cd, off_cd, regn_no, appl_no \n"
                    + "            from vh_special_order WHERE regn_no=? order by moved_on desc limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, status.getRegn_no());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                sql = "UPDATE " + TableList.VT_SPECIAL_ORDER + " SET appl_no=?,op_dt=current_timestamp"
                        + " WHERE regn_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, status.getAppl_no());
                ps.setString(2, rs.getString("regn_no"));
                ps.executeUpdate();
            }
//****************************************************************end 

            if (sizsOfApplInwarded == sizeOfSelectedApplforDispose && (status.getPur_cd() != TableConstants.STATEADMIN_OWNER_DATA_CHANGE && status.getPur_cd() != TableConstants.ADMIN_OWNER_DATA_CHANGE && status.getPur_cd() != TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                    && status.getPur_cd() != TableConstants.VM_TRANSACTION_MAST_TEMP_REG && status.getPur_cd() != TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE && status.getPur_cd() != TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE)) {
                insertIntoVaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_INSURANCE, TableList.VA_INSURANCE);
            }


            //for handling HPC/Duplicate TO TAX fee cancellation because there is no next flow for HPC/Duplicate TO TAX
            String restoreStatus = "";
            if (status.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_HPC
                    || status.getPur_cd() == TableConstants.VM_DUPLICATE_TO_TAX_CARD) {
                sql = " INSERT INTO " + TableList.VA_STATUS
                        + "       SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno+2, \n"
                        + "            action_cd, ?, cntr_id, ? as status, office_remark, public_remark, \n"
                        + "            file_movement_type, ? as emp_cd, current_timestamp as op_dt "
                        + "       FROM " + TableList.VHA_STATUS
                        + " a WHERE appl_no=? and pur_cd=? order by a.moved_on desc limit 1";
            } else if ((status.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD
                    || status.getPur_cd() == TableConstants.VM_PMT_APPLICATION_PUR_CD)
                    && pmtFlag) {

                sql = " INSERT INTO " + TableList.VA_STATUS
                        + "       SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno+2, \n"
                        + "            action_cd, ?, cntr_id,  ? as status, office_remark, public_remark, \n"
                        + "            file_movement_type, ? as emp_cd, current_timestamp as op_dt "
                        + "       FROM " + TableList.VHA_STATUS
                        + " a WHERE appl_no=? order by a.moved_on desc limit 1";
            } else {

                sql = " INSERT INTO " + TableList.VA_STATUS
                        + "       SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno+2, \n"
                        + "            action_cd, ?, cntr_id,  ? as status, office_remark, public_remark, \n"
                        + "            file_movement_type, ? as emp_cd, current_timestamp as op_dt "
                        + "       FROM " + TableList.VHA_STATUS
                        + " a WHERE appl_no=? and pur_cd=? order by a.moved_on desc limit 1";
            }
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, TableConstants.RESTOREDISPOSE);
            ps.setString(i++, "N");

            ps.setLong(i++, status.getEmp_cd());

            ps.setString(i++, status.getAppl_no());
            if (!((status.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD
                    || status.getPur_cd() == TableConstants.VM_PMT_APPLICATION_PUR_CD)
                    && pmtFlag)) {
                ps.setInt(i++, status.getPur_cd());
            }
            ps.executeUpdate();

            //***************************************insert into vha_status *********************************************************************
            if (status.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_HPC
                    || status.getPur_cd() == TableConstants.VM_DUPLICATE_TO_TAX_CARD) {
                sql = " INSERT INTO " + TableList.VHA_STATUS
                        + "       SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno+1,"
                        + "       action_cd, seat_cd, cntr_id, ? as status, ? as office_remark, public_remark,"
                        + "       file_movement_type, ? as emp_cd, op_dt, current_timestamp as moved_on,? "
                        + "       FROM " + TableList.VHA_STATUS
                        + " a WHERE appl_no=? and pur_cd=? order by a.moved_on desc limit 1";
            } else if ((status.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD
                    || status.getPur_cd() == TableConstants.VM_PMT_APPLICATION_PUR_CD)
                    && pmtFlag) {
                sql = " INSERT INTO " + TableList.VHA_STATUS
                        + "       SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno+1,"
                        + "       action_cd, seat_cd, cntr_id, ? as status, ? as office_remark, public_remark,"
                        + "       file_movement_type, ? as emp_cd, op_dt, current_timestamp as moved_on,? "
                        + "       FROM " + TableList.VHA_STATUS
                        + " a WHERE appl_no=? order by a.moved_on desc limit 1";

            } else {
                sql = " INSERT INTO " + TableList.VHA_STATUS
                        + "       SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno+1,"
                        + "       action_cd, seat_cd, cntr_id, ? as status, ? as office_remark, public_remark,"
                        + "       file_movement_type, ? as emp_cd, op_dt, current_timestamp as moved_on,? "
                        + "       FROM " + TableList.VHA_STATUS
                        + " a WHERE appl_no=? and pur_cd=? order by a.moved_on desc limit 1";

            }
            ps = tmgr.prepareStatement(sql);
            i = 1;
            ps.setString(i++, TableConstants.RESTOREDISPOSE);
            ps.setString(i++, status.getOffice_remark());
            ps.setLong(i++, status.getEmp_cd());
            ps.setString(i++, Util.getClientIpAdress());
            ps.setString(i++, status.getAppl_no());
            if (!((status.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD
                    || status.getPur_cd() == TableConstants.VM_PMT_APPLICATION_PUR_CD)
                    && pmtFlag)) {
                ps.setInt(i++, status.getPur_cd());
            }
            ps.executeUpdate();



            //************************************************************************************end restore*******************************************
            if ((status.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD
                    || status.getPur_cd() == TableConstants.VM_PMT_APPLICATION_PUR_CD)
                    && pmtFlag) {
                sql = "INSERT INTO " + TableList.VA_DETAILS
                        + "     SELECT appl_no, pur_cd, appl_dt, regn_no, user_id, user_type, entry_ip, \n"
                        + "            entry_status, confirm_ip, confirm_status, confirm_date, state_cd, \n"
                        + "            off_cd FROM " + TableList.VHA_DETAILS
                        + " a WHERE a.appl_no=? order by a.moved_on desc limit 1";
            } else {
                sql = "INSERT INTO " + TableList.VA_DETAILS
                        + "     SELECT appl_no, pur_cd, appl_dt, regn_no, user_id, user_type, entry_ip, \n"
                        + "            entry_status, confirm_ip, confirm_status, confirm_date, state_cd, \n"
                        + "            off_cd FROM " + TableList.VHA_DETAILS
                        + " a WHERE a.appl_no=? and pur_cd=? order by a.moved_on desc limit 1";
            }
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, status.getAppl_no());
            if (!((status.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD
                    || status.getPur_cd() == TableConstants.VM_PMT_APPLICATION_PUR_CD)
                    && pmtFlag)) {
                ps.setInt(2, status.getPur_cd());
            }
            ps.executeUpdate();
            if ((status.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD
                    || status.getPur_cd() == TableConstants.VM_PMT_APPLICATION_PUR_CD)
                    && pmtFlag) {
                sql = "DELETE FROM " + TableList.VHA_DETAILS + " WHERE appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, status.getAppl_no());
                ps.executeUpdate();
            } else {
                sql = "DELETE FROM " + TableList.VHA_DETAILS + " WHERE appl_no=? and pur_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, status.getAppl_no());
                ps.setInt(2, status.getPur_cd());
                ps.executeUpdate();
            }

            //********for disposing online application 
            sql = "INSERT INTO " + TableList.VA_STATUS_APPL
                    + " SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno+2, \n"
                    + "            action_cd, seat_cd, cntr_id, status, office_remark, public_remark, \n"
                    + "            file_movement_type, emp_cd, op_dt, moved_from_online "
                    + "  FROM " + TableList.VHA_STATUS_APPL + " a where appl_no=? and pur_cd=? order by a.moved_on desc limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, status.getAppl_no());
            ps.setInt(2, status.getPur_cd());
            ps.executeUpdate();


            sql = "INSERT INTO " + TableList.VHA_STATUS_APPL
                    + " SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno+1,"
                    + "       action_cd, seat_cd, cntr_id, 'R', office_remark, public_remark,"
                    + "       file_movement_type, ? as emp_cd, op_dt, moved_from_online, current_timestamp as moved_on"
                    + "  FROM " + TableList.VHA_STATUS_APPL + " a where appl_no=? and pur_cd=? order by a.moved_on desc limit 1";

            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, status.getEmp_cd());
            ps.setString(2, status.getAppl_no());
            ps.setInt(3, status.getPur_cd());
            ps.executeUpdate();
            sql = "INSERT INTO " + TableList.VA_DETAILS_APPL
                    + " SELECT appl_no, pur_cd, appl_dt, regn_no, user_id, user_type, entry_ip, \n"
                    + "            entry_status, confirm_ip, confirm_status, confirm_date, state_cd, \n"
                    + "            off_cd "
                    + "  FROM " + TableList.VHA_DETAILS_APPL
                    + " a WHERE appl_no=? and pur_cd=? order by a.moved_on desc limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, status.getAppl_no());
            ps.setInt(2, status.getPur_cd());
            ps.executeUpdate();

            sql = "Delete FROM " + TableList.VHA_DETAILS_APPL + " WHERE appl_no=? and pur_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, status.getAppl_no());
            ps.setInt(2, status.getPur_cd());
            ps.executeUpdate();

            //**************************************

            tmgr.commit();//Commiting data here....
        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }
    }

    public void insertIntoVaTable(TransactionManager tmgr, String applNo, String empCode, String tableNameVha, String tableNameVa) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        if (applNo != null && applNo.trim().length() > 0) {
            applNo = applNo.toUpperCase().trim();
            String ColName = "";
            sql = "SELECT * FROM " + tableNameVa + " limit 1";
            ps = tmgr.prepareStatement(sql);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    ColName += rsmd.getColumnName(i) + ",";
                }
            }
            if (!CommonUtils.isNullOrBlank(ColName)) ///////////////
            {
                ColName = ColName.substring(0, ColName.lastIndexOf(","));


            }
            sql = " INSERT INTO " + tableNameVa
                    + "  SELECT " + ColName + " FROM " + tableNameVha
                    + "  a WHERE a.appl_no=? order by a.moved_on desc limit 1 ";

        }
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, applNo);
        ps.executeUpdate();

    }

    public static void restoreIntoDupHistory(TransactionManager tmgr, String appl_no, int purCode) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        //inserting data into vha_ca from va_ca
        sql = "INSERT INTO " + TableList.VA_DUP
                + " SELECT "
                + "  state_cd,off_cd, appl_no, "
                + "  pur_cd,  regn_no,"
                + "  reason,  fir_no,"
                + "  fir_dt, police_station,op_dt"
                + "  FROM  " + TableList.VHA_DUP
                + " WHERE  appl_no=? and pur_cd=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, appl_no);
        ps.setInt(2, purCode);
        ps.executeUpdate();
    }
    //insertVhaRcReleaseSurrenderCancellantionDispose

    public static void reStoreVaRcReleaseSurrenderCancellantionDispose(Status_dobj statusDobj, TransactionManager tmgr) throws SQLException, VahanException {
        PreparedStatement ps = null;
        String sqlQry = "";
        try {
            if ((TableConstants.VM_MAST_RC_SURRENDER == statusDobj.getPur_cd())) {
                String sqlSelect = "select * from " + TableList.VHA_RC_SURRENDER + " where appl_no=? order by moved_on desc limit 1";
                ps = tmgr.prepareStatement(sqlSelect);
                ps.setString(1, statusDobj.getAppl_no());
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    sqlQry = "INSERT INTO " + TableList.VA_RC_SURRENDER + "(SELECT state_cd, off_cd, appl_no, regn_no, surr_dt, file_ref_no, approved_by, \n"
                            + "            reason, rc, rc_sno, permit, permit_sno, fc, fc_sno, taxexem, \n"
                            + "            op_dt \n"
                            + "  FROM " + TableList.VHA_RC_SURRENDER + " where appl_no=? order by moved_on desc limit 1)";
                } else {
                    sqlQry = "INSERT INTO " + TableList.VA_RC_SUSPEND + "(SELECT state_cd, off_cd, appl_no, regn_no, susp_dt, file_ref_no, approved_by, \n"
                            + "            reason, op_dt, suspended_upto \n"
                            + "  FROM " + TableList.VHA_RC_SUSPEND + " where appl_no=? order by moved_on desc limit 1)";
                }

            } else if ((TableConstants.VM_MAST_RC_RELEASE == statusDobj.getPur_cd())) {
                sqlQry = "INSERT INTO " + TableList.VA_RC_RELEASE + ""
                        + "(SELECT state_cd, off_cd, appl_no, regn_no, release_dt, rel_file_ref_no, \n"
                        + "            rel_approved_by, rel_op_dt \n"
                        + "FROM " + TableList.VHA_RC_RELEASE + " where appl_no=? order by moved_on desc limit 1)";
            } else if ((TableConstants.VM_MAST_RC_CANCELLATION == statusDobj.getPur_cd())) {
                sqlQry = "INSERT INTO " + TableList.VA_RC_CANCEL + "(SELECT state_cd, off_cd, appl_no, regn_no, cancel_dt, file_ref_no, approved_by, \n"
                        + "            reason, op_dt, requested_by \n"
                        + "  FROM " + TableList.VHA_RC_CANCEL + " where appl_no=? order by moved_on desc limit 1\n"
                        + "   )";
            }
            ps = tmgr.prepareStatement(sqlQry);
            ps.setString(1, statusDobj.getAppl_no());
            ps.execute();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }
    }

    public void restorePermitPrintTOvaPermitPrint(String appl_no, TransactionManager tmgr, String docId) throws VahanException {//done
        String Query;
        PreparedStatement ps;
        try {
            Query = "INSERT INTO " + TableList.VA_PERMIT_PRINT + "(\n"
                    + "            appl_no, regn_no, doc_id, op_dt)\n"
                    + "     SELECT appl_no, regn_no, doc_id, op_dt FROM " + TableList.VH_PERMIT_PRINT + " where appl_no = ? AND doc_id=?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
            ps.setString(2, docId);
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }
    }

    public static void restoreVhPermitTranactionToVtPermitTranaction(TransactionManager tmgr, String regn_no, String applNo) throws SQLException {//done
        PreparedStatement ps = null;

        String Query = "INSERT INTO " + TableList.VH_PERMIT_TRANSACTION + "(\n"
                + "            state_cd, off_cd, appl_no, regn_no, pmt_no, pur_cd, rcpt_no, \n"
                + "            trans_pur_cd, remarks, order_no, order_dt, order_by, new_regn_no, \n"
                + "            user_cd, op_dt, moved_on, moved_by,excempted_flag)\n"
                + " SELECT     state_cd, off_cd, appl_no, regn_no, pmt_no, pur_cd, rcpt_no, \n"
                + "            trans_pur_cd, remarks, order_no, order_dt, order_by, new_regn_no, \n"
                + "            user_cd, op_dt,CURRENT_TIMESTAMP,?,excempted_flag \n"
                + "  FROM " + TableList.VT_PERMIT_TRANSACTION + " WHERE regn_no = ? and appl_no = ?";
        ps = tmgr.prepareStatement(Query);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, regn_no);
        ps.setString(3, applNo);
        ps.executeUpdate();
    }

    public void restoreEndorsmentApplication(TransactionManager tmgr, String regn_no) throws SQLException {//Done
        String Query = "";
        Query = "INSERT INTO " + TableList.VH_PERMIT + "(\n"
                + "            state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                + "            valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, \n"
                + "            service_type, goods_to_carry, jorney_purpose, parking, replace_date, \n"
                + "            remarks, op_dt, pmt_status, reason, moved_on, moved_by)\n"
                + "  SELECT state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from,\n"
                + "       valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered,\n"
                + "       service_type, goods_to_carry, jorney_purpose, parking, replace_date,\n"
                + "       remarks, op_dt,'END','PERMIT ENDORSEMENT',CURRENT_TIMESTAMP,? \n"
                + "  FROM " + TableList.VT_PERMIT + " where regn_no  = ?  order by op_dt DESC limit 1";
        PreparedStatement ps = tmgr.prepareStatement(Query);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, regn_no);
        ps.executeUpdate();
    }

    public void restoreInNonUseDetails(TransactionManager tmgr, NonUseDobj dobj, Status_dobj statusDobj) throws VahanException {//done
        String sql = "";
        PreparedStatement pstm = null;
        int i = 1;
        try {

            if (statusDobj.getPur_cd() == TableConstants.VM_MAST_NON_USE) {
                sql = "INSERT INTO " + TableList.VA_NON_USE_TAX_EXEM + "(\n"
                        + "state_cd, off_cd, appl_no, regn_no, exem_fr, \n"
                        + "            exem_to, exem_by, perm_no, perm_dt, remark, deal_cd, cl_perm_dt, \n"
                        + "            clear_by, clear_dt, c_deal_cd, place, cr_no, insp_off, insp_date, \n"
                        + "            insp_flag, doc_flag, nonuse_adjust_amt, penalty, op_dt, garage_add1, \n"
                        + "            garage_add2, garage_add3, garage_district, garage_state, garage_pincode )\n"
                        + "  SELECT state_cd, off_cd, appl_no, regn_no, exem_fr, \n"
                        + "            exem_to, exem_by, perm_no, perm_dt, remark, deal_cd, cl_perm_dt, \n"
                        + "            clear_by, clear_dt, c_deal_cd, place, cr_no, insp_off, insp_date, \n"
                        + "            insp_flag, doc_flag, nonuse_adjust_amt, penalty, op_dt, garage_add1, \n"
                        + "            garage_add2, garage_add3, garage_district, garage_state, garage_pincode  \n"
                        + "  FROM " + TableList.VHA_NON_USE_TAX_EXEM + ""
                        + " WHERE and appl_no=?";

                pstm = tmgr.prepareStatement(sql);
                pstm.setString(i++, statusDobj.getAppl_no());
                pstm.executeUpdate();
            } else if (statusDobj.getPur_cd() == TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE) {
                sql = "INSERT INTO " + TableList.VA_NON_USE_RESTORE_REMOVE + "(\n"
                        + "state_cd, off_cd, appl_no, regn_no, exem_fr, exem_to, exem_by, \n"
                        + "            perm_no, perm_dt, remark, deal_cd, cl_perm_dt, clear_by, clear_dt, \n"
                        + "            c_deal_cd, place, cr_no, insp_off, insp_date, insp_flag, doc_flag, \n"
                        + "            nonuse_adjust_amt, towed_veh_no, penalty, op_dt, cunt_reson, \n"
                        + "            garage_add1, garage_add2, garage_add3, garage_district, garage_state, \n"
                        + "            garage_pincode, rr_flag )\n"
                        + "  SELECT state_cd, off_cd, appl_no, regn_no, exem_fr, exem_to, exem_by, \n"
                        + "            perm_no, perm_dt, remark, deal_cd, cl_perm_dt, clear_by, clear_dt, \n"
                        + "            c_deal_cd, place, cr_no, insp_off, insp_date, insp_flag, doc_flag, \n"
                        + "            nonuse_adjust_amt, towed_veh_no, penalty, op_dt, cunt_reson, \n"
                        + "            garage_add1, garage_add2, garage_add3, garage_district, garage_state, \n"
                        + "            garage_pincode, rr_flag\n"
                        + "  FROM " + TableList.VHA_NON_USE_RESTORE_REMOVE + ""
                        + " WHERE state_cd=?  and regn_no=? and appl_no=?";

                pstm = tmgr.prepareStatement(sql);
                //  pstm.setString(i++, Util.getEmpCode());
                pstm.setString(i++, Util.getUserStateCode());
                pstm.setString(i++, statusDobj.getRegn_no());
                pstm.setString(i++, statusDobj.getAppl_no());
                pstm.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }

    }

    public boolean checkApproved(String applno, String stateCd, int offCd, String regnNo) throws VahanException {
        boolean isPending = false;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("checkApproved");
            String sql = "(select 1 as msg from va_details where state_cd=? and off_cd=? and regn_no=? and appl_dt > (select appl_dt from vha_details where appl_no =? order by appl_dt desc limit 1))\n"
                    + "union (select 2 as msg from va_details where state_cd=? and off_cd=? and regn_no=? and entry_status=? and appl_no in (select appl_no from vha_details where appl_no =? and state_cd=? and off_cd=?))";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            ps.setString(3, regnNo);
            ps.setString(4, applno);
            ps.setString(5, stateCd);
            ps.setInt(6, offCd);
            ps.setString(7, regnNo);
            ps.setString(8, TableConstants.STATUS_APPROVED);
            ps.setString(9, applno);
            ps.setString(10, stateCd);
            ps.setInt(11, offCd);

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                isPending = true;
                if (rs.getString("msg").equals("1")) {
                    if ("KL".equals(Util.getUserStateCode())) {//35-Temporary Permit,119-MV Tax Endorsement,345-Tax Clearance,125-Tax Exemption.
                        sql = "select appl_no from va_details where state_cd=? and off_cd=? and regn_no=? and pur_cd in(35,119,125,345) and entry_status = ? "
                                + " and  appl_dt > (select appl_dt from vha_details where appl_no =? order by appl_dt desc limit 1)";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, stateCd);
                        ps.setInt(2, offCd);
                        ps.setString(3, regnNo);
                        ps.setString(4, TableConstants.STATUS_APPROVED);
                        ps.setString(5, applno);
                        RowSet rs1 = tmgr.fetchDetachedRowSet();
                        if (!rs1.next()) {
                            throw new VahanException(Util.getLocaleMsg("forRegn") + "[" + regnNo + "] " + Util.getLocaleMsg("another_inward"));
                        }
                    } else {
                        throw new VahanException(Util.getLocaleMsg("forRegn") + "[" + regnNo + "] " + Util.getLocaleMsg("another_inward"));
                    }
                } else {
                    throw new VahanException(Util.getLocaleMsg("forApplno") + "[" + applno + "]" + Util.getLocaleMsg("another_trans"));
                }
            }
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

        return isPending;
    }

    public boolean checkChassisNoInVaVtOwner(String chasi_no) throws VahanException {
        boolean isPending = false;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("checkChassisNoInVaVtOwner");
            String sql = "(select regn_no as regn_no,1 as msg from vt_owner where chasi_no=? and status <> 'N')\n"
                    + "union (select appl_no as regn_no,2 as msg from va_owner where chasi_no=?)";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, chasi_no);
            ps.setString(2, chasi_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isPending = true;
                if (rs.getString("msg").equals("1")) {
                    throw new VahanException(Util.getLocaleMsg("vehNo") + " [" + rs.getString("regn_no") + "]" + Util.getLocaleMsg("against_chassis") + " [" + chasi_no + "]." + Util.getLocaleMsg("notRestore"));
                } else {
                    throw new VahanException(Util.getLocaleMsg("chasi_no") + "[" + chasi_no + "] " + Util.getLocaleMsg("usedAlrdy") + rs.getString("regn_no") + "]." + Util.getLocaleMsg("notRestore"));
                }
            }
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

        return isPending;
    }

    public void restoreIntoVtDScDocDetailsTable(TransactionManager tmgr, String applNo, String stateCd, String tableNameVh, String tableNameVt) throws Exception {
        String sql = " INSERT INTO " + tableNameVt
                + "  select state_cd, off_cd, user_cd, appl_no, doc_catg, doc_subcatg,vendor_cd, serial_no, \n"
                + "       op_dt from " + tableNameVh + " where appl_no=? and state_cd = ? and (appl_no,moved_on) in (select appl_no,max(moved_on) from " + tableNameVh + " where appl_no = ? and state_cd  =  ? group by 1 ); ";


        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, applNo);
        ps.setString(2, stateCd);
        ps.setString(3, applNo);
        ps.setString(4, stateCd);
        ps.executeUpdate();

    }
}
