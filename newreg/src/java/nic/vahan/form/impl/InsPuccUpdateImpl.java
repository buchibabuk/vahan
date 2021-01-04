/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.PuccDobj;
import nic.vahan.form.dobj.pucc.TmConfigurationPUCCdobj;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC103
 */
public class InsPuccUpdateImpl {

    private static final Logger LOGGER = Logger.getLogger(InsPuccUpdateImpl.class);

    public PuccDobj getPuccDetails(String regnNo) throws VahanException {
        PuccDobj dobj = null;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        String insQuery = "SELECT * FROM " + TableList.VT_PUCC + " where regn_no = ? order by op_dt desc limit 1";
        try {
            tmgr = new TransactionManagerReadOnly("getPuccDetails");
            ps = tmgr.prepareStatement(insQuery);
            ps.setString(1, regnNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new PuccDobj();
                dobj.setPuccFrom(rs.getDate("pucc_from"));
                dobj.setPuccUpto(rs.getDate("pucc_upto"));
                dobj.setPuccCentreno(rs.getString("pucc_centreno"));
                dobj.setPuccNo(rs.getString("pucc_no"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong during fetching details of PUCC, Please contact to the System Administrator.");
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong during fetching details of PUCC, Please contact to the System Administrator.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            }
        }
        return dobj;
    }

    public String saveInsPuccDetails(InsDobj insDobj, PuccDobj puccDobj, String regnNo, Owner_dobj ownerDobj, OwnerIdentificationDobj ownerIdentity, boolean renderOwnerIdDetails, String stateCd, int offCd, String empCd) throws VahanException {
        TransactionManager tmgr = null;
        String message = "";
        boolean insFlag = false;
        boolean puccFlag = false;
        try {

            if (regnNo.equalsIgnoreCase("NEW") || regnNo.equalsIgnoreCase("TEMPREG")) {
                throw new VahanException("Invalid Registration No.");
            }

            tmgr = new TransactionManager("saveInsPuccDetails");

            if (ownerIdentity != null && renderOwnerIdDetails) {
                if (ownerIdentity.getRegn_no() == null) {
                    ownerIdentity.setRegn_no(regnNo);
                    ownerIdentity.setState_cd(stateCd);
                    ownerIdentity.setOff_cd(offCd);
                    OwnerIdentificationImpl.insertIntoOwnerIdentificationVT(tmgr, ownerIdentity);
                } else {
                    ownerIdentity.setState_cd(stateCd);
                    ownerIdentity.setOff_cd(offCd);
                    OwnerIdentificationImpl.insertIntoOwnerIdentificationHistoryVH(tmgr, regnNo, stateCd, offCd);
                    OwnerIdentificationImpl.updateOwnerIdentificationVT(tmgr, ownerIdentity);
                }
            }

            if (insDobj != null) {
                InsImpl insImpl = new InsImpl();
                OwnerDetailsDobj dobj = new OwnerDetailsDobj();
                dobj.setInsDobj(insDobj);
                dobj.setBlackListedVehicleDobj(ownerDobj.getBlackListedVehicleDobj());
                insImpl.validateInsuranceForBlackListedVehicle(dobj);
                String regnNum = InsImpl.checkPolicyNoUniqueness(insDobj.getPolicy_no(), insDobj.getComp_cd());
                if (CommonUtils.isNullOrBlank(regnNum) || insDobj.isIibData()) {
                    if (InsImpl.validateOwnerCodeWithInsType(ownerDobj.getOwner_cd(), insDobj.getIns_type())) {
                        insDobj.setState_cd(stateCd);
                        insDobj.setOff_cd(offCd);
                        insFlag = saveInsDetails(tmgr, insDobj, regnNo, Util.getEmpCode());
                    } else {
                        throw new VahanException("Invalid Combination of Ownership Type & Insurance Type.");
                    }
                } else {
                    throw new VahanException("Duplicate policy number Against Vehicle " + regnNum + ".");
                }
            }

            if (puccDobj != null) {
                puccDobj.setRegnNo(regnNo);
                puccDobj.setState_cd(stateCd);
                puccDobj.setOff_cd(offCd);
                if (puccDobj.getPuccFrom() != null && puccDobj.getPuccUpto() != null
                        && (DateUtils.compareDates(puccDobj.getPuccFrom(), puccDobj.getPuccUpto()) == 0
                        || DateUtils.compareDates(puccDobj.getPuccFrom(), puccDobj.getPuccUpto()) == 2)) {
                    throw new VahanException("PUCC From Date must be less than PUCC Upto Date.");
                } else if ((puccDobj.getPuccFrom() == null || puccDobj.getPuccUpto() == null)) {
                    throw new VahanException("PUCC From / Upto Date can not be empty.");
                }
                if (!CommonUtils.isNullOrBlank(puccDobj.getPuccNo()) && (!"NA".equalsIgnoreCase(puccDobj.getPuccNo()))) {
                    String regnno = checkPUCCNoUniqueness(puccDobj.getPuccNo(), puccDobj.getPuccCentreno());
                    if (!nic.java.util.CommonUtils.isNullOrBlank(regnno)) {
                        puccDobj.setPuccNo("");
                        throw new VahanException("Duplicate PUCC number Against Vehicle " + regnno + ".");
                    }
                }
                puccFlag = savePuccDetails(tmgr, puccDobj, empCd);

            }
            if (insFlag || puccFlag || renderOwnerIdDetails) {
                message = "Information is Updated Successfully.";
                tmgr.commit();
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
            }
        }
        return message;
    }

    private boolean saveInsDetails(TransactionManager tmgr, InsDobj insDobj, String regnNo, String empCd) throws VahanException {

        try {

            if (regnNo.equalsIgnoreCase("NEW") || regnNo.equalsIgnoreCase("TEMPREG")) {
                throw new VahanException("Invalid Registration No.");
            }

            PreparedStatement ps;
            String sql = "INSERT into " + TableList.VH_INSURANCE + " "
                    + "Select regn_no ,  comp_cd ,  ins_type ,  ins_from ,"
                    + " ins_upto ,  policy_no , current_timestamp as moved_on, ? as moved_by, state_cd, off_cd, idv"
                    + " FROM " + TableList.VT_INSURANCE + " where regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCd);
            ps.setString(2, regnNo);
            ps.executeUpdate();

            sql = "Delete from " + TableList.VT_INSURANCE + " where regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.executeUpdate();

            sql = "INSERT INTO " + TableList.VT_INSURANCE + "(\n"
                    + "   state_cd, off_cd ,regn_no, comp_cd, ins_type, ins_from, ins_upto, policy_no,idv,op_dt)\n"
                    + "    VALUES (?,?,?, ?, ?, ?, ?, ?,?,current_timestamp)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, insDobj.getState_cd());
            ps.setInt(2, insDobj.getOff_cd());
            ps.setString(3, regnNo);
            ps.setInt(4, insDobj.getComp_cd());
            ps.setInt(5, insDobj.getIns_type());
            if (insDobj.getIns_from() != null) {
                ps.setDate(6, new java.sql.Date(insDobj.getIns_from().getTime()));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }
            if (insDobj.getIns_upto() != null) {
                ps.setDate(7, new java.sql.Date(insDobj.getIns_upto().getTime()));
            } else {
                ps.setNull(7, java.sql.Types.DATE);
            }
            ps.setString(8, insDobj.getPolicy_no());
            ps.setLong(9, insDobj.getIdv());
            ps.executeUpdate();

            sql = "INSERT INTO " + TableList.VHA_INSURANCE + ""
                    + "     SELECT current_timestamp as moved_on,? as moved_by, state_cd, off_cd, appl_no, regn_no, comp_cd, ins_type, ins_from, "
                    + " ins_upto, policy_no, idv, op_dt"
                    + " FROM " + TableList.VA_INSURANCE + " where regn_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCd);
            ps.setString(2, regnNo);
            ps.executeUpdate();

            sql = "Delete from " + TableList.VA_INSURANCE + " where regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.executeUpdate();

            return true;

        } catch (VahanException vex) {
            throw vex;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    private boolean savePuccDetails(TransactionManager tmgr, PuccDobj puccDobj, String empCd) throws Exception {
        PreparedStatement ps;
        String sql = "INSERT INTO " + TableList.VH_PUCC
                + " SELECT  state_cd,  off_cd, regn_no, pucc_from, pucc_upto, pucc_centreno,"
                + "       pucc_no, op_dt, ? as appl_no,current_timestamp as moved_on, ? as moved_by "
                + "  FROM " + TableList.VT_PUCC + " WHERE regn_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, puccDobj.getRegnNo());
        ps.setString(2, empCd);
        ps.setString(3, puccDobj.getRegnNo());
        ps.executeUpdate();

        sql = "Delete from " + TableList.VT_PUCC + " WHERE regn_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, puccDobj.getRegnNo());
        ps.executeUpdate();

        sql = "INSERT INTO " + TableList.VT_PUCC + "(\n"
                + " state_cd, off_cd,regn_no, pucc_from, pucc_upto, pucc_centreno, pucc_no, op_dt)\n"
                + " VALUES (?,?,?, ?, ?, ?, ?, current_timestamp)";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, puccDobj.getState_cd());
        ps.setInt(2, puccDobj.getOff_cd());
        ps.setString(3, puccDobj.getRegnNo());
        ps.setDate(4, new java.sql.Date(puccDobj.getPuccFrom().getTime()));
        ps.setDate(5, new java.sql.Date(puccDobj.getPuccUpto().getTime()));
        ps.setString(6, puccDobj.getPuccCentreno());
        ps.setString(7, puccDobj.getPuccNo());
        ps.executeUpdate();

        return true;
    }

    public TmConfigurationPUCCdobj getTmConfigurationPUCCdobj(String stateCd) throws VahanException {
        TmConfigurationPUCCdobj configPuccDobj = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            String sql = "SELECT * FROM " + TableList.TM_CONFIGURATION_PUCC + " where state_cd = ? ";
            tmgr = new TransactionManagerReadOnly("getTmConfigurationPUCCdobj");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                configPuccDobj = new TmConfigurationPUCCdobj();
                configPuccDobj.setState_cd((rs.getString("state_cd")));
                configPuccDobj.setExpired_pucc_check((rs.getString("expired_pucc_check")));
                configPuccDobj.setPucc_service((rs.getBoolean("is_pucc_service")));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " getTmConfigurationPUCCdobj " + e.getStackTrace()[0]);
            throw new VahanException("Problem in fetching Configuration Details of PUCC, Please Contact to the System Administrator.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return configPuccDobj;
    }

    public String checkPUCCNoUniqueness(String pucc_no, String pucc_centreno) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        String regnNo = null;
        TransactionManagerReadOnly tmgr = null;
        RowSet rs = null;
        try {
            sql = "select * from " + TableList.VA_PUCC + " where  pucc_centreno=? and pucc_no=?";
            tmgr = new TransactionManagerReadOnly("checkPUCCNoUniqueness");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, pucc_no);
            ps.setString(2, pucc_centreno);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                regnNo = rs.getString("regn_no");
            } else {
                sql = "select * from " + TableList.VT_PUCC + " where  pucc_centreno=? and pucc_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, pucc_centreno);
                ps.setString(2, pucc_no);
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    regnNo = rs.getString("regn_no");
                }
            }
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + "-" + sqle.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            }
        }
        return regnNo;
    }
}
