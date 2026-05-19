package com.example.demo.repository;

import com.example.demo.model.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Page<Notice> findByIsActiveTrue(Pageable pageable);
    Page<Notice> findByTargetAudience(String audience, Pageable pageable);
    Page<Notice> findByDepartmentId(Long departmentId, Pageable pageable);
    Page<Notice> findByNoticeType(String noticeType, Pageable pageable);
}
