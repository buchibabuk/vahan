/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.DscDobj;
import app.eoffice.dsc.xml.common.Revocation;
import app.eoffice.dsc.xml.request.RevocationRequest;
import app.eoffice.dsc.xml.response.ChainCertificates.CertificateDetail;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.DocumentDetailsDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author komal
 */
public class DscRegistrationImpl {

    private static final Logger LOGGER = Logger.getLogger(DscRegistrationImpl.class);

    public boolean dscRegistration(DscDobj dob) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        TransactionManager tmgr = null;
        boolean flag = false;
        try {
            tmgr = new TransactionManager("dscRegistration");
            int dscChainCertCount = 0;

            int vendorCd = this.getAndInsertVendorCode(tmgr, dob.getVendorName());

            if (vendorCd != 0) {

                boolean isDscSerailNoExist = this.checkDscSerialNo(tmgr, dob.getSerialNo(), vendorCd);
                if (isDscSerailNoExist) {
                    throw new VahanException("Serial No [" + dob.getSerialNo() + "] for Vendor [" + dob.getVendorName() + "] Already Registered !!!");
                }

                sql = "INSERT INTO " + TableList.VT_DSC_REGISTRATION + "(\n"
                        + " state_cd, off_cd, user_cd,vendor_cd, serial_no, cert_level, cdp_pontl, \n"
                        + " status, dsc_user_name, cert_valid_upto, cert_content,digital_sign_to_print,dsc_verify, op_dt)\n"
                        + "    VALUES (?, ?, ?, ?, ?,?, ?,?, ?, ?, ?,?,?, current_timestamp)";
                ps = tmgr.prepareStatement(sql);
                int k = 1;
                ps.setString(k++, Util.getUserStateCode());
                ps.setInt(k++, Util.getUserSeatOffCode());
                ps.setLong(k++, Long.parseLong(Util.getEmpCode()));
                ps.setInt(k++, vendorCd);
                ps.setString(k++, dob.getSerialNo());
                ps.setString(k++, dob.getCertlevel());
                ps.setString(k++, dob.getCdpPoint());
                ps.setString(k++, dob.getActiveStatus());
                ps.setString(k++, dob.getUserName());
                ps.setDate(k++, new java.sql.Date(dob.getCertValidDate().getTime()));
                ps.setString(k++, dob.getCertContent());
                ps.setString(k++, dob.getDigitalSignToPrint());
                if (Util.getUserCategory() != null && Util.getUserCategory().equals(TableConstants.USER_CATG_DEALER_ADMIN)) {
                    ps.setBoolean(k++, false);
                } else if (Util.getUserCategory() != null && Util.getUserCategory().equals(TableConstants.USER_CATG_OFFICE_ADMIN)) {
                    ps.setBoolean(k++, true);
                }
                int count = ps.executeUpdate();
                dscChainCertCount = dscChainCertCount + count;

                for (CertificateDetail certDetail : dob.getChainCertificates()) {
                    if (certDetail.getCertLevel() != null && !certDetail.getCertLevel().equalsIgnoreCase("0")) {
                        sql = "INSERT INTO " + TableList.VT_DSC_CHAIN_CERT + "(\n"
                                + " user_cd, vendor_cd,serial_no, cert_serial_no, cert_level, cdp_pontl, cert_content, op_dt)\n"
                                + "    VALUES (?, ?, ?,?, ?, ?, ?, current_timestamp)";

                        ps = tmgr.prepareStatement(sql);
                        int m = 1;
                        ps.setLong(m++, Long.parseLong(Util.getEmpCode()));
                        ps.setInt(m++, vendorCd);
                        ps.setString(m++, dob.getSerialNo());
                        ps.setString(m++, certDetail.getSerialNumber());
                        ps.setString(m++, certDetail.getCertLevel());
                        ps.setString(m++, certDetail.getCdpPoint());
                        ps.setString(m++, certDetail.getCertContent());
                        int certCount = ps.executeUpdate();
                        dscChainCertCount = dscChainCertCount + certCount;
                    }
                }
                if (dscChainCertCount == dob.getChainCertificates().size()) {
                    tmgr.commit();
                    flag = true;
                }
            } else {
                throw new VahanException("Problem in Creating Vendor Details");
            }
        } catch (VahanException vex) {
            throw vex;
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

        return flag;
    }

