package org.rmj.gocas;

import org.json.simple.JSONObject;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.SQLUtil;
import org.rmj.gocas.service.GOCASRestAPI;
import org.rmj.replication.utility.LogWrapper;

public class ARContractUpdate {
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
            
        
        //download the record
        JSONObject loJSON = GOCASRestAPI.Update_AR_Contract(poGRider, args[2], args[3]);
        
        if (!"success".equalsIgnoreCase((String) loJSON.get("result"))) System.exit(1);
        
        System.exit(0);
    }
}
