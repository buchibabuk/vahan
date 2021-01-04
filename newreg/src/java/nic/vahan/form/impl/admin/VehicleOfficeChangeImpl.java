/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.admin;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.bean.admin.VehicleOfficeChangeDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Trailer_dobj;
import nic.vahan.form.impl.NewImpl;
import nic.vahan.form.impl.Trailer_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;

/**
 *
 * @author nicsi
 */
public class VehicleOfficeChangeImpl {

    private static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(VehicleOfficeChangeImpl.class);

    public List<OwnerDetailsDobj> getOwnerDetailsListByChassiNo(String regnNo, String stateCd, String chassiNo) throws VahanException {
        OwnerDetailsDobj dobj = null;
        List<Trailer_dobj> trailer_dobjList = null;

        if (regnNo == null || regnNo.trim().isEmpty()) {
            throw new VahanException("Blank Registration No not allowed.");
        }
        List<OwnerDetailsDobj> ownerList = new ArrayList<>();
        String sql = "";
        sql = " SELECT owner.*,type.descr as regn_type_descr,vtowner.push_back_seat,vtowner.ordinary_seat,owcode.descr as owner_cd_descr "
                + " FROM " + TableList.VIEW_VV_OWNER + " owner "
                + " left join vm_regn_type type on owner.regn_type=type.regn_typecode "
                + " left join vm_owcode owcode on owner.owner_cd = owcode.ow_code "
                + " left join " + TableList.VT_OWNER_OTHER + "  vtowner on owner.state_cd = vtowner.state_cd and  owner.off_cd = vtowner.off_cd"
                + "  and owner.regn_no = vtowner.regn_no"
                + " WHERE owner.regn_no = ?  and owner.state_cd = ? and right(owner.chasi_no,5)=? ";

        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;

        try {
            tmgr = new TransactionManagerReadOnly("getOwnerDetails");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            ps.setString(3, chassiNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            while (rs.next()) {
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
                dobj.setStatus(rs.getString("status"));
                if (dobj.getStatus().equalsIgnoreCase("Y") || dobj.getStatus().equalsIgnoreCase("A")) {
                    dobj.setStatusDescr("Active");
                } else if (dobj.getStatus().equalsIgnoreCase("N")) {
                    dobj.setStatusDescr("Noc Issued");
                } else if (dobj.getStatus().equalsIgnoreCase("T")) {
                    dobj.setStatusDescr("Scrap Vehicle");
                } else if (dobj.getStatus().equalsIgnoreCase("S")) {
                    dobj.setStatusDescr("RC Surrender");
                } else if (dobj.getStatus().equalsIgnoreCase("C")) {
                    dobj.setStatusDescr("RC Cancel");
                } else {
                    dobj.setStatusDescr(dobj.getStatus());
                }
                dobj.setVch_purchase_as_code(rs.getString("vch_purchase_as"));
                dobj.setPush_bk_seat(rs.getInt("push_back_seat"));
                dobj.setOrdinary_seat(rs.getInt("ordinary_seat"));


                if (ServerUtil.isTransport(dobj.getVh_class(), null)) {
                    dobj.setVehTypeDescr(TableConstants.VM_VEHTYPE_TRANSPORT_DESCR);
                    dobj.setVehType(TableConstants.VM_VEHTYPE_TRANSPORT);
                } else {
                    dobj.setVehTypeDescr(TableConstants.VM_VEHTYPE_NON_TRANSPORT_DESCR);
                    dobj.setVehType(TableConstants.VM_VEHTYPE_NON_TRANSPORT);
                }

                if (dobj.getVch_purchase_as_code().equalsIgnoreCase("B")) {
                    dobj.setVch_purchase_as("Fully Built");
                } else {
                    dobj.setVch_purchase_as("Drive Away Chasis");
                }

                String vh_classCase = "," + String.valueOf(dobj.getVh_class()) + ",";

                if (stateCd != null && stateCd.equalsIgnoreCase("OR") && TableConstants.GCW_VEH_CLASS.contains(vh_classCase)) {

                    String sqlGcw = "select ld_wt from " + TableList.VT_OWNER + " \n"
                            + "where regn_no in (SELECT regn_no FROM " + TableList.VT_SIDE_TRAILER + " \n"
                            + "where link_regn_no=? and state_cd = ? and off_cd = ?) and state_cd = ? and off_cd = ?";
                    ps = tmgr.prepareStatement(sqlGcw);

                    ps.setString(1, regnNo);
                    ps.setString(2, stateCd);
                    ps.setInt(3, dobj.getOff_cd());
                    ps.setString(4, stateCd);
                    ps.setInt(5, dobj.getOff_cd());
                    RowSet rset = tmgr.fetchDetachedRowSet_No_release();
                    int ldwt = 0;
                    while (rset.next()) {
                        ldwt += rset.getInt("ld_wt");
                        dobj.setRenderedGCW(true);
                    }
                    dobj.setGcw(ldwt + dobj.getUnld_wt());

                }


                //Setting Owenr Identification Details  
                String sqlOwnerId = "SELECT * FROM " + TableList.VT_OWNER_IDENTIFICATION
                        + " WHERE regn_no =? and state_cd = ? and off_cd = ? ";


                ps = tmgr.prepareStatement(sqlOwnerId);
                ps.setString(1, regnNo);
                ps.setString(2, dobj.getState_cd());
                ps.setInt(3, dobj.getOff_cd());

                RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
                OwnerIdentificationDobj own_identity = fillOwnerIdentityDobj(rs1);
                if (own_identity != null) {
                    dobj.setOwnerIdentity(own_identity);
                }
                String lastRcptdt = "select to_char(rcpt_dt,'DD-Mon-YYYY HH:MM:SS') as rcpt_dt from ((select rcpt_dt from vt_fee f where f.state_cd=? and f.off_cd=? and f.regn_no=? order by f.rcpt_dt desc limit 1)"
                        + " union all (select rcpt_dt from vt_tax where state_cd=? and off_cd=? and regn_no=? order by rcpt_dt desc limit 1)) as a order by rcpt_dt desc limit 1";
                ps = tmgr.prepareStatement(lastRcptdt);
                ps.setString(1, dobj.getState_cd());
                ps.setInt(2, dobj.getOff_cd());
                ps.setString(3, regnNo);
                ps.setString(4, dobj.getState_cd());
                ps.setInt(5, dobj.getOff_cd());
                ps.setString(6, regnNo);
                RowSet rs2 = tmgr.fetchDetachedRowSet_No_release();
                if (rs2.next()) {
                    if (rs2.getString("rcpt_dt") != null && !rs2.getString("rcpt_dt").isEmpty()) {
                        dobj.setLastRcptDt(rs2.getString("rcpt_dt"));
                    } else {
                        dobj.setLastRcptDt("No Fee/Tax Taken");
                    }
                } else {
                    dobj.setLastRcptDt("No Fee/Tax Taken");
                }

                //Setting Number of Tyres
                String sqlNoOftyres = "SELECT sum(COALESCE(f_axle_tyre,0) + COALESCE(r_axle_tyre,0) + COALESCE(o_axle_tyre,0) + COALESCE(t_axle_tyre,0)) as numberoftyre FROM " + TableList.VT_AXLE
                        + " WHERE regn_no =? and state_cd = ? and off_cd = ? ";
                ps = tmgr.prepareStatement(sqlNoOftyres);
                ps.setString(1, dobj.getRegn_no());
                ps.setString(2, dobj.getState_cd());
                ps.setInt(3, dobj.getOff_cd());
                RowSet rs3 = tmgr.fetchDetachedRowSet_No_release();
                if (rs3.next()) {
                    dobj.setNumberOfTyres(rs3.getInt("numberoftyre"));
                }
                trailer_dobjList = Trailer_Impl.set_trailer_dobjList(tmgr, null, regnNo, 0);
                if (trailer_dobjList != null && trailer_dobjList.size() > 0) {
                    dobj.setListTrailerDobj(trailer_dobjList);
                }
                ownerList.add(dobj);
            }

        } catch (VahanException vx) {
            throw vx;
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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

        return ownerList;
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

    public void updateAndSaveChangeOffice(Owner_dobj dobj, String stateCd, String empCode, VehicleOfficeChangeDobj vehicleDObj) throws VahanException {
        TransactionManager tmgr = null;
        String messageError = "";
        try {
            boolean flag = false;
            tmgr = new TransactionManager("updateAndSaveChangeOffice");
            if (dobj == null || vehicleDObj == null) {
                return;
            }
            int newOffCd = vehicleDObj.getNewOffCd();
            String chassiNo = vehicleDObj.getChassiNo();
            String regnNo = vehicleDObj.getRegnNo();
            String rmk = vehicleDObj.getRemark();
            String requestedBy = vehicleDObj.getRequestedBy();
            Date date = vehicleDObj.getReguestedDate();

            String sql = "SELECT * FROM " + TableList.VT_OWNER + " WHERE regn_no = ? and state_cd = ? and off_cd = ? ";

            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            ps.setInt(3, newOffCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                throw new VahanException(Util.getLocaleMsg("home_rightpanelregno") + ":" + regnNo + Util.getLocaleMsg("already_exist") + ServerUtil.getOfficeName(newOffCd, stateCd) + ".");
            }


            sql = "INSERT INTO vahan4.vt_change_office(\n"
                    + "            state_cd, old_off_cd, new_off_cd, regn_no, remark, requested_by, \n"
                    + "            requested_on, change_by, change_dt)\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?, \n"
                    + "            ?, ?, current_timestamp);";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, dobj.getOff_cd());
            ps.setInt(3, newOffCd);
            ps.setString(4, regnNo);
            ps.setString(5, rmk);
            ps.setString(6, requestedBy);
            ps.setDate(7, new java.sql.Date(date.getTime()));
            ps.setString(8, empCode);
            ps.executeUpdate();
            OwnerAdminImpl ownerAdmin = new OwnerAdminImpl();
            dobj.setAppl_no(regnNo);
            //Remove duplcate record other than owner table.
            new NewImpl().removeDuplicateInconsistantRecords(tmgr, regnNo, dobj.getAppl_no(), empCode);
            //1- Update office code in VT_OWNER Table
            ownerAdmin.insertIntoVh_owner(dobj, tmgr);
            messageError = Util.getLocaleMsg("error_owerdtls");
            updateIntoVt_Table(dobj, TableList.VT_OWNER, tmgr, stateCd, newOffCd, regnNo, messageError);
            //2- Update office code in VT_OWNER_IDENTIFICATION Table
            ownerAdmin.insertIntoVh_ownerIdentification(dobj, tmgr);
            //updateIntoVt_Table2(dobj, TableList.VT_OWNER_IDENTIFICATION, tmgr, stateCd, newOffCd, regnNo);
            messageError = Util.getLocaleMsg("error_ownerIdendtls");
            sql = "Update " + TableList.VT_OWNER_IDENTIFICATION + " set off_cd = ?,verified_on=? WHERE regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, newOffCd);
            ps.setDate(2, new java.sql.Date(new java.util.Date().getTime()));
            ps.setString(3, regnNo);
            ps.setString(4, stateCd);
            ps.setInt(5, dobj.getOff_cd());
            ps.executeUpdate();
            //3- Update office code in VT_INSURANCE Table
            ownerAdmin.insertIntoVhInsurance(dobj, tmgr);
            messageError = Util.getLocaleMsg("error_insuredtls");
            updateIntoVt_Table(dobj, TableList.VT_INSURANCE, tmgr, stateCd, newOffCd, regnNo, messageError);
            //4- Update office code in VT_AXLE Table
            ownerAdmin.insertUpdateVhAxel(dobj, tmgr);
            messageError = Util.getLocaleMsg("error_axeldtls");
            updateIntoVt_Table(dobj, TableList.VT_AXLE, tmgr, stateCd, newOffCd, regnNo, messageError);
            //5- Update office code in VT_TMP_REGN_DTL Table
            ownerAdmin.insertIntoVhTempRegnDtls(dobj, tmgr);
            messageError = Util.getLocaleMsg("error_tmpregndtls");
            updateIntoVt_Table(dobj, TableList.VT_TMP_REGN_DTL, tmgr, stateCd, newOffCd, regnNo, messageError);
            //6- Update office code in VT_OTHER_STATE_VEH Table
            ownerAdmin.insertVhOtherState(dobj, tmgr); //VH_OTHER_STATE_VEH
            messageError = Util.getLocaleMsg("error_othervehdtls");
            //updateIntoVt_Table(dobj, TableList.VT_OTHER_STATE_VEH, tmgr, stateCd, newOffCd, regnNo);
            sql = "Update " + TableList.VT_OTHER_STATE_VEH + " set off_cd = ?,op_dt = ? WHERE new_regn_no = ? and state_cd = ? and off_cd = ? ";

            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, newOffCd);
            ps.setDate(2, new java.sql.Date(new java.util.Date().getTime()));
            ps.setString(3, regnNo);
            ps.setString(4, stateCd);
            ps.setInt(5, dobj.getOff_cd());
            ps.executeUpdate();
            //7- Update office code in VT_OWNER_EX_ARMY Table
            ownerAdmin.insertIntoVhOwnerExArmy(dobj, tmgr);
            messageError = Util.getLocaleMsg("error_exarmydtls");
            updateIntoVt_Table(dobj, TableList.VT_OWNER_EX_ARMY, tmgr, stateCd, newOffCd, regnNo, messageError);
            //8- Update office code in VT_IMPORT_VEH Table
            ownerAdmin.insertVhImportVeh(dobj, tmgr);
            messageError = Util.getLocaleMsg("error_importvehdtls");
            updateIntoVt_Table(dobj, TableList.VT_IMPORT_VEH, tmgr, stateCd, newOffCd, regnNo, messageError);
            //9- Update office code in VT_RETROFITTING_DTLS Table
            ownerAdmin.insertVhRetrofitinDtls(dobj, tmgr);
            messageError = Util.getLocaleMsg("error_retrodtls");
            updateIntoVt_Table(dobj, TableList.VT_RETROFITTING_DTLS, tmgr, stateCd, newOffCd, regnNo, messageError);

            //10- Update office code in VT_SPEED_GOVERNOR Table
            messageError = Util.getLocaleMsg("error_speedgovdtls");
            sql = " INSERT INTO " + TableList.VH_SPEED_GOVERNOR
                    + " SELECT current_timestamp,?,"
                    + " state_cd, off_cd, regn_no,?, sg_no, sg_fitted_on, sg_fitted_at,"
                    + " op_dt, emp_cd,sg_type,sg_type_approval_no,sg_test_report_no,sg_fitment_cert_no"
                    + " FROM " + TableList.VT_SPEED_GOVERNOR
                    + " WHERE state_cd=? and regn_no=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, dobj.getAppl_no());
            ps.setString(3, stateCd);
            ps.setString(4, dobj.getRegn_no());
            ps.setInt(5, dobj.getOff_cd());
            ps.executeUpdate();
            updateIntoVt_Table(dobj, TableList.VT_SPEED_GOVERNOR, tmgr, stateCd, newOffCd, regnNo, messageError);

            //11- Update office code in VT_REFLECTIVE_TAPE Table
            messageError = Util.getLocaleMsg("error_reftapdtls");
            sql = " Insert into " + TableList.VH_REFLECTIVE_TAPE + " SELECT current_timestamp,?,state_cd, off_cd, regn_no, certificate_no, fitment_date, manu_name,current_timestamp as op_dt\n"
                    + "  FROM " + TableList.VT_REFLECTIVE_TAPE + " where state_cd=? and regn_no=? and off_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, stateCd);
            ps.setString(3, regnNo);
            ps.setInt(4, dobj.getOff_cd());
            ps.executeUpdate();
            updateIntoVt_Table(dobj, TableList.VT_REFLECTIVE_TAPE, tmgr, stateCd, newOffCd, regnNo, messageError);

            //12- Update office code in VT_HYPTH Table
            messageError = Util.getLocaleMsg("error_hyptdtls");
            sql = "Insert into  " + TableList.VH_HYPTH + " SELECT "
                    + " state_cd, off_cd, regn_no, sr_no, hp_type, fncr_name, fncr_add1,  fncr_add2,"
                    + " fncr_add3, fncr_district, fncr_pincode, fncr_state, "
                    + " from_dt, op_dt, ?, "
                    + " current_timestamp, ? "
                    + " FROM " + TableList.VT_HYPTH
                    + " where regn_no = ? and state_cd = ? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getAppl_no());
            ps.setString(2, empCode);
            ps.setString(3, dobj.getRegn_no());
            ps.setString(4, dobj.getState_cd());
            ps.setInt(5, dobj.getOff_cd());
            ps.executeUpdate();
            updateIntoVt_Table(dobj, TableList.VT_HYPTH, tmgr, stateCd, newOffCd, regnNo, messageError);


            //13- Update office code in VT_TRAILER Table
            messageError = Util.getLocaleMsg("error_trailerdtls");
            sql = "Insert into " + TableList.VH_TRAILER + " Select state_cd, off_cd, regn_no, chasi_no, body_type, ld_wt,"
                    + " unld_wt, f_axle_descp,"
                    + " r_axle_descp, o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight,"
                    + " o_axle_weight, t_axle_weight, NULL, current_timestamp, ? from " + TableList.VT_TRAILER
                    + " where regn_no=? and state_cd= ? and off_cd= ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, dobj.getRegn_no());
            ps.setString(3, stateCd);
            ps.setInt(4, dobj.getOff_cd());
            ps.executeUpdate();
            updateIntoVt_Table(dobj, TableList.VT_TRAILER, tmgr, stateCd, newOffCd, regnNo, messageError);

            //14- Update office code in VT_TRAILER Table PUCC Details
            messageError = Util.getLocaleMsg("error_puccdtls");
            sql = "INSERT INTO " + TableList.VH_PUCC
                    + " SELECT state_cd, off_cd, regn_no, pucc_from, pucc_upto, pucc_centreno,"
                    + "       pucc_no, op_dt, ? as appl_no, current_timestamp as moved_on, ? as moved_by "
                    + "  FROM " + TableList.VT_PUCC + " WHERE regn_no=? and state_cd = ? and off_cd= ?";

            ps = tmgr.prepareStatement(sql);

            ps.setString(1, dobj.getAppl_no());
            ps.setString(2, empCode);
            ps.setString(3, dobj.getRegn_no());
            ps.setString(4, stateCd);
            ps.setInt(5, dobj.getOff_cd());
            ps.executeUpdate();
            updateIntoVt_Table(dobj, TableList.VT_PUCC, tmgr, stateCd, newOffCd, regnNo, messageError);

            //14- Update office code in VT_FITNESS Table
            messageError = Util.getLocaleMsg("error_fitnessdtls");
            sql = " Insert into  " + TableList.VH_FITNESS
                    + " SELECT state_cd, off_cd, ?, regn_no, chasi_no, fit_chk_dt, fit_chk_tm, "
                    + "       fit_result, fit_valid_to, fit_nid, fit_off_cd1, fit_off_cd2, "
                    + "       remark, fare_mtr_no, speedgov_no, speedgov_compname, brake, steer, "
                    + "       susp, engin, tyre, horn, lamp, embo, speed, paint, wiper, dimen, "
                    + "       body, fare, elec, finis, road, poll, transm, glas, emis, rear, "
                    + "       others, op_dt,current_timestamp,? "
                    + "  FROM " + TableList.VT_FITNESS + " where regn_no=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getAppl_no());
            ps.setString(2, empCode);
            ps.setString(3, regnNo);
            ps.setString(4, stateCd);
            ps.executeUpdate();
            updateIntoVt_Table(dobj, TableList.VT_FITNESS, tmgr, stateCd, newOffCd, regnNo, messageError);

            //Ohter Schema VT_HSRP
            messageError = Util.getLocaleMsg("error_hsrpdtls");
            sql = "INSERT INTO " + TableList.VH_HSRP + "("
                    + "            state_cd, off_cd, appl_no, regn_no, hsrp_flag, user_cd, op_dt, "
                    + "            hsrp_no_front, hsrp_no_back, hsrp_fix_dt, hsrp_fix_amt, hsrp_amt_taken_on, "
                    + "            hsrp_op_dt, moved_reason, moved_on, moved_by)"
                    + "            (SELECT state_cd, off_cd, appl_no, regn_no, hsrp_flag, user_cd, op_dt,"
                    + "            hsrp_no_front, hsrp_no_back, hsrp_fix_dt, hsrp_fix_amt, hsrp_amt_taken_on, "
                    + "            hsrp_op_dt , ?, current_timestamp, ? "
                    + "            FROM " + TableList.VT_HSRP + " where regn_no = ? and state_cd = ? and off_cd = ? )";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, rmk);
            ps.setString(2, empCode); //hsrp_op_dt
            ps.setString(3, regnNo);
            ps.setString(4, stateCd);
            ps.setInt(5, dobj.getOff_cd());
            ps.executeUpdate();
            sql = "Update " + TableList.VT_HSRP + " set off_cd = ?,hsrp_op_dt = ? WHERE regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, newOffCd);
            ps.setDate(2, new java.sql.Date(new java.util.Date().getTime()));
            ps.setString(3, regnNo);
            ps.setString(4, stateCd);
            ps.setInt(5, dobj.getOff_cd());
            ps.executeUpdate();
            messageError = Util.getLocaleMsg("error_inspectdtls");
            sql = "insert into vh_inspection "
                    + " SELECT current_timestamp,?, state_cd, off_cd, ?, regn_no, insp_dt, remark, op_dt, fit_off_cd1 "
                    + "  FROM vt_inspection where  regn_no = ? and  state_cd=? and  off_cd=?";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, regnNo);
            ps.setString(3, regnNo);
            ps.setString(4, stateCd);
            ps.setInt(5, dobj.getOff_cd());
            ps.executeUpdate();
            updateIntoVt_Table(dobj, TableList.VT_INSPECTION, tmgr, stateCd, newOffCd, regnNo, messageError);
            messageError = Util.getLocaleMsg("error_vltddtls");
            updateIntoVt_Table(dobj, TableList.VT_VLTD, tmgr, stateCd, newOffCd, regnNo, messageError);
            tmgr.commit();

        } catch (VahanException vx) {
            throw vx;
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(messageError);
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
    }

