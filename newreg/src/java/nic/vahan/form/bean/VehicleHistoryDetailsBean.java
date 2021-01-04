/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.impl.VehicleHistoryDetailsImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC075
 */
@ManagedBean(name = "vehicleHistory_bean")
@ViewScoped
public class VehicleHistoryDetailsBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(VehicleHistoryDetailsBean.class);
    FacesMessage message = null;
    private String regnNo = "";
    private String stateCd = "";
    private int offCd;
    private String historyList;
    private List selectionArrayList = new ArrayList();
    private String[][] TArray;
    private boolean isExceedPage = false;
    private int columnCount = 0;
    private String[] columnNameArray;
    private boolean showHistoryPanel = false;
    private String msg = "";

    public void setRegNo(String regNo, int vhClass, String stateCd, int offCd) {
        try {
            boolean isTransport = ServerUtil.isTransport(vhClass, null);
            setRegnNo(regNo);
            setStateCd(stateCd);
            setOffCd(offCd);
            selectionArrayList.clear();
            selectionArrayList.add(new SelectItem("FEES", "History of Paid Fees"));
            selectionArrayList.add(new SelectItem("TAX", "History of Paid Road Tax"));
            selectionArrayList.add(new SelectItem("CA", "History of Change of Address"));
            selectionArrayList.add(new SelectItem("TO", "History of Transfer of Ownership"));
            //selectionArrayList.add(new SelectItem("HPT", "History of Hypothecation Termination"));
            //selectionArrayList.add(new SelectItem("HPA", "History of Hypothecation Additions"));
            selectionArrayList.add(new SelectItem("ALT", "History of Alteration of Vehicle"));
            selectionArrayList.add(new SelectItem("REN", "History of Renewal of Registration"));
            selectionArrayList.add(new SelectItem("INS", "History of Insurance of Vehicle"));
            selectionArrayList.add(new SelectItem("HOV", "History of Hypothecation of Vehicle"));
            selectionArrayList.add(new SelectItem("REASGN", "History of Re-Assignment of Registration Mark"));
            selectionArrayList.add(new SelectItem("DUP", "History of Duplicate Registration Certificate Taken"));
            selectionArrayList.add(new SelectItem("BLACK", "History of Blacklist"));
            selectionArrayList.add(new SelectItem("NOC", "History of NOC"));
            selectionArrayList.add(new SelectItem("NOCCANCEL", "History of NOC Cancelled"));
//            if (isTransport) {
                selectionArrayList.add(new SelectItem("HOP", "History of Permit"));
                selectionArrayList.add(new SelectItem("HOPT", "History of Permit Transaction"));
//            }
            selectionArrayList.add(new SelectItem("HSR", "History of Swapping Retention"));
            selectionArrayList.add(new SelectItem("EXEM", "History of Tax Exemption"));
            selectionArrayList.add(new SelectItem("HOCV", "History of Changed Vehicle Record by Admin"));
            selectionArrayList.add(new SelectItem("HOTC", "History of Tax Clear"));
            selectionArrayList.add(new SelectItem("FIT", "History of Fitness"));
            selectionArrayList.add(new SelectItem("DISPATCHRC", "History of Dispatch RC"));
            selectionArrayList.add(new SelectItem("NONUSE", "History of Vehicle NonUse"));
            selectionArrayList.add(new SelectItem("REFUND", "History of Tax Refund"));
            selectionArrayList.add(new SelectItem("CHALLAN", "History of Vehicle Challan"));
            selectionArrayList.add(new SelectItem("INSTALLMENT", "History of Tax Installment/Breakup"));
            selectionArrayList.add(new SelectItem("RCCANCEL", "History of RC-CANCEL"));
            selectionArrayList.add(new SelectItem("RCSUSPEND", "History of RC-SUSPEND"));
            selectionArrayList.add(new SelectItem("RCSURRENDER", "History of RC-SURENDER"));
            selectionArrayList.add(new SelectItem("38A", "History of 38A"));


            setHistoryList("-1");
            showHistoryPanel = false;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void selectionChangeListener() {
        try {
            if (historyList != null && historyList.equals("-1")) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, " Please Select Vehicle History  ", null);
                FacesContext.getCurrentInstance().addMessage(null, message);
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, " Please Select Vehicle History  ", null);
                FacesContext.getCurrentInstance().addMessage(null, message);
            } else {
                Map dataMap = (Map) VehicleHistoryDetailsImpl.makeMasterTableDO(historyList, regnNo, stateCd, offCd);
                if (dataMap != null) {
                    msg = (String) dataMap.get("message");

                    TArray = (String[][]) dataMap.get("data");
                    columnCount = (int) dataMap.get("colNumber");
                    if (TArray == null) {
                        showHistoryPanel = false;
                        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "  No Record Found  ", null);
                        FacesContext.getCurrentInstance().addMessage(null, message);
                    } else {
                        showHistoryPanel = true;
                        if (TArray.length > 50) {
                            isExceedPage = true;

                        } else {
                            isExceedPage = false;

                        }
                    }
                    columnNameArray = (String[]) dataMap.get("metadata");
                }
            }
        } catch (VahanException vex) {
            message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, vex.getMessage(), vex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception ex) {
            message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }

    }

    /**
     * @return the regnNo
     */
    public String getRegnNo() {
        return regnNo;
    }

    /**
     * @param regnNo the regnNo to set
     */
    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    /**
     * @return the selectionArrayList
     */
    public List getSelectionArrayList() {
        return selectionArrayList;
    }

    /**
     * @param selectionArrayList the selectionArrayList to set
     */
    public void setSelectionArrayList(List selectionArrayList) {
        this.selectionArrayList = selectionArrayList;
    }

    /**
     * @return the TArray
     */
    public String[][] getTArray() {
        return TArray;
    }

    /**
     * @param TArray the TArray to set
     */
    public void setTArray(String[][] TArray) {
        this.TArray = TArray;
    }

    /**
     * @return the isExceedPage
     */
    public boolean isIsExceedPage() {
        return isExceedPage;
    }

    /**
     * @param isExceedPage the isExceedPage to set
     */
    public void setIsExceedPage(boolean isExceedPage) {
        this.isExceedPage = isExceedPage;
    }

    /**
     * @return the columnCount
     */
    public int getColumnCount() {
        return columnCount;
    }

    /**
     * @param columnCount the columnCount to set
     */
    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    /**
     * @return the columnNameArray
     */
    public String[] getColumnNameArray() {
        return columnNameArray;
    }

    /**
     * @param columnNameArray the columnNameArray to set
     */
    public void setColumnNameArray(String[] columnNameArray) {
        this.columnNameArray = columnNameArray;
    }

    /**
     * @return the historyList
     */
    public String getHistoryList() {
        return historyList;
    }

    /**
     * @param historyList the historyList to set
     */
    public void setHistoryList(String historyList) {
        this.historyList = historyList;
    }

    /**
     * @return the showHistoryPanel
     */
    public boolean isShowHistoryPanel() {
        return showHistoryPanel;
    }

    /**
     * @param showHistoryPanel the showHistoryPanel to set
     */
    public void setShowHistoryPanel(boolean showHistoryPanel) {
        this.showHistoryPanel = showHistoryPanel;
    }

    /**
     * @return the stateCd
     */
    public String getStateCd() {
        return stateCd;
    }

    /**
     * @param stateCd the stateCd to set
     */
    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    /**
     * @return the offCd
     */
    public int getOffCd() {
        return offCd;
    }

    /**
     * @param offCd the offCd to set
     */
    public void setOffCd(int offCd) {
        this.offCd = offCd;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
