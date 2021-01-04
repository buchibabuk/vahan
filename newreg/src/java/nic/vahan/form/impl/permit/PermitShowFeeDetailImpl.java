/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.java.util.DateUtilsException;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.BlackListedVehicleDobj;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.configuration.TmConfigurationReceipts;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitFeeRequiredDataDobj;
import nic.vahan.form.dobj.permit.PermitShowFeeDetailDobj;
import nic.vahan.form.impl.BlackListedVehicleImpl;
import nic.vahan.form.impl.Util;
import static nic.vahan.form.impl.fancy.AdvanceRegnFeeImpl.replaceTagValues;
import static nic.vahan.form.impl.permit.CommonPermitPrintImpl.getHowManyRegionContain;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author Naman Jain
 */
public class PermitShowFeeDetailImpl {

    private static final Logger LOGGER = Logger.getLogger(PermitShowFeeDetailImpl.class);

    /**
     * @param State_Code = ParaMeter 0
     * @param pur_cd = ParaMeter 1
     * @param pmt_catg = ParaMeter 2
     * @param pmt_type = ParaMeter 3
     * @param period_mode = ParaMeter 4
     * @param period = ParaMeter 5
     * @param region_covered = ParaMeter 6
     * @param vh_class = ParaMeter 7
     * @param seat_cap = ParaMeter 8
     * @param unld_wt = ParaMeter 9
     * @param ld_wt = ParaMeter 10
     * @param regn_no = ParaMeter 11
     * @param appl_no = ParaMeter 12
     */
    public List<PermitShowFeeDetailDobj> getPermitFeeDetails(PermitFeeRequiredDataDobj requiredDataDobj, PermitShowFeeDetailDobj dobj, List<PermitShowFeeDetailDobj> feeShowDetail, String applNo) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getPermitFeeDetails");
            String whereClause = CommonPermitPrintImpl.getWhereClause(tmgr, requiredDataDobj.getState_cd(), requiredDataDobj.getPur_cd(),
                    requiredDataDobj.getPmt_type(), requiredDataDobj.getVh_class(), requiredDataDobj.getSeat_cap(), requiredDataDobj.getUnld_wt(), 0, applNo, requiredDataDobj.getPmt_catg());
            if (whereClause != null) {
                String sqlString = "SELECT fee, fine FROM " + TableList.VM_PERMIT_FEE + " " + whereClause;
                ps = tmgr.prepareStatement(sqlString);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    dobj.setPermitAmt(String.valueOf(rs.getInt("fee")));
                    dobj.setPenalty(String.valueOf(rs.getInt("fine")));
                    dobj.setPermitHeadDisable(true);
                    dobj.setDisableMinusBt(true);
                    feeShowDetail.add(new PermitShowFeeDetailDobj(dobj));
                } else {
                    requiredDataDobj.setAppl_no(applNo);
                    getPmtFeeDetailsFromSlab(requiredDataDobj, null, null, tmgr, dobj, feeShowDetail, true);
                }
            } else {
                throw new VahanException("Some problem in calculating the fees. Please contact the system administrator.");
            }
        } catch (VahanException e) {
            throw e;
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
        return feeShowDetail;
    }

    public List<PermitShowFeeDetailDobj> getPermitFeeDetailsOnlyPurCdANDStateCd(PermitFeeRequiredDataDobj requiredDataDobj, PermitShowFeeDetailDobj dobj, List<PermitShowFeeDetailDobj> feeShowDetail) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("Get Fee Details");
            String whereClause = CommonPermitPrintImpl.getWhereClause(tmgr, requiredDataDobj.getState_cd(), requiredDataDobj.getPur_cd(),
                    requiredDataDobj.getPmt_type(), requiredDataDobj.getVh_class(), 0, 0, 0, null, requiredDataDobj.getPmt_catg());
            if (whereClause != null) {
                String sqlString = "SELECT fee, fine FROM " + TableList.VM_PERMIT_FEE + " " + whereClause;
                ps = tmgr.prepareStatement(sqlString);
                RowSet rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    dobj.setPermitAmt(String.valueOf(rs.getInt("fee")));
                    dobj.setPenalty(String.valueOf(rs.getInt("fine")));
                    dobj.setPermitHeadDisable(true);
                    dobj.setDisableMinusBt(true);
                    feeShowDetail.add(new PermitShowFeeDetailDobj(dobj));
                } else {
                    getPmtFeeDetailsFromSlab(requiredDataDobj, null, null, tmgr, dobj, feeShowDetail, true);
                }
            } else {
                throw new VahanException("Some problem in calculating the fees. Please contact the system administrator.");
            }
        } catch (VahanException e) {
            throw e;
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
        return feeShowDetail;
    }

    public List<PermitShowFeeDetailDobj> getFeeforTempSpecialPermit(PermitFeeRequiredDataDobj requiredDataDobj, PermitShowFeeDetailDobj dobj, Date validFrom, Date validUpto, List<PermitShowFeeDetailDobj> feeShowDetail) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("Get Fee Details");
            String whereClause = CommonPermitPrintImpl.getWhereClause(tmgr, requiredDataDobj.getState_cd(), requiredDataDobj.getPur_cd(),
                    0, 0, 0, 0, 0, null, requiredDataDobj.getPmt_catg());
            if (whereClause != null) {
                String sqlString = "SELECT fee, fine FROM " + TableList.VM_PERMIT_FEE + " " + whereClause;
                ps = tmgr.prepareStatement(sqlString);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    dobj.setPermitAmt(String.valueOf(rs.getInt("fee")));
                    dobj.setPenalty(String.valueOf(rs.getInt("fine")));
                    dobj.setPermitHeadDisable(true);
                    dobj.setDisableMinusBt(true);
                    feeShowDetail.add(new PermitShowFeeDetailDobj(dobj));
                } else {
                    getPmtFeeDetailsFromSlab(requiredDataDobj, validFrom, validUpto, tmgr, dobj, feeShowDetail, true);
                }
            } else {
                throw new VahanException("Some problem in calculating the fees. Please contact the system administrator.");
            }
        } catch (VahanException e) {
            throw e;
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
        return feeShowDetail;
    }

    /**
     * @param State_Code = ParaMeter 0
     * @param pur_cd = ParaMeter 1
     * @param pmt_catg = ParaMeter 2
     * @param pmt_type = ParaMeter 3
     * @param period_mode = ParaMeter 4
     * @param period = ParaMeter 5
     * @param region_covered = ParaMeter 6
     * @param vh_class = ParaMeter 7
     * @param seat_cap = ParaMeter 8
     * @param unld_wt = ParaMeter 9
     * @param regn_no = ParaMeter 10
     * @param appl_no = ParaMeter 11
     * @param stand_cap = ParaMeter 13
     * @param sleeper_cap = ParaMeter 14
     */
    public PermitFeeRequiredDataDobj getVaPermitDetailsWithVtOwnerDetail(String applNo, int pur_cd, String regn_no) {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        PermitFeeRequiredDataDobj requiredDataDobj = null;
        String sqlString;
        try {
            tmgr = new TransactionManager("Get Fee Details");

            if (regn_no.equalsIgnoreCase("NEW")) {
                sqlString = "Select pmt.regn_no,pmt.state_cd,pmt.period_mode,pmt.period,pmt.pur_cd,pmt.pmt_catg,pmt.pmt_type,pmt.region_covered,COALESCE(pmt.service_type, 0::numeric) AS service_type,own.vh_class,own.seat_cap,'0' AS stand_cap,'0' sleeper_cap,own.unld_wt,own.ld_wt,own.owner_ctg,own.fuel,'0' AS owner_cd from " + TableList.VA_PERMIT + " pmt\n"
                        + " inner join " + TableList.VA_PERMIT_OWNER + " own on  own.appl_no = pmt.appl_no\n"
                        + " Where pmt.appl_no = ?";
            } else {
                sqlString = "Select pmt.regn_no,pmt.state_cd,pmt.period_mode,pmt.period,pmt.pur_cd,pmt.pmt_catg,pmt.pmt_type,pmt.region_covered,COALESCE(pmt.service_type, 0::numeric) AS service_type,own.vh_class,own.seat_cap,own.other_criteria,COALESCE(own.stand_cap, '0') AS stand_cap,COALESCE(own.sleeper_cap, '0') AS sleeper_cap,own.unld_wt,own.ld_wt,COALESCE(ownide.owner_ctg, 0::numeric) as owner_ctg,own.fuel,own.owner_cd,vtpmt.valid_upto,vtauth.auth_to from " + TableList.VA_PERMIT + " pmt\n"
                        + " inner join " + TableList.VT_OWNER + " own on  own.regn_no = pmt.regn_no\n"
                        + " left outer join " + TableList.VT_OWNER_IDENTIFICATION + " ownide on ownide.regn_no = pmt.regn_no\n"
                        + " left outer join " + TableList.VT_PERMIT + " vtpmt on pmt.state_cd = vtpmt.state_cd and pmt.regn_no = vtpmt.regn_no\n"
                        + " left outer join " + TableList.VT_PERMIT_HOME_AUTH + " vtauth on pmt.regn_no = vtauth.regn_no and vtpmt.pmt_no = vtauth.pmt_no \n"
                        + " Where pmt.appl_no = ?";
            }

            ps = tmgr.prepareStatement(sqlString);
            int i = 1;
            ps.setString(i++, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                requiredDataDobj = new PermitFeeRequiredDataDobj();
                requiredDataDobj.setState_cd(rs.getString("state_cd"));
                requiredDataDobj.setPur_cd(pur_cd);
                requiredDataDobj.setPmt_catg(rs.getInt("pmt_catg"));
                requiredDataDobj.setPmt_type(rs.getInt("pmt_type"));
                requiredDataDobj.setPeriod_mode(rs.getString("period_mode"));
                requiredDataDobj.setPeriod(rs.getInt("period"));
                requiredDataDobj.setRegion_covered("region_covered");
                requiredDataDobj.setVh_class(rs.getInt("vh_class"));
                requiredDataDobj.setSeat_cap(rs.getInt("seat_cap"));
                requiredDataDobj.setUnld_wt(rs.getInt("unld_wt"));
                requiredDataDobj.setLd_wt(rs.getInt("ld_wt"));
                requiredDataDobj.setStand_cap(rs.getInt("stand_cap"));
                requiredDataDobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                requiredDataDobj.setOwn_catg(rs.getInt("owner_ctg"));
                requiredDataDobj.setRegn_no(rs.getString("regn_no").toUpperCase());
                requiredDataDobj.setFuel_type(rs.getInt("fuel"));
                requiredDataDobj.setOwner_cd(rs.getInt("owner_cd"));
                requiredDataDobj.setService_type(rs.getInt("service_type"));
                if (pur_cd == TableConstants.VM_PMT_RENEWAL_PUR_CD) {
                    requiredDataDobj.setValidUpToPrvDate(DateUtils.getDateInDDMMYYYY(rs.getDate("valid_upto")));
                    if (rs.getDate("auth_to") != null) {
                        requiredDataDobj.setValidAuthUpToDate(DateUtils.getDateInDDMMYYYY(rs.getDate("auth_to")));
                    }
                }
                if (!regn_no.equalsIgnoreCase("NEW")) {
                    requiredDataDobj.setOther_criteria(rs.getInt("other_criteria"));
                }
                if (rs.getInt("pmt_type") == TableConstants.STAGE_CARRIAGE_PERMIT) {
                    sqlString = "select sum(b.rlength) as rlength from " + TableList.va_permit_route + " a inner join " + TableList.VM_ROUTE_MASTER + " b on a.state_cd=b.state_cd and a.off_cd=b.off_cd and a.route_cd=b.code where a.state_cd=? and a.appl_no=? group by a.appl_no";
                    ps = tmgr.prepareStatement(sqlString);
                    ps.setString(1, rs.getString("state_cd"));
                    ps.setString(2, applNo);
                    RowSet rs1 = tmgr.fetchDetachedRowSet();
                    int rLength = 0;
                    if (rs1.next()) {
                        rLength = rs1.getInt("rlength");
                    }
                    requiredDataDobj.setRoute_length(rLength);
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
        return requiredDataDobj;
    }

    public PermitFeeRequiredDataDobj getVaPermitDetailsAndVtOwnerDetail(String applNo, int pur_cd, String regn_no) {
        PreparedStatement ps;
        TransactionManagerReadOnly tmgr = null;
        PermitFeeRequiredDataDobj requiredDataDobj = null;
        String sqlString1;
        String sqlString2;
        try {
            tmgr = new TransactionManagerReadOnly("getVaPermitDetailsAndVtOwnerDetail");
            sqlString1 = "Select pmt.regn_no,pmt.state_cd,pmt.period_mode,pmt.period,pmt.pur_cd,pmt.pmt_catg,pmt.pmt_type,pmt.region_covered,COALESCE(pmt.service_type, 0::numeric) AS service_type from " + TableList.VA_PERMIT + " pmt\n"
                    + " Where pmt.appl_no = ?";

            sqlString2 = "Select own.regn_no,own.vh_class,own.seat_cap,COALESCE(own.stand_cap, '0') AS stand_cap,COALESCE(own.sleeper_cap, '0') AS sleeper_cap,own.unld_wt,own.ld_wt,own.fuel,own.other_criteria,COALESCE(ownide.owner_ctg, 0::numeric) as owner_ctg from "
                    + TableList.VT_OWNER + " own \n"
                    + " left outer join " + TableList.VT_OWNER_IDENTIFICATION + " ownide on ownide.regn_no = own.regn_no\n"
                    + " Where own.regn_no = ?";

            ps = tmgr.prepareStatement(sqlString1);
            int i = 1;
            ps.setString(i++, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                requiredDataDobj = new PermitFeeRequiredDataDobj();
                requiredDataDobj.setState_cd(rs.getString("state_cd"));
                requiredDataDobj.setPur_cd(pur_cd);
                requiredDataDobj.setPmt_catg(rs.getInt("pmt_catg"));
                requiredDataDobj.setPmt_type(rs.getInt("pmt_type"));
                requiredDataDobj.setPeriod_mode(rs.getString("period_mode"));
                requiredDataDobj.setPeriod(rs.getInt("period"));
                requiredDataDobj.setRegion_covered(rs.getString("region_covered"));
                requiredDataDobj.setService_type(rs.getInt("service_type"));
            }

            ps = tmgr.prepareStatement(sqlString2);
            i = 1;
            ps.setString(i++, regn_no);
            RowSet rs1 = tmgr.fetchDetachedRowSet();
            if (rs1.next() && requiredDataDobj != null) {
                requiredDataDobj.setVh_class(rs1.getInt("vh_class"));
                requiredDataDobj.setSeat_cap(rs1.getInt("seat_cap"));
                requiredDataDobj.setUnld_wt(rs1.getInt("unld_wt"));
                requiredDataDobj.setLd_wt(rs1.getInt("ld_wt"));
                requiredDataDobj.setStand_cap(rs1.getInt("stand_cap"));
                requiredDataDobj.setSleeper_cap(rs1.getInt("sleeper_cap"));
                requiredDataDobj.setOwn_catg(rs1.getInt("owner_ctg"));
                requiredDataDobj.setRegn_no(rs1.getString("regn_no").toUpperCase());
                requiredDataDobj.setFuel_type(rs1.getInt("fuel"));
                requiredDataDobj.setOther_criteria(rs1.getInt("other_criteria"));
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
        return requiredDataDobj;
    }

    public PermitFeeRequiredDataDobj getHomeAuthDetails(String applNo, String regn_no, int pmt_type) {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        PermitFeeRequiredDataDobj requiredDataDobj = null;
        String sqlString;
        RowSet rs;
        try {
            tmgr = new TransactionManager("Get Fee Details");

            sqlString = "Select own.state_cd,pmt.pur_cd,own.vh_class from " + TableList.VA_PERMIT_HOME_AUTH + "  pmt\n"
                    + "inner join " + TableList.VT_OWNER + " own on  own.regn_no = pmt.regn_no\n"
                    + "Where pmt.appl_no = ?";

            ps = tmgr.prepareStatement(sqlString);
            int i = 1;
            ps.setString(i++, applNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                requiredDataDobj = new PermitFeeRequiredDataDobj();
                requiredDataDobj.setState_cd(rs.getString("state_cd"));
                requiredDataDobj.setPmt_type(pmt_type);
                requiredDataDobj.setPeriod_mode("Y");
                requiredDataDobj.setPeriod(1);
                requiredDataDobj.setPur_cd(rs.getInt("pur_cd"));
                requiredDataDobj.setVh_class(rs.getInt("vh_class"));
            } else {
                sqlString = "Select own.state_cd,own.vh_class from " + TableList.VT_OWNER + " own \n"
                        + "Where regn_no = ?";
                ps = tmgr.prepareStatement(sqlString);
                ps.setString(1, regn_no);
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    requiredDataDobj = new PermitFeeRequiredDataDobj();
                    requiredDataDobj.setState_cd(rs.getString("state_cd"));
                    requiredDataDobj.setPmt_type(pmt_type);
                    requiredDataDobj.setPeriod_mode("Y");
                    requiredDataDobj.setPeriod(1);
                    requiredDataDobj.setPur_cd(TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD);
                    requiredDataDobj.setVh_class(rs.getInt("vh_class"));
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
        return requiredDataDobj;
    }

    public PermitFeeRequiredDataDobj getVaTempPermitDetailsWithVtOwnerDetail(String appl_no, int pur_cd) {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        PermitFeeRequiredDataDobj requiredDataDobj = null;
        String sqlString;
        RowSet rs;
        try {
            tmgr = new TransactionManager("Get Fee Details");
            sqlString = "Select COALESCE(vt_pmt.service_type, 0::numeric) as service_type,COALESCE(va_tax.service_type, 0::numeric) as ser_type,pmt.state_cd,pmt.period_mode,pmt.period,pmt.pur_cd,pmt.pmt_catg,pmt.pmt_type,own.vh_class,own.seat_cap,own.fuel,own.other_criteria,COALESCE(own.stand_cap, '0') AS stand_cap,COALESCE(own.sleeper_cap, '0') AS sleeper_cap,own.unld_wt,own.ld_wt,to_char(pmt.valid_from,'DD-MON-YYYY') as valid_from,to_char(pmt.valid_upto,'DD-MON-YYYY') as valid_upto,COALESCE(ownide.owner_ctg, 0::numeric) as owner_ctg, va_tax.route_length \n "
                    + " from " + TableList.VA_TEMP_PERMIT + " pmt \n "
                    + " inner join " + TableList.VT_OWNER + " own on  own.regn_no = pmt.regn_no \n "
                    + " left outer join " + TableList.VT_PERMIT + " vt_pmt on  vt_pmt.state_cd = pmt.state_cd and vt_pmt.regn_no = pmt.regn_no \n "
                    + " left outer join " + TableList.VT_OWNER_IDENTIFICATION + " ownide on ownide.regn_no = pmt.regn_no \n "
                    + " left join " + TableList.VA_TEMPSPL_TAX_BASED_ON_PERMIT + " va_tax on va_tax.regn_no = pmt.regn_no and va_tax.appl_no = pmt.appl_no \n "
                    + " Where pmt.appl_no = ?";
            ps = tmgr.prepareStatement(sqlString);
            int i = 1;
            ps.setString(i++, appl_no);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                requiredDataDobj = new PermitFeeRequiredDataDobj();
                requiredDataDobj.setState_cd(rs.getString("state_cd"));
                requiredDataDobj.setPur_cd(pur_cd);
                requiredDataDobj.setPmt_catg(rs.getInt("pmt_catg"));
                requiredDataDobj.setPmt_type(rs.getInt("pmt_type"));
                requiredDataDobj.setValidFromTemp(rs.getString("valid_from"));
                requiredDataDobj.setValidUptoTemp(rs.getString("valid_upto"));
                requiredDataDobj.setVh_class(rs.getInt("vh_class"));
                requiredDataDobj.setSeat_cap(rs.getInt("seat_cap"));
                requiredDataDobj.setUnld_wt(rs.getInt("unld_wt"));
                requiredDataDobj.setLd_wt(rs.getInt("ld_wt"));
                requiredDataDobj.setStand_cap(rs.getInt("stand_cap"));
                requiredDataDobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                requiredDataDobj.setOwn_catg(rs.getInt("owner_ctg"));
                requiredDataDobj.setService_type(rs.getInt("service_type"));
                if (rs.getInt("service_type") == 0 || rs.getInt("service_type") == -1) {
                    requiredDataDobj.setService_type(rs.getInt("ser_type"));
                }
                requiredDataDobj.setOther_criteria(rs.getInt("other_criteria"));
                if (rs.getInt("other_criteria") == 0 || rs.getInt("other_criteria") == -1) {
                    requiredDataDobj.setOther_criteria(rs.getInt("other_criteria"));
                }
                requiredDataDobj.setRoute_length(rs.getInt("route_length"));
                requiredDataDobj.setFuel_type(rs.getInt("fuel"));
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
        return requiredDataDobj;
    }

    public PermitFeeRequiredDataDobj getRequiredDtlsFromEnd_Vari(String appl_no, int pur_cd) {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        PermitFeeRequiredDataDobj requiredDataDobj = null;
        String sqlString;
        RowSet rs;
        try {
            tmgr = new TransactionManager("Get Fee Details");

            sqlString = "SELECT a.state_cd,a.period_mode,a.period,a.pur_cd,a.pmt_catg,a.pmt_type,c.vh_class,c.seat_cap,COALESCE(c.stand_cap, '0') AS stand_cap,COALESCE(c.sleeper_cap, '0') AS sleeper_cap,c.unld_wt,c.ld_wt,to_char(valid_from,'DD-MON-YYYY') as valid_from,to_char(valid_upto,'DD-MON-YYYY') as valid_upto,COALESCE(ownide.owner_ctg, 0::numeric) as owner_ctg \n"
                    + "from " + TableList.VA_PERMIT + " a\n"
                    + "inner join " + TableList.VH_PERMIT + " b on a.state_cd = b.state_cd and a.regn_no = b.regn_no and b.pmt_status = 'END'\n"
                    + "inner join " + TableList.VT_OWNER + " c on  a.regn_no = c.regn_no and a.state_cd = c.state_cd\n"
                    + " left outer join " + TableList.VT_OWNER_IDENTIFICATION + " ownide on ownide.regn_no = a.regn_no\n"
                    + " where a.appl_no = ? order by moved_on DESC limit 1;";
            ps = tmgr.prepareStatement(sqlString);
            int i = 1;
            ps.setString(i++, appl_no);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                requiredDataDobj = new PermitFeeRequiredDataDobj();
                requiredDataDobj.setState_cd(rs.getString("state_cd"));
                requiredDataDobj.setPur_cd(pur_cd);
                requiredDataDobj.setPmt_catg(rs.getInt("pmt_catg"));
                requiredDataDobj.setPmt_type(rs.getInt("pmt_type"));
                requiredDataDobj.setValidFromTemp(rs.getString("valid_from")); // Temp used for Valid From 
                requiredDataDobj.setValidUptoTemp(rs.getString("valid_upto")); // Temp used for Valid Upto
                requiredDataDobj.setVh_class(rs.getInt("vh_class"));
                requiredDataDobj.setSeat_cap(rs.getInt("seat_cap"));
                requiredDataDobj.setUnld_wt(rs.getInt("unld_wt"));
                requiredDataDobj.setLd_wt(rs.getInt("ld_wt"));
                requiredDataDobj.setStand_cap(rs.getInt("stand_cap"));
                requiredDataDobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                requiredDataDobj.setOwn_catg(rs.getInt("owner_ctg"));
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
        return requiredDataDobj;
    }

    public PermitFeeRequiredDataDobj getVaDupPermitDetailsWithVtOwnerDetail(String regn_no, int pur_cd) {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        PermitFeeRequiredDataDobj requiredDataDobj = null;
        String sqlString;
        RowSet rs;
        try {
            tmgr = new TransactionManager("Get Fee Details");
            sqlString = "Select a.reason, b.state_cd,a.pur_cd,b.vh_class,b.seat_cap,COALESCE(b.stand_cap, '0') AS stand_cap,COALESCE(b.sleeper_cap, '0') AS sleeper_cap,b.unld_wt,b.ld_wt,c.pmt_type,c.pmt_catg,to_char(valid_from,'DD-MON-YYYY') as valid_from,to_char(valid_upto,'DD-MON-YYYY') as valid_upto,COALESCE(ownide.owner_ctg, 0::numeric) as owner_ctg from VA_DUP a\n"
                    + " left join permit.vt_permit c on c.regn_no = a.regn_no\n"
                    + " inner join VT_OWNER b on b.regn_no = a.regn_no and b.state_cd = c.state_cd \n"
                    + " left outer join " + TableList.VT_OWNER_IDENTIFICATION + " ownide on ownide.regn_no = a.regn_no\n"
                    + " Where a.regn_no = ? AND  a.pur_cd = ?";
            ps = tmgr.prepareStatement(sqlString);
            int i = 1;
            ps.setString(i++, regn_no);
            ps.setInt(i++, pur_cd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                requiredDataDobj = new PermitFeeRequiredDataDobj();
                requiredDataDobj.setState_cd(rs.getString("state_cd"));
                requiredDataDobj.setPur_cd(rs.getInt("pur_cd"));
                requiredDataDobj.setPmt_catg(rs.getInt("pmt_catg"));
                requiredDataDobj.setPmt_type(rs.getInt("pmt_type"));
                requiredDataDobj.setValidFromTemp(rs.getString("valid_from"));// Temp used for Valid From 
                requiredDataDobj.setValidUptoTemp(rs.getString("valid_upto"));// Temp used for Valid Upto
                requiredDataDobj.setVh_class(rs.getInt("vh_class"));
                requiredDataDobj.setSeat_cap(rs.getInt("seat_cap"));
                requiredDataDobj.setUnld_wt(rs.getInt("unld_wt"));
                requiredDataDobj.setLd_wt(rs.getInt("ld_wt"));
                requiredDataDobj.setStand_cap(rs.getInt("stand_cap"));
                requiredDataDobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                requiredDataDobj.setDupPmtReason(rs.getString("reason"));
                requiredDataDobj.setOwn_catg(rs.getInt("owner_ctg"));
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
        return requiredDataDobj;
    }

    public PermitFeeRequiredDataDobj getVaPermitTransactionDetailsWithVtOwnerDetail(String applNo, int pur_cd) {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        PermitFeeRequiredDataDobj requiredDataDobj = null;
        String sqlString;
        RowSet rs;
        try {
            tmgr = new TransactionManager("Get Fee Details");
            sqlString = "Select pmt.state_cd,pmt.pur_cd,pmt.trans_pur_cd,own.vh_class,own.seat_cap,COALESCE(own.stand_cap, '0') AS stand_cap,COALESCE(own.sleeper_cap, '0') AS sleeper_cap,own.unld_wt,own.ld_wt,vt.pmt_type,vt.pmt_catg,to_char(valid_from,'DD-MON-YYYY') as valid_from,to_char(valid_upto,'DD-MON-YYYY') as valid_upto,COALESCE(ownide.owner_ctg, 0::numeric) as owner_ctg,own.other_criteria  from " + TableList.VA_PERMIT_TRANSACTION + " pmt\n"
                    + "inner join " + TableList.VT_PERMIT + " vt on  vt.regn_no = pmt.regn_no\n"
                    + "inner join " + TableList.VT_OWNER + " own on  own.regn_no = pmt.regn_no\n"
                    + " left outer join " + TableList.VT_OWNER_IDENTIFICATION + " ownide on ownide.regn_no = pmt.regn_no\n"
                    + "Where pmt.appl_no = ?";
            ps = tmgr.prepareStatement(sqlString);
            int i = 1;
            ps.setString(i++, applNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                requiredDataDobj = new PermitFeeRequiredDataDobj();
                requiredDataDobj.setState_cd(rs.getString("state_cd"));
                requiredDataDobj.setPur_cd(rs.getInt("pur_cd"));
                requiredDataDobj.setPmt_catg(rs.getInt("pmt_catg"));
                requiredDataDobj.setPmt_type(rs.getInt("pmt_type"));
                requiredDataDobj.setValidFromTemp(rs.getString("valid_from"));
                requiredDataDobj.setValidUptoTemp(rs.getString("valid_upto"));
                requiredDataDobj.setVh_class(rs.getInt("vh_class"));
                requiredDataDobj.setSeat_cap(rs.getInt("seat_cap"));
                requiredDataDobj.setUnld_wt(rs.getInt("unld_wt"));
                requiredDataDobj.setLd_wt(rs.getInt("ld_wt"));
                requiredDataDobj.setStand_cap(rs.getInt("stand_cap"));
                requiredDataDobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                requiredDataDobj.setOwn_catg(rs.getInt("owner_ctg"));
                requiredDataDobj.setOther_criteria(rs.getInt("other_criteria"));
                if (rs.getInt("pur_cd") == TableConstants.VM_PMT_RESTORE_PUR_CD) {
                    requiredDataDobj.setPur_cd(rs.getInt("trans_pur_cd"));
                }

                if (rs.getInt("trans_pur_cd") == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD
                        || rs.getInt("trans_pur_cd") == TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD
                        || rs.getInt("trans_pur_cd") == TableConstants.VM_PMT_CANCELATION_PUR_CD) {
                    requiredDataDobj.setPur_cd(rs.getInt("trans_pur_cd"));
                }
                requiredDataDobj.setTrans_pur_cd(rs.getInt("trans_pur_cd"));
            } else {
                sqlString = "Select pmt.state_cd,pmt.pur_cd,pmt.trans_pur_cd,own.vh_class,own.seat_cap,COALESCE(own.stand_cap, '0') AS stand_cap,COALESCE(own.sleeper_cap, '0') AS sleeper_cap,own.unld_wt,own.ld_wt,vt.pmt_type,vt.pmt_catg,to_char(valid_from,'DD-MON-YYYY') as valid_from,to_char(valid_upto,'DD-MON-YYYY') as valid_upto,COALESCE(ownide.owner_ctg, 0::numeric) as owner_ctg  from " + TableList.VA_PERMIT_TRANSACTION + " pmt\n"
                        + " inner join " + TableList.VT_TEMP_PERMIT + " vt on  vt.state_cd = pmt.state_cd and vt.regn_no = pmt.regn_no and vt.pmt_no = pmt.pmt_no\n"
                        + " inner join " + TableList.VT_OWNER + " own on  own.regn_no = pmt.regn_no \n"
                        + " left outer join " + TableList.VT_OWNER_IDENTIFICATION + " ownide on ownide.regn_no = pmt.regn_no \n"
                        + " Where pmt.appl_no = ?";
                ps = tmgr.prepareStatement(sqlString);
                int j = 1;
                ps.setString(j++, applNo);
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    requiredDataDobj = new PermitFeeRequiredDataDobj();
                    requiredDataDobj.setState_cd(rs.getString("state_cd"));
                    requiredDataDobj.setPur_cd(rs.getInt("pur_cd"));
                    requiredDataDobj.setPmt_catg(rs.getInt("pmt_catg"));
                    requiredDataDobj.setPmt_type(rs.getInt("pmt_type"));
                    requiredDataDobj.setValidFromTemp(rs.getString("valid_from"));
                    requiredDataDobj.setValidUptoTemp(rs.getString("valid_upto"));
                    requiredDataDobj.setVh_class(rs.getInt("vh_class"));
                    requiredDataDobj.setSeat_cap(rs.getInt("seat_cap"));
                    requiredDataDobj.setUnld_wt(rs.getInt("unld_wt"));
                    requiredDataDobj.setLd_wt(rs.getInt("ld_wt"));
                    requiredDataDobj.setStand_cap(rs.getInt("stand_cap"));
                    requiredDataDobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                    requiredDataDobj.setOwn_catg(rs.getInt("owner_ctg"));
                    if (rs.getInt("pur_cd") == TableConstants.VM_PMT_RESTORE_PUR_CD) {
                        requiredDataDobj.setPur_cd(rs.getInt("trans_pur_cd"));
                    }

                    if (rs.getInt("trans_pur_cd") == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD
                            || rs.getInt("trans_pur_cd") == TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD
                            || rs.getInt("trans_pur_cd") == TableConstants.VM_PMT_CANCELATION_PUR_CD) {
                        requiredDataDobj.setPur_cd(rs.getInt("trans_pur_cd"));
                    }
                    requiredDataDobj.setTrans_pur_cd(rs.getInt("trans_pur_cd"));

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
        return requiredDataDobj;
    }

    public PermitFeeRequiredDataDobj getTransferReplacementCase(String applNo, int pur_cd) {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        PermitFeeRequiredDataDobj requiredDataDobj = null;
        String sqlString;
        RowSet rs;
        try {
            tmgr = new TransactionManager("Get Fee Details");
            sqlString = "Select pmt.state_cd,pmt.pur_cd,pmt.trans_pur_cd,own.vh_class,own.seat_cap,COALESCE(own.stand_cap, '0') AS stand_cap,COALESCE(own.sleeper_cap, '0') AS sleeper_cap,own.unld_wt,own.ld_wt,vh.pmt_type,to_char(vh.valid_from,'DD-MON-YYYY') as valid_from,to_char(vh.valid_upto,'DD-MON-YYYY') as valid_upto,COALESCE(ownide.owner_ctg, 0::numeric) as owner_ctg from " + TableList.VA_PERMIT_TRANSACTION + " pmt\n"
                    + "inner join " + TableList.VH_PERMIT + " vh on  vh.regn_no = pmt.regn_no\n"
                    + "inner join " + TableList.VT_OWNER + " own on  CHANGE_STRING\n"
                    + " left outer join " + TableList.VT_OWNER_IDENTIFICATION + " ownide on ownide.regn_no = own.regn_no\n"
                    + "Where pmt.appl_no = ? order by vh.op_dt DESC limit 1";
            int i = 1;
            if (pur_cd == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD) {
                sqlString = sqlString.replace("CHANGE_STRING", "own.regn_no = pmt.new_regn_no");
            } else {
                sqlString = sqlString.replace("CHANGE_STRING", "own.regn_no = pmt.regn_no");
            }
            ps = tmgr.prepareStatement(sqlString);
            ps.setString(i++, applNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                requiredDataDobj = new PermitFeeRequiredDataDobj();
                requiredDataDobj.setState_cd(rs.getString("state_cd"));
                requiredDataDobj.setPur_cd(rs.getInt("pur_cd"));
                requiredDataDobj.setPmt_type(rs.getInt("pmt_type"));
                requiredDataDobj.setValidFromTemp(rs.getString("valid_from"));// Temp used for Valid From 
                requiredDataDobj.setValidUptoTemp(rs.getString("valid_upto"));// Temp used for Valid Upto
                requiredDataDobj.setVh_class(rs.getInt("vh_class"));
                requiredDataDobj.setSeat_cap(rs.getInt("seat_cap"));
                requiredDataDobj.setUnld_wt(rs.getInt("unld_wt"));
                requiredDataDobj.setLd_wt(rs.getInt("ld_wt"));
                requiredDataDobj.setStand_cap(rs.getInt("stand_cap"));
                requiredDataDobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                requiredDataDobj.setTrans_pur_cd(rs.getInt("trans_pur_cd"));
                requiredDataDobj.setOwn_catg(rs.getInt("owner_ctg"));
                if (rs.getInt("pur_cd") == TableConstants.VM_PMT_RESTORE_PUR_CD) {
                    requiredDataDobj.setPur_cd(rs.getInt("trans_pur_cd"));
                }
            } else {
                if (pur_cd == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD) {
                    rs = null;
                    sqlString = "Select pmt.state_cd,pmt.pur_cd,pmt.trans_pur_cd,own.vh_class,own.seat_cap,COALESCE(own.stand_cap, '0') AS stand_cap,COALESCE(own.sleeper_cap, '0') AS sleeper_cap,own.unld_wt,own.ld_wt,vh.pmt_type,to_char(vh.valid_from,'DD-MON-YYYY') as valid_from,to_char(vh.valid_upto,'DD-MON-YYYY') as valid_upto,COALESCE(ownide.owner_ctg, 0::numeric) as owner_ctg from " + TableList.VA_PERMIT_TRANSACTION + " pmt\n"
                            + "inner join " + TableList.VH_PERMIT + " vh on  vh.regn_no = pmt.regn_no\n"
                            + "inner join " + TableList.VT_OWNER + " own on  own.regn_no = pmt.new_regn_no\n"
                            + " left outer join " + TableList.VT_OWNER_IDENTIFICATION + " ownide on ownide.regn_no = pmt.regn_no\n"
                            + "Where pmt.appl_no = ? order by vh.op_dt DESC limit 1";
                    int j = 1;
                    ps = tmgr.prepareStatement(sqlString);
                    ps.setString(j++, applNo);
                    rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        requiredDataDobj = new PermitFeeRequiredDataDobj();
                        requiredDataDobj.setState_cd(rs.getString("state_cd"));
                        requiredDataDobj.setPur_cd(rs.getInt("pur_cd"));
                        requiredDataDobj.setPmt_type(rs.getInt("pmt_type"));
                        requiredDataDobj.setValidFromTemp(rs.getString("valid_from"));// Temp used for Valid From 
                        requiredDataDobj.setValidUptoTemp(rs.getString("valid_upto"));// Temp used for Valid Upto
                        requiredDataDobj.setVh_class(rs.getInt("vh_class"));
                        requiredDataDobj.setSeat_cap(rs.getInt("seat_cap"));
                        requiredDataDobj.setUnld_wt(rs.getInt("unld_wt"));
                        requiredDataDobj.setLd_wt(rs.getInt("ld_wt"));
                        requiredDataDobj.setStand_cap(rs.getInt("stand_cap"));
                        requiredDataDobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                        requiredDataDobj.setTrans_pur_cd(rs.getInt("trans_pur_cd"));
                        requiredDataDobj.setOwn_catg(rs.getInt("owner_ctg"));
                        if (rs.getInt("pur_cd") == TableConstants.VM_PMT_RESTORE_PUR_CD) {
                            requiredDataDobj.setPur_cd(rs.getInt("trans_pur_cd"));
                        }
                    }
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
        return requiredDataDobj;
    }

    public PermitFeeRequiredDataDobj getCounterSignatureCase(String applNo, int pur_cd) {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        PermitFeeRequiredDataDobj requiredDataDobj = null;
        String sqlString;
        RowSet rs;
        try {
            tmgr = new TransactionManager("Get Fee Details");
            sqlString = "select *,'0' stand_cap,'0' sleeper_cap from permit.va_permit_countersignature where appl_no =?";
            ps = tmgr.prepareStatement(sqlString);
            int i = 1;
            ps.setString(i++, applNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                requiredDataDobj = new PermitFeeRequiredDataDobj();
                requiredDataDobj.setState_cd(rs.getString("state_cd"));
                requiredDataDobj.setPur_cd(pur_cd);
                requiredDataDobj.setPmt_type(rs.getInt("pmt_type"));
                requiredDataDobj.setPeriod_mode(rs.getString("period_mode"));
                requiredDataDobj.setPeriod(rs.getInt("period"));
                requiredDataDobj.setVh_class(rs.getInt("vh_class"));
                requiredDataDobj.setSeat_cap(rs.getInt("seat_cap"));
                requiredDataDobj.setUnld_wt(rs.getInt("unld_wt"));
                requiredDataDobj.setLd_wt(rs.getInt("ld_wt"));
                requiredDataDobj.setStand_cap(rs.getInt("stand_cap"));
                requiredDataDobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                if (checkVehicleState(rs.getString("state_cd"), rs.getString("regn_no"))) {
                    requiredDataDobj.setRegn_state(true);
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
        return requiredDataDobj;
    }

    public boolean checkVehicleState(String state_cd, String regn_no) {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        boolean isExist = false;
        String vaStatusSelectSql = " select regn_no from " + TableList.VT_OWNER + " where state_cd=? and regn_no=? and status in ('A','Y')";
        try {
            tmgr = new TransactionManagerReadOnly("checkVehicleState");
            ps = tmgr.prepareStatement(vaStatusSelectSql);
            ps.setString(1, state_cd);
            ps.setString(2, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isExist = true;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return isExist;
    }

    public PermitFeeRequiredDataDobj getDupCounterSignature(String regn_no, int pur_cd) {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        PermitFeeRequiredDataDobj requiredDataDobj = null;
        String sqlString;
        RowSet rs;
        try {
            tmgr = new TransactionManager("getDupCounterSignature");
            sqlString = "select *, to_char(a.count_valid_from,'DD-MON-YYYY') as valid_from,to_char(a.count_valid_upto,'DD-MON-YYYY') as valid_upto from permit.vt_permit_countersignature a inner join VA_DUP b on a.regn_no=b.regn_no and a.state_cd=b.state_cd where a.regn_no =? and b.pur_cd=?";
            ps = tmgr.prepareStatement(sqlString);
            int i = 1;
            ps.setString(i++, regn_no);
            ps.setInt(i++, pur_cd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {

                requiredDataDobj = new PermitFeeRequiredDataDobj();
                requiredDataDobj.setState_cd(rs.getString("state_cd"));
                requiredDataDobj.setPur_cd(pur_cd);
                requiredDataDobj.setPmt_type(rs.getInt("pmt_type"));
                requiredDataDobj.setValidFromTemp(rs.getString("valid_from"));
                requiredDataDobj.setValidUptoTemp(rs.getString("valid_upto"));
                requiredDataDobj.setVh_class(rs.getInt("vh_class"));
                requiredDataDobj.setSeat_cap(rs.getInt("seat_cap"));
                requiredDataDobj.setUnld_wt(rs.getInt("unld_wt"));
                requiredDataDobj.setLd_wt(rs.getInt("ld_wt"));
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
        return requiredDataDobj;
    }

    public PermitFeeRequiredDataDobj getVaPermitHomeAuthDetailsWithVtOwnerDetail(String applNo, int pur_cd) {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        PermitFeeRequiredDataDobj requiredDataDobj = null;
        String sqlString;
        RowSet rs;
        try {
            tmgr = new TransactionManager("Get Fee Details");

            sqlString = "Select own.state_cd,pmt.pur_cd,vtpmt.pmt_catg,own.vh_class,own.seat_cap,replace(vtpmt.region_covered,' ','') as region_covered,COALESCE(own.stand_cap, '0') AS stand_cap,COALESCE(own.sleeper_cap, '0') AS sleeper_cap,own.unld_wt,own.ld_wt,vtpmt.pmt_type,to_char(pmt.auth_fr,'DD-MON-YYYY') as auth_fr,to_char(pmt.auth_to,'DD-MON-YYYY') as auth_to,COALESCE(ownide.owner_ctg, 0::numeric) as owner_ctg,vt_auth.auth_to as main_auth_from from " + TableList.VA_PERMIT_HOME_AUTH + " pmt\n"
                    + " inner join " + TableList.VT_PERMIT + " vtpmt on  vtpmt.regn_no = pmt.regn_no\n"
                    + " inner join " + TableList.VT_OWNER + " own on  own.regn_no = pmt.regn_no and vtpmt.state_cd = own.state_cd\n"
                    + " left outer join " + TableList.VT_PERMIT_HOME_AUTH + " vt_auth on  vt_auth.regn_no = own.regn_no and  vt_auth.pmt_no = vtpmt.pmt_no \n"
                    + " left outer join " + TableList.VT_OWNER_IDENTIFICATION + " ownide on ownide.regn_no = pmt.regn_no\n"
                    + "Where pmt.appl_no = ? order by vtpmt.pmt_type desc";
            ps = tmgr.prepareStatement(sqlString);
            int i = 1;
            ps.setString(i++, applNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                requiredDataDobj = new PermitFeeRequiredDataDobj();
                requiredDataDobj.setState_cd(rs.getString("state_cd"));
                requiredDataDobj.setPur_cd(rs.getInt("pur_cd"));
                requiredDataDobj.setPmt_catg(rs.getInt("pmt_catg"));
                if ("HP".contains(Util.getUserStateCode()) && rs.getInt("pmt_type") == Integer.valueOf(TableConstants.GOODS_PERMIT)) {
                    requiredDataDobj.setPmt_type(Integer.valueOf(TableConstants.NATIONAL_PERMIT));
                } else {
                    requiredDataDobj.setPmt_type(rs.getInt("pmt_type"));
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("region_covered"))) {
                    requiredDataDobj.setRegion_covered((rs.getString("region_covered")).substring(0, (rs.getString("region_covered")).length() - 1));
                } else {
                    requiredDataDobj.setRegion_covered("");
                }
                if (rs.getDate("main_auth_from") != null) {
                    requiredDataDobj.setValidFromPrvDate(JSFUtils.getDateInDD_MMM_YYYY(
                            DateUtils.parseDate(DateUtils.addToDate(rs.getDate("main_auth_from"), DateUtils.DAY, 1))));
                    requiredDataDobj.setValidAuthUpToDate(DateUtils.getDateInDDMMYYYY(rs.getDate("main_auth_from")));
                } else {
                    requiredDataDobj.setValidFromPrvDate(null);
                    requiredDataDobj.setValidAuthUpToDate(null);
                }

                requiredDataDobj.setValidFromTemp(rs.getString("auth_fr"));// Temp used for Valid From 
                requiredDataDobj.setValidUptoTemp(rs.getString("auth_to"));// Temp used for Valid Upto
                requiredDataDobj.setVh_class(rs.getInt("vh_class"));
                requiredDataDobj.setSeat_cap(rs.getInt("seat_cap"));
                requiredDataDobj.setUnld_wt(rs.getInt("unld_wt"));
                requiredDataDobj.setLd_wt(rs.getInt("ld_wt"));
                requiredDataDobj.setStand_cap(rs.getInt("stand_cap"));
                requiredDataDobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                requiredDataDobj.setOwn_catg(rs.getInt("owner_ctg"));
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
        return requiredDataDobj;
    }

    public PermitFeeRequiredDataDobj getVaPermitTransDtlsWithTransferPermit(String applNo, int pur_cd) {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        PermitFeeRequiredDataDobj requiredDataDobj = null;
        String sqlString;
        RowSet rs;
        try {
            tmgr = new TransactionManager("getVaPermitTransDtlsWithTransferPermit");

            sqlString = "select tran.excempted_flag,tran.state_cd,tran.off_cd,pmt_type,trans_pur_cd as pur_cd,own.vh_class,own.seat_cap,COALESCE(own.stand_cap, '0') AS stand_cap,COALESCE(own.sleeper_cap, '0') AS sleeper_cap,pmt.pmt_catg,to_char(valid_from,'DD-MON-YYYY') as valid_from,to_char(valid_upto,'DD-MON-YYYY') as valid_upto,COALESCE(ownide.owner_ctg, 0::numeric) as owner_ctg from " + TableList.VT_PERMIT + " pmt\n"
                    + " inner join " + TableList.VA_PERMIT_TRANSACTION + " tran on  tran.regn_no = pmt.regn_no\n"
                    + " inner join " + TableList.VT_OWNER + "  own on  own.regn_no = pmt.regn_no\n"
                    + " left outer join " + TableList.VT_OWNER_IDENTIFICATION + " ownide on ownide.regn_no = pmt.regn_no\n"
                    + " where tran.appl_no = ?";
            ps = tmgr.prepareStatement(sqlString);
            int i = 1;
            ps.setString(i++, applNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                requiredDataDobj = new PermitFeeRequiredDataDobj();
                requiredDataDobj.setState_cd(rs.getString("state_cd"));
                requiredDataDobj.setPmt_catg(rs.getInt("pmt_catg"));
                requiredDataDobj.setPmt_type(rs.getInt("pmt_type"));
                requiredDataDobj.setValidFromTemp(rs.getString("valid_from"));// Temp used for Valid From 
                requiredDataDobj.setValidUptoTemp(rs.getString("valid_upto"));// Temp used for Valid Upto
                requiredDataDobj.setPur_cd(rs.getInt("pur_cd"));
                requiredDataDobj.setVh_class(rs.getInt("vh_class"));
                requiredDataDobj.setSeat_cap(rs.getInt("seat_cap"));
                requiredDataDobj.setStand_cap(rs.getInt("stand_cap"));
                requiredDataDobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                requiredDataDobj.setExcemptedFlag(rs.getString("excempted_flag"));
                requiredDataDobj.setOwn_catg(rs.getInt("owner_ctg"));
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
        return requiredDataDobj;
    }

    public String getVhClassCatgWithRegnNo(String regnNO, int pur_cd) {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String vhClassCatg = null;
        String sqlString;
        RowSet rs;
        try {
            tmgr = new TransactionManager("getVhClassCatgWithRegnNo");
            if (pur_cd == TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD) {
                sqlString = "SELECT vch_catg FROM permit.va_permit_countersignature WHERE regn_no=?";
            } else {
                sqlString = "SELECT vch_catg FROM " + TableList.VT_OWNER + " WHERE regn_no=? and status in (?,?)";
            }
            ps = tmgr.prepareStatement(sqlString);
            int i = 1;
            ps.setString(i++, regnNO);
            if (pur_cd != TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD) {
                ps.setString(i++, "A");
                ps.setString(i++, "Y");
            }
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                vhClassCatg = rs.getString("vch_catg");
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
        return vhClassCatg;
    }

    public List<PermitShowFeeDetailDobj> getPmtFeeDetailsFromSlab(PermitFeeRequiredDataDobj requiredDataDobj, Date validFrom, Date validUpto, TransactionManager tmgr, PermitShowFeeDetailDobj dobj, List<PermitShowFeeDetailDobj> feeShowDetail, boolean checkPurCd) {
        String Query;
        Owner_dobj ownerdobj = null;
        PassengerPermitDetailDobj pmtDobj = null;
        int monthly_tax_amt = 0;
        boolean regn_state = false;
        int perState = 0, pDay = 0, pMonth = 0, pCMonth = 0, pYear = 0, perRegion = 0, perRoute = 0, exem_amount = 0, fine_to_be_taken = 0, rLength = 0, authYear = 0;
        try {

            Map<String, String> stateConfigMap = CommonPermitPrintImpl.getVmPermitStateConfiguration(Util.getUserStateCode());

            Query = "SELECT * FROM " + TableList.VM_PERMIT_FEE_SLAB + " where state_cd = ? ";
            if (checkPurCd) {
                Query += " AND pur_cd = ?";
            }
            PreparedStatement ps = tmgr.prepareStatement(Query);
            ps.setString(1, requiredDataDobj.getState_cd());
            if (checkPurCd) {
                ps.setInt(2, requiredDataDobj.getPur_cd());
            }
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            ownerdobj = new Owner_dobj();
            pmtDobj = new PassengerPermitDetailDobj();
            if (requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD) {
                pmtDobj.setDomainCovered(getPerRegionCode(requiredDataDobj.getAppl_no(), tmgr, TableList.VA_PERMIT));
                pmtDobj.setServices_TYPE(String.valueOf(requiredDataDobj.getService_type()));
            } else if (requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD) {
                pmtDobj.setDomainCovered(requiredDataDobj.getRegion_covered());
            }

            if (requiredDataDobj.getVh_class() != 0) {
                ownerdobj.setVh_class(requiredDataDobj.getVh_class());
                if (!CommonUtils.isNullOrBlank(requiredDataDobj.getAppl_no()) && (requiredDataDobj.getPur_cd() != 0)) {
                    ownerdobj.setVch_catg(getVhClassCatgWithRegnNo(requiredDataDobj.getRegn_no().toUpperCase().trim(), requiredDataDobj.getPur_cd()));
                }
                ownerdobj.setState_cd(requiredDataDobj.getState_cd().toUpperCase().trim());
                ownerdobj.setSeat_cap(requiredDataDobj.getSeat_cap());
                ownerdobj.setUnld_wt(requiredDataDobj.getUnld_wt());
                ownerdobj.setLd_wt(requiredDataDobj.getLd_wt());
                ownerdobj.setStand_cap(requiredDataDobj.getStand_cap());
                ownerdobj.setSleeper_cap(requiredDataDobj.getSleeper_cap());
                ownerdobj.setOwner_identity(new OwnerIdentificationDobj());
                ownerdobj.getOwner_identity().setOwnerCatg(requiredDataDobj.getOwn_catg());
                ownerdobj.setFuel(requiredDataDobj.getFuel_type());
                ownerdobj.setOwner_cd(requiredDataDobj.getOwner_cd());
                ownerdobj.setOther_criteria(requiredDataDobj.getOther_criteria());
                if (!CommonUtils.isNullOrBlank(requiredDataDobj.getRegn_no())) {
                    ownerdobj.setRegn_no(requiredDataDobj.getRegn_no());
                    BlackListedVehicleDobj blackListedDobj = new BlackListedVehicleImpl().getBlacklistedVehicleDetails(requiredDataDobj.getRegn_no(), null);
                    ownerdobj.setBlackListedVehicleDobj(blackListedDobj);
                }
            }
            while (rs.next()) {
                if (requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_TEMP_PUR_CD
                        || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_RENEW_TEMP_PUR_CD
                        || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_SPECIAL_PUR_CD) {
                    pmtDobj.setServices_TYPE(String.valueOf(requiredDataDobj.getService_type()));
                    if (rs.getString("period_mode").equalsIgnoreCase("D")) {
                        long diff = (validUpto.getTime() - validFrom.getTime());
                        pDay = ((int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1);
                    }

                    if (rs.getString("period_mode").equalsIgnoreCase("C")) {
                        Calendar d1 = Calendar.getInstance();
                        d1.setTime(validFrom);
                        Calendar d2 = Calendar.getInstance();
                        d2.setTime(validUpto);
                        pCMonth = ((d2.get(Calendar.YEAR) - d1.get(Calendar.YEAR)) * 12 + d2.get(Calendar.MONTH) - d1.get(Calendar.MONTH) + 1);
                    }
                    if (requiredDataDobj.getVh_class() == 73 && rs.getInt("trans_pur_cd") == TableConstants.VM_PMT_DIFFERENTIAL_SPECIAL_TAX && requiredDataDobj.getState_cd().toUpperCase().trim().equalsIgnoreCase("OR") && requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_SPECIAL_PUR_CD) {
                        int prv_pmtMonth = 0;
                        int prv_pmtYear = 0;
                        int valid_fromYear = 0;
                        int valid_UptoYear = 0;
                        PermitCheckDetailsImpl impl = new PermitCheckDetailsImpl();
                        Date prv_valid_upto = getPrvValidUpto(requiredDataDobj.getRegn_no().toUpperCase().trim(), tmgr);
                        String[] taxPmtDetls = impl.getPmtDetailsOnTaxBasedOn(requiredDataDobj.getRegn_no().toUpperCase().trim(), requiredDataDobj.getState_cd(), 0, null);
                        if (taxPmtDetls[2] != null) {
                            pmtDobj.setServices_TYPE(taxPmtDetls[2].toString());
                        }
                        if (!CommonUtils.isNullOrBlank(taxPmtDetls[6].toString())) {
                            ownerdobj.setTax_mode(taxPmtDetls[6].toString());
                        }
                        if (!CommonUtils.isNullOrBlank(DateUtils.parseDate(prv_valid_upto))) {
                            prv_pmtMonth = DateUtils.getDatePart(DateUtils.getDateInDDMMYYYY(prv_valid_upto), DateUtils.MONTH);
                            prv_pmtYear = DateUtils.getDatePart(DateUtils.getDateInDDMMYYYY(prv_valid_upto), DateUtils.YEAR);
                        }
                        int valid_fromMonth = DateUtils.getDatePart(DateUtils.getDateInDDMMYYYY(validFrom), DateUtils.MONTH);
                        valid_fromYear = DateUtils.getDatePart(DateUtils.getDateInDDMMYYYY(validFrom), DateUtils.YEAR);
                        int valid_uptoMonth = DateUtils.getDatePart(DateUtils.getDateInDDMMYYYY(validUpto), DateUtils.MONTH);
                        valid_UptoYear = DateUtils.getDatePart(DateUtils.getDateInDDMMYYYY(validUpto), DateUtils.YEAR);
                        monthly_tax_amt = getMonthlyTaxAmt(requiredDataDobj.getRegn_no());
                        //Spl Tax Orissa
                        if ((valid_fromMonth == 12) && (valid_fromMonth > valid_uptoMonth)) {
                            valid_uptoMonth = valid_fromMonth + valid_uptoMonth;
                        }

                        if ((prv_pmtMonth == valid_fromMonth) && (prv_pmtMonth < valid_uptoMonth) && prv_valid_upto != null) {
                            validFrom = DateUtils.addToDate(validFrom, DateUtils.MONTH, 1);
                            validFrom = DateUtils.parseDate(DateUtils.getStartOfMonthDate(DateUtils.parseDate(validFrom)));
                            pMonth = getDate1MinusDate2_Months(validFrom, validUpto);
                        } else if ((prv_pmtMonth < valid_fromMonth) && (prv_pmtMonth < valid_uptoMonth) && prv_valid_upto != null) {
                            pMonth = getDate1MinusDate2_Months(validFrom, validUpto);
                        } else if ((prv_pmtMonth > valid_fromMonth) && (prv_pmtMonth > valid_uptoMonth) && prv_valid_upto != null) {
                            if (valid_uptoMonth - valid_fromMonth == 0) {
                                pMonth = 1;
                            } else {
                                pMonth = ((valid_uptoMonth - valid_fromMonth) + 1);
                            }
                        } else if (prv_valid_upto != null && (prv_pmtYear != valid_fromYear && prv_pmtMonth == valid_fromMonth) && (prv_pmtYear != valid_UptoYear && prv_pmtMonth == valid_uptoMonth)) {
                            pMonth = 1;
                        } else if (prv_valid_upto == null) {
                            pMonth = getDate1MinusDate2_Months(validFrom, validUpto);
                        }
                    }
                    if (rs.getInt("trans_pur_cd") == TableConstants.VM_PMT_DIFFERENTIAL_SPECIAL_TAX && requiredDataDobj.getState_cd().toUpperCase().trim().equalsIgnoreCase("MH") && requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_SPECIAL_PUR_CD) {
                        pDay = getDaysWithinState(requiredDataDobj.getState_cd(), requiredDataDobj.getAppl_no(), pDay);
                    }
                    rLength = requiredDataDobj.getRoute_length();
                }

                // Count base fee structure
                if (rs.getBoolean("per_region_count")) {
                    if (requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_TEMP_PUR_CD
                            || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_RENEW_TEMP_PUR_CD
                            || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_SPECIAL_PUR_CD) {
                        perRegion = getTempRegionCount(requiredDataDobj.getAppl_no(), tmgr);
                        perState = getTempStateCount(requiredDataDobj.getAppl_no(), tmgr);
                    } else if (requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD && !CommonUtils.isNullOrBlank(requiredDataDobj.getRegion_covered())) {
                        perRegion = (requiredDataDobj.getRegion_covered() + ",").split(",").length;
                    } else {
                        perRegion = getPerRegionCount(requiredDataDobj.getAppl_no(), tmgr);
                    }
                    if (getPerRegionFlag(requiredDataDobj.getState_cd(), requiredDataDobj.getPmt_type(), requiredDataDobj.getPur_cd())) {
                        perRegion = getHowManyRegionContain(requiredDataDobj.getAppl_no(), tmgr);
                    }
                    if (perRegion != 0) {
                        pmtDobj.setRegion_covered(String.valueOf(perRegion));
                    }
                    if (perState != 0) {
                        pmtDobj.setState_covered(String.valueOf(perState));
                    }
                    pmtDobj.setMultiRegion(checkMultiRegion(requiredDataDobj.getAppl_no(), tmgr));
                }

                if (rs.getBoolean("per_route_count")) {
                    perRoute = getPerRegionCount(requiredDataDobj.getAppl_no(), tmgr);
                    if (perRoute != 0) {
                        pmtDobj.setRout_length(String.valueOf(perRoute));
                    }
                    if (perRoute == 0) {
                        pmtDobj.setRout_length(String.valueOf(getPerRouteCount(requiredDataDobj.getAppl_no(), tmgr)));
                    }
                }

                // Code base fee structure
                if (rs.getBoolean("per_region_code")) {
                    if (requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_TEMP_PUR_CD
                            || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_RENEW_TEMP_PUR_CD
                            || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_SPECIAL_PUR_CD) {
                        perRegion = getPerRegion(requiredDataDobj.getAppl_no(), tmgr, TableList.VA_TEMP_PERMIT_ROUTE);
                    } else {
                        perRegion = getPerRegion(requiredDataDobj.getAppl_no(), tmgr, TableList.VA_PERMIT);
                    }
                    if (perRegion != 0) {
                        pmtDobj.setRegion_covered(String.valueOf(perRegion));
                    }
                    pmtDobj.setMultiRegion(checkMultiRegion(requiredDataDobj.getAppl_no(), tmgr));
                }

                if (requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD
                        || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_APPLICATION_PUR_CD) {
                    rLength = requiredDataDobj.getRoute_length();
                    if (rs.getString("period_mode").equalsIgnoreCase("Y")) {
                        if (requiredDataDobj.getPeriod_mode().equalsIgnoreCase("M")) {
                            pYear = (int) Math.ceil(requiredDataDobj.getPeriod() / 12.0);
                        }
                        if (requiredDataDobj.getPeriod_mode().equalsIgnoreCase("Y")) {
                            pYear = Integer.valueOf(requiredDataDobj.getPeriod());
                        }
                    } else if (rs.getString("period_mode").equalsIgnoreCase("M")) {
                        if (requiredDataDobj.getPeriod_mode().equalsIgnoreCase("M")) {
                            pMonth = requiredDataDobj.getPeriod();
                        }
                        if (requiredDataDobj.getPeriod_mode().equalsIgnoreCase("Y")) {
                            pMonth = (int) Math.ceil(requiredDataDobj.getPeriod() * 12.0);
                        }
                    }
                    if (requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD) {
                        pDay = getDayDiffForNewPermit(requiredDataDobj.getRegn_no(), tmgr);
                        if (pDay < 0) {
                            pDay = 0;
                        }
                    }
                }

                if (requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD) {
                    if (rs.getString("period_mode").equalsIgnoreCase("Y")) {
                        if (requiredDataDobj.getPeriod_mode().equalsIgnoreCase("M")) {
                            pYear = (int) Math.ceil(requiredDataDobj.getPeriod() / 12.0);
                        }
                        if (requiredDataDobj.getPeriod_mode().equalsIgnoreCase("Y")) {
                            pYear = requiredDataDobj.getPeriod();
                        }
                    } else if (rs.getString("period_mode").equalsIgnoreCase("M")) {
                        if (requiredDataDobj.getPeriod_mode().equalsIgnoreCase("M")) {
                            pMonth = requiredDataDobj.getPeriod();
                        }
                        if (requiredDataDobj.getPeriod_mode().equalsIgnoreCase("Y")) {
                            pMonth = (int) Math.ceil(requiredDataDobj.getPeriod() * 12.0);
                        }
                    }
                    regn_state = requiredDataDobj.isRegn_state();
                }

                if (requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_RENEWAL_PUR_CD
                        || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_SURRENDER_PUR_CD
                        || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_TRANSFER_PUR_CD
                        || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD
                        || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD
                        || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD
                        || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_CANCELATION_PUR_CD) {
                    rLength = requiredDataDobj.getRoute_length();
                    if (rs.getString("period_mode").equalsIgnoreCase("Y")) {
                        if ((!CommonUtils.isNullOrBlank(requiredDataDobj.getPeriod_mode())) && requiredDataDobj.getPeriod_mode().equalsIgnoreCase("M")) {
                            pYear = (int) Math.ceil(requiredDataDobj.getPeriod() / 12.0);
                        }
                        if ((!CommonUtils.isNullOrBlank(requiredDataDobj.getPeriod_mode())) && requiredDataDobj.getPeriod_mode().equalsIgnoreCase("Y")) {
                            pYear = requiredDataDobj.getPeriod();
                        }
                    } else if (rs.getString("period_mode").equalsIgnoreCase("M")) {
                        if (requiredDataDobj.getPeriod_mode().equalsIgnoreCase("M")) {
                            pMonth = requiredDataDobj.getPeriod();
                        }
                        if (requiredDataDobj.getPeriod_mode().equalsIgnoreCase("Y")) {
                            pMonth = (int) Math.ceil(requiredDataDobj.getPeriod() * 12.0);
                        }
                    }
                    pmtDobj.setServices_TYPE(String.valueOf(requiredDataDobj.getService_type()));
                    pmtDobj.setTransferForexempted(requiredDataDobj.getExcemptedFlag());
                    int[] value = getPermitExpireDays(requiredDataDobj.getRegn_no(), tmgr, requiredDataDobj.getPur_cd());
                    pDay = value[0];
                    if (pDay < 0 && (!(requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_RENEWAL_PUR_CD))) {
                        pDay = 0;
                    }
                    String day = "";
                    if (requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_RENEWAL_PUR_CD && pDay > 0 && requiredDataDobj.getState_cd().toUpperCase().trim().equalsIgnoreCase("MH")) {
                        VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                        parameters.setPUR_CD(requiredDataDobj.getPur_cd());
                        parameters.setPERMIT_TYPE(requiredDataDobj.getPmt_type());
                        parameters.setTAX_DUE_FROM_DATE(requiredDataDobj.getValidUpToPrvDate());
                        if (stateConfigMap != null && !stateConfigMap.get("fine_exempted_condition").equalsIgnoreCase("FALSE")) {
                            day = FormulaUtils.getSqlFormulaValue(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("fine_exempted_condition"), parameters), "PermitShowFeeDetailImpl");
                        }
                        if (stateConfigMap != null && !CommonUtils.isNullOrBlank(day) && JSFUtils.isNumeric(day)) {
                            pDay = Integer.parseInt(day);
                        }
                    }
                    //remove this conditon on 20-06-2019 by manoj (|| requiredDataDobj.getPmt_type() == Integer.valueOf(TableConstants.GOODS_PERMIT))
                    if (requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_RENEWAL_PUR_CD
                            && (requiredDataDobj.getPmt_type() == Integer.valueOf(TableConstants.AITP) || requiredDataDobj.getPmt_type() == Integer.valueOf(TableConstants.NATIONAL_PERMIT))
                            && rs.getInt("trans_pur_cd") == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD) {
                        int authDay = getHomeAuthExpireDays(requiredDataDobj.getRegn_no(), tmgr, requiredDataDobj.getPur_cd());
                        pDay = authDay;
                    } //update GOODS_PERMIT to National permit conditon on 20-06-2019 by manoj (|| requiredDataDobj.getPmt_type() == Integer.valueOf(TableConstants.GOODS_PERMIT))
                    else if (requiredDataDobj.getPur_cd() != TableConstants.VM_PMT_RENEWAL_PUR_CD
                            && (requiredDataDobj.getPmt_type() == Integer.valueOf(TableConstants.AITP) || requiredDataDobj.getPmt_type() == Integer.valueOf(TableConstants.NATIONAL_PERMIT))) {
                        int authDay = getHomeAuthExpireDays(requiredDataDobj.getRegn_no(), tmgr, requiredDataDobj.getPur_cd());
                        if (authDay < 0) {
                            authDay = 0;
                        }
                        pDay = pDay + authDay;
                    }
                    exem_amount = value[1];
                    fine_to_be_taken = value[2];
                }
                if (requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD && requiredDataDobj.isRenderRecursiveAuthfee()) {
                    Date validFromAuth = CommonPermitPrintImpl.getDateDD_MMM_YYYY(requiredDataDobj.getValidFromTemp());
                    Date validUptoAuth = CommonPermitPrintImpl.getDateDD_MMM_YYYY(requiredDataDobj.getValidUptoTemp());
                    Map<String, Integer> ageMap = calculateAge(validFromAuth, validUptoAuth);
                    authYear = ageMap.get("YEARS");
                    int months = ageMap.get("MONTHS");
                    int days = ageMap.get("DAYS");
                    if (months > 0 || days > 0) {
                        authYear = authYear + 1;
                    }
                }
                if (requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD) {
                    pDay = getHomeAuthExpireDays(requiredDataDobj.getRegn_no(), tmgr, requiredDataDobj.getPur_cd());
                }
                if (requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_DUPLICATE_PUR_CD) {
                    pmtDobj.setMultiDoc(getMultiDocument(requiredDataDobj.getRegn_no(), requiredDataDobj.getAppl_no(), tmgr));
                    pmtDobj.setDupPmtReason(requiredDataDobj.getDupPmtReason());
                }

                if (!CommonUtils.isNullOrBlank(String.valueOf(requiredDataDobj.getPmt_type()))) {
                    pmtDobj.setPmt_type_code(String.valueOf(requiredDataDobj.getPmt_type()));
                }
                if (!CommonUtils.isNullOrBlank(String.valueOf(requiredDataDobj.getPmt_catg()))) {
                    pmtDobj.setPmtCatg(String.valueOf(requiredDataDobj.getPmt_catg()));
                }
                pmtDobj.setTransPurCd(requiredDataDobj.getTrans_pur_cd());
                Date firDateOrComplainDate = null;
                if (ownerdobj.getBlackListedVehicleDobj() != null && ownerdobj.getBlackListedVehicleDobj().getFirDate() != null) {
                    firDateOrComplainDate = ownerdobj.getBlackListedVehicleDobj().getFirDate();
                } else if (ownerdobj.getBlackListedVehicleDobj() != null && ownerdobj.getBlackListedVehicleDobj().getComplain_dt() != null) {
                    firDateOrComplainDate = ownerdobj.getBlackListedVehicleDobj().getComplain_dt();
                }
                VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(ownerdobj, pmtDobj, pDay, pMonth, pCMonth, pYear, exem_amount, fine_to_be_taken, monthly_tax_amt, rLength);
                parameters.setDAYSWITHINSTATE(pDay);
                parameters.setAUTHYEAR(authYear);
                parameters.setREGN_STATE(String.valueOf(regn_state));
                if (isCondition(FormulaUtils.replaceTagPermitValues(rs.getString("condition_formula"), parameters), "getPmtFeeDetailsFromSlab")) {
                    dobj.setPermitAmt(String.valueOf(Integer.valueOf(getPmtFeeValueFormSlab(FormulaUtils.replaceTagPermitValues(rs.getString("fee_rate_formula"), parameters), tmgr).trim())));
                    if (!isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("exempt_fine_vh_blacklist"), parameters), "getPmtFeeDetailsFromSlab")
                            || (firDateOrComplainDate != null && firDateOrComplainDate.after(JSFUtils.getStringToDateddMMMyyyy(requiredDataDobj.getValidUptoTemp())))) {
                        if (rs.getBoolean("check_max_amt")) {
                            int maxAmt = Integer.valueOf(getPmtMaxFineValue(FormulaUtils.replaceTagPermitValues(rs.getString("fine_max_amt"), parameters), tmgr));
                            dobj.setPenalty(String.valueOf(Integer.valueOf(getPmtFeeValueFormSlab(FormulaUtils.replaceTagPermitValues(rs.getString("fine_rate_formula"), parameters), tmgr).trim())));
                            if (maxAmt < Integer.valueOf(getPmtFeeValueFormSlab(FormulaUtils.replaceTagPermitValues(rs.getString("fine_rate_formula"), parameters), tmgr).trim())) {
                                dobj.setPenalty(String.valueOf(maxAmt));
                            }
                        } else {
                            dobj.setPenalty(String.valueOf(Integer.valueOf(getPmtFeeValueFormSlab(FormulaUtils.replaceTagPermitValues(rs.getString("fine_rate_formula"), parameters), tmgr).trim())));
                        }
                    } else {
                        dobj.setPenalty("0");
                    }
                    dobj.setPermitHead(ServerUtil.getTaxHead(rs.getInt("trans_pur_cd")));
                    dobj.setPurCd(String.valueOf(rs.getInt("trans_pur_cd")));
                    dobj.setPermitHeadDisable(true);
                    dobj.setDisableMinusBt(true);
                    feeShowDetail.add(new PermitShowFeeDetailDobj(dobj));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Appl_no " + requiredDataDobj.getAppl_no() + "--" + e.toString() + " " + e.getStackTrace()[0]);
        }
        return feeShowDetail;
    }

    public static Map<String, Integer> calculateAge(Date birthDate, Date uptoDate) {
        int years = 0;
        int months = 0;
        int days = 0;
// create calendar object for birth day
        Calendar birthDay = Calendar.getInstance();
        birthDay.setTimeInMillis(birthDate.getTime());
// create calendar object for current day
// long currentTime = System.currentTimeMillis();
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(uptoDate.getTime());
// Get difference between years
        years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        int currMonth = now.get(Calendar.MONTH) + 1;
        int birthMonth = birthDay.get(Calendar.MONTH) + 1;
// Get difference between months
        months = currMonth - birthMonth;
// if month difference is in negative then reduce years by one and
// calculate the number of months.
        if (months < 0) {
            years--;
            months = 12 - birthMonth + currMonth;
            if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
                months--;
            }
        } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
            years--;
            months = 11;
        }
// Calculate the days
        if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE)) {
            days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE);
        } else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
            int today = now.get(Calendar.DAY_OF_MONTH);
            now.add(Calendar.MONTH, -1);
            days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today;
        } else {
            days = 0;
            if (months == 12) {
                years++;
                months = 0;
            }
        }
        HashMap<String, Integer> ageMap = new HashMap<String, Integer>();
        ageMap.put("YEARS", years);
        ageMap.put("MONTHS", months);
        ageMap.put("DAYS", days);
        return ageMap;
// Create new Age object
// return new Age(days, months, years);
    }

    public int getDaysWithinState(String state_cd, String appl_no, int noOfDays) throws DateUtilsException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        Date[] journy = new Date[4];
        String sqlString;
        RowSet rs;
        try {
            tmgr = new TransactionManager("getDaysWithinState");
            sqlString = "Select journey_dt,sr_no from " + TableList.VA_SPL_ROUTE
                    + " where state_cd = ? and  appl_no = ? order by sr_no ";
            ps = tmgr.prepareStatement(sqlString);
            int i = 1;
            ps.setString(i++, state_cd);
            ps.setString(i, appl_no);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                journy[rs.getInt("sr_no") - 1] = rs.getDate("journey_dt");
            }
            if (journy[0] != null) {
                int total = (int) DateUtils.getDate1MinusDate2_Days(journy[0], journy[1]) + 1;
                int outerRegion = (int) DateUtils.getDate1MinusDate2_Days(journy[2], journy[3]) + 1;
                int result = total + outerRegion;
                if (journy[2].equals(journy[1])) {
                    result = result - 1;
                }
                noOfDays = result;
            }

        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
        return noOfDays;
    }

    public int getMonthlyTaxAmt(String regn_no) {
        int taxAmt = 0;
        int month = 0;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getToTaxAmt");
            String sql = "select * from " + TableList.VT_TAX + " where regn_no=? and  state_cd=? and pur_cd in (?,?)  and tax_mode <> ? order by rcpt_dt desc limit 2";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, TableConstants.TM_ROAD_TAX);
            ps.setInt(4, TableConstants.TM_ADDN_ROAD_TAX);
            ps.setString(5, "B");
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                month = DateUtils.getDate1MinusDate2_Months(rs.getDate("tax_from"), rs.getDate("tax_upto"));
                taxAmt = taxAmt + (int) Math.ceil((double) (rs.getInt("tax_amt") / month));

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

        return taxAmt;

    }

    public int getDate1MinusDate2_Months(Date date1, Date date2)
            throws DateUtilsException {

        // Check input
        if (date1 == null || date2 == null) {
            throw new DateUtilsException("DEV_ERROR : Check the dates '" + date1 + "', '" + date2 + "'");
        }

        int monthDiff = 0;
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);

        int day1 = calendar1.get(Calendar.DAY_OF_MONTH);
        int day2 = calendar2.get(Calendar.DAY_OF_MONTH);
        int month1 = calendar1.get(Calendar.MONTH);
        int month2 = calendar2.get(Calendar.MONTH);
        int year1 = calendar1.get(Calendar.YEAR);
        int year2 = calendar2.get(Calendar.YEAR);

        if (month1 == month2) {
            if (year1 == year2) {
                monthDiff = 1;
            } else {
                monthDiff = ((year2 - year1) * 12) + 1;
            }
        } else {
            if (year1 == year2) {
                monthDiff = month2 - month1 + 1;
            } else {
                int totalMonthDiff = (year2 - year1) * 12;
                int tempMonthDiff = month2 - month1 + 1;
                monthDiff = totalMonthDiff + tempMonthDiff;
            }
        }
        return monthDiff;
    }

    public String getPmtFeeValueFormSlab(String slabValue, TransactionManager tmgr) {
        String fee = "0";
        try {
            if (slabValue != null && !slabValue.isEmpty()) {
                String Query = "SELECT " + slabValue + " as feeDtls";
                PreparedStatement ps = tmgr.prepareStatement(Query);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    fee = rs.getString("feeDtls");
                    if (CommonUtils.isNullOrBlank(fee) || fee.equalsIgnoreCase("0")) {
                        fee = "0";
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return fee;
    }

    public String getPmtMaxFineValue(String slabValue, TransactionManager tmgr) {
        String maxFine = "0";
        try {
            if (!CommonUtils.isNullOrBlank(slabValue)) {
                String Query = "SELECT " + slabValue + " as maxFine";
                PreparedStatement ps = tmgr.prepareStatement(Query);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    maxFine = rs.getString("maxFine");
                    if (CommonUtils.isNullOrBlank(maxFine) || maxFine.equalsIgnoreCase("0")) {
                        maxFine = "0";
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return maxFine;
    }

    public int[] getPermitExpireDays(String regnNo, TransactionManager tmgr, int purCd) {
        int[] expireDay = new int[3];
        String Query;
        PreparedStatement ps;
        RowSet rs;
        try {
            Query = "select (current_date - exem_to_date) as days,exem_amount,fine_to_be_taken from " + TableList.VT_EXEMPTION + " where regn_no = ? AND pur_cd = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regnNo);
            ps.setInt(2, TableConstants.VM_PMT_EXEMPTION_PUR_CD);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()
                    && (purCd == TableConstants.VM_PMT_RENEWAL_PUR_CD
                    || purCd == TableConstants.VM_PMT_SURRENDER_PUR_CD
                    || purCd == TableConstants.VM_PMT_TRANSFER_PUR_CD
                    || purCd == TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD
                    || purCd == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD
                    || purCd == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD
                    || purCd == TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD)) {
                expireDay[0] = rs.getInt("days");
                expireDay[1] = rs.getInt("exem_amount");
                expireDay[2] = rs.getInt("fine_to_be_taken");
            } else {
                Query = "SELECT DATE_PART('day',current_date - max(a.valid_upto))::int as days from\n"
                        + "(SELECT valid_upto as valid_upto  from " + TableList.VT_PERMIT + " where regn_no = ?\n"
                        + "union\n"
                        + "SELECT rcpt_dt as valid_upto from " + TableList.VT_FEE + " where regn_no = ? AND state_cd = 'DL' AND pur_cd in (?,?,?,?,?)) a";
                ps = tmgr.prepareStatement(Query);
                int i = 1;
                ps.setString(i++, regnNo);
                ps.setString(i++, regnNo);
                ps.setInt(i++, TableConstants.VM_PMT_SURRENDER_PUR_CD);
                ps.setInt(i++, TableConstants.VM_PMT_TRANSFER_PUR_CD);
                ps.setInt(i++, TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD);
                ps.setInt(i++, TableConstants.VM_PMT_REPLACE_VEH_PUR_CD);
                ps.setInt(i++, TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    expireDay[0] = rs.getInt("days");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return expireDay;
    }

    public int getHomeAuthExpireDays(String regnNo, TransactionManager tmgr, int purCd) {
        int expireDay = 0;
        String Query;
        PreparedStatement ps;
        RowSet rs;
        try {
            Query = "select (current_date - exem_to_date) as days,exem_amount,fine_to_be_taken from " + TableList.VT_EXEMPTION + " where regn_no = ? AND pur_cd = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regnNo);
            ps.setInt(2, TableConstants.VM_PMT_EXEMPTION_PUR_CD);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()
                    && (purCd == TableConstants.VM_PMT_RENEWAL_PUR_CD
                    || purCd == TableConstants.VM_PMT_SURRENDER_PUR_CD
                    || purCd == TableConstants.VM_PMT_TRANSFER_PUR_CD
                    || purCd == TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD
                    || purCd == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD
                    || purCd == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD
                    || purCd == TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD)) {
                expireDay = rs.getInt("days");
            } else {
                Query = "SELECT DATE_PART('day',current_date - max(a.valid_upto))::int as days from \n"
                        + "  ((SELECT auth_to as valid_upto  from " + TableList.VT_PERMIT_HOME_AUTH + "  where regn_no = ? order by auth_to desc limit 1) \n"
                        + "  union \n"
                        + "  (SELECT rcpt_dt as valid_upto from " + TableList.VT_FEE + "  where regn_no = ? AND state_cd = 'DL' AND pur_cd in (?,?,?,?,?) and fine > 0)) a";
                ps = tmgr.prepareStatement(Query);
                int i = 1;
                ps.setString(i++, regnNo);
                ps.setString(i++, regnNo);
                ps.setInt(i++, TableConstants.VM_PMT_SURRENDER_PUR_CD);
                ps.setInt(i++, TableConstants.VM_PMT_TRANSFER_PUR_CD);
                ps.setInt(i++, TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD);
                ps.setInt(i++, TableConstants.VM_PMT_REPLACE_VEH_PUR_CD);
                ps.setInt(i++, TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    expireDay = rs.getInt("days");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return expireDay;
    }

    public String getPerRegionCode(String applNo, TransactionManager tmgr, String tablename) {
        String regionCount = "";
        try {
            String Query = "select replace(region_covered,' ','') as region_covered from " + tablename + " where appl_no = ?";
            PreparedStatement ps = tmgr.prepareStatement(Query);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (!CommonUtils.isNullOrBlank(rs.getString(1))) {
                    regionCount = rs.getString(1).substring(0, rs.getString(1).length() - 1);
                }
            }
        } catch (SQLException | NumberFormatException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return regionCount;
    }

    public int getPerRegionCount(String applNo, TransactionManager tmgr) {
        int regionCount = 0;
        try {
            String Query = "select region_covered from " + TableList.VA_PERMIT + " where appl_no = ?";
            PreparedStatement ps = tmgr.prepareStatement(Query);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                String region = rs.getString(1);
                if (!CommonUtils.isNullOrBlank(region)) {
                    String[] temp = region.split(",");
                    regionCount = temp.length;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return regionCount;
    }

    public int getPerRegion(String applNo, TransactionManager tmgr, String tablename) {
        int regionCount = 0;
        try {
            String Query = "select region_covered from " + tablename + " where appl_no = ?";
            PreparedStatement ps = tmgr.prepareStatement(Query);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                String region = rs.getString(1);
                if (!CommonUtils.isNullOrBlank(region)) {
                    if (region.split(",").length > 1) {
                        regionCount = 0;
                    } else {
                        region = region.replaceAll(",", "");
                        if (JSFUtils.isNumeric(region)) {
                            regionCount = Integer.parseInt(region);
                        } else {
                            regionCount = 0;
                        }
                    }
                }
            }
        } catch (SQLException | NumberFormatException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return regionCount;
    }

    public int getTempRegionCount(String applNo, TransactionManager tmgr) {
        int regionCount = 0;
        try {
            String Query = "select region_covered,route_cd from " + TableList.VA_TEMP_PERMIT_ROUTE + " where appl_no = ?";
            PreparedStatement ps = tmgr.prepareStatement(Query);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                String region = rs.getString("region_covered");
                if (!CommonUtils.isNullOrBlank(region)) {
                    String[] temp = region.split(",");
                    regionCount = temp.length;
                } else {
                    Query = "SELECT b.regions_covered FROM " + TableList.VA_TEMP_PERMIT_ROUTE + " a inner join " + TableList.VM_ROUTE_MASTER + " b \n"
                            + " on a.state_cd=b.state_cd and a.off_cd=b.off_cd and a.route_cd=b.code \n"
                            + " where a.route_cd = ?  and a.state_cd = ? and a.off_cd = ? limit 1";
                    ps = tmgr.prepareStatement(Query);
                    ps.setString(1, rs.getString("route_cd"));
                    ps.setString(2, Util.getUserStateCode());
                    ps.setInt(3, Util.getUserOffCode());
                    RowSet rset = tmgr.fetchDetachedRowSet_No_release();
                    if (rset.next()) {
                        regionCount = rset.getInt("regions_covered");
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return regionCount;
    }

    public int getTempStateCount(String applNo, TransactionManager tmgr) {
        int regionCount = 0;
        try {
            String Query = "select region_covered from " + TableList.VA_TEMP_PERMIT_ROUTE + " where appl_no = ?";
            PreparedStatement ps = tmgr.prepareStatement(Query);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                String region = rs.getString(1);
                if ((!CommonUtils.isNullOrBlank(region)) && JSFUtils.isAlphabet(String.valueOf(region.charAt(0)))) {
                    String[] temp = region.split(",");
                    regionCount = temp.length;

                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return regionCount;
    }

    public Date getPrvValidUpto(String regn_no, TransactionManager tmgr) {
        Date prv_valid_upto = null;
        try {
            String Query = "select max(valid_upto)valid_upto from " + TableList.VT_TEMP_PERMIT + "    where regn_no =? and state_cd=? and pur_cd=?";
            PreparedStatement ps = tmgr.prepareStatement(Query);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, TableConstants.VM_PMT_SPECIAL_PUR_CD);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                prv_valid_upto = rs.getDate("valid_upto");
            }
        } catch (Exception e) {
            LOGGER.error("getPrvValidUpto" + e.toString() + " " + e.getStackTrace()[0]);
        }
        return prv_valid_upto;
    }

    public String checkMultiRegion(String applNo, TransactionManager tmgr) {
        String multiRegion = "false";
        PreparedStatement ps;
        RowSet rs;
        String Query = "";
        try {
            Query = "select region_covered,state_cd,off_cd from " + TableList.VA_PERMIT + " where appl_no = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, applNo);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                String region = rs.getString(1);
                if (!CommonUtils.isNullOrBlank(region)) {
                    region = region.substring(0, (region.length() - 1));
                    Integer.parseInt(region);
                    Query = "select regions_covered from " + TableList.VM_REGION + " where region_cd = ANY(string_to_array(?, ',')::numeric[]) and state_cd = ? and off_cd = ?";
                    ps = tmgr.prepareStatement(Query);
                    ps.setString(1, region);
                    ps.setString(2, rs.getString("state_cd"));
                    ps.setInt(3, rs.getInt("off_cd"));
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    while (rs.next()) {
                        if (rs.getInt("regions_covered") == 99) {
                            multiRegion = "true";
                            break;
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            return "false";
        } catch (NullPointerException e) {
            return "false";
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return multiRegion;
    }

    public int getPerRouteCount(String applNo, TransactionManager tmgr) {
        int routeCount = 0;
        try {
            String Query = "select count(1) from " + TableList.va_permit_route + " where appl_no = ?";
            PreparedStatement ps = tmgr.prepareStatement(Query);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                routeCount = rs.getInt(1);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return routeCount;
    }

    public int getDayDiffForNewPermit(String regnNo, TransactionManager tmgr) {
        int expireDay = 0;
        String Query;
        PreparedStatement ps;
        RowSet rs;
        try {
            if (!regnNo.equalsIgnoreCase("NEW")) {
                Query = "SELECT COALESCE(DATE_PART('day',current_date - max(a.moved_on))::int,0) as days from\n"
                        + "(SELECT moved_on as moved_on  from vh_conversion where new_regn_no = ?\n"
                        + "union\n"
                        + "SELECT moved_on as moved_on from vh_re_assign where new_regn_no = ?) a";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, regnNo);
                ps.setString(2, regnNo);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    expireDay = rs.getInt("days");
                    if (expireDay == 0) {
                        Query = "select current_date-op_dt::date as day from " + TableList.VT_OTHER_STATE_VEH + " where new_regn_no = ? order by op_dt DESC limit 1";
                        ps = tmgr.prepareStatement(Query);
                        ps.setString(1, regnNo);
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        if (rs.next()) {
                            expireDay = rs.getInt("day");
                        } else {
                            Query = "select current_date-regn_dt as day from " + TableList.VT_OWNER + " where regn_no = ?";
                            ps = tmgr.prepareStatement(Query);
                            ps.setString(1, regnNo);
                            rs = tmgr.fetchDetachedRowSet_No_release();
                            if (rs.next()) {
                                expireDay = rs.getInt("day");
                            }
                        }
                    }
                }
                Query = "select current_date - moved_on::date as day from " + TableList.VHA_PERMIT_TRANSACTION + " where regn_no = ? AND pur_cd in (?,?) AND trans_pur_cd in (?,?,?,?) order by moved_on DESC limit 1";
                ps = tmgr.prepareStatement(Query);
                int i = 1;
                ps.setString(i++, regnNo);
                ps.setInt(i++, TableConstants.VM_PMT_SURRENDER_PUR_CD);
                ps.setInt(i++, TableConstants.VM_PMT_RESTORE_PUR_CD);
                ps.setInt(i++, TableConstants.VM_PMT_CANCELATION_PUR_CD);
                ps.setInt(i++, TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD);
                ps.setInt(i++, TableConstants.VM_PMT_TRANSFER_PUR_CD);
                ps.setInt(i++, TableConstants.VM_PMT_REPLACE_VEH_PUR_CD);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    expireDay = rs.getInt("day");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return expireDay;
    }

    public String[] getAdditionalFeePurCD(String stateCd, int purCd) {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String[] purCD = null;
        String sqlString;
        RowSet rs;
        try {
            tmgr = new TransactionManager("getAdditionalFeePurCD");
            sqlString = "SELECT  add_pur_cd FROM permit.vm_permit_additional_fee where state_cd = ? AND pur_cd = ?";
            ps = tmgr.prepareStatement(sqlString);
            int i = 1;
            ps.setString(i++, stateCd);
            ps.setInt(i++, purCd);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                purCD = rs.getString("add_pur_cd").split(",");
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
        return purCD;
    }

    public boolean checkPurCDExiatance(String applno, int purCD) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        boolean isExist = false;
        String vaStatusSelectSql = " select * from va_status where appl_no=? and pur_cd=?";
        try {
            tmgr = new TransactionManager("saveFeeDetailsInstrument");
            ps = tmgr.prepareStatement(vaStatusSelectSql);
            ps.setString(1, applno);
            ps.setInt(2, purCD);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isExist = true;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return isExist;

    }

    public int getMultiDocument(String regnNo, String applNo, TransactionManager tmgr) {
        int multiDoc = 0;
        PreparedStatement ps;
        RowSet rs;
        try {
            String Query = "SELECT doc_id from " + TableList.VA_DUP_DOCLIST + " where appl_no=? and regn_no = ?";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, applNo);
            ps.setString(i++, regnNo);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                multiDoc = rs.getString("doc_id").split(",").length;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return multiDoc;
    }

    public Date validFromDateForRenPermit(String regnNo) {
        Date validFrom = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("ValidFromDateForRenPermit");
            String Query = "SELECT valid_upto::date,current_date as current_date from permit.vt_permit where state_cd=? and regn_no = ?";
            PreparedStatement ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, Util.getUserStateCode());
            ps.setString(i++, regnNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (rs.getDate("valid_upto").compareTo(rs.getDate("current_date")) == 1) {
                    validFrom = ServerUtil.dateRange(rs.getDate("valid_upto"), 0, 0, 1);
                } else {
                    validFrom = rs.getDate("current_date");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return validFrom;
    }

    public Integer getServicesChargesForPermit(String stateCode, int purCd, int vh_class, int pmtType, String vh_catg) throws VahanException {
        int amount = 0;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        VehicleParameters vehParameters = new VehicleParameters();
        try {
            vehParameters.setPUR_CD(purCd);
            vehParameters.setVH_CLASS(vh_class);
            vehParameters.setPERMIT_TYPE(pmtType);
            vehParameters.setVCH_CATG(vh_catg);
            String sql = "Select amount,condition_formula from vm_feemast_service where  state_cd=? and pur_cd = ?";
            tmgr = new TransactionManager("getAmount");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCode);
            ps.setInt(2, 99);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "vm_feemast_service(State:" + stateCode + ",PurCd:" + purCd + ")")) {
                    amount = rs.getInt("amount");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    throw new VahanException(e.getMessage());
                }
            }
        }
        return amount;
    }

    public boolean getPerRegionFlag(String stateCode, int pmtType, int purCd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        boolean flag = false;
        try {
            String sql = "SELECT per_region_flag from " + TableList.VM_PERMIT_FEE_STATE_CONFIGURATION + " where state_cd = ? and pur_cd = ? and pmt_type = ?";
            tmgr = new TransactionManager("getPerRegionFlag");
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, stateCode);
            ps.setInt(i++, purCd);
            ps.setInt(i++, pmtType);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                flag = rs.getBoolean("per_region_flag");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    throw new VahanException(e.getMessage());
                }
            }
        }
        return flag;
    }

    public Map<String, String> getOnlinePermitFeeDtls(String regn_no, String appl_no) throws VahanException {
        Map<String, String> onlinePermitFee = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        RowSet rs = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManager("getOnlinePermitFeeDtls");
            String query = "select a.regn_no,a.pur_cd,b.rcpt_no,string_agg(c.pur_cd::text,',') as trans_pur_cd,"
                    + " string_agg((c.fees > 0)::text,',') as fees,string_agg((c.fine > 0)::text,',') as fine"
                    + " from onlineschema.va_details_appl a"
                    + " inner join vp_appl_rcpt_mapping b on a.state_cd=b.state_cd and b.appl_no = a.appl_no"
                    + " left outer join vt_fee c on  a.state_cd=c.state_cd and a.off_cd = c.off_cd and b.rcpt_no = c.rcpt_no "
                    + " where a.state_cd = ? and a.appl_no = ? and a.regn_no = ?  group by 1,2,3";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, appl_no);
            ps.setString(3, regn_no);
            rs = tmgr.fetchDetachedRowSet();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
                String[] trans = rs.getString("trans_pur_cd").split(",");
                String[] fee = rs.getString("fees").split(",");
                for (int i = 0; i < fee.length; i++) {
                    onlinePermitFee.put(trans[i], fee[i]);
                }
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    onlinePermitFee.put(rsmd.getColumnName(i), rs.getString(i));
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Some error occured while getting permit fee record from online permit. ");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Some error occured while getting permit fee record from online permit. ");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return onlinePermitFee;
    }

    public PermitFeeRequiredDataDobj getLeasePermitCase(String applNo, int pur_cd) {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        PermitFeeRequiredDataDobj requiredDataDobj = null;
        String sqlString;
        RowSet rs;
        try {
            tmgr = new TransactionManager("getLeasePermitCase");
            sqlString = "select a.*,b.* from permit.va_lease_permit a inner join vt_owner b on a.m_regn_no=b.regn_no where appl_no =?";
            ps = tmgr.prepareStatement(sqlString);
            int i = 1;
            ps.setString(i++, applNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                requiredDataDobj = new PermitFeeRequiredDataDobj();
                requiredDataDobj.setState_cd(rs.getString("state_cd"));
                requiredDataDobj.setPur_cd(pur_cd);
                requiredDataDobj.setPmt_type(0);
                requiredDataDobj.setPeriod_mode(rs.getString("period_mode"));
                requiredDataDobj.setPeriod(rs.getInt("period"));
                requiredDataDobj.setVh_class(rs.getInt("vh_class"));
                requiredDataDobj.setSeat_cap(rs.getInt("seat_cap"));
                requiredDataDobj.setUnld_wt(rs.getInt("unld_wt"));
                requiredDataDobj.setLd_wt(rs.getInt("ld_wt"));
                requiredDataDobj.setStand_cap(rs.getInt("stand_cap"));
                requiredDataDobj.setSleeper_cap(rs.getInt("sleeper_cap"));
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
        return requiredDataDobj;
    }

    public PermitFeeRequiredDataDobj getCounterSignatureAuth(String applNo, int pur_cd) throws VahanException {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        PermitFeeRequiredDataDobj requiredDataDobj = null;
        String sqlString;
        RowSet rs;
        try {
            tmgr = new TransactionManager("getCounterSignatureAuth");
            sqlString = "select pmt.state_cd,pmt.pmt_type,pmt.period_mode,pmt.period,own.vh_class,own.seat_cap,own.unld_wt,own.ld_wt,own.stand_cap,own.sleeper_cap from " + TableList.VA_PERMIT_COUNTERSIGNATURE_AUTHORIZATION + " pmt \n"
                    + "  inner join " + TableList.VT_OWNER + " own on  own.state_cd = pmt.state_cd and own.regn_no = pmt.regn_no where pmt.appl_no =?";
            ps = tmgr.prepareStatement(sqlString);
            int i = 1;
            ps.setString(i++, applNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                requiredDataDobj = new PermitFeeRequiredDataDobj();
                requiredDataDobj.setState_cd(rs.getString("state_cd"));
                requiredDataDobj.setPur_cd(pur_cd);
                requiredDataDobj.setPmt_type(rs.getInt("pmt_type"));
                requiredDataDobj.setPeriod_mode(rs.getString("period_mode"));
                requiredDataDobj.setPeriod(rs.getInt("period"));
                requiredDataDobj.setVh_class(rs.getInt("vh_class"));
                requiredDataDobj.setSeat_cap(rs.getInt("seat_cap"));
                requiredDataDobj.setUnld_wt(rs.getInt("unld_wt"));
                requiredDataDobj.setLd_wt(rs.getInt("ld_wt"));
                requiredDataDobj.setStand_cap(rs.getInt("stand_cap"));
                requiredDataDobj.setSleeper_cap(rs.getInt("sleeper_cap"));
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
                throw new VahanException("Details not found please check again.");
            }
        }
        return requiredDataDobj;
    }
}
