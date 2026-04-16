package org.rmj.gocas.pojo;

import java.util.ArrayList;
import org.json.simple.JSONObject;

public class MeansEmployed implements JEntity{
    private JSONObject poJSON;
    private String psMessage = "";
    private final ArrayList paKeys;
    
    public MeansEmployed(){
        poJSON = new JSONObject();        
        poJSON.put("cEmpSectr", "");
        poJSON.put("sIndstWrk", "");
        poJSON.put("sEmployer", "");
        poJSON.put("sWrkAddrx", "");
        poJSON.put("sWrkTownx", "");
        poJSON.put("sPosition", "");
        poJSON.put("sFunction", "");
        poJSON.put("cEmpStatx", "");
        poJSON.put("nLenServc", 0.00);
        poJSON.put("nSalaryxx", 0.00);
        poJSON.put("sWrkTelno", "");
        
        //If Employer Sector is Public
        poJSON.put("cUniforme", "");
        poJSON.put("cMilitary", "");
        poJSON.put("cGovtLevl", "");
        //If Employer Sector is Private
        poJSON.put("cCompLevl", "");
        poJSON.put("cEmpLevlx", "");
        //If Employer Sector is OFW
        poJSON.put("cOcCatgry", "");
        poJSON.put("cOFWRegnx", "");
        poJSON.put("sOFWNatnx", "");
        
        paKeys = new ArrayList();
        paKeys.add("cEmpSectr");
        paKeys.add("sIndstWrk");
        paKeys.add("sEmployer");
        paKeys.add("sWrkAddrx");
        paKeys.add("sWrkTownx");
        paKeys.add("sPosition");
        paKeys.add("sFunction");
        paKeys.add("cEmpStatx");
        paKeys.add("nLenServc");
        paKeys.add("nSalaryxx");
        paKeys.add("sWrkTelno");
    }
    
    @Override
    public boolean setData(JSONObject foData) {
        /*Java 8*/
        /*if (!paKeys.stream().noneMatch((key) -> (!poJSON.containsKey(key)))) {
            psMessage = "JSONObject missing key detected. Please inform MIS-SEG.";
            return false;
        }*/
        
        /*Java 7*/
        for (int lnCtr = 0; lnCtr <= paKeys.size()-1; lnCtr ++){
            if (!poJSON.containsKey(paKeys.get(lnCtr))){
                psMessage = "JSONObject missing key detected. Please inform MIS-SEG.";
                return false;
            }
        }
        
        if (null == (String) foData.get("cEmpSectr")){
            psMessage = "Employee sector is null. Please inform MIS-SEG.";
            return false;
        } else switch ((String) poJSON.get("cEmpSectr")) {
            case "0":                
                if (!foData.containsKey("cUniforme") ||
                    !foData.containsKey("cMilitary") ||
                    !foData.containsKey("cGovtLevl") ||
                    !foData.containsKey("cEmpLevlx")){
                    psMessage = "JSONObject missing key detected. Please inform MIS-SEG.";
                    return false;
                } break;
            case "1":
                if (!foData.containsKey("cCompLevl") ||
                    !foData.containsKey("cEmpLevlx")){
                    psMessage = "JSONObject missing key detected. Please inform MIS-SEG.";
                    return false;
                } break;
            case "2":
                if (!foData.containsKey("cOcCatgry") ||
                    !foData.containsKey("cOFWRegnx") ||
                    !foData.containsKey("sOFWNatnx")){
                    psMessage = "JSONObject missing key detected. Please inform MIS-SEG.";
                    return false;
                }
        }

        poJSON = foData;
        return true;
    }

    @Override
    public JSONObject toJSON() {                
        if (null == (String) poJSON.get("cEmpSectr")){
            poJSON.remove("cUniforme");
            poJSON.remove("cMilitary");
            poJSON.remove("cGovtLevl");
            poJSON.remove("cCompLevl");
            poJSON.remove("cEmpLevlx");
            poJSON.remove("cOcCatgry");
            poJSON.remove("cOFWRegnx");
            poJSON.remove("sOFWNatnx");
        } else switch ((String) poJSON.get("cEmpSectr")) {
            case "0":
                poJSON.remove("cCompLevl");
                poJSON.remove("cOcCatgry");
                poJSON.remove("cOFWRegnx");
                poJSON.remove("sOFWNatnx");
                break;
            case "1":
                poJSON.remove("cUniforme");
                poJSON.remove("cMilitary");
                poJSON.remove("cGovtLevl");
                poJSON.remove("cOcCatgry");
                poJSON.remove("sOFWNatnx");
                break;
            case "2":
                poJSON.remove("cUniforme");
                poJSON.remove("cMilitary");
                poJSON.remove("cGovtLevl");
                poJSON.remove("cCompLevl");
                poJSON.remove("cEmpLevlx");
                break;
            default:
                poJSON.remove("cUniforme");
                poJSON.remove("cMilitary");
                poJSON.remove("cGovtLevl");
                poJSON.remove("cCompLevl");
                poJSON.remove("cEmpLevlx");
                poJSON.remove("cOcCatgry");
                poJSON.remove("cOFWRegnx");
                poJSON.remove("sOFWNatnx");
                break;
        }
        
        return poJSON;
    }
    
