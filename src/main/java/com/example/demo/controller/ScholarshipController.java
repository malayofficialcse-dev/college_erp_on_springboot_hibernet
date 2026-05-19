package com.example.demo.controller;

import com.example.demo.model.Scholarship;
import com.example.demo.service.ScholarshipService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scholarships")
@CrossOrigin(origins = "*")
public class ScholarshipController {

    @Autowired private ScholarshipService scholarshipService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'STAFF')")
    public ResponseEntity<Page<Scholarship>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(scholarshipService.getAllScholarships(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Scholarship> getById(@PathVariable Long id) {
        return ResponseEntity.ok(scholarshipService.getScholarshipById(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<Page<Scholarship>> getByStudent(@PathVariable Long studentId,
                                                           @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(scholarshipService.getByStudent(studentId, pageable));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Scholarship>> getByStatus(@PathVariable String status,
                                                          @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(scholarshipService.getByStatus(status, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<Scholarship> create(@Valid @RequestBody Scholarship scholarship) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scholarshipService.createScholarship(scholarship));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<Scholarship> update(@PathVariable Long id, @RequestBody Scholarship details) {
        return ResponseEntity.ok(scholarshipService.updateScholarship(id, details));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scholarshipService.deleteScholarship(id);
        return ResponseEntity.noContent().build();
    }
}
