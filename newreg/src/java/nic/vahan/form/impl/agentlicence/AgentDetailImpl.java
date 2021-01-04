/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.agentlicence;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.DocumentType;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.FeeDraftDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.PaymentCollectionDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.fee.FeeAgentRegnDobj;
import nic.vahan.form.impl.FeeDraftimpl;
import nic.vahan.form.impl.FeeImpl;
import nic.vahan.form.impl.Receipt_Master_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.sendSMS;
import org.apache.log4j.Logger;

public class AgentDetailImpl {

    private static Logger LOGGER = Logger.getLogger(AgentDetailImpl.class);
    FeeImpl feeImpl = new FeeImpl();

    public static class PaymentGenInfo {

        private long cashAmt = 0;
        private long draftAmt = 0;
        private long totalAmt = 0;
        private long excessAmt = 0;

        /**
         * @return the cashAmt
         */
        public long getCashAmt() {
            return cashAmt;
        }

        /**
         * @param cashAmt the cashAmt to set
         */
        public void setCashAmt(long cashAmt) {
            this.cashAmt = cashAmt;
        }

        /**
         * @return the draftAmt
         */
        public long getDraftAmt() {
            return draftAmt;
        }

        /**
         * @param draftAmt the draftAmt to set
         */
        public void setDraftAmt(long draftAmt) {
            this.draftAmt = draftAmt;
        }

        /**
         * @return the totalAmt
         */
        public long getTotalAmt() {
            return totalAmt;
        }

        /**
         * @param totalAmt the totalAmt to set
         */
        public void setTotalAmt(long totalAmt) {
            this.totalAmt = totalAmt;
        }

        /**
         * @return the excessAmt
         */
        public long getExcessAmt() {
            return excessAmt;
        }

        /**
         * @param excessAmt the excessAmt to set
         */
        public void setExcessAmt(long excessAmt) {
            this.excessAmt = excessAmt;
        }
    }

