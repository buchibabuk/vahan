package nic.vahan.dashBoard;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.impl.SeatAllotedDetails;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author Dhananjay
 */
public class PendingApplDashBoardImpl {

    private static final Logger LOGGER = Logger.getLogger(PendingApplDashBoardImpl.class);

    public PendingApplDashBoardDobj getActionWiseData(String stateCd, int offCd) throws VahanException {
        TreeNode root = new DefaultTreeNode(new PendingApplTreeNodeDobj("Action", 0, 0, 0, "-", "-", 0), null);
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        TreeNode subRoot1 = null;
        TreeNode subRoot2 = null;
        TreeNode subRoot3 = null;
        PendingApplDashBoardDobj dobj = new PendingApplDashBoardDobj();
        String parent = null;
        String subParent = null;
        int purCd = 0;
        int actionCd = 0;
        int step = 0;
        String purposeTypeCd = null;
        String viewType = "Action";

        try {
            tmgr = new TransactionManager("createAllPendingAppl");
            ps = tmgr.prepareStatement("select off_name,off_cd,purpose_type_cd,purpose_type_desc,pur_cd,purpose_desc,action_cd,action_desc,total from dashboard.get_pending_actions(?, ?)");
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (!CommonUtils.isNullOrBlank(rs.getString("off_Name"))) {
                    String countSubString = rs.getString("off_Name").substring(0, rs.getString("off_Name").indexOf("(")) + "</b>";
                    dobj.setOffName(countSubString);
                    dobj.setTreeNodeTotalActionWise(rs.getInt("total"));
                } else if (!CommonUtils.isNullOrBlank(rs.getString("purpose_type_desc"))) {
                    purCd = rs.getInt("pur_cd");
                    actionCd = rs.getInt("action_cd");
                    purposeTypeCd = rs.getString("purpose_type_cd");
                    step = 1;
                    subRoot1 = new DefaultTreeNode(new PendingApplTreeNodeDobj(rs.getString("purpose_type_desc"), rs.getInt("total"), purCd, actionCd, viewType, purposeTypeCd, step), root);
                } else if (!CommonUtils.isNullOrBlank(rs.getString("purpose_desc"))) {
                    purCd = rs.getInt("pur_cd");
                    actionCd = rs.getInt("action_cd");
                    step = 2;
                    purposeTypeCd = rs.getString("purpose_type_cd");
                    subRoot2 = new DefaultTreeNode(new PendingApplTreeNodeDobj(rs.getString("purpose_desc"), rs.getInt("total"), purCd, actionCd, viewType, purposeTypeCd, step), subRoot1);
                } else if (!CommonUtils.isNullOrBlank(rs.getString("action_desc"))) {
                    purCd = rs.getInt("pur_cd");
                    actionCd = rs.getInt("action_cd");
                    step = 3;
                    purposeTypeCd = rs.getString("purpose_type_cd");
                    subRoot3 = new DefaultTreeNode(new PendingApplTreeNodeDobj(rs.getString("action_desc"), rs.getInt("total"), purCd, actionCd, viewType, purposeTypeCd, step), subRoot2);
                }
            }
            dobj.setTreeNodeActionWise(root);
        } catch (SQLException | NumberFormatException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during getting details ,Please contact to the system administrator.");
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

    public PendingApplDashBoardDobj getVhclassWiseData(String stateCd, int offCd) throws VahanException {
        TreeNode root = new DefaultTreeNode(new PendingApplTreeNodeDobj("Action", 0, "-", 0, 0, "-", 0), null);
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        TreeNode subRoot1 = null;
        TreeNode subRoot2 = null;
        TreeNode subRoot3 = null;
        int norms = 0;
        int step = 0;
        int vhClass = 0;
        String regnType = null;
        String viewType = "RegnType";
        PendingApplDashBoardDobj dobj = new PendingApplDashBoardDobj();

        try {
            tmgr = new TransactionManager("createAllPendingAppl");
            ps = tmgr.prepareStatement(" select off_name,off_cd,regn_type,regn_type_desc,norms,norms_desc,vh_class,vh_class_desc,total  from dashboard.get_pending_vh_class(?, ?)");
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            RowSet rsVhClass = tmgr.fetchDetachedRowSet();
            while (rsVhClass.next()) {
                if (!CommonUtils.isNullOrBlank(rsVhClass.getString("off_Name"))) {
                    dobj.setTreeNodeTotalVhClassWise(rsVhClass.getInt("total"));
                } else if (!CommonUtils.isNullOrBlank(rsVhClass.getString("regn_type_desc"))) {
                    norms = rsVhClass.getInt("norms");
                    vhClass = rsVhClass.getInt("vh_class");
                    regnType = rsVhClass.getString("regn_type");
                    step = 1;
                    subRoot1 = new DefaultTreeNode(new PendingApplTreeNodeDobj(rsVhClass.getString("regn_type_desc"), rsVhClass.getInt("total"), regnType, norms, vhClass, viewType, step), root);
                } else if (!CommonUtils.isNullOrBlank(rsVhClass.getString("norms_desc"))) {
                    norms = rsVhClass.getInt("norms");
                    vhClass = rsVhClass.getInt("vh_class");
                    regnType = rsVhClass.getString("regn_type");
                    step = 2;
                    subRoot2 = new DefaultTreeNode(new PendingApplTreeNodeDobj(rsVhClass.getString("norms_desc"), rsVhClass.getInt("total"), regnType, norms, vhClass, viewType, step), subRoot1);
                } else if (!CommonUtils.isNullOrBlank(rsVhClass.getString("vh_class_desc"))) {
                    norms = rsVhClass.getInt("norms");
                    vhClass = rsVhClass.getInt("vh_class");
                    regnType = rsVhClass.getString("regn_type");
                    step = 3;
                    subRoot3 = new DefaultTreeNode(new PendingApplTreeNodeDobj(rsVhClass.getString("vh_class_desc"), rsVhClass.getInt("total"), regnType, norms, vhClass, viewType, step), subRoot2);
                }
            }
            dobj.setTreeNodeVhClassWise(root);
        } catch (SQLException | NumberFormatException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during getting details ,Please contact to the system administrator.");
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

    public PendingApplDashBoardDobj getPermitWiseData(String stateCd, int offCd) throws VahanException {
        TreeNode root = new DefaultTreeNode(new PendingApplTreeNodeDobj("Action", 0, 0, 0, "-"), null);
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        TreeNode subRoot1 = null;
        TreeNode subRoot2 = null;
        TreeNode subRoot3 = null;
        PendingApplDashBoardDobj dobj = new PendingApplDashBoardDobj();
        int pmtType = 0;
        int pmtCatg = 0;
        String viewType = "Permit";

        try {
            tmgr = new TransactionManager("createAllPendingAppl");
            ps = tmgr.prepareStatement("select off_name,off_cd,pmt_type,pmt_type_desc,pmt_catg,pmt_catg_desc,total from dashboard.get_pending_permit(?, ?);");
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            RowSet rsPermit = tmgr.fetchDetachedRowSet();
            while (rsPermit.next()) {
                if (!CommonUtils.isNullOrBlank(rsPermit.getString("off_Name"))) {
                    dobj.setTreeNodeTotalPermitWise(rsPermit.getInt("total"));
                } else if (!CommonUtils.isNullOrBlank(rsPermit.getString("pmt_type_desc"))) {
                    pmtType = rsPermit.getInt("pmt_type");
                    pmtCatg = rsPermit.getInt("pmt_catg");
                    subRoot1 = new DefaultTreeNode(new PendingApplTreeNodeDobj(rsPermit.getString("pmt_type_desc"), rsPermit.getInt("total"), pmtType, pmtCatg, viewType), root);
                } else if (!CommonUtils.isNullOrBlank(rsPermit.getString("pmt_catg_desc"))) {
                    pmtType = rsPermit.getInt("pmt_type");
                    pmtCatg = rsPermit.getInt("pmt_catg");
                    subRoot2 = new DefaultTreeNode(new PendingApplTreeNodeDobj(rsPermit.getString("pmt_catg_desc"), rsPermit.getInt("total"), pmtType, pmtCatg, viewType), subRoot1);
                }
            }
            dobj.setTreeNodePermitWise(root);
        } catch (SQLException | NumberFormatException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during getting details ,Please contact to the system administrator.");
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

    public static ArrayList<DashboardDetails> dashBoardSeatWorkList(int offCd, PendingApplTreeNodeDobj dobj, int limit, int offset) throws VahanException {
        ArrayList<DashboardDetails> list = new ArrayList<>();
        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmg = null;
        String pname = "";


        try {
            tmg = new TransactionManagerReadOnly("dashBoardSeatWorkList");
            if (dobj.getViewType().equalsIgnoreCase("Action")) {
                if (dobj.getStep() == 2) {
                    pname = "and va.pur_cd=" + dobj.getPurCd();
                } else if (dobj.getStep() == 3) {
                    pname = "and va.pur_cd=" + dobj.getPurCd() + " and va.action_cd=" + dobj.getActionCd();
                }
                sql = "select  va.appl_no,to_char(dtls.appl_dt, 'dd-Mon-yyyy')::varchar as appl_dt ,dtls.appl_dt as applDt, to_char(va.op_dt, 'dd-Mon-yyyy')::varchar as pending_since, dtls.regn_no, va.pur_cd, d.descr as purpose, va.action_cd, c.action_descr,va.file_movement_slno, va.status, va.seat_cd ,e.descr as fee_type_descr,d.fee_type as fee_type,c.redirect_url as redirect_url "
                        + " from va_status va left outer join va_details dtls on va.appl_no = dtls.appl_no and va.pur_cd = dtls.pur_cd \n"
                        + " left outer join tm_action c on va.action_cd=c.action_cd \n"
                        + " left outer join tm_purpose_mast d on va.pur_cd = d.pur_cd \n"
                        + " left outer join vahan4.tm_purpose_types e on e.code = d.fee_type  \n"
                        + " where va.state_cd = ? and va.off_cd =?  and d.fee_type=? " + pname + " order by applDt  LIMIT " + limit + " OFFSET " + offset;
                ps = tmg.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, offCd);
                ps.setString(3, dobj.getPurposeTypeCd());
            } else if (dobj.getViewType().equalsIgnoreCase("RegnType")) {
                if (dobj.getStep() == 2) {
                    pname = "And owner.norms = " + dobj.getNorms();
                } else if (dobj.getStep() == 3) {
                    pname = "And owner.norms = " + dobj.getNorms() + " And  owner.vh_class = " + dobj.getVhClass();
                }

                sql = "select  va.appl_no,to_char(dtls.appl_dt, 'dd-Mon-yyyy')::varchar as appl_dt ,to_char(va.op_dt, 'dd-Mon-yyyy')::varchar as pending_since,dtls.appl_dt as applDt, dtls.regn_no, va.pur_cd, d.descr as purpose, va.action_cd, c.action_descr,va.file_movement_slno, va.status, va.seat_cd, \n"
                        + " owner.regn_type as regn_type,b.descr as regnType_descr,owner.norms as norms,e.descr as norms_descr, owner.vh_class as vh_class,f.descr as vhClass_descr,c.redirect_url as redirect_url \n"
                        + " from va_status va left outer join va_details dtls on va.appl_no = dtls.appl_no and va.pur_cd = dtls.pur_cd \n"
                        + " left outer join tm_action c on va.action_cd=c.action_cd \n"
                        + " left outer join tm_purpose_mast d on va.pur_cd = d.pur_cd \n"
                        + " left outer join va_owner owner on va.appl_no = owner.appl_no \n"
                        + " left outer join vahan4.vm_regn_type b on b.regn_typecode = owner.regn_type \n"
                        + "left outer join vahan4.vm_norms e on e.code = owner.norms \n"
                        + "left outer join vahan4.vm_vh_class f on f.vh_class = owner.vh_class \n"
                        + " where va.state_cd= ? and va.off_cd= ? and owner.regn_type= ? " + pname + " order by applDt LIMIT " + limit + " OFFSET " + offset;
                ps = tmg.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, offCd);
                ps.setString(3, dobj.getRegnType());
            } else if (dobj.getViewType().equalsIgnoreCase("Permit")) {
                if (dobj.getPmtCatg() != 0) {
                    pname = "And permit.pmt_catg = " + dobj.getPmtCatg();
                }
                sql = "select  va.appl_no,to_char(dtls.appl_dt, 'dd-Mon-yyyy')::varchar as appl_dt ,to_char(va.op_dt, 'dd-Mon-yyyy')::varchar as pending_since,"
                        + " dtls.appl_dt as applDt,permit.pmt_type as pmt_type,pt.descr as pmtType_descr,permit.pmt_catg as pmt_catg,pc.descr as pmt_catg_descr, dtls.regn_no, va.pur_cd, d.descr as purpose, va.action_cd, c.action_descr,va.file_movement_slno, va.status, va.seat_cd ,c.redirect_url as redirect_url \n"
                        + " from va_status va left outer join va_details dtls on va.appl_no = dtls.appl_no and va.pur_cd = dtls.pur_cd \n"
                        + " left outer join tm_action c on va.action_cd=c.action_cd \n"
                        + " left outer join tm_purpose_mast d on va.pur_cd = d.pur_cd \n"
                        + " left outer join permit.va_permit permit on va.appl_no = permit.appl_no \n"
                        + " left outer join permit.vm_permit_type pt on pt.code = permit.pmt_type  "
                        + "  left outer join permit.vm_permit_catg pc on pc.state_cd = permit.state_cd and pc.permit_type = permit.pmt_type and pc.code = permit.pmt_catg "
                        + " where va.state_cd= ? and va.off_cd= ? and permit.pmt_type = ? " + pname + " order by applDt LIMIT " + limit + " OFFSET " + offset;
                ps = tmg.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, offCd);
                ps.setInt(3, dobj.getPmtType());
            }

            RowSet rs = tmg.fetchDetachedRowSet();
            while (rs.next()) {
                DashboardDetails wd = new DashboardDetails();

                wd.setAppl_no(rs.getString("appl_no"));
                wd.setAppl_dt(rs.getString("appl_dt"));
                wd.setPendingSince(rs.getString("pending_since"));
                wd.setRegn_no(rs.getString("regn_no"));
                wd.setPurCd(rs.getInt("pur_cd"));
                wd.setPurpose_descr(rs.getString("purpose"));
                wd.setAction_cd(rs.getInt("action_cd"));
                wd.setAction_descr(rs.getString("action_descr"));
                wd.setStatus(rs.getString("status"));
                if (dobj.getViewType().equalsIgnoreCase("Action")) {
                    if (!CommonUtils.isNullOrBlank(rs.getString("fee_type_descr"))) {
                        wd.setFeeType_descr(rs.getString("fee_type_descr"));
                    } else {
                        wd.setFeeType_descr(rs.getString("fee_type"));
                    }
                } else if (dobj.getViewType().equalsIgnoreCase("RegnType")) {
                    wd.setVhClass(rs.getInt("vh_class"));
                    wd.setVhClass_descr(rs.getString("vhClass_descr"));
                    wd.setNorms(rs.getInt("norms"));
                    if (!CommonUtils.isNullOrBlank(rs.getString("norms_descr"))) {
                        wd.setNomrs_descr(rs.getString("norms_descr"));
                    } else {
                        wd.setNomrs_descr(String.valueOf(rs.getInt("norms")));
                    }
                    wd.setRegnType(rs.getString("regn_type"));
                    wd.setRegnType_descr(rs.getString("regnType_descr"));
                } else if (dobj.getViewType().equalsIgnoreCase("Permit")) {
                    wd.setPmtCatg(rs.getInt("pmt_catg"));
                    wd.setPmtType(rs.getInt("pmt_type"));
                    wd.setPmtType_descr(rs.getString("pmtType_descr"));
                    if (!CommonUtils.isNullOrBlank(rs.getString("pmt_catg_descr"))) {
                        wd.setPmtCatg_descr(rs.getString("pmt_catg_descr"));
                    } else {
                        wd.setPmtCatg_descr(String.valueOf(rs.getInt("pmt_catg")));
                    }
                }
                SeatAllotedDetails selectedSeat = new SeatAllotedDetails();
                selectedSeat.setAppl_no(rs.getString("appl_no"));
                selectedSeat.setAppl_dt(rs.getString("appl_dt"));
                selectedSeat.setRegn_no(rs.getString("regn_no"));
                selectedSeat.setPur_cd(rs.getInt("pur_cd"));
                selectedSeat.setPurpose_descr(rs.getString("purpose"));
                selectedSeat.setAction_cd(rs.getInt("action_cd"));
                selectedSeat.setAction_descr(rs.getString("action_descr"));
                selectedSeat.setFile_movement_slno(rs.getInt("file_movement_slno"));
                selectedSeat.setStatus(rs.getString("status"));
                selectedSeat.setRedirect_url(rs.getString("redirect_url"));
                wd.setSelectedSeat(selectedSeat);
                list.add(wd);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmg != null) {
                    tmg.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return list;
    }

    public boolean dashBoardActionOnSelectedAppl(DashboardDetails dobj, int offCd) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmg = null;
        boolean userAllowForAppl = false;
        try {
            tmg = new TransactionManagerReadOnly("dashBoardActionOnSelectedAppl");
            sql = " select appl_no, appl_dt,regn_no,pur_cd,purpose,action_cd,action_descr,office_remark,public_remark,file_movement_slno,status,redirect_url,seat_cd\n"
                    + " from get_user_pending_work_appl_no(?,?,?)";
            ps = tmg.prepareStatement(sql);
            ps.setString(1, dobj.getAppl_no());
            ps.setLong(2, Long.parseLong(Util.getEmpCode()));
            ps.setInt(3, offCd);
            RowSet rs = tmg.fetchDetachedRowSet();
            if (rs.next()) {
                userAllowForAppl = true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmg != null) {
                    tmg.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return userAllowForAppl;
    }
}
