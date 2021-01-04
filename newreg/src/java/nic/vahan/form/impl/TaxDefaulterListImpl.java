/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.reports.TaxDefaulterListDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author afzal
 */
public class TaxDefaulterListImpl {

    private static final Logger LOGGER = Logger.getLogger(TaxDefaulterListImpl.class);

    public static List<TaxDefaulterListDobj> getTaxDefaulterClassWise(String state_cd, int officeCode, int pur_cd, int defaulterNoticeGracePeriod) throws VahanException {
        ArrayList<TaxDefaulterListDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        TaxDefaulterListDobj dobj = null;
        String sql = null;
        try {
            sql = "select b.descr vch_class,count(a.vh_class) as total,a.vh_class as vch_class_cd,to_char(current_timestamp,'dd_Mon_yyyy_hh24_mi_ss') as cdate"
                    + " from " + TableList.VT_TAX_DEFAULTER + " a "
                    + " left outer join " + TableList.VM_VH_CLASS + " b on b.vh_class = a.vh_class "
                    + " inner join " + TableList.VT_OWNER + " c on c.regn_no = a.regn_no and c.state_cd=a.state_cd and c.off_cd = a.off_cd and c.status in ('A','Y')"
                    + " where a.state_cd = ? and a.off_cd = ? and a.pur_cd=? and a.tax_from + " + defaulterNoticeGracePeriod + "< current_date "
                    + " group by 1,3"
                    + " order by 1,3";
            tmgr = new TransactionManagerReadOnly("getTaxDefaulterClassWise");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, officeCode);
            ps.setInt(3, pur_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                dobj = new TaxDefaulterListDobj();
                dobj.setVch_class_desc(rs.getString("vch_class"));
                dobj.setVch_class_cd(rs.getInt("vch_class_cd"));
                dobj.setTotalCount(rs.getString("total"));
                dobj.setDownloadFileName(state_cd + officeCode + "_TAXDEFAULTER_" + rs.getString("cdate"));
                list.add(dobj);
            }
        } catch (SQLException e) {
            throw new VahanException("Problem in getting Details from getTaxDefaulterClassWise!!");
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
        return list;
    }

