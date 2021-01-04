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
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.PermitLOIDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author hcl
 */
public class PermitLOIImpl {

    private static final Logger LOGGER = Logger.getLogger(PermitLOIImpl.class);

    public List<PermitLOIDobj> getPendingWork(String applNo, String ownName, String stateCd) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        List<PermitLOIDobj> list = new ArrayList<>();
        try {
            tmgr = new TransactionManagerReadOnly("getPendingWork");
//            String QueryWhenRegnNew = " (SELECT b.regn_no,a.appl_no,c.owner_name,c.f_name,pmt_type,d.descr as pmt_type_descr,pmt_catg,"
//                    + " COALESCE(e.descr,'NA') as pmt_catg_descr,period_mode, f.descr as period_descr,period,to_char(a.op_dt,'DD-MON-YYYY') as op_dt,"
//                    + " g.dl_no,case when g.flag = 't' THEN 'Verify' ELSE 'Not Verify' END as onlinestatus \n"
//                    + " from permit.va_permit_offer_approval a\n"
//                    + " left outer join permit.va_permit b on a.appl_no = b.appl_no \n"
//                    + " left outer join permit.va_permit_owner c on a.appl_no = c.appl_no \n"
//                    + " left outer join PERMIT.VM_PERMIT_TYPE d on b.pmt_type = d.code\n"
//                    + " left outer join PERMIT.VM_PERMIT_CATG e on b.state_cd = e.state_cd and pmt_type = e.permit_type and pmt_catg = e.code\n"
//                    + " left outer join vm_tax_mode f on f.tax_mode = period_mode\n"
//                    + "left outer join onlineschema.vp_loi_dtls g on g.appl_no =  a.appl_no and  b.state_cd = g.state_cd\n"
//                    + " where b.regn_no IN ('NEW') and b.state_cd = ? ";
//            if (CommonUtils.isNullOrBlank(applNo) && CommonUtils.isNullOrBlank(ownName)) {
//                QueryWhenRegnNew += " )";
//            } else if (!CommonUtils.isNullOrBlank(applNo) && CommonUtils.isNullOrBlank(ownName)) {
//                QueryWhenRegnNew += " AND a.appl_no = ? )";
//            } else if (CommonUtils.isNullOrBlank(applNo) && !CommonUtils.isNullOrBlank(ownName)) {
//                QueryWhenRegnNew += " AND c.owner_name like ? )";
//            }
//
//            String QueryWhenRegnNotNew = " (SELECT b.regn_no,a.appl_no,c.owner_name,c.f_name,pmt_type,d.descr as pmt_type_descr,pmt_catg,"
//                    + " COALESCE(e.descr,'NA') as pmt_catg_descr,period_mode, f.descr as period_descr,period,to_char(a.op_dt,'DD-MON-YYYY') as op_dt,"
//                    + " g.dl_no,case when g.flag = 't' THEN 'Verify' ELSE 'Not Verify' END as onlinestatus \n"
//                    + " from permit.va_permit_offer_approval a\n"
//                    + " left outer join permit.va_permit b on a.appl_no = b.appl_no \n"
//                    + " left outer join vt_owner c on b.state_cd = c.state_cd and b.regn_no = c.regn_no \n"
//                    + " left outer join PERMIT.VM_PERMIT_TYPE d on b.pmt_type = d.code\n"
//                    + " left outer join PERMIT.VM_PERMIT_CATG e on b.state_cd = e.state_cd and pmt_type = e.permit_type and pmt_catg = e.code\n"
//                    + " left outer join vm_tax_mode f on f.tax_mode = period_mode\n"
//                    + " left outer join onlineschema.vp_loi_dtls g on g.appl_no =  a.appl_no and  b.state_cd = g.state_cd\n"
//                    + " where b.regn_no NOT IN ('NEW') and b.state_cd = ? ";
//            if (CommonUtils.isNullOrBlank(applNo) && CommonUtils.isNullOrBlank(ownName)) {
//                QueryWhenRegnNotNew += " )";
//            } else if (!CommonUtils.isNullOrBlank(applNo) && CommonUtils.isNullOrBlank(ownName)) {
//                QueryWhenRegnNotNew += " AND a.appl_no = ? )";
//            } else if (CommonUtils.isNullOrBlank(applNo) && !CommonUtils.isNullOrBlank(ownName)) {
//                QueryWhenRegnNotNew += " AND c.owner_name like ? )";
//            }

            //String Query = (QueryWhenRegnNew + " UNION ALL" + QueryWhenRegnNotNew) + " order by appl_no";
            String Query = "SELECT b.regn_no,a.appl_no,COALESCE(c.owner_name,cc.owner_name) as owner_name ,COALESCE(c.f_name,cc.f_name) as f_name,pmt_type,d.descr as pmt_type_descr,pmt_catg,"
                    + " COALESCE(e.descr,'NA') as pmt_catg_descr,period_mode, f.descr as period_descr,period,to_char(a.op_dt,'DD-MON-YYYY') as op_dt,"
                    + " h.dl_no,to_char(h.dob,'DD-MON-YYYY') as dob,h.psv_no,j.descr as maker_descr,k.model_name,case when g.flag = 't' THEN 'Verify' ELSE 'Not Verify' END as onlinestatus \n"
                    + " from permit.va_permit_offer_approval a \n"
                    + " left outer join permit.va_permit b on a.appl_no = b.appl_no \n"
                    + " left outer join permit.va_permit_owner c on a.appl_no = c.appl_no \n"
                    + " left outer join vt_owner cc on cc.state_cd = b.state_cd and cc.regn_no = b.regn_no \n"
                    + " left outer join PERMIT.VM_PERMIT_TYPE d on b.pmt_type = d.code \n"
                    + " left outer join PERMIT.VM_PERMIT_CATG e on b.state_cd = e.state_cd and pmt_type = e.permit_type and pmt_catg = e.code \n"
                    + " left outer join vm_tax_mode f on f.tax_mode = period_mode \n"
                    + " left outer join onlineschema.vp_loi_dtls g on g.appl_no =  a.appl_no and  b.state_cd = g.state_cd \n"
                    + " left outer join onlinepermit.va_permit_owner h on b.appl_no =  h.appl_no and  b.state_cd = h.state_cd \n"
                    + " left outer join vm_maker_homologation j on h.maker_cd = j.code \n"
                    + " left outer join vm_model_homologation k on  h.maker_cd = k.maker_code and h.model_unique_ref = k.unique_model_ref_no \n"
                    + " where b.state_cd = ? AND a.appl_no = ? and b.off_cd=? ";
            PreparedStatement ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, stateCd);
            ps.setString(i++, applNo);
            ps.setInt(i++, Util.getUserOffCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                PermitLOIDobj dobj = new PermitLOIDobj();
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setF_name(rs.getString("f_name"));
                dobj.setPmt_type_descr(rs.getString("pmt_type_descr"));
                dobj.setPmt_type(rs.getInt("pmt_type"));
                dobj.setPmt_catg_descr(rs.getString("pmt_catg_descr"));
                dobj.setPmt_catg(rs.getInt("pmt_catg"));
                dobj.setPeriod_descr(rs.getString("period_descr"));
                dobj.setPeriod(rs.getInt("period"));
                dobj.setOp_dt(rs.getString("op_dt"));
                dobj.setDlNo(rs.getString("dl_no"));
                dobj.setDob(rs.getString("dob"));
                dobj.setPsvNo(rs.getString("psv_no"));
                dobj.setMaker_descr(rs.getString("maker_descr"));
                dobj.setModel_descr(rs.getString("model_name"));
                if (rs.getString("onlinestatus").equalsIgnoreCase("Verify")) {
                    dobj.setDlStatus("<font color=\"green\">" + rs.getString("onlinestatus") + "</font>");
                } else if (rs.getString("onlinestatus").equalsIgnoreCase("Not Verify")) {
                    dobj.setDlStatus("<font color=\"red\">" + rs.getString("onlinestatus") + "</font>");
                }
                list.add(dobj);
            }
        } catch (SQLException e) {
            list = null;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                list = null;
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return list;
    }

    public List<PermitLOIDobj> getAllLoiPendingAppl(int off_cd, String stateCd, String selectValue, String appl_no) {
        TransactionManagerReadOnly tmgr = null;
        List<PermitLOIDobj> list = new ArrayList<>();
        String Query = "";
        try {
            tmgr = new TransactionManagerReadOnly("getAllLoiPendingAppl");
            if (selectValue.equalsIgnoreCase("allApplNo")) {
                Query = "SELECT b.regn_no,a.appl_no,COALESCE(c.owner_name,cc.owner_name) as owner_name ,COALESCE(c.f_name,cc.f_name) as f_name,pmt_type,d.descr as pmt_type_descr,pmt_catg,"
                        + " COALESCE(e.descr,'NA') as pmt_catg_descr,period_mode, f.descr as period_descr,period,to_char(a.op_dt,'DD-MON-YYYY') as op_dt,"
                        + " g.dl_no,case when g.flag = 't' THEN 'Verify' ELSE 'Not Verify' END as onlinestatus \n"
                        + " from permit.va_permit_offer_approval a \n"
                        + " left outer join permit.va_permit b on a.appl_no = b.appl_no \n"
                        + " left outer join permit.va_permit_owner c on a.appl_no = c.appl_no \n"
                        + " left outer join vt_owner cc on cc.state_cd = b.state_cd and cc.regn_no = b.regn_no \n"
                        + " left outer join PERMIT.VM_PERMIT_TYPE d on b.pmt_type = d.code \n"
                        + " left outer join PERMIT.VM_PERMIT_CATG e on b.state_cd = e.state_cd and pmt_type = e.permit_type and pmt_catg = e.code \n"
                        + " left outer join vm_tax_mode f on f.tax_mode = period_mode \n"
                        + " left outer join onlineschema.vp_loi_dtls g on g.appl_no =  a.appl_no and  b.state_cd = g.state_cd \n"
                        + " where b.state_cd = ? AND b.off_cd=? order by a.op_dt desc";
            } else {
                Query = "SELECT b.regn_no,a.appl_no,COALESCE(c.owner_name,cc.owner_name) as owner_name ,COALESCE(c.f_name,cc.f_name) as f_name,pmt_type,d.descr as pmt_type_descr,pmt_catg,"
                        + " COALESCE(e.descr,'NA') as pmt_catg_descr,period_mode, f.descr as period_descr,period,to_char(a.op_dt,'DD-MON-YYYY') as op_dt,"
                        + " g.dl_no,case when g.flag = 't' THEN 'Verify' ELSE 'Not Verify' END as onlinestatus \n"
                        + " from permit.va_permit_offer_approval a \n"
                        + " left outer join permit.va_permit b on a.appl_no = b.appl_no \n"
                        + " left outer join permit.va_permit_owner c on a.appl_no = c.appl_no \n"
                        + " left outer join vt_owner cc on cc.state_cd = b.state_cd and cc.regn_no = b.regn_no \n"
                        + " left outer join PERMIT.VM_PERMIT_TYPE d on b.pmt_type = d.code \n"
                        + " left outer join PERMIT.VM_PERMIT_CATG e on b.state_cd = e.state_cd and pmt_type = e.permit_type and pmt_catg = e.code \n"
                        + " left outer join vm_tax_mode f on f.tax_mode = period_mode \n"
                        + " left outer join onlineschema.vp_loi_dtls g on g.appl_no =  a.appl_no and  b.state_cd = g.state_cd \n"
                        + " where b.state_cd = ? AND b.off_cd=? and a.appl_no=? order by a.op_dt desc";
            }
            PreparedStatement ps = tmgr.prepareStatement(Query);
            int i = 1;
            if (selectValue.equalsIgnoreCase("allApplNo")) {
                ps.setString(i++, stateCd);
                ps.setInt(i++, off_cd);
            } else {
                ps.setString(i++, stateCd);
                ps.setInt(i++, off_cd);
                ps.setString(i++, appl_no);
            }
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                PermitLOIDobj dobj = new PermitLOIDobj();
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setF_name(rs.getString("f_name"));
                dobj.setOp_dt(rs.getString("op_dt"));
                dobj.setDlNo(rs.getString("dl_no"));
                if (rs.getString("onlinestatus").equalsIgnoreCase("Verify")) {
                    dobj.setDlStatus("<font color=\"green\">" + rs.getString("onlinestatus") + "</font>");
                } else if (rs.getString("onlinestatus").equalsIgnoreCase("Not Verify")) {
                    dobj.setDlStatus("<font color=\"red\">" + rs.getString("onlinestatus") + "</font>");
                }
                list.add(dobj);
            }
        } catch (SQLException e) {
            list = null;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                list = null;
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return list;
    }

    public void insertVaOfferApproval(TransactionManager tmgr, String applNo) {
        String Query = "";
        PreparedStatement ps;
        try {
            Query = "SELECT * FROM " + TableList.VA_PERMIT_OFFER_APPROVAL + " WHERE APPL_NO = ? ";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (!rs.next()) {
                Query = "INSERT INTO " + TableList.VA_PERMIT_OFFER_APPROVAL + " (appl_no, op_dt) VALUES (?, Current_timestamp)";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, applNo);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void deleteVaOfferApproval(TransactionManager tmgr, String applNo) {
        try {
            String Query = "DELETE FROM " + TableList.VA_PERMIT_OFFER_APPROVAL + " WHERE appl_no = ?";
            PreparedStatement ps = tmgr.prepareStatement(Query);
            ps.setString(1, applNo);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void moveInHistTable(TransactionManager tmgr, String applNo) {
        try {
            String Query = "INSERT INTO " + TableList.VH_PERMIT_OFFER_APPROVAL + " (\n"
                    + "            appl_no, op_dt, moved_on, moved_by)\n"
                    + "     SELECT appl_no, op_dt,Current_timestamp,?\n"
                    + "     FROM " + TableList.VA_PERMIT_OFFER_APPROVAL + " where appl_no = ?";
            PreparedStatement ps = tmgr.prepareStatement(Query);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, applNo);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public String approveApplication(String applNo, String orderNo, String orderBy, Date orderDt, int pmtType, String mobileNo) throws VahanException {
        TransactionManager tmgr = null;
        String offerLetterNo = null;
        try {
            tmgr = new TransactionManager("approveApplication");
            offerLetterNo = ServerUtil.getUniquePermitNo(tmgr, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), pmtType, 0, "O");
            if (CommonUtils.isNullOrBlank(offerLetterNo)) {
                throw new VahanException("Offer Letter not genrated");
            } else {
                String Query = "UPDATE " + TableList.VA_PERMIT + " SET  alloted_flag=?, offer_no=?, order_no=?, order_by=?, order_dt=? WHERE appl_no=?";
                PreparedStatement ps = tmgr.prepareStatement(Query);
                int i = 1;
                ps.setString(i++, TableConstants.permitOfferLetterActive);
                ps.setString(i++, offerLetterNo);
                ps.setString(i++, ServerUtil.getUniquePermitNo(tmgr, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), 0, 0, "A"));
                ps.setString(i++, orderBy);
                ps.setDate(i++, new java.sql.Date(orderDt.getTime()));
                ps.setString(i++, applNo);
                ps.executeUpdate();
                moveInHistTable(tmgr, applNo);
                deleteVaOfferApproval(tmgr, applNo);
                String docId = CommonPermitPrintImpl.getPermitDocIdForQuery(CommonPermitPrintImpl.getPermitDocId(tmgr, 0, TableConstants.VM_PMT_APPLICATION_PUR_CD, Util.getUserStateCode()));
                String[] beanData = {docId, applNo, "NEW"};
                CommonPermitPrintImpl.insertVaPermitPrint(tmgr, beanData);
                String message = "Permit Order No " + offerLetterNo + " generated against Application No " + applNo;
                Query = "update va_status set status = ? where appl_no = ?";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, "N");
                ps.setString(2, applNo);
                ps.executeUpdate();
                ServerUtil.insertUsersTransactionMessage(message, 1, tmgr);
                ServerUtil.sendSMS(mobileNo, "[" + applNo + "]%0D%0A Congratulations.Your name has been considered for new Permit .%0D%0A THANKS.");
                tmgr.commit();
            }
        } catch (VahanException e) {
            offerLetterNo = "";
            throw new VahanException(e.getMessage());
        } catch (SQLException e) {
            offerLetterNo = "";
            throw new VahanException("SQLException");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                offerLetterNo = "";
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return offerLetterNo;
    }

    public void revertBackForRectification(Status_dobj status_dobj, String applNo, String reason, String appDisappFlag, String mobileNo) throws VahanException {
        TransactionManager tmgr = null;
        PassengerPermitDetailImpl pmtImpl = null;
        try {
            pmtImpl = new PassengerPermitDetailImpl();
            tmgr = new TransactionManager("reverBackForRectification");
            if ("O".equalsIgnoreCase(appDisappFlag)) {
                status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
                status_dobj.setOffice_remark("Revert Back For Rectification");
                status_dobj.setPublic_remark("");
                status_dobj.setCntr_id("");
                status_dobj.setEmp_cd(Long.parseLong(Util.getEmpCode()));
                status_dobj.setOff_cd(Util.getUserOffCode());
                ServerUtil.fileFlow(tmgr, status_dobj);
            }
            moveInHistTable(tmgr, applNo);
            deleteVaOfferApproval(tmgr, applNo);
            pmtImpl.moveInVhaPmtInCaseOfDisApproval(tmgr, applNo, reason, appDisappFlag);
            if ("D".equalsIgnoreCase(appDisappFlag)) {
                applDisapproved(tmgr, status_dobj);
            }
            String[] objectDtls = reason.split(",");
            String[][] data = MasterTableFiller.masterTables.VM_PERMIT_OBJECTION.getData();
            String objList = "";
            int j = 1;
            if (objectDtls.length > 0) {
                for (int i = 0; i < objectDtls.length; i++) {
                    for (int n = 0; n < data.length; n++) {
                        if (data[n][1].equalsIgnoreCase(objectDtls[i])) {
                            objList += (" " + (j++) + " - " + data[n][3] + ",");
                        }
                    }
                }
            }
            if (!CommonUtils.isNullOrBlank(objList)) {
                ServerUtil.sendSMS(mobileNo, "[" + applNo + "]%0D%0A" + "Objection(s) : " + objList.subSequence(0, (objList.length() - 1)) + ".%0D%0A THANKS.");
            } else if ("D".equalsIgnoreCase(appDisappFlag)) {
                ServerUtil.sendSMS(mobileNo, "Your request " + applNo + " has been disapproved by authorised person.%0D%0A THANKS.");
            }
            tmgr.commit();
        } catch (VahanException e) {
            throw new VahanException(e.getMessage());
        } catch (Exception e) {
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
    }

    public boolean recodeExistInVaPmtOfferApp(String applNo) {
        TransactionManagerReadOnly tmgr = null;
        boolean flag = false;
        try {
            tmgr = new TransactionManagerReadOnly("");
            String Query = "SELECT * FROM " + TableList.VA_PERMIT_OFFER_APPROVAL + " where appl_no = ?";
            PreparedStatement ps = tmgr.prepareStatement(Query);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                flag = true;
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

    public boolean objectionAssign(String applNo) {
        TransactionManagerReadOnly tmgr = null;
        boolean flag = false;
        try {
            tmgr = new TransactionManagerReadOnly("");
            String Query = "SELECT remarks from " + TableList.VA_PERMIT + " where appl_no = ?";
            PreparedStatement ps = tmgr.prepareStatement(Query);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (CommonUtils.isNullOrBlank(rs.getString("remarks"))) {
                    flag = true;
                } else {
                    flag = false;
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

    public String readOnlyPermitUniqueNo(String stateCd, int offCd, int pmtType, int pmtCatg, String flag) throws VahanException {
        TransactionManager tmgr = null;
        String uniqueNo = "";
        try {
            tmgr = new TransactionManager("readOnlyPermitUniqueNo");
            uniqueNo = ServerUtil.getUniquePermitNo(tmgr, stateCd, offCd, pmtType, pmtCatg, flag);
            if (CommonUtils.isNullOrBlank(uniqueNo)) {
                throw new VahanException("Entry missing in number generating table. Please contact to system administrator.");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
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
        return uniqueNo;
    }

    public void applDisapproved(TransactionManager tmgr, Status_dobj status) throws VahanException {
        String sql;
        PreparedStatement ps;
        try {
            sql = " INSERT INTO " + TableList.VHA_STATUS
                    + "       SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno,"
                    + "       action_cd, seat_cd, cntr_id, ? as status, ? as office_remark, public_remark,"
                    + "       file_movement_type, ? as emp_cd, op_dt, current_timestamp as moved_on,? "
                    + "       FROM " + TableList.VA_STATUS
                    + " WHERE appl_no=? and pur_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, TableConstants.DISPOSE);
            ps.setString(2, status.getOffice_remark());
            ps.setLong(3, status.getEmp_cd());
            ps.setString(4, Util.getClientIpAdress());
            ps.setString(5, status.getAppl_no());
            ps.setInt(6, status.getPur_cd());
            ps.executeUpdate();

            sql = "DELETE FROM " + TableList.VA_STATUS + " WHERE appl_no=? and pur_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, status.getAppl_no());
            ps.setInt(2, status.getPur_cd());
            ps.executeUpdate();


            sql = "INSERT INTO " + TableList.VHA_DETAILS
                    + "     SELECT a.*,current_timestamp as moved_on,? as moved_by FROM " + TableList.VA_DETAILS
                    + "     a WHERE a.appl_no=? and pur_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, status.getEmp_cd());
            ps.setString(2, status.getAppl_no());
            ps.setInt(3, status.getPur_cd());
            ps.executeUpdate();

            sql = "DELETE FROM " + TableList.VA_DETAILS + " WHERE appl_no=? and pur_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, status.getAppl_no());
            ps.setInt(2, status.getPur_cd());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("There are some problem to disapproved. Please contact to system administratior");
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("There are some problem to disapproved. Please contact to system administratior");
        }
    }
}
