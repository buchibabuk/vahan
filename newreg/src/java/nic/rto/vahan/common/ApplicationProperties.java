/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.rto.vahan.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author kaptan singh
 */
public class ApplicationProperties {

    private static final String APPLICATION_CONFIG_FILE = "application.properties";
    private static ApplicationProperties instance;
    private String image_path;

    /**
     * @param aInstance the instance to set
     */
    public static void setInstance(ApplicationProperties aInstance) {
        instance = aInstance;
    }

    public ApplicationProperties() {

        InputStream ins = ApplicationProperties.class.getResourceAsStream(APPLICATION_CONFIG_FILE);
        Properties properties = new Properties();
        try {
            properties.load(ins);
            image_path = properties.getProperty("image_path");
            ins.close();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public static ApplicationProperties getInstance() {
        if (instance == null) {
            instance = new ApplicationProperties();
        }
        return instance;
    }

    /**
     * @return the image_path
     */
    public String getImage_path() {
        return image_path;
    }

    /**
     * @param image_path the image_path to set
     */
    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }
}
