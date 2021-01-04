/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.login.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan5.login.model.CustomLoginBeanModel;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kartikey Singh
 */
@Repository
public class LoginDAOImpl implements LoginDAO {

    private static final Logger LOGGER = Logger.getLogger(LoginDAOImpl.class);
    private final String CUSTOM_LOGIN_TABLE = "tm_custom_login";

    @Override
    public CustomLoginBeanModel insertUserEntry(CustomLoginBeanModel customLoginBean) throws VahanException {
        //LOGGER.info("Before insert LoginDAOImpl");
        TransactionManager tmgr = null;
        try {
            deleteUserEntry(customLoginBean);
            tmgr = new TransactionManager("loginController");
            String statement = "INSERT INTO " + CUSTOM_LOGIN_TABLE + "(user_id, appl_no, rand_no,cas_ticket) VALUES (?,?,?,?)";
            //LOGGER.info("Going to exececute " + statement);
            try (PreparedStatement ps = tmgr.prepareStatement(statement)) {
                ps.setString(1, customLoginBean.getUserId());
                ps.setString(2, customLoginBean.getApplNo());
                ps.setInt(3, customLoginBean.getRandNo());
                ps.setString(4, customLoginBean.getCasTicket());
                ps.executeUpdate();
                tmgr.commit();
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("Insert User Entry Failed");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);

            }
        }
        return customLoginBean;
    }

    @Override
    public int deleteUserEntry(CustomLoginBeanModel customLoginBean) throws VahanException {
        TransactionManager tmgr = null;
        int rowDeleted = 0;
        try {
            tmgr = new TransactionManager("loginController");
            String statement = "DELETE FROM " + CUSTOM_LOGIN_TABLE + " WHERE user_id = ?";
            try (PreparedStatement ps = tmgr.prepareStatement(statement)) {
                ps.setString(1, customLoginBean.getUserId());
                rowDeleted = ps.executeUpdate();
                tmgr.commit();
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("Deletion of UserEntry failed");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                //throw new VahanException(ex.getMessage());
            }
        }
        return rowDeleted;
    }

    @Override
    public CustomLoginBeanModel getUserEntry(CustomLoginBeanModel customLoginBean) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("loginController");
            String statement = "SELECT * FROM " + CUSTOM_LOGIN_TABLE + " WHERE user_id = ?";
            try (PreparedStatement ps = tmgr.prepareStatement(statement)) {
                ps.setString(1, customLoginBean.getUserId());
                RowSet rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    customLoginBean.setApplNo(rs.getString("appl_no") != null ? rs.getString("appl_no") : "ERROR");
                    customLoginBean.setRandNo(rs.getInt("rand_no"));
                    customLoginBean.setCasTicket(rs.getString("cas_ticket"));
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("Can not get UserEntry information");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                // throw new VahanException(ex.getMessage());
            }
        }
        return customLoginBean;
    }
}
