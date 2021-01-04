/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.java.util.DateUtilsException;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.bean.permit.AITPStateCoveredBean;
import nic.vahan.form.bean.permit.PermitRouteList;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.dobj.permit.PermitHomeAuthDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.VmPermitRouteDobj;
import nic.vahan.form.dobj.permit.AITPStateFeeDraftDobj;
import nic.vahan.form.dobj.permit.PermitCheckDetailsDobj;
import nic.vahan.form.dobj.permit.PermitTimeTableDobj;
import nic.vahan.form.impl.AxleImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.OwnerIdentificationImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class PassengerPermitDetailImpl {

    private static final Logger LOGGER = Logger.getLogger(PassengerPermitDetailImpl.class);
    public Map<String, String> confige = CommonPermitPrintImpl.getVmPermitStateConfiguration(Util.getUserStateCode());
    //RowSet rs;
    //PreparedStatement ps;

    public Map getSourcesRouteMap(String Appl_no, String TableName, String state_cd, int off_cd, boolean otherOffieRoute) {
        Map<String, String> routeMap = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("getRouteMap");
            String query;
            if (otherOffieRoute) {
                query = "select code, code || ' - ' || Floc || ' TO ' || Tloc as descr from " + TableList.VM_ROUTE_MASTER + " Where code not in (SELECT route_cd from " + TableName + " where appl_no=? and off_cd = " + off_cd + ") AND state_cd = ? AND off_cd = ? and state_to is null ORDER BY descr";
            } else {
                query = "select code, code || ' - ' || Floc || ' TO ' || Tloc as descr from " + TableList.VM_ROUTE_MASTER + " Where code not in (SELECT route_cd from " + TableName + " where appl_no=?) AND state_cd = ? AND off_cd = ? and state_to is null ORDER BY descr";
            }
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Appl_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                routeMap.put(rs.getString(1), rs.getString(2));
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
        return routeMap;
    }

    public Map getTargetRouteMap(String Appl_no, String TableName, String state_cd, int off_cd, boolean otherOffieRoute) {
        Map<String, String> priRouteMap = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("getTargetRouteMap");
            String query;
            if (otherOffieRoute) {
                query = "select code, code || ' - ' || Floc || ' TO ' || Tloc as descr from " + TableList.VM_ROUTE_MASTER + " Where code in (SELECT route_cd from " + TableName + " where appl_no=? and off_cd = " + off_cd + ") AND state_cd = ? AND off_cd = ? and state_to is null ORDER BY descr";
            } else {
                query = "select code, code || ' - ' || Floc || ' TO ' || Tloc as descr from " + TableList.VM_ROUTE_MASTER + " Where code in (SELECT route_cd from " + TableName + " where appl_no=?)AND state_cd = ? AND off_cd = ? and state_to is null ORDER BY descr";
            }
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Appl_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                priRouteMap.put(rs.getString(1), rs.getString(2));
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
        return priRouteMap;
    }

    public Map getOtherStateSourcesRouteMap(String Appl_no, String TableName, String state_cd, int off_cd, String state_to) {
        Map<String, String> routeMap = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("getOtherStateSourcesRouteMap");
            String query;
            query = "select code, code || ' - ' || Floc || ' TO ' || Tloc as descr from " + TableList.VM_ROUTE_MASTER + " Where code not in (SELECT route_cd from " + TableName + " where appl_no=?) AND state_cd = ? AND off_cd = ? AND state_to=? ORDER BY descr";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Appl_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            ps.setString(4, state_to);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                routeMap.put(rs.getString(1), rs.getString(2));
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
        return routeMap;
    }

    public Map getOtherStateTargetRouteMap(String Appl_no, String TableName, String state_cd, int off_cd, String state_to) {
        Map<String, String> priRouteMap = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("getOtherStateTargetRouteMap");
            String query;
            query = "select code, code || ' - ' || Floc || ' TO ' || Tloc as descr from " + TableList.VM_ROUTE_MASTER + " Where code in (SELECT route_cd from " + TableName + " where appl_no=?)AND state_cd = ? AND off_cd = ? AND state_to=? ORDER BY descr";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Appl_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            ps.setString(4, state_to);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                priRouteMap.put(rs.getString(1), rs.getString(2));
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
        return priRouteMap;
    }

    public String getStateTofromVaPermitRoute(String appl_no, String tableName) {
        String stateTo = null;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManagerReadOnly("getStateTofromVaPermitRoute");
            String query;
            query = "select state_to from " + TableList.VM_ROUTE_MASTER + " where code in (select route_cd from " + tableName + " where appl_no=? and state_cd=? and off_cd=?) and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, appl_no);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getUserOffCode());
            ps.setString(4, Util.getUserStateCode());
            ps.setInt(5, Util.getUserOffCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                stateTo = rs.getString("state_to");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return stateTo;
    }

    public Map getTargetAreaMap(String state_cd, int off_cd) {
        Map<String, String> priRouteMap = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("getTargetRouteMap");
            String query;

            query = "select region_cd,region from " + TableList.VM_REGION + " where state_cd=? AND off_cd=? ORDER BY region";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                priRouteMap.put(rs.getString(1), rs.getString(2));
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
        return priRouteMap;
    }

    public Map getSourcesAreaMapWithStrBuil(String stringBuilder, String state_cd, int off_cd) {
        Map<String, String> priRouteMap = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("getTargetRouteMap");
            String query;
            query = "select region_cd,region from " + TableList.VM_REGION + " where (region_cd) not in (" + stringBuilder + ") AND state_cd = ? AND off_cd = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                priRouteMap.put(rs.getString(1), rs.getString(2));
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
        return priRouteMap;
    }

    public Map getTargetAreaMapWithStrBuil(String stringBuilder, String state_cd, int off_cd) {
        Map<String, String> priRouteMap = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("getTargetRouteMap");
            String query;

            query = "select region_cd,region from " + TableList.VM_REGION + " where (region_cd) in (" + stringBuilder + ") AND state_cd = ? AND off_cd = ? ";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                priRouteMap.put(rs.getString(1), rs.getString(2));
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
        return priRouteMap;
    }

    public String detailRegn_no(String app_no, String state_cd, int off_cd) {
        String regnNo = null;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManagerReadOnly("detailRegn_no");
            String query;
            query = "select regn_no from " + TableList.VA_PERMIT + " where appl_no=? and state_cd=? and off_cd=? ";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, app_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                regnNo = rs.getString("regn_no");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return regnNo;
    }

    public boolean getvt_statueRegnno(String regn_no) {
        boolean flage = true;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("Owner_Impl");
            String query;
            query = "select regn_no from " + TableList.VT_PERMIT + " where regn_no=? ";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                flage = false;
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
        return flage;
    }

    public String get_ApplicationNumber(PermitOwnerDetailDobj Owner_dobj, PassengerPermitDetailDobj permit_Dobj,
            List route_code, PermitHomeAuthDobj auth_dobj, InsDobj ins_dobj, String preOfferNo, AITPStateCoveredBean aitpStateCovered, boolean multiApplAllow, boolean multiApplCondition, boolean updateOwnerDLandVoterId, boolean renderPeriod, String authPeriodMode, int authPeriod, List route_list, List<PermitTimeTableDobj> routeTimeList) {

        String app_no = null;
        TransactionManager tmgr = null;
        PermitHomeAuthImpl auth_impl;
        PermitCheckDetailsImpl pmtDtlsImpl = null;
        String Query = "";
        PreparedStatement ps;
        String route_cd = "0";
        try {
            tmgr = new TransactionManager("get_ApplicationNumber with insert");
            app_no = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
            if (!("").equals(app_no) && !(app_no == null)) {
                if (CommonUtils.isNullOrBlank(permit_Dobj.getRegion_covered()) && aitpStateCovered.getStateList().isEmpty() && (permit_Dobj.getPmt_type_code().equalsIgnoreCase(TableConstants.AITP)
                        || permit_Dobj.getPmt_type_code().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT))) {
                    permit_Dobj.setRegion_covered("");
                } else if (!aitpStateCovered.getStateList().isEmpty() && (permit_Dobj.getPmt_type_code().equalsIgnoreCase(TableConstants.AITP)
                        || permit_Dobj.getPmt_type_code().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT))) {
                    permit_Dobj.setRegion_covered(aitpStateCovered.getStateList().toString().subSequence(1, aitpStateCovered.getStateList().toString().length() - 1) + ",");
                }

                insert_into_vaPermit(app_no, permit_Dobj, tmgr);
                if (permit_Dobj.getRegnNo().isEmpty()
                        || permit_Dobj.getRegnNo().equalsIgnoreCase("NEW")) {
                    insert_into_vaPermitOwner(Owner_dobj, app_no, permit_Dobj, tmgr);
                }
                if (auth_dobj != null) {
                    auth_impl = new PermitHomeAuthImpl();
                    auth_dobj.setRegnNo(permit_Dobj.getRegnNo());
                    auth_dobj.setPmtNo("NEW");
                    auth_dobj.setPurCd(TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD);
                    auth_impl.insert_va_permit_home_auth(tmgr, app_no, auth_dobj);
                    if (CommonUtils.isNullOrBlank(permit_Dobj.getRegion_covered())) {
                        if (auth_dobj.getRegionCoveredNP() > 0) {
                            permit_Dobj.setRegion_covered("");
                        } else {
                            permit_Dobj.setRegion_covered(String.valueOf(auth_dobj.getRegionCoveredNP()) + ",");
                        }
                    }
                }
                if (!(permit_Dobj.getPmt_type_code().equalsIgnoreCase(TableConstants.AITP)
                        || permit_Dobj.getPmt_type_code().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT))) {
                    insert_into_vaPermitRoute(app_no, permit_Dobj, route_code, tmgr, route_list);
                }
                boolean flage = false;
                if (Integer.valueOf(permit_Dobj.getPaction_code()) == TableConstants.VM_PMT_FRESH_PUR_CD) {
                    Query = " select  action_cd,action_abbrv from vvv_purpose_action_flow \n"
                            + " where flow_srno = (select a.flow_srno \n"
                            + " from tm_purpose_action_flow a \n"
                            + " where a.state_cd = ? and a.pur_cd = ?  and a.action_cd = ? ) + 1 and  pur_cd = ? and state_cd = ? ";
                    ps = tmgr.prepareStatement(Query);
                    int i = 1;
                    ps.setString(i++, Util.getUserStateCode());
                    ps.setInt(i++, TableConstants.VM_PMT_APPLICATION_PUR_CD);
                    ps.setInt(i++, TableConstants.TM_ROLE_PMT_APPLICATION_ENTRY);
                    ps.setInt(i++, TableConstants.VM_PMT_APPLICATION_PUR_CD);
                    ps.setString(i++, Util.getUserStateCode());
                    RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        flage = true;
                    }
                }
                if (flage) {
                    if (ServerUtil.getVmvhClassAccTransportCatg(TableConstants.VM_VEHTYPE_TRANSPORT, "G").contains(Owner_dobj.getVh_class())
                            && Util.getUserStateCode().equalsIgnoreCase("TR")) {
                        newApplGetPermitFee(app_no, permit_Dobj, tmgr, Integer.parseInt(permit_Dobj.getPaction_code()), TableConstants.TM_ROLE_PMT_FEE);
                    } else {
                        int purCd = TableConstants.VM_PMT_APPLICATION_PUR_CD;
                        if (Boolean.valueOf(confige.get("allowed_sta_rto_relation"))) {
                            VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                            if (!CommonUtils.isNullOrBlank(preOfferNo)) {
                                parameters.setOFFER_LETTER("true");
                            }
                            purCd = new PassengerPermitDetailImpl().getPurCdFormStateConfig(FormulaUtils.replaceTagPermitValues(confige.get("sta_rto_condition"), parameters));
                            if (purCd == TableConstants.VM_PMT_FRESH_PUR_CD) {
                                Map<String, String> information = new PassengerPermitDetailImpl().getRegnNoWithApplNo(Util.getUserStateCode(), preOfferNo.toUpperCase());
                                if (information == null || CommonUtils.isNullOrBlank(information.get("appl_no"))) {
                                    throw new VahanException("Previous recode not found. Please contact to system administrator.");
                                }
                                CommonPermitPrintImpl.deleteFromTable(tmgr, TableList.VA_PERMIT, information.get("appl_no"));
                                CommonPermitPrintImpl.deleteFromTable(tmgr, TableList.VA_PERMIT_OWNER, information.get("appl_no"));
                                CommonPermitPrintImpl.deleteFromTable(tmgr, TableList.VA_PERMIT_HOME_AUTH, information.get("appl_no"));
                            }
                        }
                        newApplPermitApplicatonFee(app_no, permit_Dobj, tmgr, purCd, TableConstants.TM_ROLE_PMT_APPLICATION_ENTRY, false);
                    }
                } else {
                    insertVHA_PermitandUpdateVa_Permit(tmgr, permit_Dobj, app_no, null);
                    newApplGetPermitFee(app_no, permit_Dobj, tmgr, Integer.parseInt(permit_Dobj.getPaction_code()), TableConstants.TM_ROLE_PMT_APPLICATION_ENTRY);
                }
                if (ins_dobj != null) {
                    pmtDtlsImpl = new PermitCheckDetailsImpl();
                    pmtDtlsImpl.insertIntoVaInsurance(app_no, permit_Dobj.getRegnNo(), ins_dobj, tmgr);
                }
                if (Integer.valueOf(permit_Dobj.getPaction_code()) == TableConstants.VM_PMT_FRESH_PUR_CD
                        && permit_Dobj.getRegnNo().equalsIgnoreCase("NEW")
                        && (String.valueOf(confige.get("genrate_ol_appl")).equalsIgnoreCase("true"))) {
                    new PermitLOIImpl().insertVaOfferApproval(tmgr, app_no);
                } else if (Integer.valueOf(permit_Dobj.getPaction_code()) == TableConstants.VM_PMT_FRESH_PUR_CD
                        && (String.valueOf(confige.get("genrate_ol_appl")).equalsIgnoreCase("true"))
                        && !permit_Dobj.getRegnNo().equalsIgnoreCase("NEW")
                        && offerLetterFlowInRegisteredVehicle(permit_Dobj.getState_cd(), (CommonUtils.isNullOrBlank(permit_Dobj.getPmt_type_code())) ? 0 : Integer.valueOf(permit_Dobj.getPmt_type_code()), (CommonUtils.isNullOrBlank(permit_Dobj.getPmtCatg())) ? 0 : Integer.valueOf(permit_Dobj.getPmtCatg()))) {
                    new PermitLOIImpl().insertVaOfferApproval(tmgr, app_no);
                    notShowInWorkFlow(tmgr, app_no, permit_Dobj.getState_cd());
                }
                int region = checkMultiRegion(permit_Dobj.getRegion_covered(), permit_Dobj.getState_cd(), Integer.parseInt(permit_Dobj.getOff_cd()));
                if (route_code != null && route_code.size() > 0) {
                    if (route_code.get(0) instanceof String) {
                        route_cd = (String) route_code.get(0);
                    } else if (route_code.get(0) instanceof PermitRouteList) {
                        route_cd = ((PermitRouteList) route_code.get(0)).getKey();
                    }
                }
                if (Integer.valueOf(permit_Dobj.getPaction_code()) == TableConstants.VM_PMT_FRESH_PUR_CD) {
                    PermitReservationImpl.AddRunningNoByOne(Util.getUserStateCode(), Util.getUserStateCode().equalsIgnoreCase("DL") ? 0 : Util.getUserOffCode(), Integer.parseInt(permit_Dobj.getPmt_type_code()), Integer.parseInt(permit_Dobj.getPmtCatg()), Owner_dobj.getOwner_catg(), Owner_dobj.getFuelCd(), region, route_cd, tmgr);
                }
                if (multiApplAllow && multiApplCondition && Integer.parseInt(permit_Dobj.getPaction_code()) == TableConstants.VM_PMT_FRESH_PUR_CD) {
                    insert_into_vaMultiAppAllow(app_no, permit_Dobj, tmgr);
                }

                if (updateOwnerDLandVoterId) {
                    insert_into_VaOwnerIdentification(app_no, Owner_dobj, tmgr);
                }
                if (routeTimeList.size() > 0) {
                    insertPmtTimeTable(tmgr, permit_Dobj, routeTimeList, app_no, route_cd);
                }
                if (renderPeriod && Integer.valueOf(permit_Dobj.getPaction_code()) == TableConstants.VM_PMT_FRESH_PUR_CD) {
                    insertAuthPeriod(permit_Dobj, authPeriodMode, authPeriod, app_no, tmgr);
                }

                if (aitpStateCovered.isRender_payment_table_aitp() && Integer.valueOf(permit_Dobj.getPaction_code()) != TableConstants.VM_PMT_RENEWAL_PUR_CD && permit_Dobj.getPmt_type_code().equalsIgnoreCase(TableConstants.AITP) && aitpStateCovered.getPaymentList() != null && aitpStateCovered.getPaymentList().size() > 0) {
                    insertPaymentDetailForAITP(aitpStateCovered.getPaymentList(), app_no, permit_Dobj.getRegnNo(), TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD, tmgr);
                }

                tmgr.commit();
            }
        } catch (Exception e) {
            app_no = "";
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
        return app_no;
    }

    public String[] getAuthPeriod(String applNo) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        String[] strArr = null;
        try {
            sql = " select period_mode,period  FROM " + TableList.VA_PERMIT_HOME_AUTH_PERIOD
                    + " WHERE appl_no = ? and state_cd=? ";
            tmgr = new TransactionManager("getAuthPeriod");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                strArr = new String[2];
                strArr[0] = rs.getString("period_mode");
                strArr[1] = rs.getInt("period") + "";
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
        return strArr;
    }

    public void insertAuthPeriod(PassengerPermitDetailDobj permit_Dobj, String authPeriodMode, int authPeriod, String applNo, TransactionManager tmgr) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        int pos = 1;

        sql = "INSERT INTO  " + TableList.VA_PERMIT_HOME_AUTH_PERIOD + "(\n"
                + "         state_cd,off_cd,appl_no, regn_no, pur_cd, period_mode, period, op_dt)\n"
                + "    VALUES (?,?,?,?,?,?,?,current_timestamp)";

        ps = tmgr.prepareStatement(sql);
        ps.setString(pos++, Util.getUserStateCode());
        ps.setInt(pos++, Util.getSelectedSeat().getOff_cd());
        ps.setString(pos++, applNo);
        ps.setString(pos++, permit_Dobj.getRegnNo());
        ps.setInt(pos++, TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD);
        ps.setString(pos++, authPeriodMode);
        ps.setInt(pos++, authPeriod);
        ps.executeUpdate();

    }

    public void insertPaymentDetailForAITP(List<AITPStateFeeDraftDobj> paymentList, String applNo, String regnNo, int pur_cd, TransactionManager tmgr) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        int pos = 1;
        int srNo = 1;
        for (AITPStateFeeDraftDobj payment : paymentList) {
            sql = "INSERT INTO  " + TableList.VA_INSTRUMENT_AITP + "(\n"
                    + " state_cd,off_cd,appl_no, sr_no, regn_no, pay_state_cd,instrument_type, "
                    + " instrument_no, instrument_dt, instrument_amt,bank_code,branch_name,payable_to,recieved_dt,op_dt,pur_cd)\n"
                    + "    VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,current_timestamp,?)";

            ps = tmgr.prepareStatement(sql);
            ps.setString(pos++, Util.getUserStateCode());
            ps.setInt(pos++, Util.getSelectedSeat().getOff_cd());
            ps.setString(pos++, applNo);
            ps.setInt(pos++, srNo);
            ps.setString(pos++, regnNo);
            ps.setString(pos++, payment.getPay_state_cd());
            ps.setString(pos++, payment.getInstrument_type());
            ps.setString(pos++, payment.getInstrument_no());
            ps.setDate(pos++, new java.sql.Date(payment.getInstrument_dt().getTime()));
            ps.setLong(pos++, payment.getInstrument_amt());
            ps.setString(pos++, payment.getBank_code());
            ps.setString(pos++, payment.getBranch_name());
            ps.setString(pos++, payment.getPayable_to());
            ps.setDate(pos++, new java.sql.Date(payment.getRecieved_dt().getTime()));
            ps.setInt(pos++, pur_cd);
            ps.executeUpdate();
            pos = 1;
            srNo++;
        }
    }

    public void updatePaymentDetailForAITP(List<AITPStateFeeDraftDobj> paymentList, String applNo, String regnNo, int pur_cd, TransactionManager tmgr) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        int pos = 1;
        int srNo = 1;

        sql = " DELETE FROM " + TableList.VA_INSTRUMENT_AITP
                + " WHERE appl_no = ? and state_cd=? ";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, applNo);
        ps.setString(2, Util.getUserStateCode());
        ps.executeUpdate();

        for (AITPStateFeeDraftDobj payment : paymentList) {
            sql = "INSERT INTO  " + TableList.VA_INSTRUMENT_AITP + "(\n"
                    + " state_cd,off_cd,appl_no, sr_no, regn_no, pay_state_cd,instrument_type, "
                    + " instrument_no, instrument_dt, instrument_amt,bank_code,branch_name,payable_to,recieved_dt,op_dt,pur_cd)\n"
                    + "    VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,current_timestamp,?)";

            ps = tmgr.prepareStatement(sql);
            ps.setString(pos++, Util.getUserStateCode());
            ps.setInt(pos++, Util.getSelectedSeat().getOff_cd());
            ps.setString(pos++, applNo);
            ps.setInt(pos++, srNo);
            ps.setString(pos++, regnNo);
            ps.setString(pos++, payment.getPay_state_cd());
            ps.setString(pos++, payment.getInstrument_type());
            ps.setString(pos++, payment.getInstrument_no());
            ps.setDate(pos++, new java.sql.Date(payment.getInstrument_dt().getTime()));
            ps.setLong(pos++, payment.getInstrument_amt());
            ps.setString(pos++, payment.getBank_code());
            ps.setString(pos++, payment.getBranch_name());
            ps.setString(pos++, payment.getPayable_to());
            ps.setDate(pos++, new java.sql.Date(payment.getRecieved_dt().getTime()));
            ps.setInt(pos++, pur_cd);
            ps.executeUpdate();
            pos = 1;
            srNo++;
        }
    }

    public void moveVaInstrumentAitpToVtInstrumentAitp(List<AITPStateFeeDraftDobj> paymentList, String applNo, String regnNo, TransactionManager tmgr) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        RowSet rs = null;

        sql = " Select * FROM " + TableList.VT_INSTRUMENT_AITP
                + " WHERE regn_no = ? and state_cd=? ";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, regnNo);
        ps.setString(2, Util.getUserStateCode());
        rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            sql = " INSERT INTO " + TableList.VH_INSTRUMENT_AITP + " select current_timestamp as moved_on, ? moved_by, state_cd,off_cd,appl_no, sr_no, regn_no, pay_state_cd,instrument_type,"
                    + " instrument_no, instrument_dt, instrument_amt,bank_code,branch_name,payable_to,recieved_dt,op_dt,pur_cd,? FROM " + TableList.VT_INSTRUMENT_AITP
                    + " WHERE regn_no = ? and state_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, "REN");
            ps.setString(3, regnNo);
            ps.setString(4, Util.getUserStateCode());

            ps.executeUpdate();

            sql = " DELETE FROM " + TableList.VT_INSTRUMENT_AITP
                    + " WHERE regn_no = ? and state_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, Util.getUserStateCode());
            ps.executeUpdate();
        }

        sql = " INSERT INTO " + TableList.VT_INSTRUMENT_AITP + " select state_cd,off_cd,appl_no, sr_no, regn_no, pay_state_cd,instrument_type,"
                + " instrument_no, instrument_dt, instrument_amt,bank_code,branch_name,payable_to,recieved_dt,current_timestamp,pur_cd FROM " + TableList.VA_INSTRUMENT_AITP
                + " WHERE appl_no = ? and state_cd=? ";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, applNo);
        ps.setString(2, Util.getUserStateCode());
        ps.executeUpdate();

        sql = " INSERT INTO " + TableList.VHA_INSTRUMENT_AITP + " select current_timestamp as moved_on, ? moved_by, state_cd,off_cd,appl_no, sr_no, regn_no, pay_state_cd,instrument_type,"
                + " instrument_no, instrument_dt, instrument_amt,bank_code,branch_name,payable_to,recieved_dt,op_dt,pur_cd FROM " + TableList.VA_INSTRUMENT_AITP
                + " WHERE appl_no = ? and state_cd=? ";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, applNo);
        ps.setString(3, Util.getUserStateCode());
        ps.executeUpdate();

        sql = " DELETE FROM " + TableList.VA_INSTRUMENT_AITP
                + " WHERE appl_no = ? and state_cd=? ";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, applNo);
        ps.setString(2, Util.getUserStateCode());
        ps.executeUpdate();

    }

    public void insertPmtTimeTable(TransactionManager tmgr, PassengerPermitDetailDobj pass_dobj, List<PermitTimeTableDobj> list, String appl_no, String route_cd) throws SQLException {
        String query;
        PreparedStatement ps;
        query = "INSERT INTO  " + TableList.VA_PERMIT_ROUTE_TIME_TABLE + " (state_cd, off_cd, appl_no,regn_no,journey_day, route_cd, sr_no, trip,stoppage ,arrival_time,departure_time,halt,op_dt) VALUES (?, ?, ?,?, ?, ?,?,?,?,?::time,?::time,?,current_timestamp)";
        for (int j = 0; j < list.size(); j++) {
            PermitTimeTableDobj dobj = (PermitTimeTableDobj) list.get(j);
            ps = tmgr.prepareStatement(query);
            int i = 1;
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getUserOffCode());
            ps.setString(i++, appl_no);
            ps.setString(i++, pass_dobj.getRegnNo());
            ps.setInt(i++, dobj.getDay());
            ps.setString(i++, route_cd);
            ps.setInt(i++, j + 1);
            ps.setInt(i++, Integer.parseInt(pass_dobj.getNumberOfTrips()));
            ps.setString(i++, dobj.getStoppage());
            ps.setString(i++, dobj.getRoute_fr_time());
            ps.setString(i++, dobj.getRoute_to_time());
            ps.setBoolean(i++, false);
            ps.executeUpdate();
        }
    }

    public List<PermitTimeTableDobj> getPmtTimeTable(String regn_no, String appl_no, String state_cd) throws SQLException {
        String query;
        PreparedStatement ps;
        TransactionManagerReadOnly tmgr = null;
        List<PermitTimeTableDobj> listTimeTable = new ArrayList<>();
        query = "select * from " + TableList.VA_PERMIT_ROUTE_TIME_TABLE + " where state_cd=?  and regn_no=? and appl_no=? order by sr_no";
        try {
            tmgr = new TransactionManagerReadOnly("getPmtTimeTable");
            ps = tmgr.prepareStatement(query);
            ps.setString(1, state_cd);
            ps.setString(2, regn_no);
            ps.setString(3, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                PermitTimeTableDobj dobj = new PermitTimeTableDobj();
                dobj.setStoppage(rs.getString("stoppage"));
                dobj.setDay(rs.getInt("journey_day"));
                dobj.setRoute_fr_time(rs.getString("arrival_time"));
                dobj.setRoute_to_time(rs.getString("departure_time"));
                listTimeTable.add(dobj);
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
        return listTimeTable;
    }

    public void insert_into_VaOwnerIdentification(String appl_no, PermitOwnerDetailDobj Owner_dobj, TransactionManager tmgr) throws SQLException {
        OwnerIdentificationDobj ownerIdentificationDobj = new OwnerIdentificationDobj();
        ownerIdentificationDobj.setAppl_no(appl_no);
        ownerIdentificationDobj.setRegn_no(Owner_dobj.getRegn_no());
        ownerIdentificationDobj.setState_cd(Owner_dobj.getState_cd());
        ownerIdentificationDobj.setOff_cd(Owner_dobj.getOff_cd());
        ownerIdentificationDobj.setVoter_id(Owner_dobj.getVoter_id());
        ownerIdentificationDobj.setDl_no(Owner_dobj.getDl_no());
        ownerIdentificationDobj.setMobile_no(Owner_dobj.getMobile_no());
        OwnerIdentificationImpl.insertIntoOwnerIdentificationVA(tmgr, ownerIdentificationDobj);
    }

    public void updateOwnerIdentificationVT(TransactionManager tmgr, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        int pos = 1;
        sql = " select state_cd,regn_no,off_cd,dl_no,voter_id  FROM " + TableList.VA_OWNER_IDENTIFICATION
                + " WHERE appl_no = ? and state_cd=? ";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, appl_no);
        ps.setString(2, Util.getUserStateCode());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            OwnerIdentificationImpl.insertIntoOwnerIdentificationHistoryVH(tmgr, rs.getString("regn_no"), rs.getString("state_cd"), rs.getInt("off_cd"));
            sql = " UPDATE " + TableList.VT_OWNER_IDENTIFICATION
                    + "   SET voter_id=?, dl_no=?, verified_on=current_timestamp"
                    + " WHERE regn_no=? and state_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(pos++, rs.getString("voter_id"));
            ps.setString(pos++, rs.getString("dl_no"));
            ps.setString(pos++, rs.getString("regn_no"));
            ps.setString(pos++, rs.getString("state_cd"));
            ps.executeUpdate();
        }
        OwnerIdentificationImpl.insertIntoOwnerIdentificationHistory(tmgr, appl_no);
        sql = " delete  FROM " + TableList.VA_OWNER_IDENTIFICATION
                + " WHERE appl_no = ? and state_cd=? ";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, appl_no);
        ps.setString(2, Util.getUserStateCode());
        ps.executeUpdate();
    }

    public void insert_into_vaMultiAppAllow(String appl_no, PassengerPermitDetailDobj PermitDobj, TransactionManager tmgr) throws SQLException {
        String query;
        PreparedStatement ps;
        query = "INSERT INTO " + TableList.VA_PMT_MULTI_APP_ALLOW + "(state_cd, off_cd, appl_no, regn_no, permit_type,op_dt) VALUES (?, ?, ?, ?, ?,CURRENT_TIMESTAMP)";

        ps = tmgr.prepareStatement(query);
        int i = 1;
        ps.setString(i++, PermitDobj.getState_cd());
        ps.setInt(i++, Integer.parseInt(PermitDobj.getOff_cd()));
        ps.setString(i++, appl_no);
        if (PermitDobj.getRegnNo().isEmpty()) {
            ps.setString(i++, "NEW");
        } else {
            ps.setString(i++, PermitDobj.getRegnNo());
        }
        ps.setInt(i++, Integer.valueOf(PermitDobj.getPmt_type_code()));
        ps.executeUpdate();
    }

    public void insert_into_vaPermit(String appl_no, PassengerPermitDetailDobj PermitDobj, TransactionManager tmgr) throws SQLException {
        String query;
        PreparedStatement ps;
        query = "INSERT INTO " + TableList.VA_PERMIT + " (\n"
                + "            state_cd, off_cd, appl_no, regn_no, rcpt_no, period_mode, period, \n"
                + "            pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, service_type, \n"
                + "            goods_to_carry, jorney_purpose, parking, replace_date, alloted_flag, \n"
                + "            offer_no, order_no, order_by, order_dt, remarks, op_dt,pmt_no)\n"
                + "    VALUES (?, ?, ?, ?, ?, ?, ?, \n"
                + "            ?, ?, ?, ?, ?, ?, \n"
                + "            ?, ?, ?, ?, ?, \n"
                + "            ?, ?, ?, ?, ?, CURRENT_TIMESTAMP,?) ";

        ps = tmgr.prepareStatement(query);
        int i = 1;
        ps.setString(i++, PermitDobj.getState_cd());
        ps.setInt(i++, Integer.parseInt(PermitDobj.getOff_cd()));
        ps.setString(i++, appl_no);
        if (PermitDobj.getRegnNo().isEmpty()) {
            ps.setString(i++, "NEW");
        } else {
            ps.setString(i++, PermitDobj.getRegnNo());
        }
        ps.setString(i++, "");
        ps.setString(i++, PermitDobj.getPeriod_mode());
        ps.setInt(i++, Integer.parseInt(PermitDobj.getPeriod()));

        ps.setInt(i++, Integer.parseInt(PermitDobj.getPaction_code()));
        ps.setInt(i++, Integer.parseInt(PermitDobj.getPmt_type_code()));
        ps.setInt(i++, Integer.parseInt(PermitDobj.getPmtCatg()));
        ps.setInt(i++, 0);
        ps.setString(i++, PermitDobj.getRegion_covered());
        ps.setInt(i++, Integer.parseInt(PermitDobj.getServices_TYPE()));

        ps.setString(i++, PermitDobj.getGoods_TO_CARRY());
        ps.setString(i++, PermitDobj.getJoreny_PURPOSE());
        ps.setString(i++, PermitDobj.getParking());
        if (PermitDobj.getReplaceDate() != null) {
            ps.setDate(i++, new java.sql.Date(PermitDobj.getReplaceDate().getTime()));
        } else {
            ps.setDate(i++, null);
        }
        ps.setString(i++, "E"); //eNTER FACE
        ps.setString(i++, PermitDobj.getOffer_no());
        ps.setString(i++, PermitDobj.getOrder_no());
        ps.setString(i++, PermitDobj.getOrder_by());
        ps.setDate(i++, null);
        ps.setString(i++, PermitDobj.getRemarks());
        ps.setString(i++, PermitDobj.getPmt_no());
        ps.executeUpdate();
    }

    public void insert_into_vaPermitOwner(PermitOwnerDetailDobj OwnerDobj, String appl_no,
            PassengerPermitDetailDobj PermitDobj, TransactionManager tmgr) throws SQLException {
        String query;
        PreparedStatement ps;
        query = "INSERT INTO " + TableList.VA_PERMIT_OWNER + " (\n"
                + "            state_cd, off_cd, appl_no, regn_no, owner_name, f_name, c_add1, \n"
                + "            c_add2, c_add3, c_district, c_pincode, c_state, p_add1, p_add2, \n"
                + "            p_add3, p_district, p_pincode, p_state, mobile_no, email_id, \n"
                + "            vh_class, seat_cap, unld_wt, ld_wt, vch_catg, owner_ctg,fuel)\n"
                + "    VALUES (?, ?, ?, ?, ?, ?, ?, \n"
                + "            ?, ?, ?, ?, ?, ?, ?, \n"
                + "            ?, ?, ?, ?, ?, ?, \n"
                + "            ?, ?, ?, ?, ?, ?,?) ";

        ps = tmgr.prepareStatement(query);
        int i = 1;
        ps.setString(i++, Util.getUserStateCode());
        ps.setInt(i++, Util.getUserOffCode());
        ps.setString(i++, appl_no);
        ps.setString(i++, PermitDobj.getRegnNo());
        ps.setString(i++, OwnerDobj.getOwner_name());
        ps.setString(i++, OwnerDobj.getF_name());
        ps.setString(i++, OwnerDobj.getC_add1());
        ps.setString(i++, OwnerDobj.getC_add2());
        ps.setString(i++, OwnerDobj.getC_add3());
        ps.setInt(i++, OwnerDobj.getC_district());
        ps.setInt(i++, OwnerDobj.getC_pincode());
        ps.setString(i++, OwnerDobj.getC_state());
        ps.setString(i++, OwnerDobj.getP_add1());
        ps.setString(i++, OwnerDobj.getP_add2());
        ps.setString(i++, OwnerDobj.getP_add3());
        ps.setInt(i++, OwnerDobj.getP_district());
        ps.setInt(i++, OwnerDobj.getP_pincode());
        ps.setString(i++, OwnerDobj.getP_state());
        ps.setLong(i++, OwnerDobj.getMobile_no());
        ps.setString(i++, OwnerDobj.getEmail_id());
        ps.setInt(i++, OwnerDobj.getVh_class());
        ps.setInt(i++, OwnerDobj.getSeat_cap());
        ps.setInt(i++, OwnerDobj.getUnld_wt());
        ps.setInt(i++, OwnerDobj.getLd_wt());
        ps.setString(i++, OwnerDobj.getVch_catg());
        ps.setInt(i++, OwnerDobj.getOwner_catg());
        ps.setInt(i++, OwnerDobj.getFuelCd());
        ps.executeUpdate();
    }

    public void insert_into_vaPermitRoute(String appl_no, PassengerPermitDetailDobj PermitDobj, List route_code, TransactionManager tmgr, List<VmPermitRouteDobj> otherOffRoutecode) throws SQLException {
        String query;
        PreparedStatement ps;
        if (route_code.isEmpty() && Integer.parseInt(PermitDobj.getNumberOfTrips()) != 0) {
            if (!PermitDobj.getPmt_type_code().equalsIgnoreCase(TableConstants.GOODS_PERMIT)) {
                query = "INSERT INTO " + TableList.va_permit_route + " (\n"
                        + "            state_cd, off_cd, appl_no, route_cd,no_of_trips)\n"
                        + "    VALUES (?, ?, ?,?, ?); ";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, PermitDobj.getState_cd());
                ps.setInt(2, Integer.parseInt(PermitDobj.getOff_cd()));
                ps.setString(3, appl_no);
                ps.setString(4, "");
                ps.setInt(5, Integer.parseInt(PermitDobj.getNumberOfTrips()));
                ps.executeUpdate();
            }
        } else if (!route_code.isEmpty()) {
            for (Object s : route_code) {
                query = "INSERT INTO " + TableList.va_permit_route + "(\n"
                        + "            state_cd, off_cd, appl_no, route_cd,no_of_trips)\n"
                        + "    VALUES (?, ?, ?, ?, ?); ";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, PermitDobj.getState_cd());
                ps.setInt(2, Integer.parseInt(PermitDobj.getOff_cd()));
                ps.setString(3, appl_no);
                if (s instanceof String) {
                    ps.setString(4, (String) s);
                } else if (s instanceof PermitRouteList) {
                    ps.setString(4, ((PermitRouteList) s).getKey());
                }
                ps.setInt(5, Integer.parseInt(PermitDobj.getNumberOfTrips()));
                ps.executeUpdate();
            }
        }
        if (otherOffRoutecode != null && !otherOffRoutecode.isEmpty() && otherOffRoutecode.size() > 0) {
            for (int i = 0; i < otherOffRoutecode.size(); i++) {
                query = "INSERT INTO " + TableList.va_permit_route + "(\n"
                        + "            state_cd, off_cd, appl_no, route_cd,no_of_trips)\n"
                        + "    VALUES (?, ?, ?, ?, ?); ";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, PermitDobj.getState_cd());
                ps.setInt(2, otherOffRoutecode.get(i).getOff_code());
                ps.setString(3, appl_no);
                ps.setString(4, otherOffRoutecode.get(i).getRoute_code());
                ps.setInt(5, Integer.parseInt(PermitDobj.getNumberOfTrips()));
                ps.executeUpdate();
            }
        }
    }

    public void newApplGetPermitFee(String appl_no, PassengerPermitDetailDobj PermitDobj, TransactionManager tmgr, int pur_cd, int action_cd) throws VahanException {
        PreparedStatement ps;
        try {

            Status_dobj status = new Status_dobj();
            status.setAppl_no(appl_no);
            status.setPur_cd(pur_cd);
            status.setFile_movement_slno(1);
            status.setFlow_slno(1);
            status.setAction_cd(action_cd);
            if ((String) Util.getSession().getAttribute("selected_role_cd") == null) {
                status.setEmp_cd(0);
            } else {
                status.setEmp_cd(Long.parseLong(Util.getEmpCode()));
            }
            if (PermitDobj.getRegnNo().isEmpty()) {
                status.setRegn_no("NEW");
            } else {
                status.setRegn_no(PermitDobj.getRegnNo());
            }
            status.setOffice_remark("");
            status.setPublic_remark("");
            status.setStatus("N");
            status.setState_cd(PermitDobj.getState_cd());
            status.setOff_cd(Integer.parseInt(PermitDobj.getOff_cd()));
            if ((Boolean.valueOf(confige.get("allowed_sta_rto_relation"))
                    && action_cd != TableConstants.TM_ROLE_PMT_APPLICATION_APPROVAL)
                    || !Boolean.valueOf(confige.get("allowed_sta_rto_relation"))) {
                ServerUtil.fileFlowForNewApplication(tmgr, status);
            }
            status.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
            status.setStatus(TableConstants.STATUS_COMPLETE);
            status.setAppl_no(appl_no);
            status.setOffice_remark("");
            status.setPublic_remark("");

            VehicleParameters vehicleParametersForNewApp = new VehicleParameters();
            vehicleParametersForNewApp.setREGN_NO(status.getRegn_no());
            vehicleParametersForNewApp.setPERMIT_TYPE(Integer.parseInt(PermitDobj.getPmt_type_code()));
            status.setVehicleParameters(vehicleParametersForNewApp);
            status = ServerUtil.webServiceForNextStage(status, tmgr);

            String Query = "select * from " + TableList.TM_PURPOSE_ACTION_FLOW + " where  pur_cd = ? AND state_cd = ? ";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setInt(i++, TableConstants.VM_PMT_APPLICATION_PUR_CD);
            ps.setString(i++, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next() && (pur_cd == TableConstants.VM_PMT_FRESH_PUR_CD
                    || (pur_cd == TableConstants.VM_PMT_APPLICATION_PUR_CD && action_cd != TableConstants.TM_ROLE_PMT_APPLICATION_ENTRY))) {
                ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, appl_no,
                        action_cd, pur_cd, null, tmgr);
            } else {
                ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, appl_no,
                        action_cd, pur_cd, null, tmgr);
                ServerUtil.fileFlow(tmgr, status);
                if (("NEW").equalsIgnoreCase(PermitDobj.getRegnNo()) && ("DL").equalsIgnoreCase(PermitDobj.getState_cd())) {
                    notShowInWorkFlow(tmgr, appl_no, PermitDobj.getState_cd());
                }
            }
        } catch (Exception e) {
            throw new VahanException("Error in newApplGetPermitFee -->" + e);
        }
    }

    public void flowSequenceProcessForML(Status_dobj status, String regn_no) {
        if (takePenaltyDecision(regn_no)) {
            status.setFlow_slno(1);
        } else {
            status.setFlow_slno(2);
        }
    }

    public boolean takePenaltyDecision(String regn_no) {
        boolean flag = false;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        String Query;
        RowSet rs, rs1;
        try {
            tmgr = new TransactionManager("takePenaltyDecision");
            Query = "select DATE_PART('day', current_date-valid_upto) :: int as dateDiffrence,\n"
                    + "case\n"
                    + "when pmt_type=101 then 'sc_renew_before_days'\n"
                    + "when pmt_type=102 then 'cc_renew_before_days'\n"
                    + "when pmt_type=103 then 'ai_renew_before_days'\n"
                    + "when pmt_type=104 then 'psv_renew_before_days'\n"
                    + "when pmt_type=105 then 'gp_renew_before_days'\n"
                    + "when pmt_type=106 then 'np_renew_before_days'\n"
                    + "end as cofigValue,state_cd\n"
                    + "from " + TableList.VT_PERMIT + " where regn_no = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regn_no);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                tmgr = new TransactionManager("takePenaltyDecision1");
                Query = "select " + rs.getString("cofigValue") + " from " + TableList.VM_PERMIT_STATE_CONFIGURATION + " where state_cd = ?";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, rs.getString("state_cd"));
                rs1 = tmgr.fetchDetachedRowSet();
                if (rs1.next()) {
                    if ((rs.getInt("dateDiffrence") > 0) && (rs.getInt("dateDiffrence") > rs1.getInt(rs.getString("cofigValue")))) {
                        flag = true;
                    }
                }
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
        return flag;
    }

    public void newApplPermitApplicatonFee(String appl_no, PassengerPermitDetailDobj PermitDobj, TransactionManager tmgr, int pur_cd, int action_cd, boolean tripExtended) throws Exception {
        Status_dobj status = new Status_dobj();
        status.setAppl_no(appl_no);
        status.setPur_cd(pur_cd);
        status.setFlow_slno(1);
        status.setFile_movement_slno(1);
        status.setAction_cd(action_cd);
        if (Util.getSession().getAttribute("selected_role_cd") == null) {
            status.setEmp_cd(0);
        } else {
            status.setEmp_cd(Long.parseLong(Util.getEmpCode()));
        }
        if (PermitDobj.getRegnNo().isEmpty()) {
            status.setRegn_no("NEW");
        } else {
            status.setRegn_no(PermitDobj.getRegnNo());
        }
        status.setOffice_remark("");
        status.setPublic_remark("");
        status.setStatus("N");
        status.setState_cd(PermitDobj.getState_cd());
        status.setOff_cd(Integer.parseInt(PermitDobj.getOff_cd()));
        ServerUtil.fileFlowForNewApplication(tmgr, status);
        if (pur_cd == TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD && !CommonUtils.isNullOrBlank(PermitDobj.getPmt_type_code()) && (Integer.parseInt(PermitDobj.getPmt_type_code()) == TableConstants.STAGE_CARRIAGE_PERMIT)) {
            VehicleParameters vehicleParameters = new VehicleParameters();
            if (tripExtended) {
                vehicleParameters.setTRIPEXTENDED("Y");
            } else {
                vehicleParameters.setTRIPEXTENDED("N");
            }
            status.setVehicleParameters(vehicleParameters);
        }
        file_flow_In_Passenger_Vehicle(appl_no, status, tmgr, pur_cd, action_cd);
    }

    public void file_flow_In_Passenger_Vehicle(String appl_no, Status_dobj status, TransactionManager tmgr, int pur_cd, int action_cd) throws Exception {
        status.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
        status.setStatus(TableConstants.STATUS_COMPLETE);
        status.setAppl_no(appl_no);
        status.setOffice_remark("");
        status.setPublic_remark("");
        ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, appl_no,
                action_cd, pur_cd, null, tmgr);
        ServerUtil.fileFlow(tmgr, status);
    }

    public String getRouteVia(List route_code, String state_cd, int off_cd) {
        String via = "";
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("get Route Via");
            for (Object code : route_code) {
                String Query = "Select code ||': '|| via ||'.<br/><br/> ' as via from " + TableList.VM_ROUTE_MASTER + " where code = ? AND State_cd = ? AND off_cd = ?";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, (String) code);
                ps.setString(2, state_cd);
                ps.setInt(3, off_cd);
                RowSet rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    via += rs.getString("via");
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
        return via;
    }

    public int getRouteLength(List route_code, String appl_no, String tableName, String state_cd, int off_cd) throws VahanException {
        int length = 0;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        String Query = null;
        try {
            tmgr = new TransactionManager("get Route Via");
            if (CommonUtils.isNullOrBlank(appl_no) || CommonUtils.isNullOrBlank(tableName)) {
                for (Object code : route_code) {
                    Query = "Select rlength from " + TableList.VM_ROUTE_MASTER + " where code = ? AND State_cd = ? AND off_cd = ?";
                    ps = tmgr.prepareStatement(Query);
                    ps.setString(1, (String) code);
                    ps.setString(2, state_cd);
                    ps.setInt(3, off_cd);
                    RowSet rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        length += rs.getInt("rlength");
                    }
                }
            } else if (route_code == null) {
                Query = "SELECT sum(rlength) as rlength from " + TableList.VM_ROUTE_MASTER + " where code in (SELECT route_cd from " + tableName + " where appl_no = ?) and state_cd = ? and off_Cd = ?";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, appl_no);
                ps.setString(2, state_cd);
                ps.setInt(3, off_cd);
                RowSet rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    length += rs.getInt("rlength");
                }
            }
        } catch (SQLException e) {
            throw new VahanException("Record not found.Please contact the system administrator");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return length;
    }

    public String getRouteViaRouteList(String appl_no, String state_cd, String TableName, int off_cd, boolean otherOffieRoute) {
        String via = "";
        TransactionManager tmgr = null;
        PreparedStatement ps;
        String Query;
        try {
            tmgr = new TransactionManager("get Route Via");
            if (otherOffieRoute) {
                Query = "Select code ||': '|| via ||'.<br/><br/> ' as via from " + TableList.VM_ROUTE_MASTER + " where code in (SELECT route_cd from " + TableName + " where appl_no=? and off_cd= " + off_cd + ") AND State_cd = ? AND off_cd = ?";
            } else {
                Query = "Select code ||': '|| via ||'.<br/><br/> ' as via from " + TableList.VM_ROUTE_MASTER + " where code in (SELECT route_cd from " + TableName + " where appl_no=?) AND State_cd = ? AND off_cd = ?";
            }
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                via += rs.getString("via");
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
        return via;
    }

    public List getSelectePermitTypeAndGetPermitcage11(int pmt_type, String state_cd) {
        List permit_catg = null;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            permit_catg = new ArrayList();
            permit_catg.clear();
            tmgr = new TransactionManager("get Selecte Permit Type And Get Permit cage");
            String Query = "select * from " + TableList.VM_PERMIT_CATG + " where permit_type=? AND state_cd = ? order by descr";
            ps = tmgr.prepareStatement(Query);
            ps.setInt(1, pmt_type);
            ps.setString(2, state_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                permit_catg.add(new SelectItem(rs.getString("code"), rs.getString("descr")));
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
        return permit_catg;
    }

    public void insertVHA_PermitandUpdateVa_Permit(TransactionManager tmgr, PassengerPermitDetailDobj dobj, String appl_no, String role_cd) throws VahanException {
        try {
            String query;
            PreparedStatement ps;
            query = "SELECT * FROM " + TableList.VA_PERMIT + " where appl_no=? ";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                query = "INSERT INTO " + TableList.VHA_PERMIT + "(\n"
                        + "            state_cd, off_cd, appl_no, regn_no, rcpt_no, period_mode, period, \n"
                        + "            pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, service_type, \n"
                        + "            goods_to_carry, jorney_purpose, parking, replace_date, alloted_flag, \n"
                        + "            offer_no, order_no, order_by, order_dt, remarks, op_dt, moved_on, \n"
                        + "            moved_by,pmt_no)\n"
                        + "   SELECT  state_cd, off_cd, appl_no, regn_no, rcpt_no, period_mode, period, \n"
                        + "           pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, service_type, \n"
                        + "           goods_to_carry, jorney_purpose, parking, replace_date, alloted_flag, \n"
                        + "           offer_no, order_no, order_by, order_dt, ?, op_dt,clock_timestamp(),\n"
                        + "           ?,pmt_no\n"
                        + "  FROM " + TableList.VA_PERMIT + " where appl_no=? ";
                ps = tmgr.prepareStatement(query);
                if (Util.getUserCategory().equalsIgnoreCase("A")) {
                    ps.setString(1, "CHANGE BY ADMIN");
                } else {
                    ps.setString(1, rs.getString("remarks"));
                }
                ps.setString(2, Util.getEmpCode());
                ps.setString(3, appl_no);
                if (ps.executeUpdate() < 1) {
                    throw new VahanException("Some problem occurred while moving permit data");
                }

                if (dobj != null) {
                    query = "UPDATE " + TableList.VA_PERMIT + "\n"
                            + "   SET  period_mode=?, \n"
                            + "       period=?, pur_cd=?, pmt_type=?, pmt_catg=?, domain_cd=0, region_covered=?, \n"
                            + "       service_type=?, goods_to_carry=?, jorney_purpose=?, parking=?, \n"
                            + "       op_dt=CURRENT_TIMESTAMP,"
                            + "       offer_no=?, order_no=?, order_by=?, \n"
                            + "       order_dt=?,replace_date=?,alloted_flag = ?\n"
                            + " WHERE appl_no=? ";
                    ps = tmgr.prepareStatement(query);
                    int i = 1;
                    ps.setString(i++, dobj.getPeriod_mode());
                    ps.setInt(i++, Integer.parseInt(dobj.getPeriod()));
                    ps.setInt(i++, Integer.parseInt(dobj.getPaction_code()));
                    ps.setInt(i++, Integer.parseInt(dobj.getPmt_type_code()));
                    ps.setInt(i++, Integer.parseInt(dobj.getPmtCatg()));
                    if (CommonUtils.isNullOrBlank(dobj.getRegion_covered())
                            || dobj.getRegion_covered().replace(",", "").equalsIgnoreCase("0")) {
                        ps.setNull(i++, java.sql.Types.VARCHAR);
                    } else {
                        ps.setString(i++, dobj.getRegion_covered());
                    }
                    ps.setInt(i++, Integer.parseInt(dobj.getServices_TYPE()));
                    ps.setString(i++, dobj.getGoods_TO_CARRY());
                    ps.setString(i++, dobj.getJoreny_PURPOSE());
                    ps.setString(i++, dobj.getParking());
                    ps.setString(i++, dobj.getOffer_no());
                    if ((Util.getUserCategory().equalsIgnoreCase("A") && !CommonUtils.isNullOrBlank(dobj.getOrder_no()))
                            || (!CommonUtils.isNullOrBlank(role_cd) && role_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_APPLICATION_APPROVAL)) && !CommonUtils.isNullOrBlank(dobj.getOrder_no()))) {
                        ps.setString(i++, dobj.getOrder_no().toUpperCase());
                    } else {
                        ps.setString(i++, ServerUtil.getUniquePermitNo(tmgr, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), 0, 0, "A"));
                    }
                    ps.setString(i++, dobj.getOrder_by());
                    if (dobj.getAppDisboolenValue() == null) {
                        ps.setDate(i++, null);
                    } else if (dobj.getOrder_dt() == null) {
                        ps.setDate(i++, null);
                    } else {
                        ps.setDate(i++, new java.sql.Date(dobj.getOrder_dt().getTime()));
                    }
                    if (dobj.getReplaceDate() != null) {
                        ps.setDate(i++, new java.sql.Date(dobj.getReplaceDate().getTime()));
                    } else {
                        ps.setDate(i++, null);
                    }
                    if (CommonUtils.isNullOrBlank(dobj.getOffer_no())) {
                        ps.setString(i++, "E");
                    } else {
                        ps.setString(i++, "A");
                    }
                    ps.setString(i++, appl_no);
                    if (ps.executeUpdate() < 1) {
                        throw new VahanException("Some problem occurred while updating permit data");
                    }
                }
            } else {
                throw new VahanException("Data not found");
            }
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + "Appl_no=" + appl_no + ex.getStackTrace()[0]);
            throw new VahanException("There is some problem while saving data. Please contact to system administrator. \n" + ex.getMessage());
        }
    }

    public void insertIntoVTinsAndDeleteVaIns(TransactionManager tmgr, String appl_no, String regn_no) throws SQLException {
        String query;
        PreparedStatement ps;
        String ownerStateCd = "";
        int ownerOffCd = 0;
        query = "SELECT * FROM " + TableConstants.VIEW_OWNER + " WHERE REGN_NO = ? and state_cd = ?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, regn_no);
        ps.setString(2, Util.getUserStateCode());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            ownerStateCd = rs.getString("state_cd");
            ownerOffCd = rs.getInt("off_cd");
        } else {
            ownerStateCd = Util.getUserStateCode();
            ownerOffCd = Util.getSelectedSeat().getOff_cd();
        }
        query = "INSERT INTO " + TableList.VHA_INSURANCE + "(\n"
                + "            moved_on, moved_by,state_cd, off_cd, appl_no, regn_no, comp_cd,"
                + " ins_type, ins_from, ins_upto, policy_no, idv, op_dt) \n"
                + "     SELECT CURRENT_TIMESTAMP, ?, state_cd, off_cd, appl_no, regn_no, comp_cd, ins_type, ins_from, ins_upto,\n"
                + "           policy_no, idv, op_dt \n"
                + "  FROM " + TableList.VA_INSURANCE + " WHERE appl_no = ? ";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();

        query = "SELECT * FROM " + TableList.VT_INSURANCE + " WHERE regn_no = ? AND state_cd = ? AND off_cd = ?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, regn_no);
        ps.setString(2, ownerStateCd);
        ps.setInt(3, ownerOffCd);
        rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            query = "INSERT INTO " + TableList.VH_INSURANCE + " (\n"
                    + "            regn_no, comp_cd, ins_type, ins_from, ins_upto, policy_no, idv, \n"
                    + "            moved_on, moved_by,state_cd,off_cd)\n"
                    + "     SELECT regn_no, comp_cd, ins_type, ins_from, ins_upto, policy_no, idv,\n"
                    + "            CURRENT_TIMESTAMP,?,state_cd,off_cd\n"
                    + "  FROM " + TableList.VT_INSURANCE + " WHERE regn_no = ? AND state_cd = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, regn_no);
            ps.setString(3, ownerStateCd);
            ps.executeUpdate();

            query = "DELETE FROM " + TableList.VT_INSURANCE + " where regn_no = ? AND state_cd = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, regn_no);
            ps.setString(2, ownerStateCd);
            ps.executeUpdate();
        }

        query = "INSERT INTO  " + TableList.VT_INSURANCE + "(\n"
                + "            state_cd, off_cd ,regn_no, comp_cd, ins_type, ins_from, ins_upto, policy_no, idv,op_dt)\n"
                + "     SELECT ?,?,regn_no, comp_cd, ins_type, ins_from, ins_upto, policy_no, idv,current_timestamp\n"
                + "     FROM  " + TableList.VA_INSURANCE + " where appl_no = ? ";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, ownerStateCd);
        ps.setInt(2, ownerOffCd);
        ps.setString(3, appl_no);
        ps.executeUpdate();

        query = "DELETE FROM " + TableList.VA_INSURANCE + " where appl_no=?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, appl_no);
        ps.executeUpdate();
    }

    public void insertVHA_Owner_PermitandUpdateVa_Owner_Permit(TransactionManager tmgr, PermitOwnerDetailDobj OwnerDobj, String appl_no) throws SQLException, VahanException {
        String query;
        PreparedStatement ps;
        query = "SELECT * FROM " + TableList.VA_PERMIT_OWNER + " where appl_no=? ";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, appl_no);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            query = "INSERT INTO " + TableList.VHA_PERMIT_OWNER + " (\n"
                    + "            state_cd, off_cd, appl_no, regn_no, owner_name, f_name, c_add1, \n"
                    + "            c_add2, c_add3, c_district, c_pincode, c_state, p_add1, p_add2, \n"
                    + "            p_add3, p_district, p_pincode, p_state, mobile_no, email_id, \n"
                    + "            vh_class, seat_cap, unld_wt, ld_wt, vch_catg, owner_ctg, moved_on, moved_by,fuel)\n"
                    + "   SELECT   state_cd, off_cd, appl_no, regn_no, owner_name, f_name, c_add1, \n"
                    + "            c_add2, c_add3, c_district, c_pincode, c_state, p_add1, p_add2, \n"
                    + "            p_add3, p_district, p_pincode, p_state, mobile_no, email_id, \n"
                    + "            vh_class, seat_cap, unld_wt, ld_wt, vch_catg, owner_ctg,Current_timestamp,?,fuel \n"
                    + "  FROM " + TableList.VA_PERMIT_OWNER + " where appl_no=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, appl_no);
            if (ps.executeUpdate() < 1) {
                throw new VahanException("Some problem occurred while moving permit owner data");
            }
        }
        if (OwnerDobj != null) {
            query = "UPDATE " + TableList.VA_PERMIT_OWNER + " \n"
                    + "SET    owner_name=?, f_name=?, \n"
                    + "       c_add1=?, c_add2=?, c_add3=?, c_district=?, c_pincode=?, c_state=?, \n"
                    + "       p_add1=?, p_add2=?, p_add3=?, p_district=?, p_pincode=?, p_state=?, \n"
                    + "       mobile_no=?, email_id=?, vh_class=?, seat_cap=?, unld_wt=?, ld_wt=?,\n"
                    + "       vch_catg=?,owner_ctg=?,fuel=?"
                    + " WHERE appl_no=? ";

            ps = tmgr.prepareStatement(query);
            int i = 1;
            ps.setString(i++, OwnerDobj.getOwner_name());
            ps.setString(i++, OwnerDobj.getF_name());
            ps.setString(i++, OwnerDobj.getC_add1());
            ps.setString(i++, OwnerDobj.getC_add2());
            ps.setString(i++, OwnerDobj.getC_add3());
            ps.setInt(i++, OwnerDobj.getC_district());
            ps.setInt(i++, OwnerDobj.getC_pincode());
            ps.setString(i++, OwnerDobj.getC_state());
            ps.setString(i++, OwnerDobj.getP_add1());
            ps.setString(i++, OwnerDobj.getP_add2());
            ps.setString(i++, OwnerDobj.getP_add3());
            ps.setInt(i++, OwnerDobj.getP_district());
            ps.setInt(i++, OwnerDobj.getP_pincode());
            ps.setString(i++, OwnerDobj.getP_state());
            ps.setLong(i++, OwnerDobj.getMobile_no());
            ps.setString(i++, OwnerDobj.getEmail_id());
            ps.setInt(i++, OwnerDobj.getVh_class());
            ps.setInt(i++, OwnerDobj.getSeat_cap());
            ps.setInt(i++, OwnerDobj.getUnld_wt());
            ps.setInt(i++, OwnerDobj.getLd_wt());
            ps.setString(i++, OwnerDobj.getVch_catg());
            ps.setInt(i++, OwnerDobj.getOwner_catg());
            ps.setInt(i++, OwnerDobj.getFuelCd());
            ps.setString(i++, appl_no);
            if (ps.executeUpdate() < 1) {
                throw new VahanException("Some problem occurred while updating permit owner data");
            }
        }
    }

    public void insertVaToVhaMultiAppAllow(TransactionManager tmgr, PermitOwnerDetailDobj OwnerDobj, String appl_no) throws SQLException {
        String query;
        PreparedStatement ps;
        query = "SELECT * FROM " + TableList.VA_PMT_MULTI_APP_ALLOW + " where appl_no=? ";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, appl_no);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            query = "Insert into " + TableList.VHA_PMT_MULTI_APP_ALLOW + "(state_cd,off_cd,appl_no,regn_no,permit_type,op_dt,moved_on,moved_by) SELECT state_cd,off_cd,appl_no,regn_no,permit_type,op_dt,CURRENT_TIMESTAMP,? FROM " + TableList.VA_PMT_MULTI_APP_ALLOW + " WHERE APPL_NO = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, appl_no);
            ps.executeUpdate();
            query = "Delete from " + TableList.VA_PMT_MULTI_APP_ALLOW + " WHERE APPL_NO = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, appl_no);
            ps.executeUpdate();
        }
    }

    public void insertVHA_RouteandUpdateVa_Route(TransactionManager tmgr, PassengerPermitDetailDobj dobj, String appl_no, List route_code, boolean otherOffRouteAllow, List<VmPermitRouteDobj> otherOffRoutecode) throws SQLException {
        String query;
        PreparedStatement ps;
        query = "SELECT * FROM " + TableList.va_permit_route + " where appl_no = ? ";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, appl_no);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            query = "INSERT INTO " + TableList.VHA_PERMIT_ROUTE + "(\n"
                    + "            state_cd, off_cd, appl_no, route_cd, no_of_trips, moved_on, moved_by)\n"
                    + "    SELECT state_cd, off_cd, appl_no, route_cd, no_of_trips,CURRENT_TIMESTAMP,?\n"
                    + "  FROM " + TableList.va_permit_route + " where appl_no = ? ";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, appl_no);
            ps.executeUpdate();

            query = "DELETE  from  " + TableList.va_permit_route + "  WHERE appl_no=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, appl_no);
            ps.executeUpdate();
        }
        if (dobj != null && !route_code.isEmpty()) {
            for (Object s : route_code) {
                query = "INSERT INTO  " + TableList.va_permit_route + " (\n"
                        + "            state_cd, off_cd, appl_no, route_cd,no_of_trips)\n"
                        + "    VALUES (?, ?, ?, ?, ?); ";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, Util.getUserOffCode());
                ps.setString(3, appl_no);
                if (s instanceof String) {
                    ps.setString(4, (String) s);
                } else if (s instanceof PermitRouteList) {
                    ps.setString(4, ((PermitRouteList) s).getKey());
                }
                if (CommonUtils.isNullOrBlank(dobj.getNumberOfTrips())) {
                    ps.setInt(5, 0);
                } else {
                    ps.setInt(5, Integer.parseInt(dobj.getNumberOfTrips()));
                }
                ps.executeUpdate();
            }
        }

        if (dobj != null && otherOffRouteAllow && !otherOffRoutecode.isEmpty()) {
            for (Object s : otherOffRoutecode) {
                query = "INSERT INTO  " + TableList.va_permit_route + " (\n"
                        + "            state_cd, off_cd, appl_no, route_cd,no_of_trips)\n"
                        + "    VALUES (?, ?, ?, ?, ?); ";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, ((VmPermitRouteDobj) s).getOff_code());
                ps.setString(3, appl_no);
                if (s instanceof String) {
                    ps.setString(4, (String) s);
                } else if (s instanceof VmPermitRouteDobj) {
                    ps.setString(4, ((VmPermitRouteDobj) s).getRoute_code());
                }
                if (CommonUtils.isNullOrBlank(dobj.getNumberOfTrips())) {
                    ps.setInt(5, 0);
                } else {
                    ps.setInt(5, Integer.parseInt(dobj.getNumberOfTrips()));
                }
                ps.executeUpdate();
            }
        }

    }

    public String update_in_all_tables(PermitOwnerDetailDobj owner_dobj, PassengerPermitDetailDobj dobj, PermitHomeAuthDobj auth_dobj, List route_code, String appl_no, Status_dobj status_dobj, String CompairChange, String role_cd, String New_regn_no, boolean updateOwnerDLandVoterId, boolean renderPeriod, String authPeriodMode, int authPeriod, AITPStateCoveredBean statecoveredBean, boolean otherOffRouteAllow, List<VmPermitRouteDobj> otherOffRoutecode, List listRouteTime) throws VahanException {

        String requiredValue = null;
        TransactionManager tmgr = null;
        String Query;
        Status_dobj statDobj = status_dobj;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("update_in_all_tables");
            if (auth_dobj == null || auth_dobj.getAuthUpto() == null || auth_dobj.getAuthFrom() == null) {
                auth_dobj = null;
            }
            if (!("").equals(appl_no) && !(appl_no == null)) {
                reEntryFormEdit(role_cd, dobj, tmgr, auth_dobj, appl_no);
                if (!CompairChange.isEmpty()) {
                    if (("NEW").equalsIgnoreCase(dobj.getRegnNo())) {
                        insertVHA_Owner_PermitandUpdateVa_Owner_Permit(tmgr, owner_dobj, appl_no);
                    }
                    insertVHA_RouteandUpdateVa_Route(tmgr, dobj, appl_no, route_code, otherOffRouteAllow, otherOffRoutecode);
                }
                if (role_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_APPLICATION_APPROVAL))) {
                    if (("A").equalsIgnoreCase(dobj.getAppDisboolenValue())) {
                        if (Integer.parseInt(dobj.getPmtCatg()) == -1) {
                            dobj.setPmtCatg("0");
                        }
                        String trans_catg = "";
                        boolean trans_flage = false;
                        Query = "  select b.transport_catg,a.pmt_offer_flag from " + TableList.VM_PERMIT_CATG + " a inner join " + TableList.VM_PERMIT_TYPE + " b ON a.permit_type=b.code where a.state_cd=? and a.permit_type=? and a.code=?";
                        ps = tmgr.prepareStatement(Query);
                        ps.setString(1, Util.getUserStateCode());
                        ps.setInt(2, Integer.parseInt(dobj.getPmt_type_code()));
                        ps.setInt(3, Integer.parseInt(dobj.getPmtCatg()));
                        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                        if (rs.next()) {
                            trans_catg = rs.getString(1);
                            trans_flage = rs.getBoolean(2);
                        } else if (Util.getUserStateCode().equalsIgnoreCase("TR")) {
                            trans_catg = ServerUtil.getTransportVchType(owner_dobj.getVh_class());
                            trans_flage = true;
                        }
                        if ((("P").equalsIgnoreCase(trans_catg)) && (trans_flage)) {
                            statDobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
                            ServerUtil.fileFlow(tmgr, statDobj);
                            status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                            ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                            String offerLetterNo = ServerUtil.getUniquePermitNo(tmgr, Util.getUserStateCode(), Util.getUserOffCode(), Integer.parseInt(dobj.getPmt_type_code()), 0, "O");
                            if (CommonUtils.isNullOrBlank(offerLetterNo) || ("").equalsIgnoreCase(offerLetterNo)) {
                                throw new VahanException("Offer Letter Number not genrated.");
                            } else {
                                dobj.setOffer_no(offerLetterNo);
                                String docId = CommonPermitPrintImpl.getPermitDocIdForQuery(CommonPermitPrintImpl.getPermitDocId(tmgr, 0, TableConstants.VM_PMT_APPLICATION_PUR_CD, Util.getUserStateCode()));
                                String[] beanData = {docId, appl_no, dobj.getRegnNo()};
                                CommonPermitPrintImpl.insertVaPermitPrint(tmgr, beanData);
                                String message = "Permit Order No " + offerLetterNo + " generated against Application No " + appl_no;
                                ServerUtil.insertUsersTransactionMessage(message, 1, tmgr);
                            }
                            requiredValue = offerLetterNo;
                        } else if ((("G").equalsIgnoreCase(trans_catg)) && !(trans_flage)) {
                            requiredValue = null;
                        } else if ((("G").equalsIgnoreCase(trans_catg)) && (trans_flage) && Util.getUserStateCode().equalsIgnoreCase("ML")) {
                            statDobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
                            ServerUtil.fileFlow(tmgr, statDobj);
                            status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                            ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                            String offerLetterNo = ServerUtil.getUniquePermitNo(tmgr, Util.getUserStateCode(), Util.getUserOffCode(), Integer.parseInt(dobj.getPmt_type_code()), 0, "O");
                            if (CommonUtils.isNullOrBlank(offerLetterNo) || ("").equalsIgnoreCase(offerLetterNo)) {
                                throw new VahanException("Offer Letter Number not genrated.");
                            } else {
                                dobj.setOffer_no(offerLetterNo);
                                String docId = CommonPermitPrintImpl.getPermitDocIdForQuery(CommonPermitPrintImpl.getPermitDocId(tmgr, 0, TableConstants.VM_PMT_APPLICATION_PUR_CD, Util.getUserStateCode()));
                                String[] beanData = {docId, appl_no, dobj.getRegnNo()};
                                CommonPermitPrintImpl.insertVaPermitPrint(tmgr, beanData);
                                String message = "Permit Order No " + offerLetterNo + " generated against Application No " + appl_no;
                                ServerUtil.insertUsersTransactionMessage(message, 1, tmgr);
                            }
                            requiredValue = offerLetterNo;
                        } else {
                            vha_status_to_va_status(tmgr, status_dobj);
                            if (status_dobj.getPur_cd() == TableConstants.VM_PMT_APPLICATION_PUR_CD && !CommonUtils.isNullOrBlank(trans_catg)) {
                                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                                ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                            }
                        }
                        insertVHA_PermitandUpdateVa_Permit(tmgr, dobj, appl_no, role_cd);
                        int actionCd = 0;
                        if (Boolean.valueOf(confige.get("allowed_sta_rto_relation"))
                                && role_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_APPLICATION_APPROVAL))) {
                            actionCd = Integer.valueOf(role_cd);
                        } else {
                            actionCd = TableConstants.TM_ROLE_PMT_FEE;
                        }
                        newApplGetPermitFee(appl_no, dobj, tmgr, Integer.parseInt(dobj.getPaction_code()), actionCd);

                    } else if (("D").equalsIgnoreCase(dobj.getAppDisboolenValue())) {
                        vha_status_to_va_status(tmgr, status_dobj);
                    }
                } else if (!CompairChange.isEmpty()) {
                    insertVHA_PermitandUpdateVa_Permit(tmgr, dobj, appl_no, null);
                }
                if (!role_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_APPLICATION_APPROVAL))) {
                    if (role_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_VERIFICATION))) {
                        if (!CommonUtils.isNullOrBlank(New_regn_no)) {
                            VhaDtlsTOVaDtls(appl_no, New_regn_no.toUpperCase(), Util.getUserStateCode(), tmgr);
                            updateVaNpDetails(tmgr, appl_no, New_regn_no.toUpperCase(), Util.getUserStateCode());
                        }
                        PreparedStatement ps1;
                        Query = "SELECT regn_no FROM " + TableList.VA_PERMIT + " where state_cd=? and appl_no=? ";
                        ps1 = tmgr.prepareStatement(Query);
                        ps1.setString(1, Util.getUserStateCode());
                        ps1.setString(2, appl_no);
                        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                        if (rs.next()) {
                            if (rs.getString("regn_no").equalsIgnoreCase("NEW")) {
                                throw new VahanException("File can not be Move due to Vehicle number not Found.");
                            } else {
                                statDobj = status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
                                ServerUtil.fileFlow(tmgr, statDobj);

                            }
                        }
                    } else {
                        statDobj = status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
                        ServerUtil.fileFlow(tmgr, statDobj);

                    }
                }
                insertIntoVhaChangedData(tmgr, appl_no, CompairChange);
                Map<String, String> confige = CommonPermitPrintImpl.getVmPermitStateConfiguration(Util.getUserStateCode());
                if (Integer.valueOf(dobj.getPaction_code()) == TableConstants.VM_PMT_FRESH_PUR_CD
                        && dobj.getRegnNo().equalsIgnoreCase("NEW")
                        && (String.valueOf(confige.get("genrate_ol_appl")).equalsIgnoreCase("true"))) {
                    PermitLOIImpl impl = new PermitLOIImpl();
                    impl.moveInHistTable(tmgr, appl_no);
                    impl.deleteVaOfferApproval(tmgr, appl_no);
                    impl.insertVaOfferApproval(tmgr, appl_no);
                }

                if (updateOwnerDLandVoterId) {
                    updateOwnerIdentificationVa(appl_no, owner_dobj, tmgr);
                }
                if (listRouteTime.size() > 0 && CompairChange.contains("TimeTable")) {
                    String route_cd = "";
                    insertPmtTimeTableToVhaFromVa(tmgr, appl_no);
                    Object s = route_code.get(0);
                    if (s instanceof String) {
                        route_cd = (String) s;
                    } else if (s instanceof PermitRouteList) {
                        route_cd = ((PermitRouteList) s).getKey();
                    }
                    insertPmtTimeTable(tmgr, dobj, listRouteTime, appl_no, route_cd);
                }
                if (renderPeriod && role_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_SCRUTINY))) {
                    new PermitHomeAuthImpl().va_permit_home_period_VHA_permit_homePeriod(tmgr, appl_no, dobj.getRegnNo().toUpperCase());
                    String[] strArr = getAuthPeriod(appl_no);
                    if (strArr == null) {
                        insertAuthPeriod(dobj, authPeriodMode, authPeriod, appl_no, tmgr);
                    } else {
                        updateAuthPeriod(authPeriodMode, authPeriod, appl_no, tmgr);
                    }
                }

                if (Integer.valueOf(dobj.getPaction_code()) != TableConstants.VM_PMT_RENEWAL_PUR_CD && statecoveredBean != null && statecoveredBean.isRender_payment_table_aitp() && dobj.getPmt_type_code().equalsIgnoreCase(TableConstants.AITP) && statecoveredBean.getPaymentList() != null && statecoveredBean.getPaymentList().size() > 0) {
                    updatePaymentDetailForAITP(statecoveredBean.getPaymentList(), appl_no, dobj.getRegnNo(), TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD, tmgr);
                }
                if (role_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_VERIFICATION)) && dobj != null && dobj.getPmt_type_code().equals(TableConstants.NATIONAL_PERMIT) && !("NEW").equalsIgnoreCase(dobj.getRegnNo())) {
                    OwnerImpl ownImpl = new OwnerImpl();
                    Owner_dobj ownDobj = ownImpl.set_Owner_appl_db_to_dobj(dobj.getRegnNo(), null, null, 0);
                    if (ownDobj != null && ownDobj.getOwner_identity() != null && ownDobj.getOwner_identity().getMobile_no() != null && ownDobj.getOwner_identity().getMobile_no() != 0) {
                        ServerUtil.sendSMS(String.valueOf(ownDobj.getOwner_identity().getMobile_no()), "Please Pay Composite fee for National permit at  https://vahan.parivahan.gov.in/npermit/  Portal for application " + appl_no + " and vehicle No " + dobj.getRegnNo() + " .");
                    }
                    ServerUtil.updateVaNPAuthFlag(appl_no, tmgr, "V");
                }

                tmgr.commit();
            }
        } catch (VahanException ve) {
            requiredValue = null;
            throw ve;
        } catch (Exception e) {
            requiredValue = null;
            LOGGER.error(e.toString() + " Appl No : " + appl_no + " Regn No : " + dobj.getRegnNo() + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                requiredValue = null;
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return requiredValue;
    }

    public void updateAuthPeriod(String authPeriodMode, int authPeriod, String applNo, TransactionManager tmgr) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        int pos = 1;
        sql = "update " + TableList.VA_PERMIT_HOME_AUTH_PERIOD + " set period_mode=?,period=?,op_dt=current_timestamp where appl_no=? and state_cd=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(pos++, authPeriodMode);
        ps.setInt(pos++, authPeriod);
        ps.setString(pos++, applNo);
        ps.setString(pos++, Util.getUserStateCode());
        ps.executeUpdate();

    }

    public String updateOfferDetailsInAllTables(PermitOwnerDetailDobj owner_dobj, PassengerPermitDetailDobj dobj, PermitHomeAuthDobj auth_dobj, List route_code, String appl_no, Status_dobj status_dobj, String CompairChange, String role_cd, String New_regn_no, boolean updateOwnerDLandVoterId, boolean otherOffRouteAllow, List<VmPermitRouteDobj> otherOffRoutecode) throws VahanException {

        String requiredValue = null;
        TransactionManager tmgr = null;
        String Query;
        Status_dobj statDobj = status_dobj;
        PreparedStatement ps1;
        try {
            tmgr = new TransactionManager("updateOfferDetailsInAllTables");
            if (auth_dobj == null || auth_dobj.getAuthUpto() == null || auth_dobj.getAuthFrom() == null) {
                auth_dobj = null;
            }
            if (!CommonUtils.isNullOrBlank(appl_no)) {
                if (!CompairChange.isEmpty()) {
                    if (("NEW").equalsIgnoreCase(dobj.getRegnNo())) {
                        insertVHA_Owner_PermitandUpdateVa_Owner_Permit(tmgr, owner_dobj, appl_no);
                    }
                    insertVHA_RouteandUpdateVa_Route(tmgr, dobj, appl_no, route_code, otherOffRouteAllow, otherOffRoutecode);
                    insertVHA_PermitandUpdateVa_Permit(tmgr, dobj, appl_no, null);
                }

                if (role_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_OFFER_VERIFICATION))) {
                    if (!CommonUtils.isNullOrBlank(New_regn_no)) {
                        VhaDtlsTOVaDtls(appl_no, New_regn_no.toUpperCase(), Util.getUserStateCode(), tmgr);
                        updateVaNpDetails(tmgr, appl_no, New_regn_no, Util.getUserStateCode());
                    }
                    Query = "SELECT regn_no FROM " + TableList.VA_PERMIT + " where state_cd=? and appl_no=? ";
                    ps1 = tmgr.prepareStatement(Query);
                    ps1.setString(1, Util.getUserStateCode());
                    ps1.setString(2, appl_no);
                    RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        if (rs.getString("regn_no").equalsIgnoreCase("NEW")) {
                            throw new VahanException("File can not be Move due to Vehicle number not Found.");
                        } else {
                            statDobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
                            ServerUtil.fileFlowForPermitOfferVerification(tmgr, statDobj);
                        }
                    }
                }
                insertIntoVhaChangedData(tmgr, appl_no, CompairChange);
                tmgr.commit();
            }
        } catch (VahanException ve) {
            requiredValue = null;
            throw ve;
        } catch (Exception e) {
            requiredValue = null;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                requiredValue = null;
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return requiredValue;
    }

    public void updateOwnerIdentificationVa(String appl_no, PermitOwnerDetailDobj owner_dobj, TransactionManager tmgr) throws SQLException {
        OwnerIdentificationDobj ownerIdentificationDobj = new OwnerIdentificationDobj();
        ownerIdentificationDobj.setAppl_no(appl_no);
        ownerIdentificationDobj.setRegn_no(owner_dobj.getRegn_no());
        ownerIdentificationDobj.setState_cd(owner_dobj.getState_cd());
        ownerIdentificationDobj.setOff_cd(owner_dobj.getOff_cd());
        ownerIdentificationDobj.setVoter_id(owner_dobj.getVoter_id());
        ownerIdentificationDobj.setDl_no(owner_dobj.getDl_no());
        OwnerIdentificationImpl.insertIntoOwnerIdentificationHistory(tmgr, appl_no);
        OwnerIdentificationImpl.updateOwnerIdentification(tmgr, ownerIdentificationDobj);
    }

    public void insertPmtTimeTableToVhaFromVa(TransactionManager tmgr, String appl_no) throws SQLException {
        String query;
        PreparedStatement ps;
        query = "INSERT INTO " + TableList.VHA_PERMIT_ROUTE_TIME_TABLE + "(\n"
                + " moved_on,moved_by,state_cd, off_cd, appl_no,regn_no,journey_day, route_cd,\n"
                + " sr_no,trip,stoppage,arrival_time,departure_time,halt,op_dt)\n"
                + " SELECT clock_timestamp(), ?, state_cd, off_cd, appl_no,regn_no,journey_day, route_cd,sr_no,trip,stoppage ,arrival_time,departure_time,halt,current_timestamp from " + TableList.VA_PERMIT_ROUTE_TIME_TABLE + " WHERE appl_no = ? ";
        ps = tmgr.prepareStatement(query);
        int i = 1;
        ps.setString(i++, Util.getEmpCode());
        ps.setString(i++, appl_no);
        ps.executeUpdate();

        query = "Delete from " + TableList.VA_PERMIT_ROUTE_TIME_TABLE + " WHERE appl_no = ? ";
        ps = tmgr.prepareStatement(query);
        i = 1;
        ps.setString(i++, appl_no);
        ps.executeUpdate();

    }

    public void reEntryFormEdit(String role_cd, PassengerPermitDetailDobj dobj, TransactionManager tmgr, PermitHomeAuthDobj auth_dobj, String appl_no) throws SQLException {
        String Query;
        PreparedStatement ps;
        PermitHomeAuthImpl auth_impl;
        if (role_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_APPLICATION_ENTRY))) {
            auth_impl = new PermitHomeAuthImpl();
            if (dobj != null && !CommonUtils.isNullOrBlank(dobj.getPmt_type_code()) && !(dobj.getPmt_type_code().equalsIgnoreCase(TableConstants.AITP)
                    || dobj.getPmt_type_code().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT))) {
                Query = "SELECT * FROM " + TableList.VA_PERMIT_HOME_AUTH + " where appl_no = ? ";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, appl_no);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    auth_impl.moveDataIntoVaToVha(tmgr, appl_no);
                    auth_impl.deleteIntoHomeAuth(tmgr, appl_no, TableList.VA_PERMIT_HOME_AUTH, null);
                }
            } else {
                Query = "SELECT * FROM " + TableList.VA_PERMIT_HOME_AUTH + " where appl_no = ? ";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, appl_no);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    auth_impl.moveDataIntoVaToVha(tmgr, appl_no);
                    if (auth_dobj == null) {
                        auth_impl.deleteIntoHomeAuth(tmgr, appl_no, TableList.VA_PERMIT_HOME_AUTH, null);
                    } else {
                        Query = "UPDATE " + TableList.VA_PERMIT_HOME_AUTH + " SET  auth_no=?, auth_fr=?, auth_to=? WHERE appl_no=? ";
                        ps = tmgr.prepareStatement(Query);
                        ps.setString(1, auth_dobj.getAuthNo());
                        ps.setDate(2, new java.sql.Date(auth_dobj.getAuthFrom().getTime()));
                        ps.setDate(3, new java.sql.Date(auth_dobj.getAuthUpto().getTime()));
                        ps.setString(4, appl_no);
                        ps.executeUpdate();
                    }
                } else {
                    if (auth_dobj != null) {
                        auth_dobj.setRegnNo(dobj.getRegnNo());
                        auth_dobj.setPmtNo("NEW");
                        auth_dobj.setPurCd(TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD);
                        auth_impl.insert_va_permit_home_auth(tmgr, appl_no, auth_dobj);
                    }
                }
            }
        }
    }

    public void approval_the_permit(String appl_no, Status_dobj status_dobj, Owner_dobj ownDobj, PassengerPermitDetailDobj passPermit, boolean renderPeriod, String authPeriodMode, String authPeriod, AITPStateCoveredBean statecoveredBean, boolean showTimeTableDetails) throws VahanException {
        TransactionManager tmgr = null;
        Date validFrom = null;
        Date validUpto = null;
        PermitHomeAuthDobj auth_dobj = null;
        PermitHomeAuthImpl auth_impl = null;
        String query, pmt_no = "", regn_no;
        boolean smartCardPrint = false;
        PreparedStatement ps;
        String strTemp = "";
        boolean pmtValidityFromRegnDt = false;

        try {
            String current_date = JSFUtils.getDateInDD_MMM_YYYY(DateUtils.parseDate(new Date()));
            tmgr = new TransactionManager("approval_the_permit");
            query = "SELECT * from " + TableList.VA_PERMIT + " where state_cd = ? and appl_no=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                int pmt_catg;
                if (rs.getInt("pmt_catg") == -1) {
                    pmt_catg = 0;
                } else {
                    pmt_catg = rs.getInt("pmt_catg");
                }
                if (status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_DISPATCH_PENDING)) {
                    ServerUtil.fileFlow(tmgr, status_dobj);
                    tmgr.commit();
                } else {
                    insertVHA_PermitandUpdateVa_Permit(tmgr, null, appl_no, null);
                    pmt_no = ServerUtil.getUniquePermitNo(tmgr, Util.getUserStateCode(), Util.getUserOffCode(), rs.getInt("pmt_type"), pmt_catg, "P");
                    if (CommonUtils.isNullOrBlank(pmt_no) || ("").equalsIgnoreCase(pmt_no)) {
                        JSFUtils.showMessagesInDialog("Permit No. not genrated", "Please Entry in Master form to genrate a permit No.", FacesMessage.SEVERITY_ERROR);
                        throw new VahanException("Criteria mismatch while generating the Permit No, please select correct Permit Type & Permit Category... ");
                    } else if (pmt_no.length() > 25) {
                        throw new VahanException("Permit No length greater than column space. Please contact the administrator");
                    } else {
                        Map<String, String> getmap = CommonPermitPrintImpl.getVmPermitStateConfiguration(Util.getUserStateCode());
                        if (Util.getUserStateCode().equalsIgnoreCase("OR")) {
                            validFrom = passPermit.getValid_from();
                        } else if (getmap.get("renewal_of_permit_valid_from_flag").equalsIgnoreCase(TableConstants.RENEWAL_VALID_FROM_FOR_FEE_SUBMITTED_DATE_FLAG)
                                && !CommonUtils.isNullOrBlank(rs.getString("rcpt_no"))) {
                            query = "select to_date(to_char(rcpt_dt,'YYYY/MM/DD'),'YYYY/MM/DD') as valid_upto  from vt_fee where regn_no = ? and rcpt_no = ? AND pur_cd = ?";
                            ps = tmgr.prepareStatement(query);
                            ps.setString(1, rs.getString("regn_no"));
                            ps.setString(2, rs.getString("rcpt_no"));
                            if (Util.getUserStateCode().equalsIgnoreCase("PB") && rs.getString("pmt_type").equalsIgnoreCase(TableConstants.NATIONAL_PERMIT)) {
                                ps.setInt(3, TableConstants.VM_PMT_APPLICATION_PUR_CD);
                            } else if ("AS,ML".contains(Util.getUserStateCode()) && rs.getString("pmt_type").equalsIgnoreCase(TableConstants.AITP)) {
                                ps.setInt(3, TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD);
                            } else {
                                ps.setInt(3, TableConstants.VM_PMT_FRESH_PUR_CD);
                            }
                            RowSet rs2 = tmgr.fetchDetachedRowSet_No_release();
                            if (rs2.next()) {
                                validFrom = rs2.getDate("valid_upto");
                            } else if (getmap.get("renewal_of_permit_valid_from_flag").equalsIgnoreCase(TableConstants.RENEWAL_VALID_FROM_FOR_FEE_SUBMITTED_DATE_FLAG)
                                    && Util.getUserStateCode().equalsIgnoreCase("KA")) {
                                query = " select b.regn_no,to_date(to_char(b.rcpt_dt,'YYYY/MM/DD'),'YYYY/MM/DD') as valid_upto from vt_fee_exempted a \n"
                                        + "inner join vt_fee b on a.rcpt_no=b.rcpt_no and a.state_cd=b.state_cd and a.off_cd=b.off_cd and a.pur_cd=b.pur_cd\n"
                                        + "where a.appl_no = ? and a.pur_cd=? and a.rcpt_no=? and a.state_cd=?";
                                ps = tmgr.prepareStatement(query);
                                ps.setString(1, rs.getString("appl_no"));
                                ps.setInt(2, TableConstants.VM_PMT_FRESH_PUR_CD);
                                ps.setString(3, rs.getString("rcpt_no"));
                                ps.setString(4, rs.getString("state_cd"));
                                RowSet rs3 = tmgr.fetchDetachedRowSet_No_release();
                                if (rs3.next()) {
                                    validFrom = rs3.getDate("valid_upto");
                                    query = "UPDATE " + TableList.VT_FEE + " SET regn_no=? WHERE rcpt_no=? and state_cd=? and regn_no=?";
                                    ps = tmgr.prepareStatement(query);
                                    ps.setString(1, rs.getString("regn_no"));
                                    ps.setString(2, rs.getString("rcpt_no"));
                                    ps.setString(3, rs.getString("state_cd"));
                                    ps.setString(4, rs3.getString("regn_no"));
                                    ps.executeUpdate();
                                }
                            }
                        } else {
                            validFrom = new Date();
                        }
                        if (validFrom == null) {
                            throw new VahanException("Permit Valid From Date is null please contact the system administrator");
                        }
                        if (!Util.getUserStateCode().equalsIgnoreCase("KA")) {
                            query = "select b.regn_no,to_date(to_char(b.rcpt_dt,'YYYY/MM/DD'),'YYYY/MM/DD') as valid_upto from vt_fee_exempted a \n"
                                    + " inner join vt_fee b on a.rcpt_no=b.rcpt_no and a.state_cd=b.state_cd and a.off_cd=b.off_cd and a.pur_cd=b.pur_cd\n"
                                    + " where a.appl_no = ? and a.pur_cd=? and a.rcpt_no=? and a.state_cd=?";
                            ps = tmgr.prepareStatement(query);
                            ps.setString(1, rs.getString("appl_no"));
                            ps.setInt(2, TableConstants.VM_PMT_FRESH_PUR_CD);
                            ps.setString(3, rs.getString("rcpt_no"));
                            ps.setString(4, rs.getString("state_cd"));
                            RowSet rs3 = tmgr.fetchDetachedRowSet_No_release();
                            if (rs3.next()) {
                                if (!CommonUtils.isNullOrBlank(rs3.getString("regn_no")) && !(rs.getString("regn_no").equalsIgnoreCase(rs3.getString("regn_no")))) {
                                    query = "UPDATE " + TableList.VT_FEE + " SET regn_no=? WHERE rcpt_no=? and state_cd=? and regn_no=?";
                                    ps = tmgr.prepareStatement(query);
                                    ps.setString(1, rs.getString("regn_no"));
                                    ps.setString(2, rs.getString("rcpt_no"));
                                    ps.setString(3, rs.getString("state_cd"));
                                    ps.setString(4, rs3.getString("regn_no"));
                                    ps.executeUpdate();
                                }
                            }
                        }
                        if (("Y").equalsIgnoreCase(rs.getString("period_mode"))) {
                            validUpto = ServerUtil.dateRange(validFrom, rs.getInt("period"), 0, -1);
                        } else if (("M").equalsIgnoreCase(rs.getString("period_mode"))) {
                            validUpto = ServerUtil.dateRange(validFrom, 0, rs.getInt("period"), -1);
                        } else if (("L").equalsIgnoreCase(rs.getString("period_mode"))) {
                            validUpto = JSFUtils.getStringToDateyyyyMMdd("2099-12-31 00:00:00");
                        }

                        pmtValidityFromRegnDt = Boolean.parseBoolean(getmap.get("validity_from_regn_dt"));
                        if (pmtValidityFromRegnDt) {
                            query = "select max(count) as counter, bool_or(cancel_permit) as can_permit,max(tr_con_count) as tran_conv_count from (\n"
                                    + " select sum(case when pur_cd = 26 then 1 else 0 end) AS count, null::boolean as cancel_permit,sum(case when pur_cd in (5,11) then 1 else 0 end) AS tr_con_count from vt_fee where regn_no = ? and pur_cd in (?,?,?) and state_cd=?\n"
                                    + " union\n"
                                    + " (select 0 as count, case when entry_status='A' then true else false end as cancel_permit,0 as tr_con_count from va_details where  state_cd=? and  regn_no = ? and pur_cd in (?,?) and entry_status='A' limit 1) \n"
                                    + " )a";
                            ps = tmgr.prepareStatement(query);
                            ps.setString(1, rs.getString("regn_no"));
                            ps.setInt(2, TableConstants.VM_PMT_FRESH_PUR_CD);
                            ps.setInt(3, TableConstants.VM_TRANSACTION_MAST_TO);
                            ps.setInt(4, TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION);
                            ps.setString(5, rs.getString("state_cd"));
                            ps.setString(6, rs.getString("state_cd"));
                            ps.setString(7, rs.getString("regn_no"));
                            ps.setInt(8, TableConstants.VM_PMT_CANCELATION_PUR_CD);
                            ps.setInt(9, TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD);
                            RowSet rs4 = tmgr.fetchDetachedRowSet_No_release();
                            if (rs4.next() && ((rs4.getInt("tran_conv_count") == 0 && (rs4.getInt("counter") > 1) || rs4.getBoolean("can_permit")) || ((rs4.getInt("counter") > 0) && rs4.getInt("tran_conv_count") > 0))) {
                                validFrom = new Date();
                                if (("Y").equalsIgnoreCase(rs.getString("period_mode"))) {
                                    validUpto = ServerUtil.dateRange(validFrom, rs.getInt("period"), 0, -1);
                                } else if (("M").equalsIgnoreCase(rs.getString("period_mode"))) {
                                    validUpto = ServerUtil.dateRange(validFrom, 0, rs.getInt("period"), -1);
                                }

                            } else {
                                if (("Y").equalsIgnoreCase(rs.getString("period_mode"))) {
                                    validUpto = ServerUtil.dateRange(ownDobj.getRegn_dt(), rs.getInt("period"), 0, -1);
                                } else if (("M").equalsIgnoreCase(rs.getString("period_mode"))) {
                                    validUpto = ServerUtil.dateRange(ownDobj.getRegn_dt(), 0, rs.getInt("period"), -1);
                                }
                            }
                        }

                        FitnessImpl fitImpl = new FitnessImpl();
                        if (rs.getInt("pmt_type") == 106 && "WB".equalsIgnoreCase(Util.getUserStateCode())) {
                            Date axelValidUpto = null;
                            ownDobj.setAxleDobj(AxleImpl.setAxleVehDetails_db_to_dobj(null, rs.getString("regn_no"), Util.getUserStateCode(), ownDobj.getOff_cd()));
                            if (ownDobj.getAxleDobj() == null) {
                                throw new VahanException("Owner Axle details not found. Please contact to system administrator.");
                            }
                            if (ownDobj.getAxleDobj().getNoOfAxle() <= 2) {
                                axelValidUpto = ServerUtil.dateRange(ownDobj.getRegn_dt(), 12, 0, -1);
                            } else {
                                axelValidUpto = ServerUtil.dateRange(ownDobj.getRegn_dt(), 15, 0, -1);
                            }
                            if ((validUpto).compareTo(axelValidUpto) > 0) {
                                validUpto = axelValidUpto;
                            }
                        } else {
                            ownDobj.setPmt_type(rs.getInt("pmt_type"));
                            ownDobj.setPmt_catg(rs.getInt("pmt_catg"));
                            int vehAge = fitImpl.getVehAgeValidity(ownDobj);
                            if (vehAge != 99) {
                                VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                                parameters.setPUR_CD(rs.getInt("pur_cd"));
                                parameters.setPERMIT_TYPE(rs.getInt("pmt_type"));
                                if (getmap.get("pmt_validity_from").equalsIgnoreCase("purchase_dt")) {
                                    strTemp = "'" + getmap.get("pmt_validity_from") + "'";
                                } else {
                                    strTemp = getmap.get("pmt_validity_from");
                                }
                                String pmtValidityFrom = FormulaUtils.getSqlFormulaValue(FormulaUtils.replaceTagPermitValues(strTemp, parameters), "approval_Renewal_of_permit");
                                if (pmtValidityFrom.equalsIgnoreCase("regn_dt")) {
                                    Date maxValidUpto = ServerUtil.dateRange(ownDobj.getRegn_dt(), vehAge, 0, -1);
                                    if (maxValidUpto != null && maxValidUpto.compareTo(validUpto) <= 0) {
                                        validUpto = maxValidUpto;
                                    }
                                } else {
                                    Date maxValidUpto = ServerUtil.dateRange(ownDobj.getPurchase_dt(), vehAge, 0, -1);
                                    if (maxValidUpto != null && maxValidUpto.compareTo(validUpto) <= 0) {
                                        validUpto = maxValidUpto;
                                    }
                                }
                            }
                        }

                        query = "SELECT pmt_no,pmt_type FROM " + TableList.VT_PERMIT + " WHERE STATE_CD = ? AND REGN_NO = ? ";
                        ps = tmgr.prepareStatement(query);
                        ps.setString(1, rs.getString("state_cd"));
                        ps.setString(2, rs.getString("regn_no"));
                        RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
                        if (rs1.next()) {
                            if (!(rs.getString("state_cd").equalsIgnoreCase("PB") && (rs1.getInt("pmt_type") == Integer.parseInt(TableConstants.GOODS_PERMIT) || rs1.getInt("pmt_type") == Integer.parseInt(TableConstants.NATIONAL_PERMIT)))) {
                                throw new VahanException("Vehicle No " + rs.getString("regn_no") + " already have the permit [" + rs1.getString("pmt_no") + "]");
                            }
                        }

                        query = "INSERT INTO " + TableList.VT_PERMIT + "(\n"
                                + "            state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                                + "            valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, \n"
                                + "            service_type, goods_to_carry, jorney_purpose, parking, replace_date, \n"
                                + "            remarks, op_dt)\n"
                                + "    SELECT  state_cd, off_cd, appl_no, ?, regn_no, CURRENT_TIMESTAMP, ?,  \n"
                                + "            ?, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, \n"
                                + "            service_type, goods_to_carry, jorney_purpose, parking, replace_date, \n"
                                + "            remarks, CURRENT_TIMESTAMP\n"
                                + "  FROM " + TableList.VA_PERMIT + " where state_cd = ? and appl_no = ? ";

                        ps = tmgr.prepareStatement(query);
                        int i = 1;
                        ps.setString(i++, pmt_no);
                        regn_no = rs.getString("regn_no");
                        ps.setDate(i++, new java.sql.Date(validFrom.getTime()));
                        ps.setDate(i++, new java.sql.Date(validUpto.getTime()));
                        ps.setString(i++, Util.getUserStateCode());
                        ps.setString(i++, appl_no);
                        ps.executeUpdate();

                        query = "DELETE FROM " + TableList.VA_PERMIT + " where state_cd = ? and appl_no=?";
                        ps = tmgr.prepareStatement(query);
                        ps.setString(1, Util.getUserStateCode());
                        ps.setString(2, appl_no);
                        ps.executeUpdate();

                        if (("HP").equalsIgnoreCase(Util.getUserStateCode()) && rs.getInt("pmt_type") == Integer.valueOf(TableConstants.NATIONAL_PERMIT)) {
                            new PermitAuthCompleteImpl().insertInPermitAuthComplete(tmgr, Util.getUserStateCode(), Util.getUserOffCode(), appl_no, regn_no, pmt_no);
                        }

                        query = "INSERT INTO " + TableList.vt_permit_route + " (state_cd,off_cd,appl_no,route_cd,no_of_trips)\n"
                                + "SELECT state_cd,off_cd,appl_no,route_cd,no_of_trips FROM " + TableList.va_permit_route + "\n"
                                + "WHERE appl_no=? ";
                        ps = tmgr.prepareStatement(query);
                        ps.setString(1, appl_no);
                        ps.executeUpdate();

                        insertVHA_RouteandUpdateVa_Route(tmgr, null, appl_no, null, false, null);

                        if (rs.getInt("pmt_type") == Integer.valueOf(TableConstants.NATIONAL_PERMIT)
                                || rs.getInt("pmt_type") == Integer.valueOf(TableConstants.AITP)) {
                            auth_impl = new PermitHomeAuthImpl();
                            query = "SELECT * FROM " + TableList.VT_PERMIT_HOME_AUTH + " WHERE regn_no=? ";
                            ps = tmgr.prepareStatement(query);
                            ps.setString(1, regn_no);
                            RowSet rsVtAuth = tmgr.fetchDetachedRowSet_No_release();
                            if (rsVtAuth.next()) {
                                auth_impl.vt_permit_home_To_VH_permit_home(tmgr, regn_no);
                            }
                            query = "SELECT * FROM " + TableList.VA_PERMIT_HOME_AUTH + " WHERE appl_no=? ";
                            ps = tmgr.prepareStatement(query);
                            ps.setString(1, appl_no);
                            RowSet homeRs = tmgr.fetchDetachedRowSet_No_release();
                            if (homeRs.next()) {
                                homeAuthInsert(tmgr, appl_no, pmt_no);
                            } else {
                                auth_dobj = new PermitHomeAuthDobj();
                                auth_dobj.setRegnNo(regn_no);
                                auth_dobj.setPmtNo(pmt_no);
                                auth_dobj.setAuthNo(ServerUtil.getUniquePermitNo(tmgr, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), rs.getInt("pmt_type"), 0, "H"));
                                auth_dobj.setAuthFrom(validFrom);
                                if (validUpto.compareTo(ServerUtil.dateRange(validFrom, 1, 0, -1)) <= 0) {
                                    auth_dobj.setAuthUpto(validUpto);
                                } else {
                                    auth_dobj.setAuthUpto(ServerUtil.dateRange(validFrom, 1, 0, -1));
                                    if (renderPeriod) {
                                        if (("M").equalsIgnoreCase(authPeriodMode) && authPeriod.equals("3")) {
                                            auth_dobj.setAuthUpto(ServerUtil.dateRange(auth_dobj.getAuthFrom(), 0, 3, -1));
                                        } else if (("M").equalsIgnoreCase(authPeriodMode) && authPeriod.equals("6")) {
                                            auth_dobj.setAuthUpto(ServerUtil.dateRange(auth_dobj.getAuthFrom(), 0, 6, -1));
                                        } else if (("D").equalsIgnoreCase(authPeriodMode) && authPeriod.equals("30")) {
                                            auth_dobj.setAuthUpto(ServerUtil.dateRange(auth_dobj.getAuthFrom(), 0, 0, 30));
                                        } else {
                                            auth_dobj.setAuthUpto(ServerUtil.dateRange(auth_dobj.getAuthFrom(), 1, 0, -1));
                                        }
                                    }
                                }
                                auth_dobj.setPurCd(TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD);
                                auth_impl.insert_va_permit_home_auth(tmgr, appl_no, auth_dobj);
                                auth_impl.InsertVaPermitHomeAuthDeleteVTPermitHomeAuth(tmgr, appl_no);
                                if (renderPeriod && rs.getInt("pmt_type") == Integer.valueOf(TableConstants.AITP)) {
                                    auth_impl.va_permit_home_period_VHA_permit_homePeriod(tmgr, appl_no, rs.getString("regn_no"));
                                    auth_impl.deleteIntoHomeAuth(tmgr, appl_no, TableList.VA_PERMIT_HOME_AUTH_PERIOD, null);
                                }
                            }
                            if ("DL,UP,GJ".contains(rs.getString("state_cd")) && rs.getInt("pmt_type") == Integer.valueOf(TableConstants.NATIONAL_PERMIT)) {
                                ServerUtil.updateNPAuthorizationDetails(regn_no, appl_no, tmgr, TableConstants.VM_PMT_FRESH_PUR_CD);
                            }
                        }
                        String[] values = CommonPermitPrintImpl.getPmtValidateMsg(Util.getUserStateCode(), TableConstants.VM_PMT_FRESH_PUR_CD, null, passPermit);
                        if (values != null && values[0].equalsIgnoreCase("TRUE")) {
                            smartCardPrint = true;
                        }
                        if (!smartCardPrint) {
                            String docId = CommonPermitPrintImpl.getPermitDocIdForQuery(CommonPermitPrintImpl.getPermitDocId(tmgr, rs.getInt("pmt_type"), rs.getInt("pur_cd"), Util.getUserStateCode()));
                            String[] beanData = {docId, appl_no, rs.getString("regn_no")};
                            CommonPermitPrintImpl.insertVaPermitPrint(tmgr, beanData);
                        }
                    }
                    query = "SELECT * FROM " + TableList.VA_INSURANCE + " WHERE APPL_NO = ?";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, appl_no);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        insertIntoVTinsAndDeleteVaIns(tmgr, appl_no, regn_no);
                    }
                    String message = "Permit " + pmt_no + " issued for Appl." + appl_no + "[" + regn_no + "]";
                    ServerUtil.insertUsersTransactionMessage(message, 1, tmgr);
                    status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
                    if (smartCardPrint) {
                        status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                        PrintPermitImpl printImpl = new PrintPermitImpl();
                        printImpl.fileFlowOfPermit(tmgr, status_dobj, appl_no, status_dobj.getPur_cd(), null);
                        ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                    } else {
                        ServerUtil.fileFlow(tmgr, status_dobj);
                        status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                        ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                    }

                    if (showTimeTableDetails) {
                        insertPmtTimeTableFromVaToVt(tmgr, appl_no);
                        insertPmtTimeTableToVhaFromVa(tmgr, appl_no);
                    }
                    disposeApplication(tmgr, regn_no);

                    if (statecoveredBean != null && statecoveredBean.isRender_payment_table_aitp() && passPermit.getPmt_type_code().equalsIgnoreCase(TableConstants.AITP) && statecoveredBean.getPaymentList() != null && statecoveredBean.getPaymentList().size() > 0) {
                        moveVaInstrumentAitpToVtInstrumentAitp(statecoveredBean.getPaymentList(), appl_no, regn_no, tmgr);
                    }
                    if (Util.getUserStateCode().equalsIgnoreCase("DL")) {
                        ServerUtil.updateOnlinePermitStatus(tmgr, status_dobj);
                    }
                    tmgr.commit();
                    if (ownDobj != null && ownDobj.getOwner_identity() != null && ownDobj.getOwner_identity().getMobile_no() != null && ownDobj.getOwner_identity().getMobile_no() != 0) {
                        ServerUtil.sendSMS(String.valueOf(ownDobj.getOwner_identity().getMobile_no()), "Permit No " + pmt_no + " for Vehicle number" + regn_no + " against Application No " + appl_no + " has been approved on " + current_date + ". Please visit issuing authority " + ServerUtil.getOfficeName(Util.getUserOffCode(), Util.getUserStateCode()) + " to collect after 2 days.");
                    }
                }
            }
        } catch (VahanException e) {
            throw new VahanException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Appl_no : " + appl_no + " --" + e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There is some problem to save permit data. Please contact to system administrator.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
    }

    public void insertPmtTimeTableFromVaToVt(TransactionManager tmgr, String appl_no) throws SQLException {
        String query;
        PreparedStatement ps;
        query = "INSERT INTO " + TableList.VT_PERMIT_ROUTE_TIME_TABLE + "(\n"
                + " state_cd, off_cd, appl_no,regn_no,journey_day, route_cd,\n"
                + " sr_no,trip,stoppage ,arrival_time,departure_time,halt,op_dt)\n"
                + " SELECT state_cd, off_cd, appl_no,regn_no,journey_day, route_cd,sr_no,trip,stoppage ,arrival_time,departure_time,halt,current_timestamp from " + TableList.VA_PERMIT_ROUTE_TIME_TABLE + " WHERE appl_no = ? ";
        ps = tmgr.prepareStatement(query);
        int i = 1;
        ps.setString(i++, appl_no);
        ps.executeUpdate();
    }

    public void disposeApplication(TransactionManager tmgr, String regn_no) throws SQLException {
        String query = "";
        PreparedStatement ps;
        SessionVariables sessionVariables = new SessionVariables();
        query = "Insert into " + TableList.VHA_PERMIT + "(state_cd, off_cd, appl_no, regn_no, rcpt_no, period_mode, period, \n"
                + "pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, service_type, \n"
                + "goods_to_carry, jorney_purpose, parking, replace_date, alloted_flag, \n"
                + "offer_no, order_no, order_by, order_dt, remarks, op_dt, moved_on, \n"
                + "moved_by, pmt_no) SELECT state_cd, off_cd, appl_no, regn_no, rcpt_no, period_mode, period, \n"
                + "pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, service_type, \n"
                + "goods_to_carry, jorney_purpose, parking, replace_date, alloted_flag, \n"
                + "offer_no, order_no, order_by, order_dt, remarks, op_dt, CURRENT_TIMESTAMP, \n"
                + "?, pmt_no FROM " + TableList.VA_PERMIT + " WHERE appl_no in (select appl_no from " + TableList.VA_PMT_MULTI_APP_ALLOW + " where regn_no=?)";

        ps = tmgr.prepareStatement(query);
        ps.setString(1, sessionVariables.getEmpCodeLoggedIn());
        ps.setString(2, regn_no);
        ps.executeUpdate();

        query = "delete from " + TableList.VA_PERMIT + " WHERE appl_no in (select appl_no from " + TableList.VA_PMT_MULTI_APP_ALLOW + " where regn_no=?)";

        ps = tmgr.prepareStatement(query);
        ps.setString(1, regn_no);
        ps.executeUpdate();

        query = "INSERT INTO " + TableList.VHA_PERMIT_ROUTE + " (state_cd, off_cd, appl_no, route_cd, no_of_trips, moved_on, moved_by) "
                + "select state_cd, off_cd, appl_no, route_cd, no_of_trips,CURRENT_TIMESTAMP,? from " + TableList.va_permit_route
                + " where appl_no in (select appl_no from " + TableList.VA_PMT_MULTI_APP_ALLOW + " where regn_no=?)";

        ps = tmgr.prepareStatement(query);
        ps.setString(1, sessionVariables.getEmpCodeLoggedIn());
        ps.setString(2, regn_no);
        ps.executeUpdate();

        query = "delete from " + TableList.va_permit_route + " WHERE appl_no in (select appl_no from " + TableList.VA_PMT_MULTI_APP_ALLOW + " where regn_no=?)";

        ps = tmgr.prepareStatement(query);
        ps.setString(1, regn_no);
        ps.executeUpdate();

        query = "INSERT INTO " + TableList.VHA_STATUS + "(state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno,"
                + "action_cd, seat_cd, cntr_id, status, office_remark, public_remark,"
                + "file_movement_type, emp_cd, op_dt, moved_on) select state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno,"
                + "action_cd, seat_cd, cntr_id, 'D', office_remark, public_remark,"
                + "file_movement_type, emp_cd, op_dt, CURRENT_TIMESTAMP from " + TableList.VA_STATUS
                + " where appl_no in (select appl_no from " + TableList.VA_PMT_MULTI_APP_ALLOW + " where regn_no=?)";

        ps = tmgr.prepareStatement(query);
        ps.setString(1, regn_no);
        ps.executeUpdate();

        query = "delete from " + TableList.VA_STATUS + " WHERE appl_no in (select appl_no from " + TableList.VA_PMT_MULTI_APP_ALLOW + " where regn_no=?)";

        ps = tmgr.prepareStatement(query);
        ps.setString(1, regn_no);
        ps.executeUpdate();

        query = "INSERT INTO " + TableList.VHA_DETAILS + "(appl_no, pur_cd, appl_dt, regn_no, user_id, user_type, entry_ip,"
                + "entry_status, confirm_ip, confirm_status, confirm_date, state_cd,"
                + "off_cd, moved_on, moved_by) select appl_no, pur_cd, appl_dt, regn_no, user_id, user_type, entry_ip,"
                + "entry_status, confirm_ip, confirm_status, confirm_date, state_cd,off_cd,CURRENT_TIMESTAMP,? from " + TableList.VA_DETAILS
                + " where appl_no in (select appl_no from " + TableList.VA_PMT_MULTI_APP_ALLOW + " where regn_no=?) and entry_status !='A'";

        ps = tmgr.prepareStatement(query);
        ps.setString(1, sessionVariables.getEmpCodeLoggedIn());
        ps.setString(2, regn_no);
        ps.executeUpdate();

        query = "delete from " + TableList.VA_DETAILS + " WHERE appl_no in (select appl_no from " + TableList.VA_PMT_MULTI_APP_ALLOW + " where regn_no=?) and entry_status !='A'";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, regn_no);
        ps.executeUpdate();

        query = "Insert into " + TableList.VHA_PMT_MULTI_APP_ALLOW + "(state_cd,off_cd,appl_no,regn_no,permit_type,op_dt,moved_on,moved_by) SELECT state_cd,off_cd,appl_no,regn_no,permit_type,op_dt,CURRENT_TIMESTAMP,? FROM " + TableList.VA_PMT_MULTI_APP_ALLOW + " WHERE REGN_NO = ?";

        ps = tmgr.prepareStatement(query);
        ps.setString(1, sessionVariables.getEmpCodeLoggedIn());
        ps.setString(2, regn_no);
        ps.executeUpdate();

        query = "Delete from " + TableList.VA_PMT_MULTI_APP_ALLOW + " WHERE REGN_NO = ?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, regn_no);
        ps.executeUpdate();

    }

    public void homeAuthInsert(TransactionManager tmgr, String appl_no, String pmt_no) throws SQLException {
        String query;
        PermitHomeAuthImpl authImpl;
        PreparedStatement ps;
        query = "INSERT INTO " + TableList.VHA_PERMIT_HOME_AUTH + "(\n"
                + "            appl_no, regn_no, pmt_no, auth_no, auth_fr, auth_to, op_dt, moved_on, \n"
                + "            moved_by,pur_cd)\n"
                + "SELECT appl_no, regn_no, pmt_no, auth_no, auth_fr, auth_to, op_dt,CURRENT_TIMESTAMP,?,pur_cd\n"
                + " FROM " + TableList.VA_PERMIT_HOME_AUTH + " WHERE appl_no = ? ";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();
        query = "UPDATE " + TableList.VA_PERMIT_HOME_AUTH + " SET pmt_no=? WHERE appl_no=? ";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, pmt_no);
        ps.setString(2, appl_no);
        ps.executeUpdate();
        authImpl = new PermitHomeAuthImpl();
        authImpl.InsertVaPermitHomeAuthDeleteVTPermitHomeAuth(tmgr, appl_no);
    }

    public void approval_Renewal_of_permit(String appl_no, Status_dobj status_dobj, Owner_dobj ownDobj, PassengerPermitDetailDobj passPermit) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        Date validFrom = null, feeValidFrom = null, validUpto = null, validFromExpiry = null;;
        String pmt_no = null, regn_no;
        Timestamp issueDtTimestamp = null;
        PermitOwnerDetailImpl ownerImpl = new PermitOwnerDetailImpl();
        Status_dobj statDobj = status_dobj;
        boolean smartCardPrint = false;
        boolean printSingleDocForDL = true;
        boolean renewAuthFlag = false;
        boolean renewAuthFeeNpRenewPermit = false;
        String strTemp = "";
        boolean pmtValidityFromRegnDt = false;

        try {
            tmgr = new TransactionManager("approval_Renewal_of_permit");
            String query;
            query = "SELECT * from " + TableList.VA_PERMIT + " where appl_no=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_DISPATCH_PENDING)) {
                    ServerUtil.fileFlow(tmgr, statDobj);
                    tmgr.commit();
                } else {
                    insertVHA_PermitandUpdateVa_Permit(tmgr, null, appl_no, null);
                    query = "DELETE FROM " + TableList.VA_PERMIT + " where appl_no=?";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, appl_no);
                    ps.executeUpdate();

                    Map<String, String> getmap = CommonPermitPrintImpl.getVmPermitStateConfiguration(Util.getUserStateCode());
                    query = "SELECT issue_dt, pmt_no,to_date(to_char(valid_upto,'YYYY/MM/DD'),'YYYY/MM/DD') as valid_upto FROM " + TableList.VT_PERMIT + " where regn_no = ? ";
                    if (!CommonUtils.isNullOrBlank(passPermit.getPmt_no())) {
                        query = query + " and pmt_no=? ";
                    }
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, rs.getString("regn_no"));
                    if (!CommonUtils.isNullOrBlank(passPermit.getPmt_no())) {
                        ps.setString(2, rs.getString("pmt_no"));
                    }
                    RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
                    if (rs1.next()) {
                        //issueDtTimestamp = new Timestamp(rs1.getDate("issue_dt").getTime());
                        issueDtTimestamp = rs1.getTimestamp("issue_dt");
                        pmt_no = rs1.getString("pmt_no");
                        validFrom = rs1.getDate("valid_upto");
                        validFrom = ServerUtil.dateRange(validFrom, 0, 0, 1); // Purpose of  RENEWAL_VALID_FROM_FOR_PRIVIOUS_PERMIT_VALID_UPTO_DATE_FLAG Auto Call
                        validFromExpiry = validFrom;
                    }
                    if (getmap.get("renewal_of_permit_valid_from_flag").equalsIgnoreCase(TableConstants.RENEWAL_VALID_FROM_FOR_FEE_SUBMITTED_DATE_FLAG)
                            && !CommonUtils.isNullOrBlank(rs.getString("rcpt_no")) && !"WB".equalsIgnoreCase(Util.getUserStateCode())) {
                        query = "select to_date(to_char(rcpt_dt,'YYYY/MM/DD'),'YYYY/MM/DD') as valid_upto  from vt_fee where regn_no = ? and rcpt_no = ? AND pur_cd = ?";
                        ps = tmgr.prepareStatement(query);
                        ps.setString(1, rs.getString("regn_no"));
                        ps.setString(2, rs.getString("rcpt_no"));
                        ps.setInt(3, TableConstants.VM_PMT_RENEWAL_PUR_CD);
                        RowSet rs2 = tmgr.fetchDetachedRowSet_No_release();
                        if (rs2.next()) {
                            feeValidFrom = rs2.getDate("valid_upto");
                            if (feeValidFrom.compareTo(validFrom) > 0) {
                                validFrom = feeValidFrom;
                            }
                        }
                    } else if (getmap.get("renewal_of_permit_valid_from_flag").equalsIgnoreCase(TableConstants.RENEWAL_VALID_FROM_FOR_APPROVAL_DATE_FLAG)) {
                        if (new Date().compareTo(validFrom) > 0) {
                            validFrom = new Date();
                        }
                    }

                    if (validFrom == null) {
                        throw new VahanException("Permit Valid From Date is null please contact the system administrator");
                    }
                    if (Util.getUserStateCode().equalsIgnoreCase("OR") && TableConstants.GCW_VEH_CLASS.contains("," + ownDobj.getVh_class() + ",")) {
                        if (ownDobj.getGcw() == 0) {
                            ownDobj.setGcw(ownerImpl.setGcwWeight(ownDobj));
                        }
                    }
                    String[] values = CommonPermitPrintImpl.getPmtValidateMsg(Util.getUserStateCode(), TableConstants.VM_PMT_RENEWAL_PUR_CD, ownDobj, passPermit);
                    if (values != null) {
                        if (values[0].equalsIgnoreCase("TRUE")) {
                            smartCardPrint = true;
                        }
                        if (!CommonUtils.isNullOrBlank(values[1])) {
                            throw new VahanException(values[1]);
                        }
                    }

