/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.State;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.ImportedVehicleDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.NocDobj;
import nic.vahan.form.dobj.OtherStateVehDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.RetroFittingDetailsDobj;
import nic.vahan.form.impl.AxleImpl;
import nic.vahan.form.impl.HpaImpl;
import nic.vahan.form.impl.ImportedVehicleImpl;
import nic.vahan.form.impl.InsImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.RetroFittingDetailsImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import nic.vahan.services.insurance.InsuranceDetailService;
import nic.vahan.services.insurance.dobj.InsuranceInfoDobj;
import org.apache.log4j.Logger;

/**
 * @author Kartikey Singh
 */
public class OtherStateVehImplementation {

    private static final Logger LOGGER = Logger.getLogger(OtherStateVehImplementation.class);

    public Owner_dobj set_Owner_appl_db_to_dobj(String regn_no) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        Owner_dobj owner_dobj = null;
        RowSet rs = null;
        RowSet rs1 = null;
        String query = TableConstants.EMPTY_STRING;
        NocDobj nocVerifiedData = null;
        OwnerImpl ownerImplObj = new OwnerImpl();
        if (regn_no != null) {
            regn_no = regn_no.toUpperCase();
        }
        try {
            tmgr = new TransactionManager("set_Owner_appl_db_to_dobj");
            //vt_owner main without statecd and offcd where regn_no and status != null
            Owner_dobj ownerDobj = ownerImplObj.set_Owner_appl_db_to_dobj(regn_no, TableConstants.EMPTY_STRING, TableConstants.EMPTY_STRING, TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE);
            Date vahan4StartDate = null;
            if (ownerDobj != null) {
                vahan4StartDate = ServerUtil.getVahan4StartDate(ownerDobj.getState_cd(), ownerDobj.getOff_cd());
            }
            if (ownerDobj != null && vahan4StartDate != null && DateUtils.compareDates(vahan4StartDate, new Date()) <= 1 && !ownerDobj.getStatus().equalsIgnoreCase("N")) {
                throw new VahanException("NOC is not issued for this vehicle.");
            }

            if (regn_no != null && !regn_no.equalsIgnoreCase(TableConstants.EMPTY_STRING)) {
                query = "select * from  " + TableList.VT_NOC
                        + " where regn_no=? order by noc_dt desc limit 1";

                ps = tmgr.prepareStatement(query);
                ps.setString(1, regn_no);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    // code for noc endorsement
                    query = "select *,state.descr as current_state_name,dist.descr as current_district_name from  " + TableList.VT_OWNER + " owner "
                            + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                            + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                            + " where owner.regn_no=? and owner.state_cd = ? and owner.off_cd = ? and owner.status = 'N' ";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, regn_no);
                    ps.setString(2, rs.getString("state_cd"));
                    ps.setInt(3, rs.getInt("off_cd"));
                    rs1 = tmgr.fetchDetachedRowSet();
                    owner_dobj = ownerImplObj.fillFrom_VT_OwnerDobj_with_state_off_cd(rs1);
                    if (owner_dobj != null) {
                        if (rs.getString("state_to") == null) {
                            throw new VahanException("NOC Data Inconsistent");
                        }
                        vahan4StartDate = ServerUtil.getVahan4StartDate(rs.getString("state_to"), rs.getInt("off_to"));
                        if (vahan4StartDate == null) {
                            if (owner_dobj.getState_cd().equals(Util.getUserStateCode()) && Util.getSelectedSeat().getOff_cd() == owner_dobj.getOff_cd() || rs.getString("state_to").equals(Util.getUserStateCode()) && Util.getSelectedSeat().getOff_cd() == rs.getInt("off_to")) {
                                throw new VahanException(TableConstants.NOC_ENDORSEMENT);
                            } else if (!rs.getString("state_to").equals(Util.getUserStateCode()) || Util.getSelectedSeat().getOff_cd() != rs.getInt("off_to")) {
                                nocVerifiedData = ServerUtil.getNocVerifiedData(regn_no, null);
                                if (nocVerifiedData == null) {
                                    throw new VahanException(TableConstants.NOC_VERIFICATION);
                                }
                            }
                        }
                        if (nocVerifiedData == null) {
                            if (!rs.getString("state_to").equals(Util.getUserStateCode()) || Util.getSelectedSeat().getOff_cd() != rs.getInt("off_to")) {
                                State stateTo = MasterTableFiller.state.get(rs.getString("state_to"));
                                String stLabel = stateTo == null ? "Unknown State" : stateTo.getStateDescr();
                                String offCdLabel = "Unknown Off Code";
                                if (stateTo != null) {
                                    stLabel = stateTo.getStateDescr();
                                    List<SelectItem> listOff = stateTo.getOffice();
                                    for (SelectItem off : listOff) {
                                        if (off.getValue().equals(rs.getInt("off_to"))) {
                                            offCdLabel = off.getLabel();
                                            break;
                                        }
                                    }
                                }
                                throw new VahanException("Vehicle has NOC issued to state : " + stLabel + " Office: " + offCdLabel);
                            }
                        }
                    } else {
                        NocDobj dobj = ServerUtil.getNocEndorsementData(regn_no, null);
                        if (dobj != null && dobj.getState_cd() != null && dobj.getOff_cd() != 0 && (!dobj.getState_cd().equals(Util.getUserStateCode()) || Util.getSelectedSeat().getOff_cd() != dobj.getOff_cd())) {
                            State stateCd = MasterTableFiller.state.get(dobj.getState_cd());
                            String offCdLabel = null;
                            if (stateCd != null) {
                                List<SelectItem> listOff = stateCd.getOffice();
                                for (SelectItem off : listOff) {
                                    if (off.getValue().toString().equals(String.valueOf(dobj.getOff_cd()))) {
                                        offCdLabel = off.getLabel();
                                        break;
                                    }
                                }
                            }
                            throw new VahanException("Vehicle is Endoresed at " + stateCd.getStateDescr() + " " + offCdLabel + ", Vehicle can only be registered at " + stateCd.getStateDescr() + " " + offCdLabel + "");
                        }
                        String othStateDetailsExist = this.getOtherStateDetails(regn_no);
                        if (!othStateDetailsExist.isEmpty()) {
                            throw new VahanException(othStateDetailsExist);
                        }

                        String reassignDetailsExist = ServerUtil.getVehReassignData(regn_no);
                        if (!reassignDetailsExist.isEmpty()) {
                            throw new VahanException(reassignDetailsExist);
                        }
                    }
                }


