package com.example.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ExportService {

    @Autowired
    private ExcelExporter excelExporter;

    @Autowired
    private PdfExporter pdfExporter;

    public byte[] generateExport(ExportRequest request, List<String> headers, 
                                 List<Map<String, Object>> data) throws Exception {
        String format = request.getFormat().toUpperCase();
        String title = request.getTitle() != null ? request.getTitle() : "Export Report";
        String subtitle = "Generated on " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        log.info("Generating {} export for module: {}", format, request.getModule());

        switch (format) {
            case "EXCEL":
                return excelExporter.exportToExcel(title, headers, data);
            case "PDF":
                return pdfExporter.exportToPdf(title, subtitle, headers, data, request.isIncludeSummary());
            case "CSV":
                return generateCsv(headers, data);
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }

    private byte[] generateCsv(List<String> headers, List<Map<String, Object>> data) {
        StringBuilder csv = new StringBuilder();
        
        // Add headers
        for (int i = 0; i < headers.size(); i++) {
            csv.append(escapeCSV(headers.get(i)));
            if (i < headers.size() - 1) csv.append(",");
        }
        csv.append("\n");

        // Add data
        for (Map<String, Object> rowData : data) {
            for (int i = 0; i < headers.size(); i++) {
                Object value = rowData.get(headers.get(i));
                csv.append(escapeCSV(value != null ? value.toString() : ""));
                if (i < headers.size() - 1) csv.append(",");
            }
            csv.append("\n");
        }

        return csv.toString().getBytes();
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
