/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.form.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author Kartikey Singh
 */
public class InsImplementation {

    private static final Logger LOGGER = Logger.getLogger(InsImplementation.class);

    //Insurance details from vt_insurance if regn_no available or from va_insurance if appl_no available
    public static InsDobj set_ins_dtls_db_to_dobj(String regn_no, String appl_no, String state_cd, int off_cd) throws VahanException {
        InsDobj ins_dobj = null;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        String pvalue = null;
        String query = "";
        try {
            if ((regn_no != null && !regn_no.isEmpty() && !regn_no.equalsIgnoreCase("NEW")) || (appl_no != null && !appl_no.isEmpty())) {
                tmgr = new TransactionManagerReadOnly("set_ins_dtls_db_to_dobj");
                if (appl_no != null && !appl_no.isEmpty()) {
                    pvalue = appl_no.toUpperCase();
                    query = "select regn_no,comp_cd,descr, "
                            + "ins_type, "
                            + "ins_from, "
                            + "ins_upto,policy_no,idv,op_dt "
                            + " from " + TableList.VA_INSURANCE + " a left join  " + TableList.VM_ICCODE
                            + " b on a.comp_cd=b.ic_code"
                            + " where appl_no = ? ";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, pvalue);
                }

                if (regn_no != null && !regn_no.isEmpty()) {
                    pvalue = regn_no.toUpperCase();
                    query = "select regn_no,comp_cd,descr, "
                            + "ins_type, "
                            + "ins_from, "
                            + "ins_upto,policy_no,idv, case when op_dt is null then '1900-01-01'::timestamp else op_dt end as op_dt "
                            + " from " + TableList.VT_INSURANCE + " a left join  " + TableList.VM_ICCODE
                            + " b on a.comp_cd=b.ic_code";
                    if (state_cd != null) {
                        query = query + " where regn_no = ? and state_cd = ? order by op_dt desc";
                    } else {
                        query = query + " where regn_no = ? order by op_dt desc";
                    }

                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, pvalue);
                    if (state_cd != null) {
                        ps.setString(2, state_cd);
                    }

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
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
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

    //Insurance details from va_insurance when regn_no available
    public static InsDobj set_ins_dtls_db_to_dobjVA(String regn_no) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String tname = null, pname = null, pvalue = null;
        InsDobj ins_dobj = null;
        if (regn_no != null && !regn_no.isEmpty()) {
            tname = TableList.VA_INSURANCE;
            pname = "regn_no";
            pvalue = regn_no.toUpperCase();
            String query = "select regn_no,comp_cd, "
                    + "ins_type, "
                    + "ins_from, "
                    + "ins_upto,policy_no,idv,op_dt "
                    + "from " + tname + " where " + pname + " = ? ";
            try {
                tmgr = new TransactionManager("set_ins_dtls_db_to_dobj");
                ps = tmgr.prepareStatement(query);
                ps.setString(1, pvalue);
                RowSet rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    ins_dobj = new InsDobj();
                    ins_dobj.setComp_cd(rs.getInt("comp_cd"));
                    ins_dobj.setIns_type(rs.getInt("ins_type"));
                    ins_dobj.setIns_from(rs.getDate("ins_from"));
                    ins_dobj.setIns_upto(rs.getDate("ins_upto"));
                    ins_dobj.setPolicy_no(rs.getString("policy_no"));
                    ins_dobj.setIdv(rs.getLong("idv"));
                    ins_dobj.setRegn_no(rs.getString("regn_no"));
                    ins_dobj.setOp_dt(rs.getDate("op_dt"));
                }
            } catch (SQLException sqle) {
                LOGGER.error(sqle.toString() + "-" + sqle.getStackTrace()[0]);
                throw new VahanException("Something went wrong during fetching of insurance details, Please contact to the System Administrator.");
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
                throw new VahanException("Something went wrong during fetching of insurance details, Please contact to the System Administrator.");
            } finally {
                try {
                    if (tmgr != null) {
                        tmgr.release();
                    }
                } catch (Exception e) {
                    LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
                }
            }
        }
        return ins_dobj;
    }

