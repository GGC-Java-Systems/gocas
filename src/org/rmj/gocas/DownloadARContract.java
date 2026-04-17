package org.rmj.gocas;

import org.json.simple.JSONObject;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.SQLUtil;
import org.rmj.gocas.service.GOCASRestAPI;
import org.rmj.replication.utility.LogWrapper;

public class DownloadARContract {
    public static void main(String[] args) {
        LogWrapper logwrapr = new LogWrapper("DownloadARContract", "gocas.log");
        
        if (args.length <= 0){
            logwrapr.severe("Invalid parameter detected.");
            System.exit(1);
        }
        
        if (args[0].isEmpty()){
            logwrapr.severe("Invalid parameter detected.");
            System.exit(1);
        }
        
        String path;
        if(System.getProperty("os.name").toLowerCase().contains("win")){
            path = "D:/GGC_Java_Systems";
        }
        else{
            path = "/srv/GGC_Java_Systems";
        }
        System.setProperty("sys.default.path.config", path);
        
        GRider poGRider = new GRider(args[0]);
        
        if (!poGRider.loadUser(args[0], args[1])){
            System.out.println(poGRider.getMessage() + poGRider.getErrMsg());
            System.exit(1);
        }
            
        String lsSQL = "DELETE FROM MC_AR_Contract_Info WHERE sReferNox = " + SQLUtil.toSQL(args[2]);
        
        //delete the local records
        poGRider.executeUpdate(lsSQL);
        
        //download the record
        JSONObject loJSON = GOCASRestAPI.Request_AR_Contract(poGRider, args[2]);
        
        if ("success".equalsIgnoreCase((String) loJSON.get("result"))){    
            lsSQL = "INSERT INTO MC_AR_Contract_Info SET" +
                    "  sTransNox = " + SQLUtil.toSQL((String) loJSON.get("sTransNox")) +
                    ", sBranchCd = " + SQLUtil.toSQL((String) loJSON.get("sBranchCd")) +
                    ", dTransact = " + SQLUtil.toSQL((String) loJSON.get("dTransact")) +
                    ", sClientID = " + SQLUtil.toSQL((String) loJSON.get("sClientID")) +
                    ", sReferNox = " + SQLUtil.toSQL((String) loJSON.get("sReferNox")) +
                    ", sAcctNmbr = " + SQLUtil.toSQL((String) loJSON.get("sAcctNmbr")) +
                    ", dPurchase = " + SQLUtil.toSQL((String) loJSON.get("dPurchase")) +
                    ", sDRNoxxxx = " + SQLUtil.toSQL((String) loJSON.get("sDRNoxxxx")) +
                    ", sSerialID = " + SQLUtil.toSQL((String) loJSON.get("sSerialID")) +
                    ", nDownPaym = " + SQLUtil.toSQL(Double.valueOf((String) loJSON.get("nDownPaym"))) +
                    ", nAcctTerm = " + SQLUtil.toSQL(Double.valueOf((String) loJSON.get("nAcctTerm"))) +
                    ", nMonAmort = " + SQLUtil.toSQL(Double.valueOf((String) loJSON.get("nMonAmort"))) +
                    ", nRebatesx = " + SQLUtil.toSQL(Double.valueOf((String) loJSON.get("nRebatesx"))) +
                    ", nPenaltyx = " + SQLUtil.toSQL(Double.valueOf((String) loJSON.get("nPenaltyx"))) +
                    ", nPNValuex = " + SQLUtil.toSQL(Double.valueOf((String) loJSON.get("nPNValuex"))) +
                    ", dFirstPay = " + SQLUtil.toSQL((String) loJSON.get("dFirstPay")) +
                    ", sRemarksx = " + SQLUtil.toSQL((String) loJSON.get("sRemarksx")) +
                    ", cTranStat = " + SQLUtil.toSQL((String) loJSON.get("cTranStat")) +
                    ", sModified = " + SQLUtil.toSQL((String) loJSON.get("sModified")) +
                    ", dModified = " + SQLUtil.toSQL((String) loJSON.get("dModified")) +
                    ", dTimeStmp = " + SQLUtil.toSQL((String) loJSON.get("dTimeStmp"));
            
            poGRider.executeUpdate(lsSQL);
        }
        
        System.exit(0);
    }
}