    @Override
    public String toJSONString() {
        if (poJSON == null) return "";
        return poJSON.toJSONString();
    }
    
    @Override
    public String getMessage() {
        return psMessage;
    }
    
    /**
     * setEmploymentSector(String fsValue)
     * 
     * @param fsValue
     * 
     * char value for applicant's sector employer sector. <br>
     * 
     * 0 - Public <br>
     * 1 - Private <br>
     * 2 - OFW
     */
    public void setEmploymentSector(String fsValue){
        poJSON.remove("cEmpSectr");
        poJSON.put("cEmpSectr", fsValue);
    }
    /**
     * getEmploymentSector()
     * 
     * @return char value for applicant's sector employer sector. <br>
     * 
     * 0 - Public <br>
     * 1 - Private <br>
     * 2 - OFW
     */
    public String getEmploymentSector(){
        return (String) poJSON.get("cEmpSectr");
    }
    
    /**
     * setNatureofBusiness(String fsValue)
     * 
     * @param fsValue
     * 
     * applicant's employer nature of business.
     */
    public void setNatureofBusiness(String fsValue){
        poJSON.remove("sIndstWrk");
        poJSON.put("sIndstWrk", fsValue);
    }
    /**
     * getNatureofBusiness()
     * 
     * @return applicant's employer nature of business.
     */
    public String getNatureofBusiness(){
        return (String) poJSON.get("sIndstWrk");
    }
    
    /**
     * setCompanyName(String fsValue)
     * 
     * @param fsValue
     * 
     * applicant's employer/company name.
     */
    public void setCompanyName(String fsValue){
        poJSON.remove("sEmployer");
        poJSON.put("sEmployer", fsValue);
    }
    /**
     * getCompanyName()
     * 
     * @return applicant's employer/company name.
     */
    public String getCompanyName(){
        return (String) poJSON.get("sEmployer");
    }
    
    /**
     * setCompanyAddress(String fsValue)
     * 
     * @param fsValue
     * 
     * applicant's company address.
     */
    public void setCompanyAddress(String fsValue){
        poJSON.remove("sWrkAddrx");
        poJSON.put("sWrkAddrx", fsValue);
    }
    /**
     * getCompanyAddress()
     * 
     * @return applicant's company address.
     */
    public String getCompanyAddress(){
        return (String) poJSON.get("sWrkAddrx");
    }
    
    /**
     * setCompanyTown(String fsValue)
     * 
     * @param fsValue
     * 
     * applicant's company town.
     */
    public void setCompanyTown(String fsValue){
        poJSON.remove("sWrkTownx");
        poJSON.put("sWrkTownx", fsValue);
    }
    /**
     * getCompanyTown()
     * 
     * @return applicant's company town.
     */
    public String getCompanyTown(){
        return (String) poJSON.get("sWrkTownx");  
    }
    
    /**
     * setPosition(String fsValue)
     * 
     * @param fsValue
     * 
     * applicant's position in the company.
     */
    public void setPosition(String fsValue){
        poJSON.remove("sPosition");
        poJSON.put("sPosition", fsValue);
    }
    /**
     * getPosition()
     * 
     * @return applicant's position in the company.
     */
    public String getPosition(){
        return (String) poJSON.get("sPosition");  
    }
    
    /**
     * setJobDescription(String fsValue)
     * 
     * @param fsValue
     * 
     * applicant's specific job description for his position.
     */
    public void setJobDescription(String fsValue){
        poJSON.remove("sFunction");
        poJSON.put("sFunction", fsValue);
    }
    /**
     * getJobDescription()
     * 
     * @return applicant's specific job description for his position.
     */
    public String getJobDescription(){
        return (String) poJSON.get("sFunction");  
    }
    
