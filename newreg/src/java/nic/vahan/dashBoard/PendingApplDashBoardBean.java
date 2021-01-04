/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.dashBoard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Dhananjay
 */
@ManagedBean(name = "applDash")
@ViewScoped
public class PendingApplDashBoardBean implements Serializable {

    private static final org.apache.log4j.Logger LOGGER = Logger.getLogger(PendingApplDashBoardBean.class);
    private PendingApplDashBoardDobj dobj = new PendingApplDashBoardDobj();
    private PendingApplDashBoardImpl impl = new PendingApplDashBoardImpl();
    private String offName;
    private int actionWiseTotal = 0;
    private int vhClassWiseTotal = 0;
    private int permitWiseTotal = 0;
    private TreeNode treeNodeActionWise = new DefaultTreeNode(new PendingApplTreeNodeDobj("Action", 0, 0, 0, "-", "-", 0), null);
    private TreeNode treeNodeVhClassWise = new DefaultTreeNode(new PendingApplTreeNodeDobj("Action", 0, "-", 0, 0, "-", 0), null);
    private TreeNode treeNodePermitWise = new DefaultTreeNode(new PendingApplTreeNodeDobj("Action", 0, 0, 0, "-"), null);
    private boolean renderApplPannel = false;
    private boolean prevApplButton = false;
    private boolean nxtApplButton = false;
    private List<DashboardDetails> allseatWork = null;
    private List<DashboardDetails> filteredSeat = null;
    private int uptoRecord = 0;
    private PendingApplTreeNodeDobj forNextPrevRecord = null;
    private Map<Object, Object> allotedOffCodeList;
    private String user_catg;
    private boolean disableOfficeSelection = false;
    private boolean renderTreePannel = false;
    private Date vow4Date = null;
    private List officeList = new ArrayList();
    private String state_cd;
    private String headerDescr;
    private int office_cd;
    private boolean actionwiseRender, regnTypeRender, permitWiseRender = false;

