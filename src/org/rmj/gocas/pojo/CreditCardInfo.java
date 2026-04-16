package org.rmj.gocas.pojo;

import java.util.ArrayList;
import org.json.simple.JSONObject;

public class CreditCardInfo implements JEntity{
    private JSONObject poJSON;
    private String psMessage = "";
    private final ArrayList paKeys;
    
    public CreditCardInfo(){
        poJSON = new JSONObject();
        poJSON.put("sBankName", "");
        poJSON.put("nCrdLimit", 0.00);
        poJSON.put("nSinceYrx", 0);
        
        paKeys = new ArrayList();
        paKeys.add("sBankName");
        paKeys.add("nCrdLimit");
        paKeys.add("nSinceYrx");
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
     * setBankName(String fsValue)
     * 
     * @param fsValue 
     * 
     * bank name
     */
    public void setBankName(String fsValue){
        poJSON.remove("sBankName");
        poJSON.put("sBankName", fsValue);
    }
    /**
     * getBankName()
     * 
     * @return bank name
     */
    public String getBankName(){
        return (String) poJSON.get("sBankName");
    }
    
    /**
     * setCreditLimit(double fnValue)
     * 
     * @param fnValue 
     * 
     * credit limit
     */
    public void setCreditLimit(double fnValue){
        poJSON.remove("nCrdLimit");
        poJSON.put("nCrdLimit", fnValue);
    }
    /**
     * getCreditLimit()
     * 
     * @return credit limit
     */
    public double getCreditLimit(){
        return (double) poJSON.get("nCrdLimit");
    }
    
    /**
     * setMemberSince(int fnValue)
     * 
     * @param fnValue 
     * 
     * member since year
     */
    public void setMemberSince(int fnValue){
        poJSON.remove("nSinceYrx");
        poJSON.put("nSinceYrx", fnValue);
    }
    /**
     * getMemberSince()
     * 
     * @return member since year
     */
    public int getMemberSince(){
        return (int) poJSON.get("nSinceYrx");
    }
}