                if (owner_dobj != null) {
                    InsDobj insDobj = null;
                    //Insurance
                    //start of getting insurance details from service
                    InsuranceDetailService detailService = new InsuranceDetailService();
                    insDobj = detailService.getInsuranceDetailsByService(regn_no, owner_dobj.getState_cd(), owner_dobj.getOff_cd());
                    if (insDobj == null) {
                        insDobj = InsImpl.set_ins_dtls_db_to_dobj(owner_dobj.getRegn_no(), null, owner_dobj.getState_cd(), owner_dobj.getOff_cd());
                    }
                    if (insDobj != null) {
                        owner_dobj.setInsDobj(insDobj);
                    }

                    //Axel Details
                    AxleDetailsDobj axleDobj = AxleImpl.setAxleVehDetails_db_to_dobj(null, owner_dobj.getRegn_no(), owner_dobj.getState_cd(), owner_dobj.getOff_cd());
                    owner_dobj.setAxleDobj(axleDobj);

                    //RetroFitting Details
                    RetroFittingDetailsDobj retroDobj = RetroFittingDetailsImpl.setCngDetails_db_to_dobj_VT(owner_dobj.getRegn_no(), owner_dobj.getState_cd(), owner_dobj.getOff_cd());
                    owner_dobj.setCng_dobj(retroDobj);

                    //HPA Details
                    HpaImpl hpaImpl = new HpaImpl();
                    List<HpaDobj> listHpaDobj = new ArrayList<>();
                    HpaDobj hpaDobj = hpaImpl.set_HPA_appl_db_to_dobj(null, owner_dobj.getRegn_no(), 0, owner_dobj.getState_cd(), owner_dobj.getOff_cd());
                    listHpaDobj.add(hpaDobj);
                    owner_dobj.setListHpaDobj(listHpaDobj);

                    //Imported Vehicle Details
                    ImportedVehicleDobj impDobj = ImportedVehicleImpl.setImpVehDetails_db_to_dobj(null, owner_dobj.getRegn_no(), owner_dobj.getState_cd(), owner_dobj.getOff_cd());
                    owner_dobj.setImp_Dobj(impDobj);
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in fetching Data.");
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in fetching Data.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return owner_dobj;
    }

