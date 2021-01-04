/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.io.Serializable;
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
import nic.vahan.form.dobj.permit.AITPStateFeeDraftDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitAdministratorDobj;
import nic.vahan.form.dobj.permit.VmPermitRouteDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Naman Jain
 */
public class PermitAdministratorImpl implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(PermitAdministratorImpl.class);

    public PermitAdministratorDobj getPermitDtlsInDobj(String pmt_no) {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        PermitAdministratorDobj dobj = null;
        try {
            tmgr = new TransactionManager("getPermitDtlsInDobj");
            String Query = "SELECT appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                    + "       valid_upto, rcpt_no, pmt_type, pmt_catg, region_covered, \n"
                    + "       service_type, goods_to_carry, jorney_purpose, parking, replace_date,pur_cd\n"
                    + "FROM " + TableList.VT_PERMIT + " where pmt_no=?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, pmt_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new PermitAdministratorDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setPmt_no(rs.getString("pmt_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setIssue_dt(rs.getDate("issue_dt"));
                dobj.setValid_from(rs.getDate("valid_from"));
                dobj.setValid_upto(rs.getDate("valid_upto"));
                dobj.setRcpt_no(rs.getString("rcpt_no"));
                dobj.setPmt_type(rs.getInt("pmt_type"));
                dobj.setPmt_catg(rs.getInt("pmt_catg"));
                dobj.setService_type(rs.getInt("service_type"));
                dobj.setGoods_to_carry(rs.getString("goods_to_carry"));
                dobj.setJorney_purpose(rs.getString("jorney_purpose"));
                dobj.setParking(rs.getString("parking"));
                dobj.setReplace_date(rs.getDate("replace_date"));
                dobj.setRegion_covered(rs.getString("region_covered"));
                dobj.setPur_cd(rs.getInt("pur_cd"));
                if (!(dobj.getPmt_type() == (Integer.parseInt(TableConstants.NATIONAL_PERMIT))
                        || dobj.getPmt_type() == (Integer.parseInt(TableConstants.AITP))
                        || dobj.getPmt_type() == (Integer.parseInt(TableConstants.GOODS_PERMIT)))) {
                    Query = "select vt_permit_route.no_of_trips from " + TableList.vt_permit_route + "\n"
                            + " where vt_permit_route.appl_no=? limit 1";
                    tmgr = new TransactionManager("getNoOfTrips");
                    ps = tmgr.prepareStatement(Query);
                    ps.setString(1, dobj.getAppl_no());
                    RowSet rs1 = tmgr.fetchDetachedRowSet();
                    if (rs1.next()) {
                        dobj.setNo_of_trips(rs1.getInt("no_of_trips"));
                    } else {
                        dobj.setNo_of_trips(0);
                    }
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
        return dobj;
    }

    public boolean saveDetailsInVtTables(PermitAdministratorDobj dobj, PermitAdministratorDobj prvDobj, List route_code, List<PermitAdministratorDobj> compairValue, boolean showNpDetails, List<AITPStateFeeDraftDobj> paymentList, List otherOffRoute) throws VahanException {
        boolean flag = false;
        boolean auth_valid = false;
        PreparedStatement ps;
        TransactionManager tmgr = null;
        String Query;
        int pur_cd = 0;
        String pmt_no = dobj.getPmt_no();
        try {
            if (dobj.getOff_cd() == 0) {
                throw new VahanException("Permit office Details not found for this Vehicle");
            }
            tmgr = new TransactionManager("SaveDetailsInVtTables");
            for (PermitAdministratorDobj obj : compairValue) {
                if (obj.getPurpose().equalsIgnoreCase("Permit No")) {
                    pmt_no = obj.getOldValue();
                    break;
                }
            }
            if (!CommonUtils.isNullOrBlank(prvDobj.getAppl_no())
                    && !CommonUtils.isNullOrBlank(prvDobj.getRegn_no())
                    && !CommonUtils.isNullOrBlank(pmt_no)) {
                Query = "SELECT * FROM " + TableList.VT_PERMIT + " WHERE PMT_NO = ?";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, pmt_no);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    pur_cd = rs.getInt("PUR_CD");
                    Query = "INSERT INTO " + TableList.VH_PERMIT + "(\n"
                            + "            state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                            + "            valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, \n"
                            + "            service_type, goods_to_carry, jorney_purpose, parking, replace_date, \n"
                            + "            remarks, op_dt, pmt_status, reason, moved_on, moved_by)\n"
                            + "    SELECT  state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                            + "            valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, \n"
                            + "            service_type, goods_to_carry, jorney_purpose, parking, replace_date, \n"
                            + "            remarks, op_dt,?,?,CURRENT_TIMESTAMP,?\n"
                            + "  FROM " + TableList.VT_PERMIT + " where pmt_no = ? ORDER BY permit.vt_permit.valid_upto LIMIT 1";
                    ps = tmgr.prepareStatement(Query);
                    int i = 1;
                    ps.setString(i++, "RAT");
                    ps.setString(i++, "Permit Ratification");
                    ps.setString(i++, Util.getEmpCode());
                    ps.setString(i++, pmt_no);
                    ps.executeUpdate();

                    Query = "DELETE FROM " + TableList.VT_PERMIT + " where pmt_no = ?";
                    ps = tmgr.prepareStatement(Query);
                    ps.setString(1, pmt_no);
                    ps.executeUpdate();
                    deleteRouteDetailsInVtTables(dobj.getAppl_no(), tmgr);
                }
            }
            Query = "INSERT INTO " + TableList.VT_PERMIT + "(\n"
                    + "            state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                    + "            valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg,region_covered, \n"
                    + "            service_type, goods_to_carry, jorney_purpose, parking, replace_date, \n"
                    + "            remarks, op_dt)\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, \n"
                    + "            ?, ?, ?, ?, ?, ?,\n"
                    + "            ?, ?, ?, ?, ?, \n"
                    + "            ?, CURRENT_TIMESTAMP)";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, dobj.getOff_cd());
            ps.setString(i++, dobj.getAppl_no().toUpperCase());
            ps.setString(i++, dobj.getPmt_no().toUpperCase());
            ps.setString(i++, dobj.getRegn_no().toUpperCase());
            ps.setDate(i++, new java.sql.Date(dobj.getIssue_dt().getTime()));
            ps.setDate(i++, new java.sql.Date(dobj.getValid_from().getTime()));
            ps.setDate(i++, new java.sql.Date(dobj.getValid_upto().getTime()));
            ps.setString(i++, dobj.getRcpt_no().toUpperCase());
            if (pur_cd == 0) {
                ps.setInt(i++, dobj.getPur_cd());
            } else {
                ps.setInt(i++, pur_cd);
            }
            ps.setInt(i++, dobj.getPmt_type());
            ps.setInt(i++, dobj.getPmt_catg());
            ps.setString(i++, dobj.getRegion_covered());
            ps.setInt(i++, dobj.getService_type());
            ps.setString(i++, dobj.getGoods_to_carry().toUpperCase());
            ps.setString(i++, dobj.getJorney_purpose().toUpperCase());
            ps.setString(i++, dobj.getParking().toUpperCase());
            if (dobj.getReplace_date() == null) {
                ps.setNull(i++, java.sql.Types.DATE);
            } else {
                ps.setDate(i++, new java.sql.Date(dobj.getReplace_date().getTime()));
            }
            ps.setString(i++, "Administrator Entry".toUpperCase());
            ps.executeUpdate();
            insertRouteDetailsInVtTables(dobj, route_code, tmgr, compairValue, otherOffRoute);
            if (pur_cd == 0) {
                pur_cd = TableConstants.VM_PMT_FRESH_PUR_CD;
            }
            String document_id = CommonPermitPrintImpl.getPermitDocId(tmgr, dobj.getPmt_type(), pur_cd, Util.getUserStateCode());
            auth_valid = authValidOrNot(dobj.getRegn_no().toUpperCase(), dobj.getPmt_no().toUpperCase(), tmgr);
            if (document_id.contains("5") && !auth_valid && (dobj.getPmt_type() == Integer.parseInt(TableConstants.AITP) || dobj.getPmt_type() == Integer.parseInt(TableConstants.NATIONAL_PERMIT))) {
                String[] temp = document_id.split(",");
                StringBuilder stringBuilder = new StringBuilder();
                for (int j = 0; j < temp.length; j++) {
                    if (!temp[j].equalsIgnoreCase("5")) {
                        stringBuilder.append(temp[j]).append(",");
                    }
                }
                document_id = (stringBuilder.length() - 1) == stringBuilder.lastIndexOf(",") ? stringBuilder.substring(0, stringBuilder.length() - 1).toString() : stringBuilder.toString();
            }
            if (paymentList != null && paymentList.size() > 0) {
                int pur_code = (CommonUtils.isNullOrBlank(String.valueOf(paymentList.get(0).getPur_cd())) || paymentList.get(0).getPur_cd() == 0) ? dobj.getPur_cd() : paymentList.get(0).getPur_cd();
                moveVaInstrumentAitpToVtInstrumentAitp(paymentList, dobj.getAppl_no(), dobj.getRegn_no().toUpperCase(), pur_code, tmgr);
            }
            String docId = CommonPermitPrintImpl.getPermitDocIdForQuery(document_id);
            String[] beanData = {docId, dobj.getAppl_no().toUpperCase(), dobj.getRegn_no().toUpperCase()};
            if (!Util.getUserStateCode().equalsIgnoreCase("UP")) {
                CommonPermitPrintImpl.insertVaPermitPrint(tmgr, beanData);
            }
            if ("UP,DL,GJ".contains(Util.getUserStateCode()) && showNpDetails && dobj.getPmt_type() == Integer.parseInt(TableConstants.NATIONAL_PERMIT)) {
                ps = null;
                Query = "select * from  vahan4.update_np_vehicle_class_to_np_portal(?,?,?,?)";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, Util.getUserStateCode());
                ps.setString(2, dobj.getRegn_no().toUpperCase());
                ps.setString(3, String.valueOf(dobj.getPmt_catg()));
                ps.setString(4, Util.getEmpCode());
                RowSet rowSet1 = tmgr.fetchDetachedRowSet_No_release();
                if (rowSet1.next()) {
                }
            }
            flag = true;
            tmgr.commit();
        } catch (VahanException e) {
            flag = false;
            throw e;
        } catch (Exception e) {
            flag = false;
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                flag = false;
            }
        }
        return flag;
    }

    public void moveVaInstrumentAitpToVtInstrumentAitp(List<AITPStateFeeDraftDobj> paymentList, String appl_no, String regnNo, int pur_cd, TransactionManager tmgr) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        int pos = 1;
        int srNo = 1;
        sql = " INSERT INTO " + TableList.VH_INSTRUMENT_AITP + " select current_timestamp as moved_on, ? moved_by, state_cd,off_cd,appl_no, sr_no, regn_no, pay_state_cd,instrument_type,"
                + " instrument_no, instrument_dt, instrument_amt,bank_code,branch_name,payable_to,recieved_dt,op_dt,pur_cd,? FROM " + TableList.VT_INSTRUMENT_AITP
                + " WHERE regn_no = ? and state_cd=? ";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, "RAT");
        ps.setString(3, regnNo);
        ps.setString(4, Util.getUserStateCode());
        ps.executeUpdate();

        sql = " DELETE FROM " + TableList.VT_INSTRUMENT_AITP
                + " WHERE regn_no = ? and state_cd=? ";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, regnNo);
        ps.setString(2, Util.getUserStateCode());
        ps.executeUpdate();

        for (AITPStateFeeDraftDobj payment : paymentList) {
            sql = "INSERT INTO  " + TableList.VT_INSTRUMENT_AITP + "(\n"
                    + " state_cd,off_cd,appl_no, sr_no, regn_no, pay_state_cd,instrument_type, "
                    + " instrument_no, instrument_dt, instrument_amt,bank_code,branch_name,payable_to,recieved_dt,op_dt,pur_cd)\n"
                    + "    VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,current_timestamp,?)";

            ps = tmgr.prepareStatement(sql);
            ps.setString(pos++, Util.getUserStateCode());
            ps.setInt(pos++, Util.getSelectedSeat().getOff_cd());
            ps.setString(pos++, appl_no);
            ps.setInt(pos++, srNo);
            ps.setString(pos++, regnNo);
            ps.setString(pos++, payment.getPay_state_cd());
            ps.setString(pos++, payment.getInstrument_type());
            ps.setString(pos++, payment.getInstrument_no());
            ps.setDate(pos++, new java.sql.Date(payment.getInstrument_dt().getTime()));
            ps.setLong(pos++, payment.getInstrument_amt());
            ps.setString(pos++, payment.getBank_code());
            ps.setString(pos++, payment.getBranch_name());
            ps.setString(pos++, payment.getPayable_to());
            ps.setDate(pos++, new java.sql.Date(payment.getRecieved_dt().getTime()));
            ps.setInt(pos++, pur_cd);
            ps.executeUpdate();
            pos = 1;
            srNo++;
        }
    }

    public void deleteRouteDetailsInVtTables(String appl_no, TransactionManager tmgr) throws SQLException {
        String Query;
        PreparedStatement ps;
        Query = "SELECT * FROM " + TableList.vt_permit_route + " WHERE appl_no = ?";
        ps = tmgr.prepareStatement(Query);
        ps.setString(1, appl_no);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            Query = "DELETE FROM " + TableList.VT_PERMIT + " WHERE appl_no = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
        }
    }

    public void insertRouteDetailsInVtTables(PermitAdministratorDobj dobj, List route_code, TransactionManager tmgr, List<PermitAdministratorDobj> compairValue, List<VmPermitRouteDobj> otherOffRoutecode) throws SQLException {
        String query;
        PreparedStatement ps;
        for (PermitAdministratorDobj obj : compairValue) {
            if (obj.getPurpose().equalsIgnoreCase("Route Details") || obj.getPurpose().equalsIgnoreCase("Number of Trip")
                    || obj.getPurpose().equalsIgnoreCase("Other Office Route Details")) {
                if (!obj.getOldValue().equalsIgnoreCase(obj.getNewValue())) {
                    insertVtRouteToVhTables(dobj, route_code, tmgr);
                    if (!route_code.isEmpty()) {
                        for (Object s : route_code) {
                            query = "INSERT INTO " + TableList.vt_permit_route + "(\n"
                                    + "            state_cd, off_cd, appl_no, route_cd,no_of_trips)\n"
                                    + "    VALUES (?, ?, ?, ?, ?); ";
                            ps = tmgr.prepareStatement(query);
                            ps.setString(1, Util.getUserStateCode());
                            ps.setInt(2, dobj.getOff_cd());
                            ps.setString(3, dobj.getAppl_no());
                            ps.setString(4, (String) s);
                            ps.setInt(5, dobj.getNo_of_trips());
                            ps.executeUpdate();
                        }
                    }
                    if (otherOffRoutecode != null && !otherOffRoutecode.isEmpty() && otherOffRoutecode.size() > 0) {
                        for (int i = 0; i < otherOffRoutecode.size(); i++) {
                            query = "INSERT INTO " + TableList.vt_permit_route + "(\n"
                                    + "            state_cd, off_cd, appl_no, route_cd,no_of_trips)\n"
                                    + "    VALUES (?, ?, ?, ?, ?); ";
                            ps = tmgr.prepareStatement(query);
                            ps.setString(1, Util.getUserStateCode());
                            ps.setInt(2, otherOffRoutecode.get(i).getOff_code());
                            ps.setString(3, dobj.getAppl_no());
                            ps.setString(4, otherOffRoutecode.get(i).getRoute_code());
                            ps.setInt(5, dobj.getNo_of_trips());
                            ps.executeUpdate();
                        }
                    }
                }
                break;
            }
        }
    }

    public void insertVtRouteToVhTables(PermitAdministratorDobj dobj, List route_code, TransactionManager tmgr) throws SQLException {
        String query;
        PreparedStatement ps;
        query = "select * FROM permit.vt_permit_route WHERE appl_no = ?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, dobj.getAppl_no());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        while (rs.next()) {
            query = "INSERT INTO " + TableList.vh_permit_route + "(\n"
                    + "            state_cd, off_cd, appl_no, route_cd,no_of_trips,moved_on,moved_by)\n"
                    + "    VALUES (?, ?, ?, ?, ?,current_timestamp,?); ";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, rs.getString("state_cd"));
            ps.setInt(2, rs.getInt("off_cd"));
            ps.setString(3, rs.getString("appl_no"));
            ps.setString(4, rs.getString("route_cd"));
            ps.setInt(5, rs.getInt("no_of_trips"));
            ps.setString(6, Util.getEmpCode());
            ps.executeUpdate();
        }
        query = "DELETE FROM permit.vt_permit_route WHERE appl_no = ?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, dobj.getAppl_no());
        ps.executeUpdate();
    }

    public boolean checkRegn_Pmt_Appl_NoValidOrNot(String regn_no, String pmt_no, String appl_no) {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        String Query = "";
        boolean value = false;
        try {
            tmgr = new TransactionManager("checkRegn_Pmt_NoValidOrNot");
            if (CommonUtils.isNullOrBlank(regn_no)) {
                Query = "SELECT count(*) as count from " + TableList.VT_PERMIT + " where pmt_no=?";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, pmt_no);
            } else if (CommonUtils.isNullOrBlank(pmt_no)) {
                Query = "SELECT count(*) as count from " + TableList.VT_PERMIT + " where regn_no=?";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, regn_no);
            } else if (CommonUtils.isNullOrBlank(appl_no)) {
                Query = "SELECT count(*) as count from " + TableList.VT_PERMIT + " where appl_no=?";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, appl_no);
            }
            if (!CommonUtils.isNullOrBlank(Query)) {
                RowSet rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    int count = rs.getInt("count");
                    if (count >= 1) {
                        value = true;
                    }
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
        return value;
    }

    public boolean checkApplDetailStatus(String appl_no) {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        boolean flag = false;
        String query = "";
        RowSet rs;
        try {
            tmgr = new TransactionManager("checkApplDetailStatus");
            if (!CommonUtils.isNullOrBlank(appl_no)) {
                query = "select *  from " + TableList.VA_DETAILS + " a  \n"
                        + "  inner join " + TableList.VA_PERMIT_OWNER + " d on d.appl_no=a.appl_no and d.state_cd=a.state_cd \n"
                        + "  inner join " + TableList.VA_PERMIT + "  c on c.appl_no =d.appl_no and c.state_cd =d.state_cd  and c.off_cd=d.off_cd  \n"
                        + "  inner join " + TableList.VA_STATUS + " b on b.appl_no =c.appl_no and b.state_cd=c.state_cd   \n"
                        + "  where a.state_cd = ? AND a.appl_no=? AND a.regn_no=? ";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, Util.getUserStateCode());
                ps.setString(2, appl_no);
                ps.setString(3, "NEW");
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    flag = true;
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
        return flag;
    }

    public PassengerPermitDetailDobj getDetailsFromVtPmtTrans(String value) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        PassengerPermitDetailDobj passDobj = null;
        try {
            tmgr = new TransactionManagerReadOnly("getDetailsFromVtPmtTrans");
            String Query = "SELECT a.off_cd,a.pmt_no,a.regn_no,a.appl_no,a.order_no,"
                    + "to_char(a.order_dt,'DD-MON-YYYY') as order_dt,a.remarks,a.trans_pur_cd,b.descr as purpose  FROM "
                    + TableList.VT_PERMIT_TRANSACTION + " a inner join " + TableList.TM_PURPOSE_MAST + " b on a.trans_pur_cd = b.pur_cd"
                    + " where a.state_cd = ? and a.pmt_no = ? and a.pur_cd=?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, value);
            ps.setInt(3, TableConstants.VM_PMT_SURRENDER_PUR_CD);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                passDobj = new PassengerPermitDetailDobj();
                passDobj.setRegnNo(rs.getString("regn_no"));
                passDobj.setApplNo(rs.getString("appl_no"));
                passDobj.setPmt_no(rs.getString("pmt_no"));
                passDobj.setOrderDtInString(rs.getString("order_dt"));
                passDobj.setOrder_no(rs.getString("order_no"));
                passDobj.setRemarks(rs.getString("remarks"));
                passDobj.setPaction(rs.getString("purpose"));

            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return passDobj;
    }

    public List getPmtDetailsFromVtPmtTrans(String value) throws VahanException {
        String pmt_no = "";
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;

        boolean flag = false;
        List<PassengerPermitDetailDobj> passList = new ArrayList();
        PassengerPermitDetailDobj passDobj = null;
        try {
            tmgr = new TransactionManagerReadOnly("getPmtDetailsFromVtPmtTrans");
            String Query = "SELECT a.off_cd,c.off_name,a.pmt_no,a.regn_no,b.descr as purpose FROM " + TableList.VT_PERMIT_TRANSACTION
                    + " a inner join " + TableList.TM_PURPOSE_MAST + " b on a.trans_pur_cd = b.pur_cd  inner join " + TableList.TM_OFFICE
                    + " c on c.off_cd=a.off_cd and c.state_cd=a.state_cd where a.state_cd = ? and a.regn_no = ?";

            ps = tmgr.prepareStatement(Query);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, value);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                passDobj = new PassengerPermitDetailDobj();
                passDobj.setPmt_no(rs.getString("pmt_no"));
                passDobj.setPaction(rs.getString("purpose"));
                passDobj.setPermitOfficeName(rs.getString("off_name"));
                passDobj.setOff_cd(rs.getString("off_cd").trim());
                passDobj.setRegnNo(rs.getString("regn_no"));
                flag = true;
                passList.add(passDobj);
            }
            if (!flag) {
                Query = "SELECT a.off_cd,c.off_name,a.pmt_no,a.regn_no,b.descr as purpose FROM " + TableList.VT_PERMIT_TRANSACTION
                        + " a inner join " + TableList.TM_PURPOSE_MAST + " b on a.trans_pur_cd = b.pur_cd  inner join " + TableList.TM_OFFICE
                        + " c on c.off_cd=a.off_cd and c.state_cd=a.state_cd where a.state_cd = ? and a.pmt_no = ?";

                ps = tmgr.prepareStatement(Query);
                ps.setString(1, Util.getUserStateCode());
                ps.setString(2, value);
                rs = tmgr.fetchDetachedRowSet();
                while (rs.next()) {
                    passDobj = new PassengerPermitDetailDobj();
                    passDobj.setPmt_no(rs.getString("pmt_no"));
                    passDobj.setPaction(rs.getString("purpose"));
                    passDobj.setPermitOfficeName(rs.getString("off_name"));
                    passDobj.setOff_cd(rs.getString("off_cd").trim());
                    passDobj.setRegnNo(rs.getString("regn_no"));
                    passList.add(passDobj);
                }
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return passList;
    }

    public boolean authValidOrNot(String regn_no, String pmt_no, TransactionManager tmgr) {
        String Query = "";
        boolean flag = false;
        PreparedStatement ps;
        try {
            Query = "SELECT * from " + TableList.VT_PERMIT_HOME_AUTH + "  where regn_no = ? and pmt_no=? ";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regn_no);
            ps.setString(2, pmt_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                flag = true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return flag;
    }

    public boolean moveVtToVHpmtTransaction(String pmt_no, String regn_no) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        boolean flag = false;
        String query = "";
        try {
            tmgr = new TransactionManager("moveVtToVHpmtTransaction");
            query = "Select * from " + TableList.VT_PERMIT + " where state_cd=? and regn_no=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                query = "INSERT INTO " + TableList.VH_PERMIT_TRANSACTION + "(\n"
                        + "            state_cd, off_cd, appl_no, regn_no, pmt_no, pur_cd, rcpt_no, \n"
                        + "            trans_pur_cd, remarks, order_no, order_dt, order_by, new_regn_no, \n"
                        + "            user_cd, op_dt, moved_on, moved_by,excempted_flag)\n"
                        + " SELECT     state_cd, off_cd, appl_no, regn_no, pmt_no, pur_cd, rcpt_no, \n"
                        + "            trans_pur_cd, remarks, order_no, order_dt, order_by, new_regn_no, \n"
                        + "            user_cd, op_dt,CURRENT_TIMESTAMP,?,excempted_flag \n"
                        + "  FROM " + TableList.VT_PERMIT_TRANSACTION + " WHERE state_cd=? and pmt_no = ? and regn_no=? and trans_pur_cd <> 43 ";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, Util.getUserStateCode());
                ps.setString(3, pmt_no);
                ps.setString(4, regn_no);
                ps.executeUpdate();

                query = "DELETE FROM " + TableList.VT_PERMIT_TRANSACTION + " WHERE state_cd=? and pmt_no = ? and regn_no=? and trans_pur_cd <> 43 ";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, Util.getUserStateCode());
                ps.setString(2, pmt_no);
                ps.setString(3, regn_no);
                ps.executeUpdate();
                flag = true;

            } else {
                query = "Select * from " + TableList.VH_PERMIT + " where state_cd=? and regn_no=? order by moved_on desc limit 1";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, Util.getUserStateCode());
                ps.setString(2, regn_no);
                RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
                if (!rs1.next()) {
                    query = "INSERT INTO " + TableList.VH_PERMIT_TRANSACTION + "(\n"
                            + "            state_cd, off_cd, appl_no, regn_no, pmt_no, pur_cd, rcpt_no, \n"
                            + "            trans_pur_cd, remarks, order_no, order_dt, order_by, new_regn_no, \n"
                            + "            user_cd, op_dt, moved_on, moved_by,excempted_flag)\n"
                            + " SELECT     state_cd, off_cd, appl_no, regn_no, pmt_no, pur_cd, rcpt_no, \n"
                            + "            trans_pur_cd, remarks, order_no, order_dt, order_by, new_regn_no, \n"
                            + "            user_cd, op_dt,CURRENT_TIMESTAMP,?,excempted_flag \n"
                            + "  FROM " + TableList.VT_PERMIT_TRANSACTION + " WHERE state_cd=? and pmt_no = ? and regn_no=? ";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, Util.getEmpCode());
                    ps.setString(2, Util.getUserStateCode());
                    ps.setString(3, pmt_no);
                    ps.setString(4, regn_no);
                    ps.executeUpdate();

                    query = "DELETE FROM " + TableList.VT_PERMIT_TRANSACTION + " WHERE state_cd=? and pmt_no = ? and regn_no=?";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, Util.getUserStateCode());
                    ps.setString(2, pmt_no);
                    ps.setString(3, regn_no);
                    ps.executeUpdate();
                    flag = true;

                }
            }
            tmgr.commit();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " PMT No : " + pmt_no + " Regn No : " + regn_no + e.getStackTrace()[0]);
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
