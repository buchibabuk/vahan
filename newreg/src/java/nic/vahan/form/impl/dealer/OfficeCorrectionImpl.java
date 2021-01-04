package nic.vahan.form.impl.dealer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.OnlinePayDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.impl.OnlinePayImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author pramod-nicsi
 */
public class OfficeCorrectionImpl {

    private static final Logger LOGGER = Logger.getLogger(OfficeCorrectionImpl.class);
    String[] vaTables = {
        TableList.VA_OWNER,
        TableList.VA_HPA,
        TableList.VA_INSURANCE,
        TableList.VA_FITNESS,
        TableList.VA_OWNER_IDENTIFICATION,
        TableList.VA_AXLE,
        TableList.VA_RETROFITTING_DTLS,
        TableList.VA_SIDE_TRAILER,
        TableList.VA_SPEED_GOVERNOR,
        TableList.VA_TRAILER,
        TableList.VA_IMPORT_VEH,
        TableList.VA_CA,
        TableList.VA_TO,
        TableList.VA_HPT,
        TableList.VT_DSC_DOC_DETAILS,
        TableList.VA_STATUS,
        TableList.VHA_STATUS,
        TableList.VHA_DETAILS,
        TableList.VA_DETAILS,
        TableList.VP_APPL_RCPT_MAPPING,
        TableList.VPH_RCPT_CART,
        TableList.VT_FEE,
        TableList.VT_TAX,
        TableList.VT_TAX_BREAKUP,
        TableList.VT_TAX_BASED_ON,
        TableList.VP_RCPT_CART,
        TableList.VP_CART_TAX_BREAKUP,
        TableList.VPH_RCPT_CART_FAIL,
        TableList.VT_FEE_BREAKUP,
        TableList.VA_FC_PRINT,
        TableList.VH_FC_PRINT,
        TableList.VP_BANK_SUBSIDY,
        TableList.VA_OWNER_TEMP,
        TableList.VT_OTP_VERIFY,
        TableList.VA_STATUS_APPL,
        TableList.VA_DETAILS_APPL,
        TableList.VT_TEMP_APPL_TRANSACTION,
        TableList.VP_ACCOUNT,
        TableList.VP_DETAILS,
        TableList.VH_TEMP_APPROVE,
        TableList.VT_FACELESS_SERVICE
    };
    String[] vhaTables = {
        TableList.VHA_OWNER,
        TableList.VHA_HPA,
        TableList.VHA_INSURANCE,
        TableList.VHA_FITNESS,
        TableList.VHA_OWNER_IDENTIFICATION,
        TableList.VHA_AXLE,
        TableList.VHA_RETROFITTING_DTLS,
        TableList.VHA_SIDE_TRAILER,
        TableList.VHA_SPEED_GOVERNOR,
        TableList.VHA_TRAILER,
        TableList.VHA_IMPORT_VEH,
        TableList.VHA_CA,
        TableList.VHA_TO,
        TableList.VHA_HPT,
        TableList.VH_DSC_DOC_DETAILS
    };
    String[] insertUpdateTablesArr = {
        TableList.VA_OWNER,
        TableList.VA_HPA,
        TableList.VA_INSURANCE,
        TableList.VA_FITNESS,
        TableList.VA_OWNER_IDENTIFICATION,
        TableList.VA_AXLE,
        TableList.VA_RETROFITTING_DTLS,
        TableList.VA_SIDE_TRAILER,
        TableList.VA_SPEED_GOVERNOR,
        TableList.VA_TRAILER,
        TableList.VA_IMPORT_VEH,
        TableList.VA_CA,
        TableList.VA_TO,
        TableList.VA_HPT,
        TableList.VT_DSC_DOC_DETAILS
    };

