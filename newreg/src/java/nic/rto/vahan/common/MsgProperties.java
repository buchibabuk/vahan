/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.rto.vahan.common;

/**
 *
 * @author tranC111
 */
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author sridhar
 */
public class MsgProperties {

    static ResourceBundle rb = ResourceBundle.getBundle("myapp", Locale.getDefault());

    public static String getKeyValue(String key) {
        String tempKey = rb.getString(key);
        return ((tempKey == null) ? "" : tempKey);
    }
}