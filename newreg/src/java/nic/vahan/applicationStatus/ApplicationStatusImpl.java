/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.applicationStatus;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.db.TableConstants;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.ApplStatusFileFlowDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.common.Appl_Details_Dobj;
import nic.vahan.form.impl.ApplicationInwardImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author acer
 */
public class ApplicationStatusImpl {

    private static final Logger LOGGER = Logger.getLogger(ApplicationStatusImpl.class);
    ApplicationInwardImpl inwardImpl = new ApplicationInwardImpl();
    List<ApplStatusFileFlowDobj> listStatusStepTemp = new ArrayList<>();

    public String[] applStatus(Appl_Details_Dobj applDetailsDobj) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String[] action_desc = null;
        List<String> listStatusStep = new ArrayList<>();
        int purcd = applDetailsDobj.getPur_cd();
        OwnerImpl ownerImpl = new OwnerImpl();
        Owner_dobj owner_dobj;
        try {
            tmgr = new TransactionManager("applStatus");
            ps = tmgr.prepareStatement("select  b.action_descr as dscr,a.flow_srno ,condition_formula,role_cd,a.action_cd from tm_purpose_action_flow a\n"
                    + "inner join  tm_action b on a.action_cd = b.action_cd\n"
                    + "where a.state_cd =? and a.pur_cd=? order by a.flow_srno");
            ps.setString(1, applDetailsDobj.getCurrent_state_cd());
            ps.setInt(2, applDetailsDobj.getPur_cd());

            RowSet rs = tmgr.fetchDetachedRowSet();
            boolean isOnlineAppl = inwardImpl.isOnlineApplication(applDetailsDobj.getAppl_no(), applDetailsDobj.getPur_cd());
            if (isOnlineAppl) {
                VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(applDetailsDobj.getOwnerDobj());
                listStatusStep.add("Online Submission");
                listStatusStep.add("Office Inward");
                while (rs.next()) {
                    if (!rs.getString("dscr").equalsIgnoreCase("Entry") && !rs.getString("dscr").equalsIgnoreCase("Fees")) {
                        if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "ApplicationStatusImpl.applStatus.tm_purpose_action_flow:" + applDetailsDobj.getAppl_no())) {
                            listStatusStep.add("Office " + rs.getString("dscr"));
                        }
                    }
                }
            } else {
                if (purcd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG || purcd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || purcd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || purcd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                    owner_dobj = ownerImpl.set_Owner_appl_db_to_dobj(null, applDetailsDobj.getAppl_no().toUpperCase(), "", applDetailsDobj.getPur_cd());
                    VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(owner_dobj);
                    while (rs.next()) {
                        ApplStatusFileFlowDobj dobj = new ApplStatusFileFlowDobj();
                        if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "ApplicationStatusImpl.applStatus.tm_purpose_action_flow:" + applDetailsDobj.getAppl_no())) {
                            if (rs.getInt("role_cd") == 17) {
                                if (rs.getInt("action_cd") == TableConstants.TM_ROLE_DEALER_NEW_APPL) {
                                    dobj.setActionCd(rs.getInt("action_cd"));
                                    dobj.setActionCdDescr("Dealer New Registration entry");
                                } else if (rs.getInt("action_cd") == TableConstants.TM_ROLE_DEALER_TEMP_APPL) {
                                    dobj.setActionCd(rs.getInt("action_cd"));
                                    dobj.setActionCdDescr("Dealer Temp Registration entry");
                                } else if (rs.getInt("action_cd") == TableConstants.TM_ROLE_DEALER_VERIFICATION || rs.getInt("action_cd") == TableConstants.TM_ROLE_DEALER_TEMP_VERIFICATION) {
                                    dobj.setActionCd(rs.getInt("action_cd"));
                                    dobj.setActionCdDescr("Dealer " + rs.getString("dscr"));
                                } else if (rs.getInt("action_cd") == TableConstants.TM_ROLE_DEALER_APPROVAL || rs.getInt("action_cd") == TableConstants.TM_ROLE_DEALER_TEMP_APPROVAL) {
                                    dobj.setActionCd(rs.getInt("action_cd"));
                                    dobj.setActionCdDescr("Dealer " + rs.getString("dscr"));
                                } else if (rs.getInt("action_cd") == TableConstants.TM_ROLE_DEALER_DOCUMENT_UPLOAD) {
                                    dobj.setActionCd(rs.getInt("action_cd"));
                                    dobj.setActionCdDescr("Dealer Doc Upload");
                                } else if (rs.getInt("action_cd") == TableConstants.VM_ROLE_REGISTRATION_NO_ASSIGNMENT_DEALER) {
                                    dobj.setActionCd(rs.getInt("action_cd"));
                                    dobj.setActionCdDescr("Dealer Choice No. ");
                                } else {
                                    dobj.setActionCd(rs.getInt("action_cd"));
                                    dobj.setActionCdDescr(rs.getString("dscr"));
                                }
                            } else {
                                dobj.setActionCd(rs.getInt("action_cd"));
                                dobj.setActionCdDescr("Office " + rs.getString("dscr"));
                            }
                        }
                        if (dobj != null && !CommonUtils.isNullOrBlank(dobj.getActionCdDescr())) {
                            listStatusStepTemp.add(dobj);
                        }
                    }
                } else {
                    VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(applDetailsDobj.getOwnerDobj());
                    listStatusStep.add("Office Inward");
                    while (rs.next()) {
                        if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "ApplicationStatusImpl.applStatus.tm_purpose_action_flow:" + applDetailsDobj.getAppl_no())) {
                            listStatusStep.add("Office " + rs.getString("dscr"));
                        }
                    }
                }
            }
            if (purcd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG || purcd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || purcd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || purcd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                if (listStatusStepTemp.size() > 0) {
                    action_desc = new String[listStatusStepTemp.size()];
                    for (int i = 0; i < listStatusStepTemp.size(); i++) {
                        action_desc[i] = listStatusStepTemp.get(i).getActionCdDescr();
                    }
                }

            } else {
                if (listStatusStep.size() > 0) {
                    action_desc = new String[listStatusStep.size()];
                    for (int i = 0; i < listStatusStep.size(); i++) {
                        action_desc[i] = listStatusStep.get(i);
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return action_desc;
    }

    public int currentApplStatus(Appl_Details_Dobj applDetailsDobj) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        int flowSrNo = 0;
        List<String> listStatusStep = new ArrayList<>();
        int purcd = applDetailsDobj.getPur_cd();
        try {
            tmgr = new TransactionManager("applStatus");
            ps = tmgr.prepareStatement("select * from va_status where state_cd = ? and pur_cd = ? and appl_no = ?");
            ps.setString(1, applDetailsDobj.getCurrent_state_cd());
            ps.setInt(2, applDetailsDobj.getPur_cd());
            ps.setString(3, applDetailsDobj.getAppl_no());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                boolean isOnlineAppl = inwardImpl.isOnlineApplication(applDetailsDobj.getAppl_no(), applDetailsDobj.getPur_cd());
                if (isOnlineAppl) {
                    flowSrNo = rs.getInt("flow_slno") - 1;
                } else if (purcd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG || purcd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || purcd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || purcd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                    flowSrNo = rs.getInt("flow_slno") - 1;
                    if (listStatusStepTemp.size() > 0) {
                        for (int i = 0; i < listStatusStepTemp.size(); i++) {
                            if (listStatusStepTemp.get(i).getActionCd() == rs.getInt("action_cd")) {
                                flowSrNo = i;
                            }
                        }
                    }
                } else {
                    flowSrNo = rs.getInt("flow_slno");
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during getting details ,Please contact to the system administrator.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return flowSrNo;
    }
}
