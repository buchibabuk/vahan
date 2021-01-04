/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.ComparisonDobj;
import nic.vahan.form.dobj.NocDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.NocImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import static nic.vahan.server.ServerUtil.comparison;
import org.apache.log4j.Logger;

@ViewScoped
@ManagedBean(name = "nocAdminBean")
public class NocAdminBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(NocAdminBean.class);
    private NocDobj nocDobj = new NocDobj();
    private NocDobj nocDobjOld = new NocDobj();
    private List stateList;
    private List officeList;
    private String header = "NOC Vehicle Details";
    private boolean render;
    private String regnNo;
    private OwnerDetailsDobj ownerDetailsDobj;
    private boolean sameState = false;
    private List<ComparisonDobj> comparisonList = new ArrayList<>();

    public NocAdminBean() {
        Map<String, String> params = FacesContext.getCurrentInstance().
                getExternalContext().getRequestParameterMap();
        if (params != null & !params.isEmpty()) {
            regnNo = params.get("regnNo");
            showDetails();
        }
    }

    public void showDetails() {
        String vahanMessages = null;
        try {
            if (regnNo == null || regnNo.trim().length() <= 0) {
                vahanMessages = "Invalid Registration No, Please Enter Valid Registration No";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vahanMessages, vahanMessages));
                return;
            }
            OwnerImpl ownerImpl = new OwnerImpl();
            ownerDetailsDobj = ownerImpl.getOwnerDetails(regnNo);
            if (ownerDetailsDobj == null) {
                vahanMessages = "Record not found against Registration No " + regnNo.toUpperCase();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vahanMessages, vahanMessages));
                return;
            }
            if (ownerDetailsDobj != null && !ownerDetailsDobj.getStatus().equalsIgnoreCase(TableConstants.VT_NOC_ISSUE_STATUS)) {
                vahanMessages = "Noc is Not Issued on this Vehicle";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vahanMessages, vahanMessages));
                return;
            }
            NocImpl nocImpl = new NocImpl();
            nocDobj = nocImpl.getNocDetails(regnNo, ownerDetailsDobj.getState_cd(), ownerDetailsDobj.getOff_cd());
            if (nocDobj == null) {
                vahanMessages = "Noc Details not found against Registration No " + regnNo.toUpperCase();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vahanMessages, vahanMessages));
                return;
            } else {
                if (nocDobj != null && nocDobj.getState_cd().equals(Util.getUserStateCode())) {
                    sameState = true;
                } else {
                    sameState = false;
                }
                nocDobjOld = (NocDobj) nocDobj.clone();
            }

            if (!Util.getUserStateCode().equalsIgnoreCase(nocDobj.getState_cd())
                    && !Util.getUserStateCode().equalsIgnoreCase(nocDobj.getState_to())) {
                vahanMessages = "You are not Authorised to Modify the Noc Details because NOC is not issued for this State";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vahanMessages, vahanMessages));
                return;
            }

            stateList = MasterTableFiller.getStateList();
            officeList = MasterTableFiller.getOfficeList(nocDobj.getState_to());

            if (Util.getUserStateCode().equalsIgnoreCase(nocDobj.getState_cd()) && nocDobj.getState_cd().equalsIgnoreCase(nocDobj.getState_to())) {
                header = "Clearance Certificate(CC)";
                Iterator ite = officeList.iterator();
                while (ite.hasNext()) {
                    SelectItem obj = (SelectItem) ite.next();
                    if (Integer.parseInt(obj.getValue().toString()) == nocDobj.getOff_cd()) {
                        officeList.remove(obj);
                        break;
                    }
                }
            }

            render = true;

        } catch (VahanException vex) {
            vahanMessages = vex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vahanMessages, vahanMessages));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "RegnNo -" + regnNo + " NOC Admin Form" + ex.getStackTrace()[0]);
            vahanMessages = "Something Went Wrong, Please Contact to the System Administrator";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vahanMessages, vahanMessages));
        }
    }

    public String saveNocModification() {
        String vahanMessages = null;
        try {
            NocImpl nocImpl = new NocImpl();
            List<ComparisonDobj> comparisonChanges = compareChanges();
            if (nocDobj != null && !comparisonChanges.isEmpty()) {
                nocImpl.updateAdminNOC(nocDobj, ComparisonBeanImpl.changedDataContentsByUser(comparisonChanges), Util.getEmpCode());
            }
            vahanMessages = "Data Updated Successfully against Registration No " + regnNo;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, vahanMessages, vahanMessages));


        } catch (VahanException vex) {
            vahanMessages = vex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vahanMessages, vahanMessages));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " (NocImpl.saveNocModification) RegnNo -" + regnNo + " NOC Admin Form" + ex.getStackTrace()[0]);
            vahanMessages = "Something Went Wrong, Please Contact to the System Administrator";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vahanMessages, vahanMessages));
        }
        return "home";
    }

    public void reset() {
        nocDobj = new NocDobj();
        nocDobjOld = null;
        stateList = null;
        officeList = null;
        header = "NOC Vehicle Details";
        render = false;
        regnNo = null;
        ownerDetailsDobj = null;
        comparisonList = new ArrayList<>();
    }

    public List compareChanges() {

        if (nocDobjOld == null) {
            return comparisonList;
        }
        comparisonList.clear();
        comparison("StateTo", nocDobjOld.getState_to(), nocDobj.getState_to(), comparisonList);
        comparison("AuthTo", String.valueOf(nocDobjOld.getOff_to()), String.valueOf(nocDobj.getOff_to()), comparisonList);
        comparison("RtoDispNo", nocDobjOld.getDispatch_no(), nocDobj.getDispatch_no(), comparisonList);
        comparison("NcrbRefNo", nocDobjOld.getNcrb_ref(), nocDobj.getNcrb_ref(), comparisonList);

        return comparisonList;
    }

    public void stateToListener() {
        if (nocDobj != null) {
            officeList = MasterTableFiller.getOfficeList(nocDobj.getState_to());
        }

    }

    /**
     * @return the nocDobj
     */
    public NocDobj getNocDobj() {
        return nocDobj;
    }

    /**
     * @param nocDobj the nocDobj to set
     */
    public void setNocDobj(NocDobj nocDobj) {
        this.nocDobj = nocDobj;
    }

    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
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
     * @return the officeList
     */
    public List getOfficeList() {
        return officeList;
    }

    /**
     * @param officeList the officeList to set
     */
    public void setOfficeList(List officeList) {
        this.officeList = officeList;
    }

    /**
     * @return the render
     */
    public boolean isRender() {
        return render;
    }

    /**
     * @param render the render to set
     */
    public void setRender(boolean render) {
        this.render = render;
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
     * @return the ownerDetailsDobj
     */
    public OwnerDetailsDobj getOwnerDetailsDobj() {
        return ownerDetailsDobj;
    }

    /**
     * @param ownerDetailsDobj the ownerDetailsDobj to set
     */
    public void setOwnerDetailsDobj(OwnerDetailsDobj ownerDetailsDobj) {
        this.ownerDetailsDobj = ownerDetailsDobj;
    }

    /**
     * @return the nocDobjOld
     */
    public NocDobj getNocDobjOld() {
        return nocDobjOld;
    }

    /**
     * @param nocDobjOld the nocDobjOld to set
     */
    public void setNocDobjOld(NocDobj nocDobjOld) {
        this.nocDobjOld = nocDobjOld;
    }

    /**
     * @return the comparisonList
     */
    public List<ComparisonDobj> getComparisonList() {
        return comparisonList;
    }

    /**
     * @param comparisonList the comparisonList to set
     */
    public void setComparisonList(List<ComparisonDobj> comparisonList) {
        this.comparisonList = comparisonList;
    }

    /**
     * @return the sameState
     */
    public boolean isSameState() {
        return sameState;
    }

    /**
     * @param sameState the sameState to set
     */
    public void setSameState(boolean sameState) {
        this.sameState = sameState;
    }
}
