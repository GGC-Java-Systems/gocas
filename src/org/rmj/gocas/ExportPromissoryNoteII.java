package org.rmj.gocas;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.rmj.appdriver.MiscUtil;
import org.rmj.appdriver.SQLUtil;
import org.rmj.appdriver.agent.GRiderX;
import org.rmj.replication.utility.LogWrapper;

public class ExportPromissoryNoteII {

    /*
     * =========================================================
     * TABLE LAYOUT CONSTANTS
     * =========================================================
     * Measured directly from the "Promissory Note II" template
     * (page size is A4 -- 595.2 x 842 -- NOT the Letter size used
     * by "Special Agreement"). The 12 fixed AcroFields on page 1
     * are "DATE DUERow1".."DATE DUERow12" paired with
     * "AMOUNTRow1".."AMOUNTRow12" (this template does NOT use the
     * "fill_NN" naming Special Agreement uses).
     */
    private static final int ROWS_PER_TEMPLATE_PAGE = 12;

    // Column boundaries copied from the template's own field rects.
    private static final float TABLE_LEFT_X = 82.32f;
    private static final float TABLE_RIGHT_X = 513.0f;
    private static final float TABLE_DATE_COL_WIDTH = 301.56f - 82.32f;
    private static final float TABLE_AMOUNT_COL_WIDTH = 513.0f - 303.36f;

    // Usable vertical span on a continuation page (top margin / bottom
    // margin), sized for this template's A4 page height (842).
    private static final float CONT_TOP_Y = 790f;
    private static final float CONT_BOTTOM_Y = 55f;

    // Region of page 1 occupied by the static interest / default /
    // attorney's-fee clauses that sit directly below the table (measured:
    // text runs from y=~301 down to y=~101 in PDF bottom-up coordinates).
    // Only whited out and re-flowed onto the continuation page(s) when the
    // term overflows the fixed 12 rows -- left untouched otherwise.
    private static final float TERMS_BLOCK_X0 = 65f;
    private static final float TERMS_BLOCK_X1 = 535f;
    private static final float TERMS_BLOCK_Y0 = 90f;
    private static final float TERMS_BLOCK_Y1 = 305f;

    private static final String TERMS_PARA_1 =
            "with interest per month at ______________ percent (___%) from value "
            + "date of this note.";
    private static final String TERMS_PARA_2 =
            "In case any of the above installment payments are not made at their "
            + "stated maturity dates, the total principal sum then remaining unpaid "
            + "shall at once become due and payable. A penalty rate of "
            + "_____________ percent (___%) per month on the total outstanding "
            + "principal shall also be paid and collected. Demand, presentment for "
            + "payment and notice of dishonor are waived.";
    private static final String TERMS_PARA_3 =
            "Acceptance of partial or delayed payment by the holder hereof shall "
            + "not operate as a waiver of rights and remedies to which he/she/it is "
            + "entitled under this note.";
    private static final String TERMS_PARA_4 =
            "It is hereby agreed that if this note is placed in the hands of an "
            + "attorney for collection, I/WE shall pay the additional sum "
            + "equivalent to 25% of the total amount due, as attorney\u2019s fee "
            + "aside from incidental expenses and cost of collection. I/WE further "
            + "agree that any legal action arising here from shall be instituted in "
            + "the Courts of Dagupan City.";

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DecimalFormat PESO_FMT =
            new DecimalFormat("#,##0.00");

