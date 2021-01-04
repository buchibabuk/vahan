/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;

/**
 *
 * @author Ashok Kumar <send2ashok@hotmail.com>
 */
@ManagedBean
@SessionScoped
public class ThemeSwitcher_bean implements Serializable {

//    Commented By Naveen Kumar  10-Nov-2014
//    private String theme = "vahan-theme";
    
    private String theme = "parivahan";
    private Map<String, String> themes;

    @PostConstruct
    public void init() {
        themes = new TreeMap<String, String>();
        getThemes().put("Redmond", "redmond");
        getThemes().put("South-Street", "south-street");
    }

    public void themeSwitcherValueChangeListener(ValueChangeEvent event) {
        theme = (String) event.getOldValue();
    }

    /**
     * @return the theme
     */
    public String getTheme() {
        return theme;
    }

    /**
     * @param theme the theme to set
     */
    public void setTheme(String theme) {
        this.theme = theme;
    }

    /**
     * @return the themes
     */
    public Map<String, String> getThemes() {
        return themes;
    }
}
