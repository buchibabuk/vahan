/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.commoncarrier;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.commoncarrier.DetailCommonCarrierDobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author acer
 */
public class DetailCommonCarrierImpl {

    private static final Logger LOGGER = Logger.getLogger(DetailCommonCarrierImpl.class);

    public static void save(DetailCommonCarrierDobj dCCDobj, DetailCommonCarrierDobj dCCDobjPrev, List listBranchDetailsForPrev, List listBranchDetailsFor, String renewDupOption, String changedData) throws VahanException {
        TransactionManager tmgr = null;
        String applNo = "";
        try {
            tmgr = new TransactionManager("CommonCarrier_Impl.save()");
            if (changedData != null && !changedData.equals("") || dCCDobjPrev != null) {
                insertUpdateCC(tmgr, dCCDobj, dCCDobjPrev, listBranchDetailsForPrev, listBranchDetailsFor, changedData);
            } else {
                applNo = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
                dCCDobj.setApplNo(applNo);
                if (!renewDupOption.equalsIgnoreCase("Duplicate_CC_Certificate")) {
                    insertIntoVaCommonCarriers(tmgr, dCCDobj);
                    insertIntoVaCCBranchDetails(tmgr, listBranchDetailsFor, applNo);
                }
                Status_dobj status = new Status_dobj();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                String dt = sdf.format(new Date());
                status.setAppl_dt(dt);
                status.setAppl_no(applNo);
                if (renewDupOption.equals("New_CC_Certificate")) {
                    status.setPur_cd(TableConstants.VM_TRANSACTION_CARRIER_REGN);
                    status.setRegn_no("NEW");
                } else if (renewDupOption.equals("Renew_CC_Certificate")) {
                    status.setPur_cd(TableConstants.VM_TRANSACTION_CARRIER_RENEWAL);
                    status.setRegn_no(dCCDobj.getRegnNo());
                } else if (renewDupOption.equals("Duplicate_CC_Certificate")) {
                    status.setPur_cd(TableConstants.VM_TRANSACTION_CARRIER_DUPLICATE);
                    status.setRegn_no(dCCDobj.getRegnNo());
                }
                status.setFlow_slno(1);
                status.setFile_movement_slno(1);
                status.setAction_cd(TableConstants.VM_COMMON_CARRIER_APPL_ENTRY);
                status.setEmp_cd(Long.parseLong(Util.getEmpCode()));
                status.setOffice_remark("");
                status.setPublic_remark("");
                status.setStatus("N");
                status.setState_cd(Util.getUserStateCode());
                status.setOff_cd(Util.getSelectedSeat().getOff_cd());
                ServerUtil.fileFlowForNewApplication(tmgr, status);
                status.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
                status.setStatus(TableConstants.STATUS_COMPLETE);
                status.setAppl_no(applNo);
                status.setOffice_remark("");
                status.setPublic_remark("");
                if (renewDupOption.equals("New_CC_Certificate")) {
                    ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, applNo,
                            TableConstants.VM_COMMON_CARRIER_REGISTRATION, TableConstants.VM_TRANSACTION_CARRIER_REGN, null, tmgr);
                } else if (renewDupOption.equals("Renew_CC_Certificate")) {
                    ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, applNo,
                            TableConstants.VM_COMMON_CARRIER_REGISTRATION, TableConstants.VM_TRANSACTION_CARRIER_RENEWAL, null, tmgr);
                } else if (renewDupOption.equals("Duplicate_CC_Certificate")) {
                    ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, applNo,
                            TableConstants.VM_COMMON_CARRIER_REGISTRATION, TableConstants.VM_TRANSACTION_CARRIER_DUPLICATE, null, tmgr);
                }
                ServerUtil.fileFlow(tmgr, status);
            }
            tmgr.commit();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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
    }

    public static void insertUpdateCC(TransactionManager tmgr, DetailCommonCarrierDobj dCCDobj, DetailCommonCarrierDobj dCCDobjPrev, List listBranchDetailsForPrev, List listBranchDetailsFor, String changedData) throws SQLException, Exception {

        ComparisonBeanImpl.updateChangedData(dCCDobj.getApplNo(), changedData, tmgr);
        insertUpdateCC(tmgr, dCCDobj);//when there is change by user or Entry Scrutiny                  
        insertUpdateCCBranchHistory(tmgr, dCCDobj.getApplNo(), listBranchDetailsForPrev, listBranchDetailsFor, changedData);
    }

    public static void insertIntoVaCommonCarriers(TransactionManager tmgr, DetailCommonCarrierDobj detailCommonCarrierDobj) throws SQLException, Exception {
        PreparedStatement pstmt = null;
        String sql = "";
        sql = "INSERT INTO " + TableList.VA_COMMON_CARRIERS + " "
                + "(state_cd, off_cd, appl_no, authorized_person, org_name, address, valid_fr, valid_to, deal_cd, contact_no, op_dt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        pstmt = tmgr.prepareStatement(sql);
        int i = 1;
        pstmt.setString(i++, detailCommonCarrierDobj.getStateCd());
        pstmt.setInt(i++, detailCommonCarrierDobj.getOffCd());
        pstmt.setString(i++, detailCommonCarrierDobj.getApplNo());
        pstmt.setString(i++, detailCommonCarrierDobj.getPersonAuthorised().toUpperCase());
        pstmt.setString(i++, detailCommonCarrierDobj.getOrganijationName().toUpperCase());
        pstmt.setString(i++, detailCommonCarrierDobj.getAddress().toUpperCase());
        pstmt.setDate(i++, new java.sql.Date(detailCommonCarrierDobj.getValidFrom().getTime()));
        pstmt.setDate(i++, new java.sql.Date(detailCommonCarrierDobj.getValidUpto().getTime()));
        pstmt.setString(i++, Util.getEmpCode());
        pstmt.setLong(i++, detailCommonCarrierDobj.getContactNumber());
        pstmt.executeUpdate();
    }

    public static void insertIntoVaCCBranchDetails(TransactionManager tmgr, Collection commonCarrierDobj, String applNumber) throws SQLException, Exception {
        PreparedStatement pstmt = null;
        String sql = "";
        int srNo = 1;
        DetailCommonCarrierDobj detailCommonCarrierDobj = null;
        sql = "INSERT INTO " + TableList.VA_CC_BRANCH_DETAILS + " "
                + "(state_cd, off_cd, appl_no, location, address, hub_centre, commencement_dt, sr_no) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        pstmt = tmgr.prepareStatement(sql);
        for (Object keyObj : commonCarrierDobj) {
            detailCommonCarrierDobj = (DetailCommonCarrierDobj) keyObj;
            int i = 1;
            pstmt.setString(i++, detailCommonCarrierDobj.getStateCd());
            pstmt.setInt(i++, detailCommonCarrierDobj.getOffCd());
            pstmt.setString(i++, applNumber);
            pstmt.setString(i++, detailCommonCarrierDobj.getBranchLocation().toUpperCase());
            pstmt.setString(i++, detailCommonCarrierDobj.getBranchAddress().toUpperCase());
            pstmt.setString(i++, detailCommonCarrierDobj.getBranchCenter().toUpperCase());
            pstmt.setDate(i++, new java.sql.Date(detailCommonCarrierDobj.getBranchDateCommencement().getTime()));
            pstmt.setInt(i++, srNo++);
            pstmt.addBatch();
        }
        if (pstmt != null) {
            pstmt.executeBatch();
        }
    }

    public DetailCommonCarrierDobj set_CC_appl_db_to_dobj(String applNo) throws VahanException {
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;
        DetailCommonCarrierDobj detailCommonCarrierDobj = null;
        String sql = "Select * from " + TableList.VA_COMMON_CARRIERS + " where appl_no = ?";
        try {
            tmgr = new TransactionManager("getNameOfOwnerForSelectedApplication");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, applNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                detailCommonCarrierDobj = new DetailCommonCarrierDobj();
                detailCommonCarrierDobj.setPersonAuthorised(rs.getString("authorized_person"));
                detailCommonCarrierDobj.setAddress(rs.getString("address"));
                detailCommonCarrierDobj.setContactNumber(rs.getLong("contact_no"));
                detailCommonCarrierDobj.setApplNo(rs.getString("appl_no"));
                detailCommonCarrierDobj.setOrganijationName(rs.getString("org_name"));
                detailCommonCarrierDobj.setValidFrom(rs.getDate("valid_fr"));
                detailCommonCarrierDobj.setValidUpto(rs.getDate("valid_to"));
                detailCommonCarrierDobj.setStateCd(rs.getString("state_cd"));
                detailCommonCarrierDobj.setOffCd(rs.getInt("off_cd"));
                detailCommonCarrierDobj.setDealerCd(rs.getString("deal_cd"));
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return detailCommonCarrierDobj;
    }

    public List set_CC_branch_db_to_list(String applNo) throws VahanException {
        List listForOwnerDetail = new ArrayList<>();
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;
        DetailCommonCarrierDobj detailCommonCarrierDobj = null;
        String sql = "Select * from " + TableList.VA_CC_BRANCH_DETAILS + " where appl_no = ?";
        try {
            tmgr = new TransactionManager("getNameOfOwnerForSelectedApplication");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, applNo);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                detailCommonCarrierDobj = new DetailCommonCarrierDobj();
                detailCommonCarrierDobj.setStateCd(rs.getString("state_cd"));
                detailCommonCarrierDobj.setBranchLocation(rs.getString("location"));
                detailCommonCarrierDobj.setBranchAddress(rs.getString("address"));
                detailCommonCarrierDobj.setBranchCenter(rs.getString("hub_centre"));
                detailCommonCarrierDobj.setBranchDateCommencement(rs.getDate("commencement_dt"));
                detailCommonCarrierDobj.setSrNo(rs.getInt("sr_no"));
                listForOwnerDetail.add(detailCommonCarrierDobj);
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return listForOwnerDetail;
    }

    public static void insertIntoCCHistory(TransactionManager tmgr, String appl_no) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;
        sql = "INSERT INTO " + TableList.VHA_COMMON_CARRIERS
                + " SELECT current_timestamp as moved_on,? as moved_by,"
                + "        state_cd,off_cd,appl_no, authorized_person , org_name ,"
                + "        address , contact_no , valid_fr , valid_to "
                + "  FROM  " + TableList.VA_COMMON_CARRIERS
                + " WHERE  appl_no=? ";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();
    } // end of insertIntoCCHistory

    public static void insertUpdateCCBranchHistory(TransactionManager tmgr, String appl_no, List listBranchDetailsForPrev, List listBranchDetailsFor, String changedData) throws SQLException, Exception {


        Collection<DetailCommonCarrierDobj> removeDataList = new HashSet<DetailCommonCarrierDobj>(listBranchDetailsForPrev);
        removeDataList.removeAll(listBranchDetailsFor);
        PreparedStatement ps = null;
        String sql = null;
        DetailCommonCarrierDobj detailCommonCarrierDobj = null;
        for (Object keyObj : removeDataList) {
            detailCommonCarrierDobj = (DetailCommonCarrierDobj) keyObj;
            sql = "INSERT INTO " + TableList.VHA_CC_BRANCH_DETAILS
                    + " SELECT current_timestamp as moved_on,? as moved_by,"
                    + "        sr_no ,state_cd,off_cd,appl_no ,location  , address , hub_centre ,"
                    + "        commencement_dt  "
                    + "  FROM  " + TableList.VA_CC_BRANCH_DETAILS
                    + " WHERE  state_cd = ? and  off_cd = ? and appl_no=? and sr_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getUserOffCode());
            ps.setString(4, appl_no);
            ps.setInt(5, detailCommonCarrierDobj.getSrNo());
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
        }
        sql = "DELETE FROM " + TableList.VA_CC_BRANCH_DETAILS + " where state_cd = ? and  off_cd = ? and appl_no = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getUserStateCode());
        ps.setInt(2, Util.getUserOffCode());
        ps.setString(3, appl_no);
        ps.executeUpdate();
        int srNo = 0;
        sql = "INSERT INTO " + TableList.VA_CC_BRANCH_DETAILS + " "
                + "(state_cd, off_cd, appl_no, location, address, hub_centre, commencement_dt, sr_no) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        ps = tmgr.prepareStatement(sql);
        for (Object keyObj : listBranchDetailsFor) {
            detailCommonCarrierDobj = (DetailCommonCarrierDobj) keyObj;
            int i = 1;
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getUserOffCode());
            ps.setString(i++, appl_no);
            ps.setString(i++, detailCommonCarrierDobj.getBranchLocation().toUpperCase());
            ps.setString(i++, detailCommonCarrierDobj.getBranchAddress().toUpperCase());
            ps.setString(i++, detailCommonCarrierDobj.getBranchCenter().toUpperCase());
            ps.setDate(i++, new java.sql.Date(detailCommonCarrierDobj.getBranchDateCommencement().getTime()));
            ps.setInt(i++, ++srNo);
            ps.addBatch();
        }
        if (ps != null) {
            ps.executeBatch();
        }
    }

    public static String saveAndMoveFile(Status_dobj status_dobj, DetailCommonCarrierDobj dCCDobj, DetailCommonCarrierDobj dCCDobjPrev, List listBranchDetailsForPrev, List listBranchDetailsFor, String changedData) throws Exception {
        TransactionManager tmgr = null;
        String rcptNo = "";
        String regnNo = "";
        PreparedStatement psmt = null;
        RowSet rs = null;
        try {
            if (changedData == null
                    || dCCDobj.getOffCd() == 0
                    || dCCDobj.getStateCd() == null) {
                throw new VahanException("Something went wrong, Please try again later...");
            }
            tmgr = new TransactionManager("saveAndMoveFile");
            if (status_dobj.getAction_cd() == TableConstants.VM_COMMON_CARRIER_APPL_VERIFY) {
                if ((changedData != null && !changedData.equals("") || dCCDobjPrev == null)) {
                    insertUpdateCC(tmgr, dCCDobj, dCCDobjPrev, listBranchDetailsForPrev, listBranchDetailsFor, changedData);
                }
            }
            if (status_dobj.getAction_cd() == TableConstants.VM_COMMON_CARRIER_APPL_APPROVE
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)) {
                String sql = "Select rcpt_no from VP_APPL_RCPT_MAPPING where appl_no = ?";
                psmt = tmgr.prepareStatement(sql);
                psmt.setString(1, status_dobj.getAppl_no());
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    rcptNo = rs.getString("rcpt_no");
                    if (status_dobj.getPur_cd() == TableConstants.VM_TRANSACTION_CARRIER_REGN) {
                        regnNo = getUniqueCCRegistrationNo(tmgr, Util.getUserStateCode(), Util.getUserOffCode());
                    } else {
                        regnNo = dCCDobj.getRegnNo();
                    }
                    if ((regnNo != null && !regnNo.equals(""))) {
                        if (status_dobj.getPur_cd() == TableConstants.VM_TRANSACTION_CARRIER_REGN) {
                            sql = "INSERT INTO " + TableList.VT_COMMON_CARRIERS
                                    + " SELECT state_cd,off_cd,  ? as cc_regn_no,"
                                    + "  authorized_person ,org_name ,contact_no , "
                                    + "        address ,? as rcpt_no,? as valid_fr ,? as valid_to  ,"
                                    + "     ?  as deal_cd, current_timestamp as op_dt"
                                    + "  FROM  " + TableList.VA_COMMON_CARRIERS
                                    + " WHERE  appl_no=? ";
                            psmt = tmgr.prepareStatement(sql);
                            psmt.setString(1, regnNo);
                            psmt.setString(2, rcptNo);
                            psmt.setDate(3, new java.sql.Date(dCCDobj.getValidFrom().getTime()));
                            psmt.setDate(4, new java.sql.Date(dCCDobj.getValidUpto().getTime()));
                            psmt.setString(5, Util.getEmpCode());
                            psmt.setString(6, status_dobj.getAppl_no());
                            psmt.executeUpdate();
                            sql = "DELETE FROM " + TableList.VA_COMMON_CARRIERS + " where appl_no = ?";
                            psmt = tmgr.prepareStatement(sql);
                            psmt.setString(1, status_dobj.getAppl_no());
                            psmt.executeUpdate();
                            sql = "INSERT INTO " + TableList.VT_CC_BRANCH_DETAILS
                                    + " SELECT state_cd , off_cd , sr_no , ? as cc_regn_no , "
                                    + "  ? as rcpt_no, location  , address ,  hub_centre ,"
                                    + "        commencement_dt , ? as deal_cd , current_timestamp as op_dt  "
                                    + "  FROM  " + TableList.VA_CC_BRANCH_DETAILS
                                    + " WHERE  appl_no=? ";
                            psmt = tmgr.prepareStatement(sql);
                            psmt.setString(1, regnNo);
                            psmt.setString(2, rcptNo);
                            psmt.setString(3, Util.getEmpCode());
                            psmt.setString(4, status_dobj.getAppl_no());
                            psmt.executeUpdate();
                            sql = "DELETE FROM " + TableList.VA_CC_BRANCH_DETAILS + " where appl_no = ?";
                            psmt = tmgr.prepareStatement(sql);
                            psmt.setString(1, status_dobj.getAppl_no());
                            psmt.executeUpdate();
                        } else if (status_dobj.getPur_cd() == TableConstants.VM_TRANSACTION_CARRIER_RENEWAL) {
                            sql = "INSERT INTO " + TableList.VH_COMMON_CARRIERS
                                    + " SELECT current_timestamp as moved_on, ? moved_by, state_cd, off_cd,  ? as cc_regn_no,"
                                    + "  authorized_person ,org_name ,contact_no , "
                                    + "  address , rcpt_no,valid_fr ,valid_to "
                                    + "  FROM  " + TableList.VT_COMMON_CARRIERS
                                    + " WHERE  cc_regn_no=? ";
                            psmt = tmgr.prepareStatement(sql);
                            psmt.setString(1, Util.getEmpCode());
                            psmt.setString(2, regnNo);
                            psmt.setString(3, regnNo);
                            psmt.executeUpdate();

                            sql = "UPDATE " + TableList.VT_COMMON_CARRIERS + " "
                                    + "SET state_cd = ?, off_cd = ?, authorized_person = ?, org_name = ?, contact_no = ?, address = ?, rcpt_no = ?,  valid_fr = ?, valid_to = ?, deal_cd = ?, op_dt = CURRENT_TIMESTAMP WHERE cc_regn_no = ?";
                            psmt = tmgr.prepareStatement(sql);
                            int i = 1;
                            psmt.setString(i++, Util.getUserStateCode());
                            psmt.setInt(i++, Util.getUserOffCode());
                            psmt.setString(i++, dCCDobj.getPersonAuthorised());
                            psmt.setString(i++, dCCDobj.getOrganijationName());
                            psmt.setLong(i++, dCCDobj.getContactNumber());
                            psmt.setString(i++, dCCDobj.getAddress());
                            psmt.setString(i++, rcptNo);
                            psmt.setDate(i++, new java.sql.Date(dCCDobj.getValidFrom().getTime()));
                            psmt.setDate(i++, new java.sql.Date(dCCDobj.getValidUpto().getTime()));
                            psmt.setString(i++, Util.getEmpCode());
                            psmt.setString(i++, regnNo);
                            psmt.executeUpdate();
                            String sqlccbd = " SELECT state_cd , off_cd , sr_no , ? as cc_regn_no , "
                                    + "  ? as rcpt_no, location  , address ,  hub_centre ,"
                                    + "        commencement_dt , ? as deal_cd , current_timestamp as op_dt  "
                                    + "  FROM  " + TableList.VA_CC_BRANCH_DETAILS
                                    + " WHERE  appl_no=? ";

                            psmt = tmgr.prepareStatement(sqlccbd);
                            psmt.setString(1, regnNo);
                            psmt.setString(2, rcptNo);
                            psmt.setString(3, Util.getEmpCode());
                            psmt.setString(4, status_dobj.getAppl_no());
                            rs = tmgr.fetchDetachedRowSet_No_release();
                            if (rs.next()) {
                                sql = "DELETE FROM " + TableList.VT_CC_BRANCH_DETAILS + " where cc_regn_no = ?";
                                psmt = tmgr.prepareStatement(sql);
                                psmt.setString(1, regnNo);
                                psmt.executeUpdate();

                                sql = "INSERT INTO " + TableList.VT_CC_BRANCH_DETAILS
                                        + " SELECT state_cd , off_cd , sr_no , ? as cc_regn_no , "
                                        + "  ? as rcpt_no, location  , address ,  hub_centre ,"
                                        + "        commencement_dt , ? as deal_cd , current_timestamp as op_dt  "
                                        + "  FROM  " + TableList.VA_CC_BRANCH_DETAILS
                                        + " WHERE  appl_no=? ";
                                psmt = tmgr.prepareStatement(sql);
                                psmt.setString(1, regnNo);
                                psmt.setString(2, rcptNo);
                                psmt.setString(3, Util.getEmpCode());
                                psmt.setString(4, status_dobj.getAppl_no());
                                psmt.executeUpdate();

                                sql = "DELETE FROM " + TableList.VA_CC_BRANCH_DETAILS + " where appl_no = ?";
                                psmt = tmgr.prepareStatement(sql);
                                psmt.setString(1, status_dobj.getAppl_no());
                                psmt.executeUpdate();
                            }
                            sql = "DELETE FROM " + TableList.VA_COMMON_CARRIERS + " where appl_no = ?";
                            psmt = tmgr.prepareStatement(sql);
                            psmt.setString(1, status_dobj.getAppl_no());
                            psmt.executeUpdate();
                        }
                        sql = "update " + TableList.VT_FEE + " set regn_no=? where state_cd=? and off_cd = ? and rcpt_no=?";
                        psmt = tmgr.prepareStatement(sql);
                        psmt.setString(1, regnNo);
                        psmt.setString(2, Util.getUserStateCode());
                        psmt.setInt(3, Util.getUserOffCode());
                        psmt.setString(4, rcptNo);
                        psmt.executeUpdate();

                        status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                        ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                    }
                }
            }
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            ServerUtil.fileFlow(tmgr, status_dobj);
            tmgr.commit();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }
        return regnNo;
    }

    public static void insertUpdateCC(TransactionManager tmgr, DetailCommonCarrierDobj changedData) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "SELECT appl_no FROM " + TableList.VA_COMMON_CARRIERS + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, changedData.getApplNo());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                insertIntoCCHistory(tmgr, changedData.getApplNo());
                updateToVaCommonCarriers(tmgr, changedData);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public static void updateToVaCommonCarriers(TransactionManager tmgr, DetailCommonCarrierDobj detailCommonCarrierDobj) throws SQLException, Exception {
        PreparedStatement pstmt = null;
        String sql = "";
        sql = "UPDATE " + TableList.VA_COMMON_CARRIERS + " "
                + "SET state_cd = ?, off_cd = ?, authorized_person = ?, org_name = ?, address = ?, deal_cd = ?, contact_no = ?, op_dt = CURRENT_TIMESTAMP WHERE appl_no = ?";
        pstmt = tmgr.prepareStatement(sql);
        int i = 1;
        pstmt.setString(i++, detailCommonCarrierDobj.getStateCd());
        pstmt.setInt(i++, detailCommonCarrierDobj.getOffCd());
        pstmt.setString(i++, detailCommonCarrierDobj.getPersonAuthorised().toUpperCase());
        pstmt.setString(i++, detailCommonCarrierDobj.getOrganijationName().toUpperCase());
        pstmt.setString(i++, detailCommonCarrierDobj.getAddress().toUpperCase());
        pstmt.setString(i++, Util.getEmpCode());
        pstmt.setLong(i++, detailCommonCarrierDobj.getContactNumber());
        pstmt.setString(i++, detailCommonCarrierDobj.getApplNo());
        pstmt.executeUpdate();
    }

    public static String getUniqueCCRegistrationNo(TransactionManager tmgr, String stateCd, int off_cd) throws VahanException {
        String ccrNo = null;
        try {
            if (stateCd == null || stateCd.isEmpty() || stateCd.length() > 2 || stateCd.length() < 2) {
                throw new VahanException("Something went wrong, please try again.");
            }

            String strSQL = "SELECT state_cd || '/' || off_cd || '/' || 'CC' || '/' || to_char(CURRENT_DATE,'YYYY') || '/' || lpad(nextval('" + TableList.COMMON_CARRIER_SCHEMA + "seq_v4_cc_no')::varchar, 4, '0') AS ccr_no  from tm_office where state_cd = ? and off_cd =?";
            PreparedStatement psmt = tmgr.prepareStatement(strSQL);
            psmt.setString(1, stateCd);
            psmt.setInt(2, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                ccrNo = rs.getString("ccr_no");
            }
            rs.close();
            psmt.close();
            rs = null;
            psmt = null;
        } catch (SQLException e) {
            ccrNo = null;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please try again.");
        } catch (Exception e) {
            ccrNo = null;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please try again.");
        }
        if (ccrNo == null) {
            throw new VahanException("Something went wrong, please try again.");
        }
        return ccrNo;
    }

    public static DetailCommonCarrierDobj set_CC_renw_dup_dt(String regNo) throws VahanException {
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;
        DetailCommonCarrierDobj detailCommonCarrierDobj = null;
        String sql = "Select * from " + TableList.VT_COMMON_CARRIERS + " where cc_regn_no = ?";
        try {
            tmgr = new TransactionManager("getNameOfOwnerForSelectedApplication");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, regNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                detailCommonCarrierDobj = new DetailCommonCarrierDobj();
                detailCommonCarrierDobj.setPersonAuthorised(rs.getString("authorized_person"));
                detailCommonCarrierDobj.setAddress(rs.getString("address"));
                detailCommonCarrierDobj.setContactNumber(rs.getLong("contact_no"));
                detailCommonCarrierDobj.setRegnNo(rs.getString("cc_regn_no"));
                detailCommonCarrierDobj.setOrganijationName(rs.getString("org_name"));
                detailCommonCarrierDobj.setValidFrom(rs.getDate("valid_fr"));
                detailCommonCarrierDobj.setValidUpto(rs.getDate("valid_to"));
                detailCommonCarrierDobj.setStateCd(rs.getString("state_cd"));
                detailCommonCarrierDobj.setOffCd(rs.getInt("off_cd"));
                detailCommonCarrierDobj.setDealerCd(rs.getString("deal_cd"));
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return detailCommonCarrierDobj;
    }

    public static List set_CC_renw_dup_branch_list(String regNo) throws VahanException {
        List listForOwnerDetail = new ArrayList<>();
        PreparedStatement psmt = null;
        RowSet rs = null;
        TransactionManager tmgr = null;
        DetailCommonCarrierDobj detailCommonCarrierDobj = null;
        String sql = "Select * from " + TableList.VT_CC_BRANCH_DETAILS + " where cc_regn_no = ?";
        try {
            tmgr = new TransactionManager("getNameOfOwnerForSelectedApplication");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, regNo);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                detailCommonCarrierDobj = new DetailCommonCarrierDobj();
                detailCommonCarrierDobj.setOffCd(rs.getInt("off_cd"));
                detailCommonCarrierDobj.setStateCd(rs.getString("state_cd"));
                detailCommonCarrierDobj.setBranchLocation(rs.getString("location"));
                detailCommonCarrierDobj.setBranchAddress(rs.getString("address"));
                detailCommonCarrierDobj.setBranchCenter(rs.getString("hub_centre"));
                detailCommonCarrierDobj.setBranchDateCommencement(rs.getDate("commencement_dt"));
                detailCommonCarrierDobj.setSrNo(rs.getInt("sr_no"));
                listForOwnerDetail.add(detailCommonCarrierDobj);
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return listForOwnerDetail;
    }
}