    public OtherStateVehDobj setOtherVehicleDetailsToDobj(String appl_no) throws VahanException {

        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        OtherStateVehDobj dobj = null;

        try {
            tmgr = new TransactionManager("setOtherVehicleDetailsToDobj");

            sql = "SELECT appl_no, old_regn_no, old_off_cd, old_state_cd, ncrb_ref, confirm_ref,"
                    + "       noc_dt, noc_no, state_entry_dt "
                    + "  FROM " + TableList.VA_OTHER_STATE_VEH + " where appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                dobj = new OtherStateVehDobj();

                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setOldRegnNo(rs.getString("old_regn_no"));
                dobj.setOldOffCD(rs.getInt("old_off_cd"));
                dobj.setOldStateCD(rs.getString("old_state_cd"));
                dobj.setNcrbRef(rs.getString("ncrb_ref"));
                dobj.setConfirmRef(rs.getString("confirm_ref"));
                dobj.setNocDate(rs.getTimestamp("noc_dt"));
                dobj.setNocNo(rs.getString("noc_no"));
                dobj.setStateEntryDate(rs.getTimestamp("state_entry_dt"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Fetching of Other State Vehicle Details");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return dobj;
    }

    public OtherStateVehDobj setVTOtherVehicleDetailsToDobj(String regnNo) throws VahanException {

        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        OtherStateVehDobj dobj = null;

        try {

            tmgr = new TransactionManager("setOtherVehicleDetailsToDobj");
            sql = "SELECT *  FROM " + TableList.VT_OTHER_STATE_VEH + " where new_regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                dobj = new OtherStateVehDobj();
                dobj.setStateCD(rs.getString("state_cd"));
                dobj.setOffCD(rs.getInt("off_cd"));
                dobj.setNewRegnNo(rs.getString("new_regn_no"));
                dobj.setStateEntryDate(rs.getDate("entry_dt"));
                dobj.setOldRegnNo(rs.getString("old_regn_no"));
                //dobj.setOld_rgst_auth(rs.getString("old_rgst_auth"));
                dobj.setOldOffCD(rs.getInt("old_off_cd"));
                dobj.setOldStateCD(rs.getString("old_state_cd"));
                dobj.setNcrbRef(rs.getString("ncrb_ref"));
                dobj.setConfirmRef(rs.getString("confirm_ref"));
                dobj.setNocDate(rs.getDate("noc_dt"));
                dobj.setNocNo(rs.getString("noc_no"));

            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Fetching of Other State Vehicle Details");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

        return dobj;
    }

    public void insertIntoOtherStateVehHistory(TransactionManager tmgr, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VHA_OTHER_STATE_VEH
                + " SELECT current_timestamp as moved_on, ? moved_by,state_cd, off_cd,appl_no, old_regn_no, old_off_cd, old_state_cd, ncrb_ref, confirm_ref, "
                + "       noc_dt, noc_no, state_entry_dt,op_dt "
                + "  FROM " + TableList.VA_OTHER_STATE_VEH + " WHERE appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();
    } // end of insertIntoOtherStateVehHistory

    /**
 * @author Kartikey Singh
 */
    public void insertIntoOtherStateVehHistory(TransactionManager tmgr, String appl_no, String empCode) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VHA_OTHER_STATE_VEH
                + " SELECT current_timestamp as moved_on, ? moved_by,state_cd, off_cd,appl_no, old_regn_no, old_off_cd, old_state_cd, ncrb_ref, confirm_ref, "
                + "       noc_dt, noc_no, state_entry_dt,op_dt "
                + "  FROM " + TableList.VA_OTHER_STATE_VEH + " WHERE appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, empCode);
        ps.setString(2, appl_no);
        ps.executeUpdate();
    } // end of insertIntoOtherStateVehHistory

    public void updateOtherStateVeh(TransactionManager tmgr, OtherStateVehDobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        int pos = 1;

        sql = "UPDATE " + TableList.VA_OTHER_STATE_VEH
                + " SET state_cd = ?,off_cd = ?,old_regn_no=?, old_off_cd=?, old_state_cd=?, ncrb_ref=?,"
                + "       confirm_ref=?, noc_dt=?, noc_no=?, state_entry_dt=?,op_dt = current_timestamp "
                + " WHERE appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(pos++, Util.getUserStateCode());
        ps.setInt(pos++, Util.getSelectedSeat().getOff_cd());
        ps.setString(pos++, dobj.getOldRegnNo());
        ps.setInt(pos++, dobj.getOldOffCD());
        ps.setString(pos++, dobj.getOldStateCD());
        ps.setString(pos++, dobj.getNcrbRef());
        ps.setString(pos++, dobj.getConfirmRef());
        if (dobj.getNocDate() == null) {
            ps.setDate(pos++, new java.sql.Date(new Date().getTime()));
        } else {
            ps.setDate(pos++, new java.sql.Date(dobj.getNocDate().getTime()));
        }
        ps.setString(pos++, dobj.getNocNo());
        if (dobj.getStateEntryDate() == null) {
            ps.setDate(pos++, new java.sql.Date(new Date().getTime()));
        } else {
            ps.setDate(pos++, new java.sql.Date(dobj.getStateEntryDate().getTime()));
        }
        ps.setString(pos++, dobj.getApplNo());
        ps.executeUpdate();
    } // end of updateOtherStateVeh

    /**
 * @author Kartikey Singh
 */
    public void updateOtherStateVeh(TransactionManager tmgr, OtherStateVehDobj dobj, String userStateCode,
            int offCode) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        int pos = 1;

        sql = "UPDATE " + TableList.VA_OTHER_STATE_VEH
                + " SET state_cd = ?,off_cd = ?,old_regn_no=?, old_off_cd=?, old_state_cd=?, ncrb_ref=?,"
                + "       confirm_ref=?, noc_dt=?, noc_no=?, state_entry_dt=?,op_dt = current_timestamp "
                + " WHERE appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(pos++, userStateCode);
        ps.setInt(pos++, offCode);
        ps.setString(pos++, dobj.getOldRegnNo());
        ps.setInt(pos++, dobj.getOldOffCD());
        ps.setString(pos++, dobj.getOldStateCD());
        ps.setString(pos++, dobj.getNcrbRef());
        ps.setString(pos++, dobj.getConfirmRef());
        if (dobj.getNocDate() == null) {
            ps.setDate(pos++, new java.sql.Date(new Date().getTime()));
        } else {
            ps.setDate(pos++, new java.sql.Date(dobj.getNocDate().getTime()));
        }
        ps.setString(pos++, dobj.getNocNo());
        if (dobj.getStateEntryDate() == null) {
            ps.setDate(pos++, new java.sql.Date(new Date().getTime()));
        } else {
            ps.setDate(pos++, new java.sql.Date(dobj.getStateEntryDate().getTime()));
        }
        ps.setString(pos++, dobj.getApplNo());
        ps.executeUpdate();
    } // end of updateOtherStateVeh

    public void insertIntoVaOtherStateVeh(TransactionManager tmgr, OtherStateVehDobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        int pos = 1;

        sql = "INSERT INTO " + TableList.VA_OTHER_STATE_VEH
                + " (state_cd,off_cd,appl_no, old_regn_no, old_off_cd, old_state_cd, ncrb_ref, confirm_ref,"
                + "            noc_dt, noc_no, state_entry_dt,op_dt)"
                + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,current_timestamp)";

        ps = tmgr.prepareStatement(sql);
        ps.setString(pos++, Util.getUserStateCode());
        ps.setInt(pos++, Util.getSelectedSeat().getOff_cd());
        ps.setString(pos++, dobj.getApplNo());
        ps.setString(pos++, dobj.getOldRegnNo());
        ps.setInt(pos++, dobj.getOldOffCD() == null ? 0 : dobj.getOldOffCD());
        ps.setString(pos++, dobj.getOldStateCD() == null ? TableConstants.EMPTY_STRING : dobj.getOldStateCD());
        ps.setString(pos++, dobj.getNcrbRef() == null ? TableConstants.EMPTY_STRING : dobj.getNcrbRef());
        ps.setString(pos++, dobj.getConfirmRef() == null ? TableConstants.EMPTY_STRING : dobj.getConfirmRef());
        if (dobj.getNocDate() == null) {
            ps.setDate(pos++, new java.sql.Date(new Date().getTime()));
        } else {
            ps.setDate(pos++, new java.sql.Date(dobj.getNocDate().getTime()));
        }
        ps.setString(pos++, dobj.getNocNo() == null ? TableConstants.EMPTY_STRING : dobj.getNocNo());
        if (dobj.getStateEntryDate() == null) {
            ps.setDate(pos++, new java.sql.Date(new Date().getTime()));
        } else {
            ps.setDate(pos++, new java.sql.Date(dobj.getStateEntryDate().getTime()));
        }
        ps.executeUpdate();

    } // end of insertIntoVaOtherStateVeh

    /*
     * @author: Kartikey Singh
     */
    public void insertIntoVaOtherStateVeh(TransactionManager tmgr, OtherStateVehDobj dobj,
            String userStateCode, int offCode) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        int pos = 1;

        sql = "INSERT INTO " + TableList.VA_OTHER_STATE_VEH
                + " (state_cd,off_cd,appl_no, old_regn_no, old_off_cd, old_state_cd, ncrb_ref, confirm_ref,"
                + "            noc_dt, noc_no, state_entry_dt,op_dt)"
                + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,current_timestamp)";

