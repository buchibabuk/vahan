/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.CommonUtils;

import java.io.Serializable;

/**
 *
 * @author AMBRISH
 */
public class VehicleParameters implements Serializable {

    private Integer SEAT_CAP = 0;
    private Integer UNLD_WT = 0;
    private Integer LD_WT = 0;
    private Integer GCW = 0;
    private String REGN_DATE;
    private Integer STAND_CAP = 0;
    private Integer SLEEPAR_CAP = 0;
    private Float FLOOR_AREA = 0.0f;
    private Integer SALE_AMT = 0;
    private Integer OWNER_CD = 0;
    private Integer FUEL = 0;
    private Integer VH_CLASS = 0;
    private Float HP = 0.0f;
    private Float CC = 0.0f;
    private String VEH_PURCHASE_AS = "B";
    private String AC_FITTED = "N";
    private Integer DOMAIN_CD = 0;
    private Integer ROUTE_CLASS = 0;
    private Integer SERVICE_TYPE = 0;
    private Integer PERMIT_TYPE = 0;
    private Integer PERMIT_SUB_CATG = 0;
    private Integer DISTANCE_RUN_IN_QTR = 0;
    private Integer NO_OF_TRIPS_PER_DAY = 0;
    private Double ROUTE_LENGTH = 0.0;
    private Integer OTHER_CRITERIA = 0;
    private String STATE_CD = "";
    private Integer PUR_CD = 0;
    private String TAX_MODE = "";
    private String TAX_DUE_FROM_DATE;
    private Integer OFF_CD = 0;
    private String VCH_CATG = "";
    private String REGN_TYPE = "";
    private Integer VCH_TYPE = 0;
    private Integer OLD_VCH_TYPE = 0;
    private Integer VCH_AGE = 0;
    private Integer DELAY_DAYS = 0;
    private Integer DELAY_MONTHS = 0;
    private Integer DELAY_YEARS = 0;
    private String AUDIO_FITTED = "N";
    private String VIDEO_FITTED = "N";
    //private List<Trailer> TRAILERS;
    private Integer NO_OF_ROUTES = 0;
    private Integer NO_OF_REGIONS = 0;
    private String VCH_IMPORTED = "N";
    private String NEW_VCH = "N";
    private Integer PMT_DAYS = 0;
    private Integer PMT_MONTHS = 0;
    private Integer PMT_CEL_MONTH = 0;
    private Integer PMT_YEAR = 0;
    private String REGN_NO;
    private Integer ROUTE_COUNT = 0;
    private Integer REGION_COUNT = 0;
    private Integer STATE_COUNT = 0;
    private Integer NOC_RETENTION = 0;
    private String TMP_PURPOSE = "";
    private Integer EXEM_AMOUNT = 0;
    private Integer FINE_TO_BE_TAKEN = 0;
    private String MULTI_REGION = "false";
    private Integer PARKING_FEE_ADVANCE_DAYS = 0;
    private String VEH_AGE_EXPIRED = "false";
    private Integer MULTI_DOC = 0;
    private String VEHICLE_HYPTH = "false";
    private String FIT_UPTO = "";
    private Integer NORMS = 0;
    private Integer NET_TAX_AMT = 0;
    private Integer TAX_AMT = 0;
    private Integer PENALTY = 0;
    private Integer OWN_CATG = 0;
    private String TO_REASON = "";
    private String FIT_STATUS = "";
    private Integer PMT_AMOUNT = 0;
    private String EXCEM_FLAG = "";
    private String OFFER_LETTER = "false";
    private Integer TRANSACTION_PUR_CD = 0;
    private String DUP_PMT_REASON = "";
    private String ONLINE_PERMIT = "FALSE";
    private String PAYMENT_DATE;
    private String PERMIT_EXPIRED = "false";
    private String INSP_RQRD = "false";
    private Integer LOGIN_OFF_CD = 0;
    private String APPLICANT_TYPE = "D";
    private String APPLICATION_TYPE = "N";
    private int PREV_OFF_CD = 0;
    private String REGION_DESCR;
    private String PURCHASE_DATE;
    private String REGN_UPTO;
    private Integer OWNER_CATG = 0;
    private Integer DAYSWITHINSTATE = 0;
    private Integer BLACKLISTCODE = 0;
    private String MANUFACTUR_DATE = "";
    private Integer AUTHYEAR = 0;
    private String APPL_DATE;
    private Integer ACTION_CD = 0;
    private Integer OFF_TYPE_CD;
    private String TRIPEXTENDED = "N";
    private String ISPAYMENTDONE = "TRUE";
    private String REGN_STATE = "FALSE";

