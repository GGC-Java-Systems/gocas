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
                            ", b.sAcctNmbr" +
                        " FROM Credit_Online_Application a" +
                            ", MC_AR_Contract_Info b" +
                        " WHERE a.sTransNox = b. sReferNox" +
                            " AND b.sTransNox = " + SQLUtil.toSQL(args[0]);

            ResultSet loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) <= 0) {
                logwrapr.severe("No record found...");
                System.exit(1);
            }
 
            loRS.next();
            lsSQL = "SELECT" +
                        "  d.sBrandNme" +
                        ", c.sModelNme" +
                        ", b.sEngineNo" +
                        ", b.sFrameNox" +
                        ", e.sColorNme" +
                        ", f.sLastName" +
                        ", f.sFrstName" +
                        ", f.sMiddName" +
                        ", f.sSuffixNm" +
                        ", a.sAcctNmbr" +
                        ", f.sCompnyNm" +
                    " FROM MC_AR_Master a" +
                        " LEFT JOIN MC_Serial b ON a.sSerialID = b.sSerialID" +
                        " LEFT JOIN MC_Model c ON b.sModelIDx = c.sModelIDx" +
                        " LEFT JOIN Brand d ON c.sBrandIDx = d.sBrandIDx" +
                        " LEFT JOIN Color e ON b.sColorIDx = e.sColorIDx" +
                        ", Client_Master f" +
                    " WHERE a.sClientID = f.sClientID" +
                        " AND a.sAcctNmbr = " + SQLUtil.toSQL(loRS.getString("sAcctNmbr"));

            loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) <= 0){
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
                            loRS.getString("sCompnyNm").toUpperCase() + " - " +
                            loRS.getString("sAcctNmbr");
                PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(path + "docusign/" + lsTemplate + ".pdf"));
                
                // Get the form fields
                AcroFields form = stamper.getAcroFields();
                
                form.setField("DATE", SQLUtil.dateFormat(poGRider.getServerDate(), SQLUtil.FORMAT_LONG_DATE).toUpperCase());
                form.setField("MAKE", loRS.getString("sBrandNme").toUpperCase());
                form.setField("TYPE", loRS.getString("sModelNme").toUpperCase());
                form.setField("ENGINE NO", loRS.getString("sEngineNo").toUpperCase());
                form.setField("CHASSIS NO", loRS.getString("sFrameNox").toUpperCase());
                form.setField("COLOR", loRS.getString("sColorNme").toUpperCase());
                form.setField("COMPLETE NAME", loRS.getString("sCompnyNm").toUpperCase());
                
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