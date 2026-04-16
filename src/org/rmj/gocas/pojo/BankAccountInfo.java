package org.rmj.gocas.pojo;

import java.util.ArrayList;
import org.json.simple.JSONObject;

public class BankAccountInfo implements JEntity{
    private JSONObject poJSON;
    private String psMessage = "";
    private final ArrayList paKeys;
    
    public BankAccountInfo(){
        poJSON = new JSONObject();
        poJSON.put("sBankName", "");
        poJSON.put("sAcctType", "");
        
        paKeys = new ArrayList();
        paKeys.add("sBankName");
        paKeys.add("sAcctType");
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
     * setAccountType(String fsValue)
     * 
     * @param fsValue 
     * 
     * char value for account type. <br>
     * 
     * 0 - Checking <br>
     * 1 - Savings <br>
     * 2 - Payroll
     */
    public void setAccountType(String fsValue){
        poJSON.remove("sAcctType");
        poJSON.put("sAcctType", fsValue);
    }
    /**
     * getAccountType()
     * 
     * @return char value for account type. <br>
     * 
     * 0 - Checking <br>
     * 1 - Savings <br>
     * 2 - Payroll
     */
    public String getAccountType(){
        return (String) poJSON.get("sAcctType");
    }
}
