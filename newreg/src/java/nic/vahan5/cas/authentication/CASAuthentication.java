/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.cas.authentication;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;
import java.util.logging.Level;
import nic.vahan5.reg.rest.config.SpringContext;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 *
 * @author Ramesh
 */
public class CASAuthentication {
    
    private static final Logger LOGGER = Logger.getLogger(CASAuthentication.class);
    private WebClient.Builder webClientBuilder = SpringContext.getBean(WebClient.Builder.class);
    private static String ROOT_URI_CAS;
    private static String CAS_SERVICE_ID;
    private static String CAS_SERVICE_VALIDATE;

    // Load properties from the resources/cas.properties file for CAS methods
    static {
        try {
            Properties properties = new Properties();
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("/resources/cas.properties");
            properties.load(inputStream);
            String cas_url = properties.getProperty("cas.server.url");
            CAS_SERVICE_ID = properties.getProperty("cas.service.url");
            ROOT_URI_CAS = cas_url + properties.getProperty("cas.v1.tickets");
            CAS_SERVICE_VALIDATE = cas_url + properties.getProperty("cas.p3.validation");
            
        } catch (IOException ex) {
            LOGGER.info(CASAuthentication.class.getName() + ":---" + ex);
        }
    }

    // Internally checks if CAS ticket is already there in DB
    // IF it is there, validates it, creates a service ticket and validates it.
    // Else, creates the Ticket in CAS DB/Server by passing username and password    
    public CASTicketDetails validateCasTicket(String user_id, String password, String casTicket) {
        boolean status = false;
        
        CASTicketDetails casTicketDetails = null;
        if (casTicket != null) {
            status = casTicketStatus(casTicket);
        }
        if (!status) {
            // Create CAS ticket, create service ticket and validate the service ticket
            casTicketDetails = getCasTicket(user_id, password);
            return casTicketDetails;
            
        } else {
            CASTicketDetails serviceTicket = casServiceTicket(casTicket);
            if (serviceTicket.getResonseTicket() == "true") {
                return new CASTicketDetails(serviceTicket.getStatusCode(), casTicket);
            } else {
                return new CASTicketDetails(serviceTicket.getStatusCode(), serviceTicket.getResonseTicket());
            }
        }
        
    }

    // Create CAS ticket, create service ticket and validate the service ticket
    public CASTicketDetails getCasTicket(String user_id, String password) {
        String casTicket = null;
        String response = null;
        try {
            response = webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .build()
                    .post()
                    .uri(ROOT_URI_CAS)
                    .body(BodyInserters.fromFormData("username", user_id).with("password", password))
                    .retrieve()
                    .bodyToMono(String.class).timeout(Duration.ofSeconds(10))
                    .block();
            if (response.indexOf("TGT Created") >= 0) {
                // Create the service ticket and validate it
                casTicket = response.substring(response.lastIndexOf("/tickets/") + "/tickets/".length(), response.lastIndexOf(" method") - 1);
                CASTicketDetails serviceTicket = casServiceTicket(casTicket);
                if (serviceTicket.getResonseTicket() == "true") {
                    return new CASTicketDetails(201, casTicket);
                } else {
                    return new CASTicketDetails(serviceTicket.getStatusCode(), serviceTicket.getResonseTicket());
                }
            } else if (response.indexOf("Not Found") >= 0) {
                return new CASTicketDetails(404, "Not Found");
            } else if (response.indexOf("authentication_exceptions") >= 0) {
                return new CASTicketDetails(401, "authentication_exceptions");
            } else {
                return new CASTicketDetails(408, "Internal Error");
            }
        } catch (WebClientResponseException e) {
            LOGGER.error(e.getMessage());
            return new CASTicketDetails(401, "authentication_exceptions");
        } catch (Exception e) {
            return new CASTicketDetails(401, "authentication_exceptions");
        }
        
    }

