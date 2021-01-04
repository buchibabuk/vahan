/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.permit.Fees_Slab_dobj;
import nic.vahan.form.dobj.permit.VmPermitCatgDobj;
import nic.vahan.form.dobj.permit.VmPermitFeeMasterDobj;
import nic.vahan.form.dobj.permit.VmPermitFeeStateConfig;
import nic.vahan.form.dobj.permit.VmPermitRegionDobj;
import nic.vahan.form.dobj.permit.VmPermitRouteDobj;
import nic.vahan.form.dobj.permit.VmPermitStateConfig;
import nic.vahan.form.dobj.permit.VmPermitStateMap;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author manoj
 */
public class MasterPermitTableImpl implements Serializable {

    private static final Logger logger = Logger.getLogger(MasterPermitTableImpl.class);
    private List arrayPurposeType = new ArrayList();

    public List getArrayPurposeType() {
        String[][] data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
        for (int i = 0; i < data.length; i++) {
            if ((Integer.parseInt(data[i][0]) >= 25 && Integer.parseInt(data[i][0]) <= 47) || Integer.parseInt(data[i][0]) == TableConstants.COURT_PUR_FEE
                    || Integer.parseInt(data[i][0]) == TableConstants.VM_PMT_COMPOSIT_FEE_PUR_CD || Integer.parseInt(data[i][0]) == TableConstants.POSTAL_PUR_CD || Integer.parseInt(data[i][0]) == TableConstants.SOCIETY_FEE_PUR || Integer.parseInt(data[i][0]) == TableConstants.SCAN_FEE_PUR) {
                arrayPurposeType.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
        return arrayPurposeType;
    }

    public void setArrayPurposeType(List aArrayPurposeType) {
        arrayPurposeType = aArrayPurposeType;
    }

    public ArrayList<Fees_Slab_dobj> getFeesSlabNewDetail(int pur_code) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String sql = null;
        ArrayList<Fees_Slab_dobj> fees_Slab_dobjs_list = new ArrayList<>();
        try {
            tmgr = new TransactionManager("getTaxSlabNewDetail()");
            sql = "select a.sr_no,a.trans_pur_cd,a.condition_formula,a.fee_rate_formula,a.fine_rate_formula,a.fine_max_amt,a.check_max_amt,a.period_mode,a.per_region_count,a.per_route_count,b.descr as trans_pur_descr\n"
                    + "from permit.vm_permit_fee_slab a \n"
                    + "inner join vahan4.tm_purpose_mast b ON a.trans_pur_cd=b.pur_cd\n"
                    + "where a.pur_cd=? and a.state_cd=? \n"
                    + "order by a.sr_no,a.trans_pur_cd";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, pur_code);
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                Fees_Slab_dobj feesSlab_dobj = new Fees_Slab_dobj();
                feesSlab_dobj.setSr_no(rs.getInt("sr_no"));
                feesSlab_dobj.setTrans_pur_cd(rs.getString("trans_pur_cd"));
                feesSlab_dobj.setCondition_formula(rs.getString("condition_formula"));
                feesSlab_dobj.setFee_rate_formula(rs.getString("fee_rate_formula"));
                feesSlab_dobj.setFine_rate_formula(rs.getString("fine_rate_formula"));
                feesSlab_dobj.setPeriod_mode(rs.getString("period_mode"));
                feesSlab_dobj.setFine_max_amt(rs.getString("fine_max_amt"));
                feesSlab_dobj.setCheck_max_amt(String.valueOf(rs.getBoolean("check_max_amt")));
                feesSlab_dobj.setPer_region_count(String.valueOf(rs.getBoolean("per_region_count")));
                feesSlab_dobj.setPer_route_count(String.valueOf(rs.getBoolean("per_route_count")));
                feesSlab_dobj.setTrans_pur_descr(rs.getString("trans_pur_descr"));
                fees_Slab_dobjs_list.add(feesSlab_dobj);
            }
        } catch (Exception e) {
            logger.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                logger.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return fees_Slab_dobjs_list;
    }

