package org.rmj.gocas.pojo;

import org.json.simple.JSONObject;

public class SpouseInfo implements JEntity{
    private String psMessage = "";
    private ApplicantInfo personal_info;
    private ResidenceInfo residence_info;
    
    public SpouseInfo(){
        personal_info = new ApplicantInfo();
        residence_info = new ResidenceInfo();
    }
    
    public ApplicantInfo PersonalInfo(){
        return personal_info;
    }
    
    public ResidenceInfo ResidenceInfo(){
        return residence_info;
    }
    
    @Override
    public boolean setData(JSONObject foData) {        
        if (!personal_info.setData((JSONObject) foData.get("personal_info"))){
            psMessage = "Unable to set personal info for spouse.";
            return false;
        }
        
        if (!residence_info.setData((JSONObject) foData.get("residence_info"))){
            psMessage = "Unable to set residence info for spouse.";
            return false;
        }
            
        return true;
    }

    @Override
    public JSONObject toJSON() {  
        JSONObject loJSON = new JSONObject();
        loJSON.put("personal_info", personal_info.toJSON());
        loJSON.put("residence_info", residence_info.toJSON());
        return loJSON;
    }
    
    @Override
    public String toJSONString() {
        return toJSON().toJSONString();
    }
    
    @Override
    public String getMessage() {
        return psMessage;
    }
}
