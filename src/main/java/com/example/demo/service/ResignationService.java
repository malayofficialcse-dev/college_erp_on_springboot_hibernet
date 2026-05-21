package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Employee;
import com.example.demo.model.Resignation;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.ResignationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class ResignationService {

    @Autowired private ResignationRepository resignationRepository;
    @Autowired private EmployeeRepository employeeRepository;

    public Page<Resignation> getAll(Pageable pageable) {
        return resignationRepository.findAll(pageable);
    }

    public Page<Resignation> getByStatus(String status, Pageable pageable) {
        return resignationRepository.findByStatus(status, pageable);
    }

    public Page<Resignation> getByEmployee(Long employeeId, Pageable pageable) {
        return resignationRepository.findByEmployeeId(employeeId, pageable);
    }

    public Resignation getById(Long id) {
        return resignationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resignation", "id", id));
    }

    @Transactional
    public Resignation submit(Resignation resignation) {
        resignation.setResignationDate(LocalDate.now());
        resignation.setStatus("PENDING");
        return resignationRepository.save(resignation);
    }

    @Transactional
    public Resignation updateStatus(Long id, String status, String hrRemarks, Long reviewedById) {
        Resignation r = getById(id);
        r.setStatus(status);
        r.setHrRemarks(hrRemarks);
        r.setReviewedDate(LocalDate.now());
        if (reviewedById != null) {
            Employee reviewer = employeeRepository.findById(reviewedById).orElse(null);
            r.setReviewedBy(reviewer);
            // If accepted, mark employee as resigned
            if ("ACCEPTED".equalsIgnoreCase(status) && r.getEmployee() != null) {
                Employee emp = r.getEmployee();
                emp.setStatus("RESIGNED");
                employeeRepository.save(emp);
            }
        }
        return resignationRepository.save(r);
    }

    @Transactional
    public Resignation withdraw(Long id) {
        Resignation r = getById(id);
        if (!"PENDING".equalsIgnoreCase(r.getStatus())) {
            throw new IllegalStateException("Only PENDING resignations can be withdrawn.");
        }
        r.setStatus("WITHDRAWN");
        return resignationRepository.save(r);
    }
}
