/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.db.DocumentType;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.State;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.NocDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.ToDobj;
import nic.vahan.form.dobj.smartcard.SmartCardDobj;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertForQRDetails;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import static nic.vahan.server.ServerUtil.verifyForSmartCard;
import org.apache.log4j.Logger;

public class NocImpl implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(NocImpl.class);

    public NocImpl() {
    }

//Pawan
/*    Modifying appl_no to regn_no beacuse appl_no is different for Issue of NOC and
     *    Cancellation of NOC.So, using regn_no for showing details of Issue of NOC.
     */
    public NocDobj set_NOC_appl_db_to_dobj(String applNo, String regnNo) {

        PreparedStatement ps = null;
        RowSet rs = null;
        String sql = null;
        TransactionManager tmgr = null;
        NocDobj noc_dobj = null;
        boolean vtNOC = false;

        if (applNo != null) {
            applNo = applNo.toUpperCase();
        }

        try {
            tmgr = new TransactionManager("NOC_Impl");
            if (applNo != null) {
                sql = "SELECT appl_no, regn_no, state_to,"
                        + "  off_to, ncrb_ref, dispatch_no,reason_cd,new_owner, noc_dt"
                        + "  FROM " + TableList.VA_NOC + " where appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, applNo);
            } else {
                sql = "SELECT state_cd, off_cd, appl_no, regn_no, state_to, off_to, rto_to, "
                        + "       ncrb_ref, dispatch_no, noc_no, noc_dt,reason_cd,new_owner "
                        + "  FROM " + TableList.VT_NOC + " where regn_no=? order by noc_dt desc limit 1";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regnNo);
                vtNOC = true;
            }
            rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                noc_dobj = new NocDobj();

                noc_dobj.setAppl_no(rs.getString("appl_no"));
                noc_dobj.setRegn_no(rs.getString("regn_no"));
                noc_dobj.setState_to(rs.getString("state_to"));
                noc_dobj.setOff_to(rs.getInt("off_to"));
                noc_dobj.setNcrb_ref(rs.getString("ncrb_ref"));
                noc_dobj.setDispatch_no(rs.getString("dispatch_no"));
                noc_dobj.setPur_cd_to(rs.getInt("reason_cd"));
                noc_dobj.setNew_own_name(rs.getString("new_owner"));

                if (Util.getUserStateCode().equals("RJ")) {
                    noc_dobj.setNoc_dt(rs.getTimestamp("noc_dt"));
                }
                if (vtNOC) {
                    noc_dobj.setState_cd(rs.getString("state_cd"));
                    noc_dobj.setOff_cd(rs.getInt("off_cd"));
                    noc_dobj.setNoc_no(rs.getString("noc_no"));
                    noc_dobj.setNoc_dt(rs.getTimestamp("noc_dt"));
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
        return noc_dobj;
    }

    public NocDobj set_NOC_cancel_appl_db_to_dobjForCancel(String regn_no) {

        PreparedStatement ps = null;
        RowSet rs = null;
        String sql = null;
        TransactionManager tmgr = null;
        NocDobj noc_dobj = null;
        boolean vtNOC = false;

        if (regn_no != null) {
            regn_no = regn_no.toUpperCase();
        }

        try {
            tmgr = new TransactionManager("NOC_Impl");
            ps = tmgr.prepareStatement("Select regn_no from " + TableList.VA_NOC + " where regn_no=?");
            ps.setString(1, regn_no);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                sql = "SELECT appl_no, regn_no, state_to,"
                        + "  off_to, ncrb_ref, dispatch_no, reason_cd, new_owner"
                        + "  FROM " + TableList.VA_NOC + " where regn_no=?";
            } else {
                sql = "SELECT state_cd, off_cd, appl_no, regn_no, state_to, off_to, rto_to, "
                        + "       ncrb_ref, dispatch_no, noc_no, noc_dt, reason_cd, new_owner "
                        + "  FROM " + TableList.VT_NOC + " where regn_no=? and state_cd = ? and off_cd = ? order by noc_dt desc limit 1";
                vtNOC = true;
            }
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());

            rs = null;
            rs = tmgr.fetchDetachedRowSet();

            //rs.beforeFirst();
            if (rs.next()) // found
            {
                noc_dobj = new NocDobj();

                noc_dobj.setAppl_no(rs.getString("appl_no"));
                noc_dobj.setRegn_no(rs.getString("regn_no"));
                noc_dobj.setState_to(rs.getString("state_to"));
                noc_dobj.setOff_to(rs.getInt("off_to"));
                noc_dobj.setNcrb_ref(rs.getString("ncrb_ref"));
                noc_dobj.setDispatch_no(rs.getString("dispatch_no"));
                noc_dobj.setPur_cd_to(rs.getInt("reason_cd"));
                noc_dobj.setNew_own_name(rs.getString("new_owner"));

                if (vtNOC) {
                    noc_dobj.setState_cd(rs.getString("state_cd"));
                    noc_dobj.setOff_cd(rs.getInt("off_cd"));
                    noc_dobj.setNoc_no(rs.getString("noc_no"));
                    noc_dobj.setNoc_dt(rs.getTimestamp("noc_dt"));
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
        return noc_dobj;
    }

    public boolean isNOCInwardwithHPC(String appl_no, String regn_no, NocDobj noc_dobj, String stateCd, int offCd) throws VahanException {
        PreparedStatement ps = null;
        RowSet rs = null;
        String sql = null;
        TransactionManager tmgr = null;
        boolean NOCInwardwithHPC = false;

        try {
            tmgr = new TransactionManager("isNOCInwardwithHPC");
            sql = "Select pur_cd from " + TableList.VA_DETAILS + " where appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                if (rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_HPC) {
                    ps = tmgr.prepareStatement("select (b.fncr_name || ', ' || b.fncr_add1 || ', ' || b.fncr_add2 || ', ' || b.fncr_add3 || ', ' || chr(10) ||  b.fncr_district_name || ', ' || b.fncr_state || ', ' || b.fncr_state_name || ', ' || b.fncr_pincode) as fncr_full_add from " + TableList.VV_HYPTH + " b where b.REGN_no=? and b.state_cd = ? and b.off_cd = ? ");
                    ps.setString(1, regn_no);
                    ps.setString(2, stateCd);
                    ps.setInt(3, offCd);
                    rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        NOCInwardwithHPC = true;
                        noc_dobj.setHptDetails(rs.getString("fncr_full_add"));
                    }
                    break;
                }
            }
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            throw new VahanException("Something went wrong when getting details of HPC with NOC, Please contact to the System Administrator.");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong when getting details of HPC with NOC, Please contact to the System Administrator.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return NOCInwardwithHPC;
    }