    /**
     * @return the REGN_DATE
     */
    public String getREGN_DATE() {
        return REGN_DATE;
    }

    /**
     * @param REGN_DATE the REGN_DATE to set
     */
    public void setREGN_DATE(String REGN_DATE) {
        this.REGN_DATE = REGN_DATE;
    }

    /**
     * @return the SEAT_CAP
     */
    public Integer getSEAT_CAP() {
        return SEAT_CAP;
    }

    /**
     * @param SEAT_CAP the SEAT_CAP to set
     */
    public void setSEAT_CAP(Integer SEAT_CAP) {
        this.SEAT_CAP = SEAT_CAP;
    }

    /**
     * @return the STAND_CAP
     */
    public Integer getSTAND_CAP() {
        return STAND_CAP;
    }

    /**
     * @param STAND_CAP the STAND_CAP to set
     */
    public void setSTAND_CAP(Integer STAND_CAP) {
        this.STAND_CAP = STAND_CAP;
    }

    /**
     * @return the SLEEPAR_CAP
     */
    public Integer getSLEEPAR_CAP() {
        return SLEEPAR_CAP;
    }

    /**
     * @param SLEEPAR_CAP the SLEEPAR_CAP to set
     */
    public void setSLEEPAR_CAP(Integer SLEEPAR_CAP) {
        this.SLEEPAR_CAP = SLEEPAR_CAP;
    }

    /**
     * @return the UNLD_WT
     */
    public Integer getUNLD_WT() {
        return UNLD_WT;
    }

    /**
     * @param UNLD_WT the UNLD_WT to set
     */
    public void setUNLD_WT(Integer UNLD_WT) {
        this.UNLD_WT = UNLD_WT;
    }

    /**
     * @return the LD_WT
     */
    public Integer getLD_WT() {
        return LD_WT;
    }

    /**
     * @param LD_WT the LD_WT to set
     */
    public void setLD_WT(Integer LD_WT) {
        this.LD_WT = LD_WT;
    }

    /**
     * @return the FLOOR_AREA
     */
    public Float getFLOOR_AREA() {
        return FLOOR_AREA;
    }

    /**
     * @param FLOOR_AREA the FLOOR_AREA to set
     */
    public void setFLOOR_AREA(Float FLOOR_AREA) {
        this.FLOOR_AREA = FLOOR_AREA;
    }

    /**
     * @return the SALE_AMT
     */
    public Integer getSALE_AMT() {
        return SALE_AMT;
    }

    /**
     * @param SALE_AMT the SALE_AMT to set
     */
    public void setSALE_AMT(Integer SALE_AMT) {
        this.SALE_AMT = SALE_AMT;
    }

    /**
     * @return the OWNER_CD
     */
    public Integer getOWNER_CD() {
        return OWNER_CD;
    }

    /**
     * @param OWNER_CD the OWNER_CD to set
     */
    public void setOWNER_CD(Integer OWNER_CD) {
        this.OWNER_CD = OWNER_CD;
    }

    /**
     * @return the FUEL
     */
    public Integer getFUEL() {
        return FUEL;
    }

    /**
     * @param FUEL the FUEL to set
     */
    public void setFUEL(Integer FUEL) {
        this.FUEL = FUEL;
    }

    /**
     * @return the VH_CLASS
     */
    public Integer getVH_CLASS() {
        return VH_CLASS;
    }

    /**
     * @param VH_CLASS the VH_CLASS to set
     */
    public void setVH_CLASS(Integer VH_CLASS) {
        this.VH_CLASS = VH_CLASS;
    }

    /**
     * @return the HP
     */
    public Float getHP() {
        return HP;
    }

    /**
     * @param HP the HP to set
     */
    public void setHP(Float HP) {
        this.HP = HP;
    }

    /**
     * @return the CC
     */
    public Float getCC() {
        return CC;
    }

    /**
     * @param CC the CC to set
     */
    public void setCC(Float CC) {
        this.CC = CC;
    }

    /**
     * @return the VEH_PURCHASE_AS
     */
    public String getVEH_PURCHASE_AS() {
        return VEH_PURCHASE_AS;
    }

