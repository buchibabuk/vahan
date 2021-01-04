/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.user_mgmt.dobj.DealerMasterDobj;
import nic.vahan.form.dobj.tradecert.TradeCertDetailsDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC103
 */
public class DealerMasterImpl {

    private static final Logger LOGGER = Logger.getLogger(DealerMasterImpl.class);

    public void fillDt(DealerMasterDobj dealerDobj) {

        String state_cd = dealerDobj.getStateCode();
        int off_cd = dealerDobj.getOffCode();
        String user_catg = dealerDobj.getUserCatg().trim();
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String sql;
        try {
            tmgr = new TransactionManager("fillDt");
            if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_OFFICE_ADMIN)) {
                sql = "Select dealer_cd,dealer_name from " + TableList.VM_DEALER_MAST + " where state_cd=? and off_cd=? "
                        + "order by dealer_cd";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, state_cd);
                ps.setInt(2, off_cd);
            }

            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dealerDobj.getDtMap().put(rs.getString("dealer_cd"), rs.getString("dealer_name"));
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
    }

    public void getDealerDetails(DealerMasterDobj dealerDobj) {
        String dealerCode = dealerDobj.getDealerCode();
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;

        try {
            tmgr = new TransactionManager("getDealerDetails");
            String sql = "Select * from " + TableList.VM_DEALER_MAST + " where dealer_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dealerCode);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dealerDobj.setDealerName(rs.getString("dealer_name"));
                dealerDobj.setDealerRegnNo(rs.getString("dealer_regn_no"));
                dealerDobj.setDealerAdd1(rs.getString("d_add1"));
                dealerDobj.setDealerAdd2(rs.getString("d_add2"));
                dealerDobj.setDealerDistrict(rs.getInt("d_district"));
                dealerDobj.setDealerPincode(rs.getInt("d_pincode"));
                dealerDobj.setDealerStateCode(rs.getString("d_state"));
                dealerDobj.setDealerValidUpto(rs.getDate("valid_upto"));
                dealerDobj.setTin_NO(rs.getString("tin_no"));
                dealerDobj.setRegistrationMarkAuth(rs.getBoolean("regn_mark_gen_by_dealer"));
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
    }

    public boolean checkInsertOrUpdateDealerDetails(DealerMasterDobj dealerDobj) throws VahanException {
        boolean flag = false;
        PreparedStatement ps;
        TransactionManager tmgr = null;
        RowSet rs;
        String sql;
        String dealerCode = " ";
        if (!dealerDobj.getDealerCode().isEmpty()) {
            dealerCode = dealerDobj.getDealerCode();
        }
        try {
            tmgr = new TransactionManager("checkInsertOrUpdateDealerDetails");
            sql = "Select dealer_cd from " + TableList.VM_DEALER_MAST + " where dealer_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dealerCode);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                flag = updateDealerDetails(dealerDobj, tmgr);
            } else {
                if (!validateUniqueTinNo(dealerDobj.getTin_NO(), dealerCode)) {
                    flag = insertDealerDetails(dealerDobj, tmgr);
                } else {
                    throw new VahanException("Duplicate TIN Number.");
                }
            }
            tmgr.commit();
        } catch (VahanException e) {
            throw e;
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return flag;
    }

    private boolean updateDealerDetails(DealerMasterDobj dobj, TransactionManager tmgr) throws VahanException {
        boolean updateFlag = false;
        PreparedStatement ps;
        int i = 1;
        try {
            insertHisotry(dobj, tmgr);
            String sql = "UPDATE " + TableList.VM_DEALER_MAST + "\n"
                    + "   SET dealer_name=?, dealer_regn_no=?, \n"
                    + "       d_add1=?, d_add2=?, d_district=?, d_pincode=?, d_state=?, valid_upto=?, \n"
                    + "       entered_by=?, entered_on = current_timestamp, tin_no=?,regn_mark_gen_by_dealer =?\n"
                    + " WHERE dealer_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(i++, dobj.getDealerName());
            ps.setString(i++, dobj.getDealerRegnNo());
            ps.setString(i++, dobj.getDealerAdd1());
            ps.setString(i++, dobj.getDealerAdd2());
            ps.setInt(i++, dobj.getDealerDistrict());
            ps.setInt(i++, dobj.getDealerPincode());
            ps.setString(i++, dobj.getDealerStateCode());

            if (dobj.getDealerValidUpto() != null) {
                ps.setDate(i++, new java.sql.Date(dobj.getDealerValidUpto().getTime()));
            } else {
                ps.setNull(i++, java.sql.Types.DATE);

            }
            ps.setString(i++, Util.getEmpCode());
            ps.setString(i++, dobj.getTin_NO());
            ps.setBoolean(i++, dobj.isRegistrationMarkAuth());
            ps.setString(i++, dobj.getDealerCode());
            int temp = ps.executeUpdate();
            if (temp > 0) {
                updateFlag = true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return updateFlag;
    }

    private boolean insertDealerDetails(DealerMasterDobj dobj, TransactionManager tmgr) {
        boolean insertFlag = false;
        PreparedStatement ps;
        String sql;
        String dealerCode;
        int i = 1;
        try {
            dealerCode = ServerUtil.getUniqueDealerCd(tmgr, dobj.getStateCode());
            dobj.setDealerCode(dealerCode);
            sql = "INSERT INTO " + TableList.VM_DEALER_MAST + "(\n"
                    + "            dealer_cd, state_cd, off_cd, dealer_name, dealer_regn_no, d_add1, \n"
                    + "            d_add2, d_district, d_pincode, d_state, valid_upto, entered_by, \n"
                    + "            entered_on, tin_no,regn_mark_gen_by_dealer)\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?, \n"
                    + "            ?, ?, ?, ?, ?, ?, \n"
                    + "           current_timestamp, ?,?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(i++, dobj.getDealerCode());
            ps.setString(i++, dobj.getStateCode());
            ps.setInt(i++, dobj.getOffCode());
            ps.setString(i++, dobj.getDealerName());
            ps.setString(i++, dobj.getDealerRegnNo());
            ps.setString(i++, dobj.getDealerAdd1());
            ps.setString(i++, dobj.getDealerAdd2());
            ps.setInt(i++, dobj.getDealerDistrict());
            ps.setInt(i++, dobj.getDealerPincode());
            ps.setString(i++, dobj.getDealerStateCode());
            if (dobj.getDealerValidUpto() != null) {
                ps.setDate(i++, new java.sql.Date(dobj.getDealerValidUpto().getTime()));
            } else {
                ps.setNull(i++, java.sql.Types.DATE);
            }
            ps.setString(i++, Util.getEmpCode());
            ps.setString(i++, dobj.getTin_NO());
            ps.setBoolean(i++, dobj.isRegistrationMarkAuth());
            int temp = ps.executeUpdate();
            if (temp > 0) {
                insertFlag = true;
            }
        } catch (SQLException | VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return insertFlag;
    }

    public static String insertHisotry(DealerMasterDobj dobj, TransactionManager tmgr) throws VahanException {
        try {
            PreparedStatement ps = null;
            String sql1 = "INSERT INTO " + TableList.VHM_DEALER_MAST + " \n"
                    + "SELECT dealer_cd, state_cd, off_cd, dealer_name, dealer_regn_no, d_add1, d_add2, d_district, d_pincode, d_state, valid_upto, entered_by, entered_on, tin_no,current_timestamp as moved_on,? as moved_by,regn_mark_gen_by_dealer \n"
                    + "  FROM " + TableList.VM_DEALER_MAST + " where dealer_cd=?";
            ps = tmgr.prepareStatement(sql1);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, dobj.getDealerCode());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in insertHisotry" + e.getMessage());
        }
        return "records inserted ";
    }

    public boolean validateUniqueTinNo(String tinNo, String dealerCode) {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        RowSet rs;
        boolean status = false;
        String query = "";
        try {
            if (!CommonUtils.isNullOrBlank(dealerCode)) {
                tmgr = new TransactionManager("validateUniqueTinNo");
                query = "Select tin_no from (Select tin_no,dealer_cd from " + TableList.VM_DEALER_MAST
                        + " where tin_no = ? and state_cd=? and off_cd=?) as aa where dealer_cd !=?";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, tinNo);
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                ps.setString(4, dealerCode);
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    status = true;
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
        return status;
    }

    public static List<TradeCertDetailsDobj> getTradeCertificateDetails(String dealerCode) {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        TradeCertDetailsDobj certDobj;
        List<TradeCertDetailsDobj> tradeCertDetailsList = new ArrayList<TradeCertDetailsDobj>();
        try {
            tmgr = new TransactionManager("getTradeCertificateDetails");
            sql = "select to_char(valid_from,'dd-MON-yyyy') as fromDate,to_char(valid_upto,'dd-MON-yyyy') as uptoDate,* from " + TableList.VT_TRADE_CERTIFICATE + " where state_cd = ?  and dealer_cd = ? order by vch_catg";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, dealerCode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                certDobj = new TradeCertDetailsDobj();
                certDobj.setDealerCd(dealerCode);
                certDobj.setNoOfVeh(rs.getInt("no_of_vch"));
                certDobj.setNoOfVehUsed(rs.getInt("no_of_vch_used"));
                certDobj.setOffCd(rs.getInt("off_cd"));
                certDobj.setStateCd(rs.getString("state_cd"));
                certDobj.setTradeCertNo(rs.getString("cert_no"));
                certDobj.setValidUpto(rs.getDate("valid_upto"));
                certDobj.setTradeValidFrom(rs.getString("fromDate"));
                certDobj.setTradeValidUpto(rs.getString("uptoDate"));
                certDobj.setVehCatg(rs.getString("vch_catg"));
                tradeCertDetailsList.add(certDobj);
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
        return tradeCertDetailsList;
    }
}