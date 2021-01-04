/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import nic.vahan.form.bean.permit.PermitRouteList;
import nic.vahan.form.dobj.DupDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.DupCounterSignatureDobj;
import nic.vahan.form.dobj.permit.CounterSignatureDobj;
import nic.vahan.form.dobj.permit.OtherStateVchCounterSignDobj;
import nic.vahan.form.impl.DupImpl;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import org.apache.log4j.Logger;

/**
 *
 * @author hcl
 */
public class CounterSignatureImpl {

    private static final Logger LOGGER = Logger.getLogger(CounterSignatureImpl.class);

    public String createApplication(PermitOwnerDetailDobj ownDobj, CounterSignatureDobj countDobj, List route_code) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String applNo = "";
        try {
            tmgr = new TransactionManager("CounterSignatureImpl-createApplication");
            applNo = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
            String Query = "INSERT INTO permit.va_permit_countersignature(\n"
                    + "            state_cd, off_cd, state_cd_from, off_cd_from, appl_no, regn_no, \n"
                    + "            pur_cd, pmt_no, pmt_type, period_mode, period, count_valid_upto,valid_from, \n"
                    + "            valid_upto, owner_name, mobile_no, email_id, vh_class, seat_cap, \n"
                    + "            unld_wt, ld_wt, vch_catg, owner_ctg, f_name, c_add1, c_add2, \n"
                    + "            c_add3, c_district, c_pincode, c_state, p_add1, p_add2, p_add3, \n"
                    + "            p_district, p_pincode, p_state,op_dt,region)\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?, \n"
                    + "            ?, ?, ?, ?, ?, ?, ?, \n"
                    + "            ?, ?, ?, ?, ?, ?, \n"
                    + "            ?, ?, ?, ?, ?, ?, ?, \n"
                    + "            ?, ?, ?, ?, ?, ?, ?, \n"
                    + "            ?, ?, ?, current_timestamp,?)";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            ps.setString(i++, countDobj.getState_cd_from());
            ps.setInt(i++, countDobj.getOff_cd_from());
            ps.setString(i++, applNo);
            ps.setString(i++, countDobj.getRegn_no().toUpperCase());
            ps.setInt(i++, countDobj.getPur_cd());
            ps.setString(i++, countDobj.getPmt_no().toUpperCase());
            ps.setInt(i++, countDobj.getPmt_type());
            ps.setString(i++, countDobj.getPeriod_mode());
            ps.setInt(i++, countDobj.getPeriod());
            if (countDobj.getCount_valid_upto() != null) {
                ps.setDate(i++, new java.sql.Date(countDobj.getCount_valid_upto().getTime()));
            } else {
                ps.setDate(i++, null);
            }
            ps.setDate(i++, new java.sql.Date(countDobj.getValid_from().getTime()));
            ps.setDate(i++, new java.sql.Date(countDobj.getValid_upto().getTime()));
            ps.setString(i++, ownDobj.getOwner_name());
            ps.setLong(i++, ownDobj.getMobile_no());
            ps.setString(i++, ownDobj.getEmail_id());
            ps.setInt(i++, ownDobj.getVh_class());
            ps.setInt(i++, ownDobj.getSeat_cap());
            ps.setInt(i++, ownDobj.getUnld_wt());
            ps.setInt(i++, ownDobj.getLd_wt());
            ps.setString(i++, ownDobj.getVch_catg());
            ps.setInt(i++, ownDobj.getOwner_catg());
            ps.setString(i++, ownDobj.getF_name());
            ps.setString(i++, ownDobj.getC_add1());
            ps.setString(i++, ownDobj.getC_add2());
            ps.setString(i++, ownDobj.getC_add3());
            ps.setInt(i++, ownDobj.getC_district());
            ps.setInt(i++, ownDobj.getC_pincode());
            ps.setString(i++, ownDobj.getC_state());
            ps.setString(i++, ownDobj.getP_add1());
            ps.setString(i++, ownDobj.getP_add2());
            ps.setString(i++, ownDobj.getP_add3());
            ps.setInt(i++, ownDobj.getP_district());
            ps.setInt(i++, ownDobj.getP_pincode());
            ps.setString(i++, ownDobj.getP_state());
            if (countDobj.getRegion() == 0) {
                ps.setInt(i++, Integer.parseInt("-1"));
            } else {
                ps.setInt(i++, countDobj.getRegion());
            }
            ps.executeUpdate();

            insert_into_vaPermitRoute(applNo, countDobj, route_code, tmgr);

            Status_dobj status = new Status_dobj();
            status.setAppl_no(applNo);
            status.setPur_cd(countDobj.getPur_cd());
            status.setFlow_slno(1);
            status.setFile_movement_slno(1);
            status.setAction_cd(TableConstants.TM_ROLE_PMT_COUNTER_SIGNATURE_ENTRY);
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
            ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, applNo, TableConstants.TM_ROLE_PMT_COUNTER_SIGNATURE_ENTRY, countDobj.getPur_cd(), null, tmgr);
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
        CounterSignatureDobj couDobj = null;
        try {
            tmgr = new TransactionManager("getDeatilForApplNo");
            String Query = "SELECT state_cd_from,off_cd_from ,rcpt_no, pur_cd, pmt_no, pmt_type, period_mode, period,count_valid_upto,valid_from,valid_upto,region,maker_name,model_name,a.regn_no \n"
                    + " FROM permit.va_permit_countersignature a\n"
                    + " left outer join vv_owner b on a.regn_no=b.regn_no \n"
                    + " where a.appl_no = ?";
            PreparedStatement ps = tmgr.prepareStatement(Query);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                couDobj = new CounterSignatureDobj();
                couDobj.setState_cd_from(rs.getString("state_cd_from"));
                couDobj.setOff_cd_from(rs.getInt("off_cd_from"));
                couDobj.setPmt_no(rs.getString("pmt_no"));
                couDobj.setPmt_type(rs.getInt("pmt_type"));
                couDobj.setPeriod_mode(rs.getString("period_mode"));
                couDobj.setPeriod(rs.getInt("period"));
                couDobj.setCount_valid_upto(rs.getDate("count_valid_upto"));
                couDobj.setValid_from(rs.getDate("valid_from"));
                couDobj.setValid_upto(rs.getDate("valid_upto"));
                couDobj.setMaker_name(rs.getString("maker_name"));
                couDobj.setModel_name(rs.getString("model_name"));
                couDobj.setRegion(rs.getInt("region"));
                couDobj.setPur_cd(rs.getInt("pur_cd"));
                couDobj.setRegn_no(rs.getString("regn_no"));
                Query = "select va_permit_route.no_of_trips from " + TableList.va_permit_route + "\n"
                        + " where va_permit_route.appl_no=? limit 1";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, applNo);
                RowSet rs1 = tmgr.fetchDetachedRowSet();
                if (rs1.next()) {
                    couDobj.setNo_of_trips(rs1.getInt("no_of_trips"));
                } else {
                    couDobj.setNo_of_trips(0);
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
        return couDobj;
    }

