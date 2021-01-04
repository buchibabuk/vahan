/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.server.taxcal;

import dao.ConfigUtil;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import nic.java.util.DateUtils;
import nic.java.util.DateUtilsException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author nic5912
 */
public class TaxCalVB {

    private static final Logger LOGGER = Logger.getLogger(TaxCalVB.class);
    // desc=Road Tax,pur_cd= 58,fromdate= 26-May-2014,todate 25-May-2029,tax amt= 18000, 0, 0, 0, 0,service charge= 100

    public static DOTaxDetails[] calculateTax(String[] params) {
        DOTaxDetails[] dobjTax = null;

        try {
            String taxParm = "rtocd=9"
                    + "|regnno=" + params[0]
                    + "|taxmode=" + params[1]
                    + "|pvtcomm=" + params[2]
                    + "|registrationtype=" + params[3]
                    + "|ownership=" + params[4]
                    + "|purchasedate=" + params[5]
                    + "|vehicleclass=" + params[6]
                    + "|seatcap=" + params[7]
                    + "|standcap="
                    + "|unladenwt=" + params[8]
                    + "|ladenwt=" + params[9]
                    + "|hp=" + params[10]
                    + "|cc=" + params[11]
                    + "|saleamount=" + params[12]
                    + "|fueltype=" + params[13]
                    + "|flag=" + "TRUE"
                    + "|vchcatg=" + params[14]
                    + "|acfitted=" + params[15]
                    + "|othercriteria=|permittype=|servicetype="
                    + "|trailerulw=|trailergvw=|domain=|sleepercapacity="
                    + "|returnurl=";
            /*
             taxParm = "rtocd=9|regnno=NEW0000001|taxmode=L"
             + "|pvtcomm=2|registrationtype=N|ownership=1|purchasedate=26-05-2014"
             + "|vehicleclass=95|seatcap=5|unladenwt=2000|ladenwt=2000|hp=3|cc=5|saleamount=300000|fueltype=1|flag=TRUE"
             + "|vchcatg=LMV" + "|acfitted=N|returnurl=http://localhost/";
             */

            String encdata = vahanEncryption(taxParm);
            encdata = chksum(encdata);
            String url = ConfigUtil.tax_url;
            HttpClient client = new DefaultHttpClient();
            HttpContext context = new BasicHttpContext();
            URIBuilder builder = new URIBuilder(url);
            //ambrish disabling enc builder.addParameter("encreqtaxdata", encdata);
            builder.addParameter("encreqtaxdata", taxParm);
            HttpPost postTaxRequest = new HttpPost(builder.build());
            HttpResponse response = client.execute(postTaxRequest, context);    //call the dotnet aspx page and the result will be handed to response
            HttpEntity entity = response.getEntity();                           //transfer the response data to entity(entity holds the entire returned page)
            String responseString = "";
            if (entity != null) {
                String body = EntityUtils.toString(entity);
                if (body.contains("404 Not Found")) {
                } else {
                    dobjTax = checkAndSplitTaxValue(body);
                }
            } else {
                //  reset
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return dobjTax;
    }

    public static String[][] getTaxArry(String tax_details) {
        String tax_array[][] = null;
        String diff_tax[] = null;
        if (tax_details != null) {
            diff_tax = tax_details.split("#@#");
            String tax_col[] = null;
            tax_array = new String[diff_tax.length][10];
            for (int i = 0; i < diff_tax.length; i++) {
                tax_col = diff_tax[i].split(",");
                for (int j = 0; j < tax_col.length; j++) {
                    tax_array[i][j] = tax_col[j];
                }
            }
        }

        return tax_array;
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
        for (int i = 0; i < strVal.length() - 1; i += 2) {
            String output = strVal.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char) decimal);
        }
        return sb.toString();
    }

