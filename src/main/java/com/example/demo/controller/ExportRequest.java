package com.example.demo.export;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportRequest {
    private String format; // PDF, EXCEL, CSV
    private String module; // Students, Finance, Attendance, Exams, etc.
    private LocalDate startDate;
    private LocalDate endDate;
    private String department; // Optional filter
    private String status; // Optional filter
    private Map<String, String> additionalFilters; // Dynamic filters
    private String title;
    private boolean includeSummary;
}
