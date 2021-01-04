/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.DocumentType;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.FeeDraftDobj;
import nic.vahan.form.dobj.permit.PermitApplicatonFeeDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.CommonPermitFeeDobj;
import nic.vahan.form.impl.FeeDraftimpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class PermitApplicatonFeeImpl {

    private static final Logger LOGGER = Logger.getLogger(PermitApplicatonFeeImpl.class);
    String recpt_no = "";
    PreparedStatement ps;
    RowSet rs;

    public PermitApplicatonFeeDobj getVehicleDetails(String appl_no, String regn_no) {
        TransactionManager tmgr = null;
        PermitApplicatonFeeDobj dobj = null;
        String Query;
        try {
            tmgr = new TransactionManager("getVehicleDetails");
            if (("New").equalsIgnoreCase(regn_no)) {
                Query = "select pmt.state_cd,pmt.off_cd,pmt_o.appl_no,pmt.regn_no,owner_name,f_name,pmt_o.vh_class,pmt.pmt_type,pmt.pmt_catg,pmt_o.seat_cap,pmt.region_covered \n"
                        + "from " + TableList.VA_PERMIT + " pmt inner join " + TableList.VA_PERMIT_OWNER + " pmt_o on pmt_o.appl_no = pmt.appl_no\n"
                        + "where pmt.appl_no = ?";
            } else {
                Query = " Select pmt.state_cd,pmt.off_cd,pmt.appl_no,pmt.regn_no,owner_name,f_name,own.vh_class,own.chasi_no,pmt.pmt_type,pmt.pmt_catg,own.seat_cap,pmt.region_covered \n"
                        + "from " + TableList.VA_PERMIT + " pmt inner join " + TableList.VT_OWNER + " own on own.regn_no = pmt.regn_no\n"
                        + "where pmt.appl_no=?";
            }
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new PermitApplicatonFeeDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setF_name(rs.getString("f_name"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setPmt_type(rs.getString("pmt_type"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setVh_class(rs.getString("vh_class"));
                dobj.setSeat_cap(rs.getString("seat_cap"));
                dobj.setPmt_catg(rs.getString("pmt_catg"));
                if (("New").equalsIgnoreCase(regn_no)) {
                    dobj.setChasi_no("NEW");
                } else {
                    dobj.setChasi_no(rs.getString("chasi_no"));
                }
                dobj.getPmtDobj().setRegion_covered(rs.getString("region_covered"));
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

    public String saveFeeDetails(CommonPermitFeeDobj pmt_fee_dobj, FeeDraftDobj feeDraftDobj) throws VahanException {
        TransactionManager tmgr = null;
        CommonPermitFeeImpl pmt_fee_impl;
        FeeDraftimpl feeDraft_impl;
        String rcpt_no = "";
        long inscd = 0;
        try {
            pmt_fee_impl = new CommonPermitFeeImpl();
            feeDraft_impl = new FeeDraftimpl();
            tmgr = new TransactionManager("Save Fee Details");
            rcpt_no = pmt_fee_impl.createRecpt_no(tmgr);
            pmt_fee_dobj.setRcpt_no(rcpt_no);
            if (feeDraftDobj != null) {
                feeDraftDobj.setAppl_no(pmt_fee_dobj.getAppl_no());
                feeDraftDobj.setRcpt_no(rcpt_no);
                inscd = feeDraft_impl.saveDraftDetails(feeDraftDobj, tmgr);
            }
            CommonPermitFeeImpl.PaymentGenInfo payInfo = pmt_fee_impl.getPaymentInfo(pmt_fee_dobj, feeDraftDobj);
            pmt_fee_impl.saveRecptInstMap(inscd, pmt_fee_dobj.getAppl_no(), rcpt_no, payInfo, pmt_fee_dobj, tmgr);
            pmt_fee_impl.insert_into_vt_fee(pmt_fee_dobj, tmgr);
            ServerUtil.insertForQRDetails(pmt_fee_dobj.getAppl_no(), null, null, rcpt_no, false, DocumentType.RECEIPT_QR, Util.getUserStateCode(), Util.getUserOffCode(), tmgr);
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
            status.setStatus(TableConstants.STATUS_COMPLETE);
            status.setAppl_no(pmt_fee_dobj.getAppl_no());
            status.setOffice_remark("Fee Submitted");
            status.setPublic_remark("");
            status.setState_cd(Util.getUserStateCode());
            //status.setPur_cd(TableConstants.VM_PMT_APPLICATION_PUR_CD); // Done By Naman For testing Purpose
            status.setPur_cd(pmt_fee_dobj.getPur_cd()); // Done By Naman For testing Purpose
            ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, pmt_fee_dobj.getAppl_no(),
                    Util.getSelectedSeat().getAction_cd(), pmt_fee_dobj.getPur_cd(), null, tmgr);
            ServerUtil.fileFlow(tmgr, status);
            tmgr.commit();
        } catch (VahanException e) {
            rcpt_no = "";
            throw new VahanException(e.getMessage());
        } catch (Exception e) {
            rcpt_no = "";
            LOGGER.error(e.toString() + " Appl No : " + pmt_fee_dobj.getAppl_no() + e.getStackTrace()[0]);
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
        return rcpt_no;
    }
}
