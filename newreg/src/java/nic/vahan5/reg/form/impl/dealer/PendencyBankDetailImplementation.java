/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.form.impl.dealer;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.dealer.PendencyBankDobj;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author Kartikey Singh
 */
public class PendencyBankDetailImplementation {
    private static final Logger LOGGER = Logger.getLogger(PendencyBankDetailImplementation.class);

    public List<PendencyBankDobj> getPendencySubsidyBankDtls(String stateCd, int offCd, String applStatus) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        PendencyBankDobj bankDobj = null;
        List<PendencyBankDobj> bankDobjList = new ArrayList<>();
        try {
            tmgr = new TransactionManagerReadOnly("getPendencySubsidyBankDtls");
            String selectSql = " select b.appl_no, o.regn_no,o.owner_name, b.bank_code AS bank_cd ,b.bank_ifsc_cd AS bank_ifsc_cd, b.bank_ac_no AS bank_ac_no,  "
                    + " b.status as status,bank.descr as bank_name, b.aadhar_no as aadharno,  regexp_replace(COALESCE(o.c_add1,'') || ', ' "
                    + " || COALESCE(o.c_add2,'') || ', ' || COALESCE(o.c_add3,'') || ', ' || COALESCE(tdc.descr,'')  || ', ' || COALESCE(f.descr,'') || '-' ||"
                    + " COALESCE(o.c_pincode::varchar,'0'), '(, ){2,}', ', ') as cur_address,  o.f_name, to_char(o.regn_dt,'dd-MON-yyyy') as regndt, "
                    + " case when (o.regn_dt > '2016-03-31') then '30000' else '15000' end as subsidy_amt from " + TableList.VT_OWNER + " o  inner join " + TableList.VP_BANK_SUBSIDY + "  "
                    + " b on o.state_cd= b.state_cd  and o.off_cd=b.off_cd and o.regn_no = b.regn_no inner join " + TableList.TM_BANK + "  bank on b.bank_code = bank.bank_code "
                    + " LEFT JOIN " + TableList.TM_DISTRICT + "  tdc ON tdc.state_cd = o.c_state AND tdc.dist_cd = o.c_district "
                    + " LEFT JOIN " + TableList.TM_STATE + "  f ON f.state_code = o.c_state "
                    + " where o.state_cd = ?  and o.off_cd = ? and o.vh_class = " + TableConstants.ERICKSHAW_VCH_CLASS + "  and b.status = ? ";
            ps = tmgr.prepareStatement(selectSql);
            int i = 1;

            ps.setString(i++, stateCd);
            ps.setInt(i++, offCd);
            ps.setString(i++, applStatus);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                bankDobj = new PendencyBankDobj();
                bankDobj.setRegnNo(rs.getString("regn_no"));
                bankDobj.setOwnerName(rs.getString("owner_name"));
                bankDobj.setBankCd(rs.getString("bank_cd"));
                bankDobj.setIfscCode(rs.getString("bank_ifsc_cd"));
                bankDobj.setAccountNo(rs.getString("bank_ac_no"));
                bankDobj.setStatusCode(rs.getString("status"));
                bankDobj.setBankName(rs.getString("bank_name"));
                bankDobj.setAadharNo(rs.getString("aadharno"));
                bankDobj.setRegnDtStr(rs.getString("regndt"));
                bankDobj.setFatherName(rs.getString("f_name"));
                bankDobj.setCurAddress(rs.getString("cur_address"));
                bankDobj.setSubsidyAmount(rs.getInt("subsidy_amt"));
                if (!CommonUtils.isNullOrBlank(rs.getString("status"))) {
                    switch (rs.getString("status")) {
                        case TableConstants.SUBSIDY_PENDING_STATUS:
                            bankDobj.setStatusDescr("Pending");
                            break;
                        case TableConstants.SUBSIDY_VERIFIED_STATUS:
                            bankDobj.setStatusDescr("Verified");
                            break;
                        case TableConstants.SUBSIDY_RECORDS_PRINT_STATUS:
                            bankDobj.setStatusDescr("Under Process");
                            break;
                        case TableConstants.SUBSIDY_RECOMMENDED_STATUS:
                            bankDobj.setStatusDescr("Recommanded");
                            break;
                    }
                }
                bankDobjList.add(bankDobj);
            }
        } catch (Exception e) {
            bankDobjList = null;
            LOGGER.error(e.toString() + " getPendencyBankDtls " + e.getStackTrace()[0]);
            throw new VahanException("problem in getting bank details.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return bankDobjList;
    }

    public boolean updateBankSubsidyEditedRecords(String stateCd, int offCd, PendencyBankDobj dobj, String empCd) throws VahanException {
        TransactionManager tmgr = null;
        boolean updateStatus = false;
        try {
            tmgr = new TransactionManager("updateBankSubsidyEditedRecords");
            updateStatus = this.saveOrUpdatePendencyBankDtls(tmgr, stateCd, offCd, dobj, empCd);
            if (updateStatus) {
                tmgr.commit();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("problem in updating bank details.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return updateStatus;
    }

    public boolean saveOrUpdatePendencyBankDtls(TransactionManager tmgr, String stateCd, int offCd, PendencyBankDobj dobj, String empCd) throws VahanException {
        PreparedStatement psSelectUpdate = null;
        String sqlQuery = "";
        boolean flag = false;
        String whereCond = "";
        String searchType = " appl_no";
        int i = 1;
        try {
            if (dobj != null && !dobj.equals("")) {
                if (dobj.getRegnNo() != null && !dobj.getRegnNo().equals("")) {
                    searchType = " regn_no ";
                }
                i = 1;
                if (dobj.getAccountNo() != null && (dobj.getAadharNo() != null && !dobj.getAadharNo().equals(""))) {
                    whereCond = " (bank_ac_no = ? or aadhar_no= ?) ";
                } else if (dobj.getAccountNo() != null && (dobj.getAadharNo() == null || dobj.getAadharNo().equals(""))) {
                    whereCond = " bank_ac_no = ? ";
                }
                sqlQuery = "select * from " + TableList.VP_BANK_SUBSIDY + " where " + whereCond + " and " + searchType + " != ? ";
                PreparedStatement psExistAgainstBankOrAddhar = tmgr.prepareStatement(sqlQuery);
                if (dobj.getAccountNo() != null && (dobj.getAadharNo() != null && !dobj.getAadharNo().equals(""))) {
                    psExistAgainstBankOrAddhar.setString(i++, dobj.getAccountNo());
                    psExistAgainstBankOrAddhar.setString(i++, dobj.getAadharNo());
                } else if (dobj.getAccountNo() != null && (dobj.getAadharNo() == null || dobj.getAadharNo().equals(""))) {
                    psExistAgainstBankOrAddhar.setString(i++, dobj.getAccountNo());
                }
                if (dobj.getRegnNo() != null && !dobj.getRegnNo().equals("")) {
                    psExistAgainstBankOrAddhar.setString(i++, dobj.getRegnNo());
                } else if (dobj.getApplNo() != null && !dobj.getApplNo().equals("")) {
                    psExistAgainstBankOrAddhar.setString(i++, dobj.getApplNo());
                }

                RowSet rs2 = tmgr.fetchDetachedRowSet_No_release();
                if (!rs2.next()) {
                    i = 1;
                    sqlQuery = "select * from " + TableList.VP_BANK_SUBSIDY + " where state_cd = ? and off_cd = ? and " + searchType + " = ? ";
                    psSelectUpdate = tmgr.prepareStatement(sqlQuery);
                    psSelectUpdate.setString(i++, stateCd);
                    psSelectUpdate.setInt(i++, offCd);
                    if (dobj.getRegnNo() != null && !dobj.getRegnNo().equals("")) {
                        psSelectUpdate.setString(i++, dobj.getRegnNo());
                    } else if (dobj.getApplNo() != null && !dobj.getApplNo().equals("")) {
                        psSelectUpdate.setString(i++, dobj.getApplNo());
                    }
                    RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        sqlQuery = "INSERT INTO " + TableList.VPH_BANK_SUBSIDY + "(moved_on, moved_by, state_cd, off_cd, appl_no, regn_no, ll_dl_no, bank_code, "
                                + " bank_ifsc_cd, bank_ac_no, aadhar_no, subsidy_amount, status, op_dt) SELECT CURRENT_TIMESTAMP AS moved_on, ? AS moved_by, state_cd, off_cd, appl_no, regn_no, ll_dl_no, "
                                + " bank_code, bank_ifsc_cd, bank_ac_no, aadhar_no, subsidy_amount, status, op_dt "
                                + " FROM " + TableList.VP_BANK_SUBSIDY + " where state_cd = ? and off_cd = ? and " + searchType + " = ? ";
                        i = 1;
                        psSelectUpdate = tmgr.prepareStatement(sqlQuery);
                        psSelectUpdate.setString(i++, empCd);
                        psSelectUpdate.setString(i++, stateCd);
                        psSelectUpdate.setInt(i++, offCd);
                        if (dobj.getRegnNo() != null && !dobj.getRegnNo().equals("")) {
                            psSelectUpdate.setString(i++, dobj.getRegnNo());
                        } else if (dobj.getApplNo() != null && !dobj.getApplNo().equals("")) {
                            psSelectUpdate.setString(i++, dobj.getApplNo());
                        }
                        ServerUtil.validateQueryResult(tmgr, psSelectUpdate.executeUpdate(), psSelectUpdate);
                        flag = true;
                        i = 1;
                        sqlQuery = " UPDATE " + TableList.VP_BANK_SUBSIDY + " SET bank_code = ?, bank_ifsc_cd = ?,"
                                + " bank_ac_no = ?, aadhar_no = ?, subsidy_amount = ?, status = ? WHERE state_cd = ? and off_cd = ? and " + searchType + " = ? ";
                        psSelectUpdate = tmgr.prepareStatement(sqlQuery);
                        psSelectUpdate.setString(i++, dobj.getBankCd());
                        psSelectUpdate.setString(i++, dobj.getIfscCode().toUpperCase());
                        psSelectUpdate.setString(i++, dobj.getAccountNo());
                        if (CommonUtils.isNullOrBlank(dobj.getAadharNo())) {
                            psSelectUpdate.setString(i++, "");
                        } else {
                            psSelectUpdate.setString(i++, dobj.getAadharNo());
                        }
                        psSelectUpdate.setInt(i++, dobj.getSubsidyAmount());
                        psSelectUpdate.setString(i++, dobj.getStatusCode());
                        psSelectUpdate.setString(i++, stateCd);
                        psSelectUpdate.setInt(i++, offCd);
                        if (dobj.getRegnNo() != null && !dobj.getRegnNo().equals("")) {
                            psSelectUpdate.setString(i++, dobj.getRegnNo());
                        } else if (dobj.getApplNo() != null && !dobj.getApplNo().equals("")) {
                            psSelectUpdate.setString(i++, dobj.getApplNo());
                        }
                        psSelectUpdate.executeUpdate();
                    } else {
                        i = 1;
                        sqlQuery = " INSERT INTO " + TableList.VP_BANK_SUBSIDY + "(state_cd, off_cd, appl_no, regn_no, ll_dl_no, bank_code, bank_ifsc_cd, "
                                + "bank_ac_no, aadhar_no, status, op_dt) "
                                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
                        psSelectUpdate = tmgr.prepareStatement(sqlQuery);
                        psSelectUpdate.setString(i++, stateCd);
                        psSelectUpdate.setInt(i++, offCd);
                        psSelectUpdate.setString(i++, dobj.getApplNo());
                        if (dobj.getRegnNo() != null && !dobj.getRegnNo().equals("")) {
                            psSelectUpdate.setString(i++, dobj.getRegnNo());
                        } else {
                            psSelectUpdate.setString(i++, "NEW");
                        }
                        if (!CommonUtils.isNullOrBlank(dobj.getDlLlNo())) {
                            psSelectUpdate.setString(i++, dobj.getDlLlNo());
                        } else {
                            throw new VahanException("Blank DL/LL no.");
                        }
                        psSelectUpdate.setString(i++, dobj.getBankCd());
                        psSelectUpdate.setString(i++, dobj.getIfscCode().toUpperCase());
                        psSelectUpdate.setString(i++, dobj.getAccountNo());
                        if (!CommonUtils.isNullOrBlank(dobj.getAadharNo())) {
                            psSelectUpdate.setString(i++, dobj.getAadharNo());
                        } else {
                            psSelectUpdate.setString(i++, "");
                        }
                        psSelectUpdate.setString(i++, dobj.getStatusCode());
                        ServerUtil.validateQueryResult(tmgr, psSelectUpdate.executeUpdate(), psSelectUpdate);
                        flag = true;
                    }
                } else {
                    throw new VahanException("Duplicate aadhar/account number.");
                }
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " saveOrUpdatePendencyBankDtls " + e.getStackTrace()[0]);
            throw new VahanException("problem in updating bank subsidy details.");
        }
        return flag;
    }

    public boolean updateStatusOfPrintedRecords(List<PendencyBankDobj> pendingDobj, String stateCd, int offCd, String empCd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement psUpdate = null;
        String sqlQuery = "";
        String insertQuery = "";
        boolean flag = false;
        int i = 1;
        int counter = 0;
        int listSize = 0;
        try {
            tmgr = new TransactionManager("updateStatusOfPrintedRecords");
            sqlQuery = "update " + TableList.VP_BANK_SUBSIDY + " set status = ? where state_cd = ? and off_cd = ? and regn_no = ?";
            psUpdate = tmgr.prepareStatement(sqlQuery);
            insertQuery = "INSERT INTO " + TableList.VH_BANK_SUBSIDY_PRINT + "(state_cd, off_cd, regn_no, printed_by, printed_on, file_name)"
                    + "    VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP,  '" + stateCd + offCd + "_BankSubsidy_' || to_char(current_date,'ddMONyyyy'))";
            PreparedStatement psInsert = tmgr.prepareStatement(insertQuery);
            if (pendingDobj != null && !pendingDobj.isEmpty()) {
                listSize = pendingDobj.size();
                for (PendencyBankDobj dobj : pendingDobj) {
                    counter++;
                    i = 1;
                    psUpdate.setString(i++, TableConstants.SUBSIDY_RECORDS_PRINT_STATUS);
                    psUpdate.setString(i++, stateCd);
                    psUpdate.setInt(i++, offCd);
                    psUpdate.setString(i++, dobj.getRegnNo());
                    psUpdate.addBatch();

                    i = 1;
                    psInsert.setString(i++, stateCd);
                    psInsert.setInt(i++, offCd);
                    psInsert.setString(i++, dobj.getRegnNo());
                    psInsert.setString(i++, empCd);
                    psInsert.addBatch();
                }
            }
            if (listSize == counter) {
                int[] updateSql = psUpdate.executeBatch();
                int[] insertSql = psInsert.executeBatch();
                if (updateSql.length == listSize && insertSql.length == listSize) {
                    tmgr.commit();
                    flag = true;
                } else {
                    throw new VahanException("problem in updating status of records.");
                }
            }

        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("problem in updating status of records.");
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

    public static void updateRegnNoForBankSubsidy(TransactionManager tmgr, String regnNo, String stateCd, int offCd, String applNo) throws VahanException {
        PreparedStatement ps = null;
        String selectUpdateSql = "";
        try {
            selectUpdateSql = "select regn_no from " + TableList.VP_BANK_SUBSIDY + " where state_cd= ? and off_cd = ? and appl_no = ? ";
            ps = tmgr.prepareStatement(selectUpdateSql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            ps.setString(3, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (rs.getString("regn_no").equals("NEW")) {
                    selectUpdateSql = "update " + TableList.VP_BANK_SUBSIDY + " set regn_no = ? where state_cd= ? and off_cd = ? and appl_no = ? ";
                    ps = tmgr.prepareStatement(selectUpdateSql);
                    ps.setString(1, regnNo);
                    ps.setString(2, stateCd);
                    ps.setInt(3, offCd);
                    ps.setString(4, applNo);
                    ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("problem in updating regn. number.");
        }
    }

    public PendencyBankDobj getBankSubsidyData(String applNo, String stateCd, int offCd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        PendencyBankDobj subsidyDobj = new PendencyBankDobj();;
        try {
            tmgr = new TransactionManager("getBankDetailsData");
            String saveSQL = " SELECT * FROM " + TableList.VP_BANK_SUBSIDY + " where state_cd = ? and off_cd = ? and appl_no = ? ";
            ps = tmgr.prepareStatement(saveSQL);
            int i = 1;
            ps.setString(i++, stateCd);
            ps.setInt(i++, offCd);
            ps.setString(i++, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                subsidyDobj.setBankCd(rs.getString("bank_code"));
                subsidyDobj.setIfscCode(rs.getString("bank_ifsc_cd"));
                subsidyDobj.setAccountNo(rs.getString("bank_ac_no"));
                subsidyDobj.setAadharNo(rs.getString("aadhar_no"));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "  " + e.getStackTrace()[0]);
            throw new VahanException("problem in getting bank subsidy details.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return subsidyDobj;
    }
}
