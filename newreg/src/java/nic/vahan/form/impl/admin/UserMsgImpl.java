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
import nic.vahan.db.connection.TransactionManagerReadOnly;
import org.apache.log4j.Logger;
import nic.vahan.form.dobj.UserInfoForMsgDobj;
import nic.vahan.form.dobj.UserMessageDobj;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;

/**
 *
 * @author acer
 */
public class UserMsgImpl {
//

    private static Logger LOGGER = Logger.getLogger(UserMsgImpl.class);

    public static List<UserInfoForMsgDobj> getUsersListForMsg(Long empCd) throws VahanException {
        List<UserInfoForMsgDobj> listUsers = new ArrayList<>();
        UserInfoForMsgDobj dobj = null;
        String sql = "Select tui.user_cd,tui.user_name,tui.email_id,tui.phone_off,tui.state_cd,tui.off_cd::varchar,ts.descr, \n"
                + "(select count(1) from " + TableList.VT_USER_MSG + " where to_user_cd=? \n"
                + "and ((from_user_cd = tui.user_cd) or (from_state_cd=tui.state_cd)) and read_flag='f') as unreadmsgs, \n"
                + "(select count(1) from " + TableList.VT_USER_MSG + " where to_user_cd=? \n"
                + "and ((from_user_cd = tui.user_cd) or (from_state_cd=tui.state_cd)) and close_request='f') as unclosedrequests \n"
                + "from " + TableList.TM_USER_INFO + " tui, " + TableList.TM_STATE + " ts "
                + "where tui.state_cd=ts.state_code and tui.user_catg='S' order by tui.state_cd";

        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getUsersListForMsg");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setLong(1, empCd);
            ps.setLong(2, empCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new UserInfoForMsgDobj();
                dobj.setUserName(rs.getString("user_name"));
                dobj.setUserCode(rs.getLong("user_cd"));
                dobj.setPhoneOff(rs.getString("phone_off"));
                dobj.setEmailId(rs.getString("email_id"));
                dobj.setStateCd(rs.getString("state_cd"));
                dobj.setOffCd(rs.getString("off_cd"));
                dobj.setStateName(rs.getString("descr"));
                dobj.setUnreadMessages(rs.getLong("unreadmsgs"));
                dobj.setUnclosedRequests(rs.getLong("unclosedrequests"));
                listUsers.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Fetching of Users List");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

        if (listUsers.isEmpty()) {
            return null;
        }

        return listUsers;

    }

    public static List<UserInfoForMsgDobj> getRtoUsersListForMsg(Long empCd, String state_cd, boolean stateCdRtoCdJoined, boolean sortByOffCd, boolean sortByOffName) throws VahanException {
        List<UserInfoForMsgDobj> listUsers = new ArrayList<>();
        UserInfoForMsgDobj dobj = null;
        String sqlOffCd;
        String sqlSortOffNameCode;
        if (stateCdRtoCdJoined == true) {
            sqlOffCd = " tui.state_cd||lpad(tui.off_cd::varchar,3,'0') ";
        } else {
            sqlOffCd = " lpad(tui.off_cd::varchar,3,'0') ";
        }

        if (sortByOffName == true) {
            sqlSortOffNameCode = " order by off_name ";
        } else {
            sqlSortOffNameCode = " order by off_cd ";
        }

        String sql = "Select tui.user_cd,tui.user_name,tui.email_id,tui.phone_off,tui.state_cd,t_o.off_name," + sqlOffCd + " as off_cd,ts.descr, \n"
                + "(select count(1) from " + TableList.VT_USER_MSG + " where to_user_cd=? \n"
                + "and from_user_cd = tui.user_cd and read_flag='f') as unreadmsgs,\n"
                + "(select count(1) from " + TableList.VT_USER_MSG + " where to_user_cd=? \n"
                + "and from_user_cd = tui.user_cd and close_request='f') as unclosedrequests \n"
                + "from " + TableList.TM_USER_INFO + " tui, " + TableList.TM_STATE + " ts," + TableList.TM_OFFICE + " t_o "
                + "where tui.state_cd=ts.state_code and tui.user_catg='A' and tui.state_cd=? and tui.off_cd=t_o.off_cd \n"
                + " and t_o.state_cd=tui.state_cd " + sqlSortOffNameCode;
        TransactionManagerReadOnly tmgr = null;


        try {
            tmgr = new TransactionManagerReadOnly("getUsersListForMsg");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setLong(1, empCd);
            ps.setLong(2, empCd);
            ps.setString(3, state_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new UserInfoForMsgDobj();
                dobj.setUserName(rs.getString("user_name"));
                dobj.setUserCode(rs.getLong("user_cd"));
                dobj.setPhoneOff(rs.getString("phone_off"));
                dobj.setEmailId(rs.getString("email_id"));
                dobj.setStateCd(rs.getString("state_cd"));
                dobj.setOffCd(rs.getString("off_cd"));
                dobj.setStateName(rs.getString("descr"));
                dobj.setOffName(rs.getString("off_name"));
                dobj.setUnreadMessages(rs.getLong("unreadmsgs"));
                dobj.setUnclosedRequests(rs.getLong("unclosedrequests"));
                listUsers.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Fetching of Users List");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

        if (listUsers.isEmpty()) {
            return null;
        }
        return listUsers;
    }

    public static List<UserMessageDobj> getUnreadUserMessages(Long selfUserCd) throws VahanException {
        List<UserMessageDobj> listUserMessages = new ArrayList<>();
        UserMessageDobj dobj = null;
        String sql = "Select (select case when user_cd=? then 'You' else user_name end \n"
                + "from " + TableList.TM_USER_INFO + " where user_cd=vum.from_user_cd) as username,\n"
                + "coalesce((select case when copy_to_user <> 0 then '(Copy to ' || user_name|| ' of office '||\n"
                + "(case when tui.off_cd =0 then ' State Admin Office ' else (select off_name from tm_office t_off \n"
                + "where tui.state_cd=t_off.state_cd and tui.off_cd=t_off.off_cd) end) || ' and state ' \n"
                + "|| descr || ')' else ' ' end from " + TableList.TM_USER_INFO + " tui,\n"
                + TableList.TM_STATE + " ts where tui.user_cd=copy_to_user and \n"
                + "tui.state_cd=ts.state_code),' ') as copytouser,\n"
                + " vum.from_state_cd,vum.from_off_cd,vum.from_user_cd,vum.message,to_char(vum.msg_dt,'dd-mm-yyyy hh24:mi:ss') as mesg_dt from \n"
                + TableList.VT_USER_MSG + " vum  where vum.to_user_cd=? and vum.read_flag='f' order by msg_dt";


        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getUserMessagesList");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setLong(1, selfUserCd);
            ps.setLong(2, selfUserCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new UserMessageDobj();
                dobj.setFromUser(rs.getString("username"));
                dobj.setFromUserCd(rs.getLong("from_user_cd"));
                dobj.setFromStateCd(rs.getString("from_state_cd"));
                dobj.setFromOffCd(rs.getInt("from_off_cd"));
                dobj.setMessage(rs.getString("message"));
                dobj.setMessageDate(rs.getString("mesg_dt"));
                dobj.setCopyToUser(rs.getString("copytouser"));
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

    public static List<UserMessageDobj> getUserMessagesList(Long otherUserCd, String otherStateCd, String selfUserCd, String selfStateCd, boolean showUnclosedMsgsOnly) throws VahanException {
        List<UserMessageDobj> listUserMessages = new ArrayList<>();
        UserMessageDobj dobj = null;
        String strQry = "";
        if (showUnclosedMsgsOnly) {
            strQry = " and close_request='false'";
        }

        String sql = "Select (select case when user_cd::character varying=? then 'You' else user_name end \n"
                + "from " + TableList.TM_USER_INFO + " where user_cd=vum.from_user_cd) as username,\n"
                + "(select case when copy_to_user <> 0 then '(Copy to ' || user_name|| ' of office '||\n"
                + "(case when tui.off_cd =0 then ' State Admin Office ' else (select off_name from tm_office t_off \n"
                + "where tui.state_cd=t_off.state_cd and tui.off_cd=t_off.off_cd) end) || ' and state ' \n"
                + "|| descr || ')' else '' end from " + TableList.TM_USER_INFO + " tui,\n"
                + TableList.TM_STATE + " ts where tui.user_cd=copy_to_user and \n"
                + "tui.state_cd=ts.state_code) as copytouser,\n"
                + "vum.message,to_char(vum.msg_dt,'dd-mm-yyyy hh24:mi:ss') as mesg_dt,vum.doc_ref_no,vum.upload_state_cd,vum.upload_off_cd,vum.message_id,vum.close_request\n"
                + " from " + TableList.VT_USER_MSG + " vum"
                + " where ((vum.from_user_cd=? and vum.to_user_cd::character varying=?) or "
                + " (vum.from_user_cd::character varying=? and vum.to_user_cd=?)) " + strQry + " order by msg_dt";
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getUserMessagesList");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, selfUserCd);
            ps.setLong(2, otherUserCd);
            ps.setString(3, selfUserCd);
            ps.setString(4, selfUserCd);
            ps.setLong(5, otherUserCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new UserMessageDobj();
                dobj.setFromUser(rs.getString("username"));
                dobj.setMessage(rs.getString("message"));
                dobj.setMessageDate(rs.getString("mesg_dt"));
                dobj.setCopyToUser(rs.getString("copytouser"));
                dobj.setDocRefNo(rs.getString("doc_ref_no"));
                dobj.setUploadDocStateCode(rs.getString("upload_state_cd"));
                dobj.setUploadDocOffCode(rs.getInt("upload_off_cd"));
                dobj.setMessageId(rs.getString("message_id"));
                dobj.setCloseRequest(rs.getBoolean("close_request"));
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

    public static long getNoOfUnreadMessages(Long empCd) throws VahanException {
        long unreadMessages = 0;
        String sql = "Select count(1) as unreadmsgs from " + TableList.VT_USER_MSG + " where \n"
                + "to_user_cd=? and read_flag='false'";

        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("getNoOfUnreadMessages");
            PreparedStatement ps = tmgr.prepareStatement(sql);

            ps.setLong(1, empCd);

            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                unreadMessages = rs.getLong("unreadmsgs");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Fetching of Number of Unread Messages.");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

        return unreadMessages;

    }

    public static long getNoOfUserCatgMessages(String userCatg, String stateCd) throws VahanException {
        long userCatgMessages = 0;

        String sql = "Select count(1) as msgs from " + TableList.VT_USER_MSG + " where \n"
                + "to_user_catg=? and to_state_cd=?";



        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("getNoOfUserCatgMessages");
            PreparedStatement ps = tmgr.prepareStatement(sql);

            ps.setString(1, userCatg);
            ps.setString(2, stateCd);


            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                userCatgMessages = rs.getLong("msgs");
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Fetching of State Administrator announcements.");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

        return userCatgMessages;

    }

    public static long getNoOfReadUserCatgMessages(String userCatg, String stateCd, Long userCd) throws VahanException {
        long readUserCatgMessages = 0;

        String sql = "Select count(1) as readmsgs from " + TableList.VT_USERMSG_BYCATG_READSTATUS + " where \n"
                + "user_catg=? and state_cd=? and user_cd=?";

        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("getNoOfReadUserCatgMessages");
            PreparedStatement ps = tmgr.prepareStatement(sql);

            ps.setString(1, userCatg);
            ps.setString(2, stateCd);
            ps.setLong(3, userCd);

            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                readUserCatgMessages = rs.getLong("readmsgs");
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Fetching of read State Administrator announcements.");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

        return readUserCatgMessages;

    }

    public static String sendMsg(String frmUserCd, String frmStateCd, int frmOffCd, String msg, Long toUsrCd, String toStateCd, int toOffCd, String docReferenceNo, String uploadStateCd, int uploadOffCd, boolean sendCopyStateAdmin) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String toStateAdmUsrCdOffCd;
        String[] arrToStateAdmUsrCdOffCd;
        Long tempUserCd;
        String messageId = "";

        try {
            tmgr = new TransactionManager("sendMsg");
            String sql = "INSERT INTO " + TableList.VT_USER_MSG + "(from_user_cd,from_state_cd,from_off_cd,to_user_cd,\n"
                    + "copy_to_user,to_state_cd,to_off_cd,message,msg_dt,read_flag,upload_state_cd,upload_off_cd,doc_ref_no)\n"
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            ps = tmgr.prepareStatement(sql);
            if (sendCopyStateAdmin) {
                if (ServerUtil.getUserCategory(Long.parseLong(frmUserCd)).equals("A")) {
                    toStateAdmUsrCdOffCd = getUsrCdAndOffByStateCatg(frmStateCd, "S");
                    if (!CommonUtils.isNullOrBlank(toStateAdmUsrCdOffCd)) {
                        arrToStateAdmUsrCdOffCd = toStateAdmUsrCdOffCd.split(",");
                        tempUserCd = Long.valueOf(arrToStateAdmUsrCdOffCd[0]);
                        if (arrToStateAdmUsrCdOffCd != null) {
                            if (!tempUserCd.equals(toUsrCd)) {
                                ps.setLong(1, Long.parseLong(frmUserCd));
                                ps.setString(2, frmStateCd);
                                ps.setInt(3, frmOffCd);
                                ps.setLong(4, Long.valueOf(arrToStateAdmUsrCdOffCd[0]));
                                ps.setLong(5, toUsrCd);
                                ps.setString(6, frmStateCd);
                                ps.setLong(7, Long.valueOf(arrToStateAdmUsrCdOffCd[1]));
                                ps.setString(8, msg);
                                ps.setTimestamp(9, ServerUtil.getSystemDateInPostgres());
                                ps.setBoolean(10, false);
                                ps.setString(11, uploadStateCd);
                                ps.setInt(12, uploadOffCd);
                                ps.setString(13, docReferenceNo);
                                ps.addBatch();
                            }
                        }
                    }
                }
            }


            ps.setLong(1, Long.parseLong(frmUserCd));
            ps.setString(2, frmStateCd);
            ps.setInt(3, frmOffCd);
            ps.setLong(4, toUsrCd);
            ps.setLong(5, 0);
            ps.setString(6, toStateCd);
            ps.setInt(7, toOffCd);
            ps.setString(8, msg);
            ps.setTimestamp(9, ServerUtil.getSystemDateInPostgres());
            ps.setBoolean(10, false);
            ps.setString(11, uploadStateCd);
            ps.setInt(12, uploadOffCd);
            ps.setString(13, docReferenceNo);
            ps.addBatch();
            ps.executeBatch();
            tmgr.commit();
            sql = "select message_id from " + TableList.VT_USER_MSG + " where from_user_cd=? and to_user_cd=? order by msg_dt desc limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(frmUserCd));
            ps.setLong(2, toUsrCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                messageId = rs.getString("message_id");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
        return messageId;

    }

    public static void markUserMessageAsRead(Long otherUserCd, String selfUserCd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManager("markUserMessageAsRead");
            String sql = "update " + TableList.VT_USER_MSG + " vum set read_flag='true' \n"
                    + "where vum.from_user_cd=? and vum.to_user_cd::character varying=? \n"
                    + "and read_flag <> 'true'";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, otherUserCd);
            ps.setString(2, selfUserCd);

            ps.execute();
            tmgr.commit();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in marking messages as read.");
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

    public static void closeRequest(String messageId) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManager("markClosedMessages");

            String sql = "update " + TableList.VT_USER_MSG + " set close_request=not close_request where message_id=?\n";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(messageId));
            ps.execute();
            tmgr.commit();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in closing messages.");
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

    public static String getUsrCdAndOffByStateCatg(String stateCd, String catg) throws SQLException, Exception {
        String whereiam = "Get State Admin User Code";
        String stateAdminUserCdAndOff = null;
        PreparedStatement psmt = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly(whereiam);
            String strSQL = "select user_cd,off_cd from tm_user_info where state_cd=? and user_catg=?";
            psmt = tmgr.prepareStatement(strSQL);
            psmt.setString(1, stateCd);
            psmt.setString(2, catg);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                stateAdminUserCdAndOff = String.valueOf(rs.getLong("user_cd")) + "," + String.valueOf(rs.getLong("off_cd"));
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in sending messages.");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in sending messages.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return stateAdminUserCdAndOff;
    }

    public static String getOffNameByOffCode(String stateCd, int offCd) throws VahanException {
        String str = "";
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;

        String sql = "Select off_name from " + TableList.TM_OFFICE + " where off_cd=? and state_cd=?";
        try {
            tmgr = new TransactionManagerReadOnly("getOffNameByOffCode");
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, offCd);
            ps.setString(2, stateCd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                str = rs.getString("off_name");
            } else {
                str = "Transport Department";
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in uploading file.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return str;
    }

    public static boolean chkGeneratedDocRefNo(String docRefNo) throws VahanException {
        boolean result = false;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;

        String sql = "Select doc_ref_no from " + TableList.VT_USER_MSG + " where doc_ref_no=?";
        try {
            tmgr = new TransactionManagerReadOnly("chkGeneratedDocRefNo");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, docRefNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                result = true;
            } else {
                result = false;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in uploading file.");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return result;
    }

    public static String getDocRefNo() throws VahanException {
        String documentRefNo = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String strSQL = "SELECT nextval('usermessaging.vt_user_msg_message_id_seq')::varchar as doc_ref_no";
        try {

            tmgr = new TransactionManager("getDocRefNo");
            ps = tmgr.prepareStatement(strSQL);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                documentRefNo = rs.getString("doc_ref_no");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in uploading file.");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return documentRefNo;
    }
}
