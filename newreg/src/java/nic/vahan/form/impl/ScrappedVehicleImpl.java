/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import nic.vahan.form.dobj.ScrappedVehicleDobj;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.ToDobj;
import nic.vahan.form.dobj.reports.ScrappedVehicleReportDobj;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import org.apache.log4j.Logger;

/**
 *
 * @author kaptan singh
 */
public class ScrappedVehicleImpl {

    private static final Logger LOGGER = Logger.getLogger(ScrappedVehicleImpl.class);

    public boolean saveScrrapeDetails(ScrappedVehicleDobj dobj, Status_dobj status_dobj, String changedDataContents, boolean isScrap_veh_no_retain) throws Exception {
        boolean flag = false;
        boolean checkExist = true;
        TransactionManager tmgr = null;
        String sql = "";
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        Date applDate = format.parse(status_dobj.getAppl_dt());
        try {
            tmgr = new TransactionManager("saveScrrapeDetails");
            checkExist = checkForDataExist(tmgr, status_dobj.getAppl_no(), checkExist);
            if (checkExist) {
                if (!changedDataContents.isEmpty()) {
                    insertInVhaScrrapeDetails(dobj, status_dobj, tmgr);
                    insertIntoVhaChangedData(tmgr, status_dobj.getAppl_no(), changedDataContents);
                    updateScrrapeDetails(dobj, status_dobj, tmgr);
                    flag = true;
                }
            } else {
                flag = insertScrrapeDetails(dobj, status_dobj, tmgr);
                if (dobj.isRetain_regn_no() && isScrap_veh_no_retain) {
                    ToDobj to_dobj = new ToDobj();
                    ToImpl toImpl = new ToImpl();
                    to_dobj.setAppl_no(status_dobj.getAppl_no());
                    to_dobj.setRegn_no(status_dobj.getRegn_no());
                    to_dobj.setReason(dobj.getScrap_reason());
                    toImpl.insertIntoVaSurrenderRetention(tmgr, to_dobj, applDate);
                }
            }
            tmgr.commit();
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
        return flag;

    }

    public void isSaveAndMove(ScrappedVehicleDobj dobj, Status_dobj statusDobj, String changedDataContents, boolean isScrap_veh_no_retain) throws Exception {
        boolean flag = false;
        boolean checkExist = true;
        TransactionManager tmgr = null;
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        Date applDate = format.parse(statusDobj.getAppl_dt());
        try {
            tmgr = new TransactionManager("isSaveAndMove");
            checkExist = checkForDataExist(tmgr, statusDobj.getAppl_no(), checkExist);
            VehicleParameters vehicleParametersForNoc = new VehicleParameters();
            if (dobj.isRetain_regn_no() && isScrap_veh_no_retain) {
                vehicleParametersForNoc.setNOC_RETENTION(1);
                statusDobj.setVehicleParameters(vehicleParametersForNoc);
            } else if (!dobj.isRetain_regn_no() && isScrap_veh_no_retain) {
                vehicleParametersForNoc.setNOC_RETENTION(0);
                statusDobj.setVehicleParameters(vehicleParametersForNoc);
            } else if (!dobj.isRetain_regn_no() && !isScrap_veh_no_retain) {
                vehicleParametersForNoc.setNOC_RETENTION(0);
                statusDobj.setVehicleParameters(vehicleParametersForNoc);
            } else if (!isScrap_veh_no_retain) {
                vehicleParametersForNoc.setNOC_RETENTION(0);
                statusDobj.setVehicleParameters(vehicleParametersForNoc);
            }
            if (dobj.isRetain_regn_no() && isScrap_veh_no_retain) {
                ToDobj to_dobj = new ToDobj();
                ToImpl toImpl = new ToImpl();
                to_dobj.setAppl_no(statusDobj.getAppl_no());
                to_dobj.setRegn_no(statusDobj.getRegn_no());
                to_dobj.setReason(dobj.getScrap_reason());
                toImpl.insertIntoVaSurrenderRetention(tmgr, to_dobj, applDate);
            }
            if (checkExist) {
                if (!changedDataContents.isEmpty()) {
                    insertInVhaScrrapeDetails(dobj, statusDobj, tmgr);
                    insertIntoVhaChangedData(tmgr, statusDobj.getAppl_no(), changedDataContents);
                    updateScrrapeDetails(dobj, statusDobj, tmgr);
                }
            } else {
                flag = insertScrrapeDetails(dobj, statusDobj, tmgr);
            }
            ServerUtil.webServiceForNextStage(statusDobj, tmgr);
            ServerUtil.fileFlow(tmgr, statusDobj);
            tmgr.commit();
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

    public void isVarification(ScrappedVehicleDobj dobj, Status_dobj statusDobj, String changedDataContents) throws Exception {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("isVarification");
            if (!changedDataContents.isEmpty()) {
                insertInVhaScrrapeDetails(dobj, statusDobj, tmgr);
                insertIntoVhaChangedData(tmgr, statusDobj.getAppl_no(), changedDataContents);
                updateScrrapeDetails(dobj, statusDobj, tmgr);
            }
            ServerUtil.webServiceForNextStage(statusDobj, tmgr);
            ServerUtil.fileFlow(tmgr, statusDobj);
            tmgr.commit();
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

    }

    public void isApproval(ScrappedVehicleDobj dobj, Status_dobj statusDobj, String changedDataContents, OwnerDetailsDobj ownerDetails, boolean isScrap_veh_no_retain, Owner_dobj ownerDobj) throws VahanException {
        TransactionManager tmgr = null;
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        String new_Regn_no = "";
        String regn_no = statusDobj.getRegn_no();
        try {
            Date applDate = format.parse(statusDobj.getAppl_dt());
            tmgr = new TransactionManager("isApproval");
            insertInVhaScrrapeDetails(dobj, statusDobj, tmgr);
            if (!changedDataContents.isEmpty()) {
                insertIntoVhaChangedData(tmgr, statusDobj.getAppl_no(), changedDataContents);
                updateScrrapeDetails(dobj, statusDobj, tmgr);
            }

            OwnerImpl ownerImpl = new OwnerImpl();
            ownerImpl.insertInVhOwnerFromVtOwner(statusDobj.getAppl_no(), statusDobj.getRegn_no(), tmgr);
            changeStatusInVtOwner(dobj, statusDobj, tmgr);
            if (dobj.isRetain_regn_no() && isScrap_veh_no_retain) {
                ToDobj to_dobj = new ToDobj();
                ToImpl toImpl = new ToImpl();
                to_dobj.setAppl_no(statusDobj.getAppl_no());
                to_dobj.setRegn_no(statusDobj.getRegn_no());
                to_dobj.setReason(dobj.getScrap_reason());
                toImpl.insertIntoVhaSurrenderRetention(tmgr, statusDobj.getAppl_no());
                toImpl.insertIntoVtSurrenderRetention(tmgr, ownerDobj, dobj.getScrap_reason(), statusDobj.getAppl_no(), applDate, TableConstants.VM_MAST_VEHICLE_SCRAPE, false);
                ServerUtil.deleteFromTable(tmgr, null, dobj.getAppl_no(), TableList.VA_SURRENDER_RETENTION);
                new_Regn_no = getRetainRegistrationNO(tmgr, statusDobj.getRegn_no());
                regn_no = new_Regn_no;
            }

            inserrtInVtScrapeDetailsFromVaScrapeDetails(dobj, statusDobj, tmgr, ownerDetails, regn_no);
            deleteFromVaScrrapeDetails(dobj, statusDobj, tmgr);
            statusDobj.setEntry_status(TableConstants.STATUS_APPROVED);
            ServerUtil.updateApprovedStatus(tmgr, statusDobj);
            ServerUtil.webServiceForNextStage(statusDobj, tmgr);
            ServerUtil.fileFlow(tmgr, statusDobj);

            tmgr.commit();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: In Approval Of Scrap Vehicle");
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

    public void reback(ScrappedVehicleDobj dobj, Status_dobj statusDobj, String changedDataContents) {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("movetoapprove");
            if (!changedDataContents.isEmpty()) {
                insertInVhaScrrapeDetails(dobj, statusDobj, tmgr);
                insertIntoVhaChangedData(tmgr, statusDobj.getAppl_no(), changedDataContents);
                updateScrrapeDetails(dobj, statusDobj, tmgr);
            }
            statusDobj = ServerUtil.webServiceForNextStage(statusDobj, tmgr);
            ServerUtil.fileFlow(tmgr, statusDobj);
            tmgr.commit();
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
    }

    boolean insertScrrapeDetails(ScrappedVehicleDobj dobj, Status_dobj status_dobj, TransactionManager tmgr) throws VahanException {
        boolean flag = false;
        int i = 0;
        PreparedStatement pstmt = null;
        String sql = "INSERT INTO " + TableList.VA_SCRAP_VEHICLE + "(\n"
                + "          state_cd, off_cd, appl_no, regn_no, agency_name, agency_address, no_dues_cert_no, \n"
                + "            no_dues_issue_dt, scrap_cert_no, scrap_cert_issue_dt, retain_regn_no, \n"
                + "            scrap_reason, op_dt )\n"
                + "    VALUES (?, ?, ?, ?, \n"
                + "            ?, ?, ?, ?, ?, ?, ?, ?, \n"
                + "            current_timestamp)";
        try {
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(++i, Util.getUserStateCode());
            pstmt.setInt(++i, Util.getSelectedSeat().getOff_cd());
            pstmt.setString(++i, status_dobj.getAppl_no());
            pstmt.setString(++i, status_dobj.getRegn_no());
            pstmt.setString(++i, dobj.getAgency_name());
            pstmt.setString(++i, dobj.getAgency_address());
            pstmt.setString(++i, dobj.getNo_dues_cert_no());
            pstmt.setDate(++i, new java.sql.Date(dobj.getNo_dues_issue_dt().getTime()));
            pstmt.setString(++i, dobj.getScrap_cert_no());
            pstmt.setDate(++i, new java.sql.Date(dobj.getScrap_cert_issue_dt().getTime()));
            pstmt.setBoolean(++i, dobj.isRetain_regn_no());
            pstmt.setString(++i, dobj.getScrap_reason());
            pstmt.execute();
            flag = true;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in saving data. Try After Some time");
        }
        return flag;
    }

    boolean updateScrrapeDetails(ScrappedVehicleDobj dobj, Status_dobj status_dobj, TransactionManager tmgr) throws Exception {
        boolean flag = true;
        PreparedStatement pstmt = null;
        String sql = "UPDATE " + TableList.VA_SCRAP_VEHICLE + "\n"
                + "   SET   agency_name=?, agency_address=?, no_dues_cert_no=?, \n"
                + "       no_dues_issue_dt=?, scrap_cert_no=?, scrap_cert_issue_dt=?, retain_regn_no=?, \n"
                + "       scrap_reason=? ,op_dt=current_timestamp\n"
                + " WHERE appl_no=?";
        try {
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, dobj.getAgency_name());
            pstmt.setString(2, dobj.getAgency_address());
            pstmt.setString(3, dobj.getNo_dues_cert_no());
            pstmt.setDate(4, new java.sql.Date(dobj.getNo_dues_issue_dt().getTime()));
            pstmt.setString(5, dobj.getScrap_cert_no());
            pstmt.setDate(6, new java.sql.Date(dobj.getScrap_cert_issue_dt().getTime()));
            pstmt.setBoolean(7, dobj.isRetain_regn_no());
            pstmt.setString(8, dobj.getScrap_reason());
            pstmt.setString(9, status_dobj.getAppl_no());
            pstmt.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Updating Data !!!!!! ");
        }
        return flag;
    }

    public void deleteFromVaScrrapeDetails(ScrappedVehicleDobj dobj, Status_dobj status_dobj, TransactionManager tmgr) throws VahanException {
        PreparedStatement pstmt = null;
        String sql = "";
        try {
            sql = "DELETE FROM " + TableList.VA_SCRAP_VEHICLE + "\n"
                    + " WHERE appl_no = ? ";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, status_dobj.getAppl_no());
            pstmt.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in deleting Data  !!!!!");
        }
    }

    public List<ScrappedVehicleReportDobj> getScrapdelatils(Date op_dt, Date to_date) {
        ScrappedVehicleReportDobj dobj = null;
        PreparedStatement pstmt = null;
        TransactionManager tmgr = null;
        List<ScrappedVehicleReportDobj> scrappedList = null;
        String sql = "";
        sql = "select a.old_regn_no, a.loi_no,b.owner_name,b.f_name,b.c_add1,b.c_add2,b.c_add3,e.descr as c_district_name,b.c_pincode,f.descr as c_state_name\n"
                + ",a.op_dt,d.descr as state_name,c.off_name"
                + " from " + TableList.VT_SCRAP_VEHICLE + "  a \n"
                + " inner join " + TableList.VT_OWNER + " b ON a.old_regn_no=b.regn_no  and a.state_cd=b.state_cd and a.off_cd=b.off_cd \n"
                + " LEFT OUTER JOIN " + TableList.TM_OFFICE + "  c on c.state_cd = a.state_cd and c.off_cd =a.off_cd \n"
                + " LEFT OUTER JOIN " + TableList.TM_STATE + "  d on d.state_code = a.state_cd \n"
                + " LEFT OUTER JOIN " + TableList.TM_DISTRICT + "  e on b.c_district= e.dist_cd  and b.state_cd=e.state_cd\n"
                + " LEFT OUTER JOIN " + TableList.TM_STATE + "  f on f.state_code = b.c_state\n"
                + " where a.op_dt >= ? and a.op_dt <= ? and a.state_cd=? and a.off_cd=? ";
        try {
            tmgr = new TransactionManager("getScrapdelatils");
            scrappedList = new ArrayList<>();
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setDate(1, new java.sql.Date(op_dt.getTime()));
            pstmt.setDate(2, new java.sql.Date(to_date.getTime()));
            pstmt.setString(3, Util.getUserStateCode());
            pstmt.setInt(4, Util.getUserOffCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new ScrappedVehicleReportDobj();
                dobj.setOldRegnno(rs.getString("old_regn_no"));
                dobj.setLoino(rs.getString("loi_no"));
                dobj.setOwnName(rs.getString("owner_name"));
                dobj.setFname(rs.getString("f_name"));
                dobj.setCurrentAddress(rs.getString("c_add1") + "," + rs.getString("c_add2") + "," + rs.getString("c_add3") + "," + rs.getString("c_district_name") + "-" + rs.getInt("c_pincode") + "," + rs.getString("c_state_name"));
                dobj.setStateName(rs.getString("state_name"));
                dobj.setOffname(rs.getString("off_name"));
                dobj.setOpdt(ServerUtil.parseDateToString(rs.getDate("op_dt")));
                scrappedList.add(dobj);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            scrappedList = null;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            scrappedList = null;
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return scrappedList;
    }

    public void insertInVhaScrrapeDetails(ScrappedVehicleDobj dobj, Status_dobj status_dobj, TransactionManager tmgr) throws Exception {
        PreparedStatement pstmt = null;
        String sql = "";
        try {
            sql = "INSERT INTO " + TableList.VHA_SCRAP_VEHICLE + " (\n"
                    + "  moved_on,  moved_by, state_cd, off_cd, appl_no, regn_no, agency_name, agency_address, no_dues_cert_no, \n"
                    + " no_dues_issue_dt, scrap_cert_no, scrap_cert_issue_dt, retain_regn_no, \n"
                    + " scrap_reason, op_dt)\n"
                    + " SELECT  current_timestamp + interval '1 second' as moved_on, ? as moved_by,  state_cd, off_cd, appl_no, regn_no, agency_name, agency_address, no_dues_cert_no, \n"
                    + " no_dues_issue_dt, scrap_cert_no, scrap_cert_issue_dt, retain_regn_no, \n"
                    + " scrap_reason, op_dt\n"
                    + " FROM  " + TableList.VA_SCRAP_VEHICLE + "  where appl_no = ? ";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, Util.getEmpCode());
            pstmt.setString(2, status_dobj.getAppl_no());
            pstmt.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("there is some problem in updating data!!!!!");
        }
    }

    public boolean checkForDataExist(TransactionManager tmgr, String appl_no, boolean checkExist) throws VahanException {
        PreparedStatement pstmt = null;
        String sql = "";
        try {
            sql = "select * from " + TableList.VA_SCRAP_VEHICLE + " where appl_no = ?";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (!rs.next()) {
                checkExist = false;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Couldn't Check data exist or not!!!!!! ");
        }
        return checkExist;
    }

    public ScrappedVehicleDobj getdelatils(int purCode, int actionCode, String vehicleNo, String appl_no) throws Exception {
        ScrappedVehicleDobj dobj = null;
        PreparedStatement pstmt = null;
        TransactionManager tmgr = null;
        String sql = "";
        sql = "SELECT appl_no, regn_no, agency_name, agency_address, no_dues_cert_no, \n"
                + " no_dues_issue_dt, scrap_cert_no, scrap_cert_issue_dt, retain_regn_no, \n"
                + " scrap_reason, op_dt\n"
                + " FROM " + TableList.VA_SCRAP_VEHICLE + " where  appl_no = ?";
        try {
            tmgr = new TransactionManager("getdelatils");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new ScrappedVehicleDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setAgency_name(rs.getString("agency_name"));
                dobj.setAgency_address(rs.getString("agency_address"));
                dobj.setScrap_cert_no(rs.getString("scrap_cert_no"));
                dobj.setNo_dues_cert_no(rs.getString("no_dues_cert_no"));
                dobj.setNo_dues_issue_dt(rs.getDate("no_dues_issue_dt"));
                dobj.setScrap_cert_issue_dt(rs.getDate("scrap_cert_issue_dt"));
                dobj.setScrap_reason(rs.getString("scrap_reason"));
                dobj.setRetain_regn_no(rs.getBoolean("retain_regn_no"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in getting  details  of this application NO!!!!!!!");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dobj;
    }

    public void inserrtInVtScrapeDetailsFromVaScrapeDetails(ScrappedVehicleDobj dobj, Status_dobj statusDobj, TransactionManager tmgr, OwnerDetailsDobj ownerDetails, String regn_no) throws Exception {
        PreparedStatement pstmt = null;
        String sql = "";
        try {
            String loiNo = ServerUtil.getUniquePermitNo(tmgr, Util.getUserStateCode(), Util.getUserOffCode(), 0, 0, "L");
            sql = "   INSERT INTO " + TableList.VT_SCRAP_VEHICLE + " (\n"
                    + " state_cd, off_cd, loi_no, old_regn_no, old_chasi_no, \n"
                    + " agency_name, agency_address, no_dues_cert_no, no_dues_issue_dt, \n"
                    + " scrap_cert_no, scrap_cert_issue_dt, retain_regn_no, scrap_reason, \n"
                    + " op_dt )\n"
                    + " SELECT ? as state_cd,? as off_cd ,? as loiNo ,?, ? as oldchasisNO, agency_name, agency_address, no_dues_cert_no, \n"
                    + " no_dues_issue_dt, scrap_cert_no, scrap_cert_issue_dt, retain_regn_no, \n"
                    + " scrap_reason, op_dt \n"
                    + " FROM  " + TableList.VA_SCRAP_VEHICLE + "   where appl_no = ?";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, Util.getUserStateCode());
            pstmt.setInt(2, Util.getSelectedSeat().getOff_cd());
            pstmt.setString(3, loiNo);
            pstmt.setString(4, regn_no);
            pstmt.setString(5, ownerDetails.getChasi_no());
            pstmt.setString(6, statusDobj.getAppl_no());
            pstmt.executeUpdate();
        } catch (VahanException | SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in approval of application.");
        }
    }

    public void changeStatusInVtOwner(ScrappedVehicleDobj dobj, Status_dobj statusDobj, TransactionManager tmgr) throws Exception {
        PreparedStatement pstmt = null;
        String sql = "";
        sql = "UPDATE  " + TableList.VT_OWNER + "\n"
                + " SET status=? ,op_dt=current_timestamp\n"
                + " WHERE regn_no=? and state_cd=? ";
        try {
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, TableConstants.VT_SCRAP_VEHICLE_STATUS);
            pstmt.setString(2, statusDobj.getRegn_no());
            pstmt.setString(3, Util.getUserStateCode());
            pstmt.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("there is some problem in  Changing the status.");
        }
    }

    public ScrappedVehicleDobj getOldScrappedInformation(String regno, String stateCd, int offCd) throws VahanException {
        ScrappedVehicleDobj dobj = null;
        PreparedStatement pstmt = null;
        String sql = " Select * from  " + TableList.VT_SCRAP_VEHICLE
                + " WHERE old_regn_no=? "
                + " and state_cd=? and off_cd=?   ";
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getScrappedInformation");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, regno);
            pstmt.setString(2, stateCd);
            pstmt.setInt(3, offCd);

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new ScrappedVehicleDobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setLoi_no(rs.getString("loi_no"));
                dobj.setOld_regn_no(rs.getString("old_regn_no"));
                dobj.setOld_chasi_no(rs.getString("old_chasi_no"));
                dobj.setNew_regn_no(rs.getString("new_regn_no"));
                dobj.setNew_chasi_no(rs.getString("new_chasi_no"));
                dobj.setAgency_name(rs.getString("agency_name"));
                dobj.setAgency_address(rs.getString("agency_address"));
                dobj.setNo_dues_cert_no(rs.getString("no_dues_cert_no"));
                dobj.setNo_dues_issue_dt(rs.getDate("no_dues_issue_dt"));
                dobj.setScrap_cert_no(rs.getString("scrap_cert_no"));
                dobj.setScrap_cert_issue_dt(rs.getDate("scrap_cert_issue_dt"));
                dobj.setScrap_reason(rs.getString("scrap_reason"));
                dobj.setRegn_appl_no(rs.getString("regn_appl_no"));

                if (rs.getDate("op_dt") != null) {
                    dobj.setOp_dt(ServerUtil.parseDateToString(rs.getDate("op_dt")));
                }

            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Fetching of Scrapped Vehicle");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                throw new VahanException("Error in Database Connection ");
            }

        }
        return dobj;
    }

    public ScrappedVehicleDobj getScrappedInformationApplNo(String applNo) throws VahanException {
        ScrappedVehicleDobj dobj = null;
        PreparedStatement ps = null;
        String sql = " Select * from  " + TableList.VT_SCRAP_VEHICLE
                + " WHERE regn_appl_no = ?  ";
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getScrappedInformation");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new ScrappedVehicleDobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setLoi_no(rs.getString("loi_no"));
                dobj.setOld_regn_no(rs.getString("old_regn_no"));
                dobj.setOld_chasi_no(rs.getString("old_chasi_no"));
                dobj.setNew_regn_no(rs.getString("new_regn_no"));
                dobj.setNew_chasi_no(rs.getString("new_chasi_no"));
                dobj.setAgency_name(rs.getString("agency_name"));
                dobj.setAgency_address(rs.getString("agency_address"));
                dobj.setNo_dues_cert_no(rs.getString("no_dues_cert_no"));
                dobj.setNo_dues_issue_dt(rs.getDate("no_dues_issue_dt"));
                dobj.setScrap_cert_no(rs.getString("scrap_cert_no"));
                dobj.setScrap_cert_issue_dt(rs.getDate("scrap_cert_issue_dt"));
                dobj.setScrap_reason(rs.getString("scrap_reason"));
                dobj.setRegn_appl_no(rs.getString("regn_appl_no"));

                if (rs.getDate("op_dt") != null) {
                    dobj.setOp_dt(ServerUtil.parseDateToString(rs.getDate("op_dt")));
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Fetching of Scrapped Vehicle");
        } finally {

            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                throw new VahanException("Error in Database Connection ");
            }

        }
        return dobj;
    }

    public void updateApplNoForScrappedVeh(String oldRegno, String applNo, String stateCd, int offCd, TransactionManager tmgr) throws VahanException {

        String sql = " Select  regn_appl_no from " + TableList.VT_SCRAP_VEHICLE
                + " where old_regn_no=? "
                + " and state_cd=? and off_cd=? and regn_appl_no is not null and "
                + " new_chasi_no is not null and new_regn_no is not null ";

        PreparedStatement pstmt = null;
        try {

            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, oldRegno);
            pstmt.setString(2, stateCd);
            pstmt.setInt(3, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                throw new VahanException("Entry is already done for Registration No : " + oldRegno);
            }

            sql = " update  " + TableList.VT_SCRAP_VEHICLE
                    + " set regn_appl_no=? WHERE old_regn_no=? "
                    + " and state_cd=? and off_cd=? ";

            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, applNo);
            pstmt.setString(2, oldRegno);
            pstmt.setString(3, stateCd);
            pstmt.setInt(4, offCd);
            pstmt.executeUpdate();

            sql = " update  " + TableList.VT_SURRENDER_RETENTION
                    + " set regn_appl_no=? WHERE new_regn_no=? "
                    + " and state_cd=? and off_cd=? ";

            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, applNo);
            pstmt.setString(2, oldRegno);
            pstmt.setString(3, stateCd);
            pstmt.setInt(4, offCd);
            pstmt.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Updation of Scrapped Vehicle");
        }

    }

