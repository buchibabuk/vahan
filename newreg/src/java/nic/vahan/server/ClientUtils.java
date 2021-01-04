/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.server;

import javax.faces.component.html.HtmlInputText;
import org.primefaces.component.inputtext.InputText;

/**
 *
 * @author sushil
 */
public class ClientUtils {
    
    /**
     * Maximum number of chars in vehicle number - series part
     */
    public static final int MAX_CHARS_VEHNO_SERIES_PART = 6;
    /**
     * Maximum number of chars in vehicle number - number part
     */
    public static final int MAX_CHARS_VEHNO_NUMBER_PART = 4;
    
    /**
     * Make the Vechicle number from the given two textfields.
     * Vehicle registration number consists of total 10 chars.
     * The first 6 (MAX_CHARS_VEHNO_SERIES_PART) chars are Series part and
     * rest 4 (MAX_CHARS_VEHNO_NUMBER_PART) chars are Number part.
     *            Format is :              "SSSSSSNNNN"
     * So a vehicle number could look like "DL2CV 7415"
     *                                     "DL3CP 0023"
     *                                     "DL3CPD0023"
     *
     * @param tfSeriesPart Vehicle Number - Series Part
     * @param tfNumberPart Vehicle Number - Number Part
     *
     * @return Made Vechicle number. In case of an error or wrong
     *         input it returns null.
     */
    public static String makeFormatAndSetVehicleNo(String tfSeriesPart, String tfNumberPart) {
        String madeVehNo = null;

        // Check inputs
        if (tfSeriesPart == null || tfNumberPart == null) {
            return null;
        }

        // Format and *set* the series and number part into the *textfields*
        setFormattedSeriesPart(tfSeriesPart);
        setFormattedNumberPart(tfNumberPart);

        // Make the vehicle number from 2 text boxes
        madeVehNo = (String) tfSeriesPart+ (String) tfNumberPart;
        madeVehNo = madeVehNo.trim();

        // Check if Vehicle number does not have required number of chars
        final int totalCharsInVehNo = MAX_CHARS_VEHNO_SERIES_PART + MAX_CHARS_VEHNO_NUMBER_PART;
        if (madeVehNo.length() != totalCharsInVehNo) {
            // Just show the message to the user and proceed
            String msg = "Vehicle number does not contain " + totalCharsInVehNo + " chars";           
        }

        return madeVehNo;
    }
    
     /**
     * Sets the formatted vehicle-series-part in the vehicle-series-part
     * textfield. A series part consists of MAX_CHARS_VEHNO_SERIES_PART chars.
     * If the text contains less than MAX_CHARS_VEHNO_SERIES_PART chars then
     * the text is appended with ' ' (SPACE) chars till the
     * MAX_CHARS_VEHNO_SERIES_PARTth position.
     *
     * @param tfSeriesPart Vehicle Number - Series Part
     */
    public static void setFormattedSeriesPart(HtmlInputText tfSeriesPart) {
        // Check input
        if (tfSeriesPart == null) {
            return;
        }

        // Format vehicle series part
        String seriesPart = ((String) tfSeriesPart.getValue()).trim();
        String formattedSeriesPart = formatSeriesPart(seriesPart);

        // Set the formatted series part in the vehicle-series-part textfield.
        tfSeriesPart.setValue(formattedSeriesPart);
    }
    
