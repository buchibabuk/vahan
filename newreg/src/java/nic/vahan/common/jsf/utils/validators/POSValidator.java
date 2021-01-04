/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.common.jsf.utils.validators;

import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Aftab Khan 12-07-2016 For Validation
 */
public class POSValidator {

    private static final Logger LOGGER = Logger.getLogger(POSValidator.class);

//N = Only Numeric;
//A = Only Alphabets;
//AN = Only Alpha Numeric;
//AS = Only Alpha Space
//ANS = Only Aplha Numeric with Space;
//ADD = address validator;
// E= EmailValidator
// ANWS = Alpha Numeric with Special Char like Address
    public static boolean validate(String data, String type) throws VahanException {
        boolean validatFlag = false;
        try {

            if (type.equalsIgnoreCase("A")) {
                validatFlag = JSFUtils.isAlphabet(data);
            } else if (type.equalsIgnoreCase("N")) {
                validatFlag = JSFUtils.isNumeric(data);
            } else if (type.equalsIgnoreCase("F")) {
                validatFlag = JSFUtils.isFloat(data);
            } else if (type.equalsIgnoreCase("AN")) {
                validatFlag = JSFUtils.isAlphaNumeric(data);
            } else if (type.equalsIgnoreCase("AS")) {
                validatFlag = JSFUtils.isAlphabetWithSpace(data);
            } else if (type.equalsIgnoreCase("ANS")) {
                validatFlag = JSFUtils.isAlphaNumericWithSpace(data);
            } else if (type.equalsIgnoreCase("ADD")) {
                validatFlag = JSFUtils.isAddressValid(data);
            } else if (type.equalsIgnoreCase("DATE")) {
                validatFlag = JSFUtils.isDateValid(data);
            } else if (type.equalsIgnoreCase("DATETIME")) {
                validatFlag = JSFUtils.isDateTimeValid(data);
            } else if (type.equalsIgnoreCase("URL")) {
                validatFlag = JSFUtils.isUrlValid(data);
            } else if (type.equalsIgnoreCase("URLVS")) {
                validatFlag = JSFUtils.isUrlValidAsPerVahanService(data);
            } else if (type.equalsIgnoreCase("ENCDATA")) {
                validatFlag = JSFUtils.isEncdataValid(data);
            } else if (type.equalsIgnoreCase("IP")) {
                validatFlag = JSFUtils.isIPValid(data);
            } else if (type.equalsIgnoreCase("FNCR_NAME")) {
                validatFlag = JSFUtils.isFncrNameValid(data);
            } else if (type.equalsIgnoreCase("DBLENCDATA")) {
                validatFlag = JSFUtils.isDblVarificationEncdataValid(data);
            } else if (type.equalsIgnoreCase("USERNAME")) {
                validatFlag = JSFUtils.isUserNameValid(data);
            } else if (type.equalsIgnoreCase("ANWS")) {
                validatFlag = JSFUtils.isAlphaNumWithSpecialChar(data);
            } else if (type.equalsIgnoreCase("EMAIL")) {
                validatFlag = JSFUtils.isEmailValid(data);
            } else if (type.equalsIgnoreCase("MOBILE")) {
                validatFlag = JSFUtils.checkMobileNumber(data);
            } else if (type.equalsIgnoreCase("DESP")) {
                validatFlag = JSFUtils.isDespStr(data);
            }

            if (!validatFlag) {
                throw new POSValidationException("POSValidationException ; Column=:" + data + "could not be validated against " + type);
            }

        } catch (POSValidationException ex) {
            LOGGER.error("Inside POSValidationException catch block==" + ex);
            throw new VahanException(ex.getMessage());
        }
        return validatFlag;
    }
}
