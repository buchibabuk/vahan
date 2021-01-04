/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.AuctionDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.admin.OwnerAdminImpl;
import nic.vahan.server.NewVehicleNo;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import org.apache.log4j.Logger;

public class AuctionImpl {

    private static final Logger LOGGER = Logger.getLogger(AuctionImpl.class);

    public AuctionDobj getAuctionDetails(String regn_no, String appl_no) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        AuctionDobj auctionDobj = null;

        try {
            tmgr = new TransactionManager("getAuctionDetails");
            int i = 1;
            if (regn_no != null && !regn_no.equals("NEW")) {
                ps = tmgr.prepareStatement("SELECT * FROM " + TableList.VA_AUCTION + " WHERE regn_no=? and appl_no=? and state_cd = ? and off_cd = ?");
                ps.setString(i++, regn_no);
            } else if (regn_no != null && regn_no.equals("NEW")) {
                ps = tmgr.prepareStatement("SELECT * FROM " + TableList.VA_AUCTION + " WHERE appl_no=? and state_cd = ? and off_cd = ?");

            }
            ps.setString(i++, appl_no);
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getUserSeatOffCode());
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {
                auctionDobj = new AuctionDobj();
                auctionDobj.setStateCd(rs.getString("state_cd"));
                auctionDobj.setOffCd(rs.getInt("off_cd"));
                auctionDobj.setApplNo(rs.getString("appl_no"));
                auctionDobj.setRegnNo(rs.getString("regn_no"));
                auctionDobj.setChasiNo(rs.getString("chasi_no"));
                auctionDobj.setStateCdFrom(rs.getString("state_cd_from"));
                auctionDobj.setOffCdFrom(rs.getInt("off_cd_from"));
                auctionDobj.setAuctionDate(rs.getDate("auction_dt"));
                auctionDobj.setFirNo(rs.getString("fir_no"));
                auctionDobj.setFirDate(rs.getDate("fir_dt"));
                auctionDobj.setReason(rs.getString("reason"));
                auctionDobj.setOrderNo(rs.getString("order_no"));
                auctionDobj.setRegnDt(rs.getDate("regn_dt"));
                auctionDobj.setAuctionBy(rs.getString("auction_by"));
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem In getting Details of Auction");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return auctionDobj;
    }

    public String insertAuctionDetails(Status_dobj status, AuctionDobj dobj) throws VahanException {
        String appl_no = "";
        int i = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-YYYY");
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        int action_cd = 0;
        int actionCodeArray[] = null;
        try {
            String sql = "INSERT INTO " + TableList.VA_AUCTION + "(\n"
                    + "             state_cd, off_cd, appl_no, regn_no, chasi_no, state_cd_from, \n"
                    + " off_cd_from,order_no,auction_dt, fir_no, fir_dt, reason, op_dt,regn_dt,auction_by) \n"
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, \n"
                    + "            ?, ?,?,?,?, current_timestamp,?,?)";

            tmgr = new TransactionManager("insertAuctionDetails");
            if (dobj.getRegnNo() != null && !dobj.getRegnNo().equals("NEW") && dobj.getAuctionBy().equals("C")) {
                boolean isRegnNoExist = NewVehicleNo.checkNumberAssignable(dobj.getRegnNo(), false, tmgr);
                if (!isRegnNoExist) {
                    throw new VahanException("Registration Number " + dobj.getRegnNo() + " already exist in Vahan.Please do Auction through Registration Number");
                }
            }
            appl_no = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());//generate a new application no. 

            //if appl no is null check is required
            int j = 1;
            ps = tmgr.prepareStatement(sql);
            ps.setString(j++, Util.getUserStateCode());
            ps.setInt(j++, Util.getSelectedSeat().getOff_cd());
            ps.setString(j++, appl_no);
            ps.setString(j++, dobj.getRegnNo());
            ps.setString(j++, dobj.getChasiNo());
            ps.setString(j++, dobj.getStateCdFrom());
            ps.setInt(j++, dobj.getOffCdFrom());
            ps.setString(j++, dobj.getOrderNo());
            ps.setDate(j++, new java.sql.Date(dobj.getAuctionDate().getTime()));
            ps.setString(j++, dobj.getFirNo());
            ps.setDate(j++, new java.sql.Date(dobj.getFirDate().getTime()));
            ps.setString(j++, dobj.getReason());
            if (dobj.getRegnDt() == null) {
                ps.setNull(j++, java.sql.Types.DATE);
            } else {
                ps.setDate(j++, new java.sql.Date(dobj.getRegnDt().getTime()));
            }
            ps.setString(j++, dobj.getAuctionBy());

            i = ps.executeUpdate();
            if (i > 0) {
                actionCodeArray = ServerUtil.getInitialAction(tmgr, status.getState_cd(), status.getPur_cd(), null);

                if (actionCodeArray == null) {
                    throw new VahanException("Initial Action Code is Not Available!");
                }

                action_cd = actionCodeArray[0];
                status.setAppl_no(appl_no);
                status.setAction_cd(action_cd);//Initial Action_cd
                ServerUtil.insertIntoVaStatus(tmgr, status);
                ServerUtil.insertIntoVaDetails(tmgr, status);

                status.setStatus(TableConstants.STATUS_COMPLETE);
                status = ServerUtil.webServiceForNextStage(status, tmgr);
                ServerUtil.fileFlow(tmgr, status); //for updateing va_status

                tmgr.commit();
            } else {
                appl_no = "";
            }
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
        return appl_no;
    }

