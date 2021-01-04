/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.server;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.context.ExternalContext;
import javax.servlet.http.HttpServletResponse;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.dobj.smartcard.CsvFormatDO;

/**
 *
 * @author sushil
 */
public class CommonUtils {

    private static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(CommonUtils.class);
    HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
    private static Pattern pattern;
    private static Matcher matcher;
    private static final String PWD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";

    //added by Nitin : 16 jan 2014 for generating random throught the application 
    public static synchronized String getRanomNumber() {

        String randonNumber = "";
        SecureRandom ran = new SecureRandom();
        int num = ran.nextInt();
        num = num + 1000;
        if (num < 0) {
            num = num * (-1);
        }

        randonNumber = String.valueOf(num);
        return randonNumber;
    }

    /**
     * Checks whether the string is blank or 'null'.
     *
     * @return true if string is null or blank else false
     * @param strCheck string to be verified
     */
    public static boolean isNullOrBlank(String strCheck) {
        if ((strCheck == null) || ("null".equalsIgnoreCase(strCheck)) || (strCheck.trim().length() <= 0)) {
            return true;
        } else {
            return false;
        }
    }

    public static String isNullOrBlankReturnString(String strCheck) {
        if ((strCheck == null) || ("null".equalsIgnoreCase(strCheck)) || (strCheck.trim().length() <= 0)) {
            return "";
        } else {
            return strCheck;
        }
    }

    public static String formRegnNo(String regnNo) {
        int counter = 0;
        StringBuffer regnNumeric = new StringBuffer();

        regnNo = regnNo.trim().toUpperCase();
        regnNo = regnNo.replaceAll("[^0-9A-Z]", "");

        if ((regnNo.length() >= 4) && (regnNo.length() <= 10)) {
            for (int i = regnNo.length() - 1; i >= 0; i--) {
                char c = regnNo.charAt(i);

                if (!isNumeric(String.valueOf(c))) {
                    break;
                } else {
                    regnNumeric.append(c);
                    counter++;

                    if (counter >= 4) {
                        break;
                    }
                }
            }

            String newRegnNumeric = lpad(regnNumeric.reverse().toString(), '0', 4);

            regnNo = rpad(regnNo.substring(0, regnNo.length() - counter), ' ', 6);
            regnNo = regnNo + newRegnNumeric;
        }

        return regnNo;
    }

    public static boolean isNumeric(String numToCheck) {
        boolean retValue = false;

        if ((numToCheck != null) && (numToCheck.trim().length() > 0)) {
            try {
                Double.parseDouble(numToCheck);
                retValue = true;
            } catch (NumberFormatException ne) {
                // Do nothing in the catch as retValue is already false.
            }
        }

        return retValue;
    }

    public static String lpad(String str, char paddingChar, int numOfChars) {
        String strReturn = str;

        while (strReturn.length() < numOfChars) {
            strReturn = paddingChar + strReturn;
        }

        return strReturn;
    }

    public static String rpad(String str, char paddingChar, int numOfChars) {
        String strReturn = str;

        while (strReturn.length() < numOfChars) {
            strReturn = strReturn + paddingChar;
        }

        return strReturn;
    }

    public static String vahanEncryption(String strVal) {
        StringBuffer encVal = new StringBuffer();
        char ch[] = strVal.toCharArray();
        for (char c : ch) {
            encVal.append(Integer.toHexString((byte) c));
        }
        return encVal.toString();
    }

    public static String vahanDecryption(String strVal) {

        StringBuilder sb = new StringBuilder();
        try {
            for (int i = 0; i < strVal.length() - 1; i += 2) {
                String output = strVal.substring(i, (i + 2));
                int decimal = Integer.parseInt(output, 16);
                sb.append((char) decimal);
            }
        } catch (NumberFormatException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return sb.toString();

    }

    public static boolean validatePassword(String password) {
        pattern = Pattern.compile(PWD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static long daysBetween(Calendar startDate, Calendar endDate) {
        //assert: startDate must be before endDate  
        Calendar date = (Calendar) startDate.clone();
        long daysBetween = 0;
        while (date.before(endDate)) {
            date.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }

    public static boolean isColumnExist(RowSet rs, String colName) throws SQLException {
        boolean status = false;
        ResultSetMetaData rsm = rs.getMetaData();
        int colNum = rsm.getColumnCount();
        for (int i = 1; i <= colNum; i++) {
            if (rsm.getColumnName(i).equalsIgnoreCase(colName)) {
                status = true;
            }
        }

        return status;
    }

    public static void writeCSVFile(List<CsvFormatDO> csv, String fileName) throws VahanException {

        try {

            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();

            HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();

            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            StatefulBeanToCsv<CsvFormatDO> beanToCsv = new StatefulBeanToCsvBuilder<CsvFormatDO>(response.getWriter())
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator('|')
                    .build();
//.withApplyQuotesToAll(false)
            beanToCsv.write(csv);
            facesContext.responseComplete();

        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException
                | IOException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Downloading File");

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Downloading File");
        }
    }
}
