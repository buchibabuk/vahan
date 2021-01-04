/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.admin;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.admin.ChangesByUser;
import nic.vahan.form.bean.admin.RegistrationOwnerAdminDobj;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.dobj.ExArmyDobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.ImportedVehicleDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.OtherStateVehDobj;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Owner_temp_dobj;
import nic.vahan.form.dobj.ReflectiveTapeDobj;
import nic.vahan.form.dobj.RetroFittingDetailsDobj;
import nic.vahan.form.dobj.SpeedGovernorDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TempRegDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.Trailer_dobj;
import nic.vahan.form.dobj.smartcard.SmartCardDobj;
import nic.vahan.form.impl.AxleImpl;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.ExArmyImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.HpaImpl;
import nic.vahan.form.impl.ImportedVehicleImpl;
import nic.vahan.form.impl.InsImpl;
import static nic.vahan.form.impl.InsImpl.insertIntoInsuranceHistory;
import nic.vahan.form.impl.NewImpl;
import nic.vahan.form.impl.OtherStateVehImpl;
import nic.vahan.form.impl.OwnerIdentificationImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.RetroFittingDetailsImpl;
import nic.vahan.form.impl.SmartCardImpl;
import nic.vahan.form.impl.TempRegImpl;
import nic.vahan.form.impl.Trailer_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import nic.vahan.server.mail.MailSender;
import nic.vahan.services.insurance.InsuranceDetailService;
import nic.vahan.services.insurance.dobj.InsuranceInfoDobj;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Administrator
 */
public class OwnerAdminImpl {

    private static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(OwnerAdminImpl.class);

    public List<ChangesByUser> getModificationOnRegNoByUser(String regnNo) {
        List<ChangesByUser> prevChangedDataList = new ArrayList<>();

        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        String query = "";
        query = " select d.emp_cd,ui.user_name,d.changed_data,to_char(d.op_dt,'DD-MON-YYYY HH24:MI:SS') as op_dt"
                + "  from vha_changed_data d left outer join tm_user_info ui"
                + "  on d.emp_cd = ui.user_cd"
                + "  where d.appl_no = ? "
                + "  order by d.op_dt::timestamp  desc";
        try {
            tmgr = new TransactionManagerReadOnly("dataChangedByPreviousUser");
            ps = tmgr.prepareStatement(query);
            ps.setString(1, regnNo);
            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                ChangesByUser bean = new ChangesByUser();
                bean.setUser(rs.getInt("emp_cd"));
                bean.setUserName(rs.getString("user_name"));
                bean.setChanged_data(rs.getString("changed_data").replace("|", "&nbsp; <font color=\"red\">|</font> &nbsp;"));
                bean.setOp_dt(rs.getString("op_dt"));
                prevChangedDataList.add(bean);
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

        return prevChangedDataList;
    }

    public Owner_dobj getRegistrationDetails(String regn_no, int purCode) throws VahanException {

        Owner_dobj ownDobj = null;
        try {
            TmConfigurationDobj tmconDobj = Util.getTmConfiguration();
            ownDobj = new OwnerImpl().set_Owner_appl_db_to_dobj_with_state_off_cd(regn_no, null, null, purCode);
            if (ownDobj == null) {
                throw new VahanException(Util.getLocaleMsg("invalidRegn"));
            } else if (purCode == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE && !(ownDobj.getState_cd().equals(Util.getUserStateCode())
                    && ownDobj.getOff_cd() == Util.getSelectedSeat().getOff_cd())) {
                throw new VahanException(Util.getLocaleMsg("notCurrntRto"));
            } else if (purCode == TableConstants.VM_TRANSACTION_MAST_TEMP_REG
                    && !ownDobj.getState_cd().equals(Util.getUserStateCode())
                    && !ownDobj.getDob_temp().getState_cd_to().equals(Util.getUserStateCode())) {
                throw new VahanException(Util.getLocaleMsg("notAuthTmp"));
            } else if (purCode == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE && !tmconDobj.getOwner_admin_modify_on_status().contains(ownDobj.getStatus())) {
                throw new VahanException("NOC is Taken for this Vehicle, Can not do Modification");
            } else if (purCode == TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
                String applno = checkChassisNo(ownDobj.getChasi_no());
                if (!CommonUtils.isNullOrBlank(applno) && !applno.equalsIgnoreCase(ownDobj.getAppl_no())) {
                    throw new VahanException("Application No. " + applno + " already pending for Chassis No :" + ownDobj.getChasi_no() + ".");
                }
            }

            AxleDetailsDobj axelDobj = AxleImpl.setAxleVehDetails_db_to_dobj(null, regn_no, ownDobj.getState_cd(), ownDobj.getOff_cd());
            OtherStateVehDobj otherStateVehDobj = new OtherStateVehImpl().setVTOtherVehicleDetailsToDobj(regn_no);
            TempRegDobj tempReg = new TempRegImpl().getVtTempRegnDtl(regn_no, ownDobj.getState_cd(), ownDobj.getOff_cd());

            List<HpaDobj> listHpaDobj = new HpaImpl().getHypothecationList(regn_no, ownDobj.getState_cd(), ownDobj.getOff_cd());
            InsDobj insDobj = getInsuranceDataFromService(ownDobj);
            if (insDobj == null) {
                insDobj = InsImpl.set_ins_dtls_db_to_dobj(regn_no, null, ownDobj.getState_cd(), ownDobj.getOff_cd());
            }
            ExArmyDobj exArmy_dobj = ExArmyImpl.setExArmyDetails_db_to_dobj(null, regn_no, ownDobj.getState_cd(), ownDobj.getOff_cd());

            ImportedVehicleDobj imp_Dobj = new ImportedVehicleImpl().getImportedVehicleDetails(regn_no, ownDobj.getState_cd(), ownDobj.getOff_cd());
            ownDobj.setTempReg(tempReg);
            ownDobj.setAxleDobj(axelDobj);
            RetroFittingDetailsDobj cng_dobj = RetroFittingDetailsImpl.setCngDetails_db_to_dobj_VT(regn_no, ownDobj.getState_cd(), ownDobj.getOff_cd());
            ownDobj.setOtherStateVehDobj(otherStateVehDobj);
            ownDobj.setListHpaDobj(listHpaDobj);
            ownDobj.setInsDobj(insDobj);
            ownDobj.setExArmy_dobj(exArmy_dobj);
            ownDobj.setImp_Dobj(imp_Dobj);
            ownDobj.setCng_dobj(cng_dobj);
            if (purCode == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) {
                Trailer_Impl trailer_Impl = new Trailer_Impl();
                Trailer_dobj trailer_dobj = trailer_Impl.set_trailer_dtls_to_dobj("", regn_no.trim(), 0);
                if (trailer_dobj != null) {
                    ownDobj.setTrailerDobj(trailer_dobj);
                }
            }
            String vehClass = String.valueOf(ownDobj.getVh_class());
            if (!Util.getUserStateCode().equals("OR") && TableConstants.TRAILER_VEH_CLASS.contains("," + vehClass + ",") && purCode == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) {
                ownDobj.setLinkedRegnNo(getLinkVehicleDtls(ownDobj.getState_cd(), ownDobj.getOff_cd(), regn_no));
            } else if (!Util.getUserStateCode().equals("OR") && TableConstants.TRAILER_VEH_CLASS.contains("," + vehClass + ",") && purCode == TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
                ownDobj.setLinkedRegnNo(getLinkVaRegnoDtls(ownDobj.getAppl_no(), ownDobj.getState_cd()));
            }

            if (Util.getUserStateCode().equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(ownDobj.getVh_class()) + ",")) {
                // NewImpl.insertOrUpdateVaOwnerOther(tmgr, dobj);
                ownDobj = getOwnerOtherDetails(ownDobj);
            }

        } catch (VahanException vme) {
            throw vme;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleMsg("errorFtchDtls"));
        }

