package com.example.demo.repository;

import com.example.demo.model.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Page<Notice> findByIsActiveTrue(Pageable pageable);
    Page<Notice> findByTargetAudience(String audience, Pageable pageable);
    Page<Notice> findByDepartmentId(Long departmentId, Pageable pageable);
    Page<Notice> findByNoticeType(String noticeType, Pageable pageable);

    @Query("""
            SELECT n FROM Notice n
            LEFT JOIN n.department d
            WHERE n.isActive = true
              AND (n.expiryDate IS NULL OR n.expiryDate >= :today)
              AND (
                  UPPER(n.targetAudience) = 'ALL'
                  OR UPPER(n.targetAudience) = UPPER(:audience)
                  OR (UPPER(n.targetAudience) = 'DEPARTMENT' AND :departmentId IS NOT NULL AND d.id = :departmentId)
              )
            ORDER BY n.publishedAt DESC
            """)
    List<Notice> findRelevantNotices(@Param("audience") String audience,
                                     @Param("departmentId") Long departmentId,
                                     @Param("today") LocalDate today,
                                     Pageable pageable);

    @Query("""
            SELECT n FROM Notice n
            LEFT JOIN n.department d
            WHERE (:audience IS NULL OR UPPER(n.targetAudience) = UPPER(:audience))
              AND (:departmentId IS NULL OR d.id = :departmentId)
              AND (:noticeType IS NULL OR UPPER(n.noticeType) = UPPER(:noticeType))
              AND (:active IS NULL OR n.isActive = :active)
              AND (:dateFrom IS NULL OR n.publishedAt >= CAST(:dateFrom as java.time.LocalDateTime))
              AND (:dateTo IS NULL OR n.publishedAt < CAST(:dateToPlusOne as java.time.LocalDateTime))
              AND (:keyword IS NULL OR
                   LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Notice> search(@Param("audience") String audience,
                        @Param("departmentId") Long departmentId,
                        @Param("noticeType") String noticeType,
                        @Param("active") Boolean active,
                        @Param("dateFrom") LocalDateTime dateFrom,
                        @Param("dateToPlusOne") LocalDateTime dateToPlusOne,
                        @Param("keyword") String keyword,
                        Pageable pageable);
}