    /**
     * @param VEH_PURCHASE_AS the VEH_PURCHASE_AS to set
     */
    public void setVEH_PURCHASE_AS(String VEH_PURCHASE_AS) {
        this.VEH_PURCHASE_AS = VEH_PURCHASE_AS;
    }

    /**
     * @return the AC_FITTED
     */
    public String getAC_FITTED() {
        return AC_FITTED;
    }

    /**
     * @param AC_FITTED the AC_FITTED to set
     */
    public void setAC_FITTED(String AC_FITTED) {
        this.AC_FITTED = AC_FITTED;
    }

    /**
     * @return the DOMAIN_CD
     */
    public Integer getDOMAIN_CD() {
        return DOMAIN_CD;
    }

    /**
     * @param DOMAIN_CD the DOMAIN_CD to set
     */
    public void setDOMAIN_CD(Integer DOMAIN_CD) {
        this.DOMAIN_CD = DOMAIN_CD;
    }

    /**
     * @return the ROUTE_CLASS
     */
    public Integer getROUTE_CLASS() {
        return ROUTE_CLASS;
    }

    /**
     * @param ROUTE_CLASS the ROUTE_CLASS to set
     */
    public void setROUTE_CLASS(Integer ROUTE_CLASS) {
        this.ROUTE_CLASS = ROUTE_CLASS;
    }

    /**
     * @return the SERVICE_TYPE
     */
    public Integer getSERVICE_TYPE() {
        return SERVICE_TYPE;
    }

    /**
     * @param SERVICE_TYPE the SERVICE_TYPE to set
     */
    public void setSERVICE_TYPE(Integer SERVICE_TYPE) {
        this.SERVICE_TYPE = SERVICE_TYPE;
    }

    /**
     * @return the PERMIT_TYPE
     */
    public Integer getPERMIT_TYPE() {
        return PERMIT_TYPE;
    }

    /**
     * @param PERMIT_TYPE the PERMIT_TYPE to set
     */
    public void setPERMIT_TYPE(Integer PERMIT_TYPE) {
        this.PERMIT_TYPE = PERMIT_TYPE;
    }

    /**
     * @return the PERMIT_SUB_CATG
     */
    public Integer getPERMIT_SUB_CATG() {
        return PERMIT_SUB_CATG;
    }

    /**
     * @param PERMIT_SUB_CATG the PERMIT_SUB_CATG to set
     */
    public void setPERMIT_SUB_CATG(Integer PERMIT_SUB_CATG) {
        this.PERMIT_SUB_CATG = PERMIT_SUB_CATG;
    }

    /**
     * @return the DISTANCE_RUN_IN_QTR
     */
    public Integer getDISTANCE_RUN_IN_QTR() {
        return DISTANCE_RUN_IN_QTR;
    }

    /**
     * @param DISTANCE_RUN_IN_QTR the DISTANCE_RUN_IN_QTR to set
     */
    public void setDISTANCE_RUN_IN_QTR(Integer DISTANCE_RUN_IN_QTR) {
        this.DISTANCE_RUN_IN_QTR = DISTANCE_RUN_IN_QTR;
    }

    /**
     * @return the NO_OF_TRIPS_PER_DAY
     */
    public Integer getNO_OF_TRIPS_PER_DAY() {
        return NO_OF_TRIPS_PER_DAY;
    }

    /**
     * @param NO_OF_TRIPS_PER_DAY the NO_OF_TRIPS_PER_DAY to set
     */
    public void setNO_OF_TRIPS_PER_DAY(Integer NO_OF_TRIPS_PER_DAY) {
        this.NO_OF_TRIPS_PER_DAY = NO_OF_TRIPS_PER_DAY;
    }

    /**
     * @return the ROUTE_LENGTH
     */
    public Double getROUTE_LENGTH() {
        return ROUTE_LENGTH;
    }

    /**
     * @param ROUTE_LENGTH the ROUTE_LENGTH to set
     */
    public void setROUTE_LENGTH(Double ROUTE_LENGTH) {
        this.ROUTE_LENGTH = ROUTE_LENGTH;
    }

    /**
     * @return the OTHER_CRITERIA
     */
    public Integer getOTHER_CRITERIA() {
        return OTHER_CRITERIA;
    }

    /**
     * @param OTHER_CRITERIA the OTHER_CRITERIA to set
     */
    public void setOTHER_CRITERIA(Integer OTHER_CRITERIA) {
        this.OTHER_CRITERIA = OTHER_CRITERIA;
    }