        ps = tmgr.prepareStatement(sql);
        ps.setString(pos++, userStateCode);
        ps.setInt(pos++, offCode);
        ps.setString(pos++, dobj.getApplNo());
        ps.setString(pos++, dobj.getOldRegnNo());
        ps.setInt(pos++, dobj.getOldOffCD() == null ? 0 : dobj.getOldOffCD());
        ps.setString(pos++, dobj.getOldStateCD() == null ? TableConstants.EMPTY_STRING : dobj.getOldStateCD());
        ps.setString(pos++, dobj.getNcrbRef() == null ? TableConstants.EMPTY_STRING : dobj.getNcrbRef());
        ps.setString(pos++, dobj.getConfirmRef() == null ? TableConstants.EMPTY_STRING : dobj.getConfirmRef());
        if (dobj.getNocDate() == null) {
            ps.setDate(pos++, new java.sql.Date(new Date().getTime()));
        } else {
            ps.setDate(pos++, new java.sql.Date(dobj.getNocDate().getTime()));
        }
        ps.setString(pos++, dobj.getNocNo() == null ? TableConstants.EMPTY_STRING : dobj.getNocNo());
        if (dobj.getStateEntryDate() == null) {
            ps.setDate(pos++, new java.sql.Date(new Date().getTime()));
        } else {
            ps.setDate(pos++, new java.sql.Date(dobj.getStateEntryDate().getTime()));
        }
        ps.executeUpdate();

    } // end of insertIntoVaOtherStateVeh

    public void insertIntoVtOtherStateVeh(TransactionManager tmgr, OtherStateVehDobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        int pos = 1;

        sql = "INSERT INTO " + TableList.VT_OTHER_STATE_VEH
                + "  ( state_cd, off_cd, new_regn_no, entry_dt, old_regn_no, old_rgst_auth,"
                + "    old_off_cd, old_state_cd, ncrb_ref, confirm_ref, noc_dt, noc_no,"
                + "    op_dt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";

        ps = tmgr.prepareStatement(sql);
        ps.setString(pos++, dobj.getStateCD());
        ps.setInt(pos++, dobj.getOffCD());
        ps.setString(pos++, dobj.getNewRegnNo());
        ps.setDate(pos++, new java.sql.Date(dobj.getStateEntryDate().getTime()));
        ps.setString(pos++, dobj.getOldRegnNo());
        ps.setString(pos++, TableConstants.EMPTY_STRING);
        ps.setInt(pos++, dobj.getOldOffCD());
        ps.setString(pos++, dobj.getOldStateCD());
        ps.setString(pos++, dobj.getNcrbRef());
        ps.setString(pos++, dobj.getConfirmRef());
        ps.setDate(pos++, new java.sql.Date(dobj.getNocDate().getTime()));
        ps.setString(pos++, dobj.getNocNo());
        ps.executeUpdate();

    } // end of insertIntoVaOtherStateVeh

    public void insertUpdateOtherStateVeh(TransactionManager tmgr, OtherStateVehDobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "SELECT * FROM " + TableList.VA_OTHER_STATE_VEH + " WHERE appl_no = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getApplNo());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();

        if (rs.next()) { //if any record is exist then update otherwise insert it
            insertIntoOtherStateVehHistory(tmgr, dobj.getApplNo());
            updateOtherStateVeh(tmgr, dobj);
        } else {
            insertIntoVaOtherStateVeh(tmgr, dobj);
        }
    } // end of insertUpdateOtherStateVeh

    /**
     * @author Kartikey Singh
     */
    public void insertUpdateOtherStateVeh(TransactionManager tmgr, OtherStateVehDobj dobj,
            String empCode, String userStateCode, int offCode) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "SELECT * FROM " + TableList.VA_OTHER_STATE_VEH + " WHERE appl_no = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getApplNo());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();

        if (rs.next()) { //if any record is exist then update otherwise insert it
            insertIntoOtherStateVehHistory(tmgr, dobj.getApplNo(), empCode);
            updateOtherStateVeh(tmgr, dobj, userStateCode, offCode);
        } else {
            insertIntoVaOtherStateVeh(tmgr, dobj, userStateCode, offCode);
        }
    } // end of insertUpdateOtherStateVeh

    public OtherStateVehDobj setNocDtailsToOtherStateVeh(NocDobj dobj) {
        OtherStateVehDobj vehDobj = null;

        if (dobj != null) {
            vehDobj = new OtherStateVehDobj();
            vehDobj.setOldRegnNo(dobj.getRegn_no());
            vehDobj.setNocNo(dobj.getNoc_no());
            vehDobj.setNocDate(dobj.getNoc_dt());
            vehDobj.setOldStateCD(dobj.getState_cd());
            vehDobj.setOldOffCD(dobj.getOff_cd());
            vehDobj.setNcrbRef(dobj.getNcrb_ref());
        }

        return vehDobj;
    }

    public void insertIntoOtherStateVehHistoryVH(TransactionManager tmgr, String regNo, String applNo, String stateCd, int offCd) throws SQLException {
        PreparedStatement ps = null;
        int pos = 1;

        String sql = "INSERT INTO " + TableList.VH_OTHER_STATE_VEH
                + " SELECT state_cd, off_cd, new_regn_no, entry_dt, old_regn_no, old_rgst_auth,"
                + "       old_off_cd, old_state_cd, ncrb_ref, confirm_ref, noc_dt, noc_no,"
                + "       op_dt, ? as appl_no, current_timestamp as moved_on, ? as moved_by "
                + "  FROM " + TableList.VT_OTHER_STATE_VEH + " WHERE old_regn_no = ? and state_cd=? and off_cd=?";

        ps = tmgr.prepareStatement(sql);

        ps.setString(pos++, applNo);
        ps.setString(pos++, Util.getEmpCode());
        ps.setString(pos++, regNo);
        ps.setString(pos++, stateCd);
        ps.setInt(pos++, offCd);
        ps.executeUpdate();
    }
    
    /**
     * @author Kartikey Singh
     */
    public void insertIntoOtherStateVehHistoryVH(TransactionManager tmgr, String regNo, String applNo, String stateCd, int offCd, String empCode) throws SQLException {
        PreparedStatement ps = null;
        int pos = 1;

        String sql = "INSERT INTO " + TableList.VH_OTHER_STATE_VEH
                + " SELECT state_cd, off_cd, new_regn_no, entry_dt, old_regn_no, old_rgst_auth,"
                + "       old_off_cd, old_state_cd, ncrb_ref, confirm_ref, noc_dt, noc_no,"
                + "       op_dt, ? as appl_no, current_timestamp as moved_on, ? as moved_by "
                + "  FROM " + TableList.VT_OTHER_STATE_VEH + " WHERE old_regn_no = ? and state_cd=? and off_cd=?";

        ps = tmgr.prepareStatement(sql);

        ps.setString(pos++, applNo);
        ps.setString(pos++, empCode);
        ps.setString(pos++, regNo);
        ps.setString(pos++, stateCd);
        ps.setInt(pos++, offCd);
        ps.executeUpdate();
    }

    public void deleteFromVtOtherStateVeh(TransactionManager tmgr, String regnNo, String stateCd, int offCd) throws SQLException {
        PreparedStatement ps = null;
        String sql = "DELETE FROM " + TableList.VT_OTHER_STATE_VEH + " WHERE old_regn_no=? and state_cd=? and off_cd=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, regnNo);
        ps.setString(2, stateCd);
        ps.setInt(3, offCd);
        ps.executeUpdate();
    }

    /*
     *Petrol Vehicle registered at other state on 1st Jan 2013
     *OTT for 15 years paid at other state and  tax validity is 31st Dec 2027. Entry date  in Manipur : 14-Nov-2017
     *Vehicle is more than 2 years old. Hence no tax will be charged till 31st Dec 2027 Hence, in Manipur
     *Road Tax Amount = 0, Tax validity = 31st Dec 2027.
     * 
     * Diesel Vehicle registered at other state on 1st Jan 2013
     * OTT for 10 years paid at other state and  tax validity is 31st Dec 2022. Entry date  in Manipur : 14-Nov-2017
     * Vehicle is more than 2 years old. Hence no tax will be charged till 31st Dec 2022
     * Hence, in Manipur Road Tax Amount = 0 Tax validity = 31st Dec 2022.
     */
    public void insertIntoOtherStateVehTaxHistoryVH(TransactionManager tmgr, String applNo, String regNo, Date taxupto, OtherStateVehDobj dobj) throws SQLException {

        String vtTaxExemption = "INSERT INTO " + TableList.VT_TAX_EXEM + "(\n"
                + "            state_cd, off_cd, regn_no, exem_fr, exem_to, \n"
                + "            exem_by,perm_no, perm_dt, remark, user_cd, op_dt)\n"
                + "    VALUES (?, ?, ?, ?, ?, \n"
                + "            ?,?, CURRENT_TIMESTAMP,?, ?, CURRENT_TIMESTAMP);";

        PreparedStatement pstmtVtTaxExem = tmgr.prepareStatement(vtTaxExemption);
        int tax = 1;
        pstmtVtTaxExem.setString(tax++, Util.getUserStateCode());
        pstmtVtTaxExem.setInt(tax++, Util.getSelectedSeat().getOff_cd());
        pstmtVtTaxExem.setString(tax++, regNo);
        pstmtVtTaxExem.setDate(tax++, new java.sql.Date(dobj.getStateEntryDate().getTime()));
        pstmtVtTaxExem.setDate(tax++, new java.sql.Date(taxupto.getTime()));
        pstmtVtTaxExem.setString(tax++, Util.getUserId());
        pstmtVtTaxExem.setString(tax++, "DTO ORDER");
        pstmtVtTaxExem.setString(tax++, "TAX EXEMPTION");
        pstmtVtTaxExem.setString(tax++, Util.getEmpCode());
        pstmtVtTaxExem.executeUpdate();
    }
    
    /**
     * @author Kartikey SIngh
     * Petrol Vehicle registered at other state on 1st Jan 2013
     * OTT for 15 years paid at other state and  tax validity is 31st Dec 2027. Entry date  in Manipur : 14-Nov-2017
     * Vehicle is more than 2 years old. Hence no tax will be charged till 31st Dec 2027 Hence, in Manipur
     * Road Tax Amount = 0, Tax validity = 31st Dec 2027.
     * 
     * Diesel Vehicle registered at other state on 1st Jan 2013
     * OTT for 10 years paid at other state and  tax validity is 31st Dec 2022. Entry date  in Manipur : 14-Nov-2017
     * Vehicle is more than 2 years old. Hence no tax will be charged till 31st Dec 2022
     * Hence, in Manipur Road Tax Amount = 0 Tax validity = 31st Dec 2022.
     */
    public void insertIntoOtherStateVehTaxHistoryVH(TransactionManager tmgr, String applNo, String regNo, Date taxupto, OtherStateVehDobj dobj,
            String userStateCode, int selectedOffCode, String userId, String empCode) throws SQLException {

        String vtTaxExemption = "INSERT INTO " + TableList.VT_TAX_EXEM + "(\n"
                + "            state_cd, off_cd, regn_no, exem_fr, exem_to, \n"
                + "            exem_by,perm_no, perm_dt, remark, user_cd, op_dt)\n"
                + "    VALUES (?, ?, ?, ?, ?, \n"
                + "            ?,?, CURRENT_TIMESTAMP,?, ?, CURRENT_TIMESTAMP);";

        PreparedStatement pstmtVtTaxExem = tmgr.prepareStatement(vtTaxExemption);
        int tax = 1;
        pstmtVtTaxExem.setString(tax++, userStateCode);
        pstmtVtTaxExem.setInt(tax++, selectedOffCode);
        pstmtVtTaxExem.setString(tax++, regNo);
        pstmtVtTaxExem.setDate(tax++, new java.sql.Date(dobj.getStateEntryDate().getTime()));
        pstmtVtTaxExem.setDate(tax++, new java.sql.Date(taxupto.getTime()));
        pstmtVtTaxExem.setString(tax++, userId);
        pstmtVtTaxExem.setString(tax++, "DTO ORDER");
        pstmtVtTaxExem.setString(tax++, "TAX EXEMPTION");
        pstmtVtTaxExem.setString(tax++, empCode);
        pstmtVtTaxExem.executeUpdate();
    }

    /**
     * This method will return application no based on parameter provided to
     * this method and It is supposed to put parameter as old registration no
     *
     * @param oldRegNo is a parameter which is old registration no
     * @return application no against old registration no which is parameter of
     * this function
     * @throws VahanException
     *
     */
    public String getApplNoOfOtherState(String oldRegNo) throws VahanException {

        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        String applNo = null;

        try {
            tmgr = new TransactionManager("validateOtherState");
            sql = "SELECT appl_no FROM " + TableList.VA_OTHER_STATE_VEH + " where old_regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, oldRegNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) // found
            {
                applNo = rs.getString("appl_no");
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Validation when Fetching of Other State Vehicle Details");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return applNo;
    }

    public String getOtherStateDetails(String oldRegNo) throws VahanException {

        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        String message = TableConstants.EMPTY_STRING;

        try {
            tmgr = new TransactionManager("validateOtherState");
            sql = "  select old_regn_no,new_regn_no,b.descr AS state_name,c.off_name\n"
                    + " from " + TableList.VT_OTHER_STATE_VEH + "  a \n"
                    + " LEFT JOIN tm_state b ON b.state_code = a.state_cd::bpchar\n"
                    + "	LEFT JOIN tm_office c ON c.off_cd = a.off_cd AND c.state_cd = a.state_cd::bpchar\n"
                    + " where old_regn_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, oldRegNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) // found
            {
                message = "This Registration no " + rs.getString("old_regn_no") + " registered with this " + rs.getString("new_regn_no") + " in " + rs.getString("state_name") + " " + rs.getString("off_name") + "  ";
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Validation when Fetching of Other State Vehicle Details");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return message;
    }

    public OtherStateVehDobj setNocVerificationDetailsToOtherStateVeh(NocDobj dobj) {
        OtherStateVehDobj vehDobj = null;

        if (dobj != null) {
            vehDobj = new OtherStateVehDobj();
            vehDobj.setOldRegnNo(dobj.getRegn_no());
            vehDobj.setNocNo(dobj.getNoc_no());
            vehDobj.setNocDate(dobj.getNoc_dt());
            vehDobj.setOldStateCD(dobj.getState_from());
            vehDobj.setOldOffCD(dobj.getOff_from());
            vehDobj.setNcrbRef(dobj.getNcrb_ref());
        }

        return vehDobj;
    }

    public String getCurrentAddressInString(String regNo, String stateCd, int offCd) {
        String str = null;
        String sql = "select c_add1||case when c_add2 is not null and c_add2 !='' then ','||c_add2 else ''  end"
                + "|| case when c_add3 is not null and c_add3 !='' then ','||c_add3 else '' end "
                + "||',District-'||c_district_name||','||c_state_name||',Pin-'||c_pincode currentAddress "
                + " from  " + TableList.VIEW_VV_OWNER
                + " where regn_no=? and state_cd=? and off_cd=? ";
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getCurrenAddressInString");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regNo);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                str = rs.getString("currentAddress");
            }
        } catch (Exception e) {
            LOGGER.error(e);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex);
                }
            }
        }

        return str;
    }
}
