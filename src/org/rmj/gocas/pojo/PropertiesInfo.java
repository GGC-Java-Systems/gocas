package org.rmj.gocas.pojo;

import java.util.ArrayList;
import org.json.simple.JSONObject;

public class PropertiesInfo implements JEntity{
    private JSONObject poJSON;
    private String psMessage = "";
    private final ArrayList paKeys;
    
    public PropertiesInfo(){
        poJSON = new JSONObject();
        poJSON.put("sProprty1", "");
        poJSON.put("sProprty2", "");
        poJSON.put("sProprty3", "");
        poJSON.put("cWith4Whl", "");
        poJSON.put("cWith3Whl", "");
        poJSON.put("cWith2Whl", "");
        poJSON.put("cWithRefx", "");
        poJSON.put("cWithTVxx", "");
        poJSON.put("cWithACxx", "");
        
        paKeys = new ArrayList();
        paKeys.add("sProprty1");
        paKeys.add("sProprty2");
        paKeys.add("sProprty3");
        paKeys.add("cWith4Whl");
        paKeys.add("cWith3Whl");
        paKeys.add("cWith2Whl");
        paKeys.add("cWithRefx");
        paKeys.add("cWithTVxx");
        paKeys.add("cWithACxx");
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
     * setLotName1(String fsValue)
     * 
     * @param fsValue 
     * 
     * applicant lot name
     */
    public void setLotName1(String fsValue){
        poJSON.remove("sProprty1");
        poJSON.put("sProprty1", fsValue);
    }
    /**
     * getLotName1()
     * 
     * @return applicant lot name
     */
    public String getLotName1(){
        return (String) poJSON.get("sProprty1");
    }
    
    /**
     * setLotName2(String fsValue)
     * 
     * @param fsValue 
     * 
     * applicant lot name
     */
    public void setLotName2(String fsValue){
        poJSON.remove("sProprty2");
        poJSON.put("sProprty2", fsValue);
    }
    /**
     * getLotName2()
     * 
     * @return applicant lot name
     */
    public String getLotName2(){
        return (String) poJSON.get("sProprty2");
    }
    
    /**
     * setLotName3(String fsValue)
     * 
     * @param fsValue 
     * 
     * applicant lot name
     */
    public void setLotName3(String fsValue){
        poJSON.remove("sProprty3");
        poJSON.put("sProprty3", fsValue);
    }
    /**
     * getLotName3()
     * 
     * @return applicant lot name
     */
    public String getLotName3(){
        return (String) poJSON.get("sProprty3");
    }
    
    /**
     * Has4Wheels(String fsValue)
     * 
     * @param fsValue 
     * 
     * is applicant has 4 wheels? <br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public void Has4Wheels(String fsValue){
        poJSON.remove("cWith4Whl");
        poJSON.put("cWith4Whl", fsValue);
    }
    /**
     * Has4Wheels()
     * 
     * @return is applicant has 4 wheels? <br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public String Has4Wheels(){
        return (String) poJSON.get("cWith4Whl");
    }
    
    /**
     * Has3Wheels(String fsValue)
     * 
     * @param fsValue 
     * 
     * is applicant has 3 wheels? <br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public void Has3Wheels(String fsValue){
        poJSON.remove("cWith3Whl");
        poJSON.put("cWith3Whl", fsValue);
    }
    /**
     * Has3Wheels()
     * 
     * @return is applicant has 3 wheels? <br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public String Has3Wheels(){
        return (String) poJSON.get("cWith3Whl");
    }
    
    /**
     * Has2Wheels(String fsValue)
     * 
     * @param fsValue 
     * 
     * is applicant has 2 wheels? <br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public void Has2Wheels(String fsValue){
        poJSON.remove("cWith2Whl");
        poJSON.put("cWith2Whl", fsValue);
    }
    /**
     * Has2Wheels()
     * 
     * @return is applicant has 2 wheels? <br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public String Has2Wheels(){
        return (String) poJSON.get("cWith2Whl");
    }
    
    /**
     * WithRefrigerator(String fsValue)
     * 
     * @param fsValue 
     * 
     * is applicant has refrigerator? <br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public void WithRefrigerator(String fsValue){
        poJSON.remove("cWithRefx");
        poJSON.put("cWithRefx", fsValue);
    }
    /**
     * WithRefrigerator()
     * 
     * @return is applicant has refrigerator? <br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public String WithRefrigerator(){
        return (String) poJSON.get("cWithRefx");
    }
    
    /**
     * WithTelevision(String fsValue)
     * 
     * @param fsValue 
     * 
     * is applicant has television? <br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public void WithTelevision(String fsValue){
        poJSON.remove("cWithTVxx");
        poJSON.put("cWithTVxx", fsValue);
    }
    /**
     * WithTelevision()
     * 
     * @return is applicant has television? <br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public String WithTelevision(){
        return (String) poJSON.get("cWithTVxx");
    }
    
    /**
     * WithAirCon(String fsValue)
     * 
     * @param fsValue 
     * 
     * is applicant has air conditioner? <br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public void WithAirCon(String fsValue){
        poJSON.remove("cWithACxx");
        poJSON.put("cWithACxx", fsValue);
    }
    /**
     * WithAirCon()
     * 
     * @return is applicant has air conditioner? <br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public String WithAirCon(){
        return (String) poJSON.get("cWithACxx");
    }
}
