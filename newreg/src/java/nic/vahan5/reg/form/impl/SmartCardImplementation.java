/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.form.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.DocumentType;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.db.user_mgmt.dobj.DealerMasterDobj;
import nic.vahan.form.dobj.BlackListedVehicleDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.smartcard.DeleteSmartcardFlatFileDobj;
import nic.vahan.form.dobj.smartcard.SmartCardDobj;
import nic.vahan.form.dobj.smartcard.SmartCardIpDobj;
import nic.vahan.form.dobj.smartcard.SmartcardTempDtlsDobj;
import nic.vahan.form.impl.ApplicationInwardImpl;
import nic.vahan.form.impl.BlackListedVehicleImpl;
import nic.vahan.form.impl.ComparisonBeanImpl;
import static nic.vahan.form.impl.NewImpl.isRegnNoNewVeh;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import static nic.vahan.server.ServerUtil.verifyForHsrp;
import nic.vahan5.reg.rest.model.SessionVariablesModel;
import nic.vahan5.reg.server.ServerUtility;

/**
 *
 * @author Kartikey Singh
 */
public class SmartCardImplementation {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(SmartCardImplementation.class);

    public static SmartCardDobj getSmartCardGenerateDetailsList(String state_cd, int off_cd, int offset, int limit, String regnNo) throws VahanException {
        ArrayList listSmartCard = new ArrayList();
        List<SmartCardDobj> pendingApplList = new ArrayList<>();
        PreparedStatement ps = null;
        boolean hsrpFitted = false;
        boolean pushedforSmartCard = false;
        String regnAlreadyPushed = "";
        boolean installmentNotPaid = false;
        int vhType = 0;

        String sql;
        String sql1;
//        if ("MH".equalsIgnoreCase(state_cd) && off_cd == 12) {
        sql = "Select rc.*,va.pur_cd,to_char(current_timestamp,'dd_Mon_yyyy_hh24_mi_ss') cdate"
                + " from " + TableList.VA_SMART_CARD + " va," + TableList.VA_SMART_CARD_TEMP
                + " rc where va.op_dt < current_timestamp and va.state_cd=? "
                + "and va.off_cd=? and va.appl_no=rc.rcpt_no ";
        if (regnNo != null) {
            sql = sql + " and rc.vehregno in (?, rpad(?, 10, ?), ?) ";
        }
        sql = sql + " order by va.op_dt,va.regn_no  limit ? offset ?";


        sql1 = "Select rc.*,va.pur_cd,to_char(current_timestamp,'dd_Mon_yyyy_hh24_mi_ss') cdate"
                + " from " + TableList.VA_SMART_CARD + " va," + TableList.RC_BE_TO_BO
                + " rc where va.op_dt < current_timestamp and state_cd=?"
                + " and off_cd=? and va.appl_no=rc.rcpt_no ";
        if (regnNo != null) {
            sql1 = sql1 + " and rc.vehregno in (?, rpad(?, 10, ?), ?) ";
        }
        sql1 = sql1 + " order by va.op_dt,va.regn_no  limit ? offset ? ";

        TransactionManager tmgr = null;
        SmartCardDobj ret_dobj = new SmartCardDobj();
        try {
            tmgr = new TransactionManager("getSmartCardGenerateDetailsList-1");
            TmConfigurationDobj tmConfig = Util.getTmConfiguration();
            boolean isHsrp = verifyForHsrp(Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), tmgr);
            boolean isOldHsrp = ServerUtil.verifyForOldVehicleHsrp(Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), tmgr);
            boolean isRcAfterHsrp = tmConfig.isRc_after_hsrp();
            int i = 1;
            if (isHsrp && isRcAfterHsrp) {
                ps = tmgr.prepareStatement(sql);
            } else {
                ps = tmgr.prepareStatement(sql1);
            }
            ps.setString(i++, state_cd);
            ps.setInt(i++, off_cd);
            if (regnNo != null) {
                ps.setString(i++, regnNo);
                ps.setString(i++, regnNo);
                ps.setString(i++, " ");
                ps.setString(i++, ServerUtil.getRegnNoWithSpace(regnNo));
            }

            ps.setInt(i++, limit);
            ps.setInt(i++, offset);

