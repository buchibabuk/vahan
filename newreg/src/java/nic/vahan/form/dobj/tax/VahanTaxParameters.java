/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.tax;

/**
 *
 * @author DELL
 */
public class VahanTaxParameters extends VahanTaxParametersServer {

    private Integer SEAT_CAP = 0;
    private Integer UNLD_WT = 0;
    private Integer LD_WT = 0;
    private Integer GCW = 0;
    private String REGN_DATE;
    private String PAYMENT_DATE;
    private String PURCHASE_DATE;
    private Integer STAND_CAP = 0;
    private Integer SLEEPAR_CAP = 0;
    private Float FLOOR_AREA = 0.0f;
    private Integer SALE_AMT = 0;
    private Integer IDV = 0;
    private Integer OWNER_CD = 0;
    private Integer FUEL = 0;
    private Integer VH_CLASS = 0;
    private Float HP = 0.0f;
    private Float CC = 0.0f;
    private String VEH_PURCHASE_AS = "B";
    private String AC_FITTED = "N";
    private String AUDIO_FITTED = "N";
    private String VIDEO_FITTED = "N";
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
    private Integer OFF_CD = 0;
    private Integer PUR_CD = 0;
    private String TAX_MODE = "";
    private String REGN_TYPE = "N";
    private String VCH_CATG = "";
    private Integer TAX_MODE_NO_ADV = 1;
//    private boolean CALENDAR_MONTH;;
    private String TAX_DUE_FROM_DATE;
    //private List<Trailer> TRAILERS;
    private Integer NO_OF_ROUTES = 0;
    private Integer NO_OF_REGIONS = 0;
    private Integer VCH_TYPE = 0;
    private String VCH_IMPORTED = "N";
    private String NEW_VCH = "N";
    private Integer PM_SALE_AMT = 0;
    private Integer NO_OF_TYRES = 0;
    private Integer OWNER_CD_DEPT = 0;
    private Integer TRANSACTION_PUR_CD = 0;
    private Integer NO_OF_ATTACHED_TRAILERS = 0;
    private Integer NO_OF_AXLE = 0;
    private Integer LOGIN_OFF_CD = 0;
    private String TAX_CEASE_DATE;
    private Integer NORMS = 0;
//    private Map mapTagFields = new HashMap<String, String>();
//    private ArrayList arrTaxDuePeriods = new ArrayList();

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

//    /**
//     * @return the TRAILERS
//     */
//    public List<Trailer> getTRAILERS() {
//        return TRAILERS;
//    }
//
//    /**
//     * @param TRAILERS the TRAILERS to set
//     */
//    public void setTRAILERS(List<Trailer> TRAILERS) {
//        this.TRAILERS = TRAILERS;
//    }
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

    /**
     * @return the TAX_MODE_NO_ADV
     */
    public Integer getTAX_MODE_NO_ADV() {
        return TAX_MODE_NO_ADV;
    }

    /**
     * @param TAX_MODE_NO_ADV the TAX_MODE_NO_ADV to set
     */
    public void setTAX_MODE_NO_ADV(Integer TAX_MODE_NO_ADV) {
        this.TAX_MODE_NO_ADV = TAX_MODE_NO_ADV;
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

    /**
     * @return the PM_SALE_AMT
     */
    public Integer getPM_SALE_AMT() {
        return PM_SALE_AMT;
    }

    /**
     * @param PM_SALE_AMT the PM_SALE_AMT to set
     */
    public void setPM_SALE_AMT(Integer PM_SALE_AMT) {
        this.PM_SALE_AMT = PM_SALE_AMT;
    }

    /**
     * @return the IDV
     */
    public Integer getIDV() {
        return IDV;
    }

    /**
     * @param IDV the IDV to set
     */
    public void setIDV(Integer IDV) {
        this.IDV = IDV;
    }

    /**
     * @return the GCW
     */
    public Integer getGCW() {
        return GCW;
    }

    /**
     * @param GCW the GCW to set
     */
    public void setGCW(Integer GCW) {
        this.GCW = GCW;
    }

    /**
     * @return the NO_OF_TYRES
     */
    public Integer getNO_OF_TYRES() {
        return NO_OF_TYRES;
    }

    /**
     * @param NO_OF_TYRES the NO_OF_TYRES to set
     */
    public void setNO_OF_TYRES(Integer NO_OF_TYRES) {
        this.NO_OF_TYRES = NO_OF_TYRES;
    }

    /**
     * @return the OWNER_CD_DEPT
     */
    public Integer getOWNER_CD_DEPT() {
        return OWNER_CD_DEPT;
    }

    /**
     * @param OWNER_CD_DEPT the OWNER_CD_DEPT to set
     */
    public void setOWNER_CD_DEPT(Integer OWNER_CD_DEPT) {
        this.OWNER_CD_DEPT = OWNER_CD_DEPT;
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

    /**
     * @return the NO_OF_ATTACHED_TRAILERS
     */
    public Integer getNO_OF_ATTACHED_TRAILERS() {
        return NO_OF_ATTACHED_TRAILERS;
    }

    /**
     * @param NO_OF_ATTACHED_TRAILERS the NO_OF_ATTACHED_TRAILERS to set
     */
    public void setNO_OF_ATTACHED_TRAILERS(Integer NO_OF_ATTACHED_TRAILERS) {
        this.NO_OF_ATTACHED_TRAILERS = NO_OF_ATTACHED_TRAILERS;
    }

    public String getPURCHASE_DATE() {
        return PURCHASE_DATE;
    }

    public void setPURCHASE_DATE(String PURCHASE_DATE) {
        this.PURCHASE_DATE = PURCHASE_DATE;
    }

    /**
     * @return the NO_OF_AXLE
     */
    public Integer getNO_OF_AXLE() {
        return NO_OF_AXLE;
    }

    /**
     * @param NO_OF_AXLE the NO_OF_AXLE to set
     */
    public void setNO_OF_AXLE(Integer NO_OF_AXLE) {
        this.NO_OF_AXLE = NO_OF_AXLE;
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
     * @return the TAX_CEASE_DATE
     */
    public String getTAX_CEASE_DATE() {
        return TAX_CEASE_DATE;
    }

    /**
     * @param TAX_CEASE_DATE the TAX_CEASE_DATE to set
     */
    public void setTAX_CEASE_DATE(String TAX_CEASE_DATE) {
        this.TAX_CEASE_DATE = TAX_CEASE_DATE;
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
}
