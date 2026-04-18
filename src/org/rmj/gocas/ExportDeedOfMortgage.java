package org.rmj.gocas;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;
import org.rmj.appdriver.MiscUtil;
import org.rmj.appdriver.SQLUtil;
import org.rmj.appdriver.agent.GRiderX;
import org.rmj.replication.utility.LogWrapper;

public class ExportDeedOfMortgage {
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
                String lsTemplate = "Deed of Sale with Mortgage";
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
//                form.setField("month", monthName.toUpperCase());
//                form.setField("year", String.valueOf(date.getYear()).substring(2, 4));
                
//                form.setField("amount", formatDouble(loRS.getDouble("nGrossPrc")));
//                form.setField("amountWords", convert(loRS.getDouble("nGrossPrc")));
//                form.setField("amountPaid", formatDouble(loRS.getDouble("nDownPaym")));
//                form.setField("amountPaidWords", convert(loRS.getDouble("nDownPaym")));
//                form.setField("balanceAmount", formatDouble(loRS.getDouble("nPNValuex")));
//                form.setField("balanceWords", convert(loRS.getDouble("nPNValuex")));
                form.setField("term", String.valueOf(loRS.getInt("nAcctTerm")));
                form.setField("monthlyAmort", formatDouble(loRS.getDouble("nMonAmort")));
                form.setField("monthlyAmortWords", convert(loRS.getDouble("nMonAmort")));
                
//                date = SQLUtil.toDate(loRS.getString("dDueDatex"), SQLUtil.FORMAT_SHORT_DATE).toInstant()
//                                      .atZone(ZoneId.systemDefault())
//                                      .toLocalDate();
//                
//                monthName = date.getMonth()
//                               .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
//                
//                day = date.getDayOfMonth();
//                dayWithSuffix = day + getDaySuffix(day);
//                
//                form.setField("dueDay", dayWithSuffix.toUpperCase());
//                form.setField("dueMonth", monthName.toUpperCase());
//                form.setField("dueYear", String.valueOf(date.getYear()).substring(2, 4));

                GOCASApplication gocas = new GOCASApplication();
                gocas.setData(loRS.getString("sDetlInfo"));
                
                form.setField("buyerName", loRS.getString("sClientNm").toUpperCase());
                
                
                //customer address
                form.setField("houseNo1", gocas.ResidenceInfo().PresentAddress().getHouseNo());
                
                String lsValue = "";
                if (!gocas.ResidenceInfo().PresentAddress().getAddress1().isEmpty()){
                    lsValue = gocas.ResidenceInfo().PresentAddress().getAddress1();
                }
                
                if (!gocas.ResidenceInfo().PresentAddress().getAddress2().isEmpty()){
                    lsValue += gocas.ResidenceInfo().PresentAddress().getAddress2();
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
                form.setField("buyerAddress", lsValue);
                
                //spouse name
                lsValue = gocas.SpouseInfo().PersonalInfo().getLastName() + ", " + gocas.SpouseInfo().PersonalInfo().getFirstName();
                if (!gocas.SpouseInfo().PersonalInfo().getSuffixName().isEmpty()){
                    lsValue += " " + gocas.SpouseInfo().PersonalInfo().getSuffixName();
                }
                if (!gocas.SpouseInfo().PersonalInfo().getMiddleName().isEmpty()){
                    lsValue += " " + gocas.SpouseInfo().PersonalInfo().getMiddleName();
                }
                form.setField("buyerSpouse", lsValue.toUpperCase());
                
                
                form.setField("BUYER", loRS.getString("sClientNm").toUpperCase());
//                form.setField("COMAKER", loRS.getString("sCoMaker1").toUpperCase());
                
                form.setField("MAKE", loRS.getString("sBrandNme").toUpperCase());
                form.setField("MODEL", loRS.getString("sModelNme").toUpperCase());
                form.setField("ENGINENO", loRS.getString("sEngineNo").toUpperCase());
                form.setField("FRAMENO", loRS.getString("sFrameNox").toUpperCase());
                form.setField("COLOR", loRS.getString("sColorNme").toUpperCase());
//                form.setField("FILENO", loRS.getString("sFileNoxx").toUpperCase());
//                form.setField("REGCERT", loRS.getString("sRegORNox").toUpperCase());
                
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
    
    public static String formatDouble(double value) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(value);
    }
    
    private static final String[] units = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven",
            "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen",
            "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen",
            "Nineteen"
    };

    private static final String[] tens = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty",
            "Sixty", "Seventy", "Eighty", "Ninety"
    };

    public static String convert(double amount) {
        long pesos = (long) amount; // whole number part
        int centavos = (int) Math.round((amount - pesos) * 100); // decimal part

        String words = "";
        if (pesos == 0) {
            words = "Zero";
        } else {
            words = convertNumber(pesos);
        }

        words += " Pesos";

        if (centavos > 0) {
            words += " and " + convertNumber(centavos) + " Centavos";
        }

        return words.trim();
    }

    private static String convertNumber(long number) {
        if (number < 20) {
            return units[(int) number];
        } else if (number < 100) {
            return tens[(int) (number / 10)] + ((number % 10 != 0) ? " " + units[(int) (number % 10)] : "");
        } else if (number < 1000) {
            return units[(int) (number / 100)] + " Hundred" + ((number % 100 != 0) ? " " + convertNumber(number % 100) : "");
        } else if (number < 1_000_000) {
            return convertNumber(number / 1000) + " Thousand" + ((number % 1000 != 0) ? " " + convertNumber(number % 1000) : "");
        } else if (number < 1_000_000_000) {
            return convertNumber(number / 1_000_000) + " Million" + ((number % 1_000_000 != 0) ? " " + convertNumber(number % 1_000_000) : "");
        } else {
            return convertNumber(number / 1_000_000_000) + " Billion" + ((number % 1_000_000_000 != 0) ? " " + convertNumber(number % 1_000_000_000) : "");
        }
    }
}