package org.rmj.gocas.pojo;

import java.util.ArrayList;
import org.json.simple.JSONObject;

public class MeansSelfEmployed implements JEntity{
    private JSONObject poJSON;
    private String psMessage = "";
    private final ArrayList paKeys;
    
    public MeansSelfEmployed(){
        poJSON = new JSONObject();        
        poJSON.put("sIndstBus", "");
        poJSON.put("sBusiness", "");
        poJSON.put("sBusAddrx", "");
        poJSON.put("sBusTownx", "");
        poJSON.put("cBusTypex", "");
        poJSON.put("nBusLenxx", 0.00);
        poJSON.put("nBusIncom", 0.00);
        poJSON.put("nMonExpns", 0.00);
        poJSON.put("cOwnSizex", ""); //2019.12.12
        
        paKeys = new ArrayList();
        paKeys.add("sIndstBus");
        paKeys.add("sBusiness");
        paKeys.add("sBusAddrx");
        paKeys.add("sBusTownx");
        paKeys.add("cBusTypex");
        paKeys.add("nBusLenxx");
        paKeys.add("nBusIncom");
        paKeys.add("nMonExpns");
        paKeys.add("cOwnSizex"); //2019.12.12
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

        poJSON = foData;
        return true;
    }

    @Override
    public JSONObject toJSON() {        
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
     * setNatureOfBusiness(String fsValue)
     * 
     * @param fsValue
     * 
     * applicant's nature of business.
     */
    public void setNatureOfBusiness(String fsValue){
        poJSON.remove("sIndstBus");
        poJSON.put("sIndstBus", fsValue);
    }
    /**
     * getNatureOfBusiness()
     * 
     * @return applicant's nature of business.
     */
    public String getNatureOfBusiness(){
        return (String) poJSON.get("sIndstBus");
    }
    
    /**
     * setNameOfBusiness(String fsValue)
     * 
     * @param fsValue
     * 
     * applicant's name of business.
     */
    public void setNameOfBusiness(String fsValue){
        poJSON.remove("sBusiness");
        poJSON.put("sBusiness", fsValue);
    }
    /**
     * getNameOfBusiness()
     * 
     * @return applicant's name of business.
     */
    public String getNameOfBusiness(){
        return (String) poJSON.get("sBusiness");
    }
    
    /**
     * setBusinessAddress(String fsValue)
     * 
     * @param fsValue
     * 
     * applicant's business address.
     */
    public void setBusinessAddress(String fsValue){
        poJSON.remove("sBusAddrx");
        poJSON.put("sBusAddrx", fsValue);
    }
    /**
     * getBusinessAddress()
     * 
     * @return applicant's business address.
     */
    public String getBusinessAddress(){
        return (String) poJSON.get("sBusAddrx");
    }
    
    /**
     * setBusinessTown(String fsValue)
     * 
     * @param fsValue
     * 
     * applicant's business town.
     */
    public void setCompanyTown(String fsValue){
        poJSON.remove("sBusTownx");
        poJSON.put("sBusTownx", fsValue);
    }
    /**
     * getBusinessTown()
     * 
     * @return applicant's business town.
     */
    public String getBusinessTown(){
        return (String) poJSON.get("sBusTownx");  
    }
    
    /**
     * setBusinessType(String fsValue)
     * 
     * @param fsValue
     * 
     * char value for applicant's business type. <br>
     * 
     * 0 - Sole Proprietorship
     * 1 - Partnership
     * 2 - Corporation
     */
    public void setBusinessType(String fsValue){
        poJSON.remove("cBusTypex");
        poJSON.put("cBusTypex", fsValue);
    }
    /**
     * getBusinessType()
     * 
     * @return char value for applicant's business type. <br>
     * 
     * 0 - Sole Proprietorship
     * 1 - Partnership
     * 2 - Corporation
     */
    public String getBusinessType(){
        return (String) poJSON.get("cBusTypex");  
    }
    
    /**
     * setLengthOfService(double fnValue)
     * 
     * @param fnValue
     * 
     * applicant's business age (in years).
     */
    public void setBusinessLength(double fnValue){
        poJSON.remove("nBusLenxx");
        poJSON.put("nBusLenxx", fnValue);
    }
    /**
     * getLengthOfService()
     * 
     * @return applicant's business age (in years).
     */
    public String getBusinessLength(){
        return (String) poJSON.get("nBusLenxx");  
    }
    
    /**
     * setIncome(long fnValue)
     * 
     * @param fnValue
     * 
     * applicant's business income.
     */
    public void setIncome(long fnValue){
        poJSON.remove("nBusIncom");
        poJSON.put("nBusIncom", fnValue);
    }
    /**
     * getIncome()
     * 
     * @return business income.
     */
    public String getIncome(){
        return (String) poJSON.get("nBusIncom");  
    }
    
    /**
     * setMonthlyExpense(long fnValue)
     * 
     * @param fnValue
     * 
     * applicant's business monthly expense.
     */
    public void setMonthlyExpense(long fnValue){
        poJSON.remove("nMonExpns");
        poJSON.put("nMonExpns", fnValue);
    }
    /**
     * getMonthlyExpense()
     * 
     * @return business monthly expense.
     */
    public String getMonthlyExpense(){
        return (String) poJSON.get("nMonExpns");  
    }
    
    /**
     * setOwnershipSize(String fsValue)
     * 
     * @param fsValue 
     * 
     * char value for business ownership size<br>
     * 
     * 0 - Micro(less than 100K) <br>
     * 1 - Small(less than 300K) <br>
     * 2 - Medium(less than 1M) <br>
     * 3 - Large(greater than 1M)
     */
    public void setOwnershipSize(String fsValue){
        poJSON.remove("");
        poJSON.put("cOwnSizex", fsValue);
    }
    /**
     * getOwnershipSize()
     * 
     * @return char value for business ownership size<br>
     * 
     * 0 - Micro(less than 100K) <br>
     * 1 - Small(less than 300K) <br>
     * 2 - Medium(less than 1M) <br>
     * 3 - Large(greater than 1M)
     */
    public String getOwnershipSize(){
        return (String) poJSON.get("cOwnSizex");
    }
}
