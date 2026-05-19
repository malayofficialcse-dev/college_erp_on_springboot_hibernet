package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Course;
import com.example.demo.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseService {

    @Autowired private CourseRepository courseRepository;

    public Page<Course> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
    }

    public Page<Course> getByDepartment(Long deptId, Pageable pageable) {
        return courseRepository.findByDepartmentId(deptId, pageable);
    }

    public Page<Course> getByType(String type, Pageable pageable) {
        return courseRepository.findByCourseType(type, pageable);
    }

    public Page<Course> search(Long departmentId, String courseType, String status, String keyword, Pageable pageable) {
        return courseRepository.search(departmentId, emptyToNull(courseType), emptyToNull(status), emptyToNull(keyword), pageable);
    }

    @Transactional
    public Course createCourse(Course course) {
        if (courseRepository.findByCourseCode(course.getCourseCode()).isPresent()) {
            throw new IllegalArgumentException("Course code already exists: " + course.getCourseCode());
        }
        return courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(Long id, Course details) {
        Course course = getCourseById(id);
        course.setTitle(details.getTitle());
        course.setDescription(details.getDescription());
        course.setTotalSemesters(details.getTotalSemesters());
        course.setDurationYears(details.getDurationYears());
        course.setCredits(details.getCredits());
        course.setCourseType(details.getCourseType());
        course.setStatus(details.getStatus());
        course.setDepartment(details.getDepartment());
        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        getCourseById(id);
        courseRepository.deleteById(id);
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
