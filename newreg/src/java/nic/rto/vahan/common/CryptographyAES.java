/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.rto.vahan.common;

import Encryption.Encrypt;
import java.util.HashMap;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import nic.vahan.db.TableConstants;

/**
 *
 * @author user
 */
public class CryptographyAES {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(CryptographyAES.class);

    public CryptographyAES() {
    }

    public String getEncriptedString(String encStr) {
        String rtnString = null;
        FacesContext fcontext = FacesContext.getCurrentInstance();
        ServletContext scontext = (ServletContext) fcontext.getExternalContext().getContext();
        String propPath = scontext.getRealPath("/sbi/log4j.properties");
        String keyPath = scontext.getRealPath("/sbi/" + TableConstants.VAHAN_PGI_KEY + ".key");
        Encrypt encrpt = new Encrypt();

        encrpt.setLog4Path(propPath);
        encrpt.setSecretkey(keyPath);
        //Encrypts the string
        rtnString = encrpt.encryptFile(encStr);
        return checkString(rtnString);
    }

    /**
     * This method decript an encripted string
     *
     * @return decripted string(rtnString)
     */
    public String getDecriptedString(String dcrStr) {
        String rtnString = null;
        FacesContext fcontext = FacesContext.getCurrentInstance();
        ServletContext scontext = (ServletContext) fcontext.getExternalContext().getContext();
        String propPath = scontext.getRealPath("/sbi/log4j.properties");
        String keyPath = scontext.getRealPath("/sbi/" + TableConstants.VAHAN_PGI_KEY + ".key");
        Encrypt encrpt = new Encrypt();
        encrpt.setLog4Path(propPath);
        encrpt.setSecretkey(keyPath);
        //Decrypts the String
        rtnString = encrpt.decryptFile(dcrStr);
        return rtnString;
    }

    public String checkString(String str) {
        String rtnStr = "";
        StringBuffer sb = new StringBuffer();
        char[] chararr = str.toCharArray();
        int ascci = 0;
        for (int i = 0; i < chararr.length; i++) {
            ascci = chararr[i];
            //  DisplayMessage.show("char:" + chararr[i] + "  code:" + ascci);
            if (ascci != 10) {
                // DisplayMessage.show("Char:" + chararr[i] + "  index:" + i);
                sb.append(chararr[i]);
            }
            rtnStr = sb.toString();
        }
        return rtnStr;
    }

    public HashMap getReturnParameters(String encParamaters) throws VahanException {
        HashMap hm = new HashMap();
        try {
            //checks if encParameters is not null and empty
            if (encParamaters == null || encParamaters.equalsIgnoreCase("")) {
                throw new VahanException("Unable to get the response from the Bank.Please Try Again");
            } else if (encParamaters != null && !encParamaters.equalsIgnoreCase("")) {
                //     SbiEncription sbie = new SbiEncription();
                //calls getDecriptedString method which returns the decripted string
                String dcrpData = this.getDecriptedString(encParamaters);
                //calls separateRuleNos method which separates string with delemeter
                if (dcrpData != null) {
                    //System.out.println("dcrpData : " + dcrpData);
                    String[] data = separateRuleNos(dcrpData, '|');
                    String firstString = "";
                    String secondString = "";
                    //checks if data not equal to null
                    if (data != null && !data[0].equals("")) {
                        for (int i = 0; i < data.length; i++) {
                            //returns new string from zeroth index to EqualsTo index and stores in firstString of String type
                            firstString = data[i].substring(0, data[i].indexOf("="));
                            //returns new string from next index of EqualsTo and end of the string and stores in secondString of String type
                            secondString = data[i].substring(data[i].indexOf("=") + 1, data[i].length());
                            hm.put(firstString, secondString);
                        }
                    } else {
                        throw new VahanException("Due to some reason receipt could not generated. Please verify again.");
                    }
                } else {
                    throw new VahanException("Something went wrong. Please verify again.");
                }
            }
        } catch (Exception e) {
            throw new VahanException("Something went wrong. Please verify again.");
        }
        return hm;
    }

    private String[] separateRuleNos(String ruleStr, char sep) {
        String[] rule = null;
        int i = 1;
        int index = 0;
        //calls findNoRules method which count number of separators in string and stores in noOfComma of int type
        int noOfComma = findNoRules(ruleStr, String.valueOf(sep));
        if (noOfComma > 0) {
            rule = new String[noOfComma];
        }
        while (i > 0) {
            i = ruleStr.indexOf(sep);

            if (i > 0) {
                rule[index] = ruleStr.substring(0, i);
            } else {
                rule[index] = ruleStr;
            }
            ruleStr = ruleStr.substring(i + 1, ruleStr.length());
            index++;
        }
        return rule;
    }

    private int findNoRules(String ruleStr, String sep) {
        int noOfRule = 0;
        int noOfComma = 0;
        String str = "";
        for (int p = 0; p < ruleStr.length(); p++) {
            str = ruleStr.charAt(p) + "";
            if (str.equalsIgnoreCase(sep)) {
                noOfComma++;
            }
        }
        noOfRule = noOfComma + 1;
        return noOfRule;
    }
}
