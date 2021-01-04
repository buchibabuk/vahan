/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.eChallan;

import java.io.Serializable;
import nic.vahan.form.dobj.eChallan.AccusedDetailsDobj;
import nic.vahan.form.dobj.eChallan.CompoundingFeeDobj;
import nic.vahan.form.dobj.eChallan.DocTableDobj;
import nic.vahan.form.dobj.eChallan.OffencesDobj;
import nic.vahan.form.dobj.eChallan.SaveChallanDobj;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.eChallan.ChallanReportDobj;
import nic.vahan.form.impl.Receipt_Master_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.dobj.eChallan.WitnessdetailDobj;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import org.apache.log4j.Logger;

/**
 *
 * @author sushil
 */
public class SaveChallanImpl implements Serializable {
    
    private static Logger LOGGER = Logger.getLogger(SaveChallanImpl.class);
    
    public List getOffenceList(String vhClass) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        List offencelist = new ArrayList();
        SelectItem item = new SelectItem("-1", "Select Offence ");
        offencelist.add(item);
        try {
            String sql = "select offpenalty.offence_cd,offence.offence_desc\n"
                    + " from " + TableList.VM_OFFENCE_PENALTY + " offpenalty \n"
                    + " INNER JOIN  " + TableList.VM_OFFENCES + " offence ON offence.offence_cd= offpenalty.offence_cd and offence.state_cd= offpenalty.state_cd \n"
                    + " Where vh_class=? and offpenalty.state_cd =?";
            tmgr = new TransactionManager("SaveChallanDAO: getAddedDoc");
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(vhClass));
            ps.setString(2, Util.getUserStateCode());
            RowSet resulSet = tmgr.fetchDetachedRowSet();
            while (resulSet.next()) {
                item = new SelectItem(resulSet.getString("offence_cd"), resulSet.getString("offence_desc"));
                offencelist.add(item);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return offencelist;
    }
    
    public List getOffenceAccusedWise(String vhClass, String accused) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        List offencelist = new ArrayList();
        SelectItem item = new SelectItem("-1", "Select Offence ");
        offencelist.add(item);
        try {
            String sql = "select offpenalty.offence_cd,offence.offence_desc\n"
                    + " from echallan.vm_offence_penalty offpenalty \n"
                    + " INNER JOIN  echallan.vm_offences offence ON offence.offence_cd= offpenalty.offence_cd and offence.state_cd= offpenalty.state_cd\n"
                    + " Where vh_class=? and offpenalty.state_cd =? and offence_applied_on LIKE ('%" + accused + "%')\n";
            
            tmgr = new TransactionManager("SaveChallanDAO: getAddedDoc");
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(vhClass));
            ps.setString(2, Util.getUserStateCode());
            RowSet resulSet = tmgr.fetchDetachedRowSet();
            while (resulSet.next()) {
                item = new SelectItem(resulSet.getString("offence_cd"), resulSet.getString("offence_desc"));
                offencelist.add(item);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return offencelist;
    }
    
    public String fetchchallanNo() throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String challanNo = "";
        try {
            tmgr = new TransactionManager("fetchchallanNo");
            String currChallanNo = "SELECT user_cd, book_no, chal_no_from, chal_no_upto, chal_no_current, \n"
                    + " expired, issue_dt, issue_by, state_cd, off_cd  from " + TableList.VM_CHALLAN_BOOK + " where user_cd=?";
            ps = tmgr.prepareStatement(currChallanNo);
            ps.setInt(1, Integer.parseInt(Util.getEmpCode()));
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                challanNo = rs.getString("state_cd") + rs.getString("off_cd") + rs.getString("chal_no_current");
            } else {
                challanNo = ServerUtil.getUniqueChallanNo(tmgr, Util.getUserStateCode());
                tmgr.commit();
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return challanNo;
    }
    
    public void updateChallanNo(String userCd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = "SELECT  book_no , chal_no_current from " + TableList.VM_CHALLAN_BOOK + " where user_cd=?";
        try {
            tmgr = new TransactionManager("SaveChallanDAO");
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(userCd));
            RowSet rowSet = tmgr.fetchDetachedRowSet();
            if (rowSet.next()) {
                sql = "update " + TableList.VM_CHALLAN_BOOK + " set chal_no_current = chal_no_current::int +1   where user_cd= ? ";
                ps = tmgr.prepareStatement(sql);
                ps.setLong(1, Long.parseLong(userCd));
                ps.executeUpdate();
                tmgr.commit();
            }
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
    }
    
