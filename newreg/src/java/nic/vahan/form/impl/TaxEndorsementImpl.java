/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.TaxEndorsementDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import org.apache.log4j.Logger;

/**
 *
 * @author nicsi
 */
public class TaxEndorsementImpl {

    private static final Logger LOGGER = Logger.getLogger(TaxEndorsementImpl.class);

    public List<TaxEndorsementDobj> getEndorsmentDetails(String applno, String stateCd, int offCd) {
        TaxEndorsementDobj endorsntTaxEntryDobj = null;
        TransactionManagerReadOnly tmgr = null;
        List<TaxEndorsementDobj> endsList = new ArrayList<TaxEndorsementDobj>();
        try {
            tmgr = new TransactionManagerReadOnly("getEndorsmentDetails Method:");
            String qry = "select * from va_endorsement_tax where appl_no=? and state_cd=? and off_cd=?";
            PreparedStatement ps = tmgr.prepareStatement(qry);
            ps.setString(1, applno);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                endorsntTaxEntryDobj = new TaxEndorsementDobj();
                endorsntTaxEntryDobj.setRegnNo(rs.getString("regn_no"));
                endorsntTaxEntryDobj.setStateCd(stateCd);
                endorsntTaxEntryDobj.setOffCd(offCd);
                endorsntTaxEntryDobj.setEmpCode(Util.getEmpCode());
                endorsntTaxEntryDobj.setSerial_no(rs.getInt("serial_no"));
                endorsntTaxEntryDobj.setWithEffectDate(rs.getDate("with_effect_tax_dt"));
                endorsntTaxEntryDobj.setEndorsFromDate(rs.getDate("endorsmnttax_from_dt"));
                endorsntTaxEntryDobj.setEndorsUpto(rs.getDate("endorsmnttax_upto_dt"));
                endorsntTaxEntryDobj.setModMnulAuto(rs.getString("tax_mode"));
                endorsntTaxEntryDobj.setNoofQuarter(rs.getLong("no_of_quarter"));
                endorsntTaxEntryDobj.setTaxRate(rs.getLong("tax_rate"));
                endorsntTaxEntryDobj.setRemark(rs.getString("remark"));
                endsList.add(endorsntTaxEntryDobj);
            }

        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
            return endsList;

        }
    }

    public String inwardEndorsmentTax(List<TaxEndorsementDobj> listEndosrsmts) throws VahanException {

        TransactionManager tmgr = null;
        String applno = null;
        try {

            if (listEndosrsmts.isEmpty()) {
                return "";
            }
            TaxEndorsementDobj endorsntTaxEntryDobj = listEndosrsmts.get(0);

            String regnNo = endorsntTaxEntryDobj.getRegnNo();
            Status_dobj status = new Status_dobj();
            status.setState_cd(Util.getUserStateCode());
            status.setEmp_cd(0);
            status.setPur_cd(TableConstants.ENDORSMENT_TAX);
            status.setOff_cd(Util.getUserOffCode());
            status.setStatus("N");
            tmgr = new TransactionManager("inwartEndorsmentTax");
            int initialFlow[] = ServerUtil.getInitialAction(tmgr, status.getState_cd(), TableConstants.ENDORSMENT_TAX, null);
            status.setFlow_slno(1);
            status.setFile_movement_slno(1);
            status.setAction_cd(TableConstants.TM_ENDORSMENT_TAX_ENTRY);
            applno = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
            if (CommonUtils.isNullOrBlank(applno)) {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
            status.setAppl_no(applno);
            status.setRegn_no(regnNo);
            ArrayList<Status_dobj> applicationStatus = ServerUtil.applicationStatusByApplNo(applno, Util.getUserStateCode());
            if (applicationStatus.isEmpty()) {
                ServerUtil.fileFlowForNewApplication(tmgr, status);
                status.setStatus(TableConstants.STATUS_COMPLETE);
                status = ServerUtil.webServiceForNextStage(status, tmgr);
                ServerUtil.fileFlow(tmgr, status);
            }
            insertOrUpdateEndorsmtTax(tmgr, listEndosrsmts, applno);


            tmgr.commit();
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
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

        return applno;
    }

    public void insertOrUpdateEndorsmtTax(TransactionManager tmgr, List<TaxEndorsementDobj> listEndosrsmts, String applno) throws VahanException {

        if (listEndosrsmts.isEmpty()) {
            return;
        }
        try {
            TaxEndorsementDobj endsmntDobj = listEndosrsmts.get(0);
            String qry = "select * from va_endorsement_tax where appl_no=? and state_cd=? and off_cd=?";
            PreparedStatement ps = tmgr.prepareStatement(qry);
            ps.setString(1, applno);
            ps.setString(2, endsmntDobj.getStateCd());
            ps.setInt(3, endsmntDobj.getOffCd());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                ServerUtil.insertIntoVhaTable(tmgr, applno, Util.getEmpCode(), "vha_endorsement_tax", "va_endorsement_tax");
                // UpdateIntoVaEndorsTax(tmgr, listEndosrsmts, applno);
                qry = "delete from va_endorsement_tax where appl_no=? and state_cd=? and off_cd=?";
                ps = tmgr.prepareStatement(qry);
                ps.setString(1, applno);
                ps.setString(2, endsmntDobj.getStateCd());
                ps.setInt(3, endsmntDobj.getOffCd());
                ps.executeUpdate();
                insertEndorsmentTax(tmgr, listEndosrsmts, applno);
            } else {
                insertEndorsmentTax(tmgr, listEndosrsmts, applno);
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }

    }

    public void insertEndorsmentTax(TransactionManager tmgr, List<TaxEndorsementDobj> listEndosrsmts, String applno) throws VahanException {
        try {
            if (listEndosrsmts.isEmpty()) {
                return;
            }
            int i = 1;
            for (TaxEndorsementDobj endorsntTaxEntryDobj : listEndosrsmts) {
                String qry = "INSERT INTO vahan4.va_endorsement_tax(\n"
                        + "            state_cd, off_cd, regn_no, appl_no,serial_no, with_effect_tax_dt, endorsmnttax_from_dt, \n"
                        + "            endorsmnttax_upto_dt, tax_mode, no_of_quarter, tax_rate, remark, \n"
                        + "            emp_code, op_dt)\n"
                        + "    VALUES (?, ?, ?, ?, ?, ?, \n"
                        + "            ?, ?, ?, ?, ?,?, \n"
                        + "            ?, current_timestamp);";

                PreparedStatement ps = tmgr.prepareStatement(qry);
                ps.setString(1, endorsntTaxEntryDobj.getStateCd());
                ps.setInt(2, endorsntTaxEntryDobj.getOffCd());
                ps.setString(3, endorsntTaxEntryDobj.getRegnNo());
                ps.setString(4, applno);
                ps.setInt(5, i++);
                ps.setDate(6, endorsntTaxEntryDobj.getWithEffectDate() == null ? null : new java.sql.Date(endorsntTaxEntryDobj.getWithEffectDate().getTime()));
                ps.setDate(7, endorsntTaxEntryDobj.getEndorsFromDate() == null ? null : new java.sql.Date(endorsntTaxEntryDobj.getEndorsFromDate().getTime()));
                ps.setDate(8, endorsntTaxEntryDobj.getEndorsUpto() == null ? null : new java.sql.Date(endorsntTaxEntryDobj.getEndorsUpto().getTime()));
                ps.setString(9, endorsntTaxEntryDobj.getModMnulAuto());
                ps.setLong(10, endorsntTaxEntryDobj.getNoofQuarter());
                ps.setLong(11, endorsntTaxEntryDobj.getTaxRate());
                ps.setString(12, endorsntTaxEntryDobj.getRemark());
                ps.setString(13, endorsntTaxEntryDobj.getEmpCode());
                ps.executeUpdate();
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public void saveModifyEndosmntTax(String applNo, List<ComparisonBean> listChanges, List<TaxEndorsementDobj> listEndosrsmts) throws VahanException {


        TransactionManager tmgr = null;
        String applno = null;
        try {
            tmgr = new TransactionManager("saveModifyEndosmntTax");
            insertOrUpdateEndorsmtTax(tmgr, listEndosrsmts, applNo);
            String changedData = "";
            changedData = ComparisonBeanImpl.changedDataContents(listChanges);

            if (!changedData.isEmpty()) {
                insertIntoVhaChangedData(tmgr, applNo, changedData);
            }
            tmgr.commit();
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
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

    }

    public void saveAndMoveEndosmntTax(Status_dobj status_dobj, List<TaxEndorsementDobj> listEndosrsmts, String applNo, List<ComparisonBean> listChanges, List<TaxEndorsementDobj> historyEndosrsmtsList) throws VahanException {

        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("saveAndMoveEndosmntTax");
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            if (status_dobj.getCurrent_role() == TableConstants.TM_ENDORSMENT_TAX_VERIFY
                    || status_dobj.getCurrent_role() == TableConstants.TM_ENDORSMENT_TAX_ENTRY) {
                if (!listChanges.isEmpty()) {
                    insertOrUpdateEndorsmtTax(tmgr, listEndosrsmts, applNo);
                }

            }
            if (status_dobj.getCurrent_role() == TableConstants.TM_ENDORSMENT_TAX_APPROVAL && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                //insertOrUpdateEndorsmtTax(tmgr, listEndosrsmts, applNo);

                if (!historyEndosrsmtsList.isEmpty()) {
                    TaxEndorsementDobj endorsntTaxEntryDobj = historyEndosrsmtsList.get(0);

                    String qry = "INSERT INTO vahan4.vh_endorsement_tax select *,? as entry_status, \n"
                            + "            ? as moved_by, current_timestamp as moved_on from vt_endorsement_tax where regn_no=? and appl_no=? ";

                    PreparedStatement ps = tmgr.prepareStatement(qry);
                    ps.setString(1, "N");//N-> For New Endorsement Tax Entry
                    ps.setString(2, Util.getEmpCode());
                    ps.setString(3, endorsntTaxEntryDobj.getRegnNo());
                    ps.setString(4, endorsntTaxEntryDobj.getApplNo());
                    ps.executeUpdate();
                    qry = "delete from vt_endorsement_tax where regn_no=? and appl_no=?";
                    ps = tmgr.prepareStatement(qry);
                    ps.setString(1, endorsntTaxEntryDobj.getRegnNo());
                    ps.setString(2, endorsntTaxEntryDobj.getApplNo());
                    ps.executeUpdate();
                }

                int i = 1;
                for (TaxEndorsementDobj endorsntTaxEntryDobj : listEndosrsmts) {
                    String qry = "INSERT INTO vahan4.vt_endorsement_tax(\n"
                            + "            state_cd, off_cd, regn_no, appl_no,serial_no, with_effect_tax_dt, endorsmnttax_from_dt, \n"
                            + "            endorsmnttax_upto_dt, tax_mode, no_of_quarter, tax_rate, remark, \n"
                            + "            emp_code,op_dt)\n"
                            + "    VALUES (?, ?, ?, ?, ?, ?,?, \n"
                            + "            ?, ?, ?, ?, ?, \n"
                            + "            ?,  current_timestamp);";


                    PreparedStatement ps = tmgr.prepareStatement(qry);
                    ps.setString(1, endorsntTaxEntryDobj.getStateCd());
                    ps.setInt(2, endorsntTaxEntryDobj.getOffCd());
                    ps.setString(3, endorsntTaxEntryDobj.getRegnNo());
                    ps.setString(4, applNo);
                    ps.setInt(5, i++);
                    ps.setDate(6, endorsntTaxEntryDobj.getWithEffectDate() == null ? null : new java.sql.Date(endorsntTaxEntryDobj.getWithEffectDate().getTime()));
                    ps.setDate(7, endorsntTaxEntryDobj.getEndorsFromDate() == null ? null : new java.sql.Date(endorsntTaxEntryDobj.getEndorsFromDate().getTime()));
                    ps.setDate(8, endorsntTaxEntryDobj.getEndorsUpto() == null ? null : new java.sql.Date(endorsntTaxEntryDobj.getEndorsUpto().getTime()));
                    ps.setString(9, endorsntTaxEntryDobj.getModMnulAuto());
                    ps.setLong(10, endorsntTaxEntryDobj.getNoofQuarter());
                    ps.setLong(11, endorsntTaxEntryDobj.getTaxRate());
                    ps.setString(12, endorsntTaxEntryDobj.getRemark());
                    ps.setString(13, endorsntTaxEntryDobj.getEmpCode());
                    //ps.setBoolean(14, false);//used for endorsment 
                    ps.executeUpdate();
                }
                ServerUtil.insertIntoVhaTable(tmgr, applNo, Util.getEmpCode(), "vha_endorsement_tax", "va_endorsement_tax");
                ServerUtil.deleteFromTable(tmgr, null, applNo, "va_endorsement_tax");
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);
            }

            String changedData = "";
            changedData = ComparisonBeanImpl.changedDataContents(listChanges);

            if (!changedData.isEmpty()) {
                insertIntoVhaChangedData(tmgr, applNo, changedData);
            }

            ServerUtil.fileFlow(tmgr, status_dobj);
            tmgr.commit();
            // return status_dobj;
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Update of Registration Details");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    //Check Endorsement Entry
    public List<TaxEndorsementDobj> getVTEndorsmentDetails(String regnNo, String stateCd, int offCd) {
        TaxEndorsementDobj endorsntTaxEntryDobj = null;
        TransactionManagerReadOnly tmgr = null;
        List<TaxEndorsementDobj> endorsmntTaxList = new ArrayList<TaxEndorsementDobj>();
        try {
            tmgr = new TransactionManagerReadOnly("getEndorsmentDetails Method:");
            String qry = "select * from vt_endorsement_tax where regn_no=? and state_cd=? order by op_dt desc";
            PreparedStatement ps = tmgr.prepareStatement(qry);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                endorsntTaxEntryDobj = new TaxEndorsementDobj();
                endorsntTaxEntryDobj.setRegnNo(rs.getString("regn_no"));
                endorsntTaxEntryDobj.setApplNo(rs.getString("appl_no"));
                endorsntTaxEntryDobj.setStateCd(stateCd);
                endorsntTaxEntryDobj.setOffCd(offCd);
                endorsntTaxEntryDobj.setEmpCode(Util.getEmpCode());
                endorsntTaxEntryDobj.setSerial_no(rs.getInt("serial_no"));
                endorsntTaxEntryDobj.setWithEffectDate(rs.getDate("with_effect_tax_dt"));
                endorsntTaxEntryDobj.setEndorsFromDate(rs.getDate("endorsmnttax_from_dt"));
                endorsntTaxEntryDobj.setEndorsUpto(rs.getDate("endorsmnttax_upto_dt"));
                endorsntTaxEntryDobj.setModMnulAuto(rs.getString("tax_mode"));
                endorsntTaxEntryDobj.setNoofQuarter(rs.getLong("no_of_quarter"));
                endorsntTaxEntryDobj.setTaxRate(rs.getLong("tax_rate"));
                endorsntTaxEntryDobj.setRemark(rs.getString("remark"));
                endorsmntTaxList.add(endorsntTaxEntryDobj);
            }

        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
            return endorsmntTaxList;

        }
    }

    public PassengerPermitDetailDobj getPermitDobjFromVhaPermitNew(Owner_dobj owner_dobj) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        PassengerPermitDetailDobj permitDobj = null;
        try {
            tmgr = new TransactionManagerReadOnly("In method getPermitDobjFromVhaPermitNew");
            String qry = "SELECT  * FROM " + TableList.VA_DETAILS + " where regn_no = ? and pur_cd in (1,123)";
            PreparedStatement ps = tmgr.prepareStatement(qry);
            ps.setString(1, owner_dobj.getRegn_no());
            RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
            if (rs1.next()) {
                qry = "select regn_no,pmt_type ,pmt_catg ,service_type ,route_class ,route_length,no_of_trips ,domain_cd ,"
                        + " distance_run_in_quarter,other_criteria,op_dt from (\n"
                        + "(select regn_no,pmt_type ,pmt_catg ,service_type ,route_class ,route_length,no_of_trips ,domain_cd ,distance_run_in_quarter,other_criteria,op_dt from vt_tax_based_on where regn_no=? and state_cd=?  order by op_dt desc limit 1) \n"
                        + "union\n"
                        + "(select appl_no,pmt_type ,pmt_catg ,service_type ,0 as route_class ,0 as route_length, 0 as no_of_trips ,0 as domain_cd ,0 as distance_run_in_quarter,0 as other_criteria,op_dt from vha_permit_new_regn where appl_no =? and state_cd=? order by op_dt desc limit 1)\n"
                        + ") a order by op_dt desc limit 1";

                ps = tmgr.prepareStatement(qry);
                ps.setString(1, owner_dobj.getRegn_no());
                ps.setString(2, owner_dobj.getState_cd());
                ps.setString(3, rs1.getString("appl_no"));
                ps.setString(4, owner_dobj.getState_cd());
                RowSet rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    permitDobj = new PassengerPermitDetailDobj();
                    permitDobj.setPmt_type_code(rs.getString("pmt_type"));
                    permitDobj.setPmtCatg(rs.getString("pmt_catg"));
                    permitDobj.setServices_TYPE(rs.getString("service_type"));
                    permitDobj.setRout_code(rs.getString("route_class"));
                    permitDobj.setRout_length(rs.getString("route_length"));
                    permitDobj.setNumberOfTrips(rs.getString("no_of_trips"));
                    permitDobj.setDomain_CODE(rs.getString("domain_cd"));
                    permitDobj.setOtherCriteria(rs.getString("other_criteria"));
                    permitDobj.setOp_dt(rs.getDate("op_dt"));
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "" + ex.getStackTrace()[0]);
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
        return permitDobj;
    }

    public String checkPermitDtlsInOtherOffice(String regno, String stateCd, int offCd) throws VahanException {
        String issueDate = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("checkPermitDtlsInOtherOffic");
            String sql = "(select to_char(issue_dt,'DD-Mon-YYYY') as issue_dt_descr from permit.vt_temp_permit where regn_no  =? and state_cd=? and valid_upto::date>current_date and off_cd=? order by op_dt desc limit 1)\n"
                    + "union\n"
                    + "(select to_char(issue_dt,'DD-Mon-YYYY') as issue_dt_descr from permit.vt_permit where regn_no  =? and state_cd=? and valid_upto::date>current_date and off_cd=? order by op_dt desc limit 1)";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regno);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.setString(4, regno);
            ps.setString(5, stateCd);
            ps.setInt(6, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                issueDate = rs.getString("issue_dt_descr");
            } else {
                throw new VahanException("Vehicle Details not Found!");
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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

        return issueDate;
    }

    public PassengerPermitDetailDobj getPermitDtlsWithTmpPmt(String regno, String stateCd) throws VahanException {
        String issueDate = null;
        TransactionManagerReadOnly tmgr = null;
        PassengerPermitDetailDobj permitDobj = null;
        try {
            tmgr = new TransactionManagerReadOnly("getPermitDtlsWithTmpPmt");
            String sql = " select regn_no,pmt_type ,pmt_catg ,service_type ,route_class ,route_length,no_of_trips ,domain_cd ,"
                    + " distance_run_in_quarter,other_criteria,op_dt,issue_dt_descr,issue_dt from ((select a.regn_no,a.pmt_type ,a.pmt_catg ,b.service_type  ,b.route_length,0 as route_class , 0 as no_of_trips ,0 as domain_cd ,0 as distance_run_in_quarter,0 as other_criteria,\n"
                    + " a.op_dt,to_char(issue_dt,'DD-Mon-YYYY') as issue_dt_descr,issue_dt from permit.vt_temp_permit a left join permit.vt_tempspl_tax_based_on_permit b\n"
                    + " on a.regn_no=b.regn_no and a.state_cd=b.state_cd  where a.regn_no  =? and a.state_cd=? and a.valid_upto::date>current_date order by a.op_dt,b.op_dt desc limit 1)\n"
                    + " union\n"
                    + " (select regn_no,pmt_type ,pmt_catg ,service_type ,0 as route_class ,0 as route_length,0 as no_of_trips ,domain_cd ,\n"
                    + "  0 as distance_run_in_quarter,0 as other_criteria,op_dt,to_char(issue_dt,'DD-Mon-YYYY') as issue_dt_descr,issue_dt from permit.vt_permit where regn_no  =? and state_cd=? and valid_upto::date>current_date order by op_dt desc limit 1)) c order by op_dt desc limit 1";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regno);
            ps.setString(2, stateCd);
            ps.setString(3, regno);
            ps.setString(4, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                permitDobj = new PassengerPermitDetailDobj();
                permitDobj.setPmt_type_code(rs.getString("pmt_type"));
                permitDobj.setPmtCatg(rs.getString("pmt_catg"));
                permitDobj.setServices_TYPE(rs.getString("service_type"));
                permitDobj.setRout_code(rs.getString("route_class"));
                permitDobj.setRout_length(rs.getString("route_length"));
                permitDobj.setNumberOfTrips(rs.getString("no_of_trips"));
                permitDobj.setDomain_CODE(rs.getString("domain_cd"));
                permitDobj.setOtherCriteria(rs.getString("other_criteria"));
                permitDobj.setOp_dt(rs.getDate("op_dt"));
                issueDate = rs.getString("issue_dt_descr");
                permitDobj.setIssuePmtDateDescr(issueDate);
                permitDobj.setIssuePmtDate(rs.getDate("issue_dt"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
        return permitDobj;
    }
}
