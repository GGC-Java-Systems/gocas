package org.rmj.gocas;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.rmj.gocas.pojo.ApplicantInfo;
import org.rmj.gocas.pojo.CoMakerInfo;
import org.rmj.gocas.pojo.DisbursementInfo;
import org.rmj.gocas.pojo.MeansInfo;
import org.rmj.gocas.pojo.OtherInfo;
import org.rmj.gocas.pojo.PurchaseInfo;
import org.rmj.gocas.pojo.ResidenceInfo;
import org.rmj.gocas.pojo.SpouseInfo;

public class GOCASApplication {
    private PurchaseInfo purchase_info;
    private ApplicantInfo applicant_info;
    private ResidenceInfo residence_info;
    private MeansInfo means_info;
    private OtherInfo other_info;
    private SpouseInfo spouse_info;
    private MeansInfo spouse_means;
    private CoMakerInfo comaker_info;
    private DisbursementInfo disbursement_info;
    
    public GOCASApplication(){
        purchase_info = new PurchaseInfo();       
        applicant_info = new ApplicantInfo();
        residence_info = new ResidenceInfo();
        means_info = new MeansInfo();
        other_info = new OtherInfo();
        comaker_info = new CoMakerInfo();
        spouse_info = new SpouseInfo();
        spouse_means = new MeansInfo();
        disbursement_info = new DisbursementInfo();
//        dependent_info = new DependentInfo();
//        properties_info = new PropertiesInfo();
    }
        
    public PurchaseInfo PurchaseInfo(){
        return purchase_info;
    }
    
    public ApplicantInfo ApplicantInfo(){
        return applicant_info;
    }
    
    public ResidenceInfo ResidenceInfo(){
        return residence_info;
    }
    
    public MeansInfo MeansInfo(){
        return means_info;
    }
    
    public OtherInfo OtherInfo(){
        return other_info;
    }
    
    public CoMakerInfo CoMakerInfo(){
        return comaker_info;
    }
    
    public SpouseInfo SpouseInfo(){
        return spouse_info;
    }
    
    public MeansInfo SpouseMeansInfo(){
        return spouse_means;
    }
    
    public DisbursementInfo DisbursementInfo(){
        return disbursement_info;
    }   
    
    public boolean setData(String fsJSON){
        JSONParser loParser = new JSONParser();
        
        try {
            //fix string encoding
            fsJSON = new String(fsJSON.getBytes("ISO-8859-1"), "UTF-8");
            
            JSONObject loJSON = (JSONObject) loParser.parse(fsJSON);
            
            purchase_info.setPreferedBranch((String) loJSON.get("sBranchCd"));
            purchase_info.setDateApplied((String) loJSON.get("dAppliedx"));
            purchase_info.setAppliedFor((String) loJSON.get("cUnitAppl"));
            purchase_info.setDownPayment(Double.parseDouble(loJSON.get("nDownPaym").toString()));
            purchase_info.setDateApplied((String) loJSON.get("dCreatedx"));
            purchase_info.setCustomerType((String) loJSON.get("cApplType"));
            purchase_info.setModelID((String) loJSON.get("sModelIDx"));
            purchase_info.setAccountTerm(Integer.parseInt(String.valueOf(loJSON.get("nAcctTerm"))));
            purchase_info.setMonthlyAmortization(Double.parseDouble(loJSON.get("nMonAmort").toString()));
            purchase_info.setTargetPurchase((String) loJSON.get("dTargetDt"));   
            purchase_info.setBrandName((String) loJSON.get("sUnitAppl"));
            
            if (!applicant_info.setData((JSONObject) loParser.parse(loJSON.get("applicant_info").toString()))){
                System.err.println(applicant_info.getMessage());
                return false;
            }
            
            if (!residence_info.setData((JSONObject) loParser.parse(loJSON.get("residence_info").toString()))){
                System.err.println(residence_info.getMessage());
                return false;
            }
            
            if (!means_info.setData((JSONObject) loParser.parse(loJSON.get("means_info").toString()))){
                System.err.println(means_info.getMessage());
                return false;
            }
            
            if (!other_info.setData((JSONObject) loParser.parse(loJSON.get("other_info").toString()))){
                System.err.println(other_info.getMessage());
                return false;
            }
            
            if (!comaker_info.setData((JSONObject) loParser.parse(loJSON.get("comaker_info").toString()))){
                System.err.println(comaker_info.getMessage());
                return false;
            }
                   
            if (applicant_info.getCivilStatus().equals("1") ||
                applicant_info.getCivilStatus().equals("5")){
                
                if (!spouse_info.setData((JSONObject) loParser.parse(loJSON.get("spouse_info").toString()))){
                    System.err.println(spouse_info.getMessage());
                    return false;
                }
                
                if (!spouse_means.setData((JSONObject) loParser.parse(loJSON.get("spouse_means").toString()))){
                    System.err.println(spouse_means.getMessage());
                    return false;
                }
            }
            
            if (!disbursement_info.setData((JSONObject) loParser.parse(loJSON.get("disbursement_info").toString()))){
                System.err.println(disbursement_info.getMessage());
                return false;
            }                      
        } catch (ParseException | UnsupportedEncodingException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        
        return true;
    }
    
    public JSONObject toJSON(){        
        JSONObject loJSON = new JSONObject();
        loJSON.put("sBranchCd", purchase_info.getPreferedBranch());
        loJSON.put("dAppliedx", purchase_info.getDateApplied());
        loJSON.put("sClientNm", applicant_info.getClientName());
        loJSON.put("cUnitAppl", purchase_info.getAppliedFor());
        loJSON.put("nDownPaym", purchase_info.getDownPayment());
        loJSON.put("dCreatedx", purchase_info.getDateApplied());
        
        loJSON.put("cApplType", purchase_info.getCustomerType());
        loJSON.put("sUnitAppl", purchase_info.getBrandName());
        loJSON.put("sModelIDx", purchase_info.getModelID());
        loJSON.put("nAcctTerm", purchase_info.getAccountTerm());
        loJSON.put("nMonAmort", purchase_info.getMonthlyAmortization());
        loJSON.put("dTargetDt", purchase_info.getTargetPurchase());

        //loJSON.put("purchase_info", purchase_info.toJSON());
        loJSON.put("applicant_info", applicant_info.toJSON());
        loJSON.put("residence_info", residence_info.toJSON());
        loJSON.put("means_info", means_info.toJSON());
        loJSON.put("other_info", other_info.toJSON());
        loJSON.put("comaker_info", comaker_info.toJSON());
        loJSON.put("disbursement_info", disbursement_info.toJSON());
        
        if (applicant_info.getCivilStatus().equals("1") ||
            applicant_info.getCivilStatus().equals("5")){
            loJSON.put("spouse_info", spouse_info.toJSON());
            loJSON.put("spouse_means", spouse_means.toJSON());
        }
        
        return loJSON;
    }
    
    public String toJSONString(){
        return toJSON().toJSONString();
    }
}
