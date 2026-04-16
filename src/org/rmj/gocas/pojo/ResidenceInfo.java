package org.rmj.gocas.pojo;

import java.util.ArrayList;
import org.json.simple.JSONObject;

public class ResidenceInfo implements JEntity{
    private JSONObject poJSON;
    private String psMessage = "";
    private final ArrayList paKeys;
    
    private JSONObject poRntOther;
    private AddressInfo present_address;
    private AddressInfo permanent_address;
    
    public ResidenceInfo(){
        present_address = new AddressInfo();
        permanent_address = new AddressInfo();
        
        poJSON = new JSONObject();        
        poJSON.put("present_address", ""); //present address key
        poJSON.put("permanent_address", ""); //permanent address key
        
        poJSON.put("cOwnershp", "");
        poJSON.put("cOwnOther", "");
        poJSON.put("rent_others", "");
        poJSON.put("sCtkReltn", "");
        poJSON.put("cHouseTyp", "");
        poJSON.put("cGaragexx", "");
        
        paKeys = new ArrayList();
        paKeys.add("present_address"); //present address key
        paKeys.add("permanent_address"); //permanent address key
        
        paKeys.add("cOwnershp");
        paKeys.add("cOwnOther");
        paKeys.add("rent_others");
        paKeys.add("sCtkReltn");
        paKeys.add("cHouseTyp");
        paKeys.add("cGaragexx");        
        
        poRntOther = new JSONObject();
        poRntOther.put("cRntOther", "");
        poRntOther.put("nLenStayx", 0);
        poRntOther.put("nRentExps", 0.00);
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
        
        if ("1".equals((String) poJSON.get("cOwnershp")))
            poRntOther = (JSONObject) poJSON.get("rent_others");
        
        present_address.setData((JSONObject) poJSON.get("present_address"));
        permanent_address.setData((JSONObject) poJSON.get("permanent_address"));
        return true;
    }

    @Override
    public JSONObject toJSON() {        
        poJSON.remove("rent_others");
        if ("1".equals((String) poJSON.get("cOwnershp"))) poJSON.put("rent_others", poRntOther);
        
        if (!"0".equals((String) poJSON.get("cOwnershp"))) poJSON.remove("cOwnOther");  
        if (!"2".equals((String) poJSON.get("cOwnershp"))) poJSON.remove("cCtkReltn");       
        
        poJSON.remove("present_address");
        poJSON.put("present_address", present_address.toJSON());
        
        poJSON.remove("permanent_address");
        poJSON.put("permanent_address", permanent_address.toJSON());
        
        return poJSON;
    }
    
    @Override
    public String toJSONString() {
        toJSON();
        return poJSON.toJSONString();
    }
    
    @Override
    public String getMessage() {
        return psMessage;
    }
    
    /**
     * PresentAddress()
     * 
     * @return present address object.
     */
    public AddressInfo PresentAddress(){
        return present_address;
    }
    
    /**
     * PermanentAddress()
     * 
     * @return permanent address object.
     */
    public AddressInfo PermanentAddress(){
        return permanent_address;
    }
       
    /**
     * setOwnership(String fsValue)
     * 
     * @param fsValue
     * 
     * char value for applicant's residence ownership type. <br>
     * 
     * 0 - Owned <br>
     * 1 - Rent <br>
     * 2 - Caretaker 
     */
    public void setOwnership(String fsValue){
        poJSON.remove("cOwnershp");
        poJSON.put("cOwnershp", fsValue);
        
        switch (fsValue){
            case "0":
                if (!poJSON.containsKey("cOwnOther")) poJSON.put("cOwnOther", ""); break;
            case "1":
                if (!poJSON.containsKey("rent_others")) poJSON.put("rent_others", ""); break;
            case "2":
                if (!poJSON.containsKey("cCtkReltn")) poJSON.put("cCtkReltn", "");
        }            
    }
    /**
     * getOwnership()
     * 
     * @return char value for applicant's residence ownership type. <br>
     * 
     * 0 - Owned <br>
     * 1 - Rent <br>
     * 2 - Caretaker 
     */
    public String getOwnership(){
        return (String) poJSON.get("cOwnershp");
    }
    
    /**
     * setOwnedResidenceInfo(String fsValue)
     * 
     * @param fsValue
     * 
     * char value for residence info if the house is owned. <br>
     * 
     * 0 - Living with family <br>
     * 1 - Living with family (Parents & Siblings) <br>
     * 2 - Living with relatives
     */
    public void setOwnedResidenceInfo(String fsValue){
        if ("0".equals((String) poJSON.get("cOwnershp"))){
            poJSON.remove("cOwnOther");
            poJSON.put("cOwnOther", fsValue);
        } else {
            poJSON.remove("cOwnOther");
            poJSON.put("cOwnOther", "");
        }
    }
    /**
     * getOwnedResidenceInfo()
     * 
     * @return char value for residence info if the house is owned. <br>
     * 
     * 0 - Living with family <br>
     * 1 - Living with family (Parents & Siblings) <br>
     * 2 - Living with relatives
     */
    public String getOwnedResidenceInfo(){
        return (String) poJSON.get("cOwnOther");
    }
    
