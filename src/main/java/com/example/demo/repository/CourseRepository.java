package com.example.demo.repository;

import com.example.demo.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseCode(String courseCode);
    Page<Course> findByDepartmentId(Long departmentId, Pageable pageable);
    Page<Course> findByCourseType(String courseType, Pageable pageable);
    Page<Course> findByStatus(String status, Pageable pageable);

    @Query("""
            SELECT c FROM Course c
            WHERE (:departmentId IS NULL OR c.department.id = :departmentId)
              AND (:courseType IS NULL OR UPPER(c.courseType) = UPPER(:courseType))
              AND (:status IS NULL OR UPPER(c.status) = UPPER(:status))
              AND (:keyword IS NULL OR
                   LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(COALESCE(c.description, '')) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Course> search(@Param("departmentId") Long departmentId,
                        @Param("courseType") String courseType,
                        @Param("status") String status,
                        @Param("keyword") String keyword,
                        Pageable pageable);
}
