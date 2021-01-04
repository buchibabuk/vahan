/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.impl.blockVehNoImpl;
import nic.vahan.form.dobj.BlockVehicleNumberDobj;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Administrator
 */
@ManagedBean
@ViewScoped
public class blockVehicleNoBean implements Serializable {

    private final List listForVehSeries;
    private String selectedValueVehSeries = "";
    private static final Logger LOGGER = Logger.getLogger(blockVehicleNoBean.class);
    private BlockVehicleNumberDobj dobjBlockVehNo = new BlockVehicleNumberDobj();
    private List<BlockVehicleNumberDobj> blockvehlist = new ArrayList();
    private List<BlockVehicleNumberDobj> filteredBlockvehlist;
    private String regno;
    private int indexValue;

    @PostConstruct
    public void init() {
        Exception exception = null;
        try {
//            try {
            blockVehNoImpl.listofBlockedVeh(getBlockvehlist());
//            } catch (VahanException e) {
//                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
//            }
        } catch (VahanException e) {
            exception = e;
        } catch (Exception e) {
            exception = e;
        }
        if (exception != null) {
            LOGGER.error(exception);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void confirmSave() {
        // RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().ajax().update(":confirmMsg");
        PrimeFaces.current().executeScript("PF('save').show()");
    }

    public blockVehicleNoBean() {
        listForVehSeries = new ArrayList();
        fillVehSeries();
    }

    public void fillVehSeries() {
        try {
            blockVehNoImpl.getVehSeries(listForVehSeries);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void saveBlockedVehicleNo() {
        boolean isUpdate = false;
        try {
            if (dobjBlockVehNo.getVehSeries().equals("0")) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select Vehicle Series!!!", "Please select Vehicle Series!!!");
                FacesContext.getCurrentInstance().addMessage(null, message);
            } else if (dobjBlockVehNo.getFromNumber() != null && dobjBlockVehNo.getFromNumber() <= 0) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please enter number from!!!", "Please enter number from!!!");
                FacesContext.getCurrentInstance().addMessage(null, message);
            } else if (dobjBlockVehNo.getToNumber() != null && dobjBlockVehNo.getToNumber() <= 0) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please enter number upto!!!", "Please enter number upto!!!");
                FacesContext.getCurrentInstance().addMessage(null, message);
            } else if (dobjBlockVehNo.getFromNumber() > dobjBlockVehNo.getToNumber()) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "From number can't be greater than To number!!!", "From number can't be greater than To number!!!");
                FacesContext.getCurrentInstance().addMessage(null, message);
            } else {
                isUpdate = blockVehNoImpl.updateBlockVehNo(dobjBlockVehNo);

                if (isUpdate) {
                    getBlockvehlist().clear();
                    blockVehNoImpl.listofBlockedVeh(getBlockvehlist());
                    dobjBlockVehNo.setFromNumber(0);
                    dobjBlockVehNo.setToNumber(0);
                    dobjBlockVehNo.setVehSeries("0");
                    FacesMessage message = null;
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Vehicle Number Blocked Successfully", "Vehicle Number Blocked Successfully");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    // RequestContext ca = RequestContext.getCurrentInstance();
                    PrimeFaces.current().ajax().update(":blockveh");
                    PrimeFaces.current().ajax().update(":regPanel");
                } else {
                    FacesMessage message = null;
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Vehicle Number already blocked!!!", "Vehicle Number already blocked!!!");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void confirmRelaseVehNo() {
        Map map;
        map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        regno = (String) map.get("regnno");
        indexValue = Integer.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("indexValue"));
        // RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().ajax().update(":releaseConfirmationPopup");
        PrimeFaces.current().executeScript("PF('releaseVehicle').show()");
    }

    public void saveReleaseVehNo() {
        boolean isRelease = false;
        try {
            isRelease = blockVehNoImpl.releaseVehicle(regno);
            if (isRelease) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Vehicle Number Released Successfully", "Vehicle Number Released Successfully");
                FacesContext.getCurrentInstance().addMessage(null, message);
                BlockVehicleNumberDobj dobj = blockvehlist.remove(indexValue);
                //RequestContext ca = RequestContext.getCurrentInstance();
                PrimeFaces.current().ajax().update("regPanel");
            } else {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Vehicle Number not released!!!", "Vehicle Number not released!!!");
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void setVehicleSeriesForlistner(AjaxBehaviorEvent event) {
//        selectedValueVehSeries = 
    }

    public List getListForVehSeries() {
        return listForVehSeries;
    }

    public String getSelectedValueVehSeries() {
        return selectedValueVehSeries;
    }

    public void setSelectedValueVehSeries(String selectedValueVehSeries) {
        this.selectedValueVehSeries = selectedValueVehSeries;
    }

    public BlockVehicleNumberDobj getDobjBlockVehNo() {
        return dobjBlockVehNo;
    }

    public void setDobjBlockVehNo(BlockVehicleNumberDobj dobjBlockVehNo) {
        this.dobjBlockVehNo = dobjBlockVehNo;
    }

    public List<BlockVehicleNumberDobj> getBlockvehlist() {
        return blockvehlist;
    }

    public void setBlockvehlist(List<BlockVehicleNumberDobj> blockvehlist) {
        this.blockvehlist = blockvehlist;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public int getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(int indexValue) {
        this.indexValue = indexValue;
    }

    /**
     * @return the filteredBlockvehlist
     */
    public List<BlockVehicleNumberDobj> getFilteredBlockvehlist() {
        return filteredBlockvehlist;
    }

    /**
     * @param filteredBlockvehlist the filteredBlockvehlist to set
     */
    public void setFilteredBlockvehlist(List<BlockVehicleNumberDobj> filteredBlockvehlist) {
        this.filteredBlockvehlist = filteredBlockvehlist;
    }
}