    public void updateAllVhaRtoCorrection(String applNo, int newOffCd, String reason, String stateCode, Long userCode, int oldOfficeCd, String newOffName, boolean isTempFlow, OwnerDetailsDobj dobj) throws VahanException, Exception {
        TransactionManager tmgr = null;
        String[] rcptNo = null;
        try {
            tmgr = new TransactionManager("inside the RtoCorrectionImpl");
            rcptNo = this.getAllRcptNo(tmgr, applNo, stateCode, oldOfficeCd);
            for (int i = 0; i < vaTables.length; i++) {
                if (i < insertUpdateTablesArr.length && vaTables[i].contains(insertUpdateTablesArr[i])) {
                    ServerUtil.insertIntoVhaTable(tmgr, applNo, Util.getEmpCode(), vhaTables[i], vaTables[i]);
                }
                this.updateIntoVaTables(tmgr, applNo, newOffCd, vaTables[i], stateCode, oldOfficeCd, rcptNo);//update all va tables with the new off_cd
            }
            TmConfigurationDobj tmConf = Util.getTmConfiguration();
            if (tmConf != null && (tmConf.isTempFeeInNewRegis() || tmConf.isTempRegnToNewRegnAtDealer())) {
                this.updateTempDetails(tmgr, applNo, stateCode, newOffCd, oldOfficeCd, dobj);
            }
            this.insertIntoVtRtoCorrection(tmgr, newOffCd, applNo, reason, stateCode, oldOfficeCd, userCode);//insert RTO correction details
            String changeData = "[ Office changed " + ServerUtil.getOfficeName(oldOfficeCd, stateCode) + " To " + newOffName + " ]";
            ServerUtil.insertIntoVhaChangedData(tmgr, applNo, changeData); //insert into vhaChangeData
            if (isTempFlow) {
                boolean flag = this.getAndInsertVehicleDetailsForTempFlow(applNo, stateCode, newOffCd, oldOfficeCd, tmgr, dobj, tmConf.getTmConfigDealerDobj().getTempFlowInNewRegnActionCd());
                if (!flag) {
                    throw new VahanException("Problem in updating temporary registration details.");
                }
            }
            tmgr.commit();
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Data Updation Fail.");
        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }
    }//end of updateAllVhaRtoCorrection

    public void insertIntoVtRtoCorrection(TransactionManager tmgr, int offCd, String applNo, String reason, String stateCode, int oldOfficeCd, Long userCode) throws SQLException, Exception {
        PreparedStatement ps = null;
        String insertSql = "INSERT INTO " + TableList.VT_RTO_CORRECTION + "(state_cd, old_off_cd, new_off_cd, appl_no, reason, emp_cd, op_dt)"
                + " VALUES (?, ?, ?, ?, ?, ?, current_timestamp)";
        ps = tmgr.prepareStatement(insertSql);
        ps.setString(1, stateCode);
        ps.setInt(2, oldOfficeCd);
        ps.setInt(3, offCd);
        ps.setString(4, applNo);
        ps.setString(5, reason.trim());
        ps.setLong(6, userCode);
        ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

        if ("DL".equals(stateCode)) {
            insertSql = "update " + TableList.VT_FMS_SUBMIT_FILE_DTLS + " set off_cd = ? where application_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(insertSql);
            ps.setInt(1, offCd);
            ps.setString(2, applNo);
            ps.setString(3, stateCode);
            ps.setInt(4, oldOfficeCd);
            ps.executeUpdate();

            String insertSqlFms = "update " + TableList.VT_FILE_DTLS + " set off_cd = ? where application_no = ? and state_cd = ? and off_cd = ? ";
            PreparedStatement psfms = tmgr.prepareStatement(insertSqlFms);
            psfms.setInt(1, offCd);
            psfms.setString(2, applNo);
            psfms.setString(3, stateCode);
            psfms.setInt(4, oldOfficeCd);
            psfms.executeUpdate();
        }
    }//end of insertIntoVtRtoCorrection

