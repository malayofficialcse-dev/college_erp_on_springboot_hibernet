package com.example.demo.controller;

import com.example.demo.model.FeePayment;
import com.example.demo.service.FeePaymentService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/fees")
@CrossOrigin(origins = "*")
public class FeePaymentController {

    @Autowired private FeePaymentService feePaymentService;

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
}
