/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.dealer;

import nic.vahan.form.impl.*;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.dealer.DealertReportsDobj;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

public class DealerReportsImpl {

    private static final Logger LOGGER = Logger.getLogger(DealerReportsImpl.class);

    public List<DealertReportsDobj> getPrintList(Date fromDate, Date uptodate) {

        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        List<DealertReportsDobj> dobjList = new ArrayList<DealertReportsDobj>();

        try {
            tmgr = new TransactionManagerReadOnly("getPrintList");
            sql = "SELECT a.appl_no, a.rcpt_no, m.owner_name, m.chasi_no, vp.return_rcpt_no, vp.transaction_id, d.regn_no, to_char(a.rcpt_dt, 'dd-MM-YYYY') as rcpt_dt, a.rcptAmt \n"
                    + " FROM \n"
                    + "  (SELECT appl_no, rcpt_no, transaction_no, rcpt_dt, sum(amount + surcharge + penalty + tax1 + tax2 + interest - exempted - rebate) AS rcptAmt \n"
                    + "    FROM vph_rcpt_cart \n"
                    + "    WHERE user_cd = ? and off_cd = ? AND moved_on BETWEEN ? AND ?::date + interval '1' DAY GROUP BY 1, 2, 3, 4) a \n"
                    + " INNER JOIN vp_appl_rcpt_mapping m ON m.appl_no = a.appl_no \n"
                    + " INNER JOIN va_details d ON d.appl_no = a.appl_no and d.pur_cd in (123, 124) \n"
                    + " INNER JOIN vahanpgi.vp_pgi_details vp ON vp.payment_id = a.transaction_no \n"
                    + " order by a.rcpt_dt ";

            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(Util.getEmpCode()));
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            java.sql.Date fromSqlDate = new java.sql.Date(fromDate.getTime());
            java.sql.Date uptoSqlDate = new java.sql.Date(uptodate.getTime());
            ps.setDate(3, fromSqlDate);
            ps.setDate(4, uptoSqlDate);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                DealertReportsDobj dobj = new DealertReportsDobj();
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setoName(rs.getString("owner_name"));
                dobj.setChasiNo(rs.getString("chasi_no"));
                dobj.setRcptNo(rs.getString("rcpt_no"));
                dobj.setTransactionNo(rs.getString("transaction_id"));
                dobj.setBankRefNo(rs.getString("return_rcpt_no"));
                dobj.setRcptDt(rs.getString("rcpt_dt"));
                dobj.setRegnNo(rs.getString("regn_no"));
                dobj.setAmount(rs.getString("rcptAmt"));
                dobjList.add(dobj);
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
        return dobjList;
    }

