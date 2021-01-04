/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services.clients;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.faces.context.FacesContext;

import javax.servlet.http.HttpServletRequest;
import nic.vahan.server.ServerUtil;
import nic.vahan.services.clients.dobj.ChallanResponseDobj;
import nic.vahan.services.clients.dobj.InputChallanDobj;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Anu
 */
public class ChallanClient {

    private final static String USER_AGENT = "Mozilla/5.0";
    private final static String TOKEN = "MDczRjUyNDJDQUFGRjBBOUMzMUZGQUVEOTA4QkYzOEU2RENBNEQ4OTIwMzRGQzY1NDA0QzIyMjk3RkJENkNDMg==";
    private static final Logger LOGGER = Logger.getLogger(ChallanClient.class);
    private WebResource webResource;
    private Client client;
    //private static final String BASE_URI = "https://echallan.parivahan.gov.in/api/challan-history/";//Production
    private static final String BASE_URI = ServerUtil.getVahanPgiUrl("CHALLAN_URL");
    private static String baseURL = "";

    public ChallanClient() {
        try {
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            int serverPort = request.getLocalPort();
            baseURL = BASE_URI;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }

    }

    public ChallanResponseDobj getChallanHistory(InputChallanDobj inputVar) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ChallanResponseDobj responseFromservice = new ChallanResponseDobj();
//        inputVar.setFlag("V");
//        inputVar.setSearch_field("UP53BM0877");
        try {

            String param = mapper.writeValueAsString(inputVar);
//            String url = "https://echallan.parivahan.gov.in/api/challan-history/";
            URL obj = new URL(baseURL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(15000);
            // add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Authorization", "Bearer " + TOKEN);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");
            // Send post request
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(param.getBytes("UTF-8"));
            os.flush();
            os.close();
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                responseFromservice = mapper.readValue(response.toString(), ChallanResponseDobj.class);
            } else {
                LOGGER.info("Request failed to " + baseURL + " Response code: " + responseCode);
            }
        } catch (java.net.SocketTimeoutException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        return responseFromservice;
    }
}