    public int getAndInsertVendorCode(TransactionManager tmgr, String vendorName) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        int vendorCd = 0;
        try {
            if (vendorName != null && !vendorName.isEmpty()) {
                sql = "select vendor_cd from  " + TableList.VM_VENDOR + " where descr = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, vendorName);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();

                if (rs.next()) {
                    vendorCd = rs.getInt("vendor_cd");
                } else {
                    sql = "insert into " + TableList.VM_VENDOR + " select case when max(vendor_cd) is null then 1 else  max(vendor_cd) +1 END, ? from " + TableList.VM_VENDOR + "";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, vendorName);
                    int count = ps.executeUpdate();
                    if (count > 0) {
                        sql = "select vendor_cd from  " + TableList.VM_VENDOR + " where descr = ?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, vendorName);
                        RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();

                        if (rs1.next()) {
                            vendorCd = rs1.getInt("vendor_cd");
                        }
                    } else {
                        throw new VahanException("Problem in Creating Vendor Details");
                    }
                }
            } else {
                throw new VahanException("Problem in Creating Vendor Details");
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return vendorCd;
    }

    public boolean insertIntoDscSignerCertDtls(DscDobj dob) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        TransactionManager tmgr = null;
        boolean flag = false;
        try {
            tmgr = new TransactionManager("insertIntoDscSignerCertDtls");
            sql = "INSERT INTO " + TableList.VT_DSC_INFORMATION + "(state_cd, off_cd, user_cd,vendor_cd, serial_no,op_dt)\n"
                    + " VALUES (?, ?, ?,?, ?, current_timestamp)";

            ps = tmgr.prepareStatement(sql);
            int k = 1;
            ps.setString(k++, Util.getUserStateCode());
            ps.setInt(k++, Util.getUserLoginOffCode());
            ps.setLong(k++, Long.parseLong(Util.getEmpCode()));
            ps.setInt(k++, dob.getVendorCd());
            ps.setString(k++, dob.getSerialNo());
            int count = ps.executeUpdate();
            if (count > 0) {
                tmgr.commit();
                flag = true;
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

        return flag;
    }

    public DscDobj fetchDscSignerCertDtls() throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        TransactionManagerReadOnly tmgr = null;
        DscDobj dscDobj = null;
        try {

            tmgr = new TransactionManagerReadOnly("fetchDscSignerCertDtls");
            sql = "select serial_no,vendor_cd from " + TableList.VT_DSC_INFORMATION + " where user_cd = ?";

            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(Util.getEmpCode()));

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {
                dscDobj = new DscDobj();
                dscDobj.setSerialNo(rs.getString("serial_no"));
                dscDobj.setVendorCd(rs.getInt("vendor_cd"));
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem In getting Details of Dsc Registration");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dscDobj;
    }

    public ArrayList<DscDobj> fetchDscRegistrationDtls(String var, String serialNum, int vendorCd, String actionType) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        TransactionManagerReadOnly tmgr = null;
        ArrayList<DscDobj> dscDobjList = new ArrayList<>();
        String whereCondition = "";
        try {

            tmgr = new TransactionManagerReadOnly("fetchDscRegistrationDtls");

            if (actionType != null && actionType.equals("dscRegistration")) {
                if (var != null && var.equals("onBasisOfSerialNum")) {
                    whereCondition = "and a.serial_no= ? and a.vendor_cd = ? ";
                } else if (var != null && var.equals("onBasisOfDscVerification")) {
                    whereCondition = "and a.dsc_verify = ? ";
                }

                if (Util.getUserCategory() != null && (Util.getUserCategory().equals(TableConstants.USER_CATG_OFF_STAFF) || Util.getUserCategory().equals(TableConstants.USER_CATG_OFFICE_ADMIN))) {
                    sql = "select a.serial_no,a.vendor_cd,v.descr as vendor_name,a.cert_level,a.cdp_pontl,a.status,a.dsc_user_name,a.cert_valid_upto,a.cert_content,a.digital_sign_to_print,b.cert_serial_no ,b.cert_level as chain_cert_level,b.cdp_pontl as chain_cert_cdp_pont,b.cert_content as chain_cert_content,a.dsc_verify as verify_status from " + TableList.VT_DSC_REGISTRATION + "  a\n"
                            + "inner join " + TableList.VT_DSC_CHAIN_CERT + " b on a.user_cd=b.user_cd and a.serial_no=b.serial_no and a.vendor_cd=b.vendor_cd \n"
                            + "inner join " + TableList.TM_USER_INFO + " c on a.user_cd=c.user_cd and a.state_cd=c.state_cd and a.off_cd=c.off_cd\n"
                            + "left join " + TableList.VM_VENDOR + " v on v.vendor_cd=a.vendor_cd \n"
                            + "where a.state_cd=? and a.off_cd=?  and user_catg<>'" + TableConstants.USER_CATG_DEALER_ADMIN + "' " + whereCondition + "";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, Util.getUserStateCode());
                    ps.setLong(2, Util.getUserLoginOffCode());
                    if (var != null && var.equals("onBasisOfSerialNum")) {
                        ps.setString(3, serialNum);
                        ps.setInt(4, vendorCd);
                    } else if (var != null && var.equals("onBasisOfDscVerification")) {
                        ps.setBoolean(3, true);
                    }
                } else if (Util.getUserCategory() != null && (Util.getUserCategory().equals(TableConstants.USER_CATG_DEALER) || Util.getUserCategory().equals(TableConstants.USER_CATG_DEALER_ADMIN))) {
                    sql = "select a.serial_no,a.vendor_cd,v.descr as vendor_name,a.cert_level,a.cdp_pontl,a.status,a.dsc_user_name,a.cert_valid_upto,a.cert_content,a.digital_sign_to_print,b.cert_serial_no ,b.cert_level as chain_cert_level,b.cdp_pontl as chain_cert_cdp_pont,b.cert_content as chain_cert_content,a.dsc_verify as verify_status from " + TableList.VT_DSC_REGISTRATION + "  a\n"
                            + "inner join " + TableList.VT_DSC_CHAIN_CERT + " b on a.user_cd=b.user_cd and a.serial_no=b.serial_no and a.vendor_cd=b.vendor_cd \n"
                            + "inner join " + TableList.TM_USER_INFO + " c on a.user_cd=c.user_cd and a.state_cd=c.state_cd and a.off_cd=c.off_cd and user_catg='" + TableConstants.USER_CATG_DEALER_ADMIN + "'\n"
                            + "inner join " + TableList.TM_USER_PERMISSIONS + " d on d.user_cd=c.user_cd and d.state_cd=c.state_cd\n"
                            + "left join " + TableList.VM_VENDOR + " v on v.vendor_cd=a.vendor_cd \n"
                            + "where a.state_cd=? and a.off_cd=?  and user_catg='" + TableConstants.USER_CATG_DEALER_ADMIN + "' and dealer_cd in (select dealer_cd from " + TableList.TM_USER_PERMISSIONS + " where user_cd=?) " + whereCondition + "";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, Util.getUserStateCode());
                    ps.setLong(2, Util.getUserLoginOffCode());
                    ps.setLong(3, Long.parseLong(Util.getEmpCode()));
                    if (var != null && var.equals("onBasisOfSerialNum")) {
                        ps.setString(4, serialNum);
                        ps.setInt(5, vendorCd);
                    } else if (var != null && var.equals("onBasisOfDscVerification")) {
                        ps.setBoolean(4, true);
                    }
                }
            } else if (actionType != null && actionType.equals("dscVerification")) {
                sql = "select a.user_cd,a.vendor_cd,v.descr as vendor_name,a.serial_no,a.cert_level,a.cdp_pontl,a.status,a.dsc_user_name,a.cert_valid_upto,a.cert_content,\n"
                        + "a.digital_sign_to_print,b.cert_serial_no ,b.cert_level as chain_cert_level,b.cdp_pontl as chain_cert_cdp_pont,b.cert_content as chain_cert_content,\n"
                        + "a.dsc_verify as verify_status,\n"
                        + "COALESCE(vd.dealer_name, ''::character varying) AS dlr_name,vd.dealer_cd,\n"
                        + "COALESCE(vd.d_add1, ''::character varying)|| ',' ||COALESCE(vd.d_add2, ''::character varying) ||' ' ||COALESCE(''::character varying, ''::character varying)||' '||\n"
                        + "    COALESCE(''::character varying, ''::character varying) ||' '||\n"
                        + "    COALESCE(''::character varying, ''::character varying) ||' '||\n"
                        + "    COALESCE(''::character varying, ''::character varying)  as dealer_address\n"
                        + "from " + TableList.VT_DSC_REGISTRATION + "  a\n"
                        + "inner join " + TableList.VT_DSC_CHAIN_CERT + " b on a.user_cd=b.user_cd and a.serial_no=b.serial_no and a.vendor_cd=b.vendor_cd \n"
                        + "inner join " + TableList.TM_USER_INFO + " c on a.user_cd=c.user_cd and a.state_cd=c.state_cd and a.off_cd=c.off_cd and user_catg='D'\n"
                        + "inner join " + TableList.TM_USER_PERMISSIONS + " d on d.user_cd=c.user_cd and d.state_cd=c.state_cd\n"
                        + "LEFT JOIN " + TableList.VM_DEALER_MAST + " vd ON vd.dealer_cd::text = d.dealer_cd::text\n"
                        + "left join " + TableList.VM_VENDOR + " v on v.vendor_cd=a.vendor_cd \n"
                        + "where a.state_cd=? and a.off_cd=?  and user_catg='" + TableConstants.USER_CATG_DEALER_ADMIN + "'";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setLong(2, Util.getUserLoginOffCode());
            }
            RowSet rs = tmgr.fetchDetachedRowSet();
            String serialNo = null;
            while (rs.next()) {
                if (serialNo == null || !serialNo.equals(rs.getString("serial_no").concat(String.valueOf(rs.getInt("vendor_cd"))))) {
                    DscDobj dscDobj = new DscDobj();
                    serialNo = rs.getString("serial_no").concat(String.valueOf(rs.getInt("vendor_cd")));
                    dscDobj.setSerialNo(rs.getString("serial_no"));
                    dscDobj.setVendorCd(rs.getInt("vendor_cd"));
                    dscDobj.setVendorName(rs.getString("vendor_name"));
                    dscDobj.setCertlevel(rs.getString("cert_level"));
                    dscDobj.setCdpPoint(rs.getString("cdp_pontl"));
                    dscDobj.setActiveStatus(rs.getString("status"));
                    dscDobj.setUserName(rs.getString("dsc_user_name"));
                    dscDobj.setCertValidDate(rs.getDate("cert_valid_upto"));
                    dscDobj.setDigitalSignToPrint(rs.getString("digital_sign_to_print"));
                    dscDobj.setVerifyStatus(rs.getBoolean("verify_status"));
                    if (actionType != null && actionType.equals("dscVerification")) {
                        dscDobj.setDealerName(rs.getString("dlr_name"));
                        dscDobj.setDealerCd(rs.getString("dealer_cd"));
                        dscDobj.setDealerAdminUserCd(rs.getLong("user_cd"));
                    }
                    dscDobjList.add(dscDobj);
                }
            }
            rs.beforeFirst();
            while (rs.next()) {
                DscDobj dscDobj = new DscDobj();
                dscDobj.setSerialNo(rs.getString("cert_serial_no"));
                dscDobj.setVendorCd(rs.getInt("vendor_cd"));
                dscDobj.setVendorName(rs.getString("vendor_name"));
                dscDobj.setCertlevel(rs.getString("chain_cert_level"));
                dscDobj.setCdpPoint(rs.getString("chain_cert_cdp_pont"));
                dscDobj.setActiveStatus(rs.getString("status"));
                dscDobj.setUserName(rs.getString("dsc_user_name"));
                dscDobj.setCertValidDate(rs.getDate("cert_valid_upto"));
                dscDobj.setDigitalSignToPrint(rs.getString("digital_sign_to_print"));
                dscDobj.setVerifyStatus(rs.getBoolean("verify_status"));
                if (actionType != null && actionType.equals("dscVerification")) {
                    dscDobj.setDealerName(rs.getString("dlr_name"));
                    dscDobj.setDealerCd(rs.getString("dealer_cd"));
                    dscDobj.setDealerAdminUserCd(rs.getLong("user_cd"));
                }
                dscDobjList.add(dscDobj);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem In getting Details of Dsc Registration");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dscDobjList;
    }

