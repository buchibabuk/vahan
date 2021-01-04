/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.bean.permit.AITPStateCoveredBean;
import nic.vahan.form.dobj.DisAppNoticeDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.permit.PermitHomeAuthDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.PermitCheckDetailsDobj;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.sendSMS;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class PermitHomeAuthImpl {

    private static final Logger LOGGER = Logger.getLogger(PermitHomeAuthImpl.class);
    public String Query = "";
    RowSet rs;

    public String insert_into_VA_PERMIT_HOME(PermitHomeAuthDobj dobj, int pmtType, InsDobj ins_dobj, int pmt_catg, boolean renderPeriod, String periodMode, String period, AITPStateCoveredBean aitpStateCovered) throws VahanException {
        String appl_no = "", authNo = "";
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        PermitCheckDetailsImpl pmtDtlsImpl = null;
        try {
            if (pmtType == Integer.valueOf(TableConstants.GOODS_PERMIT)
                    && ("HP").contains(Util.getUserStateCode())) {
                pmtType = Integer.valueOf(TableConstants.NATIONAL_PERMIT);
            }
            tmgr = new TransactionManager("insert_into_VA_PERMIT_HOME");
            appl_no = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
            authNo = ServerUtil.getUniquePermitNo(tmgr, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), pmtType, pmt_catg, "H");
            dobj.setAuthNo(authNo);
            insert_va_permit_home_auth(tmgr, appl_no, dobj);
            if (renderPeriod) {
                insert_va_permit_home_authPeriod(tmgr, appl_no, dobj, periodMode, period);
            }
            if (ins_dobj != null) {
                pmtDtlsImpl = new PermitCheckDetailsImpl();
                pmtDtlsImpl.insertIntoVaInsurance(appl_no, dobj.getRegnNo().toUpperCase(), ins_dobj, tmgr);
            }
            if (aitpStateCovered != null && aitpStateCovered.isRender_payment_table_aitp() && pmtType == Integer.valueOf(TableConstants.AITP) && aitpStateCovered.getPaymentList() != null && aitpStateCovered.getPaymentList().size() > 0) {
                new PassengerPermitDetailImpl().insertPaymentDetailForAITP(aitpStateCovered.getPaymentList(), appl_no, dobj.getRegnNo(), TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD, tmgr);
            }
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
            status.setAppl_no(appl_no);
            status.setPur_cd(TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD);
            status.setAction_cd(TableConstants.TM_ROLE_PMT_HOME_AUTH_RENEW_ENTRY);
            status.setFlow_slno(1);
            status.setFile_movement_slno(1);
            if (CommonUtils.isNullOrBlank(Util.getEmpCode())) {
                status.setEmp_cd(0);
            } else {
                status.setEmp_cd(Long.parseLong(Util.getEmpCode()));
            }
            status.setRegn_no(dobj.getRegnNo());
            status.setOffice_remark("");
            status.setPublic_remark("");
            status.setStatus("N");
            status.setState_cd(Util.getUserStateCode());
            status.setOff_cd(Util.getSelectedSeat().getOff_cd());
            ServerUtil.fileFlowForNewApplication(tmgr, status);
            status.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
            status.setStatus(TableConstants.STATUS_COMPLETE);
            status.setAppl_no(appl_no);
            status.setOffice_remark("");
            status.setPublic_remark("");
            ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, appl_no,
                    TableConstants.TM_ROLE_PMT_HOME_AUTH_RENEW_ENTRY, TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD, null, tmgr);
            ServerUtil.fileFlow(tmgr, status);
            tmgr.commit();
        } catch (VahanException ve) {
            appl_no = "";
            throw ve;
        } catch (Exception e) {
            appl_no = "";
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                appl_no = "";
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
        }
        return appl_no;
    }

    public PermitHomeAuthDobj getPermitDetails(String regn_no, int pmtType, int pmtCatg) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        PermitHomeAuthDobj dobj = null;
        try {
            if (pmtType == Integer.valueOf(TableConstants.GOODS_PERMIT)
                    && ("HP").contains(Util.getUserStateCode())) {
                pmtType = Integer.valueOf(TableConstants.NATIONAL_PERMIT);
            }
            tmgr = new TransactionManager("insert_into_VA_PERMIT_HOME");
            Query = "SELECT TAX_FROM,TAX_UPTO,aut.auth_to FROM " + TableList.VT_TAX + " tax\n"
                    + "INNER JOIN " + TableList.VT_PERMIT_HOME_AUTH + " aut on aut.regn_no = tax.regn_no\n"
                    + "WHERE tax.REGN_NO=? order by  tax_upto DESC limit 1";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regn_no);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new PermitHomeAuthDobj();
                dobj.setTaxFrom(rs.getDate("TAX_FROM"));
                dobj.setTaxUpto(rs.getDate("TAX_UPTO"));
                dobj.setPrvAuthUpto(rs.getDate("auth_to"));
                if (Util.getUserStateCode().equalsIgnoreCase("PY") && pmtType == Integer.parseInt(TableConstants.AITP) && pmtCatg == TableConstants.PY_AITP_SZ_CATG) {
                    dobj.setAuthNo(ServerUtil.getUniquePermitNo(tmgr, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), pmtType, pmtCatg, "H"));
                } else {
                    dobj.setAuthNo(ServerUtil.getUniquePermitNo(tmgr, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), pmtType, 0, "H"));
                }
            }
            if (dobj == null || (dobj.getTaxUpto() == null || dobj.getTaxUpto().getTime() < new Date().getTime())) {
                Query = "SELECT clear_fr,clear_to,aut.auth_to FROM " + TableList.VT_TAX_CLEAR + " tax\n"
                        + "INNER JOIN " + TableList.VT_PERMIT_HOME_AUTH + " aut on aut.regn_no = tax.regn_no\n"
                        + "WHERE tax.REGN_NO=? order by  clear_to DESC limit 1";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, regn_no);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    dobj = new PermitHomeAuthDobj();
                    dobj.setTaxFrom(rs.getDate("clear_fr"));
                    dobj.setTaxUpto(rs.getDate("clear_to"));
                    dobj.setPrvAuthUpto(rs.getDate("auth_to"));
                    if (Util.getUserStateCode().equalsIgnoreCase("PY") && pmtType == Integer.parseInt(TableConstants.AITP) && pmtCatg == TableConstants.PY_AITP_SZ_CATG) {
                        dobj.setAuthNo(ServerUtil.getUniquePermitNo(tmgr, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), pmtType, pmtCatg, "H"));
                    } else {
                        dobj.setAuthNo(ServerUtil.getUniquePermitNo(tmgr, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), pmtType, 0, "H"));
                    }
                }
            }
            if (dobj == null || (dobj.getTaxUpto() == null || dobj.getTaxUpto().getTime() < new Date().getTime())) {
                Query = "SELECT exem_fr,exem_to,aut.auth_to FROM " + TableList.VT_TAX_EXEM + " tax\n"
                        + "INNER JOIN " + TableList.VT_PERMIT_HOME_AUTH + " aut on aut.regn_no = tax.regn_no\n"
                        + "WHERE tax.REGN_NO=? order by  exem_to DESC limit 1";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, regn_no);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    dobj = new PermitHomeAuthDobj();
                    dobj.setTaxFrom(rs.getDate("exem_fr"));
                    dobj.setTaxUpto(rs.getDate("exem_to"));
                    dobj.setPrvAuthUpto(rs.getDate("auth_to"));
                    if (Util.getUserStateCode().equalsIgnoreCase("PY") && pmtType == Integer.parseInt(TableConstants.AITP) && pmtCatg == TableConstants.PY_AITP_SZ_CATG) {
                        dobj.setAuthNo(ServerUtil.getUniquePermitNo(tmgr, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), pmtType, pmtCatg, "H"));
                    } else {
                        dobj.setAuthNo(ServerUtil.getUniquePermitNo(tmgr, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), pmtType, 0, "H"));
                    }
                }
            }
            if (dobj == null && CommonPermitPrintImpl.getPermitAuthAllowed(Util.getUserStateCode(), pmtType, pmtCatg)) {
                PermitCheckDetailsDobj dtlsDobj = new PermitCheckDetailsImpl().getLatestTaxDtls(new PermitCheckDetailsDobj(), regn_no);
                dobj = new PermitHomeAuthDobj();
                dobj.setTaxFrom(CommonPermitPrintImpl.getDateDD_MMM_YYYY(dtlsDobj.getTaxFrom()));
                dobj.setTaxUpto(CommonPermitPrintImpl.getDateDD_MMM_YYYY(dtlsDobj.getTaxUpto()));
                dobj.setPrvAuthUpto(new Date());
                if (Util.getUserStateCode().equalsIgnoreCase("PY") && pmtType == Integer.parseInt(TableConstants.AITP) && pmtCatg == TableConstants.PY_AITP_SZ_CATG) {
                    dobj.setAuthNo(ServerUtil.getUniquePermitNo(tmgr, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), pmtType, pmtCatg, "H"));
                } else {
                    dobj.setAuthNo(ServerUtil.getUniquePermitNo(tmgr, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), pmtType, 0, "H"));
                }

            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (VahanException e) {
            throw e;
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
        }
        return dobj;
    }

    public PermitHomeAuthDobj getPermitAuthDetailsThrowNP(String regn_no, int pmtType) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        PermitHomeAuthDobj dobj = null;
        try {
            if (pmtType == Integer.valueOf(TableConstants.GOODS_PERMIT)
                    && ("HP").contains(Util.getUserStateCode())) {
                pmtType = Integer.valueOf(TableConstants.NATIONAL_PERMIT);
            }
            tmgr = new TransactionManager("getPermitAuthDetailsThrowNP");
            //String regnNoWithSpace = ServerUtil.getRegnNoWithSpace(regn_no);
            dobj = ServerUtil.getPermitDetailsFromNp(regn_no);
            if (dobj == null) {
                new VahanException("Permit authorization details not found in NP-Portal");
            }
            Query = "select * from\n"
                    + "(SELECT regn_no,tax_from,tax_upto,rcpt_dt as op_dt from vt_tax where regn_no = ?\n"
                    + "union all\n"
                    + "SELECT regn_no,clear_fr as tax_from,clear_to as tax_upto,op_dt from vt_tax_clear where regn_no = ?\n"
                    + "union all\n"
                    + "SELECT regn_no,exem_fr as tax_from,exem_to as tax_upto ,op_dt from vt_tax_exem where regn_no = ?) a order by op_dt DESC limit 1";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, regn_no);
            ps.setString(i++, regn_no);
            ps.setString(i++, regn_no);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj.setTaxFrom(rs.getDate("TAX_FROM"));
                dobj.setTaxUpto(rs.getDate("TAX_UPTO"));
                dobj.setPrvAuthUpto(dobj.getAuthFrom());
                dobj.setAuthNo(ServerUtil.getUniquePermitNo(tmgr, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), pmtType, 0, "H"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (VahanException e) {
            throw e;
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
        return dobj;
    }

    public PermitHomeAuthDobj select_IN_VA_HOME_AUTH(String appl_no) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        PermitHomeAuthDobj dobj = null;
        try {
            tmgr = new TransactionManager("insert_into_VA_PERMIT_HOME");
            Query = "SELECT APPL_NO,HOME.REGN_NO,PMT_NO,AUTH_NO,AUTH_FR,AUTH_TO,TAX_FROM,TAX_UPTO FROM " + TableList.VA_PERMIT_HOME_AUTH + " HOME\n"
                    + "INNER JOIN " + TableList.VT_TAX + " TAX ON TAX.REGN_NO = HOME.REGN_NO\n"
                    + "WHERE APPL_NO = ? order by TAX_UPTO DESC limit 1";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new PermitHomeAuthDobj();
                dobj.setApplNo(rs.getString("APPL_NO"));
                dobj.setAuthNo(rs.getString("AUTH_NO"));
                dobj.setRegnNo(rs.getString("REGN_NO"));
                dobj.setPmtNo(rs.getString("PMT_NO"));
                dobj.setAuthFrom(rs.getDate("AUTH_FR"));
                dobj.setAuthUpto(rs.getDate("AUTH_TO"));
                dobj.setTaxFrom(rs.getDate("TAX_FROM"));
                dobj.setTaxUpto(rs.getDate("TAX_UPTO"));
            }
            if (dobj == null || (dobj.getTaxUpto() == null || dobj.getTaxUpto().getTime() < new Date().getTime())) {
                Query = "SELECT HOME.APPL_NO,HOME.REGN_NO,PMT_NO,AUTH_NO,AUTH_FR,AUTH_TO,clear_fr,clear_to FROM " + TableList.VA_PERMIT_HOME_AUTH + " HOME\n"
                        + "INNER JOIN " + TableList.VT_TAX_CLEAR + " TAX ON TAX.REGN_NO = HOME.REGN_NO\n"
                        + "WHERE HOME.APPL_NO = ? order by clear_to DESC limit 1";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, appl_no);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    dobj = new PermitHomeAuthDobj();
                    dobj.setApplNo(rs.getString("APPL_NO"));
                    dobj.setAuthNo(rs.getString("AUTH_NO"));
                    dobj.setRegnNo(rs.getString("REGN_NO"));
                    dobj.setPmtNo(rs.getString("PMT_NO"));
                    dobj.setAuthFrom(rs.getDate("AUTH_FR"));
                    dobj.setAuthUpto(rs.getDate("AUTH_TO"));
                    dobj.setTaxFrom(rs.getDate("clear_fr"));
                    dobj.setTaxUpto(rs.getDate("clear_to"));
                }
            }
            if (dobj == null || (dobj.getTaxUpto() == null || dobj.getTaxUpto().getTime() < new Date().getTime())) {
                Query = "SELECT HOME.APPL_NO,HOME.REGN_NO,PMT_NO,AUTH_NO,AUTH_FR,AUTH_TO,exem_fr,exem_to FROM " + TableList.VA_PERMIT_HOME_AUTH + " HOME\n"
                        + "INNER JOIN " + TableList.VT_TAX_EXEM + " TAX ON TAX.REGN_NO = HOME.REGN_NO\n"
                        + "WHERE HOME.APPL_NO = ? order by exem_to DESC limit 1";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, appl_no);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    dobj = new PermitHomeAuthDobj();
                    dobj.setApplNo(rs.getString("APPL_NO"));
                    dobj.setAuthNo(rs.getString("AUTH_NO"));
                    dobj.setRegnNo(rs.getString("REGN_NO"));
                    dobj.setPmtNo(rs.getString("PMT_NO"));
                    dobj.setAuthFrom(rs.getDate("AUTH_FR"));
                    dobj.setAuthUpto(rs.getDate("AUTH_TO"));
                    dobj.setTaxFrom(rs.getDate("exem_fr"));
                    dobj.setTaxUpto(rs.getDate("exem_to"));
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

    public PermitHomeAuthDobj getVaHomeAuthDetails(String appl_no) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        PermitHomeAuthDobj dobj = null;
        try {
            tmgr = new TransactionManager("getVaHomeAuthDetails");
            Query = "SELECT APPL_NO,HOME.REGN_NO,PMT_NO,AUTH_NO,AUTH_FR,AUTH_TO FROM " + TableList.VA_PERMIT_HOME_AUTH + " HOME \n"
                    + "WHERE APPL_NO = ? ";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new PermitHomeAuthDobj();
                dobj.setApplNo(rs.getString("APPL_NO"));
                dobj.setAuthNo(rs.getString("AUTH_NO"));
                dobj.setRegnNo(rs.getString("REGN_NO"));
                dobj.setPmtNo(rs.getString("PMT_NO"));
                dobj.setAuthFrom(rs.getDate("AUTH_FR"));
                dobj.setAuthUpto(rs.getDate("AUTH_TO"));
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

    public void rebackStatus(Status_dobj status_dobj) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        Status_dobj statDobj = status_dobj;
        try {
            tmgr = new TransactionManager("rebackStatus");
            statDobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            ServerUtil.fileFlow(tmgr, statDobj);
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

    public void moveAndSave(Status_dobj status_dobj, String action_cd, String appl_no, String regnNo, Long mobile_no, AITPStateCoveredBean statecoveredBean, boolean update_home_auth_verify, Date p_auth_fr, Date p_auth_to, int p_pmt_type) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String state_cd = "";
        int pmt_type = 0;
        try {
            tmgr = new TransactionManager("moveAndSave");
            if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_HOME_AUTH_RENEW_APPROVAL))) {
                String query = "SELECT * FROM " + TableList.VA_INSURANCE + " WHERE APPL_NO = ?";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, appl_no);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    PassengerPermitDetailImpl passImpl = new PassengerPermitDetailImpl();
                    passImpl.insertIntoVTinsAndDeleteVaIns(tmgr, appl_no, regnNo);
                }
                query = "SELECT state_cd,off_cd,pmt_type,pmt_no FROM " + TableList.VT_PERMIT + " WHERE REGN_NO = ?";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, regnNo);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    state_cd = rs.getString("state_cd");
                    pmt_type = rs.getInt("pmt_type");
                    if (("HP").equalsIgnoreCase(rs.getString("state_cd"))
                            && TableConstants.GOODS_PERMIT.equalsIgnoreCase(rs.getString("pmt_type"))) {
                        query = "INSERT INTO " + TableList.VH_PERMIT + "(\n"
                                + "            state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                                + "            valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, \n"
                                + "            service_type, goods_to_carry, jorney_purpose, parking, replace_date, \n"
                                + "            remarks, op_dt, pmt_status, reason, moved_on,moved_by)\n"
                                + "   SELECT state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                                + "       valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, \n"
                                + "       service_type, goods_to_carry, jorney_purpose, parking, replace_date, \n"
                                + "       remarks, op_dt,?,?,CURRENT_TIMESTAMP,?\n"
                                + "  FROM " + TableList.VT_PERMIT + " where regn_no = ? ";
                        ps = tmgr.prepareStatement(query);
                        ps.setString(1, "AUT");
                        ps.setString(2, "ALLOWED AUTH");
                        ps.setString(3, Util.getEmpCode());
                        ps.setString(4, regnNo);
                        ps.executeUpdate();

                        query = "UPDATE " + TableList.VT_PERMIT + " SET PMT_TYPE = ? where regn_no =?";
                        ps = tmgr.prepareStatement(query);
                        ps.setInt(1, Integer.valueOf(TableConstants.NATIONAL_PERMIT));
                        ps.setString(2, regnNo);
                        ps.executeUpdate();
                        new PermitAuthCompleteImpl().insertInPermitAuthComplete(tmgr, Util.getUserStateCode(), rs.getInt("off_cd"), appl_no, regnNo, rs.getString("pmt_no"));
                    }
                }
                vt_permit_home_To_VH_permit_home(tmgr, regnNo);
                InsertVaPermitHomeAuthDeleteVTPermitHomeAuth(tmgr, appl_no);
                if (pmt_type == Integer.parseInt(TableConstants.AITP)) {
                    va_permit_home_period_VHA_permit_homePeriod(tmgr, appl_no, regnNo);
                    deleteIntoHomeAuth(tmgr, appl_no, TableList.VA_PERMIT_HOME_AUTH_PERIOD, null);
                }
                String docId = CommonPermitPrintImpl.getPermitDocIdForQuery(CommonPermitPrintImpl.getPermitDocId(tmgr, pmt_type, TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD, Util.getUserStateCode()));
                String[] beanData = {docId, appl_no, regnNo};
                if (!("DL,UP".contains(Util.getUserStateCode()) && pmt_type == Integer.parseInt(TableConstants.NATIONAL_PERMIT))) {
                    CommonPermitPrintImpl.insertVaPermitPrint(tmgr, beanData);
                }
                if ("DL,UP,GJ".contains(state_cd) && pmt_type == Integer.parseInt(TableConstants.NATIONAL_PERMIT)) {
                    ServerUtil.updateNPAuthorizationDetails(regnNo, appl_no, tmgr, TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD);
                }
            }
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            ServerUtil.fileFlow(tmgr, status_dobj);
            if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_HOME_AUTH_RENEW_APPROVAL))) {
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                if (state_cd.equalsIgnoreCase("DL")) {
                    ServerUtil.updateOnlinePermitStatus(tmgr, status_dobj);
                }
            }

            if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_HOME_AUTH_RENEW_VERIFICATION)) && statecoveredBean != null && statecoveredBean.isRender_payment_table_aitp() && pmt_type == Integer.valueOf(TableConstants.AITP) && statecoveredBean.getPaymentList() != null && statecoveredBean.getPaymentList().size() > 0) {
                new PassengerPermitDetailImpl().updatePaymentDetailForAITP(statecoveredBean.getPaymentList(), appl_no, regnNo, TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD, tmgr);
            }

            if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_HOME_AUTH_RENEW_APPROVAL)) && statecoveredBean != null && statecoveredBean.isRender_payment_table_aitp() && pmt_type == Integer.valueOf(TableConstants.AITP) && statecoveredBean.getPaymentList() != null && statecoveredBean.getPaymentList().size() > 0) {
                new PassengerPermitDetailImpl().moveVaInstrumentAitpToVtInstrumentAitp(statecoveredBean.getPaymentList(), appl_no, regnNo, tmgr);
            }
            if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_HOME_AUTH_RENEW_VERIFICATION))) {
                ServerUtil.updateVaNPAuthFlag(appl_no, tmgr, "V");
            }

            if (("DL,UP".contains(Util.getUserStateCode()) && pmt_type == Integer.parseInt(TableConstants.NATIONAL_PERMIT)) && action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_HOME_AUTH_RENEW_APPROVAL))) {
                CommonPermitPrintImpl.removePrintFlow(tmgr, appl_no);
            }

            if (update_home_auth_verify && action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_HOME_AUTH_RENEW_VERIFICATION)) && p_pmt_type == Integer.valueOf(TableConstants.AITP)) {
                moveDataIntoVaToVha(tmgr, appl_no);
                updateVaPermitHomeAuth(tmgr, appl_no, p_auth_fr, p_auth_to);
            }

            tmgr.commit();
            String smsMessage = getMessageDetails(status_dobj, action_cd, appl_no, regnNo, status_dobj.getPur_cd());
            sendSMS(mobile_no.toString(), smsMessage);

        } catch (VahanException ex) {
            throw ex;
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

    public String getMessageDetails(Status_dobj status_dobj, String action_cd, String appl_no, String regn_no, int pur_cd) throws VahanException {
        String message = null;
        String reason = "";
        DisAppNoticeDobj dobj = new DisAppNoticeDobj();
        dobj = PrintDocImpl.getDisAppNoticeData(appl_no, regn_no, pur_cd);
        if (dobj != null && dobj.getReasonList().size() > 0) {
            for (int i = 0; i < dobj.getReasonList().size(); i++) {
                reason = reason + dobj.getReasonList().get(i) + ",";
            }
        }
        if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_HOME_AUTH_RENEW_APPROVAL)) && status_dobj.getStatus().equals(TableConstants.STATUS_COMPLETE)) {
            message = "Your application is approved successfully";
        } else if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_HOME_AUTH_RENEW_VERIFICATION)) && status_dobj.getStatus().equals(TableConstants.STATUS_COMPLETE)) {
            message = "Your application is verified successfully";
        } else if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_HOME_AUTH_RENEW_VERIFICATION)) && status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
            message = "Your application is Hold at verify due to " + reason;
        } else if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_HOME_AUTH_RENEW_APPROVAL)) && status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
            message = "Your application is Hold at approval due to " + reason;
        }

        return message;

    }

    public void vt_permit_home_To_VH_permit_home(TransactionManager tmgr, String regn_no) throws SQLException {
        PreparedStatement ps = null;
        Query = "INSERT INTO " + TableList.VH_PERMIT_HOME_AUTH + "(regn_no, pmt_no, auth_no, auth_fr, auth_to, op_dt, moved_on,moved_by,pur_cd)\n"
                + "SELECT regn_no, pmt_no, auth_no, auth_fr, auth_to, op_dt,CURRENT_TIMESTAMP,?,pur_cd FROM " + TableList.VT_PERMIT_HOME_AUTH + " WHERE REGN_NO=?";
        ps = tmgr.prepareStatement(Query);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, regn_no);
        ps.executeUpdate();
        deleteIntoHomeAuth(tmgr, null, TableList.VT_PERMIT_HOME_AUTH, regn_no);
    }

    public void updateVaPermitHomeAuth(TransactionManager tmgr, String appl_no, Date auth_fr, Date auth_to) throws SQLException {
        PreparedStatement ps = null;
        Query = " Update " + TableList.VA_PERMIT_HOME_AUTH + " set auth_fr=?,auth_to=?,op_dt=CURRENT_TIMESTAMP where appl_no=? ";
        ps = tmgr.prepareStatement(Query);
        ps.setDate(1, new java.sql.Date(auth_fr.getTime()));
        ps.setDate(2, new java.sql.Date(auth_to.getTime()));
        ps.setString(3, appl_no);
        ps.executeUpdate();

    }

    public void va_permit_home_period_VHA_permit_homePeriod(TransactionManager tmgr, String appl_no, String regn_no) throws SQLException {
        PreparedStatement ps = null;
        Query = "SELECT * FROM " + TableList.VA_PERMIT_HOME_AUTH_PERIOD + " WHERE appl_no=? and REGN_NO = ?";
        ps = tmgr.prepareStatement(Query);
        ps.setString(1, appl_no);
        ps.setString(2, regn_no);
        rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            Query = "INSERT INTO " + TableList.VHA_PERMIT_HOME_AUTH_PERIOD + " (state_cd,off_cd,appl_no,regn_no,pur_cd,period_mode,period,op_dt, moved_on,moved_by)\n"
                    + "SELECT state_cd,off_cd,appl_no,regn_no,pur_cd,period_mode,period,op_dt,CURRENT_TIMESTAMP,? FROM " + TableList.VA_PERMIT_HOME_AUTH_PERIOD + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, appl_no);
            ps.executeUpdate();
        }
    }

    public String[] getAuthPeriod(String applNo) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        String[] strArr = new String[2];
        try {
            sql = " select period_mode,period  FROM " + TableList.VA_PERMIT_HOME_AUTH_PERIOD
                    + " WHERE appl_no = ? and state_cd=? ";
            tmgr = new TransactionManager("getAuthPeriod");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                strArr[0] = rs.getString("period_mode");
                strArr[1] = rs.getInt("period") + "";
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
        return strArr;
    }

    public void InsertVaPermitHomeAuthDeleteVTPermitHomeAuth(TransactionManager tmgr, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        moveDataIntoVaToVha(tmgr, appl_no);
        Query = "INSERT INTO " + TableList.VT_PERMIT_HOME_AUTH + "(regn_no, pmt_no, auth_no, auth_fr, auth_to, op_dt,pur_cd)\n"
                + "SELECT regn_no, pmt_no, auth_no, auth_fr, auth_to, CURRENT_TIMESTAMP,pur_cd\n"
                + "FROM " + TableList.VA_PERMIT_HOME_AUTH + " WHERE appl_no = ?";
        ps = tmgr.prepareStatement(Query);
        ps.setString(1, appl_no);
        ps.executeUpdate();
        deleteIntoHomeAuth(tmgr, appl_no, TableList.VA_PERMIT_HOME_AUTH, null);
    }

    public void insert_va_permit_home_auth(TransactionManager tmgr, String appl_no, PermitHomeAuthDobj dobj) throws SQLException {
        PreparedStatement ps = null;
        int i = 1;
        Query = "INSERT INTO " + TableList.VA_PERMIT_HOME_AUTH + "(\n"
                + "            appl_no, regn_no, pmt_no, auth_no, auth_fr, auth_to, op_dt,pur_cd)\n"
                + "    VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP,?)";
        ps = tmgr.prepareStatement(Query);
        ps.setString(i++, appl_no);
        ps.setString(i++, dobj.getRegnNo());
        ps.setString(i++, dobj.getPmtNo());
        ps.setString(i++, dobj.getAuthNo());
        ps.setDate(i++, new java.sql.Date(dobj.getAuthFrom().getTime()));
        ps.setDate(i++, new java.sql.Date(dobj.getAuthUpto().getTime()));
        ps.setInt(i++, dobj.getPurCd());
        ps.executeUpdate();
    }

    public void insert_va_permit_home_authPeriod(TransactionManager tmgr, String appl_no, PermitHomeAuthDobj dobj, String period_mode, String Period) throws SQLException {
        PreparedStatement ps = null;
        int i = 1;
        Query = "INSERT INTO permit.va_permit_home_auth_period (\n"
                + " state_cd,off_cd, appl_no, regn_no,pur_cd,period_mode,period, op_dt)\n"
                + "    VALUES (?, ?, ?, ?, ?, ?,?, CURRENT_TIMESTAMP)";
        ps = tmgr.prepareStatement(Query);
        ps.setString(i++, Util.getUserStateCode());
        ps.setInt(i++, Util.getUserSeatOffCode());
        ps.setString(i++, appl_no);
        ps.setString(i++, dobj.getRegnNo());
        ps.setInt(i++, dobj.getPurCd());
        ps.setString(i++, period_mode);
        ps.setInt(i++, Integer.parseInt(Period));
        ps.executeUpdate();
    }

    public PermitHomeAuthDobj getVT_TAXdetails(String regn_no) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        PermitHomeAuthDobj dobj = null;
        try {
            if (regn_no.equalsIgnoreCase("NEW")) {
                return new PermitHomeAuthDobj();
            }
            tmgr = new TransactionManager("Get VT_TAX Details");
            Query = "SELECT TAX_FROM,TAX_UPTO FROM " + TableList.VT_TAX + " WHERE REGN_NO=? and pur_cd=58 and state_cd=? ORDER BY TAX_UPTO DESC LIMIT 1";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new PermitHomeAuthDobj();
                dobj.setTaxFrom(rs.getDate("TAX_FROM"));
                dobj.setTaxUpto(rs.getDate("TAX_UPTO"));
            }
            if (dobj == null || (dobj.getTaxUpto() == null || dobj.getTaxUpto().getTime() < new Date().getTime())) {
                Query = "SELECT clear_fr,clear_to FROM " + TableList.VT_TAX_CLEAR + " WHERE REGN_NO=? and pur_cd=58 and state_cd=? ORDER BY clear_to DESC LIMIT 1";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, regn_no);
                ps.setString(2, Util.getUserStateCode());
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    dobj = new PermitHomeAuthDobj();
                    dobj.setTaxFrom(rs.getDate("clear_fr"));
                    dobj.setTaxUpto(rs.getDate("clear_to"));
                }
            }
            if (dobj == null || (dobj.getTaxUpto() == null || dobj.getTaxUpto().getTime() < new Date().getTime())) {
                Query = "SELECT exem_fr,exem_to FROM " + TableList.VT_TAX_EXEM + " WHERE REGN_NO=? and state_cd=? ORDER BY exem_to DESC LIMIT 1";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, regn_no);
                ps.setString(2, Util.getUserStateCode());
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    dobj = new PermitHomeAuthDobj();
                    dobj.setTaxFrom(rs.getDate("exem_fr"));
                    dobj.setTaxUpto(rs.getDate("exem_to"));
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

    public void deleteIntoHomeAuth(TransactionManager tmgr, String appl_no, String table, String regn_no) throws SQLException {
        PreparedStatement ps = null;
        if (appl_no != null) {
            Query = "DELETE FROM " + table + " WHERE appl_no = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
        } else {
            Query = "DELETE FROM " + table + " WHERE regn_no = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regn_no);
        }
        ps.executeUpdate();
    }

    public void moveDataIntoVaToVha(TransactionManager tmgr, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        Query = "INSERT INTO " + TableList.VHA_PERMIT_HOME_AUTH + "(\n"
                + "            appl_no, regn_no, pmt_no, auth_no, auth_fr, auth_to, op_dt, moved_on, \n"
                + "            moved_by,pur_cd)\n"
                + "SELECT appl_no, regn_no, pmt_no, auth_no, auth_fr, auth_to, op_dt,current_timestamp + interval '1 second' as moved_on,?,pur_cd\n"
                + " FROM " + TableList.VA_PERMIT_HOME_AUTH + " WHERE appl_no = ?";
        ps = tmgr.prepareStatement(Query);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    public List<PermitHomeAuthDobj> getOldPermitAuthDetails(List<PermitHomeAuthDobj> oldAuthDetails, String pmtNo) {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("Old Permit Auth Details");
            oldAuthDetails = new ArrayList<>();
            Query = "(SELECT regn_no, pmt_no, auth_no, to_char(auth_fr,'DD-MON-YYYY') as auth_fr, to_char(auth_to,'DD-MON-YYYY') as auth_to,\n"
                    + "to_char(op_dt,'DD-MON-YYYY') as op_dt ,auth_to as auth_to_date FROM " + TableList.VT_PERMIT_HOME_AUTH + " where regn_no =?  \n"
                    + "union all\n"
                    + "SELECT regn_no, pmt_no, auth_no, to_char(auth_fr,'DD-MON-YYYY') as auth_fr, to_char(auth_to,'DD-MON-YYYY') as auth_to,\n"
                    + "to_char(op_dt,'DD-MON-YYYY') as op_dt ,auth_to as auth_to_date FROM " + TableList.VH_PERMIT_HOME_AUTH + " where regn_no =?) order by auth_to_date  DESC ";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, pmtNo);
            ps.setString(2, pmtNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                PermitHomeAuthDobj dobj = new PermitHomeAuthDobj();
                dobj.setRegnNo(rs.getString("regn_no"));
                dobj.setPmtNo(rs.getString("pmt_no"));
                dobj.setAuthNo(rs.getString("auth_no"));
                dobj.setAuthFromInString(rs.getString("auth_fr"));
                dobj.setAuthUptoInString(rs.getString("auth_to"));
                dobj.setOpDateInString(rs.getString("op_dt"));
                oldAuthDetails.add(dobj);
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
        return oldAuthDetails;
    }

    public boolean takeRenewalOfHomeAuthAfter(String state_cd, String regn_no, int pmtType) {
        boolean flag = false;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        String Query;
        RowSet rs, rs1;
        try {
            tmgr = new TransactionManager("Take Renewal Of Permit After");
            Query = "select (auth_to-current_date) :: int as dateDiffrence,\n"
                    + "case\n"
                    + "when ? = 103 then 'ai_renew_after_days'\n"
                    + "when ? = 106 then 'np_renew_after_days'\n"
                    + "when ? = 105 then 'np_renew_after_days'\n"
                    + "end as cofigValue\n"
                    + "from " + TableList.VT_PERMIT_HOME_AUTH + " where regn_no = ?";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setInt(i++, pmtType);
            ps.setInt(i++, pmtType);
            ps.setInt(i++, pmtType);
            ps.setString(i++, regn_no);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (!CommonUtils.isNullOrBlank(rs.getString("cofigValue"))) {
                    Query = "select " + rs.getString("cofigValue") + " from " + TableList.VM_PERMIT_STATE_CONFIGURATION + " where state_cd = ?";
                    ps = tmgr.prepareStatement(Query);
                    ps.setString(1, state_cd);
                    rs1 = tmgr.fetchDetachedRowSet_No_release();
                    if (rs1.next()) {
                        if ((rs.getInt("dateDiffrence") <= rs1.getInt(rs.getString("cofigValue")))) {
                            flag = true;
                        }
                    }
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
        return flag;
    }

    public String getLastSubmittedFee(String state_cd, String regn_no) {
        String msg = null;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManagerReadOnly("getLastSubmittedFee");
            String Query = "SELECT 'Vehicle No: '|| regn_no || ' has been submitted fee ' || fees || '"
                    + " and fine '|| fine || ' against recipt no: '|| rcpt_no||' on ' || to_char(rcpt_dt,'dd-MON-yyyy hh:mm AM') ||' .'"
                    + " as msg from " + TableList.VT_FEE + " where regn_no = ? AND pur_cd in (?,?) AND state_cd = ? order by RCPT_DT DESC limit 1";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, regn_no);
            ps.setInt(i++, TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD);
            ps.setInt(i++, TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD);
            ps.setString(i++, state_cd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                msg = rs.getString("msg");
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
        return msg;
    }

    public PermitHomeAuthDobj getSelectInVtPermit(String regn_no) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        PermitHomeAuthDobj dobj = null;
        try {
            tmgr = new TransactionManager("getSelectInVtPermit");
            Query = "SELECT *,current_date as date FROM " + TableList.VT_PERMIT_HOME_AUTH + " where regn_no = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regn_no);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new PermitHomeAuthDobj();
                dobj.setAuthNo(rs.getString("auth_no"));
                dobj.setAuthFrom(rs.getDate("auth_fr"));
                dobj.setAuthUpto(rs.getDate("auth_to"));
                dobj.setPrvAuthUpto(rs.getDate("date")); // Use as Current Date.
                dobj.setPmtNo(rs.getString("pmt_no"));
            }
        } catch (SQLException e) {
            dobj = null;
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

    public PermitHomeAuthDobj getSelectInVhPermit(String regnNo, String pmtNo) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        PermitHomeAuthDobj dobj = null;
        try {
            tmgr = new TransactionManager("getSelectInVtPermit");
            Query = "SELECT auth_no,auth_fr,auth_to FROM " + TableList.VH_PERMIT_HOME_AUTH + ""
                    + " where regn_no = ? and pmt_no = ? order by moved_on DESC limit 1";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regnNo);
            ps.setString(2, pmtNo);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new PermitHomeAuthDobj();
                dobj.setAuthNo(rs.getString("auth_no"));
                dobj.setAuthFrom(rs.getDate("auth_fr"));
                dobj.setAuthUpto(rs.getDate("auth_to"));
            }
        } catch (SQLException e) {
            dobj = null;
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
}
