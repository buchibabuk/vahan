package nic.vahan.form.impl.tradecert;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.tradecert.InitializeTradeCertDobj;
import java.util.Arrays;
import nic.vahan.form.impl.Util;

import org.apache.log4j.Logger;

public class InitializeTradeCertImpl {

    private static final Logger LOGGER = Logger.getLogger(InitializeTradeCertImpl.class);

    /*
     * Method to insert Trade Certificate Series
     */
    public static boolean saveTCInitializationNo(InitializeTradeCertDobj dobj) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement psmt = null;
        Boolean isSaved = false;
        try {
            String sql = "INSERT INTO " + TableList.VSM_TC_NO + "(state_cd, off_cd, tc_starting_date, module_prefix, sequence_no, applicant_type, entered_by)"
                    + " VALUES(?, ?,CURRENT_TIMESTAMP, ?, ?, ?, ?)";
            tmgr = new TransactionManager("saveTCInitializationNo");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, Util.getUserStateCode());
            psmt.setInt(2, Util.getUserOffCode());
            psmt.setString(3, dobj.getModulePrefix());
            psmt.setInt(4, dobj.getSequenceNo());
            psmt.setString(5, dobj.getApplicantType());
            psmt.setString(6, Util.getEmpCode());
            int value = psmt.executeUpdate();
            if (value > 0) {
                isSaved = true;
                tmgr.commit();
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "  " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "  " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return isSaved;
    }

    /*
     * Method to get Applicant Types
     */
    public static void fillApplicantTypeMap(Map applicantTypeMap) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement psmt = null;
        RowSet rs;
        try {
            String sql = "select * from " + TableList.VM_TC_APPLICANT_TYPE + "";
            tmgr = new TransactionManager("fillApplicantTypeMap");
            psmt = tmgr.prepareStatement(sql);
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                applicantTypeMap.put(rs.getString("code"), rs.getString("descr"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "  " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "  " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    /*
     * Method to get Required Applicants for a particular state 
     */
    public static void fillRequiredApplicantsForState(List applicantListForState) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement psmt = null;
        RowSet rs;
        String[] requiredApplicantsArray;
        try {
            String sql = "select applicant_required from " + TableList.TM_CONFIGURATION_TRADE_CERT + " where state_cd=?";
            tmgr = new TransactionManager("fillRequiredApplicantsForState");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                String requiredApplicants = rs.getString("applicant_required");
                requiredApplicantsArray = requiredApplicants.split(",");
                applicantListForState.addAll(Arrays.asList(requiredApplicantsArray));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "  " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "  " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    /*
     * Method to check if Trade Certificate Series already exists
     */
    public boolean checkIfTCSeriesExist(InitializeTradeCertDobj dobj) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement psmt = null;
        RowSet rs;
        try {
            String sql = "select * from " + TableList.VSM_TC_NO + " where state_cd=? and off_cd=? and applicant_type=?";
            tmgr = new TransactionManager("checkIfTCSeriesExist");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, Util.getUserStateCode());
            psmt.setInt(2, Util.getUserOffCode());
            psmt.setString(3, dobj.getApplicantType());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj.setModulePrefix(rs.getString("module_prefix"));
                dobj.setSequenceNo(rs.getInt("sequence_no"));
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + "  " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "  " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }
}
