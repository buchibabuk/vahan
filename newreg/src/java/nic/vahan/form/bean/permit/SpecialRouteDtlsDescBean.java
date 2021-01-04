/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.form.dobj.permit.SpecialRoutePermitDobj;

@ManagedBean(name = "splRouteDtlsDesc")
@ViewScoped
public class SpecialRouteDtlsDescBean {

    private Date valid_from = null;
    private Date temp_valid_from = null;
    private Date temp_valid_upto = null;
    private Date journeyStartValidUptoDate = null;
    private Date journeyEndsValidFromDate = null;
    private Date valid_upto = null;
    private boolean confirmCheckBox = false;
    private List<SpecialRoutePermitDobj> splRouteDobjList = null;
    private String journeyStateFrom = null;
    private boolean withInState = true;
    SpecialRoutePermitDobj dobj = null;
    private boolean disableMenuCheck = false;

    public SpecialRouteDtlsDescBean() {
        setSplRouteDobjList(new ArrayList<SpecialRoutePermitDobj>());
    }

    public void updateDates() {
        journeyEndsValidFromDate = null;
    }

    public void onClickWithInState() {
        if (withInState) {
            reset();
        } else {
            valid_from = temp_valid_from;
            valid_upto = temp_valid_upto;
        }
    }

    public void verifiedList(List<SpecialRoutePermitDobj> splRouteDobjList, String journeyStateFrom) {
        this.splRouteDobjList = splRouteDobjList;
        this.journeyStateFrom = journeyStateFrom;
        for (SpecialRoutePermitDobj dobj : splRouteDobjList) {
            switch (dobj.getSrl_no()) {
                case 1:
                    valid_from = temp_valid_from = dobj.getValid_from();
                    break;
                case 2:
                    journeyStartValidUptoDate = dobj.getValid_from();
                    break;
                case 3:
                    journeyEndsValidFromDate = dobj.getValid_from();
                    break;
                case 4:
                    valid_upto = temp_valid_upto = dobj.getValid_from();
                    break;
                default:
                    break;
            }
        }
        withInState = !(confirmCheckBox = valid_from != null && journeyStartValidUptoDate != null
                && journeyEndsValidFromDate != null && valid_upto != null);
        onClickWithInState();
    }

    public void confirmation() {
        if (confirmCheckBox) {
            if (valid_from != null && journeyStartValidUptoDate != null && journeyEndsValidFromDate != null
                    && valid_upto != null) {
                getSplRouteDobjList().add(new SpecialRoutePermitDobj(1, valid_from, journeyStateFrom, journeyStateFrom,
                        journeyStateFrom));
                getSplRouteDobjList().add(new SpecialRoutePermitDobj(2, journeyStartValidUptoDate, journeyStateFrom,
                        journeyStateFrom, journeyStateFrom));
                getSplRouteDobjList().add(new SpecialRoutePermitDobj(3, journeyEndsValidFromDate, journeyStateFrom,
                        journeyStateFrom, journeyStateFrom));
                getSplRouteDobjList().add(new SpecialRoutePermitDobj(4, valid_upto, journeyStateFrom, journeyStateFrom,
                        journeyStateFrom));
            } else {
                getSplRouteDobjList().clear();
                confirmCheckBox = false;
                JSFUtils.showMessage("Please fill all dates");
            }
        } else {
            getSplRouteDobjList().clear();
        }
    }

    public void reset() {
        withInState = true;
        valid_from = null;
        journeyStartValidUptoDate = null;
        journeyEndsValidFromDate = null;
        valid_upto = null;
        confirmCheckBox = false;
        splRouteDobjList = new ArrayList<SpecialRoutePermitDobj>();
        journeyStateFrom = null;

    }

    /**
     * @param valid_from the valid_from to set
     */
    public void setValid_from(Date valid_from) {
        this.valid_from = valid_from;
    }

    /**
     * @param valid_upto the valid_upto to set
     */
    public void setValid_upto(Date valid_upto) {
        this.valid_upto = valid_upto;
    }

    /**
     * @return the journeyStartValidUptoDate
     */
    public Date getJourneyStartValidUptoDate() {
        return journeyStartValidUptoDate;
    }

    /**
     * @param journeyStartValidUptoDate the journeyStartValidUptoDate to set
     */
    public void setJourneyStartValidUptoDate(Date journeyStartValidUptoDate) {
        this.journeyStartValidUptoDate = journeyStartValidUptoDate;
    }

    /**
     * @return the journeyEndsValidFromDate
     */
    public Date getJourneyEndsValidFromDate() {
        return journeyEndsValidFromDate;
    }

    /**
     * @param journeyEndsValidFromDate the journeyEndsValidFromDate to set
     */
    public void setJourneyEndsValidFromDate(Date journeyEndsValidFromDate) {
        this.journeyEndsValidFromDate = journeyEndsValidFromDate;
    }

    /**
     * @return the valid_from
     */
    public Date getValid_from() {
        return valid_from;
    }

    /**
     * @return the valid_upto
     */
    public Date getValid_upto() {
        return valid_upto;
    }

    /**
     * @return the confirmCheckBox
     */
    public boolean isConfirmCheckBox() {
        return confirmCheckBox;
    }

    /**
     * @param confirmCheckBox the confirmCheckBox to set
     */
    public void setConfirmCheckBox(boolean confirmCheckBox) {
        this.confirmCheckBox = confirmCheckBox;
    }

    /**
     * @return the journeyState
     */
    public String getJourneyStateFrom() {
        return journeyStateFrom;
    }

    /**
     * @param journeyState the journeyState to set
     */
    public void setJourneyState(String journeyStateFrom) {
        this.journeyStateFrom = journeyStateFrom;
    }

    /**
     * @return the splRouteDobjList
     */
    public List<SpecialRoutePermitDobj> getSplRouteDobjList() {
        return splRouteDobjList;
    }

    /**
     * @param splRouteDobjList the splRouteDobjList to set
     */
    public void setSplRouteDobjList(List<SpecialRoutePermitDobj> splRouteDobjList) {
        this.splRouteDobjList = splRouteDobjList;
    }

    /**
     * @return the withInState
     */
    public boolean isWithInState() {
        return withInState;
    }

    /**
     * @param withInState the withInState to set
     */
    public void setWithInState(boolean withInState) {
        this.withInState = withInState;
    }

    /**
     * @param temp_valid_from the temp_valid_from to set
     */
    public void setTemp_valid_from(Date temp_valid_from) {
        this.temp_valid_from = temp_valid_from;
    }

    /**
     * @param temp_valid_upto the temp_valid_upto to set
     */
    public void setTemp_valid_upto(Date temp_valid_upto) {
        this.temp_valid_upto = temp_valid_upto;
    }

    public boolean isDisableMenuCheck() {
        return disableMenuCheck;
    }

    public void setDisableMenuCheck(boolean disableMenuCheck) {
        this.disableMenuCheck = disableMenuCheck;
    }
}