    public void update_Auction_Status(AuctionDobj auctionDobj, AuctionDobj auction_dobj_prv, Status_dobj status_dobj, String changedData, Owner_dobj ownerDetail) throws VahanException {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;

        try {

            tmgr = new TransactionManager("update_Auction_Status");
            //=================WEB SERVICES FOR NEXTSTAGE START=========//
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            if (!status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                if (auctionDobj.getRegnNo() != null && !auctionDobj.getRegnNo().equals("NEW") && auctionDobj.getAuctionBy().equals("C")) {
                    boolean isRegnNoExist = NewVehicleNo.checkNumberAssignable(auctionDobj.getRegnNo(), false, tmgr);
                    if (!isRegnNoExist) {
                        throw new VahanException("Registration Number " + auctionDobj.getRegnNo() + " already exist in Vahan.Please do Auction through Registration Number");
                    }
                }
            }

            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_AUCTION_ENTRY
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_AUCTION_VERIFICATION
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_AUCTION_APPROVAL) {

                if ((changedData != null && !changedData.equals("")) || auction_dobj_prv == null) {
                    this.insertUpdateAuction(tmgr, auctionDobj);//when there is change by user or Entry Scrutiny
                }

            }

            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_AUCTION_APPROVAL
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                sql = "insert into " + TableList.VT_AUCTION
                        + " SELECT state_cd, off_cd,regn_no, chasi_no,state_cd_from,off_cd_from,order_no,auction_dt, fir_no, fir_dt, reason, current_timestamp,regn_dt,auction_by "
                        + " FROM " + TableList.VA_AUCTION
                        + " WHERE appl_no=? and state_cd = ? and off_cd = ? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, auctionDobj.getApplNo());
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, Util.getUserSeatOffCode());
                ps.executeUpdate();

                this.insertIntoAuctionHistory(tmgr, auctionDobj.getApplNo());

                sql = "DELETE FROM " + TableList.VA_AUCTION + " where appl_no = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, auctionDobj.getApplNo());
                ps.executeUpdate();

