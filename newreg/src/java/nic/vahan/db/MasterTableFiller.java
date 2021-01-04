/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db;

import dao.UserDAO;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.ApplicationStatusDobj;

/**
 *
 * @author tranC095
 */
public class MasterTableFiller {
    // Load the master tables ONLY ONCE per SERVER SESSION

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(MasterTableFiller.class);
    public static MasterTables masterTables = loadMasterTables();
    public static Map<String, State> state = getStates();
    public static Map<String, VehicleClass> vehicleClass = getVehicleClasses();
    public static List stateList = getStatesData();
    public static List vehicleClassList = getVehicleClassesList();
    public static Map<String, String> nonRtoRecords = UserDAO.getNonRtoRecords("", true, false);
    public static Map<String, String> runningRtoRecords = UserDAO.getRunningRtoRecords("", true, false);
    public static Map<String, String> totalRtoRecords = UserDAO.getTotalRtoRecords("", true, false);
    public static Map<String, ApplicationStatusDobj> applStatus = getApplStatusDobj();

    /**
     *
     * @return State Map which contains all the lists of master tables which are
     * state wise.
     */
    private synchronized static Map<String, State> getStates() {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        Map<String, State> stateData = new HashMap<>();
        Map<String, VehicleClass> vehicleData = new HashMap<>();
        String stateSql = "Select * from " + TableList.TM_STATE + " order by descr";
        try {
            tmgr = new TransactionManager("getStates");
            ps = tmgr.prepareStatement(stateSql);
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                State obj = new State();
                obj.setStateCode(rs.getString("state_code"));
                obj.setStateDescr(rs.getString("descr"));
                obj.setEgovCode(rs.getInt("egov_code"));
                obj.setDisrict(getMasterData(rs.getString("state_code"), tmgr, TableList.TM_DISTRICT, "dist_cd", "descr"));
                obj.setOffice(getMasterData(rs.getString("state_code"), tmgr, TableList.TM_OFFICE, "off_cd", "off_name"));
                obj.setOtherCriteria(getMasterData(rs.getString("state_code"), tmgr, TableList.VM_OTHER_CRITERIA, "criteria_cd", "criteria_desc"));
                obj.setCourt(getMasterData(rs.getString("state_code"), tmgr, TableList.VM_COURT, "court_cd", "court_name"));
                obj.setMagistrate(getMasterDataString(rs.getString("state_code"), tmgr, TableList.VM_COURT, "magistrate_cd", "magistrate_name"));
                obj.setOffence(getMasterData(rs.getString("state_code"), tmgr, TableList.VM_OFFENCES, "offence_cd", "offence_desc"));
                obj.setSection(getMasterData(rs.getString("state_code"), tmgr, TableList.VM_SECTION, "section_cd", "section_name"));
                obj.setDa(getMasterData(rs.getString("state_code"), tmgr, TableList.VM_DA, "code", "descr"));
                stateData.put(rs.getString("state_code"), obj);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }

            }
        }
        return stateData;
    }

    private static List getStatesData() {
        List statesList = new ArrayList();
        Set data = state.entrySet();
        Iterator iter = data.iterator();
        while (iter.hasNext()) {
            Map.Entry d = (Map.Entry) iter.next();
            statesList.add(new SelectItem(d.getKey(), ((State) d.getValue()).getStateDescr()));
        }
        return statesList;
    }

    public static String getStateDescrByStateCode(String stateCd) {
        String stateDescr = null;
        Set data = state.entrySet();
        Iterator iter = data.iterator();
        while (iter.hasNext()) {
            Map.Entry d = (Map.Entry) iter.next();
            if (d.getKey().toString().equalsIgnoreCase(stateCd)) {
                stateDescr = ((State) d.getValue()).getStateDescr();
                break;
            }
        }
        return stateDescr;
    }

    /**
     *
     * @param stateCode
     * @return District List usable for SelectOneMenu
     */
