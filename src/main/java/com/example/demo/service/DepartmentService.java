package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Department;
import com.example.demo.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepartmentService {

    @Autowired private DepartmentRepository departmentRepository;

    public Page<Department> getAllDepartments(Pageable pageable) {
        return departmentRepository.findAll(pageable);
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
    }

    public Page<Department> search(String status, String keyword, Pageable pageable) {
        return departmentRepository.search(emptyToNull(status), emptyToNull(keyword), pageable);
    }

    @Transactional
    public Department createDepartment(Department department) {
        if (departmentRepository.findByCode(department.getCode()).isPresent()) {
            throw new IllegalArgumentException("Department code already exists: " + department.getCode());
        }
        return departmentRepository.save(department);
    }

    @Transactional
    public Department updateDepartment(Long id, Department details) {
        Department dept = getDepartmentById(id);
        dept.setName(details.getName());
        dept.setCode(details.getCode());
        dept.setDescription(details.getDescription());
        dept.setEstablishedYear(details.getEstablishedYear());
        dept.setStatus(details.getStatus());
        dept.setHod(details.getHod());
        return departmentRepository.save(dept);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        getDepartmentById(id);
        departmentRepository.deleteById(id);
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
