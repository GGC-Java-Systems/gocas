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
import org.rmj.appdriver.MiscUtil;
import org.rmj.appdriver.SQLUtil;
import org.rmj.appdriver.agent.GRiderX;
import org.rmj.replication.utility.LogWrapper;

public class ExportPromissoryNoteI {

    public static void main(String[] args) {

        LogWrapper logwrapr = new LogWrapper("DCP.ExportFile","dcp.log");

        if (args.length != 1) {
            logwrapr.severe("Invalid parameter detected.");
            System.exit(1);
        }

        String path;

        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            path = "D:/GGC_Java_Systems";
        } else {
            path = "/srv/GGC_Java_Systems";
        }

        System.setProperty("sys.default.path.config", path);

        GRiderX poGRider = new GRiderX("IntegSys");

        if (!poGRider.logUser("IntegSys", "M001111122")) {
            logwrapr.severe(poGRider.getErrMsg());
            logwrapr.severe("GRiderX has error...");
            System.exit(1);
        }

        if (!poGRider.getErrMsg().isEmpty()) {
            logwrapr.severe(poGRider.getErrMsg());
            logwrapr.severe("GRiderX has error...");
            System.exit(1);
        }

        try {
            String lsSQL = "SELECT"
                    + "  a.sClientNm"
                    + ", IFNULL(a.sCatInfox, a.sDetlInfo) sDetlInfo"
                    + ", a.sTransNox"
                    + ", c.sEngineNo"
                    + ", c.sFrameNox"
                    + ", b.sAcctNmbr"
                    + ", b.nDownPaym"
                    + ", b.nAcctTerm"
                    + ", b.nMonAmort"
                    + ", b.nRebatesx"
                    + ", b.nPNValuex"
                    + ", b.nPenaltyx"
                    + ", IFNULL(b.dFirstPay, '') dFirstPay"
                    + ", DATE_ADD(b.dFirstPay,INTERVAL(b.nAcctTerm-1)MONTH ) AS dEndDate"
                    + ", e.sBrandNme"
                    + ", CONCAT(d.sModelNme, ' - ', d.sModelCde) sModelNme"
                    + ", f.sColorNme"
                    + ", IFNULL(b.sTransNox, '') xTransNox"
                    + ", a.sClientNm "
                    + ", IFNULL(a.sBranchCd, '') xBranch "
                    + " FROM Credit_Online_Application a"
                    + " LEFT JOIN MC_AR_Contract_Info b ON a.sTransNox = b.sReferNox"
                    + " LEFT JOIN MC_Serial c ON b.sSerialID = c.sSerialID"
                    + " LEFT JOIN MC_Model d ON c.sModelIDx = d.sModelIDx"
                    + " LEFT JOIN Brand e ON d.sBrandIDx = e.sBrandIDx"
                    + " LEFT JOIN Color f ON c.sColorIDx = f.sColorIDx"
                    + " WHERE b.sTransNox = " + SQLUtil.toSQL(args[0]);

            System.out.println("executeQuery : " + lsSQL);

            ResultSet loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) <= 0) {
                logwrapr.severe("No record found...");
                System.exit(1);
            }

            if (loRS.next()) {

                /*
                 * =====================================================
                 * LOAD PDF TEMPLATE
                 * =====================================================
                 */
                String lsTemplate = "Promissory Note I";

                PdfReader reader = new PdfReader(
                        path + "/reports/" + lsTemplate + ".pdf"
                );

                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    path = path.substring(0, 3);
                } else {
                    path = path.substring(0, 5);
                }

                String lsOutputFile = lsTemplate
                        + " - "
                        + loRS.getString("sClientNm").toUpperCase()
                        + " - "
                        + loRS.getString("sAcctNmbr");

                PdfStamper stamper = new PdfStamper(reader,
                        new FileOutputStream(path + "docusign/" + lsOutputFile + ".pdf")
                );

                AcroFields form = stamper.getAcroFields();

                /*
                 * =====================================================
                 * SUPPLIER
                 * =====================================================
                 */
                //supplier cannot be populated automatically because 
                //there is no table relationship based on the brand or model.
                
