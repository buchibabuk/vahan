/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.server.taxcal;

import java.util.Date;

/**
 *
 * @author nic5912
 */
public class DOTaxDetails {
    private String SL_NO;
    private int PUR_CD;
    private String HEAD;
    private Date TAX_FROM_TO;
    private int AMOUNT;
    private int EXEMPTED;
    private int FINE;
    private int REBATE;
    private int SURCHAGE;
    private int SERVICE_CHARGE;
    private int TOT_AMT;
    private Date TAX_FROM;
    private Date TAX_UPTO;

     public DOTaxDetails() {
       
    }

     public DOTaxDetails(String SL_NO, int PUR_CD, String HEAD, Date TAX_FROM_TO ,
               int AMOUNT, int  EXEMPTED , int FINE, int REBATE,
               int SURCHAGE,int SERVICE_CHARGE,int TOT_AMT, Date TAX_FROM, Date TAX_UPTO )
                
     {
         
         init( SL_NO,  PUR_CD,  HEAD,  TAX_FROM_TO ,
                AMOUNT,   EXEMPTED ,  FINE,  REBATE,
                SURCHAGE,SERVICE_CHARGE, TOT_AMT,  TAX_FROM,  TAX_UPTO );

     }
      public void init(String SL_NO, int PUR_CD, String HEAD, Date TAX_FROM_TO ,
               int AMOUNT, int  EXEMPTED , int FINE, int REBATE,
               int SURCHAGE,int SERVICE_CHARGE,int TOT_AMT, Date TAX_FROM, Date TAX_UPTO  )
       {
           this.setSL_NO(SL_NO);
           this.PUR_CD=PUR_CD;
           this.HEAD=HEAD;
           this.TAX_FROM_TO=TAX_FROM_TO;
           this.AMOUNT=AMOUNT;
           this.EXEMPTED=EXEMPTED;
           this.FINE=FINE;
           this.REBATE=REBATE;
           this.SURCHAGE=SURCHAGE;
           this.SERVICE_CHARGE=SERVICE_CHARGE;
           this.TOT_AMT=TOT_AMT;
           this.TAX_FROM=TAX_FROM;
           this.TAX_UPTO=TAX_UPTO;

       }

    public String getSL_NO() {
        return SL_NO;
    }

    public void setSL_NO(String SL_NO) {
        this.SL_NO = SL_NO;
    }

    public int getPUR_CD() {
        return PUR_CD;
    }

    public void setPUR_CD(int PUR_CD) {
        this.PUR_CD = PUR_CD;
    }

    public String getHEAD() {
        return HEAD;
    }

    public void setHEAD(String HEAD) {
        this.HEAD = HEAD;
    }

    public Date getTAX_FROM_TO() {
        return TAX_FROM_TO;
    }

    public void setTAX_FROM_TO(Date TAX_FROM_TO) {
        this.TAX_FROM_TO = TAX_FROM_TO;
    }

    public int getAMOUNT() {
        return AMOUNT;
    }

    public void setAMOUNT(int AMOUNT) {
        this.AMOUNT = AMOUNT;
    }

    public int getEXEMPTED() {
        return EXEMPTED;
    }

    public void setEXEMPTED(int EXEMPTED) {
        this.EXEMPTED = EXEMPTED;
    }

    public int getFINE() {
        return FINE;
    }

    public void setFINE(int FINE) {
        this.FINE = FINE;
    }

    public int getREBATE() {
        return REBATE;
    }

    public void setREBATE(int REBATE) {
        this.REBATE = REBATE;
    }

    public int getSURCHAGE() {
        return SURCHAGE;
    }

    public void setSURCHAGE(int SURCHAGE) {
        this.SURCHAGE = SURCHAGE;
    }

    public int getTOT_AMT() {
        return TOT_AMT;
    }

    public void setTOT_AMT(int TOT_AMT) {
        this.TOT_AMT = TOT_AMT;
    }

    public Date getTAX_FROM() {
        return TAX_FROM;
    }

    public void setTAX_FROM(Date TAX_FROM) {
        this.TAX_FROM = TAX_FROM;
    }

    public Date getTAX_UPTO() {
        return TAX_UPTO;
    }

    public void setTAX_UPTO(Date TAX_UPTO) {
        this.TAX_UPTO = TAX_UPTO;
    }

    public int getSERVICE_CHARGE() {
        return SERVICE_CHARGE;
    }

    public void setSERVICE_CHARGE(int SERVICE_CHARGE) {
        this.SERVICE_CHARGE = SERVICE_CHARGE;
    }
    
     
     
     
}
