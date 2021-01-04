/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.TaxExemptiondobj;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Kartikey Singh
 */
public class ExemptionFeeFineImplementation {

    private static Logger LOGGER = Logger.getLogger(ExemptionFeeFineImplementation.class);

    public List<TaxExemptiondobj> getExemptionDetails(String applNo) throws VahanException {
        String query;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        List<TaxExemptiondobj> exemptionDtlsList = new ArrayList<>();
        try {
            tmgr = new TransactionManager("saveExemptionOrderDetails");
            query = "SELECT a.*,b.descr from " + TableList.VA_FEE_EXEMTION + " a inner join " + TableList.TM_PURPOSE_MAST + " b "
                    + "on a.pur_cd = b.pur_cd where appl_no = ? and state_cd=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, applNo);
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                TaxExemptiondobj obj = new TaxExemptiondobj();
                obj.setAppl_no(applNo);
                obj.setState_cd(rs.getString("state_cd"));
                obj.setOff_cd(rs.getInt("off_cd"));
                obj.setPermissionNo(rs.getString("order_no"));
                obj.setPermissionDt(rs.getDate("order_dt"));
                obj.setPerpose(rs.getString("exemtion_reason"));
                obj.setPur_cd(rs.getInt("pur_cd"));
                obj.setExemAmount(rs.getLong("fine_exempted"));
                obj.setExemHead(rs.getString("descr"));
                exemptionDtlsList.add(obj);
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
        return exemptionDtlsList;
    }

    /**
     * @author Kartikey Singh
     */
    public List<TaxExemptiondobj> getExemptionDetails(String applNo, String stateCode) throws VahanException {
        String query;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        List<TaxExemptiondobj> exemptionDtlsList = new ArrayList<>();
        try {
            tmgr = new TransactionManager("saveExemptionOrderDetails");
            query = "SELECT a.*,b.descr from " + TableList.VA_FEE_EXEMTION + " a inner join " + TableList.TM_PURPOSE_MAST + " b "
                    + "on a.pur_cd = b.pur_cd where appl_no = ? and state_cd=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, applNo);
            ps.setString(2, stateCode);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                TaxExemptiondobj obj = new TaxExemptiondobj();
                obj.setAppl_no(applNo);
                obj.setState_cd(rs.getString("state_cd"));
                obj.setOff_cd(rs.getInt("off_cd"));
                obj.setPermissionNo(rs.getString("order_no"));
                obj.setPermissionDt(rs.getDate("order_dt"));
                obj.setPerpose(rs.getString("exemtion_reason"));
                obj.setPur_cd(rs.getInt("pur_cd"));
                obj.setExemAmount(rs.getLong("fine_exempted"));
                obj.setExemHead(rs.getString("descr"));
                exemptionDtlsList.add(obj);
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
        return exemptionDtlsList;
    }

    public boolean saveExemptionOrderDetails(String applNo, List<TaxExemptiondobj> taxExemList, String stateCode, int offCode) throws VahanException {
        TransactionManager tmgr = null;
        boolean status = false;
        try {
            tmgr = new TransactionManager("saveExemptionOrderDetails");
            status = this.saveExemptionOrderDetails(tmgr, applNo, taxExemList, stateCode, offCode);
        } catch (VahanException e) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", e.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", e.getMessage()));
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

    public boolean saveExemptionOrderDetails(TransactionManager tmgr, String applNo, List<TaxExemptiondobj> taxExemList, String stateCode, int offCode) throws VahanException {
        String query;
        PreparedStatement ps;
        boolean status = false;
        try {
            if (taxExemList != null && !taxExemList.isEmpty()) {
                query = "INSERT INTO " + TableList.VA_FEE_EXEMTION + "("
                        + "            state_cd, off_cd, appl_no, pur_cd, fee_exempted, fine_exempted, "
                        + "            order_no, order_dt, exemtion_reason)"
                        + "    VALUES (?, ?, ?, ?, ?, ?,"
                        + "            ?, ?, ?)";
                ps = tmgr.prepareStatement(query);
                for (TaxExemptiondobj obj : taxExemList) {
                    if (obj.getExemAmount() > 0) {
                        int i = 1;
                        ps.setString(i++, stateCode);
                        ps.setInt(i++, offCode);
                        ps.setString(i++, applNo);
                        ps.setInt(i++, obj.getPur_cd());
                        ps.setLong(i++, 0l);
                        ps.setLong(i++, obj.getExemAmount());
                        ps.setString(i++, obj.getPermissionNo());
                        ps.setDate(i++, new java.sql.Date(obj.getPermissionDt().getTime()));
                        ps.setString(i++, obj.getPerpose());
                        ps.addBatch();
                    }
                }
                ps.executeBatch();
                tmgr.commit();
                status = true;
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Record has not been inserted in database. Please Contact to the System Administrator.");
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

    public boolean saveExemptionFeeFineDetails(TransactionManager tmgr, String applNo, List<TaxExemptiondobj> taxExemList, String stateCode, int offCode) throws VahanException {
        String query;
        PreparedStatement ps;
        boolean status = false;
        try {
            if (taxExemList != null && !taxExemList.isEmpty()) {
                query = "INSERT INTO " + TableList.VA_FEE_EXEMTION + "("
                        + "            state_cd, off_cd, appl_no, pur_cd, fee_exempted, fine_exempted, "
                        + "            order_no, order_dt, exemtion_reason)"
                        + "    VALUES (?, ?, ?, ?, ?, ?,"
                        + "            ?, ?, ?)";
                ps = tmgr.prepareStatement(query);
                for (TaxExemptiondobj obj : taxExemList) {
                    if (obj.getExemAmount() > 0 || obj.getExemFeeAmount() > 0) {
                        int i = 1;
                        ps.setString(i++, stateCode);
                        ps.setInt(i++, offCode);
                        ps.setString(i++, applNo);
                        ps.setInt(i++, obj.getPur_cd());
                        ps.setLong(i++, obj.getExemFeeAmount());
                        ps.setLong(i++, obj.getExemAmount());
                        ps.setString(i++, obj.getPermissionNo());
                        ps.setDate(i++, new java.sql.Date(obj.getPermissionDt().getTime()));
                        ps.setString(i++, obj.getPerpose());
                        if (ps.executeUpdate() > 0) {
                            status = true;
                        }
                    }
                }

            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Record has not been inserted in database. Please Contact to the System Administrator.");
        }
        return status;
    }

    public void deleteExemptionOrderDetails(TransactionManager tmgr, String applNo, String stateCode) throws VahanException {
        PreparedStatement ps;
        try {
            String query = "DELETE FROM " + TableList.VA_FEE_EXEMTION + " WHERE STATE_CD =? AND APPL_NO =?";
            ps = tmgr.prepareStatement(query);
            int i = 1;
            ps.setString(i++, stateCode);
            ps.setString(i++, applNo);
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Somthing went wrong. Please Contact to the System Administrator.");
        }

    }

    public boolean updateExemptionOrderDetails(String applNo, List<TaxExemptiondobj> taxExemList, String stateCode, int offCode, String movedBy) throws VahanException {
        TransactionManager tmgr = null;
        boolean status = false;
        try {
            tmgr = new TransactionManager("updateExemptionOrderDetails");
            moveDataIntoHistory(tmgr, applNo, movedBy);
            deleteExemptionOrderDetails(tmgr, applNo, stateCode);
            status = saveExemptionOrderDetails(tmgr, applNo, taxExemList, stateCode, offCode);
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (VahanException ve) {
            throw ve;
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
        return status;
    }

    public boolean isExemptionDetailsExist(String applNo) {
        String query;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        boolean status = false;
        try {
            tmgr = new TransactionManager("isExemptionDetailsExist");
            query = "SELECT appl_no from " + TableList.VA_FEE_EXEMTION + " where appl_no = ? limit 1";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, applNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                status = true;
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
        return status;
    }

    public void moveDataIntoHistory(TransactionManager tmgr, String applNo, String movedBy) throws SQLException {
        PreparedStatement ps;
        String query = "INSERT INTO " + TableList.VHA_FEE_EXEMTION + "("
                + "            moved_on, moved_by, state_cd, off_cd, appl_no, pur_cd, fee_exempted, "
                + "            fine_exempted, order_no, order_dt, exemtion_reason)"
                + "   SELECT current_timestamp,?,*"
                + "  FROM " + TableList.VA_FEE_EXEMTION + " WHERE appl_no = ?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, movedBy);
        ps.setString(2, applNo);
        ps.executeUpdate();

    }

    public List<TaxExemptiondobj> getExemptedDetails(String rcptNo) {
        String query;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        List<TaxExemptiondobj> exemptionDtlsList = new ArrayList<>();
        TaxExemptiondobj obj = null;
        try {
            tmgr = new TransactionManager("saveExemptionOrderDetails");
            query = "SELECT a.*,b.descr from " + TableList.VT_FEE_EXEMTION + " a inner join " + TableList.TM_PURPOSE_MAST + " b "
                    + "on a.pur_cd = b.pur_cd where rcpt_no = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, rcptNo);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                obj = new TaxExemptiondobj();
                obj.setAppl_no(rcptNo);
                obj.setState_cd(rs.getString("state_cd"));
                obj.setOff_cd(rs.getInt("off_cd"));
                obj.setPermissionNo(rs.getString("order_no"));
                obj.setPermissionDt(rs.getDate("order_dt"));
                obj.setPerpose(rs.getString("exemtion_reason"));
                obj.setPur_cd(rs.getInt("pur_cd"));
                obj.setExemAmount(rs.getLong("fine_exempted"));
                obj.setExemHead(rs.getString("descr"));
                exemptionDtlsList.add(obj);
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
        return exemptionDtlsList;
    }

    public boolean isFineExemptionDetailsExist(String applNo) {
        String query;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        boolean status = false;
        try {
            tmgr = new TransactionManager("isFineExemptionDetailsExist");
            query = "SELECT appl_no from " + TableList.VA_FEE_EXEMTION + " where appl_no = ? and pur_cd = ? limit 1";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, applNo);
            ps.setInt(2, TableConstants.FEE_FINE_EXEMTION);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                status = true;
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
        return status;
    }
}
