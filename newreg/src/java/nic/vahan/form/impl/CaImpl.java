/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.CaDobj;
import nic.vahan.form.dobj.InspectionDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.common.Appl_Details_Dobj;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import org.apache.log4j.Logger;

public class CaImpl {

    private static final Logger LOGGER = Logger.getLogger(CaImpl.class);

    public CaDobj set_CA_appl_db_to_dobj(String appl_no) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        CaDobj ca_dobj = null;
        try {
            tmgr = new TransactionManager("set_CA_appl_db_to_dobj");
            ps = tmgr.prepareStatement("select state_cd,off_cd,appl_no,regn_no,from_dt,"
                    + " c_add1,c_add2,c_add3,c_state,c_pincode,c_district,"
                    + " p_add1,p_add2,p_add3,p_state,p_district,p_pincode "
                    + " from " + TableList.VA_CA + " where appl_no=?");
            ps.setString(1, appl_no);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                ca_dobj = new CaDobj();
                ca_dobj.setState_cd(rs.getString("state_cd"));
                ca_dobj.setOff_cd(rs.getInt("off_cd"));
                ca_dobj.setAppl_no(rs.getString("appl_no"));
                ca_dobj.setRegn_no(rs.getString("regn_no"));
                ca_dobj.setFrom_dt(rs.getDate("from_dt"));
                ca_dobj.setC_add1(rs.getString("c_add1"));
                ca_dobj.setC_add2(rs.getString("c_add2"));
                ca_dobj.setC_add3(rs.getString("c_add3"));
                ca_dobj.setC_state(rs.getString("c_state"));
                ca_dobj.setC_district(rs.getInt("c_district"));
                ca_dobj.setC_pincode(rs.getInt("c_pincode"));
                ca_dobj.setP_add1(rs.getString("p_add1"));
                ca_dobj.setP_add2(rs.getString("p_add2"));
                ca_dobj.setP_add3(rs.getString("p_add3"));
                ca_dobj.setP_state(rs.getString("p_state"));
                ca_dobj.setP_district(rs.getInt("p_district"));
                ca_dobj.setP_pincode(rs.getInt("p_pincode"));
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during getting details of Change of Address on application No-" + appl_no + ",Please contact to the system administrator.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return ca_dobj;
    }

    public void update_CA_Status(CaDobj ca_dobj, CaDobj ca_dobj_prv, Status_dobj status_dobj, String changedData, InspectionDobj inspDobj, InspectionDobj inspDobjPrev, Appl_Details_Dobj applDetailsDobj) throws Exception {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        try {

            if (applDetailsDobj == null
                    || applDetailsDobj.getCurrent_off_cd() == 0
                    || applDetailsDobj.getCurrent_state_cd() == null) {
                throw new VahanException("Something went wrong, Please try again later...");
            }

            tmgr = new TransactionManager("update_CA_Status");
            //=================WEB SERVICES FOR NEXTSTAGE START=========//
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);

            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_ENTRY
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_VERIFICATION
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_APPROVAL
                    || applDetailsDobj.getCurrent_action_cd() == TableConstants.INSPECTION_FOR_CHANGE_OF_ADDRESS) {

                if ((changedData != null && !changedData.equals("")) || ca_dobj_prv == null || (inspDobjPrev == null && applDetailsDobj.getCurrent_action_cd() == TableConstants.INSPECTION_FOR_CHANGE_OF_ADDRESS)) {

                    insertUpdateCA(tmgr, ca_dobj.getAppl_no(), ca_dobj.getRegn_no(), ca_dobj);//when there is change by user or Entry Scrutiny

                    //for inspection for OTHER OFFICE case
                    if (inspDobj != null
                            && inspDobj.getInsp_dt() != null
                            && applDetailsDobj.getCurrent_action_cd() == TableConstants.INSPECTION_FOR_CHANGE_OF_ADDRESS) {
                        FitnessImpl fitnessImpl = new FitnessImpl();
                        fitnessImpl.insertOrUpdateInspection(tmgr, inspDobj);
                    }
                }
            }

