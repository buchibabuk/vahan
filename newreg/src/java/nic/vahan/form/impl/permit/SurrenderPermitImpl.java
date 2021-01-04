/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.java.util.DateUtilsException;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitFeeDobj;
import nic.vahan.form.dobj.permit.PermitHomeAuthDobj;
import nic.vahan.form.dobj.permit.SurrenderPermitDobj;
import nic.vahan.form.impl.HpaImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import org.apache.log4j.Logger;

public class SurrenderPermitImpl {

    private static final Logger LOGGER = Logger.getLogger(SurrenderPermitImpl.class);
    static String VT_PERMIT_STATUS_SURRENDER = "SUR";
    static String VT_PERMIT_STATUS_RESTORE = "REP";
    static RowSet rs = null;
    static int k = 0;

    public static String saveSurenderPermitDetails(SurrenderPermitDobj dobj, boolean trans_status) {
        String appl_no = "";
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("saveSurenderPermitDetails()");
            appl_no = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
            String va_permit_transSQL = "INSERT INTO " + TableList.VA_PERMIT_TRANSACTION + "(\n"
                    + "            state_cd, off_cd, appl_no, regn_no, pmt_no, pur_cd, \n"
                    + "            trans_pur_cd, remarks, order_no, order_dt, order_by, new_regn_no, \n"
                    + "            user_cd, op_dt,excempted_flag)\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?, \n"
                    + "            ?, ?, ?, ?, ?, ?, \n"
                    + "            ?, CURRENT_TIMESTAMP,?)";
            int i = 1;
            ps = tmgr.prepareStatement(va_permit_transSQL);
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getUserOffCode());
            ps.setString(i++, appl_no);
            ps.setString(i++, dobj.getRegn_no());
            ps.setString(i++, dobj.getPmt_no());
            ps.setInt(i++, dobj.getPur_cd());
            ps.setInt(i++, Integer.valueOf(dobj.getPurpose()));
            ps.setString(i++, dobj.getRemarks());
            ps.setString(i++, dobj.getOrder_no());
            ps.setDate(i++, new java.sql.Date(dobj.getOrder_dt().getTime()));
            ps.setString(i++, dobj.getOrder_by());
            ps.setString(i++, dobj.getNew_regn_no());
            ps.setLong(i++, Long.parseLong(Util.getEmpCode()));
            ps.setString(i, dobj.getTransfer_purpose());
            ps.executeUpdate();

            if (dobj.getPur_cd() == TableConstants.VM_PMT_RESTORE_PUR_CD) {
                insertVhPermitTranactionToVtPermitTranaction(tmgr, dobj.getRegn_no(), dobj.getPmt_no());
                deleteInToVtPermitTransaction(tmgr, dobj.getRegn_no(), dobj.getPmt_no());
            }
            if (trans_status) {
                PermitFeeDobj pmt_dobj = new PermitFeeDobj();
                pmt_dobj.setAppl_no(appl_no);
                pmt_dobj.setRegn_no(dobj.getRegn_no());
                pmt_dobj.setPur_cd(dobj.getPurpose());
                pmt_dobj.setPermit_type(String.valueOf(dobj.getPmtType()));
                pmt_dobj.setPermit_catg(dobj.getPmtCatg());
                pmt_dobj.setPaymentStaus("N");
                new PermitFeeImpl().insertNpPermitDetails(pmt_dobj, "CAN", tmgr);
            }
            Status_dobj status = new Status_dobj();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            String dt = sdf.format(new Date());
            status.setAppl_dt(dt);
            status.setAppl_no(appl_no);

            if (Integer.valueOf(dobj.getPur_cd()) == TableConstants.VM_PMT_SURRENDER_PUR_CD) {
                status.setPur_cd(dobj.getPur_cd());
                status.setAction_cd(TableConstants.TM_ROLE_PMT_SURRENDER_ENTRY);
                if (Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD) {
                    status.setAction_cd(CommonPermitPrintImpl.getActionCd(Util.getUserStateCode(), dobj.getPur_cd(), Integer.valueOf(dobj.getPurpose()), 2, tmgr, 0));
                    if (status.getAction_cd() == TableConstants.TM_ROLE_PMT_FEE) {
                        status.setFlow_slno(3);
                    } else {
                        status.setFlow_slno(2);
                    }
                } else if (Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD
                        || Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD
                        || Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD
                        || Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_TEMP_SUR_PUR_CD) {
                    status.setFlow_slno(1);
                } else if (Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_TRANSFER_PUR_CD
                        || Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD
                        || Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_CA_PUR_CD
                        || Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_SUSPENSION_PUR_CD) {
                    status.setFlow_slno(1);
                    status.setPur_cd(Integer.valueOf(dobj.getPurpose()));
                    status.setAction_cd(CommonPermitPrintImpl.getActionCd(Util.getUserStateCode(), Integer.valueOf(dobj.getPurpose()), Integer.valueOf(dobj.getPurpose()), 1, tmgr, 0));
                } else if (Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD) {
                    status.setFlow_slno(1);
                    status.setPur_cd(Integer.valueOf(dobj.getPurpose()));
                    status.setAction_cd(CommonPermitPrintImpl.getActionCd(Util.getUserStateCode(), Integer.valueOf(dobj.getPurpose()), Integer.valueOf(dobj.getPurpose()), 1, tmgr, 0));
                } else if (Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_CANCELATION_PUR_CD) {
                    status.setPur_cd(Integer.valueOf(dobj.getPurpose()));
                    status.setAction_cd(CommonPermitPrintImpl.getActionCd(Util.getUserStateCode(), Integer.valueOf(dobj.getPurpose()), Integer.valueOf(dobj.getPurpose()), 1, tmgr, dobj.getTempspl_purpose()));
                    status.setFlow_slno(CommonPermitPrintImpl.getflowSerialNumber(Util.getUserStateCode(), Integer.valueOf(dobj.getPurpose()), status.getAction_cd(), tmgr));
                } else {
                    status.setFlow_slno(2);
                }
            } else if (Integer.valueOf(dobj.getPur_cd()) == TableConstants.VM_PMT_RESTORE_PUR_CD) {
                status.setFlow_slno(1);
                if (Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD
                        || Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD
                        || Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD
                        || Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD) {
                    int[] retArr = ServerUtil.getInitialAction(tmgr, Util.getUserStateCode(), Integer.valueOf(dobj.getPurpose()), null);
                    if (retArr.length == 0) {
                        throw new VahanException("Flow is not there. Please Contact to system administrator");
                    }
                    status.setPur_cd(Integer.valueOf(dobj.getPurpose()));
                    status.setAction_cd(retArr[0]);
                } else {
                    status.setPur_cd(dobj.getPur_cd());
                    status.setAction_cd(TableConstants.TM_ROLE_PMT_RESTORE_ENTRY);
                }
            }