    public static String MD5(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md;
        md = MessageDigest.getInstance("MD5");
        byte[] md5hash = new byte[32];
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        md5hash = md.digest();
        return convertToHex(md5hash);
    }

    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    private static boolean checksumVerify(String data) {
        try {
            String strMd5 = data.substring(5, 37);
            String encdataMd5 = data.substring(0, 5) + data.substring(37);
            encdataMd5 = MD5(encdataMd5);
            if (strMd5.equals(encdataMd5)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            return false;
        }
    }

    public static DOTaxDetails[] splitTaxValues(String retVal) {
        String taxHead = "";
        int feeCode = 0;
        String taxFrom = "";
        String taxTo = "";
        String taxFromTo = "";
        int grandTotalAmount = 0;
        int totalTaxAmount = 0;
        int amount = 0;
        int exempted = 0;
        int fine = 0;
        int rebate = 0;
        int surcharge = 0;

        int user_charge = 0;
        int tax_head = 0;
        int tax_penalty = 0;

        int serviceCh = 0;


        String taxpaid = "";
        String road_taxFrmDt = "";
        String road_taxUpDt = "";
        taxpaid = retVal;

        DOTaxDetails[] dobjTax = null;



        if (taxpaid != null && !taxpaid.equalsIgnoreCase("")) {
            String[][] tax = getTaxArry(taxpaid);
            if (tax != null) {
                dobjTax = new DOTaxDetails[tax.length];
                for (int i = 0; i < tax.length; i++) {
                    dobjTax[i] = new DOTaxDetails();
                    dobjTax[i].setSL_NO(new Integer(i + 1).toString());

                    taxHead = tax[i][0];

                    dobjTax[i].setHEAD(taxHead);

                    feeCode = Integer.parseInt(tax[i][1].trim());
                    dobjTax[i].setPUR_CD(feeCode);
                    taxFrom = tax[i][2].trim();
                    dobjTax[i].setTAX_FROM(DateUtils.parseDate(taxFrom));
                    taxTo = tax[i][3].trim();
                    //to display tax duration
                    dobjTax[i].setTAX_UPTO(DateUtils.parseDate(taxTo));
                    dobjTax[i].setTAX_FROM_TO(DateUtils.parseDate(taxFromTo));
                    amount = Integer.parseInt(tax[i][4].trim());
                    dobjTax[i].setAMOUNT(amount);
                    exempted = Integer.parseInt(tax[i][5].trim());
                    dobjTax[i].setEXEMPTED(exempted);
                    fine = Integer.parseInt(tax[i][6].trim());
                    dobjTax[i].setFINE(fine);
                    rebate = Integer.parseInt(tax[i][7].trim());
                    dobjTax[i].setREBATE(rebate);
                    surcharge = Integer.parseInt(tax[i][8].trim());
                    dobjTax[i].setSURCHAGE(surcharge);
                    serviceCh = Integer.parseInt(tax[i][9].trim());
                    dobjTax[i].setSERVICE_CHARGE(serviceCh);
                    totalTaxAmount = amount - exempted - rebate + surcharge + fine + serviceCh;
                    dobjTax[i].setTOT_AMT(totalTaxAmount);


                    if (taxHead.equalsIgnoreCase("User Charges")) {
                        user_charge = amount;

                    } else {
                        tax_head = tax_head + ((amount + surcharge) - (exempted + rebate));
                        tax_penalty = tax_penalty + fine;
                    }

                    if (feeCode == 58) {
                        road_taxFrmDt = taxFrom;
                        road_taxUpDt = taxTo;
                    }

                    taxFromTo = (taxFrom.length() > 0 ? taxFrom + " To " + taxTo : "");
                    if (totalTaxAmount > 0) {  //amount > 0 &&
                        //  getTaxDetails().add(new TaxDetails(i + 1, feeCode, taxHead, taxFromTo, amount, exempted, fine, rebate, surcharge, totalTaxAmount, taxFrom, taxTo));
                    }
                    grandTotalAmount = grandTotalAmount + totalTaxAmount;
                }
            }
            DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            Date today = null;
            try {
                today = df.parse(road_taxFrmDt);
            } catch (ParseException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
            Date curranDt = DateUtils.getCurrentLocalDate();
            long days = 0;
            try {
                days = DateUtils.getDate1MinusDate2_Days(curranDt, today);
                if (days > 60) {
                    Calendar dateCal = Calendar.getInstance();
                    dateCal.setTime(today);
                    dateCal.add(Calendar.DATE, -1);
                    Date tax_date = dateCal.getTime();
                    String road_tax_valid_date = df.format(tax_date);
                }
            } catch (DateUtilsException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
            taxFromTo = " To " + tax[0][7];
        }
        return dobjTax;
    }

    public static DOTaxDetails[] checkAndSplitTaxValue(String retVal) {
        DOTaxDetails[] dobjTax = null;
        if (retVal != null) {
            // ambrish disabling ecn if (checksumVerify(retVal)) {
            if (true) {
                if (retVal != null && !retVal.equalsIgnoreCase("")) {
                    if (retVal.contains(("###"))) {
                        String[] arr = retVal.split("###");
                        if (arr.length > 0) {
                            String data = arr[0];
                            if (data.startsWith("!")) {
                            } else if (data.contains(",")) {
                                dobjTax = splitTaxValues(data);
                            }
                        }
                        if (arr.length > 2) {
                            //other criteria stored here
                        }
                        if (arr.length > 1) {
                            //permit details extracted here
                            arr = arr[1].split(",");
                        }
                    } else {
                        if (retVal.startsWith("!")) {
                            // Problem in Calculating Tax.
                        } else if (retVal.contains(",")) {
                            dobjTax = splitTaxValues(retVal);
                        } else if (retVal.trim().length() == 0) {
                            // Problem in Calculating Tax. Visit Respective Registering Authority for further details...
                        } else {
                            // validationMsg
                        }
                    }



                }
            } else {
                //  Mismatch in recieved data.<br>Try again....
            }
        } else {
            // Check Vehicle Details.....
        }
        return dobjTax;
    }

    private static String chksum(String data) {
        try {
            return data.substring(0, 5) + MD5(data) + data.substring(5);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            return "";
        }
    }

    public static void main(String[] args) {
        TaxCalVB obj = new TaxCalVB();
        DOTaxDetails[] dobjTax = obj.calculateTax(new String[19]);
    }
}
