package nic.vahan.form.impl.eChallan;

import nic.vahan.form.dobj.eChallan.DocImpoundDobj;
import nic.vahan.form.dobj.eChallan.CompoundingInOfficeDobj;
import nic.vahan.form.dobj.eChallan.TaxDetailsDobj;
import nic.vahan.form.dobj.eChallan.VehcleOffenceDobj;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.Util;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableList;
import nic.vahan.form.dobj.eChallan.ChallanHoldDobj;
import nic.vahan.form.dobj.eChallan.OffenceReferDetailsDobj;
import nic.vahan.form.dobj.eChallan.OffencesDobj;
import nic.vahan.form.dobj.eChallan.TaxSettlementDobj;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

public class CompoundingOfficeImpl {

    private static Logger LOGGER = Logger.getLogger(CompoundingOfficeImpl.class);

    public CompoundingInOfficeDobj getSettleMentData(String applicationNo, int actionCode) throws VahanException {
        CompoundingInOfficeDobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement pstmtCHALLANSETTLEMENT = null;
        try {
            dobj = new CompoundingInOfficeDobj();
            tmgr = new TransactionManager("SettleChallanDAO : getSettleMentData");

            if (actionCode == TableConstants.VM_ROLE_ENFORCEMENT_SETTLMENT_ENTRY) {
                String checkChallanSql = "SELECT * FROM   " + TableList.VT_CHALLAN_SETTLEMANT + "   WHERE appl_no=? AND STATE_CD=? ";
                pstmtCHALLANSETTLEMENT = tmgr.prepareStatement(checkChallanSql);
                pstmtCHALLANSETTLEMENT.setString(1, applicationNo);
                pstmtCHALLANSETTLEMENT.setString(2, Util.getUserStateCode());
                ResultSet chkchalRs = pstmtCHALLANSETTLEMENT.executeQuery();
                if (chkchalRs.next()) {
                    dobj = null;
                    dobj = getDataForSettlement(applicationNo, actionCode);
                } else {

                    dobj = getDataForSettlement(applicationNo, actionCode);
                }
            } else if (actionCode == TableConstants.VM_ROLE_ENFORCEMENT_SETTLMENT_VERIFY || actionCode == TableConstants.VM_ROLE_ENFORCEMENT_SETTLMENT_APPROVE) {
                dobj = getDataForSettlement(applicationNo, actionCode);
            } else {
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

        return dobj;
    }

    public CompoundingInOfficeDobj getDataForSettlement(String applicationNo, int actionCode) throws VahanException {
        CompoundingInOfficeDobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement pstmtChallanSql = null;
        PreparedStatement pstmtVehImpndDetailSql = null;
        PreparedStatement pstmtRefetToCourt = null;
        try {
            dobj = new CompoundingInOfficeDobj();
            tmgr = new TransactionManager("SettleChallanDAO : getSettleMentData");
            String challansql = " SELECT challan.appl_no,challan.regn_no,TO_CHAR( challan.chal_date,'DD-MON-YYYY')chal_date, challan.chal_time,challan.comming_from,challan.going_to, \n"
                    + "    challan.chal_place,  challan.is_doc_impound, challan.is_vch_impdound, challan.chal_officer, challan.nc_c_ref_no, \n"
                    + "    ownerr.appl_no,ownerr.regn_no, ownerr.chasi_no, ownerr.vh_class, ownerr.vch_off_cd, ownerr.vch_state_cd, ownerr.owner_name, \n"
                    + "    ownerr.seat_cap, ownerr.stand_cap, ownerr.sleeper_cap, ownerr.ld_wt, ownerr.color, ownerr.fuel, ownerr.state_cd, ownerr.off_cd,\n"
                    + "    amount.cmpd_amt,amount.adv_amt,amount.mislleneous_fee,amount.mislleneous_fee_reason, COALESCE(amount.settlement_amt,amount.adv_amt) as totalFee  ,amount.adv_rcpt_no, "
                    + "    VHCLASS.DESCR AS VH_CLASS_DESCR, OFFICER.user_name AS CHALLAN_OFFICER,\n"
                    + "    coalesce(court.court_cd,0)court_cd,coalesce(courts.court_name,'') court_name,TO_CHAR( court.hearing_date,'DD-MON-YYYY')hearing_date, state.descr AS OWNER_STATE_desc,rto.off_name AS OWNER_RTO_desc \n"
                    + "    FROM  " + TableList.VT_CHALLAN + "  challan \n"
                    + "    LEFT OUTER JOIN   " + TableList.VT_CHALLAN_OWNER + "  ownerr ON challan.appl_no=ownerr.appl_no \n"
                    + "    LEFT OUTER  JOIN   " + TableList.VT_CHALLAN_AMT + " amount ON amount.appl_no=ownerr.appl_no  \n"
                    + "    LEFT OUTER JOIN  " + TableList.VM_VH_CLASS + "  VHCLASS ON ownerr.vh_class = VHCLASS.vh_class \n"
                    + "    LEFT OUTER JOIN  " + TableList.TM_STATE + "  state ON state.state_code = ownerr.vch_state_cd \n"
                    + "    LEFT OUTER JOIN  " + TableList.TM_OFFICE + "  rto ON rto.off_cd = ownerr.vch_off_cd and  rto.state_cd=ownerr.vch_state_cd\n"
                    + "    LEFT OUTER JOIN  " + TableList.TM_USER_INFO + "   OFFICER ON challan.chal_officer::int = OFFICER.user_cd \n"
                    + "    LEFT OUTER JOIN   " + TableList.VT_CHALLAN_REFER_TO_COURT + "   court ON challan.appl_no=court.appl_no \n"
                    + "    LEFT OUTER JOIN   echallan.vm_courts   courts ON courts.court_cd=court.court_cd and courts.state_cd=court.state_cd \n"
                    + "    WHERE  challan.appl_no = ? and challan.state_cd=?";
            pstmtChallanSql = tmgr.prepareStatement(challansql);
            pstmtChallanSql.setString(1, applicationNo);
            pstmtChallanSql.setString(2, Util.getUserStateCode());
            ResultSet rs = pstmtChallanSql.executeQuery();
            if (rs.next()) {
                dobj.setAdvcompfee(rs.getString("adv_amt"));
                dobj.setRegnNo(rs.getString("regn_no"));
                dobj.setChasiNo(rs.getString("chasi_no"));
                dobj.setChallanPlace(rs.getString("chal_place"));
                dobj.setNcCrNo(rs.getString("nc_c_ref_no"));
                dobj.setChallanDt(rs.getString("chal_date"));
                dobj.setChallanTime(rs.getString("chal_time"));
                dobj.setVhClass(rs.getString("vh_class"));
                dobj.setChallanPlace(rs.getString("chal_place"));
                dobj.setChallanOfficer(rs.getString("CHALLAN_OFFICER"));
                dobj.setCourtName(rs.getString("court_name"));
                dobj.setHearDate(rs.getString("hearing_date"));
                dobj.setCompoundFee(rs.getString("cmpd_amt"));
                dobj.setOnroadrecptno(rs.getString("adv_rcpt_no"));
                dobj.setTotalFee(rs.getString("totalFee"));
                dobj.setVhClassDescr(rs.getString("VH_CLASS_DESCR"));
                dobj.setVehImpound(rs.getString("is_vch_impdound"));
                dobj.setOwnerName(rs.getString("owner_name"));
                dobj.setSeatCap(rs.getString("seat_cap"));
                dobj.setStandCap(rs.getString("stand_cap"));
                dobj.setSleepCap(rs.getString("sleeper_cap"));
                dobj.setStateOfVeh(rs.getString("OWNER_STATE_desc"));
                dobj.setColor(rs.getString("color"));
                dobj.setFuel(rs.getString("fuel"));
                dobj.setLadenWt(rs.getString("ld_wt"));
                dobj.setRtoOfVeh(rs.getString("OWNER_RTO_desc"));
                dobj.setRtoCd(rs.getString("vch_off_cd"));
                dobj.setStateCd(rs.getString("vch_state_cd"));
                dobj.setMiscFee(rs.getString("mislleneous_fee"));
                dobj.setReason(rs.getString("mislleneous_fee_reason"));
                dobj.setComing_from(rs.getString("comming_from"));
                dobj.setGoing_to(rs.getString("going_to"));
                String vehImpndDetailSql = "SELECT impound_place, contact_off,TO_CHAR(impound_dt,'DD-MON-YYYY') impound_dt,"
                        + " seizure_no FROM   " + TableList.VT_VEHICLE_IMPOUND + "  WHERE appl_no=? ";
                pstmtVehImpndDetailSql = tmgr.prepareStatement(vehImpndDetailSql);
                pstmtVehImpndDetailSql.setString(1, applicationNo);
                ResultSet rsVehImpnd = pstmtVehImpndDetailSql.executeQuery();
                if (rsVehImpnd.next()) {
                    dobj.setImpndDate(rsVehImpnd.getString("impound_dt"));
                    dobj.setImpndPlace(rsVehImpnd.getString("impound_place"));
                    dobj.setSeizureNo(rsVehImpnd.getString("seizure_no"));
                    dobj.setContactOfficer(rsVehImpnd.getString("contact_off"));
                }
                if (!(actionCode == TableConstants.VM_ROLE_ENFORCEMENT_SETTLMENT_ENTRY)) {
                    String sqlRefereToCourt = "SELECT appl_no, court_cd, hearing_date, court_paid_amount,"
                            + " court_rcpt_no, \n"
                            + " remarks, court_status, op_dt, state_cd, off_cd,court_rcpt_date\n"
                            + " FROM   " + TableList.VT_CHALLAN_REFER_TO_COURT + " where appl_no=?";
                    pstmtRefetToCourt = tmgr.prepareStatement(sqlRefereToCourt);
                    pstmtRefetToCourt.setString(1, applicationNo);
                    RowSet rowSet = tmgr.fetchDetachedRowSet();
                    if (rowSet.next()) {
                        dobj.setCourtDecision(rowSet.getString("remarks"));
                        dobj.setCourtPaidAmount(rowSet.getString("court_paid_amount"));
                        dobj.setCourtRecieptNo(rowSet.getString("court_rcpt_no"));
                        dobj.setCourtRecieptDate(rowSet.getDate("court_rcpt_date"));
                    }
                }
            } else {
                dobj = null;
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
            }
        }
        return dobj;
    }

    public List<VehcleOffenceDobj> getvehOffence(CompoundingInOfficeDobj dobj) throws VahanException {
        List offenceList = new ArrayList();
        int offenceCd = 0;
        String offenceDescr = "";
        VehcleOffenceDobj dobjoffence = null;
        TransactionManager tmgr = null;
        PreparedStatement pstmtgetVehOffence = null;
        int slNo = 0;

        try {

            String offenceSql = "SELECT OFFENCE.appl_no, OFFENCE.offence_cd, OFFENCE.accused_catg, OFFENCE.offence_amt,  \n"
                    + "       OFFMAST.OFFENCE_DESC ,accused.descr AS AccusedDescR,OFFMAST.MVA_CLAUSE\n"
                    + "FROM   " + TableList.VT_VCH_OFFENCES + " OFFENCE\n"
                    + "LEFT OUTER JOIN  " + TableList.VM_OFFENCES + "  OFFMAST ON OFFENCE.OFFENCE_CD  = OFFMAST.OFFENCE_CD   and OFFMAST.state_cd=OFFENCE.state_cd \n"
                    + "LEFT OUTER JOIN  " + TableList.VM_ACCUSED + "  accused ON accused.code  =OFFENCE.accused_catg  \n"
                    + " WHERE appl_no=? AND OFFENCE.STATE_CD=? ";

            tmgr = new TransactionManager("SettleChallanDAO : getvehOffence()");
            pstmtgetVehOffence = tmgr.prepareStatement(offenceSql);
            pstmtgetVehOffence.setString(1, dobj.getApplicationNo());
            pstmtgetVehOffence.setString(2, Util.getUserStateCode());
            ResultSet rs = pstmtgetVehOffence.executeQuery();

            while (rs.next()) {
                slNo++;
                offenceCd = Integer.parseInt(rs.getString("offence_cd"));
                if (offenceCd == 999) {
                    offenceDescr = "Miscellaneous Fee";
                } else {
                    offenceDescr = rs.getString("offence_desc");
                }
                if (rs.getString("accused_catg").equals("O")) {
                    dobjoffence = new VehcleOffenceDobj(String.valueOf(slNo), rs.getString("accused_catg"), rs.getString("AccusedDescR"), rs.getString("offence_cd"),
                            rs.getString("MVA_CLAUSE"), rs.getString("offence_amt"), offenceDescr);
                }

                if (rs.getString("accused_catg").equals("D")) {
                    dobjoffence = new VehcleOffenceDobj(String.valueOf(slNo), rs.getString("accused_catg"), rs.getString("AccusedDescR"), rs.getString("offence_cd"),
                            rs.getString("MVA_CLAUSE"), rs.getString("offence_amt"), offenceDescr);
                }
                if (rs.getString("accused_catg").equals("C")) {
                    dobjoffence = new VehcleOffenceDobj(String.valueOf(slNo), rs.getString("accused_catg"), rs.getString("AccusedDescR"), rs.getString("offence_cd"),
                            rs.getString("MVA_CLAUSE"), rs.getString("offence_amt"), offenceDescr);
                }
                offenceList.add(dobjoffence);

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
            }
        }

        return offenceList;
    }

    public List<TaxDetailsDobj> getTaxDetails(CompoundingInOfficeDobj dobj) throws VahanException {
        List taxList = new ArrayList();
        TaxDetailsDobj dobjTax = null;
        int slNo = 0;
        TransactionManager tmgr = null;
        PreparedStatement pstmtgetTaxDetails = null;

        String taxDetailSql = "  select to_char(tax_from,'DD-MON-YYYY')as tax_from,to_char(tax_upto,'DD-MON-YYYY') "
                + " as tax_upto,chal_tax,chal_penalty,chal_interest,pur_cd,cmpd_rcpt_no,cmpd_rcpt_dt "
                + " from  " + TableList.VT_CHALLAN_TAX + " WHERE appl_no=? ";
        try {
            tmgr = new TransactionManager("SettleChallanDAO : getTaxDetails()");
            pstmtgetTaxDetails = tmgr.prepareStatement(taxDetailSql);
            pstmtgetTaxDetails.setString(1, dobj.getApplicationNo());
            ResultSet rs = pstmtgetTaxDetails.executeQuery();
            while (rs.next()) {
                slNo++;
                dobjTax = new TaxDetailsDobj();
                dobjTax.setTaxFrom(rs.getString("tax_from"));
                dobjTax.setTaxUpto(rs.getString("tax_upto"));
                dobjTax.setCfInterest(rs.getString("chal_interest"));
                dobjTax.setCfPeanlty(rs.getString("chal_penalty"));
                dobjTax.setCfTax(rs.getString("chal_tax"));
                dobjTax.setTaxType(rs.getString("pur_cd"));
                dobjTax.setTaxRcptNo(rs.getString("cmpd_rcpt_no"));
                dobjTax.setTaxRcptDate(rs.getString("cmpd_rcpt_dt"));
                taxList.add(dobjTax);
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
            }
        }
        return taxList;
    }

    public void addMiscellaneousFees(String appl_no, String miscFee, String reasonOffMissFee) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement pstmtVEHOFFENCE = null;
        boolean offanceExistance = false;
        String sql = "";
        String sqlCheckOffenceExistance = "SELECT appl_no, offence_cd, accused_catg, offence_amt, remarks, "
                + "  da_status, \n"
                + "  offence_status, state_cd, off_cd\n"
                + "  FROM  " + TableList.VT_VCH_OFFENCES + " WHERE appl_no=? and offence_cd=?";

        String saveOffenceSql = "INSERT INTO " + TableList.VT_VCH_OFFENCES + " ( appl_no, offence_cd, accused_catg, offence_amt,remarks,"
                + " state_cd, off_cd)VALUES (?, ?, ?, ?, ?, ?,?)";

        String sqlUpdateOffence = "UPDATE " + TableList.VT_VCH_OFFENCES + "\n"
                + "   SET appl_no=?, offence_cd=?, accused_catg=?, offence_amt=?, remarks=?, \n"
                + "        state_cd=?, off_cd=?\n"
                + " WHERE appl_no=? and offence_cd=?";

        try {
            tmgr = new TransactionManager("SettleChallanDAO : addMiscellaneousFees()");

            pstmtVEHOFFENCE = tmgr.prepareStatement(sqlCheckOffenceExistance);
            pstmtVEHOFFENCE.setString(1, appl_no);
            pstmtVEHOFFENCE.setInt(2, 999);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                offanceExistance = true;
                sql = sqlUpdateOffence;
            } else {
                offanceExistance = false;
                sql = saveOffenceSql;
            }
            pstmtVEHOFFENCE = tmgr.prepareStatement(sql);
            pstmtVEHOFFENCE.setString(1, appl_no);
            pstmtVEHOFFENCE.setInt(2, 999);
            pstmtVEHOFFENCE.setString(3, "O");
            pstmtVEHOFFENCE.setInt(4, Integer.parseInt(miscFee));
            pstmtVEHOFFENCE.setString(5, reasonOffMissFee);
            pstmtVEHOFFENCE.setString(6, Util.getUserStateCode());
            pstmtVEHOFFENCE.setInt(7, Util.getSelectedSeat().getOff_cd());
            if (offanceExistance) {
                pstmtVEHOFFENCE.setString(8, appl_no);
                pstmtVEHOFFENCE.setInt(9, 999);
            }
            pstmtVEHOFFENCE.execute();
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

    //function to add parking fee
    public void addMissFees(String appl_no, String fee, String reason) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement pstmtVEHOFFENCE = null;

        String sqlUpdateOffence = "UPDATE " + TableList.VT_CHALLAN_AMT + "\n"
                + "   SET mislleneous_fee=?,mislleneous_fee_reason=?\n"
                + " WHERE appl_no=? and state_cd=? and off_cd=?";

        try {
            tmgr = new TransactionManager("SettleChallanDAO : addMiscellaneousFees()");
            pstmtVEHOFFENCE = tmgr.prepareStatement(sqlUpdateOffence);
            pstmtVEHOFFENCE.setInt(1, Integer.parseInt(fee));
            pstmtVEHOFFENCE.setString(2, reason);
            pstmtVEHOFFENCE.setString(3, appl_no);
            pstmtVEHOFFENCE.setString(4, Util.getUserStateCode());
            pstmtVEHOFFENCE.setInt(5, Util.getSelectedSeat().getOff_cd());
            pstmtVEHOFFENCE.execute();
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

    public List<DocImpoundDobj> addDocImpoundData(CompoundingInOfficeDobj dobj) throws VahanException {
        List docImpoundList = new ArrayList();
        DocImpoundDobj dobjdocImpnd = null;
        TransactionManager tmgr = null;
        PreparedStatement pstmtVMDOCCD = null;


        String docImpndSql = "SELECT VMDOC.DESCR AS DOC_CD,DOC.accused_catg,accused.descr as AccusedDescr,DOC.DOC_NO,"
                + " DOC.ISS_AUTH,TO_CHAR(DOC.VALIDITY,'DD-MON-YYYY') AS VALIDITY,DOC.other_doc_name "
                + " FROM  " + TableList.VT_DOCS_IMPOUND + "  DOC "
                + " INNER JOIN " + TableList.VM_DOCUMENTS + "  VMDOC ON DOC.DOC_CD = VMDOC.CODE  "
                + " INNER JOIN " + TableList.VM_ACCUSED + "  accused ON accused.code=DOC.accused_catg "
                + "  WHERE appl_no=? AND DOC.STATE_CD=?";
        try {
            tmgr = new TransactionManager("SettleChallanDAO : addDocImpoundData()");
            pstmtVMDOCCD = tmgr.prepareStatement(docImpndSql);
            pstmtVMDOCCD.setString(1, dobj.getApplicationNo());
            pstmtVMDOCCD.setString(2, Util.getUserStateCode());
            ResultSet rs = pstmtVMDOCCD.executeQuery();

            while (rs.next()) {

                dobjdocImpnd = new DocImpoundDobj(rs.getString("accused_catg"), rs.getString("AccusedDescr"), rs.getString("doc_cd"), rs.getString("doc_no"), rs.getString("iss_auth").toUpperCase(), rs.getString("validity"), rs.getString("other_doc_name"));
                docImpoundList.add(dobjdocImpnd);
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
            }
        }

        return docImpoundList;
    }

    public boolean saveSettleData(CompoundingInOfficeDobj dobj, List<TaxDetailsDobj> taxDetailList, Status_dobj statusDobj, boolean isFeePaidAtCOurt, String roleCd, List<VehcleOffenceDobj> VehOffenceList, String refered, List<OffenceReferDetailsDobj> authList, List<TaxSettlementDobj> settleTaxDetail) throws VahanException {
        boolean flag = false;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String appl_no = dobj.getApplicationNo();
        boolean checkReferTOCourtFlag = false;
        RowSet rs1 = null;
        try {

            tmgr = new TransactionManager("SettleChallanDAO : saveSettleData");
            checkReferTOCourtFlag = getStatusOfReferToCourt(tmgr, dobj.getApplicationNo(), dobj, isFeePaidAtCOurt, roleCd);

            if (checkReferTOCourtFlag == isFeePaidAtCOurt) {
                String chkChallanTax = "SELECT * FROM  " + TableList.VT_CHALLAN_TAX + "  WHERE appl_no=?  ";
                ps = tmgr.prepareStatement(chkChallanTax);
                ps.setString(1, dobj.getApplicationNo());
                if (taxDetailList.size() > 0) {
                    RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        String challanTaxSql = "UPDATE  " + TableList.VT_CHALLAN_TAX + " "
                                + " SET chal_tax=?, chal_penalty=?, "
                                + " chal_interest=? "
                                + " WHERE appl_no=?";
                        ps = tmgr.prepareStatement(challanTaxSql);
                        ps.setInt(1, Integer.parseInt(dobj.getSettleChalTax()));
                        ps.setInt(2, Integer.parseInt(dobj.getSettleChalPenalty()));
                        ps.setInt(3, Integer.parseInt(dobj.getSettleChalInterest()));
                        ps.setString(4, dobj.getApplicationNo());
                        ps.executeUpdate();

                    }
                }

                if (TableConstants.VM_ROLE_ENFORCEMENT_SETTLMENT_ENTRY == Integer.parseInt(roleCd)) {

                    String sql = "INSERT INTO " + TableList.VT_CHALLAN_TAX + "(\n"
                            + "            appl_no, pur_cd, tax_from, tax_upto,  \n"
                            + "            chal_tax, chal_penalty, chal_interest, \n"
                            + "             state_cd, \n"
                            + "            off_cd)\n"
                            + "    VALUES (?, ?, ?, ?, ?, ?, \n"
                            + "            ?, ?, ? \n"
                            + "           )";

                    ps = tmgr.prepareStatement(sql);
                    if (settleTaxDetail.size() > 0 && settleTaxDetail != null) {
                        for (TaxSettlementDobj taxSettleDobj : settleTaxDetail) {
                            ps.setString(1, dobj.getApplicationNo());
                            ps.setInt(2, Integer.parseInt(taxSettleDobj.getTaxType()));
                            ps.setTimestamp(3, ChallanUtil.getTimeStamp(taxSettleDobj.getTaxFrom().toString()));
                            ps.setTimestamp(4, ChallanUtil.getTimeStamp(taxSettleDobj.getTaxUpto().toString()));
                            ps.setInt(5, Integer.parseInt(taxSettleDobj.getSetComTax()));
                            ps.setInt(6, Integer.parseInt(taxSettleDobj.getSetComPenalty()));
                            ps.setInt(7, Integer.parseInt(taxSettleDobj.getSetComInterst()));
                            ps.setString(8, Util.getUserStateCode());
                            ps.setInt(9, Util.getSelectedSeat().getOff_cd());
                        }
                        ps.executeUpdate();
                    }


                    String challanSettleSql = "INSERT INTO " + TableList.VT_CHALLAN_SETTLEMANT + "   ( "
                            + "appl_no, VEH_NO,  OP_DT, DEAL_CD, STATE_CD, off_cd)"
                            + " VALUES (?, ?, ?, ?, ?, ?)";
                    ps = tmgr.prepareStatement(challanSettleSql);
                    ps.setString(1, dobj.getApplicationNo());
                    ps.setString(2, dobj.getRegnNo());
                    ps.setDate(3, new java.sql.Date(new java.util.Date().getTime()));
                    ps.setString(4, Util.getEmpCode());
                    ps.setString(5, Util.getUserStateCode());
                    ps.setInt(6, Util.getSelectedSeat().getOff_cd());
                    ps.execute();
                }

                String SqlUpdate = "Update " + TableList.VT_VCH_OFFENCES + " set offence_amt=? where appl_no=? and state_cd=? and offence_cd=?";
                ps = tmgr.prepareStatement(SqlUpdate);
                for (VehcleOffenceDobj dobj1 : VehOffenceList) {
                    ps.setInt(1, Integer.parseInt(dobj1.getPenalty()));
                    ps.setString(2, dobj.getApplicationNo());
                    ps.setString(3, Util.getUserStateCode());
                    ps.setInt(4, Integer.parseInt(dobj1.getOffenceCd()));
                    ps.executeUpdate();
                }

                String sql1 = "Select * from " + TableList.VT_CHALLAN_AMT + ""
                        + " where appl_no=? and state_cd=?";
                ps = tmgr.prepareStatement(sql1);
                ps.setString(1, dobj.getApplicationNo());
                ps.setString(2, Util.getUserStateCode());
                rs1 = tmgr.fetchDetachedRowSet_No_release();
                if (rs1.next()) {
                    String challanFeeSql = "UPDATE " + TableList.VT_CHALLAN_AMT + " SET settlement_amt=? ,cmpd_amt=? WHERE appl_no=? ";
                    ps = tmgr.prepareStatement(challanFeeSql);
                    ps.setInt(1, Integer.parseInt(dobj.getSettleFee()));
                    ps.setInt(2, Integer.parseInt(dobj.getCompoundFee()));
                    ps.setString(3, dobj.getApplicationNo());
                    ps.executeUpdate();
                }
                if (refered.equals("Y")) {
                    ps = null;
                    String SqlUpdate1 = "Update " + TableList.VT_VCH_OFFENCES + " set authority_decision=? ,auth_decision_date=?where appl_no=? and state_cd=? and offence_cd=?";
                    ps = tmgr.prepareStatement(SqlUpdate1);
                    for (OffenceReferDetailsDobj dobj1 : authList) {
                        ps.setString(1, dobj1.getAuthority_decision());
                        ps.setTimestamp(2, new java.sql.Timestamp(dobj1.getDecision_date().getTime()));
                        ps.setString(3, dobj.getApplicationNo());
                        ps.setString(4, Util.getUserStateCode());
                        ps.setInt(5, Integer.parseInt(dobj1.getOffenceCode()));
                        ps.executeUpdate();
                    }
                }
                if (Integer.parseInt(roleCd) == TableConstants.VM_ROLE_ENFORCEMENT_SETTLMENT_ENTRY) {
                    if (!("".equals(appl_no)) && appl_no != null) {
                        statusDobj = ServerUtil.webServiceForNextStage(statusDobj, tmgr);
                        ServerUtil.fileFlow(tmgr, statusDobj);
                    }
                }
                tmgr.commit();
                flag = true;
            }
            if ((isFeePaidAtCOurt == true) && (checkReferTOCourtFlag == false)) {
                JSFUtils.setFacesMessage("Challan Not Refer TO Court", "message", JSFUtils.WARN);
                flag = false;
            }
            if ((isFeePaidAtCOurt == false) && (checkReferTOCourtFlag == true)) {
                if (Integer.parseInt(roleCd) == TableConstants.VM_ROLE_ENFORCEMENT_SETTLMENT_ENTRY) {

                    String chkChallanTax = "SELECT * FROM  " + TableList.VT_CHALLAN_TAX + "  WHERE appl_no=?  ";
                    ps = tmgr.prepareStatement(chkChallanTax);
                    ps.setString(1, dobj.getApplicationNo());
                    if (taxDetailList.size() > 0) {
                        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                        if (rs.next()) {
                            String sql = "INSERT INTO " + TableList.VT_CHALLAN_TAX + "(\n"
                                    + "            appl_no, pur_cd, tax_from, tax_upto,  \n"
                                    + "            chal_tax, chal_penalty, chal_interest, \n"
                                    + "            state_cd, \n"
                                    + "            off_cd)\n"
                                    + "    VALUES (?, ?, ?, ?, ?, ?, \n"
                                    + "            ?, ?,? \n"
                                    + "           )";
                            ps = tmgr.prepareStatement(sql);
                            for (TaxSettlementDobj taxSettleDobj : settleTaxDetail) {
                                ps.setString(1, dobj.getApplicationNo());
                                ps.setInt(2, Integer.parseInt(taxSettleDobj.getTaxType()));
                                ps.setTimestamp(3, ChallanUtil.getTimeStamp(taxSettleDobj.getTaxFrom()));
                                ps.setTimestamp(4, ChallanUtil.getTimeStamp(taxSettleDobj.getTaxUpto()));
                                ps.setInt(5, Integer.parseInt(taxSettleDobj.getSetComTax()));
                                ps.setInt(6, Integer.parseInt(taxSettleDobj.getSetComPenalty()));
                                ps.setInt(7, Integer.parseInt(taxSettleDobj.getSetComInterst()));
                                ps.setString(8, Util.getUserStateCode());
                                ps.setInt(9, Util.getSelectedSeat().getOff_cd());
                            }
                            ps.executeUpdate();
                        }
                    }
                    String updateChallanOwner = "UPDATE " + TableList.VT_CHALLAN_OWNER
                            + "   SET vch_off_cd=?, vch_state_cd=?, \n"
                            + "       owner_name=?, seat_cap=?, stand_cap=?, sleeper_cap=?, ld_wt=?, \n"
                            + "       color=?, fuel=?\n"
                            + " WHERE appl_no=? ";
                    ps = tmgr.prepareStatement(updateChallanOwner);
                    ps.setInt(1, Integer.parseInt(dobj.getRtoOfVeh()));
                    ps.setString(2, dobj.getStateOfVeh());
                    ps.setString(3, dobj.getOwnerName());
                    ps.setInt(4, Integer.parseInt(dobj.getSeatCap()));
                    ps.setInt(5, Integer.parseInt(dobj.getStandCap()));
                    ps.setInt(6, Integer.parseInt(dobj.getSleepCap()));
                    ps.setInt(7, Integer.parseInt(dobj.getLadenWt()));
                    ps.setString(8, dobj.getColor());
                    ps.setInt(9, Integer.parseInt(dobj.getFuel()));
                    ps.setString(10, dobj.getApplicationNo());
                    ps.executeUpdate();




                    String challanSettleSql = "INSERT INTO " + TableList.VT_CHALLAN_SETTLEMANT + "   ( "
                            + "appl_no, VEH_NO,  OP_DT, DEAL_CD, STATE_CD, off_cd)"
                            + " VALUES (?, ?, ?, ?, ?, ?)";
                    ps = tmgr.prepareStatement(challanSettleSql);
                    ps.setString(1, dobj.getApplicationNo());
                    ps.setString(2, dobj.getRegnNo());
                    ps.setDate(3, new java.sql.Date(new java.util.Date().getTime()));
                    ps.setString(4, Util.getEmpCode());
                    ps.setString(5, Util.getUserStateCode());
                    ps.setInt(6, Util.getSelectedSeat().getOff_cd());
                    ps.execute();

                }
                String SqlUpdate = "Update " + TableList.VT_VCH_OFFENCES + " set offence_amt=? where appl_no=? and state_cd=? and offence_cd=?";
                ps = tmgr.prepareStatement(SqlUpdate);
                for (VehcleOffenceDobj dobj1 : VehOffenceList) {
                    ps.setInt(1, Integer.parseInt(dobj1.getPenalty()));
                    ps.setString(2, dobj.getApplicationNo());
                    ps.setString(3, Util.getUserStateCode());
                    ps.setInt(4, Integer.parseInt(dobj1.getOffenceCd()));
                    ps.executeUpdate();
                }

                String sql1 = "Select * from " + TableList.VT_CHALLAN_AMT + " where appl_no=? and state_cd=? and adv_amt>0 and (adv_rcpt_no is not null OR adv_rcpt_no!='')";
                ps = tmgr.prepareStatement(sql1);
                ps.setString(1, dobj.getApplicationNo());
                ps.setString(2, Util.getUserStateCode());
                rs1 = tmgr.fetchDetachedRowSet_No_release();
                if (!rs1.next()) {
                    String challanFeeSql = "UPDATE " + TableList.VT_CHALLAN_AMT + " SET settlement_amt=? ,cmpd_amt=? WHERE appl_no=? ";
                    ps = tmgr.prepareStatement(challanFeeSql);
                    ps.setInt(1, Integer.parseInt(dobj.getSettleFee()));
                    ps.setInt(2, Integer.parseInt(dobj.getCompoundFee()));
                    ps.setString(3, dobj.getApplicationNo());
                    ps.executeUpdate();
                }
                if (refered.equals("Y")) {
                    ps = null;
                    String SqlUpdate1 = "Update " + TableList.VT_VCH_OFFENCES + " set authority_decision=? ,auth_decision_date=? where appl_no=? and state_cd=? and offence_cd=?";
                    ps = tmgr.prepareStatement(SqlUpdate1);
                    for (OffenceReferDetailsDobj dobj1 : authList) {
                        ps.setString(1, dobj1.getAuthority_decision());
                        ps.setTimestamp(2, getTimeStamp(dobj1.getDecision_date().toString()));
                        ps.setString(3, dobj.getApplicationNo());
                        ps.setString(4, Util.getUserStateCode());
                        ps.setInt(5, Integer.parseInt(dobj1.getOffenceCode()));
                        ps.executeUpdate();

                    }
                }


                if (Integer.parseInt(roleCd) == TableConstants.VM_ROLE_ENFORCEMENT_SETTLMENT_ENTRY) {
                    if (!("".equals(appl_no)) && appl_no != null) {
                        statusDobj = ServerUtil.webServiceForNextStage(statusDobj, tmgr);
                        ServerUtil.fileFlow(tmgr, statusDobj);
                    }
                }
                tmgr.commit();
                flag = true;

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
        return flag;
    }

    private boolean getStatusOfReferToCourt(TransactionManager tmgr, String applNo, CompoundingInOfficeDobj dobj, boolean isFeePaidAtCOurt, String roleCd) throws VahanException {
        boolean flag = false;
        PreparedStatement pstmtVtChallan = null;
        PreparedStatement pstmtVtReferToCourt = null;
        String sqlSelectApplNo = "";
        try {
            String sqlSelectApplNo1 = "SELECT * FROM " + TableList.VT_CHALLAN_REFER_TO_COURT + " "
                    + "where appl_no = ? "
                    + "and (court_paid_amount is null or court_rcpt_no is null "
                    + "or remarks is null or court_status is null)";
            String sqlSelectApplNo2 = "SELECT * FROM " + TableList.VT_CHALLAN_REFER_TO_COURT + " "
                    + "where appl_no = ? ";
            if (Integer.parseInt(roleCd) == TableConstants.VM_ROLE_ENFORCEMENT_SETTLMENT_ENTRY) {
                sqlSelectApplNo = sqlSelectApplNo1;
            }
            if (!(Integer.parseInt(roleCd) == TableConstants.VM_ROLE_ENFORCEMENT_SETTLMENT_ENTRY)) {
                sqlSelectApplNo = sqlSelectApplNo2;
            }
            pstmtVtChallan = tmgr.prepareStatement(sqlSelectApplNo);
            pstmtVtChallan.setString(1, applNo);
            RowSet rowSet = tmgr.fetchDetachedRowSet();
            if (rowSet.next()) {
                flag = true;
            }
            if (flag == true && isFeePaidAtCOurt == true) {
                String sqlRefTOCourt = "UPDATE " + TableList.VT_CHALLAN_REFER_TO_COURT + " "
                        + " SET court_paid_amount=?, court_rcpt_no=?,  remarks=?, "
                        + "court_status=?,court_rcpt_date=? WHERE appl_no=?";

                pstmtVtReferToCourt = tmgr.prepareStatement(sqlRefTOCourt);
                pstmtVtReferToCourt.setInt(1, Integer.parseInt(dobj.getCourtPaidAmount()));
                pstmtVtReferToCourt.setString(2, dobj.getCourtRecieptNo());
                pstmtVtReferToCourt.setString(3, dobj.getCourtDecision());
                pstmtVtReferToCourt.setString(4, "Y");
                pstmtVtReferToCourt.setDate(5, new java.sql.Date(dobj.getCourtRecieptDate().getTime()));
                pstmtVtReferToCourt.setString(6, applNo);
                pstmtVtReferToCourt.executeUpdate();

            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }

        return flag;
    }

    public boolean getCheckSattlementIsNeeded(String applicationNo) throws VahanException {
        boolean checkSettlment = true;
        TransactionManager tmgr = null;
        CompoundingInOfficeDobj dobj = null;
        PreparedStatement pstmtCheckSettalment = null;
        try {
            dobj = new CompoundingInOfficeDobj();
            tmgr = new TransactionManager("checkSettelment : is Nedded");
            String checkChallanSql = "Select amt.adv_rcpt_no,amt.adv_rcpt_dt,tax.chal_tax From " + TableList.VT_CHALLAN_AMT + " amt inner join " + TableList.VT_CHALLAN_TAX + " tax on amt.appl_no=tax.appl_no AND amt.STATE_CD=tax.STATE_CD where amt.appl_no= ? AND amt.STATE_CD=? ";
            pstmtCheckSettalment = tmgr.prepareStatement(checkChallanSql);
            pstmtCheckSettalment.setString(1, applicationNo);
            pstmtCheckSettalment.setString(2, Util.getUserStateCode());
            ResultSet chkchalRs = pstmtCheckSettalment.executeQuery();
            if (chkchalRs.next()) {
                String advCompFee = chkchalRs.getString("adv_rcpt_no");
                String advrcptdate = chkchalRs.getString("adv_rcpt_dt");
                String onroadtax = chkchalRs.getString("chal_tax");
                if ("".equals(advCompFee) || ("0".equals(onroadtax))) {
                    checkSettlment = true;
                } else {
                    checkSettlment = false;
                }
            } else {
                checkSettlment = true;
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
            }
        }
        return checkSettlment;
    }

    public void movetoapprove(String roleCd, Status_dobj statusDobj, String changedDataContents, CompoundingInOfficeDobj dobjSettlement, List<TaxDetailsDobj> taxDetailList, boolean isFeePaidAtCOurt, List<VehcleOffenceDobj> VehOffenceList, String refered, List<OffenceReferDetailsDobj> authList) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement pstmtCHALLANTAX = null;
        PreparedStatement pstmtCHALLANFEE = null;
        PreparedStatement pstmtCHKCHALLANTAX = null;
        String appl_no = statusDobj.getAppl_no();
        boolean checkReferTOCourtFlag = false;
        try {
            tmgr = new TransactionManager("SettleChallanDAO : saveSettleData");
            if (!changedDataContents.isEmpty()) {
                checkReferTOCourtFlag = getStatusOfReferToCourt(tmgr, dobjSettlement.getApplicationNo(), dobjSettlement, isFeePaidAtCOurt, roleCd);
                if (checkReferTOCourtFlag == isFeePaidAtCOurt) {
                    String chkChallanTax = "SELECT * FROM  " + TableList.VT_CHALLAN_TAX + "  WHERE appl_no=?  ";
                    pstmtCHKCHALLANTAX = tmgr.prepareStatement(chkChallanTax);
                    pstmtCHKCHALLANTAX.setString(1, dobjSettlement.getApplicationNo());
                    if (taxDetailList.size() > 0) {
                        ResultSet rs = pstmtCHKCHALLANTAX.executeQuery();
                        if (rs.next()) {
                            String challanTaxSql = "UPDATE  " + TableList.VT_CHALLAN_TAX + " "
                                    + " SET chal_tax=?, chal_penalty=?, "
                                    + " chal_interest=? "
                                    + " WHERE appl_no=?";
                            pstmtCHALLANTAX = tmgr.prepareStatement(challanTaxSql);
                            pstmtCHALLANTAX.setInt(1, Integer.parseInt(dobjSettlement.getSettleChalTax()));
                            pstmtCHALLANTAX.setInt(2, Integer.parseInt(dobjSettlement.getSettleChalPenalty()));
                            pstmtCHALLANTAX.setInt(3, Integer.parseInt(dobjSettlement.getSettleChalInterest()));
                            pstmtCHALLANTAX.setString(4, dobjSettlement.getApplicationNo());
                            pstmtCHALLANTAX.execute();
                            String SqlUpdate = "Update " + TableList.VT_VCH_OFFENCES + " set offence_amt=? where appl_no=? and state_cd=? and offence_cd=?";
                            PreparedStatement pstmVT_VCH_OFFENCES = tmgr.prepareStatement(SqlUpdate);
                            for (VehcleOffenceDobj dobj1 : VehOffenceList) {
                                pstmVT_VCH_OFFENCES.setInt(1, Integer.parseInt(dobj1.getPenalty()));
                                pstmVT_VCH_OFFENCES.setString(2, dobjSettlement.getApplicationNo());
                                pstmVT_VCH_OFFENCES.setString(3, Util.getUserStateCode());
                                pstmVT_VCH_OFFENCES.setInt(4, Integer.parseInt(dobj1.getOffenceCd()));
                                pstmVT_VCH_OFFENCES.executeUpdate();
                            }
                            if (refered.equals("Y")) {
                                pstmVT_VCH_OFFENCES = null;
                                String SqlUpdate1 = "Update " + TableList.VT_VCH_OFFENCES + " set authority_decision=? ,auth_decision_date=?where appl_no=? and state_cd=? and offence_cd=?";
                                pstmVT_VCH_OFFENCES = tmgr.prepareStatement(SqlUpdate1);
                                for (OffenceReferDetailsDobj dobj1 : authList) {
                                    pstmVT_VCH_OFFENCES.setString(1, dobj1.getAuthority_decision());
                                    pstmVT_VCH_OFFENCES.setTimestamp(2, getTimeStamp(dobj1.getDecision_date().toString()));
                                    pstmVT_VCH_OFFENCES.setString(3, dobjSettlement.getApplicationNo());
                                    pstmVT_VCH_OFFENCES.setString(4, Util.getUserStateCode());
                                    pstmVT_VCH_OFFENCES.setInt(5, Integer.parseInt(dobj1.getOffenceCode()));
                                    pstmVT_VCH_OFFENCES.executeUpdate();
                                }
                            }
                        }
                    }
                    PreparedStatement pstmt1 = null;
                    ResultSet rs1 = null;
                    String sql1 = "Select * from " + TableList.VT_CHALLAN_AMT + " where appl_no=? and state_cd=? and adv_amt>0 and (adv_rcpt_no is not null OR adv_rcpt_no!='')";
                    pstmt1 = tmgr.prepareStatement(sql1);
                    pstmt1.setString(1, dobjSettlement.getApplicationNo());
                    pstmt1.setString(2, Util.getUserStateCode());
                    rs1 = tmgr.fetchDetachedRowSet_No_release();
                    if (!rs1.next()) {
                        String challanFeeSql = "UPDATE " + TableList.VT_CHALLAN_AMT + "  SET settlement_amt=? ,cmpd_amt=? WHERE appl_no=? ";
                        pstmtCHALLANFEE = tmgr.prepareStatement(challanFeeSql);
                        pstmtCHALLANFEE.setInt(1, Integer.parseInt(dobjSettlement.getSettleFee()));
                        pstmtCHALLANFEE.setInt(2, Integer.parseInt(dobjSettlement.getCompoundFee()));
                        pstmtCHALLANFEE.setString(3, dobjSettlement.getApplicationNo());
                        pstmtCHALLANFEE.execute();
                    }


                }
                if ((isFeePaidAtCOurt == true) && (checkReferTOCourtFlag == false)) {
                    JSFUtils.setFacesMessage("Challan Not Refer TO Court", "message", JSFUtils.WARN);
                    return;
                }
                if ((isFeePaidAtCOurt == false) && (checkReferTOCourtFlag == true)) {
                    String SqlUpdate = "Update " + TableList.VT_VCH_OFFENCES + " set offence_amt=? where appl_no=? and state_cd=? and offence_cd=?";
                    PreparedStatement pstmVT_VCH_OFFENCES = tmgr.prepareStatement(SqlUpdate);
                    for (VehcleOffenceDobj dobj1 : VehOffenceList) {
                        pstmVT_VCH_OFFENCES.setInt(1, Integer.parseInt(dobj1.getPenalty()));
                        pstmVT_VCH_OFFENCES.setString(2, dobjSettlement.getApplicationNo());
                        pstmVT_VCH_OFFENCES.setString(3, Util.getUserStateCode());
                        pstmVT_VCH_OFFENCES.setInt(4, Integer.parseInt(dobj1.getOffenceCd()));
                        pstmVT_VCH_OFFENCES.executeUpdate();
                    }
                    if (refered.equals("Y")) {
                        pstmVT_VCH_OFFENCES = null;
                        String SqlUpdate1 = "Update " + TableList.VT_VCH_OFFENCES + " set authority_decision=? ,auth_decision_date=?where appl_no=? and state_cd=? and offence_cd=?";
                        pstmVT_VCH_OFFENCES = tmgr.prepareStatement(SqlUpdate1);
                        for (OffenceReferDetailsDobj dobj1 : authList) {
                            pstmVT_VCH_OFFENCES.setString(1, dobj1.getAuthority_decision());
                            pstmVT_VCH_OFFENCES.setTimestamp(2, getTimeStamp(dobj1.getDecision_date().toString()));
                            pstmVT_VCH_OFFENCES.setString(3, dobjSettlement.getApplicationNo());
                            pstmVT_VCH_OFFENCES.setString(4, Util.getUserStateCode());
                            pstmVT_VCH_OFFENCES.setInt(5, Integer.parseInt(dobj1.getOffenceCode()));
                            pstmVT_VCH_OFFENCES.executeUpdate();
                        }
                    }

                }
            }
            statusDobj = ServerUtil.webServiceForNextStage(statusDobj, tmgr);

            if (Integer.parseInt(roleCd) == TableConstants.VM_ROLE_ENFORCEMENT_SETTLMENT_APPROVE) {
                if (dobjSettlement.getCourtDecision() != null && dobjSettlement.getCourtRecieptNo() != null
                        && !dobjSettlement.getCourtDecision().equals("") && !dobjSettlement.getCourtRecieptNo().equals("")
                        && taxDetailList.isEmpty()) {
                    statusDobj.setAction_cd(TableConstants.VM_ROLE_ENFORCEMENT_DISPOSE_APPROVE);
                    statusDobj.setFlow_slno(7);
                }

                if (dobjSettlement.getCourtDecision() != null && dobjSettlement.getCourtRecieptNo() != null && !authList.isEmpty()
                        && !dobjSettlement.getCourtDecision().equals("") && !dobjSettlement.getCourtRecieptNo().equals("")
                        && taxDetailList.isEmpty()) {
                    statusDobj.setAction_cd(TableConstants.VM_ROLE_ENFORCEMENT_DISPOSE_APPROVE);
                    statusDobj.setFlow_slno(7);
                }


            }
            ServerUtil.fileFlow(tmgr, statusDobj);
            tmgr.commit();
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
    }

    public void reback(String roleCd, Status_dobj statusDobj, String changedDataContents, CompoundingInOfficeDobj dobjSettlement, List<TaxDetailsDobj> taxDetailList, boolean isFeePaidAtCOurt) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement pstmtCHALLANTAX = null;
        PreparedStatement pstmtCHALLANFEE = null;
        PreparedStatement pstmtCHKCHALLANTAX = null;
        boolean checkReferTOCourtFlag = false;
        try {
            tmgr = new TransactionManager("movetoapprove");
            if (!changedDataContents.isEmpty()) {
                checkReferTOCourtFlag = getStatusOfReferToCourt(tmgr, dobjSettlement.getApplicationNo(), dobjSettlement, isFeePaidAtCOurt, roleCd);
                if (checkReferTOCourtFlag == isFeePaidAtCOurt) {
                    String chkChallanTax = "SELECT * FROM  " + TableList.VT_CHALLAN_TAX + "   WHERE appl_no=?  ";
                    pstmtCHKCHALLANTAX = tmgr.prepareStatement(chkChallanTax);
                    pstmtCHKCHALLANTAX.setString(1, dobjSettlement.getApplicationNo());
                    if (taxDetailList.size() > 0) {
                        ResultSet rs = pstmtCHKCHALLANTAX.executeQuery();
                        if (rs.next()) {
                            String challanTaxSql = "UPDATE    " + TableList.VT_CHALLAN_TAX + " "
                                    + " SET chal_tax=?, chal_penalty=?, "
                                    + " chal_interest=? "
                                    + " WHERE appl_no=?";
                            pstmtCHALLANTAX = tmgr.prepareStatement(challanTaxSql);
                            pstmtCHALLANTAX.setInt(1, Integer.parseInt(dobjSettlement.getSettleChalTax()));
                            pstmtCHALLANTAX.setInt(2, Integer.parseInt(dobjSettlement.getSettleChalPenalty()));
                            pstmtCHALLANTAX.setInt(3, Integer.parseInt(dobjSettlement.getSettleChalInterest()));
                            pstmtCHALLANTAX.setString(4, dobjSettlement.getApplicationNo());
                            pstmtCHALLANTAX.execute();

                        }
                    } else {
                    }
                    try {
                        String challanFeeSql = "UPDATE   " + TableList.VT_CHALLAN_AMT + " SET settlement_amt=? ,cmpd_amt=? WHERE appl_no=? ";
                        pstmtCHALLANFEE = tmgr.prepareStatement(challanFeeSql);
                        pstmtCHALLANFEE.setInt(1, Integer.parseInt(dobjSettlement.getSettleFee()));
                        pstmtCHALLANFEE.setInt(2, Integer.parseInt(dobjSettlement.getCompoundFee()));
                        pstmtCHALLANFEE.setString(3, dobjSettlement.getApplicationNo());

                        pstmtCHALLANFEE.executeUpdate();

                    } catch (SQLException e) {
                        LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    }
                }
                if ((isFeePaidAtCOurt == true) && (checkReferTOCourtFlag == false)) {
                    JSFUtils.setFacesMessage("Challan Not Refer TO Court", "message", JSFUtils.WARN);
                    return;
                }
                if ((isFeePaidAtCOurt == false) && (checkReferTOCourtFlag == true)) {
                    JSFUtils.setFacesMessage("Challan Refered To  court. Please Provide The Court Details", "message", JSFUtils.WARN);
                    return;
                }

            }
            statusDobj = ServerUtil.webServiceForNextStage(statusDobj, tmgr);
            ServerUtil.fileFlow(tmgr, statusDobj);
            tmgr.commit();
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

    }

    public Map<String, Object> getAccusedList(String appl_no) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        Map<String, Object> accused = new LinkedHashMap<String, Object>();
        accused.put("Select", "-1");
        try {
            tmgr = new TransactionManager("getSectionDesc");
            String sqlAccused = "SELECT acc.code,acc.descr FROM " + TableList.VT_CHALLAN_ACCUSED + " accused\n"
                    + "JOIN " + TableList.VM_ACCUSED + " acc on accused.accused_catg=acc.code\n"
                    + "where accused.appl_no=?";
            ps = tmgr.prepareStatement(sqlAccused);
            ps.setString(1, appl_no);
            RowSet rowSet = tmgr.fetchDetachedRowSet();
            while (rowSet.next()) {
                accused.put(rowSet.getString("descr"), rowSet.getString("code"));
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
            }
        }
        return accused;
    }

    public Map<String, Object> getOffenceList(String vhClass) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        Map<String, Object> offence = new LinkedHashMap<String, Object>();
        offence.put("Select", "-1");
        try {
            tmgr = new TransactionManager("getOffenceDesc");
            String sql = "select offpenalty.offence_cd,offence.offence_desc\n"
                    + " from " + TableList.VM_OFFENCE_PENALTY + " offpenalty \n"
                    + " INNER JOIN  " + TableList.VM_OFFENCES + " offence ON offence.offence_cd= offpenalty.offence_cd \n"
                    + " Where vh_class=? and offpenalty.state_cd in =?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(vhClass));
            ps.setString(2, Util.getUserStateCode());
            RowSet rowSet = tmgr.fetchDetachedRowSet();
            while (rowSet.next()) {
                offence.put(rowSet.getString("offence_desc"), rowSet.getString("offence_cd"));
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
            }
        }
        return offence;
    }

    public List getOffenceAccusedWise(String vhClass, String accused) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement pstmtgetOffenceList = null;
        List offencelist = new ArrayList();
        SelectItem item = new SelectItem("-1", "Select Offence ");
        offencelist.add(item);
        try {
            String sqlSelectOffence = "select offpenalty.offence_cd,offence.offence_desc\n"
                    + " from echallan.vm_offence_penalty offpenalty \n"
                    + " INNER JOIN  echallan.vm_offences offence ON offence.offence_cd= offpenalty.offence_cd and offence.state_cd= offpenalty.state_cd \n"
                    + " Where vh_class=? and offpenalty.state_cd =? and offence_applied_on LIKE ('%" + accused + "%')\n";

            tmgr = new TransactionManager("SaveChallanDAO: getAddedDoc");
            pstmtgetOffenceList = tmgr.prepareStatement(sqlSelectOffence);
            pstmtgetOffenceList.setInt(1, Integer.parseInt(vhClass));
            pstmtgetOffenceList.setString(2, Util.getUserStateCode());
            RowSet resulSet = tmgr.fetchDetachedRowSet();
            while (resulSet.next()) {
                item = new SelectItem(resulSet.getString("offence_cd"), resulSet.getString("offence_desc"));
                offencelist.add(item);
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
            }
        }
        return offencelist;
    }

    public List<VehcleOffenceDobj> getPrviousvehOffence(CompoundingInOfficeDobj dobj) throws VahanException {
        List offenceListPrev = new ArrayList();
        int offenceCd = 0;
        String offenceDescr = "";
        VehcleOffenceDobj dobjoffencePrev = null;
        TransactionManager tmgr = null;
        PreparedStatement pstmtgetVehOffence = null;
        int slNo = 0;

        try {

            String offenceSql = "SELECT OFFENCE.appl_no, OFFENCE.offence_cd, OFFENCE.accused_catg, OFFENCE.offence_amt,  \n"
                    + "       OFFMAST.OFFENCE_DESC ,accused.descr AS AccusedDescR,OFFMAST.MVA_CLAUSE,OFFENCE.moved_on,OFFENCE.moved_by\n"
                    + "FROM   " + TableList.VT_VCH_OFFENCES_HIST + " OFFENCE\n"
                    + "LEFT OUTER JOIN  " + TableList.VM_OFFENCES + "  OFFMAST ON OFFENCE.OFFENCE_CD  = OFFMAST.OFFENCE_CD  AND OFFENCE.STATE_CD in('AI',?) and OFFMAST.state_cd=OFFENCE.state_cd \n"
                    + "LEFT OUTER JOIN  " + TableList.VM_ACCUSED + "  accused ON accused.code  =OFFENCE.accused_catg  \n"
                    + " WHERE appl_no=? AND OFFENCE.STATE_CD=? ";

            tmgr = new TransactionManager("SettleChallanDAO : getvehOffence()");
            pstmtgetVehOffence = tmgr.prepareStatement(offenceSql);
            pstmtgetVehOffence.setString(1, Util.getUserStateCode());
            pstmtgetVehOffence.setString(2, dobj.getApplicationNo());
            pstmtgetVehOffence.setString(3, Util.getUserStateCode());
            ResultSet rs = pstmtgetVehOffence.executeQuery();

            while (rs.next()) {
                slNo++;
                offenceCd = Integer.parseInt(rs.getString("offence_cd"));
                if (offenceCd == 999) {
                    offenceDescr = "Miscellaneous Fee";
                } else {
                    offenceDescr = rs.getString("offence_desc");
                }
                if (rs.getString("accused_catg").equals("O")) {
                    dobjoffencePrev = new VehcleOffenceDobj(String.valueOf(slNo), rs.getString("accused_catg"), rs.getString("AccusedDescR"), rs.getString("offence_cd"),
                            rs.getString("MVA_CLAUSE"), rs.getString("offence_amt"), offenceDescr, rs.getString("moved_by"), rs.getString("moved_on"));
                }

                if (rs.getString("accused_catg").equals("D")) {
                    dobjoffencePrev = new VehcleOffenceDobj(String.valueOf(slNo), rs.getString("accused_catg"), rs.getString("AccusedDescR"), rs.getString("offence_cd"),
                            rs.getString("MVA_CLAUSE"), rs.getString("offence_amt"), offenceDescr, rs.getString("moved_by"), rs.getString("moved_on"));
                }
                if (rs.getString("accused_catg").equals("C")) {
                    dobjoffencePrev = new VehcleOffenceDobj(String.valueOf(slNo), rs.getString("accused_catg"), rs.getString("AccusedDescR"), rs.getString("offence_cd"),
                            rs.getString("MVA_CLAUSE"), rs.getString("offence_amt"), offenceDescr, rs.getString("moved_by"), rs.getString("moved_on"));
                }
                offenceListPrev.add(dobjoffencePrev);

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
            }
        }
        return offenceListPrev;
    }

    public void deleteAndUpdate(String appl_no, VehcleOffenceDobj dobj) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement pstm = null;
        String sql = "";
        try {

            sql = "insert into  " + TableList.VT_VCH_OFFENCES_HIST + " select appl_no, offence_cd, accused_catg, offence_amt, remarks, da_status, \n"
                    + "       offence_status, state_cd, off_cd, current_timestamp , '" + Util.getEmpCode() + "' from " + TableList.VT_VCH_OFFENCES + " where appl_no=? and state_cd=? and accused_catg=? and offence_cd=?";
            tmgr = new TransactionManager("deleteAndUpdate");
            pstm = tmgr.prepareStatement(sql);
            pstm.setString(1, appl_no);
            pstm.setString(2, Util.getUserStateCode());
            pstm.setString(3, dobj.getAccuCatg());
            pstm.setInt(4, Integer.parseInt(dobj.getOffenceCd()));
            pstm.execute();
            sql = "delete from " + TableList.VT_VCH_OFFENCES + " where appl_no=? and state_cd=? and accused_catg=? and offence_cd=? ";
            pstm = null;
            pstm = tmgr.prepareStatement(sql);
            pstm.setString(1, appl_no);
            pstm.setString(2, Util.getUserStateCode());
            pstm.setString(3, dobj.getAccuCatg());
            pstm.setInt(4, Integer.parseInt(dobj.getOffenceCd()));
            pstm.executeUpdate();
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

    public boolean saveOffenceDetails(String appl_no, List<OffencesDobj> offenceDetails) throws VahanException {
        boolean flag = false;
        PreparedStatement pstm = null;
        String sql = "";
        TransactionManager tmgr = null;
        String penalty = "";
        try {
            sql = "INSERT INTO " + TableList.VT_VCH_OFFENCES + " ( appl_no, offence_cd, accused_catg, offence_amt,"
                    + " section_cd, state_cd, off_cd)VALUES (?, ?, ?, ?, ?, ?,?)";
            tmgr = new TransactionManager("saveOffenceDetails");
            pstm = tmgr.prepareStatement(sql);
            for (OffencesDobj dobj : offenceDetails) {
                if (dobj.getAccuseInOffDetails().equals("O")) {
                    penalty = dobj.getPenalty();
                }
                if (dobj.getAccuseInOffDetails().equals("C")) {
                    penalty = dobj.getPenalty();
                }
                if (dobj.getAccuseInOffDetails().equals("D")) {
                    penalty = dobj.getPenalty();
                }
                if (dobj.getAccuseInOffDetails().equals("N")) {
                    penalty = dobj.getPenalty();
                }
                pstm.setString(1, appl_no);
                pstm.setInt(2, Integer.parseInt(dobj.getOffenceCode()));
                pstm.setString(3, dobj.getAccuseInOffDetails());
                pstm.setInt(4, Integer.parseInt(penalty));
                pstm.setInt(5, Integer.parseInt(dobj.getOffenceCode()));
                pstm.setString(6, Util.getUserStateCode());
                pstm.setInt(7, Util.getSelectedSeat().getOff_cd());
                pstm.executeUpdate();
                tmgr.commit();
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
            }
        }

        return flag;
    }

    public static Timestamp getTimeStamp(String Date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yy");
        Timestamp st = null;
        try {
            java.util.Date date = sdf.parse(Date);
            st = new Timestamp(date.getTime());
        } catch (ParseException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return st;
    }

    public Map<String, Object> getAuthortyOffence(String appl_no) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement pstmtUserCode = null;
        Map<String, Object> offence = new LinkedHashMap<>();
        offence.put("Select", "-1");
        try {
            String sqlPendigCases = "select offences.offence_cd,offences.offence_desc  from " + TableList.VT_VCH_OFFENCES
                    + " vch_offences\n"
                    + "join " + TableList.VM_OFFENCES + " offences on vch_offences.offence_cd=offences.offence_cd and vch_offences.state_cd=offences.state_cd\n"
                    + "where vch_offences.appl_no=? and vch_offences.state_cd=? ";
            tmgr = new TransactionManager("getUserCode");
            pstmtUserCode = tmgr.prepareStatement(sqlPendigCases);
            pstmtUserCode.setString(1, appl_no);
            pstmtUserCode.setString(2, Util.getUserStateCode());
            ResultSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                offence.put(rs.getString("offence_desc"), rs.getInt("offence_cd"));
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
            }
        }
        return offence;
    }

    public Map<String, Object> getAuthorty() throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement pstmtUserCode = null;
        Map<String, Object> authorty = new LinkedHashMap<>();
        authorty.put("Select", "-1");
        try {
            String sqlPendigCases = "SELECT code, fulldescr "
                    + "  FROM echallan.vm_authorities where state_cd=? order by descr";

            tmgr = new TransactionManager("getUserCode");
            pstmtUserCode = tmgr.prepareStatement(sqlPendigCases);
            pstmtUserCode.setString(1, Util.getUserStateCode());
            ResultSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                authorty.put(rs.getString("fulldescr"), rs.getInt("code"));
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
            }
        }
        return authorty;
    }

