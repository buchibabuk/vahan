/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db;

import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Converts a given master table database into Java Object (Read-Only).
 *
 * ASSUMPTION: The master table is assumed to be having the first two columns of
 * prime interest. The first column gives 'CODE' and the second gives a 'DESCR'
 * data. However the master table DO represents the entire table and stores the
 * data in the String format irrespective of the actual data type in the table.
 *
 * @author RCN
 */
public class MasterTableDO {

    private static Logger logger = Logger.getLogger(MasterTableDO.class);
    ////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ////////////////////////////////////////////////////////////////////////
    /**
     * Index at which the Code data lies in the table
     */
    public static final int IDX_CODE = 0;
    /**
     * Index at which the Description data lies in the table
     */
    public static final int IDX_DESCR = 1;
    /**
     * CODE column name
     */
    public String COL_NAME_CODE = "CODE";
    /**
     * DESCR column name
     */
    public String COL_NAME_DESCR = "DESCR";
    ////////////////////////////////////////////////////////////////////////
    // INSTANCE VARIABLES
    ////////////////////////////////////////////////////////////////////////
    /**
     * Data that to be used by the client in *available ordered* format
     */
    private String[][] data_AO;
    ////////From TableDO
    /**
     * DB table name
     */
    protected String tableName;
    protected String tableNameLabel;
    /**
     * MetaData to give column names and column types
     */
    protected String[][] metadata;
    /**
     * Data exactly as it is in the table
     */
    protected String[][] data;
    protected Map dataMap;

    ////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR(S)
    ////////////////////////////////////////////////////////////////////////
    /**
     * Constructor.
     *
     * @param tableName Table name
     * @param metadata Gives the column name and column data type
     * @param data Data as it is in the master table
     * @param data_AO Data that is available and is ordered as per the user.
     * *Available Order* is done using the VSM_CHOICE_ORDER
     */
    public MasterTableDO(String tableName, String[][] metadata,
            String[][] data, String[][] data_AO) {
        this.tableName = tableName;
        this.metadata = metadata;
        this.data = data;
        this.data_AO = data_AO;
    }

    public MasterTableDO(String tableName, String[][] metadata,
            String[][] data, String[][] data_AO, Map datamap, String code, String descr) {

        this.tableName = tableName;
        this.metadata = metadata;
        this.data = data;
        this.dataMap = datamap;
        this.data_AO = data_AO;
        this.COL_NAME_CODE = code;
        this.COL_NAME_DESCR = descr;
    }

    public MasterTableDO(String tableName, String[][] metadata,
            String[][] data, String[][] data_AO, Map datamap, String code, String descr, String tablelabel) {

        this.tableName = tableName;
        this.metadata = metadata;
        this.data = data;
        this.dataMap = datamap;
        this.data_AO = data_AO;
        this.COL_NAME_CODE = code;
        this.COL_NAME_DESCR = descr;
        this.tableNameLabel = tablelabel;
    }

    ////////////////////////////////////////////////////////////////////////
    // METHOD(S)
    ////////////////////////////////////////////////////////////////////////
    /**
     * Gets the *Available Ordered* data.
     *
     * @return *Available Ordered* data.
     */
    public String[][] getData_AO() {
        return data_AO;
    }

    /**
     * Gets the *Disabled* data.
     *
     * @return *Disabled* data or null if no record is disabled.
     */
    public String[][] getData_Disabled() {
        // Check for empty table
        if (data == null) {
            return null;
        }

        // Get how many are disabled records
        int size = data.length - data_AO.length;
        int ncols = data[0].length;
        String[][] data_Disabled = new String[size][ncols];

        // Check the number of disabled records
        if (size <= 0) {
            return null;
        }

        // Make the list of the disabled data records
        int k = 0;
        boolean isFound = false;
        for (int i = 0; i < data.length; i++) {
            // Find if this record is there in the data_AO or not
            isFound = false;
            for (int j = 0; j < data_AO.length; j++) {
                if (data[i][0].equals(data_AO[j][0])) {
                    isFound = true;
                    break;
                }
            }

            // If the record is not there in the data_AO, add it to
            // the data_Disabled
            if (!isFound) {
                data_Disabled[k] = data[i];
                k++;
            }
        }

        // Return
        return data_Disabled;
    }

    /**
     * Gets the entire codes.
     *
     * @return Entire codes.
     *
     * @throws VahanException
     */
    public String[] getCodes() throws Exception {
        return getDataForColumn(IDX_CODE, data);
    }

    /**
     * Gets the entire descriptions.
     *
     * @return entire descriptions.
     *
     * @throws VahanException
     */
    public String[] getDescrs() throws Exception {
        return getDataForColumn(IDX_DESCR, data);
    }

    /**
     * Gets the *Available Ordered* codes.
     *
     * @return *Available Ordered* codes.
     *
     * @throws VahanException
     */
    public String[] getCodes_AO() throws Exception {
        return getDataForColumn(IDX_CODE, data_AO);
    }

    /**
     * Gets the *Available Ordered* descriptions.
     *
     * @return *Available Ordered* descriptions.
     *
     * @throws VahanException
     */
    public String[] getDescrs_AO() throws Exception {
        return getDataForColumn(IDX_DESCR, data_AO);
    }