    /**
     * @return the STATE_CD
     */
    public String getSTATE_CD() {
        return STATE_CD;
    }

    /**
     * @param STATE_CD the STATE_CD to set
     */
    public void setSTATE_CD(String STATE_CD) {
        this.STATE_CD = STATE_CD;
    }

    /**
     * @return the PUR_CD
     */
    public Integer getPUR_CD() {
        return PUR_CD;
    }

    /**
     * @param PUR_CD the PUR_CD to set
     */
    public void setPUR_CD(Integer PUR_CD) {
        this.PUR_CD = PUR_CD;
    }

    /**
     * @return the TAX_DUE_FROM_DATE
     */
    public String getTAX_DUE_FROM_DATE() {
        return TAX_DUE_FROM_DATE;
    }

    /**
     * @param TAX_DUE_FROM_DATE the TAX_DUE_FROM_DATE to set
     */
    public void setTAX_DUE_FROM_DATE(String TAX_DUE_FROM_DATE) {
        this.TAX_DUE_FROM_DATE = TAX_DUE_FROM_DATE;
    }

    /**
     * @return the TAX_MODE
     */
    public String getTAX_MODE() {
        return TAX_MODE;
    }

    /**
     * @param TAX_MODE the TAX_MODE to set
     */
    public void setTAX_MODE(String TAX_MODE) {
        this.TAX_MODE = TAX_MODE;
    }

    /**
     * @return the OFF_CD
     */
    public Integer getOFF_CD() {
        return OFF_CD;
    }

    /**
     * @param OFF_CD the OFF_CD to set
     */
    public void setOFF_CD(Integer OFF_CD) {
        this.OFF_CD = OFF_CD;
    }

    /**
     * @return the VCH_CATG
     */
    public String getVCH_CATG() {
        return VCH_CATG;
    }

    /**
     * @param VCH_CATG the VCH_CATG to set
     */
    public void setVCH_CATG(String VCH_CATG) {
        this.VCH_CATG = VCH_CATG;
    }

    /**
     * @return the VCH_TYPE
     */
    public Integer getVCH_TYPE() {
        return VCH_TYPE;
    }

    /**
     * @param VCH_TYPE the VCH_TYPE to set
     */
    public void setVCH_TYPE(Integer VCH_TYPE) {
        this.VCH_TYPE = VCH_TYPE;
    }

    /**
     * @return the REGN_TYPE
     */
    public String getREGN_TYPE() {
        return REGN_TYPE;
    }

    /**
     * @param REGN_TYPE the REGN_TYPE to set
     */
    public void setREGN_TYPE(String REGN_TYPE) {
        this.REGN_TYPE = REGN_TYPE;
    }

    /**
     * @return the VCH_AGE
     */
    public Integer getVCH_AGE() {
        return VCH_AGE;
    }

    /**
     * @param VCH_AGE the VCH_AGE to set
     */
    public void setVCH_AGE(Integer VCH_AGE) {
        this.VCH_AGE = VCH_AGE;
    }

    /**
     * @return the DELAY_DAYS
     */
    public Integer getDELAY_DAYS() {
        return DELAY_DAYS;
    }

    /**
     * @param DELAY_DAYS the DELAY_DAYS to set
     */
    public void setDELAY_DAYS(Integer DELAY_DAYS) {
        this.DELAY_DAYS = DELAY_DAYS;
    }

    /**
     * @return the DELAY_MONTHS
     */
    public Integer getDELAY_MONTHS() {
        return DELAY_MONTHS;
    }

    /**
     * @param DELAY_MONTHS the DELAY_MONTHS to set
     */
    public void setDELAY_MONTHS(Integer DELAY_MONTHS) {
        this.DELAY_MONTHS = DELAY_MONTHS;
    }

    /**
     * @return the DELAY_YEARS
     */
    public Integer getDELAY_YEARS() {
        return DELAY_YEARS;
    }

    /**
     * @param DELAY_YEARS the DELAY_YEARS to set
     */
    public void setDELAY_YEARS(Integer DELAY_YEARS) {
        this.DELAY_YEARS = DELAY_YEARS;
    }

    /**
     * @return the AUDIO_FITTED
     */
    public String getAUDIO_FITTED() {
        return AUDIO_FITTED;
    }

