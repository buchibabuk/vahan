/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.CounterSignatureDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import org.apache.log4j.Logger;

/**
 *
 * @author nicsi
 */
public class CounterSignatureAuthImpl {

    private static final Logger LOGGER = Logger.getLogger(CounterSignatureImpl.class);

    public String createApplication(PermitOwnerDetailDobj ownDobj, CounterSignatureDobj countDobj) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String applNo = "";
        try {
            tmgr = new TransactionManager("CounterSignatureImpl-createApplication");
            applNo = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
            String Query = "INSERT INTO " + TableList.VA_PERMIT_COUNTERSIGNATURE_AUTHORIZATION + "(\n"
                    + "            state_cd, off_cd, state_cd_to, off_cd_to, appl_no, regn_no, rcpt_no,\n"
                    + "            pur_cd, pmt_no, pmt_type, pmt_catg, period_mode, period,\n"
                    + "            count_valid_upto, pmt_valid_from, pmt_valid_upto,op_dt)\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, \n"
                    + "            ?, ?, ?, ?, ?,?,?,?,?, current_timestamp)";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            ps.setString(i++, countDobj.getState_to());
            ps.setInt(i++, countDobj.getOff_cd_to());
            ps.setString(i++, applNo);
            ps.setString(i++, countDobj.getRegn_no().toUpperCase());
            ps.setString(i++, countDobj.getRcpt_no());
            ps.setInt(i++, countDobj.getPur_cd());
            ps.setString(i++, countDobj.getPmt_no().toUpperCase());
            ps.setInt(i++, countDobj.getPmt_type());
            ps.setInt(i++, countDobj.getPmt_catg());
            ps.setString(i++, countDobj.getPeriod_mode());
            ps.setInt(i++, countDobj.getPeriod());
            if (countDobj.getCount_valid_upto() != null) {
                ps.setDate(i++, new java.sql.Date(countDobj.getCount_valid_upto().getTime()));
            } else {
                ps.setDate(i++, null);
            }
            ps.setDate(i++, new java.sql.Date(countDobj.getValid_from().getTime()));
            ps.setDate(i++, new java.sql.Date(countDobj.getValid_upto().getTime()));
            ps.executeUpdate();
            Status_dobj status = new Status_dobj();
            status.setAppl_no(applNo);
            status.setPur_cd(countDobj.getPur_cd());
            status.setFlow_slno(1);
            status.setFile_movement_slno(1);
            status.setAction_cd(TableConstants.TM_ROLE_PMT_COUNTER_SIGNATURE_AUTH_ENTRY);
            if (Util.getSession().getAttribute("selected_role_cd") == null) {
                status.setEmp_cd(0);
            } else {
                status.setEmp_cd(Long.parseLong(Util.getEmpCode()));
            }
            status.setRegn_no(countDobj.getRegn_no().toUpperCase());
            status.setOffice_remark("");
            status.setPublic_remark("");
            status.setStatus("N");
            status.setState_cd(Util.getUserStateCode());
            status.setOff_cd(Util.getSelectedSeat().getOff_cd());
            ServerUtil.fileFlowForNewApplication(tmgr, status);
            status.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
            status.setStatus(TableConstants.STATUS_COMPLETE);
            status.setAppl_no(applNo);
            status.setOffice_remark("");
            status.setPublic_remark("");
            ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, applNo, TableConstants.TM_ROLE_PMT_COUNTER_SIGNATURE_AUTH_ENTRY, countDobj.getPur_cd(), null, tmgr);
            ServerUtil.fileFlow(tmgr, status);
            tmgr.commit();
        } catch (VahanException e) {
            applNo = "";
            throw e;
        } catch (Exception e) {
            applNo = "";
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                applNo = "";
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return applNo;
    }

    public CounterSignatureDobj getDeatilForApplNo(String applNo) {
        TransactionManager tmgr = null;
        CounterSignatureDobj coutDobj = null;
        try {
            tmgr = new TransactionManager("getDeatilForApplNo");
            String Query = "SELECT state_cd_to,a.off_cd_to,a.pur_cd, a.pmt_no, a.pmt_type, period_mode, period,count_valid_upto,b.valid_from,b.valid_upto\n"
                    + " FROM " + TableList.VA_PERMIT_COUNTERSIGNATURE_AUTHORIZATION + " a \n"
                    + "left JOIN " + TableList.VT_PERMIT + " b on a.regn_no = b.regn_no and a.state_cd=b.state_cd \n"
                    + " where a.appl_no = ?";
            PreparedStatement ps = tmgr.prepareStatement(Query);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                coutDobj = new CounterSignatureDobj();
                coutDobj.setState_to(rs.getString("state_cd_to"));
                coutDobj.setOff_cd_to(rs.getInt("off_cd_to"));
                coutDobj.setPur_cd(rs.getInt("pur_cd"));
                coutDobj.setPmt_no(rs.getString("pmt_no"));
                coutDobj.setPmt_type(rs.getInt("pmt_type"));
                coutDobj.setPeriod_mode(rs.getString("period_mode"));
                coutDobj.setPeriod(rs.getInt("period"));
                coutDobj.setCount_valid_upto(rs.getDate("count_valid_upto"));
                coutDobj.setValid_from(rs.getDate("valid_from"));
                coutDobj.setValid_upto(rs.getDate("valid_upto"));

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
        return coutDobj;
    }

    public void updateVaCounterSignAuth(CounterSignatureDobj countDobj, TransactionManager tmgr, String applNo) throws SQLException {

        String Query = "UPDATE " + TableList.VA_PERMIT_COUNTERSIGNATURE_AUTHORIZATION + " \n"
                + "   SET state_cd_to=?, off_cd_to=?, \n"
                + "       period_mode=?, period=?, count_valid_upto=? \n"
                + " WHERE  appl_no=? ";
        PreparedStatement ps = tmgr.prepareStatement(Query);
        int i = 1;
        ps.setString(i++, countDobj.getState_to());
        ps.setInt(i++, countDobj.getOff_cd_to());
        ps.setString(i++, countDobj.getPeriod_mode());
        ps.setInt(i++, countDobj.getPeriod());
        if (countDobj.getCount_valid_upto() != null) {
            ps.setDate(i++, new java.sql.Date(countDobj.getCount_valid_upto().getTime()));
        } else {
            ps.setDate(i++, null);
        }
        ps.setString(i++, applNo);
        ps.executeUpdate();
    }

    public void moveVaToVha(TransactionManager tmgr, String applNo) throws SQLException {
        String Query = "INSERT INTO " + TableList.VHA_PERMIT_COUNTERSIGNATURE_AUTHORIZATION + "(\n"
                + "            moved_on, moved_by, state_cd, off_cd, state_cd_to, off_cd_to, \n"
                + "            appl_no, regn_no, rcpt_no, pur_cd, pmt_no, pmt_type, pmt_catg, \n"
                + "            period_mode, period, count_valid_upto, pmt_valid_from,\n"
                + "            pmt_valid_upto,  op_dt )\n"
                + "    SELECT  current_timestamp,?,state_cd, off_cd, state_cd_to, off_cd_to, appl_no, regn_no, rcpt_no, \n"
                + "            pur_cd, pmt_no, pmt_type, pmt_catg, period_mode, period, \n"
                + "            count_valid_upto, pmt_valid_from, pmt_valid_upto, op_dt \n"
                + "  FROM " + TableList.VA_PERMIT_COUNTERSIGNATURE_AUTHORIZATION + " where appl_no = ?";
        PreparedStatement ps = tmgr.prepareStatement(Query);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, applNo);
        ps.executeUpdate();
    }

