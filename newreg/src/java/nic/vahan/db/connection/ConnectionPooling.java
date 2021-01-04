package nic.vahan.db.connection;

import nic.rto.vahan.common.ApplicationConfiguration;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import nic.rto.vahan.common.VahanException;
import oracle.sql.DATE;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

public class ConnectionPooling {

    private static DataSource ds;
    private static final String dsString = "java:/comp/env/jdbc/vow4";
    private static DataSource dsReadOnly;
    private static final String dsStringReadOnly = "java:/comp/env/jdbc/vow4dash";
    private static final String dsStringLocal = "java:/comp/env/jdbc/vow4local";
    private static DataSource dsLocalOnly;
    private static final Logger LOGGER = Logger.getLogger(ConnectionPooling.class);

    static {
        try {
            ds = (DataSource) new InitialContext().lookup(dsString);
            dsReadOnly = (DataSource) new InitialContext().lookup(dsStringReadOnly);
            dsLocalOnly = (DataSource) new InitialContext().lookup(dsStringLocal);
        } catch (Exception e) {
            LOGGER.error(e.getStackTrace()[0] + "  " + e.toString());
        }

//        try {
//            String dsString = "java:/comp/env/jdbc/vow4";
//            dsReadOnly = (DataSource) new InitialContext().lookup(dsString);
//        } catch (Exception e) {
//            LOGGER.error(e);
//        }
    }

    public static Connection getDBConnection() throws SQLException {
        Connection con = null;
        try {
            if (!ApplicationConfiguration.allowConn) {
                try {
                    Thread.sleep(15000);
                } catch (Exception ee) {
                }
            }
            SQLException e = null;
            for (int i = 0; i < 10; i++) {
                e = null;
                try {
                    if (ds != null) {
                        con = ds.getConnection();
                    } else {
                        ds = (DataSource) new InitialContext().lookup(dsString);
                        con = ds.getConnection();
                    }
                    if (con != null) {
                        ApplicationConfiguration.allowConn = true;
                        break;
                    }
                } catch (SQLException sqle) {
                    ApplicationConfiguration.allowConn = false;
                    e = sqle;
                    try {
                        Thread.sleep(3000);
                    } catch (Exception ee) {
                    }
                }
            }
            if (con == null) {
                throw e;
            }

        } catch (SQLException sqe) {
            LOGGER.error(sqe.getErrorCode() + " -- " + sqe.toString() + " " + sqe.getStackTrace()[0]);
            throw sqe;
//            08000	connection_exception
//            08003	connection_does_not_exist
//            08006	connection_failure
//            08001	sqlclient_unable_to_establish_sqlconnection
//            08004	sqlserver_rejected_establishment_of_sqlconnection
//            08007	transaction_resolution_unknown
//            08P01	protocol_violation
        } catch (Exception e) {
            ApplicationConfiguration.allowConn = false;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new SQLException("Data base connection not found.");
        }
        return con;
    }

    public static Connection getDBConnectionLocalOnly() throws SQLException {
        Connection con = null;
        try {
            Exception e = null;

            try {
                if (ApplicationConfiguration.deferConnTimeLocalOnly != null && ApplicationConfiguration.deferConnTimeLocalOnly.compareTo(new Date()) == 1) {
                    return getDBConnectionReadOnly();
                }

                if (dsLocalOnly != null) {
                    con = dsLocalOnly.getConnection();
                } else {
                    dsLocalOnly = (DataSource) new InitialContext().lookup(dsStringLocal);
                    con = dsLocalOnly.getConnection();
                }
            } catch (Exception sqle) {
                LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
                e = sqle;
            }

            /**
             * con==null : No connection received from Local
             */
            if (con == null) {
                ApplicationConfiguration.deferConnTimeLocalOnly = DateUtils.addMinutes(new Date(), 30);
                //Get Connection from ReadOnly
                return getDBConnectionReadOnly();
            } else {
                //Connection received from Local Only database
                if (ApplicationConfiguration.deferConnTimeLocalOnly != null) {
                    ApplicationConfiguration.deferConnTimeLocalOnly = null;
                }
            }

            /**
             * If Con is not
             */
            if (con == null) {
                if (e != null) {
                    throw e;
                } else {
                    throw new SQLException("Error in Getting Database Connection");
                }
            }

        } catch (SQLException sqe) {
            LOGGER.error(sqe.toString() + " " + sqe.getStackTrace()[0]);
            throw sqe;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new SQLException("Error in Getting Database Connection");
        }
        return con;

    }

    public static Connection getDBConnectionReadOnly() throws SQLException {
        Connection con = null;
        try {
            Exception e = null;

            try {
                if (ApplicationConfiguration.deferConnTimeReadOnly != null && ApplicationConfiguration.deferConnTimeReadOnly.compareTo(new Date()) == 1) {
                    return getDBConnection();
                }

                if (dsReadOnly != null) {
                    con = dsReadOnly.getConnection();
                } else {
                    dsReadOnly = (DataSource) new InitialContext().lookup(dsStringReadOnly);
                    con = dsReadOnly.getConnection();
                }
            } catch (Exception sqle) {
                LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
                e = sqle;
            }

            /**
             * con==null : No connection received from readOnly
             */
            if (con == null) {
                ApplicationConfiguration.deferConnTimeReadOnly = DateUtils.addMinutes(new Date(), 30);
                //Get Connection from Read/Write
                return getDBConnection();
            } else {
                //Connection received from Read Only database
                if (ApplicationConfiguration.deferConnTimeReadOnly != null) {
                    ApplicationConfiguration.deferConnTimeReadOnly = null;
                }
            }

            /**
             * If Con is not
             */
            if (con == null) {
                if (e != null) {
                    throw e;
                } else {
                    throw new SQLException("Error in Getting Database Connection");
                }
            }

        } catch (SQLException sqe) {
            LOGGER.error(sqe.toString() + " " + sqe.getStackTrace()[0]);
            throw sqe;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new SQLException("Error in Getting Database Connection");
        }
        return con;
    }
}
