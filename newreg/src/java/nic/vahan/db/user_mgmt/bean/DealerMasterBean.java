/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.user_mgmt.dobj.DealerMasterDobj;
import nic.vahan.db.user_mgmt.impl.DealerMasterImpl;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.tradecert.TradeCertDetailsDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC103
 */
@ManagedBean
@ViewScoped
public class DealerMasterBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(DealerMasterBean.class);
    private DealerMasterDobj dealerDobj;
    private List districtList = new ArrayList();
    private List stateList = new ArrayList();
    private Date today = DateUtil.parseDate(DateUtil.getCurrentDate());
    private List filterList = null;
    private List<TradeCertDetailsDobj> tradeCertList = new ArrayList<>();
    private boolean renderRegnGenAuth = false;
    private boolean renderTCPanel = false;

    public DealerMasterBean() {
        dealerDobj = new DealerMasterDobj();
    }

    @PostConstruct
    public void init() {
        try {
            dealerDobj.setStateCode(Util.getUserStateCode());
            dealerDobj.setOffCode(Util.getSelectedSeat().getOff_cd());
            fillDataTable();
            stateList = MasterTableFiller.getStateList();
            dealerDobj.setDealerStateCode(Util.getUserStateCode());
            if (!dealerDobj.getStateCode().isEmpty()) {
                dealerStateListener();
            }
            TmConfigurationDobj tmConfig = Util.getTmConfiguration();
            if (tmConfig != null) {
                renderRegnGenAuth = tmConfig.isNum_gen_allowed_dealer();
                renderTCPanel = tmConfig.isConsiderTradeCert();
            }
        } catch (VahanException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    private void fillDataTable() {
        dealerDobj.setUserCatg(Util.getUserCategory());
        dealerDobj.setStateCode(Util.getUserStateCode());
        dealerDobj.setOffCode(Util.getSelectedSeat().getOff_cd());
        DealerMasterImpl dealerImpl = new DealerMasterImpl();
        dealerImpl.fillDt(dealerDobj);
    }

    public void dealerStateListener() {
        districtList.clear();
        String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
        for (int i = 0; i < data.length; i++) {
            if (dealerDobj.getDealerStateCode().equalsIgnoreCase(data[i][2])) {
                getDistrictList().add(new SelectItem(data[i][0], data[i][1]));
            }
        }
    }

    public void varifyTinNo() {
        DealerMasterImpl impl = new DealerMasterImpl();
        String tinNo = dealerDobj.getTin_NO();
        boolean status = false;
        if (!CommonUtils.isNullOrBlank(tinNo)) {
            status = impl.validateUniqueTinNo(tinNo, dealerDobj.getDealerCode());
            if (!status) {
                JSFUtils.setFacesMessage("Valid Tin Number", null, JSFUtils.INFO);
            } else {
                JSFUtils.setFacesMessage("Duplicate Tin Number", null, JSFUtils.INFO);
                dealerDobj.setTin_NO("");
            }
        }
    }

    public void updateListner(String dealerCode) {
        reset();
        DealerMasterImpl impl = new DealerMasterImpl();
        dealerDobj.setDealerCode(dealerCode);
        impl.getDealerDetails(dealerDobj);
        if (dealerDobj.getDealerStateCode() != null && !dealerDobj.getDealerStateCode().isEmpty()) {
            dealerStateListener();
        }
        if (renderTCPanel) {
            setTradeCertList(DealerMasterImpl.getTradeCertificateDetails(dealerCode));
        }
    }

    public void saveDealerDetails() {
        boolean flag = false;
        try {
            if (!dealerDobj.getDealerRegnNo().isEmpty()) {
                DealerMasterImpl impl = new DealerMasterImpl();
                flag = impl.checkInsertOrUpdateDealerDetails(dealerDobj);
            }
            if (flag) {
                reset();
                fillDataTable();
                JSFUtils.setFacesMessage("Dealer Details Saved Successfully", null, JSFUtils.INFO);
            } else {
                JSFUtils.setFacesMessage("Technical Error", null, JSFUtils.ERROR);
            }
        } catch (VahanException e) {
            JSFUtils.setFacesMessage(e.getMessage(), null, JSFUtils.INFO);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            JSFUtils.setFacesMessage(TableConstants.SomthingWentWrong, null, JSFUtils.ERROR);
        }
    }

    public void reset() {
        dealerDobj.setDealerName("");
        dealerDobj.setDealerRegnNo("");
        dealerDobj.setDealerAdd1("");
        dealerDobj.setDealerAdd2("");
        dealerDobj.setDealerStateCode("");
        dealerDobj.setDealerDistrict(0);
        dealerDobj.setDealerPincode(null);
        dealerDobj.setDealerValidUpto(null);
        dealerDobj.setTin_NO("");
        dealerDobj.setDealerCode("");
        dealerDobj.setRegistrationMarkAuth(false);
        setTradeCertList(null);
    }

    public DealerMasterDobj getDealerDobj() {
        return dealerDobj;
    }

    public void setDealerDobj(DealerMasterDobj dealerDobj) {
        this.dealerDobj = dealerDobj;
    }

    /**
     * @return the districtList
     */
    public List getDistrictList() {
        return districtList;
    }

    /**
     * @param districtList the districtList to set
     */
    public void setDistrictList(List districtList) {
        this.districtList = districtList;
    }

    /**
     * @return the stateList
     */
    public List getStateList() {
        return stateList;
    }

    /**
     * @param stateList the stateList to set
     */
    public void setStateList(List stateList) {
        this.stateList = stateList;
    }

    /**
     * @return the today
     */
    public Date getToday() {
        return today;
    }

    /**
     * @param today the today to set
     */
    public void setToday(Date today) {
        this.today = today;
    }

    /**
     * @return the filterList
     */
    public List getFilterList() {
        return filterList;
    }

    /**
     * @param filterList the filterList to set
     */
    public void setFilterList(List filterList) {
        this.filterList = filterList;
    }

    /**
     * @return the renderRegnGenAuth
     */
    public boolean isRenderRegnGenAuth() {
        return renderRegnGenAuth;
    }

    /**
     * @param renderRegnGenAuth the renderRegnGenAuth to set
     */
    public void setRenderRegnGenAuth(boolean renderRegnGenAuth) {
        this.renderRegnGenAuth = renderRegnGenAuth;
    }

    /**
     * @return the tradeCertList
     */
    public List<TradeCertDetailsDobj> getTradeCertList() {
        return tradeCertList;
    }

    /**
     * @param tradeCertList the tradeCertList to set
     */
    public void setTradeCertList(List<TradeCertDetailsDobj> tradeCertList) {
        this.tradeCertList = tradeCertList;
    }

    /**
     * @return the renderTCPanel
     */
    public boolean isRenderTCPanel() {
        return renderTCPanel;
    }

    /**
     * @param renderTCPanel the renderTCPanel to set
     */
    public void setRenderTCPanel(boolean renderTCPanel) {
        this.renderTCPanel = renderTCPanel;
    }
}
