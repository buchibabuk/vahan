/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.RowSet;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.user_mgmt.dobj.ActionMgmtDobj;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author tranC103
 */
public class ActionMgmtImpl {

    private static final Logger LOGGER = Logger.getLogger(ActionMgmtImpl.class);

    public static TreeNode createTreeStructure(ActionMgmtDobj dobj) {
        Integer actionType = dobj.getSelectedAction_type();
        TreeNode root = dobj.getRoot();
        TreeNode actionTypeNode;
        TreeNode actionNode;

        root = new DefaultTreeNode("Root", null);
        root.setExpanded(true);
        String temp = roleConverter(actionType.toString());
        actionTypeNode = new DefaultTreeNode(temp, root);
        actionTypeNode.setExpanded(true);

        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;

        try {
            tmgr = new TransactionManager("CreateTreeStructure");
            ps = tmgr.prepareStatement("SELECT action_cd, action_descr FROM " + TableList.TM_ACTION + " where role_cd=? order by action_cd");
            ps.setInt(1, actionType);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                String str = rs.getString(1) + ": " + rs.getString(2);
                actionNode = new DefaultTreeNode(str, actionTypeNode);
                actionNode.setExpanded(true);
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

        return root;
    }

    private static String roleConverter(String r_code) {
        RowSet rs1 = null;
        PreparedStatement ps1 = null;
        TransactionManager tmgr = null;
        String str = "";
        int purpose_code = Integer.parseInt(r_code);
        try {
            tmgr = new TransactionManager("purposeConverter");
            String sql = "select * from " + TableList.TM_ROLE + " where role_cd=?";
            ps1 = tmgr.prepareStatement(sql);
            ps1.setInt(1, purpose_code);
            rs1 = tmgr.fetchDetachedRowSet();
            while (rs1.next()) {
                str = rs1.getInt(1) + ": " + rs1.getString(2);
            }
            return str;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            return "";
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

    public static boolean checkActionCode(ActionMgmtDobj dobj) {
        Integer act_cd = dobj.getAction_cd();
        boolean flag = false;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("fillDesignationList");
            PreparedStatement ps = null;
            RowSet rs = null;

            String sql = "select * from " + TableList.TM_ACTION + " where action_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, act_cd);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                flag = true;
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
        return flag;

    }

    public static String saveActionRecord(ActionMgmtDobj dobj) {
        if (dobj.getAction_cd() != null && dobj.getSelectedAction_type() != null && dobj.getAction_descr() != null && dobj.getAction_abbrv() != null) {
            TransactionManager tmgr = null;
            PreparedStatement ps = null;
            try {
                tmgr = new TransactionManager("saveActionRecord");
                String sql = "INSERT INTO " + TableList.TM_ACTION + "(action_cd, action_descr, action_abbrv,role_cd)VALUES (?, ?, ?, ?)";
                ps = tmgr.prepareStatement(sql);
                ps.setInt(1, dobj.getAction_cd());
                ps.setString(2, dobj.getAction_descr());
                ps.setString(3, dobj.getAction_abbrv());
                ps.setInt(4, dobj.getSelectedAction_type());

                ps.executeUpdate();

                tmgr.commit();
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } finally {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
            return "Action Saved Successfully!!!";
        } else {
            return "Please Fill Required fields!!!";
        }
    }
}
