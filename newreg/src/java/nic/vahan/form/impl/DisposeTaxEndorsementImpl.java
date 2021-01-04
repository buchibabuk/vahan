/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.TaxEndorsementDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author nicsi
 */
public class DisposeTaxEndorsementImpl {

    private static final Logger LOGGER = Logger.getLogger(DisposeTaxEndorsementImpl.class);

    public String inwardDisposeEndorsmentTax(List<TaxEndorsementDobj> listDisposeEndosrsmts) throws VahanException {

        TransactionManager tmgr = null;
        String applno = null;
        try {

            if (listDisposeEndosrsmts.isEmpty()) {
                return "";
            }
            TaxEndorsementDobj endorsntTaxEntryDobj = listDisposeEndosrsmts.get(0);

            String regnNo = endorsntTaxEntryDobj.getRegnNo();
            Status_dobj status = new Status_dobj();
            status.setState_cd(Util.getUserStateCode());
            status.setEmp_cd(0);
            status.setPur_cd(TableConstants.DISPOSE_ENDRSMNT_TAX);
            status.setOff_cd(Util.getUserOffCode());
            status.setStatus("N");
            tmgr = new TransactionManager("inwartEndorsmentTax");
            int initialFlow[] = ServerUtil.getInitialAction(tmgr, status.getState_cd(), TableConstants.DISPOSE_ENDRSMNT_TAX, null);
            status.setFlow_slno(1);
            status.setFile_movement_slno(1);
            status.setAction_cd(TableConstants.DISPOSE_TM_ENDRSMNT_TAX_ENTRY);
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

    public void saveAndMoveDisposeEndosmntTax(Status_dobj status_dobj, List<TaxEndorsementDobj> historylistEndosrsmts, String applNo, String empCode) throws VahanException {

        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("saveAndMoveEndosmntTax");
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            if (status_dobj.getCurrent_role() == TableConstants.DISPOSE_TM_ENDRSMNT_TAX_VERIFY
                    || status_dobj.getCurrent_role() == TableConstants.DISPOSE_TM_ENDRSMNT_TAX_ENTRY) {
            }
            if (status_dobj.getCurrent_role() == TableConstants.DISPOSE_TM_ENDRSMNT_TAX_APPROVAL && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                if (!historylistEndosrsmts.isEmpty()) {
                    TaxEndorsementDobj endorsntTaxEntryDobj = historylistEndosrsmts.get(0);
                    String qry = "INSERT INTO vahan4.vh_endorsement_tax select *,? as entry_status, \n"
                            + "            ? as moved_by, current_timestamp as moved_on from vt_endorsement_tax where regn_no=? and appl_no=?";

                    PreparedStatement ps = tmgr.prepareStatement(qry);
                    ps.setString(1, "D");//D-> For Dispose Endorsement Tax
                    ps.setString(2, empCode);
                    ps.setString(3, endorsntTaxEntryDobj.getRegnNo());
                    ps.setString(4, endorsntTaxEntryDobj.getApplNo());
                    ps.executeUpdate();
                    qry = "delete from vt_endorsement_tax where regn_no=? and appl_no=?";
                    ps = tmgr.prepareStatement(qry);
                    ps.setString(1, endorsntTaxEntryDobj.getRegnNo());
                    ps.setString(2, endorsntTaxEntryDobj.getApplNo());
                    ps.executeUpdate();
                }
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);
            }
            ServerUtil.fileFlow(tmgr, status_dobj);
            tmgr.commit();
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
}
