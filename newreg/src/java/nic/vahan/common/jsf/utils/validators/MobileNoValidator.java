/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.common.jsf.utils.validators;

import java.util.HashSet;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("mobileNoValidator")
public class MobileNoValidator implements Validator {

    private final static int MOBILE_NO_SIZE = 10;
    private Set invalidMobNo = new HashSet();

    public MobileNoValidator() {
        //set of invalid mobile no which usually user input into text field
        invalidMobNo.add("1111111111");
        invalidMobNo.add("2222222222");
        invalidMobNo.add("3333333333");
        invalidMobNo.add("4444444444");
        invalidMobNo.add("5555555555");
        invalidMobNo.add("6666666666");
        invalidMobNo.add("7777777777");
        invalidMobNo.add("8888888888");
        invalidMobNo.add("9999999999");
        invalidMobNo.add("1234567890");
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        long mobileNo;
        if (value != null) {
            mobileNo = Long.parseLong(value.toString().trim());
            if (value.toString().trim().length() != MOBILE_NO_SIZE || mobileNo <= 999999999 || invalidMobNo.contains(value.toString().trim()) || mobileNo <= 5999999999l) {
                FacesMessage msg = new FacesMessage("Invalid Mobile No,Please Update Correct Mobile No", "Invalid Mobile No,Please Update Correct Mobile No");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
        }
    }
}
