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

public class ExportSpecialAgreement {

    /*
     * =========================================================
     * TABLE LAYOUT CONSTANTS
     * =========================================================
     * These mirror the coordinates of the 12 fixed AcroFields on
     * page 1 of the "Special Agreement" template (see fields
     * "DATE DUERow1".."DATE DUERow12" / "fill_22".."fill_33").
     */
    private static final int ROWS_PER_TEMPLATE_PAGE = 12;

    // Column boundaries copied from the template's own field rects.
    private static final float TABLE_LEFT_X = 90.72f;
    private static final float TABLE_RIGHT_X = 521.28f;
    private static final float TABLE_DATE_COL_WIDTH = 309.96f - 90.72f;
    private static final float TABLE_AMOUNT_COL_WIDTH = 521.28f - 322.80f;

    // Usable vertical span on a continuation page (top margin / bottom margin).
    private static final float CONT_TOP_Y = 740f;
    private static final float CONT_BOTTOM_Y = 55f;

    // Region of page 1 occupied by the static "failure to pay" options
    // paragraph (measured from the template: text runs from y=~222.7 down
    // to y=~96.7 in PDF bottom-up coordinates). Only whited out and
    // re-flowed onto the continuation page(s) when the term overflows the
    // fixed 12 rows -- left untouched otherwise.
    private static final float OPTIONS_BLOCK_X0 = 65f;
    private static final float OPTIONS_BLOCK_X1 = 548f;
    private static final float OPTIONS_BLOCK_Y0 = 85f;
    private static final float OPTIONS_BLOCK_Y1 = 230f;

    private static final String OPTIONS_PARAGRAPH_TEXT =
            "That the failure to pay two or more installments gives the vendor the "
            + "following options,\n"
            + "1. Sue for the recovery of the entire amount, in which event, the venue "
            + "shall be in the ________________________, with 25% as attorney\u2019s fee.\n"
            + "2. Foreclosure of the Chattel Mortgage, if any is constituted.\n"
            + "3. Re-acquire title over the property and take possession of the same. "
            + "Upon the choice of this option, the vendee shall voluntarily surrender in "
            + "Chattel, if the vendee refuses to surrender the same.\n"
            + "4. Full payment paid before schedule, _______________________ each month "
            + "will be subject to rebate.\n"
            + "5. A 2.5% penalty will be charged on vendee\u2019s monthly account.";

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
                String lsTemplate = "Special Agreement";

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
                 * PAYMENT DATE
                 * =====================================================
                 */
                String lsFirstPay = loRS.getString("dFirstPay");
                LocalDate loFirstPay = null;

