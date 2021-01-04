/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.hsrp;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.hsrp.HSRPFileExportDobj;
import nic.vahan.form.dobj.hsrp.HSRPIpDobj;
import nic.vahan.form.impl.Util;

import org.apache.log4j.Logger;

/**
 *
 * @author tranC075
 */
public class HSRPFileExportImpl {

    private static final Logger LOGGER = Logger.getLogger(HSRPFileExportImpl.class);

    public static HSRPFileExportDobj getHsrpDetails(String state_cd, int officeCode) {
        ArrayList listhsrp = new ArrayList();
        HSRPFileExportDobj ret_dobj = new HSRPFileExportDobj();
        TransactionManagerReadOnly tmgr = null;
        String fileDownloaded = "";
        PreparedStatement st = null;
        RowSet rs = null;
        try {
            tmgr = new TransactionManagerReadOnly("HSRPFileExportImpl.getHsrpDetails");
            HSRPFileExportDobj dobj = null;
            String sqlQuery;
            String sqlQuery1 = "select right('00'||(a.off_cd::varchar), 2) as off_cd,a.state_cd,to_char(current_timestamp,'dd_Mon_yyyy_hh24_mi_ss') cdate, a.regn_no, a.hsrp_flag, a.appl_no, "
                    + " COALESCE(b.chasi_no, d.chasi_no) chasi_no, COALESCE(b.eng_no, d.eng_no) eng_no, COALESCE(b.vh_class_desc, d.vh_class_desc) vh_class_desc, "
                    + " COALESCE(b.vch_catg, d.vch_catg) vch_catg,COALESCE(b.fuel_descr, d.fuel_descr) fuel_descr, COALESCE(b.maker_name, d.maker_name) maker_name,  "
                    + " COALESCE(b.model_name, d.model_name) model_name, to_char(COALESCE(b.regn_dt, d.regn_dt),'dd-MM-yyyy') regn_dt, "
                    + " COALESCE(b.owner_name, d.owner_name) owner_name, COALESCE(b.dlr_name, d.dlr_name) dlr_name , string_agg(pur_cd::text, ',') as pur_cd,d.dealer_cd"
                    + " from " + TableList.VA_HSRP + " a  "
                    + " left outer join " + TableList.VIEW_VVA_OWNER + " b on b.regn_no = a.regn_no and a.state_cd = b.state_cd and a.off_cd = b.off_cd  "
                    + " left outer join " + TableList.VIEW_VV_OWNER + " d on d.regn_no = a.regn_no and a.state_cd = d.state_cd"
                    + " left outer join " + TableList.VA_DETAILS + " c on c.appl_no =a.appl_no "
                    + " left join homologation.vm_maker e on e.code =  COALESCE(b.maker, d.maker) "
                    + " where ";

            String sqlQuery2 = "a.off_cd = ? and ";

            String sqlQuery3 = " a.state_cd = ? and (COALESCE(b.regn_dt, d.regn_dt)<'2019-11-25' or  e.code is null or COALESCE(b.regn_type, d.regn_type)IN ('A','O','D')) "
                    + " and c.entry_status = 'A' and c.pur_cd not in (?, ?) "
                    + " group by 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,18"
                    + " order by a.off_cd,  a.regn_no ,a.op_dt desc";
            int i = 1;
            if (officeCode == 0) {
                sqlQuery = sqlQuery1 + sqlQuery3;
            } else {
                sqlQuery = sqlQuery1 + sqlQuery2 + sqlQuery3;
            }
            st = tmgr.prepareStatement(sqlQuery);
            if (officeCode != 0) {
                st.setInt(i++, officeCode);
            }
            st.setString(i++, state_cd);
            st.setInt(i++, TableConstants.VM_TRANSACTION_MAST_TEMP_REG);
            st.setInt(i++, TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE);
            rs = tmgr.fetchDetachedRowSet();
            i = 0;
            while (rs.next()) {
                dobj = new HSRPFileExportDobj();
                i++;
                try {
                    dobj.setSrNo(i);
                    if (rs.getString("regn_no") == null) {
                        dobj.setRegnNo(" ");
                    } else {
                        dobj.setRegnNo(rs.getString("regn_no"));
                    }

                    dobj.setOffCd(rs.getString("off_cd"));
                    dobj.setStateCd(rs.getString("state_cd"));

                    if (rs.getString("chasi_no") == null) {
                        dobj.setChassisNo(" ");
                    } else {
                        dobj.setChassisNo(rs.getString("chasi_no"));
                    }

                    if (rs.getString("regn_dt") == null) {
                        dobj.setRegnDt(" ");
                    } else {
                        dobj.setRegnDt(rs.getString("regn_dt"));
                    }

                    if (rs.getString("eng_no") == null) {
                        dobj.setEngNo(" ");
                    } else {
                        dobj.setEngNo(rs.getString("eng_no"));
                    }

                    if (rs.getString("hsrp_flag") == null) {
                        dobj.setHrspFlag(" ");
                    } else {
                        dobj.setHrspFlag(rs.getString("hsrp_flag"));
                    }

                    if (rs.getString("vh_class_desc") == null) {
                        dobj.setVehicleClass(" ");
                    } else {
                        dobj.setVehicleClass(rs.getString("vh_class_desc"));
                    }

                    if (rs.getString("vch_catg") == null) {
                        dobj.setVehicleCatg(" ");
                    } else {
                        dobj.setVehicleCatg(rs.getString("vch_catg"));
                    }

                    if (rs.getString("fuel_descr") == null) {
                        dobj.setFuelDescr(" ");
                    } else {
                        dobj.setFuelDescr(rs.getString("fuel_descr"));
                    }

                    if (rs.getString("maker_name") == null) {
                        dobj.setMaker(" ");
                    } else {
                        dobj.setMaker(rs.getString("maker_name"));
                    }

                    if (rs.getString("model_name") == null) {
                        dobj.setModel(" ");
                    } else {
                        dobj.setModel(rs.getString("model_name"));
                    }

                    if (rs.getString("owner_name") == null) {
                        dobj.setOwnerName(" ");
                    } else {
                        dobj.setOwnerName(rs.getString("owner_name"));
                    }

                    if (rs.getString("dlr_name") == null) {
                        dobj.setDealer(" ");
                    } else {
                        dobj.setDealer(rs.getString("dlr_name"));
                    }

                    if (rs.getString("appl_no") == null) {
                        dobj.setReciptNo(" ");
                    } else {
                        dobj.setReciptNo(rs.getString("appl_no"));
                    }

                    if (rs.getString("pur_cd").contains(String.valueOf(TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE))) {
                        dobj.setPurCd("DLR-" + rs.getString("dealer_cd"));
                    } else {
                        dobj.setPurCd("RTO");
                    }

                    dobj.setStar("*");
                    dobj.setSeperator(";");
                    ret_dobj.setCur_date(rs.getString("cdate"));
                    ret_dobj.setHsrpFileName(state_cd + officeCode + "_HSRP_" + rs.getString("cdate"));

                    listhsrp.add(dobj);

                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }

            }
            if (i == 0) {
                fileDownloaded = "NORECORD";
            }

        } catch (Exception e) {
            fileDownloaded = "FAIL";
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
        ret_dobj.setListFileExport(listhsrp);
        return ret_dobj;
    }

    // table names to changed
    public static List<HSRPFileExportDobj> getHSRPOldList(String state_cd, int off_cd, Date oldDate) {
        List<HSRPFileExportDobj> retList = new ArrayList<>();
        String sql = "Select *  from " + TableList.VT_HSRP_FLATFILE
                + "   where state_cd = ? and off_cd = ? and date(op_dt)=date(?)";
        TransactionManagerReadOnly tmgr = null;
        HSRPFileExportDobj ret_dobj = null;
        try {
            tmgr = new TransactionManagerReadOnly("HSRPFileExportImpl.getHSRPOldList");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.setDate(3, new java.sql.Date(oldDate.getTime()));
            RowSet rsSm = tmgr.fetchDetachedRowSet();
            while (rsSm.next()) {
                ret_dobj = new HSRPFileExportDobj();
                ret_dobj.setCur_date(rsSm.getString("flat_file"));
                ret_dobj.setHsrpFileName(rsSm.getString("flat_file"));
                retList.add(ret_dobj);
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

        return retList;
    }

    public static List<HSRPFileExportDobj> getHSRPOldListFilewise(String state_cd, int off_cd, String fileName) {
        List<HSRPFileExportDobj> retList = new ArrayList<>();
        ArrayList listSmartCard = null;
        TransactionManagerReadOnly tmgr = null;
        HSRPFileExportDobj ret_dobj = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManagerReadOnly("HSRPFileExportImpl.getHSRPOldListFilewise");
            HSRPFileExportDobj dobj = null;
            ret_dobj = new HSRPFileExportDobj();
            ret_dobj.setCur_date(fileName);
            ret_dobj.setHsrpFileName(fileName);
            listSmartCard = new ArrayList();
            String sql;
            String sql1 = "select right('00'||(a.off_cd::varchar), 2) as off_cd,a.state_cd,to_char(current_timestamp,'dd_Mon_yyyy_hh24_mi_ss') cdate, a.regn_no, a.hsrp_flag, a.appl_no, "
                    + " COALESCE(b.chasi_no, d.chasi_no) chasi_no, COALESCE(b.eng_no, d.eng_no) eng_no, COALESCE(b.vh_class_desc, d.vh_class_desc) vh_class_desc, "
                    + " COALESCE(b.vch_catg, d.vch_catg) vch_catg ,COALESCE(b.fuel_descr, d.fuel_descr) fuel_descr"
                    + " , COALESCE(b.maker_name, d.maker_name) maker_name,  COALESCE(b.model_name, d.model_name) model_name, "
                    + " COALESCE(b.owner_name, d.owner_name) owner_name, COALESCE(b.dlr_name, d.dlr_name) dlr_name ,"
                    + "  to_char(COALESCE(b.regn_dt, d.regn_dt),'dd-MM-yyyy') regn_dt, c.pur_cd,d.dealer_cd  "
                    + " from " + TableList.VHA_HSRP + " a  "
                    + " left outer join " + TableList.VIEW_VVA_OWNER + " b on b.regn_no = a.regn_no and a.state_cd = b.state_cd and a.off_cd = b.off_cd "
                    + " left outer join " + TableList.VIEW_VV_OWNER + " d on d.regn_no = a.regn_no and a.state_cd = d.state_cd "
                    + " left outer join " + TableList.VA_DETAILS + " c on c.appl_no =a.appl_no  "
                    + " where ";
            String sql2 = " a.off_cd = ? and ";
            String sql3 = " a.state_cd = ? and a.flat_file = ? and c.entry_status = 'A' and c.pur_cd not in (?, ?)   "
                    + " order by a.off_cd , a.regn_no ,a.op_dt desc";
            int i = 1;
            if (off_cd == 0) {
                sql = sql1 + sql3;
            } else {
                sql = sql1 + sql2 + sql3;
            }
            ps = tmgr.prepareStatement(sql);
            if (off_cd != 0) {
                ps.setInt(i++, off_cd);
            }
            ps.setString(i++, state_cd);
            ps.setString(i++, fileName);
            ps.setInt(i++, TableConstants.VM_TRANSACTION_MAST_TEMP_REG);
            ps.setInt(i++, TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE);
            RowSet rs = tmgr.fetchDetachedRowSet();
            i = 0;
            while (rs.next()) {
                i++;
                dobj = new HSRPFileExportDobj();

                dobj.setSrNo(i);
                if (rs.getString("regn_no") == null) {
                    dobj.setRegnNo(" ");
                } else {
                    dobj.setRegnNo(rs.getString("regn_no"));
                }

                dobj.setOffCd(rs.getString("off_cd"));
                dobj.setStateCd(rs.getString("state_cd"));

                if (rs.getString("chasi_no") == null) {
                    dobj.setChassisNo(" ");
                } else {
                    dobj.setChassisNo(rs.getString("chasi_no"));
                }

                if (rs.getString("eng_no") == null) {
                    dobj.setEngNo(" ");
                } else {
                    dobj.setEngNo(rs.getString("eng_no"));
                }

                if (rs.getString("hsrp_flag") == null) {
                    dobj.setHrspFlag(" ");
                } else {
                    dobj.setHrspFlag(rs.getString("hsrp_flag"));
                }

                if (rs.getString("regn_dt") == null) {
                    dobj.setRegnDt(" ");
                } else {
                    dobj.setRegnDt(rs.getString("regn_dt"));
                }

                if (rs.getString("vh_class_desc") == null) {
                    dobj.setVehicleClass(" ");
                } else {
                    dobj.setVehicleClass(rs.getString("vh_class_desc"));
                }

                if (rs.getString("vch_catg") == null) {
                    dobj.setVehicleCatg(" ");
                } else {
                    dobj.setVehicleCatg(rs.getString("vch_catg"));
                }

                if (rs.getString("fuel_descr") == null) {
                    dobj.setFuelDescr(" ");
                } else {
                    dobj.setFuelDescr(rs.getString("fuel_descr"));
                }

                if (rs.getString("maker_name") == null) {
                    dobj.setMaker(" ");
                } else {
                    dobj.setMaker(rs.getString("maker_name"));
                }

                if (rs.getString("model_name") == null) {
                    dobj.setModel(" ");
                } else {
                    dobj.setModel(rs.getString("model_name"));
                }

                if (rs.getString("owner_name") == null) {
                    dobj.setOwnerName(" ");
                } else {
                    dobj.setOwnerName(rs.getString("owner_name"));
                }

                if (rs.getString("dlr_name") == null) {
                    dobj.setDealer(" ");
                } else {
                    dobj.setDealer(rs.getString("dlr_name"));
                }

                if (rs.getString("appl_no") == null) {
                    dobj.setReciptNo(" ");
                } else {
                    dobj.setReciptNo(rs.getString("appl_no"));
                }
                if (rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
                    dobj.setPurCd("DLR-" + rs.getString("dealer_cd"));
                } else {
                    dobj.setPurCd("RTO");
                }

                dobj.setStar("*");
                dobj.setSeperator(";");

                listSmartCard.add(dobj);
            }
            ret_dobj.setListFileExport(listSmartCard);
            retList.add(ret_dobj);
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

        return retList;
    }
// tables to be changed

    public static void saveDownLoadInfo(String state_cd, int off_cd, HSRPFileExportDobj dobj) throws Exception {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = "";
        try {
            tmgr = new TransactionManager("saveDownLoadInfo");

            String sql1 = "INSERT INTO " + TableList.VHA_HSRP
                    + "  Select a.state_cd, a.off_cd, a.appl_no, a.regn_no, a.hsrp_flag, a.user_cd, a.op_dt, ?,"
                    + " current_timestamp, ? from " + TableList.VA_HSRP + " a  "
                    + " left outer join " + TableList.VIEW_VVA_OWNER + " b on b.regn_no = a.regn_no and a.state_cd = b.state_cd and a.off_cd = b.off_cd  "
                    + " left outer join " + TableList.VIEW_VV_OWNER + " d on d.regn_no = a.regn_no and a.state_cd = d.state_cd "
                    + " left outer join " + TableList.VA_DETAILS + " c on c.appl_no =a.appl_no "
                    + " left join homologation.vm_maker e on e.code =  COALESCE(b.maker, d.maker) "
                    + " where a.op_dt< to_timestamp( ?,'dd_Mon_yyyy_hh24_mi_ss') and "
                    + ""
                    + " a.state_cd=? and (COALESCE(b.regn_dt, d.regn_dt)<'2019-11-25' or  e.code is null or COALESCE(b.regn_type, d.regn_type)IN ('A','O','D')) "
                    + " and c.entry_status = 'A' and c.pur_cd not in (?, ?) ";

            String sql2 = "and a.off_cd=?";

            if (off_cd == 0) {
                sql = sql1;
            } else {
                sql = sql1 + sql2;
            }
            sql = sql + " group by 1,2,3,4,5,6,7,8,9,10 order by a.off_cd,  a.regn_no ,a.op_dt desc ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getHsrpFileName());
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, dobj.getCur_date());
            ps.setString(4, state_cd);
            ps.setInt(5, TableConstants.VM_TRANSACTION_MAST_TEMP_REG);
            ps.setInt(6, TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE);
            if (off_cd != 0) {
                ps.setInt(7, off_cd);
            }
            ps.executeUpdate();

            sql1 = "Delete from " + TableList.VA_HSRP
                    + " where op_dt< to_timestamp( ?,'dd_Mon_yyyy_hh24_mi_ss') and "
                    + " state_cd=?  ";
            sql2 = " and off_cd=?";
            if (off_cd == 0) {
                sql = sql1;
            } else {
                sql = sql1 + sql2;
            }
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getCur_date());
            ps.setString(2, state_cd);
            if (off_cd != 0) {
                ps.setInt(3, off_cd);
            }
            ps.executeUpdate();

            sql = "Insert into " + TableList.VT_HSRP_FLATFILE
                    + " (state_cd, off_cd, flat_file, op_dt) values(?,?,?,current_timestamp)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.setString(3, dobj.getHsrpFileName());
            ps.executeUpdate();

            tmgr.commit();
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

    public static String saveIPDetails(String ipAdrress, String merchantKey) {
        String status = "";
        boolean flag = false;
        PreparedStatement ps = null;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        ResultSet rs = null;
        String sql = "INSERT INTO " + TableList.VM_HSRP_IP_ALLOWED + " ( "
                + "            user_cd, request_server_ip4 , merchant_key) "
                + "    VALUES (?, ? , ?)";

        try {

            String checkIPquery = "select user_cd from " + TableList.VM_HSRP_IP_ALLOWED + " where user_cd=? and request_server_ip4=?";
            tmgr = new TransactionManager("saveIPDetails");
            ps = tmgr.prepareStatement(checkIPquery);
            ps.setLong(1, Long.parseLong(Util.getEmpCode()));
            ps.setString(2, ipAdrress);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                flag = true;
                status = "REPEAT";
            }
            if (!flag) {
                psmt = tmgr.prepareStatement(sql);
                psmt.setLong(1, Long.parseLong(Util.getEmpCode()));
                psmt.setString(2, ipAdrress);
                psmt.setString(3, merchantKey);
                int i = psmt.executeUpdate();
                if (i > 0) {
                    status = "SUCCESS";
                } else {
                    status = "FAIL";
                }
            }
            tmgr.commit();
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
        return status;
    }

    public static List<HSRPIpDobj> getIpList(String empCd) {
        TransactionManagerReadOnly tmgr = null;
        List<HSRPIpDobj> list = new ArrayList<HSRPIpDobj>();
        PreparedStatement psmt = null;
        String sql = "SELECT request_server_ip4  FROM " + TableList.VM_HSRP_IP_ALLOWED + " where user_cd=? ";
        try {
            tmgr = new TransactionManagerReadOnly("HSRPFileExportImpl.getIpList");
            psmt = tmgr.prepareStatement(sql);
            psmt.setLong(1, Long.parseLong(Util.getEmpCode()));
            ResultSet rs = tmgr.fetchDetachedRowSet();
            int i = 1;
            while (rs.next()) {
                HSRPIpDobj dobj = new HSRPIpDobj();
                dobj.setIp((rs.getString("request_server_ip4")));
                list.add(dobj);
            }
        } catch (SQLException ex) {
            LOGGER.error("Sql Exception Occured in fetching Ip List- ", ex);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (VahanException vahanException) {
                LOGGER.error("Sql Exception Occured in fetching Ip list-", vahanException);
            } catch (Exception ex) {
                LOGGER.error("Sql Exception Occured in fetching Ip List- ", ex);
            }
        }
        return list;
    }

    public static String deleteIPDetails(String ipAdrress) {
        String status = "";
        TransactionManager tmgr = null;
        String sql = "Insert into " + TableList.VHM_HSRP_IP_ALLOWED
                + " SELECT user_cd, request_server_ip4, op_dt , ? , current_timestamp"
                + "  FROM " + TableList.VM_HSRP_IP_ALLOWED
                + " where user_cd=? and request_server_ip4=? ";

        try {
            tmgr = new TransactionManager("deleteIPDetails");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setLong(2, Long.parseLong(Util.getEmpCode()));
            ps.setString(3, ipAdrress);

            ps.executeUpdate();

            sql = "Delete from " + TableList.VM_HSRP_IP_ALLOWED
                    + " where user_cd=? and request_server_ip4=?";

            ps = null;
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(Util.getEmpCode()));
            ps.setString(2, ipAdrress);
            ps.executeUpdate();
            tmgr.commit();
            status = "SUCCESS";
        } catch (Exception ex) {
            LOGGER.error("Sql Exception Occured in deleting Ip- ", ex);
            status = "FAIL";
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error("Sql Exception Occured in finally block- ", ex);
                }
            }
        }
        return status;

    }
}
