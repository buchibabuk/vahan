/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.AdvertisementOnVehicleDobj;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.common.Appl_Details_Dobj;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import org.apache.log4j.Logger;

/**
 *
 * @author acer
 */
public class AdvertisementOnVehicleImpl {

    private static final Logger LOGGER = Logger.getLogger(ToImpl.class);

    public AdvertisementOnVehicleDobj set_VAAdvertisementOnVehicle_appl_to_dobj(String appl_no) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        AdvertisementOnVehicleDobj adv_dobj = null;

        try {

            tmgr = new TransactionManager("set_VAAdvertisementOnVehicle_appl_to_dobj");

            sql = "SELECT state_cd,off_cd,appl_no,regn_no,"
                    + " from_dt,upto_dt,op_dt "
                    + " FROM " + TableList.VA_ADVERTISEMENT + " where appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                adv_dobj = new AdvertisementOnVehicleDobj();

                adv_dobj.setStateCode(rs.getString("state_cd"));
                adv_dobj.setOffCode(rs.getInt("off_cd"));
                adv_dobj.setAppl_no(rs.getString("appl_no"));
                adv_dobj.setRegn_no(rs.getString("regn_no"));
                adv_dobj.setFrom_dt(rs.getDate("from_dt"));
                adv_dobj.setUpto_dt(rs.getDate("upto_dt"));
                adv_dobj.setOp_dt(rs.getString("op_dt"));
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
        return adv_dobj;

    }

    public void update_Advertisement_Status(AdvertisementOnVehicleDobj adv_Dobj, AdvertisementOnVehicleDobj adv_dobj_prv, Status_dobj status_dobj, String changedData, Owner_dobj ownerDetail, Appl_Details_Dobj applDetailsDobj) throws VahanException {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;

        try {

            tmgr = new TransactionManager("update_Advertisement_Status");
            //=================WEB SERVICES FOR NEXTSTAGE START=========//
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);

            if (applDetailsDobj.getCurrent_action_cd()== TableConstants.ADVERTISEMENT_ON_VEHICLE_ENTRY
                    || applDetailsDobj.getCurrent_action_cd() == TableConstants.ADVERTISEMENT_ON_VEHICLE_VERIFICATION) {

                if ((changedData != null && !changedData.equals("")) || adv_dobj_prv == null) {
                    this.insertUpdateAdvertisement(tmgr, adv_Dobj);//when there is change by user or Entry Scrutiny
                }

            }

            String mobileNo = getMobileNo(adv_Dobj.getRegn_no());
            String mobileSms = null;
            if (applDetailsDobj.getCurrent_action_cd() == TableConstants.ADVERTISEMENT_ON_VEHICLE_APPROVAL
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                sql = "select regn_no from " + TableList.VT_ADVERTISEMENT + " where regn_no=? and state_cd=? and off_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, adv_Dobj.getRegn_no());
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, Util.getUserOffCode());
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    this.insertIntoVTAdvertisementHistory(tmgr, adv_Dobj.getRegn_no());
                    sql = "DELETE FROM " + TableList.VT_ADVERTISEMENT + " where regn_no = ? and state_cd=? and off_cd=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, adv_Dobj.getRegn_no());
                    ps.setString(2, Util.getUserStateCode());
                    ps.setInt(3, Util.getUserOffCode());
                    ps.executeUpdate();
                }