    public static void main(String[] args) {

        LogWrapper logwrapr = new LogWrapper("DCP.ExportFile", "dcp.log");

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
                String lsTemplate = "Promissory Note II";

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
                 * FIRST PAYMENT DATE (used to build the schedule below --
                 * this template has no "day"/"startDate"/"endDate" fields
                 * to fill directly, unlike Special Agreement).
                 * =====================================================
                 */
                String lsFirstPay = loRS.getString("dFirstPay");
                LocalDate loFirstPay = null;

                if (lsFirstPay != null && !lsFirstPay.trim().isEmpty()) {
                    loFirstPay = LocalDate.parse(lsFirstPay);
                }

                /*
                 * =====================================================
                 * PAYMENT SCHEDULE TABLE (dynamic, based on nAcctTerm)
                 * =====================================================
                 * The template only has 12 fixed rows on page 1. If the
                 * account term has more than 12 installments, the extra
                 * rows are flowed onto however many continuation pages
                 * are needed, inserted right after page 1.
                 */
                int lnAcctTerm = loRS.getInt("nAcctTerm");
                double lnMonAmort = loRS.getDouble("nMonAmort");

                List<ScheduleRow> loSchedule =
                        buildSchedule(loFirstPay, lnAcctTerm, lnMonAmort);

                List<ScheduleRow> loOverflow =
                        fillFixedScheduleRows(form, loSchedule);

                if (!loOverflow.isEmpty()) {
                    // Term exceeds 12 months: the interest/default/
                    // attorney's-fee clauses can no longer sit directly
                    // under the (now truncated) table on page 1 -- blank
                    // that area out and re-flow the same text right after
                    // the real end of the schedule, on the continuation
                    // page(s).
                    whiteOutTermsBlockOnPageOne(stamper);
                    addContinuationPages(stamper, reader, loOverflow, true);
                } else {
                    // Term fits in the fixed 12 rows: leave the template's
                    // own copy of the clauses exactly where it is.
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

                form.setField("CustomerName", lsCustomer.toUpperCase());

                String Company = "";
                String CompanyAddress = "";

                if (!loRS.getString("xBranch").isEmpty()) {
                    lsSQL = "SELECT a.sCompnyID, "
                            + " b.sCompnyNm AS company, "
                            + " a.sAddressx, "
                            + " c.sTownName AS companyAddress "
                            + " FROM Branch a "
                            + " LEFT JOIN Company b "
                            + " ON a.sCompnyID = b.sCompnyID "
                            + " LEFT JOIN TownCity c "
                            + " ON a.sTownIDxx = c.sTownIDxx "
                            + " WHERE a.sBranchCD = "
                            + SQLUtil.toSQL(loRS.getString("xBranch"));

                    ResultSet loRSx = poGRider.executeQuery(lsSQL);

                    if (loRSx.next()) {
                        Company = loRSx.getString("company");
                        CompanyAddress = loRSx.getString("sAddressx")
                                + ", "
                                + loRSx.getString("companyAddress");
                    }

                    form.setField("Company", Company.toUpperCase());
                    form.setField("CompanyAddress", CompanyAddress.toUpperCase());
                }
                double lnAmount = 0.00;
                String lsAmount = "";
                lnAmount = loRS.getDouble("nPNValuex");

                        lsAmount = String.format(
                                "%,.2f",
                                lnAmount
                        );
                form.setField("AmountText", numberToWords((int) lnAmount));        
                form.setField("Amount",  loRS.getString("nPNValuex"));

                // NOTE: this template has no "VENDEE" field. The customer's
                // typed name on the signature line (page 2) belongs in
                // "BUYER" instead. "COMAKER" is left blank -- no co-maker
                // data is currently joined into the query above.
                form.setField("BUYER", lsCustomer.toUpperCase());
                form.setField("COMAKER", "");

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
                
                String lsCoMaker = gocas.CoMakerInfo().getLastName() + ", "
                        + gocas.CoMakerInfo().getFirstName();

                if (!gocas.CoMakerInfo().getSuffixName().isEmpty()) {
                    lsCoMaker += " " + gocas.CoMakerInfo().getSuffixName();
                }

                if (!gocas.CoMakerInfo().getMiddleName().isEmpty()) {
                    lsCoMaker += " " + gocas.CoMakerInfo().getMiddleName();
                }

                form.setField("COMAKER", lsCoMaker.toUpperCase());

                /*
                 * =====================================================
                 * WITNESSES
                 * =====================================================
                 * This template's real field names are "Witness1" /
                 * "Witness2" (capital W) -- fixed here (the version
                 * copy-pasted from ExportSpecialAgreement used lowercase
                 * "witness1"/"witness2", which silently does nothing).
                 */
                form.setField("Witness1", "");
                form.setField("Witness2", "");


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
     * A single row of the payment schedule: one installment date and its
     * monthly amortization amount.
     */
    private static class ScheduleRow {

        final String date;
        final String amount;

        ScheduleRow(String date, String amount) {
            this.date = date;
            this.amount = amount;
        }
    }

    /**
     * Builds one schedule row per month of the account term, starting at
     * the first payment date, all sharing the same monthly amortization
     * amount.
     *
     * @param firstPay   first due date (may be {@code null} if unknown)
     * @param acctTerm   number of monthly installments
     * @param monAmort   amount due each installment
     * @return list of schedule rows, size == acctTerm (empty if term <= 0
     *         or firstPay is null)
     */
    private static List<ScheduleRow> buildSchedule(
            LocalDate firstPay, int acctTerm, double monAmort) {

        List<ScheduleRow> rows = new ArrayList<>();

        if (firstPay == null || acctTerm <= 0) {
            return rows;
        }

        String lsAmount = PESO_FMT.format(monAmort);

        for (int i = 0; i < acctTerm; i++) {
            LocalDate loDue = firstPay.plusMonths(i);
            rows.add(new ScheduleRow(loDue.format(DATE_FMT), lsAmount));
        }

        return rows;
    }

    /**
     * Fills the 12 fixed "DATE DUERowN" / "AMOUNTRowN" fields on page 1 of
     * the template with as many schedule rows as fit. Any unused rows
     * (term < 12) are cleared; any rows beyond the 12th are returned so
     * the caller can flow them onto continuation page(s).
     *
     * @param form     the AcroFields of the stamped document
     * @param schedule the full list of installment rows
     * @return the rows that did not fit (empty if term <= 12)
     */
    private static List<ScheduleRow> fillFixedScheduleRows(
            AcroFields form, List<ScheduleRow> schedule) throws IOException, DocumentException {

        for (int i = 0; i < ROWS_PER_TEMPLATE_PAGE; i++) {
            int rowNum = i + 1;
            String dateField = "DATE DUERow" + rowNum;
            String amountField = "AMOUNTRow" + rowNum;

            if (i < schedule.size()) {
                ScheduleRow row = schedule.get(i);
                form.setField(dateField, row.date);
                form.setField(amountField, row.amount);
            } else {
                form.setField(dateField, "");
                form.setField(amountField, "");
            }
        }

        if (schedule.size() <= ROWS_PER_TEMPLATE_PAGE) {
            return new ArrayList<>();
        }

        return new ArrayList<>(
                schedule.subList(ROWS_PER_TEMPLATE_PAGE, schedule.size())
        );
    }

    /**
     * Inserts however many continuation pages are needed, right after
     * page 1, to fit every overflow row. Uses iText's ColumnText flow so
     * a single call handles any overflow size -- if the table still
     * doesn't fit after one inserted page, another is added, and so on.
     *
     * @param stamper            the active PdfStamper
     * @param reader             the PdfReader backing the stamper (for page size)
     * @param overflow           rows beyond the fixed template's 12 slots
     * @param appendTermsClauses whether to flow the interest/default/
     *                           attorney's-fee clauses right after the
     *                           schedule's real last row
     */
    private static void addContinuationPages(
            PdfStamper stamper, PdfReader reader, List<ScheduleRow> overflow,
            boolean appendTermsClauses)
            throws DocumentException, IOException {

        Rectangle pageSize = reader.getPageSizeWithRotation(1);

        // Insert the first continuation page right after page 1 (index 2),
        // pushing the original page 2 (signature/acknowledgment) later.
        int pageNum = 2;
        stamper.insertPage(pageNum, pageSize);

        PdfContentByte canvas = stamper.getOverContent(pageNum);
        drawContinuationLabel(canvas);

        // Table + (optionally) the clause paragraphs are added as elements
        // of the SAME ColumnText flow, so the paragraphs automatically pick
        // up right where the table's real last row ends -- including
        // spilling onto yet another page if the combined content doesn't
        // fit on one continuation page.
        ColumnText ct = new ColumnText(canvas);
        ct.addElement(buildScheduleTable(overflow));

        if (appendTermsClauses) {
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

            String[] paras = {TERMS_PARA_1, TERMS_PARA_2, TERMS_PARA_3, TERMS_PARA_4};
            for (int i = 0; i < paras.length; i++) {
                Paragraph p = new Paragraph(paras[i], bodyFont);
                p.setSpacingBefore(i == 0 ? 14f : 10f);
                p.setLeading(13f);
                ct.addElement(p);
            }
        }

        ct.setSimpleColumn(TABLE_LEFT_X, CONT_BOTTOM_Y, TABLE_RIGHT_X, CONT_TOP_Y);

        int status = ct.go();

        while (ColumnText.hasMoreText(status)) {
            pageNum++;
            stamper.insertPage(pageNum, pageSize);

            canvas = stamper.getOverContent(pageNum);
            drawContinuationLabel(canvas);

            ct.setCanvas(canvas);
            ct.setSimpleColumn(TABLE_LEFT_X, CONT_BOTTOM_Y, TABLE_RIGHT_X, CONT_TOP_Y);

            status = ct.go();
        }
    }

    /**
     * Draws a white rectangle over the template's static interest/default/
     * attorney's-fee clauses on page 1. Only called when the schedule
     * overflows onto continuation pages, since the clauses get re-drawn
     * after the real last row there instead.
     */
    private static void whiteOutTermsBlockOnPageOne(PdfStamper stamper) {
        PdfContentByte canvas = stamper.getOverContent(1);
        canvas.saveState();
        canvas.setColorFill(BaseColor.WHITE);
        canvas.rectangle(
                TERMS_BLOCK_X0,
                TERMS_BLOCK_Y0,
                TERMS_BLOCK_X1 - TERMS_BLOCK_X0,
                TERMS_BLOCK_Y1 - TERMS_BLOCK_Y0
        );
        canvas.fill();
        canvas.restoreState();
    }

    /**
     * Draws the small "(continued)" label above the table on continuation
     * pages. The table's own column headers are handled by
     * {@link #buildScheduleTable}'s {@code setHeaderRows(1)}, so they
     * repeat automatically with identical styling on every page -- this
     * is just an orientation label, not part of the table itself.
     */
    private static void drawContinuationLabel(PdfContentByte canvas) throws DocumentException {

        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);

        ColumnText label = new ColumnText(canvas);
        label.setSimpleColumn(
                TABLE_LEFT_X, CONT_TOP_Y + 20,
                TABLE_RIGHT_X, CONT_TOP_Y + 45
        );
        label.addElement(new Paragraph("Payment Schedule (continued)", labelFont));
        label.go();
    }

    /**
     * Builds the two-column (DATE DUE / AMOUNT) schedule table, styled to
     * match the template's own page-1 table -- thin black grid lines, a
     * plain (unshaded) centered header, and a left-aligned \u20B1 symbol in
     * the amount column -- so the table reads as one continuous, uniform
     * object whether it stays on page 1 or spills onto continuation pages.
     * <p>
     * The header row is baked into the table itself via
     * {@code setHeaderRows(1)}, so iText automatically re-draws it,
     * pixel-identical, at the top of every page the table flows onto.
     */
    private static PdfPTable buildScheduleTable(List<ScheduleRow> rows) {

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10.4f);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10.4f);