    public OffenceReferDetailsDobj getAuthortyDetails(String appl_no, String offence_cd) throws VahanException {
        OffenceReferDetailsDobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement pstm = null;
        try {
            tmgr = new TransactionManager("getAuthortyDetails");
            String sql = "select offences.descr,offences.code,vtOff.refer_date,vtOff.offence_cd,vmOff.mva_clause,vtOff.offence_amt from echallan.vt_vch_offences vtOff\n"
                    + "LEFT OUTER JOIN echallan.vm_offences vmOff on vmOff.offence_cd=vtOff.offence_cd and vtOff.state_cd =?\n"
                    + "join echallan.vm_authorities offences on vtOff.refered_authority=offences.code and vtOff.state_cd =?\n"
                    + "where vtOff.appl_no=? and  vtOff.offence_cd=? and vtOff.state_cd=?";
            pstm = tmgr.prepareStatement(sql);
            dobj = new OffenceReferDetailsDobj();
            pstm.setString(1, Util.getUserStateCode());
            pstm.setString(2, Util.getUserStateCode());
            pstm.setString(3, appl_no);
            pstm.setInt(4, Integer.parseInt(offence_cd));
            pstm.setString(5, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj.setAuthoritydescr(rs.getString("descr"));
                dobj.setPenalty(rs.getString("offence_amt"));
                dobj.setReferDate(rs.getDate("refer_date"));
                dobj.setAuthorityCode(rs.getString("code"));
                dobj.setSectionDescr(rs.getString("mva_clause"));
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
            }
        }
        return dobj;
    }

