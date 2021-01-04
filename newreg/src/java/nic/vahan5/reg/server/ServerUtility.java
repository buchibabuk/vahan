/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.server;

import dao.UserDAO;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.sql.RowSet;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import nic.java.util.DateUtils;
import nic.java.util.DateUtilsException;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.fillVehicleParametersFromDobj;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.CommonUtils.MailSenderThread;
import nic.vahan.CommonUtils.OTPSMSSERVER;
import nic.vahan.CommonUtils.SmsSenderThread;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.DocumentType;
import nic.vahan.db.Dealer;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.db.user_mgmt.dobj.DealerMasterDobj;
import nic.vahan.db.user_mgmt.dobj.TmConfigurationOtpDobj;
import nic.vahan.db.user_mgmt.dobj.TmConfigurationUserMgmtDobj;
import nic.vahan.eapplication.OnlinePaymentImpl;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.permit.PermitRouteList;
import nic.vahan.form.dobj.AuditRecoveryDobj;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.dobj.BlackListedVehicleDobj;
import nic.vahan.form.dobj.ComparisonDobj;
import nic.vahan.form.dobj.DefacementDobj;
import nic.vahan.form.dobj.EpayDobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.NocDobj;
import nic.vahan.form.dobj.QrCodeDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.PrintCertificatesDobj;
import nic.vahan.form.dobj.RefundAndExcessDobj;
import nic.vahan.form.dobj.RetroFittingDetailsDobj;
import nic.vahan.form.dobj.SeatActionsDetails;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TaxBasedOnDobj;
import nic.vahan.form.dobj.TaxInstallCollectionDobj;
import nic.vahan.form.dobj.TmCofigurationOnlinePaymentDobj;
import nic.vahan.form.dobj.TmConfigurationDMS;
import nic.vahan.form.dobj.TmConfigurationDispatchDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.TmConfigurationNonUseDobj;
import nic.vahan.form.dobj.TmConfigurationPayVerifyDobj;
import nic.vahan.form.dobj.TmConfigurationSwappingDobj;
import nic.vahan.form.dobj.TmConfigurationUserMessagingDobj;
import nic.vahan.form.dobj.VmSmartCardHsrpDobj;
import nic.vahan.form.dobj.common.DOAuditTrail;
import nic.vahan.form.dobj.common.RegistrationStatusParametersDobj;
import nic.vahan.form.dobj.configuration.TmConfigurationAdvanceRegnDobj;
import nic.vahan.form.dobj.configuration.TmConfigurationFasTag;
import nic.vahan.form.dobj.configuration.TmConfigurationReceipts;
import nic.vahan.form.dobj.dealer.PaymentGatewayDobj;
import nic.vahan.form.dobj.dealer.TmConfigurationDealerDobj;
import nic.vahan.form.dobj.eChallan.AccusedDetailsDobj;
import nic.vahan.form.dobj.eChallan.DocTableDobj;
import nic.vahan.form.dobj.eChallan.OffencesDobj;
import nic.vahan.form.dobj.eChallan.WitnessdetailDobj;
import nic.vahan.form.dobj.fancy.AdvanceRegnNo_dobj;
import nic.vahan.form.dobj.permit.ExemptionDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitHomeAuthDobj;
import nic.vahan.form.dobj.permit.PermitTimeTableDobj;
import nic.vahan.form.dobj.permit.SpecialRoutePermitDobj;
import nic.vahan.form.dobj.reports.TmConfigurationPrintDobj;
import nic.vahan.form.dobj.smartcard.SmartCardDobj;
import nic.vahan.form.dobj.tax.TaxDobj;
import nic.vahan.form.dobj.tradecert.TmConfigurationTradeCertificateDobj;
import nic.vahan.form.impl.ApplicationInwardImpl;
import nic.vahan.form.impl.ApproveImpl;
import nic.vahan.form.impl.BlackListedVehicleImpl;
import static nic.vahan.form.impl.EpayImpl.getDueDateForNewRegistration;
import static nic.vahan.form.impl.EpayImpl.getPurposeCodeFee;
import nic.vahan.form.impl.FeeImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.Home_Impl;
import nic.vahan.form.impl.HpaImpl;
import nic.vahan.form.impl.InsImpl;
import nic.vahan.form.impl.NocImpl;
import nic.vahan.form.impl.NonUseImpl;
import nic.vahan.form.impl.SeatAllotedDetails;
import nic.vahan.form.impl.SmsMailOTPImpl;
import nic.vahan.form.impl.TaxEndorsementImpl;
import nic.vahan.form.impl.TaxExemptionImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.ExemptionImpl;
import nic.vahan.form.impl.dealer.OfficeCorrectionImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.TOTP;
import nic.vahan.server.workdistribution.NextStageRequest;
import nic.vahan.server.workdistribution.NextStageResponse;
import nic.vahan.server.workdistribution.NextStageWS;
import nic.vahan.services.DefacementImpl;
import nic.vahan.services.ResponceList;
import nic.vahan.services.RetLLDetails;
import nic.vahan.services.DmsDocCheckUtils;
import nic.vahan.services.VTDocumentModel;
import nic.vahan5.reg.form.impl.ApplicationInwardImplementation;
import nic.vahan5.reg.form.impl.FeeImplementation;
import nic.vahan5.reg.form.impl.Utility;
import nic.vahan5.reg.services.DefacementImplementation;
import org.apache.commons.codec.binary.Base64;
import org.primefaces.component.calendar.Calendar;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.inputtextarea.InputTextarea;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.PrimeFaces;
//import tax.web.service.VahanTaxParameters;
import nic.vahan.form.dobj.tax.VahanTaxParameters;
import nic.vahan.form.impl.SmartCardImpl;
import nic.vahan5.reg.commonutils.FormulaUtilities;
import nic.vahan5.reg.form.impl.SmartCardImplementation;
import nic.vahan5.reg.form.impl.TaxServerImplementation;
import nic.vahan5.reg.rest.model.SessionVariablesModel;

/**
 *
 * @author Kartikey Singh
 */
public class ServerUtility {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ServerUtility.class);

    public static List<SeatActionsDetails> getSeatActionList(int off_cd, int role_cd) {
        List<SeatActionsDetails> list = new ArrayList<>();
        String sql = "SELECT row_number() over() as srl_no, off_cd, off_name, user_cd,"
                + "       user_name, desig_name, seat_cd, cntr_id, seat_descr,"
                + "       action_cd as roleCode, action_descr as role_descr,role_cd "
                + "  FROM " + TableList.VIEW_OFF_USER_SEAT_ACTION
                + " where user_cd =? and off_cd=? and role_cd=?";
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmg = null;
        try {
            tmg = new TransactionManagerReadOnly("getSeatCode");
            ps = tmg.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(Util.getEmpCode()));
            ps.setInt(2, off_cd);
            ps.setInt(3, role_cd);
            RowSet rs = tmg.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                SeatActionsDetails sad = new SeatActionsDetails();
                sad.setSrl_no(rs.getInt("srl_no"));
                sad.setSeat_cd(rs.getString("seat_cd"));
                sad.setSeat_descr(rs.getString("seat_descr"));
                sad.setOff_name(rs.getString("off_name"));
                sad.setOff_cd(rs.getInt("off_cd"));
                sad.setRole_desc(rs.getString("role_descr"));
                sad.setSeatrole(rs.getInt("roleCode"));
                sad.setCntr_id(rs.getString("cntr_id"));
                list.add(sad);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmg != null) {
                    tmg.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return list;
    }

    public static boolean getDataEntryIncomplete(String appl_no) {
        boolean dataEntryIncomplete = false;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        try {
            tmgr = new TransactionManagerReadOnly("getAllotedActionCodeDescr()");
            sql = "select a.pur_cd, a.action_cd"
                    + "  from " + TableList.VA_STATUS + " a," + TableList.TM_ACTION + " b"
                    + "  where a.appl_no =? and b.action_cd = a.action_cd and b.role_cd = 4 and a.pur_cd in (4,5,6,7,11,16)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rsvastatus = tmgr.fetchDetachedRowSet();
            if (rsvastatus.next()) {
                dataEntryIncomplete = true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return dataEntryIncomplete;
    }

    public static List<SeatAllotedDetails> getSeatCode(int off_cd, int role_cd) {
        List<SeatAllotedDetails> list = new ArrayList<>();

        String sql = "SELECT off_cd, off_name, user_cd,"
                + "       user_name, desig_name, seat_cd, cntr_id, seat_descr,"
                + "       action_cd as roleCode, action_descr as role_descr,role_cd "
                + "  FROM " + TableList.VIEW_OFF_USER_SEAT_ACTION
                + " where user_cd =? and off_cd=? and role_cd=? order by role_descr";
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmg = null;
        try {
            tmg = new TransactionManagerReadOnly("getSeatCode");

            ps = tmg.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(Util.getEmpCode()));
            ps.setInt(2, off_cd);
            ps.setInt(3, role_cd);

            RowSet rs = tmg.fetchDetachedRowSet();
            while (rs.next()) {
                SeatAllotedDetails sad = new SeatAllotedDetails();

                sad.setSeat_cd(rs.getString("seat_cd"));
                sad.setSeat_descr(rs.getString("seat_descr"));
                sad.setOff_name(rs.getString("off_name"));
                sad.setOff_cd(rs.getInt("off_cd"));
                sad.setRole_desc(rs.getString("role_descr"));
                sad.setSeatrole(rs.getInt("roleCode"));
                sad.setCntr_id(rs.getString("cntr_id"));
                list.add(sad);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmg != null) {
                    tmg.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return list;
    }

    public static ArrayList<SeatAllotedDetails> seatWorkList(String selected_seat, String selected_cntr_id, String getSelected_off_cd, int selected_role_cd) {

        ArrayList<SeatAllotedDetails> list = new ArrayList<SeatAllotedDetails>();
        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmg = null;
        try {
            tmg = new TransactionManagerReadOnly("seatWorkList1");
            sql = "select row_number() over() as srl_no,va.pur_cd,va.status,va.seat_cd,va.cntr_id,va.appl_no,va.action_cd as seat_role,c.descr,"
                    + "va.file_movement_slno, vha.office_remark,vha.public_remark,dtls.regn_no,to_char(dtls.appl_dt,'DD-MON-YYYY') as appl_dt"
                    + " from " + TableList.VA_STATUS + " va "
                    + " left outer join " + TableList.VHA_STATUS + " vha on vha.appl_no = va.appl_no and "
                    + " vha.pur_cd = va.pur_cd and vha.file_movement_slno = va.file_movement_slno-1  "
                    + "  left outer join tm_purpose_mast c on va.pur_cd=c.pur_cd "
                    + "  left outer join va_details dtls on va.appl_no=dtls.appl_no and va.pur_cd = dtls.pur_cd "
                    + " where va.action_cd=? "// and va.cntr_id=?"
                    + " and va.off_cd= ? and va.status IN ('" + TableConstants.STATUS_NOT_STARTED + "','" + TableConstants.STATUS_DISPATCH_PENDING + "')"
                    + " and va.action_cd::text NOT LIKE '%" + TableConstants.TM_ROLE_PRINT + "' and va.state_cd=? ";

            ps = tmg.prepareStatement(sql);
            ps.setInt(1, selected_role_cd);
            ps.setInt(2, Integer.parseInt(getSelected_off_cd));
            ps.setString(3, Util.getUserStateCode());
            RowSet rs = tmg.fetchDetachedRowSet();
            while (rs.next()) {
                SeatAllotedDetails wd = new SeatAllotedDetails();

                wd.setSrl_no(rs.getInt("srl_no"));
                wd.setAppl_no(rs.getString("appl_no"));
                wd.setSeat_cd(rs.getString("seat_cd"));
                wd.setSeatrole(rs.getInt("seat_role"));
                wd.setSeat_descr(rs.getString("descr"));
                wd.setAppl_no(rs.getString("appl_no"));
                wd.setPur_cd(rs.getInt("pur_cd"));
                wd.setDescr(rs.getString("descr"));
                wd.setOffice_remark(rs.getString("office_remark"));
                wd.setRemark_for_public(rs.getString("public_remark"));
                wd.setFile_movement_slno(rs.getInt("file_movement_slno"));
                wd.setRegn_no(rs.getString("regn_no"));
                wd.setAppl_dt(rs.getString("appl_dt"));
                wd.setStatus(rs.getString("status"));
                list.add(wd);
            }

            tmg = new TransactionManagerReadOnly("seatWorkList2");
            sql = "select row_number() over() as srl_no ,count(*) sum,va.pur_cd,va.seat_cd,"
                    + "    va.action_cd as seat_role,b.descr,va.status  "
                    + "    from " + TableList.VA_STATUS + " va  "
                    + "    left outer join tm_purpose_mast b on va.pur_cd=b.pur_cd "
                    + "    where va.off_cd = ? and  va.action_cd=? and"
                    + "    va.action_cd::text LIKE '%" + TableConstants.TM_ROLE_PRINT + "' "
                    + "    and va.status IN ('" + TableConstants.STATUS_NOT_STARTED + "') "
                    + "    group by va.pur_cd,va.seat_cd,va.action_cd,b.descr,va.status";

            ps = tmg.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(getSelected_off_cd));
            ps.setInt(2, selected_role_cd);
            rs = tmg.fetchDetachedRowSet();
            while (rs.next()) {
                SeatAllotedDetails wd = new SeatAllotedDetails();

                wd.setSrl_no(rs.getInt("srl_no"));
                wd.setSeat_descr(rs.getString("descr"));
                wd.setPur_cd(rs.getInt("pur_cd"));
                wd.setDescr(rs.getString("descr"));
                wd.setSeatrole(rs.getInt("seat_role"));
                wd.setStatus(rs.getString("status"));
                list.add(wd);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmg != null) {
                    tmg.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return list;
    }
// add by manoj

    public static void CompareSpacialRoute(String name, List<SpecialRoutePermitDobj> fromDb, List<SpecialRoutePermitDobj> form, List<ComparisonBean> compBeanList) {

        ComparisonBean comparisonBean = null;
        String oldVal = "";
        String newValue = "";
        for (SpecialRoutePermitDobj s : fromDb) {
            oldVal = oldVal + s.getSrl_no() + "," + s.getValid_from() + "," + s.getRoute_fr().toUpperCase() + "," + s.getVia().toUpperCase() + "," + s.getRoute_to().toUpperCase() + "|| ";
        }

        for (SpecialRoutePermitDobj ss : form) {
            newValue = newValue + ss.getSrl_no() + "," + ss.getValid_from() + "," + ss.getRoute_fr().toUpperCase() + "," + ss.getVia().toUpperCase() + "," + ss.getRoute_to().toUpperCase() + "|| ";
        }
        if (!oldVal.equalsIgnoreCase(newValue)) {
            comparisonBean = new ComparisonBean();
            comparisonBean.setFields(name);
            comparisonBean.setOld_value(oldVal);
            comparisonBean.setNew_value(newValue);
            compBeanList.add(comparisonBean);
        }
    }

    public static String[] findDisappEmpSeatRole(int pur, int curRole, String status) {

        String var[] = new String[4];
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("findDisapptEmpSeatRole");
            ps = tmgr.prepareStatement("select new_pos,role,seat,emp from disapprove_flow where pur_cd=? and cur_role=? and new_status=?");
            ps.setInt(1, pur);
            ps.setString(2, String.valueOf(curRole));
            ps.setString(3, status);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                var[0] = rs.getString("seat");
                var[1] = rs.getString("emp");
                var[2] = rs.getString("role");
                var[3] = rs.getString("new_pos");
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return var;
    }

    public static void Compare(String name, Object fromDb, UIComponent form, List<ComparisonBean> compBeanList) {

        ComparisonBean comparisonBean = null;

        if (form instanceof InputText) {
            InputText temp = (InputText) form;
            if (temp.getValue() == null) {
                temp.setValue("");
            }

            if (fromDb == null) {
                fromDb = "";
            }

            if (!temp.getValue().toString().equals(fromDb.toString())) {
                comparisonBean = new ComparisonBean();
                comparisonBean.setFields(name);
                comparisonBean.setOld_value(fromDb.toString());
                comparisonBean.setNew_value(temp.getValue().toString());
                compBeanList.add(comparisonBean);
            }
        }

        if (form instanceof SelectOneMenu) {
            SelectOneMenu temp = (SelectOneMenu) form;
            if (!temp.getValue().toString().trim().equalsIgnoreCase(fromDb.toString().trim())) {

                comparisonBean = new ComparisonBean();
                comparisonBean.setFields(name);
                comparisonBean.setOld_value(fromDb.toString());
                comparisonBean.setNew_value(temp.getValue().toString());
                compBeanList.add(comparisonBean);

            }
        }

        if (form instanceof Calendar) {
            Calendar temp = (Calendar) form;
            if (!temp.getValue().toString().equals(fromDb.toString())) {
                comparisonBean = new ComparisonBean();
                comparisonBean.setFields(name);
                comparisonBean.setOld_value(fromDb.toString());
                comparisonBean.setNew_value(temp.getValue().toString());
                compBeanList.add(comparisonBean);
            }
        }

        if (form instanceof InputTextarea) {
            InputTextarea temp = (InputTextarea) form;
            if (!temp.getValue().toString().equals(fromDb.toString())) {
                comparisonBean = new ComparisonBean();
                comparisonBean.setFields(name);
                comparisonBean.setOld_value(fromDb.toString());
                comparisonBean.setNew_value(temp.getValue().toString());
                compBeanList.add(comparisonBean);
            }
        }
    }

    public static void Compare(String name, String fromDb, String form, List<ComparisonBean> compBeanList) {

        ComparisonBean comparisonBean = null;
        if (fromDb == null) {
            fromDb = "";
        }
        if (form == null) {
            form = "";
        }

        if (!form.equals(fromDb)) {

            comparisonBean = new ComparisonBean();
            comparisonBean.setFields(name);
            comparisonBean.setOld_value(fromDb.toString());
            comparisonBean.setNew_value(form.toString());
            compBeanList.add(comparisonBean);

        }

    }

    public static void comparison(String name, String dbValue, String formValue, List<ComparisonDobj> comparisonList) {

        ComparisonDobj comparisonDobj = null;
        if (dbValue == null) {
            dbValue = "";
        }
        if (formValue == null) {
            formValue = "";
        }
        if (!formValue.equals(dbValue)) {
            comparisonDobj = new ComparisonDobj();
            comparisonDobj.setFields(name);
            comparisonDobj.setOld_value(dbValue.toString());
            comparisonDobj.setNew_value(formValue.toString());
            comparisonList.add(comparisonDobj);
        }
    }

    public static void Compare(String name, List<PermitRouteList> fromDb, List<PermitRouteList> form, List<ComparisonBean> compBeanList) {

        ComparisonBean comparisonBean = null;

        String oldVal = "";
        String newValue = "";
        for (PermitRouteList s : fromDb) {
            oldVal = oldVal + s.getKey() + ",";
        }

        for (Object ss : form) {
            if (ss instanceof String) {
                newValue = newValue + (String) ss + ",";
            } else if (ss instanceof PermitRouteList) {
                newValue = newValue + ((PermitRouteList) ss).getKey() + ",";
            }
        }
        if (!oldVal.equalsIgnoreCase(newValue)) {
            comparisonBean = new ComparisonBean();
            comparisonBean.setFields(name);
            comparisonBean.setOld_value(oldVal);
            comparisonBean.setNew_value(newValue);
            compBeanList.add(comparisonBean);

        }

    }
    //added by Raj Yadav 27-08-2015

    public static void Compare(String name, List<PermitTimeTableDobj> fromDb, List<PermitTimeTableDobj> form, List<ComparisonBean> compBeanList, String test) {
        ComparisonBean comparisonBean = null;

        String oldVal = "";
        String newValue = "";

        try {
            for (PermitTimeTableDobj fromDbObj : fromDb) {
                oldVal = oldVal + fromDbObj.getDay() + ", " + fromDbObj.getRoute_fr_time() + ", " + fromDbObj.getRoute_to_time() + " || ";
            }

            for (PermitTimeTableDobj formObj : form) {
                newValue = newValue + formObj.getDay() + ", " + formObj.getRoute_fr_time() + ", " + formObj.getRoute_to_time() + " || ";
            }
            if (!oldVal.equalsIgnoreCase(newValue)) {
                comparisonBean = new ComparisonBean();
                comparisonBean.setFields(name);
                comparisonBean.setOld_value(oldVal);
                comparisonBean.setNew_value(newValue);
                compBeanList.add(comparisonBean);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static void Compare(String name, String[] fromDbArr, String[] formArr, List<ComparisonBean> compBeanList) {
        String fromDb = "";
        String form = "";
        ComparisonBean comparisonBean = null;
        if (fromDbArr != null && fromDbArr.length > 0) {
            for (String region : fromDbArr) {
                fromDb += region + ",";
            }
        }

        if (formArr != null && formArr.length > 0) {
            for (String region : formArr) {
                form += region + ",";
            }
        }

        if (!form.equals(fromDb)) {
            comparisonBean = new ComparisonBean();
            comparisonBean.setFields(name);
            comparisonBean.setOld_value(fromDb.toString());
            comparisonBean.setNew_value(form.toString());
            compBeanList.add(comparisonBean);
        }

    }

    public static void compareRefundAndExcess(String name, List<RefundAndExcessDobj> fromDb, List<RefundAndExcessDobj> form, List<ComparisonBean> compBeanList) {

        ComparisonBean comparisonBean = null;

        String oldVal = "";
        String newValue = "";
        for (RefundAndExcessDobj s : fromDb) {

            oldVal = oldVal + s.getAppl_no() + " ," + s.getRegn_no() + " ," + s.getState_cd() + " ," + s.getOff_cd() + " ," + s.getExcessAmt() + "," + s.getRefundAmt() + "," + s.getPur_cd() + "," + s.getTaxFrom() + "," + s.getTaxUpto() + "," + s.getOp_dt() + "|| ";
        }

        for (RefundAndExcessDobj ss : form) {
            newValue = newValue + ss.getAppl_no() + " ," + ss.getRegn_no() + " ," + ss.getState_cd() + " ," + ss.getOff_cd() + " ," + ss.getExcessAmt() + "," + ss.getRefundAmt() + "," + ss.getPur_cd() + "," + ss.getTaxFrom() + "," + ss.getTaxUpto() + "," + ss.getOp_dt() + "|| ";
        }

        if (!oldVal.equalsIgnoreCase(newValue)) {
            comparisonBean = new ComparisonBean();
            comparisonBean.setFields(name);
            comparisonBean.setOld_value(oldVal);
            comparisonBean.setNew_value(newValue);
            compBeanList.add(comparisonBean);

        }

    }

    public static void CompareWitnessList(String name, List<WitnessdetailDobj> fromDb, List<WitnessdetailDobj> form, List<ComparisonBean> compBeanList) {

        ComparisonBean comparisonBean = null;

        String oldVal = "";
        String newValue = "";
        for (WitnessdetailDobj s : fromDb) {

            oldVal = oldVal + s.getWitnessName() + " ," + s.getWitnessAddress() + " ," + s.getWitnessContactNo() + " ," + s.getPoliceStation() + " , || ";
        }

        for (WitnessdetailDobj ss : form) {
            newValue = newValue + ss.getWitnessName() + " ," + ss.getWitnessAddress() + " ," + ss.getWitnessContactNo() + " ," + ss.getPoliceStation() + " , || ";
        }

        if (!oldVal.equalsIgnoreCase(newValue)) {
            comparisonBean = new ComparisonBean();
            comparisonBean.setFields(name);
            comparisonBean.setOld_value(oldVal);
            comparisonBean.setNew_value(newValue);
            compBeanList.add(comparisonBean);

        }

    }
//ADDED BY NITIN KUMAR 18-12-2014

    public static void CompareAccusedList(String name, List<AccusedDetailsDobj> fromDb, List<AccusedDetailsDobj> form, List<ComparisonBean> compBeanList) {

        ComparisonBean comparisonBean = null;

        String oldVal = "";
        String newValue = "";
        for (AccusedDetailsDobj s : fromDb) {

            oldVal = oldVal + s.getAccName() + " ," + s.getAccDesc() + " ," + s.getAccAddress() + " ," + s.getAccDLBladgeNo() + " ," + s.getAccPoliceStation() + "," + s.getAccCity() + "," + s.getAccFlag() + "," + s.getAccPincode() + "|| ";
        }

        for (AccusedDetailsDobj ss : form) {
            newValue = newValue + ss.getAccName() + " ," + ss.getAccDesc() + " ," + ss.getAccAddress() + " ," + ss.getAccDLBladgeNo() + " ," + ss.getAccPoliceStation() + "," + ss.getAccCity() + "," + ss.getAccFlag() + "," + ss.getAccPincode() + "|| ";
        }

        if (!oldVal.equalsIgnoreCase(newValue)) {
            comparisonBean = new ComparisonBean();
            comparisonBean.setFields(name);
            comparisonBean.setOld_value(oldVal);
            comparisonBean.setNew_value(newValue);
            compBeanList.add(comparisonBean);

        }

    }

    public static void CompareOffenceList(String name, List<OffencesDobj> fromDb, List<OffencesDobj> form, List<ComparisonBean> compBeanList) {

        ComparisonBean comparisonBean = null;

        String oldVal = "";
        String newValue = "";
        for (OffencesDobj s : fromDb) {

            oldVal = oldVal + s.getAccuseInOffDetails() + " ," + s.getAccusedDescr() + " ," + s.getMvcrClause() + " ," + s.getOffenceCode() + " ," + s.getOffenceDescr() + "," + s.getPenalty() + "," + s.getSectionName() + ", || ";
        }

        for (OffencesDobj ss : form) {
            newValue = newValue + ss.getAccuseInOffDetails() + " ," + ss.getAccusedDescr() + " ," + ss.getMvcrClause() + " ," + ss.getOffenceCode() + " ," + ss.getOffenceDescr() + "," + ss.getPenalty() + "," + ss.getSectionName() + ", || ";
        }

        if (!oldVal.equalsIgnoreCase(newValue)) {
            comparisonBean = new ComparisonBean();
            comparisonBean.setFields(name);
            comparisonBean.setOld_value(oldVal);
            comparisonBean.setNew_value(newValue);
            compBeanList.add(comparisonBean);

        }

    }

    public static void CompareDocumentImpounded(String name, List<DocTableDobj> fromDb, List<DocTableDobj> form, List<ComparisonBean> compBeanList) {

        ComparisonBean comparisonBean = null;

        String oldVal = "";
        String newValue = "";
        for (DocTableDobj s : fromDb) {

            oldVal = oldVal + s.getDocumentDesc() + " ," + s.getAccusedDescDocImpnd() + " ," + s.getDocNo() + " ," + s.getValidity() + "," + s.getIssueAuth() + ", || ";
        }

        for (DocTableDobj ss : form) {
            newValue = newValue + ss.getDocumentDesc() + " ," + ss.getAccusedDescDocImpnd() + " ," + ss.getDocNo() + " ," + ss.getValidity() + "," + ss.getIssueAuth() + ", || ";
        }

        if (!oldVal.equalsIgnoreCase(newValue)) {
            comparisonBean = new ComparisonBean();
            comparisonBean.setFields(name);
            comparisonBean.setOld_value(oldVal);
            comparisonBean.setNew_value(newValue);
            compBeanList.add(comparisonBean);

        }

    }

//ADDED BY NITIN KUMAR 18-12-2014
    public static void Compare(String name, Date fromDb, Date form, List<ComparisonBean> compBeanList) {
        Long FromDbLng = 0l;
        Long FormLng = 0l;
        ComparisonBean comparisonBean = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        sdf.applyPattern("yyyyMMdd");
        if (fromDb != null) {
            FromDbLng = Long.valueOf(sdf.format(fromDb));
        }
        if (form != null) {
            FormLng = Long.valueOf(sdf.format(form));
        }
        if (!FromDbLng.equals(FormLng)) {
            sdf = new SimpleDateFormat("dd-MM-yyyy");
            sdf.applyPattern("dd-MM-yyyy");
            comparisonBean = new ComparisonBean();
            comparisonBean.setFields(name);
            if (fromDb != null) {
                comparisonBean.setOld_value(DateUtils.parseDate(fromDb));
            } else {
                comparisonBean.setOld_value("");
            }
            if (form != null) {
                comparisonBean.setNew_value(DateUtils.parseDate(form));
            } else {
                comparisonBean.setNew_value("");
            }
            compBeanList.add(comparisonBean);
        }
    }

    public static void Compare(String name, int fromDb, int form, List<ComparisonBean> compBeanList) {

        ComparisonBean comparisonBean = null;

        if (fromDb != form) {

            comparisonBean = new ComparisonBean();
            comparisonBean.setFields(name);
            comparisonBean.setOld_value("" + fromDb);
            comparisonBean.setNew_value("" + form);
            compBeanList.add(comparisonBean);
        }
    }

    public static void Compare(String name, long fromDb, long form, List<ComparisonBean> compBeanList) {

        ComparisonBean comparisonBean = null;

        if (fromDb != form) {

            comparisonBean = new ComparisonBean();
            comparisonBean.setFields(name);
            comparisonBean.setOld_value("" + fromDb);
            comparisonBean.setNew_value("" + form);
            compBeanList.add(comparisonBean);

        }

    }

    public static void Compare(String name, float fromDb, float form, List<ComparisonBean> compBeanList) {

        ComparisonBean comparisonBean = null;

        if (Float.compare(fromDb, form) != 0) {
            comparisonBean = new ComparisonBean();
            comparisonBean.setFields(name);
            comparisonBean.setOld_value("" + fromDb);
            comparisonBean.setNew_value("" + form);
            compBeanList.add(comparisonBean);
        }
    }

    public static void fileFlowForNewApplication(TransactionManager tmgr, Status_dobj status_dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "insert into " + TableList.VA_STATUS
                    + "(status,state_cd,off_cd,appl_no,pur_cd,flow_slno,file_movement_slno,action_cd,seat_cd,"
                    + " cntr_id,office_remark,public_remark,file_movement_type,emp_cd,op_dt) "
                    + " values('N',?,?,?,?,?,?,?,'N','','','','F',?,current_timestamp)";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, status_dobj.getState_cd());
            ps.setInt(2, status_dobj.getOff_cd());
            ps.setString(3, status_dobj.getAppl_no());
            ps.setInt(4, status_dobj.getPur_cd());
            ps.setInt(5, status_dobj.getFlow_slno());
            ps.setInt(6, status_dobj.getFile_movement_slno());
            ps.setInt(7, status_dobj.getAction_cd());
            ps.setLong(8, status_dobj.getEmp_cd());
            ps.executeUpdate();

            /**
             * Remove this check for two flow for same application in case of
             * Temp fee in New regn.
             */
            if (status_dobj.getAction_cd() != TableConstants.TM_TMP_RC_APPROVAL && status_dobj.getAction_cd() != TableConstants.TM_ROLE_DEALER_TEMP_APPROVAL && status_dobj.getAction_cd() != TableConstants.TM_TMP_RC_VERIFICATION) {
                sql = "SELECT appl_no from " + TableList.VA_DETAILS + " where appl_no = ? and regn_no != ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, status_dobj.getAppl_no());
                ps.setString(2, status_dobj.getRegn_no());
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    LOGGER.error("WhereAmI:" + tmgr.getWhereiam() + ", Application No " + status_dobj.getAppl_no() + " already exist.");
                    throw new VahanException("Application No already exist, please try again.");
                }
            }

            String ins_va_details = "INSERT INTO " + TableList.VA_DETAILS
                    + " ( appl_no,pur_cd,appl_dt,regn_no,user_id,user_type,state_cd,off_cd,entry_status,entry_ip)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps_va_details = tmgr.prepareStatement(ins_va_details);
            ps_va_details.setString(1, status_dobj.getAppl_no()); // actual application no
            ps_va_details.setInt(2, status_dobj.getPur_cd());
            ps_va_details.setTimestamp(3, ServerUtility.getSystemDateInPostgres());
            ps_va_details.setString(4, status_dobj.getRegn_no());
            ps_va_details.setString(5, Util.getUserId());
            ps_va_details.setString(6, ""); // blank user type.
            ps_va_details.setString(7, status_dobj.getState_cd());
            ps_va_details.setInt(8, status_dobj.getOff_cd());
            ps_va_details.setString(9, "Y");
            ps_va_details.setString(10, Util.getClientIpAdress());
            ps_va_details.executeUpdate();

        } catch (SQLException sqle) {
            throw new VahanException(sqle.getMessage());
        }

    }

    /*
     * @author Kartikey Singh
     */
    public static void fileFlowForNewApplication(TransactionManager tmgr, Status_dobj status_dobj,
            String userId, String clientIpAddress) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "insert into " + TableList.VA_STATUS
                    + "(status,state_cd,off_cd,appl_no,pur_cd,flow_slno,file_movement_slno,action_cd,seat_cd,"
                    + " cntr_id,office_remark,public_remark,file_movement_type,emp_cd,op_dt) "
                    + " values('N',?,?,?,?,?,?,?,'N','','','','F',?,current_timestamp)";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, status_dobj.getState_cd());
            ps.setInt(2, status_dobj.getOff_cd());
            ps.setString(3, status_dobj.getAppl_no());
            ps.setInt(4, status_dobj.getPur_cd());
            ps.setInt(5, status_dobj.getFlow_slno());
            ps.setInt(6, status_dobj.getFile_movement_slno());
            ps.setInt(7, status_dobj.getAction_cd());
            ps.setLong(8, status_dobj.getEmp_cd());
            ps.executeUpdate();

            /**
             * Remove this check for two flow for same application in case of
             * Temp fee in New regn.
             */
            if (status_dobj.getAction_cd() != TableConstants.TM_TMP_RC_APPROVAL && status_dobj.getAction_cd() != TableConstants.TM_ROLE_DEALER_TEMP_APPROVAL && status_dobj.getAction_cd() != TableConstants.TM_TMP_RC_VERIFICATION) {
                sql = "SELECT appl_no from " + TableList.VA_DETAILS + " where appl_no = ? and regn_no != ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, status_dobj.getAppl_no());
                ps.setString(2, status_dobj.getRegn_no());
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    LOGGER.error("WhereAmI:" + tmgr.getWhereiam() + ", Application No " + status_dobj.getAppl_no() + " already exist.");
                    throw new VahanException("Application No already exist, please try again.");
                }
            }

            String ins_va_details = "INSERT INTO " + TableList.VA_DETAILS
                    + " ( appl_no,pur_cd,appl_dt,regn_no,user_id,user_type,state_cd,off_cd,entry_status,entry_ip)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps_va_details = tmgr.prepareStatement(ins_va_details);
            ps_va_details.setString(1, status_dobj.getAppl_no()); // actual application no
            ps_va_details.setInt(2, status_dobj.getPur_cd());
            ps_va_details.setTimestamp(3, ServerUtility.getSystemDateInPostgres());
            ps_va_details.setString(4, status_dobj.getRegn_no());
            ps_va_details.setString(5, userId);
            ps_va_details.setString(6, ""); // blank user type.
            ps_va_details.setString(7, status_dobj.getState_cd());
            ps_va_details.setInt(8, status_dobj.getOff_cd());
            ps_va_details.setString(9, "Y");
            ps_va_details.setString(10, clientIpAddress);
            ps_va_details.executeUpdate();

        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            throw new VahanException(sqle.getMessage());
        }

    }

    public static void fileFlow(TransactionManager tmgr, Status_dobj status_dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        String updateSeatCd = "";
        String seatCd = "";
        try {
            if (status_dobj.getStatus() != null && status_dobj.getStatus().equals(TableConstants.STATUS_HOLD)) {
                seatCd = "seat_cd = '" + status_dobj.getStatus() + "',";
            }
            sql = "update " + TableList.VA_STATUS + " set "
                    + "status = ?,"
                    + "" + seatCd + ""
                    + "office_remark=?,"
                    + "public_remark=? "
                    //+ ",op_dt=current_timestamp "
                    + " where appl_no=? and pur_cd=? and action_cd =?  ";
            ps = tmgr.prepareStatement(sql);

            ps.setString(1, status_dobj.getStatus());
            ps.setString(2, status_dobj.getOffice_remark());
            ps.setString(3, status_dobj.getPublic_remark());
            ps.setString(4, status_dobj.getAppl_no());
            ps.setInt(5, status_dobj.getPur_cd());
            if (Util.getSelectedSeat().getAction_cd() == TableConstants.TM_ROLE_SKIP_FEE) {
                ps.setInt(6, status_dobj.getPrev_action_cd());
            } else {
                ps.setInt(6, Util.getSelectedSeat().getAction_cd());
            }
            int fileMoved = ps.executeUpdate();
            if (fileMoved == 0) {
                //LOGGER.info("File has already Moved for Appl No-" + status_dobj.getAppl_no() + "{AC:" + Util.getSelectedSeat().getAction_cd() + "-PC:" + status_dobj.getPur_cd() + "-S:" + status_dobj.getStatus() + "}");
                throw new VahanException("File has already Moved for Appl No-" + status_dobj.getAppl_no());
            }
            if (status_dobj.getStatus() != null && status_dobj.getStatus().equals(TableConstants.STATUS_HOLD)) {
                sql = "INSERT INTO " + TableList.VHA_STATUS + " "
                        + " SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno, \n"
                        + "  action_cd, seat_cd, cntr_id, status, office_remark, public_remark, \n"
                        + "  file_movement_type, ? , op_dt, current_timestamp, ? "
                        + "  from  " + TableList.VA_STATUS + " where appl_no=? and pur_cd=? ";

                ps = tmgr.prepareStatement(sql);
                ps.setLong(1, Long.parseLong(Util.getEmpCode()));//this should be as status_dobj.getEmp_cd() need to be updated in future...
                ps.setString(2, Util.getClientIpAdress());
                ps.setString(3, status_dobj.getAppl_no());
                ps.setInt(4, status_dobj.getPur_cd());
                validateQueryResult(tmgr, ps.executeUpdate(), ps);

                sql = "update " + TableList.VA_STATUS + " set "
                        + "file_movement_slno=file_movement_slno+1"
                        + " where appl_no=? and pur_cd=? and action_cd =?  ";
                ps = tmgr.prepareStatement(sql);

                ps.setString(1, status_dobj.getAppl_no());
                ps.setInt(2, status_dobj.getPur_cd());
                if (Util.getSelectedSeat().getAction_cd() == TableConstants.TM_ROLE_SKIP_FEE) {
                    ps.setInt(3, status_dobj.getPrev_action_cd());
                } else {
                    ps.setInt(3, Util.getSelectedSeat().getAction_cd());
                }
                validateQueryResult(tmgr, ps.executeUpdate(), ps);

            } else if (status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_COMPLETE)
                    || status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_REVERT)) {

                if (TableConstants.isNextStageWebService) {

                    sql = "  INSERT INTO " + TableList.VHA_STATUS + " "
                            + "  SELECT appl_no, appl_no_map, pur_cd, flow_slno, file_movement_slno,"
                            + "  action_cd, seat_cd, cntr_id, status, office_remark, public_remark,"
                            + "  emp_cd, op_dt, state_cd, rto_code, off_cd "
                            + "  FROM " + TableList.VA_STATUS + " where appl_no=? and pur_cd=?";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, status_dobj.getAppl_no());
                    ps.setInt(2, status_dobj.getPur_cd());
                    ps.executeUpdate();

                    sql = "update " + TableList.VA_STATUS + " "
                            + "set status='N',"
                            + "file_movement_slno=file_movement_slno+1,"
                            + "office_remark='',"
                            + "public_remark='',"
                            + "cntr_id=?,"
                            + "emp_cd=? ,"
                            + "action_cd=?,"
                            + "rto_code=?,"
                            + "flow_slno=?,"
                            + "op_dt=current_timestamp"
                            + " where appl_no=? and pur_cd=?";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, status_dobj.getCntr_id());
                    //Added by Afzal on 12-Jan,2015
                    if ((String) Util.getSession().getAttribute("selected_role_cd") == null) {
                        ps.setInt(2, 0);
                    } else {
                        ps.setLong(2, status_dobj.getEmp_cd());
                    }
                    ps.setInt(3, status_dobj.getAction_cd());
                    ps.setString(4, status_dobj.getRto_code());
                    ps.setInt(5, status_dobj.getFlow_slno());
                    ps.setString(6, status_dobj.getAppl_no());
                    ps.setInt(7, status_dobj.getPur_cd());
                    ps.executeUpdate();

                } else {
                    // To Store Actual,paid,fine amount for future reference -----
                    if (status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_COMPLETE)) {
                        long paid_amt = 0l;
                        long actual_amt = 0l;
                        long total_amt = 0l;
                        long paid_fine = 0l;
                        long actual_fine = 0l;
                        if (status_dobj.getListFeeTaxDifference() != null) {
                            for (EpayDobj epayDobj : status_dobj.getListFeeTaxDifference()) {
                                paid_amt = epayDobj.getE_TaxFee() + epayDobj.getE_FinePenalty();
                                actual_amt = epayDobj.getAct_TaxFee() + epayDobj.getAct_FinePenalty();
                                paid_fine = epayDobj.getE_FinePenalty();
                                actual_fine = epayDobj.getAct_FinePenalty();
                                total_amt = epayDobj.getE_total() - epayDobj.getAct_total();
                                if (total_amt > 0 || total_amt < 0) {
                                    sql = "Insert into " + TableList.VH_FEE_FINE_DIFF_AMT + " (appl_no,state_cd,off_cd, regn_no,pur_cd,rcpt_no,paid_amt,actual_amt,paid_fine,actual_fine,moved_by,moved_on)  VALUES (?,?,?,?,?,?,?,?,?,?,?,current_timestamp) ";
                                    ps = tmgr.prepareStatement(sql);
                                    ps.setString(1, status_dobj.getAppl_no());
                                    ps.setString(2, Util.getUserStateCode());
                                    ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                                    ps.setString(4, status_dobj.getRegn_no());
                                    ps.setInt(5, epayDobj.getPurCd());
                                    if (CommonUtils.isNullOrBlank(epayDobj.getRcpt_no())) {
                                        ps.setString(6, "NA");
                                    } else {
                                        ps.setString(6, epayDobj.getRcpt_no());
                                    }
                                    ps.setLong(7, paid_amt);
                                    ps.setLong(8, actual_amt);
                                    ps.setLong(9, paid_fine);
                                    ps.setLong(10, actual_fine);
                                    ps.setString(11, Util.getEmpCode());
                                    ps.executeUpdate();
                                }
                            }
                        }
                    }
                    //----
                    // If (forward or backward) execute this
                    sql = "INSERT INTO " + TableList.VHA_STATUS + " "
                            + " SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno, \n"
                            + "  action_cd, seat_cd, cntr_id, status, office_remark, public_remark, \n"
                            + "  file_movement_type, ? , op_dt, current_timestamp, ? "
                            + "  from  " + TableList.VA_STATUS + " where appl_no=? and pur_cd=? ";

                    ps = tmgr.prepareStatement(sql);
                    ps.setLong(1, Long.parseLong(Util.getEmpCode()));//this should be as status_dobj.getEmp_cd() need to be updated in future...
                    ps.setString(2, Util.getClientIpAdress());
                    ps.setString(3, status_dobj.getAppl_no());
                    ps.setInt(4, status_dobj.getPur_cd());
                    int vhaStatusUpdated = ps.executeUpdate();
                    // end
                    int vaStatusUpdated = 0;
                    if ((status_dobj.getEntry_status() == null || status_dobj.getEntry_status().equals("")) && status_dobj.getAction_cd() > 0) {
                        if (status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_REVERT) && CommonUtils.isNullOrBlank(status_dobj.getSeat_cd())) {
                            updateSeatCd = "seat_cd = action_cd || ',' || flow_slno || ',' || '" + status_dobj.getStatus() + "',";
                        } else {
                            updateSeatCd = "seat_cd = '" + status_dobj.getStatus() + "',";
                        }
                        sql = "update " + TableList.VA_STATUS + " "
                                + "set status='N',"
                                + "file_movement_slno=file_movement_slno+1,"
                                + "office_remark='',"
                                + "public_remark='',"
                                + "cntr_id=?,"
                                + "emp_cd=? ,"
                                + "action_cd=?,"
                                + "flow_slno=?,"
                                + " " + updateSeatCd + " "
                                + "op_dt=current_timestamp"
                                + " where appl_no=? and pur_cd=?";

                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, status_dobj.getCntr_id());
                        ps.setLong(2, status_dobj.getEmp_cd());
                        ps.setInt(3, status_dobj.getAction_cd());
                        ps.setInt(4, status_dobj.getFlow_slno());
                        ps.setString(5, status_dobj.getAppl_no());
                        ps.setInt(6, status_dobj.getPur_cd());
                        vaStatusUpdated = ps.executeUpdate();

                        if (Util.getTmConfiguration() != null && Util.getTmConfiguration().isAllowFacelessService()) {
                            if (status_dobj.getAppl_no() != null && Util.getSelectedSeat() != null && (Util.getSelectedSeat().getOff_cd() != 0 || (Util.getUserCategory().equals(TableConstants.USER_CATG_STATE_ADMIN) && status_dobj.getPur_cd() == TableConstants.STATEADMIN_OWNER_DATA_CHANGE))) {
                                ServerUtility.updateRtoOpenDateInVtFaceLessService(tmgr, status_dobj.getAppl_no(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                            } else {
                                throw new Exception("Problem in getting office code/applNo");
                            }
                        }
                    } else if (((status_dobj.getAction_cd() == 0 && !status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_REVERT)) //it must execute based at the time of Approval
                            || (status_dobj.getAction_cd() != 0 && status_dobj.getStatus().equals(TableConstants.STATUS_COMPLETE)))
                            && (status_dobj.getEntry_status() != null && status_dobj.getEntry_status().equalsIgnoreCase(TableConstants.STATUS_APPROVED))) {
                        TmConfigurationDobj tmConfigDobj = Util.getTmConfiguration();
                        if (tmConfigDobj != null && tmConfigDobj.isDefacement()) {
                            DefacementImpl defaceImpl = new DefacementImpl();
                            DefacementDobj defacementDobj = defaceImpl.getApplPaymentDetails(tmgr, status_dobj.getAppl_no(), Util.getUserStateCode());
                            if (defacementDobj != null) {
                                defacementDobj.setMerchant_code("5003");
                                defacementDobj.setRefrence_no(status_dobj.getAppl_no());
                                defacementDobj.setState_cd(Util.getUserStateCode());
                                defacementDobj.setOff_cd(Util.getSelectedSeat().getOff_cd());
                                defaceImpl.insertIntoDefacement(tmgr, defacementDobj, status_dobj.getRegn_no());
                            }
                        }
                        sql = "Delete From " + TableList.VA_STATUS + " WHERE appl_no=? and pur_cd=?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, status_dobj.getAppl_no());
                        ps.setInt(2, status_dobj.getPur_cd());
                        vaStatusUpdated = ps.executeUpdate();
                        ApplicationInwardImpl applicationInwardImpl = new ApplicationInwardImpl();
                        boolean isOnlineAppl = applicationInwardImpl.isOnlineApplication(status_dobj.getAppl_no(), status_dobj.getPur_cd());
                        if (isOnlineAppl) {

                            applicationInwardImpl.updateApprovedStatusForOnlineAppl(tmgr, status_dobj);

                            sql = "INSERT INTO " + TableList.VHA_STATUS_APPL
                                    + " SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno,"
                                    + "       action_cd, seat_cd, cntr_id, status, office_remark, public_remark,"
                                    + "       file_movement_type, ? as emp_cd, op_dt, moved_from_online, current_timestamp as moved_on"
                                    + "  FROM " + TableList.VA_STATUS_APPL + " where appl_no=? and pur_cd=? ";
                            ps = tmgr.prepareStatement(sql);
                            ps.setLong(1, Long.parseLong(Util.getEmpCode()));
                            ps.setString(2, status_dobj.getAppl_no());
                            ps.setInt(3, status_dobj.getPur_cd());
                            ps.executeUpdate();

                            sql = "Delete FROM " + TableList.VA_STATUS_APPL + " WHERE appl_no=? and pur_cd=?";
                            ps = tmgr.prepareStatement(sql);
                            ps.setString(1, status_dobj.getAppl_no());
                            ps.setInt(2, status_dobj.getPur_cd());
                            ps.executeUpdate();
                        }
                    }

                    if (vhaStatusUpdated == 0 || (vaStatusUpdated == 0 && (status_dobj.getPur_cd() != TableConstants.VM_TRANSACTION_MAST_HPC && status_dobj.getPur_cd() != TableConstants.VM_DUPLICATE_TO_TAX_CARD))) {
                        LOGGER.info("File-Flow-" + status_dobj.getAppl_no() + "-" + status_dobj.getPur_cd() + "-" + status_dobj.getAction_cd() + "-" + status_dobj.getStatus() + "-" + status_dobj.getEntry_status());
                        throw new VahanException("Problem in File Movement for Appl No-" + status_dobj.getAppl_no() + ",Please go to home page and try again.");
                    }
                    if (status_dobj.getAction_cd() == 0 && status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_REVERT)) {
                        throw new VahanException("Please Press Button Only Once for Appl No-" + status_dobj.getAppl_no());
                    }
                }

            }
        } catch (VahanException ve) {
            throw ve;
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            LOGGER.info("File-Flow-" + status_dobj.getAppl_no() + "-" + status_dobj.getPur_cd() + "-" + status_dobj.getAction_cd() + "-" + status_dobj.getStatus() + "-" + status_dobj.getEntry_status());
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    /*
     * @author Kartikey Singh
     * Changed HttpSession references their variable counterparts
     * boolean variables have been fetched from tmConfigDobj in REST Controller
     */
    public static void fileFlow(TransactionManager tmgr, Status_dobj status_dobj, int actionCode, String selectedRoleCode,
            String userStateCode, int offCode, String empCode, String userCategory, String clientIpAddress,
            boolean allowFacelessService, boolean defacement) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        String updateSeatCd = "";
        String seatCd = "";
        try {
            if (status_dobj.getStatus() != null && status_dobj.getStatus().equals(TableConstants.STATUS_HOLD)) {
                seatCd = "seat_cd = '" + status_dobj.getStatus() + "',";
            }
            sql = "update " + TableList.VA_STATUS + " set "
                    + "status = ?,"
                    + "" + seatCd + ""
                    + "office_remark=?,"
                    + "public_remark=? "
                    //+ ",op_dt=current_timestamp "
                    + " where appl_no=? and pur_cd=? and action_cd =?  ";
            ps = tmgr.prepareStatement(sql);

            ps.setString(1, status_dobj.getStatus());
            ps.setString(2, status_dobj.getOffice_remark());
            ps.setString(3, status_dobj.getPublic_remark());
            ps.setString(4, status_dobj.getAppl_no());
            ps.setInt(5, status_dobj.getPur_cd());
            if (actionCode == TableConstants.TM_ROLE_SKIP_FEE) {
                ps.setInt(6, status_dobj.getPrev_action_cd());
            } else {
                ps.setInt(6, actionCode);
            }
            int fileMoved = ps.executeUpdate();
            if (fileMoved == 0) {
                //LOGGER.info("File has already Moved for Appl No-" + status_dobj.getAppl_no() + "{AC:" + Util.getSelectedSeat().getAction_cd() + "-PC:" + status_dobj.getPur_cd() + "-S:" + status_dobj.getStatus() + "}");
                throw new VahanException("File has already Moved for Appl No-" + status_dobj.getAppl_no());
            }
            if (status_dobj.getStatus() != null && status_dobj.getStatus().equals(TableConstants.STATUS_HOLD)) {
                sql = "INSERT INTO " + TableList.VHA_STATUS + " "
                        + " SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno, \n"
                        + "  action_cd, seat_cd, cntr_id, status, office_remark, public_remark, \n"
                        + "  file_movement_type, ? , op_dt, current_timestamp, ? "
                        + "  from  " + TableList.VA_STATUS + " where appl_no=? and pur_cd=? ";

                ps = tmgr.prepareStatement(sql);
                ps.setLong(1, Long.parseLong(empCode));//this should be as status_dobj.getEmp_cd() need to be updated in future...
                ps.setString(2, clientIpAddress);
                ps.setString(3, status_dobj.getAppl_no());
                ps.setInt(4, status_dobj.getPur_cd());
                validateQueryResult(tmgr, ps.executeUpdate(), ps);

                sql = "update " + TableList.VA_STATUS + " set "
                        + "file_movement_slno=file_movement_slno+1"
                        + " where appl_no=? and pur_cd=? and action_cd =?  ";
                ps = tmgr.prepareStatement(sql);

                ps.setString(1, status_dobj.getAppl_no());
                ps.setInt(2, status_dobj.getPur_cd());
                if (actionCode == TableConstants.TM_ROLE_SKIP_FEE) {
                    ps.setInt(3, status_dobj.getPrev_action_cd());
                } else {
                    ps.setInt(3, actionCode);
                }
                validateQueryResult(tmgr, ps.executeUpdate(), ps);

            } else if (status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_COMPLETE)
                    || status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_REVERT)) {

                if (TableConstants.isNextStageWebService) {

                    sql = "  INSERT INTO " + TableList.VHA_STATUS + " "
                            + "  SELECT appl_no, appl_no_map, pur_cd, flow_slno, file_movement_slno,"
                            + "  action_cd, seat_cd, cntr_id, status, office_remark, public_remark,"
                            + "  emp_cd, op_dt, state_cd, rto_code, off_cd "
                            + "  FROM " + TableList.VA_STATUS + " where appl_no=? and pur_cd=?";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, status_dobj.getAppl_no());
                    ps.setInt(2, status_dobj.getPur_cd());
                    ps.executeUpdate();

                    sql = "update " + TableList.VA_STATUS + " "
                            + "set status='N',"
                            + "file_movement_slno=file_movement_slno+1,"
                            + "office_remark='',"
                            + "public_remark='',"
                            + "cntr_id=?,"
                            + "emp_cd=? ,"
                            + "action_cd=?,"
                            + "rto_code=?,"
                            + "flow_slno=?,"
                            + "op_dt=current_timestamp"
                            + " where appl_no=? and pur_cd=?";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, status_dobj.getCntr_id());
                    //Added by Afzal on 12-Jan,2015
                    if (selectedRoleCode == null) {
                        ps.setInt(2, 0);
                    } else {
                        ps.setLong(2, status_dobj.getEmp_cd());
                    }
                    ps.setInt(3, status_dobj.getAction_cd());
                    ps.setString(4, status_dobj.getRto_code());
                    ps.setInt(5, status_dobj.getFlow_slno());
                    ps.setString(6, status_dobj.getAppl_no());
                    ps.setInt(7, status_dobj.getPur_cd());
                    ps.executeUpdate();

                } else {
                    // To Store Actual,paid,fine amount for future reference -----
                    if (status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_COMPLETE)) {
                        long paid_amt = 0l;
                        long actual_amt = 0l;
                        long total_amt = 0l;
                        long paid_fine = 0l;
                        long actual_fine = 0l;
                        if (status_dobj.getListFeeTaxDifference() != null) {
                            for (EpayDobj epayDobj : status_dobj.getListFeeTaxDifference()) {
                                paid_amt = epayDobj.getE_TaxFee() + epayDobj.getE_FinePenalty();
                                actual_amt = epayDobj.getAct_TaxFee() + epayDobj.getAct_FinePenalty();
                                paid_fine = epayDobj.getE_FinePenalty();
                                actual_fine = epayDobj.getAct_FinePenalty();
                                total_amt = epayDobj.getE_total() - epayDobj.getAct_total();
                                if (total_amt > 0 || total_amt < 0) {
                                    sql = "Insert into " + TableList.VH_FEE_FINE_DIFF_AMT + " (appl_no,state_cd,off_cd, regn_no,pur_cd,rcpt_no,paid_amt,actual_amt,paid_fine,actual_fine,moved_by,moved_on)  VALUES (?,?,?,?,?,?,?,?,?,?,?,current_timestamp) ";
                                    ps = tmgr.prepareStatement(sql);
                                    ps.setString(1, status_dobj.getAppl_no());
                                    ps.setString(2, userStateCode);
                                    ps.setInt(3, offCode);
                                    ps.setString(4, status_dobj.getRegn_no());
                                    ps.setInt(5, epayDobj.getPurCd());
                                    if (CommonUtils.isNullOrBlank(epayDobj.getRcpt_no())) {
                                        ps.setString(6, "NA");
                                    } else {
                                        ps.setString(6, epayDobj.getRcpt_no());
                                    }
                                    ps.setLong(7, paid_amt);
                                    ps.setLong(8, actual_amt);
                                    ps.setLong(9, paid_fine);
                                    ps.setLong(10, actual_fine);
                                    ps.setString(11, empCode);
                                    ps.executeUpdate();
                                }
                            }
                        }
                    }
                    //----
                    // If (forward or backward) execute this
                    sql = "INSERT INTO " + TableList.VHA_STATUS + " "
                            + " SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno, \n"
                            + "  action_cd, seat_cd, cntr_id, status, office_remark, public_remark, \n"
                            + "  file_movement_type, ? , op_dt, current_timestamp, ? "
                            + "  from  " + TableList.VA_STATUS + " where appl_no=? and pur_cd=? ";

                    ps = tmgr.prepareStatement(sql);
                    ps.setLong(1, Long.parseLong(empCode));//this should be as status_dobj.getEmp_cd() need to be updated in future...
                    ps.setString(2, clientIpAddress);
                    ps.setString(3, status_dobj.getAppl_no());
                    ps.setInt(4, status_dobj.getPur_cd());
                    int vhaStatusUpdated = ps.executeUpdate();
                    // end
                    int vaStatusUpdated = 0;
                    if ((status_dobj.getEntry_status() == null || status_dobj.getEntry_status().equals("")) && status_dobj.getAction_cd() > 0) {
                        if (status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_REVERT) && CommonUtils.isNullOrBlank(status_dobj.getSeat_cd())) {
                            updateSeatCd = "seat_cd = action_cd || ',' || flow_slno || ',' || '" + status_dobj.getStatus() + "',";
                        } else {
                            updateSeatCd = "seat_cd = '" + status_dobj.getStatus() + "',";
                        }
                        sql = "update " + TableList.VA_STATUS + " "
                                + "set status='N',"
                                + "file_movement_slno=file_movement_slno+1,"
                                + "office_remark='',"
                                + "public_remark='',"
                                + "cntr_id=?,"
                                + "emp_cd=? ,"
                                + "action_cd=?,"
                                + "flow_slno=?,"
                                + " " + updateSeatCd + " "
                                + "op_dt=current_timestamp"
                                + " where appl_no=? and pur_cd=?";

                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, status_dobj.getCntr_id());
                        ps.setLong(2, status_dobj.getEmp_cd());
                        ps.setInt(3, status_dobj.getAction_cd());
                        ps.setInt(4, status_dobj.getFlow_slno());
                        ps.setString(5, status_dobj.getAppl_no());
                        ps.setInt(6, status_dobj.getPur_cd());
                        vaStatusUpdated = ps.executeUpdate();

                        if (allowFacelessService) {
                            if (status_dobj.getAppl_no() != null && (offCode != 0 || (userCategory.equals(TableConstants.USER_CATG_STATE_ADMIN) && status_dobj.getPur_cd() == TableConstants.STATEADMIN_OWNER_DATA_CHANGE))) {
                                ServerUtility.updateRtoOpenDateInVtFaceLessService(tmgr, status_dobj.getAppl_no(), userStateCode, offCode);
                            } else {
                                throw new Exception("Problem in getting office code/applNo");
                            }
                        }
                    } else if (((status_dobj.getAction_cd() == 0 && !status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_REVERT)) //it must execute based at the time of Approval
                            || (status_dobj.getAction_cd() != 0 && status_dobj.getStatus().equals(TableConstants.STATUS_COMPLETE)))
                            && (status_dobj.getEntry_status() != null && status_dobj.getEntry_status().equalsIgnoreCase(TableConstants.STATUS_APPROVED))) {

                        if (defacement) {
                            DefacementImplementation defaceImpl = new DefacementImplementation();
                            DefacementDobj defacementDobj = defaceImpl.getApplPaymentDetails(tmgr, status_dobj.getAppl_no(), userStateCode);
                            if (defacementDobj != null) {
                                defacementDobj.setMerchant_code("5003");
                                defacementDobj.setRefrence_no(status_dobj.getAppl_no());
                                defacementDobj.setState_cd(userStateCode);
                                defacementDobj.setOff_cd(offCode);
                                defaceImpl.insertIntoDefacement(tmgr, defacementDobj, status_dobj.getRegn_no(), userStateCode, offCode, empCode);
                            }
                        }
                        sql = "Delete From " + TableList.VA_STATUS + " WHERE appl_no=? and pur_cd=?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, status_dobj.getAppl_no());
                        ps.setInt(2, status_dobj.getPur_cd());
                        vaStatusUpdated = ps.executeUpdate();
                        ApplicationInwardImplementation applicationInwardImpl = new ApplicationInwardImplementation();
                        boolean isOnlineAppl = applicationInwardImpl.isOnlineApplication(status_dobj.getAppl_no(), status_dobj.getPur_cd());
                        if (isOnlineAppl) {

                            applicationInwardImpl.updateApprovedStatusForOnlineAppl(tmgr, status_dobj, clientIpAddress);

                            sql = "INSERT INTO " + TableList.VHA_STATUS_APPL
                                    + " SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno,"
                                    + "       action_cd, seat_cd, cntr_id, status, office_remark, public_remark,"
                                    + "       file_movement_type, ? as emp_cd, op_dt, moved_from_online, current_timestamp as moved_on"
                                    + "  FROM " + TableList.VA_STATUS_APPL + " where appl_no=? and pur_cd=? ";
                            ps = tmgr.prepareStatement(sql);
                            ps.setLong(1, Long.parseLong(empCode));
                            ps.setString(2, status_dobj.getAppl_no());
                            ps.setInt(3, status_dobj.getPur_cd());
                            ps.executeUpdate();

                            sql = "Delete FROM " + TableList.VA_STATUS_APPL + " WHERE appl_no=? and pur_cd=?";
                            ps = tmgr.prepareStatement(sql);
                            ps.setString(1, status_dobj.getAppl_no());
                            ps.setInt(2, status_dobj.getPur_cd());
                            ps.executeUpdate();
                        }
                    }

                    if (vhaStatusUpdated == 0 || (vaStatusUpdated == 0 && (status_dobj.getPur_cd() != TableConstants.VM_TRANSACTION_MAST_HPC && status_dobj.getPur_cd() != TableConstants.VM_DUPLICATE_TO_TAX_CARD))) {
                        LOGGER.info("File-Flow-" + status_dobj.getAppl_no() + "-" + status_dobj.getPur_cd() + "-" + status_dobj.getAction_cd() + "-" + status_dobj.getStatus() + "-" + status_dobj.getEntry_status());
                        throw new VahanException("Problem in File Movement for Appl No-" + status_dobj.getAppl_no() + ",Please go to home page and try again.");
                    }
                    if (status_dobj.getAction_cd() == 0 && status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_REVERT)) {
                        throw new VahanException("Please Press Button Only Once for Appl No-" + status_dobj.getAppl_no());
                    }
                }

            }
        } catch (VahanException ve) {
            throw ve;
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            LOGGER.info("File-Flow-" + status_dobj.getAppl_no() + "-" + status_dobj.getPur_cd() + "-" + status_dobj.getAction_cd() + "-" + status_dobj.getStatus() + "-" + status_dobj.getEntry_status());
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    //For Tracing TN problem
    public static void fileFlowForPermitOfferVerification(TransactionManager tmgr, Status_dobj status_dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "update " + TableList.VA_STATUS + " set "
                    + "status = ?,"
                    + "office_remark=?,"
                    + "public_remark=? "
                    + " where appl_no=? and pur_cd=? and action_cd =?  ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, status_dobj.getStatus());
            ps.setString(2, status_dobj.getOffice_remark());
            ps.setString(3, status_dobj.getPublic_remark());
            ps.setString(4, status_dobj.getAppl_no());
            ps.setInt(5, status_dobj.getPur_cd());
            ps.setInt(6, Util.getSelectedSeat().getAction_cd());
            int fileMoved = ps.executeUpdate();
            if (fileMoved == 0) {
                throw new VahanException("File has already Moved for Appl No-" + status_dobj.getAppl_no());
            }

            if (status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_COMPLETE)
                    || status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_REVERT)) {
                sql = "INSERT INTO " + TableList.VHA_STATUS + " "
                        + " SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno, \n"
                        + "  action_cd, seat_cd, cntr_id, status, office_remark, public_remark, \n"
                        + "  file_movement_type, ? , op_dt, current_timestamp, ? "
                        + "  from  " + TableList.VA_STATUS + " where appl_no=? and pur_cd=? ";

                ps = tmgr.prepareStatement(sql);
                ps.setLong(1, Long.parseLong(Util.getEmpCode()));//this should be as status_dobj.getEmp_cd() need to be updated in future...
                ps.setString(2, Util.getClientIpAdress());
                ps.setString(3, status_dobj.getAppl_no());
                ps.setInt(4, status_dobj.getPur_cd());
                int vhaStatusUpdated = ps.executeUpdate();

                sql = "update " + TableList.VA_STATUS + " "
                        + "set status='N',"
                        + "file_movement_slno=file_movement_slno+1,"
                        + "office_remark='',"
                        + "public_remark='',"
                        + "cntr_id=?,"
                        + "emp_cd=? ,"
                        + "action_cd=?,"
                        + "off_cd=?,"
                        + "flow_slno=?,"
                        + "op_dt=current_timestamp"
                        + " where appl_no=? and pur_cd=?";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, status_dobj.getCntr_id());
                if ((String) Util.getSession().getAttribute("selected_role_cd") == null) {
                    ps.setInt(2, 0);
                } else {
                    ps.setLong(2, status_dobj.getEmp_cd());
                }
                ps.setInt(3, status_dobj.getAction_cd());
                ps.setInt(4, status_dobj.getOff_cd());
                ps.setInt(5, status_dobj.getFlow_slno());
                ps.setString(6, status_dobj.getAppl_no());
                ps.setInt(7, status_dobj.getPur_cd());
                ps.executeUpdate();
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            LOGGER.info("File-Flow-" + status_dobj.getAppl_no() + "-" + status_dobj.getPur_cd() + "-" + status_dobj.getAction_cd() + "-" + status_dobj.getStatus() + "-" + status_dobj.getEntry_status());
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }

    }

    public static void insertIntoVhaChangedData(TransactionManager tmgr, String appl_no, String changedData) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;

        try {
            //for saving the data into table those are changed by the user
            if (changedData != null && !changedData.equals("")) {
                sql = "INSERT INTO VHA_CHANGED_DATA (APPL_NO,EMP_CD,CHANGED_DATA,OP_DT,STATE_CD,OFF_CD) "
                        + " VALUES(?,?,?,CURRENT_TIMESTAMP,?,?)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, appl_no);
                ps.setLong(2, Long.parseLong(Util.getEmpCode()));
                if (changedData.length() <= 2000) {
                    ps.setString(3, changedData);
                } else {
                    ps.setString(3, changedData.substring(0, 2000));
                }
                ps.setString(4, Util.getUserStateCode());
                ps.setInt(5, Util.getSelectedSeat().getOff_cd());
                ps.executeUpdate();

                if (Util.getTmConfiguration() != null && Util.getTmConfiguration().isAllowFacelessService()) {
                    if (appl_no != null && Util.getSelectedSeat() != null && (Util.getSelectedSeat().getOff_cd() != 0 || Util.getUserCategory().equals(TableConstants.USER_CATG_STATE_ADMIN))) {
                        ServerUtility.updateRtoOpenDateInVtFaceLessService(tmgr, appl_no, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                    } else {
                        throw new Exception("Problem in getting office code/applNo");
                    }
                }
            }
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during Saving Changed Data by User, Please Contact to the System Administrator.");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during Saving Changed Data by User, Please Contact to the System Administrator.");
        }

    }

    /*
     * Author: Kartikey Singh
     */
    public static void insertIntoVhaChangedData(TransactionManager tmgr, String appl_no, String changedData, String empCode,
            String userStateCode, int officeCode, boolean allowFacelessService, String userCategory) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;

        try {
            //for saving the data into table those are changed by the user
            if (changedData != null && !changedData.equals("")) {
                sql = "INSERT INTO VHA_CHANGED_DATA (APPL_NO,EMP_CD,CHANGED_DATA,OP_DT,STATE_CD,OFF_CD) "
                        + " VALUES(?,?,?,CURRENT_TIMESTAMP,?,?)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, appl_no);
                ps.setLong(2, Long.parseLong(empCode));
                if (changedData.length() <= 2000) {
                    ps.setString(3, changedData);
                } else {
                    ps.setString(3, changedData.substring(0, 2000));
                }
                ps.setString(4, userStateCode);
                ps.setInt(5, officeCode);
                ps.executeUpdate();

                if (allowFacelessService) {
                    if (appl_no != null && (officeCode != 0 || userCategory.equals(TableConstants.USER_CATG_STATE_ADMIN))) {
                        ServerUtility.updateRtoOpenDateInVtFaceLessService(tmgr, appl_no, userStateCode, officeCode);
                    } else {
                        throw new Exception("Problem in getting office code/applNo");
                    }
                }
            }
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during Saving Changed Data by User, Please Contact to the System Administrator.");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during Saving Changed Data by User, Please Contact to the System Administrator.");
        }

    }

    public static Map<String, Object> getPrevRole(int current_action_cd, int pur_cd, String state_cd) throws Exception {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        Map<String, Object> roleLabelValue = new LinkedHashMap<>();

        try {
            tmgr = new TransactionManagerReadOnly("getPrevRole()");

            sql = "select action_cd,action_abbrv from " + TableList.view_purpose_action_flow + " where flow_srno < (select flow_srno from " + TableList.TM_PURPOSE_ACTION_FLOW + " where pur_cd=? and state_cd=? and action_cd = ? limit 1) and pur_cd=?  and state_cd = ? and isbackward='Y'";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, pur_cd);
            ps.setString(2, state_cd);
            ps.setInt(3, current_action_cd);
            ps.setInt(4, pur_cd);
            ps.setString(5, state_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next())//found
            {
                roleLabelValue.put(rs.getString("action_abbrv"), rs.getString("action_cd"));
            }

        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return roleLabelValue;
    }

    public static Map<String, Object> getTaxModeList() {
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        Map<String, Object> taxModeLabelValue = new LinkedHashMap<String, Object>();

        try {
            tmgr = new TransactionManagerReadOnly("getTaxMode()");

            sql = "select * from vm_tax_mode";
            tmgr.prepareStatement(sql);

            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next())//found
            {
                taxModeLabelValue.put(rs.getString("descr"), rs.getString("tax_mode")); //label,value
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return taxModeLabelValue;
    }

    public static Map<String, Object> vehicleClassList(int class_type) {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        Map<String, Object> taxOnVehicleLabelValue = new LinkedHashMap<String, Object>();

        try {
            tmgr = new TransactionManagerReadOnly("vehicleClassList");

            sql = "select vh_class,descr from vm_vh_class where class_type=? order by descr";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, class_type);

            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next())//found
            {
                taxOnVehicleLabelValue.put(rs.getString("descr"), rs.getString("vh_class")); //label,value
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return taxOnVehicleLabelValue;
    }

    public static void webServiceForNextStage(Status_dobj status, String newStatus, String cntr_id,
            String appl_no, int action_cd, int purcd, ApproveImpl approveImpl) throws VahanException {

        NextStageRequest request = new NextStageRequest();
        request.setCntr_id(cntr_id);
        request.setAppl_no(appl_no);
        request.setEmp_cd(Long.parseLong(Util.getEmpCode().trim())); // getting from session
        int prevAction = 0;

        TransactionManager tmgr = null;

        try {
            if (status.getStatus().equalsIgnoreCase(TableConstants.STATUS_REVERT)) {
                prevAction = Integer.parseInt(approveImpl.getPrevAction().getValue().toString());//for selectng radiobutton
                request.setFile_movement_type(TableConstants.BACKWARD);
                request.setAction_cd(prevAction);
            } else {
                request.setFile_movement_type(TableConstants.FORWARD);
                request.setAction_cd(action_cd);
            }

            request.setPur_cd(purcd);
            request.setState_cd(Util.getUserStateCode()); //getting from session
            request.setOff_cd(Util.getUserSeatOffCode()); // getting from session
            NextStageResponse response = null;

            if (TableConstants.isNextStageWebService) {

                response = NextStageWS.getNextStageResponse(request);
                status.setCntr_id(response.getCntr_id());
                status.setAction_cd(response.getAction_cd());
                status.setOff_cd(response.getOff_cd());
                status.setEmp_cd(response.getEmp_cd());
                status.setFlow_slno(response.getFlow_slno());
                status.setRto_code(response.getRto_code());

            } else {

                String sql = null;
                tmgr = new TransactionManager("webServiceForNextStage");
                PreparedStatement ps = null;
                RowSet rs = null;

                if (request.getFile_movement_type().equals(TableConstants.BACKWARD)) {

                    sql = "select a.flow_srno, a.action_cd\n"
                            + " from tm_purpose_action_flow a\n"
                            + " where a.state_cd = ? and a.action_cd = ? and a.pur_cd = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, request.getState_cd());
                    ps.setInt(2, request.getAction_cd());
                    ps.setInt(3, request.getPur_cd());
                    rs = tmgr.fetchDetachedRowSet_No_release();

                    if (rs.next()) {
                        status.setAction_cd(prevAction);
                        status.setOff_cd(request.getOff_cd());
                        status.setFlow_slno(rs.getInt("flow_srno"));
                        status.setRto_code(request.getRto_code());
                    }

                } else {
                    int cntr = 1;
                    while (true) {

                        sql = "select a.flow_srno, a.action_cd,a.condition_formula,seat_cd  "
                                + " from tm_purpose_action_flow a, va_status b "
                                + " where a.state_cd = b.state_cd and a.pur_cd = b.pur_cd and a.state_cd = ? "
                                + " and b.appl_no = ? and a.flow_srno = b.flow_slno + ? and a.pur_cd =?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, request.getState_cd());
                        ps.setString(2, request.getAppl_no());
                        ps.setInt(3, cntr);
                        ps.setInt(4, purcd);

                        rs = tmgr.fetchDetachedRowSet_No_release();

                        if (rs.next()) {
                            if (!CommonUtils.isNullOrBlank(rs.getString("seat_cd")) && rs.getString("seat_cd").trim().split(",").length > 1) {
                                String[] actionCdWithFileFlow = rs.getString("seat_cd").trim().split(",");
                                status.setAction_cd(Integer.parseInt(actionCdWithFileFlow[0]));
                                status.setOff_cd(request.getOff_cd());
                                status.setFlow_slno(Integer.parseInt(actionCdWithFileFlow[1]));
                                status.setRto_code(request.getRto_code());
                                break;
                            } else if (status.getVehicleParameters() == null || isCondition(replaceTagValues(rs.getString("condition_formula"), status.getVehicleParameters()), "webServiceForNextStage")) {
                                status.setAction_cd(rs.getInt("action_cd"));
                                status.setOff_cd(request.getOff_cd());
                                status.setFlow_slno(rs.getInt("flow_srno"));
                                status.setRto_code(request.getRto_code());
                                break;
                            } else {
                                cntr++;
                            }
                        } else {
                            status.setAction_cd(0);
                            status.setFlow_slno(0);
                            break;
                        }

                    }
                }
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error occured during getting next file flow steps.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

    }//end of webServiceForNextStage

    /*
     * author: Ramesh 
     * Changed HttpSession references their variable counterparts
     * by passing appl_no,state_cd and emp_cd from SessionVariables
     */
    public static Status_dobj webServiceForNextStage(Status_dobj status, TransactionManager tmgr, String emp_cd, int off_cd, String state_cd) throws VahanException {

        NextStageRequest request = new NextStageRequest();
        request.setCntr_id(status.getCntr_id());
        request.setAppl_no(status.getAppl_no());
        request.setEmp_cd(Long.parseLong(emp_cd.trim())); // getting from session
        int prevAction = 0;

        try {
            if (status.getStatus().equalsIgnoreCase(TableConstants.STATUS_REVERT)) {
                request.setFile_movement_type(TableConstants.BACKWARD);
                request.setAction_cd(status.getPrev_action_cd_selected());
                prevAction = status.getPrev_action_cd_selected();
            } else {
                request.setFile_movement_type(TableConstants.FORWARD);
                request.setAction_cd(status.getAction_cd());
            }

            request.setPur_cd(status.getPur_cd());
            request.setState_cd(state_cd); //getting from session
            request.setOff_cd(off_cd); // getting from session
            NextStageResponse response = null;

            if (TableConstants.isNextStageWebService) {

                response = NextStageWS.getNextStageResponse(request);
                status.setCntr_id(response.getCntr_id());
                status.setAction_cd(response.getAction_cd());
                status.setOff_cd(response.getOff_cd());
                status.setEmp_cd(response.getEmp_cd());
                status.setFlow_slno(response.getFlow_slno());
                status.setRto_code(response.getRto_code());

            } else {

                String sql = null;
                PreparedStatement ps = null;
                RowSet rs = null;

                if (request.getFile_movement_type().equals(TableConstants.BACKWARD)) {

                    sql = "select a.flow_srno, a.action_cd\n"
                            + " from tm_purpose_action_flow a\n"
                            + " where a.state_cd = ? and a.action_cd = ? and a.pur_cd = ? "
                            + " and a.flow_srno < (select flow_slno from va_status where appl_no=? and pur_cd = ? and state_cd = ?) ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, request.getState_cd());
                    ps.setInt(2, request.getAction_cd());
                    ps.setInt(3, request.getPur_cd());
                    ps.setString(4, status.getAppl_no());
                    ps.setInt(5, request.getPur_cd());
                    ps.setString(6, request.getState_cd());
                    rs = tmgr.fetchDetachedRowSet_No_release();

                    if (rs.next()) {
                        status.setAction_cd(prevAction);
                        status.setOff_cd(request.getOff_cd());
                        status.setFlow_slno(rs.getInt("flow_srno"));
                        status.setRto_code(request.getRto_code());
                    } else if (request.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD && prevAction == TableConstants.TM_ROLE_PMT_APPLICATION_ENTRY) {
                        sql = "select a.flow_srno, a.action_cd\n"
                                + " from tm_purpose_action_flow a\n"
                                + " where a.state_cd = ? and a.action_cd = ? and a.pur_cd = ? "
                                + " and a.flow_srno in (select (flow_slno-1) from va_status where appl_no= ? and file_movement_slno=1   order by  file_movement_slno desc limit 1 ) ";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, request.getState_cd());
                        ps.setInt(2, request.getAction_cd());
                        ps.setInt(3, request.getPur_cd());
                        ps.setString(4, status.getAppl_no());
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        if (rs.next()) {
                            status.setAction_cd(prevAction);
                            status.setOff_cd(request.getOff_cd());
                            status.setFlow_slno(rs.getInt("flow_srno"));
                            status.setRto_code(request.getRto_code());
                        }
                    }
                } else {
                    int cntr = 1;
                    while (true) {
                        sql = "select a.flow_srno, a.action_cd,a.condition_formula,seat_cd "
                                + " from tm_purpose_action_flow a, va_status b "
                                + " where a.state_cd = b.state_cd and a.pur_cd = b.pur_cd and a.state_cd = ? "
                                + " and b.appl_no = ? and a.flow_srno = b.flow_slno + ? and a.pur_cd =?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, request.getState_cd());
                        ps.setString(2, request.getAppl_no());
                        ps.setInt(3, cntr);
                        ps.setInt(4, status.getPur_cd());

                        rs = tmgr.fetchDetachedRowSet_No_release();

                        if (rs.next()) {
                            if (!CommonUtils.isNullOrBlank(rs.getString("seat_cd")) && rs.getString("seat_cd").trim().split(",").length > 1) {
                                String[] actionCdWithFileFlow = rs.getString("seat_cd").trim().split(",");
                                status.setAction_cd(Integer.parseInt(actionCdWithFileFlow[0]));
                                status.setOff_cd(request.getOff_cd());
                                status.setFlow_slno(Integer.parseInt(actionCdWithFileFlow[1]));
                                status.setRto_code(request.getRto_code());
                                break;
                            } else if (status.getVehicleParameters() == null || isCondition(replaceTagValues(rs.getString("condition_formula"), status.getVehicleParameters()), "webServiceForNextStage-2")) {
                                status.setAction_cd(rs.getInt("action_cd"));
                                status.setOff_cd(request.getOff_cd());
                                status.setFlow_slno(rs.getInt("flow_srno"));
                                status.setRto_code(request.getRto_code());
                                break;
                            } else {
                                cntr++;
                            }
                        } else {
                            status.setAction_cd(0);
                            status.setFlow_slno(0);
                            break;
                        }
                    }
                }
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error occured during getting next file flow steps.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return status;
    }//end of webServiceForNextStage


    /*
     * @author Kartikey Singh
     */
    public static void webServiceForNextStage(Status_dobj status, String newStatus, String cntr_id, String appl_no, int action_cd,
            int purcd, String approveImplPrevAction, String empCode, String userStateCode, int selectedOffCode) throws VahanException {

        NextStageRequest request = new NextStageRequest();
        request.setCntr_id(cntr_id);
        request.setAppl_no(appl_no);
        request.setEmp_cd(Long.parseLong(empCode.trim())); // getting from session
        int prevAction = 0;

        TransactionManager tmgr = null;

        try {
            if (status.getStatus().equalsIgnoreCase(TableConstants.STATUS_REVERT)) {
                prevAction = Integer.parseInt(approveImplPrevAction);//for selectng radiobutton
                request.setFile_movement_type(TableConstants.BACKWARD);
                request.setAction_cd(prevAction);
            } else {
                request.setFile_movement_type(TableConstants.FORWARD);
                request.setAction_cd(action_cd);
            }

            request.setPur_cd(purcd);
            request.setState_cd(userStateCode); //getting from session
            request.setOff_cd(selectedOffCode); // getting from session
            NextStageResponse response = null;

            if (TableConstants.isNextStageWebService) {

                response = NextStageWS.getNextStageResponse(request);
                status.setCntr_id(response.getCntr_id());
                status.setAction_cd(response.getAction_cd());
                status.setOff_cd(response.getOff_cd());
                status.setEmp_cd(response.getEmp_cd());
                status.setFlow_slno(response.getFlow_slno());
                status.setRto_code(response.getRto_code());

            } else {

                String sql = null;
                tmgr = new TransactionManager("webServiceForNextStage");
                PreparedStatement ps = null;
                RowSet rs = null;

                if (request.getFile_movement_type().equals(TableConstants.BACKWARD)) {

                    sql = "select a.flow_srno, a.action_cd\n"
                            + " from tm_purpose_action_flow a\n"
                            + " where a.state_cd = ? and a.action_cd = ? and a.pur_cd = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, request.getState_cd());
                    ps.setInt(2, request.getAction_cd());
                    ps.setInt(3, request.getPur_cd());
                    rs = tmgr.fetchDetachedRowSet_No_release();

                    if (rs.next()) {
                        status.setAction_cd(prevAction);
                        status.setOff_cd(request.getOff_cd());
                        status.setFlow_slno(rs.getInt("flow_srno"));
                        status.setRto_code(request.getRto_code());
                    }

                } else {
                    int cntr = 1;
                    while (true) {

                        sql = "select a.flow_srno, a.action_cd,a.condition_formula,seat_cd  "
                                + " from tm_purpose_action_flow a, va_status b "
                                + " where a.state_cd = b.state_cd and a.pur_cd = b.pur_cd and a.state_cd = ? "
                                + " and b.appl_no = ? and a.flow_srno = b.flow_slno + ? and a.pur_cd =?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, request.getState_cd());
                        ps.setString(2, request.getAppl_no());
                        ps.setInt(3, cntr);
                        ps.setInt(4, purcd);

                        rs = tmgr.fetchDetachedRowSet_No_release();

                        if (rs.next()) {
                            if (!CommonUtils.isNullOrBlank(rs.getString("seat_cd")) && rs.getString("seat_cd").trim().split(",").length > 1) {
                                String[] actionCdWithFileFlow = rs.getString("seat_cd").trim().split(",");
                                status.setAction_cd(Integer.parseInt(actionCdWithFileFlow[0]));
                                status.setOff_cd(request.getOff_cd());
                                status.setFlow_slno(Integer.parseInt(actionCdWithFileFlow[1]));
                                status.setRto_code(request.getRto_code());
                                break;
                            } else if (status.getVehicleParameters() == null || isCondition(replaceTagValues(rs.getString("condition_formula"), status.getVehicleParameters()), "webServiceForNextStage")) {
                                status.setAction_cd(rs.getInt("action_cd"));
                                status.setOff_cd(request.getOff_cd());
                                status.setFlow_slno(rs.getInt("flow_srno"));
                                status.setRto_code(request.getRto_code());
                                break;
                            } else {
                                cntr++;
                            }
                        } else {
                            status.setAction_cd(0);
                            status.setFlow_slno(0);
                            break;
                        }

                    }
                }
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error occured during getting next file flow steps.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

    }//end of webServiceForNextStage

    public static Status_dobj webServiceForNextStage(Status_dobj status, TransactionManager tmgr) throws Exception {

        NextStageRequest request = new NextStageRequest();
        request.setCntr_id(status.getCntr_id());
        request.setAppl_no(status.getAppl_no());
        request.setEmp_cd(Long.parseLong(Util.getEmpCode().trim())); // getting from session
        int prevAction = 0;

        if (status.getStatus().equalsIgnoreCase(TableConstants.STATUS_REVERT)) {
            request.setFile_movement_type(TableConstants.BACKWARD);
            request.setAction_cd(status.getPrev_action_cd_selected());
            prevAction = status.getPrev_action_cd_selected();
        } else {
            request.setFile_movement_type(TableConstants.FORWARD);
            request.setAction_cd(status.getAction_cd());
        }

        request.setPur_cd(status.getPur_cd());
        request.setState_cd(Util.getUserStateCode()); //getting from session
        request.setOff_cd(Util.getSelectedSeat().getOff_cd()); // getting from session
        NextStageResponse response = null;

        if (TableConstants.isNextStageWebService) {

            response = NextStageWS.getNextStageResponse(request);
            status.setCntr_id(response.getCntr_id());
            status.setAction_cd(response.getAction_cd());
            status.setOff_cd(response.getOff_cd());
            status.setEmp_cd(response.getEmp_cd());
            status.setFlow_slno(response.getFlow_slno());
            status.setRto_code(response.getRto_code());

        } else {

            String sql = null;
            PreparedStatement ps = null;
            RowSet rs = null;

            if (request.getFile_movement_type().equals(TableConstants.BACKWARD)) {

                sql = "select a.flow_srno, a.action_cd\n"
                        + " from tm_purpose_action_flow a\n"
                        + " where a.state_cd = ? and a.action_cd = ? and a.pur_cd = ? "
                        + " and a.flow_srno < (select flow_slno from va_status where appl_no=? and pur_cd = ? and state_cd = ?) ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, request.getState_cd());
                ps.setInt(2, request.getAction_cd());
                ps.setInt(3, request.getPur_cd());
                ps.setString(4, status.getAppl_no());
                ps.setInt(5, request.getPur_cd());
                ps.setString(6, request.getState_cd());
                rs = tmgr.fetchDetachedRowSet_No_release();

                if (rs.next()) {
                    status.setAction_cd(prevAction);
                    status.setOff_cd(request.getOff_cd());
                    status.setFlow_slno(rs.getInt("flow_srno"));
                    status.setRto_code(request.getRto_code());
                } else if (request.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD && prevAction == TableConstants.TM_ROLE_PMT_APPLICATION_ENTRY) {
                    sql = "select a.flow_srno, a.action_cd\n"
                            + " from tm_purpose_action_flow a\n"
                            + " where a.state_cd = ? and a.action_cd = ? and a.pur_cd = ? "
                            + " and a.flow_srno in (select (flow_slno-1) from va_status where appl_no= ? and file_movement_slno=1   order by  file_movement_slno desc limit 1 ) ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, request.getState_cd());
                    ps.setInt(2, request.getAction_cd());
                    ps.setInt(3, request.getPur_cd());
                    ps.setString(4, status.getAppl_no());
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        status.setAction_cd(prevAction);
                        status.setOff_cd(request.getOff_cd());
                        status.setFlow_slno(rs.getInt("flow_srno"));
                        status.setRto_code(request.getRto_code());
                    }
                }
            } else {
                int cntr = 1;
                while (true) {
                    sql = "select a.flow_srno, a.action_cd,a.condition_formula,seat_cd "
                            + " from tm_purpose_action_flow a, va_status b "
                            + " where a.state_cd = b.state_cd and a.pur_cd = b.pur_cd and a.state_cd = ? "
                            + " and b.appl_no = ? and a.flow_srno = b.flow_slno + ? and a.pur_cd =?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, request.getState_cd());
                    ps.setString(2, request.getAppl_no());
                    ps.setInt(3, cntr);
                    ps.setInt(4, status.getPur_cd());

                    rs = tmgr.fetchDetachedRowSet_No_release();

                    if (rs.next()) {
                        if (!CommonUtils.isNullOrBlank(rs.getString("seat_cd")) && rs.getString("seat_cd").trim().split(",").length > 1) {
                            String[] actionCdWithFileFlow = rs.getString("seat_cd").trim().split(",");
                            status.setAction_cd(Integer.parseInt(actionCdWithFileFlow[0]));
                            status.setOff_cd(request.getOff_cd());
                            status.setFlow_slno(Integer.parseInt(actionCdWithFileFlow[1]));
                            status.setRto_code(request.getRto_code());
                            break;
                        } else if (status.getVehicleParameters() == null || isCondition(replaceTagValues(rs.getString("condition_formula"), status.getVehicleParameters()), "webServiceForNextStage-2")) {
                            status.setAction_cd(rs.getInt("action_cd"));
                            status.setOff_cd(request.getOff_cd());
                            status.setFlow_slno(rs.getInt("flow_srno"));
                            status.setRto_code(request.getRto_code());
                            break;
                        } else {
                            cntr++;
                        }
                    } else {
                        status.setAction_cd(0);
                        status.setFlow_slno(0);
                        break;
                    }
                }
            }
        }
        return status;
    }//end of webServiceForNextStage

    /*
     * @Kartikey Singh
     */
    public static Status_dobj webServiceForNextStage(Status_dobj status, TransactionManager tmgr, String empCode, String stateCode,
            int offCode) throws Exception {

        NextStageRequest request = new NextStageRequest();
        request.setCntr_id(status.getCntr_id());
        request.setAppl_no(status.getAppl_no());
        request.setEmp_cd(Long.parseLong(empCode.trim())); // getting from session
        int prevAction = 0;

        if (status.getStatus().equalsIgnoreCase(TableConstants.STATUS_REVERT)) {
            request.setFile_movement_type(TableConstants.BACKWARD);
            request.setAction_cd(status.getPrev_action_cd_selected());
            prevAction = status.getPrev_action_cd_selected();
        } else {
            request.setFile_movement_type(TableConstants.FORWARD);
            request.setAction_cd(status.getAction_cd());
        }

        request.setPur_cd(status.getPur_cd());
        request.setState_cd(stateCode); //getting from session
        request.setOff_cd(offCode); // getting from session
        NextStageResponse response = null;

        if (TableConstants.isNextStageWebService) {

            response = NextStageWS.getNextStageResponse(request);
            status.setCntr_id(response.getCntr_id());
            status.setAction_cd(response.getAction_cd());
            status.setOff_cd(response.getOff_cd());
            status.setEmp_cd(response.getEmp_cd());
            status.setFlow_slno(response.getFlow_slno());
            status.setRto_code(response.getRto_code());

        } else {
            String sql = null;
            PreparedStatement ps = null;
            RowSet rs = null;

            if (request.getFile_movement_type().equals(TableConstants.BACKWARD)) {

                sql = "select a.flow_srno, a.action_cd\n"
                        + " from tm_purpose_action_flow a\n"
                        + " where a.state_cd = ? and a.action_cd = ? and a.pur_cd = ? "
                        + " and a.flow_srno < (select flow_slno from va_status where appl_no=? and pur_cd = ? and state_cd = ?) ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, request.getState_cd());
                ps.setInt(2, request.getAction_cd());
                ps.setInt(3, request.getPur_cd());
                ps.setString(4, status.getAppl_no());
                ps.setInt(5, request.getPur_cd());
                ps.setString(6, request.getState_cd());
                rs = tmgr.fetchDetachedRowSet_No_release();

                if (rs.next()) {
                    status.setAction_cd(prevAction);
                    status.setOff_cd(request.getOff_cd());
                    status.setFlow_slno(rs.getInt("flow_srno"));
                    status.setRto_code(request.getRto_code());
                } else if (request.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD && prevAction == TableConstants.TM_ROLE_PMT_APPLICATION_ENTRY) {
                    sql = "select a.flow_srno, a.action_cd\n"
                            + " from tm_purpose_action_flow a\n"
                            + " where a.state_cd = ? and a.action_cd = ? and a.pur_cd = ? "
                            + " and a.flow_srno in (select (flow_slno-1) from va_status where appl_no= ? and file_movement_slno=1   order by  file_movement_slno desc limit 1 ) ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, request.getState_cd());
                    ps.setInt(2, request.getAction_cd());
                    ps.setInt(3, request.getPur_cd());
                    ps.setString(4, status.getAppl_no());
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        status.setAction_cd(prevAction);
                        status.setOff_cd(request.getOff_cd());
                        status.setFlow_slno(rs.getInt("flow_srno"));
                        status.setRto_code(request.getRto_code());
                    }
                }
            } else {
                int cntr = 1;
                while (true) {
                    sql = "select a.flow_srno, a.action_cd,a.condition_formula,seat_cd "
                            + " from tm_purpose_action_flow a, va_status b "
                            + " where a.state_cd = b.state_cd and a.pur_cd = b.pur_cd and a.state_cd = ? "
                            + " and b.appl_no = ? and a.flow_srno = b.flow_slno + ? and a.pur_cd =?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, request.getState_cd());
                    ps.setString(2, request.getAppl_no());
                    ps.setInt(3, cntr);
                    ps.setInt(4, status.getPur_cd());

                    rs = tmgr.fetchDetachedRowSet_No_release();

                    if (rs.next()) {
                        if (!CommonUtils.isNullOrBlank(rs.getString("seat_cd")) && rs.getString("seat_cd").trim().split(",").length > 1) {
                            String[] actionCdWithFileFlow = rs.getString("seat_cd").trim().split(",");
                            status.setAction_cd(Integer.parseInt(actionCdWithFileFlow[0]));
                            status.setOff_cd(request.getOff_cd());
                            status.setFlow_slno(Integer.parseInt(actionCdWithFileFlow[1]));
                            status.setRto_code(request.getRto_code());
                            break;
                        } else if (status.getVehicleParameters() == null || isCondition(replaceTagValues(rs.getString("condition_formula"), status.getVehicleParameters()), "webServiceForNextStage-2")) {
                            status.setAction_cd(rs.getInt("action_cd"));
                            status.setOff_cd(request.getOff_cd());
                            status.setFlow_slno(rs.getInt("flow_srno"));
                            status.setRto_code(request.getRto_code());
                            break;
                        } else {
                            cntr++;
                        }
                    } else {
                        status.setAction_cd(0);
                        status.setFlow_slno(0);
                        break;
                    }
                }
            }
        }
        return status;
    }//end of webServiceForNextStage

    //Create by Naman : As per discuss by Akshey SIR
    // so that it can read the value updated by the same tmgr before invoking this function.
    public static void webServiceForNextStage(Status_dobj status, String newStatus, String cntr_id,
            String appl_no, int action_cd, int purcd, ApproveImpl approveImpl, TransactionManager tmgr) throws Exception {

        NextStageRequest request = new NextStageRequest();
        request.setCntr_id(cntr_id);
        request.setAppl_no(appl_no);
        if ((String) Util.getSession().getAttribute("selected_role_cd") != null) {
            request.setEmp_cd(Long.parseLong(Util.getEmpCode().trim())); // getting from session
        } else {
            request.setEmp_cd(0); // getting from session
        }

        if (newStatus.equalsIgnoreCase(TableConstants.STATUS_REVERT)) {
            String prevAction = approveImpl.getPrevAction().getValue().toString();//for selectng radiobutton
            request.setFile_movement_type(TableConstants.BACKWARD);
            request.setAction_cd(Integer.parseInt(prevAction));
        } else {
            request.setFile_movement_type(TableConstants.FORWARD);
            request.setAction_cd(action_cd);
        }

        request.setPur_cd(purcd);
        //getting from session

        request.setState_cd(status.getState_cd());
        if ((String) Util.getSession().getAttribute("selected_role_cd") == null) {
            request.setOff_cd(status.getOff_cd());
        } else {
            request.setOff_cd(Util.getUserSeatOffCode() == 0 ? Util.getUserOffCode() : Util.getUserSeatOffCode()); // getting from session
        }
        NextStageResponse response = null;

        if (TableConstants.isNextStageWebService) {

            response = NextStageWS.getNextStageResponse(request);
            status.setCntr_id(response.getCntr_id());
            status.setAction_cd(response.getAction_cd());
            status.setOff_cd(response.getOff_cd());
            status.setEmp_cd(response.getEmp_cd());
            status.setFlow_slno(response.getFlow_slno());
            status.setRto_code(response.getRto_code());

        } else {

            String sql = null;
            PreparedStatement ps = null;
            RowSet rs = null;

            if (request.getFile_movement_type().equals(TableConstants.BACKWARD)) {

                sql = "select a.flow_srno, a.action_cd \n"
                        + " from tm_purpose_action_flow a \n"
                        + " where a.state_cd = ? and a.action_cd = ? and a.pur_cd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, request.getState_cd());
                ps.setInt(2, request.getAction_cd());
                ps.setInt(3, request.getPur_cd());
                rs = tmgr.fetchDetachedRowSet_No_release();

                if (rs.next()) {
                    status.setAction_cd(rs.getInt("action_cd"));
                    status.setOff_cd(request.getOff_cd());
                    status.setFlow_slno(rs.getInt("flow_srno"));
                    status.setRto_code(request.getRto_code());
                }
            } else {
                int cntr = 1;
                while (true) {

                    sql = "select a.flow_srno, a.action_cd,a.condition_formula,seat_cd "
                            + " from tm_purpose_action_flow a, va_status b "
                            + " where a.state_cd = b.state_cd and a.pur_cd = b.pur_cd and a.state_cd = ? "
                            + " and b.appl_no = ? and a.flow_srno = b.flow_slno + ? and a.pur_cd =?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, request.getState_cd());
                    ps.setString(2, request.getAppl_no());
                    ps.setInt(3, cntr);
                    ps.setInt(4, purcd);

                    rs = tmgr.fetchDetachedRowSet_No_release();

                    if (rs.next()) {
                        if (!CommonUtils.isNullOrBlank(rs.getString("seat_cd")) && rs.getString("seat_cd").trim().split(",").length > 1) {
                            String[] actionCdWithFileFlow = rs.getString("seat_cd").trim().split(",");
                            status.setAction_cd(Integer.parseInt(actionCdWithFileFlow[0]));
                            status.setOff_cd(request.getOff_cd());
                            status.setFlow_slno(Integer.parseInt(actionCdWithFileFlow[1]));
                            status.setRto_code(request.getRto_code());
                            break;
                        } else if (status.getVehicleParameters() == null || isCondition(replaceTagValues(rs.getString("condition_formula"), status.getVehicleParameters()), "webServiceForNextStage-3")) {
                            status.setAction_cd(rs.getInt("action_cd"));
                            status.setOff_cd(request.getOff_cd());
                            status.setFlow_slno(rs.getInt("flow_srno"));
                            status.setRto_code(request.getRto_code());
                            break;
                        } else {
                            cntr++;
                        }
                    } else {
                        status.setAction_cd(0);
                        status.setFlow_slno(0);
                        break;
                    }
                }
            }
        }
    }//end of webServiceForNextStage

    /*
     * @author Kartikey Singh
     * @oldComment: read the value updated by the same tmgr before invoking this function.
     * @newComment: Removed the ternary operator condition.
     */
    public static void webServiceForNextStage(Status_dobj status, String cntr_id,
            String appl_no, int action_cd, int purcd, ApproveImpl approveImpl, TransactionManager tmgr,
            String selectedRoleCode, String empCode, int offCode) throws Exception {
        String newStatus = status.getStatus();
        NextStageRequest request = new NextStageRequest();
        request.setCntr_id(cntr_id);
        request.setAppl_no(appl_no);
        if (selectedRoleCode != null) {
            request.setEmp_cd(Long.parseLong(empCode)); // getting from session
        } else {
            request.setEmp_cd(0); // getting from session
        }

        if (newStatus.equalsIgnoreCase(TableConstants.STATUS_REVERT)) {
            String prevAction = approveImpl.getPrevAction().getValue().toString();//for selectng radiobutton
            request.setFile_movement_type(TableConstants.BACKWARD);
            request.setAction_cd(Integer.parseInt(prevAction));
        } else {
            request.setFile_movement_type(TableConstants.FORWARD);
            request.setAction_cd(action_cd);
        }

        request.setPur_cd(purcd);
        //getting from session

        request.setState_cd(status.getState_cd());
        if (selectedRoleCode == null) {
            request.setOff_cd(status.getOff_cd());
        } else {
            // Removed the ternary operator condition as all those methods point to the same thing
            request.setOff_cd(offCode); // getting from session
        }
        NextStageResponse response = null;

        if (TableConstants.isNextStageWebService) {

            response = NextStageWS.getNextStageResponse(request);
            status.setCntr_id(response.getCntr_id());
            status.setAction_cd(response.getAction_cd());
            status.setOff_cd(response.getOff_cd());
            status.setEmp_cd(response.getEmp_cd());
            status.setFlow_slno(response.getFlow_slno());
            status.setRto_code(response.getRto_code());

        } else {

            String sql = null;
            PreparedStatement ps = null;
            RowSet rs = null;

            if (request.getFile_movement_type().equals(TableConstants.BACKWARD)) {

                sql = "select a.flow_srno, a.action_cd \n"
                        + " from tm_purpose_action_flow a \n"
                        + " where a.state_cd = ? and a.action_cd = ? and a.pur_cd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, request.getState_cd());
                ps.setInt(2, request.getAction_cd());
                ps.setInt(3, request.getPur_cd());
                rs = tmgr.fetchDetachedRowSet_No_release();

                if (rs.next()) {
                    status.setAction_cd(rs.getInt("action_cd"));
                    status.setOff_cd(request.getOff_cd());
                    status.setFlow_slno(rs.getInt("flow_srno"));
                    status.setRto_code(request.getRto_code());
                }
            } else {
                int cntr = 1;
                while (true) {

                    sql = "select a.flow_srno, a.action_cd,a.condition_formula,seat_cd "
                            + " from tm_purpose_action_flow a, va_status b "
                            + " where a.state_cd = b.state_cd and a.pur_cd = b.pur_cd and a.state_cd = ? "
                            + " and b.appl_no = ? and a.flow_srno = b.flow_slno + ? and a.pur_cd =?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, request.getState_cd());
                    ps.setString(2, request.getAppl_no());
                    ps.setInt(3, cntr);
                    ps.setInt(4, purcd);

                    rs = tmgr.fetchDetachedRowSet_No_release();

                    if (rs.next()) {
                        if (!CommonUtils.isNullOrBlank(rs.getString("seat_cd")) && rs.getString("seat_cd").trim().split(",").length > 1) {
                            String[] actionCdWithFileFlow = rs.getString("seat_cd").trim().split(",");
                            status.setAction_cd(Integer.parseInt(actionCdWithFileFlow[0]));
                            status.setOff_cd(request.getOff_cd());
                            status.setFlow_slno(Integer.parseInt(actionCdWithFileFlow[1]));
                            status.setRto_code(request.getRto_code());
                            break;
                        } else if (status.getVehicleParameters() == null || isCondition(replaceTagValues(rs.getString("condition_formula"), status.getVehicleParameters()), "webServiceForNextStage-3")) {
                            status.setAction_cd(rs.getInt("action_cd"));
                            status.setOff_cd(request.getOff_cd());
                            status.setFlow_slno(rs.getInt("flow_srno"));
                            status.setRto_code(request.getRto_code());
                            break;
                        } else {
                            cntr++;
                        }
                    } else {
                        status.setAction_cd(0);
                        status.setFlow_slno(0);
                        break;
                    }
                }
            }
        }
    }//end of webServiceForNextStage

    public static Timestamp getSystemDateInPostgres() {
        Date cur_date = new Date();
        SimpleDateFormat sdf_dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currnet_date_time = sdf_dt.format(cur_date);
        Timestamp date_time = Timestamp.valueOf(currnet_date_time);
        return date_time;
    }

    public static String parseDateToString(Date dt) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        sdf.applyPattern("dd-MMM-yyyy");
        String nDate = sdf.format(dt);
        return nDate;
    }

    public static String parseDateToStringDDMMYYYY(Date dt) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        sdf.applyPattern("dd-MM-yyyy");
        String nDate = sdf.format(dt);
        return nDate;
    }

    /**
     * *
     * Use this method for setting min date or max date from current date.
     * Positive value for addition and negative value for subtraction.
     *
     * @param date Date
     * @param year Integer
     * @param month Integer
     * @param day_of_month Integer.
     * @return Date
     */
//    public static Date dateRange(Date date, int year, int month, int day_of_month) {
//        java.util.Calendar cal = new GregorianCalendar();
//        cal.setTime(date);
//        cal.add(java.util.Calendar.YEAR, year);
//        cal.add(java.util.Calendar.MONTH, month);
//        cal.add(java.util.Calendar.DAY_OF_MONTH, day_of_month);
//        return cal.getTime();
//    }
    public static Date dateRange(Date date, int year, int month, int day_of_month) {
        java.util.Calendar cal = java.util.Calendar.getInstance(TimeZone.getDefault());
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(nic.java.util.DateUtils.DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getDefault());
        cal.setTime(date);
        cal.add(java.util.Calendar.YEAR, year);
        cal.add(java.util.Calendar.MONTH, month);
        cal.add(java.util.Calendar.DAY_OF_MONTH, day_of_month);
        return cal.getTime();
    }

    public static int getOwnerSr(TransactionManager tmgr, String regn_no) throws VahanException { //need to used common method to have all the parameter from vt_owner...pending
        PreparedStatement ps = null;
        String sql = null;
        int owner_sr = 0;
        try {

            sql = "SELECT owner_sr FROM " + TableList.VT_OWNER + " where regn_no=? and state_cd = ?  ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            int cntr = 1;
            while (rs.next()) { //if any record is exist
                if (cntr > 1) {
                    owner_sr = 0;
                    throw new VahanException("Owner Details founds in Multiple Offices, please contact State Administrator.");
                }
                owner_sr = rs.getInt("owner_sr");
                cntr++;
            }
            if (owner_sr == 0) {
                throw new VahanException("Active Owner Details not found for selected vehicle, please contact State Administrator.");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return owner_sr;
    } // end of getOwnerSr

    /*
     * Author: Kartikey Singh
     * To get ApplNo from Rest, and saves it instantly
     */
    public static String getUniqueApplNo(TransactionManager tmgr, String stateCd) throws VahanException {
        String applNo = null;
        try {
            if (stateCd == null || stateCd.isEmpty() || stateCd.length() > 2 || stateCd.length() < 2) {
                throw new VahanException("Something went wrong, please try again.");
            }
            String strSQL = "SELECT ? || to_char(CURRENT_DATE,'YYMMDD') || lpad((floor(random() * 9) + 1)::varchar || nextval('appl_no_v4_seq')::varchar, 8, '0') AS appl_no";

            PreparedStatement psmt = tmgr.prepareStatement(strSQL);
            psmt.setString(1, stateCd);
            try (RowSet rs = tmgr.fetchDetachedRowSet_No_release()) {
                if (rs.next()) {
                    applNo = rs.getString("appl_no");
                    strSQL = "INSERT INTO vt_unique_appl_nos VALUES (?, current_timestamp)";
                    psmt = tmgr.prepareStatement(strSQL);
                    psmt.setString(1, applNo);
                    psmt.executeUpdate();
                }
            }
            psmt.close();
            psmt = null;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please try again.");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please try again.");
        }
        return applNo;
    }

    /*
     * Author: Kartikey Singh
     * To save ApplNo at the end of transaction from Rest     
     */
    public static String saveUniqueApplNo(TransactionManager tmgr, String applNo) throws VahanException {
        try {
            String strSQL = "INSERT INTO vt_unique_appl_nos VALUES (?, current_timestamp)";
            try (PreparedStatement psmt = tmgr.prepareStatement(strSQL)) {
                psmt.setString(1, applNo);
                psmt.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please try again.");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please try again.");
        }
        return applNo;
    }

    public static long getUniqueInstrumentNo(TransactionManager tmgr, String stateCd) throws VahanException {
        long instrumentNo = 0;
        try {
            if (stateCd == null || stateCd.isEmpty() || stateCd.length() > 2 || stateCd.length() < 2) {
                throw new VahanException("Something went wrong, please try again.");
            }

            String strSQL = "SELECT to_char(CURRENT_DATE,'YYMMDD') || lpad(egov_code::varchar, 2, '0') || lpad(nextval('seq_v4_instrument_no')::varchar, 6, '0') AS instrument_no from tm_state where state_code = ?";
            PreparedStatement psmt = tmgr.prepareStatement(strSQL);
            psmt.setString(1, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                instrumentNo = rs.getLong("instrument_no");
            }
            rs.close();
            psmt.close();
            rs = null;
            psmt = null;
        } catch (SQLException e) {
            instrumentNo = 0;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please try again.");
        } catch (Exception e) {
            instrumentNo = 0;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please try again.");
        }
        if (instrumentNo == 0) {
            throw new VahanException("Something went wrong, please try again.");
        }
        return instrumentNo;
    }

    public static long getUniqueUserCd(TransactionManager tmgr, String stateCd) throws VahanException {
        long userCd = 0;
        try {
            if (stateCd == null || stateCd.isEmpty() || stateCd.length() > 2 || stateCd.length() < 2) {
                throw new VahanException("Something went wrong, please try again.");
            }

            for (int i = 0; i <= 1000; i++) {
                String strSQL = "SELECT to_char(CURRENT_DATE,'YY') || lpad(egov_code::varchar, 2, '0') || to_char(CURRENT_DATE,'MM') || lpad(nextval('seq_v4_user_cd')::varchar, 4, '0') AS user_cd from tm_state where state_code = ?";
                PreparedStatement psmt = tmgr.prepareStatement(strSQL);
                psmt.setString(1, stateCd);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    userCd = rs.getLong("user_cd");
                }
                if (userCd != 0) {
                    strSQL = "SELECT USER_CD FROM " + TableList.TM_USER_INFO + " WHERE user_cd= ?";
                    psmt = tmgr.prepareStatement(strSQL);
                    psmt.setLong(1, userCd);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        userCd = 0;
                        continue;
                    }
                    break;
                }
            }
        } catch (SQLException e) {
            userCd = 0;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please try again.");
        } catch (Exception e) {
            userCd = 0;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please try again.");
        }
        if (userCd == 0) {
            throw new VahanException("Error generation of user information.");
        }
        return userCd;
    }

    public static String getUniqueDealerCd(TransactionManager tmgr, String stateCd) throws VahanException {
        String dealerCd = null;
        try {
            if (stateCd == null || stateCd.isEmpty() || stateCd.length() > 2 || stateCd.length() < 2) {
                throw new VahanException("Something went wrong, please try again.");
            }

            String strSQL = "SELECT ? || to_char(CURRENT_DATE,'YYMMDD') || lpad(nextval('seq_v4_dealer_cd')::varchar, 7, '0') AS dealer_cd";
            PreparedStatement psmt = tmgr.prepareStatement(strSQL);
            psmt.setString(1, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dealerCd = rs.getString("dealer_cd");
            }
            rs.close();
            psmt.close();
            rs = null;
            psmt = null;
        } catch (SQLException e) {
            dealerCd = null;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please try again.");
        } catch (Exception e) {
            dealerCd = null;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please try again.");
        }
        if (dealerCd == null) {
            throw new VahanException("Something went wrong, please try again.");
        }
        return dealerCd;
    }

    public static String getUniqueChallanNo(TransactionManager tmgr, String stateCd) throws VahanException {
        String challanNo = null;
        try {
            if (stateCd == null || stateCd.isEmpty() || stateCd.length() > 2 || stateCd.length() < 2) {
                throw new VahanException("Something went wrong, please try again.");
            }

            String strSQL = "SELECT ? || to_char(CURRENT_DATE,'YYMM') || lpad(nextval('seq_v4_challan_no')::varchar, 8, '0') AS challan_no";
            PreparedStatement psmt = tmgr.prepareStatement(strSQL);
            psmt.setString(1, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                challanNo = rs.getString("challan_no");
            }
            rs.close();
            psmt.close();
            rs = null;
            psmt = null;
        } catch (SQLException e) {
            challanNo = null;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please try again.");
        } catch (Exception e) {
            challanNo = null;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please try again.");
        }
        if (challanNo == null) {
            throw new VahanException("Something went wrong, please try again.");
        }
        return challanNo;
    }

    public static String getUniqueOfficeRcptNo(TransactionManager tmgr, String stateCd, int offCd, String flag, boolean readOnly) throws VahanException {

        String offRcptNo = null;
        String tempSeries = "";
        long tempSequence = 0;
        try {
            if (CommonUtils.isNullOrBlank(stateCd) || offCd == 0) {
                throw new VahanException("Something went wrong, please try again.");
            }
            String strSQL = null;
            PreparedStatement psmt = null;

            strSQL = "SELECT CURRENT_DATE > '2020-01-03'::date yr";
            tmgr.prepareStatement(strSQL);
            RowSet rsCheck = tmgr.fetchDetachedRowSet_No_release();
            if (rsCheck.next()) {
                if (rsCheck.getBoolean("yr")) {
                    return getUniqueOfficeRcptNoPool(tmgr, stateCd, offCd, flag, readOnly);
                } else {
                    return getUniqueOfficeRcptNo(tmgr, stateCd);
                }
            }

            if (!readOnly) {
                strSQL = "UPDATE tm_office_rcpt_no SET SEQUENCE_NO = SEQUENCE_NO + 1 WHERE state_cd = ? AND off_cd = ? AND flag = ? ";
                psmt = tmgr.prepareStatement(strSQL);
                psmt.setString(1, stateCd);
                psmt.setInt(2, offCd);
                psmt.setString(3, flag);
                psmt.executeUpdate();
            } else {
                tempSequence = 1;
            }
            strSQL = "SELECT PREFIX FROM tm_office_rcpt_no WHERE state_cd = ? AND off_cd = ? AND flag = ? ";
            psmt = tmgr.prepareStatement(strSQL);
            psmt.setString(1, stateCd);
            psmt.setInt(2, offCd);
            psmt.setString(3, flag);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            java.util.Calendar cal = java.util.Calendar.getInstance();
            String monthYear = String.valueOf(cal.get(java.util.Calendar.YEAR)).substring(2, 4) + String.format("%02d", cal.get(java.util.Calendar.MONTH) + 1);
            strSQL = "SELECT to_char(current_date, 'YYMM') as curr_month";
            psmt = tmgr.prepareStatement(strSQL);
            RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
            if (rs1.next()) {
                monthYear = rs1.getString("curr_month");
            }

            if (rs.next()) {
                if (!monthYear.equalsIgnoreCase(rs.getString("PREFIX").trim())) {
                    if (Integer.parseInt(monthYear) < Integer.parseInt(rs.getString("PREFIX"))) {
                        throw new VahanException("Date/Time not synchronized");
                    }
                    strSQL = "UPDATE tm_office_rcpt_no SET PREFIX = ?, SEQUENCE_NO = 1 WHERE state_cd = ? AND off_cd = ? AND flag = ? ";
                    psmt = tmgr.prepareStatement(strSQL);
                    psmt.setString(1, monthYear);
                    psmt.setString(2, stateCd);
                    psmt.setInt(3, offCd);
                    psmt.setString(4, flag);
                    psmt.executeUpdate();
                }
            } else {
                strSQL = "INSERT INTO tm_office_rcpt_no VALUES (?, ?, ?, ?, 1)";
                psmt = tmgr.prepareStatement(strSQL);
                psmt.setString(1, stateCd);
                psmt.setInt(2, offCd);
                psmt.setString(3, flag);
                psmt.setString(4, monthYear);
                psmt.executeUpdate();
                tempSequence = 0;
            }
            rs.close();

            strSQL = "SELECT PREFIX, SEQUENCE_NO FROM tm_office_rcpt_no WHERE state_cd = ? AND off_cd = ? AND flag = ? ";
            psmt = tmgr.prepareStatement(strSQL);
            psmt.setString(1, stateCd);
            psmt.setInt(2, offCd);
            psmt.setString(3, flag);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                tempSeries = rs.getString("PREFIX").trim();
                tempSequence = tempSequence + rs.getLong("SEQUENCE_NO");

                offRcptNo = stateCd + offCd + flag + tempSeries;
                offRcptNo = offRcptNo.trim().toUpperCase();

                int balLength = 16 - offRcptNo.length();
                String format = String.format("%%0%dd", balLength);
                String z = String.format(format, tempSequence);

                offRcptNo = offRcptNo + z;
            }

            rs.close();
            psmt.close();
            rs = null;
            psmt = null;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please try again.");
        }

        return offRcptNo;
    }

    /**
     * @author Kartikey Singh
     */
    public static String getUniqueOfficeRcptNo(TransactionManager tmgr, String stateCd, int offCd, String flag, boolean readOnly,
            String empCode) throws VahanException {

        String offRcptNo = null;
        String tempSeries = "";
        long tempSequence = 0;
        try {
            if (CommonUtils.isNullOrBlank(stateCd) || offCd == 0) {
                throw new VahanException("Something went wrong, please try again.");
            }
            String strSQL = null;
            PreparedStatement psmt = null;

            strSQL = "SELECT CURRENT_DATE > '2020-01-03'::date yr";
            tmgr.prepareStatement(strSQL);
            RowSet rsCheck = tmgr.fetchDetachedRowSet_No_release();
            if (rsCheck.next()) {
                if (rsCheck.getBoolean("yr")) {
                    return getUniqueOfficeRcptNoPool(tmgr, stateCd, offCd, flag, readOnly, empCode);
                } else {
                    return getUniqueOfficeRcptNo(tmgr, stateCd);
                }
            }

            if (!readOnly) {
                strSQL = "UPDATE tm_office_rcpt_no SET SEQUENCE_NO = SEQUENCE_NO + 1 WHERE state_cd = ? AND off_cd = ? AND flag = ? ";
                psmt = tmgr.prepareStatement(strSQL);
                psmt.setString(1, stateCd);
                psmt.setInt(2, offCd);
                psmt.setString(3, flag);
                psmt.executeUpdate();
            } else {
                tempSequence = 1;
            }
            strSQL = "SELECT PREFIX FROM tm_office_rcpt_no WHERE state_cd = ? AND off_cd = ? AND flag = ? ";
            psmt = tmgr.prepareStatement(strSQL);
            psmt.setString(1, stateCd);
            psmt.setInt(2, offCd);
            psmt.setString(3, flag);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            java.util.Calendar cal = java.util.Calendar.getInstance();
            String monthYear = String.valueOf(cal.get(java.util.Calendar.YEAR)).substring(2, 4) + String.format("%02d", cal.get(java.util.Calendar.MONTH) + 1);
            strSQL = "SELECT to_char(current_date, 'YYMM') as curr_month";
            psmt = tmgr.prepareStatement(strSQL);
            RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
            if (rs1.next()) {
                monthYear = rs1.getString("curr_month");
            }

            if (rs.next()) {
                if (!monthYear.equalsIgnoreCase(rs.getString("PREFIX").trim())) {
                    if (Integer.parseInt(monthYear) < Integer.parseInt(rs.getString("PREFIX"))) {
                        throw new VahanException("Date/Time not synchronized");
                    }
                    strSQL = "UPDATE tm_office_rcpt_no SET PREFIX = ?, SEQUENCE_NO = 1 WHERE state_cd = ? AND off_cd = ? AND flag = ? ";
                    psmt = tmgr.prepareStatement(strSQL);
                    psmt.setString(1, monthYear);
                    psmt.setString(2, stateCd);
                    psmt.setInt(3, offCd);
                    psmt.setString(4, flag);
                    psmt.executeUpdate();
                }
            } else {
                strSQL = "INSERT INTO tm_office_rcpt_no VALUES (?, ?, ?, ?, 1)";
                psmt = tmgr.prepareStatement(strSQL);
                psmt.setString(1, stateCd);
                psmt.setInt(2, offCd);
                psmt.setString(3, flag);
                psmt.setString(4, monthYear);
                psmt.executeUpdate();
                tempSequence = 0;
            }
            rs.close();

            strSQL = "SELECT PREFIX, SEQUENCE_NO FROM tm_office_rcpt_no WHERE state_cd = ? AND off_cd = ? AND flag = ? ";
            psmt = tmgr.prepareStatement(strSQL);
            psmt.setString(1, stateCd);
            psmt.setInt(2, offCd);
            psmt.setString(3, flag);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                tempSeries = rs.getString("PREFIX").trim();
                tempSequence = tempSequence + rs.getLong("SEQUENCE_NO");

                offRcptNo = stateCd + offCd + flag + tempSeries;
                offRcptNo = offRcptNo.trim().toUpperCase();

                int balLength = 16 - offRcptNo.length();
                String format = String.format("%%0%dd", balLength);
                String z = String.format(format, tempSequence);

                offRcptNo = offRcptNo + z;
            }

            rs.close();
            psmt.close();
            rs = null;
            psmt = null;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please try again.");
        }

        return offRcptNo;
    }

    public static String getUniqueOfficeRcptNoPool(TransactionManager tmgr, String stateCd, int offCd, String flag, boolean readOnly) throws VahanException {
        String rcptNo = null;
        int num = 0;
        String sql;
        PreparedStatement ps;
        ResultSet rs;
        try {
            int i = 1;
            sql = "select rcpt_no, state_cd||off_cd||flag||prefix||lpad(rcpt_no::text, 9-(length(off_cd::text)), '0') as gn_rcpt_no"
                    + " from tm_office_rcpt_no_available "
                    + " where  state_cd=?  and  off_cd=? and flag =? "
                    + " and prefix=to_char(CURRENT_DATE,'YYMM') "
                    + " order by rcpt_no LIMIT 1 "
                    + " FOR UPDATE SKIP LOCKED ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(i++, stateCd);
            ps.setInt(i++, offCd);
            ps.setString(i++, flag);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                rcptNo = rs.getString("gn_rcpt_no");
                num = rs.getInt("rcpt_no");

                if (num % 1000 == 950) {
                    fillUpRcptPoolNextMonth(stateCd, offCd, flag);
                    fillUpRcptPool(stateCd, offCd, flag, true);
                }

                sql = "Delete "
                        + " from tm_office_rcpt_no_available "
                        + " where  state_cd=?  and  off_cd=? and flag =? "
                        + " and rcpt_no=? and prefix=to_char(CURRENT_DATE,'YYMM')";
                i = 1;
                ps = tmgr.prepareStatement(sql);
                ps.setString(i++, stateCd);
                ps.setInt(i++, offCd);
                ps.setString(i++, flag);
                ps.setInt(i++, num);
                ps.executeUpdate();
                return rcptNo;
            }

            //populate pool
            fillUpRcptPool(stateCd, offCd, flag, false);
            return getUniqueOfficeRcptNoPool(tmgr, stateCd, offCd, flag, readOnly);

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please try again.");
        }

        // return rcptNo;
    }

    /*
     * @author Kartikey Singh
     */
    public static String getUniqueOfficeRcptNoPool(TransactionManager tmgr, String stateCd, int offCd, String flag, boolean readOnly,
            String empCode) throws VahanException {
        String rcptNo = null;
        int num = 0;
        String sql;
        PreparedStatement ps;
        ResultSet rs;
        try {
            int i = 1;
            sql = "select rcpt_no, state_cd||off_cd||flag||prefix||lpad(rcpt_no::text, 9-(length(off_cd::text)), '0') as gn_rcpt_no"
                    + " from tm_office_rcpt_no_available "
                    + " where  state_cd=?  and  off_cd=? and flag =? "
                    + " and prefix=to_char(CURRENT_DATE,'YYMM') "
                    + " order by rcpt_no LIMIT 1 "
                    + " FOR UPDATE SKIP LOCKED ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(i++, stateCd);
            ps.setInt(i++, offCd);
            ps.setString(i++, flag);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                rcptNo = rs.getString("gn_rcpt_no");
                num = rs.getInt("rcpt_no");

                if (num % 1000 == 950) {
                    fillUpRcptPoolNextMonth(stateCd, offCd, flag, empCode);
                    fillUpRcptPool(stateCd, offCd, flag, true, empCode);
                }

                sql = "Delete "
                        + " from tm_office_rcpt_no_available "
                        + " where  state_cd=?  and  off_cd=? and flag =? "
                        + " and rcpt_no=? and prefix=to_char(CURRENT_DATE,'YYMM')";
                i = 1;
                ps = tmgr.prepareStatement(sql);
                ps.setString(i++, stateCd);
                ps.setInt(i++, offCd);
                ps.setString(i++, flag);
                ps.setInt(i++, num);
                ps.executeUpdate();
                return rcptNo;
            }

            //populate pool
            fillUpRcptPool(stateCd, offCd, flag, false, empCode);
            return getUniqueOfficeRcptNoPool(tmgr, stateCd, offCd, flag, readOnly, empCode);

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please try again.");
        }

        // return rcptNo;
    }

    public static void fillUpRcptPool(String stateCd, int offCd, String flag, boolean prefill) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        String sql;

        try {
            tmgr = new TransactionManager("fillUpDocumentPool");

            sql = "update "
                    + " tm_office_rcpt_no "
                    + " set prefix=prefix "
                    + " where state_cd=? and off_cd=? and flag =? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            ps.setString(3, flag);
            int i = ps.executeUpdate();
            if (i <= 0) {

                sql = "INSERT INTO tm_office_rcpt_no VALUES (?, ?, ?, to_char(CURRENT_DATE,'YYMM'), 1)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setInt(2, offCd);
                ps.setString(3, flag);
                ps.executeUpdate();

                sql = "update "
                        + " tm_office_rcpt_no "
                        + " set prefix=prefix "
                        + " where state_cd=? and off_cd=? and flag =? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setInt(2, offCd);
                ps.setString(3, flag);
                ps.executeUpdate();

            }

            //For Double Check ,skip it for prefill at 950 
            if (!prefill) {
                sql = "select  prefix "
                        + " from tm_office_rcpt_no_available "
                        + "  where  state_cd=?  and  off_cd=? and flag =? "
                        + " and prefix =to_char(CURRENT_DATE,'YYMM') "
                        + " LIMIT 1 ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setInt(2, offCd);
                ps.setString(3, flag);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    return;
                }
            }

            int min = 0;
            int max = 0;
            sql = "select sequence_no,case when prefix=to_char(CURRENT_DATE,'YYMM') then false else true end as reset "
                    + " from "
                    + " tm_office_rcpt_no "
                    + " where state_cd=? and off_cd=? and flag =? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            ps.setString(3, flag);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (rs.getBoolean("reset")) {
                    min = 1;
                    max = 999;
                    if (prefill) {
                        sql = "select  max(rcpt_no) rcpt_no"
                                + " from "
                                + " tm_office_rcpt_no_available "
                                + " where "
                                + " state_cd=? and off_cd=? and flag =? and prefix=to_char(CURRENT_DATE,'YYMM')";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, stateCd);
                        ps.setInt(2, offCd);
                        ps.setString(3, flag);
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        if (rs.next()) {
                            if (rs.getInt("rcpt_no") > 0) {
                                min = rs.getInt("rcpt_no") + 1;
                                max = min + 999;

                                sql = "Delete from " + TableList.TM_OFFICE_RCPT_NO_AVAILABLE
                                        + " where"
                                        + " state_cd=? and off_cd=? and flag =? and prefix::int < to_char(CURRENT_DATE,'YYMM')::int ";
                                ps = tmgr.prepareStatement(sql);
                                ps.setString(1, stateCd);
                                ps.setInt(2, offCd);
                                ps.setString(3, flag);
                                ps.executeUpdate();
                            }
                        }
                    }
                } else {
                    min = rs.getInt("SEQUENCE_NO") + 1;
                    max = min + 999;
                }
            }

            //update tm_office_rcpt_no to new prefix,sequence_no and sequence_flag
            sql = "update tm_office_rcpt_no b "
                    + "set  sequence_no=? ,prefix=to_char(CURRENT_DATE,'YYMM') "
                    + "  where state_cd=? and off_cd=? and flag =?  ";

            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, max);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.setString(4, flag);
            ps.executeUpdate();

            //Remove all Unused Receipt,skip it for prefill at 950 
            if (!prefill) {
                sql = "Delete from " + TableList.TM_OFFICE_RCPT_NO_AVAILABLE
                        + " where state_cd=? and off_cd=? and flag =? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setInt(2, offCd);
                ps.setString(3, flag);
                ps.executeUpdate();
            }

            //Insert records into tm_office_rcpt_no_available
            sql = "insert into tm_office_rcpt_no_available "
                    + " select a.state_cd,a.off_cd,a.flag,to_char(CURRENT_DATE,'YYMM'), "
                    + "  b.rcpt_no,?,current_timestamp "
                    + "  from 	tm_office_rcpt_no a,(SELECT  rcpt_no "
                    + "  from generate_series(?, ?) rcpt_no) b "
                    + "  where a.state_cd=?  and a.off_cd=? and a.flag=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Util.getEmpCodeLong());
            ps.setInt(2, min);
            ps.setInt(3, max);
            ps.setString(4, stateCd);
            ps.setInt(5, offCd);
            ps.setString(6, flag);
            ps.executeUpdate();

            tmgr.commit();

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during Unique Number Generation, "
                    + " Please Contact to the System Administrator");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
    }

    /**
     * @author Kartikey Singh Need to check for scenarios when empCode = null
     */
    public static void fillUpRcptPool(String stateCd, int offCd, String flag, boolean prefill, String empCode) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        String sql;

        try {
            tmgr = new TransactionManager("fillUpDocumentPool");

            sql = "update "
                    + " tm_office_rcpt_no "
                    + " set prefix=prefix "
                    + " where state_cd=? and off_cd=? and flag =? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            ps.setString(3, flag);
            int i = ps.executeUpdate();
            if (i <= 0) {

                sql = "INSERT INTO tm_office_rcpt_no VALUES (?, ?, ?, to_char(CURRENT_DATE,'YYMM'), 1)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setInt(2, offCd);
                ps.setString(3, flag);
                ps.executeUpdate();

                sql = "update "
                        + " tm_office_rcpt_no "
                        + " set prefix=prefix "
                        + " where state_cd=? and off_cd=? and flag =? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setInt(2, offCd);
                ps.setString(3, flag);
                ps.executeUpdate();

            }

            //For Double Check ,skip it for prefill at 950 
            if (!prefill) {
                sql = "select  prefix "
                        + " from tm_office_rcpt_no_available "
                        + "  where  state_cd=?  and  off_cd=? and flag =? "
                        + " and prefix =to_char(CURRENT_DATE,'YYMM') "
                        + " LIMIT 1 ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setInt(2, offCd);
                ps.setString(3, flag);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    return;
                }
            }

            int min = 0;
            int max = 0;
            sql = "select sequence_no,case when prefix=to_char(CURRENT_DATE,'YYMM') then false else true end as reset "
                    + " from "
                    + " tm_office_rcpt_no "
                    + " where state_cd=? and off_cd=? and flag =? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            ps.setString(3, flag);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (rs.getBoolean("reset")) {
                    min = 1;
                    max = 999;
                    if (prefill) {
                        sql = "select  max(rcpt_no) rcpt_no"
                                + " from "
                                + " tm_office_rcpt_no_available "
                                + " where "
                                + " state_cd=? and off_cd=? and flag =? and prefix=to_char(CURRENT_DATE,'YYMM')";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, stateCd);
                        ps.setInt(2, offCd);
                        ps.setString(3, flag);
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        if (rs.next()) {
                            if (rs.getInt("rcpt_no") > 0) {
                                min = rs.getInt("rcpt_no") + 1;
                                max = min + 999;

                                sql = "Delete from " + TableList.TM_OFFICE_RCPT_NO_AVAILABLE
                                        + " where"
                                        + " state_cd=? and off_cd=? and flag =? and prefix::int < to_char(CURRENT_DATE,'YYMM')::int ";
                                ps = tmgr.prepareStatement(sql);
                                ps.setString(1, stateCd);
                                ps.setInt(2, offCd);
                                ps.setString(3, flag);
                                ps.executeUpdate();
                            }
                        }
                    }
                } else {
                    min = rs.getInt("SEQUENCE_NO") + 1;
                    max = min + 999;
                }
            }

            //update tm_office_rcpt_no to new prefix,sequence_no and sequence_flag
            sql = "update tm_office_rcpt_no b "
                    + "set  sequence_no=? ,prefix=to_char(CURRENT_DATE,'YYMM') "
                    + "  where state_cd=? and off_cd=? and flag =?  ";

            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, max);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.setString(4, flag);
            ps.executeUpdate();

            //Remove all Unused Receipt,skip it for prefill at 950 
            if (!prefill) {
                sql = "Delete from " + TableList.TM_OFFICE_RCPT_NO_AVAILABLE
                        + " where state_cd=? and off_cd=? and flag =? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setInt(2, offCd);
                ps.setString(3, flag);
                ps.executeUpdate();
            }

            //Insert records into tm_office_rcpt_no_available
            sql = "insert into tm_office_rcpt_no_available "
                    + " select a.state_cd,a.off_cd,a.flag,to_char(CURRENT_DATE,'YYMM'), "
                    + "  b.rcpt_no,?,current_timestamp "
                    + "  from 	tm_office_rcpt_no a,(SELECT  rcpt_no "
                    + "  from generate_series(?, ?) rcpt_no) b "
                    + "  where a.state_cd=?  and a.off_cd=? and a.flag=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(empCode));
            ps.setInt(2, min);
            ps.setInt(3, max);
            ps.setString(4, stateCd);
            ps.setInt(5, offCd);
            ps.setString(6, flag);
            ps.executeUpdate();

            tmgr.commit();

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during Unique Number Generation, "
                    + " Please Contact to the System Administrator");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
    }

    public static void fillUpRcptPoolNextMonth(String stateCd, int offCd, String flag) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        String sql;

        try {
            tmgr = new TransactionManager("fillUpDocumentPool");

            sql = "select  prefix  from tm_office_rcpt_no_available "
                    + "  where  state_cd=?  and  off_cd=? and flag =? "
                    + " and prefix =to_char(CURRENT_DATE+ interval '1 month','YYMM')  LIMIT 1 ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            ps.setString(3, flag);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                return;
            }

            sql = "insert into tm_office_rcpt_no_available "
                    + " select a.state_cd,a.off_cd,a.flag,to_char(CURRENT_DATE + interval '1 month','YYMM'), "
                    + "  b.rcpt_no,?,current_timestamp "
                    + "  from 	tm_office_rcpt_no a,(SELECT  rcpt_no "
                    + "  from generate_series(?, ?) rcpt_no) b "
                    + "  where a.state_cd=?  and a.off_cd=? and a.flag=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Util.getEmpCodeLong());
            ps.setInt(2, 1);
            ps.setInt(3, 999);
            ps.setString(4, stateCd);
            ps.setInt(5, offCd);
            ps.setString(6, flag);
            ps.executeUpdate();
            tmgr.commit();

        } catch (SQLException | VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during Unique Number Generation, "
                    + " Please Contact to the System Administrator");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
    }

    /**
     * @author Kartikey Singh Need to check for scenarios when empCode = null
     */
    public static void fillUpRcptPoolNextMonth(String stateCd, int offCd, String flag, String empCode) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        String sql;

        try {
            tmgr = new TransactionManager("fillUpDocumentPool");

            sql = "select  prefix  from tm_office_rcpt_no_available "
                    + "  where  state_cd=?  and  off_cd=? and flag =? "
                    + " and prefix =to_char(CURRENT_DATE+ interval '1 month','YYMM')  LIMIT 1 ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            ps.setString(3, flag);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                return;
            }

            sql = "insert into tm_office_rcpt_no_available "
                    + " select a.state_cd,a.off_cd,a.flag,to_char(CURRENT_DATE + interval '1 month','YYMM'), "
                    + "  b.rcpt_no,?,current_timestamp "
                    + "  from 	tm_office_rcpt_no a,(SELECT  rcpt_no "
                    + "  from generate_series(?, ?) rcpt_no) b "
                    + "  where a.state_cd=?  and a.off_cd=? and a.flag=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(empCode));
            ps.setInt(2, 1);
            ps.setInt(3, 999);
            ps.setString(4, stateCd);
            ps.setInt(5, offCd);
            ps.setString(6, flag);
            ps.executeUpdate();
            tmgr.commit();

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during Unique Number Generation, "
                    + " Please Contact to the System Administrator");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
    }

    public static String getUniqueOfficeRcptNo(TransactionManager tmgr, String stateCd) throws VahanException {
        String rcptNo = null;
        try {
            if (stateCd == null || stateCd.isEmpty() || stateCd.length() > 2 || stateCd.length() < 2) {
                throw new VahanException("Something went wrong, please try again.");
            }

            String strSQL = "SELECT to_char(CURRENT_DATE,'YYMMDD') || ? || lpad((floor(random() * 9) + 1)::varchar ||"
                    + " nextval('rcpt_no_v4_seq')::varchar, 8, '0') AS rcpt_no";
            PreparedStatement psmt = tmgr.prepareStatement(strSQL);
            psmt.setString(1, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                rcptNo = rs.getString("rcpt_no");
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please try again.");
        } catch (Exception e) {

            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please try again.");
        }
        return rcptNo;
    }

//    public static long getUniqueInstrumentNo(TransactionManager tmgr) throws VahanException {
//
//        String instrumentNo = null;
//        String tempSeries = "";
//        long tempSequence = 0;
//        try {
//            String strSQL = "UPDATE vm_instrument_no SET SEQUENCE_NO = SEQUENCE_NO + 1";
//            PreparedStatement psmt = tmgr.prepareStatement(strSQL);
//            psmt.executeUpdate();
//
//            strSQL = "SELECT PREFIX FROM vm_instrument_no";
//            psmt = tmgr.prepareStatement(strSQL);
//            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
//
//            java.util.Calendar cal = java.util.Calendar.getInstance();
//            String monthYear = String.valueOf(cal.get(java.util.Calendar.YEAR)).substring(2, 4) + String.format("%02d", cal.get(java.util.Calendar.MONTH) + 1);
//            strSQL = "SELECT to_char(current_date, 'YYMM') as curr_month";
//            psmt = tmgr.prepareStatement(strSQL);
//            RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
//            if (rs1.next()) {
//                monthYear = rs1.getString("curr_month");
//            }
//
//            if (rs.next()) {
//                if (!monthYear.equalsIgnoreCase(rs.getString("PREFIX").trim())) {
//                    if (Integer.parseInt(monthYear) < Integer.parseInt(rs.getString("PREFIX"))) {
//                        throw new VahanException("Date/Time not synchronized");
//                    }
//                    strSQL = "UPDATE vm_instrument_no SET PREFIX = ?, SEQUENCE_NO = 1";
//                    psmt = tmgr.prepareStatement(strSQL);
//                    psmt.setString(1, monthYear);
//                    psmt.executeUpdate();
//                }
//            } else {
//                strSQL = "INSERT INTO vm_instrument_no VALUES (?, 1)";
//                psmt = tmgr.prepareStatement(strSQL);
//                psmt.setString(1, monthYear);
//                psmt.executeUpdate();
//            }
//            rs.close();
//
//            strSQL = "SELECT PREFIX, SEQUENCE_NO FROM vm_instrument_no";
//            psmt = tmgr.prepareStatement(strSQL);
//            rs = tmgr.fetchDetachedRowSet_No_release();
//            if (rs.next()) {
//                tempSeries = rs.getString("PREFIX").trim();
//                tempSequence = rs.getLong("SEQUENCE_NO");
//            }
//
//            String format = String.format("%%0%dd", 6);
//            String z = String.format(format, tempSequence);
//            instrumentNo = tempSeries.concat(z);
//
//        } catch (Exception e) {
//            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
//            throw new VahanException("Something went wrong, please try again.");
//        }
//
//        return Long.parseLong(instrumentNo);
//    }
    public static String getUniquePermitNo(TransactionManager tmgr, String stateCd, int off_cd, int permitType, int PermitCatg, String flag) throws VahanException {
        String strSQL = "";
        String pmtNo = "";
        String tempSeries = "";
        PreparedStatement psmt = null;
        RowSet rs = null;
        boolean isPmtNoFound = false;
        try {

            if (stateCd == null || off_cd == 0) {
                throw new VahanException("Something went wrong, please try again.");
            }

            strSQL = "SELECT date_part('year',CURRENT_DATE)::numeric >=2020 yr";
            tmgr.prepareStatement(strSQL);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (rs.getBoolean("yr")) {
                    return getUniqueNumber(tmgr, stateCd, permitType, flag);
                }
            }
            strSQL = "SELECT * FROM vsm_permit_no WHERE state_cd = ? AND off_cd = ? AND permit_type = ? AND permit_catg = ? AND flag = ? ";
            psmt = tmgr.prepareStatement(strSQL);
            psmt.setString(1, stateCd);
            psmt.setInt(2, off_cd);
            psmt.setInt(3, permitType);
            psmt.setInt(4, PermitCatg);
            psmt.setString(5, flag);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                isPmtNoFound = true;
                strSQL = "UPDATE vsm_permit_no SET SEQUENCE_NO = SEQUENCE_NO + 1 WHERE state_cd = ? AND off_cd = ? AND permit_type = ? AND permit_catg = ? AND flag = ? ";
                psmt = tmgr.prepareStatement(strSQL);
                psmt.setString(1, stateCd);
                psmt.setInt(2, off_cd);
                psmt.setInt(3, permitType);
                psmt.setInt(4, PermitCatg);
                psmt.setString(5, flag);
                psmt.executeUpdate();

                java.util.Calendar cal = java.util.Calendar.getInstance();
                int Year = cal.get(java.util.Calendar.YEAR);
                strSQL = "SELECT to_char(current_date, 'YYYY')::numeric as curr_year";
                psmt = tmgr.prepareStatement(strSQL);
                RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
                if (rs1.next()) {
                    Year = rs1.getInt("curr_year");
                }
                if (Year != rs.getInt("prefix_year")) {
                    if (Year < rs.getInt("prefix_year")) {
                        throw new VahanException("Date/Time not synchronized");
                    }
                    strSQL = "UPDATE vsm_permit_no SET PREFIX_YEAR = ?, SEQUENCE_NO = 0";
                    psmt = tmgr.prepareStatement(strSQL);
                    psmt.setInt(1, Year);
                    psmt.executeUpdate();

                    strSQL = "UPDATE vsm_permit_no SET SEQUENCE_NO = 1 WHERE state_cd = ? AND off_cd = ? AND permit_type = ? AND permit_catg = ? AND flag = ? ";
                    psmt = tmgr.prepareStatement(strSQL);
                    psmt.setString(1, stateCd);
                    psmt.setInt(2, off_cd);
                    psmt.setInt(3, permitType);
                    psmt.setInt(4, PermitCatg);
                    psmt.setString(5, flag);
                    psmt.executeUpdate();
                }
                rs.close();

                strSQL = "SELECT PREFIX, PREFIX_YEAR, SEQUENCE_NO FROM vsm_permit_no WHERE state_cd = ? AND off_cd = ? AND permit_type = ? AND permit_catg = ? AND flag = ? ";
                psmt = tmgr.prepareStatement(strSQL);
                psmt.setString(1, stateCd);
                psmt.setInt(2, off_cd);
                psmt.setInt(3, permitType);
                psmt.setInt(4, PermitCatg);
                psmt.setString(5, flag);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    tempSeries = rs.getString("PREFIX").trim() + "/" + rs.getInt("PREFIX_YEAR") + "/" + rs.getLong("SEQUENCE_NO");
                } else {
                    isPmtNoFound = false;
                }

                pmtNo = stateCd + "/" + off_cd + "/" + tempSeries;
            } else {
                strSQL = "INSERT INTO vsm_permit_no(\n"
                        + "            state_cd, off_cd, permit_type, permit_catg, flag, prefix, prefix_year, \n"
                        + "            sequence_no)\n"
                        + "    SELECT  state_cd, ?, permit_type, permit_catg, flag, prefix, prefix_year, \n"
                        + "            ?\n"
                        + "    FROM vsm_permit_no WHERE state_cd = ? AND permit_type = ? AND permit_catg = ? AND flag = ? limit 1";
                psmt = tmgr.prepareStatement(strSQL);
                psmt.setInt(1, off_cd);
                psmt.setInt(2, 0);
                psmt.setString(3, stateCd);
                psmt.setInt(4, permitType);
                psmt.setInt(5, PermitCatg);
                psmt.setString(6, flag);
                int value = psmt.executeUpdate();
                if (value == 0) {
                    pmtNo = null;
                } else {
                    pmtNo = getUniquePermitNo(tmgr, stateCd, off_cd, permitType, PermitCatg, flag);
                }
            }
            rs.close();
            psmt.close();
            rs = null;
            psmt = null;
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please try again.");
        }
        return pmtNo;
    }

    /**
     * @authoer Kartikey Singh
     */
    public static String getUniquePermitNo(TransactionManager tmgr, String stateCd, int off_cd, int permitType, int PermitCatg, String flag, String empCode) throws VahanException {
        String strSQL = "";
        String pmtNo = "";
        String tempSeries = "";
        PreparedStatement psmt = null;
        RowSet rs = null;
        boolean isPmtNoFound = false;
        try {

            if (stateCd == null || off_cd == 0) {
                throw new VahanException("Something went wrong, please try again.");
            }

            strSQL = "SELECT date_part('year',CURRENT_DATE)::numeric >=2020 yr";
            tmgr.prepareStatement(strSQL);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (rs.getBoolean("yr")) {
                    return getUniqueNumber(tmgr, stateCd, permitType, flag, empCode);
                }
            }
            strSQL = "SELECT * FROM vsm_permit_no WHERE state_cd = ? AND off_cd = ? AND permit_type = ? AND permit_catg = ? AND flag = ? ";
            psmt = tmgr.prepareStatement(strSQL);
            psmt.setString(1, stateCd);
            psmt.setInt(2, off_cd);
            psmt.setInt(3, permitType);
            psmt.setInt(4, PermitCatg);
            psmt.setString(5, flag);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                isPmtNoFound = true;
                strSQL = "UPDATE vsm_permit_no SET SEQUENCE_NO = SEQUENCE_NO + 1 WHERE state_cd = ? AND off_cd = ? AND permit_type = ? AND permit_catg = ? AND flag = ? ";
                psmt = tmgr.prepareStatement(strSQL);
                psmt.setString(1, stateCd);
                psmt.setInt(2, off_cd);
                psmt.setInt(3, permitType);
                psmt.setInt(4, PermitCatg);
                psmt.setString(5, flag);
                psmt.executeUpdate();

                java.util.Calendar cal = java.util.Calendar.getInstance();
                int Year = cal.get(java.util.Calendar.YEAR);
                strSQL = "SELECT to_char(current_date, 'YYYY')::numeric as curr_year";
                psmt = tmgr.prepareStatement(strSQL);
                RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
                if (rs1.next()) {
                    Year = rs1.getInt("curr_year");
                }
                if (Year != rs.getInt("prefix_year")) {
                    if (Year < rs.getInt("prefix_year")) {
                        throw new VahanException("Date/Time not synchronized");
                    }
                    strSQL = "UPDATE vsm_permit_no SET PREFIX_YEAR = ?, SEQUENCE_NO = 0";
                    psmt = tmgr.prepareStatement(strSQL);
                    psmt.setInt(1, Year);
                    psmt.executeUpdate();

                    strSQL = "UPDATE vsm_permit_no SET SEQUENCE_NO = 1 WHERE state_cd = ? AND off_cd = ? AND permit_type = ? AND permit_catg = ? AND flag = ? ";
                    psmt = tmgr.prepareStatement(strSQL);
                    psmt.setString(1, stateCd);
                    psmt.setInt(2, off_cd);
                    psmt.setInt(3, permitType);
                    psmt.setInt(4, PermitCatg);
                    psmt.setString(5, flag);
                    psmt.executeUpdate();
                }
                rs.close();

                strSQL = "SELECT PREFIX, PREFIX_YEAR, SEQUENCE_NO FROM vsm_permit_no WHERE state_cd = ? AND off_cd = ? AND permit_type = ? AND permit_catg = ? AND flag = ? ";
                psmt = tmgr.prepareStatement(strSQL);
                psmt.setString(1, stateCd);
                psmt.setInt(2, off_cd);
                psmt.setInt(3, permitType);
                psmt.setInt(4, PermitCatg);
                psmt.setString(5, flag);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    tempSeries = rs.getString("PREFIX").trim() + "/" + rs.getInt("PREFIX_YEAR") + "/" + rs.getLong("SEQUENCE_NO");
                } else {
                    isPmtNoFound = false;
                }

                pmtNo = stateCd + "/" + off_cd + "/" + tempSeries;
            } else {
                strSQL = "INSERT INTO vsm_permit_no(\n"
                        + "            state_cd, off_cd, permit_type, permit_catg, flag, prefix, prefix_year, \n"
                        + "            sequence_no)\n"
                        + "    SELECT  state_cd, ?, permit_type, permit_catg, flag, prefix, prefix_year, \n"
                        + "            ?\n"
                        + "    FROM vsm_permit_no WHERE state_cd = ? AND permit_type = ? AND permit_catg = ? AND flag = ? limit 1";
                psmt = tmgr.prepareStatement(strSQL);
                psmt.setInt(1, off_cd);
                psmt.setInt(2, 0);
                psmt.setString(3, stateCd);
                psmt.setInt(4, permitType);
                psmt.setInt(5, PermitCatg);
                psmt.setString(6, flag);
                int value = psmt.executeUpdate();
                if (value == 0) {
                    pmtNo = null;
                } else {
                    pmtNo = getUniquePermitNo(tmgr, stateCd, off_cd, permitType, PermitCatg, flag, empCode);
                }
            }
            rs.close();
            psmt.close();
            rs = null;
            psmt = null;
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please try again.");
        }
        return pmtNo;
    }

    public static String getUniqueNumber(TransactionManager tmgr, String stateCd, int permitType, String flag) throws VahanException {
        String unqNo = null;
        String num;

        try {
            String sql = "select  state_cd||to_char(CURRENT_DATE,'YYYY')||prefix||document_no as document_no,document_no as num"
                    + " from TM_DOCUMENT_NO_AVAILABLE "
                    + "  where  state_cd=?  and  permit_type=? and flag =? "
                    + " and prefix_year=date_part('year',CURRENT_DATE) "
                    + " order by entered_on,document_no LIMIT 1 "
                    + "  FOR UPDATE SKIP LOCKED ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, permitType);
            ps.setString(3, flag);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                unqNo = rs.getString("document_no");
                num = rs.getString("num");

                sql = "Delete "
                        + " from  " + TableList.TM_DOCUMENT_NO_AVAILABLE
                        + " where  state_cd=?  and  permit_type=? and flag =? and document_no=? ";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setInt(2, permitType);
                ps.setString(3, flag);
                ps.setString(4, num);
                ps.executeUpdate();

                sql = "insert into  "
                        + TableList.TM_DOCUMENT_NO_ALLOTED
                        + " values(?,?,current_timestamp) ";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, unqNo);
                ps.setLong(2, Util.getEmpCodeLong());
                ps.executeUpdate();

                if (num.substring(0, 4).equals("9750")) {
                    fillUpDocumentPool(stateCd, permitType, flag, true);
                }
                return unqNo;
            } else {
                fillUpDocumentPool(stateCd, permitType, flag, false);
                unqNo = getUniqueNumber(tmgr, stateCd, permitType, flag);

            }

        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during Unique Number Generation, "
                    + " Please Contact to the System Administrator");
        }

        return unqNo;

    }

    /**
     * @author Kartikey Singh
     */
    public static String getUniqueNumber(TransactionManager tmgr, String stateCd, int permitType, String flag, String empCode) throws VahanException {
        String unqNo = null;
        String num;

        try {
            String sql = "select  state_cd||to_char(CURRENT_DATE,'YYYY')||prefix||document_no as document_no,document_no as num"
                    + " from TM_DOCUMENT_NO_AVAILABLE "
                    + "  where  state_cd=?  and  permit_type=? and flag =? "
                    + " and prefix_year=date_part('year',CURRENT_DATE) "
                    + " order by entered_on,document_no LIMIT 1 "
                    + "  FOR UPDATE SKIP LOCKED ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, permitType);
            ps.setString(3, flag);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                unqNo = rs.getString("document_no");
                num = rs.getString("num");

                sql = "Delete "
                        + " from  " + TableList.TM_DOCUMENT_NO_AVAILABLE
                        + " where  state_cd=?  and  permit_type=? and flag =? and document_no=? ";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setInt(2, permitType);
                ps.setString(3, flag);
                ps.setString(4, num);
                ps.executeUpdate();

                sql = "insert into  "
                        + TableList.TM_DOCUMENT_NO_ALLOTED
                        + " values(?,?,current_timestamp) ";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, unqNo);
                ps.setLong(2, Long.parseLong(empCode));
                ps.executeUpdate();

                if (num.substring(0, 4).equals("9750")) {
                    fillUpDocumentPool(stateCd, permitType, flag, true, empCode);
                }
                return unqNo;
            } else {
                fillUpDocumentPool(stateCd, permitType, flag, false, empCode);
                unqNo = getUniqueNumber(tmgr, stateCd, permitType, flag, empCode);

            }

        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during Unique Number Generation, "
                    + " Please Contact to the System Administrator");
        }

        return unqNo;

    }

    public static void fillUpDocumentPool(String stateCd, int permitType, String flag, boolean prefill) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        String sql;

        try {
            tmgr = new TransactionManager("fillUpDocumentPool");

            if (!prefill) {
                sql = "update "
                        + " tm_document_no"
                        + " set permit_type=permit_type "
                        + " where state_cd=? and permit_type=? and flag =? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setInt(2, permitType);
                ps.setString(3, flag);
                int i = ps.executeUpdate();
                if (i <= 0) {
                    LOGGER.error("No Valid Configuaration Found For Generating Number stateCd =" + stateCd
                            + " permitType= " + permitType + " flag: " + flag);
                    throw new VahanException("No Valid Configuaration Found For Generating Number");
                }

                //For Double Check
                sql = "select  state_cd||to_char(CURRENT_DATE,'YYYY')||prefix||document_no as document_no,document_no as num"
                        + " from TM_DOCUMENT_NO_AVAILABLE "
                        + "  where  state_cd=?  and  permit_type=? and flag =? "
                        + " and prefix_year=date_part('year',CURRENT_DATE) "
                        + " LIMIT 1 ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setInt(2, permitType);
                ps.setString(3, flag);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    return;
                }
            }

            //update tm_document_no to new prefix_yymm,sequence_no and sequence_flag
            sql = "update tm_document_no b "
                    + "set  sequence_no=9999 , "
                    + "sequence_flag = (case "
                    + "	 when a.prefix_year != date_part('year',CURRENT_DATE)::numeric then 'A'  "
                    + "  when length(a.sequence_flag)=1 and a.sequence_flag='Z' then 'AA' "
                    + "  when length(a.sequence_flag)=1 and a.sequence_flag in ('H','N') then CHR(ASCII(a.sequence_flag) +2) "
                    + "  when length(a.sequence_flag)=1 then CHR(ASCII(a.sequence_flag) +1) "
                    + "  when length(a.sequence_flag)=2 and a.sequence_flag ='ZZ' then 'A' "
                    + "	 when length(a.sequence_flag)=2 and substring(a.sequence_flag,2,1) ='Z' and substring(a.sequence_flag,1,1) "
                    + "  not in('H','N') "
                    + "  then CHR(ASCII(substring(a.sequence_flag,1,1))+1)||'A' "
                    + "  when length(a.sequence_flag)=2 and substring(a.sequence_flag,2,1) ='Z' and substring(a.sequence_flag,1,1) "
                    + "  in('H','N') "
                    + "  then CHR(ASCII(substring(a.sequence_flag,1,1))+2)||'A' "
                    + "  when length(a.sequence_flag)=2 and substring(a.sequence_flag,2,1) in('H','N') "
                    + "  then substring(a.sequence_flag,1,1)|| CHR(ASCII(substring(a.sequence_flag,2,1))+2) "
                    + "   when length(a.sequence_flag)=2 and  substring(a.sequence_flag,2,1) !='Z' "
                    + "  then substring(a.sequence_flag,1,1)|| CHR(ASCII(substring(a.sequence_flag,2,1))+1) "
                    + "  end) ,"
                    + "  prefix_year =date_part('year',CURRENT_DATE)::numeric "
                    + "  from tm_document_no a  where a.state_cd=b.state_cd and a.permit_type=b.permit_type and a.flag =b.flag "
                    + "  and b.state_cd=? and b.permit_type=? and b.flag =?  ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, permitType);
            ps.setString(3, flag);
            ps.executeUpdate();

            //Remove all Unused Document Numbers
            sql = "Delete from " + TableList.TM_DOCUMENT_NO_AVAILABLE
                    + " where state_cd=? and permit_type=? and flag =? "
                    + " and prefix_year != date_part('year',CURRENT_DATE)::numeric";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, permitType);
            ps.setString(3, flag);
            ps.executeUpdate();

            //Insert records into TM_DOCUMENT_NO_AVAILABLE
            sql = "insert into"
                    + " tm_document_no_available "
                    + " select a.state_cd,a.prefix_year,a.prefix,b.document_no||a.sequence_flag, "
                    + "  a.flag,a.permit_type,?,current_timestamp "
                    + "  from 	tm_document_no a,(SELECT lpad(regn_seq::varchar, 4, '0') as document_no "
                    + "  from generate_series(1, 9999) regn_seq) b "
                    + "  where a.state_cd=?  and a.permit_type=? and a.flag=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Util.getEmpCodeLong());
            ps.setString(2, stateCd);
            ps.setInt(3, permitType);
            ps.setString(4, flag);
            ps.executeUpdate();

            tmgr.commit();

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during Unique Number Generation, "
                    + " Please Contact to the System Administrator");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
    }

    /**
     * @author Kartikey Singh
     */
    public static void fillUpDocumentPool(String stateCd, int permitType, String flag, boolean prefill, String empCode) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        String sql;

        try {
            tmgr = new TransactionManager("fillUpDocumentPool");

            if (!prefill) {
                sql = "update "
                        + " tm_document_no"
                        + " set permit_type=permit_type "
                        + " where state_cd=? and permit_type=? and flag =? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setInt(2, permitType);
                ps.setString(3, flag);
                int i = ps.executeUpdate();
                if (i <= 0) {
                    LOGGER.error("No Valid Configuaration Found For Generating Number stateCd =" + stateCd
                            + " permitType= " + permitType + " flag: " + flag);
                    throw new VahanException("No Valid Configuaration Found For Generating Number");
                }

                //For Double Check
                sql = "select  state_cd||to_char(CURRENT_DATE,'YYYY')||prefix||document_no as document_no,document_no as num"
                        + " from TM_DOCUMENT_NO_AVAILABLE "
                        + "  where  state_cd=?  and  permit_type=? and flag =? "
                        + " and prefix_year=date_part('year',CURRENT_DATE) "
                        + " LIMIT 1 ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setInt(2, permitType);
                ps.setString(3, flag);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    return;
                }
            }

            //update tm_document_no to new prefix_yymm,sequence_no and sequence_flag
            sql = "update tm_document_no b "
                    + "set  sequence_no=9999 , "
                    + "sequence_flag = (case "
                    + "	 when a.prefix_year != date_part('year',CURRENT_DATE)::numeric then 'A'  "
                    + "  when length(a.sequence_flag)=1 and a.sequence_flag='Z' then 'AA' "
                    + "  when length(a.sequence_flag)=1 and a.sequence_flag in ('H','N') then CHR(ASCII(a.sequence_flag) +2) "
                    + "  when length(a.sequence_flag)=1 then CHR(ASCII(a.sequence_flag) +1) "
                    + "  when length(a.sequence_flag)=2 and a.sequence_flag ='ZZ' then 'A' "
                    + "	 when length(a.sequence_flag)=2 and substring(a.sequence_flag,2,1) ='Z' and substring(a.sequence_flag,1,1) "
                    + "  not in('H','N') "
                    + "  then CHR(ASCII(substring(a.sequence_flag,1,1))+1)||'A' "
                    + "  when length(a.sequence_flag)=2 and substring(a.sequence_flag,2,1) ='Z' and substring(a.sequence_flag,1,1) "
                    + "  in('H','N') "
                    + "  then CHR(ASCII(substring(a.sequence_flag,1,1))+2)||'A' "
                    + "  when length(a.sequence_flag)=2 and substring(a.sequence_flag,2,1) in('H','N') "
                    + "  then substring(a.sequence_flag,1,1)|| CHR(ASCII(substring(a.sequence_flag,2,1))+2) "
                    + "   when length(a.sequence_flag)=2 and  substring(a.sequence_flag,2,1) !='Z' "
                    + "  then substring(a.sequence_flag,1,1)|| CHR(ASCII(substring(a.sequence_flag,2,1))+1) "
                    + "  end) ,"
                    + "  prefix_year =date_part('year',CURRENT_DATE)::numeric "
                    + "  from tm_document_no a  where a.state_cd=b.state_cd and a.permit_type=b.permit_type and a.flag =b.flag "
                    + "  and b.state_cd=? and b.permit_type=? and b.flag =?  ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, permitType);
            ps.setString(3, flag);
            ps.executeUpdate();

            //Remove all Unused Document Numbers
            sql = "Delete from " + TableList.TM_DOCUMENT_NO_AVAILABLE
                    + " where state_cd=? and permit_type=? and flag =? "
                    + " and prefix_year != date_part('year',CURRENT_DATE)::numeric";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, permitType);
            ps.setString(3, flag);
            ps.executeUpdate();

            //Insert records into TM_DOCUMENT_NO_AVAILABLE
            sql = "insert into"
                    + " tm_document_no_available "
                    + " select a.state_cd,a.prefix_year,a.prefix,b.document_no||a.sequence_flag, "
                    + "  a.flag,a.permit_type,?,current_timestamp "
                    + "  from 	tm_document_no a,(SELECT lpad(regn_seq::varchar, 4, '0') as document_no "
                    + "  from generate_series(1, 9999) regn_seq) b "
                    + "  where a.state_cd=?  and a.permit_type=? and a.flag=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(empCode));
            ps.setString(2, stateCd);
            ps.setInt(3, permitType);
            ps.setString(4, flag);
            ps.executeUpdate();

            tmgr.commit();

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during Unique Number Generation, "
                    + " Please Contact to the System Administrator");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
    }

    public static int generateHashCode(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        int hashCode = 0;
        if (str != null && str.length() > 0) {
            String md5String = MD5(str);
            long asciiSum = getAsciiSum(md5String);
            hashCode = (int) (asciiSum % 10);
        }
        return hashCode;
    }

    public static String MD5(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md;
        md = MessageDigest.getInstance("MD5");
        byte[] md5hash = new byte[32];
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        md5hash = md.digest();
        return convertToHex(md5hash);
    }

    public static long getAsciiSum(String str) {
        long asciiSum = 0;
        if (str != null && str.length() > 0) {
            for (int i = 0; i < str.length(); i++) {
                asciiSum = asciiSum + str.charAt(i);
            }
        }
        return asciiSum;
    }

    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    //added by prashant
    public static String getSeatCodeAgainstAction(int actionCode, TransactionManager tmgr) {
        String seatCode = null;
        String sqlTmActionSQL = "SELECT * FROM TM_ACTION WHERE ACTION_CD = ?";
        try {
            PreparedStatement prstmt = tmgr.prepareStatement(sqlTmActionSQL);
            prstmt.setInt(1, actionCode);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                seatCode = rs.getString("action_descr");
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        return seatCode;

    }

    public static long getOwnerFrom(TransactionManager tmgr, String regn_no, String vhTableName) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        long owner_from = 0;
        RowSet rs = null;
        try {

            sql = "SELECT date (max(owner_upto))+ integer '1' as newOwnerFrom ,"
                    + " regn_no as new_owner_from FROM " + vhTableName + " where state_cd = ? "
                    + "  group by regn_no having regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, regn_no);
            rs = tmgr.fetchDetachedRowSet_No_release();
            int cntr = 1;
            while (rs.next()) { //if any record is exist
                if (cntr > 1) {
                    throw new VahanException("Owner Details founds in Multiple Offices, please contact State Administrator.");
                }
                if (rs.getDate("newOwnerFrom") != null) {
                    owner_from = rs.getDate("newOwnerFrom").getTime();
                }
                cntr++;
            }

            if (owner_from == 0) {
                sql = "SELECT regn_dt FROM  " + TableList.VT_OWNER + " where regn_no=? and state_cd = ? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regn_no);
                ps.setString(2, Util.getUserStateCode());
                rs = tmgr.fetchDetachedRowSet_No_release();

                cntr = 1;
                while (rs.next()) { //if any record is exist
                    if (cntr > 1) {
                        throw new VahanException("Owner Details founds in Multiple Offices, please contact State Administrator.");
                    }
                    owner_from = rs.getDate("regn_dt").getTime();
                    cntr++;
                }
            }

        } catch (VahanException ve) {
            throw ve;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return owner_from;
    } // end of getOwnerFrom

    public static String getLabelDescr(Map<String, Object> list, int value) {
        String descr = null;
        for (Object key : list.keySet()) {
            if (Integer.parseInt(list.get(key).toString()) == value) {
                descr = key.toString();
                break;
            }
        }
        return descr;
    }//end of getLabelDescr

    public static String getLableFromSelectedListToShow(List list, String selectedValue) {
        String lable = null;
        for (int i = 0; i < list.size(); i++) {
            SelectItem st = (SelectItem) list.get(i);
            if (st.getValue().toString().equalsIgnoreCase(selectedValue)) {
                lable = st.getLabel();
                break;
            }
        }
        return lable;
    }

    /**
     * Creating copy of getLableFromSelectedListToShow as we cannot have object
     * of javax.Faces passed to Rest
     *
     * @author Deependra Singh
     */
    public static String getCustomLableFromSelectedListToShow(List list, String selectedValue) {
        String lable = null;
        String str = "";
        for (int i = 0; i < list.size(); i++) {
            HashMap map = new HashMap();
            map = ((HashMap) list.get(i));
            str = (String) map.get("value");
            if (str.equalsIgnoreCase(selectedValue)) {
                lable = (String) map.get("label");
                break;// outer;
            }
        }
        return lable;
    }

    public static Map<Object, Object> getOfficeListOfState(String state_cd) {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        Map<Object, Object> officeCodeList = new LinkedHashMap<Object, Object>();

        try {
            tmgr = new TransactionManagerReadOnly("getOfficeListOfState()");
            sql = "select off_cd,off_name,state_cd from " + TableList.TM_OFFICE + " where state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd.toUpperCase());

            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next())//found
            {
                officeCodeList.put(rs.getInt("off_cd"), rs.getString("off_name"));
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return officeCodeList;
    }

    /**
     * *
     *
     * @param tableName This parameter accept the Table Name by which you will
     * get the records.
     * @param code This parameter is the column name of the table which provide
     * the numetic code of the descriptions.
     * @param descr This parameter is the column name of the table which provide
     * descriptions against the numetic code.
     * @param conditionKey This parameter is used for mathcing key for 'WHERE'
     * condition use in the sql query
     * @param conditionValue This parameter is used for mathcing value for the
     * condtion key for 'WHERE' condition use in the sql query
     * @return This Method return the MAP of key-value pairs of desired
     * code-descr from table
     */
    public static Map<Object, Object> getCodeDescr(String tableName, String code, String descr, String conditionKey, String conditionValue) {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;

        if (tableName != null && code != null && descr != null && conditionKey != null && conditionValue != null) {
            conditionKey = conditionKey.trim();
            conditionValue = conditionValue.trim();
            descr = descr.trim();
            code = code.trim();
        }

        Map<Object, Object> keyValueList = new LinkedHashMap<>();

        try {
            tmgr = new TransactionManagerReadOnly("getCodeDescr()");
            sql = "SELECT " + code + "," + descr + " from " + tableName + " where " + conditionKey + " = ? order by " + descr;
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, conditionValue);

            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next())//found
            {
                keyValueList.put(rs.getString(descr), rs.getInt(code));
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return keyValueList;
    }

    public static Map<Object, Object> getPurCodesDescrMap(String purCodes) {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        Map<Object, Object> keyValueList = new LinkedHashMap<>();
        try {
            tmgr = new TransactionManagerReadOnly("getPurCodesDescrMap()");
            sql = "SELECT pur_cd,descr from " + TableList.TM_PURPOSE_MAST + " where  pur_cd in (" + purCodes + ") order by descr";
            ps = tmgr.prepareStatement(sql);
            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next())//found
            {
                keyValueList.put(rs.getString("descr"), rs.getInt("pur_cd"));
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return keyValueList;
    }

    public static void insertIntoVaStatus(TransactionManager tmgr, Status_dobj dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {

            sql = "INSERT INTO va_status(state_cd, off_cd, appl_no, pur_cd, flow_slno,"
                    + " file_movement_slno,action_cd, seat_cd, cntr_id, status, "
                    + " office_remark, public_remark,file_movement_type, emp_cd, op_dt)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getState_cd());
            ps.setInt(2, dobj.getOff_cd());
            ps.setString(3, dobj.getAppl_no());
            ps.setInt(4, dobj.getPur_cd());
            ps.setInt(5, dobj.getFlow_slno());
            ps.setInt(6, dobj.getFile_movement_slno());
            ps.setInt(7, dobj.getAction_cd());
            ps.setString(8, dobj.getSeat_cd());
            ps.setString(9, dobj.getCntr_id());
            ps.setString(10, dobj.getStatus());
            ps.setString(11, dobj.getOffice_remark());
            ps.setString(12, dobj.getPublic_remark());
            ps.setString(13, dobj.getFile_movement_type());
            ps.setLong(14, dobj.getEmp_cd());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    } // end of insertIntoVaStatus

    public static void insertIntoVaDetails(TransactionManager tmgr, Status_dobj dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        int pos = 1;
        try {
            String strSQL = "SELECT appl_no from " + TableList.VA_DETAILS + " where appl_no = ? and regn_no != ? ";
            PreparedStatement psmt = tmgr.prepareStatement(strSQL);
            psmt.setString(1, dobj.getAppl_no());
            psmt.setString(2, dobj.getRegn_no());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                LOGGER.error("WhereAmI:" + tmgr.getWhereiam() + ", Application No " + dobj.getAppl_no() + " already exist.");
                throw new VahanException("Application No already exist, please try again.");
            }

            sql = "INSERT INTO " + TableList.VA_DETAILS + " (appl_no, pur_cd, appl_dt, regn_no, user_id, user_type, entry_ip,entry_status,"
                    + "  confirm_ip, confirm_status, confirm_date, state_cd,off_cd) "
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp, ?, ?)";

            ps = tmgr.prepareStatement(sql);
            ps.setString(pos++, dobj.getAppl_no());
            ps.setInt(pos++, dobj.getPur_cd());
            ps.setTimestamp(pos++, dobj.getAppl_date());
            ps.setString(pos++, dobj.getRegn_no());
            ps.setString(pos++, dobj.getUser_id());
            ps.setString(pos++, dobj.getUser_type());
            ps.setString(pos++, dobj.getEntry_ip());
            ps.setString(pos++, dobj.getEntry_status());
            ps.setString(pos++, dobj.getConfirm_ip());
            ps.setString(pos++, dobj.getConfirm_status());
            ps.setString(pos++, dobj.getState_cd());
            ps.setInt(pos++, dobj.getOff_cd());
            ps.executeUpdate();
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    } // end of insertIntoVaDetails

    public static void updateApprovedStatus(TransactionManager tmgr, Status_dobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "UPDATE " + TableList.VA_DETAILS
                + "   SET  entry_status=?,confirm_ip=?,confirm_date=current_timestamp"
                + " WHERE  appl_no=? and pur_cd=?";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getEntry_status());
        ps.setString(2, Util.getClientIpAdress());
        ps.setString(3, dobj.getAppl_no());
        ps.setInt(4, dobj.getPur_cd());
        ps.executeUpdate();
    } // end of updateApprovedStatus

    /**
     * @author Kartikey Singh
     */
    public static void updateApprovedStatus(TransactionManager tmgr, Status_dobj dobj, String clientIpAddress) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "UPDATE " + TableList.VA_DETAILS
                + "   SET  entry_status=?,confirm_ip=?,confirm_date=current_timestamp"
                + " WHERE  appl_no=? and pur_cd=?";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getEntry_status());
        ps.setString(2, clientIpAddress);
        ps.setString(3, dobj.getAppl_no());
        ps.setInt(4, dobj.getPur_cd());
        ps.executeUpdate();
    }

    /**
     *
     * @param tmgr
     * @param state_cd
     * @param pur_cd
     * @param parameters
     * @return action_cd,flow_sr_no in array
     * @throws VahanException
     */
    public static int[] getInitialAction(TransactionManager tmgr, String state_cd, int pur_cd, VehicleParameters parameters) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        int action_cd = 0;
        int flowsr_no = 1;
        int[] retArr = null;
        try {

            while (true) {
                sql = "select flow_srno,action_cd,condition_formula from tm_purpose_action_flow "
                        + " where pur_cd=? and state_cd=? and flow_srno=? order by 1";
                ps = tmgr.prepareStatement(sql);
                ps.setInt(1, pur_cd);
                ps.setString(2, state_cd);
                ps.setInt(3, flowsr_no);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();

                if (rs.next()) {

                    if (parameters != null) {
                        if (isCondition(replaceTagValues(rs.getString("condition_formula"), parameters), "getInitialAction")) {
                            //action_cd = rs.getInt("action_cd");
                            flowsr_no = rs.getInt("flow_srno");
                            retArr = new int[2];
                            retArr[0] = rs.getInt("action_cd");
                            retArr[1] = flowsr_no;
                            break;
                        } else {
                            flowsr_no++;
                        }

                    } else {
                        //action_cd = rs.getInt("action_cd");
                        flowsr_no = rs.getInt("flow_srno");
                        retArr = new int[2];
                        retArr[0] = rs.getInt("action_cd");
                        retArr[1] = flowsr_no;
                        break;
                    }
                } else {
                    break;
                }
            }

        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            throw new VahanException(sqle.getMessage());
        }

        return retArr;
    } // end of getInitialAction

    public static ArrayList<Status_dobj> applicationStatus(String regn_no) throws SQLException, Exception {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        ArrayList<Status_dobj> list = new ArrayList<>();

        if (regn_no != null) {

            try {
                tmgr = new TransactionManager("applicationStatus");

                sql = " SELECT distinct a.*,b.regn_no,c.descr,d.off_name,e.descr as state_name FROM " + TableList.VA_STATUS
                        + "  a INNER JOIN " + TableList.VA_DETAILS + " b on b.pur_cd=a.pur_cd and b.appl_no=a.appl_no "
                        + "  LEFT JOIN " + TableList.TM_PURPOSE_MAST + " c on c.pur_cd=b.pur_cd "
                        + "  LEFT JOIN " + TableList.TM_OFFICE + " d on d.state_cd=a.state_cd and d.off_cd=a.off_cd "
                        + "  LEFT JOIN " + TableList.TM_STATE + " e on e.state_code=a.state_cd "
                        + "  WHERE b.regn_no=? and b.entry_status <> ? ORDER BY a.op_dt desc";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regn_no.toUpperCase());
                ps.setString(2, TableConstants.STATUS_APPROVED);

                RowSet rs = tmgr.fetchDetachedRowSet();

                while (rs.next()) {
                    Status_dobj status_dobj = new Status_dobj();

                    status_dobj.setAppl_no(rs.getString("appl_no"));
                    status_dobj.setRegn_no(rs.getString("regn_no"));
                    status_dobj.setPur_cd(rs.getInt("pur_cd"));
                    status_dobj.setAction_cd(rs.getInt("action_cd"));
                    status_dobj.setState_cd(rs.getString("state_cd"));
                    status_dobj.setOff_cd(rs.getInt("off_cd"));
                    status_dobj.setPurCdDescr(rs.getString("descr"));
                    status_dobj.setOffName(rs.getString("off_name"));
                    status_dobj.setStateName(rs.getString("state_name"));
                    list.add(status_dobj);
                }

            } finally {
                try {
                    if (tmgr != null) {
                        tmgr.release();
                    }
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return list;
    }//end of applicationStatus

    public static ArrayList<Status_dobj> applicationStatusInSameOffice(String regn_no, String state_cd, int off_cd) throws SQLException, Exception {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        ArrayList<Status_dobj> list = new ArrayList<>();

        if (regn_no != null && state_cd != null) {

            try {
                tmgr = new TransactionManager("applicationStatus");

                sql = " SELECT distinct a.*,b.regn_no,c.descr,d.off_name,e.descr as state_name FROM " + TableList.VA_STATUS
                        + "  a INNER JOIN " + TableList.VA_DETAILS + " b on b.pur_cd=a.pur_cd and b.appl_no=a.appl_no "
                        + "  LEFT JOIN " + TableList.TM_PURPOSE_MAST + " c on c.pur_cd=b.pur_cd "
                        + "  LEFT JOIN " + TableList.TM_OFFICE + " d on d.state_cd=a.state_cd and d.off_cd=a.off_cd "
                        + "  LEFT JOIN " + TableList.TM_STATE + " e on e.state_code=a.state_cd "
                        + "  WHERE b.regn_no=? and a.state_cd =? and a.off_cd=? and b.entry_status <> ? ORDER BY a.op_dt desc";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regn_no.toUpperCase());
                ps.setString(2, state_cd.toUpperCase());
                ps.setInt(3, off_cd);
                ps.setString(4, TableConstants.STATUS_APPROVED);

                RowSet rs = tmgr.fetchDetachedRowSet();

                while (rs.next()) {
                    Status_dobj status_dobj = new Status_dobj();

                    status_dobj.setAppl_no(rs.getString("appl_no"));
                    status_dobj.setRegn_no(rs.getString("regn_no"));
                    status_dobj.setPur_cd(rs.getInt("pur_cd"));
                    status_dobj.setAction_cd(rs.getInt("action_cd"));
                    status_dobj.setState_cd(rs.getString("state_cd"));
                    status_dobj.setOff_cd(rs.getInt("off_cd"));
                    status_dobj.setPurCdDescr(rs.getString("descr"));
                    status_dobj.setOffName(rs.getString("off_name"));
                    status_dobj.setStateName(rs.getString("state_name"));
                    list.add(status_dobj);
                }

            } finally {
                try {
                    if (tmgr != null) {
                        tmgr.release();
                    }
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return list;
    }//end of applicationStatusInSameOffice

    public static ArrayList<Status_dobj> applicationStatusByApplNo(String applNo, String state_cd) throws VahanException, Exception {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        ArrayList<Status_dobj> list = new ArrayList<>();

        if (applNo != null && state_cd != null) {

            try {
                tmgr = new TransactionManager("applicationStatusByApplNo");

                sql = " SELECT distinct a.*,b.regn_no,c.descr,d.off_name,e.descr as state_name, to_char(b.appl_dt, 'YYYY-MM-DD') as appl_dt FROM " + TableList.VA_STATUS + " a "
                        + " INNER JOIN " + TableList.VA_DETAILS + " b on b.pur_cd=a.pur_cd and b.appl_no=a.appl_no "
                        + " LEFT  JOIN " + TableList.TM_PURPOSE_MAST + " c on c.pur_cd=b.pur_cd "
                        + " LEFT  JOIN " + TableList.TM_OFFICE + " d on d.state_cd=a.state_cd and d.off_cd=a.off_cd "
                        + " LEFT  JOIN " + TableList.TM_STATE + " e on e.state_code=a.state_cd "
                        + " WHERE b.appl_no=? and a.state_cd =? and b.entry_status <> ? ORDER BY a.op_dt desc";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, applNo);
                ps.setString(2, state_cd);
                ps.setString(3, TableConstants.STATUS_APPROVED);

                RowSet rs = tmgr.fetchDetachedRowSet();

                while (rs.next()) {
                    Status_dobj status_dobj = new Status_dobj();
                    status_dobj.setAppl_dt(rs.getString("appl_dt"));
                    status_dobj.setAppl_no(rs.getString("appl_no"));
                    status_dobj.setRegn_no(rs.getString("regn_no"));
                    status_dobj.setPur_cd(rs.getInt("pur_cd"));
                    status_dobj.setAction_cd(rs.getInt("action_cd"));
                    status_dobj.setState_cd(rs.getString("state_cd"));
                    status_dobj.setOff_cd(rs.getInt("off_cd"));
                    status_dobj.setPurCdDescr(rs.getString("descr"));
                    status_dobj.setOffName(rs.getString("off_name"));
                    status_dobj.setStateName(rs.getString("state_name"));
                    list.add(status_dobj);
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                throw new VahanException("Error in getting Application Status.");
            } finally {
                try {
                    if (tmgr != null) {
                        tmgr.release();
                    }
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return list;
    }//end of applicationStatusByApplNo

    public static ArrayList<Status_dobj> applicationStatus(String regn_no, String appl_no, String state_cd) throws SQLException, Exception {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        ArrayList<Status_dobj> list = new ArrayList<>();

        if (regn_no != null && state_cd != null) {

            try {
                tmgr = new TransactionManager("applicationStatus");

                sql = "SELECT  distinct a.*,b.regn_no from " + TableList.VA_STATUS + " a," + TableList.VA_DETAILS + " b "
                        + " WHERE   a.appl_no=b.appl_no and a.appl_no=? and b.regn_no=? and a.state_cd =? and b.entry_status <> ? "
                        + " ORDER BY a.op_dt desc";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, appl_no);
                ps.setString(2, regn_no.toUpperCase());
                ps.setString(3, state_cd.toUpperCase());
                ps.setString(4, TableConstants.STATUS_APPROVED);

                RowSet rs = tmgr.fetchDetachedRowSet();

                while (rs.next()) {
                    Status_dobj status_dobj = new Status_dobj();

                    status_dobj.setAppl_no(rs.getString("appl_no"));
                    status_dobj.setRegn_no(rs.getString("regn_no"));
                    status_dobj.setPur_cd(rs.getInt("pur_cd"));
                    status_dobj.setAction_cd(rs.getInt("action_cd"));
                    status_dobj.setState_cd(rs.getString("state_cd"));
                    status_dobj.setOff_cd(rs.getInt("off_cd"));

                    list.add(status_dobj);
                }

            } finally {
                try {
                    if (tmgr != null) {
                        tmgr.release();
                    }
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return list;
    }//end of applicationStatus

    public static String applicationStatusForPermit(String regn_no, String state_cd) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String str = "";
        if (!CommonUtils.isNullOrBlank(regn_no) && !regn_no.equalsIgnoreCase("NEW")) {
            if (regn_no != null && state_cd != null) {
                regn_no = regn_no.toUpperCase();
                try {
                    tmgr = new TransactionManager("applicationStatus");
                    sql = "SELECT  distinct a.*,b.regn_no from " + TableList.VA_STATUS + " a," + TableList.VA_DETAILS + " b "
                            + " WHERE   a.appl_no=b.appl_no and b.regn_no=? and a.state_cd =? AND a.pur_cd <> ?"
                            + " ORDER BY a.op_dt desc";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, regn_no.toUpperCase());
                    ps.setString(2, state_cd.toUpperCase());
                    ps.setInt(3, TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE);
                    RowSet rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        str = "Application No [ <font color=\"red\">" + rs.getString("appl_no") + "</font> ] in State [ " + ServerUtility.getStateNameByStateCode(rs.getString("state_cd")) + " ] at Office [ " + ServerUtility.getOfficeName(rs.getInt("off_cd"), rs.getString("state_cd")) + " ]"
                                + " is pending at the stage of [ <font color=\"red\">" + getSeatCodeAgainstAction(rs.getInt("action_cd"), tmgr) + "</font> ]";
                    }
                } catch (SQLException e) {
                    LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
                    throw new VahanException(TableConstants.SomthingWentWrong);
                } catch (Exception e) {
                    LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
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
            }
        }
        return str.toUpperCase();
    }//end of applicationStatus

    public static boolean applicationStatusForFitness(String regn_no, String state_cd) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        boolean found = false;
        if (!CommonUtils.isNullOrBlank(regn_no) && !regn_no.equalsIgnoreCase("NEW")) {
            if (regn_no != null && state_cd != null) {

                try {
                    tmgr = new TransactionManager("applicationStatusForFitness");
                    sql = "SELECT  distinct a.*,b.regn_no from " + TableList.VA_STATUS + " a," + TableList.VA_DETAILS + " b "
                            + " WHERE   a.appl_no=b.appl_no and b.regn_no=? and a.state_cd =? AND a.pur_cd = 2 "
                            + " ORDER BY a.op_dt desc";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, regn_no.toUpperCase());
                    ps.setString(2, state_cd.toUpperCase());
                    RowSet rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        found = true;
                    }
                } catch (SQLException e) {
                    LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
                    throw new VahanException(TableConstants.SomthingWentWrong);
                } catch (Exception e) {
                    LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
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
            }
        }
        return found;
    }//end of applicationStatus

    public static List<Owner_dobj> getExistingOwnerDetails(String regn_no) throws VahanException {

        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        Owner_dobj dobj = null;
        List<Owner_dobj> listExistingOwnerDetails = new ArrayList<>();

        try {

            if (regn_no != null) {
                regn_no = regn_no.toUpperCase();
            }

            tmgr = new TransactionManagerReadOnly("getExistingOwnerDetails");
            sql = "select * from " + TableList.VIEW_VV_OWNER + " where regn_no=? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getUserSeatOffCode());

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {

                dobj = new Owner_dobj();

                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setChasi_no(rs.getString("chasi_no"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setF_name(rs.getString("f_name"));
                dobj.setOwner_sr(rs.getInt("owner_sr"));
                dobj.setSale_amt(rs.getInt("sale_amt"));
                dobj.setGarage_add(rs.getString("garage_add"));
                dobj.setC_add1(rs.getString("c_add1"));
                dobj.setC_add2(rs.getString("c_add2"));
                dobj.setC_add3(rs.getString("c_add3"));
                dobj.setC_state_name(rs.getString("c_state_name"));
                dobj.setC_district_name(rs.getString("c_district_name"));
                dobj.setC_pincode(rs.getInt("c_pincode"));
                dobj.setP_add1(rs.getString("p_add1"));
                dobj.setP_add2(rs.getString("p_add2"));
                dobj.setP_add3(rs.getString("p_add3"));
                dobj.setP_district_name(rs.getString("p_district_name"));
                dobj.setP_state_name(rs.getString("p_state_name"));
                dobj.setP_pincode(rs.getInt("p_pincode"));
                dobj.setEng_no(rs.getString("eng_no"));
                dobj.setLd_wt(rs.getInt("ld_wt"));
                dobj.setUnld_wt(rs.getInt("unld_wt"));
                dobj.setNo_cyl(rs.getInt("no_cyl"));
                dobj.setHp(rs.getFloat("hp"));
                dobj.setSeat_cap(rs.getInt("seat_cap"));
                dobj.setStand_cap(rs.getInt("stand_cap"));
                dobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                dobj.setCubic_cap(rs.getFloat("cubic_cap"));
                dobj.setWheelbase(rs.getInt("wheelbase"));
                dobj.setFloor_area(rs.getFloat("floor_area"));
                dobj.setFuel_descr(rs.getString("fuel_descr"));
                dobj.setBody_type(rs.getString("body_type"));
                dobj.setColor(rs.getString("color"));
                dobj.setAc_fitted(rs.getString("ac_fitted"));
                dobj.setAudio_fitted(rs.getString("audio_fitted"));
                dobj.setVideo_fitted(rs.getString("video_fitted"));
                dobj.setLength(rs.getInt("length"));
                dobj.setHeight(rs.getInt("height"));
                dobj.setWidth(rs.getInt("width"));
                dobj.setFit_upto(rs.getDate("fit_upto"));
                dobj.setVh_class(rs.getInt("vh_class"));
                dobj.setVehType(ServerUtility.VehicleClassType(dobj.getVh_class()));
                dobj.setVch_catg(rs.getString("vch_catg"));
                dobj.setSale_amt(rs.getInt("sale_amt"));
                dobj.setOwner_cd(rs.getInt("owner_cd"));
                dobj.setStatus(rs.getString("status"));
                dobj.setPurchase_dt(rs.getDate("purchase_dt"));
                dobj.setRegn_upto(rs.getDate("regn_upto"));
                dobj.setRegn_type(rs.getString("regn_type"));
                listExistingOwnerDetails.add(dobj);
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return listExistingOwnerDetails;
    }

    /**
     * @author Kartikey Singh
     */
    public static List<Owner_dobj> getExistingOwnerDetails(String regn_no, String stateCode, int selectedOffCode) throws VahanException {

        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        Owner_dobj dobj = null;
        List<Owner_dobj> listExistingOwnerDetails = new ArrayList<>();

        try {
            if (regn_no != null) {
                regn_no = regn_no.toUpperCase();
            }

            tmgr = new TransactionManagerReadOnly("getExistingOwnerDetails");
            sql = "select * from " + TableList.VIEW_VV_OWNER + " where regn_no=? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCode);
            ps.setInt(3, selectedOffCode);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                dobj = new Owner_dobj();
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setChasi_no(rs.getString("chasi_no"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setF_name(rs.getString("f_name"));
                dobj.setOwner_sr(rs.getInt("owner_sr"));
                dobj.setSale_amt(rs.getInt("sale_amt"));
                dobj.setGarage_add(rs.getString("garage_add"));
                dobj.setC_add1(rs.getString("c_add1"));
                dobj.setC_add2(rs.getString("c_add2"));
                dobj.setC_add3(rs.getString("c_add3"));
                dobj.setC_state_name(rs.getString("c_state_name"));
                dobj.setC_district_name(rs.getString("c_district_name"));
                dobj.setC_pincode(rs.getInt("c_pincode"));
                dobj.setP_add1(rs.getString("p_add1"));
                dobj.setP_add2(rs.getString("p_add2"));
                dobj.setP_add3(rs.getString("p_add3"));
                dobj.setP_district_name(rs.getString("p_district_name"));
                dobj.setP_state_name(rs.getString("p_state_name"));
                dobj.setP_pincode(rs.getInt("p_pincode"));
                dobj.setEng_no(rs.getString("eng_no"));
                dobj.setLd_wt(rs.getInt("ld_wt"));
                dobj.setUnld_wt(rs.getInt("unld_wt"));
                dobj.setNo_cyl(rs.getInt("no_cyl"));
                dobj.setHp(rs.getFloat("hp"));
                dobj.setSeat_cap(rs.getInt("seat_cap"));
                dobj.setStand_cap(rs.getInt("stand_cap"));
                dobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                dobj.setCubic_cap(rs.getFloat("cubic_cap"));
                dobj.setWheelbase(rs.getInt("wheelbase"));
                dobj.setFloor_area(rs.getFloat("floor_area"));
                dobj.setFuel_descr(rs.getString("fuel_descr"));
                dobj.setBody_type(rs.getString("body_type"));
                dobj.setColor(rs.getString("color"));
                dobj.setAc_fitted(rs.getString("ac_fitted"));
                dobj.setAudio_fitted(rs.getString("audio_fitted"));
                dobj.setVideo_fitted(rs.getString("video_fitted"));
                dobj.setLength(rs.getInt("length"));
                dobj.setHeight(rs.getInt("height"));
                dobj.setWidth(rs.getInt("width"));
                dobj.setFit_upto(rs.getDate("fit_upto"));
                dobj.setVh_class(rs.getInt("vh_class"));
                dobj.setVehType(ServerUtility.VehicleClassType(dobj.getVh_class()));
                dobj.setVch_catg(rs.getString("vch_catg"));
                dobj.setSale_amt(rs.getInt("sale_amt"));
                dobj.setOwner_cd(rs.getInt("owner_cd"));
                dobj.setStatus(rs.getString("status"));
                dobj.setPurchase_dt(rs.getDate("purchase_dt"));
                dobj.setRegn_upto(rs.getDate("regn_upto"));
                dobj.setRegn_type(rs.getString("regn_type"));
                listExistingOwnerDetails.add(dobj);
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return listExistingOwnerDetails;
    }

    public static String getRequestedUrl(int action_cd, String bean_type) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        String requestedUrl = "";
        try {
            tmgr = new TransactionManagerReadOnly("getRequestedUrl");
            if (bean_type.equals("Home_Bean")) {
                sql = "select action_cd, redirect_menu from " + TableList.TM_ACTION + " where action_cd = ?";
            } else if (bean_type.equals("Seat_Bean")) {
                sql = "select action_cd, redirect_url from " + TableList.TM_ACTION + " where action_cd = ?";
            }
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, action_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                if (bean_type.equals("Home_Bean")) {
                    requestedUrl = rs.getString("redirect_menu");
                } else if (bean_type.equals("Seat_Bean")) {
                    requestedUrl = rs.getString("redirect_url");
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Requested Action/File is not found, Please Contact to the System Administrator.");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Requested Action/File is not found, Please Contact to the System Administrator.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return requestedUrl;
    }

    public static String getTaxHead(int pur_cd) {
        String whereiam = "Get Tax Head";
        String taxHead = "";
        PreparedStatement psmt = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly(whereiam);
            String strSQL = "SELECT descr FROM tm_purpose_mast where pur_cd = ?;";
            psmt = tmgr.prepareStatement(strSQL);
            psmt.setInt(1, pur_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                taxHead = rs.getString("descr");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return taxHead;
    }

    public static String getTaxModeDescr(String taxMode) {

        String whereiam = "getTaxModeDescr";
        String taxModeDescr = "";
        PreparedStatement psmt = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly(whereiam);
            String strSQL = "SELECT * FROM vm_tax_mode where tax_mode=?";
            psmt = tmgr.prepareStatement(strSQL);
            psmt.setString(1, taxMode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                taxModeDescr = rs.getString("descr");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return taxModeDescr;
    }

    public static String getPurposeShortDescr(String appl_no) {
        String whereiam = "Get Tax Head";
        String taxHead = "";
        PreparedStatement psmt = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly(whereiam);
            String strSQL = "SELECT * FROM getpurposeshortdescr(?);";
            psmt = tmgr.prepareStatement(strSQL);
            psmt.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                taxHead = rs.getString(1);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return taxHead;
    }

    public static String getUserName(long empCode) {
        String whereiam = "Get user name";
        String userName = "";
        PreparedStatement psmt = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly(whereiam);
            String strSQL = "select user_name from tm_user_info where user_cd=?";
            psmt = tmgr.prepareStatement(strSQL);
            psmt.setLong(1, empCode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                userName = rs.getString("user_name");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return userName;
    }

    public static String getTransportVchType(int vh_class) throws VahanException {
        String transportVchType = "";
        PreparedStatement psmt = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("isTransport");
            String strSQL = "SELECT COALESCE(transport_catg, '') as transport_catg FROM " + TableList.VM_VH_CLASS
                    + " where vh_class = ?";
            psmt = tmgr.prepareStatement(strSQL);
            psmt.setInt(1, vh_class);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                transportVchType = rs.getString("transport_catg");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        if (transportVchType == null) {
            throw new VahanException("No Transport Vehicle Type Found");
        }
        return transportVchType;
    }

    public static boolean isTransport(int vh_class, Owner_dobj ownerDobj) throws VahanException {
        Boolean isTransport = null;
        PreparedStatement psmt = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            if (vh_class > 0) {
                if (ownerDobj != null && ownerDobj.getVehType() > 0) {
                    if (ownerDobj.getVehType() == TableConstants.VM_VEHTYPE_TRANSPORT) {
                        isTransport = true;
                    } else {
                        isTransport = false;
                    }
                } else {
                    tmgr = new TransactionManagerReadOnly("isTransport");
                    String strSQL = "SELECT case when class_type = 1 then true else false end as isTransport FROM " + TableList.VM_VH_CLASS
                            + " where vh_class = ?";
                    psmt = tmgr.prepareStatement(strSQL);
                    psmt.setInt(1, vh_class);
                    RowSet rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        isTransport = rs.getBoolean("isTransport");
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        if (isTransport == null) {
            throw new VahanException("No Vehicle Type Found (VchClass:" + vh_class + ")");
        }
        return isTransport;
    }

    public static int VehicleClassType(int vh_class) throws VahanException {
        int returnType = 0;
        boolean isTranport = isTransport(vh_class, null);
        if (isTranport) {
            returnType = 1;
        } else {
            returnType = 2;
        }
        return returnType;
    }

    public static void verifyInsertNewRegHsrpDetail(String appl_no, String regn_no, String hsrp_flag,
            String state_cd, int off_cd, TransactionManager tmgr) throws Exception {

        if (verifyForHsrp(state_cd, off_cd, tmgr)) {
            insertHsrpDetail(appl_no, regn_no, hsrp_flag, state_cd, off_cd, tmgr);
        }
    }

    /**
     * @author Kartikey Singh
     */
    public static void verifyInsertNewRegHsrpDetail(String appl_no, String regn_no, String hsrp_flag,
            String state_cd, int off_cd, TransactionManager tmgr, String empCode) throws Exception {

        if (verifyForHsrp(state_cd, off_cd, tmgr)) {
            insertHsrpDetail(appl_no, regn_no, hsrp_flag, state_cd, off_cd, tmgr, empCode);
        }
    }

    public static boolean verifyForHsrp(String state_cd, int off_cd,
            TransactionManager tmgr) throws Exception {
        boolean isHsrp = false;

        String sql = "select hsrp from " + TableList.VM_SMART_CARD_HSRP
                + " where state_cd=? and off_cd=? and hsrp is true";

        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, state_cd);
        ps.setInt(2, off_cd);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();

        if (rs.next()) {
            isHsrp = true;
        }

        return isHsrp;
    }

    public static boolean verifyForOldVehicleHsrp(String state_cd, int off_cd,
            TransactionManager tmgr) throws Exception {
        boolean isHsrp = false;

        String sql = "select old_veh_hsrp from " + TableList.VM_SMART_CARD_HSRP
                + " where state_cd=? and off_cd=? and old_veh_hsrp is true";

        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, state_cd);
        ps.setInt(2, off_cd);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();

        if (rs.next()) {
            isHsrp = true;
        }

        return isHsrp;
    }

    public static void insertHsrpDetail(String appl_no, String regn_no, String hsrp_flag,
            String state_cd, int off_cd, TransactionManager tmgr) throws Exception {

        String sql = "INSERT INTO " + TableList.VA_HSRP
                + "(state_cd, off_cd, appl_no, regn_no,"
                + " hsrp_flag, user_cd, op_dt)"
                + "    VALUES (?, ?, ?, ?, ?, ?,current_timestamp);";

        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, state_cd);
        ps.setInt(2, off_cd);
        ps.setString(3, appl_no);
        ps.setString(4, regn_no);
        ps.setString(5, hsrp_flag);
        ps.setString(6, Util.getEmpCode());
        ps.executeUpdate();
    }

    /**
     * @author Kartikey Singh
     */
    public static void insertHsrpDetail(String appl_no, String regn_no, String hsrp_flag,
            String state_cd, int off_cd, TransactionManager tmgr, String empCode) throws Exception {

        String sql = "INSERT INTO " + TableList.VA_HSRP
                + "(state_cd, off_cd, appl_no, regn_no,"
                + " hsrp_flag, user_cd, op_dt)"
                + "    VALUES (?, ?, ?, ?, ?, ?,current_timestamp);";

        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, state_cd);
        ps.setInt(2, off_cd);
        ps.setString(3, appl_no);
        ps.setString(4, regn_no);
        ps.setString(5, hsrp_flag);
        ps.setString(6, empCode);
        ps.executeUpdate();
    }

    public static void insertHsrpDetailFromVhHsrp(String regn_no, String state_cd, int off_cd, TransactionManager tmgr) throws Exception {

        String sql = "INSERT INTO " + TableList.VT_HSRP + " (\n"
                + " state_cd, off_cd, appl_no, regn_no, hsrp_flag, user_cd, op_dt, \n"
                + " hsrp_no_front, hsrp_no_back, hsrp_fix_dt, hsrp_fix_amt, hsrp_amt_taken_on,hsrp_op_dt)\n"
                + " SELECT state_cd, ?, appl_no, regn_no, hsrp_flag, user_cd, op_dt, \n"
                + " hsrp_no_front, hsrp_no_back, hsrp_fix_dt, hsrp_fix_amt, hsrp_amt_taken_on, \n"
                + " hsrp_op_dt\n"
                + " FROM " + TableList.VH_HSRP + " where regn_no=? and state_cd=? order by moved_on desc limit 1;";

        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setInt(1, off_cd);
        ps.setString(2, regn_no);
        ps.setString(3, state_cd);
        ps.executeUpdate();

    }

    public static void VerifyInsertSmartCardPrintDetail(String appl_no, String regnno, String state_cd, int off_cd, int purCd, TransactionManager tmgr) throws VahanException {
        try {
            String sql = "select regn_no,pur_cd from va_details where appl_no =? and entry_status not in (?) and pur_cd in (1, 3, 4, 5, 6, 7, 10, 11, 12, 15, 16, 17, 88, 123)";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, TableConstants.STATUS_APPROVED);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            /**
             * Validate if application can be printed or send for smartcard
             */
            if (rs.next()) {
                rs.beforeFirst();
                if (purCd != TableConstants.VM_TRANSACTION_MAST_REM_HYPO) {
                    while (rs.next()) {
                        if (rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_REM_HYPO) {
                            throw new VahanException("HPT Transaction is pending for this application no, First approve the HPT transaction.");
                        }
                    }
                }
                return;

            } else {
                sql = "select regn_no from va_details where appl_no =? and pur_cd in (1, 3, 4, 5, 6, 7, 9, 10, 11, 12, 15, 16, 17, 88, 91, 123, 331)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, appl_no);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (!rs.next()) {
                    return;
                }
            }

            //Call HSRP function for Purposes other than 1,18,123,124
            sql = "select regn_no,pur_cd from va_details where appl_no =? and  pur_cd not in (1, 123,18,124,10)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if ("WB".contains(state_cd)) {
                    if (verifyForOldVehicleHsrp(state_cd, off_cd, tmgr)) {
                        sql = "select regn_no from " + TableList.VT_HSRP
                                + " where regn_no =? ";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, regnno);
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        if (!rs.next()) {
                            sql = "select regn_no from " + TableList.VH_HSRP
                                    + " where regn_no =? and state_cd=? ";
                            ps = tmgr.prepareStatement(sql);
                            ps.setString(1, regnno);
                            ps.setString(2, state_cd);
                            rs = tmgr.fetchDetachedRowSet_No_release();
                            if (!rs.next()) {
                                sql = "select regn_no from " + TableList.VA_HSRP
                                        + " where regn_no =? and state_cd=? ";
                                ps = tmgr.prepareStatement(sql);
                                ps.setString(1, regnno);
                                ps.setString(2, state_cd);
                                rs = tmgr.fetchDetachedRowSet_No_release();
                                if (!rs.next()) {
                                    sql = "select regn_no from " + TableList.VHA_HSRP
                                            + " where appl_no =? and state_cd=? and off_cd=? ";
                                    ps = tmgr.prepareStatement(sql);
                                    ps.setString(1, appl_no);
                                    ps.setString(2, state_cd);
                                    ps.setInt(3, off_cd);
                                    rs = tmgr.fetchDetachedRowSet_No_release();
                                    if (!rs.next()) {
                                        insertHsrpDetail(appl_no, regnno, "OB", state_cd, off_cd, tmgr);
                                    }
                                }
                            } else {
                                insertHsrpDetailFromVhHsrp(regnno, state_cd, off_cd, tmgr);
                            }
                        }
                    }
                }
            }

            boolean isSmartCard = verifyForSmartCard(state_cd, off_cd, tmgr);
            boolean isSmartCardNew = verifyForSmartCardNew(state_cd, off_cd, tmgr);
            insertForQRDetails(appl_no, regnno, null, null, false, DocumentType.RC_QR, state_cd, off_cd, tmgr);
            if (isSmartCardNew) {
                sql = "select * from vm_smart_card_hsrp where paper_rc= ? and state_cd = ? and off_cd = ? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, TableConstants.VM_PLASTIC_CARD_RC_PVC_CD);
                ps.setString(2, state_cd);
                ps.setInt(3, off_cd);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    SmartCardImpl.insertIntoSmartCardTempDtls(regnno, state_cd, off_cd, appl_no, tmgr);
                }
            }

            if (isSmartCard) {
                TmConfigurationDobj tmConfig = Util.getTmConfiguration();
                if (isSmartCardForUpdate(state_cd, off_cd, regnno, appl_no, tmConfig)) {
                    updateForSmartCard(appl_no, regnno, state_cd, off_cd, tmgr);
                } else {
                    insertForSmartCard(appl_no, regnno, state_cd, off_cd, purCd, tmConfig, tmgr);
                    if (tmConfig != null) {
                        String user_cd = Util.getEmpCode();
                        if (tmConfig.isIs_rc_dispatch()) {
                            if (verifyForPostalFee(state_cd, off_cd, appl_no, tmgr)) {
                                insertForDispatch(appl_no, regnno, state_cd, off_cd, user_cd, tmgr);
                            }
                        } else if (tmConfig.isIs_rc_dispatch_without_postal_fee()) {
                            insertForDispatch(appl_no, regnno, state_cd, off_cd, user_cd, tmgr);
                        }
                    }
                }

            } else {
                insertForPrint(appl_no, regnno, state_cd, off_cd, tmgr);
            }

            //Create Message //
            String mobileNo = new FeeImpl().getMobileNo(appl_no);
            String officeName = new FeeImpl().getOfficeName(Util.getSelectedSeat().getOff_cd(), Util.getUserStateCode());
            String generatedRcpt = new FeeImpl().getRcptNo(Util.getSelectedSeat().getOff_cd(), Util.getUserStateCode(), appl_no);
            String shortForm = "";
            sql = "select tm_purpose_mast.* from va_details,tm_purpose_mast where appl_no =? and va_details.pur_cd =tm_purpose_mast.pur_cd and state_cd=? and off_cd=?  ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());

            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                if (rs.getString("short_descr") != null) {
                    if (!shortForm.equals("")) {
                        shortForm = shortForm + "/";
                    }
                    shortForm = shortForm + rs.getString("short_descr");
                }
            }
            String msgMobile = "";

            msgMobile = "[" + regnno + "]%0D%0A"
                    + shortForm + " Transaction(s) approved against vide receipt no "
                    + generatedRcpt
                    + " dated " + JSFUtils.convertToStandardDateFormat(new java.util.Date()) + ". RC Under Preparation.%0D%0A"
                    + "RC can also be downloaded on DigiLocker and mParivahan mobile apps.%0D%0A" + officeName;

            sendSMS(mobileNo, msgMobile);
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    /**
     * @author Kartikey Singh
     */
    public static void VerifyInsertSmartCardPrintDetail(String appl_no, String regnno, String state_cd, int off_cd, int purCd, TransactionManager tmgr,
            String empCode, String userCategory, TmConfigurationDobj tmConfig, SessionVariablesModel sessionVariablesModel) throws VahanException {
        try {
            String sql = "select regn_no,pur_cd from va_details where appl_no =? and entry_status not in (?) and pur_cd in (1, 3, 4, 5, 6, 7, 10, 11, 12, 15, 16, 17, 88, 123)";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, TableConstants.STATUS_APPROVED);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            /**
             * Validate if application can be printed or send for smartcard
             */
            if (rs.next()) {
                rs.beforeFirst();
                if (purCd != TableConstants.VM_TRANSACTION_MAST_REM_HYPO) {
                    while (rs.next()) {
                        if (rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_REM_HYPO) {
                            throw new VahanException("HPT Transaction is pending for this application no, First approve the HPT transaction.");
                        }
                    }
                }
                return;

            } else {
                sql = "select regn_no from va_details where appl_no =? and pur_cd in (1, 3, 4, 5, 6, 7, 9, 10, 11, 12, 15, 16, 17, 88, 91, 123, 331)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, appl_no);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (!rs.next()) {
                    return;
                }
            }

            //Call HSRP function for Purposes other than 1,18,123,124
            sql = "select regn_no,pur_cd from va_details where appl_no =? and  pur_cd not in (1, 123,18,124,10)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if ("WB".contains(state_cd)) {
                    if (verifyForOldVehicleHsrp(state_cd, off_cd, tmgr)) {
                        sql = "select regn_no from " + TableList.VT_HSRP
                                + " where regn_no =? ";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, regnno);
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        if (!rs.next()) {
                            sql = "select regn_no from " + TableList.VH_HSRP
                                    + " where regn_no =? and state_cd=? ";
                            ps = tmgr.prepareStatement(sql);
                            ps.setString(1, regnno);
                            ps.setString(2, state_cd);
                            rs = tmgr.fetchDetachedRowSet_No_release();
                            if (!rs.next()) {
                                sql = "select regn_no from " + TableList.VA_HSRP
                                        + " where regn_no =? and state_cd=? ";
                                ps = tmgr.prepareStatement(sql);
                                ps.setString(1, regnno);
                                ps.setString(2, state_cd);
                                rs = tmgr.fetchDetachedRowSet_No_release();
                                if (!rs.next()) {
                                    sql = "select regn_no from " + TableList.VHA_HSRP
                                            + " where appl_no =? and state_cd=? and off_cd=? ";
                                    ps = tmgr.prepareStatement(sql);
                                    ps.setString(1, appl_no);
                                    ps.setString(2, state_cd);
                                    ps.setInt(3, off_cd);
                                    rs = tmgr.fetchDetachedRowSet_No_release();
                                    if (!rs.next()) {
                                        insertHsrpDetail(appl_no, regnno, "OB", state_cd, off_cd, tmgr, empCode);
                                    }
                                }
                            } else {
                                insertHsrpDetailFromVhHsrp(regnno, state_cd, off_cd, tmgr);
                            }
                        }
                    }
                }
            }

            boolean isSmartCard = verifyForSmartCard(state_cd, off_cd, tmgr);
            boolean isSmartCardNew = verifyForSmartCardNew(state_cd, off_cd, tmgr);
            insertForQRDetails(appl_no, regnno, null, null, false, DocumentType.RC_QR, state_cd, off_cd, tmgr, empCode);
            if (isSmartCardNew) {
                sql = "select * from vm_smart_card_hsrp where paper_rc= ? and state_cd = ? and off_cd = ? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, TableConstants.VM_PLASTIC_CARD_RC_PVC_CD);
                ps.setString(2, state_cd);
                ps.setInt(3, off_cd);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    SmartCardImplementation.insertIntoSmartCardTempDtls(regnno, state_cd, off_cd, appl_no, tmgr, tmConfig, sessionVariablesModel);
                }
            }

            if (isSmartCard) {
                if (isSmartCardForUpdate(state_cd, off_cd, regnno, appl_no, tmConfig)) {
                    updateForSmartCard(appl_no, regnno, state_cd, off_cd, tmgr, empCode);
                } else {
                    insertForSmartCard(appl_no, regnno, state_cd, off_cd, purCd, tmConfig, tmgr, userCategory, empCode, sessionVariablesModel);
                    if (tmConfig != null) {
                        String user_cd = empCode;
                        if (tmConfig.isIs_rc_dispatch()) {
                            if (verifyForPostalFee(state_cd, off_cd, appl_no, tmgr)) {
                                insertForDispatch(appl_no, regnno, state_cd, off_cd, user_cd, tmgr);
                            }
                        } else if (tmConfig.isIs_rc_dispatch_without_postal_fee()) {
                            insertForDispatch(appl_no, regnno, state_cd, off_cd, user_cd, tmgr);
                        }
                    }
                }

            } else {
                insertForPrint(appl_no, regnno, state_cd, off_cd, tmgr);
            }

            //Create Message //
            String mobileNo = new FeeImplementation().getMobileNo(appl_no);
            String officeName = new FeeImplementation().getOfficeName(off_cd, state_cd);
            String generatedRcpt = new FeeImplementation().getRcptNo(off_cd, state_cd, appl_no);
            String shortForm = "";
            sql = "select tm_purpose_mast.* from va_details,tm_purpose_mast where appl_no =? and va_details.pur_cd =tm_purpose_mast.pur_cd and state_cd=? and off_cd=?  ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);

            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                if (rs.getString("short_descr") != null) {
                    if (!shortForm.equals("")) {
                        shortForm = shortForm + "/";
                    }
                    shortForm = shortForm + rs.getString("short_descr");
                }
            }
            String msgMobile = "";

            msgMobile = "[" + regnno + "]%0D%0A"
                    + shortForm + " Transaction(s) approved against vide receipt no "
                    + generatedRcpt
                    + " dated " + JSFUtils.convertToStandardDateFormat(new java.util.Date()) + ". RC Under Preparation.%0D%0A"
                    + "RC can also be downloaded on DigiLocker and mParivahan mobile apps.%0D%0A" + officeName;

            sendSMS(mobileNo, msgMobile, state_cd);
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public static boolean verifyForSmartCard(String state_cd, int off_cd,
            TransactionManager tmgr) throws Exception {
        boolean isSmartCard = false;

        String sql = "select smart_card from "
                + TableList.VM_SMART_CARD_HSRP
                + " where state_cd=? and off_cd=? and smart_card is true";

        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, state_cd);
        ps.setInt(2, off_cd);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();

        if (rs.next()) {
            isSmartCard = true;
        }

        return isSmartCard;
    }

    public static boolean verifyForSmartCardNew(String state_cd, int off_cd,
            TransactionManager tmgr) throws Exception {
        boolean isSmartCard = false;

        String sql = "select smart_card from "
                + TableList.VM_SMART_CARD_HSRP
                + " where state_cd=? and off_cd=? and smartcard_new is true";

        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, state_cd);
        ps.setInt(2, off_cd);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();

        if (rs.next()) {
            isSmartCard = true;
        }

        return isSmartCard;
    }

    public static boolean verifyForSmartCardOld(String state_cd, int off_cd,
            TransactionManager tmgr) throws Exception {
        boolean isSmartCard = false;

        String sql = "select smart_card from "
                + TableList.VM_SMART_CARD_HSRP
                + " where state_cd=? and off_cd=? and smartcard_old is true";

        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, state_cd);
        ps.setInt(2, off_cd);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();

        if (rs.next()) {
            isSmartCard = true;
        }

        return isSmartCard;
    }

    public static void insertForSmartCard(String appl_no, String regn_no, String state_cd, int off_cd, int pur_cd, TmConfigurationDobj tmConfig, TransactionManager tmgr) throws Exception {
        String empCode = "";
        Long user_cd = Long.parseLong(Util.getEmpCode());
        if (pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE && (Util.getUserCategory().equalsIgnoreCase(TableConstants.User_Dealer))) {
            String DealerCode = getDealerCode(user_cd, state_cd, off_cd);
            if (DealerCode != null && !DealerCode.isEmpty()) {
                if (DealerCode.length() > 10) {
                    DealerCode = DealerCode.substring(DealerCode.length() - 10);
                }
            } else {
                throw new VahanException("Error in finding Dealer Code while sending the information for Smart Card.");
            }
            empCode = String.format("%1$-10s", DealerCode);
        } else {
            empCode = String.format("%1$-10s", Util.getEmpCode());
        }
        boolean isSmartCardNew = verifyForSmartCardNew(state_cd, off_cd, tmgr);
        boolean isSmartCardOld = verifyForSmartCardOld(state_cd, off_cd, tmgr);
        if (isSmartCardNew) {
            SmartCardImpl.insertIntoSmartCardTempDtls(regn_no, state_cd, off_cd, appl_no, tmgr);
        }
        if (verifyForHsrp(state_cd, off_cd, tmgr) && tmConfig.isRc_after_hsrp() && isSmartCardOld) {
            String sql = "INSERT INTO " + TableList.VA_SMART_CARD_TEMP
                    + "     SELECT ?, ?, regexp_replace(vehregno, '[\\\"'';`]', ' ','g'), regdate, regexp_replace(ownername, '[\"'';`]', ' ','g'), "
                    + "       regexp_replace(fname, '[\"'';`]', ' ','g'), regexp_replace(caddress, '[\"'';`]', ' ','g'), regexp_replace(manufacturer, '[\\\"'';`]', ' ','g'), "
                    + "       regexp_replace(modelno, '[\"'';`]', ' ','g'), regexp_replace(colour, '[\\\"'';`]', ' ','g'), regexp_replace(fuel, '[\\\"'';`]', ' ','g'), regexp_replace(vehclass, '[\\\"'';`]', ' ','g'), regexp_replace(bodytype, '[\"'';`]', ' ','g'), seatcap, standcap, "
                    + "       manufdate, unladenwt, cubiccap, wheelbase, noofcylin, ownerserial, "
                    + "       regexp_replace(chasisno, '[\\\"'';`]', ' ','g'), regexp_replace(engineno, '[\\\"'';`]', ' ','g'), taxpaidupto, regnvalidity, regexp_replace(?, '[\\\"'';`]', ' ','g'), "
                    + "       regexp_replace(finname, '[\"'';`]', ' ','g'), regexp_replace(finaddress, '[\"'';`]', ' ','g'),"
                    + "       hypofrom, hypoto, regexp_replace(nocno, '[\\\"'';`]', ' ','g'), stateto, rtoto, "
                    + "       regexp_replace(ncrbclearno, '[\\\"'';`]', ' ','g'), nocissuedt, inscompname, regexp_replace(coverpolicyno, '[\"'';`\t]', ' ','g'), instype, "
                    + "       insvalidupto, puccentercode, pucvalidupto, taxamount, fine, regexp_replace(exemptrecptno, '[\\\"'';`]', ' ','g'), "
                    + "       paymentdt, taxvalidfrom, taxvalidto, exemption, drtocode, buflag, "
                    + "       fitvalidupto, fitinsofficer, fitlocation, grossvehwt, semitrailers, "
                    + "       tyreinfo, axleinfo, rcpt_no, pur_cd, ?, current_timestamp, status, "
                    + "       flat_file from getSmartCardDetails(?, ?)";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.setString(3, String.format("%1$-10s", empCode));
            ps.setString(4, Util.getEmpCode());
            ps.setString(5, appl_no);
            ps.setString(6, regn_no);
            ServerUtility.validateQueryResult(tmgr, ps.executeUpdate(), ps);

            sql = "INSERT INTO  " + TableList.VA_SMART_CARD
                    + "    SELECT ?,?,rcpt_no,vehregno,pur_cd,?,current_timestamp  from " + TableList.VA_SMART_CARD_TEMP
                    + " where rcpt_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.setString(3, Util.getEmpCode());
            ps.setString(4, appl_no);
            ServerUtility.validateQueryResult(tmgr, ps.executeUpdate(), ps);
        } else if (isSmartCardOld) {
            String sql = "INSERT INTO " + TableList.SMART_CARD
                    + "     SELECT regexp_replace(vehregno, '[\\\"'';`]', ' ','g'), regdate, regexp_replace(ownername, '[\"'';`]', ' ','g'), "
                    + "       regexp_replace(fname, '[\"'';`]', ' ','g'), regexp_replace(caddress, '[\"'';`]', ' ','g'), regexp_replace(manufacturer, '[\\\"'';`]', ' ','g'), "
                    + "       regexp_replace(modelno,'[\"'';`]', ' ','g'), regexp_replace(colour, '[\\\"'';`]', ' ','g'), regexp_replace(fuel, '[\\\"'';`]', ' ','g'), regexp_replace(vehclass, '[\\\"'';`]', ' ','g'), regexp_replace(bodytype, '[\"'';`]', ' ','g'), seatcap, standcap, "
                    + "       manufdate, unladenwt, cubiccap, wheelbase, noofcylin, ownerserial, "
                    + "       regexp_replace(chasisno, '[\\\"'';`]', ' ','g'), regexp_replace(engineno, '[\\\"'';`]', ' ','g'), taxpaidupto, regnvalidity, regexp_replace(?, '[\\\"'';`]', ' ','g'), "
                    + "       regexp_replace(finname, '[\"'';`]', ' ','g'), regexp_replace(finaddress, '[\"'';`]', ' ','g'),"
                    + "       hypofrom, hypoto, regexp_replace(nocno, '[\\\"'';`]', ' ','g'), stateto, rtoto, "
                    + "       regexp_replace(ncrbclearno, '[\\\"'';`]', ' ','g'), nocissuedt, inscompname,regexp_replace(coverpolicyno, '[\"'';`\t]', ' ','g') , instype, "
                    + "       insvalidupto, puccentercode, pucvalidupto, taxamount, fine, regexp_replace(exemptrecptno, '[\\\"'';`]', ' ','g'), "
                    + "       paymentdt, taxvalidfrom, taxvalidto, exemption, drtocode, buflag, "
                    + "       fitvalidupto, fitinsofficer, fitlocation, grossvehwt, semitrailers, "
                    + "       tyreinfo, axleinfo, rcpt_no, pur_cd, ?, current_timestamp, status, "
                    + "       flat_file from getSmartCardDetails(?, ?)";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, String.format("%1$-10s", empCode));
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, appl_no);
            ps.setString(4, regn_no);
            ServerUtility.validateQueryResult(tmgr, ps.executeUpdate(), ps);

            sql = "INSERT INTO  " + TableList.RC_BE_TO_BO
                    + "    SELECT regexp_replace(vehregno, '[\\\"'';`]', ' ','g'), regdate, regexp_replace(ownername, '[\"'';`]', ' ','g'), "
                    + "       regexp_replace(fname, '[\"'';`]', ' ','g'), regexp_replace(caddress, '[\"'';`]', ' ','g'), regexp_replace(manufacturer, '[\\\"'';`]', ' ','g'), \n"
                    + "       regexp_replace(modelno, '[\"'';`]', ' ','g'), regexp_replace(colour, '[\\\"'';`]', ' ','g'), regexp_replace(fuel, '[\\\"'';`]', ' ','g'), regexp_replace(vehclass, '[\\\"'';`]', ' ','g'), regexp_replace(bodytype, '[\"'';`]', ' ','g'), seatcap, standcap, \n"
                    + "       manufdate, unladenwt, cubiccap, wheelbase, noofcylin, ownerserial, \n"
                    + "       regexp_replace(chasisno, '[\\\"'';`]', ' ','g'), regexp_replace(engineno, '[\\\"'';`]', ' ','g'), taxpaidupto, regnvalidity, regexp_replace(approvingauth, '[\\\"'';`]', ' ','g'), \n"
                    + "       regexp_replace(finname, '[\"'';`]', ' ','g'),regexp_replace(finaddress, '[\"'';`]', ' ','g') ,"
                    + "       hypofrom, hypoto, regexp_replace(nocno, '[\\\"'';`]', ' ','g'), stateto, rtoto, \n"
                    + "       regexp_replace(ncrbclearno, '[\\\"'';`]', ' ','g'), nocissuedt, inscompname,regexp_replace(coverpolicyno, '[\"'';`\t]', ' ','g') , instype, \n"
                    + "       insvalidupto, puccentercode, pucvalidupto, taxamount, fine, regexp_replace(exemptrecptno, '[\\\"'';`]', ' ','g'), \n"
                    + "       paymentdt, taxvalidfrom, taxvalidto, exemption, drtocode, buflag, \n"
                    + "       fitvalidupto, fitinsofficer, fitlocation, grossvehwt, semitrailers, \n"
                    + "       tyreinfo, axleinfo, rcpt_no, pur_cd, ?, current_timestamp, status from " + TableList.SMART_CARD
                    + " where rcpt_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, appl_no);
            ServerUtility.validateQueryResult(tmgr, ps.executeUpdate(), ps);

            sql = "INSERT INTO  " + TableList.VA_SMART_CARD
                    + "    SELECT ?,?,rcpt_no,vehregno,pur_cd,?,current_timestamp  from " + TableList.RC_BE_TO_BO
                    + " where rcpt_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.setString(3, Util.getEmpCode());
            ps.setString(4, appl_no);
            ServerUtility.validateQueryResult(tmgr, ps.executeUpdate(), ps);

        }
    }

    /**
     * @author Kartikey Singh
     */
    public static void insertForSmartCard(String appl_no, String regn_no, String state_cd, int off_cd, int pur_cd, TmConfigurationDobj tmConfig, TransactionManager tmgr,
            String userCategory, String utilEmpCode, SessionVariablesModel sessionVariablesModel) throws Exception {
        String empCode = "";
        Long user_cd = Long.parseLong(utilEmpCode);
        if (pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE && (userCategory.equalsIgnoreCase(TableConstants.User_Dealer))) {
            String DealerCode = getDealerCode(user_cd, state_cd, off_cd);
            if (DealerCode != null && !DealerCode.isEmpty()) {
                if (DealerCode.length() > 10) {
                    DealerCode = DealerCode.substring(DealerCode.length() - 10);
                }
            } else {
                throw new VahanException("Error in finding Dealer Code while sending the information for Smart Card.");
            }
            empCode = String.format("%1$-10s", DealerCode);
        } else {
            empCode = String.format("%1$-10s", utilEmpCode);
        }
        boolean isSmartCardNew = verifyForSmartCardNew(state_cd, off_cd, tmgr);
        boolean isSmartCardOld = verifyForSmartCardOld(state_cd, off_cd, tmgr);
        if (isSmartCardNew) {
            SmartCardImplementation.insertIntoSmartCardTempDtls(regn_no, state_cd, off_cd, appl_no, tmgr, tmConfig, sessionVariablesModel);
        }
        if (verifyForHsrp(state_cd, off_cd, tmgr) && tmConfig.isRc_after_hsrp() && isSmartCardOld) {
            String sql = "INSERT INTO " + TableList.VA_SMART_CARD_TEMP
                    + "     SELECT ?, ?, regexp_replace(vehregno, '[\\\"'';`]', ' ','g'), regdate, regexp_replace(ownername, '[\"'';`]', ' ','g'), "
                    + "       regexp_replace(fname, '[\"'';`]', ' ','g'), regexp_replace(caddress, '[\"'';`]', ' ','g'), regexp_replace(manufacturer, '[\\\"'';`]', ' ','g'), "
                    + "       regexp_replace(modelno, '[\"'';`]', ' ','g'), regexp_replace(colour, '[\\\"'';`]', ' ','g'), regexp_replace(fuel, '[\\\"'';`]', ' ','g'), regexp_replace(vehclass, '[\\\"'';`]', ' ','g'), regexp_replace(bodytype, '[\"'';`]', ' ','g'), seatcap, standcap, "
                    + "       manufdate, unladenwt, cubiccap, wheelbase, noofcylin, ownerserial, "
                    + "       regexp_replace(chasisno, '[\\\"'';`]', ' ','g'), regexp_replace(engineno, '[\\\"'';`]', ' ','g'), taxpaidupto, regnvalidity, regexp_replace(?, '[\\\"'';`]', ' ','g'), "
                    + "       regexp_replace(finname, '[\"'';`]', ' ','g'), regexp_replace(finaddress, '[\"'';`]', ' ','g'),"
                    + "       hypofrom, hypoto, regexp_replace(nocno, '[\\\"'';`]', ' ','g'), stateto, rtoto, "
                    + "       regexp_replace(ncrbclearno, '[\\\"'';`]', ' ','g'), nocissuedt, inscompname, regexp_replace(coverpolicyno, '[\"'';`\t]', ' ','g'), instype, "
                    + "       insvalidupto, puccentercode, pucvalidupto, taxamount, fine, regexp_replace(exemptrecptno, '[\\\"'';`]', ' ','g'), "
                    + "       paymentdt, taxvalidfrom, taxvalidto, exemption, drtocode, buflag, "
                    + "       fitvalidupto, fitinsofficer, fitlocation, grossvehwt, semitrailers, "
                    + "       tyreinfo, axleinfo, rcpt_no, pur_cd, ?, current_timestamp, status, "
                    + "       flat_file from getSmartCardDetails(?, ?)";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.setString(3, String.format("%1$-10s", empCode));
            ps.setString(4, utilEmpCode);
            ps.setString(5, appl_no);
            ps.setString(6, regn_no);
            ServerUtility.validateQueryResult(tmgr, ps.executeUpdate(), ps);

            sql = "INSERT INTO  " + TableList.VA_SMART_CARD
                    + "    SELECT ?,?,rcpt_no,vehregno,pur_cd,?,current_timestamp  from " + TableList.VA_SMART_CARD_TEMP
                    + " where rcpt_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.setString(3, utilEmpCode);
            ps.setString(4, appl_no);
            ServerUtility.validateQueryResult(tmgr, ps.executeUpdate(), ps);
        } else if (isSmartCardOld) {
            String sql = "INSERT INTO " + TableList.SMART_CARD
                    + "     SELECT regexp_replace(vehregno, '[\\\"'';`]', ' ','g'), regdate, regexp_replace(ownername, '[\"'';`]', ' ','g'), "
                    + "       regexp_replace(fname, '[\"'';`]', ' ','g'), regexp_replace(caddress, '[\"'';`]', ' ','g'), regexp_replace(manufacturer, '[\\\"'';`]', ' ','g'), "
                    + "       regexp_replace(modelno,'[\"'';`]', ' ','g'), regexp_replace(colour, '[\\\"'';`]', ' ','g'), regexp_replace(fuel, '[\\\"'';`]', ' ','g'), regexp_replace(vehclass, '[\\\"'';`]', ' ','g'), regexp_replace(bodytype, '[\"'';`]', ' ','g'), seatcap, standcap, "
                    + "       manufdate, unladenwt, cubiccap, wheelbase, noofcylin, ownerserial, "
                    + "       regexp_replace(chasisno, '[\\\"'';`]', ' ','g'), regexp_replace(engineno, '[\\\"'';`]', ' ','g'), taxpaidupto, regnvalidity, regexp_replace(?, '[\\\"'';`]', ' ','g'), "
                    + "       regexp_replace(finname, '[\"'';`]', ' ','g'), regexp_replace(finaddress, '[\"'';`]', ' ','g'),"
                    + "       hypofrom, hypoto, regexp_replace(nocno, '[\\\"'';`]', ' ','g'), stateto, rtoto, "
                    + "       regexp_replace(ncrbclearno, '[\\\"'';`]', ' ','g'), nocissuedt, inscompname,regexp_replace(coverpolicyno, '[\"'';`\t]', ' ','g') , instype, "
                    + "       insvalidupto, puccentercode, pucvalidupto, taxamount, fine, regexp_replace(exemptrecptno, '[\\\"'';`]', ' ','g'), "
                    + "       paymentdt, taxvalidfrom, taxvalidto, exemption, drtocode, buflag, "
                    + "       fitvalidupto, fitinsofficer, fitlocation, grossvehwt, semitrailers, "
                    + "       tyreinfo, axleinfo, rcpt_no, pur_cd, ?, current_timestamp, status, "
                    + "       flat_file from getSmartCardDetails(?, ?)";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, String.format("%1$-10s", empCode));
            ps.setString(2, utilEmpCode);
            ps.setString(3, appl_no);
            ps.setString(4, regn_no);
            ServerUtility.validateQueryResult(tmgr, ps.executeUpdate(), ps);

            sql = "INSERT INTO  " + TableList.RC_BE_TO_BO
                    + "    SELECT regexp_replace(vehregno, '[\\\"'';`]', ' ','g'), regdate, regexp_replace(ownername, '[\"'';`]', ' ','g'), "
                    + "       regexp_replace(fname, '[\"'';`]', ' ','g'), regexp_replace(caddress, '[\"'';`]', ' ','g'), regexp_replace(manufacturer, '[\\\"'';`]', ' ','g'), \n"
                    + "       regexp_replace(modelno, '[\"'';`]', ' ','g'), regexp_replace(colour, '[\\\"'';`]', ' ','g'), regexp_replace(fuel, '[\\\"'';`]', ' ','g'), regexp_replace(vehclass, '[\\\"'';`]', ' ','g'), regexp_replace(bodytype, '[\"'';`]', ' ','g'), seatcap, standcap, \n"
                    + "       manufdate, unladenwt, cubiccap, wheelbase, noofcylin, ownerserial, \n"
                    + "       regexp_replace(chasisno, '[\\\"'';`]', ' ','g'), regexp_replace(engineno, '[\\\"'';`]', ' ','g'), taxpaidupto, regnvalidity, regexp_replace(approvingauth, '[\\\"'';`]', ' ','g'), \n"
                    + "       regexp_replace(finname, '[\"'';`]', ' ','g'),regexp_replace(finaddress, '[\"'';`]', ' ','g') ,"
                    + "       hypofrom, hypoto, regexp_replace(nocno, '[\\\"'';`]', ' ','g'), stateto, rtoto, \n"
                    + "       regexp_replace(ncrbclearno, '[\\\"'';`]', ' ','g'), nocissuedt, inscompname,regexp_replace(coverpolicyno, '[\"'';`\t]', ' ','g') , instype, \n"
                    + "       insvalidupto, puccentercode, pucvalidupto, taxamount, fine, regexp_replace(exemptrecptno, '[\\\"'';`]', ' ','g'), \n"
                    + "       paymentdt, taxvalidfrom, taxvalidto, exemption, drtocode, buflag, \n"
                    + "       fitvalidupto, fitinsofficer, fitlocation, grossvehwt, semitrailers, \n"
                    + "       tyreinfo, axleinfo, rcpt_no, pur_cd, ?, current_timestamp, status from " + TableList.SMART_CARD
                    + " where rcpt_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, utilEmpCode);
            ps.setString(2, appl_no);
            ServerUtility.validateQueryResult(tmgr, ps.executeUpdate(), ps);

            sql = "INSERT INTO  " + TableList.VA_SMART_CARD
                    + "    SELECT ?,?,rcpt_no,vehregno,pur_cd,?,current_timestamp  from " + TableList.RC_BE_TO_BO
                    + " where rcpt_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.setString(3, utilEmpCode);
            ps.setString(4, appl_no);
            ServerUtility.validateQueryResult(tmgr, ps.executeUpdate(), ps);
        }
    }

    /*public static String getDealerCode(long userCode, String stateCd, int offCd) throws VahanException {
     PreparedStatement ps = null;
     String sql = null;
     String dealerCode = "";
     TransactionManagerReadOnly tmgr = null;
     try {
     tmgr = new TransactionManagerReadOnly("getDealerCode");
     if (ServerUtility.validateDealerUserForAllOffice(userCode)) {
     sql = "select m.dealer_cd \n"
     + "from  vm_dealer_mast m left outer join tm_user_permissions up\n"
     + "on m.dealer_cd = up.dealer_cd \n"
     + "where m.state_cd = ? and up.user_cd = ? ";

     ps = tmgr.prepareStatement(sql);
     ps.setString(1, stateCd);
     ps.setLong(2, userCode);
     } else {
     sql = "select m.dealer_cd \n"
     + "from  vm_dealer_mast m left outer join tm_user_permissions up\n"
     + "on m.dealer_cd = up.dealer_cd \n"
     + "where m.state_cd = ? and m.off_cd = ? and up.user_cd = ? ";

     ps = tmgr.prepareStatement(sql);
     ps.setString(1, stateCd);
     ps.setInt(2, offCd);
     ps.setLong(3, userCode);
     }
     RowSet rs = tmgr.fetchDetachedRowSet();

     if (rs.next()) {
     dealerCode = rs.getString("dealer_cd");
     }
     } catch (VahanException vex) {
     throw vex;
     } catch (SQLException e) {
     LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
     throw new VahanException("Something went wrong during getting details of dealer code, Please contact to the system administrator.");
     } catch (Exception e) {
     LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
     throw new VahanException("Something went wrong during getting details of dealer code, Please contact to the system administrator.");
     } finally {
     try {
     if (tmgr != null) {
     tmgr.release();
     }
     } catch (Exception e) {
     LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
     }
     }
     return dealerCode;
     }*/
    /**
     * @author Kartikey Singh
     * @Note Hid the original method as they had same arguments
     * @Difference We call ServerUtility.validateDealerUserForAllOffice with two
     * arguments instead of one
     */
    public static String getDealerCode(long userCode, String stateCd, int offCd) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        String dealerCode = "";
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("getDealerCode");
            if (ServerUtility.validateDealerUserForAllOffice(userCode, stateCd)) {
                sql = "select m.dealer_cd \n"
                        + "from  vm_dealer_mast m left outer join tm_user_permissions up\n"
                        + "on m.dealer_cd = up.dealer_cd \n"
                        + "where m.state_cd = ? and up.user_cd = ? ";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setLong(2, userCode);
            } else {
                sql = "select m.dealer_cd \n"
                        + "from  vm_dealer_mast m left outer join tm_user_permissions up\n"
                        + "on m.dealer_cd = up.dealer_cd \n"
                        + "where m.state_cd = ? and m.off_cd = ? and up.user_cd = ? ";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setInt(2, offCd);
                ps.setLong(3, userCode);
            }
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {
                dealerCode = rs.getString("dealer_cd");
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong during getting details of dealer code, Please contact to the system administrator.");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong during getting details of dealer code, Please contact to the system administrator.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dealerCode;
    }

    public static void insertForPrint(String appl_no, String regn_no, String state_cd, int off_cd, TransactionManager tmgr) throws Exception {
        String sql = "INSERT INTO " + TableList.VA_RC_PRINT + "(appl_no, regn_no, op_dt,state_cd,off_cd)"
                + "    VALUES (?, ?, current_timestamp,?,?)";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, appl_no);
        ps.setString(2, regn_no);
        ps.setString(3, state_cd);
        ps.setInt(4, off_cd);
        ServerUtility.validateQueryResult(tmgr, ps.executeUpdate(), ps);
    }

    public static List<String> getUsersLastTransaction(String empCode) {
        ArrayList listTransactions = new ArrayList();
        TransactionManager tmgr = null;
        try {
            if (empCode == null) {
                return listTransactions;
            }
            tmgr = new TransactionManager("getUsersLastTransaction");
            String sql = "select * from " + TableList.VT_USER_LATEST_ACTIONS + " where user_cd=? and op_dt > current_date-5 order by op_dt desc limit 10";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(empCode));
            RowSet rs = tmgr.fetchDetachedRowSet();
            int i = 0;
            while (rs.next() && i < 10) {
                listTransactions.add(rs.getString("message"));
                i++;
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return listTransactions;
    }

    public static void insertUsersTransactionMessage(String message, int counter, TransactionManager tmgr) throws VahanException, SQLException {
        String sql = "insert into " + TableList.VT_USER_LATEST_ACTIONS + " (user_cd, message,op_dt) values(?, ?,current_timestamp + (interval '" + counter + " second'))";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setLong(1, Long.parseLong(Util.getEmpCode()));
        ps.setString(2, message);
        ps.executeUpdate();
    }

    /**
     * @author Kartikey Singh
     */
    public static void insertUsersTransactionMessage(String message, int counter, TransactionManager tmgr, String empCode) throws VahanException, SQLException {
        String sql = "insert into " + TableList.VT_USER_LATEST_ACTIONS + " (user_cd, message,op_dt) values(?, ?,current_timestamp + (interval '" + counter + " second'))";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setLong(1, Long.parseLong(empCode));
        ps.setString(2, message);
        ps.executeUpdate();
    }

    public static String getUserCategory(long emp_cd) throws VahanException {
        String user_catg = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        try {
            tmgr = new TransactionManagerReadOnly("getUserCategory");
            sql = "Select user_catg from " + TableList.TM_USER_INFO + " where user_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, emp_cd);

            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                user_catg = rs.getString("user_catg");
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("getUserCategory" + ex.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                throw new VahanException("getUserCategory" + e.getMessage());
            }
        }
        return user_catg;
    }

    public static String generateRandomAlphaNumeric(int no) {
        String rtnStr = "";
        for (int i = 0; i < no; i++) {
            if ((generateRandomNumber(1, 200) % 2) == 1) {
                rtnStr = rtnStr + getRandomNumber();
            } else {
                rtnStr = rtnStr + getRandomChar();
            }
        }
        return rtnStr;
    }

    private static String getRandomNumber() {
        return String.valueOf(generateRandomNumber(0, 9));
    }

    private static char getRandomChar() {
        char c = 'a';
        if ((generateRandomNumber(1, 200) % 2) == 1) {
            c = (char) generateRandomNumber(65, 90);
        } else {
            c = (char) generateRandomNumber(97, 122);
        }
        return c;
    }

    public static int generateRandomNumber(int min, int max) {
        return ((int) (Math.random() * (max - min + 1)) + min);
    }

    public static List getOffCode(String selectedEmp) {
        List off_cd = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        RowSet rs;
        String sql = null;
        String offList = "";
        try {
            tmgr = new TransactionManagerReadOnly("getOffCode");
            sql = "Select assigned_office from " + TableList.TM_USER_PERMISSIONS + " where user_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(selectedEmp));
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                offList = rs.getString("assigned_office");
            }
            off_cd = makeList(offList);

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return off_cd;
    }

    public static Owner_dobj getOnwerDobjFromHomologation(String chasiNo, String engineNo, String stateCd, int offCd, boolean homoEngValidate) throws VahanException {
        Owner_dobj owner_dobj = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = "SELECT * FROM gethomologationchassisinfo(?) ";
        try {
            tmgr = new TransactionManagerReadOnly("getOnwerDobjFromHomologation");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, chasiNo.toUpperCase().trim());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) {
                owner_dobj = new Owner_dobj();
                owner_dobj.setOwnerDetailsDo(new OwnerDetailsDobj());
                String engnNo = rs.getString("eng_no");
                if (engnNo.length() > 5) {
                    engnNo = engnNo.substring(engnNo.length() - 5);
                }
                if (engnNo != null && !engnNo.equals("") && !engnNo.equals(engineNo) && homoEngValidate) {
                    throw new VahanException("Chassis Details Found on Homologation Portal but Chassis No and Engine No combination is not valid!!!");
                }
                owner_dobj.setEng_no(rs.getString("eng_no"));
                owner_dobj.setChasi_no(rs.getString("chasi_no"));
                owner_dobj.setMaker(rs.getInt("maker_code"));
                owner_dobj.setMaker_model(rs.getString("unique_model_ref_no"));
                owner_dobj.setVch_purchase_as(rs.getString("vehicle_make_as"));
                owner_dobj.setHp(rs.getFloat("engine_power") * 1.34f);  //Metric HP Conversion-> 1 kw = 1.36 BHP and British Mechanic HP-> 1 kw = 1.34 BHP
                owner_dobj.setSeat_cap(rs.getInt("seat_cap"));
                owner_dobj.setUnld_wt(rs.getInt("unld_wt"));
                owner_dobj.setLd_wt(rs.getInt("gvw"));
                owner_dobj.setGcw(rs.getInt("gcw"));
                owner_dobj.setFuel(rs.getInt("fuel"));
                owner_dobj.setBody_type(rs.getString("body_type"));
                owner_dobj.setNo_cyl(rs.getInt("no_cyl"));
                owner_dobj.setWheelbase(rs.getInt("wheelbase"));
                owner_dobj.setNorms(rs.getInt("norms"));
                owner_dobj.setDealer_cd(rs.getString("dealer_cd"));
                owner_dobj.setCubic_cap(rs.getFloat("cubic_cap"));
                owner_dobj.setVch_catg(rs.getString("vch_catg"));
                owner_dobj.setLength(rs.getInt("length"));
                owner_dobj.setWidth(rs.getInt("width"));
                owner_dobj.setHeight(rs.getInt("height"));
                owner_dobj.setRegn_dt(new Date());
                owner_dobj.setFeatureCode(rs.getString("feature_cd"));
                owner_dobj.setColorCode(rs.getString("color_cd"));
                owner_dobj.setHomoVchClass(rs.getString("vch_class"));
                owner_dobj.setC_state(stateCd);
                owner_dobj.setModelManuLocCode(rs.getString("model_manu_loc"));
                owner_dobj.setModelNameOnTAC(rs.getString("model_name_on_tac"));
                if (!CommonUtils.isNullOrBlank(owner_dobj.getModelManuLocCode())) {
                    switch (owner_dobj.getModelManuLocCode()) {
                        case "M":
                            owner_dobj.setModelManuLocCodeDescr("Manufactured in India");
                            break;
                        case "A":
                            owner_dobj.setModelManuLocCodeDescr("Assembled in India(Imported)");
                            break;
                        case "I":
                            owner_dobj.setModelManuLocCodeDescr("Fully Built Imported");
                            break;
                    }
                }
                int month = 0;
                int year = 0;
                String monthYear = String.format("%06d", rs.getInt("month_year"));
                if (monthYear.length() == 6) {
                    month = Integer.parseInt(monthYear.substring(0, 2));
                    year = Integer.parseInt(monthYear.substring(2, 6));
                }
                owner_dobj.setManu_mon(month);
                owner_dobj.setManu_yr(year);

                if (rs.getString("color_name") != null && rs.getString("color_name").length() > 20) {
                    owner_dobj.setColor(rs.getString("color_name").substring(0, 20));
                } else {
                    owner_dobj.setColor(rs.getString("color_name"));
                }
                owner_dobj.getOwnerDetailsDo().setMaker_name(rs.getString("maker_descr"));
                owner_dobj.getOwnerDetailsDo().setModel_name(rs.getString("model_name"));
                owner_dobj.getOwnerDetailsDo().setFuel_descr(rs.getString("fuel_descr"));
                owner_dobj.getOwnerDetailsDo().setModelNameOnTAC(rs.getString("model_name_on_tac"));

                //-----------Filling Axle Details Start-------------------------
                AxleDetailsDobj axleDobj = new AxleDetailsDobj();
                owner_dobj.setAxleDobj(axleDobj);

                owner_dobj.getAxleDobj().setTf_Front(rs.getInt("f_axle_weight"));
                owner_dobj.getAxleDobj().setTf_Front1(rs.getString("f_axle_descp"));
                owner_dobj.getAxleDobj().setTf_Rear(rs.getInt("r_axle_weight"));
                owner_dobj.getAxleDobj().setTf_Rear1(rs.getString("r_axle_descp"));
                owner_dobj.getAxleDobj().setTf_Other(rs.getInt("o1_axle_weight"));
                owner_dobj.getAxleDobj().setTf_Other1(rs.getString("o1_axle_descp"));
                owner_dobj.getAxleDobj().setTf_Tandem(rs.getInt("t_axle_weight"));
                owner_dobj.getAxleDobj().setTf_Tandem1(rs.getString("t_axle_descp"));

                owner_dobj.setPurchase_dt(new Date());
                owner_dobj.setOwner_sr(1);

                String showroomPriceSql = "SELECT gethomologationshowroomprice(?,?,?,?,?,?) as salePrice";

                ps = tmgr.prepareStatement(showroomPriceSql);
                ps.setInt(1, rs.getInt("maker_code"));
                ps.setString(2, rs.getString("unique_model_ref_no"));
                ps.setString(3, stateCd);
                ps.setInt(4, offCd);
                ps.setString(5, rs.getString("color_cd"));
                ps.setString(6, rs.getString("feature_cd"));

                RowSet rs1 = tmgr.fetchDetachedRowSet();
                if (rs1.next()) {
                    owner_dobj.setSale_amt(rs1.getInt("salePrice"));
                }
            }
            if (owner_dobj != null) {
                validateHomoData(owner_dobj.getVch_purchase_as(), owner_dobj.getNorms(), owner_dobj.getFuel(), owner_dobj.getSeat_cap(), owner_dobj.getNo_cyl(), owner_dobj.getManu_mon(), owner_dobj.getManu_yr(), owner_dobj.getLd_wt(), owner_dobj.getUnld_wt(), owner_dobj.getHomoVchClass());
            }

        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in getting homologation details");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return owner_dobj;
    }//end of getOnwerDobjFromHomologation

    public static void validateHomoData(String vchPurchase, int norms, int fuel, int seatCap, int noCyl, int manuMonth, int manuYear, int ldWwight, int unldWeight, String homoVchCls) throws VahanException {
        String homoValidateStr = null;
        if (CommonUtils.isNullOrBlank(vchPurchase)) {
            homoValidateStr = "Blank Vehicle Purchase As :";
        } else if (fuel <= 0) {
            homoValidateStr = "Blank Fuel";
        } else if (noCyl <= 0 && fuel != TableConstants.VM_FUEL_TYPE_ELECTRIC && fuel != TableConstants.VM_FUEL_TYPE_OTHERS) {
            homoValidateStr = "No of Cylinder:" + noCyl;
        } else if (seatCap <= 0 && !TableConstants.VALIDATE_SEAT_CAP_ZERO.contains("," + homoVchCls + ",")) {
            homoValidateStr = "Seating Cap is:" + seatCap;
        } else if (ldWwight <= 0 && !TableConstants.VALIDATE_LADEN_WT.contains("," + homoVchCls + ",")) {
            homoValidateStr = "Laden Weight (" + ldWwight + ")";
        } else if (unldWeight <= 0 && !TableConstants.VALIDATE_UNLDEN_WT.contains("," + homoVchCls + ",")) {
            homoValidateStr = "Unladen Weight (" + unldWeight + ")";
        } else if (ldWwight < unldWeight && !TableConstants.VALIDATE_LADEN_UNLDEN_WT.contains("," + homoVchCls + ",")) {
            homoValidateStr = "Laden Weight (" + ldWwight + ") less than Unladen Weight (" + unldWeight + ")";
        } else if (norms <= 0) {
            homoValidateStr = "Blank Norms :" + norms;
        } else if (manuMonth <= 0) {
            homoValidateStr = "Manufacture Month :" + manuMonth;
        } else if (manuYear <= 0) {
            homoValidateStr = "Manufacture Year :" + manuYear;
        }
        if (homoValidateStr != null) {
            throw new VahanException("Invalid data from homologation : " + homoValidateStr);
        }
    }

    public static void validateChassisEngineCombination(String chasiNo, String engineNo) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        String sql = "SELECT * FROM gethomologationchassisinfo(?)";
        try {
            tmgr = new TransactionManagerReadOnly("validateChassisEngineCombination");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, chasiNo.toUpperCase().trim());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                String engnNo = rs.getString("eng_no");
                if (engnNo != null && !engnNo.equals("") && !engnNo.equals(engineNo)) {
                    throw new VahanException("Chassis details found on Homologation Portal but Chassis No and Engine No combination is not valid!!!");
                }
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during validation of Engine and Chassis No Combination from Homologation Portal,Please Contact to the System Administrator");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
    }//end of validateChassisEngineCombination

    public static boolean checkHypthOrNot(String regnNo, String stateCd) throws Exception {
        boolean isHypth = false;
        HpaImpl impl = new HpaImpl();

        if (regnNo != null) {
            regnNo = regnNo.toUpperCase();
        }

        List<HpaDobj> listHypthDetails = impl.getHypoDetails(null, TableConstants.VM_TRANSACTION_MAST_REM_HYPO, regnNo, true, stateCd);

        if (listHypthDetails != null && !listHypthDetails.isEmpty()) {
            isHypth = true;
        }

        return isHypth;
    }//end of checkHypthOrNot

    public static SmartCardDobj getSmartCardDetailsFromRcBeToBo(String regn_no) throws VahanException {

        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        SmartCardDobj smartCardDobj = null;

        try {

            if (regn_no != null) {
                regn_no = regn_no.toUpperCase();
            }

            tmgr = new TransactionManager("getApplNoFromRcBeToBo");
            sql = "SELECT rcpt_no,vehregno,approvingauth,deal_cd,op_dt FROM " + TableList.RC_BE_TO_BO + " WHERE vehregno in (?, rpad(?, 10, ?))";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.setString(3, " ");

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                smartCardDobj = new SmartCardDobj();
                smartCardDobj.setAppl_no(rs.getString("rcpt_no"));
                smartCardDobj.setVehregno(rs.getString("vehregno"));
                smartCardDobj.setApprovingauth(rs.getString("approvingauth"));
                smartCardDobj.setDeal_cd(rs.getString("deal_cd"));
                smartCardDobj.setOp_dt(rs.getTimestamp("op_dt"));
            } else if (smartCardDobj == null) {
                sql = "SELECT rcpt_no,vehregno,state_cd,off_cd,approvingauth,deal_cd,op_dt FROM " + TableList.VA_SMART_CARD_TEMP + " WHERE vehregno in (?, rpad(?, 10, ?))";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regn_no);
                ps.setString(2, regn_no);
                ps.setString(3, " ");
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) // found
                {
                    smartCardDobj = new SmartCardDobj();
                    smartCardDobj.setAppl_no(rs.getString("rcpt_no"));
                    smartCardDobj.setVehregno(rs.getString("vehregno"));
                    smartCardDobj.setState_cd(rs.getString("state_cd"));
                    smartCardDobj.setOff_cd(rs.getInt("off_cd"));
                    smartCardDobj.setApprovingauth(rs.getString("approvingauth"));
                    smartCardDobj.setDeal_cd(rs.getString("deal_cd"));
                    smartCardDobj.setOp_dt(rs.getTimestamp("op_dt"));
                }
            } else {
                sql = "SELECT appl_no,regn_no,state_cd,off_cd,op_dt FROM " + TableList.VT_RC_BE_TO_BO + " WHERE regn_no = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regn_no);
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) // found
                {
                    smartCardDobj = new SmartCardDobj();
                    smartCardDobj.setAppl_no(rs.getString("appl_no"));
                    smartCardDobj.setVehregno(rs.getString("regn_no"));
                    smartCardDobj.setState_cd(rs.getString("state_cd"));
                    smartCardDobj.setOff_cd(rs.getInt("off_cd"));
                    smartCardDobj.setApprovingauth("");
                    smartCardDobj.setDeal_cd("");
                    smartCardDobj.setOp_dt(rs.getTimestamp("op_dt"));
                }
            }

        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during Getting Details of SmartCard, Please Contact to the System Administrator");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during Getting Details of SmartCard, Please Contact to the System Administrator");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return smartCardDobj;
    }//end of getSmartCardDetailsFromRcBeToBo

    public static List<Dealer> getDealerList(String stateCd, int offCd) {
        List listDealer = new ArrayList();
        String sql = "select * from vm_dealer_mast where state_cd in(?,?) and off_cd in(?,?)";
        TransactionManagerReadOnly tmgr = null;
        Dealer dobj = null;
        try {
            tmgr = new TransactionManagerReadOnly("getDealerList");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setString(2, TableConstants.ALL_INDIA);
            ps.setInt(3, offCd);
            ps.setInt(4, TableConstants.ALL_INDIA_OFF_CD);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new Dealer();
                dobj.setDealer_cd(rs.getString("dealer_cd"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setDealer_name(rs.getString("dealer_name"));
                dobj.setDealer_regn_no(rs.getString("dealer_regn_no"));
                dobj.setD_add1(rs.getString("d_add1"));
                dobj.setD_add2(rs.getString("d_add2"));
                dobj.setD_district(rs.getInt("d_district"));
                dobj.setD_pincode(rs.getInt("d_pincode"));
                dobj.setD_state(rs.getString("d_state"));
                dobj.setValid_upto(rs.getString("valid_upto"));
                dobj.setEntered_by(rs.getString("entered_by"));
                dobj.setEntered_on(rs.getString("entered_on"));
                listDealer.add(dobj);
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return listDealer;
    }

    public static List<SelectItem> getDealerListSelectItem(String dealerCd) {
        List listDealer = new ArrayList();
        String sql = "select * from vm_dealer_mast where dealer_cd in (?,?)";
        TransactionManagerReadOnly tmgr = null;

        try {
            tmgr = new TransactionManagerReadOnly("getDealerList");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, dealerCd);
            ps.setString(2, TableConstants.ALL_INDIA_DEALER_CD);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                listDealer.add(new SelectItem(rs.getString("dealer_cd"), rs.getString("dealer_name")));
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return listDealer;
    }

    public static void deleteFromTable(TransactionManager tmgr, String regnNo, String applNo, String tableName) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        String param = null;

        if (regnNo != null && regnNo.trim().length() > 0 && !regnNo.equalsIgnoreCase("NEW")) {
            param = regnNo.toUpperCase().trim();
            sql = "DELETE FROM " + tableName + " WHERE regn_no=?";
        } else if (applNo != null && applNo.trim().length() > 0) {
            param = applNo.toUpperCase().trim();
            sql = "DELETE FROM " + tableName + " WHERE appl_no=?";
        }

        if (param != null && sql != null) {
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, param);
            ps.executeUpdate();
        }
    }

    public static void deleteFromSmartCardTable(TransactionManager tmgr, String regnNo, String tableName) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        sql = "DELETE FROM " + tableName + " WHERE vehregno in (?, rpad(?, 10, ?), ?)";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, regnNo);
        ps.setString(2, regnNo);
        ps.setString(3, " ");
        ps.setString(4, getRegnNoWithSpace(regnNo));
        ps.executeUpdate();
    }

    public static void deleteFromTableByChassis(TransactionManager tmgr, String Chassis_no, String tableName) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        String param = null;

        if (Chassis_no != null && Chassis_no.trim().length() > 0) {
            param = Chassis_no.toUpperCase().trim();
            sql = "DELETE FROM " + tableName + " WHERE chasi_no=?";
        }

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, param);
        ps.executeUpdate();

    }

    public static void insertIntoVhaTable(TransactionManager tmgr, String applNo, String empCode, String tableNameVha, String tableNameVa) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        if (applNo != null && applNo.trim().length() > 0) {
            applNo = applNo.toUpperCase().trim();
            sql = " INSERT INTO " + tableNameVha
                    + "  SELECT current_timestamp as moved_on, ? as moved_by, a.* FROM " + tableNameVa
                    + "  a WHERE a.appl_no=?";
        }

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, empCode);
        ps.setString(2, applNo);
        ps.executeUpdate();

    }

    public static boolean isTaxOnPermit(int vehClass, String stateCd) throws VahanException {
        boolean taxOnPermit = false;
        taxOnPermit = isTransport(vehClass, null);
        //States On which Permit is a parameter for Tax Collection
        //MZ,KL,UK

        return taxOnPermit;
    }

    /**
     * Converts the comma separated items into list.
     *
     * @param code
     * @return
     */
    public static List makeList(String code) {
        List codeInList = null;
        if (!CommonUtils.isNullOrBlank(code)) {
            String[] codeInString = code.split(",");
            codeInList = new ArrayList();
            for (int i = 0; i < codeInString.length; i++) {
                codeInList.add(codeInString[i]);
            }
        }
        return codeInList;
    }

    public static String getStateNameByStateCode(String stateCode) {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        String stateName = null;
        String sql = null;

        try {
            tmgr = new TransactionManagerReadOnly("getStateName");
            sql = "select descr as state_name from tm_state where state_code = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                stateName = rs.getString("state_name");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return stateName;
    }

    public static List<EpayDobj> getCommonChargesForAll(Owner_dobj owner_dobj, Date paymentDate, Date dueDate) throws VahanException {
        List<EpayDobj> list = new ArrayList<>();
        EpayDobj epayDobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = "";
        VehicleParameters parameters = FormulaUtils.fillVehicleParametersFromDobj(owner_dobj, null);
        parameters.setPUR_CD(Util.getSelectedSeat().getPur_cd());
        VahanTaxParameters taxParameters = null;
        TmConfigurationDobj conf = Util.getTmConfiguration();
        try {
            if (Util.getSelectedSeat().getAction_cd() == 99994
                    && Util.getUserStateCode().equals("PB")) {
                return list;
            }

            sql = "select * from " + TableList.VC_ACTION_PURPOSE_MAP + " dl,tm_purpose_mast pm where dl.pur_cd=pm.pur_cd  and state_cd= ? "
                    + " and action in ('ALL')";
            tmgr = new TransactionManager("getTransactionChargesForFee");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                int pur_cd = rs.getInt("pur_cd");
                String pur_cdDescr = rs.getString("descr");
                // Date paymentDate = null;
                long feeValue = 0;
                long fineValue = 0;
                if (paymentDate == null) {
                    paymentDate = new java.util.Date();
                }
                feeValue = getPurposeCodeFee(owner_dobj, owner_dobj.getVh_class(), pur_cd, owner_dobj.getVch_catg());
                dueDate = getDueDateForNewRegistration(pur_cd, owner_dobj);
                fineValue = 0;  //ServerUtility.getPurposeFineAmount(owner_dobj.getState_cd(), pur_cd, feeValue, dueDate, paymentDate, parameters);
                EpayDobj ePayDobj = new EpayDobj();
                ePayDobj.setPurCd(pur_cd);
                ePayDobj.setPurCdDescr(pur_cdDescr);
                ePayDobj.setE_FinePenalty((int) fineValue);
                ePayDobj.setE_TaxFee((int) feeValue);
                ePayDobj.setE_total((int) (feeValue + ePayDobj.getE_FinePenalty()));
                list.add(ePayDobj);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Calculation of ALL transaction Fees");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return list;
    }

    public static Owner_dobj fillOwnerDobjFromAdvanceDobj(AdvanceRegnNo_dobj dobj) {
        Owner_dobj owner_dobj = new Owner_dobj();
        owner_dobj.setOwner_name(dobj.getOwner_name());
        owner_dobj.setF_name(dobj.getF_name());
        owner_dobj.setC_add1(dobj.getC_add1());
        owner_dobj.setC_add2(dobj.getC_add2());
        owner_dobj.setC_district(dobj.getC_district());
        owner_dobj.setC_pincode(dobj.getC_pincode());
        owner_dobj.setRegn_type(dobj.getRegnType());
        owner_dobj.setVh_class(Integer.parseInt(dobj.getVehClass()));
        owner_dobj.setChasi_no(dobj.getChasisNo());
        owner_dobj.setVch_catg(dobj.getVch_catg());
        return owner_dobj;
    }

    public static void saveCommonChargesForAll(EpayDobj ePayDobj, String regn_no, TransactionManager tmg, String rcpt, String paymentMode) throws VahanException {
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO vt_fee("
                    + " regn_no, payment_mode, fees, fine, rcpt_no, rcpt_dt, pur_cd, "
                    + " flag, collected_by, state_cd, off_cd)"
                    + " VALUES (?, ?, ?, ?, ?, current_timestamp, ?, "
                    + " ?, ?, ?, ?);";
            ps = tmg.prepareStatement(sql);
            ps.setString(1, regn_no);//regn_no
            ps.setString(2, paymentMode);//payment_mode
            ps.setLong(3, ePayDobj.getE_TaxFee());//fees
            ps.setLong(4, 0);//fine
            ps.setString(5, rcpt);//rcpt_no
            ps.setInt(6, ePayDobj.getPurCd());//   pur_cd
            ps.setNull(7, java.sql.Types.VARCHAR);//   flag
            ps.setString(8, Util.getEmpCode());//   collected_by
            ps.setString(9, Util.getUserStateCode());//   state_cd
            ps.setInt(10, Util.getUserOffCode());//   off_cd
            int success = ps.executeUpdate();
            if (success == 0) {
                throw new VahanException("Unable to Save Common Charges");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There is Some Problem in Saving Common Charges");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There is Some Problem in Saving Common Charges");
        }
    }

    public static RetroFittingDetailsDobj getCngMakerInfo(String regn_no) throws VahanException {
        PreparedStatement ps;
        TransactionManagerReadOnly tmgr = null;
        RowSet rs;
        RetroFittingDetailsDobj obj = null;
        String query = "Select * from getcngkitinfo(?)";
        try {
            tmgr = new TransactionManagerReadOnly("getCngMakerInfo");
            ps = tmgr.prepareStatement(query);
            ps.setString(1, regn_no);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                obj = new RetroFittingDetailsDobj();
                obj.setKit_srno(rs.getString("kit_serial_no"));
                obj.setKit_type(rs.getString("kit_type_cd"));
                obj.setKit_manuf(rs.getString("kit_manu"));
                obj.setInstall_dt(rs.getDate("fitted_date"));
                obj.setWorkshop(rs.getString("workshop"));
                obj.setWorkshop_lic_no(rs.getString("licence_no"));
                obj.setCyl_srno(rs.getString("cylinder_serial_no"));
                obj.setHydro_dt(rs.getDate("cyl_validity"));
                obj.setKit_pucc_norms(rs.getString("norms"));
                obj.setDisable(true);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in fetching CNG Details");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in fetching CNG Details");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return obj;
    }

    public static RetroFittingDetailsDobj getCngMakerInfoforAllState(String regn_no) throws VahanException {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        RowSet rs;
        RetroFittingDetailsDobj obj = null;
        String query = "Select * from getcngkitinfoAllState(?)";
        try {
            tmgr = new TransactionManager("getCngMakerInfo");
            ps = tmgr.prepareStatement(query);
            ps.setString(1, regn_no);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                obj = new RetroFittingDetailsDobj();
                obj.setKit_srno(rs.getString("kit_serial_no"));
                obj.setKit_type(rs.getString("kit_type_cd"));
                obj.setKit_manuf(rs.getString("kit_manu"));
                obj.setInstall_dt(rs.getDate("fitted_date"));
                obj.setWorkshop(rs.getString("workshop"));
                obj.setWorkshop_lic_no(rs.getString("licence_no"));
                obj.setCyl_srno(rs.getString("cylinder_serial_no"));
                obj.setHydro_dt(rs.getDate("cyl_validity"));
                obj.setKit_pucc_norms(rs.getString("norms"));
                obj.setDisable(true);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in fetching CNG Details");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in fetching CNG Details");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return obj;
    }

    public static boolean validateIpAddress(Long userCode, String ipAddress) {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        boolean status = false;
        try {
            tmgr = new TransactionManagerReadOnly("validateIpAddress");
            sql = "select login_ipaddress from " + TableList.TM_USER_INFO
                    + " WHERE user_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, userCode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                if (!CommonUtils.isNullOrBlank(rs.getString("login_ipaddress"))) {
                    status = ipAddress.equals(rs.getString("login_ipaddress"));
                } else {
                    status = true;
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return status;
    }

    /**
     * Returns Latest Receipt No against Application Number
     *
     * @param applNo
     * @param stateCd
     * @param offCd
     * @return
     */
    public static String getLatestRcptNo(String applNo, String stateCd, int offCd) {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String rcptNo = "";
        try {
            tmgr = new TransactionManager("getLatestRcptNo of Online  Cash Receipt");
            sql = " SELECT distinct a.rcpt_no, f.rcpt_dt \n"
                    + " from vt_fee f  \n"
                    + " left join vp_appl_rcpt_mapping a on f.rcpt_no = a.rcpt_no and f.state_cd = a.state_cd and f.off_cd = a.off_cd \n"
                    + " where a.appl_no = ? and a.state_cd = ? and a.off_cd = ? \n"
                    + " order by 2 desc ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                rcptNo = rs.getString("rcpt_no");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return rcptNo;
    }

    public boolean auditTrailDAO(DOAuditTrail auditTrail) throws VahanException, SQLException {
        boolean success = false;
        TransactionManager tmgr = null;
        String query = "INSERT INTO vahan4.VH_AUDIT_TRAIL( user_id, ipaddress, actiontype, status) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = null;
        int i = 0;
        try {
            tmgr = new TransactionManager("auditTrailDAO");
            pstmt = tmgr.prepareStatement(query);
            pstmt.setString(++i, auditTrail.getmUserNameId());
            pstmt.setString(++i, auditTrail.getmIpAddress());
            pstmt.setString(++i, auditTrail.getmActionType());
            pstmt.setString(++i, auditTrail.getmStatus());
            pstmt.executeUpdate();
            tmgr.commit();
            success = true;
        } finally {
            try {
                tmgr.release();
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return success;
    }

    public static Map<String, Integer> getFitOfficerList(String stateCd, int offCd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        Map<String, Integer> fitOfficerOffCd = new LinkedHashMap<>();

        try {
            tmgr = new TransactionManager("getFitOfficerList()");

            sql = "SELECT a.state_cd,a.off_cd,a.user_cd,b.user_name "
                    + " FROM  " + TableList.VM_FIT_OFFICER + " a left join " + TableList.TM_USER_INFO + " b "
                    + " ON     a.user_cd=b.user_cd  "
                    + " WHERE a.state_cd=? and a.off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);

            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next())//found
            {
                fitOfficerOffCd.put(rs.getString("user_name"), rs.getInt("user_cd")); //label,value
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong when fetching details of Fitness Officer(s), Please contact to the System Administrator.");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong when fetching details of Fitness Officer(s), Please contact to the System Administrator.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return fitOfficerOffCd;
    }

    public static String getRcptHeading() {
        String rcptHeading = "Not Available";
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        try {
            tmgr = new TransactionManagerReadOnly("getUserCategory");
            sql = "Select rcpt_heading from " + TableList.TM_CONFIGURATION + " where state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());

            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                rcptHeading = rs.getString("rcpt_heading");
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return rcptHeading;
    }

    public static String getRcptSubHeading() {
        String rcptSubHeading = "Not Available";
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        try {
            tmgr = new TransactionManagerReadOnly("getUserCategory");
            sql = "Select rcpt_subheading from " + TableList.TM_CONFIGURATION + " where state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());

            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                rcptSubHeading = rs.getString("rcpt_subheading");
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return rcptSubHeading;
    }

    public static long getPurposeFineAmount(String stateCd, int purCd, long feeAmount, Date dueDate, Date paymentDate, VehicleParameters parameters, boolean isNew) {

        long fineAmt = 0;
        long fineAmtTotal = 0;
        int monthDiff = 0;
        int dayDiff = 0;
        int actualdayFiff = 0;
        int gracePeriod = 0;
        int holidays = 0;
        Date dueDateAfterGraceDays = dueDate;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        RowSet rs = null;
        String sql = null;
        int perUnit = 0;
        try {
            int action_cd = Util.getSelectedSeat().getAction_cd();
            parameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(dueDate));
            parameters.setPAYMENT_DATE(DateUtils.parseDate(paymentDate));
            tmgr = new TransactionManagerReadOnly("getPurposeFineAmount");

//            sql = "SELECT no_of_holidays * CASE WHEN consider_holiday_fine THEN 1 ELSE 0 END as holiday "
//                    + " FROM " + TableList.THM_OFFICE_CONFIGURATION + " a, " + TableList.TM_CONFIGURATION + " b "
//                    + " WHERE a.state_cd = b.state_cd and b.state_cd = ? and a.off_cd=? "
//                    + " and a.moved_on between ?::date and (?::date + '1 day'::interval - '1 second'::interval) "
//                    + " order by a.moved_on limit 1";
            sql = "select no_of_holidays * CASE WHEN consider_holiday_fine THEN 1 ELSE 0 END as holiday\n"
                    + " FROM " + TableList.THM_OFFICE_CONFIGURATION + " a, " + TableList.TM_CONFIGURATION + " b,\n"
                    + " (SELECT a.state_cd ,a.off_cd ,max(a.moved_on) max ,min(a.moved_on) min ,case when max(a.moved_on) = min(a.moved_on) then true else false end as duecondition\n"
                    + " FROM " + TableList.THM_OFFICE_CONFIGURATION + " a WHERE a.state_cd = ? and a.off_cd=? \n"
                    + " and a.moved_on between ?::date and (?::date + '1 day'::interval - '1 second'::interval)  group by 1,2) c\n"
                    + " where a.state_cd = b.state_cd and a.state_cd = c.state_cd and a.off_cd = c.off_cd and a.moved_on=c.max and duecondition is true";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            ps.setDate(3, new java.sql.Date(paymentDate.getTime()));
            ps.setDate(4, new java.sql.Date(paymentDate.getTime()));
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                holidays = rs.getInt("holiday");
            }
            sql = "SELECT *,grace_days::numeric as grace_period "
                    + " FROM " + TableList.VM_FEE_MAST_PENALTY
                    + " WHERE state_cd = ? and pur_cd = ?"
                    + " and ((? between from_dt and upto_dt or ? between from_dt and upto_dt) OR (from_dt between ? and  ? OR upto_dt between ? and  ? ))"
                    + " order by from_dt";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, purCd);
            ps.setDate(3, new java.sql.Date(dueDate.getTime()));
            ps.setDate(4, new java.sql.Date(paymentDate.getTime()));
            ps.setDate(5, new java.sql.Date(dueDate.getTime()));
            ps.setDate(6, new java.sql.Date(paymentDate.getTime()));
            ps.setDate(7, new java.sql.Date(dueDate.getTime()));
            ps.setDate(8, new java.sql.Date(paymentDate.getTime()));
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                gracePeriod = 0;
                dueDateAfterGraceDays = dueDate;
                Date uptoDate = rs.getDate("upto_dt");
                Date fromDate = rs.getDate("from_dt");
                dayDiff = 0;
                actualdayFiff = 0;
                monthDiff = 0;
                int dayFrom = rs.getInt("day_from");
                int dayUpto = rs.getInt("day_upto");
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), parameters), "getPurposeFineAmount-2")) {
                    gracePeriod = holidays + rs.getInt("grace_period");
                    if (gracePeriod > 0) {
                        dueDateAfterGraceDays = dateRange(dueDate, 0, 0, gracePeriod);
                    }
                    //if payment_dt!=due_date && payment_dt > due_date
                    if ((DateUtils.compareDates(dueDateAfterGraceDays, paymentDate) != 0
                            && DateUtils.compareDates(paymentDate, dueDateAfterGraceDays) == 2) || (("PB".equalsIgnoreCase(Util.getUserStateCode())) && (action_cd == TableConstants.FITNESS_APPROVAL))) {

                        Date maxDate = null, minDate = null;
                        //calculation of maximum date from upto_date and payment_date
                        if (DateUtils.compareDates(dueDateAfterGraceDays, fromDate) == 0) {
                            maxDate = fromDate;
                        } else if (DateUtils.compareDates(dueDateAfterGraceDays, fromDate) == 1) {
                            maxDate = fromDate;
                        } else if (DateUtils.compareDates(dueDateAfterGraceDays, fromDate) == 2) {
                            maxDate = dueDateAfterGraceDays;
                        }
                        //calculation of minimum date from upto_date and payment_date
                        if (DateUtils.compareDates(uptoDate, paymentDate) == 0) {
                            minDate = uptoDate;
                        } else if (DateUtils.compareDates(uptoDate, paymentDate) == 1) {
                            minDate = uptoDate;
                        } else if (DateUtils.compareDates(uptoDate, paymentDate) == 2) {
                            minDate = paymentDate;
                        }

                        if (minDate != null && maxDate != null) {
                            dayDiff = (int) DateUtils.getDate1MinusDate2_Days(maxDate, minDate);
                            dayDiff++;
                            actualdayFiff = dayDiff;//for holding actual value of difference of the day
                            if ("OR,OD,KA,KL".contains(stateCd)) {
                                monthDiff = DateUtils.getDate1MinusDate2_Months(DateUtils.getStartOfMonthDate(DateUtils.parseDate(maxDate)), DateUtils.getLastOfMonthDate(DateUtils.parseDate(minDate)));
                            } else {
                                monthDiff = DateUtils.getDate1MinusDate2_Months(maxDate, minDate);
                            }
                        }

                        if ("PB".equalsIgnoreCase(Util.getUserStateCode())) {
                            int pb_days_grace_days_after_fit_check = (int) DateUtils.getDate1MinusDate2_Days(paymentDate, new Date());
                            if ((pb_days_grace_days_after_fit_check > 59) && (action_cd == TableConstants.FITNESS_APPROVAL)) {
                                dayDiff = (int) DateUtils.getDate1MinusDate2_Days(maxDate, new Date());
                                dayDiff++;
                            }
                        }
                        if (monthDiff > 0 || dayDiff > 0) {

                            if (rs.getString("penalty_mode").equalsIgnoreCase("D")) {
                                if (dayDiff >= dayUpto) {
                                    dayDiff = dayUpto;
                                }
                                if (dayFrom > 1 && dayDiff >= dayFrom) {
                                    dayDiff = dayDiff - dayFrom;
                                    dayDiff++;
                                }
                            }

                            if (rs.getString("penalty_mode").equalsIgnoreCase("M")) {
                                if (monthDiff > dayUpto) {
                                    monthDiff = dayUpto;
                                }
                                if (dayFrom > 1 && monthDiff > dayFrom) {
                                    monthDiff = monthDiff - dayFrom;
                                }
                            }

                            if (rs.getBoolean("is_percent_rate")) {
                                fineAmt = ((long) rs.getFloat("fine_amt") * (feeAmount / 100));
                            } else {
                                fineAmt = (long) rs.getFloat("fine_amt");
                            }
                            if (rs.getInt("per_unit") > 0) {
                                if (rs.getInt("grace_unit_days") > rs.getInt("per_unit")) {
                                    if (rs.getString("penalty_mode").equalsIgnoreCase("M")) {
                                        monthDiff = monthDiff - rs.getInt("grace_unit_days");
                                        if (monthDiff < 0) {
                                            monthDiff = 0;
                                        }
                                    } else if (rs.getString("penalty_mode").equalsIgnoreCase("D")) {
                                        dayDiff = dayDiff - rs.getInt("grace_unit_days");
                                        if (dayDiff < 0) {
                                            dayDiff = 0;
                                        }
                                    }
                                }
                                if (rs.getString("penalty_mode").equalsIgnoreCase("M") && monthDiff > 0 && monthDiff >= dayFrom && monthDiff <= dayUpto) {
                                    perUnit = (int) Math.ceil((monthDiff / Float.valueOf(rs.getInt("per_unit"))));
                                } else if (rs.getString("penalty_mode").equalsIgnoreCase("D") && dayDiff > 0 && actualdayFiff >= dayFrom && actualdayFiff <= dayUpto) {
                                    perUnit = (int) Math.ceil((dayDiff / Float.valueOf(rs.getInt("per_unit"))));
                                }
                                fineAmt = fineAmt * perUnit;
                            }
                            if (rs.getInt("max_amount") > 0 && fineAmt > rs.getInt("max_amount")) {
                                fineAmt = rs.getInt("max_amount");
                            }

                            if (rs.getString("penalty_mode").equalsIgnoreCase("M") && monthDiff > 0 && monthDiff >= dayFrom && monthDiff <= dayUpto) {
                                fineAmtTotal = fineAmtTotal + fineAmt;
                            } else if (rs.getString("penalty_mode").equalsIgnoreCase("D") && dayDiff > 0 && actualdayFiff >= dayFrom && actualdayFiff <= dayUpto) {
                                fineAmtTotal = fineAmtTotal + fineAmt;
                            } else if (rs.getString("penalty_mode").equalsIgnoreCase("F") && dayDiff >= dayFrom && dayDiff <= dayUpto) {//fixed rate
                                fineAmtTotal = fineAmtTotal + fineAmt;
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            String appl_no = null;
            if (Util.getSelectedSeat() != null) {
                appl_no = Util.getSelectedSeat().getAppl_no();//for finding error of due date
            }
            LOGGER.error(ex.toString() + "getPurposeFineAmount--[Appl No-" + appl_no + ",PurCd-" + purCd + "]" + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return fineAmtTotal;
    }

    /**
     * @author Kartikey Singh
     */
    public static long getPurposeFineAmount(String stateCd, int purCd, long feeAmount, Date dueDate, Date paymentDate, VehicleParameters parameters, boolean isNew,
            int actionCode, int offCode, String stateCode, String applNo) {

        long fineAmt = 0;
        long fineAmtTotal = 0;
        int monthDiff = 0;
        int dayDiff = 0;
        int actualdayFiff = 0;
        int gracePeriod = 0;
        int holidays = 0;
        Date dueDateAfterGraceDays = dueDate;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        RowSet rs = null;
        String sql = null;
        int perUnit = 0;
        try {
            int action_cd = actionCode;
            parameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(dueDate));
            parameters.setPAYMENT_DATE(DateUtils.parseDate(paymentDate));
            tmgr = new TransactionManagerReadOnly("getPurposeFineAmount");

//            sql = "SELECT no_of_holidays * CASE WHEN consider_holiday_fine THEN 1 ELSE 0 END as holiday "
//                    + " FROM " + TableList.THM_OFFICE_CONFIGURATION + " a, " + TableList.TM_CONFIGURATION + " b "
//                    + " WHERE a.state_cd = b.state_cd and b.state_cd = ? and a.off_cd=? "
//                    + " and a.moved_on between ?::date and (?::date + '1 day'::interval - '1 second'::interval) "
//                    + " order by a.moved_on limit 1";
            sql = "select no_of_holidays * CASE WHEN consider_holiday_fine THEN 1 ELSE 0 END as holiday\n"
                    + " FROM " + TableList.THM_OFFICE_CONFIGURATION + " a, " + TableList.TM_CONFIGURATION + " b,\n"
                    + " (SELECT a.state_cd ,a.off_cd ,max(a.moved_on) max ,min(a.moved_on) min ,case when max(a.moved_on) = min(a.moved_on) then true else false end as duecondition\n"
                    + " FROM " + TableList.THM_OFFICE_CONFIGURATION + " a WHERE a.state_cd = ? and a.off_cd=? \n"
                    + " and a.moved_on between ?::date and (?::date + '1 day'::interval - '1 second'::interval)  group by 1,2) c\n"
                    + " where a.state_cd = b.state_cd and a.state_cd = c.state_cd and a.off_cd = c.off_cd and a.moved_on=c.max and duecondition is true";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCode);
            ps.setDate(3, new java.sql.Date(paymentDate.getTime()));
            ps.setDate(4, new java.sql.Date(paymentDate.getTime()));
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                holidays = rs.getInt("holiday");
            }
            sql = "SELECT *,grace_days::numeric as grace_period "
                    + " FROM " + TableList.VM_FEE_MAST_PENALTY
                    + " WHERE state_cd = ? and pur_cd = ?"
                    + " and ((? between from_dt and upto_dt or ? between from_dt and upto_dt) OR (from_dt between ? and  ? OR upto_dt between ? and  ? ))"
                    + " order by from_dt";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, purCd);
            ps.setDate(3, new java.sql.Date(dueDate.getTime()));
            ps.setDate(4, new java.sql.Date(paymentDate.getTime()));
            ps.setDate(5, new java.sql.Date(dueDate.getTime()));
            ps.setDate(6, new java.sql.Date(paymentDate.getTime()));
            ps.setDate(7, new java.sql.Date(dueDate.getTime()));
            ps.setDate(8, new java.sql.Date(paymentDate.getTime()));
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                gracePeriod = 0;
                dueDateAfterGraceDays = dueDate;
                Date uptoDate = rs.getDate("upto_dt");
                Date fromDate = rs.getDate("from_dt");
                dayDiff = 0;
                actualdayFiff = 0;
                monthDiff = 0;
                int dayFrom = rs.getInt("day_from");
                int dayUpto = rs.getInt("day_upto");
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), parameters), "getPurposeFineAmount-2")) {
                    gracePeriod = holidays + rs.getInt("grace_period");
                    if (gracePeriod > 0) {
                        dueDateAfterGraceDays = dateRange(dueDate, 0, 0, gracePeriod);
                    }
                    //if payment_dt!=due_date && payment_dt > due_date
                    if ((DateUtils.compareDates(dueDateAfterGraceDays, paymentDate) != 0
                            && DateUtils.compareDates(paymentDate, dueDateAfterGraceDays) == 2) || (("PB".equalsIgnoreCase(stateCode)) && (action_cd == TableConstants.FITNESS_APPROVAL))) {

                        Date maxDate = null, minDate = null;
                        //calculation of maximum date from upto_date and payment_date
                        if (DateUtils.compareDates(dueDateAfterGraceDays, fromDate) == 0) {
                            maxDate = fromDate;
                        } else if (DateUtils.compareDates(dueDateAfterGraceDays, fromDate) == 1) {
                            maxDate = fromDate;
                        } else if (DateUtils.compareDates(dueDateAfterGraceDays, fromDate) == 2) {
                            maxDate = dueDateAfterGraceDays;
                        }
                        //calculation of minimum date from upto_date and payment_date
                        if (DateUtils.compareDates(uptoDate, paymentDate) == 0) {
                            minDate = uptoDate;
                        } else if (DateUtils.compareDates(uptoDate, paymentDate) == 1) {
                            minDate = uptoDate;
                        } else if (DateUtils.compareDates(uptoDate, paymentDate) == 2) {
                            minDate = paymentDate;
                        }

                        if (minDate != null && maxDate != null) {
                            dayDiff = (int) DateUtils.getDate1MinusDate2_Days(maxDate, minDate);
                            dayDiff++;
                            actualdayFiff = dayDiff;//for holding actual value of difference of the day
                            if ("OR,OD,KA,KL".contains(stateCd)) {
                                monthDiff = DateUtils.getDate1MinusDate2_Months(DateUtils.getStartOfMonthDate(DateUtils.parseDate(maxDate)), DateUtils.getLastOfMonthDate(DateUtils.parseDate(minDate)));
                            } else {
                                monthDiff = DateUtils.getDate1MinusDate2_Months(maxDate, minDate);
                            }
                        }

                        if ("PB".equalsIgnoreCase(stateCode)) {
                            int pb_days_grace_days_after_fit_check = (int) DateUtils.getDate1MinusDate2_Days(paymentDate, new Date());
                            if ((pb_days_grace_days_after_fit_check > 59) && (action_cd == TableConstants.FITNESS_APPROVAL)) {
                                dayDiff = (int) DateUtils.getDate1MinusDate2_Days(maxDate, new Date());
                                dayDiff++;
                            }
                        }
                        if (monthDiff > 0 || dayDiff > 0) {

                            if (rs.getString("penalty_mode").equalsIgnoreCase("D")) {
                                if (dayDiff >= dayUpto) {
                                    dayDiff = dayUpto;
                                }
                                if (dayFrom > 1 && dayDiff >= dayFrom) {
                                    dayDiff = dayDiff - dayFrom;
                                    dayDiff++;
                                }
                            }

                            if (rs.getString("penalty_mode").equalsIgnoreCase("M")) {
                                if (monthDiff > dayUpto) {
                                    monthDiff = dayUpto;
                                }
                                if (dayFrom > 1 && monthDiff > dayFrom) {
                                    monthDiff = monthDiff - dayFrom;
                                }
                            }

                            if (rs.getBoolean("is_percent_rate")) {
                                fineAmt = ((long) rs.getFloat("fine_amt") * (feeAmount / 100));
                            } else {
                                fineAmt = (long) rs.getFloat("fine_amt");
                            }
                            if (rs.getInt("per_unit") > 0) {
                                if (rs.getInt("grace_unit_days") > rs.getInt("per_unit")) {
                                    if (rs.getString("penalty_mode").equalsIgnoreCase("M")) {
                                        monthDiff = monthDiff - rs.getInt("grace_unit_days");
                                        if (monthDiff < 0) {
                                            monthDiff = 0;
                                        }
                                    } else if (rs.getString("penalty_mode").equalsIgnoreCase("D")) {
                                        dayDiff = dayDiff - rs.getInt("grace_unit_days");
                                        if (dayDiff < 0) {
                                            dayDiff = 0;
                                        }
                                    }
                                }
                                if (rs.getString("penalty_mode").equalsIgnoreCase("M") && monthDiff > 0 && monthDiff >= dayFrom && monthDiff <= dayUpto) {
                                    perUnit = (int) Math.ceil((monthDiff / Float.valueOf(rs.getInt("per_unit"))));
                                } else if (rs.getString("penalty_mode").equalsIgnoreCase("D") && dayDiff > 0 && actualdayFiff >= dayFrom && actualdayFiff <= dayUpto) {
                                    perUnit = (int) Math.ceil((dayDiff / Float.valueOf(rs.getInt("per_unit"))));
                                }
                                fineAmt = fineAmt * perUnit;
                            }
                            if (rs.getInt("max_amount") > 0 && fineAmt > rs.getInt("max_amount")) {
                                fineAmt = rs.getInt("max_amount");
                            }

                            if (rs.getString("penalty_mode").equalsIgnoreCase("M") && monthDiff > 0 && monthDiff >= dayFrom && monthDiff <= dayUpto) {
                                fineAmtTotal = fineAmtTotal + fineAmt;
                            } else if (rs.getString("penalty_mode").equalsIgnoreCase("D") && dayDiff > 0 && actualdayFiff >= dayFrom && actualdayFiff <= dayUpto) {
                                fineAmtTotal = fineAmtTotal + fineAmt;
                            } else if (rs.getString("penalty_mode").equalsIgnoreCase("F") && dayDiff >= dayFrom && dayDiff <= dayUpto) {//fixed rate
                                fineAmtTotal = fineAmtTotal + fineAmt;
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "getPurposeFineAmount--[Appl No-" + applNo + ",PurCd-" + purCd + "]" + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return fineAmtTotal;
    }

    public static String getLatestSmartCardSerialNo(String strRegnNo) {
        String regno = strRegnNo;
        if (strRegnNo.length() < 10) {
            int space = 10 - regno.length();
            String lastpart = regno.substring(regno.length() - 4);
            String firstPart = regno.substring(0, regno.length() - 4);
            regno = firstPart + String.format("%1$" + (lastpart.length() + space) + "s", lastpart);

        }
        String strQry = "SELECT * FROM " + TableList.RC_IA_TO_BE
                + " WHERE vehregno in (?, rpad(?, 10, ?),?) AND STATUS='0' ORDER BY RCISSUEDATE DESC ";
        String getLatestSmartCardSerialNo = null;
        TransactionManagerReadOnly tmgr = null;

        try {
            tmgr = new TransactionManagerReadOnly("getLatestSmartCardSerialNo");
            PreparedStatement ps = tmgr.prepareStatement(strQry);
            ps.setString(1, strRegnNo);
            ps.setString(2, strRegnNo);
            ps.setString(3, " ");
            ps.setString(4, regno);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                getLatestSmartCardSerialNo = rs.getString("RCCARDCHIPNO");
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return getLatestSmartCardSerialNo;
    }

    public static boolean isSmartCardFeeForRegisteredVehicle(String state_cd, int off_cd, String regn_no, String appl_no) {
        boolean smartcardFee = false;
        TransactionManager tmgr = null;
        try {

            String whereIam = "isSmartCardFeeForRegisteredVehicle";
            String feeSQL = "select pur_cd from " + TableList.VA_DETAILS + " where appl_no=?";
            tmgr = new TransactionManager(whereIam);

            boolean isSmartCard = verifyForSmartCard(state_cd, off_cd, tmgr);
            if (!isSmartCard) {
                return false;
            }

            PreparedStatement ps = tmgr.prepareStatement(feeSQL);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            boolean isSmartCardHyp = false;
            boolean isOtherpurCd = false;
            Integer[] allowed_pur_cd = new Integer[]{1, 3, 4, 5, 10, 11, 12, 15, 16, 17, 123, 331};
            List<Integer> lisAllowed = Arrays.asList(allowed_pur_cd);
            TmConfigurationDobj tmConf = Util.getTmConfiguration();
            while (rs.next()) {
                int pur_cd = rs.getInt("pur_cd");
                if (pur_cd == TableConstants.VM_TRANSACTION_MAST_ADD_HYPO
                        || pur_cd == TableConstants.VM_TRANSACTION_MAST_REM_HYPO) {
                    if (!tmConf.isSmart_card_hpa_hpt()) {
                        String latestSmart = ServerUtility.getLatestSmartCardSerialNo(regn_no);
                        if (latestSmart == null) {
                            isSmartCardHyp = true;
                        }
                    } else {
                        isSmartCardHyp = true;
                    }

                } else {
                    if (lisAllowed.contains(pur_cd)) {
                        isOtherpurCd = true;
                        break;
                    }

                }
            }

            if (isOtherpurCd) {
                smartcardFee = true;
            } else {
                if (isSmartCardHyp) {
                    smartcardFee = true;
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return smartcardFee;
    }

    public static boolean isSmartCardForUpdate(String state_cd, int off_cd, String regn_no, String appl_no, TmConfigurationDobj tmConf) {
        boolean isSmartCardForUpdate = false;

        TransactionManager tmgr = null;
        try {

            String whereIam = "isSmartCardUpdate";
            String feeSQL = "select pur_cd from " + TableList.VA_DETAILS + " where appl_no=? ";
            tmgr = new TransactionManager(whereIam);

            boolean isSmartCard = verifyForSmartCard(state_cd, off_cd, tmgr);
            if (!isSmartCard) {
                return false;
            }

            PreparedStatement ps = tmgr.prepareStatement(feeSQL);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();

            Integer[] allowed_pur_cd = new Integer[]{1, 3, 4, 5, 10, 11, 12, 15, 16, 17, 123, 331};
            List<Integer> lisAllowed = Arrays.asList(allowed_pur_cd);
            while (rs.next()) {
                int pur_cd = rs.getInt("pur_cd");
                if (!tmConf.isSmart_card_hpa_hpt() && (pur_cd == TableConstants.VM_TRANSACTION_MAST_ADD_HYPO
                        || pur_cd == TableConstants.VM_TRANSACTION_MAST_REM_HYPO)) {

                    String latestSmart = ServerUtility.getLatestSmartCardSerialNo(regn_no);
                    if (latestSmart != null) {
                        isSmartCardForUpdate = true;
                    }

                } else {
                    if (lisAllowed.contains(pur_cd)) {
                        isSmartCardForUpdate = false;
                        break;
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return isSmartCardForUpdate;
    }

    public static void updateForSmartCard(String appl_no, String regn_no,
            String state_cd, int off_cd, TransactionManager tmgr) throws Exception {

        String latestSmart = ServerUtility.getLatestSmartCardSerialNo(regn_no);
        String sql = "select pur_cd from va_status where appl_no=? and pur_cd in ( "
                + TableConstants.VM_TRANSACTION_MAST_ADD_HYPO + "," + TableConstants.VM_TRANSACTION_MAST_REM_HYPO + ")";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, appl_no);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        String purCd = "";
        String append = ",";
        while (rs.next()) {
            if (purCd.length() > 0) {
                purCd = purCd + ",";
            }
            purCd = purCd + rs.getInt("pur_cd");
        }

        sql = "select rpad(left(trim(COALESCE(fncr_name, '')), 35), 35, ' ') as finname,  "
                + "     rpad(left(trim(COALESCE(fncr_add1, '') || COALESCE(fncr_add2, '') ||  COALESCE(fncr_add3, '') || "
                + " COALESCE(dist.descr, '') || COALESCE(fncr_state, '') || COALESCE(fncr_pincode::varchar, '')), 30), 30, ' ') "
                + " as finaddress,"
                + " case when from_dt is not null then to_char(from_dt, 'ddmmyyyy') else '00000000' end as hypofrom,"
                + "  '00000000'::varchar as hypoto from " + TableList.VT_HYPTH + "," + TableList.TM_DISTRICT
                + " dist where regn_no=? and dist.dist_cd= VT_HYPTH.fncr_district and dist.state_cd=VT_HYPTH.state_cd and VT_HYPTH.off_cd=? and dist.state_cd=? order by op_dt desc limit 1";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, regn_no);
        ps.setInt(2, Util.getSelectedSeat().getOff_cd());
        ps.setString(3, state_cd);
        rs = tmgr.fetchDetachedRowSetWithoutTrim_No_release();
        String data_for_updation = "                                   ;                              ;00000000;00000000";
        if (rs.next()) {
            data_for_updation = rs.getString("finname") + ";" + rs.getString("finaddress") + ";" + rs.getString("hypofrom") + ";" + rs.getString("hypoto");
        }

        sql = "INSERT INTO smartcard.smart_card_update(rcpt_no, regn_no, pur_cd, data_for_updation, deal_cd, op_dt, \n"
                + "            status, rccardchipno)"
                + "    VALUES (?, ?, ?, ?, ?, current_timestamp, ?, ?)";

        ps = tmgr.prepareStatement(sql);

        int i = 1;

        ps.setString(i++, appl_no);
        ps.setString(i++, regn_no);
        ps.setString(i++, purCd);
        ps.setString(i++, data_for_updation);
        ps.setString(i++, Util.getEmpCode());
        ps.setString(i++, "0");
        ps.setString(i++, latestSmart);
        ServerUtility.validateQueryResult(tmgr, ps.executeUpdate(), ps);

    }

    /*
     * @author Kartikey Singh
     */
    public static void updateForSmartCard(String appl_no, String regn_no,
            String state_cd, int off_cd, TransactionManager tmgr, String empCode) throws Exception {

        String latestSmart = ServerUtility.getLatestSmartCardSerialNo(regn_no);
        String sql = "select pur_cd from va_status where appl_no=? and pur_cd in ( "
                + TableConstants.VM_TRANSACTION_MAST_ADD_HYPO + "," + TableConstants.VM_TRANSACTION_MAST_REM_HYPO + ")";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, appl_no);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        String purCd = "";
        String append = ",";
        while (rs.next()) {
            if (purCd.length() > 0) {
                purCd = purCd + ",";
            }
            purCd = purCd + rs.getInt("pur_cd");
        }

        sql = "select rpad(left(trim(COALESCE(fncr_name, '')), 35), 35, ' ') as finname,  "
                + "     rpad(left(trim(COALESCE(fncr_add1, '') || COALESCE(fncr_add2, '') ||  COALESCE(fncr_add3, '') || "
                + " COALESCE(dist.descr, '') || COALESCE(fncr_state, '') || COALESCE(fncr_pincode::varchar, '')), 30), 30, ' ') "
                + " as finaddress,"
                + " case when from_dt is not null then to_char(from_dt, 'ddmmyyyy') else '00000000' end as hypofrom,"
                + "  '00000000'::varchar as hypoto from " + TableList.VT_HYPTH + "," + TableList.TM_DISTRICT
                + " dist where regn_no=? and dist.dist_cd= VT_HYPTH.fncr_district and dist.state_cd=VT_HYPTH.state_cd and VT_HYPTH.off_cd=? and dist.state_cd=? order by op_dt desc limit 1";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, regn_no);
        ps.setInt(2, off_cd);
        ps.setString(3, state_cd);
        rs = tmgr.fetchDetachedRowSetWithoutTrim_No_release();
        String data_for_updation = "                                   ;                              ;00000000;00000000";
        if (rs.next()) {
            data_for_updation = rs.getString("finname") + ";" + rs.getString("finaddress") + ";" + rs.getString("hypofrom") + ";" + rs.getString("hypoto");
        }

        sql = "INSERT INTO smartcard.smart_card_update(rcpt_no, regn_no, pur_cd, data_for_updation, deal_cd, op_dt, \n"
                + "            status, rccardchipno)"
                + "    VALUES (?, ?, ?, ?, ?, current_timestamp, ?, ?)";

        ps = tmgr.prepareStatement(sql);

        int i = 1;

        ps.setString(i++, appl_no);
        ps.setString(i++, regn_no);
        ps.setString(i++, purCd);
        ps.setString(i++, data_for_updation);
        ps.setString(i++, empCode);
        ps.setString(i++, "0");
        ps.setString(i++, latestSmart);
        ServerUtility.validateQueryResult(tmgr, ps.executeUpdate(), ps);

    }

    public static Date getRegVehDueDate(int purCd, String applNo, Owner_dobj dobj) {
        Date date = new Date();
        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        RowSet rs = null;

        try {
            tmgr = new TransactionManagerReadOnly("getRegVehDueDate");
            switch (purCd) {
                case TableConstants.VM_TRANSACTION_MAST_REN_REG:
                    if (dobj.getRegn_upto() != null) {
                        date = dobj.getRegn_upto();
                        date = ServerUtility.dateRange(date, 0, 0, 1);
                    }
                    break;
                case TableConstants.VM_TRANSACTION_MAST_CHG_ADD:
                    sql = "SELECT from_dt FROM " + TableList.VA_CA + " WHERE appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, applNo);
                    rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        if (rs.getDate("from_dt") != null) {
                            date = rs.getDate("from_dt");
                        }
                    }
                    break;
                case TableConstants.VM_TRANSACTION_MAST_TO:
                    sql = "SELECT sale_dt, transfer_dt FROM " + TableList.VA_TO + " WHERE appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, applNo);
                    rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        if (rs.getDate("sale_dt") != null) {
                            date = rs.getDate("sale_dt");
                        }
                        if (dobj != null && dobj.getBlackListedVehicleDobj() != null
                                && (dobj.getBlackListedVehicleDobj().getComplain_type() == TableConstants.BLTheftCode
                                || dobj.getBlackListedVehicleDobj().getComplain_type() == TableConstants.BLDestroyedAccidentCode)
                                && rs.getDate("transfer_dt") != null) {
                            date = rs.getDate("transfer_dt");
                        }
                    }
                    break;
                case TableConstants.VM_TRANSACTION_MAST_VEH_ALTER:
                    sql = "SELECT fitment_dt FROM " + TableList.VA_RETROFITTING_DTLS + " WHERE appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, applNo);
                    rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        if (rs.getDate("fitment_dt") != null) {
                            date = rs.getDate("fitment_dt");
                        }
                    }
                    break;
                case TableConstants.VM_TRANSACTION_MAST_FIT_CERT:
                    if (dobj != null && dobj.getFit_upto() != null) {
                        date = dobj.getFit_upto();
                        ExemptionImpl exemptionImpl = new ExemptionImpl();//for fitness exemption
                        ExemptionDobj exemptionDobj = exemptionImpl.getExemptionDetailsBasedOnPurpose(dobj.getRegn_no(), TableConstants.VM_FIT_EXEMPTION_PUR_CD);
                        if (exemptionDobj != null && exemptionDobj.getExem_to_date() != null && date != null && DateUtils.compareDates(date, exemptionDobj.getExem_to_date()) <= 1) {
                            date = exemptionDobj.getExem_to_date();//max date if found in exemption table for fitness
                        }

                        date = ServerUtility.dateRange(date, 0, 0, 1);
                        TmConfigurationDobj tmConfig = Util.getTmConfiguration();

                        if (tmConfig != null && tmConfig.isFit_fine_due_nid()) {
                            sql = "SELECT fit_nid,fit_valid_to FROM " + TableList.VT_FITNESS + " WHERE regn_no=? order by op_dt desc limit 1";
                            ps = tmgr.prepareStatement(sql);
                            ps.setString(1, dobj.getRegn_no());
                            rs = tmgr.fetchDetachedRowSet();
                            if (rs.next()) {
                                if (rs.getDate("fit_valid_to") != null) {
                                    date = ServerUtility.dateRange(rs.getDate("fit_valid_to"), 0, 0, tmConfig.getNid_days());
                                    date = ServerUtility.dateRange(date, 0, 0, 1);
                                }
                                if (rs.getDate("fit_nid") != null) {
                                    if (DateUtils.compareDates(rs.getDate("fit_nid"), date) == 2) {
                                        date = rs.getDate("fit_nid");
                                    }
                                }
                            } else {
                                date = ServerUtility.dateRange(date, 0, 0, tmConfig.getNid_days());
                                date = ServerUtility.dateRange(date, 0, 0, 1);
                            }
                        }
                    }
                    break;
                case TableConstants.VM_VEHICLE_INSPECTION_FEE:
                    FitnessImpl fitImpl = new FitnessImpl();
                    if (dobj != null && dobj.getRegn_no() != null) {
                        FitnessDobj fitDobj = fitImpl.set_Fitness_appl_db_to_dobj(dobj.getRegn_no(), null);
                        if (fitDobj != null && fitDobj.getOp_dt() != null && (fitDobj.getFit_result().equalsIgnoreCase(TableConstants.FitnessResultFail) || fitDobj.getFit_result().equalsIgnoreCase("F"))) {
                            date = ServerUtility.dateRange(fitDobj.getOp_dt(), 0, 0, 1);
                        }
                    }
                    break;
                case TableConstants.VM_TRANSACTION_MAST_NOC_CANCEL:
                    NocImpl nocImpl = new NocImpl();
                    NocDobj nocDobj = nocImpl.set_NOC_appl_db_to_dobj(null, dobj.getRegn_no());
                    date = nocDobj.getNoc_dt();
                    break;
                case TableConstants.VM_TRANSACTION_MAST_GREEN_TAX:
                    if (dobj.getState_cd().equalsIgnoreCase("TN")) {
                        if (dobj.getRegn_upto() != null) {
                            date = dobj.getRegn_upto();
                            date = ServerUtility.dateRange(date, 0, 0, 1);
                        }
                    }
                    break;
                default:
                    date = new Date();
            }
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        if (date == null) {
            date = new Date();
        }
        return date;
    }

    public static Date getRegVehPaymentDateForRecheckFee(int purCd, String applNo, Owner_dobj dobj) throws VahanException {
        Date date = new Date();
        String sql = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        RowSet rs = null;

        try {
            tmgr = new TransactionManager("getRegVehPaymentDateForRecheckFee");
            switch (purCd) {

                case TableConstants.VM_TRANSACTION_MAST_FIT_CERT:
                    sql = "SELECT fit_chk_dt FROM " + TableList.VA_FITNESS + " WHERE appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, applNo);
                    rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        if (rs.getDate("fit_chk_dt") != null) {
                            date = rs.getDate("fit_chk_dt");
                        }
                    }
                    break;

                default:
                    date = new Date();
            }
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            throw new VahanException("Error in Getting Payment Date for Rechecking of Fee/Tax");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Getting Payment Date for Rechecking of Fee/Tax");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

        return date;
    }

    public static int getAddToCartStatusCount() throws VahanException {
        String empCd = null;
        int offCd = 0;

        PreparedStatement psAddtoCart = null;
        TransactionManager tmgr = null;
        String addTocartSql = "select  count(distinct a.appl_no) as total \n"
                + " from vp_rcpt_cart a inner join va_details b on b.appl_no = a.appl_no \n"
                + " where a.user_cd = ? and b.off_cd = ? and COALESCE(a.transaction_no, 'New Cart') = 'New Cart'";
        int cartCount = 0;
        try {
            if (Util.getEmpCode() != null && Util.getSelectedSeat() != null) {
                empCd = Util.getEmpCode();
                offCd = Util.getSelectedSeat().getOff_cd();
            } else {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
            tmgr = new TransactionManager("CountForAddToCart");
            psAddtoCart = tmgr.prepareStatement(addTocartSql);
            psAddtoCart.setLong(1, Long.parseLong(empCd));
            psAddtoCart.setInt(2, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                cartCount = rs.getInt("total");
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
        return cartCount;
    }

    /**
     * @author Kartikey Singh
     */
    public static int getAddToCartStatusCount(String empCode, int offCode) throws VahanException {
        String empCd = null;
        int offCd = 0;

        PreparedStatement psAddtoCart = null;
        TransactionManager tmgr = null;
        String addTocartSql = "select  count(distinct a.appl_no) as total \n"
                + " from vp_rcpt_cart a inner join va_details b on b.appl_no = a.appl_no \n"
                + " where a.user_cd = ? and b.off_cd = ? and COALESCE(a.transaction_no, 'New Cart') = 'New Cart'";
        int cartCount = 0;
        try {
            if (empCode != null && offCode > 0) {
                empCd = empCode;
                offCd = offCode;
            } else {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
            tmgr = new TransactionManager("CountForAddToCart");
            psAddtoCart = tmgr.prepareStatement(addTocartSql);
            psAddtoCart.setLong(1, Long.parseLong(empCd));
            psAddtoCart.setInt(2, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                cartCount = rs.getInt("total");
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
        return cartCount;
    }

    public static void updateLoginStatus(String userId, String status, String ipAddress) {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        try {
            tmgr = new TransactionManager("updateLoginStatus");
            sql = "update " + TableList.TM_USER_INFO
                    + " SET status = ?,login_ipaddress=? "
                    + " WHERE user_id=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, status.toUpperCase());
            ps.setString(2, ipAddress);
            ps.setString(3, userId);
            ps.executeUpdate();
            tmgr.commit();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
    }

    public static boolean getExShoowroomPriceDisableStatus(String state_cd) {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        boolean exShowroomPriceStatus = true;

        try {
            tmgr = new TransactionManager("getExShoowroomPriceDisableStatus");
            sql = "SELECT ex_showroom_price_homologation from tm_configuration where state_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);

            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                exShowroomPriceStatus = rs.getBoolean("ex_showroom_price_homologation");
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return exShowroomPriceStatus;
    }

    public static String getAlertMessages(String flag) throws Exception {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String message = "";
        String sql = "select message from " + TableList.VT_ALERT_MESSAGES + " where flag = ? and current_timestamp between start_time and end_time ";
        try {
            tmgr = new TransactionManager("alertListener");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, flag);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                if (rs.getString("message") != null) {
                    message = rs.getString("message");
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return message;
    }

    public static int getPreviousActionCode(int current_action_cd, int pur_cd, String appl_no, VehicleParameters param) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        String updateSql = null;
        int prevActionLabelValue = 0;

        try {
            tmgr = new TransactionManager("getPreviousActionCode");

            updateSql = "update " + TableList.VA_STATUS + " set "
                    + "office_remark=office_remark "
                    + "where appl_no=? and pur_cd=? and action_cd =?  ";
            ps = tmgr.prepareStatement(updateSql);
            ps.setString(1, appl_no);
            ps.setInt(2, pur_cd);
            ps.setInt(3, Util.getSelectedSeat().getAction_cd());
            int fileMoved = ps.executeUpdate();
            if (fileMoved == 0) {
                //LOGGER.info("File has already Moved for Appl No-" + appl_no);
                throw new VahanException("File has already Moved for Appl No-" + appl_no);
            }

            /**
             * for cases if Parameter is null just select previous action code
             */
            if (param == null) {
                sql = " select action_cd from tm_purpose_action_flow b where b.flow_srno =  (select a.flow_slno \n"
                        + " from " + TableList.VA_STATUS + " a \n"
                        + " where appl_no=? and pur_cd=? and action_cd =? ) - 1 and state_cd=? and pur_cd=? ;";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, appl_no);
                ps.setInt(2, pur_cd);
                ps.setInt(3, Util.getSelectedSeat().getAction_cd());
                ps.setString(4, Util.getUserStateCode());
                ps.setInt(5, pur_cd);

                RowSet rs = tmgr.fetchDetachedRowSet();

                if (rs.next()) {
                    prevActionLabelValue = rs.getInt("action_cd");
                } else {
                    throw new VahanException("No Previous Action Found !!!");
                }
            } else {
                RowSet rs = null;
                int cntr = 1;
                while (true) {

                    sql = "select a.flow_srno, a.action_cd,a.condition_formula "
                            + " from tm_purpose_action_flow a, va_status b "
                            + " where a.state_cd = b.state_cd and a.pur_cd = b.pur_cd and a.state_cd = ? "
                            + " and b.appl_no = ? and a.flow_srno = b.flow_slno - ? and a.pur_cd =?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, Util.getUserStateCode());
                    ps.setString(2, Util.getSelectedSeat().getAppl_no());
                    ps.setInt(3, cntr);
                    ps.setInt(4, pur_cd);

                    rs = tmgr.fetchDetachedRowSet();

                    if (rs.next()) {
                        if (isCondition(replaceTagValues(rs.getString("condition_formula"), param), "getPreviousActionCode")) {
                            prevActionLabelValue = rs.getInt("action_cd");
                            break;
                        } else {
                            cntr++;
                        }
                    } else {
                        break;
                    }

                }

            }

        } catch (VahanException ve) {
            throw ve;
        } catch (SQLException sqle) {
            LOGGER.error(sqle);
            throw new VahanException(sqle.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                throw new VahanException(e.getMessage());
            }
        }

        return prevActionLabelValue;
    }

    /*
     * author: Ramesh 
     * Changed HttpSession references their variable counterparts
     * by passing state_cd from SessionVariablesModel
     */
    public static int getPreviousActionCode(int current_action_cd, int pur_cd, String appl_no, VehicleParameters param, String state_cd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        String updateSql = null;
        int prevActionLabelValue = 0;

        try {
            tmgr = new TransactionManager("getPreviousActionCode");

            updateSql = "update " + TableList.VA_STATUS + " set "
                    + "office_remark=office_remark "
                    + "where appl_no=? and pur_cd=? and action_cd =?  ";
            ps = tmgr.prepareStatement(updateSql);
            ps.setString(1, appl_no);
            ps.setInt(2, pur_cd);
            ps.setInt(3, current_action_cd);
            int fileMoved = ps.executeUpdate();
            if (fileMoved == 0) {
                //LOGGER.info("File has already Moved for Appl No-" + appl_no);
                throw new VahanException("File has already Moved for Appl No-" + appl_no);
            }

            /**
             * for cases if Parameter is null just select previous action code
             */
            if (param == null) {
                sql = " select action_cd from tm_purpose_action_flow b where b.flow_srno =  (select a.flow_slno \n"
                        + " from " + TableList.VA_STATUS + " a \n"
                        + " where appl_no=? and pur_cd=? and action_cd =? ) - 1 and state_cd=? and pur_cd=? ;";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, appl_no);
                ps.setInt(2, pur_cd);
                ps.setInt(3, current_action_cd);
                ps.setString(4, state_cd);
                ps.setInt(5, pur_cd);

                RowSet rs = tmgr.fetchDetachedRowSet();

                if (rs.next()) {
                    prevActionLabelValue = rs.getInt("action_cd");
                } else {
                    throw new VahanException("No Previous Action Found !!!");
                }
            } else {
                RowSet rs = null;
                int cntr = 1;
                while (true) {

                    sql = "select a.flow_srno, a.action_cd,a.condition_formula "
                            + " from tm_purpose_action_flow a, va_status b "
                            + " where a.state_cd = b.state_cd and a.pur_cd = b.pur_cd and a.state_cd = ? "
                            + " and b.appl_no = ? and a.flow_srno = b.flow_slno - ? and a.pur_cd =?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, state_cd);
                    ps.setString(2, appl_no);
                    ps.setInt(3, cntr);
                    ps.setInt(4, pur_cd);

                    rs = tmgr.fetchDetachedRowSet();

                    if (rs.next()) {
                        if (isCondition(replaceTagValues(rs.getString("condition_formula"), param), "getPreviousActionCode")) {
                            prevActionLabelValue = rs.getInt("action_cd");
                            break;
                        } else {
                            cntr++;
                        }
                    } else {
                        break;
                    }
                }
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (SQLException sqle) {
            LOGGER.error(sqle);
            throw new VahanException(sqle.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                throw new VahanException(e.getMessage());
            }
        }

        return prevActionLabelValue;
    }

    public static List getReasonsForHolding(String applNo) {
        List reasonList = new ArrayList();
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        String sql;
        try {
            tmgr = new TransactionManager("getReasonsForHolding");
            sql = "select string_agg(b.descr, ', ') AS reasons_list\n"
                    + " from  va_status a\n"
                    + " inner join vm_reasons b ON b.code = ANY(string_to_array(trim(a.public_remark),',')::integer[]) \n"
                    + " WHERE a.appl_no =?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                reasonList = makeList(rs.getString("reasons_list"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return reasonList;
    }

    public static void sendSMS(String mobileNo, String msgMobile) {
        if (msgMobile.length() > 0) {
            msgMobile = msgMobile + "%0D%0A-Courtesy:MoRTH&NIC";
        }
        String stateCd = Util.getUserStateCode();
        SmsSenderThread sMSAppV2 = new SmsSenderThread(mobileNo.trim(), msgMobile, stateCd);
        Thread thread = new Thread(sMSAppV2);
        thread.start();
    }

    /*
     * @author Kartikey Singh
     */
    public static void sendSMS(String mobileNo, String msgMobile, String stateCode) {
        if (msgMobile.length() > 0) {
            msgMobile = msgMobile + "%0D%0A-Courtesy:MoRTH&NIC";
        }
        SmsSenderThread sMSAppV2 = new SmsSenderThread(mobileNo.trim(), msgMobile, stateCode);
        Thread thread = new Thread(sMSAppV2);
        thread.start();
    }

    public static String getNextMonth(String regnDate) {
        try {
            DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Calendar c = java.util.Calendar.getInstance();
            c.setTime(sdf.parse(regnDate));
            c.add(java.util.Calendar.MONTH, 1);  // number of days to add
            c.add(java.util.Calendar.DATE, -1);
            regnDate = format.format(c.getTime());
//            regnDate = format.format(regnDate);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return regnDate;
    }

    public static boolean isTaxPaidOrCleared(String regNo, TransactionManager tmgr, TmConfigurationDobj config, Owner_dobj dobj, int purCd) throws VahanException {
        boolean isTaxPaid = false;
        String taxMode = null;
        Date taxPaidUpto = null;
        try {

            String sql = "Select tax_upto,tax_mode from " + TableList.VT_TAX
                    + " where regn_no=? and state_cd=? and pur_cd=? and tax_mode != 'B' order by rcpt_dt desc limit 1 ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regNo);
            ps.setString(2, dobj.getState_cd());
            ps.setInt(3, purCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                taxMode = rs.getString("tax_mode");
                if (!"OLS".contains(taxMode)) {
                    taxPaidUpto = rs.getDate("tax_upto");
                } else {
                    return isTaxPaid = true;
                }
            }

            Date taxClearFrom = null;
            Date taxClearTo = null;
            sql = "Select clear_fr,clear_to from " + TableList.VT_TAX_CLEAR + " where regn_no=? and state_cd=? and pur_cd=? order by op_dt desc limit 1 ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regNo);
            ps.setString(2, dobj.getState_cd());
            ps.setInt(3, purCd);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                taxClearFrom = rs.getDate("clear_fr");
                taxClearTo = rs.getDate("clear_to");
            }

            if (taxPaidUpto != null && DateUtils.compareDates(DateUtils.getCurrentLocalDate(), taxPaidUpto) <= 1) {
                return isTaxPaid = true;
            } else if ((taxClearFrom != null && taxClearTo == null) || (taxClearTo != null && DateUtils.compareDates(DateUtils.getCurrentLocalDate(), taxClearTo) <= 1)) {
                return isTaxPaid = true;
            } else {//for checking if Tax is Exepmted or not

                TaxExemptionImpl taxExemptionImpl = new TaxExemptionImpl();
                isTaxPaid = taxExemptionImpl.isTaxExempted(regNo);

                if (!isTaxPaid && dobj.getPmt_type() <= 0) {
                    PassengerPermitDetailDobj pmt_dobj = null;
                    TaxEndorsementImpl endorsmentTaxEntryImpl = new TaxEndorsementImpl();
                    pmt_dobj = endorsmentTaxEntryImpl.getPermitDobjFromVhaPermitNew(dobj);
                    if (pmt_dobj != null) {
                        if (!CommonUtils.isNullOrBlank(pmt_dobj.getPmt_type_code())) {
                            dobj.setPmt_type(Integer.parseInt(pmt_dobj.getPmt_type_code()));
                        }
                        if (!CommonUtils.isNullOrBlank(pmt_dobj.getPmtCatg())) {
                            dobj.setPmt_catg(Integer.parseInt(pmt_dobj.getPmtCatg()));
                        }
                    }
                }

                if (!isTaxPaid && dobj != null) {
                    VehicleParameters vehParameters = fillVehicleParametersFromDobj(dobj);
                    isTaxPaid = isCondition(replaceTagValues(config.getTax_exemption(), vehParameters), "isTaxPaidOrCleared");
                }

                BlackListedVehicleImpl obj = new BlackListedVehicleImpl();
                BlackListedVehicleDobj blackListedDobj = obj.getBlacklistedVehicleDetails(dobj.getRegn_no(), dobj.getChasi_no());

                if (!isTaxPaid && dobj != null && blackListedDobj != null
                        && blackListedDobj.getComplain_type() == TableConstants.BLTheftCode
                        && blackListedDobj.getFirDate() != null) {
                    if (taxPaidUpto != null && DateUtils.compareDates(blackListedDobj.getFirDate(), taxPaidUpto) <= 1) {
                        isTaxPaid = true;
                    } else if (taxClearTo != null && DateUtils.compareDates(blackListedDobj.getFirDate(), taxClearTo) <= 1) {
                        isTaxPaid = true;
                    }
                }
                return isTaxPaid;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Fetching Details of Tax Paid or Clear, Please Contact to the System Administrator.");
        }
    }

    public static String getTaxStatus(String regNo, TransactionManager tmgr, TmConfigurationDobj config, Owner_dobj dobj, int purCd) throws VahanException {
        String taxStatus = "";
        String taxMode = null;
        boolean dateExist = false;
        Date taxPaidUpto = null;
        try {

            String sql = "Select to_char(tax_upto,'dd-mm-yyyy') as taxPaidUpto ,tax_upto,tax_mode from " + TableList.VT_TAX
                    + " where regn_no=? and state_cd=? and pur_cd=? and tax_mode != 'B' order by rcpt_dt desc limit 1 ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regNo);
            ps.setString(2, dobj.getState_cd());
            ps.setInt(3, purCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                taxMode = rs.getString("tax_mode");
                if (!"OLS".contains(taxMode)) {
                    taxStatus = rs.getString("taxPaidUpto");
                    taxPaidUpto = rs.getDate("tax_upto");
                    dateExist = true;
                } else {
                    return taxStatus = "OLS";// return tax mode
                }
            }
            Date clearTo = TaxServerImplementation.getTaxDueFromDate(dobj, purCd);
            taxStatus = DateUtils.parseDate(clearTo);
//            Date taxClearFrom = null;
//            Date taxClearTo = null;
//            sql = "Select to_char(clear_to,'dd-mm-yyyy') as taxuptodate from " + TableList.VT_TAX_CLEAR + " where regn_no=? and state_cd=? and pur_cd=? order by op_dt desc limit 1 ";
//            ps = tmgr.prepareStatement(sql);
//            ps.setString(1, regNo);
//            ps.setString(2, dobj.getState_cd());
//            ps.setInt(3, purCd);
//            rs = tmgr.fetchDetachedRowSet_No_release();
//            if (rs.next()) {
//                taxStatus = rs.getString("taxuptodate");
//                dateExist = true;
//            }

            if (!dateExist) {//for checking if Tax is Exepmted or not

                TaxExemptionImpl taxExemptionImpl = new TaxExemptionImpl();
                if (taxExemptionImpl.isTaxExempted(regNo)) {
                    taxStatus = "E";
                    dateExist = true;
                } else if (dobj.getPmt_type() <= 0) {
                    PassengerPermitDetailDobj pmt_dobj = null;
                    TaxEndorsementImpl endorsmentTaxEntryImpl = new TaxEndorsementImpl();
                    pmt_dobj = endorsmentTaxEntryImpl.getPermitDobjFromVhaPermitNew(dobj);
                    if (pmt_dobj != null) {
                        if (!CommonUtils.isNullOrBlank(pmt_dobj.getPmt_type_code())) {
                            dobj.setPmt_type(Integer.parseInt(pmt_dobj.getPmt_type_code()));
                        }
                        if (!CommonUtils.isNullOrBlank(pmt_dobj.getPmtCatg())) {
                            dobj.setPmt_catg(Integer.parseInt(pmt_dobj.getPmtCatg()));
                        }
                    }
                    VehicleParameters vehParameters = fillVehicleParametersFromDobj(dobj);
                    if (isCondition(replaceTagValues(config.getTax_exemption(), vehParameters), "isTaxPaidOrCleared")) {
                        taxStatus = "E";
                        dateExist = true;
                    }
                }

                BlackListedVehicleImpl obj = new BlackListedVehicleImpl();
                BlackListedVehicleDobj blackListedDobj = obj.getBlacklistedVehicleDetails(dobj.getRegn_no(), dobj.getChasi_no());

                if (!dateExist && dobj != null && blackListedDobj != null
                        && blackListedDobj.getComplain_type() == TableConstants.BLTheftCode
                        && blackListedDobj.getFirDate() != null) {
                    if (taxPaidUpto != null && DateUtils.compareDates(blackListedDobj.getFirDate(), taxPaidUpto) <= 1) {
                        taxStatus = "P";
                    } else if (clearTo != null && DateUtils.compareDates(blackListedDobj.getFirDate(), clearTo) <= 1) {
                        taxStatus = "P";
                    }
                }

            }
            if ("DL,UP".contains(dobj.getState_cd()) && dobj.getVehType() == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
                taxStatus = "L";
            }

            if (dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)
                    && dobj.getState_cd().equalsIgnoreCase("DL")) {
                ApplicationInwardImpl applicationInwardImpl = new ApplicationInwardImpl();
                if (applicationInwardImpl.taxPaidOrClearedStatusOnVehType(rs.getString("vehregno").trim())) {
                    taxStatus = "R";
                }
            }
//            if (!dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)
//                    && dobj.getState_cd().equalsIgnoreCase("DL")) {
//
//                taxPaidOrClear = applicationInwardImpl.taxPaidOrClearedStatusOnVehType(rs.getString("vehregno").trim());
//            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Fetching Details of Tax Paid or Clear, Please Contact to the System Administrator.");
        }
        return taxStatus;
    }

    /*
        @author Kartikey Singh
     */
    public static String getTaxStatus(String regNo, TransactionManager tmgr, TmConfigurationDobj config, Owner_dobj dobj, int purCd,
            SessionVariablesModel sessionVariablesModel) throws VahanException {
        String taxStatus = "";
        String taxMode = null;
        boolean dateExist = false;
        Date taxPaidUpto = null;
        try {

            String sql = "Select to_char(tax_upto,'dd-mm-yyyy') as taxPaidUpto ,tax_upto,tax_mode from " + TableList.VT_TAX
                    + " where regn_no=? and state_cd=? and pur_cd=? and tax_mode != 'B' order by rcpt_dt desc limit 1 ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regNo);
            ps.setString(2, dobj.getState_cd());
            ps.setInt(3, purCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                taxMode = rs.getString("tax_mode");
                if (!"OLS".contains(taxMode)) {
                    taxStatus = rs.getString("taxPaidUpto");
                    taxPaidUpto = rs.getDate("tax_upto");
                    dateExist = true;
                } else {
                    return taxStatus = "OLS";// return tax mode
                }
            }
            Date clearTo = TaxServerImplementation.getTaxDueFromDate(dobj, purCd, sessionVariablesModel.getStateCodeSelected());
            taxStatus = DateUtils.parseDate(clearTo);
//            Date taxClearFrom = null;
//            Date taxClearTo = null;
//            sql = "Select to_char(clear_to,'dd-mm-yyyy') as taxuptodate from " + TableList.VT_TAX_CLEAR + " where regn_no=? and state_cd=? and pur_cd=? order by op_dt desc limit 1 ";
//            ps = tmgr.prepareStatement(sql);
//            ps.setString(1, regNo);
//            ps.setString(2, dobj.getState_cd());
//            ps.setInt(3, purCd);
//            rs = tmgr.fetchDetachedRowSet_No_release();
//            if (rs.next()) {
//                taxStatus = rs.getString("taxuptodate");
//                dateExist = true;
//            }

            if (!dateExist) {//for checking if Tax is Exepmted or not

                TaxExemptionImpl taxExemptionImpl = new TaxExemptionImpl();
                if (taxExemptionImpl.isTaxExempted(regNo)) {
                    taxStatus = "E";
                    dateExist = true;
                } else if (dobj.getPmt_type() <= 0) {
                    PassengerPermitDetailDobj pmt_dobj = null;
                    TaxEndorsementImpl endorsmentTaxEntryImpl = new TaxEndorsementImpl();
                    pmt_dobj = endorsmentTaxEntryImpl.getPermitDobjFromVhaPermitNew(dobj);
                    if (pmt_dobj != null) {
                        if (!CommonUtils.isNullOrBlank(pmt_dobj.getPmt_type_code())) {
                            dobj.setPmt_type(Integer.parseInt(pmt_dobj.getPmt_type_code()));
                        }
                        if (!CommonUtils.isNullOrBlank(pmt_dobj.getPmtCatg())) {
                            dobj.setPmt_catg(Integer.parseInt(pmt_dobj.getPmtCatg()));
                        }
                    }
                    VehicleParameters vehParameters = FormulaUtilities.fillVehicleParametersFromDobj(dobj, sessionVariablesModel);
                    if (isCondition(replaceTagValues(config.getTax_exemption(), vehParameters), "isTaxPaidOrCleared")) {
                        taxStatus = "E";
                        dateExist = true;
                    }
                }

                BlackListedVehicleImpl obj = new BlackListedVehicleImpl();
                BlackListedVehicleDobj blackListedDobj = obj.getBlacklistedVehicleDetails(dobj.getRegn_no(), dobj.getChasi_no());

                if (!dateExist && dobj != null && blackListedDobj != null
                        && blackListedDobj.getComplain_type() == TableConstants.BLTheftCode
                        && blackListedDobj.getFirDate() != null) {
                    if (taxPaidUpto != null && DateUtils.compareDates(blackListedDobj.getFirDate(), taxPaidUpto) <= 1) {
                        taxStatus = "P";
                    } else if (clearTo != null && DateUtils.compareDates(blackListedDobj.getFirDate(), clearTo) <= 1) {
                        taxStatus = "P";
                    }
                }

            }
            if ("DL,UP".contains(dobj.getState_cd()) && dobj.getVehType() == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
                taxStatus = "L";
            }

            if (dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)
                    && dobj.getState_cd().equalsIgnoreCase("DL")) {
                ApplicationInwardImplementation applicationInwardImpl = new ApplicationInwardImplementation();
                if (applicationInwardImpl.taxPaidOrClearedStatusOnVehType(rs.getString("vehregno").trim(), sessionVariablesModel.getStateCodeSelected(), sessionVariablesModel.getOffCodeSelected())) {
                    taxStatus = "R";
                }
            }
//            if (!dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)
//                    && dobj.getState_cd().equalsIgnoreCase("DL")) {
//
//                taxPaidOrClear = applicationInwardImpl.taxPaidOrClearedStatusOnVehType(rs.getString("vehregno").trim());
//            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Fetching Details of Tax Paid or Clear, Please Contact to the System Administrator.");
        }
        return taxStatus;
    }

    public static Map<String, String> taxPaidInfo(boolean isRCPrint, String regNo, int purCd) throws VahanException {
        Map<String, String> taxPaid = new HashMap<>();
        Date taxPaidUpto = null;
        Date taxRcptDate = null;
        String taxMode = "";
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("taxPaidInfo-1");
            String sql = "Select tax_upto,tax_mode,rcpt_dt from " + TableList.VT_TAX + " where regn_no=? and state_cd=? and pur_cd=? order by rcpt_dt desc limit 1 ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, purCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                taxMode = rs.getString("tax_mode");
                if (!"OLSB".contains(taxMode)) {
                    taxPaidUpto = rs.getDate("tax_upto");
                    taxRcptDate = rs.getDate("rcpt_dt");
                }
            }

            Date taxClearTo = null;
            Date taxClearOpDt = null;
            sql = "Select clear_to,op_dt from " + TableList.VT_TAX_CLEAR + " where regn_no=? and state_cd=? and pur_cd=? order by op_dt desc limit 1 ";
            tmgr = new TransactionManagerReadOnly("taxPaidInfo-2");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, purCd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                taxClearTo = rs.getDate("clear_to");
                taxClearOpDt = rs.getDate("op_dt");
            }

            if (taxPaidUpto == null) {
                taxPaidUpto = taxClearTo;
                taxRcptDate = taxClearOpDt;
            } else {
                if (taxClearOpDt != null) {
                    if (isRCPrint) {
                        if (DateUtils.compareDates(taxRcptDate, taxClearOpDt) == 1) {
                            taxPaidUpto = taxClearTo;
                        }
                    } else {
                        if (DateUtils.compareDates(taxPaidUpto, taxClearTo) == 1) {
                            taxPaidUpto = taxClearTo;
                        }
                    }
                }
            }

            String strTaxUpto = taxPaidUpto != null ? DateUtils.parseDate(taxPaidUpto) : null;

            taxPaid.put("tax_mode", taxMode);
            taxPaid.put("tax_upto", strTaxUpto);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Fetching of Tax Details");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return taxPaid;
    }

    /*
        @author Kartikey Singh
     */
    public static Map<String, String> taxPaidInfo(boolean isRCPrint, String regNo, int purCd, String stateCode) throws VahanException {
        Map<String, String> taxPaid = new HashMap<>();
        Date taxPaidUpto = null;
        Date taxRcptDate = null;
        String taxMode = "";
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("taxPaidInfo-1");
            String sql = "Select tax_upto,tax_mode,rcpt_dt from " + TableList.VT_TAX + " where regn_no=? and state_cd=? and pur_cd=? order by rcpt_dt desc limit 1 ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regNo);
            ps.setString(2, stateCode);
            ps.setInt(3, purCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                taxMode = rs.getString("tax_mode");
                if (!"OLSB".contains(taxMode)) {
                    taxPaidUpto = rs.getDate("tax_upto");
                    taxRcptDate = rs.getDate("rcpt_dt");
                }
            }

            Date taxClearTo = null;
            Date taxClearOpDt = null;
            sql = "Select clear_to,op_dt from " + TableList.VT_TAX_CLEAR + " where regn_no=? and state_cd=? and pur_cd=? order by op_dt desc limit 1 ";
            tmgr = new TransactionManagerReadOnly("taxPaidInfo-2");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regNo);
            ps.setString(2, stateCode);
            ps.setInt(3, purCd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                taxClearTo = rs.getDate("clear_to");
                taxClearOpDt = rs.getDate("op_dt");
            }

            if (taxPaidUpto == null) {
                taxPaidUpto = taxClearTo;
                taxRcptDate = taxClearOpDt;
            } else {
                if (taxClearOpDt != null) {
                    if (isRCPrint) {
                        if (DateUtils.compareDates(taxRcptDate, taxClearOpDt) == 1) {
                            taxPaidUpto = taxClearTo;
                        }
                    } else {
                        if (DateUtils.compareDates(taxPaidUpto, taxClearTo) == 1) {
                            taxPaidUpto = taxClearTo;
                        }
                    }
                }
            }

            String strTaxUpto = taxPaidUpto != null ? DateUtils.parseDate(taxPaidUpto) : null;

            taxPaid.put("tax_mode", taxMode);
            taxPaid.put("tax_upto", strTaxUpto);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Fetching of Tax Details");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return taxPaid;
    }

    public static Map<String, Integer> monthList() {
        Map<String, Integer> monthList = new LinkedHashMap<String, Integer>();
        monthList.put("January", 0);
        monthList.put("February", 1);
        monthList.put("March", 2);
        monthList.put("April", 3);
        monthList.put("May", 4);
        monthList.put("June", 5);
        monthList.put("July", 6);
        monthList.put("August", 7);
        monthList.put("September", 8);
        monthList.put("October", 9);
        monthList.put("November", 10);
        monthList.put("December", 11);
        return monthList;
    }

    public static RegistrationStatusParametersDobj fillRegStatusParameters(OwnerDetailsDobj ownerDetails) {
        RegistrationStatusParametersDobj obj = new RegistrationStatusParametersDobj();

        if (ownerDetails != null) {

            obj.setRegnNo(ownerDetails.getRegn_no()); // for setting registration no for display

            //for CHECKING contact Details
            if (ownerDetails.getOwnerIdentity().getMobile_no() == null || ownerDetails.getOwnerIdentity().getMobile_no() <= 999999999) {
                obj.setContactStatus(true);
            }

            //for CHECKING Blacklist Details
            if (ownerDetails.getBlackListedVehicleDobj() != null && ownerDetails.getBlackListedVehicleDobj().getComplain_type() != 0) {
                obj.setBlackListStatus(true);
                obj.setBlackListDescr("BLACKLISTED");
            }

            //for CHECKING NOC Status
            if (ownerDetails.getStatus().equalsIgnoreCase(TableConstants.VT_NOC_ISSUE_STATUS)) {
                obj.setVtOwnerStatus(true);
                obj.setVtOwnerDescr("NOC:ISSUED");
            }

            //for CHECKING RC Cancelled or not
            if (ownerDetails.getStatus().equalsIgnoreCase(TableConstants.VT_RC_CANCEL_STATUS)) {
                obj.setVtOwnerStatus(true);
                obj.setVtOwnerDescr("RC:CANCELLED");
            }

            //for CHECKING RC Surrendered or Not
            if (ownerDetails.getStatus().equalsIgnoreCase(TableConstants.VT_RC_SURRENDER_STATUS)) {
                obj.setVtOwnerStatus(true);
                obj.setVtOwnerDescr("RC:SURRENDERED");
            }
            //for CHECKING DE REGISTERED or Not
            if (ownerDetails.getStatus().equalsIgnoreCase(TableConstants.VT_DE_REGISTRATION)) {
                obj.setVtOwnerStatus(true);
                obj.setVtOwnerDescr("DE:REGISTERED");
            }

            //for CHECKING Vehicle Scraped  or Not
            if (ownerDetails.getStatus().equalsIgnoreCase(TableConstants.VT_SCRAP_VEHICLE_STATUS)) {
                obj.setVtOwnerStatus(true);
                obj.setVtOwnerDescr("VEHICLE:SCRAPED");
            }

            //for CHECKING Insurance is Expired or Not
            if (ownerDetails.getInsDobj() != null) {
                InsImpl insImpl = new InsImpl();
                if (InsImpl.validateOwnerCodeWithInsType(ownerDetails.getOwner_cd(), ownerDetails.getInsDobj().getIns_type())) {
                    if (insImpl.validateInsurance(ownerDetails.getInsDobj())) {
                        obj.setInsuranceStatus(true);
                    }
                }
            }

            boolean isNonUse = NonUseImpl.nonUseDetailsExist(ownerDetails.getRegn_no(), ownerDetails.getState_cd());
            if (isNonUse) {
                obj.setVtOwnerStatus(true);
                obj.setVtOwnerDescr("VEHICLE:IN NONUSE");
            }

        }

        return obj;
    }

    public static void setTimeToBeginningOfDay(java.util.Calendar calendar) {
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
    }

    public static void setTimeToEndofDay(java.util.Calendar calendar) {
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23);
        calendar.set(java.util.Calendar.MINUTE, 59);
        calendar.set(java.util.Calendar.SECOND, 59);
        calendar.set(java.util.Calendar.MILLISECOND, 999);
    }

    public static java.util.Calendar getCalendarForNow(Date date) {
        java.util.Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static String getAvailablePrefixSeries(VehicleParameters vehParameters) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        boolean isSeriesAvailable = false;
        String stateCode;
        int OffCode;
        String retValue = "";
        boolean isStopProcess = false;
        try {
            stateCode = Util.getUserStateCode();
            OffCode = Util.getSelectedSeat().getOff_cd();
            tmgr = new TransactionManager("getAvailableRegnNoList");
            sql = "select * from vm_regn_gen_action  where action_cd in (99991,12302) and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCode);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                isStopProcess = true;
            }

            sql = "select distinct a.criteria_formula, a.upper_range_no, a.running_no, a.prefix_series,no_gen_type, \n"
                    + "  (a.upper_range_no - a.running_no) as diff,COALESCE(COALESCE(a.next_prefix_series,b.prefix_series),'') as next_prefix_series,a.series_id \n"
                    + "  from vm_regn_series a \n"
                    + "  left outer join vm_regn_series_future b on b.state_cd=a.state_cd and b.off_cd=a.off_cd and b.series_id=a.series_id \n"
                    + "  where a.state_cd = ? and a.off_cd = ?"
                    + "  order by 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCode);
            ps.setInt(2, OffCode);
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("criteria_formula"), vehParameters), "getAvailableRegnNoList")) {
                    isSeriesAvailable = true;
                    if (rs.getString("no_gen_type").equals(TableConstants.RANDOM_NUMBER_STATUS_M)) {
                        sql = "select count(1) as total from vm_regn_available where status = ? and state_cd = ? and off_cd = ? and series_id = ? ";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, TableConstants.RANDOM_NUMBER_STATUS_A);
                        ps.setString(2, stateCode);
                        ps.setInt(3, OffCode);
                        ps.setInt(4, rs.getInt("series_id"));
                        RowSet rs1 = tmgr.fetchDetachedRowSet();
                        if (rs1.next()) {
                            int total = rs.getInt("diff") + rs1.getInt("total");
                            if (total < 1) {
                                retValue = TableConstants.SERIES_EXHAUST_MESSAGE;
                                isSeriesAvailable = false;//error
                            } else {
                                retValue = rs.getString("prefix_series");//allow
                                break;
                            }
                        }
                    }

                    if (rs.getInt("upper_range_no") < rs.getInt("running_no") && (rs.getString("next_prefix_series") == null || rs.getString("next_prefix_series").isEmpty())) {
                        retValue = TableConstants.SERIES_EXHAUST_MESSAGE;
                        isSeriesAvailable = false;
                    } else {
                        if (rs.getInt("diff") < 0) {
                            retValue = TableConstants.SERIES_EXHAUST_MESSAGE;
                            isSeriesAvailable = false;
                        } else {
                            isSeriesAvailable = true;
                            if (rs.getInt("upper_range_no") < rs.getInt("running_no")) {
                                retValue = rs.getString("next_prefix_series");
                            } else {
                                retValue = rs.getString("prefix_series");
                            }
                        }
                    }
                }
            }

            if (!isStopProcess && retValue.equals(TableConstants.SERIES_EXHAUST_MESSAGE)) {
                retValue = "";
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return retValue;
    }

    /*
     * Author: Kartikey Singh
     */
    public static String getAvailablePrefixSeries(VehicleParameters vehParameters, String userStateCode, int offCode) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        boolean isSeriesAvailable = false;
        String stateCode;
        int OffCode;
        String retValue = "";
        boolean isStopProcess = false;
        try {
            stateCode = userStateCode;
            OffCode = offCode;
            tmgr = new TransactionManager("getAvailableRegnNoList");
            sql = "select * from vm_regn_gen_action  where action_cd in (99991,12302) and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCode);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                isStopProcess = true;
            }

            sql = "select distinct a.criteria_formula, a.upper_range_no, a.running_no, a.prefix_series,no_gen_type, \n"
                    + "  (a.upper_range_no - a.running_no) as diff,COALESCE(COALESCE(a.next_prefix_series,b.prefix_series),'') as next_prefix_series,a.series_id \n"
                    + "  from vm_regn_series a \n"
                    + "  left outer join vm_regn_series_future b on b.state_cd=a.state_cd and b.off_cd=a.off_cd and b.series_id=a.series_id \n"
                    + "  where a.state_cd = ? and a.off_cd = ?"
                    + "  order by 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCode);
            ps.setInt(2, OffCode);
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("criteria_formula"), vehParameters), "getAvailableRegnNoList")) {
                    isSeriesAvailable = true;
                    if (rs.getString("no_gen_type").equals(TableConstants.RANDOM_NUMBER_STATUS_M)) {
                        sql = "select count(1) as total from vm_regn_available where status = ? and state_cd = ? and off_cd = ? and series_id = ? ";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, TableConstants.RANDOM_NUMBER_STATUS_A);
                        ps.setString(2, stateCode);
                        ps.setInt(3, OffCode);
                        ps.setInt(4, rs.getInt("series_id"));
                        RowSet rs1 = tmgr.fetchDetachedRowSet();
                        if (rs1.next()) {
                            int total = rs.getInt("diff") + rs1.getInt("total");
                            if (total < 1) {
                                retValue = TableConstants.SERIES_EXHAUST_MESSAGE;
                                isSeriesAvailable = false;//error
                            } else {
                                retValue = rs.getString("prefix_series");//allow
                                break;
                            }
                        }
                    }

                    if (rs.getInt("upper_range_no") < rs.getInt("running_no") && (rs.getString("next_prefix_series") == null || rs.getString("next_prefix_series").isEmpty())) {
                        retValue = TableConstants.SERIES_EXHAUST_MESSAGE;
                        isSeriesAvailable = false;
                    } else {
                        if (rs.getInt("diff") < 0) {
                            retValue = TableConstants.SERIES_EXHAUST_MESSAGE;
                            isSeriesAvailable = false;
                        } else {
                            isSeriesAvailable = true;
                            if (rs.getInt("upper_range_no") < rs.getInt("running_no")) {
                                retValue = rs.getString("next_prefix_series");
                            } else {
                                retValue = rs.getString("prefix_series");
                            }
                        }
                    }
                }
            }

            if (!isStopProcess && retValue.equals(TableConstants.SERIES_EXHAUST_MESSAGE)) {
                retValue = "";
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return retValue;
    }

    /**
     * This Method throws vahanMessageException when query returns zero rows
     * updated.
     *
     * @param count pass ps.execute()
     * @param ps prepared Statement
     * @throws VahanMessageException
     */
    public static void validateQueryResult(TransactionManager tmgr, int count, PreparedStatement ps) throws VahanException {
        if (count > 0) {
        } else {
            LOGGER.error("Tmgr:" + tmgr.getWhereiam() + ",Error in Query:" + ps);
            throw new VahanException("There is some problem while processing your request, please try again or contact Administrator with Transaction details.");
        }
    }

    public static String getRegnNoAllotedDetail(String applNo, String stateCd, int offCd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String regnNo = null;

        try {
            tmgr = new TransactionManager("getRegnNoAllotedDetail");
            String sql = "select regn_no from " + TableList.VM_REGN_ALLOTED
                    + " where state_cd=? and off_cd=? and appl_no = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            ps.setString(3, applNo);

            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                regnNo = rs.getString("regn_no");
            }
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            throw new VahanException("Something went wrong during fetching details of allotement of Registration No on Application No-" + applNo);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during fetching details of allotement of Registration No on Application No-" + applNo);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return regnNo;
    }

    public static TmConfigurationDobj getTmConfigurationParameters(String state_cd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        TmConfigurationDobj dobj = null;
        TmConfigurationDealerDobj dealerTmConfigDobj = null;
        TmConfigurationDMS dmsTmConfigDobj = null;
        TmConfigurationUserMgmtDobj userMgmtConfigDobj = null;
        TmConfigurationOtpDobj otpConfigDobj = null;
        TmCofigurationOnlinePaymentDobj onlinePayConfigDobj = null;
        TmConfigurationPrintDobj printConfgDobj = null;
        TmConfigurationTradeCertificateDobj tradeCertConfigDobj = null;
        TmConfigurationUserMessagingDobj userMessagingTmConfigDobj = null;
        TmConfigurationPayVerifyDobj payVerifyTmConfigDobj = null;
        TmConfigurationFasTag tmConfigurationFasTag = null;

        try {
            tmgr = new TransactionManager("getTmConfigurationParameters");
            dealerTmConfigDobj = getDealerTmConfigurationParameters(tmgr, state_cd);
            dmsTmConfigDobj = getDmsTmConfigurationParameters(tmgr, state_cd);
            userMgmtConfigDobj = getUserMgmtTmConfigurationParameters(tmgr, state_cd);
            otpConfigDobj = getOtpTmConfigurationParameters(tmgr, state_cd);
            onlinePayConfigDobj = getOnlinePaymentTmConfigurationParameters(tmgr, state_cd);
            printConfgDobj = getTmConfigurationPrintParameters(tmgr, state_cd);
            tradeCertConfigDobj = getTradeCertificateTmConfigurationParameters(tmgr, state_cd);
            userMessagingTmConfigDobj = getUserMessagingTmConfigurationParameters(tmgr, state_cd);
            payVerifyTmConfigDobj = getPayVerifyTmConfigurationParameters(tmgr, state_cd);
            tmConfigurationFasTag = getTmConfigurationFasTagParameters(tmgr, state_cd);
            sql = "SELECT * FROM " + TableList.TM_CONFIGURATION
                    + " WHERE state_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);

            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new TmConfigurationDobj();
                dobj.setTmConfigDealerDobj(dealerTmConfigDobj);
                dobj.setTmConfigDmsDobj(dmsTmConfigDobj);
                dobj.setTmConfigUserMgmtDobj(userMgmtConfigDobj);
                dobj.setTmConfigOtpDobj(otpConfigDobj);
                dobj.setTmConfigOnlineDobj(onlinePayConfigDobj);
                dobj.setTmPrintConfgDobj(printConfgDobj);
                dobj.setTmTradeCertConfigDobj(tradeCertConfigDobj);
                dobj.setTmUserMessagingConfigDobj(userMessagingTmConfigDobj);
                dobj.setTmConfigPayVerifyDobj(payVerifyTmConfigDobj);
                dobj.setTmConfigurationFasTag(tmConfigurationFasTag);
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setFinancial_year_tax(rs.getBoolean("financial_year_tax"));
                dobj.setCalendar_month_tax(rs.getBoolean("calendar_month_tax"));
                dobj.setService_charges_per_rcpt(rs.getBoolean("service_charges_per_rcpt"));
                dobj.setService_charges_per_trans(rs.getBoolean("service_charges_per_trans"));
                dobj.setRegn_gen_type(rs.getString("regn_gen_type"));
                dobj.setRegn_gen_random_batch(rs.getInt("regn_gen_random_batch"));
                dobj.setRcpt_heading(rs.getString("rcpt_heading"));
                dobj.setRcpt_subheading(rs.getString("rcpt_subheading"));
                dobj.setTax_exemption(rs.getString("tax_exemption"));
                dobj.setEx_showroom_price_homologation(rs.getBoolean("ex_showroom_price_homologation"));
                dobj.setConsider_holiday_fine(rs.getBoolean("consider_holiday_fine"));
                dobj.setAdvance_regn_no(rs.getBoolean("advance_regn_no"));
                dobj.setBiometrics(rs.getBoolean("biometrics"));
                dobj.setPaper_rc(rs.getString("paper_rc"));
                dobj.setProv_rc(rs.getBoolean("prov_rc"));
                dobj.setFit_failed_grace_days(rs.getInt("fit_failed_grace_days"));
                dobj.setAdvanceNoJump(rs.getInt("advance_jump_regn"));
                dobj.setTax_adjustment(rs.getBoolean("tax_adjustment"));
                dobj.setTax_adjustment_with_surcharge(rs.getInt("tax_adjustment_with_surcharge"));
                dobj.setTo_retention(rs.getBoolean("to_retention"));
                dobj.setFitness_rqrd_for(rs.getString("fitness_rqrd_for"));
                dobj.setRenewal_regn_rqrd_for(rs.getString("renewal_regn_rqrd_for"));
                dobj.setConsiderTradeCert(rs.getBoolean("consider_trade_cert"));
                dobj.setTo_retention_for_all_regn(rs.getBoolean("to_retention_for_all_regn"));
                dobj.setIs_rc_dispatch(rs.getBoolean("is_rc_dispatch"));
                dobj.setNid_days(rs.getInt("nid_days"));
                dobj.setFit_fine_due_nid(rs.getBoolean("fit_fine_due_nid"));
                dobj.setProv_rc_rto(rs.getBoolean("prov_rc_rto"));
                dobj.setTemp_tax_as_mvtax(rs.getBoolean("temp_tax_as_mvtax"));
                dobj.setAuto_cash_rcpt_gen(rs.getBoolean("auto_cash_rcpt_gen"));
                dobj.setScrap_veh_no_retain(rs.getBoolean("scrap_veh_no_retain"));
                dobj.setFee_amt_zero(rs.getString("fee_amt_zero"));
                dobj.setReassign_retained_no_with_to(rs.getBoolean("reassign_retained_no_with_to"));
                dobj.setDealer_auth_for_all_office(rs.getBoolean("dealer_auth_for_all_office"));
                dobj.setUser_signature(rs.getBoolean("user_signature"));
                dobj.setAuto_tax_mode_filler(rs.getBoolean("auto_tax_mode_filler"));
                dobj.setInsurance_validity(rs.getInt("insurance_validity"));
                dobj.setRc_after_hsrp(rs.getBoolean("rc_after_hsrp"));
                dobj.setTax_installment(rs.getString("tax_installment"));
                dobj.setFancyFeeValidMode(rs.getString("fancy_fee_valid_mod"));
                dobj.setFancyFeeValidPeriod(rs.getInt("fancy_fee_valid_period"));
                dobj.setAuto_tax_no_units(rs.getBoolean("auto_tax_no_units"));
                dobj.setTcNoForEachVehCatg(rs.getBoolean("tcno_for_each_veh_catg"));
                dobj.setScrap_veh_type(rs.getString("scrap_veh_type"));
                dobj.setScrap_ret_age(rs.getString("scrap_ret_age"));
                dobj.setSmartcard_fee_at_vendor(rs.getBoolean("smartcard_fee_at_vendor"));
                dobj.setCnginfo_from_cngmaker(rs.getBoolean("cnginfo_from_cngmaker"));
                dobj.setPermit_exemption(rs.getString("permit_exemption"));
                dobj.setReassign_retained_no_with_conv(rs.getBoolean("reassign_retained_no_with_conv"));
                dobj.setOnlinePayment(rs.getBoolean("online_payment"));
                dobj.setFine_penalty_exemtion(rs.getBoolean("fine_penalty_exemtion"));
                dobj.setRandom_odd_even_reassign_allowed(rs.getBoolean("random_odd_even_reassign_allowed"));
                dobj.setTempFeeInNewRegis(rs.getBoolean("temp_fee_in_new_regis"));
                dobj.setOldFeeValidMod(rs.getString("old_fee_valid_mod"));
                dobj.setOldFeeValidPeriod(rs.getInt("old_fee_valid_period"));
                dobj.setRegnNoNotAssignOthState(rs.getBoolean("not_assign_regn_no_oth_state"));
                dobj.setFancyFeeEditable(rs.getBoolean("fancy_fee_editable"));
                dobj.setSmart_card_hpa_hpt(rs.getBoolean("smart_card_hpa_hpt"));
                dobj.setTempRegnToNewRegnAtDealer(rs.getBoolean("temp_regn_to_new_regn_dealer"));
                dobj.setNum_gen_allowed_dealer(rs.getBoolean("num_gen_allowed_dealer"));
                dobj.setApplInwardExempForVehAgeExpire(rs.getString("applinwardexempforvehageexpire"));
                dobj.setMv_tax_at_any_office(rs.getBoolean("mv_tax_at_any_office"));
                dobj.setIs_rc_dispatch_without_postal_fee(rs.getBoolean("is_rc_dispatch_without_postal_fee"));
                dobj.setNoOfApplsForDealerPayment(rs.getInt("no_of_appls_for_dealer_payment"));
                dobj.setHold_regnNo_with_conversion(rs.getBoolean("hold_regnNo_with_conversion"));
                dobj.setAllow_fitness_all_RTO(rs.getBoolean("allow_fitness_all_rto"));
                dobj.setRegn_upto_as_fit_upto(rs.getBoolean("regn_upto_as_fit_upto"));
                dobj.setConsiderFMSDealer(rs.getBoolean("consider_fms_dealer"));
                dobj.setRen_regn_from_date(rs.getString("ren_regn_from_date"));
                dobj.setNew_reg_loi(rs.getBoolean("new_reg_loi"));
                dobj.setValidateHomoSeatCap(rs.getBoolean("editable_homo_seat_cap_at_fit"));
                dobj.setMobile_verify(rs.getBoolean("mobile_verify"));
                dobj.setRetentionFeeValidMode(rs.getString("ret_fee_valid_mod"));
                dobj.setRetentionFeeValidPeriod(rs.getInt("ret_fee_valid_period"));
                dobj.setUser_catg_mandate_otp(rs.getString("user_catg_mandate_otp"));
                dobj.setFmsGraceDays(rs.getInt("fms_grace_days"));
                dobj.setIsFancyFeeZero(rs.getBoolean("isFancyFeeZero"));
                dobj.setOtpforOwnerAdmin(rs.getBoolean("otp_for_owner_admin"));
                dobj.setIs_rc_dispatch_repost_fee(rs.getBoolean("is_rc_dispatch_repost_fee"));
                dobj.setBlocked_purcd_for_blacklist_vehicle(rs.getString("blocked_purcd_for_blacklist_vehicle"));
                dobj.setOwner_admin_modify_on_status(rs.getString("owneradmin_modify_on_status"));
                dobj.setOther_rto_number_change(rs.getString("other_off_regn_no_change_in_new_rto"));
                dobj.setDefacement(rs.getBoolean("isDefacement"));
                dobj.setUpdateadditionallttpurcd(rs.getString("update_additional_ltt_purcd"));
                dobj.setNoCashPayment(rs.getBoolean("no_cash_payment"));
                dobj.setVltd_condition_formula(rs.getString("vltd_condition_formula"));
                dobj.setAllowRegnDataFoundForOS_OR(rs.getBoolean("allow_regn_data_found_for_os_or"));
                dobj.setAllowRegnDataFoundForTRC(rs.getBoolean("allow_regn_data_found_for_trc"));
                dobj.setAllowFacelessService(rs.getBoolean("allow_faceless_service"));
                dobj.setShowPermitMultiRegion(rs.getString("show_permit_multi_region"));
                dobj.setMdfVehDtlsApproveByStateAdmin(rs.getBoolean("mdf_veh_dtls_approve_by_state_admin"));//forstateadmin
                dobj.setEchallan_pur_cd_restict(rs.getString("echallan_pur_cd_restict"));

            }
            if (dobj == null) {
                throw new VahanException("Problem in getting the Configuration Details, Please try after sometime.");
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return dobj;
    }

    public static TmConfigurationDealerDobj getDealerTmConfigurationParameters(TransactionManager tmgr, String stateCd) throws VahanException {
        TmConfigurationDealerDobj dealerConfigDobj = null;
        PreparedStatement ps = null;
        try {
            String sql = "SELECT state_cd,tax_exemption_allowed, transport_vch_new_regn_at_dealer, validate_homo_sale_amt, temp_regn_fee_with_new_for_same_offices, temp_regn_approval_before_new_regn, "
                    + " payment_at_office, fee_tax_amt_zero, non_transport_vch_temp_regn_at_dealer, norms_condition_formulas, off_correction_at_office, exmpt_service_charge_in_trc, attach_fancy_no_at_dealer, "
                    + " allow_regn_if_data_found, temp_flow_action_cd, allow_inward_temp_any_office, allowed_owner_code, check_hsrp_pendency, owner_choice_condition_formula, dealer_validity_required,print_new_rc_at_dealer,"
                    + " regn_restriction_at_dealer, regn_restriction_message  FROM " + TableList.TM_CONFIGURATION_DEALER + " where state_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dealerConfigDobj = new TmConfigurationDealerDobj();
                dealerConfigDobj.setStateCd(rs.getString("state_cd"));
                dealerConfigDobj.setTaxExemptionAllowed(rs.getBoolean("tax_exemption_allowed"));
                dealerConfigDobj.setNewRegnNotAllowTransVeh(rs.getBoolean("transport_vch_new_regn_at_dealer"));
                dealerConfigDobj.setValidateHomoSaleAmt(rs.getBoolean("validate_homo_sale_amt"));
                dealerConfigDobj.setTempRegnFeeWithNewForSameOffices(rs.getBoolean("temp_regn_fee_with_new_for_same_offices"));
                dealerConfigDobj.setTempRegnApprovalBeforeNewRegn(rs.getBoolean("temp_regn_approval_before_new_regn"));
                dealerConfigDobj.setPaymentAtOffice(rs.getBoolean("payment_at_office"));
                dealerConfigDobj.setFeeTaxAmtZero(rs.getString("fee_tax_amt_zero"));
                dealerConfigDobj.setTempRegnAllowNonTransVeh(rs.getBoolean("non_transport_vch_temp_regn_at_dealer"));
                dealerConfigDobj.setNormsConditionFormulas(rs.getString("norms_condition_formulas"));
                dealerConfigDobj.setOffCorrectionAtOffice(rs.getBoolean("off_correction_at_office"));
                dealerConfigDobj.setExmptServiceChargeInTRC(rs.getBoolean("exmpt_service_charge_in_trc"));
                dealerConfigDobj.setAttachFancyNoAtDealer(rs.getBoolean("attach_fancy_no_at_dealer"));
                dealerConfigDobj.setAllowRegnIfDataFound(rs.getBoolean("allow_regn_if_data_found"));
                dealerConfigDobj.setTempFlowInNewRegnActionCd(rs.getInt("temp_flow_action_cd"));
                dealerConfigDobj.setAllowInwardTempAnyOffice(rs.getBoolean("allow_inward_temp_any_office"));
                dealerConfigDobj.setAllowedOwnerCode(rs.getString("allowed_owner_code"));
                dealerConfigDobj.setCheckHSRPPendency(rs.getBoolean("check_hsrp_pendency"));
                dealerConfigDobj.setOwnerChoiceConditionFormula(rs.getString("owner_choice_condition_formula"));
                dealerConfigDobj.setDealerValidityRequired(rs.getBoolean("dealer_validity_required"));
                dealerConfigDobj.setPrintNewRCAtDealer(rs.getBoolean("print_new_rc_at_dealer"));
                dealerConfigDobj.setRegnRestrictionAtDealer(rs.getString("regn_restriction_at_dealer"));
                dealerConfigDobj.setRegnRestrictionMessage(rs.getString("regn_restriction_message"));
            }

            if (dealerConfigDobj == null) {
                throw new VahanException("Problem in getting the Dealer Configuration Details, Please try after sometime.");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " getDealerTmConfigurationParameters " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting the Dealer Configuration Details, Please try after sometime.");
        }
        return dealerConfigDobj;
    }

    public static TmConfigurationFasTag getTmConfigurationFasTagParameters(TransactionManager tmgr, String stateCd) throws VahanException {
        TmConfigurationFasTag fasTagDobj = null;
        PreparedStatement ps = null;
        try {
            String sql = "SELECT state_cd, fastag_mandatory, fastag_condition "
                    + "  FROM " + TableList.TM_CONFIGURATION_FASTAG + " where state_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                fasTagDobj = new TmConfigurationFasTag();
                fasTagDobj.setStateCd(stateCd);
                fasTagDobj.setFasTagCondition(rs.getString("fastag_condition"));
                fasTagDobj.setFasTagMandatory(rs.getBoolean("fastag_mandatory"));
            }
            if (fasTagDobj == null) {
                throw new VahanException("Problem in getting the FasTag Configuration Details, Please try after sometime.");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " getDealerTmConfigurationParameters " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting the FasTag Configuration Details, Please try after sometime.");
        }
        return fasTagDobj;
    }

    public static TmConfigurationTradeCertificateDobj getTradeCertificateTmConfigurationParameters(TransactionManager tmgr, String stateCd) throws VahanException {
        TmConfigurationTradeCertificateDobj tcConfigDobj = null;
        PreparedStatement ps = null;
        try {
            String sql = "SELECT state_cd, display_fuel, do_not_show_no_of_vehicles, duplicate_tc_not_applicable, documents_upload_n_revert, dealer_valid_upto_plus_1_yr_as_tc_valid_upto, "
                    + " valid_from_next_day_of_prev_valid_upto, valid_upto_instead_of_validity_period, tc_printing_data_for_dealer, render_veh_class, multiple_tc_having_unique_tc_no, "
                    + " fee_with_tax_plus_other_variables, using_online_schema_tc, fees_not_to_be_shown_in_form_16, verify_role_on_dms_page, appl_to_have_single_veh_catg, "
                    + " dealer_master_not_to_be_updated, update_no_of_vehicle_without_using_balance, form_17_sub_list_data_in_details, calculate_tax_from_web_service, "
                    + " fuel_tax_applicable, surcharge_via_web_service, illegitimate_trade_cert_applicable, tax_applicable_for_illegitimate_trade_cert, "
                    + " yard_fee_applicable, service_charge_multiply_by_no_of_trade_cert, service_charge_hard_coded, fee_hard_coded, fee_as_per_vehicle_class, "
                    + " stock_transfer_req, do_not_consider_no_of_vch_for_expiry, tc_no_with_year, serial_no_with_tc, inspec_dtls_req, "
                    + " add_new_serial_number_to_existing_tc, applicant_type_in_inward_form_at_rto_side, tc_initiation_in_rto_by_code, "
                    + " reassign_of_tc_applicable, watermark_req, show_only_admin_dealers, cmvr_vch_catg_applicable, enable_add_vch_catg, dms_required, allow_faceless_service "
                    + " FROM " + TableList.TM_CONFIGURATION_TRADE_CERT + " where state_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                tcConfigDobj = new TmConfigurationTradeCertificateDobj();
                tcConfigDobj.setStateCd(rs.getString("state_cd"));
                tcConfigDobj.setFuelToBeDisplayed(rs.getBoolean("display_fuel"));
                tcConfigDobj.setNoOfVehiclesNotToBeShown(rs.getBoolean("do_not_show_no_of_vehicles"));
                tcConfigDobj.setDuplicateTcNotApplicable(rs.getBoolean("duplicate_tc_not_applicable"));
                tcConfigDobj.setDocumentsUploadNRevert(rs.getBoolean("documents_upload_n_revert"));
                tcConfigDobj.setDealerValidUptoPlus1YrAsTcValidUpto(rs.getBoolean("dealer_valid_upto_plus_1_yr_as_tc_valid_upto"));
                tcConfigDobj.setValidFromNextDayOfPrevValidUpto(rs.getBoolean("valid_from_next_day_of_prev_valid_upto"));
                tcConfigDobj.setValidUptoInsteadOfValidityPeriod(rs.getBoolean("valid_upto_instead_of_validity_period"));
                tcConfigDobj.setTcPrintingDataForDealer(rs.getBoolean("tc_printing_data_for_dealer"));
                tcConfigDobj.setVehClassToBeRendered(rs.getBoolean("render_veh_class"));
                tcConfigDobj.setMultipleTcHavingUniqueTcNo(rs.getBoolean("multiple_tc_having_unique_tc_no"));
                tcConfigDobj.setFeeWithTaxPlusOtherVariables(rs.getBoolean("fee_with_tax_plus_other_variables"));
                tcConfigDobj.setUsingOnlineSchemaTc(rs.getBoolean("using_online_schema_tc"));
                tcConfigDobj.setFeesNotToBeShownInForm16(rs.getBoolean("fees_not_to_be_shown_in_form_16"));
                tcConfigDobj.setVerifyRoleOnDmsPage(rs.getBoolean("verify_role_on_dms_page"));
                tcConfigDobj.setApplToHaveSingleVehCatg(rs.getBoolean("appl_to_have_single_veh_catg"));
                tcConfigDobj.setDealerMasterNotToBeUpdated(rs.getBoolean("dealer_master_not_to_be_updated"));
                tcConfigDobj.setNoOfVehicleToBeUpdatedWithoutUsingBalance(rs.getBoolean("update_no_of_vehicle_without_using_balance"));
                tcConfigDobj.setSubListDataDetailsToBeShownInForm17(rs.getBoolean("form_17_sub_list_data_in_details"));
                tcConfigDobj.setTaxToBeCalculatedWebService(rs.getBoolean("calculate_tax_from_web_service"));
                tcConfigDobj.setFuelTaxApplicable(rs.getBoolean("fuel_tax_applicable"));
                tcConfigDobj.setSurchargeToBeCalculatedViaWebService(rs.getBoolean("surcharge_via_web_service"));
                tcConfigDobj.setIllegitimateTradeCertApplicable(rs.getBoolean("illegitimate_trade_cert_applicable"));
                tcConfigDobj.setTaxApplicableForIllegitimateTradeCert(rs.getBoolean("tax_applicable_for_illegitimate_trade_cert"));
                tcConfigDobj.setYardFeeApplicable(rs.getBoolean("yard_fee_applicable"));
                tcConfigDobj.setServiceChargeMultiplyByNoOfTradeCert(rs.getBoolean("service_charge_multiply_by_no_of_trade_cert"));
                tcConfigDobj.setServiceChargeHardCoded(rs.getBoolean("service_charge_hard_coded"));
                tcConfigDobj.setFeeToBeHardCoded(rs.getBoolean("fee_hard_coded"));
                tcConfigDobj.setFeeAsPerVehicleClass(rs.getBoolean("fee_as_per_vehicle_class"));
                tcConfigDobj.setStockTransferReq(rs.getBoolean("stock_transfer_req"));
                tcConfigDobj.setDoNotConsiderNoOfVchForExpiry(rs.getBoolean("do_not_consider_no_of_vch_for_expiry"));
                tcConfigDobj.setSerialNoWithTc(rs.getBoolean("serial_no_with_tc"));
                tcConfigDobj.setInspectionDtlsReq(rs.getBoolean("inspec_dtls_req"));
                tcConfigDobj.setTcInitiationInRtoByCode(rs.getBoolean("tc_initiation_in_rto_by_code"));
                tcConfigDobj.setWatermarkReq(rs.getBoolean("watermark_req"));
                tcConfigDobj.setShowOnlyAdminDealers(rs.getBoolean("show_only_admin_dealers"));
                tcConfigDobj.setCmvrVchCatgApplicable(rs.getBoolean("cmvr_vch_catg_applicable"));
                tcConfigDobj.setAllowFacelessService(rs.getBoolean("allow_faceless_service"));
            }

            if (tcConfigDobj == null) {
                throw new VahanException("Problem in getting the Trade Certificate Configuration Details, Please try after sometime.");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " getTradeCertificateTmConfigurationParameters " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting the Trade Certificate Configuration Details, Please try after sometime.");
        }
        return tcConfigDobj;
    }

    public static TmConfigurationDMS getDmsTmConfigurationParameters(TransactionManager tmgr, String stateCd) throws VahanException {
        TmConfigurationDMS dmsConfigDobj = null;
        PreparedStatement ps = null;
        try {
            String sql = "SELECT is_doc_upload, is_doc_verify, is_doc_approve, is_temp_doc_upload, is_doc_upload_at_office, doc_upload_allow_off, upload_action_cd,"
                    + " verify_action_cd, approve_action_cd, temp_approve_action_cd, pur_cd, is_doc_flow, digital_sign_off_wise, api_based_doc_upload,digital_sign_allow FROM " + TableList.TM_CONFIGURATION_DMS + " where state_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dmsConfigDobj = new TmConfigurationDMS();
                dmsConfigDobj.setDocsUpload(rs.getBoolean("is_doc_upload"));
                dmsConfigDobj.setDocsVerify(rs.getBoolean("is_doc_verify"));
                dmsConfigDobj.setDocsApprove(rs.getBoolean("is_doc_approve"));
                dmsConfigDobj.setTempDocsUpload(rs.getBoolean("is_temp_doc_upload"));
                dmsConfigDobj.setDocsUploadAtOffice(rs.getBoolean("is_doc_upload_at_office"));
                dmsConfigDobj.setDocUploadAllotedOff(rs.getString("doc_upload_allow_off"));
                dmsConfigDobj.setUploadActionCd(rs.getString("upload_action_cd"));
                dmsConfigDobj.setVerfyActionCd(rs.getString("verify_action_cd"));
                dmsConfigDobj.setApproveActionCd(rs.getString("approve_action_cd"));
                dmsConfigDobj.setTempApproveActionCd(rs.getString("temp_approve_action_cd"));
                dmsConfigDobj.setPurCd(rs.getString("pur_cd"));
                dmsConfigDobj.setDocsFolwRequired(rs.getBoolean("is_doc_flow"));
                dmsConfigDobj.setDigitalSignAllowOffWise(rs.getString("digital_sign_off_wise"));
                dmsConfigDobj.setApiBasedDocUpload(rs.getBoolean("api_based_doc_upload"));
                dmsConfigDobj.setDigitalSignAllowStateWise(rs.getBoolean("digital_sign_allow"));
            }

            if (dmsConfigDobj == null) {
                throw new VahanException("Problem in getting the DMS Configuration Details, Please try after sometime.");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " getDmsTmConfigurationParameters " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting the DMS Configuration Details, Please try after sometime.");
        }
        return dmsConfigDobj;
    }

    public static String getLinkApplNo(String appl_no) {
        String link_appl_no = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("inside getLinkApplNo");
            String sql = " select link_appl_no from " + TableList.VA_RETENTION + " where appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                link_appl_no = rs.getString("link_appl_no");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
            return link_appl_no;
        }
    }

    public static String getDealerTradeCertificateDetails(String dealerCd, String vchCatg, String stateCd, TmConfigurationDobj tmConfig) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        Date validUptoTradeCert = null;
        Date validUptoDealerValidity = null;
        String sql = null;
        RowSet rs = null;
        try {
            if (tmConfig != null && tmConfig.getTmTradeCertConfigDobj() != null) {
                if (tmConfig.isConsiderTradeCert()) {
                    tmgr = new TransactionManagerReadOnly("getDealerTradeCertificateDetails");
                    Date currentDate = new Date();
                    if (!CommonUtils.isNullOrBlank(vchCatg)) {
                        if (tmConfig.getTmTradeCertConfigDobj().isCmvrVchCatgApplicable()) {
                            sql = "select b.valid_upto from " + TableList.VM_TRADE_VCH_CATG_MAPPING + " a \n"
                                    + " inner join " + TableList.VT_TRADE_CERTIFICATE + " b on b.vch_catg = any(string_to_array(a.v4_veh_catg,','))\n"
                                    + " where  b.dealer_cd = ? and b.state_cd = ? and ? = any(string_to_array(a.v4_veh_catg,',')) order by 1 desc";
                        } else {
                            sql = "select no_of_vch,valid_upto,no_of_vch_used from " + TableList.VT_TRADE_CERTIFICATE + " where dealer_cd = ? and state_cd = ? and vch_catg = ? order by valid_upto desc";
                        }
                    } else {
                        sql = "select no_of_vch,valid_upto,no_of_vch_used from " + TableList.VT_TRADE_CERTIFICATE + " where dealer_cd = ? and state_cd = ? order by valid_upto desc";
                    }
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, dealerCd);
                    ps.setString(2, stateCd);
                    if (!CommonUtils.isNullOrBlank(vchCatg)) {
                        ps.setString(3, vchCatg);
                    }
                    rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        validUptoTradeCert = new Date(rs.getDate("valid_upto").getTime());
                    }
                    if (validUptoTradeCert != null) {
                        if (DateUtils.compareDates(currentDate, validUptoTradeCert) == 2) {
                            if (!CommonUtils.isNullOrBlank(vchCatg)) {
                                return "Trade Certificate has been expired for the Vehicle Category " + MasterTableFiller.masterTables.VM_VCH_CATG.getDesc(vchCatg) + " . Last Validity was " + DateUtil.parseDateToString(validUptoTradeCert);
                            } else {
                                return TableConstants.TRADE_CERT_EXPIRED + DateUtil.parseDateToString(validUptoTradeCert);
                            }
                        }
                    } else {
                        if (!CommonUtils.isNullOrBlank(vchCatg)) {
                            return "Trade Certificate details not found for the Vehicle Category " + MasterTableFiller.masterTables.VM_VCH_CATG.getDesc(vchCatg) + ".";
                        } else {
                            return "Trade Certificate has been expired, Kindly Renew Trade Certificate.";
                        }
                    }
                }
                if (tmConfig.getTmConfigDealerDobj() != null && tmConfig.getTmConfigDealerDobj().isDealerValidityRequired()) {
                    tmgr = new TransactionManagerReadOnly("getDealerTradeCertificateDetails");
                    sql = "select valid_upto from " + TableList.VM_DEALER_MAST + " where dealer_cd = ? and state_cd = ? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, dealerCd);
                    ps.setString(2, stateCd);
                    rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        if (rs.getDate("valid_upto") != null) {
                            validUptoDealerValidity = new Date(rs.getDate("valid_upto").getTime());
                            if (DateUtils.compareDates(new Date(), validUptoDealerValidity) == 2) {
                                return TableConstants.DEALER_VALIDITY_EXPIRED + DateUtil.parseDateToString(validUptoDealerValidity);
                            }
                        } else {
                            return "Dealer Validity has been expired, Please contact to respective Office Admin to update the validity.";
                        }
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return null;
    }

    public static String getCatgDesc(String vch_catg) {
        String catg_desc = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("inside getCatgDesc");
            String sql = " select catg_desc from " + TableList.VM_VCH_CATG + " where catg=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, vch_catg);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                catg_desc = rs.getString("catg_desc");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        }
        return catg_desc;
    }

    public static String getOfficeName(int offCode, String stateCode) {
        String officeName = "";
        TransactionManager tmgr = null;
        String ChasiSQL = "select off_name from tm_office where off_cd=? and state_cd = ?";
        try {
            tmgr = new TransactionManager("getOfficeName");
            PreparedStatement ps = tmgr.prepareStatement(ChasiSQL);
            ps.setInt(1, offCode);
            ps.setString(2, stateCode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                officeName = rs.getString("off_name");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return officeName;
    }

    public static int getOfficeCodeType(int offCode, String stateCode) {
        int officeName = 0;
        TransactionManager tmgr = null;
        String ChasiSQL = "select off_type_cd from tm_office where off_cd=? and state_cd = ?";
        try {
            tmgr = new TransactionManager("getOfficeName");
            PreparedStatement ps = tmgr.prepareStatement(ChasiSQL);
            ps.setInt(1, offCode);
            ps.setString(2, stateCode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                officeName = rs.getInt("off_type_cd");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return officeName;
    }

    public static String isNewRegnNotAllowed(VehicleParameters vehParam) {
        String sql = "select new_regn_not_allowed,new_regn_not_allowed_msg from vm_smart_card_hsrp where state_cd = ? and off_cd = ?";
        TransactionManager tmgr = null;
        String msg = "";
        try {
            tmgr = new TransactionManager("getCriteriaForParticularOffice");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("new_regn_not_allowed"), vehParam), "isNewRegnNotAllowed")) {
                    msg = rs.getString("new_regn_not_allowed_msg");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return msg;
    }

    /*
     * @author Kartikey Singh
     */
    public static String isNewRegnNotAllowed(VehicleParameters vehParam, String userStateCode, int officeCode) {
        String sql = "select new_regn_not_allowed,new_regn_not_allowed_msg from vm_smart_card_hsrp where state_cd = ? and off_cd = ?";
        TransactionManager tmgr = null;
        String msg = "";
        try {
            tmgr = new TransactionManager("getCriteriaForParticularOffice");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, userStateCode);
            ps.setInt(2, officeCode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("new_regn_not_allowed"), vehParam), "isNewRegnNotAllowed")) {
                    msg = rs.getString("new_regn_not_allowed_msg");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return msg;
    }

    public static List<String> isHSRPUploaded(List<PrintCertificatesDobj> selectedCertDobj, String rcRadiobtn) {
        List<String> regnList = new ArrayList<>();
        TransactionManager tmgr = null;
        String sql;
        RowSet rs;
        try {
            PreparedStatement ps;
            String regn_no = "";
            for (int i = 0; i < selectedCertDobj.size(); i++) {
                if (i == 0 && i < selectedCertDobj.size() - 1) {
                    regn_no += "'" + selectedCertDobj.get(i).getRegno() + "',";
                    regnList.add(selectedCertDobj.get(i).getRegno());
                } else if (i > 0 && i < selectedCertDobj.size() - 1) {
                    regn_no += "'" + selectedCertDobj.get(i).getRegno() + "',";
                    regnList.add(selectedCertDobj.get(i).getRegno());
                } else {
                    regn_no += "'" + selectedCertDobj.get(i).getRegno() + "'";
                    regnList.add(selectedCertDobj.get(i).getRegno());
                }
                if (TableConstants.CHECK_MANU_MONTH_YEAR_FOR_HSRP > selectedCertDobj.get(i).getMfgYearMonthYYYYMM() && (rcRadiobtn.equalsIgnoreCase("REPRINTRC") || rcRadiobtn.equalsIgnoreCase("PRTRC") || rcRadiobtn.equalsIgnoreCase("PENRC"))) {
                    regnList.remove(selectedCertDobj.get(i).getRegno());
                }
            }
            if (!regn_no.equals("")) {
                tmgr = new TransactionManager("isHSRPUploaded");
                sql = "select appl_no,regn_no from  " + TableList.VT_HSRP + " where regn_no in (" + regn_no + ")";
                ps = tmgr.prepareStatement(sql);
                rs = tmgr.fetchDetachedRowSet();
                while (rs.next()) {
                    regnList.remove(rs.getString("regn_no"));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return regnList;
    }

    public static String[] getFieldsReqForTax(String state_cd, int vhClass) {
        String[] codeList = null;
        PreparedStatement ps;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getTaxPerameter");
            String Query = "SELECT string_agg(code,',') as code from get_fields_rqrd_for_tax(?,?)";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, state_cd);
            ps.setInt(i++, vhClass);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                String code = rs.getString("code");
                codeList = code.split(",");
            }
        } catch (SQLException e) {
            codeList = null;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                codeList = null;
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return codeList;
    }

    public static Date getVahan4StartDate(String stateCdTo, int offCdTo) {
        Date vahan4StrtDt = null;
        TransactionManager tmgr = null;
        String sql = "";
        if (Util.getUserCategory().equals(TableConstants.USER_CATG_STATE_ADMIN)) {//forstateadmin
            sql = "select min(vow4) as vow4 from " + TableList.TM_OFFICE + " where state_cd = ? ";
        } else {
            sql = "select vow4 from " + TableList.TM_OFFICE + " where state_cd = ? and off_cd = ? ";
        }

        try {
            tmgr = new TransactionManager("getVahan4StartDate");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCdTo);
            if (!Util.getUserCategory().equals(TableConstants.USER_CATG_STATE_ADMIN)) {//forstateadmin
                ps.setInt(2, offCdTo);
            }
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                vahan4StrtDt = rs.getDate("vow4");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return vahan4StrtDt;
    }

    public static Date getVahan4StartDate(String stateCdTo, int offCdTo, String userCategory) {
        Date vahan4StrtDt = null;
        TransactionManager tmgr = null;
        String sql = "";
        if (userCategory.equals(TableConstants.USER_CATG_STATE_ADMIN)) {//forstateadmin
            sql = "select min(vow4) as vow4 from " + TableList.TM_OFFICE + " where state_cd = ? ";
        } else {
            sql = "select vow4 from " + TableList.TM_OFFICE + " where state_cd = ? and off_cd = ? ";
        }

        try {
            tmgr = new TransactionManager("getVahan4StartDate");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCdTo);
            if (!userCategory.equals(TableConstants.USER_CATG_STATE_ADMIN)) {//forstateadmin
                ps.setInt(2, offCdTo);
            }
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                vahan4StrtDt = rs.getDate("vow4");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return vahan4StrtDt;
    }

    public static String getChassisNoExist(String chassisNo) throws VahanException {
        String chasiNoExist = "";
        TransactionManager tmgr = null;
        String sql = "select state_cd, off_cd, regn_no, chasi_no from " + TableList.VT_OWNER + " where chasi_no = ? and status NOT IN ('N','C') ";
        try {
            tmgr = new TransactionManager("getChasiNoExist");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, chassisNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                chasiNoExist = chasiNoExist + rs.getString("regn_no") + "[" + rs.getString("state_cd") + "-" + rs.getInt("off_cd") + "],";
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Getting Chassis Details");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return chasiNoExist;
    }

    public static NocDobj getChasiNoExist(String chassisNo) throws VahanException {
        String chasi_no = null;
        TransactionManager tmgr = null;
        NocDobj nocDobj = null;
        String sql = "select b.state_cd,b.off_cd, a.state_to,a.off_to,b.status,a.noc_dt from  " + TableList.VT_NOC + " a left join " + TableList.VT_OWNER + " b on b.regn_no=a.regn_no and b.state_cd=a.state_cd and b.off_cd=a.off_cd where b.chasi_no = ? order by noc_dt desc limit 1";
        try {
            tmgr = new TransactionManager("getChasiNoExist");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, chassisNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                nocDobj = new NocDobj();
                nocDobj.setState_cd(rs.getString("state_cd"));
                nocDobj.setOff_cd(rs.getInt("off_cd"));
                nocDobj.setState_to(rs.getString("state_to"));
                nocDobj.setOff_to(rs.getInt("off_to"));
                nocDobj.setVt_owner_status(rs.getString("status"));
                nocDobj.setNoc_dt(rs.getDate("noc_dt"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return nocDobj;
    }

    public static NocDobj getNocEndorsementData(String regnNo, String chassiNo) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String query = "";
        NocDobj nocDobj = null;

        if (regnNo != null) {
            regnNo = regnNo.toUpperCase();
        }
        if (chassiNo != null) {
            chassiNo = chassiNo.toUpperCase();
        }
        try {
            tmgr = new TransactionManager("getNocEndorsementData");

            query = "select state_to,off_to, state_cd,off_cd from  " + TableList.VT_NOC_ENDORSEMENT
                    + " where regn_no = ? order by noc_dt desc limit 1";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, regnNo);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                nocDobj = new NocDobj();
                nocDobj.setState_to(rs.getString("state_to"));
                nocDobj.setOff_to(rs.getInt("off_to"));
                nocDobj.setState_cd(rs.getString("state_cd"));
                nocDobj.setOff_cd(rs.getInt("off_cd"));
            } else {
                query = "select state_to,off_to from  " + TableList.VT_NOC_ENDORSEMENT
                        + " where chasi_no  = ? order by noc_dt desc limit 1";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, chassiNo);
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    nocDobj = new NocDobj();
                    nocDobj.setState_to(rs.getString("state_to"));
                    nocDobj.setOff_to(rs.getInt("off_to"));
                }
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return nocDobj;
    }

    public static String getVehReassignData(String regnNo) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String query = "";
        String message = "";

        if (regnNo != null) {
            regnNo = regnNo.toUpperCase();
        }

        try {
            tmgr = new TransactionManager("getVehReassignData");

            query = "select old_regn_no,new_regn_no,b.descr AS state_name,c.off_name\n"
                    + " from " + TableList.VH_RE_ASSIGN + "  a \n"
                    + " LEFT JOIN tm_state b ON b.state_code = a.state_cd::bpchar\n"
                    + " LEFT JOIN tm_office c ON c.off_cd = a.off_cd AND c.state_cd = a.state_cd::bpchar\n"
                    + " where old_regn_no= ? ";

            ps = tmgr.prepareStatement(query);
            ps.setString(1, regnNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                message = "" + rs.getString("new_regn_no") + " Regn no already reassign in " + rs.getString("state_name") + " " + rs.getString("off_name") + " ";
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return message;
    }

    public static TaxBasedOnDobj getTaxBasedOnDetails(String stateCd, int offCd, String rcptNo) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String sql = null;
        TaxBasedOnDobj taxBasedOnDobj = null;

        try {
            tmgr = new TransactionManager("getTaxBasedOnDetails");

            sql = "SELECT * FROM " + TableList.VT_TAX_BASED_ON
                    + " WHERE state_cd=? and off_cd=? and rcpt_no=?";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            ps.setString(3, rcptNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                taxBasedOnDobj = new TaxBasedOnDobj();
                taxBasedOnDobj.setState_cd(rs.getString("state_cd"));
                taxBasedOnDobj.setOff_cd(rs.getInt("off_cd"));
                taxBasedOnDobj.setRegn_no(rs.getString("regn_no"));
                taxBasedOnDobj.setRcpt_no(rs.getString("rcpt_no"));
                taxBasedOnDobj.setPurchase_dt(rs.getDate("purchase_dt"));
                taxBasedOnDobj.setRegn_type(rs.getString("regn_type"));
                taxBasedOnDobj.setVh_class(rs.getInt("vh_class"));
                taxBasedOnDobj.setNo_cyl(rs.getInt("no_cyl"));
                taxBasedOnDobj.setHp(rs.getFloat("hp"));
                taxBasedOnDobj.setSeat_cap(rs.getInt("seat_cap"));
                taxBasedOnDobj.setStand_cap(rs.getInt("stand_cap"));
                taxBasedOnDobj.setSleeper_cap(rs.getInt("sleeper_cap"));
                taxBasedOnDobj.setUnld_wt(rs.getInt("unld_wt"));
                taxBasedOnDobj.setLd_wt(rs.getInt("ld_wt"));
                taxBasedOnDobj.setGcw(rs.getInt("gcw"));
                taxBasedOnDobj.setFuel(rs.getInt("fuel"));
                taxBasedOnDobj.setWheelbase(rs.getInt("wheelbase"));
                taxBasedOnDobj.setCubic_cap(rs.getFloat("cubic_cap"));
                taxBasedOnDobj.setFloor_area(rs.getFloat("floor_area"));
                taxBasedOnDobj.setAc_fitted(rs.getString("ac_fitted"));
                taxBasedOnDobj.setAudio_fitted(rs.getString("audio_fitted"));
                taxBasedOnDobj.setVideo_fitted(rs.getString("video_fitted"));
                taxBasedOnDobj.setVch_purchase_as(rs.getString("vch_purchase_as"));
                taxBasedOnDobj.setVch_catg(rs.getString("vch_catg"));
                taxBasedOnDobj.setSale_amt(rs.getInt("sale_amt"));
                taxBasedOnDobj.setLength(rs.getInt("length"));
                taxBasedOnDobj.setWidth(rs.getInt("width"));
                taxBasedOnDobj.setHeight(rs.getInt("height"));
                taxBasedOnDobj.setImported_vch(rs.getString("imported_vch"));
                taxBasedOnDobj.setOther_criteria(rs.getInt("other_criteria"));
                taxBasedOnDobj.setFin_yr_sale_amt(rs.getInt("fin_yr_sale_amt"));
                taxBasedOnDobj.setPmt_type(rs.getInt("pmt_type"));
                taxBasedOnDobj.setPmt_catg(rs.getInt("pmt_catg"));
                taxBasedOnDobj.setService_type(rs.getInt("service_type"));
                taxBasedOnDobj.setRoute_class(rs.getInt("route_class"));
                taxBasedOnDobj.setRoute_length(rs.getInt("route_length"));
                taxBasedOnDobj.setNo_of_trips(rs.getInt("no_of_trips"));
                taxBasedOnDobj.setDomain_cd(rs.getInt("domain_cd"));
                taxBasedOnDobj.setDistance_run_in_quarter(rs.getInt("distance_run_in_quarter"));
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem in Getting Details of Tax_Based_On");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return taxBasedOnDobj;
    }

    public static String getVahanPgiUrl(String Conn_Type) {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String vahan_url = "";

        try {
            tmgr = new TransactionManager("inside getVahanPgiUrl of ServerUtil");
            sql = "select conn_dblink from tm_dblink_list where conn_type = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Conn_Type);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                vahan_url = rs.getString("conn_dblink");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return vahan_url;
    }

    public static NocDobj getNocVerifiedData(String regnNo, String chassiNo) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String query = "";
        NocDobj nocVerifiedData = null;

        if (regnNo != null) {
            regnNo = regnNo.toUpperCase();
        }
        if (chassiNo != null) {
            chassiNo = chassiNo.toUpperCase();
        }
        try {
            tmgr = new TransactionManager("getNocEndorsementData");

            query = "select * from  " + TableList.VT_NOC_VERIFICATION
                    + " where regn_no = ? and state_cd=? and off_cd=? order by noc_dt,entered_on desc limit 1";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, regnNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                nocVerifiedData = new NocDobj();
                nocVerifiedData.setRegn_no(rs.getString("regn_no"));
                nocVerifiedData.setState_from(rs.getString("from_state_cd"));
                nocVerifiedData.setOff_from(rs.getInt("from_off_cd"));
                nocVerifiedData.setNoc_dt(rs.getDate("noc_dt"));
                nocVerifiedData.setNoc_no(rs.getString("noc_no"));
                nocVerifiedData.setNcrb_ref(rs.getString("ncrb_ref"));
            } else {
                query = "select * from  " + TableList.VT_NOC_VERIFICATION
                        + " where chasi_no  = ? and state_cd=? and off_cd=? order by noc_dt desc limit 1";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, chassiNo);
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    nocVerifiedData = new NocDobj();
                    nocVerifiedData.setRegn_no(rs.getString("regn_no"));
                    nocVerifiedData.setState_from(rs.getString("from_state_cd"));
                    nocVerifiedData.setOff_from(rs.getInt("from_off_cd"));
                    nocVerifiedData.setNoc_dt(rs.getDate("noc_dt"));
                    nocVerifiedData.setNoc_no(rs.getString("noc_no"));
                    nocVerifiedData.setNcrb_ref(rs.getString("ncrb_ref"));
                }
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return nocVerifiedData;
    }

    public static PermitHomeAuthDobj getPermitDetailsFromNp(String regn_no) throws VahanException {
        PermitHomeAuthDobj dobj_auth = null;
        PreparedStatement pstmt = null;
        TransactionManager tmgr = null;
        String sql = "";
        try {
            sql = "select * from getnationalpermitinfo (?)";
            tmgr = new TransactionManager("getPermitDetailsFromNp");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, regn_no);
            RowSet rowSet = tmgr.fetchDetachedRowSet();
            if (rowSet.next()) {
                dobj_auth = new PermitHomeAuthDobj();
                dobj_auth.setRegnNo(rowSet.getString("regn_no"));
                dobj_auth.setAuthFrom(rowSet.getDate("val_fr"));
                dobj_auth.setAuthUpto(rowSet.getDate("val_to"));
                dobj_auth.setAuthNo(rowSet.getString("pmt_no"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There is Some Problem Fatching National Permit Data");
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dobj_auth;
    }

    public static String getRegnNoWithSpace(String authRegnNo) {
        String np_regn_no = "";
        int regn_no_length = authRegnNo.length();
        StringBuilder s = new StringBuilder();
        s.append(authRegnNo);
        String digiPart = s.toString().substring(s.length() - 4);
        String seriPart = s.toString().substring(0, s.length() - 4);
        switch (regn_no_length) {
            case 10:
                np_regn_no = authRegnNo;
                break;
            case 9:
                np_regn_no = seriPart + " " + digiPart;
                break;
            case 8:
                np_regn_no = seriPart + " " + " " + digiPart;
                break;
            case 7:
                np_regn_no = seriPart + " " + " " + " " + digiPart;
                break;
            case 6:
                np_regn_no = seriPart + " " + " " + " " + " " + digiPart;
                break;
        }
        return np_regn_no;
    }

    /**
     * This Method is used to verify dealer with all RTO registering authority.
     *
     * @param userCode
     * @return true if dealer is authorized to register in any RTO within state
     * else false.
     */
    public static boolean validateDealerUserForAllOffice(long userCode) throws VahanException {
        boolean status = false;
        boolean stateAllow = false;
        boolean dealerAuth = false;
        try {
            String userCatg = ServerUtility.getUserCategory(userCode);
            TmConfigurationDobj tmConfigDobj = Util.getTmConfiguration();
            if (tmConfigDobj != null) {
                stateAllow = tmConfigDobj.isDealer_auth_for_all_office();
            }
            Home_Impl implObj = new Home_Impl();
            dealerAuth = implObj.getDealerAuthority(userCode);
            if (userCatg != null && userCatg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER) && stateAllow && dealerAuth) {
                status = true;
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong during validation of dealer.");
        }
        return status;
    }

    /**
     * @author Kartikey Singh This Method is used to verify dealer with all RTO
     * registering authority.
     *
     * @param userCode
     * @return true if dealer is authorized to register in any RTO within state
     * else false.
     */
    public static boolean validateDealerUserForAllOffice(long userCode, String userStateCode) throws VahanException {
        boolean status = false;
        boolean stateAllow = false;
        boolean dealerAuth = false;
        try {
            String userCatg = ServerUtility.getUserCategory(userCode);
            TmConfigurationDobj tmConfigDobj = Utility.getTmConfiguration(null, userStateCode);
            if (tmConfigDobj != null) {
                stateAllow = tmConfigDobj.isDealer_auth_for_all_office();
            }
            Home_Impl implObj = new Home_Impl();
            dealerAuth = implObj.getDealerAuthority(userCode);
            if (userCatg != null && userCatg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER) && stateAllow && dealerAuth) {
                status = true;
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong during validation of dealer.");
        }
        return status;
    }

    public static Owner_dobj getLdUnLdWtFromHomologation(String uniqueModelRefNo, int makerCode) throws VahanException {
        Owner_dobj owner_dobj = null;
        TransactionManager tmgr = null;

        try {
            tmgr = new TransactionManager("getLdUnLdWtFromHomologation");

            String sql = "select * from vm_model_homologation where unique_model_ref_no = ? and maker_code = ? ";

            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, uniqueModelRefNo.toUpperCase());
            ps.setInt(2, makerCode);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {
                owner_dobj = new Owner_dobj();
                owner_dobj.setUnld_wt(rs.getInt("unld_wt"));
                owner_dobj.setLd_wt(rs.getInt("gvw"));
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in getting homoUnLdWt information ");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return owner_dobj;
    }

    public static Owner_dobj getHomologationData(String applNo) throws VahanException {
        Owner_dobj owner_dobj = null;
        TransactionManager tmgr = null;

        try {
            tmgr = new TransactionManager("getHomologationData");

            String sql = "select * from " + TableList.VA_HOMO_DETAILS + " where appl_no = ? ";

            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo.toUpperCase());

            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                owner_dobj = new Owner_dobj();
                owner_dobj.setSale_amt(rs.getInt("sale_amt"));
                owner_dobj.setSeat_cap(rs.getInt("seat_cap"));
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in getting homo information ");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return owner_dobj;
    }

    /**
     * Converts the image(InputStream) to byte array.
     *
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static byte[] ImageToByte(InputStream file) throws FileNotFoundException, IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        for (int readNum; (readNum = file.read(buf)) != -1;) {
            bos.write(buf, 0, readNum);
        }
        byte[] bytes = bos.toByteArray();
        return bytes;
    }

    public static int maxDiffYear(Date maxDate, Date miniDate) {
        int difInDays = (int) (((maxDate).getTime() - (miniDate).getTime()) / ((86400000)));
        int year = difInDays / 365;
        year += 1;
        return year;
    }

    public TaxDobj getBalanceTaxDetails(String regnNo, String appl_no, int purCd, String stateCd, String selectedOption) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String sql = null;
        TaxDobj taxDobj = null;
        try {
            sql = "SELECT * from  vahan4.vt_refund_excess where regn_no=? and pur_cd=? and state_cd=? and (road_tax_appl_no is null and bal_tax_appl_no is null)";
            tmgr = new TransactionManager("getBalanceTaxDetails");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setInt(2, purCd);
            ps.setString(3, stateCd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                taxDobj = new TaxDobj();
                taxDobj.setPur_cd(rs.getInt("pur_cd"));
                taxDobj.setTax_from(rs.getDate("taxfrom"));
                taxDobj.setTax_upto(rs.getDate("taxupto"));
                taxDobj.setTax_amt(rs.getInt("balance_amt"));
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Getting Tax Details for the Registration No " + regnNo + ", Please Contact to the System Administrator.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return taxDobj;
    }

    public String getRegnNoForScrappedVehicle(String regn_no) throws Exception {
        String scrapped_regn_no = "";
        String tempregn = "";
        int regn_no_length = regn_no.length();
        StringBuilder s = new StringBuilder();
        s.append(regn_no);
        String egov_code = "" + getEgov_code(Util.getUserStateCode());
        if ("0".equalsIgnoreCase(egov_code)) {
            throw new VahanException("Error : Session Time Out ");
        }
        if (regn_no_length == 10 || regn_no_length <= 8) {
            if (egov_code.length() < 2) {
                egov_code = "0" + egov_code;
            }
        }
        if (Util.getUserStateCode().equals(s.toString().substring(0, 2))) {
            tempregn = s.toString().substring(2);
            scrapped_regn_no = "" + egov_code + tempregn;
        } else if (Util.getUserStateCode() == null ? s.toString().substring(0, 2) != null : !Util.getUserStateCode().equals(s.toString().substring(1, 2))) {
            tempregn = regn_no;
            if (regn_no_length <= 8) {
                tempregn = regn_no;
            } else if (regn_no_length == 10) {
                tempregn = s.toString().substring(2);
            }
            scrapped_regn_no = "" + egov_code + tempregn;
        }
        return scrapped_regn_no;
    }

    private int getEgov_code(String userStateCode) {
        int egov_code = 0;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            String sql = "select state_code,descr,egov_code from " + TableList.TM_STATE + " where state_code=?";
            tmgr = new TransactionManager("getEgov_code");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, userStateCode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                egov_code = rs.getInt("egov_code");
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return egov_code;
    }

    public static String getIpPath() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String contextPath = request.getContextPath();
        StringBuffer requestURL = request.getRequestURL();
        String ipPath = requestURL.substring(0, requestURL.indexOf(contextPath + "/")) + contextPath;
        return ipPath.replaceAll("http:", "https:");
    }

    /**
     * This method is used to check that secondPurCd Application will be
     * approved before firstPurCode Application.
     *
     * @param tmgr TransactionManager Object.
     * @param applNo Application Number to be checked.
     * @param firstPurCd Purpose Code whose transaction approved later.
     * @param secondPurCd Purpose Code whose transaction approved earlier.
     */
    public static boolean checkApprovalStatusOfAppls(TransactionManager tmgr, String applNo, int firstPurCd, int secondPurCd) throws SQLException, VahanException, Exception {
        boolean status = false;
        PreparedStatement ps;
        RowSet rs;
        String query = "select * from  " + TableList.VA_DETAILS + " where appl_no = ? and ? in "
                + "( select pur_cd from " + TableList.VA_DETAILS + " where appl_no = ? ) "
                + "and ?=( select pur_cd from " + TableList.VA_DETAILS + " where appl_no = ? and  pur_cd=? ) "
                + "and pur_cd=?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, applNo);
        ps.setInt(2, firstPurCd);
        ps.setString(3, applNo);
        ps.setInt(4, secondPurCd);
        ps.setString(5, applNo);
        ps.setInt(6, secondPurCd);
        ps.setInt(7, secondPurCd);
        rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            if (rs.getString("entry_status").equalsIgnoreCase("A")) {
                status = true;
            } else {
                throw new VahanException("Please Perform Approval of " + getTaxHead(secondPurCd) + " Application First.");
            }
        }
        return status;
    }

    public static VmSmartCardHsrpDobj getVmSmartCardHsrpParameters(String state_cd, int off_cd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        VmSmartCardHsrpDobj dobj = null;

        try {
            tmgr = new TransactionManagerReadOnly("getVmSmartCardHsrpParameters");
            sql = "SELECT * FROM " + TableList.VM_SMART_CARD_HSRP
                    + " WHERE state_cd = ? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new VmSmartCardHsrpDobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setSmart_card(rs.getBoolean("smart_card"));
                dobj.setHsrp(rs.getBoolean("hsrp"));
                dobj.setDay_begin(rs.getDate("day_begin"));
                dobj.setCash_counter_closed(rs.getBoolean("cash_counter_closed"));
                dobj.setLast_working_day(rs.getDate("last_working_day"));
                dobj.setNew_regn_not_allowed(rs.getString("new_regn_not_allowed"));
                dobj.setNew_regn_not_allowed_msg(rs.getString("new_regn_not_allowed_msg"));
                dobj.setOld_veh_hsrp(rs.getBoolean("old_veh_hsrp"));
                dobj.setPaper_rc(rs.getString("paper_rc"));
                dobj.setAutomaticFitness(rs.getString("automatic_fitness_formula"));
            }
            if (dobj == null && state_cd != null && !state_cd.isEmpty() && off_cd > 0) {
                throw new VahanException("Failed to Load Cash Counter/Smart Card/HSRP Configuration (" + state_cd + "-" + off_cd + ")");
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Failed to Load Cash Counter/Smart Card/HSRP Configuration (" + state_cd + "-" + off_cd + ").Please Contact to the System Administrator");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return dobj;
    }

    public static Status_dobj getLatestFlowHistoryByActionCd(String applNo, int actionCd, String state_cd, int off_cd) throws Exception {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        Status_dobj statusDobj = null;

        try {
            tmgr = new TransactionManager("getLatestFlowHistoryByActionCd");

            sql = "SELECT * FROM " + TableList.VHA_STATUS
                    + " WHERE appl_no=? and action_cd=? and state_cd=? and off_cd=? order by moved_on desc limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setInt(2, actionCd);
            ps.setString(3, state_cd);
            ps.setInt(4, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next())//found
            {
                statusDobj = new Status_dobj();
                statusDobj.setAppl_no(rs.getString("appl_no"));
                statusDobj.setPur_cd(rs.getInt("pur_cd"));
                statusDobj.setAction_cd(rs.getInt("action_cd"));
                statusDobj.setState_cd(rs.getString("state_cd"));
                statusDobj.setOff_cd(rs.getInt("off_cd"));
                statusDobj.setMoved_on(rs.getDate("moved_on"));
                statusDobj.setOp_dt(rs.getDate("op_dt"));
            }

        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return statusDobj;
    }

    public static boolean validateDealerNumGenAuth(TransactionManager tmgr, String userId, String stateCd, int offCd) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;
        boolean regnAuthMark = false;

        sql = "Select m.regn_mark_gen_by_dealer from " + TableList.TM_USER_PERMISSIONS + " p inner join " + TableList.VM_DEALER_MAST + " m  on p.dealer_cd = m.dealer_cd where p.user_cd = ? and p.state_cd = ? and p.assigned_office = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setLong(1, Long.parseLong(userId));
        ps.setString(2, stateCd);
        ps.setString(3, String.valueOf(offCd));
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            regnAuthMark = rs.getBoolean("regn_mark_gen_by_dealer");
        }
        return regnAuthMark;
    }

    public static void insertForDispatch(String appl_no, String regn_no, String state_cd, int off_cd, String user_cd, TransactionManager tmgr) throws Exception {
        String sql = null;
        sql = " insert into " + TableList.VA_DISPATCH
                + " select a.state_cd, a.off_cd,?, a.regn_no,'RC',a.owner_name,upper(COALESCE(a.c_add1, '') || ', ' || COALESCE(a.c_add2, '') || ', ' || COALESCE(a.c_add3, '') || ',' || COALESCE(c.descr, '') || '-' || COALESCE(d.descr, '') || '-' || a.c_pincode) as c_full_add,b.mobile_no,current_timestamp,? "
                + " from " + TableList.VT_OWNER + " a "
                + " left outer join " + TableList.VT_OWNER_IDENTIFICATION + " b on b.regn_no = a.regn_no and b.state_cd = a.state_cd and b.off_cd = a.off_cd "
                + " left outer join " + TableList.TM_DISTRICT + " c on c.dist_cd = a.c_district "
                + " left outer join " + TableList.TM_STATE + " d on d.state_code = a.c_state "
                + " where a.regn_no = ? and a.state_cd=? and a.off_cd=?";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, appl_no);
        ps.setString(2, user_cd);
        ps.setString(3, regn_no);
        ps.setString(4, state_cd);
        ps.setInt(5, off_cd);
        ServerUtility.validateQueryResult(tmgr, ps.executeUpdate(), ps);
    }

    public static void reInsertForDispatch(String regn_no, String state_cd, int off_cd, TransactionManager tmgr) throws Exception {
        String sql = null;
        sql = " insert into " + TableList.VA_DISPATCH
                + " select state_cd,off_cd,appl_no,regn_no,?,owner_name,c_address,mobile_no,current_timestamp,?"
                + " from " + TableList.VHA_DISPATCH
                + " where regn_no = ? and state_cd=? and off_cd=? order by moved_on desc limit 1";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, "Re Post RC");
        ps.setString(2, Util.getEmpCode());
        ps.setString(3, regn_no);
        ps.setString(4, state_cd);
        ps.setInt(5, off_cd);
        ServerUtility.validateQueryResult(tmgr, ps.executeUpdate(), ps);

        sql = " delete from  " + TableList.VHA_DISPATCH
                + " where regn_no = ? and state_cd=? and off_cd=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, regn_no);
        ps.setString(2, state_cd);
        ps.setInt(3, off_cd);
        ServerUtility.validateQueryResult(tmgr, ps.executeUpdate(), ps);
    }

    public static List<Integer> getVmvhClassAccTransportCatg(int classType, String transportCatg) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        List<Integer> vhClassList = null;
        try {
            tmgr = new TransactionManagerReadOnly("getVmvhClassAccTransportCatg");
            String sql = "SELECT vh_class from vm_vh_class where class_type = ? and transport_catg = ?;";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, classType);
            ps.setString(2, transportCatg);
            RowSet rs = tmgr.fetchDetachedRowSet();
            vhClassList = new ArrayList<Integer>();
            while (rs.next()) {
                vhClassList.add(rs.getInt("vh_class"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return vhClassList;
    }

    public static int getPincodeFee(String stateCode, int pincode) {
        PreparedStatement ps = null;
        RowSet rs;
        TransactionManager tmgr = null;
        String sql = null;
        List pinCode = new ArrayList();
        int fees = 0;
        try {
            tmgr = new TransactionManager("getPincodeList");
            sql = "Select * from " + TableList.VM_PINCODE + " Where state_cd = ?  and (pincode=? OR pincode=0) ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCode);
            ps.setInt(2, pincode);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (rs.getInt("pincode") == pincode) {
                    fees = rs.getInt("fees");
                    break;
                } else if (rs.getInt("pincode") == 0) {
                    fees = rs.getInt("fees");
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return fees;
    }

    public static Boolean isDispatchDetailsExist(String stateCode, int offCode, String regn_no) {
        PreparedStatement ps = null;
        RowSet rs;
        TransactionManager tmgr = null;
        String sql = null;
        boolean status = false;
        try {
            tmgr = new TransactionManager("isDispatchDetailsExist");
            sql = "Select regn_no from " + TableList.VA_DISPATCH + " Where state_cd = ? and off_cd= ? and regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCode);
            ps.setInt(2, offCode);
            ps.setString(3, regn_no);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                status = true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return status;
    }

    public static AuditRecoveryDobj getAuditRecordFromVA_AUDIT(String regn_no, String state_cd, int off_cd) throws VahanException {

        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        AuditRecoveryDobj auditRecoverDobj = null;

        try {

            if (regn_no != null) {
                regn_no = regn_no.toUpperCase();
            }

            tmgr = new TransactionManager("getAuditRecordFromVA_AUDIT");
            sql = "SELECT regn_no FROM " + TableList.VA_AUDIT_RECOVERY + " WHERE regn_no=? and state_cd=? and off_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                auditRecoverDobj = new AuditRecoveryDobj();
                auditRecoverDobj.setRegn_no(rs.getString("regn_no"));

            }

        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during Getting Details of Audit, Please Contact to the System Administrator");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during Getting Details of Audit, Please Contact to the System Administrator");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return auditRecoverDobj;
    }

    //forTaxInstallCheck
    public static TaxInstallCollectionDobj getTaxInstallCheck(String regn_no, String state_cd, int off_cd) throws VahanException {

        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        TaxInstallCollectionDobj taxinstallCheck = null;

        try {

            if (regn_no != null) {
                regn_no = regn_no.toUpperCase();
            }

            tmgr = new TransactionManager("getTaxInstallCheck");

            sql = "SELECT * FROM " + TableList.VT_TAX_INSTALLMENT_BRKUP + " WHERE REGN_NO = ?  AND pay_due_date <= current_timestamp AND rcpt_no IS NULL AND  state_cd=? and off_cd=? ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                taxinstallCheck = new TaxInstallCollectionDobj();
                taxinstallCheck.setRegnNo(rs.getString("regn_no"));

            }

        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during Getting Details of TaxInstallment, Please Contact to the System Administrator");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during Getting Details of TaxInstallment, Please Contact to the System Administrator");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return taxinstallCheck;
    }

    public static void validateOwnerDobj(Owner_dobj owner_dobj) throws VahanException {
        String msg = "";
        if (owner_dobj != null) {
            if (owner_dobj.getState_cd() == null) {
                msg = msg + ((msg.length() > 0) ? "," : "") + "State Code";
            }
            if (owner_dobj.getOff_cd() == 0) {
                msg = msg + ((msg.length() > 0) ? "," : "") + "Office Code";
            }
            if (owner_dobj.getPurchase_dt() == null) {
                msg = msg + ((msg.length() > 0) ? "," : "") + "Purchase Date";
            }
            if (owner_dobj.getOwner_name() == null) {
                msg = msg + ((msg.length() > 0) ? "," : "") + "Owner Name";
            }
            if (owner_dobj.getF_name() == null) {
                msg = msg + ((msg.length() > 0) ? "," : "") + "S/W/D of";
            }
            if (owner_dobj.getC_add1() == null) {
                msg = msg + ((msg.length() > 0) ? "," : "") + "Current Address-1";
            }
            if (owner_dobj.getC_district() == 0) {
                msg = msg + ((msg.length() > 0) ? "," : "") + "Current Address District";
            }
            if (owner_dobj.getC_state() == null) {
                msg = msg + ((msg.length() > 0) ? "," : "") + "Current Address State";
            }
            if (owner_dobj.getC_pincode() == 0) {
                msg = msg + ((msg.length() > 0) ? "," : "") + "Current Address Pincode";
            }
            if (owner_dobj.getP_district() == 0) {
                msg = msg + ((msg.length() > 0) ? "," : "") + "Permanent Address District";
            }
            if (owner_dobj.getP_state() == null) {
                msg = msg + ((msg.length() > 0) ? "," : "") + "Permanent Address State";
            }
            if (owner_dobj.getP_pincode() == 0) {
                msg = msg + ((msg.length() > 0) ? "," : "") + "Permanent Address Pincode";
            }
            if (owner_dobj.getOwner_cd() <= 0) {
                msg = msg + ((msg.length() > 0) ? "," : "") + "Ownership Type";
            }
            if (owner_dobj.getRegn_type() == null) {
                msg = msg + ((msg.length() > 0) ? "," : "") + "Registration Type";
            }
            if (owner_dobj.getVh_class() <= 0) {
                msg = msg + ((msg.length() > 0) ? "," : "") + "Vehicle Class";
            }
            if (owner_dobj.getChasi_no() == null) {
                msg = msg + ((msg.length() > 0) ? "," : "") + "Chassis No";
            }
            if (owner_dobj.getEng_no() == null) {
                msg = msg + ((msg.length() > 0) ? "," : "") + "Engine No";
            }
            if (owner_dobj.getMaker() <= 0) {
                msg = msg + ((msg.length() > 0) ? "," : "") + "Manufacturer Name";
            }
            if (owner_dobj.getMaker_model() == null) {
                msg = msg + ((msg.length() > 0) ? "," : "") + "Model Name";
            }
            if (owner_dobj.getAc_fitted() == null) {
                msg = msg + ((msg.length() > 0) ? "," : "") + "AC Fitted";
            }
            if (owner_dobj.getAudio_fitted() == null) {
                msg = msg + ((msg.length() > 0) ? "," : "") + "Audio Fitted";
            }
            if (owner_dobj.getVideo_fitted() == null) {
                msg = msg + ((msg.length() > 0) ? "," : "") + "Video Fitted";
            }
            if (owner_dobj.getVch_purchase_as() == null) {
                msg = msg + ((msg.length() > 0) ? "," : "") + "Vehicle Purchase As";
            }
            if (owner_dobj.getVch_catg() == null) {
                msg = msg + ((msg.length() > 0) ? "," : "") + "Vehicle Category";
            }
            if (owner_dobj.getDealer_cd() == null) {
                msg = msg + ((msg.length() > 0) ? "," : "") + "Dealer Name";
            }
            if (owner_dobj.getImported_vch() == null) {
                msg = msg + ((msg.length() > 0) ? "," : "") + "Imported Vehicle";
            }
        } else {
            msg = "Owner/Vehicle Details";
        }
        if (msg.length() > 0) {
            msg = msg + " can not be empty.";
            throw new VahanException(msg);
        }
    }

    public static Boolean getRegisteredDealerInfo(String stateCode, int offCode, String dealerCd) {
        PreparedStatement ps = null;
        RowSet rs;
        TransactionManager tmgr = null;
        String sql = null;
        boolean status = false;
        try {
            tmgr = new TransactionManager("getRegisteredDealerInfo");
            sql = "Select * from " + TableList.TM_USER_PERMISSIONS + " Where state_cd = ? and assigned_office = ? and dealer_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCode);
            ps.setString(2, String.valueOf(offCode));
            ps.setString(3, dealerCd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                status = true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return status;
    }

    public static String validateApplicationInward(String stateCd, int offCd, String dealerCd, int fmsGraceDays) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        String inwardSQL = "";
        int nonWorkDays = 0;
        String pendingAppls = "";
        try {
            tmgr = new TransactionManagerReadOnly("validateApplicationInward");
            inwardSQL = " select " + fmsGraceDays + " + " + fmsGraceDays + "-count(1) as non_work_days  from " + TableList.THM_OFFICE_CONFIGURATION + " where state_cd = ? and off_cd = ? and day_begin::date >= CURRENT_DATE - interval '" + fmsGraceDays + " days' ";
            PreparedStatement psDays = tmgr.prepareStatement(inwardSQL);
            int i = 1;
            psDays.setString(i++, stateCd);
            psDays.setInt(i++, offCd);
            RowSet rsWorkDays = tmgr.fetchDetachedRowSet_No_release();
            if (rsWorkDays.next()) {
                nonWorkDays = rsWorkDays.getInt("non_work_days");
            }
            inwardSQL = "select string_agg(DISTINCT o.appl_no || '['|| to_char(vrc.rcpt_dt,'dd-Mon-yyyy') || ']', ',') as appl_no\n"
                    + " from " + TableList.VPH_RCPT_CART + " vrc \n"
                    + " inner join " + TableList.VA_STATUS + " s on s.appl_no = vrc.appl_no and s.state_cd = vrc.state_cd \n"
                    + " inner join " + TableList.TM_USER_PERMISSIONS + "  tmc on tmc.user_cd = vrc.user_cd and vrc.state_cd =tmc.state_cd \n"
                    + " inner join " + TableList.VA_OWNER + " o on vrc.appl_no = o.appl_no and vrc.state_cd=o.state_cd and o.dealer_cd=? and norms in (15,17,19,20,21,22,23,99) \n"
                    + " left join " + TableList.VT_FMS_SUBMIT_FILE_DTLS + "  fmsd on fmsd.file_no=o.appl_no and fmsd.state_cd=o.state_cd \n"
                    + " where vrc.rcpt_dt <= (current_date - interval '" + nonWorkDays + " days')+'23:59:59.000' and tmc.dealer_cd=? and s.pur_cd in (?, ?) "
                    + " and vrc.state_cd=? and fmsd.file_no is null";

            PreparedStatement ps = tmgr.prepareStatement(inwardSQL);
            i = 1;
            ps.setString(i++, dealerCd);
            ps.setString(i++, dealerCd);
            ps.setInt(i++, TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE);
            ps.setInt(i++, TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE);
            ps.setString(i++, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                return rs.getString("appl_no");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in getting physical files details.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return pendingAppls;
    }

    public static List fmsApplicationDetails(TransactionManagerReadOnly tmgr, String applNoList, String stateCd, int offCd, String dealerCd) throws VahanException {
        List<String> fngList = null;
        try {
            int i = 1;
            String inwardSQL = "select application_no from " + TableList.VT_FMS_SUBMIT_FILE_DTLS + " where off_cd = ? and state_cd = ? and dealer_cd  = ? and application_no IN (" + applNoList + ")  ";
            PreparedStatement psFms = tmgr.prepareStatement(inwardSQL);
            psFms.setInt(i++, offCd);
            psFms.setString(i++, stateCd);
            psFms.setString(i++, dealerCd);
            RowSet rs1 = tmgr.fetchDetachedRowSet();
            fngList = new ArrayList<>();
            while (rs1.next()) {
                fngList.add(rs1.getString("application_no"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in getting physical files details.");
        }
        return fngList;
    }

    public static List fMSVerification(String applNoList, String stateCd, int offCd, String dealerCd, int norms) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        List<String> fmsFiles = new ArrayList();
        try {
            if ("DL".equals(stateCd) && norms == 4) {
                fmsFiles.add("");
                return fmsFiles;
            }
            tmgr = new TransactionManagerReadOnly("fMSVerification()");
            fmsFiles = fmsApplicationDetails(tmgr, "'" + applNoList + "'", stateCd, offCd, dealerCd);
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in getting physical files details.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return fmsFiles;
    }

    public static boolean checkApplRegnDateForFms(String applNo, String stateCd, int offCd, String dealerCd) throws VahanException {
        TransactionManager tmgr = null;
        boolean isvalid = false;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManager("checkApplRegnDateForFms()");
            String selectSql = "select * from " + TableList.VA_DETAILS + " where state_cd = ? and off_cd = ? and appl_no = ? "
                    + " and appl_dt < (select send_date from " + TableList.VT_FMS_SUBMIT_FILE_DTLS + " where state_cd = ? and off_cd = ? "
                    + " and dealer_cd = ? order by  send_date asc limit 1)";
            int i = 1;
            ps = tmgr.prepareStatement(selectSql);
            ps.setString(i++, stateCd);
            ps.setInt(i++, offCd);
            ps.setString(i++, applNo);
            ps.setString(i++, stateCd);
            ps.setInt(i++, offCd);
            ps.setString(i++, dealerCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isvalid = true;
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in getting FMS file submit date.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return isvalid;
    }

    public static void revertBackInOnlinePayment(TransactionManager tmgr, String applNo, String userCd) throws VahanException {
        try {
            String sql = "INSERT INTO " + TableList.VP_RCPT_CART_CANCEL + "("
                    + " cancel_dt, cancel_by,state_cd, off_cd, user_cd, appl_no, pur_cd, period_mode, period_from,"
                    + " period_upto, amount, exempted, rebate, surcharge, penalty, interest, "
                    + " transaction_no, rcpt_no, rcpt_dt, pmt_type, pmt_catg, service_type,"
                    + " route_class, route_length, no_of_trips, domain_cd, distance_run_in_quarter, "
                    + " op_dt,no_adv_units,tax1,tax2,prv_adjustment)"
                    + " SELECT  current_timestamp,?,state_cd, off_cd, user_cd, appl_no, pur_cd, period_mode, period_from, "
                    + " period_upto, amount, exempted, rebate, surcharge, penalty, interest, "
                    + " transaction_no, rcpt_no, rcpt_dt, pmt_type, pmt_catg, service_type, "
                    + " route_class, route_length, no_of_trips, domain_cd, distance_run_in_quarter, "
                    + " op_dt,no_adv_units,tax1,tax2,prv_adjustment"
                    + " FROM " + TableList.VP_RCPT_CART + " where user_cd = ? and appl_no = ? and transaction_no IS NULL";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, userCd);
            ps.setLong(2, Long.parseLong(userCd));
            ps.setString(3, applNo);
            ServerUtility.validateQueryResult(tmgr, ps.executeUpdate(), ps);

            sql = " delete from " + TableList.VP_RCPT_CART + " where user_cd = ? and appl_no = ? and transaction_no IS NULL";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(userCd));
            ps.setString(2, applNo);
            ServerUtility.validateQueryResult(tmgr, ps.executeUpdate(), ps);

            sql = "INSERT INTO " + TableList.VP_CART_TAX_BREAKUP_CANCEL + "("
                    + " cancel_dt, cancel_by,state_cd, off_cd, appl_no, sr_no, tax_from, tax_upto, pur_cd, "
                    + " prv_adjustment, tax, exempted, rebate, surcharge, penalty, interest, "
                    + " tax1, tax2, op_dt )"
                    + " SELECT current_timestamp,?,state_cd, off_cd, appl_no, sr_no, tax_from, tax_upto, pur_cd, "
                    + " prv_adjustment, tax, exempted, rebate, surcharge, penalty, interest, "
                    + " tax1, tax2, op_dt"
                    + " FROM " + TableList.VP_CART_TAX_BREAKUP + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, userCd);
            ps.setString(2, applNo);
            ps.executeUpdate();

            sql = "delete from " + TableList.VP_CART_TAX_BREAKUP + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.executeUpdate();

            sql = "INSERT INTO " + TableList.VP_CART_FEE_BREAKUP_CANCEL + "("
                    + " cancel_dt, cancel_by, state_cd, off_cd, appl_no, sr_no, fee_from, "
                    + " fee_upto, pur_cd, fee, fine, exempted, rebate, surcharge, interest,prv_adjustment, op_dt)"
                    + " SELECT current_timestamp,?,state_cd, off_cd, appl_no, sr_no, fee_from, fee_upto, pur_cd,  "
                    + " fee, fine, exempted, rebate, surcharge, interest, prv_adjustment,op_dt "
                    + " FROM " + TableList.VP_CART_FEE_BREAKUP + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, userCd);
            ps.setString(2, applNo);
            ps.executeUpdate();

            sql = "delete from " + TableList.VP_CART_FEE_BREAKUP + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.executeUpdate();
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in Revert Back of Online Payment");
        }
    }

    public static String[] checkDealerAuthForAllOff(String stateCd, String dealerCd) {
        String[] dealerList = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("checkDealerAuthForAllOff");
            String sql = "select distinct m.dealer_cd,m.dealer_name,m.regn_mark_gen_by_dealer,"
                    + " case when (up.all_office_auth is null )  then false else up.all_office_auth end "
                    + " from  vm_dealer_mast m left join tm_user_permissions up "
                    + " on m.dealer_cd = up.dealer_cd where m.state_cd = ? and m.dealer_cd = ? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setString(2, dealerCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dealerList = new String[4];
                dealerList[0] = rs.getString("dealer_cd");
                dealerList[1] = rs.getString("dealer_name");
                dealerList[2] = String.valueOf(rs.getBoolean("regn_mark_gen_by_dealer"));
                dealerList[3] = String.valueOf(rs.getBoolean("all_office_auth"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return dealerList;
    }

    public String genOTP(String mobile_no) throws VahanException {
        String OTP = null;
        try {
            if (CommonUtils.isNullOrBlank(mobile_no) || mobile_no.length() < 10) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "!Alert", "Please enter the valid mobile number.");
                PrimeFaces.current().dialog().showMessageDynamic(message);
                return null;
            }
            OTP = TOTP.getOTPTimeStamp();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Otp did not Generate");
        }
        return OTP;
    }

    public static void sendOTP(String mobileNo, String message, String state_cd) throws Exception {
        try {
            OTPSMSSERVER sm = new OTPSMSSERVER(mobileNo, message, state_cd);
            sm.start();
        } catch (Exception ex) {
            throw ex;
        }
    }

    public static void sendMail(String recipients[], String subject, String message) throws Exception {
        try {
            MailSenderThread mail = new MailSenderThread(recipients, subject, message);
            mail.sendMail();
        } catch (Exception ex) {
            throw ex;
        }
    }

    public static TmConfigurationDispatchDobj getTmConfigurationDispatchParameters(String state_cd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        TmConfigurationDispatchDobj dobj = null;

        try {
            tmgr = new TransactionManager("getTmConfigurationParameters");
            sql = "SELECT * FROM " + TableList.TM_CONFIGURATION_DISPATCH
                    + " WHERE state_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);

            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new TmConfigurationDispatchDobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setIs_speed_post_series(rs.getBoolean("is_speed_post_series"));
                dobj.setIs_show_all_pending_records(rs.getBoolean("is_show_all_pending_records"));
                dobj.setIs_envelop_print(rs.getBoolean("is_envelop_print"));
                dobj.setIs_sticker_print(rs.getBoolean("is_sticker_print"));
                dobj.setIs_search_by_regn_no(rs.getBoolean("is_search_by_regn_no"));
                dobj.setIs_verify_for_hsrp(rs.getBoolean("is_verify_for_hsrp"));
                dobj.setIs_sendSMS_owner(rs.getBoolean("is_sendSMS_owner"));
                dobj.setIs_barcode_mandatory(rs.getBoolean("is_barcode_mandatory"));
                dobj.setIs_rcdispatch_userwise(rs.getBoolean("is_rcdispatch_userwise"));
                dobj.setIs_rcdispatch_letter(rs.getBoolean("is_rcdispatch_letter"));
                dobj.setIs_edit_on_all_pending_records(rs.getBoolean("is_edit_on_all_pending_records"));
                dobj.setIs_rcdispatch_byhand(rs.getBoolean("is_rcdispatch_byhand"));
                dobj.setRevert_rcdispatch_byhand(rs.getBoolean("revert_rcdispatch_byhand"));
                dobj.setSms_dispatch_return(rs.getString("sms_dispatch_return"));
                dobj.setIs_dispatch_address(rs.getBoolean("is_dispatch_address"));
                dobj.setBy_hand_class_type(rs.getInt("by_hand_class_type"));
            }
            if (dobj == null) {
                throw new VahanException("Problem in getting the Dispatch Configuration Details, Please try after sometime.");
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting the Dispatch Configuration Details, Please try after sometime.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dobj;
    }

    public static boolean verifyForPostalFee(String state_cd, int off_cd, String appl_no, TransactionManager tmgr) throws Exception {
        boolean isPostalFee = false;
        String sql = "SELECT a.* FROM " + TableList.VT_FEE + " a, " + TableList.VP_APPL_RCPT_MAPPING + " b WHERE b.appl_no=? and b.state_cd=a.state_cd and b.off_cd=a.off_cd and b.rcpt_no=a.rcpt_no and a.pur_cd = 200 ";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, appl_no);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            isPostalFee = true;
        }
        return isPostalFee;
    }

    public static boolean checkUserMobileVerifiedOrNot(Long user_cd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        TmConfigurationDispatchDobj dobj = null;
        RowSet rs;
        boolean status = false;
        try {
            sql = "Select mobile_verify from " + TableList.TM_USER_DETAILS_VERIFICATION + " where user_cd = ?";
            tmgr = new TransactionManager("checkUserMobileVerifiedOrNot");
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, user_cd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                status = rs.getBoolean("mobile_verify");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
        return status;
    }

    public static Object getDLOrLLDetails(String licenceNumber, String licenceType, String DOB) throws VahanException {
        HttpURLConnection conn = null;
        Unmarshaller unmarshaller = null;
        Object response = null;
        JAXBContext jc = null;
        URL url = null;
        try {
            byte[] llNumber = Base64.encodeBase64(licenceNumber.getBytes());
            if (licenceType.equals("LL")) {
                String olaCode = licenceNumber.replaceAll("[-,*&%$ ./]", "").substring(0, 4);
                byte[] olacode = Base64.encodeBase64(olaCode.getBytes());
                url = new URL(ServerUtility.getVahanPgiUrl(TableConstants.LEARNING_LICENSE_URL) + new String(llNumber) + "/" + new String(olacode) + "");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/xml");
                jc = JAXBContext.newInstance(RetLLDetails.class);
                unmarshaller = jc.createUnmarshaller();
                response = (RetLLDetails) unmarshaller.unmarshal(conn.getInputStream());
            } else {
                String usesr = "DelhTrans";
                byte[] user = Base64.encodeBase64(usesr.getBytes());
                String password = "d9fe1613399b0883e4ecf3f710e18317";
                byte[] ps = Base64.encodeBase64(password.getBytes());
                byte[] dob = Base64.encodeBase64(DOB.getBytes());
                url = new URL(ServerUtility.getVahanPgiUrl(TableConstants.DRIVING_LICENSE_URL) + new String(llNumber) + "/" + new String(dob) + "/" + new String(user) + "/" + new String(ps) + "");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept", "application/xml");
                jc = JAXBContext.newInstance(ResponceList.class);
                unmarshaller = jc.createUnmarshaller();
                response = (ResponceList) unmarshaller.unmarshal(conn.getInputStream());
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (IOException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (JAXBException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return response;
    }

    public static String getVehicleClassDescr(int vchClass) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String vhClassDescr = null;
        try {
            tmgr = new TransactionManagerReadOnly("getVehicleClassDescr");
            String sql = "SELECT descr from vm_vh_class where vh_class = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, vchClass);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                vhClassDescr = rs.getString("descr");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting details");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return vhClassDescr;
    }

    public static boolean checkDLIsExist(String dlNo) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        boolean flag = false;
        try {
            tmgr = new TransactionManagerReadOnly("checkDLIsExist");
            String sql = "SELECT * from " + TableList.VP_BANK_SUBSIDY + " where ll_dl_no = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dlNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                flag = true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting DL details");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public static boolean checkNextSeriesGen(String applNo, int purCd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        boolean flag = false;
        try {
            tmgr = new TransactionManagerReadOnly("checkNextSeriesGen");
            String sql = "SELECT * from " + TableList.VA_NUM_GEN_PERMITDETAILS + " where appl_no = ?  and pur_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setInt(2, purCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                flag = rs.getBoolean("num_generation");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
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
        return flag;
    }

    public static Timestamp getDateToTimesTamp(String strDt) throws VahanException {
        // SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        // the Date format is modified to 24 hrs format :
        // previously set to 12 Hrs format.
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
        Timestamp timeStampDate = null;
        Date date = null;
        try {
            date = sdf1.parse(strDt);
        } catch (ParseException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        if (date == null) {
            sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
            try {
                date = sdf1.parse(strDt);
            } catch (ParseException ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);

            }
        }
        if (date == null) {
            sdf1 = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
            try {
                date = sdf1.parse(strDt);
            } catch (ParseException ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);

            }
        }
        if (date == null) {

            sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            try {
                date = sdf1.parse(strDt);
            } catch (ParseException ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);

            }
        }
        if (date == null) {
            sdf1 = new SimpleDateFormat("dd-MM-yyyy");
            try {
                date = sdf1.parse(strDt);
            } catch (ParseException ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                throw new VahanException("Problem while parsing Date");
            }
        }
        if (date != null) {
            timeStampDate = new Timestamp(date.getTime());
        }
        return timeStampDate;
    }

    public static String getYYYYMMDD_To_DDMMMYYYY(String strdt) throws VahanException {
        String outputString = "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
            Date date = inputFormat.parse(strdt);
            // Format date into output format
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            outputString = outputFormat.format(date);

        } catch (ParseException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem while parsing Date");
        }
        return outputString;
    }

    public static String printReports(String printType, String applNo, int purCd, String regnNo) throws VahanException {
        String returnUrl = null;
        try {
            if (printType != null && printType.equals("form21")) {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("printType", "FORM21");
                returnUrl = "/ui/dealer/form_form21Report.xhtml?faces-redirect=true";
            } else if (printType != null && printType.equals("form20")) {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("printType", "FORM20");
                returnUrl = "/ui/dealer/form_form20Report.xhtml?faces-redirect=true";
            } else if (printType != null && printType.equals("disclaimer")) {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("category", "newRegisteredVehicles");
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("reportEntry", "reportFormat");
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("printType", printType);
                return "OwnerDisclaimerReport";
            } else if (printType != null && printType.equals("inspCertificate")) {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("printType", "inspCert");
                returnUrl = "/ui/dealer/form_inspection_certificate.xhtml?faces-redirect=true";
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return returnUrl;
    }

    public static List getOfficeBasedOnType(String state_cd, List<Integer> offTypeCd) throws VahanException {
        List dataList = new ArrayList<>();
        TransactionManager tmgr = null;
        RowSet rs;
        PreparedStatement ps = null;
        try {
            String sql = "Select off_cd,off_name from " + TableList.TM_OFFICE + " where state_cd = ? and "
                    + "off_type_cd in " + offTypeCd.toString().replace('[', '(').replace(']', ')') + "";

            tmgr = new TransactionManager("getOfficeBasedOnType");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dataList.add(new SelectItem(rs.getInt("off_cd"), rs.getString("off_name")));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Getting Offices Assigned for State. Please Contact to the System Administrator.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                throw new VahanException("Error in Getting Offices Assigned for State. Please Contact to the System Administrator.");
            }
        }
        return dataList;
    }

    public static String getMailIDOfStateAndOfficeAdmin(TransactionManager tmgr, String StateCd, String user_catg, Integer offCd) {
        String email_id = "";
        PreparedStatement psmt = null;
        try {
            String strSQL = "";
            strSQL = "select email_id from tm_user_info where state_cd=? and user_catg =?";
            if (offCd != null) {
                strSQL = strSQL + " and off_cd=?";
            }
            psmt = tmgr.prepareStatement(strSQL);
            psmt.setString(1, StateCd);
            psmt.setString(2, user_catg);
            if (offCd != null) {
                psmt.setInt(3, offCd);
            }
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                email_id = rs.getString("email_id");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return email_id;
    }

    public static String[] getDealerAdminMailId(String applNo, String stateCd, int offCd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String[] dealerData = new String[2];
        try {
            tmgr = new TransactionManagerReadOnly("getDealerAdminMailId");
            String sql = "select email_id, mobile_no from " + TableList.TM_USER_INFO + " where state_cd = ? and user_catg = '" + TableConstants.USER_CATG_DEALER_ADMIN + "' and "
                    + " user_cd in (select user_cd from " + TableList.TM_USER_PERMISSIONS + " p inner join " + TableList.VA_OWNER + " o on  o.dealer_cd = p.dealer_cd "
                    + " and p.state_cd = ? where o.state_cd = ? and o.appl_no = ? )";
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, stateCd);
            ps.setString(i++, stateCd);
            ps.setString(i++, stateCd);
            ps.setString(i++, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dealerData[0] = rs.getString("email_id");
                dealerData[1] = rs.getString("mobile_no");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
        return dealerData;
    }

    public static boolean cancelOnlinePayment(String applNo, String payType) throws VahanException {
        boolean flag = false;
        String password = "";
        long user_cd = 0;
        try {
            Object[] obj = new FeeImpl().getUserIDAndPassword(applNo);
            if (obj != null && obj.length > 0) {
                password = (String) obj[0];
                user_cd = (long) obj[1];
                if (CommonUtils.isNullOrBlank(new OnlinePaymentImpl().getTransactionNumber(applNo))) {
                    flag = new OnlinePaymentImpl().getPaymentRevertBack(user_cd + "", applNo, payType);
                } else {
                    throw new VahanException("Payment has been initiated, you can not Cancel Online Payment");
                }
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong + " to cancel online payment.");
        }
        return flag;
    }

    //for get user Status 
    public static String getForgetPasswordStatus(String user_id) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String userStatus = null;
        try {
            tmgr = new TransactionManagerReadOnly("getForgetPasswordStatus");
            String sql = "SELECT forget_password from " + TableList.TM_USER_INFO + " where user_id = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, user_id);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                userStatus = rs.getString("forget_password");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
        return userStatus;
    }

    public static void updateNPAuthorizationDetails(String regn_no, String appl_no, TransactionManager tmgr, int pur_cd) throws VahanException {
        PreparedStatement pstmt = null;
        String sql = "";
        ResultSet rs = null;
        String pmt_no = "", auth_no = "";
        int pmt_type = 0, pmt_catg = 0;
        Date valid_from = null, valid_upto = null, auth_fr = null, auth_to = null;
        try {
            sql = "Select a.pmt_type,a.pmt_catg,a.pmt_no,a.valid_from,a.valid_upto, b.auth_no,b.auth_fr,b.auth_to from " + TableList.VT_PERMIT + " a"
                    + " inner join " + TableList.VT_PERMIT_HOME_AUTH + " b on b.pmt_no= a.pmt_no and b.regn_no= a.regn_no where a.state_cd = ? and a.regn_no = ? and a.pmt_type=?";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, Util.getUserStateCode());
            pstmt.setString(2, regn_no);
            pstmt.setInt(3, Integer.parseInt(TableConstants.NATIONAL_PERMIT));
            RowSet rowSet1 = tmgr.fetchDetachedRowSet_No_release();
            if (rowSet1.next()) {
                pmt_no = rowSet1.getString("pmt_no");
                valid_from = rowSet1.getDate("valid_from");
                valid_upto = rowSet1.getDate("valid_upto");
                auth_no = rowSet1.getString("auth_no");
                auth_fr = rowSet1.getDate("auth_fr");
                auth_to = rowSet1.getDate("auth_to");
                pmt_type = rowSet1.getInt("pmt_type");
                pmt_catg = rowSet1.getInt("pmt_catg");

            }
            updateNPAuthDetails(pur_cd, appl_no, regn_no, pmt_no, valid_from, valid_upto, auth_no, auth_fr, auth_to, pmt_type, pmt_catg, tmgr);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There is Some Problem Fatching National Permit Data");
        }
    }

    public static boolean updateNPAuthDetails(int pur_cd, String appl_no, String v4_regno, String pmtno, Date pmt_from, Date pmt_upto, String auth_no, Date auth_from, Date auth_upto, int pmt_type, int pmt_catg, TransactionManager tmgr)
            throws VahanException, DateUtilsException {
        PreparedStatement pstmt = null;
        boolean flag = true;
        String sql = "", npverifystatus = "N";;
        ResultSet rs = null;
        try {
            sql = "Select * from permit.va_np_detail where regn_no = ? and appl_no=?";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, v4_regno);
            pstmt.setString(2, appl_no);
            RowSet rowSet = tmgr.fetchDetachedRowSet_No_release();
            if (rowSet.next()) {
                sql = "update permit.va_np_detail set pmt_no=?,valid_from=?,valid_upto=?,auth_no=?,auth_from=?,auth_upto=?,pmtapprovestatus=? WHERE REGN_NO =? and appl_no=?";
                pstmt = tmgr.prepareStatement(sql);
                pstmt.setString(1, pmtno);
                pstmt.setDate(2, new java.sql.Date(pmt_from.getTime()));
                pstmt.setDate(3, new java.sql.Date(pmt_upto.getTime()));
                pstmt.setString(4, auth_no);
                pstmt.setDate(5, new java.sql.Date(auth_from.getTime()));
                pstmt.setDate(6, new java.sql.Date(auth_upto.getTime()));
                pstmt.setString(7, "A");
                pstmt.setString(8, v4_regno);
                pstmt.setString(9, appl_no);
                if (pstmt.executeUpdate() > 0 && pur_cd == TableConstants.VM_PMT_FRESH_PUR_CD) {
                    sql = "select * from  vahan4.update_nationalpermit_info_to_np_portal(?,?,?,?,?,?,?)";
                    pstmt = tmgr.prepareStatement(sql);
                    pstmt.setString(1, v4_regno);
                    pstmt.setString(2, pmtno);
                    pstmt.setDate(3, new java.sql.Date(pmt_from.getTime()));
                    pstmt.setDate(4, new java.sql.Date(pmt_upto.getTime()));
                    pstmt.setString(5, appl_no);
                    pstmt.setDate(6, new java.sql.Date(auth_from.getTime()));
                    pstmt.setDate(7, new java.sql.Date(auth_upto.getTime()));
                    RowSet rowSet1 = tmgr.fetchDetachedRowSet_No_release();
                    if (rowSet1.next()) {
                    }
                }
                if (rowSet.getString("npverifystatus").equalsIgnoreCase("A") && pur_cd == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD) {
                    vatovhNPAuthData(appl_no, tmgr);
                }
            } else {
                PermitHomeAuthDobj getNpDobj = getPermitDetailsFromNp(v4_regno);
                if (getNpDobj != null && auth_upto != null) {
                    long dayDiff = 0;
                    if (auth_upto.after(getNpDobj.getAuthUpto())) {
                        dayDiff = DateUtils.getDate1MinusDate2_Days(getNpDobj.getAuthUpto(), auth_upto);
                    } else {
                        dayDiff = DateUtils.getDate1MinusDate2_Days(auth_upto, getNpDobj.getAuthUpto());
                    }
                    if (dayDiff <= 30) {
                        npverifystatus = "A";
                    }
                }
                String query = " INSERT INTO permit.va_np_detail(state_cd, off_cd, appl_no, pmt_no, regn_no, valid_from,valid_upto,auth_no,auth_from,auth_upto,pur_cd,permit_type,permit_catg,paymentstatus,npverifystatus,pmtapprovestatus) \n"
                        + " VALUES (?, ?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
                pstmt = tmgr.prepareStatement(query);

                int m = 1;
                pstmt.setString(m++, Util.getUserStateCode());
                pstmt.setInt(m++, Util.getUserSeatOffCode());
                pstmt.setString(m++, appl_no);
                pstmt.setString(m++, pmtno);
                pstmt.setString(m++, v4_regno);
                pstmt.setDate(m++, new java.sql.Date(pmt_from.getTime()));
                pstmt.setDate(m++, new java.sql.Date(pmt_upto.getTime()));
                pstmt.setString(m++, auth_no);
                pstmt.setDate(m++, new java.sql.Date(auth_from.getTime()));
                pstmt.setDate(m++, new java.sql.Date(auth_upto.getTime()));
                pstmt.setInt(m++, pur_cd);
                pstmt.setInt(m++, pmt_type);
                pstmt.setInt(m++, pmt_catg);
                pstmt.setString(m++, "Y");
                pstmt.setString(m++, npverifystatus);
                pstmt.setString(m++, "A");
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There is Some Problem Fatching National Permit Data");
        }
        return flag;
    }

    public static PermitHomeAuthDobj getNPAuthDetailsAtPrint(String regn_no, String appl_no, String state_cd)
            throws VahanException {
        PreparedStatement pstmt = null;
        TransactionManager tmgr = null;
        PermitHomeAuthDobj dobj = null;
        String sql = "";
        // ResultSet rs = null;
        try {
            if (getPurCdFromVaDetails(state_cd, appl_no)) {
                sql = "Select * from permit.va_np_detail where regn_no = ? and appl_no=?";
                tmgr = new TransactionManager("getNPAuthDetailsAtPrint");
                pstmt = tmgr.prepareStatement(sql);
                pstmt.setString(1, regn_no.toUpperCase());
                pstmt.setString(2, appl_no);
                RowSet rowSet = tmgr.fetchDetachedRowSet();
                if (rowSet.next()) {
                    dobj = new PermitHomeAuthDobj();
                    dobj.setPmtNo(rowSet.getString("pmt_no"));
                    dobj.setAuthUpto(rowSet.getDate("auth_upto"));
                    dobj.setAuthNo(rowSet.getString("auth_no"));
                    dobj.setNpVerifyStatus(rowSet.getString("npverifystatus"));
                }
            }
        } catch (SQLException e) {
            dobj = null;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There is Some Problem Fatching National Permit Data");
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dobj;
    }

    public static void vatovhNPAuthData(String applNo, TransactionManager tmgr) throws VahanException, Exception {
        String sql = null;
        PreparedStatement ps = null;
        RowSet rs;
        try {
            sql = "select * from permit.va_np_detail WHERE state_cd =? and appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, applNo);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                sql = "Insert into permit.vha_np_detail (select current_timestamp,?,* from permit.va_np_detail WHERE state_cd =? and appl_no=?)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, Util.getUserStateCode());
                ps.setString(3, applNo);
                ps.executeUpdate();
                ps = null;
                sql = "Delete from permit.va_np_detail WHERE state_cd =? and appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setString(2, applNo);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting va_np_detail. Please contact Administrator!!!");
        }
    }

    /*
     * @author Kartikey Singh
     */
    public static void vatovhNPAuthData(String applNo, TransactionManager tmgr, String stateCode, String empCode) throws VahanException, Exception {
        String sql = null;
        PreparedStatement ps = null;
        RowSet rs;
        try {
            sql = "select * from permit.va_np_detail WHERE state_cd =? and appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCode);
            ps.setString(2, applNo);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                sql = "Insert into permit.vha_np_detail (select current_timestamp,?,* from permit.va_np_detail WHERE state_cd =? and appl_no=?)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, empCode);
                ps.setString(2, stateCode);
                ps.setString(3, applNo);
                ps.executeUpdate();
                ps = null;
                sql = "Delete from permit.va_np_detail WHERE state_cd =? and appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCode);
                ps.setString(2, applNo);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting va_np_detail. Please contact Administrator!!!");
        }
    }

    public static boolean getPurCdFromVaDetails(String state_cd, String appl_no) {
        boolean pmtPurpose = false;
        TransactionManager tmgr = null;
        try {
            String SQL = "select pur_cd from " + TableList.VA_DETAILS + " where state_cd=? and appl_no=?";
            tmgr = new TransactionManager("getPurCdFromVaDetails");
            PreparedStatement ps = tmgr.prepareStatement(SQL);
            ps.setString(1, state_cd);
            ps.setString(2, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                int pur_cd = rs.getInt("pur_cd");
                if (pur_cd == TableConstants.VM_PMT_APPLICATION_PUR_CD
                        || pur_cd == TableConstants.VM_PMT_FRESH_PUR_CD || pur_cd == TableConstants.VM_PMT_RENEWAL_PUR_CD || pur_cd == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD || pur_cd == TableConstants.VM_PMT_CANCELATION_PUR_CD || pur_cd == TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD) {
                    pmtPurpose = true;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return pmtPurpose;
    }

    public static PermitHomeAuthDobj getTemporaryNPDetails(String regn_no, String state_cd, String appl_no) throws VahanException {
        PermitHomeAuthDobj dobj_auth = null;
        PreparedStatement pstmt = null;
        TransactionManager tmgr = null;
        String sql = "";
        try {
            sql = "select * from permit.va_np_detail where regn_no=? and state_cd =? and appl_no=? ";
            tmgr = new TransactionManager("getTemporaryNPDetails");
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, regn_no);
            pstmt.setString(2, state_cd);
            pstmt.setString(3, appl_no);
            RowSet rowSet = tmgr.fetchDetachedRowSet();
            if (rowSet.next()) {
                dobj_auth = new PermitHomeAuthDobj();
                dobj_auth.setRegnNo(rowSet.getString("regn_no"));
                dobj_auth.setAuthFrom(rowSet.getDate("auth_from"));
                dobj_auth.setAuthUpto(rowSet.getDate("auth_upto"));
                dobj_auth.setPurCd(rowSet.getInt("pur_cd"));
                dobj_auth.setAuthNo(rowSet.getString("pmt_no"));
                dobj_auth.setNpVerifyStatus(rowSet.getString("npverifystatus"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There is Some Problem Fatching National Permit Data");
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dobj_auth;
    }

    public static TmConfigurationUserMgmtDobj getUserMgmtTmConfigurationParameters(TransactionManager tmgr, String stateCd) throws VahanException {
        TmConfigurationUserMgmtDobj userMgmtConfigDobj = null;
        PreparedStatement ps = null;
        try {
            String sql = "SELECT * FROM " + TableList.TM_CONFIGURATION_USERMGMT + " where state_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                userMgmtConfigDobj = new TmConfigurationUserMgmtDobj();
                userMgmtConfigDobj.setStateCd(rs.getString("state_cd"));
                userMgmtConfigDobj.setRestrict_login_user_catg(rs.getString("restrict_login_user_catg"));
                userMgmtConfigDobj.setIp_config_by_state_admin(rs.getBoolean("ip_config_by_state_admin"));
//                userMgmtConfigDobj.setAppl_dispose_with_otp(rs.getBoolean("appl_dispose_with_otp"));
//                userMgmtConfigDobj.setRcpt_cancel_with_otp(rs.getBoolean("rcpt_cancel_with_otp"));
//                userMgmtConfigDobj.setOwner_mobile_verify_with_otp(rs.getBoolean("owner_mobile_verify_with_otp"));
//                userMgmtConfigDobj.setChange_veh_office_with_otp(rs.getBoolean("change_veh_office_with_otp"));
//                userMgmtConfigDobj.setDelete_smartcard_flatfile_withOtp(rs.getBoolean("delete_smartcard_flatfile_withOtp"));
            }

            if (userMgmtConfigDobj == null) {
                throw new VahanException("Problem in getting the User Management Configuration Details, Please try after sometime.");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " getUserMgmtTmConfigurationParameters " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting the User Management Configuration Details, Please try after sometime.");
        }
        return userMgmtConfigDobj;
    }

    /**
     * @For getting TmConfigurationOtp Parameters
     */
    public static TmConfigurationOtpDobj getOtpTmConfigurationParameters(TransactionManager tmgr, String stateCd) throws VahanException {
        TmConfigurationOtpDobj otpConfigDobj = null;
        PreparedStatement ps = null;
        try {
            String sql = "SELECT * FROM " + TableList.TM_CONFIGURATION_OTP + " where state_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                otpConfigDobj = new TmConfigurationOtpDobj();
                otpConfigDobj.setStateCd(rs.getString("state_cd"));
                otpConfigDobj.setAppl_dispose_with_otp(rs.getBoolean("appl_dispose_with_otp"));
                otpConfigDobj.setRcpt_cancel_with_otp(rs.getBoolean("rcpt_cancel_with_otp"));
                otpConfigDobj.setOwner_mobile_verify_with_otp(rs.getBoolean("owner_mobile_verify_with_otp"));
                otpConfigDobj.setChange_veh_office_with_otp(rs.getBoolean("change_veh_office_with_otp"));
                otpConfigDobj.setDelete_smartcard_flatfile_withOtp(rs.getBoolean("delete_smartcard_flatfile_withOtp"));
                otpConfigDobj.setAdd_modify_office_with_otp(rs.getBoolean("add_modify_office_with_otp"));
                otpConfigDobj.setModifyLTTorOTTwithotp(rs.getBoolean("modify_ltt_or_ott_with_otp"));
                otpConfigDobj.setRestoreDisposeApplicationOtp(rs.getBoolean("restore_dispose_application_otp"));
                otpConfigDobj.setMobile_no_count_with_otp(rs.getBoolean("mobile_no_count_with_otp"));
                otpConfigDobj.setDeleteModifyRefundWithOtp(rs.getBoolean("delete_modify_refund_with_otp"));
            }

            if (otpConfigDobj == null) {
                throw new VahanException("Problem in getting the OTP Configuration Details, Please try after sometime.");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " getOtpTmConfigurationParameters " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting the OTP Configuration Details, Please try after sometime.");
        }
        return otpConfigDobj;
    }

    /**
     * This function is used for getting the information about the blocked or
     * blacklisted vehicle.
     */
    public static boolean isPurposeCodeOnBlacklistedVehicle(String regnNo, int purCd, String stateCd, TmConfigurationDobj tmConfig) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        String listPurCd, chasisNo = null, purCode = "," + purCd + ",";
        boolean isblacklistedvehicle = false;
        try {
            tmgr = new TransactionManagerReadOnly("isPurposeCodeOnBlacklistedVehicle");
            String sqlChasis = "SELECT CHASI_NO FROM " + TableList.VT_OWNER + " WHERE REGN_NO=?";
            PreparedStatement ps = tmgr.prepareStatement(sqlChasis);
            ps.setString(1, regnNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                chasisNo = rs.getString("chasi_no");
            } else {
                throw new VahanException("Invalid Registration No.");
            }
            BlackListedVehicleImpl obj = new BlackListedVehicleImpl();
            BlackListedVehicleDobj blacklistedStatus = obj.getBlacklistedVehicleDetails(regnNo, chasisNo);
            listPurCd = tmConfig.getBlocked_purcd_for_blacklist_vehicle();
            if (blacklistedStatus != null && !CommonUtils.isNullOrBlank(listPurCd)) {
                if (listPurCd.contains(purCode)) {
                    isblacklistedvehicle = true;
                } else {
                    isblacklistedvehicle = false;
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " isPurposeCodeOnBlacklistedVehicle " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting the blacklist vehicle Details, Please try after sometime.");
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return isblacklistedvehicle;
    }

    public static TmConfigurationSwappingDobj getTmConfigurationSwappingParameters(String state_cd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        TmConfigurationSwappingDobj dobj = null;

        try {
            tmgr = new TransactionManager("getTmConfigurationParameters");
            sql = "SELECT state_cd,same_owner_name,same_father_name,same_vehicle_class,multiple_swapping_allowed,old_vehicle_age,new_vehicle_age,"
                    + " swapping_allowed_theft_untraced_case,one_regn_must_be_fancy_no,same_vehicle_category "
                    + " FROM " + TableList.TM_CONFIGURATION_SWAPPING
                    + " WHERE state_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);

            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new TmConfigurationSwappingDobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setSame_owner_name(rs.getBoolean("same_owner_name"));
                dobj.setSame_father_name(rs.getBoolean("same_father_name"));
                dobj.setSame_vehicle_class(rs.getBoolean("same_vehicle_class"));
                dobj.setMultiple_swapping_allowed(rs.getBoolean("multiple_swapping_allowed"));
                dobj.setOld_vehicle_age(rs.getInt("old_vehicle_age"));
                dobj.setNew_vehicle_age(rs.getInt("new_vehicle_age"));
                dobj.setSwapping_allowed_theft_untraced_case(rs.getBoolean("swapping_allowed_theft_untraced_case"));
                dobj.setOne_regn_must_be_fancy_no(rs.getBoolean("one_regn_must_be_fancy_no"));
                dobj.setSame_vehicle_category(rs.getBoolean("same_vehicle_category"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting the Swapping Configuration Details, Please try after sometime.");
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
        return dobj;
    }

    public static List<PaymentGatewayDobj> getCartList(long userCd, int offCd, String stateCd) throws VahanException {

        String sql = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        List<PaymentGatewayDobj> dobjList = new ArrayList<PaymentGatewayDobj>();

        try {
            tmgr = new TransactionManager("getCartList");
            sql = "select COALESCE(a.transaction_no, 'New Cart') as transaction_no, count(distinct a.appl_no) as total \n"
                    + " from vp_rcpt_cart a "
                    + " inner join va_details b on b.state_cd = a.state_cd and b.off_cd = a.off_cd and b.appl_no = a.appl_no  \n"
                    + " where a.user_cd = ? and a.state_cd = ? and b.off_cd = ? \n"
                    + " group by 1 \n"
                    + " order by 1 ";

            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, userCd);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                PaymentGatewayDobj dobj = new PaymentGatewayDobj();
                dobj.setPaymentId(rs.getString("transaction_no"));
                dobj.setTotalNoOfApplicationsIncart(rs.getString("total"));
                dobjList.add(dobj);
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
        return dobjList;
    }

    public static TmCofigurationOnlinePaymentDobj getOnlinePaymentTmConfigurationParameters(TransactionManager tmgr, String stateCd) throws VahanException {
        TmCofigurationOnlinePaymentDobj onlinePayConfigDobj = null;
        PreparedStatement ps = null;
        try {
            String sql = "SELECT * FROM " + TableList.TM_CONFIGURATION_ONLINE_PAYMENT + " where state_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                onlinePayConfigDobj = new TmCofigurationOnlinePaymentDobj();
                onlinePayConfigDobj.setState_cd(stateCd);
                onlinePayConfigDobj.setTax_collection(rs.getBoolean("is_tax_collection"));
                onlinePayConfigDobj.setTax_installment(rs.getBoolean("is_tax_installment"));
            }
            if (onlinePayConfigDobj == null) {
                throw new VahanException("Problem in getting the Online Payment Configuration Details, Please try after sometime.");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " getOnlinePaymentTmConfigurationParameters " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting the Online Payment Configuration Details, Please try after sometime.");
        }
        return onlinePayConfigDobj;
    }

    /**
     * If record exist then return User login office code else selected office
     * code in case of Temp fee in new regn.
     */
    public static int getOfficeCdForDealerTempAppl(String applNo, String stateCd, String callFrom) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        int offCd = 0;
        try {
            if (Util.getSelectedSeat() != null && !"offCorrection".equals(callFrom) && !"taxMode".equals(callFrom)) {
                offCd = Util.getSelectedSeat().getOff_cd();
            }
            tmgr = new TransactionManagerReadOnly("getOfficeCdForFileFlow");
            sql = " select a.appl_no,a.pur_cd, a.off_cd  from va_details a where a.appl_no in(select  b.appl_no from va_details b where a.appl_no = b.appl_no and b.pur_cd in (" + TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE + "," + TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE + ") group by 1 having count(b.appl_no)> 1) "
                    + " AND a.appl_no = ? and a.state_cd = ? order by a.pur_cd desc limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                offCd = rs.getInt("off_cd");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
        return offCd;
    }

    /**
     * If record exist then return User login office code else selected office
     * code in case of Temp fee in new regn.
     *
     * @author Kartikey Singh
     */
    public static int getOfficeCdForDealerTempAppl(String applNo, String stateCd, String callFrom, int offCode) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        int offCd = 0;
        try {
            if (!"offCorrection".equals(callFrom) && !"taxMode".equals(callFrom)) {
                offCd = offCode;
            }
            tmgr = new TransactionManagerReadOnly("getOfficeCdForFileFlow");
            sql = " select a.appl_no,a.pur_cd, a.off_cd  from va_details a where a.appl_no in(select  b.appl_no from va_details b where a.appl_no = b.appl_no and b.pur_cd in (" + TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE + "," + TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE + ") group by 1 having count(b.appl_no)> 1) "
                    + " AND a.appl_no = ? and a.state_cd = ? order by a.pur_cd desc limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                offCd = rs.getInt("off_cd");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
        return offCd;
    }

    public static TmConfigurationNonUseDobj getTmConfigurationNonUseParameters(String state_cd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        TmConfigurationNonUseDobj dobj = null;

        try {
            tmgr = new TransactionManager("getTmConfigurationNonUseParameters");
            sql = "SELECT state_cd,nonuse_adjust_in_tax_amt,skip_fitness_validation_in_nonuse,nonuse_continue_without_restore,disable_nonuse_fromdate_in_nonuse_continue,"
                    + "docs_surrender,vehicle_inspect_mandatory,declare_withdrawl_date,exemfrom_first_dateofmonth,exemto_last_dateofmonth,exemupto_financial_year,"
                    + "require_advance_tax,vehicle_inspect_for_removalshift,taxclear_for_nonuse_rebate_in_duration,remove_frm_nonuse_in_removalshift,acknnowledge_report_in_restore,"
                    + "section_act_rule,approved_authority FROM " + TableList.TM_CONFIGURATION_NONUSE
                    + " WHERE state_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);

            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new TmConfigurationNonUseDobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setNonuse_adjust_in_tax_amt(rs.getBoolean("nonuse_adjust_in_tax_amt"));
                dobj.setSkip_fitness_validation_in_nonuse(rs.getBoolean("skip_fitness_validation_in_nonuse"));
                dobj.setNonuse_continue_without_restore(rs.getBoolean("nonuse_continue_without_restore"));
                dobj.setDisable_nonuse_fromdate_in_nonuse_continue(rs.getBoolean("disable_nonuse_fromdate_in_nonuse_continue"));
                dobj.setDocs_surrender(rs.getBoolean("docs_surrender"));
                dobj.setVehicle_inspect_mandatory(rs.getBoolean("vehicle_inspect_mandatory"));
                dobj.setDeclare_withdrawl_date(rs.getBoolean("declare_withdrawl_date"));
                dobj.setExemfrom_first_dateofmonth(rs.getBoolean("exemfrom_first_dateofmonth"));
                dobj.setExemto_last_dateofmonth(rs.getBoolean("exemto_last_dateofmonth"));
                dobj.setExemupto_financial_year(rs.getBoolean("exemupto_financial_year"));
                dobj.setRequire_advance_tax(rs.getBoolean("require_advance_tax"));
                dobj.setVehicle_inspect_for_removalshift(rs.getBoolean("vehicle_inspect_for_removalshift"));
                dobj.setTaxclear_for_nonuse_rebate_in_duration(rs.getBoolean("taxclear_for_nonuse_rebate_in_duration"));
                dobj.setRemove_frm_nonuse_in_removalshift(rs.getBoolean("remove_frm_nonuse_in_removalshift"));
                dobj.setAcknnowledge_report_in_restore(rs.getBoolean("acknnowledge_report_in_restore"));
                dobj.setSection_act_rule(rs.getString("section_act_rule"));
                dobj.setApproved_authority(rs.getString("approved_authority"));
            }
            if (dobj == null) {
                throw new VahanException("Problem in getting the Nonuse Configuration Details, Please try after sometime.");
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting the Nonuse Configuration Details, Please try after sometime.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dobj;
    }

    public static String sendOtpAndMailForTransaction(String otpType, String enteredOtp, String sendedOtp, String empCd, String applNo, String messageType) throws VahanException {
        String otp = null;
        try {
            boolean isverified = UserDAO.getVerificationDetails(empCd);
            if (isverified) {
                switch (otpType) {
                    case "sendOtp":
                    case "resendOtp":
                        otp = SmsMailOTPImpl.sendOTPorMail(empCd, "OTP for " + messageType, otpType, sendedOtp, "OTP for " + messageType);
                        if (!CommonUtils.isNullOrBlank(otp)) {
                            return otp;
                        } else {
                            throw new VahanException("Unable to generate OTP and send it.");
                        }
                    case "confirmOtp":
                        if (sendedOtp.equals(enteredOtp)) {
                            return enteredOtp;
                        } else {
                            throw new VahanException("Invalid OTP, Please enter correct OTP");
                        }
                }
            } else {
                throw new VahanException("OTP is mandatory for this transaction and your Mobile no is not verified, please verify first from UPDATE PROFILE module.");
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + "" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return null;
    }

    public static String getConfigurationCashCounter(String state_cd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        String message = null;
        try {
            tmgr = new TransactionManagerReadOnly("getConfigurationCashCounter");
            sql = "SELECT * FROM " + TableList.TM_CONFIGURATION_CASH_COUNTER
                    + " WHERE state_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                message = rs.getString("messages");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
        return message;
    }

    public static TmConfigurationPrintDobj getTmConfigurationPrintParameters(TransactionManager tmgr, String state_cd) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        TmConfigurationPrintDobj dobj = null;
        try {
            sql = "SELECT state_cd, image_background, image_logo, temporary_rc_remarks, print_tax_token, print_no_dues_cert, road_safety_slogan, cash_rcpt_note, defaulter_notice_grace_period, advance_receipt_validity_days FROM " + TableList.TM_CONFIGURATION_PRINT
                    + " WHERE state_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new TmConfigurationPrintDobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setImage_background(rs.getString("image_background"));
                dobj.setImage_logo(rs.getString("image_logo"));
                dobj.setTemporary_rc_remarks(rs.getString("temporary_rc_remarks"));
                dobj.setPrint_tax_token(rs.getBoolean("print_tax_token"));
                dobj.setPrint_no_dues_cert(rs.getBoolean("print_no_dues_cert"));
                dobj.setRoad_safety_slogan(rs.getBoolean("road_safety_slogan"));
                dobj.setCash_rcpt_note(rs.getString("cash_rcpt_note"));
                dobj.setDefaulterNoticeGracePeriod(rs.getInt("defaulter_notice_grace_period"));
                dobj.setAdvance_receipt_validity_days(rs.getInt("advance_receipt_validity_days"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return dobj;
    }

    public static void updateApprovedStatusOfPermit(TransactionManager tmgr, Status_dobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        sql = "UPDATE " + TableList.VA_DETAILS
                + "   SET  entry_status=?,confirm_ip=?,confirm_date=current_timestamp"
                + " WHERE  appl_no=? and state_cd=?";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getEntry_status());
        ps.setString(2, Util.getClientIpAdress());
        ps.setString(3, dobj.getAppl_no());
        ps.setString(4, Util.getUserStateCode());
        ps.executeUpdate();
    }

    public static DealerMasterDobj getDealerDetailsByDealerCode(String dealerCd, String state_cd) {
        PreparedStatement ps = null;
        String sql = null;
        TransactionManagerReadOnly tmgr = null;
        DealerMasterDobj dealerDobj = null;
        String stateCdVar = "";
        try {
            if (!CommonUtils.isNullOrBlank(state_cd)) {
                stateCdVar = " and state_cd = ?";
            }
            tmgr = new TransactionManagerReadOnly("getDealerDetailsByDealerCode");
            sql = "SELECT * FROM " + TableList.VM_DEALER_MAST
                    + " WHERE dealer_cd = ? " + stateCdVar + "";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dealerCd);
            if (!CommonUtils.isNullOrBlank(state_cd)) {
                ps.setString(2, state_cd);
            }
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dealerDobj = new DealerMasterDobj();
                dealerDobj.setDealerName(rs.getString("dealer_name"));
                dealerDobj.setDealerRegnNo(rs.getString("dealer_regn_no"));
                dealerDobj.setDealerAdd1(rs.getString("d_add1"));
                dealerDobj.setDealerAdd2(rs.getString("d_add2"));
                dealerDobj.setDealerDistrict(rs.getInt("d_district"));
                dealerDobj.setDealerPincode(rs.getInt("d_pincode"));
                dealerDobj.setDealerStateCode(rs.getString("d_state"));
                dealerDobj.setDealerValidUpto(rs.getDate("valid_upto"));
                dealerDobj.setTin_NO(rs.getString("tin_no"));
                dealerDobj.setOffCode(rs.getInt("off_cd"));
                dealerDobj.setRegistrationMarkAuth(rs.getBoolean("regn_mark_gen_by_dealer"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dealerDobj;
    }

    public static void Compare(String name, List<PermitRouteList> fromDbByFlag, List<PermitRouteList> fromDb, List<PermitRouteList> form, List<ComparisonBean> compBeanList) {

        ComparisonBean comparisonBean = null;

        String oldVal = "";
        String newValue = "";
        for (PermitRouteList s : fromDb) {
            oldVal = oldVal + s.getKey() + ",";
        }

        for (PermitRouteList s : fromDbByFlag) {
            oldVal = oldVal + s.getKey() + ",";
        }

        for (Object ss : form) {
            if (ss instanceof String) {
                newValue = newValue + (String) ss + ",";
            } else if (ss instanceof PermitRouteList) {
                newValue = newValue + ((PermitRouteList) ss).getKey() + ",";
            }
        }
        if (!oldVal.equalsIgnoreCase(newValue)) {
            comparisonBean = new ComparisonBean();
            comparisonBean.setFields(name);
            comparisonBean.setOld_value(oldVal);
            comparisonBean.setNew_value(newValue);
            compBeanList.add(comparisonBean);

        }

    }

    public static void validateNoCashPayment(String PayType, String payMode) throws VahanException {
        if (Util.getTmConfiguration() != null) {
            if (Util.getTmConfiguration().isNoCashPayment() && "C".equals(payMode) && !"OnlinePayment".equalsIgnoreCase(PayType)) {
                throw new VahanException("Cash payment is not allowed in your state. Please pay through Other Payment Mode.");
            }
        } else {
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public static String getMakerModelName(String unique_model_ref_no) {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        String makerNameOnTAC = "";
        try {
            if (!CommonUtils.isNullOrBlank(unique_model_ref_no)) {
                tmgr = new TransactionManagerReadOnly("getMakerModelName");
                sql = "select model_name_on_tac from vahan4.vm_model_homologation where unique_model_ref_no = ? and upper(model_name_on_tac) != upper(model_name) ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, unique_model_ref_no);
                RowSet rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    makerNameOnTAC = rs.getString("model_name_on_tac");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return makerNameOnTAC;
    }

    public static void updateOnlinePermitStatus(TransactionManager tmgr, Status_dobj dobj) throws VahanException, SQLException {
        PreparedStatement ps = null;
        String sql = null;
        ApplicationInwardImpl applicationInwardImpl = new ApplicationInwardImpl();
        boolean isOnlineAppl = applicationInwardImpl.isOnlineApplication(dobj.getAppl_no(), dobj.getPur_cd());
        if (isOnlineAppl) {
            applicationInwardImpl.updateApprovedStatusForOnlineAppl(tmgr, dobj);
            sql = "INSERT INTO " + TableList.VHA_STATUS_APPL
                    + " SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno,"
                    + "       action_cd, seat_cd, cntr_id, status, office_remark, public_remark,"
                    + "       file_movement_type, ? as emp_cd, op_dt, moved_from_online, current_timestamp as moved_on"
                    + "  FROM " + TableList.VA_STATUS_APPL + " where appl_no=? and pur_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(Util.getEmpCode()));
            ps.setString(2, dobj.getAppl_no());
            ps.setInt(3, dobj.getPur_cd());
            ps.executeUpdate();

            sql = "Delete FROM " + TableList.VA_STATUS_APPL + " WHERE appl_no=? and pur_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getAppl_no());
            ps.setInt(2, dobj.getPur_cd());
            ps.executeUpdate();
        }
    }

    public static void saveUpdateOTPVerifyDetails(TransactionManager tmgr, Owner_dobj owner_dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = "";
        RowSet rs = null;
        try {
            if (owner_dobj != null && owner_dobj.getOwner_identity() != null) {
                sql = "select appl_no from " + TableList.VT_OTP_VERIFY + " where state_cd = ? and off_cd=? and appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, owner_dobj.getState_cd());
                ps.setInt(2, owner_dobj.getOff_cd());
                ps.setString(3, owner_dobj.getAppl_no());
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    sql = "UPDATE " + TableList.VT_OTP_VERIFY + " SET regn_no=? WHERE state_cd = ? and off_cd=? and appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, owner_dobj.getRegn_no());
                    ps.setString(2, owner_dobj.getState_cd());
                    ps.setInt(3, owner_dobj.getOff_cd());
                    ps.setString(4, owner_dobj.getAppl_no());
                    ps.executeUpdate();
                } else {
                    sql = "INSERT INTO " + TableList.VT_OTP_VERIFY + "(state_cd, off_cd, appl_no, regn_no, mobile_no, mobile_verified_on, email_id, \n"
                            + " email_verified_on, op_dt) VALUES (?, ?, ?, ?, ?, current_timestamp, ?, ?, current_timestamp);";
                    ps = tmgr.prepareStatement(sql);
                    int i = 1;
                    ps.setString(i++, owner_dobj.getState_cd());
                    ps.setInt(i++, owner_dobj.getOff_cd());
                    ps.setString(i++, owner_dobj.getAppl_no());
                    ps.setString(i++, owner_dobj.getRegn_no());
                    if (owner_dobj.getOwner_identity().getMobile_no() != 0L) {
                        ps.setLong(i++, owner_dobj.getOwner_identity().getMobile_no());
                    } else {
                        throw new VahanException("Mobile no. can't be blank.");
                    }
                    if (!CommonUtils.isNullOrBlank(owner_dobj.getOwner_identity().getEmail_id())) {
                        ps.setString(i++, owner_dobj.getOwner_identity().getEmail_id());
                        ps.setTimestamp(i++, new Timestamp(new Date().getTime()));
                    } else {
                        ps.setString(i++, owner_dobj.getOwner_identity().getEmail_id());
                        ps.setNull(i++, java.sql.Types.DATE);
                    }
                    ps.executeUpdate();
                }
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in saving OTP verification details.");
        }
    }

    public static String getMobileNoCountMessage(Long mobile_no) throws VahanException {
        String countMessage = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            String sql = "SELECT count(1) from vt_owner_identification where mobile_no=?";
            tmgr = new TransactionManager("getMobileNoCountMessage ");
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, mobile_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                int count = rs.getInt("count");
                if (count > 5) {
                    countMessage = "The Mobile no : " + mobile_no + " has been found more than 5 times against different registration nos .<br/>"
                            + "This mobile no seems to be spam or wrongly entered. <br/>"
                            + "Still you want to use this mobile no OTP comes to your mobile no for verification ";
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0] + "Mobile No" + mobile_no);
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

        return countMessage;

    }

    public static boolean isSmartCard(String state_cd, int off_cd) throws VahanException {
        TransactionManager tmgr = null;
        boolean isSmartCard = false;
        try {
            tmgr = new TransactionManager("isSmartCard");
            isSmartCard = verifyForSmartCard(state_cd, off_cd, tmgr);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Getting Smart Card Information");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return isSmartCard;
    }

    public static TmConfigurationReceipts getTmConfigurationReceipts(String stateCd) {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        TmConfigurationReceipts configFeeFineZero = null;
        try {
            tmgr = new TransactionManagerReadOnly("getUserCategory");
            sql = "Select fee_amt_zero,fine_amt_zero,covid19_from,covid19_upto from " + TableList.TM_CONFIGURATION_RECEIPTS + " where state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                configFeeFineZero = new TmConfigurationReceipts();
                configFeeFineZero.setFeeAmtZero(rs.getString("fee_amt_zero"));
                configFeeFineZero.setFineAmtZero(rs.getString("fine_amt_zero"));
                configFeeFineZero.setCovid19_from(rs.getDate("covid19_from"));
                configFeeFineZero.setCovid19_upto(rs.getDate("covid19_upto"));
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return configFeeFineZero;
    }

    public static TmConfigurationUserMessagingDobj getUserMessagingTmConfigurationParameters(TransactionManager tmgr, String stateCd) throws VahanException {
        TmConfigurationUserMessagingDobj userMsgConfigDobj = null;
        PreparedStatement ps = null;
        try {
            String sql = "SELECT * FROM " + TableList.TM_CONFIGURATION_USERMESSAGING + " where state_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                userMsgConfigDobj = new TmConfigurationUserMessagingDobj();
                userMsgConfigDobj.setViewMsgsFront(rs.getBoolean("view_msgs_front"));
                userMsgConfigDobj.setStateCdRtoCdJoined(rs.getBoolean("state_cd_rto_cd_joined"));
                userMsgConfigDobj.setFileUpload(rs.getBoolean("file_upload"));
                userMsgConfigDobj.setSortByOffCd(rs.getBoolean("sort_by_off_code"));
                userMsgConfigDobj.setSortByOffName(rs.getBoolean("sort_by_off_name"));
                userMsgConfigDobj.setShowUnclosedMsgsOnly(rs.getBoolean("show_unclosed_msgs_only"));
                userMsgConfigDobj.setSendCopyStateAdmin(rs.getBoolean("send_copy_state_admin"));
                userMsgConfigDobj.setUserMsgByCatg(rs.getBoolean("user_msg_by_catg"));
            }

            if (userMsgConfigDobj == null) {
                throw new VahanException("Problem in getting the User Messaging Configuration Details, Please try after sometime.");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " getUserMessagingTmConfigurationParameters " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting the User Messaging Configuration Details, Please try after sometime.");
        }
        return userMsgConfigDobj;
    }

    public static TmConfigurationAdvanceRegnDobj getTmConfigurationChangeAdminParameters(String stateCd) throws VahanException {
        TmConfigurationAdvanceRegnDobj changeAdminConfigDobj = null;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManagerReadOnly("getTmConfigurationChangeAdminParameters");
            String sql = "SELECT state_cd, old_number_replace, size_old_number FROM " + TableList.TM_CONFIGURATION_ADVANCE_REGN + " where state_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                changeAdminConfigDobj = new TmConfigurationAdvanceRegnDobj();
                changeAdminConfigDobj.setOldNumberReplace(rs.getBoolean("old_number_replace"));
                changeAdminConfigDobj.setSizeOldNumber(rs.getInt("size_old_number"));
            }

            if (changeAdminConfigDobj == null) {
                throw new VahanException("Problem in getting the Change Admin Configuration Details, Please try after sometime.");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " getTmConfigurationChangeAdminParameters " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting the Change Admin Configuration Details, Please try after sometime.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return changeAdminConfigDobj;
    }

    public static void validateHSRPPendency(String stateCd, int offCd, String dealerCd, int hsrpGraceDays) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        String sql = "";
        int nonWorkDays = 0;
        try {
            tmgr = new TransactionManagerReadOnly("validateHSRPPendency");
            sql = " select " + hsrpGraceDays + " + " + hsrpGraceDays + "-count(distinct day_begin::date) as non_work_days  from " + TableList.THM_OFFICE_CONFIGURATION + " where state_cd = ? and off_cd = ? and day_begin::date >= CURRENT_DATE - interval '" + hsrpGraceDays + " days' ";
            PreparedStatement psDays = tmgr.prepareStatement(sql);
            int i = 1;
            psDays.setString(i++, stateCd);
            psDays.setInt(i++, offCd);
            RowSet rsWorkDays = tmgr.fetchDetachedRowSet_No_release();
            if (rsWorkDays.next()) {
                nonWorkDays = rsWorkDays.getInt("non_work_days");
            }

            sql = "select regn_no from " + TableList.VA_DEALER_PENDENCY + " "
                    + " where dealer_cd = ? and state_cd= ? and hsrp_status = false and  op_dt < current_date-interval '" + nonWorkDays + " days' and regn_no != 'NEW'";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, dealerCd);
            ps.setString(2, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                throw new VahanException("HSRP fitment pending more than " + nonWorkDays + " working days, Check 'HSRP Pendency Reports' in 'Report' Menu.");
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in getting HSRP pendency details.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
    }

    public static void insertDealerHSRPPendencyDetails(TransactionManager tmgr, Owner_dobj dobj, String regnNo, TmConfigurationDobj tmConfDobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        RowSet rs = null;
        boolean validForInsert = false;
        try {
            if (dobj != null && !CommonUtils.isNullOrBlank(dobj.getDealer_cd())) {
                sql = "select regn_no from " + TableList.VA_DEALER_PENDENCY + " WHERE appl_no = ? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getAppl_no());
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (!rs.next()) {
                    validForInsert = true;
                }
                if (tmConfDobj != null && !tmConfDobj.isRc_after_hsrp() && validForInsert) {
                    String manuMonth = dobj.getManu_mon() < 10 ? "0" + String.valueOf(dobj.getManu_mon()) : String.valueOf(dobj.getManu_mon());
                    String manuYearMonth = String.valueOf(dobj.getManu_yr()) + manuMonth;
                    if (Integer.parseInt(manuYearMonth) >= TableConstants.CHECK_MANU_MONTH_YEAR_FOR_HSRP) {
                        sql = "INSERT INTO " + TableList.VA_DEALER_PENDENCY + "(op_dt, dealer_cd, appl_no, regn_no, state_cd)\n"
                                + " VALUES (current_timestamp, ?, ?, ?, ?)";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, dobj.getDealer_cd());
                        ps.setString(2, dobj.getAppl_no());
                        ps.setString(3, regnNo);
                        ps.setString(4, dobj.getState_cd());
                        ps.executeUpdate();
                    }
                } else if (validForInsert) {
                    sql = "INSERT INTO " + TableList.VA_DEALER_PENDENCY + "(op_dt, dealer_cd, appl_no, regn_no, state_cd)\n"
                            + " VALUES (current_timestamp, ?, ?, ?, ?)";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, dobj.getDealer_cd());
                    ps.setString(2, dobj.getAppl_no());
                    ps.setString(3, regnNo);
                    ps.setString(4, dobj.getState_cd());
                    ps.executeUpdate();
                } else {
                    sql = "UPDATE " + TableList.VA_DEALER_PENDENCY + " SET op_dt=current_timestamp, regn_no = ? WHERE appl_no= ? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, regnNo);
                    ps.setString(2, dobj.getAppl_no());
                    ps.executeUpdate();
                }
            } else {
                throw new VahanException("Dealer Code is blank, Please go to the Home page then try again.");
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem occurred during updating the dealer pendency details.");
        }
    }

    public static void updateHSRPStatus(TransactionManager tmgr, String applNo, String stateCd, String regnNo, String empCd) throws Exception {
        String sql = "";
        PreparedStatement ps = null;
        boolean isDMSRunning = false;
        try {
            sql = "UPDATE " + TableList.VA_DEALER_PENDENCY + " SET op_dt=current_timestamp, hsrp_status=true WHERE appl_no= ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.executeUpdate();

            sql = "select pur_cd from " + TableList.TM_CONFIGURATION_DMS + " WHERE state_cd = ? and pur_cd ~ '," + TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE + ",' ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                isDMSRunning = true;
            }
            insertIntoVhDealerPendency(tmgr, empCd, stateCd, applNo, isDMSRunning);
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem occurred during updating the dealer pendency details.");
        }
    }

    public static void insertIntoVhDealerPendency(TransactionManager tmgr, String empCd, String stateCd, String applNo, boolean isDMSRunning) throws SQLException {
        String sql = "INSERT INTO " + TableList.VH_DEALER_PENDENCY + "(moved_on, moved_by, op_dt, dealer_cd, appl_no, regn_no, state_cd, \n"
                + " hsrp_status, dms_status) (SELECT CURRENT_TIMESTAMP, ?, op_dt, dealer_cd, appl_no, regn_no, state_cd, hsrp_status, dms_status\n"
                + " FROM " + TableList.VA_DEALER_PENDENCY + " WHERE state_cd= ? and appl_no = ? and hsrp_status = true and dms_status = ?)";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, empCd);
        ps.setString(2, stateCd);
        ps.setString(3, applNo);
        ps.setBoolean(4, isDMSRunning);
        int count = ps.executeUpdate();
        if (count > 0) {
            ServerUtility.deleteFromTable(tmgr, null, applNo, TableList.VA_DEALER_PENDENCY);
        }
    }

    /*
     * Author: Kartikey Singh
     */
    public static void updateRtoOpenDateInVtFaceLessService(TransactionManager tmgr, String applNo, String stateCode, int offCd) throws SQLException, VahanException, Exception {
        String sql = "select * from " + TableList.VT_FACELESS_SERVICE + " where appl_no = ? and state_cd = ? and rto_open_dt is null";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, applNo);
        ps.setString(2, stateCode);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            sql = "update " + TableList.VT_FACELESS_SERVICE + " set rto_open_dt=current_timestamp "
                    + " where appl_no=? and state_cd = ?";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, stateCode);
            ServerUtility.validateQueryResult(tmgr, ps.executeUpdate(), ps);
        }
    }

    public static void sendSmsInFacelessService(String applNo, String stateCode, int offCd, String regnNo, String notVerifiedDocDetails) throws VahanException, Exception {
        TransactionManagerReadOnly tmgr = null;
        try {
            if (Util.getTmConfiguration() != null && Util.getTmConfiguration().isAllowFacelessService()) {
                tmgr = new TransactionManagerReadOnly("sendSmsInFacelessService");
                String sql = "select * from " + TableList.VT_FACELESS_SERVICE + " where appl_no = ? and state_cd = ?";
                PreparedStatement ps = tmgr.prepareStatement(sql);
                ps.setString(1, applNo);
                ps.setString(2, stateCode);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    List reason = getReasonsForHolding(applNo);
                    if (notVerifiedDocDetails != null && !notVerifiedDocDetails.isEmpty()) {
                        if (reason.size() > 1) {
                            int index = reason.indexOf(" DOCUMENTS NOT VERIFIED");
                            String notVerifiedMsg = " DOCUMENTS NOT VERIFIED " + "(" + notVerifiedDocDetails + ") Please reupload.";
                            reason.set(index, notVerifiedMsg);
                        } else if (reason.size() == 1) {
                            int index = reason.indexOf("DOCUMENTS NOT VERIFIED");
                            String notVerifiedMsg = "DOCUMENTS NOT VERIFIED " + "(" + notVerifiedDocDetails + ") Please reupload.";
                            reason.set(index, notVerifiedMsg);
                        }
                    }

                    String mobileNo = ServerUtility.getMobileNoForFacelessApplication(regnNo, tmgr, stateCode, offCd);

                    String msgMobile = "Faceless Application has been hold due to " + reason + "";

                    sendSMS(mobileNo, msgMobile);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in Sending Sms for faceless Application " + applNo + "");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
    }

    public static String[] verifyDocUploaded(String applNo, int actionCd) throws VahanException, Exception {
        String[] docDetailsNotVerify = null;
        String docDescrNotVerified = "";
        try {
            if (Util.getTmConfiguration() != null && Util.getTmConfiguration().isAllowFacelessService()) {
                List<VTDocumentModel> uploadedDocList = DmsDocCheckUtils.getUploadedDocumentList(applNo);
                if (Util.getTmConfiguration().getTmConfigDmsDobj() != null && Util.getTmConfiguration().getTmConfigDmsDobj().getVerfyActionCd().contains("," + actionCd + ",")) {
                    for (VTDocumentModel docsList : uploadedDocList) {
                        if (!docsList.isDoc_verified()) {
                            if (docDescrNotVerified.isEmpty()) {
                                docDescrNotVerified = docsList.getDoc_desc();
                            } else {
                                docDescrNotVerified = docsList.getDoc_desc() + "," + docDescrNotVerified;
                            }
                        }
                    }
                    if (!docDescrNotVerified.isEmpty()) {
                        docDetailsNotVerify = new String[2];
                        docDetailsNotVerify[0] = "Documents has not been verified. Either Verify documents or Hold application  with specific reason";
                        docDetailsNotVerify[1] = docDescrNotVerified;
                    }
                } else if (Util.getTmConfiguration().getTmConfigDmsDobj() != null && Util.getTmConfiguration().getTmConfigDmsDobj().getApproveActionCd().contains("," + actionCd + ",")) {
                    for (VTDocumentModel docsList : uploadedDocList) {
                        if (!docsList.isDoc_approved()) {
                            if (docDescrNotVerified.isEmpty()) {
                                docDescrNotVerified = docsList.getDoc_desc();
                            } else {
                                docDescrNotVerified = docsList.getDoc_desc() + "," + docDescrNotVerified;
                            }
                        }
                    }
                    if (!docDescrNotVerified.isEmpty()) {
                        docDetailsNotVerify = new String[2];
                        docDetailsNotVerify[0] = "Documents has not been approved. Either Approve documents or Hold application  with specific reason";
                        docDetailsNotVerify[1] = docDescrNotVerified;
                    }
                }
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return docDetailsNotVerify;
    }

    public static String getMobileNoForFacelessApplication(String regnNo, TransactionManagerReadOnly tmgr, String stateCode, int offCd) throws VahanException, Exception {
        String mobileNo = "";
        String sql = "select mobile_no from " + TableList.VT_OWNER_IDENTIFICATION + " where regn_no=? and state_cd = ? and off_cd = ?";
        try {
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCode);
            ps.setInt(3, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                mobileNo = rs.getString("mobile_no");
            } else {
                throw new VahanException("Mobile Number not found for faceless Registration Number " + regnNo + "");
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting mobile no for faceless Registration Number " + regnNo + "");
        }
        return mobileNo;
    }

    public static String getDealerCodeByUserCd(long userCode, String stateCd) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        String dealerCode = "";
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("getDealerCodeByUserCd");
            sql = "select m.dealer_cd \n"
                    + "from  vm_dealer_mast m left outer join tm_user_permissions up\n"
                    + "on m.dealer_cd = up.dealer_cd \n"
                    + "where m.state_cd = ? and up.user_cd = ? ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setLong(2, userCode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dealerCode = rs.getString("dealer_cd");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting details of dealer code.");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting details of dealer code.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dealerCode;
    }

    public static void updateVaNPAuthFlag(String applNo, TransactionManager tmgr, String flag) throws VahanException, Exception {
        String sql = null;
        PreparedStatement ps = null;
        RowSet rs;
        try {
            sql = "select * from permit.va_np_detail WHERE state_cd =? and appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, applNo);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                sql = "Update  permit.va_np_detail set pmtapprovestatus = ?  where appl_no = ? and state_cd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, flag);
                ps.setString(2, applNo);
                ps.setString(3, Util.getUserStateCode());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting va_np_detail. Please contact Administrator!!!");
        }
    }

    public static void sendSMSForHoldApplication(Long mobileNo, String holdReason, String applNo) {
        if (mobileNo != null && mobileNo != 0l) {
            String mobileSMS = "Dear Applicant,"
                    + "Your Application(" + applNo + ") is on hold due to " + holdReason + ", Please contact to your respective Dealer/RTO.";
            sendSMS(String.valueOf(mobileNo), mobileSMS);
        }
    }

    /*
        @author Kartikey Sigh
     */
    public static void sendSMSForHoldApplication(Long mobileNo, String holdReason, String applNo, String stateCode) {
        if (mobileNo != null && mobileNo != 0l) {
            String mobileSMS = "Dear Applicant,"
                    + "Your Application(" + applNo + ") is on hold due to " + holdReason + ", Please contact to your respective Dealer/RTO.";
            sendSMS(String.valueOf(mobileNo), mobileSMS, stateCode);
        }
    }

    public static void checkChasiNoExist(String chasiNo) throws VahanException {
        String NocNotIssuedChassisNoExist = ServerUtility.getChassisNoExist(chasiNo);
        if (NocNotIssuedChassisNoExist != null && NocNotIssuedChassisNoExist.length() > 0 && !NocNotIssuedChassisNoExist.isEmpty()) {
            throw new VahanException("Chassis No already exist - " + NocNotIssuedChassisNoExist);
        }
    }

    public static boolean insertNPAuthDetailsAtPrint(int pur_cd, String regn_no, String appl_no, String state_cd)
            throws VahanException, DateUtilsException {
        PreparedStatement pstmt = null;
        TransactionManager tmgr = null;
        boolean flag = false;
        String sql = "";
        ResultSet rs = null;
        String pmt_no = "", auth_no = "", v4_regno = regn_no, npverifystatus = "N";
        int pmt_type = 0, pmt_catg = 0;
        Date pmt_from = null, pmt_upto = null, auth_fr = null, auth_to = null;
        try {
            if (getPurCdFromVaDetails(state_cd, appl_no)) {
                sql = "Select * from permit.va_np_detail where state_cd=? and regn_no = ? and appl_no=?";
                tmgr = new TransactionManager("insertNPAuthDetailsAtPrint");
                pstmt = tmgr.prepareStatement(sql);
                pstmt.setString(1, state_cd);
                pstmt.setString(2, v4_regno);
                pstmt.setString(3, appl_no);
                RowSet rowSet = tmgr.fetchDetachedRowSet();
                if (!rowSet.next()) {
                    sql = "Select a.pmt_type,a.pmt_catg,a.pmt_no,a.valid_from,a.valid_upto, b.auth_no,b.auth_fr,b.auth_to from " + TableList.VT_PERMIT + " a"
                            + " inner join " + TableList.VT_PERMIT_HOME_AUTH + " b on b.pmt_no= a.pmt_no and b.regn_no= a.regn_no  where a.state_cd=? and a.regn_no = ? and a.pmt_type=?";
                    pstmt = tmgr.prepareStatement(sql);
                    pstmt.setString(1, state_cd);
                    pstmt.setString(2, regn_no);
                    pstmt.setInt(3, Integer.parseInt(TableConstants.NATIONAL_PERMIT));
                    rs = pstmt.executeQuery();
                    if (rs.next()) {
                        pmt_no = rs.getString("pmt_no");
                        pmt_from = rs.getDate("valid_from");
                        pmt_upto = rs.getDate("valid_upto");
                        auth_no = rs.getString("auth_no");
                        auth_fr = rs.getDate("auth_fr");
                        auth_to = rs.getDate("auth_to");
                        pmt_type = rs.getInt("pmt_type");
                        pmt_catg = rs.getInt("pmt_catg");
                        flag = true;
                    } else {
                        flag = false;
                    }

                    if (flag && (!CommonUtils.isNullOrBlank(state_cd)) && "UP,DL,GJ".contains(state_cd)) {
                        PermitHomeAuthDobj getNpDobj = getPermitDetailsFromNp(regn_no);
                        if (getNpDobj != null && auth_to != null) {
                            long dayDiff = 0;
                            if (auth_to.after(getNpDobj.getAuthUpto())) {
                                dayDiff = DateUtils.getDate1MinusDate2_Days(getNpDobj.getAuthUpto(), auth_to);
                            } else {
                                dayDiff = DateUtils.getDate1MinusDate2_Days(auth_to, getNpDobj.getAuthUpto());
                            }
                            if (dayDiff <= 30) {
                                npverifystatus = "A";
                            }
                        }
                        pstmt = null;
                        String query = " INSERT INTO permit.va_np_detail(state_cd, off_cd, appl_no, pmt_no, regn_no, valid_from,valid_upto,auth_no,auth_from,auth_upto,pur_cd,permit_type,permit_catg,paymentstatus,npverifystatus,pmtapprovestatus) \n"
                                + " VALUES (?, ?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
                        pstmt = tmgr.prepareStatement(query);
                        int m = 1;
                        pstmt.setString(m++, Util.getUserStateCode());
                        pstmt.setInt(m++, Util.getUserSeatOffCode());
                        pstmt.setString(m++, appl_no);
                        pstmt.setString(m++, pmt_no);
                        pstmt.setString(m++, v4_regno);
                        pstmt.setDate(m++, new java.sql.Date(pmt_from.getTime()));
                        pstmt.setDate(m++, new java.sql.Date(pmt_upto.getTime()));
                        pstmt.setString(m++, auth_no);
                        pstmt.setDate(m++, new java.sql.Date(auth_fr.getTime()));
                        pstmt.setDate(m++, new java.sql.Date(auth_to.getTime()));
                        pstmt.setInt(m++, pur_cd);
                        pstmt.setInt(m++, pmt_type);
                        pstmt.setInt(m++, pmt_catg);
                        pstmt.setString(m++, "Y");
                        pstmt.setString(m++, npverifystatus);
                        pstmt.setString(m++, "A");
                        if (pstmt.executeUpdate() > 0) {
                            tmgr.commit();
                        }
                    }
                } else {
                    flag = true;
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There is Some Problem Fatching National Permit Data");
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public static TmConfigurationPayVerifyDobj getPayVerifyTmConfigurationParameters(TransactionManager tmgr, String stateCd) throws VahanException {
        TmConfigurationPayVerifyDobj payVerifyConfigDobj = null;
        PreparedStatement ps = null;
        try {
            String sql = "SELECT state_cd,e_grass_verify,bank_receipt_verify FROM " + TableList.TM_CONFIGURATION_PAY_VERIFY + " where state_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                payVerifyConfigDobj = new TmConfigurationPayVerifyDobj();
                payVerifyConfigDobj.setStateCd(rs.getString("state_cd"));
                payVerifyConfigDobj.seteGrassVerify(rs.getBoolean("e_grass_verify"));
                payVerifyConfigDobj.setBankReceiptVerify(rs.getBoolean("bank_receipt_verify"));
            }

            if (payVerifyConfigDobj == null) {
                throw new VahanException("Problem in getting the Pay Verify Config Configuration Details, Please try after sometime.");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " getPayVerifyTmConfigurationParameters " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting the Pay Verify Config Configuration Details Details, Please try after sometime.");
        }
        return payVerifyConfigDobj;
    }

    public static boolean validateVehicleNorms(Owner_dobj ownerDobj, int purCd, VehicleParameters vehParameters, TmConfigurationDealerDobj tmConfig) throws VahanException {
        try {
            if (ownerDobj != null && ownerDobj.getNorms() != 0 && ownerDobj.getPurchase_dt() != null && vehParameters != null && tmConfig != null
                    && ownerDobj.getVh_class() != 0 && purCd != 0 && !CommonUtils.isNullOrBlank(ownerDobj.getRegn_type()) && !CommonUtils.isNullOrBlank(vehParameters.getAPPL_DATE())) {
                if (TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE.equals(ownerDobj.getRegn_type()) || TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE.equals(ownerDobj.getRegn_type())
                        || !(purCd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || purCd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE
                        || purCd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE || purCd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG)) {
                    return true;
                }
                if (",15,17,19,20,21,22,23,".contains("," + ownerDobj.getNorms() + ",")) {
//                    if ((ownerDobj.getNorms() != 15 && "Y".equals(ownerDobj.getImported_vch())) || (ownerDobj.getNorms() == 15)) {
//                        return true;
//                    }
                    return true;
                } else if (",5,6,12,13,14,18,".contains("," + ownerDobj.getNorms() + ",")) {
                    if (",8,9,10,11,12,13,14,20,21,22,24,25,26,27,29,62,63,80,81,83,88,90,92,".contains("," + ownerDobj.getVh_class() + ",") || (ownerDobj.getPurchase_dt().before(DateUtil.parseDate("01-04-2017")) && purCd == TableConstants.ADMIN_OWNER_DATA_CHANGE)) {
                        return true;
                    }
//                } else if (",4,10,16,".contains("," + ownerDobj.getNorms() + ",") && isCondition(replaceTagValues(tmConfig.getNormsConditionFormulas(), vehParameters), "validateVehicleNorms()") && new Date().before(DateUtil.parseDate("01-05-2020"))) {
//                    return true;
                } else if (ownerDobj.getPurchase_dt().before(DateUtil.parseDate("01-04-2017")) && purCd == TableConstants.ADMIN_OWNER_DATA_CHANGE) {
                    return true;
                } else if (ownerDobj.getNorms() == 99) {
                    if (",4,99,".contains("," + ownerDobj.getFuel() + ",")) {
                        return true;
                    }
                } else if (TableConstants.VM_REGN_TYPE_EXARMY.equals(ownerDobj.getRegn_type()) || TableConstants.VM_REGN_TYPE_CD.equals(ownerDobj.getRegn_type()) || ownerDobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_CONFISCATED_AUCTION)) {
                    if (ownerDobj.getAuctionDobj() != null && ownerDobj.getAuctionDobj().getRegnNo() != null) {
                        if (!ownerDobj.getAuctionDobj().getRegnNo().equals("NEW") || (ownerDobj.getAuctionDobj().getRegnNo().equals("NEW") && checkChassisInVtOwnerTemp(ownerDobj.getChasi_no()))) {
                            return true;
                        }
                    }
                    if (ownerDobj.getManu_mon() != 0 && ownerDobj.getManu_yr() != 0) {
                        String month = (ownerDobj.getManu_mon() + "").length() < 2 ? "0" + ownerDobj.getManu_mon() : ownerDobj.getManu_mon() + "";
                        if (Integer.parseInt(ownerDobj.getManu_yr() + "" + month) < 202001) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        throw new VahanException("Vehicle Manufacturing Month/Year should not be blank !!");
                    }
                } else if (",4,10,16,".contains("," + ownerDobj.getNorms() + ",")) {
                    String reason = checkBSIVChassisAllowedForReg(ownerDobj.getChasi_no());
                    if (!CommonUtils.isNullOrBlank(reason)) {
                        if (ownerDobj.getPurchase_dt().before(DateUtil.parseDate("01-04-2020"))) {
                            return true;
                        } else {
                            throw new VahanException("As per Supreme Court Order the Vehicle must be sold before 31-Mar-2020, so Purchase Date for this vehicle should be before 31-Mar-2020.");
                        }
                    }
                    if (ownerDobj.getPurchase_dt().before(DateUtil.parseDate("01-04-2020"))) {
                        if (DateUtil.parseDateFromYYYYMMDD(vehParameters.getAPPL_DATE()).before(DateUtil.parseDate("01-05-2020"))
                                && ((TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE == purCd
                                || TableConstants.VM_TRANSACTION_MAST_TEMP_REG == purCd)
                                || (TableConstants.VM_REGN_TYPE_NEW.equals(ownerDobj.getRegn_type())
                                && (TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE == purCd
                                || TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE == purCd)))) {
                            int payPurCd = (purCd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE
                                    || TableConstants.VM_TRANSACTION_MAST_TEMP_REG == purCd) ? TableConstants.VM_TRANSACTION_MAST_TEMP_REG : TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE;
                            String isPayDone = OfficeCorrectionImpl.getRcptNoForFeePaidForPurpose(ownerDobj.getAppl_no(), payPurCd);
                            if (!CommonUtils.isNullOrBlank(isPayDone)) {
                                vehParameters.setISPAYMENTDONE("TRUE");
                            } else {
                                vehParameters.setISPAYMENTDONE("FALSE");
                            }
                            if (isCondition(replaceTagValues(tmConfig.getNormsConditionFormulas(), vehParameters), "validateVehicleNorms()")) {
                                return true;
                            } else {
                                throw new VahanException("As per State Transport Department Order, only those BS-IV vehicles are allowed which have already paid Registration Fee / MV Tax.");
                            }
                        } else if ((TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE == purCd || purCd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) && TableConstants.VM_REGN_TYPE_TEMPORARY.equals(ownerDobj.getRegn_type())) {
                            return true;
                        }
                    }
                }
            } else {
                throw new VahanException("Vehicle details not found to validate emission norms.");
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " validateVehicleNorms " + e.getStackTrace()[0]);
            throw new VahanException("Problem in validating vehicle emission Norms.");
        }
        return false;
    }

    public static void saveVaOwnerDisclaimerDetails(Owner_dobj dobj, TransactionManager tmgr, String empCd, int actionCd) throws VahanException {
        PreparedStatement ps = null;
        try {
            if (dobj != null && !CommonUtils.isNullOrBlank(dobj.getRegn_type()) && ",4,10,16,".contains("," + dobj.getNorms() + ",") && (TableConstants.VM_REGN_TYPE_NEW.equals(dobj.getRegn_type()) || TableConstants.VM_REGN_TYPE_EXARMY.equals(dobj.getRegn_type())
                    || TableConstants.VM_REGN_TYPE_TEMPORARY.equals(dobj.getRegn_type()) || TableConstants.VM_REGN_TYPE_CD.equals(dobj.getRegn_type()))) {
                String sql = "INSERT INTO va_owner_disclaimer(state_cd, off_cd, appl_no, action_cd, emp_cd, op_dt)\n"
                        + "    VALUES (?, ?, ?, ?, ?, current_timestamp);";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getState_cd());
                ps.setInt(2, dobj.getOff_cd());
                ps.setString(3, dobj.getAppl_no());
                ps.setInt(4, actionCd);
                ps.setInt(5, Integer.parseInt(empCd));
                ServerUtility.validateQueryResult(tmgr, ps.executeUpdate(), ps);
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " saveVaOwnerDisclaimerDetails " + e.getStackTrace()[0]);
            throw new VahanException("Problem in saving owner disclaimer.");
        }
    }

    public static String sha256hex(String passwd) {
        String rtnSHa = "";
        if (passwd != null && !passwd.equalsIgnoreCase("")) {
            try {
                rtnSHa = SHA256Hash(passwd);
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return rtnSHa;
    }

    private static String SHA256Hash(String data) {
        String result = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(data.getBytes());
            return bytesToHex(digest.digest());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private static String bytesToHex(byte[] hash) {
        /*
         * javax.xml.bind.DatatypeConverter built-in class to convert byte array to a hexadecimal string
         */
//        return javax.xml.bind.DatatypeConverter.printHexBinary(hash);
        StringBuffer result = new StringBuffer();
        for (byte byt : hash) {
            result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();

    }

    public static String setTempApplDtAsNewApplDT(Status_dobj statusDobj) {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String applDt = DateUtil.convertStringDDMMMYYYYToYYYYMMDD(statusDobj.getAppl_dt());
        try {
            if (statusDobj.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                tmgr = new TransactionManagerReadOnly("setTempApplDtAsNewApplDT");
                String sql = "select to_char(appl_dt, 'yyyy-MM-dd') as applDT from " + TableList.VA_DETAILS + " where appl_no = ? and state_cd= ? and pur_cd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, statusDobj.getAppl_no());
                ps.setString(2, statusDobj.getState_cd());
                ps.setInt(3, TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE);
                RowSet rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    applDt = rs.getString("applDT");
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return applDt;
    }

    public static void validateMinInsuranceValidity(int insPeriodYears, int vhClass, String regnType, int purCd, int insType) throws VahanException {
        if (Integer.parseInt(TableConstants.INS_TYPE_NA) != insType && (purCd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || purCd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE && (TableConstants.VM_REGN_TYPE_NEW.equals(regnType) || TableConstants.VM_REGN_TYPE_TEMPORARY.equals(regnType)))) {
            if (vhClass > 0 && insPeriodYears > 0) {
                String[] dataVhClass = MasterTableFiller.masterTables.VM_VH_CLASS.getRow(vhClass + "");
                if (dataVhClass != null && dataVhClass.length > 0 && !CommonUtils.isNullOrBlank(dataVhClass[5]) && Integer.parseInt(dataVhClass[5]) > 0) {
                    if (insType != TableConstants.INS_TYPE_THIRD_PARTY) {
                        throw new VahanException("Please select Insurance Type as 'Third Party' only.");
                    } else if (insPeriodYears < Integer.parseInt(dataVhClass[5])) {
                        throw new VahanException("As per order dated 29-08-2018, the MoRTH has directed that the Third Party Insurance Cover of New Cars and Two Wheelers should mandatorily be for a period of Three Years and Five Years respectively. This may be taken and treated as a separate product. Please select Insurance Type as 'Third Party' and Insurance Period accordingly as per the Vehicle Class.");
                    }
                }
            } else {
                throw new VahanException("Invalid insurance data, please check insurance details !!!");
            }
        }
    }

    public static String checkBSIVChassisAllowedForReg(String chasiNo) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        RowSet rs;
        TransactionManagerReadOnly tmgr = null;
        try {
            if (!CommonUtils.isNullOrBlank(chasiNo)) {
                tmgr = new TransactionManagerReadOnly("checkBSIVChassisAllowedForReg");
                sql = "select reason from " + TableList.VT_BSIV_CHASSIS_ALLOWED_BY_SC_ORDER + " WHERE chasi_no = ? and (new_regn_appl_no is null OR temp_regn_appl_no is null) ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, chasiNo);
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    return rs.getString("reason");
                }
            } else {
                throw new VahanException("Chassis number found blank !!!");
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in validating BS-IV chassis number !!!");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (ps != null) {
                    ps = null;
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return null;
    }

    public static void updateApplNoOfBSIVChassis(Owner_dobj owner_dobj, TransactionManager tmgr, int purCd) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        try {
            if (owner_dobj != null && !CommonUtils.isNullOrBlank(owner_dobj.getChasi_no()) && !CommonUtils.isNullOrBlank(owner_dobj.getAppl_no()) && purCd != 0 && owner_dobj.getNorms() != 0
                    && !CommonUtils.isNullOrBlank(owner_dobj.getRegn_type())) {
                sql = "select chasi_no from " + TableList.VT_BSIV_CHASSIS_ALLOWED_BY_SC_ORDER + " where chasi_no =?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, owner_dobj.getChasi_no());
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    if ((TableConstants.VM_REGN_TYPE_NEW.equals(owner_dobj.getRegn_type()) || TableConstants.VM_REGN_TYPE_TEMPORARY.equals(owner_dobj.getRegn_type())) && ",4,10,16,".contains("," + owner_dobj.getNorms() + ",")) {
                        if (TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE == purCd || TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE == purCd) {
                            sql = "UPDATE " + TableList.VT_BSIV_CHASSIS_ALLOWED_BY_SC_ORDER + " SET new_regn_appl_no = ? WHERE chasi_no = ? and new_regn_appl_no is null ";
                        } else if (TableConstants.VM_TRANSACTION_MAST_TEMP_REG == purCd || TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE == purCd) {
                            sql = "UPDATE " + TableList.VT_BSIV_CHASSIS_ALLOWED_BY_SC_ORDER + " SET temp_regn_appl_no = ? WHERE chasi_no = ? and temp_regn_appl_no is null ";
                        }
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, owner_dobj.getAppl_no());
                        ps.setString(2, owner_dobj.getChasi_no());
                        int i = ps.executeUpdate();
                        if (i <= 0) {
                            throw new VahanException("As per the Supreme Court Order either this vehicle is not permitted to register as BS-IV or it is already registered.");
                        }
                    }
                }
            } else {
                throw new VahanException("Chassis/Application No details for BS-IV not found !!!");
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in updating BS-IV chassis number details !!!");
        }
    }

    public static String getRegnColorCode(Owner_dobj dobj, int purCd) {
        String regnColor = "";
        try {
            if (dobj != null) {
                if (TableConstants.VM_REGN_TYPE_TEMPORARY.equals(dobj.getRegn_type()) && purCd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) {
                    return "vehicle-temporary-registration";
                }
                if (dobj.getVehType() == TableConstants.VM_VEHTYPE_TRANSPORT) {
                    if (dobj.getFuel() == TableConstants.VM_FUEL_TYPE_ELECTRIC) {
                        return "vehicle-transport-battery-operated";
                    }
                    return "vehicle-transport-normal";
                }
                if (dobj.getVehType() == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
                    if (dobj.getFuel() == TableConstants.VM_FUEL_TYPE_ELECTRIC) {
                        return "vehicle-non-transport-battery-operated";
                    }
                    return "vehicle-non-transport-normal";
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return regnColor;
    }

    public static boolean insertIntoDscRegistrationHistory() {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        boolean isDataExist = true;
        try {
            tmgr = new TransactionManager("insertIntoDscRegistrationHistory");
            sql = "INSERT INTO " + TableList.VH_DSC_INFORMATION
                    + " SELECT current_timestamp as moved_on, ? as moved_by ,* "
                    + "  FROM  " + TableList.VT_DSC_INFORMATION
                    + " WHERE  user_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setLong(2, Long.parseLong(Util.getEmpCode()));
            ps.executeUpdate();

            sql = "DELETE FROM " + TableList.VT_DSC_INFORMATION + " WHERE user_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(Util.getEmpCode()));
            ps.executeUpdate();

            tmgr.commit();
            isDataExist = false;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return isDataExist;
    }

    public static String getTaxModeFromReqdTaxMode(String reqdTaxMode) {
        String taxMode = null;
        if (!CommonUtils.isNullOrBlank(reqdTaxMode)) {
            if (reqdTaxMode.contains(",")) {
                String[] str = reqdTaxMode.split(",");
                for (String sd : str) {
                    if (sd.contains("58")) {
                        taxMode = sd.substring(3);
                        break;
                    }
                }
            } else if (reqdTaxMode.contains("58")) {
                taxMode = reqdTaxMode.substring(3);
            }
        }
        return taxMode;
    }

    public static String getConditionFormulaByAction(String stateCd, String action, int purCd) {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        RowSet rs = null;
        String sql = null;
        String conditions = "<61> = 123 AND <42> IN ('N')";
        try {
            tmgr = new TransactionManagerReadOnly("getConditionFormulaByAction");
            sql = "SELECT condition_formula from " + TableList.VC_ACTION_PURPOSE_MAP + " where state_cd = ? and action=? and pur_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setString(2, action);
            ps.setInt(3, purCd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                return conditions = rs.getString("condition_formula");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return conditions;
    }

    public static ArrayList<Status_dobj> applicationStatusOnline(String appl_no, String state_cd) throws SQLException, Exception {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        ArrayList<Status_dobj> list = new ArrayList<>();
        if (appl_no != null && state_cd != null) {
            try {
                tmgr = new TransactionManagerReadOnly("applicationStatusOnline");
                sql = "SELECT appl_no, state_cd, off_cd, pur_cd, regn_no, op_dt FROM onlineschema.va_draft_appl where appl_no=? and state_cd=? ORDER BY op_dt desc";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, appl_no);
                ps.setString(2, state_cd.toUpperCase());
                RowSet rs = tmgr.fetchDetachedRowSet();
                while (rs.next()) {
                    Status_dobj status_dobj = new Status_dobj();
                    status_dobj.setAppl_no(rs.getString("appl_no"));
                    status_dobj.setRegn_no(rs.getString("regn_no"));
                    status_dobj.setPur_cd(rs.getInt("pur_cd"));
                    status_dobj.setState_cd(rs.getString("state_cd"));
                    status_dobj.setOff_cd(rs.getInt("off_cd"));
                    list.add(status_dobj);
                }

            } finally {
                try {
                    if (tmgr != null) {
                        tmgr.release();
                    }
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return list;
    }

    public static void insertForQRDetails(String appl_no, String regn_no, String chasi_no, String rcpt_no, boolean cancel_rcpt, int doc_type, String state_cd, int off_cd, TransactionManager tmgr) throws VahanException {
        RowSet rs = null;
        QrCodeDobj qrDobj = new QrCodeDobj();
        qrDobj.setOffCD(off_cd);
        qrDobj.setStateCD(state_cd);
        qrDobj.setApplNO(appl_no);
        qrDobj.setRegnNO(regn_no);
        qrDobj.setChasiNo(chasi_no);
        try {

            //1, 3, 4, 5, 6, 7, 9, 10, 11, 12, 15, 16, 17, 88, 91, 123, 331
            if (doc_type == DocumentType.RC_QR) {
                String qrhash = Util.getEncriptString(doc_type);
                qrDobj.setQrhash(qrhash);
                ServerUtility.insertAndMoveToHistoryRCQRDetails(qrDobj, tmgr);
                return;
            }
            if (doc_type == DocumentType.NOC_QR) {
                String qrhash = Util.getEncriptString(doc_type);
                qrDobj.setQrhash(qrhash);
                ServerUtility.insertAndMoveToHistoryNOCQRDetails(qrDobj, tmgr);
                return;
            }
            if (doc_type == DocumentType.FC_QR_38) {
                String qrhash = Util.getEncriptString(doc_type);
                qrDobj.setFormName(DocumentType.FORM_38_QR);
                qrDobj.setQrhash(qrhash);
                ServerUtility.insertAndMoveToHistoryFCQRDetails(qrDobj, tmgr);
                return;
            }
            if (doc_type == DocumentType.FC_QR_38A) {
                String qrhash = Util.getEncriptString(doc_type);
                qrDobj.setFormName(DocumentType.FORM_38A_QR);
                qrDobj.setQrhash(qrhash);
                ServerUtility.insertAndMoveToHistoryFCQRDetails(qrDobj, tmgr);
                return;
            }
            if (doc_type == DocumentType.RECEIPT_QR && rcpt_no != null && !cancel_rcpt) {
                String qrhash = Util.getEncriptString(doc_type);
                qrDobj.setReceiptNo(rcpt_no);
                qrDobj.setQrhash(qrhash);
                ServerUtility.insertQRReceiptDetails(qrDobj, tmgr);
                return;
            }
            if (doc_type == DocumentType.RCPT_CANCEL_QR && rcpt_no != null && cancel_rcpt) {
                qrDobj.setReceiptNo(rcpt_no);
                ServerUtility.moveToHistoryOfReceiptCancelDetails(qrDobj, tmgr);
                return;
            }
            if (doc_type == DocumentType.RC_CANCEL_QR) {
                ServerUtility.moveToHistoryRCCancelQRDetails(qrDobj, tmgr);
                return;
            }
            if (doc_type == DocumentType.NOC_CANCEL_QR) {
                ServerUtility.moveToHistoryAtNOCCancelQRDetails(qrDobj, tmgr);
                return;
            } else {
                throw new VahanException("Invalid Document Type , Please try with valid Document Type.");
            }

        } catch (VahanException vex) {
            throw vex;
        }
    }

    /*
     * @author Kartikey Singh
     */
    public static void insertForQRDetails(String appl_no, String regn_no, String chasi_no, String rcpt_no, boolean cancel_rcpt, int doc_type, String state_cd, int off_cd, TransactionManager tmgr, String empCode) throws VahanException {
        RowSet rs = null;
        QrCodeDobj qrDobj = new QrCodeDobj();
        qrDobj.setOffCD(off_cd);
        qrDobj.setStateCD(state_cd);
        qrDobj.setApplNO(appl_no);
        qrDobj.setRegnNO(regn_no);
        qrDobj.setChasiNo(chasi_no);
        try {

            //1, 3, 4, 5, 6, 7, 9, 10, 11, 12, 15, 16, 17, 88, 91, 123, 331
            if (doc_type == DocumentType.RC_QR) {
                String qrhash = Util.getEncriptString(doc_type);
                qrDobj.setQrhash(qrhash);
                ServerUtility.insertAndMoveToHistoryRCQRDetails(qrDobj, tmgr, empCode);
                return;
            }
            if (doc_type == DocumentType.NOC_QR) {
                String qrhash = Util.getEncriptString(doc_type);
                qrDobj.setQrhash(qrhash);
                ServerUtility.insertAndMoveToHistoryNOCQRDetails(qrDobj, tmgr, empCode);
                return;
            }
            if (doc_type == DocumentType.FC_QR_38) {
                String qrhash = Util.getEncriptString(doc_type);
                qrDobj.setFormName(DocumentType.FORM_38_QR);
                qrDobj.setQrhash(qrhash);
                ServerUtility.insertAndMoveToHistoryFCQRDetails(qrDobj, tmgr, empCode);
                return;
            }
            if (doc_type == DocumentType.FC_QR_38A) {
                String qrhash = Util.getEncriptString(doc_type);
                qrDobj.setFormName(DocumentType.FORM_38A_QR);
                qrDobj.setQrhash(qrhash);
                ServerUtility.insertAndMoveToHistoryFCQRDetails(qrDobj, tmgr, empCode);
                return;
            }
            if (doc_type == DocumentType.RECEIPT_QR && rcpt_no != null && !cancel_rcpt) {
                String qrhash = Util.getEncriptString(doc_type);
                qrDobj.setReceiptNo(rcpt_no);
                qrDobj.setQrhash(qrhash);
                ServerUtility.insertQRReceiptDetails(qrDobj, tmgr, empCode);
                return;
            }
            if (doc_type == DocumentType.RCPT_CANCEL_QR && rcpt_no != null && cancel_rcpt) {
                qrDobj.setReceiptNo(rcpt_no);
                ServerUtility.moveToHistoryOfReceiptCancelDetails(qrDobj, tmgr, empCode);
                return;
            }
            if (doc_type == DocumentType.RC_CANCEL_QR) {
                ServerUtility.moveToHistoryRCCancelQRDetails(qrDobj, tmgr, empCode);
                return;
            }
            if (doc_type == DocumentType.NOC_CANCEL_QR) {
                ServerUtility.moveToHistoryAtNOCCancelQRDetails(qrDobj, tmgr, empCode);
                return;
            } else {
                throw new VahanException("Invalid Document Type , Please try with valid Document Type.");
            }

        } catch (VahanException vex) {
            throw vex;
        }
    }

    public static String getQRURL(String regnOrRcptOrPmtNO, int dock_type, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        String qrurl = null;
        String sql = null;
        RowSet rs = null;
        String tableName = null;
        String whereClause = null;
        String vahanExpMsg = null;
        try {
            switch (dock_type) {
                case DocumentType.RC_QR:
                    if (!CommonUtils.isNullOrBlank(regnOrRcptOrPmtNO)) {
                        tableName = TableList.VT_RC_QR;
                        whereClause = "regn_no = '" + regnOrRcptOrPmtNO + "'";
                        break;
                    } else {
                        vahanExpMsg = "Details not found for Registration No " + regnOrRcptOrPmtNO;
                    }
                case DocumentType.RECEIPT_QR:
                    if (!CommonUtils.isNullOrBlank(regnOrRcptOrPmtNO)) {
                        tableName = TableList.VT_RECEIPT_QR;
                        whereClause = "rcpt_no = '" + regnOrRcptOrPmtNO + "'";
                        break;
                    } else {
                        vahanExpMsg = "Details not found for Receipt No " + regnOrRcptOrPmtNO;
                    }
                case DocumentType.FC_QR_38:
                case DocumentType.FC_QR_38A:
                    if (!CommonUtils.isNullOrBlank(regnOrRcptOrPmtNO)) {
                        tableName = TableList.VT_FC_QR;
                        whereClause = "appl_no = '" + regnOrRcptOrPmtNO + "'";
                        break;
                    } else {
                        vahanExpMsg = "Details not found for Application No " + regnOrRcptOrPmtNO;
                    }

                case DocumentType.NOC_QR:
                    if (!CommonUtils.isNullOrBlank(regnOrRcptOrPmtNO)) {
                        tableName = TableList.VT_NOC_QR;
                        whereClause = "regn_no = '" + regnOrRcptOrPmtNO + "'";
                        break;
                    } else {
                        vahanExpMsg = "Details not found for Registration/Application No " + regnOrRcptOrPmtNO;

                    }
                default:
                    vahanExpMsg = "Details not found for Registration/Application no " + regnOrRcptOrPmtNO + " with dock type " + dock_type + ". Please try after sometime!";

            }
            if (vahanExpMsg != null) {
                throw new VahanException(vahanExpMsg);
            }
            sql = "select qrhash from " + tableName + " WHERE " + whereClause;
            ps = tmgr.prepareStatement(sql);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (rs.getString("qrhash") != null && !rs.getString("qrhash").isEmpty()) {
                    qrurl = "https://qr.parivahan.gov.in/vq/qr?v=".concat(rs.getString("qrhash"));
                    //qrurl = "https://staging.parivahan.gov.in/vq/qr?v=".concat(rs.getString("qrhash"));
                }
            }
            return qrurl;
        } catch (VahanException ve) {
            throw ve;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem occurred during getting the QR details.");
        }
    }

    public static void insertAndMoveToHistoryRCQRDetails(QrCodeDobj qrDobj, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        RowSet rs = null;
        long emp_cd = Long.parseLong(Util.getEmpCode());
        try {
            if (qrDobj != null && !CommonUtils.isNullOrBlank(qrDobj.getApplNO()) && !CommonUtils.isNullOrBlank(qrDobj.getRegnNO())) {
                sql = "select * from " + TableList.VT_RC_QR + " WHERE regn_no = ? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, qrDobj.getRegnNO());
                rs = tmgr.fetchDetachedRowSetWithoutTrim_No_release();
                if (rs.next()) {
                    sql = "INSERT INTO " + TableList.VH_RC_QR
                            + " select *,current_timestamp,? "
                            + " from " + TableList.VT_RC_QR + " where regn_no = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setLong(1, emp_cd);
                    ps.setString(2, qrDobj.getRegnNO());
                    ps.executeUpdate();

                    sql = "DELETE FROM " + TableList.VT_RC_QR + " where regn_no = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, qrDobj.getRegnNO());
                    ps.executeUpdate();

                }
                sql = "INSERT INTO " + TableList.VT_RC_QR + "(state_cd,off_cd,appl_no,regn_no,qrhash, op_dt)\n"
                        + " VALUES (?,?,?,?,?,current_timestamp)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, qrDobj.getStateCD());
                ps.setInt(2, qrDobj.getOffCD());
                ps.setString(3, qrDobj.getApplNO());
                ps.setString(4, qrDobj.getRegnNO());
                ps.setString(5, qrDobj.getQrhash());
                ps.executeUpdate();

            } else {
                throw new VahanException("Registration / Application No should not be blank.");
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem occurred during inserting the QR data for RC details.");
        }
    }

    /*
     * @author Kartikey Singh
     */
    public static void insertAndMoveToHistoryRCQRDetails(QrCodeDobj qrDobj, TransactionManager tmgr, String empCode) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        RowSet rs = null;
        long emp_cd = Long.parseLong(empCode);
        try {
            if (qrDobj != null && !CommonUtils.isNullOrBlank(qrDobj.getApplNO()) && !CommonUtils.isNullOrBlank(qrDobj.getRegnNO())) {
                sql = "select * from " + TableList.VT_RC_QR + " WHERE regn_no = ? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, qrDobj.getRegnNO());
                rs = tmgr.fetchDetachedRowSetWithoutTrim_No_release();
                if (rs.next()) {
                    sql = "INSERT INTO " + TableList.VH_RC_QR
                            + " select *,current_timestamp,? "
                            + " from " + TableList.VT_RC_QR + " where regn_no = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setLong(1, emp_cd);
                    ps.setString(2, qrDobj.getRegnNO());
                    ps.executeUpdate();

                    sql = "DELETE FROM " + TableList.VT_RC_QR + " where regn_no = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, qrDobj.getRegnNO());
                    ps.executeUpdate();

                }
                sql = "INSERT INTO " + TableList.VT_RC_QR + "(state_cd,off_cd,appl_no,regn_no,qrhash, op_dt)\n"
                        + " VALUES (?,?,?,?,?,current_timestamp)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, qrDobj.getStateCD());
                ps.setInt(2, qrDobj.getOffCD());
                ps.setString(3, qrDobj.getApplNO());
                ps.setString(4, qrDobj.getRegnNO());
                ps.setString(5, qrDobj.getQrhash());
                ps.executeUpdate();

            } else {
                throw new VahanException("Registration / Application No should not be blank.");
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem occurred during inserting the QR data for RC details.");
        }
    }

    public static void moveToHistoryRCCancelQRDetails(QrCodeDobj qrDobj, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        RowSet rs = null;
        long emp_cd = Long.parseLong(Util.getEmpCode());
        try {
            if (qrDobj != null && !CommonUtils.isNullOrBlank(qrDobj.getApplNO()) && !CommonUtils.isNullOrBlank(qrDobj.getRegnNO())) {
                sql = "select * from " + TableList.VT_RC_QR + " WHERE regn_no = ? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, qrDobj.getRegnNO());
                rs = tmgr.fetchDetachedRowSetWithoutTrim_No_release();
                if (rs.next()) {
                    sql = "INSERT INTO " + TableList.VT_RC_CANCEL_QR
                            + " select *,current_timestamp,? "
                            + " from " + TableList.VT_RC_QR + " where regn_no = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setLong(1, emp_cd);
                    ps.setString(2, qrDobj.getRegnNO());
                    ps.executeUpdate();
                }

            } else {
                throw new VahanException("Registration / Application No should not be blank.");
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem occurred during inserting the QR data for RC details.");
        }
    }

    /*
     * @author Kartikey Singh
     */
    public static void moveToHistoryRCCancelQRDetails(QrCodeDobj qrDobj, TransactionManager tmgr, String empCode) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        RowSet rs = null;
        long emp_cd = Long.parseLong(empCode);
        try {
            if (qrDobj != null && !CommonUtils.isNullOrBlank(qrDobj.getApplNO()) && !CommonUtils.isNullOrBlank(qrDobj.getRegnNO())) {
                sql = "select * from " + TableList.VT_RC_QR + " WHERE regn_no = ? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, qrDobj.getRegnNO());
                rs = tmgr.fetchDetachedRowSetWithoutTrim_No_release();
                if (rs.next()) {
                    sql = "INSERT INTO " + TableList.VT_RC_CANCEL_QR
                            + " select *,current_timestamp,? "
                            + " from " + TableList.VT_RC_QR + " where regn_no = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setLong(1, emp_cd);
                    ps.setString(2, qrDobj.getRegnNO());
                    ps.executeUpdate();
                }

            } else {
                throw new VahanException("Registration / Application No should not be blank.");
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem occurred during inserting the QR data for RC details.");
        }
    }

    public static void insertQRReceiptDetails(QrCodeDobj qrdobj, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        RowSet rs = null;
        long emp_cd = Long.parseLong(Util.getEmpCode());
        try {
            if (qrdobj != null && !CommonUtils.isNullOrBlank(qrdobj.getApplNO()) && !CommonUtils.isNullOrBlank(qrdobj.getReceiptNo())) {
                sql = "INSERT INTO " + TableList.VT_RECEIPT_QR + "(state_cd,off_cd,appl_no,rcpt_no,qrhash)\n"
                        + " VALUES (?,?,?,?,?)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, qrdobj.getStateCD());
                ps.setInt(2, qrdobj.getOffCD());
                ps.setString(3, qrdobj.getApplNO());
                ps.setString(4, qrdobj.getReceiptNo());
                ps.setString(5, qrdobj.getQrhash());
                ps.executeUpdate();

            } else {
                throw new VahanException("Receipt No should not be blank.");
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem occurred during inserting the QR data for Receipt details.");
        }
    }

    /*
     * @author Kartikey Singh
     */
    public static void insertQRReceiptDetails(QrCodeDobj qrdobj, TransactionManager tmgr, String empCode) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        RowSet rs = null;
        long emp_cd = Long.parseLong(empCode);
        try {
            if (qrdobj != null && !CommonUtils.isNullOrBlank(qrdobj.getApplNO()) && !CommonUtils.isNullOrBlank(qrdobj.getReceiptNo())) {
                sql = "INSERT INTO " + TableList.VT_RECEIPT_QR + "(state_cd,off_cd,appl_no,rcpt_no,qrhash)\n"
                        + " VALUES (?,?,?,?,?)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, qrdobj.getStateCD());
                ps.setInt(2, qrdobj.getOffCD());
                ps.setString(3, qrdobj.getApplNO());
                ps.setString(4, qrdobj.getReceiptNo());
                ps.setString(5, qrdobj.getQrhash());
                ps.executeUpdate();

            } else {
                throw new VahanException("Receipt No should not be blank.");
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem occurred during inserting the QR data for Receipt details.");
        }
    }

    public static void moveToHistoryOfReceiptCancelDetails(QrCodeDobj qrdobj, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        RowSet rs = null;
        long emp_cd = Long.parseLong(Util.getEmpCode());
        try {
            if (qrdobj != null && !CommonUtils.isNullOrBlank(qrdobj.getApplNO()) && !CommonUtils.isNullOrBlank(qrdobj.getReceiptNo())) {
                sql = "select * from " + TableList.VT_RECEIPT_QR + " WHERE rcpt_no = ? and state_cd = ? and off_cd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, qrdobj.getReceiptNo());
                ps.setString(2, qrdobj.getStateCD());
                ps.setInt(3, qrdobj.getOffCD());
                rs = tmgr.fetchDetachedRowSetWithoutTrim_No_release();
                if (rs.next()) {
                    sql = "INSERT INTO " + TableList.VH_RECEIPT_QR
                            + " select *,current_timestamp,? "
                            + " from " + TableList.VT_RECEIPT_QR + " where rcpt_no=? and state_cd = ? and off_cd = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setLong(1, emp_cd);
                    ps.setString(2, qrdobj.getReceiptNo());
                    ps.setString(3, qrdobj.getStateCD());
                    ps.setInt(4, qrdobj.getOffCD());
                    ps.executeUpdate();

                    sql = "DELETE FROM " + TableList.VT_RECEIPT_QR + " where rcpt_no=? and state_cd = ? and off_cd = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, qrdobj.getReceiptNo());
                    ps.setString(2, qrdobj.getStateCD());
                    ps.setInt(3, qrdobj.getOffCD());
                    ps.executeUpdate();
                }
            } else {
                throw new VahanException("Receipt No should not be blank.");
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem occurred during inserting the QR data for Receipt cancel details.");
        }
    }

    /*
     * @author Kartikey Singh
     */
    public static void moveToHistoryOfReceiptCancelDetails(QrCodeDobj qrdobj, TransactionManager tmgr, String empCode) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        RowSet rs = null;
        long emp_cd = Long.parseLong(empCode);
        try {
            if (qrdobj != null && !CommonUtils.isNullOrBlank(qrdobj.getApplNO()) && !CommonUtils.isNullOrBlank(qrdobj.getReceiptNo())) {
                sql = "select * from " + TableList.VT_RECEIPT_QR + " WHERE rcpt_no = ? and state_cd = ? and off_cd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, qrdobj.getReceiptNo());
                ps.setString(2, qrdobj.getStateCD());
                ps.setInt(3, qrdobj.getOffCD());
                rs = tmgr.fetchDetachedRowSetWithoutTrim_No_release();
                if (rs.next()) {
                    sql = "INSERT INTO " + TableList.VH_RECEIPT_QR
                            + " select *,current_timestamp,? "
                            + " from " + TableList.VT_RECEIPT_QR + " where rcpt_no=? and state_cd = ? and off_cd = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setLong(1, emp_cd);
                    ps.setString(2, qrdobj.getReceiptNo());
                    ps.setString(3, qrdobj.getStateCD());
                    ps.setInt(4, qrdobj.getOffCD());
                    ps.executeUpdate();

                    sql = "DELETE FROM " + TableList.VT_RECEIPT_QR + " where rcpt_no=? and state_cd = ? and off_cd = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, qrdobj.getReceiptNo());
                    ps.setString(2, qrdobj.getStateCD());
                    ps.setInt(3, qrdobj.getOffCD());
                    ps.executeUpdate();
                }
            } else {
                throw new VahanException("Receipt No should not be blank.");
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem occurred during inserting the QR data for Receipt cancel details.");
        }
    }

    public static void insertAndMoveToHistoryFCQRDetails(QrCodeDobj qrdobj, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        RowSet rs = null;
        long emp_cd = Long.parseLong(Util.getEmpCode());
        try {
            if (qrdobj != null && !CommonUtils.isNullOrBlank(qrdobj.getApplNO()) && !CommonUtils.isNullOrBlank(qrdobj.getRegnNO())) {
                sql = "select * from " + TableList.VT_FC_QR + " WHERE regn_no = ? and chasi_no =? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, qrdobj.getRegnNO());
                ps.setString(2, qrdobj.getChasiNo());
                rs = tmgr.fetchDetachedRowSetWithoutTrim_No_release();
                if (rs.next()) {
                    sql = "INSERT INTO " + TableList.VH_FC_QR
                            + " select *,current_timestamp,? "
                            + " from " + TableList.VT_FC_QR + " where regn_no = ? and chasi_no =?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setLong(1, emp_cd);
                    ps.setString(2, qrdobj.getRegnNO());
                    ps.setString(3, qrdobj.getChasiNo());
                    ps.executeUpdate();

                    sql = "DELETE FROM " + TableList.VT_FC_QR + " where regn_no = ?  and chasi_no =? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, qrdobj.getRegnNO());
                    ps.setString(2, qrdobj.getChasiNo());
                    ps.executeUpdate();
                }

                sql = "INSERT INTO " + TableList.VT_FC_QR + "(state_cd,off_cd,appl_no,regn_no,qrhash, form_name,op_dt,chasi_no)\n"
                        + " VALUES (?,?,?,?,?,?,current_timestamp,?)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, qrdobj.getStateCD());
                ps.setInt(2, qrdobj.getOffCD());
                ps.setString(3, qrdobj.getApplNO());
                ps.setString(4, qrdobj.getRegnNO());
                ps.setString(5, qrdobj.getQrhash());
                ps.setString(6, qrdobj.getFormName());
                ps.setString(7, qrdobj.getChasiNo());
                ps.executeUpdate();
            } else {
                throw new VahanException("Registration / Application No should not be blank.");
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem occurred during inserting the QR data for FC details.");
        }
    }

    /*
     * @author Kartikey Singh
     */
    public static void insertAndMoveToHistoryFCQRDetails(QrCodeDobj qrdobj, TransactionManager tmgr, String empCode) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        RowSet rs = null;
        long emp_cd = Long.parseLong(empCode);
        try {
            if (qrdobj != null && !CommonUtils.isNullOrBlank(qrdobj.getApplNO()) && !CommonUtils.isNullOrBlank(qrdobj.getRegnNO())) {
                sql = "select * from " + TableList.VT_FC_QR + " WHERE regn_no = ? and chasi_no =? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, qrdobj.getRegnNO());
                ps.setString(2, qrdobj.getChasiNo());
                rs = tmgr.fetchDetachedRowSetWithoutTrim_No_release();
                if (rs.next()) {
                    sql = "INSERT INTO " + TableList.VH_FC_QR
                            + " select *,current_timestamp,? "
                            + " from " + TableList.VT_FC_QR + " where regn_no = ? and chasi_no =?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setLong(1, emp_cd);
                    ps.setString(2, qrdobj.getRegnNO());
                    ps.setString(3, qrdobj.getChasiNo());
                    ps.executeUpdate();

                    sql = "DELETE FROM " + TableList.VT_FC_QR + " where regn_no = ?  and chasi_no =? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, qrdobj.getRegnNO());
                    ps.setString(2, qrdobj.getChasiNo());
                    ps.executeUpdate();
                }

                sql = "INSERT INTO " + TableList.VT_FC_QR + "(state_cd,off_cd,appl_no,regn_no,qrhash, form_name,op_dt,chasi_no)\n"
                        + " VALUES (?,?,?,?,?,?,current_timestamp,?)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, qrdobj.getStateCD());
                ps.setInt(2, qrdobj.getOffCD());
                ps.setString(3, qrdobj.getApplNO());
                ps.setString(4, qrdobj.getRegnNO());
                ps.setString(5, qrdobj.getQrhash());
                ps.setString(6, qrdobj.getFormName());
                ps.setString(7, qrdobj.getChasiNo());
                ps.executeUpdate();
            } else {
                throw new VahanException("Registration / Application No should not be blank.");
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem occurred during inserting the QR data for FC details.");
        }
    }

    public static void insertAndMoveToHistoryNOCQRDetails(QrCodeDobj qrdobj, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        RowSet rs = null;
        long emp_cd = Long.parseLong(Util.getEmpCode());
        try {
            if (qrdobj != null && !CommonUtils.isNullOrBlank(qrdobj.getApplNO()) && !CommonUtils.isNullOrBlank(qrdobj.getRegnNO())) {
                sql = "select * from " + TableList.VT_NOC_QR + " WHERE regn_no = ? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, qrdobj.getRegnNO());
                rs = tmgr.fetchDetachedRowSetWithoutTrim_No_release();
                if (rs.next()) {
                    sql = "INSERT INTO " + TableList.VH_NOC_QR
                            + " select *,current_timestamp,? "
                            + " from " + TableList.VT_NOC_QR + " where regn_no = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setLong(1, emp_cd);
                    ps.setString(2, qrdobj.getRegnNO());
                    ps.executeUpdate();

                    sql = "DELETE FROM " + TableList.VT_NOC_QR + " where regn_no = ? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, qrdobj.getRegnNO());
                    ps.executeUpdate();
                }
                sql = "INSERT INTO " + TableList.VT_NOC_QR + "(state_cd,off_cd,appl_no,regn_no,qrhash,op_dt)\n"
                        + " VALUES (?,?,?,?,?,current_timestamp)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, qrdobj.getStateCD());
                ps.setInt(2, qrdobj.getOffCD());
                ps.setString(3, qrdobj.getApplNO());
                ps.setString(4, qrdobj.getRegnNO());
                ps.setString(5, qrdobj.getQrhash());
                ps.executeUpdate();
            } else {
                throw new VahanException("Registration / Application No should not be blank.");
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem occurred during getting the NOC details.");
        }
    }

    /*
     * @author Kartikey Singh
     */
    public static void insertAndMoveToHistoryNOCQRDetails(QrCodeDobj qrdobj, TransactionManager tmgr, String empCode) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        RowSet rs = null;
        long emp_cd = Long.parseLong(empCode);
        try {
            if (qrdobj != null && !CommonUtils.isNullOrBlank(qrdobj.getApplNO()) && !CommonUtils.isNullOrBlank(qrdobj.getRegnNO())) {
                sql = "select * from " + TableList.VT_NOC_QR + " WHERE regn_no = ? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, qrdobj.getRegnNO());
                rs = tmgr.fetchDetachedRowSetWithoutTrim_No_release();
                if (rs.next()) {
                    sql = "INSERT INTO " + TableList.VH_NOC_QR
                            + " select *,current_timestamp,? "
                            + " from " + TableList.VT_NOC_QR + " where regn_no = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setLong(1, emp_cd);
                    ps.setString(2, qrdobj.getRegnNO());
                    ps.executeUpdate();

                    sql = "DELETE FROM " + TableList.VT_NOC_QR + " where regn_no = ? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, qrdobj.getRegnNO());
                    ps.executeUpdate();
                }
                sql = "INSERT INTO " + TableList.VT_NOC_QR + "(state_cd,off_cd,appl_no,regn_no,qrhash,op_dt)\n"
                        + " VALUES (?,?,?,?,?,current_timestamp)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, qrdobj.getStateCD());
                ps.setInt(2, qrdobj.getOffCD());
                ps.setString(3, qrdobj.getApplNO());
                ps.setString(4, qrdobj.getRegnNO());
                ps.setString(5, qrdobj.getQrhash());
                ps.executeUpdate();
            } else {
                throw new VahanException("Registration / Application No should not be blank.");
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem occurred during getting the NOC details.");
        }
    }

    public static void moveToHistoryAtNOCCancelQRDetails(QrCodeDobj qrdobj, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        RowSet rs = null;
        long emp_cd = Long.parseLong(Util.getEmpCode());
        try {
            if (qrdobj != null && !CommonUtils.isNullOrBlank(qrdobj.getApplNO()) && !CommonUtils.isNullOrBlank(qrdobj.getRegnNO())) {
                sql = "select * from " + TableList.VT_NOC_QR + " WHERE regn_no = ? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, qrdobj.getRegnNO());
                rs = tmgr.fetchDetachedRowSetWithoutTrim_No_release();
                if (rs.next()) {
                    sql = "INSERT INTO " + TableList.VT_NOC_CANCEL_QR
                            + " select *,current_timestamp,? "
                            + " from " + TableList.VT_NOC_QR + " where regn_no = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setLong(1, emp_cd);
                    ps.setString(2, qrdobj.getRegnNO());
                    ps.executeUpdate();
                }
            } else {
                throw new VahanException("Registration / Application No should not be blank.");
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem occurred during inserting the QR data for NOC cancel details.");
        }
    }

    /*
     * @author Kartikey Singh
     */
    public static void moveToHistoryAtNOCCancelQRDetails(QrCodeDobj qrdobj, TransactionManager tmgr, String empCode) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        RowSet rs = null;
        long emp_cd = Long.parseLong(empCode);
        try {
            if (qrdobj != null && !CommonUtils.isNullOrBlank(qrdobj.getApplNO()) && !CommonUtils.isNullOrBlank(qrdobj.getRegnNO())) {
                sql = "select * from " + TableList.VT_NOC_QR + " WHERE regn_no = ? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, qrdobj.getRegnNO());
                rs = tmgr.fetchDetachedRowSetWithoutTrim_No_release();
                if (rs.next()) {
                    sql = "INSERT INTO " + TableList.VT_NOC_CANCEL_QR
                            + " select *,current_timestamp,? "
                            + " from " + TableList.VT_NOC_QR + " where regn_no = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setLong(1, emp_cd);
                    ps.setString(2, qrdobj.getRegnNO());
                    ps.executeUpdate();
                }
            } else {
                throw new VahanException("Registration / Application No should not be blank.");
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem occurred during inserting the QR data for NOC cancel details.");
        }
    }

    public static boolean checkChassisInVtOwnerTemp(String chasiNo) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        RowSet rs;
        TransactionManagerReadOnly tmgr = null;
        boolean isChassisExist = false;
        try {
            if (!CommonUtils.isNullOrBlank(chasiNo)) {
                tmgr = new TransactionManagerReadOnly("checkChassisInVtOwnerTemp");
                sql = "SELECT chasi_no,appl_no,temp_regn_no,purpose  FROM " + TableList.VT_OWNER_TEMP + " WHERE chasi_no=? order by op_dt desc limit 1 ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, chasiNo);
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    isChassisExist = true;
                }
            } else {
                throw new VahanException("Chassis number found blank !!!");
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in validating  chassis number !!!");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (ps != null) {
                    ps = null;
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return isChassisExist;
    }
}//end of the ServerUtility class
