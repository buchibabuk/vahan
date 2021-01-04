/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.homologation.daoImpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.impl.Util;
import nic.vahan.homologation.dobj.Vm_model;


import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

/**
 *
 * @author nprpE070
 */
public class ModelDetailsDaoImpl {

    private static Logger logger = Logger.getLogger(ModelDetailsDaoImpl.class);

    public static String addAndRemoveModelDetailsController(List<Vm_model> transactionList, int makerCode, boolean save) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        Boolean flag = false;
        String m = "";
        try {
            tmgr = new TransactionManager("saveModelDetails");

            for (Vm_model dobj : transactionList) {

                if (save) {
                    m = saveEmpWithAssgnActionImpl(tmgr, dobj, makerCode);
                } else {
                    m = deleteActionEmpImpl(tmgr, dobj, makerCode);
                }
            }
            if (m.equalsIgnoreCase("success")) {
                tmgr.commit();
            }

        } catch (NumberFormatException | SQLException e) {
            logger.error("Exception Occured During Save pick List-" + e);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                logger.error("Exception Occured During Save pick List-" + e);
            }
        }
        return m;
    }

    private static String deleteActionEmpImpl(TransactionManager tmgr, Vm_model dobj, int makerCode) {
        PreparedStatement ps = null;
        String m = "";
        try {

            String sql = "DELETE FROM vm_model_state_map WHERE state_cd=? and maker_code=? and unique_model_ref_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, makerCode);
            ps.setString(3, dobj.getMaker_wmi_code());

            ps.executeUpdate();
            m = "success";

        } catch (SQLException e) {
            logger.error("Exception Occured During delete pick List-" + e);
            m = "failure";

        }
        return m;
    }

    private static String saveEmpWithAssgnActionImpl(TransactionManager tmgr, Vm_model dobj, int makerCode) {
        PreparedStatement ps = null;
        String m = "";
        try {

            String sql = "INSERT INTO vm_model_state_map("
                    + " state_cd,maker_code, unique_model_ref_no, model_name,op_dt,enterred_by,enterred_on)"
                    + " VALUES (?, ?, ?, ?,current_timestamp,?,current_timestamp)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, makerCode);
            ps.setString(3, dobj.getMaker_wmi_code());
            ps.setString(4, dobj.getModel_name());
            ps.setString(5, Util.getEmpCode());

            ps.executeUpdate();
            m = "success";

        } catch (SQLException e) {
            m = "failure";
            logger.error("Exception Occured During Save  List-" + e);
        }
        return m;
    }

    public static List<Vm_model> getModelCodewithDespForManuf(Vm_model dobj) {
        TransactionManager tmgr = null;

        List<Vm_model> modelList = new ArrayList<>();
        Vm_model modelDobj = null;
        List<String> actionSource = dobj.getActionSource();
        List<String> actionTarget = dobj.getActionTarget();
        DualListModel<String> modelNameList = null;
        PreparedStatement psmt = null;
        ArrayList list = new ArrayList();
        String whereiam = "MMServer.getModelCodewithDespForManuf()";
        // String sql = "select unique_model_ref_no,model_name from vm_model_homologation where maker_code=?";
        String sql = "select vm.* , vm_fuel.descr as fuel_descr,vm_vch_catg.catg_desc,vm_maker.descr as maker_descr  from vm_fuel,vm_vch_catg,vm_maker, vm_model_homologation vm FULL OUTER JOIN "
                + " vm_model_state_map vt on vm.unique_model_ref_no=vt.unique_model_ref_no   and vt.state_cd=? "
                + " where vm.maker_code=? and vt.unique_model_ref_no is null"
                + " and  vm.fuel=vm_fuel.code and vm.vch_catg=vm_vch_catg.catg  and vm.maker_code=vm_maker.code order by vm.model_name ";


        try {

            tmgr = new TransactionManager(whereiam);
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, dobj.getState_cd());
            psmt.setInt(2, dobj.getMaker_code());
            ResultSet rs = psmt.executeQuery();
            actionSource.clear();
            actionTarget.clear();
            while (rs.next()) {
                modelDobj = new Vm_model();
                modelDobj.setMaker_wmi_code(rs.getString("unique_model_ref_no"));
                modelDobj.setTac_no(rs.getString("tac_no"));
                modelDobj.setMaker_code(rs.getInt("maker_code"));

                if (rs.getString("vehicle_make_as").equalsIgnoreCase("C")) {
                    modelDobj.setVch_make_as("Drive Away Chassis");
                } else {
                    modelDobj.setVch_make_as("Fully Built");
                }

                modelDobj.setModel_name(rs.getString("model_name"));

                modelDobj.setVch_catg(rs.getString("catg_desc"));
                modelDobj.setSeat_cap(rs.getInt("seat_cap"));
                modelDobj.setCubic_cap(rs.getFloat("cubic_cap"));
                modelDobj.setEngine_power(rs.getFloat("engine_power"));
                modelDobj.setEngine_power_bifuel(rs.getFloat("engine_power_bifuel"));
                modelDobj.setUnld_weight(rs.getInt("unld_wt"));
                modelDobj.setGvw(rs.getInt("gvw"));
                modelDobj.setGcw(rs.getInt("gcw"));
                // dobj.setFuel(rs.getString("fuel"));
                modelDobj.setFuelDescr(rs.getString("fuel_descr"));
                modelDobj.setVch_class(rs.getString("vch_class"));


                modelDobj.setBody_type(rs.getString("body_type"));
                modelDobj.setNo_of_cyl(rs.getInt("no_cyl"));
                modelDobj.setWheelbase(rs.getInt("wheelbase"));
                modelDobj.setNorms(rs.getInt("norms"));

                modelDobj.setF_axle_descp(rs.getString("f_axle_descp"));
                modelDobj.setR_axle_descp(rs.getString("r_axle_descp"));
                modelDobj.setT_axle_descp(rs.getString("t_axle_descp"));
                modelDobj.setO1_axle_descp(rs.getString("o1_axle_descp"));
                modelDobj.setO2_axle_descp(rs.getString("o2_axle_descp"));
                modelDobj.setO3_axle_descp(rs.getString("o3_axle_descp"));
                modelDobj.setO4_axle_descp(rs.getString("o4_axle_descp"));
                modelDobj.setO5_axle_descp(rs.getString("o5_axle_descp"));
                if (rs.getString("f_axle_weight") == null) {
                } else {
                    modelDobj.setF_axle_weight(rs.getInt("f_axle_weight"));
                }
                if (rs.getString("r_axle_weight") == null) {
                } else {
                    modelDobj.setR_axle_weight(rs.getInt("r_axle_weight"));
                }
                if (rs.getString("t_axle_weight") == null) {
                } else {
                    modelDobj.setT_axle_weight(rs.getInt("t_axle_weight"));
                }
                if (rs.getString("o1_axle_weight") == null) {
                } else {
                    modelDobj.setO1_axle_weight(rs.getInt("o1_axle_weight"));
                }
                if (rs.getString("o2_axle_weight") == null) {
                } else {
                    modelDobj.setO2_axle_weight(rs.getInt("o2_axle_weight"));
                }
                if (rs.getString("o3_axle_weight") == null) {
                } else {
                    modelDobj.setO3_axle_weight(rs.getInt("o3_axle_weight"));
                }
                if (rs.getString("o4_axle_weight") == null) {
                } else {
                    modelDobj.setO4_axle_weight(rs.getInt("o4_axle_weight"));
                }
                if (rs.getString("o5_axle_weight") == null) {
                } else {
                    modelDobj.setO5_axle_weight(rs.getInt("o5_axle_weight"));
                }
                if (rs.getString("length") == null) {
                } else {
                    modelDobj.setLength(rs.getInt("length"));
                }
                if (rs.getString("width") == null) {
                } else {
                    modelDobj.setWidth(rs.getInt("width"));
                }
                if (rs.getString("height") == null) {
                } else {
                    modelDobj.setHeight(rs.getInt("height"));
                }
                if (rs.getString("f_overhang") == null) {
                } else {
                    modelDobj.setF_overhang(rs.getInt("f_overhang"));
                }

                if (rs.getString("r_overhang") == null) {
                } else {
                    dobj.setR_overhang(rs.getInt("r_overhang"));
                }

                //modelDobj.setBody_builder_acc_no(rs.getString("body_builder_acc_no"));
                modelDobj.setMaker_descr(rs.getString("maker_descr"));

                modelList.add(modelDobj);

            }
            //  fillActionTarget(actionSource, actionTarget, dobj);
            // modelNameList = new DualListModel(actionSource, actionTarget);
        } catch (SQLException sqle) {
            logger.error("Sql Exception Occured in fetching in getModelCodewithDespForManuf - ", sqle);
        } catch (Exception ex) {
            logger.error(" Exception Occured in fectching in getColorCodewithDesp- ", ex);
        } finally {
            try {
                tmgr.release();
            } catch (Exception ex) {
                logger.error("Exception Occued during Connection Release-", ex);
            }

        }
        return modelList;
    }

    public static List<Vm_model> fillActionTarget(Vm_model dobj) {
        List<Vm_model> selectedModelList = new ArrayList<>();
        Vm_model modelDobj = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;

        try {
            tmgr = new TransactionManager("implFillActionTargetList");
            // actionTarget.clear();

            String sql1 = "select vm.*,vt.state_cd, vm_fuel.descr as fuel_descr,vm_vch_catg.catg_desc,vm_maker.descr as maker_descr from vm_fuel,vm_vch_catg,vm_maker, vm_model_homologation vm,vm_model_state_map vt"
                    + "  where vm.maker_code=? and vt.state_cd=? "
                    + "  and  vm.fuel=vm_fuel.code and vm.vch_catg=vm_vch_catg.catg  and vm.maker_code=vm_maker.code and vm.unique_model_ref_no=vt.unique_model_ref_no order by vm.model_name";

            // String sql1 = "SELECT maker_code, state_cd, unique_model_ref_no, model_name  FROM vm_model_state_map where maker_code=? and  state_cd=?";
            ps = tmgr.prepareStatement(sql1);
            ps.setInt(1, dobj.getMaker_code());
            ps.setString(2, dobj.getState_cd());


            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                modelDobj = new Vm_model();
                modelDobj.setMaker_wmi_code(rs.getString("unique_model_ref_no"));
                modelDobj.setTac_no(rs.getString("tac_no"));
                modelDobj.setMaker_code(rs.getInt("maker_code"));

                if (rs.getString("vehicle_make_as").equalsIgnoreCase("C")) {
                    modelDobj.setVch_make_as("Drive Away Chassis");
                } else {
                    modelDobj.setVch_make_as("Fully Built");
                }

                modelDobj.setModel_name(rs.getString("model_name"));

                modelDobj.setVch_catg(rs.getString("catg_desc"));
                modelDobj.setSeat_cap(rs.getInt("seat_cap"));
                modelDobj.setCubic_cap(rs.getFloat("cubic_cap"));
                modelDobj.setEngine_power(rs.getFloat("engine_power"));
                modelDobj.setEngine_power_bifuel(rs.getFloat("engine_power_bifuel"));
                modelDobj.setUnld_weight(rs.getInt("unld_wt"));
                modelDobj.setGvw(rs.getInt("gvw"));
                modelDobj.setGcw(rs.getInt("gcw"));
                // dobj.setFuel(rs.getString("fuel"));
                modelDobj.setFuelDescr(rs.getString("fuel_descr"));
                modelDobj.setVch_class(rs.getString("vch_class"));


                modelDobj.setBody_type(rs.getString("body_type"));
                modelDobj.setNo_of_cyl(rs.getInt("no_cyl"));
                modelDobj.setWheelbase(rs.getInt("wheelbase"));
                modelDobj.setNorms(rs.getInt("norms"));

                modelDobj.setF_axle_descp(rs.getString("f_axle_descp"));
                modelDobj.setR_axle_descp(rs.getString("r_axle_descp"));
                modelDobj.setT_axle_descp(rs.getString("t_axle_descp"));
                modelDobj.setO1_axle_descp(rs.getString("o1_axle_descp"));
                modelDobj.setO2_axle_descp(rs.getString("o2_axle_descp"));
                modelDobj.setO3_axle_descp(rs.getString("o3_axle_descp"));
                modelDobj.setO4_axle_descp(rs.getString("o4_axle_descp"));
                modelDobj.setO5_axle_descp(rs.getString("o5_axle_descp"));
                if (rs.getString("f_axle_weight") == null) {
                } else {
                    modelDobj.setF_axle_weight(rs.getInt("f_axle_weight"));
                }
                if (rs.getString("r_axle_weight") == null) {
                } else {
                    modelDobj.setR_axle_weight(rs.getInt("r_axle_weight"));
                }
                if (rs.getString("t_axle_weight") == null) {
                } else {
                    modelDobj.setT_axle_weight(rs.getInt("t_axle_weight"));
                }
                if (rs.getString("o1_axle_weight") == null) {
                } else {
                    modelDobj.setO1_axle_weight(rs.getInt("o1_axle_weight"));
                }
                if (rs.getString("o2_axle_weight") == null) {
                } else {
                    modelDobj.setO2_axle_weight(rs.getInt("o2_axle_weight"));
                }
                if (rs.getString("o3_axle_weight") == null) {
                } else {
                    modelDobj.setO3_axle_weight(rs.getInt("o3_axle_weight"));
                }
                if (rs.getString("o4_axle_weight") == null) {
                } else {
                    modelDobj.setO4_axle_weight(rs.getInt("o4_axle_weight"));
                }
                if (rs.getString("o5_axle_weight") == null) {
                } else {
                    modelDobj.setO5_axle_weight(rs.getInt("o5_axle_weight"));
                }
                if (rs.getString("length") == null) {
                } else {
                    modelDobj.setLength(rs.getInt("length"));
                }
                if (rs.getString("width") == null) {
                } else {
                    modelDobj.setWidth(rs.getInt("width"));
                }
                if (rs.getString("height") == null) {
                } else {
                    modelDobj.setHeight(rs.getInt("height"));
                }
                if (rs.getString("f_overhang") == null) {
                } else {
                    modelDobj.setF_overhang(rs.getInt("f_overhang"));
                }

                if (rs.getString("r_overhang") == null) {
                } else {
                    dobj.setR_overhang(rs.getInt("r_overhang"));
                }

                //modelDobj.setBody_builder_acc_no(rs.getString("body_builder_acc_no"));
                modelDobj.setMaker_descr(rs.getString("maker_descr"));

                selectedModelList.add(modelDobj);

            }

        } catch (SQLException | NumberFormatException e) {
            logger.error("Exception Occued during fill tagetlist-", e);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                logger.error("Exception Occued during fill tagetlist in connection release-", e);
            }
        }

        //  actionSource.removeAll(actionTarget);
        //   Collections.sort(actionTarget);
        //   Collections.sort(actionSource);
        return selectedModelList;
    }

    public static ArrayList getMakerNameListForModels() throws Exception {
        ArrayList makerList = new ArrayList();
        TransactionManager tmgr = null;
        String whereiam = "ModelDetailsDaoImpl.getMakerNameListForModels()";
        PreparedStatement psmt = null;
        String strSQL = "select * from vm_maker_homologation";

        try {
            tmgr = new TransactionManager(whereiam);
            psmt = tmgr.prepareStatement(strSQL);

            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                makerList.add(new SelectItem(rs.getString("code"), rs.getString("descr")));
            }
            rs.close();
            rs = null;
            psmt.close();
            psmt = null;
        } catch (SQLException sqle) {
            logger.error("Sql Exception Occured during fetching maker details for model-- ", sqle);
        } finally {
            tmgr.release();
        }
        return makerList;
    }
}
