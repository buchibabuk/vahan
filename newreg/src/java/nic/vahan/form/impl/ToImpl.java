/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.AuctionDobj;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Rereg_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.ToDobj;
import nic.vahan.form.dobj.common.Appl_Details_Dobj;
import nic.vahan.form.dobj.tax.TaxDobj;
import nic.vahan.form.impl.tax.TaxImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.NewVehicleNo;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.*;
import org.apache.log4j.Logger;

public class ToImpl {

    private static final Logger LOGGER = Logger.getLogger(ToImpl.class);

    public ToDobj set_TO_appl_db_to_dobj(String appl_no) {

        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        ToDobj to_dobj = null;
        try {

            tmgr = new TransactionManager("set_TO_appl_db_to_dobj");

            sql = "SELECT state_cd,off_cd,appl_no,regn_no,owner_name,owner_cd,owner_ctg,"
                    + " f_name,owner_sr,sale_amt,reason,garage_add,sale_dt,transfer_dt,"
                    + " c_add1,c_add2,c_add3,c_state,c_district,c_pincode,"
                    + " p_add1,p_add2,p_add3,p_state,p_district,p_pincode,owner_from "
                    + " FROM " + TableList.VA_TO + " where appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                to_dobj = new ToDobj();

                to_dobj.setStateCode(rs.getString("state_cd"));
                to_dobj.setOffCode(rs.getInt("off_cd"));
                to_dobj.setAppl_no(rs.getString("appl_no"));
                to_dobj.setRegn_no(rs.getString("regn_no"));
                to_dobj.setOwner_name(rs.getString("owner_name"));
                to_dobj.setOwner_cd(rs.getInt("owner_cd"));
                to_dobj.setOwner_ctg(rs.getInt("owner_ctg"));
                to_dobj.setF_name(rs.getString("f_name"));
                to_dobj.setOwner_sr(rs.getInt("owner_sr"));
                to_dobj.setSale_amt(rs.getInt("sale_amt"));
                to_dobj.setReason(rs.getString("reason"));
                to_dobj.setGarage_add(rs.getString("garage_add"));
                to_dobj.setSale_dt(rs.getTimestamp("sale_dt"));
                to_dobj.setTransfer_dt(rs.getTimestamp("transfer_dt"));
                to_dobj.setC_add1(rs.getString("c_add1"));
                to_dobj.setC_add2(rs.getString("c_add2"));
                to_dobj.setC_add3(rs.getString("c_add3"));
                to_dobj.setC_state(rs.getString("c_state"));
                to_dobj.setC_district(rs.getInt("c_district"));
                to_dobj.setC_pincode(rs.getInt("c_pincode"));
                to_dobj.setP_add1(rs.getString("p_add1"));
                to_dobj.setP_add2(rs.getString("p_add2"));
                to_dobj.setP_add3(rs.getString("p_add3"));
                to_dobj.setP_state(rs.getString("p_state"));
                to_dobj.setP_district(rs.getInt("p_district"));
                to_dobj.setP_pincode(rs.getInt("p_pincode"));
                to_dobj.setOwner_from(rs.getTimestamp("owner_from"));
            }

        } catch (Exception ex) {
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
        return to_dobj;
    }

    public void update_TO_Status(ToDobj to_dobj, ToDobj to_dobj_prv, OwnerIdentificationDobj owner_identification, Status_dobj status_dobj, String changedData, String selectedFancyRetnetion, Appl_Details_Dobj applDetailsDobj, AuctionDobj auctionDobj) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        boolean toWithRetentionOrConv = false;
        String newRegnNo = null;
        boolean NumGenrateWithTOReRegCon = false;
        boolean regNumAssign = false;