    public boolean saveChallanData(List<AccusedDetailsDobj> accusedDetails,
            List<DocTableDobj> docList, List<OffencesDobj> offencelist,
            List<CompoundingFeeDobj> ListForCompoundigFee, SaveChallanDobj dobjSaveChallan,
            Status_dobj statusDobj, List<WitnessdetailDobj> witnessList) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        boolean flag = false;
        String sql = "";
        int i = 0;
        try {
            tmgr = new TransactionManager("SaveChallanDAO : saveChallanData");
            String applicationNo = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
            sql = "INSERT INTO  " + TableList.VA_CHALLAN + " ( appl_no, chal_no, pur_cd, regn_no, chal_date,"
                    + " chal_time,chal_place, is_doc_impound, is_vch_impdound, chal_officer, nc_c_ref_no,remarks, op_dt,"
                    + " state_cd, off_cd,comming_from, going_to, settled_spot) VALUES (?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(++i, applicationNo);
            ps.setString(++i, dobjSaveChallan.getChallanNo());
            ps.setInt(++i, TableConstants.VM_MAST_ENFORCEMENT);
            ps.setString(++i, dobjSaveChallan.getVehicleNo());
            ps.setTimestamp(++i, ChallanUtil.getTimeStamp(dobjSaveChallan.getChallanDt().toString()));
            ps.setString(++i, dobjSaveChallan.getChallanTime());
            ps.setString(++i, dobjSaveChallan.getChallanPlace());
            ps.setString(++i, dobjSaveChallan.getDocImpnd());
            ps.setString(++i, dobjSaveChallan.getVehImpnd());
            ps.setString(++i, dobjSaveChallan.getOfficerNo());
            ps.setString(++i, dobjSaveChallan.getNCCRNo());
            ps.setString(++i, dobjSaveChallan.getAccusedRemarks());
            ps.setTimestamp(++i, ChallanUtil.getTimeStamp(dobjSaveChallan.getChallanDt().toString()));
            ps.setString(++i, Util.getUserStateCode());
            ps.setInt(++i, Util.getSelectedSeat().getOff_cd());
            ps.setString(++i, dobjSaveChallan.getCommingFrom());
            ps.setString(++i, dobjSaveChallan.getGoingTo());
            ps.setBoolean(++i, dobjSaveChallan.isCheckBoxsettledAtSpot());
            ps.execute();
            i = 0;
            sql = "INSERT INTO  " + TableList.VT_CHALLAN_OWNER + " (appl_no, regn_no, chasi_no, vh_class,"
                    + " vch_off_cd, vch_state_cd,owner_name, seat_cap, stand_cap, sleeper_cap, "
                    + "ld_wt, color, fuel,state_cd,off_cd) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
            ps = tmgr.prepareStatement(sql);
            ps.setString(++i, applicationNo);
            ps.setString(++i, dobjSaveChallan.getVehicleNo());
            ps.setString(++i, dobjSaveChallan.getChasiNo());
            ps.setInt(++i, Integer.parseInt(dobjSaveChallan.getVhClass()));
            ps.setInt(++i, Integer.parseInt(dobjSaveChallan.getDistrict()));
            ps.setString(++i, dobjSaveChallan.getState());
            ps.setString(++i, dobjSaveChallan.getOwnerName());
            ps.setInt(++i, Integer.parseInt(dobjSaveChallan.getSeatCap()));
            ps.setInt(++i, Integer.parseInt(dobjSaveChallan.getStandCap()));
            ps.setInt(++i, Integer.parseInt(dobjSaveChallan.getSleeperCap()));
            ps.setInt(++i, Integer.parseInt(dobjSaveChallan.getLadenWt()));
            ps.setString(++i, dobjSaveChallan.getColor());
            ps.setInt(++i, Integer.parseInt(dobjSaveChallan.getFuel()));
            ps.setString(++i, Util.getUserStateCode());
            ps.setInt(++i, Util.getSelectedSeat().getOff_cd());
            ps.execute();
            
            sql = " INSERT INTO " + TableList.VT_CHALLAN_ACCUSED + " ( appl_no, accused_catg, accused_name, accused_add, "
                    + "dl_no, state_cd, off_cd,police_station_name,city,accused_flag,pin_code) VALUES (?, ?, ?, ?, ?, ?, ?,?,?,?,?)";
            AccusedDetailsDobj dobjAccused = null;
            Iterator itr1 = accusedDetails.iterator();
            while (itr1.hasNext()) {
                i = 0;
                ps = tmgr.prepareStatement(sql);
                dobjAccused = (AccusedDetailsDobj) itr1.next();
                ps.setString(++i, applicationNo);
                ps.setString(++i, dobjAccused.getAccCatergory());
                ps.setString(++i, dobjAccused.getAccName());
                ps.setString(++i, dobjAccused.getAccAddress());
                ps.setString(++i, dobjAccused.getAccDLBladgeNo());
                ps.setString(++i, Util.getUserStateCode());
                ps.setInt(++i, Util.getSelectedSeat().getOff_cd());
                ps.setString(++i, dobjAccused.getAccPoliceStation());
                ps.setString(++i, dobjAccused.getAccCity());
                if ("-1".equalsIgnoreCase(dobjAccused.getAccFlag())) {
                    ps.setString(++i, "");
                } else {
                    ps.setString(++i, dobjAccused.getAccFlag());
                }
                ps.setInt(++i, dobjAccused.getAccPincode());
                ps.execute();
            }
            DocTableDobj docdobj = null;
            sql = "INSERT INTO  " + TableList.VT_DOCS_IMPOUND + " (appl_no, doc_cd,"
                    + " doc_no, iss_auth, validity, accused_catg, doc_impnd_dt,"
                    + "  state_cd, off_cd,other_doc_name)VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
            Iterator itr2 = docList.iterator();
            while (itr2.hasNext()) {
                i = 0;
                ps = tmgr.prepareStatement(sql);
                docdobj = (DocTableDobj) itr2.next();
                ps.setString(++i, applicationNo);
                ps.setInt(++i, Integer.parseInt(docdobj.getDocCode()));
                ps.setString(++i, docdobj.getDocNo());
                ps.setString(++i, docdobj.getIssueAuth());
                ps.setTimestamp(++i, ChallanUtil.getTimeStamp(docdobj.getValidity().toString()));
                ps.setString(++i, docdobj.getAccCategInDocImp());
                ps.setTimestamp(++i, ChallanUtil.getTimeStamp(dobjSaveChallan.getChallanDt().toString()));
                ps.setString(++i, Util.getUserStateCode());
                ps.setInt(++i, Util.getSelectedSeat().getOff_cd());
                ps.setString(++i, docdobj.getDocName());
                ps.execute();
                
            }
            i = 0;
            if (!("".equals(dobjSaveChallan.getSezNo()) || dobjSaveChallan.getSezNo() == null)) {
                sql = "INSERT INTO  " + TableList.VT_VEHICLE_IMPOUND + " (appl_no, "
                        + "regn_no, impound_place, contact_off, impound_dt, seizure_no,"
                        + "  remarks, state_cd, off_cd,police_station_name,police_station_district_name)VALUES (?, ?, ?, ?, ?, ?,  ?, ?, ?,?,?)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(++i, applicationNo);
                ps.setString(++i, dobjSaveChallan.getVehicleNo());
                ps.setString(++i, dobjSaveChallan.getVehImpndPlace());
                ps.setString(++i, dobjSaveChallan.getContactOfficer());
                ps.setTimestamp(++i, ChallanUtil.getTimeStamp(dobjSaveChallan.getVehImpdDate().toString()));
                ps.setString(++i, dobjSaveChallan.getSezNo());
                ps.setString(++i, dobjSaveChallan.getImpoundRemarks());
                ps.setString(++i, Util.getUserStateCode());
                ps.setInt(++i, Util.getSelectedSeat().getOff_cd());
                ps.setString(++i, dobjSaveChallan.getSezPoliceStation());
                ps.setString(++i, dobjSaveChallan.getSezDistrict());
                ps.execute();
                
            }
            i = 0;
            OffencesDobj dobjoffence = null;
            String penalty = "";
            sql = "INSERT INTO " + TableList.VT_VCH_OFFENCES + " ( appl_no, offence_cd,section_cd, accused_catg, offence_amt,"
                    + " remarks, state_cd, off_cd)VALUES (?, ?, ?, ?, ?, ?, ?,?)";
            Iterator itr3 = offencelist.iterator();
            while (itr3.hasNext()) {
                i = 0;
                ps = tmgr.prepareStatement(sql);
                
                dobjoffence = (OffencesDobj) itr3.next();
                if (dobjoffence.getAccuseInOffDetails().equals("O")) {
                    penalty = dobjoffence.getPenalty();
                }
                if (dobjoffence.getAccuseInOffDetails().equals("C")) {
                    penalty = dobjoffence.getPenalty();
                }
                if (dobjoffence.getAccuseInOffDetails().equals("D")) {
                    penalty = dobjoffence.getPenalty();
                }
                ps.setString(++i, applicationNo);
                ps.setInt(++i, Integer.parseInt(dobjoffence.getOffenceCode()));
                ps.setInt(++i, Integer.parseInt(dobjoffence.getOffenceCode()));
                ps.setString(++i, dobjoffence.getAccuseInOffDetails());
                ps.setInt(++i, Integer.parseInt(penalty));
                ps.setString(++i, dobjSaveChallan.getAccusedRemarks());
                ps.setString(++i, Util.getUserStateCode());
                ps.setInt(++i, Util.getSelectedSeat().getOff_cd());
                ps.execute();
                
            }
            
            i = 0;
            sql = "INSERT INTO  " + TableList.VT_CHALLAN_AMT + " (appl_no, pur_cd, "
                    + " cmpd_amt,adv_amt,adv_rcpt_no,adv_rcpt_dt ,state_cd,off_cd)"
                    + "VALUES (?, ?, ?, ?, ?,?,?,?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(++i, applicationNo);
            ps.setInt(++i, TableConstants.VM_MAST_ENFORCEMENT);
            ps.setInt(++i, Integer.parseInt(dobjSaveChallan.getCompFee()));
            if (!("".equals(dobjSaveChallan.getOnRoadPayRcieptNo()) || dobjSaveChallan.getOnRoadPayRcieptNo() == null)) {
                ps.setInt(++i, dobjSaveChallan.getAdCompFee());
                ps.setString(++i, dobjSaveChallan.getOnRoadPayRcieptNo());
                ps.setTimestamp(++i, ChallanUtil.getTimeStamp(dobjSaveChallan.getOnRoadPayRcieptDate().toString()));
            } else {
                ps.setInt(++i, 0);
                ps.setString(++i, null);
                ps.setTimestamp(++i, null);
            }
            ps.setString(++i, Util.getUserStateCode());
            ps.setInt(++i, Util.getSelectedSeat().getOff_cd());
            ps.execute();
            
            i = 0;
            if ("-1".equalsIgnoreCase(dobjSaveChallan.getCourtName()) || dobjSaveChallan.getCourtName() == null) {
            } else {
                
                sql = "INSERT INTO " + TableList.VT_CHALLAN_REFER_TO_COURT + " "
                        + "(appl_no, court_cd, hearing_date, op_dt, state_cd, off_cd)"
                        + "VALUES (?, ?, ?, ?, ?, ?)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(++i, applicationNo);
                ps.setInt(++i, Integer.parseInt(dobjSaveChallan.getCourtName()));
                ps.setTimestamp(++i, ChallanUtil.getTimeStamp(dobjSaveChallan.getHeaaringDt().toString()));
                ps.setTimestamp(++i, ChallanUtil.getTimeStamp(dobjSaveChallan.getChallanDt().toString()));
                ps.setString(++i, Util.getUserStateCode());
                ps.setInt(++i, Util.getSelectedSeat().getOff_cd());
                ps.execute();
            }
            if (witnessList != null) {
                for (WitnessdetailDobj witnessDobj : witnessList) {
                    i = 0;
                    if (!CommonUtils.isNullOrBlank(witnessDobj.getWitnessName())) {
                        sql = "INSERT INTO echallan.vt_witness_details(\n"
                                + " appl_no, witness_name, witness_contact_no, witness_address, state_cd, \n"
                                + " off_cd,police_station_name)\n"
                                + " VALUES (?, ?, ?, ?, ?, \n"
                                + " ?,?)";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(++i, applicationNo);
                        ps.setString(++i, witnessDobj.getWitnessName());
                        ps.setLong(++i, witnessDobj.getWitnessContactNo());
                        ps.setString(++i, witnessDobj.getWitnessAddress());
                        ps.setString(++i, Util.getUserStateCode());
                        ps.setInt(++i, Util.getSelectedSeat().getOff_cd());
                        ps.setString(++i, witnessDobj.getPoliceStation());
                        ps.execute();
                    }
                }
            }
            if (dobjSaveChallan.isOverloadVehicle() || dobjSaveChallan.isOverAccessPassanger()) {
                i = 0;
                sql = "INSERT INTO " + TableList.VT_CHALLAN_ADDL_INFO + "(\n"
                        + "            appl_no, overload, type_of_goods, projection, excess_passenger, \n"
                        + "            overhang, excess_animal, animal_type, state_cd, off_cd)\n"
                        + "    VALUES (?, ?, ?, ?, ?, \n"
                        + "            ?, ?, ?, ?, ?)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(++i, applicationNo);
                ps.setInt(++i, dobjSaveChallan.getOverLoadWeight());
                ps.setInt(++i, dobjSaveChallan.getTypesOfGooods());
                ps.setInt(++i, dobjSaveChallan.getProjection());
                ps.setInt(++i, dobjSaveChallan.getAccessPassangerWeight());
                ps.setInt(++i, dobjSaveChallan.getOverHang());
                ps.setInt(++i, dobjSaveChallan.getAccessAnimal());
                ps.setString(++i, dobjSaveChallan.getAnimalType());
                ps.setString(++i, Util.getUserStateCode());
                ps.setInt(++i, Util.getSelectedSeat().getOff_cd());
                ps.execute();
            }
            if (!applicationNo.equals("") && !(applicationNo == null)) {
                statusDobj.setAppl_no(applicationNo);
                ServerUtil.fileFlowForNewApplication(tmgr, statusDobj);
                statusDobj.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
                statusDobj.setStatus(TableConstants.STATUS_COMPLETE);
                statusDobj.setAppl_no(applicationNo);
                statusDobj.setOffice_remark("");
                statusDobj.setPublic_remark("");
                ServerUtil.webServiceForNextStage(statusDobj, TableConstants.FORWARD, null, applicationNo,
                        statusDobj.getAction_cd(), statusDobj.getPur_cd(), null, tmgr);
                ServerUtil.fileFlow(tmgr, statusDobj);
            }
            tmgr.commit();
            flag = true;
            dobjSaveChallan.setApplicationNO(applicationNo);
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return flag;
    }
    
