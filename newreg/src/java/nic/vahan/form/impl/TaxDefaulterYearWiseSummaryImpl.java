/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.reports.TaxDefaulterYearWiseSummaryDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author afzal
 */
public class TaxDefaulterYearWiseSummaryImpl {

    private static final Logger LOGGER = Logger.getLogger(TaxDefaulterYearWiseSummaryImpl.class);

    public ArrayList<TaxDefaulterYearWiseSummaryDobj> getTaxDefaulterYearWiseSummary(int pur_cd) throws VahanException {
        ArrayList<TaxDefaulterYearWiseSummaryDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            String sql = "select * from dashboard.gettaxdefaulteryearwisesummary(?,?,?)";
            tmgr = new TransactionManager("getPurCdPrintDocsDetails");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            ps.setInt(3, pur_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                TaxDefaulterYearWiseSummaryDobj dobj = new TaxDefaulterYearWiseSummaryDobj();
                dobj.setVchClassDesc(rs.getString("vh_class_desc"));
                dobj.setVchClasscode(rs.getInt("vh_class"));
                dobj.setTotalDefaulter(rs.getInt("total"));
                dobj.setWithinYearTotalDefaulter(rs.getInt("within_one_year"));
                dobj.setWithinOneToTwoYearTotalDefaulter(rs.getInt("one_to_two_year"));
                dobj.setWithinTwoToThreeYearTotalDefaulter(rs.getInt("two_to_three_year"));
                dobj.setWithinThreeToFourYearTotalDefaulter(rs.getInt("three_to_four_year"));
                dobj.setWithinFourToFiveYearTotalDefaulter(rs.getInt("four_to_five_year"));
                dobj.setWithinFiveToSixYearTotalDefaulter(rs.getInt("five_to_six_year"));
                dobj.setWithinSixToSevenYearTotalDefaulter(rs.getInt("six_to_seven_year"));
                dobj.setWithinSevenToEightYearTotalDefaulter(rs.getInt("seven_to_eight_year"));
                dobj.setWithinEightToNineYearTotalDefaulter(rs.getInt("eight_to_nine_year"));
                dobj.setWithinNineToTenYearTotalDefaulter(rs.getInt("nine_to_ten_year"));
                dobj.setMoreThanTenYearTotalDefaulter(rs.getInt("more_than_10_year"));
                list.add(dobj);
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
