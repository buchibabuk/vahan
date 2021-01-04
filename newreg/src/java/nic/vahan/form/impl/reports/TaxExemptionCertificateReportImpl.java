/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.reports;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.reports.TaxExemCertDobj;
import nic.vahan.form.dobj.reports.VmRoadSafetySloganPrintDobj;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author Mohd Afzal
 */
public class TaxExemptionCertificateReportImpl {

    private static final Logger LOGGER = Logger.getLogger(TaxExemptionCertificateReportImpl.class);

    public static TaxExemCertDobj getTaxExemptionReportDobj(String regn_no, String state_cd, int off_cd) throws VahanException {

        TransactionManager tmgr = null;
        TaxExemCertDobj dobj = null;
        Exception e = null;
        TmConfigurationDobj configurationDobj = null;
        try {
            configurationDobj = Util.getTmConfiguration();
            tmgr = new TransactionManager("getCommonCarrierRegnCertListByRenoNoWise");
            String sql = "select a.regn_no,a.perm_no,to_char(a.perm_dt,'dd-Mon-yyyy') as permDate,to_char(a.op_dt,'dd-Mon-yyyy') as opDate,to_char(a.exem_fr,'dd-Mon-yyyy') as exemFrom,to_char(a.exem_to,'dd-Mon-yyyy') as exemUpto"
                    + ",upper(COALESCE(v.c_add1,'') || ',' || COALESCE(v.c_add2,'') || ',' || COALESCE(v.c_add3,'') || ',' || COALESCE(b.descr,'') || ',' || COALESCE(c.descr,'') || '-' || COALESCE(v.c_pincode::varchar, '')) as curr_address"
                    + ",upper(COALESCE(v.p_add1,'') || ',' || COALESCE(v.p_add2,'') || ',' || COALESCE(v.p_add3,'') || ',' || COALESCE(e.descr,'') || ',' || COALESCE(f.descr,'') || '-' || COALESCE(v.p_pincode::varchar, '')) as per_address"
                    + " ,to_char(v.regn_dt,'dd-Mon-yyyy') as regn_date,to_char(v.purchase_dt,'dd-Mon-yyyy') as purchase_date,v.owner_name,v.f_name,v.chasi_no,v.eng_no,d.off_name,g.descr as ownerType,va.appl_no,to_char(current_timestamp,'dd-Mon-yyyy hh24:mi:ss') as printeddate,h.rcpt_heading,h.rcpt_subheading"
                    + " from  " + TableList.VT_TAX_EXEM + " a \n"
                    + " inner join " + TableList.VA_DETAILS + " va on va.regn_no = a.regn_no and va.pur_cd in (125) and va.state_cd=a.state_cd and va.off_cd=a.off_cd \n"
                    + " left outer join " + TableList.VT_OWNER + " v on v.regn_no = a.regn_no and v.state_cd = a.state_cd and v.off_cd = a.off_cd "
                    + " left outer join " + TableList.TM_DISTRICT + " b on b.dist_cd = v.c_district "
                    + " left outer join " + TableList.TM_STATE + " c on c.state_code = v.c_state "
                    + " left outer join " + TableList.TM_OFFICE + " d on d.state_cd = a.state_cd and d.off_cd=a.off_cd"
                    + " left outer join " + TableList.TM_DISTRICT + " e on e.dist_cd = v.p_district "
                    + " left outer join " + TableList.TM_STATE + " f on f.state_code = v.p_state "
                    + " left outer join " + TableList.VM_OWCODE + " g ON g.ow_code=v.owner_cd\n"
                    + " left outer join " + TableList.TM_CONFIGURATION + " h ON h.state_cd = a.state_cd"
                    + "  where a.regn_no =? and a.state_cd = ? and a.off_cd = ? order by a.op_dt DESC limit 1\n";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                dobj = new TaxExemCertDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setOwnershiptype(rs.getString("ownerType"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setF_name(rs.getString("f_name"));
                dobj.setRegn_date(rs.getString("regn_date"));
                dobj.setPurchase_date(rs.getString("purchase_date"));
                dobj.setChasi_no(rs.getString("chasi_no"));
                dobj.setEngine_no(rs.getString("eng_no"));
                dobj.setTemporary_address(rs.getString("curr_address"));
                dobj.setPermanent_address(rs.getString("per_address"));
                dobj.setFrom_date(rs.getString("exemFrom"));
                dobj.setUpto_date(rs.getString("exemUpto"));
                dobj.setOff_name(rs.getString("off_name"));
                dobj.setPrintedDate(rs.getString("printeddate"));
                dobj.setPerm_no(rs.getString("perm_no"));
                dobj.setPerm_date(rs.getString("permDate"));
                dobj.setOp_date(rs.getString("opDate"));
                dobj.setReport_header(rs.getString("rcpt_heading"));
                dobj.setReport_subheader(rs.getString("rcpt_subheading"));
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
            }
        } catch (SQLException ee) {
            e = ee;
        } catch (Exception ee) {
            e = ee;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        if (e != null) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return dobj;
    }
}
