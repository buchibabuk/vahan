/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package listeners;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import nic.vahan.server.CommonUtils;
import nic.vahan5.cas.authentication.CASAuthentication;
import org.apache.log4j.Logger;

/**
 *
 * @author Ramesh
 */
@WebListener
public class CASTicketDestroyListener implements HttpSessionListener {

    private static final Logger LOGGER = Logger.getLogger(CASTicketDestroyListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession ses = se.getSession();
        if (ses != null && ses.getAttribute("ses.expire") == "VAHAN4") {
            String casTicket = (String) ses.getAttribute("casTicket");
            if (!CommonUtils.isNullOrBlank(casTicket.trim())) {
                new CASAuthentication().deleteCasTicket(casTicket);
            }
        }
    }
}
