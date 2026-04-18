package org.rmj.gocas;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.rmj.appdriver.MiscUtil;
import org.rmj.appdriver.SQLUtil;
import org.rmj.appdriver.agent.GRiderX;
import org.rmj.replication.utility.LogWrapper;

public class ExportDeedOfSurrender {
    public static void main(String [] args){
        LogWrapper logwrapr = new LogWrapper("DCP.ExportFile", "dcp.log");

        if (args.length != 1){
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
        
        GRiderX poGRider = new GRiderX("IntegSys");
        
        if (!poGRider.logUser("IntegSys", "M001111122")){
            logwrapr.severe(poGRider.getErrMsg());
            logwrapr.severe("GRiderX has error...");
            System.exit(1);
        }
        
        if(!poGRider.getErrMsg().isEmpty()){
            logwrapr.severe(poGRider.getErrMsg());
            logwrapr.severe("GRiderX has error...");
            System.exit(1);
        }        
        
        try {
            String lsSQL = "SELECT" +
                        "  a.sClientNm" +
                        ", IFNULL(a.sCatInfox, a.sDetlInfo) sDetlInfo" +
                        ", a.sTransNox" +
                        ", c.sEngineNo" +
                        ", c.sFrameNox" +
                        ", b.sAcctNmbr" +
                        ", b.nDownPaym" +
                        ", b.nAcctTerm" +
                        ", b.nMonAmort" +
                        ", b.nRebatesx" +
                        ", b.nPNValuex" +
                        ", b.nPenaltyx" +
                        ", IFNULL(b.dFirstPay, '') dFirstPay" +
                        ", e.sBrandNme" +
                        ", CONCAT(d.sModelNme, ' - ', d.sModelCde) sModelNme" +
                        ", f.sColorNme" +
                        ", IFNULL(b.sTransNox, '') xTransNox" +
                        ", a.sClientNm" +
                    " FROM Credit_Online_Application a" +
                        " LEFT JOIN MC_AR_Contract_Info b ON a.sTransNox = b.sReferNox" +
                        " LEFT JOIN MC_Serial c ON b.sSerialID = c.sSerialID" +
                        " LEFT JOIN MC_Model d ON c.sModelIDx = d.sModelIDx" +
                        " LEFT JOIN Brand e ON d.sBrandIDx = e.sBrandIDx" +
                        " LEFT JOIN Color f ON c.sColorIDx = f.sColorIDx" +
                    " WHERE b.sTransNox = " + SQLUtil.toSQL(args[0]);

            ResultSet loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) <= 0) {
                logwrapr.severe("No record found...");
                System.exit(1);
            }           
            
            if (loRS.next()){
                // Load your template with form fields
                String lsTemplate = "Deed of Voluntary Surrender";
                PdfReader reader = new PdfReader(path + "/reports/" + lsTemplate + ".pdf");
                
                if(System.getProperty("os.name").toLowerCase().contains("win")){
                    path = path.substring(0, 3);
                }
                else{
                    path = path.substring(0, 5);
                }
                
                // Output PDF path
                lsTemplate = lsTemplate + " - " + 
                            loRS.getString("sClientNm").toUpperCase() + " - " +
                            loRS.getString("sAcctNmbr");
                PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(path + "docusign/" + lsTemplate + ".pdf"));
                
                // Get the form fields
                AcroFields form = stamper.getAcroFields();
                
                form.setField("DATE", "");
                form.setField("MAKE", loRS.getString("sBrandNme").toUpperCase());
                form.setField("TYPE", loRS.getString("sModelNme").toUpperCase());
                form.setField("ENGINE NO", loRS.getString("sEngineNo").toUpperCase());
                form.setField("CHASSIS NO", loRS.getString("sFrameNox").toUpperCase());
                form.setField("COLOR", loRS.getString("sColorNme").toUpperCase());
                form.setField("COMPLETE NAME", loRS.getString("sClientNm").toUpperCase());
                
                // Set the form flattening to true to lock all fields
                stamper.setFormFlattening(true); // <-- this locks the fields

                // Save and close
                stamper.close();
                reader.close();
                
                System.out.println("PDF form filled and fields locked.");
            } else {
                logwrapr.severe("Account for not found.");
                System.exit(1);
            }
        } catch (SQLException | DocumentException | IOException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
        
        System.out.println("File exported successfully.");
        System.exit(0);
    }
}