//                form.setField(
//                        "supplier",
//                        "GUANZON MERCHANTILE CORPORATION"
//                );

                /*
                 * =====================================================
                 * PAYMENT DATE
                 * =====================================================
                 */
                String lsFirstPay = loRS.getString("dFirstPay");

                if (lsFirstPay != null
                        && !lsFirstPay.trim().isEmpty()) {

                    LocalDate loFirstPay = LocalDate.parse(lsFirstPay);

                    int lnDay = loFirstPay.getDayOfMonth();

                    form.setField("day",lnDay + getDaySuffix(lnDay));

                    form.setField("startDate",lsFirstPay);

                    String lsEndDate = loRS.getString("dEndDate");

                    if (lsEndDate != null && !lsEndDate.trim().isEmpty()) {

                        form.setField("endDate",lsEndDate);
                    } else {
                        form.setField("endDate", "");
                    }

                } else {

                    form.setField("day", "");
                    form.setField("startDate", "");
                    form.setField("endDate", "");
                }

                /*
                 * =====================================================
                 * CUSTOMER / COMAKER
                 * =====================================================
                 */
                GOCASApplication gocas = new GOCASApplication();

                gocas.setData(loRS.getString("sDetlInfo"));

                String lsCustomer = loRS.getString("sClientNm");

                if (lsCustomer == null) {
                    lsCustomer = "";
                }

                form.setField("Customer",lsCustomer.toUpperCase());
                //customer address  
                String lsValue = "";
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
                
                form.setField("CustomerAddress", lsValue.trim().toUpperCase());

                if (!gocas.ApplicantInfo().getMobileNo(0).isEmpty()) {
                    form.setField("CustomerContactNo", gocas.ApplicantInfo().getMobileNo(0));
                }
                
                //add value for GovIssuedID (where to get?)
                // TEEJEI
                form.setField("GovIssuedID", "");


                /*
                 * Comaker name
                 */
                String lsCoMaker = gocas.CoMakerInfo().getLastName() + ", "
                        + gocas.CoMakerInfo().getFirstName();

                if (!gocas.CoMakerInfo().getSuffixName().isEmpty()) {

                    lsCoMaker += " "+ gocas.CoMakerInfo().getSuffixName();
                }

                if (!gocas.CoMakerInfo().getMiddleName().isEmpty()) {

                    lsCoMaker += " " + gocas.CoMakerInfo().getMiddleName();
                }

                form.setField("COMAKER",lsCoMaker.toUpperCase());
                
                //Disabled this block of codes by TEEJEI
                // need to identify if what amount is refering to 
                // COMMITMENT TO SETTLE OVERDUE ACCOUNT section
//                /*
//                 * =====================================================
//                 * AMOUNT
//                 * =====================================================
//                 *
//                 * Retained from your original code.
//                 */
//                String lsSelPrice = "";
//                double lnSelPrice = 0.00;
//
//                if (!gocas.PurchaseInfo().getModelID().isEmpty()) {
//
//                    lsSQL = "SELECT nSelPrice "
//                            + "FROM MC_Model_Price "
//                            + "WHERE sModelIDx = "
//                            + SQLUtil.toSQL(gocas.PurchaseInfo().getModelID());
//
//                    ResultSet loRSx = poGRider.executeQuery(lsSQL);
//
//                    if (loRSx.next()) {
//
//                        lnSelPrice = loRSx.getDouble("nSelPrice");
//
//                        lsSelPrice = String.format("%,.2f",lnSelPrice);
//                    }
//                }
//
//                form.setField("Amount",lsSelPrice);

                
                form.setField("CustomerName",lsCustomer.toUpperCase());

                /*
                 * =====================================================
                 * WITNESSES
                 * =====================================================
                 */
                form.setField("witness1", "");
                form.setField("witness2", "");


                /*
                 * =====================================================
                 * FLATTEN PDF
                 * =====================================================
                 */
                stamper.setFormFlattening(true);

                stamper.close();
                reader.close();

                System.out.println("PDF form filled and fields locked.");

            } else {

                logwrapr.severe(
                        "Account for not found."
                );

                System.exit(1);
            }

        } catch (SQLException
                | DocumentException
                | IOException e) {

            logwrapr.severe(e.getMessage());
            System.exit(1);
        }

        System.out.println(
                "File exported successfully."
        );

        System.exit(0);
    }

    /**
     * Returns the ordinal suffix for a given day of the month.
     *
     * @param day day of the month
     * @return {@code st}, {@code nd}, {@code rd}, or {@code th}
     */
    private static String getDaySuffix(int day) {

        if (day >= 11 && day <= 13) {
            return "th";
        }

        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }
}
