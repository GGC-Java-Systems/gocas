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
import static org.rmj.gocas.ExportChattelMortgage.numberToWords;
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
                form.setField("ENGINENO", loRS.getString("sEngineNo").toUpperCase());
                form.setField("CHASSISNO", loRS.getString("sFrameNox").toUpperCase());
                form.setField("COLOR", loRS.getString("sColorNme").toUpperCase());
                form.setField("COMPLETENAME", loRS.getString("sClientNm").toUpperCase());
                
                GOCASApplication gocas = new GOCASApplication();
                gocas.setData(loRS.getString("sDetlInfo"));
                
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