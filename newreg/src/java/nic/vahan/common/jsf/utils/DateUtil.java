/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.common.jsf.utils;

import nic.java.util.DateUtilsException;
import nic.java.util.Debug;
import nic.java.util.FormatUtils;

//////////////////////////////////////////////////////////////////////////////
// CLASS            : DateUtil
// PURPOSE          : Provides number of utility static methods and constants
//                    for Date
// NOTES            : None
// LAST MODIFIED    :
//  20031224 JIS023 Modified week day check
//  20031117 JIS022 Modified getDate1MinusDate2_Months() for calculating
//                  date difference in months
//  20030919 GUM019 Package re-structuring
//  20030807 JIS014 Fixed VB0087
//  20030716 AKS009 Auditing - TCC : Critical Errors
//  20030715 GUM009 Removed getServerDate() from here added to VahanServer.java
//                  as this is <<remote>>
//  20030715 AKS008 Auditing - Removed 'Superfluous Content'
//  20030702 JIS013 Added new methods
//  20030606 SIM001 Changed addToDate() variable date to tempDate
//  20030523 JIS012 Added new methods
//  20030602 AKS003 Added new methods (Contribution by JIS)
//  20030520 JIS010 Added parseDate(Date)
//  20030513 GUM002 Using dd-MM-yyyy instead dd/MM/yyyy
//  20030410 AKS001 Documentaion
//  20030310 RCN001 Created
//////////////////////////////////////////////////////////////////////////////
// Copyright 2003 National Informatics Centre, NIC. http://www.nic.in
// All Rights Reserved.
//////////////////////////////////////////////////////////////////////////////
//
// Importing standard java packages/classes
//

import java.sql.Timestamp;
import java.util.*;
import java.text.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import nic.rto.vahan.common.VahanException;

/**
 * Provides number of utility static methods and constants for Date.
 *
 * @author NIC
 */
abstract public class DateUtil {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(DateUtil.class);
    /**
     * Milliseconds in one day
     */
    public static final long MILLISECONDS_IN_ONE_DAY = 24 * 60 * 60 * 1000;
    /**
     * Default Date format
     */
    public static final String DATE_FORMAT = "dd-MM-yyyy"; // 'MM' means months, 'mm' means Minutes
    /**
     * DAY
     */
    public static final int DAY = 1;
    /**
     * MONTH
     */
    public static final int MONTH = 2;
    /**
     * YEAR
     */
    public static final int YEAR = 3;

    /**
     * Get Current local Date.
     *
     * @return local date object.
     */
    public static java.util.Date getCurrentLocalDate() {
        return new java.util.Date();
    }

    /**
     * Convert a date to 'DD/MM/YYYY' String format.
     *
     * @param date Given date.
     *
     * @return Date in DD/MM/YYYY format.
     */
    public static String getDateInDDMMYYYY(java.util.Date date) {
        // Check input
        if (date == null) {
            return "";
        }
        // Set the date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Get the month, day and year
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);

        // Get the 'DD/MM/YYYY' format
        String strDate = FormatUtils.getInNDigitFormat(2, day) + "-"
                + FormatUtils.getInNDigitFormat(2, month) + "-"
                + FormatUtils.getInNDigitFormat(4, year);