    /**
     * Returns description for a given code.
     *
     * @param code Given code in String.
     *
     * @return Associated description.
     *
     * @throws VahanException
     */
    public String getDesc(String code) throws Exception {
        // If the code given is null, then return empty string.
        if (code == null) {
            return "ERROR : Null code passed";
        }

        // Get the description
        for (int i = 0; i < data.length; i++) {
            if (code.equals(data[i][0])) {
                return data[i][1];
            }
        }

        // As the control reaches here, something is wrong.
        // Throw an exception.
//        String msg = Debug.BUG +"DEV_ERROR : Ouch! Unknown code = " +code 
//                     +" passed to MasterTablesDO.getDesc()";
//        Debug.log(msg);
        String msg = "DEV_ERROR : Ouch! Unknown code = " + code
                + " passed to MasterTablesDO.getDesc()";

        logger.info(msg);
        throw new Exception(msg);
    }

    /**
     * Returns code for a given description.
     *
     * @param desc Given description in String.
     *
     * @return Associated code.
     *
     * @throws VahanException In case of unknown description
     */
    public String getCode(String desc) throws Exception {
        // If the desc given is null, then return empty string.
        if (desc == null) {
            return "ERROR : Null description passed";
        }

        // Get the description
        String code = null;
        for (int i = 0; i < data.length; i++) {
            if (desc.equals(data[i][1])) {
                if (code != null) {

                    throw new Exception("ERROR : Duplicate Description found : " + desc);
                }
                code = data[i][0];
                // Iterate entire loop...
            }
        }

        // Check if EXACTLY ONE Code is found. If yes return the code
        if (code != null) {
            return code;
        }

        // As the control reaches here, something is wrong.
        // Throw an exception.
//        String msg = Debug.BUG +"DEV_ERROR : Ouch! Unknown description = " +desc 
//                     +" passed to MasterTablesDO.getCode()";
//        Debug.log(msg);
        String msg = "DEV_ERROR : Ouch! Unknown description = " + desc
                + " passed to MasterTablesDO.getCode()";
        logger.info("" + msg);
        throw new Exception(msg);
    }

    /**
     * Method to return specific row.
     *
     * @return one row from the table.
     */
    public String[] getRow(String code) {
        for (int i = 0; i < data.length; i++) {
            if (code.equals(data[i][0])) {
                return data[i];
            }
        }

        return null;
    }

    /**
     * Get the form data in the form CODE:DESCR
     *
     * @return Form data in the form CODE:DESCR in an array
     */
    public String[][] getFormData() {
        // Return null if the data_AO is null
        if (data_AO == null) {
            return null;
        }

        // Return the first two columns interface CODE and DESCR
        String[][] fd = new String[data_AO.length][2];
        for (int i = 0; i < data_AO.length; i++) {
            fd[i][0] = data_AO[i][0];
            fd[i][1] = data_AO[i][1];
        }
        return fd;
    }

    //Functions From TableDO
    /**
     * Gets the name of the table.
     *
     * @return Table name.
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Gets the metadata.
     *
     * <PRE>
     *  String[0][0..n] = Designated column's name
     *  String[1][0..n] = Designated column's database-specific type name
     *  String[2][0..n] = Designated column's SQL type (See java.sql.Types)
     *  String[3][0..n] = Designated column's normal maximum display width in characters
     *
     *  String[2][0..n] contains int in the String form (java.sql.Types)
     * </PRE>
     *
     * @return Metadata array. First row (String[0][i]) contain ColumnNames,
     * Second row (String[1][i]) contain ColumnTypeNames and Third row
     * (String[2][i] contain ColumnTypes
     *
     * @see <A HREF="java.sql.Types">java.sql.Types</A>
     */
    public String[][] getMetadata() {
        return metadata;
    }

    /**
     * Gets the entire data.
     *
     * @return Entire data as it is in the Table
     */
    public String[][] getData() {
        return data;
    }

    public Map getMapData() {
        return dataMap;
    }

    /**
     * Method to return specific row.
     *
     * @return one row from the table.
     */
//    public String[] getRow(String code) {
//        for (int i = 0; i < data.length; i++) {
//            if (code.equals(data[i][0])) {
//                return data[i];
//            }
//        }
//        
//        return null;
//    }
    /**
     * Returns data for the given column index from the given data.
     *
     * @param colIdx Column index for which the data need to be returned.
     *
     * @return the data
     */
    protected static String[] getDataForColumn(int colIdx, String[][] tableData)
            throws Exception {

        // Check the index
        if (colIdx > tableData.length - 1 || colIdx < 0) {
            throw new Exception("Out of range Column Index : getDataForColumn()");
        }

        // Get the data
        String[] coldata = new String[tableData.length];
        for (int i = 0; i < coldata.length; i++) {
            coldata[i] = tableData[i][colIdx];
        }

        // Return the data
        return coldata;
    }

    /**
     * Get the number of columns in the table
     *
     * @return Number of column count in the table
     */
    public int getColumnCount() {
        if (metadata == null || metadata.length == 0) {
            return 0;
        }

        return metadata[0].length;
    }

    public String getTableNameLabel() {
        return tableNameLabel;
    }

    public void setTableNameLabel(String tableNameLabel) {
        this.tableNameLabel = tableNameLabel;
    }

    /**
     * Dump. Debug method to display the data.
     */
    public void dump() {

        // Dump data as per this object (MasterTableDO)
        logger.info("\n------------<<< " + tableName + " (Available Ordered Choice List) >>>------------");
        for (int i = 0; i < metadata.length; i++) {
            for (int j = 0; j < metadata[0].length; j++) {
                logger.info(metadata[i][j]);
                if (j != metadata[0].length - 1) {
                    logger.info(" : ");
                }
            }
        }
        for (int i = 0; data_AO != null && i < data_AO.length; i++) {
            for (int j = 0; j < data_AO[0].length; j++) {
                logger.info(data_AO[i][j]);
                if (j != data_AO[0].length - 1) {
                    logger.info(" : ");
                }
            }
        }
    }
}