    public boolean checkAuthortyCase(String appl_no) throws VahanException {
        boolean flag = false;
        TransactionManager tmgr = null;
        PreparedStatement pstm = null;
        try {
            tmgr = new TransactionManager("getAuthortyDetails");
            String sql = " Select * from " + TableList.VT_VCH_OFFENCES + " where appl_no=? and offence_refer='Y' and state_cd=?";
            pstm = tmgr.prepareStatement(sql);
            pstm.setString(1, appl_no);
            pstm.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
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
            }
        }
        return flag;
    }

    public List<OffenceReferDetailsDobj> getAuthortyDetailsList(String appl_no) throws VahanException {
        OffenceReferDetailsDobj dobj = null;
        List authList = new ArrayList();
        TransactionManager tmgr = null;
        PreparedStatement pstm = null;
        try {
            tmgr = new TransactionManager("getAuthortyDetails");
            String sql = "select auth.descr,auth.code,vtOff.refer_date,vtOff.authority_decision,vtOff.auth_decision_date,vtOff.offence_cd,vmOff.offence_cd,vmOff.offence_desc,vmOff.mva_clause,vtOff.offence_amt from echallan.vt_vch_offences vtOff\n"
                    + "LEFT OUTER JOIN echallan.vm_offences vmOff on vmOff.offence_cd=vtOff.offence_cd and vtOff.state_cd =vmOff.state_cd\n"
                    + "join echallan.vm_authorities auth on vtOff.refered_authority=auth.code and vtOff.state_cd =auth.state_cd\n"
                    + "where vtOff.appl_no=? and vtOff.state_cd=? and (vtOff.authority_decision is not NULL or vtOff.authority_decision!='')";
            pstm = tmgr.prepareStatement(sql);
            pstm.setString(1, appl_no);
            pstm.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new OffenceReferDetailsDobj();
                dobj.setAuthoritydescr(rs.getString("descr"));
                dobj.setAuthorityCode(rs.getString("code"));
                dobj.setOffenceCode(rs.getString("offence_cd"));
                dobj.setOffenceDescr(rs.getString("offence_desc"));
                dobj.setPenalty(rs.getString("offence_amt"));
                dobj.setReferDate(rs.getDate("refer_date"));
                dobj.setAuthority_decision(rs.getString("authority_decision"));
                dobj.setDecision_date(rs.getDate("auth_decision_date"));
                dobj.setSectionDescr(rs.getString("mva_clause"));
                authList.add(dobj);
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
            }
        }
        return authList;
    }

    public List<OffenceReferDetailsDobj> getReferAuthorty(String appl_no) throws VahanException {
        OffenceReferDetailsDobj dobj = null;
        List authList = new ArrayList();
        TransactionManager tmgr = null;
        PreparedStatement pstm = null;
        try {
            tmgr = new TransactionManager("getAuthortyDetails");
            String sql = "select auth.descr from echallan.vt_vch_offences vtOff\n"
                    + "join echallan.vm_authorities auth on vtOff.refered_authority=auth.code and vtOff.state_cd =?\n"
                    + "where vtOff.appl_no=? and vtOff.state_cd=?";
            pstm = tmgr.prepareStatement(sql);
            pstm.setString(1, Util.getUserStateCode());
            pstm.setString(2, appl_no);
            pstm.setString(3, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new OffenceReferDetailsDobj();
                dobj.setAuthoritydescr(rs.getString("descr"));
                dobj.setAuthorityCode(rs.getString("descr"));
                authList.add(dobj);
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
            }
        }
        return authList;
    }

    //function to hold the challan
    public boolean holdAndActiveChallan(ChallanHoldDobj dobj, String applNo) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        String status = "";
        boolean flag = false;
        try {
            tmgr = new TransactionManager("holdAndActiveChallan");
            if (dobj.getChallanHoldStatus().equals("ACTIVE")) {
                status = "A";
            }
            if (dobj.getChallanHoldStatus().equals("HOLD")) {
                status = "H";
            }
            String sql = "UPDATE " + TableList.VT_CHALLAN_HOLD
                    + "   SET hold_status=?, hold_reasons=? \n"
                    + " WHERE appl_no=? and state_cd=?";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setString(2, dobj.getHoldReason());
            pstmt.setString(3, applNo);
            pstmt.setString(4, Util.getUserStateCode());
            int val = pstmt.executeUpdate();
            if (val > 0) {
                tmgr.commit();
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
            }
        }
        return flag;
    }

    public String getHoldChallanStatus(String appl_no) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String status = "";
        try {
            tmgr = new TransactionManager("IsBookNoEnable");
            String sql = " Select * from  " + TableList.VT_CHALLAN_HOLD + " where appl_no=? and  state_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                status = rs.getString("hold_status");
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
            }
        }
        return status;
    }
    //

    public boolean isChallanHold(String appl_no) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        boolean flag = false;
        try {
            tmgr = new TransactionManager("IsBookNoEnable");
            String sql = " Select * from  " + TableList.VT_CHALLAN_HOLD + " where appl_no=? and  state_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, Util.getUserStateCode());
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
            }
        }
        return flag;
    }
}