//    public void moveVtToVh(TransactionManager tmgr, String regnNo, boolean allowRenewalCS) throws SQLException {
//        String Query = "INSERT INTO permit.vh_permit_countersignature(\n"
//                + "            state_cd, off_cd, state_cd_from, off_cd_from, appl_no, regn_no, \n"
//                + "            rcpt_no, pur_cd, count_sign_no, pmt_no, pmt_type, count_valid_from, \n"
//                + "            count_valid_upto, pmt_valid_from, pmt_valid_upto, owner_name, \n"
//                + "            mobile_no, email_id, vh_class, seat_cap, unld_wt, ld_wt, vch_catg, \n"
//                + "            owner_ctg, f_name, c_add1, c_add2, c_add3, c_district, c_pincode, \n"
//                + "            c_state, p_add1, p_add2, p_add3, p_district, p_pincode, p_state, \n"
//                + "            op_dt, moved_on, moved_by,region,pmt_status,pmt_reason)\n"
//                + "   SELECT   state_cd, off_cd, state_cd_from, off_cd_from, appl_no, regn_no, \n"
//                + "            rcpt_no, pur_cd, count_sign_no, pmt_no, pmt_type, count_valid_from, \n"
//                + "            count_valid_upto, pmt_valid_from, pmt_valid_upto, owner_name, \n"
//                + "            mobile_no, email_id, vh_class, seat_cap, unld_wt, ld_wt, vch_catg, \n"
//                + "            owner_ctg, f_name, c_add1, c_add2, c_add3, c_district, c_pincode, \n"
//                + "            c_state, p_add1, p_add2, p_add3, p_district, p_pincode, p_state, \n"
//                + "            op_dt,CURRENT_TIMESTAMP,?,region,?,? \n"
//                + "  FROM permit.vt_permit_countersignature where regn_no = ? and state_cd= ?";
//        PreparedStatement ps = tmgr.prepareStatement(Query);
//        ps.setString(1, Util.getEmpCode());
//        if (allowRenewalCS) {
//            ps.setString(2, "REN");
//            ps.setString(3, "RENEWAL OF CSPERMIT");
//        } else {
//            ps.setString(2, "");
//            ps.setString(3, "");
//        }
//        ps.setString(4, regnNo);
//        ps.setString(5, Util.getUserStateCode());
//        ps.executeUpdate();
//    }
    public void insertIntoVt(TransactionManager tmgr, String regnNo, String applNo, String count_sign_auth_no) throws SQLException {
        PreparedStatement ps = null;
        String Query = "INSERT INTO " + TableList.VT_PERMIT_COUNTERSIGNATURE_AUTHORIZATION + "(\n"
                + "            state_cd, off_cd, state_cd_to, off_cd_to, appl_no, regn_no, rcpt_no, \n"
                + "            pur_cd, count_sign_auth_no, pmt_no, pmt_type, pmt_catg, period_mode, \n"
                + "            period, count_valid_from, count_valid_upto, pmt_valid_from, pmt_valid_upto, op_dt)\n"
                + "   SELECT   state_cd, off_cd, state_cd_to, off_cd_to, appl_no, regn_no, rcpt_no,\n"
                + "            pur_cd,?,pmt_no, pmt_type, pmt_catg, period_mode, period, pmt_valid_from, \n"
                + "            count_valid_upto, pmt_valid_from, pmt_valid_upto,  CURRENT_TIMESTAMP \n"
                + "  FROM " + TableList.VA_PERMIT_COUNTERSIGNATURE_AUTHORIZATION + " where regn_no = ? AND appl_no = ?";

        ps = tmgr.prepareStatement(Query);
        ps.setString(1, count_sign_auth_no);
        ps.setString(2, regnNo);
        ps.setString(3, applNo);
        ps.executeUpdate();
    }

    public void deleteVatables(TransactionManager tmgr, String applNo, String regnNo, String tableName) throws SQLException {
        String Query = "DELETE FROM " + tableName + " where state_cd=? and ";
        if (CommonUtils.isNullOrBlank(applNo)) {
            Query += "regn_no = ?";
        } else if (CommonUtils.isNullOrBlank(regnNo)) {
            Query += "appl_no = ?";
        }
        PreparedStatement ps = tmgr.prepareStatement(Query);
        ps.setString(1, Util.getUserStateCode());
        if (CommonUtils.isNullOrBlank(applNo)) {
            ps.setString(2, regnNo);
        } else if (CommonUtils.isNullOrBlank(regnNo)) {
            ps.setString(2, applNo);
        }

        ps.executeUpdate();
    }

    public String verificationStage(String appl_no, Status_dobj status_dobj, PermitOwnerDetailDobj ownDobj, CounterSignatureDobj countDobj, String CompairChange) throws VahanException {
        TransactionManager tmgr = null;
        String return_location = "";
        try {
            tmgr = new TransactionManager("verificationStage");
            if (!CommonUtils.isNullOrBlank(CompairChange)) {
                moveVaToVha(tmgr, appl_no);
                updateVaCounterSignAuth(countDobj, tmgr, appl_no);
                insertIntoVhaChangedData(tmgr, appl_no, CompairChange);
            }
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            ServerUtil.fileFlow(tmgr, status_dobj);
            tmgr.commit();
            return_location = "seatwork";
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There is some problem while saving data");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                return_location = "";
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return return_location;
    }

    public String approvalStage(String appl_no, String regn_no, Status_dobj status_dobj, PermitOwnerDetailDobj ownDobj, CounterSignatureDobj countDobj) throws VahanException {
        TransactionManager tmgr = null;
        String return_location = "";
        String count_sign_auth_no = "";
        try {
            tmgr = new TransactionManager("approvalStage");
            moveVaToVha(tmgr, appl_no);
            if (countDobj.getPur_cd() == TableConstants.VM_PMT_COUNTER_SIGNATURE_AUTH_PUR_CD) {
                count_sign_auth_no = ServerUtil.getUniquePermitNo(tmgr, Util.getUserStateCode(), Util.getUserOffCode(), 0, 0, "G");
            }
            if (CommonUtils.isNullOrBlank(count_sign_auth_no)) {
                throw new VahanException("Counter Signature No. not genrated");
            } else if (count_sign_auth_no.length() > 25) {
                throw new VahanException("Counter Signature No length greater than column space. Please contact the administrator");
            } else {
                insertIntoVt(tmgr, regn_no, appl_no, count_sign_auth_no);
                deleteVatables(tmgr, appl_no, null, TableList.VA_PERMIT_COUNTERSIGNATURE_AUTHORIZATION);
                String docId = CommonPermitPrintImpl.getPermitDocIdForQuery(CommonPermitPrintImpl.getPermitDocId(tmgr, 0, countDobj.getPur_cd(), Util.getUserStateCode()));
                String[] beanData = {docId, appl_no, regn_no};
                CommonPermitPrintImpl.insertVaPermitPrint(tmgr, beanData);
                String message = "Counter signature Auth No " + count_sign_auth_no + " generated against Application No " + appl_no;
                ServerUtil.insertUsersTransactionMessage(message, 1, tmgr);
                status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
                ServerUtil.fileFlow(tmgr, status_dobj);
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                tmgr.commit();
                return_location = "seatwork";
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There is some problem while saving data");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                return_location = "";
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return return_location;
    }

    public Map getCounterSignatureAuthorizationDetails(String Appl_no) {
        Map<String, String> countSign = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        RowSet rs;
        PreparedStatement ps;
        String query;
        try {
            tmgr = new TransactionManager("getCounterSignatureAuthorizationDetails");
            query = "      SELECT a.state_cd,a.appl_no,a.regn_no,a.count_sign_auth_no,a.pmt_no,to_char(count_valid_upto,'dd-Mon-yyyy') as count_valid_upto , to_char(a.pmt_valid_from,'dd-Mon-yyyy') as pmt_valid_from, \n"
                    + "    to_char(pmt_valid_upto,'dd-Mon-yyyy') as pmt_valid_upto,a.state_cd_to,a.off_cd_to,  \n"
                    + "    to_char(current_date,'DD-Mon-YYYY') as currentDate,b.descr as state_name, \n"
                    + "    a.state_cd as state_code,a.off_cd,d.descr as state_name_to,e.owner_name,e.f_name,e.c_add1,e.c_add2,e.c_add3,e.c_district_name,e.c_pincode,g.descr as pmt_type,h.off_name,j.floc,j.via,j.tloc \n"
                    + "    from " + TableList.VT_PERMIT_COUNTERSIGNATURE_AUTHORIZATION + " a\n"
                    + "    left outer join " + TableList.TM_STATE + "  b on b.state_code = a.state_cd \n"
                    + "    left outer join " + TableList.TM_STATE + "  d on d.state_code = a.state_cd_to \n"
                    + "    left outer join " + TableList.VIEW_VV_OWNER + "  e on e.state_cd = a.state_cd and a.regn_no = e.regn_no \n"
                    + "    left join " + TableList.VT_PERMIT + " f  on a.regn_no = f.regn_no \n"
                    + "    left join " + TableList.VM_PERMIT_TYPE + " g on g.code = f.pmt_type \n"
                    + "    left join " + TableList.TM_OFFICE + " h on h.state_cd = a.state_cd and h.off_cd = a.off_cd \n"
                    + "    left join " + TableList.vt_permit_route + " i on i.state_cd = f.state_cd and i.appl_no = f.appl_no \n"
                    + "    left join " + TableList.VM_ROUTE_MASTER + " j on j.state_cd = i.state_cd and j.off_cd = i.off_cd and j.code = i.route_cd \n"
                    + "    where a.appl_no =?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Appl_no);
            rs = tmgr.fetchDetachedRowSet();
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    countSign.put(rsmd.getColumnName(i), rs.getString(i));
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
        return countSign;
    }

    public List<CounterSignatureDobj> getPreviouseCSAuthDtls(List<CounterSignatureDobj> preTempDataTable, String regnNo, int purCd) {
        String Query;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        CounterSignatureDobj dobj = null;
        preTempDataTable = new ArrayList<>();
        try {
            tmgr = new TransactionManagerReadOnly("getPreviouseCSAuthDtls");
            Query = "select * from " + TableList.VT_PERMIT_COUNTERSIGNATURE_AUTHORIZATION + " where state_cd=? and regn_no=? AND pur_cd = ? ";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, regnNo);
            ps.setInt(3, purCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new CounterSignatureDobj();
                dobj.setPmt_no(rs.getString("pmt_no"));
                dobj.setCount_valid_upto(rs.getDate("count_valid_upto"));
                dobj.setCs_auth_no(rs.getString("count_sign_auth_no"));
                dobj.setState_to(rs.getString("state_cd_to"));
                preTempDataTable.add(dobj);
            }
        } catch (SQLException e) {
            LOGGER.error(regnNo + " - " + e);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return preTempDataTable;
    }
}