    public static TaxDefaulterListDobj getVchClassWiseDataListener(String state_cd, int off_cd, int vch_class, int defaulterNoticeGracePeriod) throws VahanException {
        ArrayList<TaxDefaulterListDobj> list = new ArrayList();
        ArrayList<TaxDefaulterListDobj> listDetails = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        TaxDefaulterListDobj dobj = new TaxDefaulterListDobj();
        TaxDefaulterListDobj dobjDetails = null;
        String sql = null;
        try {
            listDetails = new ArrayList();
            sql = "SELECT def.regn_no,def.vh_class,ow.chasi_no,ow.owner_name,tmoff.off_name,tms.descr as state_name,ow.f_name,to_char(ow.regn_dt,'dd-Mon-yyyy') regndate,to_char(ow.fit_upto,'dd-Mon-yyyy') fitdate,hp.fncr_name,"
                    + " upper(COALESCE(hp.fncr_add1::varchar, '') || ',' || COALESCE(hp.fncr_add2::varchar, '') || ',' || COALESCE(hp.fncr_add3::varchar, '') || ',' || COALESCE(hp.fncr_district_name::varchar, '') || ',' || COALESCE(hp.fncr_state_name::varchar, '') || ',' || COALESCE(hp.fncr_pincode::varchar, ''))  as fincraddress, b.descr as vh_class_desc,"
                    + " COALESCE(ow.c_add1::varchar, '') || ',' || COALESCE(ow.c_add2::varchar, '') || ',' || COALESCE(ow.c_add3::varchar, '') || ',' || "
                    + " COALESCE(tmd.descr::varchar, '') || ',' || COALESCE(tms.descr::varchar, '') || ',' || COALESCE(ow.c_pincode::varchar, '') as address ,ow.vch_catg,to_char(tax_from,'dd-Mon-yyyy') tax_from,"
                    + " to_char(tax_upto,'dd-Mon-yyyy') tax_upto,ow.ld_wt,ow.unld_wt,ow.ld_wt+ow.unld_wt as grosswt,ow.seat_cap, def.pur_cd, to_char(tax_cleared_upto,'dd-Mon-yyyy') tax_cleared_upto,"
                    + " COALESCE(tax_amt::numeric, '0') as tax_amt, COALESCE(tax_fine::numeric, '0') as tax_fine, (COALESCE(tax_amt::numeric, '0')+COALESCE(tax_fine::numeric, '0')) as  total_amt ,to_char(def.op_dt,'dd-Mon-yyyy') op_dt,to_char(current_timestamp,'dd-Mon-yyyy hh24:mi:ss') as printed_on,c.descr \n"
                    + " FROM   " + TableList.VT_TAX_DEFAULTER + " def \n"
                    + " left outer join " + TableList.VT_OWNER + " ow on ow.regn_no=def.regn_no and ow.state_cd=def.state_cd and ow.off_cd=def.off_cd \n"
                    + " left outer join " + TableList.VV_HYPTH + " hp on hp.regn_no=def.regn_no and hp.state_cd=def.state_cd and hp.off_cd=def.off_cd \n"
                    + " left outer join " + TableList.TM_PURPOSE_MAST + " c on c.pur_cd=def.pur_cd \n"
                    + " left join " + TableList.VM_VH_CLASS + "  b on b.vh_class = def.vh_class\n"
                    + " left join " + TableList.TM_STATE + " tms on  tms.state_code = ow.state_cd\n"
                    + " left join " + TableList.TM_DISTRICT + " tmd on  tmd.dist_cd = ow.c_district\n"
                    + " LEFT OUTER JOIN " + TableList.TM_OFFICE + " tmoff on tmoff.state_cd = def.state_cd and tmoff.off_cd = def.off_cd \n"
                    + " where def.state_cd = ? and def.off_cd = ? and def.vh_class=? and def.tax_from + " + defaulterNoticeGracePeriod + "< current_date order by 1 ";
            tmgr = new TransactionManagerReadOnly("getVchCatgWiseDataListener");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.setInt(3, vch_class);
            RowSet rsdetails = tmgr.fetchDetachedRowSet_No_release();
            while (rsdetails.next()) {
                dobjDetails = new TaxDefaulterListDobj();
                dobjDetails.setRegn_no(rsdetails.getString("regn_no"));
                dobjDetails.setOwner_name(rsdetails.getString("owner_name"));
                dobjDetails.setFatherName(rsdetails.getString("f_name"));
                dobjDetails.setAddress(rsdetails.getString("address").toUpperCase());
                dobjDetails.setVch_class_desc(rsdetails.getString("vh_class_desc"));
                dobjDetails.setTax_cleared_upto(rsdetails.getString("tax_cleared_upto"));
                dobjDetails.setVch_catg(rsdetails.getString("vch_catg"));
                dobjDetails.setTaxFrom(rsdetails.getString("tax_from"));
                dobjDetails.setTaxupTo(rsdetails.getString("tax_upto"));
                dobjDetails.setFitvalidt(rsdetails.getString("fitdate"));
                dobjDetails.setRegndate(rsdetails.getString("regndate"));
                dobjDetails.setLdwt(String.valueOf(rsdetails.getInt("ld_wt")));
                dobjDetails.setUnldwt(String.valueOf(rsdetails.getInt("unld_wt")));
                dobjDetails.setGrosswt(String.valueOf(rsdetails.getInt("grosswt")));
                dobjDetails.setFncr_name(rsdetails.getString("fncr_name"));
                dobjDetails.setSeatcap(String.valueOf(rsdetails.getInt("seat_cap")));
                dobjDetails.setTaxamt(String.valueOf(rsdetails.getInt("tax_amt")));
                dobjDetails.setTaxfine(String.valueOf(rsdetails.getInt("tax_fine")));
                dobjDetails.setTotalamt(String.valueOf(rsdetails.getInt("total_amt")));
                dobjDetails.setOffName(rsdetails.getString("off_name"));
                dobjDetails.setPrintDate(rsdetails.getString("printed_on"));
                dobjDetails.setStateName(rsdetails.getString("state_name"));
                dobjDetails.setVch_class_cd(rsdetails.getInt("vh_class"));
                dobjDetails.setPurcdDescr(rsdetails.getString("descr"));
                listDetails.add(dobjDetails);
            }
            dobj.setListFileExport(listDetails);
        } catch (SQLException e) {
            throw new VahanException("Problem in getting Details from getVchClassWiseDataListener!!");
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

    public static List<TaxDefaulterListDobj> getTaxDefaulterCategoryWise(String state_cd, int officeCode, int pur_cd, int defaulterNoticeGracePeriod) throws VahanException {
        ArrayList<TaxDefaulterListDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        TaxDefaulterListDobj dobj = null;
        String sql = null;
        try {
            sql = "select b.catg_desc vch_catg,count(a.vch_catg) as total,a.vch_catg as vch_catg_cd,to_char(current_timestamp,'dd_Mon_yyyy_hh24_mi_ss') as cdate"
                    + " from " + TableList.VT_TAX_DEFAULTER + " a "
                    + " left outer join " + TableList.VM_VCH_CATG + " b on b.catg = a.vch_catg "
                    + " inner join " + TableList.VT_OWNER + " c on c.regn_no = a.regn_no and c.state_cd=a.state_cd and c.off_cd = a.off_cd and c.status in ('A','Y')"
                    + " where a.state_cd = ? and a.off_cd = ? and a.pur_cd=? and a.tax_from + " + defaulterNoticeGracePeriod + " < current_date "
                    + " group by 1,3"
                    + " order by 1,3";
            tmgr = new TransactionManagerReadOnly("getTaxDefaulterCategoryWise");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, officeCode);
            ps.setInt(3, pur_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                dobj = new TaxDefaulterListDobj();
                dobj.setVch_catg(rs.getString("vch_catg"));
                dobj.setVch_catg_cd(rs.getString("vch_catg_cd"));
                dobj.setTotalCount(rs.getString("total"));
                dobj.setDownloadFileName(state_cd + officeCode + "_TAXDEFAULTER_CATEGORYWISE_" + rs.getString("cdate"));
                list.add(dobj);
            }

        } catch (SQLException e) {
            throw new VahanException("Problem in getting Details from getTaxDefaulterCategoryWise!!");
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
        return list;
    }

    public static TaxDefaulterListDobj getVchCatgWiseDataListener(String state_cd, int officeCode, TaxDefaulterListDobj download) throws VahanException {
        ArrayList<TaxDefaulterListDobj> list = new ArrayList();
        ArrayList<TaxDefaulterListDobj> listDetails = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        TaxDefaulterListDobj dobj = new TaxDefaulterListDobj();
        TaxDefaulterListDobj dobjDetails = null;
        String sql = null;
        try {
            listDetails = new ArrayList();
            sql = "SELECT def.regn_no,ow.chasi_no,ow.owner_name,tmoff.off_name,tms.descr as state_name,ow.f_name,to_char(ow.regn_dt,'dd-Mon-yyyy') regndate,to_char(ow.fit_upto,'dd-Mon-yyyy') fitdate,hp.fncr_name,"
                    + " upper(COALESCE(hp.fncr_add1::varchar, '') || ',' || COALESCE(hp.fncr_add2::varchar, '') || ',' || COALESCE(hp.fncr_add3::varchar, '') || ',' || COALESCE(hp.fncr_district_name::varchar, '') || ',' || COALESCE(hp.fncr_state_name::varchar, '') || ',' || COALESCE(hp.fncr_pincode::varchar, ''))  as fincraddress, b.descr as vh_class_desc,COALESCE(ow.c_add1::varchar, '') || ',' || COALESCE(ow.c_add2::varchar, '') || ',' || COALESCE(ow.c_add3::varchar, '') || ',' || "
                    + " COALESCE(tmd.descr::varchar, '') || ',' || COALESCE(tms.descr::varchar, '') || ',' || COALESCE(ow.c_pincode::varchar, '') as address ,ow.vch_catg,to_char(tax_from,'dd-Mon-yyyy') tax_from,"
                    + " to_char(tax_upto,'dd-Mon-yyyy') tax_upto,ow.ld_wt,ow.unld_wt,ow.ld_wt+ow.unld_wt as grosswt,ow.seat_cap, def.pur_cd, to_char(tax_cleared_upto,'dd-Mon-yyyy') tax_cleared_upto,to_char(current_timestamp,'dd-Mon-yyyy hh24:mi:ss') as printed_on,"
                    + " COALESCE(tax_amt::numeric, '0')tax_amt, COALESCE(tax_fine::numeric, '0')tax_fine, (COALESCE(tax_amt::numeric, '0')+COALESCE(tax_fine::numeric, '0')) as  total_amt ,to_char(def.op_dt,'dd-Mon-yyyy') op_dt,c.descr"
                    + " FROM   " + TableList.VT_TAX_DEFAULTER + " def "
                    + " left outer join " + TableList.VT_OWNER + " ow on ow.regn_no=def.regn_no and ow.state_cd=def.state_cd and ow.off_cd=def.off_cd"
                    + " left outer join " + TableList.VV_HYPTH + " hp on hp.regn_no=def.regn_no and hp.state_cd=def.state_cd and hp.off_cd=def.off_cd"
                    + " left join " + TableList.VM_VH_CLASS + "  b on b.vh_class = def.vh_class\n"
                    + " left outer join " + TableList.TM_PURPOSE_MAST + " c on c.pur_cd=def.pur_cd "
                    + " left join " + TableList.TM_STATE + " tms on  tms.state_code = ow.state_cd\n"
                    + " left join " + TableList.TM_DISTRICT + " tmd on  tmd.dist_cd = ow.c_district\n"
                    + " LEFT OUTER JOIN " + TableList.TM_OFFICE + " tmoff on tmoff.state_cd = def.state_cd and tmoff.off_cd = def.off_cd \n"
                    + " where def.state_cd = ? and def.off_cd = ? and def.vch_catg=? order by 1 ";
            tmgr = new TransactionManagerReadOnly("getTaxDefaulterCategoryWise");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, officeCode);
            ps.setString(3, download.getVch_catg_cd());
            RowSet rsdetails = tmgr.fetchDetachedRowSet();
            while (rsdetails.next()) {
                dobjDetails = new TaxDefaulterListDobj();
                dobjDetails.setRegn_no(rsdetails.getString("regn_no"));
                dobjDetails.setOwner_name(rsdetails.getString("owner_name"));
                dobjDetails.setFatherName(rsdetails.getString("f_name"));
                dobjDetails.setAddress(rsdetails.getString("address").toUpperCase());
                dobjDetails.setVch_class_desc(rsdetails.getString("vh_class_desc"));
                dobjDetails.setTax_cleared_upto(rsdetails.getString("tax_cleared_upto"));
                dobjDetails.setVch_catg(rsdetails.getString("vch_catg"));
                dobjDetails.setTaxFrom(rsdetails.getString("tax_from"));
                dobjDetails.setTaxupTo(rsdetails.getString("tax_upto"));
                dobjDetails.setFitvalidt(rsdetails.getString("fitdate"));
                dobjDetails.setRegndate(rsdetails.getString("regndate"));
                dobjDetails.setLdwt(String.valueOf(rsdetails.getInt("ld_wt")));
                dobjDetails.setUnldwt(String.valueOf(rsdetails.getInt("unld_wt")));
                dobjDetails.setGrosswt(String.valueOf(rsdetails.getInt("grosswt")));
                dobjDetails.setFncr_name(rsdetails.getString("fncr_name"));
                dobjDetails.setSeatcap(String.valueOf(rsdetails.getInt("seat_cap")));
                dobjDetails.setTaxamt(String.valueOf(rsdetails.getInt("tax_amt")));
                dobjDetails.setTaxfine(String.valueOf(rsdetails.getInt("tax_fine")));
                dobjDetails.setTotalamt(String.valueOf(rsdetails.getInt("total_amt")));
                dobjDetails.setOffName(rsdetails.getString("off_name"));
                dobjDetails.setPrintDate(rsdetails.getString("printed_on"));
                dobjDetails.setStateName(rsdetails.getString("state_name"));
                dobjDetails.setPurcdDescr(rsdetails.getString("descr"));
                listDetails.add(dobjDetails);
            }
            dobj.setListFileExport(listDetails);



        } catch (SQLException e) {
            throw new VahanException("Problem in getting Details from getVchCatgWiseDataListener!!");
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

    public static List<TaxDefaulterListDobj> getTaxDefaulterInformation(String state_cd, int officeCode, int pur_cd, String yrfilter, int vhclass) throws VahanException {
        ArrayList<TaxDefaulterListDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        TaxDefaulterListDobj dobj = null;
        String sql = null;
        String filterby = "EXTRACT(year from age(das.tax_cleared_upto))";
        if (yrfilter.equalsIgnoreCase("0")) {
            filterby += "<1";
        } else if (yrfilter.equalsIgnoreCase("1")) {
            filterby += ">=1 and " + filterby + " <2";
        } else if (yrfilter.equalsIgnoreCase("2")) {
            filterby += ">=2 and " + filterby + "<3";
        } else if (yrfilter.equalsIgnoreCase("3")) {
            filterby += ">=3 and " + filterby + "<4";
        } else if (yrfilter.equalsIgnoreCase("4")) {
            filterby += ">=4 and " + filterby + "<5";
        } else if (yrfilter.equalsIgnoreCase("5")) {
            filterby += ">=5 and " + filterby + "<6";
        } else if (yrfilter.equalsIgnoreCase("6")) {
            filterby += ">=6 and " + filterby + "<7";
        } else if (yrfilter.equalsIgnoreCase("7")) {
            filterby += ">=7 and " + filterby + "<8";
        } else if (yrfilter.equalsIgnoreCase("8")) {
            filterby += ">=8 and " + filterby + "<9";
        } else if (yrfilter.equalsIgnoreCase("9")) {
            filterby += ">=9 and " + filterby + "<10";
        } else if (yrfilter.equalsIgnoreCase("10")) {
            filterby += ">=10";
        }

        try {
            sql = "select das.regn_no,to_char(vv.regn_dt,'dd-Mon-yyyy') as regn_dt,b.descr vch_class,vv.owner_name,vv.f_name,to_char(das.tax_cleared_upto,'dd-Mon-yyyy') as tax_cleared_upto, hp.fncr_name, COALESCE(hp.fncr_add1::varchar, '') || ',' || COALESCE(hp.fncr_add2::varchar, '') || ',' || COALESCE(hp.fncr_add3::varchar, '') || ',' || COALESCE(tm.descr::varchar, '')  as fincraddress,to_char(das.tax_from,'dd-Mon-yyyy') as tax_from,to_char(das.tax_upto,'dd-Mon-yyyy') as tax_upto,das.tax_amt,das.tax_fine, "
                    + " COALESCE(vv.c_add1::varchar, '') || ',' || COALESCE(vv.c_add2::varchar, '') || ',' || COALESCE(vv.c_add3::varchar, '') || ',' || COALESCE(tmd.descr::varchar, '') || ',' || COALESCE(tms.descr::varchar, '') || ',' || COALESCE(vv.c_pincode::varchar, '') as address,tms.descr as state_name,tmoff.off_name,tmc.rcpt_heading,tmc.rcpt_subheading,to_char(current_timestamp,'dd-Mon-yyyy') as printed_on,taxh.head4,taxh.head5,to_char(vv.fit_upto,'DD-MON-yyyy') as fit_upto \n"
                    + " from " + TableList.VT_TAX_DEFAULTER + "  das \n"
                    + " left join " + TableList.VT_OWNER + " vv on das.regn_no=vv.regn_no and das.state_cd = vv.state_cd and das.off_cd = vv.off_cd\n"
                    + " left join " + TableList.VM_VH_CLASS + "  b on b.vh_class = das.vh_class\n"
                    + " left join " + TableList.VT_HYPTH + " hp on hp.regn_no = das.regn_no and hp.state_cd = das.state_cd and hp.off_cd = das.off_cd\n"
                    + " left join " + TableList.TM_STATE + " tm on  tm.state_code = hp.fncr_state \n"
                    + " left join " + TableList.TM_STATE + " tms on  tms.state_code = das.state_cd\n"
                    + " left join " + TableList.TM_DISTRICT + " tmd on  vv.c_district = tmd.dist_cd\n"
                    + " LEFT OUTER JOIN " + TableList.TM_OFFICE + " tmoff on tmoff.state_cd = das.state_cd and tmoff.off_cd = das.off_cd \n"
                    + " left join " + TableList.TM_CONFIGURATION + " tmc on tmc.state_cd = das.state_cd \n"
                    + " left join " + TableList.VT_TAX_NOTICE_HEAD + " taxh on taxh.state_cd = das.state_cd \n"
                    + " where " + filterby + " and das.state_cd=? AND das.OFF_CD=? AND das.PUR_CD=? AND das.VH_CLASS=? order by 1 ";

            tmgr = new TransactionManagerReadOnly("getTaxDefaulterClassWise");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, officeCode);
            ps.setInt(3, pur_cd);
            ps.setInt(4, vhclass);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                dobj = new TaxDefaulterListDobj();
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setRegndate(rs.getString("regn_dt"));
                dobj.setVch_class_desc(rs.getString("vch_class"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setAddress(rs.getString("address"));
                dobj.setFncr_name(rs.getString("fncr_name"));
                dobj.setFncr_address(rs.getString("fincraddress"));
                dobj.setFatherName(rs.getString("f_name"));
                if (state_cd.contains("UK")) {
                    dobj.setTax_cleared_upto("");
                } else {
                    dobj.setTax_cleared_upto(rs.getString("tax_cleared_upto"));
                }
                dobj.setTaxFrom(rs.getString("tax_from"));
                dobj.setTaxupTo(rs.getString("tax_upto"));
                dobj.setTaxamt(rs.getString("tax_amt"));
                dobj.setTaxfine(rs.getString("tax_fine"));
                dobj.setStateName(rs.getString("state_name"));
                dobj.setOffName(rs.getString("off_name"));
                if (state_cd.contains("WB")) {
                    dobj.setRcpt_heading("GOVERNMENT OF WEST BENGAL");
                    dobj.setRcpt_subheading("TRANSPORT DIRECTORATE");
                } else {
                    dobj.setRcpt_heading(rs.getString("rcpt_heading"));
                    dobj.setRcpt_subheading(rs.getString("rcpt_subheading"));
                }
                dobj.setPrintDate(rs.getString("printed_on"));
                dobj.setTaxNoticeHead4(rs.getString("head4"));
                dobj.setTaxNoticeHead5(rs.getString("head5"));
                dobj.setFitvalidt(rs.getString("fit_upto"));
                list.add(dobj);
            }

        } catch (SQLException e) {
            throw new VahanException("Problem in getting Details from getTaxDefaulterInformation!!");
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
        return list;
    }

    public static String isRegnExistForTaxDefaulter(String regn_no, String state_cd, int off_cd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        String regnno = null;
        String sql;
        RowSet rs;

        try {
            tmgr = new TransactionManager("isRegnExistForTaxDefaulter");
            sql = "select regn_no from  " + TableList.VT_TAX_DEFAULTER + " where regn_no=? and state_cd=? and off_cd=? order by op_dt DESC limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                regnno = rs.getString("regn_no");
            } else {
                sql = "select regn_no from  " + TableList.VT_OWNER + " where regn_no=? and state_cd=? and off_cd=? order by op_dt DESC limit 1";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regn_no);
                ps.setString(2, state_cd);
                ps.setInt(3, off_cd);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (!rs.next()) {
                    throw new VahanException("Either Invalid Registration No OR Registration No Doesn't Bolong to Current Office");
                }
            }
        } catch (SQLException e) {
            throw new VahanException("Problem in getting Details from isRegnExistForTaxDefaulter!!");
        } catch (VahanException ve) {
            throw new VahanException(ve.getMessage());
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return regnno;
    }

    public static List<TaxDefaulterListDobj> getTaxDefaulterInformationRegnNoWise(String state_cd, int officeCode, int pur_cd, String regn_no, int defaulterNoticeGracePeriod) throws VahanException {
        ArrayList<TaxDefaulterListDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        TaxDefaulterListDobj dobj = null;
        String sql = null;

        try {
            sql = "select das.regn_no,to_char(vv.regn_dt,'dd-Mon-yyyy') as regn_dt,b.descr vch_class,vv.owner_name,vv.f_name,to_char(das.tax_cleared_upto,'dd-Mon-yyyy') as tax_cleared_upto, hp.fncr_name, COALESCE(hp.fncr_add1::varchar, '') || ',' || COALESCE(hp.fncr_add2::varchar, '') || ',' || COALESCE(hp.fncr_add3::varchar, '') || ',' || COALESCE(tm.descr::varchar, '')  as fincraddress,to_char(das.tax_from,'dd-Mon-yyyy') as tax_from,to_char(das.tax_upto,'dd-Mon-yyyy') as tax_upto,das.tax_amt,das.tax_fine, "
                    + " COALESCE(vv.c_add1::varchar, '') || ',' || COALESCE(vv.c_add2::varchar, '') || ',' || COALESCE(vv.c_add3::varchar, '') || ',' || COALESCE(tmd.descr::varchar, '') || ',' || COALESCE(tms.descr::varchar, '') || ',' || COALESCE(vv.c_pincode::varchar, '') as address,tms.descr as state_name,tmoff.off_name,tmc.rcpt_heading,tmc.rcpt_subheading,to_char(current_timestamp,'dd-Mon-yyyy') as printed_on,taxh.head4,taxh.head5,to_char(vv.fit_upto,'DD-MON-yyyy') as fit_upto \n"
                    + " from " + TableList.VT_TAX_DEFAULTER + "  das \n"
                    + " left join " + TableList.VT_OWNER + " vv on das.regn_no=vv.regn_no and das.state_cd = vv.state_cd and das.off_cd = vv.off_cd\n"
                    + " left join " + TableList.VM_VH_CLASS + "  b on b.vh_class = das.vh_class\n"
                    + " left join " + TableList.VT_HYPTH + " hp on hp.regn_no = das.regn_no and hp.state_cd = das.state_cd and hp.off_cd = das.off_cd\n"
                    + " left join " + TableList.TM_STATE + " tm on  tm.state_code = hp.fncr_state \n"
                    + " left join " + TableList.TM_STATE + " tms on  tms.state_code = das.state_cd\n"
                    + " left join " + TableList.TM_DISTRICT + " tmd on  vv.c_district = tmd.dist_cd\n"
                    + " LEFT OUTER JOIN " + TableList.TM_OFFICE + " tmoff on tmoff.state_cd = das.state_cd and tmoff.off_cd = das.off_cd \n"
                    + " left join " + TableList.TM_CONFIGURATION + " tmc on tmc.state_cd = das.state_cd \n"
                    + " left join " + TableList.VT_TAX_NOTICE_HEAD + " taxh on taxh.state_cd = das.state_cd \n"
                    + " where das.state_cd=? AND das.OFF_CD=? AND das.PUR_CD=? AND das.regn_no=? and das.tax_from + " + defaulterNoticeGracePeriod + "< current_date "
                    + " order by 1 ";

            tmgr = new TransactionManagerReadOnly("getTaxDefaulterClassWise");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, officeCode);
            ps.setInt(3, pur_cd);
            ps.setString(4, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                dobj = new TaxDefaulterListDobj();
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setRegndate(rs.getString("regn_dt"));
                dobj.setVch_class_desc(rs.getString("vch_class"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setAddress(rs.getString("address"));
                dobj.setFncr_name(rs.getString("fncr_name"));
                dobj.setFncr_address(rs.getString("fincraddress"));
                dobj.setFatherName(rs.getString("f_name"));
                if (state_cd.contains("UK")) {
                    dobj.setTax_cleared_upto("");
                } else {
                    dobj.setTax_cleared_upto(rs.getString("tax_cleared_upto"));
                }
                dobj.setTaxFrom(rs.getString("tax_from"));
                dobj.setTaxupTo(rs.getString("tax_upto"));
                dobj.setTaxamt(rs.getString("tax_amt"));
                dobj.setTaxfine(rs.getString("tax_fine"));
                dobj.setStateName(rs.getString("state_name"));
                dobj.setOffName(rs.getString("off_name"));
                if (state_cd.contains("WB")) {
                    dobj.setRcpt_heading("GOVERNMENT OF WEST BENGAL");
                    dobj.setRcpt_subheading("TRANSPORT DIRECTORATE");
                } else {
                    dobj.setRcpt_heading(rs.getString("rcpt_heading"));
                    dobj.setRcpt_subheading(rs.getString("rcpt_subheading"));
                }
                dobj.setPrintDate(rs.getString("printed_on"));
                dobj.setTaxNoticeHead4(rs.getString("head4"));
                dobj.setTaxNoticeHead5(rs.getString("head5"));
                dobj.setFitvalidt(rs.getString("fit_upto"));
                list.add(dobj);
            }

        } catch (SQLException e) {
            throw new VahanException("Problem in getting Details from getTaxDefaulterInformationRegnNoWise!!");
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
        return list;
    }
    // insertUpdateTaxDefaulter(taxDobj.getRegnNo(), taxUpto, taxDobj.getPur_cd(), tmgr);//afzal taxduefromdate

    public static boolean updateTaxDefaulterInformationRegnNoWise(String state_cd, int officeCode, String regn_no) throws VahanException {
        boolean updateTaxDefaulter = false;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        Owner_dobj owner_dobj = null;

        try {
            sql = "select * from " + TableList.VT_TAX_DEFAULTER + "\n"
                    + " where state_cd=? AND OFF_CD=? AND regn_no=? order by op_dt ";

            tmgr = new TransactionManager("getTaxDefaulterClassWise");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, officeCode);
            ps.setString(3, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) // found
            {
                owner_dobj = new Owner_dobj();
                owner_dobj.setRegn_no(rs.getString("regn_no"));
                Date dateDueFrom = TaxServer_Impl.getTaxDueFromDate(owner_dobj, rs.getInt("pur_cd"));
                TaxServer_Impl.insertUpdateTaxDefaulter(rs.getString("regn_no"), dateDueFrom, rs.getInt("pur_cd"), tmgr);
                updateTaxDefaulter = true;
            }
            tmgr.commit();

        } catch (SQLException e) {
            throw new VahanException("Problem in getting Details from updateTaxDefaulterInformationRegnNoWise!!");
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
        return updateTaxDefaulter;
    }

    public static boolean insertRecordforTaxDefaulter(String regn_no, String state_cd, int officeCode) throws VahanException {
        boolean insertForTaxDefaulter = false;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        Owner_dobj owner_dobj = null;
        try {
            tmgr = new TransactionManager("getTaxDefaulterClassWise");
            owner_dobj = new Owner_dobj();
            owner_dobj.setRegn_no(regn_no);
            Date dateDueFrom = TaxServer_Impl.getTaxDueFromDate(owner_dobj, 58);
            if ("NEW,TEMPREG".contains(regn_no) || dateDueFrom == null) {
                throw new VahanException("Error in Adding in Tax Defaulter. Either it is NEW/TEMPREG Vehicle No OR Tax Details is not found for this Vehicle No");
            }
            TaxServer_Impl.insertUpdateTaxDefaulter(regn_no, dateDueFrom, 58, tmgr);
            tmgr.commit();
            insertForTaxDefaulter = true;
        } catch (SQLException e) {
            throw new VahanException("Problem in getting Details from updateTaxDefaulterInformationRegnNoWise!!");
        } catch (VahanException ve) {
            throw new VahanException(ve.getMessage());
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

        return insertForTaxDefaulter;
    }
}
