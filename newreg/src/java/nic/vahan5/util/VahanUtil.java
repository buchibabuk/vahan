/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kartikey Singh
 */
@Service
@PropertySource("classpath:resources/application.properties")
public class VahanUtil {

    @Value("${homologation.url}")
    private String homologationUrl;

    @PostConstruct
    public void init() {
    }

    public static String getBaseUrl() {
        HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String baseUrl = "";
        InetAddress localhost;
        try {
            localhost = InetAddress.getLocalHost();
            if (origRequest.getProtocol().equals("HTTPS/1.1")) {
                baseUrl = "https://";
            } else {
                baseUrl = "http://";
            }
//            baseUrl = baseUrl + req.getLocalName() + ":" + req.getLocalPort() + "/vahan5";
            baseUrl = baseUrl + (localhost.getHostAddress()).trim() + ":" + origRequest.getLocalPort() + origRequest.getContextPath();
        } catch (UnknownHostException ex) {
            Logger.getLogger(VahanUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return baseUrl;
    }

    public String getHomologationUrl() {
        if (homologationUrl != null) {
            return homologationUrl;
        } else {
            homologationUrl = setPropUrl("homologation.url");
            return homologationUrl;
        }
    }

    public String setURL(String property) {
        String url = null;
        FileReader reader = null;
        try {
            reader = new FileReader("/resources/application.properties");
            Properties p = new Properties();
            p.load(reader);
            url = p.getProperty(property);
            //   System.out.println("HOMOLOGATION VALUE FROM PROPERTIES FILE " + homologationUrl);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VahanUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VahanUtil.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Logger.getLogger(VahanUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
//            homologationUrl = "http://10.249.33.78/Homologation/homologation";
        return url;
    }

    public String setPropUrl(String property) {
        String url=null;
        try {
            Properties configProperties = new Properties();
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("resources/application.properties");
            configProperties.load(inputStream);
            url = configProperties.getProperty(property);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VahanUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VahanUtil.class.getName()).log(Level.SEVERE, null, ex);
        } 

        return url;

    }
}
