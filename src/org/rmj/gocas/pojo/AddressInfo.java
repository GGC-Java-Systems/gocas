package org.rmj.gocas.pojo;

import java.util.ArrayList;
import org.json.simple.JSONObject;

public class AddressInfo implements JEntity{
    private JSONObject poJSON;
    private String psMessage = "";
    private final ArrayList paKeys;
    
    public AddressInfo(){
        poJSON = new JSONObject();
        poJSON.put("sLandMark", "");
        poJSON.put("sHouseNox", "");
        poJSON.put("sAddress1", "");
        poJSON.put("sAddress2", "");
        poJSON.put("sTownIDxx", "");
        poJSON.put("sBrgyIDxx", "");
        
        paKeys = new ArrayList();
        paKeys.add("sLandMark");
        paKeys.add("sHouseNox");
        paKeys.add("sAddress1");
        paKeys.add("sAddress2");
        paKeys.add("sTownIDxx");
        paKeys.add("sBrgyIDxx");
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
     * setLandMark(String fsValue)
     * 
     * @param fsValue
     * 
     * land mark of the applicant's residence.
     */
    public void setLandMark(String fsValue){
        poJSON.remove("sLandMark");
        poJSON.put("sLandMark", fsValue);
    }
    /**
     * getLandMark()
     * 
     * @return land mark of the applicant's residence.
     */
    public String getLandMark(){
        return (String) poJSON.get("sLandMark");
    }
    
    /**
     * setHouseNo(String fsValue)
     * 
     * @param fsValue
     * 
     * applicant's residence house number.
     */
    public void setHouseNo(String fsValue){
        poJSON.remove("sHouseNox");
        poJSON.put("sHouseNox", fsValue);
    }
    /**
     * getHouseNo()
     * 
     * @return applicant's residence house number.
     */
    public String getHouseNo(){
        return (String) poJSON.get("sHouseNox");
    }
    
    /**
     * setAddress1(String fsValue)
     * 
     * @param fsValue
     * 
     * applicant's residence Phase #/Lot #/Sitio.
     */
    public void setAddress1(String fsValue){
        poJSON.remove("sAddress1");
        poJSON.put("sAddress1", fsValue);
    }
    /**
     * getAddress1()
     * 
     * @return applicant's residence Phase #/Lot #/Sitio.
     */
    public String getAddress1(){
        return (String) poJSON.get("sAddress1");
    }
    
    /**
     * setAddress2(String fsValue)
     * 
     * @param fsValue
     * 
     * applicant's residence Street Name.
     */
    public void setAddress2(String fsValue){
        poJSON.remove("sAddress2");
        poJSON.put("sAddress2", fsValue);
    }
    /**
     * getAddress2()
     * 
     * @return applicant's residence Street Name.
     */
    public String getAddress2(){
        return (String) poJSON.get("sAddress2");
    }
       
    /**
     * setTownCity(String fsValue)
     * 
     * @param fsValue
     * 
     * applicant's residence Province.
     */
    public void setTownCity(String fsValue){
        poJSON.remove("sTownIDxx");
        poJSON.put("sTownIDxx", fsValue);
    }
    /**
     * getTownCity()
     * 
     * @return applicant's residence Province.
     */
    public String getTownCity(){
        return (String) poJSON.get("sTownIDxx");
    }
    
    /**
     * setBarangay(String fsValue)
     * 
     * @param fsValue
     * 
     * applicant's residence Barangay.
     */
    public void setBarangay(String fsValue){
        poJSON.remove("sBrgyIDxx");
        poJSON.put("sBrgyIDxx", fsValue);
    }
    /**
     * getBarangay()
     * 
     * @return applicant's residence Barangay.
     */
    public String getBarangay(){
        return (String) poJSON.get("sBrgyIDxx");
    }
}
