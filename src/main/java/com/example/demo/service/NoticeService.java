package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Notice;
import com.example.demo.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoticeService {

    @Autowired private NoticeRepository noticeRepository;

    public Page<Notice> getActive(Pageable pageable) {
        return noticeRepository.findByIsActiveTrue(pageable);
    }

    public Page<Notice> getAll(Pageable pageable) {
        return noticeRepository.findAll(pageable);
    }

    public Notice getById(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notice", "id", id));
    }

    public Page<Notice> getByAudience(String audience, Pageable pageable) {
        return noticeRepository.findByTargetAudience(audience, pageable);
    }

    public List<Notice> getRelevantNotices(String audience, Long departmentId, int limit) {
        return noticeRepository.findRelevantNotices(audience, departmentId, LocalDate.now(), Pageable.ofSize(limit));
    }

    public Page<Notice> search(String audience, Long departmentId, String noticeType, Boolean active,
                               LocalDate dateFrom, LocalDate dateTo, String keyword, Pageable pageable) {
        LocalDateTime start = dateFrom != null ? dateFrom.atStartOfDay() : null;
        LocalDateTime endExclusive = dateTo != null ? dateTo.plusDays(1).atStartOfDay() : null;
        return noticeRepository.search(emptyToNull(audience), departmentId, emptyToNull(noticeType), active,
                start, endExclusive, emptyToNull(keyword), pageable);
    }

    @Transactional
    public Notice create(Notice notice) {
        notice.setPublishedAt(LocalDateTime.now());
        notice.setActive(true);
        return noticeRepository.save(notice);
    }

    @Transactional
    public Notice update(Long id, Notice details) {
        Notice notice = getById(id);
        notice.setTitle(details.getTitle());
        notice.setContent(details.getContent());
        notice.setNoticeType(details.getNoticeType());
        notice.setTargetAudience(details.getTargetAudience());
        notice.setDepartment(details.getDepartment());
        notice.setExpiryDate(details.getExpiryDate());
        notice.setActive(details.isActive());
        notice.setCreatedByUser(details.getCreatedByUser());
        return noticeRepository.save(notice);
    }

    @Transactional
    public void delete(Long id) {
        getById(id);
        noticeRepository.deleteById(id);
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
