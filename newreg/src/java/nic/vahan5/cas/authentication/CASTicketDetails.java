/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.cas.authentication;

import java.io.Serializable;

/**
 *
 * @author Administrator
 */
public class CASTicketDetails implements Serializable {

    private static final long serialVersionUID = 1L;
    private int statusCode;
    private String resonseTicket;

    public CASTicketDetails(int statusCode, String resonseTicket) {
        this.statusCode = statusCode;
        this.resonseTicket = resonseTicket;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getResonseTicket() {
        return resonseTicket;
    }

    public void setResonseTicket(String resonseTicket) {
        this.resonseTicket = resonseTicket;
    }

    @Override
    public String toString() {
        return "CASTicketDetails{" + "statusCode=" + statusCode + ", resonseTicket=" + resonseTicket + '}';
    }
    
    
}
