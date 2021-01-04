/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.dobj.BlackListedVehicleDobj;
import nic.vahan.form.dobj.CdDobj;
import nic.vahan.form.dobj.ExArmyDobj;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.ImportedVehicleDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.OtherStateVehDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.dobj.Owner_temp_dobj;
import nic.vahan.form.dobj.ReflectiveTapeDobj;
import nic.vahan.form.dobj.RetroFittingDetailsDobj;
import nic.vahan.form.dobj.SpeedGovernorDobj;
import nic.vahan.form.dobj.TempRegDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.Trailer_dobj;
import nic.vahan.form.dobj.dealer.PendencyBankDobj;
import nic.vahan.form.impl.dealer.PendencyBankDetailImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import nic.vahan.form.dobj.AuctionDobj;
import nic.vahan.form.dobj.fastag.FasTagDetailsDobj;

public class OwnerImpl {

    private static final Logger LOGGER = Logger.getLogger(OwnerImpl.class);

    public Owner_dobj getValidateVtOwnerTempDobj(String chasi_no) throws VahanException {
        Owner_dobj owner_dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        int offTo = 0;
        String stateTo = null;
        RowSet rs = null;
        String query = null;
        String parameterValue = null;
        String purpose = null;
        String temp_regn_no = null;
        String issue_date = null;

        try {

            if (chasi_no != null) {
                chasi_no = chasi_no.toUpperCase();
            }

            tmgr = new TransactionManager("getValidateVtOwnerTempDobj");
            query = "select a.*, b.descr as current_state_name, c.descr as current_district_name, d.off_name as issuing_off_name, e.descr as state_to_name, f.off_name as off_to_name, case when f.vow4 <= current_date then true else false end as v4_started \n"
                    + "   from VT_OWNER_TEMP a\n"
                    + "   left join TM_STATE b on a.c_state = b.state_code\n"
                    + "   left join TM_DISTRICT c on c.state_cd = a.c_state and a.c_district = c.dist_cd\n"
                    + "   left join tm_office d on d.state_cd = a.state_cd and d.off_cd = a.off_cd\n"
                    + "   left join TM_STATE e on a.state_cd_to = e.state_code\n"
                    + "   left join tm_office f on f.state_cd = a.state_cd_to and f.off_cd = a.off_cd_to\n"
                    + "   where a.chasi_no=? \n"
                    + "   order by op_dt desc limit 1";
            parameterValue = chasi_no;
            ps = tmgr.prepareStatement(query);
            ps.setString(1, parameterValue);
            rs = tmgr.fetchDetachedRowSet_No_release(); //state_cd_to, off_cd_to
            if (rs.next() && rs.getBoolean("v4_started")) {
                stateTo = rs.getString("state_cd_to").trim();
                offTo = rs.getInt("off_cd_to");
                purpose = rs.getString("purpose");
                temp_regn_no = rs.getString("temp_regn_no");
                if (rs.getDate("valid_from") != null) {
                    issue_date = JSFUtils.convertToStandardDateFormat(rs.getDate("valid_from"));
                }
                if (stateTo.equals(Util.getUserStateCode()) && (offTo == Util.getSelectedSeat().getOff_cd())
                        || (rs.getString("state_cd").equals(Util.getUserStateCode()) && rs.getInt("off_cd") == Util.getSelectedSeat().getOff_cd() && rs.getString("purpose").equalsIgnoreCase("BB"))) {
                    rs.beforeFirst();
                    owner_dobj = fillFrom_vt_owner_temp_with_state_cd_off_cd(rs);
                    owner_dobj.setRegn_dt(new Date());
                    TempRegDobj tempDobj = new TempRegDobj();
                    tempDobj.setDealer_cd(rs.getString("dealer_cd"));
                    tempDobj.setTmp_regn_no(rs.getString("temp_regn_no"));
                    tempDobj.setTmp_regn_dt(rs.getDate("valid_from"));
                    tempDobj.setTmp_state_cd(rs.getString("state_cd"));
                    tempDobj.setTmp_off_cd(rs.getInt("off_cd"));
                    tempDobj.setTmp_valid_upto(rs.getDate("valid_upto"));

                    owner_dobj.setTempReg(tempDobj);

                    //Get Linked Vehicle Detail
                    owner_dobj.setLinkedRegnNo(getLinkedVehDetails(rs.getString("appl_no"), tmgr));
                    //Insurance
                    InsDobj insDobj = InsImpl.set_ins_dtls_db_to_dobj(owner_dobj.getRegn_no(), null, rs.getString("state_cd"), rs.getInt("off_cd"));
                    owner_dobj.setInsDobj(insDobj);

                    //Axel Details
                    AxleDetailsDobj axleDobj = AxleImpl.setAxleVehDetails_db_to_dobj(null, owner_dobj.getRegn_no(), rs.getString("state_cd"), rs.getInt("off_cd"));
                    owner_dobj.setAxleDobj(axleDobj);

                    //RetroFitting Details
                    RetroFittingDetailsDobj retroDobj = RetroFittingDetailsImpl.setCngDetails_db_to_dobj_VT(owner_dobj.getRegn_no(), rs.getString("state_cd"), rs.getInt("off_cd"));
                    owner_dobj.setCng_dobj(retroDobj);

                    //HPA Details
                    HpaImpl hpaImpl = new HpaImpl();
                    List<HpaDobj> listHpaDobj = new ArrayList<>();
                    HpaDobj hpaDobj = hpaImpl.set_HPA_appl_db_to_dobj(null, owner_dobj.getRegn_no(), 0, rs.getString("state_cd"), rs.getInt("off_cd"));
                    listHpaDobj.add(hpaDobj);
                    owner_dobj.setListHpaDobj(listHpaDobj);

                    //Imported Vehicle Details
                    ImportedVehicleDobj impDobj = ImportedVehicleImpl.setImpVehDetails_db_to_dobj(null, owner_dobj.getRegn_no(), rs.getString("state_cd"), rs.getInt("off_cd"));
                    owner_dobj.setImp_Dobj(impDobj);

                } else {
                    if (purpose != null && !purpose.equalsIgnoreCase("OM")) {
                        String offName = ServerUtil.getOfficeName(offTo, stateTo);
                        stateTo = MasterTableFiller.getStateDescrByStateCode(stateTo);
                        throw new VahanException("Vehicle has Taken Temporary Registration [" + temp_regn_no + "] for State[" + stateTo + "] and Office[" + offName + "(" + offTo + ")], Issue Date [" + issue_date + "],"
                                + " Please Register this Vehicle in State[" + stateTo + "] at Office[" + offName + "(" + offTo + ")]");
                    }
                }
            }

        } catch (VahanException vme) {
            throw vme;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in fetching of Temporarily Registered details");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

        return owner_dobj;
    }

    public Owner_dobj getVaOwnerDetailsForRegnNo(String regNo) throws VahanException {
        Owner_dobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;

        try {
            tmgr = new TransactionManager("getVaOwnerDetailsForRegnNo");
            if (regNo == null || regNo.equals("")) {
                return null;
            }

            String query = "select *,state.descr as current_state_name,dist.descr as current_district_name from  " + TableList.VA_OWNER + " owner "
                    + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                    + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                    + " where owner.regn_no=? ";

            ps = tmgr.prepareStatement(query);
            ps.setString(1, regNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            dobj = fillFrom_Va_OwnerDobj(rs);

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Fetching of for regn_no=" + regNo);
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

    public Owner_dobj getVtOwnerDetailsForRegnNoStateCdOffCd(String regNo, String stateCd, int offCd) throws VahanException {
        Owner_dobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;

        try {
            tmgr = new TransactionManager("getVaOwnerDetailsForRegnNo");
            if (regNo == null || regNo.equals("")) {
                return null;
            }

            String query = "select *,state.descr as current_state_name,dist.descr as current_district_name from "
                    + TableList.VT_OWNER + " owner "
                    + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                    + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                    + " where owner.regn_no=? and owner.state_cd=? and owner.off_cd=? ";

            ps = tmgr.prepareStatement(query);
            ps.setString(1, regNo);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            dobj = fillFrom_VT_OwnerDobj(rs);

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Fetching of for regn_no=" + regNo);
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

    public Owner_dobj set_Owner_appl_db_to_dobj(String regn_no, String appl_no, String chasi_no, int pur_cd) throws Exception {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        Owner_dobj owner_dobj = null;
        RowSet rs = null;

        if (regn_no != null) {
            regn_no = regn_no.toUpperCase();
        }
        if (appl_no != null) {
            appl_no = appl_no.toUpperCase();
        }
        if (chasi_no != null) {
            chasi_no = chasi_no.toUpperCase();
        }

        try {
            tmgr = new TransactionManager("Owner_Impl");
            String query = "";
            String parameterValue = "";
            if (TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE == pur_cd
                    || TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE == pur_cd
                    || TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION == pur_cd
                    || TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE_FITNESS == pur_cd) {
                if (appl_no != null && !appl_no.equalsIgnoreCase("")) {
                    query = "select *,state.descr as current_state_name,dist.descr as current_district_name from " + TableList.VA_OWNER + " owner "
                            + " left join " + TableList.VA_SIDE_TRAILER + " trailer on owner.appl_no = trailer.appl_no "
                            + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                            + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                            + " where owner.appl_no=? ";
                    parameterValue = appl_no;

                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, parameterValue);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    owner_dobj = fillFrom_Va_OwnerDobj(rs);

                } else if (chasi_no != null && !chasi_no.equalsIgnoreCase("")) {
                    query = "select *,state.descr as current_state_name,dist.descr as current_district_name from  " + TableList.VA_OWNER + " owner "
                            + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                            + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                            + " where owner.chasi_no=? ";
                    parameterValue = chasi_no;

                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, parameterValue);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    owner_dobj = fillFrom_Va_OwnerDobj(rs);

                    if (owner_dobj == null) {
                        query = "select *,state.descr as current_state_name,dist.descr as current_district_name from  " + TableList.VT_OWNER + " owner "
                                + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                                + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                                + " where owner.chasi_no=? ";

                        ps = tmgr.prepareStatement(query);
                        ps.setString(1, parameterValue);
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        owner_dobj = fillFrom_VT_OwnerDobj(rs);
                    }

                } else if (regn_no != null && !regn_no.equalsIgnoreCase("")) {
                    query = "select *,state.descr as current_state_name,dist.descr as current_district_name from  " + TableList.VT_OWNER + " owner "
                            + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                            + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                            + " where owner.regn_no=? ";
                    parameterValue = regn_no;
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, parameterValue);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    owner_dobj = fillFrom_VT_OwnerDobj(rs);
                }
            } else if (TableConstants.VM_TRANSACTION_MAST_TEMP_REG == pur_cd || TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE == pur_cd) {
                if (appl_no != null && !appl_no.equalsIgnoreCase("")) {
                    query = "select *,state.descr as current_state_name,dist.descr as current_district_name from " + TableList.VA_OWNER + " owner "
                            + " left join " + TableList.VA_SIDE_TRAILER + " trailer on owner.appl_no = trailer.appl_no "
                            + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                            + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                            + " where owner.appl_no=? ";
                    parameterValue = appl_no;

                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, parameterValue);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    owner_dobj = fillFrom_Va_OwnerDobj(rs);
                    if (owner_dobj != null) {
                        ps = tmgr.prepareStatement("select * from va_owner_temp where appl_no=? ");
                        ps.setString(1, parameterValue);
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        Owner_temp_dobj temp = fillFrom_va_owner_temp(rs);
                        if (temp != null) {
                            temp.setAppl_no(appl_no);
                        }
                        owner_dobj.setDob_temp(temp);
                    }

                } else if (chasi_no != null && !chasi_no.equalsIgnoreCase("")) {
                    query = "select *,state.descr as current_state_name,dist.descr as current_district_name from  " + TableList.VA_OWNER + " owner "
                            + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                            + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                            + " where owner.chasi_no=?";
                    parameterValue = chasi_no;

                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, parameterValue);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    owner_dobj = fillFrom_Va_OwnerDobj(rs);
                    if (owner_dobj != null) {
                        ps = tmgr.prepareStatement("select * from va_owner_temp where appl_no=? ");
                        ps.setString(1, owner_dobj.getAppl_no());
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        Owner_temp_dobj temp = fillFrom_va_owner_temp(rs);
                        if (temp != null) {
                            temp.setAppl_no(appl_no);
                        }
                        owner_dobj.setDob_temp(temp);
                    }

                    //validate from vt_owner_temp
                    if (owner_dobj == null) {
                        query = "select *,state.descr as current_state_name,dist.descr as current_district_name from  " + TableList.VT_OWNER_TEMP + " owner "
                                + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                                + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                                + " where owner.chasi_no=? ";

                        ps = tmgr.prepareStatement(query);
                        ps.setString(1, parameterValue);
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        owner_dobj = fillFrom_vt_owner_temp(rs);
                    }

                } else if (regn_no != null && !regn_no.equalsIgnoreCase("")) {
                    query = "select *,state.descr as current_state_name,dist.descr as current_district_name from  " + TableList.VT_OWNER_TEMP + " owner "
                            + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                            + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                            + " where owner.temp_regn_no=? ";
                    parameterValue = regn_no;
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, parameterValue);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    owner_dobj = fillFrom_vt_owner_temp(rs);
                }
            } else {
                if (regn_no != null) {
                    query = "select *,state.descr as current_state_name,dist.descr as current_district_name from  " + TableList.VT_OWNER + " owner "
                            + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                            + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                            + " where owner.regn_no=? order by op_dt desc limit 1";
                    parameterValue = regn_no;
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, parameterValue);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    owner_dobj = fillFrom_VT_OwnerDobj(rs);
                } else if (chasi_no != null) {
                    query = "select *,state.descr as current_state_name,dist.descr as current_district_name from  " + TableList.VT_OWNER + " owner "
                            + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                            + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                            + " where owner.chasi_no=? ";
                    parameterValue = chasi_no;
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, parameterValue);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    owner_dobj = fillFrom_VT_OwnerDobj(rs);

                }
            }
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }

        return owner_dobj;
    }

    public Owner_dobj set_Owner_appl_db_to_dobj_with_state_off_cd(String regn_no, String appl_no, String chasi_no, int pur_cd) throws Exception {

        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        Owner_dobj owner_dobj = null;
        RowSet rs = null;

        if (regn_no != null) {
            regn_no = regn_no.toUpperCase();
        }
        if (appl_no != null) {
            appl_no = appl_no.toUpperCase();
        }
        if (chasi_no != null) {
            chasi_no = chasi_no.toUpperCase();
        }

        try {
            tmgr = new TransactionManager("Owner_Impl");
            String query = "";
            String parameterValue = "";
            if (TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE == pur_cd
                    || TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE == pur_cd
                    || TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION == pur_cd
                    || TableConstants.VM_TRANSACTION_MAST_NOC == pur_cd) {
                if (appl_no != null && !appl_no.equalsIgnoreCase("")) {
                    query = "select *,state.descr as current_state_name,dist.descr as current_district_name from " + TableList.VA_OWNER + " owner "
                            + " left join " + TableList.VA_SIDE_TRAILER + " trailer on owner.appl_no = trailer.appl_no "
                            + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                            + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                            + " where owner.appl_no=? ";
                    parameterValue = appl_no;

                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, parameterValue);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    owner_dobj = fillFrom_Va_OwnerDobj(rs);

                } else if (chasi_no != null && !chasi_no.equalsIgnoreCase("")) {
                    query = "select *,state.descr as current_state_name,dist.descr as current_district_name from  " + TableList.VA_OWNER + " owner "
                            + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                            + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                            + " where owner.chasi_no=?";
                    parameterValue = chasi_no;

                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, parameterValue);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    owner_dobj = fillFrom_Va_OwnerDobj(rs);

                    if (owner_dobj == null) {
                        query = "select *,state.descr as current_state_name,dist.descr as current_district_name from  " + TableList.VT_OWNER + " owner "
                                + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                                + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                                + " where owner.chasi_no=? and owner.state_cd = ? and owner.off_cd = ? ";

                        ps = tmgr.prepareStatement(query);
                        ps.setString(1, parameterValue);
                        ps.setString(2, Util.getUserStateCode());
                        ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        owner_dobj = fillFrom_VT_OwnerDobj_with_state_off_cd(rs);
                    }

                } else if (regn_no != null && !regn_no.equalsIgnoreCase("")) {
                    query = "select *,state.descr as current_state_name,dist.descr as current_district_name from  " + TableList.VT_OWNER + " owner "
                            + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                            + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                            + " where owner.regn_no=? and owner.state_cd = ? and owner.off_cd = ?";
                    parameterValue = regn_no;
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, parameterValue);
                    ps.setString(2, Util.getUserStateCode());
                    ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    owner_dobj = fillFrom_VT_OwnerDobj_with_state_off_cd(rs);
                }
            } else if (TableConstants.VM_TRANSACTION_MAST_TEMP_REG == pur_cd || TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE == pur_cd) {
                if (appl_no != null && !appl_no.equalsIgnoreCase("")) {
                    query = "select *,state.descr as current_state_name,dist.descr as current_district_name from " + TableList.VA_OWNER + " owner "
                            + " left join " + TableList.VA_SIDE_TRAILER + " trailer on owner.appl_no = trailer.appl_no "
                            + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                            + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                            + " where owner.appl_no=? ";
                    parameterValue = appl_no;

                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, parameterValue);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    owner_dobj = fillFrom_Va_OwnerDobj(rs);
                    //add amit
                    if (owner_dobj == null) {
                        query = "select *,state.descr as current_state_name,dist.descr as current_district_name from " + TableList.VA_OWNER_TEMP_ADMIN + " owner_admin "
                                + " left join " + TableList.VA_SIDE_TRAILER + " trailer on owner_admin.appl_no = trailer.appl_no "
                                + " left join " + TableList.TM_STATE + " state on owner_admin.c_state = state.state_code "
                                + " left join " + TableList.TM_DISTRICT + " dist on owner_admin.c_district = dist.dist_cd "
                                + " where owner_admin.appl_no=? ";
                        parameterValue = appl_no;

                        ps = tmgr.prepareStatement(query);
                        ps.setString(1, parameterValue);
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        owner_dobj = fillFrom_Va_OwnerDobj(rs);
                    }
                    if (owner_dobj != null) {
                        ps = tmgr.prepareStatement("select * from va_owner_temp where appl_no=? ");
                        ps.setString(1, parameterValue);
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        Owner_temp_dobj temp = fillFrom_va_owner_temp(rs);
                        if (temp != null) {
                            temp.setAppl_no(appl_no);
                        }
                        owner_dobj.setDob_temp(temp);
                    }

                } else if (chasi_no != null && !chasi_no.equalsIgnoreCase("")) {
                    query = "select *,state.descr as current_state_name,dist.descr as current_district_name from  " + TableList.VA_OWNER + " owner "
                            + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                            + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                            + " where owner.chasi_no=?";
                    parameterValue = chasi_no;

                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, parameterValue);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    owner_dobj = fillFrom_Va_OwnerDobj(rs);
                    if (owner_dobj != null) {
                        ps = tmgr.prepareStatement("select * from va_owner_temp where appl_no=? ");
                        ps.setString(1, owner_dobj.getAppl_no());
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        Owner_temp_dobj temp = fillFrom_va_owner_temp(rs);
                        if (temp != null) {
                            temp.setAppl_no(appl_no);
                        }
                        owner_dobj.setDob_temp(temp);
                    }

                    //validate from vt_owner_temp
                    if (owner_dobj == null) {
                        query = "select *,state.descr as current_state_name,dist.descr as current_district_name from  " + TableList.VT_OWNER_TEMP + " owner "
                                + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                                + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                                + " where owner.chasi_no=? and owner.state_cd = ? and owner.off_cd = ? ";

                        ps = tmgr.prepareStatement(query);
                        ps.setString(1, parameterValue);
                        ps.setString(2, Util.getUserStateCode());
                        ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        owner_dobj = fillFrom_vt_owner_temp_with_state_cd_off_cd(rs);
                    }

                } else if (regn_no != null && !regn_no.equalsIgnoreCase("")) {
                    query = "select *,state.descr as current_state_name,dist.descr as current_district_name from  " + TableList.VT_OWNER_TEMP + " owner "
                            + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                            + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                            + " where owner.temp_regn_no=? order by owner.op_dt desc limit 1";
                    parameterValue = regn_no;
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, parameterValue);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    owner_dobj = fillFrom_vt_owner_temp_with_state_cd_off_cd(rs);
                }
            } else {
                if (regn_no != null) {
                    //add Top
                    int off_cd = Util.getSelectedSeat().getOff_cd();
                    TmConfigurationDobj tm_Cong = Util.getTmConfiguration();
                    boolean allow_fitness_all_rto = tm_Cong.isAllow_fitness_all_RTO();
                    if (allow_fitness_all_rto) {
                        query = "select off_cd from  " + TableList.VT_OWNER + " where regn_no=? and state_cd = ?";
                        ps = tmgr.prepareStatement(query);
                        ps.setString(1, regn_no);
                        ps.setString(2, Util.getUserStateCode());
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        if (rs.next()) {
                            int off_cd_other_vechile = rs.getInt("off_cd");
                            if (off_cd_other_vechile != off_cd) {
                                off_cd = off_cd_other_vechile;
                            }
                        }
                    }
                    query = "select *,state.descr as current_state_name,dist.descr as current_district_name from  " + TableList.VT_OWNER + " owner "
                            + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                            + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                            + " where owner.regn_no=? and owner.state_cd = ? and owner.off_cd = ?";

                    parameterValue = regn_no;
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, parameterValue);
                    ps.setString(2, Util.getUserStateCode());
                    ps.setInt(3, off_cd);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    owner_dobj = fillFrom_VT_OwnerDobj_with_state_off_cd(rs);
                } else if (chasi_no != null) {
                    query = "select *,state.descr as current_state_name,dist.descr as current_district_name from  " + TableList.VT_OWNER + " owner "
                            + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                            + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                            + " where owner.chasi_no=? and owner.state_cd = ? and owner.off_cd = ?";
                    parameterValue = chasi_no;
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, parameterValue);
                    ps.setString(2, Util.getUserStateCode());
                    ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    owner_dobj = fillFrom_VT_OwnerDobj_with_state_off_cd(rs);

                }
            }
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e);
                }
            }
        }

        return owner_dobj;
    }

    public Owner_dobj fillFrom_VT_OwnerDobj_with_state_off_cd(RowSet rsOwner) throws VahanException {
        Owner_dobj owner_dobj = null;
        TransactionManager tmgrOwner = null;
        RowSet rs = null;
        try {
            if (rsOwner.next()) // found
            {
                owner_dobj = new Owner_dobj();

                owner_dobj.setState_cd(rsOwner.getString("state_cd"));
                owner_dobj.setOff_cd(rsOwner.getInt("off_cd"));
                owner_dobj.setRegn_no(rsOwner.getString("regn_no"));
                owner_dobj.setRegn_dt(rsOwner.getDate("regn_dt"));
                owner_dobj.setPurchase_dt(rsOwner.getDate("purchase_dt"));
                owner_dobj.setOwner_sr(rsOwner.getInt("owner_sr"));
                owner_dobj.setOwner_name(rsOwner.getString("owner_name"));
                owner_dobj.setF_name(rsOwner.getString("f_name"));
                owner_dobj.setC_add1(rsOwner.getString("c_add1"));
                owner_dobj.setC_add2(rsOwner.getString("c_add2"));
                owner_dobj.setC_add3(rsOwner.getString("c_add3"));
                owner_dobj.setC_district(rsOwner.getInt("c_district"));
                owner_dobj.setC_pincode(rsOwner.getInt("c_pincode"));
                owner_dobj.setC_state(rsOwner.getString("c_state"));
                owner_dobj.setP_add1(rsOwner.getString("p_add1"));
                owner_dobj.setP_add2(rsOwner.getString("p_add2"));
                owner_dobj.setP_add3(rsOwner.getString("p_add3"));
                owner_dobj.setP_district(rsOwner.getInt("p_district"));
                owner_dobj.setP_pincode(rsOwner.getInt("p_pincode"));
                owner_dobj.setP_state(rsOwner.getString("p_state"));
                owner_dobj.setOwner_cd(rsOwner.getInt("owner_cd"));
                owner_dobj.setRegn_type(rsOwner.getString("regn_type"));
                owner_dobj.setVh_class(rsOwner.getInt("vh_class"));
                owner_dobj.setChasi_no(rsOwner.getString("chasi_no"));
                owner_dobj.setEng_no(rsOwner.getString("eng_no"));
                owner_dobj.setMaker(rsOwner.getInt("maker"));
                owner_dobj.setMaker_model(rsOwner.getString("maker_model"));
                owner_dobj.setBody_type(rsOwner.getString("body_type"));
                owner_dobj.setNo_cyl(rsOwner.getInt("no_cyl"));
                owner_dobj.setHp(rsOwner.getFloat("hp"));
                owner_dobj.setSeat_cap(rsOwner.getInt("seat_cap"));
                owner_dobj.setStand_cap(rsOwner.getInt("stand_cap"));
                owner_dobj.setSleeper_cap(rsOwner.getInt("sleeper_cap"));
                owner_dobj.setUnld_wt(rsOwner.getInt("unld_wt"));
                owner_dobj.setLd_wt(rsOwner.getInt("ld_wt"));
                owner_dobj.setGcw(rsOwner.getInt("gcw"));
                owner_dobj.setFuel(rsOwner.getInt("fuel"));
                owner_dobj.setColor(rsOwner.getString("color"));
                owner_dobj.setManu_mon(rsOwner.getInt("manu_mon"));
                owner_dobj.setManu_yr(rsOwner.getInt("manu_yr"));
                owner_dobj.setNorms(rsOwner.getInt("norms"));
                owner_dobj.setWheelbase(rsOwner.getInt("wheelbase"));
                owner_dobj.setCubic_cap(rsOwner.getFloat("cubic_cap"));
                owner_dobj.setFloor_area(rsOwner.getFloat("floor_area"));
                owner_dobj.setAc_fitted(rsOwner.getString("ac_fitted"));
                owner_dobj.setAudio_fitted(rsOwner.getString("audio_fitted"));
                owner_dobj.setVideo_fitted(rsOwner.getString("video_fitted"));
                owner_dobj.setVch_purchase_as(rsOwner.getString("vch_purchase_as"));
                owner_dobj.setVch_catg(rsOwner.getString("vch_catg"));
                owner_dobj.setDealer_cd(rsOwner.getString("dealer_cd"));
                owner_dobj.setSale_amt(rsOwner.getInt("sale_amt"));
                owner_dobj.setLaser_code(rsOwner.getString("laser_code"));
                owner_dobj.setGarage_add(rsOwner.getString("garage_add"));
                owner_dobj.setLength(rsOwner.getInt("length"));
                owner_dobj.setWidth(rsOwner.getInt("width"));
                owner_dobj.setHeight(rsOwner.getInt("height"));
                owner_dobj.setRegn_upto(rsOwner.getDate("regn_upto"));
                owner_dobj.setFit_upto(rsOwner.getDate("fit_upto"));
                owner_dobj.setAnnual_income(rsOwner.getInt("annual_income"));
                owner_dobj.setImported_vch(rsOwner.getString("imported_vch"));
                owner_dobj.setOther_criteria(rsOwner.getInt("other_criteria"));
                owner_dobj.setStatus(rsOwner.getString("status"));
                owner_dobj.setVehType(ServerUtil.VehicleClassType(owner_dobj.getVh_class()));
                owner_dobj.setOp_dt(rsOwner.getString("op_dt"));
                owner_dobj.setC_district_name(rsOwner.getString("current_district_name"));
                owner_dobj.setC_state_name(rsOwner.getString("current_state_name"));

                tmgrOwner = new TransactionManager("Owner_Id");
                String sql = "SELECT * FROM " + TableList.VT_OWNER_IDENTIFICATION
                        + " where regn_no =? and state_cd = ? and off_cd = ? ";
                PreparedStatement ps = tmgrOwner.prepareStatement(sql);
                ps.setString(1, owner_dobj.getRegn_no());
                ps.setString(2, owner_dobj.getState_cd());
                ps.setInt(3, owner_dobj.getOff_cd());
                rs = tmgrOwner.fetchDetachedRowSet_No_release();
                OwnerIdentificationDobj own_identity = fillOwnerIdentityDobj(rs);
                if (own_identity != null) {
                    own_identity.setAppl_no(owner_dobj.getAppl_no());
                    owner_dobj.setOwner_identity(own_identity);
                }

                sql = "SELECT state_cd, off_cd, regn_no, sg_no, sg_fitted_on, sg_fitted_at, op_dt, emp_cd,"
                        + " sg_type,sg_type_approval_no,sg_test_report_no,sg_fitment_cert_no "
                        + "  FROM vt_speed_governor where regn_no=? and state_cd = ? and off_cd = ? ";
                ps = tmgrOwner.prepareStatement(sql);
                ps.setString(1, owner_dobj.getRegn_no());
                ps.setString(2, owner_dobj.getState_cd());
                ps.setInt(3, owner_dobj.getOff_cd());
                rs = tmgrOwner.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    SpeedGovernorDobj spDobj = new SpeedGovernorDobj();
                    spDobj.setState_cd(rs.getString("state_cd"));
                    spDobj.setOff_cd(rs.getInt("off_cd"));
                    spDobj.setRegn_no(rs.getString("regn_no"));
                    spDobj.setSg_no(rs.getString("sg_no"));
                    spDobj.setSg_fitted_on(rs.getDate("sg_fitted_on"));
                    spDobj.setSg_fitted_at(rs.getString("sg_fitted_at"));
                    spDobj.setSgGovType(rs.getInt("sg_type"));
                    spDobj.setSgTypeApprovalNo(rs.getString("sg_type_approval_no"));
                    spDobj.setSgTestReportNo(rs.getString("sg_test_report_no"));
                    spDobj.setSgFitmentCerticateNo(rs.getString("sg_fitment_cert_no"));
                    owner_dobj.setSpeedGovernorDobj(spDobj);
                }

                FitnessImpl fitImpl = new FitnessImpl();
                ReflectiveTapeDobj refDobj = fitImpl.getVtReflectiveTapeDobj(owner_dobj.getRegn_no(), owner_dobj.getState_cd(), owner_dobj.getOff_cd(), tmgrOwner);
                owner_dobj.setReflectiveTapeDobj(refDobj);

            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Errror in Getting Vehicle Details");
        } finally {
            if (tmgrOwner != null) {
                try {
                    tmgrOwner.release();
                } catch (Exception e) {
                    LOGGER.error(e);
                }
            }
        }

        return owner_dobj;
    }

    public Owner_dobj fillFrom_vt_owner_temp_with_state_cd_off_cd(RowSet rs) throws SQLException {
        Owner_dobj dobj = null;
        if (rs.next()) {
            dobj = new Owner_dobj();
            dobj.setState_cd(rs.getString("state_cd"));
            dobj.setOff_cd(rs.getInt("off_cd"));
            dobj.setAppl_no(rs.getString("appl_no"));
            dobj.setRegn_no(rs.getString("temp_regn_no"));
            //dobj.setValid_from(rs.getString("valid_from"));
            //dobj.setValid_upto(rs.getString("valid_upto"));
            dobj.setPurchase_dt(rs.getDate("purchase_dt"));
            dobj.setOwner_name(rs.getString("owner_name"));
            dobj.setF_name(rs.getString("f_name"));
            dobj.setC_add1(rs.getString("c_add1"));
            dobj.setC_add2(rs.getString("c_add2"));
            dobj.setC_add3(rs.getString("c_add3"));
            dobj.setC_district(rs.getInt("c_district"));
            dobj.setC_pincode(rs.getInt("c_pincode"));
            dobj.setC_state(rs.getString("c_state"));
            dobj.setP_add1(rs.getString("p_add1"));
            dobj.setP_add2(rs.getString("p_add2"));
            dobj.setP_add3(rs.getString("p_add3"));
            dobj.setP_district(rs.getInt("p_district"));
            dobj.setP_pincode(rs.getInt("p_pincode"));
            dobj.setP_state(rs.getString("p_state"));
            dobj.setOwner_cd(rs.getInt("owner_cd"));
            dobj.setRegn_type(rs.getString("regn_type"));
            dobj.setVh_class(rs.getInt("vh_class"));
            dobj.setChasi_no(rs.getString("chasi_no"));
            dobj.setEng_no(rs.getString("eng_no"));
            dobj.setMaker(rs.getInt("maker"));
            dobj.setMaker_model(rs.getString("maker_model"));
            dobj.setBody_type(rs.getString("body_type"));
            dobj.setNo_cyl(rs.getInt("no_cyl"));
            dobj.setHp(rs.getFloat("hp"));
            dobj.setSeat_cap(rs.getInt("seat_cap"));
            dobj.setStand_cap(rs.getInt("stand_cap"));
            dobj.setSleeper_cap(rs.getInt("sleeper_cap"));
            dobj.setUnld_wt(rs.getInt("unld_wt"));
            dobj.setLd_wt(rs.getInt("ld_wt"));
            dobj.setGcw(rs.getInt("gcw"));
            dobj.setFuel(rs.getInt("fuel"));
            dobj.setColor(rs.getString("color"));
            dobj.setManu_mon(rs.getInt("manu_mon"));
            dobj.setManu_yr(rs.getInt("manu_yr"));
            dobj.setNorms(rs.getInt("norms"));
            dobj.setWheelbase(rs.getInt("wheelbase"));
            dobj.setCubic_cap(rs.getFloat("cubic_cap"));
            dobj.setFloor_area(rs.getFloat("floor_area"));
            dobj.setAc_fitted(rs.getString("ac_fitted"));
            dobj.setAudio_fitted(rs.getString("audio_fitted"));
            dobj.setVideo_fitted(rs.getString("video_fitted"));
            dobj.setVch_purchase_as(rs.getString("vch_purchase_as"));
            dobj.setVch_catg(rs.getString("vch_catg"));
            dobj.setDealer_cd(rs.getString("dealer_cd"));
            dobj.setSale_amt(rs.getInt("sale_amt"));
            dobj.setLaser_code(rs.getString("laser_code"));
            dobj.setGarage_add(rs.getString("garage_add"));
            dobj.setLength(rs.getInt("length"));
            dobj.setWidth(rs.getInt("width"));
            dobj.setHeight(rs.getInt("height"));
            dobj.setRegn_upto(rs.getDate("regn_upto"));
            dobj.setFit_upto(rs.getDate("fit_upto"));
            dobj.setAnnual_income(rs.getInt("annual_income"));
            dobj.setImported_vch(rs.getString("imported_vch"));
            dobj.setOther_criteria(rs.getInt("other_criteria"));
            dobj.setC_district_name(rs.getString("current_district_name"));
            dobj.setC_state_name(rs.getString("current_state_name"));
            if (rs.getString("state_cd_to") != null) {
                Owner_temp_dobj tempDobj = new Owner_temp_dobj();
                tempDobj.setState_cd_to(rs.getString("state_cd_to"));
                tempDobj.setOff_cd_to(rs.getInt("off_cd_to"));
                tempDobj.setTemp_regn_no(rs.getString("temp_regn_no"));
                tempDobj.setPurpose(rs.getString("purpose"));
                tempDobj.setBodyBuilding(rs.getString("body_building"));
                tempDobj.setValidFrom(rs.getDate("valid_from"));
                tempDobj.setValidUpto(rs.getDate("valid_upto"));
                dobj.setDob_temp(tempDobj);
            }

            TransactionManager tmgrOwner = null;
            try {
                tmgrOwner = new TransactionManager("Owner_Id");
                RowSet rsOwnerId = null;
                String sqlOwnerId = "SELECT * FROM " + TableList.VT_OWNER_IDENTIFICATION
                        + " where regn_no =? and state_cd = ? and off_cd = ? ";
                PreparedStatement ps = tmgrOwner.prepareStatement(sqlOwnerId);
                ps.setString(1, dobj.getRegn_no());
                ps.setString(2, dobj.getState_cd());
                ps.setInt(3, dobj.getOff_cd());
                rsOwnerId = tmgrOwner.fetchDetachedRowSet_No_release();
                OwnerIdentificationDobj own_identity = fillOwnerIdentityDobj(rsOwnerId);
                if (own_identity != null) {
                    own_identity.setAppl_no(dobj.getAppl_no());
                    dobj.setOwner_identity(own_identity);
                }

                String sql = "SELECT state_cd, off_cd, regn_no, sg_no, sg_fitted_on, sg_fitted_at, op_dt, emp_cd,"
                        + " sg_type,sg_type_approval_no,sg_test_report_no,sg_fitment_cert_no "
                        + "  FROM vt_speed_governor where regn_no=? and state_cd = ? and off_cd = ? ";
                ps = tmgrOwner.prepareStatement(sql);
                ps.setString(1, dobj.getRegn_no());
                ps.setString(2, dobj.getState_cd());
                ps.setInt(3, dobj.getOff_cd());
                rs = tmgrOwner.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    SpeedGovernorDobj spDobj = new SpeedGovernorDobj();
                    spDobj.setState_cd(rs.getString("state_cd"));
                    spDobj.setOff_cd(rs.getInt("off_cd"));
                    spDobj.setRegn_no(rs.getString("regn_no"));
                    spDobj.setSg_no(rs.getString("sg_no"));
                    spDobj.setSg_fitted_on(rs.getDate("sg_fitted_on"));
                    spDobj.setSg_fitted_at(rs.getString("sg_fitted_at"));
                    spDobj.setSgGovType(rs.getInt("sg_type"));
                    spDobj.setSgTypeApprovalNo(rs.getString("sg_type_approval_no"));
                    spDobj.setSgTestReportNo(rs.getString("sg_test_report_no"));
                    spDobj.setSgFitmentCerticateNo(rs.getString("sg_fitment_cert_no"));
                    dobj.setSpeedGovernorDobj(spDobj);
                }
                FitnessImpl fitImpl = new FitnessImpl();
                ReflectiveTapeDobj refDobj = fitImpl.getVtReflectiveTapeDobj(dobj.getRegn_no(), dobj.getState_cd(), dobj.getOff_cd(), tmgrOwner);
                dobj.setReflectiveTapeDobj(refDobj);

            } finally {
                if (tmgrOwner != null) {
                    try {
                        tmgrOwner.release();
                    } catch (Exception ex) {
                        LOGGER.error(ex);
                    }
                }
            }

        }

        return dobj;
    }

    public Owner_dobj fillFrom_vt_owner_temp(RowSet rs) throws SQLException {
        Owner_dobj dobj = null;
        if (rs.next()) {
            dobj = new Owner_dobj();
            dobj.setState_cd(rs.getString("state_cd"));
            dobj.setOff_cd(rs.getInt("off_cd"));
            dobj.setAppl_no(rs.getString("appl_no"));
            dobj.setRegn_no(rs.getString("temp_regn_no"));
            //dobj.setValid_from(rs.getString("valid_from"));
            //dobj.setValid_upto(rs.getString("valid_upto"));
            dobj.setPurchase_dt(rs.getDate("purchase_dt"));
            dobj.setOwner_name(rs.getString("owner_name"));
            dobj.setF_name(rs.getString("f_name"));
            dobj.setC_add1(rs.getString("c_add1"));
            dobj.setC_add2(rs.getString("c_add2"));
            dobj.setC_add3(rs.getString("c_add3"));
            dobj.setC_district(rs.getInt("c_district"));
            dobj.setC_pincode(rs.getInt("c_pincode"));
            dobj.setC_state(rs.getString("c_state"));
            dobj.setP_add1(rs.getString("p_add1"));
            dobj.setP_add2(rs.getString("p_add2"));
            dobj.setP_add3(rs.getString("p_add3"));
            dobj.setP_district(rs.getInt("p_district"));
            dobj.setP_pincode(rs.getInt("p_pincode"));
            dobj.setP_state(rs.getString("p_state"));
            dobj.setOwner_cd(rs.getInt("owner_cd"));
            dobj.setRegn_type(rs.getString("regn_type"));
            dobj.setVh_class(rs.getInt("vh_class"));
            dobj.setChasi_no(rs.getString("chasi_no"));
            dobj.setEng_no(rs.getString("eng_no"));
            dobj.setMaker(rs.getInt("maker"));
            dobj.setMaker_model(rs.getString("maker_model"));
            dobj.setBody_type(rs.getString("body_type"));
            dobj.setNo_cyl(rs.getInt("no_cyl"));
            dobj.setHp(rs.getFloat("hp"));
            dobj.setSeat_cap(rs.getInt("seat_cap"));
            dobj.setStand_cap(rs.getInt("stand_cap"));
            dobj.setSleeper_cap(rs.getInt("sleeper_cap"));
            dobj.setUnld_wt(rs.getInt("unld_wt"));
            dobj.setLd_wt(rs.getInt("ld_wt"));
            dobj.setGcw(rs.getInt("gcw"));
            dobj.setFuel(rs.getInt("fuel"));
            dobj.setColor(rs.getString("color"));
            dobj.setManu_mon(rs.getInt("manu_mon"));
            dobj.setManu_yr(rs.getInt("manu_yr"));
            dobj.setNorms(rs.getInt("norms"));
            dobj.setWheelbase(rs.getInt("wheelbase"));
            dobj.setCubic_cap(rs.getFloat("cubic_cap"));
            dobj.setFloor_area(rs.getFloat("floor_area"));
            dobj.setAc_fitted(rs.getString("ac_fitted"));
            dobj.setAudio_fitted(rs.getString("audio_fitted"));
            dobj.setVideo_fitted(rs.getString("video_fitted"));
            dobj.setVch_purchase_as(rs.getString("vch_purchase_as"));
            dobj.setVch_catg(rs.getString("vch_catg"));
            dobj.setDealer_cd(rs.getString("dealer_cd"));
            dobj.setSale_amt(rs.getInt("sale_amt"));
            dobj.setLaser_code(rs.getString("laser_code"));
            dobj.setGarage_add(rs.getString("garage_add"));
            dobj.setLength(rs.getInt("length"));
            dobj.setWidth(rs.getInt("width"));
            dobj.setHeight(rs.getInt("height"));
            dobj.setRegn_upto(rs.getDate("regn_upto"));
            dobj.setFit_upto(rs.getDate("fit_upto"));
            dobj.setAnnual_income(rs.getInt("annual_income"));
            dobj.setImported_vch(rs.getString("imported_vch"));
            dobj.setOther_criteria(rs.getInt("other_criteria"));
            dobj.setC_district_name(rs.getString("current_district_name"));
            dobj.setC_state_name(rs.getString("current_state_name"));
            dobj.setOwner_sr(1);
            if (rs.getString("state_cd_to") != null) {
                Owner_temp_dobj tempDobj = new Owner_temp_dobj();
                tempDobj.setState_cd_to(rs.getString("state_cd_to"));
                tempDobj.setOff_cd_to(rs.getInt("off_cd_to"));
                tempDobj.setTemp_regn_no(rs.getString("temp_regn_no"));
                tempDobj.setPurpose(rs.getString("purpose"));
                tempDobj.setBodyBuilding(rs.getString("body_building"));
                dobj.setDob_temp(tempDobj);
            }

            TransactionManager tmgrOwner = null;
            try {
                tmgrOwner = new TransactionManager("Owner_Id");
                RowSet rsOwnerId = null;
                String sqlOwnerId = "SELECT * FROM " + TableList.VT_OWNER_IDENTIFICATION
                        + " where regn_no =? and state_cd = ? and off_cd = ? ";
                PreparedStatement ps = tmgrOwner.prepareStatement(sqlOwnerId);
                ps.setString(1, dobj.getRegn_no());
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                rsOwnerId = tmgrOwner.fetchDetachedRowSet_No_release();
                OwnerIdentificationDobj own_identity = fillOwnerIdentityDobj(rsOwnerId);
                if (own_identity != null) {
                    own_identity.setAppl_no(dobj.getAppl_no());
                    dobj.setOwner_identity(own_identity);
                }

                String sql = "SELECT state_cd, off_cd, regn_no, sg_no, sg_fitted_on, sg_fitted_at, op_dt, emp_cd,"
                        + " sg_type,sg_type_approval_no,sg_test_report_no,sg_fitment_cert_no "
                        + "  FROM vt_speed_governor where regn_no=? and state_cd = ? and off_cd = ? ";
                ps = tmgrOwner.prepareStatement(sql);
                ps.setString(1, dobj.getRegn_no());
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                rs = tmgrOwner.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    SpeedGovernorDobj spDobj = new SpeedGovernorDobj();
                    spDobj.setState_cd(rs.getString("state_cd"));
                    spDobj.setOff_cd(rs.getInt("off_cd"));
                    spDobj.setRegn_no(rs.getString("regn_no"));
                    spDobj.setSg_no(rs.getString("sg_no"));
                    spDobj.setSg_fitted_on(rs.getDate("sg_fitted_on"));
                    spDobj.setSg_fitted_at(rs.getString("sg_fitted_at"));

                    spDobj.setSgGovType(rs.getInt("sg_type"));
                    spDobj.setSgTypeApprovalNo(rs.getString("sg_type_approval_no"));
                    spDobj.setSgTestReportNo(rs.getString("sg_test_report_no"));
                    spDobj.setSgFitmentCerticateNo(rs.getString("sg_fitment_cert_no"));
                    dobj.setSpeedGovernorDobj(spDobj);
                }

                FitnessImpl fitImpl = new FitnessImpl();
                ReflectiveTapeDobj refDobj = fitImpl.getVtReflectiveTapeDobj(dobj.getRegn_no(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), tmgrOwner);
                dobj.setReflectiveTapeDobj(refDobj);

            } finally {
                if (tmgrOwner != null) {
                    try {
                        tmgrOwner.release();
                    } catch (Exception ex) {
                        LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                    }
                }
            }

        }

        return dobj;
    }

    public Owner_temp_dobj fillFrom_va_owner_temp(RowSet rs) throws SQLException {
        Owner_temp_dobj dobj = null;
        if (rs.next()) {
            dobj = new Owner_temp_dobj();
            dobj.setTemp_regn_no(rs.getString("temp_regn_no"));
            dobj.setState_cd_to(rs.getString("state_cd_to"));
            dobj.setOff_cd_to(rs.getInt("off_cd_to"));
            dobj.setPurpose(rs.getString("purpose"));
            dobj.setBodyBuilding(rs.getString("body_building"));
            dobj.setTemp_regn_type(rs.getString("temp_regn_type"));
        }

        return dobj;
    }

    public Owner_dobj fillFrom_Va_OwnerDobj(RowSet rsOwner) throws SQLException {
        Owner_dobj owner_dobj = null;
        TransactionManager tmgrOwner = null;
        RowSet rsOwnerId = null;
        try {
            if (rsOwner.next()) // found
            {
                owner_dobj = new Owner_dobj();
                OwnerIdentificationDobj own_identity = new OwnerIdentificationDobj();
                owner_dobj.setOwner_identity(own_identity);
                owner_dobj.setState_cd(rsOwner.getString("state_cd"));
                owner_dobj.setOff_cd(rsOwner.getInt("off_cd"));
                owner_dobj.setAppl_no(rsOwner.getString("appl_no"));
                owner_dobj.setRegn_no(rsOwner.getString("regn_no"));
                owner_dobj.setRegn_dt(rsOwner.getDate("regn_dt"));
                owner_dobj.setPurchase_dt(rsOwner.getDate("purchase_dt"));
                owner_dobj.setOwner_sr(rsOwner.getInt("owner_sr"));
                owner_dobj.setOwner_name(rsOwner.getString("owner_name"));
                owner_dobj.setF_name(rsOwner.getString("f_name"));
                owner_dobj.setC_add1(rsOwner.getString("c_add1"));
                owner_dobj.setC_add2(rsOwner.getString("c_add2"));
                owner_dobj.setC_add3(rsOwner.getString("c_add3"));
                owner_dobj.setC_district(rsOwner.getInt("c_district"));
                owner_dobj.setC_pincode(rsOwner.getInt("c_pincode"));
                owner_dobj.setC_state(rsOwner.getString("c_state"));
                owner_dobj.setP_add1(rsOwner.getString("p_add1"));
                owner_dobj.setP_add2(rsOwner.getString("p_add2"));
                owner_dobj.setP_add3(rsOwner.getString("p_add3"));
                owner_dobj.setP_district(rsOwner.getInt("p_district"));
                owner_dobj.setP_pincode(rsOwner.getInt("p_pincode"));
                owner_dobj.setP_state(rsOwner.getString("p_state"));
                owner_dobj.setOwner_cd(rsOwner.getInt("owner_cd"));
                owner_dobj.setRegn_type(rsOwner.getString("regn_type"));
                owner_dobj.setVh_class(rsOwner.getInt("vh_class"));
                owner_dobj.setChasi_no(rsOwner.getString("chasi_no"));
                owner_dobj.setEng_no(rsOwner.getString("eng_no"));
                owner_dobj.setMaker(rsOwner.getInt("maker"));
                owner_dobj.setMaker_model(rsOwner.getString("maker_model"));
                owner_dobj.setBody_type(rsOwner.getString("body_type"));
                owner_dobj.setNo_cyl(rsOwner.getInt("no_cyl"));
                owner_dobj.setHp(rsOwner.getFloat("hp"));
                owner_dobj.setSeat_cap(rsOwner.getInt("seat_cap"));
                owner_dobj.setStand_cap(rsOwner.getInt("stand_cap"));
                owner_dobj.setSleeper_cap(rsOwner.getInt("sleeper_cap"));
                owner_dobj.setUnld_wt(rsOwner.getInt("unld_wt"));
                owner_dobj.setLd_wt(rsOwner.getInt("ld_wt"));
                owner_dobj.setGcw(rsOwner.getInt("gcw"));
                owner_dobj.setFuel(rsOwner.getInt("fuel"));
                owner_dobj.setColor(rsOwner.getString("color"));
                owner_dobj.setManu_mon(rsOwner.getInt("manu_mon"));
                owner_dobj.setManu_yr(rsOwner.getInt("manu_yr"));
                owner_dobj.setNorms(rsOwner.getInt("norms"));
                owner_dobj.setWheelbase(rsOwner.getInt("wheelbase"));
                owner_dobj.setCubic_cap(rsOwner.getFloat("cubic_cap"));
                owner_dobj.setFloor_area(rsOwner.getFloat("floor_area"));
                owner_dobj.setAc_fitted(rsOwner.getString("ac_fitted"));
                owner_dobj.setAudio_fitted(rsOwner.getString("audio_fitted"));
                owner_dobj.setVideo_fitted(rsOwner.getString("video_fitted"));
                owner_dobj.setVch_purchase_as(rsOwner.getString("vch_purchase_as"));
                owner_dobj.setVch_catg(rsOwner.getString("vch_catg"));
                owner_dobj.setDealer_cd(rsOwner.getString("dealer_cd"));
                owner_dobj.setSale_amt(rsOwner.getInt("sale_amt"));
                owner_dobj.setLaser_code(rsOwner.getString("laser_code"));
                owner_dobj.setGarage_add(rsOwner.getString("garage_add"));
                owner_dobj.setLength(rsOwner.getInt("length"));
                owner_dobj.setWidth(rsOwner.getInt("width"));
                owner_dobj.setHeight(rsOwner.getInt("height"));
                owner_dobj.setRegn_upto(rsOwner.getDate("regn_upto"));
                owner_dobj.setFit_upto(rsOwner.getDate("fit_upto"));
                owner_dobj.setAnnual_income(rsOwner.getInt("annual_income"));
                owner_dobj.setImported_vch(rsOwner.getString("imported_vch"));
                owner_dobj.setOther_criteria(rsOwner.getInt("other_criteria"));
                owner_dobj.setPmt_type(rsOwner.getInt("pmt_type"));
                owner_dobj.setPmt_catg(rsOwner.getInt("pmt_catg"));
                owner_dobj.setRqrd_tax_modes(rsOwner.getString("rqrd_tax_modes"));
                owner_dobj.setOp_dt(rsOwner.getString("op_dt"));
                owner_dobj.setVehType(ServerUtil.VehicleClassType(owner_dobj.getVh_class()));
                if (CommonUtils.isColumnExist(rsOwner, "link_regn_no") && rsOwner.getString("link_regn_no") != null) {
                    owner_dobj.setLinkedRegnNo(rsOwner.getString("link_regn_no"));
                }
                if (Util.getUserStateCode() != null && Util.getUserStateCode().equals("RJ")) {
                    String reqdTaxMode = owner_dobj.getRqrd_tax_modes();
                    if (reqdTaxMode != null) {
                        String taxMode = ServerUtil.getTaxModeFromReqdTaxMode(reqdTaxMode);
                        owner_dobj.setTax_mode(taxMode);
                    }
                }
                owner_dobj.setC_district_name(rsOwner.getString("current_district_name"));
                owner_dobj.setC_state_name(rsOwner.getString("current_state_name"));
                owner_dobj.setModelNameOnTAC(ServerUtil.getMakerModelName(rsOwner.getString("maker_model")));

                tmgrOwner = new TransactionManager("Owner_Id");
                String sql = "SELECT * FROM " + TableList.VA_OWNER_IDENTIFICATION
                        + " where appl_no =?";
                PreparedStatement ps = tmgrOwner.prepareStatement(sql);
                ps.setString(1, owner_dobj.getAppl_no());
                rsOwnerId = tmgrOwner.fetchDetachedRowSet_No_release();
                own_identity = fillOwnerIdentityDobj(rsOwnerId);
                own_identity.setAppl_no(owner_dobj.getAppl_no());
                owner_dobj.setOwner_identity(own_identity);
                String sqll = "SELECT * FROM " + TableList.VA_OWNER_OTHER
                        + " where appl_no =?";
                ps = tmgrOwner.prepareStatement(sqll);
                ps.setString(1, owner_dobj.getAppl_no());
                rsOwnerId = tmgrOwner.fetchDetachedRowSet_No_release();
                if (rsOwnerId.next()) {
                    owner_dobj = fillVaOwnerOther(owner_dobj, rsOwnerId);
                }
                if (owner_dobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_TEMPORARY)) {
                    sql = "SELECT * FROM " + TableList.VA_TMP_REGN_DTL + " where appl_no =?";
                    ps = tmgrOwner.prepareStatement(sql);
                    ps.setString(1, owner_dobj.getAppl_no());
                    rsOwnerId = tmgrOwner.fetchDetachedRowSet_No_release();

                    if (rsOwnerId.next()) {
                        TempRegDobj dobj = new TempRegDobj();
                        dobj.setAppl_no(rsOwnerId.getString("appl_no"));
                        dobj.setRegn_no(rsOwnerId.getString("regn_no"));
                        dobj.setTmp_off_cd(rsOwnerId.getInt("tmp_off_cd"));
                        dobj.setRegn_auth(rsOwnerId.getString("regn_auth"));
                        dobj.setTmp_state_cd(rsOwnerId.getString("tmp_state_cd"));
                        dobj.setTmp_regn_no(rsOwnerId.getString("tmp_regn_no"));
                        dobj.setTmp_regn_dt(rsOwnerId.getDate("tmp_regn_dt"));
                        dobj.setTmp_valid_upto(rsOwnerId.getDate("tmp_valid_upto"));
                        dobj.setDealer_cd(rsOwnerId.getString("dealer_cd"));
                        owner_dobj.setTempReg(dobj);
                    }

                }

                if (owner_dobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_CD)) {
                    CdImpl cdImpl = new CdImpl();
                    CdDobj cdDobj = cdImpl.getVaCdDobj(owner_dobj.getAppl_no());
                    owner_dobj.setCdDobj(cdDobj);
                }

                OtherStateVehImpl otherStateVehImpl = new OtherStateVehImpl();
                OtherStateVehDobj otherStateVehDobj = otherStateVehImpl.setOtherVehicleDetailsToDobj(owner_dobj.getAppl_no());
                owner_dobj.setOtherStateVehDobj(otherStateVehDobj);

                sql = "SELECT * FROM " + TableList.VA_SPEED_GOVERNOR + " where appl_no =?";
                ps = tmgrOwner.prepareStatement(sql);
                ps.setString(1, owner_dobj.getAppl_no());
                RowSet rs = tmgrOwner.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    SpeedGovernorDobj sgDobj = new SpeedGovernorDobj();
                    sgDobj.setAppl_no(rs.getString("appl_no"));
                    sgDobj.setState_cd(rs.getString("state_cd"));
                    sgDobj.setOff_cd(rs.getInt("off_cd"));
                    sgDobj.setRegn_no(rs.getString("regn_no"));
                    sgDobj.setSg_no(rs.getString("sg_no"));
                    sgDobj.setSg_fitted_at(rs.getString("sg_fitted_at"));
                    sgDobj.setSg_fitted_on(rs.getDate("sg_fitted_on"));

                    sgDobj.setSgGovType(rs.getInt("sg_type"));
                    sgDobj.setSgTypeApprovalNo(rs.getString("sg_type_approval_no"));
                    sgDobj.setSgTestReportNo(rs.getString("sg_test_report_no"));
                    sgDobj.setSgFitmentCerticateNo(rs.getString("sg_fitment_cert_no"));

                    owner_dobj.setSpeedGovernorDobj(sgDobj);
                }

                FitnessImpl fitImpl = new FitnessImpl();
                ReflectiveTapeDobj refDobj = fitImpl.getVaReflectiveTapeDobj(owner_dobj.getAppl_no(), tmgrOwner);
                owner_dobj.setReflectiveTapeDobj(refDobj);

                ExArmyDobj exArmy = ExArmyImpl.setExArmyDetails_db_to_dobj(rsOwner.getString("appl_no"), null, null, 0);
                owner_dobj.setExArmy_dobj(exArmy);

                InsDobj insDobj = InsImpl.set_ins_dtls_db_to_dobj(null, rsOwner.getString("appl_no"), null, 0);
                owner_dobj.setInsDobj(insDobj);

                AxleDetailsDobj axleDetailsDobj = AxleImpl.setAxleVehDetails_db_to_dobj(rsOwner.getString("appl_no"), null, null, 0);
                if (axleDetailsDobj != null) {
                    owner_dobj.setAxleDobj(axleDetailsDobj);
                    owner_dobj.setNumberOfTyres(axleDetailsDobj.getTf_Front_tyre() + axleDetailsDobj.getTf_Other_tyre() + axleDetailsDobj.getTf_Rear_tyre() + axleDetailsDobj.getTf_Tandem_tyre());
                }
                if ("DL".equals(owner_dobj.getState_cd()) && owner_dobj.getVh_class() == TableConstants.ERICKSHAW_VCH_CLASS) {
                    PendencyBankDobj dobj = new PendencyBankDetailImpl().getBankSubsidyData(rsOwner.getString("appl_no"), rsOwner.getString("state_cd"), rsOwner.getInt("off_cd"));
                    if (dobj != null) {
                        owner_dobj.getOwner_identity().setAadhar_no(dobj.getAadharNo());
                    }
                }
                // auction Details
                AuctionDobj auctionDobj = new AuctionImpl().getDetailsFromVtAuction(owner_dobj.getChasi_no().trim().toUpperCase(), null);
                owner_dobj.setAuctionDobj(auctionDobj);

                FasTagDetailsDobj fasTagDobj = FasTagImpl.getFasTagDetails(owner_dobj.getChasi_no(), null, owner_dobj.getRegn_type());
                if (fasTagDobj != null) {
                    owner_dobj.setFasTagId(fasTagDobj.getTagid());
                }
            }
        } catch (VahanException e) {
        } catch (Exception e) {
            LOGGER.error(owner_dobj == null ? "" : owner_dobj.getAppl_no() + "-" + e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgrOwner != null) {
                try {
                    tmgrOwner.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }

        return owner_dobj;
    }

    public Owner_dobj fillFrom_VT_OwnerDobj(RowSet rsOwner) throws SQLException, VahanException {
        Owner_dobj owner_dobj = null;
        TransactionManager tmgrOwner = null;
        RowSet rs = null;
        try {
            if (rsOwner.next()) // found
            {
                owner_dobj = new Owner_dobj();

                owner_dobj.setState_cd(rsOwner.getString("state_cd"));
                owner_dobj.setOff_cd(rsOwner.getInt("off_cd"));
                owner_dobj.setRegn_no(rsOwner.getString("regn_no"));
                owner_dobj.setRegn_dt(rsOwner.getDate("regn_dt"));
                owner_dobj.setPurchase_dt(rsOwner.getDate("purchase_dt"));
                owner_dobj.setOwner_sr(rsOwner.getInt("owner_sr"));
                owner_dobj.setOwner_name(rsOwner.getString("owner_name"));
                owner_dobj.setF_name(rsOwner.getString("f_name"));
                owner_dobj.setC_add1(rsOwner.getString("c_add1"));
                owner_dobj.setC_add2(rsOwner.getString("c_add2"));
                owner_dobj.setC_add3(rsOwner.getString("c_add3"));
                owner_dobj.setC_district(rsOwner.getInt("c_district"));
                owner_dobj.setC_pincode(rsOwner.getInt("c_pincode"));
                owner_dobj.setC_state(rsOwner.getString("c_state"));
                owner_dobj.setP_add1(rsOwner.getString("p_add1"));
                owner_dobj.setP_add2(rsOwner.getString("p_add2"));
                owner_dobj.setP_add3(rsOwner.getString("p_add3"));
                owner_dobj.setP_district(rsOwner.getInt("p_district"));
                owner_dobj.setP_pincode(rsOwner.getInt("p_pincode"));
                owner_dobj.setP_state(rsOwner.getString("p_state"));
                owner_dobj.setOwner_cd(rsOwner.getInt("owner_cd"));
                owner_dobj.setRegn_type(rsOwner.getString("regn_type"));
                owner_dobj.setVh_class(rsOwner.getInt("vh_class"));
                owner_dobj.setChasi_no(rsOwner.getString("chasi_no"));
                owner_dobj.setEng_no(rsOwner.getString("eng_no"));
                owner_dobj.setMaker(rsOwner.getInt("maker"));
                owner_dobj.setMaker_model(rsOwner.getString("maker_model"));
                owner_dobj.setBody_type(rsOwner.getString("body_type"));
                owner_dobj.setNo_cyl(rsOwner.getInt("no_cyl"));
                owner_dobj.setHp(rsOwner.getFloat("hp"));
                owner_dobj.setSeat_cap(rsOwner.getInt("seat_cap"));
                owner_dobj.setStand_cap(rsOwner.getInt("stand_cap"));
                owner_dobj.setSleeper_cap(rsOwner.getInt("sleeper_cap"));
                owner_dobj.setUnld_wt(rsOwner.getInt("unld_wt"));
                owner_dobj.setLd_wt(rsOwner.getInt("ld_wt"));
                owner_dobj.setGcw(rsOwner.getInt("gcw"));
                owner_dobj.setFuel(rsOwner.getInt("fuel"));
                owner_dobj.setColor(rsOwner.getString("color"));
                owner_dobj.setManu_mon(rsOwner.getInt("manu_mon"));
                owner_dobj.setManu_yr(rsOwner.getInt("manu_yr"));
                owner_dobj.setNorms(rsOwner.getInt("norms"));
                owner_dobj.setWheelbase(rsOwner.getInt("wheelbase"));
                owner_dobj.setCubic_cap(rsOwner.getFloat("cubic_cap"));
                owner_dobj.setFloor_area(rsOwner.getFloat("floor_area"));
                owner_dobj.setAc_fitted(rsOwner.getString("ac_fitted"));
                owner_dobj.setAudio_fitted(rsOwner.getString("audio_fitted"));
                owner_dobj.setVideo_fitted(rsOwner.getString("video_fitted"));
                owner_dobj.setVch_purchase_as(rsOwner.getString("vch_purchase_as"));
                owner_dobj.setVch_catg(rsOwner.getString("vch_catg"));
                owner_dobj.setDealer_cd(rsOwner.getString("dealer_cd"));
                owner_dobj.setSale_amt(rsOwner.getInt("sale_amt"));
                owner_dobj.setLaser_code(rsOwner.getString("laser_code"));
                owner_dobj.setGarage_add(rsOwner.getString("garage_add"));
                owner_dobj.setLength(rsOwner.getInt("length"));
                owner_dobj.setWidth(rsOwner.getInt("width"));
                owner_dobj.setHeight(rsOwner.getInt("height"));
                owner_dobj.setRegn_upto(rsOwner.getDate("regn_upto"));
                owner_dobj.setFit_upto(rsOwner.getDate("fit_upto"));
                owner_dobj.setAnnual_income(rsOwner.getInt("annual_income"));
                owner_dobj.setImported_vch(rsOwner.getString("imported_vch"));
                owner_dobj.setOther_criteria(rsOwner.getInt("other_criteria"));
                owner_dobj.setStatus(rsOwner.getString("status"));
                owner_dobj.setOp_dt(rsOwner.getString("op_dt"));
                owner_dobj.setVehType(ServerUtil.VehicleClassType(owner_dobj.getVh_class()));
                owner_dobj.setC_district_name(rsOwner.getString("current_district_name"));
                owner_dobj.setC_state_name(rsOwner.getString("current_state_name"));
                tmgrOwner = new TransactionManager("Owner_Id");
                String sql = "SELECT * FROM " + TableList.VT_OWNER_IDENTIFICATION
                        + " where regn_no =? and state_cd=? and off_cd=?";
                PreparedStatement ps = tmgrOwner.prepareStatement(sql);
                ps.setString(1, owner_dobj.getRegn_no());
                ps.setString(2, owner_dobj.getState_cd());
                ps.setInt(3, owner_dobj.getOff_cd());
                rs = tmgrOwner.fetchDetachedRowSet_No_release();
                OwnerIdentificationDobj own_identity = fillOwnerIdentityDobj(rs);
                if (own_identity != null) {
                    own_identity.setAppl_no(owner_dobj.getAppl_no());
                    owner_dobj.setOwner_identity(own_identity);
                }

                sql = "SELECT state_cd, off_cd, regn_no, sg_no, sg_fitted_on, sg_fitted_at, op_dt, emp_cd,"
                        + "sg_type,sg_type_approval_no,sg_test_report_no,sg_fitment_cert_no "
                        + "  FROM vt_speed_governor where regn_no=? and state_cd=? and off_cd=? ";
                ps = tmgrOwner.prepareStatement(sql);
                ps.setString(1, owner_dobj.getRegn_no());
                ps.setString(2, owner_dobj.getState_cd());
                ps.setInt(3, owner_dobj.getOff_cd());
                rs = tmgrOwner.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    SpeedGovernorDobj spDobj = new SpeedGovernorDobj();
                    spDobj.setState_cd(rs.getString("state_cd"));
                    spDobj.setOff_cd(rs.getInt("off_cd"));
                    spDobj.setRegn_no(rs.getString("regn_no"));
                    spDobj.setSg_no(rs.getString("sg_no"));
                    spDobj.setSg_fitted_on(rs.getDate("sg_fitted_on"));
                    spDobj.setSg_fitted_at(rs.getString("sg_fitted_at"));

                    spDobj.setSgGovType(rs.getInt("sg_type"));
                    spDobj.setSgTypeApprovalNo(rs.getString("sg_type_approval_no"));
                    spDobj.setSgTestReportNo(rs.getString("sg_test_report_no"));
                    spDobj.setSgFitmentCerticateNo(rs.getString("sg_fitment_cert_no"));
                    owner_dobj.setSpeedGovernorDobj(spDobj);
                }

                FitnessImpl fitImpl = new FitnessImpl();
                ReflectiveTapeDobj refDobj = fitImpl.getVtReflectiveTapeDobj(owner_dobj.getRegn_no(), owner_dobj.getState_cd(), owner_dobj.getOff_cd(), tmgrOwner);
                owner_dobj.setReflectiveTapeDobj(refDobj);

                AxleDetailsDobj axleDetailsDobj = AxleImpl.setAxleVehDetails_db_to_dobj(null, owner_dobj.getRegn_no(), owner_dobj.getState_cd(), owner_dobj.getOff_cd());
                if (axleDetailsDobj != null) {
                    owner_dobj.setAxleDobj(axleDetailsDobj);
                    owner_dobj.setNumberOfTyres(axleDetailsDobj.getTf_Front_tyre() + axleDetailsDobj.getTf_Other_tyre() + axleDetailsDobj.getTf_Rear_tyre() + axleDetailsDobj.getTf_Tandem_tyre());
                }
                InsDobj insDobj = InsImpl.set_ins_dtls_db_to_dobj(owner_dobj.getRegn_no(), null, owner_dobj.getState_cd(), owner_dobj.getOff_cd());
                if (insDobj != null) {
                    owner_dobj.setInsDobj(insDobj);
                }
                // auction Details
                AuctionDobj auctionDobj = new AuctionImpl().getDetailsFromVtAuction(owner_dobj.getChasi_no().trim().toUpperCase(), null);
                owner_dobj.setAuctionDobj(auctionDobj);
            }
        } finally {
            if (tmgrOwner != null) {
                try {
                    tmgrOwner.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }

        return owner_dobj;
    }

    public OwnerIdentificationDobj fillOwnerIdentityDobj(RowSet rsOwnerId) throws SQLException {
        OwnerIdentificationDobj dobj = new OwnerIdentificationDobj();

        if (rsOwnerId.next()) {
            dobj.setState_cd(rsOwnerId.getString("state_cd"));
            dobj.setOff_cd(rsOwnerId.getInt("off_cd"));
            dobj.setRegn_no(rsOwnerId.getString("regn_no"));
            dobj.setMobile_no(rsOwnerId.getLong("mobile_no"));
            dobj.setEmail_id(rsOwnerId.getString("email_id"));
            dobj.setPan_no(rsOwnerId.getString("pan_no"));
            dobj.setAadhar_no(rsOwnerId.getString("aadhar_no"));
            dobj.setPassport_no(rsOwnerId.getString("passport_no"));
            dobj.setRation_card_no(rsOwnerId.getString("ration_card_no"));
            dobj.setVoter_id(rsOwnerId.getString("voter_id"));
            dobj.setDl_no(rsOwnerId.getString("dl_no"));
            dobj.setVerified_on(rsOwnerId.getDate("verified_on"));
            dobj.setOwnerCatg(rsOwnerId.getInt("owner_ctg"));
            dobj.setOwnerCdDept(rsOwnerId.getInt("dept_cd"));
        }
        return dobj;
    }

    public static Map getDealerDetail(long userCode) throws VahanException {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        Map owner_map = new HashMap();

        try {
            String stateCd = Util.getUserStateCode();
            if (stateCd == null || Util.getUserSeatOffCode() == null) {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
            int offCd = Util.getUserSeatOffCode();
            tmgr = new TransactionManager("getMakerAndDealerCode");

            if (ServerUtil.validateDealerUserForAllOffice(userCode)) {
                sql = "select m.dealer_name , m.dealer_cd \n"
                        + "from  vm_dealer_mast m left outer join tm_user_permissions up\n"
                        + "on m.dealer_cd = up.dealer_cd \n"
                        + "where m.state_cd = ? and up.user_cd = ? ";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setLong(2, userCode);
            } else {
                sql = "select m.dealer_name , m.dealer_cd \n"
                        + "from  vm_dealer_mast m left outer join tm_user_permissions up\n"
                        + "on m.dealer_cd = up.dealer_cd \n"
                        + "where m.state_cd = ? and m.off_cd = ? and up.user_cd = ? ";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setInt(2, offCd);
                ps.setLong(3, userCode);
            }
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {
                if (rs.getString("dealer_cd") != null && !rs.getString("dealer_cd").isEmpty()) {
                    owner_map.put("dealer_cd", rs.getString("dealer_cd"));
                }
                if (rs.getString("dealer_name") != null && !rs.getString("dealer_name").isEmpty()) {
                    owner_map.put("dealer_name", rs.getString("dealer_name"));
                }
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
        return owner_map;
    }

    public static List<SelectItem> getAssignedMakerList(long userCode) throws VahanException {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        List<SelectItem> list = null;

        try {
            tmgr = new TransactionManager("getMakerAndDealerCode");

            sql = "select maker from tm_user_permissions where user_cd=? ";

            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, userCode);

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) {
                String makerPerm = rs.getString("maker");
                if (makerPerm == null || makerPerm.trim().equals("")) {
                    throw new VahanException("No maker entry is assigned to user");
                }
                list = new ArrayList<>();
                sql = "Select * from " + TableList.VM_MAKER;
                if (!makerPerm.equals("ANY")) {
                    sql = sql + " WHERE code in (" + makerPerm + ")";
                }
                tmgr.prepareStatement(sql);
                rs = tmgr.fetchDetachedRowSet_No_release();
                while (rs.next()) {
                    SelectItem item = new SelectItem();
                    item.setValue(rs.getInt("code"));
                    item.setLabel(rs.getString("descr"));
                    list.add(item);
                }
            } else {
                throw new VahanException("No maker entry is assigned to user");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting the Maker List, please try after sometime or contact Administrator.");
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

    public List<OwnerDetailsDobj> getAllRegnNoFromAllStatesAndRto(String regnNO) {
        TransactionManager tmgr = null;
        String whereiam = "getAllRegnNoFromAllStatesAndRto()";
        List<OwnerDetailsDobj> list = new ArrayList<OwnerDetailsDobj>();
        PreparedStatement psmt = null;
        String sql = "select regn_no, owner_name,a.state_cd,a.off_cd ,e.descr AS stateName , f.off_name as offName, owner_sr "
                + " from " + TableList.VIEW_VV_OWNER + " a "
                + " LEFT JOIN " + TableList.TM_STATE + " e ON e.state_code = a.state_cd::bpchar "
                + " LEFT JOIN " + TableList.TM_OFFICE + " f ON f.off_cd = a.off_cd AND f.state_cd = a.state_cd::bpchar "
                + " where regn_no = ?";
        try {
            tmgr = new TransactionManager(whereiam);
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, regnNO);
            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                OwnerDetailsDobj dobj = new OwnerDetailsDobj();
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setState_name(rs.getString("stateName"));
                dobj.setOff_name(rs.getString("offName"));
                dobj.setOwner_sr(rs.getInt("owner_sr"));
                list.add(dobj);
            }
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return list;
    }

    public List<OwnerDetailsDobj> getRegnFromChassisNo(String chassisNo) {
        TransactionManagerReadOnly tmgr = null;
        List<OwnerDetailsDobj> list = new ArrayList<OwnerDetailsDobj>();
        PreparedStatement psmt = null;

        String sql = "select regn_no, owner_name,a.state_cd,a.off_cd ,e.descr AS stateName , f.off_name as offName  "
                + " from " + TableList.VIEW_VV_OWNER + " a "
                + " LEFT JOIN " + TableList.TM_STATE + " e ON e.state_code = a.state_cd::bpchar "
                + " LEFT JOIN " + TableList.TM_OFFICE + " f ON f.off_cd = a.off_cd AND f.state_cd = a.state_cd::bpchar "
                + "  where chasi_no = ?";
        try {
            tmgr = new TransactionManagerReadOnly("OwnerImpl.getRegnFromChassisNo");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, chassisNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            int i = 1;
            while (rs.next()) {
                OwnerDetailsDobj dobj = new OwnerDetailsDobj();
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setState_name(rs.getString("stateName"));
                dobj.setOff_name(rs.getString("offName"));
                list.add(dobj);
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return list;
    }

    public List<OwnerDetailsDobj> getRegnFromEngNo(String engNo) {
        TransactionManagerReadOnly tmgr = null;
        List<OwnerDetailsDobj> list = new ArrayList<OwnerDetailsDobj>();
        PreparedStatement psmt = null;

        String sql = "select regn_no, owner_name,a.state_cd,a.off_cd ,e.descr AS stateName , f.off_name as offName  "
                + " from " + TableList.VIEW_VV_OWNER + " a "
                + " LEFT JOIN " + TableList.TM_STATE + " e ON e.state_code = a.state_cd::bpchar "
                + " LEFT JOIN " + TableList.TM_OFFICE + " f ON f.off_cd = a.off_cd AND f.state_cd = a.state_cd::bpchar "
                + "  where eng_no = ?";
        //String sql = "select regn_no , owner_name from " + TableList.VIEW_VV_OWNER + "  where eng_no = ? ";

        try {
            tmgr = new TransactionManagerReadOnly("OwnerImpl.getRegnFromEngNo");
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, engNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            int i = 1;
            while (rs.next()) {
                OwnerDetailsDobj dobj = new OwnerDetailsDobj();
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setState_name(rs.getString("stateName"));
                dobj.setOff_name(rs.getString("offName"));
                list.add(dobj);
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return list;
    }

    /**
     *
     * @param regnNo
     * @param off_cd
     * @return
     * @throws VahanException
     */
    public OwnerDetailsDobj getOwnerDetails(String regnNo, String state_cd, String off_cd) throws VahanException {
        OwnerDetailsDobj dobj = null;
        String sql = " SELECT owner.*,type.descr as regn_type_descr,owcode.descr as owner_cd_descr "
                + " FROM " + TableList.VIEW_VV_OWNER + " owner "
                + " left join vm_regn_type type on owner.regn_type=type.regn_typecode "
                + " left join vm_owcode owcode on owner.owner_cd = owcode.ow_code "
                + " WHERE regn_no =? and owner.state_cd = ? ";
        if (off_cd.split(",").length > 1) {
            sql += "and owner.off_cd in (" + off_cd + ")";//for permit Different Off(RTO_CD). NAMAN JAIN
        } else {
            sql += "and owner.off_cd = ? ";
        }
        TransactionManager tmgr = null;
        PreparedStatement ps = null;

        try {
            //add Top Dispose
            tmgr = new TransactionManager("getOwnerDetails");
            TmConfigurationDobj tm_Cong = Util.getTmConfiguration();
            boolean allow_fitness_all_rto = tm_Cong.isAllow_fitness_all_RTO();

            if (allow_fitness_all_rto && !(off_cd.split(",").length > 1)) {
                String query = "select off_cd from  " + TableList.VT_OWNER + " where regn_no=? and state_cd = ?";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, regnNo);
                ps.setString(2, Util.getUserStateCode());
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    int off_cd_other_vechile = rs.getInt("off_cd");
                    if (off_cd_other_vechile != Integer.parseInt(off_cd)) {
                        off_cd = off_cd_other_vechile + "";
                    }

                }

            }
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, state_cd);
            if (!(off_cd.split(",").length > 1)) {
                ps.setInt(3, Integer.valueOf(off_cd));
            }
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new OwnerDetailsDobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setState_name(rs.getString("state_name"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setOff_name(rs.getString("off_name"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setRegn_dt(rs.getString("regn_dt"));
                dobj.setRegnDateDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getRegn_dt())));
                dobj.setPurchase_dt(rs.getString("purchase_dt"));
                dobj.setPurchaseDateDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getPurchase_dt())));
                dobj.setOwner_sr(rs.getInt("owner_sr"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setF_name(rs.getString("f_name"));
                dobj.setC_add1(rs.getString("c_add1"));
                dobj.setC_add2(rs.getString("c_add2"));
                dobj.setC_add3(rs.getString("c_add3"));
                dobj.setC_district(rs.getInt("c_district"));
                dobj.setC_district_name(rs.getString("c_district_name"));
                dobj.setC_state(rs.getString("c_state"));
                dobj.setC_state_name(rs.getString("c_state_name"));
                dobj.setC_pincode(rs.getInt("c_pincode"));
                dobj.setP_add1(rs.getString("p_add1"));
                dobj.setP_add2(rs.getString("p_add2"));
                dobj.setP_add3(rs.getString("p_add3"));
                dobj.setP_district(rs.getInt("p_district"));
                dobj.setP_district_name(rs.getString("p_district_name"));
                dobj.setP_state(rs.getString("p_state"));
                dobj.setP_state_name(rs.getString("p_state_name"));
                dobj.setP_pincode(rs.getInt("p_pincode"));
                dobj.setOwner_cd(rs.getInt("owner_cd"));
                dobj.setOwner_cd_descr(rs.getString("owner_cd_descr"));
                dobj.setRegn_type(rs.getString("regn_type"));
                dobj.setRegn_type_descr(rs.getString("regn_type_descr"));
                dobj.setVh_class(rs.getInt("vh_class"));
                dobj.setVh_class_desc(rs.getString("vh_class_desc"));
                dobj.setChasi_no(rs.getString("chasi_no"));
                dobj.setEng_no(rs.getString("eng_no"));
                dobj.setMaker(rs.getInt("maker"));
                dobj.setMaker_name(rs.getString("maker_name"));
                dobj.setModel_cd(rs.getString("model_cd"));
                dobj.setModel_name(rs.getString("model_name"));
                dobj.setBody_type(rs.getString("body_type"));
                dobj.setNo_cyl(rs.getInt("no_cyl"));
                dobj.setHp(rs.getFloat("hp"));
                dobj.setSeat_cap(rs.getInt("seat_cap"));
                dobj.setStand_cap(rs.getInt("stand_cap"));
                dobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                dobj.setUnld_wt(rs.getInt("unld_wt"));
                dobj.setLd_wt(rs.getInt("ld_wt"));
                dobj.setGcw(rs.getInt("gcw"));
                dobj.setFuel(rs.getInt("fuel"));
                dobj.setFuel_descr(rs.getString("fuel_descr"));
                dobj.setColor(rs.getString("color"));
                dobj.setManu_mon(rs.getInt("manu_mon"));
                dobj.setManu_yr(rs.getInt("manu_yr"));
                dobj.setNorms(rs.getInt("norms"));
                dobj.setNorms_descr(rs.getString("norms_descr"));
                dobj.setWheelbase(rs.getInt("wheelbase"));
                dobj.setCubic_cap(rs.getFloat("cubic_cap"));
                dobj.setFloor_area(rs.getFloat("floor_area"));
                dobj.setAc_fitted(rs.getString("ac_fitted"));
                dobj.setAudio_fitted(rs.getString("audio_fitted"));
                dobj.setVideo_fitted(rs.getString("video_fitted"));
                dobj.setVch_purchase_as_code(rs.getString("vch_purchase_as"));
                dobj.setVch_catg(rs.getString("vch_catg"));
                dobj.setDealer_cd(rs.getString("dealer_cd"));
                dobj.setDlr_name(rs.getString("dlr_name"));
                dobj.setDlr_add1(rs.getString("dlr_add1"));
                dobj.setDlr_add2(rs.getString("dlr_add2"));
                dobj.setDlr_add3(rs.getString("dlr_add3"));
                dobj.setDlr_city(rs.getString("dlr_city"));
                dobj.setDlr_district(rs.getString("dlr_district"));
                dobj.setDlr_pincode(rs.getString("dlr_pincode"));
                dobj.setSale_amt(rs.getInt("sale_amt"));
                dobj.setLaser_code(rs.getString("laser_code"));
                dobj.setGarage_add(rs.getString("garage_add"));
                dobj.setLength(rs.getInt("length"));
                dobj.setWidth(rs.getInt("width"));
                dobj.setHeight(rs.getInt("height"));
                dobj.setRegn_upto(rs.getString("regn_upto"));
                dobj.setRegnUptoDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getRegn_upto())));
                dobj.setFit_upto(rs.getString("fit_upto"));
                dobj.setFitUptoDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getFit_upto())));
                dobj.setAnnual_income(rs.getInt("annual_income"));
                dobj.setOp_dt(rs.getString("op_dt"));
                dobj.setImported_vch(rs.getString("imported_vch"));
                dobj.setOther_criteria(rs.getInt("other_criteria"));
                dobj.setStatus(rs.getString("status"));

                if (ServerUtil.isTransport(dobj.getVh_class(), null)) {
                    dobj.setVehTypeDescr(TableConstants.VM_VEHTYPE_TRANSPORT_DESCR);
                    dobj.setVehType(TableConstants.VM_VEHTYPE_TRANSPORT);
                } else {
                    dobj.setVehTypeDescr(TableConstants.VM_VEHTYPE_NON_TRANSPORT_DESCR);
                    dobj.setVehType(TableConstants.VM_VEHTYPE_NON_TRANSPORT);
                }

                if (dobj.getVch_purchase_as_code().equalsIgnoreCase("B")) {
                    dobj.setVch_purchase_as("Fully Built");
                } else {
                    dobj.setVch_purchase_as("Drive Away Chasis");
                }

                //Setting Owenr Identification Details                
                String sqlOwnerId = "SELECT * FROM " + TableList.VT_OWNER_IDENTIFICATION
                        + " WHERE regn_no =? and state_cd = ? and off_cd  = ?";
                ps = tmgr.prepareStatement(sqlOwnerId);
                ps.setString(1, dobj.getRegn_no());
                ps.setString(2, Util.getUserStateCode());
                if (Util.getSelectedSeat() != null) {
                    ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                } else {
                    ps.setInt(3, Util.getUserOffCode());
                }
                rs = tmgr.fetchDetachedRowSet_No_release();
                OwnerIdentificationDobj own_identity = fillOwnerIdentityDobj(rs);
                if (own_identity != null) {
                    dobj.setOwnerIdentity(own_identity);
                }
            }
        } catch (VahanException vx) {
            throw vx;
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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

        return dobj;
    }

    public OwnerDetailsDobj getOwnerDetailsFromAppl(String applNo, String stateCD, int offCd) throws VahanException {
        OwnerDetailsDobj owDobj = null;
        TransactionManager tmgr = null;
        boolean newAppl = false;
        String where_condition = "";
        TmConfigurationDobj configurationDobj = Util.getTmConfiguration();
        if (!Util.getUserCategory().equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_USER) && !configurationDobj.isAllow_fitness_all_RTO()) {
            where_condition = "and va_status.off_cd=?";
        }

        try {
            String regnNo = null;

            tmgr = new TransactionManager("getOwnerDetailsFromAppl");
            String sql = "select va_status.pur_cd,regn_no from va_status,va_details"
                    + " where va_status.appl_no=va_details.appl_no "
                    + " and va_status.appl_no=? and va_status.state_cd=? " + where_condition + " ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, stateCD);
            if (!"".equalsIgnoreCase(where_condition)) {
                ps.setInt(3, offCd);
            }

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                if (rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                        || rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_TEMP_REG
                        || rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE
                        || rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE
                        || rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE_FITNESS) {
                    newAppl = true;
                    break;
                } else {
                    regnNo = rs.getString("regn_no");
                }
            }
            if (newAppl) {
                owDobj = getVaOwnerDetails(applNo, stateCD, offCd);
            } else {
                owDobj = getOwnerDetails(regnNo, stateCD, offCd);
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return owDobj;
    }

    public OwnerDetailsDobj getOwnerDetailsFromApplForRePostRC(String regn_no, String stateCD, int offCd) throws VahanException {
        OwnerDetailsDobj owDobj = null;
        TransactionManager tmgr = null;
        try {
            String regnNo = null;
            tmgr = new TransactionManager("getOwnerDetailsFromAppl");
            String sql = "select a.regn_no from " + TableList.VH_RCDISPATCH_RETURN + " a"
                    + " INNER JOIN " + TableList.VA_DETAILS + " b ON b.appl_no = a.appl_no and b.state_cd = a.state_cd and b.off_cd = a.off_cd and b.entry_status='A'"
                    + " where a.regn_no=? and a.state_cd=? and a.off_cd=? limit 1";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCD);
            ps.setInt(3, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                regnNo = rs.getString("regn_no");
            }
            sql = "select regn_no,appl_no from " + TableList.VA_DISPATCH + " where regn_no=? and state_cd=? and off_cd=? limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCD);
            ps.setInt(3, offCd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                throw new VahanException("Can't take Re-Postal Fee due to RC Dispatch is Pending for Registration No " + regn_no + " against Application No " + rs.getString("appl_no").toUpperCase());
            }
            owDobj = getOwnerDetails(regnNo, stateCD, offCd);

        } catch (VahanException ve) {
            throw new VahanException(ve.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return owDobj;
    }

    public OwnerDetailsDobj getOwnerDetails(String regnNo, String stateCd, int offCd) throws VahanException {
        OwnerDetailsDobj dobj = null;
        String where_condition = "";
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            TmConfigurationDobj tm_Cong = Util.getTmConfiguration();
            if (!Util.getUserCategory().equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_USER) && !tm_Cong.isAllow_fitness_all_RTO()) {
                where_condition = "and off_cd=?";
            }
            String sql = " SELECT owner.*,type.descr as regn_type_descr,owcode.descr as owner_cd_descr "
                    + " FROM " + TableList.VIEW_VV_OWNER + " owner "
                    + " left join vm_regn_type type on owner.regn_type=type.regn_typecode "
                    + " left join vm_owcode owcode on owner.owner_cd = owcode.ow_code "
                    + " WHERE regn_no =? and state_cd=? " + where_condition + " ";
            tmgr = new TransactionManager("getOwnerDetails");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            if (!"".equalsIgnoreCase(where_condition)) {
                ps.setInt(3, offCd);
            }
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new OwnerDetailsDobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setState_name(rs.getString("state_name"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setOff_name(rs.getString("off_name"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setRegn_dt(rs.getString("regn_dt"));
                dobj.setRegnDateDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getRegn_dt())));
                dobj.setPurchase_dt(rs.getString("purchase_dt"));
                dobj.setPurchaseDateDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getPurchase_dt())));
                dobj.setOwner_sr(rs.getInt("owner_sr"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setF_name(rs.getString("f_name"));
                dobj.setC_add1(rs.getString("c_add1"));
                dobj.setC_add2(rs.getString("c_add2"));
                dobj.setC_add3(rs.getString("c_add3"));
                dobj.setC_district(rs.getInt("c_district"));
                dobj.setC_district_name(rs.getString("c_district_name"));
                dobj.setC_state(rs.getString("c_state"));
                dobj.setC_state_name(rs.getString("c_state_name"));
                dobj.setC_pincode(rs.getInt("c_pincode"));
                dobj.setP_add1(rs.getString("p_add1"));
                dobj.setP_add2(rs.getString("p_add2"));
                dobj.setP_add3(rs.getString("p_add3"));
                dobj.setP_district(rs.getInt("p_district"));
                dobj.setP_district_name(rs.getString("p_district_name"));
                dobj.setP_state(rs.getString("p_state"));
                dobj.setP_state_name(rs.getString("p_state_name"));
                dobj.setP_pincode(rs.getInt("p_pincode"));
                dobj.setOwner_cd(rs.getInt("owner_cd"));
                dobj.setOwner_cd_descr(rs.getString("owner_cd_descr"));
                dobj.setRegn_type(rs.getString("regn_type"));
                dobj.setRegn_type_descr(rs.getString("regn_type_descr"));
                dobj.setVh_class(rs.getInt("vh_class"));
                dobj.setVh_class_desc(rs.getString("vh_class_desc"));
                dobj.setChasi_no(rs.getString("chasi_no"));
                dobj.setEng_no(rs.getString("eng_no"));
                dobj.setMaker(rs.getInt("maker"));
                dobj.setMaker_name(rs.getString("maker_name"));
                dobj.setModel_cd(rs.getString("model_cd"));
                dobj.setModel_name(rs.getString("model_name"));
                dobj.setBody_type(rs.getString("body_type"));
                dobj.setNo_cyl(rs.getInt("no_cyl"));
                dobj.setHp(rs.getFloat("hp"));
                dobj.setSeat_cap(rs.getInt("seat_cap"));
                dobj.setStand_cap(rs.getInt("stand_cap"));
                dobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                dobj.setUnld_wt(rs.getInt("unld_wt"));
                dobj.setLd_wt(rs.getInt("ld_wt"));
                dobj.setGcw(rs.getInt("gcw"));
                dobj.setFuel(rs.getInt("fuel"));
                dobj.setFuel_descr(rs.getString("fuel_descr"));
                dobj.setColor(rs.getString("color"));
                dobj.setManu_mon(rs.getInt("manu_mon"));
                dobj.setManu_yr(rs.getInt("manu_yr"));
                dobj.setNorms(rs.getInt("norms"));
                dobj.setNorms_descr(rs.getString("norms_descr"));
                dobj.setWheelbase(rs.getInt("wheelbase"));
                dobj.setCubic_cap(rs.getFloat("cubic_cap"));
                dobj.setFloor_area(rs.getFloat("floor_area"));
                dobj.setAc_fitted(rs.getString("ac_fitted"));
                dobj.setAudio_fitted(rs.getString("audio_fitted"));
                dobj.setVideo_fitted(rs.getString("video_fitted"));
                dobj.setVch_catg(rs.getString("vch_catg"));
                dobj.setDealer_cd(rs.getString("dealer_cd"));
                dobj.setDlr_name(rs.getString("dlr_name"));
                dobj.setDlr_add1(rs.getString("dlr_add1"));
                dobj.setDlr_add2(rs.getString("dlr_add2"));
                dobj.setDlr_add3(rs.getString("dlr_add3"));
                dobj.setDlr_city(rs.getString("dlr_city"));
                dobj.setDlr_district(rs.getString("dlr_district"));
                dobj.setDlr_pincode(rs.getString("dlr_pincode"));
                dobj.setSale_amt(rs.getInt("sale_amt"));
                dobj.setLaser_code(rs.getString("laser_code"));
                dobj.setGarage_add(rs.getString("garage_add"));
                dobj.setLength(rs.getInt("length"));
                dobj.setWidth(rs.getInt("width"));
                dobj.setHeight(rs.getInt("height"));
                dobj.setRegn_upto(rs.getString("regn_upto"));
                dobj.setRegnUptoDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getRegn_upto())));
                dobj.setFit_upto(rs.getString("fit_upto"));
                dobj.setFitUptoDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getFit_upto())));
                dobj.setAnnual_income(rs.getInt("annual_income"));
                dobj.setOp_dt(rs.getString("op_dt"));
                dobj.setImported_vch(rs.getString("imported_vch"));
                dobj.setOther_criteria(rs.getInt("other_criteria"));
                dobj.setStatus(rs.getString("status"));
                dobj.setVch_purchase_as_code(rs.getString("vch_purchase_as"));

                if (ServerUtil.isTransport(dobj.getVh_class(), null)) {
                    dobj.setVehTypeDescr(TableConstants.VM_VEHTYPE_TRANSPORT_DESCR);
                    dobj.setVehType(TableConstants.VM_VEHTYPE_TRANSPORT);
                } else {
                    dobj.setVehTypeDescr(TableConstants.VM_VEHTYPE_NON_TRANSPORT_DESCR);
                    dobj.setVehType(TableConstants.VM_VEHTYPE_NON_TRANSPORT);
                }

                if (dobj.getVch_purchase_as_code().equalsIgnoreCase("B")) {
                    dobj.setVch_purchase_as("Fully Built");
                } else {
                    dobj.setVch_purchase_as("Drive Away Chasis");
                }

                //Setting Owenr Identification Details                
                String sqlOwnerId = "SELECT * FROM " + TableList.VT_OWNER_IDENTIFICATION
                        + " WHERE regn_no =? and state_cd = ? and off_cd  = ?";
                ps = tmgr.prepareStatement(sqlOwnerId);
                ps.setString(1, dobj.getRegn_no());
                ps.setString(2, stateCd);
                ps.setInt(3, offCd);
                rs = tmgr.fetchDetachedRowSet_No_release();
                OwnerIdentificationDobj own_identity = fillOwnerIdentityDobj(rs);
                if (own_identity != null) {
                    dobj.setOwnerIdentity(own_identity);
                }
            }

        } catch (VahanException vx) {
            throw vx;
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
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

        return dobj;
    }

    public OwnerDetailsDobj getOwnerDetails(String regnNo) throws VahanException {
        OwnerDetailsDobj dobj = null;

        String sql = " SELECT owner.*,type.descr as regn_type_descr,owcode.descr as owner_cd_descr "
                + " FROM " + TableList.VIEW_VV_OWNER + " owner "
                + " left join vm_regn_type type on owner.regn_type=type.regn_typecode "
                + " left join vm_owcode owcode on owner.owner_cd = owcode.ow_code "
                + " WHERE regn_no =? order by owner.op_dt limit 1";
        TransactionManager tmgr = null;
        PreparedStatement ps = null;

        try {
            tmgr = new TransactionManager("getOwnerDetails");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) {
                dobj = new OwnerDetailsDobj();

                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setState_name(rs.getString("state_name"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setOff_name(rs.getString("off_name"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setRegn_dt(rs.getString("regn_dt"));
                dobj.setRegnDateDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getRegn_dt())));
                dobj.setPurchase_dt(rs.getString("purchase_dt"));
                dobj.setPurchaseDateDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getPurchase_dt())));
                dobj.setOwner_sr(rs.getInt("owner_sr"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setF_name(rs.getString("f_name"));
                dobj.setC_add1(rs.getString("c_add1"));
                dobj.setC_add2(rs.getString("c_add2"));
                dobj.setC_add3(rs.getString("c_add3"));
                dobj.setC_district(rs.getInt("c_district"));
                dobj.setC_district_name(rs.getString("c_district_name"));
                dobj.setC_state(rs.getString("c_state"));
                dobj.setC_state_name(rs.getString("c_state_name"));
                dobj.setC_pincode(rs.getInt("c_pincode"));
                dobj.setP_add1(rs.getString("p_add1"));
                dobj.setP_add2(rs.getString("p_add2"));
                dobj.setP_add3(rs.getString("p_add3"));
                dobj.setP_district(rs.getInt("p_district"));
                dobj.setP_district_name(rs.getString("p_district_name"));
                dobj.setP_state(rs.getString("p_state"));
                dobj.setP_state_name(rs.getString("p_state_name"));
                dobj.setP_pincode(rs.getInt("p_pincode"));
                dobj.setOwner_cd(rs.getInt("owner_cd"));
                dobj.setOwner_cd_descr(rs.getString("owner_cd_descr"));
                dobj.setRegn_type(rs.getString("regn_type"));
                dobj.setRegn_type_descr(rs.getString("regn_type_descr"));
                dobj.setVh_class(rs.getInt("vh_class"));
                dobj.setVh_class_desc(rs.getString("vh_class_desc"));
                dobj.setChasi_no(rs.getString("chasi_no"));
                dobj.setEng_no(rs.getString("eng_no"));
                dobj.setMaker(rs.getInt("maker"));
                dobj.setMaker_name(rs.getString("maker_name"));
                dobj.setModel_cd(rs.getString("model_cd"));
                dobj.setModel_name(rs.getString("model_name"));
                dobj.setBody_type(rs.getString("body_type"));
                dobj.setNo_cyl(rs.getInt("no_cyl"));
                dobj.setHp(rs.getFloat("hp"));
                dobj.setSeat_cap(rs.getInt("seat_cap"));
                dobj.setStand_cap(rs.getInt("stand_cap"));
                dobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                dobj.setUnld_wt(rs.getInt("unld_wt"));
                dobj.setLd_wt(rs.getInt("ld_wt"));
                dobj.setGcw(rs.getInt("gcw"));
                dobj.setFuel(rs.getInt("fuel"));
                dobj.setFuel_descr(rs.getString("fuel_descr"));
                dobj.setColor(rs.getString("color"));
                dobj.setManu_mon(rs.getInt("manu_mon"));
                dobj.setManu_yr(rs.getInt("manu_yr"));
                dobj.setNorms(rs.getInt("norms"));
                dobj.setNorms_descr(rs.getString("norms_descr"));
                dobj.setWheelbase(rs.getInt("wheelbase"));
                dobj.setCubic_cap(rs.getFloat("cubic_cap"));
                dobj.setFloor_area(rs.getFloat("floor_area"));
                dobj.setAc_fitted(rs.getString("ac_fitted"));
                dobj.setAudio_fitted(rs.getString("audio_fitted"));
                dobj.setVideo_fitted(rs.getString("video_fitted"));
                dobj.setVch_catg(rs.getString("vch_catg"));
                dobj.setDealer_cd(rs.getString("dealer_cd"));
                dobj.setDlr_name(rs.getString("dlr_name"));
                dobj.setDlr_add1(rs.getString("dlr_add1"));
                dobj.setDlr_add2(rs.getString("dlr_add2"));
                dobj.setDlr_add3(rs.getString("dlr_add3"));
                dobj.setDlr_city(rs.getString("dlr_city"));
                dobj.setDlr_district(rs.getString("dlr_district"));
                dobj.setDlr_pincode(rs.getString("dlr_pincode"));
                dobj.setSale_amt(rs.getInt("sale_amt"));
                dobj.setLaser_code(rs.getString("laser_code"));
                dobj.setGarage_add(rs.getString("garage_add"));
                dobj.setLength(rs.getInt("length"));
                dobj.setWidth(rs.getInt("width"));
                dobj.setHeight(rs.getInt("height"));
                dobj.setRegn_upto(rs.getString("regn_upto"));
                dobj.setRegnUptoDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getRegn_upto())));
                dobj.setFit_upto(rs.getString("fit_upto"));
                dobj.setFitUptoDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getFit_upto())));
                dobj.setAnnual_income(rs.getInt("annual_income"));
                dobj.setOp_dt(rs.getString("op_dt"));
                dobj.setImported_vch(rs.getString("imported_vch"));
                dobj.setOther_criteria(rs.getInt("other_criteria"));
                dobj.setStatus(rs.getString("status"));
                dobj.setVch_purchase_as_code(rs.getString("vch_purchase_as"));

                if (ServerUtil.isTransport(dobj.getVh_class(), null)) {
                    dobj.setVehTypeDescr(TableConstants.VM_VEHTYPE_TRANSPORT_DESCR);
                    dobj.setVehType(TableConstants.VM_VEHTYPE_TRANSPORT);
                } else {
                    dobj.setVehTypeDescr(TableConstants.VM_VEHTYPE_NON_TRANSPORT_DESCR);
                    dobj.setVehType(TableConstants.VM_VEHTYPE_NON_TRANSPORT);
                }

                if (dobj.getVch_purchase_as_code().equalsIgnoreCase("B")) {
                    dobj.setVch_purchase_as("Fully Built");
                } else {
                    dobj.setVch_purchase_as("Drive Away Chasis");
                }

                //Setting Owenr Identification Details                
                String sqlOwnerId = "SELECT * FROM " + TableList.VT_OWNER_IDENTIFICATION
                        + " WHERE regn_no =? and state_cd = ? and off_cd  = ?";
                ps = tmgr.prepareStatement(sqlOwnerId);
                ps.setString(1, regnNo);
                ps.setString(2, dobj.getState_cd());
                ps.setInt(3, dobj.getOff_cd());
                rs = tmgr.fetchDetachedRowSet_No_release();
                OwnerIdentificationDobj own_identity = fillOwnerIdentityDobj(rs);
                if (own_identity != null) {
                    dobj.setOwnerIdentity(own_identity);
                }
            }

        } catch (VahanException vx) {
            dobj = null;
            throw vx;
        } catch (SQLException ex) {
            dobj = null;
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong in Getting Ownwer Details, Please Contact to the System Administrator");
        } catch (Exception ex) {
            dobj = null;
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong in Getting Ownwer Details, Please Contact to the System Administrator");
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

    public List<OwnerDetailsDobj> getOwnerDetailsList(String regnNo, String stateCd) throws VahanException {
        OwnerDetailsDobj dobj = null;
        List<Trailer_dobj> trailer_dobjList = null;

        if (regnNo == null || regnNo.trim().isEmpty()) {
            throw new VahanException("Blank Registration No not allowed.");
        }
        List<OwnerDetailsDobj> ownerList = new ArrayList<>();
        String sql = "";
        sql = " SELECT owner.*,type.descr as regn_type_descr,vtowner.push_back_seat,vtowner.ordinary_seat,owcode.descr as owner_cd_descr, fncr_name"
                + " FROM " + TableList.VIEW_VV_OWNER + " owner "
                + " left join vm_regn_type type on owner.regn_type=type.regn_typecode "
                + " left join vm_owcode owcode on owner.owner_cd = owcode.ow_code "
                + " left join " + TableList.VT_OWNER_OTHER + "  vtowner on owner.regn_no = vtowner.regn_no and owner.state_cd = vtowner.state_cd and  owner.off_cd = vtowner.off_cd"
                + " left join " + TableList.VT_HYPTH + "  vthypth on owner.regn_no = vthypth.regn_no and owner.state_cd = vthypth.state_cd and owner.off_cd = vthypth.off_cd and vthypth.sr_no=1"
                + " WHERE owner.regn_no = ?";
        if (stateCd != null) {
            sql = sql + " and owner.state_cd = ?";
        }

        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;

        try {
            tmgr = new TransactionManagerReadOnly("getOwnerDetails");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            if (stateCd != null) {
                ps.setString(2, stateCd);
            }
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            while (rs.next()) {
                dobj = new OwnerDetailsDobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setState_name(rs.getString("state_name"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setOff_name(rs.getString("off_name"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setRegn_dt(rs.getString("regn_dt"));
                dobj.setRegnDateDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getRegn_dt())));
                dobj.setPurchase_dt(rs.getString("purchase_dt"));
                dobj.setPurchase_date(rs.getDate("purchase_dt"));
                dobj.setPurchaseDateDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getPurchase_dt())));
                dobj.setOwner_sr(rs.getInt("owner_sr"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setF_name(rs.getString("f_name"));
                dobj.setC_add1(rs.getString("c_add1"));
                dobj.setC_add2(rs.getString("c_add2"));
                dobj.setC_add3(rs.getString("c_add3"));
                dobj.setC_district(rs.getInt("c_district"));
                dobj.setC_district_name(rs.getString("c_district_name"));
                dobj.setC_state(rs.getString("c_state"));
                dobj.setC_state_name(rs.getString("c_state_name"));
                dobj.setC_pincode(rs.getInt("c_pincode"));
                dobj.setP_add1(rs.getString("p_add1"));
                dobj.setP_add2(rs.getString("p_add2"));
                dobj.setP_add3(rs.getString("p_add3"));
                dobj.setP_district(rs.getInt("p_district"));
                dobj.setP_district_name(rs.getString("p_district_name"));
                dobj.setP_state(rs.getString("p_state"));
                dobj.setP_state_name(rs.getString("p_state_name"));
                dobj.setP_pincode(rs.getInt("p_pincode"));
                dobj.setOwner_cd(rs.getInt("owner_cd"));
                dobj.setOwner_cd_descr(rs.getString("owner_cd_descr"));
                dobj.setRegn_type(rs.getString("regn_type"));
                dobj.setRegn_type_descr(rs.getString("regn_type_descr"));
                dobj.setVh_class(rs.getInt("vh_class"));
                dobj.setVh_class_desc(rs.getString("vh_class_desc"));
                dobj.setChasi_no(rs.getString("chasi_no"));
                dobj.setEng_no(rs.getString("eng_no"));
                dobj.setMaker(rs.getInt("maker"));
                dobj.setMaker_name(rs.getString("maker_name"));
                dobj.setModel_cd(rs.getString("model_cd"));
                dobj.setModel_name(rs.getString("model_name"));
                dobj.setBody_type(rs.getString("body_type"));
                dobj.setNo_cyl(rs.getInt("no_cyl"));
                dobj.setHp(rs.getFloat("hp"));
                dobj.setSeat_cap(rs.getInt("seat_cap"));
                dobj.setStand_cap(rs.getInt("stand_cap"));
                dobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                dobj.setUnld_wt(rs.getInt("unld_wt"));
                dobj.setLd_wt(rs.getInt("ld_wt"));
                dobj.setGcw(rs.getInt("gcw"));
                dobj.setFuel(rs.getInt("fuel"));
                dobj.setFuel_descr(rs.getString("fuel_descr"));
                dobj.setColor(rs.getString("color"));
                dobj.setManu_mon(rs.getInt("manu_mon"));
                dobj.setManu_yr(rs.getInt("manu_yr"));
                dobj.setNorms(rs.getInt("norms"));
                dobj.setNorms_descr(rs.getString("norms_descr"));
                dobj.setWheelbase(rs.getInt("wheelbase"));
                dobj.setCubic_cap(rs.getFloat("cubic_cap"));
                dobj.setFloor_area(rs.getFloat("floor_area"));
                dobj.setAc_fitted(rs.getString("ac_fitted"));
                dobj.setAudio_fitted(rs.getString("audio_fitted"));
                dobj.setVideo_fitted(rs.getString("video_fitted"));
                dobj.setVch_catg(rs.getString("vch_catg"));
                dobj.setDealer_cd(rs.getString("dealer_cd"));
                dobj.setDlr_name(rs.getString("dlr_name"));
                dobj.setDlr_add1(rs.getString("dlr_add1"));
                dobj.setDlr_add2(rs.getString("dlr_add2"));
                dobj.setDlr_add3(rs.getString("dlr_add3"));
                dobj.setDlr_city(rs.getString("dlr_city"));
                dobj.setDlr_district(rs.getString("dlr_district"));
                dobj.setDlr_pincode(rs.getString("dlr_pincode"));
                dobj.setSale_amt(rs.getInt("sale_amt"));
                dobj.setLaser_code(rs.getString("laser_code"));
                dobj.setGarage_add(rs.getString("garage_add"));
                dobj.setLength(rs.getInt("length"));
                dobj.setWidth(rs.getInt("width"));
                dobj.setHeight(rs.getInt("height"));
                dobj.setRegn_upto(rs.getString("regn_upto"));
                dobj.setRegnUptoDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getRegn_upto())));
                dobj.setFit_upto(rs.getString("fit_upto"));
                dobj.setFitUptoDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getFit_upto())));
                dobj.setAnnual_income(rs.getInt("annual_income"));
                dobj.setOp_dt(rs.getString("op_dt"));
                dobj.setImported_vch(rs.getString("imported_vch"));
                dobj.setOther_criteria(rs.getInt("other_criteria"));
                dobj.setStatus(rs.getString("status"));
                if (dobj.getStatus().equalsIgnoreCase("Y") || dobj.getStatus().equalsIgnoreCase("A")) {
                    dobj.setStatusDescr("Active");
                } else if (dobj.getStatus().equalsIgnoreCase("N")) {
                    dobj.setStatusDescr("Noc Issued");
                } else if (dobj.getStatus().equalsIgnoreCase("T")) {
                    dobj.setStatusDescr("Scrap Vehicle");
                } else if (dobj.getStatus().equalsIgnoreCase("S")) {
                    dobj.setStatusDescr("RC Surrender");
                } else if (dobj.getStatus().equalsIgnoreCase("C")) {
                    dobj.setStatusDescr("RC Cancel");
                } else {
                    dobj.setStatusDescr(dobj.getStatus());
                }
                dobj.setVch_purchase_as_code(rs.getString("vch_purchase_as"));
                dobj.setPush_bk_seat(rs.getInt("push_back_seat"));
                dobj.setOrdinary_seat(rs.getInt("ordinary_seat"));
                if (!CommonUtils.isNullOrBlank(rs.getString("fncr_name"))) {
                    dobj.setHpaDobj(new HpaDobj());
                    dobj.getHpaDobj().setFncr_name(rs.getString("fncr_name"));
                }
                if (ServerUtil.isTransport(dobj.getVh_class(), null)) {
                    dobj.setVehTypeDescr(TableConstants.VM_VEHTYPE_TRANSPORT_DESCR);
                    dobj.setVehType(TableConstants.VM_VEHTYPE_TRANSPORT);
                } else {
                    dobj.setVehTypeDescr(TableConstants.VM_VEHTYPE_NON_TRANSPORT_DESCR);
                    dobj.setVehType(TableConstants.VM_VEHTYPE_NON_TRANSPORT);
                }

                if (dobj.getVch_purchase_as_code().equalsIgnoreCase("B")) {
                    dobj.setVch_purchase_as("Fully Built");
                } else {
                    dobj.setVch_purchase_as("Drive Away Chasis");
                }

                String vh_classCase = "," + String.valueOf(dobj.getVh_class()) + ",";

                if (stateCd != null && (stateCd.equalsIgnoreCase("NL") || stateCd.equalsIgnoreCase("OR")) && TableConstants.GCW_VEH_CLASS.contains(vh_classCase)) {

                    String sqlGcw = "select ld_wt from " + TableList.VT_OWNER + " \n"
                            + "where regn_no in (SELECT regn_no FROM " + TableList.VT_SIDE_TRAILER + " \n"
                            + "where link_regn_no=? and state_cd = ? and off_cd = ?) and state_cd = ? and off_cd = ?";
                    ps = tmgr.prepareStatement(sqlGcw);

                    ps.setString(1, regnNo);
                    ps.setString(2, stateCd);
                    ps.setInt(3, dobj.getOff_cd());
                    ps.setString(4, stateCd);
                    ps.setInt(5, dobj.getOff_cd());
                    RowSet rset = tmgr.fetchDetachedRowSet_No_release();
                    int ldwt = 0;
                    while (rset.next()) {
                        ldwt += rset.getInt("ld_wt");
                        dobj.setRenderedGCW(true);
                    }
                    dobj.setGcw(ldwt + dobj.getUnld_wt());

                }

                InsDobj insDobj = InsImpl.set_ins_dtls_db_to_dobj(regnNo, null, dobj.getState_cd(), dobj.getOff_cd());
                dobj.setInsDobj(insDobj);

                AxleDetailsDobj axleDobj = AxleImpl.setAxleVehDetails_db_to_dobj(null, regnNo, dobj.getState_cd(), dobj.getOff_cd());
                dobj.setAxleDobj(axleDobj);

                //Setting Owenr Identification Details  
                String sqlOwnerId = "SELECT * FROM " + TableList.VT_OWNER_IDENTIFICATION
                        + " WHERE regn_no =? and state_cd = ? and off_cd = ? ";

                ps = tmgr.prepareStatement(sqlOwnerId);
                ps.setString(1, regnNo);
                ps.setString(2, dobj.getState_cd());
                ps.setInt(3, dobj.getOff_cd());

                RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
                OwnerIdentificationDobj own_identity = fillOwnerIdentityDobj(rs1);
                if (own_identity != null) {
                    dobj.setOwnerIdentity(own_identity);
                }
                String lastRcptdt = "select to_char(rcpt_dt,'DD-Mon-YYYY HH:MM:SS') as rcpt_dt from ((select rcpt_dt from vt_fee f where f.state_cd=? and f.off_cd=? and f.regn_no=? order by f.rcpt_dt desc limit 1)"
                        + " union all (select rcpt_dt from vt_tax where state_cd=? and off_cd=? and regn_no=? order by rcpt_dt desc limit 1)) as a order by rcpt_dt desc limit 1";
                ps = tmgr.prepareStatement(lastRcptdt);
                ps.setString(1, dobj.getState_cd());
                ps.setInt(2, dobj.getOff_cd());
                ps.setString(3, regnNo);
                ps.setString(4, dobj.getState_cd());
                ps.setInt(5, dobj.getOff_cd());
                ps.setString(6, regnNo);
                RowSet rs2 = tmgr.fetchDetachedRowSet_No_release();
                if (rs2.next()) {
                    if (rs2.getString("rcpt_dt") != null && !rs2.getString("rcpt_dt").isEmpty()) {
                        dobj.setLastRcptDt(rs2.getString("rcpt_dt"));
                    } else {
                        dobj.setLastRcptDt("No Fee/Tax Taken");
                    }
                } else {
                    dobj.setLastRcptDt("No Fee/Tax Taken");
                }

                //Setting Number of Tyres
                String sqlNoOftyres = "SELECT sum(COALESCE(f_axle_tyre,0) + COALESCE(r_axle_tyre,0) + COALESCE(o_axle_tyre,0) + COALESCE(t_axle_tyre,0)) as numberoftyre FROM " + TableList.VT_AXLE
                        + " WHERE regn_no =? and state_cd = ? and off_cd = ? ";
                ps = tmgr.prepareStatement(sqlNoOftyres);
                ps.setString(1, dobj.getRegn_no());
                ps.setString(2, dobj.getState_cd());
                ps.setInt(3, dobj.getOff_cd());
                RowSet rs3 = tmgr.fetchDetachedRowSet_No_release();
                if (rs3.next()) {
                    dobj.setNumberOfTyres(rs3.getInt("numberoftyre"));
                }
                trailer_dobjList = Trailer_Impl.set_trailer_dobjList(tmgr, null, regnNo, 0);
                if (trailer_dobjList != null && trailer_dobjList.size() > 0) {
                    dobj.setListTrailerDobj(trailer_dobjList);
                }
                ownerList.add(dobj);
            }

        } catch (VahanException vx) {
            throw vx;
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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

        return ownerList;
    }

    public List<OwnerDetailsDobj> getStateWiseOwnerList(List<OwnerDetailsDobj> ownerList, String stateCd) {

        List<OwnerDetailsDobj> stateWiseList = new ArrayList<>();
        if (ownerList != null && !ownerList.isEmpty()) {
            for (int i = 0; i < ownerList.size(); i++) {
                if (ownerList.get(i).getState_cd().equalsIgnoreCase(stateCd)) {
                    stateWiseList.add(ownerList.get(i));
                }
            }
        }
        return stateWiseList;
    }

    public OwnerDetailsDobj getVaOwnerDetails(String applNo, String stateCd, int offCd) throws VahanException {
        OwnerDetailsDobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            String where_condition = "";
            TmConfigurationDobj configurationDobj = Util.getTmConfiguration();
            if (Util.getUserCategory() == null || configurationDobj == null) {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
            if (!Util.getUserCategory().equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_USER) && !configurationDobj.isAllow_fitness_all_RTO()) {
                where_condition = " and off_cd=?";
            }

            String sql = " SELECT owner.*,type.descr as regn_type_descr,owcode.descr as owner_cd_descr "
                    + " FROM " + TableList.VIEW_VVA_OWNER + " owner "
                    + " left join vm_regn_type type on owner.regn_type=type.regn_typecode "
                    + " left join vm_owcode owcode on owner.owner_cd = owcode.ow_code "
                    + " WHERE appl_no =? and state_cd=? " + where_condition + " ";
            tmgr = new TransactionManager("getVaOwnerDetails");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, stateCd);
            if (!"".equalsIgnoreCase(where_condition)) {
                ps.setInt(3, offCd);
            }
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new OwnerDetailsDobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setState_name(rs.getString("state_name"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setOff_name(rs.getString("off_name"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setRegn_dt(rs.getString("regn_dt"));
                dobj.setRegnDateDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getRegn_dt())));
                dobj.setPurchase_dt(rs.getString("purchase_dt"));
                dobj.setPurchase_date(rs.getDate("purchase_dt"));
                dobj.setPurchaseDateDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getPurchase_dt())));
                dobj.setOwner_sr(rs.getInt("owner_sr"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setF_name(rs.getString("f_name"));
                dobj.setC_add1(rs.getString("c_add1"));
                dobj.setC_add2(rs.getString("c_add2"));
                dobj.setC_add3(rs.getString("c_add3"));
                dobj.setC_district(rs.getInt("c_district"));
                dobj.setC_district_name(rs.getString("c_district_name"));
                dobj.setC_state(rs.getString("c_state"));
                dobj.setC_state_name(rs.getString("c_state_name"));
                dobj.setC_pincode(rs.getInt("c_pincode"));
                dobj.setP_add1(rs.getString("p_add1"));
                dobj.setP_add2(rs.getString("p_add2"));
                dobj.setP_add3(rs.getString("p_add3"));
                dobj.setP_district(rs.getInt("p_district"));
                dobj.setP_district_name(rs.getString("p_district_name"));
                dobj.setP_state(rs.getString("p_state"));
                dobj.setP_state_name(rs.getString("p_state_name"));
                dobj.setP_pincode(rs.getInt("p_pincode"));
                dobj.setOwner_cd(rs.getInt("owner_cd"));
                dobj.setOwner_cd_descr(rs.getString("owner_cd_descr"));
                dobj.setRegn_type(rs.getString("regn_type"));
                dobj.setRegn_type_descr(rs.getString("regn_type_descr"));
                dobj.setVh_class(rs.getInt("vh_class"));
                dobj.setVh_class_desc(rs.getString("vh_class_desc"));
                dobj.setChasi_no(rs.getString("chasi_no"));
                dobj.setEng_no(rs.getString("eng_no"));
                dobj.setMaker(rs.getInt("maker"));
                dobj.setMaker_name(rs.getString("maker_name"));
                dobj.setModel_cd(rs.getString("model_cd"));
                dobj.setModel_name(rs.getString("model_name"));
                dobj.setBody_type(rs.getString("body_type"));
                dobj.setNo_cyl(rs.getInt("no_cyl"));
                dobj.setHp(rs.getFloat("hp"));
                dobj.setSeat_cap(rs.getInt("seat_cap"));
                dobj.setStand_cap(rs.getInt("stand_cap"));
                dobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                dobj.setUnld_wt(rs.getInt("unld_wt"));
                dobj.setLd_wt(rs.getInt("ld_wt"));
                dobj.setGcw(rs.getInt("gcw"));
                dobj.setFuel(rs.getInt("fuel"));
                dobj.setFuel_descr(rs.getString("fuel_descr"));
                dobj.setColor(rs.getString("color"));
                dobj.setManu_mon(rs.getInt("manu_mon"));
                dobj.setManu_yr(rs.getInt("manu_yr"));
                dobj.setNorms(rs.getInt("norms"));
                dobj.setNorms_descr(rs.getString("norms_descr"));
                dobj.setWheelbase(rs.getInt("wheelbase"));
                dobj.setCubic_cap(rs.getFloat("cubic_cap"));
                dobj.setFloor_area(rs.getFloat("floor_area"));
                dobj.setAc_fitted(rs.getString("ac_fitted"));
                dobj.setAudio_fitted(rs.getString("audio_fitted"));
                dobj.setVideo_fitted(rs.getString("video_fitted"));
                dobj.setVch_purchase_as_code(rs.getString("vch_purchase_as"));
                dobj.setVch_catg(rs.getString("vch_catg"));
                dobj.setDealer_cd(rs.getString("dealer_cd"));
                dobj.setDlr_name(rs.getString("dlr_name"));
                dobj.setDlr_add1(rs.getString("dlr_add1"));
                dobj.setDlr_add2(rs.getString("dlr_add2"));
                dobj.setDlr_add3(rs.getString("dlr_add3"));
                dobj.setDlr_city(rs.getString("dlr_city"));
                dobj.setDlr_district(rs.getString("dlr_district"));
                dobj.setDlr_pincode(rs.getString("dlr_pincode"));
                dobj.setSale_amt(rs.getInt("sale_amt"));
                dobj.setLaser_code(rs.getString("laser_code"));
                dobj.setGarage_add(rs.getString("garage_add"));
                dobj.setLength(rs.getInt("length"));
                dobj.setWidth(rs.getInt("width"));
                dobj.setHeight(rs.getInt("height"));
                dobj.setRegn_upto(rs.getString("regn_upto"));
                dobj.setRegnUptoDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getRegn_upto())));
                dobj.setFit_upto(rs.getString("fit_upto"));
                dobj.setFitUptoDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getFit_upto())));
                dobj.setAnnual_income(rs.getInt("annual_income"));
                dobj.setOp_dt(rs.getString("op_dt"));
                dobj.setImported_vch(rs.getString("imported_vch"));
                dobj.setOther_criteria(rs.getInt("other_criteria"));
                dobj.setPmt_type(rs.getInt("pmt_type"));
                dobj.setPmt_catg(rs.getInt("pmt_catg"));
                dobj.setRqrd_tax_modes(rs.getString("rqrd_tax_modes"));
                dobj.setVehType(ServerUtil.VehicleClassType(dobj.getVh_class()));

                if (ServerUtil.isTransport(dobj.getVh_class(), null)) {
                    dobj.setVehTypeDescr(TableConstants.VM_VEHTYPE_TRANSPORT_DESCR);
                    dobj.setVehType(TableConstants.VM_VEHTYPE_TRANSPORT);
                    dobj.setVch_type(TableConstants.VM_VEHTYPE_TRANSPORT_DESCR);
                } else {
                    dobj.setVehTypeDescr(TableConstants.VM_VEHTYPE_NON_TRANSPORT_DESCR);
                    dobj.setVehType(TableConstants.VM_VEHTYPE_NON_TRANSPORT);
                    dobj.setVch_type(TableConstants.VM_VEHTYPE_NON_TRANSPORT_DESCR);
                }

                if (dobj.getVch_purchase_as_code().equalsIgnoreCase("B")) {
                    dobj.setVch_purchase_as("Fully Built");
                } else {
                    dobj.setVch_purchase_as("Drive Away Chasis");
                }

                //Setting Owenr Identification Details                
                String sqlOwnerId = "SELECT * FROM " + TableList.VA_OWNER_IDENTIFICATION
                        + " WHERE appl_no =?";
                ps = tmgr.prepareStatement(sqlOwnerId);
                ps.setString(1, applNo);
                rs = tmgr.fetchDetachedRowSet_No_release();
                OwnerIdentificationDobj own_identity = fillOwnerIdentityDobj(rs);
                if (own_identity != null) {
                    dobj.setOwnerIdentity(own_identity);
                }
            }
        } catch (VahanException ve) {
            throw ve;
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

        return dobj;
    }

    public Owner_dobj getOwnerDobj(OwnerDetailsDobj ownerDetailsDobj) {
        Owner_dobj owner_dobj = null;

        if (ownerDetailsDobj != null) {
            owner_dobj = new Owner_dobj();
            owner_dobj.setRegn_no(ownerDetailsDobj.getRegn_no());
            owner_dobj.setOff_cd(ownerDetailsDobj.getOff_cd());
            owner_dobj.setOwner_sr(ownerDetailsDobj.getOwner_sr());
            owner_dobj.setOwner_name(ownerDetailsDobj.getOwner_name());
            owner_dobj.setF_name(ownerDetailsDobj.getF_name().trim());
            owner_dobj.setC_add1(ownerDetailsDobj.getC_add1().trim());
            owner_dobj.setC_add2(ownerDetailsDobj.getC_add2().trim());
            owner_dobj.setC_add3(ownerDetailsDobj.getC_add3().trim());
            owner_dobj.setC_state(ownerDetailsDobj.getC_state().trim().trim());
            owner_dobj.setC_district(ownerDetailsDobj.getC_district());
            owner_dobj.setC_pincode(ownerDetailsDobj.getC_pincode());
            owner_dobj.setP_add1(ownerDetailsDobj.getP_add1().trim());
            owner_dobj.setP_add2(ownerDetailsDobj.getP_add2().trim());
            owner_dobj.setP_add3(ownerDetailsDobj.getP_add3().trim());
            owner_dobj.setP_state(ownerDetailsDobj.getP_state().trim());
            owner_dobj.setP_district(ownerDetailsDobj.getP_district());
            owner_dobj.setP_pincode(ownerDetailsDobj.getP_pincode());
            owner_dobj.setOwner_cd(ownerDetailsDobj.getOwner_cd());
            owner_dobj.setRegn_type(ownerDetailsDobj.getRegn_type());
            owner_dobj.setVh_class(ownerDetailsDobj.getVh_class());
            owner_dobj.setChasi_no(ownerDetailsDobj.getChasi_no());
            owner_dobj.setEng_no(ownerDetailsDobj.getEng_no());
            owner_dobj.setMaker(ownerDetailsDobj.getMaker());
            owner_dobj.setMaker_model(ownerDetailsDobj.getModel_cd());
            owner_dobj.setBody_type(ownerDetailsDobj.getBody_type());
            owner_dobj.setNo_cyl(ownerDetailsDobj.getNo_cyl());
            owner_dobj.setHp(ownerDetailsDobj.getHp());
            owner_dobj.setSeat_cap(ownerDetailsDobj.getSeat_cap());
            owner_dobj.setStand_cap(ownerDetailsDobj.getStand_cap());
            owner_dobj.setSleeper_cap(ownerDetailsDobj.getSleeper_cap());
            owner_dobj.setUnld_wt(ownerDetailsDobj.getUnld_wt());
            owner_dobj.setLd_wt(ownerDetailsDobj.getLd_wt());
            owner_dobj.setFuel(ownerDetailsDobj.getFuel());
            owner_dobj.setColor(ownerDetailsDobj.getColor());
            owner_dobj.setManu_mon(ownerDetailsDobj.getManu_mon());
            owner_dobj.setManu_yr(ownerDetailsDobj.getManu_yr());
            owner_dobj.setNorms(ownerDetailsDobj.getNorms());
            owner_dobj.setWheelbase(ownerDetailsDobj.getWheelbase());
            owner_dobj.setCubic_cap(ownerDetailsDobj.getCubic_cap());
            owner_dobj.setFloor_area(ownerDetailsDobj.getFloor_area());
            owner_dobj.setAc_fitted(ownerDetailsDobj.getAc_fitted());
            owner_dobj.setAudio_fitted(ownerDetailsDobj.getAudio_fitted());
            owner_dobj.setVideo_fitted(ownerDetailsDobj.getVideo_fitted());
            owner_dobj.setVch_catg(ownerDetailsDobj.getVch_catg());
            owner_dobj.setDealer_cd(ownerDetailsDobj.getDealer_cd());
            owner_dobj.setSale_amt(ownerDetailsDobj.getSale_amt());
            owner_dobj.setLaser_code(ownerDetailsDobj.getLaser_code());
            owner_dobj.setGarage_add(ownerDetailsDobj.getGarage_add());
            owner_dobj.setLength(ownerDetailsDobj.getLength());
            owner_dobj.setWidth(ownerDetailsDobj.getWidth());
            owner_dobj.setHeight(ownerDetailsDobj.getHeight());
            owner_dobj.setState_cd(ownerDetailsDobj.getState_cd());
            owner_dobj.setImported_vch(ownerDetailsDobj.getImported_vch());
            owner_dobj.setOther_criteria(ownerDetailsDobj.getOther_criteria());
            owner_dobj.setAnnual_income(ownerDetailsDobj.getAnnual_income());
            owner_dobj.setRegn_dt(JSFUtils.getStringToDateyyyyMMdd(ownerDetailsDobj.getRegn_dt()));
            owner_dobj.setPurchase_dt(JSFUtils.getStringToDateyyyyMMdd(ownerDetailsDobj.getPurchase_dt()));
            owner_dobj.setFit_upto(JSFUtils.getStringToDateyyyyMMdd(ownerDetailsDobj.getFit_upto()));
            owner_dobj.setRegn_upto(JSFUtils.getStringToDateyyyyMMdd(ownerDetailsDobj.getRegn_upto()));
            owner_dobj.setStatus(ownerDetailsDobj.getStatus());
            owner_dobj.setVch_purchase_as(ownerDetailsDobj.getVch_purchase_as_code());
            owner_dobj.setGcw(ownerDetailsDobj.getGcw());
            owner_dobj.setPmt_type(ownerDetailsDobj.getPmt_type());
            owner_dobj.setPmt_catg(ownerDetailsDobj.getPmt_catg());
            owner_dobj.setRqrd_tax_modes(ownerDetailsDobj.getRqrd_tax_modes());
            owner_dobj.setVehType(ownerDetailsDobj.getVehType());
            owner_dobj.setVehAgeExpire(ownerDetailsDobj.isVehAgeExpire());
            owner_dobj.setBlackListedVehicleDobj(ownerDetailsDobj.getBlackListedVehicleDobj());
            owner_dobj.setAxleDobj(ownerDetailsDobj.getAxleDobj());
            owner_dobj.setNumberOfTyres(ownerDetailsDobj.getNumberOfTyres());
            owner_dobj.setInsDobj(ownerDetailsDobj.getInsDobj());

            if (ownerDetailsDobj.getOwnerIdentity() != null) {
                OwnerIdentificationDobj ow_id = new OwnerIdentificationDobj();
                owner_dobj.setOwner_identity(ow_id);
                owner_dobj.getOwner_identity().setState_cd(ownerDetailsDobj.getOwnerIdentity().getState_cd());
                owner_dobj.getOwner_identity().setOff_cd(ownerDetailsDobj.getOwnerIdentity().getOff_cd());
                owner_dobj.getOwner_identity().setPan_no(ownerDetailsDobj.getOwnerIdentity().getPan_no());
                owner_dobj.getOwner_identity().setEmail_id(ownerDetailsDobj.getOwnerIdentity().getEmail_id());
                owner_dobj.getOwner_identity().setMobile_no(ownerDetailsDobj.getOwnerIdentity().getMobile_no());
                if (!CommonUtils.isNullOrBlank(ownerDetailsDobj.getOwnerIdentity().getAadhar_no())) {
                    String aadhar_no = ownerDetailsDobj.getOwnerIdentity().getAadhar_no();
                    String aadhar_last_four_digit = null;
                    if (aadhar_no.length() > 4) {
                        aadhar_last_four_digit = aadhar_no.substring(aadhar_no.length() - 4);
                    } else {
                        aadhar_last_four_digit = aadhar_no;
                    }
                    ownerDetailsDobj.getOwnerIdentity().setMask_aadhar_no("XXXXXXXX" + aadhar_last_four_digit);
                }
                owner_dobj.getOwner_identity().setAadhar_no(ownerDetailsDobj.getOwnerIdentity().getAadhar_no());
                owner_dobj.getOwner_identity().setPassport_no(ownerDetailsDobj.getOwnerIdentity().getPassport_no());
                owner_dobj.getOwner_identity().setRation_card_no(ownerDetailsDobj.getOwnerIdentity().getRation_card_no());
                owner_dobj.getOwner_identity().setVoter_id(ownerDetailsDobj.getOwnerIdentity().getVoter_id());
                owner_dobj.getOwner_identity().setDl_no(ownerDetailsDobj.getOwnerIdentity().getDl_no());
                owner_dobj.getOwner_identity().setRegn_no(ownerDetailsDobj.getOwnerIdentity().getRegn_no());
                owner_dobj.getOwner_identity().setOwnerCatg(ownerDetailsDobj.getOwnerIdentity().getOwnerCatg());
            }
            owner_dobj.setListTrailerDobj(ownerDetailsDobj.getListTrailerDobj());
        }
        return owner_dobj;
    }

    public static boolean isMakerModelInDb(int makerId, String modelNo) {
        boolean matched = false;
        String sql = "Select maker_code,unique_model_ref_no FROM " + TableList.VM_MODELS
                + " where maker_code=? and unique_model_ref_no=?";
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("isMakerModelInDb");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setInt(1, makerId);
            ps.setString(2, modelNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                matched = true;
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

        return matched;
    }

    public static boolean checkLast5ChassisChar(String chassiNo, String userInputChassi) {
        boolean isTrue = false;

        if (chassiNo.trim().length() >= 5 && userInputChassi.length() == 5) {
            chassiNo = chassiNo.toUpperCase();
            userInputChassi = userInputChassi.toUpperCase();

            if (userInputChassi.equalsIgnoreCase(chassiNo.substring(chassiNo.length() - 5, chassiNo.length()))) {
                isTrue = true;
            }

        }

        return isTrue;
    }

    public static boolean checkLast5EngineChar(String engineNo, String userInputEngine) {
        boolean isTrue = false;

        if (engineNo.trim().length() >= 5 && userInputEngine.trim().length() == 5) {
            engineNo = engineNo.toUpperCase();
            userInputEngine = userInputEngine.toUpperCase();

            if (userInputEngine.equalsIgnoreCase(engineNo.substring(engineNo.length() - 5, engineNo.length()))) {
                isTrue = true;
            }

        }

        return isTrue;
    }

    public static Owner_dobj getBacklogDetails(String regn_no, String chasi_no) throws VahanException {
        Owner_dobj dobj = null;
        String sql = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManager("getBacklogDetails");
            if ((regn_no != null && !regn_no.isEmpty()) && (chasi_no != null && !chasi_no.isEmpty())) {
                sql = "Select * from " + TableList.VB_BACKLOG_OWNER + " where  regn_no=? and chasi_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regn_no);
                ps.setString(2, chasi_no);
            } else if (regn_no != null && !regn_no.isEmpty()) {
                sql = "Select * from " + TableList.VB_BACKLOG_OWNER + " where  regn_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regn_no);

            } else if (chasi_no != null && !chasi_no.isEmpty()) {
                sql = "Select * from " + TableList.VB_BACKLOG_OWNER + " where  chasi_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, chasi_no);
            } else {
                throw new VahanException("Empty Reg No and Chasi No ");
            }

            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new Owner_dobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setRegn_dt(rs.getDate("regn_dt"));
                dobj.setPurchase_dt(rs.getDate("purchase_dt"));
                dobj.setOwner_sr(rs.getInt("owner_sr"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setF_name(rs.getString("f_name"));
                dobj.setC_add1(rs.getString("c_add1"));
                dobj.setC_add2(rs.getString("c_add2"));
                dobj.setC_add3(rs.getString("c_add3"));
                dobj.setC_district(rs.getInt("c_district"));
                dobj.setC_pincode(rs.getInt("c_pincode"));
                dobj.setC_state(rs.getString("c_state"));
                dobj.setP_add1(rs.getString("p_add1"));
                dobj.setP_add2(rs.getString("p_add2"));
                dobj.setP_add3(rs.getString("p_add3"));
                dobj.setP_district(rs.getInt("p_district"));
                dobj.setP_pincode(rs.getInt("p_pincode"));
                dobj.setP_state(rs.getString("p_state"));
                dobj.setOwner_cd(rs.getInt("owner_cd"));
                dobj.setRegn_type(rs.getString("regn_type"));
                dobj.setVh_class(rs.getInt("vh_class"));
                dobj.setChasi_no(rs.getString("chasi_no"));
                dobj.setEng_no(rs.getString("eng_no"));
                dobj.setMaker(rs.getInt("maker"));
                dobj.setMaker_model(rs.getString("maker_model"));
                dobj.setBody_type(rs.getString("body_type"));
                dobj.setNo_cyl(rs.getInt("no_cyl"));
                dobj.setHp(rs.getFloat("hp"));
                dobj.setSeat_cap(rs.getInt("seat_cap"));
                dobj.setStand_cap(rs.getInt("stand_cap"));
                dobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                dobj.setUnld_wt(rs.getInt("unld_wt"));
                dobj.setLd_wt(rs.getInt("ld_wt"));
                dobj.setGcw(rs.getInt("gcw"));
                dobj.setFuel(rs.getInt("fuel"));
                dobj.setColor(rs.getString("color"));
                dobj.setManu_mon(rs.getInt("manu_mon"));
                dobj.setManu_yr(rs.getInt("manu_yr"));
                dobj.setNorms(rs.getInt("norms"));
                dobj.setWheelbase(rs.getInt("wheelbase"));
                dobj.setCubic_cap(rs.getFloat("cubic_cap"));
                dobj.setFloor_area(rs.getFloat("floor_area"));
                dobj.setAc_fitted(rs.getString("ac_fitted"));
                dobj.setAudio_fitted(rs.getString("audio_fitted"));
                dobj.setVideo_fitted(rs.getString("video_fitted"));
                dobj.setVch_purchase_as(rs.getString("vch_purchase_as"));
                dobj.setVch_catg(rs.getString("vch_catg"));
                dobj.setDealer_cd(rs.getString("dealer_cd"));
                dobj.setSale_amt(rs.getInt("sale_amt"));
                dobj.setLaser_code(rs.getString("laser_code"));
                dobj.setGarage_add(rs.getString("garage_add"));
                dobj.setLength(rs.getInt("length"));
                dobj.setWidth(rs.getInt("width"));
                dobj.setHeight(rs.getInt("height"));
                dobj.setRegn_upto(rs.getDate("regn_upto"));
                dobj.setFit_upto(rs.getDate("fit_upto"));
                dobj.setAnnual_income(rs.getInt("annual_income"));
                dobj.setImported_vch(rs.getString("imported_vch"));
                dobj.setOther_criteria(rs.getInt("other_criteria"));
                dobj.setVehType(ServerUtil.VehicleClassType(dobj.getVh_class()));

            }

        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Fetching of Backlog Details");
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

    public void insertInVhOwnerFromVtOwner(String appl_no, String regn_no, TransactionManager tmgr) {
        PreparedStatement ps = null;
        String sql = "";
        int i = 1;
        sql = "INSERT INTO " + TableList.VH_OWNER
                + " (state_cd, off_cd, regn_no, regn_dt, purchase_dt, owner_sr, owner_name,"
                + " f_name, c_add1, c_add2, c_add3, c_district, c_pincode, c_state,"
                + " p_add1, p_add2, p_add3, p_district, p_pincode, p_state, owner_cd,"
                + " regn_type, vh_class, chasi_no, eng_no, maker, maker_model, body_type,"
                + " no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, ld_wt,"
                + " gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, cubic_cap,"
                + " floor_area, ac_fitted, audio_fitted, video_fitted, vch_purchase_as,"
                + " vch_catg, dealer_cd, sale_amt, laser_code, garage_add, length,"
                + " width, height, regn_upto, fit_upto, annual_income, imported_vch,"
                + " other_criteria, status, op_dt, appl_no, moved_on, moved_by,moved_status)"
                + "   SELECT state_cd, off_cd, regn_no, regn_dt, purchase_dt, owner_sr, owner_name,"
                + "       f_name, c_add1, c_add2, c_add3, c_district, c_pincode, c_state,"
                + "       p_add1, p_add2, p_add3, p_district, p_pincode, p_state, owner_cd,"
                + "       regn_type, vh_class, chasi_no, eng_no, maker, maker_model, body_type,"
                + "       no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, ld_wt,"
                + "       gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, cubic_cap,"
                + "       floor_area, ac_fitted, audio_fitted, video_fitted, vch_purchase_as,"
                + "       vch_catg, dealer_cd, sale_amt, laser_code, garage_add, length,"
                + "       width, height, regn_upto, fit_upto, annual_income, imported_vch,"
                + "       other_criteria, status, op_dt, ?,current_timestamp,?,?"
                + "  FROM " + TableList.VT_OWNER
                + "  where regn_no=? and state_cd=? and off_cd=?";
        try {
            ps = tmgr.prepareStatement(sql);
            i = 1;
            ps.setString(i++, appl_no);
            ps.setString(i++, Util.getEmpCode());
            ps.setString(i++, TableConstants.VH_MOVED_STATUS_UPDATE);
            ps.setString(i++, regn_no);
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);

        }
    }

    public String checkForFancyOrRetenRegn(String applno) {

        String returnFancyOrRetenRegnDtls = null;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        String sql = null;
        try {
            tmgr = new TransactionManager("checkForBlacklistedVehicle");
            sql = "SELECT regn_no FROM " + TableList.VT_ADVANCE_REGN_NO + " WHERE regn_appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applno);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                returnFancyOrRetenRegnDtls = "Vehicle Registration No " + rs.getString("regn_no").toUpperCase() + " will be allotted (as per booked Fancy/Advance Registration No)";
            }

            sql = "SELECT old_regn_no FROM " + TableList.VT_SURRENDER_RETENTION + " WHERE regn_appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applno);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {

                returnFancyOrRetenRegnDtls = "Vehicle Registration No " + rs.getString("old_regn_no").toUpperCase() + " will be allotted (as per Retention No)";
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }

        return returnFancyOrRetenRegnDtls;
    }

    private String getLinkedVehDetails(String applNo, TransactionManager tmgr) throws SQLException {
        String linkedRegnNo = "";
        String query = "Select * from " + TableList.VA_SIDE_TRAILER + " where appl_no = ?";
        PreparedStatement ps = tmgr.prepareStatement(query);
        ps.setString(1, applNo);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            linkedRegnNo = rs.getString("link_regn_no");
        }
        return linkedRegnNo;
    }

    public Date getVaOwnerRegistrationDate(String applNo, TransactionManager tmgr) throws Exception {
        Date regnDt = null;
        String query = "Select regn_dt from " + TableList.VA_OWNER + " where appl_no = ?";
        PreparedStatement ps = tmgr.prepareStatement(query);
        ps.setString(1, applNo);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            regnDt = rs.getDate("regn_dt");
        }
        return regnDt;
    }

    public String isChasiAlreadyExist(TransactionManager tmgr, String chasiNo, String regnType) throws VahanException {
        PreparedStatement ps = null;
        RowSet rs = null;
        String message = null;
        String sql = null;
        if (chasiNo != null) {
            chasiNo = chasiNo.toUpperCase();
        }
        String appl_no = null;
        String regn_no = null;
        try {
            sql = "SELECT chasi_no,appl_no,regn_no FROM " + TableList.VA_OWNER + " WHERE chasi_no=? limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, chasiNo);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                appl_no = rs.getString("appl_no");
                regn_no = rs.getString("regn_no");
                if (!regn_no.equals("NEW")) {
                    message = ("Chassis No [" + chasiNo + "] is Already in Use [Appl.No: " + appl_no + "],[ Regn.No: " + regn_no + "],Please Use Different Chassis NO");
                } else {
                    message = ("Chassis NO [" + chasiNo + "] is Already in Use [ Appl.No: " + appl_no + "],Please Use Different Chassis NO");
                }
                return message;

            } else if (TableConstants.VM_REGN_TYPE_CONFISCATED_AUCTION.equals(regnType)) {
                sql = "SELECT chasi_no,regn_no FROM " + TableList.VT_OWNER + " WHERE chasi_no=? and status != '" + TableConstants.VT_AUCTION_STATUS + "' limit 1";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, chasiNo);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    regn_no = rs.getString("regn_no");
                    message = ("Chassis NO [" + chasiNo + "] is Already in Use [Regn.No: " + regn_no + "],Please Use Different Chassis NO");
                    return message;
                }
            } else {
                if (!(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE + "," + TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE).contains(regnType)) {

                    sql = "SELECT chasi_no,regn_no FROM " + TableList.VT_OWNER + " WHERE chasi_no=? and status != 'C' limit 1";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, chasiNo);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        regn_no = rs.getString("regn_no");
                        message = ("Chassis NO [" + chasiNo + "] is Already in Use [Regn.No: " + regn_no + "],Please Use Different Chassis NO");
                        return message;
                    }
                }
                if (!(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE + "," + TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE + "," + TableConstants.VM_REGN_TYPE_TEMPORARY).contains(regnType)) {
                    //Except Stock Transfer Cases
                    sql = "SELECT chasi_no,appl_no,temp_regn_no,purpose  FROM " + TableList.VT_OWNER_TEMP + " WHERE chasi_no=? order by op_dt desc limit 1";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, chasiNo);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next() && rs.getString("purpose") != null && !rs.getString("purpose").equalsIgnoreCase(TableConstants.STOCK_TRANSFER)) {
                        appl_no = rs.getString("appl_no");
                        String tempRegno = rs.getString("temp_regn_no");
                        message = ("Chassis NO [" + chasiNo + "] is Already in Use [Appl.No: " + appl_no + "] ,[Temp Regn.No: " + tempRegno + "],Please Use Different Chassis NO");
                        return message;
                    }
                }
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during Fetching Details of CHASI NO, Please Contact to the System Administrator.");
        }

        return message;
    }

    public List<OwnerDetailsDobj> getTempRegistrationDetailsList(String chasiNo) throws VahanException {
        OwnerDetailsDobj dobj = null;
        List<OwnerDetailsDobj> ownerList = new ArrayList<>();
        String sql = null;
        sql = " select owner.*,state.descr as state_name,off.off_name,"
                + " dest_state.descr as state_cd_to_descr,dest_off.off_name as off_cd_to_descr, "
                + " to_char(owner.op_dt, 'DD-Mon-YYYY') as op_dt_descr,"
                + " to_char(owner.valid_upto, 'DD-Mon-YYYY') as valid_upto_descr,a.descr as temp_regn_reason "
                + " from  vt_owner_temp owner "
                + " left join TM_STATE state on owner.state_cd = state.state_code "
                + " left join TM_STATE dest_state on dest_state.state_code=owner.state_cd_to "
                + " left join TM_OFFICE dest_off on dest_off.off_cd=owner.off_cd_to and dest_off.state_cd=owner.state_cd_to "
                + " left join TM_OFFICE off on off.off_cd = owner.off_cd and off.state_cd=owner.state_cd "
                + " left join vm_temp_regn_reason a on a.code = owner.purpose"
                + " where owner.chasi_no=? order by op_dt asc";
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManager("getTempRegistrationDetailsList");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, chasiNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                dobj = new OwnerDetailsDobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setState_name(rs.getString("state_name"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setOff_name(rs.getString("off_name"));
                dobj.setRegn_no(rs.getString("temp_regn_no"));
                //dobj.setRegnDateDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getRegn_dt())));
                dobj.setPurchase_dt(rs.getString("purchase_dt"));
                dobj.setPurchase_date(rs.getDate("purchase_dt"));
                //dobj.setPurchaseDateDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getPurchase_dt())));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setF_name(rs.getString("f_name"));
                dobj.setC_add1(rs.getString("c_add1"));
                dobj.setC_add2(rs.getString("c_add2"));
                dobj.setC_add3(rs.getString("c_add3"));
                dobj.setC_district(rs.getInt("c_district"));
                //dobj.setC_district_name(rs.getString("c_district_name"));
                dobj.setC_state(rs.getString("c_state"));
                //dobj.setC_state_name(rs.getString("c_state_name"));
                dobj.setC_pincode(rs.getInt("c_pincode"));
                dobj.setP_add1(rs.getString("p_add1"));
                dobj.setP_add2(rs.getString("p_add2"));
                dobj.setP_add3(rs.getString("p_add3"));
                dobj.setP_district(rs.getInt("p_district"));
                //dobj.setP_district_name(rs.getString("p_district_name"));
                dobj.setP_state(rs.getString("p_state"));
                //dobj.setP_state_name(rs.getString("p_state_name"));
                dobj.setP_pincode(rs.getInt("p_pincode"));
                dobj.setOwner_cd(rs.getInt("owner_cd"));
                //dobj.setOwner_cd_descr(rs.getString("owner_cd_descr"));
                dobj.setRegn_type(rs.getString("regn_type"));
                //dobj.setRegn_type_descr(rs.getString("regn_type_descr"));
                dobj.setVh_class(rs.getInt("vh_class"));
                //dobj.setVh_class_desc(rs.getString("vh_class_desc"));
                dobj.setChasi_no(rs.getString("chasi_no"));
                dobj.setEng_no(rs.getString("eng_no"));
                dobj.setMaker(rs.getInt("maker"));
                //dobj.setMaker_name(rs.getString("maker_name"));
                //dobj.setModel_cd(rs.getString("model_cd"));
                //dobj.setModel_name(rs.getString("model_name"));
                dobj.setBody_type(rs.getString("body_type"));
                dobj.setNo_cyl(rs.getInt("no_cyl"));
                dobj.setHp(rs.getFloat("hp"));
                dobj.setSeat_cap(rs.getInt("seat_cap"));
                dobj.setStand_cap(rs.getInt("stand_cap"));
                dobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                dobj.setUnld_wt(rs.getInt("unld_wt"));
                dobj.setLd_wt(rs.getInt("ld_wt"));
                dobj.setGcw(rs.getInt("gcw"));
                dobj.setFuel(rs.getInt("fuel"));
                //dobj.setFuel_descr(rs.getString("fuel_descr"));
                dobj.setColor(rs.getString("color"));
                dobj.setManu_mon(rs.getInt("manu_mon"));
                dobj.setManu_yr(rs.getInt("manu_yr"));
                dobj.setNorms(rs.getInt("norms"));
                //dobj.setNorms_descr(rs.getString("norms_descr"));
                dobj.setWheelbase(rs.getInt("wheelbase"));
                dobj.setCubic_cap(rs.getFloat("cubic_cap"));
                dobj.setFloor_area(rs.getFloat("floor_area"));
                dobj.setAc_fitted(rs.getString("ac_fitted"));
                dobj.setAudio_fitted(rs.getString("audio_fitted"));
                dobj.setVideo_fitted(rs.getString("video_fitted"));
                dobj.setVch_catg(rs.getString("vch_catg"));
                dobj.setSale_amt(rs.getInt("sale_amt"));
                dobj.setLaser_code(rs.getString("laser_code"));
                dobj.setGarage_add(rs.getString("garage_add"));
                dobj.setLength(rs.getInt("length"));
                dobj.setWidth(rs.getInt("width"));
                dobj.setHeight(rs.getInt("height"));
                dobj.setRegn_upto(rs.getString("regn_upto"));
                dobj.setRegnUptoDescr(rs.getString("valid_upto_descr"));
                dobj.setFit_upto(rs.getString("fit_upto"));
                //dobj.setFitUptoDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getFit_upto())));
                dobj.setAnnual_income(rs.getInt("annual_income"));
                dobj.setOp_dt(rs.getString("op_dt_descr"));
                dobj.setImported_vch(rs.getString("imported_vch"));
                dobj.setOther_criteria(rs.getInt("other_criteria"));
                dobj.setVch_purchase_as_code(rs.getString("vch_purchase_as"));

                if (ServerUtil.isTransport(dobj.getVh_class(), null)) {
                    dobj.setVehTypeDescr(TableConstants.VM_VEHTYPE_TRANSPORT_DESCR);
                    dobj.setVehType(TableConstants.VM_VEHTYPE_TRANSPORT);
                } else {
                    dobj.setVehTypeDescr(TableConstants.VM_VEHTYPE_NON_TRANSPORT_DESCR);
                    dobj.setVehType(TableConstants.VM_VEHTYPE_NON_TRANSPORT);
                }

                if (dobj.getVch_purchase_as_code().equalsIgnoreCase("B")) {
                    dobj.setVch_purchase_as("Fully Built");
                } else {
                    dobj.setVch_purchase_as("Drive Away Chasis");
                }
                if (rs.getString("state_cd_to") != null) {
                    Owner_temp_dobj tempDobj = new Owner_temp_dobj();
                    tempDobj.setState_cd_to(rs.getString("state_cd_to"));
                    tempDobj.setOff_cd_to(rs.getInt("off_cd_to"));
                    tempDobj.setTemp_regn_no(rs.getString("temp_regn_no"));
                    tempDobj.setPurpose(rs.getString("purpose"));
                    tempDobj.setPurposeDescr(rs.getString("temp_regn_reason"));
                    tempDobj.setBodyBuilding(rs.getString("body_building"));
                    tempDobj.setState_cd_to_descr(rs.getString("state_cd_to_descr"));
                    tempDobj.setOff_cd_to_descr(rs.getString("off_cd_to_descr"));
                    tempDobj.setValidFrom(rs.getDate("valid_from"));
                    tempDobj.setValidUpto(rs.getDate("valid_upto"));
                    dobj.setOwnerTempDobj(tempDobj);
                }

                //Setting Owenr Identification Details  
                /* String sqlOwnerId = "SELECT * FROM " + TableList.VT_OWNER_IDENTIFICATION
                 + " WHERE regn_no =? and state_cd = ? and off_cd = ? ";


                 ps = tmgr.prepareStatement(sqlOwnerId);
                 ps.setString(1, dobj.getRegn_no());
                 ps.setString(2, dobj.getState_cd());
                 ps.setInt(3, dobj.getOff_cd());

                 RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
                 OwnerIdentificationDobj own_identity = fillOwnerIdentityDobj(rs1);
                 if (own_identity != null) {
                 dobj.setOwnerIdentity(own_identity);
                 }*/
                ownerList.add(dobj);
            }

        } catch (VahanException vx) {
            throw vx;
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during getting Details of Temporary Registration Details, Please Contact to the System Administrator.");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during getting Details of Temporary Registration Details, Please Contact to the System Administrator.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return ownerList;
    }

    public static void main(String[] args) {
        System.out.println(OwnerImpl.cal(OwnerImpl.cal(OwnerImpl.cal(23, -32), 23), 8));
    }

    static int cal(int a, int b) {
        return (a > b ? a : b);
    }

    public static List getListSpeedGovTypes() {

        TransactionManager tmgr = null;
        List speedGovTypes = new ArrayList();
        try {
            tmgr = new TransactionManager("getOwnerCatgDepts");
            String sql = "SELECT * FROM vm_speed_gov_type ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                speedGovTypes.add(new SelectItem(rs.getInt("code"), rs.getString("descr")));
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
        return speedGovTypes;
    }

    public List<OwnerDetailsDobj> getDupOwnerDetails(String regnNo, String stateCd) throws VahanException {
        OwnerDetailsDobj dobj = null;
        if (regnNo == null || regnNo.trim().isEmpty()) {
            throw new VahanException("Blank Registration No not allowed.");
        }
        List<OwnerDetailsDobj> ownerList = new ArrayList<>();
        String sql = "";
        sql = "SELECT owner.*,type.descr as regn_type_descr,owcode.descr as owner_cd_descr,f.off_name,e.descr AS state_name"
                + " FROM " + TableList.VH_OWNER + " owner "
                + " inner join " + TableList.VH_DEDUPLICATION + " b on b.state_cd=owner.state_cd and b.off_cd=owner.off_cd and b.regn_no=owner.regn_no and owner.moved_on=b.op_dt "
                + " left join " + TableList.VM_REGN_TYPE + " type on owner.regn_type=type.regn_typecode "
                + " left join " + TableList.VM_OWCODE + " owcode on owner.owner_cd = owcode.ow_code "
                + " LEFT JOIN " + TableList.TM_OFFICE + " f ON f.off_cd = owner.off_cd AND f.state_cd = owner.state_cd "
                + " LEFT JOIN " + TableList.TM_STATE + " e ON e.state_code = owner.state_cd "
                + " WHERE owner.regn_no = ?";

        if (stateCd != null) {
            sql = sql + " and state_cd = ? ";
        }

        sql = sql + " order by owner.op_dt desc ";

        TransactionManager tmgr = null;
        PreparedStatement ps = null;

        try {
            tmgr = new TransactionManager("getDupOwnerDetails");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            if (stateCd != null) {
                ps.setString(2, stateCd);
            }
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            while (rs.next()) {
                dobj = new OwnerDetailsDobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setState_name(rs.getString("state_name"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setOff_name(rs.getString("off_name"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setRegn_dt(rs.getString("regn_dt"));
                dobj.setMoved_on(rs.getString("moved_on"));
                dobj.setRegnDateDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getRegn_dt())));
                dobj.setPurchase_dt(rs.getString("purchase_dt"));
                dobj.setPurchase_date(rs.getDate("purchase_dt"));
                dobj.setPurchaseDateDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getPurchase_dt())));
                dobj.setOwner_sr(rs.getInt("owner_sr"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setF_name(rs.getString("f_name"));
                dobj.setC_add1(rs.getString("c_add1"));
                dobj.setC_add2(rs.getString("c_add2"));
                dobj.setC_add3(rs.getString("c_add3"));
                dobj.setC_district(rs.getInt("c_district"));

                dobj.setStatus(rs.getString("status"));
                if (dobj.getStatus().equalsIgnoreCase("Y") || dobj.getStatus().equalsIgnoreCase("A")) {
                    dobj.setStatusDescr("Active");
                } else if (dobj.getStatus().equalsIgnoreCase("N")) {
                    dobj.setStatusDescr("Noc Issued");
                } else if (dobj.getStatus().equalsIgnoreCase("T")) {
                    dobj.setStatusDescr("Scrap Vehicle");
                } else if (dobj.getStatus().equalsIgnoreCase("S")) {
                    dobj.setStatusDescr("RC Surrender");
                } else if (dobj.getStatus().equalsIgnoreCase("C")) {
                    dobj.setStatusDescr("RC Cancel");
                } else {
                    dobj.setStatusDescr(dobj.getStatus());
                }
                String lastRcptdt = "select to_char(rcpt_dt,'DD-Mon-YYYY HH:MM:SS') as rcpt_dt from ((select rcpt_dt from vt_fee f where f.state_cd=? and f.off_cd=? and f.regn_no=? order by f.rcpt_dt desc limit 1)"
                        + " union all (select rcpt_dt from vt_tax where state_cd=? and off_cd=? and regn_no=? order by rcpt_dt desc limit 1)) as a order by rcpt_dt desc limit 1";
                ps = tmgr.prepareStatement(lastRcptdt);
                ps.setString(1, dobj.getState_cd());
                ps.setInt(2, dobj.getOff_cd());
                ps.setString(3, regnNo);
                ps.setString(4, dobj.getState_cd());
                ps.setInt(5, dobj.getOff_cd());
                ps.setString(6, regnNo);
                RowSet rs2 = tmgr.fetchDetachedRowSet_No_release();
                if (rs2.next()) {
                    if (rs2.getString("rcpt_dt") != null && !rs2.getString("rcpt_dt").isEmpty()) {
                        dobj.setLastRcptDt(rs2.getString("rcpt_dt"));
                    } else {
                        dobj.setLastRcptDt("No Fee/Tax Taken");
                    }
                } else {
                    dobj.setLastRcptDt("No Fee/Tax Taken");
                }
                ownerList.add(dobj);
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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

        return ownerList;
    }

    public Owner_dobj fillVaOwnerOther(Owner_dobj owner_dobj, RowSet rsOwnerId) throws SQLException {
        owner_dobj.setPush_bk_seat(rsOwnerId.getInt("push_back_seat"));
        owner_dobj.setOrdinary_seat(rsOwnerId.getInt("ordinary_seat"));
        return owner_dobj;
    }

    public void seatingCapacityValidation(int seatingCapacity, int vhClass, String vehClassSaparatedByComma) throws VahanException {
        if (vehClassSaparatedByComma != null && seatingCapacity == 0 && !vehClassSaparatedByComma.contains(String.valueOf(vhClass))) {
            throw new VahanException("Seating Capacity can not be ZERO for this Vehilce Class.");
        }
    }

    public OwnerDetailsDobj getVaOwnerAdminTempDetails(String applNo, String stateCd, int offCd) throws VahanException {
        OwnerDetailsDobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            String where_condition = "";
            TmConfigurationDobj configurationDobj = Util.getTmConfiguration();
            if (Util.getUserCategory() == null || configurationDobj == null) {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
            if (!Util.getUserCategory().equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_USER) && !configurationDobj.isAllow_fitness_all_RTO()) {
                where_condition = " and off_cd=?";
            }

            String sql = " SELECT owner.*,type.descr as regn_type_descr,owcode.descr as owner_cd_descr "
                    + " FROM " + TableList.VIEW_VVA_OWNER_TEMP_ADMIN + " owner "
                    + " left join vm_regn_type type on owner.regn_type=type.regn_typecode "
                    + " left join vm_owcode owcode on owner.owner_cd = owcode.ow_code "
                    + " WHERE appl_no =? and state_cd=? " + where_condition + " ";
            tmgr = new TransactionManager("getVaOwnerAdminTempDetails");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, stateCd);
            if (!"".equalsIgnoreCase(where_condition)) {
                ps.setInt(3, offCd);
            }
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new OwnerDetailsDobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setState_name(rs.getString("state_name"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setOff_name(rs.getString("off_name"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setRegn_dt(rs.getString("regn_dt"));
                dobj.setRegnDateDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getRegn_dt())));
                dobj.setPurchase_dt(rs.getString("purchase_dt"));
                dobj.setPurchase_date(rs.getDate("purchase_dt"));
                dobj.setPurchaseDateDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getPurchase_dt())));
                dobj.setOwner_sr(rs.getInt("owner_sr"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setF_name(rs.getString("f_name"));
                dobj.setC_add1(rs.getString("c_add1"));
                dobj.setC_add2(rs.getString("c_add2"));
                dobj.setC_add3(rs.getString("c_add3"));
                dobj.setC_district(rs.getInt("c_district"));
                dobj.setC_district_name(rs.getString("c_district_name"));
                dobj.setC_state(rs.getString("c_state"));
                dobj.setC_state_name(rs.getString("c_state_name"));
                dobj.setC_pincode(rs.getInt("c_pincode"));
                dobj.setP_add1(rs.getString("p_add1"));
                dobj.setP_add2(rs.getString("p_add2"));
                dobj.setP_add3(rs.getString("p_add3"));
                dobj.setP_district(rs.getInt("p_district"));
                dobj.setP_district_name(rs.getString("p_district_name"));
                dobj.setP_state(rs.getString("p_state"));
                dobj.setP_state_name(rs.getString("p_state_name"));
                dobj.setP_pincode(rs.getInt("p_pincode"));
                dobj.setOwner_cd(rs.getInt("owner_cd"));
                dobj.setOwner_cd_descr(rs.getString("owner_cd_descr"));
                dobj.setRegn_type(rs.getString("regn_type"));
                dobj.setRegn_type_descr(rs.getString("regn_type_descr"));
                dobj.setVh_class(rs.getInt("vh_class"));
                dobj.setVh_class_desc(rs.getString("vh_class_desc"));
                dobj.setChasi_no(rs.getString("chasi_no"));
                dobj.setEng_no(rs.getString("eng_no"));
                dobj.setMaker(rs.getInt("maker"));
                dobj.setMaker_name(rs.getString("maker_name"));
                dobj.setModel_cd(rs.getString("model_cd"));
                dobj.setModel_name(rs.getString("model_name"));
                dobj.setBody_type(rs.getString("body_type"));
                dobj.setNo_cyl(rs.getInt("no_cyl"));
                dobj.setHp(rs.getFloat("hp"));
                dobj.setSeat_cap(rs.getInt("seat_cap"));
                dobj.setStand_cap(rs.getInt("stand_cap"));
                dobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                dobj.setUnld_wt(rs.getInt("unld_wt"));
                dobj.setLd_wt(rs.getInt("ld_wt"));
                dobj.setGcw(rs.getInt("gcw"));
                dobj.setFuel(rs.getInt("fuel"));
                dobj.setFuel_descr(rs.getString("fuel_descr"));
                dobj.setColor(rs.getString("color"));
                dobj.setManu_mon(rs.getInt("manu_mon"));
                dobj.setManu_yr(rs.getInt("manu_yr"));
                dobj.setNorms(rs.getInt("norms"));
                dobj.setNorms_descr(rs.getString("norms_descr"));
                dobj.setWheelbase(rs.getInt("wheelbase"));
                dobj.setCubic_cap(rs.getFloat("cubic_cap"));
                dobj.setFloor_area(rs.getFloat("floor_area"));
                dobj.setAc_fitted(rs.getString("ac_fitted"));
                dobj.setAudio_fitted(rs.getString("audio_fitted"));
                dobj.setVideo_fitted(rs.getString("video_fitted"));
                dobj.setVch_purchase_as_code(rs.getString("vch_purchase_as"));
                dobj.setVch_catg(rs.getString("vch_catg"));
                dobj.setDealer_cd(rs.getString("dealer_cd"));
                dobj.setDlr_name(rs.getString("dlr_name"));
                dobj.setDlr_add1(rs.getString("dlr_add1"));
                dobj.setDlr_add2(rs.getString("dlr_add2"));
                dobj.setDlr_add3(rs.getString("dlr_add3"));
                dobj.setDlr_city(rs.getString("dlr_city"));
                dobj.setDlr_district(rs.getString("dlr_district"));
                dobj.setDlr_pincode(rs.getString("dlr_pincode"));
                dobj.setSale_amt(rs.getInt("sale_amt"));
                dobj.setLaser_code(rs.getString("laser_code"));
                dobj.setGarage_add(rs.getString("garage_add"));
                dobj.setLength(rs.getInt("length"));
                dobj.setWidth(rs.getInt("width"));
                dobj.setHeight(rs.getInt("height"));
                dobj.setRegn_upto(rs.getString("regn_upto"));
                dobj.setRegnUptoDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getRegn_upto())));
                dobj.setFit_upto(rs.getString("fit_upto"));
                dobj.setFitUptoDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getFit_upto())));
                dobj.setAnnual_income(rs.getInt("annual_income"));
                dobj.setOp_dt(rs.getString("op_dt"));
                dobj.setImported_vch(rs.getString("imported_vch"));
                dobj.setOther_criteria(rs.getInt("other_criteria"));
                dobj.setPmt_type(rs.getInt("pmt_type"));
                dobj.setPmt_catg(rs.getInt("pmt_catg"));
                dobj.setRqrd_tax_modes(rs.getString("rqrd_tax_modes"));
                dobj.setVehType(ServerUtil.VehicleClassType(dobj.getVh_class()));

                if (ServerUtil.isTransport(dobj.getVh_class(), null)) {
                    dobj.setVehTypeDescr(TableConstants.VM_VEHTYPE_TRANSPORT_DESCR);
                    dobj.setVehType(TableConstants.VM_VEHTYPE_TRANSPORT);
                    dobj.setVch_type(TableConstants.VM_VEHTYPE_TRANSPORT_DESCR);
                } else {
                    dobj.setVehTypeDescr(TableConstants.VM_VEHTYPE_NON_TRANSPORT_DESCR);
                    dobj.setVehType(TableConstants.VM_VEHTYPE_NON_TRANSPORT);
                    dobj.setVch_type(TableConstants.VM_VEHTYPE_NON_TRANSPORT_DESCR);
                }

                if (dobj.getVch_purchase_as_code().equalsIgnoreCase("B")) {
                    dobj.setVch_purchase_as("Fully Built");
                } else {
                    dobj.setVch_purchase_as("Drive Away Chasis");
                }

                //Setting Owenr Identification Details                
                String sqlOwnerId = "SELECT * FROM " + TableList.VA_OWNER_IDENTIFICATION
                        + " WHERE appl_no =?";
                ps = tmgr.prepareStatement(sqlOwnerId);
                ps.setString(1, applNo);
                rs = tmgr.fetchDetachedRowSet_No_release();
                OwnerIdentificationDobj own_identity = fillOwnerIdentityDobj(rs);
                if (own_identity != null) {
                    dobj.setOwnerIdentity(own_identity);
                }
            }
        } catch (VahanException ve) {
            throw ve;
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

        return dobj;
    }
