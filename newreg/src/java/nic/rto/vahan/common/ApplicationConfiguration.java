package nic.rto.vahan.common;

import java.io.Serializable;
import java.util.Date;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author tranC111
 */
@ManagedBean(name = "applicationConfiguration")
@ApplicationScoped
public class ApplicationConfiguration implements Serializable {

    public static volatile boolean allowConn = true;
    public static volatile boolean allowConnReadOnly = true;
    public static volatile Date lastConnTime = null;
    public static volatile Date deferConnTimeReadOnly = null;
    public static volatile Date deferConnTimeLocalOnly = null;
    public static int dbWaitTime = 2;
    public static String showMessage = null;
    public static String showUserMessage = null;
    public static String totalRunningV4_offices = "";
    public static int totalVahan4_offices;
    public static int totalVahan_offices;
    public static int totalOlderVahan_offices;
    private static int totalnonVahan_states;
    private static int totalnonVahan_offices;

    /**
     * @return the totalnonVahan_states
     */
    public static int getTotalnonVahan_states() {
        return totalnonVahan_states;
    }

    /**
     * @param aTotalnonVahan_states the totalnonVahan_states to set
     */
    public static void setTotalnonVahan_states(int aTotalnonVahan_states) {
        totalnonVahan_states = aTotalnonVahan_states;
    }

    /**
     * @return the totalnonVahan_offices
     */
    public static int getTotalnonVahan_offices() {
        return totalnonVahan_offices;
    }

    /**
     * @param aTotalnonVahan_offices the totalnonVahan_offices to set
     */
    public static void setTotalnonVahan_offices(int aTotalnonVahan_offices) {
        totalnonVahan_offices = aTotalnonVahan_offices;
    }
}
