package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.AcademicYear;
import com.example.demo.repository.AcademicYearRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AcademicYearService {

    @Autowired private AcademicYearRepository academicYearRepository;

    public Page<AcademicYear> getAll(Pageable pageable) {
        return academicYearRepository.findAll(pageable);
    }

    public AcademicYear getById(Long id) {
        return academicYearRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicYear", "id", id));
    }

    @Transactional
    public AcademicYear create(AcademicYear academicYear) {
        return academicYearRepository.save(academicYear);
    }

    @Transactional
    public AcademicYear update(Long id, AcademicYear details) {
        AcademicYear academicYear = getById(id);
        academicYear.setYearLabel(details.getYearLabel());
        academicYear.setStartYear(details.getStartYear());
        academicYear.setEndYear(details.getEndYear());
        academicYear.setCurrent(details.isCurrent());
        return academicYearRepository.save(academicYear);
    }

    @Transactional
    public void delete(Long id) {
        getById(id);
        academicYearRepository.deleteById(id);
    }
}
