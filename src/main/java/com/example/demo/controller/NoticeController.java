package com.example.demo.controller;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Notice;
import com.example.demo.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/notices")
@CrossOrigin(origins = "*")
public class NoticeController {

    @Autowired private NoticeRepository noticeRepository;

    @GetMapping
    public ResponseEntity<Page<Notice>> getActive(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(noticeRepository.findByIsActiveTrue(pageable));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<Page<Notice>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(noticeRepository.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notice> getById(@PathVariable Long id) {
        return ResponseEntity.ok(noticeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notice", "id", id)));
    }

    @GetMapping("/audience/{audience}")
    public ResponseEntity<Page<Notice>> getByAudience(@PathVariable String audience,
                                                       @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(noticeRepository.findByTargetAudience(audience, pageable));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<Notice> create(@RequestBody Notice notice) {
        notice.setPublishedAt(LocalDateTime.now());
        notice.setActive(true);
        return ResponseEntity.status(HttpStatus.CREATED).body(noticeRepository.save(notice));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<Notice> update(@PathVariable Long id, @RequestBody Notice details) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notice", "id", id));
        notice.setTitle(details.getTitle());
        notice.setContent(details.getContent());
        notice.setNoticeType(details.getNoticeType());
        notice.setTargetAudience(details.getTargetAudience());
        notice.setExpiryDate(details.getExpiryDate());
        notice.setActive(details.isActive());
        return ResponseEntity.ok(noticeRepository.save(notice));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noticeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
