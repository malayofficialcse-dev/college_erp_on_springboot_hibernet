package com.example.demo.controller;

import com.example.demo.model.Semester;
import com.example.demo.service.SemesterService;
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
@RequestMapping("/api/semesters")
@CrossOrigin(origins = "*")
public class SemesterController {

    @Autowired private SemesterService semesterService;

    @GetMapping
    public ResponseEntity<Page<Semester>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(semesterService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Semester> getById(@PathVariable Long id) {
        return ResponseEntity.ok(semesterService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Semester> create(@Valid @RequestBody Semester semester) {
        return ResponseEntity.status(HttpStatus.CREATED).body(semesterService.create(semester));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<Semester> update(@PathVariable Long id, @RequestBody Semester details) {
        return ResponseEntity.ok(semesterService.update(id, details));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        semesterService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