    private void updateIntoVt_Table(Owner_dobj dobj, String tableName, TransactionManager tmgr, String state_cd, int newOffCd, String regnNo, String messageError) throws VahanException {
        String sql = "Update " + tableName + " set off_cd = ?,op_dt = ? WHERE regn_no = ? and state_cd = ? and off_cd = ? ";
        try {
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setInt(1, newOffCd);
            ps.setDate(2, new java.sql.Date(new java.util.Date().getTime()));
            ps.setString(3, regnNo);
            ps.setString(4, state_cd);
            ps.setInt(5, dobj.getOff_cd());
            ps.executeUpdate();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(messageError);
        }

    }

    private void updateIntoVt_Table2(Owner_dobj dobj, String tableName, TransactionManager tmgr, String state_cd, int newOffCd, String regnNo) throws VahanException {
        String sql = "Update " + tableName + " set off_cd = ? WHERE regn_no = ? and state_cd = ? and off_cd = ? ";
        try {
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setInt(1, newOffCd);
            // ps.setDate(2, new java.sql.Date(new java.util.Date().getTime()));
            ps.setString(2, regnNo);
            ps.setString(3, state_cd);
            ps.setInt(4, dobj.getOff_cd());
            ps.executeUpdate();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }

    }

    public String getApplNoFromVaFcPrint(String regn_no, String state_cd, int off_cd) {

        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        String appl_no = null;

        try {
            if (regn_no != null) {
                regn_no = regn_no.toUpperCase();
            }
            tmgr = new TransactionManagerReadOnly("getApplNoFromVaFcPrint");
            sql = "SELECT appl_no FROM " + TableList.VA_FC_PRINT + " WHERE regn_no=? and state_cd=? and off_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                appl_no = rs.getString("appl_no");
            }

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
        return appl_no;
    }