    /**
     * @param AUDIO_FITTED the AUDIO_FITTED to set
     */
    public void setAUDIO_FITTED(String AUDIO_FITTED) {
        this.AUDIO_FITTED = AUDIO_FITTED;
    }

    /**
     * @return the VIDEO_FITTED
     */
    public String getVIDEO_FITTED() {
        return VIDEO_FITTED;
    }

    /**
     * @param VIDEO_FITTED the VIDEO_FITTED to set
     */
    public void setVIDEO_FITTED(String VIDEO_FITTED) {
        this.VIDEO_FITTED = VIDEO_FITTED;
    }

    /**
     * @return the TRAILERS
     */
//    public List<Trailer> getTRAILERS() {
//        return TRAILERS;
//    }
    /**
     * @param TRAILERS the TRAILERS to set
     */
//    public void setTRAILERS(List<Trailer> TRAILERS) {
//        this.TRAILERS = TRAILERS;
//    }
    /**
     * @return the NO_OF_ROUTES
     */
    public Integer getNO_OF_ROUTES() {
        return NO_OF_ROUTES;
    }

    /**
     * @param NO_OF_ROUTES the NO_OF_ROUTES to set
     */
    public void setNO_OF_ROUTES(Integer NO_OF_ROUTES) {
        this.NO_OF_ROUTES = NO_OF_ROUTES;
    }

    /**
     * @return the NO_OF_REGIONS
     */
    public Integer getNO_OF_REGIONS() {
        return NO_OF_REGIONS;
    }

    /**
     * @param NO_OF_REGIONS the NO_OF_REGIONS to set
     */
    public void setNO_OF_REGIONS(Integer NO_OF_REGIONS) {
        this.NO_OF_REGIONS = NO_OF_REGIONS;
    }

    /**
     * @return the VCH_IMPORTED
     */
    public String getVCH_IMPORTED() {
        return VCH_IMPORTED;
    }

    /**
     * @param VCH_IMPORTED the VCH_IMPORTED to set
     */
    public void setVCH_IMPORTED(String VCH_IMPORTED) {
        this.VCH_IMPORTED = VCH_IMPORTED;
    }

    /**
     * @return the NEW_VCH
     */
    public String getNEW_VCH() {
        return NEW_VCH;
    }

    /**
     * @param NEW_VCH the NEW_VCH to set
     */
    public void setNEW_VCH(String NEW_VCH) {
        this.NEW_VCH = NEW_VCH;
    }

    public Integer getPMT_DAYS() {
        return PMT_DAYS;
    }

    public void setPMT_DAYS(Integer PMT_DAYS) {
        this.PMT_DAYS = PMT_DAYS;
    }

    public Integer getPMT_MONTHS() {
        return PMT_MONTHS;
    }

    public void setPMT_MONTHS(Integer PMT_MONTHS) {
        this.PMT_MONTHS = PMT_MONTHS;
    }

    public Integer getPMT_CEL_MONTH() {
        return PMT_CEL_MONTH;
    }

    public void setPMT_CEL_MONTH(Integer PMT_CEL_MONTH) {
        this.PMT_CEL_MONTH = PMT_CEL_MONTH;
    }

    public Integer getPMT_YEAR() {
        return PMT_YEAR;
    }

    public void setPMT_YEAR(Integer PMT_YEAR) {
        this.PMT_YEAR = PMT_YEAR;
    }

    public String getREGN_NO() {
        return REGN_NO;
    }

    public void setREGN_NO(String REGN_NO) {
        this.REGN_NO = REGN_NO;
    }

    public Integer getROUTE_COUNT() {
        return ROUTE_COUNT;
    }

    public void setROUTE_COUNT(Integer ROUTE_COUNT) {
        this.ROUTE_COUNT = ROUTE_COUNT;
    }

    public Integer getREGION_COUNT() {
        return REGION_COUNT;
    }

    public void setREGION_COUNT(Integer REGION_COUNT) {
        this.REGION_COUNT = REGION_COUNT;
    }

    /**
     * @return the NOC_RETENTION
     */
    public Integer getNOC_RETENTION() {
        return NOC_RETENTION;
    }

    /**
     * @param NOC_RETENTION the NOC_RETENTION to set
     */
    public void setNOC_RETENTION(Integer NOC_RETENTION) {
        this.NOC_RETENTION = NOC_RETENTION;
    }

    /**
     * @return the TMP_PURPOSE
     */
    public String getTMP_PURPOSE() {
        return TMP_PURPOSE;
    }

