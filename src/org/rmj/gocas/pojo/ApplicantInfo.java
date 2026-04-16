package org.rmj.gocas.pojo;

import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ApplicantInfo implements JEntity{
    private JSONObject poJSON;
    private String psMessage = "";
    private final ArrayList paKeys;
    
    private JSONArray paMobileNo;
    private JSONArray paPhoneNox;
    private JSONArray paEmailAdd;
    private JSONObject poFacebook;
    
    public ApplicantInfo(){
        poJSON = new JSONObject();
        poJSON.put("sLastName", "");
        poJSON.put("sFrstName", "");
        poJSON.put("sSuffixNm", "");
        poJSON.put("sMiddName", "");
        poJSON.put("sNickName", "");
        poJSON.put("dBirthDte", "");
        poJSON.put("sBirthPlc", "");
        poJSON.put("sCitizenx", "");
        poJSON.put("mobile_number", ""); //array
        poJSON.put("landline", ""); //array
        poJSON.put("cCvilStat", "");
        poJSON.put("cGenderCd", "");
        poJSON.put("sMaidenNm", "");
        poJSON.put("email_address", ""); //array
        poJSON.put("facebook", "");
        poJSON.put("sVibeAcct", "");
        
        paKeys = new ArrayList();
        paKeys.add("sLastName");
        paKeys.add("sFrstName");
        paKeys.add("sSuffixNm");
        paKeys.add("sMiddName");
        paKeys.add("sNickName");
        paKeys.add("dBirthDte");
        paKeys.add("sBirthPlc");
        paKeys.add("sCitizenx");
        paKeys.add("mobile_number");
        paKeys.add("landline");
        paKeys.add("cCvilStat");
        paKeys.add("cGenderCd");
        paKeys.add("sMaidenNm");
        paKeys.add("email_address");
        paKeys.add("facebook");
        paKeys.add("sVibeAcct");
        
        poFacebook = new JSONObject();
        poFacebook.put("sFBAcctxx", "");
        poFacebook.put("cAcctStat", "");
        poFacebook.put("nNoFriend", 0);
        poFacebook.put("nYearxxxx", 0);
        
        paMobileNo = new JSONArray();
        paPhoneNox = new JSONArray();
        paEmailAdd = new JSONArray();
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
        
        poFacebook = (JSONObject) poJSON.get("facebook");
        paMobileNo = (JSONArray) poJSON.get("mobile_number");
        paPhoneNox = (JSONArray) poJSON.get("landline");
        paEmailAdd = (JSONArray) poJSON.get("email_address");
        return true;
    }

    @Override
    public JSONObject toJSON() {
        //set facebook object
        poJSON.remove("facebook");
        poJSON.put("facebook", poFacebook);
        
        //set mobile information array
        poJSON.remove("mobile_number");
        poJSON.put("mobile_number", paMobileNo);        
        
        //set phone number array
        poJSON.remove("landline");
        poJSON.put("landline", paPhoneNox);        
        
        //set email address array
        poJSON.remove("email_address");
        poJSON.put("email_address", paEmailAdd);
        
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
    
    
    public int getMobileCount(){
        return paMobileNo.size();
    }
    
    public int getEmailCount(){
        return paEmailAdd.size();
    }
    
    public int getPhoneCount(){
        return paPhoneNox.size();
    }
    
    /**
     * setLastName(String fsValue)
     * 
     * @param fsValue 
     * 
     * customer's last name.
     */
    public void setLastName(String fsValue){
        poJSON.remove("sLastName");
        poJSON.put("sLastName", fsValue.replace("Ã±", "ñ"));
    }
    /**
     * getLastName()
     * 
     * @return customer's last name.
     */
    public String getLastName(){
        return (String) poJSON.get("sLastName");
    }
    
    /**
     * setFirstName(String fsValue)
     * 
     * @param fsValue 
     * 
     * customer's first name.
     */
    public void setFirstName(String fsValue){
        poJSON.remove("sFrstName");
        poJSON.put("sFrstName", fsValue.replace("Ã±", "ñ"));
    }
    /**
     * getFirstName()
     * 
     * @return customer's first name.
     */
    public String getFirstName(){
        return (String) poJSON.get("sFrstName");
    }
    
    /**
     * setSuffixName(String fsValue)
     * 
     * @param fsValue 
     * 
     * customer's suffix name. <br>
     * ex. Sr, Jr, I, II, III
     */
    public void setSuffixName(String fsValue){
        poJSON.remove("sSuffixNm");
        poJSON.put("sSuffixNm", fsValue);
    }
    /**
     * getSuffixName()
     * 
     * @return customer's suffix name.
     */
    public String getSuffixName(){
        return (String) poJSON.get("sSuffixNm");
    }
    
    /**
     * setMiddleName(String fsValue)
     * 
     * @param fsValue 
     * 
     * customer's middle name.
     */
    public void setMiddleName(String fsValue){
        poJSON.remove("sMiddName");
        poJSON.put("sMiddName", fsValue.replace("Ã±", "ñ"));
    }
    /**
     * getMiddleName()
     * 
     * @return customer's middle name.
     */
    public String getMiddleName(){
        return (String) poJSON.get("sMiddName");
    }
    
    /**
     * getClientName()
     * 
     * @return customer's full name.
     */
    public String getClientName(){
        String lsName = getLastName() + ", " + getFirstName() + " ";
        
        if (!getSuffixName().equals(""))
            lsName = lsName + getSuffixName() + " ";
        
        lsName = lsName + getMiddleName();
        
        return lsName;
    }
    
    /**
     * setNickName(String fsValue)
     * 
     * @param fsValue 
     * 
     * customer's nickname.
     */
    public void setNickName(String fsValue){
        poJSON.remove("sNickName");
        poJSON.put("sNickName", fsValue);
    }
    /**
     * getNickName()
     * 
     * @return customer's nickname.
     */
    public String getNickName(){
        return (String) poJSON.get("sNickName");
    }
    
    /**
     * setBirthdate(String fsValue)
     * 
     * @param fsValue 
     * 
     * customer's birth date. Format YYYY-MM-DD
     */
    public void setBirthdate(String fsValue){
        poJSON.remove("dBirthDte");
        poJSON.put("dBirthDte", fsValue);
    }
    /**
     * getBirthdate()
     * 
     * @return customer's birth date. Format YYYY-MM-DD
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
     * setCitizenship(String fsValue)
     * 
     * @param fsValue 
     * 
     * customer's nationality.
     */
    public void setCitizenship(String fsValue){
        poJSON.remove("sCitizenx");
        poJSON.put("sCitizenx", fsValue);
    }
    /**
     * getCitizenship()
     * 
     * @return customer's nationality.
     */
    public String getCitizenship(){
        return (String) poJSON.get("sCitizenx");
    }
    
    /**
     * setCivilStatus(String fsValue)
     * 
     * @param fsValue 
     * 
     * char value for customer's civil status. <br>
     * 
     * 0 - Single <br>
     * 1 - Married <br>
     * 2 - Separated <br>
     * 3 - Widowed <br>
     * 4 - Single Parent <br>
     * 5 - Single Parent with Live-in Partner
     */
    public void setCivilStatus(String fsValue){
        poJSON.remove("cCvilStat");
        poJSON.put("cCvilStat", fsValue);
    }
    /**
     * getCivilStatus()
     * 
     * @return char value for customer's civil status. <br>
     * 
     * 0 - Single <br>
     * 1 - Married <br>
     * 2 - Separated <br>
     * 3 - Widowed <br>
     * 4 - Single Parent <br>
     * 5 - Single Parent with Live-in Partner
     */
    public String getCivilStatus(){
        return (String) poJSON.get("cCvilStat");
    }
    
    /**
     * setGender(String fsValue)
     * 
     * @param fsValue 
     * 
     * char value for customer's gender. <br>
     * 
     * 0 - Male <br>
     * 1 - Female <br>
     * 2 - LGBTQ
     */
    public void setGender(String fsValue){
        poJSON.remove("cGenderCd");
        poJSON.put("cGenderCd", fsValue);
    }
    /**
     * getGender()
     * 
     * @return char value for customer's gender. <br>
     * 
     * 0 - Male <br>
     * 1 - Female <br>
     * 2 - LGBTQ
     */
    public String getGender(){
        return (String) poJSON.get("cGenderCd");
    }
    
    /**
     * setMaidenName(String fsValue)
     * 
     * @param fsValue 
     * 
     * customer mother's maiden name.
     */
    public void setMaidenName(String fsValue){
        poJSON.remove("sMaidenNm");
        poJSON.put("sMaidenNm", fsValue);
    }
    /**
     * getMaidenName()
     * 
     * @return customer mother's maiden name.
     */
    public String getMaidenName(){
        return (String) poJSON.get("sMaidenNm");
    }
    
    /**
     * setFBAccount(String fsValue)
     * 
     * @param fsValue 
     * 
     * customer's Facebook account.
     */
    public void setFBAccount(String fsValue){
        poFacebook.remove("sFBAcctxx");
        poFacebook.put("sFBAcctxx", fsValue);
    }
    /**
     * getFBAccount()
     * 
     * @return customer's Facebook account.
     */
    public String getFBAccount(){
        return (String) poFacebook.get("sFBAcctxx");
    }
    
    /**
     * isFBActive(String fsValue)
     * 
     * @param fsValue 
     * 
     * char value if facebook account is active. <br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public void isFBActive(String fsValue){
        poFacebook.remove("cAcctStat");
        poFacebook.put("cAcctStat", fsValue);
    }
    /**
     * isFBActive()
     * 
     * @return char value if facebook account is active. <br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public String isFBActive(){
        return (String) poFacebook.get("cAcctStat");
    }
    
    /**
     * setFBFriends(int fnValue)
     * 
     * @param fnValue 
     * 
     * number of facebook friends
     */
    public void setFBFriends(int fnValue){
        poFacebook.remove("nNoFriend");
        poFacebook.put("nNoFriend", fnValue);
    }
    /**
     * getFBFriends()
     * 
     * @return number of facebook friends
     */
    public int getFBFriends(){
        return (int) poFacebook.get("nNoFriend");
    }
    
    /**
     * setFBYears(int fnValue)
     * 
     * @param fnValue 
     * 
     * number of years as facebook user
     */
    public void setFBYears(int fnValue){
        poFacebook.remove("nYearxxxx");
        poFacebook.put("nYearxxxx", fnValue);
    }
    /**
     * getFBYears()
     * 
     * @return number of years as facebook user
     */
    public int getFBYears(){
        return (int) poFacebook.get("nNoFriend");
    }
    
    
    /**
     * setViberAccount(String fsValue)
     * 
     * @param fsValue 
     * 
     * customer's Viber account.
     */
    public void setViberAccount(String fsValue){
        poJSON.remove("sVibeAcct");
        poJSON.put("sVibeAcct", fsValue);
    }
    /**
     * getViberAccount()
     * 
     * @return customer's Viber account.
     */
    public String getViberAccount(){
        return (String) poJSON.get("sVibeAcct");
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
        
        System.out.println(paMobileNo.toJSONString());
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
        
        return Integer.parseInt(String.valueOf(loJSON.get("nPostYear")));
    }
    
    /**
     * setPhoneNoQty(int fnValue)
     * 
     * @param fnValue 
     * 
     * customer's number of active landline number.
     */
    public void setPhoneNoQty(int fnValue){
        int lnRow = paPhoneNox.size();
        
        if (lnRow != fnValue){
            //quantity of mobile number was reduced.
            if (lnRow > fnValue){
                int lnIndex = fnValue;
                for (int lnCtr = fnValue; lnCtr < lnRow; lnCtr ++){
                    paPhoneNox.remove(lnIndex);
                }
            } else if (lnRow < fnValue){
                JSONObject loJSON = new JSONObject();
                loJSON.put("sPhoneNox", "");
                for (int lnCtr = lnRow; lnCtr < fnValue; lnCtr ++){
                    paPhoneNox.add(loJSON);
                }
            }
        }
    }
    /**
     * getPhoneNoQty()
     * 
     * @return customer's number of active landline number.
     */
    public int getPhoneNoQty(){
        return paPhoneNox.size();
    }
    
    /**
     * setPhoneNo(int fnIndex, String fsValue)
     * 
     * @param fnIndex
     * landline number index
     * 
     * @param fsValue
     * landline number
     */
    public void setPhoneNo(int fnIndex, String fsValue){
        JSONObject loJSON = (JSONObject) paPhoneNox.get(fnIndex);
        
        loJSON.remove("sPhoneNox");
        loJSON.put("sPhoneNox", fsValue);
        paPhoneNox.set(fnIndex, loJSON);
    }
    /**
     * getPhoneNo(int fnIndex)
     * 
     * @param fnIndex
     * landline number index <br>
     * 
     * @return customer's landline number.
     */
    public String getPhoneNo(int fnIndex){
        JSONObject loJSON = (JSONObject) paPhoneNox.get(fnIndex);
        
        return (String) loJSON.get("sPhoneNox");
    }
    
    /**
     * setEmailAddQty(int fnValue)
     * 
     * @param fnValue 
     * 
     * customer's number of active email addresses.
     */
    public void setEmailAddQty(int fnValue){
        int lnRow = paEmailAdd.size();
        
        if (lnRow != fnValue){
            //quantity of mobile number was reduced.
            if (lnRow > fnValue){
                int lnIndex = fnValue;
                for (int lnCtr = fnValue; lnCtr < lnRow; lnCtr ++){
                    paEmailAdd.remove(lnIndex);
                }
            } else if (lnRow < fnValue){
                JSONObject loJSON = new JSONObject();
                loJSON.put("sEmailAdd", "");
                for (int lnCtr = lnRow; lnCtr < fnValue; lnCtr ++){
                    paEmailAdd.add(loJSON);
                }
            }
        }
    }
    /**
     * getEmailAddQty()
     * 
     * @return customer's number of active email addresses.
     */
    public int getEmailAddQty(){
        return paEmailAdd.size();
    }
    
    /**
     * setEmailAddress(int fnIndex, String fsValue)
     * 
     * @param fnIndex
     * email address index
     * 
     * @param fsValue
     * email address
     */
    public void setEmailAddress(int fnIndex, String fsValue){
        JSONObject loJSON = (JSONObject) paEmailAdd.get(fnIndex);
        
        loJSON.remove("sEmailAdd");
        loJSON.put("sEmailAdd", fsValue);
        paEmailAdd.set(fnIndex, loJSON);
    }
    /**
     * getEmailAddress(int fnIndex)
     * 
     * @param fnIndex
     * email address index <br>
     * 
     * @return customer's email address.
     */
    public String getEmailAddress(int fnIndex){
        JSONObject loJSON = (JSONObject) paEmailAdd.get(fnIndex);
        
        return (String) loJSON.get("sEmailAdd");
    }
}   
