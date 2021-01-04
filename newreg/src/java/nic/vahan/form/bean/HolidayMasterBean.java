/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.HolidayMasterDobj;
import nic.vahan.form.impl.HolidayMasterImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "masterHoliday")
@ViewScoped
public class HolidayMasterBean {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(HolidayMasterBean.class);
    private String holiday_reason = "";
    private Date holiday_date;
    private HolidayMasterDobj dobj = new HolidayMasterDobj();
    private List<HolidayMasterDobj> dataList = new ArrayList<>();
    private boolean renderTable = false;
    FacesMessage message;
    SessionVariables sessionVariables = null;

    public HolidayMasterBean() {
        try {
            sessionVariables = new SessionVariables();
            if (sessionVariables == null
                    || sessionVariables.getStateCodeSelected() == null
                    || sessionVariables.getOffCodeSelected() == 0
                    || sessionVariables.getEmpCodeLoggedIn() == null) {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
            fillDataTable();
        } catch (VahanException ex) {
            JSFUtils.showMessage(ex.getMessage());
        }
    }

    public void fillDataTable() throws VahanException {
        try {
            if (!dataList.isEmpty()) {
                dataList.clear();
            }
            dataList = HolidayMasterImpl.gettingListFromvm_holiday_master(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
            if (dataList.size() > 0) {
                for (int i = 0; i < dataList.size(); i++) {
                    if (dataList.get(i).getHoliday_date() != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyy");
                        Date d = sdf.parse(dataList.get(i).getHoliday_date());
                        dataList.get(i).setHoliday_date(sdf1.format(d));
                    }
                    if (dataList.get(i).getOp_date() != null) {
                        String appdt = ServerUtil.getYYYYMMDD_To_DDMMMYYYY(dataList.get(i).getOp_date());
                        dataList.get(i).setOp_date(appdt.substring(0, appdt.length() - 8));
                    }
                }
                renderTable = true;
            } else {
                renderTable = false;
            }
        } catch (VahanException ex) {
            JSFUtils.showMessage(ex.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Details not found.");
        }
    }

    public void saveHolidayData() {
        try {
            if (!CommonUtils.isNullOrBlank(getHoliday_reason()) && getHoliday_date() != null) {
                boolean status = HolidayMasterImpl.saveHoliday_DateData(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), sessionVariables.getEmpCodeLoggedIn(), getHoliday_reason(), DateUtils.parseDate(getHoliday_date()));
                if (status) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Info!", "Data successfully Saved");
                    PrimeFaces.current().dialog().showMessageDynamic(message);
                    reset();

                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert!", "Some error to save data");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                }
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Info!", "Enter All Values");
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
        } catch (VahanException ve) {
            JSFUtils.showMessage(ve.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage("Error in Saving Data");
        }

    }

    public void onDeleteListener(HolidayMasterDobj varDobj) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
            Date holidayDate = sdf.parse(varDobj.getHoliday_date());
            if (holidayDate != null) {
                varDobj.setHoliday_date(sdf1.format(holidayDate));
                boolean delStatus = HolidayMasterImpl.deleteFromvm_holiday_master(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), varDobj);
                if (delStatus) {
                    dataList.remove(varDobj);
                } else {
                    dataList.remove(varDobj);
                }
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFO!", "Data Deleted Successfully");
                FacesContext.getCurrentInstance().addMessage(null, message);
                fillDataTable();
            } else {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
        } catch (VahanException ex) {
            JSFUtils.showMessage(ex.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage("Unable to delete record");
        }

    }

    public void reset() throws VahanException {
        if (dobj != null) {
            dobj = new HolidayMasterDobj();
        }
        setHoliday_date(null);
        setHoliday_reason("");
        renderTable = false;
        dataList.clear();
        fillDataTable();
    }

    public String getHoliday_reason() {
        return holiday_reason;
    }

    public void setHoliday_reason(String holiday_reason) {
        this.holiday_reason = holiday_reason;
    }

    public Date getHoliday_date() {
        return holiday_date;
    }

    public void setHoliday_date(Date holiday_date) {
        this.holiday_date = holiday_date;
    }

    public HolidayMasterDobj getDobj() {
        return dobj;
    }

    public void setDobj(HolidayMasterDobj dobj) {
        this.dobj = dobj;
    }

    public List<HolidayMasterDobj> getDataList() {
        return dataList;
    }

    public void setDataList(List<HolidayMasterDobj> dataList) {
        this.dataList = dataList;
    }

    public boolean isRenderTable() {
        return renderTable;
    }

    public void setRenderTable(boolean renderTable) {
        this.renderTable = renderTable;
    }
}
