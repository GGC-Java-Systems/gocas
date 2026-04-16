package org.rmj.gocas.pojo;

import java.util.ArrayList;
import org.json.simple.JSONObject;

public class ExpensesInfo implements JEntity{
    private JSONObject poJSON;
    private String psMessage = "";
    private final ArrayList paKeys;
    
    public ExpensesInfo(){
        poJSON = new JSONObject();
        poJSON.put("nElctrcBl", 0.00);
        poJSON.put("nWaterBil", 0.00);
        poJSON.put("nFoodAllw", 0.00);
        poJSON.put("nLoanAmtx", 0.00);
        
        paKeys = new ArrayList();
        paKeys.add("nElctrcBl");
        paKeys.add("nWaterBil");
        paKeys.add("nFoodAllw");
        paKeys.add("nLoanAmtx");
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
     * setElectricBill(double fnValue)
     * 
     * @param fnValue 
     * 
     * monthly electric bill
     */
    public void setElectricBill(double fnValue){
        poJSON.remove("nElctrcBl");
        poJSON.put("nElctrcBl", fnValue);
    }
    /**
     * getElectricBill()
     * 
     * @return monthly electric bill
     */
    public String getElectricBill(){
        return String.valueOf(poJSON.get("nElctrcBl"));
    }
    
    /**
     * setWaterBill(double fnValue)
     * 
     * @param fnValue 
     * 
     * monthly water bill
     */
    public void setWaterBill(double fnValue){
        poJSON.remove("nWaterBil");
        poJSON.put("nWaterBil", fnValue);
    }
    /**
     * getWaterBill()
     * 
     * @return monthly water bill
     */
    public String getWaterBill(){
        return String.valueOf(poJSON.get("nWaterBil"));
    }
    
    /**
     * setFoodAllowance(double fnValue)
     * 
     * @param fnValue 
     * 
     * monthly food allowance
     */
    public void setFoodAllowance(double fnValue){
        poJSON.remove("nFoodAllw");
        poJSON.put("nFoodAllw", fnValue);
    }
    /**
     * getFoodAllowance()
     * 
     * @return monthly food allowance
     */
    public String getFoodAllowance(){
        return String.valueOf(poJSON.get("nFoodAllw"));
    }
    
    /**
     * setLoanAmount(double fnValue)
     * 
     * @param fnValue 
     * 
     * total loans
     */
    public void setLoanAmount(double fnValue){
        poJSON.remove("nLoanAmtx");
        poJSON.put("nLoanAmtx", fnValue);
    }
    /**
     * getLoanAmount()
     * 
     * @return total loans
     */
    public String getLoanAmount(){
        return String.valueOf(poJSON.get("nLoanAmtx"));
    }
}