    public List<SaveChallanDobj> getVehDetails(String regnNo, String name) throws VahanException {
        TransactionManager tmgr = null;
        SaveChallanDobj dobj = null;
        String whereCondition = "";
        List<SaveChallanDobj> list = new ArrayList<>();
        if ("regn_no".equals(name)) {
            whereCondition = "regn_no=?";
        }
        if ("chessis_no".equals(name)) {
            whereCondition = "chasi_no=?";
        }
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManager("SaveChallanDAO");
            String sql = " select owner.regn_no,owner.off_cd,owner.state_cd,owner.owner_name,owner.c_add1, owner.c_add2,"
                    + " owner.c_add3,owner.c_district,owner.c_district_name,owner.c_pincode,owner.vh_class, owner.chasi_no, owner.eng_no,"
                    + " owner.body_type,  owner.seat_cap, owner.stand_cap,  owner.sleeper_cap, owner.unld_wt, owner.ld_wt, "
                    + " owner.fuel, owner.color, vh_class.class_type "
                    + " from " + TableList.VIEW_VV_OWNER + " owner"
                    + " left outer  Join " + TableList.VM_VH_CLASS + " vh_class on vh_class.vh_class=owner.vh_class "
                    + " where " + whereCondition + "  ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo.toUpperCase());
            RowSet resultSet = tmgr.fetchDetachedRowSet();
            while (resultSet.next()) {
                dobj = new SaveChallanDobj();
                dobj.setVehno2(resultSet.getString("regn_no"));
                dobj.setChasiNo(resultSet.getString("chasi_no"));
                dobj.setVhClass(resultSet.getString("vh_class"));
                dobj.setVehicleType(resultSet.getString("class_type"));
                dobj.setOwnerName(resultSet.getString("owner_name"));
                dobj.setSeatCap(resultSet.getString("seat_cap"));
                dobj.setLadenWt(resultSet.getString("ld_wt"));
                dobj.setStandCap(resultSet.getString("stand_cap"));
                dobj.setColor(resultSet.getString("color"));
                dobj.setSleeperCap(resultSet.getString("sleeper_cap"));
                dobj.setFuel(resultSet.getString("fuel"));
                dobj.setState(resultSet.getString("state_cd"));
                dobj.setDistrict(resultSet.getString("off_cd"));
                dobj.setHiddenaddressField(resultSet.getString("c_add1") + "," + resultSet.getString("c_add2") + "," + resultSet.getString("c_add3") + "," + resultSet.getString("c_district_name") + "," + resultSet.getString("c_pincode"));
                list.add(dobj);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
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
        return list;
    }
    
    public List getrtoCode(String stete) throws Exception {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        List rto = new ArrayList();
        SelectItem item = new SelectItem("-1", "Select Rto");
        rto.add(item);
        try {
            tmgr = new TransactionManager("getrtoCode");
            String sql = "select off_cd,off_name from " + TableList.TM_OFFICE + "   where state_cd='" + stete + "'";
            ps = tmgr.prepareStatement(sql);
            RowSet resulSet = tmgr.fetchDetachedRowSet();
            while (resulSet.next()) {
                item = new SelectItem(resulSet.getString("off_cd"), resulSet.getString("off_name"));
                rto.add(item);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
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
        return rto;
    }
    
    public OffencesDobj getOffenceDetails(int offenceCode, String accuseInOffDetail, String vhClass, String compoundingFee, String regn_no) throws VahanException, Exception {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        OffencesDobj offdobj = null;
        String compFee = "";
        //List<OffencesDobj> offenceDetails = new ArrayList<>();
        try {
            String sqlOffenceDetails = "select a.*,\n"
                    + " CASE WHEN a.challan_count=0 THEN a.penalty1 \n"
                    + "      WHEN challan_count=1 THEN a.penalty2 \n"
                    + "      WHEN challan_count>=2 THEN a.penalty3  \n"
                    + "            ELSE '0' end   as offence_penalty\n"
                    + "             from \n"
                    + " (SELECT vm_offence_penalty.offence_cd,vm_offence_penalty.vh_class,vm_offence_penalty.section_cd,vm_offence_penalty.penalty1,vm_offence_penalty.penalty2,vm_offence_penalty.penalty3,vm_offence_penalty.offence_applied_on,\n"
                    + " (select count (*) as challan_count  from  echallan.vt_challan where regn_no=?) as challan_count,\n"
                    + " offence.offence_desc as offenceDescr, offence.mva_clause,\n"
                    + " accused.descr\n"
                    + " FROM echallan.vm_offence_penalty vm_offence_penalty \n"
                    + " left Outer Join  echallan.vm_offences offence on offence.offence_cd=vm_offence_penalty.offence_cd and offence.state_cd in (vm_offence_penalty.state_cd,?)\n"
                    + " left Outer Join  echallan.vm_accused accused on ?=ANY(string_to_array(vm_offence_penalty.offence_applied_on,','))\n"
                    + " where accused.code=? and  vh_class=? and vm_offence_penalty.offence_cd=? and vm_offence_penalty.state_cd =?) a";
            tmgr = new TransactionManager("SaveChallanDAO: getoffencedetails");
            ps = tmgr.prepareStatement(sqlOffenceDetails);
            
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            ps.setString(3, accuseInOffDetail);
            ps.setString(4, accuseInOffDetail);
            ps.setInt(5, Integer.parseInt(vhClass));
            ps.setInt(6, offenceCode);
            ps.setString(7, Util.getUserStateCode());
            RowSet resulSet = tmgr.fetchDetachedRowSet_No_release();
            if (resulSet.next()) {
                if (offenceCode == 24 || offenceCode == 41) {
                    compFee = compoundingFee;
                } else {
                    compFee = resulSet.getString("offence_penalty");
                }
                offdobj = new OffencesDobj(accuseInOffDetail, resulSet.getString("descr"), resulSet.getString("offence_cd"), resulSet.getString("offenceDescr"), compFee, resulSet.getString("mva_clause"));
            }
            // offenceDetails.add(offdobj);

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                tmgr.release();
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return offdobj;
        
    }
    
    public Map<String, Object> getAccused1() throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        Map<String, Object> accused = new LinkedHashMap<String, Object>();
        try {
            tmgr = new TransactionManager("getAccused1");
            String sql = "select code,descr from " + TableList.VM_ACCUSED + "   WHERE code='O' OR code='D' order by descr";
            ps = tmgr.prepareStatement(sql);
            RowSet rowSet = tmgr.fetchDetachedRowSet();
            while (rowSet.next()) {
                accused.put(rowSet.getString("descr"), rowSet.getString("code"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
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
        return accused;
    }
    
    public List getChallanOfficer() throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        List challanOfficer = new ArrayList<>();
        SelectItem item = new SelectItem("-1", "Select Officer");
        challanOfficer.add(item);
        try {
            tmgr = new TransactionManager("getChallanOfficer");
            // String sqlAccused = "select user_cd,user_name from " + TableList.TM_USER_INFO + "  where state_cd=?  and off_cd=?";
            String sql = "select * from tm_user_info  tmuser\n"
                    + "inner join vm_enforcement_officer enf_off on enf_off.user_cd=tmuser.user_cd\n"
                    + "  where tmuser.state_cd=? order by user_name";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            RowSet rowSet = tmgr.fetchDetachedRowSet();
            while (rowSet.next()) {
                item = new SelectItem(rowSet.getString("user_cd"), rowSet.getString("user_name"));
                challanOfficer.add(item);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
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
        return challanOfficer;
    }
    
    public boolean saveChallanDataToHist(List<AccusedDetailsDobj> accused_details, List<OffencesDobj> offencelist, List<CompoundingFeeDobj> ListForCompoundigFee, SaveChallanDobj dobjSaveChallan) throws SQLException, VahanException, Exception {
        boolean flag_hist = true;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        tmgr = new TransactionManager("SaveChallanDAO : saveChallanData");
        String application_no = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
        
        try {
            
            String ins_challan_data = "INSERT INTO " + TableList.VH_CHALLAN + " ( appl_no,  chal_no, regn_no, chal_date,"
                    + " chal_time,chal_place, is_doc_impound, is_vch_impdound, chal_officer, nc_c_ref_no,remarks, op_dt,"
                    + " state_cd, off_cd,comming_from, going_to, settled_spot) VALUES (?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?, ?,?,?,?)";
            ps = tmgr.prepareStatement(ins_challan_data);
            ps.setString(1, application_no);
            ps.setString(2, dobjSaveChallan.getChallanNo());
            ps.setString(3, dobjSaveChallan.getVehicleNo());
            ps.setTimestamp(4, ChallanUtil.getTimeStamp(dobjSaveChallan.getChallanDt().toString()));
            ps.setString(5, dobjSaveChallan.getChallanTime());
            ps.setString(6, dobjSaveChallan.getChallanPlace());
            ps.setString(7, dobjSaveChallan.getDocImpnd());
            ps.setString(8, dobjSaveChallan.getVehImpnd());
            ps.setString(9, dobjSaveChallan.getOfficerNo());
            ps.setString(10, dobjSaveChallan.getNCCRNo());
            ps.setString(11, dobjSaveChallan.getAccusedRemarks());
            ps.setTimestamp(12, ChallanUtil.getTimeStamp(dobjSaveChallan.getChallanDt().toString()));
            ps.setString(13, Util.getUserStateCode());
            ps.setInt(14, Util.getSelectedSeat().getOff_cd());
            ps.setString(15, dobjSaveChallan.getCommingFrom());
            ps.setString(16, dobjSaveChallan.getGoingTo());
            ps.setBoolean(17, dobjSaveChallan.isCheckBoxsettledAtSpot());
            ps.execute();
            tmgr.commit();
            dobjSaveChallan.setApplicationNO(application_no);
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        
        return flag_hist;
    }
    
    public String getCurrentRecptNo() throws VahanException {
        TransactionManager tmgr = null;
        String CurruntRecieptNo = "";
        try {
            tmgr = new TransactionManager("GET current Receipt NO");
            CurruntRecieptNo = Receipt_Master_Impl.generateNewRcptNo(Util.getSelectedSeat().getOff_cd(), tmgr);
            tmgr.commit();
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
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
        return CurruntRecieptNo;
    }
    
    public List<AccusedDetailsDobj> getAccusedDetailsForApproveVarification(String applicationNo) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        List accusedList = new ArrayList();
        AccusedDetailsDobj accdtl = null;
        try {
            String sql = "SELECT  ch_accused.accused_catg, ch_accused.accused_name, ch_accused.accused_add, ch_accused.dl_no ,accused.descr ,"
                    + " ch_accused.police_station_name,ch_accused.city,ch_accused.accused_flag,ch_accused.pin_code"
                    + " FROM " + TableList.VT_CHALLAN_ACCUSED + "  ch_accused"
                    + " INNER JOIN  " + TableList.VM_ACCUSED + "  accused ON accused.code=ch_accused.accused_catg"
                    + " WHERE appl_no=?";
            tmgr = new TransactionManager("Fatch Accused For verify And Approve");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applicationNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                accdtl = new AccusedDetailsDobj(rs.getString("accused_catg"), rs.getString("descr"), rs.getString("accused_name"), rs.getString("accused_add"), rs.getString("dl_no"), rs.getString("police_station_name"), rs.getString("city"), rs.getString("accused_flag"), rs.getInt("pin_code"));
                
                accusedList.add(accdtl);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        
        return accusedList;
    }
    
    public SaveChallanDobj getChallanDetailsAndOwnerDetails(String applicationNo) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        SaveChallanDobj challanDobj = null;
        
        try {
            String sql = "SELECT challan.appl_no, challan.chal_no,challan.comming_from,challan.going_to, "
                    + " challan.chal_date, challan.chal_time, "
                    + " challan.chal_place, challan.chal_officer, challan.nc_c_ref_no, "
                    + " challan.remarks,challan.state_cd,challan.off_cd, challan.settled_spot,"
                    + " ownerr.regn_no, ownerr.chasi_no, ownerr.vh_class, ownerr.vch_off_cd, ownerr.vch_state_cd, "
                    + "  ownerr.owner_name, ownerr.seat_cap, ownerr.stand_cap, ownerr.sleeper_cap, ownerr.ld_wt, ownerr.color, ownerr.fuel,amt.adv_amt ,amt.adv_rcpt_no, amt.adv_rcpt_dt,"
                    + " ownerr.color, ownerr.fuel,amt.adv_amt, amt.adv_rcpt_no, amt.adv_rcpt_dt,vh_class.class_type "
                    + "  FROM " + TableList.VA_CHALLAN + "  challan  "
                    + "  LEFT OUTER JOIN " + TableList.VT_CHALLAN_OWNER + " ownerr ON ownerr.appl_no=challan.appl_no"
                    + "  LEFT OUTER JOIN " + TableList.VT_CHALLAN_AMT + " amt ON ownerr.appl_no=amt.appl_no"
                    + " Inner Join " + TableList.VM_VH_CLASS + " vh_class on ownerr.vh_class=vh_class.vh_class "
                    + "  WHERE challan.appl_no=?";
            tmgr = new TransactionManager("Fatch Challan And Owner Details:: AND VEHICLE IMpounded Details");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applicationNo);
            
            RowSet rowSet = tmgr.fetchDetachedRowSet();
            challanDobj = new SaveChallanDobj();
            if (rowSet.next()) {
                challanDobj.setAdCompFee(rowSet.getInt("adv_amt"));
                challanDobj.setChallanNo(rowSet.getString("chal_no"));
                challanDobj.setChallanDt(rowSet.getDate("chal_date"));
                challanDobj.setChallanTime(rowSet.getString("chal_time"));
                challanDobj.setChallanPlace(rowSet.getString("chal_place"));
                challanDobj.setChalOff(rowSet.getString("chal_officer"));
                challanDobj.setNCCRNo(rowSet.getString("nc_c_ref_no"));
                challanDobj.setAccusedRemarks(rowSet.getString("remarks"));
                String regnNO = rowSet.getString("regn_no");
                challanDobj.setVehno2(regnNO);
                challanDobj.setChasiNo(rowSet.getString("chasi_no"));
                challanDobj.setVhClass(rowSet.getString("vh_class"));
                challanDobj.setVehicleType(rowSet.getString("class_type"));
                challanDobj.setState(rowSet.getString("vch_state_cd"));
                challanDobj.setDistrict(rowSet.getString("vch_off_cd"));
                challanDobj.setOwnerName(rowSet.getString("owner_name"));
                challanDobj.setSeatCap(rowSet.getString("seat_cap"));
                challanDobj.setStandCap(rowSet.getString("stand_cap"));
                challanDobj.setSleeperCap(rowSet.getString("sleeper_cap"));
                challanDobj.setLadenWt(rowSet.getString("ld_wt"));
                challanDobj.setColor(rowSet.getString("color"));
                challanDobj.setFuel(rowSet.getString("fuel"));
                challanDobj.setApplicationNO(rowSet.getString("appl_no"));
                challanDobj.setOfficeCode(rowSet.getString("off_cd"));
                challanDobj.setOfficeStateCode(rowSet.getString("state_cd"));
                challanDobj.setOnRoadPayRcieptNo(rowSet.getString("adv_rcpt_no"));
                challanDobj.setOnRoadPayRcieptDate(rowSet.getDate("adv_rcpt_dt"));
                challanDobj.setCommingFrom(rowSet.getString("comming_from"));
                challanDobj.setGoingTo(rowSet.getString("going_to"));
                challanDobj.setCheckBoxsettledAtSpot(rowSet.getBoolean("settled_spot"));
                
            } else {
                challanDobj = null;
            }
            
            sql = "SELECT  impound_place, contact_off, impound_dt, seizure_no,remarks,police_station_name,police_station_district_name \n"
                    + "  FROM " + TableList.VT_VEHICLE_IMPOUND + " \n"
                    + "  WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applicationNo);
            RowSet rowSetVehicleImpnd = tmgr.fetchDetachedRowSet();
            if (rowSetVehicleImpnd.next()) {
                challanDobj.setVehImpdDate(rowSetVehicleImpnd.getDate("impound_dt"));
                challanDobj.setVehImpndPlace(rowSetVehicleImpnd.getString("impound_place"));
                challanDobj.setSezNo(rowSetVehicleImpnd.getString("seizure_no"));
                challanDobj.setContactOfficer(rowSetVehicleImpnd.getString("contact_off"));
                challanDobj.setImpoundRemarks(rowSetVehicleImpnd.getString("remarks"));
                challanDobj.setSezPoliceStation(rowSetVehicleImpnd.getString("police_station_name"));
                challanDobj.setSezDistrict(rowSetVehicleImpnd.getString("police_station_district_name"));
            }
            sql = "SELECT REFER_TO_COURT.appl_no, REFER_TO_COURT.court_cd, REFER_TO_COURT.hearing_date,court.magistrate_cd "
                    + " FROM " + TableList.VT_CHALLAN_REFER_TO_COURT + " REFER_TO_COURT "
                    + " join " + TableList.VM_COURT + " court on court.court_cd=REFER_TO_COURT.court_cd and court.state_cd=REFER_TO_COURT.state_cd "
                    + "  where REFER_TO_COURT.appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applicationNo);
            RowSet rowSetReferTOCourt = tmgr.fetchDetachedRowSet();
            if (rowSetReferTOCourt.next()) {
                challanDobj.setHeaaringDt(rowSetReferTOCourt.getDate("hearing_date"));
                challanDobj.setCourtName(rowSetReferTOCourt.getString("court_cd"));
                challanDobj.setMagistrate(rowSetReferTOCourt.getString("magistrate_cd"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return challanDobj;
    }
    
    public List<OffencesDobj> getOffenceDetailsForApproveVarification(String applicationNO) throws VahanException {
        List offList = new ArrayList();
        TransactionManager tmgr = null;
        OffencesDobj offDobj = null;
        PreparedStatement ps = null;
        try {
            String sql = "SELECT offence.appl_no, offence.offence_cd,offence.section_cd, offence.accused_catg, offence.offence_amt, accused.descr as accuseDesc,vmOff.offence_desc,vmOff.mva_clause\n"
                    + " FROM " + TableList.VT_VCH_OFFENCES + "  offence \n"
                    + " LEFT OUTER JOIN " + TableList.VM_ACCUSED + "  accused ON accused.code=offence.accused_catg \n"
                    + " LEFT OUTER JOIN " + TableList.VM_OFFENCES + "  vmOff ON offence.offence_cd=vmOff.offence_cd and offence.state_cd in (vmOff.state_cd ,?) and vmOff.state_cd=offence.state_cd\n"
                    + " WHERE appl_no=?";
            tmgr = new TransactionManager("GET OFFENCES");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, applicationNO);
            RowSet rowSet = tmgr.fetchDetachedRowSet();
            while (rowSet.next()) {
                offDobj = new OffencesDobj(rowSet.getString("accused_catg"), rowSet.getString("accuseDesc"), rowSet.getString("offence_cd"), rowSet.getString("offence_desc"), rowSet.getString("offence_amt"), rowSet.getString("mva_clause"));
                offList.add(offDobj);
            }
            
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return offList;
    }
    
    public void movetoapprove(String roleCd, Status_dobj statusDobj, String change, List<AccusedDetailsDobj> accusedDetails, List<DocTableDobj> docList, List<OffencesDobj> offencelist, List<CompoundingFeeDobj> ListForCompoundigFee, SaveChallanDobj dobjSaveChallan, SaveChallanDobj previousSaveChallanDobj, List<WitnessdetailDobj> witnessDobj, String permanent_rcpt_no) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("movetoapprove");
            if (!change.isEmpty()) {
                onlySaveNotVerifyApprove(roleCd, statusDobj, change, accusedDetails, docList, offencelist, ListForCompoundigFee, dobjSaveChallan, previousSaveChallanDobj, witnessDobj, tmgr);
            }
            if (Integer.parseInt(roleCd) == TableConstants.VM_ROLE_ENFORCEMENT_APPROVE) {
                saveDataInApprovalCase(dobjSaveChallan, previousSaveChallanDobj, tmgr, permanent_rcpt_no);
            }
            statusDobj = ServerUtil.webServiceForNextStage(statusDobj, tmgr);
            if (Integer.parseInt(roleCd) == TableConstants.VM_ROLE_ENFORCEMENT_APPROVE) {
                if ((dobjSaveChallan.getAdCompFee() != 0 && dobjSaveChallan.getAdCompFee() == Integer.parseInt(dobjSaveChallan.getCompFee()))
                        && ((dobjSaveChallan.getSezNo() != null && !dobjSaveChallan.getSezNo().equals("")) || !docList.isEmpty())) {
                    statusDobj.setAction_cd(TableConstants.VM_ROLE_ENFORCEMENT_DISPOSE_APPROVE);
                    statusDobj.setFlow_slno(7);
                }
                if (((dobjSaveChallan.getAdCompFee() != 0 && dobjSaveChallan.getAdCompFee() == Integer.parseInt(dobjSaveChallan.getCompFee()))
                        && (dobjSaveChallan.getSezNo() == null || dobjSaveChallan.getSezNo().equals("")) && docList.isEmpty()) || dobjSaveChallan.isCheckBoxsettledAtSpot()) {
                    moveVtChallanTOVhChallan(previousSaveChallanDobj, tmgr);
                    statusDobj.setEntry_status(TableConstants.STATUS_APPROVED);
                    ServerUtil.updateApprovedStatus(tmgr, statusDobj);
                }
            }
            ServerUtil.fileFlow(tmgr, statusDobj);
            tmgr.commit();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
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
    
    private void saveDataInApprovalCase(SaveChallanDobj dobjSaveChallan, SaveChallanDobj previousSaveChallanDobj, TransactionManager tmgr, String permanentRCPTNO) throws VahanException {
        PreparedStatement ps = null;
        String sql = "";
        try {
            
            if (!("".equals(dobjSaveChallan.getAdCompFee()) || dobjSaveChallan.getAdCompFee() == 0) && CommonUtils.isNullOrBlank(permanentRCPTNO)) {
                sql = "INSERT INTO  " + TableList.VT_FEE + " (regn_no, "
                        + "payment_mode, fees, fine, rcpt_no, rcpt_dt, pur_cd, "
                        + "flag, collected_by, state_cd, off_cd)VALUES (?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ? )";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobjSaveChallan.getVehicleNo());
                ps.setString(2, "C");
                ps.setInt(3, dobjSaveChallan.getAdCompFee());
                ps.setInt(4, 0);
                ps.setString(5, dobjSaveChallan.getOnRoadPayRcieptNo().trim().toUpperCase());
                ps.setTimestamp(6, ChallanUtil.getTimeStamp(dobjSaveChallan.getOnRoadPayRcieptDate().toString()));
                ps.setInt(7, TableConstants.VM_MAST_ENFORCEMENT_FEE_TAX);
                ps.setString(8, "");
                ps.setString(9, dobjSaveChallan.getOfficerNo());
                ps.setString(10, Util.getUserStateCode());
                ps.setInt(11, Util.getSelectedSeat().getOff_cd());
                ps.execute();
                
                sql = "INSERT INTO " + TableList.VP_APPL_RCPT_MAPPING + " "
                        + "(state_cd, off_cd, appl_no, rcpt_no, owner_name, chasi_no, vh_class,excess_amt, cash_amt, remarks)"
                        + "  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, Util.getSelectedSeat().getOff_cd());
                ps.setString(3, previousSaveChallanDobj.getApplicationNO());
                ps.setString(4, dobjSaveChallan.getOnRoadPayRcieptNo().trim().toUpperCase());
                ps.setString(5, dobjSaveChallan.getOwnerName());
                ps.setString(6, dobjSaveChallan.getChasiNo());
                ps.setInt(7, Integer.parseInt(dobjSaveChallan.getVhClass()));
                ps.setInt(8, 0);
                ps.setInt(9, dobjSaveChallan.getAdCompFee());
                ps.setString(10, null);
                ps.execute();
                
            }
            sql = "INSERT INTO " + TableList.VT_CHALLAN + " ("
                    + " appl_no, chal_no, regn_no, chal_date, chal_time, chal_place,"
                    + " is_doc_impound, is_vch_impdound, chal_officer, nc_c_ref_no, remarks, "
                    + " op_dt, state_cd, off_cd,comming_from,going_to, settled_spot)"
                    + " SELECT appl_no,  chal_no, regn_no, chal_date, chal_time, "
                    + " chal_place, is_doc_impound, is_vch_impdound, chal_officer, nc_c_ref_no,"
                    + " remarks, op_dt, state_cd, off_cd,comming_from,going_to, settled_spot"
                    + " FROM " + TableList.VA_CHALLAN + " where appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, previousSaveChallanDobj.getApplicationNO());
            ps.executeUpdate();
            sql = " DELETE FROM " + TableList.VA_CHALLAN
                    + " where appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, previousSaveChallanDobj.getApplicationNO());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }
    
    public void onlySaveNotVerifyApprove(String roleCd, Status_dobj statusDobj, String change, List<AccusedDetailsDobj> accusedDetails, List<DocTableDobj> docList, List<OffencesDobj> offencelist, List<CompoundingFeeDobj> ListForCompoundigFee, SaveChallanDobj dobjSaveChallan, SaveChallanDobj previousSaveChallanDobj, List<WitnessdetailDobj> witnessDobj, TransactionManager tmgr) throws Exception {
        PreparedStatement ps = null;
        String sql = "";
        int i = 0;
        try {
            if (change != null) {
                sql = "UPDATE  " + TableList.VA_CHALLAN + " \n"
                        + "   SET   chal_no=?, pur_cd=?, regn_no=?, chal_date=?, \n"
                        + "       chal_time=?, chal_place=?, is_doc_impound=?, is_vch_impdound=?, \n"
                        + "       chal_officer=?, nc_c_ref_no=?, remarks=?, op_dt=current_timestamp, state_cd=?, \n"
                        + "       off_cd=?,comming_from=?, going_to=?, settled_spot=?\n"
                        + " WHERE appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(++i, dobjSaveChallan.getChallanNo());
                ps.setInt(++i, TableConstants.VM_MAST_ENFORCEMENT);
                ps.setString(++i, dobjSaveChallan.getVehicleNo());
                ps.setDate(++i, new java.sql.Date(previousSaveChallanDobj.getChallanDt().getTime()));
                ps.setString(++i, dobjSaveChallan.getChallanTime());
                ps.setString(++i, dobjSaveChallan.getChallanPlace());
                ps.setString(++i, dobjSaveChallan.getDocImpnd());
                ps.setString(++i, dobjSaveChallan.getVehImpnd());
                ps.setString(++i, dobjSaveChallan.getOfficerNo());
                ps.setString(++i, dobjSaveChallan.getNCCRNo());
                ps.setString(++i, dobjSaveChallan.getAccusedRemarks());
                // ps.setDate(12, new java.sql.Date(previousSaveChallanDobj.getChallanDt().getTime()));
                ps.setString(++i, previousSaveChallanDobj.getOfficeStateCode());
                ps.setInt(++i, Integer.parseInt(previousSaveChallanDobj.getOfficeCode()));
                ps.setString(++i, dobjSaveChallan.getCommingFrom());
                ps.setString(++i, dobjSaveChallan.getGoingTo());
                ps.setBoolean(++i, dobjSaveChallan.isCheckBoxsettledAtSpot());
                ps.setString(++i, previousSaveChallanDobj.getApplicationNO());
                ps.executeUpdate();
                i = 0;
                sql = "UPDATE " + TableList.VT_CHALLAN_OWNER + " \n"
                        + "   SET  regn_no=?, chasi_no=?, vh_class=?, vch_off_cd=?, vch_state_cd=?, \n"
                        + "       owner_name=?, seat_cap=?, stand_cap=?, sleeper_cap=?, ld_wt=?, \n"
                        + "       color=?, fuel=?, state_cd=?, off_cd=?\n"
                        + " WHERE appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(++i, dobjSaveChallan.getVehicleNo());
                ps.setString(++i, dobjSaveChallan.getChasiNo());
                ps.setInt(++i, Integer.parseInt(dobjSaveChallan.getVhClass()));
                ps.setInt(++i, Integer.parseInt(dobjSaveChallan.getDistrict()));
                ps.setString(++i, dobjSaveChallan.getState());
                ps.setString(++i, dobjSaveChallan.getOwnerName());
                ps.setInt(++i, Integer.parseInt(dobjSaveChallan.getSeatCap()));
                ps.setInt(++i, Integer.parseInt(dobjSaveChallan.getStandCap()));
                ps.setInt(++i, Integer.parseInt(dobjSaveChallan.getSleeperCap()));
                ps.setInt(++i, Integer.parseInt(dobjSaveChallan.getLadenWt()));
                ps.setString(++i, dobjSaveChallan.getColor());
                ps.setInt(++i, Integer.parseInt(dobjSaveChallan.getFuel()));
                ps.setString(++i, previousSaveChallanDobj.getOfficeStateCode());
                ps.setInt(++i, Integer.parseInt(previousSaveChallanDobj.getOfficeCode()));
                ps.setString(++i, previousSaveChallanDobj.getApplicationNO());
                ps.executeUpdate();
                // i=0;
                sql = "UPDATE " + TableList.VT_CHALLAN_ACCUSED + " \n"
                        + "   SET accused_catg=?, accused_name=?, accused_add=?, dl_no=?, \n"
                        + "       state_cd=?, off_cd=?,police_station_name=?,city=?,accused_flag=?,pin_code=?\n"
                        + " WHERE  appl_no=? AND  accused_catg=?";
                AccusedDetailsDobj dobjAccused = null;
                Iterator itr1 = accusedDetails.iterator();
                while (itr1.hasNext()) {
                    i = 0;
                    dobjAccused = (AccusedDetailsDobj) itr1.next();
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(++i, dobjAccused.getAccCatergory());
                    ps.setString(++i, dobjAccused.getAccName());
                    ps.setString(++i, dobjAccused.getAccAddress());
                    ps.setString(++i, dobjAccused.getAccDLBladgeNo());
                    ps.setString(++i, dobjSaveChallan.getState());
                    ps.setInt(++i, Integer.parseInt(dobjSaveChallan.getDistrict()));
                    ps.setString(++i, dobjAccused.getAccPoliceStation());
                    ps.setString(++i, dobjAccused.getAccCity());
                    ps.setString(++i, dobjAccused.getAccFlag());
                    ps.setInt(++i, dobjAccused.getAccPincode());
                    ps.setString(++i, previousSaveChallanDobj.getApplicationNO());
                    ps.setString(++i, dobjAccused.getAccCatergory());
                    ps.executeUpdate();
                }
                //i=0;
                DocTableDobj docdobj = null;
                sql = "UPDATE " + TableList.VT_DOCS_IMPOUND + " \n"
                        + "   SET  doc_cd=?, doc_no=?, iss_auth=?, validity=?, accused_catg=?, \n"
                        + "       doc_impnd_dt=?, state_cd=?, \n"
                        + "       off_cd=?,other_doc_name=?\n"
                        + " WHERE appl_no=? AND doc_no=? ";
                Iterator itr2 = docList.iterator();
                while (itr2.hasNext()) {
                    i = 0;
                    ps = tmgr.prepareStatement(sql);
                    docdobj = (DocTableDobj) itr2.next();
                    ps.setInt(++i, Integer.parseInt(docdobj.getDocCode()));
                    ps.setString(++i, docdobj.getDocNo());
                    ps.setString(++i, docdobj.getIssueAuth());
                    ps.setTimestamp(++i, getDDMMMYYYYToTimesTamp(docdobj.getShowdate().toString()));
                    ps.setString(++i, docdobj.getAccCategInDocImp());
                    ps.setTimestamp(++i, ChallanUtil.getTimeStamp(dobjSaveChallan.getChallanDt().toString()));
                    ps.setString(++i, previousSaveChallanDobj.getOfficeStateCode());
                    ps.setInt(++i, Integer.parseInt(previousSaveChallanDobj.getOfficeCode()));
                    ps.setString(++i, docdobj.getDocName());
                    ps.setString(++i, previousSaveChallanDobj.getApplicationNO());
                    ps.setString(++i, docdobj.getDocNo());
                    ps.executeUpdate();
                }
                i = 0;
                if (!("".equals(dobjSaveChallan.getSezNo()) || dobjSaveChallan.getSezNo() == null)) {
                    sql = "UPDATE " + TableList.VT_VEHICLE_IMPOUND + " \n"
                            + "   SET  regn_no=?, impound_place=?, contact_off=?, impound_dt=?, \n"
                            + "       seizure_no=?, remarks=?, state_cd=?, \n"
                            + "       off_cd=?,police_station_name=?,police_station_district_name=?\n"
                            + " WHERE appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(++i, dobjSaveChallan.getVehicleNo());
                    ps.setString(++i, dobjSaveChallan.getVehImpndPlace());
                    ps.setString(++i, dobjSaveChallan.getContactOfficer());
                    ps.setTimestamp(++i, ChallanUtil.getTimeStamp(dobjSaveChallan.getVehImpdDate().toString()));
                    ps.setString(++i, dobjSaveChallan.getSezNo());
                    ps.setString(++i, dobjSaveChallan.getImpoundRemarks());
                    ps.setString(++i, previousSaveChallanDobj.getOfficeStateCode());
                    ps.setInt(++i, Integer.parseInt(previousSaveChallanDobj.getOfficeCode()));
                    ps.setString(++i, dobjSaveChallan.getSezPoliceStation());
                    ps.setString(++i, dobjSaveChallan.getSezDistrict());
                    ps.setString(++i, previousSaveChallanDobj.getApplicationNO());
                    ps.executeUpdate();
                }
                //i=0;
                OffencesDobj dobjoffence = null;
                String penalty = "";
                sql = "UPDATE  " + TableList.VT_VCH_OFFENCES + " \n"
                        + "   SET  offence_cd=?,section_cd=?, accused_catg=?, offence_amt=?, remarks=?, \n"
                        + "       state_cd=?, off_cd=?\n"
                        + " WHERE appl_no=? AND offence_cd=? AND accused_catg=? ";
                Iterator itr3 = offencelist.iterator();
                while (itr3.hasNext()) {
                    i = 0;
                    ps = tmgr.prepareStatement(sql);
                    dobjoffence = (OffencesDobj) itr3.next();
                    if (dobjoffence.getAccuseInOffDetails().equals("O")) {
                        penalty = dobjoffence.getPenalty();
                    }
                    if (dobjoffence.getAccuseInOffDetails().equals("C")) {
                        penalty = dobjoffence.getPenalty();
                    }
                    if (dobjoffence.getAccuseInOffDetails().equals("D")) {
                        penalty = dobjoffence.getPenalty();
                    }
                    ps.setInt(++i, Integer.parseInt(dobjoffence.getOffenceCode()));
                    ps.setInt(++i, Integer.parseInt(dobjoffence.getOffenceCode()));
                    ps.setString(++i, dobjoffence.getAccuseInOffDetails());
                    ps.setInt(++i, Integer.parseInt(penalty));
                    ps.setString(++i, dobjSaveChallan.getAccusedRemarks());
                    ps.setString(++i, previousSaveChallanDobj.getOfficeStateCode());
                    ps.setInt(++i, Integer.parseInt(previousSaveChallanDobj.getOfficeCode()));
                    ps.setString(++i, previousSaveChallanDobj.getApplicationNO());
                    ps.setInt(++i, Integer.parseInt(dobjoffence.getOffenceCode()));
                    ps.setString(++i, dobjoffence.getAccuseInOffDetails());
                    ps.executeUpdate();
                }
                i = 0;
                String advCompFee = "";
                sql = " UPDATE  " + TableList.VT_CHALLAN_AMT + " \n"
                        + "   SET  pur_cd=?, cmpd_amt=?, adv_amt=?,   \n"
                        + "       adv_rcpt_no=?, adv_rcpt_dt=?,  \n"
                        + "       state_cd=?, off_cd=?\n"
                        + " WHERE appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setInt(++i, TableConstants.VM_MAST_ENFORCEMENT);
                ps.setInt(++i, Integer.parseInt(dobjSaveChallan.getCompFee()));
                if (!("".equals(dobjSaveChallan.getOnRoadPayRcieptNo()) || dobjSaveChallan.getOnRoadPayRcieptNo() == null)) {
                    ps.setInt(++i, dobjSaveChallan.getAdCompFee());
                    ps.setString(++i, dobjSaveChallan.getOnRoadPayRcieptNo());
                    ps.setTimestamp(++i, ChallanUtil.getTimeStamp(dobjSaveChallan.getOnRoadPayRcieptDate().toString()));
                } else {
                    ps.setInt(++i, 0);
                    ps.setString(++i, "");
                    ps.setTimestamp(++i, null);
                }
                ps.setString(++i, previousSaveChallanDobj.getOfficeStateCode());
                ps.setInt(++i, Integer.parseInt(previousSaveChallanDobj.getOfficeCode()));
                ps.setString(++i, previousSaveChallanDobj.getApplicationNO());
                ps.execute();
                i = 0;
                if ("-1".equalsIgnoreCase(dobjSaveChallan.getCourtName()) || dobjSaveChallan.getCourtName() == null) {
                } else {
                    sql = " UPDATE  " + TableList.VT_CHALLAN_REFER_TO_COURT + " "
                            + " SET  court_cd=?, hearing_date=?,   "
                            + "   op_dt=?, state_cd=?, off_cd=?"
                            + "WHERE appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setInt(++i, Integer.parseInt(dobjSaveChallan.getCourtName()));
                    ps.setTimestamp(++i, ChallanUtil.getTimeStamp(dobjSaveChallan.getHeaaringDt().toString()));
                    ps.setTimestamp(++i, ChallanUtil.getTimeStamp(dobjSaveChallan.getChallanDt().toString()));
                    ps.setString(++i, Util.getUserStateCode());
                    ps.setInt(++i, Util.getSelectedSeat().getOff_cd());
                    ps.setString(++i, previousSaveChallanDobj.getApplicationNO());
                    ps.executeUpdate();
                }
                //i=0;
                if (witnessDobj != null) {
                    
                    sql = "UPDATE " + TableList.VT_WITNESS_DETAILS
                            + "   SET  witness_name=?, witness_contact_no=?, witness_address=?,police_station_name=? \n"
                            + " WHERE appl_no=? and witness_contact_no=?";
                    ps = tmgr.prepareStatement(sql);
                    for (WitnessdetailDobj witnessdetailDobj : witnessDobj) {
                        i = 0;
                        ps.setString(++i, witnessdetailDobj.getWitnessName());
                        ps.setLong(++i, witnessdetailDobj.getWitnessContactNo());
                        ps.setString(++i, witnessdetailDobj.getWitnessAddress());
                        ps.setString(++i, witnessdetailDobj.getPoliceStation());
                        ps.setString(++i, previousSaveChallanDobj.getApplicationNO());
                        ps.setLong(++i, witnessdetailDobj.getWitnessContactNo());
                        ps.executeUpdate();
                    }
                }
                insertIntoVhaChangedData(tmgr, previousSaveChallanDobj.getApplicationNO(), change);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        
    }
    
    public static Timestamp getDDMMMYYYYToTimesTamp(String strDt) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyy");
        Timestamp timeStampDate;
        Date date = new Date();
        try {
            date = (Date) sdf1.parse(strDt);
        } catch (ParseException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        timeStampDate = new Timestamp(date.getTime());
        return timeStampDate;
    }
    
    public void reback(String roleCd, Status_dobj statusDobj, String change, List<AccusedDetailsDobj> accusedDetails, List<DocTableDobj> docList, List<OffencesDobj> offencelist, List<CompoundingFeeDobj> ListForCompoundigFee, SaveChallanDobj dobjSaveChallan, SaveChallanDobj previousSaveChallanDobj, List<WitnessdetailDobj> witnessDobj) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("movetoapprove");
            if (!change.isEmpty()) {
                onlySaveNotVerifyApprove(roleCd, statusDobj, change, accusedDetails, docList, offencelist, ListForCompoundigFee, dobjSaveChallan, previousSaveChallanDobj, witnessDobj, tmgr);
            }
            statusDobj = ServerUtil.webServiceForNextStage(statusDobj, tmgr);
            ServerUtil.fileFlow(tmgr, statusDobj);
            tmgr.commit();
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
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
    
    public List<DocTableDobj> getdocImpoundedForVarification(String applicationNO) throws VahanException {
        List documentList = new ArrayList();
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        DocTableDobj docImpoundDobj = null;
        try {
            String sql = "SELECT  docImp.doc_cd, docImp.doc_no, docImp.iss_auth,docImp.other_doc_name, to_char(docImp.validity,'dd-MON-yyyy') as validity,\n"
                    + " docImp.accused_catg,accused.descr AS accusedDescr,doc.descr AS documentDescr\n"
                    + "  FROM " + TableList.VT_DOCS_IMPOUND + "  docImp \n"
                    + " left outer join " + TableList.VM_ACCUSED + "  accused ON accused.code=docImp.accused_catg\n"
                    + " left outer join  " + TableList.VM_DOCUMENTS + "  doc ON doc.code=docImp.doc_cd \n"
                    + "  WHERE appl_no=?";
            tmgr = new TransactionManager("GET OFFENCES");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applicationNO);
            RowSet rowSet = tmgr.fetchDetachedRowSet();
            while (rowSet.next()) {
                docImpoundDobj = new DocTableDobj(rowSet.getString("doc_cd"), rowSet.getString("accusedDescr"), rowSet.getString("documentDescr"), rowSet.getString("validity"), rowSet.getString("iss_auth"), rowSet.getString("accused_catg"), rowSet.getString("doc_no"), rowSet.getString("validity"), rowSet.getString("other_doc_name"));
                documentList.add(docImpoundDobj);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
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
        return documentList;
    }
    
    public List<WitnessdetailDobj> fetchWitnessDetails(String applNo) throws VahanException {
        WitnessdetailDobj dobj = null;
        List WitnessdetailDobjList = new ArrayList();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = "";
        RowSet rowSet = null;
        sql = "SELECT appl_no, witness_name, witness_contact_no, witness_address, state_cd, \n"
                + " off_cd,police_station_name\n"
                + " FROM " + TableList.VT_WITNESS_DETAILS + " where appl_no=?";
        try {
            tmgr = new TransactionManager("Witness Details");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            rowSet = tmgr.fetchDetachedRowSet();
            while (rowSet.next()) {
                dobj = new WitnessdetailDobj();
                dobj.setApplNo(rowSet.getString("appl_no"));
                dobj.setWitnessName(rowSet.getString("witness_name"));
                dobj.setWitnessContactNo(rowSet.getLong("witness_contact_no"));
                dobj.setWitnessAddress(rowSet.getString("witness_address"));
                dobj.setStateCd(rowSet.getString("state_cd"));
                dobj.setOffCd(rowSet.getInt("off_cd"));
                dobj.setPoliceStation(rowSet.getString("police_station_name"));
                WitnessdetailDobjList.add(dobj);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
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
        return WitnessdetailDobjList;
    }
    
    public List<AccusedDetailsDobj> getAccusedDetailsForReceipt(String appl_no) throws VahanException {
        List<AccusedDetailsDobj> list = new ArrayList<AccusedDetailsDobj>();
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        AccusedDetailsDobj accusedDobj = null;
        try {
            tmgr = new TransactionManager("accused Details");
            String sql = "Select vt_accused.accused_catg, vt_accused.accused_name, "
                    + "vt_accused.accused_add, vt_accused.dl_no,vm_accused.descr as accusedDescription\n"
                    + "from  " + TableList.VT_CHALLAN_ACCUSED + " vt_accused\n"
                    + "Inner Join  " + TableList.VM_ACCUSED + " vm_accused On vm_accused.code=vt_accused.accused_catg\n"
                    + " where appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                accusedDobj = new AccusedDetailsDobj();
                accusedDobj.setAccCatergory(rs.getString("accused_catg"));
                accusedDobj.setAccDesc(rs.getString("accusedDescription"));
                accusedDobj.setAccName(rs.getString("accused_name"));
                accusedDobj.setAccAddress(rs.getString("accused_add"));
                accusedDobj.setAccDLBladgeNo(rs.getString("dl_no"));
                list.add(accusedDobj);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
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
        return list;
    }
    
    public SaveChallanDobj getChallanDetailForReceipt(String applicationNo, int Off_cd, String State_cd) throws VahanException {
        SaveChallanDobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            String sql = "SELECT vaChallan.appl_no, vaChallan.chal_no, vaChallan.pur_cd, "
                    + "vaChallan.regn_no, vaChallan.chal_date, vaChallan.chal_time, vaChallan.chal_place,\n"
                    + "vaChallan.is_doc_impound, vaChallan.is_vch_impdound, vaChallan.chal_officer,"
                    + "vaChallan.nc_c_ref_no, vaChallan.remarks,\n"
                    + "vaChallan.op_dt, vaChallan.state_cd, vaChallan.off_cd,\n"
                    + "office.off_name as RtoDescription,\n"
                    + "state.descr as StateDescription,\n"
                    + "c_owner.chasi_no, c_owner.vh_class, c_owner.vch_off_cd, c_owner.vch_state_cd, \n"
                    + "c_owner.owner_name,\n"
                    + "vh_class.descr as vhClass,vh_class.class_type,"
                    + "(CASE WHEN vh_class.class_type=1 THEN 'TRANSPORT' WHEN vh_class.class_type=2 THEN 'NON TRANSPORT' END) as vehicle_type,\n"
                    + "office_vehicle.off_name as vehicle_RtoDescription,\n"
                    + "state_vehicle.descr as vehicle_StateDescription\n"
                    + "FROM " + TableList.VA_CHALLAN + " vaChallan\n"
                    + "INNER JOIN " + TableList.VT_CHALLAN_OWNER + " c_owner ON vaChallan.appl_no=c_owner.appl_no\n"
                    + "INNER JOIN  " + TableList.TM_OFFICE + " office ON vaChallan.off_cd=office.off_cd and vaChallan.state_cd=office.state_cd\n"
                    + "INNER JOIN  " + TableList.TM_STATE + " state ON vaChallan.state_cd=state.state_code\n"
                    + "INNER JOIN  " + TableList.TM_OFFICE + " office_vehicle ON  c_owner.vch_off_cd=office_vehicle.off_cd and c_owner.vch_state_cd=office_vehicle.state_cd\n"
                    + "INNER JOIN  " + TableList.TM_STATE + " state_vehicle ON c_owner.vch_state_cd=state_vehicle.state_code\n"
                    + "INNER JOIN  " + TableList.VM_VH_CLASS + " vh_class ON vh_class.vh_class=c_owner.vh_class\n"
                    + "where vaChallan.appl_no=? and vaChallan.state_cd=? and vaChallan.off_cd=?";
            tmgr = new TransactionManager("Challan Details");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applicationNo);
            ps.setString(2, State_cd);
            ps.setInt(3, Off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new SaveChallanDobj();
                dobj.setApplicationNO(rs.getString("appl_no"));
                dobj.setChallanNo(rs.getString("chal_no"));
                dobj.setVehno2(rs.getString("regn_no"));
                dobj.setChallanDt(rs.getDate("chal_date"));
                dobj.setChallanTime(rs.getString("chal_time"));
                dobj.setChallanPlace(rs.getString("chal_place"));
                dobj.setDocImpnd(rs.getString("is_doc_impound"));
                dobj.setVehImpnd(rs.getString("is_vch_impdound"));
                dobj.setChalOff(rs.getString("chal_officer"));
                dobj.setNCCRNo(rs.getString("nc_c_ref_no"));
                dobj.setDistrict(rs.getString("vehicle_RtoDescription"));
                dobj.setState(rs.getString("vehicle_StateDescription"));
                dobj.setChasiNo(rs.getString("chasi_no"));
                dobj.setOwnerName(rs.getString("owner_name"));
                dobj.setVhClass(rs.getString("vhClass"));
                dobj.setVehicleType(rs.getString("vehicle_type"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
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
    
    public Map<String, Object> getDocumentCode() throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        Map<String, Object> doc_code = new LinkedHashMap<String, Object>();
        doc_code.put("Select Document", "-1");
        try {
            tmgr = new TransactionManager("getDocumentCode");
            String sql = "select code,descr from " + TableList.VM_DOCUMENTS + "  where state_cd=?  order by descr";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            RowSet resulSet = tmgr.fetchDetachedRowSet();
            while (resulSet.next()) {
                doc_code.put(resulSet.getString("descr"), resulSet.getString("code"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
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
        
        return doc_code;
    }
    
    public void saveUpdatedDetails(String actionCode, Status_dobj status, String changedDataContents, List<AccusedDetailsDobj> accusedDetails, List<DocTableDobj> docImpounded, List<OffencesDobj> offenceDetails, List<CompoundingFeeDobj> ListForCompoundigFee, SaveChallanDobj dobj1, SaveChallanDobj challanDobjPrv, List<WitnessdetailDobj> witnessDobj) {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("saveUpdatedDetails");
            onlySaveNotVerifyApprove(actionCode, status, changedDataContents, accusedDetails, docImpounded, offenceDetails, ListForCompoundigFee, dobj1, challanDobjPrv, witnessDobj, tmgr);
            tmgr.commit();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
            }
        }
    }
    
    public List<SaveChallanDobj> getPreviousChallanDetails(String vehicleNo) throws VahanException {
        List<SaveChallanDobj> PreviousChallanDetails = new ArrayList();
        List<SaveChallanDobj> histChallanDetails = new ArrayList();
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        SaveChallanDobj ChallanDetailsDobj = null;
        String sql = "";
        try {
            sql = "SELECT challan.appl_no, challan.chal_no,challan.chal_place, challan.chal_officer,challan.regn_no, challan.chal_date,owner.owner_name,userr.user_name,challan.remarks\n"
                    + "  FROM " + TableList.VT_CHALLAN + " challan\n"
                    + " LEFT OUTER JOIN  " + TableList.VIEW_VV_OWNER + " owner on challan.regn_no=owner.regn_no and challan.state_cd=owner.state_cd\n"
                    + "  LEFT OUTER JOIN " + TableList.TM_USER_INFO + " userr ON challan.chal_officer:: numeric=userr.user_cd and challan.state_cd=userr.state_cd\n"
                    + "  where challan.regn_no=? and challan.state_cd=?";
            tmgr = new TransactionManager("getPreviousChallanDetails");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, vehicleNo);
            ps.setString(2, Util.getUserStateCode());
            RowSet rowSet = tmgr.fetchDetachedRowSet_No_release();
            while (rowSet.next()) {
                ChallanDetailsDobj = new SaveChallanDobj();
                ChallanDetailsDobj.setApplicationNO(rowSet.getString("appl_no"));
                ChallanDetailsDobj.setChallanNo(rowSet.getString("chal_no"));
                ChallanDetailsDobj.setVehicleNo(rowSet.getString("regn_no"));
                ChallanDetailsDobj.setChallanDt(rowSet.getDate("chal_date"));
                ChallanDetailsDobj.setOwnerName(rowSet.getString("owner_name"));
                ChallanDetailsDobj.setChalOff(rowSet.getString("user_name"));
                ChallanDetailsDobj.setChallanPlace(rowSet.getString("chal_place"));
                ChallanDetailsDobj.setStatusOfChallan("Pending");
                PreviousChallanDetails.add(ChallanDetailsDobj);
            }
            sql = "SELECT challan.appl_no, challan.chal_no,challan.chal_place, challan.chal_officer,challan.regn_no, challan.chal_date,owner.owner_name,userr.user_name\n"
                    + "  FROM " + TableList.VH_CHALLAN + " challan\n"
                    + " LEFT OUTER JOIN  " + TableList.VIEW_VV_OWNER + " owner on challan.regn_no=owner.regn_no and challan.state_cd=owner.state_cd\n"
                    + "  LEFT OUTER JOIN " + TableList.TM_USER_INFO + " userr ON challan.chal_officer:: numeric=userr.user_cd and challan.state_cd=userr.state_cd\n"
                    + "  where challan.regn_no=? and challan.state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, vehicleNo);
            ps.setString(2, Util.getUserStateCode());
            rowSet = tmgr.fetchDetachedRowSet();
            while (rowSet.next()) {
                SaveChallanDobj Dobj = new SaveChallanDobj();
                Dobj.setApplicationNO(rowSet.getString("appl_no"));
                Dobj.setChallanNo(rowSet.getString("chal_no"));
                Dobj.setVehicleNo(rowSet.getString("regn_no"));
                Dobj.setChallanDt(rowSet.getDate("chal_date"));
                Dobj.setOwnerName(rowSet.getString("owner_name"));
                Dobj.setChalOff(rowSet.getString("user_name"));
                Dobj.setChallanPlace(rowSet.getString("chal_place"));
                Dobj.setStatusOfChallan("Diposed");
                histChallanDetails.add(Dobj);
            }
            PreviousChallanDetails.addAll(histChallanDetails);
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
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
        return PreviousChallanDetails;
    }
    
    public String getSelectedMagistrate(String name, String value) throws VahanException {
        String magistrate_cd = "";
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = "";
        
        try {
            sql = "SELECT court_cd, state_cd, magistrate_name, magistrate_cd, court_name\n"
                    + "  FROM  " + TableList.VM_COURT + " where " + name + " = ? ";
            tmgr = new TransactionManager("getSelectedMagistrate");
            ps = tmgr.prepareStatement(sql);
            if ("court_cd".equalsIgnoreCase(name)) {
                ps.setInt(1, Integer.parseInt(value));
            }
            if ("magistrate_cd".equalsIgnoreCase(name)) {
                ps.setString(1, value);
            }
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                if ("court_cd".equalsIgnoreCase(name)) {
                    magistrate_cd = rs.getString("magistrate_cd");
                }
                if ("magistrate_cd".equalsIgnoreCase(name)) {
                    magistrate_cd = rs.getString("court_cd");
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return magistrate_cd;
    }
    //List of goods

    public List getGoodsList() throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        List goodsList = new ArrayList();
        SelectItem item = new SelectItem("-1", "Select Goods");
        goodsList.add(item);
        try {
            tmgr = new TransactionManager("getChallanOfficer");
            String sqlAccused = "select code,descr from " + TableList.VM_CARRY_TO_GOODS;
            ps = tmgr.prepareStatement(sqlAccused);
            RowSet rowSet = tmgr.fetchDetachedRowSet();
            while (rowSet.next()) {
                item = new SelectItem(rowSet.getInt("code"), rowSet.getString("descr"));
                goodsList.add(item);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "-" + e.getStackTrace()[0]);
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
        return goodsList;
    }
    
    public static List< ChallanReportDobj> getVahaneChallanInfo(String regn_no) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        ChallanReportDobj dobj = null;
        PreparedStatement ps = null;
        List<ChallanReportDobj> challanReportDobjList = null;
        RowSet rs = null;
        try {
            
            String sql = " select challan.appl_no,challan.chal_no,to_char(challan.chal_date, 'dd-Mon-yyyy') as chal_date_descr,challan.chal_date,challan.chal_place \n"
                    + " ,amt.cmpd_amt,vt_owner.owner_name \n"
                    + " from " + TableList.VT_CHALLAN + " challan \n"
                    + " left outer join " + TableList.VIEW_VV_OWNER + " vt_owner on vt_owner.regn_no=challan.regn_no  \n"
                    + " left outer join " + TableList.VT_CHALLAN_AMT + "  amt on amt.appl_no=challan.appl_no and  amt.state_cd=challan.state_cd \n"
                    + " where challan.regn_no= ?";
            tmgr = new TransactionManagerReadOnly("printChallanReport");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (challanReportDobjList == null) {
                    challanReportDobjList = new ArrayList<ChallanReportDobj>();
                }
                dobj = new ChallanReportDobj();
                dobj.setVehicle_no(regn_no);
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setChallan_no(rs.getString("chal_no"));
                dobj.setChallan_date_descr(rs.getString("chal_date_descr"));
                dobj.setChal_date(rs.getDate("chal_date"));
                dobj.setOffence_place(rs.getString("chal_place"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setChal_amnt(rs.getLong("cmpd_amt") + "");
                sql = "select offences.offence_desc"
                        + " from " + TableList.VT_VCH_OFFENCES + "  vch_offences\n"
                        + " left outer join " + TableList.VM_OFFENCES + " offences on offences.offence_cd=vch_offences.offence_cd and offences.state_cd=vch_offences.state_cd\n"
                        + "where vch_offences.appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getAppl_no());
                RowSet rs1 = tmgr.fetchDetachedRowSet();
                while (rs1.next()) {
                    if (CommonUtils.isNullOrBlank(dobj.getOffence())) {
                        dobj.setOffence(rs1.getString("offence_desc"));
                    } else {
                        dobj.setOffence(dobj.getOffence() + "/" + rs1.getString("offence_desc"));
                    }
                    
                }
                challanReportDobjList.add(dobj);
            }
            
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
        return challanReportDobjList;
    }
    
    public static ArrayList<ChallanReportDobj>[] fetchDetailsOfChallan(String regn_no) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        RowSet rs = null;
        ArrayList<ChallanReportDobj> pendinglist = new ArrayList<>();
        ArrayList<ChallanReportDobj> disposelist = new ArrayList<>();
        ArrayList<ChallanReportDobj>[] arrList = new ArrayList[2];
        try {
            tmgr = new TransactionManager("fatchDetailsOfChallan");
            
            String sql = "select chal_no, chal_date ,user_name as chal_officer,cmpd_amt,office_name,sum(offence_amt) offence_amt,substring(array_agg(offence_desc)::text,2,length(array_agg(offence_desc)::text)-2) offence_desc\n"
                    + "from(\n"
                    + " select chal_no,to_char(chal_date,'dd-Mon-yyyy') chal_date ,offence_amt,t.off_name as office_name,cmpd_amt,user_name,c.offence_desc\n"
                    + " from " + TableList.VA_CHALLAN + " a\n"
                    + " left join " + TableList.VT_VCH_OFFENCES + " b on  a.state_cd=b.state_cd and a.off_cd=b.off_cd and a.appl_no=b.appl_no \n"
                    + " left join " + TableList.VM_OFFENCES + " c on b.state_cd=c.state_cd and b.offence_cd=c.offence_cd\n"
                    + " left join " + TableList.VT_CHALLAN_AMT + " amt on a.state_cd=amt.state_cd and a.off_cd=amt.off_cd and a.appl_no=amt.appl_no and a.pur_cd=amt.pur_cd\n"
                    + " left join " + TableList.TM_USER_INFO + " tm on a.state_cd=tm.state_cd and a.off_cd=tm.off_cd and a.chal_officer::text=tm.user_cd::text\n"
                    + " left join " + TableList.TM_OFFICE + " t on t.state_cd=a.state_cd and t.off_cd=a.off_cd\n"
                    + " where a.regn_no=? \n"
                    + " union all\n"
                    + " select chal_no,to_char(chal_date,'dd-Mon-yyyy') chal_date ,offence_amt,t.off_name as office_name,settlement_amt cmpd_amt,user_name as chal_Issued_by,c.offence_desc\n"
                    + " from " + TableList.VT_CHALLAN + " a\n"
                    + " left join " + TableList.VT_VCH_OFFENCES + " b on  a.state_cd=b.state_cd and a.off_cd=b.off_cd and a.appl_no=b.appl_no\n"
                    + " left join " + TableList.VM_OFFENCES + " c on b.state_cd=c.state_cd and b.offence_cd=c.offence_cd\n"
                    + " left join " + TableList.VT_CHALLAN_AMT + " amt on a.state_cd=amt.state_cd and a.off_cd=amt.off_cd and a.appl_no=amt.appl_no\n"
                    + " left join " + TableList.TM_USER_INFO + " tm on a.state_cd=tm.state_cd and a.off_cd=tm.off_cd and a.chal_officer::text=tm.user_cd::text\n"
                    + " left join " + TableList.TM_OFFICE + " t on t.state_cd=a.state_cd and t.off_cd=a.off_cd\n"
                    + " where a.regn_no= ? \n"
                    + " ) a group by 1,2,3,4,5 \n";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, regn_no);
            pstmt.setString(2, regn_no);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                ChallanReportDobj dobj = new ChallanReportDobj();
                dobj.setChallan_no(rs.getString("chal_no"));
                dobj.setChallan_dt(rs.getString("chal_date"));
                dobj.setChal_officer(rs.getString("chal_officer"));
                dobj.setCompFee(rs.getLong("cmpd_amt"));
                dobj.setOfficeDescr(rs.getString("office_name"));
                dobj.setOffence_amnt(rs.getInt("offence_amt"));
                dobj.setOffenceDesc(rs.getString("offence_desc"));
                pendinglist.add(dobj);
            }
            sql = "select chal_no,to_char(chal_date,'dd-Mon-yyyy') chal_date, settlement_amt cmpd_amt,user_name as chal_officer,substring(array_agg(offence_desc)::text,2,length(array_agg(offence_desc)::text)-2) offence_desc,disposal_remark\n"
                    + " from " + TableList.VH_CHALLAN + " a\n"
                    + " left join " + TableList.VT_VCH_OFFENCES_HIST + " b on  a.state_cd=b.state_cd and a.off_cd=b.off_cd and a.appl_no=b.appl_no\n"
                    + " left join " + TableList.VM_OFFENCES + " c on b.state_cd=c.state_cd and b.offence_cd=c.offence_cd\n"
                    + " left join " + TableList.VT_CHALLAN_AMT + " amt on a.state_cd=amt.state_cd and a.off_cd=amt.off_cd and a.appl_no=amt.appl_no   \n"
                    + " left join " + TableList.TM_USER_INFO + " tm on a.state_cd=tm.state_cd and a.off_cd=tm.off_cd and a.chal_officer::text=tm.user_cd::text \n"
                    + " where a.regn_no=? and  disposal_remark is not null group by 1,2,3,4,6 ";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, regn_no);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                ChallanReportDobj dobj = new ChallanReportDobj();
                dobj.setChallan_no(rs.getString("chal_no"));
                dobj.setChallan_dt(rs.getString("chal_date"));
                dobj.setChal_officer(rs.getString("chal_officer"));
                dobj.setCompFee(rs.getLong("cmpd_amt"));
                dobj.setOffenceDesc(rs.getString("offence_desc"));
                dobj.setRemark(rs.getString("disposal_remark"));
                disposelist.add(dobj);
            }
            arrList[0] = pendinglist;
            arrList[1] = disposelist;
            
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
            
        }
        return arrList;
    }
    
    public boolean isReceiptNoExist(String receiptNo, String state_cd, int off_cd) throws VahanException {
        PreparedStatement pstm = null;
        TransactionManagerReadOnly tmgr = null;
        RowSet rs = null;
        boolean flag = false;
        String sql = "SELECT * FROM  " + TableList.VP_APPL_RCPT_MAPPING + " WHERE rcpt_no=? and state_cd=? and off_cd=?";
        try {
            tmgr = new TransactionManagerReadOnly("isReceiptNoExist");
            pstm = tmgr.prepareStatement(sql);
            pstm.setString(1, receiptNo);
            pstm.setString(2, state_cd);
            pstm.setInt(3, off_cd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                flag = true;
            }
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
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
        }
        return flag;
    }
    
    public void moveVtChallanTOVhChallan(SaveChallanDobj dobjSaveChallan, TransactionManager tmgr) throws VahanException {
        PreparedStatement pstmt = null;
        String sql = null;
        try {
            
            sql = " INSERT INTO " + TableList.VH_CHALLAN + " SELECT appl_no,  chal_no, "
                    + " regn_no, chal_date, chal_time,"
                    + " chal_place, is_doc_impound, is_vch_impdound, chal_officer, nc_c_ref_no,"
                    + "  remarks,?, current_timestamp, state_cd, off_cd,comming_from,going_to, settled_spot"
                    + "  FROM " + TableList.VT_CHALLAN + " where appl_no=?";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, dobjSaveChallan.getAccusedRemarks());
            pstmt.setString(2, dobjSaveChallan.getApplicationNO());
            pstmt.executeUpdate();
            
            sql = "DELETE FROM   " + TableList.VT_CHALLAN + " WHERE  appl_no=?";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, dobjSaveChallan.getApplicationNO());
            pstmt.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        
    }
}
