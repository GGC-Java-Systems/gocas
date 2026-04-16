package org.rmj.gocas.pojo;

import java.util.ArrayList;
import org.json.simple.JSONObject;

public class MeansFinancer implements JEntity{
    private JSONObject poJSON;
    private String psMessage = "";
    private final ArrayList paKeys;
    
    public MeansFinancer(){
        poJSON = new JSONObject();        
        poJSON.put("sReltnCde", "");
        poJSON.put("sFinancer", "");
        poJSON.put("nEstIncme", 0.00);
        poJSON.put("sNatnCode", "");
        poJSON.put("sMobileNo", "");
        poJSON.put("sFBAcctxx", "");
        poJSON.put("sEmailAdd", "");
        
        paKeys = new ArrayList();
        paKeys.add("sReltnCde");
        paKeys.add("sFinancer");
        paKeys.add("nEstIncme");
        paKeys.add("sNatnCode");
        paKeys.add("sMobileNo");
        paKeys.add("sFBAcctxx");
        paKeys.add("sEmailAdd");
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
     * char value for applicant's relation to his financer. <br>
     * 
     * 0 - Parents <br>
     * 1 - Spouse <br>
     * 2 - Relatives <br>
     * 3 - Friends
     */
    public void setSource(String fsValue){
        poJSON.remove("sReltnCde");
        poJSON.put("sReltnCde", fsValue);
    }
    /**
     * getSource()
     * 
     * @return char value for applicant's relation to his financer. <br>
     * 
     * 0 - Parents <br>
     * 1 - Spouse <br>
     * 2 - Relatives <br>
     * 3 - Friends
     */
    public String getSource(){
        return (String) poJSON.get("sReltnCde");
    }
    
    /**
     * setSource(String fsValue)
     * 
     * @param fsValue
     * 
     * applicant's financer name.
     */
    public void setFinancerName(String fsValue){
        poJSON.remove("sFinancer");
        poJSON.put("sFinancer", fsValue);
    }
    /**
     * getSource()
     * 
     * @return applicant's financer name.
     */
    public String getFinancerName(){
        return (String) poJSON.get("sFinancer");
    }
    
    /**
     * setAmount(long fnValue)
     * 
     * @param fnValue
     * 
     * applicant's estimated amount financed.
     */
    public void setAmount(long fnValue){
        poJSON.remove("nEstIncme");
        poJSON.put("nEstIncme", fnValue);
    }
    /**
     * getAmount()
     * 
     * @return applicant's estimated amount financed.
     */
    public String getAmount(){
        return (String) poJSON.get("nEstIncme");  
    }
    
    /**
     * setCountry(String fsValue)
     * 
     * @param fsValue
     * 
     * applicant's financer residing country.
     */
    public void setCountry(String fsValue){
        poJSON.remove("sNatnCode");
        poJSON.put("sNatnCode", fsValue);
    }
    /**
     * getCountry()
     * 
     * @return applicant's financer residing country.
     */
    public String getCountry(){
        return (String) poJSON.get("sNatnCode");
    }
    
    /**
     * setMobileNo(String fsValue)
     * 
     * @param fsValue
     * 
     * applicant's financer mobile number.
     */
    public void setMobileNo(String fsValue){
        poJSON.remove("sMobileNo");
        poJSON.put("sMobileNo", fsValue);
    }
    /**
     * getMobileNo()
     * 
     * @return applicant's financer mobile number.
     */
    public String getMobileNo(){
        return (String) poJSON.get("sMobileNo");
    }
    
    /**
     * setFBAccount(String fsValue)
     * 
     * @param fsValue 
     * 
     * financer's Facebook account.
     */
    public void setFBAccount(String fsValue){
        poJSON.remove("sFBAcctxx");
        poJSON.put("sFBAcctxx", fsValue);
    }
    /**
     * getFBAccount()
     * 
     * @return fianncer's Facebook account.
     */
    public String getFBAccount(){
        return (String) poJSON.get("sFBAcctxx");
    }
    
    /**
     * setFBAccount(String fsValue)
     * 
     * @param fsValue 
     * 
     * customer's financer's email address.
     */
    public void setEmailAddress(String fsValue){
        poJSON.remove("sEmailAdd");
        poJSON.put("sEmailAdd", fsValue);
    }
    /**
     * getFBAccount()
     * 
     * @return financer's email address.
     */
    public String getEmailAddress(){
        return (String) poJSON.get("sEmailAdd");
    }
}
