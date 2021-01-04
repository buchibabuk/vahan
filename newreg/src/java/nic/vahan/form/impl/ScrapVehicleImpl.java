/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.faces.context.FacesContext;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.reports.ScrapVehicleDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author afzal
 */
public class ScrapVehicleImpl {

    private static final Logger LOGGER = Logger.getLogger(OwnerDisclaimerImpl.class);

    public static ArrayList<ScrapVehicleDobj> getScrapDetails(String old_regn_no) throws VahanException {
        ArrayList<ScrapVehicleDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = "";
        RowSet rs = null;
        String new_regn_no = "";
        String regn_no = "";
        String scrap_reason_descr = null;
        ScrapVehicleDobj dobj = null;
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            tmgr = new TransactionManagerReadOnly("getPurCdPrintDocsDetails");
            sql = "select * from " + TableList.VT_SURRENDER_RETENTION + " where state_cd=? and off_cd=? and old_regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            ps.setString(3, old_regn_no);
            rs = tmgr.fetchDetachedRowSetWithoutTrim_No_release();
            if (rs.next()) {
                new_regn_no = rs.getString("new_regn_no");
                regn_no = new_regn_no;
            } else {
                regn_no = old_regn_no;
            }

            sql = "select distinct a.old_regn_no, a.old_chasi_no, a.scrap_cert_no,"
                    + " a.scrap_reason,a.loi_no,a.state_cd,a.off_cd,a.new_regn_no ,b.descr as scrap_reson_descr"
                    + " from " + TableList.VT_SCRAP_VEHICLE + " a"
                    + " INNER JOIN " + TableList.VM_SCRAP_REASON + " b on a.scrap_reason=b.code"
                    + " where a.state_cd = ? and a.off_cd = ? and old_regn_no=?";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            ps.setString(3, regn_no);
            rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                dobj = new ScrapVehicleDobj();
                dobj.setOld_regn_no(old_regn_no);
                dobj.setNew_regn_no(new_regn_no);
                dobj.setOld_chasi_no(rs.getString("old_chasi_no"));
                dobj.setScrap_cert_no(rs.getString("scrap_cert_no"));
                dobj.setScrap_reason(rs.getInt("scrap_reason"));
                scrap_reason_descr = rs.getString("scrap_reson_descr");
                dobj.setLoi_no(rs.getString("loi_no"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setState_cd(rs.getString("state_cd"));
                list.add(dobj);
            }

            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("oldregnno", regn_no);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("statecd", Util.getUserStateCode());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("offcd", Util.getSelectedSeat().getOff_cd());
            if (scrap_reason_descr != null) {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("scrap_reason", scrap_reason_descr);
            }
            if (!"".equalsIgnoreCase(new_regn_no)) {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("newregn_no", old_regn_no);
            } else {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("newregn_no", "NA");
            }
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
        return list;
    }
}
