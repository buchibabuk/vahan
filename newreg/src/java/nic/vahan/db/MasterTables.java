/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db;

import java.util.*;

/**
 *
 * @author tranC095
 */
public class MasterTables {
    /*
     * Master Table Read-Only Objects
     */
    //.........................................
    // CODE/DESCR based master tables
    //.........................................

    public MasterTableDO TM_BANK;
    public MasterTableDO TM_USER_CATG;
    public MasterTableDO TM_DISTRICT;
    public MasterTableDO TM_STATE;
    public MasterTableDO TM_OFFICE;
    public MasterTableDO TM_DESIGNATION;
    public MasterTableDO TM_PURPOSE_MAST;
    public MasterTableDO TM_ROLE;
    public MasterTableDO TM_INSTRUMENTS;
    public MasterTableDO TM_COUNTRY;
    public MasterTableDO VM_AREA_MAST;
    public MasterTableDO VM_VH_CLASS;
    public MasterTableDO VM_VCH_CATG;
    public MasterTableDO VM_VHCLASS_CATG_MAP;
    public MasterTableDO VM_OWCODE;
    public MasterTableDO VM_REGN_TYPE;
    public MasterTableDO VM_FUEL;
    public MasterTableDO VM_OWCATG;
    public MasterTableDO VM_ICCODE;
    public MasterTableDO VM_INSTYP;
    public MasterTableDO VM_MAKER;
    public MasterTableDO VM_MODELS;
    public MasterTableDO VM_FIT_OFFICER;
    public MasterTableDO VM_TAX_SLAB_FIELDS;
    public MasterTableDO VM_HP_TYPE;
    public MasterTableDO VM_NORMS;
    public MasterTableDO VM_TAX_MODE;
    public MasterTableDO VM_OTHER_CRITERIA;
    public MasterTableDO VM_DOMAIN;
    public MasterTableDO VM_PMT_CATEGORY;
    public MasterTableDO VM_ROUTE_MASTER;
    public MasterTableDO VM_PMT_MAST;
    public MasterTableDO vm_service_type;
    public MasterTableDO VM_PERMIT_TYPE;
    public MasterTableDO VM_PERMIT_OBJECTION;
    public MasterTableDO VM_HSRP_FLAG;
    public MasterTableDO VM_REASON;
//Added By Nitin :25-feb-2015
    public MasterTableDO VM_ACCUSED;
    public MasterTableDO VM_CHALLAN_BOOK;
    public MasterTableDO VM_DA_PENALTY;
    public MasterTableDO VM_DOCUMENTS;
    public MasterTableDO VM_EXCESS_SCHEDULE;
    public MasterTableDO VM_OFFENCE_PENALTY;
    public MasterTableDO VM_OVERLOAD_SCHEDULE;
    public MasterTableDO VM_PERMIT_DOCUMENTS;
    //added By Raj Yadav
    public MasterTableDO VM_SCRAP_REASONS;
    //Added By Nitin :25-feb-2015    
    public MasterTableDO VM_REGION;
    public MasterTableDO VM_BLACKLIST;
    public MasterTableDO VM_SPEEDGOV_MANUFACTURE;
    public MasterTableDO VM_TEMP_REGN_REASON;
    public MasterTableDO VM_RCD_RETURN_REASON;
    public MasterTableDO VM_TAXDUE_FROM;
    //// FOR TC ODISHA (onlineschema_tc)
    public MasterTableDO VM_VCH_CLASS;
    public MasterTableDO VM_RELATION;
    //// For Office Timing /////
    public MasterTableDO TM_CONFIGURATION_LOGIN;
    
    //.............................................................
    // For internal use to store the individual references so that
    // common methods can be called in a loop
    //.............................................................
    private Vector masterTableDOs;

    /**
     * Once the master table DO are created, call this method to fill the
     * masterTableDOs.
     *
     */
    public void fillMasterTableVector() {
        masterTableDOs = new Vector();
        //..........................................................
        // Add the master table references in the masterTableDOs
        //..........................................................
        masterTableDOs.add(TM_BANK);
        masterTableDOs.add(TM_DISTRICT);
        masterTableDOs.add(TM_STATE);
        masterTableDOs.add(TM_PURPOSE_MAST);
        masterTableDOs.add(TM_INSTRUMENTS);
        masterTableDOs.add(TM_OFFICE);
        masterTableDOs.add(TM_DESIGNATION);
        masterTableDOs.add(TM_ROLE);
        masterTableDOs.add(TM_USER_CATG);
        masterTableDOs.add(VM_OWCODE);
        masterTableDOs.add(VM_REGN_TYPE);
        masterTableDOs.add(VM_FUEL);
        masterTableDOs.add(VM_OWCATG);
        masterTableDOs.add(VM_ICCODE);
        masterTableDOs.add(VM_INSTYP);
        masterTableDOs.add(VM_MAKER);
        masterTableDOs.add(VM_MODELS);
        masterTableDOs.add(VM_FIT_OFFICER);
        masterTableDOs.add(VM_TAX_SLAB_FIELDS);
        masterTableDOs.add(VM_HP_TYPE);
        masterTableDOs.add(VM_NORMS);
        masterTableDOs.add(VM_TAX_MODE);
        masterTableDOs.add(VM_OTHER_CRITERIA);
        // Added by Prashant : 31-10-2014        
        masterTableDOs.add(VM_PMT_MAST);
        masterTableDOs.add(VM_DOMAIN);
        masterTableDOs.add(VM_PMT_CATEGORY);
        masterTableDOs.add(VM_PERMIT_TYPE);
        masterTableDOs.add(VM_REGION);
        masterTableDOs.add(VM_HSRP_FLAG);
        masterTableDOs.add(VM_REASON);
        masterTableDOs.add(VM_SCRAP_REASONS);
        masterTableDOs.add(VM_SPEEDGOV_MANUFACTURE);
        masterTableDOs.add(VM_TEMP_REGN_REASON);
        masterTableDOs.add(VM_RCD_RETURN_REASON);
        masterTableDOs.add(VM_TAXDUE_FROM);

    }

    /**
     * Returns the master table list
     *
     * @return Vector containing master table DOs.
     */
    public Vector getMasterTables() {
        return masterTableDOs;
    }

    /**
     * Dump. Prints the contents of the Master Table Objects.
     */
    public void dump() {
        int iDOSize = masterTableDOs.size();
        for (int i = 0; i < iDOSize; i++) {
            ((MasterTableDO) masterTableDOs.get(i)).dump();
        }
    }
}
