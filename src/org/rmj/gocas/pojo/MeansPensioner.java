package org.rmj.gocas.pojo;

import java.util.ArrayList;
import org.json.simple.JSONObject;

public class MeansPensioner implements JEntity{
    private JSONObject poJSON;
    private String psMessage = "";
    private final ArrayList paKeys;
    
    public MeansPensioner(){
        poJSON = new JSONObject();        
        poJSON.put("cPenTypex", "");
        poJSON.put("nPensionx", 0.00);
        poJSON.put("nRetrYear", 0);
        
        paKeys = new ArrayList();
        paKeys.add("cPenTypex");
        paKeys.add("nPensionx");
        paKeys.add("nRetrYear");
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
     * setSource(String fsValue)
     * 
     * @param fsValue
     * 
     * char value for applicant's pension source. <br>
     * 
     * 0 - Private <br>
     * 1 - Government
     */
    public void setSource(String fsValue){
        poJSON.remove("cPenTypex");
        poJSON.put("cPenTypex", fsValue);
    }
    /**
     * getSource()
     * 
     * @return char value for applicant's pension source. <br>
     * 
     * 0 - Private <br>
     * 1 - Government
     */
    public String getSource(){
        return (String) poJSON.get("cPenTypex");
    }
        
    /**
     * setAmount(long fnValue)
     * 
     * @param fnValue
     * 
     * applicant's pension income amount.
     */
    public void setAmount(long fnValue){
        poJSON.remove("nPensionx");
        poJSON.put("nPensionx", fnValue);
    }
    /**
     * getAmount()
     * 
     * @return applicant's pension income amount.
     */
    public String getAmount(){
        return (String) poJSON.get("nPensionx");  
    }
    
    /**
     * setYearRetired(int fnValue)
     * 
     * @param fnValue
     * 
     * applicant's salary.
     */
    public void setYearRetired(int fnValue){
        poJSON.remove("nRetrYear");
        poJSON.put("nRetrYear", fnValue);
    }
    /**
     * getYearRetired()
     * 
     * @return salary.
     */
    public int getYearRetired(){
        return (int) poJSON.get("nRetrYear");  
    }
}