    public void updateVaCounterSig(PermitOwnerDetailDobj ownDobj, CounterSignatureDobj countDobj, TransactionManager tmgr, String applNo) throws SQLException {

        String Query = "UPDATE permit.va_permit_countersignature\n"
                + "   SET state_cd_from=?, off_cd_from=?, \n"
                + "       pmt_no=?, pmt_type=?, period_mode=?, \n"
                + "       period=?, count_valid_upto=?, valid_from=?, valid_upto=?, owner_name=?, mobile_no=?, \n"
                + "       email_id=?, vh_class=?, seat_cap=?, unld_wt=?, ld_wt=?, vch_catg=?, \n"
                + "       owner_ctg=?, f_name=?, c_add1=?, c_add2=?, c_add3=?, c_district=?, \n"
                + "       c_pincode=?, c_state=?, p_add1=?, p_add2=?, p_add3=?, p_district=?, \n"
                + "       p_pincode=?, p_state=?, region=? \n"
                + " WHERE  appl_no=? ";
        PreparedStatement ps = tmgr.prepareStatement(Query);
        int i = 1;
        ps.setString(i++, countDobj.getState_cd_from());
        ps.setInt(i++, countDobj.getOff_cd_from());
        ps.setString(i++, countDobj.getPmt_no().toUpperCase());
        ps.setInt(i++, countDobj.getPmt_type());
        ps.setString(i++, countDobj.getPeriod_mode());
        ps.setInt(i++, countDobj.getPeriod());
        if (countDobj.getCount_valid_upto() != null) {
            ps.setDate(i++, new java.sql.Date(countDobj.getCount_valid_upto().getTime()));
        } else {
            ps.setDate(i++, null);
        }
        ps.setDate(i++, new java.sql.Date(countDobj.getValid_from().getTime()));
        ps.setDate(i++, new java.sql.Date(countDobj.getValid_upto().getTime()));
        ps.setString(i++, ownDobj.getOwner_name());
        ps.setLong(i++, ownDobj.getMobile_no());
        ps.setString(i++, ownDobj.getEmail_id());
        ps.setInt(i++, ownDobj.getVh_class());
        ps.setInt(i++, ownDobj.getSeat_cap());
        ps.setInt(i++, ownDobj.getUnld_wt());
        ps.setInt(i++, ownDobj.getLd_wt());
        ps.setString(i++, ownDobj.getVch_catg());
        ps.setInt(i++, ownDobj.getOwner_catg());
        ps.setString(i++, ownDobj.getF_name());
        ps.setString(i++, ownDobj.getC_add1());
        ps.setString(i++, ownDobj.getC_add2());
        ps.setString(i++, ownDobj.getC_add3());
        ps.setInt(i++, ownDobj.getC_district());
        ps.setInt(i++, ownDobj.getC_pincode());
        ps.setString(i++, ownDobj.getC_state());
        ps.setString(i++, ownDobj.getP_add1());
        ps.setString(i++, ownDobj.getP_add2());
        ps.setString(i++, ownDobj.getP_add3());
        ps.setInt(i++, ownDobj.getP_district());
        ps.setInt(i++, ownDobj.getP_pincode());
        ps.setString(i++, ownDobj.getP_state());
        ps.setInt(i++, countDobj.getRegion());
        ps.setString(i++, applNo);
        ps.executeUpdate();
    }