//    public static List getDistrictList(String stateCode) {
//        if (state.get(stateCode) == null) {
//            return new ArrayList();
//        }
//        return (ArrayList) ((ArrayList) ((State) state.get(stateCode)).getDisrict()).clone();
//    }
    public static List getDistrictList(String stateCode) {
        if (state.get(stateCode) == null) {
            return new ArrayList();
        }
        State st = (State) state.get(stateCode);
        ArrayList list = new ArrayList(st.getDisrict());

        return list;
    }

    /**
     *
     * @param stateCode
     * @return Office List usable for SelectOneMenu
     */
    public static List getOfficeList(String stateCode) {
        if (state.get(stateCode) == null) {
            return new ArrayList();
        }
        State st = (State) state.get(stateCode);
        ArrayList list = new ArrayList(st.getOffice());

        return list;
    }

    /**
     *
     * @return State List usable for SelectOneMenu
     */
    public static List getStateList() {
        return (ArrayList) ((ArrayList) stateList).clone();
    }

    /**
     *
     * @param stateCode
     * @return Court List usable for SelectOneMenu
     */
    public static List getCourtList(String stateCode) {
        return (ArrayList) ((ArrayList) ((State) state.get(stateCode)).getCourt()).clone();
    }

    /**
     *
     * @param stateCode
     * @return Magistrate List usable for SelectOneMenu
     */
    public static List getMagistrateList(String stateCode) {
        return (ArrayList) ((ArrayList) ((State) state.get(stateCode)).getMagistrate()).clone();
    }

    /**
     *
     * @param stateCode
     * @return offenses List usable for SelectOneMenu
     */
    public static List getOffenceList(String stateCode) {
        return (ArrayList) ((ArrayList) ((State) state.get(stateCode)).getOffence()).clone();
    }

    /**
     *
     * @param stateCode
     * @return Section List usable for SelectOneMenu
     */
    public static List getSectionList(String stateCode) {
        return (ArrayList) ((ArrayList) ((State) state.get(stateCode)).getSection()).clone();
    }

    /**
     *
     * @param stateCode
     * @return DA List usable for SelectOneMenu
     */
    public static List getDaList(String stateCode) {
        return (ArrayList) ((ArrayList) ((State) state.get(stateCode)).getDa()).clone();
    }

    /**
     *
     * @param stateCode
     * @return Other Criteria List usable for SelectOneMenu
     */
    public static List getOtherCriteriaList(String stateCode) {
        return (ArrayList) ((ArrayList) ((State) state.get(stateCode)).getOtherCriteria()).clone();
    }

    /**
     *
     * @return All Type(T & NT) of Vehicle Class List
     */
    public static List getVehicleClassList() {
        return vehicleClassList;
    }

    /**
     *
     * @param classType
     * @return list of vehicle class according to class wise
     */
    public static List getVehicleClassTypeWise(int classType) {
        List dump = new ArrayList();
        if (classType == TableConstants.VM_VEHTYPE_TRANSPORT) {
            return (ArrayList) ((ArrayList) ((VehicleClass) vehicleClass.get(String.valueOf(classType))).getTransportVehilceClass()).clone();
        } else if (classType == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
            return (ArrayList) ((ArrayList) ((VehicleClass) vehicleClass.get(String.valueOf(classType))).getNonTransportVehicleClass()).clone();
        }
        return dump;
    }

    //Loads the Master Tables in the respective MasterTableDO
    private synchronized static MasterTables loadMasterTables() {

        // Create an empty MasterTables object
        MasterTables mtables = new MasterTables();

        // Where I Am
        String whereiam = "MasterTableFiller.loadMasterTables()";

        // Load the tables and convert to the java objects
        try {
            LOGGER.info("........... Creating MasterTables object ...");

            // Create the objects
            //Transaction Masters
            mtables.TM_BANK = makeMasterTableDO(TableList.TM_BANK, "bank_code", "descr", "BANK");
            mtables.TM_STATE = makeMasterTableDO(TableList.TM_STATE, "state_code", "descr", "STATE MASTER");
            mtables.TM_DISTRICT = makeMasterTableDO(TableList.TM_DISTRICT, "dist_cd", "descr", "DISTRICT");
            mtables.TM_DESIGNATION = makeMasterTableDO(TableList.TM_DESIGNATION, "desig_cd", "desig_name", "TM_DESIGNATION");
            mtables.TM_ROLE = makeMasterTableDO(TableList.TM_ROLE, "role_cd", "role_descr", "TM_ROLE");
            mtables.TM_USER_CATG = makeMasterTableDO(TableList.TM_USER_CATG, "code", "descr", "TM_USER_CATG");
            mtables.TM_PURPOSE_MAST = makeMasterTableDO(TableList.TM_PURPOSE_MAST, "pur_cd", "descr", "DOMAIN DESCRIPTION");
            mtables.TM_INSTRUMENTS = makeMasterTableDO(TableList.TM_INSTRUMENTS, "code", "descr", "INSTRUMENT TYPE");
            mtables.TM_OFFICE = makeMasterTableDO(TableList.TM_OFFICE, "off_cd", "off_name", "TM_OFFICE");
            mtables.TM_COUNTRY = makeMasterTableDO(TableList.TM_COUNTRY, "code", "descr", "TM_COUNTRY");
            //Vahan Masters
            mtables.VM_VH_CLASS = makeMasterTableDO(TableList.VM_VH_CLASS, "vh_class", "descr", "VEHICLE CLASS");
            mtables.VM_VCH_CATG = makeMasterTableDO(TableList.VM_VCH_CATG, "catg", "catg_desc", "VEHICLE CATEGORY");
            mtables.VM_VHCLASS_CATG_MAP = makeMasterTableDO(TableList.VM_VHCLASS_CATG_MAP, "vh_class", "vch_catg", "VEHICLE CLASS CATEGORY");
            mtables.VM_OWCODE = makeMasterTableDO(TableList.VM_OWCODE, "ow_code", "DESCR", "OWNER TYPE MASTER");
            mtables.VM_REGN_TYPE = makeMasterTableDO(TableList.VM_REGN_TYPE, "regn_typecode", "DESCR", "REGISTRATION");
            mtables.VM_FUEL = makeMasterTableDO(TableList.VM_FUEL, "code", "DESCR", "FUEL MASTER");
            mtables.VM_OWCATG = makeMasterTableDO(TableList.VM_OWCATG, "owcatg_code", "DESCR", "OWNER CATEGORY");
            mtables.VM_ICCODE = makeMasterTableDO(TableList.VM_ICCODE, "ic_code", "DESCR", "IC CODE MASTER");
            mtables.VM_INSTYP = makeMasterTableDO(TableList.VM_INSTYP, "instyp_code", "DESCR", "INSURANCE TYPE MASTER");
            mtables.VM_MAKER = makeMasterTableDO(TableList.VM_MAKER, "code", "descr", "MAKER MASTER");
            mtables.VM_MODELS = makeMasterTableDO(TableList.VM_MODELS, "unique_model_ref_no", "model_name", "MODEL MASTER");
            mtables.VM_TAX_SLAB_FIELDS = makeMasterTableDO(TableList.VM_TAX_SLAB_FIELDS, "code", "descr", "TAX SLAB FIELDS");
            mtables.VM_HP_TYPE = makeMasterTableDO(TableList.VM_HP_TYPE, "hp_type_cd", "hp_type_descr", "HYPOTHECATION TYPE");
            mtables.VM_NORMS = makeMasterTableDO(TableList.VM_NORMS, "code", "descr", "NORMS");
            mtables.VM_TAX_MODE = makeMasterTableDO(TableList.VM_TAX_MODE, "tax_mode", "descr", "TAX MODE");
            mtables.VM_OTHER_CRITERIA = makeMasterTableDO(TableList.VM_OTHER_CRITERIA, "criteria_cd", "criteria_desc", "OTHER CRITERIA");
            mtables.VM_PMT_CATEGORY = makeMasterTableDO(TableList.VM_PERMIT_CATG, "code", "descr", "VM_PERMIT_CATG");
            mtables.VM_PERMIT_TYPE = makeMasterTableDO(TableList.VM_PERMIT_TYPE, "code", "descr", "PERMIT TYPE");
            mtables.vm_service_type = makeMasterTableDO(TableList.VM_SERVICES_TYPE, "code", "descr", "PERMIT SERVICES TYPE");
            mtables.VM_HSRP_FLAG = makeMasterTableDO(TableList.VM_HSRP_FLAG, "code", "descr", "HSRP FLAG");
            mtables.VM_REASON = makeMasterTableDO(TableList.VM_REASON, "code", "descr", "VM_REASON");
            mtables.VM_REGION = makeMasterTableDO(TableList.VM_REGION, "region_cd", "region", "REGION DESCRIPTION");
            mtables.VM_BLACKLIST = makeMasterTableDO(TableList.VM_BLACKLIST, "code", "descr", "COMPLAIN TYPE");
            mtables.VM_ACCUSED = makeMasterTableDO(TableList.VM_ACCUSED, "descr", "code", "ACCUSED");
            mtables.VM_DOCUMENTS = makeMasterTableDO(TableList.VM_DOCUMENTS, "code", "descr", "DOCUMENT DESCRIPTION");
            mtables.VM_PERMIT_DOCUMENTS = makeMasterTableDO(TableList.VM_PERMIT_DOCUMENTS, "doc_id", "descr", "PERMIT DOCUMENT");
            mtables.VM_SCRAP_REASONS = makeMasterTableDO(TableList.VM_SCRAP_REASON, "code", "descr", "SCRAP REASONS");
            mtables.VM_PERMIT_OBJECTION = makeMasterTableDO(TableList.VM_PERMIT_OBJECTION, "code", "descr", "PERMIT OBJECTION");
            mtables.VM_SPEEDGOV_MANUFACTURE = makeMasterTableDO(TableList.VM_SPEEDGOV_MANUFACTURE, "code", "descr", "SPEED GOVERNER");
            mtables.VM_TEMP_REGN_REASON = makeMasterTableDO(TableList.VM_TEMP_REGN_REASON, "code", "descr", "TEMPORARY REASON");
            mtables.VM_RCD_RETURN_REASON = makeMasterTableDO(TableList.VM_RCD_RETURN_REASON, "code", "descr", "RETURN REASON");
            mtables.VM_TAXDUE_FROM = makeMasterTableDO(TableList.VM_TAXDUE_FROM, "code", "descr", "CONV TAX DUE FROM");
            mtables.VM_RELATION = makeMasterTableDO(TableList.VM_RELATION, "code", "descr", "RELATION TYPE");
            mtables.TM_CONFIGURATION_LOGIN = makeMasterTableDO(TableList.TM_CONFIGURATION_LOGIN, "state_cd", "state_cd", "CONFIGURATION_LOGIN");
            //......................................................................
            // Once the master table DO are created, call this method
            // to fill in the masterTableDOs.
            //......................................................................
            mtables.fillMasterTableVector();

            LOGGER.info("...DONE!... Creating MasterTables object ...");
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

        return mtables;
    }

    /**
     * Make DO for a given master table.
     *
     * @param tableName The master table name
     *
     * @return The MasterTableDO object
     *
     * @throws SQLException
     * @throws VahanException
     */
    public static MasterTableDO makeMasterTableDO(String tableName, String codeName, String descName, String tableLable)
            throws SQLException, Exception {

        String[][] metadata = null;
        String[][] data = null;
        Map dataMap = new LinkedHashMap();
        TransactionManager tmg = null;

        String query = "SELECT  * FROM " + tableName + " ORDER BY " + descName;
        LOGGER.info("[Master Query] " + query);
        String whereiam = "MasterTableFiller.makeTableDO()";
        try {
            tmg = new TransactionManager("rowCount");
            String sqlRowCount = "SELECT COUNT(*) FROM " + tableName;
            PreparedStatement ps = tmg.prepareStatement(sqlRowCount);
            javax.sql.RowSet rsC = tmg.fetchDetachedRowSet();
            int rowsCount = 0;
            if (rsC.next()) {
                rowsCount = rsC.getInt(1);
            }

            int nCols = 0;

            //................................................................
            // Get the whole table and store the data into this object.
            // NOTE:Though the master table can contain columns more than 2,
            // but we will store only the first two columns
            //................................................................
            tmg = new TransactionManager(whereiam);
            ps = tmg.prepareStatement(query);
            rsC = tmg.fetchDetachedRowSet();

            // Get the number of columns
            java.sql.ResultSetMetaData rsmd = rsC.getMetaData();
            nCols = rsmd.getColumnCount();

            // Get the metadata
            metadata = new String[4][nCols];
            for (int i = 1; i <= nCols; i++) {
                // Get the designated column's name
                metadata[0][i - 1] = rsmd.getColumnName(i);
                // Retrieves the designated column's database-specific type name
                metadata[1][i - 1] = rsmd.getColumnTypeName(i);
                // Retrieves the designated column's SQL type
                metadata[2][i - 1] = "" + rsmd.getColumnType(i);
                // Retrieves the designated column's normal maximum width in characters
                metadata[3][i - 1] = "" + rsmd.getColumnDisplaySize(i);
            }

            // Get the table data
            if (rowsCount > 0) {

                data = new String[rowsCount][nCols];
                int row = 0;
                while (rsC.next()) {
                    row++;
                    // Note: Column number starts with 1
                    for (int col = 1; col <= nCols; col++) {
                        data[row - 1][col - 1] = rsC.getString(col);
                    }

                    dataMap.put(rsC.getString(codeName), rsC.getString(descName));
                }
            }

            if (nCols < 2) {
                throw new Exception("Master table \"" + tableName + "\" contains less than 2 columns!");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        String[][] data_AO = null;
        data_AO = data;
        return new MasterTableDO(tableName, metadata, data, data_AO, dataMap, codeName, descName, tableLable);
    }

    /**
     * Modify the given data by copying the first column and inserting it as
     * second column.
     *
     * @param tn - Tablename
     * @param md - Metadata
     * @param d - data (full)
     * @param d_AO - data in available order
     *
     * @return The modified data
     */
    private static Object[] modifyData(String tn,
            String[][] md,
            String[][] d,
            String[][] d_AO) {

        // Check input. Return null if any of the data given is null.
        if (md == null || d == null || d_AO == null) {
            return null;
        }

        // Increase column size by one
        String[][] m_md = new String[md.length][md[0].length + 1];
        String[][] m_d = new String[d.length][d[0].length + 1];
        String[][] m_d_AO = new String[d_AO.length][d_AO[0].length + 1];

        // Insert the column
        int ncol = 0;
        for (int i = 0; i < md.length; i++) {
            ncol = 0;
            for (int j = 0; j < md[0].length; j++) {
                m_md[i][ncol] = md[i][j];
                if (j == 0) {
                    ncol++;
                    m_md[i][ncol] = md[i][j];
                }
                ncol++;
            }
        }
        // Rename the name of the column just inserted
        //04-April-2014

        for (int i = 0; i < d.length; i++) {
            ncol = 0;
            for (int j = 0; j < d[0].length; j++) {
                m_d[i][ncol] = d[i][j];
                if (j == 0) {
                    ncol++;
                    m_d[i][ncol] = d[i][j];
                }
                ncol++;
            }
        }

        for (int i = 0; i < d_AO.length; i++) {
            ncol = 0;
            for (int j = 0; j < d_AO[0].length; j++) {
                m_d_AO[i][ncol] = d_AO[i][j];
                if (j == 0) {
                    ncol++;
                    m_d_AO[i][ncol] = d_AO[i][j];
                }
                ncol++;
            }
        }

        // Return modified data
        return new Object[]{m_md, m_d, m_d_AO};
    }

    public synchronized static void ReloadMasterTables() {
        masterTables = null;
        masterTables = loadMasterTables();
        state = null;
        state = getStates();
        vehicleClass = null;
        vehicleClass = getVehicleClasses();
        stateList.clear();
        stateList = getStatesData();
        vehicleClassList.clear();
        vehicleClassList = getVehicleClassesList();
    }

    /**
     * Standalone testing.
     */
    public static void main(String[] args) {
        MasterTables mtables = loadMasterTables();
        String[][] data = mtables.VM_VH_CLASS.getData();
        String[][] metData = mtables.VM_VH_CLASS.getMetadata();

    }

    /**
     *
     * @return Vehicle Class Map which contains all the lists of master tables
     * which are class type wise.
     */
    private synchronized static Map<String, VehicleClass> getVehicleClasses() {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        Map<String, VehicleClass> vehicleClassData = new HashMap<>();
        String stateSql = "Select distinct(class_type) from " + TableList.VM_VH_CLASS;
        try {
            tmgr = new TransactionManager("getVehicleClasses");
            ps = tmgr.prepareStatement(stateSql);
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                VehicleClass obj = new VehicleClass();
                if (rs.getInt("class_type") == TableConstants.VM_VEHTYPE_TRANSPORT) {
                    obj.setTransportVehilceClass(getMasterData(rs.getInt("class_type"), tmgr, TableList.VM_VH_CLASS, "vh_class", "descr"));
                }
                if (rs.getInt("class_type") == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
                    obj.setNonTransportVehicleClass(getMasterData(rs.getInt("class_type"), tmgr, TableList.VM_VH_CLASS, "vh_class", "descr"));
                }
                vehicleClassData.put(rs.getString("class_type"), obj);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }

            }
        }
        return vehicleClassData;
    }

    /**
     *
     * @return list of vehicle class both (transport & non-transport)
     */
    private static List getVehicleClassesList() {
        List vehicleClassAll = new ArrayList();
        vehicleClassAll.addAll((ArrayList) ((ArrayList) ((VehicleClass) vehicleClass.get(String.valueOf(TableConstants.VM_VEHTYPE_TRANSPORT))).getTransportVehilceClass()).clone());
        vehicleClassAll.addAll((ArrayList) ((ArrayList) ((VehicleClass) vehicleClass.get(String.valueOf(TableConstants.VM_VEHTYPE_NON_TRANSPORT))).getNonTransportVehicleClass()).clone());
        return vehicleClassAll;
    }

    /**
     *
     * @param stateCode
     * @param tmgr Instance of TransactionManager
     * @param tableName
     * @param colName
     * @param descr
     * @return List of key, value pair used for selectOneMenu
     */
    private static List getMasterData(String stateCode, TransactionManager tmgr, String tableName, String colName, String descr) {
        List dataList = new ArrayList<>();
        String Sql = "Select * from " + tableName + " where state_cd = ? order by " + descr + "";
        PreparedStatement ps;
        RowSet rs;
        try {
            ps = tmgr.prepareStatement(Sql);
            ps.setString(1, stateCode);
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                dataList.add(new SelectItem(rs.getInt(colName), rs.getString(descr)));
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
        return dataList;
    }

    /**
     *
     * @param classType
     * @param tmgr
     * @param tableName
     * @param colName
     * @param descr
     * @return
     */
    private static List getMasterData(int classType, TransactionManager tmgr, String tableName, String colName, String descr) {
        List dataList = new ArrayList<>();
        String Sql = "Select * from " + tableName + " where class_type = ? order by " + descr + "";
        PreparedStatement ps;
        RowSet rs;
        try {
            ps = tmgr.prepareStatement(Sql);
            ps.setInt(1, classType);
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                dataList.add(new SelectItem(rs.getInt(colName), rs.getString(descr)));
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
        return dataList;
    }

    /**
     *
     * @param stateCode
     * @param tmgr
     * @param tableName
     * @param colName
     * @param descr
     * @return
     */
    private static List getMasterDataString(String stateCode, TransactionManager tmgr, String tableName, String colName, String descr) {
        List dataList = new ArrayList<>();
        String Sql = "Select * from " + tableName + " where state_cd = ? order by " + descr + "";
        PreparedStatement ps;
        RowSet rs;
        try {
            ps = tmgr.prepareStatement(Sql);
            ps.setString(1, stateCode);
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                dataList.add(new SelectItem(rs.getString(colName), rs.getString(colName) + "-" + rs.getString(descr)));
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());

        }
        return dataList;
    }

    public static Map<String, ApplicationStatusDobj> getApplStatusDobj() {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        Map<String, ApplicationStatusDobj> applicationStatusColur = new HashMap<>();
        try {
            tmgr = new TransactionManagerReadOnly("getApplStatusDobj");
            sql = "select * from " + TableList.VM_STATUS_MAST + "";
            ps = tmgr.prepareStatement(sql);
            RowSet rsStatus = tmgr.fetchDetachedRowSet();
            while (rsStatus.next()) {
                ApplicationStatusDobj asDbj = new ApplicationStatusDobj();
                asDbj.setSeatCd(rsStatus.getString("code"));
                asDbj.setDescr(rsStatus.getString("descr"));
                asDbj.setColourCode(rsStatus.getString("colour_code"));
                applicationStatusColur.put(rsStatus.getString("code"), asDbj);
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
        return applicationStatusColur;
    }

    public static boolean getCasFlag() {

        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        String stateSql = "Select * from " + TableList.TM_CONFIGURATION_CAS + " where code = ?";
        try {
            tmgr = new TransactionManager("getCASFlag");
            ps = tmgr.prepareStatement(stateSql);
            ps.setString(1, "CAS");
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                return rs.getBoolean("value");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }

            }
        }
        return false;
    }

    public static boolean getVahan5RedirectFlag(String state_cd, String officeCd) {

        String offCd = null;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        String stateSql = "Select off_cd from " + TableList.TM_CONFIGURATION_REDIRECT + " where state_cd = ? and new_regn_flag = ?";
        try {
            tmgr = new TransactionManager("getVahan5RedirectDetails");
            ps = tmgr.prepareStatement(stateSql);
            ps.setString(1, state_cd);
            ps.setBoolean(2, true);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                offCd = rs.getString("off_cd");
            }
            if (offCd != null) {
                StringTokenizer st = new StringTokenizer(offCd, ",");
                while (st.hasMoreTokens()) {
                    if (st.nextToken().equalsIgnoreCase(officeCd)) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            //throw new VahanException("Error in connecting to New Reg Application");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }

            }
        }

        return false;
    }
}