    /**
     * setOwnedResidenceInfo(String fsValue)
     * 
     * @param fsValue
     * 
     * char value for residence info if the house is owned. <br>
     * 
     * 0 - Living with family <br>
     * 1 - Living with family (Parents & Siblings) <br>
     * 2 - Living with relatives
     */
    public void setRentedResidenceInfo(String fsValue){
        if ("1".equals((String) poJSON.get("cOwnershp")) ||
            "2".equals((String) poJSON.get("cOwnershp"))){
            poRntOther.remove("cRntOther");
            poRntOther.put("cRntOther", fsValue);
        } else{
            poRntOther.remove("cRntOther");
            poRntOther.put("cRntOther", "");
        }
    }
    /**
     * getRentedResidenceInfo()
     * 
     * @return char value for residence info if the house is owned. <br>
     * 
     * 0 - Living with family <br>
     * 1 - Living with family (Parents & Siblings) <br>
     * 2 - Living with relatives
     */
    public String getRentedResidenceInfo(){
        return (String) poRntOther.get("cRntOther");
    }
    
    /**
     * setRentLengthOfStay(double fnValue)
     * 
     * @param fnValue
     * 
     * length of renting the house (in years)
     */
    public void setRentNoYears(double fnValue){
        if ("1".equals((String) poJSON.get("cOwnershp")) ||
            "2".equals((String) poJSON.get("cOwnershp"))){
            poRntOther.remove("nLenStayx");
            poRntOther.put("nLenStayx", fnValue);
        } else{
            poRntOther.remove("nLenStayx");
            poRntOther.put("nLenStayx", 0);
        }
    }
    /**
     * getRentNoYears()
     * 
     * @return length of renting the house (in years)
     */
    public double getRentNoYears(){
        return Double.valueOf(poRntOther.get("nLenStayx").toString());
    }
    
    /**
     * setRentExpenses(double fnValue)
     * 
     * @param fnValue
     * 
     * rent expenses
     */
    public void setRentExpenses(double fnValue){
        if ("1".equals((String) poJSON.get("cOwnershp")) ||
            "2".equals((String) poJSON.get("cOwnershp"))){
            poRntOther.remove("nRentExps");
            poRntOther.put("nRentExps", fnValue);
        } else{
            poRntOther.remove("nRentExps");
            poRntOther.put("nRentExps", 0.00);
        }
    }
    /**
     * getRentExpenses()
     * 
     * @return rent expenses
     */
    public double getRentExpenses(){
        return Double.valueOf(poRntOther.get("nRentExps").toString());
    }
    
    /**
     * setCareTakerRelation(String fsValue)
     * 
     * @param fsValue
     * 
     * relationship of the applicant to the owner of the house
     */
    public void setCareTakerRelation(String fsValue){
        if ("2".equals((String) poJSON.get("cOwnershp"))){
            poJSON.remove("sCtkReltn");
            poJSON.put("sCtkReltn", fsValue);
        } else{
            poJSON.remove("sCtkReltn");
            poJSON.put("sCtkReltn", "");
        }
    }
    /**
     * getCareTakerRelation()
     * 
     * @return relationship of the applicant to the owner of the house
     */
    public String getCareTakerRelation(){
        return (String) poJSON.get("sCtkReltn");
    }
    
    /**
     * setHouseType(String fsValue)
     * 
     * @param fsValue
     * 
     * char value for house type. <br>
     * 
     * 0 - Concrete <br>
     * 1 - Concrete and Wood <br>
     * 2 - Wood
     */
    public void setHouseType(String fsValue){
        poJSON.remove("cHouseTyp");
        poJSON.put("cHouseTyp", fsValue);
    }
    /**
     * getHouseType()
     * 
     * @return char value for house type. <br>
     * 
     * 0 - Concrete <br>
     * 1 - Concrete and Wood <br>
     * 2 - Wood
     */
    public String getHouseType(){
        return (String) poJSON.get("cHouseTyp");
    }
    
    /**
     * setHouseType(String fsValue)
     * 
     * @param fsValue
     * 
     * char value for house has garage. <br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public void hasGarage(String fsValue){
        poJSON.remove("cGaragexx");
        poJSON.put("cGaragexx", fsValue);
    }
    /**
     * getHouseType()
     * 
     * @return char value for house has garage. <br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public String hasGarage(){
        return (String) poJSON.get("cGaragexx");
    }
}