    public void moveVaToVha(TransactionManager tmgr, String applNo) throws SQLException {
        String Query = "INSERT INTO permit.vha_permit_countersignature(\n"
                + "            state_cd, off_cd, state_cd_from, off_cd_from, appl_no, regn_no, \n"
                + "            rcpt_no, pur_cd, pmt_no, pmt_type, period_mode, period, count_valid_upto, valid_from, \n"
                + "            valid_upto, owner_name, f_name, mobile_no, email_id, vh_class, \n"
                + "            seat_cap, unld_wt, ld_wt, vch_catg, owner_ctg, c_add1, c_add2, \n"
                + "            c_add3, c_district, c_pincode, c_state, p_add1, p_add2, p_add3, \n"
                + "            p_district, p_pincode, p_state,op_dt, moved_on, moved_by,region)\n"
                + "    SELECT  state_cd, off_cd, state_cd_from, off_cd_from, appl_no, regn_no, \n"
                + "            rcpt_no, pur_cd, pmt_no, pmt_type, period_mode, period, count_valid_upto, valid_from, \n"
                + "            valid_upto, owner_name, f_name, mobile_no, email_id, vh_class, seat_cap, \n"
                + "            unld_wt, ld_wt, vch_catg, owner_ctg, c_add1, c_add2, \n"
                + "            c_add3, c_district, c_pincode, c_state, p_add1, p_add2, p_add3, \n"
                + "            p_district, p_pincode, p_state,op_dt, Current_timestamp,?,region \n"
                + "  FROM permit.va_permit_countersignature where appl_no = ?";
        PreparedStatement ps = tmgr.prepareStatement(Query);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, applNo);
        ps.executeUpdate();
    }

    public void moveVtToVh(TransactionManager tmgr, String regnNo, boolean allowRenewalCS) throws SQLException {
        String Query = "INSERT INTO permit.vh_permit_countersignature(\n"
                + "            state_cd, off_cd, state_cd_from, off_cd_from, appl_no, regn_no, \n"
                + "            rcpt_no, pur_cd, count_sign_no, pmt_no, pmt_type, count_valid_from, \n"
                + "            count_valid_upto, pmt_valid_from, pmt_valid_upto, owner_name, \n"
                + "            mobile_no, email_id, vh_class, seat_cap, unld_wt, ld_wt, vch_catg, \n"
                + "            owner_ctg, f_name, c_add1, c_add2, c_add3, c_district, c_pincode, \n"
                + "            c_state, p_add1, p_add2, p_add3, p_district, p_pincode, p_state, \n"
                + "            op_dt, moved_on, moved_by,region,pmt_status,pmt_reason)\n"
                + "   SELECT   state_cd, off_cd, state_cd_from, off_cd_from, appl_no, regn_no, \n"
                + "            rcpt_no, pur_cd, count_sign_no, pmt_no, pmt_type, count_valid_from, \n"
                + "            count_valid_upto, pmt_valid_from, pmt_valid_upto, owner_name, \n"
                + "            mobile_no, email_id, vh_class, seat_cap, unld_wt, ld_wt, vch_catg, \n"
                + "            owner_ctg, f_name, c_add1, c_add2, c_add3, c_district, c_pincode, \n"
                + "            c_state, p_add1, p_add2, p_add3, p_district, p_pincode, p_state, \n"
                + "            op_dt,CURRENT_TIMESTAMP,?,region,?,? \n"
                + "  FROM permit.vt_permit_countersignature where regn_no = ? and state_cd= ?";
        PreparedStatement ps = tmgr.prepareStatement(Query);
        ps.setString(1, Util.getEmpCode());
        if (allowRenewalCS) {
            ps.setString(2, "REN");
            ps.setString(3, "RENEWAL OF CSPERMIT");
        } else {
            ps.setString(2, "");
            ps.setString(3, "");
        }
        ps.setString(4, regnNo);
        ps.setString(5, Util.getUserStateCode());
        ps.executeUpdate();
    }

    public void insertIntoVt(TransactionManager tmgr, CounterSignatureDobj countDobj, String regnNo, String applNo, String count_sign_no) throws SQLException {
        PreparedStatement ps = null;
        Date validFrom = null;
        Date validUpto = null;
        if (("Y").equalsIgnoreCase(countDobj.getPeriod_mode())) {
            Calendar cal = Calendar.getInstance();
            validFrom = cal.getTime();
            cal.add(Calendar.YEAR, countDobj.getPeriod());
            cal.add(Calendar.DAY_OF_YEAR, -1);
            validUpto = cal.getTime();
        }
        if (("M").equalsIgnoreCase(countDobj.getPeriod_mode())) {
            Calendar cal = Calendar.getInstance();
            validFrom = cal.getTime();
            cal.add(Calendar.MONTH, countDobj.getPeriod());
            cal.add(Calendar.DAY_OF_MONTH, -1);
            validUpto = cal.getTime();
        }

        String Query = "SELECT * FROM permit.va_permit_countersignature where regn_no = ? AND appl_no = ?";
        ps = tmgr.prepareStatement(Query);
        ps.setString(1, regnNo);
        ps.setString(2, applNo);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            Date countValidUpto = rs.getDate("count_valid_upto");
            if (countValidUpto != null && countValidUpto.compareTo(validUpto) <= 0) {
                validUpto = countValidUpto;
            }
        }

        Query = "INSERT INTO permit.vt_permit_countersignature(\n"
                + "            state_cd, off_cd, state_cd_from, off_cd_from, appl_no, regn_no, \n"
                + "            rcpt_no, pur_cd, count_sign_no, pmt_no, pmt_type, count_valid_from, \n"
                + "            count_valid_upto, pmt_valid_from, pmt_valid_upto, owner_name, \n"
                + "            mobile_no, email_id, vh_class, seat_cap, unld_wt, ld_wt, vch_catg, \n"
                + "            owner_ctg, f_name, c_add1, c_add2, c_add3, c_district, c_pincode, \n"
                + "            c_state, p_add1, p_add2, p_add3, p_district, p_pincode, p_state, \n"
                + "            op_dt,region)\n"
                + "   SELECT   state_cd, off_cd, state_cd_from, off_cd_from, appl_no, regn_no, \n"
                + "            rcpt_no, pur_cd,?, pmt_no, pmt_type, ?,\n"
                + "            ?, valid_from,valid_upto, owner_name,\n"
                + "            mobile_no, email_id, vh_class, seat_cap,unld_wt, ld_wt, vch_catg,\n"
                + "            owner_ctg, f_name, c_add1, c_add2, c_add3, c_district, c_pincode,\n"
                + "            c_state, p_add1, p_add2, p_add3,p_district, p_pincode, p_state,\n"
                + "            CURRENT_TIMESTAMP,region \n"
                + "  FROM permit.va_permit_countersignature where regn_no = ? AND appl_no = ?";
        ps = tmgr.prepareStatement(Query);
        ps.setString(1, count_sign_no);
        ps.setDate(2, new java.sql.Date(validFrom.getTime()));
        ps.setDate(3, new java.sql.Date(validUpto.getTime()));
        ps.setString(4, regnNo);
        ps.setString(5, applNo);
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
                updateVaCounterSig(ownDobj, countDobj, tmgr, appl_no);
                insertIntoVhaChangedData(tmgr, appl_no, CompairChange);
            }
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            ServerUtil.fileFlow(tmgr, status_dobj);
            tmgr.commit();
            return_location = "seatwork";
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);;
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

    public String approvalStage(String appl_no, String regn_no, Status_dobj status_dobj, PermitOwnerDetailDobj ownDobj, CounterSignatureDobj countDobj, boolean allowRenewalCS) throws VahanException {
        TransactionManager tmgr = null;
        String return_location = "";
        String count_sign_no = "";
        try {
            tmgr = new TransactionManager("approvalStage");
            moveVaToVha(tmgr, appl_no);
            if (countDobj.getPur_cd() == TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD) {
                count_sign_no = ServerUtil.getUniquePermitNo(tmgr, Util.getUserStateCode(), Util.getUserOffCode(), 0, 0, "C");
            } else if (countDobj.getPur_cd() == TableConstants.VM_PMT_RENEW_COUNTER_SIGNATURE_PUR_CD) {
                count_sign_no = getCounterSignatureNumberFromVt(regn_no, Util.getUserStateCode());
                if (CommonUtils.isNullOrBlank(count_sign_no) && "TN".contains(Util.getUserStateCode())) {
                    count_sign_no = ServerUtil.getUniquePermitNo(tmgr, Util.getUserStateCode(), Util.getUserOffCode(), 0, 0, "C");
                }
            }
            if (CommonUtils.isNullOrBlank(count_sign_no)) {
                throw new VahanException("Counter Signature No. not genrated");
            } else if (count_sign_no.length() > 25) {
                throw new VahanException("Counter Signature No length greater than column space. Please contact the administrator");
            } else {
                if (countDobj.getPur_cd() == TableConstants.VM_PMT_RENEW_COUNTER_SIGNATURE_PUR_CD || !allowRenewalCS) {
                    moveVtToVh(tmgr, regn_no, allowRenewalCS);
                    deleteVatables(tmgr, null, regn_no, "permit.vt_permit_countersignature");
                }
                insertIntoVt(tmgr, countDobj, regn_no, appl_no, count_sign_no);
                deleteVatables(tmgr, appl_no, null, "permit.va_permit_countersignature");
                String docId = CommonPermitPrintImpl.getPermitDocIdForQuery(CommonPermitPrintImpl.getPermitDocId(tmgr, 0, countDobj.getPur_cd(), Util.getUserStateCode()));
                String[] beanData = {docId, appl_no, regn_no};
                CommonPermitPrintImpl.insertVaPermitPrint(tmgr, beanData);

                insertIntoVtPermitRouteFromVa(tmgr, appl_no);
                insertVHA_RouteandUpdateVa_Route(tmgr, null, appl_no, null);

                String message = "Counter signature No " + count_sign_no + " generated against Application No " + appl_no;
                ServerUtil.insertUsersTransactionMessage(message, 1, tmgr);
                status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
                ServerUtil.fileFlow(tmgr, status_dobj);
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

    public void stayOnTheSameStage(String appl_no, PermitOwnerDetailDobj ownDobj, CounterSignatureDobj countDobj, String CompairChange) throws VahanException {
        TransactionManager tmgr = null;

        try {
            tmgr = new TransactionManager("stayOnTheSameStage");
            if (!CommonUtils.isNullOrBlank(CompairChange)) {
                moveVaToVha(tmgr, appl_no);
                updateVaCounterSig(ownDobj, countDobj, tmgr, appl_no);
                insertIntoVhaChangedData(tmgr, appl_no, CompairChange);
                tmgr.commit();
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("There is some problem while saving data");
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

    public Map getSourcesRouteMap(String Appl_no, String TableName, String state_cd, int off_cd) {
        Map<String, String> routeMap = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("getRouteMap");
            String query;
            query = "select code, code || ' - ' || Floc || ' TO ' || Tloc as descr from " + TableList.VM_ROUTE_MASTER + " Where code not in (SELECT route_cd from " + TableName + " where appl_no=?) AND state_cd = ? AND off_cd = ? ORDER BY descr";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Appl_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                routeMap.put(rs.getString(1), rs.getString(2));
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
        return routeMap;
    }

    public Map getTargetRouteMap(String Appl_no, String TableName, String state_cd, int off_cd) {
        Map<String, String> priRouteMap = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("getTargetRouteMap");
            String query;
            query = "select code, code || ' - ' || Floc || ' TO ' || Tloc as descr from " + TableList.VM_ROUTE_MASTER + " Where code in (SELECT route_cd from " + TableName + " where appl_no=?)AND state_cd = ? AND off_cd = ? ORDER BY descr";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Appl_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                priRouteMap.put(rs.getString(1), rs.getString(2));
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
        return priRouteMap;
    }

    public String getRouteVia(List route_code, String state_cd, int off_cd) {
        String via = "";
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("get Route Via");
            for (Object code : route_code) {
                String Query = "Select code ||': '|| via ||'.<br/><br/> ' as via from " + TableList.VM_ROUTE_MASTER + " where code = ? AND State_cd = ? AND off_cd = ?";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, (String) code);
                ps.setString(2, state_cd);
                ps.setInt(3, off_cd);
                RowSet rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    via += rs.getString("via");
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
        return via;
    }

    public int getRouteLength(List route_code, String appl_no, String tableName, String state_cd, int off_cd) throws VahanException {
        int length = 0;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        String Query = null;
        try {
            tmgr = new TransactionManager("get Route Via");
            if (CommonUtils.isNullOrBlank(appl_no) || CommonUtils.isNullOrBlank(tableName)) {
                for (Object code : route_code) {
                    Query = "Select rlength from " + TableList.VM_ROUTE_MASTER + " where code = ? AND State_cd = ? AND off_cd = ?";
                    ps = tmgr.prepareStatement(Query);
                    ps.setString(1, (String) code);
                    ps.setString(2, state_cd);
                    ps.setInt(3, off_cd);
                    RowSet rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        length += rs.getInt("rlength");
                    }
                }
            } else if (route_code == null) {
                Query = "SELECT sum(rlength) as rlength from " + TableList.VM_ROUTE_MASTER + " where code in (SELECT route_cd from " + tableName + " where appl_no = ?) and state_cd = ? and off_Cd = ?";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, appl_no);
                ps.setString(2, state_cd);
                ps.setInt(3, off_cd);
                RowSet rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    length += rs.getInt("rlength");
                }
            }
        } catch (SQLException e) {
            throw new VahanException("Record not found.Please contact the system administrator");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return length;
    }

    public String getRouteViaRouteList(String appl_no, String state_cd, String TableName, int off_cd) {
        String via = "";
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("get Route Via");
            String Query = "Select code ||': '|| via ||'.<br/><br/> ' as via from " + TableList.VM_ROUTE_MASTER + " where code in (SELECT route_cd from " + TableName + " where appl_no=?) AND State_cd = ? AND off_cd = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                via += rs.getString("via");
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
        return via;
    }

    public void insert_into_vaPermitRoute(String appl_no, CounterSignatureDobj countDobj, List route_code, TransactionManager tmgr) throws SQLException {
        String query;
        PreparedStatement ps;
        if (route_code.isEmpty() && countDobj.getNo_of_trips() != 0) {

            query = "INSERT INTO " + TableList.va_permit_route + " (\n"
                    + "            state_cd, off_cd, appl_no, route_cd,no_of_trips)\n"
                    + "    VALUES (?, ?, ?,?, ?); ";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserOffCode());
            ps.setString(3, appl_no);
            ps.setString(4, "");
            ps.setInt(5, countDobj.getNo_of_trips());
            ps.executeUpdate();

        } else if (!route_code.isEmpty()) {
            for (Object s : route_code) {
                query = "INSERT INTO " + TableList.va_permit_route + "(\n"
                        + "            state_cd, off_cd, appl_no, route_cd,no_of_trips)\n"
                        + "    VALUES (?, ?, ?, ?, ?); ";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, Util.getUserOffCode());
                ps.setString(3, appl_no);
                ps.setString(4, (String) s);
                ps.setInt(5, countDobj.getNo_of_trips());
                ps.executeUpdate();
            }
        }
    }

    public void insertVHA_RouteandUpdateVa_Route(TransactionManager tmgr, CounterSignatureDobj dobj, String appl_no, List route_code) throws SQLException {
        String query;
        PreparedStatement ps;
        query = "SELECT * FROM " + TableList.va_permit_route + " where appl_no = ? ";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, appl_no);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            query = "INSERT INTO " + TableList.VHA_PERMIT_ROUTE + "(\n"
                    + "            state_cd, off_cd, appl_no, route_cd, no_of_trips, moved_on, moved_by)\n"
                    + "    SELECT state_cd, off_cd, appl_no, route_cd, no_of_trips,CURRENT_TIMESTAMP,?\n"
                    + "  FROM " + TableList.va_permit_route + " where appl_no = ? ";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, appl_no);
            ps.executeUpdate();

            query = "DELETE  from  " + TableList.va_permit_route + "  WHERE appl_no=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, appl_no);
            ps.executeUpdate();
        }
        if (dobj != null && !route_code.isEmpty()) {
            for (Object s : route_code) {
                query = "INSERT INTO  " + TableList.va_permit_route + " (\n"
                        + "            state_cd, off_cd, appl_no, route_cd,no_of_trips)\n"
                        + "    VALUES (?, ?, ?, ?, ?); ";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, Util.getUserOffCode());
                ps.setString(3, appl_no);
                if (s instanceof String) {
                    ps.setString(4, (String) s);
                } else if (s instanceof PermitRouteList) {
                    ps.setString(4, ((PermitRouteList) s).getKey());
                }
                ps.setInt(5, dobj.getNo_of_trips());

                ps.executeUpdate();
            }
        }
    }

    public void insertIntoVtPermitRouteFromVa(TransactionManager tmgr, String appl_no) throws SQLException {
        String query;
        PreparedStatement ps;
        query = "INSERT INTO " + TableList.vt_permit_route + " (state_cd,off_cd,appl_no,route_cd,no_of_trips)\n"
                + "SELECT state_cd,off_cd,appl_no,route_cd,no_of_trips FROM " + TableList.va_permit_route + "\n"
                + "WHERE appl_no=? ";
        ps = tmgr.prepareStatement(query);

        ps.setString(1, appl_no);
        ps.executeUpdate();
    }

    public static DupCounterSignatureDobj getCounterSignatureDetailFromVt(String value, String stateCd) throws VahanException {
        String regnNo = "";
        String Query;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs = null;
        DupCounterSignatureDobj countDobj = null;
        try {
            tmgr = new TransactionManager("getRegnNoinVtPermit");
            Query = "SELECT * FROM " + TableList.VT_PERMIT_COUNTERSIGNATURE + " where regn_no = ? and state_cd=?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, value);
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                countDobj = new DupCounterSignatureDobj();
                countDobj.setAppl_no(rs.getString("appl_no"));
                countDobj.setCountSignNo(rs.getString("count_sign_no"));
                countDobj.setState_cd(rs.getString("state_cd"));
                countDobj.setOff_cd(rs.getInt("off_cd"));
                countDobj.setState_cd_from(rs.getString("state_cd_from"));
                countDobj.setOff_cd_from(rs.getInt("off_cd_from"));
                countDobj.setCount_valid_from(rs.getDate("count_valid_from"));
                countDobj.setCount_valid_upto(rs.getDate("count_valid_upto"));
                countDobj.setPur_cd(rs.getInt("pur_cd"));
                countDobj.setRegn_no(rs.getString("regn_no"));
                countDobj.setRegion(rs.getInt("region"));
                countDobj.setPmt_no(rs.getString("pmt_no"));
                countDobj.setPmt_type(rs.getInt("pmt_type"));
                countDobj.setValid_from(rs.getDate("pmt_valid_from"));
                countDobj.setValid_upto(rs.getDate("pmt_valid_upto"));

            } else {
                Query = "SELECT * FROM " + TableList.VT_PERMIT_COUNTERSIGNATURE + " where count_sign_no = ? and state_cd=?";

                ps = tmgr.prepareStatement(Query);
                ps.setString(1, value);
                ps.setString(2, Util.getUserStateCode());

                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    countDobj = new DupCounterSignatureDobj();
                    countDobj.setAppl_no(rs.getString("appl_no"));
                    countDobj.setCountSignNo(rs.getString("count_sign_no"));
                    countDobj.setState_cd(rs.getString("state_cd"));
                    countDobj.setOff_cd(rs.getInt("off_cd"));
                    countDobj.setState_cd_from(rs.getString("state_cd_from"));
                    countDobj.setOff_cd_from(rs.getInt("off_cd_from"));
                    countDobj.setCount_valid_from(rs.getDate("count_valid_from"));
                    countDobj.setCount_valid_upto(rs.getDate("count_valid_upto"));
                    countDobj.setPur_cd(rs.getInt("pur_cd"));
                    countDobj.setRegn_no(rs.getString("regn_no"));
                    countDobj.setRegion(rs.getInt("region"));
                    countDobj.setPmt_no(rs.getString("pmt_no"));
                    countDobj.setPmt_type(rs.getInt("pmt_type"));
                    countDobj.setValid_from(rs.getDate("pmt_valid_from"));
                    countDobj.setValid_upto(rs.getDate("pmt_valid_upto"));
                }
            }

            if (countDobj != null) {
                Query = "SELECT no_of_trips FROM " + TableList.vt_permit_route + " where appl_no = ? and state_cd=? and off_cd=?";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, countDobj.getAppl_no());
                ps.setString(2, countDobj.getState_cd());
                ps.setInt(3, countDobj.getOff_cd());
                RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
                if (rs1.next()) {
                    countDobj.setNo_of_trips(rs1.getInt("no_of_trips"));
                }
            }

            if (CommonUtils.isNullOrBlank(countDobj.getCountSignNo())) {
                throw new VahanException("Details not found, Please enter correct registration/permit mumber.");
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
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
        return countDobj;
    }

    public String getCountrSignatureDocCode(String state_cd) {
        String docList = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getCountrSignatureDocCode");
            String Query = "SELECT doc_id from " + TableList.VM_PERMIT_DOC_STATE_MAP + " where state_cd = ? and pur_cd = ? ";
            PreparedStatement ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, state_cd);
            ps.setInt(i++, TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                docList = rs.getString("doc_id");
            }
        } catch (SQLException e) {
            docList = "";
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
        return docList;
    }

    public String insertIntoVa_Dup(DupDobj dobj) {
        String appl_no = "";
        TransactionManager tmgr = null;

        try {
            tmgr = new TransactionManager("insertIntoVa_Dup");
            appl_no = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
            dobj.setAppl_no(appl_no);
            dobj.setState_cd(Util.getUserStateCode());
            dobj.setOff_cd(Util.getUserOffCode());

            DupImpl.insertIntoDup(tmgr, dobj);
            if (!CommonUtils.isNullOrBlank(dobj.getPmtDoc())) {
                insertIntoVaDupDocList(appl_no, dobj.getRegn_no(), dobj.getPmtDoc(), tmgr);
            }

            Status_dobj status = new Status_dobj();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            String dt = sdf.format(new Date());
            status.setAppl_dt(dt);
            status.setAppl_no(appl_no);
            status.setPur_cd(dobj.getPur_cd());
            status.setFlow_slno(1);
            status.setFile_movement_slno(1);
            status.setAction_cd(TableConstants.TM_ROLE_PMT_DUPCOUNTER_SIGNATURE_ENTRY);
            status.setEmp_cd(Long.parseLong(Util.getEmpCode()));
            status.setRegn_no(dobj.getRegn_no());
            status.setOffice_remark("");
            status.setPublic_remark("");
            status.setStatus("N");
            status.setState_cd(dobj.getState_cd());
            status.setOff_cd(dobj.getOff_cd());
            ServerUtil.fileFlowForNewApplication(tmgr, status);

            status.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
            status.setStatus(TableConstants.STATUS_COMPLETE);
            status.setAppl_no(appl_no);
            status.setOffice_remark("");
            status.setPublic_remark("");

            ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, appl_no,
                    TableConstants.TM_ROLE_PMT_FEE, dobj.getPur_cd(), null, tmgr);
            ServerUtil.fileFlow(tmgr, status);
            tmgr.commit();
        } catch (Exception e) {
            appl_no = "";
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
        return appl_no;
    }

    public static void insertIntoVaDupDocList(String applNo, String regnNo, String docList, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        try {
            String Query = "INSERT INTO permit.va_dup_doclist(appl_no, regn_no, doc_id, op_dt) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, applNo);
            ps.setString(2, regnNo);
            ps.setString(3, docList);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new VahanException(e.getMessage());
        }
    }

    public CounterSignatureDobj getRegnNoDetailsFromReservation(String state_from, String regn_no) {
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManagerReadOnly tmgr = null;
        CounterSignatureDobj countDobj = null;;
        try {
            tmgr = new TransactionManagerReadOnly("getRegnNoDetailsFromReservation");
            String sql = "select * from " + TableList.VT_OTHER_STATE_VCH_FOR_CS + " where state_from=? and regn_no=? and state_cd=? and off_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, state_from);
            psmt.setString(2, regn_no);
            psmt.setString(3, Util.getUserStateCode());
            psmt.setInt(4, Util.getUserOffCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                countDobj = new CounterSignatureDobj();
                countDobj.setPmt_no(rs.getString("pmt_no"));
                countDobj.setValid_upto(rs.getDate("pmt_valid_upto"));
            }
        } catch (Exception ex) {
            countDobj = null;
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return countDobj;
    }

    public boolean getRegnNoinVtPmtCounterSignature(String value, String stateCd) throws VahanException {
        boolean flag = false;
        String Query;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs = null;
        try {
            tmgr = new TransactionManager("getRegnNoinVtPmtCounterSignature");
            Query = "SELECT regn_no FROM " + TableList.VT_PERMIT_COUNTERSIGNATURE + " where regn_no = ? ";
            if (!CommonUtils.isNullOrBlank(stateCd)) {
                Query += " and state_cd = ?";
            }
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, value);
            if (!CommonUtils.isNullOrBlank(stateCd)) {
                ps.setString(2, stateCd);
            }
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                flag = true;
            } else {
                Query = "SELECT regn_no FROM " + TableList.VT_PERMIT_COUNTERSIGNATURE + " where pmt_no = ?";
                if (!CommonUtils.isNullOrBlank(stateCd)) {
                    Query += " and state_cd = ?";
                }
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, value);
                if (!CommonUtils.isNullOrBlank(stateCd)) {
                    ps.setString(2, stateCd);
                }
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    flag = true;
                }
            }

        } catch (Exception e) {
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
        return flag;
    }

    public CounterSignatureDobj getCounterSignatureDetailsFromVt(String value, String stateCd) throws VahanException {
        String Query;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs = null;
        CounterSignatureDobj countDobj = null;
        try {
            tmgr = new TransactionManager("getCounterSignatureDetailsFromVt");
            Query = "SELECT * FROM " + TableList.VT_PERMIT_COUNTERSIGNATURE + " where regn_no = ? and state_cd=?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, value);
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                countDobj = new CounterSignatureDobj();
                countDobj.setAppl_no(rs.getString("appl_no"));
                countDobj.setState_cd_from(rs.getString("state_cd_from"));
                countDobj.setOff_cd_from(rs.getInt("off_cd_from"));
                countDobj.setCount_valid_upto(rs.getDate("count_valid_upto"));
                countDobj.setPur_cd(rs.getInt("pur_cd"));
                countDobj.setRegn_no(rs.getString("regn_no"));
                countDobj.setRegion(rs.getInt("region"));
                countDobj.setPmt_no(rs.getString("pmt_no"));
                countDobj.setPmt_type(rs.getInt("pmt_type"));
                countDobj.setValid_from(rs.getDate("pmt_valid_from"));
                countDobj.setValid_upto(rs.getDate("pmt_valid_upto"));
            }

            if (countDobj != null) {
                Query = "SELECT no_of_trips FROM " + TableList.vt_permit_route + " where appl_no = ? and state_cd=? and off_cd=?";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, countDobj.getAppl_no());
                ps.setString(2, rs.getString("state_cd"));
                ps.setInt(3, rs.getInt("off_cd"));
                RowSet rs1 = tmgr.fetchDetachedRowSet();
                if (rs1.next()) {
                    countDobj.setNo_of_trips(rs1.getInt("no_of_trips"));
                }
            }

        } catch (Exception e) {
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
        return countDobj;
    }

    public String getCounterSignatureNumberFromVt(String value, String stateCd) throws VahanException {
        String countSignNo = "";
        String Query;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        RowSet rs = null;

        try {
            tmgr = new TransactionManagerReadOnly("getCounterSignatureNumberFromVt");
            Query = "SELECT count_sign_no FROM " + TableList.VT_PERMIT_COUNTERSIGNATURE + " where regn_no = ? and state_cd=?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, value);
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                countSignNo = rs.getString("count_sign_no");
            }

        } catch (Exception e) {
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
        return countSignNo;
    }

    public boolean insertVchDetailsForCS(String state_from, String regn_no, String pmtNo, Date pmtValidUpto, int vacancyNo, int pur_cd) {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("insertVchDetailsForCS");
            String sql = " insert into " + TableList.VT_OTHER_STATE_VCH_FOR_CS + "(state_cd,off_cd,state_from,regn_no,pmt_no,pmt_valid_upto,vacancy_no,op_dt,emp_cd,pur_cd)"
                    + "\n"
                    + " values(?,?,?,?,?,?,?,CURRENT_DATE,?,?)";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, Util.getUserStateCode());
            psmt.setInt(2, Util.getUserOffCode());
            psmt.setString(3, state_from.toUpperCase());
            psmt.setString(4, regn_no.toUpperCase());
            psmt.setString(5, pmtNo.toUpperCase());
            psmt.setDate(6, new java.sql.Date(pmtValidUpto.getTime()));
            psmt.setInt(7, vacancyNo);
            psmt.setString(8, Util.getEmpCode());
            psmt.setInt(9, pur_cd);
            psmt.executeUpdate();

            sql = "update " + TableList.VM_COUNTER_SIGN_RESERVATION + " set running_no=running_no+1 where state_cd=? and state_from=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, Util.getUserStateCode());
            psmt.setString(2, state_from);
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public List<OtherStateVchCounterSignDobj> getCsReservationData() {
        RowSet rs;
        PreparedStatement ps;
        TransactionManager tmgr = null;
        List<OtherStateVchCounterSignDobj> list = null;
        try {
            tmgr = new TransactionManager("getCsReservationData");
            list = new ArrayList<>();
            String sql = "select b.descr as state_from,a.regn_no,a.pmt_no,to_char(a.pmt_valid_upto,'DD-MON-YYYY') as valid_upto,a.vacancy_no,c.descr as purpose from " + TableList.VT_OTHER_STATE_VCH_FOR_CS + " a inner join " + TableList.TM_STATE + " b on a.state_from=b.state_code inner join " + TableList.TM_PURPOSE_MAST + " c on a.pur_cd=c.pur_cd where state_cd = ? and off_cd=? order by state_from,vacancy_no,regn_no ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserOffCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                OtherStateVchCounterSignDobj dobj = new OtherStateVchCounterSignDobj();
                dobj.setState_cd_from(rs.getString("state_from"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setPmt_no(rs.getString("pmt_no"));
                dobj.setPmtvaliduptostring(rs.getString("valid_upto"));
                dobj.setVacancy_no(rs.getInt("vacancy_no"));
                dobj.setPurpose(rs.getString("purpose"));
                list.add(dobj);
            }
        } catch (Exception ex) {
            list = null;
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return list;

    }

    public int getRunningVacancyNumber(String state_cd_from) {
        int running_vacancy_no = 0;
        RowSet rs;
        PreparedStatement ps;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getRunningVacancyNumber");
            String sql = "select COALESCE(max(vacancy_no),0)+1 as running_vacancy_no from " + TableList.VT_OTHER_STATE_VCH_FOR_CS + " where state_cd=? and off_cd=? and state_from=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserOffCode());
            ps.setString(3, state_cd_from);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                running_vacancy_no = rs.getInt("running_vacancy_no");
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return running_vacancy_no;

    }

    public boolean checkResrvationQouta(String state_from) {
        boolean flag = false;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("checkResrvationQouta");
            String sql = "select running_no,max_no from " + TableList.VM_COUNTER_SIGN_RESERVATION + " where state_cd=? and state_from=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, Util.getUserStateCode());
            psmt.setString(2, state_from);
            rs = psmt.executeQuery();
            if (rs.next()) {
                if (rs.getInt("running_no") == rs.getInt("max_no")) {
                    flag = true;
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public boolean checkRegnNoExistinQouta(String state_from, String regn_no) {
        boolean flag = false;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("checkRegnNoExistinQouta");
            String sql = "select * from " + TableList.VT_OTHER_STATE_VCH_FOR_CS + " where state_cd=? and state_from=? and regn_no=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, Util.getUserStateCode());
            psmt.setString(2, state_from);
            psmt.setString(3, regn_no);
            rs = psmt.executeQuery();
            if (rs.next()) {
                flag = true;
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public int getPurposeFromReservation(String state_from, String regn_no) {
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManagerReadOnly tmgr = null;
        int pur_cd = 0;;
        try {
            tmgr = new TransactionManagerReadOnly("getPurposeFromReservation");
            String sql = "select pur_cd from " + TableList.VT_OTHER_STATE_VCH_FOR_CS + " where state_from=? and regn_no=? and state_cd=? and off_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, state_from);
            psmt.setString(2, regn_no);
            psmt.setString(3, Util.getUserStateCode());
            psmt.setInt(4, Util.getUserOffCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                pur_cd = rs.getInt("pur_cd");
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return pur_cd;
    }
}
