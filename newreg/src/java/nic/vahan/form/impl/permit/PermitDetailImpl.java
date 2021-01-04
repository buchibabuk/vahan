/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.RowSet;
import nic.java.util.UtilsException;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.permit.PermitDetailDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class PermitDetailImpl {

    private static final Logger LOGGER = Logger.getLogger(PermitDetailImpl.class);

    private PermitDetailImpl() {
    }

    public static PermitDetailDobj getPermitdetails(String pmtNo) {
        PermitDetailDobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        try {
            if (CommonUtils.isNullOrBlank(pmtNo)) {
                return dobj;
            }
            tmgr = new TransactionManager("Permit Details Implimentation");
            String sql = " SELECT state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from,"
                    + " valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, "
                    + " service_type, goods_to_carry, jorney_purpose, parking, replace_date, "
                    + " remarks, op_dt"
                    + " FROM " + TableList.VT_PERMIT + " where pmt_no=? and state_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, pmtNo);
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new PermitDetailDobj();
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setIssue_dt(rs.getDate("issue_dt"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setPmt_no(rs.getString("pmt_no"));
                dobj.setValid_from(rs.getDate("valid_from"));
                dobj.setValid_upto(rs.getDate("valid_upto"));
                dobj.setReplaceDt(rs.getDate("replace_date"));
                dobj.setRcpt_no(rs.getString("rcpt_no"));
                dobj.setPur_cd(rs.getInt("pur_cd"));
                dobj.setPmt_type(rs.getInt("pmt_type"));
                dobj.setPmt_catg(rs.getInt("pmt_catg"));
                dobj.setPmt_catg_desc(getPermitCatgDesc(rs.getInt("pmt_catg"),rs.getString("state_cd")));
                dobj.setService_type(rs.getInt("service_type"));
                dobj.setApplicationNo(rs.getString("appl_no"));                
                
            } else {
                sql = " SELECT state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from,"
                        + " valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg "
                        + " FROM " + TableList.VT_TEMP_PERMIT + " where pmt_no=? and state_cd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, pmtNo);
                ps.setString(2, Util.getUserStateCode());
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    dobj = new PermitDetailDobj();
                    dobj.setRegn_no(rs.getString("regn_no"));
                    dobj.setIssue_dt(rs.getDate("issue_dt"));
                    dobj.setState_cd(rs.getString("state_cd"));
                    dobj.setOff_cd(rs.getInt("off_cd"));
                    dobj.setPmt_no(rs.getString("pmt_no"));
                    dobj.setValid_from(rs.getDate("valid_from"));
                    dobj.setValid_upto(rs.getDate("valid_upto"));
                    dobj.setRcpt_no(rs.getString("rcpt_no"));
                    dobj.setPur_cd(rs.getInt("pur_cd"));
                    dobj.setPmt_type(rs.getInt("pmt_type"));
                    dobj.setPmt_catg(rs.getInt("pmt_catg"));
                    dobj.setPmt_catg_desc(getPermitCatgDesc(rs.getInt("pmt_catg"),rs.getString("state_cd")));
                    dobj.setService_type(-1);
                    dobj.setApplicationNo(rs.getString("appl_no"));                  
                }
            }
        } catch (SQLException e) {
            dobj = null;
            LOGGER.error("PMT NO - " + pmtNo + " ;" + e.toString() + " " + e.getStackTrace()[0]);
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

    public static PermitDetailDobj getPermitdetailsThroughPmtNo(String pmtNo, int purCd) {
        PermitDetailDobj dobj = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("Permit Details Implimentation");
            if (purCd == TableConstants.VM_PMT_FRESH_PUR_CD
                    || purCd == TableConstants.VM_PMT_RENEWAL_PUR_CD
                    || purCd == TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD) {
                String sql = " SELECT state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from,"
                        + " valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, "
                        + " service_type, goods_to_carry, jorney_purpose, parking, replace_date, "
                        + " remarks, op_dt"
                        + " FROM " + TableList.VT_PERMIT + " where pmt_no=?";
                PreparedStatement ps = tmgr.prepareStatement(sql);
                ps.setString(1, pmtNo);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    dobj = new PermitDetailDobj();
                    dobj.setRegn_no(rs.getString("regn_no"));
                    dobj.setIssue_dt(rs.getDate("issue_dt"));
                    dobj.setState_cd(rs.getString("state_cd"));
                    dobj.setOff_cd(rs.getInt("off_cd"));
                    dobj.setPmt_no(rs.getString("pmt_no"));
                    dobj.setValid_from(rs.getDate("valid_from"));
                    dobj.setValid_upto(rs.getDate("valid_upto"));
                    dobj.setRcpt_no(rs.getString("rcpt_no"));
                    dobj.setPur_cd(rs.getInt("pur_cd"));
                    dobj.setPmt_type(rs.getInt("pmt_type"));
                    dobj.setPmt_catg(rs.getInt("pmt_catg"));
                    dobj.setPmt_catg_desc(getPermitCatgDesc(rs.getInt("pmt_catg"),rs.getString("state_cd")));
                    dobj.setService_type(rs.getInt("service_type"));
                    dobj.setReplaceDt(rs.getDate("replace_date"));
                }
            } else if (purCd == TableConstants.VM_PMT_TEMP_PUR_CD
                    || purCd == TableConstants.VM_PMT_SPECIAL_PUR_CD) {
                String sql = " SELECT state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from,"
                        + " valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg "
                        + " FROM " + TableList.VT_TEMP_PERMIT + " where pmt_no=?";
                PreparedStatement ps = tmgr.prepareStatement(sql);
                ps.setString(1, pmtNo);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    dobj = new PermitDetailDobj();
                    dobj.setRegn_no(rs.getString("regn_no"));
                    dobj.setIssue_dt(rs.getDate("issue_dt"));
                    dobj.setState_cd(rs.getString("state_cd"));
                    dobj.setOff_cd(rs.getInt("off_cd"));
                    dobj.setPmt_no(rs.getString("pmt_no"));
                    dobj.setValid_from(rs.getDate("valid_from"));
                    dobj.setValid_upto(rs.getDate("valid_upto"));
                    dobj.setRcpt_no(rs.getString("rcpt_no"));
                    dobj.setPur_cd(rs.getInt("pur_cd"));
                    dobj.setPmt_type(rs.getInt("pmt_type"));
                    dobj.setPmt_catg(rs.getInt("pmt_catg"));
                    dobj.setPmt_catg_desc(getPermitCatgDesc(rs.getInt("pmt_catg"),rs.getString("state_cd")));
                    dobj.setService_type(-1);
                }
            }
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
        return dobj;
    }

    public static PermitDetailDobj getVH_Permitdetails(String regnNo, String pmtNo) {
        PermitDetailDobj dobj = null;
        TransactionManager tmg = null;
        try {
            tmg = new TransactionManager("Permit Details Implimentation");
            String sql = " SELECT state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from,"
                    + "valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, "
                    + " service_type, goods_to_carry, jorney_purpose, parking, replace_date, "
                    + " remarks, op_dt"
                    + " FROM " + TableList.VH_PERMIT + " where regn_no=? and pmt_no=? and pmt_status='SUR' order by moved_on DESC limit 1";

            PreparedStatement ps = tmg.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, pmtNo);
            RowSet rs = tmg.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new PermitDetailDobj();
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setIssue_dt(rs.getDate("issue_dt"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setPmt_no(rs.getString("pmt_no"));
                dobj.setValid_from(rs.getDate("valid_from"));
                dobj.setValid_upto(rs.getDate("valid_upto"));
                dobj.setRcpt_no(rs.getString("rcpt_no"));
                dobj.setPur_cd(rs.getInt("pur_cd"));
                dobj.setPmt_type(rs.getInt("pmt_type"));
                dobj.setPmt_catg(rs.getInt("pmt_catg"));
                dobj.setPmt_catg_desc(getPermitCatgDesc(rs.getInt("pmt_catg"),rs.getString("state_cd")));
                dobj.setService_type(rs.getInt("service_type"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmg != null) {
                    tmg.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dobj;
    }

    private static String getPermitCatgDesc(int code,String state_cd) {
        String desc = "";
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getPurposeList()");
            String sql = "select descr from " + TableList.VM_PERMIT_CATG + " where state_cd=? and code=?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, code);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next())//found
            {
                desc = rs.getString("descr");
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

        return desc;
    }

    /**
     * @param off_cd Util office code.
     * @param state_cd Util State code.
     * @param regn_off_cd vt_owner office code.
     */
    public static Map<String, String> getOffAllotmentResult(String state_cd, int off_cd, int regn_off_cd) {
        TransactionManager tmgr = null;
        Map<String, String> OffAllotResult = new HashMap<>();
        try {
            tmgr = new TransactionManager("getPurposeList()");
            String sql = "SELECT allotted_off_cd FROM " + TableList.VM_OFF_ALLOTMENT + " where state_cd= ? AND off_cd = ?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                OffAllotResult.put("offAllowed", String.valueOf((rs.getString("allotted_off_cd")).contains(String.valueOf(regn_off_cd))));
                OffAllotResult.put("purAllowed", "26,27");
            } else {
                OffAllotResult.put("offAllowed", "false");
                OffAllotResult.put("purAllowed", null);
            }
        } catch (SQLException e) {
            LOGGER.error(new VahanException("Function getOffAllotmentResult :: " + e.getMessage()));
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return OffAllotResult;
    }

    public static String getOffAllowedString(String state_cd) {
        TransactionManager tmgr = null;
        String OffAllowedString = ",";
        try {
            tmgr = new TransactionManager("getOffAllowedString()");
            String sql = "SELECT off_cd FROM " + TableList.VM_OFF_ALLOTMENT + " where state_cd= ? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                OffAllowedString += rs.getInt("off_cd") + ",";
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
        return OffAllowedString;
    }

    public static PermitDetailDobj getPermitdetailsFromRegnNo(String regnNo) {
        PermitDetailDobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        try {
            tmgr = new TransactionManager("getPermitdetailsFromRegnNo");
            String sql = " SELECT  a.state_cd,a.off_cd,appl_no, pmt_no, regn_no, issue_dt, valid_from,a.pmt_type as pmt_type_code,a.pmt_catg as pmt_catg_code,"
                    + " valid_upto, rcpt_no ,b.descr as purcd, c.descr as permitType, d.descr as permitCatg , "
                    + " service_type, goods_to_carry, jorney_purpose, parking, replace_date, "
                    + " remarks, op_dt"
                    + " FROM " + TableList.VT_PERMIT + " a"
                    + " left join " + TableList.TM_PURPOSE_MAST + " b on b.pur_cd=a.pur_cd "
                    + " left join " + TableList.VM_PERMIT_TYPE + " c on c.code = a.pmt_type  "
                    + " left join " + TableList.VM_PERMIT_CATG + " d on d.code = a.pmt_catg and a.state_cd = d.state_cd"
                    + " where regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new PermitDetailDobj();
                dobj.setApplicationNo(rs.getString("appl_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setIssue_dt(rs.getDate("issue_dt"));
                dobj.setPmt_no(rs.getString("pmt_no"));
                dobj.setValid_from(rs.getDate("valid_from"));
                dobj.setValid_upto(rs.getDate("valid_upto"));
                dobj.setRcpt_no(rs.getString("rcpt_no"));
                dobj.setPurCdDesc(rs.getString("purcd"));
                dobj.setPmt_catg_desc(rs.getString("permitCatg"));
                dobj.setPmt_type_desc(rs.getString("permitType"));
                dobj.setGoodsToCarry(rs.getString("goods_to_carry"));
                dobj.setJourney(rs.getString("jorney_purpose"));
                dobj.setParking(rs.getString("parking"));
                dobj.setReplaceDt(rs.getDate("replace_date"));
                dobj.setPmt_type(rs.getInt("pmt_type_code"));
                dobj.setPmt_catg(rs.getInt("pmt_catg_code"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
            }
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
        return dobj;
    }

    public static PermitDetailDobj getTempPermitdetailsFromRegnNo(String regnNo) {
        PermitDetailDobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        try {
            tmgr = new TransactionManager("getTempPermitdetailsFromRegnNo");
            String sql = " SELECT appl_no,regn_no,issue_dt,pmt_no,valid_from,valid_upto,rcpt_no,b.descr as purcd,a.pur_cd,c.descr as permitType,d.descr as permitCatg,goods_to_carry,route_fr,route_to,via,reason,pmt_type,pmt_catg  FROM " + TableList.VT_TEMP_PERMIT + " a"
                    + " left join " + TableList.TM_PURPOSE_MAST + " b on b.pur_cd=a.pur_cd "
                    + " left join " + TableList.VM_PERMIT_TYPE + " c on c.code = a.pmt_type  "
                    + " left join " + TableList.VM_PERMIT_CATG + " d on d.code = a.pmt_catg and a.state_cd = d.state_cd"
                    + " where regn_no=? order by op_dt DESC limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new PermitDetailDobj();
                dobj.setApplicationNo(rs.getString("appl_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setIssue_dt(rs.getDate("issue_dt"));
                dobj.setPmt_no(rs.getString("pmt_no"));
                dobj.setValid_from(rs.getDate("valid_from"));
                dobj.setValid_upto(rs.getDate("valid_upto"));
                dobj.setRcpt_no(rs.getString("rcpt_no"));
                dobj.setPurCdDesc(rs.getString("purcd"));
                dobj.setPur_cd(rs.getInt("pur_cd"));
                dobj.setPmt_catg_desc(rs.getString("permitCatg"));
                dobj.setPmt_type_desc(rs.getString("permitType"));
                dobj.setGoodsToCarry(rs.getString("goods_to_carry"));
                dobj.setJourney(rs.getString("route_fr"));
                dobj.setParking(rs.getString("route_to"));
                dobj.setAuthFrom(rs.getString("via"));
                dobj.setAuthTo(rs.getString("reason"));
                dobj.setPmt_type(rs.getInt("pmt_type"));
                dobj.setPmt_catg(rs.getInt("pmt_catg"));

            }
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
        return dobj;
    }

    public static PermitDetailDobj getAuthDetails(String regnNo) {
        TransactionManager tmgr = null;
        PermitDetailDobj dobj = null;
        try {
            tmgr = new TransactionManager("getAuthDetails()");
            String sql = "SELECT auth_no,to_char( auth_to,'dd-Mon-yyyy') as auth_to ,to_char( auth_fr,'dd-Mon-yyyy') as auth_fr "
                    + "FROM " + TableList.VT_PERMIT_HOME_AUTH + " where regn_no = ?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new PermitDetailDobj();
                dobj.setAuthNo(rs.getString("auth_no").toUpperCase());
                dobj.setAuthTo(rs.getString("auth_to"));
                dobj.setAuthFrom(rs.getString("auth_fr"));
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
        return dobj;
    }

    public static PermitDetailDobj getPermitSurrenderDetails(String regnNo, String stateCd) throws VahanException {
        PermitDetailDobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        boolean flag = false;
        try {
            tmgr = new TransactionManager("getPermitSurrenderDetails");
            String sql = "SELECT pur_cd FROM " + TableList.VH_PERMIT
                    + " WHERE regn_no=? and state_cd=? and pmt_status=? order by moved_on desc limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            ps.setString(3, "SUR");
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                flag = true;
            }

            if (flag) {
                sql = "SELECT a.*,b.pur_cd,b.descr as surrender_reason FROM " + TableList.VT_PERMIT_TRANSACTION + " a "
                        + " LEFT JOIN " + TableList.TM_PURPOSE_MAST + " b ON a.trans_pur_cd=b.pur_cd "
                        + " WHERE a.regn_no=? and a.state_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regnNo);
                ps.setString(2, stateCd);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    dobj = new PermitDetailDobj();
                    dobj.setTransactionPurCdDescr(rs.getString("surrender_reason"));
                    dobj.setPur_cd(rs.getInt("pur_cd"));
                    dobj.setTrans_pur_cd(rs.getInt("trans_pur_cd"));
                } else {
                    sql = "SELECT a.*,b.pur_cd,b.descr as surrender_reason FROM " + TableList.VHA_PERMIT_TRANSACTION + " a "
                            + " LEFT JOIN " + TableList.TM_PURPOSE_MAST + " b ON a.trans_pur_cd=b.pur_cd "
                            + " WHERE regn_no=? and state_cd=? and a.pur_cd=? order by moved_on desc limit 1";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, regnNo);
                    ps.setString(2, stateCd);
                    ps.setInt(3, TableConstants.VM_PMT_SURRENDER_PUR_CD);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        dobj = new PermitDetailDobj();
                        dobj.setTransactionPurCdDescr(rs.getString("surrender_reason"));
                        dobj.setPur_cd(rs.getInt("pur_cd"));
                        dobj.setTrans_pur_cd(rs.getInt("trans_pur_cd"));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong due to Error in Getting Details of Permit Surrender, Please Contact to the System Administrator.");
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

    public static boolean isTSRallowed(String regnNo, String stateCd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        boolean flag = false;
        try {
            tmgr = new TransactionManager("isTSRallowed");
            String sql = "SELECT regn_no FROM " + TableList.VH_PERMIT
                    + " WHERE regn_no=? and state_cd=? and pmt_status=? and pmt_type =? and pmt_catg in(?,?,?,?) order by moved_on desc limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            ps.setString(3, "SUR");
            ps.setInt(4, TableConstants.CONTRACT_CARRIAGE_PERMIT);
            ps.setInt(5, 11);//"AUTO RICKSHAW (TSR)"
            ps.setInt(6, 14);//"PHATPHAT SEWA"
            ps.setInt(7, 15);//"AUTO RICKSHAW (TSR-NCR-HR)"
            ps.setInt(8, 16);//"AUTO RICKSHAW (TSR-NCR-UP)"
            rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) {
                flag = true;
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong due to Error in Getting Details of Permit Surrender for TSR, Please Contact to the System Administrator.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return flag;
    }
}
