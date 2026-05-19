package com.example.demo.controller;

import com.example.demo.model.Notice;
import com.example.demo.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/notices")
@CrossOrigin(origins = "*")
public class NoticeController {

    @Autowired private NoticeService noticeService;

    @GetMapping
    public ResponseEntity<Page<Notice>> getActive(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(noticeService.getActive(pageable));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<Page<Notice>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(noticeService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notice> getById(@PathVariable Long id) {
        return ResponseEntity.ok(noticeService.getById(id));
    }

    @GetMapping("/audience/{audience}")
    public ResponseEntity<Page<Notice>> getByAudience(@PathVariable String audience,
                                                       @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(noticeService.getByAudience(audience, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Notice>> search(@RequestParam(required = false) String audience,
                                               @RequestParam(required = false) Long departmentId,
                                               @RequestParam(required = false) String noticeType,
                                               @RequestParam(required = false) Boolean active,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
                                               @RequestParam(required = false) String keyword,
                                               @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(noticeService.search(audience, departmentId, noticeType, active, dateFrom, dateTo, keyword, pageable));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<Notice> create(@RequestBody Notice notice) {
        return ResponseEntity.status(HttpStatus.CREATED).body(noticeService.create(notice));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<Notice> update(@PathVariable Long id, @RequestBody Notice details) {
        return ResponseEntity.ok(noticeService.update(id, details));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