    /**
     * Sets the formatted vehicle-series-part in the vehicle-series-part
     * textfield. A series part consists of MAX_CHARS_VEHNO_SERIES_PART chars.
     * If the text contains less than MAX_CHARS_VEHNO_SERIES_PART chars then
     * the text is appended with ' ' (SPACE) chars till the
     * MAX_CHARS_VEHNO_SERIES_PARTth position.
     *
     * @param tfSeriesPart Vehicle Number - Series Part
     */
    public static void setFormattedSeriesPart(String tfSeriesPart) {
        // Check input
        if (tfSeriesPart == null) {
            return;
        }        
        // Format vehicle series part
        String seriesPart = ((String) tfSeriesPart).trim();        
    }
    /**
     * Method for right padding upto CommonUtils.MAX_CHARS_VEHNO_SERIES_PART
     * characters with SPACE. If series is sent null then it returns " ".
     *
     * @param str String to be formatted (e.g "NIDC")
     *
     * @return padded string (e.g "NIDC" to "NIDC ")
     */
    public static String formatSeriesPart(String str) {
        return rpad(str, ' ', MAX_CHARS_VEHNO_SERIES_PART);
    }

public static String rpad(String str, char paddingChar, int numOfChars) {
        StringBuffer paddedStr = new StringBuffer();
        if (str == null) {
            for (int i = 0; i < numOfChars; i++) {
                paddedStr.insert(0, paddingChar);
            }
        } else {
            int len = str.length();
            for (int i = 0; i < numOfChars - len; i++) {
                paddedStr.insert(0, paddingChar);
            }
            paddedStr.insert(0, str);
        }

        return paddedStr.toString();
    }

public static void setFormattedNumberPart(String tfNumberPart) {
        // Check input
        if (tfNumberPart == null) {
            return;
        }

        // Format vehicle number part
        String numberPart = ((String) tfNumberPart).trim();
        String formattedNumberPart = formatNumberPart(numberPart);

        // Set the formatted series part in the vehicle-number-part textfield.
        tfNumberPart=formattedNumberPart;
    }
public static String formatNumberPart(String str) {
        return lpad(str, '0', MAX_CHARS_VEHNO_NUMBER_PART);
    }

/**
     * Method for left padding upto given number of characters with given
     * padding char. If num is sent null then it returns eg "0000" if the
     * padding char is '0' and num Of Chars is 4.
     *
     * @param str String to be formatted (e.g "1", "abc")
     * @param paddingChar Char with which the padding needs to be done (e.g '0',
     * 'g', '-')
     * @param numOfChars Number of chars that the final string should have after
     * padding, if done.
     *
     * @return padded string (e.g "1" to "0001")
     */
    public static String lpad(String str, char paddingChar, int numOfChars) {
        StringBuffer paddedStr = new StringBuffer();
        if (str == null) {
            for (int i = 0; i < numOfChars; i++) {
                paddedStr.append(paddingChar);
            }
        } else {
            int len = str.length();
            for (int i = 0; i < numOfChars - len; i++) {
                paddedStr.append(paddingChar);
            }
            paddedStr.append(str);
        }

        return paddedStr.toString();
    }
    
     
    
    
    
    
    public static String makeFormatAndSetVehicleNoindb(String tfSeriesPart, String tfNumberPart) {
        // Return variable
        String madeVehNo = null;        
        // Check inputs
        if (tfSeriesPart == null) {
            return null;
        }

     
        if (tfNumberPart == null) {
            return "";
        }        
        // Format vehicle number part
        String numberPart = ((String) tfNumberPart).trim();
        String formattedNumberPart = formatNumberPartindb(numberPart);        

        // Set the formatted series part in the vehicle-number-part textfield.
        tfNumberPart = formattedNumberPart;                
        // Format vehicle series part
        String seriesPart = ((String) tfSeriesPart).trim();        
        String formattedSeriesPart = formatSeriesPartindb(seriesPart);        

        // Set the formatted series part in the vehicle-series-part textfield.
        tfSeriesPart = formattedSeriesPart;
        
        // Make the vehicle number from 2 text boxes
        madeVehNo = (String) tfSeriesPart + (String) tfNumberPart;
        madeVehNo = madeVehNo.trim();

        // Check if Vehicle number does not have required number of chars
        final int totalCharsInVehNo = MAX_CHARS_VEHNO_SERIES_PART + MAX_CHARS_VEHNO_NUMBER_PART;
        if (madeVehNo.length() != totalCharsInVehNo) {
            // Just show the message to the user and proceed
            String msg = "Vehicle number does not contain " + totalCharsInVehNo + " chars";            
            
        }
        madeVehNo=madeVehNo.toUpperCase();
        return madeVehNo;
    }

    
    
    public static String formatSeriesPartindb(String str) {
        return rpad(str, ' ', MAX_CHARS_VEHNO_SERIES_PART);
    }

    /**
     * Method for left padding upto CommonUtils.MAX_CHARS_VEHNO_NUMBER_PART
     * characters with zero. If num is sent null then it returns "0000".
     *
     * @param str String to be formatted (e.g "1")
     *
     * @return padded string (e.g "1" to "0001")
     */
    public static String formatNumberPartindb(String str) {
        return lpad(str, '0', MAX_CHARS_VEHNO_NUMBER_PART);
    }

    /**
     * Method for left padding upto given number of characters with 
     * given padding char. If num is sent null then it returns eg "0000"
     * if the padding char is '0' and num Of Chars is 4.
     *
     * @param str String to be formatted (e.g "1", "abc")
     * @param paddingChar Char with which the padding needs to be done (e.g '0', 'g', '-')
     * @param numOfChars Number of chars that the final string should have
     *        after padding, if done.
     *
     * @return padded string (e.g "1" to "0001")
     */
    public static String lpadindb(String str, char paddingChar, int numOfChars) {
        StringBuffer paddedStr = new StringBuffer();
        if (str == null) {
            for (int i = 0; i < numOfChars; i++) {
                paddedStr.append(paddingChar);
            }
        } else {
            int len = str.length();
            for (int i = 0; i < numOfChars - len; i++) {
                paddedStr.append(paddingChar);
            }
            paddedStr.append(str);
        }

        return paddedStr.toString();
    }

    /**
     * Method for right padding upto given number of characters with 
     * given padding char. If num is sent null then it returns eg "0000"
     * if the padding char is '0' and num Of Chars is 4.
     *
     * @param str String to be formatted (e.g "1", "abc")
     * @param paddingChar Char with which the padding needs to be done (e.g '0', 'g', '-')
     * @param numOfChars Number of chars that the final string should have
     *        after padding, if done.
     *
     * @return padded string (e.g "1" to "1000")
     */
    public static String rpadindb(String str, char paddingChar, int numOfChars) {
        StringBuffer paddedStr = new StringBuffer();
        if (str == null) {
            for (int i = 0; i < numOfChars; i++) {
                paddedStr.insert(0, paddingChar);
            }
        } else {
            int len = str.length();
            for (int i = 0; i < numOfChars - len; i++) {
                paddedStr.insert(0, paddingChar);
            }
            paddedStr.insert(0, str);
        }

        return paddedStr.toString();
    }

    
    
}
