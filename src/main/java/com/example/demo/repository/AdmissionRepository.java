package com.example.demo.repository;

import com.example.demo.model.Admission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdmissionRepository extends JpaRepository<Admission, Long> {

    Optional<Admission> findByAdmissionNumber(String admissionNumber);

    Page<Admission> findByStudentId(Long studentId, Pageable pageable);

    Page<Admission> findByCourseId(Long courseId, Pageable pageable);

    Page<Admission> findByDepartmentId(Long departmentId, Pageable pageable);

    Page<Admission> findByStatus(String status, Pageable pageable);

    Page<Admission> findByAcademicYear(String academicYear, Pageable pageable);

    Page<Admission> findByPaymentPlan(String paymentPlan, Pageable pageable);

    @Query("""
            SELECT a FROM Admission a
            WHERE (:studentId IS NULL OR a.student.id = :studentId)
              AND (:courseId IS NULL OR a.course.id = :courseId)
              AND (:departmentId IS NULL OR a.department.id = :departmentId)
              AND (:status IS NULL OR UPPER(a.status) = UPPER(:status))
              AND (:academicYear IS NULL OR a.academicYear = :academicYear)
              AND (:paymentPlan IS NULL OR UPPER(a.paymentPlan) = UPPER(:paymentPlan))
              AND (:keyword IS NULL OR
                   LOWER(a.admissionNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(COALESCE(a.student.firstName, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(COALESCE(a.student.lastName, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(COALESCE(a.student.enrollmentNumber, '')) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Admission> search(@Param("studentId") Long studentId,
                           @Param("courseId") Long courseId,
                           @Param("departmentId") Long departmentId,
                           @Param("status") String status,
                           @Param("academicYear") String academicYear,
                           @Param("paymentPlan") String paymentPlan,
                           @Param("keyword") String keyword,
                           Pageable pageable);

    List<Admission> findByStudentIdOrderByAdmissionDateDesc(Long studentId);

    long countByStatus(String status);

    long countByPaymentPlan(String paymentPlan);
}