    public void updateIntoVaTables(TransactionManager tmgr, String applNo, int offCd, String tableNameVa, String stateCode, int oldOffCd, String[] rcptNo) throws SQLException, Exception {
        PreparedStatement ps = null;
        String updateSQL = "";
        String columnName = "";

        if (tableNameVa.equals(TableList.VT_TEMP_APPL_TRANSACTION) || tableNameVa.equals(TableList.VP_ACCOUNT)
                || tableNameVa.equals(TableList.VP_DETAILS) || tableNameVa.equals(TableList.VH_TEMP_APPROVE)) {
            columnName = "transaction_no";
        } else {
            columnName = "appl_no";
        }

        if (!tableNameVa.equals(TableList.VT_FEE) && !tableNameVa.equals(TableList.VT_TAX)
                && !tableNameVa.equals(TableList.VT_TAX_BREAKUP) && !tableNameVa.equals(TableList.VT_TAX_BASED_ON)
                && !tableNameVa.equals(TableList.VT_FEE_BREAKUP)) {
            updateSQL = "UPDATE " + tableNameVa + " SET off_cd =? where " + columnName + " = ?  and state_cd= ? and off_cd = ? ";
            ps = tmgr.prepareStatement(updateSQL);
            ps.setInt(1, offCd);
            ps.setString(2, applNo);
            ps.setString(3, stateCode);
            ps.setInt(4, oldOffCd);
            ps.executeUpdate();
        } else {
            if (rcptNo != null) {
                for (String receiptNo : rcptNo) {
                    if (tableNameVa.equals(TableList.VT_FEE) || tableNameVa.equals(TableList.VT_TAX)) {
                        updateSQL = "UPDATE " + tableNameVa + " SET off_cd =? where rcpt_no = ? and state_cd= ? and off_cd = ? and payment_mode = '" + TableConstants.PAYMENT_MODE + "'";
                    } else {
                        updateSQL = "UPDATE " + tableNameVa + " SET off_cd =? where rcpt_no = ? and state_cd= ? and off_cd = ?";
                    }
                    ps = tmgr.prepareStatement(updateSQL);
                    ps.setInt(1, offCd);
                    ps.setString(2, receiptNo);
                    ps.setString(3, stateCode);
                    ps.setInt(4, oldOffCd);
                    ps.executeUpdate();
                }
            }
        }
    }