        // Return
        return strDate;
    }

    /**
     * Convert a date's time to 'HH24:MI:SS' String format.
     *
     * @param date Given date.
     *
     * @return Time in HH24:MI:SS format.
     */
    public static String convertDatePattern1(String dateToParse) throws ParseException {

        if (dateToParse != null && !dateToParse.equals("")) {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            String dateStringToParse = dateToParse;
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
            Date date = sdf1.parse(dateStringToParse);
            dateToParse = sdf2.format(date);
            return dateToParse;

        } else {
            System.out.println("convertDatePattern1 Date utils parameeters Please check the date");
        }
        return "";
    }

    public static String getTimeInHHMMSS(java.util.Date date) {
        // Set the date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Get the hours, minutes and seconds
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);

        // Get the 'HH24:MI:SS' format
        String strTime = FormatUtils.getInNDigitFormat(2, hours) + ":"
                + FormatUtils.getInNDigitFormat(2, minutes) + ":"
                + FormatUtils.getInNDigitFormat(2, seconds);

        // Return
        return strTime;
    }

    /**
     * Convert a date's time to 'HH12:MI AM/PM' String format.
     *
     * @param date Given date.
     *
     * @return Time in HH12:MI AM/PM format.
     */
    public static String getTimeInHHMM_AMPM(java.util.Date date) {
        // Set the date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Get the hours, minutes and seconds
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        String am_pm = "";
        if (hours < 12) {
            am_pm = "AM";
        } else if (hours == 12) {
            am_pm = "PM";
        } else {
            hours -= 12;
            am_pm = "PM";
        }

        // Get the 'HH12:MI AM/PM' format
        String strTime = FormatUtils.getInNDigitFormat(2, hours) + ":"
                + FormatUtils.getInNDigitFormat(2, minutes) + " "
                + am_pm;

        // Return
        return strTime;
    }

    /**
     * Convert a date to 'DD/MM/YYYY HH24:MI:SS' String format.
     *
     * TECH_NOTE : java.sql.Date Vs java.sql.Date Vs java.sql.Timestamp
     * --------------------------------------------------------
     * ResultSet.getDate() returns java.sql.Date providing DD-MON-YYYY value
     * only ResultSet.getTime() returns java.sql.Time providing HH:MM:SS value
     * only ResultSet.getTimestamp() returns java.sql.Timestamp providing
     * yyyy-mm-dd hh:mm:ss.fffffffff value
     *
     * @param date Given date.
     *
     * @return Date in DD/MM/YYYY HH24:MI:SS format.
     */
    public static String getDateInDDMMYYYY_HHMMSS(java.util.Date date) {
        // Check input
        if (date == null) {
            return "";
        }

        // Set the date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Get the 'DD/MM/YYYY HH24:MI:SS' format
        String strDate = getDateInDDMMYYYY(date)
                + " "
                + getTimeInHHMMSS(date);

        // Return
        return strDate;
    }

    /**
     * Return a Date for the DATE_FORMAT format string
     *
     * @param dateStr Date format string
     *
     * @return Java Date object if the input is valid, else null
     */
    public static Date parseDate(String dateStr) {
        // Check input
        if (dateStr == null) {
            return null;
        }

        // Create the formatter object
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

        // Do not parse the string if it do not adhere to the format given
        formatter.setLenient(false);

        // Parse
        ParsePosition pos = new ParsePosition(0);
        Date date = formatter.parse(dateStr, pos);

        // Return
        return date;
    }

    /**
     * Return a String for the given java.util.Date as per DATE_FORMAT
     *
     * @param date Date object
     *
     * @return String for the given java.util.Date as per DATE_FORMAT, null in
     * case of error
     */
    public static String parseDate(Date date) {
        // Check input
        if (date == null) {
            return null;
        }

        // Create the formatter object
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

        // Do not parse the string if it do not adhere to the format given
        formatter.setLenient(false);

        // Parse
        return formatter.format(date);
    }

    /**
     * Compares two given valid dates to return a number flag
     *
     * @param dateStr1 Date format string ('MM/DD/YYYY HH24:MM:SS' format)
     * @param dateStr2 Date format string ('MM/DD/YYYY HH24:MM:SS' format)
     *
     * @return Number flag as per following -1 Inputs are wrong (null) 0 Dates
     * are equal 1 First date is before the second one 2 First date is after the
     * second one
     */
    public static int compareDates(String dateStr1, String dateStr2) {
        // Parse the date
        Date date1 = parseDate(dateStr1);
        Date date2 = parseDate(dateStr2);

        // Return
        return compareDates(date1, date2);
    }

    /**
     * Compares two given valid dates to return a number flag
     *
     * @param date1 Date object
     * @param date2 Date object
     *
     * @return Number flag as per following -1 Inputs are wrong (null) 0 Dates
     * are equal 1 First date is before the second one 2 First date is after the
     * second one
     */
    public static int compareDates(Date date1, Date date2) {
        // Check input
        if (date1 == null || date2 == null) {
            return -1;
        }

        int dateDifference = 0;

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);

        // Retrieve days, months and years for both dates
        int day1 = calendar1.get(Calendar.DATE);
        int month1 = calendar1.get(Calendar.MONTH);
        int year1 = calendar1.get(Calendar.YEAR);

        int day2 = calendar2.get(Calendar.DATE);
        int month2 = calendar2.get(Calendar.MONTH);
        int year2 = calendar2.get(Calendar.YEAR);

        // Compare years
        if (year1 < year2) {
            dateDifference = 1;
        } else if (year1 > year2) {
            dateDifference = 2;
        } // Years are same so compare months
        else {
            if (month1 < month2) {
                dateDifference = 1;
            } else if (month1 > month2) {
                dateDifference = 2;
            } // Months are same so compare days
            else {
                if (day1 < day2) {
                    dateDifference = 1;
                } else if (day1 > day2) {
                    dateDifference = 2;
                } else {
                    dateDifference = 0;
                }
            }
        }

        // Return
        return dateDifference;
    }

    /**
     * Get the date after given days
     *
     * @param date Date object
     * @param days Number of days
     *
     * @return New Date
     * @throws DateUtilsException
     */
    public static Date getDateAfterGivenDays(Date date, int days)
            throws DateUtilsException {

        // Check input
        if (date == null) {
            throw new DateUtilsException("DEV_ERROR : Check the date '" + date);
        }

        // Time in milliseconds since "the epoch" (January 1, 1970,
        // 00:00:00 GMT)
        long t1 = date.getTime();
        long t2 = days * MILLISECONDS_IN_ONE_DAY;
        long newms = t1 + t2;
        Date newDate = new Date(newms);

        // Return
        return newDate;
    }

    /**
     * For given two dates return the difference in days.
     *
     * @param dateStr1 Date format string ('MM/DD/YYYY HH24:MM:SS' format)
     * @param dateStr2 Date format string ('MM/DD/YYYY HH24:MM:SS' format)
     *
     * @return Number of days difference
     *
     * @throws DateUtilsException
     */
    public static long getDate1MinusDate2_Days(String dateStr1, String dateStr2)
            throws DateUtilsException {

        // Parse the date
        Date date1 = parseDate(dateStr1);
        Date date2 = parseDate(dateStr2);

        // Return
        return getDate1MinusDate2_Days(date1, date2);
    }

    /**
     * For given two dates return the difference in days.
     *
     * @param date1 Date object
     * @param date2 Date object
     *
     * @return Number of days difference
     *
     * @throws DateUtilsException
     */
    public static long getDate1MinusDate2_Days(Date date1, Date date2)
            throws DateUtilsException {

        // Check input
        if (date1 == null || date2 == null) {
            throw new DateUtilsException("DEV_ERROR : Check the dates '" + date1 + "', '" + date2 + "'");
        }

        // Time in milliseconds since "the epoch" (January 1, 1970,
        // 00:00:00 GMT)
        long t1 = date1.getTime();
        long t2 = date2.getTime();
        long diff = t2 - t1;
        long days = diff / MILLISECONDS_IN_ONE_DAY; // Integral Division

        // Return
        return days;
    }

    /**
     * For given two dates return the difference in months.
     *
     * @param dateStr1 Date format string (DATE_FORMAT format)
     * @param dateStr2 Date format string (DATE_FORMAT format)
     *
     * @return Number of months difference
     *
     * @throws DateUtilsException
     */
    public static int getDate1MinusDate2_Months(String dateStr1, String dateStr2)
            throws DateUtilsException {

        // Parse the date
        Date date1 = parseDate(dateStr1);
        Date date2 = parseDate(dateStr2);

        // Return
        return getDate1MinusDate2_Months(date1, date2);
    }

    /**
     * For given two dates return the difference in months.
     *
     * @param date1 Date object
     * @param date2 Date object
     *
     * @return Number of months difference
     *
     * @throws DateUtilsException
     */
    public static int getDate1MinusDate2_Months(Date date1, Date date2)
            throws DateUtilsException {

        // Check input
        if (date1 == null || date2 == null) {
            throw new DateUtilsException("DEV_ERROR : Check the dates '" + date1 + "', '" + date2 + "'");
        }

        int monthDiff = 0;
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);

        int day1 = calendar1.get(Calendar.DAY_OF_MONTH);
        int day2 = calendar2.get(Calendar.DAY_OF_MONTH);
        int month1 = calendar1.get(Calendar.MONTH);
        int month2 = calendar2.get(Calendar.MONTH);
        int year1 = calendar1.get(Calendar.YEAR);
        int year2 = calendar2.get(Calendar.YEAR);

        if (month1 == month2) {
            if (year1 == year2) {
                monthDiff = 1;
            } else {
                monthDiff = ((year2 - year1) * 12) + 1;
            }
        } else {
            if (year1 == year2) {
                monthDiff = month2 - month1 + 1;
            } else {
                int totalMonthDiff = (year2 - year1) * 12;
                int tempMonthDiff = month2 - month1 + 1;
                monthDiff = totalMonthDiff + tempMonthDiff;
            }
        }

        //*** Modset -198 ************ UDS:Start:31/OCT/2007 *********************/
        /**
         * NOTE : This is commented but a thorough testing has to be done to
         * find out whther the following modification will affect the code
         * anywhere else. EXAMPLE : Whenever a vehicle comes for the payment of
         * tax in month of 31/July/2007. Then tax is calculated for the 2 months
         * only and not for 3 months. This is incorrect. But if we take tax on
         * 30/July/2007. Then tax is calculated correctly. This is happening so
         * because of the following code.
         */
        //if (day2 < day1) {
        //    monthDiff -= 1;
        // }
        //*************** UDS:End:31/OCT/2007 *********************/
        //*************** DIV:Start:12/Nov/2007 *********************/
        /**
         * NOTE : The above code again un-commented becoz it is not calculating
         * month difference correctly
         */
        /* System.out.println("...DateUtil...getDate1MinusDate2_Months...day2..."+day2);
         System.out.println("...DateUtil...getDate1MinusDate2_Months...day1..."+day1);
         System.out.println("...DateUtil...getDate1MinusDate2_Months...monthDiff..."+monthDiff);
         if (day2 < day1) {
         monthDiff -= 1;
         }*/
        //*************** DIV:Start:12/Nov/2007 - End *********************/
        //
        //ggh - 27 Nov, 2007 ----
        //NOTE:
        // By virtue of Modset - 198 (dt: 31/OCT/2007, UDS has commented the code -
        //if (day2 < day1) {
        //    monthDiff -= 1;
        // }
        // But, due to the absence of the above check, errors are comming in following cases -
        /* Example -
         *Case 3:

         Purchase Date : 13-11-05
         Regist.  Date : 12-11-07
         day_diff      : 729
         month_diff    : 25
         No. of Year   : 3
         Penalty       : 1200
         *Note: Here, month_diff is comming erroneous as 25, therefore Penalty is also
         *      calculated wrong due to wrong month_diff.
         *
         *After re-introducing the above check , we get following result -
         *Case 6:
         -------

         Purchase Date : 13-11-05
         Regist.  Date : 12-11-07
         day_diff      : 729
         month_diff    : 24
         No. of Year   : 2
         Penalty       : 800
         **Note: Here, month_diff is comming correct as 24, therefore Penalty is also
         *       being calculated correctly.
         *
         *Note: Pl. see the detail discussion against Modset No.
         *The reason for the above error is as follows -
         *
         * The normal calculation of month_diff may come incorrect , in case of
         * day_of_To_Date (i.e., day2) < day_of_From_Date (i.e., day1).
         * In such case, month_diff comes 1 more than the actual, e.g.,
         * if From_Date = 13-11-05, and To_Date = 12-11-07, then
         * day_of_To_Date (=12) < day_of_From_Date (=13).
         *
         * In this case, system calculates month_diff as 25, which is one more
         * than the actual and it is wrong.
         * Actual value of month_diff should be - 24.
         * Therefore, in such case, we do -  "monthDiff -= 1;"
         *
         * But, in a particular case of -
         * day_of_To_Date (i.e., day2) = 30 and
         * day_of_From_Date (i.e., day1) = 31,
         * such error does not come.
         * Therefore, we should not go for - "monthDiff -= 1;"
         *
         */
        if (day2 < day1) {
            if ((day2 == 30) && (day1 == 31)) {
                // System.out.println("...ggh .if..DateUtil...getDate1MinusDate2_Months...monthDiff..." + monthDiff);
            } else {
                // System.out.println("...ggh .else..DateUtil...getDate1MinusDate2_Months...monthDiff..." + monthDiff);
                monthDiff -= 1;
            }
        }

        // Return
        return monthDiff;
    }

    /**
     * Returns Day or Month or Year Part Of Date from the given date.
     *
     * @param date Date from which Day/Month/Year is to be found.
     * @param partOfDate Part of date (Day, Month, Year)
     *
     * @return part of date (Day, Month, Year)
     *
     * @throws DateUtilsException When date or partOfDate is invalid
     */
    public static int getDatePart(String date, int partOfDate)
            throws DateUtilsException {

        int datePart = 0;

        // Check input
        if (date == null) {
            throw new DateUtilsException("DEV_ERROR : Check the date '" + date + "'");
        }

        Date tempDate = parseDate(date);
        if (tempDate == null) {
            throw new DateUtilsException("DEV_ERROR : Check the date format '"
                    + tempDate + "'");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(tempDate);

        switch (partOfDate) {
            case DAY:
                datePart = calendar.get(Calendar.DAY_OF_MONTH);
                break;
            case MONTH:
                datePart = calendar.get(Calendar.MONTH) + 1;
                break;
            case YEAR:
                datePart = calendar.get(Calendar.YEAR);
                break;
            default:
                throw new DateUtilsException("DEV_ERROR : partOfDate "
                        + partOfDate + " is not valid");
        }

        // Return
        return datePart;
    }

    /**
     * Creates a date with given Day, Month and Year.
     *
     * @param day Day part of Date
     * @param month Month part of Date
     * @param year Year part of Date
     *
     * @return Date object created
     *
     * @throws DateUtilsException When Day, Month or Year is zero or negative
     * value
     */
    public static Date createDateObject(int day, int month, int year)
            throws DateUtilsException {

        // Check input
        if (day <= 0) {
            throw new DateUtilsException("DEV_ERROR : Check the day '" + day + "'");
        }

        if (month <= 0) {
            throw new DateUtilsException("DEV_ERROR : Check the month '" + month + "'");
        }

        if (year <= 0) {
            throw new DateUtilsException("DEV_ERROR : Check the year '" + year + "'");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);

        // Return
        return calendar.getTime();
    }

    /**
     * Creates a date with given Day,Month and Year.
     *
     * @param day Day part of Date
     * @param month Month part of Date
     * @param year Year part of Date
     *
     * @return String Created date
     *
     * @throws DateUtilsException When Day, Month or Year is zero or negative
     * value
     */
    public static String createDate(int day, int month, int year)
            throws DateUtilsException {

        Date date = createDateObject(day, month, year);
        return parseDate(date);
    }

    /**
     * Returns the starting date of the month in which given date lies. eg. if
     * date is 23/03/2003 the returned date would be 01/03/2003
     *
     * @param date Date for which the starting date of month is to be found
     *
     * @return String Start date of the month
     *
     * @throws DateUtilsException When date is invalid
     */
    public static String getStartOfMonthDate(String date) throws DateUtilsException {
        String startDate = null;
        Date tempDate = parseDate(date);

        if (tempDate != null) {
            // Set the date
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(tempDate);
            calendar.set(Calendar.DATE, 1);
            startDate = parseDate(calendar.getTime());
        } else {
            throw new DateUtilsException("DEV_ERROR : Check the date " + date);
        }

        // Return
        return startDate;
    }

    //Satish8Oct07----
    /**
     * Returns the starting date of the Quarter in which given date lies. eg. if
     * date is 23/03/2003 the returned date would be 01/01/2003
     *
     * @param date Date for which the starting date of month is to be found
     *
     * @return String Start date of the quarter
     *
     * @throws DateUtilsException When date is invalid
     */
    public static String getStartOfQuarterDate(String date) throws DateUtilsException {
        String startDate = null;

        String dtDate = null;
        String dtMnth = null;
        String dtYear = null;
        dtDate = date.substring(0, 2);
        dtMnth = date.substring(3, 5);
        dtYear = date.substring(6);

        switch (Integer.parseInt(dtMnth)) {

            case 01:
                dtMnth = "01";
                break;

            case 02:
                dtMnth = "01";
                break;

            case 03:
                dtMnth = "01";
                break;

            case 04:
                dtMnth = "04";
                break;

            case 05:
                dtMnth = "04";
                break;

            case 06:
                dtMnth = "04";
                break;

            case 07:
                dtMnth = "07";
                break;

            case 8:
                dtMnth = "07";
                break;

            case 9:
                dtMnth = "07";
                break;

            case 10:
                dtMnth = "10";
                break;

            case 11:
                dtMnth = "10";
                break;

            case 12:
                dtMnth = "10";
                break;

            default:
                dtMnth = "XXX";
                break;
        }
        startDate = 01 + "-" + dtMnth + "-" + dtYear;
        return startDate;
    }

    /////END OF QUARTER DATE///////////////Satish
    /**
     * Returns the end date of the Quarter in which given date lies. eg. if date
     * is 23/03/2003 the returned date would be 31/03/2003
     *
     * @param date Date for which the starting date of month is to be found
     *
     * @return String End date of the quarter
     *
     * @throws DateUtilsException When date is invalid
     */
    public static String getEndOfQuarterDate(String date) throws DateUtilsException {
        String endDate = null;

        String dtDate = null;
        String dtMnth = null;
        String dtYear = null;
        dtDate = date.substring(0, 2);
        dtMnth = date.substring(3, 5);
        dtYear = date.substring(6);

        switch (Integer.parseInt(dtMnth)) {

            case 01:
                dtDate = "31";
                dtMnth = "03";
                break;

            case 02:
                dtDate = "31";
                dtMnth = "03";
                break;

            case 03:
                dtDate = "31";
                dtMnth = "03";
                break;

            case 04:
                dtDate = "30";
                dtMnth = "06";
                break;

            case 05:
                dtDate = "30";
                dtMnth = "06";
                break;

            case 06:
                dtDate = "30";
                dtMnth = "06";
                break;

            case 07:
                dtDate = "30";
                dtMnth = "09";
                break;

            case 8:
                dtDate = "30";
                dtMnth = "09";
                break;

            case 9:
                dtDate = "30";
                dtMnth = "09";
                break;

            case 10:
                dtDate = "31";
                dtMnth = "12";
                break;

            case 11:
                dtDate = "31";
                dtMnth = "12";
                break;

            case 12:
                dtDate = "31";
                dtMnth = "12";
                break;

            default:
                dtDate = "AA";
                dtMnth = "XXX";
                break;
        }
        endDate = dtDate + "-" + dtMnth + "-" + dtYear;
        return endDate;
    }

    ////////////////////////////
    //ggh ----------------9th April 2007------------------------------------------Start
    ///// ggh - inner-3 ///// Start
    /**
     * Returns different dates of a Financial Year in which given date lies. eg.
     * if date is 23/03/2003 and index = 0, then, Start Date of the Financial
     * Year - (1/4/2002 to 31/3/2003) in which 'date' (i.e., 23/03/2003) lies,
     * is the return value (i.e.,1/4/2002)
     *
     * @author GGH
     *
     * @param date - Date for which the desired date of the concerned Financial
     * Year , is to be found, depending on the 'index' value.
     * @param index - Flag value which indicates the type of date calculation.
     * e.g., if index = 0, the function calculates the start of concerned
     * Financial Year. e.g., if index = 1, the function calculates the End Date
     * of concerned Financial Year.
     *
     * @return String - Desired Date of concerned Financial Year
     *
     * @throws DateUtilsException When date is invalid
     */
    public static String getDatesOf_FinancialYear(String date, int index) throws DateUtilsException {

        String endDate = null;
        String startDate = null;
        String desiredDate = null;
        Date tempDate = parseDate(date);

        if (tempDate != null) {
            // Set the date
            Calendar calendar = Calendar.getInstance();

            switch (index) {
                case 0:
                    calendar.setTime(tempDate);
                    // Start Date of a Financial Year in which 'date' lies
                    // NOTE: It is to be noted that -
                    //       getDatePart(date, MONTH)- function - returns -
                    //       '1' - if MONTH = January
                    //       '2' - if MONTH = February
                    //       '3' - if MONTH = March
                    //       '4' - if MONTH = April , etc.
                    if (getDatePart(date, MONTH) < 4) {
                        // It is a case - when supplied 'date' lise
                        // between Jan to March
                        // In this case, the start of Financial Year -
                        calendar.set(Calendar.DATE, 1);
                        // NOTE: It is to be noted that -
                        //       calendar.set(Calendar.MONTH, iMonth) - function
                        //       iMonth = 0 --- for January
                        //       iMonth = 1 --- for February
                        //       iMonth = 2 --- for March
                        //       iMonth = 3 --- for April, etc.
                        calendar.set(Calendar.MONTH, 3);
                        calendar.add(Calendar.YEAR, -1);

                    } else {
                        // It is a case - when supplied 'date' lise
                        // between April to December
                        // In this case, the start of Financial Year -
                        calendar.set(Calendar.DATE, 1);
                        calendar.set(Calendar.MONTH, 3);

                    }
                    break;
                case 1:
                    // End Date of a Financial Year in which 'date' lies
                    startDate = getStartOfFinancialYear(date);
                    Date tempDate1 = parseDate(startDate);

                    //Set the 'calender' with the end of Financial Year of
                    // the supplied 'date'
                    calendar.setTime(tempDate1);

                    calendar.set(Calendar.DATE, 31);
                    calendar.set(Calendar.MONTH, 2);
                    calendar.add(Calendar.YEAR, 1);
                    break;
                case 2:
                    // Start of Next Financial Year
                    //
                    startDate = getStartOfFinancialYear(date);
                    Date tempDate3 = parseDate(startDate);

                    //Set the 'calender' with the Start of
                    // Next Financial Year of the supplied 'date'
                    //
                    calendar.setTime(tempDate3);

                    calendar.add(Calendar.YEAR, 1);
                    break;
                case 3:
                    // End of Next Financial Year
                    //
                    startDate = getStartOfFinancialYear(date);
                    Date tempDate4 = parseDate(startDate);

                    //Set the 'calender' with the end of  Next
                    //Financial Year of the supplied 'date'
                    //
                    calendar.setTime(tempDate4);

                    calendar.set(Calendar.DATE, 31);
                    calendar.set(Calendar.MONTH, 2);
                    calendar.add(Calendar.YEAR, 2);
                    break;
                case 4:
                    // Start of Previous Financial Year
                    //
                    startDate = getStartOfFinancialYear(date);
                    Date tempDate5 = parseDate(startDate);

                    //Set the 'calender' with the start of Previous
                    //Financial Year of the supplied 'date'
                    //
                    calendar.setTime(tempDate5);

                    calendar.add(Calendar.YEAR, -1);
                    break;
                case 5:
                    // End of Previous Financial Year
                    //
                    startDate = getStartOfFinancialYear(date);
                    Date tempDate6 = parseDate(startDate);

                    //Set the 'calender' with the end of Previous
                    //Financial Year of the supplied 'date'
                    //
                    calendar.setTime(tempDate6);

                    calendar.set(Calendar.DATE, 31);
                    calendar.set(Calendar.MONTH, 2);
                    break;
                default:
                    // By default we will set the Calender as Start of
                    // Financial Year
                    startDate = getStartOfFinancialYear(date);
                    Date tempDate2 = parseDate(startDate);
                    calendar.setTime(tempDate2);
            } // End of Switch Stmt.
            desiredDate = parseDate(calendar.getTime());
            //

            //ggh - End
            //
        } // End of "if (tempDate != null)"
        else {
            throw new DateUtilsException("DEV_ERROR : Check the date " + date);
        }

        // Return
        return desiredDate;
    }
//

    /**
     * Returns Start of a Financial Year in which given date lies. eg. if date
     * is 23/03/2003 , then, Start Date of the Financial Year - 1/04/2004 in
     * which 'date' (i.e., 23/03/2003) lies, is the return value
     *
     * @author GGH
     *
     * @param date - Date for which the Start date of the concerned Financial
     * Year , is to be found.
     *
     * @return String - Start Date of concerned Financial Year
     *
     * @throws DateUtilsException When date is invalid
     */
    public static String getStartOfFinancialYear(String date) throws DateUtilsException {
        String startDate = null;
        int flagValue = 0;
        //
        // Call - getDatesOf_FinancialYear()- function with flagValue =0;
        // For flagValue = 0, this function returns Start Date of Financial Yr.
        if (date != null) {
            startDate = getDatesOf_FinancialYear(date, flagValue);
        } // End of "if (date != null)"
        else {
            throw new DateUtilsException("DEV_ERROR : Check the date " + date);
        }
        //Return
        return startDate;
    }
//

    /**
     * Returns End of a Financial Year in which given date lies. eg. if date is
     * 23/03/2003 , then, End Date of the Financial Year - 31/3/2003 in which
     * 'date' (i.e., 23/03/2003) lies, is the return value
     *
     * @author GGH
     *
     * @param date - Date for which the desired date of the concerned Financial
     * Year , is to be found.
     *
     * @return String - Desired Date of concerned Financial Year
     *
     * @throws DateUtilsException When date is invalid
     */
    public static String getEndOfFinancialYear(String date) throws DateUtilsException {
        String endDate = null;
        int flagValue = 1;
        //
        // Call - getDatesOf_FinancialYear()- function with flagValue =1;
        // For flagValue = 1, this function returns End Date of Financial Yr.
        if (date != null) {
            endDate = getDatesOf_FinancialYear(date, flagValue);
        } // End of "if (date != null)"
        else {
            throw new DateUtilsException("DEV_ERROR : Check the date " + date);
        }
        //Return
        return endDate;
    }

    public static String getStartOf_NextFinancialYear(String date) throws DateUtilsException {
        String startDate = null;
        int flagValue = 2;
        //
        // Call - getDatesOf_FinancialYear()- function with flagValue =2;
        // For flagValue = 2, this function returns
        // Start Date of Next Financial Yr.
        if (date != null) {
            startDate = getDatesOf_FinancialYear(date, flagValue);
        } // End of "if (date != null)"
        else {
            throw new DateUtilsException("DEV_ERROR : Check the date " + date);
        }
        //Return
        return startDate;
    }
//

    public static String getEndOf_NextFinancialYear(String date) throws DateUtilsException {
        String endDate = null;
        int flagValue = 3;
        //
        // Call - getDatesOf_FinancialYear()- function with flagValue =3;
        // For flagValue = 3, this function returns
        // End Date of Next Financial Yr.
        if (date != null) {
            endDate = getDatesOf_FinancialYear(date, flagValue);
        } // End of "if (date != null)"
        else {
            throw new DateUtilsException("DEV_ERROR : Check the date " + date);
        }
        //Return
        return endDate;
    }

    public static String getStartOf_PreviousFinancialYear(String date) throws DateUtilsException {
        String startDate = null;
        int flagValue = 4;
        //
        // Call - getDatesOf_FinancialYear()- function with flagValue =4;
        // For flagValue = 4, this function returns
        // Start Date of Previous Financial Yr.
        if (date != null) {
            startDate = getDatesOf_FinancialYear(date, flagValue);
        } // End of "if (date != null)"
        else {
            throw new DateUtilsException("DEV_ERROR : Check the date " + date);
        }
        //Return
        return startDate;
    }
//

    public static String getEndOf_PreviousFinancialYear(String date) throws DateUtilsException {
        String endDate = null;
        int flagValue = 5;
        //
        // Call - getDatesOf_FinancialYear()- function with flagValue =5;
        // For flagValue = 5, this function returns
        // End Date of Previous Financial Yr.
        if (date != null) {
            endDate = getDatesOf_FinancialYear(date, flagValue);
        } // End of "if (date != null)"
        else {
            throw new DateUtilsException("DEV_ERROR : Check the date " + date);
        }
        //Return
        return endDate;
    }

    //ggh ------------------------9th April 2007----------------------------------End
    /**
     * Returns the last date of the month in which given date lies. eg. if date
     * is 23/03/2003 the returned date would be 31/03/2003
     *
     * @param date Date for which the last date of month is to be found
     *
     * @return String Last date of the month
     *
     * @throws DateUtilsException When date is invalid
     */
    public static String getLastOfMonthDate(String date) throws DateUtilsException {
        String lastDate = null;
        Date tempDate = parseDate(date);

        if (tempDate != null) {
            // Set the date
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(tempDate);
            calendar.set(Calendar.DATE, 1);
            calendar.add(Calendar.MONTH, 1);
            calendar.add(Calendar.DATE, -1);
            lastDate = parseDate(calendar.getTime());
        } else {
            throw new DateUtilsException("DEV_ERROR : Check the date " + date);
        }

        // Return
        return lastDate;
    }

    /**
     * Adds or Subtracts days, months, years to the date.
     *
     * @param date Date in which days/months/years are to be added
     * @param datePart Part of date which is to be added - day/month/year
     * @param duration Duration of datePart if positive value passed adds to the
     * date, if negative value is passed then subtracts it.
     *
     * @return Date with added Date part
     *
     * @throws DateUtilsException When date or datePart is invalid, duration is
     * zero
     */
    public static Date addToDate(Date date, int datePart, int duration)
            throws DateUtilsException {

        String strDate = null;
        Date modifiedDate = null;

        if (date == null) {
            throw new DateUtilsException("DEV_ERROR : Check the date " + date);
        }

        if (duration == 0) {
            throw new DateUtilsException("DEV_ERROR : invalid duration " + duration);
        }

        // Set the date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        switch (datePart) {
            case DAY:
                calendar.add(Calendar.DATE, duration);
                break;
            case MONTH:
                calendar.add(Calendar.MONTH, duration);
                break;
            case YEAR:
                calendar.add(Calendar.YEAR, duration);
                break;
            default:
                throw new DateUtilsException("DEV_ERROR : invalid Date part "
                        + datePart + " is not valid");
        }

        strDate = parseDate(calendar.getTime());
        modifiedDate = parseDate(strDate);

        // Return
        return modifiedDate;
    }

    /**
     * Adds or Subtracts days, months, years to the date.
     *
     * @param date Date in which days/months/years are to be added
     * @param datePart Part of date which is to be added - day/month/year
     * @param duration Duration of datePart if positive value passed adds to the
     * date, if negative value is passed then subtracts it.
     *
     * @return Date with added Date part
     *
     * @throws DateUtilsException When date or datePart is invalid, duration is
     * zero
     */
    public static Date addToDate(String date, int datePart, int duration)
            throws DateUtilsException {

        Date tempDate = parseDate(date);
        return addToDate(tempDate, datePart, duration);
    }

    /**
     * Check whether date1 is after date2
     *
     * @param strDate1 First date in string format
     * @param strDate2 Second date in string format
     *
     * @return true if passed date1 is after date2 else false
     */
    public static boolean isAfter(String strDate1, String strDate2) {
        boolean result = false;
        if (nic.java.util.DateUtils.compareDates(strDate1, strDate2) == 2) {
            result = true;
        }

        // Return
        return result;
    }

    /**
     * Check whether date1 is before date2
     *
     * @param strDate1 First date in string format
     * @param strDate2 Second date in string format
     *
     * @return true if passed date1 is before date2 else false
     */
    public static boolean isBefore(String strDate1, String strDate2) {
        boolean result = false;
        if (nic.java.util.DateUtils.compareDates(strDate1, strDate2) == 1) {
            result = true;
        }

        // Return
        return result;
    }

    /**
     * Checks if the given date is a week day. The week days are - Monday to
     * Friday, if the date falls on any of these days the function returns true,
     * else returns false.
     *
     * @param date Date which is checked if it is a week day
     *
     * @return Returns true if the date is a week day, else returns false
     *
     * @throws DateUtilsException When date parameter is null
     */
    public static boolean isWeekDay(java.util.Date date) throws DateUtilsException {
        if (date == null) {
            throw new DateUtilsException("DEV_ERROR : Check the date '" + date + "'");
        }

        boolean weekDay = false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.MONDAY:
            case Calendar.TUESDAY:
            case Calendar.WEDNESDAY:
            case Calendar.THURSDAY:
            case Calendar.FRIDAY:
            case Calendar.SATURDAY:
                weekDay = true;
                break;

            case Calendar.SUNDAY:
                weekDay = false;
                break;

            default:
                Debug.log(Debug.BUG + "Control should never come here"
                        + " TaxCalculator:isWeekDay");
                break;
        }

        //Return
        return weekDay;
    }

    /**
     * Here get the number of days in particular month
     *
     * @param c GregorianCalendar
     *
     * @return int Number of days in particular month
     */
    public static int daysInMonth(GregorianCalendar c) {
        int[] daysInMonths = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        daysInMonths[1] += c.isLeapYear(c.get(GregorianCalendar.YEAR)) ? 1 : 0;
        return daysInMonths[c.get(GregorianCalendar.MONTH)];
    }

    /**
     * Here get the date in format 'DD-MM-YYYY'
     *
     * @return String Date in format 'DD-MM-YYYY'
     */
    public static String formatDate(String strDate) {
        int dashPos1 = 0, dashPos2 = 0, dashPos3 = 0;
        String strDay = "00", strMonth = "00", strYear = "0000", strcurrCent = "";
        strDate = strDate.replace('.', '-');
        strDate = strDate.replace('/', '-');
        //
        dashPos1 = strDate.indexOf('-');
        if (dashPos1 > -1) {
            dashPos2 = strDate.indexOf('-', dashPos1 + 1);
            if (dashPos2 > -1) {
                dashPos3 = strDate.indexOf('-', dashPos2 + 1);
                if (dashPos3 > -1) {
                    strDate = strDate.substring(0, dashPos3);
                }
            }
        }
        //
        dashPos1 = strDate.indexOf('-');
        if (dashPos1 > -1) {
            strDay = strDate.substring(0, dashPos1).trim();
            switch (strDay.length()) {
                case 0:
                    strDay = "00";
                    break;
                case 1:
                    strDay = "0" + strDay;
                    break;
                default:
                    strDay = strDay.substring(0, 2);
                    break;
            }
            dashPos2 = strDate.indexOf('-', dashPos1 + 1);
            if (dashPos2 > -1) {
                strMonth = strDate.substring(dashPos1 + 1, dashPos2).trim();
                switch (strMonth.length()) {
                    case 0:
                        strMonth = "00";
                        break;
                    case 1:
                        strMonth = "0" + strMonth;
                        break;
                    default:
                        strMonth = strMonth.substring(0, 2);
                        break;
                }
                strYear = strDate.substring(dashPos2 + 1).trim();
                strcurrCent = getCurrentDate().substring(6, 8);
                switch (strYear.length()) {
                    case 0:
                        strYear = strcurrCent + "00";
                        break;

                    case 1:
                        strYear = strcurrCent + "0" + strYear;
                        break;

                    case 2:
                        strYear = strcurrCent + strYear;
                        break;

                    case 3:
                        strYear = "0" + strYear;
                        break;

                    default:
                        strYear = strYear.substring(0, 4);
                        break;
                }
            }

            strDate = strDay + '-' + strMonth + '-' + strYear;
        }

        return strDate;
    }

    /**
     * Here get the current date in format 'dd-MM-yyyy'
     *
     * @return String Current date in format 'dd-MM-yyyy'
     */
    public static String getCurrentDate() {
        //    get current date & time
        /*
         ** on some JDK, the default TimeZone is wrong
         ** we must set the TimeZone manually!!!
         **   Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("EST"));
         */
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(nic.java.util.DateUtils.DATE_FORMAT);

        /*
         ** on some JDK, the default TimeZone is wrong
         ** we must set the TimeZone manually!!!
         **     sdf.setTimeZone(TimeZone.getTimeZone("EST"));
         */
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(cal.getTime());
    }

    /**
     * Here get the date in format YYYYMMDD
     *
     * @param c GregorianCalendar
     *
     * @return String Date in YYYYMMDD string format
     */
    public static String getStrDate(GregorianCalendar c) {
        //    Returns as a String (YYYYMMDD) a GregorianCalendar date
        int m = c.get(GregorianCalendar.MONTH) + 1;
        int d = c.get(GregorianCalendar.DATE);
        //return  "" +  c.get(GregorianCalendar.YEAR) + (m < 10 ? "0" + m : m) + (d < 10 ? "0" + d : d);
        return "" + c.get(GregorianCalendar.YEAR) + m + d;
    }

    /**
     * Here validate the date of format 'DD-MM-YYYY'
     *
     * @param strDate Date in 'DD-MM-YYYY' format
     *
     * @return boolean Input date is valid or not.
     */
    public static boolean validateDate(String strDate) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(nic.java.util.DateUtils.DATE_FORMAT);

        /*
         ** on some JDK, the default TimeZone is wrong
         ** we must set the TimeZone manually!!!
         **     sdf.setTimeZone(TimeZone.getTimeZone("EST"));
         */
        sdf.setTimeZone(TimeZone.getDefault());
        try {
            sdf.setLenient(false);  // this is important!
            Date dt2 = sdf.parse(strDate);
            return true;
        } catch (ParseException e) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Here get the current date in format 'YYYY-MM-DD'
     *
     * @return Current date in format 'YYYY-MM-DD'
     */
    public static String getCurrentDate_YYYY_MM_DD() {
        //    get current date in Format YYYY-MM-DD
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        final String dateFormat = "yyyy-MM-dd";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(dateFormat);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(cal.getTime());
    }

    /**
     * Get the date before given days
     *
     * @param date Date object
     * @param days Number of days
     *
     * @return New Date
     * @throws DateUtilsException
     */
    public static Date getDateBeforeGivenDays(Date date, int days)
            throws DateUtilsException {

        // Check input
        if (date == null) {
            throw new DateUtilsException("DEV_ERROR : Check the date '" + date);
        }

        // Time in milliseconds since "the epoch" (January 1, 1970,
        // 00:00:00 GMT)
        long t1 = date.getTime();
        long t2 = days * MILLISECONDS_IN_ONE_DAY;
        long newms = t1 - t2;
        Date newDate = new Date(newms);

        // Return
        return newDate;
    }

    /**
     * This method will convert the date into particular format
     *
     * @param format
     * @param date
     * @return
     * @ PAWAN SOOD
     */
    public static String formatDate(String format, Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar c1 = Calendar.getInstance(); // today
        c1.setTime(date);
        return sdf.format(c1.getTime());
    }

    /**
     * Returns the current month
     *
     * @return int
     */
    public static int getCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * This method returns the current year
     *
     * @return int
     */
    public static int getCurrentYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }

    /**
     * Returns the no of days
     *
     * @return int
     */
    public static int getNoDays() {
        Calendar cal = Calendar.getInstance();
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);

    }

    //test :takes the string in yyyy-MM-dd format and return util date 07oct2011
    public static String convertStringYYYYMMDDToDDMMYYYY(String strDt) {
        String ddMMyyyy = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date = null;
        try {
            date = sdf.parse(strDt);
            ddMMyyyy = getDateInDDMMYYYY(date);
        } catch (ParseException ex) {
//            System.out.println("test1:exception raised here");
//            ddMMyyyy=convertStringDDMMMYYYYToDDMMYYYY(strDt);
            ex.printStackTrace();
        }
        return ddMMyyyy;
    }

    //test 18oct2011
    public static String convertStringDDMMMYYYYToDDMMYYYY(String strDt) {
        String ddMMyyyy = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        java.util.Date date = null;
        try {
            date = sdf.parse(strDt);
            ddMMyyyy = getDateInDDMMYYYY(date);
        } catch (ParseException ex) {
            ex.printStackTrace();
            System.out.println("test2:exception raised..");
        }
        return ddMMyyyy;
    }

    public static String convertStringDDMMMYYYYToYYYYMMDD(String strDt) {
        String yyyyMMdd = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date = null;
        try {
            date = sdf.parse(strDt);
            yyyyMMdd = sdf1.format(date);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return yyyyMMdd;
    }

    public static Date converStringToDateHtmlCalenda(String dateString) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yy");
        java.util.Date dt = null;
        try {
            dt = (Date) sdf.parse(dateString);
        } catch (java.text.ParseException p) {
            Logger.getLogger(p.getMessage());
        }
        return dt;
    }

    public static Timestamp getTimeStampYYMMDDHHMMSS(String Date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Timestamp st = null;
        try {
            java.util.Date date = sdf.parse(Date);
            st = new Timestamp(date.getTime());
        } catch (ParseException pr) {
            pr.printStackTrace();
        }
        return st;
    }

    public static Timestamp getTimeStamp(String Date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yy");

        Timestamp st = null;
        try {
            java.util.Date date = sdf.parse(Date);
            st = new Timestamp(date.getTime());

        } catch (ParseException pr) {
            pr.printStackTrace();
        }
        return st;
    }

    public static String getCurrentDateTimeAsString() {
        Calendar cal = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        // return sdf.format(getCurrentDate());
        return sdf.format(cal.getTime());
    }

    public static String convertDatePatternEEE_MMM_dd(String dateToParse) throws ParseException {
        if (dateToParse != null && !dateToParse.equals("")) {
            SimpleDateFormat sdf1 = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yy");
            String dateStringToParse = dateToParse;
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
            Date date = sdf1.parse(dateStringToParse);
            dateToParse = sdf2.format(date);
            return dateToParse;

        } else {
            //VahanBean.showMessage("Please check the date");
        }
        return "";
    }

    public static String convertDatePattern(String dateToParse) throws VahanException {
        if (dateToParse != null && !dateToParse.equals("")) {
            SimpleDateFormat sdf1 = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yy");
            String dateStringToParse = dateToParse;
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MMM-yyyy");
            Date date;
            try {
                date = sdf1.parse(dateStringToParse);
            } catch (ParseException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                throw new VahanException("No Proper Date format found");
            }
            dateToParse = sdf2.format(date);
            return dateToParse;
        } else {
            throw new VahanException("Invalid date parameter");
        }

    }

    // Methods copied from ServerUtils.java
    /**
     * Converts String date from one format to other format by vinay
     *
     * @param String date in the format YYYY-MM-dd
     * @return string date in the format dd-MM-YYYY
     */
    public static String getStringYYYYMMDDtoStringDDMMYYYY(String input_dt) {

        String formated_dt = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt1 = sdf.parse(input_dt);
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
            formated_dt = sdf1.format(dt1);
            //LOGGER.info("the new date " + formated_dt);
        } catch (ParseException pe) {
            LOGGER.error(pe);
        }

        return formated_dt;
    }

    public static Date dateRange(Date date, int year, int month, int day_of_month) {
        java.util.Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(java.util.Calendar.YEAR, year);
        cal.add(java.util.Calendar.MONTH, month);
        cal.add(java.util.Calendar.DAY_OF_MONTH, day_of_month);
        return cal.getTime();
    }

    public static Date parseDateFromYYYYMMDD(String dateStr) {
        // Check input

        if (dateStr == null) {
            return null;
        }
        //69. SAR_MAN -24/02/2007 --- inserted next line ----start-----part1--

        dateStr = dateStr.replace('/', '-');
        //69. SAR_MAN -24/02/2007 ---end-----part1--
        // Create the formatter object
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        // Do not parse the string if it do not adhere to the format given
        formatter.setLenient(false);

        // Parse
        ParsePosition pos = new ParsePosition(0);
        Date date = formatter.parse(dateStr, pos);

        // Return
        return date;
    }