    /**
     * @param TMP_PURPOSE the TMP_PURPOSE to set
     */
    public void setTMP_PURPOSE(String TMP_PURPOSE) {
        this.TMP_PURPOSE = TMP_PURPOSE;
    }

    public Integer getEXEM_AMOUNT() {
        return EXEM_AMOUNT;
    }

    public void setEXEM_AMOUNT(Integer EXEM_AMOUNT) {
        this.EXEM_AMOUNT = EXEM_AMOUNT;
    }

    public Integer getFINE_TO_BE_TAKEN() {
        return FINE_TO_BE_TAKEN;
    }

    public void setFINE_TO_BE_TAKEN(Integer FINE_TO_BE_TAKEN) {
        this.FINE_TO_BE_TAKEN = FINE_TO_BE_TAKEN;
    }

    public String getMULTI_REGION() {
        return MULTI_REGION;
    }

    public void setMULTI_REGION(String MULTI_REGION) {
        this.MULTI_REGION = MULTI_REGION;
    }

    /**
     * @return the PARKING_FEE_ADVANCE_DAYS
     */
    public Integer getPARKING_FEE_ADVANCE_DAYS() {
        return PARKING_FEE_ADVANCE_DAYS;
    }

    /**
     * @param PARKING_FEE_ADVANCE_DAYS the PARKING_FEE_ADVANCE_DAYS to set
     */
    public void setPARKING_FEE_ADVANCE_DAYS(Integer PARKING_FEE_ADVANCE_DAYS) {
        this.PARKING_FEE_ADVANCE_DAYS = PARKING_FEE_ADVANCE_DAYS;
    }

    /**
     * @return the VEH_AGE_EXPIRED
     */
    public String getVEH_AGE_EXPIRED() {
        return VEH_AGE_EXPIRED;
    }

    /**
     * @param VEH_AGE_EXPIRED the VEH_AGE_EXPIRED to set
     */
    public void setVEH_AGE_EXPIRED(String VEH_AGE_EXPIRED) {
        this.VEH_AGE_EXPIRED = VEH_AGE_EXPIRED;
    }

    public Integer getMULTI_DOC() {
        return MULTI_DOC;
    }

    public void setMULTI_DOC(Integer MULTI_DOC) {
        this.MULTI_DOC = MULTI_DOC;
    }

    public String getVEHICLE_HYPTH() {
        return VEHICLE_HYPTH;
    }

    public void setVEHICLE_HYPTH(String VEHICLE_HYPTH) {
        this.VEHICLE_HYPTH = VEHICLE_HYPTH;
    }

    /**
     * @return the FIT_UPTO
     */
    public String getFIT_UPTO() {
        return FIT_UPTO;
    }

    /**
     * @param FIT_UPTO the FIT_UPTO to set
     */
    public void setFIT_UPTO(String FIT_UPTO) {
        this.FIT_UPTO = FIT_UPTO;
    }

    /**
     * @return the NORMS
     */
    public Integer getNORMS() {
        return NORMS;
    }

    /**
     * @param NORMS the NORMS to set
     */
    public void setNORMS(Integer NORMS) {
        this.NORMS = NORMS;
    }

    /**
     * @return the NET_TAX_AMT
     */
    public Integer getNET_TAX_AMT() {
        return NET_TAX_AMT;
    }

    /**
     * @param NET_TAX_AMT the NET_TAX_AMT to set
     */
    public void setNET_TAX_AMT(Integer NET_TAX_AMT) {
        this.NET_TAX_AMT = NET_TAX_AMT;
    }

    /**
     * @return the TAX_AMT
     */
    public Integer getTAX_AMT() {
        return TAX_AMT;
    }

    /**
     * @return the PENALTY
     */
    public Integer getPENALTY() {
        return PENALTY;
    }

    public Integer getGCW() {
        return GCW;
    }

    public void setGCW(Integer GCW) {
        this.GCW = GCW;
    }

    public Integer getOWN_CATG() {
        return OWN_CATG;
    }

    public void setOWN_CATG(Integer OWN_CATG) {
        this.OWN_CATG = OWN_CATG;
    }

    /**
     * @return the TO_REASON
     */
    public String getTO_REASON() {
        return TO_REASON;
    }

    /**
     * @param TO_REASON the TO_REASON to set
     */
    public void setTO_REASON(String TO_REASON) {
        this.TO_REASON = TO_REASON;
    }

