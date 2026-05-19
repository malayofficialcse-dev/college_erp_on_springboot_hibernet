package com.example.demo.controller;

import com.example.demo.model.AcademicYear;
import com.example.demo.service.AcademicYearService;
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
@RequestMapping("/api/academic-years")
@CrossOrigin(origins = "*")
public class AcademicYearController {

    @Autowired private AcademicYearService academicYearService;

    @GetMapping
    public ResponseEntity<Page<AcademicYear>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(academicYearService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AcademicYear> getById(@PathVariable Long id) {
        return ResponseEntity.ok(academicYearService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AcademicYear> create(@Valid @RequestBody AcademicYear academicYear) {
        return ResponseEntity.status(HttpStatus.CREATED).body(academicYearService.create(academicYear));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<AcademicYear> update(@PathVariable Long id, @RequestBody AcademicYear details) {
        return ResponseEntity.ok(academicYearService.update(id, details));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        academicYearService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