    /**
     * setEmployeeStatus(String fsValue)
     * 
     * @param fsValue
     * 
     * char value for applicant's employment status. <br>
     * 
     * R - Regular <br>
     * P - Probationary <br>
     * C - Contractual <br>
     * S - Seasonal
     */
    public void setEmployeeStatus(String fsValue){
        poJSON.remove("cEmpStatx");
        poJSON.put("cEmpStatx", fsValue);
    }
    /**
     * getEmployeeStatus()
     * 
     * @return char value for applicant's employment status. <br>
     * 
     * R - Regular <br>
     * P - Probationary <br>
     * C - Contractual <br>
     * S - Seasonal
     */
    public String getEmployeeStatus(){
        return (String) poJSON.get("cEmpStatx");  
    }
    
    /**
     * setLengthOfService(double fnValue)
     * 
     * @param fnValue
     * 
     * applicant's length of service in the company (in years).
     */
    public void setLengthOfService(double fnValue){
        poJSON.remove("nLenServc");
        poJSON.put("nLenServc", fnValue);
    }
    /**
     * getLengthOfService()
     * 
     * @return applicant's length of service in the company (in years).
     */
    public String getLengthOfService(){
        return String.valueOf(poJSON.get("nLenServc"));  
    }
    
    /**
     * setSalary(long fnValue)
     * 
     * @param fnValue
     * 
     * applicant's salary.
     */
    public void setSalary(long fnValue){
        poJSON.remove("nSalaryxx");
        poJSON.put("nSalaryxx", fnValue);
    }
    /**
     * getSalary()
     * 
     * @return salary.
     */
    public String getSalary(){
        return String.valueOf(poJSON.get("nSalaryxx"));  
    }
    
    /**
     * setCompanyNo(String fsValue)
     * 
     * @param fsValue
     * 
     * applicant's company landline/mobile number.
     */
    public void setCompanyNo(String fsValue){
        poJSON.remove("sWrkTelno");
        poJSON.put("sWrkTelno", fsValue);
    }
    /**
     * getCompanyNo()
     * 
     * @return applicant's company landline/mobile number.
     */
    public String getCompanyNo(){
        return (String) poJSON.get("sWrkTelno");
    }
    
    /**
     * IsUniformedPersonel(String fsValue)
     * 
     * @param fsValue 
     * 
     * char value if the customer is a uniformed personnel. <br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public void IsUniformedPersonel(String fsValue){
        if ("0".equals((String) poJSON.get("cEmpSectr"))){
            poJSON.remove("cUniforme");
            poJSON.put("cUniforme", fsValue);
        } else {
            poJSON.remove("cUniforme");
            poJSON.put("cUniforme", "");
        }
    }
    /**
     * IsUniformedPersonel()
     * 
     * @return char value if the customer is a uniformed personnel. <br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public String IsUniformedPersonel(){
        return (String) poJSON.get("cUniforme");
    }
    
    /**
     * IsMilitaryPersonel(String fsValue)
     * 
     * @param fsValue 
     * 
     * char value if the customer is a military personnel. <br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public void IsMilitaryPersonel(String fsValue){
        if ("0".equals((String) poJSON.get("cEmpSectr"))){
            poJSON.remove("cMilitary");
            poJSON.put("cMilitary", fsValue);
        } else {
            poJSON.remove("cMilitary");
            poJSON.put("cMilitary", "");
        }
    }
    /**
     * IsMilitaryPersonel()
     * 
     * @return char value if the customer is a military personnel. <br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public String IsMilitaryPersonel(){
        return (String) poJSON.get("cMilitary");
    }
    
    /**
     * setGovernmentLevel(String fsValue)
     * 
     * @param fsValue 
     * 
     * char value for government level. <br>
     * 
     * 0 - LGU <br>
     * 1 - Provincial <br>
     * 2 - National
     */    
    public void setGovernmentLevel(String fsValue){
        if ("0".equals((String) poJSON.get("cEmpSectr"))){
            poJSON.remove("cGovtLevl");
            poJSON.put("cGovtLevl", fsValue);
        }else {
            poJSON.remove("cGovtLevl");
            poJSON.put("cGovtLevl", "");
        }
    }
    /**
     * getGovernmentLevel()
     * 
     * @return char value for government level. <br>
     * 
     * 0 - LGU <br>
     * 1 - Provincial <br>
     * 2 - National
     */
    public String getGovernmentLevel(){
        return (String) poJSON.get("cGovtLevl");
    }
    