    /**
     * @return the FIT_STATUS
     */
    public String getFIT_STATUS() {
        return FIT_STATUS;
    }

    /**
     * @param FIT_STATUS the FIT_STATUS to set
     */
    public void setFIT_STATUS(String FIT_STATUS) {
        this.FIT_STATUS = FIT_STATUS;
    }

    public Integer getPMT_AMOUNT() {
        return PMT_AMOUNT;
    }

    public void setPMT_AMOUNT(Integer PMT_AMOUNT) {
        this.PMT_AMOUNT = PMT_AMOUNT;
    }

    public String getEXCEM_FLAG() {
        return EXCEM_FLAG;
    }

    public void setEXCEM_FLAG(String EXCEM_FLAG) {
        this.EXCEM_FLAG = EXCEM_FLAG;
    }

    public String getOFFER_LETTER() {
        return OFFER_LETTER;
    }

    public void setOFFER_LETTER(String OFFER_LETTER) {
        this.OFFER_LETTER = OFFER_LETTER;
    }

    /**
     * @return the TRANSACTION_PUR_CD
     */
    public Integer getTRANSACTION_PUR_CD() {
        return TRANSACTION_PUR_CD;
    }

    /**
     * @param TRANSACTION_PUR_CD the TRANSACTION_PUR_CD to set
     */
    public void setTRANSACTION_PUR_CD(Integer TRANSACTION_PUR_CD) {
        this.TRANSACTION_PUR_CD = TRANSACTION_PUR_CD;
    }

    public String getDUP_PMT_REASON() {
        return DUP_PMT_REASON;
    }

    public void setDUP_PMT_REASON(String DUP_PMT_REASON) {
        this.DUP_PMT_REASON = DUP_PMT_REASON;
    }

    public String getONLINE_PERMIT() {
        return ONLINE_PERMIT;
    }

    public void setONLINE_PERMIT(String ONLINE_PERMIT) {
        this.ONLINE_PERMIT = ONLINE_PERMIT;
    }

    /**
     * @return the PAYMENT_DATE
     */
    public String getPAYMENT_DATE() {
        return PAYMENT_DATE;
    }

    /**
     * @param PAYMENT_DATE the PAYMENT_DATE to set
     */
    public void setPAYMENT_DATE(String PAYMENT_DATE) {
        this.PAYMENT_DATE = PAYMENT_DATE;
    }

    public String getPERMIT_EXPIRED() {
        return PERMIT_EXPIRED;
    }

    public void setPERMIT_EXPIRED(String PERMIT_EXPIRED) {
        this.PERMIT_EXPIRED = PERMIT_EXPIRED;
    }

    /**
     * @return the INSP_RQRD
     */
    public String getINSP_RQRD() {
        return INSP_RQRD;
    }

    /**
     * @param INSP_RQRD the INSP_RQRD to set
     */
    public void setINSP_RQRD(String INSP_RQRD) {
        this.INSP_RQRD = INSP_RQRD;
    }

    /**
     * @return the LOGIN_OFF_CD
     */
    public Integer getLOGIN_OFF_CD() {
        return LOGIN_OFF_CD;
    }

    /**
     * @param LOGIN_OFF_CD the LOGIN_OFF_CD to set
     */
    public void setLOGIN_OFF_CD(Integer LOGIN_OFF_CD) {
        this.LOGIN_OFF_CD = LOGIN_OFF_CD;
    }

    /**
     * @return the APPLICANT_TYPE
     */
    public String getAPPLICANT_TYPE() {
        return APPLICANT_TYPE;
    }

    /**
     * @param APPLICANT_TYPE the APPLICANT_TYPE to set
     */
    public void setAPPLICANT_TYPE(String APPLICANT_TYPE) {
        this.APPLICANT_TYPE = APPLICANT_TYPE;
    }

    /**
     * @return the APPLICATION_TYPE
     */
    public String getAPPLICATION_TYPE() {
        return APPLICATION_TYPE;
    }

    /**
     * @param APPLICATION_TYPE the APPLICATION_TYPE to set
     */
    public void setAPPLICATION_TYPE(String APPLICATION_TYPE) {
        this.APPLICATION_TYPE = APPLICATION_TYPE;
    }

    /**
     * @return the PREV_OFF_CD
     */
    public int getPREV_OFF_CD() {
        return PREV_OFF_CD;
    }