            status.setFile_movement_slno(1);
            status.setEmp_cd(Long.parseLong(Util.getEmpCode()));
            status.setRegn_no(dobj.getRegn_no());
            status.setOffice_remark("");
            status.setPublic_remark("");
            status.setStatus("N");
            status.setState_cd(Util.getUserStateCode());
            status.setOff_cd(Util.getSelectedSeat().getOff_cd());
            if (Integer.valueOf(dobj.getPur_cd()) == TableConstants.VM_PMT_RESTORE_PUR_CD
                    && Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD
                    && dobj.getState_cd().equalsIgnoreCase("UK")) {
                status.setRegn_no(dobj.getNew_regn_no());
            }
            ServerUtil.fileFlowForNewApplication(tmgr, status);
            status.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
            status.setStatus(TableConstants.STATUS_COMPLETE);
            status.setAppl_no(appl_no);
            status.setOffice_remark("");
            status.setPublic_remark("");
            if ((Integer.valueOf(dobj.getPur_cd()) == TableConstants.VM_PMT_SURRENDER_PUR_CD)
                    && (Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_TRANSFER_PUR_CD
                    || Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD
                    || Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD
                    || Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_CANCELATION_PUR_CD
                    || Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_CA_PUR_CD
                    || Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_SUSPENSION_PUR_CD
                    || Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD)) {
                ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, appl_no,
                        status.getAction_cd(), status.getPur_cd(), null, tmgr);
            } else if (Integer.valueOf(dobj.getPur_cd()) == TableConstants.VM_PMT_RESTORE_PUR_CD
                    && (Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD
                    || Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD
                    || Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD
                    || Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD)) {
                ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, appl_no,
                        status.getAction_cd(), status.getPur_cd(), null, tmgr);
            } else {
                ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, appl_no,
                        TableConstants.TM_ROLE_PMT_APPL, dobj.getPur_cd(), null, tmgr);
                ServerUtil.fileFlow(tmgr, status);
            }
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
                appl_no = "";
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return appl_no;
    }

    public static SurrenderPermitDobj getVaPermitTransactionDetails(String appl_no) throws VahanException {
        SurrenderPermitDobj dobj = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("Get Verify Permit Permit Details");
            String va_trans = "SELECT regn_no,pmt_no, trans_pur_cd, remarks, order_no, order_dt, order_by,new_regn_no,excempted_flag FROM " + TableList.VA_PERMIT_TRANSACTION + " where appl_no= ?";
            ps = tmgr.prepareStatement(va_trans);
            ps.setString(1, appl_no);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new SurrenderPermitDobj();
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setPmt_no(rs.getString("pmt_no"));
                dobj.setPurpose(rs.getString("trans_pur_cd"));
                dobj.setOrder_dt(rs.getDate("order_dt"));
                dobj.setOrder_no(rs.getString("order_no"));
                dobj.setOrder_by(rs.getString("order_by"));
                dobj.setRemarks(rs.getString("remarks"));
                if (CommonUtils.isNullOrBlank(rs.getString("excempted_flag"))) {
                    dobj.setTransfer_purpose("O");
                } else {
                    dobj.setTransfer_purpose(rs.getString("excempted_flag"));
                }
                if (rs.getString("new_regn_no") != null || (!("").equalsIgnoreCase(rs.getString("new_regn_no")))) {
                    dobj.setNew_regn_no(rs.getString("new_regn_no"));
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

    public static SurrenderPermitDobj getRebackPermitDetails(String regn_no, String pmtNo, int action_cd) {
        SurrenderPermitDobj dobj = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;

        try {
            tmgr = new TransactionManager("getGoodspermitdetails");
            String va_trans = "SELECT state_cd, off_cd, appl_no, regn_no, pmt_no, pur_cd, rcpt_no, "
                    + " trans_pur_cd, remarks, order_no, order_dt, order_by, new_regn_no, "
                    + "  user_cd, op_dt::date,excempted_flag FROM " + TableList.VT_PERMIT_TRANSACTION + " where regn_no=? and pmt_no = ?";
            if (CommonUtils.isNullOrBlank(pmtNo)) {
                va_trans = "SELECT state_cd, off_cd, appl_no, regn_no, pmt_no, pur_cd, rcpt_no, "
                        + " trans_pur_cd, remarks, order_no, order_dt, order_by, new_regn_no, "
                        + "  user_cd, op_dt::date,excempted_flag FROM " + TableList.VT_PERMIT_TRANSACTION + " where regn_no=? ";
            }
            if (TableConstants.TM_ROLE_PMT_SURRENDER_ENTRY == action_cd) {
                va_trans += " AND trans_pur_cd <> ? order by op_dt DESC LIMIT 1";
            } else {
                va_trans += " order by op_dt DESC LIMIT 1";
            }
            ps = tmgr.prepareStatement(va_trans);
            int i = 1;
            ps.setString(i++, regn_no);
            if (!CommonUtils.isNullOrBlank(pmtNo)) {
                ps.setString(i++, pmtNo);
            }
            if (TableConstants.TM_ROLE_PMT_SURRENDER_ENTRY == action_cd) {
                ps.setInt(i++, TableConstants.VM_PMT_REPLACE_VEH_PUR_CD);
            }
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new SurrenderPermitDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setPmt_no(rs.getString("pmt_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setPurpose(rs.getString("trans_pur_cd"));
                dobj.setOrder_dt(rs.getDate("order_dt"));
                dobj.setOrder_no(rs.getString("order_no"));
                dobj.setOrder_by(rs.getString("order_by"));
                dobj.setRemarks(rs.getString("remarks"));
                dobj.setSurrenderDate(rs.getDate("op_dt"));
                dobj.setTransfer_purpose(rs.getString("excempted_flag"));
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

    public static String saveSurenderPermitVerifyDetails(SurrenderPermitDobj dobj, String appl_no, Status_dobj status_dobj, String compairChange) throws VahanException {
        String flage = "";
        Status_dobj statDobj = status_dobj;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("saveSurenderPermitDetails()");
            if (statDobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_DISPATCH_PENDING)) {
                ServerUtil.fileFlow(tmgr, statDobj);
                tmgr.commit();
                flage = "Success";
            } else {
                if (!compairChange.isEmpty()) {
                    vhaPermitTransactionToVaPermitTransaction(tmgr, appl_no);
                    UpateVaPermitTransaction(tmgr, appl_no, dobj);
                    insertIntoVhaChangedData(tmgr, appl_no, compairChange);
                }
                statDobj = ServerUtil.webServiceForNextStage(statDobj, tmgr);
                ServerUtil.fileFlow(tmgr, statDobj);
                tmgr.commit();
                flage = "Success";
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

        return flage;
    }

    public static SurrenderPermitDobj getApprovePermitdetails(String regn_no) throws VahanException {
        SurrenderPermitDobj dobj = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {

            tmgr = new TransactionManager("getApprovePermitdetails");
            dobj = new SurrenderPermitDobj();
            String va_trans = " SELECT state_cd, off_cd, appl_no, regn_no, pmt_no, pur_cd, rcpt_no, "
                    + "  trans_purpose_cd, remarks, order_no, order_dt, order_by, new_regn_no, "
                    + "  user_cd, op_dt FROM " + TableList.VA_PERMIT_TRANSACTION + " where regn_no=?";
            ps = tmgr.prepareStatement(va_trans);
            ps.setString(1, regn_no);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj.setPurpose(rs.getString("trans_purpose_cd"));
                dobj.setOrder_dt(rs.getDate("order_dt"));
                dobj.setOrder_no(rs.getString("order_no"));
                dobj.setOrder_by(rs.getString("order_by"));
                dobj.setRemarks(rs.getString("remarks"));
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

    public static String saveApproveSurrenderPermitDetails(SurrenderPermitDobj dobj, String appl_no, Status_dobj status_dobj, String compairChange, boolean nocIssue, boolean showNpDetail) throws VahanException {
        String successFlag = "";
        Status_dobj statDobj = status_dobj;
        PrintPermitImpl printImpl = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("saveApproveSurrenderPermitDetails()");
            printImpl = new PrintPermitImpl();
            if (!compairChange.isEmpty()) {
                vhaPermitTransactionToVaPermitTransaction(tmgr, appl_no);
                UpateVaPermitTransaction(tmgr, appl_no, dobj);
                insertIntoVhaChangedData(tmgr, appl_no, compairChange);
            }
            PermitDetailDobj pmt_dobj = PermitDetailImpl.getPermitdetails(dobj.getPmt_no());
            if (pmt_dobj == null) {
                throw new VahanException("Permit details not found. Please contact to system administrator");
            }
            if (pmt_dobj.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD
                    || pmt_dobj.getPur_cd() == TableConstants.VM_PMT_RENEWAL_PUR_CD
                    || pmt_dobj.getPur_cd() == TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD) {
                insertVhPermitToVtPermit(tmgr, dobj.getPmt_no());
                deleteInToVtPermit(tmgr, dobj.getPmt_no());
            } else if (pmt_dobj.getPur_cd() == TableConstants.VM_PMT_TEMP_PUR_CD
                    || pmt_dobj.getPur_cd() == TableConstants.VM_PMT_SPECIAL_PUR_CD) {
                insertVhTempPermitToVtTempPermit(tmgr, dobj.getPmt_no());
                deleteInToVtTempPermit(tmgr, dobj.getPmt_no());
            }
            vhaPermitTransactionToVaPermitTransaction(tmgr, appl_no);
            if (!(dobj.getPurpose().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_CANCELATION_PUR_CD)))
                    && !(dobj.getPurpose().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD)))) {
                insertVtPermitTranactionToVaPermitTranaction(tmgr, appl_no);
            }
            if ((dobj.getPurpose().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_CANCELATION_PUR_CD)))
                    || (dobj.getPurpose().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD)))) {
                moveVtInstrumentAitpToVhInstrumentAitp(dobj.getRegn_no(), tmgr);
            }
            if ((Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_TEMP_SUR_PUR_CD)) {
                getTaxDetails(appl_no, dobj.getRegn_no(), tmgr);
            }
            if (((Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_CANCELATION_PUR_CD)
                    || (Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD)
                    || (Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD)
                    || (Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD)
                    || (Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD))
                    && (dobj.getPmtType() == Integer.valueOf(TableConstants.AITP)
                    || dobj.getPmtType() == Integer.valueOf(TableConstants.NATIONAL_PERMIT))) {
                insertVhPermitHomeAuthToVtPermitHomeAuth(dobj.getRegn_no(), tmgr);
                PermitHomeAuthDobj authdobj = ServerUtil.getNPAuthDetailsAtPrint(dobj.getRegn_no(), appl_no, Util.getUserStateCode());
                if (authdobj != null && ((!CommonUtils.isNullOrBlank(authdobj.getNpVerifyStatus()) && authdobj.getNpVerifyStatus().equalsIgnoreCase("Y")) || ((!CommonUtils.isNullOrBlank(authdobj.getNpVerifyStatus())) && authdobj.getNpVerifyStatus().equalsIgnoreCase("N") && !showNpDetail))) {
                    ServerUtil.updateVaNPAuthFlag(appl_no, tmgr, "A");
                    ServerUtil.vatovhNPAuthData(appl_no, tmgr);
                } else if (authdobj != null && (!CommonUtils.isNullOrBlank(authdobj.getNpVerifyStatus())) && authdobj.getNpVerifyStatus().equalsIgnoreCase("N") && showNpDetail) {
                    throw new VahanException("As per records the existing National Permit Authorization has to be cancelled before the approval of this application on the portal https://vahan.parivahan.gov.in/npermit/ ");
                }
            }
            deleteInToVaPermitTransaction(tmgr, appl_no);
            statDobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            statDobj.setEntry_status(TableConstants.STATUS_APPROVED);
            ServerUtil.updateApprovedStatus(tmgr, statDobj);
            printImpl.fileFlowOfPermit(tmgr, statDobj, appl_no, statDobj.getPur_cd(), null);
            if (nocIssue) {
                String docId = "'8'";
                String[] beanData = {docId, appl_no, dobj.getRegn_no()};
                CommonPermitPrintImpl.insertVaPermitPrint(tmgr, beanData);
            }
            String docId = "'9'";
            String[] beanData = {docId, appl_no, dobj.getRegn_no()};
            CommonPermitPrintImpl.insertVaPermitPrint(tmgr, beanData);
            if (Util.getUserStateCode().equalsIgnoreCase("DL")) {
                ServerUtil.updateOnlinePermitStatus(tmgr, status_dobj);
            }
            tmgr.commit();
            successFlag = "Success";
        } catch (VahanException e) {
            successFlag = "";
            throw e;
        } catch (Exception e) {
            successFlag = "";
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
        return successFlag;
    }

    public static void moveVtInstrumentAitpToVhInstrumentAitp(String regnNo, TransactionManager tmgr) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        RowSet rs = null;
        sql = " Select * FROM " + TableList.VT_INSTRUMENT_AITP
                + " WHERE regn_no = ? and state_cd=? ";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, regnNo);
        ps.setString(2, Util.getUserStateCode());
        rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            sql = " INSERT INTO " + TableList.VH_INSTRUMENT_AITP + " select current_timestamp as moved_on, ? moved_by, state_cd,off_cd,appl_no, sr_no, regn_no, pay_state_cd,instrument_type,"
                    + " instrument_no, instrument_dt, instrument_amt,bank_code,branch_name,payable_to,recieved_dt,op_dt,pur_cd,? FROM " + TableList.VT_INSTRUMENT_AITP
                    + " WHERE regn_no = ? and state_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, "SUR");
            ps.setString(3, regnNo);
            ps.setString(4, Util.getUserStateCode());

            ps.executeUpdate();

            sql = " DELETE FROM " + TableList.VT_INSTRUMENT_AITP
                    + " WHERE regn_no = ? and state_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, Util.getUserStateCode());
            ps.executeUpdate();
        }
    }

