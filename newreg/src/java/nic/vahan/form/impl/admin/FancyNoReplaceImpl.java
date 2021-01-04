/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.admin;

import java.sql.PreparedStatement;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.fancy.FancyNoReplaceDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;

/**
 *
 * @author nicsi
 */
public class FancyNoReplaceImpl {

    private static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(FancyNoReplaceImpl.class);

    public boolean saveRegisterdNoToFancyNo(String stateCd, String empCode, FancyNoReplaceDobj fancyNoDobj, Owner_dobj ownerDobj, TmConfigurationDobj tmConfDobj, boolean blnOldRegParam) throws VahanException {

        TransactionManager tmgr = null;
        String fancyNo = null;
        try {
            if (Util.getEmpCode() == null) {
                throw new VahanException(Util.getLocaleSessionMsg());
            }

            if (fancyNoDobj.isAssignAdvanceNunber()) {
                fancyNo = fancyNoDobj.getAssignAdvanceNO();
            } else if (fancyNoDobj.isAssignRetainNo()) {
                fancyNo = fancyNoDobj.getAssignRetainRegnNo();
            }

            String regnNo = ownerDobj.getRegn_no().trim();

            if (CommonUtils.isNullOrBlank(fancyNo) || ownerDobj == null || tmConfDobj == null || CommonUtils.isNullOrBlank(fancyNoDobj.getApplNo())) {
                if (!blnOldRegParam) {
                    throw new VahanException(Util.getLocaleSomthingMsg());
                }
            }
            String applNo = fancyNoDobj.getApplNo();
            int pur_cd = fancyNoDobj.getPurCode();

            String sql = "";
            PreparedStatement ps = null;
            tmgr = new TransactionManager("InsaveRegisterdNoToFancyNoMethod");
            sql = "Update " + TableList.VT_OTHER_STATE_VEH + " set new_regn_no = ? WHERE new_regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, fancyNo);
            ps.setString(2, regnNo);
            ps.setString(3, stateCd);
            ps.setInt(4, ownerDobj.getOff_cd());
            ps.executeUpdate();
            String[] vt_Tables = {
                TableList.VT_OWNER,
                TableList.VT_OWNER_IDENTIFICATION,
                TableList.VT_INSURANCE,
                TableList.VT_AXLE,
                TableList.VT_TMP_REGN_DTL,
                TableList.VT_OWNER_EX_ARMY,
                TableList.VT_IMPORT_VEH,
                TableList.VT_RETROFITTING_DTLS,
                TableList.VT_SPEED_GOVERNOR,
                TableList.VT_REFLECTIVE_TAPE,
                TableList.VT_HYPTH,
                TableList.VT_TRAILER,
                TableList.VT_PUCC,
                TableList.VT_FITNESS,
                TableList.VT_INSPECTION,
                TableList.VT_FEE,
                TableList.VT_TAX,
                TableList.VT_TAX_BASED_ON,
                TableList.VT_SIDE_TRAILER,
                TableList.VT_VLTD
            };
            for (int i = 0; i < vt_Tables.length; i++) {
                updateFancyNoIntoVt_Table(ownerDobj, vt_Tables[i], tmgr, stateCd, fancyNo);
            }


            if (blnOldRegParam) {

                applNo = ServerUtil.getUniqueApplNo(tmgr, stateCd);
                int i = 1;
                sql = "INSERT INTO " + TableList.VH_RE_ASSIGN + "("
                        + "            state_cd, off_cd, appl_no, old_regn_no, new_regn_no, reason,"
                        + "            moved_on, moved_by)"
                        + "    VALUES (?, ?, ?, ?, ?, ?,"
                        + "            current_timestamp, ?)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(i++, stateCd);
                ps.setInt(i++, ownerDobj.getOff_cd());
                ps.setString(i++, applNo);
                ps.setString(i++, regnNo);
                ps.setString(i++, fancyNo);
                ps.setString(i++, "OLD NUMBER REPLACEMENT");
                ps.setString(i++, Util.getEmpCode());
                ps.executeUpdate();

                sql = "INSERT INTO " + TableList.VA_DETAILS + " (appl_no, pur_cd, appl_dt, regn_no, user_id, user_type, entry_ip,entry_status,"
                        + "  confirm_ip, confirm_status, confirm_date, state_cd,off_cd) "
                        + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp, ?, ?)";

                i = 1;
                ps = tmgr.prepareStatement(sql);
                ps.setString(i++, applNo);
                ps.setInt(i++, TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO);
                ps.setTimestamp(i++, ServerUtil.getSystemDateInPostgres());
                ps.setString(i++, fancyNo);
                ps.setString(i++, Util.getUserId());
                ps.setString(i++, "");
                ps.setString(i++, Util.getClientIpAdress());
                ps.setString(i++, "A");
                ps.setString(i++, Util.getClientIpAdress());
                ps.setString(i++, TableConstants.STATUS_APPROVED);
                ps.setString(i++, stateCd);
                ps.setInt(i++, ownerDobj.getOff_cd());
                ps.executeUpdate();

            }


            //******************************************************** Schema HSRP ***************************************************************************
            RowSet rs;
            boolean ishsrp = ServerUtil.verifyForHsrp(stateCd, ownerDobj.getOff_cd(), tmgr);
            if (ishsrp) {
                if (!blnOldRegParam) {
                    sql = " select * from " + TableList.VT_HSRP + " WHERE regn_no = ? and state_cd = ? and off_cd = ? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, regnNo);
                    ps.setString(2, stateCd);
                    ps.setInt(3, ownerDobj.getOff_cd());
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        ServerUtil.insertHsrpDetail(applNo, fancyNo, "NB", stateCd, ownerDobj.getOff_cd(), tmgr);
                        sql = "Delete from " + TableList.VT_HSRP + "  WHERE regn_no = ? and state_cd = ? and off_cd = ? ";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, regnNo);
                        ps.setString(2, stateCd);
                        ps.setInt(3, ownerDobj.getOff_cd());
                        ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                        ServerUtil.deleteFromTable(tmgr, null, applNo, TableList.VHA_HSRP);
                    } else {
                        updateFancyNoIntoVHA_Table(ownerDobj, TableList.VA_HSRP, tmgr, stateCd, fancyNo, applNo);
                        updateFancyNoIntoVHA_Table(ownerDobj, TableList.VHA_HSRP, tmgr, stateCd, fancyNo, applNo);
                    }
                } else {
                    ServerUtil.insertHsrpDetail(applNo, fancyNo, "NB", stateCd, ownerDobj.getOff_cd(), tmgr);
                }
            }

            //****************************************************************************************End  Schema HSRP************************************


            //******************************************************** Schema SmartCard ***************************************************************************
            boolean isSmartCard = ServerUtil.verifyForSmartCard(stateCd, ownerDobj.getOff_cd(), tmgr);
            if (isSmartCard) {
                if (!blnOldRegParam) {
                    sql = "SELECT * FROM " + TableList.RC_IA_TO_BE
                            + " WHERE vehregno in (?, rpad(?, 10, ?)) AND STATUS='0' and rcpt_no=? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, regnNo);
                    ps.setString(2, regnNo);
                    ps.setString(3, " ");
                    ps.setString(4, applNo);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        sql = "INSERT INTO  " + TableList.RC_BE_TO_BO
                                + "    SELECT rpad(left(trim(?), 10), 10, ' ') as vehregno, regdate, regexp_replace(ownername, '[\"'';`]', ' ','g'), "
                                + "       regexp_replace(fname, '[\"'';`]', ' ','g'), regexp_replace(caddress, '[\"'';`]', ' ','g'), manufacturer, \n"
                                + "       regexp_replace(modelno, '[\"'';`]', ' ','g'), colour, fuel, vehclass, regexp_replace(bodytype, '[\"'';`]', ' ','g'), seatcap, standcap, \n"
                                + "       manufdate, unladenwt, cubiccap, wheelbase, noofcylin, ownerserial, \n"
                                + "       chasisno, engineno, taxpaidupto, regnvalidity, approvingauth, \n"
                                + "       finname, finaddress, hypofrom, hypoto, nocno, stateto, rtoto, \n"
                                + "       ncrbclearno, nocissuedt, inscompname, coverpolicyno, instype, \n"
                                + "       insvalidupto, puccentercode, pucvalidupto, taxamount, fine, exemptrecptno, \n"
                                + "       paymentdt, taxvalidfrom, taxvalidto, exemption, drtocode, buflag, \n"
                                + "       fitvalidupto, fitinsofficer, fitlocation, grossvehwt, semitrailers, \n"
                                + "       tyreinfo, axleinfo, rcpt_no, pur_cd, ?, current_timestamp, '1' from " + TableList.SMART_CARD
                                + " where rcpt_no=?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, fancyNo);
                        ps.setString(2, Util.getEmpCode());
                        ps.setString(3, applNo);
                        ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                    } else {
                        // Update in RC_BE_TO_BO Table
                        sql = "update " + TableList.RC_BE_TO_BO + " set vehregno=rpad(left(trim(?), 10), 10, ' ') where vehregno in (?, rpad(?, 10, ?))";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, fancyNo);//Up
                        ps.setString(2, regnNo);
                        ps.setString(3, regnNo);
                        ps.setString(4, " ");
                        ps.executeUpdate();

                    }

                    //Update in SMART_CARD Table
                    sql = "update " + TableList.SMART_CARD + " set vehregno=rpad(left(trim(?), 10), 10, ' ') where vehregno in (?, rpad(?, 10, ?)) ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, fancyNo);
                    ps.setString(2, regnNo);
                    ps.setString(3, regnNo);
                    ps.setString(4, " ");
                    ps.executeUpdate();

                    sql = "UPDATE smartcard.va_smart_card_temp set vehregno=rpad(left(trim(?), 10), 10, ' ') WHERE  vehregno in (?, rpad(?, 10, ?))";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, fancyNo);
                    ps.setString(2, regnNo);
                    ps.setString(3, regnNo);
                    ps.setString(4, " ");
                    ps.executeUpdate();

                    sql = "UPDATE smartcard.va_smart_card set regn_no=rpad(left(trim(?), 10), 10, ' ') WHERE  regn_no in (?, rpad(?, 10, ?))";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, fancyNo);
                    ps.setString(2, regnNo);
                    ps.setString(3, regnNo);
                    ps.setString(4, " ");
                    ps.executeUpdate();

                    sql = "UPDATE smartcard.vh_smart_card set regn_no=rpad(left(trim(?), 10), 10, ' ') WHERE  regn_no in (?, rpad(?, 10, ?))";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, fancyNo);
                    ps.setString(2, regnNo);
                    ps.setString(3, regnNo);
                    ps.setString(4, " ");
                    ps.executeUpdate();

                } else {
                    TmConfigurationDobj tmConfig = Util.getTmConfiguration();
                    ServerUtil.insertForSmartCard(applNo, fancyNo, stateCd, ownerDobj.getOff_cd(), TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE, tmConfig, tmgr);
                    if (tmConfig != null) {
                        String user_cd = Util.getEmpCode();
                        if (tmConfig.isIs_rc_dispatch()) {
                            if (ServerUtil.verifyForPostalFee(stateCd, ownerDobj.getOff_cd(), applNo, tmgr)) {
                                ServerUtil.insertForDispatch(applNo, fancyNo, stateCd, ownerDobj.getOff_cd(), user_cd, tmgr);
                            }
                        } else if (tmConfig.isIs_rc_dispatch_without_postal_fee()) {
                            ServerUtil.insertForDispatch(applNo, fancyNo, stateCd, ownerDobj.getOff_cd(), user_cd, tmgr);
                        }
                    }
                }
            } else if (blnOldRegParam) {
                ServerUtil.insertForPrint(applNo, fancyNo, stateCd, ownerDobj.getOff_cd(), tmgr);
            }


            //****************************************************************end smartCard***********************************************************************


            //**************************************************************Inward Table******************************************************************************
            if (!blnOldRegParam) {
                String[] vha_Tables = {
                    TableList.VA_DETAILS,
                    TableList.VA_DISPATCH,//Dispatch Schema 
                    TableList.VHA_DISPATCH,
                    TableList.VHA_OWNER,
                    TableList.VHA_HPA,
                    TableList.VHA_INSURANCE,
                    TableList.VHA_FITNESS,
                    TableList.VHA_OWNER_IDENTIFICATION,
                    TableList.VHA_SIDE_TRAILER,
                    TableList.VHA_SPEED_GOVERNOR,
                    TableList.VHA_TRAILER,
                    TableList.VHA_TMP_REGN_DTL,
                    TableList.VHA_INSPECTION,
                    TableList.VA_RC_PRINT,
                    TableList.VH_RC_PRINT,
                    TableList.VA_FC_PRINT,
                    TableList.VH_FC_PRINT
                };
                for (int i = 0; i < vha_Tables.length; i++) {
                    updateFancyNoIntoVHA_Table(ownerDobj, vha_Tables[i], tmgr, stateCd, fancyNo, applNo);
                }
                sql = "update " + TableList.VHA_PUCC + " set regn_no=? where appl_no=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, fancyNo);
                ps.setString(2, regnNo);
                ps.executeUpdate();

                sql = "update " + TableList.VHA_REFLECTIVE_TAPE + " set regn_no=? where appl_no=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, fancyNo);
                ps.setString(2, regnNo);
                ps.executeUpdate();

                //*******************************************************************************Insert Into Series**************************************************************

                String seriesPart = regnNo.substring(0, regnNo.length() - 4);
                int series_id = 0;
                sql = "select * from " + TableList.VM_REGN_SERIES + " where  state_cd=? and off_cd=? and prefix_series=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setInt(2, ownerDobj.getOff_cd());
                ps.setString(3, seriesPart);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    series_id = rs.getInt("series_id");
                } else {
                    sql = "select * from " + TableList.VHM_REGN_SERIES + " where  state_cd=? and off_cd=? and prefix_series=? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, stateCd);
                    ps.setInt(2, ownerDobj.getOff_cd());
                    ps.setString(3, seriesPart);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        series_id = rs.getInt("series_id");
                    }
                }
                if (series_id == 0) {
                    throw new VahanException("No Valid Series Found For Number : " + regnNo);
                }

                sql = "INSERT INTO " + TableList.VM_REGN_AVAILABLE + " (state_cd,off_cd,regn_no,status,amount,entered_by,entered_on,prefix_series,series_id) \n"
                        + " VALUES (?,?,?,'A',?,?,current_timestamp,?,?);";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setInt(2, ownerDobj.getOff_cd());
                ps.setString(3, regnNo);
                ps.setInt(4, 0);
                ps.setString(5, Util.getEmpCode());
                ps.setString(6, seriesPart);
                ps.setInt(7, series_id);
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
            }

