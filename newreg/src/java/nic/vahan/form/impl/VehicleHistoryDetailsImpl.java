/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC075
 */
public class VehicleHistoryDetailsImpl {

    private static final Logger LOGGER = Logger.getLogger(VehicleHistoryDetailsImpl.class);

    public static Map makeMasterTableDO(String historyListValue, String regNo, String stateCd, int offCd)
            throws VahanException {
        String[][] metadata = null;
        String[][] data = null;
        Map dataMap = new LinkedHashMap();
        TransactionManagerReadOnly tmg = null;
        String query = "";
        String message = "";

        try {
            if (historyListValue.equals("FEES")) {
                query = "select a.rcpt_no ,to_char( rcpt_dt,'dd-Mon-yyyy hh:mi:ss AM') ,fees,fine ,b.descr , "
                        + " COALESCE(o.off_name, '') || ',' || a.state_cd as office "
                        + " ,CASE when instrument_type = 'G' THEN i.instrument_cd else 0  END as grn_no "
                        + " from " + TableList.VT_FEE + " a "
                        + " left join  vp_appl_rcpt_mapping r on a.rcpt_no = r.rcpt_no and a.state_cd = r.state_cd and a.off_cd = r.off_cd"
                        + " left join vt_instruments i on i.instrument_cd = r.instrument_cd and i.state_cd = a.state_cd and i.off_cd = a.off_cd"
                        + " left join tm_purpose_mast b on b.pur_cd=a.pur_cd "
                        + " left outer join tm_office o on o.state_cd=a.state_cd and o.off_cd = a.off_cd "
                        + " where regn_no= '" + regNo + "' "
                        + " order by rcpt_dt desc";
            } else if (historyListValue.equals("ALT")) {
                query = "select appl_no,d.descr,chasi_no,eng_no,c.descr,seat_cap,stand_cap,"
                        + " unld_wt,ld_wt,b.descr,color,hp,no_cyl,COALESCE(o.off_name, '') || ',' || a.state_cd as office "
                        + " from " + TableList.VH_ALT + " a "
                        + " left join " + TableList.VM_FUEL + " b on b.code=a.fuel "
                        + " left join " + TableList.VM_BD_TYPE + " c on c.bd_type_code = a.body_type "
                        + " left join " + TableList.VM_VH_CLASS + " d on d.vh_class = a.vh_class  "
                        + " left outer join tm_office o on o.state_cd=a.state_cd and o.off_cd = a.off_cd "
                        + " where regn_no= '" + regNo + "' "
                        + " order by moved_on desc ";
            } else if (historyListValue.equals("TAX")) {
                query = "select a.rcpt_no ,COALESCE(to_char(t.tax_from, 'dd-Mon-yyyy'),to_char(a.tax_from, 'dd-Mon-yyyy')) as tax_from, COALESCE(to_char(t.tax_upto, 'dd-Mon-yyyy'),to_char(a.tax_upto, 'dd-Mon-yyyy')) as tax_upto, b.descr ,to_char(a.rcpt_dt,'dd-Mon-yyyy hh:mi:ss AM') ,\n"
                        + " COALESCE((t.tax+t.penalty+t.surcharge+t.interest+t.tax1+t.tax2+t.prv_adjustment-t.exempted-t.rebate),a.tax_amt) as total_tax ,COALESCE(o.off_name, '') || ',' || a.state_cd as office , \n"
                        + " COALESCE(t.tax,a.tax_amt) ,COALESCE(t.penalty,a.tax_fine),CASE when instrument_type = 'G' THEN i.instrument_cd else 0  END as grn_no, "
                        + " trim(case when t.exempted > 0 then 'Exempted:' || t.exempted::varchar else '' end || ' ' ||\n"
                        + " case when t.prv_adjustment > 0 then 'Previous Adjustment:' || t.prv_adjustment::varchar else '' end || ' ' ||\n"
                        + " case when t.rebate > 0 then 'Rebate:' || t.rebate::varchar else '' end || ' ' ||\n"
                        + " case when t.surcharge > 0 then 'Surcharge:' || t.surcharge::varchar else '' end || ' ' ||\n"
                        + " case when t.interest > 0 then 'Interest:' || t.interest::varchar else '' end || ' ' ||\n"
                        + " case when t.tax1 > 0 then COALESCE(f.tax1, '') || ':' || t.tax1::varchar else '' end || ' ' ||\n"
                        + " case when t.tax2 > 0 then COALESCE(f.tax2, '') || ':' || t.tax2::varchar else '' end) as breakup\n"
                        + " ,m.descr "
                        + " from  " + TableList.VT_TAX + " a "
                        + " left join vp_appl_rcpt_mapping mm  on mm.rcpt_no=a.rcpt_no and mm.state_cd=a.state_cd and mm.off_Cd=a.off_cd "
                        + " left join vt_instruments i on i.instrument_cd=mm.instrument_cd and  mm.state_cd=i.state_cd and mm.off_Cd=i.off_cd "
                        + " left outer join  " + TableList.TM_PURPOSE_MAST + " b on b.pur_cd=a.pur_cd  "
                        + " left outer join " + TableList.TM_OFFICE + "  o on o.state_cd=a.state_cd and o.off_cd = a.off_cd \n"
                        + " left outer join " + TableList.VT_TAX_BREAKUP + " t on t.rcpt_no = a.rcpt_no and a.pur_cd=t.pur_cd and t.state_cd = a.state_cd and t.off_cd = a.off_cd and t.pur_cd = a.pur_cd "
                        + " left outer join " + TableList.VM_TAX_FIELD_LABEL + " f on f.state_cd = a.state_cd\n"
                        + " left outer join " + TableList.VM_TAX_MODE + " m on m.tax_mode =a.tax_mode "
                        + " where regn_no= '" + regNo + "' "
                        + " order by rcpt_dt desc,COALESCE(to_char(t.tax_upto, 'dd-Mon-yyyy'),to_char(a.tax_upto, 'dd-Mon-yyyy'))::date desc";
            } else if (historyListValue.equals("CA")) {
                query = "select appl_no,to_char(from_dt,'dd-Mon-yyyy'),to_char(to_dt,'dd-Mon-yyyy'),c_add1,p_add1,COALESCE(o.off_name, '') || ',' || a.state_cd as office "
                        + " from " + TableList.VH_CA + " a "
                        + " left outer join tm_office o on o.state_cd=a.state_cd and o.off_cd = a.off_cd "
                        + " where regn_no= '" + regNo + "' "
                        + "  order by moved_on desc ";
            } else if (historyListValue.equals("HOV")) {
                query = "select appl_no ,hp_type,fncr_name,fncr_add1,to_char(from_dt,'dd-Mon-yyyy'),to_char(upto_dt,'dd-Mon-yyyy'),"
                        + "to_char(term_dt,'dd-Mon-yyyy'),COALESCE(o.off_name, '') || ',' || a.state_cd as office "
                        + " from " + TableList.VH_HPT + " a "
                        + " left outer join tm_office o on o.state_cd=a.state_cd and o.off_cd = a.off_cd "
                        + " where regn_no= '" + regNo + "' "
                        + "  order by moved_on desc";
            } else if (historyListValue.equals("REN")) {
                query = "select appl_no,to_char(old_fit_dt,'dd-Mon-yyyy'),to_char(new_fit_dt,'dd-Mon-yyyy'),inspected_by,to_char(inspected_dt,'dd-Mon-yyyy'),COALESCE(o.off_name, '') || ',' || a.state_cd as office "
                        + " from " + TableList.VH_RENEWAL + " a "
                        + " left outer join tm_office o on o.state_cd=a.state_cd and o.off_cd = a.off_cd "
                        + " where regn_no= '" + regNo + "' "
                        + "  order by moved_on desc";
            } else if (historyListValue.equals("TO")) {
                query = "select appl_no, owner_sr,to_char(owner_from,'dd-Mon-yyyy'),to_char(owner_upto,'dd-Mon-yyyy'),owner_name,f_name "
                        + ",c_add1,p_add1,owner_ctg,to_char(sale_dt,'dd-Mon-yyyy'), sale_amt ,"
                        + " reason ,COALESCE(o.off_name, '') || ',' || a.state_cd as office  "
                        + " from " + TableList.VH_TO + " a "
                        + " left outer join tm_office o on o.state_cd=a.state_cd and o.off_cd = a.off_cd "
                        + " where regn_no= '" + regNo + "' "
                        + " order by moved_on desc ";
            } else if (historyListValue.equals("INS")) {
                query = "select c.descr,b.descr,to_char(ins_from,'dd-Mon-yyyy'),to_char(ins_upto,'dd-Mon-yyyy'),policy_no,COALESCE(o.off_name, '') || ',' || a.state_cd as office  "
                        + " from " + TableList.VH_INSURANCE + " a "
                        + " left join " + TableList.VM_INSTYP + " b on b.instyp_code=a.ins_type "
                        + " left join " + TableList.VM_ICCODE + " c on c.ic_code=a.comp_cd "
                        + " left outer join tm_office o on o.state_cd=a.state_cd and o.off_cd = a.off_cd "
                        + " where regn_no= '" + regNo + "' "
                        + "  order by moved_on desc ";
            } else if (historyListValue.equals("REASGN")) {
                query = "select appl_no,old_regn_no,new_regn_no,reason,to_char(moved_on,'dd-Mon-yyyy'),COALESCE(o.off_name, '') || ',' || a.state_cd as office  "
                        + " from " + TableList.VH_RE_ASSIGN + " a "
                        + " left outer join tm_office o on o.state_cd=a.state_cd and o.off_cd = a.off_cd "
                        + " where new_regn_no= '" + regNo + "' "
                        + "  order by moved_on  desc";
            } else if (historyListValue.equals("DUP")) {
                query = "select appl_no, reason,fir_no,to_char(fir_dt,'dd-Mon-yyyy'),police_station,COALESCE(o.off_name, '') || ',' || a.state_cd as office  "
                        + " from " + TableList.VH_DUP + " a "
                        + " left outer join tm_office o on o.state_cd=a.state_cd and o.off_cd = a.off_cd "
                        + " where regn_no= '" + regNo + "' "
                        + "  order by moved_on desc";
            } else if (historyListValue.equals("BLACK")) {
                query = " select a.complain,to_char(a.complain_dt,'dd-Mon-yyyy') as complain_dt,a.user_name,a.action_taken,a.action_taken_by,a.action_date,a.office,a.compounding_amt from \n"
                        + " ((select case when complain_type = 62 then a.fir_no else a.complain end,a.complain_dt,b.user_name,a.action_taken,c.user_name action_taken_by,to_char(a.action_dt,'dd-Mon-yyyy') as action_date,COALESCE(o.off_name, '') || ',' || a.state_cd as office,a.compounding_amt   \n"
                        + " from " + TableList.VH_BLACKLIST + " a\n"
                        + " left join " + TableList.TM_USER_INFO + " b on b.user_cd = regexp_replace(COALESCE(trim(a.entered_by), '0'), '[^0-9]', '0', 'g')::numeric  \n"
                        + " left join " + TableList.TM_USER_INFO + " c on c.user_cd = regexp_replace(COALESCE(trim(a.action_taken_by), '0'), '[^0-9]', '0', 'g')::numeric  \n"
                        + " left outer join " + TableList.TM_OFFICE + " o on o.state_cd=a.state_cd and o.off_cd = a.off_cd  \n"
                        + " where regn_no= '" + regNo + "' )\n"
                        + " union all\n"
                        + " (select case when complain_type = 62 then a.fir_no else a.complain end,a.complain_dt,b.user_name,'' as action_taken ,'' as action_taken_by ,'' as action_date ,COALESCE(o.off_name, '') || ',' || a.state_cd as office,a.compounding_amt   \n"
                        + " from " + TableList.VT_BLACKLIST + " a\n"
                        + " left join " + TableList.TM_USER_INFO + " b on b.user_cd = regexp_replace(COALESCE(trim(a.entered_by), '0'), '[^0-9]', '0', 'g')::numeric  \n"
                        + " left outer join " + TableList.TM_OFFICE + " o on o.state_cd=a.state_cd and o.off_cd = a.off_cd  \n"
                        + " where regn_no= '" + regNo + "')\n"
                        + " ) a  order by complain_dt desc ";
            } else if (historyListValue.equals("NOC")) {
                query = "select a.appl_no,noc_no,to_char(noc_dt,'dd-Mon-yyyy') ,e.descr AS state_to, case when a.rto_to is not null then a.rto_to else f.off_name end as off_to ,dispatch_no,new_owner,COALESCE(o.off_name, '') || ',' || a.state_cd as office  "
                        + " from " + TableList.VT_NOC + " a "
                        + " LEFT JOIN " + TableList.TM_STATE + " e ON e.state_code = a.state_to::bpchar "
                        + " LEFT JOIN " + TableList.TM_OFFICE + " f ON f.off_cd = a.off_to AND f.state_cd = a.state_to::bpchar "
                        + " left outer join tm_office o on o.state_cd=a.state_cd and o.off_cd = a.off_cd "
                        + " where regn_no= '" + regNo + "' "
                        + " order by noc_dt desc";
            } else if (historyListValue.equals("NOCCANCEL")) {
                query = "select a.appl_no,noc_no,to_char(noc_dt,'dd-Mon-yyyy'),e.descr AS state_to, case when a.rto_to is not null then a.rto_to else f.off_name end as off_to ,"
                        + " reason,to_char(cancel_dt,'dd-Mon-yyyy') ,dispatch_no , approved_by , file_ref_no,COALESCE(o.off_name, '') || ',' || a.state_cd as office "
                        + " from " + TableList.VT_NOC_CANCEL + " a "
                        + " LEFT JOIN " + TableList.TM_STATE + " e ON e.state_code = a.state_to::bpchar "
                        + " LEFT JOIN " + TableList.TM_OFFICE + " f ON f.off_cd = a.off_to AND f.state_cd = a.state_to::bpchar "
                        + " left outer join tm_office o on o.state_cd=a.state_cd and o.off_cd = a.off_cd "
                        + " where regn_no= '" + regNo + "' "
                        + " order by cancel_dt desc";
            } else if (historyListValue.equals("HOP")) {
                query = "SELECT a.appl_no, a.pmt_no, to_char(issue_dt,'dd-Mon-yyyy'), to_char(a.valid_from,'dd-Mon-yyyy'), to_char(a.valid_upto,'dd-Mon-yyyy'), b.descr  ,c.descr,COALESCE(o.off_name, '') || ',' || a.state_cd as office "
                        + " FROM " + TableList.VH_PERMIT + " a "
                        + " left join permit.vm_permit_type b on b.code = a.pmt_type"
                        + " left join permit.vm_permit_catg  c on c.code = a.pmt_catg and c.state_cd = a.state_cd "
                        + " left outer join tm_office o on o.state_cd=a.state_cd and o.off_cd = a.off_cd "
                        + " where regn_no = '" + regNo + "' order by issue_dt desc";
            } else if (historyListValue.equals("HOPT")) {

                query = " (SELECT a.appl_no, a.pmt_no, a.order_no, to_char(a.order_dt,'dd-Mon-yyyy') as order_dt, a.order_by,COALESCE(a.new_regn_no,'NA') new_regn_no,  b.descr,c.descr ,COALESCE(o.off_name, '') || ',' || a.state_cd as office  "
                        + " FROM " + TableList.VH_PERMIT_TRANSACTION + " a "
                        + " left join tm_purpose_mast b on b.pur_cd = a.pur_cd "
                        + " left join tm_purpose_mast c on c.pur_cd = a.trans_pur_cd "
                        + " left outer join tm_office o on o.state_cd=a.state_cd and o.off_cd = a.off_cd "
                        + " where regn_no = '" + regNo + "' "
                        + " ) "
                        + " UNION ALL "
                        + " (SELECT a.appl_no, a.pmt_no, a.order_no, to_char(a.order_dt,'dd-Mon-yyyy') as order_dt, a.order_by,COALESCE(a.new_regn_no,'NA') new_regn_no, b.descr,c.descr ,COALESCE(o.off_name, '') || ',' || a.state_cd as office  "
                        + " FROM " + TableList.VHA_PERMIT_TRANSACTION + " a "
                        + " left join tm_purpose_mast b on b.pur_cd = a.pur_cd "
                        + " left join tm_purpose_mast c on c.pur_cd = a.trans_pur_cd "
                        + " left outer join tm_office o on o.state_cd=a.state_cd and o.off_cd = a.off_cd "
                        + " where regn_no = '" + regNo + "'  "
                        + " ) order by order_dt desc";
            } else if (historyListValue.equals("HSR")) {
                query = " select b.appl_no,a.old_regn_no,order_no,order_by,b.reason,to_char(b.moved_on,'dd-Mon-yyyy'),COALESCE(o.off_name, '') || ',' || a.state_cd as office  "
                        + " from " + TableList.VHA_RETENTION + "  b"
                        + " left outer join " + TableList.VH_RE_ASSIGN + "  a on old_regn_no=regn_no "
                        + " left outer join tm_office o on o.state_cd=a.state_cd and o.off_cd = a.off_cd "
                        + " where new_regn_no='" + regNo + "'"
                        + " and b.moved_on =(select max(moved_on)"
                        + " from " + TableList.VHA_RETENTION + ""
                        + " where regn_no='" + regNo + "'"
                        + ") order by b.moved_on desc";

            } else if (historyListValue.equals("EXEM")) {
                query = " SELECT to_char(exem_fr,'dd-Mon-yyyy'), to_char(exem_to,'dd-Mon-yyyy'), exem_by, perm_no, "
                        + " to_char(perm_dt,'dd-Mon-yyyy'),COALESCE(o.off_name, '') || ',' || a.state_cd as office "
                        + " FROM vt_tax_exem a"
                        + " left outer join tm_office o on o.state_cd=a.state_cd and o.off_cd = a.off_cd "
                        + " where regn_no = '" + regNo + "' order by op_dt desc";

            } else if (historyListValue.equals("HOCV")) {
                query = " SELECT b.user_name, changed_data , to_char(a.op_dt,'dd-Mon-yyyy HH24:MI:SS'),COALESCE(o.off_name, '') || ',' || a.state_cd as office  "
                        + "  FROM " + TableList.VHA_CHANGED_DATA + " a "
                        + "  left join " + TableList.TM_USER_INFO + "  b on b.user_cd = a.emp_cd "
                        + "  left outer join tm_office o on o.state_cd=a.state_cd and o.off_cd = a.off_cd "
                        + "  where appl_no = '" + regNo + "'"
                        + "  order by a.op_dt desc";
            } else if (historyListValue.equals("HOTC")) {
                query = " select a.descr, a.clear_to, a.tcr_no, to_char(a.op_dt,'dd-Mon-yyyy') as op_date, a.remark,office  "
                        + " from((select tcr_no ,to_char(clear_fr, 'dd-Mon-yyyy') as clear_fr , "
                        + " to_char(clear_to, 'dd-Mon-yyyy') as clear_to,a.pur_cd, b.descr , op_dt, remark,COALESCE(o.off_name, '') || ',' || a.state_cd as office "
                        + " from vt_tax_clear a"
                        + " left join TM_PURPOSE_MAST b on b.pur_cd=a.pur_cd "
                        + " left outer join tm_office o on o.state_cd=a.state_cd and o.off_cd = a.off_cd "
                        + " where regn_no= '" + regNo + "') "
                        + " UNION ALL  "
                        + " (select tcr_no ,to_char(clear_fr, 'dd-Mon-yyyy') as clear_fr , "
                        + " to_char(clear_to, 'dd-Mon-yyyy') as clear_to,a.pur_cd, b.descr , op_dt, remark,COALESCE(o.off_name, '') || ',' || a.state_cd as office "
                        + " from vh_tax_clear a left join TM_PURPOSE_MAST b on b.pur_cd=a.pur_cd "
                        + " left outer join tm_office o on o.state_cd=a.state_cd and o.off_cd = a.off_cd "
                        + " where regn_no= '" + regNo + "')) a "
                        + " order by op_dt DESC";
            } else if (historyListValue.equals("FIT")) {
                query = "SELECT a.appl_no as appl_no,  to_char(a.fit_chk_dt,'dd-Mon-yyyy') as fit_chk_dt, "
                        + " case when fit_result='Y' then 'Pass' when fit_result ='F' then 'Failed' else '' end as Result, to_char(a.fit_valid_to,'dd-Mon-yyyy') as fit_valid_to, "
                        + " to_char(a.fit_nid,'dd-Mon-yyyy') as fit_nid, to_char(a.op_dt,'dd-Mon-yyyy') as op_date , b.user_name as fit_off_cd1 ,c.user_name as fit_off_cd2,COALESCE(o.off_name, '') || ',' || a.state_cd as office "
                        + " FROM " + TableList.VH_FITNESS + " a "
                        + " left join " + TableList.TM_USER_INFO + "  b on b.user_cd = a.fit_off_cd1 and a.state_cd= b.state_cd "
                        + " left join " + TableList.TM_USER_INFO + "  c on c.user_cd = a.fit_off_cd2 and a.state_cd= c.state_cd "
                        + " left outer join tm_office o on o.state_cd=a.state_cd and o.off_cd = a.off_cd "
                        + " where regn_no = '" + regNo + "'"
                        + " order by a.op_dt desc";
            } else if (historyListValue.equals("DISPATCHRC")) {
                query = "select a.appl_no,a.regn_no,a.dispatch_ref_no,a.flat_file,to_char(a.moved_on,'dd-MMM-yyyy') as dispatch_date,ui.user_name,COALESCE(o.off_name, '') || ',' || a.state_cd as office"
                        + " from " + TableList.VHA_DISPATCH + " a"
                        + " left outer join " + TableList.TM_USER_INFO + " ui on ui.user_cd = regexp_replace(COALESCE(trim(a.moved_by), '0'), '[^0-9]', '0', 'g')::numeric"
                        + " left outer join tm_office o on o.state_cd=a.state_cd and o.off_cd = a.off_cd "
                        + " where regn_no= '" + regNo + "'"
                        + " order by a.moved_on DESC";
            } else if (historyListValue.equals("NONUSE")) {
                query = " SELECT regn_no, exem_fr, exem_to, nonuse_adjust_amt, penalty,"
                        + " to_char(op_dt,'dd-MON-yyyy') as op_dt,COALESCE(o.off_name, '') || ',' || a.state_cd as office "
                        + " FROM " + TableList.VT_NON_USE_TAX_EXEM + " a"
                        + " left outer join tm_office o on o.state_cd=a.state_cd and o.off_cd = a.off_cd "
                        + " where regn_no='" + regNo + "'"
                        + " UNION ALL \n"
                        + " SELECT regn_no, exem_fr, exem_to, nonuse_adjust_amt, penalty,"
                        + " to_char(op_dt,'dd-MON-yyyy') as op_dt,COALESCE(o.off_name, '') || ',' || a.state_cd as office "
                        + " FROM " + TableList.VH_NON_USE_TAX_EXEM + " a "
                        + " left outer join tm_office o on o.state_cd=a.state_cd and o.off_cd = a.off_cd "
                        + " where regn_no='" + regNo + "' order by  op_dt  desc\n";
            } else if (historyListValue.equals("REFUND")) {
                query = "SELECT  regn_no, taxfrom, taxupto, \n"
                        + " refund_amt, excess_amt, balance_amt ,to_char(op_dt,'dd-MON-yyyy') as op_dt,COALESCE(o.off_name, '') || ',' || a.state_cd as office "
                        + " FROM  " + TableList.VT_REFUND_EXCESS + " a"
                        + " left outer join tm_office o on o.state_cd=a.state_cd and o.off_cd = a.off_cd "
                        + " where regn_no='" + regNo + "'"
                        + " UNION ALL\n"
                        + " SELECT  regn_no, taxfrom, taxupto, \n"
                        + " refund_amt, exempt_amt, balance_amt, to_char(op_dt,'dd-MON-yyyy') as op_dt,COALESCE(o.off_name, '') || ',' || a.state_cd as office "
                        + " FROM " + TableList.VH_REFUND_EXCESS + " a "
                        + " left outer join tm_office o on o.state_cd=a.state_cd and o.off_cd = a.off_cd "
                        + " where regn_no='" + regNo + "' order by  op_dt  desc\n";
            } else if (historyListValue.equals("CHALLAN")) {
                query = "select a.regn_no,COALESCE(o.off_name, '') || ',' || a.state_cd as office, string_agg(distinct a.chal_no,',') chal_no,to_char(a.chal_date,'dd-Mon-yyyy') chal_date ,user_name as chal_Issued_by,string_agg(c.offence_desc,',') as offence "
                        + " from " + TableList.VH_CHALLAN + " a "
                        + " left join   " + TableList.VT_VCH_OFFENCES_HIST + " b on  a.state_cd=b.state_cd and a.off_cd=b.off_cd and a.appl_no=b.appl_no "
                        + " left join " + TableList.VM_OFFENCES + " c on b.state_cd=c.state_cd and b.offence_cd::text=c.mva_clause::text "
                        + " left join " + TableList.TM_USER_INFO + " tm on a.state_cd=tm.state_cd and a.off_cd=tm.off_cd and a.chal_officer::text=tm.user_cd::text "
                        + " left outer join " + TableList.TM_OFFICE + " o on o.state_cd=a.state_cd and o.off_cd = a.off_cd "
                        + " where a.regn_no='" + regNo + "' group by 1,2,4,5 ";

            } else if (historyListValue.equals("INSTALLMENT")) {
                query = "select tax_amt_instl,pay_due_date,rcpt_no from " + TableList.VT_TAX_INSTALLMENT + " i "
                        + " inner join " + TableList.VA_DETAILS + " d on d.state_cd=i.state_cd and d.off_cd=i.off_cd and d.appl_no=i.appl_regn_no"
                        + " left join " + TableList.VT_TAX_INSTALLMENT_BRKUP + " b on b.state_cd=i.state_cd and b.off_cd=i.off_cd and b.regn_no=d.regn_no"
                        + " where d.regn_no ='" + regNo + "' order by serial_no";
            } else if (historyListValue.equals("RCCANCEL")) {
                query = "select cancel_dt,file_ref_no,reason from " + TableList.VH_RC_CANCEL
                        + " where regn_no ='" + regNo + "' order by  cancel_dt  desc ";
            } else if (historyListValue.equals("RCSUSPEND")) {
                query = "select susp_dt,file_ref_no,reason from " + TableList.VH_RC_SUSPEND
                        + " where regn_no ='" + regNo + "' order by  susp_dt  desc ";
            } else if (historyListValue.equals("RCSURRENDER")) {
                query = "select surr_dt,file_ref_no,reason from " + TableList.VH_RC_SURRENDER
                        + " where regn_no ='" + regNo + "' order by  surr_dt  desc ";
            } else if (historyListValue.equals("38A")) {
                query = " select a.appl_no,c.descr,d.off_name,to_char(a.fit_chk_dt,'dd-Mon-yyyy')as fit_chk_date ,b.user_name"
                        + " from vt_fitness_temp a"
                        + " left join tm_user_info b on b.user_cd = a.fit_off_cd1"
                        + " left join tm_state c on c.state_code = a.state_cd"
                        + " left join tm_office d on a.state_cd = d.state_cd and a.off_cd=d.off_cd "
                        + " where regn_no='" + regNo + "' "
                        + " union all "
                        + " select a.appl_no,c.descr,d.off_name,to_char(a.fit_chk_dt,'dd-Mon-yyyy')as fit_chk_date ,b.user_name "
                        + " from vh_fitness_temp a "
                        + " left join tm_user_info b on b.user_cd = a.fit_off_cd1  "
                        + " left join tm_state c on c.state_code = a.state_cd "
                        + " left join tm_office d on a.state_cd = d.state_cd and a.off_cd=d.off_cd "
                        + " where regn_no='" + regNo + "'";
            }




            int rowsCount = 0;
            int nCols = 0;

            //................................................................
            // Get the whole table and store the data into this object.
            // NOTE:Though the master table can contain columns more than 2,
            // but we will store only the first two columns
            //................................................................
            tmg = new TransactionManagerReadOnly("VehicleHistoryDetailsImpl.makeMasterTableDO");
            PreparedStatement ps = tmg.prepareStatement(query);
            javax.sql.RowSet rsC = tmg.fetchDetachedRowSet();
            int c = 0;
            while (rsC.next()) {
                c++;
            }
            rowsCount = c;
            rsC.beforeFirst();
            // Get the number of columns // metadata[0][4] = "";
            java.sql.ResultSetMetaData rsmd = rsC.getMetaData();
            nCols = rsmd.getColumnCount();
            dataMap.put("colNumber", nCols);
            // Get the metadata
            metadata = new String[4][nCols];
            if (historyListValue.equals("FEES")) {
                metadata[0][0] = "Receipt No";
                metadata[0][1] = "Receipt Date";
                metadata[0][2] = "Fee Amount";
                metadata[0][3] = "Fine";
                metadata[0][4] = "Fee Particular";
                metadata[0][5] = "Office";
                metadata[0][6] = "GRN No";
            } else if (historyListValue.equals("ALT")) {
                metadata[0][0] = "Application No";
                metadata[0][1] = "Vehicle Class ";
                metadata[0][2] = "Chassis No";
                metadata[0][3] = "Engine No";
                metadata[0][4] = "BodyType";
                metadata[0][5] = "Seating Capacity";
                metadata[0][6] = "Standing Capacity";
                metadata[0][7] = "Unladden Weight";
                metadata[0][8] = "Laden weight";
                metadata[0][9] = "Fuel Type";
                metadata[0][10] = "Color";
                metadata[0][11] = "Horse Power";
                metadata[0][12] = "No of Cylinders";
                metadata[0][13] = "Office";
            } else if (historyListValue.equals("TAX")) {
                metadata[0][0] = "Receipt No";
                metadata[0][1] = "Tax From";
                metadata[0][2] = "Tax Upto";
                metadata[0][3] = "Tax Type";
                metadata[0][4] = "Challan Date";
                metadata[0][5] = "Total Amount";
                metadata[0][6] = "Office";
                metadata[0][7] = "Tax";
                metadata[0][8] = "Penalty";
                metadata[0][9] = "GRN No";
                metadata[0][10] = "Breakup";
                metadata[0][11] = "Tax Mode";
            } else if (historyListValue.equals("CA")) {
                metadata[0][0] = "Application No";
                metadata[0][1] = "From Date";
                metadata[0][2] = "To Date";
                metadata[0][3] = "Current Address";
                metadata[0][4] = "Permanent Address";
                metadata[0][5] = "Office";
            } else if (historyListValue.equals("HOV")) {
                metadata[0][0] = "Application No";
                metadata[0][1] = "Hypothecation Type";
                metadata[0][2] = "Financer Name";
                metadata[0][3] = "Finamcer Address";
                metadata[0][4] = "From Date";
                metadata[0][5] = "To Date";
                metadata[0][6] = "Termination Date";
                metadata[0][7] = "Office";
            } else if (historyListValue.equals("REN")) {
                metadata[0][0] = "Application No";
                metadata[0][1] = "Old Fitness Validity";
                metadata[0][2] = "New Fitness Validity";
                metadata[0][3] = "Inspected By";
                metadata[0][4] = "Inspection Date";
                metadata[0][5] = "Office";
            } else if (historyListValue.equals("TO")) {
                metadata[0][0] = "Application No.";
                metadata[0][1] = "Ownership Serial";
                metadata[0][2] = "Owner From";
                metadata[0][3] = "Owner Upto";
                metadata[0][4] = "Owner Name";
                metadata[0][5] = "Father / Husband Name";
                metadata[0][6] = "Present Address";
                metadata[0][7] = "Permanent Address";
                metadata[0][8] = "Owner Type";
                metadata[0][9] = "Sale / Auction Date";
                metadata[0][10] = "Sale Amount";
                metadata[0][11] = "Reason";
                metadata[0][12] = "Office";
            } else if (historyListValue.equals("INS")) {
                metadata[0][0] = "Insurance Company";
                metadata[0][1] = "Insurance Type";
                metadata[0][2] = "Insurance From";
                metadata[0][3] = "Insurance Upto";
                metadata[0][4] = "Cover Note No";
                metadata[0][5] = "Office";
            } else if (historyListValue.equals("REASGN")) {
                metadata[0][0] = "Application No";
                metadata[0][1] = "Old Registration No";
                metadata[0][2] = "New Registration No";
                metadata[0][3] = "Reason";
                metadata[0][4] = "Dated";
                metadata[0][5] = "Office";
            } else if (historyListValue.equals("DUP")) {
                metadata[0][0] = "Application No.";
                metadata[0][1] = "Reason";
                metadata[0][2] = "FIR / Dairy No";
                metadata[0][3] = "FIR / Dairy Dated";
                metadata[0][4] = "Police Station";
                metadata[0][5] = "Office";
            } else if (historyListValue.equals("BLACK")) {
                metadata[0][0] = "Complain/File Number";
                metadata[0][1] = "Complain Date";
                metadata[0][2] = "Complain Entered By";
                metadata[0][3] = "Action Taken";
                metadata[0][4] = "Action Entered By";
                metadata[0][5] = "Action Date";
                metadata[0][6] = "Office";
                metadata[0][7] = "Compounding Amount";
            } else if (historyListValue.equals("NOC")) {
                metadata[0][0] = "Application No";
                metadata[0][1] = "NOC No";
                metadata[0][2] = "NOC Date";
                metadata[0][3] = "State To";
                metadata[0][4] = "Office TO";
                metadata[0][5] = "Dispatch No";
                metadata[0][6] = "New Owner";
                metadata[0][7] = "Office";
            } else if (historyListValue.equals("NOCCANCEL")) {
                metadata[0][0] = "Application No";
                metadata[0][1] = "NOC No";
                metadata[0][2] = "NOC Date";
                metadata[0][3] = "State To";
                metadata[0][4] = "Office To";
                metadata[0][5] = "Reason";
                metadata[0][6] = "Cancel Date";
                metadata[0][7] = "Dispatch No";
                metadata[0][8] = "Approved By";
                metadata[0][9] = "File Reference No";
                metadata[0][10] = "Office";
            } else if (historyListValue.equals("HOP")) {
                metadata[0][0] = "Application No";
                metadata[0][1] = "Permit No";
                metadata[0][2] = "Issue Date";
                metadata[0][3] = "Valid From";
                metadata[0][4] = "Valid Upto";
                metadata[0][5] = "Permit Type";
                metadata[0][6] = "Permit Category";
                metadata[0][7] = "Office";
            } else if (historyListValue.equals("HOPT")) {
                metadata[0][0] = "Application No";
                metadata[0][1] = "Permit No";
                metadata[0][2] = "Order No";
                metadata[0][3] = "Order Date";
                metadata[0][4] = "Order By";
                metadata[0][5] = "New Registration No";
                metadata[0][6] = "Purpose ";
                metadata[0][7] = "Transaction Purpose ";
                metadata[0][8] = "Office";
            } else if (historyListValue.equals("HSR")) {
                metadata[0][0] = "Application No";
                metadata[0][1] = "Old Registration No";
                metadata[0][2] = "Order No";
                metadata[0][3] = "Order By";
                metadata[0][4] = "Reason";
                metadata[0][5] = "Moved On";
                metadata[0][6] = "Office";
            } else if (historyListValue.equals("EXEM")) {
                metadata[0][0] = "Exemption from";
                metadata[0][1] = "Exemption to";
                metadata[0][2] = "Authorized By";
                metadata[0][3] = "Permission No";
                metadata[0][4] = "Permission Date";
                metadata[0][5] = "Office";
            } else if (historyListValue.equals("HOCV")) {
                metadata[0][0] = "Changed By";
                metadata[0][1] = "Changed Data";
                metadata[0][2] = "Changed On";
                metadata[0][3] = "Office";
            } else if (historyListValue.equals("HOTC")) {
                metadata[0][0] = "Tax Description";
                metadata[0][1] = "Tax Clear To";
                metadata[0][2] = "TCR No.";
                metadata[0][3] = "Operation Date";
                metadata[0][4] = "Remarks";
                metadata[0][5] = "Office";
            } else if (historyListValue.equals("FIT")) {
                metadata[0][0] = "Appl. No";
                metadata[0][1] = "Fitness Check date";
                metadata[0][2] = "Result";
                metadata[0][3] = "Fitness UPTO";
                metadata[0][4] = "NID";
                metadata[0][5] = "Operation Date";
                metadata[0][6] = "Fitness Officer Name1";
                metadata[0][7] = "Fitness Officer Name2";
                metadata[0][8] = "Office";
            } else if (historyListValue.equals("DISPATCHRC")) {
                metadata[0][0] = "Application No";
                metadata[0][1] = "Registration No";
                metadata[0][2] = "Barcode No";
                metadata[0][3] = "Flat File Name";
                metadata[0][4] = "Dispatch Date";
                metadata[0][5] = "Dispatch By";
                metadata[0][6] = "Office";
            } else if (historyListValue.equals("NONUSE")) {
                metadata[0][0] = "Registration No";
                metadata[0][1] = " From";
                metadata[0][2] = " Upto";
                metadata[0][3] = "Adjustment Amount";
                metadata[0][4] = "NonUse Penalty";
                metadata[0][5] = "Operation Date";
                metadata[0][6] = "Office";
            } else if (historyListValue.equals("REFUND")) {
                metadata[0][0] = "Registration No";
                metadata[0][1] = "From";
                metadata[0][2] = "Upto";
                metadata[0][3] = "Refund Amount";
                metadata[0][4] = "Excess Amount";
                metadata[0][5] = "Balance Amount";
                metadata[0][6] = "Operation Date";
                metadata[0][7] = "Office";
            } else if (historyListValue.equals("CHALLAN")) {
                metadata[0][0] = "Registration No";
                metadata[0][1] = "Challan Issuing Office";
                metadata[0][2] = "Challan No";
                metadata[0][3] = "Challan Date";
                metadata[0][4] = "challan Issue By";
                metadata[0][5] = "Offence";
            } else if (historyListValue.equals("INSTALLMENT")) {
                metadata[0][0] = "Tax Amount Installment";
                metadata[0][1] = "Payment Due Date";
                metadata[0][2] = "Reciept no.";

            } else if (historyListValue.equals("RCCANCEL")) {
                metadata[0][0] = "RC Cancel date";
                metadata[0][1] = "File Ref no.";
                metadata[0][2] = "Reason";

            } else if (historyListValue.equals("RCSUSPEND")) {
                metadata[0][0] = "RC Suspend date";
                metadata[0][1] = "File Ref no.";
                metadata[0][2] = "Reason";

            } else if (historyListValue.equals("RCSURRENDER")) {
                metadata[0][0] = "RC Surrender date";
                metadata[0][1] = "File Ref no.";
                metadata[0][2] = "Reason";

            } else if (historyListValue.equals("38A")) {
                metadata[0][0] = "Application No";
                metadata[0][1] = "State";
                metadata[0][2] = "Office";
                metadata[0][3] = "Fit Check Date";
                metadata[0][4] = "Fit Checked By";
            }
            dataMap.put("metadata", metadata[0]);
            // Get the table data
            if (rowsCount > 0) {
                data = new String[rowsCount][nCols];
                int row = 0;
                while (rsC.next()) {
                    row++;
                    // Note: Column number starts with 1
                    for (int col = 1; col <= nCols; col++) {
                        data[row - 1][col - 1] = rsC.getString(col);
                        if (historyListValue.equals("INSTALLMENT") && col == 1) {
                            if (rsC.getString(col).equals("")) {
                                message = "Tax Installment Done but Tax Breakup not make.";
                            } else {
                                data[row - 1][col - 1] = rsC.getString(col);
                            }
                        }

                    }

                    // dataMap.put(rsC.getString(codeName), rsC.getString(descName));
                }
            }
            dataMap.put("message", message);
            dataMap.put("data", data);
            if (nCols < 2) {
                throw new Exception(" table \"" + "" + "\" contains less than 2 columns!");
            }
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return dataMap;
    }

    public static String statusOfNOC(String regNo) {

        String status = "";
        TransactionManager tmgr = null;
        String whereiam = "VehicleHistoryDetailsImpl.statusOfNOC()";
        PreparedStatement psmt = null;
        String sql = "SELECT status FROM " + TableList.VT_OWNER + " WHERE regn_no = ? and state_cd = ? and off_cd = ? ";
        try {
            tmgr = new TransactionManager(whereiam);
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                status = rs.getString("status");
            }
            rs.close();
            rs = null;
            // psmt.close();
            //  psmt = null;
        } catch (SQLException sqle) {
            LOGGER.error(whereiam + "---SQLException Occured during fetch user details from db-", sqle);
        } finally {
            try {
                tmgr.release();
            } catch (VahanException ex) {
                LOGGER.error(whereiam + "Exception occured in connection release", ex);
            } catch (Exception ex) {
                LOGGER.error(whereiam + "Exception occured in connection release", ex);
            }
        }
        return status;

    }
}