    public int verifyDealer(List<DscDobj> dealerList) throws VahanException, Exception {
        PreparedStatement psUpdate = null;
        TransactionManager tmgr = null;
        int count = 0;
        try {
            tmgr = new TransactionManager("verify Dealer Dsc");
            for (DscDobj dlr : dealerList) {
                if (dlr.isVerifyStatus()) {
                    String updateSql = "UPDATE " + TableList.VT_DSC_REGISTRATION + "\n"
                            + "   SET  dsc_verify=?\n"
                            + " WHERE user_cd=? and serial_no=? and vendor_cd = ?";

                    psUpdate = tmgr.prepareStatement(updateSql);
                    int i = 1;
                    psUpdate.setBoolean(i++, true);
                    psUpdate.setLong(i++, dlr.getDealerAdminUserCd());
                    psUpdate.setString(i++, dlr.getSerialNo());
                    psUpdate.setInt(i++, dlr.getVendorCd());
                    psUpdate.executeUpdate();
                    count++;
                }
            }
            if (count > 0) {
                tmgr.commit();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return count;
    }

    public boolean checkDscSerialNo(TransactionManager tmgr, String serialNo, int vendorCd) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        boolean isExist = false;
        try {

            sql = "select serial_no from " + TableList.VT_DSC_REGISTRATION + " where serial_no = ? and vendor_cd = ?";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, serialNo);
            ps.setInt(2, vendorCd);

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) {
                isExist = true;
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem In getting Details of Serail No of Dsc");
        }
        return isExist;
    }

