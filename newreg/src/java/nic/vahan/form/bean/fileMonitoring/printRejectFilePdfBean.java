/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.fileMonitoring;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import nic.vahan.form.dobj.fileMonitoring.printRejectFilePdfDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;

/**
 *
 * @author Deepak
 */
@ManagedBean(name = "missingdocsbean")
@ViewScoped
public class printRejectFilePdfBean extends printRejectFilePdfDobj implements Serializable {

    HttpSession session = Util.getSession();

    @PostConstruct
    public void init() {
        Map mapReport;
        mapReport = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        List<printRejectFilePdfDobj> DocsList = (ArrayList<printRejectFilePdfDobj>) mapReport.get("missingdocs");
        setDocsList(DocsList);
        String reason = (String) session.getAttribute("REASON");
        setReason(reason);
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = formatter.format(date);
        setReject_by((ServerUtil.getOfficeName(Util.getUserOffCode(), Util.getUserStateCode())));
        setReject_date(strDate);
    }

    public void backtoApplication() {
        session.removeAttribute("MissingDocs");
        session.removeAttribute("RtoName");
        session.removeAttribute("REASON");
        FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "IncomingFiles");
    }
}
