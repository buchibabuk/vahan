/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.FancyAuctionDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.ApproveImpl;
import nic.vahan.form.impl.FancyAuctionVerifyImpl;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;
import org.primefaces.component.commandbutton.CommandButton;

/**
 *
 * @author AMBRISH
 */
@ManagedBean(name = "fancynumberverifyapprovebean")
@RequestScoped
public class FancyAuctionVerifyBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(FancyAuctionEntryBean.class);
    private ArrayList<FancyAuctionDobj> listAuctionApplicationNumbers = new ArrayList<FancyAuctionDobj>();
    private CommandButton btn_save = new CommandButton();
    private FancyAuctionVerifyImpl impl = new FancyAuctionVerifyImpl();

    @PostConstruct
    public void init() {

        try {

            Map map = (Map) Util.getSession().getAttribute("seat_map");
            String REGN_NO = map.get("regn_no").toString();
            listAuctionApplicationNumbers = impl.getFancyApplicationDetailsVerifyApprove(REGN_NO);
            getBtn_save().setValue("Lottery Pending");

        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, ex.getMessage(), null));
        }
    }

    public String approveActionPerformed() {
        try {
            
            ApproveImpl approveImp = (ApproveImpl) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("approveImpl");
          
            String officeRemark = approveImp.getOffice_remark()!=null && approveImp.getOffice_remark().getValue()!=null ? approveImp.getOffice_remark().getValue().toString():"";
            String PublicRemark = approveImp.getPublic_remark()!=null && approveImp.getPublic_remark().getValue()!=null ? approveImp.getPublic_remark().getValue().toString():"";

            Status_dobj status = new Status_dobj();
            Map map = (Map) Util.getSession().getAttribute("seat_map");
            //this.APPL_NO = map.get("appl_no").toString();
            String PUR_CD = (String) map.get("pur_code");
            String APPL_NO = map.get("appl_no").toString();
            /////////////////////////////////////////////////////
            int ACTION_CDOE = Integer.parseInt(map.get("role").toString().trim());
            if (ACTION_CDOE == TableConstants.TM_ROLE_FANCY_NUMBER_VERIFY) {
                impl.verifyFancyApplication(officeRemark,PublicRemark);
            }

            if (ACTION_CDOE == TableConstants.TM_ROLE_FANCY_NUMBER_APPROVE) {
                FancyAuctionDobj winner = null;
                for (int i = 0; i < listAuctionApplicationNumbers.size(); i++) {
                    winner = listAuctionApplicationNumbers.get(i);
                    if (winner.getStatus().equalsIgnoreCase("A")) {
                        break;
                    }
                }

                impl.approveFancyApplication(winner,officeRemark,PublicRemark);

            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        return "seatwork";
    }

    /**
     * @return the listAuctionApplicationNumbers
     */
    public ArrayList<FancyAuctionDobj> getListAuctionApplicationNumbers() {
        return listAuctionApplicationNumbers;
    }

    /**
     * @param listAuctionApplicationNumbers the listAuctionApplicationNumbers to
     * set
     */
    public void setListAuctionApplicationNumbers(ArrayList<FancyAuctionDobj> listAuctionApplicationNumbers) {
        this.listAuctionApplicationNumbers = listAuctionApplicationNumbers;
    }

    /**
     * @return the btn_save
     */
    public CommandButton getBtn_save() {
        return btn_save;
    }

    /**
     * @param btn_save the btn_save to set
     */
    public void setBtn_save(CommandButton btn_save) {
        this.btn_save = btn_save;
    }

    /**
     * @return the impl
     */
    public FancyAuctionVerifyImpl getImpl() {
        return impl;
    }

    /**
     * @param impl the impl to set
     */
    public void setImpl(FancyAuctionVerifyImpl impl) {
        this.impl = impl;
    }
}
