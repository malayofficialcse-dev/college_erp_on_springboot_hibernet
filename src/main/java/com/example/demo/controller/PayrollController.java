package com.example.demo.controller;

import com.example.demo.model.Payroll;
import com.example.demo.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/payroll")
@CrossOrigin(origins = "*")
public class PayrollController {

    @Autowired private PayrollService payrollService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<Page<Payroll>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(payrollService.getAllPayrolls(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<Payroll> getById(@PathVariable Long id) {
        return ResponseEntity.ok(payrollService.getById(id));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Page<Payroll>> getByEmployee(@PathVariable Long employeeId,
                                                        @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(payrollService.getByEmployee(employeeId, pageable));
    }

    @GetMapping("/month/{month}/year/{year}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<Page<Payroll>> getByMonthYear(@PathVariable Integer month,
                                                         @PathVariable Integer year,
                                                         @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(payrollService.getByMonthYear(month, year, pageable));
    }

    @GetMapping("/total/month/{month}/year/{year}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<Map<String, BigDecimal>> getTotalPayroll(@PathVariable Integer month,
                                                                    @PathVariable Integer year) {
        return ResponseEntity.ok(Map.of("totalPayroll", payrollService.getTotalPayrollByMonthYear(month, year)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<Payroll> generate(@RequestBody Payroll payroll) {
        return ResponseEntity.status(HttpStatus.CREATED).body(payrollService.generatePayroll(payroll));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<Payroll> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(payrollService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        payrollService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
