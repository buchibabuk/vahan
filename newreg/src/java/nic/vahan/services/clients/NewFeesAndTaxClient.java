/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services.clients;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 *
 * @author nicsi
 */
public class NewFeesAndTaxClient {

    private static final Logger LOGGER = Logger.getLogger(NewFeesAndTaxClient.class);
    private WebResource webResource;
    private Client client;
    // private static final String BASE_URI = "http://localhost:8084/vahantaxws/webresources/";
//    private static final String BASE_URI = "http://127.0.0.1:8080/vahantaxws/webresources/";
//     private static final String BASE_URI = "http://10.248.93.47:88/vahantaxws/webresources";
//    private static final String BASE_URI = "http://164.100.78.110/vahantaxws/webresources";
    private static final String BASE_URI = "http://localhost:8084/vahantaxws/webresources/";//Production
    private static String baseURL = "";

    public NewFeesAndTaxClient() {
        try {
            // HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            //  int serverPort = request.getLocalPort();
            //  baseURL = BASE_URI.replace("{port}", serverPort + "");
            baseURL = BASE_URI;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        com.sun.jersey.api.client.config.ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
        client = Client.create(config);
        // commented
        //webResource = client.resource(BASE_URI).path("fees");
        //added---------------------
        webResource = client.resource(baseURL).path("fees/newFeesTaxForNewReg");
        //   webResource = client.resource(BASE_URI).path("fees");
        //----------------
    }

    public NewFeesAndTaxClient(int thread, String baseURLService) {
        try {
            // HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            //  int serverPort = request.getLocalPort();
            //  baseURL = BASE_URI.replace("{port}", serverPort + "");
            baseURL = baseURLService;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        com.sun.jersey.api.client.config.ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
        client = Client.create(config);
        // commented
        //webResource = client.resource(BASE_URI).path("fees");
        //added---------------------
        webResource = client.resource(baseURL).path("fees/newFeesTaxForNewReg");
        //   webResource = client.resource(BASE_URI).path("fees");
        //----------------
    }

    public NewFeesAndTaxClient(String atApproval) {
        try {
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            int serverPort = request.getLocalPort();
            baseURL = BASE_URI.replace("{port}", serverPort + "");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        com.sun.jersey.api.client.config.ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
        client = Client.create(config);
        // commented
        //webResource = client.resource(BASE_URI).path("fees");
        //added---------------------
        webResource = client.resource(baseURL).path("fees/newFeesRecalculation");
    }

    public NewFeesAndTaxClient(String atApproval, String baseURLService) {
        try {
            baseURL = baseURLService;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        com.sun.jersey.api.client.config.ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
        client = Client.create(config);
        // commented
        //webResource = client.resource(BASE_URI).path("fees");
        //added---------------------
        webResource = client.resource(baseURL).path("fees/newFeesRecalculation");
    }

    public <T> T newFeesTaxCalculation_JSON_STRING(Class<T> responseType, Object requestEntity) throws UniformInterfaceException {
        //webResource.path("test");
        return webResource.type(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(responseType, requestEntity);
    }

    public <T> T newFeesRecalculation_JSON_STRING(Class<T> responseType, Object requestEntity) throws UniformInterfaceException {
        //webResource.path("test");
        return webResource.type(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(responseType, requestEntity);
    }
}