    public int missingSr_no(int purpose_code) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String query = "select case when min(sr_no) > 1 "
                + "then 1 "
                + "else "
                + "(select sr_no+1 from permit.vm_permit_fee_slab pfs "
                + "where not exists"
                + "( select * from permit.vm_permit_fee_slab where sr_no = pfs.sr_no+1 and state_cd=? and pur_cd=?) and state_cd=? and pur_cd=? limit 1) end \"missing_sr\" from permit.vm_permit_fee_slab where state_cd=? and pur_cd=?";
        try {
            tmgr = new TransactionManager("missingSr_no()");
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, purpose_code);
            ps.setString(3, Util.getUserStateCode());
            ps.setInt(4, purpose_code);
            ps.setString(5, Util.getUserStateCode());
            ps.setInt(6, purpose_code);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                return rs.getInt("missing_sr");
            }

        } catch (Exception e) {
            logger.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                logger.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return 0;
    }

    public void addUpdateRecords(Fees_Slab_dobj fsbd, String saveButton, String purpose_code) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String query;

        try {
            tmgr = new TransactionManager("addUpdateRecords");
            switch (saveButton) {
                case "EditSlab":
                    query = "UPDATE permit.vm_permit_fee_slab "
                            + "SET "
                            + "trans_pur_cd = ?, condition_formula = ?, fee_rate_formula = ?, "
                            + "fine_rate_formula = ?, period_mode = ?, fine_max_amt = ?, "
                            + "check_max_amt = ?, per_region_count=?, per_route_count=? "
                            + "WHERE "
                            + "pur_cd = ? AND sr_no = ? AND state_cd=?";
                    ps = tmgr.prepareStatement(query);

                    break;
                case "AddNewSlab":
                    query = "INSERT INTO permit.vm_permit_fee_slab ("
                            + "trans_pur_cd, condition_formula, fee_rate_formula, "
                            + "fine_rate_formula, period_mode, fine_max_amt,"
                            + "check_max_amt, per_region_count, per_route_count,"
                            + " pur_cd, sr_no, state_cd"
                            + ")\n"
                            + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
                    ps = tmgr.prepareStatement(query);
                    break;
            }
            ps.setInt(1, Integer.parseInt(fsbd.getTrans_pur_cd()));
            ps.setString(2, fsbd.getCondition_formula());
            ps.setString(3, fsbd.getFee_rate_formula());
            ps.setString(4, fsbd.getFine_rate_formula());
            ps.setString(5, fsbd.getPeriod_mode());
            ps.setString(6, fsbd.getFine_max_amt());
            ps.setBoolean(7, Boolean.parseBoolean(fsbd.getCheck_max_amt()));
            ps.setBoolean(8, Boolean.parseBoolean(fsbd.getPer_region_count()));
            ps.setBoolean(9, Boolean.parseBoolean(fsbd.getPer_route_count()));
            ps.setInt(10, Integer.parseInt(purpose_code));
            ps.setInt(11, fsbd.getSr_no());
            ps.setString(12, Util.getUserStateCode());
            ps.executeUpdate();
            tmgr.commit();

        } catch (SQLException | NumberFormatException e) {
            logger.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                logger.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
    }

    public List<VmPermitCatgDobj> pmtCatgData() {
        RowSet rs;
        PreparedStatement ps;
        TransactionManager tmgr = null;
        List<VmPermitCatgDobj> list = null;
        try {
            tmgr = new TransactionManager("detailData");
            list = new ArrayList<>();
            String sql = "select * from " + TableList.VM_PERMIT_CATG + " where state_cd = ? order by code";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                VmPermitCatgDobj dobj = new VmPermitCatgDobj();
                dobj.setState_code(rs.getString("state_cd"));
                dobj.setCcode(String.valueOf(rs.getInt("code")));
                dobj.setDescription(rs.getString("descr"));
                dobj.setPermit_code(rs.getInt("permit_type"));
                dobj.setPmt_type_descr(getPmtTypeDescr(rs.getInt("permit_type")));
                dobj.setFlag(rs.getBoolean("pmt_offer_flag"));
                list.add(dobj);
            }
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return list;

    }

    public List<VmPermitRouteDobj> getRouteData() {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        List<VmPermitRouteDobj> routeList = null;
        RowSet rs = null;
        try {
            routeList = new ArrayList<>();
            tmgr = new TransactionManager("getRouteData");
            String sql = "select * from " + TableList.VM_ROUTE_MASTER + " where state_cd = ? and off_cd=? order by code";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserOffCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                VmPermitRouteDobj dobj = new VmPermitRouteDobj();
                dobj.setState_code(rs.getString("state_cd"));
                dobj.setOff_code(rs.getInt("off_cd"));
                dobj.setRoute_code(rs.getString("code"));
                dobj.setFrom_loc(rs.getString("floc"));
                dobj.setVia(rs.getString("via"));
                dobj.setTo_loc(rs.getString("tloc"));
                dobj.setLength(rs.getInt("rlength"));
                dobj.setRegion_cover(rs.getInt("regions_covered"));
                dobj.setNhOverlapping(rs.getString("overlapping"));
                dobj.setNhOverlappingLength(rs.getInt("overlapping_length"));
                routeList.add(dobj);
            }

        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return routeList;

    }

    public List<VmPermitRegionDobj> getRegionData() {
        TransactionManager tmgr = null;
        List<VmPermitRegionDobj> regionList = null;
        PreparedStatement ps;
        VmPermitRegionDobj dobj = null;
        RowSet rs = null;
        try {
            tmgr = new TransactionManager("getRegionData");
            regionList = new ArrayList<>();
            String sql = "select * from " + TableList.VM_REGION + " where state_cd = ? and off_cd=? order by region_cd";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserOffCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new VmPermitRegionDobj();
                dobj.setState_code(rs.getString("state_cd"));
                dobj.setOff_code(rs.getInt("off_cd"));
                dobj.setCode(rs.getInt("region_cd"));
                dobj.setRegion(rs.getString("region"));
                dobj.setRegion_covered(rs.getInt("regions_covered"));
                regionList.add(dobj);
            }
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return regionList;

    }

    public List<VmPermitFeeMasterDobj> getFeeData() {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs = null;
        String pmt_type = "";
        String vh_class = "";
        String up_vh_class = "";
        List<VmPermitFeeMasterDobj> feeList = null;
        VmPermitFeeMasterDobj dobj = null;
        try {
            tmgr = new TransactionManager("getFeeData");
            feeList = new ArrayList<>();
            String sql = "select * from " + TableList.VM_PERMIT_FEE + " where state_cd = ? order by pur_cd,pmt_type";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {

                dobj = new VmPermitFeeMasterDobj();
                dobj.setState_code(rs.getString("state_cd"));
                dobj.setPurpose_code(rs.getInt("pur_cd"));
                dobj.setPur_cd_descr(getPurposeDescr(rs.getInt("pur_cd")));
                dobj.setPermit_type(rs.getInt("pmt_type"));
                pmt_type = getPmtTypeDescr(rs.getInt("pmt_type"));
                if (pmt_type.equalsIgnoreCase("")) {
                    dobj.setPmt_type_descr("0");
                    dobj.setPermit_type(0);
                } else {
                    dobj.setPmt_type_descr(pmt_type);
                    dobj.setPermit_type(rs.getInt("pmt_type"));
                }
                if (rs.getInt("pmt_catg") == 0) {
                    dobj.setPmt_catg_descr(String.valueOf(rs.getInt("pmt_catg")));
                    dobj.setPermit_catg(rs.getInt("pmt_catg"));
                } else {
                    dobj.setPmt_catg_descr(getPmtCatgDescr(rs.getInt("pmt_catg"), rs.getString("state_cd")));
                    dobj.setPermit_catg(rs.getInt("pmt_catg"));
                }

                if (rs.getInt("l_vh_class") == 0) {
                    dobj.setL_vh_class_descr("Lower Vehicle Class");
                    dobj.setL_vh_class(0);
                } else {
                    vh_class = getVhClassDescr(rs.getInt("l_vh_class"));
                    if (vh_class.equalsIgnoreCase("")) {
                        dobj.setL_vh_class_descr(String.valueOf(rs.getInt("l_vh_class")));
                        dobj.setL_vh_class(rs.getInt("l_vh_class"));
                    } else {
                        dobj.setL_vh_class_descr(vh_class);
                        dobj.setL_vh_class(rs.getInt("l_vh_class"));
                    }
                }
                if (rs.getInt("u_vh_class") == 999) {
                    dobj.setU_vh_class_descr("Upper Vehicle Class");
                    dobj.setU_vh_class(999);
                } else {
                    up_vh_class = getVhClassDescr(rs.getInt("u_vh_class"));
                    if (up_vh_class.equalsIgnoreCase("")) {
                        dobj.setU_vh_class_descr(String.valueOf(rs.getInt("u_vh_class")));
                        dobj.setU_vh_class(rs.getInt("u_vh_class"));
                    } else {
                        dobj.setU_vh_class_descr(up_vh_class);
                        dobj.setU_vh_class(rs.getInt("u_vh_class"));
                    }
                }
                dobj.setL_seat_cap(rs.getInt("l_seat_cap"));
                dobj.setU_seat_cap(rs.getInt("u_seat_cap"));
                dobj.setL_unld_wt(rs.getInt("l_unld_wt"));
                dobj.setU_unld_wt(rs.getInt("u_unld_wt"));
                dobj.setL_ld_wt(rs.getInt("l_ld_wt"));
                dobj.setU_ld_wt(rs.getInt("u_ld_wt"));
                dobj.setPer_region(rs.getString("per_region"));
                dobj.setPer_period(rs.getString("per_period"));
                dobj.setFee(rs.getInt("fee"));
                dobj.setFine(rs.getInt("fine"));
                feeList.add(dobj);
            }

        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return feeList;

    }

    public String getPurposeDescr(int pur_cd) {
        String pur_descr = "";
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs = null;
        try {
            tmgr = new TransactionManager("getPurposeDescr");
            String sql = "Select descr from " + TableList.TM_PURPOSE_MAST + " where pur_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, pur_cd);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                pur_descr = rs.getString("descr");
            }
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return pur_descr;
    }

    public String getDocDescr(String doc_id) {
        String doc_id_descr = "";
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs = null;
        try {
            tmgr = new TransactionManager("getDocDescr");
            String sql = "Select descr from " + TableList.VM_PERMIT_DOCUMENTS + " where doc_id=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, doc_id);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                doc_id_descr = rs.getString("descr");
            }
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }

        return doc_id_descr;
    }

    public String getPmtTypeDescr(int pmt_type) {
        String pmt_descr = "";
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs = null;
        try {
            tmgr = new TransactionManager("getPmtTypeDescr");
            String sql = "Select descr from " + TableList.VM_PERMIT_TYPE + " where code=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, pmt_type);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                pmt_descr = rs.getString("descr");
            }
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }

        return pmt_descr;
    }

    public String getPmtCatgDescr(int pmt_catg, String state_cd) {
        String pmt_descr = "";
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs = null;
        try {
            tmgr = new TransactionManager("getPmtCatgDescr");
            String sql = "Select descr from " + TableList.VM_PERMIT_CATG + " where code=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, pmt_catg);
            ps.setString(2, state_cd);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                pmt_descr = rs.getString("descr");
            }
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }

        return pmt_descr;
    }

    public String getVhClassDescr(int vh_class) {
        String vh_descr = "";
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs = null;
        try {
            tmgr = new TransactionManager("getVhClassDescr");
            String sql = "Select descr from " + TableList.VM_VH_CLASS + " where vh_class=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, vh_class);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                vh_descr = rs.getString("descr");
            }
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return vh_descr;
    }

    public List<VmPermitStateMap> getStateMapDoc() {
        TransactionManager tmgr = null;
        String doc_id = "";
        List<VmPermitStateMap> docmapList = null;
        VmPermitStateMap dobj = null;
        PreparedStatement ps;
        String pmt_type = "";
        RowSet rs = null;
        try {
            tmgr = new TransactionManager("getStateMapDoc");
            docmapList = new ArrayList<>();
            String sql = "select * from " + TableList.VM_PERMIT_DOC_STATE_MAP + " where state_cd = ? order by pur_cd";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new VmPermitStateMap();
                StringBuilder sb = new StringBuilder();
                dobj.setState_code(rs.getString("state_cd"));
                dobj.setPurpose_code(rs.getInt("pur_cd"));
                dobj.setPur_cd_descr(getPurposeDescr(rs.getInt("pur_cd")));
                dobj.setPermit_code(rs.getInt("pmt_type"));
                pmt_type = getPmtTypeDescr(rs.getInt("pmt_type"));
                if (pmt_type.equalsIgnoreCase("")) {
                    dobj.setPmt_type_descr("0");
                } else {
                    dobj.setPmt_type_descr(pmt_type);
                }
                String[] tokensVal = rs.getString("doc_id").split(",");
                for (int i = 0; i < tokensVal.length; i++) {
                    doc_id = getDocDescr(tokensVal[i]);
                    sb.append(doc_id);
                    sb.append(",");
                }
                dobj.setDocumentid(sb.toString());
                dobj.setDocumentList(tokensVal);
                dobj.setSelectDocList(getDocumentList(dobj.getPurpose_code(), dobj.getPermit_code()));
                docmapList.add(dobj);
            }
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return docmapList;

    }

    public List getDocumentList(int pur_cd, int pmt_type) {
        List docList = new ArrayList();
        String[][] data = MasterTableFiller.masterTables.VM_PERMIT_DOCUMENTS.getData();
        if (pur_cd == TableConstants.VM_PMT_FRESH_PUR_CD && pmt_type == Integer.parseInt(TableConstants.AITP)
                || pur_cd == TableConstants.VM_PMT_FRESH_PUR_CD && pmt_type == Integer.parseInt(TableConstants.NATIONAL_PERMIT)
                || pur_cd == TableConstants.VM_PMT_RENEWAL_PUR_CD && pmt_type == Integer.parseInt(TableConstants.AITP)
                || pur_cd == TableConstants.VM_PMT_RENEWAL_PUR_CD && pmt_type == Integer.parseInt(TableConstants.NATIONAL_PERMIT)
                || pur_cd == TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD && pmt_type == Integer.parseInt(TableConstants.AITP)
                || pur_cd == TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD && pmt_type == Integer.parseInt(TableConstants.NATIONAL_PERMIT)) {

            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equals("1") || data[i][0].equals("2") || data[i][0].equals("5")) {
                    docList.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
        } else if (pur_cd == TableConstants.VM_PMT_APPLICATION_PUR_CD && pmt_type == 0) {
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equals("3")) {
                    docList.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
        } else if (pur_cd == TableConstants.VM_PMT_SPECIAL_PUR_CD && pmt_type == 0) {
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equals("7")) {
                    docList.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
        } else if (pur_cd == TableConstants.VM_PMT_TEMP_PUR_CD && pmt_type == 0) {
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equals("6")) {
                    docList.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
        } else if (pur_cd == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD && pmt_type == 0) {
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equals("5")) {
                    docList.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
        } else {
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equals("1") || data[i][0].equals("2")) {
                    docList.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
        }
        return docList;
    }

    public List<VmPermitFeeStateConfig> getFeeStateConfig() {
        String pmt_type = "";
        TransactionManager tmgr = null;
        List<VmPermitFeeStateConfig> feeStateList = null;
        VmPermitFeeStateConfig dobj = null;
        PreparedStatement ps;
        RowSet rs = null;
        try {
            tmgr = new TransactionManager("getFeeStateConfig");
            feeStateList = new ArrayList<>();
            String sql = "select * from " + TableList.VM_PERMIT_FEE_STATE_CONFIGURATION + " where state_cd = ? order by pur_cd,pmt_type";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new VmPermitFeeStateConfig();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setPur_cd(rs.getInt("pur_cd"));
                dobj.setPur_cd_descr(getPurposeDescr(rs.getInt("pur_cd")));
                dobj.setPmt_type(rs.getInt("pmt_type"));
                pmt_type = getPmtTypeDescr(rs.getInt("pmt_type"));
                if (pmt_type.equalsIgnoreCase("")) {
                    dobj.setPmt_cd_descr("0");
                } else {
                    dobj.setPmt_cd_descr(pmt_type);
                }
                dobj.setPmt_type_flag(rs.getBoolean("pmt_type_flag"));
                dobj.setPmt_catg_flag(rs.getBoolean("pmt_catg_flag"));
                dobj.setVh_class_flag(rs.getBoolean("vh_class_flag"));
                dobj.setSeat_cap_flag(rs.getBoolean("seat_cap_flag"));
                dobj.setUnld_wt_flag(rs.getBoolean("unld_wt_flag"));
                dobj.setLd_wt_flag(rs.getBoolean("ld_wt_flag"));
                dobj.setPer_region_flag(rs.getBoolean("per_region_flag"));
                dobj.setPer_period_flag(rs.getBoolean("per_period_flag"));
                feeStateList.add(dobj);
            }
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return feeStateList;
    }

    public List<VmPermitStateConfig> getStateConfigMaster(List sur_pur_list) {
        TransactionManager tmgr = null;
        List<VmPermitStateConfig> stateList = null;
        VmPermitStateConfig dobj = null;
        String pur_descr = "";
        PreparedStatement ps;
        RowSet rs = null;
        try {
            tmgr = new TransactionManager("getStateConfigMaster");
            stateList = new ArrayList<>();
            String sql = "select * from " + TableList.VM_PERMIT_STATE_CONFIGURATION + " where state_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new VmPermitStateConfig();
                StringBuilder sb = new StringBuilder();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setTemp_days_valid_upto(rs.getInt("temp_days_valid_upto"));
                dobj.setTemp_weeks_valid_upto(rs.getInt("temp_weeks_valid_upto"));
                dobj.setSpec_days_valid_upto(rs.getInt("spec_days_valid_upto"));
                dobj.setSpec_weeks_valid_upto(rs.getInt("spec_weeks_valid_upto"));
                dobj.setSc_renew_before_days(rs.getInt("sc_renew_before_days"));
                dobj.setCc_renew_before_days(rs.getInt("cc_renew_before_days"));
                dobj.setAi_renew_before_days(rs.getInt("ai_renew_before_days"));
                dobj.setPsv_renew_before_days(rs.getInt("psv_renew_before_days"));
                dobj.setGp_renew_before_days(rs.getInt("gp_renew_before_days"));
                dobj.setNp_renew_before_days(rs.getInt("np_renew_before_days"));
                dobj.setSc_rule_heading(rs.getString("sc_rule_heading"));
                dobj.setSc_formno_heading(rs.getString("sc_formno_heading"));
                dobj.setCc_rule_heading(rs.getString("cc_rule_heading"));
                dobj.setCc_formno_heading(rs.getString("cc_formno_heading"));
                dobj.setAitp_rule_heading(rs.getString("aitp_rule_heading"));
                dobj.setAitp_formno_heading(rs.getString("aitp_formno_heading"));
                dobj.setPsvp_rule_heading(rs.getString("psvp_rule_heading"));
                dobj.setPsvp_formno_heading(rs.getString("psvp_formno_heading"));
                dobj.setGp_rule_heading(rs.getString("gp_rule_heading"));
                dobj.setGp_formno_heading(rs.getString("gp_formno_heading"));
                dobj.setNp_rule_heading(rs.getString("np_rule_heading"));
                dobj.setNp_formno_heading(rs.getString("np_formno_heading"));
                dobj.setTemp_rule_heading(rs.getString("temp_rule_heading"));
                dobj.setTemp_formno_heading(rs.getString("temp_formno_heading"));
                dobj.setSpec_rule_heading(rs.getString("spec_rule_heading"));
                dobj.setSpec_formno_heading(rs.getString("spec_formno_heading"));
                dobj.setAuth_aitp_rule_heading(rs.getString("auth_aitp_rule_heading"));
                dobj.setAuth_aitp_formno_heading(rs.getString("auth_aitp_formno_heading"));
                dobj.setAuth_np_rule_heading(rs.getString("auth_np_rule_heading"));
                dobj.setAuth_np_formno_heading(rs.getString("auth_np_formno_heading"));
                dobj.setNote_in_footer(rs.getString("note_in_footer"));
                dobj.setTemp_days_valid_from(rs.getInt("temp_days_valid_from"));
                dobj.setTemp_weeks_valid_from(rs.getInt("temp_weeks_valid_from"));
                dobj.setSpec_days_valid_from(rs.getInt("spec_days_valid_from"));
                dobj.setSpec_weeks_valid_from(rs.getInt("spec_weeks_valid_from"));
                dobj.setSc_renew_after_days(rs.getInt("sc_renew_after_days"));
                dobj.setCc_renew_after_days(rs.getInt("cc_renew_after_days"));
                dobj.setAi_renew_after_days(rs.getInt("ai_renew_after_days"));
                dobj.setPsv_renew_after_days(rs.getInt("psv_renew_after_days"));
                dobj.setGp_renew_after_days(rs.getInt("gp_renew_after_days"));
                dobj.setNp_renew_after_days(rs.getInt("np_renew_after_days"));
                dobj.setRenewal_of_permit_valid_from_flag(rs.getString("renewal_of_permit_valid_from_flag"));
                dobj.setTemp_route_area(rs.getBoolean("temp_route_area"));
                dobj.setGenrate_ol_appl(rs.getBoolean("genrate_ol_appl"));
                dobj.setPermanent_permit_valid(rs.getBoolean("permanent_permit_valid"));
                String[] tokensVal = rs.getString("allowed_surr_pur_cd").split(",");
                if (rs.getString("allowed_surr_pur_cd").equalsIgnoreCase("ANY")) {
                    dobj.setAllowed_surr_pur_cd(rs.getString("allowed_surr_pur_cd"));
                    dobj.setSur_pur_cd("ANY");
                    dobj.setSurPurList(tokensVal);
                } else {
                    for (int i = 0; i < tokensVal.length; i++) {
                        if (tokensVal[i].equalsIgnoreCase("ANY")) {
                            pur_descr = "ANY";
                            sb.append(pur_descr);
                            sb.append(",");
                        } else {
                            pur_descr = getPurposeDescr(Integer.parseInt(tokensVal[i].toString()));
                            sb.append(pur_descr);
                            sb.append(",");
                        }
                    }
                    dobj.setSur_pur_cd(sb.toString());
                    dobj.setSurPurList(tokensVal);
                }
                dobj.setSalectedPurList(sur_pur_list);
                dobj.setTemp_pmt_type(rs.getBoolean("temp_pmt_type"));
                dobj.setRenew_temp_pmt(rs.getBoolean("renew_temp_pmt"));
                dobj.setSpl_pmt_route(rs.getBoolean("spl_pmt_route"));
                stateList.add(dobj);
            }
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return stateList;

    }

    public boolean permitCategoryRecord(VmPermitCatgDobj pmt_dobj, String data) {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        String sql = "";
        try {
            tmgr = new TransactionManager("permitCategoryRecord");
            if (data.equals("insertData")) {
                sql = "INSERT INTO " + TableList.VM_PERMIT_CATG + "(\n"
                        + "descr,permit_type,pmt_offer_flag,state_cd, code)\n"
                        + "VALUES (?,?,?,?,?)";
            } else if (data.equals("updateData")) {
                sql = "UPDATE " + TableList.VM_PERMIT_CATG + "\n"
                        + "   SET descr=?,permit_type=?,pmt_offer_flag=?\n"
                        + " WHERE state_cd=? and code=?";
            }
            psmt = tmgr.prepareStatement(sql);
            int i = 1;
            psmt.setString(i++, pmt_dobj.getDescription());
            psmt.setInt(i++, pmt_dobj.getPermit_code());
            psmt.setBoolean(i++, pmt_dobj.isFlag());
            psmt.setString(i++, pmt_dobj.getState_code());
            psmt.setInt(i++, Integer.parseInt(pmt_dobj.getCcode()));
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public boolean permitRouteRecord(VmPermitRouteDobj pmt_route_dobj, String data) {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        String sql = "";
        try {
            tmgr = new TransactionManager("permitRouteRecord");
            if (data.equals("insertData")) {
                sql = "INSERT INTO " + TableList.VM_ROUTE_MASTER + "(\n"
                        + "floc,via,tloc,rlength,regions_covered";
                if (pmt_route_dobj.getRoute_flag() != null && !pmt_route_dobj.getRoute_flag().trim().equals("")) {
                    sql += ",newroute_cd,route_flag";
                }
                if (!CommonUtils.isNullOrBlank(pmt_route_dobj.getNhOverlapping())) {
                    sql += ",overlapping,overlapping_length";
                }
                if (!CommonUtils.isNullOrBlank(pmt_route_dobj.getState_to())) {
                    sql += ",state_to";
                }
                sql += ",state_cd,off_cd,code)\n";
                sql += "VALUES (?,?,?,?,?";
                if (pmt_route_dobj.getRoute_flag() != null && !pmt_route_dobj.getRoute_flag().trim().equals("")) {
                    sql += ",?,?";
                }
                if (!CommonUtils.isNullOrBlank(pmt_route_dobj.getNhOverlapping())) {
                    sql += ",?,?";
                }
                if (!CommonUtils.isNullOrBlank(pmt_route_dobj.getState_to())) {
                    sql += ",?";
                }
                sql += ",?,?,?)";
            }
            if (data.equals("updateData")) {
                sql = "UPDATE " + TableList.VM_ROUTE_MASTER + "\n"
                        + "   SET floc=?,via=?,tloc=?,rlength=?,regions_covered=?";
                if (pmt_route_dobj.getRoute_flag() != null && !pmt_route_dobj.getRoute_flag().trim().equals("")) {
                    sql += ",newroute_cd=?,route_flag=?\n";
                }
                if (!CommonUtils.isNullOrBlank(pmt_route_dobj.getNhOverlapping())) {
                    sql += ",overlapping=?,overlapping_length=?";
                }
                sql += " WHERE state_cd = ? and off_cd=? and code= ? ";
            }
            psmt = tmgr.prepareStatement(sql);
            int i = 1;
            psmt.setString(i++, pmt_route_dobj.getFrom_loc());
            psmt.setString(i++, pmt_route_dobj.getVia());
            psmt.setString(i++, pmt_route_dobj.getTo_loc());
            psmt.setInt(i++, pmt_route_dobj.getLength());
            psmt.setInt(i++, pmt_route_dobj.getRegion_cover());
            if (pmt_route_dobj.getRoute_flag() != null && !pmt_route_dobj.getRoute_flag().trim().equals("")) {
                psmt.setString(i++, pmt_route_dobj.getNew_route_code());
                psmt.setString(i++, pmt_route_dobj.getRoute_flag());
            }
            if (!CommonUtils.isNullOrBlank(pmt_route_dobj.getNhOverlapping())) {
                psmt.setString(i++, pmt_route_dobj.getNhOverlapping());
                psmt.setInt(i++, pmt_route_dobj.getNhOverlappingLength());
            }
            if (!CommonUtils.isNullOrBlank(pmt_route_dobj.getState_to())) {
                psmt.setString(i++, pmt_route_dobj.getState_to());
            }
            psmt.setString(i++, pmt_route_dobj.getState_code());
            psmt.setInt(i++, pmt_route_dobj.getOff_code());
            psmt.setString(i++, pmt_route_dobj.getRoute_code());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }

        return flag;
    }

    public boolean permitRegionRecord(VmPermitRegionDobj pmt_dobj, String data) {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        String sql = "";
        try {
            tmgr = new TransactionManager("permitRegionRecord");
            if (data.equals("insertData")) {
                sql = "INSERT INTO " + TableList.VM_REGION + "(\n"
                        + "region,regions_covered,state_cd,off_cd,region_cd)\n"
                        + "VALUES (?,?,?,?,?)";
            }
            if (data.equals("updateData")) {
                sql = "UPDATE " + TableList.VM_REGION + "\n"
                        + "   SET region=?,regions_covered=?\n"
                        + " WHERE state_cd=? and off_cd=? and region_cd=?";
            }
            psmt = tmgr.prepareStatement(sql);
            int i = 1;
            psmt.setString(i++, pmt_dobj.getRegion().toUpperCase());
            psmt.setInt(i++, pmt_dobj.getRegion_covered());
            psmt.setString(i++, pmt_dobj.getState_code());
            psmt.setInt(i++, pmt_dobj.getOff_code());
            psmt.setInt(i++, pmt_dobj.getCode());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public boolean permitFeeRecord(VmPermitFeeMasterDobj pmt_dobj, String data) {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        String sql = "";
        try {
            tmgr = new TransactionManager("permitFeeRecord");
            if (data.equals("insertData")) {
                sql = "INSERT INTO " + TableList.VM_PERMIT_FEE + "(\n"
                        + "l_unld_wt,u_unld_wt,l_ld_wt,u_ld_wt,per_period,fee,fine,state_cd, pur_cd,pmt_type,pmt_catg,l_vh_class,u_vh_class,l_seat_cap,u_seat_cap,per_region)\n"
                        + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            }
            if (data.equals("updateData")) {
                sql = "UPDATE " + TableList.VM_PERMIT_FEE + "\n"
                        + " SET l_unld_wt=?,u_unld_wt=?,l_ld_wt=?,u_ld_wt=?,per_period=?,fee=?,fine=?\n"
                        + " WHERE state_cd=? and pur_cd=? and pmt_type=? and pmt_catg=? and l_vh_class=? and u_vh_class=? and  l_seat_cap=? and u_seat_cap=? and per_region=? ";
            }
            psmt = tmgr.prepareStatement(sql);
            int i = 1;
            psmt.setInt(i++, pmt_dobj.getL_unld_wt());
            psmt.setInt(i++, pmt_dobj.getU_unld_wt());
            psmt.setInt(i++, pmt_dobj.getL_ld_wt());
            psmt.setInt(i++, pmt_dobj.getU_ld_wt());
            psmt.setString(i++, pmt_dobj.getPer_period());
            psmt.setInt(i++, pmt_dobj.getFee());
            psmt.setInt(i++, pmt_dobj.getFine());
            psmt.setString(i++, pmt_dobj.getState_code());
            psmt.setInt(i++, pmt_dobj.getPurpose_code());
            psmt.setInt(i++, pmt_dobj.getPermit_type());
            psmt.setInt(i++, pmt_dobj.getPermit_catg());
            psmt.setInt(i++, pmt_dobj.getL_vh_class());
            psmt.setInt(i++, pmt_dobj.getU_vh_class());
            psmt.setInt(i++, pmt_dobj.getL_seat_cap());
            psmt.setInt(i++, pmt_dobj.getU_seat_cap());
            psmt.setString(i++, pmt_dobj.getPer_region());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public boolean permitFeeStateConfig(VmPermitFeeStateConfig pmt_fee_dobj, String data) {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        String sql = "";
        try {
            tmgr = new TransactionManager("permitFeeStateConfig");
            if (data.equals("insertData")) {
                sql = "INSERT INTO " + TableList.VM_PERMIT_FEE_STATE_CONFIGURATION + "(\n"
                        + "pmt_type_flag,pmt_catg_flag,vh_class_flag,seat_cap_flag,unld_wt_flag,ld_wt_flag,per_region_flag,per_period_flag,state_cd, pur_cd,pmt_type)\n"
                        + "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
            }
            if (data.equals("updateData")) {
                sql = "UPDATE " + TableList.VM_PERMIT_FEE_STATE_CONFIGURATION + "\n"
                        + " SET pmt_type_flag=?,pmt_catg_flag=?,vh_class_flag=?,seat_cap_flag=?,unld_wt_flag=?,ld_wt_flag=?,per_region_flag=?,per_period_flag=?\n"
                        + " WHERE state_cd=? and pur_cd=? and pmt_type=? ";

            }
            psmt = tmgr.prepareStatement(sql);
            int i = 1;
            psmt.setBoolean(i++, pmt_fee_dobj.isPmt_type_flag());
            psmt.setBoolean(i++, pmt_fee_dobj.isPmt_catg_flag());
            psmt.setBoolean(i++, pmt_fee_dobj.isVh_class_flag());
            psmt.setBoolean(i++, pmt_fee_dobj.isSeat_cap_flag());
            psmt.setBoolean(i++, pmt_fee_dobj.isUnld_wt_flag());
            psmt.setBoolean(i++, pmt_fee_dobj.isLd_wt_flag());
            psmt.setBoolean(i++, pmt_fee_dobj.isPer_region_flag());
            psmt.setBoolean(i++, pmt_fee_dobj.isPer_period_flag());
            psmt.setString(i++, pmt_fee_dobj.getState_cd());
            psmt.setInt(i++, pmt_fee_dobj.getPur_cd());
            psmt.setInt(i++, pmt_fee_dobj.getPmt_type());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public boolean permitStateConfig(VmPermitStateConfig pmt_state_dobj, String data) {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        String sql = "";
        try {
            tmgr = new TransactionManager("permitStateConfig");
            if (data.equals("insertData")) {
                sql = "INSERT INTO " + TableList.VM_PERMIT_STATE_CONFIGURATION + "(\n"
                        + "temp_days_valid_upto,temp_weeks_valid_upto,spec_days_valid_upto,spec_weeks_valid_upto,sc_renew_before_days,cc_renew_before_days,ai_renew_before_days,psv_renew_before_days,gp_renew_before_days,np_renew_before_days"
                        + " ,sc_rule_heading, sc_formno_heading,cc_rule_heading,cc_formno_heading,aitp_rule_heading,aitp_formno_heading,psvp_rule_heading,psvp_formno_heading,gp_rule_heading,gp_formno_heading"
                        + " ,np_rule_heading,np_formno_heading,temp_rule_heading,temp_formno_heading,spec_rule_heading,spec_formno_heading,auth_aitp_rule_heading,auth_aitp_formno_heading,auth_np_rule_heading,auth_np_formno_heading,note_in_footer"
                        + " ,temp_days_valid_from,temp_weeks_valid_from,spec_days_valid_from,spec_weeks_valid_from,sc_renew_after_days,cc_renew_after_days,ai_renew_after_days,psv_renew_after_days,gp_renew_after_days,np_renew_after_days,renewal_of_permit_valid_from_flag,temp_route_area,genrate_ol_appl,permanent_permit_valid"
                        + " ,allowed_surr_pur_cd,temp_pmt_type,renew_temp_pmt,spl_pmt_route,state_cd )\n"
                        + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            }
            if (data.equals("updateData")) {
                sql = "UPDATE " + TableList.VM_PERMIT_STATE_CONFIGURATION + "\n"
                        + " SET temp_days_valid_upto=?,temp_weeks_valid_upto=?, spec_days_valid_upto=?,spec_weeks_valid_upto=?,sc_renew_before_days=?,cc_renew_before_days=?,ai_renew_before_days=?,psv_renew_before_days=?,gp_renew_before_days=?,"
                        + " np_renew_before_days=?, sc_rule_heading=?,sc_formno_heading=?,cc_rule_heading=?,cc_formno_heading=?,aitp_rule_heading=?,aitp_formno_heading=?,psvp_rule_heading=?,psvp_formno_heading=?,gp_rule_heading=?,gp_formno_heading=?,"
                        + " np_rule_heading=?,np_formno_heading=?,temp_rule_heading=?,temp_formno_heading=?,spec_rule_heading=?,spec_formno_heading=?,auth_aitp_rule_heading=?,auth_aitp_formno_heading=?,auth_np_rule_heading=?,auth_np_formno_heading=?,"
                        + " note_in_footer=?,temp_days_valid_from=?, temp_weeks_valid_from=?,spec_days_valid_from=?,spec_weeks_valid_from=?,sc_renew_after_days=?,cc_renew_after_days=?,ai_renew_after_days=?,psv_renew_after_days=?,gp_renew_after_days=?,"
                        + " np_renew_after_days=?,renewal_of_permit_valid_from_flag=?,temp_route_area=?,genrate_ol_appl=?,permanent_permit_valid=?,"
                        + " allowed_surr_pur_cd=?,temp_pmt_type=?,renew_temp_pmt=?,spl_pmt_route=? \n"
                        + " WHERE state_cd=?";
            }
            psmt = tmgr.prepareStatement(sql);
            int i = 1;
            psmt.setInt(i++, pmt_state_dobj.getTemp_days_valid_upto());
            psmt.setInt(i++, pmt_state_dobj.getTemp_weeks_valid_upto());
            psmt.setInt(i++, pmt_state_dobj.getSpec_days_valid_upto());
            psmt.setInt(i++, pmt_state_dobj.getSpec_weeks_valid_upto());
            psmt.setInt(i++, pmt_state_dobj.getSc_renew_before_days());
            psmt.setInt(i++, pmt_state_dobj.getCc_renew_before_days());
            psmt.setInt(i++, pmt_state_dobj.getAi_renew_before_days());
            psmt.setInt(i++, pmt_state_dobj.getPsv_renew_before_days());
            psmt.setInt(i++, pmt_state_dobj.getGp_renew_before_days());
            psmt.setInt(i++, pmt_state_dobj.getNp_renew_before_days());
            psmt.setString(i++, pmt_state_dobj.getSc_rule_heading());
            psmt.setString(i++, pmt_state_dobj.getSc_formno_heading());
            psmt.setString(i++, pmt_state_dobj.getCc_rule_heading());
            psmt.setString(i++, pmt_state_dobj.getCc_formno_heading());
            psmt.setString(i++, pmt_state_dobj.getAitp_rule_heading());
            psmt.setString(i++, pmt_state_dobj.getAitp_formno_heading());
            psmt.setString(i++, pmt_state_dobj.getPsvp_rule_heading());
            psmt.setString(i++, pmt_state_dobj.getPsvp_formno_heading());
            psmt.setString(i++, pmt_state_dobj.getGp_rule_heading());
            psmt.setString(i++, pmt_state_dobj.getGp_formno_heading());
            psmt.setString(i++, pmt_state_dobj.getNp_rule_heading());
            psmt.setString(i++, pmt_state_dobj.getNp_formno_heading());
            psmt.setString(i++, pmt_state_dobj.getTemp_rule_heading());
            psmt.setString(i++, pmt_state_dobj.getTemp_formno_heading());
            psmt.setString(i++, pmt_state_dobj.getSpec_rule_heading());
            psmt.setString(i++, pmt_state_dobj.getSpec_formno_heading());
            psmt.setString(i++, pmt_state_dobj.getAuth_aitp_rule_heading());
            psmt.setString(i++, pmt_state_dobj.getAuth_aitp_formno_heading());
            psmt.setString(i++, pmt_state_dobj.getAuth_np_rule_heading());
            psmt.setString(i++, pmt_state_dobj.getAuth_np_formno_heading());
            psmt.setString(i++, pmt_state_dobj.getNote_in_footer());
            psmt.setInt(i++, pmt_state_dobj.getTemp_days_valid_from());
            psmt.setInt(i++, pmt_state_dobj.getTemp_weeks_valid_from());
            psmt.setInt(i++, pmt_state_dobj.getSpec_days_valid_from());
            psmt.setInt(i++, pmt_state_dobj.getSpec_weeks_valid_from());
            psmt.setInt(i++, pmt_state_dobj.getSc_renew_after_days());
            psmt.setInt(i++, pmt_state_dobj.getCc_renew_after_days());
            psmt.setInt(i++, pmt_state_dobj.getAi_renew_after_days());
            psmt.setInt(i++, pmt_state_dobj.getPsv_renew_after_days());
            psmt.setInt(i++, pmt_state_dobj.getGp_renew_after_days());
            psmt.setInt(i++, pmt_state_dobj.getNp_renew_after_days());
            psmt.setString(i++, pmt_state_dobj.getRenewal_of_permit_valid_from_flag().toUpperCase());
            psmt.setBoolean(i++, pmt_state_dobj.isTemp_route_area());
            psmt.setBoolean(i++, pmt_state_dobj.isGenrate_ol_appl());
            psmt.setBoolean(i++, pmt_state_dobj.isPermanent_permit_valid());
            psmt.setString(i++, pmt_state_dobj.getAllowed_surr_pur_cd());
            psmt.setBoolean(i++, pmt_state_dobj.isTemp_pmt_type());
            psmt.setBoolean(i++, pmt_state_dobj.isRenew_temp_pmt());
            psmt.setBoolean(i++, pmt_state_dobj.isSpl_pmt_route());
            psmt.setString(i++, pmt_state_dobj.getState_cd());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public boolean permitDocMapRecord(VmPermitStateMap state_dobj, String data) {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        String sql = "";
        try {
            tmgr = new TransactionManager("permitDocMapRecord");
            if (data.equals("insertData")) {
                sql = "INSERT INTO " + TableList.VM_PERMIT_DOC_STATE_MAP + "(\n"
                        + "state_cd, pur_cd,pmt_type,doc_id)\n"
                        + "VALUES (?,?,?,?)";
                psmt = tmgr.prepareStatement(sql);
                int i = 1;
                psmt.setString(i++, state_dobj.getState_code());
                psmt.setInt(i++, state_dobj.getPurpose_code());
                psmt.setInt(i++, state_dobj.getPermit_code());
                psmt.setString(i++, state_dobj.getDocumentid());
                psmt.executeUpdate();
            }
            if (data.equals("updateData")) {
                sql = "UPDATE " + TableList.VM_PERMIT_DOC_STATE_MAP + "\n"
                        + "   SET state_cd=?,pur_cd=?,pmt_type=?,doc_id=?\n"
                        + " WHERE state_cd=? and pur_cd=? and pmt_type=?";
                psmt = tmgr.prepareStatement(sql);
                int i = 1;
                psmt.setString(i++, state_dobj.getState_code());
                psmt.setInt(i++, state_dobj.getPurpose_code());
                psmt.setInt(i++, state_dobj.getPermit_code());
                psmt.setString(i++, state_dobj.getDocumentid());
                psmt.setString(i++, state_dobj.getState_code());
                psmt.setInt(i++, state_dobj.getPurpose_code());
                psmt.setInt(i++, state_dobj.getPermit_code());
                psmt.executeUpdate();
            }
            flag = true;
            tmgr.commit();
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }

        return flag;
    }

    public boolean checkDuplicatePermitCategory(String code, String state_code) {
        boolean flag = false;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("checkDuplicatePermitCategory");
            String sql = " Select * from " + TableList.VM_PERMIT_CATG + " where code = ? and state_cd = ?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, Integer.parseInt(code));
            psmt.setString(2, state_code);
            rs = psmt.executeQuery();
            if (rs.next()) {
                flag = true;
            }
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public boolean checkDuplicatePermitRoute(String code, String state_code, int off_code) {
        boolean flag = false;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("checkDuplicatePermitRoute");
            String sql = " Select * from " + TableList.VM_ROUTE_MASTER + " where code = ? and state_cd = ? and off_cd= ? ";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, code);
            psmt.setString(2, state_code);
            psmt.setInt(3, off_code);
            rs = psmt.executeQuery();
            if (rs.next()) {
                flag = true;
            }

        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public boolean checkDuplicatePermitRoutes(VmPermitRouteDobj pmt_route_dobj) {
        boolean flag = false;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("checkDuplicatePermitRoutes");
            String sql = " Select * from " + TableList.VM_ROUTE_MASTER + " where code = ? and off_cd=? and state_cd = ? ";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, pmt_route_dobj.getRoute_code());
            psmt.setInt(2, pmt_route_dobj.getOff_code());
            psmt.setString(3, pmt_route_dobj.getState_code());
            rs = psmt.executeQuery();
            if (rs.next()) {
                flag = true;
            }
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public VmPermitRouteDobj getPermitRouteByCode(String code, String state_code, int off_code) {
        boolean flag = false;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        VmPermitRouteDobj pmt_route_dobj = new VmPermitRouteDobj();
        try {
            tmgr = new TransactionManager("checkDuplicatePermitRoute");
            String sql = " Select * from " + TableList.VM_ROUTE_MASTER + " where code = ? and state_cd = ? and off_cd= ? ";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, code);
            psmt.setString(2, state_code);
            psmt.setInt(3, off_code);
            rs = psmt.executeQuery();
            if (rs.next()) {
                pmt_route_dobj.setState_code(state_code);
                pmt_route_dobj.setOff_code(off_code);
                pmt_route_dobj.setRoute_code(code);
                pmt_route_dobj.setLength(rs.getInt("rlength"));
                pmt_route_dobj.setFrom_loc(rs.getString("floc"));
                pmt_route_dobj.setTo_loc(rs.getString("tloc"));
                pmt_route_dobj.setVia(rs.getString("via"));
                pmt_route_dobj.setRegion_cover(rs.getInt("regions_covered"));
                pmt_route_dobj.setNhOverlapping(rs.getString("overlapping"));
                pmt_route_dobj.setNhOverlappingLength(rs.getInt("overlapping_length"));
            }

        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return pmt_route_dobj;
    }

    public boolean checkDuplicatePermitRegion(int code, String state_cd, int off_cd) {
        boolean flag = false;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("checkDuplicatePermitRegion");
            String sql = " Select * from " + TableList.VM_REGION + " where region_cd = ? and state_cd = ? and off_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, code);
            psmt.setString(2, state_cd);
            psmt.setInt(3, off_cd);
            rs = psmt.executeQuery();
            if (rs.next()) {
                flag = true;
            }
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public boolean checkDupPermitFeeConfig(VmPermitFeeStateConfig config) {
        boolean flag = false;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("checkDupPermitFeeConfig");
            String sql = " Select * from " + TableList.VM_PERMIT_FEE_STATE_CONFIGURATION + " where pur_cd = ?  and state_cd = ? and pmt_type=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, config.getPur_cd());
            psmt.setString(2, config.getState_cd());
            psmt.setInt(3, config.getPmt_type());
            rs = psmt.executeQuery();
            if (rs.next()) {
                flag = true;
            }

        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public boolean checkDupPermitStateConfig(VmPermitStateConfig config) {
        boolean flag = false;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("checkDupPermitStateConfig");
            String sql = " Select * from " + TableList.VM_PERMIT_STATE_CONFIGURATION + " where state_cd = ?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, config.getState_cd());
            rs = psmt.executeQuery();
            if (rs.next()) {
                flag = true;
            }

        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public boolean checkDuplicatePermitFees(VmPermitFeeMasterDobj pmt_fee_dobj) {
        boolean flag = false;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("checkDuplicatePermitFees");
            String sql = " Select * from " + TableList.VM_PERMIT_FEE + " where state_cd=? and pur_cd = ? and pmt_type=? and pmt_catg =? and l_vh_class = ? and u_vh_class=? and l_seat_cap=? and u_seat_cap=? and per_region=?";
            psmt = tmgr.prepareStatement(sql);
            int i = 1;
            psmt.setString(i++, pmt_fee_dobj.getState_code());
            psmt.setInt(i++, pmt_fee_dobj.getPurpose_code());
            psmt.setInt(i++, pmt_fee_dobj.getPermit_type());
            psmt.setInt(i++, pmt_fee_dobj.getPermit_catg());
            psmt.setInt(i++, pmt_fee_dobj.getL_vh_class());
            psmt.setInt(i++, pmt_fee_dobj.getU_vh_class());
            psmt.setInt(i++, pmt_fee_dobj.getL_seat_cap());
            psmt.setInt(i++, pmt_fee_dobj.getU_seat_cap());
            psmt.setString(i++, pmt_fee_dobj.getPer_region());
            rs = psmt.executeQuery();
            if (rs.next()) {
                flag = true;
            }
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public boolean checkDuplicateDocStateMap(int pur_cd, int pmt_type) {
        boolean flag = false;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("checkDuplicateDocStateMap");
            String sql = " Select * from " + TableList.VM_PERMIT_DOC_STATE_MAP + " where pur_cd = ? and pmt_type=? and state_cd = ?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, pur_cd);
            psmt.setInt(2, pmt_type);
            psmt.setString(3, Util.getUserStateCode());
            rs = psmt.executeQuery();
            if (rs.next()) {
                flag = true;
            }
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public boolean deletePermitCatgRecord(VmPermitCatgDobj pmt_dobj) {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("deletePermitCatgRecord");
            String sql = "DELETE from " + TableList.VM_PERMIT_CATG + "\n"
                    + " WHERE state_cd=? and code=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, pmt_dobj.getState_code());
            psmt.setInt(2, Integer.parseInt(pmt_dobj.getCcode()));
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public boolean deletePmtFeeRecord(VmPermitFeeMasterDobj pmt_dobj) {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("deletePmtFeeRecord");
            String sql = "DELETE from " + TableList.VM_PERMIT_FEE + "\n"
                    + " WHERE state_cd=? and pur_cd=? and pmt_type=? and pmt_catg=? and l_vh_class=? and u_vh_class=? and l_seat_cap=? and u_seat_cap=? and per_region=?";
            psmt = tmgr.prepareStatement(sql);
            int i = 1;
            psmt.setString(i++, pmt_dobj.getState_code());
            psmt.setInt(i++, pmt_dobj.getPurpose_code());
            psmt.setInt(i++, pmt_dobj.getPermit_type());
            psmt.setInt(i++, pmt_dobj.getPermit_catg());
            psmt.setInt(i++, pmt_dobj.getL_vh_class());
            psmt.setInt(i++, pmt_dobj.getU_vh_class());
            psmt.setInt(i++, pmt_dobj.getL_seat_cap());
            psmt.setInt(i++, pmt_dobj.getU_seat_cap());
            psmt.setString(i++, pmt_dobj.getPer_region());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public boolean deletePmtFeeStateConfig(VmPermitFeeStateConfig pmt_dobj) {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("deletePmtFeeStateConfig");
            String sql = "DELETE from " + TableList.VM_PERMIT_FEE_STATE_CONFIGURATION + "\n"
                    + " WHERE state_cd=? and pur_cd=? and pmt_type=? ";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, pmt_dobj.getState_cd());
            psmt.setInt(2, pmt_dobj.getPur_cd());
            psmt.setInt(3, pmt_dobj.getPmt_type());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public boolean deletePmtStateConfig(VmPermitStateConfig pmt_dobj) {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("deletePmtStateConfig");
            String sql = "DELETE from " + TableList.VM_PERMIT_STATE_CONFIGURATION + "\n"
                    + " WHERE state_cd=? ";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, pmt_dobj.getState_cd());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public boolean deletePmtDocMapRecord(VmPermitStateMap pmt_dobj) {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("deletePmtDocMapRecord");
            String sql = "DELETE from " + TableList.VM_PERMIT_DOC_STATE_MAP + "\n"
                    + " WHERE state_cd=? and pur_cd=? and pmt_type=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, pmt_dobj.getState_code());
            psmt.setInt(2, pmt_dobj.getPurpose_code());
            psmt.setInt(3, pmt_dobj.getPermit_code());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public boolean deletePmtRouteRecord(VmPermitRouteDobj pmt_route_dobj) {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("deletePmtRouteRecord");
            String sql = "DELETE from " + TableList.VM_ROUTE_MASTER + "\n"
                    + " WHERE state_cd = ? and off_cd = ? and code=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, pmt_route_dobj.getState_code());
            psmt.setInt(2, pmt_route_dobj.getOff_code());
            psmt.setString(3, pmt_route_dobj.getRoute_code());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public boolean deletePmtRegionRecord(VmPermitRegionDobj pmt_reg_dobj) {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("deletePmtRegionRecord");
            String sql = "DELETE from " + TableList.VM_REGION + " WHERE state_cd=? and off_cd=? and region_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, pmt_reg_dobj.getState_code());
            psmt.setInt(2, pmt_reg_dobj.getOff_code());
            psmt.setInt(3, pmt_reg_dobj.getCode());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return flag;
    }
}