//Pawan

    public NocDobj set_NOC_cancel_appl_db_to_dobj(String applNo, String regn_no) throws VahanException {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        NocDobj noc_dobj = null;

        try {
            tmgr = new TransactionManager("NOC_Impl");
            ps = tmgr.prepareStatement("SELECT appl_no, regn_no, state_to, off_to, ncrb_ref, dispatch_no, \n"
                    + "       noc_no, noc_dt,file_ref_no, approved_by, reason, reason_cd, new_owner \n"
                    + "  FROM " + TableList.VA_NOC_CANCEL + " WHERE appl_no=?");
            ps.setString(1, applNo);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                noc_dobj = new NocDobj();
                noc_dobj.setAppl_no(rs.getString("appl_no"));
                noc_dobj.setRegn_no(rs.getString("regn_no"));
                noc_dobj.setState_to(rs.getString("state_to"));
                noc_dobj.setOff_to(rs.getInt("off_to"));
                noc_dobj.setNcrb_ref(rs.getString("ncrb_ref"));
                noc_dobj.setNoc_dt(rs.getDate("noc_dt")); // for current date
                noc_dobj.setDispatch_no(rs.getString("dispatch_no"));
                noc_dobj.setNoc_no(rs.getString("noc_no"));
                noc_dobj.setFile_ref_no(rs.getString("file_ref_no"));
                noc_dobj.setApp_by(rs.getString("approved_by"));
                noc_dobj.setReason(rs.getString("reason"));
                noc_dobj.setPur_cd_to(rs.getInt("reason_cd"));
                noc_dobj.setNew_own_name(rs.getString("new_owner"));

            } else {
                noc_dobj = set_NOC_cancel_appl_db_to_dobjForCancel(regn_no);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in No Objection Certificate Application");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return noc_dobj;
    }
//Ends

    public void update_NOC_Status(NocDobj noc_dobj, NocDobj noc_dobj_prv, Status_dobj status_dobj, String changedData, String selectedFancyRetnetion, Owner_dobj ownerDobj, String stateCd, int offCd, String empCd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        String newRegnNo = "";
        String status = null;
        try {

            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            Date applDate = format.parse(status_dobj.getAppl_dt());

            tmgr = new TransactionManager("update_NOC_Status");

            VehicleParameters vehicleParametersForNoc = new VehicleParameters();
            vehicleParametersForNoc = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj);
            vehicleParametersForNoc.setSTATE_CD(noc_dobj.getState_to());
            if (selectedFancyRetnetion != null && selectedFancyRetnetion.equalsIgnoreCase("YES")) {
                vehicleParametersForNoc.setNOC_RETENTION(1);
                status_dobj.setVehicleParameters(vehicleParametersForNoc);
            } else if (selectedFancyRetnetion != null && selectedFancyRetnetion.equalsIgnoreCase("NO")) {
                vehicleParametersForNoc.setNOC_RETENTION(0);
                status_dobj.setVehicleParameters(vehicleParametersForNoc);
            } else if (selectedFancyRetnetion == null) {
                vehicleParametersForNoc.setNOC_RETENTION(0);
                status_dobj.setVehicleParameters(vehicleParametersForNoc);
            }

            //=================WEB SERVICES FOR NEXTSTAGE START=========
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            //=================WEB SERVICES FOR NEXTSTAGE END===========

            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_ENTRY
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_VERIFICATION
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_APPROVAL) {

                if ((changedData != null && !changedData.equals("")) || noc_dobj_prv == null) {
                    if (noc_dobj.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_NOC_CANCEL) {
                        insertUpdateCancelNOC(tmgr, noc_dobj.getAppl_no(), noc_dobj.getRegn_no(), noc_dobj);
                    } else {
                        insertUpdateNOC(tmgr, noc_dobj.getAppl_no(), noc_dobj.getRegn_no(), noc_dobj);
                        if (selectedFancyRetnetion != null && selectedFancyRetnetion.equalsIgnoreCase("YES")) {
                            ToImpl toImpl = new ToImpl();
                            ToDobj to_dobj = new ToDobj();
                            to_dobj.setAppl_no(noc_dobj.getAppl_no());
                            to_dobj.setRegn_no(noc_dobj.getRegn_no());
                            to_dobj.setReason("NOC ISSUED");
                            toImpl.insertIntoVaSurrenderRetention(tmgr, to_dobj, applDate);
                        }
                    }
                }
            }
            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_APPROVAL
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                //Added by Pawan
                //For NOC Cancellation
                if (noc_dobj.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_NOC_CANCEL) {

                    sql = "insert into " + TableList.VT_NOC_CANCEL + "("
                            + " appl_no, state_cd, off_cd, noc_appl_no, regn_no, state_to, off_to, \n"
                            + " rto_to, ncrb_ref, dispatch_no, noc_no, noc_dt, file_ref_no, approved_by, \n"
                            + " reason, cancel_dt,reason_cd,new_owner)"
                            + " SELECT ?, state_cd, off_cd, appl_no, regn_no, state_to, off_to, rto_to,"
                            + " ncrb_ref, dispatch_no,"
                            + " noc_no, noc_dt, file_ref_no, approved_by, reason, current_timestamp as cancel_dt,reason_cd,new_owner"
                            + "  FROM " + TableList.VA_NOC_CANCEL + " where appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, noc_dobj.getAppl_no());
                    ps.setString(2, noc_dobj.getAppl_no());
                    ps.executeUpdate();

                    sql = "select * from " + TableList.VT_RC_SURRENDER + " WHERE regn_no=? and state_cd = ? and off_cd = ? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, noc_dobj.getRegn_no());
                    ps.setString(2, stateCd);
                    ps.setInt(3, offCd);
                    RowSet rs = tmgr.fetchDetachedRowSet_No_release();

                    if (rs.next()) {
                        status = "S";
                    }
                    ApplicationInwardImpl inwardImpl = new ApplicationInwardImpl();
                    Status_dobj applInwardOthOffDobj = inwardImpl.getApplInwardOthOffDobj(noc_dobj.getAppl_no());
                    sql = "UPDATE " + TableList.VT_OWNER + " SET status=?, op_dt=? WHERE regn_no=? and state_cd = ? and off_cd = ? ";
                    ps = tmgr.prepareStatement(sql);
                    if (status != null) {
                        ps.setString(1, status);
                    } else {
                        ps.setString(1, "A");
                    }
                    ps.setDate(2, new java.sql.Date(new java.util.Date().getTime()));
                    ps.setString(3, noc_dobj.getRegn_no());
                    ps.setString(4, stateCd);
                    if (applInwardOthOffDobj != null) {
                        ps.setInt(5, applInwardOthOffDobj.getOff_cd());
                    } else {
                        ps.setInt(5, offCd);
                    }
                    ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                    sql = "INSERT INTO " + TableList.VHA_NOC_CANCEL + "( moved_on, moved_by,state_cd,off_cd, appl_no, regn_no, state_to, off_to,rto_to, ncrb_ref, dispatch_no,noc_no,noc_dt, file_ref_no, approved_by,"
                            + " reason,op_dt,reason_cd,new_owner)"
                            + "SELECT current_timestamp + interval '1 second' as moved_on, ? as moved_by,state_cd,off_cd, appl_no, regn_no, state_to, off_to, rto_to, ncrb_ref, dispatch_no,noc_no,noc_dt, file_ref_no, approved_by, reason ,"
                            + "op_dt,reason_cd,new_owner  FROM " + TableList.VA_NOC_CANCEL + " WHERE appl_no=? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, empCd);
                    ps.setString(2, noc_dobj.getAppl_no());
                    ps.executeUpdate();

                    sql = "DELETE FROM " + TableList.VA_NOC_CANCEL + " where appl_no = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, noc_dobj.getAppl_no());
                    ps.executeUpdate();

                    sql = "INSERT INTO " + TableList.VH_NOC + "( \n"
                            + "           moved_on, moved_by, state_cd, off_cd, appl_no, regn_no, state_to, off_to, rto_to, \n"
                            + "            ncrb_ref, dispatch_no, noc_no, noc_dt,reason_cd,new_owner,op_dt ) \n"
                            + "   SELECT current_timestamp,?,state_cd, off_cd, appl_no, regn_no, state_to, off_to, rto_to, \n"
                            + "       ncrb_ref, dispatch_no, noc_no, noc_dt ,reason_cd,new_owner,op_dt \n"
                            + "  FROM " + TableList.VT_NOC + " where regn_no=? and state_cd = ? and off_cd = ? and noc_dt = (Select noc_dt from " + TableList.VT_NOC + " where regn_no=? and state_cd = ? and off_cd = ? order by noc_dt desc limit 1)";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, empCd);
                    ps.setString(2, noc_dobj.getRegn_no());
                    ps.setString(3, stateCd);
                    ps.setInt(4, offCd);
                    ps.setString(5, noc_dobj.getRegn_no());
                    ps.setString(6, stateCd);
                    ps.setInt(7, offCd);
                    ps.executeUpdate();

                    sql = "Delete from " + TableList.VT_NOC + " where regn_no=? and state_cd = ? and off_cd = ? and noc_dt = (Select noc_dt from " + TableList.VT_NOC + " where regn_no=? and state_cd = ? and off_cd = ? order by noc_dt desc limit 1)";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, noc_dobj.getRegn_no());
                    ps.setString(2, stateCd);
                    ps.setInt(3, offCd);
                    ps.setString(4, noc_dobj.getRegn_no());
                    ps.setString(5, stateCd);
                    ps.setInt(6, offCd);
                    ps.executeUpdate();

                    //for handling tax defaulter
                    Map<Integer, String> taxPurCdDesc = TaxServer_Impl.getTaxPurCodeDescr(ownerDobj);
                    if (taxPurCdDesc != null && !taxPurCdDesc.isEmpty()) {
                        for (Map.Entry<Integer, String> entry : taxPurCdDesc.entrySet()) {
                            int purCd = entry.getKey();
                            Date taxDueFrom = TaxServer_Impl.getTaxDueFromDate(ownerDobj, purCd);
                            if (taxDueFrom != null) {
                                taxDueFrom = ServerUtil.dateRange(taxDueFrom, 0, 0, -1);
                                TaxServer_Impl.insertUpdateTaxDefaulter(noc_dobj.getRegn_no(), taxDueFrom, purCd, tmgr);
                            }
                        }
                    }

                    //for updating the status of application when it is approved start
                    status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                    ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                    insertForQRDetails(noc_dobj.getAppl_no(), noc_dobj.getRegn_no(), null, null, false, DocumentType.NOC_CANCEL_QR, stateCd, offCd, tmgr);
                    //for updating the status of application when it is approved end

                } else {
                    //For Issue of NOC
                    //Approve Insurance
                    InsImpl.approvalInsurance(tmgr, ownerDobj.getRegn_no(), stateCd, offCd, empCd);
                    //For Checking if Other Application is Pending for Approval Befor NOC Approval
                    List<Status_dobj> statusList = ServerUtil.applicationStatus(ownerDobj.getRegn_no(), noc_dobj.getAppl_no(), stateCd);
                    if (statusList != null && !statusList.isEmpty() && statusList.size() > 1) {
                        for (int i = 0; i < statusList.size(); i++) {
                            if (statusList.get(i).getPur_cd() != TableConstants.VM_TRANSACTION_MAST_NOC
                                    && statusList.get(i).getPur_cd() != TableConstants.VM_DUPLICATE_TO_TAX_CARD && !"KL".contains(Util.getUserStateCode()) && statusList.get(i).getPur_cd() != TableConstants.VM_TRANSACTION_MAST_TO) {
                                throw new VahanException("Other Transaction is Pending for this Application No, First Approve them Before Approving NOC.");
                            }
                        }
                    }
                    boolean isSmartCard = verifyForSmartCard(stateCd, offCd, tmgr);
                    String appl_no = null;
                    if (isSmartCard) {
                        //Check for smart Card
                        SmartCardDobj smartCardDobj = ServerUtil.getSmartCardDetailsFromRcBeToBo(ownerDobj.getRegn_no());
                        if (smartCardDobj != null) {
                            throw new VahanException("Can't Approve Application due to Smart Card is Pending for Activation for Registration No " + ownerDobj.getRegn_no() + " against Application No " + smartCardDobj.getAppl_no());
                        }
                    } else {
                        //Check for RC Print
                        PrintDocImpl docImpl = new PrintDocImpl();
                        appl_no = docImpl.getApplNoFromVaRcPrint(ownerDobj.getRegn_no());
                        if (appl_no != null) {
                            throw new VahanException("Can't Approve Application due to RC is Pending for Print for Registration No " + ownerDobj.getRegn_no() + " against Application No " + appl_no);
                        }
                    }

                    String noc_no;
                    if (Util.getUserStateCode().equals("GJ") && noc_dobj.getState_cd().equals(noc_dobj.getState_to())) {
                        noc_no = ServerUtil.getUniquePermitNo(tmgr, noc_dobj.getState_cd(), noc_dobj.getOff_cd(), 0, 0, "R");
                    } else {
                        noc_no = ServerUtil.getUniquePermitNo(tmgr, noc_dobj.getState_cd(), noc_dobj.getOff_cd(), 0, 0, "N");
                    }

                    if (!CommonUtils.isNullOrBlank(noc_no)) {

                        if (Util.getUserStateCode().equals("RJ")) {
                            sql = "insert into " + TableList.VT_NOC
                                    + " SELECT  state_cd,off_cd,appl_no, regn_no, state_to, off_to, rto_to, ncrb_ref, dispatch_no,"
                                    + "? as noc_no, noc_dt,reason_cd,new_owner,current_timestamp as op_dt FROM " + TableList.VA_NOC + ""
                                    + " where appl_no=?";
                        } else {
                            sql = "insert into " + TableList.VT_NOC
                                    + " SELECT  state_cd,off_cd,appl_no, regn_no, state_to, off_to, rto_to, ncrb_ref, dispatch_no,"
                                    + "? as noc_no,current_timestamp as noc_dt,reason_cd,new_owner,current_timestamp as op_dt FROM " + TableList.VA_NOC + ""
                                    + " where appl_no=?";
                        }
                        ps = tmgr.prepareStatement(sql);
//                        ps.setString(1, noc_dobj.getState_cd());
//                        ps.setInt(2, noc_dobj.getOff_cd());
                        ps.setString(1, noc_no);
                        ps.setString(2, noc_dobj.getAppl_no());
                        ps.executeUpdate();

                        //Update record from vt_owner
                        sql = "UPDATE " + TableList.VT_OWNER + " SET status=?, op_dt = ? WHERE regn_no=? and state_cd = ? and off_cd = ?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, "N");
                        ps.setDate(2, new java.sql.Date(new java.util.Date().getTime()));
                        ps.setString(3, ownerDobj.getRegn_no());
                        ps.setString(4, stateCd);
                        if (Util.getUserStateCode().equalsIgnoreCase("KL") && Util.getUserStateCode().equalsIgnoreCase(ownerDobj.getState_cd())
                                && Util.getUserSeatOffCode() != ownerDobj.getOff_cd()) {
                            ps.setInt(5, ownerDobj.getOff_cd());
                        } else {
                            ps.setInt(5, offCd);
                        }
                        ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                        sql = "INSERT INTO " + TableList.VHA_NOC + "( moved_on, moved_by,state_cd,off_cd, appl_no, regn_no, state_to, off_to,ncrb_ref, dispatch_no, op_dt,reason_cd,new_owner, noc_dt) "
                                + " SELECT current_timestamp + interval '1 second' as moved_on,? as moved_by,state_cd,off_cd, appl_no, regn_no, state_to, off_to, ncrb_ref, dispatch_no, op_dt,reason_cd,new_owner, noc_dt FROM " + TableList.VA_NOC + " WHERE appl_no=?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, empCd);
                        ps.setString(2, noc_dobj.getAppl_no());
                        ps.executeUpdate();

                        sql = "DELETE FROM " + TableList.VA_NOC + " where appl_no = ?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, noc_dobj.getAppl_no());
                        ps.executeUpdate();

                        if (selectedFancyRetnetion != null && (selectedFancyRetnetion.equalsIgnoreCase("YES") || selectedFancyRetnetion.equalsIgnoreCase("NO"))) {
                            ToImpl toImpl = new ToImpl();
                            toImpl.insertIntoVhaSurrenderRetention(tmgr, status_dobj.getAppl_no());
                            if (selectedFancyRetnetion.equalsIgnoreCase("YES")) {
                                newRegnNo = toImpl.insertIntoVtSurrenderRetention(tmgr, ownerDobj, "NOC ISSUED", status_dobj.getAppl_no(), applDate, TableConstants.VM_TRANSACTION_MAST_NOC, false);
                                ServerUtil.deleteFromTable(tmgr, null, noc_dobj.getAppl_no(), TableList.VA_SURRENDER_RETENTION);
                            }
                        }
                        //for updating the status of application when it is approved start
                        status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                        ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                        insertForQRDetails(noc_dobj.getAppl_no(), noc_dobj.getRegn_no(), null, null, false, DocumentType.NOC_QR, stateCd, offCd, tmgr);
                        //for updating the status of application when it is approved end

                        //for inserting data for rc or smartcard if noc with retention performed
                        if (selectedFancyRetnetion != null && selectedFancyRetnetion.equalsIgnoreCase("YES")) {
                            ServerUtil.VerifyInsertSmartCardPrintDetail(noc_dobj.getAppl_no(), newRegnNo,
                                    stateCd, offCd, status_dobj.getPur_cd(), tmgr);
                        }
                    } else {
                        throw new VahanException("There is no Noc Number Entry in Master Table.");
                    }
                }
                if (noc_dobj.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_NOC_CANCEL && noc_dobj.getState_cd().equalsIgnoreCase("OR")) {
                    //SmartCard Or Print
                    ServerUtil.VerifyInsertSmartCardPrintDetail(noc_dobj.getAppl_no(), noc_dobj.getRegn_no(),
                            stateCd, offCd, status_dobj.getPur_cd(), tmgr);
                }
            }

            insertIntoVhaChangedData(tmgr, noc_dobj.getAppl_no(), changedData); //for saving the data into table those are changed by the user

            ServerUtil.fileFlow(tmgr, status_dobj); // for updateing va_status and vha status for new role,seat for new emp

            tmgr.commit(); //Commiting data here....
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in NOC update, please try after sometime or contact Administrator");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }

    }

    public static void insertIntoNOCHistory(TransactionManager tmgr, String appl_no) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "INSERT INTO " + TableList.VHA_NOC + "(moved_on, moved_by,state_cd,off_cd, appl_no, regn_no, state_to, off_to,ncrb_ref, dispatch_no, op_dt,reason_cd,new_owner, noc_dt) \n"
                    + "SELECT  current_timestamp as moved_on,? as moved_by,state_cd,off_cd, appl_no, regn_no, state_to, off_to, ncrb_ref, dispatch_no, op_dt,reason_cd,new_owner, noc_dt FROM " + TableList.VA_NOC + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, appl_no);

            ps.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        }
    } // end of insertIntoNOCHistory

    public static void updateNOC(TransactionManager tmgr, NocDobj noc_dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            if (Util.getUserStateCode().equals("RJ")) {
                sql = " update " + TableList.VA_NOC + " set  "
                        + " state_to=?,"
                        + " off_to=?,"
                        + " ncrb_ref=?,"
                        + " dispatch_no=?,"
                        + " op_dt = current_timestamp,"
                        + " reason_cd=?,new_owner=?, noc_dt =? "
                        + " where appl_no=?";
            } else {
                sql = " update " + TableList.VA_NOC + " set  "
                        + " state_to=?,"
                        + " off_to=?,"
                        + " ncrb_ref=?,"
                        + " dispatch_no=?,"
                        + " op_dt = current_timestamp,"
                        + " reason_cd=?,new_owner=? "
                        + " where appl_no=?";
            }
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, noc_dobj.getState_to());
            ps.setInt(2, noc_dobj.getOff_to());
            ps.setString(3, noc_dobj.getNcrb_ref());
            ps.setString(4, noc_dobj.getDispatch_no());
            ps.setInt(5, noc_dobj.getPur_cd_to());
            ps.setString(6, noc_dobj.getNew_own_name());
            if (Util.getUserStateCode().equals("RJ")) {
                ps.setDate(7, new java.sql.Date(noc_dobj.getNoc_dt().getTime()));
                ps.setString(8, noc_dobj.getAppl_no());
            } else {
                ps.setString(7, noc_dobj.getAppl_no());
            }
            ps.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        }
    } // end of updateNOC

    public void updateVtNOC(TransactionManager tmgr, NocDobj noc_dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = " update " + TableList.VT_NOC + " set  "
                    + " state_to=?,"
                    + " off_to=?,"
                    + " ncrb_ref=?,"
                    + " dispatch_no=?"
                    + " where appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, noc_dobj.getState_to());
            ps.setInt(2, noc_dobj.getOff_to());
            ps.setString(3, noc_dobj.getNcrb_ref());
            ps.setString(4, noc_dobj.getDispatch_no());
            ps.setString(5, noc_dobj.getAppl_no());
            ps.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong During Updation of NOC, Please Contact to the System Administrator.");
        }
    } // end of updateVtNOC

    public void insertIntoNOCHistoryVh(TransactionManager tmgr, NocDobj nocDobj, String empCode) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "INSERT INTO " + TableList.VH_NOC
                    + " ( moved_on, moved_by, state_cd, off_cd, appl_no, regn_no, state_to, off_to, rto_to,"
                    + "   ncrb_ref, dispatch_no, noc_no, noc_dt,reason_cd,new_owner,op_dt ) "
                    + "   SELECT current_timestamp,?,state_cd, off_cd, appl_no, regn_no, state_to, off_to, rto_to,"
                    + "       ncrb_ref, dispatch_no, noc_no, noc_dt, reason_cd, new_owner, op_dt "
                    + "  FROM " + TableList.VT_NOC + " where appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, nocDobj.getAppl_no());
            ps.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong During Moving Data of NOC into History of NOC , Please Contact to the System Administrator");
        }
    } // end of insertIntoNOCHistoryVh

    public static void insertIntoNOC(TransactionManager tmgr, NocDobj noc_dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            if (Util.getUserStateCode().equals("RJ")) {
                sql = "INSERT INTO " + TableList.VA_NOC + "(state_cd,off_cd,appl_no, regn_no, state_to, off_to, ncrb_ref, dispatch_no, op_dt,reason_cd,new_owner, noc_dt) "
                        + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, current_timestamp,?,?,?)";
            } else {
                sql = "INSERT INTO " + TableList.VA_NOC + "(state_cd,off_cd,appl_no, regn_no, state_to, off_to, ncrb_ref, dispatch_no, op_dt,reason_cd,new_owner) "
                        + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, current_timestamp,?,?)";
            }
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            ps.setString(3, noc_dobj.getAppl_no());
            ps.setString(4, noc_dobj.getRegn_no());
            ps.setString(5, noc_dobj.getState_to());
            ps.setInt(6, noc_dobj.getOff_to());
            ps.setString(7, noc_dobj.getNcrb_ref());
            ps.setString(8, noc_dobj.getDispatch_no());
            ps.setInt(9, noc_dobj.getPur_cd_to());
            ps.setString(10, noc_dobj.getNew_own_name());

            if (Util.getUserStateCode().equals("RJ")) {
                ps.setDate(11, new java.sql.Date(noc_dobj.getNoc_dt().getTime()));
            }

            // ps.execute();
            ps.executeUpdate();
        } catch (SQLException se) {
            LOGGER.error(se);
            throw new VahanException(se.getMessage());
        }
    } // end of insertIntoNOC

    public static void insertUpdateNOC(TransactionManager tmgr, String appl_no, String regn_no, NocDobj noc_dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {

            sql = "SELECT * FROM " + TableList.VA_NOC + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) { //if any record is exist then update otherwise insert it
                insertIntoNOCHistory(tmgr, appl_no);
                updateNOC(tmgr, noc_dobj);
            } else {
                insertIntoNOC(tmgr, noc_dobj);
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        }
    } // end of insertUpdateNOC

    public static void makeChangeNOC(NocDobj noc_dobj, String changedata, String selectedFancyRetnetion, Date applDate) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("makeChangeNOC");
