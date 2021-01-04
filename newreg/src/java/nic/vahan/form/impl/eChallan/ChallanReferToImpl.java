/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.eChallan;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.eChallan.ChallanReferToDobj;
import nic.vahan.form.dobj.eChallan.ChallanReportDobj;
import nic.vahan.form.dobj.eChallan.InformationViolationReportDobj;
import nic.vahan.form.dobj.eChallan.OffenceReferDetailsDobj;
import nic.vahan.form.dobj.eChallan.OffencesDobj;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author nicsi
 */
public class ChallanReferToImpl {

    private static Logger LOGGER = Logger.getLogger(ChallanReferToImpl.class);

    public Map<String, Object> getChallanApplicationNoList() {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement pstmt = null;
        Map<String, Object> offenceList = new LinkedHashMap<>();
        offenceList.put("Select Application NO", "-1");
        String sql = "";
        sql = "select vtChal.appl_no\n"
                + " from eChallan.vt_challan vtChal \n"
                + " Left Join eChallan.vt_challan_refer_to_court court on court.appl_no=vtChal.appl_no \n"
                + " left join eChallan.vt_vch_offences c on c.appl_no= vtChal.appl_no and c.offence_refer = 'Y'\n"
                + " where court.appl_no is null and vtChal.state_cd=? and vtChal.Off_cd=? order by  vtChal.appl_no";
        try {
            tmgr = new TransactionManagerReadOnly("getChallanApplicationNoList");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, Util.getUserStateCode());
            pstmt.setInt(2, Util.getUserOffCode());
            RowSet rowSet = tmgr.fetchDetachedRowSet();
            while (rowSet.next()) {
                offenceList.put(rowSet.getString("appl_no"), rowSet.getString("appl_no"));
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

        return offenceList;
    }

    public Map<String, Object> getOffenceOfChallan(String appl_no) {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement pstmt = null;
        Map<String, Object> offenceList = null;

        RowSet rowSet = null;
        String sql = "select vtOff.offence_cd,vmOff.offence_desc from echallan.vt_vch_offences vtOff\n"
                + "LEFT OUTER JOIN echallan.vm_offences vmOff on vmOff.offence_cd=vtOff.offence_cd and vtOff.state_cd =?\n"
                + "where vtOff.appl_no=? and vtOff.state_cd=?";
        try {
            tmgr = new TransactionManagerReadOnly("getOffenceOfChallan");

            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, Util.getUserStateCode());
            pstmt.setString(2, appl_no);
            pstmt.setString(3, Util.getUserStateCode());
            rowSet = tmgr.fetchDetachedRowSet();
            while (rowSet.next()) {
                offenceList = new LinkedHashMap<>();
                offenceList.put(rowSet.getString("offence_desc"), rowSet.getString("offence_cd"));
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
        return offenceList;
    }

    public Map<String, Object> getSectionOffenceWise(String offenceCode, String appl_no) throws Exception {

        TransactionManagerReadOnly tmgr = null;
        PreparedStatement pstmt = null;
        Map<String, Object> sectionList = new LinkedHashMap<>();
        RowSet rowSet = null;
        String sql = "select vtOff.offence_cd,vmOff.mva_clause from echallan.vt_vch_offences vtOff \n"
                + " LEFT OUTER JOIN echallan.vm_offences vmOff on vmOff.offence_cd=vtOff.offence_cd and vtOff.state_cd =? \n"
                + " where vtOff.appl_no=? and  vtOff.offence_cd=? and vtOff.state_cd=?";
        try {
            tmgr = new TransactionManagerReadOnly("getSectionOffenceWise");

            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, Util.getUserStateCode());
            pstmt.setString(2, appl_no);
            pstmt.setInt(3, Integer.parseInt(offenceCode));
            pstmt.setString(4, Util.getUserStateCode());
            rowSet = tmgr.fetchDetachedRowSet();
            while (rowSet.next()) {
                sectionList.put(rowSet.getString("mva_clause"), rowSet.getString("offence_cd"));
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
        return sectionList;
    }

    public Map<String, Object> getAuthorityList(String userStateCode) {

        TransactionManagerReadOnly tmgr = null;
        PreparedStatement pstmt = null;
        Map<String, Object> authorityList = new LinkedHashMap<>();
        authorityList.put("Select Authority", "-1");
        RowSet rowSet = null;
        String sql = "select code, fulldescr from echallan.vm_authorities where state_cd=? order by descr";
        try {
            tmgr = new TransactionManagerReadOnly("getAuthorityList");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, Util.getUserStateCode());
            rowSet = tmgr.fetchDetachedRowSet();
            while (rowSet.next()) {
                authorityList.put(rowSet.getString("fulldescr"), rowSet.getString("code"));
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
        return authorityList;

    }

    public OffencesDobj getOffenceDetails(String offenceCode, String appl_no) throws Exception {
        OffencesDobj dobj = null;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement pstmt = null;
        String sql = "select vchOffence.offence_cd,vchOffence.accused_catg,vchOffence.offence_amt,vchOffence.section_cd ,\n"
                + "vmOff.offence_desc,vmOff.mva_clause,accused.descr\n"
                + "from eChallan.vt_vch_offences vchOffence\n"
                + "Left Outer Join eChallan.vm_offences vmOff on vmOff.offence_cd=vchOffence.offence_cd and vmOff.state_cd =?\n"
                + "LEFT OUTER JOIN eChallan.vm_accused accused on accused.code=vchOffence.accused_catg\n"
                + "where appl_no=? and  vchOffence.offence_cd=? and vchOffence.state_cd=?";
        try {
            tmgr = new TransactionManagerReadOnly("getOffenceDetails");

            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, Util.getUserStateCode());
            pstmt.setString(2, appl_no);
            pstmt.setInt(3, Integer.parseInt(offenceCode));
            pstmt.setString(4, Util.getUserStateCode());
            RowSet rowSet = tmgr.fetchDetachedRowSet();
            while (rowSet.next()) {
                dobj = new OffencesDobj(rowSet.getString("accused_catg"), rowSet.getString("descr"), rowSet.getString("offence_cd"), rowSet.getString("offence_desc"), rowSet.getString("offence_amt"), rowSet.getString("mva_clause"));
            }

        } finally {
            tmgr.release();
        }

        return dobj;
    }

    public boolean saveReferedOffencesOrChallan(ChallanReferToDobj dobj, List<OffenceReferDetailsDobj> challanReferDetailList) throws VahanException {
        boolean saveStatus = false;
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        String sqlVtChalRefer = "";
        String sqlVtVchOff = "";
        sqlVtChalRefer = "INSERT INTO " + TableList.VT_CHALLAN_REFER_TO_COURT + "(\n"
                + "            appl_no, court_cd, hearing_date,  \n"
                + "            op_dt, state_cd, off_cd)\n"
                + "    VALUES (?, ?, ?, current_timestamp, ?, \n"
                + "            ?)";
        sqlVtVchOff = "UPDATE echallan.vt_vch_offences\n"
                + "   SET offence_refer=?, refered_authority=? , refer_date=current_date\n"
                + " WHERE appl_no=?  and  offence_cd=?";
        try {
            tmgr = new TransactionManager("saveReferedOffencesOrChallan");
            if (!"-1".equalsIgnoreCase(dobj.getCourtCode())) {
                pstmt = tmgr.prepareStatement(sqlVtChalRefer);
                pstmt.setString(1, dobj.getAppl_no());
                pstmt.setInt(2, Integer.parseInt(dobj.getCourtCode()));
                if (dobj.getHearingDate() != null) {
                    pstmt.setTimestamp(3, ChallanUtil.getTimeStamp(dobj.getHearingDate().toString()));
                } else {
                    pstmt.setTimestamp(3, null);
                }

                pstmt.setString(4, Util.getUserStateCode());
                pstmt.setInt(5, Util.getUserSeatOffCode());
                pstmt.executeUpdate();
            }
            pstmt = null;

            pstmt = tmgr.prepareStatement(sqlVtVchOff);
            for (OffenceReferDetailsDobj offRefDobj : challanReferDetailList) {
                pstmt.setString(1, "Y");
                pstmt.setInt(2, Integer.parseInt(offRefDobj.getAuthorityCode()));
                pstmt.setString(3, dobj.getAppl_no());
                pstmt.setInt(4, Integer.parseInt(offRefDobj.getOffenceCode()));
                pstmt.executeUpdate();
            }

            tmgr.commit();
            saveStatus = true;


        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return saveStatus;

    }

    public ChallanReportDobj getCourtDetailsForIr(String appl_no) throws Exception {
        ChallanReportDobj crDodj = new ChallanReportDobj();
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement pstmt = null;
        String sql = "";
        RowSet rs = null;
        sql = "select \n"
                + " vtchal.appl_no as vt_appl_no, vtchal.chal_no,  vtchal.chal_date, vtchal.chal_time,accused.accused_name,accused.police_station_name,accused.accused_add,accused.dl_no,accused.city,  "
                + " vtchal.chal_place, vtchal.is_doc_impound, vtchal.is_vch_impdound, vtchal.chal_officer,tmUser.user_name, vtchal.nc_c_ref_no,tmUser.desig_cd, \n"
                + " refToCourt.appl_no as court_appl_no,refToCourt.hearing_date,vmCourt.court_name,witness.witness_name,witness.witness_address, witness.police_station_name,\n"
                + " owner.regn_no, owner.chasi_no, owner.vh_class, owner.vch_off_cd, owner.vch_state_cd,owner.owner_name,  owner.color,  \n"
                + " vhClass.descr as vehicle_class_descr, \n"
                + " c.off_name \n"
                + " FROM " + TableList.VT_CHALLAN + " vtchal \n"
                + " left Outer join  " + TableList.VT_CHALLAN_REFER_TO_COURT + " refToCourt on vtchal.appl_no=refToCourt.appl_no \n"
                + " left Outer join  " + TableList.VM_COURT + " vmCourt on vmCourt.court_cd=refToCourt.court_cd and vmCourt.state_cd=refToCourt.state_cd \n"
                + " left Outer join  " + TableList.VT_CHALLAN_OWNER + " owner on vtchal.appl_no=owner.appl_no \n"
                + " left Outer join  " + TableList.VT_CHALLAN_ACCUSED + " accused on accused.appl_no=vtchal.appl_no \n"
                + " left Outer join  " + TableList.VT_WITNESS_DETAILS + " witness on vtchal.appl_no=witness.appl_no \n"
                + " left Outer join  " + TableList.VM_VH_CLASS + " vhClass on owner.vh_class=vhClass.vh_class \n"
                + " left outer join " + TableList.TM_OFFICE + " c on c.state_cd = vtchal.state_cd and c.off_cd = vtchal.off_cd \n"
                + " left Outer join  " + TableList.TM_USER_INFO + " tmUser on vtchal.chal_officer :: numeric=tmUser.user_cd \n"
                + " where vtchal.appl_no=? and vtchal.state_cd=? and vtchal.off_cd=?";

        try {
            tmgr = new TransactionManagerReadOnly("getCourtDetailsForIr");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, appl_no);
            pstmt.setString(2, Util.getUserStateCode());
            pstmt.setInt(3, Util.getSelectedSeat().getOff_cd());

            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                crDodj = new ChallanReportDobj();
                crDodj.setAppl_no(rs.getString("vt_appl_no"));
                crDodj.setChal_date(rs.getDate("chal_date"));
                crDodj.setVehicle_no(rs.getString("regn_no"));
                crDodj.setChallan_no(rs.getString("chal_no"));
                crDodj.setChal_officer(rs.getString("chal_officer"));
                if (rs.getString("owner_name") != null && !rs.getString("owner_name").isEmpty()) {
                    crDodj.setOwner_name(rs.getString("owner_name"));
                } else {
                    crDodj.setOwner_name("NA");
                }
                crDodj.setHearing_date(rs.getDate("hearing_date"));
                crDodj.setCourt_name(rs.getString("court_name"));
                crDodj.setReporting_off(rs.getString("user_name"));
                if (rs.getString("witness_name") != null && !rs.getString("witness_name").isEmpty()) {
                    crDodj.setWitness_name(rs.getString("witness_name"));
                } else {
                    crDodj.setWitness_name("NA");
                }
                crDodj.setWitness_address(rs.getString("witness_address"));
                crDodj.setChal_time(rs.getString("chal_time"));
                crDodj.setChal_place(rs.getString("chal_place"));
                if (rs.getString("accused_name") != null && !rs.getString("accused_name").isEmpty()) {
                    crDodj.setAccused(rs.getString("accused_name"));
                } else {
                    crDodj.setAccused("NA");
                }
                crDodj.setPolice_station(rs.getString("police_station_name"));
                crDodj.setAccused_addr(rs.getString("accused_add"));
                crDodj.setOfficeDescr(rs.getString("off_name"));
                crDodj.setOfficerDesig(rs.getString("desig_cd"));

            }
            RowSet rs1 = null;
            sql = " select \n"
                    + " vmOffences.mva_clause \n "
                    + " FROM " + TableList.VT_CHALLAN + " vtchal \n"
                    + " left Outer join " + TableList.VT_CHALLAN_OWNER + " owner on vtchal.appl_no=owner.appl_no \n"
                    + " left Outer join " + TableList.VT_VCH_OFFENCES + " vchOffence on vtchal.appl_no=vchOffence.appl_no \n"
                    + " left outer join " + TableList.VM_OFFENCES + " vmOffences on vmOffences.offence_cd=vchOffence.offence_cd and vmOffences.state_cd=vtchal.state_cd \n"
                    + " where vtchal.appl_no=? and vtchal.state_cd=? and vtchal.off_cd=? ";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, appl_no);
            pstmt.setString(2, Util.getUserStateCode());
            pstmt.setInt(3, Util.getSelectedSeat().getOff_cd());
            rs1 = tmgr.fetchDetachedRowSet();
            String a = "";
            String d = ",";
            while (rs1.next()) {
                a = a + rs1.getString("mva_clause") + d;
                crDodj.setOffence(a);
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
        return crDodj;
    }
    //function to get court and auhtority details

    public List<InformationViolationReportDobj> getcourtAndAuthDetails(String applNo) {
        InformationViolationReportDobj dobj = null;
        List<InformationViolationReportDobj> list = new ArrayList();
        PreparedStatement pstmt = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = "";
        RowSet rs = null;
        sql = "select offences.refer_date,auth.descr,off.offence_desc\n"
                + " from " + TableList.VT_VCH_OFFENCES + " offences\n"
                + " join echallan.vm_authorities auth on auth.code=offences.refered_authority and auth.state_cd=offences.state_cd\n"
                + " join " + TableList.VM_OFFENCES + " off on off.offence_cd=offences.offence_cd and off.state_cd=offences.state_cd\n"
                + " where offences.appl_no=? and offences.state_cd=?";
        try {
            tmgr = new TransactionManagerReadOnly("fetchWitnessDetails");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, applNo);
            pstmt.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new InformationViolationReportDobj();
                dobj.setAuthorityReferDate(rs.getDate("refer_date"));
                dobj.setAuthorityName(rs.getString("descr"));
                dobj.setOffence(rs.getString("offence_desc"));
                list.add(dobj);
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

        return list;
    }

    public boolean isCourtDetailsExist(String appl_no) throws VahanException {
        boolean isPrExist = false;
        PreparedStatement pstmt = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = "SELECT * from echallan.vt_challan_refer_to_court where appl_no=?";
        try {
            tmgr = new TransactionManagerReadOnly("isCourtDetailsExist");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isPrExist = true;
            } else {
                isPrExist = false;
                throw new VahanException("Can not print Prosecution Report, Due to invalid Application No!!");

            }
        } catch (VahanException e) {
            throw e;
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

        return isPrExist;

    }

    public String isCourtDetailsExistForChallan(String challan_no) throws VahanException {
        String applNo = null;
        PreparedStatement pstmt = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = "Select * from " + TableList.VT_CHALLAN_REFER_TO_COURT + " vt\n"
                + " inner join " + TableList.VT_CHALLAN + " ch on ch.appl_no = vt.appl_no\n"
                + " where ch.chal_no=? ";
        try {
            tmgr = new TransactionManagerReadOnly("isCourtDetailsExistForChallan");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, challan_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                applNo = rs.getString("appl_no");
            } else {

                throw new VahanException("Can not print Prosecution Report, Due to invalid Challan No!!");

            }
        } catch (VahanException e) {
            throw e;
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

        return applNo;

    }
}
