package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Scholarship;
import com.example.demo.repository.ScholarshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScholarshipService {

    @Autowired private ScholarshipRepository scholarshipRepository;

    public Page<Scholarship> getAllScholarships(Pageable pageable) {
        return scholarshipRepository.findAll(pageable);
    }

    public Scholarship getScholarshipById(Long id) {
        return scholarshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship", "id", id));
    }

    public Page<Scholarship> getByStudent(Long studentId, Pageable pageable) {
        return scholarshipRepository.findByStudentId(studentId, pageable);
    }

    public Page<Scholarship> getByStatus(String status, Pageable pageable) {
        return scholarshipRepository.findByStatus(status, pageable);
    }

    @Transactional
    public Scholarship createScholarship(Scholarship scholarship) {
        return scholarshipRepository.save(scholarship);
    }

    @Transactional
    public Scholarship updateScholarship(Long id, Scholarship details) {
        Scholarship scholarship = getScholarshipById(id);
        scholarship.setScholarshipName(details.getScholarshipName());
        scholarship.setAcademicYear(details.getAcademicYear());
        scholarship.setAmount(details.getAmount());
        scholarship.setType(details.getType());
        scholarship.setAwardedDate(details.getAwardedDate());
        scholarship.setStatus(details.getStatus());
        scholarship.setRemarks(details.getRemarks());
        scholarship.setStudent(details.getStudent());
        return scholarshipRepository.save(scholarship);
    }

    @Transactional
    public void deleteScholarship(Long id) {
        getScholarshipById(id);
        scholarshipRepository.deleteById(id);
    }
}
