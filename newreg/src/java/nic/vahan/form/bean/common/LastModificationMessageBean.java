/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.common;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "msgBean")
@ViewScoped
public class LastModificationMessageBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(LastModificationMessageBean.class);
    private List<String> modificationMessage;
    private String firstMessage;
    private String otherMessage;

    @PostConstruct
    public void init() {

        try {
            modificationMessage = ServerUtil.getUsersLastTransaction(Util.getEmpCode());
            if (modificationMessage.size() > 0) {
                firstMessage = getModificationMessage().get(0);
            }
            if (modificationMessage.size() > 1) {
                otherMessage = "";
                String preF = "<div class='top-space bottom-space left-space right-space'><ol>";
                for (int i = 1; i < getModificationMessage().size(); i++) {
                    otherMessage = otherMessage + "<li>" + getModificationMessage().get(i) + "</li>";
                }

                String postF = "</ol></div>";
                otherMessage = preF + otherMessage + postF;
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    /**
     * @return the modificationMessage
     */
    public List<String> getModificationMessage() {
        return modificationMessage;
    }

    /**
     * @param modificationMessage the modificationMessage to set
     */
    public void setModificationMessage(List<String> modificationMessage) {
        this.modificationMessage = modificationMessage;
    }

    /**
     * @return the firstMessage
     */
    public String getFirstMessage() {
        return firstMessage;
    }

    /**
     * @param firstMessage the firstMessage to set
     */
    public void setFirstMessage(String firstMessage) {
        this.firstMessage = firstMessage;
    }

    /**
     * @return the otherMessage
     */
    public String getOtherMessage() {
        return otherMessage;
    }

    /**
     * @param otherMessage the otherMessage to set
     */
    public void setOtherMessage(String otherMessage) {
        this.otherMessage = otherMessage;
    }
}
