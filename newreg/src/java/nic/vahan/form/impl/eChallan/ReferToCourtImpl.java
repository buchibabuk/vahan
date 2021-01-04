package nic.vahan.form.impl.eChallan;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.eChallan.CourtCasesDobj;
import nic.vahan.form.dobj.eChallan.InformationViolationReportDobj;
import nic.vahan.form.dobj.eChallan.RefertoCourtDobj;
import nic.vahan.form.impl.Util;
import static nic.vahan.form.impl.eChallan.DisposalOfChallanImpl.getTimeStamp;
import org.apache.log4j.Logger;

public class ReferToCourtImpl {

    private static Logger LOGGER = Logger.getLogger(ReferToCourtImpl.class);
    boolean flag = false;

    public List<RefertoCourtDobj> getPendingCaseDetails() throws VahanException, Exception {
        TransactionManager tmgr = null;
        PreparedStatement pstmtPendingCase = null;
        RefertoCourtDobj dobjrefCourt = null;
        List<RefertoCourtDobj> dorefCourtList = new ArrayList<RefertoCourtDobj>();
        try {
            String sqlPendigCases = "select\n"
                    + " * from  " + TableList.VT_CHALLAN + "  \n"
                    + "inner join   " + TableList.VT_CHALLAN_OWNER + " on vt_challan.appl_no=vt_challan_owner.appl_no \n"
                    + " where vt_challan.state_cd=? and vt_challan.off_cd=? and vt_challan.appl_no not in( select vt_challan_refer_to_court.appl_no from " + TableList.VT_CHALLAN_REFER_TO_COURT + " )order by vt_challan.chal_date  ";
            tmgr = new TransactionManager("getPendingCaseDetails");
            pstmtPendingCase = tmgr.prepareStatement(sqlPendigCases);
            pstmtPendingCase.setString(1, Util.getUserStateCode());
            pstmtPendingCase.setInt(2, Util.getSelectedSeat().getOff_cd());

            ResultSet rs = pstmtPendingCase.executeQuery();

            while (rs.next()) {
                dobjrefCourt = new RefertoCourtDobj(rs.getString("appl_no"), rs.getString("chal_no"), rs.getString("regn_no"), rs.getDate("chal_date"), rs.getString("owner_name"));
                dorefCourtList.add(dobjrefCourt);
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return dorefCourtList;
    }

    public List getCourtMaster() throws Exception {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        List courtMaster = new ArrayList();
        SelectItem item = new SelectItem("-1", "Select Court");
        courtMaster.add(item);
        try {
            tmgr = new TransactionManager("getCourtMaster");
            String sqlOfficer = "select court_cd,court_name from  " + TableList.VM_COURT + " order by court_cd";
            ps = tmgr.prepareStatement(sqlOfficer);
            RowSet resulSet = tmgr.fetchDetachedRowSet();
            while (resulSet.next()) {
                item = new SelectItem(resulSet.getString("court_cd"), resulSet.getString("court_name"));
                courtMaster.add(item);

            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }
        return courtMaster;
    }

    public boolean saveChallanData(List selectedPendingCases, RefertoCourtDobj refertocourtDobj) throws SQLException, VahanException, Exception {
        TransactionManager tmgr = null;
        PreparedStatement pstmtCHALREFTO = null;
        try {
            tmgr = new TransactionManager("SaveChallanDAO : saveChallanData");

            String RefertoCourtData = "INSERT INTO  " + TableList.VT_CHALLAN_REFER_TO_COURT + " "
                    + "(appl_no, court_cd, hearing_date, op_dt, state_cd, off_cd)"
                    + "VALUES (?, ?, ?, ?, ?, ?)";

            Iterator itr = selectedPendingCases.iterator();

            while (itr.hasNext()) {
                refertocourtDobj = new RefertoCourtDobj();
                refertocourtDobj = (RefertoCourtDobj) itr.next();

                pstmtCHALREFTO = tmgr.prepareStatement(RefertoCourtData);

                pstmtCHALREFTO.setString(1, refertocourtDobj.getApplicationno());

                pstmtCHALREFTO.setInt(2, Integer.parseInt(refertocourtDobj.getCourtNAme()));

                pstmtCHALREFTO.setTimestamp(3, getTimeStamp(refertocourtDobj.getHearingDate().toString()));
                pstmtCHALREFTO.setTimestamp(4, getTimeStamp(new Date() + "00:00:00"));
                pstmtCHALREFTO.setString(5, Util.getUserStateCode());
                pstmtCHALREFTO.setInt(6, Util.getSelectedSeat().getOff_cd());

                pstmtCHALREFTO.execute();
            }

            tmgr.commit();
            flag = true;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {

                tmgr.release();
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return flag;

    }

    public List<CourtCasesDobj> getAllreferToCourtCases() {
        List listOfCourtCases = new ArrayList();
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        CourtCasesDobj dobj = null;
        String sql = "";
        sql = "SELECT refercourt.appl_no, refercourt.court_cd, refercourt.hearing_date,ownerr.owner_name,"
                + "  court.court_name,ownerr.regn_no\n"
                + "  FROM echallan.vt_challan_refer_to_court refercourt\n"
                + "  left Outer Join " + TableList.VT_CHALLAN_OWNER + " ownerr on ownerr.appl_no= refercourt.appl_no\n"
                + "  left Outer Join " + TableList.VM_COURT + " court on court.court_cd= refercourt.court_cd\n"
                + "  where court_rcpt_no is null and court_paid_amount is null and remarks is null and refercourt.state_cd=? and refercourt.off_cd=?";
        try {
            tmgr = new TransactionManager("all cases of coutcases");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, Util.getUserStateCode());
            pstmt.setInt(2, Util.getSelectedSeat().getOff_cd());

            RowSet rowSet = tmgr.fetchDetachedRowSet();
            while (rowSet.next()) {
                dobj = new CourtCasesDobj(rowSet.getString("appl_no"), rowSet.getInt("court_cd"), rowSet.getDate("hearing_date"), rowSet.getString("owner_name"), rowSet.getString("court_name"), rowSet.getString("regn_no"));
                listOfCourtCases.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return listOfCourtCases;
    }

    public InformationViolationReportDobj getCourtDetailsForIr(CourtCasesDobj dobj) throws Exception {
        InformationViolationReportDobj dobjIr = null;
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        String sql = "";
        RowSet rs = null;
        sql = "select \n"
                + " vtchal.appl_no as vt_appl_no, vtchal.chal_no,  vtchal.chal_date, vtchal.chal_time,"
                + " vtchal.chal_place, vtchal.is_doc_impound, vtchal.is_vch_impdound, vtchal.chal_officer,tmUser.user_name, vtchal.nc_c_ref_no, \n"
                + " refToCourt.appl_no as court_appl_no, \n"
                + " owner.regn_no, owner.chasi_no, owner.vh_class, owner.vch_off_cd, owner.vch_state_cd,owner.owner_name,  owner.color, \n"
                + " vhClass.descr as vehicle_class_descr \n"
                + " FROM echallan.vt_challan vtchal \n"
                + " left Outer join  echallan.vt_challan_refer_to_court refToCourt on vtchal.appl_no=refToCourt.appl_no \n"
                + " left Outer join  echallan.vt_challan_owner owner on vtchal.appl_no=owner.appl_no \n"
                + " left Outer join  echallan.vt_vch_offences vchOffence on vtchal.appl_no=vchOffence.appl_no \n"
                + " left Outer join  echallan.vt_witness_details witness on vtchal.appl_no=witness.appl_no \n"
                + " left Outer join  vahan4.vm_vh_class vhClass on owner.vh_class=vhClass.vh_class \n"
                + " left Outer join  vahan4.tm_user_info tmUser on vtchal.chal_officer :: numeric=tmUser.user_cd \n"
                + " where vtchal.appl_no=? and vtchal.state_cd=? and vtchal.off_cd=?";

        try {
            tmgr = new TransactionManager("getCourtDetailsForIr");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, dobj.getAppl_no());
            pstmt.setString(2, Util.getUserStateCode());
            pstmt.setInt(3, Util.getSelectedSeat().getOff_cd());

            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobjIr = new InformationViolationReportDobj();
                dobjIr.setApplicationno(rs.getString("vt_appl_no"));
                dobjIr.setChallanDate(rs.getDate("chal_date"));
                dobjIr.setVehicleNo(rs.getString("regn_no"));
                dobjIr.setChallanNo(rs.getString("chal_no"));
                dobjIr.setOwnerName(rs.getString("owner_name"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dobjIr;
    }
}
