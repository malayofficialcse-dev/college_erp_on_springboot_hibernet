package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Semester;
import com.example.demo.repository.SemesterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SemesterService {

    @Autowired private SemesterRepository semesterRepository;

    public Page<Semester> getAll(Pageable pageable) {
        return semesterRepository.findAll(pageable);
    }

    public Semester getById(Long id) {
        return semesterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", id));
    }

    @Transactional
    public Semester create(Semester semester) {
        return semesterRepository.save(semester);
    }

    @Transactional
    public Semester update(Long id, Semester details) {
        Semester semester = getById(id);
        semester.setName(details.getName());
        semester.setSemesterNumber(details.getSemesterNumber());
        semester.setStartDate(details.getStartDate());
        semester.setEndDate(details.getEndDate());
        semester.setStatus(details.getStatus());
        semester.setAcademicYear(details.getAcademicYear());
        return semesterRepository.save(semester);
    }

    @Transactional
    public void delete(Long id) {
        getById(id);
        semesterRepository.deleteById(id);
    }
}