    public PendingApplDashBoardBean() {
        try {
            state_cd = Util.getUserStateCode();
            user_catg = Util.getUserCategory();
            officeList.clear();
            allseatWork = new ArrayList<DashboardDetails>();
            allotedOffCodeList = ServerUtil.getOfficeListOfState(state_cd);
            for (Map.Entry<Object, Object> entry : allotedOffCodeList.entrySet()) {
                int off_cd = (int) entry.getKey();
                String off_name = (String) entry.getValue();
                officeList.add(new SelectItem(off_cd, off_name));
            }

            if (!user_catg.equals(TableConstants.USER_CATG_STATE_ADMIN)) {
                renderTreePannel = true;
                disableOfficeSelection = true;
                offName = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("OffName");
                office_cd = Util.getUserOffCode();
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(PendingApplDashBoardBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void actionWiseLoadData() {
        try {
            dobj = impl.getActionWiseData(state_cd, office_cd);
            actionWiseTotal = dobj.getTreeNodeTotalActionWise();
            treeNodeActionWise = dobj.getTreeNodeActionWise();
        } catch (VahanException vme) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vme.getMessage(), vme.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void vhclassWiseLoadData() {
        try {
            dobj = impl.getVhclassWiseData(state_cd, office_cd);
            vhClassWiseTotal = dobj.getTreeNodeTotalVhClassWise();
            treeNodeVhClassWise = dobj.getTreeNodeVhClassWise();
        } catch (VahanException vme) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vme.getMessage(), vme.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void permitWiseLoadData() {
        try {
            dobj = impl.getPermitWiseData(state_cd, office_cd);
            permitWiseTotal = dobj.getTreeNodeTotalPermitWise();
            treeNodePermitWise = dobj.getTreeNodePermitWise();
        } catch (VahanException vme) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vme.getMessage(), vme.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void displaySelectedSingle(PendingApplTreeNodeDobj dobj) {
        int allotedOfficeCode = office_cd;
        allseatWork = new ArrayList<DashboardDetails>();
        String errorMsg = "";
        nxtApplButton = false;
        try {
            if (dobj != null) {
                allseatWork.clear();
                renderApplPannel = true;
                uptoRecord = 0;
                allseatWork = impl.dashBoardSeatWorkList(allotedOfficeCode, dobj, 200, 0);
                errorMsg = "No pending work against";
                if (allseatWork.size() > 0) {
                    if (dobj.getViewType().equalsIgnoreCase("Action")) {
                        headerDescr = allseatWork.get(0).getFeeType_descr();
                        permitWiseRender = false;
                        actionwiseRender = true;
                        regnTypeRender = false;

                    } else if (dobj.getViewType().equalsIgnoreCase("RegnType")) {
                        headerDescr = allseatWork.get(0).getRegnType_descr();
                        permitWiseRender = false;
                        actionwiseRender = false;
                        regnTypeRender = true;

                    } else if (dobj.getViewType().equalsIgnoreCase("Permit")) {
                        headerDescr = allseatWork.get(0).getPmtType_descr();
                        permitWiseRender = true;
                        actionwiseRender = false;
                        regnTypeRender = false;
                    }
                    PrimeFaces.current().ajax().update("actionMsg");
                    forNextPrevRecord = dobj;
                    if (dobj.getTotal() > 200) {
                        nxtApplButton = true;
                        uptoRecord = 200;
                    } else {
                        nxtApplButton = false;
                        prevApplButton = false;
                    }
                } else {
                    PrimeFaces.current().ajax().update("actionMsg");
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", errorMsg));
                }

            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            }
        } catch (VahanException vme) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vme.getMessage(), vme.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void getPendingWorkDateWiseNext() {
        int allotedOfficeCode = office_cd;
        try {

            int temUptoRecord = uptoRecord;
            if (!CommonUtils.isNullOrBlank(forNextPrevRecord.getViewType())) {

                prevApplButton = true;
                allseatWork = impl.dashBoardSeatWorkList(allotedOfficeCode, forNextPrevRecord, 200, temUptoRecord);
                if (forNextPrevRecord.getTotal() > temUptoRecord + 200) {
                    uptoRecord = uptoRecord + 200;
                } else {
                    uptoRecord = uptoRecord + (forNextPrevRecord.getTotal() - temUptoRecord);
                    nxtApplButton = false;
                }
            }
        } catch (VahanException vme) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vme.getMessage(), vme.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void getPendingWorkDateWisePrev() {
        int allotedOfficeCode = office_cd;
        try {
            if (uptoRecord > 0) {
                int temUptoRecord = 0;
                int limit = 0;
                if (uptoRecord - 200 > 0) {
                    temUptoRecord = uptoRecord - 200;
                    limit = 200;
                } else {
                    temUptoRecord = 0;
                    limit = uptoRecord;
                }

                if (!CommonUtils.isNullOrBlank(forNextPrevRecord.getViewType())) {
                    prevApplButton = true;
                    allseatWork = impl.dashBoardSeatWorkList(allotedOfficeCode, forNextPrevRecord, limit, temUptoRecord);
                    if (uptoRecord - 200 > 0) {
                        uptoRecord = uptoRecord - 200;
                        nxtApplButton = true;
                    } else {
                        uptoRecord = 0;
                        prevApplButton = false;
                        nxtApplButton = true;
                    }
                } else {
                    prevApplButton = false;
                    JSFUtils.showMessagesInDialog("Information", "No Record !!", FacesMessage.SEVERITY_ERROR);
                }
            }

        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, vex.getMessage(), vex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void updateOffWiseDasBoard() {
        try {
            vow4Date = ServerUtil.getVahan4StartDate(Util.getUserStateCode(), office_cd);
            if (vow4Date == null && office_cd != 0) {
                JSFUtils.showMessagesInDialog("Alert!", "Vahan 4.0 is not implemented in this RTO.", FacesMessage.SEVERITY_INFO);
                return;
            } else {
                renderTreePannel = true;
                dobj = null;
                renderApplPannel = false;
                if (allseatWork.size() > 0) {
                    allseatWork.clear();
                }
                for (Map.Entry<Object, Object> entry : allotedOffCodeList.entrySet()) {
                    int off_cd = (int) entry.getKey();
                    if (off_cd == office_cd) {
                        offName = (String) entry.getValue();
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }

    }

    public PendingApplDashBoardDobj getDobj() {
        return dobj;
    }

    public void setDobj(PendingApplDashBoardDobj dobj) {
        this.dobj = dobj;
    }

    public PendingApplDashBoardImpl getImpl() {
        return impl;
    }

    public void setImpl(PendingApplDashBoardImpl impl) {
        this.impl = impl;
    }

    public String getOffName() {
        return offName;
    }

    public void setOffName(String offName) {
        this.offName = offName;
    }

    public int getActionWiseTotal() {
        return actionWiseTotal;
    }

    public void setActionWiseTotal(int actionWiseTotal) {
        this.actionWiseTotal = actionWiseTotal;
    }

    public int getVhClassWiseTotal() {
        return vhClassWiseTotal;
    }

    public void setVhClassWiseTotal(int vhClassWiseTotal) {
        this.vhClassWiseTotal = vhClassWiseTotal;
    }

    public int getPermitWiseTotal() {
        return permitWiseTotal;
    }

    public void setPermitWiseTotal(int permitWiseTotal) {
        this.permitWiseTotal = permitWiseTotal;
    }

    public TreeNode getTreeNodeActionWise() {
        return treeNodeActionWise;
    }

    public void setTreeNodeActionWise(TreeNode treeNodeActionWise) {
        this.treeNodeActionWise = treeNodeActionWise;
    }

    public TreeNode getTreeNodeVhClassWise() {
        return treeNodeVhClassWise;
    }

    public void setTreeNodeVhClassWise(TreeNode treeNodeVhClassWise) {
        this.treeNodeVhClassWise = treeNodeVhClassWise;
    }

    public TreeNode getTreeNodePermitWise() {
        return treeNodePermitWise;
    }

    public void setTreeNodePermitWise(TreeNode treeNodePermitWise) {
        this.treeNodePermitWise = treeNodePermitWise;
    }

    /**
     * @return the renderApplPannel
     */
    public boolean isRenderApplPannel() {
        return renderApplPannel;
    }

    /**
     * @param renderApplPannel the renderApplPannel to set
     */
    public void setRenderApplPannel(boolean renderApplPannel) {
        this.renderApplPannel = renderApplPannel;
    }

    /**
     * @return the prevApplButton
     */
    public boolean isPrevApplButton() {
        return prevApplButton;
    }

    /**
     * @param prevApplButton the prevApplButton to set
     */
    public void setPrevApplButton(boolean prevApplButton) {
        this.prevApplButton = prevApplButton;
    }

    /**
     * @return the nxtApplButton
     */
    public boolean isNxtApplButton() {
        return nxtApplButton;
    }

    /**
     * @param nxtApplButton the nxtApplButton to set
     */
    public void setNxtApplButton(boolean nxtApplButton) {
        this.nxtApplButton = nxtApplButton;
    }

    public int getUptoRecord() {
        return uptoRecord;
    }

    public void setUptoRecord(int uptoRecord) {
        this.uptoRecord = uptoRecord;
    }

    public PendingApplTreeNodeDobj getForNextPrevRecord() {
        return forNextPrevRecord;
    }

    public void setForNextPrevRecord(PendingApplTreeNodeDobj forNextPrevRecord) {
        this.forNextPrevRecord = forNextPrevRecord;
    }

    public Map<Object, Object> getAllotedOffCodeList() {
        return allotedOffCodeList;
    }

    public void setAllotedOffCodeList(Map<Object, Object> allotedOffCodeList) {
        this.allotedOffCodeList = allotedOffCodeList;
    }

    public String getUser_catg() {
        return user_catg;
    }

    public void setUser_catg(String user_catg) {
        this.user_catg = user_catg;
    }

    public boolean isRenderTreePannel() {
        return renderTreePannel;
    }

    public void setRenderTreePannel(boolean renderTreePannel) {
        this.renderTreePannel = renderTreePannel;
    }

    public Date getVow4Date() {
        return vow4Date;
    }

    public void setVow4Date(Date vow4Date) {
        this.vow4Date = vow4Date;
    }

    public List getOfficeList() {
        return officeList;
    }

    public void setOfficeList(List officeList) {
        this.officeList = officeList;
    }

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public int getOffice_cd() {
        return office_cd;
    }

    public void setOffice_cd(int office_cd) {
        this.office_cd = office_cd;
    }

    public List<DashboardDetails> getAllseatWork() {
        return allseatWork;
    }

    public void setAllseatWork(List<DashboardDetails> allseatWork) {
        this.allseatWork = allseatWork;
    }

    public List<DashboardDetails> getFilteredSeat() {
        return filteredSeat;
    }

    public void setFilteredSeat(List<DashboardDetails> filteredSeat) {
        this.filteredSeat = filteredSeat;
    }

    public boolean isActionwiseRender() {
        return actionwiseRender;
    }

    public void setActionwiseRender(boolean actionwiseRender) {
        this.actionwiseRender = actionwiseRender;
    }

    public boolean isRegnTypeRender() {
        return regnTypeRender;
    }

    public void setRegnTypeRender(boolean regnTypeRender) {
        this.regnTypeRender = regnTypeRender;
    }

    public boolean isPermitWiseRender() {
        return permitWiseRender;
    }

    public void setPermitWiseRender(boolean permitWiseRender) {
        this.permitWiseRender = permitWiseRender;
    }

    public boolean isDisableOfficeSelection() {
        return disableOfficeSelection;
    }

    public void setDisableOfficeSelection(boolean disableOfficeSelection) {
        this.disableOfficeSelection = disableOfficeSelection;
    }

    public void setHeaderDescr(String headerDescr) {
        this.headerDescr = headerDescr;
    }

    public String getHeaderDescr() {
        return headerDescr;
    }
}