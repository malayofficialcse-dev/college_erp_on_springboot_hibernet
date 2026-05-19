package com.example.demo.repository;

import com.example.demo.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
    Optional<Student> findByEnrollmentNumber(String enrollmentNumber);
    Page<Student> findByDepartmentId(Long departmentId, Pageable pageable);
    Page<Student> findByStatus(String status, Pageable pageable);
    Page<Student> findByDepartmentIdAndStatus(Long departmentId, String status, Pageable pageable);
    Page<Student> findByCurrentSemester(Integer semester, Pageable pageable);
    long countByDepartmentId(Long departmentId);
    long countByStatus(String status);
    long countByStatusIgnoreCase(String status);
    List<Student> findByCurrentSemesterAndStatusIgnoreCase(Integer currentSemester, String status);

    @Query("""
            SELECT s FROM Student s
            WHERE (:departmentId IS NULL OR s.department.id = :departmentId)
              AND (:semester IS NULL OR s.currentSemester = :semester)
              AND (:status IS NULL OR UPPER(s.status) = UPPER(:status))
              AND (:keyword IS NULL OR
                   LOWER(s.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(s.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(s.enrollmentNumber) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Student> search(@Param("departmentId") Long departmentId,
                         @Param("semester") Integer semester,
                         @Param("status") String status,
                         @Param("keyword") String keyword,
                         Pageable pageable);
}