//                if (Util.getUserStateCode().equalsIgnoreCase("DL") && (ownDobj.getFuel() == 2 || ownDobj.getFuel() == 12)
//                        && Integer.valueOf(TableConstants.AITP) == rs.getInt("pmt_type") && Integer.valueOf(rs.getInt("pmt_catg")) != 31
//                        && Integer.valueOf(rs.getInt("pmt_catg")) != 32) {
//                    throw new VahanException("Fuel type is diesel so that permit is not renewal");
//                }
                    if (("Y").equalsIgnoreCase(rs.getString("period_mode"))) {
                        validUpto = ServerUtil.dateRange(validFrom, rs.getInt("period"), 0, -1);
                    } else if (("M").equalsIgnoreCase(rs.getString("period_mode"))) {
                        validUpto = ServerUtil.dateRange(validFrom, 0, rs.getInt("period"), -1);
                    }

                    pmtValidityFromRegnDt = Boolean.parseBoolean(getmap.get("validity_from_regn_dt"));
                    if (pmtValidityFromRegnDt) {
                        if (("Y").equalsIgnoreCase(rs.getString("period_mode"))) {
                            validUpto = ServerUtil.dateRange(validFromExpiry, rs.getInt("period"), 0, -1);
                        } else if (("M").equalsIgnoreCase(rs.getString("period_mode"))) {
                            validUpto = ServerUtil.dateRange(validFromExpiry, 0, rs.getInt("period"), -1);
                        }
                    }

                    FitnessImpl fitImpl = new FitnessImpl();
                    if (rs.getInt("pmt_type") == 106 && "WB".equalsIgnoreCase(Util.getUserStateCode())) {
                        Date axelValidUpto = null;
                        ownDobj.setAxleDobj(AxleImpl.setAxleVehDetails_db_to_dobj(null, rs.getString("regn_no"), Util.getUserStateCode(), ownDobj.getOff_cd()));
                        if (ownDobj.getAxleDobj() == null) {
                            throw new VahanException("Owner Axle details not found. Please contact to system administrator.");
                        }
                        if (ownDobj.getAxleDobj().getNoOfAxle() <= 2) {
                            axelValidUpto = ServerUtil.dateRange(ownDobj.getRegn_dt(), 12, 0, -1);
                        } else {
                            axelValidUpto = ServerUtil.dateRange(ownDobj.getRegn_dt(), 15, 0, -1);
                        }
                        if ((validUpto).compareTo(axelValidUpto) > 0) {
                            validUpto = axelValidUpto;
                        }
                    } else {
                        ownDobj.setPmt_type(rs.getInt("pmt_type"));
                        ownDobj.setPmt_catg(rs.getInt("pmt_catg"));
                        int vehAge = fitImpl.getVehAgeValidity(ownDobj);
                        if (vehAge != 99) {
                            VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                            parameters.setPUR_CD(rs.getInt("pur_cd"));
                            parameters.setPERMIT_TYPE(rs.getInt("pmt_type"));
                            if (getmap.get("pmt_validity_from").equalsIgnoreCase("purchase_dt")) {
                                strTemp = "'" + getmap.get("pmt_validity_from") + "'";
                            } else {
                                strTemp = getmap.get("pmt_validity_from");
                            }
                            String pmtValidityFrom = FormulaUtils.getSqlFormulaValue(FormulaUtils.replaceTagPermitValues(strTemp, parameters), "approval_Renewal_of_permit");
                            if (pmtValidityFrom.equalsIgnoreCase("regn_dt")) {
                                Date maxValidUpto = ServerUtil.dateRange(ownDobj.getRegn_dt(), vehAge, 0, -1);
                                if (maxValidUpto != null && maxValidUpto.compareTo(validUpto) <= 0) {
                                    validUpto = maxValidUpto;
                                }
                            } else {
                                Date maxValidUpto = ServerUtil.dateRange(ownDobj.getPurchase_dt(), vehAge, 0, -1);
                                if (maxValidUpto != null && maxValidUpto.compareTo(validUpto) <= 0) {
                                    validUpto = maxValidUpto;
                                }
                            }

                        }
                    }
                    if (rs.getInt("pmt_type") == Integer.valueOf(TableConstants.AITP)
                            || rs.getInt("pmt_type") == Integer.valueOf(TableConstants.NATIONAL_PERMIT)) {
                        query = "select *,rcpt_dt :: Date as rcptDtInDate from vt_fee where state_cd = ? and off_cd = ? and regn_no = ? and rcpt_no = ? AND pur_cd IN (?,?)";
                        ps = tmgr.prepareStatement(query);
                        ps.setString(1, Util.getUserStateCode());
                        ps.setInt(2, Util.getSelectedSeat().getOff_cd());
                        ps.setString(3, rs.getString("regn_no"));
                        ps.setString(4, rs.getString("rcpt_no"));
                        ps.setInt(5, TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD);
                        ps.setInt(6, TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD);
                        RowSet rs3 = tmgr.fetchDetachedRowSet_No_release();
                        if (rs3.next()) {
                            if (rs.getInt("pmt_type") == Integer.valueOf(TableConstants.NATIONAL_PERMIT)) {
                                renewAuthFeeNpRenewPermit = true;
                            }
                            if (Util.getUserStateCode().equalsIgnoreCase("DL")) {
                                printSingleDocForDL = false;
                            }
                            try {
                                boolean pending_auth = false;
                                Date authUpto = null;
                                VehicleParameters vchparameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                                vchparameters.setPERMIT_TYPE(rs.getInt("pmt_type"));
                                if (isCondition(FormulaUtils.replaceTagPermitValues(getmap.get("auth_recursive_fee"), vchparameters), "get_Details")) {
                                    pending_auth = true;
                                }

                                PermitHomeAuthImpl authImpl = new PermitHomeAuthImpl();
                                PermitHomeAuthDobj authDobj = new PermitHomeAuthDobj();
                                authDobj.setApplNo(appl_no);
                                authDobj.setRegnNo(rs.getString("regn_no"));
                                authDobj.setPmtNo(pmt_no);
                                authDobj.setAuthNo(ServerUtil.getUniquePermitNo(tmgr, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), rs.getInt("pmt_type"), 0, "H"));

                                query = "select * from " + TableList.VT_PERMIT_HOME_AUTH + " where regn_no = ?";
                                ps = tmgr.prepareStatement(query);
                                ps.setString(1, rs.getString("regn_no"));
                                RowSet rs4 = tmgr.fetchDetachedRowSet_No_release();
                                if (rs4.next()) {
                                    if ((rs4.getDate("auth_to").compareTo(rs3.getDate("rcptDtInDate")) <= 0) && !(renewAuthFeeNpRenewPermit && Util.getUserStateCode().equalsIgnoreCase("UP,GJ"))) {
                                        authDobj.setAuthFrom(ServerUtil.dateRange(rs3.getDate("rcptDtInDate"), 0, 0, 1));
                                    } else {
                                        authDobj.setAuthFrom(ServerUtil.dateRange(rs4.getDate("auth_to"), 0, 0, 1));
                                    }
                                    if (pending_auth && Util.getUserStateCode().equalsIgnoreCase("DL") && ServerUtil.dateRange(authDobj.getAuthFrom(), 1, 0, -1).compareTo(validUpto) <= 0) {
                                        authDobj.setAuthUpto(ServerUtil.dateRange(rs4.getDate("auth_to"), 1, 0, 0));
                                        while (authDobj.getAuthUpto().compareTo(new Date()) <= 0) {
                                            authUpto = DateUtils.addToDate(authDobj.getAuthUpto(), DateUtils.YEAR, 1);
                                            authDobj.setAuthUpto(authUpto);
                                        }
                                    } else if (ServerUtil.dateRange(authDobj.getAuthFrom(), 1, 0, -1).compareTo(validUpto) <= 0) {
                                        authDobj.setAuthUpto(ServerUtil.dateRange(authDobj.getAuthFrom(), 1, 0, -1));
                                    } else {
                                        authDobj.setAuthUpto(validUpto);
                                    }
                                } else {
                                    throw new VahanException("Previous authorization not found. Please update the authorization throw administrator form.");
                                }
                                authDobj.setPurCd(TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD);
                                authImpl.insert_va_permit_home_auth(tmgr, appl_no, authDobj);
                                authImpl.vt_permit_home_To_VH_permit_home(tmgr, rs.getString("regn_no"));
                                authImpl.InsertVaPermitHomeAuthDeleteVTPermitHomeAuth(tmgr, appl_no);
                                if (rs.getInt("pmt_type") == Integer.valueOf(TableConstants.NATIONAL_PERMIT)) {
                                    renewAuthFlag = true;
                                }
                            } catch (Exception e) {
                                throw new VahanException("Error in Authorization Details. Please Contact to System Administrator. " + e.getMessage());
                            }
                        }
                    }

                    query = "INSERT INTO " + TableList.VH_PERMIT + "(\n"
                            + "            state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                            + "            valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, \n"
                            + "            service_type, goods_to_carry, jorney_purpose, parking, replace_date, \n"
                            + "            remarks, op_dt, pmt_status, reason, moved_on,moved_by)\n"
                            + "   SELECT state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                            + "       valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, \n"
                            + "       service_type, goods_to_carry, jorney_purpose, parking, replace_date, \n"
                            + "       remarks, op_dt,?,?,CURRENT_TIMESTAMP,?\n"
                            + "  FROM " + TableList.VT_PERMIT + " where regn_no = ? ";
                    if (!CommonUtils.isNullOrBlank(rs.getString("pmt_no"))) {
                        query = query + " and pmt_no = ? ";
                    }
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, "REN");
                    ps.setString(2, "RENEWAL OF PERMIT");
                    ps.setString(3, Util.getEmpCode());
                    ps.setString(4, rs.getString("regn_no"));
                    if (!CommonUtils.isNullOrBlank(rs.getString("pmt_no"))) {
                        ps.setString(5, rs.getString("pmt_no"));
                    }
                    regn_no = rs.getString("regn_no");
                    ps.executeUpdate();

                    query = "DELETE FROM " + TableList.VT_PERMIT + " where regn_no =? ";
                    if (!CommonUtils.isNullOrBlank(rs.getString("pmt_no"))) {
                        query = query + " and pmt_no = ? ";
                    }
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, rs.getString("regn_no"));
                    if (!CommonUtils.isNullOrBlank(rs.getString("pmt_no"))) {
                        ps.setString(2, rs.getString("pmt_no"));
                    }
                    ps.executeUpdate();

                    if (validFrom.compareTo(validUpto) >= 0) {
                        throw new VahanException("Permit is not approved because valid From date is greater than valid upto date. Valid from : " + CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(validFrom) + " and Valid from : " + CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(validUpto));
                    }

                    query = "INSERT INTO " + TableList.VT_PERMIT + "(\n"
                            + "            state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                            + "            valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, \n"
                            + "            service_type, goods_to_carry, jorney_purpose, parking, replace_date, \n"
                            + "            remarks, op_dt)\n"
                            + "    VALUES (?, ?, ?, ?, ?, ?, ?, \n"
                            + "            ?, ?, ?, ?, ?, ?, ?, \n"
                            + "            ?, ?, ?, ?, ?, \n"
                            + "            ?, CURRENT_TIMESTAMP) ";

                    ps = tmgr.prepareStatement(query);
                    int i = 1;
                    ps.setString(i++, Util.getUserStateCode());
                    ps.setInt(i++, Util.getUserOffCode());
                    ps.setString(i++, appl_no);
                    ps.setString(i++, pmt_no);
                    ps.setString(i++, rs.getString("regn_no"));
                    ps.setTimestamp(i++, issueDtTimestamp);
                    ps.setDate(i++, new java.sql.Date(validFrom.getTime()));
                    ps.setDate(i++, new java.sql.Date(validUpto.getTime()));
                    ps.setString(i++, rs.getString("rcpt_no"));
                    ps.setInt(i++, rs.getInt("pur_cd"));
                    ps.setInt(i++, rs.getInt("pmt_type"));
                    ps.setInt(i++, rs.getInt("pmt_catg"));
                    ps.setInt(i++, rs.getInt("domain_cd"));
                    ps.setString(i++, rs.getString("region_covered"));
                    ps.setInt(i++, rs.getInt("service_type"));
                    ps.setString(i++, rs.getString("goods_to_carry"));
                    ps.setString(i++, rs.getString("jorney_purpose"));
                    ps.setString(i++, rs.getString("parking"));
                    ps.setDate(i++, rs.getDate("replace_date"));
                    ps.setString(i++, rs.getString("remarks"));
                    ps.executeUpdate();

                    query = "INSERT INTO " + TableList.vt_permit_route + " (state_cd,off_cd,appl_no,route_cd,no_of_trips)\n"
                            + "SELECT * FROM " + TableList.va_permit_route + "\n"
                            + "WHERE appl_no=? ";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, appl_no);
                    ps.executeUpdate();

                    insertVHA_RouteandUpdateVa_Route(tmgr, null, appl_no, null, false, null);
                    // insert data for NP integration
                    if (renewAuthFeeNpRenewPermit && "UP,DL,GJ".contains(rs.getString("state_cd")) && rs.getInt("pmt_type") == Integer.valueOf(TableConstants.NATIONAL_PERMIT)) {
                        ServerUtil.updateNPAuthorizationDetails(rs.getString("regn_no"), appl_no, tmgr, TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD);
                    }
                    if (!smartCardPrint) {
                        String docId = "";
                        if (Util.getUserStateCode().equalsIgnoreCase("DL")
                                && printSingleDocForDL) {
                            docId = "('1')";
                        } else {
                            docId = CommonPermitPrintImpl.getPermitDocIdForQuery(CommonPermitPrintImpl.getPermitDocId(tmgr, rs.getInt("pmt_type"), rs.getInt("pur_cd"), Util.getUserStateCode()));
                        }
                        String[] beanData = {docId, appl_no, rs.getString("regn_no")};
                        CommonPermitPrintImpl.insertVaPermitPrint(tmgr, beanData);
                    }
                    query = "SELECT * FROM " + TableList.VA_INSURANCE + " WHERE APPL_NO = ?";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, appl_no);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        insertIntoVTinsAndDeleteVaIns(tmgr, appl_no, regn_no);
                    }
                    String message = "Renewed Permit " + pmt_no + " for Appl." + appl_no + "[" + regn_no + "]";
                    ServerUtil.insertUsersTransactionMessage(message, 1, tmgr);
                    statDobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
                    if (smartCardPrint) {
                        status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                        PrintPermitImpl printImpl = new PrintPermitImpl();
                        printImpl.fileFlowOfPermit(tmgr, status_dobj, appl_no, status_dobj.getPur_cd(), null);
                        ServerUtil.updateApprovedStatus(tmgr, status_dobj);

                    } else {
                        ServerUtil.fileFlow(tmgr, statDobj);
                        status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                        ServerUtil.updateApprovedStatus(tmgr, status_dobj);

                    }
                    if (Util.getUserStateCode().equalsIgnoreCase("DL")) {
                        ServerUtil.updateOnlinePermitStatus(tmgr, status_dobj);
                    }
                    updateOwnerIdentificationVT(tmgr, appl_no);
                    tmgr.commit();
                }
            }
        } catch (VahanException | SQLException e) {
            throw new VahanException(e.getMessage());
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
    }

    public boolean get_status_vt_Challan(String regn_no) {
        boolean flage = false;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("get_status_vt_Challan");
            String sqlQuery = "SELECT VEH_NO FROM echallan.challan WHERE VEH_NO =?";
            ps = tmgr.prepareStatement(sqlQuery);
            ps.setString(1, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                flage = true;
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
        return flage;
    }

    public boolean get_status_VT_TAX_CLEAR(String regn_no) {
        boolean flage = false;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("get_status_vt_Challan");
            String sqlQuery = "SELECT MAX(CLEAR_TO) AS CLEAR_TO FROM VT_TAX_CLEAR WHERE REGN_NO =?";
            ps = tmgr.prepareStatement(sqlQuery);
            ps.setString(1, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                flage = true;
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
        return flage;
    }

    public boolean get_status_VT_BLOCK_VEH(String regn_no) {
        boolean flage = false;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("get status VT_BLOCK_VEH");
            String sqlQuery = "SELECT REGN_NO,BLOCK_DT,REASON FROM VT_BLOCK_VEH WHERE REGN_NO =?";
            ps = tmgr.prepareStatement(sqlQuery);
            ps.setString(1, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                flage = true;
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
        return flage;
    }

    public boolean get_status_VT_Permit(String regn_no) {
        boolean flage = false;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("get status VT_Permit");
            String sqlQuery = "SELECT * FROM " + TableList.VT_PERMIT + " where DEAL_CODE=?";
            ps = tmgr.prepareStatement(sqlQuery);
            ps.setString(1, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                flage = true;
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
        return flage;
    }

    public boolean get_status_VT_REPLACE_VEH(String regn_no) {
        boolean flage = false;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("get_status_VT_REPLACE_VEH");
            String sqlQuery = "SELECT REGNO_OLD FROM VT_REPLACE_VEH WHERE REGNO_NEW = ?";
            ps = tmgr.prepareStatement(sqlQuery);
            ps.setString(1, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                flage = true;
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
        return flage;
    }

    public PassengerPermitDetailDobj set_permit_appl_db_to_dobj(String appl_no) {
        TransactionManager tmgr = null;
        PassengerPermitDetailDobj permit_dobj = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("set_permit_appl_db_to_dobj");
            String query;
            query = "select va_permit.*, to_char(replace_date,'DD-MON-YYYY') as replaceDateInString from " + TableList.VA_PERMIT + "\n"
                    + " where va_permit.appl_no=? ";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                permit_dobj = new PassengerPermitDetailDobj();
                permit_dobj.setRegion_covered(rs.getString("region_covered"));
                permit_dobj.setRegnNo(rs.getString("regn_no"));
                permit_dobj.setPeriod_mode(rs.getString("period_mode"));
                permit_dobj.setPeriod(String.valueOf(rs.getInt("period")));
                permit_dobj.setPmt_type(String.valueOf(rs.getInt("pmt_type")));
                permit_dobj.setPmtCatg(String.valueOf(rs.getInt("pmt_catg")));
                permit_dobj.setDomain_CODE(String.valueOf(rs.getInt("domain_cd")));
                permit_dobj.setServices_TYPE(String.valueOf(rs.getInt("service_type")));
                permit_dobj.setGoods_TO_CARRY(rs.getString("goods_to_carry"));
                permit_dobj.setJoreny_PURPOSE(rs.getString("jorney_purpose"));
                permit_dobj.setParking(rs.getString("parking"));
                permit_dobj.setPaction(String.valueOf(rs.getInt("pur_cd")));
                permit_dobj.setAllotedFlag(rs.getString("alloted_flag"));
                permit_dobj.setRemarks(rs.getString("remarks"));
                permit_dobj.setOffer_no(rs.getString("offer_no"));
                permit_dobj.setOrder_no(rs.getString("order_no"));
                permit_dobj.setOrder_dt(rs.getDate("order_dt"));
                permit_dobj.setOrder_by(rs.getString("order_by"));
                permit_dobj.setPmt_no(rs.getString("pmt_no"));
                if (CommonUtils.isNullOrBlank(rs.getString("replaceDateInString"))) {
                    permit_dobj.setReplaceDateInString("");
                } else {
                    permit_dobj.setReplaceDateInString(rs.getString("replaceDateInString"));
                }
                permit_dobj.setReplaceDate(rs.getDate("replace_date"));
                if (!(permit_dobj.getPmt_type().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT)
                        || permit_dobj.getPmt_type().equalsIgnoreCase(TableConstants.AITP)
                        || permit_dobj.getPmt_type().equalsIgnoreCase(TableConstants.GOODS_PERMIT))) {
                    query = "select va_permit_route.no_of_trips from " + TableList.va_permit_route + "\n"
                            + " where va_permit_route.appl_no=? limit 1";
                    tmgr = new TransactionManager("set_permit_appl_db_to_dobj1");
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, appl_no);
                    rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        permit_dobj.setNumberOfTrips(String.valueOf(rs.getInt("no_of_trips")));
                    } else {
                        permit_dobj.setNumberOfTrips("0");
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
        return permit_dobj;
    }

    public List<AITPStateFeeDraftDobj> getAitpPaymentDetails(String appl_no) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        List<AITPStateFeeDraftDobj> paymentList = new ArrayList<>();
        AITPStateFeeDraftDobj dobj = null;
        String sql = "";
        try {
            tmgr = new TransactionManager("getAitpPaymentDetails");
            sql = "Select state_cd,off_cd,appl_no, sr_no, regn_no, pay_state_cd,instrument_type, "
                    + " instrument_no, instrument_dt, instrument_amt,bank_code,branch_name,payable_to,recieved_dt,op_dt,b.descr from "
                    + TableList.VA_INSTRUMENT_AITP + " a inner join " + TableList.TM_STATE + " b on a.pay_state_cd=b.state_code where appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new AITPStateFeeDraftDobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setSr_no(rs.getInt("sr_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setPay_state_cd(rs.getString("pay_state_cd"));
                dobj.setPay_state_descr(rs.getString("descr"));
                dobj.setInstrument_type(rs.getString("instrument_type"));
                dobj.setInstrument_no(rs.getString("instrument_no"));
                dobj.setInstrument_dt(rs.getDate("instrument_dt"));
                dobj.setInstrument_amt(rs.getLong("instrument_amt"));
                dobj.setBank_code(rs.getString("bank_code"));
                dobj.setBranch_name(rs.getString("branch_name"));
                dobj.setPayable_to(rs.getString("payable_to"));
                dobj.setRecieved_dt(rs.getDate("recieved_dt"));
                dobj.setMax_draft_date(DateUtil.parseDate(DateUtil.getCurrentDate()));
                dobj.setMin_draft_date(DateUtils.addToDate(dobj.getMax_draft_date(), 2, -3));
                if (dobj.getInstrument_dt() != null && dobj.getMin_draft_date() != null && dobj.getInstrument_dt().before(dobj.getMin_draft_date())) {
                    dobj.setMin_draft_date(dobj.getInstrument_dt());
                }
                paymentList.add(dobj);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (DateUtilsException e) {
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
        return paymentList;
    }

    public PassengerPermitDetailDobj set_vt_permit_regnNo_to_dobj(String regn_no, String pmt_no) {
        TransactionManager tmgr = null;
        PassengerPermitDetailDobj permit_dobj = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("set_vt_permit_regnNo_to_dobj");
            String query;
            query = "select *,to_char(issue_dt,'DD-Mon-YYYY') as issue_dt_descr from " + TableList.VT_PERMIT + " where regn_no=?";
            if (!CommonUtils.isNullOrBlank(pmt_no)) {
                query = query + " and pmt_no =? ";
            }
            ps = tmgr.prepareStatement(query);
            ps.setString(1, regn_no);
            if (!CommonUtils.isNullOrBlank(pmt_no)) {
                ps.setString(2, pmt_no);
            }
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                permit_dobj = new PassengerPermitDetailDobj();
                permit_dobj.setState_cd(rs.getString("state_cd"));
                permit_dobj.setOff_cd(String.valueOf(rs.getInt("off_cd")));
                permit_dobj.setApplNo(rs.getString("appl_no"));
                permit_dobj.setRegnNo(rs.getString("regn_no"));
                permit_dobj.setPmt_type(String.valueOf(rs.getInt("pmt_type")));
                permit_dobj.setPmt_type_code(String.valueOf(rs.getInt("pmt_type")));
                permit_dobj.setPmtCatg(String.valueOf(rs.getInt("pmt_catg")));
                permit_dobj.setDomain_CODE(String.valueOf(rs.getInt("domain_cd")));
                permit_dobj.setServices_TYPE(String.valueOf(rs.getInt("service_type")));
                permit_dobj.setGoods_TO_CARRY(rs.getString("goods_to_carry"));
                permit_dobj.setJoreny_PURPOSE(rs.getString("jorney_purpose"));
                permit_dobj.setRegion_covered(rs.getString("region_covered"));
                permit_dobj.setParking(rs.getString("parking"));
                permit_dobj.setPaction(String.valueOf(rs.getInt("pur_cd")));
                permit_dobj.setStart_POINT(rs.getString("remarks"));
                permit_dobj.setPmt_no(rs.getString("pmt_no"));
                permit_dobj.setValid_from(rs.getDate("valid_from"));
                permit_dobj.setValid_upto(rs.getDate("valid_upto"));
                permit_dobj.setReplaceDate(rs.getDate("replace_date"));
                permit_dobj.setIssuePmtDateDescr(rs.getString("issue_dt_descr"));
                permit_dobj.setIssuePmtDate(rs.getDate("issue_dt"));

                if (!(permit_dobj.getPmt_type().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT)
                        || permit_dobj.getPmt_type().equalsIgnoreCase(TableConstants.AITP)
                        || permit_dobj.getPmt_type().equalsIgnoreCase(TableConstants.GOODS_PERMIT))) {
                    query = "select a.no_of_trips,b.rlength from " + TableList.vt_permit_route + " a "
                            + "left outer join permit.vm_route_master b "
                            + " on  a.state_cd=b.state_cd and a.off_cd=b.off_cd and a.route_cd=b.code   "
                            + " where a.appl_no=? limit 1";
                    tmgr = new TransactionManager("set_vt_permit_regnNo_to_dobj1");
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, rs.getString("appl_no"));
                    rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        permit_dobj.setNumberOfTrips(String.valueOf(rs.getInt("no_of_trips")));
                        permit_dobj.setRout_length(String.valueOf(rs.getInt("rlength")));
                    } else {
                        permit_dobj.setNumberOfTrips("0");
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
        return permit_dobj;
    }

    public PassengerPermitDetailDobj getPermitHistory(String regnNo, String stateCd) {
        TransactionManager tmgr = null;
        PassengerPermitDetailDobj permit_dobj = null;
        RowSet rs = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManager("getPermitHistory");
            String query = "SELECT * FROM  " + TableList.VH_PERMIT
                    + " WHERE regn_no=? and state_cd=? order by moved_on desc limit 1";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                if (!"HP".contains(Util.getUserStateCode())) {
                    query = "SELECT * FROM  permit.vha_permit_transaction  WHERE regn_no=? and state_cd=? order by moved_on desc limit 1";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, regnNo);
                    ps.setString(2, stateCd);
                    RowSet rs1 = tmgr.fetchDetachedRowSet();
                    if (rs1.next()) {
                        if ((rs1.getInt("pur_cd") == TableConstants.VM_PMT_SURRENDER_PUR_CD && rs1.getInt("trans_pur_cd") == TableConstants.VM_PMT_CANCELATION_PUR_CD)) {
                            return null;
                        }
                    }
                }
                permit_dobj = new PassengerPermitDetailDobj();
                permit_dobj.setApplNo(rs.getString("appl_no"));
                permit_dobj.setRegnNo(rs.getString("regn_no"));
                permit_dobj.setPmt_type(String.valueOf(rs.getInt("pmt_type")));
                permit_dobj.setPmt_type_code(String.valueOf(rs.getInt("pmt_type")));
                permit_dobj.setPmtCatg(String.valueOf(rs.getInt("pmt_catg")));
                permit_dobj.setDomain_CODE(String.valueOf(rs.getInt("domain_cd")));
                permit_dobj.setServices_TYPE(String.valueOf(rs.getInt("service_type")));
                permit_dobj.setGoods_TO_CARRY(rs.getString("goods_to_carry"));
                permit_dobj.setJoreny_PURPOSE(rs.getString("jorney_purpose"));
                permit_dobj.setRegion_covered(rs.getString("region_covered"));
                permit_dobj.setParking(rs.getString("parking"));
                permit_dobj.setPaction(String.valueOf(rs.getInt("pur_cd")));
                permit_dobj.setStart_POINT(rs.getString("remarks"));
                permit_dobj.setPmt_no(rs.getString("pmt_no"));
                permit_dobj.setValid_from(rs.getDate("valid_from"));
                permit_dobj.setValid_upto(rs.getDate("valid_upto"));
                permit_dobj.setReplaceDate(rs.getDate("replace_date"));
                if (!(permit_dobj.getPmt_type().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT)
                        || permit_dobj.getPmt_type().equalsIgnoreCase(TableConstants.AITP)
                        || permit_dobj.getPmt_type().equalsIgnoreCase(TableConstants.GOODS_PERMIT))) {
                    query = "select vt_permit_route.no_of_trips from " + TableList.vt_permit_route + "\n"
                            + " where vt_permit_route.appl_no=? limit 1";
                    tmgr = new TransactionManager("getPermitHistory1");
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, rs.getString("appl_no"));
                    rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        permit_dobj.setNumberOfTrips(String.valueOf(rs.getInt("no_of_trips")));
                    } else {
                        permit_dobj.setNumberOfTrips("0");
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
        return permit_dobj;
    }

    public void rebackStatus(PermitOwnerDetailDobj owner_dobj, Status_dobj status_dobj, String Change, PassengerPermitDetailDobj dobj, List route_code, String appl_no, boolean otherOffRouteAllow, List<VmPermitRouteDobj> otherOffRoutecode) {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("rebackStatus");
            if (!Change.isEmpty()) {
                insertVHA_PermitandUpdateVa_Permit(tmgr, dobj, appl_no, null);
                if (("NEW").equalsIgnoreCase(dobj.getRegnNo())) {
                    insertVHA_Owner_PermitandUpdateVa_Owner_Permit(tmgr, owner_dobj, appl_no);
                }
                insertVHA_RouteandUpdateVa_Route(tmgr, dobj, appl_no, route_code, otherOffRouteAllow, otherOffRoutecode);
            }
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            ServerUtil.fileFlow(tmgr, status_dobj);
            tmgr.commit();
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
    }

    public void stayOnTheSameStage(PermitOwnerDetailDobj owner_dobj, PassengerPermitDetailDobj dobj, List route_code, String appl_no, String changedata, String New_regn_no, int action_cd, boolean otherOffRouteAllow, List<VmPermitRouteDobj> otherOffRoutecode) {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("stayOnTheSameStage");
            if (!changedata.isEmpty()) {
                insertVHA_PermitandUpdateVa_Permit(tmgr, dobj, appl_no, null);
                if (("NEW").equalsIgnoreCase(dobj.getRegnNo())) {
                    insertVHA_Owner_PermitandUpdateVa_Owner_Permit(tmgr, owner_dobj, appl_no);
                }
                insertVHA_RouteandUpdateVa_Route(tmgr, dobj, appl_no, route_code, otherOffRouteAllow, otherOffRoutecode);

            }
            if (!CommonUtils.isNullOrBlank(New_regn_no) && action_cd == TableConstants.TM_ROLE_PMT_VERIFICATION) {
                VhaDtlsTOVaDtls(appl_no, New_regn_no.toUpperCase(), Util.getUserStateCode(), tmgr);
            }
            tmgr.commit();

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

    }

    public String saveEApplicationPMT(PassengerPermitDetailDobj dobj) {
        TransactionManager tmgr = null;
        String generated_appl_no = null;
        try {
            tmgr = new TransactionManager("saveEApplicationPMT");
            generated_appl_no = ServerUtil.getUniqueApplNo(tmgr, dobj.getState_cd());
            dobj.setApplNo(generated_appl_no);
            tmgr.commit();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (VahanException e) {
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
        return generated_appl_no;
    }

    public void vha_status_to_va_status(TransactionManager tmgr, Status_dobj status_dobj) throws SQLException {
        String Query = "";
        PreparedStatement ps;
        Query = "INSERT INTO " + TableList.VHA_STATUS + "(\n"
                + "            state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno, \n"
                + "            action_cd, seat_cd, cntr_id, status, office_remark, public_remark, \n"
                + "            file_movement_type, emp_cd, op_dt, moved_on, entry_ip)\n"
                + "   SELECT   state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno, \n"
                + "            action_cd, seat_cd, cntr_id, status, office_remark, public_remark, \n"
                + "            file_movement_type, emp_cd, op_dt,CURRENT_TIMESTAMP, ? "
                + "  FROM " + TableList.VA_STATUS + " where appl_no=? and pur_cd=? ";
        ps = tmgr.prepareStatement(Query);
        ps.setString(1, Util.getClientIpAdress());
        ps.setString(2, status_dobj.getAppl_no());
        ps.setInt(3, status_dobj.getPur_cd());
        ps.executeUpdate();

        Query = "delete from " + TableList.VA_STATUS + " where appl_no=? and pur_cd=?";
        ps = tmgr.prepareStatement(Query);
        ps.setString(1, status_dobj.getAppl_no());
        ps.setInt(2, status_dobj.getPur_cd());
        ps.executeUpdate();
    }

    public void VhaDtlsTOVaDtls(String appl_no, String regn_no, String state_cd, TransactionManager tmgr) throws VahanException {
        String Query;
        PreparedStatement ps;
        try {
            Query = "INSERT INTO " + TableList.VHA_DETAILS + " (\n"
                    + "            appl_no, pur_cd, appl_dt, regn_no, user_id, user_type, entry_ip, \n"
                    + "            entry_status, confirm_ip, confirm_status, confirm_date, state_cd, \n"
                    + "            off_cd, moved_on, moved_by)\n"
                    + "     SELECT appl_no, pur_cd, appl_dt, regn_no, user_id, user_type, entry_ip, \n"
                    + "            entry_status, confirm_ip, confirm_status, confirm_date, state_cd, \n"
                    + "            off_cd,CURRENT_TIMESTAMP,? FROM " + TableList.VA_DETAILS + " where appl_no = ?; ";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, appl_no);
            if (ps.executeUpdate() < 1) {
                throw new VahanException("Some problem occurred while moving data");
            }
            Query = "UPDATE " + TableList.VA_DETAILS + " SET regn_no=? WHERE appl_no=?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regn_no);
            ps.setString(2, appl_no);
            if (ps.executeUpdate() < 1) {
                throw new VahanException("Some problem occurred while updating data");
            }

            insertVHA_PermitandUpdateVa_Permit(tmgr, null, appl_no, null);
            insertVHA_Owner_PermitandUpdateVa_Owner_Permit(tmgr, null, appl_no);

            Query = "DELETE FROM " + TableList.VA_PERMIT_OWNER + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
            if (ps.executeUpdate() < 1) {
                throw new VahanException("Some problem occurred while removing permit owner data");
            }

            Query = "UPDATE " + TableList.VA_PERMIT + " SET regn_no=? WHERE appl_no=?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regn_no);
            ps.setString(2, appl_no);
            if (ps.executeUpdate() < 1) {
                throw new VahanException("Some problem occurred while updating vehicle number");
            }
            Query = "SELECT * FROM " + TableList.VA_PERMIT + " where appl_no=? ";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if ((rs.getInt("pmt_type") == Integer.parseInt(TableConstants.AITP)) || (rs.getInt("pmt_type") == Integer.parseInt(TableConstants.NATIONAL_PERMIT))) {
                    Query = "SELECT * FROM " + TableList.VA_PERMIT_HOME_AUTH + " WHERE appl_no=? ";
                    ps = tmgr.prepareStatement(Query);
                    ps.setString(1, appl_no);
                    RowSet homeRs = tmgr.fetchDetachedRowSet_No_release();
                    if (homeRs.next()) {
                        Query = "UPDATE " + TableList.VA_PERMIT_HOME_AUTH + " SET regn_no=? WHERE appl_no=?";
                        ps = tmgr.prepareStatement(Query);
                        ps.setString(1, regn_no);
                        ps.setString(2, appl_no);
                        if (ps.executeUpdate() < 1) {
                            throw new VahanException("Some problem occurred while updating home auth details");
                        }
                    }
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("rcpt_no"))) {
                    Query = "SELECT regn_no,off_cd FROM " + TableList.VT_FEE + " where  state_cd=? and off_cd=? and rcpt_no=? and pur_cd=? ";
                    ps = tmgr.prepareStatement(Query);
                    ps.setString(1, state_cd);
                    ps.setInt(2, rs.getInt("off_cd"));
                    ps.setString(3, rs.getString("rcpt_no"));
                    ps.setInt(4, rs.getInt("pur_cd"));
                    RowSet feeRs = tmgr.fetchDetachedRowSet_No_release();
                    if (feeRs.next()) {
                        if (!feeRs.getString("regn_no").equalsIgnoreCase(regn_no)) {
                            Query = "UPDATE " + TableList.VT_FEE + " SET regn_no=? WHERE rcpt_no=? and state_cd=? and off_cd=?";
                            ps = tmgr.prepareStatement(Query);
                            ps.setString(1, regn_no);
                            ps.setString(2, rs.getString("rcpt_no"));
                            ps.setString(3, state_cd);
                            ps.setInt(4, feeRs.getInt("off_cd"));
                            if (ps.executeUpdate() < 1) {
                                throw new VahanException("Some problem occurred while updating permit fee data");
                            }
                        }
                    } else {
                        throw new VahanException("Some problem occurred while retrieving permit fee data");
                    }
                }
            } else {
                throw new VahanException("Some problem occurred while retrieving permit data");
            }
        } catch (VahanException e) {
            throw new VahanException(e.getMessage());
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There were some problem while Updating of permit data");
        }
    }

    public String[] vtPermitOwner(String appl_no) {
        TransactionManager tmgr = null;
        String[] owner = null;
        String Query;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("vtPermitOwner");
            Query = "select owner_name,vh_class from " + TableList.VA_PERMIT_OWNER + " where appl_no = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                owner = new String[2];
                owner[0] = rs.getString("owner_name");
                owner[1] = rs.getString("vh_class");
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
        return owner;
    }

    public void moveInVhaPmtInCaseOfDisApproval(TransactionManager tmgr, String appl_no, String reason, String appDisappFlag) throws SQLException {
        String query;
        PreparedStatement ps;
        query = "SELECT * FROM " + TableList.VA_PERMIT + " where appl_no=? ";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, appl_no);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            query = "INSERT INTO " + TableList.VHA_PERMIT + "(\n"
                    + "            state_cd, off_cd, appl_no, regn_no, rcpt_no, period_mode, period, \n"
                    + "            pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, service_type, \n"
                    + "            goods_to_carry, jorney_purpose, parking, replace_date, alloted_flag, \n"
                    + "            offer_no, order_no, order_by, order_dt, remarks, op_dt, moved_on, \n"
                    + "            moved_by)\n"
                    + "   SELECT  state_cd, off_cd, appl_no, regn_no, rcpt_no, period_mode, period, \n"
                    + "           pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, service_type, \n"
                    + "           goods_to_carry, jorney_purpose, parking, replace_date, alloted_flag, \n"
                    + "           offer_no, order_no, order_by, order_dt, remarks, op_dt,Current_timestamp,\n"
                    + "           ?\n"
                    + "  FROM " + TableList.VA_PERMIT + " where appl_no=? ";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, appl_no);
            ps.executeUpdate();

            query = "UPDATE " + TableList.VA_PERMIT + " SET alloted_flag=?,remarks=? where appl_no = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, appDisappFlag);
            ps.setString(2, reason);
            ps.setString(3, appl_no);
            ps.executeUpdate();

            if (("D").equalsIgnoreCase(appDisappFlag)) {
                query = "INSERT INTO " + TableList.VHA_PERMIT + "(\n"
                        + "            state_cd, off_cd, appl_no, regn_no, rcpt_no, period_mode, period, \n"
                        + "            pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, service_type, \n"
                        + "            goods_to_carry, jorney_purpose, parking, replace_date, alloted_flag, \n"
                        + "            offer_no, order_no, order_by, order_dt, remarks, op_dt, moved_on, \n"
                        + "            moved_by)\n"
                        + "   SELECT  state_cd, off_cd, appl_no, regn_no, rcpt_no, period_mode, period, \n"
                        + "           pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, service_type, \n"
                        + "           goods_to_carry, jorney_purpose, parking, replace_date, alloted_flag, \n"
                        + "           offer_no, order_no, order_by, order_dt, remarks, op_dt,current_timestamp + (10 * interval '1 second'),\n"
                        + "           ?\n"
                        + "  FROM " + TableList.VA_PERMIT + " where appl_no=? ";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, appl_no);
                ps.executeUpdate();

                query = "DELETE FROM " + TableList.VA_PERMIT + " where appl_no = ?";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, appl_no);
                ps.executeUpdate();

                query = "INSERT INTO " + TableList.VHA_PERMIT_OWNER + " (\n"
                        + "            state_cd, off_cd, appl_no, regn_no, owner_name, f_name, c_add1, \n"
                        + "            c_add2, c_add3, c_district, c_pincode, c_state, p_add1, p_add2, \n"
                        + "            p_add3, p_district, p_pincode, p_state, mobile_no, email_id, \n"
                        + "            vh_class, seat_cap, unld_wt, ld_wt, vch_catg, owner_ctg, moved_on, moved_by,fuel)\n"
                        + "   SELECT   state_cd, off_cd, appl_no, regn_no, owner_name, f_name, c_add1, \n"
                        + "            c_add2, c_add3, c_district, c_pincode, c_state, p_add1, p_add2, \n"
                        + "            p_add3, p_district, p_pincode, p_state, mobile_no, email_id, \n"
                        + "            vh_class, seat_cap, unld_wt, ld_wt, vch_catg, owner_ctg,Current_timestamp,?,fuel \n"
                        + "  FROM " + TableList.VA_PERMIT_OWNER + " where appl_no=?";

                ps = tmgr.prepareStatement(query);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, appl_no);
                ps.executeUpdate();

                query = "DELETE FROM " + TableList.VA_PERMIT_OWNER + " where appl_no=?";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, appl_no);
                ps.executeUpdate();

                query = "INSERT INTO " + TableList.VH_LOI_DTLS + "(\n"
                        + "            appl_no, state_cd, off_cd, dl_no, psv_no, op_dt, flag, image_data, \n"
                        + "            aadhar_no, dl_issue_date, pp_add1, pp_add2, pp_add3, pp_pincode, \n"
                        + "            pp_state, mobile_no, permit_no, education, category, email_id, \n"
                        + "            status, vch_class, pc_add1, pc_add2, pc_add3, pc_pincode, pc_state, \n"
                        + "            owner_name, dob, pp_district, pc_district, f_name, moved_on, \n"
                        + "            moved_by)\n"
                        + "    SELECT appl_no, state_cd, off_cd, dl_no, psv_no, op_dt, flag, image_data, \n"
                        + "           aadhar_no, dl_issue_date, pp_add1, pp_add2, pp_add3, pp_pincode, \n"
                        + "           pp_state, mobile_no, permit_no, education, category, email_id, \n"
                        + "           status, vch_class, pc_add1, pc_add2, pc_add3, pc_pincode, pc_state, \n"
                        + "           owner_name, dob, pp_district, pc_district, f_name,Current_timestamp,?\n"
                        + "  FROM " + TableList.VP_LOI_DTLS + " where appl_no=?";

                ps = tmgr.prepareStatement(query);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, appl_no);
                ps.executeUpdate();

                query = "DELETE FROM " + TableList.VP_LOI_DTLS + " where appl_no=?";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, appl_no);
                ps.executeUpdate();
            }
        }
    }

    public void notShowInWorkFlow(TransactionManager tmgr, String applNo, String StateCd) throws VahanException {
        try {
            PreparedStatement ps = null;
            String Query = "update va_status set status = ? where appl_no = ? and state_cd = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, "");
            ps.setString(2, applNo);
            ps.setString(3, StateCd);
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("Error in Update va_status...");
        }
    }

    public boolean offerLetterFlowInRegisteredVehicle(String stateCd, int pmtType, int pmtCatg) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        boolean flag = false;
        try {
            tmgr = new TransactionManager("offerLetterFlowInRegisteredVehicle");
            String Query = "SELECT pmt_offer_flag from " + TableList.VM_PERMIT_CATG + " where state_cd = ? and permit_type = ? and code = ? ";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, stateCd);
            ps.setInt(2, pmtType);
            ps.setInt(3, pmtCatg);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                flag = rs.getBoolean("pmt_offer_flag");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("Data not found in catagory.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public Map<String, String> getReplacementDtls(String stateCd, int purCd, VehicleParameters parameters) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        Map<String, String> replacementDtls = null;
        try {
            tmgr = new TransactionManager("getReplacementDtls");
            String Query = "SELECT condition_formula,period_mode,period from " + TableList.VM_PERMIT_REPLACEMENT_VALIDITY + " where state_cd = ? and pur_cd = ?";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, stateCd);
            ps.setInt(i++, purCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (isCondition(FormulaUtils.replaceTagPermitValues(rs.getString("condition_formula"), parameters), "getReplacementDtls")) {
                    replacementDtls = new HashMap<>();
                    replacementDtls.put("period_mode", rs.getString("period_mode"));
                    replacementDtls.put("period", rs.getString("period"));
                    break;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("Data not found in master replacement table. Please contact to system administrartor.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return replacementDtls;
    }

    public Map<String, Boolean> multiRouteRegionRestriction(String stateCd, int purCd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        Map<String, Boolean> routeRegionRestriction = null;
        try {
            tmgr = new TransactionManager("multiRouteRegionRestriction");
            String Query = "SELECT  multi_route_allowed, multi_region_allowed FROM " + TableList.VM_PERMIT_ROUTE_REGION_RESTRICTION + " where state_cd = ? and pur_cd = ?";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, stateCd);
            ps.setInt(i++, purCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                routeRegionRestriction = new HashMap<>();
                routeRegionRestriction.put("multi_route_allowed", rs.getBoolean("multi_route_allowed"));
                routeRegionRestriction.put("multi_region_allowed", rs.getBoolean("multi_region_allowed"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("Data not found in master replacement table. Please contact to system administrartor.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return routeRegionRestriction;
    }

    public Map<String, Boolean> visibleRestrictionAreaRoute(String stateCd, int purCd, int pmtType, int pmtCatg) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        Map<String, Boolean> visibleRestrictionAreaRoute = null;
        try {
            tmgr = new TransactionManager("visibleRestrictionAreaRoute");
            String Query = "SELECT  condition_formula, show_route_dtls,show_region_dtls FROM " + TableList.VM_PERMIT_ROUTE_REGION_RESTRICTION + " where state_cd = ? and pur_cd = ?";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, stateCd);
            ps.setInt(i++, purCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                PassengerPermitDetailDobj pmtDobj = new PassengerPermitDetailDobj();
                pmtDobj.setPmt_type_code(String.valueOf(pmtType));
                pmtDobj.setPmtCatg(String.valueOf(pmtCatg));
                VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, pmtDobj, 0, 0, 0, 0, 0, 0, 0, 0);
                if (isCondition(FormulaUtils.replaceTagPermitValues(rs.getString("condition_formula"), parameters), "visibleRestrictionAreaRoute")) {
                    visibleRestrictionAreaRoute = new HashMap<>();
                    visibleRestrictionAreaRoute.put("show_route_dtls", rs.getBoolean("show_route_dtls"));
                    visibleRestrictionAreaRoute.put("show_region_dtls", rs.getBoolean("show_region_dtls"));
                    break;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("Data not found in master replacement table. Please contact to system administrartor.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return visibleRestrictionAreaRoute;
    }

    public boolean applPermitIsThere(String applNo, int purCd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        boolean applIsAlsoThere = false;
        try {
            tmgr = new TransactionManager("applPermitIsThere");
            String Query = "SELECT  * FROM " + TableList.VA_DETAILS + " where appl_no = ? and pur_cd = ?";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, applNo);
            ps.setInt(i++, purCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                applIsAlsoThere = true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Data not found in respected table. Please contact to system administrartor.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return applIsAlsoThere;
    }

    public int getPurCdFormStateConfig(String conditon) throws VahanException {
        int purCd = 0;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getPurCdFormStateConfig");
            if (conditon != null && !conditon.isEmpty()) {
                String Query = "SELECT " + conditon + " as conditon";
                tmgr.prepareStatement(Query);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    purCd = rs.getInt("conditon");
                }
            }
            if (purCd == 0) {
                throw new VahanException("Data not found in respected table. Please contact to system administrartor.");
            }
        } catch (SQLException e) {
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
        return purCd;
    }

    public Map<String, String> getRegnNoWithApplNo(String stateCd, String offerNo) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        Map<String, String> information = null;
        try {
            tmgr = new TransactionManager("getReplacementDtls");
            String Query = "SELECT appl_no,regn_no from " + TableList.VA_PERMIT + " where state_cd = ? and offer_no = ?";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, stateCd);
            ps.setString(i++, offerNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                information = new HashMap<>();
                information.put("appl_no", rs.getString("appl_no"));
                information.put("regn_no", rs.getString("regn_no"));
            }
            if (information == null) {
                throw new VahanException("Permit details not found againts this offer no. Plase check the details.");
            }
        } catch (VahanException e) {
            throw new VahanException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return information;
    }

    public boolean saveOfferDetilsInVaTables(String appl_no, PermitOwnerDetailDobj ownDobj, PermitOwnerDetailDobj prvownDobj, PassengerPermitDetailDobj passPermit, PassengerPermitDetailDobj prvPassPermit, List route_code, String CompairChange, boolean otherOffRouteAllow, List<VmPermitRouteDobj> otherOffRoutecode) throws VahanException {
        TransactionManager tmgr = null;
        boolean flag = false;
        try {
            tmgr = new TransactionManager("saveOfferDetilsInVaTables");
            if (!CompairChange.isEmpty()) {
                insertVHA_PermitandUpdateVa_Permit(tmgr, passPermit, appl_no, null);
                insertVHA_Owner_PermitandUpdateVa_Owner_Permit(tmgr, ownDobj, appl_no);
                if (route_code.size() > 0 && CompairChange.contains("Route Details")) {
                    insertVHA_RouteandUpdateVa_Route(tmgr, passPermit, appl_no, route_code, otherOffRouteAllow, otherOffRoutecode);
                }
                tmgr.commit();
            }
            flag = true;
        } catch (VahanException e) {
            throw new VahanException(e.getMessage());
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
        return flag;
    }

    public List getCurrentPermitDetails(String stateCd, String regn_no) {
        String Query;
        PreparedStatement ps;
        List<PassengerPermitDetailDobj> multiPermitDetails = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getCurrentPermitDetails");
            multiPermitDetails = new ArrayList<>();
            Query = "SELECT PMT_TYPE,TYPE.DESCR as pmt_descr,PMT_NO,PMT.PUR_CD AS PUR_CD,MST.descr AS descr,to_char(VALID_FROM,'DD-MON-YYYY') AS VALID_FROM ,to_char(VALID_UPTO, 'DD-MON-YYYY') AS VALID_UPTO FROM " + TableList.VT_PERMIT + " PMT\n"
                    + "INNER JOIN " + TableList.TM_PURPOSE_MAST + " MST ON MST.PUR_CD = PMT.PUR_CD \n"
                    + "INNER JOIN " + TableList.VM_PERMIT_TYPE + " TYPE ON TYPE.CODE = PMT.PMT_TYPE \n"
                    + "WHERE REGN_NO = ? and state_cd = ? ";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                PassengerPermitDetailDobj dobj = new PassengerPermitDetailDobj();
                if (rs.getString("PMT_TYPE").equalsIgnoreCase(TableConstants.GOODS_PERMIT)
                        || rs.getString("PMT_TYPE").equalsIgnoreCase(TableConstants.NATIONAL_PERMIT)) {
                    dobj.setPmt_type(rs.getString("PMT_TYPE"));
                    dobj.setPmt_type_code(rs.getString("pmt_descr"));
                    dobj.setRegnNo(regn_no);
                    dobj.setPmt_no(rs.getString("PMT_NO"));
                    dobj.setPaction_code(String.valueOf(TableConstants.VM_PMT_RENEWAL_PUR_CD));
                    dobj.setPaction("RENEWAL OF PERMIT");
                    multiPermitDetails.add(dobj);
                }
            }
            if (multiPermitDetails.size() == 1 && multiPermitDetails.get(0).getPmt_type().equalsIgnoreCase(TableConstants.GOODS_PERMIT)) {
                PassengerPermitDetailDobj pmtdobj = new PassengerPermitDetailDobj();
                pmtdobj.setPmt_type(TableConstants.NATIONAL_PERMIT);
                pmtdobj.setPmt_type_code("National Permit");
                pmtdobj.setRegnNo(regn_no);
                pmtdobj.setPmt_no("");
                pmtdobj.setPaction_code(String.valueOf(TableConstants.VM_PMT_FRESH_PUR_CD));
                pmtdobj.setPaction("FRESH PERMIT");
                multiPermitDetails.add(pmtdobj);
            } else if (multiPermitDetails.size() == 1 && multiPermitDetails.get(0).getPmt_type().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT)) {
                PassengerPermitDetailDobj pmtdobj = new PassengerPermitDetailDobj();
                pmtdobj.setPmt_type(TableConstants.GOODS_PERMIT);
                pmtdobj.setPmt_type_code("Goods Carriage Permit");
                pmtdobj.setRegnNo(regn_no);
                pmtdobj.setPmt_no("");
                pmtdobj.setPaction_code(String.valueOf(TableConstants.VM_PMT_FRESH_PUR_CD));
                pmtdobj.setPaction("FRESH PERMIT");
                multiPermitDetails.add(pmtdobj);
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
        return multiPermitDetails;
    }

    public int checkMultiRegion(String region, String state_cd, int off_cd) {
        int regions_covered = 0;
        PreparedStatement ps;
        RowSet rs;
        String Query = "";
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("checkMultiRegion");
            if (!CommonUtils.isNullOrBlank(region)) {
                Query = "select regions_covered from " + TableList.VM_REGION + " where region_cd::text = ANY(string_to_array(?, ',')) and state_cd = ? and off_cd = ?";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, region);
                ps.setString(2, state_cd);
                ps.setInt(3, off_cd);
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    regions_covered = rs.getInt("regions_covered");
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
        return regions_covered;
    }

    public boolean getpmtCatgstatus(int pmt_type, int pmt_catg) {
        boolean flage = true;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManagerReadOnly("getpmtCatgstatus");
            String query;
            query = "select * from " + TableList.VM_PERMIT_CATG + " where state_cd=? and permit_type=? and code=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, pmt_type);
            ps.setInt(3, pmt_catg);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                flage = false;
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
        return flage;
    }

    public String getPmtTypeForSplVhClass(int pur_cd, int vh_class) {
        String pmt_type_allowed = "";
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManagerReadOnly("getPmtTypeForSplVhClass");
            String query;
            query = "select pmt_type_allow from " + TableList.VM_SPL_VH_CLASS_PMT + " where state_cd=? and pur_cd=? and vh_class=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, pur_cd);
            ps.setInt(3, vh_class);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                pmt_type_allowed = rs.getString("pmt_type_allow");
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
        return pmt_type_allowed;
    }

    public static Map<String, VmPermitRouteDobj> getAllRoutesForPermit(String state_cd, String selected_off_cd)
            throws VahanException {
        String sql = "select b.off_name,code as route_code,floc as start_from,tloc as end_upto,via,rlength as route_length,a.off_cd "
                + " from " + TableList.VM_ROUTE_MASTER + " a left outer join tm_office b on a.state_cd=b.state_cd and a.off_cd=b.off_cd where a.state_cd = ? and a.off_cd in (" + selected_off_cd + ") order by 1,3 ";
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        Map<String, VmPermitRouteDobj> routeMap = new HashMap<String, VmPermitRouteDobj>();
        try {
            tmgr = new TransactionManager("getAllRoutesForPermit");
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, state_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                VmPermitRouteDobj dobj = new VmPermitRouteDobj();
                dobj.setOff_cd_descr(rs.getString("off_name"));
                dobj.setRoute_code(rs.getString("route_code"));
                dobj.setFrom_loc(rs.getString("start_from"));
                dobj.setTo_loc(rs.getString("end_upto"));
                dobj.setVia(rs.getString("via"));
                dobj.setLength(rs.getInt("route_length"));
                dobj.setOff_code(rs.getInt("off_cd"));
                dobj.setRowKey(rs.getString("route_code")+rs.getInt("off_cd"));
                routeMap.put(rs.getString("route_code")+rs.getInt("off_cd"), dobj);
            }
        } catch (SQLException e) {
            routeMap = null;
            LOGGER.error(e.getMessage());
            throw new VahanException(
                    "Some Problem Occured while getting the routes of Your State and permitted office");
        } catch (Exception e) {
            routeMap = null;
            LOGGER.error(e.getMessage());
            throw new VahanException(
                    "Some Problem Occured while getting the routes of Your State and permitted office");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                routeMap = null;
                LOGGER.error(e.getMessage());
                throw new VahanException(
                        "Some Problem Occured while getting the routes for Your State and permitted office");
            }
        }

        return routeMap;
    }

    public Map<String, String> getOtherOffRouteCode(String Appl_no, String state_cd, String TableName, int off_cd) {
        Map<String, String> routeMap = new HashMap<>();
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("getOtherOffRouteCode");
            String query;
            query = "SELECT distinct off_cd as off_cd,string_agg(route_cd::text,',') as route_cd from " + TableName + " where state_cd=? and appl_no=? and off_cd <> " + off_cd + " group by off_cd ";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, state_cd);
            ps.setString(2, Appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                routeMap.put(rs.getString("off_cd"), rs.getString("route_cd"));
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
        return routeMap;
    }

    public String getVhClassAllowedForTaxExempt(int pur_cd) {
        String vhClassAllowed = "";
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManagerReadOnly("getVhClassAllowedForTaxExempt");
            String query;
            query = "select vh_class_allow from " + TableList.VM_PERMIT_FOR_TAX_ENDORSEMENT + " where state_cd=? and pur_cd=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, pur_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                vhClassAllowed = rs.getString("vh_class_allow");
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
        return vhClassAllowed;
    }

    public String[] getGracePeriodForTax(int permit_type,PermitCheckDetailsDobj validDobj) {        
        String[] gracePeriodArr = null;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManagerReadOnly("getGracePeriodForTax");
            String query;
            query = "select grace_period_mode,grace_period from " + TableList.VM_PERMIT_GRACE_PERIOD + " where state_cd=? and pmt_type=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, permit_type);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {                
                gracePeriodArr = new String[2];
                gracePeriodArr[0] = rs.getString("grace_period_mode");                
                if (Util.getUserStateCode().equals("PY")) {                    
                    VehicleParameters parameters = new VehicleParameters();
                    parameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(CommonPermitPrintImpl.getDateDD_MMM_YYYY(validDobj.getTaxUpto())));                    
                    gracePeriodArr[1] = FormulaUtils.getSqlFormulaValue(FormulaUtils.replaceTagPermitValues(rs.getString("grace_period"), parameters), "getGracePeriodForTax()");                    
                }else{
                    gracePeriodArr[1] = rs.getString("grace_period");
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
        return gracePeriodArr;
    }

    public int getNoOfDaysAfterFee(String applNo) {
        int dateDiff = 0;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManagerReadOnly("getNoOfDaysAfterFee");
            String query;
            query = "select current_date - rcpt_dt::date as datediff from " + TableList.VT_FEE + " a inner join " + TableList.VA_PERMIT
                    + " b on a.rcpt_no=b.rcpt_no and a.state_cd=b.state_cd and a.off_cd=b.off_cd "
                    + " where a.state_cd=? and a.off_cd=? and a.pur_cd=? and b.appl_no=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserOffCode());
            ps.setInt(3, TableConstants.VM_PMT_FRESH_PUR_CD);
            ps.setString(4, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dateDiff = rs.getInt("datediff");
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
        return dateDiff;
    }

    public List<String> getStateToFromRouteMaster(String state_cd, int off_cd) {
        List<String> listStateTo = new ArrayList();
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManagerReadOnly("getStateToFromRouteMaster");
            String query;
            query = "select distinct state_to from permit.vm_route_master where state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                listStateTo.add(rs.getString("state_to"));
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

        return listStateTo;
    }

    public void updateVaNpDetails(TransactionManager tmgr, String appl_no, String new_regn_no, String state_cd) throws SQLException {
        String query;
        PreparedStatement ps;
        query = "SELECT regn_no FROM permit.va_np_detail where state_cd=? and appl_no=?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, state_cd);
        ps.setString(2, appl_no);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next() && rs.getString("regn_no").equalsIgnoreCase("NEW")) {
            query = "UPDATE permit.va_np_detail SET regn_no=? where  state_cd=? and appl_no = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, new_regn_no);
            ps.setString(2, state_cd);
            ps.setString(3, appl_no);
            ps.executeUpdate();
        }
    }

    public String[] getRoutedetail(List route_code, String state_cd, int off_cd) {
        String[] via = null;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("getRoutedetail");
            for (Object code : route_code) {
                String Query = "Select code,floc,tloc,floc ||', '|| via ||','||tloc  as routedetail from " + TableList.VM_ROUTE_MASTER + " where code = ? AND State_cd = ? AND off_cd = ?";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, (String) code);
                ps.setString(2, state_cd);
                ps.setInt(3, off_cd);
                RowSet rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    via = new String[4];
                    via[0] = rs.getString("code");
                    via[1] = rs.getString("floc");
                    via[2] = rs.getString("tloc");
                    via[3] = rs.getString("routedetail");
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
        return via;
    }

    public boolean isOtherOfficeRoute(String appl_no, String tableName, String state_cd, int off_cd) {
        boolean otherOfficeRouteFound = false;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManagerReadOnly("isOtherOfficeRoute");
            String query;
            query = "select off_cd from " + tableName + " where appl_no=? and state_cd=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, appl_no);
            ps.setString(2, state_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                if (rs.getInt("off_cd") != off_cd) {
                    otherOfficeRouteFound = true;
                    break;
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return otherOfficeRouteFound;
    }
}