    public DealertReportsDobj getForm20Data(String appl_no) {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        DealertReportsDobj dobj = null;

        try {
            tmgr = new TransactionManager("getForm20Data of DealerReportsImpl");
            sql = " select a.state_cd, a.state_name, a.off_cd, a.off_name, a.appl_no, a.regn_no, a.owner_name as o_name,a.chasi_no, a.model_name, d.mobile_no,\n"
                    + " a.f_name, a.c_add1 as temp_address1, replace(a.c_add2 || ', '|| a.c_add3 ||', '|| a.c_district_name, ', ,', ',') as temp_address2,a.c_state_name as temp_city, a.c_pincode as temp_pin, a.p_add1 as address1 , replace(a.p_add2 || ', '|| a.p_add3 ||', '|| a.p_district_name, ', ,', ',') as addrress2, a.p_state_name as city, a.p_pincode as pin, \n"
                    + " a.dealer_cd, a.dlr_name as dealer_name, a.dlr_add1 as dealer_address1, a.dlr_add2 as dealer_address2, a.dlr_city as dealer_city, a.dlr_pincode as dealer_pin, a.vh_class,a.vh_class_desc as veh_class ,a.body_type, a.maker_name as makers_name,a.vch_catg as veh_catg,a.no_cyl as  no_of_cyl, \n"
                    + " to_char(to_timestamp (a.manu_mon::text, 'MM'), 'Mon') ||'-'|| a.manu_yr as mfg_month_year, \n"
                    + " a.hp as horse_power,a.cubic_cap as cub_cap,a.wheelbase as wheel_base,a.eng_no as engine_no,a.seat_cap,a.fuel,a.fuel_descr,a.unld_wt as un_ld_wt,a.color,a.ld_wt, \n"
                    + " c.ins_company_name||'.'|| ' Covernote No.-'||c.policy_no|| ', Valid From '||to_char(c.ins_from,'dd-Mon-yyyy') ||' To '||to_char(c.ins_upto,'dd-Mon-yyyy') as ins_dtls, \n"
                    + "'(1) Hypothecation Type :'||b.hp_type||', Financier''s Name :'||b.fncr_name as hypo_dtls,a.chasi_no as barcode_chasis_no, a.off_name as rto_name, f.off_name as off_to_name , \n"
                    + " CASE WHEN a.ac_fitted = 'Y' THEN 'YES' ELSE 'NO' END AS ac_fitted , e.f_axle_descp, e.r_axle_descp, e.o_axle_descp, e.t_axle_descp,e.f_axle_weight, e.r_axle_weight, e.o_axle_weight, e.t_axle_weight, h.model_manu_loc \n"
                    + " from vva_owner a \n"
                    + " left outer join vva_hpa b on b.appl_no = a.appl_no \n"
                    + " left outer join vva_insurance c on c.appl_no = a.appl_no \n"
                    + " left outer join va_owner_identification d on d.appl_no = a.appl_no \n"
                    + " left outer join va_axle e on e.appl_no = a.appl_no \n "
                    + " left outer join va_owner_temp t on t.appl_no = a.appl_no "
                    + " left outer join va_homologation_details h on a.appl_no = h.appl_no and a.state_cd = h.state_cd and a.off_cd = h.off_cd "
                    + " LEFT JOIN tm_office f ON t.off_cd_to = f.off_cd AND t.state_cd_to = f.state_cd::bpchar  "
                    + " where a.appl_no = ? \n ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new DealertReportsDobj();
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setRegnNo(rs.getString("regn_no"));
                dobj.setoName(rs.getString("o_name"));
                dobj.setChasiNo(rs.getString("chasi_no"));
                dobj.setModelName(rs.getString("model_name"));
                dobj.setMobileNo(rs.getString("mobile_no"));
                dobj.setfName(rs.getString("f_name"));
                dobj.setTempAddress1(rs.getString("temp_address1"));
                dobj.setTempAddress2(rs.getString("temp_address2"));
                dobj.setTempCity(rs.getString("temp_city"));
                dobj.setTempPin(rs.getString("temp_pin"));
                dobj.setAddress1(rs.getString("address1"));
                dobj.setAddress2(rs.getString("addrress2"));
                dobj.setCity(rs.getString("city"));
                dobj.setPin(rs.getString("pin"));
                dobj.setDealerName(rs.getString("dealer_name"));
                dobj.setDealerAddress1(rs.getString("dealer_address1"));
                dobj.setDealerAddress2(rs.getString("dealer_address2"));
                dobj.setDealerCity(rs.getString("dealer_city"));
                dobj.setDealerPin(rs.getString("dealer_pin"));
                dobj.setVehClass(rs.getString("veh_class"));
                dobj.setVehCatg(rs.getString("veh_catg"));
                dobj.setBodyType(rs.getString("body_type"));
                dobj.setMakersName(rs.getString("makers_name"));
                dobj.setNoOfCyl(rs.getString("no_of_cyl"));
                dobj.setMfgMonthYear(rs.getString("mfg_month_year"));
                dobj.setHorsePower(rs.getString("horse_power"));
                dobj.setColor(rs.getString("color"));
                dobj.setCubCap(rs.getString("cub_cap"));
                dobj.setWheelBase(rs.getString("wheel_base"));
                dobj.setEngineNo(rs.getString("engine_no"));
                dobj.setSeatCap(rs.getString("seat_cap"));
                dobj.setFuel(rs.getString("fuel_descr"));
                dobj.setUnLdWt(rs.getString("un_ld_wt"));
                dobj.setLdWt(rs.getString("ld_wt"));
                dobj.setInsDtls(rs.getString("ins_dtls"));
                dobj.setHypoDtls(rs.getString("hypo_dtls"));
                dobj.setOffName(rs.getString("rto_name"));
                dobj.setBarcodeChasisNo(rs.getString("barcode_chasis_no"));
                dobj.setAcFitted(rs.getString("ac_fitted"));
                dobj.setfAxleDesc(rs.getString("f_axle_descp"));
                dobj.setfAxleWeight(rs.getString("f_axle_weight"));
                dobj.setrAxleDesc(rs.getString("r_axle_descp"));
                dobj.setrAxleWeight(rs.getString("r_axle_weight"));
                dobj.setoAxleDesc(rs.getString("o_axle_descp"));
                dobj.setoAxleWeight(rs.getString("o_axle_weight"));
                dobj.settAxleDesc(rs.getString("t_axle_descp"));
                dobj.settAxleWeight(rs.getString("t_axle_weight"));
                dobj.setStateName(rs.getString("state_name"));
                dobj.setOfficeTmpName(rs.getString("off_to_name"));
                dobj = getModelManuLocation(rs.getString("model_manu_loc"), dobj);
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
        return dobj;
    }

    public DealertReportsDobj getForm21Data(String appl_no) {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        DealertReportsDobj dobj = null;

        try {
            tmgr = new TransactionManager("getForm21Data  of DealerReportsImpl");
            sql = "  select a.state_cd, a.state_name, a.off_cd, a.off_name, a.appl_no, a.regn_no, a.owner_name as o_name,a.chasi_no,a.model_name,to_char(a.purchase_dt,'dd-Mon-YYYY') as purchase_date,\n"
                    + "  a.f_name, (a.c_add1 || ', ' || a.c_add2 || ', ' || a.c_add3 || ', ' || a.c_district_name || ', ' || a.c_state_name || '-' || a.c_pincode::varchar) as address, (a.p_add1 || ', ' || a.p_add2 || ', ' || a.p_add3 || ', ' || a.p_district_name || ', ' || a.p_state_name || '-' || a.p_pincode::varchar) as param_address ,  \n"
                    + "  a.dealer_cd, a.dlr_name as dealer_name,a.vh_class as vh_class,a.vh_class_desc as vh_class_desc,a.body_type, a.maker_name as maker_name,a.no_cyl as  no_cyl, \n"
                    + "  to_char(to_timestamp (a.manu_mon::text, 'MM'), 'Mon') ||'-'|| a.manu_yr as mfg_year, \n"
                    + "  a.hp as horse_power,a.eng_no as engine_no,a.seat_cap,a.fuel,a.fuel_descr,a.unld_wt as un_ld_wt,a.color,a.ld_wt,a.cubic_cap, \n"
                    + "  b.hp_type, (b.hp_type_descr || ', ' || b.fncr_name || ', ' || b.fncr_add1 || ', ' || b.fncr_add2 || ', ' || b.fncr_add3 || ', ' ||  b.fncr_district_name || ', ' || fncr_state_name || '-' || fncr_pincode::varchar) as hypo_dtls, \n"
                    + "  CASE WHEN a.ac_fitted = 'Y' THEN 'YES' ELSE 'NO' END AS ac_fitted , e.f_axle_descp, e.r_axle_descp, e.o_axle_descp, e.t_axle_descp,e.f_axle_weight, e.r_axle_weight, e.o_axle_weight, e.t_axle_weight,r.kit_srno,r.kit_type,r.kit_manuf,r.kit_pucc_norms,r.workshop,r.workshop_lic_no,to_char(r.fitment_dt,'dd-Mon-YYYY') as fitment_dt,to_char(r.hydro_test_dt,'dd-Mon-YYYY') as hydro_test_dt,r.cyl_srno,r.approval_no,to_char(r.approval_dt,'dd-Mon-YYYY') as approval_dt, h.model_manu_loc \n"
                    + "  from vva_owner a \n"
                    + "  left outer join vva_hpa b on b.appl_no = a.appl_no\n"
                    + "  left outer join va_owner_identification d on d.appl_no = a.appl_no \n"
                    + "  left outer join va_axle e on e.appl_no = a.appl_no \n"
                    + "  left outer join va_retrofitting_dtls r on r.appl_no = a.appl_no \n"
                    + "  left outer join va_homologation_details h on a.appl_no = h.appl_no and a.state_cd = h.state_cd and a.off_cd = h.off_cd "
                    + "  where a.appl_no = ? ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new DealertReportsDobj();
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setRegnNo(rs.getString("regn_no"));
                dobj.setoName(rs.getString("o_name"));
                dobj.setChasiNo(rs.getString("chasi_no"));
                dobj.setModelName(rs.getString("model_name"));
                dobj.setPurchaseDate(rs.getString("purchase_date"));
                dobj.setfName(rs.getString("f_name"));
                dobj.setTempAddress1(rs.getString("address"));
                dobj.setAddress1(rs.getString("param_address"));
                dobj.setDealerName(rs.getString("dealer_name"));
                dobj.setVehClass(rs.getString("vh_class_desc"));
                dobj.setBodyType(rs.getString("body_type"));
                dobj.setMakersName(rs.getString("maker_name"));
                dobj.setNoOfCyl(rs.getString("no_cyl"));
                dobj.setMfgMonthYear(rs.getString("mfg_year"));
                dobj.setHorsePower(rs.getString("horse_power"));
                dobj.setColor(rs.getString("color"));
                dobj.setEngineNo(rs.getString("engine_no"));
                dobj.setSeatCap(rs.getString("seat_cap"));
                dobj.setFuel(rs.getString("fuel_descr"));
                dobj.setUnLdWt(rs.getString("un_ld_wt"));
                dobj.setLdWt(rs.getString("ld_wt"));
                dobj.setHypoDtls(rs.getString("hypo_dtls"));
                dobj.setAcFitted(rs.getString("ac_fitted"));
                dobj.setfAxleDesc(rs.getString("f_axle_descp"));
                dobj.setfAxleWeight(rs.getString("f_axle_weight"));
                dobj.setrAxleDesc(rs.getString("r_axle_descp"));
                dobj.setrAxleWeight(rs.getString("r_axle_weight"));
                dobj.setoAxleDesc(rs.getString("o_axle_descp"));
                dobj.setoAxleWeight(rs.getString("o_axle_weight"));
                dobj.settAxleDesc(rs.getString("t_axle_descp"));
                dobj.settAxleWeight(rs.getString("t_axle_weight"));
                dobj.setCubCap(rs.getString("cubic_cap"));
                if (ServerUtil.isTransport(rs.getInt("vh_class"), null)) {
                    dobj.setVehType("Transport");
                } else {
                    dobj.setVehType("Non-Transport");
                }
                dobj = getModelManuLocation(rs.getString("model_manu_loc"), dobj);
                dobj.setKitSrNo(rs.getString("kit_srno"));
                dobj.setKitManuf(rs.getString("kit_manuf"));
                dobj.setKitType(rs.getString("kit_type"));
                dobj.setWorkShop(rs.getString("workshop"));
                dobj.setFitmentDate(rs.getString("fitment_dt"));
                dobj.setApprovalDate(rs.getString("approval_dt"));
                dobj.setApprovalNo(rs.getString("approval_no"));
                dobj.setCylSrNo(rs.getString("cyl_srno"));
                dobj.setHydroTestDate(rs.getString("hydro_test_dt"));
                if (rs.getInt("fuel") == TableConstants.VM_FUEL_TYPE_PETROL_CNG || rs.getInt("fuel") == TableConstants.VM_FUEL_CNG_TYPE) {
                    dobj.setFuelType("CngFuel");
                }
            }
        } catch (VahanException ve) {
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
        return dobj;
    }

    public List<DealertReportsDobj> getPrintListForDisclaimer(Date fromDate, Date uptodate, String reportType, long userCd, String stateCd, int offCd, String applRegnNo) {

        StringBuilder sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        StringBuilder selectVar = null;
        StringBuilder whereConditionVar = null;
        List<DealertReportsDobj> dobjList = new ArrayList<DealertReportsDobj>();

        try {
            tmgr = new TransactionManagerReadOnly("getPrintListForDisclaimer of DealerReportsImpl");

            if (!reportType.equals("form2021AfterApproval")) {
                selectVar = new StringBuilder("select distinct o.appl_no, o.owner_name, o.chasi_no, o.regn_no,d.pur_cd  \n"
                        + " from va_owner o, va_details d, tm_user_permissions p ");

                if (reportType.equals("RTOform20")) {
                    whereConditionVar = new StringBuilder(" where o.state_cd = ? AND o.off_cd = ? and o.regn_type in ('N', 'T') and  \n"
                            + " o.appl_no = d.appl_no and d.state_cd=o.state_cd and d.appl_dt between ? and ? :: date + interval '1' day and d.pur_cd in  (" + TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE + ", " + TableConstants.VM_TRANSACTION_MAST_TEMP_REG + ") ");
                } else {
                    whereConditionVar = new StringBuilder(" where p.user_cd = ? and o.state_cd = ? AND o.off_cd = ? and  \n"
                            + " o.appl_no = d.appl_no and o.dealer_cd = p.dealer_cd and d.state_cd=o.state_cd and d.appl_dt between ? and ? :: date + interval '1' day ");
                }

                if (reportType.equals("inspCertificate")) {
                    sql = selectVar.append(", vm_vh_class vh").append(whereConditionVar).append("and o.vh_class=vh.vh_class and vh.class_type = 2");
                } else {
                    sql = selectVar.append(whereConditionVar);
                }

                if (reportType.equals("disclaimer")) {
                    sql = sql.append(" and d.pur_cd != ? ");
                }
                if (reportType.equals("form2021")) {
                    sql = sql.append(" and d.pur_cd in  (" + TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE + ", " + TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE + ") ");
                }
            } else {
                sql = new StringBuilder("select o.regn_no as regn_no,o.owner_name, o.chasi_no, d.pur_cd as pur_cd from " + TableList.VT_OWNER + " o  "
                        + "  inner join " + TableList.TM_USER_PERMISSIONS + " p on o.dealer_cd = p.dealer_cd and p.user_cd = ? and p.state_cd = o.state_cd and "
                        + "  (o.off_cd = p.assigned_office::int or all_office_auth = true)"
                        + "  inner join " + TableList.VA_DETAILS + " d  on d.state_cd=o.state_cd and d.off_cd=o.off_cd and d.regn_no=o.regn_no"
                        + "  where (d.appl_no = ? or d.regn_no = ? )and o.state_cd = ? and o.off_cd = ? and o.status <> '" + TableConstants.VT_NOC_ISSUE_STATUS + "' and d.pur_cd = " + TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE + " "
                        + "  union "
                        + "  select o.temp_regn_no as regn_no,o.owner_name, o.chasi_no, " + TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE + " as pur_cd from " + TableList.VT_OWNER_TEMP + " o "
                        + "  inner join " + TableList.TM_USER_PERMISSIONS + " p on o.dealer_cd = p.dealer_cd and p.user_cd = ? and p.state_cd = o.state_cd and "
                        + "  (o.off_cd = p.assigned_office::int or all_office_auth = true)"
                        + "  inner join " + TableList.VA_DETAILS + " d  on d.state_cd=o.state_cd and d.off_cd=o.off_cd and d.appl_no=o.appl_no"
                        + "  where (d.appl_no = ? or d.regn_no = ? )and o.state_cd = ? and o.off_cd = ? and d.pur_cd= " + TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE + " ");
            }
            ps = tmgr.prepareStatement(sql.toString());
            int i = 1;
            java.sql.Date fromSqlDate = new java.sql.Date(fromDate.getTime());
            java.sql.Date uptoSqlDate = new java.sql.Date(uptodate.getTime());
            if (!reportType.equals("form2021AfterApproval")) {
                if (!reportType.equals("RTOform20")) {
                    ps.setLong(i++, userCd);
                }
                ps.setString(i++, stateCd);
                ps.setInt(i++, offCd);
                ps.setDate(i++, fromSqlDate);
                ps.setDate(i++, uptoSqlDate);
                if (reportType.equals("disclaimer")) {
                    ps.setInt(i++, TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE);
                }
            } else {
                ps.setLong(i++, userCd);
                ps.setString(i++, applRegnNo);
                ps.setString(i++, applRegnNo);
                ps.setString(i++, stateCd);
                ps.setInt(i++, offCd);
                ps.setLong(i++, userCd);
                ps.setString(i++, applRegnNo);
                ps.setString(i++, applRegnNo);
                ps.setString(i++, stateCd);
                ps.setInt(i++, offCd);
            }

            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                DealertReportsDobj dobj = new DealertReportsDobj();
                if (!reportType.equals("form2021AfterApproval")) {
                    dobj.setApplNo(rs.getString("appl_no"));
                }
                dobj.setoName(rs.getString("owner_name"));
                dobj.setChasiNo(rs.getString("chasi_no"));
                dobj.setRegnNo(rs.getString("regn_no"));
                dobj.setPurCd(rs.getInt("pur_cd"));
                dobjList.add(dobj);
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
        return dobjList;
    }

    public DealertReportsDobj getRcptNo(String applNo) {

        String sql = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        DealertReportsDobj dobj = new DealertReportsDobj();

        try {
            tmgr = new TransactionManager("getRcptNo of DealerReportsImpl");
            sql = "select distinct rcpt_no from vph_rcpt_cart where appl_no = ? and moved_by = ? ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getEmpCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj.setRcptNo(rs.getString("rcpt_no"));
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
        return dobj;
    }

    public DealertReportsDobj getInspectionCertificateData(String appl_no) {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        DealertReportsDobj dobj = null;

        try {
            tmgr = new TransactionManager("getInspectionCertificateData of DealerReportsImpl");

            sql = "  select a.state_cd,a.off_cd,a.off_name,a.state_name,a.regn_no,a.owner_name as o_name,(a.c_add1 || ', ' || a.c_add2 || ', ' || a.c_add3 || ', ' || a.c_district_name || ', ' || a.c_state_name || '-' || a.c_pincode::varchar) as owner_caddress, \n"
                    + " a.dealer_cd, a.dlr_name as dealer_name,(a.dlr_add1 || ', ' || a.dlr_add2 || ', ' || a.dlr_add3 || ', ' || a.dlr_city || ', ' || a.dlr_district || '-' || a.dlr_pincode::varchar) as dlr_address, maker_name, model_name, \n"
                    + " a.vch_catg as veh_catg,a.chasi_no,a.eng_no as engine_no,to_char(a.purchase_dt,'dd-Mon-yyyy') as purchase_dt,a.fuel_descr,a.no_cyl,upper(a.vh_class_desc)  as vh_class_desc,upper(a.norms_descr) as norms_descr, coalesce(tc.cert_no, d.dealer_regn_no) as cert_no\n"
                    + " from vva_owner a \n"
                    + " left join vt_trade_certificate tc on a.dealer_cd = tc.dealer_cd \n"
                    + " left join vm_dealer_mast d on d.dealer_cd = a.dealer_cd "
                    + " where a.appl_no = ? ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new DealertReportsDobj();
                dobj.setDealerTradeCertCode(rs.getString("cert_no"));
                dobj.setDealerName(rs.getString("dealer_name"));
                dobj.setDealerAddress1(rs.getString("dlr_address"));
                dobj.setoName(rs.getString("o_name"));
                dobj.setTempAddress1(rs.getString("owner_caddress"));
                dobj.setPurchaseDate(rs.getString("purchase_dt"));
                dobj.setVehCatg(rs.getString("veh_catg"));
                dobj.setStateName(rs.getString("state_name"));
                dobj.setRegnNo(rs.getString("regn_no"));
                dobj.setModelName(rs.getString("model_name"));
                dobj.setMakersName(rs.getString("maker_name"));
                dobj.setChasiNo(rs.getString("chasi_no"));
                dobj.setEngineNo(rs.getString("engine_no"));
                dobj.setFuel(rs.getString("fuel_descr"));
                dobj.setNoOfCyl(rs.getString("no_cyl"));
                dobj.setVehClass(rs.getString("vh_class_desc"));
                dobj.setNorms(rs.getString("norms_descr"));
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
        return dobj;
    }

    public DealertReportsDobj getForm2021DataAfterApproval(String regnNo, String printType, int purCd, String stateCd, int offCd) {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        DealertReportsDobj dobj = null;
        String regnType = "";
        String tableName = "";
        String officeTable = "";
        String caseForTemp = "";
        String homoTable = " vha_homologation_details ";
        if (purCd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
            regnType = "regn_no";
            tableName = "vv_owner";
        } else {
            regnType = "temp_regn_no";
            tableName = "vv_owner_temp";
            caseForTemp = ",o.off_name as off_to_name";
            officeTable = " left outer join tm_office o on o.off_cd = a.off_cd_to and o.state_cd = a.state_cd_to ";
            homoTable = " va_homologation_details ";
        }
        try {
            tmgr = new TransactionManagerReadOnly("getForm2021DataAfterApproval of DealerReportsImpl");
            sql = " select a.state_cd, a.state_name, a.off_cd, a.off_name, a." + regnType + " as regn_no, a.owner_name as o_name,a.chasi_no,a.model_name,to_char(a.purchase_dt,'dd-Mon-YYYY') as purchase_date, d.mobile_no,"
                    + " a.f_name, regexp_replace(a.c_add1|| ', ' || a.c_add2|| ', ' || a.c_add3 || ', ' || a.c_district_name || ', ' || a.c_state_name || '-' || a.c_pincode::varchar, '(, ){2,}', ', ') as temp_address,"
                    + " regexp_replace(a.p_add1|| ', ' || a.p_add2|| ', ' || a.p_add3 || ', ' || a.p_district_name || ', ' || a.p_state_name || '-' || a.p_pincode::varchar, '(, ){2,}', ', ') as param_address ,a.chasi_no as barcode_chasis_no,"
                    + " regexp_replace(a.dlr_name|| ', ' || a.dlr_add1|| ', ' || a.dlr_add2 || ', ' || a.dlr_add3 || ', ' || a.dlr_district || ', ' || a.dlr_city, '(, ){2,}', ', ') as dlr_address, a.off_name as rto_name, "
                    + " a.dealer_cd, a.dlr_name as dealer_name,a.vh_class as vh_class,a.vh_class_desc as vh_class_desc,a.body_type, a.maker_name as maker_name,a.no_cyl as  no_cyl,a.vch_catg as veh_catg,"
                    + " c.ins_company_name||'.'|| ' Covernote No.-'||c.policy_no|| ', Valid From '||to_char(c.ins_from,'dd-Mon-yyyy') ||' To '||to_char(c.ins_upto,'dd-Mon-yyyy') as ins_dtls,"
                    + " to_char(to_timestamp (a.manu_mon::text, 'MM'), 'Mon') ||'-'|| a.manu_yr as mfg_year, a.hp as horse_power,a.eng_no as engine_no,a.seat_cap,a.fuel,a.fuel_descr,a.unld_wt as un_ld_wt,a.color,a.ld_wt,a.cubic_cap as cub_cap,a.wheelbase as wheel_base,"
                    + " b.hp_type, CASE WHEN '" + printType + "' = 'FORM21AfterApproval' THEN "
                    + " (b.hp_type_descr || ', ' || b.fncr_name || ', ' || b.fncr_add1 || ', ' || b.fncr_add2 || ', ' || b.fncr_add3 || ', ' ||  b.fncr_district_name || ', ' || fncr_state_name || '-' || fncr_pincode::varchar) "
                    + " ELSE '(1) Hypothecation Type :'||b.hp_type||', Financier''s Name :'||b.fncr_name END as hypo_dtls,"
                    + " CASE WHEN a.ac_fitted = 'Y' THEN 'YES' ELSE 'NO' END AS ac_fitted , e.f_axle_descp, e.r_axle_descp, e.o_axle_descp, e.t_axle_descp,e.f_axle_weight, e.r_axle_weight, e.o_axle_weight ,h.model_manu_loc, "
                    + " e.t_axle_weight,r.kit_srno,r.kit_type,r.kit_manuf,r.kit_pucc_norms,r.workshop,r.workshop_lic_no,to_char(r.fitment_dt,'dd-Mon-YYYY') as fitment_dt,to_char(r.hydro_test_dt,'dd-Mon-YYYY') as hydro_test_dt,r.cyl_srno,r.approval_no,to_char(r.approval_dt,'dd-Mon-YYYY') as approval_dt " + caseForTemp + " "
                    + " from " + tableName + " a  "
                    + " left outer join vv_insurance c on c.regn_no = a." + regnType + " and  c.state_cd = a.state_cd and  c.off_cd = a.off_cd "
                    + " left outer join vv_hypth b on b.regn_no = a." + regnType + " and  b.state_cd = a.state_cd and  b.off_cd = a.off_cd"
                    + " left outer join vt_owner_identification d on d.regn_no = a." + regnType + "  and  d.state_cd = a.state_cd and  d.off_cd = a.off_cd"
                    + " left outer join vt_axle e on e.regn_no = a." + regnType + "  and  e.state_cd = a.state_cd and  e.off_cd = a.off_cd"
                    + " left outer join vt_retrofitting_dtls r on r.regn_no = a." + regnType + "  and  r.state_cd = a.state_cd and  r.off_cd = a.off_cd "
                    + " left outer join " + homoTable + " h on a.chasi_no = h.chasi_no and a.state_cd = h.state_cd and a.off_cd = h.off_cd "
                    + " " + officeTable + " "
                    + " where a." + regnType + " =  ? and a.off_cd = ? and a.state_cd = ? ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setInt(2, offCd);
            ps.setString(3, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new DealertReportsDobj();
                dobj.setRegnNo(rs.getString("regn_no"));
                dobj.setoName(rs.getString("o_name"));
                dobj.setChasiNo(rs.getString("chasi_no"));
                dobj.setModelName(rs.getString("model_name"));
                dobj.setMobileNo(rs.getString("mobile_no"));
                dobj.setfName(rs.getString("f_name"));
                dobj.setTempAddress1(rs.getString("temp_address"));
                dobj.setAddress1(rs.getString("param_address"));
                dobj.setDealerName(rs.getString("dealer_name"));
                dobj.setDealerAddress1(rs.getString("dlr_address"));
                dobj.setVehClass(rs.getString("vh_class_desc"));
                dobj.setVehCatg(rs.getString("veh_catg"));
                dobj.setBodyType(rs.getString("body_type"));
                dobj.setMakersName(rs.getString("maker_name"));
                dobj.setNoOfCyl(rs.getString("no_cyl"));
                dobj.setMfgMonthYear(rs.getString("mfg_year"));
                dobj.setHorsePower(rs.getString("horse_power"));
                dobj.setColor(rs.getString("color"));
                dobj.setCubCap(rs.getString("cub_cap"));
                dobj.setWheelBase(rs.getString("wheel_base"));
                dobj.setEngineNo(rs.getString("engine_no"));
                dobj.setSeatCap(rs.getString("seat_cap"));
                dobj.setFuel(rs.getString("fuel_descr"));
                dobj.setUnLdWt(rs.getString("un_ld_wt"));
                dobj.setLdWt(rs.getString("ld_wt"));
                dobj.setInsDtls(rs.getString("ins_dtls"));
                dobj.setHypoDtls(rs.getString("hypo_dtls"));
                dobj.setOffName(rs.getString("rto_name"));
                dobj.setBarcodeChasisNo(rs.getString("barcode_chasis_no"));
                dobj.setAcFitted(rs.getString("ac_fitted"));
                dobj.setfAxleDesc(rs.getString("f_axle_descp"));
                dobj.setfAxleWeight(rs.getString("f_axle_weight"));
                dobj.setrAxleDesc(rs.getString("r_axle_descp"));
                dobj.setrAxleWeight(rs.getString("r_axle_weight"));
                dobj.setoAxleDesc(rs.getString("o_axle_descp"));
                dobj.setoAxleWeight(rs.getString("o_axle_weight"));
                dobj.settAxleDesc(rs.getString("t_axle_descp"));
                dobj.settAxleWeight(rs.getString("t_axle_weight"));
                dobj.setStateName(rs.getString("state_name"));
                dobj = getModelManuLocation(rs.getString("model_manu_loc"), dobj);
                if (purCd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                    dobj.setOfficeTmpName(rs.getString("off_to_name"));
                }
                if (printType.equals("FORM21AfterApproval")) {
                    if (ServerUtil.isTransport(rs.getInt("vh_class"), null)) {
                        dobj.setVehType("Transport");
                    } else {
                        dobj.setVehType("Non-Transport");
                    }
                    dobj.setPurchaseDate(rs.getString("purchase_date"));
                    dobj.setKitSrNo(rs.getString("kit_srno"));
                    dobj.setKitManuf(rs.getString("kit_manuf"));
                    dobj.setKitType(rs.getString("kit_type"));
                    dobj.setWorkShop(rs.getString("workshop"));
                    dobj.setFitmentDate(rs.getString("fitment_dt"));
                    dobj.setApprovalDate(rs.getString("approval_dt"));
                    dobj.setApprovalNo(rs.getString("approval_no"));
                    dobj.setCylSrNo(rs.getString("cyl_srno"));
                    dobj.setHydroTestDate(rs.getString("hydro_test_dt"));
                    if (rs.getInt("fuel") == TableConstants.VM_FUEL_TYPE_PETROL_CNG || rs.getInt("fuel") == TableConstants.VM_FUEL_CNG_TYPE) {
                        dobj.setFuelType("CngFuel");
                    }
                }
            }
        } catch (VahanException ve) {
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
        return dobj;
    }

    public DealertReportsDobj getModelManuLocation(String modelMenuLocCode, DealertReportsDobj dealerDobj) {
        if (!CommonUtils.isNullOrBlank(modelMenuLocCode)) {
            switch (modelMenuLocCode) {
                case "M":
                    dealerDobj.setModelManuLocCodeDescr("Manufactured in India");
                    break;
                case "A":
                    dealerDobj.setModelManuLocCodeDescr("Assembled in India(Imported)");
                    break;
                case "I":
                    dealerDobj.setModelManuLocCodeDescr("Fully Built Imported");
                    break;
            }
        }
        return dealerDobj;
    }

    public List<DealertReportsDobj> getHSRPPendencyReports(String dealerCd, String stateCd, Date fromDate, Date uptoDate) throws VahanException {
        String sql = "";
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        DealertReportsDobj dobj = null;
        List<DealertReportsDobj> dobjList = new ArrayList<>();
        try {
            tmgr = new TransactionManagerReadOnly("getHSRPPendencyReports");
            sql = "select o.off_name,d.appl_no, a.regn_no, a.owner_name, 'NO' AS HSRP_FITTED, m.dealer_name,to_char(a.regn_dt, 'dd-MON-yyyy') as regn_dt \n"
                    + " from " + TableList.VA_OWNER + " a \n"
                    + " inner join " + TableList.VM_DEALER_MAST + " m on a.state_cd=m.state_cd and a.dealer_cd=m.dealer_cd\n"
                    + " inner join " + TableList.VA_DETAILS + " d on  d.appl_no=a.appl_no and d.state_cd = a.state_cd and a.off_cd=d.off_cd  and d.regn_no != 'NEW'  and  d.pur_cd = ? \n"
                    + " inner join " + TableList.TM_OFFICE + " o on o.state_cd = a.state_cd and o.off_cd= a.off_cd\n"
                    + " left join " + TableList.VT_HSRP + " p on a.regn_no=p.regn_no  and a.state_cd=p.state_cd and a.off_cd=p.off_cd \n"
                    + " where a.state_cd= ? and a.dealer_cd= ? and  a.regn_dt between ? and ? and p.regn_no is null \n"
                    + " UNION \n"
                    + " select o.off_name,d.appl_no, a.regn_no, a.owner_name, 'NO' AS HSRP_FITTED,m.dealer_name, to_char(a.regn_dt, 'dd-MON-yyyy') as regn_dt \n"
                    + " from " + TableList.VT_OWNER + " a\n"
                    + " inner join " + TableList.VM_DEALER_MAST + " m on a.state_cd=m.state_cd and a.dealer_cd=m.dealer_cd\n"
                    + " inner join " + TableList.VA_DETAILS + " d on  d.regn_no=a.regn_no and d.state_cd = a.state_cd and a.off_cd=d.off_cd  and d.regn_no != 'NEW'  and  d.pur_cd = ?\n"
                    + " inner join " + TableList.TM_OFFICE + " o on o.state_cd = a.state_cd and o.off_cd= a.off_cd\n"
                    + " left join " + TableList.VT_HSRP + " p on a.regn_no=p.regn_no  and a.state_cd=p.state_cd and a.off_cd=p.off_cd \n"
                    + " where a.state_cd= ? and a.dealer_cd= ? and  a.regn_dt between ? and ? and p.regn_no is null";

            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE);
            ps.setString(2, stateCd);
            ps.setString(3, dealerCd);
            ps.setDate(4, new java.sql.Date(fromDate.getTime()));
            ps.setDate(5, new java.sql.Date(uptoDate.getTime()));
            ps.setInt(6, TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE);
            ps.setString(7, stateCd);
            ps.setString(8, dealerCd);
            ps.setDate(9, new java.sql.Date(fromDate.getTime()));
            ps.setDate(10, new java.sql.Date(uptoDate.getTime()));
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new DealertReportsDobj();
                dobj.setOffName(rs.getString("off_name"));
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setRegnNo(rs.getString("regn_no"));
                dobj.setoName(rs.getString("owner_name"));
                dobj.setIsHSRPFitted(rs.getString("HSRP_FITTED"));
                dobj.setDealerName(rs.getString("dealer_name"));
                dobj.setRegnDate(rs.getString("regn_dt"));
                dobjList.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem occurred during getting HSRP fitted reports.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dobjList;
    }
}