    public static void updateInsurance(TransactionManager tmgr, InsDobj dobj, String appl_no, String regn_no) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "UPDATE " + TableList.VA_INSURANCE + " "
                    + "   SET comp_cd=?,"
                    + "       ins_type=?,"
                    + "       ins_from=?,"
                    + "       ins_upto=?,"
                    + "       policy_no=?,"
                    + "       idv=?,"
                    + "       op_dt=current_timestamp"
                    + " WHERE appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, dobj.getComp_cd());
            ps.setInt(2, dobj.getIns_type());
            if (dobj.getIns_from() != null) {
                ps.setDate(3, new java.sql.Date(dobj.getIns_from().getTime()));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }
            if (dobj.getIns_upto() != null) {
                ps.setDate(4, new java.sql.Date(dobj.getIns_upto().getTime()));
            } else {
                ps.setNull(4, java.sql.Types.DATE);
            }
            ps.setString(5, dobj.getPolicy_no());
            ps.setLong(6, dobj.getIdv());
            ps.setString(7, appl_no);
            ps.executeUpdate();
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + "-" + sqle.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        }
    } // end of updateInsurance

    public static void insertIntoInsuranceHistory(TransactionManager tmgr, String appl_no, String regn_no) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        String empCode = "";
        try {
            Map map = (Map) Util.getSession().getAttribute("seat_map");
            if (map == null) {
                empCode = "0";
            } else {
                empCode = Util.getEmpCode();
            }
            sql = "INSERT INTO " + TableList.VHA_INSURANCE + ""
                    + "     SELECT current_timestamp as moved_on,? as moved_by, state_cd, off_cd, appl_no, regn_no, comp_cd, ins_type, ins_from, "
                    + " ins_upto, policy_no, idv, op_dt"
                    + " FROM " + TableList.VA_INSURANCE + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, appl_no);
            ps.executeUpdate();
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + "-" + sqle.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        }
    } // end of insertIntoInsuranceHistory

    /*
     * @author Kartikey Singh
     */
    public static void insertIntoInsuranceHistory(TransactionManager tmgr, String appl_no, String regn_no,
            String empCode) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "INSERT INTO " + TableList.VHA_INSURANCE + ""
                    + "     SELECT current_timestamp as moved_on,? as moved_by, state_cd, off_cd, appl_no, regn_no, comp_cd, ins_type, ins_from, "
                    + " ins_upto, policy_no, idv, op_dt"
                    + " FROM " + TableList.VA_INSURANCE + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, appl_no);
            ps.executeUpdate();
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + "-" + sqle.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        }
    }

    public static String checkPolicyNoUniqueness(String policy_no, int compCode) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        boolean flag = false;
        String regnNo = null;
        TransactionManager tmgr = null;
        ResultSet rs = null;
        try {
            if (policy_no != null && !policy_no.equalsIgnoreCase("NA")) {
                tmgr = new TransactionManager("checkPolicyNoUniqueness");
                sql = "select * from " + TableList.VA_INSURANCE + " where comp_cd = ? and policy_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setInt(1, compCode);
                ps.setString(2, policy_no);
                rs = ps.executeQuery();
                if (rs.next()) {
                    regnNo = rs.getString("regn_no");
                } else {
                    sql = "select * from " + TableList.VT_INSURANCE + " where comp_cd = ? and policy_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setInt(1, compCode);
                    ps.setString(2, policy_no);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        regnNo = rs.getString("regn_no");
                    }
                }
            }
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + "-" + sqle.getStackTrace()[0]);
            throw new VahanException("Something went wrong during updation of insurance details, Please contact to the System Administrator.");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during updation of insurance details, Please contact to the System Administrator.");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            }
        }
        return regnNo;
    }

    public static String checkPolicyNoUniqueness_without_tmgr(String policy_no, int compCode, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        boolean flag = false;
        String regnNo = null;
        RowSet rs = null;
        try {
            if (policy_no != null && !policy_no.equalsIgnoreCase("NA")) {
//                sql = "select * from " + TableList.VA_INSURANCE + " where comp_cd = ? and policy_no=?";
//                ps = tmgr.prepareStatement(sql);
//                ps.setInt(1, compCode);
//                ps.setString(2, policy_no);
//                rs = tmgr.fetchDetachedRowSet_No_release();
//                if (rs.next()) {
//                    regnNo = rs.getString("regn_no");
//                } else {
                sql = "select * from " + TableList.VT_INSURANCE + " where comp_cd = ? and policy_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setInt(1, compCode);
                ps.setString(2, policy_no);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    regnNo = rs.getString("regn_no");
                }
            }
            // }
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + "-" + sqle.getStackTrace()[0]);
            throw new VahanException("Something went wrong during updation of insurance details For Policy No:" + policy_no + ", Please contact to the System Administrator.");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during updation of insurance details For Policy No:" + policy_no + ", Please contact to the System Administrator.");
        }
        return regnNo;
    }

    public static void insertIntoInsurance(TransactionManager tmgr, InsDobj dobj, String appl_no, String regn_no,
            String stateCd, int offCd) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "INSERT INTO " + TableList.VA_INSURANCE + " (state_cd, off_cd, appl_no, regn_no, comp_cd, ins_type, ins_from, ins_upto, policy_no, idv, op_dt)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            ps.setString(3, appl_no);
            ps.setString(4, regn_no);
            ps.setInt(5, dobj.getComp_cd());
            ps.setInt(6, dobj.getIns_type());
            if (dobj.getIns_from() != null) {
                ps.setDate(7, new java.sql.Date(dobj.getIns_from().getTime()));
            } else {
                ps.setNull(7, java.sql.Types.DATE);
            }
            if (dobj.getIns_upto() != null) {
                ps.setDate(8, new java.sql.Date(dobj.getIns_upto().getTime()));
            } else {
                ps.setNull(8, java.sql.Types.DATE);
            }
            ps.setString(9, dobj.getPolicy_no() == null ? "" : dobj.getPolicy_no());
            ps.setLong(10, dobj.getIdv());
            ps.executeUpdate();
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + "-" + sqle.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        }
    } // end of insertIntoInsurance

    public static void insertUpdateInsurance(TransactionManager tmgr, String appl_no, String regn_no, InsDobj ins_dobj, String stateCd, int offCd) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "SELECT regn_no FROM " + TableList.VA_INSURANCE + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) { //if any record is exist then update otherwise insert it
                insertIntoInsuranceHistory(tmgr, appl_no, regn_no);
                if (ins_dobj != null && !CommonUtils.isNullOrBlank(ins_dobj.getPolicy_no())) {
                    updateInsurance(tmgr, ins_dobj, appl_no, regn_no);
                } else {
                    throw new VahanException("Policy No Can't be Blank");
                }
            } else {
                insertIntoInsurance(tmgr, ins_dobj, appl_no, regn_no, stateCd, offCd);
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + "-" + sqle.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        }
    } // end of insertUpdateInsurance

    /**
     * @author Kartikey Singh
     */
    public static void insertUpdateInsurance(TransactionManager tmgr, String appl_no, String regn_no, InsDobj ins_dobj,
            String stateCd, int offCd, String empCode) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "SELECT regn_no FROM " + TableList.VA_INSURANCE + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) { //if any record is exist then update otherwise insert it
                insertIntoInsuranceHistory(tmgr, appl_no, regn_no, empCode);
                if (ins_dobj != null && !CommonUtils.isNullOrBlank(ins_dobj.getPolicy_no())) {
                    updateInsurance(tmgr, ins_dobj, appl_no, regn_no);
                } else {
                    throw new VahanException("Policy No Can't be Blank");
                }
            } else {
                insertIntoInsurance(tmgr, ins_dobj, appl_no, regn_no, stateCd, offCd);
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + "-" + sqle.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        }
    }

