package com.example.demo.controller;

import com.example.demo.model.Admission;
import com.example.demo.model.AdmissionEmi;
import com.example.demo.service.AdmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admissions")
@CrossOrigin(origins = "*")
public class AdmissionController {

    @Autowired private AdmissionService admissionService;

    @GetMapping
    public ResponseEntity<Page<Admission>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(admissionService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Admission> getById(@PathVariable Long id) {
        return ResponseEntity.ok(admissionService.getById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Admission>> search(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String paymentPlan,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(admissionService.search(
                studentId, courseId, departmentId,
                status, academicYear, paymentPlan, keyword, pageable));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Admission>> getByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(admissionService.getByStudent(studentId));
    }

    @GetMapping("/{id}/emi-schedule")
    public ResponseEntity<List<AdmissionEmi>> getEmiSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(admissionService.getEmiSchedule(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'ACCOUNTANT')")
    public ResponseEntity<Admission> create(@RequestBody Admission admission) {
        return ResponseEntity.status(HttpStatus.CREATED).body(admissionService.createAdmission(admission));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'ACCOUNTANT')")
    public ResponseEntity<Admission> update(@PathVariable Long id, @RequestBody Admission details) {
        return ResponseEntity.ok(admissionService.updateAdmission(id, details));
    }

    @PostMapping("/emi/{emiId}/pay")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'ACCOUNTANT')")
    public ResponseEntity<AdmissionEmi> payEmi(@PathVariable Long emiId,
                                               @RequestBody AdmissionEmi paymentDetails) {
        return ResponseEntity.ok(admissionService.recordEmiPayment(emiId, paymentDetails));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        admissionService.deleteAdmission(id);
        return ResponseEntity.noContent().build();
    }
}
