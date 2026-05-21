package com.example.demo.repository;

import com.example.demo.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByEmployeeCode(String employeeCode);
    Page<Employee> findByDepartmentId(Long departmentId, Pageable pageable);
    Page<Employee> findByEmployeeType(String employeeType, Pageable pageable);
    Page<Employee> findByStatus(String status, Pageable pageable);
    long countByDepartmentId(Long departmentId);
    long countByEmployeeType(String employeeType);
    long countByStatusIgnoreCase(String status);

    @Query("""
            SELECT e FROM Employee e
            LEFT JOIN e.department d
            WHERE (:departmentId IS NULL OR d.id = :departmentId)
              AND (:employeeType IS NULL OR UPPER(e.employeeType) = UPPER(:employeeType))
              AND (:status IS NULL OR UPPER(e.status) = UPPER(:status))
              AND (:keyword IS NULL OR
                   LOWER(e.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(e.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(e.designation) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Employee> search(@Param("departmentId") Long departmentId,
                          @Param("employeeType") String employeeType,
                          @Param("status") String status,
                          @Param("keyword") String keyword,
                          Pageable pageable);
}
