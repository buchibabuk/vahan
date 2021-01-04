/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
import nic.vahan.form.dobj.permit.CounterSignatureDobj;
import nic.vahan.form.dobj.permit.OtherStateVchCounterSignDobj;
import nic.vahan.form.impl.permit.MasterPermitTableImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.CounterSignatureImpl;
import nic.vahan.form.impl.permit.PassengerPermitDetailImpl;
import org.apache.log4j.Logger;

/**
 *
 * @author
 */
@ManagedBean(name = "vchForCSBean")
@ViewScoped
public class VehicleDetailsForCounterSignBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(VehicleDetailsForCounterSignBean.class);
    private List stateList = new ArrayList<>();
    private String regnNo;
    private String pmtNo;
    private Date pmtValidUpto;
    private String stateFrom;
    private int vacancyNo;
    private Date maxDate;
    private Date minDate = DateUtil.parseDate(DateUtil.getCurrentDate());
    private List<OtherStateVchCounterSignDobj> reservationList = null;
    CounterSignatureImpl impl = new CounterSignatureImpl();
    private List purpose_list = new ArrayList();
    private int pur_cd;

    public VehicleDetailsForCounterSignBean() {
        String[][] data = MasterTableFiller.masterTables.TM_STATE.getData();
        for (int i = 0; i < data.length; i++) {
            stateList.add(new SelectItem(data[i][0], data[i][1]));
        }

        data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
        purpose_list.add(new SelectItem("-1", "SELECT"));
        for (int i = 0; i < data.length; i++) {
            if (String.valueOf(TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD).equals(data[i][0])
                    || data[i][0].equals(String.valueOf(TableConstants.VM_PMT_RENEW_COUNTER_SIGNATURE_PUR_CD))) {
                purpose_list.add(new SelectItem(data[i][0], data[i][1]));
            }
        }

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 5);
        cal.add(Calendar.MONTH, 2);
        maxDate = cal.getTime();
        getReservationData();


    }

    public void reset() {
        this.stateFrom = "";
        this.regnNo = "";
        this.pmtNo = "";
        this.pmtValidUpto = null;
    }

    public void getReservationData() {
        reservationList = new ArrayList<>();
        reservationList = impl.getCsReservationData();
    }

    public String save_details() {
        String returnUrl = "";
        String errLabelMsg = "";
        try {
            if (999 != Util.getUserOffCode()) {
                throw new VahanException("This office is not authorised to save data");
            } else if (this.stateFrom.equals("-1")) {
                throw new VahanException("Please select state of vehicle registered");
            } else if (this.pur_cd == -1) {
                throw new VahanException("Please select purpose");
            } else if (impl.checkResrvationQouta(this.stateFrom)) {
                throw new VahanException("Qouta for counter signature is completed");
            } else if (impl.checkRegnNoExistinQouta(this.stateFrom, this.regnNo)) {
                throw new VahanException("Vehicle Number already exist in reservation qouta");
            }
            boolean flag = impl.insertVchDetailsForCS(this.stateFrom, this.regnNo, this.pmtNo, this.pmtValidUpto, this.vacancyNo, this.pur_cd);
            if (flag) {
                errLabelMsg = "Data save sucessfully";
                JSFUtils.showMessage(errLabelMsg);
                reloadData();
            } else {
                errLabelMsg = "Data not saved";
                JSFUtils.showMessage(errLabelMsg);

            }

        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Exception", e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
        return returnUrl;
    }

    public void reloadData() {
        try {
            reservationList.clear();
            setReservationList(impl.getCsReservationData());
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void getRunningVacancyNo() {
        this.vacancyNo = impl.getRunningVacancyNumber(this.stateFrom);
    }

    public List getStateList() {
        return stateList;
    }

    public void setStateList(List stateList) {
        this.stateList = stateList;
    }

    public String getPmtNo() {
        return pmtNo;
    }

    public void setPmtNo(String pmtNo) {
        this.pmtNo = pmtNo;
    }

    public Date getPmtValidUpto() {
        return pmtValidUpto;
    }

    public void setPmtValidUpto(Date pmtValidUpto) {
        this.pmtValidUpto = pmtValidUpto;
    }

    public String getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    public String getStateFrom() {
        return stateFrom;
    }

    public void setStateFrom(String stateFrom) {
        this.stateFrom = stateFrom;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    public List<OtherStateVchCounterSignDobj> getReservationList() {
        return reservationList;
    }

    public void setReservationList(List<OtherStateVchCounterSignDobj> reservationList) {
        this.reservationList = reservationList;
    }

    public int getVacancyNo() {
        return vacancyNo;
    }

    public void setVacancyNo(int vacancyNo) {
        this.vacancyNo = vacancyNo;
    }

    public int getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    public List getPurpose_list() {
        return purpose_list;
    }

    public void setPurpose_list(List purpose_list) {
        this.purpose_list = purpose_list;
    }
}
