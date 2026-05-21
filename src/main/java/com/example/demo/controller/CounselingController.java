package com.example.demo.controller;

import com.example.demo.model.Counseling;
import com.example.demo.service.CounselingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/counseling")
@CrossOrigin(origins = "*")
public class CounselingController {

    @Autowired
    private CounselingService counselingService;

    @GetMapping
    public ResponseEntity<Page<Counseling>> getAll(Pageable pageable) {
        return ResponseEntity.ok(counselingService.getAll(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Counseling>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        return ResponseEntity.ok(counselingService.search(keyword, status, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Counseling> getById(@PathVariable Long id) {
        return ResponseEntity.ok(counselingService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Counseling> create(@Valid @RequestBody Counseling counseling) {
        return ResponseEntity.status(HttpStatus.CREATED).body(counselingService.createCounseling(counseling));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Counseling> update(@PathVariable Long id, @Valid @RequestBody Counseling counseling) {
        return ResponseEntity.ok(counselingService.updateCounseling(id, counseling));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        counselingService.deleteCounseling(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/admit")
    public ResponseEntity<Counseling> markAsAdmitted(@PathVariable Long id) {
        return ResponseEntity.ok(counselingService.markAsAdmitted(id));
    }
}