    /**
     * @param PREV_OFF_CD the PREV_OFF_CD to set
     */
    public void setPREV_OFF_CD(int PREV_OFF_CD) {
        this.PREV_OFF_CD = PREV_OFF_CD;
    }

    public String getREGION_DESCR() {
        return REGION_DESCR;
    }

    public void setREGION_DESCR(String REGION_DESCR) {
        this.REGION_DESCR = REGION_DESCR;
    }

    /**
     * @return the PURCHASE_DATE
     */
    public String getPURCHASE_DATE() {
        return PURCHASE_DATE;
    }

    /**
     * @param PURCHASE_DATE the PURCHASE_DATE to set
     */
    public void setPURCHASE_DATE(String PURCHASE_DATE) {
        this.PURCHASE_DATE = PURCHASE_DATE;
    }

    public String getREGN_UPTO() {
        return REGN_UPTO;
    }

    public void setREGN_UPTO(String REGN_UPTO) {
        this.REGN_UPTO = REGN_UPTO;
    }

    /**
     * @return the OWNER_CATG
     */
    public Integer getOWNER_CATG() {
        return OWNER_CATG;
    }

    /**
     * @param OWNER_CATG the OWNER_CATG to set
     */
    public void setOWNER_CATG(Integer OWNER_CATG) {
        this.OWNER_CATG = OWNER_CATG;
    }

    public Integer getDAYSWITHINSTATE() {
        return DAYSWITHINSTATE;
    }

    public void setDAYSWITHINSTATE(Integer DAYSWITHINSTATE) {
        this.DAYSWITHINSTATE = DAYSWITHINSTATE;
    }

    public Integer getBLACKLISTCODE() {
        return BLACKLISTCODE;
    }

    public void setBLACKLISTCODE(Integer BLACKLISTCODE) {
        this.BLACKLISTCODE = BLACKLISTCODE;
    }

    /**
     * @return the MANUFACTUR_DATE
     */
    public String getMANUFACTUR_DATE() {
        return MANUFACTUR_DATE;
    }

    /**
     * @param MANUFACTUR_DATE the MANUFACTUR_DATE to set
     */
    public void setMANUFACTUR_DATE(String MANUFACTUR_DATE) {
        this.MANUFACTUR_DATE = MANUFACTUR_DATE;
    }

    public Integer getAUTHYEAR() {
        return AUTHYEAR;
    }

    public void setAUTHYEAR(Integer AUTHYEAR) {
        this.AUTHYEAR = AUTHYEAR;
    }

    /**
     * @return the APPL_DATE
     */
    public String getAPPL_DATE() {
        return APPL_DATE;
    }

    /**
     * @param APPL_DATE the APPL_DATE to set
     */
    public void setAPPL_DATE(String APPL_DATE) {
        this.APPL_DATE = APPL_DATE;
    }

    public Integer getACTION_CD() {
        return ACTION_CD;
    }

    public void setACTION_CD(Integer ACTION_CD) {
        this.ACTION_CD = ACTION_CD;
    }

    public Integer getOLD_VCH_TYPE() {
        return OLD_VCH_TYPE;
    }

    public void setOLD_VCH_TYPE(Integer OLD_VCH_TYPE) {
        this.OLD_VCH_TYPE = OLD_VCH_TYPE;
    }

    public Integer getOFF_TYPE_CD() {
        return OFF_TYPE_CD;
    }

    public void setOFF_TYPE_CD(Integer OFF_TYPE_CD) {
        this.OFF_TYPE_CD = OFF_TYPE_CD;
    }

    public Integer getSTATE_COUNT() {
        return STATE_COUNT;
    }

    public void setSTATE_COUNT(Integer STATE_COUNT) {
        this.STATE_COUNT = STATE_COUNT;
    }

    public String getTRIPEXTENDED() {
        return TRIPEXTENDED;
    }

    public void setTRIPEXTENDED(String TRIPEXTENDED) {
        this.TRIPEXTENDED = TRIPEXTENDED;
    }

    public String getISPAYMENTDONE() {
        return ISPAYMENTDONE;
    }

    public void setISPAYMENTDONE(String ISPAYMENTDONE) {
        this.ISPAYMENTDONE = ISPAYMENTDONE;
    }

    public String getREGN_STATE() {
        return REGN_STATE;
    }

    public void setREGN_STATE(String REGN_STATE) {
        this.REGN_STATE = REGN_STATE;
    }

}
