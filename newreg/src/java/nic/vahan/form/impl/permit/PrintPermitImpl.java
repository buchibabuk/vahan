/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class PrintPermitImpl {

    private static final Logger LOGGER = Logger.getLogger(PrintPermitImpl.class);

    public List<PrintPermitShowDetails> getPermitPrintRow(int purCd, int permitType, String documentId) {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        List<PrintPermitShowDetails> list = new ArrayList<>();
        String query;
        try {
            tmgr = new TransactionManagerReadOnly("Get Permit print Row");
            list = getAdminstartorPermitPrint(tmgr, purCd, permitType, documentId, list);
            if (documentId.equalsIgnoreCase("9")) {
                query = "SELECT row_number() over() as srl_no,* from ("
                        + "(select a.appl_no,b.pur_cd, to_char(c.op_dt,'dd-MON-yyyy') as appl_dt, b.regn_no, b.pmt_no, c.office_remark, c.public_remark,c.op_dt\n"
                        + "from " + TableList.VA_PERMIT_PRINT + " a \n"
                        + "inner join " + TableList.VT_PERMIT_TRANSACTION + " b on a.appl_no = b.appl_no\n"
                        + "inner join " + TableList.VHA_STATUS + " c on c.appl_no = b.appl_no\n"
                        + "where a.doc_id = ? AND b.state_cd = ? AND b.off_cd = ? AND trans_pur_cd in (?,?,?,?,?) AND c.action_cd in (?,?,?,?,?) order by c.op_dt DESC)\n"
                        + "union\n"
                        + "(select a.appl_no,b.pur_cd, to_char(c.op_dt,'dd-MON-yyyy') as appl_dt, b.regn_no, b.pmt_no, c.office_remark, c.public_remark,c.op_dt\n"
                        + "from " + TableList.VA_PERMIT_PRINT + " a \n"
                        + "inner join " + TableList.VHA_PERMIT_TRANSACTION + " b on a.appl_no = b.appl_no\n"
                        + "inner join " + TableList.VHA_STATUS + " c on c.appl_no = b.appl_no\n"
                        + "where a.doc_id = ? AND b.state_cd = ? AND b.off_cd = ? AND trans_pur_cd in (?,?) AND c.action_cd in (?,?) order by c.op_dt DESC)"
                        + ") vahan order by op_dt DESC";
                ps = tmgr.prepareStatement(query);
                int i = 1;
                ps.setString(i++, documentId);
                ps.setString(i++, Util.getUserStateCode());
                ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
                ps.setInt(i++, TableConstants.VM_PMT_TRANSFER_PUR_CD);
                ps.setInt(i++, TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD);
                ps.setInt(i++, TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD);
                ps.setInt(i++, TableConstants.VM_PMT_REPLACE_VEH_PUR_CD);
                ps.setInt(i++, TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD);
                ps.setInt(i++, TableConstants.TM_ROLE_PMT_SURRENDER_TRANSFER_APPROVE);
                ps.setInt(i++, TableConstants.TM_ROLE_PMT_SURRENDER_TRANSFER_IN_DEATH_APPROVE);
                ps.setInt(i++, TableConstants.TM_ROLE_PMT_REPLACE_VEH_APPROVAL);
                ps.setInt(i++, TableConstants.TM_ROLE_PMT_TO_REPLACE_APPROVAL);
                ps.setInt(i++, TableConstants.TM_ROLE_PMT_SURRENDER_APPROVAL);
                ps.setString(i++, documentId);
                ps.setString(i++, Util.getUserStateCode());
                ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
                ps.setInt(i++, TableConstants.VM_PMT_CANCELATION_PUR_CD);
                ps.setInt(i++, TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD);
                ps.setInt(i++, TableConstants.TM_ROLE_PMT_PERMANENT_SURRENDER_APPROVAL);
                ps.setInt(i++, TableConstants.TM_ROLE_PMT_CANCEL_APPROVAL);
            } else if (purCd == TableConstants.VM_PMT_APPLICATION_PUR_CD) {
                Map<String, String> confige = CommonPermitPrintImpl.getVmPermitStateConfiguration(Util.getUserStateCode());
                if ((String.valueOf(confige.get("genrate_ol_appl")).equalsIgnoreCase("true"))) {
                    query = "Select row_number() over() as srl_no,a.appl_no,a.pur_cd, to_char(b.op_dt,'dd-MON-yyyy') as appl_dt, a.regn_no, a.offer_no as pmt_no, c.office_remark, c.public_remark\n"
                            + " from " + TableList.VA_PERMIT + "   a\n"
                            + " left outer join " + TableList.VA_STATUS + "  b on b.appl_no = a.appl_no\n"
                            + " left outer join " + TableList.VHA_STATUS + "  c on c.appl_no = a.appl_no and c.pur_cd = b.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                            + " inner join " + TableList.VA_PERMIT_PRINT + "  d on d.appl_no = a.appl_no \n"
                            + " where a.state_cd = ? AND a.off_cd = ? AND b.pur_cd in (?,?) AND d.doc_id = ? AND d.regn_no = ? ";
                    ps = tmgr.prepareStatement(query);
                    int i = 1;
                    ps.setString(i++, Util.getUserStateCode());
                    ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
                    ps.setInt(i++, TableConstants.VM_PMT_APPLICATION_PUR_CD);
                    ps.setInt(i++, TableConstants.VM_PMT_FRESH_PUR_CD);
                    ps.setString(i++, documentId);
                    ps.setString(i++, "NEW");
                } else {
                    query = "SELECT * from " + TableList.TM_PURPOSE_ACTION_FLOW + " where pur_cd = ? AND state_cd = ?";
                    ps = tmgr.prepareStatement(query);
                    ps.setInt(1, TableConstants.VM_PMT_APPLICATION_PUR_CD);
                    ps.setString(2, Util.getUserStateCode());
                    RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
                    if (rs1.next()) {
                        query = "Select row_number() over() as srl_no,a.appl_no,a.pur_cd, to_char(b.op_dt,'dd-MON-yyyy') as appl_dt, a.regn_no, a.offer_no as pmt_no, c.office_remark, c.public_remark\n"
                                + " from " + TableList.VA_PERMIT + "  a\n"
                                + " left outer join " + TableList.VA_STATUS + " b on b.appl_no = a.appl_no\n"
                                + " left outer join " + TableList.VHA_STATUS + " c on c.appl_no = a.appl_no and c.pur_cd = b.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                                + " inner join " + TableList.VA_PERMIT_PRINT + " d on d.appl_no = a.appl_no \n"
                                + " where b.action_cd = ? AND a.state_cd = ? AND b.off_cd = ?";
                        ps = tmgr.prepareStatement(query);
                        ps.setInt(1, TableConstants.TM_PMT_APPL_PRINT);
                        ps.setString(2, Util.getUserStateCode());
                        ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                    } else {
                        query = "Select row_number() over() as srl_no,a.appl_no,a.pur_cd, to_char(b.op_dt,'dd-MON-yyyy') as appl_dt, 'NEW' as regn_no, a.offer_no as pmt_no, c.office_remark, c.public_remark\n"
                                + " from " + TableList.VA_PERMIT + "  a\n"
                                + " left outer join " + TableList.VA_STATUS + " b on b.appl_no = a.appl_no\n"
                                + " left outer join " + TableList.VHA_STATUS + " c on c.appl_no = a.appl_no and c.pur_cd = b.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                                + " inner join " + TableList.VA_PERMIT_PRINT + " d on d.appl_no = a.appl_no \n"
                                + " where b.action_cd in (?,?) AND a.state_cd = ? AND b.off_cd = ?";
                        ps = tmgr.prepareStatement(query);
                        ps.setInt(1, TableConstants.TM_ROLE_PMT_VERIFICATION);
                        ps.setInt(2, TableConstants.TM_ROLE_PMT_OFFER_VERIFICATION);
                        ps.setString(3, Util.getUserStateCode());
                        ps.setInt(4, Util.getSelectedSeat().getOff_cd());
                    }
                }
            } else if (purCd == TableConstants.VM_PMT_FRESH_PUR_CD) {
                query = "Select row_number() over() as srl_no,a.appl_no,a.pur_cd,d.doc_id, to_char(b.op_dt,'dd-MON-yyyy') as appl_dt, a.regn_no, a.pmt_no, c.office_remark, c.public_remark\n"
                        + "from " + TableList.VT_PERMIT + "  a\n"
                        + "left outer join " + TableList.VA_STATUS + " b on b.appl_no = a.appl_no \n"
                        + "left outer join " + TableList.VHA_STATUS + " c on c.appl_no = a.appl_no and c.pur_cd = b.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                        + "inner join " + TableList.VA_PERMIT_PRINT + " d on d.appl_no = a.appl_no AND d.doc_id = ?\n"
                        + "where b.action_cd = ? AND a.pmt_type = ? AND a.state_cd = ? AND b.off_cd = ?";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, documentId);
                ps.setInt(2, TableConstants.TM_PMT_FRESH_PRINT);
                ps.setInt(3, permitType);
                ps.setString(4, Util.getUserStateCode());
                ps.setInt(5, Util.getSelectedSeat().getOff_cd());

            } else if (purCd == TableConstants.VM_PMT_RENEWAL_PUR_CD) {
                query = "Select row_number() over() as srl_no,a.appl_no,a.pur_cd,d.doc_id, to_char(b.op_dt,'dd-MON-yyyy') as appl_dt, a.regn_no, a.pmt_no, c.office_remark, c.public_remark\n"
                        + "from " + TableList.VT_PERMIT + "  a\n"
                        + "left outer join " + TableList.VA_STATUS + " b on b.appl_no = a.appl_no \n"
                        + "left outer join " + TableList.VHA_STATUS + " c on c.appl_no = a.appl_no and c.pur_cd = b.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                        + "inner join " + TableList.VA_PERMIT_PRINT + " d on d.appl_no = a.appl_no AND d.doc_id = ?\n"
                        + "where b.action_cd = ? AND a.pmt_type = ? AND a.state_cd = ? AND b.off_cd = ?";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, documentId);
                ps.setInt(2, TableConstants.TM_PMT_RENEWAL_PRINT);
                ps.setInt(3, permitType);
                ps.setString(4, Util.getUserStateCode());
                ps.setInt(5, Util.getSelectedSeat().getOff_cd());
            } else if (purCd == TableConstants.VM_PMT_TEMP_PUR_CD) {
                query = "Select row_number() over() as srl_no,a.appl_no, to_char(b.op_dt,'dd-MON-yyyy') as appl_dt, a.regn_no, a.pmt_no, c.office_remark, c.public_remark\n"
                        + "from " + TableList.VT_TEMP_PERMIT + "  a\n"
                        + "left outer join " + TableList.VA_STATUS + " b on b.appl_no = a.appl_no \n"
                        + "left outer join " + TableList.VHA_STATUS + " c on c.appl_no = a.appl_no and c.pur_cd = b.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                        + "inner join " + TableList.VA_PERMIT_PRINT + " d on d.appl_no = a.appl_no\n"
                        + "where b.action_cd = ? AND a.state_cd = ? AND b.off_cd = ?";
                ps = tmgr.prepareStatement(query);
                ps.setInt(1, TableConstants.TM_PMT_TEMP_PRINT);
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            } else if (purCd == TableConstants.VM_PMT_SPECIAL_PUR_CD) {
                query = "Select row_number() over() as srl_no,a.appl_no, to_char(b.op_dt,'dd-MON-yyyy') as appl_dt, a.regn_no, a.pmt_no, c.office_remark, c.public_remark\n"
                        + "from " + TableList.VT_TEMP_PERMIT + "  a\n"
                        + "left outer join " + TableList.VA_STATUS + " b on b.appl_no = a.appl_no \n"
                        + "left outer join " + TableList.VHA_STATUS + " c on c.appl_no = a.appl_no and c.pur_cd = b.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                        + "inner join " + TableList.VA_PERMIT_PRINT + " d on d.appl_no = a.appl_no\n"
                        + "where b.action_cd = ? AND a.state_cd = ? AND b.off_cd = ?";
                ps = tmgr.prepareStatement(query);
                ps.setInt(1, TableConstants.TM_PMT_SPECIAL_PRINT);
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            } else if (purCd == TableConstants.VM_PMT_DUPLICATE_PUR_CD) {
                query = "SELECT row_number() over() as srl_no,a.appl_no,to_char(b.op_dt,'dd-MON-yyyy') as appl_dt, e.regn_no, e.pmt_no, d.office_remark, d.public_remark FROM " + TableList.VA_PERMIT_PRINT + " a\n"
                        + "INNER JOIN " + TableList.VA_STATUS + " b on b.PUR_CD = ? AND b.action_cd = ? AND a.appl_no = b.appl_no\n"
                        + "INNER JOIN " + TableList.VA_DETAILS + " c on  c.appl_no = b.appl_no\n"
                        + "inner join " + TableList.VHA_STATUS + "  d on  d.appl_no = b.appl_no and d.pur_cd = b.pur_cd and d.file_movement_slno = b.file_movement_slno - 1 \n"
                        + "INNER JOIN " + TableList.VT_PERMIT + " e on e.regn_no = c.regn_no AND e.STATE_CD = ? AND e.pmt_type=?\n"
                        + "WHERE doc_id = ?";
                ps = tmgr.prepareStatement(query);
                ps.setInt(1, TableConstants.VM_PMT_DUPLICATE_PUR_CD);
                ps.setInt(2, TableConstants.TM_PMT_DUPLICATE_PRINT);
                ps.setString(3, Util.getUserStateCode());
                ps.setInt(4, permitType);
                ps.setString(5, documentId);
            } else if (purCd == TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD) {
                query = "select row_number() over() as srl_no,PMT.APPL_NO,to_char(STA.op_dt,'dd-MON-yyyy') as appl_dt,AUTH.REGN_NO,PMT.pmt_no, VHASTA.office_remark, VHASTA.public_remark \n"
                        + "from " + TableList.VA_PERMIT_PRINT + " PRI\n"
                        + "INNER JOIN " + TableList.VT_PERMIT_HOME_AUTH + " AUTH  ON  AUTH.REGN_NO = PRI.REGN_NO AND AUTH.PUR_CD = ? \n"
                        + "INNER JOIN " + TableList.VT_PERMIT + " PMT ON  PMT.REGN_NO = AUTH.REGN_NO\n"
                        + "INNER JOIN " + TableList.VA_STATUS + " STA ON STA.appl_no = PMT.appl_no\n"
                        + "inner join " + TableList.VHA_STATUS + " VHASTA on  VHASTA.appl_no = PMT.appl_no and VHASTA.pur_cd = PMT.pur_cd and VHASTA.file_movement_slno = STA.file_movement_slno - 1 \n"
                        + "where doc_id = ? AND PMT.state_cd = ? and STA.off_cd = ?";
                ps = tmgr.prepareStatement(query);
                ps.setInt(1, TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD);
                ps.setString(2, documentId);
                ps.setString(3, Util.getUserStateCode());
                ps.setInt(4, Util.getSelectedSeat().getOff_cd());
            } else if (purCd == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD) {
                query = "select row_number() over() as srl_no,VHA_AUTH.APPL_NO,to_char(STA.op_dt,'dd-MON-yyyy') as appl_dt,AUTH.REGN_NO,PMT.pmt_no, VHASTA.office_remark, VHASTA.public_remark \n"
                        + "from " + TableList.VA_PERMIT_PRINT + " PRI\n"
                        + "INNER JOIN " + TableList.VT_PERMIT_HOME_AUTH + "  AUTH  ON  AUTH.REGN_NO = PRI.REGN_NO AND AUTH.PUR_CD =? \n"
                        + "INNER JOIN " + TableList.VHA_PERMIT_HOME_AUTH + "  VHA_AUTH  ON  VHA_AUTH.REGN_NO = PRI.REGN_NO \n"
                        + "INNER JOIN " + TableList.VT_PERMIT + "  PMT ON  PMT.REGN_NO = AUTH.REGN_NO\n"
                        + "INNER JOIN " + TableList.VA_STATUS + "  STA ON STA.appl_no = VHA_AUTH.appl_no\n"
                        + "inner join " + TableList.VHA_STATUS + "  VHASTA on  VHASTA.appl_no = VHA_AUTH.appl_no and VHASTA.pur_cd = AUTH.pur_cd and VHASTA.file_movement_slno = STA.file_movement_slno - 1\n"
                        + "where doc_id = ? AND PMT.state_cd = ? and STA.off_cd = ?";
                ps = tmgr.prepareStatement(query);
                ps.setInt(1, TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD);
                ps.setString(2, documentId);
                ps.setString(3, Util.getUserStateCode());
                ps.setInt(4, Util.getSelectedSeat().getOff_cd());
            } else if (purCd == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD) {
                if (documentId.equalsIgnoreCase("8")) {
                    query = "select row_number() over() as srl_no,a.appl_no,to_char(b.op_dt,'dd-MON-yyyy') as appl_dt, a.regn_no, c.pmt_no, b.office_remark, b.public_remark from " + TableList.VA_PERMIT_PRINT + " a\n"
                            + "INNER JOIN " + TableList.VHA_STATUS + " b on b.appl_no = a.appl_no \n"
                            + "INNER JOIN " + TableList.VT_PERMIT_TRANSACTION + " c on c.appl_no = a.appl_no \n"
                            + "where b.state_cd= ? AND a.doc_id = ? AND b.pur_cd= ? AND b.off_cd = ? order by moved_on DESC limit 1 ";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, Util.getUserStateCode());
                    ps.setString(2, documentId);
                    ps.setInt(3, TableConstants.VM_PMT_SURRENDER_PUR_CD);
                    ps.setInt(4, Util.getSelectedSeat().getOff_cd());
                } else {
                    query = "SELECT row_number() over() as srl_no,trn.appl_no,to_char(sta.op_dt,'dd-MON-yyyy') as appl_dt, pmt.regn_no, pmt.pmt_no, d.office_remark, d.public_remark FROM " + TableList.VA_STATUS + " sta\n"
                            + "INNER JOIN " + TableList.VHA_PERMIT_TRANSACTION + " trn on trn.appl_no = sta.appl_no\n"
                            + "INNER JOIN " + TableList.VT_PERMIT + " pmt on pmt.regn_no = trn.new_regn_no AND pmt_type = ?\n"
                            + "INNER JOIN " + TableList.VA_DETAILS + "  c on  c.appl_no = trn.appl_no\n"
                            + "INNER JOIN " + TableList.VHA_STATUS + "  d on  d.appl_no = trn.appl_no and d.file_movement_slno = sta.file_movement_slno - 1 \n"
                            + "INNER JOIN " + TableList.VA_PERMIT_PRINT + " e on e.appl_no = trn.appl_no AND doc_id = ?\n"
                            + "WHERE sta.action_cd = ? AND sta.STATE_CD = ? AND sta.off_cd = ?";
                    ps = tmgr.prepareStatement(query);
                    ps.setInt(1, permitType);
                    ps.setString(2, documentId);
                    ps.setInt(3, TableConstants.TM_PMT_REPLACE_VEHICAL_PRINT);
                    ps.setString(4, Util.getUserStateCode());
                    ps.setInt(5, Util.getSelectedSeat().getOff_cd());
                }
            } else if (purCd == TableConstants.VM_PMT_TRANSFER_PUR_CD
                    || purCd == TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD
                    || purCd == TableConstants.VM_PMT_CA_PUR_CD) {
                if (documentId.equalsIgnoreCase("8")) {
                    query = "select row_number() over() as srl_no,a.appl_no,to_char(b.op_dt,'dd-MON-yyyy') as appl_dt, a.regn_no, c.pmt_no, b.office_remark, b.public_remark from " + TableList.VA_PERMIT_PRINT + " a\n"
                            + "INNER JOIN " + TableList.VHA_STATUS + " b on b.appl_no = a.appl_no \n"
                            + "INNER JOIN " + TableList.VT_PERMIT_TRANSACTION + " c on c.appl_no = a.appl_no \n"
                            + "where b.state_cd= ? AND a.doc_id = ? AND b.pur_cd= ? AND b.off_cd = ? order by moved_on DESC limit 1 ";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, Util.getUserStateCode());
                    ps.setString(2, documentId);
                    ps.setInt(3, purCd);
                    ps.setInt(4, Util.getSelectedSeat().getOff_cd());
                } else {
                    query = "SELECT row_number() over() as srl_no,trn.appl_no,to_char(sta.op_dt,'dd-MON-yyyy') as appl_dt, trn.regn_no, trn.pmt_no, d.office_remark, d.public_remark from " + TableList.VA_PERMIT_PRINT + " print\n"
                            + "INNER JOIN " + TableList.VHA_PERMIT_TRANSACTION + "  trn on trn.appl_no = print.appl_no \n"
                            + "INNER JOIN " + TableList.VT_PERMIT + " PMT on PMT.regn_no = trn.regn_no\n"
                            + "INNER JOIN " + TableList.VA_STATUS + " sta on trn.appl_no = sta.appl_no \n"
                            + "INNER JOIN " + TableList.VA_DETAILS + " c on  c.appl_no = trn.appl_no\n"
                            + "INNER JOIN " + TableList.VHA_STATUS + " d on  d.appl_no = trn.appl_no and d.file_movement_slno = sta.file_movement_slno - 1 \n"
                            + "WHERE sta.STATE_CD = ? AND print.doc_id = ? AND trans_pur_cd = ? AND PMT.pmt_type = ? AND sta.off_cd = ? ";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, Util.getUserStateCode());
                    ps.setString(2, documentId);
                    ps.setInt(3, purCd);
                    ps.setInt(4, permitType);
                    ps.setInt(5, Util.getSelectedSeat().getOff_cd());
                }
            } else if (purCd == TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD) {
                query = "Select row_number() over() as srl_no,a.appl_no,a.pur_cd,d.doc_id, to_char(b.op_dt,'dd-MON-yyyy') as appl_dt, a.regn_no, a.pmt_no, c.office_remark, c.public_remark\n"
                        + "from " + TableList.VT_PERMIT + "  a\n"
                        + "left outer join " + TableList.VA_STATUS + " b on b.appl_no = a.appl_no \n"
                        + "left outer join " + TableList.VHA_STATUS + " c on c.appl_no = a.appl_no and c.pur_cd = b.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                        + "inner join " + TableList.VA_PERMIT_PRINT + " d on d.appl_no = a.appl_no AND d.doc_id = ?\n"
                        + "where b.action_cd = ? AND a.pmt_type = ? AND a.state_cd = ? AND b.off_cd = ?";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, documentId);
                ps.setInt(2, TableConstants.TM_PMT_VARIATION_ENDORSEMENT_PRINT);
                ps.setInt(3, permitType);
                ps.setString(4, Util.getUserStateCode());
                ps.setInt(5, Util.getSelectedSeat().getOff_cd());
            } else if (purCd == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD) {
                if (documentId.equalsIgnoreCase("8")) {
                    query = "select row_number() over() as srl_no,a.appl_no,to_char(b.op_dt,'dd-MON-yyyy') as appl_dt, a.regn_no, c.pmt_no, b.office_remark, b.public_remark from " + TableList.VA_PERMIT_PRINT + " a\n"
                            + "INNER JOIN " + TableList.VHA_STATUS + " b on b.appl_no = a.appl_no \n"
                            + "INNER JOIN " + TableList.VT_PERMIT_TRANSACTION + " c on c.appl_no = a.appl_no \n"
                            + "where b.state_cd= ? AND a.doc_id = ? AND b.pur_cd= ? AND b.off_cd = ? order by moved_on DESC limit 1 ";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, Util.getUserStateCode());
                    ps.setString(2, documentId);
                    ps.setInt(3, TableConstants.VM_PMT_SURRENDER_PUR_CD);
                    ps.setInt(4, Util.getSelectedSeat().getOff_cd());
                } else {
                    query = "SELECT row_number() over() as srl_no,trn.appl_no,to_char(sta.op_dt,'dd-MON-yyyy') as appl_dt, pmt.regn_no, pmt.pmt_no, d.office_remark, d.public_remark FROM " + TableList.VA_STATUS + " sta\n"
                            + "INNER JOIN " + TableList.VHA_PERMIT_TRANSACTION + " trn on trn.appl_no = sta.appl_no\n"
                            + "INNER JOIN " + TableList.VT_PERMIT + " pmt on pmt.regn_no = trn.new_regn_no AND pmt_type = ?\n"
                            + "INNER JOIN " + TableList.VA_DETAILS + "  c on  c.appl_no = trn.appl_no\n"
                            + "INNER JOIN " + TableList.VHA_STATUS + "  d on  d.appl_no = trn.appl_no and d.file_movement_slno = sta.file_movement_slno - 1 \n"
                            + "INNER JOIN " + TableList.VA_PERMIT_PRINT + " e on e.appl_no = trn.appl_no AND doc_id = ?\n"
                            + "WHERE sta.action_cd = ? AND sta.STATE_CD = ? AND sta.off_cd = ?";
                    ps = tmgr.prepareStatement(query);
                    ps.setInt(1, permitType);
                    ps.setString(2, documentId);
                    ps.setInt(3, TableConstants.TM_PMT_TO_REPLACE_PRINT);
                    ps.setString(4, Util.getUserStateCode());
                    ps.setInt(5, Util.getSelectedSeat().getOff_cd());
                }
            } else if (purCd == TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD) {
                query = "Select row_number() over() as srl_no,a.appl_no,a.pur_cd,d.doc_id, to_char(b.op_dt,'dd-MON-yyyy') as appl_dt, a.regn_no, a.pmt_no, c.office_remark, c.public_remark\n"
                        + "from PERMIT.VT_PERMIT_COUNTERSIGNATURE  a\n"
                        + "left outer join VA_STATUS  b on b.appl_no = a.appl_no \n"
                        + "left outer join VHA_STATUS  c on c.appl_no = a.appl_no and c.pur_cd = b.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                        + "inner join PERMIT.VA_PERMIT_PRINT d on d.appl_no = a.appl_no AND d.doc_id = ?\n"
                        + "where b.action_cd = ? AND a.state_cd = ? AND b.off_cd = ?";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, documentId);
                ps.setInt(2, TableConstants.TM_PMT_COUNTER_SIGNATURE_PRINT);
                ps.setString(3, Util.getUserStateCode());
                ps.setInt(4, Util.getSelectedSeat().getOff_cd());
            } else if (purCd == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD) {
                query = "SELECT row_number() over() as srl_no,trn.appl_no,to_char(sta.op_dt,'dd-MON-yyyy') as appl_dt, trn.regn_no, trn.pmt_no, d.office_remark, d.public_remark from " + TableList.VA_PERMIT_PRINT + " print\n"
                        + "LEFT OUTER JOIN " + TableList.VHA_PERMIT_TRANSACTION + "  trn on trn.appl_no = print.appl_no \n"
                        + "LEFT OUTER JOIN " + TableList.VT_PERMIT + " PMT on PMT.regn_no = trn.new_regn_no\n"
                        + "LEFT OUTER JOIN " + TableList.VA_STATUS + " sta on trn.appl_no = sta.appl_no \n"
                        + "LEFT OUTER JOIN " + TableList.VA_DETAILS + " c on  c.appl_no = trn.appl_no\n"
                        + "LEFT OUTER JOIN " + TableList.VHA_STATUS + " d on  d.appl_no = trn.appl_no and d.file_movement_slno = sta.file_movement_slno - 1 \n"
                        + "WHERE sta.STATE_CD = ? AND print.doc_id = ? AND trans_pur_cd = ? AND PMT.pmt_type = ? AND sta.off_cd = ? ";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, Util.getUserStateCode());
                ps.setString(2, documentId);
                ps.setInt(3, purCd);
                ps.setInt(4, permitType);
                ps.setInt(5, Util.getSelectedSeat().getOff_cd());
            }
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                list.add(new PrintPermitShowDetails(rs.getString("srl_no"), rs.getString("appl_no"), rs.getString("appl_dt"), rs.getString("regn_no"), rs.getString("pmt_no"), rs.getString("office_remark"), rs.getString("public_remark"), null, null));
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
        return list;
    }
