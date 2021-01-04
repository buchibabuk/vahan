package dao;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Ashok Kumar <send2ashok@hotmail.com>
 */
public class ConfigUtil {

    private static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ConfigUtil.class);
    public static String reportTemplateUrl = "Config.properties";
    public static String tax_url;
    // Added by Prashant : For Tax Service Wsdl Url
    public static String tax_service_wsdl_url = "";

    static {
        Properties prop = new Properties();
        InputStream input = null;


        try {

            input = Thread.currentThread().getContextClassLoader().getResourceAsStream(reportTemplateUrl);

            // load a properties file
            prop.load(input);

            tax_url = prop.getProperty("tax_url");
        } catch (IOException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }


    }
}
