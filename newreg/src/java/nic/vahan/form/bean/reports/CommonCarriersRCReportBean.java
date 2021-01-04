/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.reports.CommonCarrierRCDobj;
import nic.vahan.form.impl.CommonCarrierRCImpl;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author NIC
 */
@ManagedBean(name = "CommCarrRCReportBean")
@ViewScoped
public class CommonCarriersRCReportBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(CommonCarriersRCReportBean.class);
    private CommonCarrierRCDobj dobj = null;
    private List<CommonCarrierRCDobj> selectedComCarRClist = new ArrayList<>();

    public CommonCarriersRCReportBean() {
        Map map;
        map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        List<CommonCarrierRCDobj> comCarRCPrintlist = (ArrayList<CommonCarrierRCDobj>) map.get("listPrintCCRC");
        map.remove("listPrintCCRC");
        try {
            for (int i = 0; i < comCarRCPrintlist.size(); i++) {
                CommonCarrierRCDobj dobjComCarRC = CommonCarrierRCImpl.getComCarRCReportDobj(comCarRCPrintlist.get(i).getRegn_no(), comCarRCPrintlist.get(i).getRcpt_no(), comCarRCPrintlist.get(i).getState_cd(), comCarRCPrintlist.get(i).getOff_cd());
                if (dobjComCarRC != null) {
                    selectedComCarRClist.add(dobjComCarRC);
                }
            }

        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }

    }

    /**
     * @return the dobj
     */
    public CommonCarrierRCDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(CommonCarrierRCDobj dobj) {
        this.dobj = dobj;
    }

    /**
     * @return the selectedComCarRClist
     */
    public List<CommonCarrierRCDobj> getSelectedComCarRClist() {
        return selectedComCarRClist;
    }

    /**
     * @param selectedComCarRClist the selectedComCarRClist to set
     */
    public void setSelectedComCarRClist(List<CommonCarrierRCDobj> selectedComCarRClist) {
        this.selectedComCarRClist = selectedComCarRClist;
    }
}