//Pawan
//Modify insertUpdateInsurance() method.
    public static boolean insertUpdateInsurance(String appl_no, String regn_no, String stateCd, int offCd, InsDobj ins_dobj) throws VahanException {
        TransactionManager tmgr = null;
        ResultSet rs1 = null;
        PreparedStatement ps = null;
        String sql = null;
        try {
            tmgr = new TransactionManager("insertUpdateInsurance");

            sql = "SELECT * FROM " + TableList.VA_INSURANCE + " where regn_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            ins_dobj.setState_cd(stateCd);
            ins_dobj.setOff_cd(offCd);
            if (rs.next()) { //if any record is exist then update otherwise insert it
                insertIntoInsuranceHistory(tmgr, regn_no);
                updateInsurance(tmgr, ins_dobj, regn_no);
                tmgr.commit();
                return true;
            } else {
                String regnNum = null;
                if (!ins_dobj.isIibData()) {
                    regnNum = checkPolicyNoUniqueness(ins_dobj.getPolicy_no(), ins_dobj.getComp_cd());
                }
                if (CommonUtils.isNullOrBlank(regnNum)) {
                    insertIntoInsurance(tmgr, ins_dobj, appl_no, regn_no, stateCd, offCd);
                    tmgr.commit();
                    return true;
                } else {
                    throw new VahanException("Duplicate policy number Against Vehicle " + regnNum + ".");
                }
            }

        } catch (VahanException ve) {
            throw ve;
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + "-" + sqle.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            }
        }
    } // end of insertUpdateInsurance

    public static void insertIntoInsuranceHistory(TransactionManager tmgr, String regn_no) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            //inserting data into vha_insurance from va_insurance
            sql = "INSERT INTO " + TableList.VHA_INSURANCE + " "
                    + "  SELECT current_timestamp as moved_on, ? as moved_by, state_cd, off_cd, appl_no, regn_no, comp_cd, ins_type, "
                    + " ins_from, ins_upto, policy_no, idv, op_dt"
                    + " FROM " + TableList.VA_INSURANCE + " where regn_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, regn_no);
            ps.executeUpdate();
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + "-" + sqle.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        }
    } // end of insertIntoInsuranceHistory

    public static void updateInsurance(TransactionManager tmgr, InsDobj dobj, String regn_no) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "UPDATE " + TableList.VA_INSURANCE
                    + "   SET comp_cd=?,"
                    + "       ins_type=?,"
                    + "       ins_from=?,"
                    + "       ins_upto=?,"
                    + "       policy_no=?,"
                    + "       idv = ?,"
                    + "       op_dt = current_timestamp"
                    + " WHERE regn_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, dobj.getComp_cd());
            ps.setInt(2, dobj.getIns_type());
            if (dobj.getIns_from() != null) {
                ps.setDate(3, new java.sql.Date(dobj.getIns_from().getTime()));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }
            if (dobj.getIns_upto() != null) {
                ps.setDate(4, new java.sql.Date(dobj.getIns_upto().getTime()));
            } else {
                ps.setNull(4, java.sql.Types.DATE);
            }
            ps.setString(5, dobj.getPolicy_no());
            ps.setLong(6, dobj.getIdv());
            ps.setString(7, regn_no);
            ps.executeUpdate();
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + "-" + sqle.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        }
    } // end of updateInsurance

    //Insert insurance details into vt_insurance
    public static void insert_ins_dtls_to_Vt_insurance(TransactionManager tmgr, String regn_no, String stateCd, int offCd) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "INSERT into " + TableList.VT_INSURANCE
                    + " SELECT ?, ?, regn_no, comp_cd, ins_type, ins_from,"
                    + " ins_upto, policy_no, idv, current_timestamp as op_dt "
                    + " FROM " + TableList.VA_INSURANCE + " where regn_no=? order by op_dt desc limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            ps.setString(3, regn_no);
            ps.executeUpdate();
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + "-" + sqle.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        }
    }

    //Insert insurance details into vh_insurance
    public static void insert_ins_dtls_to_Vh_insurance(TransactionManager tmgr, String regn_no, String stateCd, String empCd) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "INSERT into " + TableList.VH_INSURANCE + " "
                    + "Select regn_no, comp_cd, ins_type, ins_from,"
                    + " ins_upto, policy_no, current_timestamp as moved_on, ? as moved_by, state_cd, off_cd, idv "
                    + " FROM " + TableList.VT_INSURANCE + " where regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCd);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = "Delete from " + TableList.VT_INSURANCE + " where regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.executeUpdate();

        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + "-" + sqle.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        }
    }

    //Insert into vha_insurance from va_insurance with 1 Second interval
    public static void insertIntoInsuranceHistoryWithInterval(TransactionManager tmgr, String regn_no, String appl_no, String empCd) throws VahanException {
        String pname = "", pvalue = "";
        try {
            if (appl_no != null && !appl_no.isEmpty()) {
                pname = "appl_no";
                pvalue = appl_no.toUpperCase();
            }
            if (regn_no != null && !regn_no.isEmpty()) {
                pname = "regn_no";
                pvalue = regn_no.toUpperCase();
            }
            //inserting data into vha_insurance from va_insurance
            String sql = "INSERT INTO " + TableList.VHA_INSURANCE + ""
                    + "     SELECT current_timestamp + interval '1 second' as moved_on, ? as moved_by,state_cd,off_cd, "
                    + " appl_no, regn_no, comp_cd, ins_type, ins_from, ins_upto, policy_no, idv, op_dt"
                    + "       FROM " + TableList.VA_INSURANCE + " where " + pname + "= ?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCd);
            ps.setString(2, pvalue);
            ps.executeUpdate();
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + "-" + sqle.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        }
    }

    //Delete from va_insurance using regn_no
    public static void deleteFromVaInsurance(TransactionManager tmgr, String regn_no, String appl_no) throws VahanException {
        String pname = null, pvalue = null;
        try {
            if (appl_no != null && !appl_no.isEmpty()) {
                pname = "appl_no";
                pvalue = appl_no.toUpperCase();
            }
            if (regn_no != null && !regn_no.isEmpty() && !regn_no.equalsIgnoreCase("NEW") && !regn_no.equalsIgnoreCase("TEMPREG")) {
                pname = "regn_no";
                pvalue = regn_no.toUpperCase();
            }

            if (pname != null && pvalue != null) {
                String sql = "Delete from " + TableList.VA_INSURANCE + " where " + pname + "=?";
                PreparedStatement ps = tmgr.prepareStatement(sql);
                ps.setString(1, pvalue);
                ps.executeUpdate();
            }
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + "-" + sqle.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        }
    }

    public static void approvalInsurance(TransactionManager tmgr, String regn_no, String stateCd, int offCd, String empCd) throws VahanException {
        PreparedStatement ps;
        RowSet rs;
        String sql = "Select regn_no from " + TableList.VA_INSURANCE + " where regn_no=?";
        try {
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                //inserting data into vh_insurance from vt_insurance and delete from vt_insurance
                insert_ins_dtls_to_Vh_insurance(tmgr, regn_no, stateCd, empCd);
                //inserting data into vt_insurance from va_insurance
                insert_ins_dtls_to_Vt_insurance(tmgr, regn_no, stateCd, offCd);
                //Inserting data into vha_insurance from va_insurance
                insertIntoInsuranceHistoryWithInterval(tmgr, regn_no, null, empCd);
                //Delete from va_insurance
                deleteFromVaInsurance(tmgr, regn_no, null);
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + "" + sqle.getStackTrace()[0]);
            throw new VahanException("Something went wrong in Inserting/Updating Insurance Details.Please contact to the System Administrator.");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "" + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong in Inserting/Updating Insurance Details.Please contact to the System Administrator.");
        }
    }

    public static boolean validateOwnerCodeWithInsType(int ownerCode, int insType) {
        if (ownerCode == TableConstants.VEH_TYPE_GOVT || ownerCode == TableConstants.VEH_TYPE_STATE_GOVT
                || ownerCode == TableConstants.VEH_TYPE_GOVT_UNDERTAKING
                || ownerCode == TableConstants.VEH_TYPE_POLICE_DEPT
                || ownerCode == TableConstants.VEH_TYPE_STATE_TRANS_DEPT
                || ownerCode == TableConstants.VEH_TYPE_LOCAL_AUTHORITY) {
            return true;
        } else if (insType == Integer.parseInt(TableConstants.INS_TYPE_NA)) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validateInsurance(InsDobj dobj) {
        boolean insFlag = false;

        if (dobj != null) {
            if (dobj.getIns_type() == Integer.parseInt(TableConstants.INS_TYPE_NA)) {
                return true;
            }

            Date ins_upto1 = dobj.getIns_upto();
            Date dt = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.applyPattern("yyyy-MM-dd");
            String today = sdf.format(dt);
            int compare = 0;

            if (ins_upto1 != null) {
                String ins_d = sdf.format(ins_upto1);
                compare = ins_d.compareTo(today);
            }
            if (compare >= 0) {
                insFlag = true;
            } else {
                insFlag = false;
            }

        }
        return insFlag;
    }

    public static void insertIntoVtInsurance(TransactionManager tmgr, InsDobj dobj, String regn_no, String stateCd, int offCd) throws VahanException {
        try {
            if (dobj != null && dobj.getPolicy_no() != null && (dobj.isIibData() || CommonUtils.isNullOrBlank(checkPolicyNoUniqueness_without_tmgr(dobj.getPolicy_no(), dobj.getComp_cd(), tmgr)))) {
                PreparedStatement ps = null;
                String sql = null;
                sql = "INSERT INTO " + TableList.VT_INSURANCE
                        + " (state_cd,off_cd,regn_no, comp_cd, ins_type, ins_from, ins_upto, policy_no,idv,op_dt) "
                        + "  VALUES (?,?,?, ?, ?, ?, ?, ?,?,current_timestamp)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setInt(2, offCd);
                ps.setString(3, regn_no);
                ps.setInt(4, dobj.getComp_cd());
                ps.setInt(5, dobj.getIns_type());
                if (dobj.getIns_from() != null) {
                    ps.setDate(6, new java.sql.Date(dobj.getIns_from().getTime()));
                } else {
                    ps.setNull(6, java.sql.Types.DATE);
                }
                if (dobj.getIns_upto() != null) {
                    ps.setDate(7, new java.sql.Date(dobj.getIns_upto().getTime()));
                } else {
                    ps.setNull(7, java.sql.Types.DATE);
                }
                ps.setString(8, dobj.getPolicy_no());
                ps.setLong(9, dobj.getIdv());
                ps.executeUpdate();
            } else if (dobj != null && dobj.getPolicy_no() != null) {
                throw new VahanException("Duplicate Policy Number in Insurance Details.");
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + "-" + sqle.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of insurance details, Please contact to the System Administrator.");
        }
    } // end of insertIntoVtInsurance

    public void validateInsuranceForBlackListedVehicle(OwnerDetailsDobj dobj) throws VahanException {

        try {
            if (dobj.getBlackListedVehicleDobj() != null && dobj.getInsDobj() != null) {
                if (dobj.getBlackListedVehicleDobj().getComplain_type() == TableConstants.BLTheftCode
                        && dobj.getBlackListedVehicleDobj().getFirDate() != null
                        && dobj.getInsDobj().getIns_from() != null
                        && DateUtils.compareDates(dobj.getInsDobj().getIns_from(), dobj.getBlackListedVehicleDobj().getFirDate()) == 2) {
                    throw new VahanException("Vehicle is Blacklisted due to Reason [" + dobj.getBlackListedVehicleDobj().getComplainDesc().toUpperCase() + "] Dated [ " + dobj.getBlackListedVehicleDobj().getComplaindt() + " ] in State [ " + dobj.getBlackListedVehicleDobj().getStateName() + " ] at Office [ " + dobj.getBlackListedVehicleDobj().getOfficeName() + " ]. Insurance from Date Must be Less than FIR Date [" + ServerUtil.parseDateToString(dobj.getBlackListedVehicleDobj().getFirDate()) + "]");
                }
                if (dobj.getBlackListedVehicleDobj().getComplain_type() == TableConstants.BLDestroyedAccidentCode
                        && dobj.getBlackListedVehicleDobj().getComplain_dt() != null
                        && dobj.getInsDobj().getIns_from() != null
                        && DateUtils.compareDates(dobj.getInsDobj().getIns_from(), dobj.getBlackListedVehicleDobj().getComplain_dt()) == 2) {
                    throw new VahanException("Vehicle is Blacklisted due to Reason [" + dobj.getBlackListedVehicleDobj().getComplainDesc().toUpperCase() + "] Dated [ " + dobj.getBlackListedVehicleDobj().getComplaindt() + " ] in State [ " + dobj.getBlackListedVehicleDobj().getStateName() + " ] at Office [ " + dobj.getBlackListedVehicleDobj().getOfficeName() + " ]. Insurance from Date Must be Less than Complaint Date [" + ServerUtil.parseDateToString(dobj.getBlackListedVehicleDobj().getComplain_dt()) + "]");
                }
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during validation of Blacklisted Vehicle, Please contact to the System Administrator");
        }
    }
}
