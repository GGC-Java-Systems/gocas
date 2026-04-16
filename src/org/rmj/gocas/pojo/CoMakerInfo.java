package org.rmj.gocas.pojo;

import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CoMakerInfo implements JEntity{
    private JSONObject poJSON;
    private String psMessage = "";
    private final ArrayList paKeys;
    
    private JSONArray paMobileNo;
    private ResidenceInfo residence_info;
    
    public CoMakerInfo(){
        poJSON = new JSONObject();
        poJSON.put("sLastName", "");
        poJSON.put("sFrstName", "");
        poJSON.put("sSuffixNm", "");
        poJSON.put("sMiddName", "");
        poJSON.put("sNickName", "");
        poJSON.put("dBirthDte", "");
        poJSON.put("sBirthPlc", "");
        
        poJSON.put("cIncmeSrc", "");
        poJSON.put("sReltnCde", "");
        poJSON.put("mobile_number", "");
        poJSON.put("sFBAcctxx", "");
        
        paKeys = new ArrayList();
        paKeys.add("sLastName");
        paKeys.add("sFrstName");
        paKeys.add("sSuffixNm");
        paKeys.add("sMiddName");
        paKeys.add("sNickName");
        paKeys.add("dBirthDte");
        paKeys.add("sBirthPlc");
        
        paKeys.add("cIncmeSrc");
        paKeys.add("sReltnCde");
        paKeys.add("mobile_number");
        paKeys.add("sFBAcctxx");
        
        paMobileNo = new JSONArray();
        residence_info = new ResidenceInfo();
    }
    
    public ResidenceInfo ResidenceInfo(){
        return residence_info;
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
        
        paMobileNo = (JSONArray) poJSON.get("mobile_number");
        
        if (foData.get("residence_info") != null){
            if (!residence_info.setData((JSONObject) foData.get("residence_info"))){
                psMessage = "Unable to set residence info for comaker.";
                return false;
            }
        }
        
        return true;
    }

    @Override
    public JSONObject toJSON() {
        //set mobile information array
        poJSON.remove("mobile_number");
        poJSON.put("mobile_number", paMobileNo); 
        
        poJSON.remove("residence_info");
        poJSON.put("residence_info", residence_info.toJSON());
        
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
    
    /**
     * setLastName(String fsValue)
     * 
     * @param fsValue 
     * 
     * co-maker's last name.
     */
    public void setLastName(String fsValue){
        poJSON.remove("sLastName");
        poJSON.put("sLastName", fsValue);
    }
    /**
     * getLastName()
     * 
     * @return co-maker's last name.
     */
    public String getLastName(){
        return (String) poJSON.get("sLastName");
    }
    
    /**
     * setFirstName(String fsValue)
     * 
     * @param fsValue 
     * 
     * co-maker's first name.
     */
    public void setFirstName(String fsValue){
        poJSON.remove("sFrstName");
        poJSON.put("sFrstName", fsValue);
    }
    /**
     * getFirstName()
     * 
     * @return co-maker's first name.
     */
    public String getFirstName(){
        return (String) poJSON.get("sFrstName");
    }
    
    /**
     * setSuffixName(String fsValue)
     * 
     * @param fsValue 
     * 
     * co-maker's suffix name. <br>
     * ex. Sr, Jr, I, II, III
     */
    public void setSuffixName(String fsValue){
        poJSON.remove("sSuffixNm");
        poJSON.put("sSuffixNm", fsValue);
    }
    /**
     * getSuffixName()
     * 
     * @return co-maker's suffix name.
     */
    public String getSuffixName(){
        return (String) poJSON.get("sSuffixNm");
    }
    
    /**
     * setMiddleName(String fsValue)
     * 
     * @param fsValue 
     * 
     * co-maker's middle name.
     */
    public void setMiddleName(String fsValue){
        poJSON.remove("sMiddName");
        poJSON.put("sMiddName", fsValue);
    }
    /**
     * getMiddleName()
     * 
     * @return co-maker's middle name.
     */
    public String getMiddleName(){
        return (String) poJSON.get("sMiddName");
    }
    
    
    /**
     * setNickName(String fsValue)
     * 
     * @param fsValue 
     * 
     * co-maker's nickname.
     */
    public void setNickName(String fsValue){
        poJSON.remove("sNickName");
        poJSON.put("sNickName", fsValue);
    }
    /**
     * getNickName()
     * 
     * @return co-maker's nickname.
     */
    public String getNickName(){
        return (String) poJSON.get("sNickName");
    }
    
    /**
     * setBirthdate(String fsValue)
     * 
     * @param fdValue 
     * 
     * co-maker's birth date.
     */
    public void setBirthdate(String fsValue){
        poJSON.remove("dBirthDte");
        poJSON.put("dBirthDte", fsValue);
    }
    /**
     * getBirthdate()
     * 
     * @return co-maker's birth date. Format YYYY-MM-DD
     */
    public String getBirthdate(){
        return (String) poJSON.get("dBirthDte");
    }
       
    /**
     * setBirthPlace(String fsValue)
     * 
     * @param fsValue 
     * 
     * customer's birth place town.
     */
    public void setBirthPlace(String fsValue){       
        poJSON.remove("sBirthPlc");
        poJSON.put("sBirthPlc", fsValue);
    }
    /**
     * getBirthPlace()
     * 
     * @return customer's birth place town.
     */
    public String getBirthPlace(){
        return (String) poJSON.get("sBirthPlc");
    }
    
    /**
     * setIncomeSource(String fsValue)
     * 
     * @param fsValue 
     * 
     * char value for applicant's income source. <br>
     * 
     * 0 - Employed <br>
     * 1 - Self-employed <br>
     * 2 - With Financer <br>
     * 3 - Pensioner
     */
    public void setIncomeSource(String fsValue){
        poJSON.remove("cIncmeSrc");
        poJSON.put("cIncmeSrc", fsValue);
    }
    /**
     * getIncomeSource()
     * 
     * @return char value for applicant's income source. <br>
     * 
     * 0 - Employed <br>
     * 1 - Self-employed <br>
     * 2 - With Financer <br>
     * 3 - Pensioner
     */
    public String getIncomeSource(){
        return (String) poJSON.get("cIncmeSrc");
    }
    
    /**
     * setRelation(String fsValue)
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
    public void setRelation(String fsValue){
        poJSON.remove("sReltnCde");
        poJSON.put("sReltnCde", fsValue);
    }
    /**
     * getRelation()
     * 
     * @return char value for applicant's relation to his financer. <br>
     * 
     * 0 - Parents <br>
     * 1 - Spouse <br>
     * 2 - Relatives <br>
     * 3 - Friends
     */
    public String getRelation(){
        return (String) poJSON.get("sReltnCde");
    }
    
    /**
     * setMobileNoQty(int fnValue)
     * 
     * @param fnValue 
     * 
     * customer's number of active mobile numbers.
     */
    public void setMobileNoQty(int fnValue){
        int lnRow = paMobileNo.size();
        
        if (lnRow != fnValue){
            //quantity of mobile number was reduced.
            if (lnRow > fnValue){
                int lnIndex = fnValue;
                for (int lnCtr = fnValue; lnCtr < lnRow; lnCtr ++){
                    paMobileNo.remove(lnIndex);
                }
            } else if (lnRow < fnValue){
                JSONObject loJSON = new JSONObject();
                loJSON.put("sMobileNo", "");
                loJSON.put("cPostPaid", "0");
                loJSON.put("nPostYear", 0); //2019.12.05
                for (int lnCtr = lnRow; lnCtr < fnValue; lnCtr ++){
                    paMobileNo.add(loJSON);
                }
            }
        }
    }
    /**
     * getMobileNoQty()
     * 
     * @return customer's number of active mobile numbers.
     */
    public int getMobileNoQty(){
        return paMobileNo.size();
    }
    
    /**
     * setMobileNo(int fnIndex, String fsValue)
     * 
     * @param fnIndex
     * mobile number index
     * 
     * @param fsValue
     * customer's mobile number
     */
    public void setMobileNo(int fnIndex, String fsValue){
        JSONObject loJSON = (JSONObject) paMobileNo.get(fnIndex);
        
        loJSON.remove("sMobileNo");
        loJSON.put("sMobileNo", fsValue);
        paMobileNo.set(fnIndex, loJSON);
    }
    /**
     * getMobileNo(int fnIndex)
     * 
     * @param fnIndex
     * mobile number index <br>
     * 
     * @return customer's mobile number.
     */
    public String getMobileNo(int fnIndex){
        JSONObject loJSON = (JSONObject) paMobileNo.get(fnIndex);
        
        return (String) loJSON.get("sMobileNo");
    }
    
    /**
     * setFBAccount(String fsValue)
     * 
     * @param fsValue 
     * 
     * customer's Facebook account.
     */
    public void setFBAccount(String fsValue){
        poJSON.remove("sFBAcctxx");
        poJSON.put("sFBAcctxx", fsValue);
    }
    /**
     * getFBAccount()
     * 
     * @return customer's Facebook account.
     */
    public String getFBAccount(){
        return (String) poJSON.get("sFBAcctxx");
    }
    
    /**
     * IsMobilePostpaid(int fnIndex, String fsValue)
     * 
     * @param fnIndex
     * mobile number index <br>
     * 
     * @param fsValue
     * char value if the mobile number is Postpaid or Prepaid. <br>
     * 
     * 0 - Prepaid <br>
     * 1 - Postpaid
     */
    public void IsMobilePostpaid(int fnIndex, String fsValue){
        JSONObject loJSON = (JSONObject) paMobileNo.get(fnIndex);
        
        loJSON.remove("cPostPaid");
        loJSON.put("cPostPaid", fsValue);
        paMobileNo.set(fnIndex, loJSON);
    }
    /**
     * IsMobilePostpaid(int fnIndex))
     * 
     * @param fnIndex
     * mobile number index <br>
     * 
     * @return char value if the mobile number is Postpaid or Prepaid. <br>
     * 
     * 0 - Prepaid <br>
     * 1 - Postpaid
     */
    public String IsMobilePostpaid(int fnIndex){
        JSONObject loJSON = (JSONObject) paMobileNo.get(fnIndex);
        
        return (String) loJSON.get("cPostPaid");
    }
    
    /**
     * setPostPaidYears(int fnIndex, int fnValue)
     * 
     * @param fnIndex mobile number index
     * @param fnValue number of years with the plan.
     */
    public void setPostPaidYears(int fnIndex, int fnValue){
        JSONObject loJSON = (JSONObject) paMobileNo.get(fnIndex);
        
        loJSON.remove("nPostYear");
        loJSON.put("nPostYear", fnValue);
        paMobileNo.set(fnIndex, loJSON);
    }
    /**
     * getPostPaidYears(int fnIndex)
     * 
     * @param fnIndex mobile number index
     * @return number of years with the plan.
     */
    public int getPostPaidYears(int fnIndex){
        JSONObject loJSON = (JSONObject) paMobileNo.get(fnIndex);
        
        return (int) loJSON.get("nPostYear");
    }
}
