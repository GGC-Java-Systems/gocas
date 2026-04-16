package org.rmj.gocas.pojo;

import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DependentInfo implements JEntity{
    private JSONObject poJSON;
    private String psMessage = "";
    private final ArrayList paKeys;
    
    private JSONArray paChildren;
    
    public DependentInfo(){
        poJSON = new JSONObject();
        poJSON.put("nHouseHld", 0);
        poJSON.put("children", "");

        paKeys = new ArrayList();
        paKeys.add("nHouseHld");
        poJSON.put("children", "");
        
        paChildren = new JSONArray();
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
                
        paChildren = (JSONArray) poJSON.get("children");
        return true;
    }

    @Override
    public JSONObject toJSON() {
        //set dependent/children array
        poJSON.remove("children");
        poJSON.put("children", paChildren);    
        
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
    
    public int getNoOfChildren(){
        JSONObject loJSON = (JSONObject) paChildren.get(paChildren.size() - 1);
        
        if ("".equals((String) loJSON.get("sFullName")) || loJSON.size() == 0){
            return paChildren.size() - 1;
        }
        
        return paChildren.size();
    }
    
    public int getNoOfDependent(){
        int lnValue = 0;
        JSONObject loJSON;
        
        for (int lnCtr = 0; lnCtr <= paChildren.size()-1; lnCtr++){
            loJSON = (JSONObject) paChildren.get(lnCtr);
            if ("1".equals((String) loJSON.get("cDependnt"))) lnValue++;
        }
        
        return lnValue;
    }
    
    /**
     * addDependent()
     * 
     * @return boolean
     * 
     * adds dependent
     */
    public boolean addDependent(){
        JSONObject loJSON = new JSONObject();
        loJSON.put("sFullName", "");
        loJSON.put("sRelatnCD", "");
        loJSON.put("nDepdAgex", 0);
        loJSON.put("cIsPupilx", "0");
        loJSON.put("sSchlName", "");
        loJSON.put("sSchlAddr", "");
        loJSON.put("sSchlTown", "");
        loJSON.put("cIsPrivte", "0");
        loJSON.put("sEducLevl", "");
        loJSON.put("cIsSchlrx", "0");
        loJSON.put("cHasWorkx", "0");
        loJSON.put("cWorkType", "0");
        loJSON.put("sCompanyx", "");
        loJSON.put("cHouseHld", "0");
        loJSON.put("cDependnt", "0");
        loJSON.put("cIsChildx", "0");
        loJSON.put("cIsMarrdx", "0");
        paChildren.add(loJSON);
        
        return true;
    }
    
    /**
     * delDependent(int fnRow)
     * 
     * @return boolean
     * 
     * removes dependent
     */
    public boolean delDependent(int fnRow){
        paChildren.remove(fnRow);
        if (paChildren.isEmpty()) return addDependent();
        
        return true;
    }
    
    /**
     * setNoOfHouseHold(int fnValue)
     * 
     * @param fnValue 
     * 
     * number of household
     */
    public void setNoOfHouseHold(int fnValue){
        poJSON.remove("nHouseHld");
        poJSON.put("nHouseHld", fnValue);
    }
    /**
     * getNoOfHouseHold()
     * 
     * @return number of household
     */
    public int getNoOfHouseHold(){
        return (int) poJSON.get("nHouseHld");
    }
    
    /**
     * setFullName(int fnRow, String fsValue)
     * 
     * full name of dependent 
     * 
     * @param fnRow
     * @param fsValue 
     */
    public void setFullName(int fnRow, String fsValue){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        loJSON.remove("sFullName");
        loJSON.put("sFullName", fsValue);
        paChildren.set(fnRow, loJSON);
    }
    /**
     * getFullName(int fnRow)
     * 
     * @param fnRow
     * @return full name of dependent 
     */
    public String getFullName(int fnRow){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        return (String) loJSON.get("sFullName");
    }
    
    /**
     * setRelation(String fsValue)
     * 
     * @param fnRow
     * @param fsValue
     * 
     * char value for dependent relation to applicant. <br>
     * 
     * 0 - Parents <br>
     * 1 - Spouse <br>
     * 2 - Relatives <br>
     * 3 - Friends <br>
     * 4 - Child
     */
    public void setRelation(int fnRow, String fsValue){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        loJSON.remove("sReltnCde");
        loJSON.put("sReltnCde", fsValue);
        paChildren.set(fnRow, loJSON);
    }
    /**
     * getRelation()
     * 
     * @param fnRow
     * @return char value for dependent relation to applicant. <br>
     * 
     * 0 - Parents <br>
     * 1 - Spouse <br>
     * 3 - Friends <br>
     * 4 - Child
     */
    public String getRelation(int fnRow){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        return (String) loJSON.get("sReltnCde");
    }
    
    /**
     * setAge(int fnRow, int fnValue)
     * 
     * dependent age
     * 
     * @param fnRow
     * @param fnValue 
     */
    public void setAge(int fnRow, int fnValue){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        loJSON.remove("nDepdAgex");
        loJSON.put("nDepdAgex", fnValue);
        paChildren.set(fnRow, loJSON);
    }
    /**
     * getAge(int fnRow)
     * 
     * @param fnRow
     * @return dependent age
     */
    public int getAge(int fnRow){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        return (int) loJSON.get("nDepdAgex");
    }
    
    /**
     * IsStudent(int fnRow, String fsValue)
     * 
     * @param fnRow
     * @param fsValue 
     * 
     * is the dependent a student?<br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public void IsStudent(int fnRow, String fsValue){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        loJSON.remove("cIsPupilx");
        loJSON.put("cIsPupilx", fsValue);
        paChildren.set(fnRow, loJSON);
    }
    /**
     * 
     * 
     * @param fnRow
     * @return is the dependent a student?<br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public String IsStudent(int fnRow){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        return (String) loJSON.get("cIsPupilx");
    }
    
    /**
     * setSchoolName(int fnRow, String fsValue)
     * 
     * @param fnRow
     * @param fsValue 
     * 
     * school name of the dependent
     */
    public void setSchoolName(int fnRow, String fsValue){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        loJSON.remove("sSchlName");
        loJSON.put("sSchlName", fsValue);
        paChildren.set(fnRow, loJSON);
    }
    /**
     * getSchoolName(int fnRow)
     * 
     * @param fnRow
     * @return school name of the dependent
     */
    public String getSchoolName(int fnRow){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        return (String) loJSON.get("sSchlName");
    }
    
    /**
     * setSchoolAddress(int fnRow, String fsValue)
     * 
     * @param fnRow
     * @param fsValue 
     * 
     * school address of the dependent
     */
    public void setSchoolAddress(int fnRow, String fsValue){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        loJSON.remove("sSchlAddr");
        loJSON.put("sSchlAddr", fsValue);
        paChildren.set(fnRow, loJSON);
    }
    /**
     * getSchoolAddress(int fnRow)
     * 
     * @param fnRow
     * @return school address of the dependent
     */
    public String getSchoolAddress(int fnRow){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        return (String) loJSON.get("sSchlAddr");
    }
    
    /**
     * setSchoolTown(int fnRow, String fsValue)
     * 
     * @param fnRow
     * @param fsValue 
     * 
     * town if the school if dependent
     */
    public void setSchoolTown(int fnRow, String fsValue){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        loJSON.remove("sSchlTown");
        loJSON.put("sSchlTown", fsValue);
        paChildren.set(fnRow, loJSON);
    }
    /**
     * getSchoolTown(int fnRow)
     * 
     * @param fnRow
     * @return town if the school if dependent
     */
    public String getSchoolTown(int fnRow){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        return (String) loJSON.get("sSchlTown");
    }
    
    /**
     * IsPrivateSchool(int fnRow, String fsValue)
     * 
     * @param fnRow
     * @param fsValue 
     * 
     * is dependent studying in private school?<br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public void IsPrivateSchool(int fnRow, String fsValue){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        loJSON.remove("cIsPrivte");
        loJSON.put("cIsPrivte", fsValue);
        paChildren.set(fnRow, loJSON);
    }
    /**
     * IsPrivateSchool(int fnRow)
     * 
     * @param fnRow
     * @return is dependent studying in private school?<br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public String IsPrivateSchool(int fnRow){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        return (String) loJSON.get("cIsPrivte");
    }
    
    /**
     * setEducationalLevel(int fnRow, String fsValue)
     * 
     * @param fnRow
     * @param fsValue 
     * 
     * educational level of the dependent
     */
    public void setEducationalLevel(int fnRow, String fsValue){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        loJSON.remove("sEducLevl");
        loJSON.put("sEducLevl", fsValue);
        paChildren.set(fnRow, loJSON);
    }
    /**
     * getEducationalLevel(int fnRow)
     * 
     * @param fnRow
     * @return educational level of the dependent
     */
    public String getEducationalLevel(int fnRow){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        return (String) loJSON.get("sEducLevl");
    }
    
    /**
     * IsScholar(int fnRow, String fsValue)
     * 
     * @param fnRow
     * @param fsValue 
     * 
     * is dependent a scholar?<br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public void IsScholar(int fnRow, String fsValue){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        loJSON.remove("cIsSchlrx");
        loJSON.put("cIsSchlrx", fsValue);
        paChildren.set(fnRow, loJSON);
    }
    /**
     * IsScholar(int fnRow)
     * 
     * @param fnRow
     * @return is dependent a scholar?<br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public String IsScholar(int fnRow){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        return (String) loJSON.get("cIsSchlrx");
    }
    
    /**
     * IsHouseHold(int fnRow, String fsValue)
     * 
     * @param fnRow
     * @param fsValue 
     * 
     * is dependent working?<br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public void IsWorking(int fnRow, String fsValue){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        loJSON.remove("cHasWorkx");
        loJSON.put("cHasWorkx", fsValue);
        paChildren.set(fnRow, loJSON);
    }
    /**
     * IsWorking(int fnRow)
     * 
     * @param fnRow
     * @return is dependent working?<br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public String IsWorking(int fnRow){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        return (String) loJSON.get("cHasWorkx");
    }
    
    /**
     * setWorkType(int fnRow, String fsValue)
     * 
     * @param fnRow
     * @param fsValue 
     * 
     * char value on where is the dependent working. <br>
     * 
     * 0 - Private <br>
     * 1 - Public <br>
     * 2 - Self-Employed
     */
    public void setWorkType(int fnRow, String fsValue){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        loJSON.remove("cWorkType");
        loJSON.put("cWorkType", fsValue);
        paChildren.set(fnRow, loJSON);
    }
    /**
     * getWorkType(int fnRow)
     * 
     * @param fnRow
     * @return char value on where is the dependent working. <br>
     * 
     * 0 - Private <br>
     * 1 - Public <br>
     * 2 - Self-Employed
     */
    public String getWorkType(int fnRow){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        return (String) loJSON.get("cWorkType");
    }
    
    /**
     * setCompany(int fnRow, String fsValue)
     * 
     * @param fnRow
     * @param fsValue 
     * 
     * company name of the dependent on where he is working.
     */
    public void setCompany(int fnRow, String fsValue){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        loJSON.remove("sCompanyx");
        loJSON.put("sCompanyx", fsValue);
        paChildren.set(fnRow, loJSON);
    }
    /**
     * getCompany(int fnRow)
     * 
     * @param fnRow
     * @return company name of the dependent on where he is working.
     */
    public String getCompany(int fnRow){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        return (String) loJSON.get("sCompanyx");
    }
    
    /**
     * IsHouseHold(int fnRow, String fsValue)
     * 
     * @param fnRow
     * @param fsValue 
     * 
     * is household?<br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public void IsHouseHold(int fnRow, String fsValue){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        loJSON.remove("cHouseHld");
        loJSON.put("cHouseHld", fsValue);
        paChildren.set(fnRow, loJSON);
    }
    /**
     * IsHouseHold(int fnRow)
     * 
     * @param fnRow
     * @return is household?<br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public String IsHouseHold(int fnRow){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        return (String) loJSON.get("cHouseHld");
    }
    /**
     * IsDependent(int fnRow, String fsValue)
     * 
     * @param fnRow
     * @param fsValue 
     * 
     * is dependent?<br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public void IsDependent(int fnRow, String fsValue){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        loJSON.remove("cDependnt");
        loJSON.put("cDependnt", fsValue);
        paChildren.set(fnRow, loJSON);
    }
    /**
     * IsDependent(int fnRow)
     * 
     * @param fnRow
     * @return is dependent?<br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public String IsDependent(int fnRow){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        return (String) loJSON.get("cDependnt");
    }
    
    /**
     * IsChild(int fnRow, String fsValue)
     * 
     * @param fnRow
     * @param fsValue 
     * 
     * is the dependent is his child?<br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public void IsChild(int fnRow, String fsValue){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        loJSON.remove("cIsChildx");
        loJSON.put("cIsChildx", fsValue);
        paChildren.set(fnRow, loJSON);
    }
    /**
     * IsChild(int fnRow)
     * 
     * @param fnRow
     * @return is the dependent is his child?<br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public String IsChild(int fnRow){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        return (String) loJSON.get("cIsChildx");
    }
    
    /**
     * IsMarried(int fnRow, String fsValue)
     * 
     * @param fnRow
     * @param fsValue 
     * 
     * is the dependent married?<br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public void IsMarried(int fnRow, String fsValue){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        loJSON.remove("cIsMarrdx");
        loJSON.put("cIsMarrdx", fsValue);
        paChildren.set(fnRow, loJSON);
    }
    /**
     * IsMarried(int fnRow)
     * 
     * @param fnRow
     * @return is the dependent married?<br>
     * 
     * 0 - No <br>
     * 1 - Yes
     */
    public String IsMarried(int fnRow){
        JSONObject loJSON = (JSONObject) paChildren.get(fnRow);
        return (String) loJSON.get("cIsMarrdx");
    }
}
    