// by manoj

    public List<PrintPermitShowDetails> getPermitPrintByRegn_no(String regn_no) {
        String documentId = "";
        String doc_descr = "";
        String regn_no_va = "";
        String appl_no = "";
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        List<PrintPermitShowDetails> list = new ArrayList<>();
        PrintPermitShowDetails dobj = null;
        String query;
        try {
            tmgr = new TransactionManager("getPermitPrintByRegn_no");
            query = "select vapermit.appl_no as appl_no, vapermit.regn_no as regn_no, vapermit.doc_id as doc_id, vapermit.op_dt,perdoc.descr as doc_id_descr\n"
                    + " from " + TableList.VA_PERMIT_PRINT + " vapermit \n"
                    + " Left Join " + TableList.VM_PERMIT_DOCUMENTS + " perdoc on perdoc.doc_id=vapermit.doc_id\n"
                    + " where vapermit.regn_no=? order by vapermit.doc_id ASC";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                doc_descr = rs.getString("doc_id_descr");
                documentId = rs.getString("doc_id");
                regn_no_va = rs.getString("regn_no");
                appl_no = rs.getString("appl_no");
                if ("1".equals(documentId) || "2".equals(documentId) || "4".equals(documentId)) {
                    query = " (select vt_per.appl_no as appl_no, vt_per.pmt_no as pmt_no, vt_per.regn_no as regn_no,vt_per.pur_cd as pur_cd,vt_per.pmt_type as pmt_type,\n"
                            + " pur_mst.descr as pur_descr,pmt_mst.descr as pmt_descr,c.office_remark, c.public_remark,to_char(b.op_dt,'dd-MON-yyyy') as appl_dt\n "
                            + " from " + TableList.VT_PERMIT + " vt_per\n"
                            + " left JOIN " + TableList.TM_PURPOSE_MAST + " pur_mst ON pur_mst.pur_cd=vt_per.pur_cd\n"
                            + " left JOIN " + TableList.VM_PERMIT_TYPE + " pmt_mst ON pmt_mst.code=vt_per.pmt_type\n"
                            + " left outer join " + TableList.VA_STATUS + " b on b.appl_no = vt_per.appl_no\n"
                            + " left outer join " + TableList.VHA_STATUS + " c on c.appl_no = vt_per.appl_no and c.pur_cd = b.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                            + " where vt_per.appl_no=? and vt_per.regn_no=? and vt_per.state_cd=? and vt_per.off_cd=?)"
                            + " union \n"
                            + " (select d.appl_no as appl_no, vt_per.pmt_no as pmt_no, vt_per.regn_no as regn_no,d.pur_cd as pur_cd,vt_per.pmt_type, \n"
                            + " pur_mst.descr as pur_descr,pmt_mst.descr as pmt_descr,c.office_remark, c.public_remark,to_char(b.op_dt,'dd-MON-yyyy') as appl_dt\n"
                            + " from " + TableList.VT_PERMIT + " vt_per\n"
                            + " inner join " + TableList.VHA_PERMIT_TRANSACTION + " d ON d.regn_no=vt_per.regn_no and d.pmt_no=vt_per.pmt_no \n"
                            + " left JOIN " + TableList.TM_PURPOSE_MAST + " pur_mst ON pur_mst.pur_cd=d.pur_cd\n"
                            + " left JOIN " + TableList.VM_PERMIT_TYPE + " pmt_mst ON pmt_mst.code=vt_per.pmt_type\n"
                            + " left outer join " + TableList.VA_STATUS + " b on b.appl_no = d.appl_no\n"
                            + " left outer join " + TableList.VHA_STATUS + " c on c.appl_no = d.appl_no and c.pur_cd = b.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                            + " where d.appl_no=? and vt_per.regn_no=? and vt_per.state_cd=? and vt_per.off_cd=? ORDER BY d.moved_on DESC limit 1)"
                            + " union \n"
                            + " (select DUP.appl_no as appl_no, vt_per.pmt_no as pmt_no, DUP.REGN_NO as regn_no,DUP.pur_cd, vt_per.pmt_type,\n"
                            + " pur_mst.descr as pur_descr,pmt_mst.descr as pmt_descr,c.office_remark, c.public_remark,to_char(b.op_dt,'dd-MON-yyyy') as appl_dt\n"
                            + " from " + TableList.VT_PERMIT + " vt_per \n"
                            + " INNER JOIN " + TableList.VHA_DUP + " DUP ON DUP.REGN_NO = vt_per.REGN_NO \n"
                            + " LEFT JOIN " + TableList.TM_PURPOSE_MAST + " pur_mst ON pur_mst.pur_cd=DUP.pur_cd \n"
                            + " LEFT JOIN " + TableList.VM_PERMIT_TYPE + " pmt_mst ON pmt_mst.code=vt_per.pmt_type \n"
                            + " LEFT OUTER join " + TableList.VA_STATUS + " b on b.appl_no = DUP.appl_no AND b.PUR_CD=DUP.PUR_CD\n"
                            + " LEFT OUTER join " + TableList.VHA_STATUS + " c on c.appl_no = b.appl_no and c.pur_cd = b.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                            + " where DUP.appl_no=? and DUP.regn_no=? and vt_per.state_cd=? and vt_per.off_cd=? ORDER BY DUP.moved_on DESC limit 1)"
                            + " union \n"
                            + " (select d.appl_no as appl_no, vt_per.pmt_no as pmt_no, vt_per.regn_no as regn_no,d.pur_cd as pur_cd,vt_per.pmt_type, \n"
                            + " pur_mst.descr as pur_descr,pmt_mst.descr as pmt_descr,c.office_remark, c.public_remark,to_char(b.op_dt,'dd-MON-yyyy') as appl_dt\n"
                            + " from " + TableList.VT_PERMIT + " vt_per\n"
                            + " inner join " + TableList.VHA_PERMIT_TRANSACTION + " d ON d.new_regn_no=vt_per.regn_no and d.pmt_no=vt_per.pmt_no \n"
                            + " left JOIN " + TableList.TM_PURPOSE_MAST + " pur_mst ON pur_mst.pur_cd=d.pur_cd \n"
                            + " left JOIN " + TableList.VM_PERMIT_TYPE + " pmt_mst ON pmt_mst.code=vt_per.pmt_type \n"
                            + " left outer join " + TableList.VA_STATUS + " b on b.appl_no = d.appl_no \n"
                            + " left outer join " + TableList.VHA_STATUS + " c on c.appl_no = d.appl_no and c.pur_cd = b.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                            + " where d.appl_no=? and d.regn_no=? and vt_per.state_cd=? and vt_per.off_cd=? ORDER BY d.moved_on DESC limit 1)";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, appl_no);
                    ps.setString(2, regn_no_va);
                    ps.setString(3, Util.getUserStateCode());
                    ps.setInt(4, Util.getSelectedSeat().getOff_cd());
                    ps.setString(5, appl_no);
                    ps.setString(6, regn_no_va);
                    ps.setString(7, Util.getUserStateCode());
                    ps.setInt(8, Util.getSelectedSeat().getOff_cd());
                    ps.setString(9, appl_no);
                    ps.setString(10, regn_no_va);
                    ps.setString(11, Util.getUserStateCode());
                    ps.setInt(12, Util.getSelectedSeat().getOff_cd());
                    ps.setString(13, appl_no);
                    ps.setString(14, regn_no_va);
                    ps.setString(15, Util.getUserStateCode());
                    ps.setInt(16, Util.getSelectedSeat().getOff_cd());
                    RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
                    if (rs1.next()) {
                        dobj = new PrintPermitShowDetails();
                        dobj.setAppl_no(rs1.getString("appl_no"));
                        dobj.setAppl_dt(rs1.getString("appl_dt"));
                        dobj.setRegn_no(rs1.getString("regn_no"));
                        dobj.setDoc_id(documentId);
                        dobj.setDoc_id_descr(doc_descr);
                        dobj.setPmt_no(rs1.getString("pmt_no"));
                        dobj.setPmt_type(rs1.getString("pmt_type"));
                        dobj.setPmt_type_descr(rs1.getString("pmt_descr"));
                        dobj.setPur_cd(rs1.getString("pur_cd"));
                        dobj.setPur_cd_descr(rs1.getString("pur_descr"));
                        dobj.setPublic_remark(rs1.getString("public_remark"));
                        dobj.setOffice_remark(rs1.getString("office_remark"));
                        list.add(dobj);
                    }
                } else if ("3".equals(documentId)) {
                    Map<String, String> confige = CommonPermitPrintImpl.getVmPermitStateConfiguration(Util.getUserStateCode());
                    if ((String.valueOf(confige.get("genrate_ol_appl")).equalsIgnoreCase("true"))) {
                        query = "Select a.appl_no,a.pur_cd, to_char(b.op_dt,'dd-MON-yyyy') as appl_dt, a.regn_no,a.pmt_type,e.descr as pur_cd_descr,f.descr as pmt_type_descr, a.offer_no as pmt_no, c.office_remark, c.public_remark\n"
                                + " from " + TableList.VA_PERMIT + "   a\n"
                                + " left join " + TableList.TM_PURPOSE_MAST + " e ON e.pur_cd=a.pur_cd \n"
                                + " left join " + TableList.VM_PERMIT_TYPE + " f ON f.code=a.pmt_type \n"
                                + " left outer join " + TableList.VA_STATUS + "  b on b.appl_no = a.appl_no\n"
                                + " left outer join " + TableList.VHA_STATUS + "  c on c.appl_no = a.appl_no and c.pur_cd = b.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                                + " inner join " + TableList.VA_PERMIT_PRINT + "  d on d.appl_no = a.appl_no \n"
                                + " where a.state_cd = ? AND a.off_cd = ? AND d.doc_id = ? AND d.regn_no = ? and d.appl_no=?";
                        ps = tmgr.prepareStatement(query);
                        ps.setString(1, Util.getUserStateCode());
                        ps.setInt(2, Util.getSelectedSeat().getOff_cd());
                        ps.setString(3, documentId);
                        ps.setString(4, "NEW");
                        ps.setString(5, appl_no);
                        RowSet rs15 = tmgr.fetchDetachedRowSet_No_release();
                        if (rs15.next()) {
                            dobj = new PrintPermitShowDetails();
                            dobj.setAppl_no(rs15.getString("appl_no"));
                            dobj.setAppl_dt(rs15.getString("appl_dt"));
                            dobj.setRegn_no(rs15.getString("regn_no"));
                            dobj.setDoc_id(documentId);
                            dobj.setDoc_id_descr(doc_descr);
                            dobj.setPmt_no(rs15.getString("pmt_no"));
                            dobj.setPmt_type(rs15.getString("pmt_type"));
                            dobj.setPmt_type_descr(rs15.getString("pmt_type_descr"));
                            dobj.setPur_cd(rs15.getString("pur_cd"));
                            dobj.setPur_cd_descr(rs15.getString("pur_cd_descr"));
                            dobj.setPublic_remark(rs15.getString("public_remark"));
                            dobj.setOffice_remark(rs15.getString("office_remark"));
                            list.add(dobj);
                        }
                    } else {
                        query = "SELECT * from " + TableList.TM_PURPOSE_ACTION_FLOW + " where pur_cd = ? AND state_cd = ?";
                        ps = tmgr.prepareStatement(query);
                        ps.setInt(1, TableConstants.VM_PMT_APPLICATION_PUR_CD);
                        ps.setString(2, Util.getUserStateCode());
                        RowSet rs18 = tmgr.fetchDetachedRowSet_No_release();
                        if (rs18.next()) {
                            query = "Select a.appl_no,b.pur_cd, to_char(b.op_dt,'dd-MON-yyyy') as appl_dt, a.regn_no,a.pmt_type,e.descr as pur_cd_descr,f.descr as pmt_type_descr, a.offer_no as pmt_no, c.office_remark, c.public_remark\n"
                                    + " from " + TableList.VA_PERMIT + "  a\n"
                                    + " left join " + TableList.VM_PERMIT_TYPE + " f ON f.code=a.pmt_type \n"
                                    + " left outer join " + TableList.VA_STATUS + " b on b.appl_no = a.appl_no\n"
                                    + " left outer join " + TableList.VHA_STATUS + " c on c.appl_no = a.appl_no and c.pur_cd = b.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                                    + " inner join " + TableList.VA_PERMIT_PRINT + " d on d.appl_no = a.appl_no \n"
                                    + " left join " + TableList.TM_PURPOSE_MAST + " e ON e.pur_cd=b.pur_cd \n"
                                    + " where b.action_cd = ? AND a.state_cd = ? AND b.off_cd = ? and a.regn_no=?";
                            ps = tmgr.prepareStatement(query);
                            ps.setInt(1, TableConstants.TM_PMT_APPL_PRINT);
                            ps.setString(2, Util.getUserStateCode());
                            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                            ps.setString(4, regn_no_va);
                            RowSet rs16 = tmgr.fetchDetachedRowSet_No_release();
                            if (rs16.next()) {
                                dobj = new PrintPermitShowDetails();
                                dobj.setAppl_no(rs16.getString("appl_no"));
                                dobj.setAppl_dt(rs16.getString("appl_dt"));
                                dobj.setRegn_no(rs16.getString("regn_no"));
                                dobj.setDoc_id(documentId);
                                dobj.setDoc_id_descr(doc_descr);
                                dobj.setPmt_no(rs16.getString("pmt_no"));
                                dobj.setPmt_type(rs16.getString("pmt_type"));
                                dobj.setPmt_type_descr(rs16.getString("pmt_type_descr"));
                                dobj.setPur_cd(rs16.getString("pur_cd"));
                                dobj.setPur_cd_descr(rs16.getString("pur_cd_descr"));
                                dobj.setPublic_remark(rs16.getString("public_remark"));
                                dobj.setOffice_remark(rs16.getString("office_remark"));
                                list.add(dobj);
                            }
                        } else {
                            query = "Select a.appl_no,a.pur_cd, to_char(b.op_dt,'dd-MON-yyyy') as appl_dt, 'NEW' as regn_no,a.pmt_type,e.descr as pur_cd_descr,f.descr as pmt_type_descr, a.offer_no as pmt_no, c.office_remark, c.public_remark\n"
                                    + " from " + TableList.VA_PERMIT + "  a\n"
                                    + " left join " + TableList.TM_PURPOSE_MAST + " e ON e.pur_cd=a.pur_cd \n"
                                    + " left join " + TableList.VM_PERMIT_TYPE + " f ON f.code=a.pmt_type \n"
                                    + " left outer join " + TableList.VA_STATUS + " b on b.appl_no = a.appl_no\n"
                                    + " left outer join " + TableList.VHA_STATUS + " c on c.appl_no = a.appl_no and c.pur_cd = b.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                                    + " inner join " + TableList.VA_PERMIT_PRINT + " d on d.appl_no = a.appl_no \n"
                                    + " where b.action_cd = ? AND a.state_cd = ? AND b.off_cd = ? and d.appl_no=?";
                            ps = tmgr.prepareStatement(query);
                            ps.setInt(1, TableConstants.TM_ROLE_PMT_VERIFICATION);
                            ps.setString(2, Util.getUserStateCode());
                            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                            ps.setString(4, appl_no);
                            RowSet rs17 = tmgr.fetchDetachedRowSet_No_release();
                            if (rs17.next()) {
                                dobj = new PrintPermitShowDetails();
                                dobj.setAppl_no(rs17.getString("appl_no"));
                                dobj.setAppl_dt(rs17.getString("appl_dt"));
                                dobj.setRegn_no(rs17.getString("regn_no"));
                                dobj.setDoc_id(documentId);
                                dobj.setDoc_id_descr(doc_descr);
                                dobj.setPmt_no(rs17.getString("pmt_no"));
                                dobj.setPmt_type(rs17.getString("pmt_type"));
                                dobj.setPmt_type_descr(rs17.getString("pmt_type_descr"));
                                dobj.setPur_cd(rs17.getString("pur_cd"));
                                dobj.setPur_cd_descr(rs17.getString("pur_cd_descr"));
                                dobj.setPublic_remark(rs17.getString("public_remark"));
                                dobj.setOffice_remark(rs17.getString("office_remark"));
                                list.add(dobj);
                            }
                        }
                    }
                } else if ("6".equals(documentId) || "7".equals(documentId)) {
                    query = " select vt_per.appl_no as appl_no, vt_per.pmt_no as pmt_no, vt_per.regn_no as regn_no,vt_per.pur_cd, vt_per.pmt_type,\n"
                            + " pur_mst.descr as pur_descr,pmt_mst.descr as pmt_descr,c.office_remark, c.public_remark,to_char(b.op_dt,'dd-MON-yyyy') as appl_dt\n"
                            + " from " + TableList.VT_TEMP_PERMIT + " vt_per\n"
                            + " LEFT JOIN " + TableList.TM_PURPOSE_MAST + " pur_mst ON pur_mst.pur_cd=vt_per.pur_cd\n"
                            + " LEFT JOIN " + TableList.VM_PERMIT_TYPE + " pmt_mst ON pmt_mst.code=vt_per.pmt_type\n"
                            + " left outer join " + TableList.VA_STATUS + " b on b.appl_no = vt_per.appl_no\n"
                            + " left outer join " + TableList.VHA_STATUS + " c on c.appl_no = b.appl_no and c.pur_cd = b.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                            + " where vt_per.appl_no=? and vt_per.regn_no=? and vt_per.state_cd=? and vt_per.off_cd=?";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, appl_no);
                    ps.setString(2, regn_no_va);
                    ps.setString(3, Util.getUserStateCode());
                    ps.setInt(4, Util.getSelectedSeat().getOff_cd());
                    RowSet rowSet = tmgr.fetchDetachedRowSet_No_release();
                    if (rowSet.next()) {
                        dobj = new PrintPermitShowDetails();
                        dobj.setAppl_no(rowSet.getString("appl_no"));
                        dobj.setAppl_dt(rowSet.getString("appl_dt"));
                        dobj.setRegn_no(rowSet.getString("regn_no"));
                        dobj.setDoc_id(documentId);
                        dobj.setDoc_id_descr(doc_descr);
                        dobj.setPmt_no(rowSet.getString("pmt_no"));
                        dobj.setPmt_type(rowSet.getString("pmt_type"));
                        dobj.setPmt_type_descr(rowSet.getString("pmt_descr"));
                        dobj.setPur_cd(rowSet.getString("pur_cd"));
                        dobj.setPur_cd_descr(rowSet.getString("pur_descr"));
                        dobj.setPublic_remark(rowSet.getString("public_remark"));
                        dobj.setOffice_remark(rowSet.getString("office_remark"));
                        list.add(dobj);

                    }
                } else if ("5".equals(documentId) || "12".equals(documentId)) {
                    query = " (select vt_per.appl_no as appl_no, vt_per.pmt_no as pmt_no, AUTH.REGN_NO as regn_no,AUTH.pur_cd, vt_per.pmt_type,\n"
                            + " pur_mst.descr as pur_descr,pmt_mst.descr as pmt_descr,c.office_remark, c.public_remark,to_char(b.op_dt,'dd-MON-yyyy') as appl_dt \n"
                            + " from " + TableList.VT_PERMIT + " vt_per \n"
                            + " INNER JOIN " + TableList.VT_PERMIT_HOME_AUTH + " AUTH ON AUTH.REGN_NO = vt_per.REGN_NO and AUTH.pmt_no = vt_per.pmt_no \n"
                            + " LEFT JOIN " + TableList.TM_PURPOSE_MAST + " pur_mst ON pur_mst.pur_cd=AUTH.pur_cd \n"
                            + " LEFT JOIN " + TableList.VM_PERMIT_TYPE + " pmt_mst ON pmt_mst.code=vt_per.pmt_type \n"
                            + " left outer join " + TableList.VA_STATUS + " b on b.appl_no = vt_per.appl_no \n"
                            + " left outer join " + TableList.VHA_STATUS + " c on c.appl_no = vt_per.appl_no and c.pur_cd = vt_per.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                            + " where vt_per.appl_no=? and AUTH.regn_no=? and vt_per.state_cd=? and vt_per.off_cd=?)"
                            + " union \n"
                            + "( select dup.appl_no as appl_no, vt_per.pmt_no as pmt_no, AUTH.REGN_NO as regn_no,AUTH.pur_cd, vt_per.pmt_type,\n"
                            + " pur_mst.descr as pur_descr,pmt_mst.descr as pmt_descr,c.office_remark, c.public_remark,to_char(b.op_dt,'dd-MON-yyyy') as appl_dt \n"
                            + " from " + TableList.VT_PERMIT + " vt_per \n"
                            + " INNER JOIN " + TableList.VT_PERMIT_HOME_AUTH + " AUTH ON AUTH.REGN_NO = vt_per.REGN_NO and AUTH.pmt_no = vt_per.pmt_no \n"
                            + " left outer join " + TableList.VHA_DUP + " dup on dup.regn_no = vt_per.regn_no and dup.state_cd=vt_per.state_cd \n"
                            + " LEFT JOIN " + TableList.TM_PURPOSE_MAST + " pur_mst ON pur_mst.pur_cd=AUTH.pur_cd \n"
                            + " LEFT JOIN " + TableList.VM_PERMIT_TYPE + " pmt_mst ON pmt_mst.code=vt_per.pmt_type \n"
                            + " left outer join " + TableList.VA_STATUS + " b on b.appl_no = dup.appl_no \n"
                            + " left outer join " + TableList.VHA_STATUS + " c on c.appl_no = b.appl_no and c.pur_cd = b.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                            + " where dup.appl_no=? and AUTH.regn_no=? and dup.state_cd=? and dup.off_cd=? ORDER BY dup.moved_on DESC limit 1)"
                            + " union \n"
                            + " (select d.appl_no as appl_no, vt_per.pmt_no as pmt_no, AUTH.REGN_NO as regn_no,AUTH.pur_cd, vt_per.pmt_type,\n"
                            + " pur_mst.descr as pur_descr,pmt_mst.descr as pmt_descr,c.office_remark, c.public_remark,to_char(b.op_dt,'dd-MON-yyyy') as appl_dt \n"
                            + " from " + TableList.VT_PERMIT + " vt_per \n"
                            + " INNER JOIN " + TableList.VT_PERMIT_HOME_AUTH + " AUTH ON AUTH.REGN_NO = vt_per.REGN_NO and AUTH.pmt_no = vt_per.pmt_no\n"
                            + " inner join " + TableList.VHA_PERMIT_TRANSACTION + "  d ON d.regn_no=vt_per.regn_no and d.pmt_no=vt_per.pmt_no  \n"
                            + " LEFT JOIN " + TableList.TM_PURPOSE_MAST + " pur_mst ON pur_mst.pur_cd=AUTH.pur_cd \n"
                            + " LEFT JOIN " + TableList.VM_PERMIT_TYPE + " pmt_mst ON pmt_mst.code=vt_per.pmt_type \n"
                            + " left outer join " + TableList.VA_STATUS + " b on b.appl_no = d.appl_no \n"
                            + " left outer join " + TableList.VHA_STATUS + " c on c.appl_no = d.appl_no and c.pur_cd = d.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                            + " where d.appl_no=?  and AUTH.regn_no=? and vt_per.state_cd=? and vt_per.off_cd=? ORDER BY d.moved_on DESC limit 1)"
                            + " union \n"
                            + " (select d.appl_no as appl_no, vt_per.pmt_no as pmt_no, AUTH.REGN_NO as regn_no,AUTH.pur_cd, vt_per.pmt_type,\n"
                            + " pur_mst.descr as pur_descr,pmt_mst.descr as pmt_descr,c.office_remark, c.public_remark,to_char(b.op_dt,'dd-MON-yyyy') as appl_dt \n"
                            + " from " + TableList.VT_PERMIT + " vt_per \n"
                            + " inner join " + TableList.VHA_PERMIT_TRANSACTION + "  d ON d.new_regn_no=vt_per.regn_no and d.pmt_no=vt_per.pmt_no \n"
                            + " INNER JOIN " + TableList.VT_PERMIT_HOME_AUTH + " AUTH ON AUTH.REGN_NO = d.new_regn_no and AUTH.pmt_no = vt_per.pmt_no\n"
                            + " LEFT JOIN " + TableList.TM_PURPOSE_MAST + " pur_mst ON pur_mst.pur_cd=AUTH.pur_cd \n"
                            + " LEFT JOIN " + TableList.VM_PERMIT_TYPE + " pmt_mst ON pmt_mst.code=vt_per.pmt_type \n"
                            + " left outer join " + TableList.VA_STATUS + " b on b.appl_no = d.appl_no \n"
                            + " left outer join " + TableList.VHA_STATUS + " c on c.appl_no = d.appl_no and c.pur_cd = d.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                            + " where d.appl_no=?  and d.regn_no=? and vt_per.state_cd=? and vt_per.off_cd=? ORDER BY d.moved_on DESC limit 1)";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, appl_no);
                    ps.setString(2, regn_no_va);
                    ps.setString(3, Util.getUserStateCode());
                    ps.setInt(4, Util.getSelectedSeat().getOff_cd());
                    ps.setString(5, appl_no);
                    ps.setString(6, regn_no_va);
                    ps.setString(7, Util.getUserStateCode());
                    ps.setInt(8, Util.getSelectedSeat().getOff_cd());
                    ps.setString(9, appl_no);
                    ps.setString(10, regn_no_va);
                    ps.setString(11, Util.getUserStateCode());
                    ps.setInt(12, Util.getSelectedSeat().getOff_cd());
                    ps.setString(13, appl_no);
                    ps.setString(14, regn_no_va);
                    ps.setString(15, Util.getUserStateCode());
                    ps.setInt(16, Util.getSelectedSeat().getOff_cd());
                    RowSet rst = tmgr.fetchDetachedRowSet_No_release();
                    if (rst.next()) {
                        dobj = new PrintPermitShowDetails();
                        dobj.setAppl_no(rst.getString("appl_no"));
                        dobj.setAppl_dt(rst.getString("appl_dt"));
                        dobj.setRegn_no(rst.getString("regn_no"));
                        dobj.setDoc_id(documentId);
                        dobj.setDoc_id_descr(doc_descr);
                        dobj.setPmt_no(rst.getString("pmt_no"));
                        dobj.setPmt_type(rst.getString("pmt_type"));
                        dobj.setPmt_type_descr(rst.getString("pmt_descr"));
                        dobj.setPur_cd(rst.getString("pur_cd"));
                        dobj.setPur_cd_descr(rst.getString("pur_descr"));
                        dobj.setPublic_remark(rst.getString("public_remark"));
                        dobj.setOffice_remark(rst.getString("office_remark"));
                        list.add(dobj);

                    } else {
                        query = " (select VHA_AUTH.appl_no as appl_no, AUTH.pmt_no as pmt_no, AUTH.REGN_NO as regn_no,AUTH.pur_cd, vt_per.pmt_type,\n"
                                + " pur_mst.descr as pur_descr,pmt_mst.descr as pmt_descr,c.office_remark, c.public_remark,to_char(b.op_dt,'dd-MON-yyyy') as appl_dt\n"
                                + " from " + TableList.VT_PERMIT + " vt_per\n"
                                + " INNER JOIN " + TableList.VT_PERMIT_HOME_AUTH + " AUTH ON AUTH.REGN_NO = vt_per.REGN_NO \n"
                                + " INNER JOIN " + TableList.VHA_PERMIT_HOME_AUTH + "  VHA_AUTH  ON  VHA_AUTH.REGN_NO = vt_per.REGN_NO\n"
                                + " LEFT JOIN " + TableList.TM_PURPOSE_MAST + " pur_mst ON pur_mst.pur_cd=AUTH.pur_cd\n"
                                + " LEFT JOIN " + TableList.VM_PERMIT_TYPE + " pmt_mst ON pmt_mst.code=vt_per.pmt_type\n"
                                + " left outer join " + TableList.VA_STATUS + " b on b.appl_no = VHA_AUTH.appl_no\n"
                                + " left outer join " + TableList.VHA_STATUS + " c on c.appl_no = VHA_AUTH.appl_no and c.pur_cd = AUTH.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                                + " where VHA_AUTH.appl_no=? and AUTH.regn_no=? and vt_per.state_cd=? and vt_per.off_cd=? ORDER BY VHA_AUTH.moved_on DESC limit 1)"
                                + " union \n"
                                + "      ( select VHA_AUTH.appl_no as appl_no, AUTH.pmt_no as pmt_no, AUTH.REGN_NO as regn_no,AUTH.pur_cd, vt_per.pmt_type, \n"
                                + "       pur_mst.descr as pur_descr,pmt_mst.descr as pmt_descr,c.office_remark, c.public_remark,to_char(b.op_dt,'dd-MON-yyyy') as appl_dt \n"
                                + "       from " + TableList.VT_PERMIT + "  vt_per \n"
                                + "       INNER JOIN " + TableList.VT_PERMIT_HOME_AUTH + "  AUTH ON AUTH.REGN_NO = vt_per.REGN_NO \n"
                                + "       INNER JOIN " + TableList.VHA_PERMIT_HOME_AUTH + "  VHA_AUTH  ON  VHA_AUTH.REGN_NO = vt_per.REGN_NO \n"
                                + "       INNER JOIN " + TableList.VA_DETAILS + " DTL   ON  DTL.REGN_NO = VHA_AUTH.REGN_NO and  DTL.APPL_NO = VHA_AUTH.APPL_NO \n"
                                + "       LEFT JOIN " + TableList.TM_PURPOSE_MAST + "  pur_mst ON pur_mst.pur_cd=AUTH.pur_cd \n"
                                + "       LEFT JOIN " + TableList.VM_PERMIT_TYPE + "  pmt_mst ON pmt_mst.code=vt_per.pmt_type \n"
                                + "       left outer join " + TableList.VA_STATUS + "  b on b.appl_no = VHA_AUTH.appl_no \n"
                                + "       left outer join " + TableList.VHA_STATUS + "  c on c.appl_no = VHA_AUTH.appl_no and c.pur_cd = AUTH.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                                + "       where VHA_AUTH.appl_no=? and AUTH.regn_no=? and vt_per.state_cd=? and DTL.off_cd=? ORDER BY VHA_AUTH.moved_on DESC limit 1)";
                        ps = tmgr.prepareStatement(query);
                        ps.setString(1, appl_no);
                        ps.setString(2, regn_no_va);
                        ps.setString(3, Util.getUserStateCode());
                        ps.setInt(4, Util.getSelectedSeat().getOff_cd());
                        ps.setString(5, appl_no);
                        ps.setString(6, regn_no_va);
                        ps.setString(7, Util.getUserStateCode());
                        ps.setInt(8, Util.getSelectedSeat().getOff_cd());
                        RowSet rs3 = tmgr.fetchDetachedRowSet_No_release();
                        if (rs3.next()) {
                            dobj = new PrintPermitShowDetails();
                            dobj.setAppl_no(rs3.getString("appl_no"));
                            dobj.setAppl_dt(rs3.getString("appl_dt"));
                            dobj.setRegn_no(rs3.getString("regn_no"));
                            dobj.setDoc_id(documentId);
                            dobj.setDoc_id_descr(doc_descr);
                            dobj.setPmt_no(rs3.getString("pmt_no"));
                            dobj.setPmt_type(rs3.getString("pmt_type"));
                            dobj.setPmt_type_descr(rs3.getString("pmt_descr"));
                            dobj.setPur_cd(rs3.getString("pur_cd"));
                            dobj.setPur_cd_descr(rs3.getString("pur_descr"));
                            dobj.setPublic_remark(rs3.getString("public_remark"));
                            dobj.setOffice_remark(rs3.getString("office_remark"));
                            list.add(dobj);

                        }
                    }
                } else if ("10".equals(documentId)) {
                    query = " select vt_per.appl_no as appl_no, vt_per.pmt_no as pmt_no, vt_per.REGN_NO as regn_no,vt_per.pur_cd, vt_per.pmt_type,\n"
                            + " pur_mst.descr as pur_descr,pmt_mst.descr as pmt_descr,c.office_remark, c.public_remark,to_char(b.op_dt,'dd-MON-yyyy') as appl_dt\n"
                            + "  from " + TableList.VT_PERMIT_COUNTERSIGNATURE + " vt_per\n"
                            + " LEFT JOIN " + TableList.TM_PURPOSE_MAST + " pur_mst ON pur_mst.pur_cd=vt_per.pur_cd\n"
                            + " LEFT JOIN " + TableList.VM_PERMIT_TYPE + " pmt_mst ON pmt_mst.code=vt_per.pmt_type\n"
                            + " left outer join " + TableList.VA_STATUS + " b on b.appl_no = vt_per.appl_no\n"
                            + " left outer join " + TableList.VHA_STATUS + " c on c.appl_no = b.appl_no and c.pur_cd = b.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                            + " where vt_per.appl_no=? and vt_per.regn_no=? and vt_per.state_cd=? and vt_per.off_cd=?"
                            + " union \n"
                            + " (select DUP.appl_no as appl_no, vt_per.pmt_no as pmt_no, vt_per.REGN_NO as regn_no,DUP.pur_cd, vt_per.pmt_type,\n"
                            + " pur_mst.descr as pur_descr,pmt_mst.descr as pmt_descr,c.office_remark, c.public_remark,to_char(b.op_dt,'dd-MON-yyyy') as appl_dt\n"
                            + "  from " + TableList.VT_PERMIT_COUNTERSIGNATURE + " vt_per \n"
                            + " INNER JOIN " + TableList.VHA_DUP + " DUP ON DUP.REGN_NO = vt_per.REGN_NO \n"
                            + " LEFT JOIN " + TableList.TM_PURPOSE_MAST + " pur_mst ON pur_mst.pur_cd=DUP.pur_cd\n"
                            + " LEFT JOIN " + TableList.VM_PERMIT_TYPE + " pmt_mst ON pmt_mst.code=vt_per.pmt_type\n"
                            + " left outer join " + TableList.VA_STATUS + " b on b.appl_no = vt_per.appl_no\n"
                            + " left outer join " + TableList.VHA_STATUS + " c on c.appl_no = b.appl_no and c.pur_cd = b.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                            + " where DUP.appl_no=? and vt_per.regn_no=? and vt_per.state_cd=? and vt_per.off_cd=? order by DUP.moved_on DESC limit 1)";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, appl_no);
                    ps.setString(2, regn_no_va);
                    ps.setString(3, Util.getUserStateCode());
                    ps.setInt(4, Util.getSelectedSeat().getOff_cd());
                    ps.setString(5, appl_no);
                    ps.setString(6, regn_no_va);
                    ps.setString(7, Util.getUserStateCode());
                    ps.setInt(8, Util.getSelectedSeat().getOff_cd());
                    RowSet rs11 = tmgr.fetchDetachedRowSet_No_release();
                    if (rs11.next()) {
                        dobj = new PrintPermitShowDetails();
                        dobj.setAppl_no(rs11.getString("appl_no"));
                        dobj.setAppl_dt(rs11.getString("appl_dt"));
                        dobj.setRegn_no(rs11.getString("regn_no"));
                        dobj.setDoc_id(documentId);
                        dobj.setDoc_id_descr(doc_descr);
                        dobj.setPmt_no(rs11.getString("pmt_no"));
                        dobj.setPmt_type(rs11.getString("pmt_type"));
                        dobj.setPmt_type_descr(rs11.getString("pmt_descr"));
                        dobj.setPur_cd(rs11.getString("pur_cd"));
                        dobj.setPur_cd_descr(rs11.getString("pur_descr"));
                        dobj.setPublic_remark(rs11.getString("public_remark"));
                        dobj.setOffice_remark(rs11.getString("office_remark"));
                        list.add(dobj);

                    }
                } else if ("8".equals(documentId)) {
                    query = " select a.appl_no,to_char(b.op_dt,'dd-MON-yyyy') as appl_dt, a.regn_no,c.pur_cd,d.descr as pur_descr, c.pmt_no, b.office_remark, b.public_remark from " + TableList.VA_PERMIT_PRINT + " a\n"
                            + "INNER JOIN " + TableList.VHA_STATUS + " b on b.appl_no = a.appl_no \n"
                            + "INNER JOIN " + TableList.VT_PERMIT_TRANSACTION + " c on c.appl_no = a.appl_no \n"
                            + "left join " + TableList.TM_PURPOSE_MAST + " d on d.pur_cd=c.pur_cd "
                            + "where b.state_cd= ? AND a.doc_id = ? AND c.pur_cd= ? AND b.off_cd = ? and a.appl_no=? order by b.moved_on DESC limit 1 ";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, Util.getUserStateCode());
                    ps.setString(2, documentId);
                    ps.setInt(3, TableConstants.VM_PMT_SURRENDER_PUR_CD);
                    ps.setInt(4, Util.getSelectedSeat().getOff_cd());
                    ps.setString(5, appl_no);

                    RowSet rs4 = tmgr.fetchDetachedRowSet_No_release();
                    if (rs4.next()) {
                        dobj = new PrintPermitShowDetails();
                        dobj.setAppl_no(rs4.getString("appl_no"));
                        dobj.setAppl_dt(rs4.getString("appl_dt"));
                        dobj.setRegn_no(rs4.getString("regn_no"));
                        dobj.setDoc_id(documentId);
                        dobj.setDoc_id_descr(doc_descr);
                        dobj.setPmt_no(rs4.getString("pmt_no"));
                        dobj.setPmt_type("0");
                        dobj.setPmt_type_descr(null);
                        dobj.setPur_cd(rs4.getString("pur_cd"));
                        dobj.setPur_cd_descr(rs4.getString("pur_descr"));
                        dobj.setPublic_remark(rs4.getString("public_remark"));
                        dobj.setOffice_remark(rs4.getString("office_remark"));
                        list.add(dobj);
                    }
                } else if (documentId.equalsIgnoreCase("9")) {
                    query = "SELECT * from ("
                            + " (select a.appl_no,b.pur_cd, to_char(c.op_dt,'dd-MON-yyyy') as appl_dt,pur_mst.descr as pur_descr, b.regn_no, b.pmt_no, c.office_remark, c.public_remark\n"
                            + " from " + TableList.VA_PERMIT_PRINT + " a \n"
                            + " inner join " + TableList.VT_PERMIT_TRANSACTION + " b on a.appl_no = b.appl_no\n"
                            + " inner join " + TableList.VHA_STATUS + " c on c.appl_no = b.appl_no\n"
                            + " LEFT JOIN " + TableList.TM_PURPOSE_MAST + " pur_mst ON pur_mst.pur_cd=b.pur_cd\n"
                            + " where a.doc_id = ? AND b.state_cd = ? AND b.off_cd = ? and a.regn_no=? and a.appl_no=? order by c.moved_on DESC limit 1)\n"
                            + " union\n"
                            + " (select a.appl_no,b.pur_cd, to_char(c.op_dt,'dd-MON-yyyy') as appl_dt,pur_mst.descr as pur_descr, b.regn_no, b.pmt_no, c.office_remark, c.public_remark\n"
                            + " from " + TableList.VA_PERMIT_PRINT + " a \n"
                            + " inner join " + TableList.VHA_PERMIT_TRANSACTION + " b on a.appl_no = b.appl_no\n"
                            + " inner join " + TableList.VHA_STATUS + " c on c.appl_no = b.appl_no\n"
                            + " LEFT JOIN " + TableList.TM_PURPOSE_MAST + " pur_mst ON pur_mst.pur_cd=b.pur_cd\n"
                            + " where a.doc_id = ? AND b.state_cd = ? AND b.off_cd = ? AND trans_pur_cd in (?,?) AND c.action_cd in (?,?) and a.regn_no=? and a.appl_no=? order by c.moved_on DESC limit 1)"
                            + ") vahan";
                    ps = tmgr.prepareStatement(query);
                    int i = 1;
                    ps.setString(i++, documentId);
                    ps.setString(i++, Util.getUserStateCode());
                    ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
                    ps.setString(i++, regn_no_va);
                    ps.setString(i++, appl_no);
                    ps.setString(i++, documentId);
                    ps.setString(i++, Util.getUserStateCode());
                    ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
                    ps.setInt(i++, TableConstants.VM_PMT_CANCELATION_PUR_CD);
                    ps.setInt(i++, TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD);
                    ps.setInt(i++, TableConstants.TM_ROLE_PMT_PERMANENT_SURRENDER_APPROVAL);
                    ps.setInt(i++, TableConstants.TM_ROLE_PMT_CANCEL_APPROVAL);
                    ps.setString(i++, regn_no_va);
                    ps.setString(i++, appl_no);
                    RowSet rs6 = tmgr.fetchDetachedRowSet_No_release();
                    if (rs6.next()) {
                        dobj = new PrintPermitShowDetails();
                        dobj.setAppl_no(rs6.getString("appl_no"));
                        dobj.setAppl_dt(rs6.getString("appl_dt"));
                        dobj.setRegn_no(rs6.getString("regn_no"));
                        dobj.setDoc_id(documentId);
                        dobj.setDoc_id_descr(doc_descr);
                        dobj.setPmt_no(rs6.getString("pmt_no"));
                        dobj.setPmt_type("0");
                        dobj.setPmt_type_descr(null);
                        dobj.setPur_cd(rs6.getString("pur_cd"));
                        dobj.setPur_cd_descr(rs6.getString("pur_descr"));
                        dobj.setPublic_remark(rs6.getString("public_remark"));
                        dobj.setOffice_remark(rs6.getString("office_remark"));
                        list.add(dobj);
                    }
                } else if (TableConstants.CS_AUTH_DOCUMENT.equals(documentId)) {
                    query = " select a.appl_no,to_char(b.op_dt,'dd-MON-yyyy') as appl_dt, a.regn_no,c.pur_cd,d.descr as pur_descr, c.pmt_no, b.office_remark, b.public_remark from " + TableList.VA_PERMIT_PRINT + " a\n"
                            + "INNER JOIN " + TableList.VT_PERMIT_COUNTERSIGNATURE_AUTHORIZATION + " c on c.appl_no = a.appl_no and c.regn_no = a.regn_no \n"
                            + " left outer join " + TableList.VHA_STATUS + " b on b.appl_no = c.appl_no and b.pur_cd = c.pur_cd  \n"
                            + " left join " + TableList.TM_PURPOSE_MAST + " d on d.pur_cd=c.pur_cd "
                            + " where c.state_cd= ? AND a.doc_id = ? AND c.pur_cd= ? and a.appl_no=? order by b.moved_on DESC limit 1 ";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, Util.getUserStateCode());
                    ps.setString(2, documentId);
                    ps.setInt(3, TableConstants.VM_PMT_COUNTER_SIGNATURE_AUTH_PUR_CD);
                    ps.setString(4, appl_no);
                    RowSet rs4 = tmgr.fetchDetachedRowSet_No_release();
                    if (rs4.next()) {
                        dobj = new PrintPermitShowDetails();
                        dobj.setAppl_no(rs4.getString("appl_no"));
                        dobj.setAppl_dt(rs4.getString("appl_dt"));
                        dobj.setRegn_no(rs4.getString("regn_no"));
                        dobj.setDoc_id(documentId);
                        dobj.setDoc_id_descr(doc_descr);
                        dobj.setPmt_no(rs4.getString("pmt_no"));
                        dobj.setPmt_type("0");
                        dobj.setPmt_type_descr(null);
                        dobj.setPur_cd(rs4.getString("pur_cd"));
                        dobj.setPur_cd_descr(rs4.getString("pur_descr"));
                        dobj.setPublic_remark(rs4.getString("public_remark"));
                        dobj.setOffice_remark(rs4.getString("office_remark"));
                        list.add(dobj);
                    }
                }
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
        return list;
    }
