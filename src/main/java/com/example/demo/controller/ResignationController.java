package com.example.demo.controller;

import com.example.demo.model.Resignation;
import com.example.demo.service.ResignationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resignations")
@CrossOrigin(origins = "*")
public class ResignationController {

    @Autowired private ResignationService resignationService;

    // HR: all resignations
    @GetMapping
    public ResponseEntity<Page<Resignation>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(resignationService.getAll(pageable));
    }

    // HR: filter by status
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Resignation>> getByStatus(@PathVariable String status,
                                                          @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(resignationService.getByStatus(status, pageable));
    }

    // Employee: my resignations
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Page<Resignation>> getByEmployee(@PathVariable Long employeeId,
                                                            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(resignationService.getByEmployee(employeeId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resignation> getById(@PathVariable Long id) {
        return ResponseEntity.ok(resignationService.getById(id));
    }

    // Employee: submit resignation
    @PostMapping
    public ResponseEntity<Resignation> submit(@RequestBody Resignation resignation) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resignationService.submit(resignation));
    }

    // HR: approve or reject
    @PatchMapping("/{id}/status")
    public ResponseEntity<Resignation> updateStatus(@PathVariable Long id,
                                                     @RequestParam String status,
                                                     @RequestParam(required = false) String hrRemarks,
                                                     @RequestParam(required = false) Long reviewedById) {
        return ResponseEntity.ok(resignationService.updateStatus(id, status, hrRemarks, reviewedById));
    }

    // Employee: withdraw resignation
    @PatchMapping("/{id}/withdraw")
    public ResponseEntity<Resignation> withdraw(@PathVariable Long id) {
        return ResponseEntity.ok(resignationService.withdraw(id));
    }
}
