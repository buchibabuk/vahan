/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.Random;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpSession;
import nl.captcha.Captcha;
import org.primefaces.component.graphicimage.GraphicImage;

/**
 *
 * @author Ashok Kumar <send2ashok@hotmail.com>
 */
@ManagedBean(name = "captchaBean")
@RequestScoped
public class CaptchaBean implements Serializable {

    private GraphicImage captchaImage = new GraphicImage();
    java.util.Random rand = new Random();

    public CaptchaBean() {
        captchaImage.setValue("/DispplayCaptcha?bkgp_cd=3&noise_cd=2&gimp_cd=3&pfdrid_c=true?" + rand.nextInt());
    }

    public void validateCaptcha(FacesContext context, UIComponent componentToValidate, Object value) throws ValidatorException {
        try {
            HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
//        Captcha secretcaptcha = (Captcha) session.getAttribute(Captcha.NAME);
            session.setAttribute("captcha", value);
            Captcha secretcaptcha = (Captcha) session.getAttribute("serverCaptcha");
            if (secretcaptcha.isCorrect(value.toString())) {
                return;
            }

            ((HtmlInputText) componentToValidate).setSubmittedValue("");
            captchaImage.setValue("/DispplayCaptcha?bkgp_cd=3&noise_cd=2&gimp_cd=3&pfdrid_c=true?" + rand.nextInt());
            throw new ValidatorException(new FacesMessage("Captcha does not match"));
        } catch (Exception e) {
            throw new ValidatorException(new FacesMessage("Captcha does not match"));
        }
    }

    /**
     * @return the captchaImage
     */
    public GraphicImage getCaptchaImage() {
        return captchaImage;
    }

    /**
     * @param captchaImage the captchaImage to set
     */
    public void setCaptchaImage(GraphicImage captchaImage) {
        this.captchaImage = captchaImage;
    }
}
