package org.rmj.gocas.pojo;

import java.util.ArrayList;
import org.json.simple.JSONObject;

public class DisbursementInfo implements JEntity{
    private JSONObject poJSON;
    private String psMessage = "";
    private final ArrayList paKeys;
    
    private ExpensesInfo monthly_expenses;
    private BankAccountInfo bank_account;
    private CreditCardInfo credit_card;
    private DependentInfo dependent_info;
    private PropertiesInfo properties_info;
    
    public DisbursementInfo(){
        poJSON = new JSONObject();
        poJSON.put("monthly_expenses", "");
        poJSON.put("bank_account", "");
        poJSON.put("credit_card", "");
        poJSON.put("dependent_info", "");
        poJSON.put("properties", "");
        
        paKeys = new ArrayList();
        paKeys.add("monthly_expenses");
        paKeys.add("bank_account");
        paKeys.add("credit_card");
        paKeys.add("dependent_info");
        paKeys.add("properties");
        
        monthly_expenses = new ExpensesInfo();
        bank_account = new BankAccountInfo();
        credit_card = new CreditCardInfo();
        dependent_info = new DependentInfo();
        properties_info = new PropertiesInfo();
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
        
        if (!monthly_expenses.setData((JSONObject) foData.get("monthly_expenses"))){
            System.err.println(monthly_expenses.getMessage());
            return false;
        }
        
        if (!bank_account.setData((JSONObject) foData.get("bank_account"))){
            System.err.println(bank_account.getMessage());
            return false;
        }
        
        if (!credit_card.setData((JSONObject) foData.get("credit_card"))){
            System.err.println(credit_card.getMessage());
            return false;
        }
        
        if (!dependent_info.setData((JSONObject) foData.get("dependent_info"))){
            System.err.println(dependent_info.getMessage());
            return false;
        }
        
        if (!properties_info.setData((JSONObject) foData.get("properties"))){
            System.err.println(properties_info.getMessage());
            return false;
        }

        poJSON = foData;
        return true;
    }

    @Override
    public JSONObject toJSON() {   
        poJSON.put("monthly_expenses", monthly_expenses.toJSON());
        poJSON.put("bank_account", bank_account.toJSON());
        poJSON.put("credit_card", credit_card.toJSON());
        poJSON.put("dependent_info", dependent_info.toJSON());
        poJSON.put("properties", properties_info.toJSON());
        
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
    
    public ExpensesInfo Expenses(){
        return monthly_expenses;
    }
    
    public BankAccountInfo BankAccount(){
        return bank_account;
    }
    
    public CreditCardInfo CreditCard(){
        return credit_card;
    }
    
    public DependentInfo DependentInfo(){
        return dependent_info;
    }
    
    public PropertiesInfo PropertiesInfo(){
        return properties_info;
    }
}
