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
                                ", a.sClientNm " +
                                ", IFNULL(a.sBranchCd, '') xBranch " +
                            " FROM Credit_Online_Application a" +
                                " LEFT JOIN MC_AR_Contract_Info b ON a.sTransNox = b.sReferNox" +
                                " LEFT JOIN MC_Serial c ON b.sSerialID = c.sSerialID" +
                                " LEFT JOIN MC_Model d ON c.sModelIDx = d.sModelIDx" +
                                " LEFT JOIN Brand e ON d.sBrandIDx = e.sBrandIDx" +
                                " LEFT JOIN Color f ON c.sColorIDx = f.sColorIDx" +
                            " WHERE b.sTransNox = " + SQLUtil.toSQL(args[0]);
            System.out.println("executeQuery : " + lsSQL);
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
                
                form.setField("day", dayWithSuffix.toUpperCase());
                form.setField("wDay", dayWithSuffix.toUpperCase());
              
                form.setField("monthYear", monthName.toUpperCase() + " " + date.getYear());
                form.setField("wMonth", monthName.toUpperCase());
                form.setField("wYear", String.valueOf(date.getYear()).substring(2));
                
                form.setField("date", monthName.toUpperCase() + " " + day + ", " + date.getYear());
                String managerNme = "";

                if (!loRS.getString("xBranch").isEmpty()) {
                    lsSQL = "SELECT b.sClientID, "
                            + "b.sCompnyNm AS Manager "
                            + "FROM Employee_Master001 a "
                            + "LEFT JOIN Client_Master b "
                            + "ON a.sEmployID = b.sClientID "
                            + "WHERE a.sBranchCD = "
                            + SQLUtil.toSQL(loRS.getString("xBranch"))
                            + " AND a.cManagerx = '1' "
                            + "AND a.sPositnID = '310'";

                    ResultSet loRSx = poGRider.executeQuery(lsSQL);

                    if (loRSx.next()) {
                        managerNme = loRSx.getString("Manager");
                    }

                    form.setField("branchHead", managerNme.toUpperCase());
                }
                
                
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
                
                
                //Added by TEEJEI
                
                form.setField("term", String.valueOf(gocas.PurchaseInfo().getAccountTerm()));
                form.setField("termText", numberToWords(gocas.PurchaseInfo().getAccountTerm()));
                
                String lsSelPrice = "";
                double lnSelPrice = 0.00;

                if (!gocas.PurchaseInfo().getModelID().isEmpty()) {
                    lsSQL = "SELECT nSelPrice "
                            + "FROM MC_Model_Price "
                            + "WHERE sModelIDx = "
                            + SQLUtil.toSQL(gocas.PurchaseInfo().getModelID());

                    ResultSet loRSx = poGRider.executeQuery(lsSQL);

                    if (loRSx.next()) {
                        lnSelPrice = loRSx.getDouble("nSelPrice");

                        lsSelPrice = String.format(
                                "%,.2f",
                                lnSelPrice
                        );
                    }
                }
                form.setField("amount", lsSelPrice);
                form.setField("amountText", numberToWords((int) lnSelPrice));
                String lsCoMaker = gocas.CoMakerInfo().getLastName() + ", "
                        + gocas.CoMakerInfo().getFirstName();

                if (!gocas.CoMakerInfo().getSuffixName().isEmpty()) {
                    lsCoMaker += " " + gocas.CoMakerInfo().getSuffixName();
                }

                if (!gocas.CoMakerInfo().getMiddleName().isEmpty()) {
                    lsCoMaker += " " + gocas.CoMakerInfo().getMiddleName();
                }

                form.setField("COMAKER", lsCoMaker.toUpperCase());
                
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
    
    
    /**
     * Converts a numeric value into its corresponding English word
     * representation.
     *
     * <p>
     * This method supports positive and negative whole numbers up to 999,999.
     * Values greater than 999,999 are returned as their numeric string
     * representation.
     * </p>
     *
     * @param number the numeric value to convert
     * @return the English word representation of the given number
     * @author TEEJEI DECELIS
     */
    public static String numberToWords(int number) {
    if (number == 0) {
        return "ZERO";
    }

    if (number < 0) {
        return "MINUS " + numberToWords(Math.abs(number));
    }

    String[] ones = {
        "", "ONE", "TWO", "THREE", "FOUR", "FIVE",
        "SIX", "SEVEN", "EIGHT", "NINE", "TEN",
        "ELEVEN", "TWELVE", "THIRTEEN", "FOURTEEN",
        "FIFTEEN", "SIXTEEN", "SEVENTEEN", "EIGHTEEN",
        "NINETEEN"
    };

    String[] tens = {
        "", "", "TWENTY", "THIRTY", "FORTY",
        "FIFTY", "SIXTY", "SEVENTY", "EIGHTY", "NINETY"
    };

    if (number < 20) {
        return ones[number];
    }

    if (number < 100) {
        return tens[number / 10]
                + (number % 10 != 0 ? "-" + ones[number % 10] : "");
    }

    if (number < 1000) {
        return ones[number / 100] + " HUNDRED"
                + (number % 100 != 0
                    ? " " + numberToWords(number % 100)
                    : "");
    }

    if (number < 1000000) {
        return numberToWords(number / 1000) + " THOUSAND"
                + (number % 1000 != 0
                    ? " " + numberToWords(number % 1000)
                    : "");
    }

    return String.valueOf(number);
}
}