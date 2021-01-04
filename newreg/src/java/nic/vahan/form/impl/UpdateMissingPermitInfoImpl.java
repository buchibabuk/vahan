/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import nic.vahan.db.TableConstants;
import nic.vahan.db.connection.TransactionManagerReadOnly;

/**
 *
 * @author Divya Kamboj
 */
public class UpdateMissingPermitInfoImpl {

    private static final Logger LOGGER = Logger.getLogger(UpdateMissingPermitInfoImpl.class);

    public boolean saveMissingPermitInfo(PassengerPermitDetailDobj updateMissingPermitInfoDobj, VehicleParameters vehParameters, int actionCd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String query = "";
        String applNo = "";
        boolean flag = false;
        String state_cd = updateMissingPermitInfoDobj.getState_cd();
        int off_cd = updateMissingPermitInfoDobj.getOff_cd() != null ? Integer.parseInt(updateMissingPermitInfoDobj.getOff_cd()) : 0;
        String regn_no = updateMissingPermitInfoDobj.getRegnNo().trim().toUpperCase();
        int pmt_type = updateMissingPermitInfoDobj.getPmt_type_code() != null ? Integer.parseInt(updateMissingPermitInfoDobj.getPmt_type_code()) : 0;
        int pmt_catg = !CommonUtils.isNullOrBlank(updateMissingPermitInfoDobj.getPmtCatg()) ? Integer.parseInt(updateMissingPermitInfoDobj.getPmtCatg()) : 0;
        int service_type = !CommonUtils.isNullOrBlank(updateMissingPermitInfoDobj.getServices_TYPE()) && updateMissingPermitInfoDobj.getServices_TYPE() != null ? Integer.parseInt(updateMissingPermitInfoDobj.getServices_TYPE()) : 0;
        int rout_length = (updateMissingPermitInfoDobj.getRout_length() != null && !updateMissingPermitInfoDobj.getRout_length().equalsIgnoreCase("")) ? Integer.parseInt(updateMissingPermitInfoDobj.getRout_length()) : 0;
        int number_trips = (updateMissingPermitInfoDobj.getNumberOfTrips() != null && !updateMissingPermitInfoDobj.getNumberOfTrips().equalsIgnoreCase("")) ? Integer.parseInt(updateMissingPermitInfoDobj.getNumberOfTrips()) : 0;
        int domain_cd = (updateMissingPermitInfoDobj.getDomain_CODE() != null && !updateMissingPermitInfoDobj.getDomain_CODE().equalsIgnoreCase("-1")) ? Integer.parseInt(updateMissingPermitInfoDobj.getDomain_CODE()) : 0;
        try {
            tmgr = new TransactionManager("saveMissingPermitInfo");
            if (actionCd == TableConstants.UPDATE_MISSING_PERMIT_ENTRY || regn_no == null || regn_no.isEmpty()) {
                applNo = ServerUtil.getUniqueApplNo(tmgr, state_cd);
                if (!CommonUtils.isNullOrBlank(applNo)) {
                    query = " INSERT INTO " + TableList.VA_TAX_BASED_ON_PERMIT_INFO + "( "
                            + "    state_cd, off_cd, regn_no, appl_No, pmt_type, pmt_catg, service_type, "
                            + "    route_class, route_length, no_of_trips, domain_cd, distance_run_in_quarter,op_dt) "
                            + "    VALUES (?, ?, ?, ?, ?, ?, ?, "
                            + "     ?, ?, ?, ?, ?, current_timestamp) ";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, state_cd);
                    ps.setInt(2, off_cd);
                    ps.setString(3, regn_no);
                    ps.setString(4, applNo);
                    ps.setInt(5, pmt_type);
                    ps.setInt(6, pmt_catg);
                    ps.setInt(7, service_type);
                    ps.setInt(8, 0);
                    ps.setInt(9, rout_length);
                    ps.setInt(10, number_trips);
                    ps.setInt(11, domain_cd);
                    ps.setInt(12, 0);
                    ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                    Status_dobj status = new Status_dobj();
                    status.setPur_cd(TableConstants.MISSING_PERMIT_INFO_PUR_CD);
                    status.setAppl_no(applNo);
                    status.setRegn_no(regn_no);
                    status.setOff_cd(off_cd);
                    status.setState_cd(state_cd);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                    String dt = sdf.format(new Date());
                    status.setAppl_dt(dt);
                    status.setEmp_cd(0);
                    status.setOffice_remark("");
                    status.setPublic_remark("");
                    int initialFlow[] = ServerUtil.getInitialAction(tmgr, status.getState_cd(), status.getPur_cd(), vehParameters);
                    status.setFlow_slno(initialFlow[1]);
                    status.setFile_movement_slno(initialFlow[1]);
                    status.setAction_cd(initialFlow[0]);
                    ServerUtil.fileFlowForNewApplication(tmgr, status);
                    status.setStatus("C");
                    status = ServerUtil.webServiceForNextStage(status, tmgr);
                    ServerUtil.fileFlow(tmgr, status);
                    tmgr.commit();
                    flag = true;
                } else {
                    throw new VahanException("ERROR IN GENERATING APPLICATION NUMBER");
                }
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in inserting data.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.equals(e);
            }
        }
        return flag;
    }

    public PassengerPermitDetailDobj getSpareTaxParameterDetailsFromVa(String appl_no) throws VahanException {
        PassengerPermitDetailDobj dobj = null;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManagerReadOnly("getSpareTaxParameterDetailsFromVa");
            String query;
            if (appl_no != null || !appl_no.isEmpty()) {
                query = " select * from " + TableList.VA_TAX_BASED_ON_PERMIT_INFO + "  where appl_no=? ";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, appl_no);
                RowSet rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    dobj = new PassengerPermitDetailDobj();
                    dobj.setDomain_CODE(rs.getString("domain_cd"));
                    dobj.setPmt_type_code(rs.getString("pmt_type"));
                    dobj.setPmtCatg(rs.getString("pmt_catg"));
                    dobj.setServices_TYPE(rs.getString("service_type"));
                    dobj.setRout_length(rs.getString("route_length"));
                    dobj.setRout_code(rs.getString("route_class"));
                    dobj.setNumberOfTrips(rs.getString("no_of_trips"));
                    dobj.setRegnNo(rs.getString("regn_no"));
                    dobj.setApplNo(rs.getString("appl_no"));
                    dobj.setOff_cd(rs.getString("off_cd"));
                    dobj.setState_cd(rs.getString("state_cd"));

                }
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Some Error occurred while fetching the Record");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return dobj;
    }


    public void insertIntoVhaFromVa(PassengerPermitDetailDobj updateMissingPermitInfoDobj, String changeData, String empCd, String applNo, String stateCd, int offCd, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        String query = null;
        try {
            query = "INSERT INTO " + TableList.VHA_TAX_BASED_ON_PERMIT_INFO + " (SELECT current_timestamp,?,* FROM " + TableList.VA_TAX_BASED_ON_PERMIT_INFO + " WHERE appl_no=? and state_cd= ? and off_cd= ?)";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, empCd);
            ps.setString(2, applNo);
            ps.setString(3, stateCd);
            ps.setInt(4, offCd);
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

            query = " UPDATE vahan4.va_tax_based_on_permit_info "
                    + "   SET pmt_type=?, pmt_catg=?, service_type=?, route_class=?, route_length=?, no_of_trips=?, "
                    + "       domain_cd=?  WHERE appl_no = ? and state_cd = ? and off_cd = ?";
            ps = tmgr.prepareStatement(query);
            int i = 1;

            ps.setInt(i++, !CommonUtils.isNullOrBlank(updateMissingPermitInfoDobj.getPmt_type_code()) ? Integer.parseInt(updateMissingPermitInfoDobj.getPmt_type_code()) : 0);
            ps.setInt(i++, !CommonUtils.isNullOrBlank(updateMissingPermitInfoDobj.getPmtCatg()) ? Integer.parseInt(updateMissingPermitInfoDobj.getPmtCatg()) : 0);
            ps.setInt(i++, !CommonUtils.isNullOrBlank(updateMissingPermitInfoDobj.getServices_TYPE()) ? Integer.parseInt(updateMissingPermitInfoDobj.getServices_TYPE()) : 0);
            ps.setInt(i++, !CommonUtils.isNullOrBlank(updateMissingPermitInfoDobj.getRout_code()) ? Integer.parseInt(updateMissingPermitInfoDobj.getRout_code()) : 0);
            ps.setInt(i++, !CommonUtils.isNullOrBlank(updateMissingPermitInfoDobj.getRout_length()) ? Integer.parseInt(updateMissingPermitInfoDobj.getRout_length()) : 0);
            ps.setInt(i++, !CommonUtils.isNullOrBlank(updateMissingPermitInfoDobj.getNumberOfTrips()) ? Integer.parseInt(updateMissingPermitInfoDobj.getNumberOfTrips()) : 0);
            ps.setInt(i++, !CommonUtils.isNullOrBlank(updateMissingPermitInfoDobj.getDomain_CODE()) ? Integer.parseInt(updateMissingPermitInfoDobj.getDomain_CODE()) : 0);
            ps.setString(i++, applNo);
            ps.setString(i++, stateCd);
            ps.setInt(i++, offCd);
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
            ComparisonBeanImpl.updateChangedData(updateMissingPermitInfoDobj.getApplNo(), changeData, tmgr);
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public void saveChangeData(PassengerPermitDetailDobj updateMissingPermitInfoDobj, String changeData, String empCd, String applNo, String stateCd, int offCd) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getMissingPemitDetails");
            insertIntoVhaFromVa(updateMissingPermitInfoDobj, changeData, empCd, applNo, stateCd, offCd, tmgr);
            tmgr.commit();
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in save change data.");
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

    public void saveChangeDataAndFileMove(PassengerPermitDetailDobj updateMissingPermitInfoDobj, String changeData, String empCd, String applNo, String stateCd, int offCd, Status_dobj status_dobj) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String query = "";
        try {
            tmgr = new TransactionManager("getMissingPemitDetails");
            if (!CommonUtils.isNullOrBlank(changeData)) {
                insertIntoVhaFromVa(updateMissingPermitInfoDobj, changeData, empCd, applNo, stateCd, offCd, tmgr);
            }

            if (status_dobj.getAction_cd() == TableConstants.UPDATE_MISSING_PERMIT_APPROVAL && status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_COMPLETE)) {
                query = "select regn_no from " + TableList.VT_TAX_BASED_ON_PERMIT_INFO + " WHERE  state_cd= ? and regn_no = ? ";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, stateCd);
                ps.setString(2, updateMissingPermitInfoDobj.getRegnNo());
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    query = "INSERT INTO " + TableList.VH_TAX_BASED_ON_PERMIT_INFO + " (SELECT *,CURRENT_TIMESTAMP,? FROM " + TableList.VT_TAX_BASED_ON_PERMIT_INFO + " WHERE  STATE_CD= ? AND OFF_CD= ? AND REGN_NO = ?)";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, empCd);
                    ps.setString(2, stateCd);
                    ps.setInt(3, offCd);
                    ps.setString(4, updateMissingPermitInfoDobj.getRegnNo());
                    ps.executeUpdate();

                    query = "DELETE FROM " + TableList.VT_TAX_BASED_ON_PERMIT_INFO + " WHERE  STATE_CD= ? AND OFF_CD= ? AND REGN_NO = ?";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, stateCd);
                    ps.setInt(2, offCd);
                    ps.setString(3, updateMissingPermitInfoDobj.getRegnNo());
                    ps.executeUpdate();
                }

                query = "INSERT INTO " + TableList.VT_TAX_BASED_ON_PERMIT_INFO + " (SELECT state_cd, off_cd, regn_no, pmt_type, pmt_catg, service_type, \n"
                        + "       route_class, route_length, no_of_trips, domain_cd, distance_run_in_quarter, \n"
                        + "       op_dt  FROM " + TableList.VA_TAX_BASED_ON_PERMIT_INFO + " WHERE appl_no=? and state_cd= ? and off_cd= ?)";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, applNo);
                ps.setString(2, stateCd);
                ps.setInt(3, offCd);
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                query = "DELETE FROM " + TableList.VA_TAX_BASED_ON_PERMIT_INFO + " where appl_no = ?  and state_cd= ? and off_cd= ?";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, applNo);
                ps.setString(2, stateCd);
                ps.setInt(3, offCd);
                ps.executeUpdate();

                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);

            }
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            ServerUtil.fileFlow(tmgr, status_dobj);
            tmgr.commit();
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in updating information.");
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
}
