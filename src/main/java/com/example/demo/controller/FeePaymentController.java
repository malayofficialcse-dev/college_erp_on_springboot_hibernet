package com.example.demo.controller;

import com.example.demo.model.FeePayment;
import com.example.demo.service.FeePaymentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fees")
@CrossOrigin(origins = "*")
public class FeePaymentController {

    @Autowired private FeePaymentService feePaymentService;
    @Autowired private ExportService exportService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<Page<FeePayment>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(feePaymentService.getAllFeePayments(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeePayment> getById(@PathVariable Long id) {
        return ResponseEntity.ok(feePaymentService.getFeePaymentById(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<Page<FeePayment>> getByStudent(@PathVariable Long studentId,
                                                          @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(feePaymentService.getByStudent(studentId, pageable));
    }

    @GetMapping("/student/{studentId}/total")
    public ResponseEntity<Map<String, BigDecimal>> getTotalPaidByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(Map.of("totalPaid", feePaymentService.getTotalPaidByStudent(studentId)));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<FeePayment>> getByStatus(@PathVariable String status,
                                                         @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(feePaymentService.getByStatus(status, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<FeePayment>> search(@RequestParam(required = false) Long studentId,
                                                   @RequestParam(required = false) String status,
                                                   @RequestParam(required = false) String semester,
                                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
                                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
                                                   @RequestParam(required = false) String keyword,
                                                   @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(feePaymentService.search(studentId, status, semester, dateFrom, dateTo, keyword, pageable));
    }

    @GetMapping("/total-collected")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<Map<String, BigDecimal>> getTotalCollected() {
        return ResponseEntity.ok(Map.of("totalCollected", feePaymentService.getTotalCollected()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'STAFF')")
    public ResponseEntity<FeePayment> create(@RequestBody FeePayment feePayment) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feePaymentService.saveFeePayment(feePayment));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<FeePayment> update(@PathVariable Long id, @RequestBody FeePayment details) {
        return ResponseEntity.ok(feePaymentService.updateFeePayment(id, details));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        feePaymentService.deleteFeePayment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export/all")
    @Operation(summary = "Export fee payments", description = "Export all fee payment records as PDF, Excel, or CSV with filters")
    public ResponseEntity<byte[]> exportFeePayments(
            @RequestParam(defaultValue = "PDF") String format,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) throws Exception {
        
        List<FeePayment> payments = feePaymentService.getAllFeePayments().stream().collect(Collectors.toList());
        
        if (status != null) {
            payments = payments.stream()
                .filter(p -> status.equalsIgnoreCase(p.getStatus()))
                .collect(Collectors.toList());
        }
        
        if (dateFrom != null && dateTo != null) {
            payments = payments.stream()
                .filter(p -> !p.getPaymentDate().isBefore(dateFrom) && !p.getPaymentDate().isAfter(dateTo))
                .collect(Collectors.toList());
        }

        List<String> headers = Arrays.asList(
            "ID", "Student", "Amount", "Amount Paid", "Status", "Payment Date", "Semester"
        );

        List<Map<String, Object>> data = payments.stream()
            .map(p -> {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("ID", p.getId());
                row.put("Student", p.getStudent().getUser().getFullName());
                row.put("Amount", p.getAmount());
                row.put("Amount Paid", p.getAmountPaid());
                row.put("Status", p.getStatus());
                row.put("Payment Date", p.getPaymentDate());
                row.put("Semester", p.getSemester());
                return row;
            })
            .collect(Collectors.toList());

        ExportRequest request = ExportRequest.builder()
            .format(format)
            .module("FeePayments")
            .title("Fee Payment Records Export")
            .includeSummary(true)
            .build();

        byte[] content = exportService.generateExport(request, headers, data);

        String fileName = "Fee-Payments-Export." + format.toLowerCase();
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=" + fileName)
            .header("Content-Type", getContentType(format))
            .body(content);
    }

    private String getContentType(String format) {
        switch (format.toUpperCase()) {
            case "PDF":
                return "application/pdf";
            case "EXCEL":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "CSV":
                return "text/csv";
            default:
                return "application/octet-stream";
        }
    }
}
