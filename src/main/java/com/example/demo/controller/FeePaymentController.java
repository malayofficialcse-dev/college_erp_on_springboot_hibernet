package com.example.demo.controller;

import com.example.demo.model.FeePayment;
import com.example.demo.service.FeePaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fees")
public class FeePaymentController {

    @Autowired
    private FeePaymentService feePaymentService;

    @GetMapping
    public List<FeePayment> getAllFeePayments() {
        return feePaymentService.getAllFeePayments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeePayment> getFeePaymentById(@PathVariable Long id) {
        FeePayment feePayment = feePaymentService.getFeePaymentById(id);
        if (feePayment != null) {
            return ResponseEntity.ok(feePayment);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public FeePayment createFeePayment(@RequestBody FeePayment feePayment) {
        return feePaymentService.saveFeePayment(feePayment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeePayment> updateFeePayment(@PathVariable Long id, @RequestBody FeePayment feePaymentDetails) {
        FeePayment updatedFeePayment = feePaymentService.updateFeePayment(id, feePaymentDetails);
        if (updatedFeePayment != null) {
            return ResponseEntity.ok(updatedFeePayment);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeePayment(@PathVariable Long id) {
        feePaymentService.deleteFeePayment(id);
        return ResponseEntity.ok().build();
    }
}