                //for updating the status of application when it is approved start
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                //for updating the status of application when it is approved end
                if (auctionDobj != null && !auctionDobj.getRegnNo().equals("NEW") && auctionDobj.getAuctionBy().equals("R")) {
                    //new OwnerImpl().insertInVhOwnerFromVtOwner(auctionDobj.getApplNo(), auctionDobj.getRegnNo(), tmgr);
                    ownerDetail.setAppl_no(auctionDobj.getApplNo());
                    new OwnerAdminImpl().insertIntoVh_owner(ownerDetail, tmgr);
                    status_dobj.setEmp_cd(Long.parseLong(Util.getEmpCode()));
                    HpaImpl.insertIntoHypthVH(tmgr, status_dobj, auctionDobj.getStateCdFrom(), auctionDobj.getOffCdFrom(), auctionDobj.getRegnNo());

                    HpaImpl.deleteFromVtHypth(tmgr, auctionDobj.getRegnNo(), auctionDobj.getStateCdFrom(), auctionDobj.getOffCdFrom());

                    //vt_tax_clear
                    sql = "INSERT INTO " + TableList.VT_TAX_CLEAR + "(\n"
                            + "            state_cd, off_cd, appl_no, regn_no, pur_cd, clear_fr, clear_to, \n"
                            + "            tcr_no, iss_auth, iss_dt, remark, user_cd, op_dt, rcpt_no, rcpt_dt)\n"
                            + "    VALUES (?, ?, ?, ?, ?, ?, ?, \n"
                            + "            ?, ?, current_timestamp, ?, ?, current_timestamp, ?, current_timestamp)";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, Util.getUserStateCode());
                    ps.setInt(2, Util.getUserSeatOffCode());
                    ps.setString(3, auctionDobj.getApplNo());
                    ps.setString(4, auctionDobj.getRegnNo());
                    ps.setInt(5, TableConstants.TM_ROAD_TAX);
                    ps.setDate(6, new java.sql.Date(ownerDetail.getRegn_dt().getTime()));
                    ps.setDate(7, new java.sql.Date(auctionDobj.getAuctionDate().getTime()));
                    ps.setString(8, "Auction");
                    ps.setString(9, Util.getSelectedSeat().getSeat_cd());
                    ps.setString(10, TableConstants.AUCTION_REMARK);
                    ps.setString(11, Util.getEmpCode());
                    ps.setString(12, "");
                    ps.executeUpdate();

                    this.updateVtOwnerStatus(tmgr, auctionDobj.getRegnNo(), TableConstants.VT_AUCTION_STATUS);
                    if (Util.getUserStateCode() != null && Util.getUserStateCode().equals("BR") && auctionDobj.isAllowToMoveBlackListedDataToHistory()) {
                        sql = "INSERT INTO " + TableList.VH_BLACKLIST + "(\n"
                                + "            state_cd, off_cd, regn_no, complain_type, fir_no, fir_dt, complain, \n"
                                + "            complain_dt, entered_by, action_taken, action_dt, action_taken_by, \n"
                                + "            compounding_amt)\n"
                                + "   SELECT state_cd, off_cd, regn_no, complain_type, fir_no, fir_dt, complain, \n"
                                + "       complain_dt, entered_by,?,current_timestamp,?,compounding_amt\n"
                                + "  FROM " + TableList.VT_BLACKLIST + " where regn_no = ? and complain_type <> " + TableConstants.BLCompoundingAmtCode + "";

                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, "Vehicle is auctioned in " + ServerUtil.getStateNameByStateCode(Util.getUserStateCode()) + " and " + ServerUtil.getOfficeName(Util.getUserSeatOffCode(), Util.getUserStateCode()) + "");
                        ps.setString(2, Util.getEmpCode());
                        ps.setString(3, auctionDobj.getRegnNo());
                        ps.executeUpdate();

