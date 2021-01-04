/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.NonUseDobj;
import nic.vahan.form.dobj.TmConfigurationNonUseDobj;
import nic.vahan.server.ServerUtil;

/**
 *
 * @author acer
 */
public class NonUseReportingImpl {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(NonUseReportingImpl.class);
    private static boolean blnNonTbl = false;

    public static NonUseDobj getDetails(String regn_no, String state_cd, int off_cd, TmConfigurationNonUseDobj configDobj, String radiobuttonValue) throws VahanException, SQLException {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        NonUseDobj dobj = null;
        String tableName = "";
        String colName = "";
        String sqlfield = "";

        RowSet rs;
        String sql = "select regn_no from " + TableList.VT_NON_USE_TAX_EXEM + " where regn_no=? and state_cd=?";
        tmgr = new TransactionManagerReadOnly("NonUseReportingImpl.getDetails()");
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, regn_no);
        ps.setString(2, state_cd);

        rs = tmgr.fetchDetachedRowSet();
        if (rs.next() && !"R".equalsIgnoreCase(radiobuttonValue)) {
            blnNonTbl = true;
            tableName = TableList.VT_NON_USE_TAX_EXEM;
            colName = "op_dt";
        } else {
            blnNonTbl = false;
            tableName = TableList.VHA_NON_USE_RESTORE_REMOVE;
            colName = "moved_on";
            sqlfield = ",nute.rr_flag,nute.vehicle_use_frm, vhn.exem_fr as TaxExemFr ,vhn.exem_to as TaxExemTo";
        }

        sql = "select"
                + " vo.owner_name, vvc.descr as vehicle_class, vvc.class_type as vehicle_type, "
                + " vch.catg_desc as vehicle_category, to_char(nute.exem_fr, 'dd-Mon-yyyy') as period_from, "
                + " to_char(nute.exem_to, 'dd-Mon-yyyy') as period_to, nute.remark as reason, nute.place,"
                + " nute.garage_add1 , nute.garage_add2 ,nute.appl_no ,"
                + " nute.garage_add3, nute.garage_district, nute.garage_state , nute.garage_pincode, "
                + " to_char(now(),'YYYY') as curr_year, to_char(current_timestamp, 'dd-Mon-yyyy') as dt, "
                + " tso.descr as owner_state, toff.off_name as owner_off,"
                + " gast.descr as garage_State_cd ,"
                + " gaff.descr as garage_district_descr " + sqlfield + ""
                + " from " + TableList.VT_OWNER + " vo "
                + " inner join " + tableName + " nute on nute.regn_no = vo.regn_no "
                + " left join " + TableList.VH_NON_USE_TAX_EXEM + " vhn on vhn.regn_no=vo.regn_no "
                + " inner join " + TableList.VM_VH_CLASS + " vvc on vo.vh_class = vvc.vh_class "
                + " inner join " + TableList.VM_VCH_CATG + " vch on vch.catg = vo.vch_catg"
                + " inner join " + TableList.TM_OFFICE + " toff on toff.state_cd = vo.state_cd and toff.off_cd = vo.off_cd"
                + " inner join " + TableList.TM_STATE + " tso on tso.state_code =  vo.state_cd "
                + " inner join TM_DISTRICT gaff on gaff.state_cd = nute.garage_state and gaff.dist_cd = nute.garage_district"
                + " inner join TM_STATE gast on gast.state_code =  nute.garage_state "
                + " where vo.regn_no = ?  and vo.state_cd = ? and vo.off_cd = ? "
                + " order by nute." + colName + " desc limit 1 ";
        try {
            //tmgr = new TransactionManagerReadOnly("NonUseReportingImpl.getDetails()");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new NonUseDobj();
                setDobj(rs, dobj, regn_no, configDobj);
            } else {
                throw new VahanException("No Record Found For This Registration No");
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

        return dobj;
    }

    private static void setDobj(RowSet rs, NonUseDobj dobj, String regn_no, TmConfigurationNonUseDobj configDobj) throws SQLException {
        dobj.setRegn_no(regn_no);
        dobj.setOwner_name(rs.getString("owner_name"));
        dobj.setVh_class_desc(rs.getString("vehicle_class"));
        dobj.setVehType(rs.getInt("vehicle_type"));
        if (dobj.getVehType() == TableConstants.VM_VEHTYPE_TRANSPORT) {
            dobj.setVehTypeDescr(TableConstants.VM_VEHTYPE_TRANSPORT_DESCR);
        } else if (dobj.getVehType() == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
            dobj.setVehTypeDescr(TableConstants.VM_VEHTYPE_NON_TRANSPORT_DESCR);
        }
        dobj.setVch_catg(rs.getString("vehicle_category"));
        dobj.setPeriod_from(rs.getString("period_from"));
        dobj.setPeriod_to(rs.getString("period_to"));
        if (!(rs.getString("reason").equalsIgnoreCase("0"))) {
            dobj.setNon_use_purpose(rs.getString("reason"));
        } else {
            dobj.setNon_use_purpose("Not Intending to Use or Keep for Use of Motor Vehicle");
        }
        dobj.setNewGarageLocation(rs.getString("place"));
        dobj.setOwner_state(rs.getString("owner_state"));
        dobj.setOff_name(rs.getString("owner_off"));
        dobj.setCurrent_year(rs.getString("curr_year"));
        dobj.setCurrent_date(rs.getString("dt"));
        dobj.setGarage_add1(rs.getString("garage_add1"));
        dobj.setGarage_add2(rs.getString("garage_add2"));
        dobj.setGarage_add3(rs.getString("garage_add3"));
        dobj.setGarage_district_descr(rs.getString("garage_district_descr"));
        dobj.setGarage_state(rs.getString("garage_State_cd"));
        dobj.setGarage_pincode(rs.getInt("garage_pincode"));
        dobj.setAppl_no(rs.getString("appl_no"));
        dobj.setRcpt_heading(ServerUtil.getRcptHeading());
        dobj.setRcpt_sub_heading(ServerUtil.getRcptSubHeading());
        if (configDobj != null && configDobj.isDeclare_withdrawl_date()) {
            dobj.setSection_act_rule(configDobj.getSection_act_rule());
            dobj.setApproved_authority(configDobj.getApproved_authority());
            if (!blnNonTbl) {
                String pattern = "dd-MMM-yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

                dobj.setVehicle_used_from_dtls("Now the applicant wishes to use vehicle from " + simpleDateFormat.format(rs.getDate("vehicle_use_frm")) + ".");
                dobj.setExemFrmDate(rs.getDate("TaxExemFr"));
                dobj.setExemUptoDate(rs.getDate("TaxExemTo"));

                if (dobj.getExemUptoDate().after(dobj.getExemFrmDate())) {
                    dobj.setNonUseDeclareDtls("\n Tax Exemption will be applicable for the duration From: " + simpleDateFormat.format(dobj.getExemFrmDate()) + "   To: " + simpleDateFormat.format(dobj.getExemUptoDate()) + " only.");
                } else {
                    dobj.setNonUseDeclareDtls("\n No Tax Exemption will be applicable.");
                }
            }
        } else {
            dobj.setSection_act_rule(configDobj.getSection_act_rule());
            dobj.setApproved_authority(configDobj.getApproved_authority());
        }
    }
}