//Pawan
            if (noc_dobj.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_NOC_CANCEL) {
                insertUpdateCancelNOC(tmgr, noc_dobj.getAppl_no(), noc_dobj.getRegn_no(), noc_dobj);
            } else {
                insertUpdateNOC(tmgr, noc_dobj.getAppl_no(), noc_dobj.getRegn_no(), noc_dobj);
                if (selectedFancyRetnetion != null && selectedFancyRetnetion.equalsIgnoreCase("YES")) {
                    ToDobj to_dobj = new ToDobj();
                    ToImpl toImpl = new ToImpl();
                    to_dobj.setAppl_no(noc_dobj.getAppl_no());
                    to_dobj.setRegn_no(noc_dobj.getRegn_no());
                    to_dobj.setReason("NOC ISSUED");
                    toImpl.insertIntoVaSurrenderRetention(tmgr, to_dobj, applDate);
                }
            }
//Ends
            ComparisonBeanImpl.updateChangedData(noc_dobj.getAppl_no(), changedata, tmgr);
            tmgr.commit();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    throw new VahanException(e.getMessage());
                }
            }
        }
    }//end of makeChangeNOC

//Pawan   Code Starts
    public static void insertUpdateCancelNOC(TransactionManager tmgr, String appl_no, String regn_no, NocDobj noc_dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {

            sql = "SELECT * FROM " + TableList.VA_NOC_CANCEL + " where appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) { //if any record is exist then update otherwise insert it
                insertIntoCancelNOCHistory(tmgr, appl_no);
                updateCancelNOC(tmgr, noc_dobj);
            } else {
                insertIntoCancelNOC(tmgr, noc_dobj);
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        }
    }

    public static void insertIntoCancelNOCHistory(TransactionManager tmgr, String appl_no) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "INSERT INTO " + TableList.VHA_NOC_CANCEL + "(moved_on, moved_by,state_cd,off_cd, appl_no, regn_no, state_to, off_to,rto_to, ncrb_ref, dispatch_no,noc_no,noc_dt, file_ref_no, approved_by,"
                    + " reason,  op_dt,reason_cd,new_owner)"
                    + "SELECT current_timestamp as moved_on, ? as moved_by,state_cd,off_cd, appl_no, regn_no, state_to, off_to, rto_to, ncrb_ref, dispatch_no,noc_no,noc_dt, file_ref_no, approved_by, reason ,"
                    + " op_dt,reason_cd,new_owner  FROM " + TableList.VA_NOC_CANCEL + " WHERE appl_no=? ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, appl_no);
            ps.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        }
    }

    private static void updateCancelNOC(TransactionManager tmgr, NocDobj noc_dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "UPDATE " + TableList.VA_NOC_CANCEL + ""
                    + "   SET file_ref_no=?, approved_by=?, reason=?, op_dt = current_timestamp"
                    + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, noc_dobj.getFile_ref_no());
            ps.setString(2, noc_dobj.getApp_by());
            ps.setString(3, noc_dobj.getReason());
            ps.setString(4, noc_dobj.getAppl_no());
            ps.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        }
    }

    private static void insertIntoCancelNOC(TransactionManager tmgr, NocDobj noc_dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            //need to update for noc_no which is generated by some table.
            sql = "INSERT INTO " + TableList.VA_NOC_CANCEL + "(\n"
                    + " state_cd,off_cd, appl_no, regn_no, state_to, off_to,rto_to, ncrb_ref, dispatch_no, \n"
                    + "   noc_no, noc_dt, file_ref_no, approved_by, reason, op_dt,reason_cd,new_owner)\n"
                    + "SELECT state_cd,off_cd, ? as appl_no, regn_no, state_to, off_to,rto_to, ncrb_ref, dispatch_no, \n"
                    + "       noc_no, noc_dt, ?, ?, ?, current_timestamp,reason_cd,new_owner\n"
                    + "  FROM " + TableList.VT_NOC + " WHERE regn_no=? and state_cd = ? and off_cd = ? order by noc_dt DESC limit 1";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, noc_dobj.getAppl_no());
            ps.setString(2, noc_dobj.getFile_ref_no());
            ps.setString(3, noc_dobj.getApp_by());
            ps.setString(4, noc_dobj.getReason());
            ps.setString(5, noc_dobj.getRegn_no());
            ps.setString(6, noc_dobj.getState_cd());
            ps.setInt(7, noc_dobj.getOff_cd());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        }
    }