//    public static Date parseDate(String dateStr) {
//        // Check input
//
//        if (dateStr == null) {
//            return null;
//        }
//        //69. SAR_MAN -24/02/2007 --- inserted next line ----start-----part1--
//
//        dateStr = dateStr.replace('/', '-');
//        //69. SAR_MAN -24/02/2007 ---end-----part1--
//        // Create the formatter object
//        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
//
//        // Do not parse the string if it do not adhere to the format given
//        formatter.setLenient(false);
//
//        // Parse
//        ParsePosition pos = new ParsePosition(0);
//        Date date = formatter.parse(dateStr, pos);
//
//        // Return
//        return date;
//    }
    public static Date parseDate1(String dateStr) {
        // Check input

        if (dateStr == null) {
            return null;
        }
        //69. SAR_MAN -24/02/2007 --- inserted next line ----start-----part1--

        dateStr = dateStr.replace('/', '-');
        //69. SAR_MAN -24/02/2007 ---end-----part1--
        // Create the formatter object
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        // Do not parse the string if it do not adhere to the format given
        formatter.setLenient(false);

        // Parse
        ParsePosition pos = new ParsePosition(0);
        Date date = formatter.parse(dateStr, pos);

        // Return
        return date;
    }

    public static String getStringDDMMMYYYYtoStringDDMMYYYY(String input_dt) {

        String formated_dt = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date dt1 = sdf.parse(input_dt);
            //LOGGER.info("formated date " + dt1);
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
            formated_dt = sdf1.format(dt1);
            //LOGGER.info("the new date " + formated_dt);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        return formated_dt;
    }

    public static String getStringYYYYMMDDHHmmsstoStringDDMMYYYY(String input_dt) {

        String formated_dt = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date dt1 = sdf.parse(input_dt);
            // LOGGER.info("formated date " + dt1);
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
            formated_dt = sdf1.format(dt1);
            // LOGGER.info("the new date " + formated_dt);
        } catch (ParseException pe) {
            LOGGER.error(pe);
        }

        return formated_dt;
    }

    public static Timestamp getCurrentTimeStamp() {
        return getDateToTimesTamp(getCurrentDateDDMMYYYHHMMSS());
    }

    public static String getCurrentDateDDMMYYYHHMMSS() {
        //    get current date & time

        java.util.Calendar cal = java.util.Calendar.getInstance(TimeZone.getDefault());
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");

        /*
         ** on some JDK, the default TimeZone is wrong
         ** we must set the TimeZone manually!!!
         **     sdf.setTimeZone(TimeZone.getTimeZone("EST"));
         */
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(cal.getTime());
    }

    public static String getCurrentDateDDMMYYY() {
        //    get current date & time

        java.util.Calendar cal = java.util.Calendar.getInstance(TimeZone.getDefault());
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");

        /*
         ** on some JDK, the default TimeZone is wrong
         ** we must set the TimeZone manually!!!
         **     sdf.setTimeZone(TimeZone.getTimeZone("EST"));
         */
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(cal.getTime());
    }

    /**
     *
     * @param string date in many format as dd-MM-yyyy hh:mm:ss a
     * @return TimeStamp
     */
    public static Timestamp getDateToTimesTamp(String strDt) {
        //SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        // the Date format is modified to 24 hrs format :
        // previously set to 12 Hrs format.
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
        Timestamp timeStampDate = null;
        Date date = null;
        try {
            date = sdf1.parse(strDt);
//            LOGGER.info("1");
        } catch (ParseException ex) {
            // Logger.getLogger(DateUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (date == null) {

            sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
            try {
                date = sdf1.parse(strDt);
//                LOGGER.info("2");
            } catch (ParseException ex) {
                // Logger.getLogger(DateUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (date == null) {

            sdf1 = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
            try {
                date = sdf1.parse(strDt);
//                LOGGER.info("3");
            } catch (ParseException ex) {
                // Logger.getLogger(DateUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (date == null) {

            sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            try {
                date = sdf1.parse(strDt);
//                LOGGER.info("5");
            } catch (ParseException ex) {
                // Logger.getLogger(DateUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (date == null) {

            sdf1 = new SimpleDateFormat("dd-MM-yyyy");
            try {
                date = sdf1.parse(strDt);
//                LOGGER.info("4");
            } catch (ParseException ex) {
                // Logger.getLogger(DateUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (date != null) {
            timeStampDate = new Timestamp(date.getTime());
        }
        return timeStampDate;
    }

    public static String parseDateDDMMYYYYToString(Date dt) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        sdf.applyPattern("dd-MM-yyyy");
        String nDate = sdf.format(dt);
        return nDate;
    }

    public static String parseDateYYYYMMDDToString(Date dt) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.applyPattern("yyyy-MM-dd");
        String nDate = sdf.format(dt);
        return nDate;
    }

    public static String parseDateToString(Date dt) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        sdf.applyPattern("dd-MMM-yyyy");
        String nDate = sdf.format(dt);
        return nDate;
    }

    public static String getStringTimeStampFormatted(java.util.Date timestamp) {
        String result = null;
        String DATE_FORMAT = "dd-MMM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        long timeLong = timestamp.getTime();
        Date date = new Date(timeLong);
        result = sdf.format(date);
        //  System.out.println(" Formatted Date : " + result);
        return result;
    }

    public static boolean compareCurrentTimeAndOfficeTime(String currentTime, String tableTime) {
        String[] currentTimeArr = currentTime.split(":");
        String[] tableTimeArr = tableTime.split(":");
        long currentT = Integer.parseInt(currentTimeArr[0]) * 3600 + Integer.parseInt(currentTimeArr[1]) * 60 + Integer.parseInt(currentTimeArr[2]);
        long tableT = Integer.parseInt(tableTimeArr[0]) * 3600 + Integer.parseInt(tableTimeArr[1]) * 60 + Integer.parseInt(tableTimeArr[2]);
        return (currentT > tableT);
    }
}
