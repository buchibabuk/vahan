/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.reports;

import java.sql.PreparedStatement;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.reports.NoDuesCertReportDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author Mohd Afzal
 */
public class NoDuesCertReportImpl {

    private static final Logger LOGGER = Logger.getLogger(NoDuesCertReportImpl.class);

    public static NoDuesCertReportDobj getNoDuesCertDetails(String appl_no) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        NoDuesCertReportDobj dobj = null;
        String sql;
        RowSet rs;
        try {
            tmgr = new TransactionManagerReadOnly("PrintDocImpl.isRegnExistForFC");
            sql = "select a.appl_no,a.regn_no,b.owner_name as transferorName,b.f_name as transferorGuardianName ,b.c_add1 as transferorAdd1,b.c_add2 as transferorAdd2,b.c_add3 as transferorAdd3,to_char(b.sale_dt,'dd-Mon-yyyy') as dateOfSale,to_char(b.transfer_dt,'dd-Mon-yyyy') as dateOftransfer"
                    + ",f.owner_name as transfereeName,f.f_name as transfereeGuardianName ,f.c_add1 as transfereeAdd1 ,f.c_add2 as transfereeAdd2,f.c_add3 as transfereeAdd3"
                    + ",c.descr as vh_class_descr ,d.off_name as transferorOffice ,to_char(current_timestamp,'dd-Mon-yyyy hh24:mi:ss') as printed_on,e.descr as place ,g.off_name as transfereeOffice  \n"
                    + " from  " + TableList.VT_NOC + " a\n"
                    + " INNER JOIN " + TableList.VA_TO + " b on  b.appl_no = a.appl_no and b.state_cd = a.state_to \n"
                    + " left outer join " + TableList.VT_OWNER + " f on f.regn_no = b.regn_no and f.state_cd = a.state_cd and f.off_cd = a.off_cd \n"
                    + " left outer join " + TableList.VM_VH_CLASS + " c on c.vh_class = f.vh_class \n"
                    + " LEFT OUTER JOIN " + TableList.TM_OFFICE + " d on d.state_cd = a.state_cd and d.off_cd =a.off_cd \n"
                    + " LEFT OUTER JOIN " + TableList.TM_STATE + " e on e.state_code = a.state_cd \n"
                    + " LEFT OUTER JOIN " + TableList.TM_OFFICE + " g on g.state_cd = b.state_cd and g.off_cd =b.off_cd \n"
                    + " where a.appl_no=? order by a.noc_dt DESC";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new NoDuesCertReportDobj();
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setTransferorName(rs.getString("transferorName"));
                dobj.setTransferorGuardianName(rs.getString("transferorGuardianName"));
                dobj.setTransferorAdd1(rs.getString("transferorAdd1"));
                dobj.setTransferorAdd2(rs.getString("transferorAdd2"));
                dobj.setTransferorAdd3(rs.getString("transferorAdd3"));
                dobj.setDateOfSale(rs.getString("dateOfSale"));
                dobj.setDateOftransfer(rs.getString("dateOftransfer"));
                dobj.setTransfereeName(rs.getString("transfereeName"));
                dobj.setTransfereeGuardianName(rs.getString("transfereeGuardianName"));
                dobj.setTransfereeAdd1(rs.getString("transfereeAdd1"));
                dobj.setTransfereeAdd2(rs.getString("transfereeAdd2"));
                dobj.setTransfereeAdd3(rs.getString("transfereeAdd3"));
                dobj.setVch_class(rs.getString("vh_class_descr"));
                dobj.setTransferorOffice(rs.getString("transferorOffice"));
                dobj.setTransfereeOffice(rs.getString("transfereeOffice"));
                dobj.setPlace(rs.getString("place"));
                dobj.setPrinted_date(rs.getString("printed_on"));

            }
        } catch (Exception ex) {
            throw new VahanException("Error in fetching details for [ " + appl_no + "]");

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