        try {

            tmgr = new TransactionManager("update_TO_Status");
            //=================WEB SERVICES FOR NEXTSTAGE START=========//
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            Date applDate = format.parse(status_dobj.getAppl_dt());
            //=================WEB SERVICES FOR NEXTSTAGE END=========//

            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_ENTRY
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_VERIFICATION
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_APPROVAL) {

                if ((changedData != null && !changedData.equals("")) || to_dobj_prv == null) {

                    insertUpdateTO(tmgr, to_dobj.getAppl_no(), to_dobj.getRegn_no(), to_dobj); //when there is change by user or Entry Scrutiny
                    OwnerIdentificationImpl.insertUpdateOwnerIdentification(tmgr, owner_identification);

                }

                if (selectedFancyRetnetion != null && selectedFancyRetnetion.equalsIgnoreCase("YES")) {
                    insertIntoVaSurrenderRetention(tmgr, to_dobj, applDate);

                    String purCode = statusList(to_dobj.getAppl_no(), to_dobj.getRegn_no(), applDetailsDobj.getCurrent_state_cd());
                    if (purCode.contains("," + TableConstants.VM_TRANSACTION_MAST_TO + ",") && purCode.contains("," + TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION + ",")) {
                        insertVaStopDupRegnNoGen(to_dobj.getAppl_no(), to_dobj.getRegn_no(), null, TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION, tmgr);
                    } else if (purCode.contains("," + TableConstants.VM_TRANSACTION_MAST_TO + ",") && purCode.contains("," + TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO + ",")) {
                        insertVaStopDupRegnNoGen(to_dobj.getAppl_no(), to_dobj.getRegn_no(), null, TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO, tmgr);
                    }
                } else if (selectedFancyRetnetion != null && selectedFancyRetnetion.equalsIgnoreCase("NO")) {
                    insertIntoVhaSurrenderRetention(tmgr, to_dobj.getAppl_no());
                    ServerUtil.deleteFromTable(tmgr, null, to_dobj.getAppl_no(), TableList.VA_SURRENDER_RETENTION);
                }

                if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_ENTRY) {
                    if (to_dobj.isAssignRetainNo() && to_dobj.getAssignRetainRegnNo() != null) {
                        updateRetentionRegNoDetails(to_dobj.getAppl_no(), to_dobj.getAssignRetainRegnNo(), tmgr);
                    } else if (to_dobj.isAssignFancyNumber() && to_dobj.getAssignFancyRegnNumber() != null) {
                        new Rereg_Impl().updateAdvanceRegNoDetails(to_dobj.getAppl_no(), to_dobj.getAssignFancyRegnNumber(), tmgr);
                    }
                }
            }

            if (TableConstants.STATUS_HOLD.equals(status_dobj.getStatus()) && applDetailsDobj.getOwnerDobj() != null && applDetailsDobj.getOwnerDobj().getOwner_identity() != null) {
                ServerUtil.sendSMSForHoldApplication(applDetailsDobj.getOwnerDobj().getOwner_identity().getMobile_no(), status_dobj.getOffice_remark(), applDetailsDobj.getAppl_no());
            }

            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_APPROVAL
                    && !status_dobj.getStatus().trim().equals(TableConstants.STATUS_REVERT)
                    && !status_dobj.getStatus().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                //Approve Insurance
                String pur_cd_d = "";
                InsImpl.approvalInsurance(tmgr, to_dobj.getRegn_no(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), Util.getEmpCode());
                //For Checking if Other Application is Pending for Approval Befor TO Approval
                List<Status_dobj> statusList = ServerUtil.applicationStatus(to_dobj.getRegn_no(), to_dobj.getAppl_no(), applDetailsDobj.getCurrent_state_cd());
                if (!statusList.isEmpty() && statusList.size() > 1) {
                    for (int i = 0; i < statusList.size(); i++) {
                        if (statusList.get(i).getPur_cd() == TableConstants.VM_TRANSACTION_MAST_HPC) {
                            throw new VahanException("HPC is Pending for this Application No, First Approve HPC Before Approving TO.");
                        }
                        pur_cd_d = pur_cd_d + "," + statusList.get(i).getPur_cd();
                        if ((statusList.get(i).getPur_cd() == TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO || statusList.get(i).getPur_cd() == TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION)
                                && statusList.get(i).getAction_cd() == TableConstants.VM_ROLE_REGISTRATION_NO_ASSIGNMENT_RTO) {
                            regNumAssign = true;
                        }
                    }
                    pur_cd_d = pur_cd_d + ",";
                    if (pur_cd_d.contains("," + TableConstants.VM_TRANSACTION_MAST_TO + ",") && !regNumAssign) {
                        if (pur_cd_d.contains("," + TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO + ",")) {
                            for (int i = 0; i < statusList.size(); i++) {
                                if (statusList.get(i).getPur_cd() == TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO && (statusList.get(i).getAction_cd() != TableConstants.TM_ROLE_REASIGN_APPROVAL && statusList.get(i).getAction_cd() != TableConstants.VM_ROLE_REGISTRATION_NO_ASSIGNMENT_RTO)) {
                                    throw new VahanException("Reassign is Pending for this Application No, First Verify Reassign Before Approving TO.");
                                }
                            }
                            NumGenrateWithTOReRegCon = isNumGenrateWithTOReRegCon(to_dobj.getAppl_no(), TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO);
                        } else if (pur_cd_d.contains("," + TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION + ",")) {
                            for (int i = 0; i < statusList.size(); i++) {
                                if (statusList.get(i).getPur_cd() == TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION && (statusList.get(i).getAction_cd() != TableConstants.TM_ROLE_CONV_APPROVAL && statusList.get(i).getAction_cd() != TableConstants.VM_ROLE_REGISTRATION_NO_ASSIGNMENT_RTO)) {
                                    throw new VahanException("Conversion is Pending for this Application No, First Verify Conversion Before Approving TO.");
                                }
                            }
                            NumGenrateWithTOReRegCon = isNumGenrateWithTOReRegCon(to_dobj.getAppl_no(), TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION);
                        }
                    }
                    if ("KL".contains(applDetailsDobj.getCurrent_state_cd())) {
                        for (int i = 0; i < statusList.size(); i++) {
                            if (statusList.get(i).getPur_cd() == TableConstants.VM_TRANSACTION_MAST_NOC) {
                                throw new VahanException("NOC is Pending for this Application No, First Approve NOC Before Approving TO.");
                            }
                        }
                    }
                }
                // inserting data into vh_to from vt_owner
                sql = "INSERT INTO " + TableList.VH_TO
                        + " SELECT o.state_cd, o.off_cd, ?, o.regn_no, ?, o.owner_sr, o.owner_name, o.owner_cd, ?, \n"
                        + "       o.f_name, o.c_add1, o.c_add2, o.c_add3, o.c_district, o.c_pincode, \n"
                        + "       o.c_state, o.p_add1, o.p_add2, o.p_add3, o.p_district, o.p_pincode, o.p_state, \n"
                        + "       ?, current_timestamp as owner_upto, ?, ?, ?, ?, \n"
                        + "       o.garage_add, current_timestamp as moved_on, ? moved_by  \n"
                        + "  FROM " + TableList.VT_OWNER + " o where o.regn_no = ?";

                ps = tmgr.prepareStatement(sql);

                ps.setString(1, to_dobj.getAppl_no());
                ps.setInt(2, to_dobj.getPur_cd());
                ps.setInt(3, to_dobj.getOwner_ctg());
                if (to_dobj.getOwner_from() != null) {
                    ps.setDate(4, new java.sql.Date(to_dobj.getOwner_from().getTime()));
                } else if (to_dobj.getOwner_from() == null) {
                    long owner_from = getOwnerFrom(tmgr, to_dobj.getRegn_no(), TableList.VH_TO);
                    if (owner_from == 0) {
                        throw new VahanException("Calculation of 'Ownership Period From' is Unable to Calculate, Please Contact to System Administrator!!!");
                    }
                    ps.setDate(4, new java.sql.Date(owner_from));
                }
                ps.setDate(5, new java.sql.Date(to_dobj.getSale_dt().getTime()));
                ps.setDate(6, new java.sql.Date(to_dobj.getTransfer_dt().getTime()));
                ps.setInt(7, to_dobj.getSale_amt());
                ps.setString(8, to_dobj.getReason());
                ps.setString(9, applDetailsDobj.getCurrentEmpCd());
                ps.setString(10, to_dobj.getRegn_no());
                ps.executeUpdate();

                //if current office and vehicle registration office is not same
                if (applDetailsDobj.getCurrent_state_cd().equalsIgnoreCase(applDetailsDobj.getOwnerDobj().getState_cd())
                        && applDetailsDobj.getCurrent_off_cd() != applDetailsDobj.getOwnerDobj().getOff_cd()) {
                    NewImpl newImpl = new NewImpl();
                    newImpl.updateOffCodeInRegnTables(tmgr, to_dobj.getAppl_no(), to_dobj.getRegn_no(), applDetailsDobj.getCurrent_state_cd(), applDetailsDobj.getCurrent_off_cd(), applDetailsDobj.getCurrentEmpCd());
                }

                //updation of vt_ownwer
                if (!applDetailsDobj.getCurrent_state_cd().equalsIgnoreCase("KL")) {
                    sql = " update " + TableList.VT_OWNER + " set "
                            + " owner_name=?,"
                            + " owner_cd=?,"
                            + " owner_sr=?,"
                            + " f_name=?,"
                            + " garage_add=?,"
                            + " c_add1=?,"
                            + " c_add2=?,"
                            + " c_add3=?,"
                            + " c_state=?,"
                            + " c_district=?,"
                            + " c_pincode=?,"
                            + " p_add1=?,"
                            + " p_add2=?,"
                            + " p_add3=?,"
                            + " p_state=?,"
                            + " p_district=?,"
                            + " p_pincode=?,"
                            + " op_dt=current_timestamp "
                            + " WHERE regn_no=? and state_cd = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, to_dobj.getOwner_name());
                    ps.setInt(2, to_dobj.getOwner_cd());
                    ps.setInt(3, to_dobj.getOwner_sr());
                    ps.setString(4, to_dobj.getF_name());
                    ps.setString(5, to_dobj.getGarage_add());
                    ps.setString(6, to_dobj.getC_add1());
                    ps.setString(7, to_dobj.getC_add2());
                    ps.setString(8, to_dobj.getC_add3());
                    ps.setString(9, to_dobj.getC_state());
                    ps.setInt(10, to_dobj.getC_district());
                    ps.setInt(11, to_dobj.getC_pincode());
                    ps.setString(12, to_dobj.getP_add1());
                    ps.setString(13, to_dobj.getP_add2());
                    ps.setString(14, to_dobj.getP_add3());
                    ps.setString(15, to_dobj.getP_state());
                    ps.setInt(16, to_dobj.getP_district());
                    ps.setInt(17, to_dobj.getP_pincode());
                    ps.setString(18, to_dobj.getRegn_no());
                    ps.setString(19, applDetailsDobj.getCurrent_state_cd());
                    ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                } else {
                    sql = " update " + TableList.VT_OWNER + " set "
                            + " owner_name=?,"
                            + " owner_cd=?,"
                            + " owner_sr=?,"
                            + " f_name=?,"
                            + " garage_add=?,"
                            + " c_add1=?,"
                            + " c_add2=?,"
                            + " c_add3=?,"
                            + " c_state=?,"
                            + " c_district=?,"
                            + " c_pincode=?,"
                            + " p_add1=?,"
                            + " p_add2=?,"
                            + " p_add3=?,"
                            + " p_state=?,"
                            + " p_district=?,"
                            + " p_pincode=?,"
                            + " status='A',"
                            + " op_dt=current_timestamp "
                            + " WHERE regn_no=? and state_cd = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, to_dobj.getOwner_name());
                    ps.setInt(2, to_dobj.getOwner_cd());
                    ps.setInt(3, to_dobj.getOwner_sr());
                    ps.setString(4, to_dobj.getF_name());
                    ps.setString(5, to_dobj.getGarage_add());
                    ps.setString(6, to_dobj.getC_add1());
                    ps.setString(7, to_dobj.getC_add2());
                    ps.setString(8, to_dobj.getC_add3());
                    ps.setString(9, to_dobj.getC_state());
                    ps.setInt(10, to_dobj.getC_district());
                    ps.setInt(11, to_dobj.getC_pincode());
                    ps.setString(12, to_dobj.getP_add1());
                    ps.setString(13, to_dobj.getP_add2());
                    ps.setString(14, to_dobj.getP_add3());
                    ps.setString(15, to_dobj.getP_state());
                    ps.setInt(16, to_dobj.getP_district());
                    ps.setInt(17, to_dobj.getP_pincode());
                    ps.setString(18, to_dobj.getRegn_no());
                    ps.setString(19, applDetailsDobj.getCurrent_state_cd());
                    ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                }

                owner_identification.setState_cd(applDetailsDobj.getCurrent_state_cd());
                owner_identification.setOff_cd(applDetailsDobj.getCurrent_off_cd());
                OwnerIdentificationImpl.insertUpdateOwnerIdentificationVT(tmgr, owner_identification);

                sql = "INSERT INTO " + TableList.VHA_TO
                        + " SELECT current_timestamp + interval '1 second' as moved_on, ? moved_by,"
                        + "        state_cd,off_cd,appl_no, regn_no, pur_cd, owner_sr, owner_name, owner_cd, owner_ctg, \n"
                        + "        f_name, c_add1, c_add2, c_add3, c_district, c_pincode, \n"
                        + "        c_state, p_add1, p_add2, p_add3, p_district, p_pincode, p_state, \n"
                        + "        owner_from, sale_dt, transfer_dt, sale_amt, reason, garage_add, \n"
                        + "        op_dt"
                        + "  FROM " + TableList.VA_TO + " where appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, applDetailsDobj.getCurrentEmpCd());
                ps.setString(2, to_dobj.getAppl_no());
                ps.executeUpdate();

                sql = "DELETE FROM " + TableList.VA_TO + " where appl_no = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, to_dobj.getAppl_no());
                ps.executeUpdate();

                sql = "DELETE FROM " + TableList.VA_OWNER_IDENTIFICATION + " where appl_no = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, to_dobj.getAppl_no());
                ps.executeUpdate();

                if (selectedFancyRetnetion != null && (selectedFancyRetnetion.equalsIgnoreCase("YES") || selectedFancyRetnetion.equalsIgnoreCase("NO"))) {
                    insertIntoVhaSurrenderRetention(tmgr, status_dobj.getAppl_no());
                    if (selectedFancyRetnetion.equalsIgnoreCase("YES")) {
                        if (!NumGenrateWithTOReRegCon) {
                            newRegnNo = insertIntoVtSurrenderRetention(tmgr, applDetailsDobj.getOwnerDobj(), to_dobj.getReason(), status_dobj.getAppl_no(), applDate, TableConstants.VM_TRANSACTION_MAST_TO, toWithRetentionOrConv);
                        }
                        ServerUtil.deleteFromTable(tmgr, null, to_dobj.getAppl_no(), TableList.VA_SURRENDER_RETENTION);
                    }
                }
                if (!NumGenrateWithTOReRegCon && !regNumAssign) {
                    if (to_dobj.isAssignRetainNo() || to_dobj.isAssignFancyNumber()) {
                        Rereg_Impl obj = new Rereg_Impl();
                        Rereg_dobj dobj = new Rereg_dobj();
                        dobj.setAppl_no(to_dobj.getAppl_no());
                        dobj.setOld_regn_no(to_dobj.getRegn_no());
                        if (to_dobj.isAssignRetainNo()) {
                            dobj.setNew_regn_no(to_dobj.getAssignRetainRegnNo());
                        } else {
                            dobj.setNew_regn_no(to_dobj.getAssignFancyRegnNumber());
                        }
                        obj.updateFeeAndTaxForAdvanceRegnNo(tmgr, dobj);
                        if (to_dobj.isAssignRetainNo()) {
                            SwappingRegnImpl.updateTablesForRetention(tmgr, to_dobj.getRegn_no(), to_dobj.getAssignRetainRegnNo());
                        } else {
                            SwappingRegnImpl.updateTablesForRetention(tmgr, to_dobj.getRegn_no(), to_dobj.getAssignFancyRegnNumber());
                        }
                        String query = "Select appl_no from " + TableList.VH_RE_ASSIGN + " where state_cd = ? and off_cd =? and appl_no = ?";
                        ps = tmgr.prepareStatement(query);
                        ps.setString(1, Util.getUserStateCode());
                        ps.setInt(2, Util.getSelectedSeat().getOff_cd());
                        ps.setString(3, to_dobj.getAppl_no());
                        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                        if (!rs.next()) {

                            query = "INSERT INTO " + TableList.VH_RE_ASSIGN + "("
                                    + "            state_cd, off_cd, appl_no, old_regn_no, new_regn_no, reason,"
                                    + "            moved_on, moved_by)"
                                    + "    VALUES (?, ?, ?, ?, ?, ?,"
                                    + "            current_timestamp, ?)";
                            ps = tmgr.prepareStatement(query);
                            ps.setString(1, Util.getUserStateCode());
                            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
                            ps.setString(3, to_dobj.getAppl_no());
                            ps.setString(4, to_dobj.getRegn_no());
                            // ps.setString(5, to_dobj.getAssignRetainRegnNo());
                            if (to_dobj.isAssignRetainNo()) {
                                ps.setString(5, to_dobj.getAssignRetainRegnNo());
                            } else {
                                ps.setString(5, to_dobj.getAssignFancyRegnNumber());
                            }
                            ps.setString(6, "Ret RMA With TO");
                            ps.setString(7, Util.getEmpCode());
                            ps.executeUpdate();
                        }
                    }
                }
                //Release from Tax Exemption and tax_clear if any Start  
                sql = "INSERT INTO " + TableList.VH_TAX_EXEM
                        + " SELECT current_timestamp as moved_on, ? as moved_by,"
                        + " a.state_cd, a.off_cd, ?, a.regn_no, a.exem_fr, a.exem_to,"
                        + " a.exem_by, a.perm_no, a.perm_dt, a.remark, a.user_cd, a.op_dt"
                        + " FROM " + TableList.VT_TAX_EXEM + " a "
                        + " WHERE  regn_no=? and state_cd=? and exem_to > current_date";
                ps = tmgr.prepareStatement(sql);
                ps.setLong(1, Long.parseLong(applDetailsDobj.getCurrentEmpCd()));
                ps.setString(2, to_dobj.getAppl_no());
                ps.setString(3, to_dobj.getRegn_no());
                ps.setString(4, applDetailsDobj.getCurrent_state_cd());
                ps.executeUpdate();

                sql = "UPDATE " + TableList.VT_TAX_EXEM + " SET remark=?,exem_to=current_date,user_cd=?,op_dt=current_timestamp"
                        + " WHERE regn_no = ? and state_cd=? and exem_to > current_date";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, "Transfer of Ownership");
                ps.setString(2, applDetailsDobj.getCurrentEmpCd());
                ps.setString(3, to_dobj.getRegn_no());
                ps.setString(4, applDetailsDobj.getCurrent_state_cd());
                ps.executeUpdate();

                TaxImpl taxImpl = new TaxImpl();
                TaxDobj taxDobj = taxImpl.getTaxDetails(to_dobj.getRegn_no(), String.valueOf(TableConstants.TM_ROAD_TAX), Util.getUserStateCode());
                String taxModes = "," + TableConstants.TAX_MODE_ONE_TIME + "," + TableConstants.TAX_MODE_LIFE_TIME + "," + TableConstants.TAX_MODE_LUMP_SUM + "," + TableConstants.TAX_MODE_FIVE_YEAR + "," + TableConstants.TAX_MODE_TEN_YEAR + "," + TableConstants.TAX_MODE_FIFTEEN_YEAR + ",";
                if (taxDobj != null && !taxModes.contains("," + taxDobj.getTax_mode() + ",") && taxDobj.getTax_upto() != null && DateUtils.compareDates(taxDobj.getTax_upto(), new Date()) == 1)/*if tax is not paid for future date*/ {
                    sql = "INSERT INTO " + TableList.VT_TAX_CLEAR
                            + " SELECT state_cd, off_cd, ? as appl_no, regn_no, pur_cd, clear_fr, current_date as clear_to,"
                            + "       tcr_no, iss_auth, current_date as iss_dt, ? as remark, ? as user_cd, current_timestamp as op_dt "
                            + "  FROM " + TableList.VT_TAX_CLEAR
                            + "  WHERE regn_no = ? and state_cd = ? and off_cd=? and clear_to > current_date"
                            + "  and (state_cd, off_cd, regn_no, pur_cd, op_dt) in "
                            + " (select state_cd, off_cd, regn_no, pur_cd, max(op_dt) as op_dt "
                            + "  FROM " + TableList.VT_TAX_CLEAR
                            + "  where regn_no = ? and state_cd=? and off_cd=? and clear_to > current_date group by 1,2,3,4)";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, to_dobj.getAppl_no());
                    ps.setString(2, "Transfer of Ownership");
                    ps.setString(3, applDetailsDobj.getCurrentEmpCd());
                    ps.setString(4, to_dobj.getRegn_no());
                    ps.setString(5, applDetailsDobj.getCurrent_state_cd());
                    ps.setInt(6, applDetailsDobj.getCurrent_off_cd());
                    ps.setString(7, to_dobj.getRegn_no());
                    ps.setString(8, applDetailsDobj.getCurrent_state_cd());
                    ps.setInt(9, applDetailsDobj.getCurrent_off_cd());
                    ps.executeUpdate();
                }
                //Release from Tax Exemption and tax_clear if any End

                //for updating the status of application when it is approved start
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                //for updating the status of application when it is approved end

                //for approving HPC because there is only fee but not approval of HPC start
                Status_dobj statusDobj = new Status_dobj();
                statusDobj.setAppl_no(status_dobj.getAppl_no());
                statusDobj.setPur_cd(TableConstants.VM_TRANSACTION_MAST_HPC);
                statusDobj.setEntry_status(status_dobj.getEntry_status());
                ServerUtil.updateApprovedStatus(tmgr, statusDobj);
                ApplicationInwardImpl applicationInwardImpl = new ApplicationInwardImpl();
                applicationInwardImpl.deleteVaStatus(tmgr, statusDobj.getAppl_no(), TableConstants.VM_TRANSACTION_MAST_HPC);
                //for approving HPC because there is only fee but not approval of HPC end

                //SmartCard Or Print
                if (selectedFancyRetnetion != null && (selectedFancyRetnetion.equalsIgnoreCase("YES"))) {
                    if (!CommonUtils.isNullOrBlank(newRegnNo)) {
                        ServerUtil.VerifyInsertSmartCardPrintDetail(to_dobj.getAppl_no(), newRegnNo,
                                applDetailsDobj.getCurrent_state_cd(), applDetailsDobj.getCurrent_off_cd(), status_dobj.getPur_cd(), tmgr);
                    }
                } else {
                    if (!NumGenrateWithTOReRegCon && !regNumAssign) {
                        if (to_dobj.isAssignRetainNo()) {
                            to_dobj.setRegn_no(to_dobj.getAssignRetainRegnNo());
                        } else if (to_dobj.isAssignFancyNumber()) {
                            to_dobj.setRegn_no(to_dobj.getAssignFancyRegnNumber());
                        }
                    }
                    ServerUtil.VerifyInsertSmartCardPrintDetail(to_dobj.getAppl_no(), to_dobj.getRegn_no(),
                            applDetailsDobj.getCurrent_state_cd(), applDetailsDobj.getCurrent_off_cd(), status_dobj.getPur_cd(), tmgr); //checkcondition
                }

                if (auctionDobj != null) {
                    if (newRegnNo != null) {
                        status_dobj.setRegn_no(newRegnNo);
                    } else {
                        status_dobj.setRegn_no(to_dobj.getRegn_no());
                    }
                    status_dobj.setEmp_cd(Long.parseLong(Util.getEmpCode()));
                    new AuctionImpl().insertIntoVhAuction(tmgr, status_dobj, auctionDobj.getRegnNo(), null);
                    new AuctionImpl().updateVtOwnerStatus(tmgr, status_dobj.getRegn_no(), TableConstants.STATUS_APPROVED);
                    ServerUtil.deleteFromTable(tmgr, auctionDobj.getRegnNo(), null, TableList.VT_AUCTION);
                }
            }

            insertIntoVhaChangedData(tmgr, to_dobj.getAppl_no(), changedData); //for saving the data into table those are changed by the user

            if (status_dobj.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_FRESH_RC
                    && status_dobj.getEntry_status() != null
                    && status_dobj.getEntry_status().equals(TableConstants.STATUS_APPROVED)
                    && "OR".equals(applDetailsDobj.getCurrent_state_cd())) {
                sql = "Delete From " + TableList.VA_FRC_PRINT + " WHERE appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, status_dobj.getAppl_no());
                ps.executeUpdate();
            }

            fileFlow(tmgr, status_dobj); // for updating va_status and vha_status for new role,seat for the other employee

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
    }//end of update_TO_status()

    public static void insertIntoToHistory(TransactionManager tmgr, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        //inserting data into vha_to from va_to
        sql = "INSERT INTO " + TableList.VHA_TO
                + " SELECT current_timestamp as moved_on, ? moved_by,"
                + "        state_cd,off_cd,appl_no, regn_no, pur_cd, owner_sr, owner_name, owner_cd, owner_ctg, \n"
                + "        f_name, c_add1, c_add2, c_add3, c_district, c_pincode,"
                + "        c_state, p_add1, p_add2, p_add3, p_district, p_pincode, p_state,"
                + "        owner_from, sale_dt, transfer_dt, sale_amt, reason, garage_add, op_dt"
                + "  FROM " + TableList.VA_TO + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();
    } // end of insertIntoToHistory

    public static void updateTo(TransactionManager tmgr, ToDobj to_dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        int pos = 1;

        sql = "UPDATE " + TableList.VA_TO
                + "   SET owner_name=?, owner_cd=?, owner_ctg=?, "
                + "       f_name=?, c_add1=?, c_add2=?, c_add3=?, c_state=?, "
                + "       c_district=?, c_pincode=?, p_add1=?, p_add2=?, p_add3=?, p_state=?, "
                + "       p_district=?, p_pincode=?, sale_dt=?,transfer_dt=?, sale_amt=?,"
                + "       reason=?, garage_add=?,op_dt=current_timestamp "
                + " WHERE appl_no=? ";
        ps = tmgr.prepareStatement(sql);
        ps.setString(pos++, to_dobj.getOwner_name());
        ps.setInt(pos++, to_dobj.getOwner_cd());
        ps.setInt(pos++, to_dobj.getOwner_ctg());
        ps.setString(pos++, to_dobj.getF_name());
        ps.setString(pos++, to_dobj.getC_add1());
        ps.setString(pos++, to_dobj.getC_add2());
        ps.setString(pos++, to_dobj.getC_add3());
        ps.setString(pos++, to_dobj.getC_state());
        ps.setInt(pos++, to_dobj.getC_district());
        ps.setInt(pos++, to_dobj.getC_pincode());
        ps.setString(pos++, to_dobj.getP_add1());
        ps.setString(pos++, to_dobj.getP_add2());
        ps.setString(pos++, to_dobj.getP_add3());
        ps.setString(pos++, to_dobj.getP_state());
        ps.setInt(pos++, to_dobj.getP_district());
        ps.setInt(pos++, to_dobj.getP_pincode());
        ps.setDate(pos++, new java.sql.Date(to_dobj.getSale_dt().getTime()));
        ps.setDate(pos++, new java.sql.Date(to_dobj.getTransfer_dt().getTime()));
        ps.setInt(pos++, to_dobj.getSale_amt());
        ps.setString(pos++, to_dobj.getReason());
        ps.setString(pos++, to_dobj.getGarage_add());
        ps.setString(pos++, to_dobj.getAppl_no());
        ps.executeUpdate();
    } // end of updateTo

    public static void insertIntoTo(TransactionManager tmgr, ToDobj to_dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        int owner_sr = 0;
        long owner_from = 0;
        int pos = 1;
        try {

            owner_sr = getOwnerSr(tmgr, to_dobj.getRegn_no());
            owner_from = getOwnerFrom(tmgr, to_dobj.getRegn_no(), TableList.VH_TO);

            sql = "INSERT INTO " + TableList.VA_TO + "(state_cd,off_cd,appl_no, regn_no, pur_cd,"
                    + "    owner_sr, owner_name, owner_cd, owner_ctg,"
                    + "    f_name, c_add1, c_add2, c_add3, c_district,"
                    + "    c_pincode, c_state, p_add1, p_add2, p_add3, p_district, p_pincode, p_state ,"
                    + "    owner_from, sale_dt, transfer_dt, sale_amt, reason,"
                    + "    garage_add, op_dt)"
                    + "    VALUES (?,?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
                    + "            ?, ?, ?, ?, ?, ?, current_timestamp)";

            ps = tmgr.prepareStatement(sql);
            ps.setString(pos++, to_dobj.getStateCode());
            ps.setInt(pos++, to_dobj.getOffCode());
            ps.setString(pos++, to_dobj.getAppl_no());
            ps.setString(pos++, to_dobj.getRegn_no());
            ps.setInt(pos++, to_dobj.getPur_cd());
            ps.setInt(pos++, owner_sr + 1);
            ps.setString(pos++, to_dobj.getOwner_name());
            ps.setInt(pos++, to_dobj.getOwner_cd());
            ps.setInt(pos++, to_dobj.getOwner_ctg());
            ps.setString(pos++, to_dobj.getF_name());
            ps.setString(pos++, to_dobj.getC_add1());
            ps.setString(pos++, to_dobj.getC_add2());
            ps.setString(pos++, to_dobj.getC_add3());
            ps.setInt(pos++, to_dobj.getC_district());
            ps.setInt(pos++, to_dobj.getC_pincode());
            ps.setString(pos++, to_dobj.getC_state());
            ps.setString(pos++, to_dobj.getP_add1());
            ps.setString(pos++, to_dobj.getP_add2());
            ps.setString(pos++, to_dobj.getP_add3());
            ps.setInt(pos++, to_dobj.getP_district());
            ps.setInt(pos++, to_dobj.getP_pincode());
            ps.setString(pos++, to_dobj.getP_state());
            ps.setDate(pos++, new java.sql.Date(owner_from));// this is reginstration date of particular application appl need to validate before update
            ps.setDate(pos++, new java.sql.Date(to_dobj.getSale_dt().getTime()));
            ps.setDate(pos++, new java.sql.Date(to_dobj.getTransfer_dt().getTime()));
            ps.setInt(pos++, to_dobj.getSale_amt());
            ps.setString(pos++, to_dobj.getReason());
            ps.setString(pos++, to_dobj.getGarage_add());
            ps.executeUpdate();

        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    } // end of insertIntoTo

    public static void insertUpdateTO(TransactionManager tmgr, String appl_no, String regn_no, ToDobj to_dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "SELECT regn_no FROM " + TableList.VA_TO + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) { //if any record is exist then update otherwise insert it
                insertIntoToHistory(tmgr, appl_no);
                updateTo(tmgr, to_dobj);
            } else {
                insertIntoTo(tmgr, to_dobj);
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    } // end of insertUpdateTO

    public void makeChangeTO(ToDobj to_dobj, OwnerIdentificationDobj owner_identification, String changedata, String selectedFancyRetnetion, Date applDate) throws Exception {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("makeChangeTO");
            insertUpdateTO(tmgr, to_dobj.getAppl_no(), to_dobj.getRegn_no(), to_dobj);
            OwnerIdentificationImpl.insertUpdateOwnerIdentification(tmgr, owner_identification);
            ComparisonBeanImpl.updateChangedData(to_dobj.getAppl_no(), changedata, tmgr);
            if (selectedFancyRetnetion != null && selectedFancyRetnetion.equalsIgnoreCase("YES")) {
                insertIntoVaSurrenderRetention(tmgr, to_dobj, applDate);
            } else if (selectedFancyRetnetion != null && selectedFancyRetnetion.equalsIgnoreCase("NO")) {
                insertIntoVhaSurrenderRetention(tmgr, to_dobj.getAppl_no());
                ServerUtil.deleteFromTable(tmgr, null, to_dobj.getAppl_no(), TableList.VA_SURRENDER_RETENTION);
            }
            tmgr.commit();
        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }
    }

    public boolean isFancyNo(String regnNo) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        boolean fancy = false;
        try {
            if (regnNo != null) {
                regnNo = regnNo.toUpperCase();
            }
            tmgr = new TransactionManager("isFancyNo");
            String sql = "SELECT regn_no FROM " + TableList.VT_ADVANCE_REGN_NO + " WHERE regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                fancy = true;
            }

        } catch (Exception ex) {
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
        return fancy;
    }

    public void insertIntoVaSurrenderRetention(TransactionManager tmgr, ToDobj toDobj, Date applDate) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "SELECT regn_no FROM " + TableList.VA_SURRENDER_RETENTION + " WHERE appl_no = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, toDobj.getAppl_no());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();

        if (rs.next()) {
            //if any record exist then against appl_no then no insert operation would perform.
        } else {
            sql = "INSERT INTO " + TableList.VA_SURRENDER_RETENTION
                    + " (state_cd,off_cd,appl_no, regn_no, file_no, reason, dt_of_app, approved_by, op_dt)"
                    + " VALUES (?, ?,?, ?, ?, ?, ?, ?, current_timestamp)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            ps.setString(3, toDobj.getAppl_no());
            ps.setString(4, toDobj.getRegn_no());
            ps.setNull(5, java.sql.Types.VARCHAR);//file no is not known so currently putting it as NULL
            ps.setString(6, toDobj.getReason());
            ps.setDate(7, new java.sql.Date(applDate.getTime()));
            ps.setString(8, Util.getEmpCode());
            ps.executeUpdate();
        }
    }

    public void insertIntoVhaSurrenderRetention(TransactionManager tmgr, String applNo) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VHA_SURRENDER_RETENTION
                + " SELECT current_timestamp as moved_on, ? as moved_by,* FROM " + TableList.VA_SURRENDER_RETENTION
                + " WHERE appl_no =?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, applNo);
        ps.executeUpdate();
    }

    public String insertIntoVtSurrenderRetention(TransactionManager tmgr, Owner_dobj ownerDobj, String reason, String applNo, Date applDate, int pur_cd, boolean isTowithRetentionOrConv) throws VahanException, Exception {
        PreparedStatement ps = null;
        String sql = null;
        String newRegnNo = null;
        String rcptNo = null;
        String oldRegnNo = null;
        int pos = 1;

        if (Util.getUserStateCode().equalsIgnoreCase("SK") && (ownerDobj.getOwner_cd() == TableConstants.VEH_TYPE_GOVT || ownerDobj.getOwner_cd() == TableConstants.VEH_TYPE_STATE_GOVT || ownerDobj.getOwner_cd() == TableConstants.VEH_TYPE_GOVT_UNDERTAKING)) {

            sql = "select a.rcpt_no  from " + TableList.VT_FEE + " a , " + TableList.VP_APPL_RCPT_MAPPING + "  b where  a.rcpt_no=b.rcpt_no and a.state_cd=b.state_cd and a.off_cd=b.off_cd and b.appl_no=? and pur_cd in(5,9,10) and a.state_cd = ? and a.off_cd= ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                rcptNo = rs.getString("rcpt_no");
            }

            if (rcptNo == null || rcptNo.trim().equalsIgnoreCase("")) {
                throw new VahanException("Can't Approve as Fees is not Paid for Fancy No Surrender Retention.");
            }
        } else {
            sql = "select a.rcpt_no  from " + TableList.VT_FEE + " a , " + TableList.VP_APPL_RCPT_MAPPING + "  b where  a.rcpt_no=b.rcpt_no and a.state_cd=b.state_cd and a.off_cd=b.off_cd and b.appl_no=? and pur_cd=? and a.state_cd = ? and a.off_cd= ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setInt(2, TableConstants.SWAPPING_REGN_PUR_CD);
            ps.setString(3, Util.getUserStateCode());
            ps.setInt(4, Util.getSelectedSeat().getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                rcptNo = rs.getString("rcpt_no");
            }

            if (rcptNo == null || rcptNo.trim().equalsIgnoreCase("")) {
                throw new VahanException("Can't Approve as Fees is not Paid for Fancy No Surrender Retention.");
            }
        }
        if (isTowithRetentionOrConv) {
            newRegnNo = ownerDobj.getRegn_no();
            //Get Old regn no details from vh_re_assgin(Conversion)
            oldRegnNo = getOldRegnNoDetails(tmgr, applNo);
        } else if (pur_cd == TableConstants.VM_MAST_VEHICLE_SCRAPE) {
            ServerUtil serverUtil = new ServerUtil();
            newRegnNo = serverUtil.getRegnNoForScrappedVehicle(ownerDobj.getRegn_no());

        } else {
            NewVehicleNo newVehicleNo = new NewVehicleNo();
            newRegnNo = newVehicleNo.generateAssignNewRegistrationNo(Util.getSelectedSeat().getOff_cd(),
                    Util.getSelectedSeat().getAction_cd(), applNo, ownerDobj.getRegn_no(), 1, null, null, tmgr);
            if (newRegnNo == null || newRegnNo.trim().equalsIgnoreCase("")) {
                throw new VahanException("Can't Approved because of New Vehicle No Generation is Failed.");
            }
        }

        sql = "INSERT INTO " + TableList.VT_SURRENDER_RETENTION
                + "  ( state_cd, off_cd, regn_appl_no, old_regn_no, new_regn_no, owner_name, f_name, c_add1, c_add2, "
                + "    c_add3, c_district, c_pincode, c_state, vh_class, rcpt_no, mobile_no, surr_dt, "
                + "    file_no, reason, dt_of_app, approved_by, op_dt) "
                + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        ps = tmgr.prepareStatement(sql);

        ps.setString(pos++, Util.getUserStateCode());
        ps.setInt(pos++, Util.getSelectedSeat().getOff_cd());
        ps.setNull(pos++, java.sql.Types.VARCHAR);
        if (isTowithRetentionOrConv && !CommonUtils.isNullOrBlank(oldRegnNo)) {
            ps.setString(pos++, oldRegnNo);
        } else {
            ps.setString(pos++, ownerDobj.getRegn_no());
        }
        ps.setString(pos++, newRegnNo);
        ps.setString(pos++, ownerDobj.getOwner_name());
        ps.setString(pos++, ownerDobj.getF_name());
        ps.setString(pos++, ownerDobj.getC_add1());
        ps.setString(pos++, ownerDobj.getC_add2());
        ps.setString(pos++, ownerDobj.getC_add3());
        ps.setInt(pos++, ownerDobj.getC_district());
        ps.setInt(pos++, ownerDobj.getC_pincode());
        ps.setString(pos++, ownerDobj.getC_state());
        ps.setInt(pos++, ownerDobj.getVh_class());
        ps.setString(pos++, rcptNo);
        if (ownerDobj.getOwner_identity().getMobile_no() != null) {
            ps.setLong(pos++, ownerDobj.getOwner_identity().getMobile_no());
        } else {
            ps.setLong(pos++, 0);
        }
        ps.setDate(pos++, new java.sql.Date(new Date().getTime()));//surrender date is the approval date
        ps.setNull(pos++, java.sql.Types.VARCHAR);//file no is not known so currently putting it as NULL
        ps.setString(pos++, reason);
        ps.setDate(pos++, new java.sql.Date(applDate.getTime()));
        ps.setString(pos++, Util.getEmpCode());
        ps.setTimestamp(pos++, ServerUtil.getSystemDateInPostgres());
        ps.executeUpdate();

        if (!isTowithRetentionOrConv) {
            SwappingRegnImpl.updateTablesForRetention(tmgr, ownerDobj.getRegn_no(), newRegnNo);
        }

        if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NOC) {
            sql = "UPDATE " + TableList.VT_NOC + " SET regn_no=? WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, newRegnNo);
            ps.setString(2, applNo);
            ps.executeUpdate();
        }
        //HSRP
        ServerUtil.verifyInsertNewRegHsrpDetail(applNo, newRegnNo, TableConstants.HSRP_NEW_BOTH_SIDE, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), tmgr);
        return newRegnNo;
    }

    public void insertIntoVtSurrenderRetention(Owner_dobj ownerDobj, String applNo, String reason, Date applDate, String newRegnNo, String oldRegnNo, TransactionManager tmgr) throws VahanException, Exception {
        PreparedStatement ps = null;
        String sql = null;
        String rcptNo = "";
        int pos = 1;
        RowSet rs = null;
        try {
            if (Util.getUserStateCode().equalsIgnoreCase("SK") && (ownerDobj.getOwner_cd() == TableConstants.VEH_TYPE_GOVT || ownerDobj.getOwner_cd() == TableConstants.VEH_TYPE_STATE_GOVT || ownerDobj.getOwner_cd() == TableConstants.VEH_TYPE_GOVT_UNDERTAKING)) {
                sql = "select a.rcpt_no  from " + TableList.VT_FEE + " a , " + TableList.VP_APPL_RCPT_MAPPING + "  b where  a.rcpt_no=b.rcpt_no and a.state_cd=b.state_cd and a.off_cd=b.off_cd and b.appl_no=? and pur_cd in(5,9,10) and a.state_cd = ? and a.off_cd= ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, applNo);
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    rcptNo = rs.getString("rcpt_no");
                }
                if (rcptNo == null || rcptNo.trim().equalsIgnoreCase("")) {
                    throw new VahanException("Can't Approve as Fees is not Paid for Fancy No Surrender Retention.");
                }
            } else {
                sql = "select a.rcpt_no  from " + TableList.VT_FEE + " a , " + TableList.VP_APPL_RCPT_MAPPING + "  b where  a.rcpt_no=b.rcpt_no and a.state_cd=b.state_cd and a.off_cd=b.off_cd and b.appl_no=? and pur_cd=? and a.state_cd = ? and a.off_cd= ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, applNo);
                ps.setInt(2, TableConstants.SWAPPING_REGN_PUR_CD);
                ps.setString(3, Util.getUserStateCode());
                ps.setInt(4, Util.getSelectedSeat().getOff_cd());
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    rcptNo = rs.getString("rcpt_no");
                }
                if (rcptNo == null || rcptNo.trim().equalsIgnoreCase("")) {
                    throw new VahanException("Can't Approve as Fees is not Paid for Fancy No Surrender Retention.");
                }
            }

            sql = "INSERT INTO " + TableList.VT_SURRENDER_RETENTION
                    + "  ( state_cd, off_cd, regn_appl_no, old_regn_no, new_regn_no, owner_name, f_name, c_add1, c_add2, "
                    + "    c_add3, c_district, c_pincode, c_state, vh_class, rcpt_no, mobile_no, surr_dt, "
                    + "    file_no, reason, dt_of_app, approved_by, op_dt) "
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            ps = tmgr.prepareStatement(sql);

            ps.setString(pos++, Util.getUserStateCode());
            ps.setInt(pos++, Util.getSelectedSeat().getOff_cd());
            ps.setNull(pos++, java.sql.Types.VARCHAR);
            if (!CommonUtils.isNullOrBlank(oldRegnNo)) {
                ps.setString(pos++, oldRegnNo);
            } else {
                ps.setString(pos++, ownerDobj.getRegn_no());
            }
            ps.setString(pos++, newRegnNo);
            ps.setString(pos++, ownerDobj.getOwner_name());
            ps.setString(pos++, ownerDobj.getF_name());
            ps.setString(pos++, ownerDobj.getC_add1());
            ps.setString(pos++, ownerDobj.getC_add2());
            ps.setString(pos++, ownerDobj.getC_add3());
            ps.setInt(pos++, ownerDobj.getC_district());
            ps.setInt(pos++, ownerDobj.getC_pincode());
            ps.setString(pos++, ownerDobj.getC_state());
            ps.setInt(pos++, ownerDobj.getVh_class());
            if (!CommonUtils.isNullOrBlank(rcptNo)) {
                ps.setString(pos++, rcptNo);
            } else {
                ps.setString(pos++, null);
            }
            if (ownerDobj.getOwner_identity().getMobile_no() != null) {
                ps.setLong(pos++, ownerDobj.getOwner_identity().getMobile_no());
            } else {
                ps.setLong(pos++, 0);
            }
            ps.setDate(pos++, new java.sql.Date(new Date().getTime()));//surrender date is the approval date
            ps.setNull(pos++, java.sql.Types.VARCHAR);//file no is not known so currently putting it as NULL
            ps.setString(pos++, reason);
            ps.setDate(pos++, new java.sql.Date(applDate.getTime()));
            ps.setString(pos++, Util.getEmpCode());
            ps.setTimestamp(pos++, ServerUtil.getSystemDateInPostgres());
            ps.executeUpdate();

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public boolean isSurrenderRetention(String applNo) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        boolean isSurRetn = false;
        try {
            tmgr = new TransactionManager("isSurrenderRetention");
            String sql = "SELECT regn_no FROM " + TableList.VA_SURRENDER_RETENTION + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                isSurRetn = true;
            }

        } catch (Exception ex) {
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
        return isSurRetn;
    }

    public void updateRetentionRegNoDetails(String applNo, String regnNo, TransactionManager tmgr) throws SQLException, VahanException {
        String sql = "Update " + TableList.VT_SURRENDER_RETENTION
                + " set regn_appl_no=? where old_regn_no=? and (regn_appl_no is null or length(regn_appl_no) = 0)";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, applNo);
        ps.setString(2, regnNo);
        int count = ps.executeUpdate();
        if (count == 0) {
            throw new VahanException("This Retained Registration Number already used by another application.");
        }
    }

    public boolean isHRSurrenderRetention(String applNo) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        boolean isSurRetn = false;
        RowSet rs = null;
        String sql = null;
        try {
            tmgr = new TransactionManager("isSurrenderRetention");
            sql = "SELECT regn_no FROM " + TableList.VA_SURRENDER_RETENTION + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);

            rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {
                isSurRetn = true;
            }
            if (!isSurRetn) {
                sql = "select regn_no"
                        + " from " + TableList.VA_RETENTION + " WHERE appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, applNo);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    isSurRetn = true;
                }
            }

        } catch (Exception ex) {
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
        return isSurRetn;
    }

    private String getOldRegnNoDetails(TransactionManager tmgr, String applNo) throws SQLException {
        String oldRegnNo = null;
        PreparedStatement ps;
        RowSet rs;
        String query = "Select old_regn_no from " + TableList.VH_RE_ASSIGN + " where appl_no = ?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, applNo);
        rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            oldRegnNo = rs.getString("old_regn_no");
        }
        return oldRegnNo;
    }

    public int getOwner_Cd(String regno, String state_cd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String sql = null;
        int owner_cd = 0;
        try {
            tmgr = new TransactionManager("isSurrenderRetention");
            sql = "SELECT owner_cd FROM " + TableList.VH_TO + " WHERE regn_no=? and state_cd=? order by moved_on limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regno);
            ps.setString(2, state_cd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                owner_cd = rs.getInt("owner_cd");
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
        return owner_cd;
    }

    public void insertVaStopDupRegnNoGen(String applNo, String oldRegnNo, String newRegnNo, int purCd, TransactionManager tmgr) throws SQLException, VahanException {
        int pos = 1;
        ServerUtil.deleteFromTable(tmgr, "", applNo, TableList.VA_STOP_DUP_REGN_NO_GENERATION);
        String sql = "insert into " + TableList.VA_STOP_DUP_REGN_NO_GENERATION
                + "  ( state_cd, off_cd, appl_no, old_regn_no, new_regn_no,pur_cd,op_dt )"
                + "    VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(pos++, Util.getUserStateCode());
        ps.setInt(pos++, Util.getSelectedSeat().getOff_cd());
        ps.setString(pos++, applNo);
        ps.setString(pos++, oldRegnNo);
        if (!CommonUtils.isNullOrBlank(newRegnNo)) {
            ps.setString(pos++, newRegnNo);
        } else {
            ps.setString(pos++, oldRegnNo);
        }
        ps.setInt(pos++, purCd);
        ps.setTimestamp(pos++, ServerUtil.getSystemDateInPostgres());
        ps.executeUpdate();

    }

    public boolean isNumGenrateWithTOReRegCon(String applNo, int purCd) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        boolean isNumGenrateWithTOReRegCon = false;
        RowSet rs = null;
        String sql = null;
        try {
            tmgr = new TransactionManager("isSurrenderRetention");
            sql = "SELECT * FROM " + TableList.VA_STOP_DUP_REGN_NO_GENERATION + " WHERE appl_no=? and pur_cd  in(?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setInt(2, purCd);

            rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {
                isNumGenrateWithTOReRegCon = true;
            }

        } catch (Exception ex) {
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
        return isNumGenrateWithTOReRegCon;
    }

    public static void insertIntoVhaVaStopDupRegnNoHistory(TransactionManager tmgr, String appl_no, Appl_Details_Dobj sessionVariable) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            //inserting data into VhaNumGenPermitHistory from VaNumGenPermitHistory
            sql = "INSERT INTO " + TableList.VHA_STOP_DUP_REGN_NO_GENERATION + " (\n"
                    + "            moved_on, moved_by, state_cd, off_cd, appl_no, old_regn_no, new_regn_no,pur_cd,op_dt)  \n"
                    + "SELECT current_timestamp as moved_on, ? as moved_by, state_cd, off_cd, appl_no, old_regn_no, new_regn_no,pur_cd, op_dt  \n"
                    + "  FROM " + TableList.VA_STOP_DUP_REGN_NO_GENERATION + " where appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, sessionVariable.getCurrentEmpCd());
            ps.setString(2, appl_no);
            ps.executeUpdate();

            ServerUtil.deleteFromTable(tmgr, "", appl_no, TableList.VA_STOP_DUP_REGN_NO_GENERATION);
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public String statusList(String applNo, String regnNo, String stateCd) throws VahanException {
        String pur_cd_d = "";
        try {

            List<Status_dobj> statusList = ServerUtil.applicationStatus(regnNo, applNo, stateCd);
            if (!statusList.isEmpty() && statusList.size() > 1) {
                for (int i = 0; i < statusList.size(); i++) {
                    pur_cd_d = pur_cd_d + "," + statusList.get(i).getPur_cd();
                }
                pur_cd_d = pur_cd_d + ",";
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return pur_cd_d;
    }
}//end of ToImpl class
