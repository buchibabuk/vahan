package nic.vahan.form.impl.eChallan;

import nic.vahan.form.dobj.eChallan.CompoundingFeeDobj;
import nic.vahan.form.dobj.eChallan.DocTableDobj;
import nic.vahan.form.dobj.eChallan.OffencesDobj;
import nic.vahan.form.dobj.eChallan.SaveChallanDobj;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sql.RowSet;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.server.CommonUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.server.ServerUtil;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.eChallan.CompoundingInOfficeDobj;
import nic.vahan.form.dobj.eChallan.DisposeChallanDobj;
import nic.vahan.form.dobj.eChallan.VehcleOffenceDobj;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

public class DisposalOfChallanImpl implements Serializable {

    private static Logger LOGGER = Logger.getLogger(DisposalOfChallanImpl.class);

    public SaveChallanDobj getVehicleDetails(String applicationNO, String challan_no, String actionCode) throws VahanException {
        PreparedStatement pstm = null;
        TransactionManagerReadOnly tmgr = null;
        ResultSet rs = null;
        SaveChallanDobj dobj = null;
        String stateCD = null;
        int rtoCD;
        int courtCD;
        String whereCondition = "";
        String parameter = "";
        if (Integer.parseInt(actionCode) == TableConstants.VM_ROLE_ENFORCEMENT_DISPOSE_APPROVE) {
            whereCondition = "challan.appl_no=?";
            parameter = applicationNO;
        }
        if (Integer.parseInt(actionCode) == TableConstants.VM_ROLE_ENFORCEMENT_DISPOSE_ENTRY) {
            whereCondition = "challan.chal_no=?";
            parameter = challan_no;
        }
        String sqlChallandetails = "SELECT challan.appl_no, challan.regn_no,challan.chal_date, challan.chal_time, challan.chal_place,challan.comming_from,challan.going_to,"
                + " challan.is_doc_impound, challan.is_vch_impdound, challan.chal_officer, challan.nc_c_ref_no, challan.state_cd, challan.off_cd,"
                + " ownerr.regn_no,ownerr.chasi_no, ownerr.vh_class, ownerr.off_cd, ownerr.state_cd, ownerr.owner_name, ownerr.seat_cap,"
                + " ownerr.stand_cap, ownerr.sleeper_cap, ownerr.ld_wt, ownerr.color, ownerr.fuel ,"
                + " vehImpound.impound_place, vehImpound.contact_off, vehImpound.impound_dt, vehImpound.seizure_no,"
                + " refCourt.court_cd, hearing_date , court.magistrate_cd ,vh_chal.disposal_remark, to_char(vh_chal.op_dt,'dd-Mon-yyyy') as dispose_date "
                + " FROM " + TableList.VT_CHALLAN + " challan"
                + " LEFT OUTER JOIN " + TableList.VIEW_VV_OWNER + "  ownerr ON challan.regn_no=ownerr.regn_no"
                + " LEFT OUTER JOIN " + TableList.VT_VEHICLE_IMPOUND + " vehImpound ON challan.appl_no=vehImpound.appl_no"
                + " LEFT OUTER JOIN " + TableList.VT_CHALLAN_REFER_TO_COURT + "  refCourt ON challan.appl_no=refCourt.appl_no"
                + " LEFT OUTER JOIN " + TableList.VM_COURT + "  court ON court.court_cd=refCourt.court_cd "
                + " LEFT OUTER JOIN " + TableList.VH_CHALLAN + " vh_chal ON challan.appl_no=vh_chal.appl_no "
                + " WHERE  " + whereCondition + " ";
        try {
            tmgr = new TransactionManagerReadOnly("DAODisposalOfChallan.getVehicleDetails()");
            pstm = tmgr.prepareStatement(sqlChallandetails);
            pstm.setString(1, parameter);
            rs = pstm.executeQuery();
            if (rs.next()) {
                dobj = new SaveChallanDobj();
                rtoCD = rs.getInt("off_cd");
                stateCD = rs.getString("state_cd");
                // courtCD = rs.getInt("court_cd");
                String officerNo = rs.getString("chal_officer");
                dobj.setApplicationNO(rs.getString("appl_no"));
                dobj.setState(getStateDescr(stateCD));
                dobj.setRto(getRtoDescr(rtoCD + "", stateCD));
                dobj.setRtoCode(rtoCD + "");
                dobj.setStateCode(stateCD + "");
                dobj.setDocImpnd(rs.getString("is_doc_impound"));
                dobj.setVehImpnd(rs.getString("is_vch_impdound"));
                dobj.setNCCRNo(rs.getString("nc_c_ref_no"));
                dobj.setChallanTime(rs.getString("chal_time"));
                dobj.setDate(rs.getString("chal_date"));
                dobj.setPlaceCh(rs.getString("chal_place"));
                dobj.setCommingFrom(rs.getString("comming_from"));
                dobj.setGoingTo(rs.getString("going_to"));
                if (null != officerNo && !CommonUtils.isNullOrBlank(officerNo)) {
                    dobj.setChalOff(getOfficerName(officerNo));
                    dobj.setChalOffCode(officerNo);
                }
                dobj.setHeaaringDt(rs.getDate("hearing_date"));
                //dobj.setCourtCd(courtCD + "");
                dobj.setCourtName(rs.getString("court_cd"));
                dobj.setMagistrate(rs.getString("magistrate_cd"));
                String vehClass = rs.getString("vh_class");
                String fuel = rs.getString("fuel");
                dobj.setVehicleNo(rs.getString("regn_no"));
                dobj.setOwnerName(rs.getString("owner_name"));
                dobj.setChasiNo(rs.getString("chasi_no"));
                dobj.setSeatCap(rs.getString("seat_cap"));
                dobj.setStandCap(rs.getString("stand_cap"));
                dobj.setSleeperCap(rs.getString("sleeper_cap"));
                dobj.setLadenWt(rs.getString("ld_wt"));
                dobj.setColor(rs.getString("color"));
                if (null != fuel && !CommonUtils.isNullOrBlank(fuel)) {
                    dobj.setFuel(getFuelDescr(fuel));
                }
                if (null != vehClass && !CommonUtils.isNullOrBlank(vehClass)) {
                    dobj.setVhClassCode(vehClass);
                    dobj.setVhClass(getVehClassDescr(vehClass));
                }
                dobj.setImpndPlace(rs.getString("impound_place"));
                dobj.setContactOfficer(rs.getString("contact_off"));
                dobj.setImpndDate(rs.getString("impound_dt"));
                dobj.setSezNo(rs.getString("seizure_no"));
                dobj.setDisposalChallanDate(rs.getString("dispose_date"));
                dobj.setDisposalChallanRemarks(rs.getString("disposal_remark"));
            }
        } catch (VahanException e) {
            throw e;
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
        return dobj;
    }

    public List<DocTableDobj> getDocDetails(String applicationNo) throws VahanException {
        List<DocTableDobj> dobj = new ArrayList<DocTableDobj>();
        PreparedStatement pstm = null;
        TransactionManagerReadOnly tmgr = null;
        ResultSet rs = null;
        String sqlDocImp = "SELECT doc.DOC_CD,doc.VALIDITY,doc.DOC_NO,doc.ISS_AUTH, docu.descr,doc.other_doc_name\n"
                + "FROM " + TableList.VT_DOCS_IMPOUND + " doc \n"
                + "LEFT OUTER JOIN " + TableList.VM_DOCUMENTS + " docu on doc.DOC_CD  =docu.code \n"
                + "WHERE  appl_no=? and doc.state_cd=?";
        try {
            tmgr = new TransactionManagerReadOnly("DAODisposalOfChallan.getDocDetails()");
            pstm = tmgr.prepareStatement(sqlDocImp);

            pstm.setString(1, applicationNo);
            pstm.setString(2, Util.getUserStateCode());
            rs = pstm.executeQuery();
            while (rs.next()) {
                DocTableDobj docDobj = new DocTableDobj();
                String docCd = rs.getString("DOC_CD");
                docDobj.setDocumentDesc(rs.getString("descr"));
                docDobj.setDocNo(rs.getString("DOC_NO"));
                docDobj.setIssueAuth(rs.getString("ISS_AUTH"));
                docDobj.setValidity(rs.getString("VALIDITY"));
                docDobj.setDocName(rs.getString("other_doc_name"));
                dobj.add(docDobj);
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

    public List<CompoundingFeeDobj> getRcptDetails(String vehNo, String applicationNo) throws VahanException {
        List<CompoundingFeeDobj> list_rcpt_details = new ArrayList<>();
        PreparedStatement pstm = null;
        TransactionManagerReadOnly tmgr = null;
        ResultSet rs = null;
        int purcd;
        String sqlTaxDtls = "select vtchaltax.cmpd_rcpt_no,to_char(vtchaltax.cmpd_rcpt_dt,'dd-MON-yyyy') as rcpt_dt,taxbreakup.rcpt_no,\n"
                + "taxbreakup.tax_from,taxbreakup.tax_upto,(taxbreakup.tax+taxbreakup.penalty+taxbreakup.interest)totalTax ,taxbreakup.pur_cd\n"
                + " from " + TableList.VT_CHALLAN_TAX + " vtchaltax \n"
                + " Inner JOIN  " + TableList.VT_TAX_BREAKUP + " taxbreakup on vtchaltax.cmpd_rcpt_no=taxbreakup.rcpt_no and vtchaltax.pur_cd=taxbreakup.pur_cd  and vtchaltax.state_cd=taxbreakup.state_cd\n"
                + "where appl_no=? and vtchaltax.state_cd=? and vtchaltax.cmpd_rcpt_no !='' ";

        String sqlAccount = "SELECT vtchalamt.cmpd_rcpt_no ,to_char(vtFee.rcpt_dt,'dd-MON-yyyy') as rcpt_date ,vtFee.*\n"
                + "FROM   " + TableList.VT_CHALLAN_AMT + " vtchalamt \n"
                + "LEFT OUTER JOIN  " + TableList.VT_FEE + "  vtFee on ( vtchalamt.cmpd_rcpt_no= vtFee.rcpt_no or vtchalamt.adv_rcpt_no=vtFee.rcpt_no) and vtchalamt.state_cd= vtFee.state_cd\n"
                + " WHERE appl_no=? and regn_no =? and  vtchalamt.state_cd=? ";

        String sqlChallanReferTOCourt = "select *,to_char(court_rcpt_date,'dd-MON-yyyy') as court_rcpt_date from  " + TableList.VT_CHALLAN_REFER_TO_COURT + "  where appl_no=?";

        try {
            tmgr = new TransactionManagerReadOnly("DAODisposalOfChallan.getRcptDetails()");
            pstm = tmgr.prepareStatement(sqlTaxDtls);
            pstm.setString(1, applicationNo);
            pstm.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                CompoundingFeeDobj doTax = new CompoundingFeeDobj();
                purcd = rs.getInt("PUR_CD");
                if (purcd == 58) {
                    doTax.setFeetaxDesc("Road Tax");
                } else if (purcd == 59) {
                    doTax.setFeetaxDesc("Additional Tax");
                }
                doTax.setRecieptNo(rs.getString("rcpt_no"));
                doTax.setRecieptDate(rs.getString("rcpt_dt"));
                doTax.setFromDt((rs.getString("tax_from")));
                doTax.setUptoDt((rs.getString("tax_upto")));
                doTax.setAmount(rs.getString("totalTax"));
                list_rcpt_details.add(doTax);
            }
            rs = null;
            pstm = null;
            pstm = tmgr.prepareStatement(sqlAccount);
            pstm.setString(1, applicationNo);
            pstm.setString(2, vehNo);
            pstm.setString(3, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                CompoundingFeeDobj doAccount = new CompoundingFeeDobj();
                purcd = rs.getInt("PUR_CD");
                if (purcd == 999) {
                    doAccount.setFeetaxDesc("Compounding Fee");
                } else if (purcd == 111) {
                    doAccount.setFeetaxDesc("Compounding Fee");
                } else {
                    doAccount.setFeetaxDesc("");
                }
                doAccount.setRecieptNo(rs.getString("rcpt_no"));
                doAccount.setRecieptDate(rs.getString("rcpt_date"));
                doAccount.setFromDt("");
                doAccount.setUptoDt("");
                doAccount.setAmount(rs.getString("fees"));
                list_rcpt_details.add(doAccount);
            }
            pstm = null;
            rs = null;
            pstm = tmgr.prepareStatement(sqlChallanReferTOCourt);
            pstm.setString(1, applicationNo);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                CompoundingFeeDobj doAccount = new CompoundingFeeDobj();
                doAccount.setFeetaxDesc("Paid At Court");
                doAccount.setRecieptNo(rs.getString("court_rcpt_no"));
                doAccount.setRecieptDate(rs.getString("court_rcpt_date"));
                doAccount.setFromDt("");
                doAccount.setUptoDt("");
                doAccount.setAmount(rs.getString("court_paid_amount"));
                list_rcpt_details.add(doAccount);
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
        return list_rcpt_details;
    }

    public List<OffencesDobj> getOffenceDetailsOnVehicle(String vhClass, String applicationNo) throws VahanException {
        List<OffencesDobj> dobj = new ArrayList<OffencesDobj>();
        PreparedStatement pstm = null;
        TransactionManager tmgr = null;
        ResultSet rs = null;
        String sqlVehOffence = "SELECT * FROM " + TableList.VT_VCH_OFFENCES + "  WHERE appl_no=? and state_cd=? ";
        try {
            tmgr = new TransactionManager("DAODisposalOfChallan.getOffenceDetailsOnVehicle()");
            pstm = tmgr.prepareStatement(sqlVehOffence);
            pstm.setString(1, applicationNo);
            pstm.setString(2, Util.getUserStateCode());
            rs = pstm.executeQuery();
            while (rs.next()) {
                String offcCode = rs.getString("offence_cd");
                String acctg = rs.getString("accused_catg");
                OffencesDobj dOOffences = getOffenceDtlsBasedOnOffcCD(acctg, offcCode, vhClass, applicationNo, tmgr);
                if (dOOffences != null) {
                    dobj.add(dOOffences);
                }
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

    public OffencesDobj getOffenceDtlsBasedOnOffcCD(String acctg, String offcCode, String vehClass, String applicationNo, TransactionManager tmgr) throws VahanException {
        OffencesDobj dOOffences = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sqlOffcPenalty = "SELECT * FROM " + TableList.VM_OFFENCE_PENALTY + "  WHERE offence_cd=? AND vh_class=? and state_cd=?";
        try {
            pstm = tmgr.prepareStatement(sqlOffcPenalty);
            pstm.setInt(1, Integer.parseInt(offcCode));
            pstm.setInt(2, Integer.parseInt(vehClass));
            pstm.setString(3, Util.getUserStateCode());
            rs = pstm.executeQuery();
            if (rs.next()) {
                dOOffences = new OffencesDobj();
                String sectioCode = rs.getString("section_cd");
                dOOffences.setOffenceCode(offcCode);
                dOOffences.setOffenceDescr(getOffenceName(Integer.parseInt(offcCode)));
                if (!CommonUtils.isNullOrBlank(sectioCode)) {
                    dOOffences.setSectionName(getSectionName(Integer.parseInt(sectioCode)));
                }
                double dblPenaltyAmt = rs.getDouble("O_PENALTY");
                if (dblPenaltyAmt > 0) {
                    dOOffences.setPenalty(getFinalPenaltyAmount(applicationNo, Integer.parseInt(vehClass), Integer.parseInt(offcCode), dblPenaltyAmt, tmgr) + "");
                } else {
                    dOOffences.setPenalty("0");
                }
                dblPenaltyAmt = rs.getDouble("C_PENALTY");
                if (dblPenaltyAmt > 0) {
                    dOOffences.setPenalty(getFinalPenaltyAmount(applicationNo, Integer.parseInt(vehClass), Integer.parseInt(offcCode), dblPenaltyAmt, tmgr) + "");
                } else {
                    dOOffences.setPenalty("");
                }
                dblPenaltyAmt = rs.getDouble("D_PENALTY");
                if (dblPenaltyAmt > 0) {
                    dOOffences.setPenalty(getFinalPenaltyAmount(applicationNo, Integer.parseInt(vehClass), Integer.parseInt(offcCode), dblPenaltyAmt, tmgr) + "");
                } else {
                    dOOffences.setPenalty("");
                }
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return dOOffences;
    }

    public double getFinalPenaltyAmount(String applicationNo, int VhClass, int OffCode, double dblPenaltyAmount, TransactionManager tmgr) throws VahanException {

        PreparedStatement pstm = null;
        ResultSet rs = null;
        double fPenalty = 0;
        double dblFlatRate = 0;
        double dblOverLoadWt = 0;
        int intOverLoadWt = 0;
        String sqlChallanAdlInfo = "SELECT * FROM  " + TableList.VT_CHALLAN_ADDL_INFO + "  WHERE appl_no=?";
        try {
            pstm = tmgr.prepareStatement(sqlChallanAdlInfo);
            pstm.setString(1, applicationNo);
            rs = pstm.executeQuery();
            if (rs.next()) {
                int projection = rs.getInt("projection");
                int overhang = rs.getInt("overhang");
                String animalType = rs.getString("animal_type");
                int excessPassenger = rs.getInt("excess_passenger");
                int excessAnimal = rs.getInt("excess_animal");
                int overLoad = rs.getInt("overload");
                String passenger = "P";
                if (OffCode == 49 || OffCode == 93) {
                    double arr[] = getExcessPassCF(VhClass + "", passenger, tmgr);
                    fPenalty = (excessPassenger * arr[1]) + arr[0];
                }
                if (OffCode == 41) {
                    double arr[] = getExcessCattleCF(VhClass + "", animalType, tmgr);
                    fPenalty = (excessAnimal * arr[1]) + arr[0];
                }
                if (OffCode == 14) {
                    fPenalty = overhang * dblPenaltyAmount;
                }
                if (OffCode == 1) {
                    String arr[] = getOverLoadCF(overLoad, tmgr);
                    if (arr != null) {

                        if (Integer.parseInt(arr[3]) > 0) {
                            dblFlatRate = Double.parseDouble(arr[3]);
                        }
                        if ("T".equalsIgnoreCase(arr[0])) {
                            dblOverLoadWt = overLoad / (Double.parseDouble(arr[1]) * 100);
                            intOverLoadWt = overLoad / (Integer.parseInt(arr[1]) * 100);
                            if (dblOverLoadWt > intOverLoadWt) {
                                intOverLoadWt = intOverLoadWt + 1;
                            }
                        } else {
                            dblOverLoadWt = overLoad / (Double.parseDouble(arr[1]) * 100);
                            intOverLoadWt = overLoad / (Integer.parseInt(arr[1]) * 100);
                            if (dblOverLoadWt > intOverLoadWt) {
                                intOverLoadWt = intOverLoadWt + 1;
                            }
                        }
                    }
                    fPenalty = dblFlatRate + Double.parseDouble(arr[2]) * intOverLoadWt;
                }
                if (OffCode == 11 || OffCode == 12 || OffCode == 13 || OffCode == 15 || OffCode == 16 || OffCode == 17 || (OffCode > 1 && OffCode < 11)) {
                    fPenalty = projection * dblPenaltyAmount;
                }

            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        if (fPenalty > 0) {
            return fPenalty;
        } else {
            return dblPenaltyAmount;
        }
    }

    public String[] getOverLoadCF(int overLoad, TransactionManager tmgr) throws VahanException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String arr[] = new String[4];
        String sqlOverLoadSchedule = "SELECT  WT_UNIT,WT_UNIT_VAL,UNIT_CF_AMT,FLAT_CF_AMT FROM   " + TableList.VM_OVERLOAD_SCHEDULE + "   where ?  between L_WT and U_WT";
        try {
            pstm = tmgr.prepareStatement(sqlOverLoadSchedule);
            pstm.setInt(1, overLoad);
            rs = pstm.executeQuery();
            if (rs.next()) {
                arr[0] = rs.getString("WT_UNIT");
                arr[1] = rs.getString("WT_UNIT_VAL");
                arr[2] = rs.getString("UNIT_CF_AMT");
                arr[3] = rs.getString("FLAT_CF_AMT");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return arr;
    }

    public double[] getExcessCattleCF(String vhClass, String animalType, TransactionManager tmgr) throws VahanException {
        double arr[] = new double[2];
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String strQry = "select * from  " + TableList.VM_EXCESS_SCHEDULE + "  WHERE VH_CLASS Like  '%?%' AND type=?";
        try {
            pstm = tmgr.prepareStatement(strQry);
            pstm.setString(1, vhClass);
            pstm.setString(2, animalType);
            rs = pstm.executeQuery();
            if (rs.next()) {
                arr[0] = rs.getDouble("flat_rate");
                arr[1] = rs.getDouble("unit_rate");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return arr;
    }

    public double[] getExcessPassCF(String vhClass, String passanger, TransactionManager tmgr) throws VahanException {
        double arr[] = new double[2];
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String strQry = "select * from  " + TableList.VM_EXCESS_SCHEDULE + "  WHERE VH_CLASS Like  '%?%' AND type=?";
        try {
            pstm = tmgr.prepareStatement(strQry);
            pstm.setString(1, vhClass);
            pstm.setString(2, passanger);
            rs = pstm.executeQuery();
            if (rs.next()) {
                arr[0] = rs.getDouble("flat_rate");
                arr[1] = rs.getDouble("unit_rate");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return arr;
    }

    public String getStateDescr(String code) throws VahanException {
        String descr = "";
        PreparedStatement pstm = null;
        TransactionManager tmgr = null;
        ResultSet rs = null;
        String sqlChallanHist = "SELECT * FROM  " + TableList.TM_STATE + "  WHERE state_code=?";
        try {
            tmgr = new TransactionManager("DAODisposalOfChallan.getStateDescr()");
            pstm = tmgr.prepareStatement(sqlChallanHist);
            pstm.setString(1, code);
            rs = pstm.executeQuery();
            if (rs.next()) {
                descr = rs.getString("descr");
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
        return descr;
    }

    public String getRtoDescr(String rtoCD, String stateCD) throws VahanException {
        String descr = "";
        PreparedStatement pstm = null;
        TransactionManager tmgr = null;
        ResultSet rs = null;
        String sqlChallanHist = "SELECT * FROM " + TableList.TM_OFFICE + " WHERE off_cd=? AND state_cd=?";
        try {
            tmgr = new TransactionManager("DAODisposalOfChallan.getRtoDescr()");
            pstm = tmgr.prepareStatement(sqlChallanHist);
            pstm.setInt(1, Integer.parseInt(rtoCD));
            pstm.setString(2, stateCD);
            rs = pstm.executeQuery();
            if (rs.next()) {
                descr = rs.getString("off_name");
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
        return descr;
    }

    public String getVehClassDescr(String vehClass) throws VahanException {
        String descr = "";
        PreparedStatement pstm = null;
        TransactionManager tmgr = null;
        ResultSet rs = null;
        String sqlChallanHist = "SELECT * FROM  " + TableList.VM_VH_CLASS + "  WHERE vh_class=? ";
        try {
            tmgr = new TransactionManager("DAODisposalOfChallan.getVehClassDescr()");
            pstm = tmgr.prepareStatement(sqlChallanHist);
            pstm.setInt(1, Integer.parseInt(vehClass));
            rs = pstm.executeQuery();
            if (rs.next()) {
                descr = rs.getString("descr");
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
        return descr;
    }

    public String getFuelDescr(String code) throws VahanException {
        String descr = "";
        PreparedStatement pstm = null;
        TransactionManager tmgr = null;
        ResultSet rs = null;
        String sqlChallanHist = "SELECT * FROM  " + TableList.VM_FUEL + "  WHERE code=? ";
        try {
            tmgr = new TransactionManager("DAODisposalOfChallan.getFuelDescr()");
            pstm = tmgr.prepareStatement(sqlChallanHist);
            pstm.setInt(1, Integer.parseInt(code));
            rs = pstm.executeQuery();
            if (rs.next()) {
                descr = rs.getString("descr");
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
        return descr;
    }

    public String getOfficerName(String officerNo) throws VahanException {
        String descr = "";
        PreparedStatement pstm = null;
        TransactionManager tmgr = null;
        ResultSet rs = null;
        String sqlChallanOffc = "SELECT * FROM " + TableList.TM_USER_INFO + "  WHERE user_cd=? ";
        try {
            tmgr = new TransactionManager("DAODisposalOfChallan.getOfficerName()");
            pstm = tmgr.prepareStatement(sqlChallanOffc);
            pstm.setInt(1, Integer.parseInt(officerNo));
            rs = pstm.executeQuery();
            if (rs.next()) {
                descr = rs.getString("user_name");
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
        return descr;
    }

    public String getCourtName(int courtCD) throws VahanException {
        String descr = "";
        PreparedStatement pstm = null;
        TransactionManager tmgr = null;
        ResultSet rs = null;
        String sqlChallanOffc = "SELECT * FROM  " + TableList.VM_COURT + "  WHERE COURT_CD=? and state_cd=? ";
        try {
            tmgr = new TransactionManager("DAODisposalOfChallan.getCourtName()");
            pstm = tmgr.prepareStatement(sqlChallanOffc);
            pstm.setInt(1, courtCD);
            pstm.setString(2, Util.getUserStateCode());
            rs = pstm.executeQuery();
            if (rs.next()) {
                descr = rs.getString("court_name");
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
        return descr;
    }

    public String getSectionName(int sectionCD) throws VahanException {
        String descr = "";
        PreparedStatement pstm = null;
        TransactionManager tmgr = null;
        ResultSet rs = null;
        String sqlSectionMast = "SELECT * FROM " + TableList.VM_SECTION + "  WHERE section_cd=?";
        try {
            tmgr = new TransactionManager("DAODisposalOfChallan.getSectionName()");
            pstm = tmgr.prepareStatement(sqlSectionMast);
            pstm.setInt(1, sectionCD);
            rs = pstm.executeQuery();
            if (rs.next()) {
                descr = rs.getString("section_name");
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
        return descr;
    }

    public String getOffenceName(int offCode) throws VahanException {
        String descr = "";
        PreparedStatement pstm = null;
        TransactionManager tmgr = null;
        ResultSet rs = null;
        String sqlOffcMast = "SELECT * FROM  " + TableList.VM_OFFENCES + "   WHERE offence_cd=? and state_cd=?";
        try {
            tmgr = new TransactionManager("DAODisposalOfChallan.getOffenceName()");
            pstm = tmgr.prepareStatement(sqlOffcMast);
            pstm.setInt(1, offCode);
            pstm.setString(2, Util.getUserStateCode());
            rs = pstm.executeQuery();
            if (rs.next()) {
                descr = rs.getString("offence_desc");
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
        return descr;
    }

    public String getDocName(String docCode) throws VahanException {
        String descr = "";
        PreparedStatement pstm = null;
        TransactionManager tmgr = null;
        ResultSet rs = null;
        String sqlOffcMast = "SELECT * FROM  " + TableList.VM_DOCUMENTS + "  WHERE code=?";
        try {
            tmgr = new TransactionManager("DAODisposalOfChallan.getDocName()");
            pstm = tmgr.prepareStatement(sqlOffcMast);
            pstm.setInt(1, Integer.parseInt(docCode));

            rs = pstm.executeQuery();
            if (rs.next()) {
                descr = rs.getString("descr");
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
        return descr;
    }

    public boolean updateDisposeChallan(String VehNo, String applicationNO, String Courtcase, SaveChallanDobj dobj, Status_dobj statusDobj, String roleCd, String changedDataContents, SaveChallanDobj privirosDobj, DisposeChallanDobj disposeDobj) throws VahanException {
        PreparedStatement pstmt = null;
        TransactionManager tmgr = null;
        RowSet rs = null;
        String rcptNo = null;
        boolean flag = false;
        String sql = "";
        int i = 0;
        try {
            tmgr = new TransactionManager("updateDisposeChallan");
            sql = " INSERT INTO " + TableList.VH_CHALLAN + " SELECT appl_no,  chal_no, "
                    + " regn_no, chal_date, chal_time,"
                    + " chal_place, is_doc_impound, is_vch_impdound, chal_officer, nc_c_ref_no,"
                    + "  remarks,?, ?, state_cd, off_cd,comming_from,going_to, settled_spot"
                    + "  FROM " + TableList.VT_CHALLAN + " where appl_no=?";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, disposeDobj.getRemarksOfDispose());
            pstmt.setTimestamp(2, getTimeStamp1(disposeDobj.getDateOfDispose()));
            pstmt.setString(3, applicationNO);
            pstmt.execute();
            if (Integer.parseInt(roleCd) == TableConstants.VM_ROLE_ENFORCEMENT_DISPOSE_ENTRY && CommonUtils.isNullOrBlank(dobj.getCourtCd())) {
                String check = "Select * from " + TableList.VT_CHALLAN_REFER_TO_COURT + " where appl_no=?";
                pstmt = tmgr.prepareStatement(check);
                pstmt.setString(1, applicationNO);
                RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
                if (rs1.next()) {//if record is exist then updated otherwise inserted
                    sql = "UPDATE echallan.vt_challan_refer_to_court\n"
                            + " SET appl_no=?, court_cd=?, hearing_date=?, court_paid_amount=?, court_rcpt_no=?, \n"
                            + " remarks=?, court_status=?, op_dt=current_timestamp, state_cd=?, off_cd=?, court_rcpt_date=?\n"
                            + " WHERE appl_no=? and state_cd=?";
                    pstmt = tmgr.prepareStatement(sql);
                    pstmt.setString(++i, dobj.getApplicationNO());
                    pstmt.setInt(++i, Integer.parseInt(disposeDobj.getCourt_name()));
                    if (disposeDobj.getHearing_date() != null) {
                        pstmt.setTimestamp(++i, ChallanUtil.getTimeStamp(disposeDobj.getHearing_date().toString()));
                    } else {
                        pstmt.setTimestamp(++i, null);
                    }
                    if (disposeDobj.getCourt_paid_amount() != null) {
                        pstmt.setInt(++i, Integer.parseInt(disposeDobj.getCourt_paid_amount()));
                    } else {
                        pstmt.setInt(++i, 0);
                    }
                    pstmt.setString(++i, disposeDobj.getCourt_rcpt_no());
                    pstmt.setString(++i, disposeDobj.getDecisionOfCourt());
                    pstmt.setString(++i, Courtcase);
                    pstmt.setString(++i, Util.getUserStateCode());
                    pstmt.setInt(++i, Util.getSelectedSeat().getOff_cd());
                    if (disposeDobj.getCourt_rcpt_date() != null) {
                        pstmt.setTimestamp(++i, ChallanUtil.getTimeStamp(disposeDobj.getCourt_rcpt_date().toString()));
                    } else {
                        pstmt.setTimestamp(++i, null);
                    }
                    pstmt.setString(++i, dobj.getApplicationNO());
                    pstmt.setString(++i, Util.getUserStateCode());
                    pstmt.executeUpdate();
                }

                sql = null;
                pstmt = null;
                sql = "DELETE FROM   " + TableList.VA_CHALLAN + " WHERE regn_no= ? AND appl_no=?";
                pstmt = tmgr.prepareStatement(sql);
                pstmt.setString(1, VehNo);
                pstmt.setString(2, applicationNO);
                pstmt.executeUpdate();
                sql = null;
                pstmt = null;
                sql = "DELETE FROM   " + TableList.VT_CHALLAN + " WHERE regn_no= ? AND appl_no=?";
                pstmt = tmgr.prepareStatement(sql);
                pstmt.setString(1, VehNo);
                pstmt.setString(2, applicationNO);
                pstmt.executeUpdate();
                sql = "SELECT * FROM  " + TableList.VT_CHALLAN_AMT + "  WHERE appl_no=? ";
                pstmt = tmgr.prepareStatement(sql);
                pstmt.setString(1, applicationNO);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    if (!CommonUtils.isNullOrBlank(rs.getString("cmpd_rcpt_no"))) {
                        rcptNo = rs.getString("cmpd_rcpt_no");
                        sql = "UPDATE  " + TableList.VT_FEE + "  SET FLAG=1 where rcpt_no=? AND regn_no=?";
                        pstmt = tmgr.prepareStatement(sql);
                        pstmt.setString(1, rcptNo);
                        pstmt.setString(2, VehNo);
                        pstmt.executeUpdate();
                    }
                }
                sql = "UPDATE " + TableList.VT_VCH_OFFENCES + " SET offence_status=1 where appl_no=? ";
                pstmt = tmgr.prepareStatement(sql);
                pstmt.setString(1, applicationNO);
                pstmt.executeUpdate();
                if (!CommonUtils.isNullOrBlank(Courtcase) && "Y".equalsIgnoreCase(Courtcase)) {
                    sql = "UPDATE  " + TableList.VT_CHALLAN_REFER_TO_COURT + "  SET court_status=1 where rcpt_no=? AND appl_no=?";
                    pstmt = tmgr.prepareStatement(sql);
                    pstmt.setString(1, applicationNO);
                }
            }

            flag = true;
            tmgr.commit();
        } catch (SQLException | VahanException | NumberFormatException e) {
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

    public boolean chkChallanSettlement(String applicationNo) throws VahanException {
        PreparedStatement pstm = null;
        TransactionManager tmgr = null;
        ResultSet rs = null;
        boolean flag = false;
        String sql = "select *  from " + TableList.VT_CHALLAN_AMT
                + " where appl_no=? and adv_amt>0 ::numeric and  (adv_rcpt_no is not null OR adv_rcpt_no !='') ";
        String sqlChallanFee = "select * from  " + TableList.VT_CHALLAN_AMT + "  where appl_no=? and (settlement_amt IS NULL)";
        String sqlChallanTax = "select * from  " + TableList.VT_CHALLAN_TAX + "  where appl_no=? and cmpd_rcpt_no is null";
        try {
            tmgr = new TransactionManager("DAODisposalOfChallan.chkChallanSettlement()");
            pstm = tmgr.prepareStatement(sql);
            pstm.setString(1, applicationNo);
            rs = pstm.executeQuery();
            if (rs.next()) {
                flag = false;
            } else {
                pstm = null;
                rs = null;
                pstm = tmgr.prepareStatement(sqlChallanFee);
                pstm.setString(1, applicationNo);
                rs = pstm.executeQuery();
                if (rs.next()) {
                    flag = true;
                }
                pstm = null;
                rs = null;
                pstm = tmgr.prepareStatement(sqlChallanTax);
                pstm.setString(1, applicationNo);
                rs = pstm.executeQuery();
                if (rs.next()) {
                    flag = true;
                }
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

    public boolean chkRefToCourt(String applicationNo) throws VahanException {
        PreparedStatement pstm = null;
        TransactionManager tmgr = null;
        ResultSet rs = null;
        boolean flag = false;
        String sqlCourt = "SELECT * FROM  " + TableList.VT_CHALLAN_REFER_TO_COURT + "   WHERE appl_no=? ";
        try {
            tmgr = new TransactionManager("DAODisposalOfChallan.chkRefToCourt()");
            pstm = tmgr.prepareStatement(sqlCourt);
            pstm.setString(1, applicationNo);

            rs = pstm.executeQuery();
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

    public boolean chkChallanPayment(String applicationNO) throws VahanException {
        PreparedStatement pstm = null;
        TransactionManager tmgr = null;
        ResultSet rs = null;
        boolean flag = false;

        String sqlChallanFee = "select * from  " + TableList.VT_CHALLAN_AMT + " "
                + " where appl_no=? "
                + "and  "
                + "  ((adv_rcpt_no is not null OR adv_rcpt_no !='') OR (cmpd_rcpt_no is not null OR cmpd_rcpt_no !='')) ";
        String sqlChallanTax = "select * from  " + TableList.VT_CHALLAN_TAX + "  "
                + " where appl_no=? "
                + " and    (cmpd_rcpt_no is not null OR cmpd_rcpt_no !='')";

        String sqlChallanReferTOCourt = "select * from  " + TableList.VT_CHALLAN_REFER_TO_COURT + " where appl_no=?";

        try {
            tmgr = new TransactionManager("DAODisposalOfChallan.chkChallanPayment()");
            pstm = tmgr.prepareStatement(sqlChallanFee);
            pstm.setString(1, applicationNO);
            rs = pstm.executeQuery();
            if (rs.next()) {
                flag = true;
            }
            pstm = null;
            rs = null;
            pstm = tmgr.prepareStatement(sqlChallanTax);
            pstm.setString(1, applicationNO);

            rs = pstm.executeQuery();
            if (rs.next()) {
                flag = true;
            }
            pstm = null;
            rs = null;
            pstm = tmgr.prepareStatement(sqlChallanReferTOCourt);
            pstm.setString(1, applicationNO);
            rs = pstm.executeQuery();
            if (rs.next()) {
                String sqlChkPayment = " select * from  " + TableList.VT_CHALLAN_REFER_TO_COURT + "  "
                        + "where appl_no=?"
                        + " and court_paid_amount >0 "
                        + "AND (court_rcpt_no is null or court_rcpt_no= '')";
                pstm = null;
                rs = null;
                pstm = tmgr.prepareStatement(sqlChkPayment);
                pstm.setString(1, applicationNO);
                rs = pstm.executeQuery();
                if (rs.next()) {
                    flag = true;
                }

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

    public static String dateFormat1(String inputDt) throws VahanException {
        String formatedDt = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date dt1 = sdf.parse(inputDt);

            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyy");
            formatedDt = sdf1.format(dt1);

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);

        }
        return formatedDt;
    }

    public static Timestamp getDDMMMYYYYToTimesTamp(String strDt) throws VahanException {

        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyy hh:mm:ss");
        Timestamp timeStampDate;
        Date date = new Date();
        try {
            date = (Date) sdf1.parse(strDt);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);

        }
        timeStampDate = new Timestamp(date.getTime());
        return timeStampDate;
    }

    public static Timestamp getTimeStamp(String Date) throws VahanException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yy");

        Timestamp st = null;
        try {
            java.util.Date date = sdf.parse(Date);
            st = new Timestamp(date.getTime());

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);

        }

        return st;
    }

    public static Timestamp getTimeStamp1(String Date) throws VahanException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");

        Timestamp st = null;
        try {
            java.util.Date date = sdf.parse(Date);
            st = new Timestamp(date.getTime());

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);

        }


        return st;
    }

    public void movetoapprove(String roleCd, Status_dobj statusDobj, String changedDataContents, String VehNo, String applicationNO, String Courtcase, String Date, String User, String CourtDecision, String DecisionDt, SaveChallanDobj dobj, boolean blnManDispose, String ManDisposeReason, SaveChallanDobj previousDobj, List<DocTableDobj> docDetails) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;

        String rcptNo = "";
        ResultSet rs = null;
        String sqlChallanFee = "SELECT * FROM  " + TableList.VT_CHALLAN_AMT + "  WHERE appl_no=? ";
        String updateVTFEE = "UPDATE  " + TableList.VT_FEE + "  SET FLAG=1 where rcpt_no=? AND regn_no=?";
        String updateVTVCHOFFENCE = "UPDATE " + TableList.VT_VCH_OFFENCES + " SET offence_status=1 where appl_no=? ";
        String updateChallanRefToCourt = "UPDATE  " + TableList.VT_CHALLAN_REFER_TO_COURT + "  SET court_status=1 where rcpt_no=? AND regn_no=?";
        String sqlDeleteChallanSQl = "DELETE FROM   " + TableList.VA_CHALLAN + " WHERE regn_no= ? AND appl_no=?";
        try {
            tmgr = new TransactionManager("Dispose Challan : saveSettleData");
            if (!changedDataContents.isEmpty()) {

                ps = tmgr.prepareStatement(sqlChallanFee);
                ps.setString(1, applicationNO);

                rs = ps.executeQuery();
                if (rs.next()) {
                    if (!CommonUtils.isNullOrBlank(rs.getString("cmpd_rcpt_no"))) {
                        rcptNo = rs.getString("cmpd_rcpt_no");
                        ps = tmgr.prepareStatement(updateVTFEE);
                        ps.setString(1, rcptNo);
                        ps.setString(2, VehNo);
                        ps.executeUpdate();
                    }
                }

                ps = tmgr.prepareStatement(updateVTVCHOFFENCE);
                ps.setString(1, applicationNO);

                ps.executeUpdate();
                if (!CommonUtils.isNullOrBlank(Courtcase) && "Y".equalsIgnoreCase(Courtcase)) {

                    ps = tmgr.prepareStatement(updateChallanRefToCourt);
                    ps.setString(1, applicationNO);
                }

                if ("".equals(previousDobj.getDisposalChallanDate()) || previousDobj.getDisposalChallanDate() == null) {
                    if (Integer.parseInt(roleCd) == TableConstants.VM_ROLE_ENFORCEMENT_DISPOSE_APPROVE) {

                        String ins_challan_data = " INSERT INTO " + TableList.VH_CHALLAN + " SELECT appl_no,  chal_no, "
                                + " regn_no, chal_date, chal_time,"
                                + " chal_place, is_doc_impound, is_vch_impdound, chal_officer, nc_c_ref_no,"
                                + "  remarks,?, ?, state_cd, off_cd,comming_from,going_to, settled_spot"
                                + "  FROM " + TableList.VT_CHALLAN + " where appl_no=?";
                        ps = tmgr.prepareStatement(ins_challan_data);
                        ps.setString(1, CourtDecision);
                        ps.setTimestamp(2, getTimeStamp1(DecisionDt));
                        ps.setString(3, applicationNO);
                        ps.execute();
                        ps = tmgr.prepareStatement(sqlDeleteChallanSQl);
                        ps.setString(1, VehNo);
                        ps.setString(2, applicationNO);
                        ps.executeUpdate();
                        String sqlDelete = "DELETE FROM " + TableList.VT_CHALLAN + " WHERE APPL_NO=?";
                        ps = tmgr.prepareStatement(sqlDelete);
                        ps.setString(1, applicationNO);
                        ps.executeUpdate();
                    }
                }
            }
            String sqlDelete = "DELETE FROM " + TableList.VT_CHALLAN + " WHERE APPL_NO=?";
            ps = tmgr.prepareStatement(sqlDelete);
            ps.setString(1, applicationNO);
            ps.executeUpdate();

            if (Integer.parseInt(roleCd) == TableConstants.VM_ROLE_ENFORCEMENT_DISPOSE_APPROVE) {
                if (dobj.getSezNo() == null && docDetails.isEmpty()) {
                    statusDobj.setEntry_status(TableConstants.STATUS_APPROVED);
                    ServerUtil.updateApprovedStatus(tmgr, statusDobj);
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

    public void reback(String roleCd, Status_dobj statusDobj, String changedDataContents, String toUpperCase, String applicationNo, String strCourtcase, String ladn, String string, String courtDecision, String courtDate, SaveChallanDobj dobjOwnerChallan, boolean b, String string0, SaveChallanDobj previousDobj) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("movetoapprove");
            if (!changedDataContents.isEmpty()) {
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

    public List<VehcleOffenceDobj> getvehOffence(String applNo) throws VahanException {
        List offenceList = new ArrayList();
        List miscFeeList = new ArrayList();
        int offenceCd = 0;
        String offenceDescr = "";
        VehcleOffenceDobj dobjoffence = null;
        TransactionManager tmgr = null;
        PreparedStatement pstmtgetVehOffence = null;
        int slNo = 0;
        try {
            String offenceSql = "SELECT OFFENCE.appl_no, OFFENCE.offence_cd, OFFENCE.accused_catg, OFFENCE.offence_amt,  \n"
                    + "       OFFMAST.OFFENCE_DESC ,accused.descr AS AccusedDescR,OFFMAST.MVA_CLAUSE\n"
                    + "FROM   echallan.vt_vch_offences OFFENCE\n"
                    + "LEFT OUTER JOIN  " + TableList.VM_OFFENCES + "   OFFMAST ON OFFENCE.OFFENCE_CD  = OFFMAST.OFFENCE_CD  AND OFFENCE.STATE_CD=OFFMAST.STATE_CD \n"
                    + "LEFT OUTER JOIN  " + TableList.VM_ACCUSED + "  accused ON accused.code  =OFFENCE.accused_catg  \n"
                    + " WHERE appl_no=? AND OFFENCE.STATE_CD=? ";
            tmgr = new TransactionManager("SettleChallanDAO : getvehOffence()");
            pstmtgetVehOffence = tmgr.prepareStatement(offenceSql);
            pstmtgetVehOffence.setString(1, applNo);
            pstmtgetVehOffence.setString(2, Util.getUserStateCode());
            ResultSet rs = pstmtgetVehOffence.executeQuery();
            while (rs.next()) {
                offenceCd = Integer.parseInt(rs.getString("offence_cd"));
                if (offenceCd == 999) {
                    offenceDescr = "Miscellaneous Fee";
                } else {
                    offenceDescr = rs.getString("offence_desc");
                }
                slNo++;
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

    public CompoundingInOfficeDobj getMiscFeeDetails(String appl_no) throws VahanException {
        CompoundingInOfficeDobj dobj = null;
        PreparedStatement pstm = null;
        TransactionManager tmgr = null;
        ResultSet rs = null;

        String sqlCourt = "SELECT * FROM  " + TableList.VT_CHALLAN_AMT + "   WHERE appl_no=? ";
        try {
            tmgr = new TransactionManager("getMiscFeeDetails()");
            pstm = tmgr.prepareStatement(sqlCourt);
            pstm.setString(1, appl_no);
            rs = pstm.executeQuery();
            if (rs.next()) {
                dobj = new CompoundingInOfficeDobj();
                dobj.setMiscFee(rs.getString("mislleneous_fee"));
                dobj.setReason(rs.getString("mislleneous_fee_reason"));
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

    public boolean isApplicationNoExistInVaStatus(String appl_no) throws VahanException {

        PreparedStatement pstm = null;
        TransactionManager tmgr = null;
        ResultSet rs = null;
        boolean flag = false;
        String sqlCourt = "SELECT * FROM  " + TableList.VA_DETAILS + "   WHERE appl_no=? and state_cd=?";
        try {
            tmgr = new TransactionManager("applicationExistInVaStatus()");
            pstm = tmgr.prepareStatement(sqlCourt);
            pstm.setString(1, appl_no);
            pstm.setString(2, Util.getUserStateCode());
            rs = pstm.executeQuery();
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
