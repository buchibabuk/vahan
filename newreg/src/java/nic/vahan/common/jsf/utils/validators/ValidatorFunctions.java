/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.common.jsf.utils.validators;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import nic.vahan.server.CommonUtils;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "validateFunction")
@ViewScoped
public class ValidatorFunctions implements Serializable {

    private boolean validateCheckFeeTax;

    public void validatePositiveIntegerMaxSize2(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        int valueInt;
        FacesMessage msg = null;
        if (!(value instanceof Integer)) {
            msg = new FacesMessage("Not Integer Value");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
        valueInt = Integer.parseInt(value.toString().trim());

        if (valueInt > 99 || valueInt <= 0) {
            msg = new FacesMessage("Invalid Value");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void validateRegNo(FacesContext context, UIComponent comp,
            Object value) {
        String errorMessage = "";
        String regNo = (String) value;
        String pattern = "[a-zA-Z0-9]+";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(regNo);

        if (regNo.length() > 10) {
            errorMessage = "Allowed Maximum Length of Registration Number is 10";
        }

        if (regNo.length() < 4) {
            errorMessage = "Allowed Minimum Length of Registration Number is 4";
        }
        if (!m.matches()) {
            errorMessage = "Invalid Registration number,Please enter valid Registration Number";
        }
        if (CommonUtils.isNullOrBlank(regNo)) {
            errorMessage = "Registration No can't be blank.";
        }
        if (!errorMessage.isEmpty()) {
            FacesMessage msg = new FacesMessage(errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void validateDealerName(FacesContext context, UIComponent comp,
            Object value) {
        String errorMessage = "";
        String DealerName = (String) value;
        String pattern = "[a-zA-Z0-9./$&-_() ]+";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(DealerName);
        if (DealerName.length() > 50) {
            errorMessage = "Allowed Maximum Length of Dealer Name is 50.";
        }
        if (!m.matches()) {
            errorMessage = "Invalid Dealer name,Please enter this type of character (./$&-_()) for  Dealer Name.";
        }
        if (CommonUtils.isNullOrBlank(DealerName)) {
            errorMessage = "Dealer name can't be blank.";
        }
        if (!errorMessage.isEmpty()) {
            FacesMessage msg = new FacesMessage(errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void validateRegNoForDealerMaster(FacesContext context, UIComponent comp,
            Object value) {
        String errorMessage = "";
        String regNo = (String) value;
        String pattern = "[a-zA-Z0-9_/.-<>@#!&()`]+";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(regNo);
        if (regNo.length() > 20) {
            errorMessage = "Allowed Maximum Length of Registration Number is 20";
        }
        if (!m.matches()) {
            errorMessage = "Invalid Registration number,Only allowed special characters as (_/.-<>@#!&()`) for Registration Number";
        }
        if (CommonUtils.isNullOrBlank(regNo)) {
            errorMessage = "Dealer Registration no can't be blank.";
        }
        if (!errorMessage.isEmpty()) {
            FacesMessage msg = new FacesMessage(errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void validateAddress(FacesContext context, UIComponent comp,
            Object value) {
        String errorMessage = "";
        String address = (String) value;
        String pattern = "[a-zA-Z0-9,.(~!-`)[@#&$^*]/<=:;>?\"}{%|+ ]+";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(address);
        if (address.length() > 35) {
            errorMessage = "Allowed Maximum Length of Address  is 35";
        }
        if (!m.matches()) {
            errorMessage = "Invalid  Address,only allowed special characters as (,.(~!-`)[@#&$^*]/<=:;>?\"}{%|+)  for valid  Address";
        }
        if (CommonUtils.isNullOrBlank(address)) {
            errorMessage = "Address  can't be blank.";
        }
        if (!errorMessage.isEmpty()) {
            FacesMessage msg = new FacesMessage(errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }

    }

    public void validateTinNo(FacesContext context, UIComponent comp,
            Object value) {
        String errorMessage = "";
        String tinNo = (String) value;
        if (tinNo.length() > 15) {
            errorMessage = "Allowed Maximum Length of Tin Number is 15.";
        }
        if (!errorMessage.isEmpty()) {
            FacesMessage msg = new FacesMessage(errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void validateApplNo(FacesContext context, UIComponent comp,
            Object value) {
        String errorMessage = "";
        String ApplNo = (String) value;
        String pattern = "[a-zA-Z0-9]+";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(ApplNo);
        if (ApplNo.length() > 16) {
            errorMessage = "Allowed Maximum Length of Application Number is 16";
        }
        if (ApplNo.length() < 9) {
            errorMessage = "Allowed Minimum Length of Application Number is 10";
        }
        if (!m.matches()) {
            errorMessage = "Invalid Application number,Please enter valid Application Number";
        }
        if (CommonUtils.isNullOrBlank(ApplNo)) {
            errorMessage = "Appplication no can't be blank.";

        }

        if (!errorMessage.isEmpty()) {
            FacesMessage msg = new FacesMessage(errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void validateCheckBoxIsTrue(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        boolean bln = false;
        FacesMessage msg = null;
        if (!(value instanceof Boolean)) {
            msg = new FacesMessage("Not Boolean Value");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
        bln = Boolean.valueOf(value.toString().trim());
        if (!bln) {
            msg = new FacesMessage("Invalid Value");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void validateChasiNo(FacesContext context, UIComponent comp,
            Object value) {
        String errorMessage = "";
        String ChasNo = (String) value;
        String pattern = "[a-zA-Z0-9,-_.()@#&$~`*!~`]+";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(ChasNo);

        if (ChasNo.length() > 30) {
            errorMessage = "Maximum Length of Chasis Number is 30";
        }

        if (ChasNo.length() < 5) {
            errorMessage = "Minimum Length of Chasis Number is 5";
        }
        if (!m.matches()) {
            errorMessage = "Invalid Chesis number,Only allowed special characters as (a-zA-Z0-9,-_.()@#&$~`*!~`) for Chesis Number";
        }
        if (!errorMessage.isEmpty()) {
            FacesMessage msg = new FacesMessage(errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void validateRecptNo(FacesContext context, UIComponent comp,
            Object value) {
        String errorMessage = "";
        String RecpNo = (String) value;
        String pattern = "[a-zA-Z0-9]+";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(RecpNo);

        if (RecpNo.length() > 16) {
            errorMessage = "Allowed Maximum Length of Receipt Number is 16";
        }
        if (!m.matches()) {
            errorMessage = "Invalid Receipt number, please enter valid Receipt Number";
        }
        if (!errorMessage.isEmpty()) {
            FacesMessage msg = new FacesMessage(errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void validateLast5ChasiNo(FacesContext context, UIComponent comp,
            Object value) {
        String errorMessage = "";
        String ChasNo = (String) value;
        String pattern = "[a-zA-Z0-9]+";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(ChasNo);

        if (ChasNo.length() > 5) {
            errorMessage = "Maximum Length of Chasis Number is 5";
        }
        if (!m.matches()) {
            errorMessage = "Invalid Chasis number entered";
        }
        if (!errorMessage.isEmpty()) {
            FacesMessage msg = new FacesMessage(errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void validateLast5EngNo(FacesContext context, UIComponent comp,
            Object value) {
        String errorMessage = "";
        String EngNo = (String) value;
        String pattern = "[a-zA-Z0-9]+";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(EngNo);

        if (EngNo.length() > 5) {
            errorMessage = "Maximum Length of Engine Number is 5";
        }
        if (!m.matches()) {
            errorMessage = "Invalid Engine number entered";
        }
        if (!errorMessage.isEmpty()) {
            FacesMessage msg = new FacesMessage(errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void validateEngNo(FacesContext context, UIComponent comp,
            Object value) {
        String errorMessage = "";
        String ChasNo = (String) value;
        String pattern = "[a-zA-Z0-9,-_.()@#&$~`*!~`+]+";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(ChasNo);

        if (ChasNo.length() > 30) {
            errorMessage = "Maximum Length of Engine Number is 30";
        }
        if (!m.matches()) {
            errorMessage = "Invalid Engine number entered";
        }
        if (!errorMessage.isEmpty()) {
            FacesMessage msg = new FacesMessage(errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void vaidateOrdinarySeat(FacesContext context, UIComponent comp,
            Object value) {
        int odinarySeat = 2;
        String errorMessage = "";
        String OrdinarySeat = Integer.toString(odinarySeat);
        if (OrdinarySeat.length() > 2) {
            errorMessage = "Allowed Maximum Length of Ordinary Seat is 2";
        }
        if (!errorMessage.isEmpty()) {
            FacesMessage msg = new FacesMessage(errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void vaidatePushBackSeat(FacesContext context, UIComponent comp,
            Object value) {
        String errorMessage = "";
        String PushBackSeat = (String) value;
        String pattern = "[0-9]+";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(PushBackSeat);

        if (PushBackSeat.length() > 2) {
            errorMessage = "Allowed Maximum Length of PushBackSeat Seat is 2";
        }
        if (!errorMessage.isEmpty()) {
            FacesMessage msg = new FacesMessage(errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void validateMobileNo(FacesContext context, UIComponent comp,
            Object value) {
        long MobileNo = 9l;
        String errorMessage = "";
        String mobileNo = Long.toString(MobileNo);
        if (mobileNo.length() > 10) {
            errorMessage = "Allowed Maximum Length of Mobile Number is 10";
        }
        if (!errorMessage.isEmpty()) {
            FacesMessage msg = new FacesMessage(errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void vaidateBodyType(FacesContext context, UIComponent comp,
            Object value) {
        String errorMessage = "";
        String BodyType = (String) value;
        String pattern = "[a-zA-Z0-9,-.()@#&$~`+'!` ]+";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(BodyType);

        if (BodyType.length() > 30) {
            errorMessage = "Allowed Maximum Length of Body type  is 30";
        }
        if (!m.matches()) {
            errorMessage = "Invalid  Body type,only allowed special characters as (a-zA-Z0-9,-_.()@#&$~`+'!`)  for valid  Body type.";
        }
        if (!errorMessage.isEmpty()) {
            FacesMessage msg = new FacesMessage(errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void validateSaleAmount(FacesContext context, UIComponent comp,
            Object value) {
        String errorMessage = "";
        String saleAmount = (String) value.toString();
        String pattern = "[0-9]+";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(saleAmount);
        if (saleAmount.length() > 0) {
            if (saleAmount.trim().length() > 9) {
                errorMessage = "Allowed Maximum Length of Sales amount  is 9.";
            }
            if (saleAmount.trim().length() < 5) {
                errorMessage = "Allowed Minimum Length of Sales amout is 5.";
            }
            if (!m.matches()) {
                errorMessage = "Invalid Sales amout,Please enter validSales amout";
            }

            if (!errorMessage.isEmpty()) {
                FacesMessage msg = new FacesMessage(errorMessage);
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
        }
    }

    public void validateFtName(FacesContext context, UIComponent comp,
            Object value) {
        String errorMessage = "";
        String FirstName = (String) value;
        String pattern = "[a-zA-Z0-9,-_.()@#&$~`+/<>:;'+=^! ]+";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(FirstName);

        if (FirstName.length() > 35) {
            errorMessage = "Allowed Maximum Length of FirstName  is 35";
        }
        if (!m.matches()) {
            errorMessage = "Invalid  Name,only allowed special characters as (,-_.()@#&$~`+/<>:;'+=^!)  for father/husband Name";
        }
        if (!errorMessage.isEmpty()) {
            FacesMessage msg = new FacesMessage(errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void validateOwnerName(FacesContext context, UIComponent comp,
            Object value) {
        String errorMessage = "";
        String OwnerName = (String) value;
        String pattern = "[a-zA-Z0-9,-_.()@#&$~`+/<>:;'+=^! ]+";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(OwnerName);

        if (OwnerName.length() > 35) {
            errorMessage = "Allowed Maximum Length of OwnerName  is 35";
        }
        if (!m.matches()) {
            errorMessage = "Invalid  Owner Name,only allowed special characters as (,-_.()@#&$~`+/<>:;'+=^!)  for valid  Owner Name";
        }
        if (!errorMessage.isEmpty()) {
            FacesMessage msg = new FacesMessage(errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void validateOtp(FacesContext context, UIComponent comp,
            Object value) {
        String errorMessage = "";
        String Otp = (String) value;
        String pattern = "[0-9]+";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(Otp);

        if (Otp.length() > 4) {
            errorMessage = "Maximum Length of OTP Number is 4";
        }
        if (Otp.length() < 4) {
            errorMessage = "Allowed Minimum Length of  Otp  Number is 4";
        }
        if (!m.matches()) {
            errorMessage = "Invalid Otp number entered";
        }
        if (!errorMessage.isEmpty()) {
            FacesMessage msg = new FacesMessage(errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void validatePolicyNo(FacesContext context, UIComponent comp,
            Object value) {
        String errorMessage = "";
        String PolicyNo = (String) value;
        String pattern = "[a-zA-Z0-9,-_.()*/`!@#$^(=?<>)[:;/]+ ]+";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(PolicyNo);

        if (PolicyNo.length() > 35) {
            errorMessage = "Maximum Length of Policy Number is 35";
        }
        if (!m.matches()) {
            errorMessage = "Invalid Policy number,Only allowed special characters as (~`!@$^&*()-+:;<>?/,.[] ) for Policy Number";
        }
        if (!errorMessage.isEmpty()) {
            FacesMessage msg = new FacesMessage(errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void validateColor(FacesContext context, UIComponent comp,
            Object value) {
        String errorMessage = "";
        String color = (String) value;
        String pattern = "[a-zA-Z0-9,-_.()*/`~!@#$^(=?<>)[:;/]+ ]+";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(color);

        if (color.length() > 20) {
            errorMessage = "Maximum Length of Color is 20";
        }
        if (!m.matches()) {
            errorMessage = "Invalid color,Only allowed special characters as (~`~!@$^&*()-+:;<>?/,.[] ) for Color";
        }
        if (!errorMessage.isEmpty()) {
            FacesMessage msg = new FacesMessage(errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void validateIDV(FacesContext context, UIComponent comp,
            Object value) {
        long idv = 10l;
        String errorMessage = "";
        String IDV = Long.toString(idv);
        if (IDV.length() > 0) {
            if (IDV.trim().length() > 9) {
                errorMessage = "Allowed Maximum Length of IDV  is 9.";
            }
            if (!errorMessage.isEmpty()) {
                FacesMessage msg = new FacesMessage(errorMessage);
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
        }
    }

    public void validateMakerModel(FacesContext context, UIComponent comp,
            Object value) {
        String errorMessage = "";
        String MakerModel = (String) value;
        String pattern = "[a-zA-Z0-9,`~/[=.-]+ ]+";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher m = r.matcher(MakerModel);

        if (MakerModel.length() > 30) {
            errorMessage = "Maximum Length of Maker Model is 30";
        }

        if (MakerModel.length() < 5) {
            errorMessage = "Minimum Length of Maker Model is 5";
        }
        if (!m.matches()) {
            errorMessage = "Invalid Maker Model,Only allowed special characters as (,`~/[=.-]+) for Maker Model.";
        }
        if (!errorMessage.isEmpty()) {
            FacesMessage msg = new FacesMessage(errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void validateSeatCap(FacesContext context, UIComponent comp,
            Object value) {
        int seatCap = 3;
        String errorMessage = "";
        String SeatCap = Integer.toString(seatCap);
        if (SeatCap.length() > 0) {
            if (SeatCap.trim().length() > 3) {
                errorMessage = "Allowed Maximum Length of Seat Cap  is 3.";
            }
            if (!errorMessage.isEmpty()) {
                FacesMessage msg = new FacesMessage(errorMessage);
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
        }
    }

    public void validateNoCyl(FacesContext context, UIComponent comp,
            Object value) {
        int noCyl = 2;
        String errorMessage = "";
        String NoCyl = Integer.toString(noCyl);
        if (NoCyl.length() > 0) {
            if (NoCyl.trim().length() > 2) {
                errorMessage = "Allowed Maximum Length of No of Cylinder is 2.";
            }
            if (!errorMessage.isEmpty()) {
                FacesMessage msg = new FacesMessage(errorMessage);
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
        }
    }

    public void ValidateUlWt(FacesContext context, UIComponent comp,
            Object value) {
        int ulWt = 6;
        String errorMessage = "";
        String UlWt = Integer.toString(ulWt);
        if (UlWt.length() > 0) {
            if (UlWt.trim().length() > 6) {
                errorMessage = "Allowed Maximum Length of Ul weight is 6.";
            }
            if (!errorMessage.isEmpty()) {
                FacesMessage msg = new FacesMessage(errorMessage);
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
        }
    }

    public void validatePushBackSeat(FacesContext context, UIComponent comp,
            Object value) {
        int pushBackSeat = 6;
        String errorMessage = "";
        String PushBackSeat = Integer.toString(pushBackSeat);
        if (PushBackSeat.length() > 0) {
            if (PushBackSeat.trim().length() > 2) {
                errorMessage = "Allowed Maximum Length of Push Back seat is 2.";
            }
            if (!errorMessage.isEmpty()) {
                FacesMessage msg = new FacesMessage(errorMessage);
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
        }
    }

    public void validateTempRegnNo(FacesContext context, UIComponent comp,
            Object value) {
        String errorMessage = "";
        String regNo = (String) value;
        String pattern = "[a-zA-Z0-9-/]+";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(regNo);

        if (regNo.length() > 20) {
            errorMessage = "Allowed Maximum Length of Temprory Registration Number is 20.";
        }
        if (!m.matches()) {
            errorMessage = "Invalid Registration number,Only allowed special characters as (_-) for Temorory Registration Number.";
        }

        if (!errorMessage.isEmpty()) {
            FacesMessage msg = new FacesMessage(errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void validateFinancerName(FacesContext context, UIComponent comp,
            Object value) {
        String errorMessage = "";
        String FinancerName = (String) value;
        String pattern = "[a-zA-Z0-9,.(~!-`)[@#&$^*]/<=:;>?\"{}%|+ ]+";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(FinancerName);

        if (FinancerName.length() > 35) {
            errorMessage = "Allowed Maximum Length of Financer Name is 35.";
        }
        if (!m.matches()) {
            errorMessage = "Invalid Financer name,Only allowed special characters as (,.(~!-`)[@#&$^*]/<=:;>?\"}{%|+) for  Financer Name.";
        }

        if (!errorMessage.isEmpty()) {
            FacesMessage msg = new FacesMessage(errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void validateNocNo(FacesContext context, UIComponent comp,
            Object value) {
        String errorMessage = "";
        String NocNo = (String) value;
        String pattern = "[a-zA-Z0-9,`/()-=[~!.#*]?{},\";|+ ]+";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(NocNo);
        if (NocNo.length() > 30) {
            errorMessage = "Allowed Maximum Length of Noc Number is 30.";
        }
        if (!m.matches()) {
            errorMessage = "Invalid Noc Number,Only allowed special characters as (,`/~!.#*()-=[]{},\";?|+) for  Noc Number.";
        }

        if (!errorMessage.isEmpty()) {
            FacesMessage msg = new FacesMessage(errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void validateAmount(FacesContext context, UIComponent comp,
            Object value) {
        long amount = 10l;
        String errorMessage = "";
        String Amount = Long.toString(amount);
        if (Amount.length() > 0) {
            if (Amount.trim().length() > 10) {
                errorMessage = "Allowed Maximum Length of Amount  is 10.";
            }
            if (!errorMessage.isEmpty()) {
                FacesMessage msg = new FacesMessage(errorMessage);
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
        }
    }

    public void validateReason(FacesContext context, UIComponent comp,
            Object value) {
        String errorMessage = "";
        String Reason = (String) value;
        String pattern = "[/[A-Z_\\u0020]/i ]+";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(Reason);

        if (Reason.length() > 100) {
            errorMessage = "Allowed Maximum Length of Reason  is 100";
        }
        if (!m.matches()) {
            errorMessage = "Invalid Reason ,Only allowed  characters as (/[A-Z_\\u0020]/i)  for Reason";
        }
        if (!errorMessage.isEmpty()) {
            FacesMessage msg = new FacesMessage(errorMessage);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    /**
     * @return the validateCheckFeeTax
     */
    public boolean isValidateCheckFeeTax() {
        return validateCheckFeeTax;
    }

    /**
     * @param validateCheckFeeTax the validateCheckFeeTax to set
     */
    public void setValidateCheckFeeTax(boolean validateCheckFeeTax) {
        this.validateCheckFeeTax = validateCheckFeeTax;
    }
}
