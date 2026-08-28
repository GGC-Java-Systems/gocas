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
                            loRS.getString("sClientNm").toUpperCase() + " - " +
                            loRS.getString("sAcctNmbr");
                PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(path + "docusign/" + lsTemplate + ".pdf"));
                
                // Get the form fields
                AcroFields form = stamper.getAcroFields();
                
                LocalDate date = LocalDate.now();
                
                String monthName = date.getMonth()
                               .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                
                int day = date.getDayOfMonth();
                String dayWithSuffix = day + getDaySuffix(day);
                
//                form.setField("day", dayWithSuffix.toUpperCase());
//                form.setField("monthYear", monthName.toUpperCase() + " " + date.getYear());

                GOCASApplication gocas = new GOCASApplication();
                gocas.setData(loRS.getString("sDetlInfo"));
                
                //spouse name
                String lsValue = gocas.SpouseInfo().PersonalInfo().getLastName() + ", " + gocas.SpouseInfo().PersonalInfo().getFirstName();
                if (!gocas.SpouseInfo().PersonalInfo().getSuffixName().isEmpty()){
                    lsValue += " " + gocas.SpouseInfo().PersonalInfo().getSuffixName();
                }
                if (!gocas.SpouseInfo().PersonalInfo().getMiddleName().isEmpty()){
                    lsValue += " " + gocas.SpouseInfo().PersonalInfo().getMiddleName();
                }
                form.setField("buyerSpouse", lsValue.toUpperCase());
                
                form.setField("buyerName", loRS.getString("sClientNm").toUpperCase());
                form.setField("marriedTo", lsValue);
                
                //customer address                
                lsValue = gocas.ResidenceInfo().PresentAddress().getHouseNo() + " ";
                
                if (!gocas.ResidenceInfo().PresentAddress().getAddress1().isEmpty()){
                    lsValue += gocas.ResidenceInfo().PresentAddress().getAddress1() + " ";
                }
                
                if (!gocas.ResidenceInfo().PresentAddress().getAddress2().isEmpty()){
                    lsValue += gocas.ResidenceInfo().PresentAddress().getAddress2() + " ";
                }
                
                if (!gocas.ResidenceInfo().PresentAddress().getBarangay().isEmpty()){
                    lsSQL = "SELECT sBrgyName FROM Barangay WHERE sBrgyIDxx = " + SQLUtil.toSQL(gocas.ResidenceInfo().PresentAddress().getBarangay());
                    ResultSet loRSx = poGRider.executeQuery(lsSQL);
                    if (loRSx.next()){
                        lsValue += ", " + loRSx.getString("sBrgyName");
                    }
                }
                
                if (!gocas.ResidenceInfo().PresentAddress().getTownCity().isEmpty()){
                    lsSQL = "SELECT" +
                                "  a.sTownName" +
                                ", b.sProvName" +
                            " FROM TownCity a" +
                                ", Province b" +
                            " WHERE a.sProvIDxx = b.sProvIDxx" +
                                " AND a.sTownIDxx = " + SQLUtil.toSQL(gocas.ResidenceInfo().PresentAddress().getTownCity());
                    ResultSet loRSx = poGRider.executeQuery(lsSQL);
                    if (loRSx.next()){
                        lsValue += ", " + loRSx.getString("sTownName");
                        lsValue += ", " + loRSx.getString("sProvName");
                    }
                }
                
                form.setField("buyerAddress", lsValue.trim().toUpperCase());
                
                form.setField("MORTGAGOR", loRS.getString("sClientNm").toUpperCase());
                //form.setField("COMAKER", loRS.getString("sCoMaker1").toUpperCase());
                
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