    public FeeAgentRegnDobj getFeeData(FeeAgentRegnDobj feeAgentRegnDobj, Integer purCd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sqlFee = "";
        try {
            tmgr = new TransactionManager("feeAmount");
            sqlFee = "SELECT vmfe.fees , vmfe.pur_cd ,tmst.descr as stateName,vmfe.state_cd FROM vm_feemast vmfe left outer join tm_state tmst on "
                    + " tmst.state_code=vmfe.state_cd where vmfe.state_cd=? and vmfe.pur_cd=?";
            ps = tmgr.prepareStatement(sqlFee);
            ps.setString(1, Util.getUserStateCode());
            ps.setLong(2, purCd);
            RowSet rsFee = tmgr.fetchDetachedRowSet();
            if (rsFee.next()) {
                feeAgentRegnDobj.setFees(rsFee.getLong("fees"));
                feeAgentRegnDobj.setGrandTotal(rsFee.getInt("fees"));
                feeAgentRegnDobj.setStateName(rsFee.getString("stateName"));
                feeAgentRegnDobj.setStateCD(rsFee.getString("state_cd"));
                feeAgentRegnDobj.setPurCd(rsFee.getString("pur_cd"));
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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
        return feeAgentRegnDobj;
    }

    public FeeAgentRegnDobj agentDetails(String applNo) throws VahanException {
        FeeAgentRegnDobj feeAgentRegnDobj = null;
        PreparedStatement psAgent = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("agentDetails");
            String sql = "SELECT agent_name, f_name, contact_no  FROM " + TableList.VA_AGENT_DETAILS + " where state_cd = ? and off_cd = ? and appl_no = ? ";
            psAgent = tmgr.prepareStatement(sql);
            psAgent.setString(1, Util.getUserStateCode());
            psAgent.setInt(2, Util.getUserOffCode());
            psAgent.setString(3, applNo);
            RowSet rsAgent = tmgr.fetchDetachedRowSet();
            if (rsAgent.next()) {
                feeAgentRegnDobj = new FeeAgentRegnDobj();
                feeAgentRegnDobj.setOwnName(rsAgent.getString("agent_name"));
                feeAgentRegnDobj.setFname(rsAgent.getString("f_name"));
                feeAgentRegnDobj.setMobileNo(rsAgent.getString("contact_no"));
                feeAgentRegnDobj.setAppl_no(applNo);
                feeAgentRegnDobj.setStateCD(Util.getUserStateCode());
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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
        return feeAgentRegnDobj;
    }

    public List purCdList() throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        List<SelectItem> list = new ArrayList();
        try {
            String sqlPurCd = "select pur_cd,descr from " + TableList.TM_PURPOSE_MAST + "  where pur_cd in (?,?,?)";
            tmgr = new TransactionManager("feeAmount");
            ps = tmgr.prepareStatement(sqlPurCd);
            ps.setInt(1, TableConstants.AGENT_DETAIL_PUR_CD);
            ps.setInt(2, TableConstants.AGENT_DETAIL_REN_PUR_CD);
            ps.setInt(3, TableConstants.AGENT_DETAIL_DUP_PUR_CD);
            RowSet rs = tmgr.fetchDetachedRowSet();
            list.add(new SelectItem("-1", "Select Fee"));
            while (rs.next()) {
                list.add(new SelectItem(rs.getInt("pur_cd"), rs.getString("descr")));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
        return list;
    }

    public void insertIntoVaAgentDetails(String appl_no, FeeAgentRegnDobj dto, TransactionManager tmgr) throws SQLException {
        PreparedStatement ps;
        String sql = "INSERT INTO " + TableList.VA_AGENT_DETAILS + " (\n"
                + "appl_no, agent_name, f_name, add1, add2, city, district, pincode, padd1,"
                + " padd2, pcity, pdistrict, ppincode, contact_no, valid_fr, valid_to, "
                + " name_of_counter, place_of_business,deal_cd, op_dt, state_cd, off_cd)"
                + " VALUES (?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp, ?, ?)";
        int i = 1;
        ps = tmgr.prepareStatement(sql);
        ps.setString(i++, appl_no);
        ps.setString(i++, dto.getOwnName().toUpperCase());
        ps.setString(i++, dto.getFname().toUpperCase());
        ps.setString(i++, dto.getCurrAdd1());
        ps.setString(i++, dto.getCurrAdd2());
        ps.setString(i++, dto.getCity());
        ps.setInt(i++, dto.getC_district());
        ps.setInt(i++, dto.getC_pincode());
        ps.setString(i++, dto.getPcurrAdd1());
        ps.setString(i++, dto.getPcurrAdd2());
        ps.setString(i++, dto.getPcity());
        ps.setInt(i++, dto.getP_district());
        ps.setInt(i++, dto.getC_pincode());
        ps.setString(i++, dto.getMobileNo());
        ps.setDate(i++, new java.sql.Date(dto.getValidFrom().getTime()));
        ps.setDate(i++, new java.sql.Date(dto.getValidUpTo().getTime()));
        ps.setString(i++, dto.getCounter().toUpperCase());
        ps.setString(i++, dto.getPlaceOfBusiness().toUpperCase());
        ps.setString(i++, Util.getEmpCode());
        ps.setString(i++, Util.getUserStateCode());
        ps.setInt(i++, Util.getUserOffCode());
        ps.executeUpdate();
    }

    public void insertIntoVhaAgentDetails(String appl_no, TransactionManager tmgr) throws SQLException {
        PreparedStatement ps;
        String sql = "INSERT INTO " + TableList.VHA_AGENT_DETAILS + " (\n"
                + "           moved_on,moved_by, state_cd, off_cd,appl_no, agent_name, f_name, add1, add2, city, district, pincode, \n"
                + "            padd1, padd2, pcity, pdistrict, ppincode, contact_no,\n"
                + "            valid_fr, valid_to, name_of_counter, place_of_business \n"
                + "            ) SELECT current_timestamp,? ,state_cd, off_cd,appl_no, agent_name, f_name, add1, add2, city, district, \n"
                + "       pincode, padd1, padd2, pcity, pdistrict, ppincode, contact_no, \n"
                + "       valid_fr, valid_to, name_of_counter, place_of_business\n"
                + "  FROM " + TableList.VA_AGENT_DETAILS + " where state_cd=? and off_cd=? and appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, Util.getUserStateCode());
        ps.setInt(3, Util.getUserOffCode());
        ps.setString(4, appl_no);
        ps.executeUpdate();
    }

    public String saveAgentDetails(FeeAgentRegnDobj feeAgentRegnDobj, String stateCD, int offCD, String transactionName) throws VahanException {
        String appl_no = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("saveAgentDetails");
            appl_no = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
            feeAgentRegnDobj.setAppl_no(appl_no);
            insertIntoVaAgentDetails(appl_no, feeAgentRegnDobj, tmgr);
            Status_dobj status = new Status_dobj();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            String dt = sdf.format(new java.util.Date());
            status.setAppl_dt(dt);
            status.setAppl_no(appl_no);
            if (CommonUtils.isNullOrBlank(transactionName)) {
                status.setPur_cd(TableConstants.AGENT_DETAIL_PUR_CD);
                status.setRegn_no("NEW");
            } else if (transactionName.equalsIgnoreCase("Renew_Agent_Licence")) {
                status.setPur_cd(TableConstants.AGENT_DETAIL_REN_PUR_CD);
                status.setRegn_no(feeAgentRegnDobj.getLicence_No());
            } else {
                status.setPur_cd(TableConstants.AGENT_DETAIL_DUP_PUR_CD);
                status.setRegn_no(feeAgentRegnDobj.getLicence_No());
            }
            status.setFlow_slno(1);
            status.setFile_movement_slno(1);
            status.setAction_cd(TableConstants.AGENT_DETAIL_APPLICATION);
            status.setEmp_cd(Long.parseLong(Util.getEmpCode()));
            status.setOffice_remark("");
            status.setPublic_remark("");
            status.setState_cd(Util.getUserStateCode());
            status.setOff_cd(Util.getUserOffCode());
            ServerUtil.fileFlowForNewApplication(tmgr, status);
            status.setStatus(TableConstants.STATUS_COMPLETE);
            ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, appl_no, TableConstants.AGENT_DETAIL_FEE, status.getPur_cd(), null, tmgr);
            ServerUtil.fileFlow(tmgr, status);
            tmgr.commit();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
        return appl_no;
    }

    public FeeAgentRegnDobj getAgentData(FeeAgentRegnDobj dobj, String state_code, int off_cd) throws VahanException {
        TransactionManager tmgr = null;
        String FetchSQL = "select * from " + TableList.VA_AGENT_DETAILS + " where state_cd=? and off_cd=? and appl_no=?";
        try {
            tmgr = new TransactionManager("getAgentData");
            PreparedStatement ps = tmgr.prepareStatement(FetchSQL);
            ps.setString(1, state_code);
            ps.setInt(2, off_cd);
            ps.setString(3, dobj.getAppl_no());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj.setOwnName(rs.getString("agent_name"));
                dobj.setFname(rs.getString("f_name"));
                dobj.setMobileNo(rs.getString("contact_no"));
                dobj.setCity(rs.getString("city"));
                dobj.setC_district(rs.getInt("district"));
                dobj.setCurrAdd1(rs.getString("add1"));
                dobj.setCurrAdd2(rs.getString("add2"));
                dobj.setC_pincode(rs.getInt("pincode"));
                dobj.setPcity(rs.getString("pcity"));
                dobj.setP_district(rs.getInt("pdistrict"));
                dobj.setPcurrAdd1(rs.getString("padd1"));
                dobj.setPcurrAdd2(rs.getString("padd2"));
                dobj.setP_pincode(rs.getInt("pincode"));
                dobj.setValidFrom(rs.getDate("valid_fr"));
                dobj.setValidUpTo(rs.getDate("valid_to"));
                dobj.setCounter(rs.getString("name_of_counter"));
                dobj.setPlaceOfBusiness(rs.getString("place_of_business"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
        return dobj;
    }

    public FeeAgentRegnDobj agentData(String licNo, String state_code, int off_cd) throws VahanException {
        FeeAgentRegnDobj dobj = null;
        TransactionManager tmgr = null;
        String FetchSQL = "select * from " + TableList.VT_AGENT_DETAILS + " where agent_licence_no=? and state_cd=? and off_cd=?";
        try {
            tmgr = new TransactionManager("AgentData");
            PreparedStatement ps = tmgr.prepareStatement(FetchSQL);
            ps.setString(1, licNo);
            ps.setString(2, state_code);
            ps.setInt(3, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new FeeAgentRegnDobj();
                dobj.setOwnName(rs.getString("agent_name"));
                dobj.setFname(rs.getString("f_name"));
                dobj.setLicence_No(rs.getString("agent_licence_no"));
                dobj.setMobileNo(rs.getString("contact_no"));
                dobj.setCity(rs.getString("city"));
                dobj.setC_district(rs.getInt("district"));
                dobj.setCurrAdd1(rs.getString("add1"));
                dobj.setCurrAdd2(rs.getString("add2"));
                dobj.setC_pincode(rs.getInt("pincode"));
                dobj.setPcity(rs.getString("pcity"));
                dobj.setP_district(rs.getInt("pdistrict"));
                dobj.setPcurrAdd1(rs.getString("padd1"));
                dobj.setPcurrAdd2(rs.getString("padd2"));
                dobj.setP_pincode(rs.getInt("pincode"));
                dobj.setValidFrom(rs.getDate("valid_fr"));
                dobj.setValidUpTo(rs.getDate("valid_to"));
                dobj.setCounter(rs.getString("name_of_counter"));
                dobj.setPlaceOfBusiness(rs.getString("place_of_business"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
        return dobj;
    }

    public String saveAndMoveFeeAgent(Status_dobj statusParam, FeeAgentRegnDobj dobj) throws VahanException {
        Status_dobj status = statusParam;
        TransactionManager tmgr = null;
        String lic_no = "";
        try {
            tmgr = new TransactionManager("saveAndMoveFeeAgent()");
            if (status.getAction_cd() == TableConstants.AGENT_DETAIL_VERIFICATION) {
                insertIntoVhaAgentDetails(dobj.getAppl_no(), tmgr);
                status = ServerUtil.webServiceForNextStage(status, tmgr);
                ServerUtil.fileFlow(tmgr, status);
            } else if (status.getAction_cd() == TableConstants.AGENT_DETAIL_APPROVAL) {
                lic_no = insertIntoVtAgentDetails(tmgr, dobj);
                status.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status);
                ServerUtil.fileFlow(tmgr, status);
            }
            tmgr.commit();
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
        return lic_no;
    }

    public String insertIntoVtAgentDetails(TransactionManager tmgr, FeeAgentRegnDobj feeAgentRegnDobj) throws VahanException {
        PreparedStatement ps = null;
        String sqlFee = "", rcptNo = "", sqlVtAgent = "", sqlVhAgent = "", sqlUpdateVtAgent = "", sqlFeeUpdate = "", agentregno = "", lic_no = "";
        int purCd = Integer.parseInt(feeAgentRegnDobj.getPurCd());
        try {
            sqlFee = "SELECT rcpt_no from " + TableList.VP_APPL_RCPT_MAPPING + " WHERE state_cd=? and off_cd = ? and appl_no= ?";
            ps = tmgr.prepareStatement(sqlFee);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserOffCode());
            ps.setString(3, feeAgentRegnDobj.getAppl_no());

            RowSet rsFee = tmgr.fetchDetachedRowSet_No_release();
            if (rsFee.next()) {
                rcptNo = rsFee.getString("rcpt_no");
            }
            lic_no = feeAgentRegnDobj.getLicence_No();
            if (purCd == TableConstants.AGENT_DETAIL_PUR_CD) {
                agentregno = getUniqueAgentRegistrationNo(tmgr, Util.getUserStateCode(), Util.getUserOffCode());
                sqlVtAgent = "INSERT INTO " + TableList.VT_AGENT_DETAILS + " ("
                        + "            state_cd, off_cd, agent_licence_no, agent_name, f_name, add1, add2, city, district, pincode, \n"
                        + "            padd1, padd2, pcity, pdistrict, ppincode, contact_no, rcpt_no, \n"
                        + "            valid_fr, valid_to, name_of_counter, deal_cd, \n"
                        + "            op_dt, place_of_business) SELECT state_cd, off_cd, ?, agent_name, f_name, add1, add2, city, district, \n"
                        + "       pincode, padd1, padd2, pcity, pdistrict, ppincode, contact_no, \n"
                        + "       ?, valid_fr, valid_to, name_of_counter, \n"
                        + "       ?, op_dt , place_of_business \n"
                        + "  FROM " + TableList.VA_AGENT_DETAILS + " where state_cd=? and off_cd = ? and appl_no=?";
                ps = tmgr.prepareStatement(sqlVtAgent);
                ps.setString(1, agentregno);
                ps.setString(2, rcptNo);
                ps.setString(3, Util.getEmpCode());
                ps.setString(4, Util.getUserStateCode());
                ps.setInt(5, Util.getUserOffCode());
                ps.setString(6, feeAgentRegnDobj.getAppl_no());
                ps.executeUpdate();

                sqlFeeUpdate = "update " + TableList.VT_FEE + " set regn_no=? where state_cd=? and off_cd = ? and rcpt_no=?";
                ps = tmgr.prepareStatement(sqlFeeUpdate);
                ps.setString(1, agentregno);
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, Util.getUserOffCode());
                ps.setString(4, rcptNo);
                ps.executeUpdate();
            }
            if (purCd == TableConstants.AGENT_DETAIL_REN_PUR_CD) {
                sqlVhAgent = "INSERT INTO " + TableList.VH_AGENT_DETAILS
                        + "  select current_timestamp as moved_on,'ADMIN' as moved_by,  state_cd,  off_cd ,  agent_licence_no ,  agent_name ,  f_name  ,  add1  ,  add2  ,  city  ,  district  ,\n"
                        + "  pincode  ,  padd1  ,  padd2  ,  pcity  ,  pdistrict  ,  ppincode  ,  contact_no  ,  rcpt_no  ,  valid_fr ,\n"
                        + "  valid_to ,  name_of_counter, place_of_business "
                        + "  FROM " + TableList.VT_AGENT_DETAILS + " a where state_cd=? and off_cd = ? and a.agent_licence_no=?";
                ps = tmgr.prepareStatement(sqlVhAgent);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, Util.getUserOffCode());
                ps.setString(3, lic_no);
                ps.executeUpdate();

                sqlUpdateVtAgent = "UPDATE " + TableList.VT_AGENT_DETAILS
                        + " SET valid_fr=?,valid_to=?,rcpt_no='" + rcptNo + "',op_dt=current_timestamp where state_cd=? and off_cd = ? and agent_licence_no=?";
                ps = tmgr.prepareStatement(sqlUpdateVtAgent);
                ps.setDate(1, new java.sql.Date(feeAgentRegnDobj.getValidFrom().getTime()));
                ps.setDate(2, new java.sql.Date(feeAgentRegnDobj.getValidUpTo().getTime()));
                ps.setString(3, Util.getUserStateCode());
                ps.setInt(4, Util.getUserOffCode());
                ps.setString(5, lic_no);
                ps.executeUpdate();
                agentregno = lic_no;
            }

            sqlVtAgent = "Delete From " + TableList.VA_AGENT_DETAILS + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(sqlVtAgent);
            ps.setString(1, feeAgentRegnDobj.getAppl_no());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return agentregno;
    }

    public String[] saveFeeDetails(FeeAgentRegnDobj feeDobj, FeeDraftDobj feeDraftDobj, TransactionManager tmgr) throws VahanException, SQLException, Exception {
        PreparedStatement pstmtVtFee = null;
        String applNo = null;
        String applno[] = new String[3];
        String rcptNo = null, licNo = null;
        int purCd = Integer.parseInt(feeDobj.getPurCd());
        if (purCd == TableConstants.AGENT_DETAIL_PUR_CD) {
            licNo = "NEW";
        } else if (purCd == TableConstants.AGENT_DETAIL_REN_PUR_CD || purCd == TableConstants.AGENT_DETAIL_DUP_PUR_CD) {
            licNo = feeDobj.getLicence_No();
        }
        try {
            String vtFeeSQL = "INSERT INTO " + TableList.VT_FEE + "(regn_no, payment_mode, fees, fine, rcpt_no, rcpt_dt, pur_cd, "
                    + " flag, collected_by, state_cd, off_cd) "
                    + " VALUES (?, ?, ?, ?, ?,current_timestamp, ?, ?, ?, ?, ?);";
            rcptNo = Receipt_Master_Impl.generateNewRcptNo(Util.getUserOffCode(), tmgr);
            applNo = feeDobj.getAppl_no();
            applno[0] = applNo;
            applno[1] = rcptNo;
            pstmtVtFee = tmgr.prepareStatement(vtFeeSQL);
            pstmtVtFee.setString(1, licNo);
            pstmtVtFee.setString(2, feeDobj.getPaymentMode());
            pstmtVtFee.setLong(3, feeDobj.getGrandTotal());
            pstmtVtFee.setInt(4, 0);
            pstmtVtFee.setString(5, rcptNo);
            pstmtVtFee.setInt(6, Integer.parseInt(feeDobj.getPurCd()));
            pstmtVtFee.setString(7, "");
            pstmtVtFee.setString(8, Util.getEmpCode());
            pstmtVtFee.setString(9, feeDobj.getStateCD());
            pstmtVtFee.setInt(10, Util.getUserOffCode());
            pstmtVtFee.executeUpdate();
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return applno;
    }

    public String[] saveFeeDetailsInstrument(FeeAgentRegnDobj feeDobj, FeeDraftDobj feeDraftDobj, String remarks) throws VahanException {
        String[] rcpt = null;
        String mobileNo = feeDobj.getMobileNo();
        TransactionManager tmgr = null;
        Status_dobj status = new Status_dobj();
        int pur_cd = Integer.parseInt(feeDobj.getPurCd());
        try {
            tmgr = new TransactionManager("saveFeeDetailsInstrument");
            rcpt = saveFeeDetails(feeDobj, feeDraftDobj, tmgr);
            if (rcpt == null) {
                return rcpt;
            }
            Long inscd = null;
            if (feeDraftDobj != null) {
                FeeDraftimpl feeDraft_impl = new FeeDraftimpl();
                feeDraftDobj.setAppl_no(rcpt[0]);
                feeDraftDobj.setRcpt_no(rcpt[1]);
                inscd = feeDraft_impl.saveDraftDetails(feeDraftDobj, tmgr);
            }
            feeDobj.setRecpeNo(rcpt[1]);
            FeeImpl.PaymentGenInfo payInfo = getPaymentInfo(feeDobj, feeDraftDobj);
            Owner_dobj owndobj = null;
            owndobj = new Owner_dobj();
            owndobj.setOwner_name(feeDobj.getOwnName());
            feeImpl.saveRecptInstMap(inscd, feeDobj.getAppl_no(), rcpt[1], payInfo, owndobj, tmgr, remarks);
            ServerUtil.insertForQRDetails(feeDobj.getAppl_no(), null, null, rcpt[1], false, DocumentType.RECEIPT_QR, Util.getUserStateCode(), Util.getUserOffCode(), tmgr);
            status.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
            status.setAppl_no(feeDobj.getAppl_no());
            status.setAction_cd(Util.getSelectedSeat().getAction_cd());
            status.setStatus(TableConstants.STATUS_COMPLETE);
            status.setOffice_remark("");
            status.setPublic_remark("");
            status.setPur_cd(pur_cd);
            ServerUtil.webServiceForNextStage(status, TableConstants.STATUS_COMPLETE, null, rcpt[0], TableConstants.AGENT_DETAIL_VERIFICATION, pur_cd, null);
            ServerUtil.fileFlow(tmgr, status);
            tmgr.commit();
            long amtCollectionFeeTotal = 0;
            long totalAmount = 0;
            String msgMobileCollection = "";
            String purCd = "";
            String purShortForm = "";
            String officeName = ServerUtil.getOfficeName(Util.getSelectedSeat().getOff_cd(), Util.getUserStateCode());
            if (feeDobj != null) {
                if (purCd.equals("")) {
                    purCd = String.valueOf(feeDobj.getPurCd());
                } else {
                    purCd = purCd + ", " + String.valueOf(feeDobj.getPurCd());
                }
                amtCollectionFeeTotal = feeDobj.getGrandTotal();
                totalAmount = amtCollectionFeeTotal;
                msgMobileCollection =
                        "[" + feeDobj.getAppl_no().toUpperCase() + "]%0D%0A"
                        + "Received Rs." + totalAmount + "/- against " + purShortForm + " fee vide receipt no " + rcpt[1] + " dated " + JSFUtils.convertToStandardDateFormat(new java.util.Date()) + ".%0D%0A"
                        + "Thanks " + officeName;
                sendSMS(mobileNo, msgMobileCollection);
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Receipt No Genereation Failed...!!!");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return rcpt;
    }

    public FeeImpl.PaymentGenInfo getPaymentInfo(FeeAgentRegnDobj feeDobj, FeeDraftDobj feeDraftDobj) {
        FeeImpl.PaymentGenInfo payInfo = new FeeImpl.PaymentGenInfo();
        long amtTotal = 0;
        long amtDraft = 0;
        long amtCash = 0;
        long amtExcess = 0;
        if (feeDobj != null) {
            amtTotal = feeDobj.getGrandTotal();
        }
        if (feeDraftDobj != null) {
            for (PaymentCollectionDobj draftPayment : feeDraftDobj.getDraftPaymentList()) {
                amtDraft = amtDraft + Long.parseLong(draftPayment.getAmount());
            }
        }
        if (amtDraft > amtTotal) {
            amtExcess = amtDraft - amtTotal;
            amtCash = 0;
        } else {
            amtExcess = 0;
            amtCash = amtTotal - amtDraft;
        }
        payInfo.setCashAmt(amtCash);
        payInfo.setDraftAmt(amtDraft);
        payInfo.setExcessAmt(amtExcess);
        payInfo.setTotalAmt(amtTotal);

        return payInfo;
    }

    public static String getUniqueAgentRegistrationNo(TransactionManager tmgr, String stateCd, int off_cd) throws VahanException {
        String agentLicenceNo = null;
        try {
            if (stateCd == null || stateCd.isEmpty() || stateCd.length() > 2 || stateCd.length() < 2) {
                throw new VahanException("Something went wrong, please try again.");
            }

            String strSQL = "SELECT state_cd || '/' || off_cd || '/' || 'AGL' || '/' || to_char(CURRENT_DATE,'YYYY') || '/' || lpad(nextval('" + TableList.AGENT_LICENCE + "seq_v4_al_no')::varchar, 4, '0') AS agentLicenceNo  from tm_office where state_cd = ? and off_cd =?";
            PreparedStatement psmt = tmgr.prepareStatement(strSQL);
            psmt.setString(1, stateCd);
            psmt.setInt(2, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                agentLicenceNo = rs.getString("agentLicenceNo");
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in generating Agent licence No., please try again.");
        }
        if (agentLicenceNo == null) {
            throw new VahanException("Error in generating Agent licence No., please try again.");
        }
        return agentLicenceNo;
    }
}