                sql = "insert into " + TableList.VT_ADVERTISEMENT
                        + " SELECT state_cd, off_cd, appl_no, regn_no, from_dt, upto_dt, current_timestamp "
                        + " FROM " + TableList.VA_ADVERTISEMENT
                        + " WHERE appl_no=? and state_cd = ? and off_cd = ? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, adv_Dobj.getAppl_no());
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, Util.getUserOffCode());
                ps.executeUpdate();

                this.insertIntoVAAdvertisementHistory(tmgr, adv_Dobj.getAppl_no());

                sql = "DELETE FROM " + TableList.VA_ADVERTISEMENT + " where appl_no = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, adv_Dobj.getAppl_no());
                ps.executeUpdate();

                //for updating the status of application when it is approved start
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                //for updating the status of application when it is approved end

                if (status_dobj.getEntry_status().equalsIgnoreCase(TableConstants.STATUS_APPROVED)) {
                    mobileSms = "Your application for the permission of advertisement on your vehicle <" + adv_Dobj.getRegn_no() + "> is Approved. You may take approval Print from Citizen portal <https://vahan.parivahan.gov.in/vahanservice/vahan/ui/statevalidation/homepage.xhtml>";
                }
                ServerUtil.sendSMS(mobileNo, mobileSms);
            }

            if (applDetailsDobj.getCurrent_action_cd() == TableConstants.ADVERTISEMENT_ON_VEHICLE_APPROVAL
                    && status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                mobileSms = "Your application for the permission of advertisement on your vehicle <" + adv_Dobj.getRegn_no() + "> is Rejected. Reason : <" + ServerUtil.getReasonsForHolding(adv_Dobj.getAppl_no()) + ">. May please do the needful and apply again";
                ServerUtil.sendSMS(mobileNo, mobileSms);
            }
            insertIntoVhaChangedData(tmgr, adv_Dobj.getRegn_no(), changedData);//for saving the data into table those are changed by the user

            ServerUtil.fileFlow(tmgr, status_dobj); //for updateing va_status and vha status for new role,seat for new emp

            tmgr.commit();//Commiting data here....

        } catch (VahanException ve) {
            throw new VahanException(TableConstants.SomthingWentWrong);
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

    public String getMobileNo(String regnNo) {
        String mobileNo = "";
        TransactionManager tmgr = null;
        String sql = "select mobile_no from vt_owner_identification where regn_no=? and state_cd=? and off_cd=?";
        try {
            tmgr = new TransactionManager("getMobileNo");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getUserOffCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                mobileNo = rs.getString("mobile_no");
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
        return mobileNo;
    }

    public void makeChangeAdvertisementOnVehicle(AdvertisementOnVehicleDobj adv_Dobj, OwnerIdentificationDobj owner_identification, String changedata, Date applDate) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("makeChangeAdvertisementOnVehicle");
            this.insertUpdateAdvertisement(tmgr, adv_Dobj);
            ComparisonBeanImpl.updateChangedData(adv_Dobj.getAppl_no(), changedata, tmgr);
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

    public void insertUpdateAdvertisement(TransactionManager tmgr, AdvertisementOnVehicleDobj adv_Dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "SELECT regn_no FROM " + TableList.VA_ADVERTISEMENT + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, adv_Dobj.getAppl_no());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) { //if any record is exist then update otherwise insert it
                this.insertIntoVAAdvertisementHistory(tmgr, adv_Dobj.getAppl_no());
                this.updateAdvertisement(tmgr, adv_Dobj);
            } else {
                this.insertIntoAdvertisement(tmgr, adv_Dobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public void insertIntoVAAdvertisementHistory(TransactionManager tmgr, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        sql = "INSERT INTO " + TableList.VHA_ADVERTISEMENT
                + " SELECT current_timestamp as moved_on, ? moved_by,"
                + " state_cd,off_cd,appl_no,regn_no,"
                + " from_dt,upto_dt,op_dt "
                + " FROM " + TableList.VA_ADVERTISEMENT + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    public void insertIntoVTAdvertisementHistory(TransactionManager tmgr, String regnNo) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        sql = "INSERT INTO " + TableList.VH_ADVERTISEMENT
                + " SELECT "
                + " state_cd,off_cd,appl_no,regn_no,"
                + " from_dt,upto_dt,op_dt, current_timestamp as moved_on, ? moved_by"
                + " FROM " + TableList.VT_ADVERTISEMENT + " where regn_no=? and state_cd=? and off_cd=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, regnNo);
        ps.setString(3, Util.getUserStateCode());
        ps.setInt(4, Util.getUserOffCode());
        ps.executeUpdate();
    }

    public void updateAdvertisement(TransactionManager tmgr, AdvertisementOnVehicleDobj adv_Dobj) throws Exception {
        PreparedStatement ps = null;
        String sql = null;
        int j = 1;

        //updation of va_tax_exem
        sql = " update " + TableList.VA_ADVERTISEMENT
                + " set  from_dt=?,"
                + " upto_dt=?,"
                + " op_dt=current_timestamp"
                + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setDate(j++, new java.sql.Date(adv_Dobj.getFrom_dt().getTime()));
        ps.setDate(j++, new java.sql.Date(adv_Dobj.getUpto_dt().getTime()));
        ps.setString(j++, adv_Dobj.getAppl_no());
        ps.executeUpdate();
    }

    public void insertIntoAdvertisement(TransactionManager tmgr, AdvertisementOnVehicleDobj adv_Dobj) throws Exception {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VA_ADVERTISEMENT + "(\n"
                + "             state_cd, off_cd, appl_no, regn_no, \n"
                + " from_dt,upto_dt,op_dt "
                + "    VALUES (?, ?, ?, ?, \n"
                + "            ?, ?, current_timestamp)";

        ps = tmgr.prepareStatement(sql);
        int j = 1;
        ps.setString(j++, Util.getUserStateCode());
        ps.setInt(j++, Util.getUserSeatOffCode());
        ps.setString(j++, adv_Dobj.getAppl_no());
        ps.setString(j++, adv_Dobj.getRegn_no());
        ps.setDate(j++, new java.sql.Date(adv_Dobj.getFrom_dt().getTime()));
        ps.setDate(j++, new java.sql.Date(adv_Dobj.getUpto_dt().getTime()));
        ps.executeUpdate();
    }
}