    public String[] getAllRcptNo(TransactionManager tmgr, String applNo, String stateCd, int offCd) throws Exception {
        String sql = null;
        PreparedStatement ps = null;
        String[] rcptNo = new String[3];
        try {
            sql = " SELECT distinct a.rcpt_no \n"
                    + " from vp_appl_rcpt_mapping a \n "
                    + " where a.appl_no = ? and a.state_cd = ? and a.off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            int i = 0;
            while (rs.next()) {
                rcptNo[i] = rs.getString("rcpt_no");
                i++;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in getting Receipt no.");
        }
        return rcptNo;
    }

    public void updateTempDetails(TransactionManager tmgr, String applNo, String stateCd, int offCd, int oldOffCd, OwnerDetailsDobj ownerDtlsDobj) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        try {
            sql = " update " + TableList.VT_OWNER_TEMP + " set off_cd_to = ? where state_cd = ? and (appl_no = ? OR chasi_no = ? )";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, offCd);
            ps.setString(2, stateCd);
            ps.setString(3, applNo);
            ps.setString(4, ownerDtlsDobj.getChasi_no());
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in updating office.");
        }
    }

    public static boolean isWorkDoneAtOffice(String applNo, String stateCd, int offCd, int purCd) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgrReadOnly = null;
        boolean workDoneAtOffice = false;
        try {
            tmgrReadOnly = new TransactionManagerReadOnly("isWorkDoneAtOffice");
            sql = " select appl_no from vha_status s inner join tm_user_info u on s.state_cd=u.state_cd and s.off_cd = u.off_cd and s.emp_cd = u.user_cd"
                    + " and user_catg = ? where appl_no = ? and s.state_cd = ? and s.off_cd = ? and pur_cd = ? and s.status = ? ";
            ps = tmgrReadOnly.prepareStatement(sql);
            ps.setString(1, TableConstants.USER_CATG_OFF_STAFF);
            ps.setString(2, applNo);
            ps.setString(3, stateCd);
            ps.setInt(4, offCd);
            ps.setInt(5, purCd);
            ps.setString(6, TableConstants.STATUS_COMPLETE);
            RowSet rs = tmgrReadOnly.fetchDetachedRowSet();
            if (rs.next()) {
                workDoneAtOffice = true;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting application details.");
        } finally {
            try {
                if (tmgrReadOnly != null) {
                    tmgrReadOnly.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return workDoneAtOffice;
    }

    public boolean getAndInsertVehicleDetailsForTempFlow(String applNo, String stateCd, int selectOffCd, int loginOffcd, TransactionManager tmgr, OwnerDetailsDobj dobj, int tempFlowActionCd) throws VahanException {
        PreparedStatement psmt = null;
        RowSet rs = null;
        String sql = "";
        OnlinePayDobj payDobj = null;
        String empCd = "";
        boolean flag = false;
        try {
            sql = "select c.user_cd, c.transaction_no,c.rcpt_no from va_status s inner join vph_rcpt_cart c on s.state_cd=c.state_cd and s.off_cd=c.off_cd and s.appl_no = c.appl_no and s.pur_cd=123\n"
                    + " where s.state_cd = ? and s.off_cd = ? and s.appl_no = ? ";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, stateCd);
            psmt.setInt(2, selectOffCd);
            psmt.setString(3, applNo);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                payDobj = new OnlinePayDobj();
                payDobj.setOwnerDobj(new OwnerImpl().getOwnerDobj(dobj));
                payDobj.setReceiptNo(rs.getString("rcpt_no"));
                payDobj.setTransactionNo(rs.getString("transaction_no"));
                payDobj.setApplNo(applNo);
                empCd = rs.getString("user_cd");
            }
            if (payDobj != null && !CommonUtils.isNullOrBlank(empCd) && payDobj.getOwnerDobj() != null) {
                new OnlinePayImpl().insertTemporaryRegnDetails(selectOffCd, loginOffcd, "OR", payDobj, empCd, stateCd, payDobj.getTransactionNo(), tmgr, tempFlowActionCd);
                flag = true;
            } else {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            throw new VahanException("Error in Fetching temporary flow details.");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Fetching temporary flow details.");
        }
        return flag;
    }

    public static String getRcptNoForFeePaidForPurpose(String applNo, int purCd) throws VahanException {
        TransactionManagerReadOnly tmgrReadOnly = null;
        String rcptNo = null;
        try {
            tmgrReadOnly = new TransactionManagerReadOnly("checkFeePaidForPurpose");
            String sql = "SELECT * from get_appl_rcpt_details(?) where pur_cd = ? ";
            PreparedStatement psmt = tmgrReadOnly.prepareStatement(sql);
            psmt.setString(1, applNo);
            psmt.setInt(2, purCd);
            RowSet rs = tmgrReadOnly.fetchDetachedRowSet();
            if (rs.next()) {
                rcptNo = rs.getString("rcpt_no");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in Fetching Fee details.");
        } finally {
            try {
                if (tmgrReadOnly != null) {
                    tmgrReadOnly.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return rcptNo;
    }

    public List<Status_dobj> getOnlineApplicationDetail(String applNo) throws VahanException {
        List<Status_dobj> onlineApplDataList = new ArrayList<>();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmg = null;
        String sql = null;

        try {
            tmg = new TransactionManagerReadOnly("getOnlineApplicationDetails");
            sql = "SELECT b.appl_no,b.regn_no,b.pur_cd,b.state_cd,b.off_cd FROM " + TableList.VA_STATUS_APPL
                    + "  a inner join " + TableList.VA_DETAILS_APPL
                    + "  b on a.appl_no=b.appl_no and a.pur_cd=b.pur_cd and b.appl_no=?"
                    + "  inner join " + TableList.VT_TEMP_APPL_TRANSACTION
                    + "  c on b.regn_no=c.regn_no and b.pur_cd=c.pur_cd and c.application_status=? and b.appl_no=c.transaction_no "
                    + "  WHERE b.appl_no=? and b.pur_cd in (?,?)";
            ps = tmg.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, TableConstants.ONLINE_APPL_STATUS_PAYMENT_SUCCESSFUL);
            ps.setString(3, applNo);
            ps.setInt(4, TableConstants.VM_CHANGE_OF_ADDRESS_PUR_CD);
            ps.setInt(5, TableConstants.VM_TRANSFER_OWNER_PUR_CD);

            RowSet rs = tmg.fetchDetachedRowSet();
            while (rs.next()) {
                Status_dobj status_dobj = new Status_dobj();
                status_dobj.setAppl_no(rs.getString("appl_no"));
                status_dobj.setRegn_no(rs.getString("regn_no"));
                status_dobj.setPur_cd(rs.getInt("pur_cd"));
                status_dobj.setState_cd(rs.getString("state_cd"));
                status_dobj.setOff_cd(rs.getInt("off_cd"));
                onlineApplDataList.add(status_dobj);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
        return onlineApplDataList;
    }
}
