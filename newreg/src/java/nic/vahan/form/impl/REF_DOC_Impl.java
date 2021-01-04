/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import javax.sql.RowSet;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.REF_DOC_PURPOSE_dobj;
import nic.vahan.form.dobj.REF_DOC_dobj;
import nic.vahan.server.ServerUtil;

/**
 *
 * @author AMBRISH
 */
public class REF_DOC_Impl {

    private static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(REF_DOC_Impl.class);

    public ArrayList<REF_DOC_dobj> set_ref_doc_db_to_dobj(String appl_no) {

        ArrayList<REF_DOC_dobj> list = new ArrayList<REF_DOC_dobj>();

        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("set_ref_doc_db_to_dobj");
            PreparedStatement prstmt = tmgr.prepareStatement("select a.refdoc_no, a.ref_doc_source_cd, a.scan_doc, a.dig_signature , b.refdoc_source_descr from va_refdoc a, tm_refdoc_source b where a.ref_doc_source_cd = b.refdoc_source_cd and  appl_no= ? ");
            prstmt.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                REF_DOC_dobj ref_doc_dobj = new REF_DOC_dobj();
                ref_doc_dobj.setAppl_no(appl_no);
                ref_doc_dobj.setDig_signature(rs.getBytes("dig_signature"));
                ref_doc_dobj.setRef_doc_source_cd(rs.getInt("ref_doc_source_cd"));
                ref_doc_dobj.setScan_doc(rs.getBytes("scan_doc"));
                ref_doc_dobj.setScan_doc_no(rs.getString("refdoc_no"));
                ref_doc_dobj.setRef_doc_source_descr(rs.getString("refdoc_source_descr"));

                list.add(ref_doc_dobj);
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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

    public REF_DOC_dobj fetchImageFromDB(String appl_no, int refdoc_purpose_cd) {
        String query = " select a.appl_no,a.refdoc_no,c.refdoc_source_cd,c.refdoc_source_descr, d.refdoc_purpose_descr,d.refdoc_purpose_cd "
                + " from va_refdoc a, va_refpurpose b, tm_refdoc_source c, tm_refdocpurpose d "
                + " where a.ref_doc_source_cd = c.refdoc_source_cd and b.ref_doc_purpose_cd = d.refdoc_purpose_cd and "
                + " a.appl_no = b.appl_no and a.refdoc_no = b.refdoc_no and a.appl_no = ? and d.refdoc_purpose_cd =?";
        REF_DOC_dobj ref_doc_dobj = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("fetchImageFromDB");
            PreparedStatement prstmt = tmgr.prepareStatement(query);
            prstmt.setString(1, appl_no);
            prstmt.setInt(2, refdoc_purpose_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                ref_doc_dobj = new REF_DOC_dobj();
                ref_doc_dobj.setAppl_no(appl_no);
                ref_doc_dobj.setRef_doc_source_cd(rs.getInt("refdoc_source_cd"));
                ref_doc_dobj.setScan_doc_no(rs.getString("refdoc_no"));
                ref_doc_dobj.setRef_doc_source_descr(rs.getString("refdoc_source_descr"));
                ref_doc_dobj.setScan_doc_purpose_cd(refdoc_purpose_cd);


            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return ref_doc_dobj;
    }

    public ArrayList<REF_DOC_PURPOSE_dobj> transactionAllRequiredDocList(int pur_cd, String appl_no) {


        ArrayList<REF_DOC_PURPOSE_dobj> list = new ArrayList<REF_DOC_PURPOSE_dobj>();
        TransactionManager tmgr = null;
        try {

            tmgr = new TransactionManager("transactionAllRequiredDocList");
            PreparedStatement prstmt = tmgr.prepareStatement("select  master.refdoc_purpose_cd,master.refdoc_purpose_descr,trans.refdoc_no,trans.verified,trans.verified_on,trans.remarks from "
                    + " (select b.refdoc_purpose_cd ,b.refdoc_purpose_descr "
                    + " from tm_purpose_refdocpurpose_map a, tm_refdocpurpose b "
                    + " where pur_cd = ? and  a.refdoc_purpose_cd = b.refdoc_purpose_cd order by 1) as master "
                    + " left join \n"
                    + " ( select a.ref_doc_purpose_cd,refdoc_purpose_descr,refdoc_no,a.verified, to_char(a.verified_on,'DD-MON-YYYY hh:mi AM') as verified_on,a.remarks "
                    + " from va_refpurpose a, tm_refdocpurpose  b "
                    + " where a.ref_doc_purpose_cd = b.refdoc_purpose_cd and  a.appl_no= ? order by a.ref_doc_purpose_cd)  as trans  "
                    + " on master.refdoc_purpose_cd  = trans.ref_doc_purpose_cd");
            prstmt.setInt(1, pur_cd);
            prstmt.setString(2, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {

                REF_DOC_PURPOSE_dobj refDocPurpose = new REF_DOC_PURPOSE_dobj();
                refDocPurpose.setAppl_no(appl_no);
                refDocPurpose.setRef_doc_purpose_cd(rs.getInt("refdoc_purpose_cd"));
                refDocPurpose.setRef_doc_purpose_descr(rs.getString("refdoc_purpose_descr"));
                refDocPurpose.setScan_doc_no(rs.getString("refdoc_no"));
                refDocPurpose.setVerified(rs.getBoolean("verified"));
                refDocPurpose.setVerified_on(rs.getString("verified_on"));
                refDocPurpose.setRemarks(rs.getString("remarks"));


                list.add(refDocPurpose);
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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

    public byte[] fetchImageByteArray(String appl_no, String scan_doc_no) {
        String query = "select scan_doc from va_refdoc where appl_no=? and refdoc_no=?";
        byte[] imageData = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("fetchImageByteArray");
            PreparedStatement prstmt = tmgr.prepareStatement(query);
            prstmt.setString(1, appl_no);
            prstmt.setInt(2, Integer.parseInt(scan_doc_no));
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                imageData = rs.getBytes("scan_doc");
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return imageData;
    }

    public boolean verifyImage(String appl_no, String scan_doc_no, int ref_doc_purpose_cd, boolean isVerified, long verified_by, String remarks) throws Exception {
        Timestamp current_time = ServerUtil.getSystemDateInPostgres();
        String vha_ref_scan_doc_purposeQuery = "insert into vha_ref_scan_doc_purpose select *,?,? from va_refpurpose WHERE appl_no=? and refdoc_no=? and ref_doc_purpose_cd=?";
        String va_ref_scan_doc_purposeQuery = "UPDATE va_refpurpose SET verified=?, verified_on=?,verified_by=?, remarks=?  WHERE appl_no=? and refdoc_no=? and ref_doc_purpose_cd=?";
        TransactionManager tmgr = null;
        Exception ex = null;
        boolean flag = true;
        try {
            tmgr = new TransactionManager("verifyImage");

            //////////////----- Insert in History table query 
            PreparedStatement prstmt = tmgr.prepareStatement(vha_ref_scan_doc_purposeQuery);
            prstmt.setTimestamp(1, current_time);
            prstmt.setString(2, String.valueOf(verified_by));
            prstmt.setString(3, appl_no);
            prstmt.setInt(4, Integer.parseInt(scan_doc_no));
            prstmt.setInt(5, ref_doc_purpose_cd);
            prstmt.executeUpdate();


            //---------------------Update New Status of Verification for the selected image Query--------------
            prstmt = tmgr.prepareStatement(va_ref_scan_doc_purposeQuery);
            prstmt.setBoolean(1, isVerified);
            prstmt.setTimestamp(2, current_time);
            prstmt.setString(3, String.valueOf(verified_by));
            prstmt.setString(4, remarks);
            prstmt.setString(5, appl_no);
            prstmt.setInt(6, Integer.parseInt(scan_doc_no));
            prstmt.setInt(7, ref_doc_purpose_cd);
            prstmt.executeUpdate();

            tmgr.commit();
        } catch (SQLException sqlex) {
            ex = sqlex;
            flag = false;

        } finally {
            if (tmgr != null) {
                tmgr.release();
            }

        }
        if (ex != null) {
            flag = false;
            throw ex;

        }
        return flag;
    }
}