            if (TableConstants.STATUS_HOLD.equals(status_dobj.getStatus()) && applDetailsDobj.getOwnerDobj() != null && applDetailsDobj.getOwnerDobj().getOwner_identity() != null) {
                ServerUtil.sendSMSForHoldApplication(applDetailsDobj.getOwnerDobj().getOwner_identity().getMobile_no(), status_dobj.getOffice_remark(), applDetailsDobj.getAppl_no());
            }

            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_APPROVAL
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                sql = "insert into " + TableList.VH_CA + " select state_cd,off_cd,"
                        + " ? as appl_no, regn_no, regn_dt as from_dt,current_timestamp as to_dt,"
                        + " c_add1, c_add2, c_add3, c_district, c_pincode, c_state,"
                        + " p_add1, p_add2, p_add3, p_district, p_pincode, p_state,"
                        + " current_timestamp as moved_on, ? as moved_by "
                        + " FROM " + TableList.VT_OWNER
                        + " where regn_no=? and state_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, ca_dobj.getAppl_no());
                ps.setString(2, applDetailsDobj.getCurrentEmpCd());
                ps.setString(3, ca_dobj.getRegn_no());
                ps.setString(4, applDetailsDobj.getCurrent_state_cd());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                //if current office and vehicle registration office is not same
                if (applDetailsDobj.getCurrent_state_cd().equalsIgnoreCase(applDetailsDobj.getOwnerDobj().getState_cd()) && applDetailsDobj.getCurrent_off_cd() != applDetailsDobj.getOwnerDobj().getOff_cd()) {
                    NewImpl newImpl = new NewImpl();
                    newImpl.updateOffCodeInRegnTables(tmgr, ca_dobj.getAppl_no(), ca_dobj.getRegn_no(), applDetailsDobj.getCurrent_state_cd(), applDetailsDobj.getCurrent_off_cd(), applDetailsDobj.getCurrentEmpCd());
                    //for inspection
                    if (inspDobj != null
                            && inspDobj.getInsp_dt() != null) {
                        FitnessImpl fitImpl = new FitnessImpl();
                        fitImpl.insertOrUpdateInspection(tmgr, inspDobj);
                        fitImpl.insertVtInspection(tmgr, inspDobj);
                        ServerUtil.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_INSPECTION);
                    }
                }

                sql = " update " + TableList.VT_OWNER + " set  "
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
                        + " op_dt=current_timestamp"
                        + " where regn_no=? and state_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, ca_dobj.getC_add1());
                ps.setString(2, ca_dobj.getC_add2());
                ps.setString(3, ca_dobj.getC_add3());
                ps.setString(4, ca_dobj.getC_state());
                ps.setInt(5, ca_dobj.getC_district());
                ps.setInt(6, ca_dobj.getC_pincode());
                ps.setString(7, ca_dobj.getP_add1());
                ps.setString(8, ca_dobj.getP_add2());
                ps.setString(9, ca_dobj.getP_add3());
                ps.setString(10, ca_dobj.getP_state());
                ps.setInt(11, ca_dobj.getP_district());
                ps.setInt(12, ca_dobj.getP_pincode());
                ps.setString(13, ca_dobj.getRegn_no());
                ps.setString(14, applDetailsDobj.getCurrent_state_cd());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                sql = "INSERT INTO " + TableList.VHA_CA
                        + " SELECT current_timestamp + interval '1 second' as moved_on,? as moved_by,"
                        + "        state_cd,off_cd,appl_no, regn_no, from_dt,"
                        + "        c_add1, c_add2, c_add3, c_district, c_pincode,c_state,"
                        + "        p_add1, p_add2, p_add3, p_district, p_pincode,p_state,"
                        + "        op_dt "
                        + "  FROM  " + TableList.VA_CA
                        + " WHERE  appl_no=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, applDetailsDobj.getCurrentEmpCd());
                ps.setString(2, ca_dobj.getAppl_no());
                ps.executeUpdate();

                sql = "DELETE FROM " + TableList.VA_CA + " where appl_no = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, ca_dobj.getAppl_no());
                ps.executeUpdate();

                //for updating the status of application when it is approved start
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                //for updating the status of application when it is approved end

                //SmartCard Or Print
                ServerUtil.VerifyInsertSmartCardPrintDetail(ca_dobj.getAppl_no(), ca_dobj.getRegn_no(),
                        applDetailsDobj.getCurrent_state_cd(), applDetailsDobj.getCurrent_off_cd(), status_dobj.getPur_cd(), tmgr);
            }

            insertIntoVhaChangedData(tmgr, ca_dobj.getAppl_no(), changedData);//for saving the data into table those are changed by the user

            ServerUtil.fileFlow(tmgr, status_dobj); //for updateing va_status and vha status for new role,seat for new emp

            tmgr.commit();//Commiting data here....

        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }

    }//End of CA_Update_Status()

    public static void insertIntoCAHistory(TransactionManager tmgr, String appl_no) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;

        //inserting data into vha_ca from va_ca
        sql = "INSERT INTO " + TableList.VHA_CA
                + " SELECT current_timestamp as moved_on,? as moved_by,"
                + "        state_cd,off_cd,appl_no, regn_no, from_dt,"
                + "        c_add1, c_add2, c_add3, c_district, c_pincode,c_state,"
                + "        p_add1, p_add2, p_add3, p_district, p_pincode,p_state,"
                + "        op_dt "
                + "  FROM  " + TableList.VA_CA
                + " WHERE  appl_no=? ";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();
    } // end of insertIntoCAHistory

    public static void updateCA(TransactionManager tmgr, CaDobj ca_dobj) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;

        //updation of va_ca
        sql = " update " + TableList.VA_CA
                + " set from_dt=?,"
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
                + " p_pincode=?,op_dt=current_timestamp"
                + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setDate(1, new java.sql.Date(ca_dobj.getFrom_dt().getTime()));
        ps.setString(2, ca_dobj.getC_add1());
        ps.setString(3, ca_dobj.getC_add2());
        ps.setString(4, ca_dobj.getC_add3());
        ps.setString(5, ca_dobj.getC_state());
        ps.setInt(6, ca_dobj.getC_district());
        ps.setInt(7, ca_dobj.getC_pincode());
        ps.setString(8, ca_dobj.getP_add1());
        ps.setString(9, ca_dobj.getP_add2());
        ps.setString(10, ca_dobj.getP_add3());
        ps.setString(11, ca_dobj.getP_state());
        ps.setInt(12, ca_dobj.getP_district());
        ps.setInt(13, ca_dobj.getP_pincode());
        ps.setString(14, ca_dobj.getAppl_no());
        ps.executeUpdate();
    } // end of updateCA

    public static void insertIntoCA(TransactionManager tmgr, CaDobj ca_dobj) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;
        int pos = 1;

        sql = "INSERT INTO " + TableList.VA_CA + "(state_cd, off_cd, appl_no, regn_no, from_dt, c_add1, c_add2, c_add3,"
                + "            c_state, c_district, c_pincode, p_add1, p_add2, p_add3, p_state,"
                + "            p_district, p_pincode, op_dt)"
                + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";

        ps = tmgr.prepareStatement(sql);
        ps.setString(pos++, ca_dobj.getState_cd());
        ps.setInt(pos++, ca_dobj.getOff_cd());
        ps.setString(pos++, ca_dobj.getAppl_no());
        ps.setString(pos++, ca_dobj.getRegn_no());
        ps.setDate(pos++, new java.sql.Date(ca_dobj.getFrom_dt().getTime()));
        ps.setString(pos++, ca_dobj.getC_add1());
        ps.setString(pos++, ca_dobj.getC_add2());
        ps.setString(pos++, ca_dobj.getC_add3());
        ps.setString(pos++, ca_dobj.getC_state());
        ps.setInt(pos++, ca_dobj.getC_district());
        ps.setInt(pos++, ca_dobj.getC_pincode());
        ps.setString(pos++, ca_dobj.getP_add1());
        ps.setString(pos++, ca_dobj.getP_add2());
        ps.setString(pos++, ca_dobj.getP_add3());
        ps.setString(pos++, ca_dobj.getP_state());
        ps.setInt(pos++, ca_dobj.getP_district());
        ps.setInt(pos++, ca_dobj.getP_pincode());
        ps.executeUpdate();
    } // end of insertIntoCA

    public static void makeChangeCA(CaDobj ca_dobj, String changedata, InspectionDobj inspDobj, int actionCode) throws SQLException, Exception {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("makeChangeCA");
            insertUpdateCA(tmgr, ca_dobj.getAppl_no(), ca_dobj.getRegn_no(), ca_dobj);

            if (inspDobj != null && inspDobj.getInsp_dt() != null
                    && actionCode == TableConstants.INSPECTION_FOR_CHANGE_OF_ADDRESS) {
                FitnessImpl fitnessImpl = new FitnessImpl();
                fitnessImpl.insertOrUpdateInspection(tmgr, inspDobj);
            }

            ComparisonBeanImpl.updateChangedData(ca_dobj.getAppl_no(), changedata, tmgr);
            tmgr.commit();
        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }
    }

    public static void insertUpdateCA(TransactionManager tmgr, String appl_no, String regn_no, CaDobj ca_dobj) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;

        sql = "SELECT regn_no FROM " + TableList.VA_CA + " where appl_no = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, appl_no);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();

        if (rs.next()) { //if any record is exist then update otherwise insert it
            insertIntoCAHistory(tmgr, appl_no);
            updateCA(tmgr, ca_dobj);
        } else {
            insertIntoCA(tmgr, ca_dobj);
        }
    } // end of insertUpdateCA
}//End of class