/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services.clients;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import nic.rto.vahan.common.VahanException;
import org.apache.log4j.Logger;

/**
 *
 * @author Komal
 */
public class DocumentUploadClient {

    private static final Logger LOGGER = Logger.getLogger(DocumentUploadClient.class);
    private WebResource webResource;
    private Client client;
    private static String baseURL = "";

    public DocumentUploadClient() {
        try {
            com.sun.jersey.api.client.config.ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
            client = Client.create(config);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public String getDocumentListToBeUpload(String url) throws VahanException {
        try {
            baseURL = url;
            client.setConnectTimeout(3000);
            client.setReadTimeout(45000);
            webResource = client.resource(baseURL);
            ClientResponse response = webResource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(ClientResponse.class);
            if (response.getStatus() != 200) {
                throw new VahanException("DMS URL not responding. Please try after sometime");
            }
            String output = response.getEntity(String.class);
            return output;
        } catch (ClientHandlerException | UniformInterfaceException sex) {
            LOGGER.error(sex.toString() + " " + sex.getStackTrace()[0]);
            throw new VahanException("DMS URL not responding. Please try after sometime");
        }
    }

    public <T> T uploadDoc(Class<T> responseType, Object requestEntity, String url) throws VahanException {
        try {
            baseURL = url;
            client.setConnectTimeout(3000);
            client.setReadTimeout(45000);
            webResource = client.resource(baseURL);
            return webResource.type(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(responseType, requestEntity);
        } catch (ClientHandlerException | UniformInterfaceException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("DMS URL not responding. Please try after sometime");
        }
    }
//    public String callDscClient(String url) throws UniformInterfaceException, VahanException {
//        baseURL = url;
//        webResource = client.resource(baseURL);
//        ClientResponse response = webResource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(ClientResponse.class);
//        if (response.getStatus() != 200) {
//            throw new VahanException("DSC URL not responding. Please try after sometime");
//        }
//        String output = response.getEntity(String.class);
//        return output;
//    }
}
