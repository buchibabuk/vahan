/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.PuccDobj;
import nic.vahan.form.dobj.permit.PermitCheckDetailsDobj;
import static nic.vahan.form.impl.InsImpl.insertIntoInsurance;
import static nic.vahan.form.impl.InsImpl.insertIntoInsuranceHistory;
import static nic.vahan.form.impl.InsImpl.updateInsurance;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class PermitCheckDetailsImpl {

    private static final Logger LOGGER = Logger.getLogger(PermitCheckDetailsImpl.class);
    public String Query = "";

    public PermitCheckDetailsDobj getLatestTaxDtls(PermitCheckDetailsDobj pmtCheckDobj, String regn_no) {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs = null;
        try {
            Query = " select * from ((Select tax_mode,rcpt_no,to_char(tax_from,'DD-MON-YYYY') as tax_from,\n"
                    + " COALESCE(to_char(tax_upto,'DD-MON-YYYY'), 'LIFE TIME') as tax_upto ,\n"
                    + " descr as pur_cd,rcpt_dt from " + TableList.VT_TAX + "  a\n"
                    + " left outer join " + TableList.TM_PURPOSE_MAST + "  b on a.pur_cd = b.pur_cd\n"
                    + " where state_cd = ? and regn_no = ?  AND a.pur_cd = ? AND left(tax_mode,1) NOT IN ('B') order by a.rcpt_dt DESC limit 1)\n"
                    + " union all \n"
                    + " (Select '' as tax_mode,TCR_NO as rcpt_no,to_char(CLEAR_FR,'DD-MON-YYYY') as tax_from,to_char(CLEAR_TO,'DD-MON-YYYY') as tax_upto ,descr as pur_cd,op_dt as rcpt_dt from " + TableList.VT_TAX_CLEAR + "  a\n"
                    + " left outer join " + TableList.TM_PURPOSE_MAST + "   b on a.pur_cd = b.pur_cd \n"
                    + " where state_cd = ? and regn_no = ?  AND a.pur_cd = ? AND clear_to is not null order by op_dt DESC limit 1)"
                    + " union all						\n"
                    + " (SELECT '' as tax_mode,perm_no as rcpt_no,to_char(exem_fr,'DD-MON-YYYY') as tax_from,to_char(exem_to,'DD-MON-YYYY') as tax_upto,'TAX Exemp' as pur_cd ,exem_to as rcpt_dt\n"
                    + " from " + TableList.VT_TAX_EXEM + "  where state_cd = ? and regn_no = ? AND exem_to is not null order by exem_to DESC limit 1)) a order by a.rcpt_dt desc limit 1";

            tmgr = new TransactionManager("Get Tax Details");
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, regn_no);
            ps.setInt(3, TableConstants.TM_ROAD_TAX);
            ps.setString(4, Util.getUserStateCode());
            ps.setString(5, regn_no);
            ps.setInt(6, TableConstants.TM_ROAD_TAX);
            ps.setString(7, Util.getUserStateCode());
            ps.setString(8, regn_no);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                pmtCheckDobj.setTaxFrom(rs.getString("tax_from"));
                pmtCheckDobj.setTaxPurCd(rs.getString("pur_cd"));
                pmtCheckDobj.setTaxRcptNo(rs.getString("rcpt_no"));
                pmtCheckDobj.setTaxUpto(rs.getString("tax_upto"));
                pmtCheckDobj.setTaxMode(rs.getString("tax_mode"));
                if ((!CommonUtils.isNullOrBlank(rs.getString("tax_mode"))) && (!"OLS".contains(rs.getString("tax_mode"))) && rs.getString("tax_upto").equalsIgnoreCase("LIFE TIME")) {
                    pmtCheckDobj.setTaxUpto("");
                }
            }
            if (CommonUtils.isNullOrBlank(pmtCheckDobj.getTaxUpto())) {
                pmtCheckDobj.setTaxUpto("");
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
        return pmtCheckDobj;
    }

//    public PermitCheckDetailsDobj getTaxDtls(PermitCheckDetailsDobj pmtCheckDobj, String regn_no) {
//        TransactionManager tmgr = null;
//        PreparedStatement ps;
//        Date currentDate = null, taxUptoDate = null;
//        String taxModeSTR;
//        RowSet rs = null;
//        try {
//            Query = "Select tax_mode,rcpt_no,to_char(tax_from,'DD-MON-YYYY') as tax_from,COALESCE(to_char(tax_upto,'DD-MON-YYYY'), 'LIFE TIME') as tax_upto ,\n"
//                    + " descr as pur_cd ,tax_upto as taxUptoDate,current_date as currentDate from " + TableList.VT_TAX + " a\n"
//                    + " left outer join " + TableList.TM_PURPOSE_MAST + " b on a.pur_cd = b.pur_cd\n"
//                    + " where state_cd=? and regn_no = ?  AND a.pur_cd = ? AND left(tax_mode,1) IN ('Y', 'H', 'Q', 'M', 'L', 'O', 'S','I') order by a.rcpt_dt DESC limit 1";
//            tmgr = new TransactionManager("Get Tax Details");
//            ps = tmgr.prepareStatement(Query);
//            ps.setString(1, Util.getUserStateCode());
//            ps.setString(2, regn_no);
//            ps.setInt(3, TableConstants.TM_ROAD_TAX);
//            rs = tmgr.fetchDetachedRowSet_No_release();
//            if (rs.next()) {
//                pmtCheckDobj.setTaxFrom(rs.getString("tax_from"));
//                pmtCheckDobj.setTaxPurCd(rs.getString("pur_cd"));
//                pmtCheckDobj.setTaxRcptNo(rs.getString("rcpt_no"));
//                pmtCheckDobj.setTaxUpto(rs.getString("tax_upto"));
//                pmtCheckDobj.setTaxMode(rs.getString("tax_mode"));
//                if ((!"OLS".contains(rs.getString("tax_mode"))) && rs.getString("tax_upto").equalsIgnoreCase("LIFE TIME")) {
//                    pmtCheckDobj.setTaxUpto("");
//                }
//                taxUptoDate = rs.getDate("taxUptoDate");
//                currentDate = rs.getDate("currentDate");
//                taxModeSTR = rs.getString("tax_mode");
//            }
//            if (CommonUtils.isNullOrBlank(pmtCheckDobj.getTaxUpto())) {
//                pmtCheckDobj.setTaxUpto("");
//            }
//            if ((rs == null) || (taxUptoDate == null && CommonUtils.isNullOrBlank(pmtCheckDobj.getInsFrom()) && !pmtCheckDobj.getTaxUpto().equalsIgnoreCase("LIFE TIME"))
//                    || (taxUptoDate != null && !(taxUptoDate.getTime() >= currentDate.getTime()))) {
//                Query = "Select TCR_NO,to_char(CLEAR_FR,'DD-MON-YYYY') as CLEAR_FROM,to_char(CLEAR_TO,'DD-MON-YYYY') as CLEAR_upto ,descr as pur_cd,current_date as currentDate,CLEAR_TO from " + TableList.VT_TAX_CLEAR + " a\n"
//                        + " left outer join " + TableList.TM_PURPOSE_MAST + "  b on a.pur_cd = b.pur_cd\n"
//                        + " where state_cd = ? and regn_no = ?  AND a.pur_cd = ? AND clear_to is not null order by op_dt DESC limit 1 ";
//                ps = tmgr.prepareStatement(Query);
//                ps.setString(1, Util.getUserStateCode());
//                ps.setString(2, regn_no);
//                ps.setInt(3, TableConstants.TM_ROAD_TAX);
//                rs = tmgr.fetchDetachedRowSet_No_release();
//                if (rs.next() && (taxUptoDate != null && rs.getDate("CLEAR_TO") != null && (rs.getDate("CLEAR_TO").getTime() > taxUptoDate.getTime()))) {
//                    pmtCheckDobj.setTaxFrom(rs.getString("CLEAR_FROM"));
//                    pmtCheckDobj.setTaxPurCd(rs.getString("pur_cd"));
//                    pmtCheckDobj.setTaxRcptNo(rs.getString("TCR_NO"));
//                    pmtCheckDobj.setTaxUpto(rs.getString("CLEAR_upto"));
//                    taxUptoDate = rs.getDate("CLEAR_TO");
//                    currentDate = rs.getDate("currentDate");
//                }
//            }
//            if ((rs == null) || (taxUptoDate == null && CommonUtils.isNullOrBlank(pmtCheckDobj.getInsFrom()) && !pmtCheckDobj.getTaxUpto().equalsIgnoreCase("LIFE TIME"))
//                    || (taxUptoDate != null && !(taxUptoDate.getTime() > currentDate.getTime()))) {
//                Query = "SELECT perm_no,to_char(exem_fr,'DD-MON-YYYY') as exem_from,to_char(exem_to,'DD-MON-YYYY') as exem_upto,'TAX Exemp' as pur "
//                        + " ,exem_to from " + TableList.VT_TAX_EXEM + " where state_cd = ? and regn_no = ? order by exem_to DESC limit 1";
//                ps = tmgr.prepareStatement(Query);
//                ps.setString(1, Util.getUserStateCode());
//                ps.setString(2, regn_no);
//                rs = tmgr.fetchDetachedRowSet_No_release();
//                if (rs.next() && (taxUptoDate != null && rs.getDate("exem_to") != null && (rs.getDate("exem_to").getTime() > taxUptoDate.getTime()))) {
//                    pmtCheckDobj.setTaxFrom(rs.getString("exem_from"));
//                    pmtCheckDobj.setTaxPurCd(rs.getString("pur"));
//                    pmtCheckDobj.setTaxRcptNo(rs.getString("perm_no"));
//                    pmtCheckDobj.setTaxUpto(rs.getString("exem_upto"));
//                }
//            }
//        } catch (Exception e) {
//            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
//        } finally {
//            try {
//                if (tmgr != null) {
//                    tmgr.release();
//                }
//            } catch (Exception e) {
//                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
//            }
//        }
//        return pmtCheckDobj;
//    }
    public PermitCheckDetailsDobj getGoodsPassengerTaxDtls(PermitCheckDetailsDobj pmtCheckDobj, String regn_no) {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        Date currentDate = null, taxUptoDate = null;
        String taxModeSTR;
        RowSet rs = null;
        try {
            Query = "Select tax_mode,rcpt_no,to_char(tax_from,'DD-MON-YYYY') as tax_from,COALESCE(to_char(tax_upto,'DD-MON-YYYY'), 'LIFE TIME') as tax_upto ,\n"
                    + " descr as pur_cd ,a.pur_cd as pur_code, tax_upto as taxUptoDate,current_date as currentDate from " + TableList.VT_TAX + " a\n"
                    + " left outer join " + TableList.TM_PURPOSE_MAST + " b on a.pur_cd = b.pur_cd\n"
                    + " where state_cd=? and regn_no = ?  AND a.pur_cd in (54,55) AND left(tax_mode,1) IN ('Y', 'H', 'Q', 'M', 'L', 'O', 'S') order by a.rcpt_dt DESC limit 1";
            tmgr = new TransactionManager("getGoodsPassengerTaxDtls");
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, regn_no);

            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                pmtCheckDobj.setGptaxFrom(rs.getString("tax_from"));
                pmtCheckDobj.setGptaxPurCd(rs.getString("pur_cd"));
                pmtCheckDobj.setGptaxRcptNo(rs.getString("rcpt_no"));
                pmtCheckDobj.setGptaxUpto(rs.getString("tax_upto"));
                pmtCheckDobj.setGptaxMode(rs.getString("tax_mode"));
                if ((!"OLS".contains(rs.getString("tax_mode"))) && rs.getString("tax_upto").equalsIgnoreCase("LIFE TIME")) {
                    pmtCheckDobj.setTaxUpto("");
                }
                taxUptoDate = rs.getDate("taxUptoDate");
                currentDate = rs.getDate("currentDate");
                taxModeSTR = rs.getString("tax_mode");
            }

            if (CommonUtils.isNullOrBlank(pmtCheckDobj.getTaxUpto())) {
                pmtCheckDobj.setTaxUpto("");
            }
            if ((rs == null) || (taxUptoDate == null && CommonUtils.isNullOrBlank(pmtCheckDobj.getInsFrom()) && !pmtCheckDobj.getTaxUpto().equalsIgnoreCase("LIFE TIME"))
                    || (taxUptoDate != null && !(taxUptoDate.getTime() >= currentDate.getTime()))) {
                Query = "Select TCR_NO,to_char(CLEAR_FR,'DD-MON-YYYY') as CLEAR_FROM,to_char(CLEAR_TO,'DD-MON-YYYY') as CLEAR_upto ,descr as pur_cd,a.pur_cd as pur_code,current_date as currentDate,CLEAR_TO from " + TableList.VT_TAX_CLEAR + " a\n"
                        + " left outer join " + TableList.TM_PURPOSE_MAST + "  b on a.pur_cd = b.pur_cd\n"
                        + " where state_cd = ? and regn_no = ?  AND a.pur_cd in (54,55) AND clear_to is not null order by op_dt DESC limit 1 ";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, Util.getUserStateCode());
                ps.setString(2, regn_no);

                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    pmtCheckDobj.setGptaxFrom(rs.getString("CLEAR_FROM"));
                    pmtCheckDobj.setGptaxPurCd(rs.getString("pur_cd"));
                    pmtCheckDobj.setGptaxRcptNo(rs.getString("TCR_NO"));
                    pmtCheckDobj.setGptaxUpto(rs.getString("CLEAR_upto"));
                    taxUptoDate = rs.getDate("CLEAR_TO");
                    currentDate = rs.getDate("currentDate");
                }
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
        return pmtCheckDobj;
    }

    public PermitCheckDetailsDobj getFitnessDtls(PermitCheckDetailsDobj pmtCheckDobj, String regn_no) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs;
        try {
            Query = "SELECT to_char(fit_upto,'DD-MON-YYYY') as fit_valid_to,fit_upto,current_date as current_date FROM " + TableList.VT_OWNER + " where REGN_No = ?";
            tmgr = new TransactionManager("Get Fitness Details");
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regn_no);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (rs.getDate("fit_upto").compareTo(rs.getDate("current_date")) != -1) {
                    pmtCheckDobj.setFitValidTo(rs.getString("fit_valid_to"));
                } else {
                    Query = "select to_char(fit_valid_to,'DD-MON-YYYY') as fit_upto from " + TableList.VT_FITNESS + " where regn_no=? AND state_cd=? AND fit_result in (?,?) order by op_dt desc limit 1";
                    ps = tmgr.prepareStatement(Query);
                    ps.setString(1, regn_no);
                    ps.setString(2, Util.getUserStateCode());
                    ps.setString(3, TableConstants.FitnessResultPass);
                    ps.setString(4, "P");
                    RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
                    if (rs1.next()) {
                        pmtCheckDobj.setFitValidTo(rs1.getString("fit_upto"));
                    }
                }
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
        return pmtCheckDobj;
    }

    public PermitCheckDetailsDobj getInsuranceDtls(PermitCheckDetailsDobj pmtCheckDobj, String regn_no) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        pmtCheckDobj.setInsValid(false);
        pmtCheckDobj.setInsSaveData(true);
        try {
            Date date = new Date();
            Query = "SELECT comp_cd as comp_code,ins_type as ins_type_code,cmpy_cd.descr as comp_cd,ins_cd.descr as ins_type, to_char(ins_from,'DD-MON-YYYY') as ins_from, to_char(ins_upto,'DD-MON-YYYY') as ins_upto, policy_no, idv FROM " + TableList.VT_INSURANCE + "\n"
                    + " inner join " + TableList.VM_ICCODE + " cmpy_cd ON ic_code = comp_cd\n"
                    + " inner join  " + TableList.VM_INSTYP + " ins_cd ON instyp_code = ins_type\n"
                    + " WHERE regn_no = ? AND state_cd = ? order by ins_upto desc limit 1";
            tmgr = new TransactionManager("Get Insurance Details");
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                pmtCheckDobj.setInsFrom(rs.getString("ins_from"));
                pmtCheckDobj.setInsPolicyNo(rs.getString("policy_no"));
                pmtCheckDobj.setInsType(rs.getString("ins_type"));
                pmtCheckDobj.setInsUpto(rs.getString("ins_upto"));
                pmtCheckDobj.setInsCmpyNo(rs.getString("comp_cd"));
                pmtCheckDobj.setInsIdv(rs.getString("idv"));
                pmtCheckDobj.setInsCmpyNoCode(rs.getInt("comp_code"));
                pmtCheckDobj.setInsTypeCode(rs.getInt("ins_type_code"));
                pmtCheckDobj.setInsValid(true);
                pmtCheckDobj.setInsSaveData(false);
            }
            if (CommonUtils.isNullOrBlank(pmtCheckDobj.getInsUpto()) || CommonPermitPrintImpl.getDateDD_MMM_YYYY(pmtCheckDobj.getInsUpto()).getTime() < date.getTime()) {
                Query = "SELECT comp_cd as comp_code,ins_type as ins_type_code,cmpy_cd.descr as comp_cd,ins_cd.descr as ins_type, to_char(ins_from,'DD-MON-YYYY') as ins_from, to_char(ins_upto,'DD-MON-YYYY') as ins_upto, policy_no, idv FROM " + TableList.VA_INSURANCE + "\n"
                        + "inner join " + TableList.VM_ICCODE + " cmpy_cd ON ic_code = comp_cd\n"
                        + "inner join  " + TableList.VM_INSTYP + " ins_cd ON instyp_code = ins_type\n"
                        + "WHERE regn_no = ? order by ins_upto desc limit 1";

                ps = tmgr.prepareStatement(Query);
                ps.setString(1, regn_no);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    pmtCheckDobj.setInsFrom(rs.getString("ins_from"));
                    pmtCheckDobj.setInsPolicyNo(rs.getString("policy_no"));
                    pmtCheckDobj.setInsType(rs.getString("ins_type"));
                    pmtCheckDobj.setInsUpto(rs.getString("ins_upto"));
                    pmtCheckDobj.setInsCmpyNo(rs.getString("comp_cd"));
                    pmtCheckDobj.setInsIdv(rs.getString("idv"));
                    pmtCheckDobj.setInsCmpyNoCode(rs.getInt("comp_code"));
                    pmtCheckDobj.setInsTypeCode(rs.getInt("ins_type_code"));
                    pmtCheckDobj.setInsValid(true);
                    pmtCheckDobj.setInsSaveData(false);
                }
                if (CommonUtils.isNullOrBlank(pmtCheckDobj.getInsUpto()) || CommonPermitPrintImpl.getDateDD_MMM_YYYY(pmtCheckDobj.getInsUpto()).getTime() < date.getTime()) {
                    pmtCheckDobj.setInsValid(false);
                    pmtCheckDobj.setInsSaveData(true);
                }
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
        return pmtCheckDobj;
    }

    public PermitCheckDetailsDobj getChallanDtls(PermitCheckDetailsDobj pmtCheckDobj, String regn_no) {
        TransactionManager tmgr = null;
        pmtCheckDobj.setChalPending(true);
        PreparedStatement ps = null;
        try {
            Query = "SELECT chal_no, to_char(chal_date,'DD-MON-YYYY') as chal_date, chal_time, chal_place,user_name as chal_officer FROM " + TableList.VA_CHALLAN + " \n"
                    + "inner join " + TableList.TM_USER_INFO + " on user_cd = chal_officer :: numeric\n"
                    + "where regn_no = ?";
            tmgr = new TransactionManager("Get Challan Details");
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                pmtCheckDobj.setChalDate(rs.getString("chal_date"));
                pmtCheckDobj.setChalNo(rs.getString("chal_no"));
                pmtCheckDobj.setChalOfficer(rs.getString("chal_officer"));
                pmtCheckDobj.setChalPlace(rs.getString("chal_place"));
                pmtCheckDobj.setChalTime(rs.getString("chal_time"));
                pmtCheckDobj.setChalPending(false);
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
        return pmtCheckDobj;
    }

    public PermitCheckDetailsDobj getVehicleBlackListedDtls(PermitCheckDetailsDobj pmtCheckDobj, String regn_no) {
        TransactionManager tmgr = null;
        pmtCheckDobj.setVehicleIsBlackListed(false);
        PreparedStatement ps = null;
        try {
            Query = "SELECT * from " + TableList.VT_BLACKLIST + " where regn_no = ?";
            tmgr = new TransactionManager("get Vehicle Black Listed Dtls");
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                pmtCheckDobj.setVehicleIsBlackListed(true);
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
        return pmtCheckDobj;
    }

    public PermitCheckDetailsDobj getPuccDetails(PermitCheckDetailsDobj pmtCheckDobj, String regnNo) {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        String insQuery = "SELECT to_char(pucc_from,'DD-MON-YYYY') as pucc_from,to_char(pucc_upto,'DD-MON-YYYY') as pucc_upto,pucc_centreno,pucc_no FROM " + TableList.VT_PUCC + " where regn_no = ? and state_cd=? order by op_dt desc limit 1";
        try {
            tmgr = new TransactionManagerReadOnly("getPuccDetails");
            ps = tmgr.prepareStatement(insQuery);
            ps.setString(1, regnNo);
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                pmtCheckDobj.setPuccFrom(rs.getString("pucc_from"));
                pmtCheckDobj.setPuccUpto(rs.getString("pucc_upto"));
                pmtCheckDobj.setPuccCentreno(rs.getString("pucc_centreno"));
                pmtCheckDobj.setPuccNo(rs.getString("pucc_no"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            }
        }
        return pmtCheckDobj;
    }

    public void insertIntoVaInsurance(String appl_no, String regn_no, InsDobj ins_dobj, TransactionManager tmgr) throws VahanException {
        String stateCode = "";
        Integer offCode = null;
        PreparedStatement ps = null;
        try {
            Query = "select state_cd,off_cd from  " + TableList.VT_OWNER
                    + " where regn_no=?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                stateCode = rs.getString(1);
                offCode = rs.getInt(2);
            }
            Query = "SELECT * FROM " + TableList.VA_INSURANCE + " where regn_no = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regn_no);
            rs = tmgr.fetchDetachedRowSet_No_release();
            ins_dobj.setState_cd(stateCode);
            ins_dobj.setOff_cd(offCode);
            if (rs.next()) {
                insertIntoInsuranceHistory(tmgr, regn_no);
                updateInsurance(tmgr, ins_dobj, regn_no);
            } else {
                insertIntoInsurance(tmgr, ins_dobj, appl_no, regn_no, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
            }
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + "-" + sqle.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of Insurance Details, Please contact to the System Administrator.");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during update of Insurance Details, Please contact to the System Administrator.");
        }
    }

    public String[] getPermitDetailsOnTaxBasedOn(String regnNo, String state_cd, int off_cd, String pmt_type) {
        PreparedStatement ps;
        String Query;
        TransactionManager tmgr = null;
        RowSet rs = null;
        String[] taxPmtDetls = new String[6];
        try {
            if (!"NEW".equalsIgnoreCase(regnNo)) {
                tmgr = new TransactionManager("getPermitDetailsOnTaxBasedOn");
                if (!CommonUtils.isNullOrBlank(pmt_type) && state_cd.equalsIgnoreCase("PB") && (pmt_type.equalsIgnoreCase(TableConstants.GOODS_PERMIT) || pmt_type.equalsIgnoreCase(TableConstants.NATIONAL_PERMIT))) {
                    Query = "select pmt_type,pmt_catg,service_type,route_length,no_of_trips,domain_cd from " + TableList.VT_TAX_BASED_ON + "  where regn_no = ? and state_cd=? and pmt_type=? order by op_dt DESC limit 1";
                    ps = tmgr.prepareStatement(Query);
                    ps.setString(1, regnNo);
                    ps.setString(2, state_cd);
                    ps.setInt(3, Integer.parseInt(pmt_type));
                    rs = tmgr.fetchDetachedRowSet();
                } else {
                    Query = "select pmt_type,pmt_catg,service_type,route_length,no_of_trips,domain_cd from " + TableList.VT_TAX_BASED_ON + "  where regn_no = ? and state_cd=? order by op_dt DESC limit 1";
                    ps = tmgr.prepareStatement(Query);
                    ps.setString(1, regnNo);
                    ps.setString(2, state_cd);
                    rs = tmgr.fetchDetachedRowSet();
                }

                if (rs.next()) {
                    if (("-1,0").contains(rs.getString("pmt_type"))) {
                        taxPmtDetls[0] = "";
                    } else {
                        taxPmtDetls[0] = rs.getString("pmt_type");
                    }
                    if (("-1,0").contains(rs.getString("pmt_catg"))) {
                        taxPmtDetls[1] = "";
                    } else {
                        taxPmtDetls[1] = rs.getString("pmt_catg");
                    }
                    taxPmtDetls[2] = rs.getString("service_type");
                    taxPmtDetls[3] = rs.getString("route_length");
                    taxPmtDetls[4] = rs.getString("no_of_trips");
                    taxPmtDetls[5] = rs.getString("domain_cd");
                }
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
        return taxPmtDetls;
    }

    public String[] getPmtDetailsOnTaxBasedOn(String regnNo, String state_cd, int off_cd, String pmt_type) {
        PreparedStatement ps;
        String Query;
        TransactionManagerReadOnly tmgr = null;
        RowSet rs = null;
        String[] taxPmtDetls = new String[7];
        try {
            if (!"NEW".equalsIgnoreCase(regnNo)) {
                tmgr = new TransactionManagerReadOnly("getPmtDetailsOnTaxBasedOn");
                Query = "select pmt_type,pmt_catg,service_type,route_length,no_of_trips,domain_cd,tax_mode from " + TableList.VT_TAX_BASED_ON + " "
                        + " inner join  " + TableList.VT_TAX + " on vt_tax_based_on.regn_no=vt_tax.regn_no and vt_tax_based_on.state_cd=vt_tax.state_cd and vt_tax_based_on.off_cd=vt_tax.off_cd  where vt_tax_based_on.regn_no = ? and vt_tax_based_on.state_cd=? order by op_dt DESC limit 1";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, regnNo);
                ps.setString(2, state_cd);
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    if (("-1,0").contains(rs.getString("pmt_type"))) {
                        taxPmtDetls[0] = "";
                    } else {
                        taxPmtDetls[0] = rs.getString("pmt_type");
                    }
                    if (("-1,0").contains(rs.getString("pmt_catg"))) {
                        taxPmtDetls[1] = "";
                    } else {
                        taxPmtDetls[1] = rs.getString("pmt_catg");
                    }
                    taxPmtDetls[2] = rs.getString("service_type");
                    taxPmtDetls[3] = rs.getString("route_length");
                    taxPmtDetls[4] = rs.getString("no_of_trips");
                    taxPmtDetls[5] = rs.getString("domain_cd");
                    taxPmtDetls[6] = rs.getString("tax_mode");
                }
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
        return taxPmtDetls;
    }

    public String[] getPermitDetailsFromVhaNewRegn(String regnNo, String state_cd) {
        PreparedStatement ps;
        String Query;
        TransactionManager tmgr = null;
        RowSet rs = null;
        String[] taxPmtDetls = new String[4];
        try {
            if (!"NEW".equalsIgnoreCase(regnNo)) {
                tmgr = new TransactionManager("getPermitDetailsFromVhaNewRegn");

                Query = "select b.pmt_type,b.pmt_catg,b.service_type,b.region_covered,b.moved_on from " + TableList.VA_DETAILS + " a inner join " + TableList.VHA_PERMIT_NEW_REGN + " b on a.state_cd = b.state_cd and a.appl_no = b.appl_no where  a.state_cd= ? and a.regn_no = ? and a.pur_cd in (?,?) order by b.moved_on DESC limit 1";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, state_cd);
                ps.setString(2, regnNo);
                ps.setInt(3, TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE);
                ps.setInt(4, TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE);
                rs = tmgr.fetchDetachedRowSet();

                if (rs.next()) {
                    if (("-1,0").contains(rs.getString("pmt_type"))) {
                        taxPmtDetls[0] = "";
                    } else {
                        taxPmtDetls[0] = rs.getString("pmt_type");
                    }
                    if (("-1,0").contains(rs.getString("pmt_catg"))) {
                        taxPmtDetls[1] = "";
                    } else {
                        taxPmtDetls[1] = rs.getString("pmt_catg");
                    }
                    taxPmtDetls[2] = rs.getString("service_type");
                    taxPmtDetls[3] = rs.getString("region_covered");
                }
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
        return taxPmtDetls;
    }
}
