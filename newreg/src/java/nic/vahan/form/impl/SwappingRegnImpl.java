/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.SwappingRegnDobj;
import nic.vahan.form.dobj.TmConfigurationSwappingDobj;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author nic
 */
public class SwappingRegnImpl {

    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(SwappingRegnImpl.class);

    public static String[] present_technicalDetail(String regn_no) {
        String current_technical_detail[] = null;

        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("present_c_addressDetails");
            String sql = "select vh_class,vch_catg, 'Vehicle Class ['||vh_class_desc||'], Vehicle Category ['||vch_catg||'], Fitment Date Upto ['||fit_upto||'], Tax Mode ['||tax_mode||']' as current_technical_detail\n"
                    + "from vv_owner inner join vt_tax on vv_owner.regn_no=vt_tax.regn_no and vv_owner.state_cd = vt_tax.state_cd and vv_owner.off_cd = vt_tax.off_cd \n"
                    + "where vv_owner.regn_no=? and vv_owner.state_cd = ? and vv_owner.off_cd = ? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                current_technical_detail = new String[3];
                current_technical_detail[0] = rs.getString("current_technical_detail").replace(",", "&nbsp; <font color=\"red\">|</font> &nbsp;");
                current_technical_detail[1] = rs.getString("vh_class");
                current_technical_detail[2] = rs.getString("vch_catg");
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
        return current_technical_detail;
    }

    public static ArrayList<SwappingRegnDobj> saveDataforInward(SwappingRegnDobj swap_dobj, Status_dobj status) throws VahanException, Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        ArrayList<SwappingRegnDobj> list = null;
        SimpleDateFormat opdtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String appl_no_one = null;
        String appl_no_two = null;
        TransactionManager tmgr = null;
        Exception e = null;

