package com.example.demo.controller;

import com.example.demo.dto.finance.FeeInvoiceRequest;
import com.example.demo.dto.finance.FeeInvoiceResponse;
import com.example.demo.dto.finance.FeePaymentTransactionRequest;
import com.example.demo.dto.finance.PayrollSlipResponse;
import com.example.demo.model.FeeInvoice;
import com.example.demo.service.FinanceFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finance")
@CrossOrigin(origins = "*")
public class FinanceFeatureController {

    @Autowired private FinanceFeatureService financeFeatureService;

    @GetMapping("/invoices")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'STAFF')")
    public ResponseEntity<Page<FeeInvoice>> getInvoices(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(financeFeatureService.getInvoices(pageable));
    }

    @GetMapping("/invoices/student/{studentId}")
    public ResponseEntity<Page<FeeInvoice>> getInvoicesByStudent(@PathVariable Long studentId,
                                                                 @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(financeFeatureService.getInvoicesByStudent(studentId, pageable));
    }

    @GetMapping("/invoices/{invoiceId}")
    public ResponseEntity<FeeInvoiceResponse> getInvoice(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(financeFeatureService.getInvoice(invoiceId));
    }

    @PostMapping("/invoices")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<FeeInvoiceResponse> generateInvoice(@RequestBody FeeInvoiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(financeFeatureService.generateInvoice(request));
    }

    @PostMapping("/invoices/{invoiceId}/payments")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'STAFF')")
    public ResponseEntity<FeeInvoiceResponse> payInvoice(@PathVariable Long invoiceId,
                                                         @RequestBody FeePaymentTransactionRequest request) {
        return ResponseEntity.ok(financeFeatureService.recordPartialPayment(invoiceId, request));
    }

    @GetMapping("/payroll/{payrollId}/slip")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'PRINCIPAL')")
    public ResponseEntity<PayrollSlipResponse> getPayrollSlip(@PathVariable Long payrollId) {
        return ResponseEntity.ok(financeFeatureService.generatePayrollSlip(payrollId));
    }
}
