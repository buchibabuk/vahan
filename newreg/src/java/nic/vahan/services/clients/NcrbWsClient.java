/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services.clients;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author acer
 */
public class NcrbWsClient {

    private static final Logger LOGGER = Logger.getLogger(NcrbWsClient.class);
    private WebResource webResource;
    private Client client;
    private static final String BASE_URI = "http://127.0.0.1:90/ncrbWS/webresources/ncrbwebservice/checkVehicleStatus";//Production
    // private static final String BASE_URI = "http://10.248.93.47/vahanPucc/webresources";//Staging
    //private static final String BASE_URI = "http://localhost:{port}/ncrbWS/webresources/ncrbwebservice/checkVehicleStatus"; //localuse   
    //http://10.25.85.184:8085/vahanInsuranceDataWS/dataportws?wsdl
    private static String baseURL = "";

    public NcrbWsClient() {
        try {
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            int serverPort = request.getLocalPort();
            baseURL = BASE_URI.replace("{port}", serverPort + "");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }

    }

    public String callNcrbService(String regNo) {
        String status = "";
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            int timeout = 5; // seconds
            HttpParams httpParams = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, timeout * 1000); // http.connection.timeout
            HttpConnectionParams.setSoTimeout(httpParams, timeout * 1000); // http.socket.timeout
            HttpPost postRequest = new HttpPost(baseURL);
            postRequest.addHeader("content-type", "text/plain");
            StringEntity input = new StringEntity(regNo);
            postRequest.setEntity(input);
            HttpResponse httpresponse = httpClient.execute(postRequest);
            int statusCode = httpresponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                HttpEntity httpEntity = httpresponse.getEntity();
                status = EntityUtils.toString(httpEntity);
            }

        } catch (java.net.SocketTimeoutException ex) {
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }

        return status;
    }
}
