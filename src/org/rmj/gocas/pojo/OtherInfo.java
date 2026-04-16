package org.rmj.gocas.pojo;

import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class OtherInfo implements JEntity{
    private JSONObject poJSON;
    private String psMessage = "";
    private final ArrayList paKeys;
    
    private JSONArray paReference;
    public OtherInfo(){
        poJSON = new JSONObject();        
        poJSON.put("sUnitUser", "");
        
        poJSON.put("sUnitUser", "");
        poJSON.put("sUsr2Buyr", "");
        poJSON.put("sPurposex", "");
        poJSON.put("sUnitPayr", "");
        poJSON.put("sPyr2Buyr", "");
        poJSON.put("sSrceInfo", "");
        poJSON.put("personal_reference", "");

        paKeys = new ArrayList();
        paKeys.add("sUnitUser");
        paKeys.add("sUsr2Buyr"); //if Others
        paKeys.add("sPurposex");
        paKeys.add("sUnitPayr");
        paKeys.add("sPyr2Buyr"); //if Others
        paKeys.add("sSrceInfo");
        paKeys.add("personal_reference");
        
        paReference = new JSONArray();
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
        
        paReference = (JSONArray) poJSON.get("personal_reference");
        return true;
    }

    @Override
    public JSONObject toJSON() {    
        if (!"1".equals((String) poJSON.get("sUnitUser"))) poJSON.remove("sUsr2Buyr");
        if (!"1".equals((String) poJSON.get("sUnitPayr"))) poJSON.remove("sPyr2Buyr");
        
        //set reference array
        poJSON.remove("personal_reference");
        poJSON.put("personal_reference", paReference);    
        
        return poJSON;
    }
    
    @Override
    public String toJSONString() {
        return toJSON().toJSONString();
    }
    
    @Override
    public String getMessage() {
        return psMessage;
    }
    
    public int getPersonalReferenceCount(){
        return paReference.size();
    }
    
    /**
     * setUnitUser(String fsValue)
     * 
     * @param fsValue 
     * 
     * char value for the user of the unit. <br>
     * 
     * 0 - Principal Customer
     * 1 - Other
     */
    public void setUnitUser(String fsValue){
        poJSON.remove("sUnitUser");
        poJSON.put("sUnitUser", fsValue);
    }
    /**
     * getUnitUser()
     * 
     * @return char value for the user of the unit. <br>
     * 
     * 0 - Principal Customer
     * 1 - Other
     */
    public String getUnitUser(){
        return (String) poJSON.get("sUnitUser");
    }
    
    /**
     * setOtherUser(String fsValue)
     * 
     * @param fsValue 
     * 
     * char value for the relationship of the buyer to the user of unit. <br>
     * 
     * 0 - Parents <br>
     * 1 - Spouse <br>
     * 2 - Relatives <br>
     * 3 - Friends
     */
    public void setOtherUser(String fsValue){
        if ("1".equals((String) poJSON.get("sUnitUser"))){
            poJSON.remove("sUsr2Buyr");
            poJSON.put("sUsr2Buyr", fsValue);
        } else {
            poJSON.remove("sUsr2Buyr");
            poJSON.put("sUsr2Buyr", "");
        }
    }
    /**
     * getOtherUser()
     * 
     * @return char value for the relationship of the buyer to the user of unit. <br>
     * 
     * 0 - Parents <br>
     * 1 - Spouse <br>
     * 2 - Relatives <br>
     * 3 - Friends
     */
    public String getOtherUser(){
        if ("1".equals((String) poJSON.get("sUnitUser")))
            return (String) poJSON.get("sUsr2Buyr");
        else
            return "";
    }
    
    /**
     * setPurpose(String fsValue)
     * 
     * @param fsValue 
     * 
     * purpose in buying the unit. <br>
     * 
     * 0 - Business <br>
     * 1 - Personal Service <br>
     * 2 - Raffle <br>
     * 3 - Gift
     */
    public void setPurpose(String fsValue){
        poJSON.remove("sPurposex");
        poJSON.put("sPurposex", fsValue);
    }
    /**
     * getPurpose()
     * 
     * @return purpose in buying the unit. <br>
     * 
     * 0 - Business <br>
     * 1 - Personal Service <br>
     * 2 - Raffle <br>
     * 3 - Gift
     */
    public String getPurpose(){
        return (String) poJSON.get("sPurposex");
    }
    
    /**
     * setUnitPayor(String fsValue)
     * 
     * @param fsValue 
     * 
     * char value for who will shoulder the monthly payment. <br>
     * 
     * 0 - Principal Customer
     * 1 - Other
     */
    public void setUnitPayor(String fsValue){
        poJSON.remove("sUnitPayr");
        poJSON.put("sUnitPayr", fsValue);
    }
    /**
     * getUnitPayor()
     * 
     * @return char value for who will shoulder the monthly payment. <br>
     * 
     * 0 - Principal Customer
     * 1 - Other
     */
    public String getUnitPayor(){
        return (String) poJSON.get("sUnitPayr");
    }
    
    /**
     * setPayorRelation(String fsValue)
     * 
     * @param fsValue 
     * 
     * char value for relationship of the buyer to the payor. <br>
     * 
     * 0 - Parents
     * 1 - Spouse
     * 2 - Relatives
     * 3 - Friends
     * 4 - Both customer and spouse
     */
    public void setPayorRelation(String fsValue){
        poJSON.remove("sPyr2Buyr");
        poJSON.put("sPyr2Buyr", fsValue);
    }
    /**
     * getPayorRelation()
     * 
     * @return char value for relationship of the buyer to the payor. <br>
     * 
     * 0 - Parents
     * 1 - Spouse
     * 2 - Relatives
     * 3 - Friends
     * 4 - Both customer and spouse
     */
    public String getPayorRelation(){
        return (String) poJSON.get("sPyr2Buyr");
    }
    
    /**
     * setSourceInfo(String fsValue)
     * 
     * @param fsValue 
     * 
     * on how did the customer know about us?
     */
    public void setSourceInfo(String fsValue){
        poJSON.remove("sSrceInfo");
        poJSON.put("sSrceInfo", fsValue);
    }
    /**
     * getSourceInfo()
     * 
     * @return how did the customer know about us?
     */
    public String getSourceInfo(){
        return (String) poJSON.get("sSrceInfo");
    }
    
    /**
     * addReference()
     * 
     * @return boolean
     * 
     * adds personal reference index
     */
    public boolean addReference(){
        JSONObject loJSON = new JSONObject();
        loJSON.put("sRefrNmex", "");
        loJSON.put("sRefrMPNx", "");
        loJSON.put("sRefrAddx", "");
        loJSON.put("sRefrTown", "");
        paReference.add(loJSON);
        
        return true;
    }
    
    /**
     * delReference(int fnRow)
     * 
     * @param fnRow
     * @return boolean
     * 
     * removes personal reference index
     */
    public boolean delReference(int fnRow){
        paReference.remove(fnRow);
        if (paReference.isEmpty()) return addReference();
        
        return true;
    }
    
    /**
     * setPRName(String fsValue)
     * 
     * @param fnRow
     * @param fsValue 
     * 
     * personal reference name.
     */
    public void setPRName(int fnRow, String fsValue){
        JSONObject loJSON = (JSONObject) paReference.get(fnRow);
        loJSON.remove("sRefrNmex");
        loJSON.put("sRefrNmex", fsValue);
        paReference.set(fnRow, loJSON);
    }
    /**
     * getPRName(int fnRow)
     * 
     * @param fnRow
     * @return personal reference name.
     */
    public String getPRName(int fnRow){
        JSONObject loJSON = (JSONObject) paReference.get(fnRow);
        return (String) loJSON.get("sRefrNmex");
    }
    
    /**
     * setPRMobileNo(int fnRow, String fsValue)
     * 
     * @param fnRow
     * @param fsValue 
     * 
     * personal reference mobile number.
     */
    public void setPRMobileNo(int fnRow, String fsValue){
        JSONObject loJSON = (JSONObject) paReference.get(fnRow);
        loJSON.remove("sRefrMPNx");
        loJSON.put("sRefrMPNx", fsValue);
        paReference.set(fnRow, loJSON);
    }
    /**
     * getPRMobileNo()
     * 
     * @param fnRow
     * @return personal reference mobile number.
     */
    public String getPRMobileNo(int fnRow){
        JSONObject loJSON = (JSONObject) paReference.get(fnRow);
        return (String) loJSON.get("sRefrMPNx");
    }
    
    /**
     * setPRAddress(String fsValue)
     * 
     * @param fsValue 
     * 
     * personal reference address.
     */
    public void setPRAddress(int fnRow, String fsValue){
        JSONObject loJSON = (JSONObject) paReference.get(fnRow);
        loJSON.remove("sRefrAddx");
        loJSON.put("sRefrAddx", fsValue);
        paReference.set(fnRow, loJSON);
    }
    /**
     * getPRAddress()
     * 
     * @return personal reference address.
     */
    public String getPRAddress(int fnRow){
        JSONObject loJSON = (JSONObject) paReference.get(fnRow);
        return (String) loJSON.get("sRefrAddx");        
    }
        
    /**
     * setPRTownCity(String fsValue)
     * 
     * @param fsValue 
     * 
     * personal reference town of the address.
     */
    public void setPRTownCity(int fnRow, String fsValue){
        JSONObject loJSON = (JSONObject) paReference.get(fnRow);
        loJSON.remove("sRefrTown");
        loJSON.put("sRefrTown", fsValue);
        paReference.set(fnRow, loJSON);
    }
    /**
     * getPRTownCity()
     * 
     * @return personal reference town of the address.
     */
    public String getPRTownCity(int fnRow){
        JSONObject loJSON = (JSONObject) paReference.get(fnRow);
        return (String) loJSON.get("sRefrTown");        
    }
}
