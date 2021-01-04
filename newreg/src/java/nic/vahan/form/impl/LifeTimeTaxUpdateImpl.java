/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.LifeTimeTaxUpdateDobj;
import nic.vahan.form.impl.admin.OwnerAdminImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author nicsi
 */
public class LifeTimeTaxUpdateImpl {

    private static final Logger logger = Logger.getLogger(LifeTimeTaxUpdateImpl.class);

    public boolean saveAndUpdatLTTaxAmt(LifeTimeTaxUpdateDobj lifeTimeTaxUpdateDobj) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("saveAndUpdatLTT");
            String qry = "select * from va_ltt_amt_update where appl_no=? and regn_no=? and state_cd=?  and off_cd=?";
            PreparedStatement pms = tmgr.prepareStatement(qry);
            pms.setString(1, lifeTimeTaxUpdateDobj.getApplNo());
            pms.setString(2, lifeTimeTaxUpdateDobj.getRegnNo());
            pms.setString(3, lifeTimeTaxUpdateDobj.getStateCd());
            pms.setInt(4, lifeTimeTaxUpdateDobj.getOffcd());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                moveToHistoryVhLttUpdate(tmgr, lifeTimeTaxUpdateDobj);
                UpdateIntoVaLttAmtUpdate(tmgr, lifeTimeTaxUpdateDobj);
            } else {
                insertInotVaLttAmtUpdate(tmgr, lifeTimeTaxUpdateDobj);
            }
            //Send mail ---

            String forNewLine = "<br/>";
            String subject = "Modification LTT/OTT amount for Vehicle No " + lifeTimeTaxUpdateDobj.getRegnNo() + ".";
            OwnerAdminImpl ownerAdmin = new OwnerAdminImpl();
            String dataModification = subject + forNewLine + " LTT/OTT amount rupees" + lifeTimeTaxUpdateDobj.getTaxAmt() + " Modification  By [" + ServerUtil.getUserName(Long.parseLong(Util.getEmpCode())) + "( " + Util.getUserId() + " ) ] at Office [ " + ServerUtil.getOfficeName(Util.getUserOffCode(), Util.getUserStateCode()) + "(" + Util.getUserOffCode() + ")" + " ]";
            dataModification = dataModification + forNewLine + " Note-: If you found this information suspicious please contact to office admin.";
            ownerAdmin.sendMailChangeData(tmgr, Util.getUserStateCode(), "S", null, subject, dataModification);//Change data send to State Administrator
            ownerAdmin.sendMailChangeData(tmgr, Util.getUserStateCode(), "A", Util.getUserOffCode(), subject, dataModification);//Change data send to Office Administrator
            //end

            tmgr.commit();
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " : " + ex.getStackTrace()[0]);
            }

        }
        return false;
    }

    public void moveToHistoryVhLttUpdate(TransactionManager tmgr, LifeTimeTaxUpdateDobj lifeTimeTaxUpdateDobj) throws VahanException {
        try {
            String str = "insert into vh_ltt_amt_update select state_cd, off_cd, appl_no, regn_no, tax_amt,remark, ? as moved_by, current_timestamp from "
                    + "va_ltt_amt_update where appl_no=? and regn_no=? and state_cd=?  and off_cd=?";
            PreparedStatement pms = tmgr.prepareStatement(str);
            pms.setString(1, lifeTimeTaxUpdateDobj.getEmpCode());
            pms.setString(2, lifeTimeTaxUpdateDobj.getApplNo());
            pms.setString(3, lifeTimeTaxUpdateDobj.getRegnNo());
            pms.setString(4, lifeTimeTaxUpdateDobj.getStateCd());
            pms.setInt(5, lifeTimeTaxUpdateDobj.getOffcd());
            ServerUtil.validateQueryResult(tmgr, pms.executeUpdate(), pms);
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }

    }

    public void UpdateIntoVaLttAmtUpdate(TransactionManager tmgr, LifeTimeTaxUpdateDobj lifeTimeTaxUpdateDobj) throws VahanException {
        try {
            String str = "update va_ltt_amt_update set tax_amt=? ,remark=?,op_dt=current_timestamp  where appl_no=? and regn_no=? and state_cd=?  and off_cd=?";
            PreparedStatement pms = tmgr.prepareStatement(str);
            pms.setLong(1, lifeTimeTaxUpdateDobj.getTaxAmt());
            pms.setString(2, lifeTimeTaxUpdateDobj.getRemark());
            pms.setString(3, lifeTimeTaxUpdateDobj.getApplNo());
            pms.setString(4, lifeTimeTaxUpdateDobj.getRegnNo());
            pms.setString(5, lifeTimeTaxUpdateDobj.getStateCd());
            pms.setInt(6, lifeTimeTaxUpdateDobj.getOffcd());
            ServerUtil.validateQueryResult(tmgr, pms.executeUpdate(), pms);
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }
    }

    public void insertInotVaLttAmtUpdate(TransactionManager tmgr, LifeTimeTaxUpdateDobj lifeTimeTaxUpdateDobj) throws VahanException {
        try {
            String gry = "INSERT INTO vahan4.va_ltt_amt_update(\n"
                    + "            state_cd, off_cd, appl_no, regn_no, tax_amt, remark, created_by, \n"
                    + "            op_dt)\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, \n"
                    + "            current_timestamp)";
            PreparedStatement ps = tmgr.prepareStatement(gry);
            ps.setString(1, lifeTimeTaxUpdateDobj.getStateCd());
            ps.setInt(2, lifeTimeTaxUpdateDobj.getOffcd());
            ps.setString(3, lifeTimeTaxUpdateDobj.getApplNo());
            ps.setString(4, lifeTimeTaxUpdateDobj.getRegnNo());
            ps.setLong(5, lifeTimeTaxUpdateDobj.getTaxAmt());
            ps.setString(6, lifeTimeTaxUpdateDobj.getRemark());
            ps.setString(7, lifeTimeTaxUpdateDobj.getEmpCode());
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }
    }

    public long getLifeTaxUpdateAmt(LifeTimeTaxUpdateDobj lifeTimeTaxUpdateDobj) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        long taxAmt = 0;
        try {
            tmgr = new TransactionManagerReadOnly("getLifeTaxAmt Method");
            String qry = "select * from va_ltt_amt_update where regn_no=? and state_cd=?  and off_cd=?";
            PreparedStatement pms = tmgr.prepareStatement(qry);
            pms.setString(1, lifeTimeTaxUpdateDobj.getRegnNo());
            pms.setString(2, lifeTimeTaxUpdateDobj.getStateCd());
            pms.setInt(3, lifeTimeTaxUpdateDobj.getOffcd());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                taxAmt = rs.getLong("tax_amt");
            }

        } catch (Exception ex) {
            logger.error(ex.toString() + " : " + ex.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " : " + ex.getStackTrace()[0]);
            }
        }


        return taxAmt;
    }

    public String getPayTaxFromVahan4(String regnNo, String stateCd, int offCd) {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String rcptNo = "";
        try {
            tmgr = new TransactionManagerReadOnly("getPayTaxFromVahan4 Method");
            sql = " SELECT distinct a.rcpt_no, f.rcpt_dt \n"
                    + " from vt_tax f  \n"
                    + " inner join vp_appl_rcpt_mapping a on f.rcpt_no = a.rcpt_no and f.state_cd = a.state_cd and f.off_cd = a.off_cd \n"
                    + " where f.regn_no = ? and a.state_cd = ? and a.off_cd = ? and f.tax_mode <> 'B' \n"
                    + " order by 2 desc ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                rcptNo = rs.getString("rcpt_no");
            }
        } catch (Exception e) {
            logger.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                logger.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return rcptNo;
    }

    public String checkEntryInValttamtupdate(String regnNo, String stateCd, int offCd) {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String regNo = "";
        try {
            tmgr = new TransactionManagerReadOnly("checkEntryInValttamtupdate Method");
            sql = "select * from va_details d inner join va_ltt_amt_update l on d.appl_no=l.appl_no and d.state_cd=l.state_cd and d.off_cd=l.off_cd "
                    + "where d.regn_no=? and d.state_cd=? and d.off_cd=? and d.entry_status='A'";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                regNo = rs.getString("regn_no");
            }
        } catch (Exception e) {
            logger.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                logger.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return regNo;
    }
}
