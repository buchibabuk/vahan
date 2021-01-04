/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.common.jsf.utils;

import java.sql.PreparedStatement;
import java.util.Map;
import javax.faces.application.Application;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostConstructApplicationEvent;
import javax.faces.event.PreDestroyApplicationEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.State;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import org.apache.log4j.Logger;

/**
 *
 * @author Ashok
 */
public class JSFAppListener implements SystemEventListener {

    private static final Logger LOGGER = Logger.getLogger(JSFAppListener.class);
    private Map<String, State> state;
    private Map<String, String> totalRtoRecords;
    private Map<String, String> runningRtoRecords;

    @Override
    public void processEvent(SystemEvent event) throws AbortProcessingException {

        if (event instanceof PostConstructApplicationEvent) {
            System.out.println("VAHAN4 PostConstructApplicationEvent is Called. VAHAN4 Application has Started.");
//            updateBlockedUser();
            state = MasterTableFiller.state;
            totalRtoRecords = MasterTableFiller.totalRtoRecords;
            runningRtoRecords = MasterTableFiller.runningRtoRecords;
        }

        if (event instanceof PreDestroyApplicationEvent) {
            System.out.println("VAHAN4 PreDestroyApplicationEvent is Called. VAHAN4 Application has Shutdown.");
        }

    }

    @Override
    public boolean isListenerForSource(Object source) {
        //only for Application
        return (source instanceof Application);

    }

    public void updateBlockedUser() {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        try {
            tmgr = new TransactionManager("updateBlockedUser");
            sql = "update " + TableList.TM_USER_INFO
                    + " SET status = ?"
                    + " WHERE status = ?";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, "F");
            ps.setString(2, "T");
            ps.executeUpdate();

            tmgr.commit();

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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
}
