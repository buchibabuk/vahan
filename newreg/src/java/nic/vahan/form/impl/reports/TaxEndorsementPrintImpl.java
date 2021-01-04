/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.reports;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.reports.TaxEndorsementPrintDobj;
import nic.vahan.form.dobj.reports.VmRoadSafetySloganPrintDobj;
import static nic.vahan.form.impl.PrintDocImpl.getStateRoadSafetySlogan;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author Mohd Afzal
 */
public class TaxEndorsementPrintImpl {

    private static final Logger LOGGER = Logger.getLogger(TaxEndorsementPrintImpl.class);

    public TaxEndorsementPrintDobj getTaxEndorsementDetails(String state_cd, int off_cd, String enterNo, String radioBtnvalue) throws VahanException {

        TaxEndorsementPrintDobj dobj = null;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        String sql;
        RowSet rs;
        String whereClause = null;
        String qrText = null;
        try {
            TmConfigurationDobj configurationDobj = Util.getTmConfiguration();
            if (radioBtnvalue.equals("A")) {
                whereClause = "and a.appl_no=upper('" + enterNo + "')  ";
            } else if (radioBtnvalue.equals("R")) {
                whereClause = "and a.regn_no=upper('" + enterNo + "') ";
            }
            tmgr = new TransactionManagerReadOnly("NocPrintImpl.isApplExistForNOC method");
            sql = "select a.appl_no,a.regn_no,a.no_of_quarter,a.tax_rate,to_char(a.with_effect_tax_dt,'dd-Mon-yyyy') as with_effect_tax_dt,to_char(a.endorsmnttax_upto_dt,'dd-Mon-yyyy') as endorsmnttax_upto_dt,to_char(current_timestamp,'dd-Mon-yyyy hh24:mi:ss') as printed_on"
                    + " ,c.off_name,e.descr,b.rcpt_heading,b.rcpt_subheading"
                    + " from  " + TableList.VT_ENDORSEMENT_TAX + " a \n"
                    + " left outer join " + TableList.TM_CONFIGURATION + " b ON b.state_cd = a.state_cd \n"
                    + " left outer join " + TableList.TM_OFFICE + " c ON  c.off_cd = a.off_cd and c.state_cd=a.state_cd \n"
                    + " left outer join " + TableList.VT_OWNER + " d on d.regn_no=a.regn_no and d.state_cd = a.state_cd and d.off_cd = a.off_cd \n"
                    + " left outer join " + TableList.VM_VH_CLASS + " e on e.vh_class = d.vh_class \n"
                    + " where a.state_cd=? and a.off_cd=? " + whereClause + " order by a.op_dt desc limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new TaxEndorsementPrintDobj();
                if (rs.getString("appl_no") != null && !rs.getString("appl_no").isEmpty()) {
                    dobj.setAppl_no(rs.getString("appl_no"));
                    qrText = "Application No " + rs.getString("appl_no");
                }

                if (rs.getString("regn_no") != null && !rs.getString("regn_no").isEmpty()) {
                    dobj.setRegn_no(rs.getString("regn_no"));
                    qrText += " Registration No " + rs.getString("appl_no").toUpperCase();
                }
                dobj.setPrintDate(rs.getString("printed_on"));
                if (rs.getString("descr") != null && !rs.getString("descr").isEmpty()) {
                    dobj.setVch_class_desc(rs.getString("descr"));
                    qrText += "(" + rs.getString("descr") + ")";
                }
                if (rs.getString("with_effect_tax_dt") != null && !rs.getString("with_effect_tax_dt").isEmpty()) {
                    dobj.setWith_effect_from(rs.getString("with_effect_tax_dt"));
                    qrText += " Tax with effect from " + rs.getString("with_effect_tax_dt");
                }
                if (rs.getString("endorsmnttax_upto_dt") != null && !rs.getString("endorsmnttax_upto_dt").isEmpty()) {
                    dobj.setEndorsement_date(rs.getString("with_effect_tax_dt"));
                    qrText += " Tax Endorsement Date " + rs.getString("endorsmnttax_upto_dt");
                }

                if (rs.getString("rcpt_heading") != null && !rs.getString("rcpt_heading").isEmpty()) {
                    dobj.setHeader(rs.getString("rcpt_heading"));
                }
                if (rs.getString("rcpt_subheading") != null && !rs.getString("rcpt_subheading").isEmpty()) {
                    dobj.setSubHeader(rs.getString("rcpt_subheading"));
                }
                if (rs.getString("tax_rate") != null && !rs.getString("tax_rate").isEmpty()) {
                    dobj.setTax_rate(rs.getString("tax_rate"));
                }
                if (rs.getString("no_of_quarter") != null && !rs.getString("no_of_quarter").isEmpty()) {
                    dobj.setNoofqtr(rs.getString("no_of_quarter"));
                }
                if (rs.getString("off_name") != null && !rs.getString("off_name").isEmpty()) {
                    dobj.setOff_name(rs.getString("off_name"));
                }
                dobj.setQrtext(qrText);
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
                        VmRoadSafetySloganPrintDobj vmrssdobj = getStateRoadSafetySlogan();
                        if (vmrssdobj != null) {
                            dobj.setShowRoadSafetySlogan(configurationDobj.getTmPrintConfgDobj().isRoad_safety_slogan());
                            dobj.setRoadSafetySloganDobj(vmrssdobj);
                        }
                    }
                }
            } else {
                throw new VahanException("Application / Registration does not exist / Already Paid TAX or you are not authorized to print Tax Endorsement Certificate for this Application / Registration.!");
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
        return dobj;
    }
}
