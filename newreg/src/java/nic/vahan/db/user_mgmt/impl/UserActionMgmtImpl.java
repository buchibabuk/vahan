/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.impl;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.user_mgmt.dobj.UserActionMgmtDobj;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author tranC102
 */
public class UserActionMgmtImpl implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(UserActionMgmtImpl.class);

    //Employee List Function
    public static List fillEmpList(String state_cd, List offUnder, String user_catg) throws VahanException {
        List empList = new ArrayList();
        PreparedStatement ps;
        TransactionManager tmgr = null;
        RowSet rs;

        try {
            tmgr = new TransactionManager("fillEmpList");
            if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_PORTAL_ADMIN)) {
                String sql = "Select user_cd,user_id,user_name,desig_name from " + TableList.TM_USER_INFO + " e," + TableList.TM_DESIGNATION + " d where"
                        + " e.state_cd=? and e.user_catg=? and d.desig_cd=e.desig_cd";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, state_cd);
                ps.setString(2, TableConstants.USER_CATG_STATE_ADMIN);
                rs = tmgr.fetchDetachedRowSet();
                while (rs.next()) {
                    empList.add(new SelectItem(rs.getString("user_cd"), rs.getString("user_id") + "(" + rs.getString("user_name") + ")"));
                }
            }
            if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_STATE_ADMIN)) {
                String sql = "Select user_cd,user_id,user_name,desig_name from " + TableList.TM_USER_INFO + " e," + TableList.TM_DESIGNATION + " d where"
                        + " e.state_cd=? and  e.user_catg IN ('A','R','F','W','V') and d.desig_cd=e.desig_cd";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, state_cd);
                rs = tmgr.fetchDetachedRowSet();
                while (rs.next()) {
                    empList.add(new SelectItem(rs.getString("user_cd"), rs.getString("user_id") + "(" + rs.getString("user_name") + ")"));
                }
            }
            if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_OFFICE_ADMIN)) {
                for (Object offCode : offUnder) {
                    String sql = "Select b.user_cd,b.user_id,b.user_name,d.desig_name from " + TableList.TM_USER_INFO + " b\n"
                            + "inner join " + TableList.TM_DESIGNATION + " d on b.desig_cd = d.desig_cd \n"
                            + "where b.user_cd IN (Select user_cd from " + TableList.TM_USER_PERMISSIONS + " a\n"
                            + "where state_cd = ? and a.assigned_office!='ANY' and ? = ANY(string_to_array(a.assigned_office,',')::numeric[]) order by user_cd) and b.user_catg IN ('L','Y','X','D','Z')";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, state_cd);
                    ps.setInt(2, Integer.parseInt(offCode.toString()));
                    rs = tmgr.fetchDetachedRowSet();
                    while (rs.next()) {
                        empList.add(new SelectItem(rs.getString("user_cd"), rs.getString("user_id") + "(" + rs.getString("user_name") + ")"));
                    }
                }
            }
            if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER_ADMIN)) {
                String dealerCode = (String) OwnerImpl.getDealerDetail(Long.parseLong(Util.getEmpCode().toString())).get("dealer_cd");
                for (Object offCode : offUnder) {
                    String sql = " Select b.user_cd,b.user_id,b.user_name,d.desig_name from " + TableList.TM_USER_INFO + "  b \n"
                            + "inner join  " + TableList.TM_DESIGNATION + " d on b.desig_cd = d.desig_cd \n"
                            + "where state_cd = ? and b.user_cd IN (Select user_cd from " + TableList.TM_USER_PERMISSIONS + " a where a.assigned_office!='ANY' and ? = ANY(string_to_array(a.assigned_office,',')::numeric[])"
                            + " and dealer_cd = ? order by user_cd) "
                            + "and b.user_catg = ?  order by user_name";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, state_cd);
                    ps.setInt(2, Integer.parseInt(offCode.toString()));
                    ps.setString(3, dealerCode);
                    ps.setString(4, TableConstants.USER_CATG_DEALER);

                    rs = tmgr.fetchDetachedRowSet();
                    while (rs.next()) {
                        empList.add(new SelectItem(rs.getString("user_cd"), rs.getString("user_id") + "(" + rs.getString("user_name") + ")"));
                    }
                }
            }
            if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_ADMIN)) {
                for (Object offCode : offUnder) {
                    String sql = "Select b.user_cd,b.user_id,b.user_name,d.desig_name from " + TableList.TM_USER_INFO + " b\n"
                            + "inner join " + TableList.TM_DESIGNATION + " d on b.desig_cd = d.desig_cd \n"
                            + "where b.user_cd IN (Select user_cd from " + TableList.TM_USER_PERMISSIONS + " a\n"
                            + "where state_cd = ? and a.assigned_office!='ANY' and ? = ANY(string_to_array(a.assigned_office,',')::numeric[]) order by user_cd) and b.user_catg IN ('E')";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, state_cd);
                    ps.setInt(2, Integer.parseInt(offCode.toString()));
                    rs = tmgr.fetchDetachedRowSet();
                    while (rs.next()) {
                        empList.add(new SelectItem(rs.getString("user_cd"), rs.getString("user_id") + "(" + rs.getString("user_name") + ")"));
                    }
                }
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            throw new VahanException("Something went wrong during filling employee list, please contact to the systeme administrator.");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong during filling employee list, please contact to the systeme administrator.");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return empList;
    }

    //Action List Function
    public static void implFillActionList(UserActionMgmtDobj dobj) throws VahanException {
        List<String> actionSource = dobj.getActionSource();
        List<String> actionTarget = dobj.getActionTarget();
        List office_code = dobj.getSelectedOfficeCode();
        String selectedEmp = dobj.getSelectedEmp();
        int selectedPurpose = Integer.parseInt(dobj.getSelectedPurpose());
        RowSet rs = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("implFillActionList");
            actionSource.clear();
            actionTarget.clear();
            String sql = "";
            if (dobj.getUser_categ().equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_USER)) {
                sql = "SELECT action_cd,action_abbrv,state_cd from tm_action where role_cd = ? and state_cd = ? and user_catg = ?\n"
                        + "UNION\n"
                        + "SELECT action_cd,action_abbrv,state_cd from tm_action where role_cd = ? and user_catg = ? and  ? = ANY(string_to_array(state_cd,','));";
                ps = tmgr.prepareStatement(sql);
                ps.setInt(1, selectedPurpose);
                ps.setString(2, "ALL");
                ps.setString(3, dobj.getUser_categ());
                ps.setInt(4, selectedPurpose);
                ps.setString(5, dobj.getUser_categ());
                ps.setString(6, Util.getUserStateCode());
            } else {
                sql = "SELECT action_cd,action_abbrv,state_cd from tm_action where role_cd = ? and state_cd = ?\n"
                        + "UNION\n"
                        + "SELECT action_cd,action_abbrv,state_cd from tm_action where role_cd = ? and  ? = ANY(string_to_array(state_cd,','));";
                ps = tmgr.prepareStatement(sql);
                ps.setInt(1, selectedPurpose);
                ps.setString(2, "ALL");
                ps.setInt(3, selectedPurpose);
                ps.setString(4, Util.getUserStateCode());
            }

            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                String str = rs.getInt(1) + ": " + rs.getString(2);
                actionSource.add(str);
            }

            implFillActionTargetList(actionSource, actionTarget, selectedEmp, selectedPurpose, office_code);
        } catch (SQLException e) {
            throw new VahanException("implFillActionList : " + e.getMessage());
        } catch (Exception e) {
            throw new VahanException("implFillActionList : " + e.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                throw new VahanException("implFillActionList : " + e.getMessage());
            }
        }
    }

    //Function to fill the Target List
    private static void implFillActionTargetList(List<String> actionSource, List<String> actionTarget, String selectedEmp, int selectedPurpose, List selectedOff_cd) throws VahanException {
        RowSet rs = null;
        PreparedStatement ps = null;
        int e_code = Integer.parseInt(selectedEmp);
        int p_code = selectedPurpose;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("implFillActionTargetList");
            if (selectedOff_cd == null) {
                actionTarget.clear();
                String sql1 = "select action_cd from " + TableList.TM_OFF_EMP_ACTION + " where user_cd=?";
                ps = tmgr.prepareStatement(sql1);
                ps.setInt(1, e_code);

            } else {
                for (Object tmp : selectedOff_cd) {
                    if (tmp.equals("ANY")) {
                        String sql1 = "select action_cd from " + TableList.TM_OFF_EMP_ACTION + " where user_cd=?";
                        ps = tmgr.prepareStatement(sql1);
                        ps.setInt(1, e_code);
                    } else {
                        int off_cd = Integer.parseInt(tmp.toString());
                        actionTarget.clear();
                        String sql1 = "select action_cd from " + TableList.TM_OFF_EMP_ACTION + " where user_cd=? and off_cd=?";
                        ps = tmgr.prepareStatement(sql1);
                        ps.setInt(1, e_code);
                        ps.setInt(2, off_cd);
                    }
                }
            }
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                int tmp1;
                tmp1 = rs.getInt(1);
                actionTarget = implFillActionTargetList(actionTarget, tmp1);
            }
        } catch (SQLException | NumberFormatException e) {
            throw new VahanException("implFillActionTargetList : " + e.getMessage());
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    throw new VahanException("implFillActionTargetList : " + e.getMessage());
                }
            }
        }
        actionTarget.retainAll(actionSource);
        actionSource.removeAll(actionTarget);
        Collections.sort(actionTarget);
        Collections.sort(actionSource);
    }

    //Function to fill the Target List
    private static List<String> implFillActionTargetList(List<String> actionTarget, int a_code) throws VahanException {
        RowSet rs1 = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("implFillActionTargetList");
            String sql = "select action_cd,action_abbrv from " + TableList.TM_ACTION + " where action_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, a_code);
            rs1 = tmgr.fetchDetachedRowSet();
            while (rs1.next()) {
                String str1;
                str1 = rs1.getString(1) + ": " + rs1.getString(2);
                actionTarget.add(str1);
            }
        } catch (SQLException e) {
            throw new VahanException("implFillActionTargetList : " + e.getMessage());
        } catch (Exception e) {
            throw new VahanException("implFillActionTargetList : " + e.getMessage());
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    throw new VahanException("implFillActionTargetList : " + e.getMessage());
                }
            }
        }
        return actionTarget;
    }

    //Function to select that save function is called or delete function.
    public static String saveActionImpl(UserActionMgmtDobj dobj) throws VahanException {
        int of_cd = 0;
        String selectedEmp = dobj.getSelectedEmp();
        String selectedPurpose = dobj.getSelectedPurpose();
        String strTarget = dobj.getStrTarget();
        String stateCode = dobj.getStateCode();
        List officeCode = dobj.getSelectedOfficeCode();
        RowSet rs = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        Boolean flag = false;
        try {
            tmgr = new TransactionManager("saveActionImpl");
            int emp_code = 0;
            if (!"".equalsIgnoreCase(selectedEmp) && !"".equalsIgnoreCase(selectedPurpose) && !"".equalsIgnoreCase(strTarget)) {
                Date d = new Date();
                java.sql.Date da = new java.sql.Date(d.getTime());
                String m = "";
                String[] act = strTarget.split(",");
                if (officeCode.size() < 2) {
                    if (officeCode.get(0).toString().equalsIgnoreCase("ANY")) {
                        of_cd = 0;
                    } else {
                        of_cd = Integer.parseInt(officeCode.get(0).toString());
                    }
                    for (String act1 : act) {
                        String[] a_code = act1.split(":");
                        String selectedAction = a_code[0];
                        emp_code = Integer.parseInt(selectedEmp);
                        int act_code = Integer.parseInt(selectedAction);
                        String sql = "Select state_cd,off_cd,user_cd,action_cd from " + TableList.TM_OFF_EMP_ACTION + " where user_cd=? and action_cd=? and state_cd=? and off_cd=?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setInt(1, emp_code);
                        ps.setInt(2, act_code);
                        ps.setString(3, stateCode);
                        ps.setInt(4, of_cd);
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        while (rs.next()) {
                            flag = true;
                        }
                        if (flag) {
                            m = deleteActionEmpImpl(tmgr, selectedPurpose, of_cd, selectedAction, dobj);
                            flag = false;
                        } else {
                            m = saveEmpWithAssgnActionImpl(tmgr, selectedPurpose, of_cd, selectedAction, da, dobj);
                        }
                    }
                } else {
                    for (Object offCode : officeCode) {
                        of_cd = Integer.parseInt(offCode.toString());
                        for (String act1 : act) {
                            String[] a_code = act1.split(":");
                            String selectedAction = a_code[0];
                            emp_code = Integer.parseInt(selectedEmp);
                            int act_code = Integer.parseInt(selectedAction);
                            String sql = "Select state_cd,off_cd,user_cd,action_cd from " + TableList.TM_OFF_EMP_ACTION + " where user_cd=? and action_cd=? and state_cd=? and off_cd=?";
                            ps = tmgr.prepareStatement(sql);
                            ps.setInt(1, emp_code);
                            ps.setInt(2, act_code);
                            ps.setString(3, stateCode);
                            ps.setInt(4, of_cd);
                            rs = tmgr.fetchDetachedRowSet_No_release();
                            while (rs.next()) {
                                flag = true;
                            }
                            if (flag) {
                                m = deleteActionEmpImpl(tmgr, selectedPurpose, of_cd, selectedAction, dobj);
                                flag = false;
                            } else {
                                m = saveEmpWithAssgnActionImpl(tmgr, selectedPurpose, of_cd, selectedAction, da, dobj);
                            }
                        }
                    }
                }
                //if cashier role assign to any user insert data in TM_USER_OPEN_CASH.
                if (dobj != null && Integer.parseInt(dobj.getSelectedPurpose()) == TableConstants.USER_ROLE_CASHIER) {
                    UserMgmtImpl.insertCashierData(tmgr, stateCode, of_cd, emp_code);
                }
                tmgr.commit();
                return m;
            } else {
                return "Insufficient Data!!!";
            }
        } catch (NumberFormatException | SQLException e) {
            throw new VahanException("saveActionImpl : " + e.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                throw new VahanException("saveActionImpl : " + e.getMessage());
            }
        }
    }

    //Function for Saving the employee with selected purpose and assigned actions.
    private static String saveEmpWithAssgnActionImpl(TransactionManager tmgr, String selectedPurpose, int officeCode, String strTarget, Date da, UserActionMgmtDobj dobj) throws VahanException {
        PreparedStatement ps = null;
        String m = "";
        try {
            if (!"".equalsIgnoreCase(dobj.getSelectedEmp()) && !"".equalsIgnoreCase(selectedPurpose) && !"".equalsIgnoreCase(strTarget)) {
                int emp_code = Integer.parseInt(dobj.getSelectedEmp());
                int a_code = Integer.parseInt(strTarget);
                int enterBy = Integer.parseInt(dobj.getEnteredBy());
                String sql = "insert into " + TableList.TM_OFF_EMP_ACTION + " values (?,?,?,?,?,?)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getStateCode());
                ps.setInt(2, officeCode);
                ps.setInt(3, emp_code);
                ps.setInt(4, a_code);
                ps.setInt(5, enterBy);
                ps.setDate(6, (java.sql.Date) da);
                ps.executeUpdate();
                m = "Action Assigned Successfully!!!";
            } else {
                m = "Insufficient Data!!!";
            }
        } catch (SQLException e) {
            throw new VahanException("saveEmpWithAssgnActionImpl : " + e.getMessage());
        }
        return m;
    }

    //Function for removing the assigned action.
    private static String deleteActionEmpImpl(TransactionManager tmgr, String selectedPurpose, int officeCode, String strTarget, UserActionMgmtDobj dobj) throws VahanException {
        PreparedStatement ps = null;
        String m = "";
        try {
            if (!"".equalsIgnoreCase(dobj.getSelectedEmp()) && !"".equalsIgnoreCase(selectedPurpose) && !"".equalsIgnoreCase(strTarget)) {
                int emp_code = Integer.parseInt(dobj.getSelectedEmp());
                int a_code = Integer.parseInt(strTarget);
                String sql = "DELETE FROM " + TableList.TM_OFF_EMP_ACTION + " WHERE state_cd=? and off_cd=? and user_cd=? and action_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getStateCode());
                ps.setInt(2, officeCode);
                ps.setInt(3, emp_code);
                ps.setInt(4, a_code);
                ps.executeUpdate();
                m = "Action Removed Successfully!!!";
                return m;
            } else {
                m = "Insufficient Data!!!";
                return m;
            }
        } catch (SQLException e) {
            throw new VahanException("deleteActionEmpImpl : " + e.getMessage());
        }
    }

    //Function for creating tree structure of assigned action to employee with purpose's.
    public static TreeNode createTreeStructureImpl(UserActionMgmtDobj dobj) throws VahanException {
        TreeNode root = dobj.getRoot();
        String selectedEmp = dobj.getSelectedEmp();
        RowSet rs = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        int e_code = Integer.parseInt(selectedEmp);
        List p_code = new ArrayList<>();
        List a_code = new ArrayList<>();
        List off_code = new ArrayList<>();
        TreeNode p;
        TreeNode c;
        TreeNode sc;
        TreeNode ssc;
        try {
            tmgr = new TransactionManager("implCreateTreeStructure");
            root = new DefaultTreeNode("Root", null);
            root.setExpanded(true);
            String e_name = empConverter(selectedEmp);
            p = new DefaultTreeNode(e_name, root);
            p.setExpanded(true);

            String sqloff = "select distinct(off_cd) from " + TableList.TM_OFF_EMP_ACTION + " where user_cd=?";
            ps = tmgr.prepareStatement(sqloff);
            ps.setInt(1, e_code);
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                off_code.add(rs.getInt(1));
            }
            Collections.sort(off_code);
            for (Object of_cd : off_code) {
                String off_c = officeConverter(Integer.parseInt(of_cd.toString()), dobj.getState_cd());
                c = new DefaultTreeNode(off_c, p);
                c.setExpanded(true);
                // tmgr = new TransactionManager("implCreateTreeStructure");
                String sql = "select distinct(role_cd) from (\n"
                        + "select tm_action.action_cd,tm_action.role_cd from " + TableList.TM_ACTION + " inner join " + TableList.TM_OFF_EMP_ACTION + " on tm_action.action_cd = tm_off_emp_action.action_cd where tm_off_emp_action.user_cd = ? and tm_off_emp_action.off_cd=?) AS foo";
                ps = tmgr.prepareStatement(sql);
                ps.setInt(1, e_code);
                ps.setInt(2, Integer.parseInt(of_cd.toString()));
                rs = tmgr.fetchDetachedRowSet_No_release();
                while (rs.next()) {
                    p_code.add(rs.getInt(1));
                }

                String sql1;
                //tmgr = new TransactionManager("implCreateTreeStructure");
                sql1 = "select action_cd from (\n"
                        + "select tm_action.action_cd,tm_action.role_cd from " + TableList.TM_ACTION + " inner join " + TableList.TM_OFF_EMP_ACTION + " on tm_action.action_cd = tm_off_emp_action.action_cd where tm_off_emp_action.user_cd = ? and tm_off_emp_action.off_cd=?) AS foo where role_cd=?";
                ps = tmgr.prepareStatement(sql1);
                ps.setInt(1, e_code);
                ps.setInt(2, Integer.parseInt(of_cd.toString()));
                Collections.sort(p_code);
                for (Object tmp : p_code) {
                    String p_name = purposeConverter(tmp.toString());
                    sc = new DefaultTreeNode(p_name, c);
                    sc.setExpanded(true);

                    ps.setInt(3, Integer.parseInt(tmp.toString()));
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    while (rs.next()) {
                        a_code.add(rs.getInt(1));
                    }
                    Collections.sort(a_code);
                    for (Object y : a_code) {
                        String a_name = actionConverter(y.toString());
                        ssc = new DefaultTreeNode(a_name, sc);
                    }
                    a_code.clear();
                }
                p_code.clear();
            }
        } catch (SQLException e) {
            throw new VahanException("createTreeStructureImpl : " + e.getMessage());
        } catch (Exception e) {
            throw new VahanException("createTreeStructureImpl : " + e.getMessage());
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    throw new VahanException("createTreeStructureImpl : " + e.getMessage());
                }
            }
        }
        return root;
    }

    //Function for converting emp_code to string of employee code with name.
    private static String empConverter(String e_code) throws VahanException {
        RowSet rs = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String str = "";
        int emp_code = Integer.parseInt(e_code);
        try {
            tmgr = new TransactionManager("empConverter");
            String sql = "select user_name,desig_name from " + TableList.TM_USER_INFO + " e," + TableList.TM_DESIGNATION + " d "
                    + "where user_cd=? and e.desig_cd=d.desig_cd";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, emp_code);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                str = rs.getString("user_name") + "(" + rs.getString("desig_name") + ")";
            }
        } catch (SQLException e) {
            throw new VahanException("empConverter : " + e.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                throw new VahanException("empConverter : " + e.getMessage());
            }
        }
        return str;
    }

    //Function for converting p_code to string of purpose code with description.
    private static String purposeConverter(String p_code) throws VahanException {
        RowSet rs1 = null;
        PreparedStatement ps1 = null;
        TransactionManager tmgr = null;
        String str = "";
        int purpose_code = Integer.parseInt(p_code);
        try {
            tmgr = new TransactionManager("purposeConverter");
            String sql = "select role_descr from " + TableList.TM_ROLE + " where role_cd=?";
            ps1 = tmgr.prepareStatement(sql);
            ps1.setInt(1, purpose_code);
            rs1 = tmgr.fetchDetachedRowSet();
            while (rs1.next()) {
                str = rs1.getString("role_descr");
            }
        } catch (SQLException e) {
            throw new VahanException("purposeConverter : " + e.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                throw new VahanException("purposeConverter : " + e.getMessage());
            }
        }
        return str;
    }

    //Function for converting a_code to string of action code with description.
    private static String actionConverter(String a_code) throws VahanException {
        RowSet rs1 = null;
        PreparedStatement ps1 = null;
        TransactionManager tmgr = null;
        String str = "";
        int action_code = Integer.parseInt(a_code);
        try {
            tmgr = new TransactionManager("actionConverter");
            String sql = "select action_abbrv from " + TableList.TM_ACTION + " where action_cd=?";
            ps1 = tmgr.prepareStatement(sql);
            ps1.setInt(1, action_code);
            rs1 = tmgr.fetchDetachedRowSet();
            if (rs1.next()) {
                str = rs1.getString("action_abbrv");
            }
        } catch (SQLException e) {
            throw new VahanException("actionConverter : " + e.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                throw new VahanException("actionConverter : " + e.getMessage());
            }
        }
        return str;
    }

    //Function for office code converter
    private static String officeConverter(int off_cd, String state_cd) throws VahanException {
        String str = "";
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;

        String sql = "Select off_name from " + TableList.TM_OFFICE + " where off_cd=? and state_cd=?";
        try {
            tmgr = new TransactionManager("officeConverter");
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, off_cd);
            ps.setString(2, state_cd);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                str = rs.getString("off_name");
            }
        } catch (SQLException e) {
            throw new VahanException("officeConverter : " + e.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                throw new VahanException("officeConverter : " + e.getMessage());
            }
        }
        return str;
    }
}
