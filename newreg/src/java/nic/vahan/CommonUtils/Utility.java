/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.CommonUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nic.rto.vahan.common.VahanException;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author iftekhar
 */
public class Utility {

    private static final Logger LOGGER = Logger.getLogger(Utility.class);
    String string;
    String st1[] = {"", "one", "two", "three", "four", "five", "six", "seven",
        "eight", "nine",};
    String st2[] = {"hundred", "thousand", "lakh", "crore"};
    String st3[] = {"ten", "eleven", "twelve", "thirteen", "fourteen",
        "fifteen", "sixteen", "seventeen", "eighteen", "ninteen",};
    String st4[] = {"twenty", "thirty", "forty", "fifty", "sixty", "seventy",
        "eighty", "ninety"};
    static String receiptNo = null;

    public static String checkDataIsNULL(Object obj) {
        if (obj != null) {
            return obj.toString();

        } else {
            return "";
        }
    }

    public static java.sql.Timestamp convertStringToTimestamp(String date) {
        Timestamp fromTS1 = null;
        try {
            SimpleDateFormat datetimeFormatter1 = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy");
            Date lFromDate1 = datetimeFormatter1.parse(date);
            fromTS1 = new Timestamp(lFromDate1.getTime());

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return fromTS1;
    }

    public static String convertdateFormatString(String dateString) {
        String returnDateFormat = "";
        try {
            if (!CommonUtils.isNullOrBlank(dateString)) {
                SimpleDateFormat datetimeFormatRead = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                Date date = datetimeFormatRead.parse(dateString);
                SimpleDateFormat convertDateformat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                returnDateFormat = convertDateformat.format(date);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return returnDateFormat;
    }

    public static boolean isNullOrBlank(String content) {
        /// this will return false if conetnt is NULL or Blank, so it will easy when we use this.
        /// e.g do the action if field is Not Null and blank, in this case it will return true so that you can process your work.

        return content == null || content.equalsIgnoreCase("") ? true : false;
    }

    public static boolean isZeroOrNot(String content) {
        /// this will return false if conetnt is zero, so it will easy when we use this.
        /// e.g do the action if field is Not zero, in this case it will return true so that you can process your work.

        return content.equalsIgnoreCase("0") ? true : false;
    }

    public String ConvertNumberToWords(int number) {

        int n = 1;
        int word;
        string = "";
        while (number != 0) {
            switch (n) {
                case 1:
                    word = number % 100;
                    pass(word);
                    if (number > 100 && number % 100 != 0) {
                        show("and ");
                    }
                    number /= 100;
                    break;

                case 2:
                    word = number % 10;
                    if (word != 0) {
                        show(" ");
                        show(st2[0]);
                        show(" ");
                        pass(word);
                    }
                    number /= 10;
                    break;

                case 3:
                    word = number % 100;
                    if (word != 0) {
                        show(" ");
                        show(st2[1]);
                        show(" ");
                        pass(word);
                    }
                    number /= 100;
                    break;

                case 4:
                    word = number % 100;
                    if (word != 0) {
                        show(" ");
                        show(st2[2]);
                        show(" ");
                        pass(word);
                    }
                    number /= 100;
                    break;

                case 5:
                    word = number % 100;
                    if (word != 0) {
                        show(" ");
                        show(st2[3]);
                        show(" ");
                        pass(word);
                    }
                    number /= 100;
                    break;

            }
            n++;
        }
        if (string != null && !string.equals("")) {
            return string.toUpperCase() + " ONLY";
        } else {
            return "ZERO RUPEE ONLY";
        }
    }

    public void pass(int number) {
        int word, q;
        if (number < 10) {
            show(st1[number]);
        }
        if (number > 9 && number < 20) {
            show(st3[number - 10]);
        }
        if (number > 19) {
            word = number % 10;
            if (word == 0) {
                q = number / 10;
                show(st4[q - 2]);
            } else {
                q = number / 10;
                show(st1[word]);
                show(" ");
                show(st4[q - 2]);
            }
        }
    }

    public void show(String s) {
        String st;
        st = string;
        string = s;
        string += st;
    }

    /**
     * To validate illegal characters
     *
     * @author varun
     * @paramtext , regex
     * @return boolean
     */
    public static String validateTextForIllegalCharacters(String text, String regex) {
        String allowed = regex;
        if (text != null && !text.equalsIgnoreCase("")) {
            boolean flag = text.matches(allowed);
            if (!flag) {
                //htmlinputtext.setValue("");
                String notAllowed = text.replaceAll(allowed, "");
                return notAllowed;
            }
        }

        return null;

    }

    public static String checkAndReturnData(String value) {
        if (!isNullOrBlank(value)) {
            if (value.equalsIgnoreCase("null")) {
                return " ";
            } else {
                return value.trim();
            }
        } else {
            return " ";
        }
    }

    /**
     * Check if there are values given in the "series-part" and "number-part"
     * vehicle number text fields.
     *
     * @param tfSeriesPart Vehicle Number - Series Part
     * @param tfNumberPart Vehicle Number - Number Part
     *
     * @return false if any of the textfields are empty else true
     */
    public static boolean checkVehicleNoTextFields(String tfSeriesPart,
            String tfNumberPart) {
        //checks if series part and number part is null or empty
        boolean b1 = false;
        boolean b2 = false;
        Pattern p = null;
        Matcher m = null;
        p = Pattern.compile("[0-9A-Z ]{3,6}");
        m = p.matcher(tfSeriesPart.toString().toUpperCase());
        b1 = m.matches();

        p = Pattern.compile("[0-9]{1,4}");
        m = p.matcher(tfNumberPart.toString());
        b2 = m.matches();
        if (!b1 || !b2) {
            return false;
        }
        if (isNullOrBlank((String) tfSeriesPart) || isNullOrBlank((String) tfNumberPart)) {
            return false;
        }
        return true;
    }

    public static String showMsgWhenSeriesPartEmpty(String tfSeriesPart, String tfNumberPart) {
        String message = "";
        //checks if seies part and number part is null or blank
        if (isNullOrBlank(tfSeriesPart.trim()) && !isNullOrBlank(tfNumberPart.trim())) {

            // Show the error message
            message = "Empty 'Series Part' Message...!!!";
        }
        return message;
    }

    public static String getOnlyNumberPart(String tfString) throws VahanException {
        String numPart = tfString.substring((tfString.length() - 4), tfString.length());
        String[] part = numPart.split("(?<=\\D)(?=\\d)");
        if (part != null && part.length >= 1) {
            return part[part.length - 1];
        } else {
            throw new VahanException("Can't find Numeric Part");
        }
    }

    public static List<String> convertStringToList(String listToString) throws VahanException {
        if (isNullOrBlank(listToString)) {
            return null;
        }
        List<String> returnString = new ArrayList<>();
        try {
            if (listToString.contains(",")) {
                String[] temp = listToString.split(",");
                returnString.addAll(Arrays.asList(temp));
            } else {
                returnString.add(listToString);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in conversion to list");
        }
        return returnString;
    }

    public static String convertListToString(List<String> listToString) throws VahanException {
        String returnString = null;
        try {
            for (String arrayListString : listToString) {
                if (returnString == null) {
                    returnString = arrayListString;
                } else {
                    returnString += "," + arrayListString;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in conversion to string");
        }
        return returnString;
    }
}
