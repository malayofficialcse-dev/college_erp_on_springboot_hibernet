package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Counseling;
import com.example.demo.model.Course;
import com.example.demo.repository.CounselingRepository;
import com.example.demo.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CounselingService {

    @Autowired
    private CounselingRepository counselingRepository;

    @Autowired
    private CourseRepository courseRepository;

    public Page<Counseling> getAll(Pageable pageable) {
        return counselingRepository.findAll(pageable);
    }

    public Page<Counseling> search(String keyword, String status, Pageable pageable) {
        return counselingRepository.search(
                emptyToNull(keyword),
                emptyToNull(status),
                pageable
        );
    }

    public Counseling getById(Long id) {
        return counselingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Counseling", "id", id));
    }

    public Counseling createCounseling(Counseling counseling) {
        if (counseling.getDesiredCourse() != null && counseling.getDesiredCourse().getId() != null) {
            Course course = courseRepository.findById(counseling.getDesiredCourse().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course", "id", counseling.getDesiredCourse().getId()));
            counseling.setDesiredCourse(course);
        }
        return counselingRepository.save(counseling);
    }

    public Counseling updateCounseling(Long id, Counseling details) {
        Counseling existing = getById(id);
        existing.setFirstName(details.getFirstName());
        existing.setLastName(details.getLastName());
        existing.setEmail(details.getEmail());
        existing.setPhone(details.getPhone());
        existing.setDateOfBirth(details.getDateOfBirth());
        existing.setGender(details.getGender());
        existing.setPreviousQualification(details.getPreviousQualification());
        existing.setCounselorName(details.getCounselorName());
        existing.setRemarks(details.getRemarks());
        existing.setStatus(details.getStatus());

        if (details.getDesiredCourse() != null && details.getDesiredCourse().getId() != null) {
            Course course = courseRepository.findById(details.getDesiredCourse().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course", "id", details.getDesiredCourse().getId()));
            existing.setDesiredCourse(course);
        } else {
            existing.setDesiredCourse(null);
        }

        return counselingRepository.save(existing);
    }

    public void deleteCounseling(Long id) {
        Counseling existing = getById(id);
        counselingRepository.delete(existing);
    }

    public Counseling markAsAdmitted(Long id) {
        Counseling existing = getById(id);
        existing.setStatus("ADMITTED");
        return counselingRepository.save(existing);
    }

    private String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}
