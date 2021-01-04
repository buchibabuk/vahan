package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.ChipDetailsDobj;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author nicsi
 */
public class ChipDetailsImpl {

    private static final Logger LOGGER = Logger.getLogger(ChipDetailsImpl.class);

    public static ChipDetailsDobj getDataFromDataTable() throws VahanException, Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        ArrayList<ChipDetailsDobj> list = new ArrayList<>();
        String sql = "Select * from " + TableList.VM_WSDL_IP + " where  state_cd=? and off_cd=?";
        ChipDetailsDobj chipDtlsDobj = null;
        try {
            tmgr = new TransactionManager("getDataFromDataTable");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserOffCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                chipDtlsDobj = new ChipDetailsDobj();
                chipDtlsDobj.setState_cd(rs.getString("state_cd"));
                chipDtlsDobj.setOff_cd(rs.getInt("off_cd"));
                chipDtlsDobj.setIa1_chip_no(rs.getString("ia1_chip_no"));
                chipDtlsDobj.setIa2_chip_no(rs.getString("ia2_chip_no"));
                chipDtlsDobj.setIa3_chip_no(rs.getString("ia3_chip_no"));
                chipDtlsDobj.setIa4_chip_no(rs.getString("ia4_chip_no"));
                chipDtlsDobj.setIa5_chip_no(rs.getString("ia5_chip_no"));
                chipDtlsDobj.setIa6_chip_no(rs.getString("ia6_chip_no"));
                chipDtlsDobj.setTest_server_status(rs.getBoolean("test_server_status"));
                chipDtlsDobj.setStatge_server_status(rs.getBoolean("statge_server_status"));
                chipDtlsDobj.setProd_server_status(rs.getBoolean("prod_server_status"));
                chipDtlsDobj.setTest_server_ip4(rs.getString("test_server_ip4"));
                chipDtlsDobj.setStage_server_ip4(rs.getString("stage_server_ip4"));
                chipDtlsDobj.setProd_server_ip4(rs.getString("prod_server_ip4"));
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (tmgr != null) {
                tmgr.release();
            }
        }
        return chipDtlsDobj;
    }

    public static String insertDataintoDataTable(ChipDetailsDobj dobj) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String msg = null;
        String sql = "INSERT INTO  " + TableList.VM_WSDL_IP + " ("
                + "            state_cd, off_cd, test_server_ip4, stage_server_ip4, prod_server_ip4, "
                + "            test_server_status, statge_server_status, prod_server_status, "
                + "            ia1_chip_no, ia2_chip_no, ia3_chip_no, ia4_chip_no, ia5_chip_no, "
                + "            ia6_chip_no)"
                + "    VALUES (?, ?, ?, ?, ?,"
                + "            ?, ?, ?,"
                + "            ?, ?, ?, ?, ?,"
                + "            ?)";
        try {
            tmgr = new TransactionManager("insertDataintoDataTable");
            ps = tmgr.prepareStatement(sql);

            int i = 1;
            ps.setString(i++, dobj.getState_cd());
            ps.setInt(i++, dobj.getOff_cd());
            if (CommonUtils.isNullOrBlank(dobj.getTest_server_ip4())) {
                ps.setString(i++, null);
            } else {
                ps.setString(i++, dobj.getTest_server_ip4());
            }
            if (CommonUtils.isNullOrBlank(dobj.getStage_server_ip4())) {
                ps.setString(i++, null);
            } else {
                ps.setString(i++, dobj.getStage_server_ip4());
            }
            if (CommonUtils.isNullOrBlank(dobj.getProd_server_ip4())) {
                ps.setString(i++, null);
            } else {
                ps.setString(i++, dobj.getProd_server_ip4());
            }

            ps.setBoolean(i++, dobj.isTest_server_status());
            ps.setBoolean(i++, dobj.isStatge_server_status());
            ps.setBoolean(i++, dobj.isProd_server_status());
            if (CommonUtils.isNullOrBlank(dobj.getIa1_chip_no())) {
                ps.setString(i++, null);
            } else {
                ps.setString(i++, dobj.getIa1_chip_no());
            }
            if (CommonUtils.isNullOrBlank(dobj.getIa2_chip_no())) {
                ps.setString(i++, null);
            } else {
                ps.setString(i++, dobj.getIa2_chip_no());
            }
            if (CommonUtils.isNullOrBlank(dobj.getIa3_chip_no())) {
                ps.setString(i++, null);
            } else {
                ps.setString(i++, dobj.getIa3_chip_no());
            }
            if (CommonUtils.isNullOrBlank(dobj.getIa4_chip_no())) {
                ps.setString(i++, null);
            } else {
                ps.setString(i++, dobj.getIa4_chip_no());
            }
            if (CommonUtils.isNullOrBlank(dobj.getIa5_chip_no())) {
                ps.setString(i++, null);
            } else {
                ps.setString(i++, dobj.getIa5_chip_no());
            }
            if (CommonUtils.isNullOrBlank(dobj.getIa6_chip_no())) {
                ps.setString(i++, null);
            } else {
                ps.setString(i++, dobj.getIa6_chip_no());
            }
            ps.executeUpdate();
            tmgr.commit();
            msg = "Data SuccessFully Added";
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            msg = TableConstants.SomthingWentWrong;
            throw new VahanException(msg);
        } finally {
            try {
                ps.close();
                tmgr.release();
            } catch (Exception e) {
                msg = TableConstants.SomthingWentWrong;
            }
        }
        return msg;
    }

    public static String modifydeleteintoDataTable(ChipDetailsDobj dobj) throws VahanException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        String msg = null;

        String sql = "INSERT INTO " + TableList.VHM_WSDL_IP + "("
                + "            moved_on, moved_by, state_cd, off_cd, test_server_ip4, stage_server_ip4, "
                + "            prod_server_ip4, test_server_status, statge_server_status, prod_server_status, "
                + "            ia1_chip_no, ia2_chip_no, ia3_chip_no, ia4_chip_no, ia5_chip_no, "
                + "            ia6_chip_no)"
                + "    (SELECT CURRENT_TIMESTAMP,?,state_cd, off_cd, test_server_ip4, stage_server_ip4, prod_server_ip4, "
                + "       test_server_status, statge_server_status, prod_server_status, "
                + "       ia1_chip_no, ia2_chip_no, ia3_chip_no, ia4_chip_no, ia5_chip_no, "
                + "       ia6_chip_no"
                + "  FROM " + TableList.VM_WSDL_IP + " WHERE state_cd=? and off_cd=? )";
        try {
            tmgr = new TransactionManager("insertDataintoDataTable");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getUserOffCode());
            int l = ps.executeUpdate();

            if (l > 0) {
                ps = null;
                sql = "UPDATE " + TableList.VM_WSDL_IP + " "
                        + "   SET test_server_ip4=?, stage_server_ip4=?, "
                        + "       prod_server_ip4=?, test_server_status=?, statge_server_status=?, "
                        + "       prod_server_status=?, ia1_chip_no=?, ia2_chip_no=?, ia3_chip_no=?, "
                        + "       ia4_chip_no=?, ia5_chip_no=?, ia6_chip_no=?"
                        + " WHERE state_cd=? and off_cd=?";
                int i = 1;

                ps = tmgr.prepareStatement(sql);
                if (CommonUtils.isNullOrBlank(dobj.getTest_server_ip4())) {
                    ps.setString(i++, null);
                } else {
                    ps.setString(i++, dobj.getTest_server_ip4());
                }
                if (CommonUtils.isNullOrBlank(dobj.getStage_server_ip4())) {
                    ps.setString(i++, null);
                } else {
                    ps.setString(i++, dobj.getStage_server_ip4());
                }
                if (CommonUtils.isNullOrBlank(dobj.getProd_server_ip4())) {
                    ps.setString(i++, null);
                } else {
                    ps.setString(i++, dobj.getProd_server_ip4());
                }

                ps.setBoolean(i++, dobj.isTest_server_status());
                ps.setBoolean(i++, dobj.isStatge_server_status());
                ps.setBoolean(i++, dobj.isProd_server_status());
                if (CommonUtils.isNullOrBlank(dobj.getIa1_chip_no())) {
                    ps.setString(i++, null);
                } else {
                    ps.setString(i++, dobj.getIa1_chip_no());
                }
                if (CommonUtils.isNullOrBlank(dobj.getIa2_chip_no())) {
                    ps.setString(i++, null);
                } else {
                    ps.setString(i++, dobj.getIa2_chip_no());
                }
                if (CommonUtils.isNullOrBlank(dobj.getIa3_chip_no())) {
                    ps.setString(i++, null);
                } else {
                    ps.setString(i++, dobj.getIa3_chip_no());
                }

                if (CommonUtils.isNullOrBlank(dobj.getIa4_chip_no())) {
                    ps.setString(i++, null);
                } else {
                    ps.setString(i++, dobj.getIa4_chip_no());
                }
                if (CommonUtils.isNullOrBlank(dobj.getIa5_chip_no())) {
                    ps.setString(i++, null);
                } else {
                    ps.setString(i++, dobj.getIa5_chip_no());
                }
                if (CommonUtils.isNullOrBlank(dobj.getIa6_chip_no())) {
                    ps.setString(i++, null);
                } else {
                    ps.setString(i++, dobj.getIa6_chip_no());
                }

                ps.setString(i++, dobj.getState_cd());
                ps.setInt(i++, dobj.getOff_cd());
                ps.executeUpdate();
            }
            tmgr.commit();
            msg = "Data SuccessFully Updated";
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            msg = TableConstants.SomthingWentWrong;
            throw new VahanException(msg);
        } finally {
            try {
                ps.close();
                tmgr.release();
            } catch (Exception e) {
                msg = "Data Not Updated";
            }
        }
        return msg;
    }
}
