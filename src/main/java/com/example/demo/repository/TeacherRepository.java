package com.example.demo.repository;

import com.example.demo.model.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByEmail(String email);
    Optional<Teacher> findByEmployeeCode(String employeeCode);
    Page<Teacher> findByDepartmentId(Long departmentId, Pageable pageable);
    Page<Teacher> findByStatus(String status, Pageable pageable);
    long countByDepartmentId(Long departmentId);
    long countByStatusIgnoreCase(String status);
    List<Teacher> findByDepartmentId(Long departmentId);

    @Query("""
            SELECT t FROM Teacher t
            WHERE (:departmentId IS NULL OR t.department.id = :departmentId)
              AND (:status IS NULL OR UPPER(t.status) = UPPER(:status))
              AND (:keyword IS NULL OR
                   LOWER(t.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(t.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(t.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(t.employeeCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(t.designation) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Teacher> search(@Param("departmentId") Long departmentId,
                         @Param("status") String status,
                         @Param("keyword") String keyword,
                         Pageable pageable);
}