                        sql = "DELETE FROM " + TableList.VT_BLACKLIST + " where regn_no = ? and complain_type <> " + TableConstants.BLCompoundingAmtCode + "";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, auctionDobj.getRegnNo());
                        ps.executeUpdate();

                        sql = "INSERT INTO " + TableList.VH_BLACKLIST_CHASSIS + "(\n"
                                + "            state_cd, off_cd, chasi_no, complain_type, fir_no, fir_dt, complain, \n"
                                + "            complain_dt, entered_by, action_taken, action_dt, action_taken_by)\n"
                                + "   SELECT state_cd, off_cd, chasi_no, complain_type, fir_no, fir_dt, complain, \n"
                                + "       complain_dt, entered_by,?,current_timestamp,?,compounding_amt\n"
                                + "  FROM " + TableList.VT_BLACKLIST_CHASSIS + " where chasi_no = ? and complain_type <> " + TableConstants.BLCompoundingAmtCode + "";

                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, "Vehicle is auctioned in " + ServerUtil.getStateNameByStateCode(Util.getUserStateCode()) + " and " + ServerUtil.getOfficeName(Util.getUserSeatOffCode(), Util.getUserStateCode()) + "");
                        ps.setString(2, Util.getEmpCode());
                        ps.setString(3, auctionDobj.getChasiNo());
                        ps.executeUpdate();

                        sql = "DELETE FROM " + TableList.VT_BLACKLIST_CHASSIS + " where chasi_no = ? and complain_type <> " + TableConstants.BLCompoundingAmtCode + "";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, auctionDobj.getChasiNo());
                        ps.executeUpdate();

                    }
                }
            }

            insertIntoVhaChangedData(tmgr, auctionDobj.getApplNo(), changedData);//for saving the data into table those are changed by the user

            ServerUtil.fileFlow(tmgr, status_dobj); //for updateing va_status and vha status for new role,seat for new emp

            tmgr.commit();//Commiting data here....

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

    }

    public void insertUpdateAuction(TransactionManager tmgr, AuctionDobj auctionDobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "SELECT regn_no FROM " + TableList.VA_AUCTION + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, auctionDobj.getApplNo());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) { //if any record is exist then update otherwise insert it
                this.insertIntoAuctionHistory(tmgr, auctionDobj.getApplNo());
                this.updateAuction(tmgr, auctionDobj);
            } else {
                this.insertIntoAuction(tmgr, auctionDobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public void insertIntoAuction(TransactionManager tmgr, AuctionDobj auctionDobj) throws Exception {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VA_AUCTION + "(\n"
                + "             state_cd, off_cd, appl_no, regn_no, chasi_no, state_cd_from, \n"
                + " off_cd_from,order_no,auction_dt, fir_no, fir_dt, reason, op_dt,regn_dt,auction_by) \n"
                + "    VALUES (?, ?, ?, ?, ?, ?, ?,?,?, \n"
                + "            ?, ?, current_timestamp,?,?)";

        ps = tmgr.prepareStatement(sql);
        int j = 1;
        ps.setString(j++, Util.getUserStateCode());
        ps.setInt(j++, Util.getUserSeatOffCode());
        ps.setString(j++, auctionDobj.getApplNo());
        ps.setString(j++, auctionDobj.getRegnNo());
        ps.setString(j++, auctionDobj.getChasiNo());
        ps.setString(j++, auctionDobj.getStateCdFrom());
        ps.setInt(j++, auctionDobj.getOffCdFrom());
        ps.setString(j++, auctionDobj.getOrderNo());
        ps.setDate(j++, new java.sql.Date(auctionDobj.getAuctionDate().getTime()));
        ps.setString(j++, auctionDobj.getFirNo());
        ps.setDate(j++, new java.sql.Date(auctionDobj.getFirDate().getTime()));
        ps.setString(j++, auctionDobj.getReason());
        if (auctionDobj.getRegnDt() == null) {
            ps.setNull(j++, java.sql.Types.DATE);
        } else {
            ps.setDate(j++, new java.sql.Date(auctionDobj.getRegnDt().getTime()));
        }
        ps.setString(j++, auctionDobj.getAuctionBy());
        ps.executeUpdate();
    }

    public void insertIntoAuctionHistory(TransactionManager tmgr, String appl_no) throws Exception {
        PreparedStatement ps = null;
        String sql = null;

        //inserting data into vha_tax_exem from va_tax_exem
        sql = "INSERT INTO " + TableList.VHA_AUCTION
                + " SELECT current_timestamp as moved_on, ? as moved_by ,* "
                + "  FROM  " + TableList.VA_AUCTION
                + " WHERE  appl_no=? ";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    public void updateAuction(TransactionManager tmgr, AuctionDobj auctionDobj) throws Exception {
        PreparedStatement ps = null;
        String sql = null;
        int j = 1;

        //updation of va_tax_exem
        sql = " update " + TableList.VA_AUCTION
                + " set  auction_dt=?,"
                + " fir_no=?,"
                + " fir_dt=?,"
                + " reason=?,"
                + " order_no=?,"
                + " state_cd_from=?,"
                + " off_cd_from=?,"
                + " op_dt=current_timestamp,"
                + " regn_no=?,"
                + " regn_dt=?"
                + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setDate(j++, new java.sql.Date(auctionDobj.getAuctionDate().getTime()));
        ps.setString(j++, auctionDobj.getFirNo());
        ps.setDate(j++, new java.sql.Date(auctionDobj.getFirDate().getTime()));
        ps.setString(j++, auctionDobj.getReason());
        ps.setString(j++, auctionDobj.getOrderNo());
        ps.setString(j++, auctionDobj.getStateCdFrom());
        ps.setInt(j++, auctionDobj.getOffCdFrom());
        ps.setString(j++, auctionDobj.getRegnNo());
        if (auctionDobj.getRegnDt() == null) {
            ps.setNull(j++, java.sql.Types.DATE);
        } else {
            ps.setDate(j++, new java.sql.Date(auctionDobj.getRegnDt().getTime()));
        }
        ps.setString(j++, auctionDobj.getApplNo());
        ps.executeUpdate();
    }

    public AuctionDobj getDetailsFromVtAuction(String chasiNo, String regnNo) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        AuctionDobj auctionDobj = null;
        String sql = "select *  from " + TableList.VT_AUCTION + " ";
        try {
            tmgr = new TransactionManagerReadOnly("getDetailsFromVtAuction");
            if (chasiNo != null) {
                sql = sql + "where chasi_no = ?";
            } else if (regnNo != null) {
                sql = sql + "where regn_no = ?";
            }
            ps = tmgr.prepareStatement(sql);
            if (chasiNo != null) {
                ps.setString(1, chasiNo);
            } else if (regnNo != null) {
                ps.setString(1, regnNo);
            }

            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                auctionDobj = new AuctionDobj();
                auctionDobj.setAuctionDate(rs.getDate("auction_dt"));
                auctionDobj.setRegnNo(rs.getString("regn_no"));
                auctionDobj.setFirDate(rs.getDate("fir_dt"));
                auctionDobj.setFirNo(rs.getString("fir_no"));
                auctionDobj.setStateCdFrom(rs.getString("state_cd_from"));
                auctionDobj.setOffCdFrom(rs.getInt("off_cd_from"));
                auctionDobj.setReason(rs.getString("reason"));
                auctionDobj.setOrderNo(rs.getString("order_no"));
                auctionDobj.setStateCd(rs.getString("state_cd"));
                auctionDobj.setOffCd(rs.getInt("off_cd"));
                auctionDobj.setChasiNo(rs.getString("chasi_no"));
                auctionDobj.setRegnDt(rs.getDate("regn_dt"));
                auctionDobj.setAuctionBy(rs.getString("auction_by"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem In getting Details of Auction");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return auctionDobj;
    }

    public void makeChangeAuction(AuctionDobj auctionDobj, String changedata) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("makeChangeAuction");
            this.insertUpdateAuction(tmgr, auctionDobj);
            ComparisonBeanImpl.updateChangedData(auctionDobj.getApplNo(), changedata, tmgr);
            tmgr.commit();
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
    }

    public void insertIntoVhAuction(TransactionManager tmgr, Status_dobj dobj, String oldRegn, String chasiNo) throws Exception {

        PreparedStatement ps = null;
        int pos = 1;
        String sql = "INSERT INTO " + TableList.VH_AUCTION
                + " SELECT state_cd, off_cd, regn_no, chasi_no, state_cd_from, off_cd_from, "
                + " order_no,auction_dt, fir_no, fir_dt, reason, op_dt,?,?,current_timestamp,?,regn_dt,auction_by "
                + "  FROM " + TableList.VT_AUCTION + " where regn_no = ? ";
        if (oldRegn != null && oldRegn.equals("NEW")) {
            sql = sql + " and  chasi_no = ? ";
        }
        ps = tmgr.prepareStatement(sql);

        ps.setString(pos++, dobj.getAppl_no());
        ps.setString(pos++, dobj.getRegn_no());
        ps.setString(pos++, String.valueOf(dobj.getEmp_cd()));
        ps.setString(pos++, oldRegn);
        if (oldRegn != null && oldRegn.equals("NEW")) {
            ps.setString(pos++, chasiNo);
        }
        ps.executeUpdate();
    }

    public AuctionDobj getVtAuctionDetailsForRegnDtUpdation(String applNo) throws VahanException {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        AuctionDobj auctionDobj = null;

        try {
            tmgr = new TransactionManager("getVtAuctionDetailsForRegnDtUpdation");
            ps = tmgr.prepareStatement("select * \n"
                    + "from " + TableList.VT_AUCTION + " t inner join " + TableList.VA_OWNER + " o on t.chasi_no =  o.chasi_no \n"
                    + "where o.appl_no = ? and o.regn_type = '" + TableConstants.VM_REGN_TYPE_CONFISCATED_AUCTION + "'");
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                auctionDobj = new AuctionDobj();
                auctionDobj.setStateCd(rs.getString("state_cd"));
                auctionDobj.setOffCd(rs.getInt("off_cd"));
                auctionDobj.setApplNo(rs.getString("appl_no"));
                auctionDobj.setRegnNo(rs.getString("regn_no"));
                auctionDobj.setChasiNo(rs.getString("chasi_no"));
                auctionDobj.setStateCdFrom(rs.getString("state_cd_from"));
                auctionDobj.setOffCdFrom(rs.getInt("off_cd_from"));
                auctionDobj.setAuctionDate(rs.getDate("auction_dt"));
                auctionDobj.setFirNo(rs.getString("fir_no"));
                auctionDobj.setFirDate(rs.getDate("fir_dt"));
                auctionDobj.setReason(rs.getString("reason"));
                auctionDobj.setOrderNo(rs.getString("order_no"));
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem In getting Details of Auction");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return auctionDobj;
    }

    public AuctionDobj getVtAuctionDetailsOnBasisOfStateCd(String regnNo, String stateCd) throws VahanException {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        AuctionDobj auctionDobj = null;

        try {
            tmgr = new TransactionManager("getVtAuctionDetailsForRegnDtUpdation");
            ps = tmgr.prepareStatement("select * \n"
                    + "from " + TableList.VT_AUCTION + " t where regn_no = ? and state_cd = ? ");
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                auctionDobj = new AuctionDobj();
                auctionDobj.setStateCd(rs.getString("state_cd"));
                auctionDobj.setOffCd(rs.getInt("off_cd"));
                auctionDobj.setRegnNo(rs.getString("regn_no"));
                auctionDobj.setChasiNo(rs.getString("chasi_no"));
                auctionDobj.setStateCdFrom(rs.getString("state_cd_from"));
                auctionDobj.setOffCdFrom(rs.getInt("off_cd_from"));
                auctionDobj.setAuctionDate(rs.getDate("auction_dt"));
                auctionDobj.setFirNo(rs.getString("fir_no"));
                auctionDobj.setFirDate(rs.getDate("fir_dt"));
                auctionDobj.setReason(rs.getString("reason"));
                auctionDobj.setOrderNo(rs.getString("order_no"));
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem In getting Details of Auction");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return auctionDobj;
    }

    public boolean isAuctionDetailsExist(String regnNo, String chasiNo) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        boolean isExist = false;
        String sql = null;
        try {
            tmgr = new TransactionManagerReadOnly("getDetailsFromVtAuction");
            if (regnNo != null && !regnNo.isEmpty() && !regnNo.equals("NEW")) {
                sql = "select regn_no  from " + TableList.VT_AUCTION + " t where t.regn_no = ? union all select regn_no from " + TableList.VA_AUCTION + "  a where a.regn_no = ? ";
            } else {
                sql = "select chasi_no  from " + TableList.VT_AUCTION + " t where t.chasi_no = ? union all select chasi_no from " + TableList.VA_AUCTION + "  a where a.chasi_no = ?";

            }
            ps = tmgr.prepareStatement(sql);
            if (regnNo != null && !regnNo.isEmpty() && !regnNo.equals("NEW")) {
                ps.setString(1, regnNo);
                ps.setString(2, regnNo);
            } else {
                ps.setString(1, chasiNo);
                ps.setString(2, chasiNo);
            }

            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isExist = true;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem In getting Details of Auction");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return isExist;
    }

    public int countOfChassisNoAgainstRegnNo(String regnNo) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        int countOfChassis = 0;
        try {
            tmgr = new TransactionManagerReadOnly("countOfChassisNoAgainstRegnNo");
            String sql = "select count(chasi_no) as count from vt_owner where chasi_no = (select chasi_no from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);

            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                countOfChassis = rs.getInt("count");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem In getting Details of Auction");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return countOfChassis;
    }

    public void updateVtOwnerStatus(TransactionManager tmgr, String regnNo, String status) throws Exception {
        PreparedStatement ps = null;
        String sql = null;

        sql = "update " + TableList.VT_OWNER + " set status = ? where regn_no = ?";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, status);
        ps.setString(2, regnNo);
        ps.executeUpdate();
    }

    public String checkChassisNoExist(String chasino, String regnType) throws VahanException {
        TransactionManager tmgr = null;
        String chasiNoExistMess = null;

        try {
            tmgr = new TransactionManager("checkChassisNoExist");
            chasiNoExistMess = new OwnerImpl().isChasiAlreadyExist(tmgr, chasino, regnType);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem In getting Details of Chassis No.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return chasiNoExistMess;
    }
}