        String sqlInsert = "insert into " + TableList.VA_RETENTION + " (appl_no,link_appl_no,regn_no,order_by,order_no,reason,op_dt,order_dt,state_cd,off_cd,relation_type)"
                + " values(?,?,?,?,?,?,current_timestamp,?,?,?,?)";
        try {
            list = new ArrayList<SwappingRegnDobj>();
            tmgr = new TransactionManager("makeChangeCA");
            appl_no_one = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());//generate a new application no.        
            appl_no_two = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());//generate a new application no.       

            PreparedStatement ps = tmgr.prepareStatement(sqlInsert);
            ps.setString(1, appl_no_one);
            ps.setString(2, appl_no_two);
            ps.setString(3, swap_dobj.getRegnNoOne());
            ps.setString(4, swap_dobj.getOrder_by());
            ps.setString(5, swap_dobj.getOrder_no());
            ps.setString(6, swap_dobj.getReason());
            if (swap_dobj.getOrder_dt() != null) {
                ps.setDate(7, new Date(((java.util.Date) sdf.parse(swap_dobj.getOrder_dt())).getTime()));
            }
            ps.setString(8, Util.getUserStateCode());
            ps.setInt(9, Util.getSelectedSeat().getOff_cd());
            ps.setInt(10, swap_dobj.getRelation_code());
            ps.executeUpdate();

            PreparedStatement pstwo = tmgr.prepareStatement(sqlInsert);
            pstwo.setString(1, appl_no_two);
            pstwo.setString(2, appl_no_one);
            pstwo.setString(3, swap_dobj.getRegnNoTwo());
            pstwo.setString(4, swap_dobj.getOrder_by());
            pstwo.setString(5, swap_dobj.getOrder_no());
            pstwo.setString(6, swap_dobj.getReason());
            if (swap_dobj.getOrder_dt() != null) {
                pstwo.setDate(7, new Date(((java.util.Date) sdf.parse(swap_dobj.getOrder_dt())).getTime()));
            }
            pstwo.setString(8, Util.getUserStateCode());
            pstwo.setInt(9, Util.getSelectedSeat().getOff_cd());
            pstwo.setInt(10, swap_dobj.getRelation_code());
            pstwo.executeUpdate();

            InsertforActionFlow(tmgr, status, appl_no_one, swap_dobj.getRegnNoOne());
            status.setPur_cd(TableConstants.SWAPPING_REGN_PUR_CD);
            status.setFlow_slno(1);//initial flow serial no.
            status.setFile_movement_slno(1);//initial file movement serial no.
            status.setState_cd(Util.getUserStateCode());
            status.setEmp_cd(0);
            status.setSeat_cd("");
            status.setCntr_id("");
            status.setStatus("N");
            status.setOffice_remark("");
            status.setPublic_remark("");
            status.setFile_movement_type("F");
            status.setUser_id(Util.getUserId());
            status.setAppl_date(ServerUtil.getSystemDateInPostgres());
            status.setUser_type("");
            status.setEntry_ip("");
            status.setEntry_status("");//for New File
            status.setConfirm_ip("");
            status.setConfirm_status("");
            status.setConfirm_date(new java.util.Date());

            InsertforActionFlow(tmgr, status, appl_no_two, swap_dobj.getRegnNoTwo());
            swap_dobj.setApplnoOne(appl_no_one);
            swap_dobj.setApplnoTwo(appl_no_two);
            list.add(swap_dobj);
            tmgr.commit();//Commiting data here....
        } catch (VahanException ee) {
            e = ee;
        } catch (ParseException ee) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            e = ee;
        } catch (SQLException ee) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            e = ee;
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ee) {
                LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            }

        }
        if (e != null) {
            throw new VahanException("Error in insertion for Retention after  generated appl no");
        }
        return list;
    }

    public static void InsertforActionFlow(TransactionManager tmgr, Status_dobj status, String appl_no, String regn_no) throws VahanException, Exception {
        int actionCodeArray[] = null;
        int action_cd = 0;

        actionCodeArray = ServerUtil.getInitialAction(tmgr, status.getState_cd(), status.getPur_cd(), null);

        if (actionCodeArray == null) {
            throw new VahanException("Initial Action Code is Not Available!");
        }
        action_cd = actionCodeArray[0];
        status.setAppl_no(appl_no);
        status.setRegn_no(regn_no);
        status.setAction_cd(action_cd);//Initial Action_cd
        status.setAppl_date(ServerUtil.getSystemDateInPostgres());
        ServerUtil.insertIntoVaStatus(tmgr, status);
        ServerUtil.insertIntoVaDetails(tmgr, status);
        status.setStatus(TableConstants.STATUS_COMPLETE);
        status = ServerUtil.webServiceForNextStage(status, tmgr);
        ServerUtil.fileFlow(tmgr, status); //for updateing va_status and vha status for new role,seat for new emp
    }

    public static SwappingRegnDobj setPreSwapRetDobj(String applNo) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        SwappingRegnDobj dobj = null;
        VahanException vahanexecption = null;
        Exception e = null;
        try {
            String sql = "select order_by,order_no,reason,to_char(order_dt,'dd-Mon-yyyy') as order_dt"
                    + " from " + TableList.VA_RETENTION + " where appl_no =?";
            tmgr = new TransactionManager("set Previous SwapRet Dobj");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                dobj = new SwappingRegnDobj();
                dobj.setOrder_by(rs.getString("order_by"));
                dobj.setOrder_no(rs.getString("order_no"));
                dobj.setOrder_dt(rs.getString("order_dt"));
                dobj.setReason(rs.getString("reason"));

            }

        } catch (SQLException ee) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            e = ee;

        } catch (Exception ee) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            e = ee;
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ee) {
                LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            }
        }
        if (e != null) {
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return dobj;
    }

    public static void makeChangeRET(SwappingRegnDobj swap_dobj, String changedata) throws VahanException, Exception {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("makeChangeCA");
            insertUpdateVAVHRetention(tmgr, swap_dobj);
            ComparisonBeanImpl.updateChangedData(swap_dobj.getApplnoOne(), changedata, tmgr);
            tmgr.commit();
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ee) {
                LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            }
        }
    }

    public static SwappingRegnDobj getApplOwnerDetailDBToDobj(String regnNoOne, String regnNoTwo, int actioncd, String chasione, String chasitwo, TmConfigurationSwappingDobj tmConfSwappingDobj) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        SwappingRegnDobj dobj = null;
        VahanException vahanexecption = null;
        Exception e = null;
        boolean isRecordFoundForRegnOne = false;
        boolean isRecordFoundForRegnTwo = false;
        try {
            String sqlforregnNoOne = "select a.status,a.regn_type,a.purchase_dt,a.regn_dt,a.vh_class,a.vch_catg,a.owner_cd,a.regn_no as regnNo,a.owner_name as OwnerName,a.f_name as FName,a.fit_upto,a.regn_upto,a.state_cd,a.c_district,a.op_dt,"
                    + " a.c_add1 , a.c_add2 , a.c_add3, a.c_district_name, a.c_state_name, a.c_pincode,a.dealer_cd,a.state_cd,a.c_district,a.chasi_no,a.eng_no,a.state_cd,a.off_cd,"
                    + " ret.order_by,ret.order_no,ret.reason,to_char(ret.order_dt,'dd-Mon-yyyy') as order_dt,ret.op_dt,ret.appl_no,ret.link_appl_no,hpt.regn_no as hyptregnno,ret.relation_type"
                    + " from " + TableList.VIEW_VV_OWNER + " a "
                    + " left outer join " + TableList.VA_RETENTION + " ret on ret.regn_no= a.regn_no and a.state_cd = ret.state_cd and a.off_cd = ret.off_cd  "
                    + " left outer join " + TableList.VT_HYPTH + " hpt on hpt.regn_no= a.regn_no and hpt.state_cd = a.state_cd and hpt.off_cd = a.off_cd "
                    + " where a.regn_no in (?, ?) and a.state_cd=? and a.off_cd=? ";

            tmgr = new TransactionManager("getApplOwnerDetailDBToDobj");
            ps = tmgr.prepareStatement(sqlforregnNoOne);
            ps.setString(1, regnNoOne);
            ps.setString(2, regnNoTwo);
            ps.setString(3, Util.getUserStateCode());
            ps.setInt(4, Util.getSelectedSeat().getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();
            dobj = new SwappingRegnDobj();
            while (rs.next()) {
                dobj.setTmConfSwappingDobj(tmConfSwappingDobj);
                if (regnNoOne.equalsIgnoreCase(rs.getString("regnNo"))) {
                    isRecordFoundForRegnOne = true;
                    dobj.setIsrendered(true);
                    dobj.setRegnNoOne(regnNoOne.toUpperCase());
                    dobj.setOwnernameone(rs.getString("OwnerName"));
                    dobj.setFathernameone(rs.getString("FName"));
                    dobj.setC_add1One(rs.getString("c_add1"));
                    dobj.setC_add2One(rs.getString("c_add2"));
                    dobj.setC_add3One(rs.getString("c_add3"));
                    dobj.setC_districtOne(rs.getString("c_district_name"));
                    dobj.setC_stateOne(rs.getString("c_state_name"));
                    dobj.setC_districtcodeOne(rs.getInt("c_district"));
                    dobj.setC_statecodeOne(rs.getString("state_cd"));
                    dobj.setC_pincodeOne(rs.getInt("c_pincode"));
                    dobj.setOrder_by(rs.getString("order_by"));
                    dobj.setOrder_no(rs.getString("order_no"));
                    dobj.setOrder_dt(rs.getString("order_dt"));
                    dobj.setReason(rs.getString("reason"));
                    dobj.setOpdate(rs.getString("op_dt"));
                    dobj.setApplnoOne(rs.getString("appl_no"));
                    dobj.setLink_appl_no_one(rs.getString("link_appl_no"));
                    dobj.setVh_class_one(rs.getInt("vh_class"));
                    dobj.setFit_upto(rs.getDate("fit_upto"));
                    dobj.setRegn_dt(rs.getDate("regn_dt"));
                    dobj.setChassi_one(rs.getString("chasi_no"));
                    dobj.setVch_catg_new_vehicle(rs.getString("vch_catg"));
                    dobj.setNewVehicleOwnercd(rs.getInt("owner_cd"));
                    if (dobj.getChassi_one() != null && dobj.getChassi_one().length() > 5) {
                        if (actioncd == 10114 && !dobj.getChassi_one().substring(dobj.getChassi_one().length() - 5).equalsIgnoreCase(chasione)) {
                            throw new VahanException("Chasi no does not matched for " + regnNoOne.toUpperCase());
                        } else {
                            dobj.setChasiOneLastFiveChar(dobj.getChassi_one().substring(dobj.getChassi_one().length() - 5));
                        }
                    }
                    dobj.setEngine_one(rs.getString("eng_no"));
                    dobj.setPurchase_dt(rs.getDate("purchase_dt"));
                    dobj.setRegnTypeforold(rs.getString("regn_type"));
                    if (rs.getString("hyptregnno") != null && !rs.getString("hyptregnno").equals("")) {
                        dobj.setIsHyptRegnNo(true);
                    }
                    dobj.setRegn_upto(rs.getDate("regn_upto"));
                    dobj.setStatus(rs.getString("status"));
                    dobj.setState_cd_one(rs.getString("state_cd"));
                    dobj.setOff_cd_one(rs.getInt("off_cd"));
                } else if (regnNoTwo.equalsIgnoreCase(rs.getString("regnNo"))) {
                    isRecordFoundForRegnTwo = true;
                    dobj.setRegnNoTwo(rs.getString("regnNo"));
                    dobj.setOwnernametwo(rs.getString("OwnerName"));
                    dobj.setFathernametwo(rs.getString("FName"));
                    dobj.setC_add1Two(rs.getString("c_add1"));
                    dobj.setC_add2Two(rs.getString("c_add2"));
                    dobj.setC_add3Two(rs.getString("c_add3"));
                    dobj.setC_districtTwo(rs.getString("c_district_name"));
                    dobj.setC_stateTwo(rs.getString("c_state_name"));
                    dobj.setC_districtcodeTwo(rs.getInt("c_district"));
                    dobj.setC_statecodeTwo(rs.getString("state_cd"));
                    dobj.setC_pincodeTwo(rs.getInt("c_pincode"));
                    dobj.setApplnoTwo(rs.getString("appl_no"));
                    dobj.setLink_appl_no_two(rs.getString("link_appl_no"));
                    dobj.setVh_class_two(rs.getInt("vh_class"));
                    dobj.setRegn_dt_two(rs.getDate("regn_dt"));
                    dobj.setChassi_two(rs.getString("chasi_no"));
                    dobj.setEngine_two(rs.getString("eng_no"));
                    dobj.setRegnTypefornew(rs.getString("regn_type"));
                    if (dobj.getChassi_two() != null && dobj.getChassi_two().length() > 5) {
                        if (actioncd == 10114 && !dobj.getChassi_two().substring(dobj.getChassi_two().length() - 5).equalsIgnoreCase(chasitwo)) {
                            throw new VahanException("Chasi no does not matched for " + regnNoTwo.toUpperCase());
                        } else {
                            dobj.setChasiTwoLastFiveChar(dobj.getChassi_two().substring(dobj.getChassi_two().length() - 5));
                        }
                    }
                    dobj.setState_cd_two(rs.getString("state_cd"));
                    dobj.setOff_cd_two(rs.getInt("off_cd"));
                    dobj.setRelation_code(rs.getInt("relation_type"));
                    dobj.setVch_catg_old_vehicle(rs.getString("vch_catg"));
                    dobj.setOldVehicleOwnercd(rs.getInt("owner_cd"));
                }
            }
            if (actioncd == 10114) {
                if (!isRecordFoundForRegnOne) {
                    throw new VahanException("Details not found for Registration no " + regnNoOne.toUpperCase());

                } else if (!isRecordFoundForRegnTwo) {
                    throw new VahanException("Details not found for Registration no " + regnNoTwo.toUpperCase());
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date CurrentDate = null;
                java.sql.Date CurrentDateinsql = null;
                // Skip OLD vehicle age in DL state case
                if (!"DL".contains(Util.getUserStateCode()) && tmConfSwappingDobj.getOld_vehicle_age() > 0 && dobj.getRegn_dt() != null && !dobj.getRegn_dt().equals("")) {
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.YEAR, -tmConfSwappingDobj.getOld_vehicle_age());
                    CurrentDate = sdf.parse("" + cal.get(Calendar.YEAR) + "-"
                            + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH));
                    CurrentDateinsql = new java.sql.Date(CurrentDate.getTime());
                    if (!dobj.getRegn_dt().before(CurrentDateinsql)) {
                        vahanexecption = new VahanException("Could not swap the registration no as Registration date for first vehicle must be greater than three years from Current Date!!!");
                        throw vahanexecption;
                    }
                }
                if (tmConfSwappingDobj.getNew_vehicle_age() > 0 && dobj.getRegn_dt_two() != null && !dobj.getRegn_dt_two().equals("")) {
                    Calendar cal2 = Calendar.getInstance();
                    cal2.add(Calendar.YEAR, -tmConfSwappingDobj.getNew_vehicle_age());
                    CurrentDate = sdf.parse("" + cal2.get(Calendar.YEAR) + "-"
                            + (cal2.get(Calendar.MONTH) + 1) + "-" + cal2.get(Calendar.DAY_OF_MONTH));
                    CurrentDateinsql = new java.sql.Date(CurrentDate.getTime());
                    if (!dobj.getRegn_dt_two().after(CurrentDateinsql)) {
                        vahanexecption = new VahanException("Could not swap the registration no as Registration date for second vehicle must not be less than one years from Current Date!!!");
                        throw vahanexecption;
                    }
                }
                if (dobj.isIsHyptRegnNo()) {
                    vahanexecption = new VahanException("Could not swap the registration no as the registration no is hypothecated for first vehicle!!!");
                    throw vahanexecption;
                }
                if (tmConfSwappingDobj.isSame_owner_name() && dobj.getOwnernameone() != null && dobj.getOwnernametwo() != null) {
                    if (!dobj.getOwnernameone().equalsIgnoreCase(dobj.getOwnernametwo())) {
                        vahanexecption = new VahanException("Could not swap the registration no as owner details (owner name, father name) could not match!!!");
                        throw vahanexecption;
                    }
                }
                if (tmConfSwappingDobj.isSame_father_name() && dobj.getFathernameone() != null && dobj.getFathernametwo() != null) {
                    if (!dobj.getFathernameone().equalsIgnoreCase(dobj.getFathernametwo())) {
                        vahanexecption = new VahanException("Could not swap the registration no as owner details (owner name, father name) could not match!!!");
                        throw vahanexecption;
                    }
                }
                if (tmConfSwappingDobj.isSame_vehicle_class() && dobj.getVh_class_one() != 0 && dobj.getVh_class_two() != 0) {
                    if (dobj.getVh_class_one() != dobj.getVh_class_two()) {
                        vahanexecption = new VahanException("Could not swap the registration no as vehicle detail (vehicle class) could not match!!!");
                        throw vahanexecption;
                    }
                }
                if (tmConfSwappingDobj.isSame_vehicle_category() && dobj.getVch_catg_new_vehicle() != null && dobj.getVch_catg_old_vehicle() != null) {
                    if (!dobj.getVch_catg_new_vehicle().equalsIgnoreCase(dobj.getVch_catg_old_vehicle())) {
                        vahanexecption = new VahanException("Could not swap the registration no as vehicle detail (vehicle category) could not match!!!");
                        throw vahanexecption;
                    }
                }
                if (dobj.getNewVehicleOwnercd() == 4 || dobj.getNewVehicleOwnercd() == 5) {
                    if (dobj.getOldVehicleOwnercd() != 4 && dobj.getOldVehicleOwnercd() != 5) {
                        vahanexecption = new VahanException("Could not swap the registration no as vehicle detail (Ownership Type) could not match!!!");
                        throw vahanexecption;
                    }
                }
                if (dobj.getOldVehicleOwnercd() == 4 || dobj.getOldVehicleOwnercd() == 5) {
                    if (dobj.getNewVehicleOwnercd() != 4 && dobj.getNewVehicleOwnercd() != 5) {
                        vahanexecption = new VahanException("Could not swap the registration no as vehicle detail (Ownership Type) could not match!!!");
                        throw vahanexecption;
                    }
                }
            }
        } catch (SQLException ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            e = ee;

        } catch (ParseException ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            e = ee;
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ee) {
                LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            }
        }
        if (e != null) {
            vahanexecption = new VahanException(e.getMessage());
            throw vahanexecption;
        }
        return dobj;
    }

    public static SwappingRegnDobj setSwappingFromApplDBToDobj(String applNo, TmConfigurationSwappingDobj tmConfSwappingDobj) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        SwappingRegnDobj dobj = null;
        Exception e = null;
        VahanException vahanexecption = null;
        String firstRegnNo = "";
        String secondRegnNo = "";
        try {
            tmgr = new TransactionManager("setSwappingFromApplDBToDobj");
            String sql = "select * from " + TableList.VA_RETENTION + " where appl_no = ? or "
                    + " appl_no in (select link_appl_no from " + TableList.VA_RETENTION + " where appl_no=?) "
                    + " order by appl_no ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, applNo);

            RowSet rs = tmgr.fetchDetachedRowSet();
            dobj = new SwappingRegnDobj();
            while (rs.next()) {
                if (firstRegnNo.isEmpty()) {
                    firstRegnNo = rs.getString("regn_no");
                } else {
                    secondRegnNo = rs.getString("regn_no");
                }
            }
            dobj = getApplOwnerDetailDBToDobj(firstRegnNo, secondRegnNo, 0, null, null, tmConfSwappingDobj);

        } catch (VahanException ve) {
            throw ve;
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            e = ee;
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ee) {
                LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
                e = ee;
            }
        }
        if (e != null) {
            vahanexecption = new VahanException(TableConstants.SomthingWentWrong);
            throw vahanexecption;
        }
        return dobj;
    }

    public static void InsertAndUpdateVhAndVtTables(SwappingRegnDobj swap_dobj) throws VahanException {
        TransactionManager tmgr = null;
        Exception e = null;
        try {
            tmgr = new TransactionManager("makeChangeCA");
            InsertInVaVehRetention(tmgr, swap_dobj);
            tmgr.commit();
        } catch (SQLException ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            e = ee;
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            e = ee;
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ee) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    e = ee;
                }
            }
        }
        if (e != null) {
            throw new VahanException(e.getMessage());
        }
    }

    public static void UpdateVARetention(TransactionManager tmgr, SwappingRegnDobj swap_dobj) throws VahanException {
        PreparedStatement ps = null;
        String sqlone = null;
        Exception e = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            sqlone = " update " + TableList.VA_RETENTION
                    + " set order_by=?,"
                    + " order_dt=?,"
                    + " order_no=?,"
                    + " reason=?,"
                    + " op_dt = current_timestamp"
                    + " where appl_no=? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sqlone);
            ps.setString(1, swap_dobj.getOrder_by());
            ps.setDate(2, new Date(((java.util.Date) sdf.parse(swap_dobj.getOrder_dt())).getTime()));
            ps.setString(3, swap_dobj.getOrder_no());
            ps.setString(4, swap_dobj.getReason());
            ps.setString(5, swap_dobj.getApplnoOne());
            ps.setString(6, swap_dobj.getState_cd_one());
            ps.setInt(7, swap_dobj.getOff_cd_one());
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

            //Update Second Vehicle Registration No
            ps = tmgr.prepareStatement(sqlone);
            ps.setString(1, swap_dobj.getOrder_by());
            ps.setDate(2, new Date(((java.util.Date) sdf.parse(swap_dobj.getOrder_dt())).getTime()));
            ps.setString(3, swap_dobj.getOrder_no());
            ps.setString(4, swap_dobj.getReason());
            ps.setString(5, swap_dobj.getApplnoTwo());
            ps.setString(6, swap_dobj.getState_cd_two());
            ps.setInt(7, swap_dobj.getOff_cd_two());

            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            e = ee;
        }
        if (e != null) {
            throw new VahanException("Error in updation  for retention");
        }
    }

    public void update_Swapping_Status(SwappingRegnDobj swap_dobj, SwappingRegnDobj swap_dobj_prv, Status_dobj status_dobj, String changedData) throws VahanException {
        TransactionManager tmgr = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        Exception e = null;
        try {
            tmgr = new TransactionManager("update_Swap_Status");
            //=================WEB SERVICES FOR NEXTSTAGE START=========//
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            if (Util.getSelectedSeat().getAction_cd() == TableConstants.SWAPPING_REGN_VERIFICATION
                    || Util.getSelectedSeat().getAction_cd() == TableConstants.SWAPPING_REGN_APPROVAL) {
                if ((changedData != null && !changedData.equals("")) || swap_dobj_prv == null) {
                    insertUpdateVAVHRetention(tmgr, swap_dobj);//when there is change by user or Entry Scrutiny
                }
            }
            updateAndFlowForNextStage(status_dobj, swap_dobj, swap_dobj_prv, changedData, swap_dobj.getRegnNoOne(), swap_dobj.getRegnNoTwo(), swap_dobj.getApplnoOne(), tmgr);
            status_dobj.setAppl_no(swap_dobj.getLink_appl_no_one());
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            updateAndFlowForNextStage(status_dobj, swap_dobj, swap_dobj_prv, changedData, swap_dobj.getRegnNoTwo(), swap_dobj.getRegnNoOne(), swap_dobj.getApplnoTwo(), tmgr);
            if (Util.getSelectedSeat().getAction_cd() == TableConstants.SWAPPING_REGN_APPROVAL
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                String temp_regn_no = swap_dobj.getRegnNoOne();
                if (temp_regn_no != null) {
                    temp_regn_no = temp_regn_no.replace(temp_regn_no.substring(0, 2), "@#");
                } else {
                    LOGGER.error("Temporary Generation Failed");
                }
                updateTablesForRetention(tmgr, swap_dobj.getRegnNoOne(), temp_regn_no);
                updateTablesForRetention(tmgr, swap_dobj.getRegnNoTwo(), swap_dobj.getRegnNoOne());
                updateTablesForRetention(tmgr, temp_regn_no, swap_dobj.getRegnNoTwo());
                //HSRP
                ServerUtil.verifyInsertNewRegHsrpDetail(swap_dobj.getApplnoOne(), swap_dobj.getRegnNoOne(), TableConstants.HSRP_NEW_BOTH_SIDE,
                        Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), tmgr);

                //SmartCard Or Print
                ServerUtil.VerifyInsertSmartCardPrintDetail(swap_dobj.getApplnoOne(), swap_dobj.getRegnNoOne(),
                        Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), status_dobj.getPur_cd(), tmgr);
                //HSRP
                ServerUtil.verifyInsertNewRegHsrpDetail(swap_dobj.getApplnoTwo(), swap_dobj.getRegnNoTwo(), TableConstants.HSRP_NEW_BOTH_SIDE,
                        Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), tmgr);

                //SmartCard Or Print
                ServerUtil.VerifyInsertSmartCardPrintDetail(swap_dobj.getApplnoTwo(), swap_dobj.getRegnNoTwo(),
                        Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), status_dobj.getPur_cd(), tmgr);

                updateVaDetailsForSwappingRegnNo(tmgr, swap_dobj.getRegnNoTwo(), swap_dobj.getApplnoOne());
                updateVaDetailsForSwappingRegnNo(tmgr, swap_dobj.getRegnNoOne(), swap_dobj.getApplnoTwo());
            }

            tmgr.commit();//Commiting data here....

        } catch (SQLException ee) {
            e = ee;
        } catch (Exception ee) {
            e = ee;
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ee) {
                    LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
                    e = ee;
                }
            }
        }
        if (e != null) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Update Error in Swapping Application");
        }


    }

    private void updateVaDetailsForSwappingRegnNo(TransactionManager tmgr, String regnno, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        String sql = "update " + TableList.VA_DETAILS + " set regn_no=? where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, regnno);
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    public void updateAndFlowForNextStage(Status_dobj status_dobj, SwappingRegnDobj swap_dobj, SwappingRegnDobj swap_dobj_prv,
            String changedData, String regnnoone, String regnnotwo, String applno, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        boolean flag = false;
        Exception e = null;
        try {

            if (Util.getSelectedSeat().getAction_cd() == TableConstants.SWAPPING_REGN_APPROVAL
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                sql = "INSERT INTO " + TableList.VH_RE_ASSIGN + "("
                        + "            state_cd, off_cd, appl_no, old_regn_no, new_regn_no, reason,"
                        + "            moved_on, moved_by)"
                        + "    VALUES (?, ?, ?, ?, ?, ?,"
                        + "            current_timestamp, ?)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, Util.getSelectedSeat().getOff_cd());
                ps.setString(3, applno);
                ps.setString(4, regnnoone);
                ps.setString(5, regnnotwo);
                ps.setString(6, "SWAPPING OF REGN.MARKS");
                ps.setString(7, Util.getEmpCode());
                ps.executeUpdate();
                sql = "INSERT INTO " + TableList.VHA_RETENTION
                        + " select current_timestamp + interval '1 second' as moved_on,? as moved_by ,state_cd, off_cd, appl_no, regn_no, link_appl_no, order_no, order_dt,order_by,reason, op_dt,relation_type "
                        + " FROM  " + TableList.VA_RETENTION
                        + " WHERE  appl_no=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, applno);
                ps.executeUpdate();

                sql = "DELETE FROM " + TableList.VA_RETENTION + " where appl_no = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, applno);
                ps.executeUpdate();
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);
            }
            ServerUtil.fileFlow(tmgr, status_dobj); //for updateing va_status and vha status for new role,seat for new emp
        } catch (SQLException ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            e = ee;
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
        }
        if (e != null) {
            throw new VahanException("Swapping Application : Error in Database Update");
        }
    }

    public static void updateTablesForRetention(TransactionManager tmgr, String oldRegnNo, String newRegnNo) throws SQLException, VahanException {
        PreparedStatement ps;
        String sqlQuery = null;
        List UpdatedTableList = new ArrayList();
        List UpdatedTableListWithNewRegnNo = new ArrayList();
        List UpdateTableListWithoutStateCodeAndOffCode = new ArrayList();
        Exception e = null;
        //Update corresponding VT tables for retention of registration mark.
        try {
            UpdatedTableList.add(TableList.VT_AXLE);
            UpdatedTableList.add(TableList.VT_FITNESS);
            UpdatedTableList.add(TableList.VT_HYPTH);
            UpdatedTableList.add(TableList.VT_IMPORT_VEH);
            UpdatedTableList.add(TableList.VT_INSURANCE);
            UpdatedTableList.add(TableList.VT_OWNER_EX_ARMY);
            UpdatedTableList.add(TableList.VT_OWNER_IDENTIFICATION);
            UpdatedTableList.add(TableList.VT_PUCC);
            UpdatedTableList.add(TableList.VT_RETROFITTING_DTLS);
            UpdatedTableList.add(TableList.VT_TRAILER);
            UpdatedTableList.add(TableList.VT_TMP_REGN_DTL);
            UpdatedTableList.add(TableList.VT_OWNER);
            UpdatedTableList.add(TableList.VT_FEE);
            UpdatedTableList.add(TableList.VT_TAX);
            UpdatedTableList.add(TableList.VT_TAX_CLEAR);
            UpdatedTableList.add(TableList.VT_TAX_BASED_ON);
            UpdatedTableList.add(TableList.VT_TAX_EXEM);
            //Update corresponding VH tables for retention of registration mark.        
            UpdatedTableList.add(TableList.VH_ALT);
            UpdatedTableList.add(TableList.VH_AXLE);
            UpdatedTableList.add(TableList.VH_BLACKLIST);
            UpdatedTableList.add(TableList.VH_TO);
            UpdatedTableList.add(TableList.VH_CA);
            UpdatedTableList.add(TableList.VH_DUP);
            UpdatedTableList.add(TableList.VA_FC_PRINT);
            UpdatedTableList.add(TableList.VH_FC_PRINT);
            //UpdateTableListWithoutStateCodeAndOffCode.add(TableList.VH_FC_PRINT);//NO state_cd and off_cd
            UpdatedTableList.add(TableList.VH_FITNESS);
            UpdatedTableList.add(TableList.VH_HPT);
            UpdatedTableList.add(TableList.VH_HYPTH);
            UpdatedTableList.add(TableList.VH_IMPORT_VEH);
            UpdatedTableList.add(TableList.VH_INSURANCE);
            UpdatedTableList.add(TableList.VH_OWNER_EX_ARMY);
            UpdatedTableList.add(TableList.VH_OWNER_IDENTIFICATION);
            UpdatedTableList.add(TableList.VH_PUCC);
            UpdatedTableList.add(TableList.VH_RC_PRINT);
            UpdatedTableList.add(TableList.VH_OWNER);
            UpdatedTableList.add(TableList.VH_RENEWAL);
            UpdatedTableList.add(TableList.VH_RETROFITTING_DTLS);
            UpdatedTableList.add(TableList.VH_TAX_CLEAR);
            UpdatedTableList.add(TableList.VH_TAX_INSTALLMENT);
            UpdatedTableList.add(TableList.VH_TAX_INSTALLMENT_BRKUP);
            UpdatedTableList.add(TableList.VH_TRAILER);
            UpdatedTableListWithNewRegnNo.add(TableList.VT_OTHER_STATE_VEH);
            UpdatedTableListWithNewRegnNo.add(TableList.VH_CONVERSION);
            for (Object TableName : UpdatedTableList) {
                sqlQuery = "UPDATE " + TableName + " set regn_no=? WHERE regn_no = ? and state_cd = ?";
                ps = tmgr.prepareStatement(sqlQuery);
                ps.setString(1, newRegnNo);
                ps.setString(2, oldRegnNo);
                ps.setString(3, Util.getUserStateCode());
                ps.executeUpdate();

            }
            for (Object TableName : UpdateTableListWithoutStateCodeAndOffCode) {
                sqlQuery = "UPDATE " + TableName + " set regn_no=? WHERE regn_no = ?";
                ps = tmgr.prepareStatement(sqlQuery);
                ps.setString(1, newRegnNo);
                ps.setString(2, oldRegnNo);
                ps.executeUpdate();

            }
            for (Object tabelName : UpdatedTableListWithNewRegnNo) {
                sqlQuery = "UPDATE " + tabelName + " SET new_regn_no=? WHERE new_regn_no=? and state_cd = ? and off_cd = ?";
                ps = tmgr.prepareStatement(sqlQuery);
                ps.setString(1, newRegnNo);
                ps.setString(2, oldRegnNo);
                ps.setString(3, Util.getUserStateCode());
                ps.setInt(4, Util.getSelectedSeat().getOff_cd());
                ps.executeUpdate();
            }
        } catch (SQLException ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            e = ee;
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            e = ee;
        }
        if (e != null) {
            throw new VahanException("Error in Updating tables for Retention");
        }
    }

    public static void InsertInVaVehRetention(TransactionManager tmgr, SwappingRegnDobj swap_dobj) throws VahanException, Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat opdtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String appl_no_one = null;
        String appl_no_two = null;
        String sqlInsert = "insert into " + TableList.VA_RETENTION + " (appl_no,link_appl_no,regn_no,order_by,order_no,reason,op_dt,order_dt,state_cd,off_cd)"
                + " values(?,?,?,?,?,?,current_timestamp,?,?,?)";
        Exception e = null;
        try {
            appl_no_one = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());//generate a new application no.        
            appl_no_two = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());//generate a new application no.        
            PreparedStatement ps = tmgr.prepareStatement(sqlInsert);
            ps.setString(1, appl_no_one);
            ps.setString(2, appl_no_two);
            ps.setString(3, swap_dobj.getRegnNoOne());
            ps.setString(4, swap_dobj.getOrder_by());
            ps.setString(5, swap_dobj.getOrder_no());
            ps.setString(6, swap_dobj.getReason());
            if (swap_dobj.getOrder_dt() != null) {
                ps.setDate(7, new Date(((java.util.Date) sdf.parse(swap_dobj.getOrder_dt())).getTime()));
            }
            ps.setString(8, Util.getUserStateCode());
            ps.setInt(9, Util.getSelectedSeat().getOff_cd());
            ps.executeUpdate();
            PreparedStatement pstwo = tmgr.prepareStatement(sqlInsert);
            pstwo.setString(1, appl_no_two);
            pstwo.setString(2, appl_no_one);
            pstwo.setString(3, swap_dobj.getRegnNoTwo());
            pstwo.setString(4, swap_dobj.getOrder_by());
            pstwo.setString(5, swap_dobj.getOrder_no());
            pstwo.setString(6, swap_dobj.getReason());
            if (swap_dobj.getOrder_dt() != null) {
                pstwo.setDate(7, new Date(((java.util.Date) sdf.parse(swap_dobj.getOrder_dt())).getTime()));
            }
            pstwo.setString(8, Util.getUserStateCode());
            pstwo.setInt(9, Util.getSelectedSeat().getOff_cd());
            pstwo.executeUpdate();
        } catch (ParseException ee) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (SQLException ee) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (VahanException ve) {
            throw e;
        } catch (Exception ve) {
            e = ve;
        }

        if (e != null) {
            throw new VahanException("Error in insertion for Retention");
        }

    }

    public static void InsertIntoVha_Retention(TransactionManager tmgr, SwappingRegnDobj swap_dobj) throws VahanException {
        PreparedStatement psone = null;
        Exception e = null;
        String sqlInsertone = "insert into " + TableList.VHA_RETENTION + " select current_timestamp,?,* from " + TableList.VA_RETENTION + " where "
                + " appl_no in (?, ?)";
        try {
            psone = tmgr.prepareStatement(sqlInsertone);
            psone.setString(1, Util.getEmpCode());
            psone.setString(2, swap_dobj.getApplnoOne());
            psone.setString(3, swap_dobj.getApplnoTwo());
            psone.executeUpdate();
        } catch (SQLException ee) {
            e = ee;
        }
        if (e != null) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Insertion of Retention");

        }
    }

    public static void insertUpdateVAVHRetention(TransactionManager tmgr, SwappingRegnDobj swap_dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        Exception e = null;
        try {
            if (swap_dobj.getApplno() == null || swap_dobj.getApplno().equals("")) {
                InsertInVaVehRetention(tmgr, swap_dobj);
            } else {
                InsertIntoVha_Retention(tmgr, swap_dobj);
                UpdateVARetention(tmgr, swap_dobj);

            }
        } catch (SQLException ee) {

            e = ee;
        } catch (Exception ee) {
            e = ee;
        }
        if (e != null) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in insert and update for retention");
        }
    }

    public static boolean isAlreadyRetened(String regn_no) {
        boolean isExist = false;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            String sqlforregnNoTwo = "select appl_no from " + TableList.VH_RE_ASSIGN + " a  where a.old_regn_no=? and a.state_cd=? and a.off_cd=? limit 1";
            tmgr = new TransactionManager("get Detail For Already Retened");
            ps = tmgr.prepareStatement(sqlforregnNoTwo);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                isExist = true;
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
        return isExist;
    }

    public static void insertIntoVaRetentionHistory(TransactionManager tmgr, String appl_no) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VHA_RETENTION
                + " SELECT current_timestamp as moved_on, ? as moved_by , * "
                + "  FROM  " + TableList.VA_RETENTION
                + " WHERE  appl_no=? and state_cd=? and off_cd=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.setString(3, Util.getUserStateCode());
        ps.setInt(4, Util.getUserSeatOffCode());
        ps.executeUpdate();
    }

    public static void deleteVaRetention(TransactionManager tmgr, String appl_no) throws SQLException, Exception {
        String sql = "DELETE FROM " + TableList.VA_RETENTION + "  WHERE appl_no=? ";
        int i = 1;
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(i++, appl_no);
        ps.executeUpdate();

    }

    public static boolean isFancyNo(String regnNo) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        boolean fancy = false;
        try {
            tmgr = new TransactionManager("isFancyNo");
            String sql = "SELECT regn_no FROM " + TableList.VT_ADVANCE_REGN_NO + " WHERE regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            // Not found
            if (!rs.next()) {
                regnNo = regnNo.substring((regnNo.length() - 4)).toUpperCase();
                sql = "SELECT fancy_number FROM " + TableList.VM_FANCY_MAST + " WHERE state_cd=? AND fancy_number =?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setString(2, regnNo);
                rs = tmgr.fetchDetachedRowSet();
                // found
                if (rs.next()) {
                    fancy = true;
                }
            } else {
                fancy = true;
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
        return fancy;
    }
}
