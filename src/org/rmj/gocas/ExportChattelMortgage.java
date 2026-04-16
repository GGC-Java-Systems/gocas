package org.rmj.gocas;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import org.rmj.appdriver.MiscUtil;
import org.rmj.appdriver.SQLUtil;
import org.rmj.appdriver.agent.GRiderX;
import org.rmj.replication.utility.LogWrapper;

public class ExportChattelMortgage {
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
                                "  b.sCompnyNm" +
                                ", IFNULL(c.sCompnyNm, '') sSpouseNm" +
                                ", TRIM(CONCAT(b.sHouseNox, ' ', b.sAddressx, ', ', d.sTownName, ' ', e.sProvName)) sAddressx" +
                                ", IFNULL(f.sCompnyNm, '') sCoMaker1" +
                                ", IFNULL(g.sCompnyNm, '') sCoMaker2" +
                                ", a.sAcctNmbr" +
                            " FROM MC_AR_Master a" +
                                    " LEFT JOIN Client_Master f ON a.sCoCltID1 = f.sClientID" +
                                    " LEFT JOIN Client_Master g ON a.sCoCltID2 = g.sClientID" +
                                ", Client_Master b" +
                                    " LEFT JOIN Client_Master c ON b.sSpouseID = c.sClientID" +
                                    " LEFT JOIN TownCity d ON b.sTownIDxx = d.sTownIDxx" +
                                    " LEFT JOIN Province e ON d.sProvIDxx = e.sProvIDxx" +
                            " WHERE a.sClientID = b.sClientID" +
                                " AND a.sAcctNmbr = " + SQLUtil.toSQL(loRS.getString("sAcctNmbr"));

            loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) <= 0){
                logwrapr.severe("No record found...");
                System.exit(1);
            }
            
            if (loRS.next()){
                // Load your template with form fields
                String lsTemplate = "Chattel Mortgage";
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
                
                LocalDate date = LocalDate.now();
                
                String monthName = date.getMonth()
                               .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                
                int day = date.getDayOfMonth();
                String dayWithSuffix = day + getDaySuffix(day);
                
                form.setField("day", dayWithSuffix.toUpperCase());
                form.setField("monthYear", monthName.toUpperCase() + " " + date.getYear());
                
                form.setField("buyerName", loRS.getString("sCompnyNm").toUpperCase());
                form.setField("marriedTo", loRS.getString("sSpouseNm").toUpperCase());
                form.setField("buyerAddress", loRS.getString("sAddressx").toUpperCase());
                form.setField("MORTGAGOR", loRS.getString("sCompnyNm").toUpperCase());
                form.setField("COMAKER", loRS.getString("sCoMaker1").toUpperCase());
                
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
    
    private static String getDaySuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:  return "st";
            case 2:  return "nd";
            case 3:  return "rd";
            default: return "th";
        }
    }
}