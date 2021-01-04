/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TaxClearanceCertificateDetailDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.reports.TaxClearanceCertificatePrintReportDobj;
import nic.vahan.form.dobj.reports.TaxClearanceCertificatePrintReportSubDobj;
import nic.vahan.form.dobj.reports.VmRoadSafetySloganPrintDobj;
import nic.vahan.form.impl.ApplicationInwardImpl;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import nic.vahan5.reg.server.ServerUtility;
import org.apache.log4j.Logger;

/**
 *
 * @author Kartikey Singh
 */
public class TaxClearanceCertificatePrintImplementation {
    

    private static final Logger LOGGER = Logger.getLogger(TaxClearanceCertificatePrintImplementation.class);

    // Constructor Initiation
    public static ArrayList<TaxClearanceCertificateDetailDobj> getPurCdPrintTCCDetails() throws VahanException {
        ArrayList<TaxClearanceCertificateDetailDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getPurCdPrintDocsDetails");
            ps = tmgr.prepareStatement("select distinct appl_no, regn_no, state_cd, off_cd,coalesce(tcc_no,'') as tcc_no"
                    + " from " + TableList.VA_TCC_PRINT
                    + " where state_cd = ? and off_cd = ? and regn_no <> 'NEW' and op_dt >= current_date - interval '7 day' order by regn_no");
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserSeatOffCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                TaxClearanceCertificateDetailDobj dobj = new TaxClearanceCertificateDetailDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegno(rs.getString("regn_no"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setTcc_no(rs.getString("tcc_no"));

                list.add(dobj);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "--" + ex.getStackTrace()[0]);
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

    // Constructor For history tax clear
    public static ArrayList<TaxClearanceCertificateDetailDobj> getPurCdPrintHistTCCDetails() throws VahanException {
        ArrayList<TaxClearanceCertificateDetailDobj> listh = new ArrayList();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getPurCdPrintHistTCCDetails");
            ps = tmgr.prepareStatement("select distinct appl_no, regn_no, state_cd, off_cd,coalesce(tcc_no,'') as tcc_no"
                    + " from " + TableList.VHA_TCC_PRINT
                    + " where printed_on between current_date and (current_date + '1 day'::interval)"
                    + " and state_cd = ? and off_cd = ? order by regn_no");
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserSeatOffCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                TaxClearanceCertificateDetailDobj dobj = new TaxClearanceCertificateDetailDobj();
                dobj.setAppl_no_hist(rs.getString("appl_no"));
                dobj.setRegno_hist(rs.getString("regn_no"));
                dobj.setTcc_no(rs.getString("tcc_no"));
                listh.add(dobj);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "--" + ex.getStackTrace()[0]);
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
        return listh;
    }

    // isPrint button
    public static String deleteAndSaveHistoryTCC(List<TaxClearanceCertificateDetailDobj> selectedTccList) {
        PreparedStatement psvatccprint = null;
        PreparedStatement psvhtccprint = null;
        String isExist = "";
        TransactionManager tmgr = null;
        Status_dobj statusDobj = null;
        try {
            tmgr = new TransactionManager("deleteAndSaveHistory");
            for (int i = 0; i < selectedTccList.size(); i++) {
                statusDobj = null;
                isExist = selectedTccList.get(i).getAppl_no();
                String vhtccprintSql = " insert into " + TableList.VHA_TCC_PRINT
                        + " select current_timestamp,?, ?, ?, appl_no,regn_no,op_dt,tcc_no from " + TableList.VA_TCC_PRINT + " where appl_no=?";
                psvhtccprint = tmgr.prepareStatement(vhtccprintSql);
                psvhtccprint.setString(1, Util.getEmpCode());
                psvhtccprint.setString(2, Util.getUserStateCode());
                psvhtccprint.setInt(3, Util.getSelectedSeat().getOff_cd());
                psvhtccprint.setString(4, selectedTccList.get(i).getAppl_no());
                psvhtccprint.executeUpdate();
                String varcprintSql = " delete from " + TableList.VA_TCC_PRINT + " where appl_no=?";
                psvatccprint = tmgr.prepareStatement(varcprintSql);
                psvatccprint.setString(1, selectedTccList.get(i).getAppl_no());
                psvatccprint.executeUpdate();
                statusDobj = new Status_dobj();
                statusDobj.setAppl_no(selectedTccList.get(i).getAppl_no());
                statusDobj.setPur_cd(TableConstants.VM_DUPLICATE_TO_TAX_CARD);
                statusDobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, statusDobj);
                ApplicationInwardImpl applicationInwardImpl = new ApplicationInwardImpl();
                applicationInwardImpl.deleteVaStatus(tmgr, selectedTccList.get(i).getAppl_no(), TableConstants.VM_DUPLICATE_TO_TAX_CARD);
            }
            isExist = "success";
            tmgr.commit();
        } catch (Exception e) {
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
        return isExist;
    }

//    // Insert during taking Fee at Fee module after the Inward of Application
//    public static void insertintoVA_TCC_Print(String regnno, String applno, TransactionManager tmgr, String stateCd, int offCd) {
//        PreparedStatement psvhtccprint = null;
//        try {
//
//            String unique_No = ServerUtil.getUniquePermitNo(tmgr, stateCd, offCd, 0, 0, "C");
//            String vhrcprintSql = " insert into " + TableList.VA_TCC_PRINT + " values( ?, ?, ? ,?,current_timestamp,? )";
//            psvhtccprint = tmgr.prepareStatement(vhrcprintSql);
//            psvhtccprint.setString(1, Util.getUserStateCode());
//            psvhtccprint.setInt(2, Util.getSelectedSeat().getOff_cd());
//            psvhtccprint.setString(3, applno);
//            psvhtccprint.setString(4, regnno);
//            psvhtccprint.setString(5, unique_No);
//            psvhtccprint.executeUpdate();
//            // tmgr.commit();
//        } catch (Exception e) {
//            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
//        }
//    }
    
    /*
     * @author Kartikey Singh
     * 
     * Insert during taking Fee at Fee module after the Inward of Application
     * NOTE: Commented the original code out as both the methods had the same signature
     * The values being passed as parameters to the function are the same as the ones being referred
     * in Util.getUserStateCode() and Util.getSelectedSeat().getOff_cd()
     */
    public static void insertintoVA_TCC_Print(String regnno, String applno, TransactionManager tmgr, String stateCode, int selectedOffCode) {
        PreparedStatement psvhtccprint = null;
        try {

            String unique_No = ServerUtility.getUniquePermitNo(tmgr, stateCode, selectedOffCode, 0, 0, "C");
            String vhrcprintSql = " insert into " + TableList.VA_TCC_PRINT + " values( ?, ?, ? ,?,current_timestamp,? )";
            psvhtccprint = tmgr.prepareStatement(vhrcprintSql);
            psvhtccprint.setString(1, stateCode);
            psvhtccprint.setInt(2, selectedOffCode);
            psvhtccprint.setString(3, applno);
            psvhtccprint.setString(4, regnno);
            psvhtccprint.setString(5, unique_No);
            psvhtccprint.executeUpdate();
            // tmgr.commit();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    // Print TCC via table from VT_TAX_CLEAR
    public static TaxClearanceCertificatePrintReportDobj getTCCPrintDobj(String regn_no, String appl_no) throws VahanException, ParseException {
        TransactionManager tmgr = null;
        TaxClearanceCertificatePrintReportDobj dobj = null;
        TaxClearanceCertificatePrintReportSubDobj subdobj = null;
        TaxClearanceCertificatePrintReportDobj vttaxdobj = null;
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        ArrayList<TaxClearanceCertificatePrintReportDobj> dataList = new ArrayList<TaxClearanceCertificatePrintReportDobj>();
        ArrayList<TaxClearanceCertificatePrintReportSubDobj> dataSubList = new ArrayList<TaxClearanceCertificatePrintReportSubDobj>();
        TmConfigurationDobj configurationDobj = Util.getTmConfiguration();
        try {
            String sql = " SELECT a.state_cd, upper(a.state_name)as state_name , a.off_name,a.regn_no, to_char(a.regn_dt, 'dd-Mon-yyyy') as regn_dt,"
                    + " a.owner_name,vh_class_desc, a.maker_name as maker_name, a.model_name as model_name,"
                    + " to_char(b.clear_fr, 'dd-Mon-yyyy') as clear_fr, to_char(b.clear_to, 'dd-Mon-yyyy') as clear_to,b.pur_cd as pur_cd, c.descr as pur_cd_descr,coalesce(f.tcc_no,coalesce(g.tcc_no,'')) as tcc_no,"
                    + " b.tcr_no,coalesce(b.remark,'') as remark_vt_clr,to_char(h.exem_fr,'dd-Mon-yyyy') as exem_fr ,to_char(h.exem_to, 'dd-Mon-yyyy') as exem_to ,"
                    + " coalesce(h.remark,'') as remark_vt_exem,i.rcpt_heading,i.rcpt_subheading \n"
                    + " from " + TableList.VIEW_VV_OWNER + " a \n"
                    + " left join " + TableList.VT_TAX_CLEAR + " b on a.regn_no = b.regn_no and a.state_cd = b.state_cd \n"
                    + " left join " + TableList.TM_PURPOSE_MAST + " c on b.pur_cd=c.pur_cd \n"
                    + " left join " + TableList.VA_TCC_PRINT + " f on f.state_cd = a.state_cd and f.regn_no=a.regn_no and f.appl_no=? \n"
                    + " left join " + TableList.VHA_TCC_PRINT + " g on g.state_cd = a.state_cd and g.regn_no=a.regn_no and g.appl_no=? \n"
                    + " left join " + TableList.VT_TAX_EXEM + " h on h.state_cd = a.state_cd and h.off_cd=a.off_cd and h.regn_no=a.regn_no \n"
                    + " left join " + TableList.TM_CONFIGURATION + " i ON i.state_cd = a.state_cd \n"
                    + " where a.regn_no=? and a.state_cd = ? ";
            tmgr = new TransactionManager("getTCCPrintDobj");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, appl_no);
            ps.setString(3, regn_no);
            ps.setString(4, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            int i = 1;
            while (rs.next()) {
                subdobj = new TaxClearanceCertificatePrintReportSubDobj();
                if (i == 1) {
                    dobj = new TaxClearanceCertificatePrintReportDobj();
                    dobj.setHeader(rs.getString("rcpt_heading"));
                    dobj.setSubHeader(rs.getString("rcpt_subheading"));

                    if (configurationDobj.getTmPrintConfgDobj() != null) {
                        if (configurationDobj.getTmPrintConfgDobj().getImage_background() != null && !configurationDobj.getTmPrintConfgDobj().getImage_background().isEmpty()) {
                            dobj.setImage_background(configurationDobj.getTmPrintConfgDobj().getImage_background());
                            dobj.setShow_image_background(true);
                        } else {
                            dobj.setShow_image_background(false);
                        }
                        if (configurationDobj.getTmPrintConfgDobj().getImage_logo() != null && !configurationDobj.getTmPrintConfgDobj().getImage_logo().isEmpty()) {
                            dobj.setImage_logo(configurationDobj.getTmPrintConfgDobj().getImage_logo());
                            dobj.setShow_image_logo(true);
                        } else {
                            dobj.setShow_image_logo(false);
                        }
                        if (configurationDobj.getTmPrintConfgDobj().isRoad_safety_slogan()) {
                            VmRoadSafetySloganPrintDobj vmrssdobj = PrintDocImpl.getStateRoadSafetySlogan();
                            if (vmrssdobj != null) {
                                dobj.setShowRoadSafetySlogan(configurationDobj.getTmPrintConfgDobj().isRoad_safety_slogan());
                                dobj.setRoadSafetySloganDobj(vmrssdobj);
                            }
                        }
                    }
                    dobj.setOffName(rs.getString("off_name"));
                    dobj.setRegnNO(rs.getString("regn_no"));
                    dobj.setStateCD(rs.getString("state_cd"));
                    dobj.setStateName(rs.getString("state_name"));
                    dobj.setVchDescr(rs.getString("vh_class_desc"));
                    dobj.setOwnerName(rs.getString("owner_name"));
                    dobj.setRegDate(rs.getString("regn_dt"));
                    dobj.setMakerName(rs.getString("maker_name"));
                    dobj.setModelName(rs.getString("model_name"));
                    dobj.setVtTaxDetailsList(getVT_TAXDetailsPrintDobj(regn_no, appl_no));
                    dobj.setDiffofTaxxDetailsList(getDiffOfTaxDetailsPrintDobj(regn_no, appl_no));
                    dobj.setTcc_no(rs.getString("tcc_no"));
                    dobj.setRemark_vt_tax_clr(rs.getString("remark_vt_clr"));
                    dobj.setRemark_vt_tax_exem(rs.getString("remark_vt_exem"));
                    dobj.setTrc_no(rs.getString("tcr_no"));
                    dobj.setExem_fr(rs.getString("exem_fr"));
                    dobj.setExem_to(rs.getString("exem_to"));
                }
                subdobj.setClear_fr(rs.getString("clear_fr"));
                subdobj.setClear_to(rs.getString("clear_to"));
                subdobj.setPur_cd(rs.getString("pur_cd"));
                subdobj.setPur_cd_descr(rs.getString("pur_cd_descr"));
                dataSubList.add(subdobj);
                dobj.setTaxPurposeList(dataSubList);
                i++;
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "--" + ex.getStackTrace()[0]);
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

    // Print via table from VT_TAX for Tax Paid Details
    public static ArrayList<TaxClearanceCertificatePrintReportSubDobj> getVT_TAXDetailsPrintDobj(String regn_no, String appl_no) throws VahanException {
        TransactionManager tmgr = null;
        TaxClearanceCertificatePrintReportDobj dobj = null;
        TaxClearanceCertificatePrintReportSubDobj subdobjvt_tax = null;
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        ArrayList<TaxClearanceCertificatePrintReportDobj> dataList = new ArrayList<TaxClearanceCertificatePrintReportDobj>();
        ArrayList<TaxClearanceCertificatePrintReportSubDobj> dataSubListVT_TAX = new ArrayList<TaxClearanceCertificatePrintReportSubDobj>();
        try {
            String sql = " SELECT a.regn_no as Regn_no, a.state_cd as state_cd, a.tax_amt, a.tax_fine, (a.tax_amt + a.tax_fine) as tax_total, a.rcpt_no, to_char(a.rcpt_dt, 'dd-Mon-yyyy') as rcpt_dt, b.descr as tax_mode_descr,"
                    + " to_char(a.tax_from, 'dd-Mon-yyyy') as tax_from, to_char(a.tax_upto, 'dd-Mon-yyyy') as tax_upto, "
                    + " a.pur_cd as pur_cd, c.descr as pur_cd_descr "
                    + " from " + TableList.VT_TAX + " a left join " + TableList.VM_TAX_MODE + " b on a.tax_mode = b.tax_mode "
                    + " left join " + TableList.TM_PURPOSE_MAST + " c on a.pur_cd=c.pur_cd"
                    + " where a.regn_no=? and a.state_cd = ? order by a.rcpt_dt DESC limit 5";
            tmgr = new TransactionManager("getTCCPrintDobj");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            int i = 1;
            while (rs.next()) {
                subdobjvt_tax = new TaxClearanceCertificatePrintReportSubDobj();
                subdobjvt_tax.setPur_cd_descr_vttax(rs.getString("pur_cd_descr"));
                subdobjvt_tax.setTax_amt_vttax(rs.getString("tax_amt"));
                subdobjvt_tax.setTax_total_vttax(rs.getString("tax_total"));
                subdobjvt_tax.setRcpt_no_vttax(rs.getString("rcpt_no"));
                subdobjvt_tax.setTax_from_vttax(rs.getString("tax_from"));
                subdobjvt_tax.setTax_upto_vttax(rs.getString("tax_upto"));
                subdobjvt_tax.setTax_mode_descr_vttax(rs.getString("tax_mode_descr"));
                dataSubListVT_TAX.add(subdobjvt_tax);
                //  dobj.setVTtaxDetailsList(dataSubListVT_TAX);
                i++;
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + "--" + ex.getStackTrace()[0]);
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
        return dataSubListVT_TAX;
    }

    // Print Difference of Tax Details
    public static ArrayList<TaxClearanceCertificatePrintReportSubDobj> getDiffOfTaxDetailsPrintDobj(String regn_no, String appl_no) throws VahanException, ParseException {
        TransactionManager tmgr = null;
        TaxClearanceCertificatePrintReportDobj dobj = null;
        TaxClearanceCertificatePrintReportSubDobj subdobjDiff_tax = null;
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        ArrayList<TaxClearanceCertificatePrintReportDobj> dataList = new ArrayList<TaxClearanceCertificatePrintReportDobj>();
        ArrayList<TaxClearanceCertificatePrintReportSubDobj> dataSubListDiff_TAX = new ArrayList<TaxClearanceCertificatePrintReportSubDobj>();
        try {
            String sql = " SELECT a.regn_no as Regn_no, a.fees, a.fine, a.rcpt_no, to_char(a.rcpt_dt, 'dd-Mon-yyyy') as rcpt_dt,a.pur_cd as pur_cd,"
                    + " b.descr as pur_cd_descr "
                    + " from " + TableList.VT_FEE + " a left join " + TableList.TM_PURPOSE_MAST + " b on a.pur_cd = b.pur_cd "
                    + " where a.regn_no=? and a.pur_cd = 86 and a.state_cd = ? order by a.rcpt_dt DESC limit 5";
            tmgr = new TransactionManager("getTCCPrintDobj");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            int i = 1;
            while (rs.next()) {
                subdobjDiff_tax = new TaxClearanceCertificatePrintReportSubDobj();
                subdobjDiff_tax.setFees_Diff_tax(rs.getString("fees"));
                subdobjDiff_tax.setFine_Diff_tax(rs.getString("fine"));
                subdobjDiff_tax.setRcpt_no_Diff_tax(rs.getString("rcpt_no"));
                subdobjDiff_tax.setRcpt_dt_Diff_tax(rs.getString("rcpt_dt"));
                dataSubListDiff_TAX.add(subdobjDiff_tax);
                i++;
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + "--" + ex.getStackTrace()[0]);
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
        return dataSubListDiff_TAX;
    }

    public static TaxClearanceCertificateDetailDobj isRegnExistForTCC(String regno, String state_cd, int off_cd) {
        boolean isExist = false;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        TaxClearanceCertificateDetailDobj dobj = null;
        String sql;
        RowSet rs;
        try {
            DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            Date CurrDate = new Date();
            String currentDt = format.format(CurrDate);
            tmgr = new TransactionManager("isRegnExistForTCC");
            sql = " select * from " + TableList.VT_OWNER + " where regn_no  =? and off_cd=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regno);
            ps.setInt(2, off_cd);
            ps.setString(3, state_cd);

            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new TaxClearanceCertificateDetailDobj();
                dobj.setRegno(rs.getString("regn_no"));

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
            return dobj;
        }
    }

    // Print via only Application number
    public static TaxClearanceCertificateDetailDobj isApplExistForTCC(String applNo) {
        boolean isExist = false;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        TaxClearanceCertificateDetailDobj dobj = null;
        String sql;
        RowSet rs;
        try {
            DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            Date CurrDate = new Date();
            String currentDt = format.format(CurrDate);
            tmgr = new TransactionManager("Check regn no");
            sql = "select DISTINCT a.appl_no,a.regn_no, b.state_cd, b.off_cd from  " + TableList.VHA_TCC_PRINT + " a, " + TableList.VT_FEE + " b "
                    + " where a.appl_no=? and a.regn_no = b.regn_no and b.state_cd = ? and b.off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getUserSeatOffCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new TaxClearanceCertificateDetailDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegno(rs.getString("regn_no"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setState_cd(rs.getString("state_cd"));
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
            return dobj;
        }
    }

    public static void insertIntoTCCHistory(TransactionManager tmgr, String appl_no) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;

        //inserting data into VHA_TCC_PRINT from VA_TCC_PRINT
        sql = "INSERT INTO " + TableList.VHA_TCC_PRINT
                + " SELECT current_timestamp as moved_on, ? as moved_by, * "
                + "  FROM  " + TableList.VA_TCC_PRINT
                + " WHERE  appl_no=? and state_cd=? and off_cd=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.setString(3, Util.getUserStateCode());
        ps.setInt(4, Util.getUserSeatOffCode());
        ps.executeUpdate();
    } // end of insertIntoTCCHistory

    public static TaxClearanceCertificateDetailDobj isRegnExistFor_TCC_before_SevenDays(String regno, String state_cd, int off_cd) {
        boolean isExist = false;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        TaxClearanceCertificateDetailDobj dobj = null;
        String sql;
        RowSet rs;
        try {
            tmgr = new TransactionManager("isRegnExistFor_TCC_before_SevenDays");
            ps = tmgr.prepareStatement("select distinct appl_no, regn_no, state_cd, off_cd,coalesce(tcc_no,'') as tcc_no"
                    + " from " + TableList.VA_TCC_PRINT
                    + " where state_cd = ? and off_cd = ? and regn_no=?");
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserSeatOffCode());
            ps.setString(3, regno);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                dobj = new TaxClearanceCertificateDetailDobj();
                dobj.setRegno(rs.getString("regn_no"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setTcc_no(rs.getString("tcc_no"));
                dobj.setAppl_no(rs.getString("appl_no"));
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
            return dobj;
        }
    }
}
