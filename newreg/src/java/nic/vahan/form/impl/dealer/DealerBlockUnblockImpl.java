/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.dealer;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.Dealer;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.impl.Util;

public class DealerBlockUnblockImpl {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(DealerBlockUnblockImpl.class);

    public int blockDealer(List<Dealer> dealerList) throws VahanException, Exception {
        PreparedStatement psInsert = null;
        TransactionManager tmgr = null;
        int count = 0;
        try {
            tmgr = new TransactionManager("insert Block Dealer Details");
            for (Dealer dlr : dealerList) {
                if (dlr.isBlockUnBlockStatus()) {
                    String insertSql = "INSERT INTO " + TableList.VP_DEALER_STATUS
                            + "(state_cd, off_cd, dealer_cd, reason, status, blocked_by, blocked_on)\n"
                            + "VALUES (?, ?, ?, ?, ?, ?, current_timestamp)";

                    psInsert = tmgr.prepareStatement(insertSql);
                    int i = 1;
                    psInsert.setString(i++, Util.getUserStateCode());
                    psInsert.setInt(i++, dlr.getOff_cd());
                    psInsert.setString(i++, dlr.getDealer_cd());
                    if (dlr.getReason() != null && !dlr.getReason().isEmpty()) {
                        psInsert.setString(i++, dlr.getReason());
                    } else {
                        throw new VahanException("Reason Can't be empty for the selected dealers");
                    }
                    psInsert.setString(i++, "B");
                    psInsert.setString(i++, Util.getEmpCode());
                    psInsert.executeUpdate();
                    count++;
                }
            }
            if (count > 0) {
                tmgr.commit();
            }
        } catch (VahanException vme) {
            throw vme;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw e;
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return count;
    }

    public static List<Dealer> getDealerUnBlockedList(String stateCd, int offCd) {
        List listDealer = new ArrayList();
        String sql = "select distinct m.dealer_name , m.dealer_cd \n"
                + "from  vm_dealer_mast m inner join tm_user_permissions up\n"
                + "on m.dealer_cd = up.dealer_cd \n"
                + "where m.state_cd = ? and m.off_cd = ? and m.dealer_cd Not in (select dealer_cd from vp_dealer_status s where s.state_cd = ? and s.off_cd = ?) ";
        TransactionManager tmgr = null;
        Dealer dobj = null;
        try {
            tmgr = new TransactionManager("getDealerUnBlockList");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            ps.setString(3, stateCd);
            ps.setInt(4, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new Dealer();
                dobj.setDealer_cd(rs.getString("dealer_cd"));
                dobj.setDealer_name(rs.getString("dealer_name"));
                dobj.setOff_cd(offCd);
                listDealer.add(dobj);
            }

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
        return listDealer;
    }

    public static List<Dealer> getDealerBlockedList(String stateCd, int offCd) {
        List listDealer = new ArrayList();
        String sql = "select m.dealer_name , m.dealer_cd ,up.status,up.reason \n"
                + "from  vm_dealer_mast m inner join vp_dealer_status up\n"
                + "on m.dealer_cd = up.dealer_cd \n"
                + "where m.state_cd = ? and m.off_cd = ? ";
        TransactionManager tmgr = null;
        Dealer dobj = null;
        try {
            tmgr = new TransactionManager("getDealerBlockList");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new Dealer();
                dobj.setDealer_cd(rs.getString("dealer_cd"));
                dobj.setDealer_name(rs.getString("dealer_name"));
                dobj.setReason(rs.getString("reason"));
                dobj.setOff_cd(offCd);
                listDealer.add(dobj);
            }

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
        return listDealer;
    }

    public int unBlockDealer(List<Dealer> dealerList) {
        PreparedStatement psInsertHistory = null;
        PreparedStatement psDelete = null;
        TransactionManager tmgr = null;
        int count = 0;
        try {
            tmgr = new TransactionManager("insert Block Dealer Details");
            for (Dealer dlr : dealerList) {
                if (dlr.isBlockUnBlockStatus()) {
                    String insertHisSql = "INSERT INTO " + TableList.VPH_DEALER_STATUS + " SELECT state_cd, off_cd, dealer_cd, reason, status, blocked_by, blocked_on,current_timestamp,? "
                            + " FROM " + TableList.VP_DEALER_STATUS + " where state_cd = ? and off_cd = ? and dealer_cd = ? ";

                    psInsertHistory = tmgr.prepareStatement(insertHisSql);
                    psInsertHistory.setString(1, Util.getEmpCode());
                    psInsertHistory.setString(2, Util.getUserStateCode());
                    psInsertHistory.setInt(3, dlr.getOff_cd());
                    psInsertHistory.setString(4, dlr.getDealer_cd());
                    psInsertHistory.executeUpdate();

                    String deleteSql = " delete from " + TableList.VP_DEALER_STATUS + " where state_cd = ? and off_cd = ? and dealer_cd = ? ";
                    psDelete = tmgr.prepareStatement(deleteSql);
                    psDelete.setString(1, Util.getUserStateCode());
                    psDelete.setInt(2, dlr.getOff_cd());
                    psDelete.setString(3, dlr.getDealer_cd());
                    psDelete.executeUpdate();
                    count++;
                }
            }
            if (count > 0) {
                tmgr.commit();
            }
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
        return count;
    }
}
