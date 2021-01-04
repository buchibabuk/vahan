/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class PermitOwnerDetailImpl {

    private static final Logger LOGGER = Logger.getLogger(PermitOwnerDetailImpl.class);

    public PermitOwnerDetailDobj set_Owner_appl_db_to_dobj(String regn_no, String state_cd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        PermitOwnerDetailDobj owner_dobj = null;
        String query = "";
        String regn_state = "";
        RowSet rs;
        if (regn_no != null) {
            regn_no = regn_no.toUpperCase();
        }
        if (state_cd == null) {
            regn_state = Util.getUserStateCode();
        } else {
            regn_state = state_cd.toUpperCase();
        }
        try {
            tmgr = new TransactionManager("Owner_Impl");
            if (!CommonUtils.isNullOrBlank(regn_no)) {
                query = "select a.*, COALESCE(b.mobile_no, 0) AS mobile_no, COALESCE(b.email_id, '') AS email_id, COALESCE(b.owner_ctg, -1) AS owner_ctg"
                        + " from " + TableList.VIEW_VV_OWNER + " a \n"
                        + " left join " + TableList.VT_OWNER_IDENTIFICATION + " b on a.regn_no = b.regn_no and b.state_cd = a.state_cd and b.off_cd = a.off_cd  \n"
                        + " where a.state_cd = ? AND a.regn_no=? AND UPPER(status) in ('A','Y') ";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, regn_state);
                ps.setString(2, regn_no);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    owner_dobj = new PermitOwnerDetailDobj();
                    owner_dobj.setRegn_no(rs.getString("regn_no"));
                    owner_dobj.setOwner_name(rs.getString("owner_name"));
                    owner_dobj.setF_name(rs.getString("f_name"));
                    owner_dobj.setVh_class(rs.getInt("vh_class"));
                    owner_dobj.setOwner_cd(rs.getInt("owner_cd"));
                    owner_dobj.setSeat_cap(rs.getInt("seat_cap"));
                    owner_dobj.setLd_wt(rs.getInt("ld_wt"));
                    owner_dobj.setUnld_wt(rs.getInt("unld_wt"));
                    owner_dobj.setState_cd(rs.getString("state_cd"));
                    owner_dobj.setMobile_no(rs.getLong("mobile_no"));
                    owner_dobj.setEmail_id(rs.getString("email_id"));
                    owner_dobj.setOwner_catg(rs.getInt("owner_ctg"));
                    owner_dobj.setVch_catg(rs.getString("vch_catg"));
                    owner_dobj.setC_add1(rs.getString("c_add1"));
                    owner_dobj.setC_add2(rs.getString("c_add2"));
                    owner_dobj.setC_add3(rs.getString("c_add3"));
                    owner_dobj.setC_state(rs.getString("c_state"));
                    owner_dobj.setC_district(rs.getInt("c_district"));
                    owner_dobj.setC_pincode(rs.getInt("c_pincode"));
                    owner_dobj.setP_add1(rs.getString("p_add1"));
                    owner_dobj.setP_add2(rs.getString("p_add2"));
                    owner_dobj.setP_add3(rs.getString("p_add3"));
                    owner_dobj.setP_state(rs.getString("p_state"));
                    owner_dobj.setP_district(rs.getInt("p_district"));
                    owner_dobj.setP_pincode(rs.getInt("p_pincode"));
                    owner_dobj.setChasi_no(rs.getString("chasi_no"));
                    owner_dobj.setOff_cd(rs.getInt("off_cd"));
                    owner_dobj.setRegnDt(rs.getDate("regn_dt"));
                    owner_dobj.setReplaceDateByVtOwner(rs.getDate("regn_upto"));
                    owner_dobj.setFuelCd(rs.getInt("fuel"));
                    owner_dobj.setMakerName(rs.getString("maker_name"));
                    owner_dobj.setModelName(rs.getString("model_name"));
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception e) {
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
        return owner_dobj;
    }

    public PermitOwnerDetailDobj setVtOwnerDtlsOnlyDisplay(String regn_no, String state_cd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        PermitOwnerDetailDobj owner_dobj = null;
        String query = "";
        RowSet rs;
        if (regn_no != null) {
            regn_no = regn_no.toUpperCase();
        }
        try {
            tmgr = new TransactionManager("Owner_Impl");
            if (regn_no != null) {
                query = "select a.*, COALESCE(b.mobile_no, 0) AS mobile_no, COALESCE(b.email_id, '') AS email_id, COALESCE(b.owner_ctg, -1) AS owner_ctg"
                        + " from " + TableList.VIEW_VV_OWNER + " a \n"
                        + " left join " + TableList.VT_OWNER_IDENTIFICATION + " b on a.regn_no = b.regn_no and b.state_cd = a.state_cd and b.off_cd = a.off_cd "
                        + " where a.regn_no=? and a.state_cd = ?  ";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, regn_no);
                ps.setString(2, state_cd);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    owner_dobj = new PermitOwnerDetailDobj();
                    owner_dobj.setRegn_no(rs.getString("regn_no"));
                    owner_dobj.setOwner_name(rs.getString("owner_name"));
                    owner_dobj.setF_name(rs.getString("f_name"));
                    owner_dobj.setVh_class(rs.getInt("vh_class"));
                    owner_dobj.setSeat_cap(rs.getInt("seat_cap"));
                    owner_dobj.setLd_wt(rs.getInt("ld_wt"));
                    owner_dobj.setUnld_wt(rs.getInt("unld_wt"));
                    owner_dobj.setState_cd(rs.getString("state_cd"));
                    owner_dobj.setMobile_no(rs.getLong("mobile_no"));
                    owner_dobj.setEmail_id(rs.getString("email_id"));
                    owner_dobj.setOwner_catg(rs.getInt("owner_ctg"));
                    owner_dobj.setVch_catg(rs.getString("vch_catg"));
                    owner_dobj.setC_add1(rs.getString("c_add1"));
                    owner_dobj.setC_add2(rs.getString("c_add2"));
                    owner_dobj.setC_add3(rs.getString("c_add3"));
                    owner_dobj.setC_state(rs.getString("c_state"));
                    owner_dobj.setC_district(rs.getInt("c_district"));
                    owner_dobj.setC_pincode(rs.getInt("c_pincode"));
                    owner_dobj.setP_add1(rs.getString("p_add1"));
                    owner_dobj.setP_add2(rs.getString("p_add2"));
                    owner_dobj.setP_add3(rs.getString("p_add3"));
                    owner_dobj.setP_state(rs.getString("p_state"));
                    owner_dobj.setP_district(rs.getInt("p_district"));
                    owner_dobj.setP_pincode(rs.getInt("p_pincode"));
                    owner_dobj.setChasi_no(rs.getString("chasi_no"));
                    owner_dobj.setOff_cd(rs.getInt("off_cd"));
                    owner_dobj.setRegnDt(rs.getDate("regn_dt"));
                    owner_dobj.setReplaceDateByVtOwner(rs.getDate("regn_upto"));
                    owner_dobj.setFuelCd(rs.getInt("fuel"));
                    owner_dobj.setOwnerStatus(rs.getString("status"));
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception e) {
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
        return owner_dobj;
    }

    public PermitOwnerDetailDobj setVhOwnerDtlsOnlyDisplay(String regn_no, String state_cd) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        PermitOwnerDetailDobj owner_dobj = null;
        String query = "";
        RowSet rs;
        if (regn_no != null) {
            regn_no = regn_no.toUpperCase();
        }
        try {
            tmgr = new TransactionManagerReadOnly("setVhOwnerDtlsOnlyDisplay");
            if (regn_no != null) {
                query = "select a.*, COALESCE(b.mobile_no, 0) AS mobile_no, COALESCE(b.email_id, '') AS email_id, COALESCE(b.owner_ctg, -1) AS owner_ctg"
                        + " from " + TableList.VH_OWNER + " a \n"
                        + " left join " + TableList.VH_OWNER_IDENTIFICATION + " b on a.regn_no = b.regn_no and b.state_cd = a.state_cd and b.off_cd = a.off_cd "
                        + " where a.regn_no=? and a.state_cd = ?  order by a.moved_on DESC limit 1";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, regn_no);
                ps.setString(2, state_cd);
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    owner_dobj = new PermitOwnerDetailDobj();
                    owner_dobj.setRegn_no(rs.getString("regn_no"));
                    owner_dobj.setOwner_name(rs.getString("owner_name"));
                    owner_dobj.setF_name(rs.getString("f_name"));
                    owner_dobj.setVh_class(rs.getInt("vh_class"));
                    owner_dobj.setSeat_cap(rs.getInt("seat_cap"));
                    owner_dobj.setLd_wt(rs.getInt("ld_wt"));
                    owner_dobj.setUnld_wt(rs.getInt("unld_wt"));
                    owner_dobj.setState_cd(rs.getString("state_cd"));
                    owner_dobj.setMobile_no(rs.getLong("mobile_no"));
                    owner_dobj.setEmail_id(rs.getString("email_id"));
                    owner_dobj.setOwner_catg(rs.getInt("owner_ctg"));
                    owner_dobj.setVch_catg(rs.getString("vch_catg"));
                    owner_dobj.setC_add1(rs.getString("c_add1"));
                    owner_dobj.setC_add2(rs.getString("c_add2"));
                    owner_dobj.setC_add3(rs.getString("c_add3"));
                    owner_dobj.setC_state(rs.getString("c_state"));
                    owner_dobj.setC_district(rs.getInt("c_district"));
                    owner_dobj.setC_pincode(rs.getInt("c_pincode"));
                    owner_dobj.setP_add1(rs.getString("p_add1"));
                    owner_dobj.setP_add2(rs.getString("p_add2"));
                    owner_dobj.setP_add3(rs.getString("p_add3"));
                    owner_dobj.setP_state(rs.getString("p_state"));
                    owner_dobj.setP_district(rs.getInt("p_district"));
                    owner_dobj.setP_pincode(rs.getInt("p_pincode"));
                    owner_dobj.setChasi_no(rs.getString("chasi_no"));
                    owner_dobj.setOff_cd(rs.getInt("off_cd"));
                    owner_dobj.setRegnDt(rs.getDate("regn_dt"));
                    owner_dobj.setReplaceDateByVtOwner(rs.getDate("regn_upto"));
                    owner_dobj.setFuelCd(rs.getInt("fuel"));
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception e) {
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
        return owner_dobj;
    }

    public PermitOwnerDetailDobj set_VA_Owner_permit_to_dobj(String appl_no, String regn_no) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String query = "";
        PermitOwnerDetailDobj owner_dobj = null;

        try {
            tmgr = new TransactionManager("VA_Owner_permit_to_dobj");
            if (("NEW").equalsIgnoreCase(regn_no)) {
                query = "select a.*,a.owner_ctg as own_catg,b.descr as vh_class_descr from " + TableList.VA_PERMIT_OWNER + " a left outer join " + TableList.VM_VH_CLASS + " b on a.vh_class=b.vh_class where appl_no = ?";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, appl_no);
            } else {
                query = "SELECT own.*, COALESCE(ide.mobile_no, 0) AS mobile_no, COALESCE(ide.email_id, '') AS email_id, COALESCE(ide.owner_ctg, -1) AS own_catg,b.descr as vh_class_descr FROM " + TableList.VT_OWNER + " own\n"
                        + "left JOIN " + TableList.VT_OWNER_IDENTIFICATION + " ide on ide.regn_no = own.regn_no and ide.state_cd = own.state_cd and ide.off_cd = own.off_cd \n"
                        + "left JOIN " + TableList.VM_VH_CLASS + " b on own.vh_class = b.vh_class \n"
                        + "WHERE own.STATE_CD = ? AND own.REGN_NO = ? AND UPPER(status) in ('A','Y')  ";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, Util.getUserStateCode());
                ps.setString(2, regn_no);
            }
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                owner_dobj = new PermitOwnerDetailDobj();
                owner_dobj.setState_cd(rs.getString("state_cd"));
                owner_dobj.setOff_cd(rs.getInt("off_cd"));
                owner_dobj.setRegn_no(rs.getString("regn_no"));
                owner_dobj.setOwner_name(rs.getString("owner_name"));
                owner_dobj.setF_name(rs.getString("f_name"));
                owner_dobj.setVh_class(rs.getInt("vh_class"));
                owner_dobj.setVh_class_desc(rs.getString("vh_class_descr"));
                owner_dobj.setMobile_no(rs.getLong("mobile_no"));
                owner_dobj.setEmail_id(rs.getString("email_id"));
                owner_dobj.setOwner_catg(rs.getInt("own_catg"));
                owner_dobj.setC_add1(rs.getString("c_add1"));
                owner_dobj.setC_add2(rs.getString("c_add2"));
                owner_dobj.setC_add3(rs.getString("c_add3"));
                owner_dobj.setC_district(rs.getInt("c_district"));
                owner_dobj.setC_pincode(rs.getInt("c_pincode"));
                owner_dobj.setC_state(rs.getString("c_state"));
                owner_dobj.setP_add1(rs.getString("p_add1"));
                owner_dobj.setP_add2(rs.getString("p_add2"));
                owner_dobj.setP_add3(rs.getString("p_add3"));
                owner_dobj.setP_district(rs.getInt("p_district"));
                owner_dobj.setP_pincode(rs.getInt("p_pincode"));
                owner_dobj.setP_state(rs.getString("p_state"));
                owner_dobj.setSeat_cap(rs.getInt("seat_cap"));
                owner_dobj.setLd_wt(rs.getInt("ld_wt"));
                owner_dobj.setUnld_wt(rs.getInt("unld_wt"));
                owner_dobj.setVch_catg(rs.getString("vch_catg"));
                owner_dobj.setFuelCd(rs.getInt("fuel"));
                if (!("NEW").equalsIgnoreCase(regn_no)) {
                    owner_dobj.setChasi_no(rs.getString("chasi_no"));
                    owner_dobj.setFuelCd(rs.getInt("fuel"));
                    owner_dobj.setGcw(rs.getInt("gcw"));
                    owner_dobj.setReplaceDateByVtOwner(rs.getDate("regn_upto"));
                    owner_dobj.setRegnDt(rs.getDate("regn_dt"));
                    owner_dobj.setOwner_cd(rs.getInt("owner_cd"));
                    owner_dobj.setOther_criteria(rs.getInt("other_criteria"));
                }
            }

            if (!CommonUtils.isNullOrBlank(appl_no)) {
                query = "SELECT dl_no,voter_id FROM " + TableList.VA_OWNER_IDENTIFICATION + " WHERE appl_no=? and state_cd=? ";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, appl_no);
                ps.setString(2, Util.getUserStateCode());
                RowSet rs1 = tmgr.fetchDetachedRowSet();
                if (rs1.next() && owner_dobj != null) {
                    owner_dobj.setDl_no(rs1.getString("dl_no"));
                    owner_dobj.setVoter_id(rs1.getString("voter_id"));
                }
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
        return owner_dobj;
    }

    public PermitOwnerDetailDobj ownerDeatilsCounterSig(String appl_no) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        PermitOwnerDetailDobj owner_dobj = null;
        try {
            tmgr = new TransactionManager("VA_Owner_permit_to_dobj");
            String query = "select * from permit.va_permit_countersignature where appl_no =?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                owner_dobj = new PermitOwnerDetailDobj();
                owner_dobj.setState_cd(rs.getString("state_cd"));
                owner_dobj.setOff_cd(rs.getInt("off_cd"));
                owner_dobj.setRegn_no(rs.getString("regn_no"));
                owner_dobj.setOwner_name(rs.getString("owner_name"));
                owner_dobj.setF_name(rs.getString("f_name"));
                owner_dobj.setVh_class(rs.getInt("vh_class"));
                owner_dobj.setMobile_no(rs.getLong("mobile_no"));
                owner_dobj.setEmail_id(rs.getString("email_id"));
                owner_dobj.setOwner_catg(rs.getInt("owner_ctg"));
                owner_dobj.setC_add1(rs.getString("c_add1"));
                owner_dobj.setC_add2(rs.getString("c_add2"));
                owner_dobj.setC_add3(rs.getString("c_add3"));
                owner_dobj.setC_district(rs.getInt("c_district"));
                owner_dobj.setC_pincode(rs.getInt("c_pincode"));
                owner_dobj.setC_state(rs.getString("c_state"));
                owner_dobj.setP_add1(rs.getString("p_add1"));
                owner_dobj.setP_add2(rs.getString("p_add2"));
                owner_dobj.setP_add3(rs.getString("p_add3"));
                owner_dobj.setP_district(rs.getInt("p_district"));
                owner_dobj.setP_pincode(rs.getInt("p_pincode"));
                owner_dobj.setP_state(rs.getString("p_state"));
                owner_dobj.setSeat_cap(rs.getInt("seat_cap"));
                owner_dobj.setLd_wt(rs.getInt("ld_wt"));
                owner_dobj.setUnld_wt(rs.getInt("unld_wt"));
                owner_dobj.setVch_catg(rs.getString("vch_catg"));
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
        return owner_dobj;
    }

    public PermitOwnerDetailDobj ownerDeatilsCounterSigFromVt(String regn_no) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        PermitOwnerDetailDobj owner_dobj = null;
        try {
            tmgr = new TransactionManager("ownerDeatilsCounterSigFromVt");
            String query = "select * from permit.vt_permit_countersignature where regn_no =?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                owner_dobj = new PermitOwnerDetailDobj();
                owner_dobj.setState_cd(rs.getString("state_cd"));
                owner_dobj.setOff_cd(rs.getInt("off_cd"));
                owner_dobj.setRegn_no(rs.getString("regn_no"));
                owner_dobj.setOwner_name(rs.getString("owner_name"));
                owner_dobj.setF_name(rs.getString("f_name"));
                owner_dobj.setVh_class(rs.getInt("vh_class"));
                owner_dobj.setMobile_no(rs.getLong("mobile_no"));
                owner_dobj.setEmail_id(rs.getString("email_id"));
                owner_dobj.setOwner_catg(rs.getInt("owner_ctg"));
                owner_dobj.setC_add1(rs.getString("c_add1"));
                owner_dobj.setC_add2(rs.getString("c_add2"));
                owner_dobj.setC_add3(rs.getString("c_add3"));
                owner_dobj.setC_district(rs.getInt("c_district"));
                owner_dobj.setC_pincode(rs.getInt("c_pincode"));
                owner_dobj.setC_state(rs.getString("c_state"));
                owner_dobj.setP_add1(rs.getString("p_add1"));
                owner_dobj.setP_add2(rs.getString("p_add2"));
                owner_dobj.setP_add3(rs.getString("p_add3"));
                owner_dobj.setP_district(rs.getInt("p_district"));
                owner_dobj.setP_pincode(rs.getInt("p_pincode"));
                owner_dobj.setP_state(rs.getString("p_state"));
                owner_dobj.setSeat_cap(rs.getInt("seat_cap"));
                owner_dobj.setLd_wt(rs.getInt("ld_wt"));
                owner_dobj.setUnld_wt(rs.getInt("unld_wt"));
                owner_dobj.setVch_catg(rs.getString("vch_catg"));
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
        return owner_dobj;
    }

    public int setGcwWeight(Owner_dobj dobj) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        int gcw = 0;
        try {
            tmgr = new TransactionManager("setGcwWeight");
            String sqlGcw = "select ld_wt from " + TableList.VT_OWNER + " \n"
                    + "where regn_no in (SELECT regn_no FROM " + TableList.VT_SIDE_TRAILER + " \n"
                    + "where link_regn_no=? and state_cd = ? and off_cd = ?) and state_cd = ? and off_cd = ?";
            ps = tmgr.prepareStatement(sqlGcw);
            ps.setString(1, dobj.getRegn_no());
            ps.setString(2, dobj.getState_cd());
            ps.setInt(3, dobj.getOff_cd());
            ps.setString(4, dobj.getState_cd());
            ps.setInt(5, dobj.getOff_cd());
            RowSet rset = tmgr.fetchDetachedRowSet();
            if (rset.next()) {
                gcw = rset.getInt("ld_wt") + dobj.getUnld_wt();
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
        return gcw;
    }

    public Owner_dobj getPermitOwnerData(PermitOwnerDetailDobj dobj) {
        Owner_dobj owner_dobj = new Owner_dobj();
        if (!CommonUtils.isNullOrBlank(String.valueOf(dobj.getVh_class()))) {
            owner_dobj.setVh_class(dobj.getVh_class());
        } else {
            owner_dobj.setVh_class(-1);
        }
        if (!CommonUtils.isNullOrBlank(dobj.getState_cd())) {
            owner_dobj.setState_cd(dobj.getState_cd());
        } else {
            owner_dobj.setState_cd(Util.getUserStateCode());
        }
        if (!CommonUtils.isNullOrBlank(String.valueOf(dobj.getOff_cd())) && dobj.getOff_cd() != 0) {
            owner_dobj.setOff_cd(dobj.getOff_cd());
        } else {
            owner_dobj.setOff_cd(Util.getSelectedSeat().getOff_cd());
        }
        if (!CommonUtils.isNullOrBlank(dobj.getRegn_no())) {
            owner_dobj.setRegn_no(dobj.getRegn_no());
        } else {
            owner_dobj.setRegn_no("");
        }
        if (!CommonUtils.isNullOrBlank(String.valueOf(dobj.getLd_wt()))) {
            owner_dobj.setLd_wt(dobj.getLd_wt());
        } else {
            owner_dobj.setLd_wt(0);
        }
        if (!CommonUtils.isNullOrBlank(String.valueOf(dobj.getUnld_wt()))) {
            owner_dobj.setUnld_wt(dobj.getUnld_wt());
        } else {
            owner_dobj.setUnld_wt(0);
        }
        if (!CommonUtils.isNullOrBlank(String.valueOf(dobj.getGcw()))) {
            owner_dobj.setGcw(dobj.getGcw());
        } else {
            owner_dobj.setGcw(0);
        }
        if (!CommonUtils.isNullOrBlank(dobj.getOwner_name())) {
            owner_dobj.setOwner_name(dobj.getOwner_name());
        } else {
            owner_dobj.setOwner_name("");
        }
        if (!CommonUtils.isNullOrBlank(dobj.getF_name())) {
            owner_dobj.setF_name(dobj.getF_name());
        } else {
            owner_dobj.setF_name("");
        }
        if (!CommonUtils.isNullOrBlank(String.valueOf(dobj.getFuelCd()))) {
            owner_dobj.setFuel(dobj.getFuelCd());
        } else {
            owner_dobj.setFuel(0);
        }
        if (!CommonUtils.isNullOrBlank(String.valueOf(dobj.getOwner_cd()))) {
            owner_dobj.setOwner_cd(dobj.getOwner_cd());
        } else {
            owner_dobj.setOwner_cd(0);
        }
        if (!CommonUtils.isNullOrBlank(String.valueOf(dobj.getOther_criteria()))) {
            owner_dobj.setOther_criteria(dobj.getOther_criteria());
        } else {
            owner_dobj.setOther_criteria(0);
        }
        if (dobj.getRegnDt() != null) {
            owner_dobj.setRegn_dt(dobj.getRegnDt());
        }
        return owner_dobj;
    }

    public PermitOwnerDetailDobj leaseOwnerDeatilsPermit(String appl_no) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        PermitOwnerDetailDobj owner_dobj = null;
        try {
            tmgr = new TransactionManager("leaseOwnerDeatilsPermit");
            String query = "select * from permit.va_lease_permit where appl_no =?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                owner_dobj = new PermitOwnerDetailDobj();
                owner_dobj.setState_cd(rs.getString("state_cd"));
                owner_dobj.setOff_cd(rs.getInt("off_cd"));
                owner_dobj.setRegn_no(rs.getString("l_regn_no"));
                owner_dobj.setOwner_name(rs.getString("l_owner_name"));
                owner_dobj.setF_name(rs.getString("l_f_name"));
                owner_dobj.setC_add1(rs.getString("c_add1"));
                owner_dobj.setC_add2(rs.getString("c_city"));
                owner_dobj.setC_pincode(rs.getInt("c_pincode"));
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
        return owner_dobj;
    }
}