    public void updateNewChaisNoForScrappedVeh(String newChasi, String applNo, TransactionManager tmgr) throws VahanException {

        PreparedStatement pstmt = null;
        try {

            String sql = " update  " + TableList.VT_SCRAP_VEHICLE
                    + " set new_chasi_no  =? WHERE  regn_appl_no=? ";

            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, newChasi);
            pstmt.setString(2, applNo);
            pstmt.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Updation of Scrapped Vehicle");
        }

    }

    private String getRetainRegistrationNO(TransactionManager tmgr, String old_regn_no) throws VahanException {
        String newRegn_no = "";
        PreparedStatement pstmt = null;
        String sql = "";
        try {
            sql = "select * from " + TableList.VT_SURRENDER_RETENTION + " where old_regn_no = ?";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, old_regn_no);
            RowSet rowSet = tmgr.fetchDetachedRowSet_No_release();
            if (rowSet.next()) {
                newRegn_no = rowSet.getString("new_regn_no");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There is some problem in getting Retained Registration No. details");
        }
        return newRegn_no;
    }

    public static boolean scrapDetailsExist(String regnNo, String stateCd, int offCd) {
        boolean isDetailsExist = false;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            String sql = "select * from " + TableList.VT_SCRAP_VEHICLE + " where old_regn_no=? and state_cd=? and off_cd=?";
            tmgr = new TransactionManager("scrapDetailsExist");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isDetailsExist = true;
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
        return isDetailsExist;
    }

    public void fillOwnerDobjInTheftCaseFronVhTo(String scrap_regn_no, Owner_dobj dobj) throws VahanException {
        TransactionManager tmgr = null;
        String sql = "";
        try {
            sql = "  SELECT e.owner_name, e.f_name,\n"
                    + "  e.c_add1  ,  e.c_add2 ,  e.c_add3 , e.c_district , e.c_state , e.c_pincode , \n"
                    + "  e.p_add1  ,  e.p_add2 ,  e.p_add3 , e.p_district , e.p_state , e.p_pincode , \n"
                    + " e.owner_cd, e.owner_ctg"
                    + "  FROM " + TableList.VT_SCRAP_VEHICLE + " a \n"
                    + "  LEFT OUTER JOIN " + TableList.VH_TO + " e ON  e.regn_no = a.old_regn_no and e.state_cd=a.state_cd and e.off_cd=a.off_cd \n"
                    + "  WHERE a.old_regn_no=? and a.state_cd= ? and a.off_cd=?  order by e.moved_on desc limit 1";
            tmgr = new TransactionManager("fillOwnerDobjInTheftCaseFronVhTo");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, scrap_regn_no);
            ps.setString(2, dobj.getState_cd());
            ps.setInt(3, dobj.getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setF_name(rs.getString("f_name"));
                dobj.setC_add1(rs.getString("c_add1"));
                dobj.setC_add2(rs.getString("c_add2"));
                dobj.setC_add3(rs.getString("c_add3"));
                dobj.setC_district(rs.getInt("c_district"));
                dobj.setC_state(rs.getString("c_state"));
                dobj.setC_pincode(rs.getInt("c_pincode"));
                dobj.setP_add1(rs.getString("p_add1"));
                dobj.setP_add2(rs.getString("p_add2"));
                dobj.setP_add3(rs.getString("p_add3"));
                dobj.setP_district(rs.getInt("p_district"));
                dobj.setP_state(rs.getString("p_state"));
                dobj.setP_pincode(rs.getInt("p_pincode"));
                dobj.setOwner_cd(rs.getInt("owner_cd"));
                dobj.getOwner_identity().setOwnerCatg(rs.getInt("owner_ctg"));
            } else {
                throw new VahanException("Error: Previous Owner Details Not Found !");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: There is some problem in DataBase.");
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