            RowSet rs = tmgr.fetchDetachedRowSetWithoutTrim();
            SmartCardDobj dobj = null;
            ret_dobj.setRecordsPresent(false);
            while (rs.next()) {
                if (isHsrp && isRcAfterHsrp) {

                    if (isRegnNoNewVeh(tmgr, rs.getString("rcpt_no")) || isOldHsrp) {
                        hsrpFitted = checkHSRPFittedVehicle(tmgr, rs.getString("vehregno").trim());
                    } else {
                        hsrpFitted = true;
                    }
                    String pushedSmartCardsql = "select rcpt_no,vehregno from smartcard.smart_card where rcpt_no=?"
                            + " union "
                            + "select rcpt_no,vehregno from smartcard.rc_be_to_bo where rcpt_no=?";
                    ps = tmgr.prepareStatement(pushedSmartCardsql);
                    ps.setString(1, rs.getString("rcpt_no").trim());
                    ps.setString(2, rs.getString("rcpt_no").trim());
                    RowSet rs2 = tmgr.fetchDetachedRowSet_No_release();
                    if (rs2.next()) {
                        pushedforSmartCard = true;
                        regnAlreadyPushed = rs2.getString("vehregno");
                    } else {
                        pushedforSmartCard = false;
                    }

                } else {
                    if (rs.getString("pur_cd").trim().contains("NEW")) {
                        String manuDate = rs.getString("manufdate");
                        String year = (manuDate.substring(3, 7));
                        String month = (manuDate.substring(0, 2));
                        int yearMon = Integer.parseInt(year + month);
                        if (yearMon >= TableConstants.CHECK_MANU_MONTH_YEAR_FOR_HSRP) {
                            hsrpFitted = checkHSRPFittedVehicle(tmgr, rs.getString("vehregno").trim());
                        } else {
                            hsrpFitted = true;
                        }
                    } else {
                        hsrpFitted = true;
                    }
                }
                String dealerName = "";

                OwnerImpl ownerImpl = new OwnerImpl();
                Owner_dobj ownerDobj = ownerImpl.set_Owner_appl_db_to_dobj_with_state_off_cd(rs.getString("vehregno").trim(), null, "", 1);
                if (ownerDobj != null) {
                    vhType = ownerDobj.getVehType();
                    if (!hsrpFitted) {
                        DealerMasterDobj dealrDobj = ServerUtil.getDealerDetailsByDealerCode(ownerDobj.getDealer_cd(), "");
                        if (dealrDobj != null) {
                            dealerName = dealrDobj.getDealerName();
                        }

                    }
                    BlackListedVehicleImpl obj = new BlackListedVehicleImpl();
                    BlackListedVehicleDobj blackListedDobj = obj.getBlacklistedVehicleDetails(ownerDobj.getRegn_no(), ownerDobj.getChasi_no());
                    if (blackListedDobj != null && (blackListedDobj.getComplain_type() == TableConstants.BLTheftCode
                            || blackListedDobj.getComplain_type() == TableConstants.BLDestroyedAccidentCode)
                            && rs.getString("pur_cd").trim().contains("TO")) {
                        hsrpFitted = true;
                    }
                    installmentNotPaid = checkFirstInstallmentPaidOrNot(tmgr, rs.getString("rcpt_no").trim(), rs.getString("vehregno").trim());
                    ApplicationInwardImpl applicationInwardImpl = new ApplicationInwardImpl();
                    boolean taxPaidOrClear = applicationInwardImpl.taxPaidOrClearedStatus(rs.getString("vehregno").trim(), tmConfig, ownerDobj, TableConstants.TM_ROAD_TAX);
                    if (!taxPaidOrClear && "DL,UP".contains(state_cd) && vhType == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
                        taxPaidOrClear = true;
                    }
                    if (!taxPaidOrClear && ownerDobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)
                            && state_cd.equalsIgnoreCase("DL")) {

                        taxPaidOrClear = applicationInwardImpl.taxPaidOrClearedStatusOnVehType(rs.getString("vehregno").trim());
                    }
                    if (hsrpFitted && taxPaidOrClear && !pushedforSmartCard && !installmentNotPaid) {
                        dobj = new SmartCardDobj();
                        dobj.setVehregno(rs.getString("vehregno"));
                        dobj.setRegdate(rs.getString("regdate"));
                        dobj.setOwnername(rs.getString("ownername"));
                        dobj.setFname(rs.getString("fname"));
                        dobj.setCaddress(rs.getString("caddress"));
                        dobj.setManufacturer(rs.getString("manufacturer"));
                        dobj.setModelno(rs.getString("modelno"));
                        dobj.setColour(rs.getString("colour"));
                        dobj.setFuel(rs.getString("fuel"));
                        dobj.setVehclass(rs.getString("vehclass"));
                        dobj.setBodytype(rs.getString("bodytype"));
                        dobj.setSeatcap(rs.getString("seatcap"));
                        dobj.setStandcap(rs.getString("standcap"));
                        dobj.setManufdate(rs.getString("manufdate"));
                        dobj.setUnladenwt(rs.getString("unladenwt"));
                        dobj.setCubiccap(rs.getString("cubiccap"));
                        dobj.setWheelbase(rs.getString("wheelbase"));
                        dobj.setNoofcylin(rs.getString("noofcylin"));
                        dobj.setOwnerserial(rs.getString("ownerserial"));
                        dobj.setChasisno(rs.getString("chasisno"));
                        dobj.setEngineno(rs.getString("engineno"));
                        dobj.setTaxpaidupto(rs.getString("taxpaidupto"));
                        dobj.setRegnvalidity(rs.getString("regnvalidity"));
                        dobj.setApprovingauth(rs.getString("approvingauth"));
                        dobj.setFinname(rs.getString("finname"));
                        dobj.setFinaddress(rs.getString("finaddress"));
                        dobj.setHypofrom(rs.getString("hypofrom"));
                        dobj.setHypoto(rs.getString("hypoto"));
                        dobj.setNocno(rs.getString("nocno"));
                        dobj.setStateto(rs.getString("stateto"));
                        dobj.setRtoto(rs.getString("rtoto"));
                        dobj.setNcrbclearno(rs.getString("ncrbclearno"));
                        dobj.setNocissuedt(rs.getString("nocissuedt"));
                        dobj.setInscompname(rs.getString("inscompname"));
                        dobj.setCoverpolicyno(rs.getString("coverpolicyno"));
                        dobj.setInstype(rs.getString("instype"));
                        dobj.setInsvalidupto(rs.getString("insvalidupto"));
                        dobj.setPuccentercode(rs.getString("puccentercode"));
                        dobj.setPucvalidupto(rs.getString("pucvalidupto"));
                        dobj.setTaxamount(rs.getString("taxamount"));
                        dobj.setFine(rs.getString("fine"));
                        dobj.setExemptrecptno(rs.getString("exemptrecptno"));
                        dobj.setPaymentdt(rs.getString("paymentdt"));
                        dobj.setTaxvalidfrom(rs.getString("taxvalidfrom"));
                        dobj.setTaxvalidto(rs.getString("taxvalidto"));
                        dobj.setExemption(rs.getString("exemption"));
                        dobj.setDrtocode(rs.getString("drtocode"));
                        dobj.setBuflag(rs.getString("buflag"));
                        dobj.setFitvalidupto(rs.getString("fitvalidupto"));
                        dobj.setFitinsofficer(rs.getString("fitinsofficer"));
                        dobj.setFitlocation(rs.getString("fitlocation"));
                        dobj.setGrossvehwt(rs.getString("grossvehwt"));
                        dobj.setSemitrailers(rs.getString("semitrailers"));
                        dobj.setTyreinfo(rs.getString("tyreinfo"));
                        dobj.setAxleinfo(rs.getString("axleinfo"));
                        dobj.setRcpt_no(rs.getString("rcpt_no"));
                        dobj.setPur_cd(rs.getString("pur_cd"));
                        dobj.setDeal_cd(rs.getString("deal_cd"));
                        dobj.setOp_dt(rs.getTimestamp("op_dt"));
                        dobj.setStatus(rs.getString("status"));
                        dobj.setCur_date(rs.getString("cdate"));
                        dobj.setSmartCardFileName(state_cd + off_cd + "_SC_" + rs.getString("cdate"));
                        dobj.setState_cd(state_cd);
                        dobj.setOff_cd(off_cd);
                        ret_dobj.setCur_date(rs.getString("cdate"));
                        ret_dobj.setSmartCardFileName(state_cd + off_cd + "_SC_" + rs.getString("cdate"));
                        listSmartCard.add(dobj);
                    } else {

                        if (!hsrpFitted && !taxPaidOrClear) {
                            SmartCardDobj smart_card_dobj = new SmartCardDobj();
                            smart_card_dobj.setPendingApplnoForHsrp(rs.getString("rcpt_no"));
                            smart_card_dobj.setPendingVehnoForHsrp(rs.getString("vehregno"));
                            smart_card_dobj.setPendingReason("HSRP Pending - " + dealerName
                                    + " and Tax Not Clear");
                            pendingApplList.add(smart_card_dobj);

                        } else {
                            if (!hsrpFitted) {
                                SmartCardDobj smart_card_dobj = new SmartCardDobj();
                                smart_card_dobj.setPendingApplnoForHsrp(rs.getString("rcpt_no"));
                                smart_card_dobj.setPendingVehnoForHsrp(rs.getString("vehregno"));
                                smart_card_dobj.setPendingReason("HSRP Pending - " + dealerName);
                                pendingApplList.add(smart_card_dobj);
                            }
                            if (!taxPaidOrClear) {
                                SmartCardDobj smart_card_dobj = new SmartCardDobj();
                                smart_card_dobj.setPendingApplnoForHsrp(rs.getString("rcpt_no"));
                                smart_card_dobj.setPendingVehnoForHsrp(rs.getString("vehregno"));
                                smart_card_dobj.setPendingReason("Tax not clear");
                                pendingApplList.add(smart_card_dobj);
                            }
                            if (pushedforSmartCard) {
                                SmartCardDobj smart_card_dobj = new SmartCardDobj();
                                smart_card_dobj.setPendingApplnoForHsrp(rs.getString("rcpt_no"));
                                smart_card_dobj.setPendingVehnoForHsrp(rs.getString("vehregno"));
                                smart_card_dobj.setPendingReason("Already Data Pushed for Smart Card Preparation for " + regnAlreadyPushed);
                                pendingApplList.add(smart_card_dobj);
                            }
                            if (installmentNotPaid) {
                                SmartCardDobj smart_card_dobj = new SmartCardDobj();
                                smart_card_dobj.setPendingApplnoForHsrp(rs.getString("rcpt_no"));
                                smart_card_dobj.setPendingVehnoForHsrp(rs.getString("vehregno"));
                                smart_card_dobj.setPendingReason("First Installment Not Paid");
                                pendingApplList.add(smart_card_dobj);
                            }
                        }
                    }
                }
                ret_dobj.setRecordsPresent(true);
            }
            ret_dobj.setListHsrpPending(pendingApplList);
            ret_dobj.setListSmartCard(listSmartCard);
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in getting smartcard pending list.");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return ret_dobj;
    }

    private static boolean checkHSRPFittedVehicle(TransactionManager tmgr, String regnNo) throws VahanException {
        boolean hsrpFitted = false;
        PreparedStatement ps = null;
        try {
            String sqlhsrp = "select * from " + TableList.VT_HSRP + " where regn_no=?";
            ps = tmgr.prepareStatement(sqlhsrp);
            ps.setString(1, regnNo);
            RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
            if (rs1.next()) {
                hsrpFitted = true;
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in getting HSRP vehicle fitted details.");
        }
        return hsrpFitted;
    }

    private static boolean checkFirstInstallmentPaidOrNot(TransactionManager tmgr, String rcptNo, String regNo) throws SQLException {
        boolean status = false;
        String sql = "SELECT * from " + TableList.VT_TAX_INSTALLMENT + " where appl_regn_no = ?";

        try {
            PreparedStatement ps = tmgr.prepareStatement(sql);

            ps.setString(1, rcptNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                sql = "SELECT * from " + TableList.VT_TAX_INSTALLMENT_BRKUP + " where regn_no = ? and serial_no = '1'";
                PreparedStatement psmt = tmgr.prepareStatement(sql);
                psmt.setString(1, regNo);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    if (!(rs.getString("rcpt_no") != null)) {
                        status = true;
                    }
                } else {
                    status = true;
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        return status;
    }

    public static List<SmartCardDobj> checkAndUpdatePendingHsrp(String state_cd, int off_cd, TransactionManager tmgr, SmartCardDobj dobj) throws VahanException {
        PreparedStatement ps = null;
        boolean dataInserted = false;
        List<SmartCardDobj> pendingApplList = new ArrayList<>();
        try {

            for (SmartCardDobj list : dobj.getListSmartCard()) {

                dataInserted = true;
                String sql = "INSERT INTO " + TableList.SMART_CARD
                        + "     SELECT regexp_replace(vehregno, '[\\\"'';`]', ' ','g'), regdate, regexp_replace(ownername, '[\"'';`]', ' ','g'), "
                        + "       regexp_replace(fname, '[\"'';`]', ' ','g') ,regexp_replace(caddress, '[\"'';`]', ' ','g') , regexp_replace(manufacturer, '[\\\"'';`]', ' ','g'), "
                        + "       regexp_replace(modelno, '[\"'';`]', ' ','g'), regexp_replace(colour, '[\\\"'';`]', ' ','g'), regexp_replace(fuel, '[\\\"'';`]', ' ','g'), regexp_replace(vehclass, '[\\\"'';`]', ' ','g'),"
                        + "       regexp_replace(bodytype,'[\"'';`]', ' ','g'), seatcap, standcap, "
                        + "       manufdate, unladenwt, cubiccap, wheelbase, noofcylin, ownerserial, "
                        + "       regexp_replace(chasisno, '[\\\"'';`]', ' ','g'), regexp_replace(engineno, '[\\\"'';`]', ' ','g'), taxpaidupto, regnvalidity, regexp_replace(approvingauth, '[\\\"'';`]', ' ','g'), "
                        + "       regexp_replace(finname, '[\\\"'';`]', ' ','g'), regexp_replace(finaddress, '[\\\"'';`]', ' ','g'), hypofrom, hypoto, regexp_replace(nocno, '[\\\"'';`]', ' ','g'), stateto, rtoto, "
                        + "       regexp_replace(ncrbclearno, '[\\\"'';`]', ' ','g'), nocissuedt, inscompname, regexp_replace(coverpolicyno, '[\\\"'';`\t]', ' ','g'), instype, "
                        + "       insvalidupto, puccentercode, pucvalidupto, taxamount, fine, regexp_replace(exemptrecptno, '[\\\"'';`]', ' ','g'), "
                        + "       paymentdt, taxvalidfrom, taxvalidto, exemption, drtocode, buflag, "
                        + "       fitvalidupto, fitinsofficer, fitlocation, grossvehwt, semitrailers, "
                        + "       tyreinfo, axleinfo, rcpt_no, pur_cd, ?, current_timestamp, status, "
                        + "       flat_file from " + TableList.VA_SMART_CARD_TEMP
                        + " where rcpt_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, list.getRcpt_no());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                sql = "INSERT INTO  " + TableList.RC_BE_TO_BO
                        + "    SELECT regexp_replace(vehregno, '[\\\"'';`]', ' ','g'), regdate, regexp_replace(ownername, '[\"'';`]', ' ','g'), regexp_replace(fname, '[\"'';`]', ' ','g'),"
                        + "       regexp_replace(caddress, '[\"'';`]', ' ','g'), regexp_replace(manufacturer, '[\\\"'';`]', ' ','g'), \n"
                        + "       regexp_replace(modelno, '[\"'';`]', ' ','g'), regexp_replace(colour, '[\\\"'';`]', ' ','g'), regexp_replace(fuel, '[\\\"'';`]', ' ','g'), regexp_replace(vehclass, '[\\\"'';`]', ' ','g'), regexp_replace(bodytype, '[\"'';`]', ' ','g'), seatcap, standcap, \n"
                        + "       manufdate, unladenwt, cubiccap, wheelbase, noofcylin, ownerserial, \n"
                        + "       regexp_replace(chasisno, '[\\\"'';`]', ' ','g'), regexp_replace(engineno, '[\\\"'';`]', ' ','g'), taxpaidupto, regnvalidity, regexp_replace(approvingauth, '[\\\"'';`]', ' ','g'), \n"
                        + "       regexp_replace(finname, '[\\\"'';`]', ' ','g'), regexp_replace(finaddress, '[\\\"'';`]', ' ','g'), hypofrom, hypoto, regexp_replace(nocno, '[\\\"'';`]', ' ','g'), stateto, rtoto, \n"
                        + "       regexp_replace(ncrbclearno, '[\\\"'';`]', ' ','g'), nocissuedt, inscompname, regexp_replace(coverpolicyno, '[\\\"'';`\t]', ' ','g'), instype, \n"
                        + "       insvalidupto, puccentercode, pucvalidupto, taxamount, fine, regexp_replace(exemptrecptno, '[\\\"'';`]', ' ','g'), \n"
                        + "       paymentdt, taxvalidfrom, taxvalidto, exemption, drtocode, buflag, \n"
                        + "       fitvalidupto, fitinsofficer, fitlocation, grossvehwt, semitrailers, \n"
                        + "       tyreinfo, axleinfo, rcpt_no, pur_cd, ?, current_timestamp, status from " + TableList.SMART_CARD
                        + " where rcpt_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, list.getRcpt_no());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                sql = "DELETE FROM " + TableList.VA_SMART_CARD_TEMP + " where rcpt_no=? and state_cd=? and off_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, list.getRcpt_no());
                ps.setString(2, state_cd);
                ps.setInt(3, off_cd);
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                sql = "INSERT INTO " + TableList.VH_SMART_CARD
                        + "  Select state_cd, off_cd, appl_no, regn_no, pur_cd, user_cd, op_dt, ?,"
                        + " current_timestamp, ? from " + TableList.VA_SMART_CARD
                        + " where op_dt< to_timestamp( ?,'dd_Mon_yyyy_hh24_mi_ss') and "
                        + " state_cd=? and off_cd=? and appl_no = ? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getSmartCardFileName());
                ps.setString(2, Util.getEmpCode());
                ps.setString(3, dobj.getCur_date());
                ps.setString(4, state_cd);
                ps.setInt(5, off_cd);
                ps.setString(6, list.getRcpt_no());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);



                sql = "Delete from " + TableList.VA_SMART_CARD
                        + " where op_dt< to_timestamp( ?,'dd_Mon_yyyy_hh24_mi_ss') and "
                        + " state_cd=? and off_cd=? and appl_no = ? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getCur_date());
                ps.setString(2, state_cd);
                ps.setInt(3, off_cd);
                ps.setString(4, list.getRcpt_no());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

            }

            if (dataInserted) {
                String sql = "Insert into " + TableList.VT_SC_FLATFILE
                        + " (state_cd, off_cd, flat_file, op_dt) values(?,?,?,current_timestamp)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, state_cd);
                ps.setInt(2, off_cd);
                ps.setString(3, dobj.getSmartCardFileName());
                ps.executeUpdate();
            } else {
                throw new VahanException("Error in downloading Flat File");
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in downloading Flat File");
        }
        return pendingApplList;
    }

    public static List<SmartCardDobj> getSmartCardOldList(String state_cd, int off_cd, Date oldDate) {

        List<SmartCardDobj> retList = new ArrayList<>();
        ArrayList listSmartCard = null;
        String sql = "Select *  from " + TableList.VT_SC_FLATFILE
                + "   where state_cd = ? and off_cd = ? and date(op_dt)=date(?)";
        TransactionManagerReadOnly tmgr = null;
        SmartCardDobj ret_dobj = null;

        try {
            tmgr = new TransactionManagerReadOnly("SmartCardImpl.getSmartCardOldList");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.setDate(3, new java.sql.Date(oldDate.getTime()));
            RowSet rsSm = tmgr.fetchDetachedRowSet();
            SmartCardDobj dobj = null;

            while (rsSm.next()) {

                ret_dobj = new SmartCardDobj();
                ret_dobj.setCur_date(rsSm.getString("flat_file"));
                ret_dobj.setSmartCardFileName(rsSm.getString("flat_file"));
                retList.add(ret_dobj);
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

        return retList;
    }

    public static String getSmartCardFlatFileName(String state_cd, int off_cd, String regNo) {
        String flatFile = "";
        String sql = "Select flat_file  from " + TableList.VH_SMART_CARD
                + "   where state_cd = ? and off_cd = ? and regn_no in (?, rpad(?, 10, ?), ?) "
                + " order by moved_on desc limit 1 ";
        TransactionManagerReadOnly tmgr = null;
        SmartCardDobj ret_dobj = null;
        try {
            tmgr = new TransactionManagerReadOnly("SmartCardImpl.getSmartCardFlatFileName");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.setString(3, regNo);
            ps.setString(4, regNo);
            ps.setString(5, " ");
            ps.setString(6, ServerUtil.getRegnNoWithSpace(regNo));
            RowSet rsSm = tmgr.fetchDetachedRowSet();
            SmartCardDobj dobj = null;

            if (rsSm.next()) {
                flatFile = rsSm.getString("flat_file");
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

        return flatFile;
    }

    public static List<SmartCardDobj> getSmartCardOldListFilewise(String state_cd, int off_cd, String fileName) {

        List<SmartCardDobj> retList = new ArrayList<>();
        ArrayList listSmartCard = null;
        TransactionManagerReadOnly tmgr = null;
        SmartCardDobj ret_dobj = new SmartCardDobj();
        PreparedStatement ps = null;
        try {
            SmartCardDobj dobj = null;
            ret_dobj.setSmartCardFileName(fileName);
            listSmartCard = new ArrayList();
            String sql = "Select rc.*"
                    + " from " + TableList.VH_SMART_CARD + " vh," + TableList.RC_BE_TO_BO
                    + " rc where vh.flat_file =? and state_cd=? and off_cd=? and vh.appl_no=rc.rcpt_no";
            tmgr = new TransactionManagerReadOnly("SmartCardImpl.getSmartCardOldListFilewise");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, fileName);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSetWithoutTrim();
            while (rs.next()) {
                dobj = new SmartCardDobj();
                dobj.setVehregno(rs.getString("vehregno"));
                dobj.setRegdate(rs.getString("regdate"));
                dobj.setOwnername(rs.getString("ownername"));
                dobj.setFname(rs.getString("fname"));
                dobj.setCaddress(rs.getString("caddress"));
                dobj.setManufacturer(rs.getString("manufacturer"));
                dobj.setModelno(rs.getString("modelno"));
                dobj.setColour(rs.getString("colour"));
                dobj.setFuel(rs.getString("fuel"));
                dobj.setVehclass(rs.getString("vehclass"));
                dobj.setBodytype(rs.getString("bodytype"));
                dobj.setSeatcap(rs.getString("seatcap"));
                dobj.setStandcap(rs.getString("standcap"));
                dobj.setManufdate(rs.getString("manufdate"));
                dobj.setUnladenwt(rs.getString("unladenwt"));
                dobj.setCubiccap(rs.getString("cubiccap"));
                dobj.setWheelbase(rs.getString("wheelbase"));
                dobj.setNoofcylin(rs.getString("noofcylin"));
                dobj.setOwnerserial(rs.getString("ownerserial"));
                dobj.setChasisno(rs.getString("chasisno"));
                dobj.setEngineno(rs.getString("engineno"));
                dobj.setTaxpaidupto(rs.getString("taxpaidupto"));
                dobj.setRegnvalidity(rs.getString("regnvalidity"));
                dobj.setApprovingauth(rs.getString("approvingauth"));
                dobj.setFinname(rs.getString("finname"));
                dobj.setFinaddress(rs.getString("finaddress"));
                dobj.setHypofrom(rs.getString("hypofrom"));
                dobj.setHypoto(rs.getString("hypoto"));
                dobj.setNocno(rs.getString("nocno"));
                dobj.setStateto(rs.getString("stateto"));
                dobj.setRtoto(rs.getString("rtoto"));
                dobj.setNcrbclearno(rs.getString("ncrbclearno"));
                dobj.setNocissuedt(rs.getString("nocissuedt"));
                dobj.setInscompname(rs.getString("inscompname"));
                dobj.setCoverpolicyno(rs.getString("coverpolicyno"));
                dobj.setInstype(rs.getString("instype"));
                dobj.setInsvalidupto(rs.getString("insvalidupto"));
                dobj.setPuccentercode(rs.getString("puccentercode"));
                dobj.setPucvalidupto(rs.getString("pucvalidupto"));
                dobj.setTaxamount(rs.getString("taxamount"));
                dobj.setFine(rs.getString("fine"));
                dobj.setExemptrecptno(rs.getString("exemptrecptno"));
                dobj.setPaymentdt(rs.getString("paymentdt"));
                dobj.setTaxvalidfrom(rs.getString("taxvalidfrom"));
                dobj.setTaxvalidto(rs.getString("taxvalidto"));
                dobj.setExemption(rs.getString("exemption"));
                dobj.setDrtocode(rs.getString("drtocode"));
                dobj.setBuflag(rs.getString("buflag"));
                dobj.setFitvalidupto(rs.getString("fitvalidupto"));
                dobj.setFitinsofficer(rs.getString("fitinsofficer"));
                dobj.setFitlocation(rs.getString("fitlocation"));
                dobj.setGrossvehwt(rs.getString("grossvehwt"));
                dobj.setSemitrailers(rs.getString("semitrailers"));
                dobj.setTyreinfo(rs.getString("tyreinfo"));
                dobj.setAxleinfo(rs.getString("axleinfo"));
                dobj.setRcpt_no(rs.getString("rcpt_no"));
                dobj.setPur_cd(rs.getString("pur_cd"));
                dobj.setDeal_cd(rs.getString("deal_cd"));
                dobj.setOp_dt(rs.getTimestamp("op_dt"));
                dobj.setStatus(rs.getString("status"));
                //dobj.setCur_date(rs.getString("cdate"));
                dobj.setState_cd(state_cd);
                dobj.setOff_cd(off_cd);
                listSmartCard.add(dobj);
            }
            ret_dobj.setListSmartCard(listSmartCard);
            retList.add(ret_dobj);
            //}
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
        return retList;
    }

    public static SmartCardDobj saveDownLoadInfo(String state_cd, int off_cd, SmartCardDobj dobj, int offset, int limit) throws Exception {
        SmartCardDobj ret_dobj = new SmartCardDobj();
        TransactionManager tmgr = null;
        try {
            TmConfigurationDobj tmConfig = Util.getTmConfiguration();
            tmgr = new TransactionManager("saveDownLoadInfo");
            boolean isHsrp = verifyForHsrp(Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), tmgr);
            if (tmConfig.isRc_after_hsrp() && isHsrp) {
                ret_dobj.setListHsrpPending(checkAndUpdatePendingHsrp(state_cd, off_cd, tmgr, dobj));
                tmgr.commit();
            } else {
                saveDownLoadInfoWithoutHsrp(state_cd, off_cd, dobj);
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in generating flat file.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return ret_dobj;
    }

    public static void saveDownLoadInfoWithoutHsrp(String state_cd, int off_cd, SmartCardDobj dobj) throws Exception {

        boolean dataInserted = false;
        PreparedStatement ps = null;

        TransactionManager tmgr = null;
        List<SmartCardDobj> pendingApplList = new ArrayList<>();
        try {
            tmgr = new TransactionManager("saveDownLoadInfoWithoutHsrp");

            for (SmartCardDobj list : dobj.getListSmartCard()) {

                dataInserted = true;
                String sql = "INSERT INTO " + TableList.VH_SMART_CARD
                        + "   Select state_cd, off_cd, appl_no, regn_no, pur_cd, user_cd, op_dt, ?,"
                        + " current_timestamp, ? from " + TableList.VA_SMART_CARD
                        + " where op_dt< to_timestamp( ?,'dd_Mon_yyyy_hh24_mi_ss') and "
                        + " state_cd=? and off_cd=?"
                        + " and appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getSmartCardFileName());
                ps.setString(2, Util.getEmpCode());
                ps.setString(3, dobj.getCur_date());
                ps.setString(4, state_cd);
                ps.setInt(5, off_cd);
                ps.setString(6, list.getRcpt_no());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                sql = "Delete from " + TableList.VA_SMART_CARD
                        + " where op_dt< to_timestamp( ?,'dd_Mon_yyyy_hh24_mi_ss') and "
                        + " state_cd=? and off_cd=? and appl_no = ? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getCur_date());
                ps.setString(2, state_cd);
                ps.setInt(3, off_cd);
                ps.setString(4, list.getRcpt_no());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

            }
            String sql = "Insert into " + TableList.VT_SC_FLATFILE
                    + " (state_cd, off_cd, flat_file, op_dt) values(?,?,?,current_timestamp)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.setString(3, dobj.getSmartCardFileName());
            ps.executeUpdate();

            tmgr.commit();
        } catch (Exception e) {
            throw e;
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

    public static boolean isSmartCardFeePaid(String regnNo, TransactionManager tmgr) throws VahanException {
        Exception e = null;
        boolean smartFeePaid = false;
        try {
            String sql = "select * from vt_fee where regn_no=? and pur_cd=? and state_cd=? and off_cd=? ";

            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setInt(2, TableConstants.VM_TRANSACTION_MAST_SMART_CARD_FEE);
            ps.setString(3, Util.getUserStateCode());
            ps.setInt(4, Util.getSelectedSeat().getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                smartFeePaid = true;
            }
        } catch (SQLException ex) {
            e = ex;
        }

        if (e != null) {
            throw new VahanException("Error in Fetching SmartCard Fee Details");
        }

        return smartFeePaid;
    }

    public static boolean getSmartCardFeePaid(String applNo, TransactionManager tmgr) throws VahanException {
        Exception e = null;
        boolean smartFeePaid = false;
        try {
            String sql = "SELECT * from get_appl_rcpt_details(?) where pur_cd =? ";

            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setInt(2, TableConstants.VM_TRANSACTION_MAST_SMART_CARD_FEE);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                smartFeePaid = true;
            }
        } catch (SQLException ex) {
            e = ex;
        }

        if (e != null) {
            throw new VahanException("Error in Fetching SmartCard Fee Details");
        }

        return smartFeePaid;
    }

    public static String saveIPDetails(String ipAdrress) {
        String status = "";
        boolean flag = false;
        PreparedStatement ps = null;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        ResultSet rs = null;
        String sql = "INSERT INTO " + TableList.VM_SMART_CARD_IP_ALLOWED + " ( "
                + "            user_cd, request_server_ip4) "
                + "    VALUES (?, ?)";

        try {

            String checkIPquery = "select user_cd from " + TableList.VM_SMART_CARD_IP_ALLOWED + " where user_cd=? and request_server_ip4=?";
            tmgr = new TransactionManager("saveIPDetails");
            ps = tmgr.prepareStatement(checkIPquery);
            ps.setLong(1, Long.parseLong(Util.getEmpCode()));
            ps.setString(2, ipAdrress);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                flag = true;
                status = "REPEAT";
            }
            if (!flag) {
                psmt = tmgr.prepareStatement(sql);
                psmt.setLong(1, Long.parseLong(Util.getEmpCode()));
                psmt.setString(2, ipAdrress);
                int i = psmt.executeUpdate();
                if (i > 0) {
                    status = "SUCCESS";
                } else {
                    status = "FAIL";
                }
            }
            tmgr.commit();
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
        return status;
    }

    public static List<SmartCardIpDobj> getIpList(String empCd) {
        TransactionManagerReadOnly tmgr = null;
        List<SmartCardIpDobj> list = new ArrayList<SmartCardIpDobj>();
        PreparedStatement psmt = null;

        String sql = "SELECT request_server_ip4  FROM " + TableList.VM_SMART_CARD_IP_ALLOWED + " where user_cd=? ";
        try {
            tmgr = new TransactionManagerReadOnly("SmartCardImpl.getIpList");
            psmt = tmgr.prepareStatement(sql);
            psmt.setLong(1, Long.parseLong(Util.getEmpCode()));
            RowSet rs = tmgr.fetchDetachedRowSet();
            int i = 1;
            while (rs.next()) {
                SmartCardIpDobj dobj = new SmartCardIpDobj();
                dobj.setIp((rs.getString("request_server_ip4")));


                list.add(dobj);
            }
        } catch (SQLException ex) {
            LOGGER.error("Sql Exception Occured in fetching Ip List- ", ex);
        } finally {

            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (VahanException vahanException) {
                LOGGER.error("Sql Exception Occured in fetching Ip list-", vahanException);
            } catch (Exception ex) {
                LOGGER.error("Sql Exception Occured in fetching Ip List- ", ex);
            }
        }
        return list;
    }

    public static String deleteIPDetails(String ipAdrress) {
        String status = "";
        TransactionManager tmgr = null;
        String sql = "Insert into " + TableList.VHM_SMART_CARD_IP_ALLOWED
                + " SELECT user_cd, request_server_ip4, op_dt , ? , current_timestamp"
                + "  FROM " + TableList.VM_SMART_CARD_IP_ALLOWED
                + " where user_cd=? and request_server_ip4=? ";

        try {

            tmgr = new TransactionManager("deleteIPDetails");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setLong(2, Long.parseLong(Util.getEmpCode()));
            ps.setString(3, ipAdrress);

            ps.executeUpdate();
            ps = null;
            sql = "Delete from " + TableList.VM_SMART_CARD_IP_ALLOWED
                    + " where user_cd=? and request_server_ip4=?";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(Util.getEmpCode()));
            ps.setString(2, ipAdrress);
            ps.executeUpdate();
            tmgr.commit();
            status = "SUCCESS";
        } catch (Exception ex) {
            LOGGER.error("Sql Exception Occured in deleting Ip- ", ex);
            status = "FAIL";
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

    public static boolean isSmartCard(String state_cd, int officeCode) {
        TransactionManager tmgr = null;
        String whereiam = "SmartCardImpl.isSmartCard";
        boolean isSmart = false;
        try {
            tmgr = new TransactionManager(whereiam);
            isSmart = ServerUtil.verifyForSmartCard(Util.getUserStateCode(), officeCode, tmgr);

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
        return isSmart;
    }

    public DeleteSmartcardFlatFileDobj getGeneratedFlatFileRecord(String regnNo) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        DeleteSmartcardFlatFileDobj dobj = null;
        try {
            tmgr = new TransactionManagerReadOnly("SmartCardImpl.getGeneratedFlatFileRecord");
            sql = "select to_char(op_dt::date,'dd-Mon-yyyy') as op_date,vehregno,rcpt_no,op_dt from " + TableList.RC_BE_TO_BO + " where vehregno in (?, rpad(?, 10, ?), ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, regnNo);
            ps.setString(3, " ");
            ps.setString(4, ServerUtil.getRegnNoWithSpace(regnNo));
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new DeleteSmartcardFlatFileDobj();
                dobj.setGenrated_on(rs.getString("op_date"));
                dobj.setRegn_no(rs.getString("vehregno").replaceAll(" ", ""));
                dobj.setRcpt_no(rs.getString("rcpt_no"));
                dobj.setOp_dt(rs.getDate("op_dt"));
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in getting Flat File Details");
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

    public Date getOfficeStartDate(String regn_no, String state_cd, int off_cd) throws VahanException {
        Date start_dt = null;
        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("SmartCardImpl.getOfficeStartDate");
            sql = "select vow4 from " + TableList.TM_OFFICE
                    + " t left join " + TableList.VT_OWNER + " o on o.off_cd=t.off_cd  and o.state_cd=t.state_cd"
                    + " where o.regn_no=? and o.state_cd=? and o.off_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                start_dt = rs.getDate("vow4");
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during getting record, Please Contact to the System Administrator");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return start_dt;
    }

    public void updateSmartCardPendingDetails(TransactionManager tmgr, Owner_dobj ownerDobj,
            SmartCardDobj smartCardDobj, String empCd, TmConfigurationDobj tmConfigurationDobj) throws VahanException {
        try {

            if (smartCardDobj == null) {
                return;
            }

            PreparedStatement ps = null;
            String sql = null;
            fillSmartCardDobj(tmgr, smartCardDobj);
            int i = 1;
            if (smartCardDobj.getState_cd() != null && smartCardDobj.getOff_cd() > 0
                    && ServerUtil.verifyForHsrp(smartCardDobj.getState_cd(), smartCardDobj.getOff_cd(), tmgr)
                    && tmConfigurationDobj != null && tmConfigurationDobj.isRc_after_hsrp()) {

                sql = "UPDATE smartcard.va_smart_card_temp"
                        + "   SET regdate=?, ownername=?, fname=?, "
                        + "       caddress=?, manufacturer=?, modelno=?, colour=?, fuel=?, vehclass=?, "
                        + "       bodytype=?, seatcap=?, standcap=?, manufdate=?, unladenwt=?, "
                        + "       cubiccap=?, wheelbase=?, noofcylin=?, ownerserial=?, chasisno=?, "
                        + "       engineno=?, regnvalidity=?, finname=?, "
                        + "       finaddress=?, hypofrom=?, hypoto=?, nocno=?, stateto=?, rtoto=?, "
                        + "       ncrbclearno=?, nocissuedt=?, inscompname=?, coverpolicyno=?, "
                        + "       instype=?, insvalidupto=?, puccentercode=?, pucvalidupto=?, taxamount=?, "
                        + "       fine=?, exemptrecptno=?, paymentdt=?, taxvalidfrom=?, taxvalidto=?, "
                        + "       exemption=?, drtocode=?, buflag=?, fitvalidupto=?, fitinsofficer=?, "
                        + "       fitlocation=?, grossvehwt=?, semitrailers=?, tyreinfo=?, axleinfo=?, "
                        + "       deal_cd=?, op_dt=?, status=?, flat_file=?"
                        + " WHERE state_cd=? and off_cd=? and vehregno=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(i++, smartCardDobj.getRegdate());
                ps.setString(i++, smartCardDobj.getOwnername());
                ps.setString(i++, smartCardDobj.getFname());
                ps.setString(i++, smartCardDobj.getCaddress());
                ps.setString(i++, smartCardDobj.getManufacturer());
                ps.setString(i++, smartCardDobj.getModelno());
                ps.setString(i++, smartCardDobj.getColour());
                ps.setString(i++, smartCardDobj.getFuel());
                ps.setString(i++, smartCardDobj.getVehclass());
                ps.setString(i++, smartCardDobj.getBodytype());
                ps.setString(i++, smartCardDobj.getSeatcap());
                ps.setString(i++, smartCardDobj.getStandcap());
                ps.setString(i++, smartCardDobj.getManufdate());
                ps.setString(i++, smartCardDobj.getUnladenwt());
                ps.setString(i++, smartCardDobj.getCubiccap());
                ps.setString(i++, smartCardDobj.getWheelbase());
                ps.setString(i++, smartCardDobj.getNoofcylin());
                ps.setString(i++, smartCardDobj.getOwnerserial());
                ps.setString(i++, smartCardDobj.getChasisno());
                ps.setString(i++, smartCardDobj.getEngineno());
                ps.setString(i++, smartCardDobj.getRegnvalidity());
                ps.setString(i++, smartCardDobj.getFinname());
                ps.setString(i++, smartCardDobj.getFinaddress());
                ps.setString(i++, smartCardDobj.getHypofrom());
                ps.setString(i++, smartCardDobj.getHypoto());
                ps.setString(i++, smartCardDobj.getNocno());
                ps.setString(i++, smartCardDobj.getStateto());
                ps.setString(i++, smartCardDobj.getRtoto());
                ps.setString(i++, smartCardDobj.getNcrbclearno());
                ps.setString(i++, smartCardDobj.getNocissuedt());
                ps.setString(i++, smartCardDobj.getInscompname());
                ps.setString(i++, smartCardDobj.getCoverpolicyno());
                ps.setString(i++, smartCardDobj.getInstype());
                ps.setString(i++, smartCardDobj.getInsvalidupto());
                ps.setString(i++, smartCardDobj.getPuccentercode());
                ps.setString(i++, smartCardDobj.getPucvalidupto());
                ps.setString(i++, smartCardDobj.getTaxamount());
                ps.setString(i++, smartCardDobj.getFine());
                ps.setString(i++, smartCardDobj.getExemptrecptno());
                ps.setString(i++, smartCardDobj.getPaymentdt());
                ps.setString(i++, smartCardDobj.getTaxvalidfrom());
                ps.setString(i++, smartCardDobj.getTaxvalidto());
                ps.setString(i++, smartCardDobj.getExemption());
                ps.setString(i++, smartCardDobj.getDrtocode());
                ps.setString(i++, smartCardDobj.getBuflag());
                ps.setString(i++, smartCardDobj.getFitvalidupto());
                ps.setString(i++, smartCardDobj.getFitinsofficer());
                ps.setString(i++, smartCardDobj.getFitlocation());
                ps.setString(i++, smartCardDobj.getGrossvehwt());
                ps.setString(i++, smartCardDobj.getSemitrailers());
                ps.setString(i++, smartCardDobj.getTyreinfo());
                ps.setString(i++, smartCardDobj.getAxleinfo());
                ps.setString(i++, smartCardDobj.getDeal_cd());
                ps.setTimestamp(i++, smartCardDobj.getOp_dt());
                ps.setString(i++, smartCardDobj.getStatus());
                ps.setString(i++, smartCardDobj.getFlat_file());
                ps.setString(i++, smartCardDobj.getState_cd());
                ps.setInt(i++, smartCardDobj.getOff_cd());
                ps.setString(i++, smartCardDobj.getVehregno());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
            } else {
                sql = "UPDATE smartcard.smart_card"
                        + "   SET regdate=?, ownername=?, fname=?, caddress=?, manufacturer=?, "
                        + "       modelno=?, colour=?, fuel=?, vehclass=?, bodytype=?, seatcap=?, "
                        + "       standcap=?, manufdate=?, unladenwt=?, cubiccap=?, wheelbase=?, "
                        + "       noofcylin=?, ownerserial=?, chasisno=?, engineno=?, "
                        + "       regnvalidity=?, finname=?, finaddress=?, hypofrom=?,"
                        + "       hypoto=?, nocno=?, stateto=?, rtoto=?, ncrbclearno=?, nocissuedt=?, "
                        + "       inscompname=?, coverpolicyno=?, instype=?, insvalidupto=?, puccentercode=?, "
                        + "       pucvalidupto=?, taxamount=?, fine=?, exemptrecptno=?, paymentdt=?, "
                        + "       taxvalidfrom=?, taxvalidto=?, exemption=?, drtocode=?, buflag=?, "
                        + "       fitvalidupto=?, fitinsofficer=?, fitlocation=?, grossvehwt=?, "
                        + "       semitrailers=?, tyreinfo=?, axleinfo=?,  "
                        + "       deal_cd=?, op_dt=?, status=?, flat_file=?"
                        + " WHERE (vehregno,rcpt_no) in (select vehregno,rcpt_no from smartcard.smart_card where vehregno=? order by op_dt desc limit 1)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(i++, smartCardDobj.getRegdate());
                ps.setString(i++, smartCardDobj.getOwnername());
                ps.setString(i++, smartCardDobj.getFname());
                ps.setString(i++, smartCardDobj.getCaddress());
                ps.setString(i++, smartCardDobj.getManufacturer());
                ps.setString(i++, smartCardDobj.getModelno());
                ps.setString(i++, smartCardDobj.getColour());
                ps.setString(i++, smartCardDobj.getFuel());
                ps.setString(i++, smartCardDobj.getVehclass());
                ps.setString(i++, smartCardDobj.getBodytype());
                ps.setString(i++, smartCardDobj.getSeatcap());
                ps.setString(i++, smartCardDobj.getStandcap());
                ps.setString(i++, smartCardDobj.getManufdate());
                ps.setString(i++, smartCardDobj.getUnladenwt());
                ps.setString(i++, smartCardDobj.getCubiccap());
                ps.setString(i++, smartCardDobj.getWheelbase());
                ps.setString(i++, smartCardDobj.getNoofcylin());
                ps.setString(i++, smartCardDobj.getOwnerserial());
                ps.setString(i++, smartCardDobj.getChasisno());
                ps.setString(i++, smartCardDobj.getEngineno());
                ps.setString(i++, smartCardDobj.getRegnvalidity());
                ps.setString(i++, smartCardDobj.getFinname());
                ps.setString(i++, smartCardDobj.getFinaddress());
                ps.setString(i++, smartCardDobj.getHypofrom());
                ps.setString(i++, smartCardDobj.getHypoto());
                ps.setString(i++, smartCardDobj.getNocno());
                ps.setString(i++, smartCardDobj.getStateto());
                ps.setString(i++, smartCardDobj.getRtoto());
                ps.setString(i++, smartCardDobj.getNcrbclearno());
                ps.setString(i++, smartCardDobj.getNocissuedt());
                ps.setString(i++, smartCardDobj.getInscompname());
                ps.setString(i++, smartCardDobj.getCoverpolicyno());
                ps.setString(i++, smartCardDobj.getInstype());
                ps.setString(i++, smartCardDobj.getInsvalidupto());
                ps.setString(i++, smartCardDobj.getPuccentercode());
                ps.setString(i++, smartCardDobj.getPucvalidupto());
                ps.setString(i++, smartCardDobj.getTaxamount());
                ps.setString(i++, smartCardDobj.getFine());
                ps.setString(i++, smartCardDobj.getExemptrecptno());
                ps.setString(i++, smartCardDobj.getPaymentdt());
                ps.setString(i++, smartCardDobj.getTaxvalidfrom());
                ps.setString(i++, smartCardDobj.getTaxvalidto());
                ps.setString(i++, smartCardDobj.getExemption());
                ps.setString(i++, smartCardDobj.getDrtocode());
                ps.setString(i++, smartCardDobj.getBuflag());
                ps.setString(i++, smartCardDobj.getFitvalidupto());
                ps.setString(i++, smartCardDobj.getFitinsofficer());
                ps.setString(i++, smartCardDobj.getFitlocation());
                ps.setString(i++, smartCardDobj.getGrossvehwt());
                ps.setString(i++, smartCardDobj.getSemitrailers());
                ps.setString(i++, smartCardDobj.getTyreinfo());
                ps.setString(i++, smartCardDobj.getAxleinfo());
                ps.setString(i++, smartCardDobj.getDeal_cd());
                ps.setTimestamp(i++, smartCardDobj.getOp_dt());
                ps.setString(i++, smartCardDobj.getStatus());
                ps.setString(i++, smartCardDobj.getFlat_file());
                ps.setString(i++, smartCardDobj.getVehregno());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                sql = "INSERT INTO  " + TableList.VH_RC_BE_TO_BO
                        + " SELECT ?,current_timestamp as moved_on,? as moved_by,a.* FROM " + TableList.RC_BE_TO_BO
                        + " a WHERE vehregno = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, smartCardDobj.getReason());
                ps.setString(2, empCd);
                ps.setString(3, smartCardDobj.getVehregno());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                sql = "DELETE FROM " + TableList.RC_BE_TO_BO + " WHERE vehregno = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, smartCardDobj.getVehregno());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                sql = "INSERT INTO  " + TableList.RC_BE_TO_BO
                        + "    SELECT regexp_replace(vehregno, '[\\\"'';`]', ' ','g'), regdate, regexp_replace(ownername, '[\"'';`]', ' ','g'), regexp_replace(fname, '[\"'';`]', ' ','g'), "
                        + "       regexp_replace(caddress, '[\"'';`]', ' ','g'), regexp_replace(manufacturer, '[\\\"'';`]', ' ','g'), "
                        + "       regexp_replace(modelno, '[\"'';`]', ' ','g'), regexp_replace(colour, '[\\\"'';`]', ' ','g'), regexp_replace(fuel, '[\\\"'';`]', ' ','g'), regexp_replace(vehclass, '[\\\"'';`]', ' ','g'), regexp_replace(bodytype, '[\"'';`]', ' ','g'), seatcap, standcap, "
                        + "       manufdate, unladenwt, cubiccap, wheelbase, noofcylin, ownerserial, "
                        + "       regexp_replace(chasisno, '[\\\"'';`]', ' ','g'), regexp_replace(engineno, '[\\\"'';`]', ' ','g'), taxpaidupto, regnvalidity, regexp_replace(approvingauth, '[\\\"'';`]', ' ','g'), "
                        + "       regexp_replace(finname, '[\"'';`]', ' ','g'), regexp_replace(finaddress, '[\"'';`]', ' ','g'),"
                        + "       hypofrom, hypoto, regexp_replace(nocno, '[\\\"'';`]', ' ','g'), stateto, rtoto, "
                        + "       regexp_replace(ncrbclearno, '[\\\"'';`]', ' ','g'), nocissuedt, inscompname, regexp_replace(coverpolicyno, '[\"'';`\t]', ' ','g'), instype, "
                        + "       insvalidupto, puccentercode, pucvalidupto, taxamount, fine, regexp_replace(exemptrecptno, '[\\\"'';`]', ' ','g'), "
                        + "       paymentdt, taxvalidfrom, taxvalidto, exemption, drtocode, buflag, "
                        + "       fitvalidupto, fitinsofficer, fitlocation, grossvehwt, semitrailers, "
                        + "       tyreinfo, axleinfo, rcpt_no, pur_cd, deal_cd, op_dt, status from " + TableList.SMART_CARD
                        + " where vehregno=? order by op_dt desc limit 1";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, smartCardDobj.getVehregno());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
            }

        } catch (VahanException vme) {
            throw vme;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Updating Smart Card Details, Please Contact to the System Administrator.");
        }
    }

    private void fillSmartCardDobj(TransactionManager tmgr, SmartCardDobj smartCardDobj) throws SQLException {
        String sql = "SELECT regexp_replace(vehregno, '[\\\"'';`]', ' ','g') as vehregno, regdate, regexp_replace(ownername, '[\"'';`]', ' ','g') as ownername, regexp_replace(fname, '[\"'';`]', ' ','g') as fname, "
                + "       regexp_replace(caddress, '[\"'';`]', ' ','g') as caddress, regexp_replace(manufacturer, '[\\\"'';`]', ' ','g') as manufacturer, "
                + "       regexp_replace(modelno, '[\"'';`]', ' ','g') as modelno, regexp_replace(colour, '[\\\"'';`]', ' ','g') as colour, regexp_replace(fuel, '[\\\"'';`]', ' ','g') as fuel, regexp_replace(vehclass, '[\\\"'';`]', ' ','g') as vehclass, regexp_replace(bodytype, '[\"'';`]', ' ','g') as bodytype, seatcap, standcap, "
                + "       manufdate, unladenwt, cubiccap, wheelbase, noofcylin, ownerserial, "
                + "       regexp_replace(chasisno, '[\\\"'';`]', ' ','g') as chasisno, regexp_replace(engineno, '[\\\"'';`]', ' ','g') as engineno, taxpaidupto, regnvalidity, regexp_replace(approvingauth, '[\\\"'';`]', ' ','g') as approvingauth, "
                + "       regexp_replace(finname, '[\"'';`]', ' ','g') as finname, regexp_replace(finaddress, '[\"'';`]', ' ','g') as finaddress,"
                + "       hypofrom, hypoto, regexp_replace(nocno, '[\\\"'';`]', ' ','g') as nocno, stateto, rtoto, "
                + "       regexp_replace(ncrbclearno, '[\\\"'';`]', ' ','g') as ncrbclearno, nocissuedt, inscompname, regexp_replace(coverpolicyno, '[\"'';`\t]', ' ','g') as coverpolicyno, instype, "
                + "       insvalidupto, puccentercode, pucvalidupto, taxamount, fine, regexp_replace(exemptrecptno, '[\\\"'';`]', ' ','g') as exemptrecptno, "
                + "       paymentdt, taxvalidfrom, taxvalidto, exemption, drtocode, buflag, "
                + "       fitvalidupto, fitinsofficer, fitlocation, grossvehwt, semitrailers, "
                + "       tyreinfo, axleinfo,status, flat_file"
                + "  from getSmartCardDetails(?)";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, smartCardDobj.getVehregno());
        RowSet rs = tmgr.fetchDetachedRowSetWithoutTrim_No_release();
        if (rs.next()) {
            smartCardDobj.setVehregno(rs.getString("vehregno"));//regdate, 
            smartCardDobj.setRegdate(rs.getString("regdate"));//regdate, 
            smartCardDobj.setOwnername(rs.getString("ownername"));//ownername, 
            smartCardDobj.setFname(rs.getString("fname"));//fname, 
            smartCardDobj.setCaddress(rs.getString("caddress"));//caddress, 
            smartCardDobj.setManufacturer(rs.getString("manufacturer"));//manufacturer, 
            smartCardDobj.setModelno(rs.getString("modelno"));//modelno,
            smartCardDobj.setColour(rs.getString("colour"));//colour,
            smartCardDobj.setFuel(rs.getString("fuel"));//fuel,
            smartCardDobj.setVehclass(rs.getString("vehclass"));//vehclass,
            smartCardDobj.setBodytype(rs.getString("bodytype"));//bodytype,
            smartCardDobj.setSeatcap(rs.getString("seatcap"));//seatcap, 
            smartCardDobj.setStandcap(rs.getString("standcap"));//standcap,
            smartCardDobj.setManufdate(rs.getString("manufdate"));//manufdate, 
            smartCardDobj.setUnladenwt(rs.getString("unladenwt"));//unladenwt,
            smartCardDobj.setCubiccap(rs.getString("cubiccap"));//cubiccap,
            smartCardDobj.setWheelbase(rs.getString("wheelbase"));//wheelbase,
            smartCardDobj.setNoofcylin(rs.getString("noofcylin"));//noofcylin, 
            smartCardDobj.setOwnerserial(rs.getString("ownerserial"));//ownerserial, 
            smartCardDobj.setChasisno(rs.getString("chasisno"));//chasisno,
            smartCardDobj.setEngineno(rs.getString("engineno"));//engineno,
            smartCardDobj.setTaxpaidupto(rs.getString("taxpaidupto"));//taxpaidupto, 
            smartCardDobj.setRegnvalidity(rs.getString("regnvalidity"));//regnvalidity,
            smartCardDobj.setApprovingauth(rs.getString("approvingauth"));//approvingauth, 
            smartCardDobj.setFinname(rs.getString("finname"));//finname,
            smartCardDobj.setFinaddress(rs.getString("finaddress"));//finaddress,
            smartCardDobj.setHypofrom(rs.getString("hypofrom"));//hypofrom, 
            smartCardDobj.setHypoto(rs.getString("hypoto"));//hypoto,
            smartCardDobj.setNocno(rs.getString("nocno"));//nocno, 
            smartCardDobj.setStateto(rs.getString("stateto"));//stateto, 
            smartCardDobj.setRtoto(rs.getString("rtoto"));//rtoto, 
            smartCardDobj.setNcrbclearno(rs.getString("ncrbclearno"));//ncrbclearno,
            smartCardDobj.setNocissuedt(rs.getString("nocissuedt"));//nocissuedt,
            smartCardDobj.setInscompname(rs.getString("inscompname"));//inscompname,
            smartCardDobj.setCoverpolicyno(rs.getString("coverpolicyno"));//coverpolicyno,
            smartCardDobj.setInstype(rs.getString("instype"));//instype, 
            smartCardDobj.setInsvalidupto(rs.getString("insvalidupto"));//insvalidupto, 
            smartCardDobj.setPuccentercode(rs.getString("puccentercode"));//puccentercode,
            smartCardDobj.setPucvalidupto(rs.getString("pucvalidupto"));//pucvalidupto, 
            smartCardDobj.setTaxamount(rs.getString("taxamount"));//taxamount,
            smartCardDobj.setFine(rs.getString("fine"));//fine,
            smartCardDobj.setExemptrecptno(rs.getString("exemptrecptno"));//exemptrecptno, 
            smartCardDobj.setPaymentdt(rs.getString("paymentdt"));//paymentdt,
            smartCardDobj.setTaxvalidfrom(rs.getString("taxvalidfrom"));//taxvalidfrom,
            smartCardDobj.setTaxvalidto(rs.getString("taxvalidto"));//taxvalidto,
            smartCardDobj.setExemption(rs.getString("exemption"));//exemption, 
            smartCardDobj.setDrtocode(rs.getString("drtocode"));//drtocode, 
            smartCardDobj.setBuflag(rs.getString("buflag"));//buflag, 
            smartCardDobj.setFitvalidupto(rs.getString("fitvalidupto"));//fitvalidupto,
            smartCardDobj.setFitinsofficer(rs.getString("fitinsofficer"));//fitinsofficer, 
            smartCardDobj.setFitlocation(rs.getString("fitlocation"));//fitlocation,
            smartCardDobj.setGrossvehwt(rs.getString("grossvehwt"));//grossvehwt,
            smartCardDobj.setSemitrailers(rs.getString("semitrailers"));//semitrailers, 
            smartCardDobj.setTyreinfo(rs.getString("tyreinfo"));//tyreinfo, 
            smartCardDobj.setAxleinfo(rs.getString("axleinfo"));//axleinfo,                                
            smartCardDobj.setStatus(rs.getString("status"));//status, 
            smartCardDobj.setFlat_file(rs.getString("flat_file"));//flat_file
        }
    }

    public void update_RCtoSmartCardConversion_Status(Status_dobj status_dobj, TmConfigurationDobj tmConfig) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("update_RCtoSmartCardConversion_Status");
            status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
            ServerUtil.updateApprovedStatus(tmgr, status_dobj);
            //SmartCard Or Print
            ServerUtil.VerifyInsertSmartCardPrintDetail(status_dobj.getAppl_no(), status_dobj.getRegn_no(),
                    status_dobj.getState_cd(), status_dobj.getOff_cd(), status_dobj.getPur_cd(), tmgr);
            //for updating the status of application when it is approved start                   
            ApplicationInwardImpl applicationInwardImpl = new ApplicationInwardImpl();
            applicationInwardImpl.moveToVaStatusHistory(tmgr, status_dobj);
            applicationInwardImpl.deleteVaStatus(tmgr, status_dobj.getAppl_no(), status_dobj.getPur_cd());
            //for updating the status of application when it is approved end
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
    }

    public static boolean isOldApplication(String appl_no) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        boolean isOldApplication = true;
        try {
            tmgr = new TransactionManagerReadOnly("isOldApplication");
            sql = "select * from " + TableList.VA_DETAILS + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isOldApplication = false;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during getting record, Please Contact to the System Administrator");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return isOldApplication;
    }

    public boolean checkOfficeForUser(String regn_no, String stateCodeSelected, int offCodeSelected) throws VahanException {
        boolean userFlag = false;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("checkOfficeForUser");
            String sql = "select * from " + TableList.VT_OWNER + " where regn_no=? and off_cd=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setInt(2, offCodeSelected);
            ps.setString(3, stateCodeSelected);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                userFlag = true;
            } else {
                throw new VahanException("This Registration No is Not Found in this Office");
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
        return userFlag;
    }

    public String insertIntoVaDeleteFlatFile(Status_dobj status, DeleteSmartcardFlatFileDobj deleteSmartcardFlatFileDobj) throws VahanException {
        String appl_no = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        int action_cd = 0;
        int actionCodeArray[] = null;
        try {
            tmgr = new TransactionManager("insertIntoVaDeleteFlatFile");
            appl_no = ServerUtil.getUniqueApplNo(tmgr, status.getState_cd());//generate a new application no.
            sql = "INSERT INTO " + TableList.VA_REMOVE_RC_BE_TO_BO
                    + " (state_cd, off_cd, appl_no, regn_no, genrated_on, reason, op_dt)"
                    + "  VALUES (?, ?, ?, ?, ?, ?, current_timestamp)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, status.getState_cd());
            ps.setInt(2, status.getOff_cd());
            ps.setString(3, appl_no);
            ps.setString(4, deleteSmartcardFlatFileDobj.getRegn_no());
            ps.setDate(5, new java.sql.Date(deleteSmartcardFlatFileDobj.getOp_dt().getTime()));
            ps.setString(6, deleteSmartcardFlatFileDobj.getReason());
            ps.executeUpdate();

            actionCodeArray = ServerUtil.getInitialAction(tmgr, status.getState_cd(), status.getPur_cd(), null);
            if (actionCodeArray == null) {
                throw new VahanException("Initial Action Code is Not Available!");
            }

            action_cd = actionCodeArray[0];
            status.setAppl_no(appl_no);
            status.setAction_cd(action_cd);//Initial Action_cd
            status.setAppl_date(ServerUtil.getSystemDateInPostgres());
            deleteSmartcardFlatFileDobj.setAppl_no(appl_no);
            ServerUtil.insertIntoVaStatus(tmgr, status);
            ServerUtil.insertIntoVaDetails(tmgr, status);
            status.setStatus(TableConstants.STATUS_COMPLETE);
            status = ServerUtil.webServiceForNextStage(status, tmgr);
            ServerUtil.fileFlow(tmgr, status); //for updateing va_status and vha status for new role,seat for new emp

            tmgr.commit();//Commiting data here....
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error In Application Number Generation");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return appl_no;
    }

    public static void insertUpdateDeleteFlatFileData(TransactionManager tmgr, DeleteSmartcardFlatFileDobj deleteSmartcardFlatFileDobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "SELECT regn_no FROM " + TableList.VA_REMOVE_RC_BE_TO_BO + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, deleteSmartcardFlatFileDobj.getAppl_no());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                insertIntoDeleteFlatFileHistory(tmgr, deleteSmartcardFlatFileDobj);
                updateDeleteFlatFileData(tmgr, deleteSmartcardFlatFileDobj);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public static void insertIntoDeleteFlatFileHistory(TransactionManager tmgr, DeleteSmartcardFlatFileDobj deleteSmartcardFlatFileDobj) throws Exception {
        PreparedStatement ps = null;
        String sql = null;
        sql = "INSERT INTO " + TableList.VHA_REMOVE_RC_BE_TO_BO
                + " SELECT current_timestamp, ? ,* "
                + "  FROM  " + TableList.VA_REMOVE_RC_BE_TO_BO
                + " WHERE  appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, deleteSmartcardFlatFileDobj.getAppl_no());
        ps.executeUpdate();
    }

    public static void updateDeleteFlatFileData(TransactionManager tmgr, DeleteSmartcardFlatFileDobj deleteSmartcardFlatFileDobj) throws Exception {
        PreparedStatement ps = null;
        String sql = null;
        sql = "UPDATE " + TableList.VA_REMOVE_RC_BE_TO_BO
                + " SET reason=?"
                + " WHERE appl_no = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, deleteSmartcardFlatFileDobj.getReason());
        ps.setString(2, deleteSmartcardFlatFileDobj.getAppl_no());
        ps.executeUpdate();
    }

    public static void makeChangeDeleteFlatFileData(DeleteSmartcardFlatFileDobj deleteSmartcardFlatFileDobj, String changedata) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("makeChangeDeleteFlatFileData");
            insertUpdateDeleteFlatFileData(tmgr, deleteSmartcardFlatFileDobj);
            ComparisonBeanImpl.updateChangedData(deleteSmartcardFlatFileDobj.getAppl_no(), changedata, tmgr);
            tmgr.commit();
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
    }

    public void update_deleteFlatFile_Status(DeleteSmartcardFlatFileDobj deleteSmartcardFlatFileDobj, DeleteSmartcardFlatFileDobj deleteSmartcardFlatFileDobj_prev, Status_dobj statusDobj, String changeData) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        try {
            tmgr = new TransactionManager("update_deleteFlatFile_Status");
            statusDobj = ServerUtil.webServiceForNextStage(statusDobj, tmgr);

            if ((changeData != null && !changeData.equals("")) || deleteSmartcardFlatFileDobj_prev == null) {
                insertUpdateDeleteFlatFileData(tmgr, deleteSmartcardFlatFileDobj);
            }
            sql = "insert into " + TableList.RC_BE_TO_BO_DELETE
                    + " select *,?,current_timestamp as moved_on,? from " + TableList.RC_BE_TO_BO
                    + " where vehregno in (?, rpad(?, 10, ?), ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, deleteSmartcardFlatFileDobj.getReason());
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, deleteSmartcardFlatFileDobj.getRegn_no());
            ps.setString(4, deleteSmartcardFlatFileDobj.getRegn_no());
            ps.setString(5, " ");
            ps.setString(6, ServerUtil.getRegnNoWithSpace(deleteSmartcardFlatFileDobj.getRegn_no()));
            ps.executeUpdate();

            sql = "delete from " + TableList.RC_BE_TO_BO + " where vehregno in (?, rpad(?, 10, ?), ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, deleteSmartcardFlatFileDobj.getRegn_no());
            ps.setString(2, deleteSmartcardFlatFileDobj.getRegn_no());
            ps.setString(3, " ");
            ps.setString(4, ServerUtil.getRegnNoWithSpace(deleteSmartcardFlatFileDobj.getRegn_no()));
            ps.executeUpdate();

            sql = "INSERT INTO " + TableList.VHA_REMOVE_RC_BE_TO_BO
                    + " SELECT current_timestamp, ? ,* "
                    + "  FROM  " + TableList.VA_REMOVE_RC_BE_TO_BO
                    + " WHERE  appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, deleteSmartcardFlatFileDobj.getAppl_no());
            ps.executeUpdate();

            sql = "delete from " + TableList.VA_REMOVE_RC_BE_TO_BO + " where appl_no = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, deleteSmartcardFlatFileDobj.getAppl_no());
            ps.executeUpdate();

            //for updating the status of application when it is approved start
            statusDobj.setEntry_status(TableConstants.STATUS_APPROVED);
            ServerUtil.updateApprovedStatus(tmgr, statusDobj);
            //for updating the status of application when it is approved end

            insertIntoVhaChangedData(tmgr, deleteSmartcardFlatFileDobj.getAppl_no(), changeData);//for saving the data into table those are changed by the user

            ServerUtil.fileFlow(tmgr, statusDobj); //for updateing va_status and vha status for new role,seat for new emp

            tmgr.commit();//Commiting data here....
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error In Database Update");
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

    public DeleteSmartcardFlatFileDobj getVehicleDataFromVaDeleteFlatFile(String appl_no) throws Exception {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        DeleteSmartcardFlatFileDobj dobj = null;
        try {
            tmgr = new TransactionManager("getVehicleDataFromVaDeleteFlatFile");
            ps = tmgr.prepareStatement("select to_char(genrated_on::date,'dd-Mon-yyyy') as genrated_on,appl_no,regn_no,reason from " + TableList.VA_REMOVE_RC_BE_TO_BO + " where appl_no =?");
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new DeleteSmartcardFlatFileDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setReason(rs.getString("reason"));
                dobj.setGenrated_on(rs.getString("genrated_on"));
            }
            if (dobj == null) {
                throw new VahanException("No Record Found for this Application");
            }
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
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

    public void insertDeleteSmartcardDetails(TransactionManager tmgr, String regnNo, String reason) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "insert into " + TableList.RC_BE_TO_BO_DELETE
                    + " select *,?,current_timestamp as moved_on,? from " + TableList.RC_BE_TO_BO
                    + " where vehregno in (?, rpad(?, 10, ?), ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, reason);
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, regnNo);
            ps.setString(4, regnNo);
            ps.setString(5, " ");
            ps.setString(6, ServerUtil.getRegnNoWithSpace(regnNo));
            ps.executeUpdate();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error In Database Update");
        }
    }

    public static void insertIntoSmartCardTempDtls(String regnNo, String stateCd, int OffCd, String applNo, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        SmartcardTempDtlsDobj dobj = new SmartcardTempDtlsDobj();
        int vhType = 0;
        try {
            String sql = "Select * from f_smart_card_details(?,?,?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            ps.setInt(3, OffCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj.setRegnNo(rs.getString("o_regn_no"));
                dobj.setCardIssueDate(rs.getString("o_cid"));
                dobj.setRegnDtls(rs.getString("regn_dtls"));
                dobj.setOwnerDtls(rs.getString("owner_dtls"));
                dobj.setVehicleDtls(rs.getString("vehicle_dtls"));
                dobj.setAxleDtls(rs.getString("axle_dtls"));
                dobj.setTrailerDtls(rs.getString("trailer_dtls"));
                dobj.setLinkedVehDtls(rs.getString("linked_veh_dtls"));
                dobj.setHypthDtls(rs.getString("hypth_dtls"));
                dobj.setSemiTrailerDtls(rs.getString("semi_trailer_dtls"));
                dobj.setRetroFittingDtls(rs.getString("retro_fitting_dtls"));
                dobj.setChallanDtls(rs.getString("challan_dtls"));
                dobj.setPermitDtls(rs.getString("permit_dtls"));
            }

            int purCd = 0;// from va_details
            String purDesc = "";
            sql = " Select a.pur_cd,short_descr  from va_details a "
                    + " left join tm_purpose_mast b on a.pur_cd = b.pur_cd "
                    + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);

            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                purCd = rs.getInt("pur_cd");
                purDesc = rs.getString("short_descr");
            }
            OwnerImpl ownerImpl = new OwnerImpl();
            //Owner_dobj ownerDobj = ownerImpl.set_Owner_appl_db_to_dobj_with_state_off_cd(regnNo.trim(), null, "", 1);
            Owner_dobj ownerDobj = ownerImpl.set_Owner_appl_db_to_dobj_with_state_off_cd(regnNo.trim(), null, "", 1, tmgr);
            if (ownerDobj != null) {
                vhType = ownerDobj.getVehType();

            }
            ApplicationInwardImpl applicationInwardImpl = new ApplicationInwardImpl();
            TmConfigurationDobj tmConfig = Util.getTmConfiguration();
            boolean installmentNotPaid = checkFirstInstallmentPaidOrNot(tmgr, applNo, regnNo);
            String taxStatus = applicationInwardImpl.taxPaidStatusForSmartCard(regnNo.trim(), tmConfig, ownerDobj, purCd, tmgr);
            String qrURL = ServerUtil.getQRURL(regnNo, DocumentType.RC_QR, tmgr);

            sql = "INSERT INTO smartcard.va_smart_card_new("
                    + "            state_cd, off_cd, appl_no, regn_no, pur_cd,"
                    + " user_cd, op_dt, tax_upto ,tax_installment, regn_dtls, "
                    + " owner_dtls, vehicle_dtls, axle_dtls, trailer_dtls, linked_veh_dtls,"
                    + " hypth_dtls,challan_dtls,permit_dtls, semi_trailer_dtls,  retro_fitting_dtls,qr_url) "
                    + "    VALUES (?, ?, ?, ?, ?, ?, current_timestamp,?, ?, ?, ?, ?, ?,  "
                    + "            ?, ?, ?, ?, ? ,?,?,?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, OffCd);
            ps.setString(3, applNo);
            ps.setString(4, regnNo);
            ps.setString(5, purDesc);
            ps.setString(6, Util.getEmpCode());
            ps.setString(7, taxStatus);
            ps.setBoolean(8, installmentNotPaid);
            ps.setString(9, dobj.getRegnDtls());
            ps.setString(10, dobj.getOwnerDtls());
            ps.setString(11, dobj.getVehicleDtls());
            ps.setString(12, dobj.getAxleDtls());
            ps.setString(13, dobj.getTrailerDtls());
            ps.setString(14, dobj.getLinkedVehDtls());
            ps.setString(15, dobj.getHypthDtls());
            ps.setString(16, dobj.getChallanDtls());
            ps.setString(17, dobj.getPermitDtls());
            ps.setString(18, dobj.getSemiTrailerDtls());
            ps.setString(19, dobj.getRetroFittingDtls());
            ps.setString(20, qrURL);
            ps.executeUpdate();

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in smartcard New table inserion");
        }
    }
    
    /*
        @author Kartikey Singh
    */
    public static void insertIntoSmartCardTempDtls(String regnNo, String stateCd, int OffCd, String applNo, TransactionManager tmgr,
            TmConfigurationDobj tmConfig, SessionVariablesModel sessionVariablesModel) throws VahanException {
        PreparedStatement ps = null;
        SmartcardTempDtlsDobj dobj = new SmartcardTempDtlsDobj();
        int vhType = 0;
        try {
            String sql = "Select * from f_smart_card_details(?,?,?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            ps.setInt(3, OffCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj.setRegnNo(rs.getString("o_regn_no"));
                dobj.setCardIssueDate(rs.getString("o_cid"));
                dobj.setRegnDtls(rs.getString("regn_dtls"));
                dobj.setOwnerDtls(rs.getString("owner_dtls"));
                dobj.setVehicleDtls(rs.getString("vehicle_dtls"));
                dobj.setAxleDtls(rs.getString("axle_dtls"));
                dobj.setTrailerDtls(rs.getString("trailer_dtls"));
                dobj.setLinkedVehDtls(rs.getString("linked_veh_dtls"));
                dobj.setHypthDtls(rs.getString("hypth_dtls"));
                dobj.setSemiTrailerDtls(rs.getString("semi_trailer_dtls"));
                dobj.setRetroFittingDtls(rs.getString("retro_fitting_dtls"));
                dobj.setChallanDtls(rs.getString("challan_dtls"));
                dobj.setPermitDtls(rs.getString("permit_dtls"));
            }

            int purCd = 0;// from va_details
            String purDesc = "";
            sql = " Select a.pur_cd,short_descr  from va_details a "
                    + " left join tm_purpose_mast b on a.pur_cd = b.pur_cd "
                    + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);

            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                purCd = rs.getInt("pur_cd");
                purDesc = rs.getString("short_descr");
            }
            OwnerImplementation ownerImpl = new OwnerImplementation();
            //Owner_dobj ownerDobj = ownerImpl.set_Owner_appl_db_to_dobj_with_state_off_cd(regnNo.trim(), null, "", 1);
            Owner_dobj ownerDobj = ownerImpl.set_Owner_appl_db_to_dobj_with_state_off_cd(regnNo.trim(), null, "", 1, tmgr, stateCd, OffCd, tmConfig.isAllow_fitness_all_RTO());
            if (ownerDobj != null) {
                vhType = ownerDobj.getVehType();

            }
            ApplicationInwardImplementation applicationInwardImpl = new ApplicationInwardImplementation();
            boolean installmentNotPaid = checkFirstInstallmentPaidOrNot(tmgr, applNo, regnNo);
            String taxStatus = applicationInwardImpl.taxPaidStatusForSmartCard(regnNo.trim(), tmConfig, ownerDobj, purCd, tmgr, sessionVariablesModel);
            String qrURL = ServerUtility.getQRURL(regnNo, DocumentType.RC_QR, tmgr);

            sql = "INSERT INTO smartcard.va_smart_card_new("
                    + "            state_cd, off_cd, appl_no, regn_no, pur_cd,"
                    + " user_cd, op_dt, tax_upto ,tax_installment, regn_dtls, "
                    + " owner_dtls, vehicle_dtls, axle_dtls, trailer_dtls, linked_veh_dtls,"
                    + " hypth_dtls,challan_dtls,permit_dtls, semi_trailer_dtls,  retro_fitting_dtls,qr_url) "
                    + "    VALUES (?, ?, ?, ?, ?, ?, current_timestamp,?, ?, ?, ?, ?, ?,  "
                    + "            ?, ?, ?, ?, ? ,?,?,?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, OffCd);
            ps.setString(3, applNo);
            ps.setString(4, regnNo);
            ps.setString(5, purDesc);
            ps.setString(6, sessionVariablesModel.getEmpCodeLoggedIn());
            ps.setString(7, taxStatus);
            ps.setBoolean(8, installmentNotPaid);
            ps.setString(9, dobj.getRegnDtls());
            ps.setString(10, dobj.getOwnerDtls());
            ps.setString(11, dobj.getVehicleDtls());
            ps.setString(12, dobj.getAxleDtls());
            ps.setString(13, dobj.getTrailerDtls());
            ps.setString(14, dobj.getLinkedVehDtls());
            ps.setString(15, dobj.getHypthDtls());
            ps.setString(16, dobj.getChallanDtls());
            ps.setString(17, dobj.getPermitDtls());
            ps.setString(18, dobj.getSemiTrailerDtls());
            ps.setString(19, dobj.getRetroFittingDtls());
            ps.setString(20, qrURL);
            ps.executeUpdate();

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in smartcard New table inserion");
        }
    }
}
