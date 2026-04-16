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
import org.rmj.appdriver.StringHelperMisc;
import org.rmj.appdriver.agent.GRiderX;
import org.rmj.replication.utility.LogWrapper;

public class ExportGOCASForm {
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
                
        String[] BUSINESS_NATURE = {
                                    "BPO/Call Center",
                                    "Business Services",
                                    "Communication/Transportation",
                                    "Construction",
                                    "Financing Banking",
                                    "Health and Wellness",
                                    "Hotel/Resort",
                                    "Insurance",
                                    "Manufacturing",
                                    "Mining/Agriculture",
                                    "Pharmaceutical",
                                    "Real Estate",
                                    "Restaurants/Bars/Caffe",
                                    "Social Services",
                                    "utilities",
                                    "WholeSale/Retail",
                                    "Others"};
        
        try {
            String lsSQL = args[0];
            ResultSet loRS;

            if (lsSQL.length() == 10){
                lsSQL = "SELECT" +
                            " b.sReferNox sTransNox" +
                        " FROM MC_AR_Master a" +
                            ", MC_Credit_Application b" +
                        " WHERE a.sApplicNo = b.sTransNox" +
                            " AND a.sAcctNmbr = " + SQLUtil.toSQL(lsSQL);

                loRS = poGRider.executeQuery(lsSQL);

                if (!loRS.next()){
                    logwrapr.severe("No record found...");
                    System.exit(1);
                }
                
                lsSQL = loRS.getString("sTransNox");
            }

            lsSQL = "SELECT" +
                        "  sClientNm" +
                        ", IFNULL(sCatInfox, sDetlInfo) sDetlInfo" +
                        ", sTransNox" +
                    " FROM Credit_Online_Application" +
                    " WHERE sTransNox = " + SQLUtil.toSQL(lsSQL);

            loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) <= 0){
                logwrapr.severe("No record found...");
                System.exit(1);
            }
            
            if (loRS.next()){
                GOCASApplication gocas = new GOCASApplication();
                gocas.setData(loRS.getString("sDetlInfo"));
                
                // Load your template with form fields
                String lsTemplate = "Guanzon Credit Application Form";
                PdfReader reader = new PdfReader(path + "/reports/" + lsTemplate + ".pdf");
                
                if(System.getProperty("os.name").toLowerCase().contains("win")){
                    path = path.substring(0, 3);
                }
                else{
                    path = path.substring(0, 5);
                }
                
                // Output PDF path
                lsTemplate = lsTemplate + " - " + 
                            gocas.ApplicantInfo().getLastName() + ", " + gocas.ApplicantInfo().getFirstName() + " - " +
                            loRS.getString("sTransNox");
                PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(path + "docusign/" + lsTemplate + ".pdf"));
                
                // Get the form fields
                AcroFields form = stamper.getAcroFields();

                form.setField("applicationNo", "Reference No: " + loRS.getString("sTransNox"));
               
                String lsValue = "";
                
                //application for
                switch (gocas.PurchaseInfo().getAppliedFor()) {
                    case "0":
                        lsValue = "Motorcycle";
                        break;
                    case "1":
                        lsValue = "Sidecar";
                        break;
                    case "2":
                        lsValue = "Others";
                        break;
                    case "3":
                        lsValue = "Mobile Phone";
                        break;
                    case "4":
                        lsValue = "Cars";
                        break;
                    case "5":
                        lsValue = "Services";
                        break;
                    default:
                        lsValue = "";
                }
                form.setField("unitApplied", lsValue);         
                
                ResultSet loRSx;

                //brand/model
                lsSQL = "SELECT sBrandNme FROM Brand WHERE sBrandIDx = " + SQLUtil.toSQL(gocas.PurchaseInfo().getBrandName());
                loRSx = poGRider.executeQuery(lsSQL);
                if (loRSx.next()){
                    lsValue = loRSx.getString("sBrandNme");
                }
                
                lsSQL = "SELECT sModelNme FROM MC_Model WHERE sModelIDx = " + SQLUtil.toSQL(gocas.PurchaseInfo().getModelID());
                loRSx = poGRider.executeQuery(lsSQL);
                if (loRSx.next()){
                    if (lsValue.isEmpty()){
                        lsValue = loRSx.getString("sModelNme");
                    } else {
                        lsValue += " - " +loRSx.getString("sModelNme");
                    }
                }
                form.setField("brandModelName", lsValue);         
                
                //client type
                form.setField("clientType", "Off");   
                switch (gocas.PurchaseInfo().getAppliedFor()) {
                    case "0":
                        form.setField("clientType", "New Customer");   
                        break;
                    case "1":
                        form.setField("clientType", "Repeat Customer");   
                        break;
                }
                
                //term
                form.setField("monthTerm", String.valueOf(gocas.PurchaseInfo().getAccountTerm()));   
                
                //application date
                lsValue = gocas.PurchaseInfo().getDateApplied(); //2025-07-31
                form.setField("applicationDate0", lsValue.substring(5, 7));
                form.setField("applicationDate1", lsValue.substring(8, 10));   
                form.setField("applicationDate2", lsValue.substring(0, 4));
                
                //who will use the unit
                form.setField("checkPrimaryCustomer", "No");
                switch (gocas.OtherInfo().getUnitUser()) {
                    case "0":
                        form.setField("checkPrimaryCustomer", "Yes");
                        break;
                    default:
                        form.setField("checkParents", "No");
                        form.setField("checkRelatives", "No");
                        form.setField("checkFriends", "No");
                        switch (gocas.OtherInfo().getOtherUser()) {
                            case "0":
                                form.setField("checkParents", "Yes");
                                break;
                            case "1":
                            case "2":
                                form.setField("checkRelatives", "Yes");
                                break;
                            default:
                                form.setField("checkFriends", "Yes");
                        }
                }
                
                //purpose in buying the unit
                form.setField("checkBusinessPurpose", "No");
                form.setField("checkPersonal", "No");
                switch (gocas.OtherInfo().getPurpose()) {
                    case "0":
                        form.setField("checkBusinessPurpose", "Yes");
                        break;
                    case "1":
                        form.setField("checkPersonal", "Yes");
                        break;
                }
                
                //who will shoulder the payment
                form.setField("willPayMonthly", "Off");
                switch (gocas.OtherInfo().getUnitPayor()) {
                    case "0":
                        form.setField("willPayMonthly", "Principal Customer");
                        break;
                    default:
                        form.setField("willPayMonthly", "Off");
                        switch (gocas.OtherInfo().getPayorRelation()) {
                            case "0":
                                form.setField("willPayMonthly", "Parents");
                                break;
                            case "1":
                                form.setField("willPayMonthly", "Spouse");
                                break;
                            case "2":
                                form.setField("willPayMonthly", "Relatives");
                                break;
                            case "4":
                                form.setField("willPayMonthly", "Both customer and spouse");
                                break;
                            default:
                                form.setField("willPayMonthly", "Friends");
                        }
                }
                
                //how did you find out about guanzon TODO
                form.setField("checkFoundOutOthers", "Yes");
                
                //personal info
                form.setField("clientLastName", gocas.ApplicantInfo().getLastName());
                form.setField("clientFirstName", gocas.ApplicantInfo().getFirstName());
                form.setField("clientMiddleName", gocas.ApplicantInfo().getMiddleName());
                form.setField("clientSuffixName", gocas.ApplicantInfo().getSuffixName());
                
                lsValue = gocas.ApplicantInfo().getBirthPlace();
                lsSQL = "SELECT" +
                            "  CONCAT(a.sTownName, ', ', b.sProvName) sBirthPlc" +
                        " FROM TownCity a" +
                            ", Province b" +
                        " WHERE a.sProvIDxx = b.sProvIDxx" +
                            " AND a.sTownIDxx = " + SQLUtil.toSQL(lsValue);
                loRSx = poGRider.executeQuery(lsSQL);
                if (loRSx.next()){
                    form.setField("clientBirthPlace", loRSx.getString("sBirthPlc"));
                }
                
                lsValue = gocas.ApplicantInfo().getBirthdate(); //2025-07-31
                form.setField("birthDate0", lsValue.substring(5, 7));
                form.setField("birthDate1", lsValue.substring(8, 10));   
                form.setField("birthDate2", lsValue.substring(0, 4));
                
                if (gocas.ApplicantInfo().getMobileNoQty() > 0){
                    form.setField("clientMobile", gocas.ApplicantInfo().getMobileNo(0)); 
                }
                
                form.setField("clientEmail", gocas.ApplicantInfo().getEmailAddress(0)); 
                form.setField("clientViber", gocas.ApplicantInfo().getViberAccount()); 
                form.setField("clientFacebook", gocas.ApplicantInfo().getFBAccount()); 
                
                form.setField("civilStatus", "Off");
                switch (gocas.ApplicantInfo().getCivilStatus()) {
                    case "0":
                        form.setField("civilStatus", "single");
                        break;
                    case "1":
                        form.setField("civilStatus", "married");
                        break;
                    case "2":
                        form.setField("civilStatus", "separated");
                        break;
                    case "3":
                        form.setField("civilStatus", "widowed");
                        break;
                    case "4":
                        form.setField("civilStatus", "singleParent");
                        break;
                    case "5":
                        form.setField("civilStatus", "singleLiveIn");
                        break;
                }
                
                form.setField("gender", "Off");
                switch (gocas.ApplicantInfo().getGender()) {
                    case "0":
                        form.setField("gender", "male");
                        break;
                    case "1":
                        form.setField("gender", "female");
                        break;
                    case "2":
                        form.setField("gender", "lgbtq");
                        break;
                }
                
                form.setField("citizenship", "Off");
                switch (gocas.ApplicantInfo().getCitizenship()) {
                    case "":
                        break;
                    case "01":
                        form.setField("citizenship", "filipino");
                        break;
                    default:
                        lsSQL = "SELECT sCntryNme FROM Country WHERE sCntryCde = " + SQLUtil.toSQL(gocas.ApplicantInfo().getCitizenship());
                        loRSx = poGRider.executeQuery(lsSQL);
                        if (loRSx.next()){
                            form.setField("citizenship", "others");
                            form.setField("citizenOthers", loRSx.getString("sCntryNme"));
                        }
                }

                //spouse name
                lsValue = gocas.SpouseInfo().PersonalInfo().getLastName() + ", " + gocas.SpouseInfo().PersonalInfo().getFirstName();
                if (!gocas.SpouseInfo().PersonalInfo().getSuffixName().isEmpty()){
                    lsValue += " " + gocas.SpouseInfo().PersonalInfo().getSuffixName();
                }
                if (!gocas.SpouseInfo().PersonalInfo().getMiddleName().isEmpty()){
                    lsValue += " " + gocas.SpouseInfo().PersonalInfo().getMiddleName();
                }
                form.setField("clientSpouse", lsValue);
                
                //spouse employment
                switch (gocas.SpouseMeansInfo().getIncomeSource()){
                    case "0":
                        form.setField("spouseEmployment", "Employed");
                        break;
                    case "1":
                        form.setField("spouseEmployment", "Self-employed");
                        break;
                    case "2":
                        form.setField("spouseEmployment", "With financer");
                        break;
                    case "3":
                        form.setField("spouseEmployment", "Pensioner");
                        break;
                }
                
                if (gocas.SpouseInfo().PersonalInfo().getMobileCount() > 0){
                    form.setField("spouseMobile", gocas.SpouseInfo().PersonalInfo().getMobileNo(0)); 
                }
                
                //customer address
                form.setField("houseNo1", gocas.ResidenceInfo().PresentAddress().getHouseNo());
                
                lsValue = "";
                if (!gocas.ResidenceInfo().PresentAddress().getAddress1().isEmpty()){
                    lsValue = gocas.ResidenceInfo().PresentAddress().getAddress1();
                }
                
                if (!gocas.ResidenceInfo().PresentAddress().getAddress2().isEmpty()){
                    lsValue += gocas.ResidenceInfo().PresentAddress().getAddress2();
                }
                
                if (!gocas.ResidenceInfo().PresentAddress().getBarangay().isEmpty()){
                    lsSQL = "SELECT sBrgyName FROM Barangay WHERE sBrgyIDxx = " + SQLUtil.toSQL(gocas.ResidenceInfo().PresentAddress().getBarangay());
                    loRSx = poGRider.executeQuery(lsSQL);
                    if (loRSx.next()){
                        lsValue += ", " + loRSx.getString("sBrgyName");
                    }
                }
                
                if (!gocas.ResidenceInfo().PresentAddress().getLandMark().trim().replace("-", "").isEmpty()){
                    lsValue += " (landmark:" + gocas.ResidenceInfo().PresentAddress().getLandMark() +")";
                }
                form.setField("barangay1", lsValue);
                
                if (!gocas.SpouseInfo().ResidenceInfo().PresentAddress().getTownCity().isEmpty()){
                    lsSQL = "SELECT" +
                                "  a.sTownName" +
                                ", b.sProvName" +
                            " FROM TownCity a" +
                                ", Province b" +
                            " WHERE a.sProvIDxx = b.sProvIDxx" +
                                " AND a.sTownIDxx = " + SQLUtil.toSQL(gocas.ResidenceInfo().PresentAddress().getTownCity());
                    loRSx = poGRider.executeQuery(lsSQL);
                    if (loRSx.next()){
                        form.setField("town1", loRSx.getString("sTownName"));
                        form.setField("province1", loRSx.getString("sProvName"));
                    }
                }
                
                if (gocas.ResidenceInfo().PresentAddress().getHouseNo().equals(gocas.ResidenceInfo().PermanentAddress().getHouseNo()) &&
                    gocas.ResidenceInfo().PresentAddress().getAddress1().equals(gocas.ResidenceInfo().PermanentAddress().getAddress1()) &&
                    gocas.ResidenceInfo().PresentAddress().getAddress2().equals(gocas.ResidenceInfo().PermanentAddress().getAddress2()) &&
                    gocas.ResidenceInfo().PresentAddress().getTownCity().equals(gocas.ResidenceInfo().PermanentAddress().getTownCity())){
                    form.setField("checkSameAsPresentAddress", "Yes");
                } else {
                    form.setField("houseNo2", gocas.ResidenceInfo().PermanentAddress().getHouseNo());
                
                    lsValue = "";
                    if (!gocas.ResidenceInfo().PermanentAddress().getAddress1().isEmpty()){
                        lsValue = gocas.ResidenceInfo().PermanentAddress().getAddress1();
                    }

                    if (!gocas.ResidenceInfo().PermanentAddress().getAddress2().isEmpty()){
                        lsValue += gocas.ResidenceInfo().PermanentAddress().getAddress2();
                    }

                    if (!gocas.ResidenceInfo().PermanentAddress().getBarangay().isEmpty()){
                        lsSQL = "SELECT sBrgyName FROM Barangay WHERE sBrgyIDxx = " + SQLUtil.toSQL(gocas.ResidenceInfo().PermanentAddress().getBarangay());
                        loRSx = poGRider.executeQuery(lsSQL);
                        if (loRSx.next()){
                            lsValue += ", " + loRSx.getString("sBrgyName");
                        }
                    }

                    if (!gocas.ResidenceInfo().PermanentAddress().getLandMark().trim().replace("-", "").isEmpty()){
                        lsValue += " (landmark:" + gocas.ResidenceInfo().PermanentAddress().getLandMark() +")";
                    }
                    form.setField("barangay2", lsValue);

                    if (!gocas.SpouseInfo().ResidenceInfo().PermanentAddress().getTownCity().isEmpty()){
                        lsSQL = "SELECT" +
                                    "  a.sTownName" +
                                    ", b.sProvName" +
                                " FROM TownCity a" +
                                    ", Province b" +
                                " WHERE a.sProvIDxx = b.sProvIDxx" +
                                    " AND a.sTownIDxx = " + SQLUtil.toSQL(gocas.ResidenceInfo().PermanentAddress().getTownCity());
                        loRSx = poGRider.executeQuery(lsSQL);
                        if (loRSx.next()){
                            form.setField("town2", loRSx.getString("sTownName"));
                            form.setField("province2", loRSx.getString("sProvName"));
                        }
                    }
                }
                
                //properties
                form.setField("check4Wheels", gocas.DisbursementInfo().PropertiesInfo().Has4Wheels().equals("1") ? "Yes" : "No");
                form.setField("check3Wheels", gocas.DisbursementInfo().PropertiesInfo().Has3Wheels().equals("1") ? "Yes" : "No");
                form.setField("check2Wheels", gocas.DisbursementInfo().PropertiesInfo().Has2Wheels().equals("1") ? "Yes" : "No");
                
                if (gocas.DisbursementInfo().PropertiesInfo().Has2Wheels().equals("0") &&
                    gocas.DisbursementInfo().PropertiesInfo().Has3Wheels().equals("0") &&
                    gocas.DisbursementInfo().PropertiesInfo().Has4Wheels().equals("0")){
                    form.setField("checkNoVehicle", "Yes");
                }
                
                form.setField("houseOwnership", "Off");
                switch (gocas.ResidenceInfo().getOwnership()) {
                    case "0":
                        form.setField("houseOwnership", "owned");
                        
                        form.setField("houseOwned", "");
                        switch(gocas.ResidenceInfo().getOwnedResidenceInfo()){
                            case "0":
                                form.setField("houseOwned", "withFamily1");
                                break;
                            case "1":
                                form.setField("houseOwned", "withFamily2");
                                break;
                            case "2":
                                form.setField("houseOwned", "withRelatives");
                                break;
                        }
                        
                        form.setField("houseStructure", "Off");
                        switch(gocas.ResidenceInfo().getHouseType()){
                            case "0":
                                form.setField("houseStructure", "concrete");
                                break;
                            case "1":
                                form.setField("houseStructure", "combination");
                                break;
                            case "2":
                                form.setField("houseStructure", "wood");
                                break;
                        }
                        break;
                    case "1":
                        form.setField("houseOwnership", "rented");
                        
                        form.setField("houseRented", "Off");
                        switch(gocas.ResidenceInfo().getRentedResidenceInfo()){
                            case "0":
                                form.setField("houseRented", "withFamily1");
                                break;
                            case "1":
                                form.setField("houseRented", "withFamily2");
                                break;
                            case "2":
                                form.setField("houseRented", "withRelatives");
                                break;
                        }
                        
                        form.setField("rentLengthOfStay", String.valueOf(gocas.ResidenceInfo().getRentNoYears()));
                        form.setField("rentMonthlyExpense", String.valueOf(gocas.ResidenceInfo().getRentExpenses()));
                        break;
                    case "2":
                        form.setField("houseOwnership", "caretaker");
                        form.setField("relationshipToOwner", gocas.ResidenceInfo().getCareTakerRelation());
                        break;
                }
                
                //expenses
                form.setField("electricityExpense", gocas.DisbursementInfo().Expenses().getElectricBill());
                form.setField("waterExpense", gocas.DisbursementInfo().Expenses().getWaterBill());
                form.setField("groceryExpense", gocas.DisbursementInfo().Expenses().getFoodAllowance());
                form.setField("loanExpense", gocas.DisbursementInfo().Expenses().getLoanAmount());
                
                //dependent
                int lnValue = gocas.DisbursementInfo().DependentInfo().getNoOfChildren();
                
                if (lnValue > 0){
                    form.setField("noOfDependents", String.valueOf(lnValue));
                    form.setField("dependents", "child");
                }
                
                lnValue = gocas.OtherInfo().getPersonalReferenceCount();
                
                if (lnValue > 0){
                    for (int lnCtr = 0; lnCtr <= lnValue-1; lnCtr++){
                        if (lnCtr == 0) {
                            form.setField("referenceName1", gocas.OtherInfo().getPRName(lnCtr));
                            form.setField("referenceContact1", gocas.OtherInfo().getPRMobileNo(lnCtr));
                            
                            lsValue = gocas.OtherInfo().getPRAddress(lnCtr);
                            if (!gocas.OtherInfo().getPRTownCity(lnCtr).isEmpty()){
                                lsSQL = "SELECT" +
                                            "  CONCAT(a.sTownName, ', ', b.sProvName) sTownName" +
                                        " FROM TownCity a" +
                                            ", Province b" +
                                        " WHERE a.sProvIDxx = b.sProvIDxx" +
                                            " AND a.sTownIDxx = " + SQLUtil.toSQL(gocas.OtherInfo().getPRTownCity(lnCtr));
                                loRSx = poGRider.executeQuery(lsSQL);
                                if (loRSx.next()){
                                    lsValue += ", " + loRSx.getString("sTownName");
                                }
                            }
                            form.setField("referenceAddress1", lsValue);
                        } else if (lnCtr == 1){
                            form.setField("referenceName2", gocas.OtherInfo().getPRName(lnCtr));
                            form.setField("referenceContact2", gocas.OtherInfo().getPRMobileNo(lnCtr));
                            
                            lsValue = gocas.OtherInfo().getPRAddress(lnCtr);
                            if (!gocas.OtherInfo().getPRTownCity(lnCtr).isEmpty()){
                                lsSQL = "SELECT" +
                                            "  CONCAT(a.sTownName, ', ', b.sProvName) sTownName" +
                                        " FROM TownCity a" +
                                            ", Province b" +
                                        " WHERE a.sProvIDxx = b.sProvIDxx" +
                                            " AND a.sTownIDxx = " + SQLUtil.toSQL(gocas.OtherInfo().getPRTownCity(lnCtr));
                                loRSx = poGRider.executeQuery(lsSQL);
                                if (loRSx.next()){
                                    lsValue += ", " + loRSx.getString("sTownName");
                                }
                            }
                            form.setField("referenceAddress2", lsValue);
                        }
                    }
                }
                
                //income information
                form.setField("incomeSource", "Off");
                switch (gocas.MeansInfo().getIncomeSource()) {
                    case "0": //employed
                        form.setField("incomeSource", "employed");
                        
                        form.setField("employed", "Off");
                        switch (gocas.MeansInfo().EmployedInfo().getEmploymentSector()){
                            case "0":
                                form.setField("employed", "government");
                                break;
                            case "1":
                                form.setField("employed", "private");
                                break;
                        }
                        
                        if (StringHelperMisc.isNumeric(gocas.MeansInfo().EmployedInfo().getNatureofBusiness())){
                            form.setField("natureOfCompany", BUSINESS_NATURE[Integer.parseInt(gocas.MeansInfo().EmployedInfo().getNatureofBusiness())]);
                        } else {
                            form.setField("natureOfCompany", gocas.MeansInfo().EmployedInfo().getNatureofBusiness());
                        }
                        
                        form.setField("companyName", gocas.MeansInfo().EmployedInfo().getCompanyName());
                        
                        lsValue = gocas.MeansInfo().EmployedInfo().getCompanyAddress();
                        if (!gocas.MeansInfo().EmployedInfo().getCompanyTown().isEmpty()){
                            lsSQL = "SELECT" +
                                        "  CONCAT(a.sTownName, ', ', b.sProvName) sTownName" +
                                    " FROM TownCity a" +
                                        ", Province b" +
                                    " WHERE a.sProvIDxx = b.sProvIDxx" +
                                        " AND a.sTownIDxx = " + SQLUtil.toSQL(gocas.MeansInfo().EmployedInfo().getCompanyTown());
                            loRSx = poGRider.executeQuery(lsSQL);
                            if (loRSx.next()){
                                lsValue += ", " + loRSx.getString("sTownName");
                            }
                        }
                        
                        form.setField("companyAddress", lsValue);
                        
                        if (gocas.MeansInfo().EmployedInfo().getPosition().length() == 7 &&
                            gocas.MeansInfo().EmployedInfo().getPosition().substring(0, 1).equalsIgnoreCase("M")){
                            lsSQL = "SELECT" +
                                        "  sOccptnNm" +
                                    " FROM Occupation" +
                                    " WHERE sOccptnID = " + SQLUtil.toSQL(gocas.MeansInfo().EmployedInfo().getPosition());
                            loRSx = poGRider.executeQuery(lsSQL);
                            if (loRSx.next()){
                                form.setField("position", loRSx.getString("sOccptnNm"));
                            }
                        } else {
                            form.setField("position", gocas.MeansInfo().EmployedInfo().getPosition());
                        }
                        
                        form.setField("employmentStatus", gocas.MeansInfo().EmployedInfo().getEmployeeStatus());
                        form.setField("employmentTenure", String.valueOf(gocas.MeansInfo().EmployedInfo().getLengthOfService()));
                        form.setField("employmentIncome", String.valueOf(gocas.MeansInfo().EmployedInfo().getSalary()));
                        form.setField("companyMobile", gocas.MeansInfo().EmployedInfo().getCompanyNo());
                        break;
                    case "1": //self employed    
                        form.setField("incomeSource", "selfEmployed");
                        
                        if (StringHelperMisc.isNumeric(gocas.MeansInfo().SelfEmployedInfo().getNatureOfBusiness())){
                            form.setField("natureOfBusiness", BUSINESS_NATURE[Integer.parseInt(gocas.MeansInfo().SelfEmployedInfo().getNatureOfBusiness())]);
                        } else {
                            form.setField("natureOfBusiness", gocas.MeansInfo().SelfEmployedInfo().getNatureOfBusiness());
                        }
                        
                        form.setField("businessName", gocas.MeansInfo().SelfEmployedInfo().getNameOfBusiness());
                        
                        lsValue = gocas.MeansInfo().SelfEmployedInfo().getBusinessAddress();
                        if (!gocas.MeansInfo().SelfEmployedInfo().getBusinessTown().isEmpty()){
                            lsSQL = "SELECT" +
                                        "  CONCAT(a.sTownName, ', ', b.sProvName) sTownName" +
                                    " FROM TownCity a" +
                                        ", Province b" +
                                    " WHERE a.sProvIDxx = b.sProvIDxx" +
                                        " AND a.sTownIDxx = " + SQLUtil.toSQL(gocas.MeansInfo().SelfEmployedInfo().getBusinessTown());
                            loRSx = poGRider.executeQuery(lsSQL);
                            if (loRSx.next()){
                                lsValue += ", " + loRSx.getString("sTownName");
                            }
                        }
                        
                        form.setField("businessAddress", lsValue);
                        
                        form.setField("businessType", "Off");
                        switch (gocas.MeansInfo().SelfEmployedInfo().getBusinessType()){
                            case "0":
                                form.setField("businessType", "sole");
                                break;
                            case "1":
                                form.setField("businessType", "partnership");
                                break;
                            case "2":
                                form.setField("businessType", "corporation");
                                break;
                        }                        
                        
                        form.setField("yearsOfBusiness", String.valueOf(gocas.MeansInfo().SelfEmployedInfo().getBusinessLength()));
                        
                        form.setField("businessCapital", "Off");
                        switch (gocas.MeansInfo().SelfEmployedInfo().getOwnershipSize()){
                            case "0":
                                form.setField("businessCapital", "Micro(less than 100K)");
                                break;
                            case "1":
                                form.setField("businessCapital", "Small(less than 300K)");
                                break;
                            case "2":
                                form.setField("businessCapital", "Medium(less than 1M)");
                                break;
                            case "3":
                                form.setField("businessCapital", "Large(greater than 1M)");
                                break;
                        }
                        
                        form.setField("businessMonthlyIncome", String.valueOf(gocas.MeansInfo().SelfEmployedInfo().getIncome()));
                        form.setField("businessMonthlyExpense", String.valueOf(gocas.MeansInfo().SelfEmployedInfo().getMonthlyExpense()));
                        break;
                    case "2": //with financer   
                        form.setField("incomeSource", "withFinancer");
                        
                        form.setField("financer", "Off");
                        switch (gocas.MeansInfo().FinancerInfo().getSource()) {
                            case "0": //parents    
                                form.setField("financer", "family");
                                break;
                            case "1": //spouse    
                                form.setField("financer", "spouse");
                                break;
                            default: //relatives
                                form.setField("financer", "relatives");
                                break;
                                
                        }
                        
                        form.setField("financerName", gocas.MeansInfo().FinancerInfo().getFinancerName());
                        form.setField("financerIncomeRange", String.valueOf(gocas.MeansInfo().FinancerInfo().getAmount()));
                        
                        lsSQL = "SELECT sCntryNme FROM Country WHERE sCntryCde = " + SQLUtil.toSQL(gocas.MeansInfo().FinancerInfo().getCountry());
                        loRSx = poGRider.executeQuery(lsSQL);
                        if (loRSx.next()){
                            form.setField("financerCountry", loRSx.getString("sCntryNme"));
                        }
                        
                        
                        form.setField("financerContactNo", gocas.MeansInfo().FinancerInfo().getMobileNo());
                        form.setField("financerFacebook", gocas.MeansInfo().FinancerInfo().getFBAccount());
                        form.setField("financerEmail", gocas.MeansInfo().FinancerInfo().getEmailAddress());
                        break;
                    case "3": //pensioner   
                        form.setField("incomeSource", "pensioner");
                        
                        form.setField("pensionSource", "Off");
                        switch (gocas.MeansInfo().PensionerInfo().getSource()) {
                            case "0": //parents    
                                form.setField("pensionSource", "private");
                                break;
                            case "1": //spouse    
                                form.setField("pensionSource", "government");
                                break;                                
                        }
                        
                        form.setField("pensionIncomeRange", String.valueOf(gocas.MeansInfo().PensionerInfo().getAmount()));
                        form.setField("retirementYear1", String.valueOf(gocas.MeansInfo().PensionerInfo().getYearRetired()));
                        break;
                }
                
                //comaker
                form.setField("comakerLastName", gocas.CoMakerInfo().getLastName());
                form.setField("comakerFirstName", gocas.CoMakerInfo().getFirstName());
                form.setField("comakerMiddleName", gocas.CoMakerInfo().getMiddleName());
                form.setField("comakerSuffixName", gocas.CoMakerInfo().getSuffixName());
                
                if (gocas.CoMakerInfo().getMobileNoQty()> 0){
                    form.setField("comakerMobile", gocas.CoMakerInfo().getMobileNo(0));
                }
                
                form.setField("comakerEmail", "");
                form.setField("comakerViber", "");
                form.setField("comakerFacebook", gocas.CoMakerInfo().getFBAccount());
                
                lsSQL = "SELECT" +
                            "  CONCAT(a.sTownName, ', ', b.sProvName) sTownName" +
                        " FROM TownCity a" +
                            ", Province b" +
                        " WHERE a.sProvIDxx = b.sProvIDxx" +
                            " AND a.sTownIDxx = " + SQLUtil.toSQL(gocas.CoMakerInfo().getBirthPlace());
                loRSx = poGRider.executeQuery(lsSQL);
                if (loRSx.next()){
                    form.setField("comakerBirthPlace", loRSx.getString("sTownName"));
                }
                
                lsValue = gocas.CoMakerInfo().getBirthdate(); //2025-07-31
                form.setField("comakerBirthDate0", lsValue.substring(5, 7));
                form.setField("comakerBirthDate1", lsValue.substring(8, 10));   
                form.setField("comakerBirthDate2", lsValue.substring(0, 4));
                
                form.setField("comakerHouseNo", gocas.CoMakerInfo().ResidenceInfo().PresentAddress().getHouseNo());
                
                lsValue = "";
                if (!gocas.CoMakerInfo().ResidenceInfo().PresentAddress().getAddress1().isEmpty()){
                    lsValue = gocas.CoMakerInfo().ResidenceInfo().PresentAddress().getAddress1();
                }
                
                if (!gocas.CoMakerInfo().ResidenceInfo().PresentAddress().getAddress2().isEmpty()){
                    lsValue += gocas.CoMakerInfo().ResidenceInfo().PresentAddress().getAddress2();
                }
                
                if (!gocas.CoMakerInfo().ResidenceInfo().PresentAddress().getBarangay().isEmpty()){
                    lsSQL = "SELECT sBrgyName FROM Barangay WHERE sBrgyIDxx = " + SQLUtil.toSQL(gocas.CoMakerInfo().ResidenceInfo().PresentAddress().getBarangay());
                    loRSx = poGRider.executeQuery(lsSQL);
                    if (loRSx.next()){
                        lsValue += ", " + loRSx.getString("sBrgyName");
                    }
                }
                
                if (!gocas.CoMakerInfo().ResidenceInfo().PresentAddress().getLandMark().trim().replace("-", "").isEmpty()){
                    lsValue += " (landmark:" + gocas.CoMakerInfo().ResidenceInfo().PresentAddress().getLandMark() +")";
                }
                form.setField("comakerBarangay", lsValue);
                
                if (!gocas.CoMakerInfo().ResidenceInfo().PresentAddress().getTownCity().isEmpty()){
                    lsSQL = "SELECT" +
                                "  a.sTownName" +
                                ", b.sProvName" +
                            " FROM TownCity a" +
                                ", Province b" +
                            " WHERE a.sProvIDxx = b.sProvIDxx" +
                                " AND a.sTownIDxx = " + SQLUtil.toSQL(gocas.CoMakerInfo().ResidenceInfo().PresentAddress().getTownCity());
                    loRSx = poGRider.executeQuery(lsSQL);
                    if (loRSx.next()){
                        form.setField("comakerTown", loRSx.getString("sTownName"));
                        form.setField("comakerProvince", loRSx.getString("sProvName"));
                    }
                }
                
                form.setField("comakerCitizenship", "filipino");
                
                // Set the form flattening to true to lock all fields
                stamper.setFormFlattening(true); // <-- this locks the fields

                // Save and close
                stamper.close();
                reader.close();
                
                System.out.println("PDF form filled and fields locked.");
                
                System.out.println(gocas.toJSONString());
            } else {
                logwrapr.severe("GOCAS for not found.");
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