// Starting Done by Madhurendra kumar on 14-6-2018
    public static void getTaxDetails(String appl_no, String regn_no, TransactionManager tmgr) throws DateUtilsException, ParseException {
        PreparedStatement ps = null;
        String query;
        Date tax_from = null;
        Date tax_upto = null;
        Date sur_op_dt = null;
        try {
            query = "SELECT a.tax_amt,a.tax_from,a.tax_upto,a.tax_mode,b.op_dt "
                    + " from " + TableList.VT_TAX + " a"
                    + " left join " + TableList.VA_PERMIT_TRANSACTION + " b on a.regn_no = b.regn_no"
                    + " where a.regn_no = ? and a.tax_upto >  current_date and b.appl_no=? order by a.rcpt_dt desc limit 1";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, regn_no);
            ps.setString(2, appl_no);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (rs.getString("tax_mode").equalsIgnoreCase("M")) {
                    int amt = rs.getInt("tax_amt");
                    tax_from = rs.getDate("tax_from");
                    tax_upto = rs.getDate("tax_upto");
                    sur_op_dt = rs.getDate("op_dt");
                    String rebate_month_start_date = DateUtils.getStartOfMonthDate(DateUtils.getDateInDDMMYYYY(sur_op_dt));
                    int day = (int) DateUtils.getDate1MinusDate2_Days(JSFUtils.getStringToDate(rebate_month_start_date), tax_upto) + 1;
                    int month = DateUtils.getDate1MinusDate2_Months(tax_from, tax_upto);
                    float pd_taxamt_month = (float) amt / month;
                    float pd_taxamt = pd_taxamt_month / day;
                    int remain_dtax = (int) DateUtils.getDate1MinusDate2_Days(sur_op_dt, tax_upto) + 1;
                    float remain_amt = pd_taxamt * remain_dtax;
                    query = "INSERT INTO " + TableList.VT_NON_USE_TAX_ADJUST + " ( state_cd, off_cd, appl_no, regn_no, exem_fr, exem_to, exem_by, nonuse_adjust_amt,penalty,op_dt)"
                            + " VALUES( ?,  ?,  ?,  ?,  ?,  ?,  ?,?,  0,current_timestamp)";
                    PreparedStatement ps1 = null;
                    ps1 = tmgr.prepareStatement(query);
                    ps1.setString(1, Util.getUserStateCode());
                    ps1.setInt(2, Util.getUserOffCode());
                    ps1.setString(3, appl_no);
                    ps1.setString(4, regn_no);
                    ps1.setDate(5, new java.sql.Date(sur_op_dt.getTime()));
                    ps1.setDate(6, new java.sql.Date(tax_upto.getTime()));
                    ps1.setString(7, Util.getUserId());
                    ps1.setFloat(8, remain_amt);
                    if (ps1.executeUpdate() > 0) {
                        query = "INSERT INTO vahan4.vt_tax_clear("
                                + " state_cd, off_cd, appl_no, regn_no, pur_cd, clear_fr, clear_to,"
                                + " tcr_no, iss_auth, iss_dt, remark, user_cd, op_dt, rcpt_no, rcpt_dt)"
                                + " VALUES( ?,  ?,  ?,  ?,  ?,  ?,  ?,?,  null,  ?,  ?,  ?,  current_timestamp,  null,  null)";
                        PreparedStatement ps2 = null;
                        ps2 = tmgr.prepareStatement(query);
                        ps2.setString(1, Util.getUserStateCode());
                        ps2.setInt(2, Util.getUserOffCode());
                        ps2.setString(3, appl_no);
                        ps2.setString(4, regn_no);
                        ps2.setInt(5, TableConstants.TM_ROAD_TAX);
                        ps2.setDate(6, new java.sql.Date(tax_from.getTime()));
                        ps2.setDate(7, new java.sql.Date(sur_op_dt.getTime()));
                        ps2.setString(8, "NA");
                        ps2.setDate(9, new java.sql.Date(tax_from.getTime()));
                        ps2.setString(10, "NA");
                        ps2.setString(11, Util.getUserId());
                        ps2.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

    }
// End Done by Madhurendra kumar on 14-6-2018

    public static void insertVhPermitToVtPermit(TransactionManager tmgr, String pmt_no) throws SQLException {
        PreparedStatement ps = null;

        String query = "INSERT INTO " + TableList.VH_PERMIT + "(\n"
                + "            state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                + "            valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, \n"
                + "            service_type, goods_to_carry, jorney_purpose, parking, replace_date, \n"
                + "            remarks, op_dt, pmt_status, reason, moved_on,moved_by)\n"
                + "   SELECT state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                + "       valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, \n"
                + "       service_type, goods_to_carry, jorney_purpose, parking, replace_date, \n"
                + "       remarks, op_dt,?,?,CURRENT_TIMESTAMP,?\n"
                + "  FROM " + TableList.VT_PERMIT + " where pmt_no = ?";
        ps = tmgr.prepareStatement(query);
        int j = 1;
        ps.setString(j++, VT_PERMIT_STATUS_SURRENDER);
        ps.setString(j++, "SURRENDER OF PERMIT");
        ps.setString(j++, Util.getEmpCode());
        ps.setString(j++, pmt_no);
        ps.executeUpdate();
    }

    public static void insertVhTempPermitToVtTempPermit(TransactionManager tmgr, String pmt_no) throws SQLException {
        PreparedStatement ps = null;
        String query = "INSERT INTO " + TableList.VH_TEMP_PERMIT + "(\n"
                + "            state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                + "            valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, reason, route_fr, \n"
                + "            route_to, op_dt, pmt_status, pmt_reason, moved_on, moved_by)\n"
                + "     SELECT state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                + "            valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, reason, route_fr, \n"
                + "            route_to, op_dt,?,?,CURRENT_TIMESTAMP,? \n"
                + "  FROM " + TableList.VT_TEMP_PERMIT + " where pmt_no=?";
        ps = tmgr.prepareStatement(query);
        int j = 1;
        ps.setString(j++, VT_PERMIT_STATUS_SURRENDER);
        ps.setString(j++, "CANCELATION OF PERMIT");
        ps.setString(j++, Util.getEmpCode());
        ps.setString(j++, pmt_no);
        ps.executeUpdate();
    }

    public static void insertVtPermitTranactionToVaPermitTranaction(TransactionManager tmgr, String appl_no) throws SQLException {
        PreparedStatement ps = null;

        String query = "INSERT INTO " + TableList.VT_PERMIT_TRANSACTION + "(\n"
                + "            state_cd, off_cd, appl_no, regn_no, pmt_no, pur_cd, rcpt_no, \n"
                + "            trans_pur_cd, remarks, order_no, order_dt, order_by, new_regn_no, \n"
                + "            user_cd, op_dt,excempted_flag) SELECT state_cd, off_cd, appl_no, regn_no, pmt_no, pur_cd, rcpt_no, \n"
                + "       trans_pur_cd, remarks, order_no, order_dt, order_by, new_regn_no, \n"
                + "       user_cd, Current_TIMESTAMP,excempted_flag \n"
                + "  FROM " + TableList.VA_PERMIT_TRANSACTION + " where appl_no=?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, appl_no);
        ps.executeUpdate();
    }

    public static void insertVhPermitTranactionToVtPermitTranaction(TransactionManager tmgr, String regn_no, String pmt_no) throws SQLException {
        PreparedStatement ps = null;

        String Query = "INSERT INTO " + TableList.VH_PERMIT_TRANSACTION + "(\n"
                + "            state_cd, off_cd, appl_no, regn_no, pmt_no, pur_cd, rcpt_no, \n"
                + "            trans_pur_cd, remarks, order_no, order_dt, order_by, new_regn_no, \n"
                + "            user_cd, op_dt, moved_on, moved_by,excempted_flag)\n"
                + " SELECT     state_cd, off_cd, appl_no, regn_no, pmt_no, pur_cd, rcpt_no, \n"
                + "            trans_pur_cd, remarks, order_no, order_dt, order_by, new_regn_no, \n"
                + "            user_cd, op_dt,CURRENT_TIMESTAMP,?,excempted_flag \n"
                + "  FROM " + TableList.VT_PERMIT_TRANSACTION + " WHERE regn_no = ? and pmt_no = ?";
        ps = tmgr.prepareStatement(Query);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, regn_no);
        ps.setString(3, pmt_no);
        ps.executeUpdate();
    }

    public static void insertVtPermitTranactionToVhPermitTranaction(TransactionManager tmgr, String regn_no) throws SQLException {
        PreparedStatement ps = null;
        String Query = "INSERT INTO " + TableList.VT_PERMIT_TRANSACTION + "  (\n"
                + " state_cd, off_cd, appl_no, regn_no, pmt_no, pur_cd, rcpt_no, \n"
                + " trans_pur_cd, remarks, order_no, order_dt, order_by, new_regn_no, \n"
                + " user_cd, op_dt)\n"
                + " SELECT     state_cd, off_cd, appl_no, regn_no, pmt_no, pur_cd, rcpt_no, \n"
                + " trans_pur_cd, remarks, order_no, order_dt, order_by, new_regn_no, \n"
                + " user_cd, op_dt\n"
                + " FROM   " + TableList.VH_PERMIT_TRANSACTION + " WHERE regn_no = ? order by moved_on DESC limit 1";
        ps = tmgr.prepareStatement(Query);
        ps.setString(1, regn_no);
        ps.executeUpdate();
    }

    public static void deleteInToVaPermitTransaction(TransactionManager tmgr, String appl_no) throws SQLException {
        PreparedStatement ps = null;

        String query = "DELETE FROM " + TableList.VA_PERMIT_TRANSACTION + " where appl_no =?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, appl_no);
        ps.executeUpdate();
    }

    public static void deleteInToVtPermit(TransactionManager tmgr, String pmt_no) throws SQLException {
        PreparedStatement ps = null;
        String query = "DELETE FROM " + TableList.VT_PERMIT + " where pmt_no =?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, pmt_no);
        ps.executeUpdate();
    }

    public static void deleteInToVtTempPermit(TransactionManager tmgr, String pmt_no) throws SQLException {
        PreparedStatement ps = null;
        String query = "DELETE FROM " + TableList.VT_TEMP_PERMIT + " where pmt_no =?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, pmt_no);
        ps.executeUpdate();
    }

    public static void deleteInToVtPermitTransaction(TransactionManager tmgr, String Regn_no, String pmt_no) throws SQLException {
        PreparedStatement ps = null;
        String query = "DELETE FROM " + TableList.VT_PERMIT_TRANSACTION + " where regn_no =? and pmt_no = ?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, Regn_no);
        ps.setString(2, pmt_no);
        ps.executeUpdate();
    }

    public static void vhaPermitTransactionToVaPermitTransaction(TransactionManager tmgr, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        Timestamp currentTimestamp = new Timestamp(now.getTime());
        if (k > 0) {
            calendar.setTime(now);
            calendar.add(calendar.MILLISECOND, 1000);
            now = calendar.getTime();
            currentTimestamp = new Timestamp(now.getTime());
        }
        k++;
        String Query = "INSERT INTO " + TableList.VHA_PERMIT_TRANSACTION + "(\n"
                + "            state_cd, off_cd, appl_no, regn_no, pmt_no, pur_cd, rcpt_no, \n"
                + "            trans_pur_cd, remarks, order_no, order_dt, order_by, new_regn_no, \n"
                + "            user_cd, op_dt, moved_on, moved_by,excempted_flag)\n"
                + "    SELECT state_cd, off_cd, appl_no, regn_no, pmt_no, pur_cd, rcpt_no, \n"
                + "       trans_pur_cd, remarks, order_no, order_dt, order_by, new_regn_no, \n"
                + "       user_cd, op_dt,?,?,excempted_flag\n"
                + "  FROM " + TableList.VA_PERMIT_TRANSACTION + " where appl_no=? order by va_permit_transaction.op_dt DESC";
        ps = tmgr.prepareStatement(Query);
        ps.setTimestamp(1, currentTimestamp);
        ps.setString(2, Util.getEmpCode());
        ps.setString(3, appl_no);
        ps.executeUpdate();
    }

    public static void UpateVaPermitTransaction(TransactionManager tmgr, String appl_no, SurrenderPermitDobj dobj) throws SQLException {
        PreparedStatement ps = null;

        String Query = "UPDATE " + TableList.VA_PERMIT_TRANSACTION + "\n"
                + "   SET state_cd=?, off_cd=?, appl_no=?, regn_no=?, pmt_no=?, pur_cd=?, \n"
                + "       rcpt_no=?, trans_pur_cd=?, remarks=?, order_no=?, order_dt=?, \n"
                + "       order_by=?, new_regn_no=?, user_cd=?, op_dt=CURRENT_TIMESTAMP\n"
                + " WHERE appl_no=? ";

        ps = tmgr.prepareStatement(Query);
        int i = 1;
        ps.setString(i++, Util.getUserStateCode());
        ps.setInt(i++, Util.getUserOffCode());
        ps.setString(i++, appl_no);
        ps.setString(i++, dobj.getRegn_no());
        ps.setString(i++, dobj.getPmt_no());
        ps.setInt(i++, dobj.getPur_cd());
        ps.setString(i++, dobj.getRcpt_no());
        ps.setInt(i++, Integer.valueOf(dobj.getPurpose()));
        ps.setString(i++, dobj.getRemarks());
        ps.setString(i++, dobj.getOrder_no());
        ps.setDate(i++, new java.sql.Date(dobj.getOrder_dt().getTime()));
        ps.setString(i++, dobj.getOrder_by());
        if (dobj.getNew_regn_no() != null) {
            ps.setString(i++, dobj.getNew_regn_no().toUpperCase());
        } else {
            ps.setString(i++, null);
        }
        ps.setLong(i++, Long.parseLong(Util.getEmpCode()));
        ps.setString(i++, appl_no);
        ps.executeUpdate();
    }

    public static String saveApproveRestroPermitDetails(SurrenderPermitDobj dobj, String appl_no, Status_dobj status_dobj, String compairChange) throws VahanException {
        String flage = "";
        Status_dobj statDobj = status_dobj;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("saveApproveRestroPermitDetails");
            String query = "";
            if (!compairChange.isEmpty()) {
                vhaPermitTransactionToVaPermitTransaction(tmgr, appl_no);
                UpateVaPermitTransaction(tmgr, appl_no, dobj);
            }
            vhaPermitTransactionToVaPermitTransaction(tmgr, appl_no);
            deleteInToVaPermitTransaction(tmgr, appl_no);
            if (Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD) {
                query = "INSERT INTO " + TableList.VT_PERMIT + "(\n"
                        + "            state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                        + "            valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, \n"
                        + "            service_type, goods_to_carry, jorney_purpose, parking, replace_date, \n"
                        + "            remarks, op_dt)\n"
                        + "   SELECT state_cd, ?, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                        + "       valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, \n"
                        + "       service_type, goods_to_carry, jorney_purpose, parking, replace_date, \n"
                        + "       remarks,CURRENT_TIMESTAMP\n"
                        + "  FROM " + TableList.VH_PERMIT + " where regn_no = ? AND pmt_no = ? order by vh_permit.moved_on DESC limit 1";
                ps = tmgr.prepareStatement(query);
                ps.setInt(1, Util.getUserOffCode());
                ps.setString(2, dobj.getRegn_no());
                ps.setString(3, dobj.getPmt_no());
                ps.executeUpdate();
            } else {
                query = "INSERT INTO " + TableList.VT_PERMIT + "(\n"
                        + "            state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                        + "            valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, \n"
                        + "            service_type, goods_to_carry, jorney_purpose, parking, replace_date, \n"
                        + "            remarks, op_dt)\n"
                        + "   SELECT state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                        + "       valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, \n"
                        + "       service_type, goods_to_carry, jorney_purpose, parking, replace_date, \n"
                        + "       remarks,CURRENT_TIMESTAMP\n"
                        + "  FROM " + TableList.VH_PERMIT + " where regn_no = ? AND pmt_no = ? order by vh_permit.moved_on DESC limit 1";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, dobj.getRegn_no());
                ps.setString(2, dobj.getPmt_no());
                ps.executeUpdate();
            }
            if (Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_TRANSFER_PUR_CD
                    || Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_TEMP_SUR_PUR_CD
                    || Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD
                    || Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD
                    || Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_CA_PUR_CD
                    || Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_SUSPENSION_PUR_CD) {
                query = "SELECT * FROM " + TableList.VH_PERMIT + " WHERE REGN_NO = ? AND pmt_no = ? order by vh_permit.moved_on DESC limit 1";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, dobj.getRegn_no());
                ps.setString(2, dobj.getPmt_no());
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    String docId = CommonPermitPrintImpl.getPermitDocIdForQuery(CommonPermitPrintImpl.getPermitDocId(tmgr, rs.getInt("pmt_type"), TableConstants.VM_PMT_RENEWAL_PUR_CD, Util.getUserStateCode()));
                    String[] beanData = {docId, appl_no, rs.getString("regn_no")};
                    CommonPermitPrintImpl.insertVaPermitPrint(tmgr, beanData);
                }
            }
            statDobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            ServerUtil.fileFlow(tmgr, statDobj);
            tmgr.commit();
            flage = "Success";
        } catch (VahanException e) {
            flage = "";
            throw e;
        } catch (SQLException e) {
            flage = "";
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            flage = "";
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
        return flage;
    }

    public static String saveApprovePurposeOfVehChange(SurrenderPermitDobj dobj, String appl_no, Status_dobj status_dobj, String compairChange) throws VahanException {
        String flage = "";
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        int pmtType = 0;
        try {
            tmgr = new TransactionManager("saveApproveRestroPermitDetails");
            String query = "";
            if (!compairChange.isEmpty()) {
                vhaPermitTransactionToVaPermitTransaction(tmgr, appl_no);
                UpateVaPermitTransaction(tmgr, appl_no, dobj);
            }
            vhaPermitTransactionToVaPermitTransaction(tmgr, appl_no);
            query = "SELECT * FROM " + TableList.VH_PERMIT + " WHERE REGN_NO = ? AND PMT_NO=? order by vh_permit.moved_on DESC limit 1";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, dobj.getRegn_no());
            ps.setString(2, dobj.getPmt_no());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                pmtType = rs.getInt("pmt_type");
                String docId = CommonPermitPrintImpl.getPermitDocIdForQuery(CommonPermitPrintImpl.getPermitDocId(tmgr, pmtType, TableConstants.VM_PMT_RENEWAL_PUR_CD, Util.getUserStateCode()));
                String[] beanData = {docId, appl_no, rs.getString("regn_no")};
                CommonPermitPrintImpl.insertVaPermitPrint(tmgr, beanData);
            }
            deleteInToVaPermitTransaction(tmgr, appl_no);
            HpaImpl hpa_Impl = new HpaImpl();
            List<HpaDobj> hypth = hpa_Impl.getHypoDetails(null, TableConstants.VM_TRANSACTION_MAST_REM_HYPO, dobj.getNew_regn_no(), true, Util.getUserStateCode());
            if ((hypth.size() > 0) && (Util.getUserStateCode().equalsIgnoreCase("ML"))) {
                query = "INSERT INTO " + TableList.VT_PERMIT + "(\n"
                        + "            state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                        + "            valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, \n"
                        + "            service_type, goods_to_carry, jorney_purpose, parking, replace_date, \n"
                        + "            remarks, op_dt)\n"
                        + "   SELECT state_cd, off_cd, appl_no, pmt_no, ?, issue_dt, valid_from, \n"
                        + "       ?, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, \n"
                        + "       service_type, goods_to_carry, jorney_purpose, parking, replace_date, \n"
                        + "       remarks,CURRENT_TIMESTAMP\n"
                        + "  FROM " + TableList.VH_PERMIT + " where regn_no = ? AND PMT_NO=? order by vh_permit.moved_on DESC limit 1";
                ps = tmgr.prepareStatement(query);
                int i = 1;
                ps.setString(i++, dobj.getNew_regn_no().toUpperCase());
                ps.setDate(i++, new java.sql.Date(ServerUtil.dateRange(new Date(), 0, 4, -1).getTime()));
                ps.setString(i++, dobj.getRegn_no());
                ps.setString(i++, dobj.getPmt_no());
                ps.executeUpdate();
            } else {
                query = "INSERT INTO " + TableList.VT_PERMIT + "(\n"
                        + "            state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                        + "            valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, \n"
                        + "            service_type, goods_to_carry, jorney_purpose, parking, replace_date, \n"
                        + "            remarks, op_dt)\n"
                        + "   SELECT state_cd, off_cd, appl_no, pmt_no, ?, issue_dt, valid_from, \n"
                        + "       valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, \n"
                        + "       service_type, goods_to_carry, jorney_purpose, parking, replace_date, \n"
                        + "       remarks,CURRENT_TIMESTAMP\n"
                        + "  FROM " + TableList.VH_PERMIT + " where regn_no = ? AND PMT_NO=? order by vh_permit.moved_on DESC limit 1";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, dobj.getNew_regn_no().toUpperCase());
                ps.setString(2, dobj.getRegn_no());
                ps.setString(3, dobj.getPmt_no());
                ps.executeUpdate();
            }
            if (pmtType == Integer.valueOf(TableConstants.AITP)
                    || pmtType == Integer.valueOf(TableConstants.NATIONAL_PERMIT)) {
                query = "INSERT INTO " + TableList.VT_PERMIT_HOME_AUTH + "(\n"
                        + "            regn_no, pmt_no, auth_no, auth_fr, auth_to, op_dt, pur_cd)\n"
                        + " SELECT ?, pmt_no, auth_no, auth_fr, auth_to, CURRENT_TIMESTAMP, pur_cd\n"
                        + " FROM " + TableList.VH_PERMIT_HOME_AUTH + " where regn_no = ? AND pmt_no = ? order by vh_permit_home_auth.moved_on DESC limit 1;";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, dobj.getNew_regn_no().toUpperCase());
                ps.setString(2, dobj.getRegn_no());
                ps.setString(3, dobj.getPmt_no());
                int count = ps.executeUpdate();
                if (count < 1) {
                    throw new VahanException("Authorization details not found.");
                }
            }
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            ServerUtil.fileFlow(tmgr, status_dobj);
            tmgr.commit();
            flage = "Success";
        } catch (VahanException e) {
            flage = "";
            throw e;
        } catch (SQLException e) {
            flage = "";
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            flage = "";
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
        return flage;
    }

    public static Date getDateFromStr_ddMMyyy(String dt) {
        SimpleDateFormat pgf = new SimpleDateFormat("dd-MM-yyyy");
        Date convertedDate = null;

        if (dt != null) {
            try {
                convertedDate = pgf.parse(dt);
            } catch (ParseException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return convertedDate;
    }

    public static Date convertStringYYYYMMDDToDDMMYYYYDate(String strDate) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date = null;
        try {
            date = sdf.parse(strDate);

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);

        }
        return date;
    }

    public static java.sql.Date getPostgresDate(Date dt) {
        java.sql.Date sqlDate = null;
        if (dt != null) {
            sqlDate = new java.sql.Date(dt.getTime());
        }
        return sqlDate;
    }

    public static String getDDMMYYYYDateString(Date dt) {
        SimpleDateFormat pgf = new SimpleDateFormat("dd-MM-yyyy");
        String convertedDate = "";
        if (dt != null) {
            convertedDate = pgf.format(dt);
        }
        return convertedDate;
    }

    public static List getVerificationPendingList() {
        List list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getVerificationList");
            String sql = "select appl_no, pmt_no,regn_no from  " + TableList.VA_PERMIT_TRANSACTION + " ";
            ps = tmgr.prepareStatement(sql);
            rs = tmgr.fetchDetachedRowSet();
            list.add(new SelectItem("-1", "SELECT"));
            while (rs.next()) {
                list.add(new SelectItem(rs.getString("regn_no"), ":APPLICATION_NO:" + rs.getString("appl_no")));
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
        return list;
    }

    public static boolean getvhPermit_statueRegnno(String regn_no, String pmtNo) {
        boolean flage = false;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("Owner_Impl");
            String query;
            query = "select hist.regn_no from " + TableList.VH_PERMIT + " hist\n"
                    + "inner join " + TableList.VT_PERMIT_TRANSACTION + " trans on trans.regn_no = hist.regn_no and trans.pmt_no = hist.pmt_no\n"
                    + "where hist.regn_no=? AND hist.pmt_no=? AND hist.pmt_status=?";
            if (CommonUtils.isNullOrBlank(pmtNo)) {
                query = "select hist.regn_no from " + TableList.VH_PERMIT + " hist\n"
                        + "inner join " + TableList.VT_PERMIT_TRANSACTION + " trans on trans.regn_no = hist.regn_no\n"
                        + "where hist.regn_no=? AND hist.pmt_status=?";
            }
            ps = tmgr.prepareStatement(query);
            int i = 1;
            ps.setString(i++, regn_no);
            if (!CommonUtils.isNullOrBlank(pmtNo)) {
                ps.setString(i++, pmtNo);
            }
            ps.setString(i++, VT_PERMIT_STATUS_SURRENDER);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                flage = true;
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
        return flage;
    }

    public void rebackStatus(Status_dobj status_dobj, String compairChange, SurrenderPermitDobj dobj, String appl_no) {
        Status_dobj staDobj = status_dobj;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("rebackStatus");
            if (!compairChange.isEmpty()) {
                vhaPermitTransactionToVaPermitTransaction(tmgr, appl_no);
                UpateVaPermitTransaction(tmgr, appl_no, dobj);
            }
            insertIntoVhaChangedData(tmgr, appl_no, compairChange);
            staDobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            ServerUtil.fileFlow(tmgr, staDobj);
            tmgr.commit();
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
    }

    public void stayOnTheSameStage(String Change, SurrenderPermitDobj dobj, String appl_no) {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("Stay On The Same Stage");
            if (!Change.isEmpty()) {
                vhaPermitTransactionToVaPermitTransaction(tmgr, appl_no);
                UpateVaPermitTransaction(tmgr, appl_no, dobj);
            }
            tmgr.commit();
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
    }

    public static String checkPermitIn_VT_Permit(String regn_no) {
        String Query = "";
        String pmt_detail = "";
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("checkPermitIn_VT_Permit");
            Query = "SELECT 'Registration No: '||REGN_NO|| ' already have a permit. The Permit Number is : ' ||PMT_NO as pmt_detail FROM " + TableList.VT_PERMIT + " WHERE REGN_NO = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regn_no);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                pmt_detail = rs.getString("pmt_detail");
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
        return pmt_detail;
    }

    public static String checkSameVehClass(String stateCd, String old_regn_no, String new_regn_no, int trans_pur_cd) {
        String Query;
        String status = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("checkSameVehClass");
            Query = "SELECT vh_class from " + TableList.VT_OWNER + " where state_cd = ? AND regn_no = ?";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, stateCd);
            ps.setString(i++, old_regn_no);
            RowSet rs1 = tmgr.fetchDetachedRowSet();
            if (rs1.next()) {
                if (rs1.getInt("vh_class") > 0 && ServerUtil.isTransport(rs1.getInt("vh_class"), null)) {
                    Query = "select case when ( (SELECT vh_class from " + TableList.VT_OWNER + " where state_cd = ? AND regn_no = ?) = (SELECT vh_class from " + TableList.VT_OWNER + " where state_cd = ? AND regn_no = ? and status in (?,?)) ) THEN 'YES' ELSE 'NO' END RS ";
                    ps = tmgr.prepareStatement(Query);
                    i = 1;
                    ps.setString(i++, stateCd);
                    ps.setString(i++, old_regn_no);
                    ps.setString(i++, stateCd);
                    ps.setString(i++, new_regn_no);
                    ps.setString(i++, "A");
                    ps.setString(i++, "Y");
                    RowSet rs2 = tmgr.fetchDetachedRowSet();
                    if (rs2.next()) {
                        status = rs2.getString("RS");
                    }
                } else if (!ServerUtil.isTransport(rs1.getInt("vh_class"), null)) {
                    Query = "select * from " + TableList.VH_CONVERSION + " where state_cd = ? AND old_regn_no = ? order by moved_on DESC";
                    ps = tmgr.prepareStatement(Query);
                    i = 1;
                    ps.setString(i++, stateCd);
                    ps.setString(i++, old_regn_no);
                    RowSet rs3 = tmgr.fetchDetachedRowSet();
                    if (rs3.next()) {
                        if (ServerUtil.isTransport(rs3.getInt("old_vch_class"), null)) {
                            Query = "select case when " + rs3.getInt("old_vch_class") + " = (SELECT vh_class from " + TableList.VT_OWNER + " where state_cd = ? AND regn_no = ? and status in (?,?)) THEN 'YES' ELSE 'NO' END RS ";
                            ps = tmgr.prepareStatement(Query);
                            i = 1;
                            ps.setString(i++, stateCd);
                            ps.setString(i++, new_regn_no);
                            ps.setString(i++, "A");
                            ps.setString(i++, "Y");
                            RowSet rs4 = tmgr.fetchDetachedRowSet();
                            if (rs4.next()) {
                                status = rs4.getString("RS");
                            }
                        }
                    }
                }
            } else if (trans_pur_cd == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD || trans_pur_cd == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD) {
                Query = "select case when ( (SELECT vh_class from " + TableList.VH_OWNER + " where state_cd = ? AND regn_no = ? order by moved_on DESC limit 1) = (SELECT vh_class from " + TableList.VT_OWNER + " where state_cd = ? AND regn_no = ? and status in (?,?)) ) THEN 'YES' ELSE 'NO' END RS ";
                ps = tmgr.prepareStatement(Query);
                int j = 1;
                ps.setString(j++, stateCd);
                ps.setString(j++, old_regn_no);
                ps.setString(j++, stateCd);
                ps.setString(j++, new_regn_no);
                ps.setString(j++, "A");
                ps.setString(j++, "Y");
                RowSet rs3 = tmgr.fetchDetachedRowSet();
                if (rs3.next()) {
                    status = rs3.getString("RS");
                }
            }

        } catch (VahanException ve) {
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
        return status;
    }

    public static String checkVhClassOfReplacedVehicle(String stateCd, String old_regn_no, String new_regn_no) {
        String Query = "";
        String pmt_detail = "";
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String status = "";
        try {
            tmgr = new TransactionManager("checkVhClassOfReplacedVehicle");
            Query = "select case when ( (SELECT seat_cap from " + TableList.VT_OWNER + " where state_cd = ? AND regn_no = ? and vh_class=? Limit 1 ) = (SELECT seat_cap from " + TableList.VT_OWNER + " where state_cd = ? AND regn_no = ? and vh_class= ? and status in (?,?)) ) THEN 'YES' ELSE 'NO' END RS ";
            ps = tmgr.prepareStatement(Query);
            int j = 1;
            ps.setString(j++, stateCd);
            ps.setString(j++, old_regn_no);
            ps.setInt(j++, 57);
            ps.setString(j++, stateCd);
            ps.setString(j++, new_regn_no);
            ps.setInt(j++, 86);
            ps.setString(j++, "A");
            ps.setString(j++, "Y");
            RowSet rs3 = tmgr.fetchDetachedRowSet();
            if (rs3.next()) {
                status = rs3.getString("RS");
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
        return status;
    }

    public boolean checkPermitInVtPermit(String regn_no) {
        String Query;
        boolean status = false;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("checkPermitIn_VT_Permit");
            Query = "SELECT * from " + TableList.VT_PERMIT + " where regn_no=?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regn_no);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                status = true;
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
        return status;
    }

    public boolean checkVehicleTransferDetails(String regn_no, String state_cd, int off_cd) {
        String Query;
        boolean flag = false;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("checkVehicleTransferDetails");
            Query = "select state_to,off_to from " + TableList.VT_NOC + "   where  regn_no=? and state_cd=? order by noc_dt desc limit 1";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regn_no.toUpperCase());
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                if (state_cd.equals(rs.getString("state_to")) && off_cd == rs.getInt("off_to")) {
                    flag = true;
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
        return flag;
    }

    public static void insertVhPermitHomeAuthToVtPermitHomeAuth(String regn_no, TransactionManager tmgr) throws SQLException {
        String Query;
        PreparedStatement ps = null;
        Query = "INSERT INTO " + TableList.VH_PERMIT_HOME_AUTH + "(\n"
                + "            regn_no, pmt_no, auth_no, auth_fr, auth_to, op_dt, moved_on, \n"
                + "            moved_by, pur_cd)\n"
                + "    SELECT regn_no, pmt_no, auth_no, auth_fr, auth_to, op_dt,CURRENT_TIMESTAMP,\n"
                + "            ?,pur_cd\n"
                + "           FROM " + TableList.VT_PERMIT_HOME_AUTH + " as auth where auth.regn_no = ?";
        ps = tmgr.prepareStatement(Query);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, regn_no);
        ps.executeUpdate();

        String query = "DELETE FROM " + TableList.VT_PERMIT_HOME_AUTH + " where regn_no =?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, regn_no);
        ps.executeUpdate();
    }

    public static List getToatalNoOfPermitList(String stateCd, String regn_no) {
        String Query;
        PreparedStatement ps;
        List<PassengerPermitDetailDobj> multiPermitDetails = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getToatalNoOfPermitList");
            multiPermitDetails = new ArrayList<>();
            Query = "SELECT PMT_NO,PMT.PUR_CD AS PUR_CD,MST.descr AS descr,to_char(VALID_FROM,'DD-MON-YYYY') AS VALID_FROM ,to_char(VALID_UPTO, 'DD-MON-YYYY') AS VALID_UPTO FROM " + TableList.VT_PERMIT + " PMT\n"
                    + "INNER JOIN " + TableList.TM_PURPOSE_MAST + " MST ON MST.PUR_CD = PMT.PUR_CD \n"
                    + "WHERE REGN_NO = ? and state_cd = ? \n"
                    + "UNION\n"
                    + "SELECT PMT_NO,TEMP.PUR_CD AS PUR_CD,MST.descr AS descr,to_char(VALID_FROM,'DD-MON-YYYY') AS VALID_FROM ,to_char(VALID_UPTO, 'DD-MON-YYYY') AS VALID_UPTO FROM " + TableList.VT_TEMP_PERMIT + " TEMP\n"
                    + "INNER JOIN " + TableList.TM_PURPOSE_MAST + " MST ON MST.PUR_CD = TEMP.PUR_CD \n"
                    + "WHERE REGN_NO = ?  and state_cd = ? ";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setString(3, regn_no);
            ps.setString(4, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                PassengerPermitDetailDobj dobj = new PassengerPermitDetailDobj();
                dobj.setPmt_no(rs.getString("PMT_NO"));
                dobj.setPaction_code(rs.getString("PUR_CD"));
                dobj.setPaction(rs.getString("DESCR"));
                dobj.setValidFromInString(rs.getString("VALID_FROM"));
                dobj.setValidUptoInString(rs.getString("VALID_UPTO"));
                multiPermitDetails.add(dobj);
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
        return multiPermitDetails;
    }

    public static List getToatalNoOfPermitSurrInSameRegn(String regn_no) {
        String Query;
        PreparedStatement ps;
        List<PassengerPermitDetailDobj> multiPermitDetails = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getToatalNoOfPermitSurrInSameRegn");
            multiPermitDetails = new ArrayList<>();
            Query = "SELECT a.pmt_no,a.trans_pur_cd,c.descr,to_char(b.valid_from,'DD-MON-YYYY') as valid_from,to_char(b.valid_upto,'DD-MON-YYYY') as valid_upto from " + TableList.VT_PERMIT_TRANSACTION + " a\n"
                    + "inner join " + TableList.VH_PERMIT + " b on a.regn_no = b.regn_no and a.pmt_no = b.pmt_no and pmt_status = ? \n"
                    + "inner join " + TableList.TM_PURPOSE_MAST + " c on a.trans_pur_cd = c.pur_cd\n"
                    + "where a.regn_no = ?;";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, "SUR");
            ps.setString(2, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                PassengerPermitDetailDobj dobj = new PassengerPermitDetailDobj();
                dobj.setPmt_no(rs.getString("PMT_NO"));
                dobj.setPaction_code(rs.getString("TRANS_PUR_CD"));
                dobj.setPaction(rs.getString("DESCR"));
                dobj.setValidFromInString(rs.getString("VALID_FROM"));
                dobj.setValidUptoInString(rs.getString("VALID_UPTO"));
                multiPermitDetails.add(dobj);
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
        return multiPermitDetails;
    }

    public int getTransPurCdFromVtPmtTranaction(String regn_no) throws VahanException {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        int trans_pur_cd = 0;
        try {
            tmgr = new TransactionManager("getTransPurCdFromVtPmtTranaction");
            String Query = "SELECT * FROM " + TableList.VT_PERMIT_TRANSACTION + " where pur_cd = ? AND regn_no = ? order by op_dt desc";
            ps = tmgr.prepareStatement(Query);
            ps.setInt(1, TableConstants.VM_PMT_SURRENDER_PUR_CD);
            ps.setString(2, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                trans_pur_cd = rs.getInt("trans_pur_cd");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Getting Details from Permit Transaction Purpose Code");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return trans_pur_cd;
    }

    public boolean checkTOCasePerform(String regn_no, String newRegnNo) {
        PreparedStatement ps;
        TransactionManager tmgr = null;;
        boolean flag = true;
        try {
            tmgr = new TransactionManager("getTransPurCdFromVtPmtTranaction");
            String Query = "select * from " + TableList.VH_TO + " where moved_on > (select op_dt from " + TableList.VT_PERMIT_TRANSACTION + " where regn_no = ?) AND regn_no = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regn_no);
            ps.setString(2, newRegnNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                flag = false;
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
        return flag;
    }

    public List<OwnerDetailsDobj> toOwnerDetails(String regn_no) {
        PreparedStatement ps;
        TransactionManager tmgr = null;;
        List<OwnerDetailsDobj> listExistingOwnerDetails = null;
        OwnerDetailsDobj dobj = null;
        try {
            tmgr = new TransactionManager("toOwnerDetails");
            String Query = "select owner_sr, owner_name,owner_cd, owner_ctg, f_name, c_add1, c_add2, c_add3, \n"
                    + " c_district,c_pincode, c_state, p_add1, p_add2, p_add3, p_district, \n"
                    + " p_pincode,p_state from " + TableList.VH_TO + " where regn_no = ? order by moved_on DESC";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            listExistingOwnerDetails = new ArrayList<>();
            while (rs.next()) {
                dobj = new OwnerDetailsDobj();
                dobj.setOwner_sr(rs.getInt("owner_sr"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setF_name(rs.getString("f_name"));
                dobj.setC_add1(rs.getString("c_add1"));
                dobj.setC_add2(rs.getString("c_add2"));
                dobj.setC_add3(rs.getString("c_add3"));
                dobj.setC_district(rs.getInt("c_pincode"));
                dobj.setC_pincode(rs.getInt("c_district"));
                dobj.setP_add1(rs.getString("p_add1"));
                dobj.setP_add2(rs.getString("p_add2"));
                dobj.setP_add3(rs.getString("p_add3"));
                dobj.setP_district(rs.getInt("p_district"));
                listExistingOwnerDetails.add(dobj);
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
        return listExistingOwnerDetails;
    }

    public List<OwnerDetailsDobj> caOwnerDetails(String regn_no) {
        PreparedStatement ps;
        TransactionManager tmgr = null;;
        List<OwnerDetailsDobj> listExistingOwnerDetails = null;
        OwnerDetailsDobj dobj = null;
        try {
            tmgr = new TransactionManager("toOwnerDetails");
            String Query = "select owner_sr, owner_name,owner_cd, f_name, a.c_add1, a.c_add2, a.c_add3,\n"
                    + "a.c_district,a.c_pincode, a.c_state, a.p_add1, a.p_add2, a.p_add3, a.p_district, a.p_pincode, a.p_state\n"
                    + "from " + TableList.VH_CA + " a\n"
                    + "inner join " + TableList.VT_OWNER + " b on a.regn_no = b.regn_no\n"
                    + "inner join " + TableList.VT_PERMIT_TRANSACTION + " c on a.regn_no = c.regn_no\n"
                    + "where a.regn_no = ? and a.moved_on > c.op_dt order by moved_on DESC";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            listExistingOwnerDetails = new ArrayList<>();
            while (rs.next()) {
                dobj = new OwnerDetailsDobj();
                dobj.setOwner_sr(rs.getInt("owner_sr"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setF_name(rs.getString("f_name"));
                dobj.setC_add1(rs.getString("c_add1"));
                dobj.setC_add2(rs.getString("c_add2"));
                dobj.setC_add3(rs.getString("c_add3"));
                dobj.setC_district(rs.getInt("c_pincode"));
                dobj.setC_pincode(rs.getInt("c_district"));
                dobj.setP_add1(rs.getString("p_add1"));
                dobj.setP_add2(rs.getString("p_add2"));
                dobj.setP_add3(rs.getString("p_add3"));
                dobj.setP_district(rs.getInt("p_district"));
                listExistingOwnerDetails.add(dobj);
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
        return listExistingOwnerDetails;
    }

    public String permitSurrMsg(String regn_no, boolean multi_pmt_allow) {
        PreparedStatement ps;
        TransactionManagerReadOnly tmgr = null;;
        String msg = "";
        try {
            tmgr = new TransactionManagerReadOnly("permitSurrMsg");
            String pmtNo = CommonPermitPrintImpl.getPmtNoThroughVtPermit(regn_no);
            if (multi_pmt_allow && CommonUtils.isNullOrBlank(pmtNo)) {
                pmtNo = null;
            }
            if (CommonUtils.isNullOrBlank(pmtNo)) {
                String Query = "Select 'Permit is Surrendered against this vehicle, Please first Restore.' as msg from " + TableList.VT_PERMIT_TRANSACTION + " a\n"
                        + " inner join " + TableList.VH_PERMIT + " b on a.regn_no = b.regn_no AND b.pmt_status = 'SUR' and a.state_cd = b.state_cd\n"
                        + " where  a.state_cd = ? and a.regn_no = ? and trans_pur_cd <> ? order by b.op_dt DESC limit 1";
                ps = tmgr.prepareStatement(Query);
                int i = 1;
                ps.setString(i++, Util.getUserStateCode());
                ps.setString(i++, regn_no);
                ps.setInt(i++, TableConstants.VM_PMT_REPLACE_VEH_PUR_CD);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    msg = rs.getString("msg");
                }
            } else {
                String Query = "Select 'Permit is Surrendered against this vehicle, Please first Restore.' as msg from " + TableList.VT_PERMIT_TRANSACTION + " a\n"
                        + " inner join " + TableList.VH_PERMIT + " b on a.regn_no = b.regn_no AND b.pmt_status = 'SUR' and a.state_cd = b.state_cd\n"
                        + " where  a.state_cd = ? and a.regn_no = ? and a.pmt_no = ? and trans_pur_cd <> ? order by b.op_dt DESC limit 1";
                ps = tmgr.prepareStatement(Query);
                int i = 1;
                ps.setString(i++, Util.getUserStateCode());
                ps.setString(i++, regn_no);
                ps.setString(i++, pmtNo);
                ps.setInt(i++, TableConstants.VM_PMT_REPLACE_VEH_PUR_CD);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    msg = rs.getString("msg");
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
        return msg;
    }

    public String permitSurrPrvMsg(String regn_no, int pmt_type) {
        PreparedStatement ps;
        TransactionManagerReadOnly tmgr = null;;
        String msg = "";
        try {
            tmgr = new TransactionManagerReadOnly("permitSurrPrvMsg");
            String Query = "Select 'Permit is Surrendered against this vehicle, Please first Restore.' as msg from " + TableList.VT_PERMIT_TRANSACTION + " a\n"
                    + " inner join " + TableList.VH_PERMIT + " b on a.regn_no = b.regn_no AND b.pmt_status = 'SUR' and a.state_cd = b.state_cd\n"
                    + " where  a.state_cd = ? and a.regn_no = ? and trans_pur_cd <> ? and b.pmt_type=?  order by b.op_dt DESC limit 1";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, Util.getUserStateCode());
            ps.setString(i++, regn_no);
            ps.setInt(i++, TableConstants.VM_PMT_REPLACE_VEH_PUR_CD);
            ps.setInt(i++, pmt_type);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                msg = rs.getString("msg");
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
        return msg;
    }

    public String getReAssigmentOldToNew(String oldRegnNo) throws VahanException {
        PreparedStatement ps;
        TransactionManager tmgr = null;;
        String newRegnNo = "";
        try {
            tmgr = new TransactionManager("getReAssigmentOldToNew");
            String Query = "SELECT new_regn_no from vh_re_assign where old_regn_no = ? \n"
                    + "and moved_on > (SELECT max(moved_on) as moved_on from permit.vh_permit where regn_no = ? and pmt_status = ? and state_cd = ?) \n"
                    + "order by moved_on DESC limit 1";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, oldRegnNo);
            ps.setString(2, oldRegnNo);
            ps.setString(3, "SUR");
            ps.setString(4, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                newRegnNo = rs.getString("new_regn_no");
            } else {
                throw new VahanException("Please Do first Re-Assigment Case from Register Vehicle form");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Getting Details from Re-Assigment Table");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return newRegnNo;
    }

    public String getReAssigmentNewToOld(String newRegnNo) throws VahanException {
        PreparedStatement ps;
        TransactionManager tmgr = null;;
        String oldRegnNo = "";
        try {
            tmgr = new TransactionManager("getReAssigmentNewToOld");
            String Query = "SELECT old_regn_no from vh_re_assign where new_regn_no = ? order by moved_on DESC limit 1";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, newRegnNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                oldRegnNo = rs.getString("old_regn_no");
            } else {
                throw new VahanException("Please Do first Re-Assigment Case from Register Vehicle form");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Getting Details from Re-Assigment Table");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return oldRegnNo;
    }

    public List getTransferCaseForSurrender() {
        RowSet rs;
        PreparedStatement ps;
        TransactionManager tmgr = null;
        List transferList = null;
        try {
            tmgr = new TransactionManager("getTransferCaseForSurrender");
            transferList = new ArrayList<>();
            String sql = "select transfer_flag,descr from " + TableList.VM_PERMIT_TRANSFER_FLAG + "";
            ps = tmgr.prepareStatement(sql);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                transferList.add(new SelectItem(rs.getString("transfer_flag"), rs.getString("descr")));
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
        return transferList;

    }

    public String[] getPmtSurFeeDetails(String regnNo, int purCd, String sur_porpose) {
        String[] values = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = "";
        try {
            tmgr = new TransactionManager("getPmtSurFeeDetails");
            String allottedOffCd = CommonPermitPrintImpl.getAllottedOffCd(Util.getUserOffCode(), Util.getUserStateCode());
            if (sur_porpose.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD))) {
                sql = "select rcpt_no, rcpt_dt::date from vt_fee where state_cd=? and off_cd=? and regn_no=? and pur_cd in (4,5) order by rcpt_dt desc limit 1";
            } else if (purCd == TableConstants.VM_CHANGE_OF_ADDRESS_PUR_CD) {
                sql = "select rcpt_no, rcpt_dt::date from vt_fee where state_cd=? and off_cd in (" + allottedOffCd + ") and regn_no=? and pur_cd=? order by rcpt_dt desc limit 1";
            } else {
                sql = "select rcpt_no, rcpt_dt::date from vt_fee where state_cd=? and off_cd=? and regn_no=? and pur_cd=? order by rcpt_dt desc limit 1";
            }
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            if (sur_porpose.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD))) {
                ps.setInt(2, Util.getUserOffCode());
                ps.setString(3, regnNo);
            } else if (purCd == TableConstants.VM_CHANGE_OF_ADDRESS_PUR_CD) {
                ps.setString(2, regnNo);
                ps.setInt(3, purCd);
            } else {
                ps.setInt(2, Util.getUserOffCode());
                ps.setString(3, regnNo);
                ps.setInt(4, purCd);
            }
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                values = new String[2];
                values[0] = rs.getString("rcpt_no");
                values[1] = rs.getString("rcpt_dt");
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
        return values;
    }

    public boolean checkApplPendingStatusInOnlineApp(String regn_no) {
        String[] values = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        boolean flag = false;
        try {
            tmgr = new TransactionManager("checkApplPendingStatusInOnlineApp");
            String sql = "select a.appl_no from " + TableList.VA_DETAILS_APPL + " a inner join " + TableList.VA_STATUS_APPL + " b on a.state_cd = b.state_cd "
                    + " and a.off_cd = b.off_cd and a.pur_cd = b.pur_cd  and a.appl_no = b.appl_no "
                    + " where a.state_cd=?  and a.regn_no=?  and a.pur_cd in (26,27,39,35,36,34) and b.moved_from_online <> 'Y'"
                    + " order by a.appl_dt desc limit 1 ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, regn_no);
            RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
            if (rs1.next()) {
                flag = true;
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
        return flag;
    }
}
