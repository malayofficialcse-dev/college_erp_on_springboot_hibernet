package com.example.demo.repository;

import com.example.demo.model.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByName(String name);
    Optional<Department> findByCode(String code);
    Page<Department> findByStatus(String status, Pageable pageable);
    long countByStatusIgnoreCase(String status);

    @Query("""
            SELECT d FROM Department d
            WHERE (:status IS NULL OR UPPER(d.status) = UPPER(:status))
              AND (:keyword IS NULL OR
                   LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(d.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(COALESCE(d.description, '')) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Department> search(@Param("status") String status,
                            @Param("keyword") String keyword,
                            Pageable pageable);
}