//vt_owner_temp fill ownerDetialsDobj Niraj

    public OwnerDetailsDobj getTempOwnerDetails(String regnNo, String stateCd, int offCd) throws VahanException {
        OwnerDetailsDobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            String sql = " SELECT owner.*,type.descr as regn_type_descr,owcode.descr as owner_cd_descr, to_char(valid_from,'DD-Mon-YYYY') as valid_from_desc,to_char(valid_upto,'DD-Mon-YYYY') as valid_upto_desc "
                    + " FROM " + TableList.VIEW_VV_OWNER_TEMP + " owner "
                    + " left join vm_regn_type type on owner.regn_type=type.regn_typecode "
                    + " left join vm_owcode owcode on owner.owner_cd = owcode.ow_code "
                    + " WHERE temp_regn_no =? and state_cd=? and off_cd=? ";
            tmgr = new TransactionManager("getTempOwnerDetails");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new OwnerDetailsDobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setState_name(rs.getString("state_name"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setOff_name(rs.getString("off_name"));
                dobj.setRegn_no(rs.getString("temp_regn_no"));
                dobj.setRegn_dt(rs.getString("valid_from_desc"));
                dobj.setRegnDateDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getRegn_dt())));
                dobj.setPurchase_dt(rs.getString("purchase_dt"));
                dobj.setPurchaseDateDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getPurchase_dt())));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setF_name(rs.getString("f_name"));
                dobj.setC_add1(rs.getString("c_add1"));
                dobj.setC_add2(rs.getString("c_add2"));
                dobj.setC_add3(rs.getString("c_add3"));
                dobj.setC_district(rs.getInt("c_district"));
                dobj.setC_district_name(rs.getString("c_district_name"));
                dobj.setC_state(rs.getString("c_state"));
                dobj.setC_state_name(rs.getString("c_state_name"));
                dobj.setC_pincode(rs.getInt("c_pincode"));
                dobj.setP_add1(rs.getString("p_add1"));
                dobj.setP_add2(rs.getString("p_add2"));
                dobj.setP_add3(rs.getString("p_add3"));
                dobj.setP_district(rs.getInt("p_district"));
                dobj.setP_district_name(rs.getString("p_district_name"));
                dobj.setP_state(rs.getString("p_state"));
                dobj.setP_state_name(rs.getString("p_state_name"));
                dobj.setP_pincode(rs.getInt("p_pincode"));
                dobj.setOwner_cd(rs.getInt("owner_cd"));
                dobj.setOwner_cd_descr(rs.getString("owner_cd_descr"));
                dobj.setRegn_type(rs.getString("regn_type"));
                dobj.setRegn_type_descr(rs.getString("regn_type_descr"));
                dobj.setVh_class(rs.getInt("vh_class"));
                dobj.setVh_class_desc(rs.getString("vh_class_desc"));
                dobj.setChasi_no(rs.getString("chasi_no"));
                dobj.setEng_no(rs.getString("eng_no"));
                dobj.setMaker(rs.getInt("maker"));
                dobj.setMaker_name(rs.getString("maker_name"));
                dobj.setModel_cd(rs.getString("model_cd"));
                dobj.setModel_name(rs.getString("model_name"));
                dobj.setBody_type(rs.getString("body_type"));
                dobj.setNo_cyl(rs.getInt("no_cyl"));
                dobj.setHp(rs.getFloat("hp"));
                dobj.setSeat_cap(rs.getInt("seat_cap"));
                dobj.setStand_cap(rs.getInt("stand_cap"));
                dobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                dobj.setUnld_wt(rs.getInt("unld_wt"));
                dobj.setLd_wt(rs.getInt("ld_wt"));
                dobj.setGcw(rs.getInt("gcw"));
                dobj.setFuel(rs.getInt("fuel"));
                dobj.setFuel_descr(rs.getString("fuel_descr"));
                dobj.setColor(rs.getString("color"));
                dobj.setManu_mon(rs.getInt("manu_mon"));
                dobj.setManu_yr(rs.getInt("manu_yr"));
                dobj.setNorms(rs.getInt("norms"));
                dobj.setNorms_descr(rs.getString("norms_descr"));
                dobj.setWheelbase(rs.getInt("wheelbase"));
                dobj.setCubic_cap(rs.getFloat("cubic_cap"));
                dobj.setFloor_area(rs.getFloat("floor_area"));
                dobj.setAc_fitted(rs.getString("ac_fitted"));
                dobj.setAudio_fitted(rs.getString("audio_fitted"));
                dobj.setVideo_fitted(rs.getString("video_fitted"));
                dobj.setVch_catg(rs.getString("vch_catg"));
                dobj.setDealer_cd(rs.getString("dealer_cd"));
                dobj.setDlr_name(rs.getString("dlr_name"));
                dobj.setDlr_add1(rs.getString("dlr_add1"));
                dobj.setDlr_add2(rs.getString("dlr_add2"));
                dobj.setDlr_add3(rs.getString("dlr_add3"));
                dobj.setDlr_city(rs.getString("dlr_city"));
                dobj.setDlr_district(rs.getString("dlr_district"));
                dobj.setDlr_pincode(rs.getString("dlr_pincode"));
                dobj.setSale_amt(rs.getInt("sale_amt"));
                dobj.setLaser_code(rs.getString("laser_code"));
                dobj.setGarage_add(rs.getString("garage_add"));
                dobj.setLength(rs.getInt("length"));
                dobj.setWidth(rs.getInt("width"));
                dobj.setHeight(rs.getInt("height"));
                dobj.setRegn_upto(rs.getString("regn_upto"));
                dobj.setRegnUptoDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getRegn_upto())));
                dobj.setFit_upto(rs.getString("valid_upto_desc"));
                dobj.setFitUptoDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getFit_upto())));
                dobj.setAnnual_income(rs.getInt("annual_income"));
                dobj.setOp_dt(rs.getString("op_dt"));
                dobj.setImported_vch(rs.getString("imported_vch"));
                dobj.setOther_criteria(rs.getInt("other_criteria"));
                dobj.setVch_purchase_as_code(rs.getString("vch_purchase_as"));
                if (ServerUtil.isTransport(dobj.getVh_class(), null)) {
                    dobj.setVehTypeDescr(TableConstants.VM_VEHTYPE_TRANSPORT_DESCR);
                    dobj.setVehType(TableConstants.VM_VEHTYPE_TRANSPORT);
                } else {
                    dobj.setVehTypeDescr(TableConstants.VM_VEHTYPE_NON_TRANSPORT_DESCR);
                    dobj.setVehType(TableConstants.VM_VEHTYPE_NON_TRANSPORT);
                }

                if (dobj.getVch_purchase_as_code().equalsIgnoreCase("B")) {
                    dobj.setVch_purchase_as("Fully Built");
                } else {
                    dobj.setVch_purchase_as("Drive Away Chasis");
                }
                //Setting Owenr Identification Details                
                String sqlOwnerId = "SELECT * FROM " + TableList.VT_OWNER_IDENTIFICATION
                        + " WHERE regn_no =? and state_cd = ? and off_cd  = ?";
                ps = tmgr.prepareStatement(sqlOwnerId);
                ps.setString(1, dobj.getRegn_no());
                ps.setString(2, stateCd);
                ps.setInt(3, offCd);
                rs = tmgr.fetchDetachedRowSet_No_release();
                OwnerIdentificationDobj own_identity = fillOwnerIdentityDobj(rs);
                if (own_identity != null) {
                    dobj.setOwnerIdentity(own_identity);
                }
            }

        } catch (VahanException vx) {
            throw vx;
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
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

        return dobj;
    }

    public OwnerDetailsDobj getOwnerDetailsWithOffice(String regnNo, String stateCd, int offCd) throws VahanException {
        OwnerDetailsDobj dobj = null;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        try {
            String sql = " SELECT owner.*,type.descr as regn_type_descr,owcode.descr as owner_cd_descr,vtowner.push_back_seat,vtowner.ordinary_seat "
                    + " FROM " + TableList.VIEW_VV_OWNER + " owner "
                    + " left join vm_regn_type type on owner.regn_type=type.regn_typecode "
                    + " left join vm_owcode owcode on owner.owner_cd = owcode.ow_code "
                    + " left join " + TableList.VT_OWNER_OTHER + "  vtowner on owner.state_cd = vtowner.state_cd and  owner.off_cd = vtowner.off_cd "
                    + " and owner.regn_no = vtowner.regn_no"
                    + " WHERE owner.regn_no =? and owner.state_cd=? ";
            if (offCd != 0) {
                sql += " and owner.off_cd=?";
            }
            tmgr = new TransactionManagerReadOnly("getOwnerDetailsWithOffice");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            if (offCd != 0) {
                ps.setInt(3, offCd);
            }
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new OwnerDetailsDobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setState_name(rs.getString("state_name"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setOff_name(rs.getString("off_name"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setRegn_dt(rs.getString("regn_dt"));
                dobj.setRegnDateDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getRegn_dt())));
                dobj.setPurchase_dt(rs.getString("purchase_dt"));
                dobj.setPurchaseDateDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getPurchase_dt())));
                dobj.setOwner_sr(rs.getInt("owner_sr"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setF_name(rs.getString("f_name"));
                dobj.setC_add1(rs.getString("c_add1"));
                dobj.setC_add2(rs.getString("c_add2"));
                dobj.setC_add3(rs.getString("c_add3"));
                dobj.setC_district(rs.getInt("c_district"));
                dobj.setC_district_name(rs.getString("c_district_name"));
                dobj.setC_state(rs.getString("c_state"));
                dobj.setC_state_name(rs.getString("c_state_name"));
                dobj.setC_pincode(rs.getInt("c_pincode"));
                dobj.setP_add1(rs.getString("p_add1"));
                dobj.setP_add2(rs.getString("p_add2"));
                dobj.setP_add3(rs.getString("p_add3"));
                dobj.setP_district(rs.getInt("p_district"));
                dobj.setP_district_name(rs.getString("p_district_name"));
                dobj.setP_state(rs.getString("p_state"));
                dobj.setP_state_name(rs.getString("p_state_name"));
                dobj.setP_pincode(rs.getInt("p_pincode"));
                dobj.setOwner_cd(rs.getInt("owner_cd"));
                dobj.setOwner_cd_descr(rs.getString("owner_cd_descr"));
                dobj.setRegn_type(rs.getString("regn_type"));
                dobj.setRegn_type_descr(rs.getString("regn_type_descr"));
                dobj.setVh_class(rs.getInt("vh_class"));
                dobj.setVh_class_desc(rs.getString("vh_class_desc"));
                dobj.setChasi_no(rs.getString("chasi_no"));
                dobj.setEng_no(rs.getString("eng_no"));
                dobj.setMaker(rs.getInt("maker"));
                dobj.setMaker_name(rs.getString("maker_name"));
                dobj.setModel_cd(rs.getString("model_cd"));
                dobj.setModel_name(rs.getString("model_name"));
                dobj.setBody_type(rs.getString("body_type"));
                dobj.setNo_cyl(rs.getInt("no_cyl"));
                dobj.setHp(rs.getFloat("hp"));
                dobj.setSeat_cap(rs.getInt("seat_cap"));
                dobj.setStand_cap(rs.getInt("stand_cap"));
                dobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                dobj.setUnld_wt(rs.getInt("unld_wt"));
                dobj.setLd_wt(rs.getInt("ld_wt"));
                dobj.setGcw(rs.getInt("gcw"));
                dobj.setFuel(rs.getInt("fuel"));
                dobj.setFuel_descr(rs.getString("fuel_descr"));
                dobj.setColor(rs.getString("color"));
                dobj.setManu_mon(rs.getInt("manu_mon"));
                dobj.setManu_yr(rs.getInt("manu_yr"));
                dobj.setNorms(rs.getInt("norms"));
                dobj.setNorms_descr(rs.getString("norms_descr"));
                dobj.setWheelbase(rs.getInt("wheelbase"));
                dobj.setCubic_cap(rs.getFloat("cubic_cap"));
                dobj.setFloor_area(rs.getFloat("floor_area"));
                dobj.setAc_fitted(rs.getString("ac_fitted"));
                dobj.setAudio_fitted(rs.getString("audio_fitted"));
                dobj.setVideo_fitted(rs.getString("video_fitted"));
                dobj.setVch_catg(rs.getString("vch_catg"));
                dobj.setDealer_cd(rs.getString("dealer_cd"));
                dobj.setDlr_name(rs.getString("dlr_name"));
                dobj.setDlr_add1(rs.getString("dlr_add1"));
                dobj.setDlr_add2(rs.getString("dlr_add2"));
                dobj.setDlr_add3(rs.getString("dlr_add3"));
                dobj.setDlr_city(rs.getString("dlr_city"));
                dobj.setDlr_district(rs.getString("dlr_district"));
                dobj.setDlr_pincode(rs.getString("dlr_pincode"));
                dobj.setSale_amt(rs.getInt("sale_amt"));
                dobj.setLaser_code(rs.getString("laser_code"));
                dobj.setGarage_add(rs.getString("garage_add"));
                dobj.setLength(rs.getInt("length"));
                dobj.setWidth(rs.getInt("width"));
                dobj.setHeight(rs.getInt("height"));
                dobj.setRegn_upto(rs.getString("regn_upto"));
                dobj.setRegnUptoDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getRegn_upto())));
                dobj.setFit_upto(rs.getString("fit_upto"));
                dobj.setFitUptoDescr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(dobj.getFit_upto())));
                dobj.setAnnual_income(rs.getInt("annual_income"));
                dobj.setOp_dt(rs.getString("op_dt"));
                dobj.setImported_vch(rs.getString("imported_vch"));
                dobj.setOther_criteria(rs.getInt("other_criteria"));
                dobj.setStatus(rs.getString("status"));
                dobj.setVch_purchase_as_code(rs.getString("vch_purchase_as"));
                dobj.setPush_bk_seat(rs.getInt("push_back_seat"));
                dobj.setOrdinary_seat(rs.getInt("ordinary_seat"));

                if (ServerUtil.isTransport(dobj.getVh_class(), null)) {
                    dobj.setVehTypeDescr(TableConstants.VM_VEHTYPE_TRANSPORT_DESCR);
                    dobj.setVehType(TableConstants.VM_VEHTYPE_TRANSPORT);
                } else {
                    dobj.setVehTypeDescr(TableConstants.VM_VEHTYPE_NON_TRANSPORT_DESCR);
                    dobj.setVehType(TableConstants.VM_VEHTYPE_NON_TRANSPORT);
                }

                if (dobj.getVch_purchase_as_code().equalsIgnoreCase("B")) {
                    dobj.setVch_purchase_as("Fully Built");
                } else {
                    dobj.setVch_purchase_as("Drive Away Chasis");
                }

                //Setting Owenr Identification Details                
                String sqlOwnerId = "SELECT * FROM " + TableList.VT_OWNER_IDENTIFICATION
                        + " WHERE regn_no =? and state_cd = ?";
                if (offCd != 0) {
                    sqlOwnerId += "  and off_cd  = ?";
                }
                ps = tmgr.prepareStatement(sqlOwnerId);
                ps.setString(1, dobj.getRegn_no());
                ps.setString(2, stateCd);
                if (offCd != 0) {
                    ps.setInt(3, offCd);
                }
                rs = tmgr.fetchDetachedRowSet_No_release();
                OwnerIdentificationDobj own_identity = fillOwnerIdentityDobj(rs);
                if (own_identity != null) {
                    dobj.setOwnerIdentity(own_identity);
                }

                //Blacklisted Vehicle Information
                BlackListedVehicleImpl obj = new BlackListedVehicleImpl();
                BlackListedVehicleDobj blackListedDobj = obj.getBlacklistedVehicleDetails(dobj.getRegn_no(), dobj.getChasi_no());
                if (blackListedDobj != null) {
                    dobj.setBlackListedVehicleDobj(blackListedDobj);
                }
            }

        } catch (VahanException vx) {
            throw vx;
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
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

        return dobj;
    }

    public static void validatePurchaseAs(Owner_dobj dobj, String regnType, int purCd) throws VahanException {
        if (dobj == null || dobj.getVch_purchase_as() == null || (purCd == 18 || purCd == 124 || regnType == null)) {
            return;
        }

        if (dobj.getVch_purchase_as().equals("C") && regnType.equals("N")) {
            throw new VahanException("Vehicle have been Purchase as 'Drive Away Chassis', Vehicle should have the 'Temporary Registration' so please select Registration Type as 'Temporary Registered Vehicle'.");
        }
    }

    public static int checkPanNoCount(String panNo, String stateCd) throws VahanException {
        int panCount = 0;
        //Check same PAN No exist For Motor Cars Only with state_cd in VT_OWNER (NEW Registration) Table Only
        String sql = "Select count(1) as total FROM " + TableList.VT_OWNER_IDENTIFICATION + " a "
                + " inner join " + TableList.VT_OWNER + " b on b.regn_no=a.regn_no and b.state_cd=a.state_cd and b.vh_class=7 and b.status in ('Y', 'A') "
                + " where a.pan_no=? and a.state_cd=? ";
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("checkPanNoCount");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, panNo);
            ps.setString(2, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                panCount = rs.getInt("total");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Getting Pan Card details");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

        return panCount;
    }

    public static String verifyInVhReAssgin(String regnNo) {
        int i = 1;
        String returnString = "";
        TransactionManagerReadOnly tmg = null;
        PreparedStatement ps = null;
        try {
            String sqlString = "select new_regn_no as regn_no from " + TableList.VH_RE_ASSIGN + " where old_regn_no=? "
                    + " and old_regn_no not in (Select regn_no from vt_owner where regn_no = ?) ";

            tmg = new TransactionManagerReadOnly("verifyInVhReAssgin");
            ps = tmg.prepareStatement(sqlString);
            ps.setString(i++, regnNo);
            ps.setString(i++, regnNo);

            RowSet rs = tmg.fetchDetachedRowSet();
            if (rs.next()) {
                return ("This Registration Number has been allotted following Registration No : " + rs.getString("regn_no"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmg != null) {
                try {
                    tmg.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

        return returnString;

    }

    public static void insertUpdateFastagSchedular(String applNo, String oldStateCd, int oldOffCd, String oldRegnNo,
            String newStateCd, int newOffCd, String newRegnNo, String chasiNo,
            TransactionManager tmgr) throws VahanException {
        if (newRegnNo == null || "NEW,TEMPREG".contains(newRegnNo) || newRegnNo.matches(".*/+.*")
                || "NEW,TEMPREG".contains(oldRegnNo) || oldRegnNo.matches(".*/+.*")) {
            return;
        }

        try {
            String sql = "Select * from " + TableList.VT_FASTAG + " where chasi_no = ?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, chasiNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                sql = "Select * from " + TableList.VT_FASTTAG_SCHEDULAR + " where appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, applNo);
                RowSet rsFastagSch = tmgr.fetchDetachedRowSet_No_release();
                if (!rsFastagSch.next()) {
                    int i = 1;
                    sql = "INSERT INTO " + TableList.VT_FASTTAG_SCHEDULAR
                            + "(appl_no, new_state_cd, new_off_cd, new_regn_no, old_state_cd, old_off_cd, old_regn_no, op_dt, status, count)\n"
                            + " VALUES (?, ?, ?, ?, ?, ?, ?,  current_timestamp, 'P',0)";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(i++, applNo);
                    ps.setString(i++, newStateCd);
                    ps.setInt(i++, newOffCd);
                    ps.setString(i++, newRegnNo);
                    ps.setString(i++, oldStateCd);
                    ps.setInt(i++, oldOffCd);
                    ps.setString(i++, oldRegnNo);
                    ps.executeUpdate();
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Fast Tag Details");
        }
    }

    public Owner_dobj set_Owner_appl_db_to_dobj_with_state_off_cd(String regn_no, String appl_no, String chasi_no, int pur_cd, TransactionManager tmgr) throws Exception {

        PreparedStatement ps = null;
        Owner_dobj owner_dobj = null;
        RowSet rs = null;

        if (regn_no != null) {
            regn_no = regn_no.toUpperCase();
        }
        if (appl_no != null) {
            appl_no = appl_no.toUpperCase();
        }
        if (chasi_no != null) {
            chasi_no = chasi_no.toUpperCase();
        }

        try {
            String query = "";
            String parameterValue = "";
            if (TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE == pur_cd
                    || TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE == pur_cd
                    || TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION == pur_cd
                    || TableConstants.VM_TRANSACTION_MAST_NOC == pur_cd) {
                if (appl_no != null && !appl_no.equalsIgnoreCase("")) {
                    query = "select *,state.descr as current_state_name,dist.descr as current_district_name from " + TableList.VA_OWNER + " owner "
                            + " left join " + TableList.VA_SIDE_TRAILER + " trailer on owner.appl_no = trailer.appl_no "
                            + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                            + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                            + " where owner.appl_no=? ";
                    parameterValue = appl_no;

                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, parameterValue);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    owner_dobj = fillFrom_Va_OwnerDobj(rs);

                } else if (chasi_no != null && !chasi_no.equalsIgnoreCase("")) {
                    query = "select *,state.descr as current_state_name,dist.descr as current_district_name from  " + TableList.VA_OWNER + " owner "
                            + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                            + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                            + " where owner.chasi_no=?";
                    parameterValue = chasi_no;

                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, parameterValue);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    owner_dobj = fillFrom_Va_OwnerDobj(rs);

                    if (owner_dobj == null) {
                        query = "select *,state.descr as current_state_name,dist.descr as current_district_name from  " + TableList.VT_OWNER + " owner "
                                + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                                + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                                + " where owner.chasi_no=? and owner.state_cd = ? and owner.off_cd = ? ";

                        ps = tmgr.prepareStatement(query);
                        ps.setString(1, parameterValue);
                        ps.setString(2, Util.getUserStateCode());
                        ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        owner_dobj = fillFrom_VT_OwnerDobj_with_state_off_cd(rs);
                    }

                } else if (regn_no != null && !regn_no.equalsIgnoreCase("")) {
                    query = "select *,state.descr as current_state_name,dist.descr as current_district_name from  " + TableList.VT_OWNER + " owner "
                            + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                            + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                            + " where owner.regn_no=? and owner.state_cd = ? and owner.off_cd = ?";
                    parameterValue = regn_no;
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, parameterValue);
                    ps.setString(2, Util.getUserStateCode());
                    ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    owner_dobj = fillFrom_VT_OwnerDobj_with_state_off_cd(rs);
                }
            } else if (TableConstants.VM_TRANSACTION_MAST_TEMP_REG == pur_cd || TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE == pur_cd) {
                if (appl_no != null && !appl_no.equalsIgnoreCase("")) {
                    query = "select *,state.descr as current_state_name,dist.descr as current_district_name from " + TableList.VA_OWNER + " owner "
                            + " left join " + TableList.VA_SIDE_TRAILER + " trailer on owner.appl_no = trailer.appl_no "
                            + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                            + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                            + " where owner.appl_no=? ";
                    parameterValue = appl_no;

                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, parameterValue);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    owner_dobj = fillFrom_Va_OwnerDobj(rs);
                    //add amit
                    if (owner_dobj == null) {
                        query = "select *,state.descr as current_state_name,dist.descr as current_district_name from " + TableList.VA_OWNER_TEMP_ADMIN + " owner_admin "
                                + " left join " + TableList.VA_SIDE_TRAILER + " trailer on owner_admin.appl_no = trailer.appl_no "
                                + " left join " + TableList.TM_STATE + " state on owner_admin.c_state = state.state_code "
                                + " left join " + TableList.TM_DISTRICT + " dist on owner_admin.c_district = dist.dist_cd "
                                + " where owner_admin.appl_no=? ";
                        parameterValue = appl_no;

                        ps = tmgr.prepareStatement(query);
                        ps.setString(1, parameterValue);
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        owner_dobj = fillFrom_Va_OwnerDobj(rs);
                    }
                    if (owner_dobj != null) {
                        ps = tmgr.prepareStatement("select * from va_owner_temp where appl_no=? ");
                        ps.setString(1, parameterValue);
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        Owner_temp_dobj temp = fillFrom_va_owner_temp(rs);
                        if (temp != null) {
                            temp.setAppl_no(appl_no);
                        }
                        owner_dobj.setDob_temp(temp);
                    }

                } else if (chasi_no != null && !chasi_no.equalsIgnoreCase("")) {
                    query = "select *,state.descr as current_state_name,dist.descr as current_district_name from  " + TableList.VA_OWNER + " owner "
                            + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                            + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                            + " where owner.chasi_no=?";
                    parameterValue = chasi_no;

                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, parameterValue);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    owner_dobj = fillFrom_Va_OwnerDobj(rs);
                    if (owner_dobj != null) {
                        ps = tmgr.prepareStatement("select * from va_owner_temp where appl_no=? ");
                        ps.setString(1, owner_dobj.getAppl_no());
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        Owner_temp_dobj temp = fillFrom_va_owner_temp(rs);
                        if (temp != null) {
                            temp.setAppl_no(appl_no);
                        }
                        owner_dobj.setDob_temp(temp);
                    }

                    //validate from vt_owner_temp
                    if (owner_dobj == null) {
                        query = "select *,state.descr as current_state_name,dist.descr as current_district_name from  " + TableList.VT_OWNER_TEMP + " owner "
                                + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                                + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                                + " where owner.chasi_no=? and owner.state_cd = ? and owner.off_cd = ? ";

                        ps = tmgr.prepareStatement(query);
                        ps.setString(1, parameterValue);
                        ps.setString(2, Util.getUserStateCode());
                        ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        owner_dobj = fillFrom_vt_owner_temp_with_state_cd_off_cd(rs);
                    }

                } else if (regn_no != null && !regn_no.equalsIgnoreCase("")) {
                    query = "select *,state.descr as current_state_name,dist.descr as current_district_name from  " + TableList.VT_OWNER_TEMP + " owner "
                            + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                            + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                            + " where owner.temp_regn_no=? order by owner.op_dt desc limit 1";
                    parameterValue = regn_no;
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, parameterValue);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    owner_dobj = fillFrom_vt_owner_temp_with_state_cd_off_cd(rs);
                }
            } else {
                if (regn_no != null) {
                    //add Top
                    int off_cd = Util.getSelectedSeat().getOff_cd();
                    TmConfigurationDobj tm_Cong = Util.getTmConfiguration();
                    boolean allow_fitness_all_rto = tm_Cong.isAllow_fitness_all_RTO();
                    if (allow_fitness_all_rto) {
                        query = "select off_cd from  " + TableList.VT_OWNER + " where regn_no=? and state_cd = ?";
                        ps = tmgr.prepareStatement(query);
                        ps.setString(1, regn_no);
                        ps.setString(2, Util.getUserStateCode());
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        if (rs.next()) {
                            int off_cd_other_vechile = rs.getInt("off_cd");
                            if (off_cd_other_vechile != off_cd) {
                                off_cd = off_cd_other_vechile;
                            }
                        }
                    }
                    query = "select *,state.descr as current_state_name,dist.descr as current_district_name from  " + TableList.VT_OWNER + " owner "
                            + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                            + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                            + " where owner.regn_no=? and owner.state_cd = ? and owner.off_cd = ?";

                    parameterValue = regn_no;
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, parameterValue);
                    ps.setString(2, Util.getUserStateCode());
                    ps.setInt(3, off_cd);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    owner_dobj = fillFrom_VT_OwnerDobj_with_state_off_cd(rs);
                } else if (chasi_no != null) {
                    query = "select *,state.descr as current_state_name,dist.descr as current_district_name from  " + TableList.VT_OWNER + " owner "
                            + " left join " + TableList.TM_STATE + " state on owner.c_state = state.state_code "
                            + " left join " + TableList.TM_DISTRICT + " dist on owner.c_district = dist.dist_cd "
                            + " where owner.chasi_no=? and owner.state_cd = ? and owner.off_cd = ?";
                    parameterValue = chasi_no;
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, parameterValue);
                    ps.setString(2, Util.getUserStateCode());
                    ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    owner_dobj = fillFrom_VT_OwnerDobj_with_state_off_cd(rs);

                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error In Database Update");
        }

        return owner_dobj;
    }

}