// end by manoj

    public List<PrintPermitShowDetails> getRePermitPrintRow(String applNo, String regnNo, String loiNo) {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        List<PrintPermitShowDetails> list = new ArrayList<>();
        String query;
        try {
            tmgr = new TransactionManagerReadOnly("getRePermitPrintRow");
            if (!CommonUtils.isNullOrBlank(loiNo)) {
                query = " SELECT row_number() over() as srl_no,pri.appl_no,b.regn_no,detail.pur_cd,pri.doc_id, to_char(pri.op_dt,'DD-MON-YYYY') as op_dt FROM permit.vh_permit_print pri\n"
                        + " inner join permit.va_permit b on pri.appl_no=b.appl_no \n"
                        + " inner join va_details detail on detail.appl_no=b.appl_no \n"
                        //                        + " inner join vha_status sta on sta.appl_no = b.appl_no AND sta.moved_on = (select max(moved_on) from vha_status where appl_no = pri.appl_no) \n"
                        + " where  pri.doc_id='3'";
            } else {
                query = "SELECT row_number() over() as srl_no,pri.appl_no,regn_no,sta.pur_cd,pri.doc_id, to_char(pri.op_dt,'DD-MON-YYYY') as op_dt FROM permit.vh_permit_print pri\n"
                        + "inner join vha_status sta on sta.appl_no = pri.appl_no AND sta.moved_on = (select max(moved_on) from vha_status where appl_no = pri.appl_no) \n"
                        + "where  to_date(to_char(pri.printed_on,'YYYY/MM/DD'), 'YYYY/MM/DD') >= current_date-10";
            }
            if (!CommonUtils.isNullOrBlank(applNo)) {
                query += " AND pri.appl_no = ? ";
            } else if (!CommonUtils.isNullOrBlank(regnNo)) {
                query += " AND regn_no = ? ";
            } else if (!CommonUtils.isNullOrBlank(loiNo)) {
                query += " AND b.appl_no=? and b.state_cd=? and b.off_cd=?  and detail.entry_status !='A' and detail.regn_no='NEW' AND b.offer_no <> null ";
            }
            //query += " AND printed_by = ? order by pri.op_dt";
            if (CommonUtils.isNullOrBlank(loiNo)) {
                query += " AND sta.state_cd = ? AND sta.off_cd = ? order by pri.op_dt";
            }
            ps = tmgr.prepareStatement(query);
            if (!CommonUtils.isNullOrBlank(applNo)) {
                ps.setString(1, applNo);
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            } else if (!CommonUtils.isNullOrBlank(regnNo)) {
                ps.setString(1, regnNo);
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            } else if (!CommonUtils.isNullOrBlank(loiNo)) {
                ps.setString(1, loiNo);
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            }

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                PrintPermitShowDetails dobj = new PrintPermitShowDetails();
                dobj.setSr(rs.getString("srl_no"));
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setPur_cd(rs.getString("pur_cd"));
                dobj.setDoc_id(rs.getString("doc_id"));
                dobj.setOp_dt(rs.getString("op_dt"));
                list.add(dobj);
            }
            if (list.size() == 0 && (!CommonUtils.isNullOrBlank(applNo) || !CommonUtils.isNullOrBlank(regnNo))) {
                query = "SELECT row_number() over() as srl_no,pri.appl_no,pri.regn_no,sta.pur_cd,11 AS doc_id, to_char(pri.op_dt,'DD-MON-YYYY') as op_dt FROM " + TableList.VA_PERMIT + " pri\n"
                        + "inner join " + TableList.VA_STATUS + " sta on sta.appl_no = pri.appl_no AND pri.state_cd=sta.state_cd and sta.off_cd=pri.off_cd \n"
                        + "where  ";
                if (!CommonUtils.isNullOrBlank(applNo)) {
                    query += "  pri.appl_no = ? ";
                } else if (!CommonUtils.isNullOrBlank(regnNo)) {
                    query += "  pri.regn_no = ? ";
                }
                query += " AND sta.state_cd = ? AND sta.off_cd = ? ";
                ps = tmgr.prepareStatement(query);
                if (!CommonUtils.isNullOrBlank(applNo)) {
                    ps.setString(1, applNo);
                    ps.setString(2, Util.getUserStateCode());
                    ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                } else if (!CommonUtils.isNullOrBlank(regnNo)) {
                    ps.setString(1, regnNo);
                    ps.setString(2, Util.getUserStateCode());
                    ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                }
                RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
                if (rs1.next()) {
                    PrintPermitShowDetails dobj = new PrintPermitShowDetails();
                    dobj.setSr(rs1.getString("srl_no"));
                    dobj.setAppl_no(rs1.getString("appl_no"));
                    dobj.setRegn_no(rs1.getString("regn_no"));
                    dobj.setPur_cd(rs1.getString("pur_cd"));
                    dobj.setDoc_id(rs1.getString("doc_id"));
                    dobj.setOp_dt(rs1.getString("op_dt"));
                    list.add(dobj);
                }
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
        return list;
    }

    public List<PrintPermitShowDetails> getOfferLeter(String flage) {
        List<PrintPermitShowDetails> list = new ArrayList<>();
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        String query;
        try {
            tmgr = new TransactionManagerReadOnly("Get Permit print Row");
            query = "Select row_number() over() as srl_no,a.appl_no,regn_no,typ.descr,offer_no,c.office_remark, c.public_remark\n"
                    + "from " + TableList.VA_PERMIT + " a INNER JOIN VM_PERMIT_TYPE typ on typ.code = pmt_type\n"
                    + "left outer join " + TableList.VA_STATUS + " b on b.appl_no = a.appl_no\n"
                    + "left outer join " + TableList.VHA_STATUS + " c on c.appl_no = a.appl_no and c.pur_cd = b.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                    + " where alloted_flag = ? AND b.action_cd = 25007 AND a.STATE_CD = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, flage);
            ps.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                list.add(new PrintPermitShowDetails(rs.getString("srl_no"), rs.getString("appl_no"), null, rs.getString("regn_no"), null, rs.getString("office_remark"), rs.getString("public_remark"), rs.getString("descr"), rs.getString("offer_no")));
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
        return list;
    }

    public String getPermitPrintInXHTML(String appl_no, int purCd, int permitType, String documentId, Status_dobj status, String regn_no) throws VahanException {
        String jprint = null;
        TransactionManager tmgr = null;
        String mainApplNo = "";
        try {
            if (purCd == TableConstants.VM_PMT_RESTORE_PUR_CD) {
                purCd = CommonPermitPrintImpl.getPurCdInVhaPermitTranaction(appl_no, purCd);
            }
            tmgr = new TransactionManager("getPermitPrintInXHTML");
            HttpSession ses = Util.getSession();
            if (permitType == 0) {
                permitType = getVtPermitPermitType(tmgr, appl_no, regn_no, purCd);
                ses.setAttribute("permitPrintDocPermitType", permitType);
            } else {
                ses.setAttribute("permitPrintDocPermitType", permitType);
            }
            ses.setAttribute("permitCurrentApplNo", appl_no);
            ses.setAttribute("permitPrintDocApplNo", appl_no);
            ses.setAttribute("permitPrintDocPurCd", purCd);
            ses.setAttribute("permitPrintDocRegnNo", regn_no);
            ses.setAttribute("permitPrintDocID", documentId);
            mainApplNo = appl_no;
            if (documentId.equalsIgnoreCase("4")) {
                jprint = "/ui/permitreports/PermitRegister.xhtml?faces-redirect=true";
            } else if (documentId.equalsIgnoreCase("11")) {
                jprint = "/ui/permitreports/PermitAcknowledgement.xhtml?faces-redirect=true";
            } else if (documentId.equalsIgnoreCase("9")) {
                jprint = "/ui/permitreports/SurrenderSlip.xhtml?faces-redirect=true";
            } else if (documentId.equalsIgnoreCase("8")) {
                jprint = "/ui/permitreports/NocOfPermit.xhtml?faces-redirect=true";
            } else if (documentId.equalsIgnoreCase(TableConstants.CS_AUTH_DOCUMENT)) {
                jprint = "/ui/permitreports/CounterRecommendationLetter.xhtml?faces-redirect=true";
            } else if (purCd == TableConstants.VM_PMT_TEMP_PUR_CD || purCd == TableConstants.VM_PMT_RENEW_TEMP_PUR_CD) {
                ses.setAttribute("permitPrintDocApplNo", appl_no);
                jprint = "/ui/permitreports/TemporaryPermitPrintDoc.xhtml?faces-redirect=true";
            } else if (purCd == TableConstants.VM_PMT_SPECIAL_PUR_CD) {
                ses.setAttribute("permitPrintDocApplNo", appl_no);
                jprint = "/ui/permitreports/SpecialPermitPrintDoc.xhtml?faces-redirect=true";
            } else if (purCd == TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD
                    || (purCd == TableConstants.VM_PMT_FRESH_PUR_CD
                    && (documentId.equalsIgnoreCase("5") || documentId.equalsIgnoreCase("12")))
                    || (purCd == TableConstants.VM_PMT_DUPLICATE_PUR_CD && (documentId.equalsIgnoreCase("5") || documentId.equalsIgnoreCase("12")))) {
                //documentId = "5";
                if (purCd == TableConstants.VM_PMT_DUPLICATE_PUR_CD) {
                    appl_no = getDupApplicationNo(appl_no, TableConstants.VM_PMT_DUPLICATE_PUR_CD, tmgr);
                }
                ses.setAttribute("permitPrintDocApplNo", appl_no);
                jprint = "/ui/permitreports/HomeAuthorisation.xhtml?faces-redirect=true";
                if (documentId.equalsIgnoreCase("12")) {
                    jprint = "/ui/permitreports/NPAutharisationPrint.xhtml?faces-redirect=true";
                }
            } else if (purCd == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD
                    || (purCd == TableConstants.VM_PMT_RENEWAL_PUR_CD && documentId.equalsIgnoreCase("5"))
                    || documentId.equalsIgnoreCase("5") || documentId.equalsIgnoreCase("12")) {
                ses.setAttribute("permitPrintDocApplNo", appl_no);
                jprint = "/ui/permitreports/RenewalHomeAuthorisation.xhtml?faces-redirect=true";
                if (documentId.equalsIgnoreCase("12")) {
                    jprint = "/ui/permitreports/NPAutharisationPrint.xhtml?faces-redirect=true";
                }
            } else if (purCd == TableConstants.VM_PMT_APPLICATION_PUR_CD || documentId.equalsIgnoreCase("3")) {
                ses.setAttribute("permitPrintDocApplNo", appl_no);
                jprint = "/ui/permitreports/OfferLetter.xhtml?faces-redirect=true";
            } else if (purCd == TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD || purCd == TableConstants.VM_PMT_DUP_COUNTER_SIGNATURE_PUR_CD || purCd == TableConstants.VM_PMT_RENEW_COUNTER_SIGNATURE_PUR_CD) {
                if (purCd == TableConstants.VM_PMT_DUP_COUNTER_SIGNATURE_PUR_CD) {
                    appl_no = getDupCSApplNo(appl_no, TableConstants.VM_PMT_DUP_COUNTER_SIGNATURE_PUR_CD, tmgr);
                    ses.setAttribute("permitPrintDocApplNo", appl_no);
                }
                jprint = "/ui/permitreports/CounterSignature.xhtml?faces-redirect=true";
            } else if (purCd == TableConstants.VM_PMT_FRESH_PUR_CD || purCd == TableConstants.VM_PMT_RENEWAL_PUR_CD
                    || purCd == TableConstants.VM_PMT_DUPLICATE_PUR_CD || purCd == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD
                    || purCd == TableConstants.VM_PMT_TRANSFER_PUR_CD || purCd == TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD
                    || purCd == TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD || purCd == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD
                    || purCd == TableConstants.VM_PMT_CA_PUR_CD || purCd == TableConstants.VM_PMT_SUSPENSION_PUR_CD || purCd == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD || purCd == TableConstants.VM_PMT_LEASE_PUR_CD
                    || purCd == TableConstants.VM_PMT_TEMP_SUR_PUR_CD || purCd == TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD) {
                if (purCd == TableConstants.VM_PMT_DUPLICATE_PUR_CD) {
                    appl_no = getDupApplicationNo(appl_no, TableConstants.VM_PMT_DUPLICATE_PUR_CD, tmgr);
                }
                if (purCd == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD) {
                    appl_no = getRestoreApplicationNo(appl_no, TableConstants.VM_PMT_REPLACE_VEH_PUR_CD, tmgr);
                }
                if (purCd == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD) {
                    appl_no = getRestoreApplicationNo(appl_no, TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD, tmgr);
                }
                if (purCd == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD) {
                    appl_no = getRestoreApplicationNo(appl_no, TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD, tmgr);
                }
                if (purCd == TableConstants.VM_PMT_TRANSFER_PUR_CD || purCd == TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD
                        || purCd == TableConstants.VM_PMT_CA_PUR_CD || purCd == TableConstants.VM_PMT_SUSPENSION_PUR_CD || purCd == TableConstants.VM_PMT_TEMP_SUR_PUR_CD
                        || purCd == TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD) {
                    appl_no = getTransferApplicationNo(appl_no, purCd, tmgr);
                }
                if (purCd == TableConstants.VM_PMT_LEASE_PUR_CD) {
                    appl_no = getLeaseApplicationNo(appl_no, TableConstants.VM_PMT_LEASE_PUR_CD, tmgr);
                }
                switch (permitType) {
                    case 101: {
                        ses.setAttribute("permitPrintDocApplNo", appl_no);
                        if (documentId.equalsIgnoreCase("1")) {
                            jprint = "/ui/permitreports/SCPermitPartA.xhtml?faces-redirect=true";
                        } else if (documentId.equalsIgnoreCase("2")) {
                            jprint = "/ui/permitreports/SCPermitPartB.xhtml?faces-redirect=true";
                        }
                        break;
                    }
                    case 102: {
                        ses.setAttribute("permitPrintDocApplNo", appl_no);
                        if (documentId.equalsIgnoreCase("1")) {
                            jprint = "/ui/permitreports/CGPermitPartA.xhtml?faces-redirect=true";
                        } else if (documentId.equalsIgnoreCase("2")) {
                            jprint = "/ui/permitreports/CGPermitPartB.xhtml?faces-redirect=true";
                        }
                        break;
                    }
                    case 103: {
                        ses.setAttribute("permitPrintDocApplNo", appl_no);
                        if (documentId.equalsIgnoreCase("1")) {
                            jprint = "/ui/permitreports/AITPermit.xhtml?faces-redirect=true";
                        } else if (documentId.equalsIgnoreCase("2")) {
                            jprint = "/ui/permitreports/AITPermitPartB.xhtml?faces-redirect=true";
                        }
                        break;
                    }
                    case 104: {
                        ses.setAttribute("permitPrintDocApplNo", appl_no);
                        if (documentId.equalsIgnoreCase("1")) {
                            jprint = "/ui/permitreports/PSVPermitPartA.xhtml?faces-redirect=true";
                        } else if (documentId.equalsIgnoreCase("2")) {
                            jprint = "/ui/permitreports/PSVPermitPartB.xhtml?faces-redirect=true";
                        }
                        break;
                    }
                    case 105: {
                        ses.setAttribute("permitPrintDocApplNo", appl_no);
                        if (documentId.equalsIgnoreCase("1")) {
                            jprint = "/ui/permitreports/GCPermitPartA.xhtml?faces-redirect=true";
                        } else if (documentId.equalsIgnoreCase("2")) {
                            jprint = "/ui/permitreports/GCPermitPartB.xhtml?faces-redirect=true";
                        }
                        break;
                    }
                    case 106: {
                        ses.setAttribute("permitPrintDocApplNo", appl_no);
                        if (documentId.equalsIgnoreCase("1") && ("HP").contains(Util.getUserStateCode())) {
                            jprint = "/ui/permitreports/GCPermitPartA.xhtml?faces-redirect=true";
                        } else if (!("HP").contains(Util.getUserStateCode())) {
                            if (documentId.equalsIgnoreCase("1")) {
                                jprint = "/ui/permitreports/NGPermitPartA.xhtml?faces-redirect=true";
                            } else if (documentId.equalsIgnoreCase("2")) {
                                jprint = "/ui/permitreports/NGPermitPartB.xhtml?faces-redirect=true";
                            }
                        }
                        break;
                    }
                }
            }

            status.setAppl_no(mainApplNo);

            status.setPur_cd(purCd);
            if (purCd == TableConstants.VM_PMT_TRANSFER_PUR_CD || purCd == TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD
                    || purCd == TableConstants.VM_PMT_CA_PUR_CD || purCd == TableConstants.VM_PMT_TEMP_SUR_PUR_CD) {
                status.setPur_cd(TableConstants.VM_PMT_RESTORE_PUR_CD);
            }
            if (purCd == TableConstants.VM_PMT_FRESH_PUR_CD || purCd == TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD
                    || purCd == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD) {
                ServerUtil.vatovhNPAuthData(appl_no, tmgr);
            }
            vaPermitPrintTOvhPermitPrint(mainApplNo, tmgr, documentId);
            status = ServerUtil.webServiceForNextStage(status, tmgr);

            status.setEntry_status(TableConstants.STATUS_APPROVED);

            if (!regn_no.equalsIgnoreCase("NEW") && (countPermitPrint(tmgr, mainApplNo) == 0)) {
                if (purCd == TableConstants.VM_PMT_SUSPENSION_PUR_CD || purCd == TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD || purCd == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD) {
                    ServerUtil.updateApprovedStatusOfPermit(tmgr, status);
                } else {
                    if (!CommonUtils.isNullOrBlank(documentId) && !documentId.equalsIgnoreCase("11")) {
                        ServerUtil.updateApprovedStatus(tmgr, status);
                    }
                }
                status.setStatus(TableConstants.STATUS_COMPLETE);
            }
            if (!CommonUtils.isNullOrBlank(documentId) && !documentId.equalsIgnoreCase("11")) {
                fileFlowOfPermit(tmgr, status, mainApplNo, purCd, documentId);
            }
            tmgr.commit();
        } catch (Exception e) {
            throw new VahanException("Function getJasperPrint, Some problem in JRXML PRINT >>>" + e);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return jprint;
    }

    public void vaPermitPrintTOvhPermitPrint(String appl_no, TransactionManager tmgr, String docId) throws VahanException {
        String Query;
        PreparedStatement ps;
        try {
            Query = "INSERT INTO " + TableList.VH_PERMIT_PRINT + "(\n"
                    + "            appl_no, regn_no, doc_id, op_dt, printed_on, printed_by)\n"
                    + "     SELECT appl_no, regn_no, doc_id, op_dt,CURRENT_TIMESTAMP,?  FROM " + TableList.VA_PERMIT_PRINT + " where appl_no = ? AND doc_id=?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, appl_no);
            ps.setString(3, docId);
            ps.executeUpdate();

            Query = "DELETE FROM " + TableList.VA_PERMIT_PRINT + " where appl_no = ? AND doc_id=?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
            ps.setString(2, docId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new VahanException("Function vaPermitPrintTOvhPermitPrint >>>" + e.getMessage());
        }
    }

    private String getDupApplicationNo(String applNo, int purCd, TransactionManager tmgr) throws VahanException {
        String Query;
        String applNumber = "";
        PreparedStatement ps;
        try {
            Query = "SELECT APPL_NO FROM " + TableList.VT_PERMIT + " WHERE regn_no in (select regn_no from " + TableList.VH_DUP + " where appl_no = ? AND pur_cd = ?)";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, applNo);
            ps.setInt(2, purCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                applNumber = rs.getString("APPL_NO");
            }
        } catch (Exception e) {
            throw new VahanException("Function getDupApplicationNo >>>" + e.getMessage());
        }
        return applNumber;
    }

    private String getLeaseApplicationNo(String applNo, int purCd, TransactionManager tmgr) throws VahanException {
        String Query;
        String applNumber = "";
        PreparedStatement ps;
        try {
            Query = "SELECT APPL_NO FROM " + TableList.VT_PERMIT + " WHERE regn_no in (select m_regn_no from permit.vt_lease_permit where appl_no = ? AND pur_cd = ?)";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, applNo);
            ps.setInt(2, purCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                applNumber = rs.getString("APPL_NO");
            }
        } catch (Exception e) {
            throw new VahanException("Function getDupApplicationNo >>>" + e.getMessage());
        }
        return applNumber;
    }

    private String getDupCSApplNo(String applNo, int purCd, TransactionManager tmgr) throws VahanException {
        String Query;
        String applNumber = "";
        PreparedStatement ps;
        try {
            Query = "SELECT APPL_NO FROM " + TableList.VT_PERMIT_COUNTERSIGNATURE + " WHERE regn_no in (select regn_no from vha_dup where appl_no = ? AND pur_cd = ? order by moved_on desc limit 1) and pur_cd=?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, applNo);
            ps.setInt(2, purCd);
            ps.setInt(3, TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                applNumber = rs.getString("APPL_NO");
            }
        } catch (Exception e) {
            throw new VahanException("Function getDupApplicationNo >>>" + e.getMessage());
        }
        return applNumber;
    }

    private String getRestoreApplicationNo(String applNo, int purCd, TransactionManager tmgr) throws VahanException {
        String Query;
        String applNumber = "";
        PreparedStatement ps;
        try {
            Query = "SELECT APPL_NO FROM " + TableList.VT_PERMIT + " WHERE regn_no in (select new_regn_no from " + TableList.VHA_PERMIT_TRANSACTION + " where appl_no = ? AND trans_pur_cd = ?)";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, applNo);
            ps.setInt(2, purCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                applNumber = rs.getString("APPL_NO");
            }
        } catch (Exception e) {
            throw new VahanException("Function getDupApplicationNo >>>" + e.getMessage());
        }
        return applNumber;
    }

    private String getTransferApplicationNo(String applNo, int purCd, TransactionManager tmgr) throws VahanException {
        String Query;
        String applNumber = "";
        PreparedStatement ps;
        try {
            Query = "SELECT APPL_NO FROM " + TableList.VT_PERMIT + " WHERE regn_no in (select regn_no from " + TableList.VHA_PERMIT_TRANSACTION + "  where appl_no = ? AND trans_pur_cd =?)";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, applNo);
            ps.setInt(2, purCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                applNumber = rs.getString("APPL_NO");
            }
        } catch (Exception e) {
            throw new VahanException("Function getDupApplicationNo >>>" + e.getMessage());
        }
        return applNumber;
    }

    public void fileFlowOfPermit(TransactionManager tmgr, Status_dobj status_dobj, String applNo, int purCd, String doc_id) throws VahanException {
        String sql;
        PreparedStatement ps;
        try {
            sql = "SELECT * FROM " + TableList.VA_PERMIT_PRINT + " where appl_no = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (!rs.next()) {
                if (!CommonUtils.isNullOrBlank(doc_id) && doc_id.equalsIgnoreCase("3") && purCd == TableConstants.VM_PMT_FRESH_PUR_CD) {
                } else {
                    sql = "INSERT INTO " + TableList.VHA_STATUS + " "
                            + " SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno, \n"
                            + "  action_cd, seat_cd, cntr_id, ?, office_remark, public_remark, \n"
                            + "  file_movement_type, ? , op_dt, current_timestamp, ?"
                            + "  from  " + TableList.VA_STATUS + " where appl_no=? ";
                    if (purCd == TableConstants.VM_PMT_APPLICATION_PUR_CD) {
                        sql += " AND pur_cd = ?";
                    }
                    ps = tmgr.prepareStatement(sql);
                    int i = 1;
                    ps.setString(i++, status_dobj.getStatus());
                    ps.setLong(i++, Long.parseLong(Util.getEmpCode()));
                    ps.setString(i++, Util.getClientIpAdress());
                    ps.setString(i++, applNo);
                    if (purCd == TableConstants.VM_PMT_APPLICATION_PUR_CD) {
                        ps.setInt(i++, purCd);
                    }
                    ps.executeUpdate();
                    sql = "Delete From " + TableList.VA_STATUS + " WHERE appl_no=?";
                    if (purCd == TableConstants.VM_PMT_APPLICATION_PUR_CD) {
                        sql += " AND pur_cd = ?";
                    }
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, applNo);
                    if (purCd == TableConstants.VM_PMT_APPLICATION_PUR_CD) {
                        ps.setInt(2, purCd);
                    }
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            LOGGER.error("File has already Moved for Appl No-" + applNo);
            throw new VahanException("Problem in file Flow !!!");
        }
    }

    public int getVtPermitPermitType(TransactionManager tmgr, String applNo, String regn_no, int pur_cd) throws VahanException {
        String sql;
        int pmtType = 0;
        PreparedStatement ps;
        try {
            RowSet rs = null;
            sql = "SELECT pmt_type FROM " + TableList.VT_PERMIT + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                pmtType = rs.getInt("pmt_type");
            } else {
                sql = "SELECT pmt_type FROM " + TableList.VT_PERMIT + " where regn_no = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regn_no);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    pmtType = rs.getInt("pmt_type");
                }
            }
            if (pmtType == 0 && (pur_cd == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD || pur_cd == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD || pur_cd == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD)) {
                sql = "SELECT pmt_type FROM " + TableList.VH_PERMIT + " where regn_no = ? and pmt_status='SUR' order by moved_on DESC limit 1";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regn_no);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    pmtType = rs.getInt("pmt_type");
                }
            }
        } catch (SQLException e) {
            throw new VahanException("getVtPermitPermitType");
        }
        return pmtType;
    }

    public List<PrintPermitShowDetails> getAdminstartorPermitPrint(TransactionManagerReadOnly tmgr, int purCd, int permitType, String documentId, List<PrintPermitShowDetails> list) throws SQLException {
        PreparedStatement ps;
        String query = "select row_number() over() as srl_no,a.appl_no,a.pur_cd,b.doc_id, to_char(a.op_dt,'dd-MON-yyyy') as appl_dt, a.regn_no, a.pmt_no, 'ADMINISTRATOR ENTRY' as office_remark,'ADMINISTRATOR ENTRY' as public_remark from " + TableList.VT_PERMIT + " a\n"
                + "join " + TableList.VA_PERMIT_PRINT + " b on a.appl_no = b.appl_no\n"
                + "where a.remarks = ? AND a.pur_cd = ? AND a.pmt_type = ? AND a.state_cd = ? AND a.off_cd = ? AND b.doc_id = ?";
        ps = tmgr.prepareStatement(query);
        int i = 1;
        ps.setString(i++, "ADMINISTRATOR ENTRY");
        ps.setInt(i++, purCd);
        ps.setInt(i++, permitType);
        ps.setString(i++, Util.getUserStateCode());
        ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
        ps.setString(i++, documentId);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        while (rs.next()) {
            list.add(new PrintPermitShowDetails(rs.getString("srl_no"), rs.getString("appl_no"), rs.getString("appl_dt"), rs.getString("regn_no"), rs.getString("pmt_no"), rs.getString("office_remark"), rs.getString("public_remark"), null, null));
        }
        return list;
    }

    public int countPermitPrint(TransactionManager tmgr, String applNo) throws VahanException {
        String sql;
        int count = 0;
        PreparedStatement ps;
        try {
            sql = "SELECT count(*) FROM " + TableList.VA_PERMIT_PRINT + " where appl_no = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.error("countPermitPrint Moved for Appl No-" + applNo);
            throw new VahanException("Problem in file Flow !!!");
        }
        return count;
    }

    public boolean checkAuthDetailsforPrint(String regn_no) {
        String documentId = "";
        String regn_no_va = "";
        String appl_no = "";
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        boolean authDetailFound = false;
        String query;
        try {
            tmgr = new TransactionManagerReadOnly("checkAuthDetailsforPrint");
            query = "select vapermit.appl_no as appl_no, vapermit.regn_no as regn_no, vapermit.doc_id as doc_id, vapermit.op_dt,perdoc.descr as doc_id_descr\n"
                    + " from " + TableList.VA_PERMIT_PRINT + " vapermit \n"
                    + " Left Join " + TableList.VM_PERMIT_DOCUMENTS + " perdoc on perdoc.doc_id=vapermit.doc_id\n"
                    + " where vapermit.regn_no=? order by vapermit.doc_id ASC";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                documentId = rs.getString("doc_id");
                regn_no_va = rs.getString("regn_no");
                appl_no = rs.getString("appl_no");
                if ("5".equals(documentId)) {
                    query = " (select vt_per.appl_no as appl_no, vt_per.pmt_no as pmt_no, AUTH.REGN_NO as regn_no,AUTH.pur_cd, vt_per.pmt_type,\n"
                            + " pur_mst.descr as pur_descr,pmt_mst.descr as pmt_descr,c.office_remark, c.public_remark,to_char(b.op_dt,'dd-MON-yyyy') as appl_dt \n"
                            + " from " + TableList.VT_PERMIT + " vt_per \n"
                            + " INNER JOIN " + TableList.VT_PERMIT_HOME_AUTH + " AUTH ON AUTH.REGN_NO = vt_per.REGN_NO and AUTH.pmt_no = vt_per.pmt_no \n"
                            + " LEFT JOIN " + TableList.TM_PURPOSE_MAST + " pur_mst ON pur_mst.pur_cd=AUTH.pur_cd \n"
                            + " LEFT JOIN " + TableList.VM_PERMIT_TYPE + " pmt_mst ON pmt_mst.code=vt_per.pmt_type \n"
                            + " left outer join " + TableList.VA_STATUS + " b on b.appl_no = vt_per.appl_no \n"
                            + " left outer join " + TableList.VHA_STATUS + " c on c.appl_no = vt_per.appl_no and c.pur_cd = vt_per.pur_cd and c.file_movement_slno = b.file_movement_slno - 1 \n"
                            + " where vt_per.appl_no=? and AUTH.regn_no=? and vt_per.state_cd=? and vt_per.off_cd=?)";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, appl_no);
                    ps.setString(2, regn_no_va);
                    ps.setString(3, Util.getUserStateCode());
                    ps.setInt(4, Util.getSelectedSeat().getOff_cd());
                    RowSet rst = tmgr.fetchDetachedRowSet_No_release();
                    if (!rst.next()) {
                        authDetailFound = true;

                    }
                }
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
        return authDetailFound;
    }
}