//            updateFancyNoIntoVt_Table(ownerDobj, TableList.VM_REGN_ALLOTED, tmgr, stateCd, fancyNo);
            if (fancyNoDobj.isAssignAdvanceNunber()) {
                updateVM_RegNo_Allocated(ownerDobj, TableList.VM_REGN_ALLOTED, tmgr, stateCd, fancyNo);
            } else if (fancyNoDobj.isAssignRetainNo()) {
                sql = "select * from " + TableList.VM_REGN_ALLOTED + " where regn_no =? and state_cd=? and off_cd=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, fancyNo);
                ps.setString(2, stateCd);
                ps.setInt(3, ownerDobj.getOff_cd());
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    sql = "Insert into vhm_regn_alloted select state_cd, off_cd, regn_no, appl_no, op_dt,?,current_timestamp as moved_on from " + TableList.VM_REGN_ALLOTED + " where regn_no =? and state_cd=? and off_cd=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, Util.getEmpCode());
                    ps.setString(2, ownerDobj.getRegn_no());
                    ps.setString(3, stateCd);
                    ps.setInt(4, ownerDobj.getOff_cd());
                    ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                    sql = "delete from " + TableList.VM_REGN_ALLOTED + " where regn_no =? and state_cd=? and off_cd=? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, ownerDobj.getRegn_no());
                    ps.setString(2, stateCd);
                    ps.setInt(3, ownerDobj.getOff_cd());
                    ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                } else {
                    updateVM_RegNo_Allocated(ownerDobj, TableList.VM_REGN_ALLOTED, tmgr, stateCd, fancyNo);
                }
            }
            //comman 
            // insert int vt_replace_regn_no
            sql = "insert into vahan4.vt_replace_advance_regn_no (state_cd,off_cd,old_regn_no,new_regn_no,rcpt_no,remark,moved_by,moved_on) values(?,?,?,?,?,?,?,current_timestamp)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, ownerDobj.getOff_cd());
            ps.setString(3, ownerDobj.getRegn_no());
            ps.setString(4, fancyNo);
            ps.setString(5, fancyNoDobj.getRcpt_no());
            ps.setString(6, fancyNoDobj.getRemarks());
            ps.setString(7, Util.getEmpCode());
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
            if (!CommonUtils.isNullOrBlank(applNo) || blnOldRegParam) {
                if (fancyNoDobj.isAssignAdvanceNunber()) {
                    sql = "update " + TableList.VT_ADVANCE_REGN_NO + " set regn_appl_no =? where state_cd=? and regn_no=? and off_cd=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, applNo);
                    ps.setString(2, stateCd);
                    ps.setString(3, fancyNo);
                    ps.setInt(4, ownerDobj.getOff_cd());
                    ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                } else if (fancyNoDobj.isAssignRetainNo()) {
                    sql = "update " + TableList.VT_SURRENDER_RETENTION + " set regn_appl_no =? where old_regn_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, applNo);
                    ps.setString(2, fancyNo);
                    ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                }
                tmgr.commit();
                return true;

            } else {
                return false;
            }
        } catch (VahanException vx) {
            throw vx;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage() + " : " + ex.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }

            } catch (Exception e) {
                LOGGER.error(e.getMessage() + " : " + e.getStackTrace()[0]);
            }
        }


    }

    public void updateFancyNoIntoVt_Table(Owner_dobj dobj, String tableName, TransactionManager tmgr, String state_cd, String fancyNo) throws VahanException {
        String sql = "Update " + tableName + " set regn_no = ? WHERE regn_no = ? and state_cd = ? and off_cd = ? ";
        try {

            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, fancyNo);
            ps.setString(2, dobj.getRegn_no());
            ps.setString(3, state_cd);
            ps.setInt(4, dobj.getOff_cd());
            ps.executeUpdate();

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }
    }

    public void updateVM_RegNo_Allocated(Owner_dobj dobj, String tableName, TransactionManager tmgr, String state_cd, String fancyNo) throws VahanException {
        String sql = "Update " + tableName + " set regn_no = ? WHERE regn_no = ? and state_cd = ? and off_cd = ? ";
        try {

            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, fancyNo);
            ps.setString(2, dobj.getRegn_no());
            ps.setString(3, state_cd);
            ps.setInt(4, dobj.getOff_cd());
            ps.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }
    }

    public FancyNoReplaceDobj getApplNoAndPurCode(String stateCd, String regnNo) throws VahanException {
        String isPending = null;
        TransactionManager tmgr = null;
        FancyNoReplaceDobj fancyDobj = null;
        try {
            tmgr = new TransactionManager("getApplNoAndPurCode");
            String sql = "select appl_no,pur_cd from " + TableList.VA_DETAILS + " where regn_no=? and pur_cd in (1,123,5,15) and state_cd=? and entry_status=?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            ps.setString(3, TableConstants.STATUS_APPROVED);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (rs.getInt("pur_cd") == TableConstants.VM_TRANSFER_OWNER_PUR_CD) {
                    sql = "select * from va_details where state_cd=? and off_cd=? and regn_no=? and pur_cd=? and appl_dt < (select max(appl_dt) from va_details where state_cd=? and off_cd=? and regn_no=?)";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, stateCd);
                    ps.setInt(2, Util.getSelectedSeat().getOff_cd());
                    ps.setString(3, regnNo);
                    ps.setInt(4, TableConstants.VM_TRANSFER_OWNER_PUR_CD);
                    ps.setString(5, stateCd);
                    ps.setInt(6, Util.getSelectedSeat().getOff_cd());
                    ps.setString(7, regnNo);
                    RowSet rs1 = tmgr.fetchDetachedRowSet();
                    if (rs1.next()) {
                        throw new VahanException("For Registration Number " + "[" + regnNo + "] " + " another transaction has already been inwarded.You Can not Replace Assigned Registered Vehicle Number.");
                    }
                }
                fancyDobj = new FancyNoReplaceDobj();
                fancyDobj.setApplNo(rs.getString("appl_no"));
                fancyDobj.setPurCode(rs.getInt("pur_cd"));
            }
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);

        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return fancyDobj;

    }

    public void updateFancyNoIntoVHA_Table(Owner_dobj dobj, String tableName, TransactionManager tmgr, String state_cd, String fancyNo, String applNo) throws VahanException {
        String sql = "Update " + tableName + " set regn_no = ? WHERE appl_no = ? and state_cd = ? and off_cd = ? ";
        try {

            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, fancyNo);
            ps.setString(2, applNo);
            ps.setString(3, state_cd);
            ps.setInt(4, dobj.getOff_cd());
            ps.executeUpdate();

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        }
    }
}