    //Create the Servicet Ticket and validate from CAS server by passing CAS TGT (Ticket Granting Ticket)
    public CASTicketDetails casServiceTicket(String casTicket) {
        
        try {
            String response = webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .build()
                    .post()
                    .uri(ROOT_URI_CAS + "/" + casTicket)
                    .body(BodyInserters.fromFormData("service", CAS_SERVICE_ID))
                    .retrieve()
                    .bodyToMono(String.class).timeout(Duration.ofSeconds(10))
                    .block();
            if (response.indexOf("ST-") >= 0) {
                String serviceTicket = response.trim();
                // CAS service validation based on service ticket
                if (casServiceValidate(serviceTicket)) {
                    return new CASTicketDetails(200, "true");
                }
            } else if (response.indexOf("considered invalid") >= 0 || response.indexOf("Not Found") >= 0) {
                return new CASTicketDetails(404, "false");
            } else if (response.indexOf("Service unauthorized") >= 0) {
                return new CASTicketDetails(501, "false");
            } else {
                return new CASTicketDetails(408, "false");
            }
        } catch (WebClientResponseException e) {
            LOGGER.error(e.getMessage());
            return new CASTicketDetails(408, "false");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return new CASTicketDetails(408, "false");
        }
        return new CASTicketDetails(408, "false");
        
    }

    // Validate the Service ticket in CAS Server
    public boolean casServiceValidate(String serviceTicket) {
        try {
            String validation = webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .build()
                    .get()
                    .uri(CAS_SERVICE_VALIDATE + "service=" + CAS_SERVICE_ID + "&ticket=" + serviceTicket.trim())
                    .retrieve()
                    .bodyToMono(String.class).timeout(Duration.ofSeconds(10))
                    .block();
            if (validation.indexOf("authenticationFailure") < 0) {
                return true;
            }
        } catch (WebClientResponseException e) {
            LOGGER.error(e.getMessage());
            return false;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return false;
        }
        return false;
        
    }

    // Get the status of  CAS Ticket from CAS Server
    // Checks if the ticket is there in the DB or not
    public boolean casTicketStatus(String casTicket) {
        try {
            Mono<ClientResponse> exchange = webClientBuilder.build()
                    .get()
                    .uri(ROOT_URI_CAS + "/" + casTicket)
                    .exchange().timeout(Duration.ofSeconds(10));
            HttpStatus statusCode = exchange.block().statusCode();
            if (statusCode.toString().contains("200 OK")) {
                
                return true;
            }
        } catch (WebClientResponseException e) {
            LOGGER.error(e.getMessage());
            return false;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return false;
        }
        return false;
    }

    // Delete CAS Ticket in the database
    public boolean deleteCasTicket(String casTicket) {
        try {
            Mono<ClientResponse> exchange = webClientBuilder.build()
                    .delete()
                    .uri(ROOT_URI_CAS + "/" + casTicket)
                    .exchange().timeout(Duration.ofSeconds(10));
            HttpStatus statusCode = exchange.block().statusCode();
            if (statusCode.toString().contains("200 OK")) {
                return true;
            }
        } catch (WebClientResponseException e) {
            LOGGER.error(e.getMessage());
            return false;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return false;
        }
        return false;
    }
    
    public CASTicketDetails homologationCasServiceTicket(String casTicket) {

        String response = webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build()
                .post()
                .uri(ROOT_URI_CAS + "/" + casTicket)
                .body(BodyInserters.fromFormData("service", CAS_SERVICE_ID))
                .retrieve()
                .bodyToMono(String.class).timeout(Duration.ofSeconds(10))
                .block();
        if (response.indexOf("ST-") >= 0) {
            return new CASTicketDetails(200, response.trim());
        } else if (response.indexOf("considered invalid") >= 0 || response.indexOf("Not Found") >= 0) {
            return new CASTicketDetails(404, "Not Found");
        } else if (response.indexOf("Service unauthorized") >= 0) {
            return new CASTicketDetails(501, "Service unauthorized");
        } else {
            return new CASTicketDetails(408, "false");
        }
    }
}