        PdfPTable table = new PdfPTable(new float[]{TABLE_DATE_COL_WIDTH, TABLE_AMOUNT_COL_WIDTH});
        table.setTotalWidth(TABLE_DATE_COL_WIDTH + TABLE_AMOUNT_COL_WIDTH);
        table.setLockedWidth(true);
        table.setKeepTogether(false); // allow the table to split across pages

        PdfPCell dateHeader = new PdfPCell(new Phrase("DATE DUE", headerFont));
        dateHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        dateHeader.setVerticalAlignment(Element.ALIGN_MIDDLE);
        dateHeader.setPadding(6f);
        dateHeader.setBorderWidth(0.75f);
        table.addCell(dateHeader);

        PdfPCell amountHeader = new PdfPCell(new Phrase("AMOUNT", headerFont));
        amountHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        amountHeader.setVerticalAlignment(Element.ALIGN_MIDDLE);
        amountHeader.setPadding(6f);
        amountHeader.setBorderWidth(0.75f);
        table.addCell(amountHeader);

        table.setHeaderRows(1);

        for (ScheduleRow row : rows) {
            PdfPCell dateCell = new PdfPCell(new Phrase(row.date, cellFont));
            dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dateCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            dateCell.setPadding(5f);
            dateCell.setBorderWidth(0.75f);
            dateCell.setFixedHeight(21.3f);
            table.addCell(dateCell);

            PdfPCell amountCell = new PdfPCell(new Phrase("\u20B1 " + row.amount, cellFont));
            amountCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            amountCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            amountCell.setPadding(5f);
            amountCell.setBorderWidth(0.75f);
            amountCell.setFixedHeight(21.3f);
            table.addCell(amountCell);
        }

        return table;
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