package org.innovative.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.innovative.model.Quote;
import org.innovative.model.QuoteLine;
import org.innovative.model.Customer;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Service
public class QuotePdfService {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.DARK_GRAY);
    private static final Font BOLD_FONT = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font TOTAL_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, new BaseColor(37, 99, 235));

    public byte[] generateQuotePdf(Quote quote) throws DocumentException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);
        PdfWriter.getInstance(document, out);
        document.open();

        // 1. Header
        Paragraph company = new Paragraph("Innovative Trailers", TITLE_FONT);
        document.add(company);
        document.add(new Paragraph("Sales Quote", NORMAL_FONT));
        document.add(new Chunk(new LineSeparator(1f, 100, new BaseColor(229, 231, 235), Element.ALIGN_CENTER, -2)));

        // 2. Info Table
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingBefore(15);
        infoTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        // Left Side: Quote Details
        PdfPCell qCell = new PdfPCell();
        qCell.setBorder(Rectangle.NO_BORDER);
        qCell.addElement(new Phrase("QUOTE DETAILS", BOLD_FONT));
        qCell.addElement(new Phrase("ID: " + quote.getId().toString().substring(0, 8).toUpperCase(), NORMAL_FONT));
        if (quote.getCreatedAt() != null) {
            qCell.addElement(new Phrase("Date: " + quote.getCreatedAt().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")), NORMAL_FONT));
        }
        infoTable.addCell(qCell);

        // Right Side: Customer Info (Fixed to use quote.getCustomer())
        PdfPCell cCell = new PdfPCell();
        cCell.setBorder(Rectangle.NO_BORDER);
        cCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        Customer customer = quote.getCustomer();
        if (customer != null) {
            cCell.addElement(new Phrase("BILL TO", BOLD_FONT));
            cCell.addElement(new Phrase(customer.getName(), NORMAL_FONT));
            if (customer.getEmail() != null) cCell.addElement(new Phrase(customer.getEmail(), NORMAL_FONT));
            if (customer.getPhone() != null) cCell.addElement(new Phrase(customer.getPhone(), NORMAL_FONT));
        }
        infoTable.addCell(cCell);
        document.add(infoTable);

        // 3. Items Table
        PdfPTable itemTable = new PdfPTable(4);
        itemTable.setWidthPercentage(100);
        itemTable.setWidths(new float[]{4f, 1f, 2f, 2f});
        itemTable.setSpacingBefore(20);

        String[] headers = {"Description", "Qty", "Price", "Total"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, HEADER_FONT));
            cell.setBackgroundColor(new BaseColor(37, 99, 235));
            cell.setPadding(6);
            cell.setBorder(Rectangle.NO_BORDER);
            itemTable.addCell(cell);
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        for (QuoteLine line : quote.getLines()) {
            addCell(itemTable, line.getAssembly() != null ? line.getAssembly().getName() : "Item", NORMAL_FONT, Element.ALIGN_LEFT);
            addCell(itemTable, String.valueOf(line.getQuantity()), NORMAL_FONT, Element.ALIGN_CENTER);
            addCell(itemTable, "$" + (line.getUnitPrice() != null ? line.getUnitPrice().toString() : "0.00"), NORMAL_FONT, Element.ALIGN_RIGHT);
            addCell(itemTable, "$" + (line.getLineTotal() != null ? line.getLineTotal().toString() : "0.00"), BOLD_FONT, Element.ALIGN_RIGHT);
            if (line.getLineTotal() != null) subtotal = subtotal.add(line.getLineTotal());
        }
        document.add(itemTable);

        // 4. Totals (Calculated on the fly to avoid missing DB columns)
        PdfPTable totalTable = new PdfPTable(2);
        totalTable.setWidthPercentage(40);
        totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalTable.setSpacingBefore(10);

        PdfPCell label = new PdfPCell(new Phrase("TOTAL AMOUNT", BOLD_FONT));
        label.setBorder(Rectangle.TOP);
        label.setPadding(8);

        PdfPCell value = new PdfPCell(new Phrase("$" + subtotal.toString(), TOTAL_FONT));
        value.setBorder(Rectangle.TOP);
        value.setHorizontalAlignment(Element.ALIGN_RIGHT);
        value.setPadding(8);

        totalTable.addCell(label);
        totalTable.addCell(value);
        document.add(totalTable);

        document.close();
        return out.toByteArray();
    }

    private void addCell(PdfPTable table, String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(new BaseColor(243, 244, 246));
        cell.setHorizontalAlignment(align);
        table.addCell(cell);
    }
}