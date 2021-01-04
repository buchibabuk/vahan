/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.admin;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;
import javax.sql.RowSet;

import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.UserMessageDobj;

import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author DELL
 */
public class UserMsgByCatgImpl {

    private static final Logger LOGGER = Logger.getLogger(UserMsgByCatgImpl.class);

    public static void sendMsgByUserCatg(String stateCd, Integer offCd, List<String> userMsgByCatgList, String message) throws VahanException {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = "";


        try {

            tmgr = new TransactionManager("sendMsgByUserCatg");
            int counter = 0;
            sql = "INSERT INTO " + TableList.VT_USER_MSG + "(from_user_cd,from_state_cd,from_off_cd,to_user_cd,\n"
                    + "copy_to_user,to_state_cd,to_off_cd,message,msg_dt,read_flag,upload_state_cd,upload_off_cd,doc_ref_no,to_user_catg)\n"
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            ps = tmgr.prepareStatement(sql);
            for (int i = 0; i < userMsgByCatgList.size(); i++) {
                counter++;
                ps.setLong(1, Long.parseLong(Util.getEmpCode()));
                ps.setString(2, stateCd);
                ps.setInt(3, offCd);
                ps.setInt(4, 0);
                ps.setInt(5, 0);
                ps.setString(6, stateCd);
                ps.setInt(7, 0);
                ps.setString(8, message);
                ps.setTimestamp(9, ServerUtil.getSystemDateInPostgres());
                ps.setBoolean(10, false);
                ps.setString(11, "");
                ps.setInt(12, 0);
                ps.setString(13, "");

                ps.setString(14, userMsgByCatgList.get(i));
                ps.addBatch();
            }
            int[] updatedCount = ps.executeBatch();
            if (counter != 0 && updatedCount.length == counter) {
                tmgr.commit();

            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            String nextex = ex.getNextException().toString();
            throw new VahanException("there is some problem in sending message !!!!!");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

    }

    public static List<UserMessageDobj> getMessagesListByCatg(String userCatg, String fromStateCd) throws VahanException {
        List<UserMessageDobj> listUserMessages = new ArrayList<UserMessageDobj>();
        UserMessageDobj dobj = null;


        String sql = "Select (select user_name from " + TableList.TM_USER_INFO + " where user_cd=vum.from_user_cd) as username,"
                + "vum.message,to_char(vum.msg_dt,'dd-mm-yyyy hh24:mi:ss') as mesg_dt\n"
                + " from " + TableList.VT_USER_MSG + " vum"
                + " where from_state_cd=? and to_user_catg=? order by msg_dt desc";
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getMessagesListByCatg");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, fromStateCd);
            ps.setString(2, userCatg);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new UserMessageDobj();
                dobj.setFromUser(rs.getString("username"));
                dobj.setMessage(rs.getString("message"));
                dobj.setMessageDate(rs.getString("mesg_dt"));
                listUserMessages.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);

            throw new VahanException("Error in Fetching of User Messages.");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

        return listUserMessages;

    }

    public static void insertReadUserCatgMessage(Long userCd, String stateCd, Integer offCd, String userCatg) throws VahanException {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = "";


        try {

            tmgr = new TransactionManager("insertReadUserCatgMessage");
            sql = "insert into " + TableList.VT_USERMSG_BYCATG_READSTATUS + "(message_id,user_cd,user_catg,msg_read_dt,state_cd,off_cd) \n"
                    + " select message_id,?,to_user_catg,?,to_state_cd,? from " + TableList.VT_USER_MSG + " vum\n"
                    + " where to_state_cd=? and to_user_catg=? and not exists (select message_id from " + TableList.VT_USERMSG_BYCATG_READSTATUS + "\n"
                    + " where user_cd=? and vum.message_id=message_id)";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Util.getEmpCodeLong());
            ps.setTimestamp(2, ServerUtil.getSystemDateInPostgres());
            ps.setInt(3, offCd);
            ps.setString(4, stateCd);
            ps.setString(5, userCatg);
            ps.setLong(6, Util.getEmpCodeLong());
            ps.executeUpdate();
            tmgr.commit();
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("There is a problem in marking messages as read.");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

    }
}
