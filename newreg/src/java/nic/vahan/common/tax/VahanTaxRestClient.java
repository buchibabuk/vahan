/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.common.tax;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.form.dobj.DOTaxDetail;
import nic.vahan.form.dobj.DOTaxDetails;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author DELL
 */
public class VahanTaxRestClient {

    private static final Logger LOGGER = Logger.getLogger(VahanTaxRestClient.class);

    private WebResource webResource;
    private Client client;
    private static String BASE_URI = "http://127.0.0.1:{port}/vahantaxws/webresources/";//Production

    public VahanTaxRestClient() throws VahanException {
        String baseURL = null;
        try {
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            int serverPort = request.getLocalPort();
            BASE_URI = BASE_URI.replace("{port}", serverPort + "");

//            com.sun.jersey.api.client.config.ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
//            client = Client.create(config);
//            client.setConnectTimeout(60000);
//            client.setReadTimeout(60000);
//            webResource = client.resource(baseURL).path("mvtax/taxDetails");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Can not establish connection with Tax Service");
        }

    }

    public String mvTax_JSON_STRING(String requestEntity) throws VahanException {
        String status = "";
        try {
            //return webResource.type(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(responseType, requestEntity);

            String baseURL = BASE_URI + "mvtax/taxDetails";

            URL obj = new URL(baseURL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setConnectTimeout(15000);
            //con.setReadTimeout(120000);
            // add reuqest header
            con.setRequestMethod("POST");
            //con.setRequestProperty("User-Agent", USER_AGENT);
            //con.setRequestProperty("Authorization", "Bearer " + TOKEN);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");
            // Send post request
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(requestEntity.getBytes("UTF-8"));
            os.flush();
            os.close();
            int responseCode = con.getResponseCode();

//            DefaultHttpClient httpClient = new DefaultHttpClient();
//            int timeout = 50; // seconds
//            HttpParams httpParams = httpClient.getParams();
//            HttpConnectionParams.setConnectionTimeout(httpParams, timeout * 1000); // http.connection.timeout
//            HttpConnectionParams.setSoTimeout(httpParams, timeout * 1000); // http.socket.timeout
//            HttpPost postRequest = new HttpPost(baseURL);
//            postRequest.addHeader("content-type", "application/json");
//            postRequest.addHeader("Accept", "application/json");
//            StringEntity input = new StringEntity(requestEntity);
//            postRequest.setEntity(input);
//            HttpResponse httpresponse = httpClient.execute(postRequest);
//            int statusCode = httpresponse.getStatusLine().getStatusCode();
            if (responseCode == 200) {
//                HttpEntity httpEntity = httpresponse.getEntity();
//                status = EntityUtils.toString(httpEntity);
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                status = response.toString();
                //responseFromservice = mapper.readValue(response.toString(), ChallanResponseDobj.class);

            } else {
                throw new VahanException("Can not establish connection with Tax Service");
            }
        } catch (VahanException ex) {
            throw ex;
            //java.util.logging.Logger.getLogger(VahanTaxRestClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Can not establish connection with Tax Service");

        }
        return status;

    }

    public List<DOTaxDetail> parseTaxResponse(String taxserviceResponse, int taxPurCd, String taxMode) throws VahanException {
        List<DOTaxDetail> taxList = null;
        if (Utility.isNullOrBlank(taxserviceResponse)) {
            return taxList;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            DOTaxDetails dobj = mapper.readValue(taxserviceResponse, DOTaxDetails.class);
            String taxHead = ServerUtil.getTaxHead(taxPurCd);
            taxList = dobj.getTaxList();
            DateFormat parser = new SimpleDateFormat("dd-MM-yyyy");
            if (taxList != null) {
                for (DOTaxDetail taxDobj : taxList) {
                    Date dateFrom = (Date) parser.parse(taxDobj.getTAX_FROM());
                    String writedateFrom = new SimpleDateFormat("dd-MMM-yyyy").format(dateFrom);
                    Date dateUpto = (Date) parser.parse(taxDobj.getTAX_UPTO());
                    String writedateUpto = "";
                    // Add by Afzal on 17 June 2016. To display the OTT in Known Your MV Tax form.
                    if (Util.getUserStateCode() == null) {
                        if ("S,O,L".contains(taxMode)) {
                            writedateUpto = "One Time Tax";
                        } else {
                            writedateUpto = new SimpleDateFormat("dd-MMM-yyyy").format(dateUpto);
                        }
                    } else {
                        writedateUpto = new SimpleDateFormat("dd-MMM-yyyy").format(dateUpto);
                    }

                    taxDobj.setTAX_HEAD(taxHead);
                    taxDobj.setPUR_CD(taxPurCd);
                    taxDobj.setTAX_MODE(taxMode);
                    taxDobj.setTAX_FROM(writedateFrom);
                    taxDobj.setTAX_UPTO(writedateUpto);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            taxList = null;
            throw new VahanException("Unable to Parse Tax Response");
        } catch (ParseException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            taxList = null;
            throw new VahanException("Unable to Parse Tax Date Response");
        }

        return taxList;

    }
}