//Code Ends

    /**
     * Function to validate NOC Cancellation Application Checks that NOC
     * Cancellation Application is valid or not. Checks that the authority will
     * be same for both issue of noc and cancellation of noc
     *
     * @param regn_no
     * @return
     */
    public static boolean validateAppOfNoc(String regn_no) throws VahanException {
        TransactionManager tmgr = null;
        String state_cd = Util.getUserStateCode();
        int off_cd = Util.getSelectedSeat().getOff_cd();
        String status = "";
        PreparedStatement ps = null;
        RowSet rs = null;
        boolean retFlg = false;
        try {
            ApplicationInwardImpl inwardImpl = new ApplicationInwardImpl();
            Status_dobj applInwardOthOffDobj = inwardImpl.getApplInwardOthOffDobj(Util.getSelectedSeat().getAppl_no());
            String sql = "Select status from " + TableList.VT_OWNER + " where regn_no=? and state_cd = ? and off_cd  =? ";
            tmgr = new TransactionManager("validateAppOfNoc");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            if (applInwardOthOffDobj != null) {
                ps.setInt(3, applInwardOthOffDobj.getOff_cd());
            } else {
                ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            }
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                status = rs.getString("status");
            }
            if (status.equalsIgnoreCase(TableConstants.VT_NOC_ISSUE_STATUS)) {
                String sql1 = "Select state_cd,off_cd from " + TableList.VT_NOC + " where regn_no=? and state_cd = ? and off_cd  =? order by noc_dt desc limit 1";
                ps = tmgr.prepareStatement(sql1);
                ps.setString(1, regn_no);
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    if (state_cd.equalsIgnoreCase(rs.getString("state_cd")) && off_cd == rs.getInt("off_cd")) {
                        retFlg = true;
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
            //return false;
        }
        return retFlg;
    }

    public void checkForNocVerificationAndEndorsement(Owner_dobj owner_dobj, String regnNo) throws VahanException, Exception {
        NocDobj noc_dobj = ServerUtil.getChasiNoExist(owner_dobj.getChasi_no());
        if (noc_dobj != null) {
            if (noc_dobj.getVt_owner_status().equals("N")) {
                Date vahan4StartDate = ServerUtil.getVahan4StartDate(noc_dobj.getState_to(), noc_dobj.getOff_to());
                if (vahan4StartDate == null) {
                    if (noc_dobj.getState_cd().equals(Util.getUserStateCode()) && Util.getSelectedSeat().getOff_cd() == noc_dobj.getOff_cd() || noc_dobj.getState_to().equals(Util.getUserStateCode()) && Util.getSelectedSeat().getOff_cd() == noc_dobj.getOff_to()) {
                        throw new VahanException(TableConstants.NOC_ENDORSEMENT);
                    } else if (!noc_dobj.getState_to().equals(Util.getUserStateCode()) || Util.getSelectedSeat().getOff_cd() != noc_dobj.getOff_to()) {
                        NocDobj nocVerifiedData = ServerUtil.getNocVerifiedData(regnNo, owner_dobj.getChasi_no());
                        if (nocVerifiedData == null) {
                            throw new VahanException(TableConstants.NOC_VERIFICATION);
                        }
                    }
                } else {
                    if (!noc_dobj.getState_to().equals(Util.getUserStateCode()) || Util.getSelectedSeat().getOff_cd() != noc_dobj.getOff_to()) {
                        State stateCd = MasterTableFiller.state.get(noc_dobj.getState_to());
                        String offCdLabel = null;
                        if (stateCd != null) {
                            List<SelectItem> listOff = stateCd.getOffice();
                            for (SelectItem off : listOff) {
                                if (off.getValue().toString().equals(String.valueOf(noc_dobj.getOff_to()))) {
                                    offCdLabel = off.getLabel();
                                    break;
                                }
                            }
                        }
                        throw new VahanException("Vehicle has NOC issued to state : " + stateCd.getStateDescr() + " Office: " + offCdLabel);
                    }
                }
            } else {
                // if vt_owner status not equal to N and chassisno already exist bcz on that chassi no registration can be don using regntype is other statew/ other rto if vt_owner status equal to N
                throw new VahanException("Vehicle is already Registered!!!");
            }
        }
    }

    public void updateAdminNOC(NocDobj nocDobj, String changedata, String empCode) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("updateAdminNOC");
            insertIntoNOCHistoryVh(tmgr, nocDobj, empCode);
            updateVtNOC(tmgr, nocDobj);
            ComparisonBeanImpl.updateChangedData(nocDobj.getRegn_no(), changedata, tmgr);
            tmgr.commit();
        } catch (VahanException vex) {
            throw vex;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong During the Data Saving, Please Contact to the System Administrator.");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong During the Data Saving, Please Contact to the System Administrator.");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    throw new VahanException("Something Went Wrong During the Data Saving, Please Contact to the System Administrator.");
                }
            }
        }
    }//end of updateAdminNOC   

    public static String[][] getNocReason() {

        PreparedStatement ps = null;
        RowSet rs = null;
        String sql = null;
        TransactionManager tmgr = null;
        Map<String, String> map = new HashMap<String, String>();
        String[][] arr = null;
        try {
            tmgr = new TransactionManager("getNocReason");
            sql = "SELECT * from vm_noc_reason order by code";
            ps = tmgr.prepareStatement(sql);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                map.put(rs.getInt("code") + "", rs.getString("descr"));
            }

            arr = new String[map.size()][2];
            Set set = map.entrySet();
            Iterator itr = set.iterator();
            int count = 0;
            while (itr.hasNext()) {
                Map.Entry entryMap = (Map.Entry) itr.next();
                arr[count][0] = (String) entryMap.getKey();
                arr[count][1] = (String) entryMap.getValue();
                count++;
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
        return arr;
    }

    public NocDobj getNocDetails(String regn_no, String stateCd, int offCd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        NocDobj noc_dobj = null;
        String sql = "SELECT state_cd, off_cd, appl_no, regn_no, state_to, off_to, rto_to, "
                + "       ncrb_ref, dispatch_no, noc_no, noc_dt,reason_cd,new_owner FROM " + TableList.VT_NOC + " WHERE regn_no=? and state_cd= ? and off_cd = ? order by noc_dt desc limit 1";
        try {
            tmgr = new TransactionManager("getVtNocDate");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                noc_dobj = new NocDobj();
                noc_dobj.setAppl_no(rs.getString("appl_no"));
                noc_dobj.setRegn_no(rs.getString("regn_no"));
                noc_dobj.setState_to(rs.getString("state_to"));
                noc_dobj.setOff_to(rs.getInt("off_to"));
                noc_dobj.setNoc_no(rs.getString("noc_no"));
                noc_dobj.setNoc_dt(rs.getDate("noc_dt"));
                noc_dobj.setNcrb_ref(rs.getString("ncrb_ref"));
                noc_dobj.setDispatch_no(rs.getString("dispatch_no"));
                noc_dobj.setPur_cd_to(rs.getInt("reason_cd"));
                noc_dobj.setNew_own_name(rs.getString("new_owner"));
                noc_dobj.setState_cd(rs.getString("state_cd"));
                noc_dobj.setOff_cd(rs.getInt("off_cd"));
            }
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
        return noc_dobj;
    }
}