    public DscDobj isDscVerified(String serialNo, String vendorName) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        TransactionManagerReadOnly tmgr = null;
        DscDobj dscDobjExist = null;
        try {

            tmgr = new TransactionManagerReadOnly("isDscVerified");
            sql = "select serial_no, dsc_verify,vendor_cd from " + TableList.VT_DSC_REGISTRATION + " where serial_no = ? and vendor_cd in (select vendor_cd from " + TableList.VM_VENDOR + " where descr = ? )";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, serialNo);
            ps.setString(2, vendorName);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {
                dscDobjExist = new DscDobj();
                dscDobjExist.setSerialNo(rs.getString("serial_no"));
                dscDobjExist.setVerifyStatus(rs.getBoolean("dsc_verify"));
                dscDobjExist.setVendorCd(rs.getInt("vendor_cd"));
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem In getting Details of Serail No of Dsc");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dscDobjExist;
    }

    public boolean insertDocDetailsApplNoWise(DscDobj dob, DocumentDetailsDobj docDobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        TransactionManager tmgr = null;
        boolean flag = false;
        int count = 0;
        try {
            tmgr = new TransactionManager("insertDocDetailsApplNoWise");

            sql = "select serial_no||vendor_cd as serial_no from " + TableList.VT_DSC_DOC_DETAILS + " where appl_no = ? and doc_catg = ? ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, docDobj.getApplNo());
            ps.setString(2, docDobj.getCatId());

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {

                if (rs.getString("serial_no") != null && !rs.getString("serial_no").equalsIgnoreCase(dob.getSerialNo().concat(String.valueOf(dob.getVendorCd())))) {

                    sql = "INSERT INTO " + TableList.VH_DSC_DOC_DETAILS
                            + " SELECT current_timestamp as moved_on, ? as moved_by ,* "
                            + "  FROM  " + TableList.VT_DSC_DOC_DETAILS
                            + " WHERE  appl_no = ? and doc_catg = ?  ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setLong(1, Long.parseLong(Util.getEmpCode()));
                    ps.setString(2, docDobj.getApplNo());
                    ps.setString(3, docDobj.getCatId());
                    ps.executeUpdate();

                    sql = "DELETE FROM " + TableList.VT_DSC_DOC_DETAILS + " WHERE  appl_no = ? and doc_catg = ? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, docDobj.getApplNo());
                    ps.setString(2, docDobj.getCatId());
                    ps.executeUpdate();

                    count = this.insertDscDocDetails(tmgr, dob, docDobj);
                }

            } else {
                count = this.insertDscDocDetails(tmgr, dob, docDobj);
            }

            if (count > 0) {
                tmgr.commit();
                flag = true;
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

        return flag;
    }

    public int insertDscDocDetails(TransactionManager tmgr, DscDobj dob, DocumentDetailsDobj docDobj) throws Exception {
        int count = 0;
        String sql = "INSERT INTO " + TableList.VT_DSC_DOC_DETAILS + "(\n"
                + " state_cd, off_cd, user_cd, appl_no, doc_catg, doc_subcatg, vendor_cd, serial_no, op_dt)\n"
                + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";

        PreparedStatement ps = tmgr.prepareStatement(sql);
        int k = 1;
        ps.setString(k++, Util.getUserStateCode());
        ps.setInt(k++, Util.getUserSeatOffCode());
        ps.setLong(k++, Long.parseLong(Util.getEmpCode()));
        ps.setString(k++, docDobj.getApplNo());
        ps.setString(k++, docDobj.getCatId());
        ps.setString(k++, docDobj.getSubCatg());
        ps.setInt(k++, dob.getVendorCd());
        ps.setString(k++, dob.getSerialNo());
        count = ps.executeUpdate();
        return count;
    }

    public ArrayList<DocumentDetailsDobj> getDocDigitalSignedDetails(String applNo) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        TransactionManagerReadOnly tmgr = null;
        ArrayList<DocumentDetailsDobj> docDetailsList = new ArrayList<>();
        try {

            tmgr = new TransactionManagerReadOnly("checkDscSerialNo");
            sql = "select appl_no,state_cd,doc_catg from " + TableList.VT_DSC_DOC_DETAILS + " where appl_no = ?";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);

            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                DocumentDetailsDobj dobj = new DocumentDetailsDobj();
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setStateCd(rs.getString("state_cd"));
                dobj.setCatId(rs.getString("doc_catg"));
                docDetailsList.add(dobj);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem In getting Doc Details of Digitally Signed");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return docDetailsList;
    }
}
