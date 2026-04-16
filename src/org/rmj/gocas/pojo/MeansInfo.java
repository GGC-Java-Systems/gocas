package org.rmj.gocas.pojo;

import java.util.ArrayList;
import java.util.Iterator;
import org.json.simple.JSONObject;

public class MeansInfo implements JEntity{
    private JSONObject poJSON;
    private String psMessage = "";
    private final ArrayList paKeys;
    
    private JSONObject poEmployed;
    private JSONObject poOthrIncm;
    
    private MeansEmployed means_employed;
    private MeansSelfEmployed means_self_employed;
    private MeansPensioner means_pensioner;
    private MeansFinancer means_financer;
    
    public MeansInfo(){
        poJSON = new JSONObject();
        poJSON.put("cIncmeSrc", "0");
        poJSON.put("employed", "");
        poJSON.put("self_employed", "");
        poJSON.put("pensioner", "");
        poJSON.put("financed", "");
        poJSON.put("other_income", "");
        
        paKeys = new ArrayList();
        paKeys.add("cIncmeSrc");
        paKeys.add("employed");
        paKeys.add("self_employed");
        paKeys.add("pensioner");
        paKeys.add("financed");
        paKeys.add("other_income");
  
        poOthrIncm = new JSONObject();
        poOthrIncm.put("sOthrIncm", "");
        poOthrIncm.put("nOthrIncm", "");
        
        means_employed = new MeansEmployed();
        means_self_employed = new MeansSelfEmployed();
        means_pensioner = new MeansPensioner();
        means_financer = new MeansFinancer();
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
        
        means_employed.setData((JSONObject) foData.get("employed"));
        means_self_employed.setData((JSONObject) foData.get("self_employed"));
        means_pensioner.setData((JSONObject) foData.get("pensioner"));
        means_financer.setData((JSONObject) foData.get("financed"));
        
        poJSON = foData;
        return true;
    }

    @Override
    public JSONObject toJSON() {                                 
        poJSON.remove("other_income");
        poJSON.put("other_income", poOthrIncm);
        
        poJSON.remove("employed");
        poJSON.put("employed", means_employed.toJSON());
        
        poJSON.remove("self_employed");
        poJSON.put("self_employed", means_self_employed.toJSON());
        
        poJSON.remove("pensioner");
        poJSON.put("pensioner", means_pensioner.toJSON());
        
        poJSON.remove("financed");
        poJSON.put("financed", means_financer.toJSON());
            
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
     * setOtherIncomeNature(String fsValue)
     * 
     * @param fsValue 
     * 
     * applicant's other income nature.
     */
    public void setOtherIncomeNature(String fsValue){
        poOthrIncm.remove("sOthrIncm");
        poOthrIncm.put("sOthrIncm", fsValue);
    }
    /**
     * getOtherIncomeNature()
     * 
     * @return applicant's other income nature.
     */
    public String getOtherIncomeNature(){
        return (String) poOthrIncm.get("sOthrIncm");
    }
    
    /**
     * setOtherIncomeAmount(long fnValue)
     * 
     * @param fnValue 
     * 
     * applicant's other income amount.
     */
    public void setOtherIncomeAmount(long fnValue){
        poOthrIncm.remove("nOthrIncm");
        poOthrIncm.put("sOthrIncm", fnValue);
    }
    /**
     * getOtherIncomeAmount()
     * 
     * @return applicant's other income amount.
     */
    public long getOtherIncomeAmount(){
        return (long) poOthrIncm.get("sOthrIncm");
    }
 
    /**
     * EmployedInfo()
     * 
     * @return other information object for employed applicants.
     */
    public MeansEmployed EmployedInfo(){
        return means_employed;
    }
    
    /**
     * SelfEmployedInfo()
     * 
     * @return other information object for employed applicants.
     */
    public MeansSelfEmployed SelfEmployedInfo(){
        return means_self_employed;
    }
    
    /**
     * FinancerInfo()
     * 
     * @return other information object for financed applicants.
     */
    public MeansFinancer FinancerInfo(){
        return means_financer;        
    }
    
    /**
     * PensionerInfo()
     * 
     * @return other information object for pensioner applicants.
     */
    public MeansPensioner PensionerInfo(){
        return means_pensioner;
    }
}
