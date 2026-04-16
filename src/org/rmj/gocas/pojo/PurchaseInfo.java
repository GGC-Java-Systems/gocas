package org.rmj.gocas.pojo;

import java.util.ArrayList;
import org.json.simple.JSONObject;

public class PurchaseInfo implements JEntity{
    private JSONObject poJSON;
    private String psMessage = "";
    private final ArrayList paKeys;
    
    public PurchaseInfo(){
        poJSON = new JSONObject();
        poJSON.put("dAppliedx", "");
        poJSON.put("cUnitAppl", "");
        poJSON.put("cApplType", "");
        poJSON.put("sUnitAppl", "");
        poJSON.put("sModelIDx", "");
        poJSON.put("nDownPaym", 0.00);
        poJSON.put("nAcctTerm", 0);
        poJSON.put("nMonAmort", 0.00);
        poJSON.put("dTargetDt", "");
        poJSON.put("sBranchCd", ""); //2019-12-05
        
        paKeys = new ArrayList();
        paKeys.add("dAppliedx");
        paKeys.add("cUnitAppl");
        paKeys.add("cApplType");
        paKeys.add("sUnitAppl");
        paKeys.add("sModelIDx");
        paKeys.add("nDownPaym");
        paKeys.add("nAcctTerm");
        paKeys.add("nMonAmort");
        paKeys.add("dTargetDt");
        paKeys.add("sBranchCd"); //2019-12-05
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
     * setDateApplied(String fsValue)
     * 
     * @param fsValue
     * 
     * application date of GOCAS. Format YYYY-MM-DD
     */
    public void setDateApplied(String fsValue){
        poJSON.remove("dAppliedx");
        poJSON.put("dAppliedx", fsValue);
    }
    /**
     * getDateApplied()
     * 
     * @return application date of GOCAS. Format YYYY-MM-DD
     */
    public String getDateApplied(){
        return (String) poJSON.get("dAppliedx");
    }
    
    /**
     * setAppliedFor(String fsValue)
     * 
     * @param fsValue
     * 
     * char value for what is applied for GOCAS. <br>
     * 
     * 0 - Mobile Phone <br>
     * 1 - Motorcycle <br>
     * 2 - Lending/Financing <br>
     * 3 - Cars <br>
     * 4 - Services
     */
    public void setAppliedFor(String fsValue){
        poJSON.remove("cUnitAppl");
        poJSON.put("cUnitAppl", fsValue);
    }
    /**
     * getAppliedFor()
     * 
     * @return char value of what is applied for GOCAS. <br>
     * 
     * 0 - Mobile Phone <br>
     * 1 - Motorcycle <br>
     * 2 - Lending/Financing <br>
     * 3 - Cars <br>
     * 4 - Services
     */
    public String getAppliedFor(){
        return (String) poJSON.get("cUnitAppl");
    }
    
    /**
     * setCustomerType(String fsValue)
     * 
     * @param fsValue
     * 
     * char value for customer type. <br>
     * 
     * 0 - New Customer <br>
     * 1 - Repeat Customer
     */
    public void setCustomerType(String fsValue){
        poJSON.remove("cApplType");
        poJSON.put("cApplType", fsValue);
    }
    /**
     * getCustomerType()
     * 
     * @return char value for customer type. <br>
     * 
     * 0 - New Customer <br>
     * 1 - Repeat Customer
     */
    public String getCustomerType(){
        return (String) poJSON.get("cApplType");
    }
    
    /**
     * setBrandName(String fsValue)
     * 
     * @param fsValue 
     * 
     * unit brand.
     */
    public void setBrandName(String fsValue){
        poJSON.remove("sUnitAppl");
        poJSON.put("sUnitAppl", fsValue);
    }
    /**
     * getBrandName()
     * 
     * @return unit brand.
     */
    public String getBrandName(){
        return (String) poJSON.get("sUnitAppl");
    }
    
    /**
     * setModelID(String fsValue)
     * 
     * @param fsValue
     * 
     * selected model for GOCAS application.
     */
    public void setModelID(String fsValue){
        poJSON.remove("sModelIDx");
        poJSON.put("sModelIDx", fsValue);
    }
    /**
     * getModelID()
     * 
     * @return selected model for GOCAS application.
     */
    public String getModelID(){
        return (String) poJSON.get("sModelIDx");
    }
    
    /**
     * setDownPayment(double fnValue)
     * 
     * @param fnValue
     * 
     * down payment for GOCAS application.
     */
    public void setDownPayment(double fnValue){
        poJSON.remove("nDownPaym");
        poJSON.put("nDownPaym", fnValue);
    }
    /**
     * getDownPayment()
     * 
     * @return down payment for GOCAS application.
     */
    public double getDownPayment(){
        return (double) poJSON.get("nDownPaym");
    }
    
    /**
     * setAccountTerm(double fnValue)
     * 
     * @param fnValue
     * 
     * preferred account term for GOCAS application (in years).
     */
    public void setAccountTerm(int fnValue){
        poJSON.remove("nAcctTerm");
        poJSON.put("nAcctTerm", fnValue);
    }
    /**
     * getAccountTerm()
     * 
     * @return preferred account term for GOCAS application (in years).
     */
    public int getAccountTerm(){
        return (int) poJSON.get("nAcctTerm");
    }
    
    /**
     * setMonthlyAmortization(double fnValue)
     * 
     * @param fnValue
     * 
     * monthly amortization based on the down payment, account term and model selected. 
     */
    public void setMonthlyAmortization(double fnValue){
        poJSON.remove("nMonAmort");
        poJSON.put("nMonAmort", fnValue);
    }
    /**
     * getMonthlyAmortization()
     * 
     * @return monthly amortization based on the down payment, account term and model selected. 
     */
    public double getMonthlyAmortization(){
        return (double) poJSON.get("nMonAmort");
    }
    
    /**
     * setTargetPurchase(String fsValue)
     * 
     * @param fsValue
     * 
     * target date for purchasing the product/service applied. Format YYYY-MM-DD
     */
    public void setTargetPurchase(String fsValue){
        poJSON.remove("dTargetDt");
        poJSON.put("dTargetDt", fsValue);
    }
    /**
     * getTargetPurchase()
     * 
     * @return target date for purchasing the product/service applied. Format YYYY-MM-DD
     */
    public String getTargetPurchase(){
        return (String) poJSON.get("dTargetDt");
    }
    
    /**
     * setPreferedBranch(String fsValue)
     * 
     * @param fsValue 
     * 
     * branch code of the preferred branch.
     */
    public void setPreferedBranch(String fsValue){
        poJSON.remove("sBranchCd");
        poJSON.put("sBranchCd", fsValue);
    }
    /**
     * getPreferedBranch()
     * 
     * @return branch code of the preferred branch.
     */
    public String getPreferedBranch(){
        return (String) poJSON.get("sBranchCd");
    }
}
