/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.language;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import org.codehaus.groovy.util.ListHashMap;

/**
 *
 * @author DELL
 */
@ManagedBean(name = "languageBean")
//@ViewScoped
@SessionScoped
public class LocaleSwitcher implements Serializable {

    private static final long serialVersionUID = 1L;
    private Locale locale = null;
    private Map<String, String> languageList;
    private String langName = "en";

    @PostConstruct
    public void init() {
        if (locale == null) {
            langName = "en";
            locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
            languageList = new LinkedHashMap<>();
            languageList.put("English", "en"); //label, value
            languageList.put("हिंदी", "hi");
            languageList.put("தமிழ்", "tm");
        }
    }

    public Locale getLocale() {
        return locale;
    }

    public String getLanguage() {
        return locale.getLanguage();
    }

    public void changeLanguage(ValueChangeEvent vce) {
        String language = vce.getNewValue().toString();
        setLangName(language);
        locale = new Locale(language);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale(language));
    }

    public void changeLanguage(String language) {
//        String language = vce.getNewValue().toString();
        setLangName(language);
        locale = new Locale(language);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale(language));
    }

    public Map<String, String> getLanguageList() {
        return languageList;
    }

    public void setLanguageList(Map<String, String> languageList) {
        this.languageList = languageList;
    }

    public String getLangName() {
        return langName;
    }

    public void setLangName(String langName) {
        this.langName = langName;
    }
}
