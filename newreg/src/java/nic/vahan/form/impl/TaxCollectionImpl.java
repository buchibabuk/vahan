/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import org.apache.log4j.Logger;

/**
 *
 * @author nic
 */
public class TaxCollectionImpl {

    private static Logger LOGGER = Logger.getLogger(TaxCollectionImpl.class);

    public ArrayList getSelectePermitTypeAndGetPermitcage(int pmt_type, String state_cd) {
        ArrayList permit_catg = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        try {
            permit_catg = new ArrayList();
            permit_catg.clear();
            tmgr = new TransactionManager("getSelectePermitTypeAndGetPermitcage");
            String Query = "select * from " + TableList.VM_PERMIT_CATG + " where permit_type=? AND state_cd = ? order by descr";
            ps = tmgr.prepareStatement(Query);
            ps.setInt(1, pmt_type);
            ps.setString(2, state_cd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs != null) {
                while (rs.next()) {
                    permit_catg.add(new SelectItem(rs.getString("code"), rs.getString("descr")));
                }
            } else {
                return permit_catg;
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
        return permit_catg;
    }

    public ArrayList getDomainListOnStateCode(String state_cd) {
        ArrayList Domaincd = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        try {
            Domaincd = new ArrayList();
            Domaincd.clear();
            tmgr = new TransactionManager("getDomainListOnStateCode");
            String Query = "select * from " + TableList.VM_REGION + " where state_cd = ? order by region";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, state_cd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs != null) {
                while (rs.next()) {
                    Domaincd.add(new SelectItem(rs.getString("region_cd"), rs.getString("region")));
                }
            } else {
                return Domaincd;
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
        return Domaincd;
    }

    public ArrayList getOtherCriteriaListOnStateCode(String state_cd) {
        ArrayList OtherCriteriacd = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        try {
            OtherCriteriacd = new ArrayList();
            OtherCriteriacd.clear();
            tmgr = new TransactionManager("getOtherCriteriaListOnStateCode");
            String QueryOtherCriteria = "select * from " + TableList.VM_OTHER_CRITERIA + " where state_cd = ? order by criteria_desc";
            ps = tmgr.prepareStatement(QueryOtherCriteria);
            ps.setString(1, state_cd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs != null) {
                while (rs.next()) {
                    OtherCriteriacd.add(new SelectItem(rs.getString("criteria_cd"), rs.getString("criteria_desc")));
                }
            } else {
                return OtherCriteriacd;
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
        return OtherCriteriacd;
    }
}
