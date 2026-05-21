package com.example.demo.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PdfExporter {

    public byte[] exportToPdf(String title, String subtitle, List<String> headers, 
                              List<Map<String, Object>> data, boolean includeSummary) throws DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate(), 25, 25, 25, 25);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Add title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
        Paragraph titlePara = new Paragraph(title, titleFont);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        titlePara.setSpacingAfter(10);
        document.add(titlePara);

        // Add subtitle (timestamp)
        if (subtitle != null) {
            Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);
            Paragraph subtitlePara = new Paragraph(subtitle, subtitleFont);
            subtitlePara.setAlignment(Element.ALIGN_RIGHT);
            subtitlePara.setSpacingAfter(15);
            document.add(subtitlePara);
        }

        // Create table
        PdfPTable table = new PdfPTable(headers.size());
        table.setWidthPercentage(100);

        // Add header cells
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(new BaseColor(0, 51, 102)); // Dark blue
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }

        // Add data rows
        Font dataFont = new Font(Font.FontFamily.HELVETICA, 10);
        for (Map<String, Object> rowData : data) {
            for (String header : headers) {
                Object value = rowData.get(header);
                String cellValue = value != null ? value.toString() : "";
                PdfPCell cell = new PdfPCell(new Phrase(cellValue, dataFont));
                cell.setPadding(5);
                table.addCell(cell);
            }
        }

        document.add(table);

        // Add summary if requested
        if (includeSummary && !data.isEmpty()) {
            document.add(new Paragraph("\n"));
            Font summaryFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            Paragraph summary = new Paragraph("Summary: Total Records: " + data.size(), summaryFont);
            summary.setAlignment(Element.ALIGN_RIGHT);
            document.add(summary);
        }

        document.close();
        return outputStream.toByteArray();
    }
}