    public String getPendingApplNoInwardotheroffices(String regnNo, String stateCd, int offcd) throws VahanException {
        String isPending = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getPendingApplNoInwardotheroffices");
            String sql = " SELECT appl_no FROM " + TableList.VA_DETAILS
                    + " WHERE regn_no = ? and entry_status <> ? and state_cd = ? and off_cd=?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, TableConstants.STATUS_APPROVED);
            ps.setString(3, stateCd);
            ps.setInt(4, offcd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                isPending = Util.getLocaleMsg("pending_applno") + " [ " + rs.getString("appl_no") + " ]";
                return isPending;
            }
            sql = "select d.appl_no ,d.state_cd,d.off_cd  from " + TableList.VA_DETAILS + " d inner join " + TableList.VA_INWARD_OTH_OFFICE + " a on d.appl_no=a.appl_no where d.regn_no=? and entry_status <>? and a.off_cd_fr=? and a.state_cd_fr=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, TableConstants.STATUS_APPROVED);
            ps.setInt(3, offcd);
            ps.setString(4, stateCd);
            RowSet rs2 = tmgr.fetchDetachedRowSet();
            if (rs2.next()) {
                isPending = Util.getLocaleMsg("pending_applno") + " [ " + rs2.getString("appl_no") + " ]" + Util.getLocaleMsg("in_state") + " [ " + ServerUtil.getStateNameByStateCode(rs2.getString("state_cd")) + " ] " + Util.getLocaleMsg("at_office") + " [ " + ServerUtil.getOfficeName(rs2.getInt("off_cd"), rs2.getString("state_cd")) + " ]";
                return isPending;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
}
