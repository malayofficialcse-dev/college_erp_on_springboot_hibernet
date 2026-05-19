package com.example.demo.repository;

import com.example.demo.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseCode(String courseCode);
    Page<Course> findByDepartmentId(Long departmentId, Pageable pageable);
    Page<Course> findByCourseType(String courseType, Pageable pageable);
    Page<Course> findByStatus(String status, Pageable pageable);
}