    /**
     * setCompanyLevel(String fsValue)
     * 
     * @param fsValue 
     * 
     * char value for company level. <br>
     * 
     * 0 - Local <br>
     * 1 - National <br>
     * 2 - Multi-National
     */
    public void setCompanyLevel(String fsValue){
        if ("1".equals((String) poJSON.get("cEmpSectr"))){
            poJSON.remove("cCompLevl");
            poJSON.put("cCompLevl", fsValue);
        } else {
            poJSON.remove("cCompLevl");
            poJSON.put("cCompLevl", "");
        }
    }
    /**
     * getCompanyLevel()
     * 
     * @return char value for company level. <br>
     * 
     * 0 - Local <br>
     * 1 - National <br>
     * 2 - Multi-National
     */
    public String getCompanyLevel(){
        return (String) poJSON.get("cCompLevl");
    }
    
    /**
     * setEmployeeLevel(String fsValue)
     * 
     * @param fsValue 
     * 
     * char value for employee level. <br>
     * 
     * 0 - Rank and File <br>
     * 1 - Supervisory <br>
     * 2 - Managerial
     */
    public void setEmployeeLevel(String fsValue){
        if ("1".equals((String) poJSON.get("cEmpSectr"))){
            poJSON.remove("cEmpLevlx");
            poJSON.put("cEmpLevlx", fsValue);
        } else {
            poJSON.remove("cEmpLevlx");
            poJSON.put("cEmpLevlx", "");
        }
    }
    /**
     * getEmployeeLevel()
     * 
     * @return char value for employee level. <br>
     * 
     * 0 - Rank and File <br>
     * 1 - Supervisory <br>
     * 2 - Managerial
     */
    public String getEmployeeLevel(){
        return (String) poJSON.get("cEmpLevlx");
    }
    
    /**
     * setOFWCategory(String fsValue)
     * 
     * @param fsValue 
     * 
     * char value for OFW category. <br>
     * 
     * 0 - Household Services <br>
     * 1 - Non-Technical <br>
     * 2 - Skilled/Professional
     */
    public void setOFWCategory(String fsValue){
        if ("2".equals((String) poJSON.get("cEmpSectr"))){
            poJSON.remove("cOcCatgry");
            poJSON.put("cOcCatgry", fsValue);
        } else {
            poJSON.remove("cOcCatgry");
            poJSON.put("cOcCatgry", "");
        }
    }
    /**
     * getOFWCategory()
     * 
     * @return * char value for OFW category. <br>
     * 
     * 0 - Household Services <br>
     * 1 - Non-Technical <br>
     * 2 - Skilled/Professional
     */
    public String getOFWCategory(){
        return (String) poJSON.get("cOcCatgry");
    }
    
    /**
     * setOFWRegion(String fsValue)
     * 
     * @param fsValue 
     * 
     * char value for overseas region. <br>
     * 
     * 0 - America <br>
     * 1 - Europe <br>
     * 2 - Oceana  <br>
     * 3 - Asia <br>
     * 4 - Others
     */
    public void setOFWRegion(String fsValue){
        if ("2".equals((String) poJSON.get("cEmpSectr"))){
            poJSON.remove("cOFWRegnx");
            poJSON.put("cOFWRegnx", fsValue);
        } else {
            poJSON.remove("cOFWRegnx");
            poJSON.put("cOFWRegnx", "");
        }
    }
    /**
     * getOFWRegion()
     * 
     * @return char value for overseas region. <br>
     * 
     * 0 - America <br>
     * 1 - Europe <br>
     * 2 - Oceana  <br>
     * 3 - Asia <br>
     * 4 - Others
     */
    public String getOFWRegion(){
        return (String) poJSON.get("cOFWRegnx");
    }
    
    /**
     * setOFWNation(String fsValue)
     * 
     * @param fsValue 
     * 
     * overseas country.
     */
    public void setOFWNation(String fsValue){
        if ("2".equals((String) poJSON.get("cEmpSectr"))){
            poJSON.remove("sOFWNatnx");
            poJSON.put("sOFWNatnx", fsValue);
        } else {
            poJSON.remove("sOFWNatnx");
            poJSON.put("sOFWNatnx", "");
        }
    }
    /**
     * getOFWNation()
     * 
     * @return overseas country.
     */
    public String getOFWNation(){
        return (String) poJSON.get("sOFWNatnx");
    }
}
