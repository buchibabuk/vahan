/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.lang.String;
import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.RowSet;
import javax.xml.xpath.XPathExpressionException;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.FancyAuctionDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.common.Draft_dobj;
import nic.vahan.server.ServerUtil;
import nic.vahan.server.workdistribution.InitialStageWS;
import nic.vahan.server.workdistribution.NextStageRequest;
import nic.vahan.server.workdistribution.NextStageResponse;
import nic.vahan.server.workdistribution.NextStageWS;
import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

/**
 *
 * @author AMBRISH
 */
public class FancyAuctionVerifyImpl {

    private static final Logger LOGGER = Logger.getLogger(FancyNumberImpl.class);

    public void getFancyApplicationDetails(String appl_no) {
    }

    public ArrayList<FancyAuctionDobj> getFancyApplicationDetailsNew(String regn_no) throws VahanException {
        ArrayList<FancyAuctionDobj> list = new ArrayList();
        PreparedStatement ps;
        TransactionManager tmgr = null;
        VahanException vahanexecption;

        try {

            tmgr = new TransactionManager("getAll_E_payments");
            ps = tmgr.prepareStatement("select recp_no, to_char(recp_dt,'DD-MON-YYYY') as recp_dt, regn_appl_no, regn_no, app_auth, dt_of_app, file_no, owner_name, c_add1, c_add2, c_village, c_taluk, c_district,"
                    + " c_pincode, reserve_amt, draft_amt, auction_amt, total_amt, auction_dt,auction_amt_recp_no, aution_amt_recp_dt, attendance_at_auction,status, state_cd, rto_cd from va_fancy_register where regn_no=? and attendance_at_auction is null");
            ps.setString(1, regn_no);

            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                FancyAuctionDobj dobj = new FancyAuctionDobj();
                dobj.setRecp_no(rs.getString("recp_no"));
                dobj.setRecp_dt(rs.getString("recp_dt"));
                dobj.setRegn_appl_no(rs.getString("regn_appl_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setApp_auth(rs.getString("app_auth"));
                dobj.setDt_of_app(rs.getString("dt_of_app"));
                dobj.setFile_no(rs.getString("file_no"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setC_add1(rs.getString("c_add1"));
                dobj.setC_add2(rs.getString("c_add2"));
                dobj.setC_village(rs.getInt("c_village"));
                dobj.setC_taluk(rs.getInt("c_taluk"));
                dobj.setC_district(rs.getInt("c_district"));
                dobj.setC_pincode(rs.getString("c_pincode"));
                dobj.setReserve_amt(rs.getInt("reserve_amt"));
                dobj.setDraft_amt(rs.getInt("draft_amt"));
                dobj.setAuction_amt(rs.getInt("auction_amt"));
                dobj.setTotal_amt(rs.getInt("total_amt"));
                dobj.setStatus("Aution Pending");
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setRto_cd(rs.getInt("rto_cd"));
                dobj.setAttendance_at_auction(rs.getString("attendance_at_auction"));

                list.add(dobj);
            }



        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            vahanexecption = new VahanException("Error in fetching details for [  Fancy Number Auction  ]");
            throw vahanexecption;
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }


        return list;

    }

    public ArrayList<FancyAuctionDobj> getFancyApplicationDetailsVerifyApprove(String regn_no) throws VahanException {
        ArrayList<FancyAuctionDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        VahanException vahanexecption = null;

        try {

            tmgr = new TransactionManager("getAll_E_payments");
            ps = tmgr.prepareStatement("select recp_no, to_char(recp_dt,'DD-MON-YYYY') as recp_dt, regn_appl_no, regn_no, app_auth, dt_of_app, file_no, owner_name, c_add1, c_add2, c_village, c_taluk, c_district,"
                    + " c_pincode, reserve_amt, draft_amt, auction_amt, total_amt, auction_dt,auction_amt_recp_no, aution_amt_recp_dt, attendance_at_auction,status, state_cd, rto_cd from va_fancy_register where regn_no=?");
            ps.setString(1, regn_no);

            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {// found
                FancyAuctionDobj dobj = new FancyAuctionDobj();
                dobj.setRecp_no(rs.getString("recp_no"));
                dobj.setRecp_dt(rs.getString("recp_dt"));
                dobj.setRegn_appl_no(rs.getString("regn_appl_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setApp_auth(rs.getString("app_auth"));
                dobj.setDt_of_app(rs.getString("dt_of_app"));
                dobj.setFile_no(rs.getString("file_no"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setC_add1(rs.getString("c_add1"));
                dobj.setC_add2(rs.getString("c_add2"));
                dobj.setC_village(rs.getInt("c_village"));
                dobj.setC_taluk(rs.getInt("c_taluk"));
                dobj.setC_district(rs.getInt("c_district"));
                dobj.setC_pincode(rs.getString("c_pincode"));
                dobj.setReserve_amt(rs.getInt("reserve_amt"));
                dobj.setDraft_amt(rs.getInt("draft_amt"));
                dobj.setAuction_amt(rs.getInt("auction_amt"));
                dobj.setTotal_amt(rs.getInt("total_amt"));
                dobj.setStatus(rs.getString("status"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setRto_cd(rs.getInt("rto_cd"));
                dobj.setAttendance_at_auction(rs.getString("attendance_at_auction"));



                list.add(dobj);
            }



        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            vahanexecption = new VahanException("Error in fetching details for [  Fancy Number Auction  ]");
            throw vahanexecption;
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }


        return list;

    }

    public ArrayList<String> getAllFancyNumberAuction() throws VahanException {
        ArrayList<String> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        VahanException vahanexecption = null;

        try {

            tmgr = new TransactionManager("getAll_E_payments");
            ps = tmgr.prepareStatement("select distinct(regn_no) from va_fancy_register where attendance_at_auction is null");

            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {// found

                String str = rs.getString("regn_no");

                list.add(str);
            }



        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            vahanexecption = new VahanException("Error in fetching details for [  Fancy Number Auction  ]");
            throw vahanexecption;
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }


        return list;
    }

    public RowSet getOpenSeriesDetails(String state_cd, int off_cd) throws VahanException {
        RowSet rs = null;
        TransactionManager tmgr = null;
        try {

            tmgr = new TransactionManager("Fancy_Auction_Verify_Impl:getOpenSeriesDetails");
            PreparedStatement ps = tmgr.prepareStatement("select to_char(date_from,'dd-MON-yyyy') as date_from ,"
                    + "to_char(date_upto,'dd-MON-yyyy') as date_upto,reg_series,regn_no_from,"
                    + "regn_no_upto from vt_fancy_range where state_cd=? and off_cd=?");
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            rs = tmgr.fetchDetachedRowSet_No_release();
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
        return rs;
    }

    public boolean saveAuctionDetailsEntry(ArrayList<FancyAuctionDobj> listAuctionApplicationNumbers, HashMap<String, ArrayList<Draft_dobj>> draft_details) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        VahanException vahanexecption = null;
        boolean dataSavedSuccessfully = true;
        try {

            tmgr = new TransactionManager("save_updateAll_Auction_details");


            for (int i = 0; i < listAuctionApplicationNumbers.size(); i++) {
                FancyAuctionDobj Fancy_Auction_dobj = listAuctionApplicationNumbers.get(i);
                String updateQuery = "UPDATE va_fancy_register SET  offer_amt=?, draft_amt=?,auction_amt=?, total_amt=?, bal_amount=?, attendance_at_auction=?, status=? WHERE regn_appl_no = ? and regn_no=?";
                ps = tmgr.prepareStatement(updateQuery);
                ps.setInt(1, Fancy_Auction_dobj.getOffer_amt());
                ps.setInt(2, Fancy_Auction_dobj.getDraft_amt());
                ps.setInt(3, Fancy_Auction_dobj.getAuction_amt());
                ps.setInt(4, Fancy_Auction_dobj.getTotal_amt());
                ps.setInt(5, Fancy_Auction_dobj.getBal_amt());
                ps.setString(6, Fancy_Auction_dobj.getAttendance_at_auction());
                ps.setString(7, Fancy_Auction_dobj.getStatus());
                ps.setString(8, Fancy_Auction_dobj.getRegn_appl_no());
                ps.setString(9, Fancy_Auction_dobj.getRegn_no());

                if (ps.executeUpdate() < 1) {
                    dataSavedSuccessfully = false;
                    break;
                }

            }
            saveDraftEntries(draft_details, tmgr);

            applicationFlow(tmgr, "", "");

            //===============WEB SERVICES FOR NEXTSTAGE END============//
            if (dataSavedSuccessfully) {
                tmgr.commit();
            }




        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            vahanexecption = new VahanException("Error in updating details for [  Fancy Number Auction  ]");
            throw vahanexecption;
        } finally {
            try {
                tmgr.release();
                tmgr = null;
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }

        return dataSavedSuccessfully;
    }

    public void verifyFancyApplication(String officeRemark, String publicRemark) throws SQLException, VahanException, Exception {
        /*
     
         * 
         * Some logic may be written here, 
         * currently it is only moving application to the next seat for the verification purpose.
         * 
         */
        TransactionManager tmgr = new TransactionManager("FANCY_VERIFY");
        try {
            applicationFlow(tmgr, officeRemark, publicRemark);
            tmgr.commit();
        } finally {
            tmgr.release();
        }


    }

    public void approveFancyApplication(FancyAuctionDobj auction_winner, String officeRemark, String publicRemark) throws SQLException, VahanException, Exception {
        /*
     
         * 
         * Some logic may be written here
         * 
         */
        TransactionManager tmgr = new TransactionManager("FANCY_APPROVE");
        try {

            String VHA_QUERY = "insert into vha_fancy_register SELECT recp_no, recp_dt, regn_appl_no, regn_no, chasi_no, app_auth, "
                    + "       dt_of_app, file_no, owner_name, c_add1, c_add2, c_village, c_taluk, "
                    + "       c_district, c_pincode, reserve_amt, draft_amt, auction_amt, total_amt, "
                    + "       auction_dt, auction_amt_recp_no, aution_amt_recp_dt, attendance_at_auction, "
                    + "       status, offer_amt, bal_amount,current_timestamp as moved_on,'TEST' as moved_by, state_cd, rto_cd "
                    + "  FROM va_fancy_register  where regn_no=?";
            String delete = "delete from va_fancy_register where regn_no=?";
            String insert = "insert into vt_fancy_register select * from va_fancy_register where regn_no=? and status='A'";

            PreparedStatement ps = tmgr.prepareStatement(insert);
            ps.setString(1, auction_winner.getRegn_no());
            ps.executeUpdate();

            ps = tmgr.prepareStatement(VHA_QUERY);
            ps.setString(1, auction_winner.getRegn_no());
            ps.executeUpdate();

            ps = tmgr.prepareStatement(delete);
            ps.setString(1, auction_winner.getRegn_no());
            ps.executeUpdate();

            applicationFlow(tmgr, officeRemark, publicRemark);
            tmgr.commit();
        } finally {
            tmgr.release();
        }


    }

    private void saveDraftEntries(HashMap<String, ArrayList<Draft_dobj>> draft_details, TransactionManager tmgr) throws SQLException {
        PreparedStatement ps;
        Timestamp current_date_time_postgresFormat = ServerUtil.getSystemDateInPostgres();
        for (Map.Entry<String, ArrayList<Draft_dobj>> entry : draft_details.entrySet()) {
            String string = entry.getKey();
            ArrayList<Draft_dobj> arrayList = entry.getValue();
            for (int i = 0; i < arrayList.size(); i++) {
                Draft_dobj draft_dobj = arrayList.get(i);
                String Query = "INSERT INTO va_draft( appl_no, draft_cd, flag, draft_num, dated, amount, bank_code,branch_name, received_dt,collected_by, state_cd, off_cd)"
                        + "VALUES (?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?)";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, draft_dobj.getAppl_no());
                ps.setInt(2, i + 1); // Assuming that this is a Sr. No of the Draft.
                ps.setString(3, "A"); // Discussion Required for this column. I have just put A because it is a not Null column.
                ps.setString(4, draft_dobj.getDraft_num());
                ps.setDate(5, new java.sql.Date(draft_dobj.getDated().getTime()));
                ps.setLong(6, draft_dobj.getAmount());
                ps.setString(7, draft_dobj.getBank_code());
                ps.setString(8, draft_dobj.getBranch_name());
                ps.setTimestamp(9, current_date_time_postgresFormat); // cuurent date with time time
                ps.setString(10, Util.getEmpCode());
                ps.setString(11, TableConstants.STATE_CD);
                ps.setInt(12, draft_dobj.getOff_cd()); // need to carry this field data from database.
                ps.executeUpdate();
            }
        }
    }

    private void applicationFlow(TransactionManager tmgr, String officeRemark, String publicRemark) throws VahanException {
        //=================WEB SERVICES FOR NEXTSTAGE START=========//
        Status_dobj status = new Status_dobj();
        Map map = (Map) Util.getSession().getAttribute("seat_map");
        String PUR_CD = (String) map.get("pur_code");
        String APPL_NO = map.get("appl_no").toString();
        /////////////////////////////////////////////////////
        int ACTION_CDOE = Integer.parseInt(map.get("role").toString().trim());
        int ROLE_CD = Integer.parseInt(map.get("role").toString().substring(3));// for extracting role_code from action_code
        /////////////////////////////////////////////////////

        String APPL_DT = map.get("appl_dt") != null ? map.get("appl_dt").toString() : "";



        String REGN_NO = map.get("regn_no").toString();
        String CUR_STATUS = map.get("cur_status").toString();

        NextStageRequest request = new NextStageRequest();

        request.setAction_cd(ACTION_CDOE);
        request.setCntr_id(Util.getSession().getAttribute("selected_cntr_id").toString()); // getting from Map
        request.setAppl_no(APPL_NO); // getting from map
        request.setEmp_cd(Long.parseLong(Util.getEmpCode().trim())); // getting from session
        request.setFile_movement_type(TableConstants.FORWARD);
        request.setPur_cd(Integer.parseInt(PUR_CD)); // getting from Map
        request.setState_cd(Util.getUserStateCode()); //getting from session
        request.setOff_cd(Util.getUserSeatOffCode()); // getting from session

        NextStageResponse response = NextStageWS.getNextStageResponse(request);

        status.setStatus(TableConstants.STATUS_COMPLETE); // setting status as complete, because user can not sent back.
        status.setOffice_remark(officeRemark);
        status.setPublic_remark(publicRemark);
        status.setAppl_no(APPL_NO);
        status.setCntr_id(response.getCntr_id());
        status.setEmp_cd(response.getEmp_cd());
        status.setAction_cd(response.getAction_cd());
        status.setRto_code(response.getRto_code());
        status.setOff_cd(response.getOff_cd());
        status.setFlow_slno(response.getFlow_slno());

        ServerUtil.fileFlow(tmgr, status);
    }

    /**
     * Cuurently Assuming that only one auction event will be open at one point
     * of time. Means user can not apply for two different auctions at the same
     * time. File will be inwarded only once at the end of the allowed period,
     * and once applications are inwared for the auction / bidding / lottery or
     * number allotment. Logic is very simple -- reading distinct list of
     * regn_no from va_fancy_register and creating an application of each unique
     * registrain no with the help of GetInitialflowforVahan web service.
     *
     * @return List<Fancy_Auction_dobj>
     * @throws VahanException
     */
    public Map<String, List<FancyAuctionDobj>> getFancyNumberListPendingForInward(int off_cd) throws VahanException {
        Map<String, List<FancyAuctionDobj>> returnList = new HashMap<>();
        String sqlGetFancyNumberListPendingForInward = "select recp_no,to_char(recp_dt,'dd-MON-yyyy') as recp_dt,"
                + "regn_appl_no,regn_no,owner_name from " + TableList.VA_FANCY_REGISTER
                + " where rto_cd = ? and status is null order by regn_no";
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("Fancy_Auction_Verify_Impl:getFancyNumberListPendingForInward");
            PreparedStatement prstmt = tmgr.prepareStatement(sqlGetFancyNumberListPendingForInward);
            prstmt.setInt(1, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                FancyAuctionDobj dobj = new FancyAuctionDobj();
                dobj.setRecp_no(rs.getString("recp_no"));
                dobj.setRecp_dt(rs.getString("recp_dt"));
                dobj.setRegn_appl_no(rs.getString("regn_appl_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setOwner_name(rs.getString("owner_name"));

                if (returnList.get(dobj.getRegn_no()) == null) {
                    List<FancyAuctionDobj> applList = new ArrayList<>();
                    applList.add(dobj);
                    returnList.put(dobj.getRegn_no(), applList);
                } else {
                    returnList.get(dobj.getRegn_no()).add(dobj);
                }

            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in fetching details from the database.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return returnList;
    }

    public void processFancyNumberListPendingForInward(Set<String> regnList, String state_cd, int off_cd) throws VahanException {
        TransactionManager tmgr = null;

        try {
            tmgr = new TransactionManager("fancyNumberAutoScheduledJob");
            Iterator itr = regnList.iterator();
            while (itr.hasNext()) {

                String regn_no = itr.next().toString();

                String appl_no = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());

                NextStageResponse seatDetails = null;

                /// do while have to use here because some time common portal web service is 
                /// returing negative application no 
                do {
                    seatDetails = InitialStageWS.findInitialSeat(appl_no, TableConstants.VM_TRANSACTION_MAST_CHOICE_NO, state_cd, off_cd);
                } while (seatDetails.getAppl_no().startsWith("-"));

                String ins_va_status = "INSERT INTO " + TableList.VA_STATUS + " (appl_no, appl_no_map, pur_cd, flow_slno, file_movement_slno, action_cd, seat_cd, cntr_id, status, office_remark, public_remark, emp_cd, op_dt, state_cd, off_cd, rto_code, file_movement_type)    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = tmgr.prepareStatement(ins_va_status);
                ps.setString(1, appl_no); // actual application no 
                ps.setInt(2, Integer.parseInt(seatDetails.getAppl_no())); // application _ map no
                ps.setInt(3, seatDetails.getPur_cd());
                ps.setInt(4, seatDetails.getFlow_slno());
                ps.setInt(5, 1); // default file movement no.
                ps.setInt(6, seatDetails.getAction_cd());
                ps.setString(7, ""); // blank seat code because web service is not givin data
                ps.setString(8, seatDetails.getCntr_id());
                ps.setString(9, "N"); // default Status
                ps.setString(10, ""); // default blank office remark.
                ps.setString(11, ""); // default blank public remark.
                ps.setLong(12, seatDetails.getEmp_cd());

                ps.setTimestamp(13, ServerUtil.getSystemDateInPostgres());
                ps.setString(14, seatDetails.getState_cd());
                ps.setInt(15, seatDetails.getOff_cd());
                ps.setString(16, seatDetails.getRto_code());
                ps.setString(17, "F"); // default file movement type.
                ps.executeUpdate();

                String ins_va_details = "INSERT INTO va_details( appl_no, pur_cd, appl_dt, regn_no,user_id ,user_type,  state_cd,    off_cd)  VALUES (?, ?, ?, ?, ?,?,?,?)";
                PreparedStatement ps_va_details = tmgr.prepareStatement(ins_va_details);
                ps_va_details.setString(1, appl_no); // actual application no 
                ps_va_details.setInt(2, seatDetails.getPur_cd());
                ps_va_details.setTimestamp(3, ServerUtil.getSystemDateInPostgres());
                ps_va_details.setString(4, regn_no);
                ps_va_details.setString(5, "");// blank User id
                ps_va_details.setString(6, ""); // blank user type.
                ps_va_details.setString(7, seatDetails.getState_cd());
                ps_va_details.setInt(8, seatDetails.getOff_cd());
                ps_va_details.executeUpdate();

                String upd_va_fancy_register = "UPDATE " + TableList.VA_FANCY_REGISTER + " SET STATUS='I' where regn_no=? and rto_cd=? ";
                PreparedStatement ps_va_fancy_register = tmgr.prepareStatement(upd_va_fancy_register);
                ps_va_fancy_register.setString(1, regn_no);
                ps_va_fancy_register.setInt(2, off_cd);
                ps_va_fancy_register.executeUpdate();

            }

            tmgr.commit();

        } catch (SQLException sql) {
            throw new VahanException("Problem in saving data");
        } catch (AxisFault ex) {
            throw new VahanException("Problem in application inward.");
        } catch (RemoteException ex) {
            throw new VahanException("Problem in application inward.");
        } catch (XPathExpressionException ex) {
            throw new VahanException("Problem in application inward.");
        } finally {
            try {
                tmgr.release();
            } catch (Exception ex) {
                throw new VahanException("Problem in saving data");
            }

        }
    }
}