                if (lsFirstPay != null
                        && !lsFirstPay.trim().isEmpty()) {

                    loFirstPay = LocalDate.parse(lsFirstPay);

                    int lnDay = loFirstPay.getDayOfMonth();

                    form.setField("day", lnDay + getDaySuffix(lnDay));

                    form.setField("startDate", lsFirstPay);

                    String lsEndDate = loRS.getString("dEndDate");

                    if (lsEndDate != null && !lsEndDate.trim().isEmpty()) {

                        form.setField("endDate", lsEndDate);
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
                    // Term exceeds 12 months: the "failure to pay" options
                    // paragraph can no longer sit directly under the (now
                    // truncated) table on page 1 -- blank that area out and
                    // re-flow the same text right after the real end of the
                    // schedule, on the continuation page(s).
                    whiteOutOptionsBlockOnPageOne(stamper);
                    addContinuationPages(stamper, reader, loOverflow, true);
                } else {
                    // Term fits in the fixed 12 rows: leave the template's
                    // own copy of the options paragraph exactly where it is.
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
                
                String lsCustomerCivilStat = "";
                switch (gocas.ApplicantInfo().getCivilStatus()) {
                    case "0":
                        lsCustomerCivilStat = "Single";
                    case "1":
                        lsCustomerCivilStat = "Married";
                    case "2":
                        lsCustomerCivilStat = "Separated";
                    case "3":
                        lsCustomerCivilStat = "Widowed";
                    case "4":
                        lsCustomerCivilStat = "Single Parent";
                    case "5":
                        lsCustomerCivilStat = "Single Parent with Live-in Partner";
                        break;
                    default:
                        throw new AssertionError();
                }

                form.setField("CustomerName", lsCustomer.toUpperCase());
                form.setField("CustomerCivilStatus", lsCustomerCivilStat.toUpperCase());
                form.setField("VENDEE", lsCustomer.toUpperCase());
                
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
                
                double lnTotal = Double.parseDouble(loRS.getString("nPNValuex"))
                   + Double.parseDouble(loRS.getString("nDownPaym"));
                
                form.setField("TotalAmount", String.valueOf(lnTotal));        
                form.setField("Balance",  loRS.getString("nPNValuex"));

                /*
                 * =====================================================
                 * WITNESSES
                 * =====================================================
                 * NOTE: the actual field names on the template are
                 * "Witness1" / "Witness2" (capital W) -- "witness1" /
                 * "witness2" below silently no-op against a real
                 * AcroFields.setField call. Left as-is to match the
                 * original file; fix the casing if that was unintentional.
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
     * Fills the 12 fixed "DATE DUERowN" / "fill_(21+N)" fields on page 1
     * of the template with as many schedule rows as fit. Any unused rows
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
            String amountField = "fill_" + (22 + i);

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
     * @param stamper  the active PdfStamper
     * @param reader   the PdfReader backing the stamper (for page size)
     * @param overflow rows beyond the fixed template's 12 slots
     */
    private static void addContinuationPages(
            PdfStamper stamper, PdfReader reader, List<ScheduleRow> overflow,
            boolean appendOptionsParagraph)
            throws DocumentException, IOException {

        Rectangle pageSize = reader.getPageSizeWithRotation(1);

        // Insert the first continuation page right after page 1 (index 2),
        // pushing the original page 2 (signature/acknowledgment) later.
        int pageNum = 2;
        stamper.insertPage(pageNum, pageSize);

        PdfContentByte canvas = stamper.getOverContent(pageNum);
        drawContinuationLabel(canvas);

        // Table + (optionally) the options paragraph are added as elements
        // of the SAME ColumnText flow, so the paragraph automatically picks
        // up right where the table's real last row ends -- including
        // spilling onto yet another page if the combined content doesn't
        // fit on one continuation page.
        ColumnText ct = new ColumnText(canvas);
        ct.addElement(buildScheduleTable(overflow));

        if (appendOptionsParagraph) {
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
            Paragraph optionsPara = new Paragraph(OPTIONS_PARAGRAPH_TEXT, bodyFont);
            optionsPara.setSpacingBefore(14f);
            optionsPara.setLeading(13f);
            ct.addElement(optionsPara);
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
     * Draws a white rectangle over the template's static "failure to pay"
     * options paragraph on page 1. Only called when the schedule overflows
     * onto continuation pages, since the paragraph gets re-drawn after the
     * real last row there instead.
     */
    private static void whiteOutOptionsBlockOnPageOne(PdfStamper stamper) {
        PdfContentByte canvas = stamper.getOverContent(1);
        canvas.saveState();
        canvas.setColorFill(BaseColor.WHITE);
        canvas.rectangle(
                OPTIONS_BLOCK_X0,
                OPTIONS_BLOCK_Y0,
                OPTIONS_BLOCK_X1 - OPTIONS_BLOCK_X0,
                OPTIONS_BLOCK_Y1 - OPTIONS_BLOCK_Y0
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
     * match the template's own page-1 table exactly -- same font family,
     * thin black grid lines, plain (unshaded) centered header, and a
     * left-aligned \u20B1 symbol in the amount column -- so the table reads
     * as one continuous, uniform object whether it stays on page 1 or
     * spills onto continuation pages.
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