        return ownDobj;

    }

    public Owner_dobj getRegistrationDetailsafterinward(String regn_no, String appl_no, int purCode) throws VahanException {
        Owner_dobj ownDobj = null;
        try {
            ownDobj = new OwnerImpl().set_Owner_appl_db_to_dobj_with_state_off_cd(null, appl_no, null, purCode);

            if (ownDobj == null) {
                throw new VahanException(Util.getLocaleMsg("invalidRegn"));
            } else if (purCode == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE && !(ownDobj.getState_cd().equals(Util.getUserStateCode()))
                    && (ownDobj.getOff_cd() == Util.getUserLoginOffCode() && !Util.getUserCategory().equals(TableConstants.USER_CATG_STATE_ADMIN))) {//forstateadmin
                throw new VahanException(Util.getLocaleMsg("notCurrntRto"));
            } else if (purCode == TableConstants.VM_TRANSACTION_MAST_TEMP_REG
                    && !ownDobj.getState_cd().equals(Util.getUserStateCode())
                    && !ownDobj.getDob_temp().getState_cd_to().equals(Util.getUserStateCode())) {
                throw new VahanException(Util.getLocaleMsg("notAuthTmp"));
            }
            HpaDobj listHpaDobj2 = new HpaImpl().set_HPA_appl_db_to_dobj(appl_no, null, purCode, ownDobj.getState_cd(), ownDobj.getOff_cd());
            if (listHpaDobj2 != null) {
                List<HpaDobj> listHpaDobj = new ArrayList<HpaDobj>();
                listHpaDobj.add(listHpaDobj2);
                ownDobj.setListHpaDobj(listHpaDobj);
            }
            ImportedVehicleDobj imp_Dobj = null;
            if (TableConstants.VM_REGN_TYPE_IMPORTED_YES.equalsIgnoreCase(ownDobj.getImported_vch())) {
                imp_Dobj = new ImportedVehicleImpl().setImpVehDetails_db_to_dobj(appl_no, null, ownDobj.getState_cd(), ownDobj.getOff_cd());

            }
            RetroFittingDetailsDobj cng_dobj = null;
            int fuel_type = ownDobj.getFuel();
            if (fuel_type == TableConstants.VM_FUEL_CNG_TYPE
                    || fuel_type == TableConstants.VM_FUEL_TYPE_PETROL_CNG
                    || fuel_type == TableConstants.VM_FUEL_TYPE_LPG
                    || fuel_type == TableConstants.VM_FUEL_TYPE_PETROL_LPG) {
                cng_dobj = RetroFittingDetailsImpl.setCngDetails_db_to_dobj(appl_no);

            }
            ownDobj.setImp_Dobj(imp_Dobj);
            ownDobj.setCng_dobj(cng_dobj);
            if (purCode == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) {
                Trailer_Impl trailer_Impl = new Trailer_Impl();
                Trailer_dobj trailer_dobj = trailer_Impl.set_trailer_dtls_to_dobj(appl_no, "", 1);
                if (trailer_dobj != null) {
                    ownDobj.setTrailerDobj(trailer_dobj);
                }
            }
            String vehClass = String.valueOf(ownDobj.getVh_class());

            if (!Util.getUserStateCode().equals("OR") && TableConstants.TRAILER_VEH_CLASS.contains("," + vehClass + ",")) {
                ownDobj.setLinkedRegnNo(getLinkVaRegnoDtls(appl_no, ownDobj.getState_cd()));
            }
            InsDobj insDobj = getInsuranceDataFromService(ownDobj);
            if (insDobj != null) {
                if (!insDobj.isIibData()) {
                    insDobj = InsImpl.set_ins_dtls_db_to_dobj(null, appl_no, ownDobj.getState_cd(), ownDobj.getOff_cd());
                }
                ownDobj.setInsDobj(insDobj);
            }
        } catch (VahanException vme) {
            throw vme;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleMsg("errorFtchDtls"));
        }

        return ownDobj;

    }

    public void insertIntoVh_owner(Owner_dobj ownerDobj, TransactionManager tmgr) throws VahanException {
        try {
            String sql = "INSERT INTO " + TableList.VH_OWNER
                    + " (state_cd, off_cd, regn_no, regn_dt, purchase_dt, owner_sr, owner_name,"
                    + " f_name, c_add1, c_add2, c_add3, c_district, c_pincode, c_state,"
                    + " p_add1, p_add2, p_add3, p_district, p_pincode, p_state, owner_cd,"
                    + " regn_type, vh_class, chasi_no, eng_no, maker, maker_model, body_type,"
                    + " no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, ld_wt,"
                    + " gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, cubic_cap,"
                    + " floor_area, ac_fitted, audio_fitted, video_fitted, vch_purchase_as,"
                    + " vch_catg, dealer_cd, sale_amt, laser_code, garage_add, length,"
                    + " width, height, regn_upto, fit_upto, annual_income, imported_vch,"
                    + " other_criteria, status, op_dt, appl_no, moved_on, moved_by,moved_status)"
                    + "   SELECT state_cd, off_cd, regn_no, regn_dt, purchase_dt, owner_sr, owner_name,"
                    + "       f_name, c_add1, c_add2, c_add3, c_district, c_pincode, c_state,"
                    + "       p_add1, p_add2, p_add3, p_district, p_pincode, p_state, owner_cd,"
                    + "       regn_type, vh_class, chasi_no, eng_no, maker, maker_model, body_type,"
                    + "       no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, ld_wt,"
                    + "       gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, cubic_cap,"
                    + "       floor_area, ac_fitted, audio_fitted, video_fitted, vch_purchase_as,"
                    + "       vch_catg, dealer_cd, sale_amt, laser_code, garage_add, length,"
                    + "       width, height, regn_upto, fit_upto, annual_income, imported_vch,"
                    + "       other_criteria, status, op_dt, ?,current_timestamp,?,?"
                    + "  FROM " + TableList.VT_OWNER
                    + "  where regn_no=? and state_cd=? and off_cd=?";

            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDobj.getAppl_no());
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, TableConstants.VH_MOVED_STATUS_UPDATE);
            ps.setString(4, ownerDobj.getRegn_no());
            ps.setString(5, ownerDobj.getState_cd());
            ps.setInt(6, ownerDobj.getOff_cd());
            ps.executeUpdate();

        } catch (Exception e) {
            throw new VahanException(e.getMessage());
        }
    }

    public void insertIntoVhOwnerTemp(Owner_dobj dobj, TransactionManager tmgr) throws VahanException {
        try {
            String sql = "INSERT INTO " + TableList.VH_OWNER_TEMP
                    + " SELECT state_cd, off_cd, appl_no, temp_regn_no, valid_from, valid_upto, \n"
                    + "       purchase_dt, owner_name, f_name, c_add1, c_add2, c_add3, c_district, \n"
                    + "       c_pincode, c_state, p_add1, p_add2, p_add3, p_district, p_pincode, \n"
                    + "       p_state, owner_cd, regn_type, vh_class, chasi_no, eng_no, maker, \n"
                    + "       maker_model, body_type, no_cyl, hp, seat_cap, stand_cap, sleeper_cap, \n"
                    + "       unld_wt, ld_wt, gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, \n"
                    + "       cubic_cap, floor_area, ac_fitted, audio_fitted, video_fitted, \n"
                    + "       vch_purchase_as, vch_catg, dealer_cd, sale_amt, laser_code, garage_add, \n"
                    + "       length, width, height, regn_upto, fit_upto, annual_income, imported_vch, \n"
                    + "       other_criteria, state_cd_to, off_cd_to, op_dt,current_timestamp as moved_on,"
                    + " ? as moved_by, ? as reason, purpose, body_building FROM " + TableList.VT_OWNER_TEMP
                    + " WHERE temp_regn_no=? and appl_no =?";

            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, "CHANGE BY OWNER ADMIN FORM");
            ps.setString(3, dobj.getDob_temp().getTemp_regn_no());
            ps.setString(4, dobj.getDob_temp().getAppl_no());
            ps.executeUpdate();

        } catch (Exception e) {
            throw new VahanException(e.getMessage());
        }
    }

    public void updateVt_owner(Owner_dobj dobj, TransactionManager tmgr) throws VahanException {

        if (dobj == null) {
            return;
        }

        insertIntoVh_owner(dobj, tmgr);

        String sql = "UPDATE  " + TableList.VT_OWNER
                + "   SET regn_dt=?, purchase_dt=?, owner_sr=?, "//state_cd=?, off_cd=?, regn_no=?, 
                + "       owner_name=?, f_name=?, c_add1=?, c_add2=?, c_add3=?, c_district=?, "
                + "       c_pincode=?, c_state=?, p_add1=?, p_add2=?, p_add3=?, p_district=?, "
                + "       p_pincode=?, p_state=?, owner_cd=?, regn_type=?, vh_class=?, "
                + "       chasi_no=?, eng_no=?, maker=?, maker_model=?, body_type=?, no_cyl=?, "
                + "       hp=?, seat_cap=?, stand_cap=?, sleeper_cap=?, unld_wt=?, ld_wt=?, "
                + "       gcw=?, fuel=?, color=?, manu_mon=?, manu_yr=?, norms=?, wheelbase=?, "
                + "       cubic_cap=?, floor_area=?, ac_fitted=?, audio_fitted=?, video_fitted=?, "
                + "       vch_purchase_as=?, vch_catg=?, dealer_cd=?, sale_amt=?, laser_code=?, "
                + "       garage_add=?, length=?, width=?, height=?, regn_upto=?, fit_upto=?, "
                + "       annual_income=?, imported_vch=?, other_criteria=?, op_dt=current_timestamp"
                + "       WHERE regn_no=? and state_cd= ? and off_cd= ? ";
        PreparedStatement ps = null;

        try {
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            //ps.setString(i++, dobj.getState_cd());
            //ps.setInt(i++, dobj.getOff_cd());
            //ps.setString(i++, dobj.getRegn_no());
            ps.setDate(i++, new java.sql.Date(dobj.getRegn_dt().getTime()));
            ps.setDate(i++, new java.sql.Date(dobj.getPurchase_dt().getTime()));
            ps.setInt(i++, dobj.getOwner_sr());
            ps.setString(i++, dobj.getOwner_name());
            ps.setString(i++, dobj.getF_name());
            ps.setString(i++, dobj.getC_add1());
            ps.setString(i++, dobj.getC_add2());
            ps.setString(i++, dobj.getC_add3());
            ps.setInt(i++, dobj.getC_district());
            ps.setInt(i++, dobj.getC_pincode());
            ps.setString(i++, dobj.getC_state());
            ps.setString(i++, dobj.getP_add1());
            ps.setString(i++, dobj.getP_add2());
            ps.setString(i++, dobj.getP_add3());
            ps.setInt(i++, dobj.getP_district());
            ps.setInt(i++, dobj.getP_pincode());
            ps.setString(i++, dobj.getP_state());
            ps.setInt(i++, dobj.getOwner_cd());
            ps.setString(i++, dobj.getRegn_type());
            ps.setInt(i++, dobj.getVh_class());
            ps.setString(i++, dobj.getChasi_no());
            ps.setString(i++, dobj.getEng_no());
            ps.setInt(i++, dobj.getMaker());
            ps.setString(i++, dobj.getMaker_model());
            ps.setString(i++, dobj.getBody_type());
            ps.setInt(i++, dobj.getNo_cyl());
            ps.setFloat(i++, dobj.getHp());
            ps.setInt(i++, dobj.getSeat_cap());
            ps.setInt(i++, dobj.getStand_cap());
            ps.setInt(i++, dobj.getSleeper_cap());
            ps.setInt(i++, dobj.getUnld_wt());
            ps.setInt(i++, dobj.getLd_wt());
            ps.setInt(i++, dobj.getGcw());
            ps.setInt(i++, dobj.getFuel());
            ps.setString(i++, dobj.getColor());
            ps.setInt(i++, dobj.getManu_mon());
            ps.setInt(i++, dobj.getManu_yr());
            ps.setInt(i++, dobj.getNorms());
            ps.setInt(i++, dobj.getWheelbase());
            ps.setFloat(i++, dobj.getCubic_cap());
            ps.setFloat(i++, dobj.getFloor_area());
            ps.setString(i++, dobj.getAc_fitted());
            ps.setString(i++, dobj.getAudio_fitted());
            ps.setString(i++, dobj.getVideo_fitted());
            ps.setString(i++, dobj.getVch_purchase_as());
            ps.setString(i++, dobj.getVch_catg());
            ps.setString(i++, dobj.getDealer_cd());
            ps.setInt(i++, dobj.getSale_amt());
            ps.setString(i++, dobj.getLaser_code());
            ps.setString(i++, dobj.getGarage_add());
            ps.setInt(i++, dobj.getLength());
            ps.setInt(i++, dobj.getWidth());
            ps.setInt(i++, dobj.getHeight());
            ps.setDate(i++, new java.sql.Date(dobj.getRegn_upto().getTime()));
            ps.setDate(i++, new java.sql.Date(dobj.getFit_upto().getTime()));
            ps.setInt(i++, dobj.getAnnual_income());
            ps.setString(i++, dobj.getImported_vch());
            ps.setInt(i++, dobj.getOther_criteria());
            //ps.setString(i++, dobj.getStatus());
            //ps.setString(i++, dobj.getOp_dt());
            ps.setString(i++, dobj.getRegn_no());
            ps.setString(i++, dobj.getState_cd());
            ps.setInt(i++, dobj.getOff_cd());
            ps.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }

    }

    public void updateVtOwnerTemp(Owner_dobj dobj, TransactionManager tmgr) throws VahanException {

        if (dobj == null && dobj.getDob_temp() == null) {
            return;
        }

        insertIntoVhOwnerTemp(dobj, tmgr);

        String sql = "UPDATE " + TableList.VT_OWNER_TEMP
                //+ "   SET state_cd=?, off_cd=?, appl_no=?, temp_regn_no=?, valid_from=?, valid_upto=?, " 
                + "   SET purchase_dt=?, owner_name=?, f_name=?, c_add1=?,"
                + "       c_add2=?, c_add3=?, c_district=?, c_pincode=?, c_state=?, p_add1=?,"
                + "       p_add2=?, p_add3=?, p_district=?, p_pincode=?, p_state=?, owner_cd=?,"
                + "       regn_type=?, vh_class=?, chasi_no=?, eng_no=?, maker=?, maker_model=?,"
                + "       body_type=?, no_cyl=?, hp=?, seat_cap=?, stand_cap=?, sleeper_cap=?,"
                + "       unld_wt=?, ld_wt=?, gcw=?, fuel=?, color=?, manu_mon=?, manu_yr=?,"
                + "       norms=?, wheelbase=?, cubic_cap=?, floor_area=?, ac_fitted=?,"
                + "       audio_fitted=?, video_fitted=?, vch_purchase_as=?, vch_catg=?,"
                + "       dealer_cd=?, sale_amt=?, laser_code=?, garage_add=?, length=?,"
                + "       width=?, height=?,annual_income=?," // regn_upto=?, fit_upto=?, 
                + "       imported_vch=?, other_criteria=?, state_cd_to=?, off_cd_to=?,"
                + "       op_dt=current_timestamp,purpose=?,body_building=? "
                + " WHERE appl_no=? and temp_regn_no=?";
        PreparedStatement ps = null;

        try {
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            //ps.setString(i++, dobj.getState_cd());
            //ps.setInt(i++, dobj.getOff_cd());
            //ps.setString(i++, dobj.getRegn_no());
            ps.setDate(i++, new java.sql.Date(dobj.getPurchase_dt().getTime()));
            ps.setString(i++, dobj.getOwner_name());
            ps.setString(i++, dobj.getF_name());
            ps.setString(i++, dobj.getC_add1());
            ps.setString(i++, dobj.getC_add2());
            ps.setString(i++, dobj.getC_add3());
            ps.setInt(i++, dobj.getC_district());
            ps.setInt(i++, dobj.getC_pincode());
            ps.setString(i++, dobj.getC_state());
            ps.setString(i++, dobj.getP_add1());
            ps.setString(i++, dobj.getP_add2());
            ps.setString(i++, dobj.getP_add3());
            ps.setInt(i++, dobj.getP_district());
            ps.setInt(i++, dobj.getP_pincode());
            ps.setString(i++, dobj.getP_state());
            ps.setInt(i++, dobj.getOwner_cd());
            ps.setString(i++, dobj.getRegn_type());
            ps.setInt(i++, dobj.getVh_class());
            ps.setString(i++, dobj.getChasi_no());
            ps.setString(i++, dobj.getEng_no());
            ps.setInt(i++, dobj.getMaker());
            ps.setString(i++, dobj.getMaker_model());
            ps.setString(i++, dobj.getBody_type());
            ps.setInt(i++, dobj.getNo_cyl());
            ps.setFloat(i++, dobj.getHp());
            ps.setInt(i++, dobj.getSeat_cap());
            ps.setInt(i++, dobj.getStand_cap());
            ps.setInt(i++, dobj.getSleeper_cap());
            ps.setInt(i++, dobj.getUnld_wt());
            ps.setInt(i++, dobj.getLd_wt());
            ps.setInt(i++, dobj.getGcw());
            ps.setInt(i++, dobj.getFuel());
            ps.setString(i++, dobj.getColor());
            ps.setInt(i++, dobj.getManu_mon());
            ps.setInt(i++, dobj.getManu_yr());
            ps.setInt(i++, dobj.getNorms());
            ps.setInt(i++, dobj.getWheelbase());
            ps.setFloat(i++, dobj.getCubic_cap());
            ps.setFloat(i++, dobj.getFloor_area());
            ps.setString(i++, dobj.getAc_fitted());
            ps.setString(i++, dobj.getAudio_fitted());
            ps.setString(i++, dobj.getVideo_fitted());
            ps.setString(i++, dobj.getVch_purchase_as());
            ps.setString(i++, dobj.getVch_catg());
            ps.setString(i++, dobj.getDealer_cd());
            ps.setInt(i++, dobj.getSale_amt());
            ps.setString(i++, dobj.getLaser_code());
            ps.setString(i++, dobj.getGarage_add());
            ps.setInt(i++, dobj.getLength());
            ps.setInt(i++, dobj.getWidth());
            ps.setInt(i++, dobj.getHeight());
            //ps.setDate(i++, new java.sql.Date(dobj.getRegn_upto().getTime()));
            //ps.setDate(i++, new java.sql.Date(dobj.getFit_upto().getTime()));
            ps.setInt(i++, dobj.getAnnual_income());
            ps.setString(i++, dobj.getImported_vch());
            ps.setInt(i++, dobj.getOther_criteria());
            ps.setString(i++, dobj.getDob_temp().getState_cd_to());
            ps.setInt(i++, dobj.getDob_temp().getOff_cd_to());
            ps.setString(i++, dobj.getDob_temp().getPurpose());
            ps.setString(i++, dobj.getDob_temp().getBodyBuilding());
            ps.setString(i++, dobj.getDob_temp().getAppl_no());
            ps.setString(i++, dobj.getDob_temp().getTemp_regn_no());

            ps.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }

    }

    public void insertIntoVh_ownerIdentification(Owner_dobj ownerDobj, TransactionManager tmgr) throws VahanException {

        try {

            String sql = "insert into " + TableList.VH_OWNER_IDENTIFICATION
                    + " Select state_cd,off_cd,regn_no, mobile_no, email_id, pan_no, aadhar_no, passport_no,"
                    + " ration_card_no, voter_id, dl_no, verified_on, current_timestamp as moved_on, "
                    + " ? as moved_by,owner_ctg from "
                    + TableList.VT_OWNER_IDENTIFICATION + " where regn_no=? and state_cd= ?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, ownerDobj.getRegn_no());
            ps.setString(3, ownerDobj.getState_cd());
            ps.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }
    }

    public void updateVt_ownerIdentification(OwnerIdentificationDobj dobj, Owner_dobj ownerDobj, TransactionManager tmgr) throws VahanException {

        try {

            if (dobj == null) {
                return;
            }

            insertIntoVh_ownerIdentification(ownerDobj, tmgr);

            String sql = "Delete from  " + TableList.VT_OWNER_IDENTIFICATION
                    + " WHERE regn_no=? and state_cd= ?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDobj.getRegn_no());
            ps.setString(2, ownerDobj.getState_cd());
            ps.executeUpdate();

            sql = "INSERT INTO " + TableList.VT_OWNER_IDENTIFICATION
                    + "( state_cd, off_cd, regn_no, mobile_no, email_id, pan_no, aadhar_no, passport_no, "
                    + "            ration_card_no, voter_id, dl_no, verified_on,owner_ctg )"
                    + "    VALUES (?,?,?, ?, ?, ?, ?, ?,  ?, ?, ?, current_timestamp,?)";

            int i = 1;

            ps = tmgr.prepareStatement(sql);
            ps.setString(i++, ownerDobj.getState_cd());
            ps.setInt(i++, ownerDobj.getOff_cd());
            ps.setString(i++, ownerDobj.getRegn_no());
            ps.setLong(i++, dobj.getMobile_no());
            ps.setString(i++, dobj.getEmail_id());
            ps.setString(i++, dobj.getPan_no());
            ps.setString(i++, dobj.getAadhar_no());
            ps.setString(i++, dobj.getPassport_no());
            ps.setString(i++, dobj.getRation_card_no());
            ps.setString(i++, dobj.getVoter_id());
            ps.setString(i++, dobj.getDl_no());
            ps.setInt(i++, dobj.getOwnerCatg());

            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }
    }

    public void deleteFromVt_hypth(Owner_dobj ownerDobj, TransactionManager tmgr) throws VahanException {

        try {
            String sql = "Delete from " + TableList.VT_HYPTH + " where regn_no=? and state_cd= ?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDobj.getRegn_no());
            ps.setString(2, ownerDobj.getState_cd());
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }

    }

    public void updateVT_HYPTH(HpaDobj dobj, Owner_dobj ownerDobj, TransactionManager tmgr, boolean isHypth) throws VahanException {

        try {

            if (dobj == null) {
                return;
            }
            String sql = "Insert into  " + TableList.VH_HYPTH + " SELECT "
                    + " state_cd, off_cd, regn_no, sr_no, hp_type, fncr_name, fncr_add1,  fncr_add2,"
                    + " fncr_add3, fncr_district, fncr_pincode, fncr_state, "
                    + " from_dt, op_dt, ?, "
                    + " current_timestamp, ? "
                    + " FROM " + TableList.VT_HYPTH
                    + " where regn_no = ? and state_cd = ? and off_cd=?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDobj.getAppl_no());
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, ownerDobj.getRegn_no());
            ps.setString(4, ownerDobj.getState_cd());
            ps.setInt(5, ownerDobj.getOff_cd());
            ps.executeUpdate();

            deleteFromVt_hypth(ownerDobj, tmgr);

            if (isHypth) {
                sql = "INSERT INTO " + TableList.VT_HYPTH
                        + "(state_cd, off_cd, regn_no, sr_no, hp_type, fncr_name, fncr_add1, fncr_add2, fncr_add3, "
                        + "            fncr_district, fncr_pincode, fncr_state, from_dt, op_dt)"
                        + "    VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, "
                        + "            ?, ?, ?, ?, current_timestamp)";

                ps = tmgr.prepareStatement(sql);
                int i = 1;
                ps.setString(i++, ownerDobj.getState_cd());
                ps.setInt(i++, ownerDobj.getOff_cd());
                ps.setString(i++, ownerDobj.getRegn_no());
                ps.setInt(i++, 1);
                ps.setString(i++, dobj.getHp_type());
                ps.setString(i++, dobj.getFncr_name());
                ps.setString(i++, dobj.getFncr_add1());
                ps.setString(i++, dobj.getFncr_add2());
                ps.setString(i++, dobj.getFncr_add3());
                ps.setInt(i++, dobj.getFncr_district());
                ps.setInt(i++, dobj.getFncr_pincode());
                ps.setString(i++, dobj.getFncr_state());
                ps.setDate(i++, new java.sql.Date(dobj.getFrom_dt().getTime()));
                ps.executeUpdate();
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }
    }

    public void insertIntoVhInsurance(Owner_dobj ownerDobj, TransactionManager tmgr) throws VahanException {
        String sql = "Insert into " + TableList.VH_INSURANCE + " Select regn_no, comp_cd, ins_type, ins_from, ins_upto,  "
                + "       policy_no,current_timestamp,?,state_cd, off_cd,idv from " + TableList.VT_INSURANCE
                + " where regn_no=? and state_cd= ?";
        try {
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, ownerDobj.getRegn_no());
            ps.setString(3, ownerDobj.getState_cd());
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }
    }

    public void updateVT_Ins(InsDobj dobj, Owner_dobj ownerDobj, TransactionManager tmgr) throws VahanException {

        if (dobj == null) {
            return;
        }

        try {

            insertIntoVhInsurance(ownerDobj, tmgr);

            String sql = "Delete from " + TableList.VT_INSURANCE + " where regn_no=? and state_cd= ?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDobj.getRegn_no());
            ps.setString(2, ownerDobj.getState_cd());
            ps.executeUpdate();

            sql = "INSERT INTO " + TableList.VT_INSURANCE
                    + "( state_cd, off_cd,regn_no, comp_cd, ins_type, ins_from, ins_upto, policy_no,idv,op_dt) "
                    + "    VALUES (?,?,?, ?, ?, ?, ?, ?,?,current_timestamp)";
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, ownerDobj.getState_cd());
            ps.setInt(i++, ownerDobj.getOff_cd());
            ps.setString(i++, ownerDobj.getRegn_no());
            ps.setInt(i++, dobj.getComp_cd());
            ps.setInt(i++, dobj.getIns_type());

            if (dobj.getIns_from() != null) {
                ps.setDate(i++, new java.sql.Date(dobj.getIns_from().getTime()));
            } else {
                ps.setNull(i++, java.sql.Types.DATE);
            }

            if (dobj.getIns_upto() != null) {
                ps.setDate(i++, new java.sql.Date(dobj.getIns_upto().getTime()));
            } else {
                ps.setNull(i++, java.sql.Types.DATE);
            }

            ps.setString(i++, dobj.getPolicy_no());
            ps.setLong(i++, dobj.getIdv());

            ps.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }

    }

    public void updateVTFitness(FitnessDobj fitDobj, TransactionManager tmgr) throws VahanException {

        if (fitDobj == null) {
            return;
        }
        String sql = null;
        PreparedStatement ps = null;
        try {
            sql = " INSERT INTO " + TableList.VH_FITNESS
                    + " SELECT state_cd, off_cd, ? , regn_no, chasi_no, fit_chk_dt, fit_chk_tm, "
                    + "       fit_result, fit_valid_to, fit_nid, fit_off_cd1, fit_off_cd2, "
                    + "       remark, fare_mtr_no, speedgov_no, speedgov_compname, brake, steer, "
                    + "       susp, engin, tyre, horn, lamp, embo, speed, paint, wiper, dimen, "
                    + "       body, fare, elec, finis, road, poll, transm, glas, emis, rear, "
                    + "       others, op_dt,current_timestamp,? "
                    + "  FROM " + TableList.VT_FITNESS + " where regn_no=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, fitDobj.getAppl_no());//app
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, fitDobj.getRegn_no());
            ps.setString(4, fitDobj.getState_cd());
            ps.executeUpdate();

            sql = "DELETE FROM " + TableList.VT_FITNESS
                    + " WHERE regn_no=? and state_cd=? and"
                    + " op_dt < (SELECT MAX(op_dt) FROM " + TableList.VT_FITNESS
                    + " WHERE regn_no=? and state_cd=?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, fitDobj.getRegn_no());
            ps.setString(2, fitDobj.getState_cd());
            ps.setString(3, fitDobj.getRegn_no());
            ps.setString(4, fitDobj.getState_cd());
            ps.executeUpdate();

            sql = "UPDATE " + TableList.VT_FITNESS
                    + " SET fit_valid_to=?,fit_nid=?,remark=?,op_dt=current_timestamp"
                    + " WHERE regn_no=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(fitDobj.getFit_valid_to().getTime()));
            ps.setDate(2, new java.sql.Date(fitDobj.getFit_nid().getTime()));
            ps.setString(3, fitDobj.getRemark());
            ps.setString(4, fitDobj.getRegn_no());
            ps.setString(5, fitDobj.getState_cd());
            ps.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }
    }

    public void insertVhOtherState(Owner_dobj ownerDobj, TransactionManager tmgr) throws VahanException {
        try {
            String sql = "insert into " + TableList.VH_OTHER_STATE_VEH + " select *,?,current_timestamp,? from " + TableList.VT_OTHER_STATE_VEH
                    + " where new_regn_no=? and state_cd= ? and off_cd= ? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDobj.getAppl_no());
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, ownerDobj.getRegn_no());
            ps.setString(4, ownerDobj.getState_cd());
            ps.setInt(5, ownerDobj.getOff_cd());
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }
    }

    public void updateVtOtherStateVeh(OtherStateVehDobj dobj, Owner_dobj ownerDobj, TransactionManager tmgr) throws VahanException {

        if (dobj == null) {
            return;
        }
        try {

            insertVhOtherState(ownerDobj, tmgr);

            String sql = "Delete from " + TableList.VT_OTHER_STATE_VEH
                    + " where new_regn_no=? and state_cd= ? and off_cd= ?";

            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDobj.getRegn_no());
            ps.setString(2, ownerDobj.getState_cd());
            ps.setInt(3, ownerDobj.getOff_cd());
            ps.executeUpdate();

            sql = "INSERT INTO " + TableList.VT_OTHER_STATE_VEH
                    + " (state_cd, off_cd, new_regn_no, entry_dt, old_regn_no, old_rgst_auth, "
                    + "     old_off_cd, old_state_cd, ncrb_ref, confirm_ref, noc_dt, noc_no,  op_dt)"
                    + "    VALUES (?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?,  current_timestamp)";

            int i = 1;
            ps = tmgr.prepareStatement(sql);
            ps.setString(i++, ownerDobj.getState_cd());
            ps.setInt(i++, ownerDobj.getOff_cd());
            ps.setString(i++, ownerDobj.getRegn_no());
            ps.setDate(i++, new java.sql.Date(dobj.getStateEntryDate().getTime()));
            ps.setString(i++, dobj.getOldRegnNo());
            ps.setString(i++, "");
            ps.setInt(i++, dobj.getOldOffCD());
            ps.setString(i++, dobj.getOldStateCD());
            ps.setString(i++, dobj.getNcrbRef());
            ps.setString(i++, dobj.getConfirmRef());
            ps.setDate(i++, new java.sql.Date(dobj.getNocDate().getTime()));
            ps.setString(i++, dobj.getNocNo());
            ps.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }
    }

    public void insertIntoVhTempRegnDtls(Owner_dobj ownerDobj, TransactionManager tmgr) throws VahanException {
        try {
            String sql = "insert into " + TableList.VH_TMP_REGN_DTL + " select state_cd, off_cd,regn_no, tmp_off_cd, regn_auth, tmp_state_cd, "
                    + "tmp_regn_no, tmp_regn_dt, tmp_valid_upto, dealer_cd,?,current_timestamp,? from " + TableList.VT_TMP_REGN_DTL
                    + " where regn_no=? and state_cd= ? and off_cd= ? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);

            ps.setString(1, ownerDobj.getAppl_no());
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, ownerDobj.getRegn_no());
            ps.setString(4, ownerDobj.getState_cd());
            ps.setInt(5, ownerDobj.getOff_cd());
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }
    }

    public void updateVtTempRegnDetails(TempRegDobj dobj, Owner_dobj ownerDobj, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        try {
            insertIntoVhTempRegnDtls(ownerDobj, tmgr);
            String sql = "Delete from " + TableList.VT_TMP_REGN_DTL + " where regn_no=? and state_cd= ? and off_cd= ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDobj.getRegn_no());
            ps.setString(2, ownerDobj.getState_cd());
            ps.setInt(3, ownerDobj.getOff_cd());
            ps.executeUpdate();

            if (dobj != null) {
                sql = "INSERT INTO " + TableList.VT_TMP_REGN_DTL
                        + " (state_cd, off_cd,regn_no, tmp_off_cd, regn_auth, tmp_state_cd, tmp_regn_no, tmp_regn_dt,  tmp_valid_upto, dealer_cd, op_dt)"
                        + "  VALUES (?,?,?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";
                int i = 1;
                ps = tmgr.prepareStatement(sql);
                ps.setString(i++, ownerDobj.getState_cd());
                ps.setInt(i++, ownerDobj.getOff_cd());
                ps.setString(i++, ownerDobj.getRegn_no());
                ps.setInt(i++, dobj.getTmp_off_cd());
                ps.setString(i++, dobj.getRegn_auth());
                ps.setString(i++, dobj.getTmp_state_cd());
                ps.setString(i++, dobj.getTmp_regn_no());
                ps.setDate(i++, new java.sql.Date(dobj.getTmp_regn_dt().getTime()));
                ps.setDate(i++, new java.sql.Date(dobj.getTmp_valid_upto().getTime()));
                ps.setString(i++, dobj.getDealer_cd());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }

    }

    public void insertUpdateVhAxel(Owner_dobj ownerDobj, TransactionManager tmgr) throws VahanException {
        try {
            String sql = "INSERT INTO " + TableList.VH_AXLE
                    + " SELECT state_cd, off_cd, regn_no, f_axle_descp, r_axle_descp, o_axle_descp, "
                    + "       t_axle_descp, f_axle_weight, r_axle_weight, o_axle_weight, t_axle_weight, "
                    + "       ? as appl_no, current_timestamp as moved_on, ? as moved_by,no_of_axles "
                    + "  FROM " + TableList.VT_AXLE + " WHERE regn_no = ? and state_cd = ? and off_cd = ? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDobj.getAppl_no());
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, ownerDobj.getRegn_no());
            ps.setString(4, ownerDobj.getState_cd());
            ps.setInt(5, ownerDobj.getOff_cd());
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }

    }

    public void insertUpdateVtAxel(AxleDetailsDobj dobj, Owner_dobj ownerDobj, TransactionManager tmgr) throws VahanException {
        try {
            insertUpdateVhAxel(ownerDobj, tmgr);
            String sql = "delete from  " + TableList.VT_AXLE
                    + "    WHERE regn_no=? and state_cd= ? and off_cd= ?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDobj.getRegn_no());
            ps.setString(2, ownerDobj.getState_cd());
            ps.setInt(3, ownerDobj.getOff_cd());
            ps.executeUpdate();
            if (dobj == null) {
                return;
            }
            sql = "INSERT INTO " + TableList.VT_AXLE
                    + "(state_cd, off_cd,regn_no, f_axle_descp, r_axle_descp, o_axle_descp, t_axle_descp,"
                    + "    f_axle_weight, r_axle_weight, o_axle_weight, t_axle_weight,op_dt,no_of_axles,"
                    + "    r_overhang, f_axle_tyre, r_axle_tyre, o_axle_tyre,t_axle_tyre)"
                    + "    VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp,?,?,?,?,?,?)";

            int i = 1;
            ps = tmgr.prepareStatement(sql);
            ps.setString(i++, ownerDobj.getState_cd());
            ps.setInt(i++, ownerDobj.getOff_cd());
            ps.setString(i++, ownerDobj.getRegn_no());
            ps.setString(i++, dobj.getTf_Front1());
            ps.setString(i++, dobj.getTf_Rear1());
            ps.setString(i++, dobj.getTf_Other1());
            ps.setString(i++, dobj.getTf_Tandem1());
            ps.setInt(i++, dobj.getTf_Front());
            ps.setInt(i++, dobj.getTf_Rear());
            ps.setInt(i++, dobj.getTf_Other());
            ps.setInt(i++, dobj.getTf_Tandem());
            ps.setInt(i++, dobj.getNoOfAxle());
            ps.setInt(i++, dobj.getTf_Rear_Over());
            ps.setInt(i++, dobj.getTf_Front_tyre());
            ps.setInt(i++, dobj.getTf_Rear_tyre());
            ps.setInt(i++, dobj.getTf_Other_tyre());
            ps.setInt(i++, dobj.getTf_Tandem_tyre());
            ps.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }

    }

    public void insertIntoVhOwnerExArmy(Owner_dobj ownerDobj, TransactionManager tmgr) throws VahanException {
        try {
            String sql = "insert into " + TableList.VH_OWNER_EX_ARMY + " select state_cd, off_cd,regn_no, voucher_no, voucher_dt, place,?,current_timestamp,? from  "
                    + TableList.VT_OWNER_EX_ARMY + " where regn_no=? and state_cd= ? and off_cd= ?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDobj.getAppl_no());
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, ownerDobj.getRegn_no());
            ps.setString(4, ownerDobj.getState_cd());
            ps.setInt(5, ownerDobj.getOff_cd());
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }
    }

    public void updateVT_OWNER_EX_ARMY(ExArmyDobj dobj, Owner_dobj ownerDobj, TransactionManager tmgr) throws VahanException {

        try {

            insertIntoVhOwnerExArmy(ownerDobj, tmgr);

            String sql = "Delete from " + TableList.VT_OWNER_EX_ARMY
                    + " WHERE regn_no=? and state_cd= ? and off_cd= ?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDobj.getRegn_no());
            ps.setString(2, ownerDobj.getState_cd());
            ps.setInt(3, ownerDobj.getOff_cd());
            ps.executeUpdate();

            if (dobj != null) {

                sql = "INSERT INTO " + TableList.VT_OWNER_EX_ARMY
                        + "(state_cd, off_cd, regn_no, voucher_no, voucher_dt, place, op_dt )"
                        + "    VALUES (?,?, ?, ?, ?, ?, current_timestamp)";
                ps = tmgr.prepareStatement(sql);
                int i = 1;
                ps.setString(i++, ownerDobj.getState_cd());
                ps.setInt(i++, ownerDobj.getOff_cd());
                ps.setString(i++, ownerDobj.getRegn_no());
                ps.setString(i++, dobj.getTf_Voucher_no());
                ps.setDate(i++, new java.sql.Date(dobj.getTf_VoucherDate().getTime()));
                ps.setString(i++, dobj.getTf_POP());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }
    }

    public void insertVhImportVeh(Owner_dobj ownerDobj, TransactionManager tmgr) throws VahanException {
        try {
            String sql = "Insert into " + TableList.VH_IMPORT_VEH + " select state_cd, off_cd, regn_no, contry_code, dealer, place, "
                    + "foreign_regno, manu_year,?,current_timestamp,? from " + TableList.VT_IMPORT_VEH + " where regn_no=? and state_cd= ? and off_cd= ? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDobj.getAppl_no());
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, ownerDobj.getRegn_no());
            ps.setString(4, ownerDobj.getState_cd());
            ps.setInt(5, ownerDobj.getOff_cd());
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }
    }

    public void updateVT_IMPORT_VEH(ImportedVehicleDobj dobj, Owner_dobj ownerDobj, TransactionManager tmgr) throws VahanException {

        try {

            insertVhImportVeh(ownerDobj, tmgr);

            String sql = "Delete from " + TableList.VT_IMPORT_VEH
                    + "    WHERE regn_no=? and state_cd= ? and off_cd= ?";

            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDobj.getRegn_no());
            ps.setString(2, ownerDobj.getState_cd());
            ps.setInt(3, ownerDobj.getOff_cd());
            ps.executeUpdate();
            if (dobj == null) {
                return;
            }
            sql = "INSERT INTO " + TableList.VT_IMPORT_VEH
                    + "( state_cd, off_cd, regn_no, contry_code, dealer, place, foreign_regno, manu_year, op_dt)"
                    + "    VALUES (?,?, ?, ?, ?, ?, ?, ? , current_timestamp) ";

            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, ownerDobj.getState_cd());
            ps.setInt(i++, ownerDobj.getOff_cd());
            ps.setString(i++, ownerDobj.getRegn_no());
            ps.setInt(i++, dobj.getCm_country_imp());
            ps.setString(i++, dobj.getTf_dealer_imp());
            ps.setString(i++, dobj.getTf_place_imp());
            ps.setString(i++, dobj.getTf_foreign_imp());
            ps.setInt(i++, dobj.getTf_YOM_imp());
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }
    }

    public void insertVhRetrofitinDtls(Owner_dobj ownerDobj, TransactionManager tmgr) throws VahanException {
        try {
            String sql = "INSERT INTO " + TableList.VH_RETROFITTING_DTLS
                    + "  select state_cd, off_cd,regn_no, kit_srno, kit_type, kit_manuf, kit_pucc_norms,workshop, workshop_lic_no,"
                    + " fitment_dt, hydro_test_dt, cyl_srno, approval_no, approval_dt, op_dt,?,current_timestamp,? "
                    + " from " + TableList.VT_RETROFITTING_DTLS + " where regn_no=? and state_cd= ? and off_cd= ? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDobj.getAppl_no());
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, ownerDobj.getRegn_no());
            ps.setString(4, ownerDobj.getState_cd());
            ps.setInt(5, ownerDobj.getOff_cd());
            ps.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }
    }

    public void insertUpdateVT_RETROFITTING_DTLS(RetroFittingDetailsDobj dobj, Owner_dobj ownerDobj, TransactionManager tmgr) throws VahanException {

        try {

            insertVhRetrofitinDtls(ownerDobj, tmgr);
            String sql = "Delete from " + TableList.VT_RETROFITTING_DTLS
                    + "    WHERE regn_no=? and state_cd= ? and off_cd= ? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDobj.getRegn_no());
            ps.setString(2, ownerDobj.getState_cd());
            ps.setInt(3, ownerDobj.getOff_cd());
            ps.executeUpdate();
            if (dobj == null) {
                return;
            }
            sql = "INSERT INTO " + TableList.VT_RETROFITTING_DTLS
                    + "  ( state_cd, off_cd,regn_no, kit_srno, kit_type, kit_manuf, kit_pucc_norms, workshop, workshop_lic_no, "
                    + "fitment_dt, hydro_test_dt, cyl_srno, approval_no, approval_dt, op_dt)"
                    + "    VALUES (?,?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";

            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, ownerDobj.getState_cd());
            ps.setInt(i++, ownerDobj.getOff_cd());
            ps.setString(i++, ownerDobj.getRegn_no());
            ps.setString(i++, dobj.getKit_srno());
            ps.setString(i++, dobj.getKit_type());
            ps.setString(i++, dobj.getKit_manuf());
            ps.setString(i++, dobj.getKit_pucc_norms());
            ps.setString(i++, dobj.getWorkshop());
            ps.setString(i++, dobj.getWorkshop_lic_no());

            if (dobj.getInstall_dt() == null) {
                ps.setNull(i++, java.sql.Types.NULL);
            } else {
                ps.setDate(i++, new java.sql.Date(dobj.getInstall_dt().getTime()));
            }

            if (dobj.getHydro_dt() == null) {
                ps.setNull(i++, java.sql.Types.NULL);
            } else {
                ps.setDate(i++, new java.sql.Date(dobj.getHydro_dt().getTime()));
            }

            ps.setString(i++, dobj.getCyl_srno());
            ps.setString(i++, dobj.getApproval_no());
            ps.setDate(i++, new java.sql.Date(dobj.getApproval_dt().getTime()));
            ps.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }
    }

    public void deleteInsertVT_SPEED_GOVERNER(SpeedGovernorDobj dobj, Owner_dobj ownerDobj, TransactionManager tmgr) throws VahanException {
        try {

            String sql = "Delete from " + TableList.VT_SPEED_GOVERNOR
                    + "    WHERE regn_no=? and state_cd= ? and off_cd= ? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDobj.getRegn_no());
            ps.setString(2, ownerDobj.getState_cd());
            ps.setInt(3, ownerDobj.getOff_cd());
            ps.executeUpdate();
            if (dobj == null) {
                return;
            }
            sql = " INSERT INTO " + TableList.VT_SPEED_GOVERNOR
                    + " (state_cd, off_cd, regn_no, sg_no, sg_fitted_on, sg_fitted_at,op_dt, emp_cd,"
                    + "sg_type,sg_type_approval_no,sg_test_report_no,sg_fitment_cert_no)"
                    + "   VALUES (?, ?, ?, ?, ?, ?,current_timestamp, ?,"
                    + "?,?,?,?) ";
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, ownerDobj.getState_cd());
            ps.setInt(i++, ownerDobj.getOff_cd());
            ps.setString(i++, ownerDobj.getRegn_no());
            ps.setString(i++, dobj.getSg_no());
            ps.setDate(i++, new java.sql.Date(dobj.getSg_fitted_on().getTime()));
            ps.setString(i++, dobj.getSg_fitted_at());
            ps.setString(i++, Util.getEmpCode());
            ps.setInt(i++, dobj.getSgGovType());
            ps.setString(i++, dobj.getSgTypeApprovalNo());
            ps.setString(i++, dobj.getSgTestReportNo());
            ps.setString(i++, dobj.getSgFitmentCerticateNo());
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }
    }

    public void insertDeleteVtReflectiveTape(ReflectiveTapeDobj dobj, Owner_dobj ownerDobj, TransactionManager tmgr) throws VahanException {
        try {

            FitnessImpl fitImpl = new FitnessImpl();
            fitImpl.moveFromVtReflectiveTapeToVhReflectiveTape(ownerDobj.getRegn_no(), ownerDobj.getState_cd(), tmgr);
            if (dobj == null) {
                return;
            }

            String sql = "INSERT INTO " + TableList.VT_REFLECTIVE_TAPE
                    + " (state_cd, off_cd, regn_no, certificate_no, fitment_date, manu_name,op_dt)"
                    + "    VALUES (?, ?, ?, ?, ?, ?,current_timestamp) ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, ownerDobj.getState_cd());
            ps.setInt(i++, ownerDobj.getOff_cd());
            ps.setString(i++, ownerDobj.getRegn_no());
            ps.setString(i++, dobj.getCertificateNo());
            ps.setDate(i++, new java.sql.Date(dobj.getFitmentDate().getTime()));
            ps.setString(i++, dobj.getManuName());
            ps.executeUpdate();
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }
    }

    public boolean saveChangesafterinward(RegistrationOwnerAdminDobj registrationOwnerAdminDobj, String empCd, String appl_No, String hpRmks) throws VahanException {
        boolean retStatus = false;
        TransactionManager tmgr = null;
        try {

            Owner_dobj dobj = registrationOwnerAdminDobj.getOwnerDobj();
            if (CommonUtils.isNullOrBlank(dobj.getRegn_type()) || "0".equals(dobj.getRegn_type())) {
                throw new VahanException("Registration Type can not be blank.");
            }
            String linkRegn = getLinkVehicleDtls(dobj.getState_cd(), dobj.getOff_cd(), dobj.getRegn_no());
            NewImpl newImpl = new NewImpl();
            int purCd = registrationOwnerAdminDobj.getRegVehType();
            String chassisNo = null;

            tmgr = new TransactionManager("saveChanges_after_inward");
            List<ComparisonBean> listChanges = registrationOwnerAdminDobj.getListChanges();
            String remarks = registrationOwnerAdminDobj.getRemarks();
            boolean isHypth = registrationOwnerAdminDobj.isIsHypth();
            SmartCardDobj smartCardDobj = registrationOwnerAdminDobj.getSmartCardDobj();
            String admin_rmk = registrationOwnerAdminDobj.getAdmin_Remarks();
            String changedata_byappl_no = registrationOwnerAdminDobj.getChangedata_appl_no();
            String applnoPrevTempRegn = registrationOwnerAdminDobj.getPrevTempApplno();
            TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();
            // Add For Temporary Rgn
            if (dobj.isCheckVaownerTempFlag() && purCd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG && !applnoPrevTempRegn.equals("")) {
                saveAndApproveTmpRegn(tmgr, dobj, applnoPrevTempRegn, TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE, isHypth);
                dobj.getDob_temp().setAppl_no(applnoPrevTempRegn.trim());
            }  //end Tempory

            if (purCd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) {
                chassisNo = getExistChassisNoInVahanBacklog(dobj.getState_cd(), dobj.getRegn_no(), dobj.getOff_cd());
                if (!CommonUtils.isNullOrBlank(chassisNo) && !chassisNo.equalsIgnoreCase(dobj.getChasi_no())) {
                    String sql = "update vahanbacklog.vb_owner_hist  set chasi_no=? where regn_no=? and chasi_no=? ";
                    PreparedStatement ps = tmgr.prepareStatement(sql);
                    ps.setString(1, dobj.getChasi_no());
                    ps.setString(2, dobj.getRegn_no());
                    ps.setString(3, chassisNo);
                    ps.executeUpdate();
                }

                updateVt_owner(dobj, tmgr);
                //Vt_Trailer 
                if (dobj.getTrailerDobj() != null && dobj.getTrailerDobj().getChasi_no() != null && !dobj.getTrailerDobj().getChasi_no().trim().isEmpty()) {
                    Trailer_Impl.movedataapprovalTrailer(tmgr, dobj.getRegn_no(), appl_No, dobj.getOff_cd());
                }
            }
            if (purCd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
                if (!applnoPrevTempRegn.equals("")) {
                    dobj.getDob_temp().setAppl_no(applnoPrevTempRegn.trim());
                    updateVtOwnerTemp(dobj, tmgr);
                    NewImpl.insertIntoVhaOwnerTemp(tmgr, appl_No);
                    NewImpl.updateVaOwnerTemp(tmgr, dobj.getDob_temp());
                    if (!CommonUtils.isNullOrBlank(dobj.getLinkedRegnNo())) {
                        dobj.setAppl_no(applnoPrevTempRegn.trim());
                        NewImpl.insertOrUpdateVaSideTrailer(tmgr, dobj);
                    }
                }
            }
            dobj.setAppl_no(appl_No);
            if (dobj.getListHpaDobj() != null && !dobj.getListHpaDobj().isEmpty()) {
                updateVT_HYPTH(dobj.getListHpaDobj().get(0), dobj, tmgr, isHypth);
            } else if (dobj.getListHpaDobj() == null && !isHypth) {
                HpaDobj hpaDobj = new HpaDobj();
                updateVT_HYPTH(hpaDobj, dobj, tmgr, isHypth);
            }
            updateVt_ownerIdentification(dobj.getOwner_identity(), dobj, tmgr);
            if (dobj.getInsDobj() != null && !dobj.getInsDobj().isIibData()) {
                updateVT_Ins(dobj.getInsDobj(), dobj, tmgr);
            }
            updateVtOtherStateVeh(dobj.getOtherStateVehDobj(), dobj, tmgr);
            updateVtTempRegnDetails(dobj.getTempReg(), dobj, tmgr);
            insertUpdateVtAxel(dobj.getAxleDobj(), dobj, tmgr);
            updateVT_OWNER_EX_ARMY(dobj.getExArmy_dobj(), dobj, tmgr);
            updateVT_IMPORT_VEH(dobj.getImp_Dobj(), dobj, tmgr);
            insertUpdateVT_RETROFITTING_DTLS(dobj.getCng_dobj(), dobj, tmgr);
            deleteInsertVT_SPEED_GOVERNER(dobj.getSpeedGovernorDobj(), dobj, tmgr);
            insertDeleteVtReflectiveTape(dobj.getReflectiveTapeDobj(), dobj, tmgr);
            //sideTrailerApproval
            if (!CommonUtils.isNullOrBlank(dobj.getLinkedRegnNo()) && purCd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) {
                updateSideTrailer(tmgr, dobj, linkRegn);//
            }
            NewImpl.insertIntoVhaSideTrailer(tmgr, appl_No);
            ServerUtil.deleteFromTable(tmgr, null, appl_No, TableList.VA_SIDE_TRAILER);

            if (dobj.getFitnessDobj() != null) {
                dobj.getFitnessDobj().setAppl_no(appl_No);
                updateVTFitness(dobj.getFitnessDobj(), tmgr);
            }
            //Delete Va_Trailer
            Trailer_Impl.insertIntoTrailerHistory(tmgr, appl_No);
            Trailer_Impl.deleteFromVaTrailer(tmgr, dobj.getRegn_no(), appl_No);

            if (Util.getUserStateCode().equals("GA") && (Util.getUserCategory().equals(TableConstants.USER_CATG_STATE_ADMIN)) && purCd != TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
                saveAdminRemarkDetails(dobj, admin_rmk, tmgr);
            }
            // if there is any change in Vehicle Technical Parameters
            if (dobj != null && purCd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) {
                boolean isFastTagSchd = false;
                int i = 1;
                String sql = "select a.appl_no a,b.appl_no b from (Select appl_no from vh_owner where state_cd=? and off_cd=? and regn_no=? and appl_no=? ) a "
                        + "  left outer join (Select appl_no from vh_owner where state_cd=? and off_cd=? and regn_no=? and appl_no=? "
                        + "                   and chasi_no=? and  eng_no=? and  maker=? and  maker_model=? and manu_mon=? and  manu_yr=? "
                        + "                   and body_type=? and color=? and cubic_cap=? and hp=? and   fuel=? and  unld_wt=? and  ld_wt=? and "
                        + "                   wheelbase=? and height=? and width=? and length=? and norms=? "
                        + "                 ) b on a.appl_no=b.appl_no ";
                PreparedStatement ps = tmgr.prepareStatement(sql);
                ps.setString(i++, dobj.getState_cd());
                ps.setInt(i++, dobj.getOff_cd());
                ps.setString(i++, dobj.getRegn_no());
                ps.setString(i++, appl_No);
                ps.setString(i++, dobj.getState_cd());
                ps.setInt(i++, dobj.getOff_cd());
                ps.setString(i++, dobj.getRegn_no());
                ps.setString(i++, appl_No);
                ps.setString(i++, dobj.getChasi_no());
                ps.setString(i++, dobj.getEng_no());
                ps.setInt(i++, dobj.getMaker());
                ps.setString(i++, dobj.getMaker_model());
                ps.setInt(i++, dobj.getManu_mon());
                ps.setInt(i++, dobj.getManu_yr());
                ps.setString(i++, dobj.getBody_type());
                ps.setString(i++, dobj.getColor());
                ps.setDouble(i++, dobj.getCubic_cap());
                ps.setDouble(i++, dobj.getHp());
                ps.setInt(i++, dobj.getFuel());
                ps.setInt(i++, dobj.getUnld_wt());
                ps.setInt(i++, dobj.getLd_wt());
                ps.setInt(i++, dobj.getWheelbase());
                ps.setInt(i++, dobj.getHeight());
                ps.setInt(i++, dobj.getWidth());
                ps.setInt(i++, dobj.getLength());
                ps.setInt(i++, dobj.getNorms());
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    if (!CommonUtils.isNullOrBlank(rs.getString("a")) && CommonUtils.isNullOrBlank(rs.getString("b"))) {
                        isFastTagSchd = true;
                    }

                }

                i = 1;
                sql = "select a.regn_no a,b.regn_no b from (Select regn_no from vh_axle where state_cd=? and off_cd=? and regn_no=? and appl_no=? ) a"
                        + " ,(Select regn_no from vt_axle where state_cd=? and off_cd=? and regn_no=? ) b ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(i++, dobj.getState_cd());
                ps.setInt(i++, dobj.getOff_cd());
                ps.setString(i++, dobj.getRegn_no());
                ps.setString(i++, appl_No);
                ps.setString(i++, dobj.getState_cd());
                ps.setInt(i++, dobj.getOff_cd());
                ps.setString(i++, dobj.getRegn_no());
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    if (!CommonUtils.isNullOrBlank(rs.getString("a")) || CommonUtils.isNullOrBlank(rs.getString("b"))) {
                        isFastTagSchd = true;
                    }

                }

                if (isFastTagSchd) {
                    OwnerImpl.insertUpdateFastagSchedular(appl_No, dobj.getState_cd(), dobj.getOff_cd(), dobj.getRegn_no(),
                            dobj.getState_cd(), dobj.getOff_cd(), dobj.getRegn_no(), dobj.getChasi_no(), tmgr);
                }

            }

            String changedData = ComparisonBeanImpl.changedDataContents(listChanges);
            if (!CommonUtils.isNullOrBlank(hpRmks)) {
                changedData = changedData + hpRmks + "|";
            }
            changedData = changedData + changedata_byappl_no + "|";
            if (!remarks.equals("")) {
                changedData = changedData + "[AdminRemarks-" + remarks + "]";
            } else {
                changedData = changedData.substring(0, changedData.lastIndexOf("|"));
            }
            if (smartCardDobj != null) {
                changedData = "SMART-CARD:[Owner Details Updated before SmartCard Activation] " + changedData;
            }
            insertIntoVhaChangedData(tmgr, dobj.getRegn_no(), changedData);
            if (smartCardDobj != null) {
                smartCardDobj.setReason(remarks);
                SmartCardImpl smartCardImpl = new SmartCardImpl();
                smartCardImpl.updateSmartCardPendingDetails(tmgr, dobj, smartCardDobj, empCd, tmConfigurationDobj);
            }
            if (Util.getUserStateCode().equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(dobj.getVh_class()) + ",")) {
                newImpl.ownerOtherApproval(tmgr, dobj);
            }
            ServerUtil.deleteFromTable(tmgr, null, appl_No, TableList.VA_OWNER_OTHER);
            ///va_owner bk
            NewImpl.insertIntoVhaOwnerWithTimeInterval(tmgr, appl_No);
            ServerUtil.deleteFromTable(tmgr, null, appl_No, TableList.VA_OWNER);
            //VA_HPA
            if (dobj.getListHpaDobj() != null && !dobj.getListHpaDobj().isEmpty()) {
                HpaImpl.insertDeleteFromVaHpa(tmgr, appl_No);

            }
            /// VH_Owner Identification
            ServerUtil.deleteFromTable(tmgr, null, appl_No, TableList.VA_OWNER_IDENTIFICATION);
            //VA_Insurance
            insertIntoInsuranceHistory(tmgr, appl_No, null);
            ServerUtil.deleteFromTable(tmgr, null, appl_No, TableList.VA_INSURANCE);

            //VA_OTHER_STATE_VEH
            OtherStateVehImpl othervehicle = new OtherStateVehImpl();//VA_OTHER_STATE_VEH
            othervehicle.insertIntoOtherStateVehHistory(tmgr, appl_No);
            ServerUtil.deleteFromTable(tmgr, null, appl_No, TableList.VA_OTHER_STATE_VEH);

            //VA_TMP_REGN_DTL
            //  NewImpl.insertIntoTempRegnDetails(tmgr, dobj.getAppl_no());
            ServerUtil.deleteFromTable(tmgr, null, appl_No, TableList.VA_TMP_REGN_DTL);

            //VA_AXLE
            AxleImpl.insertIntoVhaAxle(tmgr, appl_No);
            ServerUtil.deleteFromTable(tmgr, null, appl_No, TableList.VA_AXLE);

            //VA_OWNER_EX_ARMY
            ExArmyImpl.insertIntoVhaExArmy(tmgr, appl_No);
            ServerUtil.deleteFromTable(tmgr, null, appl_No, TableList.VA_OWNER_EX_ARMY);

            //VA_IMPORT_VEH
            ImportedVehicleImpl.insertIntoVhaImpVeh(tmgr, appl_No);
            ServerUtil.deleteFromTable(tmgr, null, appl_No, TableList.VA_IMPORT_VEH);

            //VA_RETROFITTING_DTLS
            RetroFittingDetailsImpl.insertIntoVhaCng(tmgr, appl_No);
            ServerUtil.deleteFromTable(tmgr, null, appl_No, TableList.VA_RETROFITTING_DTLS);

            //VA_SPEED_GOVERNOR
            FitnessImpl.insertIntoVhaSpeedGovernor(appl_No, tmgr);
            ServerUtil.deleteFromTable(tmgr, null, appl_No, TableList.VA_SPEED_GOVERNOR);

            //VA_REFLECTIVE_TAPE
            FitnessImpl fitnessImpl = new FitnessImpl();
            fitnessImpl.insertIntoVhaReflectiveTape(tmgr, appl_No, Util.getEmpCode());
            ServerUtil.deleteFromTable(tmgr, null, appl_No, TableList.VA_REFLECTIVE_TAPE);

            ServerUtil.deleteFromTable(tmgr, null, appl_No, TableList.VA_OWNER_TEMP);
            ///Delete va_owner Admin Temp
            ServerUtil.deleteFromTable(tmgr, null, appl_No, TableList.VA_OWNER_TEMP_ADMIN);

            Status_dobj status_dobj = registrationOwnerAdminDobj.getStatus_dobj();
            status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
            ServerUtil.updateApprovedStatus(tmgr, status_dobj);
            ServerUtil.fileFlow(tmgr, status_dobj);
            String forNewLine = "<br/>";
            String subject = "Admin Modification of Owner Details for Vehicle No " + dobj.getRegn_no() + ".";
            changedData = changedData.replace("]|[", forNewLine);
            changedData = changedData.replace("[", forNewLine);
            changedData = changedData.replace("]", forNewLine);
            String dataModification = subject + forNewLine + " As " + changedData + " Modification Approved By [" + ServerUtil.getUserName(Long.parseLong(Util.getEmpCode())) + "( " + Util.getUserId() + " ) ] at Office [ " + ServerUtil.getOfficeName(Util.getUserOffCode(), Util.getUserStateCode()) + " ]";
            sendMailChangeData(tmgr, Util.getUserStateCode(), "S", null, subject, dataModification);//Change data send to State Administrator
            sendMailChangeData(tmgr, Util.getUserStateCode(), "A", Util.getUserOffCode(), subject, dataModification);//Change data send to Office Administrator
            tmgr.commit();
            retStatus = true;
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return retStatus;
    }

    public static void saveAdminRemarkDetails(Owner_dobj dobj, String admin_rmk, TransactionManager tmgr) throws SQLException, VahanException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "SELECT remarks FROM " + TableList.VT_OWNER_REMARKS + " where regn_no = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getRegn_no());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();

        if (rs.next()) { //if any record is exist then update it otherwise insert it
            insertIntoVhOwnerRemarks(tmgr, admin_rmk, dobj);
            updateVtOwnerRemarks(tmgr, admin_rmk, dobj);
        } else {
            insertIntoVtOwnerRemarks(tmgr, admin_rmk, dobj);
        }
    }

    public static void insertIntoVhOwnerRemarks(TransactionManager tmgr, String admin_rmk, Owner_dobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        sql = "INSERT INTO " + TableList.VH_OWNER_REMARKS
                + " (state_cd, off_cd, regn_no, remarks, op_dt, moved_on, moved_by )"
                + " SELECT state_cd, off_cd, regn_no, remarks, op_dt, current_timestamp as moved_on, ? as moved_by"
                + "  FROM " + TableList.VT_OWNER_REMARKS + " where  state_cd=? and off_cd=? and regn_no=?";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, dobj.getState_cd());
        ps.setInt(3, dobj.getOff_cd());
        ps.setString(4, dobj.getRegn_no());
        ps.executeUpdate();
    }

    private static void updateVtOwnerRemarks(TransactionManager tmgr, String admin_rmk, Owner_dobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        sql = "UPDATE " + TableList.VT_OWNER_REMARKS + " \n"
                + "SET  remarks=?,op_dt=current_timestamp "
                + "WHERE state_cd=? and off_cd=? and regn_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, admin_rmk);
        ps.setString(2, dobj.getState_cd());
        ps.setInt(3, dobj.getOff_cd());
        ps.setString(4, dobj.getRegn_no());
        ps.executeUpdate();
    }

    public static void insertIntoVtOwnerRemarks(TransactionManager tmgr, String admin_rmk, Owner_dobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        sql = "INSERT INTO " + TableList.VT_OWNER_REMARKS + " (\n"
                + "state_cd, off_cd, regn_no, remarks, op_dt)\n"
                + "VALUES (?, ?, ?, ?, current_timestamp);";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getState_cd());
        ps.setInt(2, dobj.getOff_cd());
        ps.setString(3, dobj.getRegn_no());
        ps.setString(4, admin_rmk);
        ps.executeUpdate();
    }

    public String getRemarks(String regno, String state_cd, int off_cd) throws VahanException {
        String sql = null;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        String regNo = "";
        String remarks = "";

        try {
            tmgr = new TransactionManagerReadOnly("getRemarks");

            sql = "SELECT * FROM " + TableList.VT_OWNER_REMARKS + " WHERE regn_no =? and state_cd= ? and off_cd= ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regno);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                remarks = rs.getString("remarks");
            }
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
        return remarks;
    }

    public void insertOrupdateVaTempRegn(TransactionManager tmgr, TempRegDobj dobj) throws SQLException {
        PreparedStatement ps = null;

        String sql = "SELECT * FROM " + TableList.VA_TMP_REGN_DTL + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getAppl_no());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            NewImpl.insertIntoTempRegnDetails(tmgr, dobj.getAppl_no());
            NewImpl.updateVaTempRegnDetails(tmgr, dobj);
        } else {
            NewImpl.insertVaTempRegnDetails(tmgr, dobj);
        }
    }

    public void insertOrUpdateFor_OwnerAdmin(TransactionManager tmgr, Owner_dobj owner_dobj, int pur_cd) throws VahanException, SQLException {

        String str = "Select * from " + TableList.VA_OWNER
                + " where appl_no=?";
        if (pur_cd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
            str = "Select * from " + TableList.VA_OWNER_TEMP_ADMIN
                    + " where appl_no=?";
        }
        PreparedStatement ps = tmgr.prepareStatement(str);
        ps.setString(1, owner_dobj.getAppl_no());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            insertIntoVhaOwnerAdmin(tmgr, owner_dobj.getAppl_no(), pur_cd);
            updateVaOwnerAdmin(tmgr, owner_dobj, pur_cd);
        } else {
            insertVaOwnerAdmin(tmgr, owner_dobj, pur_cd);
        }
        if (!CommonUtils.isNullOrBlank(owner_dobj.getLinkedRegnNo())) {
            NewImpl.insertOrUpdateVaSideTrailer(tmgr, owner_dobj);
        }
    }

    public static void updateVaOwnerAdmin(TransactionManager tmgr, Owner_dobj owner_dobj, int pur_cd) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            /**
             * For other state vehicle/other district Regn Dt will previous regn
             * date
             */
            if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) {
                sql = "UPDATE " + TableList.VA_OWNER
                        + "   SET appl_no=?,"
                        //                        + "       regn_dt=?,"
                        + "  purchase_dt=?, "
                        + "       owner_sr=?, owner_name=?, f_name=?, c_add1=?, c_add2=?, c_add3=?, "
                        + "       c_district=?, c_pincode=?, c_state=?, p_add1=?, p_add2=?, p_add3=?, "
                        + "       p_district=?, p_pincode=?, p_state=?, owner_cd=?, regn_type=?, "
                        + "       vh_class=?, chasi_no=?, eng_no=?, maker=?, maker_model=?, body_type=?, "
                        + "       no_cyl=?, hp=?, seat_cap=?, stand_cap=?, sleeper_cap=?, unld_wt=?, "
                        + "       ld_wt=?, gcw=?, fuel=?, color=?, manu_mon=?, manu_yr=?, norms=?, "
                        + "       wheelbase=?, cubic_cap=?, floor_area=?, ac_fitted=?, audio_fitted=?, "
                        + "       video_fitted=?, vch_purchase_as=?, vch_catg=?, dealer_cd=?, sale_amt=?, "
                        + "       laser_code=?, garage_add=?, length=?, width=?, height=?, regn_upto=?, "
                        + "       fit_upto=?, annual_income=?, imported_vch=?, other_criteria=?,"
                        + "       pmt_type=?,pmt_catg=?,rqrd_tax_modes=?,op_dt=current_timestamp"
                        + " WHERE appl_no=?";
            } else if (pur_cd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
                sql = "UPDATE " + TableList.VA_OWNER_TEMP_ADMIN
                        + "   SET appl_no=?,"
                        //                        + "       regn_dt=?,"
                        + "  purchase_dt=?, "
                        + "       owner_sr=?, owner_name=?, f_name=?, c_add1=?, c_add2=?, c_add3=?, "
                        + "       c_district=?, c_pincode=?, c_state=?, p_add1=?, p_add2=?, p_add3=?, "
                        + "       p_district=?, p_pincode=?, p_state=?, owner_cd=?, regn_type=?, "
                        + "       vh_class=?, chasi_no=?, eng_no=?, maker=?, maker_model=?, body_type=?, "
                        + "       no_cyl=?, hp=?, seat_cap=?, stand_cap=?, sleeper_cap=?, unld_wt=?, "
                        + "       ld_wt=?, gcw=?, fuel=?, color=?, manu_mon=?, manu_yr=?, norms=?, "
                        + "       wheelbase=?, cubic_cap=?, floor_area=?, ac_fitted=?, audio_fitted=?, "
                        + "       video_fitted=?, vch_purchase_as=?, vch_catg=?, dealer_cd=?, sale_amt=?, "
                        + "       laser_code=?, garage_add=?, length=?, width=?, height=?, regn_upto=?, "
                        + "       fit_upto=?, annual_income=?, imported_vch=?, other_criteria=?,"
                        + "       pmt_type=?,pmt_catg=?,rqrd_tax_modes=?,op_dt=current_timestamp"
                        + " WHERE appl_no=?";
            }

            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, owner_dobj.getAppl_no());
            // ps.setDate(i++, new java.sql.Date(owner_dobj.getRegn_dt().getTime()));
            ps.setDate(i++, owner_dobj.getPurchase_dt() == null ? null : new java.sql.Date(owner_dobj.getPurchase_dt().getTime()));
            ps.setInt(i++, owner_dobj.getOwner_sr());
            ps.setString(i++, owner_dobj.getOwner_name());
            ps.setString(i++, owner_dobj.getF_name());
            ps.setString(i++, owner_dobj.getC_add1());
            ps.setString(i++, owner_dobj.getC_add2());
            ps.setString(i++, owner_dobj.getC_add3());
            ps.setInt(i++, owner_dobj.getC_district());
            ps.setInt(i++, owner_dobj.getC_pincode());
            ps.setString(i++, owner_dobj.getC_state());
            ps.setString(i++, owner_dobj.getP_add1());
            ps.setString(i++, owner_dobj.getP_add2());
            ps.setString(i++, owner_dobj.getP_add3());
            ps.setInt(i++, owner_dobj.getP_district());
            ps.setInt(i++, owner_dobj.getP_pincode());
            ps.setString(i++, owner_dobj.getP_state());
            ps.setInt(i++, owner_dobj.getOwner_cd());
            ps.setString(i++, owner_dobj.getRegn_type());
            ps.setInt(i++, owner_dobj.getVh_class());
            ps.setString(i++, owner_dobj.getChasi_no());
            ps.setString(i++, owner_dobj.getEng_no());
            ps.setInt(i++, owner_dobj.getMaker());
            ps.setString(i++, owner_dobj.getMaker_model());
            ps.setString(i++, owner_dobj.getBody_type());
            ps.setInt(i++, owner_dobj.getNo_cyl());
            ps.setFloat(i++, owner_dobj.getHp());
            ps.setInt(i++, owner_dobj.getSeat_cap());
            ps.setInt(i++, owner_dobj.getStand_cap());
            ps.setInt(i++, owner_dobj.getSleeper_cap());
            ps.setInt(i++, owner_dobj.getUnld_wt());
            ps.setInt(i++, owner_dobj.getLd_wt());
            ps.setInt(i++, owner_dobj.getGcw());
            ps.setInt(i++, owner_dobj.getFuel());
            ps.setString(i++, owner_dobj.getColor());
            if (owner_dobj.getManu_mon() != null) {
                ps.setInt(i++, owner_dobj.getManu_mon());
            } else {
                ps.setInt(i++, 0);
            }
            if (owner_dobj.getManu_yr() != null) {
                ps.setInt(i++, owner_dobj.getManu_yr());
            } else {
                ps.setInt(i++, 0);
            }
            ps.setInt(i++, owner_dobj.getNorms());
            ps.setInt(i++, owner_dobj.getWheelbase());
            ps.setFloat(i++, owner_dobj.getCubic_cap());
            ps.setFloat(i++, owner_dobj.getFloor_area());
            ps.setString(i++, owner_dobj.getAc_fitted());
            ps.setString(i++, owner_dobj.getAudio_fitted());
            ps.setString(i++, owner_dobj.getVideo_fitted());
            ps.setString(i++, owner_dobj.getVch_purchase_as());
            ps.setString(i++, owner_dobj.getVch_catg());
            ps.setString(i++, owner_dobj.getDealer_cd());
            ps.setInt(i++, owner_dobj.getSale_amt());
            ps.setString(i++, owner_dobj.getLaser_code());
            ps.setString(i++, owner_dobj.getGarage_add());
            ps.setInt(i++, owner_dobj.getLength());
            ps.setInt(i++, owner_dobj.getWidth());
            ps.setInt(i++, owner_dobj.getHeight());
            ps.setDate(i++, owner_dobj.getRegn_upto() == null ? null : new java.sql.Date(owner_dobj.getRegn_upto().getTime()));
            ps.setDate(i++, owner_dobj.getFit_upto() == null ? null : new java.sql.Date(owner_dobj.getFit_upto().getTime()));
            ps.setInt(i++, owner_dobj.getAnnual_income());
            ps.setString(i++, owner_dobj.getImported_vch());
            ps.setInt(i++, owner_dobj.getOther_criteria());
            ps.setInt(i++, owner_dobj.getPmt_type());
            ps.setInt(i++, owner_dobj.getPmt_catg());
            ps.setString(i++, owner_dobj.getRqrd_tax_modes());
            ps.setString(i++, owner_dobj.getAppl_no());

            ps.executeUpdate();

            if (owner_dobj.getDob_temp() != null) {
                owner_dobj.getDob_temp().setAppl_no(owner_dobj.getAppl_no());
                NewImpl.updateVaOwnerTemp(tmgr, owner_dobj.getDob_temp());
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }
    }//end of updateVaOwner

    public void insertVaOwnerAdmin(TransactionManager tmgr, Owner_dobj owner_dobj, int pur_cd) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            if (owner_dobj == null) {
                return;
            }
            if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) {
                sql = "select * from  " + TableList.VA_OWNER + " owner "
                        + " where owner.chasi_no=? ";
                PreparedStatement pmt = tmgr.prepareStatement(sql);
                pmt.setString(1, owner_dobj.getChasi_no());
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    throw new VahanException("Application No. " + rs.getString("appl_no") + " already pending for Chassis No :" + owner_dobj.getChasi_no() + ".");
                }

                sql = "INSERT INTO " + TableList.VA_OWNER
                        + " (  state_cd, off_cd, appl_no, regn_no, regn_dt, purchase_dt, owner_sr, "
                        + "     owner_name, f_name, c_add1, c_add2, c_add3, c_district, c_pincode, "
                        + "     c_state, p_add1, p_add2, p_add3, p_district, p_pincode, p_state, "
                        + "     owner_cd, regn_type, vh_class, chasi_no, eng_no, maker, maker_model, "
                        + "     body_type, no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, "
                        + "     ld_wt, gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, "
                        + "     cubic_cap, floor_area, ac_fitted, audio_fitted, video_fitted, "
                        + "     vch_purchase_as, vch_catg, dealer_cd, sale_amt, laser_code, garage_add, "
                        + "     length, width, height, regn_upto, fit_upto, annual_income, imported_vch, "
                        + "     other_criteria,pmt_type,pmt_catg,rqrd_tax_modes,op_dt)"
                        + "    VALUES (?, ?, ?, ?, ?, ?, ?, "
                        + "            ?, ?, ?, ?, ?, ?, ?, "
                        + "            ?, ?, ?, ?, ?, ?, ?, "
                        + "            ?, ?, ?, ?, ?, ?, ?, "
                        + "            ?, ?, ?, ?, ?, ?, ?, "
                        + "            ?, ?, ?, ?, ?, ?, ?, ?, "
                        + "            ?, ?, ?, ?, ?, "
                        + "            ?, ?, ?, ?, ?, ?, "
                        + "            ?, ?, ?, ?, ?, ?, ?, "
                        + "            ?,?,?,?, current_timestamp)";
            } else if (pur_cd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
                sql = "select * from  " + TableList.VA_OWNER_TEMP_ADMIN + " owner "
                        + " where owner.chasi_no=? ";
                PreparedStatement pmt = tmgr.prepareStatement(sql);
                pmt.setString(1, owner_dobj.getChasi_no());
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    throw new VahanException("Application No. " + rs.getString("appl_no") + " already pending for Chassis No :" + owner_dobj.getChasi_no() + ".");
                }
                sql = "INSERT INTO " + TableList.VA_OWNER_TEMP_ADMIN
                        + " (  state_cd, off_cd, appl_no, regn_no, regn_dt, purchase_dt, owner_sr, "
                        + "     owner_name, f_name, c_add1, c_add2, c_add3, c_district, c_pincode, "
                        + "     c_state, p_add1, p_add2, p_add3, p_district, p_pincode, p_state, "
                        + "     owner_cd, regn_type, vh_class, chasi_no, eng_no, maker, maker_model, "
                        + "     body_type, no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, "
                        + "     ld_wt, gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, "
                        + "     cubic_cap, floor_area, ac_fitted, audio_fitted, video_fitted, "
                        + "     vch_purchase_as, vch_catg, dealer_cd, sale_amt, laser_code, garage_add, "
                        + "     length, width, height, regn_upto, fit_upto, annual_income, imported_vch, "
                        + "     other_criteria,pmt_type,pmt_catg,rqrd_tax_modes,op_dt)"
                        + "    VALUES (?, ?, ?, ?, ?, ?, ?, "
                        + "            ?, ?, ?, ?, ?, ?, ?, "
                        + "            ?, ?, ?, ?, ?, ?, ?, "
                        + "            ?, ?, ?, ?, ?, ?, ?, "
                        + "            ?, ?, ?, ?, ?, ?, ?, "
                        + "            ?, ?, ?, ?, ?, ?, ?, ?, "
                        + "            ?, ?, ?, ?, ?, "
                        + "            ?, ?, ?, ?, ?, ?, "
                        + "            ?, ?, ?, ?, ?, ?, ?, "
                        + "            ?,?,?,?, current_timestamp)";
            }
            ps = tmgr.prepareStatement(sql);

            int i = 1;

            ps.setString(i++, owner_dobj.getState_cd());
            ps.setInt(i++, owner_dobj.getOff_cd());
            ps.setString(i++, owner_dobj.getAppl_no());
            ps.setString(i++, owner_dobj.getRegn_no());
            ps.setDate(i++, owner_dobj.getRegn_dt() == null ? null : new java.sql.Date(owner_dobj.getRegn_dt().getTime()));
            ps.setDate(i++, owner_dobj.getPurchase_dt() == null ? null : new java.sql.Date(owner_dobj.getPurchase_dt().getTime()));
            ps.setInt(i++, owner_dobj.getOwner_sr());
            ps.setString(i++, owner_dobj.getOwner_name());
            ps.setString(i++, owner_dobj.getF_name());
            ps.setString(i++, owner_dobj.getC_add1());
            ps.setString(i++, owner_dobj.getC_add2());
            ps.setString(i++, owner_dobj.getC_add3());
            ps.setInt(i++, owner_dobj.getC_district());
            ps.setInt(i++, owner_dobj.getC_pincode());
            ps.setString(i++, owner_dobj.getC_state());
            ps.setString(i++, owner_dobj.getP_add1());
            ps.setString(i++, owner_dobj.getP_add2());
            ps.setString(i++, owner_dobj.getP_add3());
            ps.setInt(i++, owner_dobj.getP_district());
            ps.setInt(i++, owner_dobj.getP_pincode());
            ps.setString(i++, owner_dobj.getP_state());
            ps.setInt(i++, owner_dobj.getOwner_cd());
            ps.setString(i++, owner_dobj.getRegn_type());
            ps.setInt(i++, owner_dobj.getVh_class());
            ps.setString(i++, owner_dobj.getChasi_no() == null ? TableConstants.EMPTY_STRING : owner_dobj.getChasi_no());
            ps.setString(i++, owner_dobj.getEng_no() == null ? TableConstants.EMPTY_STRING : owner_dobj.getEng_no());
            ps.setInt(i++, owner_dobj.getMaker());
            ps.setString(i++, owner_dobj.getMaker_model() == null ? TableConstants.EMPTY_STRING : owner_dobj.getMaker_model());
            ps.setString(i++, owner_dobj.getBody_type() == null ? TableConstants.EMPTY_STRING : owner_dobj.getBody_type());
            ps.setInt(i++, owner_dobj.getNo_cyl());

            if (owner_dobj.getHp() != null) {
                ps.setFloat(i++, owner_dobj.getHp());
            } else {
                ps.setNull(i++, java.sql.Types.FLOAT);
            }

            ps.setInt(i++, owner_dobj.getSeat_cap());
            ps.setInt(i++, owner_dobj.getStand_cap());
            ps.setInt(i++, owner_dobj.getSleeper_cap());
            ps.setInt(i++, owner_dobj.getUnld_wt());
            ps.setInt(i++, owner_dobj.getLd_wt());
            ps.setInt(i++, owner_dobj.getGcw());
            ps.setInt(i++, owner_dobj.getFuel());
            ps.setString(i++, owner_dobj.getColor() == null ? TableConstants.EMPTY_STRING : owner_dobj.getColor());
            ps.setInt(i++, owner_dobj.getManu_mon() == null ? 0 : owner_dobj.getManu_mon());
            ps.setInt(i++, owner_dobj.getManu_yr() == null ? 0 : owner_dobj.getManu_yr());
            ps.setInt(i++, owner_dobj.getNorms());
            ps.setInt(i++, owner_dobj.getWheelbase());
            ps.setFloat(i++, owner_dobj.getCubic_cap());
            ps.setFloat(i++, owner_dobj.getFloor_area());
            ps.setString(i++, owner_dobj.getAc_fitted());
            ps.setString(i++, owner_dobj.getAudio_fitted());
            ps.setString(i++, owner_dobj.getVideo_fitted());
            ps.setString(i++, owner_dobj.getVch_purchase_as() == null ? TableConstants.EMPTY_STRING : owner_dobj.getVch_purchase_as());
            ps.setString(i++, owner_dobj.getVch_catg());
            ps.setString(i++, owner_dobj.getDealer_cd() == null ? TableConstants.EMPTY_STRING : owner_dobj.getDealer_cd());
            ps.setInt(i++, owner_dobj.getSale_amt());
            ps.setString(i++, owner_dobj.getLaser_code() == null ? TableConstants.EMPTY_STRING : owner_dobj.getLaser_code());
            ps.setString(i++, owner_dobj.getGarage_add() == null ? TableConstants.EMPTY_STRING : owner_dobj.getGarage_add());
            ps.setInt(i++, owner_dobj.getLength());
            ps.setInt(i++, owner_dobj.getWidth());
            ps.setInt(i++, owner_dobj.getHeight());

            ps.setDate(i++, owner_dobj.getRegn_upto() == null ? null : new java.sql.Date(owner_dobj.getRegn_upto().getTime()));
            ps.setDate(i++, owner_dobj.getFit_upto() == null ? null : new java.sql.Date(owner_dobj.getFit_upto().getTime()));
            ps.setInt(i++, owner_dobj.getAnnual_income());
            ps.setString(i++, owner_dobj.getImported_vch() == null ? TableConstants.EMPTY_STRING : owner_dobj.getImported_vch());
            ps.setInt(i++, owner_dobj.getOther_criteria());
            ps.setInt(i++, owner_dobj.getPmt_type());
            ps.setInt(i++, owner_dobj.getPmt_catg());
            ps.setString(i++, owner_dobj.getRqrd_tax_modes() == null ? TableConstants.EMPTY_STRING : owner_dobj.getRqrd_tax_modes());
            ps.executeUpdate();

            //Add Va_owner_Temp
            if (owner_dobj.getDob_temp() != null) {
                if (owner_dobj.getDob_temp().getState_cd_to() == null) {
                    throw new VahanException("State to & Office to is not selected for Temporary Registration.");
                }
                owner_dobj.getDob_temp().setAppl_no(owner_dobj.getAppl_no());
                NewImpl.insertVaOwnerTemp(tmgr, owner_dobj.getDob_temp());
            }
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }

    }//end of insertVaOwner

    public void insertIntoVhaOwnerAdmin(TransactionManager tmgr, String appl_no, int pur_cd) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;

        try {
            if (pur_cd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
                sql = "INSERT INTO  " + TableList.VHA_OWNER
                        + " SELECT current_timestamp as moved_on , ? as moved_by, state_cd, off_cd, appl_no, regn_no, regn_dt, purchase_dt, owner_sr, "
                        + "        owner_name, f_name, c_add1, c_add2, c_add3, c_district, c_pincode, "
                        + "        c_state, p_add1, p_add2, p_add3, p_district, p_pincode, p_state, "
                        + "        owner_cd, regn_type, vh_class, chasi_no, eng_no, maker, maker_model, "
                        + "        body_type, no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, "
                        + "        ld_wt, gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, "
                        + "        cubic_cap, floor_area, ac_fitted, audio_fitted, video_fitted, "
                        + "        vch_purchase_as, vch_catg, dealer_cd, sale_amt, laser_code, garage_add, "
                        + "        length, width, height, regn_upto, fit_upto, annual_income, imported_vch, "
                        + "        other_criteria, pmt_type, pmt_catg, rqrd_tax_modes, op_dt "
                        + "  FROM  " + TableList.VA_OWNER_TEMP_ADMIN
                        + " where appl_no=?";

            } else if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) {
                sql = "INSERT INTO  " + TableList.VHA_OWNER
                        + " SELECT current_timestamp as moved_on , ? as moved_by, state_cd, off_cd, appl_no, regn_no, regn_dt, purchase_dt, owner_sr, "
                        + "        owner_name, f_name, c_add1, c_add2, c_add3, c_district, c_pincode, "
                        + "        c_state, p_add1, p_add2, p_add3, p_district, p_pincode, p_state, "
                        + "        owner_cd, regn_type, vh_class, chasi_no, eng_no, maker, maker_model, "
                        + "        body_type, no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, "
                        + "        ld_wt, gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, "
                        + "        cubic_cap, floor_area, ac_fitted, audio_fitted, video_fitted, "
                        + "        vch_purchase_as, vch_catg, dealer_cd, sale_amt, laser_code, garage_add, "
                        + "        length, width, height, regn_upto, fit_upto, annual_income, imported_vch, "
                        + "        other_criteria, pmt_type, pmt_catg, rqrd_tax_modes, op_dt "
                        + "  FROM  " + TableList.VA_OWNER
                        + " where appl_no=?";

            }
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, appl_no);
            ps.executeUpdate();
            //add Va_owner_temp
            NewImpl.insertIntoVhaOwnerTemp(tmgr, appl_no);

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }
    }

    public String checkPending(String regnNo, String stateCd) throws VahanException {
        String isPending = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("checkPending");
            String sql = " SELECT * FROM " + TableList.VA_DETAILS
                    + " WHERE regn_no = ? and entry_status <> ? ";
            if (stateCd != null) {
                sql = sql + " and state_cd = ?";
            }
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, TableConstants.STATUS_APPROVED);
            if (stateCd != null) {
                ps.setString(3, stateCd);
            }
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isPending = "Application No [ " + rs.getString("appl_no") + " ]" + " in State [ " + ServerUtil.getStateNameByStateCode(rs.getString("state_cd")) + " ] at Office [ " + ServerUtil.getOfficeName(rs.getInt("off_cd"), rs.getString("state_cd")) + " ]"
                        + " is pending and not yet approved";
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

    public Map<Object, Object> check_RegistrationType(String regnNo) throws VahanException {
        int regType = TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE;
        TransactionManagerReadOnly tmgr = null;
        Map<Object, Object> map = null;
        try {
            tmgr = new TransactionManagerReadOnly("check_RegistrationType");
            String sql = "select * from  " + TableList.VT_OWNER_TEMP + " where temp_regn_no=?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                map = new HashMap<Object, Object>();
                map.put("regVehType", TableConstants.VM_TRANSACTION_MAST_TEMP_REG);
                map.put("appl_no", rs.getString("appl_no"));
                map.put("off_cd", rs.getInt("off_cd"));
                map.put("state_cd", rs.getString("state_cd"));
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

        return map;
    }

    public String ownerAdminapplicationInward(String applno, Owner_dobj ownerDobj, List<ComparisonBean> listChanges, int pur_cd, String remarks, String admin_rmk, String hpRemarks) throws VahanException {
        TransactionManager tmgr = null;
        try {
            String regnNo = ownerDobj.getRegn_no();
            Status_dobj status = new Status_dobj();
            status.setState_cd(Util.getUserStateCode());
            status.setEmp_cd(0);
            int actonCode = Util.getSelectedSeat().getAction_cd();
            if (actonCode == TableConstants.TM_STATEADMIN_OWNER_DATA_CHANGE_ENTRY) {
                status.setPur_cd(TableConstants.STATEADMIN_OWNER_DATA_CHANGE);
            } else {
                status.setPur_cd(TableConstants.ADMIN_OWNER_DATA_CHANGE);
            }
            status.setOff_cd(Util.getUserOffCode());
            status.setStatus("N");
            tmgr = new TransactionManager("ownerAdmin_applicationInward");
            if (applno == null || applno.isEmpty()) {
                int initialFlow[] = null;
                if (actonCode == TableConstants.TM_STATEADMIN_OWNER_DATA_CHANGE_ENTRY) {
                    initialFlow = ServerUtil.getInitialAction(tmgr, status.getState_cd(), TableConstants.STATEADMIN_OWNER_DATA_CHANGE, null);
                } else {
                    initialFlow = ServerUtil.getInitialAction(tmgr, status.getState_cd(), TableConstants.ADMIN_OWNER_DATA_CHANGE, null);
                }
                status.setFlow_slno(1);
                status.setFile_movement_slno(1);
                if (actonCode == TableConstants.TM_STATEADMIN_OWNER_DATA_CHANGE_ENTRY) {
                    status.setAction_cd(TableConstants.TM_STATEADMIN_OWNER_DATA_CHANGE_ENTRY);
                } else {
                    status.setAction_cd(TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_ENTRY);
                }
            }
            if (applno == null || applno.isEmpty()) {
                applno = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
            }
            status.setAppl_no(applno);
            status.setRegn_no(regnNo);
            ArrayList<Status_dobj> applicationStatus = ServerUtil.applicationStatusByApplNo(applno, Util.getUserStateCode());
            if (applicationStatus.isEmpty()) {
                ServerUtil.fileFlowForNewApplication(tmgr, status);
                status.setStatus(TableConstants.STATUS_COMPLETE);
                status = ServerUtil.webServiceForNextStage(status, tmgr);
                ServerUtil.fileFlow(tmgr, status);
            }
            // }
            saveAndMoveToHistory(tmgr, ownerDobj, applno, pur_cd, listChanges, remarks, admin_rmk, hpRemarks);
            tmgr.commit();
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }

        return applno;
    }

    public Status_dobj updateOrInsertStatus(Owner_dobj ownerDobj, String regn_no, Status_dobj status_dobj, String app_no, List<ComparisonBean> listChanges, String admin_rmk, int pur_cd, String remarks, String hpRemarks) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("updateOrInsertStatus");
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            if (status_dobj.getCurrent_role() == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_VERIFY
                    || status_dobj.getCurrent_role() == TableConstants.TM_STATEADMIN_OWNER_DATA_CHANGE_VERIFY
                    || status_dobj.getCurrent_role() == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_ENTRY
                    || status_dobj.getCurrent_role() == TableConstants.TM_STATEADMIN_OWNER_DATA_CHANGE_ENTRY) {
                saveAndMoveToHistory(tmgr, ownerDobj, app_no, pur_cd, listChanges, remarks, admin_rmk, hpRemarks);
            }
            if ((status_dobj.getCurrent_role() == TableConstants.TM_ADMIN_OWNER_DATA_CHANGE_APPROVAL
                    || status_dobj.getCurrent_role() == TableConstants.TM_STATEADMIN_OWNER_DATA_CHANGE_APPROVAL
                    || status_dobj.getCurrent_role() == TableConstants.TM_RTOADMIN_OWNER_DATA_CHANGE_APPROVAL)
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                return status_dobj;
            }
            ServerUtil.fileFlow(tmgr, status_dobj);
            tmgr.commit();
            return status_dobj;
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }

    }

    private void saveAndMoveToHistory(TransactionManager tmgr, Owner_dobj ownerDobj, String appl_no, int regVehType, List<ComparisonBean> listChanges, String remarks, String admin_Remarks, String hpRemarks) throws SQLException, VahanException, Exception {
        OwnerAdminImpl own = new OwnerAdminImpl();
        String regnNo = ownerDobj.getRegn_no();
        ownerDobj.setAppl_no(appl_no);

        //Delete link Vehicle From VA_Table
        if (ownerDobj.isInsertDeletelinkVehicle()) {
            NewImpl.insertIntoVhaSideTrailer(tmgr, appl_no);
            ServerUtil.deleteFromTable(tmgr, null, appl_no, TableList.VA_SIDE_TRAILER);
        }

        if (ownerDobj.isInsertUpdateFlag()) {
            ownerDobj.setInsertUpdateFlag(true);
            own.insertOrUpdateFor_OwnerAdmin(tmgr, ownerDobj, regVehType);
        }
        if (Util.getUserStateCode().equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(ownerDobj.getVh_class()) + ",")) {
            NewImpl.insertOrUpdateVaOwnerOther(tmgr, ownerDobj);
        }

        if (ownerDobj.isInsertUpdateHpaFlag()) {
            ownerDobj.getListHpaDobj().get(0).setAppl_no(appl_no);
            if (ownerDobj.isCheckVaownerTempFlag()) {
                ownerDobj.getListHpaDobj().get(0).setRegn_no("NEW");
            } else {
                ownerDobj.getListHpaDobj().get(0).setRegn_no(ownerDobj.getRegn_no());
            }
            if (ownerDobj.getListHpaDobj() != null && !ownerDobj.getListHpaDobj().isEmpty()) {
                HpaImpl.insertUpdateHPA(tmgr, ownerDobj.getListHpaDobj(), Util.getUserStateCode(), Util.getUserOffCode());
            }
        } else if (ownerDobj.isDeleteUpdateHpaFlag()) {
            //delete Hypothicate
            HpaImpl.insertDeleteFromVaHpa(tmgr, appl_no);
        }
        // add owner identification 
        if (ownerDobj.getOwner_identity() != null && ownerDobj.getOwner_identity().isInsertUpdateIdentification()) {
            OwnerIdentificationDobj ownerIde = ownerDobj.getOwner_identity();
            ownerIde.setAppl_no(appl_no);
            if (ownerDobj.isCheckVaownerTempFlag()) {
                ownerIde.setRegn_no("NEW");
            } else {
                ownerIde.setRegn_no(regnNo);
            }
            //ownerIde.setRegn_no(regnNo);
            OwnerIdentificationImpl.insertUpdateOwnerIdentification(tmgr, ownerIde);
        }
        // add insert into insurance
        if (ownerDobj.getInsDobj() != null && ownerDobj.getInsDobj().isInsertUpdateInsurnaceFlag() && !ownerDobj.getInsDobj().isIibData()) {
            //InsImpl.insertUpdateInsurance(tmgr, appl_no, regnNo, ownerDobj.getInsDobj(), Util.getUserStateCode(), Util.getUserOffCode());
            if (ownerDobj.isCheckVaownerTempFlag()) {
                InsImpl.insertUpdateInsurance(tmgr, appl_no, "NEW", ownerDobj.getInsDobj(), Util.getUserStateCode(), Util.getUserOffCode());
            } else {
                InsImpl.insertUpdateInsurance(tmgr, appl_no, regnNo, ownerDobj.getInsDobj(), Util.getUserStateCode(), Util.getUserOffCode());
            }
        }

        //other state vehicle
        if (!ownerDobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE) && ownerDobj.getOtherStateVehDobj() == null && ownerDobj.isDeleteUpdaeOtherStateFlag()) {
            //VA_OTHER_STATE_VEH
            OtherStateVehImpl othervehicle = new OtherStateVehImpl();//VA_OTHER_STATE_VEH
            othervehicle.insertIntoOtherStateVehHistory(tmgr, appl_no);
            ServerUtil.deleteFromTable(tmgr, null, appl_no, TableList.VA_OTHER_STATE_VEH);
        }
        if (ownerDobj.getOtherStateVehDobj() != null && ownerDobj.getOtherStateVehDobj().isInserUpdaeOtherStateFlag()) {
            OtherStateVehImpl othervehicle = new OtherStateVehImpl();
            OtherStateVehDobj otherStateDobj = ownerDobj.getOtherStateVehDobj();
            otherStateDobj.setApplNo(appl_no);
            ownerDobj.getOtherStateVehDobj().setInserUpdaeOtherStateFlag(true);
            othervehicle.insertUpdateOtherStateVeh(tmgr, otherStateDobj);
        }

        //Insert into TempRegn
        if (!ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_TEMPORARY) && ownerDobj.getTempReg() == null && ownerDobj.isTempRegndeleteFlag()) {
            NewImpl.insertIntoTempRegnDetails(tmgr, appl_no);
            ServerUtil.deleteFromTable(tmgr, null, appl_no, TableList.VA_TMP_REGN_DTL);
        }
        if (ownerDobj.getTempReg() != null && ownerDobj.getTempReg().isTempRegnInsertFlag()) {
            TempRegDobj tempDobj = ownerDobj.getTempReg();
            tempDobj.setAppl_no(appl_no);
            if (ownerDobj.isCheckVaownerTempFlag()) {
                tempDobj.setRegn_no("NEW");
            }
            own.insertOrupdateVaTempRegn(tmgr, tempDobj);
        }
        //Insert into Axle
        if (ownerDobj.getVehType() == TableConstants.VM_VEHTYPE_NON_TRANSPORT && ownerDobj.isAxelDeleteFlag()) {
            //VA_AXLE
            AxleImpl.insertIntoVhaAxle(tmgr, appl_no);
            ServerUtil.deleteFromTable(tmgr, null, appl_no, TableList.VA_AXLE);
        }
        if (ownerDobj.getAxleDobj() != null && ownerDobj.getAxleDobj().isAxelInsertUpdateFlag()) {
            AxleImpl.saveAxleDetails_Impl(ownerDobj.getAxleDobj(), appl_no, tmgr);
        }

        //Insert into ExArmy
        if (!ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_EXARMY) && ownerDobj.getExArmy_dobj() == null && ownerDobj.isExArmyDeleteFlag()) {
            ExArmyImpl.insertIntoVhaExArmy(tmgr, appl_no);
            ServerUtil.deleteFromTable(tmgr, null, appl_no, TableList.VA_OWNER_EX_ARMY);
        }
        if (ownerDobj.getExArmy_dobj() != null && ownerDobj.getExArmy_dobj().isExArmyInsertUpdateFlag()) {
            ExArmyImpl.saveExArmyVehicleDetails_Impl(ownerDobj.getExArmy_dobj(), appl_no, tmgr);
        }
        //Insert inot Imported
        if (!TableConstants.VM_REGN_TYPE_IMPORTED_YES.equalsIgnoreCase(ownerDobj.getImported_vch()) && ownerDobj.isImportedVehDeleteFlag() && ownerDobj.getImp_Dobj() == null) {
            ImportedVehicleImpl.insertIntoVhaImpVeh(tmgr, appl_no);
            ServerUtil.deleteFromTable(tmgr, null, appl_no, TableList.VA_IMPORT_VEH);
        }
        if (ownerDobj.getImp_Dobj() != null && ownerDobj.getImp_Dobj().isImportedVehInsertUpdateFlag()) {
            ImportedVehicleImpl.saveImportedDetails_Impl(ownerDobj.getImp_Dobj(), appl_no, tmgr);
        }

        //Insert inot Retrofiting
        if (ownerDobj.isRetrofitDeleteFlag() && ownerDobj.getCng_dobj() == null) {
            //VA_RETROFITTING_DTLS
            RetroFittingDetailsImpl.insertIntoVhaCng(tmgr, appl_no);
            ServerUtil.deleteFromTable(tmgr, null, appl_no, TableList.VA_RETROFITTING_DTLS);
        }
        if (ownerDobj.getCng_dobj() != null && ownerDobj.getCng_dobj().isRetrofitInsertUpdateFlag()) {
            RetroFittingDetailsImpl.saveCngVehicleDetails_Impl(ownerDobj.getCng_dobj(), appl_no, tmgr);
        }
        //Insert into Governor
        SpeedGovernorDobj speedGVr = ownerDobj.getSpeedGovernorDobj();
        if (speedGVr == null && ownerDobj.isSpeedGovDeleteFlag()) {
            FitnessImpl.insertIntoVhaSpeedGovernor(appl_no, tmgr);
            ServerUtil.deleteFromTable(tmgr, null, appl_no, TableList.VA_SPEED_GOVERNOR);
        }
        if (speedGVr != null && speedGVr.isSpeedGovInsertUpdateFlag()) {
            speedGVr.setAppl_no(appl_no);
            // speedGVr.setRegn_no(ownerDobj.getRegn_no());
            if (ownerDobj.isCheckVaownerTempFlag()) {
                speedGVr.setRegn_no("NEW");
            } else {
                speedGVr.setRegn_no(ownerDobj.getRegn_no());
            }
            FitnessImpl.insertUpdateVaSpeedGovernor(speedGVr, tmgr);
        }
        //insert into  ReflectiveTape
        ReflectiveTapeDobj reflectTap = ownerDobj.getReflectiveTapeDobj();
        if (reflectTap == null && ownerDobj.isReflectiveTapeDeleteFlag()) {
            FitnessImpl fitnessImpl = new FitnessImpl();
            fitnessImpl.insertIntoVhaReflectiveTape(tmgr, appl_no, Util.getEmpCode());
            ServerUtil.deleteFromTable(tmgr, null, appl_no, TableList.VA_REFLECTIVE_TAPE);
        }
        if (reflectTap != null && reflectTap.isReflectiveTapeInsertUpdateFlag()) {
            reflectTap.setApplNo(appl_no);
            if (ownerDobj.isCheckVaownerTempFlag()) {
                reflectTap.setRegn_no("NEW");
            } else {
                reflectTap.setRegn_no(ownerDobj.getRegn_no());
            }
            //reflectTap.setRegn_no(ownerDobj.getRegn_no());
            FitnessImpl fitnessImpl = new FitnessImpl();
            fitnessImpl.insertOrUpdateVaReflectiveTape(tmgr, reflectTap);
        }
        if (Util.getUserStateCode().equals("GA") && (Util.getUserCategory().equals(TableConstants.USER_CATG_STATE_ADMIN)) && regVehType != TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
            saveAdminRemarkDetails(ownerDobj, admin_Remarks, tmgr);

        }
        if (regVehType == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE && ownerDobj.isTrailerDeleteFlag()) {
            //Delete Va_Trailer
            Trailer_Impl.insertIntoTrailerHistory(tmgr, appl_no);
            Trailer_Impl.deleteFromVaTrailer(tmgr, ownerDobj.getRegn_no(), appl_no);
        }

        if (regVehType == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE && ownerDobj.getTrailerDobj() != null && ownerDobj.getTrailerDobj().getChasi_no() != null && !ownerDobj.getTrailerDobj().getChasi_no().trim().isEmpty() && ownerDobj.getTrailerDobj().isInsertUpdateTrailerFlag()) {
            Trailer_Impl.insertUpdateTrailer(tmgr, appl_no, regnNo, appl_no, ownerDobj.getTrailerDobj());
        }
        String changedData = "";
        changedData = ComparisonBeanImpl.changedDataContents(listChanges);
        if (!CommonUtils.isNullOrBlank(hpRemarks)) {
            changedData = changedData + hpRemarks + "|";
        }
        if (!remarks.equals("")) {
            changedData = changedData + "[AdminRemarks-" + remarks + "]";
        }
        if (!changedData.equals("")) {
            insertIntoVhaChangedData(tmgr, appl_no, changedData);
        }

    }

    public String getOwnerApplicationForTempRegn(String applno, String stateCode, int pur_cd) throws VahanException {
        String applOwner = null;
        TransactionManagerReadOnly tmgr = null;
        Map<Object, Object> map = null;
        try {
            tmgr = new TransactionManagerReadOnly("getOwnerApplicationForTempRegn");
            String sql = "select * from va_owner vo inner join va_details vd on vo.appl_no =vd.appl_no and  vo.state_cd =vd.state_cd where vo.appl_no =? and vo.state_cd=? and vd.entry_status <> ? and vd.pur_cd=?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, applno);
            ps.setString(2, stateCode);
            ps.setString(3, TableConstants.STATUS_APPROVED);
            ps.setInt(4, pur_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                applOwner = rs.getString("appl_no");
            }
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

        return applOwner;
    }

    private void saveAndApproveTmpRegn(TransactionManager tmgr, Owner_dobj ownerDobj, String appl_no, int regVehType, boolean isHypth) throws SQLException, VahanException, Exception {
        OwnerAdminImpl own = new OwnerAdminImpl();
        String regnNo = ownerDobj.getRegn_no();
        ownerDobj.setAppl_no(appl_no);
        own.insertOrUpdateFor_OwnerAdmin(tmgr, ownerDobj, regVehType);
        if (isHypth && ownerDobj.getListHpaDobj() != null && !ownerDobj.getListHpaDobj().isEmpty()) {
            ownerDobj.getListHpaDobj().get(0).setAppl_no(appl_no);
            if (ownerDobj.isCheckVaownerTempFlag()) {
                ownerDobj.getListHpaDobj().get(0).setRegn_no("NEW");
            } else {
                ownerDobj.getListHpaDobj().get(0).setRegn_no(ownerDobj.getRegn_no());
            }
            if (ownerDobj.getListHpaDobj() != null && !ownerDobj.getListHpaDobj().isEmpty()) {
                HpaImpl.insertUpdateHPA(tmgr, ownerDobj.getListHpaDobj(), Util.getUserStateCode(), Util.getUserOffCode());
            }
        } else if (ownerDobj.isDeleteUpdateHpaFlag()) {
            //delete Hypothicate
            HpaImpl.insertDeleteFromVaHpa(tmgr, appl_no);
        }
        // add owner identification 
        if (ownerDobj.getOwner_identity() != null) {
            OwnerIdentificationDobj ownerIde = ownerDobj.getOwner_identity();
            ownerIde.setAppl_no(appl_no);
            if (ownerDobj.isCheckVaownerTempFlag()) {
                ownerIde.setRegn_no("NEW");
            } else {
                ownerIde.setRegn_no(regnNo);
            }
            OwnerIdentificationImpl.insertUpdateOwnerIdentification(tmgr, ownerIde);
        }
        // add insert into insurance
        if (ownerDobj.getInsDobj() != null && !ownerDobj.getInsDobj().isIibData()) {
            if (ownerDobj.isCheckVaownerTempFlag()) {
                InsImpl.insertUpdateInsurance(tmgr, appl_no, "NEW", ownerDobj.getInsDobj(), Util.getUserStateCode(), Util.getUserOffCode());
            } else {
                InsImpl.insertUpdateInsurance(tmgr, appl_no, regnNo, ownerDobj.getInsDobj(), Util.getUserStateCode(), Util.getUserOffCode());
            }
        }

        //other state vehicle
        if (!ownerDobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE) && ownerDobj.getOtherStateVehDobj() == null) {
            //VA_OTHER_STATE_VEH
            OtherStateVehImpl othervehicle = new OtherStateVehImpl();//VA_OTHER_STATE_VEH
            othervehicle.insertIntoOtherStateVehHistory(tmgr, appl_no);
            ServerUtil.deleteFromTable(tmgr, null, appl_no, TableList.VA_OTHER_STATE_VEH);
        }
        if (ownerDobj.getOtherStateVehDobj() != null) {//Neeraj Sir
            OtherStateVehImpl othervehicle = new OtherStateVehImpl();
            OtherStateVehDobj otherStateDobj = ownerDobj.getOtherStateVehDobj();
            otherStateDobj.setApplNo(appl_no);
            ownerDobj.getOtherStateVehDobj().setInserUpdaeOtherStateFlag(true);
            othervehicle.insertUpdateOtherStateVeh(tmgr, otherStateDobj);
        }

        //Insert into TempRegn
        if (!ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_TEMPORARY) && ownerDobj.getTempReg() == null) {
            NewImpl.insertIntoTempRegnDetails(tmgr, appl_no);
            ServerUtil.deleteFromTable(tmgr, null, appl_no, TableList.VA_TMP_REGN_DTL);
        }
        if (ownerDobj.getTempReg() != null) {
            TempRegDobj tempDobj = ownerDobj.getTempReg();
            tempDobj.setAppl_no(appl_no);
            if (ownerDobj.isCheckVaownerTempFlag()) {
                tempDobj.setRegn_no("NEW");
            }
            own.insertOrupdateVaTempRegn(tmgr, tempDobj);
        }

        //Insert into Axle
        if (ownerDobj.getVehType() == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
            //VA_AXLE
            AxleImpl.insertIntoVhaAxle(tmgr, appl_no);
            ServerUtil.deleteFromTable(tmgr, null, appl_no, TableList.VA_AXLE);
        }
        if (ownerDobj.getAxleDobj() != null) {
            AxleImpl.saveAxleDetails_Impl(ownerDobj.getAxleDobj(), appl_no, tmgr);
        }

        //Insert into ExArmy
        if (!ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_EXARMY) && ownerDobj.getExArmy_dobj() == null) {
            ExArmyImpl.insertIntoVhaExArmy(tmgr, appl_no);
            ServerUtil.deleteFromTable(tmgr, null, appl_no, TableList.VA_OWNER_EX_ARMY);
        }
        if (ownerDobj.getExArmy_dobj() != null) {
            ExArmyImpl.saveExArmyVehicleDetails_Impl(ownerDobj.getExArmy_dobj(), appl_no, tmgr);
        }
        //Insert inot Imported
        if (!TableConstants.VM_REGN_TYPE_IMPORTED_YES.equalsIgnoreCase(ownerDobj.getImported_vch()) && ownerDobj.getImp_Dobj() == null) {
            ImportedVehicleImpl.insertIntoVhaImpVeh(tmgr, appl_no);
            ServerUtil.deleteFromTable(tmgr, null, appl_no, TableList.VA_IMPORT_VEH);
        }
        if (ownerDobj.getImp_Dobj() != null) {
            ImportedVehicleImpl.saveImportedDetails_Impl(ownerDobj.getImp_Dobj(), appl_no, tmgr);
        }

        //Insert inot Retrofiting
        int fuel_type = ownerDobj.getFuel();
        if ((fuel_type != TableConstants.VM_FUEL_CNG_TYPE
                && fuel_type != TableConstants.VM_FUEL_TYPE_PETROL_CNG
                && fuel_type != TableConstants.VM_FUEL_TYPE_LPG
                && fuel_type != TableConstants.VM_FUEL_TYPE_PETROL_LPG) && ownerDobj.getCng_dobj() == null) {
            RetroFittingDetailsImpl.insertIntoVhaCng(tmgr, appl_no);
            ServerUtil.deleteFromTable(tmgr, null, appl_no, TableList.VA_RETROFITTING_DTLS);
        }
        if (ownerDobj.getCng_dobj() != null) {
            RetroFittingDetailsImpl.saveCngVehicleDetails_Impl(ownerDobj.getCng_dobj(), appl_no, tmgr);
        }
        //Insert into Governor
        SpeedGovernorDobj speedGVr = ownerDobj.getSpeedGovernorDobj();
        if (speedGVr == null) {
            FitnessImpl.insertIntoVhaSpeedGovernor(appl_no, tmgr);
            ServerUtil.deleteFromTable(tmgr, null, appl_no, TableList.VA_SPEED_GOVERNOR);
        }
        if (speedGVr != null) {
            speedGVr.setAppl_no(appl_no);
            if (ownerDobj.isCheckVaownerTempFlag()) {
                speedGVr.setRegn_no("NEW");
            } else {
                speedGVr.setRegn_no(ownerDobj.getRegn_no());
            }
            FitnessImpl.insertUpdateVaSpeedGovernor(speedGVr, tmgr);
        }
        //insert into  ReflectiveTape
        ReflectiveTapeDobj reflectTap = ownerDobj.getReflectiveTapeDobj();
        if (reflectTap == null) {
            FitnessImpl fitnessImpl = new FitnessImpl();
            fitnessImpl.insertIntoVhaReflectiveTape(tmgr, appl_no, Util.getEmpCode());
            ServerUtil.deleteFromTable(tmgr, null, appl_no, TableList.VA_REFLECTIVE_TAPE);
        }
        if (reflectTap != null) {
            reflectTap.setApplNo(appl_no);
            if (ownerDobj.isCheckVaownerTempFlag()) {
                reflectTap.setRegn_no("NEW");
            } else {
                reflectTap.setRegn_no(ownerDobj.getRegn_no());
            }
            FitnessImpl fitnessImpl = new FitnessImpl();
            fitnessImpl.insertOrUpdateVaReflectiveTape(tmgr, reflectTap);
        }
    }

    public static Owner_temp_dobj getVTOwnerTemporyRegnDtls(String appl_no) throws VahanException {

        Owner_temp_dobj tempDobj = null;
        TransactionManagerReadOnly tmg = null;
        try {
            String sql = "Select * from  vt_owner_temp where appl_no=? ";
            tmg = new TransactionManagerReadOnly("getVTOwnerTemporyRegnDtls");
            PreparedStatement ps = tmg.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rsTemp = tmg.fetchDetachedRowSet();

            if (rsTemp.next()) {
                if (rsTemp.getString("state_cd_to") != null) {
                    tempDobj = new Owner_temp_dobj();
                    tempDobj.setState_cd_to(rsTemp.getString("state_cd_to"));
                    tempDobj.setOff_cd_to(rsTemp.getInt("off_cd_to"));
                    tempDobj.setTemp_regn_no(rsTemp.getString("temp_regn_no"));
                    tempDobj.setPurpose(rsTemp.getString("purpose"));
                    tempDobj.setBodyBuilding(rsTemp.getString("body_building"));
                    tempDobj.setValidFrom(rsTemp.getDate("valid_from"));
                    tempDobj.setValidUpto(rsTemp.getDate("valid_upto"));
                }
            } else {
                throw new VahanException(Util.getLocaleMsg("invalidRegn"));
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } finally {
            if (tmg != null) {
                try {
                    tmg.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    throw new VahanException(e.getMessage());
                }
            }
        }

        return tempDobj;
    }

    public String sendMailChangeData(TransactionManager tmgr, String stateCd, String userCatg, Integer offCd, String subject, String changeData) {
        String content_mail = "";
        String eMail = "";
        try {
            eMail = ServerUtil.getMailIDOfStateAndOfficeAdmin(tmgr, stateCd, userCatg, offCd);
            if (eMail != null && !eMail.toString().equals("")) {
                MailSender sendMail = new MailSender(eMail, changeData, subject);
                sendMail.start();
            }
        } catch (Exception e) {
            eMail = null;
        }
        return eMail;
    }

    public String getLinkVehicleDtls(String stateCd, int offCd, String regnNo) {

        String linkRegno = "";
        String sql = "Select * from " + TableList.VT_SIDE_TRAILER + " where regn_no=? and state_cd = ? and off_cd = ? order by op_dt desc limit 1";
        TransactionManagerReadOnly tmgr = null;

        try {
            tmgr = new TransactionManagerReadOnly("getLinkedVehicle");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                linkRegno = rs.getString("link_regn_no");
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
        return linkRegno;
    }

    private String getLinkVaRegnoDtls(String applNo, String stateCd) {
        TransactionManagerReadOnly tmgr = null;
        String linkedRegnNo = "";
        try {
            tmgr = new TransactionManagerReadOnly("getLinkVaRegnoDtls");
            String query = "Select * from " + TableList.VA_SIDE_TRAILER + " where appl_no = ? and state_cd=?";
            PreparedStatement ps = tmgr.prepareStatement(query);
            ps.setString(1, applNo);
            ps.setString(2, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                linkedRegnNo = rs.getString("link_regn_no");
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
        return linkedRegnNo;
    }

    public void updateSideTrailer(TransactionManager tmgr, Owner_dobj owner_dobj, String linkRegn) throws SQLException {
        PreparedStatement ps = null;

        String query = "INSERT INTO " + TableList.VH_SIDE_TRAILER + "(\n"
                + "            regn_no, link_regn_no, state_cd, off_cd, op_dt, moved_on, moved_by)\n"
                + "    SELECT regn_no, link_regn_no, state_cd, off_cd, op_dt,current_timestamp,?\n"
                + "  FROM " + TableList.VT_SIDE_TRAILER + " where link_regn_no = ?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, linkRegn);
        ps.executeUpdate();
        query = "Delete from " + TableList.VT_SIDE_TRAILER + " where link_regn_no = ?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, linkRegn);
        ps.executeUpdate();
        query = "INSERT INTO " + TableList.VT_SIDE_TRAILER + "(\n"
                + "   regn_no, link_regn_no, state_cd, off_cd, op_dt)\n"
                + "    SELECT regn_no, link_regn_no, state_cd, off_cd, current_timestamp\n"
                + "  FROM " + TableList.VA_SIDE_TRAILER + " where appl_no = ?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, owner_dobj.getAppl_no());
        ps.executeUpdate();
    }

    public String checkChassisNo(String chasi_no) {
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        String applNo = null;
        try {
            tmgr = new TransactionManagerReadOnly("checkChassisNo");
            sql = "select * from  VA_OWNER  owner\n"
                    + " inner join va_details d on d.appl_no=owner.appl_no and d.state_cd=owner.state_cd and d.off_cd=owner.off_cd\n"
                    + " where owner.chasi_no=? and ((d.pur_cd <> ? ) or (d.pur_cd=? and entry_status <> ?)) ";
            PreparedStatement pmt = tmgr.prepareStatement(sql);
            pmt.setString(1, chasi_no);
            pmt.setInt(2, TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE_FITNESS);
            pmt.setInt(3, TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE_FITNESS);
            pmt.setString(4, TableConstants.STATUS_APPROVED);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                applNo = rs.getString("appl_no");
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
        return applNo;

    }

    public Owner_dobj getOwnerOtherDetails(Owner_dobj ownerdobj) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("getOwnerOtherDetails");
            PreparedStatement ps = null;
            String query = "";
            query = "SELECT * FROM " + TableList.VT_OWNER_OTHER + " WHERE regn_no=? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, ownerdobj.getRegn_no());
            ps.setString(2, ownerdobj.getState_cd());
            ps.setInt(3, ownerdobj.getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                ownerdobj.setPush_bk_seat(rs.getInt("push_back_seat"));
                ownerdobj.setOrdinary_seat(rs.getInt("ordinary_seat"));
            }

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
        return ownerdobj;
    }

    String getExistChassisNoInVahanBacklog(String stateCd, String regNo, int offCd) {
        TransactionManagerReadOnly tmgr = null;
        String chasiNo = null;
        try {
            tmgr = new TransactionManagerReadOnly("getExistChassisNoInVahanBacklog");
            String sql = "SELECT chasi_no FROM  vahanbacklog.vb_owner_hist where regn_no=? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regNo);

            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                chasiNo = rs.getString("chasi_no");
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "" + ex.getStackTrace()[0]);
            new VahanException(Util.getLocaleSomthingMsg());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + "" + ex.getStackTrace()[0]);
            }
        }

        return chasiNo;
    }

    public boolean checkRegnBeforeVahan4(String chasiNo, Date regnDate, String stateCd, int offCd) throws VahanException {
        try {
            Date vahan4StrtDt = ServerUtil.getVahan4StartDate(stateCd, offCd);
            if (vahan4StrtDt != null && regnDate != null) {
                SimpleDateFormat sdfo = new SimpleDateFormat("yyyy-MM-dd");
                Date d1 = sdfo.parse(sdfo.format(regnDate));
                Date d2 = sdfo.parse(sdfo.format(vahan4StrtDt));
                if (d1.after(d2)) {
                    // When Date regnDate > Date vahan4StrtDt 
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return false;
    }

    public static boolean isHomologationChassis(String chasiNo) {
        TransactionManagerReadOnly tmgr = null;
        String sql = "SELECT * FROM gethomologationchassisinfo(?)";
        try {
            tmgr = new TransactionManagerReadOnly("validateChassisEngineCombination");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, chasiNo.toUpperCase().trim());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                return true;
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return false;
    }

    public static String checkEntryVerifyAndApproveByUserCd(String applNo, long loginUsrCd, int purCd, int action_cd) {
        TransactionManagerReadOnly tmgr = null;
        String sql = "select vh.action_cd,vh.emp_cd,to_char(vh.op_dt,'dd-Mon-yyyy HH24:MI:SS') as op_dt,tm.action_descr from " + TableList.VHA_STATUS + " vh left join \n"
                + TableList.TM_ACTION + " tm on tm.action_cd=vh.action_cd where vh.appl_no =? and vh. pur_cd=? order by vh.moved_on desc";
        try {
            tmgr = new TransactionManagerReadOnly("checkEntryVerifyAndApproveByUserCd");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setInt(2, purCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (loginUsrCd == rs.getLong("emp_cd")) {
                    if (action_cd != rs.getInt("action_cd")) {
                        return "Same user can not Approve the Modify-Owner/Vehicle Details, you already done " + rs.getString("action_descr") + " on this application no [" + applNo + "] dated " + rs.getString("op_dt") + ".";
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return "";

    }

    public InsDobj getInsuranceDataFromService(Owner_dobj ownDobj) {
        InsDobj insDobj = null;
        try {
            //start of getting insurance details from service
            InsuranceDetailService detailService = new InsuranceDetailService();
            insDobj = detailService.getInsuranceDetailsByService(ownDobj.getRegn_no(), ownDobj.getState_cd(), ownDobj.getOff_cd());
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", TableConstants.SomthingWentWrong));
        }
        return insDobj;
    }
}
