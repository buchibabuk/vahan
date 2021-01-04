/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.bean.permit.PermitCheckDetailsBean;
import nic.vahan.form.dobj.DupDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.DupImpl;
import static nic.vahan.form.impl.InsImpl.insertIntoInsurance;
import static nic.vahan.form.impl.InsImpl.insertIntoInsuranceHistory;
import static nic.vahan.form.impl.InsImpl.updateInsurance;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class PermitDupPrintImpl {

    private static final Logger LOGGER = Logger.getLogger(PermitDupPrintImpl.class);

    public static String insertIntoVa_Dup(DupDobj dobj, InsDobj ins_dobj, PermitCheckDetailsBean pmtCheckDtsl) {
        String appl_no = "";
        TransactionManager tmgr = null;
        PermitCheckDetailsImpl pmtDtlsImpl = null;
        try {
            tmgr = new TransactionManager("insertIntoVa_Dup");
            appl_no = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
            dobj.setAppl_no(appl_no);
            dobj.setState_cd(Util.getUserStateCode());
            dobj.setOff_cd(Util.getUserOffCode());
            if (pmtCheckDtsl.getDtlsDobj().isInsSaveData()) {
                ins_dobj = new InsDobj();
                ins_dobj.setAppl_no(appl_no);
                ins_dobj.setIns_from(CommonPermitPrintImpl.getDateDD_MMM_YYYY(pmtCheckDtsl.getDtlsDobj().getInsFrom()));
                ins_dobj.setIns_upto(CommonPermitPrintImpl.getDateDD_MMM_YYYY(pmtCheckDtsl.getDtlsDobj().getInsUpto()));
                ins_dobj.setComp_cd(Integer.valueOf(CommonPermitPrintImpl.getValueForSelectedList((ArrayList) pmtCheckDtsl.getArrayInsCmpy(), pmtCheckDtsl.getDtlsDobj().getInsCmpyNo())));
                ins_dobj.setIns_type(Integer.valueOf(CommonPermitPrintImpl.getValueForSelectedList((ArrayList) pmtCheckDtsl.getArrayInsType(), pmtCheckDtsl.getDtlsDobj().getInsType())));
                ins_dobj.setPolicy_no(pmtCheckDtsl.getDtlsDobj().getInsPolicyNo());
            }
            DupImpl.insertIntoDup(tmgr, dobj);
            if (!CommonUtils.isNullOrBlank(dobj.getPmtDoc())) {
                insertIntoVaDupDocList(appl_no, dobj.getRegn_no(), dobj.getPmtDoc(), tmgr);
            }
            if (ins_dobj != null) {
                pmtDtlsImpl = new PermitCheckDetailsImpl();
                pmtDtlsImpl.insertIntoVaInsurance(appl_no, dobj.getRegn_no(), ins_dobj, tmgr);
            }
            Status_dobj status = new Status_dobj();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            String dt = sdf.format(new Date());
            status.setAppl_dt(dt);
            status.setAppl_no(appl_no);
            status.setPur_cd(dobj.getPur_cd());
            status.setFlow_slno(1);
            status.setFile_movement_slno(1);
            status.setAction_cd(TableConstants.TM_ROLE_PMT_DUPL_APPLICATION);
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

    public String getPmtDocCode(String state_cd, int pmtType) {
        String docList = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getPmtDocCode");
            String Query = "SELECT doc_id from " + TableList.VM_PERMIT_DOC_STATE_MAP + " where state_cd = ? and pur_cd = ? and pmt_type = ?";
            PreparedStatement ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, state_cd);
            ps.setInt(i++, TableConstants.VM_PMT_FRESH_PUR_CD);
            ps.setInt(i++, pmtType);
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

    public void moveVaToVhDupDocList(String applNo, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        try {

            String Query = "INSERT INTO permit.vh_dup_doclist(\n"
                    + "            appl_no, regn_no, doc_id, op_dt, moved_on, moved_by)\n"
                    + "     SELECT appl_no, regn_no, doc_id, op_dt,CURRENT_TIMESTAMP,?\n"
                    + "            FROM permit.va_dup_doclist WHERE appl_no = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, applNo);
            ps.executeUpdate();

            Query = "DELETE FROM permit.va_dup_doclist WHERE appl_no = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, applNo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new VahanException(e.getMessage());
        }
    }

    public String getMultiDocumentList(String regnNo, String applNo) {
        String multiDoc = "";
        PreparedStatement ps;
        RowSet rs;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getMultiDocumentList");
            String Query = "SELECT doc_id from " + TableList.VA_DUP_DOCLIST + " where appl_no=? and regn_no = ?";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, applNo);
            ps.setString(i++, regnNo);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                multiDoc = rs.getString("doc_id");
            }
        } catch (SQLException e) {
            multiDoc = "";
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
        return multiDoc;
    }
}
